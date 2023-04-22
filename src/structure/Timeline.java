package structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import structure.metric.Utils;
import tbp.Encoding;
import tbp.Symbol;
import tools.ToolBox;

public class Timeline implements Serializable {

	private static final long serialVersionUID = 1L;

//	// From Transcription
//	public static final int MI_NUM = 0;
//	public static final int MI_DEN = 1;
//	public static final int MI_FIRST_BAR = 2;
//	public static final int MI_LAST_BAR = 3;
//	public static final int MI_NUM_MT_FIRST_BAR = 4;
//	public static final int MI_DEN_MT_FIRST_BAR = 5;
//	public static final int MI_DIM = 6;

//	private static final int MI_SIZE_TAB = 7;
//	public static final int MI_SIZE_TRANS = 6;

	private List<Integer[]> bars;
	private List<Integer> diminutions;
	private List<Integer[]> timeSignatures;
//	private List<Integer[]> meterInfo;
	private List<Integer[]> diminutionPerBar;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Timeline() {
	}


	public Timeline(Encoding encoding) {
		init(encoding);
	}


	private void init(Encoding encoding) {
		setBars(encoding);
		setDiminutions(encoding);
		setTimeSignatures(encoding);
//		setMeterInfo(encoding);		
		setDiminutionPerBar();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
//	void setMeterInfo(Encoding encoding) {
//		meterInfo = makeMeterInfo(encoding);
//	}


	void setBars(Encoding encoding) {
		bars = makeBars(encoding);
	}


	// TESTED
	List<Integer[]> makeBars(Encoding encoding) {
		List<Integer[]> b = new ArrayList<>();
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
		return b;
	}


	void setDiminutions(Encoding encoding) {
		diminutions = makeDiminutions(encoding);
	}


	// TESTED
	List<Integer> makeDiminutions(Encoding encoding) {
		List<Integer> d = new ArrayList<>();
		Arrays.stream(encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.DIMINUTION_IND]).split(";"))
			.forEach(m -> d.add(Integer.valueOf(m.trim())));
		return d;
	}


	void setTimeSignatures(Encoding encoding) {
		timeSignatures = makeTimeSignatures(encoding);
	}


	// TESTED
	List<Integer[]> makeTimeSignatures(Encoding encoding) {
		List<Integer[]> ts = new ArrayList<>();
		List<String> meterBars = new ArrayList<>();
		Arrays.stream(encoding.getMetadata()
			.get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";"))
			.forEach(m -> meterBars.add(m.trim()));
		List<Integer[]> bars = getBars();
		for (int i = 0; i < meterBars.size(); i++) {
			String m = meterBars.get(i);
			String meter = m.substring(0, m.indexOf("(")).trim();
			ts.add(new Integer[]{
				Integer.valueOf(meter.split("/")[0]), 
				Integer.valueOf(meter.split("/")[1]),
				i == 0 ? 0 : getTimeSignatureOnset(i, ts, bars)
			});
		}		
		return ts;
	}


//	// TESTED
//	List<Integer[]> makeMeterInfo(Encoding encoding) {
//		List<Integer[]> meterInfo = new ArrayList<>();
//
//		Rational prevMeterAsRat = Rational.ZERO;
//		int prevNumBars = 0;
//		Rational prevMt = Rational.ZERO;
//		for (Integer[] in : encoding.getMetersBarsDiminutions()) {
//			Integer[] currentMeterInfo = new Integer[MI_SIZE_TAB];
//			// 1. Meter
//			currentMeterInfo[MI_NUM] = in[0];
//			currentMeterInfo[MI_DEN] = in[1];
//			// 2. Bar number(s)
//			currentMeterInfo[MI_FIRST_BAR] = in[2];
//			currentMeterInfo[MI_LAST_BAR] = in[3];
//			// 3. Metric times
//			Rational currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
//			currMt.reduce();
//			currentMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
//			currentMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
//			// 4. Diminution
//			currentMeterInfo[MI_DIM] = in[4];
//
//			meterInfo.add(currentMeterInfo);
//			prevNumBars = (in[3] - in[2]) + 1;
//			prevMt = currMt;
//			prevMeterAsRat = new Rational(in[0], in[1]);
//		}
//		return meterInfo;
//	}


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
//	/**
//	 * Gets the meterInfo.
//	 * 
//	 * @return A list whose elements represent the meters in the piece. Each element contains<br>
//	 *         <ul>
//	 *         <li> As element 0: the numerator of the meter.</li>
//	 *         <li> As element 1: the denominator of the meter.</li>
//	 *         <li> As element 2: the first (metric) bar in the meter.</li>
//	 *         <li> As element 3: the last (metric) bar in the meter.</li>
//	 *         <li> As element 4: the numerator of the metric time of that first bar.</li>
//	 *         <li> As element 5: the denominator of the metric time of that first bar.</li>
//	 *         <li> As element 6: the diminution for the meter.</li>
//	 *         </ul>
//	 *         
//	 *         An anacrusis bar would be denoted with bar number 0; however, the current 
//	 *         approach is to pre-pad an anacrusis bar with rests, making it a complete bar.
//	 */
//	public List<Integer[]> getMeterInfo() {
//		return meterInfo;
//	}


	public List<Integer[]> getBars() {
		return bars;
	}


	public List<Integer> getDiminutions() {
		return diminutions;
	}


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
	 * Gets the number of metric bars.
	 * 
	 * @return An Integer[] containing<br>
	 * <ul>
	 * <li>as element 0: the number of metric bars, not counting any anacrusis</li>
	 * <li>as element 1: 1 if there is an anacrusis; 0 if not</li>
	 * </ul>
	 */
	// TESTED
	public Integer[] getNumberOfMetricBars() {
		List<Integer[]> bars = getBars();
		return new Integer[]{bars.get(bars.size()-1)[1], 0};
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


	/**
	 * Given an undiminuted metric position, gets the meter section.
	 * 
	 * @param mp
	 * @param meterSectionOnsets
	 * @return
	 */
	// TESTED
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
	 * Given an undiminuted time, gets the meter section.
	 * 
	 * @param time
	 * @param meterSectionTimes
	 * @return
	 */
	// TESTED
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


	/**
	 * Given an undiminuted metric position, gets the diminuted metric position. 
	 * 
	 * @param mp The undiminuted metric position.
	 * @param meterSectionOnsets The meter section onsets (undiminuted). 
	 * @param meterSectionOnsetsDim The meter section onsets (diminuted).
	 * @param diminutions The meter section diminutions.
	 * @return
	 */
	// TESTED
	private static Rational getDiminutedMetricPositionOLD(Rational mp, List<Rational> meterSectionOnsets, 
		List<Rational> meterSectionOnsetsDim, List<Integer> diminutions) {

		int section = getMeterSectionOLD(mp, meterSectionOnsets);

		// Set mp relative to undiminuted meter section onset
		mp = mp.sub(meterSectionOnsets.get(section));
		// Diminute mp
		mp = Utils.diminute(mp, diminutions.get(section));
		// Set mp relative to diminuted meter section onset 
		mp = meterSectionOnsetsDim.get(section).add(mp);

		return mp;
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
	// TESTED
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


//	void setUndiminutedMeterInfoOBS(Encoding encoding) {
//		undiminutedMeterInfoOBS = makeUndiminutedMeterInfoOBS(encoding);
//	}


//	// TESTED
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


//	// TESTED
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
