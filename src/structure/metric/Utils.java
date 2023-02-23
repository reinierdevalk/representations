package structure.metric;

import java.util.ArrayList;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import representations.Tablature;

public class Utils {
	
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
			newMeter = new Rational(meter.getNumer(), meter.getDenom() / Math.abs(diminution));
		}
		return newMeter;
	}


	/**
	 * Given a diminuted meter and a diminution, calculates the undiminuted meter. 
	 * This method does the opposite of <code>diminuteMeter()</code>.
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
			return new Rational(meter.getNumer(), meter.getDenom() / diminution); 
		}
		else {
			return new Rational(meter.getNumer(), (meter.getDenom() * Math.abs(diminution)));
		}
	}


	/**
	 * Given a Rational (metric time or metric duration) and a diminution, calculates the 
	 * diminuted Rational.
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
	 * Given a double (time or tempo) and a diminution, calculates the diminuted double.
	 * 
	 * @param d
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
	 * Calculates the time the given metric duration takes in the given tempo. 
	 * 
	 * Formula:
	 * tmp BPM = tmp/60 beats/s --> one whole note (four beats) every 240/tmp s (tmp/60 * x = 4 --> x = 240/tmp)
	 * 							--> ten whole notes every (10*240)/tmp s
	 * 							--> n whole notes every (n*240)/tmp s 
	 */
	// TESTED
	public static long calculateTime(Rational dur, double tempo) {
		double time = (dur.toDouble() * 240) / tempo;
		// Multiply by 1000000 to get time in microseconds; round
		time = Math.round(time * 1000000);
		return (long) time;
	}


	/**
	 * Gets the metric position of the note at the onset time. Returns a Rational[] with 
	 *   <ul>
	 *   <li>As element 0: the bar number, where the denominator is 1.</li>
	 *   <li>As element 1: the position within the bar, reduced and starting at 0/x (where x is the 
	 *                     common denominator, i.e., the product of the denominator of metricTime 
	 *                     and the largest meter denominator).</li>
	 *   </ul>
	 * If there is an anacrusis: if mt falls within the anacrusis, the bar number returned will be 0,
	 * and the position within the bar will be the position as if the anacrusis were a full bar.
	 * <br><br>
	 * Example: a metric time of 9/8 in meter 6/8 returns 2/1 and 3/8 (i.e., the fourth 8th note in bar 2).
	 * 
	 * Convenience method, to be used when when the <code>Transcription</code> or <code>Tablature</code>
	 * from which the meter info is extracted is not available.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static Rational[] getMetricPosition(Rational mt, List<Integer[]> meterInfo) {
		Rational[] metricPos = new Rational[2];

		// 0. Determine the presence of an anacrusis
		boolean containsAnacrusis = false;
		if (meterInfo.get(0)[Tablature.MI_FIRST_BAR] == 0) {
			containsAnacrusis = true;
		}

		// 1. Determine the largest meter denominator and then the common denominator
		int largestMeterDenom = -1;
		for (Integer[] in : meterInfo) {
			if (in[Tablature.MI_DEN] > largestMeterDenom) {
				largestMeterDenom = in[Tablature.MI_DEN];
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
			Integer[] currMeter = 
				new Integer[]{meterInfo.get(i)[Tablature.MI_NUM], meterInfo.get(i)[Tablature.MI_DEN]};
			// factor will always be an int because largestMeterDenom will always be a multiple of currMeter[1]    	
			int factor = (largestMeterDenom / currMeter[1]) * mt.getDenom();  
			metersInLargestDenom.add(new Rational(currMeter[0] * factor, commonDenom));
		}

		// 3. List for the initial meter and any following meter change points the metric time (in commonDenom).
		// The first element of the list will be the metric time of the first full bar
		// The last element of the list will be the metric time of the fictional bar after the last bar
		List<Rational> meterChangePointsMetricTimes = new ArrayList<Rational>();
		// Determine the initial meter change point and set startIndex so that if an anacrusis is present, the
		// first element of argMeterInfo (containing the anacrusis information) is skipped
		int startIndex;
		if (containsAnacrusis) {
			meterChangePointsMetricTimes.add(
				new Rational(metersInLargestDenom.get(0).getNumer(), commonDenom));
			startIndex = 1;
		}
		else {
			meterChangePointsMetricTimes.add(new Rational(0, commonDenom));
			startIndex = 0;
		}
		// Determine the remaining meter change points
		for (int i = startIndex; i < meterInfo.size(); i++) {
			// Determine the number of bars in the current meter
			int numBarsInCurrMeter = 
				(meterInfo.get(i)[Tablature.MI_LAST_BAR] - meterInfo.get(i)[Tablature.MI_FIRST_BAR]) + 1;
			// Determine the metric time of the next meter change point and add it to meterChangePointsMetricTimes
			// NB: When creating the new Rational do not use add() to avoid automatic reduction
			Rational currMeter = metersInLargestDenom.get(i);
			int toAdd = numBarsInCurrMeter * currMeter.getNumer();
			meterChangePointsMetricTimes.add(
				new Rational(meterChangePointsMetricTimes.get(i - startIndex).getNumer() + toAdd, commonDenom));	 	
		}

		// 4. Determine the bar number and the position in the bar, and set metricPos
		// a. If metricTime falls within the anacrusis (the if can only be satisfied if there
		// is an anacrusis)
		if (metricTimeInLargestDenom.getNumer() < meterChangePointsMetricTimes.get(0).getNumer()) {
			// Determine the position in the bar as if it were a full bar 
			Rational lengthAnacrusis = metersInLargestDenom.get(0);
			Rational meterFirstBar = metersInLargestDenom.get(1);
			int toAdd = meterFirstBar.getNumer() - lengthAnacrusis.getNumer();
			Rational posInBar = new Rational(metricTimeInLargestDenom.getNumer() + toAdd, commonDenom);
			posInBar.reduce();
			// Set metricPos; the bar number is 0
			metricPos[0] = new Rational(0, 1);
			metricPos[1] = posInBar;
		}
		// b. If metricTime falls after the anacrusis
		else {
			for (int i = 0; i < meterChangePointsMetricTimes.size() - 1; i++) {
				// Determine the meter change points and bar size (in commonDenom) for the current meter
				Rational currPrev = meterChangePointsMetricTimes.get(i);
				Rational currNext = meterChangePointsMetricTimes.get(i + 1); 
				int currBarSize = metersInLargestDenom.get(i + startIndex).getNumer();

				// If metricTime falls within the current meter change points: determine bar number and position in bar
				if (metricTimeInLargestDenom.isGreaterOrEqual(currPrev) && metricTimeInLargestDenom.isLess(currNext)) {
					// Determine the bar number
					int currDistance = metricTimeInLargestDenom.getNumer() - currPrev.getNumer();
					int numBarsToAdd =	(currDistance - (currDistance % currBarSize)) / currBarSize;   			
					int currBarNum = meterInfo.get(i + startIndex)[Tablature.MI_FIRST_BAR] + numBarsToAdd;
					// Determine the position in the bar
					Rational currPosInBar = new Rational(currDistance % currBarSize, commonDenom);
					currPosInBar.reduce();
					// Set metricPos and break
					metricPos[0] = new Rational(currBarNum, 1);
					metricPos[1] = currPosInBar;
					break;
				}
			}
		}
		return metricPos;
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
					new Rational(in[Tablature.MI_NUM_MT_FIRST_BAR], in[Tablature.MI_DEN_MT_FIRST_BAR]);
				Rational upper = 
					new Rational(meterInfo.get(i+1)[Tablature.MI_NUM_MT_FIRST_BAR], 
					meterInfo.get(i+1)[Tablature.MI_DEN_MT_FIRST_BAR]);
				if (mt.isGreaterOrEqual(lower) && mt.isLess(upper)) {
					diminution = in[Tablature.MI_DIM];
					break;
				}
			}
			// Last (or only) meter: mt must fall in this meter
			else {
				diminution = in[Tablature.MI_DIM];
			}
		}
		return diminution;
	}


	/**
	 * Returns the given metric position as a String.
	 * 
	 * @param metricPosition
	 * @return
	 */
	public static String getMetricPositionAsString(Rational[] metricPosition) {
		int currentBar = metricPosition[0].getNumer();
		Rational currentPositionInBar = metricPosition[1];
		currentPositionInBar.reduce();
		if (currentPositionInBar.getNumer() != 0) {
			return Integer.toString(currentBar).concat(" ").concat(currentPositionInBar.toString());
		}
		else {
			return Integer.toString(currentBar);
		}
	}
}
