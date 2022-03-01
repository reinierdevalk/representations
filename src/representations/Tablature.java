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

	// For tunings
	public static final int ENCODED_TUNING_IND = 0;
	public static final int NORMALISED_TUNING_IND = 1;

	// For basicTabSymbolProperties
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

	private Encoding encoding;
	private boolean normaliseTuning;
	private String piecename;
	private Timeline timeline;
	private Tuning[] tunings;
	private Integer[][] basicTabSymbolProperties;
	private List<List<TabSymbol>> tablatureChords;
	private List<Integer> numberOfNotesPerChord;
	private List<Integer[]> keyInfo; // TODO remove
	private File file; // TODO remove

	public static enum Tuning  {
		C_HIGH("C_HIGH", 5, false, Arrays.asList(new String[]{"C", "F", "Bb", "D", "G", "C"})), // 1030_coment_peult_avoir_joye.tbp
		C6Bb("C6Bb", -7, true, Arrays.asList(new String[]{"Bb", "F", "Bb", "D", "G", "C"})),
		D("D", -5, false, Arrays.asList(new String[]{"D", "G", "C", "E", "A", "D"})),
		F("F", -2, false, Arrays.asList(new String[]{"F", "Bb", "Eb", "G", "C", "F"})),
		F_ENH("F", -2, false, Arrays.asList(new String[]{"F", "A#", "D#", "G", "C", "F"})),
		Fx("F#", -1, false, Arrays.asList(new String[]{"F#", "B", "E", "G#", "C#", "F#"})), // 5148_51_respice_in_me_deus._F#_lute_T.tbp
		G("G", 0, false, Arrays.asList(new String[]{"G", "C", "F", "A", "D", "G"})),
		G6F("G6F", 0, true, Arrays.asList(new String[]{"F", "C", "F", "A", "D", "G"})),
		G7F("G7F", 0, false, Arrays.asList(new String[]{"F", "G", "C", "F", "A", "D", "G"})),
		G7D("G7D", 0, false, Arrays.asList(new String[]{"D", "G", "C", "F", "A", "D", "G"})),
		A("A", 2, false, Arrays.asList(new String[]{"A", "D", "G", "B", "E", "A"})),
		A6G("A6G", 2, true, Arrays.asList(new String[]{"G", "D", "G", "B", "E", "A"}));

		private String name;
		private int transposition;
		private boolean isDrop;
		private List<String> courses;
		Tuning(String s, int t, boolean a, List<String> l) {
			this.name = s;
			this.transposition = t;
			this.isDrop = a;
			this.courses = l;
		}
		
		public String getName() {
			return name;
		}

		public int getTransposition() {
			return transposition;
		}

		public boolean isDrop() {
			return isDrop;
		}

		public List<String> getCourses() {
			return courses;
		}

		public static Tuning getTuning(String s) {
			for (Tuning t : Tuning.values()) { 
				if (t.toString().equals(s)) {
					return t;
				}
			}
			return null;
		}
	}


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


	private void init(Encoding encoding, boolean normaliseTuning) {
		// Each of the following methods needs one or more of the preceding ones
		setEncoding(encoding);
		setNormaliseTuning(normaliseTuning);
		setPiecename();
		setTimeline();
		setTunings();
		setBasicTabSymbolProperties();
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


	void setNormaliseTuning(boolean arg) {
		normaliseTuning = arg;
	}


	void setPiecename() {
		piecename = getEncoding().getPiecename(); 
	}


	void setTimeline() {
		timeline = new Timeline(getEncoding());
	}


	void setTunings() {
		tunings = makeTunings();
	}


	// TESTED
	Tuning[] makeTunings() {
		Tuning encodedTun = 
			Tuning.getTuning(getEncoding().getMetadata().get(Encoding.TUNING_IND));
		Tuning[] tuns = new Tuning[2];
		tuns[ENCODED_TUNING_IND] = encodedTun;
		tuns[NORMALISED_TUNING_IND] = 
			!getNormaliseTuning() ? encodedTun : (encodedTun.isDrop() ? Tuning.G6F : Tuning.G);
		return tuns;
	}


	void setBasicTabSymbolProperties() {
		basicTabSymbolProperties = makeBasicTabSymbolProperties();
	}


	// TESTED
	Integer[][] makeBasicTabSymbolProperties() {
		TabSymbolSet tss = getEncoding().getTabSymbolSet();

		List<List<String>> symbols = getEncoding().getListsOfSymbols();
//		List<String> listOfAllEvents = symbols.get(Encoding.ALL_EVENTS_IND);
		List<String> listOfTabSymbols = symbols.get(Encoding.TAB_SYMBOLS_IND);
		List<List<Integer>> stats = getEncoding().getListsOfStatistics();
//		List<Integer> isTabSymbolEvent = stats.get(Encoding.IS_TAB_SYMBOL_EVENT_IND);
		List<Integer> sizeOfEvents = stats.get(Encoding.SIZE_OF_EVENTS_IND);
		List<Integer> horPosTabSymbols = stats.get(Encoding.HORIZONTAL_POS_IND);
		List<Integer> vertPosTabSymbols = stats.get(Encoding.VERTICAL_POS_IND);
		List<Integer> horPosInTabSymbolEventsOnly = 
			stats.get(Encoding.HORIZONTAL_POS_TAB_SYMBOLS_ONLY_IND);

		// 1. Make durations and metric times TODO rename gridX to onsets
		List<List<Integer>> durAndGridX = getDurationsAndGridX();
		List<Integer> durOfTabSymbols = durAndGridX.get(0);
		List<Integer> gridXOfTabSymbols = durAndGridX.get(1);
		// Diminute
//		Timeline tl = getTimeline();
//		List<Integer> diminutions = ToolBox.getItemsAtIndex(tl.getMeterInfo(), Timeline.MI_DIM);
		List<List<Integer>> scaled = adaptToDiminutions(durOfTabSymbols, gridXOfTabSymbols);
		durOfTabSymbols = scaled.get(0);
		gridXOfTabSymbols = scaled.get(1);

		// 2. Make pitches
		List<Integer> gridYOfTabSymbols = new ArrayList<>();
		Tuning t = getNormaliseTuning() ? tunings[NORMALISED_TUNING_IND] : tunings[ENCODED_TUNING_IND];
		listOfTabSymbols.forEach(ts -> gridYOfTabSymbols.add(TabSymbol.getTabSymbol(ts, tss).getPitch(t)));

		// 3. Make btp
		Integer[][] btp = new Integer[listOfTabSymbols.size()][10];
		for (int i = 0; i < btp.length; i++) {
			TabSymbol currTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			btp[i][PITCH] = gridYOfTabSymbols.get(i);
			btp[i][COURSE] = currTabSymbol.getCourse();
			btp[i][FRET] = currTabSymbol.getFret();
			int currOnsetTime = gridXOfTabSymbols.get(i);
			btp[i][ONSET_TIME] = currOnsetTime;
			int currMinDur = durOfTabSymbols.get(i);
			btp[i][MIN_DURATION] = currMinDur;
//			int currMaxDur = -1;
//			// a. If currTabSymbol is in the last tabSymbolEvent: set currMaxDur to currMinDur
////			// Get the index of the last TabSymbolEvent
////			int numTabSymbolEvents = 0;
////			for (int j = 0; j < listOfAllEvents.size(); j++) {	
////				if (isTabSymbolEvent.get(j) == 1) {
////					numTabSymbolEvents++;
////				}
////			}
////			int indLastChord = numTabSymbolEvents - 1;
//			int indLastChord = 
//				horPosInTabSymbolEventsOnly.get(horPosInTabSymbolEventsOnly.size()-1);
//			int indCurrChord = horPosInTabSymbolEventsOnly.get(i);
//			if (indCurrChord == indLastChord) {
//				currMaxDur = currMinDur;
//			}
//			// b. If not: calculate currMaxDuration
	
//			// Get the onset time of the next TS that has the same course as currTabSymbol
//			int nextOnsetTime = 0;
////			else {			
//			for (int j = i + 1; j < listOfTabSymbols.size(); j++) {
//				TabSymbol nextTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(j), tss);
//				if (nextTabSymbol.getCourse() == currTabSymbol.getCourse()) {
//					nextOnsetTime = gridXOfTabSymbols.get(j);
//					break;
//				}
//			}
//			// If there is no next TS found: nextOnsetTime is the end of the piece, i.e.,
//			// the onset time of the last TS + its minimum duration
//			if (nextOnsetTime == 0) {  
////				int indLastTabSymbol = listOfTabSymbols.size() - 1;
//				nextOnsetTime = 
//					gridXOfTabSymbols.get(listOfTabSymbols.size() - 1) + 
//					durOfTabSymbols.get(listOfTabSymbols.size() - 1);
//			}
//			currMaxDur = nextOnsetTime - currOnsetTime;
////			}
			btp[i][MAX_DURATION] = getMaximumDuration(durOfTabSymbols, gridXOfTabSymbols, i);
			btp[i][CHORD_SEQ_NUM] = horPosInTabSymbolEventsOnly.get(i);
			btp[i][CHORD_SIZE_AS_NUM_ONSETS] = sizeOfEvents.get(horPosTabSymbols.get(i));
			btp[i][NOTE_SEQ_NUM] = vertPosTabSymbols.get(i);
			btp[i][TAB_EVENT_SEQ_NUM] = horPosTabSymbols.get(i);
		}
		return btp;
	}


	// TESTED
	List<List<Integer>> getDurationsAndGridX() {
		List<Integer> durOfTabSymbols = new ArrayList<>();
		List<Integer> gridXOfTabSymbols = new ArrayList<>();	

		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		int currDur = 0;
		int prevDur = 0;
		int gridX = 0;
		// a. Get undiminuted lists from encoding
		List<String> allEvents = 
			getEncoding().getListsOfSymbols().get(Encoding.ALL_EVENTS_IND);
		List<Integer> sizeOfEvents = 
			getEncoding().getListsOfStatistics().get(Encoding.SIZE_OF_EVENTS_IND);
		for (int i = 0; i < allEvents.size(); i++) {
			String currEvent = allEvents.get(i);
			currEvent = currEvent.concat(ss);
			boolean isRsEvent = Encoding.isRhythmSymbolEvent(currEvent);
			boolean isTsEvent = Encoding.isTabSymbolEvent(currEvent, getEncoding().getTabSymbolSet());
			// If currEvent is a RS event (which can be a rest event)
			if (isRsEvent) {
				String rsStr = currEvent.substring(0, currEvent.indexOf(ss));
				// a. Regular RS 
				if (!rsStr.equals(RhythmSymbol.rhythmDot.getEncoding())) {
					RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(rsStr);
					currDur = rs.getDuration();
				}
				// b. rhythmDot
				else {
					currDur = prevDur/2;
				}
				prevDur = currDur;
			}
			// If currEvent is a not a RS event
			else {
				currDur = prevDur;
			}
			// If currEvent is a TS event: add to lists
			if (isTsEvent) {
				durOfTabSymbols.addAll(Collections.nCopies(sizeOfEvents.get(i), currDur));
				gridXOfTabSymbols.addAll(Collections.nCopies(sizeOfEvents.get(i), gridX));
			}
			// If currEvent is an RS event or a TS event: increment gridX 
			if (isRsEvent || isTsEvent) {
				gridX += currDur;
			}
		}
		List<List<Integer>> durAndGridX = new ArrayList<>();
		durAndGridX.add(durOfTabSymbols);
		durAndGridX.add(gridXOfTabSymbols);
		return durAndGridX;
	}


	// TESTED
	List<List<Integer>> adaptToDiminutions(List<Integer> durOfTabSymbols, 
		List<Integer> gridXOfTabSymbols) {
		List<List<Integer>> res = new ArrayList<>();

		Timeline tl = getTimeline();
		List<Integer> diminutions = ToolBox.getItemsAtIndex(tl.getMeterInfo(), Timeline.MI_DIM);
		List<Integer[]> undiminutedMeterInfo = tl.getUndiminutedMeterInfo();

		// Get the metric time and the adapted metric time of beat 0 for all new meters
		List<Integer> metricTimesBeatZero = new ArrayList<>();
		metricTimesBeatZero.add(0);
		List<Integer> metricTimesBeatZeroAdapted = new ArrayList<>();
		metricTimesBeatZeroAdapted.add(0);
		for (int i = 1 ; i < undiminutedMeterInfo.size(); i++) {
			Integer[] prevMeterInfo = undiminutedMeterInfo.get(i-1);
			int prevNumBars = (prevMeterInfo[Timeline.MI_LAST_BAR] - 
				prevMeterInfo[Timeline.MI_FIRST_BAR]) + 1;
			Rational prevMeter = new Rational(prevMeterInfo[Timeline.MI_NUM], 
				prevMeterInfo[Timeline.MI_DEN]);
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
			int beatZeroAsInt = getTabSymbolDur(beatZero);
			int beatZeroAdaptedAsInt = getTabSymbolDur(beatZeroAdapted);

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
		for (int i = 0; i < durOfTabSymbols.size(); i++) {
			int currDur = durOfTabSymbols.get(i);
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
				adaptedGridXOfTabSymbols.add(beatZeroAdapted + ((currOnset-beatZero)*dim));
			}
			else {
				adaptedDurationOfTabSymbols.add(currDur/Math.abs(dim));
				adaptedGridXOfTabSymbols.add(beatZeroAdapted + ((currOnset-beatZero)/Math.abs(dim)));
			}
		}
		res.add(adaptedDurationOfTabSymbols);
		res.add(adaptedGridXOfTabSymbols);
		return res;
	}


//	void setNormaliseTuning(boolean arg) {
//		normaliseTuning = normaliseTuning(arg);
//	}


//	// TESTED
//	void normaliseTuning(boolean arg) {
//		if (arg) {
//			Encoding enc = getEncoding();
////			// 1. Reset Encoding
////			// a. tunings
//			Tuning[] tunings = enc.getTunings();
//			tunings[NORMALISED_TUNING_IND] = 
//				tunings[ENCODED_TUNING_IND].isAvallee() ? Tuning.G_AVALLEE : Tuning.G;
//			enc.setTunings(tunings);
//
////			// b. listsOfStatistics (list of gridY values only)
////			int transpositionInterval = getTranspositionInterval();
////			List<List<Integer>> stats = enc.getListsOfStatistics();
////			List<Integer> newGridYOfTabSymbols = new ArrayList<Integer>(); 
////			for (int i : stats.get(Encoding.GRID_Y_IND)) {
////				newGridYOfTabSymbols.add(i + transpositionInterval);
////			}
////			stats.set(Encoding.GRID_Y_IND, newGridYOfTabSymbols);
////			enc.setListOfStatistics(stats);
////			setEncoding(enc);
//
////			// 2. Reset basicTabSymbolProperties (pitches only)
////			Integer[][] btp = getBasicTabSymbolProperties();
////			Integer[][] newBtp = Arrays.copyOfRange(btp, 0, btp.length);
////			for (int i = 0; i < newBtp.length; i++) {
////				newBtp[i][PITCH] = newBtp[i][PITCH] + transpositionInterval;
////			}
////			setBasicTabSymbolProperties(newBtp);
////			return true;
//		}
////		else {
////			return false;
////		}
//	} TODO remove


	// TESTED
	int getMaximumDuration(List<Integer> durOfTabSymbols, List<Integer> gridXOfTabSymbols, 
		int noteIndex) {

		List<String> listOfTabSymbols = getEncoding().getListsOfSymbols().get(Encoding.TAB_SYMBOLS_IND);
		TabSymbolSet tss = getEncoding().getTabSymbolSet();
		TabSymbol currTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(noteIndex), tss);
		int currOnsetTime = gridXOfTabSymbols.get(noteIndex);

		// Get the onset time of the next TS that has the same course as currTabSymbol
		int nextOnsetTime = 0;
		for (int j = noteIndex + 1; j < listOfTabSymbols.size(); j++) {
			TabSymbol nextTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(j), tss);
			if (nextTabSymbol.getCourse() == currTabSymbol.getCourse()) {
				nextOnsetTime = gridXOfTabSymbols.get(j);
				break;
			}
		}
		// If there is no next TS found: nextOnsetTime is the end of the piece, i.e.,
		// the onset time of the last TS + its minimum duration
		if (nextOnsetTime == 0) {
			nextOnsetTime = 
				gridXOfTabSymbols.get(listOfTabSymbols.size() - 1) + 
				durOfTabSymbols.get(listOfTabSymbols.size() - 1);
		}
		return nextOnsetTime - currOnsetTime;
	}


	void setTablatureChords() {
		tablatureChords = makeTablatureChords();
	}


	// TESTED
	List<List<TabSymbol>> makeTablatureChords() {
		List<List<TabSymbol>> tc = new ArrayList<List<TabSymbol>>();
		Integer[][] btp = getBasicTabSymbolProperties();
		TabSymbolSet tss = getEncoding().getTabSymbolSet();

		List<TabSymbol> currChord = new ArrayList<TabSymbol>();
		List<String> listOfTabSymbols = 
			getEncoding().getListsOfSymbols().get(Encoding.TAB_SYMBOLS_IND);
		TabSymbol firstTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(0), tss);
		int onsetTimeFirstTabSymbol = btp[0][ONSET_TIME];
		currChord.add(firstTabSymbol);
		int onsetTimePrevTabSymbol = onsetTimeFirstTabSymbol;
		for (int i = 1; i < listOfTabSymbols.size(); i++) {
			TabSymbol currTabSymbol = TabSymbol.getTabSymbol(listOfTabSymbols.get(i), tss);
			int onsetTimeCurrTabSymbol = btp[i][ONSET_TIME];
			// If currTabSymbol and prevTabSymbol have the same onset time, they are in the 
			// same chord
			if (onsetTimeCurrTabSymbol == onsetTimePrevTabSymbol) {
				currChord.add(currTabSymbol);
			}
			// If currTabSymbol and prevTabSymbol have different onset times, currTabSymbol 
			// is the first TabSymbol of the next chord
			else {
				tc.add(currChord);
				currChord = new ArrayList<TabSymbol>();
				currChord.add(currTabSymbol);
			}
			onsetTimePrevTabSymbol = onsetTimeCurrTabSymbol;
		}
		// Add the last chord to tablatureChords
		tc.add(currChord);
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
	}


	public boolean getNormaliseTuning() {
		return normaliseTuning;
	}


	public String getPiecename() {
		return piecename;
	}


	public Timeline getTimeline() {
		return timeline;
	}


	/**
	 * Gets the tunings.
	 * 
	 * @return A <code>Tuning[]</code> containing:
	 * <ul>
	 * <li>As element 0: the encoded tuning. Is set to the tuning specified in the
	 *                   encoding and retains this initial value.</li>
	 * <li>As element 1: the normalised tuning. Is set to the tuning specified in the 
	 *                   encoding and retains this value if the tuning is not normalised;
	 *                   else, is set to a G tuning.</li>
	 * </ul>  
	 */
	public Tuning[] getTunings() {
		return tunings;
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
	 *                     same course (as multiples of SRV_DEN). The maximum duration of the 
	 *                     notes in the final chord equals their minimum duration.</li>                
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
	 * Returns the TabSymbol duration of the given CMN duration.
	 *  
	 * @param dur
	 * @return
	 */
	// TESTED
	public static int getTabSymbolDur(Rational dur) {
		return dur.mul(SRV_DEN).getNumer();
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
		return -(getTunings()[ENCODED_TUNING_IND].getTransposition());
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
	 * NB: This method belong to Tablature and not Timeline: MS are specific to the 
	 *     tablature and are often not in sync with the meters (e.g., are left out).
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
	 * Gets the number of tablature bars, as specified by the number of barlines (where
	 * decorative initial barlines are not counted).
	 * 
	 * NB: This method belong to Tablature and not Timeline: tablature bars are specific to 
	 *     the tablature and are often not in sync with the metric bars.
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
	 * Get, for each tab bar, the metric bar it belongs to. A metric bar can have 
	 * multiple tab bars. Example :<br> 
	 * metric bars: 2/2 H H | H H | H   H | H   H | H H | H H |<br>
	 * tab bars   : 2/2 H H | H H | H | H | H | H | H H | H H |<br> 
 	 * returns [[1, 1], [2, 2], [3, 3], [4, 3], [5, 4], [6, 4], [7, 5], [8, 6]]
	 * 
	 * NB: This method belong to Tablature and not Timeline: tablature bars are specific to 
	 *     the tablature and are often not in sync with the metric bars.
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
		for (Integer[] in : getTimeline().getUndiminutedMeterInfo()) {
			Rational currMeter = 
				new Rational(in[Timeline.MI_NUM], in[Timeline.MI_DEN]);
			int barLenInSrv = (int) currMeter.div(SMALLEST_RHYTHMIC_VALUE).toDouble();
			int numBarsInMeter = 
				(in[Timeline.MI_LAST_BAR] - in[Timeline.MI_FIRST_BAR]) + 1;
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
	 * Reverses the Tablature.
	 */
	// TESTED
	public void reverse() {
		this.init(getEncoding().reverseEncoding(getTimeline().getMeterInfo()), 
			getNormaliseTuning());
	}


	/**
	 * Gets the reversed Tablature.
	 * 
	 * @return The reversed Tablature.
	 */
	public Tablature getReversed() {
		return new Tablature(
			getEncoding().reverseEncoding(getTimeline().getMeterInfo()), 
			getNormaliseTuning());
	}


	/**
	 * Deornaments the tablature.
	 * 
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental and are removed.
	 */
	// TESTED
	public void deornament(Rational dur) {
		this.init(getEncoding().deornamentEncoding(getTabSymbolDur(dur)), 
			getNormaliseTuning());
	}


	/**
	 * Gets the deornamented Tablature.
	 * 
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental and are removed.
	 * @return The deornamented Tablature.
	 */
	public Tablature getDeornamented(Rational dur) {
		return new Tablature(
			getEncoding().deornamentEncoding(getTabSymbolDur(dur)), 
			getNormaliseTuning());
	}


	/**
	 * Stretches the Tablature durationally. 
	 * 
	 * @param factor The factor to stretch the durations by.
	 * @return
	 */
	// TESTED
	public void stretch(double factor) {
		this.init(getEncoding().stretchEncoding(getTimeline().getMeterInfo(), factor), 
			getNormaliseTuning());
	}


	/**
	 * Gets a durationally stretched version of the Tablature.
	 * 
	 * @param tab
	 * @param factor The factor to stretch the durations by.
	 * @return
	 */
	public static Tablature getStretched(Tablature tab, double factor) {
		return new Tablature(
			tab.getEncoding().stretchEncoding(tab.getTimeline().getMeterInfo(), factor), 
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
		List<Integer[]> mi = getTimeline().getMeterInfo();
		Integer[][] btp = getBasicTabSymbolProperties();
		for (Integer[] b : btp) {
			allMetricPositions.add(Timeline.getMetricPosition(new Rational(b[ONSET_TIME], SRV_DEN), mi)); 
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
			if (bar >= in[Timeline.MI_FIRST_BAR] && bar <= in[Timeline.MI_LAST_BAR]) {
				diminution = in[Timeline.MI_DIM];
				break;
			}
		}
		return diminution;
	}


	/**
	 * Returns a reversed version of the Tablature.
	 * 
	 * @param tab
	 * @return
	 */
	private static Tablature reverse(Tablature tab) {
		return new Tablature(
			tab.getEncoding().reverseEncoding(tab.getTimeline().getMeterInfo()), 
			tab.getNormaliseTuning());
	}


	/**
	 * Returns a deornamented version of the Tablature.
	 * 
	 * @param tab
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental and are removed.
	 * @return
	 */
	private static Tablature deornament(Tablature tab, Rational dur) {
		return new Tablature(
			tab.getEncoding().deornamentEncoding(getTabSymbolDur(dur)), 
			tab.getNormaliseTuning());
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
