package structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.math.Rational;
import tools.music.TimeMeterTools;

/**
 * Convenience class, complementing <code>MetricalTimeLine</code>. 
 * 
 * Alternative solution: 
 * <ul> 
 * <li>Remove the inheritance, make all instance methods static, and pass the 
 *     <code>MetricalTimeLine</code> as their first argument.</li>
 * <li>Add all methods directly to <code>MetricalTimeLine</code>.</li>
 * </ul>
 * 
 * @author Reinier
 *
 */
public class ScoreMetricalTimeLine extends MetricalTimeLine {

	private static final long serialVersionUID = 1L;

	private List<Rational> meterSectionOnsets;
	private List<Long> meterSectionTimes;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public ScoreMetricalTimeLine(MetricalTimeLine mtl) {
		super(mtl);
		init();
	}


	private void init() {
		setMeterSectionOnsets();
		setMeterSectionTimes();
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setMeterSectionOnsets() {
		meterSectionOnsets = makeMeterSectionOnsets();
	}


	// TESTED
	List<Rational> makeMeterSectionOnsets() {
		List<Rational> mso = new ArrayList<>();
		Arrays.stream(getTimeSignature()).forEach(ts -> mso.add(new Rational(ts[3], ts[4])));
		return mso;
	}


	void setMeterSectionTimes() {
		meterSectionTimes = makeMeterSectionTimes();
	}


	// TESTED
	List<Long> makeMeterSectionTimes() {
		List<Long> mst = new ArrayList<>();
		Arrays.stream(getTimeSignature()).forEach(ts -> mst.add(ts[2]));
		return mst;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public List<Rational> getMeterSectionOnsets() {
		return meterSectionOnsets;
	}


	public List<Long> getMeterSectionTimes() {
		return meterSectionTimes;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	/**
	 * Gets the meter section for the given metric time.
	 * 
	 * @param mt
	 * @return
	 */
	// TESTED
	public int getMeterSection(Rational mt) {
		List<Rational> mso = getMeterSectionOnsets();

		int numSections = mso.size();
		for (int i = 0; i < numSections; i++) {
			// If there is a next section: check if mp is in section at i
			if (i < numSections - 1) {
				if (mt.isGreaterOrEqual(mso.get(i)) && mt.isLess(mso.get(i+1))) {
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
	 * Gets the meter section for the given time.
	 * 
	 * @param time
	 * @return
	 */
	// TESTED
	public int getMeterSection(long time) {
		List<Long> mst = getMeterSectionTimes();
		int numSections = mst.size();
		for (int i = 0; i < numSections; i++) {
			// If there is a next section: check if time is in section at i
			if (i < numSections - 1) {
				if (time >= mst.get(i) && time < mst.get(i+1)) {
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
	 * Gets the diminuted metric time for the given undiminuted metric time.
	 * 
	 * @param mp The undiminuted metric time.
	 * @param mtlDim The <code>MetricalTimeLine</code>, diminuted.
	 * @param diminutions The meter section diminutions.
	 * @return
	 */
	// TESTED
	public Rational getDiminutedMetricTime(Rational mp, ScoreMetricalTimeLine smtlDim, 
		List<Integer> diminutions) {

		int section = getMeterSection(mp);

		// Set mp relative to undiminuted meter section onset
		mp = mp.sub(getMeterSectionOnsets().get(section));
		// Diminute mp
		mp = TimeMeterTools.diminute(mp, diminutions.get(section));
		// Set mp relative to diminuted meter section onset
		mp = smtlDim.getMeterSectionOnsets().get(section).add(mp);

		return mp;
	}


	/**
	 * Gets the metric position for the given metric time.<br><br>
	 * 
	 * NB: See also <code>Timeline.getMetricPosition()</code>.
	 * 
	 * @param mt The metric time.
	 * @return A Rational[] with 
	 *         <ul>
	 *         <li>As element 0: the bar, where the numerator is the bar number, and the denominator 1.</li>
	 *         <li>As element 1: the position within the bar (starting at 0).</li>
	 *		   </ul>
	 */
	// TESTED
	public Rational[] getMetricPosition(Rational mt) {
		long[][] tss = getTimeSignature();

		// Calculate, for each meter section, the number of bars preceding it
		List<Integer> numBarsBeforeMeterSec = new ArrayList<>();
		numBarsBeforeMeterSec.add(0);
		for (int i = 1; i < tss.length; i++) {
			// Number of bars in previous meter section is 
			// (currMso - prevMso) / prevMsBarLen
			int numBarsPrevMeterSec = 
				(int) (new Rational(tss[i][3], tss[i][4]).sub(new Rational(tss[i-1][3], tss[i-1][4]))).div( 
				new Rational(tss[i-1][0], tss[i-1][1])).toDouble();
			numBarsBeforeMeterSec.add(numBarsPrevMeterSec + numBarsBeforeMeterSec.get(i-1));
		}

		// Calculate bar and metric position
		for (int i = 0; i < tss.length; i++) {
			Rational currOns = new Rational(tss[i][3], tss[i][4]);
			if (i < (tss.length - 1) && (mt.isGreaterOrEqual(currOns) && 
				mt.isLess(new Rational(tss[i+1][3], tss[i+1][4]))) || i == (tss.length - 1)) {
				Rational mtInCurrMeterSec = mt.sub(currOns);
				Rational currBarLen = new Rational(tss[i][0], tss[i][1]);
				Rational mp = mt.equals(currOns) ? Rational.ZERO : mtInCurrMeterSec.mod(currBarLen);
				int barInCurrMeterSec = ((int) mtInCurrMeterSec.sub(mp).div(currBarLen).toDouble()) + 1;
				int bar = numBarsBeforeMeterSec.get(i) + barInCurrMeterSec;
				return new Rational[]{new Rational(bar, 1), mp};
			}
		}
		return null;
	}

}
