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
import representations.Tablature;
import representations.Transcription;
import structure.metric.Utils;
import tools.ToolBox;
import utility.DataConverter;

/**
 * Convenience class, complementing <code>Piece</code>. 
 * 
 * Alternative solutions:
 * <ul> 
 * <li>Remove the inheritance, make all instance methods static, and pass the <code>Piece</code> 
 *     as their first argument.</li>
 * <li>Add all methods directly to <code>Piece</code>.</li>
 * </ul>
 * 
 * @author Reinier
 *
 */
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

	private ScoreMetricalTimeLine scoreMetricalTimeLine;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public ScorePiece(Piece p) {
		super(p);
		init(null, null, null, null, null, null, -1, null);
	}


	public ScorePiece(Integer[][] btp, Integer[][] bnp, List<List<Double>> voiceLabels, 
		List<List<Double>> durLabels, MetricalTimeLine mtl, SortedContainer<Marker> ht, 
		int numVoices, String name) {
		super();
		init(btp, bnp, voiceLabels, durLabels, mtl, ht, numVoices, name);
	}


	private void init(Integer[][] btp, Integer[][] bnp, List<List<Double>> voiceLabels, 
		List<List<Double>> durLabels, MetricalTimeLine mtl, SortedContainer<Marker> ht, 
		int numVoices, String name) {
		if (name == null) {
			setMetricalTimeLine();
			setScoreMetricalTimeLine();
			setHarmonyTrack();
		}
		else {
			setScore(btp, bnp, voiceLabels, durLabels, mtl, numVoices);
			setMetricalTimeLine(mtl);
			setScoreMetricalTimeLine();
			setHarmonyTrack(ht);
			setName(name);
		}
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	void setMetricalTimeLine() {
		setMetricalTimeLine(cleanMetricalTimeLine(getMetricalTimeLine()));
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
	static MetricalTimeLine cleanMetricalTimeLine(MetricalTimeLine mtl) {
		MetricalTimeLine mtlClean = initialiseMetricalTimeLine();

		// Add TimeSignatureMarkers and TempoMarkers 
		List<Rational> mts = new ArrayList<>();
		Rational mtLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {
				Rational mt = m.getMetricTime();
				if (!mts.contains(mt)) {
					mtlClean = 
						addToMetricalTimeLine(mtlClean, mt, mtl.getTime(mt), 
						((TimeSignatureMarker) m).getTimeSignature(), null);
					if (mt.isGreater(mtLastTimedMetrical)) {
						mtLastTimedMetrical = mt;
					}
					mts.add(mt);
				}
			}
		}
		// Remove all TimedMetricals but the zeroMarker; add endMarker
		long tLastTimedMetrical = mtlClean.getTime(mtLastTimedMetrical);
		mtlClean = 
			finaliseMetricalTimeLine(mtlClean, mtLastTimedMetrical, tLastTimedMetrical, 
			mtl.getTempo(tLastTimedMetrical), 1);

		return mtlClean;
	}
	
	
//	/**
//	 * Removes all duplicate of <code>TimeSignatureMarkers</code> from the given 
//	 * <code>MetricalTimeLine</code>.
//	 * 
//	 * @param mtl
//	 * @return
//	 */
//	// TESTED
//	static MetricalTimeLine cleanTimeSignatures(MetricalTimeLine mtl) {
//		List<Integer> indsToRemove = new ArrayList<>();
//		List<Rational> mts = new ArrayList<>();
//		for (int i = 0; i < mtl.size(); i++) {
//			Marker m = mtl.get(i);
//			if (m instanceof TimeSignatureMarker) {
//				Rational mt = m.getMetricTime();
////			if (m instanceof TimedMetrical && !(m instanceof TempoMarker)) {
//				if (mts.contains(mt)) {
////				if (!m.getMetricTime().equals(Rational.ZERO)) {
//					indsToRemove.add(i);
//				}
//				else {
//					mts.add(mt);					
//				}
//			}
//		}
//		for (int i = 0; i < indsToRemove.size(); i++) {
//			mtl.remove(indsToRemove.get(i) - i);
//		}
//
//		return mtl;
//	}


	// NOT TESTED (wrapper method)
	static MetricalTimeLine initialiseMetricalTimeLine() {
		MetricalTimeLine mtl = new MetricalTimeLine();	

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
	 * Removes all <code>TimedMetricals</code> but the zeroMarker from the given 
	 * <code>MetricalTimeLine</code>.
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
	 * Calculates the endMarker given the time, tempo, and metric time of the last <code>TimedMetrical</code> 
	 * before the endMarker. The endMarker is placed ten whole notes (10/1) after the last 
	 * <code>TimedMetrical</code>, meaning that its metric time is that of the last <code>TimedMetrical</code> 
	 * + 10/1, and its time is that of the last <code>TimedMetrical</code> + the time 10/1 takes in the 
	 * tempo at the last <code>TimedMetrical</code>.
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
		Rational r = Utils.diminute(new Rational(10, 1), dim);
		return new TimedMetrical(
			tLastTimedMetrical + Utils.calculateTime(r, tmpLastTimedMetrical), 
			mtLastTimedMetrical.add(r));
	}


	void setScoreMetricalTimeLine() {
		scoreMetricalTimeLine = new ScoreMetricalTimeLine(getMetricalTimeLine());
	}


	void setScoreMetricalTimeLine(ScoreMetricalTimeLine smtl) {
		scoreMetricalTimeLine = smtl;
	}


	void setHarmonyTrack() {
		setHarmonyTrack(cleanHarmonyTrack(getHarmonyTrack()));
	}


	/**
	 * Cleans the given harmony track (i.e., removes any duplicate <code>KeyMarker</code>s 
	 * from it). 
	 * 
	 * @param ht 
	 * @return
	 */
	// TESTED 
	static SortedContainer<Marker> cleanHarmonyTrack(SortedContainer<Marker> ht) {
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


	void setScore(Integer[][] btp, Integer[][] bnp, List<List<Double>> voiceLabels, 
		List<List<Double>> durLabels, MetricalTimeLine mtl, int numVoices) {
		setScore(makeScore(btp, bnp, voiceLabels, durLabels, mtl, numVoices));
	}


	// TESTED
	static NotationSystem makeScore(Integer[][] btp, Integer[][] bnp, List<List<Double>> 
		voiceLabels, List<List<Double>> durLabels, MetricalTimeLine mtl, int numVoices) {

		Transcription.verifyCase(btp, bnp);

		NotationSystem ns = new NotationSystem();
		for (int i = 0; i < numVoices; i++) {
			NotationStaff nst = new NotationStaff(); 
			nst.add(new NotationVoice());
			ns.add(nst);
		}
		if (btp != null) {
			for (int i = 0; i < btp.length; i++) {
				// Create Note
				Rational mt = new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
				// When not modelling duration, durLabels == null
				Rational mDur = 
					durLabels == null ? new Rational(btp[i][Tablature.MIN_DURATION], Tablature.SRV_DEN) :
					DataConverter.convertIntoDuration(durLabels.get(i))[0]; // each label contains only one element as only one duration is predicted
				Note note = ScorePiece.createNote(btp[i][Tablature.PITCH], mt, mDur, -1, mtl);
				// Add Note to voice(s)
				DataConverter.convertIntoListOfVoices(voiceLabels.get(i)).forEach(v -> 
					ns.get(v).get(0).add(note));
			}
		}
		else {
			for (int i = 0; i < bnp.length; i++) {
				// Create Note
				Rational mt = new Rational(
					bnp[i][Transcription.ONSET_TIME_NUMER], 
					bnp[i][Transcription.ONSET_TIME_DENOM]);
				Rational mDur = new Rational(
					bnp[i][Transcription.DUR_NUMER], 
					bnp[i][Transcription.DUR_DENOM]);
				Note note = ScorePiece.createNote(bnp[i][Transcription.PITCH], mt, mDur, -1, mtl);
				// Add Note voice(s)
				DataConverter.convertIntoListOfVoices(voiceLabels.get(i)).forEach(v ->
					ns.get(v).get(0).add(note));
			}
		}
		return ns; 
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public ScoreMetricalTimeLine getScoreMetricalTimeLine() {
		return scoreMetricalTimeLine;
	}


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	/**
	 * Creates a Note that has 
	 * <ul>
	 * <li>A <code>ScoreNote</code> with the given pitch, metric time, and metric duration.</li> 
	 * <li>A <code>PerformanceNote</code> with the given pitch, and
	 * <ul>
	 * <li>If the given <code>MetricalTimeLine</code> is not <code>null</code>, the time and 
	 *     duration on that <code>MetricalTimeLine</code>; else, a <code>PerformanceNote</code>'s 
	 *     default time (0) and duration assuming a quarter note being 600000.</li>
	 * <li>If the given velocity is not -1, the given velocity; else, a <code>PerformanceNote</code>'s 
	 *     default velocity (90).</li> 
	 * </ul>
	 * </ul>
	 * 
	 * @param pitch
	 * @param mt
	 * @param mDur
	 * @param velocity 
	 * @param mtl 
	 * @return
	 */
	// TESTED
	public static Note createNote(int pitch, Rational mt, Rational mDur, int velocity, 
		MetricalTimeLine mtl) {
		ScoreNote sn = new ScoreNote(new ScorePitch(pitch), mt, mDur);		  
		PerformanceNote def = new PerformanceNote();
		PerformanceNote pn =
			mtl == null ? new PerformanceNote(def.getTime(), def.getDuration(), def.getVelocity(), pitch) : 
			sn.toPerformanceNote(mtl);
		pn.setDuration((long) mDur.mul(4 * 600000).toDouble());
		if (velocity != -1) {
			pn.setVelocity(velocity);
		}

		return new Note(sn, MidiNote.convert(pn));
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	/**
	 * Transposes the given <code>ScorePiece</code> according to the given transposition.
	 * 
	 * @param transposition
	 */
	// NOT TESTED (wrapper method)
	public void transpose(int transposition) {
		SortedContainer<Marker> ht = getHarmonyTrack();
		ht = ScorePiece.transposeHarmonyTrack(ht, transposition);
		setHarmonyTrack(ht);
		NotationSystem ns = getScore();
		ns = ScorePiece.transposeNotationSystem(ns, transposition);
		setScore(ns);
	}


	/**
	 * Transposes the given harmony track according to the given transposition.
	 * 
	 * @param ht
	 * @param transposition
	 * @return
	 */
	// TESTED
	static SortedContainer<Marker> transposeHarmonyTrack(SortedContainer<Marker> ht, int transposition) {
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
	 * accidentals (there are always two outcomes: sharps and flats).
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
	}


	/**
	 * Transposes the given <code>NotationSystem</code> according to the given transposition.
	 * 
	 * @param ns
	 * @param transposition
	 * @return 
	 */
	// TESTED
	static NotationSystem transposeNotationSystem(NotationSystem ns, int transposition) {
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
	 * Diminutes the <code>ScorePiece</code> according to the diminutions in the given meter 
	 * info (from a <code>Tablature</code>.
	 * 
	 * @param mi
	 */
	// NOT TESTED (wrapper method)
	public void diminute(List<Integer[]> mi) {
//		// Clean, align, and diminute mtl
//		List<Integer[]> mi = tab.getMeterInfo();
		MetricalTimeLine mtl = getMetricalTimeLine();
//		mtl = ScorePiece.cleanMetricalTimeLine(mtl);
//		for (Integer[] in : mi) {
//			System.out.println(Arrays.asList(in));
//		}
		mtl = ScorePiece.alignMetricalTimeLine(mtl, mi);
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(mtl);
		MetricalTimeLine mtlDim = ScorePiece.diminuteMetricalTimeLine(mtl, mi);
		ScoreMetricalTimeLine smtlDim = new ScoreMetricalTimeLine(mtlDim);
//		// Clean and diminute ht
		SortedContainer<Marker> ht = getHarmonyTrack();
//		ht = ScorePiece.cleanHarmonyTrack(ht);
		ht = ScorePiece.diminuteHarmonyTrack(ht, mi, smtl, smtlDim);
		// Diminute ns
		NotationSystem ns = getScore();
		ns = ScorePiece.diminuteNotationSystem(ns, mi, smtl, smtlDim);
		// Set ns, mtl, and ht
		setScore(ns);
		setMetricalTimeLine(mtlDim);
		setScoreMetricalTimeLine(smtlDim);
		setHarmonyTrack(ht);
	}


	/**
	 * Aligns the given <code>MetricalTimeLine</code> with the given meter info (from a 
	 * <code>Tablature</code>).
	 * 
	 * This is necessary if in the <code>Tablature</code> different diminutions are used for
	 * a section that is in a single meter in the <code>Transcription</code>, resulting in 
	 * the <code>MetricalTimeLine</code> lacking the repeated meter 'changes'. In such cases, 
	 * the <code>MetricalTimeLine</code> is aligned with the meter info by adding each
	 * repeated meter at the appropriate onset to the <code>MetricalTimeLine</code> (i.e.,
	 * adding <code>TimeSignatureMarkers</code> and <code>TempoMarkers</code>). Example: <br> 
	 * meters from <code>mi</code> 				2/2, 2/4, 2/2, 3/4, 2/2 <br>
	 * diminutions from <code>mi</code> 		2,   4,   2,   4,   2   <br>
	 * meters from <code>mtl</code>				2/1, ..., ..., 3/1,	2/1 <br>
	 * meters from <code>mtl</code>, aligned	2/1, 2/1, 2/1, 3/1, 2/1 <br>
	 * 
	 * @param mtl
	 * @param mi
	 * @return
	 */
	// TESTED
	static MetricalTimeLine alignMetricalTimeLine(MetricalTimeLine mtl,	List<Integer[]> mi) {
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

		System.out.println(mtl);
		
		// 1. Get undiminuted meters and meter section onsets from meterInfoTab to enable aligning
		List<Rational> metersTabUndim = new ArrayList<>();
		List<Rational> msosTabUndim = new ArrayList<>();
		for (int i = 0; i < mi.size(); i++) {
			Integer[] currMi = mi.get(i);
			metersTabUndim.add(Utils.undiminuteMeter(
				new Rational(currMi[Tablature.MI_NUM], currMi[Tablature.MI_DEN]), 
				currMi[Tablature.MI_DIM]));
			Rational msoTabUndim;
			if (i == 0) {
				msoTabUndim = 
					new Rational(currMi[Tablature.MI_NUM_MT_FIRST_BAR], currMi[Tablature.MI_DEN_MT_FIRST_BAR]);
			}
			else {
				Integer[] prevMi = mi.get(i-1);
				int numBarsPrevMeter = (prevMi[Tablature.MI_LAST_BAR] - prevMi[Tablature.MI_FIRST_BAR]) + 1; 
				msoTabUndim = msosTabUndim.get(i-1).add(metersTabUndim.get(i-1).mul(numBarsPrevMeter));
			}
			msosTabUndim.add(msoTabUndim);
		}

		// 2. Align
		MetricalTimeLine mtlAligned = initialiseMetricalTimeLine();
		// Add TimeSignatureMarkers and TempoMarkers 
		int ind = 0; // equals index in meterInfoTab
		Rational mtLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {
				TimeSignature ts = ((TimeSignatureMarker) m).getTimeSignature();
				Rational mt = m.getMetricTime();
				mtlAligned = addToMetricalTimeLine(mtlAligned, mt, mtl.getTime(mt), ts, null);				
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
		if (allTss.length < mi.size()) {
			Rational lastTs = new Rational(allTss[allTss.length-1][0], allTss[allTss.length-1][1]);
			for (int i = allTss.length; i < mi.size(); i++) {
				Rational msoTabUndim = msosTabUndim.get(i);
				mtlAligned.add(new TimeSignatureMarker(new TimeSignature(lastTs), msoTabUndim));
				mtlAligned.add(new TempoMarker(mtl.getTime(msoTabUndim), msoTabUndim));
				if (msoTabUndim.isGreater(mtLastTimedMetrical)) {
					mtLastTimedMetrical = msoTabUndim;
				}
			}
		}
		// Remove all TimedMetricals but the zeroMarker; add endMarker
		long tLastTimedMetrical = mtlAligned.getTime(mtLastTimedMetrical);
		mtlAligned = 
			finaliseMetricalTimeLine(mtlAligned, mtLastTimedMetrical, 
			tLastTimedMetrical, mtl.getTempo(tLastTimedMetrical), 1);

		return mtlAligned;
	}


	/**
	 * Diminutes the given <code>MetricalTimeLine</code> according to the given meter info 
	 * (from a <code>Tablature</code>).
	 * 
	 * NB: Only the time signatures and the meter section onsets are diminuted; not the
	 *     meter section times.
	 * 
	 * @param mtl
	 * @param mi
	 * @return
	 */
	// TESTED
	static MetricalTimeLine diminuteMetricalTimeLine(MetricalTimeLine mtl, List<Integer[]> mi) {
		// 1. Get diminuted tempi
		List<Integer> diminutions = ToolBox.getItemsAtIndex(mi, Tablature.MI_DIM);
		List<Double[]> tempiDim = new ArrayList<>();
		int ind = 0; // equals index in meterInfoTab
		for (int i = 0; i < mtl.size()-1; i++) { // exclude endMarker
			Marker m = mtl.get(i);
			if (m instanceof TimedMetrical) {
				long time = mtl.getTime(m.getMetricTime());
				double tempo = mtl.getTempo(time);
				int dim = diminutions.get(ind);
				double tempoDim = Utils.diminute(tempo, dim);
				tempiDim.add(new Double[]{tempoDim, (double) time});
				ind++;
			}
		}

		// 2. Diminute
		MetricalTimeLine mtlDim = initialiseMetricalTimeLine();

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
					Integer[] prevMi = mi.get(ind-1);
					long[] prevTsDim = mtlDim.getTimeSignature()[ind-1];
					Rational prevMeterDim = 
						new Rational(prevMi[Tablature.MI_NUM], prevMi[Tablature.MI_DEN]);
					int prevNumBars = 
						(prevMi[Tablature.MI_LAST_BAR] - prevMi[Tablature.MI_FIRST_BAR]) + 1;
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
					new TimeSignature(Utils.diminuteMeter(new Rational(tsUndim.getNumerator(), 
					tsUndim.getDenominator()), diminutions.get(ind)));
				mtlDim = addToMetricalTimeLine(mtlDim, mtDim, t, tsDim, tempiDim);
				if (mtDim.isGreater(mtLastTimedMetrical)) {
					mtLastTimedMetrical = mtDim;
				}
				ind++;
			}
		}
		// Remove all TimedMetricals but the zeroMarker; add endMarker
		long tLastTimedMetrical = mtlDim.getTime(mtLastTimedMetrical);
		mtlDim = finaliseMetricalTimeLine(mtlDim, mtLastTimedMetrical, tLastTimedMetrical, 
			tempiDim.get(tempiDim.size()-1)[0], diminutions.get(diminutions.size()-1));

		return mtlDim; 
	}


	/**
	 * Diminutes the given harmony track according to the given meter info (from a <code>Tablature</code>) 
	 * and undiminuted and diminuted <code>ScoreMetricalTimeLine</code>.
	 * 
	 * @param ht 
	 * @param mi
	 * @param smtl 
	 * @param smtlDim
	 * @return
	 */
	// TESTED
	static SortedContainer<Marker> diminuteHarmonyTrack(SortedContainer<Marker> ht, List<Integer[]> mi, 
		ScoreMetricalTimeLine smtl, ScoreMetricalTimeLine smtlDim) {
		SortedContainer<Marker> htDim = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		List<Integer> diminutions = 
			ToolBox.getItemsAtIndex(mi, Tablature.MI_DIM);
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				km.setMetricTime(
					smtl.getDiminutedMetricTime(m.getMetricTime(), smtlDim, diminutions)
				);
				htDim.add(km);
			}
		}
		return htDim;
	}


	/**
	 * Diminutes the given <code>NotationSystem</code> according to the given meter info 
	 * (from a <code>Tablature</code>) and undiminuted and diminuted <code>ScoreMetricalTimeLine</code>.
	 * 
	 * @param ns
	 * @param mi
	 * @param smtl
	 * @param smtlDim
	 * @return
	 */
	// TESTED
	static NotationSystem diminuteNotationSystem(NotationSystem ns, List<Integer[]> mi, 
		ScoreMetricalTimeLine smtl, ScoreMetricalTimeLine smtlDim) {
		NotationSystem nsDim = new NotationSystem();
		for (int v = 0; v < ns.size(); v++) {
			NotationStaff nstDim = new NotationStaff();
			nstDim.add(new NotationVoice());
			nsDim.add(nstDim);
		}

		List<Integer> diminutions = ToolBox.getItemsAtIndex(mi, Tablature.MI_DIM);	
		for (int v = 0; v < ns.size(); v++) {
			NotationVoice nv = ns.get(v).get(0);
			for (NotationChord nc : nv) {
				// All notes in nc have the same (metric) time
				Rational mt = nc.getMetricTime();
				long time = nc.getTime();
				int sec = smtl.getMeterSection(mt);
				// Diminute the notes in nc (and therewith nc itself) and add to nsDim
				for (Note n : nc) {
					// Adapt ScoreNote
					ScoreNote sn = n.getScoreNote();
					Rational onsDim = smtl.getDiminutedMetricTime(mt, smtlDim, diminutions);
					sn.setMetricTime(onsDim);
					Rational durDim = Utils.diminute(n.getMetricDuration(), diminutions.get(sec));
					sn.setMetricDuration(durDim);
					n.setScoreNote(sn);
					// Adapt PerformanceNote
					PerformanceNote pn = n.getPerformanceNote();
					long duration = n.getDuration();
					// By uncommenting the lines below, the time and duration are diminuted
//					time = 
//						Timeline.getDiminutedTime(time, meterSectionTimesUndim, meterSectionTimesDim, 
//						diminutions);
//					long duration = dim > 0 ? n.getDuration() / dim : n.getDuration() * Math.abs(dim);
					pn.setTime(time);
					pn.setDuration(duration);
					n.setPerformanceNote(pn);
				}
				nsDim.get(v).get(0).add(nc);
			}
		}

		return nsDim;
	}


	/** 
	 * Adds the given <code>Note</code> to the given voice.<br><br>
	 * 
	 * Each <code>Note</code> is wrapped in a <code>NotationChord</code>, which has a single metric 
	 * duration (i.e., all its <code>Note</code>s have the same duration). 
	 * 
	 * <ul>
	 * <li>If the given voice contains no <code>NotationChord</code>s at the given <code>Note</code>'s 
	 * metric time, the given <code>Note</code> is simply added.</li>
	 * <li>If the given voice contains one or more <code>NotationChord</code>s at the given <code>Note</code>'s 
	 * metric time</li> 
	 * <ul>
	 * <li>If one of these <code>NotationChord</code>s has the duration of the given <code>Note</code>, 
	 *     the given <code>Note</code> is added to this <code>NotationChord</code>.</li>
	 * <li>If none of these <code>NotationChord</code>s have the duration of the given <code>Note</code>, 
	 *     the given <code>Note</code> is added in a new <code>NotationChord</code>, at the same metric time.</li>
	 * </ul>
	 * </ul>
	 * 
	 * NB1: If a <code>NotationVoice</code> has multiple <code>NotationChord</code>s at the same metric time,
	 *      the ordering of the <code>NotationChord</code>s in the <code>NotationVoice</code> is based on their 
	 *      order of adding (at that metric time): the one added last appears first, and the one added first  
	 *      appears last.<br><br>
	 * 
	 * NB2: If a <code>NotationChord</code> has multiple <code>Note</code>s, the ordering of the <code>Notes</code>s 
	 *      in the <code>NotationChord</code> is based on their order of adding: the one added first appears 
	 *      first, and the one added last appears last.<br><br>
	 * 
	 * @param n
	 * @param v
	 */
	// TESTED
	public void addNote(Note n, int v) {
		NotationVoice nv = getScore().get(v).get(0);
		Rational mt = n.getMetricTime();
		Rational dur = n.getMetricDuration();
		MidiNote mn = MidiNote.convert(n.getPerformanceNote());
		mn.setChannel(v);

		// Get NotationChord(s) at mt
		int ncInd = nv.find(mt); // < 0 if there is no NotationChord at mt
		// If there is no NotationChord at mt
		if (ncInd < 0) {
			NotationChord nc = new NotationChord();
			nc.add(n);
			nv.add(nc);
		}
		// If there is already a NotationChord at mt
		else {
			// Get the index of the NotationChord at mt with duration dur 
			int indNcWithSameDur = -1;
			int incr = 0;
			while (indNcWithSameDur == -1 && ncInd - incr >= 0 && ncInd + incr < nv.size()) {
				// Check left
				NotationChord prev = nv.get(ncInd - incr);
				if (prev.getMetricTime().equals(mt) && prev.getMetricDuration().equals(dur)) {
					indNcWithSameDur = ncInd - incr;
				}
				// Check right
				NotationChord next = nv.get(ncInd + incr);
				if (next.getMetricTime().equals(mt) && next.getMetricDuration().equals(dur)) {
					indNcWithSameDur = ncInd + incr;
				}
				// No NotationChord found 
				if (prev.getMetricTime().isLess(mt) && next.getMetricTime().isGreater(mt))  {
					break;
				}
				incr++;
			}

			// 2. Add n to NotationChord
			// a. If there is a NotationChord at mt that has the same duration as n: add n to it 
			// (n will be added as the *last* element in the NotationChord) 
			// TODO: cleaner is sorted by pitch (lowest first)
			if (indNcWithSameDur != -1) {
				nv.get(indNcWithSameDur).add(n);
			}
			// b. If not: add n to a new NotationChord, and add this to nv (the new NotationChord 
			// will be added as the *first* of the NotationChords with the same mt)
			// TODO: cleaner is sorted by duration (longest first)
			else {
				NotationChord nc = new NotationChord();
				nc.add(n);
				nv.add(nc);
			}
		}
	}


	/** 
	 * Removes the <code>Note</code> with the given pitch, metric time, and duration from the given voice.<br><br>
	 * 
	 * Each <code>Note</code> is wrapped in a <code>NotationChord</code>, which has a single metric 
	 * duration (i.e., all its <code>Note</code>s have the same duration).
	 *  
	 * <ul>
	 * <li>If the given voice contains no <code>NotationChord</code>s at the given metric time, no action 
	 * is taken.</li>
	 * <li>If the given voice contains one or more <code>NotationChord</code>s at the given metric time</li>
	 * <ul>
	 * <li>If one of these <code>NotationChord</code>s has the given duration, all notes with the given 
	 *     pitch are removed from this <code>NotationChord</code>. If, after the removal, the 
	 *     <code>NotationChord</code> is empty, it itself is removed from the given voice.</li>
	 * <li>If none of these <code>NotationChord</code>s have the given duration, no action is taken.</li>
	 * </ul>
	 * </ul>
	 *  
	 * @param p
	 * @param mt  
	 * @param dur
	 * @param v 
	 */
	// TESTED
	public void removeNote(int p, Rational mt, Rational dur, int v) {
		NotationVoice nv = getScore().get(v).get(0);

		// Get NotationChord(s) at mt
		int ncInd = nv.find(mt); // < 0 if there is no NotationChord at mt
		if (ncInd >= 0) {
			// Get the index of the NotationChord at mt with duration dur 
			int indNcWithSameDur = -1;
			int incr = 0;
			while (indNcWithSameDur == -1 && ncInd - incr >= 0 && ncInd + incr < nv.size()) {
				// Check left
				NotationChord prev = nv.get(ncInd - incr);
				if (prev.getMetricTime().equals(mt) && prev.getMetricDuration().equals(dur)) {
					indNcWithSameDur = ncInd - incr;
				}
				// Check right
				NotationChord next = nv.get(ncInd + incr);
				if (next.getMetricTime().equals(mt) && next.getMetricDuration().equals(dur)) {
					indNcWithSameDur = ncInd + incr;
				}
				// No NotationChord found 
				if (prev.getMetricTime().isLess(mt) && next.getMetricTime().isGreater(mt))  {
					break;
				}
				incr++;
			}

			// 2. Remove all Notes with pitch p from NotationChord with duration dur
			if (indNcWithSameDur != -1) {
				NotationChord currNc = nv.get(indNcWithSameDur);
				for (int j = currNc.size() - 1; j >= 0; j--) {
					Note currN = currNc.get(j);
					if (currN.getMidiPitch() == p) {
						currNc.remove(currN);
					}
				}
				if (currNc.size() == 0) {
					nv.remove(currNc);
				}
			}
		}
	}


	/**
	 * Gives each <code>Note</code> in the <code>ScorePiece</code>'s <code>NotationSystem</code> 
	 * its maximum duration. Given <code>note</code>s n_t and n_t+1 in a <code>NotationVoice</code>, 
	 * the duration of n_t is
	 * <ul>
	 * <li>If the inter-onset time between n_t and n_t+1 <= the given maxDur: the inter-onset time.</li>
	 * <li>If the inter-onset time between n_t and n_t+1 >  the given maxDur: the given maxDur.</li>
	 * <ul>
	 * 
	 * The final <code>Note</code> in each <code>NotationVoice</code> is not altered.
	 * 
	 * @param maxDur
	 * @return
	 */
	// TESTED
	public void completeDurations(Rational maxDur) {
		NotationSystem ns = getScore();
		MetricalTimeLine mtl = getMetricalTimeLine();
		for (int i = 0; i < ns.size(); i++) {
			NotationVoice nv = ns.get(i).get(0);
			// Do not adapt the last Note
			for (int j = 0; j < nv.size() - 1; j++) {
				NotationChord nc = nv.get(j);
				NotationChord ncCompl = new NotationChord();
				for (Note n : nc) {
					Rational onset = n.getMetricTime();
					Rational ioi = nv.get(j+1).getMetricTime().sub(onset);
					if (ioi.isGreater(maxDur)) {
						ioi = maxDur;
					}
					ncCompl.add(ScorePiece.createNote(n.getMidiPitch(), onset, ioi, n.getVelocity(), mtl));
				}
				nv.remove(nc);
				nv.add(ncCompl);
			}
		}
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

		ScoreMetricalTimeLine smtl = getScoreMetricalTimeLine();
		MetricalTimeLine mtlAugm = 
			augmentMetricalTimeLine(smtl, mp, rescaleFactor, augmentation);
		SortedContainer<Marker> htAugm = 
			augmentHarmonyTrack(getHarmonyTrack(), smtl, mp, rescaleFactor, augmentation);
		ScoreMetricalTimeLine smtlAugm = new ScoreMetricalTimeLine(mtlAugm);
		NotationSystem nsAugm = 
			augmentNotationSystem(getScore(), smtlAugm, mp, chords, allOnsetTimes, thresholdDur, 
			rescaleFactor, augmentation, "");

		setMetricalTimeLine(mtlAugm);
		setScoreMetricalTimeLine(smtlAugm);
		setHarmonyTrack(htAugm);
		setScore(nsAugm);
	}


	/**
	 * Augments the given <code>ScoreMetricalTimeLine</code> according to the given augmentation.
	 * 
	 * @param smtl NB: Specific functionality of <code>ScoreMetricalTimeLine</code> not needed; nevertheless 
	 *                 preferred over a <code>MetricalTimeLine</code> becaue the augmentation is performed 
	 *                 on a completed <code>ScorePiece</code>.
	 * @param mp 
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	// TESTED
	static MetricalTimeLine augmentMetricalTimeLine(ScoreMetricalTimeLine smtl, Rational mp,
		int rescaleFactor, String augmentation) {

		MetricalTimeLine mtlAugm = initialiseMetricalTimeLine();		
		List<Rational> meterSecMts = new ArrayList<>();
		Arrays.stream(smtl.getTimeSignature())
			.forEach(ts -> meterSecMts.add(new Rational(ts[3], ts[4])));

		// Add TimeSignatureMarkers and TempoMarkers 
		int ind = 0; // equals index in meterSecMts
		Rational mtAugmLastTimedMetrical = Rational.ZERO;
		long mpTime = augmentation.equals("reverse") ? smtl.getTime(mp) : -1;
		for (Marker m : smtl) {
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
					tAugm = mpTime - smtl.getTime(mtNextMeterSec);
					tsAugm = ts;
				}
				// Rescale
				else {
					Rational mt = m.getMetricTime();
					long t = smtl.getTime(mt);
					mtAugm = rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor));
					tAugm = rescaleFactor > 0 ? t * rescaleFactor : t / Math.abs(rescaleFactor);
					Rational meter = new Rational(ts.getNumerator(), ts.getDenominator());
					// Take into account exception case where meter is x/1 and rescaleFactor > 1
					tsAugm = new TimeSignature(
						ts.getDenominator() == 1 && rescaleFactor > 1 ?	
						new Rational(meter.getNumer() * rescaleFactor, meter.getDenom()) :
						Utils.undiminuteMeter(meter, rescaleFactor)
					);
				}
				
				mtlAugm = addToMetricalTimeLine(mtlAugm, mtAugm, tAugm, tsAugm, null);
				if (mtAugm.isGreater(mtAugmLastTimedMetrical)) {
					mtAugmLastTimedMetrical = mtAugm;
				}
				ind++;
			}
		}
		// Remove all TimedMetricals but the zeroMarker; add endMarker
		long tAugmLastTimedMetrical = mtlAugm.getTime(mtAugmLastTimedMetrical);
		mtlAugm = 
			finaliseMetricalTimeLine(mtlAugm, mtAugmLastTimedMetrical, tAugmLastTimedMetrical, 
			smtl.getTempo(tAugmLastTimedMetrical), !augmentation.equals("rescale") ? 1 : -rescaleFactor);

		return mtlAugm;
	}


	/**
	 * Augments the given harmony track according to the given augmentation.
	 *  
	 * @param ht 
	 * @param smtl NB: Specific functionality of <code>ScoreMetricalTimeLine</code> not needed; nevertheless 
	 *                 preferred over a <code>MetricalTimeLine</code> because the augmentation is performed 
	 *                 on a completed <code>ScorePiece</code>.
	 * @param mp
	 * @param rescaleFactor
	 * @param augmentation
	 * @return
	 */
	// TESTED
	static SortedContainer<Marker> augmentHarmonyTrack(SortedContainer<Marker> ht, 
		ScoreMetricalTimeLine smtl, Rational mp, int rescaleFactor, String augmentation) {
		SortedContainer<Marker> htAugm = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		List<Rational> keySecMts = new ArrayList<>();
		ht.forEach(m -> {
			if (m instanceof KeyMarker) {
				keySecMts.add(m.getMetricTime());
			}
		});

		int ind = 0; // equals index in keySecMts
		long mpTime = augmentation.equals("reverse") ? smtl.getTime(mp) : -1;
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
					tAugm = mpTime - smtl.getTime(mtNextKeySec);
				}
				// Rescale
				else {
					Rational mt = km.getMetricTime();
					long t = smtl.getTime(mt);
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
	 * @param smtl NB: Specific functionality of <code>ScoreMetricalTimeLine</code> not needed; nevertheless 
	 *                 preferred over a <code>MetricalTimeLine</code> because the augmentation is performed 
	 *                 on a completed <code>ScorePiece</code>.
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
	static NotationSystem augmentNotationSystem(NotationSystem ns, ScoreMetricalTimeLine smtl,
		Rational mp, List<List<Note>> ch, List<Rational> onsetTimes, Rational thresholdDur, 
		int rescaleFactor, String augmentation, String name) {

		NotationSystem nsAugm = new NotationSystem();

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
								ncRev.add(createNote(n.getMidiPitch(), mp.sub(n.getMetricTime().add(dur)), 
									dur, n.getPerformanceNote().getVelocity(), smtl));
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
								ncRev.add(createNote(n.getMidiPitch(), mt, dur, 
									n.getPerformanceNote().getVelocity(), smtl));
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
										ncDeorn.add(createNote(n.getMidiPitch(), n.getMetricTime(), 
											durNcDeorn, n.getPerformanceNote().getVelocity(), smtl))
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
								n.getPerformanceNote().getVelocity(), smtl)
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
