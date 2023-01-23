package structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TempoMarker;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.utility.math.Rational;
import representations.Transcription;
import tools.ToolBox;

public class ScorePiece extends Piece {

	private static final long serialVersionUID = 1L;

	private static final Map<Integer, Integer[]> ROOT_MAP;
	static { ROOT_MAP = new LinkedHashMap<Integer, Integer[]>();
		// Key: number of accidentals (negative for flats, positive for sharps)
		// Value: element 0: index in "ABCDEFG" of the root that goes with the number of accidentals
		//        element 1: root alteration, i.e., the number of alterations (sharps/flats) to be 
		//             		 added to the root (sharps if key is positive; else, flats)
		ROOT_MAP.put(0, new Integer[]{2, 0}); // C 
		// Sharps
		ROOT_MAP.put(1, new Integer[]{6, 0}); // G
		ROOT_MAP.put(2, new Integer[]{3, 0}); // D
		ROOT_MAP.put(3, new Integer[]{0, 0}); // A
		ROOT_MAP.put(4, new Integer[]{4, 0}); // E
		ROOT_MAP.put(5, new Integer[]{1, 0}); // B
		ROOT_MAP.put(6, new Integer[]{5, 0}); // F#
		ROOT_MAP.put(7, new Integer[]{2, 1}); // C#
		ROOT_MAP.put(8, new Integer[]{6, 1}); // G#
		ROOT_MAP.put(9, new Integer[]{3, 1}); // D#
		ROOT_MAP.put(10, new Integer[]{0, 1}); // A#
		ROOT_MAP.put(11, new Integer[]{4, 1}); // E#
		// Flats
		ROOT_MAP.put(-1, new Integer[]{5, 0}); // F
		ROOT_MAP.put(-2, new Integer[]{1, 1}); // Bb
		ROOT_MAP.put(-3, new Integer[]{4, 1}); // Eb
		ROOT_MAP.put(-4, new Integer[]{0, 1}); // Ab
		ROOT_MAP.put(-5, new Integer[]{3, 1}); // Db
		ROOT_MAP.put(-6, new Integer[]{6, 1}); // Gb
		ROOT_MAP.put(-7, new Integer[]{2, 1}); // Cb
		ROOT_MAP.put(-8, new Integer[]{5, 1}); // Fb
		ROOT_MAP.put(-9, new Integer[]{1, 2}); // Bbb
		ROOT_MAP.put(-10, new Integer[]{4, 2}); // Ebb
		ROOT_MAP.put(-11, new Integer[]{0, 2}); // Abb
	}

	public ScorePiece (Piece p) {
		super(p);
	}


	/**
	 * Cleans the given <code>MetricalTimeLine</code> (i.e., removes any duplicate 
	 * <code>TimeSignatureMarker</code>s and <code>TempoMarkers</code> from it).
	 * 
	 * A correct <code>MetricalTimeLine</code> consists of the following elements:
	 * <ul>
	 * <li>The zeroMarker (a <code>TimedMetrical</code>).</li>
	 * <li>A <code>TimeSignatureMarker</code>: for the initial time sig.</li>
	 * <li>A <code>TimeSignatureMarker</code> + a <code>TempoMarker</code> (a <code>TimedMetrical</code>): 
	 *     for any following time sig(s).</li>
	 * <li>The endMarker (a <code>TimedMetrical</code>), placed 10/1 (10 whole notes) after the last 
	 *     <code>TimedMetrical</code>, i.e., the zeroMarker (if there is a single <code>TimeSignatureMarker</code>) 
	 *     or the last <code>TempoMarker</code> (if there are multiple <code>TimeSignatureMarkers</code>). The 
	 *     zeroMarker's time is this last <code>TimedMetrical</code>'s time + the time that 10/1 takes in this 
	 *     <code>TimedMetrical</code>'s tempo.</li>
	 * </ul> 
	 * 
	 * @param mtl 
	 * @return 
	 */
	// TESTED
	public static MetricalTimeLine cleanMetricalTimeLine(MetricalTimeLine mtl) {
		MetricalTimeLine mtlClean = initialiseMetricalTimeLine(new MetricalTimeLine());
//		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
//		// zeroMarker, and endMarker) 
//		MetricalTimeLine mtlClean = new MetricalTimeLine();
//		mtlClean.clear();
//		// Add zeroMarker
//		mtlClean.add((Marker) new TimedMetrical(0, Rational.ZERO));

		// Add TimeSignatureMarkers and TempoMarkers 
		List<Rational> mts = new ArrayList<>();
		Rational mtLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {
//				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				Rational mt = m.getMetricTime();
//				long t = mtl.getTime(mt);
				if (!mts.contains(mt)) {
					mtlClean = 
						addToMetricalTimeLine(mtlClean, mt, mtl.getTime(mt), 
						((TimeSignatureMarker) m).getTimeSignature(), null);
					
//					mtlClean.add(new TimeSignatureMarker(tsm.getTimeSignature(), mt));
//					if (mt.isGreater(Rational.ZERO)) {
//						mtlClean.add(new TempoMarker(t, mt));
////						if (mt.isGreater(mtLastTimedMetrical)) {
////							mtLastTimedMetrical = mt;
////						}
//					}
					
					if (mt.isGreater(mtLastTimedMetrical)) {
						mtLastTimedMetrical = mt;
					}
					mts.add(mt);
				}
			}
		}

		long tLastTimedMetrical = mtlClean.getTime(mtLastTimedMetrical);
		mtlClean = 
			finaliseMetricalTimeLine(mtlClean, mtLastTimedMetrical, tLastTimedMetrical, 
			mtl.getTempo(tLastTimedMetrical), 1);
		
//		// If TempoMarkers (and through them, endMarker(s)) have been added: 
//		// remove all TimedMetricals but the zeroMarker	
//		if (mtLastTimedMetrical.isGreater(Rational.ZERO)) {
//			mtlClean = cleanTimedMetricals(mtlClean);
//		}
//
//		// Add endMarker
//		long tLastTimedMetrical = mtlClean.getTime(mtLastTimedMetrical);
//		TimedMetrical end = 
//			calculateEndMarker(tLastTimedMetrical, mtl.getTempo(tLastTimedMetrical), 
//			mtLastTimedMetrical, 1);
//		mtlClean.add((Marker) end);

		return mtlClean;
	}


	// NOT TESTED (wrapper method)
	static MetricalTimeLine initialiseMetricalTimeLine(MetricalTimeLine mtl) {
		// Clear the default TimeSignatureMarker, zeroMarker, and endMarker, and
		// add a new zeroMarker
		mtl.clear();
		mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
		return mtl;
	}


	// NOT TESTED (wrapper method)
	static MetricalTimeLine addToMetricalTimeLine(MetricalTimeLine mtl, Rational mt, long t, 
		TimeSignature ts, List<Double[]> tempiDim) {
		mtl.add(new TimeSignatureMarker(ts, mt));
		if (mt.isGreater(Rational.ZERO)) {
			mtl.add(new TempoMarker(t, mt));
			if (tempiDim != null) {
				double tmpDim = 
					tempiDim.get(ToolBox.getItemsAtIndex(tempiDim, 1).indexOf((double) t))[0];
				mtl.setTempo(mt, tmpDim, 4);
			}
		}
		return mtl;
	}


	// NOT TESTED (wrapper method)
	static MetricalTimeLine finaliseMetricalTimeLine(MetricalTimeLine mtl, Rational mtLastTimedMetrical, 
		long tLastTimedMetrical, double tempo, int dim) {
		// If TempoMarkers (and, through them, endMarker(s)) have been added: 
		// remove all TimedMetricals but the zeroMarker	
		if (mtLastTimedMetrical.isGreater(Rational.ZERO)) {
			mtl = cleanTimedMetricals(mtl);
		}

		// Add endMarker
		TimedMetrical end = 
			calculateEndMarker(tLastTimedMetrical, tempo, mtLastTimedMetrical, dim);
		mtl.add((Marker) end);

		return mtl;
	}


	/**
	 * Removes all TimedMetricals but the zeroMarker from the given mtl.
	 * 
	 * @param mtl
	 * @return
	 */
	// TESTED
	static MetricalTimeLine cleanTimedMetricals(MetricalTimeLine mtl) {
		List<Integer> indsToRemove = new ArrayList<>();
		for (int i = 0; i < mtl.size(); i++) {
			Marker m = mtl.get(i);
			if (m instanceof TimedMetrical && !(m instanceof TempoMarker)) {
				if (!m.getMetricTime().equals(Rational.ZERO)) {
					indsToRemove.add(i);
				}
			}
		}
		for (int i = 0; i < indsToRemove.size(); i++) {
			mtl.remove(indsToRemove.get(i) - i);
		}

		return mtl;
	}


	/**
	 * Calculates the endMarker given the time, tempo, and metric time of the last TimedMetrical 
	 * before the endMarker. The endMarker is placed ten whole notes (10/1) after the last 
	 * TimedMetrical, meaning that its metric time is that of the last TimedMetrical + 10/1, and 
	 * its time is that of the last TimedMetrical + the time 10/1 takes in the tempo at the last
	 * TimedMetrical.
	 * 
	 * @param tLastTimedMetrical
	 * @param tempoLastTimedMetrical
	 * @param mtLastTimedMetrical
	 * @param dim
	 * @return
	 */
	// TESTED
	static TimedMetrical calculateEndMarker(long tLastTimedMetrical, double tmpLastTimedMetrical, 
		Rational mtLastTimedMetrical, int dim) {
		Rational r = Timeline.diminute(new Rational(10, 1), dim);
		return new TimedMetrical(
			tLastTimedMetrical + calculateTime(r, tmpLastTimedMetrical), 
			mtLastTimedMetrical.add(r));
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
	static long calculateTime(Rational dur, double tempo) {
		double time = (dur.toDouble() * 240) / tempo;
		// Multiply by 1000000 to get time in microseconds; round
		time = Math.round(time * 1000000);
		return (long) time;
	}


	/**
	 * Aligns the given <code>MetricalTimeLine</code> (from a <code>Transcription</code>) 
	 * with the given <code>Timeline</code> (from a <code>Tablature</code>).
	 * 
	 * This is necessary if in the <code>Tablature</code> different diminutions are used for
	 * a section that is in a single meter in the <code>Transcription</code>, resulting in 
	 * the <code>MetricalTimeLine</code> lacking the repeated meter 'changes'. In such cases, 
	 * the <code>MetricalTimeLine</code> is aligned with the <code>Timeline</code> by adding 
	 * each repeated meter at the appropriate onset to the <code>MetricalTimeLine</code> (i.e.,
	 * adding <code>TimeSignatureMarkers</code> and <code>TempoMarkers</code>). Example: <br> 
	 * meters from <code>TimeLine</code> 			2/2, 2/4, 2/2, 3/4, 2/2 <br>
	 * diminutions from <code>TimeLine</code> 		2,   4,   2,   4,   2   <br>
	 * meters from <code>MetricalTimeLine</code>	2/1, ..., ..., 3/1,	2/1 <br>
	 * meters from MTL, aligned						2/1, 2/1, 2/1, 3/1, 2/1 <br>
	 * 
	 * @param mtl
	 * @param tl
	 * @return
	 */
	// TESTED
	public static MetricalTimeLine alignMetricalTimeLine(MetricalTimeLine mtl, Timeline tl) {
		// Align mtl with tl. Examples where this is necessary:
		// 4465_33-34_memor_esto-2.tbp / Jos1714-Memor_esto_verbi_tui-166-325.mid
		// meters      2/2, 2/4, 2/2, 2/4, 2/2, 2/4, 2/2 
		// diminutions 2,   4,   2,   4,   2,   4,   2
		// =           2/1, 2/1, 2/1, 2/1, 2/1, 2/1, 2/1 
		// in Piece    2/1 
		// 5263_12_in_exitu_israel_de_egipto_desprez-3.tbp / Jos1704-In_exitu_Israel_de_Egypto-281-401.mid
		// meters      2/2, 3/4, 2/2, 3/4, 2/2, 2/4, 2/2, 3/4, 2/2
		// diminutions 2,   4,   2,   4,   2,   4,   2,   4,   2
		// =           2/1, 3/1, 2/1, 3/1, 2/1, 2/1, 2/1, 3/1, 2/1
		// in Piece    2/1, 3/1, 2/1, 3/1, 2/1,           3/1, 2/1

		// 1. Get undiminuted meters and meter section onsets from meterInfoTab to enable aligning
		List<Rational> metersTabUndim = new ArrayList<>();
		List<Rational> msosTabUndim = new ArrayList<>();
		List<Integer[]> meterInfoTab = tl.getMeterInfo();
		for (int i = 0; i < meterInfoTab.size(); i++) {
			Integer[] currMi = meterInfoTab.get(i);
			metersTabUndim.add(Timeline.undiminuteMeter(
				new Rational(currMi[Timeline.MI_NUM], currMi[Timeline.MI_DEN]), currMi[Timeline.MI_DIM]));
			Rational msoTabUndim;
			if (i == 0) {
				msoTabUndim = 
					new Rational(currMi[Timeline.MI_NUM_MT_FIRST_BAR], currMi[Timeline.MI_DEN_MT_FIRST_BAR]);
			}
			else {
				Integer[] prevMi = meterInfoTab.get(i-1);
				int numBarsPrevMeter = (prevMi[Timeline.MI_LAST_BAR] - prevMi[Timeline.MI_FIRST_BAR]) + 1; 
				msoTabUndim = msosTabUndim.get(i-1).add(metersTabUndim.get(i-1).mul(numBarsPrevMeter));
			}
			msosTabUndim.add(msoTabUndim);
		}

		// 2. Align
		MetricalTimeLine mtlAligned = initialiseMetricalTimeLine(new MetricalTimeLine());
//		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
//		// zeroMarker, and endMarker) 
//		MetricalTimeLine mtlAligned = new MetricalTimeLine();
//		mtlAligned.clear();
//		// Add zeroMarker
//		mtlAligned.add((Marker) new TimedMetrical(0, Rational.ZERO));
		
		// Add TimeSignatureMarkers and TempoMarkers 
		int ind = 0; // equals index in meterInfoTab
		Rational mtLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {
//				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				TimeSignature ts = ((TimeSignatureMarker) m).getTimeSignature();
				Rational mt = m.getMetricTime();
//				long t = mtl.getTime(mt);
				mtlAligned = addToMetricalTimeLine(mtlAligned, mt, mtl.getTime(mt), ts, null);				
//				mtlAligned.add(new TimeSignatureMarker(ts, mt));
//				if (mt.isGreater(Rational.ZERO)) {
//					mtlAligned.add(new TempoMarker(t, mt));
////					if (mt.isGreater(mtLastTimedMetrical)) {
////						mtLastTimedMetrical = mt;
////					}
//				}
				if (mt.isGreater(mtLastTimedMetrical)) {
					mtLastTimedMetrical = mt;
				}
				
				// If the meter and meter section onset at index ind in tl are not the same
				// as those in m, mtl is not aligned with tl: add Markers to mtlAligned
				Rational meterTabUndim = metersTabUndim.get(ind);
				Rational msoTabUndim = msosTabUndim.get(ind);
				boolean isAligned = 
					meterTabUndim.equals(new Rational(ts.getNumerator(), ts.getDenominator())) && 
					msoTabUndim.equals(mt);
//					msoTabUndim.equals(tsm.getMetricTime());
				if (!isAligned) {
					mtlAligned.add(new TimeSignatureMarker(new TimeSignature(meterTabUndim), msoTabUndim));
					mtlAligned.add(new TempoMarker(mtl.getTime(msoTabUndim), msoTabUndim));
					if (msoTabUndim.isGreater(mtLastTimedMetrical)) {
						mtLastTimedMetrical = msoTabUndim;
					}
				}
				ind++;
			}
		}
		// If mtl and tl are still not aligned, the last time sig in mtl (which can be 
		// the only one) has not been added often enough
		long[][] allTss = mtlAligned.getTimeSignature();
		if (allTss.length < meterInfoTab.size()) {
			Rational lastTs = new Rational(allTss[allTss.length-1][0], allTss[allTss.length-1][1]);
			for (int i = allTss.length; i < meterInfoTab.size(); i++) {
				Rational msoTabUndim = msosTabUndim.get(i);
				mtlAligned.add(new TimeSignatureMarker(new TimeSignature(lastTs), msoTabUndim));
				mtlAligned.add(new TempoMarker(mtl.getTime(msoTabUndim), msoTabUndim));
				if (msoTabUndim.isGreater(mtLastTimedMetrical)) {
					mtLastTimedMetrical = msoTabUndim;
				}
			}
		}

		long tLastTimedMetrical = mtlAligned.getTime(mtLastTimedMetrical);
		mtlAligned = 
			finaliseMetricalTimeLine(mtlAligned, mtLastTimedMetrical, 
			tLastTimedMetrical, mtl.getTempo(tLastTimedMetrical), 1);
//		// If TempoMarkers (and through them, endMarker(s)) have been added: 
//		// remove all TimedMetricals but the zeroMarker	
//		if (mtLastTimedMetrical.isGreater(Rational.ZERO)) {
//			mtlAligned = cleanTimedMetricals(mtlAligned);
//		}
//
//		// Add endMarker
//		long tLastTimedMetrical = mtlAligned.getTime(mtLastTimedMetrical);
//		TimedMetrical end = 
//			calculateEndMarker(tLastTimedMetrical, mtl.getTempo(tLastTimedMetrical), 
//			mtLastTimedMetrical, 1);
//		mtlAligned.add((Marker) end);

		return mtlAligned;
	}


	/**
	 * Diminutes the given <code>MetricalTimeLine</code> according to the given <code>Timeline</code>.
	 * 
	 * NB: Only the time signatures and the meter section onsets are diminuted; not the
	 *     meter section times.
	 * 
	 * @param mtl
	 * @param tl
	 * @return
	 */
	// TESTED
	public static MetricalTimeLine diminuteMetricalTimeLine(MetricalTimeLine mtl, Timeline tl) {
		// 1. Get diminuted tempi
		List<Integer[]> meterInfoTab = tl.getMeterInfo();
		List<Integer> diminutions = ToolBox.getItemsAtIndex(meterInfoTab, Timeline.MI_DIM);
		List<Double[]> tempiDim = new ArrayList<>();
		int ind = 0; // equals index in meterInfoTab
		for (int i = 0; i < mtl.size()-1; i++) { // exclude endMarker
			Marker m = mtl.get(i);
			if (m instanceof TimedMetrical) {
				long time = mtl.getTime(m.getMetricTime());
				double tempo = mtl.getTempo(time);
				int dim = diminutions.get(ind);
				double tempoDim = Timeline.diminute(tempo, dim);
				tempiDim.add(new Double[]{tempoDim, (double) time});
				ind++;
			}
		}

		// 2. Diminute
		MetricalTimeLine mtlDim = initialiseMetricalTimeLine(new MetricalTimeLine());
//		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
//		// zeroMarker, and endMarker) 
//		MetricalTimeLine mtlDim = new MetricalTimeLine();
//		mtlDim.clear();
//		// Add zeroMarker
//		mtlDim.add((Marker) new TimedMetrical(0, Rational.ZERO));

		// Add TimeSignatureMarkers and TempoMarkers		
		ind = 0; // equals index in meterInfoTab
		Rational mtLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {
				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				Rational mtUndim = m.getMetricTime();
				Rational mtDim;
				if (ind == 0) {
					mtDim = mtUndim;
				}
				else {
					Integer[] prevMi = meterInfoTab.get(ind-1);
					long[] prevTsDim = mtlDim.getTimeSignature()[ind-1];
					Rational prevMeterDim = 
						new Rational(prevMi[Timeline.MI_NUM], prevMi[Timeline.MI_DEN]);
					int prevNumBars = 
						(prevMi[Timeline.MI_LAST_BAR] - prevMi[Timeline.MI_FIRST_BAR]) + 1;
					mtDim = 
						new Rational(prevTsDim[3], prevTsDim[4]).add(prevMeterDim.mul(prevNumBars));
					// By uncommenting the lines below, the meter section time is diminuted
//					long[] prevTsUndim = mtl.getTimeSignature()[ind-1];
//					long prevSecLenUndim = mtl.getTime(msoUndim) - prevTsUndim[2];
//					int prevDim = prevMi[Timeline.MI_DIM];
//					long prevSecLenDim = prevDim > 0 ? prevSecLenUndim / prevDim : prevSecLenUndim * Math.abs(prevDim);
//					mst = prevTsDim[2] + prevSecLenDim;
				}
				long t = mtl.getTime(mtUndim); // NB: is not diminuted
				TimeSignature tsUndim = tsm.getTimeSignature();
				TimeSignature tsDim = 
					new TimeSignature(Timeline.diminuteMeter(new Rational(tsUndim.getNumerator(), 
					tsUndim.getDenominator()), diminutions.get(ind)));
				
				mtlDim = addToMetricalTimeLine(mtlDim, mtDim, t, tsDim, tempiDim);
				
//				mtlDim.add(new TimeSignatureMarker(tsDim, mtDim));
//				if (mtDim.isGreater(Rational.ZERO)) {
//					mtlDim.add(new TempoMarker(t, mtDim));
////					if (mtDim.isGreater(mtLastTimedMetrical)) {
////						mtLastTimedMetrical = mtDim;
////					}
//					// Set tempo
//					double tmpDim = 
//						tempiDim.get(ToolBox.getItemsAtIndex(tempiDim, 1).indexOf((double) t))[0];
//					mtlDim.setTempo(mtDim, tmpDim, 4);
//				}
				if (mtDim.isGreater(mtLastTimedMetrical)) {
					mtLastTimedMetrical = mtDim;
				}
				ind++;
			}
		}

		long tLastTimedMetrical = mtlDim.getTime(mtLastTimedMetrical);
		mtlDim = finaliseMetricalTimeLine(mtlDim, mtLastTimedMetrical, tLastTimedMetrical, 
			tempiDim.get(tempiDim.size()-1)[0], diminutions.get(diminutions.size()-1));
				
//		// If TempoMarkers (and through them, endMarker(s)) have been added: 
//		// remove all TimedMetricals but the zeroMarker	
//		if (mtLastTimedMetrical.isGreater(Rational.ZERO)) {
//			mtlDim = cleanTimedMetricals(mtlDim);
//		}
//
//		// Add endMarker
//		long tLastTimedMetrical = mtlDim.getTime(mtLastTimedMetrical);
//		TimedMetrical end = 
//			calculateEndMarker(tLastTimedMetrical, 
//			tempiDim.get(tempiDim.size()-1)[0],	
////			Timeline.diminute(mtl.getTempo(tLastTimedMetrical), diminutions.get(diminutions.size()-1)), 
//			mtLastTimedMetrical, diminutions.get(diminutions.size()-1));
//		mtlDim.add((Marker) end);

		return mtlDim; 
	}


	/**
	 * Cleans the given harmony track (i.e., removes any duplicate <code>KeyMarker</code>s 
	 * from it). 
	 * 
	 * @param ht 
	 * @return
	 */
	// TESTED 
	public static SortedContainer<Marker> cleanHarmonyTrack(SortedContainer<Marker> ht) {
		SortedContainer<Marker> htClean = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());
		List<Rational> mts = new ArrayList<>();
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				Rational mt = m.getMetricTime();
				if (!mts.contains(mt)) {
					htClean.add(km);
					mts.add(mt);
				}
			}
		}
		return htClean;
	}


	/**
	 * Transposes the given harmony track according to the given transposition.
	 * 
	 * @param ht
	 * @param transposition
	 * @return
	 */
	// TESTED
	public static SortedContainer<Marker> transposeHarmonyTrack(SortedContainer<Marker> ht, int transposition) {
		SortedContainer<Marker> htTrn = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;		
				// Determine the number of accidentals and redefine km by setting
				// - alterationNum: the number of accidentals, negative for flats and positive for sharps
				// - mode: MODE_MINOR or MODE_MAJOR
				// - root: the root, always the major parallel, even when mode is MODE_MINOR. Example:
				//         A major: root = 'A', alterationNum = 3; rootAlteration = 0; mode = Mode.MODE_MAJOR
				//         F# minor: root = 'A', alterationNum = 3; rootAlteration = 0; mode = Mode.MODE_MINOR 				               
				// - rootAlteration: the number of flats/sharps that must be added to the root
				// NB: setAlterationNumAndMode() should set all four at once, but doesn't work correctly 
				//     for root and rootAlteration (the problem is in determineRootAndAccidental()); therefore, 
				//     root and rootAlteration must additionally be set manually 
				int accid = transposeNumAccidentals(transposition, km.getAlterationNum());
				km.setAlterationNumAndMode(accid, km.getMode());
				Integer[] rra = ROOT_MAP.get(accid);
				km.setRoot("ABCDEFG".charAt(rra[0]));
				km.setRootAlteration(rra[1]);
				htTrn.add(km);
			}
		}
		return htTrn;
	}


	/**
	 * Given a number of accidentals and a transposition, returns the smallest new number of 
	 * accidentals (there are always two outcomes, sharps and flats.
	 * 
	 * Examples: 
	 * Transposition 2 semitones down from F major: transp = -2 (or +10); accid = -1;
	 * new number is -1 + -2 = -3 (3b, i.e., Eb major) OR -1 + 10 = 9 (9#, i.e., D# major) 
	 * --> -3 is returned
	 * Transposition 1 semitone down from B major: transp = -1 (or +11); accid = 5;
	 * new number is 5 + 5 = 10 (10#, i.e., A#) OR 5 + (-7) = -2 (2b, i.e., Bb major) 
	 * --> -2 is returned
	 * 
	 * @param transposition
	 * @param accid
	 * @return
	 */
	// TESTED
	static int transposeNumAccidentals(int transposition, int accid) {
		// transposition	#accidentals
		// -1 or 11			+5/-7
		// -2 or 10			-2/+10
		// -3 or 9			+3/-9
		// -4 or 8			-4/+8
		// -5 or 7			+1/-11
		// -6 or 6			-6/+6
		// -7 or 5			-1/+11
		// -8 or 4			+4/-8
		// -9 or 3			-3/+9
		// -10 or 2			+2/-10
		// -11 or 1			-5/+7
		//
		// The 1st and 2nd row elements are the transposition; the 3rd and 4th are the 
		// two ways by which accid can be altered
		List<Integer[]> transpMatrix = new ArrayList<>();
		transpMatrix.add(new Integer[]{0, 0, 0, 0});
		transpMatrix.add(new Integer[]{-1, 11, 5, -7});
		transpMatrix.add(new Integer[]{-2, 10, -2, 10});
		transpMatrix.add(new Integer[]{-3, 9, 3, -9});
		transpMatrix.add(new Integer[]{-4, 8, -4, 8});
		transpMatrix.add(new Integer[]{-5, 7, 1, -11});
		transpMatrix.add(new Integer[]{-6, 6, -6, 6});
		transpMatrix.add(new Integer[]{-7, 5, -1, 11});
		transpMatrix.add(new Integer[]{-8, 4, 4, -8});
		transpMatrix.add(new Integer[]{-9, 3, -3, 9});
		transpMatrix.add(new Integer[]{-10, 2, 2, -10});
		transpMatrix.add(new Integer[]{-11, 1, -5, 7});
		
		int col = (transposition < 0) ? 0 : 1;
		int	rowInd = ToolBox.getItemsAtIndex(transpMatrix, col).indexOf(transposition);
		Integer[] in = transpMatrix.get(rowInd);
		int optionA = accid + in[2];
		int optionB = accid + in[3];
		return Math.abs(optionA) <= Math.abs(optionB) ? optionA : optionB;
//		if (Math.abs(optionA) <= Math.abs(optionB)) {
//			return optionA;
//		}
//		else {
//			return optionB;
//		}
	}


	/**
	 * Diminutes the given harmony track according to the given <code>Timeline</code> 
	 * and undiminuted and diminuted <code>MetricalTimeLine</code>.
	 * 
	 * @param ht 
	 * @param tl
	 * @param mtl
	 * @param mtlDim
	 * @return
	 */
	// TESTED
	public static SortedContainer<Marker> diminuteHarmonyTrack(SortedContainer<Marker> ht, Timeline tl, 
		MetricalTimeLine mtl, MetricalTimeLine mtlDim) {
		SortedContainer<Marker> htDim = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		List<Rational> msoUndim = new ArrayList<>();
		Arrays.stream(mtl.getTimeSignature()).forEach(ts -> msoUndim.add(new Rational(ts[3], ts[4])));
//		List<Rational> msoUndim = ToolBox.getItemsAtIndex(getMeterSections(mtl), 1);
		List<Rational> msoDim = new ArrayList<>();
		Arrays.stream(mtlDim.getTimeSignature()).forEach(ts -> msoDim.add(new Rational(ts[3], ts[4])));
//		List<Rational> msoDim = ToolBox.getItemsAtIndex(getMeterSections(mtlDim), 1);
		
		List<Integer> diminutions = ToolBox.getItemsAtIndex(tl.getMeterInfo(), Timeline.MI_DIM);
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				km.setMetricTime(Timeline.getDiminutedMetricPosition(m.getMetricTime(), 
					msoUndim, msoDim, diminutions));
				htDim.add(km);
			}
		}
		return htDim;
	}


	/**
	 * Gets, for each meter section in the given <code>MetricalTimeLine</code>, a Rational[] containing
	 * <ul>
	 * <li>As element 0: the time signature for the meter section.</li>
	 * <li>As element 1: the metric time for the meter section.</li>
	 * </ul>  
	 * @param argMtl
	 * @return
	 */
	// TESTED
	public static List<Rational[]> getMeterSections(MetricalTimeLine argMtl) {
		List<Rational[]> ms = new ArrayList<>();
		Arrays.stream(argMtl.getTimeSignature())
			.forEach(ts -> ms.add(
				new Rational[]{
				new Rational((int) ts[0], (int) ts[1]), // cast necessary to avoid reduction	
				new Rational(ts[3], ts[4])
			})
		);
		return ms; 
	}


	/**
	 * Transposes the given <code>NotationSystem</code> according to the given transposition.
	 * 
	 * @param ns
	 * @param transposition
	 * @return 
	 */
	// TESTED
	public static NotationSystem transposeNotationSystem(NotationSystem ns, int transposition) {
		ns.getContentsRecursiveList(null).stream()
			.filter(c -> c instanceof Note)
			.forEach(c -> {
				int p = ((Note) c).getScoreNote().getMidiPitch() + transposition;
				((Note) c).getScoreNote().setPitch(new ScorePitch(p));
				((Note) c).getPerformanceNote().setPitch(p);
			}
			);
		return ns;
	}


	/**
	 * Diminutes the given <code>NotationSystem</code> according to the given 
	 * <code>Timeline</code> and undiminuted and diminuted <code>MetricalTimeLine</code>.
	 * 
	 * @param ns
	 * @param tl
	 * @param mtl
	 * @param mtlDim
	 * @return
	 */
	// TESTED
	public static NotationSystem diminuteNotationSystem(NotationSystem ns, Timeline tl, 
		MetricalTimeLine mtl, MetricalTimeLine mtlDim) {
//		NotationSystem ns = argPiece.getScore();
		NotationSystem nsDim = new NotationSystem();
		for (int v = 0; v < ns.size(); v++) {
			NotationStaff nstDim = new NotationStaff();
			nstDim.add(new NotationVoice());
			nsDim.add(nstDim);
		}

		List<Rational> msoUndim = new ArrayList<>();
		Arrays.stream(mtl.getTimeSignature()).forEach(ts -> msoUndim.add(new Rational(ts[3], ts[4])));
//		List<Rational> msoUndim = ToolBox.getItemsAtIndex(getMeterSections(mtl), 1);
		List<Rational> msoDim = new ArrayList<>();
		Arrays.stream(mtlDim.getTimeSignature()).forEach(ts -> msoDim.add(new Rational(ts[3], ts[4])));
//		List<Rational> msoDim = ToolBox.getItemsAtIndex(getMeterSections(mtlDim), 1);

		// 1. Get, per voice, the diminuted NotationChords for each meter section
///		// Initialise meterSectionsPerVoice with empty lists  
///		List<List<List<NotationChord>>> meterSectionsPerVoice = new ArrayList<>();
///		for (int v = 0; v < ns.size(); v++) {
///			List<List<NotationChord>> currVoice = new ArrayList<>();
/////			for (int dim : diminutions) {
/////			for (int j = 0; j < meterInfoTab.size(); j++) {	
/////				currVoice.add(new ArrayList<>());
/////			}
///			diminutions.forEach(d -> currVoice.add(new ArrayList<>()));
///			meterSectionsPerVoice.add(currVoice);
///		}
///		// Fill meterSectionsPerVoice
///		// meterSectionsPerVoice.get(v)       : the sections for voice v
///		// meterSectionsPerVoice.get(v).get(s): the notes for section s in voice v

		List<Integer> diminutions = ToolBox.getItemsAtIndex(tl.getMeterInfo(), Timeline.MI_DIM);
		for (int v = 0; v < ns.size(); v++) {
			NotationVoice nv = ns.get(v).get(0);
			for (NotationChord nc : nv) {
				// All notes in nc have the same (metric) time
				Rational mt = nc.getMetricTime();
				long time = nc.getTime();
				int sec = Timeline.getMeterSection(mt, msoUndim);
				// Diminute the notes in nc (and therewith nc itself) and add to nsDim
				for (Note n : nc) {
					// Adapt ScoreNote
					ScoreNote sn = n.getScoreNote();
					Rational onsDim = 
						Timeline.getDiminutedMetricPosition(mt, msoUndim, 
						msoDim, diminutions);
					sn.setMetricTime(onsDim);
					Rational durDim = Timeline.diminute(n.getMetricDuration(), diminutions.get(sec));
					sn.setMetricDuration(durDim);
					n.setScoreNote(sn);
					// Adapt PerformanceNote
					PerformanceNote pn = n.getPerformanceNote();
					long duration = n.getDuration();
//					// By uncommenting the lines below, the time and duration are diminuted
//					time = 
//						Timeline.getDiminutedTime(time, meterSectionTimesUndim, meterSectionTimesDim, 
//						diminutions);
//					long duration = dim > 0 ? n.getDuration() / dim : n.getDuration() * Math.abs(dim);
					pn.setTime(time);
					pn.setDuration(duration);
					n.setPerformanceNote(pn);
				}
///				meterSectionsPerVoice.get(v).get(sec).add(nc);
				nsDim.get(v).get(0).add(nc);
			}
		}
///		// 2. Create diminuted NotationSystem from meterSectionsPerVoice 
///		// For each voice
///		for (int i = 0; i < meterSectionsPerVoice.size(); i++) {
///			NotationStaff nstDim = new NotationStaff();
///			NotationVoice nvDim = new NotationVoice();
///			// For each section
///			for (List<NotationChord> l : meterSectionsPerVoice.get(i)) {
///				for (NotationChord nc : l) {
///					nvDim.add(nc);
///				}
///			}
///			nstDim.add(nvDim);
///			nsDim.add(nstDim);
///		}

		return nsDim;
	}


	/**
	 * Creates a Note with the given pitch, MetricTime, and MetricDuration. 
	 * 
	 * @param pitch
	 * @param metricTime
	 * @param metricDuration
	 * @param mtl
	 * @return
	 */
	// TODO test
	public static Note createNote(int pitch, Rational metricTime, Rational metricDuration, 
		MetricalTimeLine mtl) {
		// A Note consists of a ScoreNote and a PerformanceNote; each need to be created 
		// separately first 
		// 1. Create the ScoreNote
		ScorePitch scorePitch = new ScorePitch(pitch);
		ScoreNote scoreNote = new ScoreNote(scorePitch, metricTime, metricDuration);
		// 2. Create the PerformanceNote. The argumentless constructor can be used; after 
		// the creation only the object variable pitch needs to be set: the others 
		// (duration, velocity, and generated) are irrelevant here
		
		PerformanceNote performanceNote;
		if (mtl == null) {
			performanceNote = new PerformanceNote(0, 0, 127, pitch);
		}
		else {
			performanceNote = scoreNote.toPerformanceNote(mtl);
//			System.out.println(performanceNote.getPitch());
//			System.out.println(performanceNote.getTime());
//			System.out.println(performanceNote.getVelocity());
//			System.out.println(performanceNote.getDuration());
		}
		performanceNote = MidiNote.convert(performanceNote);
		
//		PerformanceNote performanceNote = new PerformanceNote();
//		performanceNote.setPitch(pitch);

		Note note = new Note(scoreNote, performanceNote);
		// TODO? OR, as PerformanceNote does not really apply in our case:
//		MidiNote midiNote = MidiNote.convert(performanceNote);
//		Note note = new Note(scoreNote, midiNote);

		return note;
	}


	/**
	 * Augments the <code>ScorePiece</code> according to the given augmentation. Must be
	 * called on a <code>ScorePiece</code> extracted from an existing <code>Transcription</code>.
	 * 
	 * @param mp
	 * @param chords
	 * @param allOnsetTimes
	 * @param thresholdDur
	 * @param rescaleFactor
	 * @param augmentation
	 */
	// NOT TESTED (wrapper method)
	public void augment(Rational mp, List<List<Note>> chords, List<Rational> allOnsetTimes, 
		Rational thresholdDur, int rescaleFactor, String augmentation) {

		MetricalTimeLine mtlAugm = 
			augmentMetricalTimeLine(getMetricalTimeLine(), mp, rescaleFactor, augmentation);
		SortedContainer<Marker> htAugm = 
			augmentHarmonyTrack(getHarmonyTrack(), getMetricalTimeLine(), mp, rescaleFactor, 
			augmentation);
		NotationSystem nsAugm = 
			augmentNotationSystem(getScore(), mtlAugm, mp, chords, allOnsetTimes, thresholdDur, 
			rescaleFactor, augmentation, "");

		setMetricalTimeLine(mtlAugm);
		setHarmonyTrack(htAugm);
		setScore(nsAugm);
	}


	/**
	 * Augments the given <code>MetricalTimeLine</code> according to the given augmentation.
	 * 
	 * @param mtl
	 * @param mp 
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	// TESTED
	static MetricalTimeLine augmentMetricalTimeLine(MetricalTimeLine mtl, Rational mp,
		int rescaleFactor, String augmentation) {
		
		MetricalTimeLine mtlAugm = initialiseMetricalTimeLine(new MetricalTimeLine());
//		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
//		// zeroMarker, and endMarker) 
//		MetricalTimeLine mtlAugm = new MetricalTimeLine();
//		mtlAugm.clear();
//		// Add zeroMarker
//		mtlAugm.add((Marker) new TimedMetrical(0, Rational.ZERO));

//		MetricalTimeLine mtl = getMetricalTimeLine();
//		Rational mp = getMirrorPoint(mi);
		
		List<Rational> meterSecMts = new ArrayList<>();
//		List<Rational> meterSecMetricTimes = ToolBox.getItemsAtIndex(getMeterSections(mtl), 1);
		Arrays.stream(mtl.getTimeSignature())
			.forEach(ts -> meterSecMts.add(new Rational(ts[3], ts[4])));

		// Add TimeSignatureMarkers and TempoMarkers 
		int ind = 0; // equals index in meterSecMts
		Rational mtAugmLastTimedMetrical = Rational.ZERO;
		long mpTime = augmentation.equals("reverse") ? mtl.getTime(mp) : -1;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {				
				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				TimeSignature ts = tsm.getTimeSignature();
				Rational mtAugm;
				long tAugm;
				TimeSignature tsAugm;
				// Reverse
				if (augmentation.equals("reverse")) {
					// Reversed mt/t = mirror point - mt/t of next meter section
					Rational mtNextMeterSec = 
						ind == (meterSecMts.size() - 1) ? mp : meterSecMts.get(ind + 1);
					mtAugm = mp.sub(mtNextMeterSec);
					tAugm = mpTime - mtl.getTime(mtNextMeterSec);
					tsAugm = ts;
				}
				// Rescale
				else {
					Rational mt = m.getMetricTime();
					long t = mtl.getTime(mt);
					mtAugm = rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor));
					tAugm = rescaleFactor > 0 ? t * rescaleFactor : t / Math.abs(rescaleFactor);
					Rational meter = new Rational(ts.getNumerator(), ts.getDenominator());
					// Take into account exception case where meter is x/1 and rescaleFactor > 1
					tsAugm = new TimeSignature(
						ts.getDenominator() == 1 && rescaleFactor > 1 ?	
						new Rational(meter.getNumer() * rescaleFactor, meter.getDenom()) :
						Timeline.undiminuteMeter(meter, rescaleFactor)
					);
				}
				
				mtlAugm = addToMetricalTimeLine(mtlAugm, mtAugm, tAugm, tsAugm, null);
				
//				mtlAugm.add(new TimeSignatureMarker(tsAugm, mtAugm));
//				if (mtAugm.isGreater(Rational.ZERO)) {
//					mtlAugm.add(new TempoMarker(tAugm, mtAugm));
//					if (mtAugm.isGreater(mtAugmLastTimedMetrical)) {
//						mtAugmLastTimedMetrical = mtAugm;
//					}
//				}
				if (mtAugm.isGreater(mtAugmLastTimedMetrical)) {
					mtAugmLastTimedMetrical = mtAugm;
				}
				ind++;
			}
		}

		long tAugmLastTimedMetrical = mtlAugm.getTime(mtAugmLastTimedMetrical);
		mtlAugm = 
			finaliseMetricalTimeLine(mtlAugm, mtAugmLastTimedMetrical, tAugmLastTimedMetrical, 
			mtl.getTempo(tAugmLastTimedMetrical), !augmentation.equals("rescale") ? 1 : -rescaleFactor);
		
//		// If TempoMarkers (and through them, endMarker(s)) have been added: 
//		// remove all TimedMetricals but the zeroMarker	
//		if (mtAugmLastTimedMetrical.isGreater(Rational.ZERO)) {
//			mtlAugm = cleanTimedMetricals(mtlAugm);
//		}
//
//		// Add endMarker
//		long tAugmLastTimedMetrical = mtlAugm.getTime(mtAugmLastTimedMetrical);
//		TimedMetrical end = 
//			calculateEndMarker(tAugmLastTimedMetrical, mtl.getTempo(tAugmLastTimedMetrical), 
//			mtAugmLastTimedMetrical, !augmentation.equals("rescale") ? 1 : -rescaleFactor);
//		mtlAugm.add((Marker) end);

		return mtlAugm;
	}


	/**
	 * Augments the given harmony track according to the given augmentation.
	 *  
	 * @param ht 
	 * @param mtl
	 * @param mp
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	// TESTED
	static SortedContainer<Marker> augmentHarmonyTrack(SortedContainer<Marker> ht, MetricalTimeLine mtl, 
		Rational mp, int rescaleFactor, String augmentation) {
		SortedContainer<Marker> htAugm = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

//		NotationSystem ns = getScore();
//		MetricalTimeLine mtl = getMetricalTimeLine();
//		SortedContainer<Marker> ht = getHarmonyTrack();
//		Rational mp = getMirrorPoint(mi);
		
		List<Rational> keySecMts = new ArrayList<>();
//		List<Rational> meterSecMetricTimes = ToolBox.getItemsAtIndex(getMeterSections(mtl), 1);
//		Arrays.stream(mtl.getTimeSignature())
//			.forEach(ts -> keySecMetricTimes.add(new Rational(ts[3], ts[4])));
		ht.forEach(m -> {
			if (m instanceof KeyMarker) {
				keySecMts.add(m.getMetricTime());
			}
		});

		int ind = 0; // equals index in keySecMts
		long mpTime = augmentation.equals("reverse") ? mtl.getTime(mp) : -1;
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				Rational mtAugm;
				long tAugm;
				// Reverse
				if (augmentation.equals("reverse")) {
					// Reversed mt/t = mirror point - mt/t of next meter section
					Rational mtNextKeySec = 
						ind == (keySecMts.size() - 1) ? mp : keySecMts.get(ind + 1);
					mtAugm = mp.sub(mtNextKeySec);
					tAugm = mpTime - mtl.getTime(mtNextKeySec);
				}
				// Rescale
				else {
					Rational mt = km.getMetricTime();
					long t = mtl.getTime(mt);
					mtAugm = rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor)); 
					tAugm = rescaleFactor > 0 ? t * rescaleFactor : t / Math.abs(rescaleFactor);
				}
				km.setMetricTime(mtAugm);
				km.setTime(tAugm);
				htAugm.add(km);
				ind++;
			}
		}
		return htAugm;
	}


	/**
	 * Augments the given <code>NotationSystem</code> according to the given augmentation. 
	 * 
	 * @param ns
	 * @param mtl
	 * @param mp
	 * @param ch
	 * @param onsetTimes
	 * @param thresholdDur
	 * @param rescaleFactor
	 * @param augmentation
	 * @param name
	 * @return
	 */
	// TESTED
	static NotationSystem augmentNotationSystem(NotationSystem ns, MetricalTimeLine mtl,
		Rational mp, List<List<Note>> ch, List<Rational> onsetTimes, Rational thresholdDur, 
		int rescaleFactor, String augmentation, String name) {

		NotationSystem nsAugm = new NotationSystem();

//		NotationSystem ns = getScore();
//		MetricalTimeLine mtl = getMetricalTimeLine();
//		Rational mp = getMirrorPoint(mi);		
		
		for (NotationStaff nst : ns) {	
			NotationStaff nstAugm = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvAugm = new NotationVoice();
				for (int i = 0; i < nv.size(); i++) {
					NotationChord nc = nv.get(i);
					NotationChord ncAugm = new NotationChord();
					// Reverse
					if (augmentation.equals("reverse")) {
						NotationChord ncRev = new NotationChord();
						if (!name.equals("barbetta-1582_1-il_nest.mid")) {
							// Reversed mt = mirror point - offset time
							nc.forEach(n -> { 
								Rational dur = n.getMetricDuration();
								ncRev.add(createNote(n.getMidiPitch(), mp.sub(n.getMetricTime().add(dur)), dur, mtl));
							});
						}
						// Error in barbetta-1582_1-il_nest.tbp: last chord should be cosb (and not cobr), 
						// leading to mt being -1/2 TODO fix and remove else (and if open and close above) 
						else {
							for (Note n : nc) {
								Rational dur = n.getMetricDuration();
								Rational mt = mp.sub(n.getMetricTime().add(dur));
								if (mt.equals(new Rational(-1, 2))) {
									mt = Rational.ZERO;
									dur = new Rational(1, 2);
								}
								ncRev.add(createNote(n.getMidiPitch(), mt, dur, mtl));
							}
						
						}
						ncAugm = ncRev;
					}
					// Deornament
					else if (augmentation.equals("deornament")) {
						NotationChord ncDeorn = new NotationChord();
						NotationChord ncPrev = i > 0 ? nv.get(i-1) : null;
						int ind = onsetTimes.indexOf(nc.getMetricTime());
						// If nc is ornamental and not part of an ornamental sequence at the beginning of
						// nv (in which case nvAugm is still empty)
						// NB: It is assumed that an ornamental sequence is not interrupted by (ornamental) rests 
						if ((ch.get(ind).size() == 1 && nc.size() == 1 && 
							nc.getMetricDuration().isLess(thresholdDur)) && nvAugm.size() > 0) {
							Rational durOrnSeq = nc.getMetricDuration();
							for (int j = i+1; j < nv.size(); j++) {
								NotationChord ncNext = nv.get(j);
								int indNext = onsetTimes.indexOf(ncNext.getMetricTime());
								// If ncNext is ornamental: increment duration of ornamental sequence
								if (ch.get(indNext).size() == 1 && ncNext.size() == 1 && 
									ncNext.getMetricDuration().isLess(thresholdDur)) {
									durOrnSeq = durOrnSeq.add(ncNext.getMetricDuration());
								}
								// If not: make ncAugm, which replaces ncPrev 
								else {
									Rational durNcDeorn = ncPrev.getMetricDuration().add(durOrnSeq);
									ncPrev.forEach(n ->
										ncDeorn.add(createNote(n.getMidiPitch(), n.getMetricTime(), durNcDeorn, mtl))
									);
									ncAugm = ncDeorn;
									nvAugm.remove(ncPrev);							
									i = j-1;
									break;
								}
							}
						}
						// If nc is not ornamental
						else {
							ncAugm = nc;
						}
					}
					// Rescale
					else {
						NotationChord ncResc = new NotationChord();
						nc.forEach(n -> { 
							Rational dur = n.getMetricDuration();
							Rational mt = n.getMetricTime();
							ncResc.add(createNote(
								n.getMidiPitch(), 
								rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor)),
								rescaleFactor > 0 ? dur.mul(rescaleFactor) : dur.div(Math.abs(rescaleFactor)), 
								mtl)
							);
						});
						ncAugm = ncResc;
					}
					nvAugm.add(ncAugm);
				}
				nstAugm.add(nvAugm);
			}
			nsAugm.add(nstAugm);
		}
		return nsAugm;
	}


	/**
	 * Makes a deep copy of the given NotationSystem.
	 * 
	 * @param ns
	 * @return
	 */
	// TESTED
	static NotationSystem copyNotationSystem(NotationSystem ns) {
		NotationSystem copy = new NotationSystem();

		for (NotationStaff notationStaff : ns) {
			NotationStaff copyNs = new NotationStaff();
			for (NotationVoice nv : notationStaff) {
				NotationVoice copyNv = new NotationVoice();
				for (NotationChord nc : nv) {
					NotationChord copyNc = new NotationChord();
					for (Note n : nc) {
						try {
							copyNc.add((Note) n.clone());
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
//						copyNc.add(createNote(n.getMidiPitch(), n.getMetricTime(), n.getMetricDuration()));
					}
					copyNv.add(copyNc);
				}
				copyNs.add(copyNv);
			}
			copy.add(copyNs);
		}
		return copy;
	}

}
