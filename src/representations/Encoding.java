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
	private boolean hasMetadataErrors;
	private List<String> infoAndSettings;
	private List<String> footnotes;
	private List<String[]> eventsList; 
	public static final int AUTHOR_INDEX = 0;
	public static final int TITLE_INDEX = 1;
	public static final int SOURCE_INDEX = 2;
	static final int TABSYMBOLSET_INDEX = 3;
	private static final int TUNING_INDEX = 4;
	private static final int TUNING_BASS_COURSES_INDEX = 5;
	public static final int METER_INDEX = 6;
	public static final int DIMINUTION_INDEX = 7;
//	private static final String DUR_SCALE = "DUR_SCALE";
		
	private List<List<String>> listsOfSymbols;
	private static final int ALL_SYMBOLS_INDEX = 0;
	public static final int TAB_SYMBOLS_INDEX = 1;
	private static final int RHYTHM_SYMBOLS_INDEX = 2;
	private static final int MENSURATION_SIGNS_INDEX = 3;
	private static final int BARLINES_INDEX = 4;
	public static final int ALL_EVENTS_INDEX = 5;
	
	private List<List<Integer>> listsOfStatistics;
	public static final int IS_TAB_SYMBOL_EVENT_INDEX = 0;
	private static final int IS_RHYTHM_SYMBOL_EVENT_INDEX = 1;
	public static final int IS_REST_EVENT_INDEX = 2;
	private static final int IS_MENSURATION_SIGN_EVENT_INDEX = 3;
	private static final int IS_BARLINE_EVENT_INDEX = 4;
	public static final int SIZE_OF_EVENTS_INDEX = 5;
	private static final int DURATION_OF_EVENTS_INDEX = 6;  
	public static final int HORIZONTAL_POSITION_INDEX = 7;
	public static final int VERTICAL_POSITION_INDEX = 8;
	public static final int DURATION_INDEX = 9;
	public static final int GRID_X_INDEX = 10;
	public static final int GRID_Y_INDEX = 11;
	public static final int HORIZONTAL_POSITION_TAB_SYMBOLS_ONLY_INDEX = 12;
	public static final String METADATA_ERROR = "METADATA ERROR -- Check for missing curly brackets.";

	private Tuning[] tunings;
	public static final int ENCODED_TUNING_INDEX = 0;
	public static final int NEW_TUNING_INDEX = 1;
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
	
	public static String[] metadataTags = new String[]{
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
			setHasMetadataErrors(); // needs rawEncoding
			if (getHasMetadataErrors() == true) {
				return;
			}
			setEventsLists(); // needs rawEncoding 
			setCleanEncoding(); // needs rawEncoding 
			setInfoAndSettings(); // needs rawEncoding 
			setFootnotes(); // needs rawEncoding
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
		setHasMetadataErrors(); // needs rawEncoding
		if (getHasMetadataErrors() == true) {
			throw new RuntimeException(METADATA_ERROR);
		}
		setEventsLists(); // needs rawEncoding
		setCleanEncoding(); // needs rawEncoding 
		setInfoAndSettings(); // needs rawEncoding 
		setFootnotes(); // needs rawEncoding
		if (checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
			throw new RuntimeException("ERROR: The encoding contains encoding errors; run the TabViewer to correct them.");
		}
		setTunings();
		setListsOfSymbols();
		setListsOfStatistics();
	}


	private void setHasMetadataErrors() {
		hasMetadataErrors = (checkForMetadataErrors() == true) ? true : false;
	}


	public boolean getHasMetadataErrors() {
		return hasMetadataErrors;
	}
	
	
	public static String[] getMetadataTags() {
		return metadataTags;
	}


	/**
	 * Given the clean encoding, calculates the meter info string. Barring is ignored, and bar each
	 * bar end is instead determined by the bar duration under the current mensuration sign. It is 
	 * assumed that the piece contains no errors. 
	 * 
	 * @param argCleanEncoding
	 * @param tss
	 * @return
	 */
	// TESTED
	public static String createMeterInfoString(String argCleanEncoding, String tss) {

		// Remove line breaks and system separators; then split into symbols
		String[] symbols = argCleanEncoding.replace("\r\n", "").replace("/", "").split("\\"+SymbolDictionary.SYMBOL_SEPARATOR);
		// Remove any initial barline
		if (ConstantMusicalSymbol.getConstantMusicalSymbol(symbols[0]) != null) {
			symbols = Arrays.copyOfRange(symbols, 1, symbols.length);
		}

		List<Integer[]> mensSigns = new ArrayList<>();
		mensSigns.add(new Integer[]{2, 2}); // assume 2/2 in case there is no MS
		List<String> barsPerMeter = new ArrayList<>();
		int meterStartBar = 1;
		int currBar = 1;
		int prevDur = 0;
		int posInBar = 0;
		// fullBar is the length (in SMALLEST_RHYTHMIC_VALUE) of a full bar under the current meter
		int fullBar = 
			(int) (mensSigns.get(0)[0] / (double) mensSigns.get(0)[1]) * 
			Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom(); // trp
		boolean semibreveBarring = false;
		for (int i = 0; i < symbols.length; i++) {
			String s = symbols[i];
//			System.out.println("symbol = " + s);
			MensurationSign ms = MensurationSign.getMensurationSign(s);
			RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(s);
			TabSymbol ts = TabSymbol.getTabSymbol(s, TabSymbolSet.getTabSymbolSet(tss));
			ConstantMusicalSymbol cms = ConstantMusicalSymbol.getConstantMusicalSymbol(s);
			ConstantMusicalSymbol prevCms = null;
			if (i > 0) {
				prevCms = ConstantMusicalSymbol.getConstantMusicalSymbol(symbols[i-1]);
			}

			// MS
			if (ms != null) {
				Integer[] meter = ms.getMeter();
				// In case of double (ternary) MS: get the meter from the second MS
				MensurationSign nextMS = MensurationSign.getMensurationSign(symbols[i+1]);
				if (nextMS != null) {
					meter = nextMS.getMeter();
					i++; // skip next symbol
				}
				// If still bar 1: replace default MS; else add MS
				if (currBar == 1) {
					mensSigns.set(0, meter);
				}
				else {
					mensSigns.add(meter);
					// Add bars under previous MS
					if (meterStartBar == currBar-1) {
						barsPerMeter.add(String.valueOf(meterStartBar));
					}
					else {
						barsPerMeter.add(meterStartBar + "-" + (currBar-1));
					}
					meterStartBar = currBar;
				}
				// Set fullBar under new meter
				fullBar = 
					(int) ((meter[0] / (double) meter[1]) * 
					Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()); // trp
			}

			// RS
			if (rs != null) {
				// Only if previous symbol was no barline and the RS event is not preceded by a MS 
				// (in both cases posInBar was already updated)
				if (!(prevCms != null && prevCms != ConstantMusicalSymbol.SPACE) && 
					(i >= 2 && MensurationSign.getMensurationSign(symbols[i-2]) == null)) {
					posInBar += prevDur;
//					System.out.println("RS");
//					System.out.println("posInBar = " + posInBar);
				}
				prevDur = rs.getDuration();
			}

			// TS event without RS (always preceded by space or barline)
			if (ts != null && prevCms != null) {
				// Only if previous symbol was no barline (in which case posInBar was already updated)
				if (!(prevCms != ConstantMusicalSymbol.SPACE)) {
					posInBar += prevDur;
//					System.out.println("TS event w/o RS");
//					System.out.println("posInBar = " + posInBar);
				}
			}
			
			// Barline
			if (cms != null && cms != ConstantMusicalSymbol.SPACE) {
				// Only if previous symbol was no barline (in which case posInBar was already updated)
				if (!(prevCms != null && prevCms != ConstantMusicalSymbol.SPACE)) {
					posInBar += prevDur;
//					System.out.println("barline");
//					System.out.println("posInBar = " + posInBar);
				}
				// Check for semibreve barring (assumed to be regular)
				if (currBar == 1 && posInBar >= RhythmSymbol.semibrevis.getDuration()) {
					semibreveBarring = true;
				}
			}

//			// Increment bar and reset posInBar
//			// Semibreve barring: bar end reached only if bar is full
//			// No semibreve barring: bar end reached if bar is full or barline is encountered
//			if ( (semibreveBarring && posInBar >= fullBar) || (!semibreveBarring && 
//				(posInBar >= fullBar || (cms != null && cms != ConstantMusicalSymbol.SPACE)))) {
//				currBar++;
//				// Account for ties over bar
//				posInBar -= fullBar;
//			}
			
			// Increment bar and reset posInBar
			if (posInBar >= fullBar) {
				currBar++;
//				System.out.println("currBar = " + currBar);
				// Account for ties over bar
				posInBar -= fullBar;
			}
		}
		if (meterStartBar == currBar-1) {
			barsPerMeter.add(String.valueOf(meterStartBar));
		}
		else {
			barsPerMeter.add(meterStartBar + "-" + (currBar-1));
		}
		
		String meterInfoString = "";
		for (int i = 0; i < mensSigns.size(); i++) {
			meterInfoString += mensSigns.get(i)[0] + "/" + mensSigns.get(i)[1] + " (" +
			barsPerMeter.get(i) + ")";
			if (i < mensSigns.size() - 1) {
				meterInfoString += "; ";
			}
		}
		
		return meterInfoString;
	}


	// TESTED (together with getRawEncoding())
	void setRawEncoding(String aString) {
		this.rawEncoding = aString.trim();
	}


	// TESTED (together with setRawEncoding())
	public String getRawEncoding() {
		return rawEncoding;
	}


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
	boolean checkForMetadataErrors() {		
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
	 * Sets <code>eventsList</code>, which contains all events in the piece. Each event is a 
	 * String[] consisting of three elements:
	 * <ul>
	 * <li>at element 0: the event as encoded</li>
	 * <li>at element 1: if the event has an editorial comment, that comment; otherwise 
	 * <code>null</code></li>
	 * <li>at element 2: if the event has an editorial comment, the system the event is in; 
	 * otherwise <code>null</code></li>
	 * </ul>
	 */
	// TESTED (together with getEventsLists())
	void setEventsLists() {
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String oib = SymbolDictionary.OPEN_INFO_BRACKET;
		String cib = SymbolDictionary.CLOSE_INFO_BRACKET;
		String sp = ConstantMusicalSymbol.SPACE.getEncoding();
		String sbi = SymbolDictionary.SYSTEM_BREAK_INDICATOR;
		String invertedSp = "<";

		String rawEnc = getRawEncoding();
		// Remove all carriage returns and line breaks; remove leading and trailing whitespace
		rawEnc = rawEnc.replaceAll("\r", "");
		rawEnc = rawEnc.replaceAll("\n", "");
		rawEnc = rawEnc.trim();
		// Remove end break indicator
		rawEnc = rawEnc.replaceAll(SymbolDictionary.END_BREAK_INDICATOR, "");
		
		// List all comments
		List<String> allNonEditorialComments = new ArrayList<>();
		List<String[]> allEditorialComments = new ArrayList<>();
		int sys = 1;
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
				allEditorialComments.add(new String[]{
				comment.substring(comment.indexOf(oib)+1, comment.indexOf(cib)), 
				"system " + sys});
				// In rawEnc, temporarily replace any spaces within comments, so that 
				// splitting on them (see below) remains possible
				if (comment.contains(sp)) {
					rawEnc = rawEnc.replace(comment, comment.replace(sp, invertedSp));
				}
			}
			if (commCloseInd == rawEnc.lastIndexOf(cib)) {
				break;
			}
			else {
				i = commCloseInd;
				// If there is a SBI before the next comment: increase system
				// NB: Does not apply for the last system, where sbiInd will be -1   
				int sbiInd = rawEnc.indexOf(sbi, i);
				if (sbiInd != -1 && sbiInd < rawEnc.indexOf(oib, i)) {
					sys++; 
				}
			}
		}

		// Remove all non-editorial comments from rawEnc
		for (String comment : allNonEditorialComments) {
			rawEnc = rawEnc.replace(comment, "");
		}

		// Remove all barlines. NB: This will also remove any barlines in comments (but
		// only if they are followed by a symbol separator!) - which is not a problem as 
		// the unadapted comments are stored in allNonEditorialComments		
		List<String> barlinesAsString = new ArrayList<>();
		for (ConstantMusicalSymbol cms : ConstantMusicalSymbol.constantMusicalSymbols) {
			if (cms != ConstantMusicalSymbol.SPACE) {
				barlinesAsString.add(cms.getEncoding());
			}
		}
		// Sort the barlines by length (longest first), so that they are removed correctly
		// (a shorter barline, e.g., :|, can be part of a longer one, e.g., :|:, leading to 
		// partial removal of the longer one) 
		// See https://stackoverflow.com/questions/29280257/how-to-sort-an-arraylist-by-its-elements-size-in-java
		barlinesAsString.sort(Comparator.comparing(String::length).reversed());
		for (String s : barlinesAsString) {
			if (ConstantMusicalSymbol.isBarline(s)) {
				if (rawEnc.contains(s)) {
					rawEnc = rawEnc.replace(s + ss, "");
				}
			}
		}

		// Remove all SBIs. NB: This will also remove any SBI in comments - which is not a 
		// problem as the unadapted comments are stored in allNonEditorialComments
		rawEnc = rawEnc.replace(sbi, "");

		// Split per event and make argEventsList
		List<String[]> argEventsList = new ArrayList<>();
		int commentCounter = 0;
		for (String event : rawEnc.split(sp + ss)) {
			// If the event contains a comment
//			String comment = null;
			String[] editedComment = new String[]{null, null};
			// NB: a comment on an event will follow that event; a comment on a barline will
			// precede the following event (because barlines are not followed by a space)
			if (event.contains(oib + FOOTNOTE_INDICATOR)) {
//				comment = 
//					event.substring(event.indexOf(FOOTNOTE_INDICATOR) + 1, event.indexOf(cib));
				event = 
					event.substring(0, event.indexOf(oib)) + event.substring(event.indexOf(cib) + 1);
				// Find the (unadapted) comment, including system information, in allEditorialComments
				editedComment = allEditorialComments.get(commentCounter);
				commentCounter++;
			}
			argEventsList.add(new String[]{event, editedComment[0], editedComment[1]});
		}
		eventsList = argEventsList;
	}


	public List<String[]> getEventsList() {
		return eventsList;
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
	 * Sets infoAndSettings, a String[] containing:
	 *   at element 0: the author
	 *   at element 1: the title
	 *   at element 2: the source
	 *   at element 3: the TabSymbolSet used for the encoding
	 *   at element 4: the tuning
	 *   at element 5: the TuningSeventhCourse (if any)
	 *   at element 6: the meter information
	 *   at element 7: the diminution
	 */
	// TESTED (together with getInfoAndSettings())
	void setInfoAndSettings() {
		List<String> ias = new ArrayList<>(); 
		List<String> metaData = getMetaData();
		ias.add(AUTHOR_INDEX, metaData.get(0).substring(metaData.get(0).indexOf(":") + 1).trim());
		ias.add(TITLE_INDEX, metaData.get(1).substring(metaData.get(1).indexOf(":" ) + 1).trim());
		ias.add(SOURCE_INDEX, metaData.get(2).substring(metaData.get(2).indexOf(":") + 1).trim());
		ias.add(TABSYMBOLSET_INDEX, metaData.get(3).substring(metaData.get(3).indexOf(":") + 1).trim());
		ias.add(TUNING_INDEX, metaData.get(4).substring(metaData.get(4).indexOf(":") + 1).trim());
		ias.add(TUNING_BASS_COURSES_INDEX, metaData.get(5).substring(metaData.get(5).indexOf(":") + 1).trim());
		ias.add(METER_INDEX, metaData.get(6).substring(metaData.get(6).indexOf(":") + 1).trim());
		ias.add(DIMINUTION_INDEX, metaData.get(7).substring(metaData.get(7).indexOf(":") + 1).trim());
		infoAndSettings = ias;
	}


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
	List<String> getMetaData() {
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


	/**
	 * Sets footnotes, a List<String> containing all the footnotes added to the encoding.
	 */
	// TESTED (together with getFootnotes())
	void setFootnotes() {
		List<String> fn = new ArrayList<>();
		int footNoteCounter = 1;
		for (String item : getMetaData()) {
			if (item.startsWith(FOOTNOTE_INDICATOR)) {
				fn.add("(" + footNoteCounter + ") " + item.substring(1));
				footNoteCounter++;
			}
		}
		footnotes = fn;
	}


	// TESTED (together with setFootnotes())
	public List<String> getFootnotes() {
		return footnotes;
	}


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
	 * Return the TabSymbolSet that was used to encode the Tablature.
	 * 
	 * @return
	 */
	public TabSymbolSet getTabSymbolSet() {
//		return TabSymbolSet.getTabSymbolSet(infoAndSettings.get(TABSYMBOLSET_INDEX));
		return TabSymbolSet.getTabSymbolSet(getInfoAndSettings().get(TABSYMBOLSET_INDEX));
	}


	public void setName(String s) {
		name = s;
	}
	
	
	public String getName() {
		return name;
	}


	/**
	 *  Sets tuning and encodedTuning both with the tuning specified in the encoding. tuning may
	 *  be changed during further processing; encodedTuning retains its initial value.
	 */
	// TESTED (together with getTunings());
	void setTunings() {
		Tuning[] tun = new Tuning[2];
		for (Tuning t : Tuning.values()) { 
			if (t.toString().equals(getInfoAndSettings().get(TUNING_INDEX))) {
				tun[ENCODED_TUNING_INDEX] = t;
				tun[NEW_TUNING_INDEX] = t; 
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
	// TESTED (together with getListsOfSymbols)
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
		los.add(ALL_SYMBOLS_INDEX, listOfAllSymbols);
		los.add(TAB_SYMBOLS_INDEX, listOfTabSymbols);
		los.add(RHYTHM_SYMBOLS_INDEX, listOfRhythmSymbols);
		los.add(MENSURATION_SIGNS_INDEX, listOfMensurationSigns);
		los.add(BARLINES_INDEX, listOfBarlines);
		los.add(ALL_EVENTS_INDEX, listOfAllEvents);
		
		listsOfSymbols = los;
	}


	// TESTED (together with setListsOfSymbols)
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
	// TESTED (together with getListsOfStatistics)
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
		List<String> listOfAllEvents = loss.get(Encoding.ALL_EVENTS_INDEX);
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
		List<String> listOfTabSymbols = loss.get(Encoding.TAB_SYMBOLS_INDEX);
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
			gridYOfTabSymbols.add(currentTabSymbol.getPitch(getTunings()[NEW_TUNING_INDEX], getTuningBassCourses()));
			// 12. horizontalPositionInTabSymbolEventsOnly
			int horizontalPosition = eventIndex - numberOfNonTabSymbolEventsPreceding;
			horizontalPositionInTabSymbolEventsOnly.add(horizontalPosition);
		}
		// Add the lists to listsOfStatistics
		los.add(IS_TAB_SYMBOL_EVENT_INDEX, isTabSymbolEvent);
		los.add(IS_RHYTHM_SYMBOL_EVENT_INDEX, isRhythmSymbolEvent);
		los.add(IS_REST_EVENT_INDEX, isRestEvent);
		los.add(IS_MENSURATION_SIGN_EVENT_INDEX, isMensurationSignEvent);
		los.add(IS_BARLINE_EVENT_INDEX, isBarlineEvent);
		los.add(SIZE_OF_EVENTS_INDEX, sizeOfEvents);
		los.add(DURATION_OF_EVENTS_INDEX, durationOfEvents);
		los.add(HORIZONTAL_POSITION_INDEX, horizontalPositionOfTabSymbols);
		los.add(VERTICAL_POSITION_INDEX, verticalPositionOfTabSymbols);
		los.add(DURATION_INDEX, durationOfTabSymbols);
		los.add(GRID_X_INDEX, gridXOfTabSymbols);
		los.add(GRID_Y_INDEX, gridYOfTabSymbols);
		los.add(HORIZONTAL_POSITION_TAB_SYMBOLS_ONLY_INDEX, horizontalPositionInTabSymbolEventsOnly);
	
		listsOfStatistics = los;
	}


	/**
	 * Gets listsOfStatistics.
	 */
	// TESTED (together with setListsOfStatistics)
	public List<List<Integer>> getListsOfStatistics() {
		return listsOfStatistics;
	}


	public static String createMetadata(String[] metadata) {
		String metadataStub = "";
		String [] tags = getMetadataTags();
		for (int i = 0; i < tags.length; i++) {
			String currTag = tags[i];
			metadataStub += SymbolDictionary.OPEN_INFO_BRACKET + currTag + metadata[i] +
				SymbolDictionary.CLOSE_INFO_BRACKET + "\r\n";
			if (i == 2 || i == tags.length - 1) {
				metadataStub += "\r\n";
			}
		}
		return metadataStub;
	}


	/**
	 * Gets the TuningSeventhCourse required for the Tablature.
	 * @return
	 */
	private TuningBassCourses getTuningBassCourses() {
		for (TuningBassCourses tsc: TuningBassCourses.values()) {
			if (tsc.toString().equals(getInfoAndSettings().get(TUNING_BASS_COURSES_INDEX))) {
				return tsc;
			}
		}
		return null;
	}


	/** 
	 *  Determines the staff length by calculating the number of segments needed for the longest
	 *  system. The number of segments needed for a system can be calculated by looking at the 
	 *  CMS only, as it equals the sum of
	 *    (i)  twice the system's number of spaces (each space is preceded by an event, and both
	 *         the event and the space need one segment);  
	 *    (ii) the total length of all the system's barlines. 
	 **/
	// TESTED
	public int getStaffLength() {
		int largestStaffLength = 0;

		String cleanEncoding = getCleanEncoding();
		String[] allSystems = cleanEncoding.substring(0, 
			cleanEncoding.indexOf(SymbolDictionary.END_BREAK_INDICATOR)).split(SymbolDictionary.SYSTEM_BREAK_INDICATOR);

		// For each system
		for (String system : allSystems) {
			int lengthCurrentSystem = 0;
			int symbolSeparatorIndex = -1;
			int nextSymbolSeparatorIndex = 
				system.indexOf(SymbolDictionary.SYMBOL_SEPARATOR, symbolSeparatorIndex + 1);
			// For each symbol
			while (nextSymbolSeparatorIndex != -1) {
				String symbol = 
					system.substring(symbolSeparatorIndex + 1, nextSymbolSeparatorIndex);
				// If symbol is a CMS       
				if (ConstantMusicalSymbol.getConstantMusicalSymbol(symbol) != null) { 
					// a. If symbol is a space, lengthOfCurrentSystem must be incremented by 2: 
					// one for the space and one for the event before it
					if (symbol.equals(ConstantMusicalSymbol.SPACE.getEncoding())) {
						lengthCurrentSystem += 2;
					}
					// b. If symbol is any CMS but a space, lengthOfCurrentSystem must be 
					// incremented by the length of the symbol 
					else {
						lengthCurrentSystem += symbol.length();
					}
				}
				symbolSeparatorIndex = nextSymbolSeparatorIndex;
				nextSymbolSeparatorIndex = system.indexOf(SymbolDictionary.SYMBOL_SEPARATOR, symbolSeparatorIndex + 1);
			}
			// Reset largestStaffLength if necessary
			if (lengthCurrentSystem > largestStaffLength) {
				largestStaffLength = lengthCurrentSystem;
			}
		}
		return largestStaffLength;
	}

}
