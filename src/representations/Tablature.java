package representations;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	private static final long serialVersionUID = 1L;

	public static final int MAXIMUM_NUMBER_OF_NOTES = 5;
	public static final Rational SMALLEST_RHYTHMIC_VALUE = 
		new Rational(RhythmSymbol.semifusa.getDuration()/3, RhythmSymbol.brevis.getDuration());
	public static final int SRV_DEN = SMALLEST_RHYTHMIC_VALUE.getDenom();

	public static final int PITCH = 0;
	public static final int COURSE = 1;
	public static final int FRET = 2;
	public static final int ONSET_TIME = 3;
	public static final int MIN_DURATION = 4;
	public static final int MAX_DURATION = 5;
	public static final int CHORD_SEQ_NUM = 6;
	public static final int CHORD_SIZE_AS_NUM_ONSETS = 7;
	public static final int NOTE_SEQ_NUM = 8;
	public static final int TAB_EVENT_SEQ_NUM = 9;

	// MI_NUM, MI_DEN, MI_FIRST_BAR, MI_LAST_BAR, MI_NUM_MT_FIRST_BAR, and MI_DEN_MT_FIRST_BAR 
	// are the same as for a Transcription, and are defined there
	public static final int MI_DIM = 6;
	private static final int MI_SIZE = 7;
	
	private Encoding encoding;
	private String pieceName;
	private List<Integer[]> undiminutedMeterInfo;
	private List<Integer> diminutions;
	private List<Integer[]> meterInfo;	
	private List<Integer[]> diminutionPerBar;
	private Integer[][] basicTabSymbolProperties;
	private boolean normaliseTuning;	
	private List<List<TabSymbol>> tablatureChords;
	private List<Integer> numberOfNotesPerChord;
	private List<Integer[]> keyInfo;
	private File file;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Tablature() {
	}


	public Tablature(File argFile, boolean argNormaliseTuning) {
		init(new Encoding(argFile), argNormaliseTuning);
	}


	public Tablature(Encoding argEncoding, boolean argNormaliseTuning) {
		init(argEncoding, argNormaliseTuning);
	}


	private void init(Encoding encoding, boolean argNormaliseTuning) {
		// Each of the following methods needs one or more of the preceding ones
		setEncoding(encoding);
		setPieceName(); 
		setUndiminutedMeterInfo();
		setDiminutions();
		setMeterInfo();
		setDiminutionPerBar();
		setBasicTabSymbolProperties();
		setNormaliseTuning(argNormaliseTuning);
		setTablatureChords();
		setNumberOfNotesPerChord();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setEncoding(Encoding e) {
		encoding = e;
	}


	void setPieceName() {
		pieceName = getEncoding().getName(); 
	}


	void setUndiminutedMeterInfo() {
		undiminutedMeterInfo = makeUndiminutedMeterInfo();
	}


	// TESTED
	List<Integer[]> makeUndiminutedMeterInfo() {
		List<Integer[]> undiminutedMeterInfo = new ArrayList<>();

		String[] undiminutedMeters = 
			getEncoding().getInfoAndSettings().get(Encoding.METER_IND).split(";");		
		Rational prevMeterAsRat = Rational.ZERO;
		int prevNumBars = 0;
		Rational prevMt = Rational.ZERO;
		for (int i = 0; i < undiminutedMeters.length; i++) {
			Integer[] currentMeterInfo = new Integer[MI_SIZE - 1];
			String currInfo = undiminutedMeters[i].trim();
			// 1. Meter
			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
			int currNum = Integer.parseInt(currMeter.split("/")[0].trim());
			int currDen = Integer.parseInt(currMeter.split("/")[1].trim());
			currentMeterInfo[Transcription.MI_NUM] = currNum;
			currentMeterInfo[Transcription.MI_DEN] = currDen;
			// 2. Bar number(s)
			int currNumBars = 0;
			String currBars = 
				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();
			// If the meter is only for a single bar
			if (!currBars.contains("-")) {
				currentMeterInfo[Transcription.MI_FIRST_BAR] = Integer.parseInt(currBars.trim());
				currentMeterInfo[Transcription.MI_LAST_BAR] = Integer.parseInt(currBars.trim());
				currNumBars = 1;
			}
			// If the meter is for more than one bar
			else {
				int firstBar = Integer.parseInt(currBars.split("-")[0].trim());
				int lastBar = Integer.parseInt(currBars.split("-")[1].trim());
				currentMeterInfo[Transcription.MI_FIRST_BAR] = firstBar;
				currentMeterInfo[Transcription.MI_LAST_BAR] = lastBar;
				currNumBars = (lastBar-firstBar) + 1;
			}
			// 3. Metric times
			Rational currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
			currMt.reduce();
			currentMeterInfo[Transcription.MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
			currentMeterInfo[Transcription.MI_DEN_MT_FIRST_BAR] = currMt.getDenom();

			// Add and update
			undiminutedMeterInfo.add(currentMeterInfo);
			prevNumBars = currNumBars;
			prevMt = currMt;
			prevMeterAsRat = new Rational(currNum, currDen);
		}
		return undiminutedMeterInfo;
	}


	void setDiminutions() {
		diminutions = makeDiminutions();
	}


	// TESTED
	List<Integer> makeDiminutions() {
		List<Integer> diminutions = new ArrayList<>();
		String diminutionsStr = getEncoding().getInfoAndSettings().get(Encoding.DIMINUTION_IND);
		for (String s : diminutionsStr.split(";")) {
			diminutions.add(Integer.parseInt(s.trim()));
		}
		return diminutions;
	}


	void setMeterInfo() {
		meterInfo = makeMeterInfo();
	}


	// TESTED
	List<Integer[]> makeMeterInfo() {
		List<Integer[]> mi = new ArrayList<>();
		
		List<Integer[]> undiminutedMeterInfo = getUndiminutedMeterInfo();
		List<Integer> diminutions = getDiminutions();
		// For each meter
		Rational prevMeterAsRat = Rational.ZERO;
		int prevNumBars = 0;
		Rational prevMt = Rational.ZERO;
		for (int i = 0; i < undiminutedMeterInfo.size(); i++) {
			Integer[] currMeterInfo = new Integer[MI_SIZE];
			for (int j = 0; j < undiminutedMeterInfo.get(i).length; j++) {
				currMeterInfo[j] = undiminutedMeterInfo.get(i)[j];
			}
			int currNum = currMeterInfo[Transcription.MI_NUM];
			int currDen = currMeterInfo[Transcription.MI_DEN];
			Rational currMt = 
				new Rational(currMeterInfo[Transcription.MI_NUM_MT_FIRST_BAR], 
				currMeterInfo[Transcription.MI_DEN_MT_FIRST_BAR]);
			int currNumBars = (currMeterInfo[Transcription.MI_LAST_BAR] - 
				currMeterInfo[Transcription.MI_FIRST_BAR]) + 1;
			int currDim = diminutions.get(i);
			// 1. Meter
			Rational newMeter = diminuteMeter(new Rational(currNum, currDen), currDim);

			currMeterInfo[Transcription.MI_NUM] = newMeter.getNumer();
			currMeterInfo[Transcription.MI_DEN] = newMeter.getDenom();
			// 2. Metric time
			currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
			currMt.reduce();
			currMeterInfo[Transcription.MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
			currMeterInfo[Transcription.MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
			currMeterInfo[MI_DIM] = currDim;

			// Add and update
			mi.add(currMeterInfo);
			prevNumBars = currNumBars;
			prevMt = currMt;
			prevMeterAsRat = newMeter;
		}
		return mi;
	}


	void setDiminutionPerBar() {
		diminutionPerBar = makeDiminutionPerBar();
	}


	// TESTED
	List<Integer[]> makeDiminutionPerBar() {
		List<Integer[]> dimPerBar = new ArrayList<>();
		List<Integer[]> mi = getMeterInfo();
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, Transcription.MI_FIRST_BAR);
		// In case of an anacrusis, firstBar == 0
		int firstBar = mi.get(0)[Transcription.MI_FIRST_BAR];
		int lastBar = mi.get(mi.size()-1)[Transcription.MI_LAST_BAR];
		int currDiminution = 0;
		for (int bar = firstBar; bar <= lastBar; bar++) {
			if (meterChangeBars.contains(bar)) {
				currDiminution = mi.get(meterChangeBars.indexOf(bar))[MI_DIM];
			}
			dimPerBar.add(new Integer[]{bar, currDiminution});
		}
		return dimPerBar;
	}


	void setBasicTabSymbolProperties() {
		basicTabSymbolProperties = makeBasicTabSymbolProperties();
	}


	void setBasicTabSymbolProperties(Integer[][] in) {
		basicTabSymbolProperties = in;
	}


	// TESTED
	Integer[][] makeBasicTabSymbolProperties() {
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
			adaptToDiminutions(durationOfTabSymbols, gridXOfTabSymbols, makeDiminutions(),
			getUndiminutedMeterInfo());
		durationOfTabSymbols = scaled.get(0);
		gridXOfTabSymbols = scaled.get(1);

		Integer[][] btp = new Integer[listOfTabSymbols.size()][10];
		for (int i = 0; i < btp.length; i++) {
			TabSymbol currentTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			// 0. Pitch
			btp[i][PITCH] = gridYOfTabSymbols.get(i);
			// 1. Course
			btp[i][COURSE] = currentTabSymbol.getCourse();
			// 2. Fret
			btp[i][FRET] = currentTabSymbol.getFret();
			// 3. Onset time
			int currentOnsetTime = gridXOfTabSymbols.get(i);
			btp[i][ONSET_TIME] = currentOnsetTime;
			// 4. Minimum duration
			int currentMinDuration = durationOfTabSymbols.get(i);
			btp[i][MIN_DURATION] = currentMinDuration;
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
				for (int j = i + 1; j < btp.length; j++) {
					TabSymbol nextTabSymbol = 
						TabSymbol.getTabSymbol(listOfTabSymbols.get(j), tss);
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
			btp[i][MAX_DURATION] = currentMaxDuration;
			// 6. The sequence number of the chord the TS is in
			btp[i][CHORD_SEQ_NUM] = indexOfCurrentChord;
			// 7. The size of the chord the TS is in
			int indexOfCurrentTablatureEvent = horizontalPositionOfTabSymbols.get(i);
			btp[i][CHORD_SIZE_AS_NUM_ONSETS] = sizeOfEvents.get(indexOfCurrentTablatureEvent);
			// 8. The sequence number within the chord of the TS
			btp[i][NOTE_SEQ_NUM] = verticalPositionOfTabSymbols.get(i);
			// 9. The sequence number of the tablature event the TS is in, with ALL events considered 
			btp[i][TAB_EVENT_SEQ_NUM] = horizontalPositionOfTabSymbols.get(i);
		}
		return btp;
	}


	/**
	 * Given the list of diminutions per meter in the given meterInfo, adapts the given 
	 * lists of durations and onsets.
	 * 
	 * @param durationOfTabSymbols
	 * @param gridXOfTabSymbols
	 * @param diminutions
	 * @param undiminutedMeterInfo
	 * @return
	 */
	// TESTED
	static List<List<Integer>> adaptToDiminutions(List<Integer> durationOfTabSymbols, 
		List<Integer> gridXOfTabSymbols, List<Integer> diminutions, List<Integer[]> 
		undiminutedMeterInfo) {
		List<List<Integer>> res = new ArrayList<>();

		// Get the metric time and the adapted metric time of beat 0 for all new meters
		List<Integer> metricTimesBeatZero = new ArrayList<>();
		metricTimesBeatZero.add(0);
		List<Integer> metricTimesBeatZeroAdapted = new ArrayList<>();
		metricTimesBeatZeroAdapted.add(0);
		for (int i = 1 ; i < undiminutedMeterInfo.size(); i++) {
			Integer[] prevMeterInfo = undiminutedMeterInfo.get(i-1);
			int prevNumBars = (prevMeterInfo[Transcription.MI_LAST_BAR] - 
				prevMeterInfo[Transcription.MI_FIRST_BAR]) + 1;
			Rational prevMeter = new Rational(prevMeterInfo[Transcription.MI_NUM], 
				prevMeterInfo[Transcription.MI_DEN]);
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
		List<Integer> adaptedDurationOfTabSymbols = new ArrayList<>();
		List<Integer> adaptedGridXOfTabSymbols = new ArrayList<>();
		int ind = 0;
		int dim = -1;
		int beatZero = -1; 
		int beatZeroAdapted = -1; 
		for (int i = 0; i < durationOfTabSymbols.size(); i++) {
			int currDur = durationOfTabSymbols.get(i);
			int currOnset = gridXOfTabSymbols.get(i);
			// If currOnset is on or past metric beat zero at index ind: determine new metric beat zeros
			if (ind < diminutions.size() && currOnset >= metricTimesBeatZero.get(ind)) {
				dim = diminutions.get(ind);
				beatZero = metricTimesBeatZero.get(ind);
				beatZeroAdapted = metricTimesBeatZeroAdapted.get(ind);
				ind++;
			}
			// Add current duration and onset to lists
			if (dim > 0) {
				adaptedDurationOfTabSymbols.add(currDur*dim);
				adaptedGridXOfTabSymbols.add(beatZeroAdapted + (currOnset-beatZero)*dim);
			}
			else {
				adaptedDurationOfTabSymbols.add(currDur/Math.abs(dim));
				adaptedGridXOfTabSymbols.add(beatZeroAdapted + (currOnset-beatZero)/Math.abs(dim));
			}
		}
		res.add(adaptedDurationOfTabSymbols);
		res.add(adaptedGridXOfTabSymbols);
		return res;
	}


	void setNormaliseTuning(boolean arg) {
		normaliseTuning = normaliseTuning(arg);
	}


	// TESTED
	boolean normaliseTuning(boolean arg) {
		if (arg) {
			Encoding enc = getEncoding();
			// 1. Reset Encoding
			// a. tunings
			Tuning[] tunings = enc.getTunings();
			tunings[Encoding.NEW_TUNING_IND] = 
				tunings[Encoding.ENCODED_TUNING_IND].isAvallee() ? Tuning.G_AVALLEE : Tuning.G;
			enc.setTunings(tunings);

			// b. listsOfStatistics (list of gridY values only)
			int transpositionInterval = getTranspositionInterval();
			List<List<Integer>> stats = enc.getListsOfStatistics();
			List<Integer> newGridYOfTabSymbols = new ArrayList<Integer>(); 
			for (int i : stats.get(Encoding.GRID_Y_IND)) {
				newGridYOfTabSymbols.add(i + transpositionInterval);
			}
			stats.set(Encoding.GRID_Y_IND, newGridYOfTabSymbols);
			enc.setListOfStatistics(stats);
			setEncoding(enc);

			// 2. Reset basicTabSymbolProperties (pitches only)
			Integer[][] btp = getBasicTabSymbolProperties();
			Integer[][] newBtp = Arrays.copyOfRange(btp, 0, btp.length);
			for (int i = 0; i < newBtp.length; i++) {
				newBtp[i][PITCH] = newBtp[i][PITCH] + transpositionInterval;
			}
			setBasicTabSymbolProperties(newBtp);
			return true;
		}
		else {
			return false;
		}
	}


	void setTablatureChords() {
		tablatureChords = makeTablatureChords();
	}


	// TESTED
	List<List<TabSymbol>> makeTablatureChords() {
		List<List<TabSymbol>> tc = new ArrayList<List<TabSymbol>>();
		Integer[][] btp = getBasicTabSymbolProperties();
		TabSymbolSet tss = getEncoding().getTabSymbolSet();

		List<TabSymbol> currentChord = new ArrayList<TabSymbol>();
		List<String> listOfTabSymbols = 
			getEncoding().getListsOfSymbols().get(Encoding.TAB_SYMBOLS_IND);
		TabSymbol firstTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(0), tss);
		int onsetTimeOfFirstTabSymbol = btp[0][ONSET_TIME];
		currentChord.add(firstTabSymbol);
		int onsetTimeOfPreviousTabSymbol = onsetTimeOfFirstTabSymbol;
		for (int i = 1; i < listOfTabSymbols.size(); i++) {
			TabSymbol currentTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			int onsetTimeOfCurrentTabSymbol = btp[i][ONSET_TIME];
			// If currentTabSymbol has the same onset time as previousTabSymbol, they belong to the same chord: add 
			// currentTabSymbol to currentChord
			if (onsetTimeOfCurrentTabSymbol == onsetTimeOfPreviousTabSymbol) {
				currentChord.add(currentTabSymbol);
			}
			// If currentTabSymbol has a different onset time than previousTabSymbol, currentTabSymbol is the first
			// TabSymbol of the next chord. Add currentChord to tablatureChords, create a new currentChord, and add 
			// currentTabSymbol to it.  
			else {
				tc.add(currentChord);
				currentChord = new ArrayList<TabSymbol>();
				currentChord.add(currentTabSymbol);
			}
			onsetTimeOfPreviousTabSymbol = onsetTimeOfCurrentTabSymbol;
		}
		// Add the last chord to tablatureChords
		tc.add(currentChord);
		return tc;
	}


	void setNumberOfNotesPerChord() {
		numberOfNotesPerChord = makeNumberOfNotesPerChord();
	}


	// TESTED
	List<Integer> makeNumberOfNotesPerChord() {
		return getTablatureChords().stream().map(List::size).collect(Collectors.toList());
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public Encoding getEncoding() {
		return encoding;
	};


	public String getPieceName() {
		return pieceName;
	}


	/**
	 * Gets the original (undiminuted) meterInfo.
	 * 
	 * @return A list, containing, for each meter<break>
	 *         <ul>
	 *         <li> as element 0: the numerator of the meter </li>
	 * 		   <li> as element 1: the denominator of the meter </li>
	 *         <li> as element 2: the first (metric) bar in the meter </li>
	 *         <li> as element 3: the last (metric) bar in the meter </li>
	 *         <li> as element 4: the numerator of the metric time of that first bar </li>
	 *         <li> as element 5: the denominator of the metric time of that first bar </li>
	 *         </ul>
	 */
	public List<Integer[]> getUndiminutedMeterInfo() {
		return undiminutedMeterInfo;
	}


	public List<Integer> getDiminutions() {
		return diminutions;
	}


	/**
	 * Gets the meterInfo.
	 * 
	 * @return A list whose elements represent the meters in the piece. Each element contains<br>
	 *         <ul>
	 *         <li> as element 0: the numerator of the meter (adapted according to the diminution)</li>
	 *         <li> as element 1: the denominator of the meter (adapted according to the diminution)</li>
	 *         <li> as element 2: the first (metric) bar in the meter </li>
	 *         <li> as element 3: the last (metric) bar in the meter </li>
	 *         <li> as element 4: the numerator of the metric time of that first bar (adapted according to the diminution)</li>
	 *         <li> as element 5: the denominator of the metric time of that first bar (adapted according to the diminution)</li>
	 *         <li> as element 6: the diminution for the meter </li>
	 *         </ul>
	 *         
	 *         An anacrusis bar will be denoted with bar numbers 0-0.
	 */
	public List<Integer[]> getMeterInfo() {
		return meterInfo;
	}


	public List<Integer[]> getDiminutionPerBar() {
		return diminutionPerBar;
	}


	/**
	 * Gets a two-dimensional array in which the basic TS properties are stored.
	 * 
	 * NB: This method must always be called after Encoding.listsOfStatistics is set.
	 * 
	 * @return An Integer[][], containing for each TS (row) the following properties:<br>
	 *   <ul>
	 *   <li>as element 0: the pitch of the TS (as a MIDI number)</li>
	 *   <li>as element 1: the course of the TS</li>
	 *   <li>as element 2: the fret of the TS</li>
	 *   <li>as element 3: the onset time of the TS (as multiples of SRV_DEN)</li>
	 *   <li>as element 4: the minimum duration of the TS, which is also the duration of the chord it is in 
	 *                     (as multiples of SRV_DEN)</li>
	 *   <li>as element 5: the maximum duration of the TS, i.e., its duration until it is cut off by another TS on the
	 *                     same course (as multiples of SRV_DEN)</li>                
	 *   <li>as element 6: the sequence number of the chord the TS is in</li>
	 *   <li>as element 7: the size of the chord the TS is in</li>
	 *   <li>as element 8: the sequence number within the chord of the TS</li>
	 *   <li>as element 9: the sequence number of the event the TS is in, with ALL events 
	 *                     considered</li>
	 *   </ul>
	 */
	public Integer[][] getBasicTabSymbolProperties() {
		return basicTabSymbolProperties;
	}


	public boolean getNormaliseTuning() {
		return normaliseTuning;
	}


	/** 
	 * Gets a List in which all TS in the Tablature are arranged in chords. The Tablature 
	 * is traversed from left to right, and the chords themselves are arranged starting 
	 * with the lowest-string TS.
	 *  
	 * NB: Rest events are not included in the returned list. 
	 */
	public List<List<TabSymbol>> getTablatureChords() {
		return tablatureChords;
	}


	/**
	 * Gets a list containing the number of notes in each Tablature chord.
	 */
	public List<Integer> getNumberOfNotesPerChord() {
		return numberOfNotesPerChord;
	}


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	//  P I T C H
	/**
	 * Gets the pitches in the chord at the given index. Element 0 of the List represents the
	 * lowest note's pitch, element 1 the second-lowest note's, etc.
	 * 
	 * NB: If the chord contains course crossings, the list returned will not be in numerical 
	 * order.
	 * 
	 * @param btp
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED
	public static List<Integer> getPitchesInChord(Integer[][] btp, int lowestNoteIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();	
		int chordSize = btp[lowestNoteIndex][CHORD_SIZE_AS_NUM_ONSETS];
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
			pitchesInChord.add(btp[i][PITCH]);
		}
		return pitchesInChord;
	}


	//  T I M E
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
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static Rational[] getMetricPosition(Rational mt, List<Integer[]> meterInfo) {
		Rational[] metricPosition = new Rational[2];

		// 0. Determine the presence of an anacrusis
		boolean containsAnacrusis = false;
		if (meterInfo.get(0)[Transcription.MI_FIRST_BAR] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] in : meterInfo) {
			if (in[Transcription.MI_DEN] > largestMeterDenom) {
				largestMeterDenom = in[Transcription.MI_DEN];
			}
		}
		int commonDenom = mt.getDenom() * largestMeterDenom;

		// 2. Express metricTime and all meters in commonDenom  	
		// a. metricTime
		Rational metricTimeInLargestDenom = 
			new Rational(mt.getNumer() * largestMeterDenom, mt.getDenom() * largestMeterDenom);
		// b. All meters
		List<Rational> metersInLargestDenom = new ArrayList<Rational>();
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] currentMeter = 
				new Integer[]{meterInfo.get(i)[Transcription.MI_NUM], 
					meterInfo.get(i)[Transcription.MI_DEN]};
			// factor will always be an int because largestMeterDenom will always be a multiple of currentMeter[1]    	
			int factor = (largestMeterDenom / currentMeter[1]) * mt.getDenom();  
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
		for (int i = startIndex; i < meterInfo.size(); i++) {
			// Determine the number of bars in the current meter
			int numBarsInCurrentMeter = (meterInfo.get(i)[Transcription.MI_LAST_BAR] - 
				meterInfo.get(i)[Transcription.MI_FIRST_BAR]) + 1;
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
					int numberOfBarsToAdd =	
						(currentDistance - (currentDistance % currentBarSize)) / currentBarSize;   			
					int currentBarNumber = 
						meterInfo.get(i + startIndex)[Transcription.MI_FIRST_BAR] + 
						numberOfBarsToAdd;
					// Determine the position in the bar
					Rational currentPositionInBar = 
						new Rational(currentDistance % currentBarSize, commonDenom);
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
	 * Gets the minimum duration of the given Note.
	 * 
	 * NB Tablature case only. 
	 * 
	 * @param btp
	 * @param note
	 * @return
	 */
	// TESTED
	public static Rational getMinimumDurationOfNote(Integer[][] btp, Note note) {
		int pitch = note.getMidiPitch();
		Rational metricTime = note.getMetricTime(); 
		int metricTimeAsInt = metricTime.getNumer() * (SRV_DEN / metricTime.getDenom());

		// Find the note with pitch and metricTimeAsInt. In case there are two such notes 
		// (i.e., in case of a unison), their minimum duration will be the same as they are 
		// in the same chord
		Rational minDuration = null;
		for (Integer[] in : btp) {
			if (in[ONSET_TIME] == metricTimeAsInt && in[PITCH] == pitch) {
				minDuration = new Rational(in[MIN_DURATION], SRV_DEN);
				break;
			}
		}
		return minDuration;
	}


	/**
	 * Returns the int value of the given duration Rational.
	 *  
	 * @param dur
	 * @return
	 */
	// TESTED
	public static int rationalToIntDur(Rational dur) {
		return dur.mul(SRV_DEN).getNumer();
	}


	//  M E T E R
	/**
	 * Given a meter and a diminution, calculates the diminuted meter.
	 * 
	 * <ul>
	 * <li>diminution > 0: meter count stays the same; meter unit doubles</li>
	 * <ul>
	 * <li>diminution = 2:	2/2 --> 2/(2/2) = 2/1</li>
	 * <li>diminution = 2:	4/4 --> 4/(4/2) = 4/2</li>
	 * <li>diminution = 4:	4/4 --> 4/(4/4) = 4/1</li>
	 * </ul>
	 * <li>diminution < 0: meter count stays the same; meter unit halves</li>
	 * <ul>
	 * <li>diminution = -2: 2/2 --> 2/(2*|-2|) = 2/4</li>
	 * <li>diminution = -2: 4/4 --> 4/(4*|-2|) = 4/8</li>                    
	 * <li>diminution = -4: 4/4 --> 4/(4*|-4|) = 4/16</li>
	 * </ul>
	 * </ul>
	 * @param meter
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational diminuteMeter(Rational meter, int diminution) {
		Rational newMeter;
		if (diminution == 1) {
			newMeter = new Rational(meter.getNumer(), meter.getDenom());
		}
		else if (diminution > 0) {
			newMeter = new Rational(meter.getNumer(), (int) (meter.getDenom() / diminution)); 
		}
		else {
			newMeter = new Rational(meter.getNumer(), (meter.getDenom() * Math.abs(diminution)));
		}
		return newMeter;
	}


	/**
	 * Given a diminuted meter and a diminution, calculates the original meter.
	 * 
	 * <ul>
	 * <li>diminution > 0: meter count stays the same; meter unit halves</li>
	 * <ul>
	 * <li>diminution = 2:	2/1 --> 2/(1*2) = 2/2</li>
	 * <li>diminution = 2:	4/2 --> 4/(2*2) = 4/4</li>
	 * <li>diminution = 4:	4/1 --> 4/(1*4) = 4/4</li>
	 * </ul>
	 * <li>diminution < 0: meter count stays the same; meter unit doubles</li>
	 * <ul>
	 * <li>diminution = -2: 2/4  --> 2/(4/|-2|) = 2/2</li>
	 * <li>diminution = -2: 4/8  --> 4/(8/|-2|) = 4/4</li>                    
	 * <li>diminution = -4: 4/16 --> 4/(16/|-4|) = 4/4</li>
	 * </ul>
	 * </ul>
	 * @param meter
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational undiminuteMeter(Rational meter, int diminution) {
		Rational newMeter;
		if (diminution == 1) {
			newMeter = new Rational(meter.getNumer(), meter.getDenom());
		}
		else if (diminution > 0) {
			newMeter = new Rational(meter.getNumer(), (meter.getDenom() * diminution)); 
		}
		else {
			newMeter = new Rational(meter.getNumer(), (int) (meter.getDenom() / Math.abs(diminution)));
		}
		return newMeter;
	}


	/**
	 * Gets the diminution for the given metric time.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static int getDiminution(Rational mt, List<Integer[]> meterInfo) {
		int diminution = 1; 
		// For each meter
		for (int i = 0; i < meterInfo.size(); i++) {
			Integer[] in = meterInfo.get(i);
			// Not last meter: check if mt falls in current meter
			if (i < meterInfo.size() - 1) {
				Rational lower = new Rational(in[Transcription.MI_NUM_MT_FIRST_BAR], 
					in[Transcription.MI_DEN_MT_FIRST_BAR]);
				Rational upper = 
					new Rational(meterInfo.get(i+1)[Transcription.MI_NUM_MT_FIRST_BAR], 
					meterInfo.get(i+1)[Transcription.MI_DEN_MT_FIRST_BAR]);
				if (mt.isGreaterOrEqual(lower) && mt.isLess(upper)) {
					diminution = in[MI_DIM];
					break;
				}
			}
			// Last (or only) meter: mt must fall in this meter
			else {
				diminution = in[MI_DIM];
			}
		}
		return diminution;
	}


	//  O T H E R
	/**
	 * Converts the TabSymbol at the given note index into a Note.
	 * 
	 * @param btp The basic TabSymbol properties
	 * @param noteIndex The index of the note in the Tablature
	 * @return
	 */
	// TESTED
	public static Note convertTabSymbolToNote(Integer[][] btp, int noteIndex) {
		return Transcription.createNote(
			btp[noteIndex][PITCH], 
			new Rational(btp[noteIndex][ONSET_TIME], SRV_DEN), 
			new Rational(btp[noteIndex][MIN_DURATION], SRV_DEN)
		);
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	//  P I T C H
	/**
	 * Gets the pitches in the chord at the given index. Element 0 of the List represents the
	 * lowest note's pitch, element 1 the second-lowest note's, etc. 
	 * 
	 * NB: If the chord contains course crossings, the list returned will not be in numerical 
	 * order.
	 * 
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	public List<Integer> getPitchesInChord(int chordIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();
		Integer[][] btpChord = getBasicTabSymbolPropertiesChord(chordIndex);	
		for (Integer[] currBtp : btpChord) {
			pitchesInChord.add(currBtp[PITCH]);
		}
		return pitchesInChord;
	}


	/**
	 * Gets information on the unison(s) in the chord at the given index. A unison occurs when 
	 * two different Tablature notes in the same chord have the same pitch.  
	 *
	 * NB: This method presumes that<br>
	 * <ul>
	 * <li>A chord contains only one unison, and neither a CoD nor a course crossing.</li>
	 * <li>A chord will not contain two unisons of the same pitch (e.g., 6th c., 10th fr. - 
	 *    5th c., 5th fr. - 4th c., open), which is theoretically possible but will not occur
	 *    in practice.</li>
	 * </ul>
	 *
	 * @param chordIndex
	 * @return An List of Integer[]s, each element of which represents a unison pair (starting 
	 * from below), each element of which contains<br>
	 * <ul>
	 * <li>As element 0: the pitch (as a MIDI number) of the unison note.</li>
	 * <li>As element 1: the sequence number in the chord of the lower unison note (i.e., 
	 *     the one appearing first in the chord).</li>
	 * <li>As element 2: the sequence number in the chord of the upper unison note.</li> 
	 * </ul>
	 * If the chord does not contain (a) unison(s), <code>null</code> is returned. 
	 */
	// TESTED
	public List<Integer[]> getUnisonInfo(int chordIndex) {
		List<Integer[]> unisonInfo = new ArrayList<>();
		// For each pitch in pitchesInChord
		List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
		for (int i = 0; i < pitchesInChord.size(); i++) {
			int currentPitch = pitchesInChord.get(i);        
			// Search the remainder of pitchesInChord for a note with the same pitch (unison)
			for (int j = i + 1; j < pitchesInChord.size(); j++) {
				if (pitchesInChord.get(j) == currentPitch) {
					unisonInfo.add(new Integer[]{currentPitch, i, j});
					break; // See NB b) for reason of break
				}
			} 
		}
		return unisonInfo.size() != 0 ? unisonInfo : null;
	}


	/**
	 * Determines the number of unisons in the chord at the given index. A unison occurs when
	 * two different Tablature notes in the same chord have the same pitch.   
	 * 
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	public int getNumberOfUnisonsInChord(int chordIndex) {
		if (getUnisonInfo(chordIndex) == null) {
			return 0;
		}
		else {
			return getUnisonInfo(chordIndex).size();
		}
	}


	/**
	 * Gets information on the course crossing(s) in the chord at the given index in the given
	 * list. A course crossing occurs when an note on course x has a pitch that is higher than
	 * that of a note (in the same chord) on course y above it.
	 * 
	 * NB: This method presumes that<br>
	 * <ul>
	 * <li>A chord contains only one course crossing, and neither a CoD nor a unison.</li> 
	 * <li>The course crossing will not span more than two pitches, which is theoretically 
	 *    possible (e.g., 6th c., 12th fr. - 5th c., 6th fr. - 4th c., open), but will not 
	 *    likely occur in practice.</li>
	 * </ul>
	 * @param chordIndex 
	 * @return A List of Integer[]s, each element of which represents a course crossing pair 
	 * (starting from below), each element of which contains<br>
	 * <ul>
	 * <li>As element 0: the pitch (as a MIDI number) of the lower CC note (i.e., the one 
	 *     appearing first in the chord).</li>
	 * <li>As element 1: the pitch (as a MIDI number) of the upper CC note.</li>
	 * <li>As element 2: the sequence number in the chord of the lower CC note.</li>
	 * <li>As element 3: the sequence number in the chord of the upper CC note.</li>
	 * </ul>  
	 * If the chord does not contain (a) course crossing(s), <code>null</code> is returned. 
	 */
	// TESTED
	public List<Integer[]> getCourseCrossingInfo(int chordIndex) {
		List<Integer[]> courseCrossingsInfo = new ArrayList<>();
		// For each pitch in pitchesInChord
		List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
		for (int i = 0; i < pitchesInChord.size(); i++) {
			int currentPitch = pitchesInChord.get(i);        
			// Search the remainder of pitchesInChord for a note with a lower pitch (course crossing)
			for (int j = i + 1; j < pitchesInChord.size(); j++) {
				if (pitchesInChord.get(j) < currentPitch) {
					courseCrossingsInfo.add(new Integer[]{currentPitch, pitchesInChord.get(j), i, j});
					break; // See NB b) for reason of break
				}
			} 
		}
		return courseCrossingsInfo.size() != 0 ? courseCrossingsInfo : null;
	}


	/**
	 * Determines the number of course crossings in the chord at the given index. A course crossing occurs
	 * when an onset on course x has a pitch that is higher than that of an onset on course y above it.
	 * 
	 * @param chordIndex
	 * @return 
	 */
	// TESTED
	public int getNumberOfCourseCrossingsInChord(int chordIndex) {
		if (getCourseCrossingInfo(chordIndex) == null) {
			return 0;
		}
		else {
			return getCourseCrossingInfo(chordIndex).size();
		}
	}


	/**
	 * Lists all the unique chords in the tablature in the order they are encountered. Each 
	 * chord is represented as a series of pitches, with the lowest pitch listed first. Chords 
	 * with course crossings are therefore rearranged so that their pitches are sorted 
	 * numerically.
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
	 * Returns the interval (in semitones) by which the tablature must be transposed in order
	 * to normalise its tuning to G.
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
		return transpositionInterval;
	}


	//  T I M E
	/**
	 * Returns the metric times for all chords, including, if specified, those of rest events.
	 * 
	 * NB: Successive individual rest events are combined into a single rest event. Any rest
	 *     events before the first chord are included; any added after the final chord are not.
	 * 
	 * @param includeRestEvents Whether or not to include rest event metric times.
	 * @return For each chord or rest event, a Rational[] containing<br>
	 *         <ul>
	 *         <li>As element 0: the metric time.</li>
	 *         <li>As element 1: whether (Rational.ONE) or not (Rational.ZERO) it is a note 
	 *             event.</li>
	 *         </ul>
	 */
	// TESTED
	public List<Rational[]> getMetricTimePerChord(boolean includeRestEvents) {
		List<Rational[]> allMetricTimes = new ArrayList<Rational[]>();
		Integer[][] btp = getBasicTabSymbolProperties();

		// If there is a rest before the first chord, add it at onset time zero
		// NB An anacrusis is interpreted as a full bar starting with rests
		if (includeRestEvents && btp[0][ONSET_TIME] > 0) {
			allMetricTimes.add(new Rational[]{Rational.ZERO, Rational.ZERO});
		}
		for (int i = 0; i < btp.length; i++) {
			Integer[] in = btp[i];
			Rational currOnsetTime = new Rational(in[ONSET_TIME], SRV_DEN);
			Rational currMinDur = new Rational(in[MIN_DURATION], SRV_DEN);
			Rational currOffsetTime = currOnsetTime.add(currMinDur);
			Rational nextOnsetTime = 
				(i < btp.length-1) ? new Rational(btp[i+1][ONSET_TIME], SRV_DEN) :
				currOffsetTime;
			allMetricTimes.add(new Rational[]{currOnsetTime, Rational.ONE});
			if (includeRestEvents && currOffsetTime.isLess(nextOnsetTime)) {
				allMetricTimes.add(new Rational[]{currOffsetTime, Rational.ZERO});
			}
			i += in[CHORD_SIZE_AS_NUM_ONSETS]-1;
		}
		return allMetricTimes;
	}


	/**
	 * Returns the minimum durations for all chords.
	 * 
	 * @return
	 */
	// TESTED
	public List<Rational> getMinimumDurationPerChord() {
		List<Rational> allMinimumDurations = new ArrayList<>();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (int i = 0; i < btp.length; i++) {
			Integer[] in = btp[i];
			Rational currMinDur = new Rational(in[MIN_DURATION], SRV_DEN);
			allMinimumDurations.add(currMinDur);
			i += in[CHORD_SIZE_AS_NUM_ONSETS]-1;
		}
		return allMinimumDurations;
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
			List<Rational[]> onsetTimes = getMetricTimePerChord(true);
//			List<Rational[]> onsetTimes = getAllOnsetTimesRestsInclusive();

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


	//  M E T E R
	/**
	 * Gets, for each MensurationSign in the encoding, the sign's encoding, its tab bar,
	 * and its metric bar.
	 * 
	 * @return
	 */
	// TESTED
	public List<String[]> getMensurationSigns() {
		List<String[]> tabMeters = new ArrayList<>();
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		List<Integer[]> tabBarsToMetricBars = mapTabBarsToMetricBars();
		List<List<String[]>> ebf = getEncoding().getEventsBarlinesFootnotesPerBar(true);
		// For each tab bar
		for (int i = 0; i < ebf.size(); i++) {
			// For each event
			for (String[] s : ebf.get(i)) {
				// If the first symbol in s[0] (the encoded event) is a MS
				String firstSym = s[0].substring(0, s[0].indexOf(ss));
				if (MensurationSign.getMensurationSign(firstSym) != null) {
					// Add complete event (which will be a MS event) without trailing SS
					tabMeters.add(new String[]{s[0].substring(0, s[0].lastIndexOf(ss)), 
						String.valueOf(i+1), String.valueOf(tabBarsToMetricBars.get(i)[1])});
				}
			}
		}
		return tabMeters;
	}


	/**
	 * Gets the diminution for the given metric bar.
	 * 
	 * @param bar
	 * @return
	 */
	// TESTED
	public int getDiminution(int bar) {
		List<Integer[]> dpb = getDiminutionPerBar();
		return dpb.get(ToolBox.getItemsAtIndex(dpb, 0).indexOf(bar))[1];
	}


	/**
	 * Gets the number of tablature bars, as specified by the number of barlines (where
	 * decorative initial barlines are not counted).
	 * 
	 * @return
	 */
	// TESTED
	public int getNumberOfTabBars() {
		int numBarlines = 0;
		// For each bar
		for (List<String[]> l : getEncoding().getEventsBarlinesFootnotesPerBar(true)) {
			// For each event in the bar
			for (String[] s : l) {
				String firstSymbol = 
					s[0].substring(0, s[0].lastIndexOf(SymbolDictionary.SYMBOL_SEPARATOR));
				if (ConstantMusicalSymbol.isBarline(firstSymbol)) {
					numBarlines++;
				}
			}
		}
		return numBarlines;
	}


	/**
	 * Gets the number of metric bars, as specified in the meterInfo.
	 * 
	 * @return An Integer[] containing<br>
	 * <ul>
	 * <li>as element 0: the number of metric bars, not counting any anacrusis</li>
	 * <li>as element 1: 1 if there is an anacrusis; 0 if not</li>
	 * </ul>
	 */
	// TESTED
	public Integer[] getNumberOfMetricBars() {
		List<Integer[]> mi = getMeterInfo();
		int firstBar = mi.get(0)[Transcription.MI_FIRST_BAR];
		int lastBar = mi.get(mi.size()-1)[Transcription.MI_LAST_BAR];
		return new Integer[]{lastBar, firstBar == 0 ? 1 : 0};
	}


	/**
	 * Get, for each tab bar, the metric bar it belongs to. A metric bar can have 
	 * multiple tab bars. Example :<br> 
	 * metric bars: 2/2 H H | H H | H   H | H   H | H H | H H |<br>
	 * tab bars   : 2/2 H H | H H | H | H | H | H | H H | H H |<br> 
 	 * returns [[1, 1], [2, 2], [3, 3], [4, 3], [5, 4], [6, 4], [7, 5], [8, 6]]
	 * 
	 * @return A list of Integer[]s, each representing a tab bar and containing<br>
	 *         <ul>
	 *         <li>as element 0: the tab bar</li>
	 *         <li>as element 1: the metric bar the tab bar belongs to</li>
	 *         </ul>
	 */
	// TESTED
	public List<Integer[]> mapTabBarsToMetricBars() {
		List<Integer[]> mapped = new ArrayList<>();

		// Get metric bar lengths in SMALLEST_RHYTHMIC_VALUE
		List<Integer> metricBarLengths = new ArrayList<>();
		for (Integer[] in : getUndiminutedMeterInfo()) {
			Rational currMeter = 
				new Rational(in[Transcription.MI_NUM], in[Transcription.MI_DEN]);
			int barLenInSrv = (int) currMeter.div(SMALLEST_RHYTHMIC_VALUE).toDouble();
			int numBarsInMeter = 
				(in[Transcription.MI_LAST_BAR] - in[Transcription.MI_FIRST_BAR]) + 1;
			metricBarLengths.addAll(Collections.nCopies(numBarsInMeter, barLenInSrv));
		}
	
		// Get tablature bar lengths in SMALLEST_RHYTHMIC_VALUE
		List<Integer> tabBarLengths = new ArrayList<>();
		List<List<String[]>> ebf = getEncoding().getEventsBarlinesFootnotesPerBar(true);
		int prevDur = -1;
		for (List<String[]> bar : ebf) {
			int durBar = 0;
			for (String[] eventInfo : bar) {
				String event = eventInfo[0];
				String first = 
					event.substring(0, event.indexOf(SymbolDictionary.SYMBOL_SEPARATOR));
				// If the event is not a barline event or a MS event
				if (!ConstantMusicalSymbol.isBarline(first)
					&& MensurationSign.getMensurationSign(first) == null) {
					int dur = -1;
					RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(first);
					// If the event starts with a RS
					if (rs != null) {
						dur = rs.getDuration();
						prevDur = dur;
					}
					// If the event does not start with a RS
					else {
						dur = prevDur; 
					}
					durBar += dur;
				}
			}
			tabBarLengths.add(durBar);	
		}

		// Map
		int metricBar = 1;
		int currTabBarLen = 0;
		for (int i = 0; i < tabBarLengths.size(); i++) {
			int bar = i+1;
			mapped.add(new Integer[]{bar, metricBar});
			currTabBarLen += tabBarLengths.get(i);
			int currMetricBarLen = metricBarLengths.get(metricBar-1);
			if (currTabBarLen == currMetricBarLen) {
				metricBar++;
				currTabBarLen = 0;
			}	
		}
		return mapped;
	}


	//  O T H E R
	/**
	 * Gets the basicTabSymbolProperties of the chord at the given index in the Tablature, 
	 * i.e., the elements of basicTabSymbolProperties corresponding to the notes within that
	 * chord. 
	 * 
	 * @param chordIndex 
	 * @return
	 */
	// TESTED
	public Integer[][] getBasicTabSymbolPropertiesChord(int chordIndex) {
		// Determine the size of the chord at chordIndex and the index of the lowest note in it
		int chordSize = 0;
		int lowestNoteIndex = 0;
		Integer[][] btp = getBasicTabSymbolProperties();
		for (int i = 0; i < btp.length; i++) {
			if (btp[i][CHORD_SEQ_NUM] == chordIndex) {
				lowestNoteIndex = i;
				chordSize = btp[i][CHORD_SIZE_AS_NUM_ONSETS];
				break;
			}
		}
		// Create and return basicTabSymbolPropertiesChord
		Integer[][] basicTabSymbolPropertiesChord = 
			Arrays.copyOfRange(btp, lowestNoteIndex, lowestNoteIndex + chordSize);   
		return basicTabSymbolPropertiesChord;
	}


	// TESTED
	public int getNumberOfNotes() {
		return getBasicTabSymbolProperties().length;	
	}


	/**
	 * Returns a List<List>> containing, for each chord, the indices in the Tablature of the
	 * notes in that chord.
	 * 
	 * @param isBwd 
	 * @return
	 */
	// TESTED
	public List<List<Integer>> getIndicesPerChord(boolean isBwd) {
		List<List<Integer>> indicesPerChord = new ArrayList<List<Integer>>();

		List<List<TabSymbol>> tablatureChords = getTablatureChords();
		if (isBwd) {
			Collections.reverse(tablatureChords);
		}

		int startIndex = 0;
		// For each chord
		for (int i = 0; i < tablatureChords.size(); i++) {
		List<Integer> indicesCurrChord = new ArrayList<Integer>();
		int endIndex = startIndex + tablatureChords.get(i).size();
		// For each note in the chord at index i: add its index to indicesCurrChord
		for (int j = startIndex; j < endIndex; j++) {
			indicesCurrChord.add(j);
		}
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
		return Collections.max(getNumberOfNotesPerChord());
	}


	/**
	 * Returns a reversed version of the Tablature.
	 * 
	 * @param tab
	 * @return
	 */
	// TODO test
	public static Tablature reverse(Tablature tab) { // TODO make non-static?
		return new Tablature(
			tab.getEncoding().reverseEncoding(tab.getMeterInfo()), tab.getNormaliseTuning());
//		return new Tablature(
//			getEncoding().reverseEncoding(getMeterInfo()), getNormaliseTuning());
	}


	/**
	 * Returns a deornamented version of the Tablature.
	 * 
	 * @param tab
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental.
	 * @return
	 */
	// TODO test
	public static Tablature deornament(Tablature tab, Rational dur) { // TODO make non-static?
		return new Tablature(
			tab.getEncoding().deornamentEncoding(rationalToIntDur(dur)), 
			tab.getNormaliseTuning());
	}


	/**
	 * Returns a durationally stretched version of the Tablature.
	 * 
	 * @param tab
	 * @param factor The factor to stretch the durations.
	 * @return
	 */
	// TODO test
	public static Tablature stretch(Tablature tab, int factor) { // TODO make non-static?
		return new Tablature(
			tab.getEncoding().stretchEncoding(tab.getMeterInfo(), factor), 
			tab.getNormaliseTuning());
	}


	/**
	 * Converts the TabSymbol at the given onsetIndex into a Note. Alternative to the method
	 * with the same name.
	 * 
	 * @param onsetIndex The index of the onset in the Tablature
	 * @return
	 */
	private Note convertTabSymbolToNote(int onsetIndex) {
		Integer[][] btp = getBasicTabSymbolProperties();
		// 1. Extract the tabSymbol's gridY (pitch), minimum duration, and onset time
		int tabSymbolPitch = btp[onsetIndex][PITCH];
		int tabSymbolMinimumDuration = btp[onsetIndex][MIN_DURATION];
		int tabSymbolOnsetTime = btp[onsetIndex][ONSET_TIME];

		// 2. Convert ints into Rationals 
		Rational noteMinimumDuration = new Rational(tabSymbolMinimumDuration, SRV_DEN); 
		Rational noteOnsetTime = new Rational(tabSymbolOnsetTime, SRV_DEN);

		// 3. Create a Note with the given pitch, onset time, and minimum duration
		Note note = Transcription.createNote(tabSymbolPitch, noteOnsetTime, noteMinimumDuration);

		return note; 
	}


	/**
	 * Returns all onset times.
	 * 
	 * @return
	 */
	private List<Rational> getAllOnsetTimes() {
		List<Rational> allOnsetTimes = new ArrayList<Rational>();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (Integer[] in : btp) {
			Rational currOnsetTime = new Rational(in[ONSET_TIME], SRV_DEN); 
			if (!allOnsetTimes.contains(currOnsetTime)) {
				allOnsetTimes.add(currOnsetTime);
			}
		}
		Collections.sort(allOnsetTimes);
		return allOnsetTimes;
	}


	/**
	 * Returns all metric positions.
	 * 
	 * @return
	 */
	// TESTED
	private List<Rational[]> getAllMetricPositions() {
		List<Rational[]> allMetricPositions = new ArrayList<Rational[]>();
		List<Integer[]> mi = getMeterInfo();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (Integer[] b : btp) {
			allMetricPositions.add(getMetricPosition(new Rational(b[ONSET_TIME], SRV_DEN), mi)); 
		}
		return allMetricPositions;
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
	private List<Rational[]> getAllOnsetTimesRestsInclusive() {
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
	private List<Rational[]> getAllOnsetTimesAndMinDurations() {
		List<Rational[]> allOnsetTimesAndMinDurs = new ArrayList<Rational[]>();
		Integer[][] btp = getBasicTabSymbolProperties();
		List<Rational> allOnsetTimes = new ArrayList<>();
		for (Integer[] b : btp) {
			Rational currentOnsetTime = new Rational(b[ONSET_TIME], SRV_DEN);
			Rational currentMinDur = new Rational(b[MIN_DURATION], SRV_DEN);
			if (!allOnsetTimes.contains(currentOnsetTime)) {
				allOnsetTimes.add(currentOnsetTime);
				allOnsetTimesAndMinDurs.add(new Rational[]{currentOnsetTime, currentMinDur});
			}
		}
		ToolBox.sortByRational(allOnsetTimesAndMinDurs, 0);
		return allOnsetTimesAndMinDurs;
	}


	private static int getDiminution(int bar, List<Integer[]> mi) {
		int diminution = 1; 
		for (Integer[] in : mi) {
			if (bar >= in[Transcription.MI_FIRST_BAR] && bar <= in[Transcription.MI_LAST_BAR]) {
				diminution = in[MI_DIM];
				break;
			}
		}
		return diminution;
	}


	private void setFile(File argFile) {
		file = argFile;
	}


	private File getFile() {
		return file;
	}


	private void setKeyInfo(List<Integer[]> arg) {
		keyInfo = arg;
	}


	private List<Integer[]> getKeyInfo() {
		return keyInfo;
	}

}
