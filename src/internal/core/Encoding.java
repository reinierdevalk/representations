package internal.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.uos.fmt.musitech.utility.math.Rational;
import external.Tablature;
import external.Tablature.Tuning;
import internal.structure.Event;
import internal.structure.Timeline;
import tbp.editor.Staff;
import tbp.symbols.ConstantMusicalSymbol;
import tbp.symbols.MensurationSign;
import tbp.symbols.RhythmSymbol;
import tbp.symbols.Symbol;
import tbp.symbols.TabSymbol;
import tbp.symbols.TabSymbol.TabSymbolSet;
import tools.ToolBox;

/**
 * @author Reinier de Valk
 * @version 19.02.2024 (last well-formedness check)
 */
public class Encoding implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String TBP_EXT = ".tbp"; 
	public static final String FOOTNOTE_INDICATOR = "@";
	public static final String OPEN_METADATA_BRACKET = "{";
	public static final String CLOSE_METADATA_BRACKET = "}";
	public static final String NO_BARLINE_TEXT = "no barline";
	public static final String MISPLACED_BARLINE_TEXT = "misplaced barline";
	public static final String SPACE_BETWEEN_STAFFS = "\n";
	public static final String AUTHOR_TAG = "AUTHOR";
	public static final String TITLE_TAG = "TITLE";
	public static final String SOURCE_TAG = "SOURCE";
	public static final String TABSYMBOLSET_TAG = "TABSYMBOLSET";
	public static final String TUNING_TAG = "TUNING";
	public static final String METER_INFO_TAG = "METER_INFO";
	public static final String DIMINUTION_TAG = "DIMINUTION";
	public static final String[] METADATA_TAGS = new String[]{
		AUTHOR_TAG, 
		TITLE_TAG, 
		SOURCE_TAG, 
		TABSYMBOLSET_TAG, 
		TUNING_TAG, 
		METER_INFO_TAG, 
		DIMINUTION_TAG
	};

	// For metadata
	public static final int AUTHOR_IND = 0;
	public static final int TITLE_IND = 1;
	public static final int SOURCE_IND = 2;
	public static final int TABSYMBOLSET_IND = 3;
	public static final int TUNING_IND = 4;
	public static final int METER_INFO_IND = 5;
	public static final int DIMINUTION_IND = 6;

	// For listsOfSymbols
	public static final int ALL_SYMBOLS_IND = 0;
	public static final int TAB_SYMBOLS_IND = 1;
	public static final int RHYTHM_SYMBOLS_IND = 2;
	public static final int MENSURATION_SIGNS_IND = 3;
	public static final int BARLINES_IND = 4;

	// For listsOfStatistics
	public static final int IS_TAB_SYMBOL_EVENT_IND = 0;
	public static final int IS_RHYTHM_SYMBOL_EVENT_IND = 1;
	public static final int IS_REST_EVENT_IND = 2;
	public static final int IS_MENSURATION_SIGN_EVENT_IND = 3;
	public static final int IS_BARLINE_EVENT_IND = 4;
	public static final int SIZE_OF_EVENTS_IND = 5;
	public static final int HORIZONTAL_POS_IND = 6;
	public static final int VERTICAL_POS_IND = 7;
	public static final int HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND = 8;

	private String piecename; 
	private String rawEncoding;
	private String cleanEncoding;
	private Map<String, String> metadata;
	private String header;
	private TabSymbolSet tabSymbolSet;
	private List<Event> events;
	private Timeline timeline;
	private Timeline timelineAgnostic;
	private List<List<String>> listsOfSymbols;
	private List<List<Integer>> listsOfStatistics;

	public static enum Stage {
		COMMENTS_AND_METADATA_CHECKED, RULES_CHECKED;
	}


	public static void main(String[] args) {
	}


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Encoding() {
	}


	/**
	 * Creates an <code>Encoding</code> from a <code>.tbp</code> file. 
	 * IT IS ASSUMED THAT THE TBP FILE IS CHECKED IN THE VIEWER AND IS CORRECT TODO
	 * 
	 * @param f
	 */
	public Encoding(File f) {
		init(ToolBox.readTextFile(f), ToolBox.getFilename(f, TBP_EXT), Stage.RULES_CHECKED);
	}


	/**
	 * Creates an <code>Encoding</code> from an existing raw encoding.
	 * RAW ENCODING MAY CONTAIN ERRORS // or RAW ENCODING CHECKED IN VIEWER? TODO
	 *  
	 * @param rawEncoding
	 * @param piecename
	 * @param stage
	 */
	public Encoding(String rawEncoding, String piecename, Stage stage) {
		init(rawEncoding, piecename, stage);
	}


	private void init(String rawEncoding, String piecename, Stage stage) {
		if (stage == Stage.COMMENTS_AND_METADATA_CHECKED || stage == Stage.RULES_CHECKED) {
			setPiecename(piecename);
			setRawEncoding(rawEncoding);
			setCleanEncoding();
			setMetadata();
			setHeader();
			setTabSymbolSet();
		}
		if (stage == Stage.RULES_CHECKED) {
			setEvents();
			setTimeline();
			setTimelineAgnostic();
			setListsOfSymbols();
			setListsOfStatistics();
		}
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setPiecename(String s) {
		piecename = s.trim();
	}


	void setRawEncoding(String s) {
		rawEncoding = s.trim();
	}


	void setCleanEncoding() {
		cleanEncoding = makeCleanEncoding();
	}


	// TESTED
	String makeCleanEncoding() {
		// Remove all carriage returns and line breaks; remove leading and trailing whitespace
		 String cleanEnc = getRawEncoding().replaceAll("\r", "").replaceAll("\n", "").trim();

		// Remove all metadata and comments
		// NB: while-loop more convenient than for-loop in order not to overlook comments 
		// immediately succeeding one another
		while (cleanEnc.contains(OPEN_METADATA_BRACKET)) {
			int openCommentInd = cleanEnc.indexOf(OPEN_METADATA_BRACKET);
			int closeCommentInd = cleanEnc.indexOf(CLOSE_METADATA_BRACKET, openCommentInd);
			String comment = cleanEnc.substring(openCommentInd, closeCommentInd + 1);
			cleanEnc = cleanEnc.replace(comment, "");
		}
		return cleanEnc;
	}


	void setMetadata() {
		metadata = makeMetadata();
	}


	// TESTED
	Map<String, String> makeMetadata() {
		Map<String, String> md = new LinkedHashMap<String, String>();
		String rawEnc = getRawEncoding();
		for (int i = 0; i < rawEnc.length(); i++) {
			if (rawEnc.substring(i, i+1).equals(OPEN_METADATA_BRACKET)) {
				int closeInfoInd = rawEnc.indexOf(CLOSE_METADATA_BRACKET, i);
				String comm = rawEnc.substring(i + 1, closeInfoInd);
				// If comment is no longer a metadata element: break
				if (!(Stream.of(METADATA_TAGS).anyMatch(s -> comm.startsWith(s + ":")))) {
					break;
				}
				// Else: add parsed comment. The order of metadata tags has already 
				// been checked, so the tag and its content can simply be added 
				else {
					int ind = comm.indexOf(":");
					md.put(comm.substring(0, ind).trim(), comm.substring(ind + 1).trim());
					i = closeInfoInd;
				}
			}
		}
		return md;
	}


	void setHeader() {
		header = makeHeader();
	}


	// TESTED
	String makeHeader() {
		List<String> elements = new ArrayList<>();
		getMetadata().entrySet().forEach(e -> elements.add(
			OPEN_METADATA_BRACKET + e.getKey() + ": " + e.getValue() + CLOSE_METADATA_BRACKET
		));
		return String.join("\r\n", elements);
	}


	void setTabSymbolSet() {
		tabSymbolSet = makeTabSymbolSet();
	}


	// TESTED
	TabSymbolSet makeTabSymbolSet() {
		return TabSymbolSet.getTabSymbolSet(getMetadata().get(METADATA_TAGS[TABSYMBOLSET_IND]), null);
	}


	void setEvents() {
		events = makeEvents();
	}


	// TESTED
	List<Event> makeEvents() {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		String sp = Symbol.SPACE.getEncoding();
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		// Remove any line breaks, EBI, and leading and trailing whitespace from rawEnc
		String rawEnc = getRawEncoding();
		rawEnc = rawEnc.replaceAll("\r", "").replaceAll("\n", "").replaceAll(ebi, "");
		rawEnc = rawEnc.trim();

		// List all comments
		List<String> nonEditorialComments = new ArrayList<>();
		List<String> editorialComments = new ArrayList<>();


		for (int i = 0; i < rawEnc.length(); i++) {
			int ombInd = rawEnc.indexOf(omb, i);
			int cmbInd = rawEnc.indexOf(cmb, ombInd + 1);
			String comment = rawEnc.substring(ombInd, cmbInd+1);
			// Non-editorial comment
			if (!comment.startsWith(omb + FOOTNOTE_INDICATOR)) {
				nonEditorialComments.add(comment);
			}
			// Editorial comment
			else {
				editorialComments.add(
					comment.substring(comment.indexOf(omb)+1, comment.indexOf(cmb)));
			}
			if (cmbInd == rawEnc.lastIndexOf(cmb)) {
				break;
			}
			i = cmbInd;
		}

		// Remove all non-editorial comments from rawEnc
		for (String comment : nonEditorialComments) {
			rawEnc = rawEnc.replace(comment, "");
		}
		// Temporarily substitute all editorial comments
		for (int i = 0; i < editorialComments.size(); i++) {
			rawEnc = rawEnc.replace(omb + editorialComments.get(i) + cmb, omb + i + cmb);
		}

		// List events per bar
		List<Event> events = new ArrayList<>();
		int systemCounter = 1;
		int barCounter = 1;
		int commentCounter = 1;
		String currEvent = "";
		for (int i = 0; i < rawEnc.length(); i++) {
			int ssInd = rawEnc.indexOf(ss, i);
			String symbol = rawEnc.substring(i, ssInd);
			boolean symbolContainsComment = symbol.contains(omb);
			boolean firstInSystem = (i == 0) || symbol.startsWith(sbi);
			symbol = firstInSystem ? symbol.replace(sbi, "") : symbol;
			// Each symbol is either a space, a barline, or a TS, RS, or MS
			// Space: add to events
			if (symbol.equals(sp)) {
				boolean eventContainsComment = currEvent.contains(omb);
				events.add(new Event(
					eventContainsComment ? removeComment(currEvent) : currEvent,
					systemCounter, barCounter,
					eventContainsComment ? editorialComments.get(commentCounter-1) : null,
					eventContainsComment ? "#" + String.valueOf(commentCounter) : null
				));
				if (eventContainsComment) {
					commentCounter++;
				}
				currEvent = "";
			}
			// Barline: add to events
			else if (!symbolContainsComment && (Symbol.getConstantMusicalSymbol(symbol) != null && 
				Symbol.getConstantMusicalSymbol(symbol).isBarline()) ||
				symbolContainsComment && Symbol.getConstantMusicalSymbol(removeComment(symbol)) != null &&
				Symbol.getConstantMusicalSymbol(removeComment(symbol)).isBarline()) {
				// Decorative opening barline: increment system
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
				events.add(new Event(
					symbolContainsComment ? removeComment(symbol) + ss : symbol + ss,
					systemCounter, barCounter,
					symbolContainsComment ? editorialComments.get(commentCounter-1) : null,
					symbolContainsComment ? "#" + String.valueOf(commentCounter) : null
				));
				if (symbolContainsComment) {
					commentCounter++;
				}
				// Not a decorative opening barline: increment bar
				if (!firstInSystem) {
					barCounter++;
				}
			}
			// TS, RS, or MS: add to currEvent
			else {
				currEvent += symbol + ss;
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
			}
			i = ssInd;
		}
		return events;
	}


	void setTimeline() {
		timeline = new Timeline(this, false);
	}


	void setTimelineAgnostic() {
		timelineAgnostic = new Timeline(this, true);
	}


	void setListsOfSymbols() {
		listsOfSymbols = makeListsOfSymbols();
	}


	// TESTED
	List<List<String>> makeListsOfSymbols() {
		List<List<String>> los = new ArrayList<>();

		List<String> allSymbols = new ArrayList<String>();
		List<String> tabSymbols = new ArrayList<String>();
		List<String> rhythmSymbols = new ArrayList<String>();
		List<String> mensurationSigns = new ArrayList<String>();
		List<String> barlines = new ArrayList<String>();
		List<Event> events = getEvents();
		for (int i = 0; i < events.size(); i++) {
			for (String currSymbol : 
				events.get(i).getEncoding().split("\\" + Symbol.SYMBOL_SEPARATOR)) {
				allSymbols.add(currSymbol);
				if(Symbol.getTabSymbol(currSymbol, getTabSymbolSet()) != null) {
					tabSymbols.add(currSymbol);
				}
				else if (Symbol.getRhythmSymbol(currSymbol) != null) {
					rhythmSymbols.add(currSymbol);
				}
				else if (Symbol.getMensurationSign(currSymbol) != null) {
					mensurationSigns.add(currSymbol);
				}
				else if (Symbol.getConstantMusicalSymbol(currSymbol) != null && 
					Symbol.getConstantMusicalSymbol(currSymbol).isBarline()) {
					barlines.add(currSymbol);
				}
			}
		}
		los.add(ALL_SYMBOLS_IND, allSymbols);
		los.add(TAB_SYMBOLS_IND, tabSymbols);
		los.add(RHYTHM_SYMBOLS_IND, rhythmSymbols);
		los.add(MENSURATION_SIGNS_IND, mensurationSigns);
		los.add(BARLINES_IND, barlines);

		return los;
	}


	void setListsOfStatistics() {
		listsOfStatistics = makeListsOfStatistics();
	}


	// TESTED
	List<List<Integer>> makeListsOfStatistics() { 
		List<List<Integer>> los = new ArrayList<>();
		
		String ss = Symbol.SYMBOL_SEPARATOR;
		
		// 0-5. Lists that have the same size as listOfAllEvents (from listsOfSymbols)
		List<Integer> isTabSymbolEvent = new ArrayList<Integer>();
		List<Integer> isRhythmSymbolEvent = new ArrayList<Integer>();
		List<Integer> isRestEvent = new ArrayList<Integer>();
		List<Integer> isMensurationSignEvent = new ArrayList<Integer>();
		List<Integer> isBarlineEvent = new ArrayList<Integer>();
		List<Integer> sizeOfEvents = new ArrayList<Integer>();
		// 6-8. Lists that have the same size as listOfTabSymbols (from listsOfSymbols)
		List<Integer> horizontalPosOfTabSymbols = new ArrayList<Integer>();
		List<Integer> verticalPosOfTabSymbols = new ArrayList<Integer>();
		List<Integer> horizontalPosInTabSymbolEventsOnly = new ArrayList<Integer>();

		TabSymbolSet tss = getTabSymbolSet();
		List<Event> events = getEvents();
		int tabSymbolEventInd = 0;
		for (int i = 0; i < events.size(); i++) {
			String currEvent = events.get(i).getEncoding();
			// 0. isTabSymbolEvent
			boolean isTsEvent = assertEventType(currEvent, tss, "TabSymbol");
			isTabSymbolEvent.add(ToolBox.toInt(isTsEvent));
			// 1. isRhythmSymbolEvent
			boolean isRsEvent = assertEventType(currEvent, null, "RhythmSymbol");
			isRhythmSymbolEvent.add(ToolBox.toInt(isRsEvent));
			int numTsInEvent = 0;
			if (isTsEvent) {
				String[] symbols = 
					currEvent.substring(0, currEvent.lastIndexOf(ss)).split("\\" + ss);
				numTsInEvent = !isRsEvent ? symbols.length : symbols.length - 1;
			}
			// 2. isRestEvent
			isRestEvent.add(ToolBox.toInt(assertEventType(currEvent, null, "rest")));  
			// 3. isMensurationSignEvent
			isMensurationSignEvent.add(ToolBox.toInt(assertEventType(currEvent, null, "MensurationSign")));
			// 4. isBarlineEvent
			isBarlineEvent.add(ToolBox.toInt(assertEventType(currEvent, null, "barline")));  
			// 5. sizeOfEvents
			sizeOfEvents.add(numTsInEvent);
			// 6-8. horizontalPosOfTabSymbols, verticalPosOfTabSymbols, horizontalPosInTabSymbolEventsOnly
			if (isTsEvent) {
				horizontalPosOfTabSymbols.addAll(Collections.nCopies(numTsInEvent, i));
				verticalPosOfTabSymbols.addAll(
					IntStream.rangeClosed(0, numTsInEvent-1).boxed().collect(Collectors.toList()));
				horizontalPosInTabSymbolEventsOnly.addAll(
					Collections.nCopies(numTsInEvent, tabSymbolEventInd));
				tabSymbolEventInd++;
			}
		}
		los.add(IS_TAB_SYMBOL_EVENT_IND, isTabSymbolEvent);
		los.add(IS_RHYTHM_SYMBOL_EVENT_IND, isRhythmSymbolEvent);
		los.add(IS_REST_EVENT_IND, isRestEvent);
		los.add(IS_MENSURATION_SIGN_EVENT_IND, isMensurationSignEvent);
		los.add(IS_BARLINE_EVENT_IND, isBarlineEvent);
		los.add(SIZE_OF_EVENTS_IND, sizeOfEvents);
		los.add(HORIZONTAL_POS_IND, horizontalPosOfTabSymbols);
		los.add(VERTICAL_POS_IND, verticalPosOfTabSymbols);
		los.add(HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND, horizontalPosInTabSymbolEventsOnly);
	
		return los;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public String getPiecename() {
		return piecename;
	}


	public String getRawEncoding() {
		return rawEncoding;
	}


	/**
	 * Gets the clean encoding: the raw encoding from which all line breaks, whitespace, 
	 * comments, and added information is removed.
	 */
	public String getCleanEncoding() {
		return cleanEncoding;
	}


	/**
	 * Gets metadata.
	 * 
	 * @return A <code>Map</code> containing as keys the METADATA_TAGs, and as values
	 * <ul>
	 * <li>At element 0: the author.</li>
	 * <li>At element 1: the title.</li>
	 * <li>At element 2: the source.</li>
	 * <li>At element 3: the TabSymbolSet used for the encoding.</li>
	 * <li>At element 4: the tuning.</li>
	 * <li>At element 5: the meter information.</li>
	 * <li>At element 6: the diminution.</li>
	 * </ul>
	 * 
	 * Any leading or trailing whitespace is trimmed from all value fields.
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}


	public String getHeader() {
		return header;
	}


	public TabSymbolSet getTabSymbolSet() {
		return tabSymbolSet;
	}


	/**
	 * Gets all events in the piece. Each event is one of five types: 
	 * TS event, RS event, rest event, MS event, or barline event.
	 *
	 * @return A <code>List</code> of Events. Each event contains 
	 * <ul>
	 * <li>The encoding of the event.</li>
	 * <li>The system the event is in.</li>
	 * <li>The bar the event is in, derived from barline placement (where decorative 
	 *     barlines at the beginning of a system are ignored). Barlines themselves are 
	 *     counted as belonging to the bar they close (where decorative barlines are 
	 *     counted as belonging to the bar they open).</li>
	 * <li>If the event has a footnote, that footnote; otherwise <code>null</code>.</li>
	 * <li>If the event has a footnote, the ID of that footnote (# + a number); otherwise 
	 *     <code>null</code>.</li>
	 * </ul>
	 */
	public List<Event> getEvents() {
		return events;
	}


	public Timeline getTimeline() {
		return timeline;
	}


	public Timeline getTimelineAgnostic() {
		return timelineAgnostic;
	}


	/**
	 * Gets the lists of symbols.
	 * 
	 * @return A List containing the following Lists:
	 * <ul>
	 * <li> as element 0: allSymbols: contains all the individual symbols (CMS and VMS) 
	 *                    in the encoding</li>
	 * <li> as element 1: tabSymbols: contains all the individual TS in the encoding</li>
	 * <li> as element 2: rhythmSymbols: contains all the individual RS in the encoding</li>
	 * <li> as element 3: mensurationSigns: contains all the individual MS in the encoding</li>
	 * <li> as element 4: barlines: contains all the barlines (incl. double, repeat, etc.)  
	 *                    in the encoding</li>
	 * </ul>
	 */
	public List<List<String>> getListsOfSymbols() {
		return listsOfSymbols;
	}


	/**
	 * Gets the lists of statistics.  
	 * 
	 * @return A List containing the following Lists of the same length as listOfAllEvents 
	 *        (from listOfSymbols):
	 * <ul>
	 * <li>At element 0: isTabSymbolEvent: indicates by means of 1 or 0 whether the 
	 *                   event at index i contains a TS or not.</li>
	 * <li>At element 1: isRhythmSymbolEvent: indicates by means of 1 or 0 whether 
	 *                   the event at index i contains a RS or not.</li> 
	 * <li>At element 2: isRestEvent: indicates by means of 1 or 0 whether the event 
	 *                   at index i represents a rest or not.</li>
	 * <li>At element 3: isMensurationSignEvent: indicates by means of 1 or 0 whether 
	 *                   the event at index i contains a MS or not.</li>
	 * <li>At element 4: isBarlineEvent: indicates by means of 1 or 0 whether the 
	 *                   event at index i contains a barline or not.</li>
	 * <li>At element 5: sizeOfEvents: indicates the size (in number of TabSymbols) of 
	 *                   the event at index i (0 if the event is a barline, MS, or a rest).</li>
	 * </ul>
	 * 
	 * and the following Lists of the same length as listOfTabSymbols (from listOfSymbols):
	 *
	 * <ul>
	 * <li>At element 6: horizontalPositionOfTabSymbols: indicates the index of the 
	 *                   event a TS belongs to, considering ALL events.</li>
	 * <li>At element 7: verticalPositionOfTabSymbols: indicates the sequence number 
	 *                   of a TS in an event (0 being the lowest).</li> 
	 * <li>At element 8: horizontalPositionInTabSymbolEventsOnly: indicates the index 
	 *                    of the event a TS belongs to, considering ONLY TS events.</li>
	 * </ul>
	 */
	public List<List<Integer>> getListsOfStatistics() {
		return listsOfStatistics;
	}


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	/**
	 * Checks the <code>Encoding</code>, i.e.,  
	 * <ul>
	 * <li>Checks for correct bracketing of the metadata and comments.</li> 
	 * <li>Checks for missing or incorrect metadata.</li> 
	 * <li>Checks all VALIDITY RULES.</li> 
	 * <li>Checks for missing or unknown symbols.</li> 
	 * <li>Checks all LAYOUT RULES.</li> 
	 * </ul>
	 * 
	 * @param rawEnc
	 * @return 
     * <ul>
	 * <li><code>null</code> if there are no errors.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: additional information.</li>
	 * </ul>
	 * </ul>
	 */
	// NOT TESTED (wrapper method))
	public static String[] checkEncoding(String rawEnc) {
		String[] commErrs = checkComments(rawEnc);
		if (commErrs != null) {
			return commErrs;
		}
		else {
			String[] mdErrs = checkMetadata(rawEnc);
			if (mdErrs != null) {
				return mdErrs;
			}
			else {
				Encoding e = new Encoding(rawEnc, "", Stage.COMMENTS_AND_METADATA_CHECKED);
				String cleanEnc = e.getCleanEncoding();
				TabSymbolSet tss = e.getTabSymbolSet();
				Integer[] indsAligned = alignRawAndCleanEncoding(rawEnc, cleanEnc);
				String[] vrErrs = checkValidityRules(cleanEnc, indsAligned);
				if (vrErrs != null) {
					return vrErrs;
				}
				else {
					String[] symErrs = checkSymbols(cleanEnc, tss, indsAligned);
					if (symErrs != null) {
						return symErrs;    
					}
					else {
						String[] lrErrs = checkLayoutRules(cleanEnc, tss, indsAligned);
						if (lrErrs != null) {
							return lrErrs;
						}
						else {
							return null;
						}
					}
				}
			}
		}
	}


	/**
	 * Checks for correct bracketing of the metadata and comments.
	 * 
	 * @param rawEnc
	 * @return  
	 * <ul>
	 * <li><code>null</code> if there are no errors.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: additional information.</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	static String[] checkComments(String rawEnc) {
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		for (int i = 0; i < rawEnc.length(); i++) {
			String curr = rawEnc.substring(i, i+1);
			// If an omb: the next bracket must be a cmb
			if (curr.equals(omb)) {
				for (int j = i+1; j < rawEnc.length(); j++) {
					String currNext = rawEnc.substring(j, j+1);
					if (currNext.equals(omb) || (!currNext.equals(cmb) && j == rawEnc.length() - 1)) {
						return new String[]{
							String.valueOf(i), 
							String.valueOf(i+1),
							"COMMENT ERROR -- Insert complementing closing bracket.", 
							""
						};
					}
					if (currNext.equals(cmb)) {
						i = j;
						break;
					}
				}
			}
			// If a cmb: the previous bracket must be an omb
			if (curr.equals(cmb)) {
				for (int j = i-1; j >= 0; j--) {
					String currPrev = rawEnc.substring(j, j+1);
					if (currPrev.equals(cmb) || (!currPrev.equals(omb) && j == 0)) {
						return new String[]{
							String.valueOf(i),
							String.valueOf(i+1),
							"COMMENT ERROR -- Insert complementing opening bracket.", 
							""
						};
					}
					if (currPrev.equals(omb)) {
						i = j;
						break;
					}
				}
			}
		}		
		return null;
	}


	/**
	 * Checks for missing or incorrect metadata.
	 * 
	 * @param rawEnc
	 * @return 
	 * <ul>
	 * <li><code>null</code> if there are no errors.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: additional information.</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	static String[] checkMetadata(String rawEnc) {
		String cmb = CLOSE_METADATA_BRACKET;
		List<String> missing = Arrays.asList(METADATA_TAGS).stream()
			.filter(t -> rawEnc.indexOf(t + ":") == -1)
			.collect(Collectors.toList());
		List<Integer> tagInds = Arrays.asList(METADATA_TAGS).stream()
			.map(t -> rawEnc.indexOf(t + ":"))
			.collect(Collectors.toList());

		// Missing tags
		if (missing.size() != 0) {
			return new String[]{
				String.valueOf(-1), String.valueOf(-1),	
				"METADATA ERROR -- Insert missing " + (missing.size() == 1 ? "tag:" : "tags:"), 
				String.join(", ", missing) + "."
			};
		}	
		// Tag order
		else if (!(tagInds.stream().sorted().collect(Collectors.toList()).equals(tagInds))) {
			return new String[]{
				String.valueOf(-1), String.valueOf(-1),
				"METADATA ERROR -- Order tags correctly:", 
				String.join(", ", Arrays.asList(METADATA_TAGS)) + "."
			};
		}
		// Tag content (author, title, and source need not be checked)
		else {
			List<List<String>> validNames = new ArrayList<>(Collections.nCopies(METADATA_TAGS.length, null));
			validNames.set(TABSYMBOLSET_IND, Arrays.asList(TabSymbolSet.values()).stream()
				.map(TabSymbolSet::getName)
				.collect(Collectors.toList())
			);
			validNames.set(TUNING_IND, Arrays.asList(Tuning.values()).stream()
				.map(Tuning::getName)
				.collect(Collectors.toList())
			);
			validNames.set(METER_INFO_IND, Symbol.MENSURATION_SIGNS.values().stream()
				.map(MensurationSign::getMeterString)
				.distinct()
				.collect(Collectors.toList())
			);
			validNames.set(DIMINUTION_IND, Arrays.asList("-4", "-2", "1", "2", "4"));
			List<String> tagStrs = new ArrayList<>(Collections.nCopies(METADATA_TAGS.length, null));
			tagStrs.set(TABSYMBOLSET_IND, "TabSymbolSet");
			tagStrs.set(TUNING_IND, "tuning");
			tagStrs.set(METER_INFO_IND, "meter info");
			tagStrs.set(DIMINUTION_IND, "diminution");

			for (String t : Arrays.copyOfRange(METADATA_TAGS, TABSYMBOLSET_IND, METADATA_TAGS.length)) {
				int indT = rawEnc.indexOf(t);
				int tagNum = Arrays.asList(METADATA_TAGS).indexOf(t);
				String c = rawEnc.substring(indT + (t + ":").length(), rawEnc.indexOf(cmb, indT + 1)).trim();
				int indC = rawEnc.indexOf(c, indT + 1);
				int start = -1;
				int end = -1;
				String e = "";

				// Invalid TabSymbolSet, Tuning
				if (Arrays.asList(TABSYMBOLSET_TAG, TUNING_TAG).contains(t)) {
					// No content: highlight tag
					if (c.equals("")) {
						start = indT;
						end = indT + t.length();
						e = "METADATA ERROR -- Insert valid " + tagStrs.get(tagNum) + ".";
					}
					// Invalid content: highlight content
					else {
						if (!validNames.get(tagNum).contains(c)) {
							start = indC;
							end = indC + c.length();
							e = "METADATA ERROR -- Insert valid " + tagStrs.get(tagNum) + ".";
						}
					}
				}
				// Invalid diminution
				else if (Arrays.asList(DIMINUTION_TAG).contains(t)) {
					// Invalid content: highlight content
					if (!c.equals("")) {
						List<String> dimItems = 
							Arrays.stream(c.split(";")).map(String::trim).collect(Collectors.toList());
						for (String dimItem : dimItems) {
							int indDimItem = rawEnc.indexOf(dimItem, indC);
							if (!validNames.get(tagNum).contains(dimItem)) {
								e = "METADATA ERROR -- Insert valid diminution.";
								start = indDimItem;
								end = indDimItem + dimItem.length();
								break;
							}
						}
					}
				}
				// Invalid meterInfo
				else if (t.equals(METER_INFO_TAG)) {
					// No content allowed
					if (!c.equals("")) {
						List<String> miItems = 
							Arrays.stream(c.split(";")).map(String::trim).collect(Collectors.toList());
						List<Integer> prevBarNums = new ArrayList<>(Arrays.asList(-1));
						for (String miItem : miItems) {
							int indMiItem = rawEnc.indexOf(miItem, indC);
							// Check formatting 
							if (!miItem.contains(" ")) {
								e = "METADATA ERROR -- Insert valid time signature and bars.";
							}
							// Check meter validity and bars validity
							else {
								String meterStr = miItem.substring(0, miItem.indexOf(" ")).trim();
								String barsStr = miItem.substring(miItem.indexOf(" ")).trim();
								int barsStrCheck = checkBarsString(barsStr, prevBarNums.get(prevBarNums.size() - 1)); 
								prevBarNums.add(barsStrCheck);
								if (!validNames.get(tagNum).contains(meterStr)) {
									e = "METADATA ERROR -- Insert valid time signature.";
								}
								else if (barsStrCheck == -1) {
									e = "METADATA ERROR -- Insert valid bars.";
								}						
							}
							if (!e.equals("")) {
								start = indMiItem;
								end = indMiItem + miItem.length();
								break;
							}
						}
					}
				}
				if (!e.equals("")) {
					return new String[]{
						String.valueOf(start), String.valueOf(end),	e, "" 
					};
				}
			}
		}
		return null;
	}


	/**
	 * Checks the given bars String for correctness.
	 * 
	 * @param barsStr
	 * @param prevBarNum
	 * @return If the bars String is correct, the upper bar number; if not, -1.
	 */
	// TESTED
	static int checkBarsString(String barsStr, int prevBarNum) {
		String r = "[0-9]+";
		// barsStr must start and end with parentheses
		if (!barsStr.startsWith("(") || !barsStr.endsWith(")")) {
			return -1;
		}
		barsStr = barsStr.substring(1, barsStr.length()-1);
		String[] bars = barsStr.split("-"); // NB returns full barsStr if there is no dash
		// barsStr must contain numeric characters separated by a dash
		if (!bars[0].matches(r) || (bars.length == 2 && !bars[1].matches(r))) {
			return -1;
		}
		// barsStr must contain increasing numbers
		else if (bars.length == 2 && Integer.valueOf(bars[0]) > Integer.valueOf(bars[1])) {
			return -1;
		}
		// barsStr must connect to prevBarNum
		else if (prevBarNum != -1 && Integer.valueOf(bars[0]) != prevBarNum + 1) {
			return -1;
		}
		else {
			return Integer.valueOf(bars[bars.length-1]);
		}
	}


	/**
	 * Aligns the indices of the raw and the clean encoding, i.e., gets for each char in 
	 * rawEncoding the index of that character in cleanEncoding. 
	 * 
	 * @param rawEnc
	 * @param cleanEnc
	 * @return An Integer[] the size of rawEncoding, containing the aligned indices. All 
	 * indices where rawEncoding contains a char that is not part of cleanEncoding hold -1.
	 */
	// TESTED 
	static Integer[] alignRawAndCleanEncoding(String rawEnc, String cleanEnc) {
		// Initialise with default values of -1
		Integer[] indsAligned = new Integer[rawEnc.length()];
		Arrays.fill(indsAligned, -1);

		int startInd = 0;
		for (int i = 0; i < cleanEnc.length(); i++) {
			String currentChar = cleanEnc.substring(i, i + 1);
			for (int j = startInd; j < rawEnc.length(); j++) {
				String s = rawEnc.substring(j, j + 1); 
				// Skip comments
				if (s.equals(OPEN_METADATA_BRACKET)) {
					j = rawEnc.indexOf(CLOSE_METADATA_BRACKET, j);
				}
				else if (s.equals(currentChar)) {
					indsAligned[j] = i;
					startInd = j + 1;
					break;
				}
			}  
		}
		return indsAligned;
	}


	/**
	 * Checks all VALIDITY RULES.
	 * 
	 * @param cleanEnc
	 * @param indsRawAndCleanAligned
	 * @return  
	 * <ul>
	 * <li><code>null</code> if all the rules are met.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: a reference to the rule that was broken.</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	static String[] checkValidityRules(String cleanEnc, Integer[] indsRawAndCleanAligned) {
		List<Integer> inds = Arrays.asList(indsRawAndCleanAligned);

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;
		String ws = " ";

		String VR1 = "VALIDITY RULE 1: The encoding cannot contain whitespace.";
		String VR2 = "VALIDITY RULE 2: The encoding must end with an end break indicator.";
		String VR3 = "VALIDITY RULE 3: A system cannot start with a punctuation symbol.";
		String VR4 = "VALIDITY RULE 4: Each system must end with a symbol separator.";

		// SBIs are
		// - placed at the end of all but the last system: always preceded by chars
		// - placed in the middle of the piece: always followed by chars
		// EBIs are
		// - placed at the end of the last system: always preceded by chars
		// - placed at the end of the piece: never followed by chars
		//
		// Finds any SBI followed by a SBI. Matches with cleanEnc if that equals
		// one or more chars (.+); followed by a SBI + SBI; followed by one or more chars 
		String regexSbiSbi = ".+//.+";
		// Finds any SBI followed by a SS. Matches with cleanEnc if that equals
		// one or more chars (.+); followed by a SBI + SS; followed by one or more chars 
		String regexSbiSs = ".+/\\..+"; 
		// Finds any SBI not preceded by a SS. Matches with cleanEnc if that equals
		// one or more chars (.+); followed by not ([^...]) a SS + SBI; followed by one or more chars (.+)
		String regexNotSsSbi = ".+[^\\.]/.+";
		// Finds any EBI not preceded by an SS. Matches with cleanEnc if that equals
		// one or more chars (.+); followed by not ([^...]) a SS + EBI; followed by nothing
		String regexNotSsEbi = ".+[^\\.]//";

		// VALIDITY RULE 1: The encoding cannot contain whitespace 
		if (cleanEnc.contains(ws)) {
			int errorInd = inds.indexOf(cleanEnc.indexOf(ws));
			int wsLength = 0;
			int ind = cleanEnc.indexOf(ws);
			while (cleanEnc.substring(ind, ind+1).equals(" ")) {
				wsLength++;
				ind++;
			}
			return new String[]{
				String.valueOf(errorInd),
				String.valueOf(errorInd + wsLength),
				"INVALID ENCODING ERROR -- Remove this whitespace.",
				"See " + VR1
			};
		}
		// VALIDITY RULE 2: The encoding must end with an end break indicator
		else if (!cleanEnc.endsWith(ebi)) {
			return new String[]{
				String.valueOf(-1),
				String.valueOf(-1),
				"INVALID ENCODING ERROR -- Insert an end break indicator.",
				"See " + VR2
			};
		}
		// VALIDITY RULE 3: A system cannot start with a punctuation symbol
		else if (cleanEnc.startsWith(sbi) || cleanEnc.matches(regexSbiSbi) || 
			cleanEnc.startsWith(ss) || cleanEnc.matches(regexSbiSs)) {
			boolean sbiCase = cleanEnc.startsWith(sbi) || cleanEnc.matches(regexSbiSbi);
			String s = sbiCase ? sbi : ss;
			int errorInd = 
				cleanEnc.startsWith(s) ? inds.indexOf(cleanEnc.indexOf(s)) : 
				inds.indexOf(cleanEnc.indexOf(sbi + s)) + 1;
			return new String[] {
				String.valueOf(errorInd), 
				String.valueOf(errorInd + (sbiCase ? sbi.length() : ss.length())),
				"INVALID ENCODING ERROR -- Remove this " + 
					(sbiCase ? "system break indicator" : "symbol separator") + ".",
				"See " + VR3
			};
		}
		// VALIDITY RULE 4: Each system must end with a symbol separator
		else if (cleanEnc.matches(regexNotSsSbi) || cleanEnc.matches(regexNotSsEbi)) {
			boolean ebiCase = cleanEnc.matches(regexNotSsEbi);
			int sbiInd = -1;			
			for (int i = 0; i < cleanEnc.length(); i++) {
				sbiInd = cleanEnc.indexOf(sbi, i);
				if (!cleanEnc.substring(sbiInd - 1, sbiInd).equals(ss)) {
					break;
				}
				i = sbiInd;
			}
			int errorInd = inds.indexOf(sbiInd);
			// NB ebiCase must be checked first, because when the EBI is not preceded
			// by a SS, both regexNotSsSBi and regexNotSsEBi match
			return new String[]{
				String.valueOf(errorInd),
				String.valueOf(errorInd + (ebiCase ? ebi.length() : sbi.length())),
				"INVALID ENCODING ERROR -- Insert a symbol separator before this " + 
					(ebiCase ? "end" : "system") + " break indicator.",
				"See " + VR4
			};
		}
		else {
			return null;
		}
	}


	/**
	 * Checks for missing or unknown symbols.
	 *
	 * @param cleanEnc
	 * @param tss
	 * @param indsRawAndCleanAligned
	 * @return
	 * <ul>
	 * <li><code>null</code> if there are no errors.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: a reference to the rule that was broken.</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	static String[] checkSymbols(String cleanEnc, TabSymbolSet tss, Integer[] indsRawAndCleanAligned) {
		List<Integer> inds = Arrays.asList(indsRawAndCleanAligned);

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;

		String VR5 = "VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator.";

		for (int i = 0; i < cleanEnc.length(); i++) {
			int ssInd = cleanEnc.indexOf(ss, i);
			if (ssInd != -1) {
				// Create symbol; remove any SBI (at index i) directly preceding it
				String s = cleanEnc.substring(i, ssInd).replace(sbi, "");
				// Missing symbol found if symbol at i is an empty string (ssInd == i);
				// unknown symbol found if symbol at i is neither a CMS nor a VMS 
				if (s.equals("") || 
					(Symbol.getConstantMusicalSymbol(s) == null &&
					Symbol.getTabSymbol(s, tss) == null && 
					Symbol.getRhythmSymbol(s) == null && 
					Symbol.getMensurationSign(s) == null)) {
					int errorInd = inds.indexOf(i);
					return new String[]{
						String.valueOf(errorInd),
						String.valueOf(errorInd + (s.equals("") ? ss.length() : s.length())),
						s.equals("") ? 
							"MISSING SYMBOL ERROR -- Remove symbol separator or insert symbol before." :
							"UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.",
						"See " + VR5
					};
				}
				i = ssInd;
			}
		}
		return null;
	}


	/**
	 * Checks all LAYOUT RULES.
	 *
	 * @param cleanEnc
	 * @param tss
	 * @param indsRawAndCleanAligned
	 * @return
	 * <ul>
	 * <li><code>null</code> if all the rules are met.</li>
	 * <li>If not, a String[] containing</li>
	 * <ul>
	 * <li>At element 0: the index in rawEncoding of the first error char to be highlighted.</li>
	 * <li>At element 1: the index in rawEncoding of the last error char to be highlighted.</li>
	 * <li>At element 2: the appropriate error message.</li>
	 * <li>At element 3: a reference to the rule that was broken.</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	static String[] checkLayoutRules(String cleanEnc, TabSymbolSet tss, Integer[] indsRawAndCleanAligned) {
		List<Integer> inds = Arrays.asList(indsRawAndCleanAligned);

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String sp = Symbol.SPACE.getEncoding();

		String LR1 = "LAYOUT RULE 1: A system can start with any event but a space.";
		String LR2 = "LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline.";
		String LR3 = "LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space.";
		String LR4 = "LAYOUT RULE 4: A vertical sonority must be succeeded by a space.";
		String LR5 = "LAYOUT RULE 5: A rest (or rhythm dot at the beginning of a system or bar) must be succeeded by a space.";
		String LR6 = "LAYOUT RULE 6: A mensuration sign must be succeeded by a space.";
		String LR7 = "LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course.";
		String LR8 = "LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence.";

		// Finds any SBI not preceded by a CMS. Matches with cleanEnc if that equals
		// one or more chars (.+); followed by not ([^...]) a CMS + SS + SBI; followed by one or more chars
		List<String> allCMSStr = new ArrayList<>();
		Symbol.CONSTANT_MUSICAL_SYMBOLS.entrySet().forEach(e -> allCMSStr.add("(" + e.getValue().getEncoding() + ")"));

		String regexNotCmsSsSbi = ".+[^" + String.join("", allCMSStr) + "]\\./.+";
		// Finds any CMS succeeded by a space. Matches with cleanEnc if that equals
		// zero or more chars (.*); followed by a CMS + SS + space; followed by one or more chars (.+)
		String regexCmsSsSp = ".*[" + String.join("", allCMSStr) + "]\\.>.+";

		// LR 1-3 pertain to CMS exclusively, and can therefore be checked using regexes
		// LAYOUT RULE 1: A system can start with any event but a space
		if (cleanEnc.startsWith(sp) || cleanEnc.contains(sbi + sp)) {
			int errorInd = 
				cleanEnc.startsWith(sp) ? inds.indexOf(0) : 
				inds.indexOf(cleanEnc.indexOf(sbi + sp)) + sp.length();
			return new String[]{
				String.valueOf(errorInd), 
				String.valueOf(errorInd + sp.length()),
				"INVALID ENCODING ERROR -- Remove this space.", 
				"See " + LR1
			};
		}
		// LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat 
		// barline. I.e., a system must end with a CMS
		else if (cleanEnc.matches(regexNotCmsSsSbi)) {
			Integer[] indLeftRight = getIndicesOfPrecedingSymbol(sbi, cleanEnc, false);
			int errorInd = inds.indexOf(indLeftRight[1]);
			String s = cleanEnc.substring(indLeftRight[1], indLeftRight[2]);
			return new String[]{
				String.valueOf(errorInd),
				String.valueOf(errorInd + s.length()),
				"INVALID ENCODING ERROR -- Insert a space after this " + 
					(Symbol.getTabSymbol(s, tss) != null ? "TabSymbol" :
					(Symbol.getRhythmSymbol(s) != null ? "RhythmSymbol" : 
					"MensurationSign")) + ".",
				"See " + LR2
			};
		}
		// LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space
		else if (cleanEnc.matches(regexCmsSsSp)) {
			int errorInd = inds.indexOf(getIndicesOfPrecedingSymbol(sp, cleanEnc, true)[0]);
			return new String[]{
				String.valueOf(errorInd),
				String.valueOf(errorInd + sp.length()),
				"INVALID ENCODING ERROR -- Remove this space.",
				"See " + LR3
			};
		}
		else {
			// LR 4-6 pertain to TS, RS, or MS, and must therefore be checked using a for loop
			for (int i = 0; i < cleanEnc.length(); i++) {
				int ssInd = cleanEnc.indexOf(ss, i);
				int nextSsInd = cleanEnc.indexOf(ss, ssInd+1);
				if (nextSsInd != -1) {
					// Create symbols; remove any SBI (at index i) directly preceding them
					String s = cleanEnc.substring(i, ssInd).replace(sbi, "");
					String nextS = cleanEnc.substring(ssInd + 1, nextSsInd).replace(sbi, "");
					// LAYOUT RULE 4: A vertical sonority must be succeeded by a space. I.e., 
					// a TS can only be succeeded by 
					// - another TS, in which case both are part of the same chord
					// - a space, in which case it is the only or last TS of a chord
					boolean lr4Broken = 
						Symbol.getTabSymbol(s, tss) != null && 
						Symbol.getTabSymbol(nextS, tss) == null && !nextS.equals(sp);
					// LAYOUT RULE 5: A rest (or a rhythm dot at the beginning of system or 
					// bar) must be succeeded by a space. I.e., a RS can only be succeeded by 
					// - a TS, in which case it is the first symbol of a chord
					// - a space, in which case it denotes a rest or a rhythm dot at the 
					//   beginning of a bar
					boolean lr5Broken = 
						Symbol.getRhythmSymbol(s) != null && 
						Symbol.getTabSymbol(nextS, tss) == null && !nextS.equals(sp); 					
					// LAYOUT RULE 6: A MensurationSign must be succeeded by a space. I.e., 
					// a MS can only be succeeded by 
					// - another MS, in which case it is the first encoded MS of a compound MS
					// - a space, in which case it is either a single MS or the last 
					//   encoded MS of a compound MS
					boolean lr6Broken = 
						Symbol.getMensurationSign(s) != null &&
						Symbol.getMensurationSign(nextS) == null && !nextS.equals(sp);
					if (lr4Broken || lr5Broken || lr6Broken) {
						int errorInd = 
							inds.indexOf((cleanEnc.substring(i, i+1).equals(sbi)) ? i+1 : i);
						return new String[]{
							String.valueOf(errorInd),
							String.valueOf(errorInd + s.length()),
							"INVALID ENCODING ERROR -- Insert a space after this " + 
								(lr4Broken ? "TabSymbol" : 
								(lr5Broken ? "RhythmSymbol" : "MensurationSign")) + ".",
							"See " + (lr4Broken ? LR4 : (lr5Broken ? LR5 : LR6))
						};	
					}
					i = ssInd;
				}
				else {
					break;
				}
			}
			// LR 7-8 pertain to events, and must therefore be checked using a for loop
			for (int i = 0; i < cleanEnc.length(); i++) {
				int spInd = cleanEnc.indexOf(sp, i);
				if (spInd != -1) {
					// Space always preceded by at least one SS (one symbol)
					int rightInd = cleanEnc.lastIndexOf(ss, spInd);
					int leftInd = cleanEnc.lastIndexOf(ss, rightInd - 1);
					leftInd = leftInd == -1 ? 0 : leftInd + 1;
					// Create symbol; remove any SBI (at index leftInd) directly preceding it
					String s = cleanEnc.substring(leftInd, rightInd).replace(sbi, "");
					// If symbol is a TS: reconstruct event and check LRs
					if (Symbol.getTabSymbol(s, tss) != null) {
						String event = s;
						int leftIndEvent = -1;
						for (int newRightInd = cleanEnc.lastIndexOf(ss, leftInd); 
							newRightInd >= 0; newRightInd--) {
							int newLeftInd = cleanEnc.lastIndexOf(ss, newRightInd - 1);
							newLeftInd = newLeftInd == -1 ? 0 : newLeftInd + 1;
							// Create symbol; remove any SBI (at index newLeftInd) directly preceding it
							String prevS = cleanEnc.substring(newLeftInd, newRightInd).replace(sbi,  ""); 
							if (Symbol.getTabSymbol(prevS, tss) != null ||
								Symbol.getRhythmSymbol(prevS) != null) {
								event = prevS + ss + event;
								leftIndEvent = newLeftInd;
							}
							else {
								break;
							}
							newRightInd = newLeftInd;
						}
						List<String> symbols = Arrays.asList(event.split("\\" + ss));
						// LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course
						// NB: exception is German lute tablature (see Ochsenkun)
						List<Integer> courses = new ArrayList<>();
						symbols.forEach(item -> { if (Symbol.getTabSymbol(item, tss) != null) {
							courses.add(Symbol.getTabSymbol(item, tss).getCourse());}});
						List<Integer> uniqueCourses = 
							courses.stream().distinct().collect(Collectors.toList());
						boolean lr7Broken = (courses.size() > uniqueCourses.size()) && 
							!tss.getType().equals("German");
//						boolean lr7Broken = courses.size() > uniqueCourses.size();

						// LAYOUT RULE 8: A vertical sonority must be encoded in a fixed 
						// sequence. I.e.,
						// - Any RS must be encoded first
						// - Any TS must follow, encoded with the one on the lowest course first 
						//   NB: exception is German lute tablature (see Ochsenkun)
						List<Integer> orderedReversedCourses = 
							courses.stream().sorted(Comparator.reverseOrder()).collect(
							Collectors.toList());
						List<Boolean> symbolIsRs = new ArrayList<>();
						symbols.forEach(item -> 
							symbolIsRs.add(Symbol.getRhythmSymbol(item) != null));					
						boolean firstIsNotRs = (symbols.size() > 1 && symbolIsRs.contains(true) && 
							symbolIsRs.indexOf(true) != 0);
						
						boolean lr8Broken = firstIsNotRs || (
							!courses.equals(orderedReversedCourses) && !tss.getType().equals("German")
						);
//						boolean lr8Broken = firstIsNotRs || !courses.equals(orderedReversedCourses);
						if (lr7Broken || lr8Broken) {
							int errorInd = 
								inds.indexOf((cleanEnc.substring(leftIndEvent, 
								leftIndEvent+1).equals(sbi)) ? leftIndEvent+1 : leftIndEvent);
							return new String[]{
								String.valueOf(errorInd),
								String.valueOf(errorInd + event.length()),
								"INVALID ENCODING ERROR -- " + 
									(lr7Broken ? "Remove duplicate TabSymbol(s)" + "." : 
									"This vertical sonority is not encoded in the correct sequence"	+ "."),
								"See " + (lr7Broken ? LR7 : LR8)
							};
						}
					}
					i = spInd;
				}
			}
			return null;
		}
	}


	/**
	 * Gets, in the given cleanEncoding, the start index of the given symbol and the start
	 * and end index of the symbol preceding it if the given condition (whether or not the 
	 * preceding symbol is a CMS) holds true.
	 * 
	 * @param s
	 * @param cleanEnc
	 * @param condition Whether or not the preceding symbol should be a CMS.
	 * @return An Integer[] containing<br>
	 *         <ul>
	 *         <li>At index 0: the index of the given symbol.</li>
	 *         <li>At index 1: the start index of the preceding symbol.</li>
	 *         <li>At index 2: the end index (i.e., the index directly after it) of the 
	 *             preceding symbol.</li>
	 *         </ul>
	 */
	// TESTED
	static Integer[] getIndicesOfPrecedingSymbol(String s, String cleanEnc, boolean condition) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		int ind = -1;
		int leftInd = -1; 
		int rightInd = -1;
		for (int i = 0; i < cleanEnc.length(); i++) {
			ind = cleanEnc.indexOf(s, i);
			// s always preceded by at least one SS (one symbol)
			rightInd = cleanEnc.lastIndexOf(ss, ind);
			leftInd = cleanEnc.lastIndexOf(ss, rightInd - 1);
			leftInd = leftInd == -1 ? 0 : leftInd + 1;
			String prevS = cleanEnc.substring(leftInd, rightInd);
			if ((Symbol.getConstantMusicalSymbol(prevS) != null) == condition) {
				break;
			}
			i = ind;
		}
		return new Integer[]{ind, leftInd, rightInd};
	}


	/**
	 * Combines any successive rest events in the given list of events.
	 * 
	 * NB: Successive rest events separated by a barline event are not considered successive, and 
	 *     are split up by the barline.
	 * 
	 * @param events
	 * @return
	 */
	// TESTED
	public static List<String> combineSuccessiveRestEvents(List<String> events, TabSymbolSet tss) {
		List<String> res = new ArrayList<>();

		String ss = Symbol.SYMBOL_SEPARATOR;

		List<String> successiveRests = new ArrayList<>();
		for (int i = 0; i < events.size(); i++) {
			String t = events.get(i);
			String[] split = t.split("\\" + ss);
//			System.out.println(t);
			// If rest event: add to temporary list and continue for loop
			if (assertEventType(t, tss, "rest") == true) {
//			if (split.length == 2 && RhythmSymbol.getRhythmSymbol(split[0]) != null &&
//				split[1].equals(Symbol.SPACE.getEncoding())) {
				successiveRests.add(split[0]);
//				System.out.println("IS REST: " + t);
			}
//			// If barline event: ignore if any event after is a rest event; add to final list if not
//			else if (assertEventType(t, tss, "barline") == true) {
//				// It temporary list contains rests
//				// a. If any next event is also a rest, skip barline and continue temporary list
//				// b. If not, add combined rest from temporary list, add barline, and clear temporary list 
//				if (!successiveRests.isEmpty()) {
//					if (i + 1 != events.size() - 1) {
//						String nextT = events.get(i+1);
//						if (assertEventType(nextT, tss, "rest") == false) {
//							res.add(t);
//						}
//					}
//				}
//				
//				else {
//					res.add(t);
//				}
//			}
			// If not rest event
			else {
				// If the temporary list contains (successive) rests, which still need to be added
				if (!successiveRests.isEmpty()) {
//					System.out.println("IS NOT: " + t);
//					System.out.println(successiveRests);
					boolean combinedIsOpen = 
						successiveRests.get(0).contains(RhythmSymbol.TRIPLET_OPEN);
					boolean combinedIsClose = 
						successiveRests.get(successiveRests.size()-1).contains(RhythmSymbol.TRIPLET_CLOSE);
					int totalDur = 0;
					for (String s : successiveRests) {
						totalDur += Symbol.getRhythmSymbol(s).getDuration();
					}
//					System.out.println(combinedIsOpen);
//					System.out.println(combinedIsClose);
//					System.out.println(totalDur);
					RhythmSymbol combinedRs = null;
					for (RhythmSymbol rs : Symbol.RHYTHM_SYMBOLS.values()) {
						// Do not consider coronas
						if (rs.getDuration() == totalDur && 
							!rs.getEncoding().startsWith(Symbol.CORONA_BREVIS.getEncoding().substring(0, 2))) {
							// In case of triplets beginning/ending: make sure the RS's encoding contains the 
							// correct indicator (open/close) (necessary because tr[<RS> and tr]<RS> have the 
							// same duration)
							String enc = rs.getEncoding();
							if (combinedIsOpen && enc.contains(RhythmSymbol.TRIPLET_CLOSE)) {
//								enc = enc.replace(RhythmSymbol.TRIPLET_CLOSE, RhythmSymbol.TRIPLET_OPEN);
								combinedRs = 
									Symbol.getRhythmSymbol(enc.replace(RhythmSymbol.TRIPLET_CLOSE, 
									RhythmSymbol.TRIPLET_OPEN));
							}
							else if (combinedIsClose && enc.contains(RhythmSymbol.TRIPLET_OPEN)) {
//								enc = enc.replace(RhythmSymbol.TRIPLET_OPEN, RhythmSymbol.TRIPLET_CLOSE);
								combinedRs = 
									Symbol.getRhythmSymbol(enc.replace(RhythmSymbol.TRIPLET_OPEN, 
									RhythmSymbol.TRIPLET_CLOSE));
							}
//							if (combinedIsOpen || combinedIsClose) {
//								String openClose = "";
//								if (combinedIsOpen && !rs.getEncoding().contains(RhythmSymbol.TRIPLET_OPEN)) {
//									openClose = RhythmSymbol.TRIPLET_OPEN;
//								}
//								else if (combinedIsClose && !rs.getEncoding().contains(RhythmSymbol.TRIPLET_CLOSE)){
//									openClose = RhythmSymbol.TRIPLET_CLOSE;
//								}
//								combinedRs = RhythmSymbol.getRhythmSymbol(
//									RhythmSymbol.TRIPLET_INDICATOR + 
//									openClose +
//									rs.getEncoding().substring(RhythmSymbol.TRIPLET_INDICATOR.length()));
//								combinedRs = Symbol.getRhythmSymbol(rs.getEncoding());
//							}
							else {
								combinedRs = rs;
							}
							break;
						}
					}
//					System.out.println("gaaaaah " + combinedRs.getEncoding());
//					System.out.println(combinedRs.getSymbol());
//					System.out.println(combinedRs.getDuration());
//					System.out.println(combinedRs.getNumberOfDots());
					res.add(combinedRs.getEncoding() + ss +	Symbol.SPACE.getEncoding() + ss);
					successiveRests.clear();
				}
				res.add(t);
			}
		}
		return res;
	}


	/**
	 * Asserts whether the given event is of the given type. 
	 * 
	 * @param event
	 * @param tss
	 * @param type One of "TabSymbol" (contains TabSymbols), "RhythmSymbol" (starts with 
	 *             RhythmSymbol), "rest", "MensurationSign", "barline".
	 * @return
	 */
	// TESTED
	public static boolean assertEventType(String event, TabSymbolSet tss, String type) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String space = Symbol.SPACE.getEncoding();

		// Add SS to end and remove any space
		if (!event.endsWith(ss)) {
			event += ss;
		}
		if (event.endsWith(space + ss)) {
			event = event.substring(0, event.indexOf(space));
		}

		if (!event.equals("")) {
			if (type.equals("TabSymbol")) {
				for (String s : event.split("\\" + ss)) {
					if (Symbol.getTabSymbol(s, tss) != null) {
						return true;
					}
				}
			}
			else if (type.equals("RhythmSymbol")) {
				return Symbol.getRhythmSymbol(event.substring(0, event.indexOf(ss))) != null;
			}
			else if (type.equals("rest")) {
				return (Symbol.getRhythmSymbol(event.substring(0, event.indexOf(ss))) != null) &&
					(event.indexOf(ss) == event.lastIndexOf(ss));
			}		
			else if (type.equals("MensurationSign")) {
				return Symbol.getMensurationSign(event.substring(0, event.indexOf(ss))) != null;
			}
			else if (type.equals("barline")) {
				return Symbol.getConstantMusicalSymbol(event.substring(0, event.indexOf(ss))) != null &&
					Symbol.getConstantMusicalSymbol(event.substring(0, event.indexOf(ss))).isBarline();
			} 
			else {
				return false;
			}
		}
		return false;
	}


	/**
	 * Removes the comment from the given string.
	 * 
	 * @param s
	 * @return
	 */
	// TESTED
	public static String removeComment(String s) {
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		if (!(s.contains(omb) && s.contains(cmb))) {
			return s;
		}
		else {
			return s.substring(0, s.indexOf(omb)) + s.substring(s.indexOf(cmb)+1);
		}
	}


	/**
	 * Removes any decorative opening barline (barlines at the beginning of a system) events 
	 * from the given list of Events.
	 * 
	 * @param events
	 * @return
	 */
	// TESTED
	public static List<Event> removeDecorativeBarlineEvents(List<Event> events) {
		List<Event> pruned = new ArrayList<>();
		String ss = Symbol.SYMBOL_SEPARATOR;

		for (int i = 0; i < events.size(); i++) {
			Event currEvent = events.get(i);
			String firstSymbol = 
				currEvent.getEncoding().substring(0, currEvent.getEncoding().lastIndexOf(ss));
			int currBar = currEvent.getBar();
			if (!(Symbol.getConstantMusicalSymbol(firstSymbol) != null && 
				Symbol.getConstantMusicalSymbol(firstSymbol).isBarline())) {
				pruned.add(currEvent);
			}
			else {
				// Add only if not DOB; i.e., if the event is the same bar as the previous
				if (i > 0 && events.get(i - 1).getBar() == currBar) {
					pruned.add(currEvent);
				}
			}
		}
		return pruned;
	}


	/**
	 * Recomposes a (clean) encoding from the individual events contained by the list given, 
	 * adding a line break after each barline, as well as after each SBI.
	 * 
	 * @param events
	 * @return
	 */ 
	// TESTED
	public static String recompose(List<String> events) {
		String recomposed = "";

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		for (String s : events) {
			recomposed += s;
			if (!s.equals(sbi) && !s.equals(ebi)	) {
				String first = s.substring(0, s.indexOf(ss));
				// Add a line break after each CMS (space, barline) 
				if (Symbol.getConstantMusicalSymbol(first) != null) {
					recomposed += "\r\n";
				}
			}
			// Add a line break after each SBI
			else if (!s.equals(ebi)) {
				recomposed += "\r\n";
			}
		}		
		return recomposed;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//  augmentation
	//
	/**
	 * Augments the <code>Encoding</code> according to the given augmentation. Must be
	 * called on an <code>Encoding</code> extracted from an existing <code>Tablature</code>.
	 * 
	 * @param thresholdDur
	 * @param rescaleFactor
	 * @param ornIndsToSkip
	 * @param augmentation
	 */
	// NOT TESTED (wrapper method)
	public void augment(int thresholdDur, List<Integer> ornIndsToSkip, int rescaleFactor, 
		String augmentation) {		
		String rawEncAugm = augmentHeader(
			getHeader(), getTimeline(), getMetadata(), rescaleFactor, augmentation
		) + 
		"\r\n\r\n" + 
		recompose(augmentEvents(
			decompose(true, true), getTimeline(), getTabSymbolSet(), 
			thresholdDur, ornIndsToSkip, rescaleFactor, augmentation)
		);
		this.init(rawEncAugm, getPiecename(), Stage.RULES_CHECKED);
	}


	/**
	 * Augments the given header according to the given augmentation. 
	 * 
	 * @param argHeader
	 * @param tl
	 * @param metadata
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	// TESTED
	static String augmentHeader(String argHeader, Timeline tl, Map<String, String> metadata, 
		int rescaleFactor, String augmentation) {

		List<Integer[]> ts = tl.getTimeSignatures();
		List<Integer[]> b = tl.getBars();
		List<Integer> d = tl.getDiminutions();

		if (!augmentation.equals("deornament")) {
			// Make augmented content
			String miAugmContent = "";
			String dimAugmContent = "";
			if (augmentation.equals("reverse")) {
				int lastBar = b.get(b.size() - 1)[1];
				for (int i = ts.size() - 1; i >= 0; i--) {
					Integer[] currTs = ts.get(i);
					Integer[] currB = b.get(i);
					miAugmContent += 
						currTs[0] + "/" + currTs[1] + " " + 
						"(" + (currB[0] == currB[1] ? (lastBar - currB[0] + 1):
						(lastBar - (currB[1]) + 1) + "-" + (lastBar - currB[0] + 1)) + 
						(i > 0 ? "); " : ")");
					dimAugmContent += d.get(i) + (i > 0 ? "; " : "");
				}
			}
			else if (augmentation.equals("rescale")) {
				for (int i = 0; i < ts.size(); i++) {
					Integer[] currTs = ts.get(i);
					Integer[] currB = b.get(i);
					miAugmContent += 
						currTs[0] + "/" + (rescaleFactor > 0 ? currTs[1]/rescaleFactor : 
						currTs[1]*Math.abs(rescaleFactor)) + " " + 
						"(" + (currB[0] == currB[1] ? currB[0] : (currB[0] + "-" + currB[1])) + 
						(i < ts.size() -1 ? "); " : ")");
				}
			}

			// Replace original content with augmented content
			argHeader = argHeader.replace(metadata.get(METADATA_TAGS[METER_INFO_IND]), miAugmContent);
			if (augmentation.equals("reverse")) {
				argHeader = argHeader.replace(metadata.get(METADATA_TAGS[DIMINUTION_IND]), dimAugmContent);
			}
		}
		return argHeader;
	}


	/**
	 * Augments the given list of events according to the given augmentation.
	 * 
	 * @param events List of events with any missing RS complemented.
	 * @param tl
	 * @param tss
	 * @param thresholdDur
	 * @param ornIndsToSkip
	 * @param rescaleFactor
	 * @param augmentation If augmentation is "reverse", any MensurationSigns not encoded 
	 *        explicitly in the given list of events (i.e., only appearing in the meter info) 
	 *        will be included in the augmented list of events returned.
	 * @return
	 */
	// TESTED
	static List<String> augmentEvents(List<String> events, Timeline tl, TabSymbolSet tss, 
		int thresholdDur, List<Integer> ornIndsToSkip, int rescaleFactor, String augmentation) {

		String ss = Symbol.SYMBOL_SEPARATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		List<String> eventsAugm = new ArrayList<>();
		if (augmentation.equals("reverse")) {
			List<Integer[]> bars = tl.getBars();
			int lastBar = bars.get(bars.size() - 1)[1];
			Collections.reverse(bars);
			// Adapt bar numbers
			for (int i = 0; i < bars.size(); i++) {
				Integer[] in = bars.get(i); 
				bars.set(i, new Integer[]{lastBar - in[1] + 1, in[1] = lastBar - in[0] + 1});
			}

			List<Integer[]> ts = tl.getTimeSignatures();
			Collections.reverse(ts);
			// Adapt onset times
			for (int i = 0; i < ts.size(); i++) {
				ts.get(i)[2] = i == 0 ? 0 : Timeline.getTimeSignatureOnset(i, ts, bars);
			}

			// Get the encodings of the MSs
			List<String> msEncodings = new ArrayList<>();
			ts.forEach(in -> msEncodings
				.add(Symbol.getMensurationSign(new Integer[]{in[0], in[1]}, -1)
				.getEncoding() + ss + Symbol.SPACE.getEncoding() + ss));

			// 1. Strip events that need to be re-placed from events; reverse events
			// EBI
			events = events.subList(0, events.indexOf(ebi));
			// Decorative opening barline
			String first = events.get(0);
			if (assertEventType(first, tss, "barline")) {
				events = events.subList(1, events.size() - 1);
			}
			// Closing barline
			String last = events.get(events.size() - 1);
			if (assertEventType(last, tss, "barline")) {
				events = events.subList(0, events.size() - 1);
			}
			// MensurationSigns
			for (String ms : msEncodings) {
				events.remove(ms);
			}
			Collections.reverse(events);

			// 2. Construct eventsAugm 
			// Decorative opening barline
			if (assertEventType(first, tss, "barline")) {
				eventsAugm.add(0, first);
			}			
			// Events and Mss
			int mt = 0;
			List<Integer> msOnsets = ToolBox.getItemsAtIndex(ts, 2);
			for (int i = 0; i < events.size(); i++) {
				String e = events.get(i);
				if (assertEventType(e, tss, "RhythmSymbol")) {
					if (msOnsets.contains(mt)) {
						String ms = msEncodings.get(msOnsets.indexOf(mt));
						eventsAugm.add(ms);
					}	
					mt += Symbol.getRhythmSymbol(e.substring(0, e.indexOf(ss))).getDuration();
				}
				eventsAugm.add(e);
			}
			// Closing barline and EBI
			if (assertEventType(last, tss, "barline")) {
				eventsAugm.add(last);
			}
			eventsAugm.add(ebi);
		}
		else {
			int noteInd = 0;
			for (int i = 0; i < events.size(); i++) {
				String e = events.get(i);
				// If e is a RS event
				if (assertEventType(e, null, "RhythmSymbol")) {
					String[] symbols = e.split("\\" + ss);
					RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
					if (augmentation.equals("deornament")) {
						int currNoteInd = noteInd;
						int numNotesInEvent = symbols.length - 2;
						// If e is ornamental (in which case it consists of only a RS, a TS, and a space) and 
						// not part of an ornamental sequence at the beginning of the Encoding (in which case 
						// eventsAugm is still empty)
						// NB: It is assumed that an ornamental sequence is not interrupted by (ornamental) rests 
						if (r.getDuration() < thresholdDur && symbols.length == 3 && eventsAugm.size() > 0
							&& !ornIndsToSkip.contains(currNoteInd)) {
							int durOrnSeq = r.getDuration();
							for (int j = i+1; j < events.size(); j++) {
								String eNext = events.get(j);
								// If eNext is a RS event
								if (assertEventType(eNext, null, "RhythmSymbol")) {
									String[] symbolsNext = eNext.split("\\" + ss);
									RhythmSymbol rNext = Symbol.getRhythmSymbol(symbolsNext[0]);
									// If eNext is ornamental: increment duration of ornamental sequence
									if (rNext.getDuration() < thresholdDur && symbolsNext.length == 3 && 
										!ornIndsToSkip.contains(currNoteInd + 1)) {
										currNoteInd++;
										durOrnSeq += rNext.getDuration();
									}
									// If not: make ePrevDeorn, which replaces ePrev
									else {
										String ePrev = events.get(i-1);
										String[] symbolsPrev = ePrev.split("\\" + ss);
										RhythmSymbol rPrev = Symbol.getRhythmSymbol(symbolsPrev[0]);
										String ePrevDeorn = Symbol.getRhythmSymbol(
											rPrev.getDuration() + durOrnSeq,
											rPrev.getEncoding().startsWith(Symbol.CORONA_INDICATOR),
											rPrev.getBeam(),
											rPrev.isTriplet()
										).getEncoding();
										ePrevDeorn += ePrev.substring(ePrev.indexOf(ss));
										eventsAugm.set(eventsAugm.lastIndexOf(ePrev), ePrevDeorn);
										i = j-1;
										break;
									}
								}
								// If eNext is not a RS event
								else {
									eventsAugm.add(eNext);
								}
							}
							noteInd = currNoteInd + numNotesInEvent;
						}
						// If e is not ornamental
						else {
							eventsAugm.add(e);
							noteInd += numNotesInEvent;
						}
					}
					else if (augmentation.equals("rescale")) {
						String eResc = Symbol.getRhythmSymbol(
							r.getDuration() * rescaleFactor, 
							r.getEncoding().startsWith(Symbol.CORONA_INDICATOR), 
							r.getBeam(), 
							r.isTriplet()
						).getEncoding();
						eResc += e.substring(e.indexOf(ss), e.length());
						eventsAugm.add(eResc);
					}
				}
				// If e is not a RS event
				else {
					eventsAugm.add(e);
				}
			}
		}
		return eventsAugm;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//  visualisation
	//
	/**
	 * Visualises the Encoding.
	 * 
	 * @param tssSelected Determines the staff's tablature style.
	 * @param ignoreRepeatedRs If set to <code>true</code>, RS will only be 
	 *        displayed when they change - regardless of whether this is specified in 
	 *        the encoding.
	 * @param showHeader Whether or not to show the header (author, title, source).
	 * @param showFootnotes Whether or not to show the footnotes.       
	 * 
	 * @return A String representation of the encoding.
	 */
	// NOT TESTED
	public String visualise(TabSymbolSet tssSelected, boolean ignoreRepeatedRs, 
		boolean showHeader, boolean showFootnotes) {
		String tab = "";

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;

		String cleanEnc = getCleanEncoding();
		TabSymbolSet currTss = getTabSymbolSet();

		List<List<Integer>> barlineSegmentInds = getStaffSegmentIndices("barline");
		List<List<Integer>> footnoteSegmentInds = getStaffSegmentIndices("footnote");
		
		List<Integer[]> sl = getStaffLength();
		int staffLength = sl.get(0)[0];
		int longestStaff = sl.get(0)[1];
		boolean longestStaffStartsWithBarline = sl.get(1)[longestStaff] == 1;

		// Add selected metadata values
		if (showHeader) {
			Map<String, String> metadata = getMetadata();
			String metadataStr = metadata.get(METADATA_TAGS[AUTHOR_IND]) + "\n";
			metadataStr += metadata.get(METADATA_TAGS[TITLE_IND]) + "\n";
			metadataStr += metadata.get(METADATA_TAGS[SOURCE_IND]) + "\n";
			tab += metadataStr + "\n" + SPACE_BETWEEN_STAFFS.repeat(2);
		}

		int staffInd = 0;
		int sbiInd = -1; // fictional SBI before first symbol of cleanEnc
		int nextSbiInd = cleanEnc.indexOf(sbi, sbiInd + 1);
		int firstBar = 1;
		int firstFootnote = 1;
		boolean startsWithUnfinishedBar = false;
		// For each system
		while (sbiInd + 1 != nextSbiInd) {
			RhythmSymbol prevRs = null;
			List<String[]> staffContent = new ArrayList<>();
			int segment = 0;
			String currSysEnc = cleanEnc.substring(sbiInd + 1, nextSbiInd);
			int ssInd = -1; // fictional SS before first symbol of system
			int nextSsInd = currSysEnc.indexOf(ss, ssInd + 1);
			String firstEncSymbol = currSysEnc.substring(0, nextSsInd);
			String lastEncSymbol = null;
			// For each encoded symbol
			while (nextSsInd != -1) {
				String encSymbol = currSysEnc.substring(ssInd + 1, nextSsInd);
				int nextNextSsInd = currSysEnc.indexOf(ss, nextSsInd + 1);
				// nextEncSymbol can exist for all encoded symbols except the last -- 
				// i.e., as long as nextNextSsInd is not -1
				String nextEncSymbol = null;
				if (nextNextSsInd != -1) {
					nextEncSymbol = currSysEnc.substring(nextSsInd + 1, nextNextSsInd);
				}
				
				ConstantMusicalSymbol cms = Symbol.getConstantMusicalSymbol(encSymbol);
				TabSymbol ts = Symbol.getTabSymbol(encSymbol, currTss);
				RhythmSymbol rs = Symbol.getRhythmSymbol(encSymbol);
				MensurationSign ms = Symbol.getMensurationSign(encSymbol);
				// ConstantMusicalSymbol
				if (cms != null) {
					staffContent.add(new String[]{encSymbol, String.valueOf(segment), null, null});
					segment = segment + cms.getSymbol().length();
				}
				// TabSymbol
				else if (ts != null) { 
					staffContent.add(new String[]{
						encSymbol, String.valueOf(segment), currTss.getName(), null});
				}
				// RhythmSymbol
				else if (rs != null) {
					// In case all RS are added: add RS; always add any beam
					if (!ignoreRepeatedRs) {
						staffContent.add(new String[]{
							encSymbol, String.valueOf(segment), null, "true"});
					}
					// In case only differing RS are added: add rs only if prevRs does not 
					// exist, or if rs is not equal to prevRs; never add any beam
					// NB: because of possibly present beams, direct comparison does not work:
					// an RS and its beamed variant are considered inequal because they are 
					// defined as two different objects
					else {
						if (prevRs == null || 
							prevRs != null && rs.getDuration() != prevRs.getDuration()) {
							staffContent.add(new String[]{
								encSymbol, String.valueOf(segment), null, "false"});
						}
					}
					prevRs = rs;
				}     
				// MensurationSign
				else if (ms != null) {
					staffContent.add(new String[]{
						encSymbol, String.valueOf(segment), null, null});
				}
				// If encSymbol is not a CMS, but (i) is) the last TS of a vertical sonority 
				// (TS case); (ii) represents a rest (RS case); or (iii) is the only or the 
				// last symbol of a (compound) MS (MS case)
				if (cms == null && nextEncSymbol.equals(sp)) {
					segment++;
				}
				// Update indices
				ssInd = nextSsInd;
				nextSsInd = currSysEnc.indexOf(ss, ssInd + 1);
				lastEncSymbol = encSymbol;
			}
			// Make staff and populate it
			Staff staff = new Staff(tssSelected.getType(), staffLength);
			staff.populate(staffContent);
			// Add footnotes
			List<Integer> currFootnoteSegmentInds = footnoteSegmentInds.get(staffInd);
			staff.addFootnoteNumbers(currFootnoteSegmentInds, firstFootnote);
			firstFootnote += currFootnoteSegmentInds.size();
			// Add bar numbers
			boolean startsWithBarline = assertEventType(firstEncSymbol, null, "barline");
			boolean endsWithBarline = assertEventType(lastEncSymbol, null, "barline");
			if (barlineSegmentInds.get(staffInd).size() > 0) {
				staff.addBarNumbers(barlineSegmentInds.get(staffInd), firstBar, 
					startsWithUnfinishedBar, startsWithBarline,	endsWithBarline);
			}

			// System traversed? Add to tab and update information for the next system
			tab += staff.visualise(
				!startsWithBarline, staffLength + 
				(longestStaffStartsWithBarline ? Staff.LEFT_MARGIN - 1 : Staff.LEFT_MARGIN)
			) + SPACE_BETWEEN_STAFFS;
			startsWithUnfinishedBar = endsWithBarline ? false : true;
			if (staffInd < barlineSegmentInds.size() - 1) {
				firstBar = getSystemBarNumbers().get(staffInd + 1).get(0);
			}
			staffInd++;
			sbiInd = nextSbiInd;
			nextSbiInd = cleanEnc.indexOf(sbi, sbiInd + 1);
		}

		// Add formatted footnotes
		if (showFootnotes) {
			tab += SPACE_BETWEEN_STAFFS.repeat(2);
			tab += visualiseFootnotes(tssSelected);
		}
		return tab;
	}


	/**
	 * Gets, per system, the segment indices in the Staff of the events of the given type.
	 * 
	 * @param type The type of event: "footnote" or "barline".
	 *  
	 * @return A <code>List</code> of <code>List</code>s, each of which represents a system, 
	 * and contains the segment indices in the Staff of the events of the given 
	 * type. NB:
	 * <ul>
	 * <li>In case of a system without any events of the given type, the <code>List</code>
	 *     remains empty.</li>
	 * <li>In case of a barline that spans multiple segments, the index of the first 
	 *     pipe segment is given.</li>
	 * </ul>
	 */
	// TESTED
	List<List<Integer>> getStaffSegmentIndices(String type) {
		List<List<Integer>> segmentInds = new ArrayList<>();

		List<Event> events = getEvents();

		int currSystem = 1;
		List<Integer> segmentIndsCurrSystem = new ArrayList<>();
		int currSegmentInd = 0;
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			String currEvent = event.getEncoding();
			// In case of a barline, the bar number/footnote indicator is added 
			// above/below the first pipe char, so currSegmentInd must be 
			// incremented with the index in currEvent of that pipe char
			boolean isBarlineEvent = assertEventType(currEvent, null, "barline");
			int charsAfterFirstPipe = 0;
			if (isBarlineEvent) {
				int indFirstPipe = currEvent.indexOf(Symbol.BARLINE.getEncoding()); 
				currSegmentInd += indFirstPipe;
				// In case of a multiple-char barline: determine how many chars follow
				// the first pipe char (the extra -1 is for the SS at the end of currEvent)
				if (currEvent.length() > 1) {
					charsAfterFirstPipe = (currEvent.length() - 1 - 1) - indFirstPipe;
				}
			}
			// Exclude follow-up footnotes (consisting only of a FOOTNOTE_INDICATOR), 
			// missing barlines footnotes, and misplaced barlines footnotes
			String fn = event.getFootnote(); 
			boolean isFootnoteEvent = 
				fn != null && !(fn.equals(FOOTNOTE_INDICATOR) ||
				fn.startsWith(FOOTNOTE_INDICATOR + NO_BARLINE_TEXT) ||
				fn.startsWith(FOOTNOTE_INDICATOR + MISPLACED_BARLINE_TEXT));
			// Add to list
			if ((type.equals("footnote") && isFootnoteEvent) || 
				(type.equals("barline") && isBarlineEvent)) {
				segmentIndsCurrSystem.add(currSegmentInd);
			}
			// Increment currSegmentInd to go to the next segment. If barline event:
			// increment with the number of chars after the first pipe char in the 
			// barline + 1. If not (i.e., if TS, RS, rest, or MS event): increment 
			// with 2: 1 for the event itself and 1 for the space following it
			currSegmentInd = 
				isBarlineEvent ? currSegmentInd + (charsAfterFirstPipe + 1) : 
				currSegmentInd + 2;
			// Last bar in the system: add and reset
			if ((i < events.size() - 1 && events.get(i+1).getSystem() > currSystem) || 
				i == events.size() - 1) {
				segmentInds.add(segmentIndsCurrSystem);
				segmentIndsCurrSystem = new ArrayList<>();
				currSegmentInd = 0;
				currSystem++;
			}
		}
		return segmentInds;
	}


	/** 
	 *  Determines the staff length by calculating the number of segments needed for the longest
	 *  system. The number of segments needed for a system can be calculated by looking at the 
	 *  CMS only, as it equals the sum of (i) twice the system's number of spaces (each space 
	 *  is preceded by an event, and both the event and the space need one segment); and (ii) 
	 *  the total length of all the system's barlines (including any decorative opening barline).
	 *  
	 *  @return A <code>List</code> of <code>Integer[]</code>s, containing
	 * <ul>
	 * <li>As element 0: an <code>Integer[]</code> containing</li>
	 *     <ul>
	 *     <li>As element 0: the length of the staff, measured in staff segments.</li>
	 *     <li>As element 1: the sequence number (0-based) of that staff.</li>
	 *     </ul>
	 * </li>
	 * <li>As element 1: an <code>Integer[]</code> containing, for each staff, whether (1)
	 *     or not (0) that staff starts with a decorative opening barline.
	 * </li>
	 * </ul>
	 **/
	// TESTED
	List<Integer[]> getStaffLength() {
		List<Integer[]> slInfo = new ArrayList<>();

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		String cleanEncoding = getCleanEncoding();
		String[] allSystems = cleanEncoding.substring(0, cleanEncoding.indexOf(ebi)).split(sbi);

		slInfo.add(new Integer[]{0, 0});
		slInfo.add(new Integer[allSystems.length]);

		for (int i = 0; i < allSystems.length; i++) {
			String system = allSystems[i];
			int lengthCurrSystem = 0;
			int ssIndex = -1;
			int nextSsIndex = system.indexOf(ss, ssIndex + 1);
			// For each symbol
			while (nextSsIndex != -1) {
				String symbol = system.substring(ssIndex + 1, nextSsIndex);
				// If symbol is a CMS       
				if (Symbol.getConstantMusicalSymbol(symbol) != null) { 
					// a. If symbol is a space, increment by 2 (space + preceding event)
					if (symbol.equals(sp)) {
						lengthCurrSystem += 2;
					}
					// b. If symbol is any CMS but a space, increment by length of the CMS 
					else {
						lengthCurrSystem += symbol.length();
					}
				}
				ssIndex = nextSsIndex;
				nextSsIndex = system.indexOf(ss, ssIndex + 1);

			}
			// Reset
			if (lengthCurrSystem > slInfo.get(0)[0]) {
				slInfo.get(0)[0] = lengthCurrSystem;
				slInfo.get(0)[1] = i;
			}
			// Update 
			slInfo.get(1)[i] = Encoding.assertEventType(
				system.substring(0, system.indexOf(ss)), null, "barline"
			) ? 1 : 0;
		}

		return slInfo;
	}


	/**
	 * Gets the bar numbers for each system. If a system ends with an incomplete 
	 * bar, the next systems begins with that same bar.
	 *  
	 * @return A <code>List</code>, each element of which represents a system as a 
	 * <code>List</code> of <code>Integer</code>s.
	 */
	// TESTED
	List<List<Integer>> getSystemBarNumbers() {
		List<List<Integer>> systemBarNumbers = new ArrayList<>();

		List<Event> events = getEvents();
		List<Integer> barsCurrSystem = new ArrayList<>();
		int currSystem = 1;
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			int bar = event.getBar();
			if (!barsCurrSystem.contains(bar)) {
				barsCurrSystem.add(bar);
			}
			// Last bar in the system: add and reset
			if ((i < events.size() - 1 && events.get(i+1).getSystem() > currSystem) || 
				i == events.size() - 1) {
				systemBarNumbers.add(barsCurrSystem);
				barsCurrSystem = new ArrayList<>();
				currSystem++;
			}
		}
		return systemBarNumbers;
	}


	// NOT TESTED
	StringBuffer visualiseFootnotes(TabSymbolSet argTss) {
		StringBuffer footnotesStr;

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();
		String ebi = Symbol.END_BREAK_INDICATOR;
		String sts = Staff.STAFF_SEGMENT;
		int numTabs = 3;
		String emptyLine = ToolBox.tabify("", numTabs, false);

		// Get all footnoteEvents
		List<Event> footnoteEvents = new ArrayList<>();
		getEvents().forEach(e -> { 
			if (e.getFootnote() != null) { footnoteEvents.add(e); }
		});

		// Remove any follow-up footnotes; collect no-barline and misplaced-barline bars
		List<Event> footnotes = new ArrayList<>();
		List<Integer> noBarlineBars = new ArrayList<>();
		List<Integer> misplacedBarlineBars = new ArrayList<>();
		int count = 1;		
		for (Event e : footnoteEvents) {
			// If not a follow-up footnote (containing only a FOOTNOTE_INDICATOR)
			if (!e.getFootnote().equals(FOOTNOTE_INDICATOR)) {
				if (e.getFootnote().startsWith(FOOTNOTE_INDICATOR + NO_BARLINE_TEXT)) {
					noBarlineBars.add(e.getBar());
				}
				else if (e.getFootnote().startsWith(FOOTNOTE_INDICATOR + MISPLACED_BARLINE_TEXT)) {
					misplacedBarlineBars.add(e.getBar());
				}
				else {
					footnotes.add(new Event(e.getEncoding(), e.getSystem(), e.getBar(), 
						e.getFootnote(), "#" + count));
					count++;
				}
			}
		}

		// Get header; remove meterInfo (NB: not all fields are actually needed)
		String hdr = getHeader();
		int startInd = hdr.indexOf(METER_INFO_TAG);
		int endInd = hdr.indexOf(CLOSE_METADATA_BRACKET, startInd);
		String miStr = hdr.substring(startInd, endInd);
		hdr = hdr.replace(miStr, METER_INFO_TAG + ":");

		// 1. Add footnote lists to allFootnoteLists. A footnote list is a list of 
		// Staff.STAFF_LINES strings, representing the individual lines of a footnote
		List<List<String>> allFootnoteLists = new ArrayList<>();
		List<Integer> textFootnotes = new ArrayList<>();
		String fnNumBuffer = "    ";
		for (Event currFn : footnotes) {
			String currFnStr = currFn.getFootnote().trim();
			currFnStr = 
				currFnStr.substring(currFnStr.indexOf(FOOTNOTE_INDICATOR) + 1, currFnStr.length());
			int currFnNum = 
				Integer.parseInt(currFn.getFootnoteID().trim().substring("#".length()));
			String currFnNumStr = "(" + currFnNum + ")" + (currFnNum < 10 ? " " : "");
			
			// Make currFnList, containing the current footnote split up into lines
			List<String> currFnList = new ArrayList<>();
			// a. Staff footnote
			if (currFnStr.contains("'")) {
				// 1. Get the staff part of the footnote
				String currFnStaffPart = "";
				// Get the encoding to be visualised
				String currFnEnc = 
					currFnStr.substring(currFnStr.indexOf("'") + 1, currFnStr.lastIndexOf("'"));
				if (!currFnEnc.endsWith(ss)) {
					currFnEnc += ss;
				}
				if (!currFnEnc.endsWith(sp + ss)) {
					currFnEnc += sp + ss;
				}

				// 1. Add each footnote event (split into lines) to allCurrFnEventStaffPartSplit
				List<String[]> allCurrFnEventStaffPartSplit = new ArrayList<>();
				for (String currFnEventEnc : currFnEnc.split(sp + ss)) {
					// The encoding can have doubled symbols: two symbols on a single course,
					// either by mistake or intentionally (German tablature unisons)
					// Count unique symbols
					String[] currFnEventEncSplit = currFnEventEnc.split("\\" + ss);
					int numUniqueSymbols = Arrays.asList(currFnEventEncSplit)
						.stream().distinct().collect(Collectors.toList()).size();

					// Re-add space and SS 
					currFnEventEnc += sp + ss;

					String currFnEventStaffPart;
					String[] currFnEventStaffPartSplit;
					// If the event does not contain a doubled symbol, or if the tablature style 
					// to visualise is German
					if (currFnEventEncSplit.length == numUniqueSymbols || argTss.getType().equals("German")) {
						// 1. Make currFnEventStaffPart
						currFnEventStaffPart = new Encoding(
							hdr + "\r\n" + currFnEventEnc + ebi, "", Stage.RULES_CHECKED
						).visualise(argTss, false, false, true);
						// 2. Split currFnEventStaffPart into lines
						currFnEventStaffPartSplit = currFnEventStaffPart.split("\n");
						// 3. Add
						allCurrFnEventStaffPartSplit.add(currFnEventStaffPartSplit);
					}					
					// If the event contains a doubled symbol
					else {
						String dbld = ToolBox.getDuplicates(Arrays.asList(currFnEventEncSplit)).get(0);

						// 1. Make currFnEventStaffPart
						// Remove doubled symbol from currFnEventEnc
						currFnEventEnc = currFnEventEnc.replaceFirst(dbld, "");
						currFnEventStaffPart = new Encoding(
							hdr + "\r\n" + currFnEventEnc + ebi, "", Stage.RULES_CHECKED
						).visualise(argTss, false, false, true);

						// 2. Split currFnEventStaffPart into lines and adapt them
						currFnEventStaffPartSplit = currFnEventStaffPart.split("\n");
						// Get changeLine for doubled symbol
						TabSymbol tsInArgTss = Symbol.getTabSymbolEquivalent(
							Symbol.getTabSymbol(dbld, getTabSymbolSet()), argTss
						);
						String fret = tsInArgTss.getSymbol();
						int course = tsInArgTss.getCourse();
						int changeLine = -1;
						if (argTss == TabSymbolSet.FRENCH || argTss == TabSymbolSet.SPANISH) {
							changeLine = Staff.TOP_LINE + (course - 1);
						}
						else if (argTss == TabSymbolSet.ITALIAN) {
							changeLine = Staff.BOTTOM_LINE - (course - 1);
						}
						for (int i = 0; i < currFnEventStaffPartSplit.length; i++) {
							String line = currFnEventStaffPartSplit[i];
							// Insert staff segments
							if (line.contains(sts) && i != Staff.RHYTHM_LINE) {
								line = ToolBox.insertIntoString(
									line, sts.repeat(2), (line.lastIndexOf(sts) + 1)
								);
							}
							// Add empty string
							else {
								line = line + " ".repeat(2);
							}
							// Add doubled symbol
							if (i == changeLine) {
								int ind = ToolBox.getFirstIndexOfNot(
									line, Arrays.asList(new String[]{" "})
								);
								String toReplace = line.substring(
									ind, line.lastIndexOf(sts) + 1
								);
								String replacement = 
									toReplace.substring(0, 1) + "/" + fret + sts;
								line = line.replace(toReplace, replacement);
							}
							currFnEventStaffPartSplit[i] = line;
						}
						// 3. Add
						allCurrFnEventStaffPartSplit.add(currFnEventStaffPartSplit);
					}
				}

				// 2. Combine allCurrFnEventStaffPartSplit into currFnStaffPart
				int numLines = allCurrFnEventStaffPartSplit.get(0).length;
				int numEvents = allCurrFnEventStaffPartSplit.size();
				// For each line
				for (int i = 0; i < numLines; i++) {
					// For each event
					for (int j = 0; j < numEvents; j++) {
						// Get line i for event j
						String l = allCurrFnEventStaffPartSplit.get(j)[i];
						// Remove whitespace if there is more than one event
						if (numEvents > 1) {
							// First and middle event : remove right margin
							if (j == 0 || (j > 0 && j < numEvents-1)) {
								l = l.substring(0, l.length() - Staff.RIGHT_MARGIN);
							}
							// Middle event and last event: remove left margin
							if ((j > 0 && j < numEvents-1) || (j == numEvents-1)) {
								l = l.substring(Staff.LEFT_MARGIN);
							}
						}
						currFnStaffPart += l;
					}
					if (i < numLines-1) {
						currFnStaffPart += "\n"; 
					}
				}

				// Split currFnStaffPart into lines. currFnStaffPartSplit has Staff.STAFF_LINES
				// lines, and both begins with an empty line and ends with one (for the 
				// content of Staff.BAR_NUMS_LINE and Staff.FOOTNOTES_LINE, respectively)
				String[] currFnStaffPartSplit = currFnStaffPart.split("\n");

				// 2. Get the text part of the footnote (generally, 'in source')
				String currFnTextPart = currFnStr.substring(
					currFnStr.lastIndexOf("'") + 1, currFnStr.length()
				).trim();

				// Break the text part into lines and add them to the staff part lines, 
				// starting at Staff.UPPER_MIDDLE_LINE
				int lenLine = 
					fnNumBuffer.length() + 
					(currFnStaffPartSplit[Staff.TOP_LINE].length() - Staff.RIGHT_MARGIN);
				int maxLenTextPart = ((ToolBox.TAB_LEN * numTabs) - 1) - (lenLine + 1);
				List<String> currFnTextPartBroken = 
					ToolBox.breakIntoLines(currFnTextPart, maxLenTextPart);
				for (int i = 0; i < currFnStaffPartSplit.length; i++) {
					String line = currFnStaffPartSplit[i];
					// Remove right margin
					line = line.substring(0, line.length() - Staff.RIGHT_MARGIN);
					// Add line with text part
					if (i >= Staff.UPPER_MIDDLE_LINE && 
						i < Staff.UPPER_MIDDLE_LINE + currFnTextPartBroken.size()) {
						// Prepend prefix: footnote number for first line; buffer for other
						line = (i == Staff.UPPER_MIDDLE_LINE) ? currFnNumStr + line : 
							fnNumBuffer + line;
						// Append text part
						line += " " + currFnTextPartBroken.get(i - Staff.UPPER_MIDDLE_LINE);
					}
					// Add other line
					else {
						// If line contains only whitespace: use only tabs
						if ((i == Staff.BAR_NUMS_LINE || i == Staff.DIAPASONS_LINE_ITALIAN || 
							 i == Staff.DIAPASONS_LINE_OTHER || i == Staff.FOOTNOTES_LINE) 
							&& ToolBox.getFirstIndexOfNot(
							line, Arrays.asList(new String[]{" ", "\t"})) == -1) {
							line = "";
						}
						else {
							line = fnNumBuffer + line;
						}
					}
					currFnList.add(ToolBox.tabify(line, numTabs, false));
				}
			}
			// b. Text footnote
			else {
				textFootnotes.add(currFnNum - 1);
				// Prepend with empty lines
				for (int j = 0; j < Staff.UPPER_MIDDLE_LINE; j++) {
					currFnList.add(emptyLine);
				}
				// Break footnote into lines and add
				int maxLen = ((ToolBox.TAB_LEN * numTabs) - 1) - (currFnNumStr.length() + 1);
				for (String line : ToolBox.breakIntoLines(currFnStr, maxLen)) {
					boolean footnoteTextLine = currFnList.size() == Staff.UPPER_MIDDLE_LINE;
					currFnList.add(ToolBox.tabify(
						((footnoteTextLine ? currFnNumStr : fnNumBuffer) + " " + line), 
						numTabs, false));
				}
				// Append with empty lines
				for (int j = currFnList.size(); j < Staff.LEN_STAFF_GRID; j++) {
					currFnList.add(emptyLine);
				}
			}
			// Add currFnList to allFootnoteLists
			allFootnoteLists.add(currFnList);
		}
		// Remove the Staff.RHYTHM_LINE and Staff.FOOTNOTES_LINE lines, which are always 
		// unused, from each footnote list
		for (int i = 0; i < allFootnoteLists.size(); i++) {
			allFootnoteLists.set(i, 
				allFootnoteLists.get(i).subList(Staff.RHYTHM_LINE, Staff.FOOTNOTES_LINE));
		}

		// 2. Make footnote list groups, stringify them, and add them to footnotesStr. A 
		// footnote list group is a list of n footnote lists (where n = fnListGroupSize), 
		// whose stringifications are to be displayed next to one another
		footnotesStr = new StringBuffer();
		// Pad allFootnoteLists with nulls to make divisible by fnListGroupSize
		int numFn = footnotes.size();
		int fnListGroupSize = 3;
		while (numFn % fnListGroupSize != 0) {
			allFootnoteLists.add(null);
			numFn = allFootnoteLists.size();
		}

		// Create and stringify footnote list groups
		for (int i = 0; i < numFn; i+=fnListGroupSize) {
			List<List<String>> currFnListGroup = 
				allFootnoteLists.subList(i, i + fnListGroupSize);

			String currFnListGroupStr = "";
			// For each line in the footnote list
			for (int j = 0; j < currFnListGroup.get(0).size() ; j++) {
				// For each footnote list in the footnote list group
				for (int k = 0 ; k < currFnListGroup.size(); k++) {
					// Append current line in each footnote list
					if (currFnListGroup.get(k) != null) {
						currFnListGroupStr += currFnListGroup.get(k).get(j);
					}	
				}
				currFnListGroupStr += "\r\n";
			}
			currFnListGroupStr += "\r\n";
			footnotesStr.append(currFnListGroupStr);
		}

		// Add missing and misplaced barlines information
		List<List<Integer>> noAndMisplaced = new ArrayList<>();
		noAndMisplaced.add(noBarlineBars);
		noAndMisplaced.add(misplacedBarlineBars);
		for (int i = 0; i < noAndMisplaced.size(); i++) {
			List<Integer> l = noAndMisplaced.get(i);
			if (l.size() > 0) {
				String barsStr = "";
				List<List<Integer>> groups = ToolBox.groupListOfIntegers(l);
				for (int j = 0; j < groups.size(); j++) {
					List<Integer> group = groups.get(j);
					barsStr = 
						(group.size() == 1 ? barsStr + "[" + group.get(0) + "]" : 
						barsStr + "[" + group.get(0) + "-" + group.get(group.size()-1) + "]");
					if (j < groups.size() - 1) {
						barsStr += ", ";
					}
				}
				footnotesStr.append(
					(i == 0 ? "No barline in source at end of " : 
					"Misplaced barline in source in middle of ") + 
					(l.size() == 1 ? "bar " : "bars ") + barsStr +
					(i == 0 ? "\r\n" : ""));
			}
		}

		return footnotesStr;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//  other
	//
	/**
	 * Decomposes the Encoding into its individual event encodings.
	 * 
	 * @param complementRs If <code>true</code>, the last active RS is assigned to each 
	 *                     TS event encoding that is lacking one.
	 * @param includeBreakIndicators If <code>true</code>, SBI and EBI are included as 
	 * 							     separate events.
	 * @return A <code>List<String></code> of all event encodings in the Encoding.
	 */
	// TESTED
	public List<String> decompose(boolean complementRs, boolean includeBreakIndicators) {
		List<String> decomposed = new ArrayList<>();

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;
		TabSymbolSet tss = getTabSymbolSet();

		// List all events and barlines for each system
		List<Event> events = getEvents();
		RhythmSymbol currRs = null;
		for (int i = 0; i < events.size(); i++) {
			Event curr = events.get(i);
			String e = curr.getEncoding();
			int currSystem = curr.getSystem();
			int currBar = curr.getBar();
			String firstSymbol = e.substring(0, e.indexOf(ss));
			boolean isBarline = 
				Symbol.getConstantMusicalSymbol(firstSymbol) != null && 
				Symbol.getConstantMusicalSymbol(firstSymbol).isBarline();
			RhythmSymbol rs = Symbol.getRhythmSymbol(firstSymbol);

			// If event is a TS event with no RS: prepend RS
			if (complementRs) {
				if (rs == null) {
					if (assertEventType(e, tss, "TabSymbol")) {
						e = currRs.getEncoding() + ss + e;
					}
				}
				else {
					currRs = rs;
				}
			}
			// If event is not a barline: append space
			if (!isBarline) {
				e += Symbol.SPACE.getEncoding() + ss;
			}
			// Special case for barline followed by barline (this happens when a 
			// full-bar note is tied at its left (see end quis_me_statim): these 
			// two bars must be seen as a single event, so the first barline must 
			// be added to the event added last
			else {
				if (i < events.size() - 1) {
					String nextE = events.get(i + 1).getEncoding();
					int nextBar = events.get(i + 1).getBar();
					boolean nextIsBarline = 
						Symbol.getConstantMusicalSymbol(nextE.substring(0, nextE.lastIndexOf(ss))) != null &&
						Symbol.getConstantMusicalSymbol(nextE.substring(0, nextE.lastIndexOf(ss))).isBarline();
					if (isBarline && (nextIsBarline && currBar == nextBar)) {
						// Append first barline to event added last
						int lastInd = decomposed.size()-1;
						decomposed.set(lastInd, decomposed.get(lastInd) + e);
						// Set e to second barline and skip event at i+1
						e = nextE;
						i++;
					}
				}
			}
			// Add event
			decomposed.add(e);
			// If event is the last in the system: add SBI
			if (i < events.size() - 1) {				
				if (includeBreakIndicators) {
					if (events.get(i + 1).getSystem() == currSystem + 1) {
						decomposed.add(sbi);
					}
				}
			}
		}
		if (includeBreakIndicators) {
			decomposed.add(ebi);
		}
		return decomposed;
	}


	/**
	 * Checks whether the Encoding contains triplets.
	 * 
	 * Returns <code>true</code> if the Encoding contains triplets, and <code>false </code> if not.
	 */
	// TESTED
	public boolean containsTriplets() {
		if (getCleanEncoding().contains(RhythmSymbol.TRIPLET_INDICATOR)) {
			return true;
		}
		else {
			return false;
		}
	}


	public void overwriteTuning(String tuning) {
		getMetadata().put(TUNING_TAG, tuning);
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Encoding)) {
			return false;
		}
		Encoding e = (Encoding) o;
		return
			getPiecename().equals(e.getPiecename()) &&
			getRawEncoding().equals(e.getRawEncoding()) &&
			getCleanEncoding().equals(e.getCleanEncoding()) &&
			getMetadata().equals(e.getMetadata()) &&
			getHeader().equals(e.getHeader()) &&
			getTabSymbolSet().equals(e.getTabSymbolSet()) &&
			getEvents().equals(e.getEvents()) &&
			getTimeline().equals(e.getTimeline()) &&
			getListsOfSymbols().equals(e.getListsOfSymbols()) &&
			getListsOfStatistics().equals(e.getListsOfStatistics());
	}


	// TESTED BUT NOT IN USE -->
	// TESTED
	static int getTotalEventLength(List<Event> events, TabSymbolSet tss) {
		int eventsLength = 0;
		int prev = -1;
		for (Event e : events) {
			String eStr = e.getEncoding();
			if (assertEventType(eStr, tss, "RhythmSymbol") || assertEventType(eStr, tss, "rest")) {
				int dur = Symbol.getRhythmSymbol(
					eStr.substring(0, eStr.indexOf(Symbol.SYMBOL_SEPARATOR))).getDuration();
				eventsLength += dur;
				prev = dur;
			}
			else {
				if (assertEventType(eStr, tss, "TabSymbol")) {
					eventsLength += prev;
				}
			}
		}
		return eventsLength;
	}


	// DEPRECATED -->
	private static String[] checkComplianceToMetricStructure(Timeline tl, TabSymbolSet tss, List<Event> events) {
		int timelineLength = tl.getLength();
		int eventsLength = getTotalEventLength(events, tss);

		if (timelineLength != eventsLength) {
			int diff = Math.abs(timelineLength - eventsLength);
			int numNotes = 0;
			String type = "";
			if (diff % RhythmSymbol.MINIM.getDuration() == 0) {
				numNotes = (int) new Rational(diff, RhythmSymbol.MINIM.getDuration()).toDouble();
				type = "quarter";
			}
			else if (diff % RhythmSymbol.SEMIMINIM.getDuration() == 0) {
				numNotes = (int) new Rational(diff, RhythmSymbol.SEMIMINIM.getDuration()).toDouble();
				type = "eighth";
			}
			else {
				numNotes = diff;
			}
			return new String[]{
				String.valueOf(numNotes), type, timelineLength > eventsLength ? "short" : "long"
			};
		}
		return null;
	}


	/**
	 * Gets all the events. 
	 * 
	 * @return A <code>List<String></code> of all events in the encoding; the SBI are
	 * kept in place (i.e, form separate events). A rhythm symbol (the last active one)
	 * is assigned to each event that is lacking one.
	 */
	private List<String> getEventsOLD() {
		String enc = splitHeaderAndEncodingOLD()[1];

		String[] systems = enc.split(Symbol.SYSTEM_BREAK_INDICATOR);

		List<String> allEvents = new ArrayList<>();
		for (int i = 0; i < systems.length; i++) {
			String system = systems[i];
			// List all events and barlines for the current system
			String[] symbols = system.split("\\" + Symbol.SYMBOL_SEPARATOR);
			List<String> currEvents = new ArrayList<>();
			String currEvent = "";
			for (int j = 0; j < symbols.length; j++) {
				String s = symbols[j];
				currEvent += s + Symbol.SYMBOL_SEPARATOR;
				// Add event after each space or barline (i.e., CMS)
				if (Symbol.CONSTANT_MUSICAL_SYMBOLS.values().contains(Symbol.getConstantMusicalSymbol(s))) {
					// Special case for barline followed by barline (this happens when a 
					// full-bar note is tied at its left (see end quis_me_statim): these two bars
					// must be seen as a single event, so the second barline must be added too
					if (j < symbols.length - 2) { 						
						String nextS = symbols[j+1];
						String nextNextS = symbols[j+2];
						if (Symbol.CONSTANT_MUSICAL_SYMBOLS.keySet().contains(nextS) &&
							Symbol.CONSTANT_MUSICAL_SYMBOLS.keySet().contains(nextNextS)) {
							currEvent += nextS + Symbol.SYMBOL_SEPARATOR;
							j++;
						}
					}
					currEvents.add(currEvent);
					currEvent = "";
				}
			}
			allEvents.addAll(currEvents);
			if (i != systems.length-1) {
				allEvents.add(Symbol.SYSTEM_BREAK_INDICATOR);
			}
		}

		// Add a RS to each event lacking one
		String activeRs = "";
		for (int j = 0; j < allEvents.size(); j++) {
			String t = allEvents.get(j);
			if (!t.equals(Symbol.SYSTEM_BREAK_INDICATOR)) {
				String first = t.substring(0, t.indexOf(Symbol.SYMBOL_SEPARATOR));
				// RS: set activeRs
				if (Symbol.getRhythmSymbol(first) != null) {
					activeRs = first;
				}
				// No RS: prepend activeRs to event if applicable  
				else {
					// Only if event is not a MS or a CMS (barline)
					if (Symbol.getMensurationSign(first) == null && 
						Symbol.getConstantMusicalSymbol(first) == null) {
						allEvents.set(j, activeRs + Symbol.SYMBOL_SEPARATOR + t);
					}
				}
			}
		}
		return allEvents;
	}


	private String read(File f) {
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return rawEncoding;
	}


	/**
	 * Checks the <code>Encoding</code> to see whether 
	 * <ul>
	 * <li>All VALIDITY RULES are met.</li> 
	 * <li>There are no unknown or missing symbols.</li> 
	 * <li>All LAYOUT RULES are met.</li> 
	 * </ul>
	 * NB: The the <code>Encoding</code> must always be checked in the sequence 
	 *     <code>checkValidityRules()</code> - <code>checkSymbols()</code> - 
	 *     <code>checkLayoutRules()</code>.<br><br>
	 * 
	 * @param rawEnc
	 * @param cleanEnc
	 * @param tss
	 * @return <ul>
	 * <li><code>null</code> if and only if all conditions are met.</li> 
     * <li>A <code>String[]</code> containing the relevant error information if not.</li>
     * </ul>
	 */
	private static String[] checkEncodingOLD(String rawEnc, String cleanEnc, TabSymbolSet tss) {
		Integer[] indsAligned = alignRawAndCleanEncoding(rawEnc, cleanEnc);
		String[] checkVR = checkValidityRules(cleanEnc, indsAligned);
		if (checkVR != null) {
			return checkVR;
		}
		else {
			String[] checkSymbols = checkSymbols(cleanEnc, tss, indsAligned);
			if (checkSymbols != null) {
				return checkSymbols;    
			}
			else {
				String[] checkLR = checkLayoutRules(cleanEnc, tss, indsAligned);
				return checkLR != null ? checkLR : null;
			}
		}
	}


	private List<String[]> makeEventsOLD() {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		String sp = Symbol.SPACE.getEncoding();
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		// Remove any line breaks, EBI, and leading and trailing whitespace from rawEnc
		String rawEnc = getRawEncoding();
		rawEnc = rawEnc.replaceAll("\r", "").replaceAll("\n", "").replaceAll(ebi, "");
		rawEnc = rawEnc.trim();

		// List all comments
		List<String> nonEditorialComments = new ArrayList<>();
		List<String> editorialComments = new ArrayList<>();
		for (int i = 0; i < rawEnc.length(); i++) {
			int ombInd = rawEnc.indexOf(omb, i);
			int cmbInd = rawEnc.indexOf(cmb, ombInd + 1);
			String comment = rawEnc.substring(ombInd, cmbInd+1);
			// Non-editorial comment
			if (!comment.startsWith(omb + FOOTNOTE_INDICATOR)) {
				nonEditorialComments.add(comment);
			}
			// Editorial comment
			else {
				editorialComments.add(
					comment.substring(comment.indexOf(omb)+1, comment.indexOf(cmb)));
			}
			if (cmbInd == rawEnc.lastIndexOf(cmb)) {
				break;
			}
			i = cmbInd;
		}
		// Remove all non-editorial comments from rawEnc
		for (String comment : nonEditorialComments) {
			rawEnc = rawEnc.replace(comment, "");
		}
		// Temporarily substitute all editorial comments
		for (int i = 0; i < editorialComments.size(); i++) {
			rawEnc = rawEnc.replace(omb + editorialComments.get(i) + cmb, omb + i + cmb);
		}

		// List events per bar
		List<String[]> events = new ArrayList<>();
		int systemCounter = 1;
		int barCounter = 1;
		int commentCounter = 1;
		String currEvent = "";
		for (int i = 0; i < rawEnc.length(); i++) {
			int ssInd = rawEnc.indexOf(ss, i);
			String symbol = rawEnc.substring(i, ssInd);
			boolean symbolContainsComment = symbol.contains(omb);
			boolean firstInSystem = (i == 0) || symbol.startsWith(sbi);
			symbol = firstInSystem ? symbol.replace(sbi, "") : symbol;
			// Each symbol is either a space, a barline, or a TS, RS, or MS
			// Space: add to events
			if (symbol.equals(sp)) {
				boolean eventContainsComment = currEvent.contains(omb);
				events.add(new String[]{
					eventContainsComment ? removeComment(currEvent) : currEvent,
					String.valueOf(systemCounter),
					String.valueOf(barCounter),
					eventContainsComment ? editorialComments.get(commentCounter-1) : null,
					eventContainsComment ? "#" + String.valueOf(commentCounter) : null
				});
				if (eventContainsComment) {
					commentCounter++;
				}
				currEvent = "";
			}
			// Barline: add to events
			else if (true) {
//			else if (!symbolContainsComment && ConstantMusicalSymbol.isBarline(symbol) ||
//				symbolContainsComment && ConstantMusicalSymbol.isBarline(removeComment(symbol))) {
				// Decorative opening barline: increment system
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
				events.add(new String[]{
					symbolContainsComment ? removeComment(symbol) + ss : symbol + ss,
					String.valueOf(systemCounter),
					String.valueOf(barCounter),
					symbolContainsComment ? editorialComments.get(commentCounter-1) : null,
					symbolContainsComment ? "#" + String.valueOf(commentCounter) : null
				});
				if (symbolContainsComment) {
					commentCounter++;
				}
				// Not a decorative opening barline: increment bar
				if (!firstInSystem) {
					barCounter++;
				}
			}
			// TS, RS, or MS: add to currEvent
			else {
				currEvent += symbol + ss;
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
			}
			i = ssInd;
		}
		return events;
	}


	private List<List<String[]>> makeEventsOLDEST() {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		String sp = Symbol.SPACE.getEncoding();
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		// Remove any line breaks, EBI, and leading and trailing whitespace from rawEnc
		String rawEnc = getRawEncoding();
		rawEnc = rawEnc.replaceAll("\r", "").replaceAll("\n", "").replaceAll(ebi, "");
		rawEnc = rawEnc.trim();

		// List all comments
		List<String> nonEditorialComments = new ArrayList<>();
		List<String> editorialComments = new ArrayList<>();
		for (int i = 0; i < rawEnc.length(); i++) {
			int ombInd = rawEnc.indexOf(omb, i);
			int cmbInd = rawEnc.indexOf(cmb, ombInd + 1);
			String comment = rawEnc.substring(ombInd, cmbInd+1);
			// Non-editorial comment
			if (!comment.startsWith(omb + FOOTNOTE_INDICATOR)) {
				nonEditorialComments.add(comment);
			}
			// Editorial comment
			else {
				editorialComments.add(
					comment.substring(comment.indexOf(omb)+1, comment.indexOf(cmb)));
			}
			if (cmbInd == rawEnc.lastIndexOf(cmb)) {
				break;
			}
			i = cmbInd;
		}
		// Remove all non-editorial comments from rawEnc
		for (String comment : nonEditorialComments) {
			rawEnc = rawEnc.replace(comment, "");
		}
		// Temporarily substitute all editorial comments
		for (int i = 0; i < editorialComments.size(); i++) {
			rawEnc = rawEnc.replace(omb + editorialComments.get(i) + cmb, omb + i + cmb);
		}

		// List events per bar
		List<List<String[]>> eventsPerBar = new ArrayList<>();
		int systemCounter = 1;
		int barCounter = 1;
		int commentCounter = 1;
		List<String[]> currBar = new ArrayList<>();
		String currEvent = "";
		for (int i = 0; i < rawEnc.length(); i++) {
			int ssInd = rawEnc.indexOf(ss, i);
			String symbol = rawEnc.substring(i, ssInd);
			boolean symbolContainsComment = symbol.contains(omb);
			boolean firstInSystem = (i == 0) || symbol.startsWith(sbi);
			boolean lastInSystem = 
				ssInd + 1 == rawEnc.length() || rawEnc.substring(ssInd + 1, ssInd + 2).equals(sbi);
			symbol = firstInSystem ? symbol.replace(sbi, "") : symbol;
			// Each symbol is either a space, a barline, or a TS, RS, or MS
			// Space
			if (symbol.equals(sp)) {
				// Add currEvent to currBar
				boolean eventContainsComment = currEvent.contains(omb);
				currBar.add(new String[]{
					eventContainsComment ? removeComment(currEvent) : currEvent,
					String.valueOf(systemCounter),
					String.valueOf(barCounter),
					eventContainsComment ? editorialComments.get(commentCounter-1) : null,
					eventContainsComment ? "#" + String.valueOf(commentCounter) : null
				});
				if (eventContainsComment) {
					commentCounter++;
				}
				// Incomplete bar (with no barline) at the end of the system: add currBar
				if (lastInSystem) {
					eventsPerBar.add(currBar);
					currBar = new ArrayList<>();
				}
				currEvent = "";
			}
			// Barline
			else if (true) {
//			else if (!symbolContainsComment && ConstantMusicalSymbol.isBarline(symbol) ||
//				symbolContainsComment && ConstantMusicalSymbol.isBarline(removeComment(symbol))) {
				// Decorative opening barline
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
				// Add symbol (barline) to currBar
				currBar.add(new String[]{
					symbolContainsComment ? removeComment(symbol) + ss : symbol + ss,
					String.valueOf(systemCounter),
					String.valueOf(barCounter),
					symbolContainsComment ? editorialComments.get(commentCounter-1) : null,
					symbolContainsComment ? "#" + String.valueOf(commentCounter) : null
				});
				if (symbolContainsComment) {
					commentCounter++;
				}
				// Barline (not a decorative opening barline): add currBar
				if (!firstInSystem) {
					eventsPerBar.add(currBar);
					currBar = new ArrayList<>();
					barCounter++;
				}
			}
			// TS, RS, or MS
			else {
				currEvent += symbol + ss;
				if (firstInSystem && i > 0) {
					systemCounter++;
				}
			}
			i = ssInd;
		}
		return eventsPerBar;
	}


//	private List<List<String[]>> makeExtendedEventsOLD() {
//		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
//		String oib = SymbolDictionary.OPEN_METADATA_BRACKET;
//		String cib = SymbolDictionary.CLOSE_METADATA_BRACKET;
//		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
//		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
//		String invertedSp = "<";
//		String invertedSbi = "\\";
//
//		String rawEnc = getRawEncoding();
//		// Remove all carriage returns and line breaks; remove leading and trailing whitespace
//		rawEnc = rawEnc.replaceAll("\r", "");
//		rawEnc = rawEnc.replaceAll("\n", "");
//		rawEnc = rawEnc.trim();
//		// Remove end break indicator
//		rawEnc = rawEnc.replaceAll(SymbolDictionary.END_BREAK_INDICATOR, "");
//		
//		// List all comments
//		List<String> allNonEditorialComments = new ArrayList<>();
//		List<String> allEditorialComments = new ArrayList<>();
//		for (int i = 0; i < rawEnc.length(); i++) {
//			int commOpenInd = rawEnc.indexOf(oib, i);
//			int commCloseInd = rawEnc.indexOf(cib, commOpenInd + 1);
//			String comment = rawEnc.substring(commOpenInd, commCloseInd+1);
//			// Non-editorial comment
//			if (!comment.startsWith(oib + FOOTNOTE_INDICATOR)) {
//				allNonEditorialComments.add(comment);
//			}
//			// Editorial comment
//			else {
//				allEditorialComments.add(comment.substring(comment.indexOf(oib)+1, 
//					comment.indexOf(cib)));
//				// In rawEnc, temporarily replace any spaces and SBIs within comments, so 
//				// that splitting on them (see below) remains possible
//				// NB: String.replaceFirst() does not work because of regex special characters
//				String temp = comment;
//				if (comment.contains(sp)) {
//					temp = temp.replace(sp, invertedSp);
//				}
//				if (comment.contains(sbi)) {
//					temp = temp.replace(sbi, invertedSbi);
//				}
//				rawEnc = ToolBox.replaceFirstInString(rawEnc, comment, temp);
//			}
//			if (commCloseInd == rawEnc.lastIndexOf(cib)) {
//				break;
//			}
//			else {
//				i = commCloseInd;
//			}
//		}
//		System.out.println(allEditorialComments);
//		System.out.println(allNonEditorialComments);
////		System.exit(0);
//
//		// Remove all non-editorial comments from rawEnc
//		for (String comment : allNonEditorialComments) {
//			rawEnc = rawEnc.replace(comment, "");
//		}
//
//		// To enable splitting, add a space after all barlines. NB: This will also affect any
//		// barlines in comments (but only if they are followed by a symbol separator!) - which
//		// is not a problem as the unadapted comments are stored in allNonEditorialComments		
//		List<String> barlinesAsString = new ArrayList<>();
//		ConstantMusicalSymbol.getBarlines().forEach(cms -> barlinesAsString.add(cms.getEncoding()));
////		for (ConstantMusicalSymbol cms : ConstantMusicalSymbol.CONSTANT_MUSICAL_SYMBOLS) {
////			if (cms != ConstantMusicalSymbol.SPACE) {
////				barlinesAsString.add(cms.getEncoding());
////			}
////		}
//
//		// Sort the barlines by length (longest first), so that they are replaced correctly
//		// (a shorter barline, e.g., :|, can be part of a longer one, e.g., :|:, leading to 
//		// partial replacement of the longer one)
//		// See https://stackoverflow.com/questions/29280257/how-to-sort-an-arraylist-by-its-elements-size-in-java
//		barlinesAsString.sort(Comparator.comparing(String::length).reversed());
//		// A barline is always preceded and followed by a SS. By including both these 
//		// SSs in the replace command, it is ensured that a shorter barline that is part 
//		// of a longer one does not accidentally replace part of the longer one. 
//		// Example: if s == "|:", any occurences of "|:." will be replaced, but also the 
//		// last two chars in any occurrences of "||:." By adding a SS also before s, 
//		// only occurrences of ".|:." will be replaced.
//		// NB: Any decorative barlines at the beginning of a system are NOT preceded by 
//		// a SS, and must be dealt with separately
//		for (String b : barlinesAsString) {
////			if (ConstantMusicalSymbol.isBarline(b)) {
//			String firstHalf, secondHalf;
//			int startInd = 0;
//			int breakInd = -1;
//
//			// If the encoding starts with a barline, add an auxiliary SBI 
//			// (which enables the for-loop below)
//			if (rawEnc.startsWith(b+ss)) {
//				rawEnc = sbi + rawEnc;
//			}
//			// Handle any remaining barlines
//			// If the barline is not followed by a comment: replace all
//			// (for loop because the barline can be regular or decorative)
//			for (String s : Arrays.asList(new String[]{ss+b+ss, sbi+b+ss})) {
//				if (rawEnc.contains(s)) {
//					rawEnc = rawEnc.replace(s, s+sp+ss);
//				}
//			}
//			// If the barline is followed by a comment: replace one by one
//			// (for loop because the barline can be regular or decorative)
//			for (String s : Arrays.asList(new String[]{ss+b+oib, sbi+b+oib})) {
//				if (rawEnc.contains(s)) {
//					// Traverse rawEnc and add (sp + ss) after each occurence of 
//					// b + comment + ss
//					int barlineInd = rawEnc.indexOf(s, startInd);
//					while (barlineInd >= 0) {
//						int oibInd = rawEnc.indexOf(oib, barlineInd);
//						int cibInd = rawEnc.indexOf(cib, oibInd);
//						int ssInd = rawEnc.indexOf(ss, cibInd);
//						breakInd = ssInd + 1;
//						firstHalf = rawEnc.substring(0, breakInd);
//						secondHalf = rawEnc.substring(breakInd);
//						rawEnc = firstHalf + sp + ss + secondHalf;
//						barlineInd = rawEnc.indexOf(s, barlineInd + 1);
//					}
//				}
//			}
//			// Remove any auxiliary SBI at the beginning of rawEnc
//			if (rawEnc.startsWith(sbi)) {
//				rawEnc = rawEnc.substring(rawEnc.indexOf(sbi) +1);
//			}
////			}
//		}
//		System.out.println(rawEnc);
//
//		// List events per system
//		List<List<String[]>> eventsPerSystem = new ArrayList<>();
//		int commentCounter = 0;
//		int bar = 1;
//		String[] systems = rawEnc.split(sbi);
//		for (int i = 0; i < systems.length; i++) {
//			List<String[]> eventsCurrSystem = new ArrayList<>();
//			String[] events = systems[i].split(sp + ss);
//			for (int j = 0; j < events.length; j++) {
//				String event = events[j];
//				boolean containsComment = event.contains(oib + FOOTNOTE_INDICATOR);
//
//				String[] e = new String[]{null, null, null, null};
//				e[BAR_IND] = String.valueOf(bar);
//				// If the event does not contain a comment: add
//				if (!containsComment) {
//					e[EVENT_IND] = event;
//				}
//				// If the event contains a comment: separate event and comment, and add
//				if (containsComment) {					
//					e[EVENT_IND] = event.substring(0, event.indexOf(oib)) + 
//						event.substring(event.indexOf(cib) + 1);
//					// Get unadapted comment (the one in event may have been altered if 
//					// it contains symbols split on, such as a space or a SBI)
//					e[FOOTNOTE_IND] = allEditorialComments.get(commentCounter);
//					e[FOOTNOTE_NUM_IND] = "#" + (commentCounter + 1);
//					commentCounter++;
//				}
//				eventsCurrSystem.add(e);
//
//				// Increment bar (if it is not a decorative opening barline)
//				// NB: This works both for systems ending with a complete bar
//				//                       [5]       [6]
//				// ... | ... | ... | ... | ... | / ... | ... | etc.
//				// 
//				// and for systems ending with an incomplete bar
//				//                       [5]         [6]          
//				// ... | ... | ... | ... | ... / ... | ... | etc.
//				if (j > 0) {
//					// NB: This is possible because the barlines are sorted by length
//					for (String b : barlinesAsString) {
//						if (event.startsWith(b)) {
//							bar++;
//							break;
//						}
//					}
//				}
//			}
//			eventsPerSystem.add(eventsCurrSystem);
//		}
//		return eventsPerSystem;
//	}


//	/**
//	 * Gets all the events. 
//	 * 
//	 * @return A <code>List<String></code> of all events in the Encoding; the SBI are
//	 * kept in place (i.e, form separate events). A rhythm symbol (the last active one)
//	 * is assigned to each event that is lacking one.
//	 */
//	private List<String> getEventEncodingsOLD() {
//		List<String> allEvents = new ArrayList<>();
//
//		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
//		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
//
//		// List all events and barlines for each system
//		List<List<String[]>> events = null; //getEvents();
//		for (int i = 0; i < events.size(); i++) {
//			List<String[]> system = events.get(i);
//			for (int j = 0; j < system.size(); j++) {
//				String e = system.get(j)[EVENT_IND];
//				boolean isCMS = ConstantMusicalSymbol.getConstantMusicalSymbol(
//					e.substring(0, e.lastIndexOf(ss))) != null;
//				// Add a space after each event that is not a CMS
//				if (!isCMS) {
//					e += ConstantMusicalSymbol.SPACE.getEncoding() + ss;
//				}
//				// Special case for barline followed by barline (this happens when a 
//				// full-bar note is tied at its left (see end quis_me_statim): these 
//				// two bars must be seen as a single event, so the first barline must 
//				// be added to the event added last
//				if (j < system.size() - 1) {
//					String nextE = system.get(j+1)[EVENT_IND];
//					boolean nextIsCMS = ConstantMusicalSymbol.getConstantMusicalSymbol(
//						nextE.substring(0, nextE.lastIndexOf(ss))) != null;
//					if (isCMS && nextIsCMS) {
//						// Add first barline to last event
//						int lastInd = allEvents.size()-1;
//						allEvents.set(lastInd, allEvents.get(lastInd) + e);
//						// Set e to second barline and skip event at j+1
//						e = nextE;
//						j++;
//					}
//				}
//				allEvents.add(e);
//			}
//			if (i < events.size() - 1) {
//				allEvents.add("/");
//			}
//		}
//
//		// Add a RS to each event lacking one
//		String activeRs = "";
//		for (int j = 0; j < allEvents.size(); j++) {
//			String t = allEvents.get(j);
//			if (!t.equals(sbi)) {
//				String first = t.substring(0, t.indexOf(ss));
//				// RS: set activeRs
//				if (RhythmSymbol.getRhythmSymbol(first) != null) {
//					activeRs = first;
//				}
//				// No RS: prepend activeRs to event if applicable  
//				else {
//					// Only if event is not a MS or a CMS (barline)
//					if (MensurationSign.getMensurationSign(first) == null && 
//						ConstantMusicalSymbol.getConstantMusicalSymbol(first) == null) {
//						allEvents.set(j, activeRs + ss + t);
//					}
//				}
//			}
//		}
//		return allEvents;
//	}


//	/**
//	 * Gets, per system, the segment indices in the tbp Staff of the events of the given
//	 * type.
//	 * 
//	 * @param type The type of event: "footnote" or "barline".
//	 *  
//	 * @return A <code>List</code> of <code>List</code>s, each of which represents a system, 
//	 * and contains the segment indices in the tbp Staff of the events of the given 
//	 * type. NB:
//	 * <ul>
//	 * <li>In case of a system without any events of the given type, the <code>List</code>
//	 *     remains empty.</li>
//	 * <li>In case of a barline that spans multiple segments, the index of the first 
//	 *     segment is given.</li>
//	 * </ul>
//	 */
//	private List<List<Integer>> getStaffSegmentIndicesOLD(String type) {
//		List<List<Integer>> segmentIndices = new ArrayList<>();
//
//		// Reorganise events per system
//		List<List<String[]>> events = null; // getEvents();
//		List<List<String[]>> eventsPerSystem = new ArrayList<>();
//		List<String[]> eventsCurrSystem = new ArrayList<>();
//		int currSystem = 1;
//		for (int i = 0; i < events.size(); i++) {
//			eventsCurrSystem.addAll(events.get(i));
//			if ((i < events.size() - 1 && Integer.parseInt(events.get(i+1).get(0)[SYSTEM_IND])
//				> currSystem) || i == events.size() - 1) { 
//				eventsPerSystem.add(eventsCurrSystem);
//				eventsCurrSystem = new ArrayList<>();
//				currSystem++;
//			}
//		}
//		
//		// For each system
//		for (List<String[]> system : eventsPerSystem) {
//			int currSegmentInd = 0;
//			List<Integer> currSegmentIndices = new ArrayList<>();
//			// For each event in the system
//			for (String[] event : system) {
//				String currEvent = event[EVENT_IND].substring(0, 
//					event[EVENT_IND].lastIndexOf(SymbolDictionary.SYMBOL_SEPARATOR));
//				// In case of a barline, the bar number/footnote indicator is added 
//				// above/below the first pipe char, so currSegmentInd must be 
//				// incremented with the index in currEvent of that pipe char
//				boolean isBarlineEvent = 
//					ConstantMusicalSymbol.isBarline(currEvent) ? true : false;
//				int charsAfterFirstPipe = 0;
//				if (isBarlineEvent) {
//					int indFirstPipe = 
//						currEvent.indexOf(ConstantMusicalSymbol.BARLINE.getEncoding()); 
//					currSegmentInd += indFirstPipe;
//					// In case of a multiple-char barline: determine how many chars
//					// follow the first pipe char
//					if (currEvent.length() > 1) {
//						charsAfterFirstPipe = (currEvent.length()-1) - indFirstPipe;
//					}
//				}
//				
//				// Exclude follow-up footnotes (consisting only of a FOOTNOTE_INDICATOR), 
//				// missing barlines footnotes, and misplaced barlines footnotes
//				boolean isFootnoteEvent = 
//					event[FOOTNOTE_IND] != null && 
//					!(event[FOOTNOTE_IND].equals(FOOTNOTE_INDICATOR) ||
//					event[FOOTNOTE_IND].startsWith(FOOTNOTE_INDICATOR + NO_BARLINE_TEXT) ||
//					event[FOOTNOTE_IND].startsWith(FOOTNOTE_INDICATOR + MISPLACED_BARLINE_TEXT));
//
//				// Add to list
//				if ( (type.equals("footnote") && isFootnoteEvent) ||
//					 (type.equals("barline") && isBarlineEvent)	) {
//					currSegmentIndices.add(currSegmentInd);
//				}
//				// Increment currSegmentInd to go to the next segment. If barline event:
//				// increment with the number of chars after the first pipe char in the 
//				// barline + 1. If not (i.e., if TS, RS, rest, or MS event): increment 
//				// with 2: 1 for the event itself and 1 for the space following it
//				currSegmentInd = 
//					isBarlineEvent ? currSegmentInd + (charsAfterFirstPipe + 1) :			
//					currSegmentInd + 2;
//			}
//			segmentIndices.add(currSegmentIndices);
//		}
//		return segmentIndices;
//	}


//	/**
//	 * Gets the bar numbers for each system. If a system ends with an incomplete 
//	 * bar, the next systems begins with that same bar.
//	 *  
//	 * @return A <code>List</code>, each element of which represents a system as a 
//	 * <code>List</code> of <code>Integer</code>s.
//	 */
//	private List<List<Integer>> getSystemBarNumbersOLD() {
//		List<List<Integer>> sbn = new ArrayList<>();
//		
//		// Reorganise events per system
//		List<List<String[]>> events = null; //getEvents();
//		List<List<String[]>> eventsPerSystem = new ArrayList<>();
//		List<String[]> eventsCurrSystem = new ArrayList<>();
//		int currSystem = 1;
//		for (int i = 0; i < events.size(); i++) {
//			eventsCurrSystem.addAll(events.get(i));
//			if ((i < events.size() - 1 && Integer.parseInt(events.get(i+1).get(0)[SYSTEM_IND])
//				> currSystem) || i == events.size() - 1) { 
//				eventsPerSystem.add(eventsCurrSystem);
//				eventsCurrSystem = new ArrayList<>();
//				currSystem++;
//			}
//		}
//		
//		for (List<String[]> system : eventsPerSystem) {
//			List<Integer> barsCurrSystem = new ArrayList<>();
//			for (String[] event : system) {
//				int bar = Integer.parseInt(event[BAR_IND]);
//				if (!barsCurrSystem.contains(bar)) {
//					barsCurrSystem.add(bar);
//				}
//			}
//			sbn.add(barsCurrSystem);
//		}
//		return sbn;
//	}


	/**
	 * Asserts whether or not the given event is a TS event (i.e., contains TSS).
	 * 
	 * @param event
	 * @param tss
	 * @return
	 */
	private static boolean isTabSymbolEvent(String event, TabSymbolSet tss) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		if (!event.endsWith(ss)) {
			event += ss;
		}
		String[] split = event.split("\\" + ss);
		for (String s : event.split("\\" + ss)) {
			if (Symbol.getTabSymbol(s, tss) != null) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Asserts whether or not the given event is a RS event (i.e., starts with a RS). A rest
	 * event is also an RS event.
	 * 
	 * @param event
	 * @return
	 */
	private static boolean isRhythmSymbolEvent(String event) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		if (!event.endsWith(ss)) {
			event += ss;
		}
		return Symbol.getRhythmSymbol(event.substring(0, event.indexOf(ss))) != null;
	}


	/**
	 * Asserts whether or not the given event is a rest event (i.e., consists only of a RS).
	 * 
	 * @param event
	 * @return
	 */
	private static boolean isRestEvent(String event) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		if (!event.endsWith(ss)) {
			event += ss;
		}
		return false; //isRhythmSymbolEvent(event) && (event.indexOf(ss) == event.lastIndexOf(ss)); 
	}


	private static boolean isMensurationSignEvent(String event) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		if (!event.endsWith(ss)) {
			event += ss;
		}
		if (event.equals(Symbol.SYSTEM_BREAK_INDICATOR)) {
			return false;
		}
		else {
			return Symbol.getMensurationSign(event.substring(0, event.indexOf(ss))) != null;
		}
	}


	/**
	 * Asserts whether or not the given event is a barline event or a SBI.
	 * 
	 * @param event
	 * @return
	 */
	private static boolean isBarlineEvent(String event) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		if (!event.endsWith(ss)) {
			event += ss;
		}
		if (event.equals(Symbol.SYSTEM_BREAK_INDICATOR)) {
			return false;
		}
		else {
			if (Symbol.CONSTANT_MUSICAL_SYMBOLS.keySet().contains(event.substring(0, event.indexOf(ss)))) {
//			if (Symbol.CONSTANT_MUSICAL_SYMBOLS.contains(
//				Symbol.getConstantMusicalSymbol(event.substring(0, event.indexOf(ss))))) {
				return true;
			}
			else {
				return false;
			}
		}
	}


	/**
	 * Splits the header and the encoding. Removes all comments and the EBI from the
	 * encoding.
	 * 
	 * @return A <code>String[]</code>, containing
	 * <ul>
	 * <li>as element 0: the header</li>
	 * <li>as element 1: the encoding</li>
	 * </ul>
	 */
	private String[] splitHeaderAndEncodingOLD() {
		String omb = OPEN_METADATA_BRACKET;
		String cmb = CLOSE_METADATA_BRACKET;
		// Separate header and encoding
		String raw = getRawEncoding();
		int endHeader = raw.indexOf(cmb, raw.indexOf(METADATA_TAGS[METADATA_TAGS.length-1]));		
		String header = raw.substring(0, endHeader+1).trim();
		String enc = raw.substring(endHeader+1, raw.length()).replace("\r\n", "").trim();
		// Remove comments and EBI from encoding
		while (enc.contains(omb)) {
			int openCommentIndex = enc.indexOf(omb);
			int closeCommentIndex = enc.indexOf(cmb, openCommentIndex);
			String comment = enc.substring(openCommentIndex, closeCommentIndex + 1);
			enc = enc.replace(comment, "");
		}
		enc = enc.substring(0, enc.indexOf(Symbol.END_BREAK_INDICATOR));

		return new String[]{header, enc};
	}


	private void reverse(List<Integer[]> mi) {
		this.init(
			reverseHeader(mi) + "\r\n\r\n" + reverseCleanEncoding(), 
			getPiecename(), 
			Stage.RULES_CHECKED
		);
	}


	/**
	 * Reverses the encoding.
	 * 
	 * @param  meterInfo
	 * @return
	 */
	private void reverseOLD(List<Integer[]> meterInfo) {
		String header = getHeader();

		// 1. Adapt header (reverse meterInfo information)
		int startInd = 
			header.indexOf("METER_INFO") + "METER_INFO".length() + ": ".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(CLOSE_METADATA_BRACKET, startInd));
		List<Integer[]> meterInfoRev = new ArrayList<>();
		for (Integer[] in : meterInfo) {
			meterInfoRev.add(Arrays.copyOf(in, in.length));
		}
		Integer[] last = meterInfoRev.get(meterInfoRev.size() - 1);
		int numBars = last[Tablature.MI_LAST_BAR];
		for (Integer[] in : meterInfoRev) {
			in[Tablature.MI_FIRST_BAR] = (numBars - in[Tablature.MI_FIRST_BAR]) + 1;
			in[Tablature.MI_LAST_BAR] = (numBars - in[Tablature.MI_LAST_BAR]) + 1;
		}
		Collections.reverse(meterInfoRev);
		String reversedMeterInfo = "";
		for (int i = 0; i < meterInfoRev.size(); i++) {
			Integer[] in = meterInfoRev.get(i);
			reversedMeterInfo += in[Tablature.MI_NUM] + "/" + in[Tablature.MI_DEN] + " (";
			if (in[Tablature.MI_FIRST_BAR] == in[Tablature.MI_LAST_BAR]) {
				reversedMeterInfo += in[Tablature.MI_LAST_BAR];
			}
			if (in[Tablature.MI_FIRST_BAR] != in[Tablature.MI_LAST_BAR]) {
				reversedMeterInfo += in[Tablature.MI_LAST_BAR] + "-" + in[Tablature.MI_FIRST_BAR];
			}
			reversedMeterInfo += ")";
			if (i < meterInfoRev.size() - 1) {
				reversedMeterInfo += "; ";
			}
		}
		header = header.replace(origMeterInfo, reversedMeterInfo);
		
		// 2. Reverse encoding and recombine
		List<String> events = decompose(true, true);
		events = events.subList(0, events.size() - 1);
		Collections.reverse(events);
		
		this.init(
			header + "\r\n\r\n" + recompose(events) + Symbol.END_BREAK_INDICATOR, 
			getPiecename(), 
			Stage.RULES_CHECKED
		);
//		return new Encoding(
//			header + "\r\n\r\n" + recompose(events) + Symbol.END_BREAK_INDICATOR, 
//			getPiecename(), 
//			SYNTAX_CHECKED
//		);
	}


	private void deornament(int dur) {
		this.init(
			getHeader() + "\r\n\r\n" + deornamentCleanEncoding(dur), 
			getPiecename(), 
			Stage.RULES_CHECKED
		);
	}


	/**
	 * Removes all sequences of single-note events shorter than the given duration from the
	 * <code>Encoding</code>, and lengthens the duration of the event preceding the sequence 
	 * by the total length of the removed sequence.
	 * 
	 * @param dur The given duration (as a <code>RhythmSymbol</code> duration). 
	 * @return
	 */
	private Encoding deornamentOLD(int dur) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		// 1. Adapt events
		List<String> events = decompose(true, true);
		String pre = null;
		int durPre = -1;
		int indPre = -1;
		List<Integer> removed = new ArrayList<>();
		int i2 = 0;
		for (int i = 0; i < events.size(); i++) {
			String e = events.get(i);
			// If e is not a barline, a SBI, or an EBI
			if (!e.equals(sbi) && !e.equals(ebi) && !assertEventType(e, null, "barline")) {
				String[] symbols = e.split("\\" + ss);
				RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
				// If the event is an ornamentation (which always consists of only a RS, 
				// a TS, and a space)
				if (r != null && r.getDuration() < dur && symbols.length == 3) {
					removed.add(i2);
					// Determine pre, if it has not yet been determined
					if (pre == null) {
						for (int j = i - 1; j >= 0; j--) {
							String tPrev = events.get(j);
							// If tPrev is not a barline or SBI
							if (!tPrev.equals(sbi) && !assertEventType(tPrev, null, "barline")) {
								pre = tPrev;
								durPre = 
									Symbol.getRhythmSymbol(tPrev.substring(0, 
									tPrev.indexOf(ss))).getDuration();
								indPre = j;
								break;
							}
						}
					}
					// Increment durPre and set event to null
					durPre += r.getDuration();
					events.set(i, null);
				}
				// If the event is the first after a sequence of one or more ornamental
				// notes (i.e., it does not meet the if conditions above but pre != null)
				else if (pre != null) {
					// Determine the new Rs for pre, and adapt and set it
					String newRs = "";
					for (RhythmSymbol rs : Symbol.RHYTHM_SYMBOLS.values()) {
						if (rs.getDuration() == durPre) {
							newRs = rs.getEncoding();
							break;
						}
					}
					events.set(indPre, newRs + pre.substring(pre.indexOf(ss), pre.length()));
					// Reset
					pre = null;
					indPre = -1;
				}
				// Do not consider rests
				if (symbols.length != 2) { 
					i2++;
				}
			}
		}
		events.removeIf(t -> t == null);

		// 2. Recombine
		return new Encoding(
			getHeader() + "\r\n\r\n" + recompose(events), 
			getPiecename(), 
			Stage.RULES_CHECKED
		);
	}


	/**
	 * Stretches the encoding durationally by the given factor.  
	 * 
	 * @param meterInfo
	 * @param factor
	 * @return
	 */
	private Encoding stretchOLD(List<Integer[]> meterInfo, double factor) {
		String header = getHeader();

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		// 1. Adapt header
		// Stretch meterInfo information 
		int startInd = 
			header.indexOf("METER_INFO") + "METER_INFO".length() + ": ".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(CLOSE_METADATA_BRACKET, startInd));
		List<Integer[]> copyOfMeterInfo = new ArrayList<>();
		String stretchedMeterInfo = "";
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] in = meterInfo.get(i);
			if (i > 0) {
				in[Tablature.MI_FIRST_BAR] = meterInfo.get(i - 1)[Tablature.MI_LAST_BAR] + 1;
			}
			in[Tablature.MI_LAST_BAR] = (int) (in[Tablature.MI_LAST_BAR] * factor);
			stretchedMeterInfo += 
				in[Tablature.MI_NUM] + "/" + in[Tablature.MI_DEN] + 
				" (" + in[Tablature.MI_FIRST_BAR] + "-" + in[Tablature.MI_LAST_BAR] + ")";
			if (i < copyOfMeterInfo.size() - 1) {
				stretchedMeterInfo += "; ";
			}
		}
		header = header.replace(origMeterInfo, stretchedMeterInfo);

		// 2. Adapt events
		List<String> events = decompose(true, true);
		for (int i = 0; i < events.size(); i++) {
			String e = events.get(i);
			// If e is not a barline, a SBI, or an EBI
			if (!e.equals(sbi) && !e.equals(ebi) && !assertEventType(e, null, "barline")) {
				String[] symbols = e.split("\\" + ss);
				RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
				String newRs = "";
				if (r != null) {
					for (RhythmSymbol rs : Symbol.RHYTHM_SYMBOLS.values()) {
						if (rs.getDuration() == r.getDuration() * factor) {
							newRs = rs.getEncoding();
							break;
						}
					}
					events.set(i, newRs + e.substring(e.indexOf(ss), e.length()));
				}
			}
		}

		// 3. Recombine
		return new Encoding(
			header + "\r\n\r\n" + recompose(events), 
			getPiecename(), 
			Stage.RULES_CHECKED);
	}


	/**
	 * Deornaments the (clean) encoding. If not all events have an RS, the missing RS are 
	 * complemented before deornamenting.
	 * 
	 * @param thresholdDur
	 * 
	 * @return
	 */
	private String deornamentCleanEncodingOLD(int thresholdDur) {
		String ss = Symbol.SYMBOL_SEPARATOR;
		String sbi = Symbol.SYSTEM_BREAK_INDICATOR;
		String ebi = Symbol.END_BREAK_INDICATOR;

		List<String> events = decompose(true, true);
		String pre = null;
		int durPre = -1;
		int indPre = -1;
		for (int i = 0; i < events.size(); i++) {
			String e = events.get(i);
			// If e is not a barline, a SBI, or an EBI
			if (!e.equals(sbi) && !e.equals(ebi) && !assertEventType(e, null, "barline")) {
				String[] symbols = e.split("\\" + ss);
				RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
				// If the event is an ornamentation (which always consists of only a RS, 
				// a TS, and a space)
				if (r != null && r.getDuration() < thresholdDur && symbols.length == 3) {
					// Determine pre, if it has not yet been determined
					if (pre == null) {
						for (int j = i - 1; j >= 0; j--) {
							String tPrev = events.get(j);
							// If tPrev is not a barline or SBI
							if (!tPrev.equals(sbi) && !assertEventType(tPrev, null, "barline")) {
								pre = tPrev;
								durPre = 
									Symbol.getRhythmSymbol(tPrev.substring(0, 
									tPrev.indexOf(ss))).getDuration();
								indPre = j;
								break;
							}
						}
					}
					// Increment durPre and set event to null
					durPre += r.getDuration();
					events.set(i, null);
				}
				// If the event is the first after a sequence of one or more ornamental
				// notes (i.e., it does not meet the if conditions above but pre != null)
				else if (pre != null) {
					// Determine the new RS for pre, and adapt and set it
					String newRs = "";
					for (RhythmSymbol rs : Symbol.RHYTHM_SYMBOLS.values()) {
						if (rs.getDuration() == durPre) {
							newRs = rs.getEncoding();
							break;
						}
					}
					events.set(indPre, newRs + pre.substring(pre.indexOf(ss), pre.length()));
					// Reset
					pre = null;
					indPre = -1;
				}
			}
		}
		events.removeIf(t -> t == null);
		return recompose(events); 
	}


	/**
	 * Reverses the header (i.e., adapts the METER_INFO and DIMINUTION information).
	 * 
	 * @param mi
	 * @return
	 */
	private String reverseHeader(List<Integer[]> mi) {
		String argHeader = getHeader();

		// Get original METER_INFO and DIMINUTION content
		// NB: makeHeader() guarantees that (i) there is a space after the colon following
		//     the METER_INFO_TAG and DIMINUTION_TAG, and (ii) the content following that
		//     space, which is succeeded by the CLOSE_METADATA_BRACKET, is trimmed
		int startInd =
			argHeader.indexOf("METER_INFO") + "METER_INFO".length() + ": ".length(); 
		String miOrigContent = 
			argHeader.substring(startInd, argHeader.indexOf(CLOSE_METADATA_BRACKET, startInd));
		startInd = 
			argHeader.indexOf("DIMINUTION") + "DIMINUTION".length() + ": ".length(); 
		String dimOrigContent = 
			argHeader.substring(startInd, argHeader.indexOf(CLOSE_METADATA_BRACKET, startInd));

		// Reverse mi
		List<Integer[]> miRev = new ArrayList<>();
		for (Integer[] in : mi) {
			miRev.add(Arrays.copyOf(in, in.length));
		}
		int numBars = miRev.get(miRev.size() - 1)[Tablature.MI_LAST_BAR];
		for (Integer[] in : miRev) {
			in[Tablature.MI_FIRST_BAR] = (numBars - in[Tablature.MI_FIRST_BAR]) + 1;
			in[Tablature.MI_LAST_BAR] = (numBars - in[Tablature.MI_LAST_BAR]) + 1;
		}
		Collections.reverse(miRev);

		// Make reversed METER_INFO and DIMINUTION content
		String miRevContent = "";
		String dimRevContent = "";		
		for (int i = 0; i < miRev.size(); i++) {
			Integer[] in = miRev.get(i);
			int firstBar = in[Tablature.MI_FIRST_BAR];
			int lastBar = in[Tablature.MI_LAST_BAR];
			miRevContent += 
				in[Tablature.MI_NUM] + "/" + in[Tablature.MI_DEN] + " (" + 
				(firstBar != lastBar ? lastBar + "-" + firstBar : lastBar) + ")" +
				(i < miRev.size() - 1 ? "; " : "");
			dimRevContent += in[Tablature.MI_DIM] + (i < miRev.size() - 1 ? "; " : "");
		}
		argHeader = argHeader.replace(miOrigContent, miRevContent);
		argHeader = argHeader.replace(dimOrigContent, dimRevContent);
		return argHeader;
	}


	/**
	 * Rescales the header (i.e., adapts the METER_INFO information).
	 * 
	 * @param mi
	 * @param rescaleFactor 
	 * @return
	 */
	private String rescaleHeader(List<Integer[]> mi, int rescaleFactor) {
		String argHeader = getHeader();

		// Get original METER_INFO content
		// NB: makeHeader() guarantees that (i) there is a space after the colon following
		//     the METER_INFO_TAG, and (ii) the content following that space, which is 
		//     succeeded by the CLOSE_METADATA_BRACKET, is trimmed
		int startInd = 
			argHeader.indexOf("METER_INFO") + "METER_INFO".length() + ": ".length();
		String miOrigContent = 
			argHeader.substring(startInd, argHeader.indexOf(CLOSE_METADATA_BRACKET, startInd));

		// Make rescaled METER_INFO content
		String miRescContent = "";
		for (int i = 0; i < mi.size(); i++) {
			Integer[] in = mi.get(i);
			int firstBar = in[Tablature.MI_FIRST_BAR];
			int lastBar = in[Tablature.MI_LAST_BAR];
			int den = in[Tablature.MI_DEN];
			miRescContent += 
				in[Tablature.MI_NUM] + "/" + 
				(rescaleFactor > 0 ? den/rescaleFactor : den*Math.abs(rescaleFactor)) + " (" +
				(firstBar != lastBar ? firstBar + "-" + lastBar : firstBar) + ")" +
				(i < mi.size() - 1 ? "; " : "");
		}
		return argHeader = argHeader.replace(miOrigContent, miRescContent);
	}


	/**
	 * Reverses the (clean) encoding. If not all events have an RS, the missing RS are 
	 * complemented before reversing. 
	 * 
	 * @return
	 */
	private String reverseCleanEncoding() {
		List<String> events = decompose(true, true);
		Collections.reverse(events);
//		events.forEach(e -> System.out.println(e));
		return recompose(events.subList(1, events.size())) + events.get(0);
	}


	/**
	 * Deornaments the (clean) encoding. If not all events have an RS, the missing RS are 
	 * complemented before deornamenting.
	 * 
	 * @param thresholdDur
	 * 
	 * @return
	 */
	private String deornamentCleanEncoding(int thresholdDur) {
		List<String> events = decompose(true, true);
		List<String> eventsDeorn = new ArrayList<>();
		for (int i = 0; i < events.size(); i++) {
			String e = events.get(i);
			// If e is a RS event
			if (assertEventType(e, null, "RhythmSymbol")) {
				String[] symbols = e.split("\\" + Symbol.SYMBOL_SEPARATOR);
				RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
				// If e is ornamental (in which case it consists of only a RS, a TS, and a space) and 
				// not part of an ornamental sequence at the beginning of the Encoding (in which case 
				// eventsDeorn is still empty)
				// NB: It is assumed that an ornamental sequence is not interrupted by (ornamental) rests 
				if (r.getDuration() < thresholdDur && symbols.length == 3 && eventsDeorn.size() > 0) {
					int durOrnSeq = r.getDuration();
					for (int j = i+1; j < events.size(); j++) {
						String eNext = events.get(j);
						// If eNext is a RS event
						if (assertEventType(eNext, null, "RhythmSymbol")) {
							String[] symbolsNext = eNext.split("\\" + Symbol.SYMBOL_SEPARATOR);
							RhythmSymbol rNext = Symbol.getRhythmSymbol(symbolsNext[0]);
							// If eNext is ornamental: increment duration of ornamental sequence
							if (rNext.getDuration() < thresholdDur && symbolsNext.length == 3) {
								durOrnSeq += rNext.getDuration();
							}
							// If not: make ePrevDeorn, which replaces ePrev
							else {
								String ePrev = events.get(i-1);
								String[] symbolsPrev = ePrev.split("\\" + Symbol.SYMBOL_SEPARATOR);
								RhythmSymbol rPrev = Symbol.getRhythmSymbol(symbolsPrev[0]);
								String ePrevDeorn = Symbol.getRhythmSymbol(
									rPrev.getDuration() + durOrnSeq,
									rPrev.getEncoding().startsWith(Symbol.CORONA_INDICATOR),
									rPrev.getBeam(),
									rPrev.isTriplet()
								).getEncoding();
								ePrevDeorn += ePrev.substring(ePrev.indexOf(Symbol.SYMBOL_SEPARATOR));
								eventsDeorn.set(eventsDeorn.lastIndexOf(ePrev), ePrevDeorn);
								i = j-1;
								break;
							}
						}
						// If eNext is not a RS event
						else {
							eventsDeorn.add(eNext);
						}
					}
				}
				// If e is not ornamental
				else {
					eventsDeorn.add(e);
				}
			}
			// If e is not a RS event
			else {
				eventsDeorn.add(e);
			}
		}
		return recompose(eventsDeorn);
	}


	/**
	 * Rescales the (clean) encoding. If not all events have an RS, the missing RS are 
	 * complemented before rescaling.
	 * 
	 * @param rescaleFactor  
	 * @return
	 */
	private String rescaleCleanEncoding(int rescaleFactor) {
		List<String> events = decompose(true, true);
		for (int i = 0; i < events.size(); i++) {
			String e = events.get(i);
			// If e is a RS event
			if (assertEventType(e, null, "RhythmSymbol")) {
				String[] symbols = e.split("\\" + Symbol.SYMBOL_SEPARATOR);
				RhythmSymbol r = Symbol.getRhythmSymbol(symbols[0]);
				String eResc = Symbol.getRhythmSymbol(
					r.getDuration() * rescaleFactor, 
					r.getEncoding().startsWith(Symbol.CORONA_INDICATOR), 
					r.getBeam(), 
					r.isTriplet()
				).getEncoding();
				eResc += e.substring(e.indexOf(Symbol.SYMBOL_SEPARATOR), e.length());
				events.set(i, eResc);
			}
		}
		return recompose(events); 
	}


	/**
	 * Augments the given header according to the given augmentation. 
	 * 
	 * @param argHeader
	 * @param mi
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	private static String augmentHeaderOLD(String argHeader, List<Integer[]> mi, int rescaleFactor, String augmentation) {
		// Get original METER_INFO and DIMINUTION content
		// NB: makeHeader() guarantees that (i) there is a space after the colon following
		//     the METER_INFO_TAG and DIMINUTION_TAG, and (ii) the content following that
		//     space, which is succeeded by the CLOSE_METADATA_BRACKET, is trimmed
		int startInd =
			argHeader.indexOf("METER_INFO") + "METER_INFO".length() + ": ".length(); 
		String miOrigContent = 
			argHeader.substring(startInd, argHeader.indexOf(CLOSE_METADATA_BRACKET, startInd));
		startInd = 
			argHeader.indexOf("DIMINUTION") + "DIMINUTION".length() + ": ".length(); 
		String dimOrigContent = 
			argHeader.substring(startInd, argHeader.indexOf(CLOSE_METADATA_BRACKET, startInd));

		// Reverse mi
		List<Integer[]> miRev = new ArrayList<>();
		if (augmentation.equals("reverse")) {
			mi.forEach(in -> miRev.add(Arrays.copyOf(in, in.length)));
			int numBars = miRev.get(miRev.size() - 1)[Tablature.MI_LAST_BAR];
			for (Integer[] in : miRev) {
				in[Tablature.MI_FIRST_BAR] = (numBars - in[Tablature.MI_FIRST_BAR]) + 1;
				in[Tablature.MI_LAST_BAR] = (numBars - in[Tablature.MI_LAST_BAR]) + 1;
			}
			Collections.reverse(miRev);
		}

		// Make augmented METER_INFO and DIMINUTION content
		String miAugmContent = "";
		String dimAugmContent = "";
		for (int i = 0; i < mi.size(); i++) {
			Integer[] in = augmentation.equals("reverse") ? miRev.get(i) : mi.get(i);
			int firstBar = in[Tablature.MI_FIRST_BAR];
			int lastBar = in[Tablature.MI_LAST_BAR];
			int den = in[Tablature.MI_DEN];
			if (augmentation.equals("reverse")) {
				
				miAugmContent += 
					in[Tablature.MI_NUM] + "/" + in[Tablature.MI_DEN] + " (" + 
					(firstBar != lastBar ? lastBar + "-" + firstBar : lastBar) + ")" +
					(i < miRev.size() - 1 ? "; " : "");
				dimAugmContent += in[Tablature.MI_DIM] + (i < miRev.size() - 1 ? "; " : "");
			}
			else if (augmentation.equals("rescale")) {
				miAugmContent +=
					in[Tablature.MI_NUM] + "/" + 
					(rescaleFactor > 0 ? den/rescaleFactor : den*Math.abs(rescaleFactor)) + " (" +
					(firstBar != lastBar ? firstBar + "-" + lastBar : firstBar) + ")" +
					(i < mi.size() - 1 ? "; " : "");
			}
		}

		// Replace original with augmented content
		argHeader = argHeader.replace(miOrigContent, miAugmContent);
		if (augmentation.equals("reverse")) {
			argHeader = argHeader.replace(dimOrigContent, dimAugmContent);
		}
		return argHeader;
	}


	/**
	 * Parses the meter information from the metadata. 
	 * 
	 * @return A List containing, for each meter, an Integer[] containing
	 * <ul>
	 * <li>As element 0: the meter's numerator.</li>
	 * <li>As element 1: the meter's denominator.</li>
	 * <li>As element 2: the meter's first bar.</li>
	 * <li>As element 3: the meter's last bar.</li>
	 * <li>As element 4: the meter's diminution.</li>
	 * </ul>
	 */
	private List<Integer[]> getMetersBarsDiminutions() {
		List<Integer[]> mbd = new ArrayList<>();
		List<String> metersBars = new ArrayList<>();
		Arrays.stream(getMetadata().get(METADATA_TAGS[METER_INFO_IND]).split(";"))
			.forEach(m -> metersBars.add(m.trim()));
		List<String> diminutions = new ArrayList<>();
		Arrays.stream(getMetadata().get(METADATA_TAGS[DIMINUTION_IND]).split(";"))
			.forEach(m -> diminutions.add(m.trim()));
		for (int i = 0; i < metersBars.size(); i++) {
			String m = metersBars.get(i);
			String meter = m.substring(0, m.indexOf("(")).trim();
			String bars = m.substring(m.indexOf("(") + 1, m.indexOf(")")).trim();
			mbd.add(new Integer[]{
				Integer.valueOf(meter.split("/")[0]), 
				Integer.valueOf(meter.split("/")[1]), 
				bars.contains("-") ? Integer.valueOf(bars.split("-")[0]) : Integer.valueOf(bars),
				bars.contains("-") ? Integer.valueOf(bars.split("-")[1]) : Integer.valueOf(bars),
				Integer.valueOf(diminutions.get(i)) 		
			});
		}
		return mbd;
	}
}