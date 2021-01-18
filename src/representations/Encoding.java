package representations;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tbp.ConstantMusicalSymbol;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.Staff;
import tbp.SymbolDictionary;
import tbp.TabSymbol;
import tbp.TabSymbolSet;

public class Encoding implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String FOOTNOTE_INDICATOR = "@";
	private static final String WHITESPACE = " ";
	private String name; 
	private String rawEncoding;
	private String cleanEncoding;
//	private boolean hasMetadataErrors;
	private List<String> infoAndSettings;
//	private List<String> metaData;
//	private List<String> footnotes;
	private List<List<String[]>> eventsBarlinesFootnotes;
	private static final int AUTHOR_IND = 0;
	private static final int TITLE_IND = 1;
	private static final int SOURCE_IND = 2;
	public static final int TABSYMBOLSET_IND = 3;
	private static final int TUNING_IND = 4;
	private static final int TUNING_BASS_COURSES_IND = 5;
	public static final int METER_IND = 6;
	public static final int DIMINUTION_IND = 7;
	private static final int EVENT_IND = 0;
	private static final int BAR_IND = 1;
	private static final int FOOTNOTE_IND = 2;
	private static final int FOOTNOTE_NUM_IND = 3;
		
	private List<List<String>> listsOfSymbols;
	private static final int ALL_SYMBOLS_IND = 0;
	public static final int TAB_SYMBOLS_IND = 1;
	private static final int RHYTHM_SYMBOLS_IND = 2;
	private static final int MENSURATION_SIGNS_IND = 3;
	private static final int BARLINES_IND = 4;
	public static final int ALL_EVENTS_IND = 5;
	
	private List<List<Integer>> listsOfStatistics;
	public static final int IS_TAB_SYMBOL_EVENT_IND = 0;
	private static final int IS_RHYTHM_SYMBOL_EVENT_IND = 1;
	public static final int IS_REST_EVENT_IND = 2;
	private static final int IS_MENSURATION_SIGN_EVENT_IND = 3;
	private static final int IS_BARLINE_EVENT_IND = 4;
	public static final int SIZE_OF_EVENTS_IND = 5;
	private static final int DURATION_OF_EVENTS_IND = 6;  
	public static final int HORIZONTAL_POSITION_IND = 7;
	public static final int VERTICAL_POSITION_IND = 8;
	public static final int DURATION_IND = 9;
	public static final int GRID_X_IND = 10;
	public static final int GRID_Y_IND = 11;
	public static final int HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND = 12;
	public static final String METADATA_ERROR = "METADATA ERROR -- Check for missing curly brackets.";

	private Tuning[] tunings;
	public static final int ENCODED_TUNING_IND = 0;
	public static final int NEW_TUNING_IND = 1;
	public static enum Tuning  {
		
		C_AVALLEE("C_AVALLEE", -7, true, Arrays.asList(new String[]{"Bb", "F", "Bb", "D", "G", "C"})),
		C_HIGH("C_HIGH", 5, false, Arrays.asList(new String[]{"C", "F", "Bb", "D", "G", "C"})),
		D("D", -5, false, Arrays.asList(new String[]{"D", "G", "C", "E", "A", "D"})),
		F("F", -2, false, Arrays.asList(new String[]{"F", "Bb", "Eb", "G", "C", "F"})),
		F_ENH("F", -2, false, Arrays.asList(new String[]{"F", "A#", "D#", "G", "C", "F"})),
		F_SHARP("F_SHARP", -1, false, Arrays.asList(new String[]{"F#", "B", "E", "G#", "C#", "F#"})),
		G("G", 0, false, Arrays.asList(new String[]{"G", "C", "F", "A", "D", "G"})),
		G_AVALLEE("G_AVALLEE", 0, true, Arrays.asList(new String[]{"F", "C", "F", "A", "D", "G"})),
		A("A", 2, false, Arrays.asList(new String[]{"A", "D", "G", "B", "E", "A"})),
		A_AVALLEE("A_AVALLEE", 2, true, Arrays.asList(new String[]{"G", "D", "G", "B", "E", "A"})),
		;
		
		private String name;
		private int transposition;
		private boolean isAvallee;
		private List<String> courseString;
		Tuning(String s, int t, boolean a, List<String> l) {
			this.name = s;
			this.transposition = t;
			this.isAvallee = a;
			this.courseString = l;
		}
		
		public String getName() {
			return name;
		}

		public int getTransposition() {
			return transposition;
		}

		public boolean isAvallee() {
			return isAvallee;
		}

		public List<String> getCourseString() {
			return courseString;
		}
	}
//	public enum Tuning {F, G, G_AVALLEE, A, A_AVALLEE, C_AVALLEE};
	public enum TuningBassCourses {
		SECOND, FOURTH,
		P4M2, // 8-course with perfect 5th and major 2nd: G tuning (8) D, (7) F
		P5P4m3M2, // 10-course with perfect 5th, perfect 4th, minor 3rd, major 2nd: G tuning (10) C, (9) D, (8) E, (7) F  
		P5P4M3M2, // 10-course with perfect 5th, perfect 4th, major 3rd, major 2nd: G tuning (10) C, (9) D, (8) Eb, (7) F  	
	};
	
	private static String[] metaDataTags = new String[]{
		"AUTHOR:", 
		"TITLE:", 
		"SOURCE:", 
		"TABSYMBOLSET:", 
		"TUNING:", 
		"TUNING_SEVENTH_COURSE:", 
		"METER_INFO:",
		"DIMINUTION:"
	};


	public Encoding() {
	}


	/**
	 * Constructor for an unchecked encoding.
	 * 
	 * @param rawEncoding
	 * @param Whether or not the Encoding is checked.
	 */
	public Encoding(String rawEncoding, boolean isChecked) {
		// Unchecked encoding
		if (!isChecked) {
			setRawEncoding(rawEncoding);
//			setHasMetadataErrors(); // needs rawEncoding
//			if (getHasMetadataErrors() == true) {
//				return;
//			}
			if (checkForMetadataErrors() == true) {
				return;
			}
//			setFootnotes(); // needs rawEncoding
			setCleanEncoding(); // needs rawEncoding 
			setEventsBarlinesFootnotes(); // needs rawEncoding 
			setInfoAndSettings(); // needs rawEncoding
//			setExtendedEvents(); // needs rawEncoding
//			setMetaData(); // niet nodig hier// needs infoAndSettings
//			setFootnotes(); // niet nodig hier // needs rawEncoding
		}
		// Checked encoding (retrieved from an existing Encoding)
		else {
			createEncoding(rawEncoding);
		}
	}


	/**
	 * Constructor for a checked encoding.
	 * 
	 * @param argFile
	 */
	public Encoding(File argFile) { 
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(argFile.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		createEncoding(rawEncoding);
	}


	private void createEncoding(String rawEncoding) {
		setRawEncoding(rawEncoding);
//		setHasMetadataErrors(); // needs rawEncoding
//		if (getHasMetadataErrors() == true) {
//			throw new RuntimeException(METADATA_ERROR);
//		}
		if (checkForMetadataErrors() == true) {
			throw new RuntimeException(METADATA_ERROR);
		}
		
//		setFootnotes(); // needs rawEncoding
		setCleanEncoding(); // needs rawEncoding
		setEventsBarlinesFootnotes(); // needs rawEncoding 
		setInfoAndSettings(); // needs rawEncoding
//		setMetadata(); // needs infoAndSettings
//		setFootnotes(); // needs rawEncoding
		if (checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
			throw new RuntimeException("ERROR: The encoding contains encoding errors; run the TabViewer to correct them.");
		}
		setTunings();
		setListsOfSymbols();
		setListsOfStatistics();
	}


	// TESTED (together with getRawEncoding())
	void setRawEncoding(String aString) {
		this.rawEncoding = aString.trim();
	}


	// TESTED (together with setRawEncoding())
	public String getRawEncoding() {
		return rawEncoding;
	}


//	private void setHasMetadataErrors() {
//		hasMetadataErrors = (checkForMetadataErrors() == true) ? true : false;
//	}


//	public boolean getHasMetadataErrors() {
//		return hasMetadataErrors;
//	}


	/**
	 * Verifies the correct encoding of all metadata in rawEncoding, i.e., checks whether:
	 * <ul>
	 * <li><code>rawEncoding</code> contains all info and settings tags in the correct sequence</li> 
	 * <li>whether each tag is preceded by an <code>OPEN_INFO_BRACKET</code> and succeeded by a 
	 *     <code>CLOSE_INFO_BRACKET</code></li>
	 * <li>whether any additional information items after the info and settings items (pages,
	 *     footnotes) are preceded by an <code>OPEN_INFO_BRACKET</code> and succeeded by a 
	 *     <code>CLOSE_INFO_BRACKET</code></li>
	 * </ul>
	 * 
	 * @return <code>true</code> if the encoding has metadata errors, and <code>false</code> if
	 *  it is correct.  
	 */
	// TESTED
	public boolean checkForMetadataErrors() {		
		String oib = SymbolDictionary.OPEN_INFO_BRACKET;
		String cib = SymbolDictionary.CLOSE_INFO_BRACKET;
		
		// 1. Info and settings tags
		// Check whether rawEncoding contains all info and settings tags, in the correct order
		List<Integer> indicesOfTags = new ArrayList<Integer>();
		String rawEnc = getRawEncoding();
		for (String tag : getMetadataTags()) {
			if (!rawEnc.contains(tag)) {
				return true;
			}
			// The tags are encoded in the correct order if none of the indices in indicesOfTags 
			// are greater than the index of the tag last added
			int indexOfTag = rawEnc.indexOf(tag);
			indicesOfTags.add(indexOfTag);
			for (int i : indicesOfTags) {
				if (i > indexOfTag) {
					return true;
				}
			}
		}
		// Check whether each tag is preceded by an OPEN_INFO_BRACKET and succeeded by only one CLOSE_INFO_BRACKET
		int indexOfLastCloseInfoBracket = -1;
		for (int i = 0; i < indicesOfTags.size(); i++) {
			int startIndex = indicesOfTags.get(i);
			// Check whether the tag is preceded by an OPEN_INFO_BRACKET 
			if (!rawEnc.substring(startIndex - 1, startIndex).equals(oib)) {
				return true;
			}
			// Check whether the tag is succeeded by only one CLOSE_INFO_BRACKET
			// a. For any but the last tag
			if (i != indicesOfTags.size() - 1) {
				int endIndex = indicesOfTags.get(i + 1) - 1;
				List<String> inbetween = new ArrayList<String>();
				for (int j = startIndex; j < endIndex; j++) {
					inbetween.add(Character.toString(rawEnc.charAt(j)));
				}
				if (Collections.frequency(inbetween, cib) != 1 || 
					Collections.frequency(inbetween, oib) != 0) {
					return true;
				}
			}
			// b. For the last tag
			else {
				String firstFound = null;
				for (int j = startIndex; j < rawEnc.length(); j++) {
					if (Character.toString(rawEnc.charAt(j)).equals(oib)) {
						firstFound = oib;
						break;
					}
					else if (Character.toString(rawEnc.charAt(j)).equals(cib)) {
						firstFound = cib;
						indexOfLastCloseInfoBracket = j;
						break;
					}
				}
				if (firstFound == oib || firstFound == null) {
					return true;
				}
			}
		}

		// 2. Additional information items
		// List the remaining OPEN_ and CLOSE_INFO_BRACKETS
		List<String> remainingBrackets = new ArrayList<String>();
		for (int i = indexOfLastCloseInfoBracket + 1; i < rawEnc.length(); i++) {
			String currentChar = Character.toString(rawEnc.charAt(i));
			if (currentChar.equals(oib) || currentChar.equals(cib)) {
				remainingBrackets.add(currentChar);
			}
		}
		// Check whether each element on an even index (including 0) is an OPEN_INFO_BRACKET, whether each on an 
		// odd index is a CLOSE_INFO_BRACKET, and whether the last element is a CLOSE_INFO_BRACKET
		for (int i = 0; i < remainingBrackets.size(); i++) {
			String currentBracket = remainingBrackets.get(i);
			if (i % 2 == 0 && !currentBracket.equals(oib) || 
				i % 2 == 1 && !currentBracket.equals(cib)) {
				return true;
			}
			if (i == remainingBrackets.size() - 1 && 
				!currentBracket.equals(cib)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Removes all line breaks, whitespace, comments, and added information from rawEncoding and 
	 * sets cleanEncoding to the result.    
	 */  
	// TESTED (together with getCleanEncoding())
	void setCleanEncoding() {
		String cleanEnc = "";
		
		String rawEnc = getRawEncoding();
		
		String oib = SymbolDictionary.OPEN_INFO_BRACKET;
		String cib = SymbolDictionary.CLOSE_INFO_BRACKET;
		
		// 1. Remove all carriage returns and line breaks; remove leading and trailing whitespace
		cleanEnc = rawEnc.replaceAll("\r", "");
		cleanEnc = cleanEnc.replaceAll("\n", "");
		cleanEnc = cleanEnc.trim();

		// 2. Remove all comments
		// NB: while-loop more convenient than for-loop in order not to overlook comments 
		// immediately succeeding one another
		while (cleanEnc.contains(oib)) {
			int openCommentIndex = cleanEnc.indexOf(oib);
			int closeCommentIndex = cleanEnc.indexOf(cib, openCommentIndex);
			String comment = cleanEnc.substring(openCommentIndex, closeCommentIndex + 1);
			cleanEnc = cleanEnc.replace(comment, "");
		}
		cleanEncoding = cleanEnc;
	}


	// TESTED (together with setCleanEncoding())
	public String getCleanEncoding() {
		return cleanEncoding;
	}


	/**
	 * Sets <code>eventsBarlinesFootnotes</code>.
	 **/
	// TESTED (together with getEventsBarlinesFootnotes())
	void setEventsBarlinesFootnotes() {
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String oib = SymbolDictionary.OPEN_INFO_BRACKET;
		String cib = SymbolDictionary.CLOSE_INFO_BRACKET;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String invertedSp = "<";
		String invertedSbi = "\\";

		String rawEnc = getRawEncoding();
		// Remove all carriage returns and line breaks; remove leading and trailing whitespace
		rawEnc = rawEnc.replaceAll("\r", "");
		rawEnc = rawEnc.replaceAll("\n", "");
		rawEnc = rawEnc.trim();
		// Remove end break indicator
		rawEnc = rawEnc.replaceAll(SymbolDictionary.END_BREAK_INDICATOR, "");
		
		// List all comments
		List<String> allNonEditorialComments = new ArrayList<>();
		List<String> allEditorialComments = new ArrayList<>();
		for (int i = 0; i < rawEnc.length(); i++) {
			int commOpenInd = rawEnc.indexOf(oib, i);
			int commCloseInd = rawEnc.indexOf(cib, commOpenInd + 1);
			String comment = rawEnc.substring(commOpenInd, commCloseInd+1);
			// Non-editorial comment
			if (!comment.startsWith(oib + FOOTNOTE_INDICATOR)) {
				allNonEditorialComments.add(comment);
			}
			// Editorial comment
			else {
				allEditorialComments.add(comment.substring(comment.indexOf(oib)+1, 
					comment.indexOf(cib)));
				// In rawEnc, temporarily replace any spaces and SBIs within comments, so 
				// that splitting on them (see below) remains possible
				if (comment.contains(sp)) {
					rawEnc = rawEnc.replace(comment, comment.replace(sp, invertedSp));
				}
				if (comment.contains(sbi)) {
					rawEnc = rawEnc.replace(comment, comment.replace(sbi, invertedSbi));
				}
			}
			if (commCloseInd == rawEnc.lastIndexOf(cib)) {
				break;
			}
			else {
				i = commCloseInd;
			}
		}

		// Remove all non-editorial comments from rawEnc
		for (String comment : allNonEditorialComments) {
			rawEnc = rawEnc.replace(comment, "");
		}

		// To enable splitting, add a space after all barlines. NB: This will also affect any
		// barlines in comments (but only if they are followed by a symbol separator!) - which
		// is not a problem as the unadapted comments are stored in allNonEditorialComments		
		List<String> barlinesAsString = new ArrayList<>();
		for (ConstantMusicalSymbol cms : ConstantMusicalSymbol.constantMusicalSymbols) {
			if (cms != ConstantMusicalSymbol.SPACE) {
				barlinesAsString.add(cms.getEncoding());
			}
		}
		// Sort the barlines by length (longest first), so that they are replaced correctly
		// (a shorter barline, e.g., :|, can be part of a longer one, e.g., :|:, leading to 
		// partial replacement of the longer one)
		// See https://stackoverflow.com/questions/29280257/how-to-sort-an-arraylist-by-its-elements-size-in-java
		barlinesAsString.sort(Comparator.comparing(String::length).reversed());
		// A barline is always preceded and followed by a SS. By including both these 
		// SSs in the replace command, it is ensured that a shorter barline that is part 
		// of a longer one does not accidentally replace part of the longer one. 
		// Example: if s == "|:", any occurences of "|:." will be replaced, but also the 
		// last two chars in any occurrences of "||:." By adding a SS also before s, 
		// only occurrences of ".|:." will be replaced.
		// NB: Any barlines at the beginning of a piece are NOT preceded by a SS, and 
		// must be dealt with separately
		for (String b : barlinesAsString) {
			if (ConstantMusicalSymbol.isBarline(b)) {
				String firstHalf, secondHalf;
				int startInd = 0;
				int breakInd = -1;
				// a. If the encoding starts with a barline, handle this barline first 
				// and adapt startInd
				if (rawEnc.startsWith(b+ss)) { 
					// If the barline is not followed by a comment: replace
					if (rawEnc.startsWith(b+ss)) {
						breakInd = rawEnc.indexOf(ss)+1;
					}
					// If the barline is followed by a comment: replace 
					if (rawEnc.startsWith(b+oib)) {
						breakInd = rawEnc.indexOf(cib)+1 + ss.length();
					}
					firstHalf = rawEnc.substring(0, breakInd);
					secondHalf = rawEnc.substring(breakInd);
					rawEnc = firstHalf + sp + ss + secondHalf;
					startInd = (b+ss).length();
				}
				// b. Handle any remaining barlines
				// If the barline is not followed by a comment: replace all
				if (rawEnc.contains(ss+b+ss)) {
					rawEnc = rawEnc.replace(ss+b+ss, ss+b+ss+sp+ss);
				}
				// If the barline is followed by a comment: replace one by one
				if (rawEnc.contains(ss+b+oib)) {
					// Traverse rawEnc and add (sp + ss) after each occurence of 
					// b + comment + ss
					int barlineInd = rawEnc.indexOf(ss+b+oib, startInd); // 263
					while (barlineInd >= 0) {
						int oibInd = rawEnc.indexOf(oib, barlineInd); // 264
//						System.out.println("oibInd = " + oibInd);
						int cibInd = rawEnc.indexOf(cib, oibInd); // 286
//						System.out.println("cibInd = " + cibInd);
						int ssInd = rawEnc.indexOf(ss, cibInd); // 287
//						System.out.println("ssInd = " + ssInd);
						breakInd = ssInd + 1;
						firstHalf = rawEnc.substring(0, breakInd);
//						System.out.println("1st = " + firstHalf);
						secondHalf = rawEnc.substring(breakInd);
//						System.out.println("2nd = " + secondHalf);
						rawEnc = firstHalf + sp + ss + secondHalf;
						barlineInd = rawEnc.indexOf(ss+b+oib, barlineInd + 1);
//						System.out.println("barlineInd = " + barlineInd);
					}
				}
			}
		}

		// List events per system
		List<List<String[]>> eventsPerSystem = new ArrayList<>();
		int commentCounter = 0;
		int bar = 1;
		String[] systems = rawEnc.split(sbi);
		for (int i = 0; i < systems.length; i++) {
			List<String[]> eventsCurrSystem = new ArrayList<>();
			String[] events = systems[i].split(sp + ss);
			for (int j = 0; j < events.length; j++) {
				String event = events[j];
				System.out.println(Arrays.asList(event));
				boolean containsComment = event.contains(oib + FOOTNOTE_INDICATOR);
				
				// If the event does not contain a comment: add
				if (!containsComment) {
					eventsCurrSystem.add(new String[]{
						event, String.valueOf(bar), null, null});
				}
				// If the event contains a comment: separate event and comment, and add
				if (containsComment) {
//					boolean startsWithComment = event.startsWith(oib + FOOTNOTE_INDICATOR);
					String adaptedEvent = event.substring(0, event.indexOf(oib)) + 
						event.substring(event.indexOf(cib) + 1);
					// Get unadapted comment (the one in event may have been altered if it 
					// contains symbols split on, such as a space or a SBI)
					String comment = allEditorialComments.get(commentCounter);
					String commentNum = "footnote #" + (commentCounter + 1);
					commentCounter++;
//					// If the comment is at the end of the event: add
//					if (!startsWithComment) {
					eventsCurrSystem.add(new String[]{
						adaptedEvent, String.valueOf(bar), comment, commentNum});
//					}
//					// If the comment is at the beginning of the event: reset last added and add
//					// NB: If there is a barline that is followed by a comment before the current
//					// event, that comment will end up preceding the current event. Example:
//					// original encoding:         sm.a2.a1.>.|.{@a footnote}sm.a2.a1}.>. 
//					// space added after barline: sm.a2.a1.>.|.>.{@a footnote}sm.a2.a1}.>.
//					// after split on space:      [sm.a2.a1., |., {@a footnote}sm.a2.a1}]
//					// In such a case, the comment must be moved to the last added element in
//					// eventsPerSystem
//					else {						
//						// systemToAdapt is the previous system if event is the first in the 
//						// current system, and the current system if not 
//						List<String[]> systemToAdapt = 
//							(j == 0) ? eventsPerSystem.get(i-1) : eventsCurrSystem;  
//						// Adapt comment and commentNum in element last added to systemToAdapt
//						systemToAdapt.set(systemToAdapt.size()-1, new String[]{
//							systemToAdapt.get(systemToAdapt.size()-1)[EVENT_IND], comment, commentNum});
//						// Add
//						eventsCurrSystem.add(new String[]{adaptedEvent, null, null});
//					}
				}
				
				// Increment bar (if it is not a decorative opening barline)
				// NB This works because the barlines are sorted by length
				if (j > 0) {
					for (String b : barlinesAsString) {
						if (event.startsWith(b)) {
							bar++;
						}
					}
				}
			}
//			System.out.println("new sys");
//			for (String[] e : eventsCurrSystem) {
//				System.out.println(Arrays.asList(e));
//			}
			eventsPerSystem.add(eventsCurrSystem);
		}
//		for (List<String[]> l : eventsPerSystem) {
//			System.out.println("system-------------------------");
//			for (String[] s : l) {
//				System.out.println(Arrays.toString(s));
//			}
//			System.out.println("-------------");
//		}
		eventsBarlinesFootnotes = eventsPerSystem;
	}


	/**
	 * Gets all events in the piece, organised per system. Each event is one of five types: 
	 * TS event, RS event, rest event, MS event, or barline event.
	 * 
	 * @return A <code>List</code>, each element of which represents a system as a 
	 * <code>List</code> of <code>String[]</code>s, each of which represents an event. 
	 * Each event contains 
	 * <ul>
	 * <li>at element 0: the event as encoded</li>
	 * <li>at element 1: the bar the event is in, derived from barline placement (where 
	 *     decorative barlines at the beginning of a staff are ignored); barlines 
	 *     themselves are counted as belonging to the bar they close</li>
	 * <li>at element 2: if the event has a footnote, that footnote; otherwise 
	 * <code>null</code></li>
	 * <li>at element 3: if the event has a footnote, the sequence number of that
	 * footnote; otherwise <code>null</code></li>
	 * </ul>
	 */
	// TESTED (together with setEventsBarlinesFootnotes())
	public List<List<String[]>> getEventsBarlinesFootnotes() {
		return eventsBarlinesFootnotes;
	}


	/**
	 * Sets <code>infoAndSettings</code>.
	 */
	// TESTED (together with getInfoAndSettings())
	void setInfoAndSettings() {
		List<String> ias = new ArrayList<>(); 
		List<String> metaData = getAllMetadata(); // needs rawEncoding
		ias.add(AUTHOR_IND, metaData.get(0).substring(metaData.get(0).indexOf(":") + 1).trim());
		ias.add(TITLE_IND, metaData.get(1).substring(metaData.get(1).indexOf(":" ) + 1).trim());
		ias.add(SOURCE_IND, metaData.get(2).substring(metaData.get(2).indexOf(":") + 1).trim());
		ias.add(TABSYMBOLSET_IND, metaData.get(3).substring(metaData.get(3).indexOf(":") + 1).trim());
		ias.add(TUNING_IND, metaData.get(4).substring(metaData.get(4).indexOf(":") + 1).trim());
		ias.add(TUNING_BASS_COURSES_IND, metaData.get(5).substring(metaData.get(5).indexOf(":") + 1).trim());
		ias.add(METER_IND, metaData.get(6).substring(metaData.get(6).indexOf(":") + 1).trim());
		ias.add(DIMINUTION_IND, metaData.get(7).substring(metaData.get(7).indexOf(":") + 1).trim());
		infoAndSettings = ias;
	}


	/**
	 * Gets info and settings.
	 * 
	 * @return A <code>String[]</code> containing:
	 * <ul>
	 * <li>at element 0: the author</li>
	 * <li>at element 1: the title</li>
	 * <li>at element 2: the source</li>
	 * <li>at element 3: the TabSymbolSet used for the encoding</li>
	 * <li>at element 4: the tuning</li>
	 * <li>at element 5: the TuningSeventhCourse (if any)</li>
	 * <li>at element 6: the meter information</li>
	 * <li>at element 7: the diminution</li>
	 * </ul>
	 */
	// TESTED (together with setInfoAndSettings())
	public List<String> getInfoAndSettings() {
		return infoAndSettings;
	}


	/**
	 * Gets all information added to the encoding, consisting of:
	 *   (1) the info and settings items (author, title, source, TabSymbolSet, tuning, tuningSeventhCourse, and meterInfo )
	 *   (2) a mix of footnotes and other indications such as bar numbers.
	 */
	// TESTED
	List<String> getAllMetadata() {
		String rawEnc = getRawEncoding();
		List<String> metaData = new ArrayList<String>();
		for (int i = 0; i < rawEnc.length(); i++) {
			char c = rawEnc.charAt(i);
			String currentChar = Character.toString(c); 
			if (currentChar.equals(SymbolDictionary.OPEN_INFO_BRACKET)) {
				int closeInfoIndex = rawEnc.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, i);
				String info = rawEnc.substring(i + 1, closeInfoIndex);
				metaData.add(info);
				i = closeInfoIndex;
			}
		}    
		return metaData;
	}


//	/**
//	 * Sets footnotes, a List<String> containing all the footnotes added to the encoding.
//	 */
//	// TESTED (together with getFootnotes())
//	void setFootnotes() {
//		List<String> fn = new ArrayList<>();
//		int footNoteCounter = 1;
//		for (String item : getMetaData()) {
//			if (item.startsWith(FOOTNOTE_INDICATOR)) {
//				fn.add("(" + footNoteCounter + ") " + item.substring(1));
//				footNoteCounter++;
//			}
//		}
//		footnotes = fn;
//	}


//	// TESTED (together with setFootnotes())
//	public List<String> getFootnotes() {
//		return footnotes;
//	}


	/**
	 * Checks the encoding to see whether 
	 * <ul>
	 * <li>all VALIDITY RULES are met</li> 
	 * <li>there are no unknown or missing symbols</li> 
	 * <li>all LAYOUT RULES are met</li> 
	 * </ul>
	 * NB: The encoding must always be checked in the sequence checkValidityRules() - checkSymbols() - 
	 *     checkLayoutRules()<br><br>
	 *      
	 * @return
	 * <ul>
	 * <li><code>null</code> if and only if all three conditions are true</li> 
     * <li>a String[] containing the relevant error information if not</li>
     * </ul>
	 */
	// TESTED
	public String[] checkForEncodingErrors() {
		Integer[] indicesRawAndCleanAligned = alignRawAndCleanEncoding();
		if (checkValidityRules(indicesRawAndCleanAligned) != null) {
			return checkValidityRules(indicesRawAndCleanAligned);
		}
		else if (checkSymbols(indicesRawAndCleanAligned) != null) {
			return checkSymbols(indicesRawAndCleanAligned);    
		}
		else if (checkLayoutRules(indicesRawAndCleanAligned) != null) {
			return checkLayoutRules(indicesRawAndCleanAligned);
		}
		return null;
	}
	
	
	/**
	 * Returns an Integer[] the size of rawEncoding in which the indices of cleanEncoding and rawEncoding are
	 * aligned, i.e., in which for each char in rawEncoding the index of that character in cleanEncoding is given.
	 * -1 is given where rawEncoding contains a char that is not part of cleanEncoding.
	 */
	// TESTED 
	Integer[] alignRawAndCleanEncoding() {
		String rawEnc = getRawEncoding();
		String cleanEnc = getCleanEncoding();
		// Initialise with default values of -1
		Integer[] indicesRawAndCleanAligned = new Integer[rawEnc.length()];
		indicesRawAndCleanAligned = new Integer[rawEnc.length()];
		Arrays.fill(indicesRawAndCleanAligned, -1);

		int startIndex = 0;
		for (int i = 0; i < cleanEnc.length(); i++) {
			String currentChar = cleanEnc.substring(i, i + 1);
			for (int j = startIndex; j < rawEnc.length(); j++) {
				// Skip comments
				if (rawEnc.substring(j, j + 1).equals(SymbolDictionary.OPEN_INFO_BRACKET)) {
					j = rawEnc.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, j);
				}
				else if (rawEnc.substring(j, j + 1).equals(currentChar)) {
					indicesRawAndCleanAligned[j] = i;
					startIndex = j + 1;
					break;
				}
			}  
		}
		return indicesRawAndCleanAligned;
	}


	/**
	 * Checks all VALIDITY RULES.
	 *     
	 * @param indicesRawAndCleanAligned
	 * @return  
	 * <ul>
	 * <li><code>null</code> if all the rules are met</li>
	 * <li>if not, a String[] containing</li>
	 * <ul>
	 * <li>at element 0: the index in rawEncoding of the first error char to be highlighted</li>
	 * <li>at element 1: the index in rawEncoding of the last error char to be highlighted</li>
	 * <li>at element 2: the appropriate error message</li>
	 * <li>at element 3: a reference to the rule that was broken</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	String[] checkValidityRules(Integer[] indicesRawAndCleanAligned) {
		String[] indicesAndMessages = new String[4]; 

		String cleanEnc = getCleanEncoding();
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String ebi = SymbolDictionary.END_BREAK_INDICATOR;
		
		// Check VALIDITY RULE 1: The encoding cannot contain whitespace 
		if (cleanEnc.contains(WHITESPACE)) {
			int indexOfFirstErrorChar = cleanEnc.indexOf(WHITESPACE);
			indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
			indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + WHITESPACE.length());
			indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove this whitespace.";
			indicesAndMessages[3] = "See VALIDITY RULE 1: The encoding cannot contain whitespace.";
			return indicesAndMessages;
		}
		// Check VALIDITY RULE 2: The encoding must end with an end break indicator
		if (!cleanEnc.endsWith(ebi)) {
			indicesAndMessages[0] = String.valueOf(-1);
			indicesAndMessages[1] = String.valueOf(-1);
			indicesAndMessages[2] = String.valueOf("INVALID ENCODING ERROR -- The encoding does not end with an end break indicator.");
			indicesAndMessages[3] = String.valueOf("See VALIDITY RULE 2: The encoding must end with an end break indicator.");
			return indicesAndMessages;
		}
		// Check VALIDITY RULE 3: A system cannot start with a punctuation symbol
		String VR3 = "See VALIDITY RULE 3: A system cannot start with a punctuation symbol.";
		String noEBI = cleanEnc.substring(0, cleanEnc.length() - ebi.length());
		String[] allSystems = noEBI.split(sbi);
		// a. Check whether there is a system starting with a SBI
		if (noEBI.startsWith(sbi) || noEBI.contains(ebi)) {
			int indexOfFirstErrorChar = -1;
			// a. If the first system starts with a SBI
			if (noEBI.startsWith(sbi)) {
				indexOfFirstErrorChar = noEBI.indexOf(sbi);
			}
			// b. If a later system starts with a SBI
			else {
				indexOfFirstErrorChar = noEBI.indexOf(ebi) + 1;
			}
			indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
			indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + sbi.length());
			indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove this system break indicator.";
			indicesAndMessages[3] = VR3;
			return indicesAndMessages;
		}
		// b. Check whether there is a system starting with a SS
		else {
			int indicesTraversed = 0;
			for (String system : allSystems) {
				if (system.startsWith(ss)) {
					int indexOfFirstErrorChar = indicesTraversed;
					indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + ss.length());		
					indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove this symbol separator.";
					indicesAndMessages[3] = VR3;
					return indicesAndMessages;
				}
				indicesTraversed += system.length() + sbi.length();
			}
		}
		// Check VALIDITY RULE 4: Each system must end with a symbol separator
		int numSystemsTraversed = 0;
		int indicesTraversed = 0;
		for (String system : allSystems) {
			numSystemsTraversed++;
			indicesTraversed += system.length() + sbi.length();   	
			if (!system.endsWith(ss)) {
				int indexOfFirstErrorChar = indicesTraversed - 1;
				indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
				// Is the error in the last system?
				if (numSystemsTraversed == allSystems.length) {
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, 
						indexOfFirstErrorChar) + ebi.length());
					indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a symbol separator before this end break indicator.";
				}
				else {
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned,
						indexOfFirstErrorChar) + sbi.length());
					indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a symbol separator before this system break indicator.";
				}
				indicesAndMessages[3] = "See VALIDITY RULE 4: Each system must end with a symbol separator.";
				return indicesAndMessages;
			}
		}
		return null;
	}


	/**
	 * Checks whether there are any missing or unknown symbols.
	 *
	 * @param indicesRawAndCleanAligned
	 * @return
	 * <ul>
	 * <li><code>null</code> if there are no missing or unknown symbols</li>
	 * <li>if so, a String[] containing</li>
	 * <ul>
	 * <li>at element 0: the index in rawEncoding of the first error char to be highlighted</li>
	 * <li>at element 1: the index in rawEncoding of the last error char to be highlighted</li>
	 * <li>at element 2: the appropriate error message</li>
	 * <li>at element 3: a reference to the rule that was broken</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	String[] checkSymbols(Integer[] indicesRawAndCleanAligned) {
		String[] indicesAndMessages = new String[4];
		
		String cleanEnc = getCleanEncoding();
		
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String ebi = SymbolDictionary.END_BREAK_INDICATOR;

		String VR5 = "See VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator.";
		String noEBI = cleanEnc.substring(0, cleanEnc.length() - ebi.length());
		String[] allSystems = noEBI.split(sbi);
		int indicesTraversed = 0;
		for (String system : allSystems) {
			int symbolSeparatorIndex = -1;
			int nextSymbolSeparatorIndex = system.indexOf(ss, symbolSeparatorIndex + 1);
			while (nextSymbolSeparatorIndex != -1) {
				String symbol = system.substring(symbolSeparatorIndex + 1, nextSymbolSeparatorIndex);
				// a. Does nextSymbolSeparatorIndex succeed symbolSeparatorIndex immediately? Missing symbol found
				if (nextSymbolSeparatorIndex == symbolSeparatorIndex + 1) {
					int indexOfFirstErrorChar = indicesTraversed + nextSymbolSeparatorIndex;
					indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + SymbolDictionary.SYMBOL_SEPARATOR.length());
					indicesAndMessages[2] = "MISSING SYMBOL ERROR -- Remove symbol separator or insert symbol before.";
					indicesAndMessages[3] = VR5;
					return indicesAndMessages;
				}
				// b. Is encodedSymbol no CMS and no VMS? Unknown symbol found 
				else if (ConstantMusicalSymbol.getConstantMusicalSymbol(symbol) == null &&	
					TabSymbol.getTabSymbol(symbol, getTabSymbolSet()) == null && 
					RhythmSymbol.getRhythmSymbol(symbol) == null && 
					MensurationSign.getMensurationSign(symbol) == null) {
					int indexOfFirstErrorChar = indicesTraversed + symbolSeparatorIndex + 1;
					indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar));
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length()); 
					indicesAndMessages[2] = "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.";
					indicesAndMessages[3] = VR5;
					return indicesAndMessages;
				}
				symbolSeparatorIndex = nextSymbolSeparatorIndex;
				nextSymbolSeparatorIndex = system.indexOf(ss, symbolSeparatorIndex + 1);
			}
			indicesTraversed += system.length() + sbi.length();
		}
		return null;
	}


	/**
	 * Checks all LAYOUT RULES.
	 * 
	 * @param indicesRawAndCleanAligned
	 * @return
	 * <ul>
	 * <li><code>null</code> if all the rules are met</li>
	 * <li>if not, a String[] containing</li>
	 * <ul>
	 * <li>at element 0: the index in rawEncoding of the first error char to be highlighted</li>
	 * <li>at element 1: the index in rawEncoding of the last error char to be highlighted</li>
	 * <li>at element 2: the appropriate error message</li>
	 * <li>at element 3: a reference to the rule that was broken</li>
	 * </ul>
	 * </ul>
	 */
	// TESTED
	String[] checkLayoutRules(Integer[] indicesRawAndCleanAligned) {
		String[] indicesAndMessages = new String[4];

		String cleanEnc = getCleanEncoding();
		
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String ebi = SymbolDictionary.END_BREAK_INDICATOR;
		
		String noEBI = cleanEnc.substring(0, cleanEnc.length() - ebi.length());
		String[] allSystems = noEBI.split(sbi);
		int indicesTraversed = 0;
		for (String system : allSystems) {
			// a. Rules 1-6 pertain to individual symbols within the system 
			// Get the indices of the first and last two SSindices 
			int symbolSeparatorIndex = -1;
			int nextSymbolSeparatorIndex = system.indexOf(ss, symbolSeparatorIndex + 1);
			// VR 4 garuantees that each system ends with a SS
			int lastSymbolSeparatorIndex = system.lastIndexOf(ss); 
			int penultimateSymbolSeparatorIndex = system.lastIndexOf(ss, lastSymbolSeparatorIndex - 1);
			while (nextSymbolSeparatorIndex != -1) {
				// Determine the current and, if the current is not the last symbol in the system, the next encoded symbol
				String symbol = system.substring(symbolSeparatorIndex + 1, nextSymbolSeparatorIndex);
				String nextSymbol = null;
				int nextNextSymbolSeparatorIndex = -1;
				if (symbolSeparatorIndex < penultimateSymbolSeparatorIndex) {
					nextNextSymbolSeparatorIndex = system.indexOf(ss, nextSymbolSeparatorIndex + 1);
					nextSymbol = system.substring(nextSymbolSeparatorIndex + 1, nextNextSymbolSeparatorIndex);
				}
				// LAYOUT RULE 1: A system can start with any event but a space
				// Is th)e first encoded symbol a space?
				if (symbolSeparatorIndex == -1 && symbol.equals(sp)) {
					int indexOfFirstErrorChar = indicesTraversed + symbolSeparatorIndex + 1;
					indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length());
					indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove this space."; 
					indicesAndMessages[3] = "See LAYOUT RULE 1: A system can start with any event but a space."; 
					return indicesAndMessages;
				}
				// LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline (i.e., with a CMS)
				if (symbolSeparatorIndex == penultimateSymbolSeparatorIndex && 
					ConstantMusicalSymbol.getConstantMusicalSymbol(symbol) == null) {
					int indexOfFirstErrorChar = indicesTraversed + penultimateSymbolSeparatorIndex + 1;
					indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
					indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length());
					if (TabSymbol.getTabSymbol(symbol, getTabSymbolSet()) != null) {
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
					}
					if (RhythmSymbol.getRhythmSymbol(symbol) != null) {
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this RhythmSymbol.";
					}
					if (MensurationSign.getMensurationSign(symbol) != null) {
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this MensurationSign.";
					}        
					indicesAndMessages[3] = "See LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline.";
					return indicesAndMessages;
				}
				// LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space  
				// Skip the last symbol of the system, which, as guaranteed by LR2 above, is always a CMS         
				if (symbolSeparatorIndex < penultimateSymbolSeparatorIndex && 
					ConstantMusicalSymbol.getConstantMusicalSymbol(symbol) != null) {        	
					if (nextSymbol.equals(sp)) {
						int indexOfFirstErrorChar = indicesTraversed + nextSymbolSeparatorIndex + 1;
						indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
						indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + nextSymbol.length());
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove this space.";
						indicesAndMessages[3] = "See LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space.";  
						return indicesAndMessages;
					}
				}
				// LAYOUT RULE 4: A vertical sonority must be succeeded by a space. Put differently: a TabSymbol can 
				// only be succeeded by 
				// (i)  another TabSymbol, in which case both are part of the same vertical sonority;
				// (ii) a space, in which case it is the only or last TabSymbol of a vertical sonority.
				// NB: LR2 above guarantees that the last symbol in the system is a CMS; thus, when symbol is the last
				//     symbol, the inner if, then yielding a nullPointerException (nextSymbol will be null), is never called 
				if (TabSymbol.getTabSymbol(symbol, getTabSymbolSet()) != null) {
					if (TabSymbol.getTabSymbol(nextSymbol, getTabSymbolSet()) == null && 
						!nextSymbol.equals(sp)) {
						int indexOfFirstErrorChar = indicesTraversed + symbolSeparatorIndex + 1;   
						indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
						indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length());
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
						indicesAndMessages[3] = "See LAYOUT RULE 4: A vertical sonority must be succeeded by a space.";
						return indicesAndMessages;
					}
				}
				// LAYOUT RULE 5: A rest (or a rhythm dot at the beginning of system or bar) must be succeeded by a space.
				// Put differently: a RhythmSymbol can only be succeeded by 
				// (i)  a TabSymbol, in which case it is the first symbol of a vertical sonority; 
				// (ii) a space, in which case it denotes a rest or a rhythmDot at the beginning of a bar.  
				// NB: LR2 above guarantees that the last symbol in the system is a CMS; thus, when symbol is the last
				//     symbol, the inner if, then yielding a nullPointerException (nextSymbol will be null), is never called 
				if (RhythmSymbol.getRhythmSymbol(symbol) != null) {
					if (TabSymbol.getTabSymbol(nextSymbol, getTabSymbolSet()) == null &&
						!nextSymbol.equals(sp)) {
						int indexOfFirstErrorChar = indicesTraversed + symbolSeparatorIndex + 1;   
						indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
						indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length());
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this RhythmSymbol.";
						indicesAndMessages[3] = "See LAYOUT RULE 5: A rest (or rhythm dot at the beginning of a system or bar) must be succeeded " + 
							"by a space.";
						return indicesAndMessages;          
					}
				}
				// LAYOUT RULE 6: A MensurationSign must be succeeded by a space. Compound mensuration signs, consisting 
				// of two separate mensuration signs, exist. Thus, a MensurationSign can only be succeeded by 
				// (i)  another MensurationSign, in which case it is the first encoded mensuration sign of
				//      a compound mensuration sign;
				// (ii) a space, in which case it is either a single mensuration sign or the last encoded
				//      mensuration sign of a compound mensuration sign.   
				// NB: LR2 above guarantees that the last symbol in the system is a CMS; thus, when symbol is the last
				//     symbol, the inner if, then yielding a nullPointerException (nextSymbol will be null), is never called 
				if (MensurationSign.getMensurationSign(symbol) != null) {
					if (MensurationSign.getMensurationSign(nextSymbol) == null && 
						!nextSymbol.equals(sp)) {
						int indexOfFirstErrorChar = indicesTraversed + symbolSeparatorIndex + 1;
						indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
						indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + symbol.length());
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- Insert a space after this MensurationSign.";
						indicesAndMessages[3] = "See LAYOUT RULE 6: A mensuration sign must be succeeded by a space.";
						return indicesAndMessages;
					}
				}
				symbolSeparatorIndex = nextSymbolSeparatorIndex;
				nextSymbolSeparatorIndex = system.indexOf(ss, symbolSeparatorIndex + 1);
			}

			// b. Rules 7 and 8 pertain to individual events within the system 
			String[] allEvents = 
				system.split(sp + ss);
			for (String event : allEvents) {
				// Remove any barlines preceding the event as a result of the splitting
				String firstSymbol = event.substring(0, event.indexOf(ss));
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(firstSymbol) != null) {
					event = event.substring(event.indexOf(ss) + 1, event.length());	
				}
				// Split the event into its individual symbols
				// NB: The SS cannot be used as the regular expression to split around because a dot is an existing
				// regular expression in Java. Therefore, all SS are replaced with whitespace before the splitting is done
				String[] allSymbols = event.replace(ss, WHITESPACE).split(WHITESPACE);
				List<Integer> coursesUsed = new ArrayList<Integer>();
				for (int i = 0; i < allSymbols.length; i++) {
					String symbol = allSymbols[i];
					// LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course      	
					if (TabSymbol.getTabSymbol(symbol, getTabSymbolSet()) != null) {
						int course = TabSymbol.getTabSymbol(symbol, getTabSymbolSet()).getCourse();
						if (coursesUsed.contains(course)) {
							int indexOfFirstErrorChar = indicesTraversed + system.indexOf(event);
							indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
							indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + (event.length() - 1));
							indicesAndMessages[2] = "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).";
							indicesAndMessages[3] = "See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course.";
							return indicesAndMessages;
						}
						else {
							coursesUsed.add(course);
						}
					}
					// LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence, i.e.:
					// (i)  Any RS must be encoded first
					// (ii) Any TS must follow, encoded with the one on the lowest course first 
					List<Integer> coursesUsedOrdered = new ArrayList<Integer>(coursesUsed);
					Collections.sort(coursesUsedOrdered);
					Collections.reverse(coursesUsedOrdered);
					if ((i != 0 && RhythmSymbol.getRhythmSymbol(symbol) != null) ||
						!coursesUsed.equals(coursesUsedOrdered)	) {
						int indexOfFirstErrorChar = indicesTraversed + system.indexOf(event); 
						indicesAndMessages[0] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar)); 
						indicesAndMessages[1] = String.valueOf(getIndexInRawEncoding(indicesRawAndCleanAligned, indexOfFirstErrorChar) + (event.length() - 1));	
						indicesAndMessages[2] = "INVALID ENCODING ERROR -- This vertical sonority is not encoded in the correct sequence.";
						indicesAndMessages[3] = "See LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence.";
						return indicesAndMessages;
					}
				}
			}      
			indicesTraversed += system.length() + sbi.length();
		}
		return null;
	}


	/**
	 * Given the index of a char in cleanEncoding, finds the corresponding index of that same
	 * char in rawEncoding.
	 * Returns -1 if the index is not found.
	 * 
	 * @param indexInClean
	 * @return 
	 */
	// TESTED
	int getIndexInRawEncoding(Integer[] indicesRawAndCleanAligned, int indexInClean) {
		for (int i = 0; i < indicesRawAndCleanAligned.length; i++) {
			if (indicesRawAndCleanAligned[i] == indexInClean) {
				return i;
			}
		}
		return -1;
	}


	/**
	 *  Sets tuning and encodedTuning both with the tuning specified in the encoding. tuning may
	 *  be changed during further processing; encodedTuning retains its initial value.
	 */
	// TESTED (together with getTunings());
	void setTunings() {
		Tuning[] tun = new Tuning[2];
		for (Tuning t : Tuning.values()) { 
			if (t.toString().equals(getInfoAndSettings().get(TUNING_IND))) {
				tun[ENCODED_TUNING_IND] = t;
				tun[NEW_TUNING_IND] = t; 
				break;
			}
		}
		tunings = tun;
	}


	// TESTED (together with setTunings())
	public Tuning[] getTunings() {
		return tunings;
	}


	/**
	 * Fills the following lists and adds them to listsOfSymbols:
	 * 0. listOfAllSymbols: contains all the individual symbols (CMS and VMS) in the encoding
	 * 1. listOfTabSymbols: contains all the individual TS in the encoding
	 * 2. listOfRhythmSymbols: contains all the individual RS in the encoding
	 * 3. listOfMensurationSigns: contains all the individual MS in the encoding
	 * 4. listOfBarlines: contains all the barlines (incl. double, repeat, etc.) in the encoding 
	 * 5. listOfAllEvents: contains all the individual events in the encoding 
	 * 
	 * NB: This method must always be called along with (before) setListsOfStatistics()
	 */
	// TESTED (together with getListsOfSymbols())
	void setListsOfSymbols() {
		List<List<String>> los = new ArrayList<>();
		
		// Remove EBI and SBI from cleanEncoding    
		String encodingAsReadNoSBI = 
			getCleanEncoding().replace(SymbolDictionary.SYSTEM_BREAK_INDICATOR, "");

		// Make the lists
		List<String> listOfAllSymbols = new ArrayList<String>();
		List<String> listOfTabSymbols = new ArrayList<String>();
		List<String> listOfRhythmSymbols = new ArrayList<String>();
		List<String> listOfMensurationSigns = new ArrayList<String>();
		List<String> listOfBarlines = new ArrayList<String>();
		List<String> listOfAllEvents = new ArrayList<String>();

		String event = "";
		for (int i = 0; i < encodingAsReadNoSBI.length() - 1; i++) {
			int indexOfNextSymbolSeparator = encodingAsReadNoSBI.indexOf(SymbolDictionary.SYMBOL_SEPARATOR, i);
			String currentSymbol = encodingAsReadNoSBI.substring(i, indexOfNextSymbolSeparator);
			// 0. listOfAllSymbols
			listOfAllSymbols.add(currentSymbol);
			// 1. listOfTabSymbols 
			if(TabSymbol.getTabSymbol(currentSymbol, getTabSymbolSet()) != null) {
				listOfTabSymbols.add(currentSymbol);
			}
			// 2. listOfRhythmSymbols
			else if (RhythmSymbol.getRhythmSymbol(currentSymbol) != null) {
				listOfRhythmSymbols.add(currentSymbol);
			}
			// 3. listOfMensurationSigns
			else if (MensurationSign.getMensurationSign(currentSymbol) != null) {
				listOfMensurationSigns.add(currentSymbol);
			}
			// 4. listOfBarlines
			else if (ConstantMusicalSymbol.getConstantMusicalSymbol(currentSymbol) != null && 
				!currentSymbol.equals(ConstantMusicalSymbol.SPACE.getEncoding())) {
				listOfBarlines.add(currentSymbol);
			}
			// 5. listOfAllEvents
			// a. If currentSymbol is not a CMS: add to event
			if (ConstantMusicalSymbol.getConstantMusicalSymbol(currentSymbol) == null) {
				event = event.concat(currentSymbol + ".");
			}
			// b. If currentSymbol is a CMS
			else {
				// a. If currentSymbol is a space: remove the last SS from event and add it to listOfAllEvents; reset event  
				if (currentSymbol.equals(ConstantMusicalSymbol.SPACE.getEncoding())) {
					event = event.substring(0, event.length() - 1);
					listOfAllEvents.add(event);
					event = "";
				}
				// b. If symbol is any CMS but a space: add to listOfAllEvents
				else { 
					listOfAllEvents.add(currentSymbol);
				}
			}
			// Reset i
			i = indexOfNextSymbolSeparator;
		}
		// Add the lists to listsOfSymbols
		los.add(ALL_SYMBOLS_IND, listOfAllSymbols);
		los.add(TAB_SYMBOLS_IND, listOfTabSymbols);
		los.add(RHYTHM_SYMBOLS_IND, listOfRhythmSymbols);
		los.add(MENSURATION_SIGNS_IND, listOfMensurationSigns);
		los.add(BARLINES_IND, listOfBarlines);
		los.add(ALL_EVENTS_IND, listOfAllEvents);
		
		listsOfSymbols = los;
	}


	// TESTED (together with setListsOfSymbols())
	public List<List<String>> getListsOfSymbols() {
		return listsOfSymbols;
	}


	/**
	 * Fills the following lists and adds them to listsOfStatistics:
	 * Of the same length as listOfAllEvents are:
	 * 0.  isTabSymbolEvent: indicates by means of 1 or 0 whether the event at index i contains a TS or not  
	 * 1.  isRhythmSymbolEvent: indicates by means of 1 or 0 whether the event at index i contains a RS or not 
	 * 2.  isRestEvent: indicates by means of 1 or 0 whether the event at index i represents a rest or not
	 * 3.  isMensurationSignEvent: indicates by means of 1 or 0 whether the event at index i contains a MS or not
	 * 4.  isBarlineEvent: indicates by means of 1 or 0 whether the event at index i contains a barline or not
	 * 5.  sizeOfEvents: indicates the size (in number of TabSymbols) of the the event at index i (0 if the event is
	 *     a barline, MS, or a rest). 
	 * 6.  durationOfEvents: indicates by means of a number the duration (in semifusae/32nd notes) of the event at 
	 *     index i (0 if the event is a barline or a MS).   
	 * 
	 * Of the same length as listOfTabSymbols are:
	 * 7.  horizontalPositionOfTabSymbols: indicates the index of the event a TS belongs to, considering ALL events
	 * 8.  verticalPositionOfTabSymbols: indicates the sequence number of a TS in an event (0 being the lowest). 
	 * 9.  durationOfTabSymbols: indicates the duration (in semifusae/32nd notes) of each TS
	 * 10. gridXOfTabsymbols: indicates the X-coordinate in the grid of a TS (measured in multiples of the 
	 *     smallest note duration)
	 * 11. gridYOfTabSymbols: indicates the Y-coordinate in the grid of a TS (measured in pitch as a MIDInumber)
	 * 12. horizontalPositionInTabSymbolEventsOnly: indicates the index of the event a TS belongs to, considering
	 *	   ONLY TS events
	 *
	 * NB: This method must always be called along with (after) setListsOfSymbols()
	 */
	// TESTED (together with getListsOfStatistics())
	void setListsOfStatistics() { 
		List<List<Integer>> los = new ArrayList<>();
		
		// 0-6. Make the lists that have the same size as listOfAllEvents
		List<Integer> isTabSymbolEvent = new ArrayList<Integer>();
		List<Integer> isRhythmSymbolEvent = new ArrayList<Integer>();
		List<Integer> isRestEvent = new ArrayList<Integer>();
		List<Integer> isMensurationSignEvent = new ArrayList<Integer>();
		List<Integer> isBarlineEvent = new ArrayList<Integer>();
		List<Integer> sizeOfEvents = new ArrayList<Integer>();
		List<Integer> durationOfEvents = new ArrayList<Integer>();
		int newDuration = 0;
		int currentDuration = 0;
		List<List<String>> loss = getListsOfSymbols();
		TabSymbolSet tss = getTabSymbolSet();
		List<String> listOfAllEvents = loss.get(Encoding.ALL_EVENTS_IND);
//		boolean tripletActive = false;
//		List<Integer> triplet = new ArrayList<>();
		for (int i = 0; i < listOfAllEvents.size(); i++) {
			String currentEvent = listOfAllEvents.get(i);    		
			// 0-4. isTabSymbolEvent, isRhythmSymbolEvent, isRestEvent, isMensurationSignEvent, and isBarlineEvent
			isTabSymbolEvent.add(0); isRhythmSymbolEvent.add(0); isRestEvent.add(0); isMensurationSignEvent.add(0);
			isBarlineEvent.add(0); 
			// Add a SS so that the for-loop below also works for events containing only a single symbol
			currentEvent = currentEvent.concat(".");
			int numTabSymbolsInEvent = 0;
			// For each symbol in the event
			for (int j = 0; j < currentEvent.length() - 1; j++) {
				int indexOfNextSymbolSeparator = currentEvent.indexOf(SymbolDictionary.SYMBOL_SEPARATOR, j);
				String currentSymbol = currentEvent.substring(j, indexOfNextSymbolSeparator);
				// If TS
				if (TabSymbol.getTabSymbol(currentSymbol, tss) != null) {
					isTabSymbolEvent.set(i, 1);
					// Also increase numTabSymbolsInEvent, which detemines the size of the event
					numTabSymbolsInEvent++;
				}
				// If RS
				if (RhythmSymbol.getRhythmSymbol(currentSymbol) != null) {
					isRhythmSymbolEvent.set(i, 1);
					// If the event contains only one symbol and that symbol is a RS, the event is a restEvent
					if (currentEvent.indexOf(SymbolDictionary.SYMBOL_SEPARATOR) == (currentEvent.length() - 1)) {
						isRestEvent.set(i, 1);
					}
				}
				// If MS
				if (MensurationSign.getMensurationSign(currentSymbol) != null) {
					isMensurationSignEvent.set(i, 1);
				}
				// If barline
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(currentSymbol) != null) {
					isBarlineEvent.set(i, 1);
				}
				j = indexOfNextSymbolSeparator;
			}
			// 5. sizeOfEvents
			sizeOfEvents.add(numTabSymbolsInEvent);     
			// 6. durationOfEvents
			// a. If currentEvent is a rhythmSymbolEvent (which can be a restEvent as well): determine 
			// newDuration, add it to durationOfEvents, and reset currentDuration 
			if (isRhythmSymbolEvent.get(i) == 1) {
				// Determine the RS, which is the first (or only) symbol of the event
				String firstSymbol = currentEvent.substring(0, currentEvent.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));      	
				// a. If firstSymbol is a regular RS 
				if (!firstSymbol.equals(RhythmSymbol.rhythmDot.getEncoding())) {
					RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(firstSymbol);
					newDuration = rs.getDuration();
					// First RS of a triplet? Add to triplet 
//					if (firstSymbol.startsWith(RhythmSymbol.triplet.getEncoding())) {
//						triplet.add(newDuration);
//					}
//					// Second or third RS of a triplet?
//					// NB Triplets always appear in successive events (i values)
//					else {
//						if (triplet.size() != 0) {
//							// Second RS: add element at index 1; third RS: add element at index 2
//							// --> general: add element at index triplet.size()
//							newDuration = rs.getTripletValues().get(triplet.size());
//							// Add to triplet if i is second triplet event; reset triplet if i is
//							// third triplet event
//							if (triplet.size() == 1) {
//								triplet.add(newDuration);
//							}
//							else if (triplet.size() == 2) {
//								triplet = new ArrayList<>();
//							}
//						}
//					}
				}
				// b. If firstSymbol is a rhythmDot
				else {
					newDuration = currentDuration/2;
				}
				durationOfEvents.add(newDuration);
				currentDuration = newDuration;
			}
			// b. If currentEvent is a tabSymbolEvent but not a rhythmSymbolEvent, the value stored in currentDuration
			// is added to durationOfEvents
			// NB It is assumed that a triplet is always followed by a rhythmSymbolEvent
			else if (isTabSymbolEvent.get(i) == 1 && isRhythmSymbolEvent.get(i) == 0) {
				durationOfEvents.add(currentDuration);
			}
			// c. If currentEvent is a MS or a barline, 0 is added to durationOfEvents  
			else if (isMensurationSignEvent.get(i) == 1 || isBarlineEvent.get(i) == 1) {
				durationOfEvents.add(0);
			}
		}

		// 7-12. Make the lists that have the same size as listOfTabSymbols
		List<Integer> horizontalPositionOfTabSymbols = new ArrayList<Integer>();
		List<Integer> verticalPositionOfTabSymbols = new ArrayList<Integer>();
		List<Integer> durationOfTabSymbols = new ArrayList<Integer>();
		List<Integer> gridXOfTabSymbols = new ArrayList<Integer>();
		List<Integer> gridYOfTabSymbols = new ArrayList<Integer>();
		List<Integer> horizontalPositionInTabSymbolEventsOnly = new ArrayList<Integer>();
		// List the TS indices per event. Events that contain no TS are represented by empty lists
		List<List<Integer>> indicesPerEvent = new ArrayList<List<Integer>>();
		int lowestNoteIndex = 0;
		for (int i = 0; i < sizeOfEvents.size(); i++) {
			int sizeCurrentEvent = sizeOfEvents.get(i);
			List<Integer> currentIndices = new ArrayList<Integer>();
			for (int j = 0; j < sizeCurrentEvent; j++) {
				currentIndices.add(lowestNoteIndex + j);
			}
			indicesPerEvent.add(currentIndices);
			lowestNoteIndex += sizeCurrentEvent;
		}
		// Determine for each TS at index i its horizontal and vertical position
		List<String> listOfTabSymbols = loss.get(Encoding.TAB_SYMBOLS_IND);
		for (int i = 0; i < listOfTabSymbols.size(); i++) {
			// 7-8. horizontalPositionOfTabSymbols and verticalPositionOfTabSymbols
			// For each event at index j
			for (int j = 0; j < indicesPerEvent.size(); j++) {
				// For each index k in the event
				List<Integer> indicesCurrentEvent = indicesPerEvent.get(j);
				for (int k = 0; k < indicesCurrentEvent.size(); k++) {
					if (indicesCurrentEvent.get(k) == i) {
						horizontalPositionOfTabSymbols.add(j);
						verticalPositionOfTabSymbols.add(k);
						break;
					}
				}
			}      
			// 9. durationOfTabSymbols
			int eventIndex = horizontalPositionOfTabSymbols.get(i); 
			durationOfTabSymbols.add(durationOfEvents.get(eventIndex));
			// 10. gridXOfTabSymbols
			int gridX = 0;
			int numberOfNonTabSymbolEventsPreceding = 0;
			for (int j = 0; j < eventIndex; j++) {
				gridX += durationOfEvents.get(j);
				if (isTabSymbolEvent.get(j) == 0) {
					numberOfNonTabSymbolEventsPreceding++;
				}
			}
			gridXOfTabSymbols.add(gridX);
			// 11. gridYOfTabSymbols
			TabSymbol currentTabSymbol = 
				TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			gridYOfTabSymbols.add(currentTabSymbol.getPitch(getTunings()[NEW_TUNING_IND], getTuningBassCourses()));
			// 12. horizontalPositionInTabSymbolEventsOnly
			int horizontalPosition = eventIndex - numberOfNonTabSymbolEventsPreceding;
			horizontalPositionInTabSymbolEventsOnly.add(horizontalPosition);
		}
		// Add the lists to listsOfStatistics
		los.add(IS_TAB_SYMBOL_EVENT_IND, isTabSymbolEvent);
		los.add(IS_RHYTHM_SYMBOL_EVENT_IND, isRhythmSymbolEvent);
		los.add(IS_REST_EVENT_IND, isRestEvent);
		los.add(IS_MENSURATION_SIGN_EVENT_IND, isMensurationSignEvent);
		los.add(IS_BARLINE_EVENT_IND, isBarlineEvent);
		los.add(SIZE_OF_EVENTS_IND, sizeOfEvents);
		los.add(DURATION_OF_EVENTS_IND, durationOfEvents);
		los.add(HORIZONTAL_POSITION_IND, horizontalPositionOfTabSymbols);
		los.add(VERTICAL_POSITION_IND, verticalPositionOfTabSymbols);
		los.add(DURATION_IND, durationOfTabSymbols);
		los.add(GRID_X_IND, gridXOfTabSymbols);
		los.add(GRID_Y_IND, gridYOfTabSymbols);
		los.add(HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND, horizontalPositionInTabSymbolEventsOnly);
	
		listsOfStatistics = los;
	}


	/**
	 * Gets listsOfStatistics.
	 */
	// TESTED (together with setListsOfStatistics())
	public List<List<Integer>> getListsOfStatistics() {
		return listsOfStatistics;
	}


	/**
	 * Gets the TuningSeventhCourse required for the Tablature.
	 * @return
	 */
	private TuningBassCourses getTuningBassCourses() {
		for (TuningBassCourses tsc: TuningBassCourses.values()) {
			if (tsc.toString().equals(getInfoAndSettings().get(TUNING_BASS_COURSES_IND))) {
				return tsc;
			}
		}
		return null;
	}


	public void setName(String s) {
		name = s;
	}
	
	
	public String getName() {
		return name;
	}


	/**
	 * Return the TabSymbolSet that was used to encode the Tablature.
	 * 
	 * @return
	 */
	public TabSymbolSet getTabSymbolSet() {
		return TabSymbolSet.getTabSymbolSet(getInfoAndSettings().get(TABSYMBOLSET_IND));
	}


	public static String[] getMetadataTags() {
		return metaDataTags;
	}


	/**
	 * Returns the metadata (author, title, and source information) as stored in
	 * <code>infoAndSettings</code>.
	 * 
	 * @return The metadata, as a <code>List</code> of strings.
	 */
	// TESTED
	public List<String> getMetadata() {
		List<String> ias = getInfoAndSettings();
		List<String> md = new ArrayList<>(); 
		for (int ind : Arrays.asList(new Integer[]{AUTHOR_IND, TITLE_IND, SOURCE_IND})) {
			md.add(ias.get(ind));
		}
		return md;
	}


	/**
	 * Gets the footnotes.
	 * 
	 * @return A <code>List</code> of strings consisting of all footnotes, numbered and 
	 * separated per bar as follows: 
	 * ["bar 1", "(1) Footnote text", "bar 3", "(2) Footnote text", "(3) Footnote text"]
	 */
	// TESTED
	public List<String> getFootnotes() {
		List<String> footnotes = new ArrayList<>();
		List<List<String[]>> ewf = getEventsBarlinesFootnotes();
		// For each system
		for (int i = 0; i < ewf.size(); i++) {
			List<String> footnotesCurrSys = new ArrayList<>();
			// For each event
			for (String[] currEvent : ewf.get(i)) {
				String footnote = currEvent[FOOTNOTE_IND];
				if (footnote != null) {
					String bar = "bar " + currEvent[BAR_IND];
					String footnoteNumStr = currEvent[FOOTNOTE_NUM_IND];
					int footnoteNum = 
						Integer.parseInt(footnoteNumStr.substring(footnoteNumStr.indexOf("#") + 1)); 
					// Add bar (only if it has not been added yet)
					if (!footnotesCurrSys.contains(bar)) {
						footnotesCurrSys.add(bar);
					}
					footnotesCurrSys.add("(" + footnoteNum + ") " + 
						footnote.substring(footnote.indexOf(FOOTNOTE_INDICATOR) + 1));
				}
			}
			footnotes.addAll(footnotesCurrSys);			
		}
		return footnotes;
	}


	/**
	 * Combines any successive rest tabwords in the given list of tabwords.
	 * 
	 * @param tabwords
	 * @return
	 */
	// TESTED
	public static List<String> combineSuccessiveRestTabwords(List<String> tabwords) {
		List<String> res = new ArrayList<>();
		
		List<String> successiveRests = new ArrayList<>();
		for (String t : tabwords) {
			String[] split = t.split("\\" + SymbolDictionary.SYMBOL_SEPARATOR);
			// If rest: add to list and continue for loop
			if (split.length==2 && RhythmSymbol.getRhythmSymbol(split[0]) != null &&
				split[1].equals(ConstantMusicalSymbol.SPACE.getEncoding())) {
				successiveRests.add(split[0]);
			}
			// If not rest
			else {
				// If successive rests still need to be added
				if (!successiveRests.isEmpty()) {
					boolean combinedIsOpen = 
						successiveRests.get(0).contains(RhythmSymbol.tripletOpen);
					boolean combinedIsClose = 
						successiveRests.get(successiveRests.size()-1).contains(RhythmSymbol.tripletClose);
					int totalDur = 0;
					for (String s : successiveRests) {
						totalDur += RhythmSymbol.getRhythmSymbol(s).getDuration();
					}
					RhythmSymbol combinedRs = null;
					for (RhythmSymbol rs : RhythmSymbol.getRhythmSymbols()) {
						// Do not consider coronas
						if (rs.getDuration() == totalDur && 
							!rs.getEncoding().startsWith(RhythmSymbol.coronaBrevis.getEncoding().substring(0, 2))) {
							// In case of triplets beginning/ending: make sure the RS containing 
							// the open/close indicator is chosen
							if (combinedIsOpen || combinedIsClose) {
								String openClose = "";
								if (combinedIsOpen && !rs.getEncoding().contains(RhythmSymbol.tripletOpen)) {
									openClose = RhythmSymbol.tripletOpen;
								}
								else if (combinedIsClose && !rs.getEncoding().contains(RhythmSymbol.tripletClose)){
									openClose = RhythmSymbol.tripletClose;
								}
								combinedRs = RhythmSymbol.getRhythmSymbol(
									RhythmSymbol.tripletIndicator + 
									openClose +
									rs.getEncoding().substring(RhythmSymbol.tripletIndicator.length()));
							}
							else {
								combinedRs = rs;
							}
							break;
						}
					}
					res.add(combinedRs.getEncoding() + SymbolDictionary.SYMBOL_SEPARATOR +
						ConstantMusicalSymbol.SPACE.getEncoding() + SymbolDictionary.SYMBOL_SEPARATOR);
					successiveRests.clear();
				}
				res.add(t);
			}
		}
		return res;
	}


	/**
	 * Checks whether the Encoding contains triplets.
	 * 
	 * Returns <code>true</code> if the Tablature contain triplets, anf <code>false </code> it not.
	 */
	boolean containsTriplets() {
		if (getCleanEncoding().contains(RhythmSymbol.tripletIndicator)) {
			return true;
		}
		else {
			return false;
		}
	}


	/**
	 * Gets all the tabwords. 
	 * 
	 * @return A <code>List<String></code> of all tabwords in the encoding; the SBI are
	 * kept in place (i.e, form separate tabwords). A rhythm symbol (the last active one)
	 * is assigned to each tabword that is lacking one.
	 */
	// TESTED
	// TODOO get this from eventsWithFootnotes
	public List<String> getTabwords() {
		String enc = splitHeaderAndEncoding()[1];

		String[] systems = enc.split(SymbolDictionary.SYSTEM_BREAK_INDICATOR);

		List<String> allTabwords = new ArrayList<>();
		for (int i = 0; i < systems.length; i++) {
			String system = systems[i];
			// List all tabwords and barlines for the current system
			String[] symbols = system.split("\\" + SymbolDictionary.SYMBOL_SEPARATOR);
			List<String> currTabwords = new ArrayList<>();
			String currTabword = "";
			for (int j = 0; j < symbols.length; j++) {
				String s = symbols[j];
				currTabword += s + SymbolDictionary.SYMBOL_SEPARATOR;
				// Add tabword after each space or barline (i.e., CMS)
				if (ConstantMusicalSymbol.constantMusicalSymbols.contains(
					ConstantMusicalSymbol.getConstantMusicalSymbol(s))) {
					// Special case for barline followed by barline (this happens when a 
					// full-bar note is tied at its left (see end quis_me_statim): these two bars
					// must be seen as a single tabword, so the second barlines must be added too
					if (j < symbols.length - 2) { 						
						String nextS = symbols[j+1];
						String nextNextS = symbols[j+2];
						if (ConstantMusicalSymbol.constantMusicalSymbols.contains(
							ConstantMusicalSymbol.getConstantMusicalSymbol(nextS)) &&
							ConstantMusicalSymbol.constantMusicalSymbols.contains(
							ConstantMusicalSymbol.getConstantMusicalSymbol(nextNextS))) {
							currTabword += nextS + SymbolDictionary.SYMBOL_SEPARATOR;
							j++;
						}
					}
					currTabwords.add(currTabword);
					currTabword = "";
				}
			}
			allTabwords.addAll(currTabwords);
			if (i != systems.length-1) {
				allTabwords.add(SymbolDictionary.SYSTEM_BREAK_INDICATOR);
			}
		}

		// Add a RS to each tabword lacking one
		String activeRs = "";
		for (int j = 0; j < allTabwords.size(); j++) {
			String t = allTabwords.get(j);
			if (!t.equals(SymbolDictionary.SYSTEM_BREAK_INDICATOR)) {
				String first = t.substring(0, t.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));
				// RS: set activeRs
				if (RhythmSymbol.getRhythmSymbol(first) != null) {
					activeRs = first;
				}
				// No RS: prepend activeRs to tabword if applicable  
				else {
					// Only if tabword is not a MS or a CMS (barline)
					if (MensurationSign.getMensurationSign(first) == null && 
						ConstantMusicalSymbol.getConstantMusicalSymbol(first) == null) {
						allTabwords.set(j, activeRs + SymbolDictionary.SYMBOL_SEPARATOR + t);
					}
				}
			}
		}
		return allTabwords;
	}


	/**
	 * Reverses the encoding.
	 * 
	 * @param  meterInfo
	 * @return
	 */
	// TESTED
	public Encoding reverseEncoding(List<Integer[]> meterInfo) {
		String header = splitHeaderAndEncoding()[0];

		// 1. Adapt header
		// Reverse meterInfo information 
		int startInd = header.indexOf("METER_INFO:") + "METER_INFO:".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, startInd));
		List<Integer[]> copyOfMeterInfo = new ArrayList<>();
		for (Integer[] in : meterInfo) {
			copyOfMeterInfo.add(Arrays.copyOf(in, in.length));
		}
		Integer[] last = copyOfMeterInfo.get(copyOfMeterInfo.size()-1);
//		int numBars = last[last.length-1];
		int numBars = last[3];
		for (Integer[] in : copyOfMeterInfo) {
			in[2] = (numBars - in[2]) + 1;
			in[3] = (numBars - in[3]) + 1;
		}
		Collections.reverse(copyOfMeterInfo);
		String reversedMeterInfo = "";
		for (int i = 0; i < copyOfMeterInfo.size(); i++) {
			Integer[] in = copyOfMeterInfo.get(i);
			reversedMeterInfo += in[0] + "/" + in[1] + " (";
			if (in[2] == in[3]) {
				reversedMeterInfo += in[3];
			}
			if (in[2] != in[3]) {
				reversedMeterInfo += in[3] + "-" + in[2];
			}
			reversedMeterInfo += ")";
			if (i < copyOfMeterInfo.size()-1) {
				reversedMeterInfo += "; ";
			}
		}
		header = header.replace(origMeterInfo, reversedMeterInfo);
		
		// 2. Reverse encoding and recombine
		List<String> tabwords = getTabwords();
		Collections.reverse(tabwords);
		return new Encoding(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
			SymbolDictionary.END_BREAK_INDICATOR, true);
	}


	/**
	 * Removes all sequences of single-note events shorter than the given duration from the
	 * encoding, and lengthens the duration of the event preceding the sequence by the total 
	 * length of the removed sequence.
	 * 
	 * @param dur In multiples of 
	 * @return
	 */
	// TESTED
	public Encoding deornamentEncoding(int dur) {
		String header = splitHeaderAndEncoding()[0];

		// 1. Adapt tabwords
		List<String> tabwords = getTabwords();
		String pre = null;
		int durPre = -1;
		int indPre = -1;
		List<Integer> removed = new ArrayList<>();
		int i2 = 0;
		for (int i = 0; i < tabwords.size(); i++) {
			String t = tabwords.get(i);
			// If t is not a barline or a SBI
			if (!tabwordIsBarlineOrSBI(t)) {
				String[] symbols = t.split("\\" + SymbolDictionary.SYMBOL_SEPARATOR);
				RhythmSymbol r = RhythmSymbol.getRhythmSymbol(symbols[0]);
				// If the tabword is an ornamentation (which always consists of only a RS, a TS,
				// and a space)
				if (r != null && r.getDuration() < dur && symbols.length == 3) {
					removed.add(i2);
					// Determine pre, if it has not yet been determined
					if (pre == null) {
						for (int j = i-1; j >= 0; j--) {
							String tPrev = tabwords.get(j);
							// If tPrev is not a barline or SBI
							if (!tabwordIsBarlineOrSBI(tPrev) ) {
								pre = tPrev;
								durPre = RhythmSymbol.getRhythmSymbol(tPrev.substring(0, 
									tPrev.indexOf(SymbolDictionary.SYMBOL_SEPARATOR))).getDuration();
								indPre = j;
								break;
							}
						}
					}
					// Increment durPre and set tabword to null
					durPre += r.getDuration();
					tabwords.set(i, null);
				}
				// If the tabword is the first after a sequence of one or more ornamental
				// notes (i.e., it does not meet the if conditions above but pre != null)
				else if (pre != null) {
					// Determine the new Rs for pre, and adapt and set it
					String newRs = "";
					for (RhythmSymbol rs : RhythmSymbol.getRhythmSymbols()) {
						if (rs.getDuration() == durPre) {
							newRs = rs.getEncoding();
							break;
						}
					}
					tabwords.set(indPre, newRs + 
						pre.substring(pre.indexOf(SymbolDictionary.SYMBOL_SEPARATOR), pre.length()));
					// Reset
					pre = null;
					indPre = -1;
				}
				if (symbols.length != 2) { // Do not consider rests
					i2++;
				}
			}
		}
		tabwords.removeIf(t -> t == null);

		// 2. Recombine
		return new Encoding(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
			SymbolDictionary.END_BREAK_INDICATOR, true);
	}


	/**
	 * Stretches the encoding durationally by the given factor.
	 * 
	 * @param meterInfo
	 * @param factor
	 * @return
	 */
	// TESTED
	public Encoding stretchEncoding(List<Integer[]> meterInfo, double factor) {
		String header = splitHeaderAndEncoding()[0];

		// 1. Adapt header
		// Reverse meterInfo information 
		int startInd = header.indexOf("METER_INFO:") + "METER_INFO:".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, startInd));
		List<Integer[]> copyOfMeterInfo = new ArrayList<>();
		String stretchedMeterInfo = "";
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] in = meterInfo.get(i);
			if (i > 0) {
				in[2] = meterInfo.get(i-1)[3] + 1;
			}
			in[3] = (int) (in[3] * factor);
			stretchedMeterInfo += in[0] + "/" + in[1] + " (" + in[2] + "-" + in[3] + ")";
			if (i < copyOfMeterInfo.size()-1) {
				stretchedMeterInfo += "; ";
			}
		}
		header = header.replace(origMeterInfo, stretchedMeterInfo);
		
		// 2. Adapt tabwords
		List<String> tabwords = getTabwords();
		for (int i = 0; i < tabwords.size(); i++) {
			String t = tabwords.get(i);
			// If t is not a barline or a SBI
			if (!tabwordIsBarlineOrSBI(t)) {
				String[] symbols = t.split("\\" + SymbolDictionary.SYMBOL_SEPARATOR);
				RhythmSymbol r = RhythmSymbol.getRhythmSymbol(symbols[0]);
				String newRs = "";
				if (r != null) {
					for (RhythmSymbol rs : RhythmSymbol.getRhythmSymbols()) {
						if (rs.getDuration() == r.getDuration() * factor) {
							newRs = rs.getEncoding();
							break;
						}
					}
					tabwords.set(i, 
						newRs + t.substring(t.indexOf(SymbolDictionary.SYMBOL_SEPARATOR), t.length()));
				}
			}
		}

		// 3. Recombine
		return new Encoding(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
			SymbolDictionary.END_BREAK_INDICATOR, true);
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
	// TESTED
	String[] splitHeaderAndEncoding() {
		// Separate header and encoding
		String raw = getRawEncoding();
		String[] metadataTags = Encoding.getMetadataTags();
		int endHeader = raw.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, 
			raw.indexOf(metadataTags[metadataTags.length-1]));		
		String header = raw.substring(0, endHeader+1).trim();
		String enc = raw.substring(endHeader+1, raw.length()).replace("\r\n", "").trim();
		
		// Remove comments and EBI from encoding
		while (enc.contains(SymbolDictionary.OPEN_INFO_BRACKET)) {
			int openCommentIndex = enc.indexOf(SymbolDictionary.OPEN_INFO_BRACKET);
			int closeCommentIndex = 
				enc.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, openCommentIndex);
			String comment = enc.substring(openCommentIndex, closeCommentIndex + 1);
				enc = enc.replace(comment, "");
		}
		enc = enc.substring(0, enc.indexOf(SymbolDictionary.END_BREAK_INDICATOR));
		
		return new String[]{header, enc};
	}


	/**
	 * Recombines the given list of tabwords into a String, adding a line break after each 
	 * constant music symbol (space or barline), as well as after each system break indicator.
	 * 
	 * @param tabwords
	 * @return
	 */ 
	// TODO test
	private String recombineTabwords(List<String> tabwords) {
		String recombined = "";
		for (String s : tabwords) {
			recombined += s;
			if (!s.equals(SymbolDictionary.SYSTEM_BREAK_INDICATOR)) {
				String first = s.substring(0, s.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));
				// Add a line break after each CMS (space, barline) 
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(first) != null) {
					recombined += "\r\n";
				}
			}
			// Add a line break after each SBI
			else {
				recombined += "\r\n";
			}
		}		
		return recombined;
	}


	// TODO test
	private boolean tabwordIsBarlineOrSBI(String tabword) {
		if (tabword.equals(SymbolDictionary.SYSTEM_BREAK_INDICATOR)) {
			return true;
		}
		else {
			String first = tabword.substring(0, 
				tabword.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));
			if (ConstantMusicalSymbol.constantMusicalSymbols.contains(
				ConstantMusicalSymbol.getConstantMusicalSymbol(first))) {
				return true;
			}
			else {
				return false;
			}
		}
	}


	/**
	 * Gets, per system, the segment indices in the tbp Staff of the events of the given
	 * type.
	 * 
	 * @param type The type of event: "footnote" or "barline".
	 *  
	 * @return A <code>List</code> of <code>List</code>s, each of which represents a system, 
	 * and contains the segment indices in the tbp Staff of the events of the given 
	 * type. NB:
	 * <ul>
	 * <li>In case of a system without any events of the given type, the <code>List</code>
	 *     remains empty.</li>
	 * <li>In case of a barline that spans multiple segments, the index of the first 
	 *     segment is given.</li>
	 * </ul>
	 */
	// TESTED
	List<List<Integer>> getStaffSegmentIndices(String type) {
		List<List<Integer>> segmentIndices = new ArrayList<>();

		// For each system
		for (List<String[]> system : getEventsBarlinesFootnotes()) {
			int currSegmentInd = 0;
			List<Integer> currSegmentIndices = new ArrayList<>();
			// For each event in the system
			for (String[] event : system) {
				String currEvent = event[EVENT_IND].substring(0, 
					event[EVENT_IND].lastIndexOf(SymbolDictionary.SYMBOL_SEPARATOR));
				boolean isBarlineEvent = 
					ConstantMusicalSymbol.isBarline(currEvent) ? true : false;
				if (type.equals("footnote")) {
					// If the event contains a footnote: add currSegmentInd
					if (event[FOOTNOTE_IND] != null) {
						// In case of a barline, the footnote indicator is added below 
						// the first pipe char (and not below any repeat dots), so 
						// currSegmentInd must be incremented with the index in currEvent
						// of that pipe char
						if (isBarlineEvent) {
							currSegmentInd += 
								currEvent.indexOf(ConstantMusicalSymbol.BARLINE.getEncoding());
						}
						currSegmentIndices.add(currSegmentInd);
					}
				}
				else if (type.equals("barline")) {
					// If the event contains a barline: add currSegmentIn
					if (isBarlineEvent) {
						// The bar number is added above the first pipe char (and not
						// below any repeat dots), so currSegmentInd must be incremented 
						// with the index in currEvent of that pipe char
						currSegmentInd += 
							currEvent.indexOf(ConstantMusicalSymbol.BARLINE.getEncoding());
						currSegmentIndices.add(currSegmentInd);
					}
				}

				// Increment currSegmentInd. If barline event: increment with the 
				// number of chars in the barline; if not (so if TS, RS, rest, or 
				// MS event): increment with 2: one segment for the event itself,
				// and one for the space following it
				currSegmentInd = 
					isBarlineEvent ? currSegmentInd + currEvent.length() : 
					currSegmentInd + 2;
			}
			segmentIndices.add(currSegmentIndices);
		}
		return segmentIndices;
	}


	/** 
	 *  Determines the staff length by calculating the number of segments needed for the longest
	 *  system. The number of segments needed for a system can be calculated by looking at the 
	 *  CMS only, as it equals the sum of (i) twice the system's number of spaces (each space 
	 *  is preceded by an event, and both the event and the space need one segment); and (ii) 
	 *  the total length of all the system's barlines.
	 *  
	 *  @return The lenght of the staff, measured in staff segments.
	 **/
	// TESTED
	public int getStaffLength() {
		int largestStaffLength = 0;

		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String ebi = SymbolDictionary.END_BREAK_INDICATOR;
		
		String cleanEncoding = getCleanEncoding();
		String[] allSystems = cleanEncoding.substring(0, cleanEncoding.indexOf(ebi)).split(sbi);

		// For each system
		for (String system : allSystems) {
			int lengthCurrSystem = 0;
			int ssIndex = -1;
			int nextSsIndex = system.indexOf(ss, ssIndex + 1);
			// For each symbol
			while (nextSsIndex != -1) {
				String symbol = system.substring(ssIndex + 1, nextSsIndex);
				// If symbol is a CMS       
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(symbol) != null) { 
					// a. If symbol is a space, lengthCurrSystem must be incremented by 2: 
					// one for the space and one for the event before it
					if (symbol.equals(sp)) {
						lengthCurrSystem += 2;
					}
					// b. If symbol is any CMS but a space, lengthCurrSystem must be 
					// incremented by the length of the symbol 
					else {
						lengthCurrSystem += symbol.length();
					}
				}
				ssIndex = nextSsIndex;
				nextSsIndex = system.indexOf(ss, ssIndex + 1);
			}
			// Reset largestStaffLength if necessary
			if (lengthCurrSystem > largestStaffLength) {
				largestStaffLength = lengthCurrSystem;
			}
		}
		return largestStaffLength;
	}
	
	
	List<List<Integer>> getBarNumbers() {
		return null;
	}


	/**
	 * Get the bar number of bar with which each system starts (which may be an 
	 * incomplete bar continued from the previous system).
	 *  
	 * @param barlineSegmentInds
	 * @return 
	 */
	// TESTED
	static List<Integer> getFirstBarNumber(List<List<Integer>> barlineSegmentInds) {
		List<Integer> firstBarNumbers = new ArrayList<>();
		// NB: the below works both for systems starting with a complete bar
		//                       [5]       [6]
		// ... | ... | ... | ... | ... | / ... | ... | etc.
		// 
		// and for systems starting with an incomplete bar
		//                       [5]         [6]          
		// ... | ... | ... | ... | ... / ... | ... | etc.
		int firstBar = 1;
		firstBarNumbers.add(firstBar);
		for (int i = 0; i < barlineSegmentInds.size(); i++) {
			int lastBarCurrStaff = 0;
			for (int ind : barlineSegmentInds.get(i)) {
				// Ignore decorative opening barlines
				if (ind != 0) {
					lastBarCurrStaff++;
				}
			}
			firstBar += lastBarCurrStaff;
			// Do not add for non-existing system after last system
			if (i < barlineSegmentInds.size() -1) {
				firstBarNumbers.add(firstBar);
			}
		}
		return firstBarNumbers;
	}


	/**
	 * Renders the encoding as String.
	 * 
	 * @param TabSymbolSet Determines the tablature style.
	 * @param ignoreRepeatedRhythmSymbols If set to <code>true</code>, RS will only be 
	 * displayed when they change - regardless of whether this is specified in the encoding.
	 * 
	 * @return A String representation of the encoding.
	 */
	public String visualise(TabSymbolSet argTss, boolean ignoreRepeatedRhythmSymbols,
		boolean showHeader, boolean showFootnotes) {
		String tab = "";
		
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;

		String cleanEnc = getCleanEncoding();
		TabSymbolSet tss = getTabSymbolSet();

		List<List<Integer>> barlineSegmentInds = getStaffSegmentIndices("barline");
		List<List<Integer>> footnoteSegmentInds = getStaffSegmentIndices("footnote");
		
		// Search all systems one by one
		int staffIndex = 0;
		int sbiIndex = -1;
		int nextSbiIndex = cleanEnc.indexOf(sbi, sbiIndex + 1);
		int firstBar = 1;
		while (sbiIndex + 1 != nextSbiIndex) { 
			RhythmSymbol prevRhythmSymbol = null;
			Staff staff = new Staff(getStaffLength());
			int segment = 0;
			String currSysEncoding = cleanEnc.substring(sbiIndex + 1, nextSbiIndex);
			// Check for each system the encoded symbols one by one and for each encoded symbol 
			// add its tablature representation to staff 
			int ssIndex = -1;
			int nextSsIndex = currSysEncoding.indexOf(ss, ssIndex);
			String lastEncodedSymbol = null;
			boolean startsWithUnfinishedBar = false;
			while (nextSsIndex != -1) {
				String encodedSymbol = currSysEncoding.substring(ssIndex + 1, nextSsIndex);
				int nextNextSsIndex = currSysEncoding.indexOf(ss, nextSsIndex + 1);
				// nextEncodedSymbol is needed for b, c, and d below and can exist for all encoded 
				// symbols except for the last--i.e., as long as nextNextSsIndex is not -1
				String nextEncodedSymbol = null;
				if (nextNextSsIndex != -1) {
					nextEncodedSymbol = currSysEncoding.substring(nextSsIndex + 1, nextNextSsIndex);
				}
				// a. Add ConstantMusicalSymbol?
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(encodedSymbol) != null) {
					ConstantMusicalSymbol c = ConstantMusicalSymbol.getConstantMusicalSymbol(encodedSymbol);
					staff.addConstantMusicalSymbol(encodedSymbol, segment);
					segment = segment + c.getSymbol().length();
				}
				// b. Add TabSymbol?
				else if (TabSymbol.getTabSymbol(encodedSymbol, tss) != null) { 
					TabSymbol t = TabSymbol.getTabSymbol(encodedSymbol, tss);
					if (argTss == TabSymbolSet.FRENCH_TAB) {   
						staff.addTabSymbolFrench(t, segment); 
					}
					else if (argTss == TabSymbolSet.ITALIAN_TAB) {
						staff.addTabSymbolItalian(t, segment);
					}
					else if (argTss == TabSymbolSet.SPANISH_TAB) {
						staff.addTabSymbolSpanish(t, segment);
					}
					else if (argTss == TabSymbolSet.NEWSIDLER_1536 ) {
						// TODO 
					} 
					// Is encodedSymbol followed by a space and not by another TS--i.e., is it the 
					// last TS of a vertical sonority? Increment segment
					// NB: LAYOUT RULE 4 guarantees that a vertical sonority is always followed by a
					// space, meaning that nextEncodedSymbol always exists if encodedSymbol is a TS
					if (nextEncodedSymbol.equals(sp)) {
						segment++;
					}
				}
				// c. Add RhythmSymbol?
				else if (RhythmSymbol.getRhythmSymbol(encodedSymbol) != null) {
					RhythmSymbol r = RhythmSymbol.getRhythmSymbol(encodedSymbol);
					boolean showBeam = true;
					// Always RS? Add RS; always add any beam
					if (!ignoreRepeatedRhythmSymbols) {
						staff.addRhythmSymbol(r, segment, showBeam);    
					}
					// Only differing RS? Add RS only if r is not equal to previousRhythmSymbol; 
					// never add any beam
					else {
						// Compare r with prevRhythmSymbol; if prevRhythmSymbol is null or if they
						// do not have the same duration: add r to staff
						// NB: because of possibly present beams, direct comparison does not work: an RS and 
						// its beamed variant are considered inequal because they are defined as two different 
						// objects
						showBeam = false;
						if (prevRhythmSymbol == null) {
							staff.addRhythmSymbol(r, segment, showBeam);
						}
						else {
							if (r.getDuration() != prevRhythmSymbol.getDuration()) {
								staff.addRhythmSymbol(r, segment, showBeam);
							}
						}
					}
					// Is encodedSymbol followed by a space and not by a TS -- i.e., does 
					// encodedSymbol represent a rest? Increment segment
					// NB: LAYOUT RULE 5 guarantees that a rest is always followed by a space, 
					// meaning that nextEncodedSymbol always exists if encodedSymbol is a RS
					if (nextEncodedSymbol.equals(sp)) {
						segment ++;
					}
					prevRhythmSymbol = r;
				}     
				// d. Add MensurationSign?
				else if (MensurationSign.getMensurationSign(encodedSymbol) != null) {
					MensurationSign m = MensurationSign.getMensurationSign(encodedSymbol);
					staff.addMensurationSign(m, segment);
					// Is encodedSymbol followed by a space and not by another MS--i.e., is
					// encodedSymbol the only or the last symbol of a (compound) MS? Increment segment
					// NB: LAYOUT RULE 6 guarantees that the last MS is always followed by a space,
					// meaning that nextEncodedSymbol always exists if encodedSymbol is a MS 
					if (nextEncodedSymbol.equals(sp)) {
						segment ++;
					}
				}
//				// e. Add footnote
//				staff.addFootnoteIndicators(getStaffSegmentIndices("footnote").get(staffIndex));
//				// f. Add bar numbers
//				System.out.println("staff = " + (staffIndex + 1));
//				System.out.println(getStaffSegmentIndices("barline"));
//				staff.addBarNumbers(getStaffSegmentIndices("barline").get(staffIndex));
				// Prepare indices for next iteration inner while
				ssIndex = nextSsIndex;
				nextSsIndex = currSysEncoding.indexOf(ss, ssIndex + 1);
				lastEncodedSymbol = encodedSymbol;
			}
			boolean endsWithBarline = ConstantMusicalSymbol.isBarline(lastEncodedSymbol);
			// e. Add footnote
			staff.addFootnoteIndicators(footnoteSegmentInds.get(staffIndex));
			// f. Add bar numbers
			staff.addBarNumbers(barlineSegmentInds.get(staffIndex), firstBar, 
				startsWithUnfinishedBar, endsWithBarline);
						
			// System traversed? Add to tab and update information for the next system
			tab += staff.getStaff() + Staff.SPACE_BETWEEN_STAFFS;
			startsWithUnfinishedBar = endsWithBarline ? false : true;
			if (staffIndex < barlineSegmentInds.size() -1) {
				firstBar = getFirstBarNumber(barlineSegmentInds).get(staffIndex+1);
			}
			staffIndex++;
			sbiIndex = nextSbiIndex;
			nextSbiIndex = cleanEnc.indexOf(sbi, sbiIndex + 1);
		}
		return tab;
	}

}
