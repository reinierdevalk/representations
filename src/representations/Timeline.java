package representations;

import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import tools.ToolBox;

public class Timeline {
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

	private List<Integer[]> undiminutedMeterInfo;
	private List<Integer> diminutions;
	private List<Integer[]> meterInfo;	
	private List<Integer[]> diminutionPerBar;
	
	// From Tablature
	// MI_NUM, MI_DEN, MI_FIRST_BAR, MI_LAST_BAR, MI_NUM_MT_FIRST_BAR, and MI_DEN_MT_FIRST_BAR 
	// are the same as for a Transcription, and are defined there
//	private static final int MI_SIZE = 7;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Timeline(Encoding encoding) {
		init(encoding);
	}


	private void init(Encoding encoding) {
		setDiminutions(encoding);
		setUndiminutedMeterInfo(encoding);
		setMeterInfo();
		setDiminutionPerBar();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setDiminutions(Encoding encoding) {
		diminutions = makeDiminutions(encoding);
	}


	// TESTED
	List<Integer> makeDiminutions(Encoding encoding) {
		List<Integer> diminutions = new ArrayList<>();
		String diminutionsStr = encoding.getInfoAndSettings().get(Encoding.DIMINUTION_IND);
		for (String s : diminutionsStr.split(";")) {
			diminutions.add(Integer.parseInt(s.trim()));
		}
		return diminutions;
	}


	void setUndiminutedMeterInfo(Encoding encoding) {
		undiminutedMeterInfo = makeUndiminutedMeterInfo(encoding);
	}


	// TESTED
	List<Integer[]> makeUndiminutedMeterInfo(Encoding encoding) {
		List<Integer[]> undiminutedMeterInfo = new ArrayList<>();

		String[] undiminutedMeters = 
			encoding.getInfoAndSettings().get(Encoding.METER_IND).split(";");		
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


	void setMeterInfo() {
		meterInfo = makeMeterInfo();
	}


	// TESTED
	List<Integer[]> makeMeterInfo() {
		List<Integer[]> mi = new ArrayList<>();

		List<Integer> diminutions = getDiminutions();
		List<Integer[]> undiminutedMeterInfo = getUndiminutedMeterInfo();
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
			Rational newMeter = diminuteMeter(new Rational(currNum, currDen), currDim);

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


	void setDiminutionPerBar() {
		diminutionPerBar = makeDiminutionPerBar();
	}


	// TESTED
	List<Integer[]> makeDiminutionPerBar() {
		List<Integer[]> dimPerBar = new ArrayList<>();
		List<Integer[]> mi = getMeterInfo();
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
	public List<Integer> getDiminutions() {
		return diminutions;
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


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
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

}
