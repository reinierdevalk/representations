package structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uos.fmt.musitech.utility.math.Rational;
import representations.Tablature;
import tbp.Encoding;
import tbp.Event;
import tbp.RhythmSymbol;
import tbp.Symbol;
import tools.ToolBox;
import tools.music.TimeMeterTools;

/**
 * @author Reinier de Valk
 * @version 19.02.2024 (last well-formedness check)
 */
public class Timeline implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Integer[]> barInfo;
	private List<Integer[]> bars;
	private List<Integer> diminutions;
	private List<Integer[]> timeSignatures;
	private List<Integer[]> diminutionPerBar;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Timeline() {
	}


	public Timeline(Encoding encoding, boolean isAgnostic) {
		init(encoding, isAgnostic);
	}


	private void init(Encoding encoding, boolean isAgnostic) {
		setBarInfo(encoding);
		setBars(encoding, isAgnostic);
		setDiminutions(encoding, isAgnostic);
		setTimeSignatures(encoding, isAgnostic);
		setDiminutionPerBar();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setBarInfo(Encoding encoding) {
		barInfo = makeBarInfo(encoding);
	}


	// TESTED
	List<Integer[]> makeBarInfo(Encoding encoding) {
		List<Integer[]> bi = new ArrayList<>();

		List<Event> events = Encoding.removeDecorativeBarlineEvents(encoding.getEvents());
		int barLenInSrv = 0;
		int onset = 0;
		int durPrevE = -1;
		for (int i = 0; i < events.size(); i++) {
			Event currEvent = events.get(i);
			String e = currEvent.getEncoding();
			int currBar = currEvent.getBar();
			// If the event is not a barline event or a MS event
			if (!Encoding.assertEventType(e, null, "barline") && 
				!Encoding.assertEventType(e, null, "MensurationSign")) {
				RhythmSymbol rs = Symbol.getRhythmSymbol(
					e.substring(0, e.indexOf(Symbol.SYMBOL_SEPARATOR))
				);
				int durE = rs != null ? rs.getDuration() : durPrevE;
				if (rs != null) {
					durPrevE = durE;
				}
				barLenInSrv += durE;
			}
			// Add to list if the next event belongs to the next bar or if event is the last
			if (i <= events.size() - 1) {
				Integer[] curr = new Integer[]{onset, barLenInSrv, onset + barLenInSrv};
				// Next event belongs to the next bar 
				if (i < events.size() - 1) {
					if (events.get(i + 1).getBar() == currBar + 1) {
						bi.add(curr);
						onset += barLenInSrv;
						barLenInSrv = 0;
					}
				}
				// Event is the last
				else {
					bi.add(curr);
				}
			}
		}

		return bi; 
	}


	void setBars(Encoding encoding, boolean isAgnostic) {
		bars = makeBars(encoding, isAgnostic);
	}


	// TESTED
	List<Integer[]> makeBars(Encoding encoding, boolean isAgnostic) {
		List<Integer[]> b = new ArrayList<>();

		boolean miProvided = !encoding.getMetadata().get(
			Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).equals("");

		// meterInfo determines bars (deviating barlines are ignored)
		if (!isAgnostic && miProvided) {
			List<String> meterBars = new ArrayList<>();
			Arrays.stream(encoding.getMetadata()
				.get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";"))
				.forEach(m -> meterBars.add(m.trim()));
			for (String m : meterBars) {
				String bars = m.substring(m.indexOf("(") + 1, m.indexOf(")")).trim();
				b.add(new Integer[]{
					bars.contains("-") ? Integer.valueOf(bars.split("-")[0]) : Integer.valueOf(bars),
					bars.contains("-") ? Integer.valueOf(bars.split("-")[1]) : Integer.valueOf(bars)
				});
			}
		}
		// barlines determine bars
		else {
			List<Integer[]> bi = getBarInfo();
			int barLen = bi.get(0)[1];
			int startBar = 1;
			for (int i = 0; i < bi.size(); i++) {
				int currBar = i + 1;
				int currBarLen = bi.get(i)[1];
				if (currBarLen != barLen) {
					b.add(new Integer[]{startBar, currBar - 1});
					barLen = currBarLen;
					startBar = currBar;
				}
				// Last bar
				if (i == bi.size() - 1) {
					b.add(new Integer[]{startBar, currBar});
				}
			}
		}

		return b;
	}


	void setDiminutions(Encoding encoding, boolean isAgnostic) {
		diminutions = makeDiminutions(encoding, isAgnostic);
	}


	// TESTED
	List<Integer> makeDiminutions(Encoding encoding, boolean isAgnostic) {
		boolean miProvided = !encoding.getMetadata().get(
			Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).equals("");

		// meterInfo determines diminutions
		if (!isAgnostic && miProvided) {
			List<Integer> d = new ArrayList<>();
			Arrays.stream(encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.DIMINUTION_IND]).split(";"))
				.forEach(m -> d.add(Integer.valueOf(m.trim())));
			return d;
		}
		// bars determine diminutions
		else {
			return new ArrayList<>(Collections.nCopies(getBars().size(), 1));
		}
	}


	void setTimeSignatures(Encoding encoding, boolean isAgnostic) {
		timeSignatures = makeTimeSignatures(encoding, isAgnostic);
	}


	// TESTED
	List<Integer[]> makeTimeSignatures(Encoding encoding, boolean isAgnostic) {
		List<Integer[]> ts = new ArrayList<>();
		List<Integer[]> bars = getBars();

		boolean miProvided = !encoding.getMetadata().get(
			Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).equals("");
		
		// meterInfo determines time sigs
		if (!isAgnostic && miProvided) {
			List<String> meterBars = new ArrayList<>();
			Arrays.stream(encoding.getMetadata()
				.get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";"))
				.forEach(m -> meterBars.add(m.trim()));			
			for (int i = 0; i < meterBars.size(); i++) {
				String m = meterBars.get(i);
				String meter = m.substring(0, m.indexOf("(")).trim();
				ts.add(new Integer[]{
					Integer.valueOf(meter.split("/")[0]), 
					Integer.valueOf(meter.split("/")[1]),
					i == 0 ? 0 : getTimeSignatureOnset(i, ts, bars)
				});
			}		
		}
		// bar lengths determine time sigs
		else {			
			List<Integer[]> bi = getBarInfo();
			int barLen = bi.get(0)[1];
			int tsInd = 0;
			for (int i = 0; i < bi.size(); i++) {
				int currBarLen = bi.get(i)[1];
				if (currBarLen != barLen) {
					Rational meter = calculateMeter(
						new Rational(barLen, Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom())
					);
					ts.add(new Integer[]{
						meter.getNumer(), 
						meter.getDenom(), 
						tsInd == 0 ? 0 : getTimeSignatureOnset(tsInd, ts, bars)});
					barLen = currBarLen;
					tsInd++; 
				}
				// Last bar
				if (i == bi.size() - 1) {
					Rational meter = calculateMeter(
						new Rational(barLen, Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom())
					);
					ts.add(new Integer[]{
						meter.getNumer(), 
						meter.getDenom(),
						tsInd == 0 ? 0 : getTimeSignatureOnset(tsInd, ts, bars)
					});
				}
			}
		}

		return ts;
	}


	// TESTED
	static Rational calculateMeter(Rational meter) {
		// Reduce and handle exceptions
		meter.reduce();
		if (meter.equals(Rational.ONE)) {
			meter = new Rational(2, 2);
		}
		if (meter.equals(new Rational(3, 2))) {
			meter = new Rational(3, 2);
		}
		if (meter.equals(new Rational(2, 1))) {
			meter = new Rational(4, 2);
		}
		if (meter.equals(new Rational(1, 2))) {
			meter = new Rational(2, 4);
		}
		return meter;
	}


	void setDiminutionPerBar() {
		diminutionPerBar = makeDiminutionPerBar();
	}


	// TESTED
	List<Integer[]> makeDiminutionPerBar() {
		List<Integer[]> dimPerBar = new ArrayList<>();
		List<Integer[]> bars = getBars();
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(bars, 0);
		List<Integer> diminutions = getDiminutions();

		int firstBar = bars.get(0)[0];
		int lastBar = bars.get(bars.size()-1)[1];
		int currDiminution = 0;
		for (int bar = firstBar; bar <= lastBar; bar++) {
			if (meterChangeBars.contains(bar)) {
				currDiminution = diminutions.get(meterChangeBars.indexOf(bar));
			}
			dimPerBar.add(new Integer[]{bar, currDiminution});
		}
		return dimPerBar;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	/**
	 * Gets information on the barring.
	 * 
	 * @return A list of Integer[]s, each representing a bar and containing<br>
	 *         <ul>
	 *         <li>as element 0: its onset, in multiples of Tablature.SMALLEST_RHYTHMIC_VALUE</li>
	 *         <li>as element 1: its length, in multiples of Tablature.SMALLEST_RHYTHMIC_VALUE</li>
	 *         <li>as element 2: its offset, in multiples of Tablature.SMALLEST_RHYTHMIC_VALUE</li>
	 *         </ul>
	 */
	public List<Integer[]> getBarInfo() {
		return barInfo;
	}


	/**
	 * Gets the bars for each time signature.
	 * 
	 * @return
	 */
	public List<Integer[]> getBars() {
		return bars;
	}


	public List<Integer> getDiminutions() {
		return diminutions;
	}


	/**
	 * Gets the time signatures.
	 * 
	 * @return
	 */
	public List<Integer[]> getTimeSignatures() {
		return timeSignatures;
	}


	/**
	 * Gets the diminution for each metric bar.
	 * 
	 * @return
	 */
	public List<Integer[]> getDiminutionPerBar() {
		return diminutionPerBar;
	}


	//////////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	/**
	 * Calculates the onset (in <code>TabSymbol</code> duration) of the time signature
	 * at the given index in the given list of time signatures.
	 * 
	 * @param i Must be > 0.
	 * @param ts
	 * @param bars
	 * @return
	 */
	// TESTED
	public static int getTimeSignatureOnset(int i, List<Integer[]> ts, List<Integer[]> bars) {
		// The onset time of a time signature is the sum of the durations of all 
		// preceding meter sections, where the duration of a meter section = 
		// (meter * number of bars * a whole note)
		return ts.get(i-1)[2] +
			(int) new Rational(ts.get(i-1)[0], ts.get(i-1)[1])
			.mul((bars.get(i-1)[1] - bars.get(i-1)[0]) + 1)
			.mul(Symbol.BREVIS.getDuration()).toDouble();
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
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
	 * Gets the metric position of the given metric time.<br><br>
	 * 
	 * NB: See also <code>ScoreMetricalTimeLine.getMetricPosition()</code>.
	 * 
	 * @param mt The metric time in <code>TabSymbol</code> duration.
	 * @return A Rational[] with 
	 *         <ul>
	 *         <li>As element 0: the bar, where the numerator is the bar number, and the denominator 1.</li>
	 *         <li>As element 1: the position within the bar, where the numerator is the metric position (in 
	 *                           <code>TabSymbol</code> duration, starting at 0), and the denominator the 
	 *                           duration of <code>Symbol.BREVIS</code>.</li>
	 *		   </ul>
	 */
	// TESTED
	public Rational[] getMetricPosition(int mt) {
		int br = Symbol.BREVIS.getDuration();
		List<Integer[]> tss = getTimeSignatures();

		// Calculate, for each meter section, the number of bars preceding it
		List<Integer> numBarsBeforeMeterSec = new ArrayList<>();
		numBarsBeforeMeterSec.add(0);
		for (int i = 1; i < tss.size(); i++) {
			// Number of bars in previous meter section is 
			// (currMso - prevMso) / prevMsBarLen (in TS duration)
			int numBarsPrevMeterSec = 
				(tss.get(i)[2] - tss.get(i-1)[2]) / 
				((int) new Rational(tss.get(i-1)[0], tss.get(i-1)[1]).mul(br).toDouble());
			numBarsBeforeMeterSec.add(numBarsPrevMeterSec + numBarsBeforeMeterSec.get(i-1));
		}

		// Calculate bar and metric position
		for (int i = 0; i < tss.size(); i++) {
			int currOns = tss.get(i)[2];
			if (i < (tss.size() - 1) && (mt >= currOns && mt < tss.get(i+1)[2]) || i == (tss.size() - 1)) {
				int mtInCurrMeterSec = mt - currOns;
				int currBarLenInTsDur = 
					(int) new Rational(tss.get(i)[0], tss.get(i)[1]).mul(br).toDouble();
				int mp = mt == currOns ? 0 : mtInCurrMeterSec % currBarLenInTsDur;
				int barInCurrMeterSec = ((mtInCurrMeterSec - mp) / currBarLenInTsDur) + 1;
				int bar = numBarsBeforeMeterSec.get(i) + barInCurrMeterSec;
				return new Rational[]{new Rational(bar, 1), new Rational(mp, br)};
			}
		}
		return null;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Timeline)) {
			return false;
		}
		Timeline t = (Timeline) o;
		return
			getBars().equals(t.getBars()) &&
			getDiminutions().equals(t.getDiminutions()) &&
			getTimeSignatures().equals(t.getTimeSignatures()) &&
			getDiminutionPerBar().equals(t.getDiminutionPerBar());
	}


	// TESTED BUT NOT IN USE -->
	/**
	 * Calculates the metric length (in <code>TabSymbol</code> duration) of the TimeLine.
	 * @return
	 */
	// TESTED
	public int getLength() {
		List<Integer[]> tss = getTimeSignatures();
		List<Integer[]> bars = getBars();
		List<Integer> inds = IntStream.range(0, tss.size()).boxed().collect(Collectors.toList());

		// Calculate the duration of the bars for each tss. E.g.,
		// Four bars of 2/2 = 2/2 * 96 * 4 = 384
		// Four bars of 3/4 = 3/4 * 96 * 4 = 288
		// Four bars of 3/2 = 3/2 * 96 * 4 = 576
		List<Integer> dursTss = new ArrayList<>();
		inds.forEach(i -> dursTss.add(
			((int) new Rational(tss.get(i)[0], tss.get(i)[1]).mul(RhythmSymbol.BREVIS.getDuration()).toDouble())
			* 
			((bars.get(i)[1] - bars.get(i)[0]) + 1) 
			)
		);

		return ToolBox.sumListInteger(dursTss);
	}


	/**
	 * Gets the number of metric bars.
	 * 
	 * @return An Integer[] containing<br>
	 * <ul>
	 * <li>as element 0: the number of metric bars, not counting any anacrusis</li>
	 * <li>as element 1: 1 if there is an anacrusis; 0 if not</li>
	 * </ul>
	 */
	// TESTED
	Integer[] getNumberOfMetricBars() {
		List<Integer[]> bars = getBars();
		return new Integer[]{bars.get(bars.size()-1)[1], 0};
	}


	// DEPRECATED -->
	/**
	 * Given an undiminuted metric position, gets the diminuted metric position. 
	 * 
	 * @param mp The undiminuted metric position.
	 * @param meterSectionOnsets The meter section onsets (undiminuted). 
	 * @param meterSectionOnsetsDim The meter section onsets (diminuted).
	 * @param diminutions The meter section diminutions.
	 * @return
	 */
	private static Rational getDiminutedMetricPositionOLD(Rational mp, List<Rational> meterSectionOnsets, 
		List<Rational> meterSectionOnsetsDim, List<Integer> diminutions) {

		int section = getMeterSectionOLD(mp, meterSectionOnsets);

		// Set mp relative to undiminuted meter section onset
		mp = mp.sub(meterSectionOnsets.get(section));
		// Diminute mp
		mp = TimeMeterTools.diminute(mp, diminutions.get(section));
		// Set mp relative to diminuted meter section onset 
		mp = meterSectionOnsetsDim.get(section).add(mp);

		return mp;
	}


	/**
	 * Given an undiminuted metric position, gets the meter section.
	 * 
	 * @param mp
	 * @param meterSectionOnsets
	 * @return
	 */
	private static int getMeterSectionOLD(Rational mp, List<Rational> meterSectionOnsets) {
		int numSections = meterSectionOnsets.size();
		for (int i = 0; i < numSections; i++) {
			// If there is a next section: check if mp is in section at i
			if (i < numSections - 1) {
				if (mp.isGreaterOrEqual(meterSectionOnsets.get(i)) && mp.isLess(meterSectionOnsets.get(i+1))) {
					return i;
				}
			}
			// If not: mp is in last section
			else {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Given an undiminuted time, gets the diminuted time.
	 * 
	 * @param mp The undiminuted time.
	 * @param meterSectionTimes The meter section times (undiminuted). 
	 * @param meterSectionTimesDim The meter section times (diminuted).
	 * @param diminutions The meter section diminutions.
	 * @return
	 */
	private static long getDiminutedTimeNOTINUSE(long time, List<Long> meterSectionTimes, 
		List<Long> meterSectionTimesDim, List<Integer> diminutions) {
		
		int section = getMeterSectionOLD(time, meterSectionTimes);
		

		// Set time relative to undiminuted meter section time
		time -= meterSectionTimes.get(section);
		// Diminute time
		int dim = diminutions.get(section);
		time = dim > 0 ? time / dim : time * Math.abs(dim);   
		// Set time relative to diminuted meter section time
		time += meterSectionTimesDim.get(section);

		return time; 
	}


	/**
	 * Given an undiminuted time, gets the meter section.
	 * 
	 * @param time
	 * @param meterSectionTimes
	 * @return
	 */
	private static int getMeterSectionOLD(long time, List<Long> meterSectionTimes) {
		int numSections = meterSectionTimes.size();
		for (int i = 0; i < numSections; i++) {
			// If there is a next section: check if time is in section at i
			if (i < numSections - 1) {
				if (time >= meterSectionTimes.get(i) && time < meterSectionTimes.get(i+1)) {
					return i;
				}
			}
			// If not: time is in last section
			else {
				return i;
			}
		}
		return -1;
	}


//	void setUndiminutedMeterInfoOBS(Encoding encoding) {
//		undiminutedMeterInfoOBS = makeUndiminutedMeterInfoOBS(encoding);
//	}


//	List<Integer[]> makeUndiminutedMeterInfoOBS(Encoding encoding) {
//		List<Integer[]> undiminutedMeterInfo = new ArrayList<>();
//
//		String[] undiminutedMeters = 
//			encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";");		
//		Rational prevMeterAsRat = Rational.ZERO;
//		int prevNumBars = 0;
//		Rational prevMt = Rational.ZERO;
//		for (int i = 0; i < undiminutedMeters.length; i++) {
//			Integer[] currentMeterInfo = new Integer[MI_SIZE_TAB - 1];
//			String currInfo = undiminutedMeters[i].trim();
//			// 1. Meter
//			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
//			int currNum = Integer.parseInt(currMeter.split("/")[0].trim());
//			int currDen = Integer.parseInt(currMeter.split("/")[1].trim());
//			currentMeterInfo[MI_NUM] = currNum;
//			currentMeterInfo[MI_DEN] = currDen;
//			// 2. Bar number(s)
//			int currNumBars = 0;
//			String currBars = 
//				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();
//			// If the meter is only for a single bar
//			if (!currBars.contains("-")) {
//				currentMeterInfo[MI_FIRST_BAR] = Integer.parseInt(currBars.trim());
//				currentMeterInfo[MI_LAST_BAR] = Integer.parseInt(currBars.trim());
//				currNumBars = 1;
//			}
//			// If the meter is for more than one bar
//			else {
//				int firstBar = Integer.parseInt(currBars.split("-")[0].trim());
//				int lastBar = Integer.parseInt(currBars.split("-")[1].trim());
//				currentMeterInfo[MI_FIRST_BAR] = firstBar;
//				currentMeterInfo[MI_LAST_BAR] = lastBar;
//				currNumBars = (lastBar-firstBar) + 1;
//			}
//			// 3. Metric times
//			Rational currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
//			currMt.reduce();
//			currentMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
//			currentMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
//
//			// Add and update
//			undiminutedMeterInfo.add(currentMeterInfo);
//			prevNumBars = currNumBars;
//			prevMt = currMt;
//			prevMeterAsRat = new Rational(currNum, currDen);
//		}
//		return undiminutedMeterInfo;
//	}


//	private void setMeterInfoOBS(Encoding encoding) {
//		meterInfoOBS = makeMeterInfoOBS(encoding);
//	}


//	List<Integer[]> makeMeterInfoOBS(Encoding encoding) {
//		List<Integer[]> mi = new ArrayList<>();
//
//		List<Integer> diminutions = new ArrayList<>();
//		String dimStr = encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.DIMINUTION_IND]);
//		Arrays.asList(dimStr.split(";")).forEach(d -> diminutions.add(Integer.parseInt(d.trim())));
//		List<Integer[]> undiminutedMeterInfo = getUndiminutedMeterInfoOBS();
//		// For each meter
//		Rational prevMeterAsRat = Rational.ZERO;
//		int prevNumBars = 0;
//		Rational prevMt = Rational.ZERO;
//		for (int i = 0; i < undiminutedMeterInfo.size(); i++) {
//			Integer[] currMeterInfo = new Integer[MI_SIZE_TAB];
//			for (int j = 0; j < undiminutedMeterInfo.get(i).length; j++) {
//				currMeterInfo[j] = undiminutedMeterInfo.get(i)[j];
//			}
//			int currNum = currMeterInfo[MI_NUM];
//			int currDen = currMeterInfo[MI_DEN];
//			Rational currMt = 
//				new Rational(currMeterInfo[MI_NUM_MT_FIRST_BAR], 
//				currMeterInfo[MI_DEN_MT_FIRST_BAR]);
//			int currNumBars = (currMeterInfo[MI_LAST_BAR] - 
//				currMeterInfo[MI_FIRST_BAR]) + 1;
//			int currDim = diminutions.get(i);
//			// 1. Meter
//			Rational newMeter = undiminuteMeter(new Rational(currNum, currDen), currDim);
//			currMeterInfo[MI_NUM] = newMeter.getNumer();
//			currMeterInfo[MI_DEN] = newMeter.getDenom();
//			// 2. Metric time
//			currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
//			currMt.reduce();
//			currMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
//			currMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
//			currMeterInfo[MI_DIM] = currDim;
//
//			// Add and update
//			mi.add(currMeterInfo);
//			prevNumBars = currNumBars;
//			prevMt = currMt;
//			prevMeterAsRat = newMeter;
//		}
//		return mi;
//	}


//	/**
//	 * Gets the original (undiminuted) meterInfo.
//	 * 
//	 * @return A list, containing, for each meter<break>
//	 *         <ul>
//	 *         <li> as element 0: the numerator of the meter </li>
//	 * 		   <li> as element 1: the denominator of the meter </li>
//	 *         <li> as element 2: the first (metric) bar in the meter </li>
//	 *         <li> as element 3: the last (metric) bar in the meter </li>
//	 *         <li> as element 4: the numerator of the metric time of that first bar </li>
//	 *         <li> as element 5: the denominator of the metric time of that first bar </li>
//	 *         </ul>
//	 */
//	public List<Integer[]> getUndiminutedMeterInfoOBS() {
//		return undiminutedMeterInfoOBS;
//	}


//	/**
//	 * Gets the meterInfo.
//	 * 
//	 * @return A list whose elements represent the meters in the piece. Each element contains<br>
//	 *         <ul>
//	 *         <li> as element 0: the numerator of the meter (adapted according to the diminution)</li>
//	 *         <li> as element 1: the denominator of the meter (adapted according to the diminution)</li>
//	 *         <li> as element 2: the first (metric) bar in the meter </li>
//	 *         <li> as element 3: the last (metric) bar in the meter </li>
//	 *         <li> as element 4: the numerator of the metric time of that first bar (adapted according to the diminution)</li>
//	 *         <li> as element 5: the denominator of the metric time of that first bar (adapted according to the diminution)</li>
//	 *         <li> as element 6: the diminution for the meter </li>
//	 *         </ul>
//	 *         
//	 *         An anacrusis bar will be denoted with bar numbers 0-0.
//	 */
//	private List<Integer[]> getMeterInfoOBS() {
//		return meterInfoOBS;
//	}

}
