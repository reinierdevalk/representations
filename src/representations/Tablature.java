package representations;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;
import representations.Encoding.Tuning;
import tbp.ConstantMusicalSymbol;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.SymbolDictionary;
import tbp.TabSymbol;
import tbp.TabSymbolSet;
import tools.ToolBox;

public class Tablature implements Serializable {
	
	public static final int MAXIMUM_NUMBER_OF_NOTES = 5;
	public static final Rational SMALLEST_RHYTHMIC_VALUE = 
		new Rational(RhythmSymbol.semifusa.getDuration()/3, RhythmSymbol.brevis.getDuration());

	private Encoding encoding;
	private Integer[][] basicTabSymbolProperties;
	private List<List<TabSymbol>> tablatureChords;
	private List<Integer> numberOfNotesPerChord;
	private List<Integer[]> meterInfo = null;
	private List<Integer[]> keyInfo = null;
	private String pieceName;
	private boolean normaliseTuning;
	private File file;
	public static final int PITCH = 0;
	public static final int COURSE = 1;
	public static final int FRET = 2;
	public static final int ONSET_TIME = 3;
	public static final int MIN_DURATION = 4;
	public static final int MAX_DURATION = 5;
	public static final int CHORD_SEQ_NUM = 6;
	public static final int CHORD_SIZE_AS_NUM_ONSETS = 7;
	public static final int NOTE_SEQ_NUM = 8;
	private static final int TABLATURE_EVENT_SEQ_NUM = 9;

	public Tablature() {
	}


	public Tablature(File argFile, boolean argNormaliseTuning) { 
		encoding = new Encoding(argFile);
//		setPieceName(argFile.getName().substring(0, (argFile.getName().length() - ".tbp".length())));
		encoding.setName(argFile.getName().substring(0, (argFile.getName().length() - ".tbp".length())));
//		setFile(argFile);

		createTablature(argNormaliseTuning);
	}


	public Tablature(Encoding argEncoding, boolean argNormaliseTuning) {
		encoding = argEncoding;
		createTablature(argNormaliseTuning);
	}


	private void createTablature(boolean argNormaliseTuning) {
//		setRawEncoding(ToolBox.readTextFile(argFile));
//		if (checkForAddedInformationErrors() == false) { // needs rawEncoding
//			throw new RuntimeException("ERROR: The added information is not encoded correctly.");
//		}
//		setCleanEncoding(); // needs rawEncoding 
//		setInfoAndSettings(); // needs rawEncoding 
//		setFootnotes(); // needs rawEncoding
		
//		setTunings(); // needs infoAndSettings
		setPieceName(encoding.getName());
		setMeterInfo(); // needs infoAndSettings
//		if (checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
//			throw new RuntimeException("ERROR: The encoding contains encoding errors; run the TabViewer to correct them.");
//		}
//		setListsOfSymbols(); // needs cleanEncoding and infoAndSettings
//		setListsOfStatistics(); // needs infoAndSettings, tunings, and listOfSymbols
		
		setBasicTabSymbolProperties(); // needs infoAndSettings, listOfSymbols, and listOfStatistics

		if (argNormaliseTuning) {
			normaliseTuning(); // needs basicTabSymbolProperties
		}
		setNormaliseTuning(argNormaliseTuning);
		
		// added 3-9-2015
		setTablatureChords();
		setNumberOfNotesPerChord(); // needs tablatureChords
	}


	private Tablature(File argFile, boolean normaliseTuning, boolean bla) { 
		encoding = new Encoding(argFile);
		setPieceName(argFile.getName().substring(0, (argFile.getName().length() - ".tbp".length())));
		setFile(argFile);

//		setRawEncoding(ToolBox.readTextFile(argFile));
//		if (checkForAddedInformationErrors() == false) { // needs rawEncoding
//			throw new RuntimeException("ERROR: The added information is not encoded correctly.");
//		}
//		setCleanEncoding(); // needs rawEncoding 
//		setInfoAndSettings(); // needs rawEncoding 
//		setFootnotes(); // needs rawEncoding
		
//		setTunings(); // needs infoAndSettings
		setMeterInfo(); // needs infoAndSettings
//		if (checkForEncodingErrors() != null) { // needs rawEncoding, cleanEncoding, and infoAndSettings
//			throw new RuntimeException("ERROR: The encoding contains encoding errors; run the TabViewer to correct them.");
//		}
//		setListsOfSymbols(); // needs cleanEncoding and infoAndSettings
//		setListsOfStatistics(); // needs infoAndSettings, tunings, and listOfSymbols
		
		setBasicTabSymbolProperties(); // needs infoAndSettings, listOfSymbols, and listOfStatistics

		if (normaliseTuning) {
			normaliseTuning(); // needs basicTabSymbolProperties
		}
		
		// added 3-9-2015
		setTablatureChords();
		setNumberOfNotesPerChord(); // needs tablatureChords
	}
	
	
	public Encoding getEncoding() {
		return encoding;
	};


	private void setPieceName(String argName) {
		pieceName = argName; 
	}


	public String getPieceName() {
		return pieceName;
	}


	public void setNormaliseTuning(boolean arg) {
		normaliseTuning = arg;
	}


	public boolean getNormaliseTuning() {
		return normaliseTuning;
	}


	private void setFile(File argFile) {
		file = argFile;
	}


	private File getFile() {
		return file;
	}


//	/**
//	 *  Sets tuning and encodedTuning both with the tuning specified in the encoding. tuning may
//	 *  be changed during the preprocessing; encodedTuning retains its initial value.
//	 */
//	// TESTED (together with getTunings());
//	void setTunings() {
//		for (Tuning aTuning : Tuning.values()) { 
//			if (aTuning.toString().equals(encoding.getInfoAndSettings().get(Encoding.TUNING_INDEX))) {
//				tunings[ENCODED_TUNING_INDEX] = aTuning;
//				tunings[NEW_TUNING_INDEX] = aTuning; 
//				break;
//			}
//		}
//	}
//
//
//	// TESTED (together with setTunings())
//	public Tuning[] getTunings() {
//		return tunings;
//	}


	/**
	 * Creates the keyInfo.
	 * 
	 * @return A list, each element of which represents a key in the piece and contains:
	 *   <ul>
	 *   <li> as element 0: the key, as a number of sharps (positive) or flats (negative) </li>
	 *   <li> as element 1: the mode, where major = 0 and minor = 1 </li>
	 *   <li> as element 2: the first bar in the key </li>
	 *   <li> as element 3: the last bar in the key </li>
	 *   <li> as element 4: the numerator of the metric time of that first bar </li>
	 *   <li> as element 5: the denominator of the metric time of that first bar </li>
	 *   </ul>
	 */
	public void setKeyInfo(List<Integer[]> arg) {
		keyInfo = arg;
	}


	public List<Integer[]> getKeyInfo() {
		return keyInfo;
	}


	/**
	 * Sets meterInfo, a list whose elements represent the meters in the piece and contain:
	 * <ul>
	 * <li> as element 0: the numerator of the meter </li>
	 * <li> as element 1: the denominator of the meter </li>
	 * <li> as element 2: the first bar in the meter </li>
	 * <li> as element 3: the last bar in the meter </li>
	 * </ul>
	 * An anacrusis bar will be denoted with bar numbers 0-0.
	 */
	// TESTED (together with getMeterInfo())
	void setMeterInfo() {
		meterInfo = new ArrayList<Integer[]>();

//		int dim = 0; Integer.parseInt(encoding.getInfoAndSettings().get(Encoding.DIMINUTION_INDEX));
		String[] allMeters = encoding.getInfoAndSettings().get(Encoding.METER_INDEX).split(";");
//		String[] allDiminutions = encoding.getInfoAndSettings().get(Encoding.DIMINUTION_INDEX).split(";");
		List<Integer> allDiminutions = new ArrayList<>();
		for (String s : encoding.getInfoAndSettings().get(Encoding.DIMINUTION_INDEX).split(";")) {
			allDiminutions.add(Integer.parseInt(s.trim()));
		}
		
		// For each meter
		for (int i = 0; i < allMeters.length; i++) {
			String currInfo = allMeters[i].trim();
			// Get the num and denom of the current meter
			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
			String currMeterNum = currMeter.split("/")[0].trim();
			String currMeterDen = currMeter.split("/")[1].trim();			
			// Get the bar number(s) going with the current meter
			String currBars = 
				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();

			// Add the current meter num and denom
			Integer[] currentMeterInfo = new Integer[4];
			currentMeterInfo[0] = Integer.parseInt(currMeterNum);
			currentMeterInfo[1] = Integer.parseInt(currMeterDen);
			// Add the current bar numbers
			// If the meter is only for a single bar
			if (!currBars.contains("-")) {
				currentMeterInfo[2] = Integer.parseInt(currBars.trim());
				currentMeterInfo[3] = Integer.parseInt(currBars.trim());
			}
			// If the meter is for more than one bar
			else {
				String[] individualNumbers = currBars.split("-");
				currentMeterInfo[2] = Integer.parseInt(individualNumbers[0].trim());
				currentMeterInfo[3] = Integer.parseInt(individualNumbers[1].trim());
			}
			// Diminution
			int dim = allDiminutions.get(i);
			if (dim != 1) {
				if (dim > 0) {
					currentMeterInfo[1] = currentMeterInfo[1] / dim; // TODO will this always give an int
				}
				else {
					currentMeterInfo[1] = currentMeterInfo[1] * Math.abs(dim);
				}
			}
			meterInfo.add(currentMeterInfo);
		}
	}


	// TESTED (together with setMeterInfo())
	public List<Integer[]> getMeterInfo() {
		return meterInfo;
	}


	public List<Integer> getDiminutions() {
		List<Integer> diminutions = new ArrayList<>();
		String diminutionsStr = getEncoding().getInfoAndSettings().get(Encoding.DIMINUTION_INDEX);
		for (String s : diminutionsStr.split(";")) {
			diminutions.add(Integer.parseInt(s.trim()));
		}
		return diminutions;
	}


	/**
	 * Given the list of diminutions per meter in the given meterinfo, adapts the durations and 
	 * the onsets in the given lists of durations and onsets.
	 * 
	 * @param durationOfTabSymbols
	 * @param gridXOfTabSymbols
	 * @param diminutions
	 * @param originalMeterInfo
	 * @return
	 */
	// TESTED
	static List<List<Integer>> adaptToDiminutions(List<Integer> durationOfTabSymbols, List<Integer> 
		gridXOfTabSymbols, List<Integer> diminutions, List<Integer[]> originalMeterInfo) {
		List<List<Integer>> res = new ArrayList<>();

		// Get the metric time and the adapted metric time of beat 0 for all new meters
		List<Integer> metricTimesBeatZero = new ArrayList<>();
		metricTimesBeatZero.add(0);
		List<Integer> metricTimesBeatZeroAdapted = new ArrayList<>();
		metricTimesBeatZeroAdapted.add(0);
		for (int i = 1 ; i < originalMeterInfo.size(); i++) {
			Integer[] prevMeterInfo = originalMeterInfo.get(i-1);
			int prevNumBars = (prevMeterInfo[3] - prevMeterInfo[2]) + 1; 
			Rational prevMeter = new Rational(prevMeterInfo[0], prevMeterInfo[1]);
			// The metric time for beat zero equals 
			// original: previous meter * number of bars in that meter 
			// adapted: previous meter * number of bars in that meter * (or /) the dim for that meter
			int prevDim = diminutions.get(i-1);
			Rational beatZero = prevMeter.mul(prevNumBars);
			Rational beatZeroAdapted;
			if (prevDim > 0) {
				beatZeroAdapted = prevMeter.mul(prevDim).mul(prevNumBars ); 
			}
			else {
				beatZeroAdapted = prevMeter.mul(prevNumBars).div(Math.abs(prevDim));
			}
			// Represent Rational r as integer using cross-multiplication
			// num(r)/den(r) = x/32 --> x * den(r) = num(r) * 32 --> x = (num(r) * 32) / den(r) 
//			int beatZeroAsInt = 
//				(beatZero.getNumer() * SMALLEST_RHYTHMIC_VALUE.getDenom()) / beatZero.getDenom();
//			int beatZeroAdaptedAsInt = 
//				(beatZeroAdapted.getNumer() * SMALLEST_RHYTHMIC_VALUE.getDenom()) / beatZeroAdapted.getDenom();
			int beatZeroAsInt = rationalToIntDur(beatZero);
			int beatZeroAdaptedAsInt = rationalToIntDur(beatZeroAdapted);

			metricTimesBeatZero.add(metricTimesBeatZero.get(i-1) + beatZeroAsInt);
			metricTimesBeatZeroAdapted.add(metricTimesBeatZeroAdapted.get(i-1) + beatZeroAdaptedAsInt);
		}
		
		// Get the adapted durations and onsets. gridXOfTabSymbols and durationOfTabSymbols have
		// the same size: that of listOfTabSymbols (i.e., the number of TS in the tablature)
		List<Integer> newDurationOfTabSymbols = new ArrayList<>();
		List<Integer> newGridXOfTabSymbols = new ArrayList<>();
		int ind = 0;
		int dim = -1;
		int beatZero = -1; 
		int beatZeroAdapted = -1; 
		for (int i = 0; i < durationOfTabSymbols.size(); i++) {
			int currDur = durationOfTabSymbols.get(i);
			int currOnset = gridXOfTabSymbols.get(i); // orig
			// If currOnset is on or past metric beat zero at index ind: determine new metric beat zeros
			if (ind < diminutions.size() && currOnset >= metricTimesBeatZero.get(ind)) {
				dim = diminutions.get(ind);
				beatZero = metricTimesBeatZero.get(ind);
				beatZeroAdapted = metricTimesBeatZeroAdapted.get(ind);
				ind++;
			}
			// Add current duration and onset to lists
			if (dim > 0) {
				newDurationOfTabSymbols.add(currDur*dim);
				newGridXOfTabSymbols.add(beatZeroAdapted + (currOnset-beatZero)*dim);
			}
			else {
				newDurationOfTabSymbols.add(currDur/Math.abs(dim));
				newGridXOfTabSymbols.add(beatZeroAdapted + (currOnset-beatZero)/Math.abs(dim));
			}
		}

		res.add(newDurationOfTabSymbols);
		res.add(newGridXOfTabSymbols);
		return res;
	}


	/**
	 * Sets <i>basicTabSymbolProperties</i>, a two-dimensional Array in which the basic TS properties are stored.
	 * It contains for each TS in row i the following properties:
	 *   in column 0: the pitch of the TS (as a MIDInumber)
	 *   in column 1: the course of the TS
	 *   in column 2: the fret of the TS
	 *   in column 3: the onset time of the TS (as multiples of SMALLEST_RHYTHMIC_VALUE.getDenom())
	 *   in column 4: the minimum duration of the TS, which is also the duration of the chord it is in 
	 *                (as multiples of SMALLEST_RHYTHMIC_VALUE.getDenom())
	 *   in column 5: the maximum duration of the TS, i.e., its duration until it is cut off by another TS on the
	 *                same course (as multiples of SMALLEST_RHYTHMIC_VALUE.getDenom())               
	 *   in column 6: the sequence number of the chord the TS is in
	 *   in column 7: the size of the chord the TS is in
	 *   in column 8: the sequence number within the chord of the TS
	 *   in column 9: the sequence number of the event the TS is in, with ALL events considered
	 *
	 * NB: This method must always be called after setListsOfStatistics
	 */
	// TESTED (together with getBasicTabSymbolProperties())
	void setBasicTabSymbolProperties() {
		List<List<String>> symbols = encoding.getListsOfSymbols();
		List<List<Integer>> stats = encoding.getListsOfStatistics();
		TabSymbolSet tss = encoding.getTabSymbolSet();
		List<String> listOfTabSymbols = symbols.get(Encoding.TAB_SYMBOLS_INDEX);
		List<String> listOfAllEvents = symbols.get(Encoding.ALL_EVENTS_INDEX);
		List<Integer> isTabSymbolEvent = stats.get(Encoding.IS_TAB_SYMBOL_EVENT_INDEX); 
		List<Integer> sizeOfEvents = stats.get(Encoding.SIZE_OF_EVENTS_INDEX);
		List<Integer> horizontalPositionOfTabSymbols = stats.get(Encoding.HORIZONTAL_POSITION_INDEX);
		List<Integer> verticalPositionOfTabSymbols = stats.get(Encoding.VERTICAL_POSITION_INDEX);
		List<Integer> durationOfTabSymbols = stats.get(Encoding.DURATION_INDEX);
		List<Integer> gridXOfTabSymbols = stats.get(Encoding.GRID_X_INDEX);
		List<Integer> gridYOfTabSymbols = stats.get(Encoding.GRID_Y_INDEX);  	
		List<Integer> horizontalPositionInTabSymbolEventsOnly = 
			stats.get(Encoding.HORIZONTAL_POSITION_TAB_SYMBOLS_ONLY_INDEX);

		// Reconstruct the original meterInfo. diminutions and meterInfo have the same size
		List<Integer[]> unadaptedMeterinfo = new ArrayList<>();
		List<Integer> diminutions = new ArrayList<>();
		for (String s : encoding.getInfoAndSettings().get(Encoding.DIMINUTION_INDEX).split(";")) {
			diminutions.add(Integer.parseInt(s.trim()));
		}
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] in = meterInfo.get(i);
			int dim = diminutions.get(i);
			Integer[] unAdapted = Arrays.copyOf(in, in.length);
			if (dim > 0) {
				unAdapted[1] = unAdapted[1]*dim;
			}
			else {
				unAdapted[1] = unAdapted[1]/Math.abs(dim);
			}
			unadaptedMeterinfo.add(unAdapted);
		}

		List<List<Integer>> scaled = 
			adaptToDiminutions(durationOfTabSymbols, gridXOfTabSymbols, diminutions, unadaptedMeterinfo);
		durationOfTabSymbols = scaled.get(0);
		gridXOfTabSymbols = scaled.get(1);
		
		basicTabSymbolProperties = new Integer[listOfTabSymbols.size()][10];
		for (int i = 0; i < basicTabSymbolProperties.length; i++) {
			TabSymbol currentTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			// 0. Pitch
			basicTabSymbolProperties[i][PITCH] = gridYOfTabSymbols.get(i);
			// 1. Course
			basicTabSymbolProperties[i][COURSE] = currentTabSymbol.getCourse();
			// 2. Fret
			basicTabSymbolProperties[i][FRET] = currentTabSymbol.getFret();
			// 3. Onset time
			int currentOnsetTime = gridXOfTabSymbols.get(i);
			basicTabSymbolProperties[i][ONSET_TIME] = currentOnsetTime;
			// 4. Minimum duration
			int currentMinDuration = durationOfTabSymbols.get(i);
			basicTabSymbolProperties[i][MIN_DURATION] = currentMinDuration;
			// 5. Maximum duration
			int currentMaxDuration;
			// a. If currentabSymbol is in the last tabSymbolEvent: set currentMaxDuration to currentMinDuration
			// Get the index of the last TabSymbolEvent
			int numberOfTabSymbolEvents = 0;
			for (int j = 0; j < listOfAllEvents.size(); j++) {
				if (isTabSymbolEvent.get(j) == 1) {
					numberOfTabSymbolEvents++;
				}
			}
			int indexOfLastChord = numberOfTabSymbolEvents - 1;
			int indexOfCurrentChord = horizontalPositionInTabSymbolEventsOnly.get(i);
			if (indexOfCurrentChord == indexOfLastChord) {
				currentMaxDuration = currentMinDuration;
			}
			// b. If not: calculate currentMaxDuration
			// Find the next TS in listOfTabSymbols that has the same course and get its onset time, then determine
			// currentMaxDuration
			else {
				int nextOnsetTime = 0;
				for (int j = i + 1; j < basicTabSymbolProperties.length; j++) {
					TabSymbol nextTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(j), tss);
					// If there is a next TabSymbol on the same course
					if (nextTabSymbol.getCourse() == currentTabSymbol.getCourse()) {
						nextOnsetTime = gridXOfTabSymbols.get(j);
						break;
					}
					// If there is no next TabSymbol on the same course anymore: nextOnsetTime is the 
					// end of the piece, i.e., the onset time of the last TabSymbol + its minDuration
					else {
						// Get the onset time of the last TabSymbol and its minDuration  
						int indexOfLastTabSymbol = listOfTabSymbols.size() - 1;
						int onsetTimeLastTabSymbol = gridXOfTabSymbols.get(indexOfLastTabSymbol);
						int minDurationLastTabSymbol = durationOfTabSymbols.get(indexOfLastTabSymbol); 
						nextOnsetTime = onsetTimeLastTabSymbol + minDurationLastTabSymbol;
					}
				}
				currentMaxDuration = nextOnsetTime - currentOnsetTime;
			}
			basicTabSymbolProperties[i][MAX_DURATION] = currentMaxDuration;
			// 6. The sequence number of the chord the TS is in
			basicTabSymbolProperties[i][CHORD_SEQ_NUM] = indexOfCurrentChord;
			// 7. The size of the chord the TS is in
			int indexOfCurrentTablatureEvent = horizontalPositionOfTabSymbols.get(i);
			basicTabSymbolProperties[i][CHORD_SIZE_AS_NUM_ONSETS] = sizeOfEvents.get(indexOfCurrentTablatureEvent);
			// 8. The sequence number within the chord of the TS
			basicTabSymbolProperties[i][NOTE_SEQ_NUM] = verticalPositionOfTabSymbols.get(i);
			// 9. The sequence number of the tablature event the TS is in, with ALL events considered 
			basicTabSymbolProperties[i][TABLATURE_EVENT_SEQ_NUM] = horizontalPositionOfTabSymbols.get(i);
		}
	}


	/**
	 * Gets <i>basicTabSymbolProperties</i>.
	 * 
	 * @return
	 */
	// TESTED (together with setBasicTabSymbolProperties())
	public Integer[][] getBasicTabSymbolProperties() {
		return basicTabSymbolProperties;
	}


	/** 
	 * Sets tablatureChords, a List in which all TabSymbols in the Tablature are arranged in chords. The 
	 * Tablature is traversed from left to right, and the chords themselves are arranged starting with the 
	 * lowest-string TabSymbol. 
	 * NB: Rest events are not included in the returned list. 
	 */
	// TESTED (together with getTablatureChord())
	void setTablatureChords() {
		tablatureChords = new ArrayList<List<TabSymbol>>();
		TabSymbolSet tss = encoding.getTabSymbolSet();

		List<TabSymbol> currentChord = new ArrayList<TabSymbol>();
		List<String> listOfTabSymbols = encoding.getListsOfSymbols().get(Encoding.TAB_SYMBOLS_INDEX);
		TabSymbol firstTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(0), tss);
		int onsetTimeOfFirstTabSymbol = basicTabSymbolProperties[0][ONSET_TIME];
		currentChord.add(firstTabSymbol);
		int onsetTimeOfPreviousTabSymbol = onsetTimeOfFirstTabSymbol;
		for (int i = 1; i < listOfTabSymbols.size(); i++) {
			TabSymbol currentTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			int onsetTimeOfCurrentTabSymbol = basicTabSymbolProperties[i][ONSET_TIME];
			// If currentTabSymbol has the same onset time as previousTabSymbol, they belong to the same chord: add 
			// currentTabSymbol to currentChord
			if (onsetTimeOfCurrentTabSymbol == onsetTimeOfPreviousTabSymbol) {
				currentChord.add(currentTabSymbol);
			}
			// If currentTabSymbol has a different onset time than previousTabSymbol, currentTabSymbol is the first
			// TabSymbol of the next chord. Add currentChord to tablatureChords, create a new currentChord, and add 
			// currentTabSymbol to it.  
			else {
				tablatureChords.add(currentChord);
				currentChord = new ArrayList<TabSymbol>();
				currentChord.add(currentTabSymbol);
			}
			onsetTimeOfPreviousTabSymbol = onsetTimeOfCurrentTabSymbol;
		}
		// Add the last chord to tablatureChords
		tablatureChords.add(currentChord);
	}


	// TESTED (together with setTablatureChord())
	public List<List<TabSymbol>> getTablatureChords() {
		return tablatureChords;
	}


	/**
	 * Sets numberOfNotesPerChord, a list containing the number of notes in each Tablature chord.
	 */ 
	// TESTED (together with getNumberOfNotesPerChord())
	void setNumberOfNotesPerChord() { // TODO set in conjunction with setTablatureChords()?
//		public List<Integer> getNumberOfNotesPerChord() {
		numberOfNotesPerChord = new ArrayList<Integer>();

//		List<List<TabSymbol>> tablatureChords = getTablatureChords();
		for (List<TabSymbol> l : getTablatureChords()) {
			numberOfNotesPerChord.add(l.size());
		}
//		return numberOfNotesPerChord;
	}


	// TESTED (together with setNumberOfNotesPerChord())
	public List<Integer> getNumberOfNotesPerChord() {
		return numberOfNotesPerChord;
	}


	/**
	 * Gets the basicTabSymbolProperties of the chord at the given index in the Tablature, i.e., the elements of 
	 * <i>basicTabSymbolProperties</i> corresponding to the notes within that chord. 
	 * 
	 * @param chordIndex 
	 * @return
	 */
	// TESTED
	public Integer[][] getBasicTabSymbolPropertiesChord(int chordIndex) {
		// Determine the size of the chord at chordIndex and the index of the lowest note in it
		int chordSize = 0;
		int lowestNoteIndex = 0;
		for (int i = 0; i < basicTabSymbolProperties.length; i++) {
			if (basicTabSymbolProperties[i][CHORD_SEQ_NUM] == chordIndex) {
				lowestNoteIndex = i;
				chordSize = basicTabSymbolProperties[i][CHORD_SIZE_AS_NUM_ONSETS];
				break;
			}
		}
		// Create and return basicTabSymbolPropertiesChord
		Integer[][] basicTabSymbolPropertiesChord = 
			Arrays.copyOfRange(basicTabSymbolProperties, lowestNoteIndex, 
			lowestNoteIndex + chordSize);   
		return basicTabSymbolPropertiesChord;
	}


	/**
	 * Returns the interval (in semitones) by which the tablature must be transposed in order to normalise its
	 * tuning to G.
	 * 
	 * @return
	 */
	// TESTED
	public int getTranspositionInterval() {
		int transpositionInterval = 0;

		Tuning originalTuning = encoding.getTunings()[Encoding.ENCODED_TUNING_INDEX];
		
		for (Tuning t : Tuning.values()) {
			if (t.equals(originalTuning)) {
				transpositionInterval = -(t.getTransposition());
				break;
			}
		}
		
//		switch (originalTuning) {
//			case A:
//				transpositionInterval = -2;
//				break;
//			case A_AVALLEE:
//				transpositionInterval = -2;
//				break;
//			case F:
//				transpositionInterval = 2;
//				break;			
//			case C_AVALLEE:
//				transpositionInterval = 7;
//				break;
//		}

		return transpositionInterval;
	}


	/**
	 * Normalises the tuning to G. Resets 
	 *   tunings[NEW_TUNING_INDEX];
	 *   element GRID_Y_INDEX of listsOfStatistics; 
	 *   element PITCH in each element of basicTabSymbolProperties.
	 */
	// TESTED
	void normaliseTuning() {
		int transpositionInterval = getTranspositionInterval();

		Tuning[] tunings = encoding.getTunings();

		Tuning originalTuning = tunings[Encoding.ENCODED_TUNING_INDEX];
		
//		for (Tuning t : Tuning.values()) {
//			if (t.equals(originalTuning)) {
//				transpositionInterval = -(t.getTransposition());
//				break;
//			}
//		}
		
		if (originalTuning.isAvallee()) {
			tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G_AVALLEE;
		}
		else {
			tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G;
		}
		
//		switch (originalTuning) {
//			case A:
//				tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G;
//				break;
//			case A_AVALLEE:
//				tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G_AVALLEE;
//				break;
//			case F:
//				tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G;
//				break;
//			case C_AVALLEE:
//				tunings[Encoding.NEW_TUNING_INDEX] = Tuning.G_AVALLEE;
//				break;
//		}

		// Reset the list of gridY values in listsOfStatistics
		List<List<Integer>> stats = encoding.getListsOfStatistics();
		List<Integer> oldGridYOfTabSymbols = stats.get(Encoding.GRID_Y_INDEX);
		List<Integer> newGridYOfTabSymbols = new ArrayList<Integer>(); 
		for (int i : oldGridYOfTabSymbols) {
			newGridYOfTabSymbols.add(i + transpositionInterval);
		}
		stats.set(Encoding.GRID_Y_INDEX, newGridYOfTabSymbols);

		// Reset the pitches in basicTabSymbolProperties
		for (Integer[] in : basicTabSymbolProperties) {
			in[PITCH] = in[PITCH] + transpositionInterval;
		}  	
	}


	/**
	 * Gets the metric position of the note at the onset time. Returns a Rational[] with 
	 *   as element 0: the bar number (whose denominator will always be 1);
	 *   as element 1: the position within the bar, reduced and starting at 0/x (where x is the common denominator,
	 *                 i.e., the product of the denominator of metricTime and the largest meter denominator).
	 * 
	 * If there is an anacrusis: if metricTime falls within the anacrusis, the bar number returned will be 0,
	 * and the position within the bar will be the position as if the anacrusis were a full bar.
	 *                 
	 * Example: a metric time of 9/8 in meter 6/8 returns 2/1 and 3/8 (i.e., the fourth 8th note in bar 2).
	 * 
	 * @param metricTime
	 * @param argMeterInfo
	 * @return
	 */
	// TESTED
	public static Rational[] getMetricPosition(Rational metricTime, List<Integer[]> argMeterInfo) {
		Rational[] metricPosition = new Rational[2];

		// 0. Determine the presence of an anacrusis
		boolean containsAnacrusis = false;
		if (argMeterInfo.get(0)[2] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] i : argMeterInfo) {
			if (i[1] > largestMeterDenom) {
				largestMeterDenom = i[1];
			}
		}
		int commonDenom = metricTime.getDenom() * largestMeterDenom;

		// 2. Express metricTime and all meters in commonDenom  	
		// a. metricTime
		Rational metricTimeInLargestDenom = 
			new Rational(metricTime.getNumer() * largestMeterDenom, metricTime.getDenom() * largestMeterDenom);
		// b. All meters
		List<Rational> metersInLargestDenom = new ArrayList<Rational>();
		for (int i = 0; i < argMeterInfo.size(); i++) {
			Integer[] currentMeter = new Integer[]{argMeterInfo.get(i)[0], argMeterInfo.get(i)[1]};
			// factor will always be an int because largestMeterDenom will always be a multiple of currentMeter[1]    	
			int factor = (largestMeterDenom / currentMeter[1]) * metricTime.getDenom();  
			metersInLargestDenom.add(new Rational(currentMeter[0] * factor, commonDenom));
		}

		// 3. List for the initial meter and any following meter change points the metric time (in commonDenom).
		// The first element of the list will be the metric time of the first full bar
		// The last element of the list will be the metric time of the fictional bar after the last bar
		List<Rational> meterChangePointsMetricTimes = new ArrayList<Rational>();
		// Determine the initial meter change point and set startIndex so that if an anacrusis is present, the
		// first element of argMeterInfo (containing the anacrusis information) is skipped
		int startIndex;
		if (containsAnacrusis) {
			meterChangePointsMetricTimes.add(new Rational(metersInLargestDenom.get(0).getNumer(), commonDenom));
			startIndex = 1;
		}
		else {
			meterChangePointsMetricTimes.add(new Rational(0, commonDenom));
			startIndex = 0;
		}
		// Determine the remaining meter change points
		for (int i = startIndex; i < argMeterInfo.size(); i++) {
			// Determine the number of bars in the current meter
			int numBarsInCurrentMeter = (argMeterInfo.get(i)[3] - argMeterInfo.get(i)[2]) + 1;
			// Determine the metric time of the next meter change point and add it to meterChangePointsMetricTimes
			// NB: When creating the new Rational do not use add() to avoid automatic reduction
			Rational currentMeter = metersInLargestDenom.get(i);
			int toAdd = numBarsInCurrentMeter * currentMeter.getNumer();
			meterChangePointsMetricTimes.add(new Rational(meterChangePointsMetricTimes.get(i - startIndex).getNumer() +
				toAdd, commonDenom));	 	
		}
      	
		// 4. Determine the bar number and the position in the bar, and set metricPosition
		// a. If metricTime falls within the anacrusis (the if can only be satisfied if there is an anacrusis)
		if (metricTimeInLargestDenom.getNumer() < meterChangePointsMetricTimes.get(0).getNumer()) {
			// Determine the position in the bar as if it were a full bar 
			Rational lengthAnacrusis = metersInLargestDenom.get(0);
			Rational meterFirstBar = metersInLargestDenom.get(1);
			int toAdd = meterFirstBar.getNumer() - lengthAnacrusis.getNumer();
			Rational positionInBar = new Rational(metricTimeInLargestDenom.getNumer() + toAdd, commonDenom);
//			positionInBar = positionInBar.div(meterFirstBar);
			positionInBar.reduce();
			// Set metricPosition; the bar number is 0
			metricPosition[0] = new Rational(0, 1);
			metricPosition[1] = positionInBar;
		}
		// b. If metricTime falls after the anacrusis
		else {
			for (int i = 0; i < meterChangePointsMetricTimes.size() - 1; i++) {
				// Determine the meter change points and bar size (in commonDenom) for the current meter
				Rational currentPrevious = meterChangePointsMetricTimes.get(i);
				Rational currentNext = meterChangePointsMetricTimes.get(i + 1); 
				int currentBarSize = metersInLargestDenom.get(i + startIndex).getNumer();

				// If metricTime falls within the current meter change points: determine bar number and position in bar
				if (metricTimeInLargestDenom.isGreaterOrEqual(currentPrevious) && metricTimeInLargestDenom.isLess(currentNext)) {
					// Determine the bar number
					int currentDistance = metricTimeInLargestDenom.getNumer() - currentPrevious.getNumer();
					int numberOfBarsToAdd =	(currentDistance - (currentDistance % currentBarSize)) / currentBarSize;   			
					int currentBarNumber = argMeterInfo.get(i + startIndex)[2] + numberOfBarsToAdd;
					// Determine the position in the bar
					Rational currentPositionInBar = new Rational(currentDistance % currentBarSize, commonDenom);
//					Rational currentMeter = metersInLargestDenom.get(i + startIndex);
//					currentPositionInBar = currentPositionInBar.div(currentMeter);
					currentPositionInBar.reduce();
					// Set metricPosition and break
					metricPosition[0] = new Rational(currentBarNumber, 1);
					metricPosition[1] = currentPositionInBar;
					break;
				}
			}
		}
		return metricPosition;
	}


	/**
	 * Returns the int value of the given duration Rational.
	 *  
	 * @param dur
	 * @return
	 */
	// TESTED
	public static int rationalToIntDur(Rational dur) {
		return dur.mul(SMALLEST_RHYTHMIC_VALUE.getDenom()).getNumer();
	}


	/**
	 * Returns all metric positions.
	 * 
	 * @return
	 */
	// TESTED
	public List<Rational[]> getAllMetricPositions() {
		List<Rational[]> allMetricPositions = new ArrayList<Rational[]>();
		List<Integer[]> meterInf = getMeterInfo();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (Integer[] b : btp) {
			Rational currentMetricTime = 
				new Rational(b[ONSET_TIME], SMALLEST_RHYTHMIC_VALUE.getDenom());
			allMetricPositions.add(getMetricPosition(currentMetricTime, meterInf)); 
		}
		return allMetricPositions;
	}


	/**
	 * Returns all onset times.
	 * 
	 * @return
	 */
	// TESTED
	public List<Rational> getAllOnsetTimes() {
		List<Rational> allOnsetTimes = new ArrayList<Rational>();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (Integer[] b : btp) {
			Rational currOnsetTime = new Rational(b[ONSET_TIME], SMALLEST_RHYTHMIC_VALUE.getDenom()); 
			if (!allOnsetTimes.contains(currOnsetTime)) {
				allOnsetTimes.add(currOnsetTime);
			}
		}
		Collections.sort(allOnsetTimes);
		return allOnsetTimes;
	}
	
	
	/**
	 * Returns, for each onset time, the onset time and the minimum duration of the chord at it.
	 * 
	 * @return
	 */
	// TESTED
	public List<Rational[]> getAllOnsetTimesAndMinDurations() {
		List<Rational[]> allOnsetTimesAndMinDurs = new ArrayList<Rational[]>();
		Integer[][] btp = getBasicTabSymbolProperties();
		List<Rational> allOnsetTimes = new ArrayList<>();
		for (Integer[] b : btp) {
			Rational currentOnsetTime = 
				new Rational(b[ONSET_TIME], SMALLEST_RHYTHMIC_VALUE.getDenom());
			Rational currentMinDur = new Rational(b[MIN_DURATION], SMALLEST_RHYTHMIC_VALUE.getDenom());
			if (!allOnsetTimes.contains(currentOnsetTime)) {
				allOnsetTimes.add(currentOnsetTime);
				allOnsetTimesAndMinDurs.add(new Rational[]{currentOnsetTime, currentMinDur});
			}
		}
//		Collections.sort(allOnsetTimesAndMinDurs);
		ToolBox.sortByRational(allOnsetTimesAndMinDurs, 0);
		return allOnsetTimesAndMinDurs;
	}


	/**
	 * Returns the minimum duration as a duration label for each TabSymbol in the tablature.
	 *   
	 * @return
	 */
	// TESTED
	public List<List<Double>> getMinimumDurationLabels() {
		List<List<Double>> minDurLabels = new ArrayList<>();
		for (Integer[] in : getBasicTabSymbolProperties()) {
			minDurLabels.add(Transcription.createDurationLabel(in[MIN_DURATION]));
		}
		return minDurLabels;
	}


	/**
	 * Determines the number of unisons in the chord at the given index. A unison occurs when two different 
	 * Tablature notes in the same chord have the same pitch.   
	 * 
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	int getNumberOfUnisonsInChord(int chordIndex) { // TODO integrate into getUnisonInfo()?
		int numberOfUnisons = 0;

		List<List<TabSymbol>> tablatureChords = getTablatureChords();
		List<TabSymbol> tablatureChord = tablatureChords.get(chordIndex);

		// Only relevant if tablatureChord contains multiple TS
		if (tablatureChord.size() > 1) {
			// List all the unique pitches in tablatureChord
			List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
			List<Integer> uniquePitchesInChord = new ArrayList<Integer>();
			for (int pitch : pitchesInChord) {
				if (!uniquePitchesInChord.contains(pitch)) {
					uniquePitchesInChord.add(pitch);
				}
			}
			// Compare the sizes of pitchesInChord and uniquePitchesInChord; if they are not the same the chord
			// contains (a) unison(s)
			if (pitchesInChord.size() == (uniquePitchesInChord.size() + 1)) {
				numberOfUnisons = 1;
			}
			if (pitchesInChord.size() == (uniquePitchesInChord.size() + 2)) {
				numberOfUnisons = 2;
			}
			if (pitchesInChord.size() == (uniquePitchesInChord.size() + 3)) {
				numberOfUnisons = 3;
			}
		}
		return numberOfUnisons;
	}


	/**
	 * Gets information on the unison(s) in the chord at the given index. A unison occurs when 
	 * two different Tablature notes in the same chord have the same pitch.  
	 *
	 * Returns an Integer[][], each element of which represents a unison pair (starting from below), each element
	 * of which contains:
	 *   as element 0: the pitch (as a MIDInumber) of the unison note
	 *   as element 1: the sequence number in the chord of the lower unison note (i.e., the one appearing first in the chord)
	 *   as element 2: the sequence number in the chord of the upper unison note 
	 * If the chord does not contain (a) unison(s), <code>null</code> is returned. 
	 *
	 * NB: This method presumes that
	 * a) a chord contains only one unison, and neither a CoD nor a course crossing;
	 * b) a chord will not contain two unisons of the same pitch (e.g., 6th c., 10th fr. - 
	 *    5th c., 5th fr. - 4th c., open), which is theoretically possible but will not occur in
	 *    practice.
	 *
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	public Integer[][] getUnisonInfo(int chordIndex) {
		Integer[][] unisonInfo = null;

		// If the chord at chordIndex contains (a) unison(s)
		if (getNumberOfUnisonsInChord(chordIndex) > 0) {
			unisonInfo = new Integer[getNumberOfUnisonsInChord(chordIndex)][3];

			// List the pitches in the chord at chordIndex
			List<Integer> pitchesInChord = getPitchesInChord(chordIndex);

			// For each pitch in pitchesInChord 
			int currentRowInUnisonInfo = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);        
				// Search the remainder of eventPitches for an onset with the same pitch
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					// Same pitch found? Unison found; fill the currentRowInUnisonInfo-th row of unisonInfo, increase
					// currentRowInUnisonInfo, break from inner for, and continue with the next iteration of the outer
					// for (the next pitch)
					if (pitchesInChord.get(j) == currentPitch) {
						unisonInfo[currentRowInUnisonInfo][0] = currentPitch;
						unisonInfo[currentRowInUnisonInfo][1] = i;
						unisonInfo[currentRowInUnisonInfo][2] = j;
						currentRowInUnisonInfo++;
						break; // See NB b) for reason of break
					}
				} 
			}
		}
		return unisonInfo;
	}


	/**
	 * Determines the number of course crossings in the chord at the given index. A course crossing occurs
	 * when an onset on course x has a pitch that is higher than that of an onset on course y above it.
	 * 
	 * @param chordIndex
	 * @return 
	 */
	// TESTED
	int getNumberOfCourseCrossingsInChord(int chordIndex) {
		int numberOfCourseCrossings = 0;

		List<TabSymbol> tablatureChord = getTablatureChords().get(chordIndex);
		// Only relevant if tablatureChord contains multiple TS
		if (tablatureChord.size() > 1) {
			// List all the pitches in tablatureChord
			List<Integer> pitchesInChord = getPitchesInChord(chordIndex); 
			// Check for each pitch all the pitches above it; increase numberOfCourseCrossings if a lower pitch
			// is found
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);
				// Check the following pitches; if a lower pitch is found: increase numberOfCourseCrossings, break
				// from inner for, and continue with the next iteration of the outer for (the next note) 
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					if (pitchesInChord.get(j) < currentPitch) {
						numberOfCourseCrossings++;
						break;
					}
				}
			}
		}
		return numberOfCourseCrossings;
	}


	/**
	 * Gets information on the course crossing(s) in the event at the given index in the given list. A course
	 * crossing occurs when an note on course x has a pitch that is higher than that of a note (in the same chord)
	 * on course y above it.
	 * Returns an Integer[][], each element of which represents a course crossing pair (starting from below), 
	 * each element of which contains
	 *   as element 0: the pitch (as a MIDInumber) of the lower CC-note (i.e., the one appearing first in the chord)
	 *   as element 1: the pitch (as a MIDInumber) of the upper CC-note
	 *   as element 2: the sequence number in the chord of the lower CC-note
	 *   as element 3: the sequence number in the chord of the upper CC-note 
	 * If the chord does not contain (a) course crossing(s), <code>null</code> is returned.   
	 *
	 * NB: This method presumes that
	 * a) a chord contains only one course crossing, and neither a CoD nor a unison; 
	 * b) the course crossing will not span more than two pitches, which is theoretically possible (e.g.,
	 * 6th c., 12th fr. - 5th c., 6th fr. - 4th c., open), but will not occur in practice.
	 *
	 * @param chordIndex 
	 * @return 
	 */
	// TESTED
	public Integer[][] getCourseCrossingInfo(int chordIndex) {

		Integer[][] courseCrossingsInfo = null;

		// If the chord at chordIndex contains (a) course crossing(s)
		if (getNumberOfCourseCrossingsInChord(chordIndex) > 0) {
			courseCrossingsInfo = new Integer[getNumberOfCourseCrossingsInChord(chordIndex)][4];

			// List the pitches in the chord at chordIndex
			List<Integer> pitchesInChord = getPitchesInChord(chordIndex);

			// For each pitch in pitchesInChord
			int currentRowInCourseCrossingInfo = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);        
				// Search the remainder of pitchesInChord for a note with a lower pitch
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					// Lower pitch found? Course crossing found; fill the currentRowInCourseCrossingsInfo-th row of 
					// courseCrossingsInfo, increase currentRowInCourseCrossingsInfo, break from inner for, and 
					// continue with the next iteration of the outer for (the next pitch)
					if (pitchesInChord.get(j) < currentPitch) {
						int lowerPitch = pitchesInChord.get(j);
						courseCrossingsInfo[currentRowInCourseCrossingInfo][0] = currentPitch;
						courseCrossingsInfo[currentRowInCourseCrossingInfo][1] = lowerPitch;
						courseCrossingsInfo[currentRowInCourseCrossingInfo][2] = i;
						courseCrossingsInfo[currentRowInCourseCrossingInfo][3] = j;
						currentRowInCourseCrossingInfo++;
						break; // See NB b) for reason of break
					}
				} 
			}
		}
		return courseCrossingsInfo;
	}


	// TESTED
	public int getNumberOfNotes() {
		return basicTabSymbolProperties.length;	
	}


	/**
	 * Returns a List<List>> containing, for each chord, the indices in the Tablature of the notes in that chord.
	 * 
	 * @param isBwd 
	 * @return
	 */
	// TESTED
	public List<List<Integer>> getIndicesPerChord(/*ProcessingMode procMode*/ boolean isBwd) {
		List<List<Integer>> indicesPerChord = new ArrayList<List<Integer>>();

		List<List<TabSymbol>> tablatureChords = getTablatureChords();
		if (isBwd) {
//		if (procMode == ProcessingMode.BWD) {
			Collections.reverse(tablatureChords);
		}

		int startIndex = 0;
		// For each chord
		int numChords = tablatureChords.size();
		for (int i = 0; i < numChords; i++) {
		List<Integer> indicesCurrChord = new ArrayList<Integer>();
		int endIndex = startIndex + tablatureChords.get(i).size();
		// For each note in the chord at index i: add its index to indicesCurrChord
		for (int j = startIndex; j < endIndex; j++) {
			indicesCurrChord.add(j);
		}
		// When the chord is traversed: add indicesCurrChord to indicesPerChord
		indicesPerChord.add(indicesCurrChord);
		startIndex = endIndex;	
		}
		return indicesPerChord;
	}


	/**
	 * Returns the size of the largest chord in the tablature.
	 * 
	 * @return
	 */
	// TESTED
	public int getLargestTablatureChord() {
		List<Integer> onsetsPerChord = getNumberOfNotesPerChord();
		return Collections.max(onsetsPerChord);
	}


	/**
	 * Converts the TabSymbol at the given onsetIndex into a Note.
	 * 
	 * @param argBtp The basicTabSymbolProperties
	 * @param noteIndex The index of the onset in the Tablature
	 * @return
	 */
	public static Note convertTabSymbolToNote(Integer[][] argBtp, int noteIndex) {
		// 1. Extract the tabSymbol's gridY (pitch), minimum duration, and onset time
		int tabSymbolPitch = argBtp[noteIndex][Tablature.PITCH];
		int tabSymbolMinimumDuration = argBtp[noteIndex][Tablature.MIN_DURATION];
		int tabSymbolOnsetTime = argBtp[noteIndex][Tablature.ONSET_TIME];

		// 2. Convert ints into Rationals 
//		ScorePitch noteScorePitch = new ScorePitch(tabSymbolPitch);
		Rational noteMinimumDuration = new Rational(tabSymbolMinimumDuration, SMALLEST_RHYTHMIC_VALUE.getDenom()); 
		Rational noteOnsetTime = new Rational(tabSymbolOnsetTime, SMALLEST_RHYTHMIC_VALUE.getDenom());

		// 3. Create a Note with the given pitch, onset time, and minimum duration
		Note note = Transcription.createNote(tabSymbolPitch, noteOnsetTime, noteMinimumDuration);

		return note; 
	}


	/**
	 * Converts the TabSymbol at the given onsetIndex into a Note. Alternative to the method
	 * with the same name.
	 * 
	 * @param onsetIndex The index of the onset in the Tablature
	 * @return
	 */
	public Note convertTabSymbolToNote(int onsetIndex) {
		// 1. Extract the tabSymbol's gridY (pitch), minimum duration, and onset time
		int tabSymbolPitch = basicTabSymbolProperties[onsetIndex][Tablature.PITCH];
		int tabSymbolMinimumDuration = basicTabSymbolProperties[onsetIndex][Tablature.MIN_DURATION];
		int tabSymbolOnsetTime = basicTabSymbolProperties[onsetIndex][Tablature.ONSET_TIME];

		// 2. Convert ints into Rationals 
		Rational noteMinimumDuration = new Rational(tabSymbolMinimumDuration, SMALLEST_RHYTHMIC_VALUE.getDenom()); 
		Rational noteOnsetTime = new Rational(tabSymbolOnsetTime, SMALLEST_RHYTHMIC_VALUE.getDenom());

		// 3. Create a Note with the given pitch, onset time, and minimum duration
		Note note = Transcription.createNote(tabSymbolPitch, noteOnsetTime, noteMinimumDuration);

		return note; 
	}


	/**
	 * Gets the minimum duration of the given Note.
	 * 
	 * NB Tablature case only. 
	 * 
	 * @param basicTabSymbolProperties
	 * @param note
	 * @return
	 */
	// TESTED
	public static Rational getMinimumDurationOfNote(Integer[][] argBasicTabSymbolProperties, Note note) {

		int pitch = note.getMidiPitch();
		Rational metricTime = note.getMetricTime(); 
		int metricTimeAsInt =	
			metricTime.getNumer() * (Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom() / metricTime.getDenom());
		Rational minimumDuration = null;

		// Find the the note with pitch and metricTimeAsInt. In case there are two such notes (i.e., in case of a 
		// unison), their minimum duration will be the same as they are in the same chord
		for (Integer[] currentBtp : argBasicTabSymbolProperties) {
			if (currentBtp[Tablature.ONSET_TIME] == metricTimeAsInt && currentBtp[Tablature.PITCH] == pitch) {
				minimumDuration = new Rational(currentBtp[Tablature.MIN_DURATION], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
				break;
			}
		}
		return minimumDuration;
	}


	/**
	 * Gets the pitches in the chord at the given index. Element 0 of the List represents the lowest note's pitch,
	 * element 1 the second-lowest note's, etc. If the chord contains course crossings, the List will not be in
	 * numerical order.
	 * 
	 * @param basicTabSymbolPropertiesChord
	 * @return
	 */
	// TESTED
	public List<Integer> getPitchesInChord(int chordIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();

		Integer[][] basicTabSymbolPropertiesChord = getBasicTabSymbolPropertiesChord(chordIndex);	
		for (Integer[] currentBasicTabSymbolProperties : basicTabSymbolPropertiesChord) {
			pitchesInChord.add(currentBasicTabSymbolProperties[Tablature.PITCH]);
		}
		return pitchesInChord;
	}


	/**
	 * Gets the pitches in the chord. Element 0 of the List represents the lowest note's pitch, element 1 the
	 * second-lowest note's, etc.
	 * 
	 * NB: If the chord contains course crossings, the List will not be in numerical order.
	 * 
	 * @param btp
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED
	public static List<Integer> getPitchesInChord(Integer[][] btp, int lowestNoteIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();	
		int chordSize = btp[lowestNoteIndex][Tablature.CHORD_SIZE_AS_NUM_ONSETS];
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
			Integer[] currentBasicTabSymbolProperties = btp[i];
			int currentPitch = currentBasicTabSymbolProperties[Tablature.PITCH];
			pitchesInChord.add(currentPitch);
		}
		return pitchesInChord;
	}


	/**
	 * Lists all the unique chords in the tablature in the order they are encountered. Each chord is 
	 * represented as a series of pitches, with the lowest pitch listed first. Chords with course crossings 
	 * are therefore rearranged so that their pitches are sorted numerically.
	 * 
	 * @return
	 */
	// TESTED
	public List<List<Integer>> generateChordDictionary() {
		List<List<Integer>> chordDictionary = new ArrayList<List<Integer>>();

		// For each chord 
		List<List<TabSymbol>> tablatureChords = getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			// List the pitches in the chord
			List<Integer> pitchesInCurrentChord = getPitchesInChord(i);
			// Sort the pitches numerically
			Collections.sort(pitchesInCurrentChord);
			// If chordDictionary does not contain pitchesInCurrentChord: add
			if (!chordDictionary.contains(pitchesInCurrentChord)) {
				chordDictionary.add(pitchesInCurrentChord);
			}
		}
		return chordDictionary;
	}


	/**
	 * Returns a reversed version of the Tablature.
	 * 
	 * @param tab
	 * @return
	 */
	public static Tablature reverse(Tablature tab) {
		return new Tablature(reverseEncoding(tab), tab.getNormaliseTuning());
	}


	/**
	 * Returns a deornamented version of the Tablature.
	 * 
	 * @param tab
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental.
	 * @return
	 */
	public static Tablature deornament(Tablature tab, Rational dur) {
		return new Tablature(deornamentEncoding(tab, rationalToIntDur(dur)), tab.getNormaliseTuning());
	}
	
	
	public static Tablature stretch(Tablature tab, int factor) {
		return new Tablature(stretchEncoding(tab, factor), tab.getNormaliseTuning());
	}


	/**
	 * Split the header and the encoding. Removes all comments and the EBI from the encoding.
	 * 
	 * @return
	 */
	// TESTED
	String[] splitHeaderAndEncoding() {
		// Separate header and encoding
		String raw = getEncoding().getRawEncoding();
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
	 * Returns a list of all tabwords in the given encoding; the SBI are kept in place (i.e,
	 * form separate tabwords). In the list returned, a rhythm symbol (the last active one) is 
	 * assigned to each tabword that is lacking one. 
	 * 
	 * @param enc
	 * @return
	 */
	// TESTED
	static List<String> getTabwords(String enc) {
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
	 * Recombines the given list of tabwords into a String, adding a line break after each 
	 * constant music symbol (space or barline), as well as after each system break indicator.
	 * @param tabwords
	 * @return
	 */
	private static String recombineTabwords(List<String> tabwords) {
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


	private static boolean tabwordIsBarlineOrSBI(String tabword) {
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
	 * Reverses the encoding.
	 * 
	 * @param tab
	 * @return
	 */
	// TESTED
	public static Encoding reverseEncoding(Tablature tab) {
		String[] hAndE = tab.splitHeaderAndEncoding();
		String header = hAndE[0];
		String enc = hAndE[1];

		// 1. Adapt header
		// Reverse meterInfo information 
		int startInd = header.indexOf("METER_INFO:") + "METER_INFO:".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, startInd));
		List<Integer[]> copyOfMeterInfo = new ArrayList<>();
		for (Integer[] in : tab.getMeterInfo()) {
			copyOfMeterInfo.add(Arrays.copyOf(in, in.length));
		}
		Integer[] last = copyOfMeterInfo.get(copyOfMeterInfo.size()-1);
		int numBars = last[last.length-1];
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
		List<String> tabwords = getTabwords(enc);
		Collections.reverse(tabwords);
		return new Encoding(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
			SymbolDictionary.END_BREAK_INDICATOR, true);
	}


	/**
	 * Removes all sequences of single-note events shorter than the given duration from the
	 * encoding, and lengthens the duration of the event preceding the sequence by the total 
	 * length of the removed sequence.
	 * 
	 * @param tab
	 * @param dur In multiples of 
	 * @return
	 */
	// TESTED
	public static Encoding deornamentEncoding(Tablature tab, int dur) {
		String[] hAndE = tab.splitHeaderAndEncoding();
		String header = hAndE[0];
		String enc = hAndE[1];

		// 1. Adapt tabwords
		List<String> tabwords = getTabwords(enc);
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
				// If the tabword is the first after a sequence of one or more ornamental notes
				// (i.e., it does not meet the if conditions above but pre != null)
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


	public static Encoding stretchEncoding(Tablature tab, double factor) {
		String[] hAndE = tab.splitHeaderAndEncoding();
		String header = hAndE[0];
		String enc = hAndE[1];

		// 1. Adapt header
		// Reverse meterInfo information 
		int startInd = header.indexOf("METER_INFO:") + "METER_INFO:".length();
		String origMeterInfo = header.substring(startInd, 
			header.indexOf(SymbolDictionary.CLOSE_INFO_BRACKET, startInd));
		List<Integer[]> copyOfMeterInfo = new ArrayList<>();
		List<Integer[]> mi = tab.getMeterInfo();
		String stretchedMeterInfo = "";
		for (int i = 0; i < mi.size(); i++) {
			Integer[] in = mi.get(i);
			if (i > 0) {
				in[2] = mi.get(i-1)[3] + 1;
			}
			in[3] = (int) (in[3] * factor);
			stretchedMeterInfo += in[0] + "/" + in[1] + " (" + in[2] + "-" + in[3] + ")";
			if (i < copyOfMeterInfo.size()-1) {
				stretchedMeterInfo += "; ";
			}
		}
		header = header.replace(origMeterInfo, stretchedMeterInfo);
		
		// 2. Adapt tabwords
		List<String> tabwords = getTabwords(enc);
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
		System.out.println(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
				SymbolDictionary.END_BREAK_INDICATOR);
		
		// 3. Recombine
		return new Encoding(header + "\r\n\r\n" + recombineTabwords(tabwords) + 
			SymbolDictionary.END_BREAK_INDICATOR, true);
	}

}
