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
import tbp.RhythmSymbol;
import tbp.SymbolDictionary;
import tbp.TabSymbol;
import tbp.TabSymbolSet;
import tools.ToolBox;

public class Tablature implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public static final int MI_NUM = 0;
	public static final int MI_DEN = 1;
	public static final int MI_FIRST_BAR = 2;
	public static final int MI_LAST_BAR = 3;
	private static final int MI_DIM = 4;
	public static final int MI_SIZE = 5; // NB this must not be the same as Transcription.MI_SIZE

	public Tablature() {
	}


	public Tablature(File argFile, boolean argNormaliseTuning) { 	
		Encoding e = new Encoding(argFile);
//		e.setName(argFile.getName().substring(0, (argFile.getName().length() - ".tbp".length())));
		setEncoding(e);
		createTablature(argNormaliseTuning);
	}


	public Tablature(Encoding argEncoding, boolean argNormaliseTuning) {
		setEncoding(argEncoding);
		createTablature(argNormaliseTuning);
	}


	private void createTablature(boolean argNormaliseTuning) {
		setPieceName(getEncoding().getName());
		setMeterInfo(); // needs infoAndSettings
		setBasicTabSymbolProperties(); // needs infoAndSettings, listOfSymbols, and listOfStatistics
		if (argNormaliseTuning) {
			normaliseTuning(); // needs basicTabSymbolProperties
		}
		setNormaliseTuning(argNormaliseTuning);
		setTablatureChords();
		setNumberOfNotesPerChord(); // needs tablatureChords
	}


	private Tablature(File argFile, boolean normaliseTuning, boolean bla) { 
		setEncoding(new Encoding(argFile));
		setPieceName(argFile.getName().substring(0, (argFile.getName().length() - ".tbp".length())));
		setFile(argFile);
		setMeterInfo(); // needs infoAndSettings		
		setBasicTabSymbolProperties(); // needs infoAndSettings, listOfSymbols, and listOfStatistics
		if (normaliseTuning) {
			normaliseTuning(); // needs basicTabSymbolProperties
		}
		setTablatureChords();
		setNumberOfNotesPerChord(); // needs tablatureChords
	}


	void setEncoding(Encoding e) {
		encoding = e;
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
	 * Given the list of original (unreduced) meters and the reductions, calculates the 
	 * meterInfo.
	 * 
	 * @param originalMeterInfo
	 * @param diminutions
	 * @return A <code>List<String[]></code> whose elements represent the meters in 
	 * the piece. Each element of this list contains:<br>
	 * <ul>
	 * <li> as element 0: the numerator of the meter (adapted according to the diminution)</li>
	 * <li> as element 1: the denominator of the meter (adapted according to the diminution)</li>
	 * <li> as element 2: the first bar in the meter </li>
	 * <li> as element 3: the last bar in the meter </li>
	 * <li> as element 4: the diminution for the meter </li>
	 * </ul>
	 * An anacrusis bar will be denoted with bar numbers 0-0.
	 */
	// TESTED
	List<Integer[]> createMeterInfo(/*List<Integer[]> originalMeterInfo,*/ 
		List<Integer> diminutions) {
		List<Integer[]> originalMeterInfo = getOriginalMeterInfo();
		List<Integer[]> mi = new ArrayList<>();
		// For each meter
		for (int i = 0; i < originalMeterInfo.size(); i++) {
			Integer[] currMeterInfo = new Integer[MI_SIZE];
			for (int j = 0; j < originalMeterInfo.get(i).length; j++) {
				currMeterInfo[j] = originalMeterInfo.get(i)[j];
			}						
			// Diminution
			int dim = diminutions.get(i);
			if (dim != 1) {
				Rational newMeter;
				// times 2:	2/2 --> (2*2)/2 = 2/1
				// times 4:	2/2 --> (2*4)/2 = 4/1
				if (dim > 0) {
					newMeter = new Rational(currMeterInfo[MI_NUM] * dim, currMeterInfo[MI_DEN]);
				}
				// divided by 2: 2/2 --> (2/2)/2 = 1/2 
				// divided by 4: 2/2 --> (2/4)/2 = 1/4
				else {
					newMeter = 
						new Rational(currMeterInfo[MI_NUM], Math.abs(dim)).div(currMeterInfo[MI_DEN]);
				}
				newMeter.reduce();
				currMeterInfo[MI_NUM] = newMeter.getNumer();
				currMeterInfo[MI_DEN] = newMeter.getDenom();
			}
			currMeterInfo[MI_DIM] = dim;
			mi.add(currMeterInfo);
		}
		return mi;
	}


	public static int getDiminution(int bar, List<Integer[]> mi) {
		int diminution = 1; 
		for (Integer[] in : mi) {
			if (bar >= in[MI_FIRST_BAR] && bar <= in[MI_LAST_BAR]) {
				diminution = in[4];
				break;
			}
		}
		return diminution;
	}


	void setMeterInfo() {
		meterInfo = createMeterInfo(/*getOriginalMeterInfo(),*/ getDiminutions());
	}


	/**
	 * Returns a list whose elements represent the meters in the piece. Each element contains:<br>
	 * <ul>
	 * <li> as element 0: the numerator of the meter (adapted according to the diminution)</li>
	 * <li> as element 1: the denominator of the meter (adapted according to the diminution)</li>
	 * <li> as element 2: the first bar in the meter </li>
	 * <li> as element 3: the last bar in the meter </li>
	 * <li> as element 4: the diminution for the meter </li>
	 * </ul>
	 * An anacrusis bar will be denoted with bar numbers 0-0.
	 * 
	 */
	public List<Integer[]> getMeterInfo() {
		return meterInfo;
	}


	/**
	 * Gets the original (unreduced) meterInfo.
	 * 
	 * @return A list, containing, for each meter<break>
	 *         <ul>
	 *         <li> as element 0: the numerator of the meter </li>
	 * 		   <li> as element 1: the denominator of the meter </li>
	 *         <li> as element 2: the first bar in the meter </li>
	 *         <li> as element 3: the last bar in the meter </li>
	 *         </ul>
	 */
	// TESTED
	List<Integer[]> getOriginalMeterInfo() {
		List<Integer[]> originalMeterInfo = new ArrayList<>();

		String[] originalMeters = 
			getEncoding().getInfoAndSettings().get(Encoding.METER_IND).split(";");		
		for (int i = 0; i < originalMeters.length; i++) {
			Integer[] currentMeterInfo = new Integer[MI_SIZE - 1];
			String currInfo = originalMeters[i].trim();
			// Meter
			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
			currentMeterInfo[MI_NUM] = Integer.parseInt(currMeter.split("/")[0].trim());
			currentMeterInfo[MI_DEN] = Integer.parseInt(currMeter.split("/")[1].trim());
			// Bar number(s)
			String currBars = 
				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();
			// If the meter is only for a single bar
			if (!currBars.contains("-")) {
				currentMeterInfo[MI_FIRST_BAR] = Integer.parseInt(currBars.trim());
				currentMeterInfo[MI_LAST_BAR] = Integer.parseInt(currBars.trim());
			}
			// If the meter is for more than one bar
			else {
				currentMeterInfo[MI_FIRST_BAR] = Integer.parseInt(currBars.split("-")[0].trim());
				currentMeterInfo[MI_LAST_BAR] = Integer.parseInt(currBars.split("-")[1].trim());
			}
			originalMeterInfo.add(currentMeterInfo);
		}
		return originalMeterInfo;
	}


	/**
	 * Gets the reductions per meter.
	 * 
	 * @return
	 */
	// TESTED
	public List<Integer> getDiminutions() {
		List<Integer> diminutions = new ArrayList<>();
		String diminutionsStr = getEncoding().getInfoAndSettings().get(Encoding.DIMINUTION_IND);
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
			int prevNumBars = (prevMeterInfo[MI_LAST_BAR] - prevMeterInfo[MI_FIRST_BAR]) + 1;
			Rational prevMeter = new Rational(prevMeterInfo[MI_NUM], prevMeterInfo[MI_DEN]);
			// The metric time (duration of the previous bars) for beat zero equals 
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
		List<List<String>> symbols = getEncoding().getListsOfSymbols();
		List<List<Integer>> stats = getEncoding().getListsOfStatistics();
		TabSymbolSet tss = getEncoding().getTabSymbolSet();
		List<String> listOfTabSymbols = symbols.get(Encoding.TAB_SYMBOLS_IND);
		List<String> listOfAllEvents = symbols.get(Encoding.ALL_EVENTS_IND);
		List<Integer> isTabSymbolEvent = stats.get(Encoding.IS_TAB_SYMBOL_EVENT_IND); 
		List<Integer> sizeOfEvents = stats.get(Encoding.SIZE_OF_EVENTS_IND);
		List<Integer> horizontalPositionOfTabSymbols = stats.get(Encoding.HORIZONTAL_POSITION_IND);
		List<Integer> verticalPositionOfTabSymbols = stats.get(Encoding.VERTICAL_POSITION_IND);
		List<Integer> durationOfTabSymbols = stats.get(Encoding.DURATION_IND);
		List<Integer> gridXOfTabSymbols = stats.get(Encoding.GRID_X_IND);
		List<Integer> gridYOfTabSymbols = stats.get(Encoding.GRID_Y_IND);  	
		List<Integer> horizontalPositionInTabSymbolEventsOnly = 
			stats.get(Encoding.HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND);

		List<List<Integer>> scaled = 
			adaptToDiminutions(durationOfTabSymbols, gridXOfTabSymbols, getDiminutions(), 
			getOriginalMeterInfo());
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
		TabSymbolSet tss = getEncoding().getTabSymbolSet();

		List<TabSymbol> currentChord = new ArrayList<TabSymbol>();
		List<String> listOfTabSymbols = 
			getEncoding().getListsOfSymbols().get(Encoding.TAB_SYMBOLS_IND);
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

		Tuning originalTuning = getEncoding().getTunings()[Encoding.ENCODED_TUNING_IND];
		
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

		Tuning[] tunings = getEncoding().getTunings();

		Tuning originalTuning = tunings[Encoding.ENCODED_TUNING_IND];
		
//		for (Tuning t : Tuning.values()) {
//			if (t.equals(originalTuning)) {
//				transpositionInterval = -(t.getTransposition());
//				break;
//			}
//		}
		
		if (originalTuning.isAvallee()) {
			tunings[Encoding.NEW_TUNING_IND] = Tuning.G_AVALLEE;
		}
		else {
			tunings[Encoding.NEW_TUNING_IND] = Tuning.G;
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
		List<List<Integer>> stats = getEncoding().getListsOfStatistics();
		List<Integer> oldGridYOfTabSymbols = stats.get(Encoding.GRID_Y_IND);
		List<Integer> newGridYOfTabSymbols = new ArrayList<Integer>(); 
		for (int i : oldGridYOfTabSymbols) {
			newGridYOfTabSymbols.add(i + transpositionInterval);
		}
		stats.set(Encoding.GRID_Y_IND, newGridYOfTabSymbols);

		// Reset the pitches in basicTabSymbolProperties
		for (Integer[] in : basicTabSymbolProperties) {
			in[PITCH] = in[PITCH] + transpositionInterval;
		}  	
	}


	/**
	 * Gets the metric position of the note at the onset time. Returns a Rational[] with 
	 *   <ul>
	 *   <li>as element 0: the bar number (whose denominator will always be 1);</li>
	 *   <li>as element 1: the position within the bar, reduced and starting at 0/x (where x is the common denominator,
	 *                 i.e., the product of the denominator of metricTime and the largest meter denominator).</li>
	 *   </ul>
	 * If there is an anacrusis: if metricTime falls within the anacrusis, the bar number returned will be 0,
	 * and the position within the bar will be the position as if the anacrusis were a full bar.
	 * <br><br>
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
		if (argMeterInfo.get(0)[MI_FIRST_BAR] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] in : argMeterInfo) {
			if (in[MI_DEN] > largestMeterDenom) {
				largestMeterDenom = in[MI_DEN];
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
			Integer[] currentMeter = 
				new Integer[]{argMeterInfo.get(i)[MI_NUM], argMeterInfo.get(i)[MI_DEN]};
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
			int numBarsInCurrentMeter = 
				(argMeterInfo.get(i)[MI_LAST_BAR] - argMeterInfo.get(i)[MI_FIRST_BAR]) + 1;
			// Determine the metric time of the next meter change point and add it to meterChangePointsMetricTimes
			// NB: When creating the new Rational do not use add() to avoid automatic reduction
			Rational currentMeter = metersInLargestDenom.get(i);
			int toAdd = numBarsInCurrentMeter * currentMeter.getNumer();
			meterChangePointsMetricTimes.add(new Rational(meterChangePointsMetricTimes.get(i - startIndex).getNumer() +
				toAdd, commonDenom));	 	
		}

		// 4. Determine the bar number and the position in the bar, and set metricPosition
		// a. If metricTime falls within the anacrusis (the if can only be satisfied if there
		// is an anacrusis)
		if (metricTimeInLargestDenom.getNumer() < meterChangePointsMetricTimes.get(0).getNumer()) {
			// Determine the position in the bar as if it were a full bar 
			Rational lengthAnacrusis = metersInLargestDenom.get(0);
			Rational meterFirstBar = metersInLargestDenom.get(1);
			int toAdd = meterFirstBar.getNumer() - lengthAnacrusis.getNumer();
			Rational positionInBar = 
				new Rational(metricTimeInLargestDenom.getNumer() + toAdd, commonDenom);
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
					int currentBarNumber = argMeterInfo.get(i + startIndex)[MI_FIRST_BAR] + numberOfBarsToAdd;
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
	 * Returns all onset times, including those of rests.
	 * 
	 * NB: individual, successive rests are combined into single rests; any rest added after
	 * the final chord are omitted. Rests before the first chord are included.
	 * 
	 * @return For each note or rest event, a Rational[] containing<br>
	 *         <ul>
	 *         <li>as element 0: the onset time</li>
	 *         <li>as element 1: whether (1) or not (0) it is a note event </li>
	 *         </ul>
	 */
	// TESTED
	public List<Rational[]> getAllOnsetTimesRestsInclusive() {
		List<Rational[]> allOnsetTimes = new ArrayList<>();
		
		List<Rational[]> otmd = getAllOnsetTimesAndMinDurations();
		// Check for rests before first chord; if there are, add 0 as first onset time
		// NB This assumes that anacruses do not happen
		if (otmd.get(0)[0].isGreater(Rational.ZERO)) {
			allOnsetTimes.add(new Rational[]{Rational.ZERO, Rational.ZERO});
		}
		for (int i = 0; i < otmd.size(); i++) {
			Rational currOns = otmd.get(i)[0];
			allOnsetTimes.add(new Rational[]{currOns, Rational.ONE});
			// For all but last onset: check for possible rests between curr and next onset
			if (i < otmd.size()-1) {
				// If nextOnset does not follow immediately: add rest onset in between
				Rational currOffs = currOns.add(otmd.get(i)[1]); 
				Rational nextOns = otmd.get(i+1)[0];
				if (currOffs.isLess(nextOns)) {
					allOnsetTimes.add(new Rational[]{currOffs, Rational.ZERO});
				}
			}
		}
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
	 * 6th c., 12th fr. - 5th c., 6th fr. - 4th c., open), but will not likely occur in practice.
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
		return new Tablature(
			tab.getEncoding().reverseEncoding(tab.getMeterInfo()), 
			tab.getNormaliseTuning());
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
		return new Tablature(
			tab.getEncoding().deornamentEncoding(rationalToIntDur(dur)), 
			tab.getNormaliseTuning());
	}


	public static Tablature stretch(Tablature tab, int factor) {
		return new Tablature(
			tab.getEncoding().stretchEncoding(tab.getMeterInfo(), factor), 
			tab.getNormaliseTuning());
	}


	/**
	 * Gets, for each triplet in the Tablature, the onset time of the opening event and the
	 * offset time of the closing event.
	 *  
	 * @return <code>null</code> if there are no triplets; otherwise a list containing, for 
	 *         each triplet, a Rational[] containing <br>
	 *         <ul>
	 *         <li>as element 0: the onset time of the triplet opening event</li>
	 *         <li>as element 1: the onset time of the triplet closing event</li>
	 *         <li>as element 2: the triplet unit, as RhythmSymbol duration divided by 1 
	 *             (i.e., semibrevis = 48/1; minim = 24/1; semiminim = 12/1; etc.)</li>
	 *         </ul>
	 */
	// TESTED
	public List<Rational[]> getTripletOnsetPairs() {
		
		List<Rational[]> pairs; 
		if (!getEncoding().containsTriplets()) {
			pairs = null;
		}
		else {
			pairs = new ArrayList<>();
			TabSymbolSet tss = getEncoding().getTabSymbolSet();
			List<String> events = getEncoding().getEvents();
			List<Rational[]> onsetTimes = getAllOnsetTimesRestsInclusive();

			// 1. Align events and onsetTimes
			// Remove all SBI
			events.removeIf(t -> t.equals(SymbolDictionary.SYSTEM_BREAK_INDICATOR));
			// Combine all successive rest events
			events = Encoding.combineSuccessiveRestEvents(events);
			// Remove all events that are neither a chord nor a rest
			List<String> tmp = new ArrayList<>();
			for (String t : events) {
				String[] split = t.split("\\" + SymbolDictionary.SYMBOL_SEPARATOR);
				// Remove space from split
				if (split[split.length-1].equals(ConstantMusicalSymbol.SPACE.getEncoding())) {
					split = Arrays.copyOf(split, split.length-1);
				}
				// Add event if first element is a RS or last element is a TS
				if (RhythmSymbol.getRhythmSymbol(split[0]) != null ||
						TabSymbol.getTabSymbol(split[(split.length)-1], tss) != null) {
					tmp.add(t);
				}
			}
			events = tmp;

			// 2. Get the start and end onset times of triplet events
			Rational[] pair = new Rational[]{null, null, null};
			int dur = 0;
			for (int i = 0; i < events.size(); i++) {
				String curr = events.get(i);
				Rational ons = onsetTimes.get(i)[0];
				ons.reduce();
				if (curr.startsWith(RhythmSymbol.tripletIndicator)) {
					String rs = curr.substring(0, curr.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));
					RhythmSymbol nonTripletVar = RhythmSymbol.getNonTripletVariant(rs);
					dur += nonTripletVar.getDuration();
					// Triplet open chord: add to pair
					if (curr.contains(RhythmSymbol.tripletOpen)) {
						pair[0] = ons;	
					}
					// Triplet close chord: complete pair, add it to list, and reset it
					else if (curr.contains(RhythmSymbol.tripletClose)) {
						pair[1] = ons;
						pair[2] = new Rational(dur/3, 1);
						pairs.add(pair);
						pair = new Rational[]{null, null, null};
						dur = 0;
					}
				}
			}
		}		
		return pairs;
	}

}
