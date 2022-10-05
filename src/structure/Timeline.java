package structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import tbp.Encoding;
import tools.ToolBox;

public class Timeline implements Serializable {

	private static final long serialVersionUID = 1L;

	// From Transcription
	public static final int MI_NUM = 0;
	public static final int MI_DEN = 1;
	public static final int MI_FIRST_BAR = 2;
	public static final int MI_LAST_BAR = 3;
	public static final int MI_NUM_MT_FIRST_BAR = 4;
	public static final int MI_DEN_MT_FIRST_BAR = 5;
	public static final int MI_DIM = 6;

	private static final int MI_SIZE_TAB = 7;
	public static final int MI_SIZE_TRANS = 6; // TODO make private

	private List<Integer[]> meterInfo;
	private List<Integer[]> undiminutedMeterInfoOBS;
	private List<Integer[]> meterInfoOBS;	
	private List<Integer[]> diminutionPerBar;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Timeline(Encoding encoding) {
		init(encoding);
	}


	private void init(Encoding encoding) {
//		setUndiminutedMeterInfoOBS(encoding);
//		setMeterInfoOBS(encoding);
		setMeterInfo(encoding);		
		setDiminutionPerBar();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setMeterInfo(Encoding encoding) {
		meterInfo = makeMeterInfo(encoding);
	}


	// TESTED
	List<Integer[]> makeMeterInfo(Encoding encoding) {
		List<Integer[]> meterInfo = new ArrayList<>();

		List<Integer> diminutions = new ArrayList<>();
		String dimStr = encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.DIMINUTION_IND]);
		Arrays.asList(dimStr.split(";")).forEach(d -> diminutions.add(Integer.parseInt(d.trim())));
		
		String[] meters = 
			encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";");		

		Rational prevMeterAsRat = Rational.ZERO;
		int prevNumBars = 0;
		Rational prevMt = Rational.ZERO;
		for (int i = 0; i < meters.length; i++) {
			Integer[] currentMeterInfo = new Integer[MI_SIZE_TAB];
			String currInfo = meters[i].trim();
			// 1. Meter
			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
			int currNum = Integer.parseInt(currMeter.split("/")[0].trim());
			int currDen = Integer.parseInt(currMeter.split("/")[1].trim());
			currentMeterInfo[MI_NUM] = currNum;
			currentMeterInfo[MI_DEN] = currDen;
			// 2. Bar number(s)
			int currNumBars = 0;
			String currBars = 
				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();
			// If the meter is only for a single bar
			if (!currBars.contains("-")) {
				currentMeterInfo[MI_FIRST_BAR] = Integer.parseInt(currBars.trim());
				currentMeterInfo[MI_LAST_BAR] = Integer.parseInt(currBars.trim());
				currNumBars = 1;
			}
			// If the meter is for more than one bar
			else {
				int firstBar = Integer.parseInt(currBars.split("-")[0].trim());
				int lastBar = Integer.parseInt(currBars.split("-")[1].trim());
				currentMeterInfo[MI_FIRST_BAR] = firstBar;
				currentMeterInfo[MI_LAST_BAR] = lastBar;
				currNumBars = (lastBar-firstBar) + 1;
			}
			// 3. Metric times
			Rational currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
			currMt.reduce();
			currentMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
			currentMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
			// 4. Diminution
			currentMeterInfo[MI_DIM] = diminutions.get(i);

			// Add and update
			meterInfo.add(currentMeterInfo);
			prevNumBars = currNumBars;
			prevMt = currMt;
			prevMeterAsRat = new Rational(currNum, currDen);
		}
		return meterInfo;
	}


	void setDiminutionPerBar() {
		diminutionPerBar = makeDiminutionPerBar();
	}


	// TESTED
	List<Integer[]> makeDiminutionPerBar() {
		List<Integer[]> dimPerBar = new ArrayList<>();
		List<Integer[]> mi = getMeterInfo();
//		List<Integer[]> mi = getMeterInfoOBS();
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, MI_FIRST_BAR);
		// In case of an anacrusis, firstBar == 0
		int firstBar = mi.get(0)[MI_FIRST_BAR];
		int lastBar = mi.get(mi.size()-1)[MI_LAST_BAR];
		int currDiminution = 0;
		for (int bar = firstBar; bar <= lastBar; bar++) {
			if (meterChangeBars.contains(bar)) {
				currDiminution = mi.get(meterChangeBars.indexOf(bar))[MI_DIM];
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


	/**
	 * Gets the diminution for each metric bar.
	 * 
	 * @return
	 */
	public List<Integer[]> getDiminutionPerBar() {
		return diminutionPerBar;
	}


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	/**
	 * Given an undiminuted meter and a diminution, calculates the diminuted meter.
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
	public static Rational diminuteMeter(Rational meter, int diminution) {
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
	 * Given a diminuted meter and a diminution, calculates the undiminuted meter. 
	 * This method does the opposite of diminuteMeter().
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
	public static Rational undiminuteMeter(Rational meter, int diminution) {
		if (diminution == 1) {
			return new Rational(meter.getNumer(), meter.getDenom());
		}
		else if (diminution > 0) {
			return new Rational(meter.getNumer(), (int) (meter.getDenom() / diminution)); 
		}
		else {
			return new Rational(meter.getNumer(), (meter.getDenom() * Math.abs(diminution)));
		}
	}


	/**
	 * Given a Rational and a diminution, calculates the diminuted Rational.
	 * 
	 * @param r
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static Rational diminute(Rational r, int diminution) {
		if (diminution == 1) {
			return r;
		}
		else if (diminution > 0) {
			return r.div(diminution); 
		}
		else {
			return r.mul(Math.abs(diminution));
		}
	}


	/**
	 * Given a double and a diminution, calculates the diminuted double.
	 * 
	 * @param r
	 * @param diminution
	 * @return
	 */
	// TESTED
	public static double diminute(double d, int diminution) {
		if (diminution == 1) {
			return d;
		}
		else if (diminution > 0) {
			return d / diminution; 
		}
		else {
			return d * Math.abs(diminution);
		}
	}


	/**
	 * Given an undiminuted metric position, gets the meter section.
	 * 
	 * @param mp
	 * @param meterSectionOnsets
	 * @return
	 */
	// TESTED
	public static int getMeterSection(Rational mp, List<Rational> meterSectionOnsets) {
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
	public static int getMeterSection(long time, List<Long> meterSectionTimes) {
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
	public static Rational getDiminutedMetricPosition(Rational mp, List<Rational> meterSectionOnsets, 
		List<Rational> meterSectionOnsetsDim, List<Integer> diminutions) {

		int section = getMeterSection(mp, meterSectionOnsets);

		// Set mp relative to undiminuted meter section onset
		mp = mp.sub(meterSectionOnsets.get(section));
		// Diminute mp
		mp = diminute(mp, diminutions.get(section));
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
	public static long getDiminutedTime(long time, List<Long> meterSectionTimes, 
		List<Long> meterSectionTimesDim, List<Integer> diminutions) {
		
		int section = getMeterSection(time, meterSectionTimes);

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
	 * Gets the metric position of the note at the onset time. Returns a Rational[] with 
	 *   <ul>
	 *   <li>as element 0: the bar number (whose denominator will always be 1);</li>
	 *   <li>as element 1: the position within the bar, reduced and starting at 0/x (where x is the common denominator,
	 *                 i.e., the product of the denominator of metricTime and the largest meter denominator).</li>
	 *   </ul>
	 * If there is an anacrusis: if mt falls within the anacrusis, the bar number returned will be 0,
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
		if (meterInfo.get(0)[MI_FIRST_BAR] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] in : meterInfo) {
			if (in[MI_DEN] > largestMeterDenom) {
				largestMeterDenom = in[MI_DEN];
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
				new Integer[]{meterInfo.get(i)[MI_NUM], meterInfo.get(i)[MI_DEN]};
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
			int numBarsInCurrentMeter = 
				(meterInfo.get(i)[MI_LAST_BAR] - meterInfo.get(i)[MI_FIRST_BAR]) + 1;
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
						meterInfo.get(i + startIndex)[MI_FIRST_BAR] + numberOfBarsToAdd;
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
				Rational lower = 
					new Rational(in[MI_NUM_MT_FIRST_BAR], in[MI_DEN_MT_FIRST_BAR]);
				Rational upper = 
					new Rational(meterInfo.get(i+1)[MI_NUM_MT_FIRST_BAR], 
					meterInfo.get(i+1)[MI_DEN_MT_FIRST_BAR]);
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
//		List<Integer[]> mi = getMeterInfoOBS();
		int firstBar = mi.get(0)[MI_FIRST_BAR];
		int lastBar = mi.get(mi.size()-1)[MI_LAST_BAR];
		return new Integer[]{lastBar, firstBar == 0 ? 1 : 0};
	}


	void setUndiminutedMeterInfoOBS(Encoding encoding) {
		undiminutedMeterInfoOBS = makeUndiminutedMeterInfoOBS(encoding);
	}


	// TESTED
	List<Integer[]> makeUndiminutedMeterInfoOBS(Encoding encoding) {
		List<Integer[]> undiminutedMeterInfo = new ArrayList<>();

		String[] undiminutedMeters = 
			encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.METER_INFO_IND]).split(";");		
		Rational prevMeterAsRat = Rational.ZERO;
		int prevNumBars = 0;
		Rational prevMt = Rational.ZERO;
		for (int i = 0; i < undiminutedMeters.length; i++) {
			Integer[] currentMeterInfo = new Integer[MI_SIZE_TAB - 1];
			String currInfo = undiminutedMeters[i].trim();
			// 1. Meter
			String currMeter = currInfo.substring(0, currInfo.indexOf("(")).trim();
			int currNum = Integer.parseInt(currMeter.split("/")[0].trim());
			int currDen = Integer.parseInt(currMeter.split("/")[1].trim());
			currentMeterInfo[MI_NUM] = currNum;
			currentMeterInfo[MI_DEN] = currDen;
			// 2. Bar number(s)
			int currNumBars = 0;
			String currBars = 
				currInfo.substring(currInfo.indexOf("(") + 1, currInfo.indexOf(")")).trim();
			// If the meter is only for a single bar
			if (!currBars.contains("-")) {
				currentMeterInfo[MI_FIRST_BAR] = Integer.parseInt(currBars.trim());
				currentMeterInfo[MI_LAST_BAR] = Integer.parseInt(currBars.trim());
				currNumBars = 1;
			}
			// If the meter is for more than one bar
			else {
				int firstBar = Integer.parseInt(currBars.split("-")[0].trim());
				int lastBar = Integer.parseInt(currBars.split("-")[1].trim());
				currentMeterInfo[MI_FIRST_BAR] = firstBar;
				currentMeterInfo[MI_LAST_BAR] = lastBar;
				currNumBars = (lastBar-firstBar) + 1;
			}
			// 3. Metric times
			Rational currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
			currMt.reduce();
			currentMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
			currentMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();

			// Add and update
			undiminutedMeterInfo.add(currentMeterInfo);
			prevNumBars = currNumBars;
			prevMt = currMt;
			prevMeterAsRat = new Rational(currNum, currDen);
		}
		return undiminutedMeterInfo;
	}


	private void setMeterInfoOBS(Encoding encoding) {
		meterInfoOBS = makeMeterInfoOBS(encoding);
	}


	// TESTED
	List<Integer[]> makeMeterInfoOBS(Encoding encoding) {
		List<Integer[]> mi = new ArrayList<>();

		List<Integer> diminutions = new ArrayList<>();
		String dimStr = encoding.getMetadata().get(Encoding.METADATA_TAGS[Encoding.DIMINUTION_IND]);
		Arrays.asList(dimStr.split(";")).forEach(d -> diminutions.add(Integer.parseInt(d.trim())));
		List<Integer[]> undiminutedMeterInfo = getUndiminutedMeterInfoOBS();
		// For each meter
		Rational prevMeterAsRat = Rational.ZERO;
		int prevNumBars = 0;
		Rational prevMt = Rational.ZERO;
		for (int i = 0; i < undiminutedMeterInfo.size(); i++) {
			Integer[] currMeterInfo = new Integer[MI_SIZE_TAB];
			for (int j = 0; j < undiminutedMeterInfo.get(i).length; j++) {
				currMeterInfo[j] = undiminutedMeterInfo.get(i)[j];
			}
			int currNum = currMeterInfo[MI_NUM];
			int currDen = currMeterInfo[MI_DEN];
			Rational currMt = 
				new Rational(currMeterInfo[MI_NUM_MT_FIRST_BAR], 
				currMeterInfo[MI_DEN_MT_FIRST_BAR]);
			int currNumBars = (currMeterInfo[MI_LAST_BAR] - 
				currMeterInfo[MI_FIRST_BAR]) + 1;
			int currDim = diminutions.get(i);
			// 1. Meter
			Rational newMeter = undiminuteMeter(new Rational(currNum, currDen), currDim);
			currMeterInfo[MI_NUM] = newMeter.getNumer();
			currMeterInfo[MI_DEN] = newMeter.getDenom();
			// 2. Metric time
			currMt = prevMt.add(prevMeterAsRat.mul(prevNumBars));
			currMt.reduce();
			currMeterInfo[MI_NUM_MT_FIRST_BAR] = currMt.getNumer();
			currMeterInfo[MI_DEN_MT_FIRST_BAR] = currMt.getDenom();
			currMeterInfo[MI_DIM] = currDim;

			// Add and update
			mi.add(currMeterInfo);
			prevNumBars = currNumBars;
			prevMt = currMt;
			prevMeterAsRat = newMeter;
		}
		return mi;
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
	public List<Integer[]> getUndiminutedMeterInfoOBS() {
		return undiminutedMeterInfoOBS;
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
	private List<Integer[]> getMeterInfoOBS() {
		return meterInfoOBS;
	}

}
