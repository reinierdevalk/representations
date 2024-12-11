package external;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang3.SerializationUtils;

import conversion.imports.MIDIImport;
import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationStaffConnector;
import de.uos.fmt.musitech.data.score.NotationStaffConnector.CType;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.NoteSequence;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalComparator;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TempoMarker;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.data.time.TimedMetrical;
import de.uos.fmt.musitech.score.ScoreEditor;
import de.uos.fmt.musitech.score.ScoreEditor.Mode;
import de.uos.fmt.musitech.utility.math.Rational;
import conversion.exports.MEIExport;
import external.Tablature.Tuning;
import internal.core.Encoding;
import internal.core.ScorePiece;
import internal.structure.ScoreMetricalTimeLine;
import internal.structure.Timeline;
import tools.labels.LabelTools;
import tbp.symbols.RhythmSymbol;
import tbp.symbols.TabSymbol;
import tools.ToolBox;
import tools.music.TimeMeterTools;

public class Transcription implements Serializable {

	private static final long serialVersionUID = 1L;
	public static int MAX_NUM_VOICES = 5;
	public static int DUR_LABEL_MULTIPLIER = 1; // TODO set to 2 for Byrd and 3 for JosquIntab (to allow for note 2 (3) times the length of a W) 
	public static int MAX_TABSYMBOL_DUR = (Tablature.SRV_DEN / 3) * DUR_LABEL_MULTIPLIER; // trp dur
	public static final Rational SMALLEST_RHYTHMIC_VALUE = new Rational(1, 128);
	
	public static final int INCORRECT_IND = 0;
	public static final int ORNAMENTATION_IND = 1;
	public static final int REPETITION_IND = 2;
	public static final int FICTA_IND = 3;
	public static final int ADAPTATION_IND = 4;
	public static final int SPECIAL_ORN_IND = 5;

	private Type type;
	private ScorePiece scorePiece;
	private ScorePiece unaugmentedScorePiece;
	private String name;
	private List<Integer[]> meterInfo;
	private List<Integer[]> keyInfo;
	private List<TaggedNote> taggedNotes;
	private List<Note> notes;
	private List<List<Note>> chords;
	private List<List<Double>> voiceLabels;
	private List<List<List<Double>>> chordVoiceLabels;
	private List<List<Double>> durationLabels;
	private List<List<Double>> minimumDurationLabels;	
	private List<Integer[]> voicesSNU;
	private List<Integer[]> voicesUnison;
	private List<Integer[]> voicesEDU;
	private List<Integer[]> voicesIDU;
	private Integer[][] basicNoteProperties;
	private List<Integer> numberOfNewNotesPerChord;
	
//	private NoteSequence noteSequence;
//	private List<List<List<Double>>> durationLabelsOLD;
//	private List<List<Integer>> colourIndices;
	private static String chordCheck;
	static String alignmentCheck;
	private String handledNotes;

	// For basicNoteProperties
	public static final int PITCH = 0;
	public static final int ONSET_TIME_NUMER = 1;
	public static final int ONSET_TIME_DENOM = 2;
	public static final int DUR_NUMER = 3;
	public static final int DUR_DENOM = 4;
	public static final int CHORD_SEQ_NUM = 5;
	public static final int CHORD_SIZE_AS_NUM_ONSETS = 6;
	public static final int NOTE_SEQ_NUM = 7;
	public static final int NUM_BNP = 8;

	// For meterInfo
	public static final int MI_NUM = 0; // TODO 0-5 also in Tablature
	public static final int MI_DEN = 1;
	public static final int MI_FIRST_BAR = 2;
	public static final int MI_LAST_BAR = 3;
	public static final int MI_NUM_MT_FIRST_BAR = 4;
	public static final int MI_DEN_MT_FIRST_BAR = 5;	
	public static final int MI_SIZE_TRANS = 6;

	// For keyInfo
	public static final int KI_KEY = 0;
	public static final int KI_MODE = 1;
	public static final int KI_FIRST_BAR = 2;
	public static final int KI_LAST_BAR = 3;
	public static final int KI_NUM_MT_FIRST_BAR = 4;
	public static final int KI_DEN_MT_FIRST_BAR = 5;
	private static final int KI_SIZE = 6;

//	public static final List<Double> THIRTYSECOND = createDurationLabel(new Integer[]{1*3});
//	public static final List<Double> SIXTEENTH = createDurationLabel(new Integer[]{2*3});
//	public static final List<Double> DOTTED_SIXTEENTH = createDurationLabel(new Integer[]{3*3});
//	public static final List<Double> EIGHTH = createDurationLabel(new Integer[]{4*3});
//	public static final List<Double> EIGHTH_AND_THIRTYSECOND = createDurationLabel(new Integer[]{5*3});
//	public static final List<Double> DOTTED_EIGHTH = createDurationLabel(new Integer[]{6*3});
//	public static final List<Double> DOUBLE_DOTTED_EIGHTH = createDurationLabel(new Integer[]{7*3});
//	public static final List<Double> QUARTER = createDurationLabel(new Integer[]{8*3});
//	public static final List<Double> QUARTER_AND_THIRTYSECOND = createDurationLabel(new Integer[]{9*3});
//	public static final List<Double> QUARTER_AND_SIXTEENTH = createDurationLabel(new Integer[]{10*3});
//	public static final List<Double> QUARTER_AND_DOTTED_SIXTEENTH = createDurationLabel(new Integer[]{11*3});
//	public static final List<Double> DOTTED_QUARTER = createDurationLabel(new Integer[]{12*3});
//	public static final List<Double> DOTTED_QUARTER_AND_THIRTYSECOND = createDurationLabel(new Integer[]{13*3});
//	public static final List<Double> DOUBLE_DOTTED_QUARTER = createDurationLabel(new Integer[]{14*3});
//	public static final List<Double> TRIPLE_DOTTED_QUARTER = createDurationLabel(new Integer[]{15*3});
//	public static final List<Double> HALF = createDurationLabel(new Integer[]{16*3});
//	public static final List<Double> DOTTED_HALF = createDurationLabel(new Integer[]{24*3});
//	public static final List<Double> WHOLE = createDurationLabel(new Integer[]{32*3});

//	public static final List<Double> VOICE_0 = createVoiceLabel(new Integer[]{0});
//	public static final List<Double> VOICE_1 = createVoiceLabel(new Integer[]{1});
//	public static final List<Double> VOICE_2 = createVoiceLabel(new Integer[]{2});
//	public static final List<Double> VOICE_3 = createVoiceLabel(new Integer[]{3});
//	public static final List<Double> VOICE_4 = createVoiceLabel(new Integer[]{4});
	
	public static enum Type {
		FROM_FILE("from file", 0), PREDICTED("predicted", 1);

		private String stringRep; 
		private int intRep;
		Type(String s, int i) {
			this.stringRep = s;
			this.intRep = i;
		}

		@Override
	    public String toString() {
	        return getStringRep();
	    }

		public int getIntRep() {
			return intRep;
		}

		public String getStringRep() {
			return stringRep;
		}
	}


	public class TaggedNote implements Serializable {
		private static final long serialVersionUID = 1L;
		private Note note;
		private Integer[] voices;
		private Rational[] durations;
		private int indexOtherUnisonNote = -1;

		public TaggedNote(Note n, Integer[] v, Rational[] d, int i) {
			note = n;
			voices = v;
			durations = d;
			indexOtherUnisonNote = i;
		}

		public TaggedNote(Note n) {
			note = n;
		}

		public Note getNote() {
			return note;
		}

		public Integer[] getVoices() {
			return voices;
		}

		public Rational[] getDurations() {
			return durations;
		}

		public int getIndexOtherUnisonNote() {
			return indexOtherUnisonNote;
		}

		public boolean isEDU() {
			Rational[] d = getDurations();
			return d[0].isEqual(d[1]);
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (!(o instanceof TaggedNote)) {
				return false;
			}
			TaggedNote t = (TaggedNote) o;
			return 
				getNote().equals(t.getNote()) &&
				getVoices() == null ? t.getVoices() == null : 
					Arrays.equals(getVoices(), t.getVoices()) &&
				getDurations() == null ? t.getDurations() == null : 
					Arrays.equals(getDurations(), t.getDurations()) &&
				getIndexOtherUnisonNote() == t.getIndexOtherUnisonNote();
		}
	}


	public static void main(String[] args) {
	}


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Transcription() {
	}


	/**
	 * Copy constructor. Creates a deep copy of the given <code>Transcription</code>.
	 * 
	 * @param t
	 */
	public Transcription(Transcription t) {
		init(t.getScorePiece(), null, null, null, false, null, t.getType());
	}


	/**
	 * Short constructor for a <code>Transcription</code> of <code>Type.FROM_FILE</code>,
	 * non-normalised and non-diminuted.
	 *                              
	 * @param f A <code>.mid</code> file (mandatory) and a <code>.tbp</code> file (optional; 
	 *          tablature case only).
	 */
	public Transcription(File... f) {
		// https://stackoverflow.com/questions/285177/how-do-i-call-one-constructor-from-another-in-java
		this(false, null, f);
	}


	/**
	 * Constructor for a <code>Transcription</code> of <code>Type.FROM_FILE</code>, normalised. 
	 * 
	 * @param normalise Whether or not to normalise the <code>Transcription</code>. Must be <code>true</code>
	 *                  when this constructor is called.
	 * @param mf A <code>.mid</code> file. 
	 * @param ef A <code>.tbp</code> file. 
	 */
	public Transcription(boolean normalise, File mf, File ef) {
		// https://stackoverflow.com/questions/285177/how-do-i-call-one-constructor-from-another-in-java
		this(normalise, null, new File[]{mf, ef});
	}


	/**
	 * Constructor for a <code>Transcription</code> of <code>Type.FROM_FILE</code>, diminuted.
	 *     
	 * @param mi The meterInfo that governs the diminution(s). Must be non-<code>null<code> when 
	 *           this constructor is called.
	 * @param f A <code>.mid</code> file.
	 */
	public Transcription(List<Integer[]> mi, File f) {
		// https://stackoverflow.com/questions/285177/how-do-i-call-one-constructor-from-another-in-java
		this(false, mi, f);
	}


	/**
	 * Full constructor for a <code>Transcription</code> of <code>Type.FROM_FILE</code>.
	 * 
	 * @param normalise Whether or not to normalise the <code>Transcription</code>.
	 * @param mi The meterInfo that governs the diminution(s), or <code>null</code> (no diminution).
	 * @param f A <code>.mid</code> file (mandatory) and a <code>.tbp</code> file (optional; 
	 *          tablature case only).
	 */
	public Transcription(boolean normalise, List<Integer[]> mi, File... f) {
		File midF = f.length == 1 ? f[0] : 
			(f[0].getName().endsWith(MIDIImport.EXTENSION) ? f[0] : f[1]);
		File encF = f.length == 1 ? null : 
			(f[0].getName().endsWith(Encoding.EXTENSION) ? f[0] : f[1]);
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midF));
		Encoding enc = encF != null ? new Encoding(encF) : null;
		init(sp, enc, null, null, normalise, mi, Type.FROM_FILE);
	}


//	/**
//	 * Constructor for a ground truth <code>Transcription</code>. Creates a <code>Transcription</code> 
//	 * from a <code>.mid</code> and a <code>.tbp</code> file.
//	 *                              
//	 * @param argFiles A <code>.mid</code> file and a <code>.tbp</code> file (optional; tablature 
//	 *                 case only).
//	 */
//	public Transcription(int i, File... argFiles) {
//		File argMidiFile = argFiles.length == 1 ? argFiles[0] : 
//			(argFiles[0].getName().endsWith(MIDIImport.EXTENSION) ? argFiles[0] : argFiles[1]);
//		File argEncodingFile = argFiles.length == 1 ? null : 
//			(argFiles[0].getName().endsWith(Encoding.EXTENSION) ? argFiles[0] : argFiles[1]);
//
//		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(argMidiFile));
//		Encoding encoding = argEncodingFile != null ? new Encoding(argEncodingFile) : null;
//		init(sp, encoding, null, null, Type.GROUND_TRUTH);
//	}


//	/**
//	 * Constructor for a mapping <code>Transcription</code>. Creates a <code>Transcription</code> 
//	 * from a <code>.mid</code> and a <code>.tbp</code> file.
//	 * 
//	 * @param t
//	 * @param argFiles
//	 */
//	public Transcription(Type t, int i, File... argFiles) {
//		File argMidiFile =  
//			(argFiles[0].getName().endsWith(MIDIImport.EXTENSION) ? argFiles[0] : argFiles[1]);
//		File argEncodingFile = 
//			(argFiles[0].getName().endsWith(Encoding.EXTENSION) ? argFiles[0] : argFiles[1]);
//
//		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(argMidiFile));
//		Encoding encoding = new Encoding(argEncodingFile);
//		init(sp, encoding, null, null, Type.MAPPING);	
//	}


	/**
	 * Constructor for a <code>Transcription</code> of <code>Type.PREDICTED</code>. 
	 * Creates a <code>Transcription</code> from an existing <code>ScorePiece</code> 
	 * and <code>Encoding</code>. 
	 * 
	 * @param argPredScorePiece
	 * @param argEncoding
	 * @param argPredVoiceLabels
	 * @param argPredDurLabels
	 */
	public Transcription(ScorePiece argPredScorePiece, Encoding argEncoding,  
		List<List<Double>> argPredVoiceLabels, List<List<Double>> argPredDurLabels) {
		
//		Encoding argEncoding = argEncoding != null ? new Encoding(argEncoding) : null;

//		// Create and set the predicted Piece
//		Piece predictedPiece = 
//			createPiece(argBtp, argBnp, argVoiceLabels, argDurationLabels, argHiNumVoices, mtl, ks);
//		predictedPiece.setName(name);

//		boolean normaliseTuning = true; // is only used in the tablature case
//		boolean isGroundTruthTranscription = false;
		init(argPredScorePiece, argEncoding, /*null,*/ 
			/*normaliseTuning, isGroundTruthTranscription,*/
			argPredVoiceLabels, argPredDurLabels, false, null, Type.PREDICTED);
			
//		// Set the predicted class fields. When creating a ground truth Transcription, this happens inside
//		// handleCoDNotes() and handleCourseCrossings(), but when creating a predicted Transcription this step
//		// is skipped in those methods because the voice labels and duration labels are already ready-to-use. In 
//		// the tablature case, only voicesCoDNotes must still be created from them
//		setVoiceLabels(argVoiceLabels);
//		// a. In the tablature case
//		if (argEncodingFile != null) {
//			// Set durationLabels
//			// NB: The durationLabels created in createTranscription are overwritten by argDurationLabels. Thus, 
//			// when not modelling duration (when argDurationLabels == null), they are reset to null
//			setDurationLabels(argDurationLabels);
//			// Create voicesCoDNotes
//			// NB: currently, only one duration is always predicted; both CoDnotes thus have the same duration. In
//			// this case, the lower CoDnote (i.e., the one in the lower voice that comes first in the NoteSequence) 
//			// is placed at element 0 (see Javadoc handleCoDNotes())
//			List<Integer[]> voicesCoDNotes = new ArrayList<Integer[]>();
//			// For each predicted voiceLabel
//			for (int i = 0; i < argVoiceLabels.size(); i++) {
//				List<Double> currLabel = argVoiceLabels.get(i);
//				// IN case of a CoD, voices contain two elements: the highest predicted voice as element 0, 
//				// and the lowest predicted voice as element 1 
//				List<Integer> voices = DataConverter.convertIntoListOfVoices(currLabel);
//				// If a CoD is predicted
//				if (voices.size() > 1) {
//					Integer[] currVoicesCoDNotes = new Integer[2];
//					currVoicesCoDNotes[0] = voices.get(1); // lowest predicted voice
//					currVoicesCoDNotes[1] = voices.get(0); // highest predicted voice
//					voicesCoDNotes.add(currVoicesCoDNotes);
//				}
//				// If no CoD is predicted
//				else {
//					voicesCoDNotes.add(null);
//				}
//			}	
//			setVoicesSNU(voicesCoDNotes);
//		}	
//		// b. In the non-tablature case
//		else {
//			setEqualDurationUnisonsInfo(argEqualDurationUnisonsInfo);
//		}  	  	
	}


	/**
	 * Makes an empty Transcription with the given time signature, key signature, and number of voices.
	 *
	 * @param mtl
	 * @param numVoices
	 * @return
	 */
	// TODO copied from TestManager(); replace by simpler list method in TestManager
	public Transcription (MetricalTimeLine mtl, /*TimeSignature timeSig,
	 	KeyMarker keyMarker,*/ int numVoices) {
//		newTranscription = new Transcription();
//		Transcription newTranscription = new Transcription();
//		NotationSystem notationSystem = newTranscription.createNotationSystem();

		Piece p = new Piece();
		p.setMetricalTimeLine(mtl); // TODO
		setPiece(new ScorePiece(p));
//		newTranscription.setPiece(p);
		NotationSystem notationSystem = getScorePiece().createNotationSystem();
//		NotationSystem notationSystem = newTranscription.getPiece().createNotationSystem();

		// Add time and key signatures
//		MetricalTimeLine mtl = newTranscription.getPiece().getMetricalTimeLine();

//		TimeSignatureMarker timeSigMarker = 
//			new TimeSignatureMarker(timeSig.getNumerator(), timeSig.getDenominator(), 
//			new Rational(0, 1));
//		timeSigMarker.setTimeSignature(timeSig);
//		mtl.add(timeSigMarker);
//		mtl.add(keyMarker);

		// Create staves
		for (int i = 0; i < numVoices; i++) { 
			NotationStaff staff = new NotationStaff(notationSystem);
//			// Ensure correct cleffing for each staff: G-clef for the upper two and F-clef for the lower three
//			if (i < 2) {
//				staff.setClefType('g', -1, 0);
//			}
//			else {
//				staff.setClefType('f', 1, 0);
//			}
			notationSystem.add(staff);
			NotationVoice notationVoice = new NotationVoice(staff);
			staff.add(notationVoice);
		}

		// Set the initial NoteSequence and voice labels
//		makeNoteSequence();
//		newTranscription.initialiseNoteSequence();
//		initialiseVoiceLabels(null);
//		newTranscription.initialiseVoiceLabels(null);

//		return newTranscription;
	}


	/**
	 * Creates a new Transcription from the given arguments. 
	 *
	 * @param argScorePiece 
	 * @param argEncoding Only non-<code>null</code> in the tablature case.
	 * @param argVoiceLabels Only none-<code>null</code> if <code>t</code> is <code>Type.PREDICTED</code>.
	 * @param argDurLabels Only none-<code>null</code> if <code>t</code> is <code>Type.PREDICTED</code>.
	 * @param normalise
	 * @param mi Only non-<code>null</code> if the <code>Transcription</code> is diminuted.
	 * @param t
	 */
	private void init(ScorePiece argScorePiece, Encoding argEncoding, List<List<Double>> argVoiceLabels, 
		List<List<Double>> argDurLabels, boolean normalise, List<Integer[]> mi, Type t) {
		// normaliseTuning is
		// - GROUND_TRUTH case: true if the Transcription is used for training a model; false if not
		// - PREDICTED case: true, as the predicted ScorePiece is created from a normalised Tablature 
		//
		// The ScorePiece is transposed
		// - only if normaliseTuning == true
		// - only in the GROUND_TRUTH case. When init() is called 
		//   - in the GROUND_TRUTH case: ScorePiece has been loaded from file and is still 
		//     in its original key (the key of the non-normalised Tablature)
		//   - in the PREDICTED case: ScorePiece has been created from scratch (i.e., predicted) 
		//     from an already normalised Tablature, and is therefore already in the normalised key
		//
		// The Tablature is non-null only in the tablature case. It is NOT the Tablature that forms
		// a TablatureTranscriptionPair with the Transcription during training, but is used for
		// - transposition of the ScorePiece, if applicable (w/ setScorePiece()) 
		// - alignment of the Transcription and Tablature (w/ setTaggedNotes())
		//   - checking chords 
		//   - handling of SNUs 
		//   - handling of course crossings
		//   - checking final alignment 
		// - setting the chordVoiceLabels
		// - setting the minimumDurationLabels
		Tablature tab = 
			argEncoding != null ? new Tablature(argEncoding, (t != Type.PREDICTED ? normalise : true)) 
			: null;
		boolean isTabCase = tab != null;

		setType(t);
		setScorePiece(argScorePiece, tab, mi);
		setUnaugmentedScorePiece();
		setName();
		setMeterInfo();
		setKeyInfo();
		setTaggedNotes(tab);
		setNotes();
		setChords();

		setVoiceLabels(t == Type.PREDICTED ? argVoiceLabels : null, isTabCase);
		setChordVoiceLabels(isTabCase ? tab : null);
		// a. Tablature case
		if (isTabCase) {
//			setVoicesSNU();
			setDurationLabels(t == Type.PREDICTED ? argDurLabels : null);
			setMinimumDurationLabels(tab, MAX_TABSYMBOL_DUR);
//			if (t != Type.PREDICTED) {
//				setDurationLabels(null);
////				initialiseDurationLabels(null); // needs <noteSequence>
//			}
//			else {
//				setDurationLabels(argDurLabels);
////				initialiseDurationLabels(argDurLabels); // labels have their final form 
//			}
			setVoicesSNU();

//			// 1. Check chords 
//			// NB: normaliseTuning is false when creating a ground truth Transcription and true 
//			// when creating a predicted Transcription (see Javadoc for this method)
//			tab = new Tablature(encoding, normaliseTuning);
//			if (checkChords(tab) == false) { // needs <noteSequence>
//				System.out.println(chordCheck);
//				throw new RuntimeException("ERROR: Chord error (see console).");
//			}
//			// 2. Align tablature and transcription
//			handleSNUs(tab, t); // needs <noteSequence>, <voiceLabels>, and <durationLabels> (and changes these)
//			handleCourseCrossings(tab, t); // needs <noteSequence>, <voiceLabels>, and <durationLabels> (and changes these)
//			// 3. Do final alignment check
//			if (checkAlignment(tab) == false) {
//				System.out.println(alignmentCheck);
//				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
//			}
//			// 4. Transpose (only if ground truth Transcription; see Javadoc for this method)
//			if (t != Type.PREDICTED) {
//				transpose(tab.getTranspositionInterval());
//			}
//			setChords(); // needs <noteSequence> (finalised)
//			setMeterInfo(tab.getTimeline().getMeterInfo());
//			setKeyInfo(); // must be done after possible transpose()
//			setMinimumDurationLabels(tab);
		}
		// b. Non-tablature case
		else {
			setVoicesUnison();
			setVoicesEDU();
			setVoicesIDU();
//			setMeterInfo();
//			setKeyInfo();
//			handleUnisonsss(t); // needs <noteSequence> and <voiceLabels> (and changes these)
//			setChords(); // needs <noteSequence> (finalised)
			setBasicNoteProperties();
			setNumberOfNewNotesPerChord();
		}
//		// c. In both
//		if (t != Type.PREDICTED) {
//			setChordVoiceLabels(tab); // needs <chords>
//		}
//		else {
//			// Currently no chordVoiceLabels needed in bidir model
//		}
	}


	//////////////////////////////
	//
	//  S E T T E R S  
	//  for instance variables
	//
	public void setPiece(ScorePiece argPiece) { // TODO make access package
		scorePiece = argPiece;
	}


	void setType(Type t) {
		type = t;
	}


	void setScorePiece(ScorePiece argScorePiece, Tablature tab, List<Integer[]> mi) {
		scorePiece = makeScorePiece(argScorePiece, tab, mi);
	}


	// NOT TESTED (wrapper method)
	ScorePiece makeScorePiece(ScorePiece argScorePiece, Tablature tab, List<Integer[]> mi) {
		Type t = getType();
		if (t == Type.FROM_FILE) {
			if (tab != null && tab.getNormaliseTuning() == true) {
				int ti = tab.getTranspositionInterval();
				if (ti != 0) {
					argScorePiece.transpose(ti);
				}
			}
			else if (mi != null) {
				argScorePiece.diminute(mi);
			}
		}
		return argScorePiece;
		
//		if (t == Type.GROUND_TRUTH) {
//			// Clean mtl
////			MetricalTimeLine mtl = argPiece.getMetricalTimeLine();
////			mtl = ScorePiece.cleanMetricalTimeLine(mtl);
//			// Clean ht
////			SortedContainer<Marker> ht = argPiece.getHarmonyTrack();
////			ht = ScorePiece.cleanHarmonyTrack(ht);
//			// Transpose ht and ns
//			if (tab != null) {
//				argScorePiece.transpose(tab.getTranspositionInterval());
////				int transposition = tab.getTranspositionInterval();
////				SortedContainer<Marker> ht = argPiece.getHarmonyTrack();
////				ht = ScorePiece.transposeHarmonyTrack(ht, transposition);
////				argPiece.setHarmonyTrack(ht);
////				NotationSystem ns = argPiece.getScore();
////				ns = ScorePiece.transposeNotationSystem(ns, transposition);
////				argPiece.setScore(ns);
//			}
//			// Set mtl and ht
////			argPiece.setMetricalTimeLine(mtl);
////			argPiece.setScoreMetricalTimeLine(new ScoreMetricalTimeLine(mtl));
////			argPiece.setHarmonyTrack(ht);
//		}
//		else if (t == Type.MAPPING) {
//			argScorePiece.diminute(tab.getMeterInfo());
////			// Clean, align, and diminute mtl
////			List<Integer[]> mi = tab.getMeterInfo();
////			MetricalTimeLine mtl = argPiece.getScoreMetricalTimeLine();
//////			mtl = ScorePiece.cleanMetricalTimeLine(mtl);
////			mtl = ScorePiece.alignMetricalTimeLine(mtl, mi);
////			ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(mtl);
////			MetricalTimeLine mtlDim = ScorePiece.diminuteMetricalTimeLine(mtl, mi);
////			ScoreMetricalTimeLine smtlDim = new ScoreMetricalTimeLine(mtlDim);
////			// Clean and diminute ht
////			SortedContainer<Marker> ht = argPiece.getHarmonyTrack();
//////			ht = ScorePiece.cleanHarmonyTrack(ht);
////			ht = ScorePiece.diminuteHarmonyTrack(ht, mi, smtl, smtlDim);
////			// Diminute ns
////			NotationSystem ns = argPiece.getScore();
////			ns = ScorePiece.diminuteNotationSystem(ns, mi, smtl, smtlDim);
////			// Set ns, mtl, and ht
////			argPiece.setScore(ns);
////			argPiece.setMetricalTimeLine(mtlDim);
////			argPiece.setScoreMetricalTimeLine(smtlDim);
////			argPiece.setHarmonyTrack(ht);
//		}
//		else if (t == Type.AUGMENTED || t == Type.PREDICTED) {
//			return argScorePiece;
//		}
//		return argScorePiece;
	}


	void setUnaugmentedScorePiece() {
		unaugmentedScorePiece = SerializationUtils.clone(getScorePiece());
	}


	void setName() {
		String n = getScorePiece().getName(); 
		name = 
			n.contains(MIDIImport.EXTENSION) ? n.substring(0, n.indexOf(MIDIImport.EXTENSION)) : n;
	}


	void setMeterInfo() {
		meterInfo = makeMeterInfo();
	}


	// TESTED
	List<Integer[]> makeMeterInfo() {
		long[][] timeSigs = getScorePiece().getMetricalTimeLine().getTimeSignature();

		int numTimeSigs = timeSigs.length;
		int start = 1;
//		// If there is an anacrusis, start should be 0
//		if (numTimeSigs > 1) {
//			Rational firstTimeSig = new Rational((int)timeSigs[0][0], (int)timeSigs[0][1]);
//			Rational secondMetricTime = new Rational((int)timeSigs[1][3], (int)timeSigs[1][4]);
//			Rational secondTimeSig = new Rational((int)timeSigs[1][0], (int)timeSigs[1][1]);
//			// An anacrusis is assumed when the first time sig is smaller than the second and 
//			// the metric time of the second time sig equals the first time sig
//			// NB: When exporting a .sib file with a real anacrusis to MIDI, the anacrusis bar
//			// is padded with rests. To get a real anacrusis in a MIDI file, the anacrusis bar
//			// must thus be given its own meter
//			if (firstTimeSig.isLess(secondTimeSig) && secondMetricTime.equals(firstTimeSig)) {
//				start = 0;
//			}
//		}
		List<Integer[]> mi = new ArrayList<Integer[]>();

		int numBars;
		for (int i = 0; i < numTimeSigs; i++) {
			Integer[] currMeterInfo = new Integer[MI_SIZE_TRANS];
			long[] curr = timeSigs[i];
			Rational currMeter = new Rational(curr[0], curr[1]);
			Rational currMetricTime = new Rational(curr[3], curr[4]);
			// If there is a next time sig
			if ((i+1) < numTimeSigs) {
				long[] next = timeSigs[i+1];
				Rational nextMetricTime = new Rational(next[3], next[4]);
				numBars = (int) (nextMetricTime.sub(currMetricTime)).div(currMeter).toDouble();
			}
			// If there is no next time sig
			else {
				// Determine the offset of the last note of the piece
				Rational end = Rational.ZERO;
				for (NotationStaff ns : getScorePiece().getScore()) {
					NotationVoice nv = ns.get(0);
					NotationChord lastNc = nv.get(nv.size() - 1);
					Rational currEnd = lastNc.getMetricTime().add(lastNc.getMetricDuration());
					if (currEnd.isGreater(end)) {
						end = currEnd;
					}
				}
				// Determine the remaining time in bars
				Rational rem = end.sub(currMetricTime);
				numBars = (rem.div(currMeter)).ceil();
			}
			currMeterInfo[MI_NUM] = (int)curr[0];
			currMeterInfo[MI_DEN] = (int)curr[1];
			currMeterInfo[MI_FIRST_BAR] = start;
			currMeterInfo[MI_LAST_BAR] = start + (numBars - 1);
			currMeterInfo[MI_NUM_MT_FIRST_BAR] = currMetricTime.getNumer();
			currMeterInfo[MI_DEN_MT_FIRST_BAR] = currMetricTime.getNumer() == 0 ? 1 : currMetricTime.getDenom();
			mi.add(currMeterInfo);
			start += numBars;
		}		
		return mi;
	}


	void setKeyInfo() {
		keyInfo = makeKeyInfo();
	}


	// TESTED
	List<Integer[]> makeKeyInfo() {
		List<Integer[]> keyInfo = new ArrayList<Integer[]>();

//		MetricalTimeLine mtl = getScorePiece().getMetricalTimeLine();
		SortedContainer<Marker> keySigs = getScorePiece().getHarmonyTrack();
		List<Integer[]> mi = getMeterInfo();
		ScoreMetricalTimeLine smtl = getScorePiece().getScoreMetricalTimeLine();
		int numKeySigs = keySigs.size();
		for (int i = 0; i < numKeySigs; i++) {
			Integer[] currKeyInfo = new Integer[KI_SIZE];
			KeyMarker km = (KeyMarker) keySigs.get(i);
			int key = km.getAlterationNum(); 
			// Reverse KeyMarker.Mode labels (minor = 0; major = 1)
			int mode = Math.abs(km.getMode().getCode() -1);
			Rational mt = km.getMetricTime();
			// It is assumed that key signature changes only occur at the beginning of a bar
			int firstBar = 
				smtl.getMetricPosition(mt)[0].getNumer();	
//				ScoreMetricalTimeLine.getMetricPosition(mtl, mt)[0].getNumer();	
//				Utils.getMetricPosition(mt, meterInfo)[0].getNumer();
			int lastBar = -1;
			// If there is a next keysig
			if ((i+1) < numKeySigs) {
				KeyMarker next = (KeyMarker) keySigs.get(i+1);
				Rational mtNext = next.getMetricTime();
				int firstBarNext = 
					smtl.getMetricPosition(mtNext)[0].getNumer();	
//					ScoreMetricalTimeLine.getMetricPosition(mtl, mtNext)[0].getNumer();
//					Utils.getMetricPosition(mtNext, meterInfo)[0].getNumer();
				lastBar = firstBarNext - 1;
			}
			else {
				lastBar = mi.get(mi.size() -1)[MI_LAST_BAR];
			}
			currKeyInfo[KI_KEY] = key;
			currKeyInfo[KI_MODE] = mode;
			currKeyInfo[KI_FIRST_BAR] = firstBar;
			currKeyInfo[KI_LAST_BAR] = lastBar;
			currKeyInfo[KI_NUM_MT_FIRST_BAR] = mt.getNumer();
			currKeyInfo[KI_DEN_MT_FIRST_BAR] = mt.getNumer() == 0 ? 1 : mt.getDenom();
			keyInfo.add(currKeyInfo);
		}
		return keyInfo;
	}
	
	
	public List<Integer[]> makeKI() {
		return makeKeyInfo();
	}


	void setTaggedNotes(Tablature tab) {
		taggedNotes = makeTaggedNotes(tab);
	}


	/**
	 * Makes the list of <code>TaggedNote</code>s, in which all notes from the <code>Piece</code> are 
	 * added as a <code>TaggedNote</code>, ordered hierarchically by<br>
	 * <ol>
	 * <li>Onset time (lower first).</li>
	 * <li>If two notes have the same onset time: pitch (lower first).</li>
	 * <li>If two notes have the same onset time and the same pitch: voice (lower first).</li>
	 * </ol>
	 * 
	 * In the list returned, any SNUs (tablature case), course crossings (tablature case), 
	 * and unisons (non-tablature case) are handled, i.e.,
	 * <ul>
	 * <li>SNU notes are merged; the SNU voices and durations are set in the <code>TaggedNote</code> 
	 *     (see <code>handleSNUs()</code>).</li>
	 * <li>Course crossing notes are swapped so that they are in the correct order (see 
	 *     <code>handleCourseCrossings()</code>).</li>
	 * <li>Unisons notes are swapped (if necessary) so that they are in the correct order; the 
	 *     unison voices and durations, as well as the index of the respective complementary 
	 *     unison note are set in the <code>TaggedNote</code>s (see <code>handleUnisons()</code>).</li>
	 *      
	 * </ul>
	 *  
	 * NB: Any unison notes (tablature case) need no further handling, as the lower-course unison
	 * note automatically always comes first.
	 * 
	 * @param tab
	 * @param t 
	 * @return
	 */
	// NOT TESTED (wrapper method)
	List<TaggedNote> makeTaggedNotes(Tablature tab) {
		List<TaggedNote> argTaggedNotes;

		// Make unhandled notes
		List<Note> argNotes = makeUnhandledNotes();
//		System.out.println(argNotes.size());
		

		// In the tablature case: handle SNUs and course crossings
		if (tab != null) {
//		if (tab != null && t != Type.MAPPING) {
			// Check tablature chords
			if (checkChords(argNotes, tab) == false) {
				System.out.println(chordCheck);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}

			// Handle SNUs
			argTaggedNotes = handleSNUs(argNotes, tab);
			// Handle course crossings
			argTaggedNotes = handleCourseCrossings(argTaggedNotes, tab);

			// Check alignment
			if (checkAlignment(argTaggedNotes, tab) == false) {
				System.out.println(alignmentCheck);
				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");
			}
		}
		// In the non-tablature case: handle unisons
		else {
			argTaggedNotes = handleUnisons(argNotes);
		}
//		System.out.println(argTaggedNotes.get(0).getNote());
//		System.out.println(argTaggedNotes.get(0).getNote().getPerformanceNote());
		return argTaggedNotes;
	}


	// TESTED
	List<Note> makeUnhandledNotes() {
		List<Note> notes = new ArrayList<>();
		getScorePiece().getScore().getContentsRecursiveList(null).stream()
			.filter(c -> c instanceof Note)
			.forEach(c -> notes.add((Note) c));
//		System.out.println(notes.get(0));
//		System.out.println(notes.get(0).getPerformanceNote());
		Collections.sort(notes, Comparator.comparing(Note::getMetricTime)
			.thenComparing(Note::getMidiPitch)
			.thenComparing(this::findVoice, Comparator.reverseOrder()));
		return notes;
	}


	// TESTED
	public int findVoice(Note note) {
		NotationSystem ns = getScorePiece().getScore();
		for (int i = 0; i < ns.size(); i++) {
			if (ns.get(i).get(0).containsRecursive(note)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Checks whether the given <code>Tablature</code> contains any illegal chords, i.e.,
	 * <ul>
	 * <li>Chords with more than one SNU, unison, or course crossing.</li>
	 * <li>Chords with combinations of SNUs, unisons, and course crossings.</li>
	 * </ul>
	 * 
	 * Adds a summary of the findings to <code>chordCheck</code>.<br><br>
	 *   
	 * NB: Tablature case only; must be called before handling of SNUs and course crossings.
	 *  
	 * @param argNotes
	 * @param tab
	 * @return <code>true</code> if no illegal chords are found; else <code>false</code>.
	 */
	// NOT TESTED
	static boolean checkChords(List<Note> argNotes, Tablature tab) {  
		boolean checkPassed = true;

		List<List<TabSymbol>> tabCh = tab.getChords();
		List<List<Note>> argChords = getChordsFromNotes(argNotes);
		Timeline tl = tab.getEncoding().getTimeline();
		Tuning t = tab.getNormaliseTuning() ? tab.getTunings()[Tablature.NORMALISED_TUNING_IND] : 
			tab.getTunings()[Tablature.ENCODED_TUNING_IND];

		// 0. Check equality of number of chords
		if (tabCh.size() != argChords.size()) {
			chordCheck = tabCh.size() + " chords in Tablature, " + argChords.size() + " in Transcription";
			return false;
		}

		// 1. Counts
		String counts = "the Tablature/Transcription contains" + "\n";
		counts = counts.concat(
			"- " + tab.getNumberOfNotes() + "/" + 
				argChords.stream().flatMap(List::stream).collect(Collectors.toList()).size() + " notes" + "\n" +
			"- " + tabCh.size() + "/" + argChords.size() + " chords" + "\n" +
			"- " + Collections.frequency(tab.getEncoding().getListsOfStatistics()
				.get(Encoding.IS_REST_EVENT_IND), 1) + " rest events (Tablature)" + "\n"
		);

		// 2. Special chords
		String special = "chords with SNUs, unisons, or course crossings" + "\n"; 
		List<String> specialChords = new ArrayList<String>();
		List<String> oneEqualPitchPair = new ArrayList<String>();
		List<String> twoEqualPitchPairs = new ArrayList<String>();
		List<String> equalPitchTriplet = new ArrayList<String>();
		List<String> oneSNU = new ArrayList<String>();
		List<String> twoSNUs = new ArrayList<String>();
		List<String> threeSNUs = new ArrayList<String>();
		List<String> oneUnison = new ArrayList<String>();
		List<String> twoUnisons = new ArrayList<String>();
		List<String> threeUnisons = new ArrayList<String>();
		List<String> oneCC = new ArrayList<String>();
		List<String> twoCCs = new ArrayList<String>();
		List<String> threeCCs = new ArrayList<String>();
		for (int i = 0; i < tabCh.size(); i++) {
			Rational[] metPos = 
				tl.getMetricPosition(tab.getBasicTabSymbolPropertiesChord(i)[0][Tablature.ONSET_TIME])
//				Utils.getMetricPosition(
//				new Rational(tab.getBasicTabSymbolPropertiesChord(i)[0][Tablature.ONSET_TIME], 
//				Tablature.SRV_DEN), tab.getMeterInfo())
			;
			String metPosAsString = 
				String.valueOf(metPos[0].getNumer()) + 
				(metPos[1].getNumer() == 0 ? "" : " " + String.valueOf(metPos[1]));
			// a. Check for equal-pitch-pairs
			List<Integer> pitches = getPitchesInChord(argChords.get(i));
			if (pitches.stream().distinct().collect(Collectors.toList()).size() < pitches.size()) {
				List<Integer> pitchFreq = getPitchFrequency(argChords.get(i));
				if (Collections.frequency(pitchFreq, 2) == 2) {
					oneEqualPitchPair.add(metPosAsString);
				}
				if (Collections.frequency(pitchFreq, 2) == 4) {
					twoEqualPitchPairs.add(metPosAsString);
				}
				if (pitchFreq.contains(3)) { 
					equalPitchTriplet.add(metPosAsString);
				}
				if (!specialChords.contains(metPosAsString)) {
					specialChords.add(metPosAsString);
				}
			}
			// b. Check for any SNUs
			List<Note> ch = argChords.get(i);
			List<TabSymbol> tch = tabCh.get(i);
			if (ch.size() != tabCh.get(i).size()) {
				if (ch.size() == (tch.size() + 1)) {					
					oneSNU.add(metPosAsString);
				}
				if (ch.size() == (tch.size() + 2)) {
					twoSNUs.add(metPosAsString);
				}
				if (ch.size() == (tch.size() + 3)) {
					threeSNUs.add(metPosAsString);
				}
				if (!specialChords.contains(metPosAsString)) {
					specialChords.add(metPosAsString);
				}
			}	     
			// c. Check for any unisons
			List<Integer[]> uniInfo = Tablature.getUnisonInfo(tabCh.get(i), t);
			if (uniInfo != null) {
				if (uniInfo.size() == 1) {
					oneUnison.add(metPosAsString);
				}
				if (uniInfo.size() == 2) {
					twoUnisons.add(metPosAsString);
				}
				if (uniInfo.size() == 3) {
					threeUnisons.add(metPosAsString);
				}
				if (!specialChords.contains(metPosAsString)) {
					specialChords.add(metPosAsString);
				}
			}
			// d. Check for any course crossings
			List<Integer[]> ccInfo = Tablature.getCourseCrossingInfo(tabCh.get(i), t);
			if (ccInfo != null) {
				if (ccInfo.size() == 1) {
					oneCC.add(metPosAsString);
				}
				if (ccInfo.size() == 2) {
					twoCCs.add(metPosAsString);
				}
				if (ccInfo.size() == 3) {
					threeCCs.add(metPosAsString);
				}
				if (!specialChords.contains(metPosAsString)) {
					specialChords.add(metPosAsString);
				}
			}
		}
		special = special.concat(
			"- equal-pitch pairs" + "\n" +
			"  - one in bar(s)   " + oneEqualPitchPair + "\n" +
			"  - two in bar(s)   " + twoEqualPitchPairs + "\n" +  	
			"  - three in bar(s) " + equalPitchTriplet + "\n" +
			"- SNUs" + "\n" +
			"  - one in bar(s)   " + oneSNU + "\n" +
			"  - two in bar(s)   " + twoSNUs + "\n" +
			"  - three in bar(s) " + threeSNUs + "\n" +
			"- unisons" + "\n" +
			"  - one in bar(s)   " + oneUnison + "\n" +
			"  - two in bar(s)   " + twoUnisons + "\n" +
			"  - three in bar(s) " + threeUnisons + "\n" +
			"- course crossings" + "\n" +
			"  - one in bar(s)   " + oneCC + "\n" +
			"  - two in bar(s)   " + twoCCs + "\n" +
			"  - three in bar(s) " + threeCCs + "\n" +
			"- duplicates or combinations" + "\n"
		);
		// Find any illegal duplicates or combinations, i.e., chords containing (a) more than one SNU, 
		// unison, or course crossing; (b) any combination of SNUs, unisons, or course crossings
		String details = "  (none)";
		// a. More than one SNU, unison, or course crossing
		if (twoSNUs.size() != 0 || threeSNUs.size() != 0 || twoUnisons.size() != 0 ||
			threeUnisons.size() != 0 ||	twoCCs.size() != 0 || threeCCs.size() != 0) {
			details = "";
			checkPassed = false;
			if (twoSNUs.size() != 0) {
				details = details.concat("  - two SNUs in bar(s) " + twoSNUs + "\n");
			}
			if (threeSNUs.size() != 0) {
				details = details.concat("  - three SNUs in bar(s) " + threeSNUs + "\n");
			}
			if (twoUnisons.size() != 0) {
				details = details.concat("  - two unisons in bar(s) " + twoUnisons + "\n");
			}
			if (threeUnisons.size() != 0) {
				details = details.concat("  - three unisons in bar(s) " + threeUnisons + "\n");
			}
			if (twoCCs.size() != 0) {
				details = details.concat("  - two course crossings in bar(s) " + twoCCs + "\n");
			}
			if (threeCCs.size() != 0) {
				details = details.concat("  - three course crossings in bar(s) " + threeCCs + "\n");
			}
		}
		// b. Combinations of SNUs, unisons, and course crossings 
		// Find which chord appears more than once in specialChords and add that to combiChords
		List<String> combiChords = new ArrayList<String>();
		specialChords.stream()
			.filter(c -> Collections.frequency(specialChords, c) > 1)
			.forEach(c -> { if (!combiChords.contains(c)) {combiChords.add(c);} });
		if (combiChords.size() != 0) {
			details = "";
			checkPassed = false;
			List<String> allSNUs = 
				Stream.concat(Stream.concat(oneSNU.stream(), twoSNUs.stream()), threeSNUs.stream())
				.collect(Collectors.toList());
			List<String> allUnisons = 
				Stream.concat(Stream.concat(oneUnison.stream(), twoUnisons.stream()), threeUnisons.stream())
				.collect(Collectors.toList());
			List<String> allCCs = 
				Stream.concat(Stream.concat(oneCC.stream(), twoCCs.stream()), threeCCs.stream())
				.collect(Collectors.toList());
			for (String metPos : combiChords) {
				// There are four possible combinations:
				// 1. SNUs and unisons (= unisons and SNUs)  
				// 2. SNUs and course crossings (= course crossings and SNUs)
				// 3. unisons and course crossings (= course crossings and unisons)
				// 4. SNUs, unisons, and course crossings
				if (allSNUs.contains(metPos) && allUnisons.contains(metPos)) {
					details = details.concat("  - SNU(s) and unison(s) in bar " + metPos + "\n");
				}
				if (allSNUs.contains(metPos) && allCCs.contains(metPos)) {
					details = details.concat("  - SNU(s) and course crossing(s) in bar " + metPos + "\n");	
				}
				if (allUnisons.contains(metPos) && allCCs.contains(metPos)) {
					details = details.concat("  - unison(s) and course crossing(s) in bar " + metPos + "\n");
				}	 
				if (allSNUs.contains(metPos) && allUnisons.contains(metPos) && allCCs.contains(metPos)) {
					details = details.concat("  - SNU(s), unison(s), and course crossing(s) in bar " + metPos + "\n");
				}
			}
		}
		if (chordCheck == null) {
			chordCheck = "";
		}
		chordCheck = chordCheck.concat(counts + special + details);
		return checkPassed; 
	}


	/**
	 * Returns a list of the same size of the chord given, containing the frequency of 
	 * each pitch within the chord.
	 *
	 * @param chord
	 * @return
	 */
	// TESTED
	static List<Integer> getPitchFrequency(List<Note> chord) {
		List<Integer> frequencies = new ArrayList<Integer>();
		List<Integer> pitchesInChord = getPitchesInChord(chord);
		for (int p : pitchesInChord) {
			frequencies.add(Collections.frequency(pitchesInChord, p));
		}
		return frequencies;

//		// Create occurrences with default values 1
//		List<Integer> occurrences = new ArrayList<Integer>();
//		for (int j = 0; j < pitchesInChord.size(); j++) {
//			occurrences.add(1);
//		}
//		// Determine how often each individual pitch appears in chord
//		for (int j = 0; j < pitchesInChord.size(); j++) {
//			int currentPitch = pitchesInChord.get(j);
//			int occurrencesOfCurrentPitch = 1;
//			// Set index j of pitchesInChord temporarily to 0 and search pitchesInChord for other occurrences of 
//			// currentPitch
//			pitchesInChord.set(j, 0);
//			for (int k = 0; k < pitchesInChord.size(); k++) {
//				if (pitchesInChord.get(k) == currentPitch) {
//					occurrencesOfCurrentPitch++;
//				}
//			}
//			// Set element j of occurences to occurenceOfCurrentPitch
//			occurrences.set(j, occurrencesOfCurrentPitch);
//			// Reset index j of pitchesInChord and proceed with the next iteration of the outer for
//			pitchesInChord.set(j, currentPitch);
//		}
//		return occurrences;
	}


	/**
	 * Gets the chords from the given list of <code>Note</code>s.
	 * NB: If <code>notes</code> is not <code>null</code>, <code>getChords()</code> should be called.
	 * 
	 * @param argNotes 
	 * @return
	 */
	// TESTED
	static List<List<Note>> getChordsFromNotes(List<Note> argNotes) {
		List<List<Note>> argChords = new ArrayList<List<Note>>();

		List<Note> currChord = new ArrayList<Note>();
		Rational onsetPrevNote = argNotes.get(0).getMetricTime();
		for (int i = 0; i < argNotes.size(); i++) {
			Note currNote = argNotes.get(i);
			Rational onsetCurrNote = currNote.getMetricTime();
			if (onsetCurrNote.equals(onsetPrevNote)) {
				currChord.add(currNote);
			}
			else {
				argChords.add(currChord);
				currChord = new ArrayList<Note>();
				currChord.add(currNote);
			}
			if (i == argNotes.size() - 1) {
				argChords.add(currChord);
			}
			onsetPrevNote = onsetCurrNote;
		}
		return argChords;
	}


	/** 
	 * Handles single-note unisons (SNUs). Iterates through the given list of unhandled <code>Note</code>s
	 * and checks for SNU note pairs. If a <code>Note</code> in the list is not part of a SNU note pair, it 
	 * is added to the list of <code>TaggedNote</code>s returned as a simple <code>TaggedNote</code>; else, 
	 * the SNU note pair is handled at once:
	 * 
	 * <ul>
	 * <li>One SNU note is added as a full <code>TaggedNote</code> (the complementary SNU note is excluded)
	 *     <ul>
	 *     <li>If the SNU notes have the same duration: the lower (lower-voice) SNU note.</li>
	 *     <li>If the SNU notes have different durations: the SNU note that has the longer duration.</li>
	 *     </ul>
	 * </li>
	 * <li>The SNU voices are set in the full <code>TaggedNote</code>
	 *     <ul>
	 *     <li>If the SNU notes have the same duration: with the lower voice first.</li>
	 *     <li>If the SNU notes have different durations: with the voice that contains the note that has 
	 *         the longer duration first.</li>
	 *     </ul>
	 * </li>         
	 * <li>The SNU durations are set in the full <code>TaggedNote</code>, with the longer 
	 *     duration first.</li>
	 * </ul>
	 * 
	 * If <code>t == Type.PREDICTED</code>, the SNU notes always have the same duration: i.e., if no 
	 * duration is predicted, the (minimum) duration for the Tablature note; else, the duration that 
	 * is predicted (only one duration is predicted).<br><br>
	 * 
	 * Adds a summary of the findings to <code>handledNotes</code>.<br><br>
	 * 
	 * NB1: This method presumes that a chord contains only one SNU, and neither a course crossing nor a 
	 *      unison.<br>
	 * NB2: Tablature case only; must be called before handleCourseCrossings().
	 * 
	 * @param argNotes
	 * @param argTabChords
	 */
	// TESTED
	List<TaggedNote> handleSNUs(List<Note> argNotes, Tablature tab) {
		List<TaggedNote> argTaggedNotes = new ArrayList<>();
		argNotes.forEach(n -> argTaggedNotes.add(new TaggedNote(n)));

		List<List<Note>> argChords = getChordsFromNotes(argNotes);
		List<List<TabSymbol>> argTabChords = tab.getChords();
		int notesPreceding = 0;
		for (int i = 0; i < argTabChords.size(); i++) {
			// If the chord contains a SNU note pair
			Integer[][] SNUInfo = getSNUInfo(argChords.get(i), argTabChords.get(i));			
			if (SNUInfo != null) {
				// For each SNU note pair in the chord (there should only be one)
				int notesRemovedFromChord = 0;
				for (Integer[] in : SNUInfo) {
					// 1. Determine indices
					// a. Indices of the lower and upper SNU note
					int indLower = notesPreceding + (in[1] - notesRemovedFromChord);
					int indUpper = notesPreceding + (in[2] - notesRemovedFromChord);
					// b. Indices of the longer and shorter SNU note
					Rational durLower = argNotes.get(indLower).getMetricDuration();
					Rational durUpper = argNotes.get(indUpper).getMetricDuration();

					// 2. Set argTaggedNotes
					// a. If the SNU notes have the same duration
					int removeInd;
					if (durLower.isEqual(durUpper)) {
//						argVoicesSNU.set(indLower, new Integer[]{
//							findVoice(argNotes.get(indLower)), findVoice(argNotes.get(indUpper)),
//							durLower.getNumer(), durLower.getDenom(), 
//							durUpper.getNumer(), durUpper.getDenom()
//						});
						argTaggedNotes.set(indLower, new TaggedNote(
							argNotes.get(indLower), 
							new Integer[]{findVoice(argNotes.get(indLower)), findVoice(argNotes.get(indUpper))},
							new Rational[]{durLower, durUpper}, -1)
						);
						removeInd = indUpper;
					}
					// b. If the SNU notes have different durations
					else {
						int indLonger = durLower.isGreater(durUpper) ? indLower : indUpper;
						int indShorter = durLower.isGreater(durUpper) ? indUpper : indLower;	
//						argVoicesSNU.set(indLower, new Integer[]{
//							findVoice(argNotes.get(indLonger)), findVoice(argNotes.get(indShorter)),
//							durLonger.getNumer(), durLonger.getDenom(),
//							durShorter.getNumer(), durShorter.getDenom(),
//						});
						argTaggedNotes.set(indLower, new TaggedNote(
							argNotes.get(indLonger), 
							new Integer[]{findVoice(argNotes.get(indLonger)), findVoice(argNotes.get(indShorter))},
							new Rational[]{durLower.max(durUpper), durLower.min(durUpper)}, -1)
						);
						removeInd = indShorter;
					}
					Note removeNote = argNotes.get(removeInd);
					argNotes.remove(removeInd); // TODO not needed (but clearer?)
					argTaggedNotes.remove(indUpper);
					notesRemovedFromChord++;

					if (handledNotes == null) {
						handledNotes = "";
					}
					// TODO Without the below, this method can be static
					handledNotes = handledNotes.concat(
						"SNU found in chord " + i + ": note " + (removeInd - notesPreceding) + 
						" (" + removeNote +	") excluded from list of tagged notes" + "\n");
				}
			}
			notesPreceding += argTabChords.get(i).size();
		}
		return argTaggedNotes;
	}


	/**
	 * Gets information on the single-note unison (SNU) notes in the given chord. 
	 * A SNU occurs when a single Tablature note is shared by two Transcription Notes.
	 *
	 * NB1: This method presumes that a chord contains only one SNU, and neither a course crossing nor a 
	 *      unison.<br>
	 * NB2: Tablature case only. 
	 *
	 * @param argChord
	 * @param argTabChord
	 * @return An Integer[][], each element of which represents a SNU note pair (starting from below 
	 *         in the chord) containing
	 *         <ul>
	 *         <li>As element 0: the pitch (as a MIDInumber) of the SNU notes.</li>
	 *         <li>As element 1: the sequence number in the chord of the lower SNU note.</li>
	 *         <li>As element 2: the sequence number in the chord of the upper SNU note.</li> 
	 *         </ul>
	 *         or <code>null</code> if the chord does not contain any SNUs.   
	 */
	// TESTED
	static Integer[][] getSNUInfo(List<Note> argChord, List<TabSymbol> argTabChord) {
		Integer[][] SNUInfo = null;

		List<Integer> pitchesInChord = getPitchesInChord(argChord);
		int numSNUs = argChord.size() - argTabChord.size();
		if (numSNUs > 0) { 
			SNUInfo = new Integer[numSNUs][3];
			// For each pitch: search the remainder of pitchesInChord for the same pitch 
			// (the upper SNU note) 
			int currRow = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currPitch = pitchesInChord.get(i); 
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					if (pitchesInChord.get(j) == currPitch) {
						SNUInfo[currRow] = new Integer[]{currPitch, i, j};
						currRow++;
						break; 
					}
				} 
			}
		}
		return SNUInfo;
	}


	/** 
	 * Handles course crossings (CCs). Iterates through the given list of <code>TaggedNote</code>s and checks
	 * for course crossing note pairs. If a <code>TaggedNote</code> in the list is part of a course 
	 * crossing note pair, the course crossing note pair is handled at once:
	 * 
	 * <ul>
	 * <li>The <code>TaggedNote</code>s are swapped so that the lower-course (i.e., higher-pitch) course 
	 *     crossing note comes first.</li>
	 * </ul>
	 *  
	 * Adds a summary of the findings to <code>handledNotes</code>.<br><br>
	 *   
	 * NB1: This method presumes that a chord contains only one course crossing, and neither a SNU 
	 *      nor a unison.<br>
	 * NB2: Tablature case only; must be called after handleSNUs().
	 * 	  
	 * @param argTaggedNotes
	 * @param tab
	 */
	// TESTED
	List<TaggedNote> handleCourseCrossings(List<TaggedNote> argTaggedNotes, Tablature tab) {
		List<List<TabSymbol>> tabChords = tab.getChords();
		int notesPreceding = 0;
		Tuning t = tab.getNormaliseTuning() ? tab.getTunings()[Tablature.NORMALISED_TUNING_IND] : 
			tab.getTunings()[Tablature.ENCODED_TUNING_IND];
		for (int i = 0; i < tabChords.size(); i++) {
			List<Integer[]> CCInfo = Tablature.getCourseCrossingInfo(tabChords.get(i), t);
			// If the chord contains a course crossing note pair
			if (CCInfo != null) {
				// For each course crossing note pair in the chord (there should be only one)
				for (Integer[] in : CCInfo) {
					// 1. Determine indices of the lower and upper course crossing note
					int indLower = notesPreceding + in[2];
					int indUpper = notesPreceding + in[3];
					Note noteLower = argTaggedNotes.get(indLower).getNote();
					Note noteUpper = argTaggedNotes.get(indUpper).getNote();

					// 2. Adapt argTaggedNotes
					Collections.swap(argTaggedNotes, indLower, indUpper);

					// TODO Without the below, this method can be static
					if (handledNotes == null) {
						handledNotes = "";
					}
					handledNotes = handledNotes.concat(
						"course crossing found in chord " + i + ": notes " + (indLower - notesPreceding) + 
						" (" + noteLower + ") and " + (indUpper - notesPreceding) + " (" + noteUpper + 
						") swapped in list of tagged notes" + "\n");
				}
			}
			notesPreceding += tabChords.get(i).size();
		}
		return argTaggedNotes;
	}


	/**
	 * Checks whether, after handling of SNUs and course crossings, the <code>Transcription</code> is 
	 * aligned with the given <code>Tablature</code>, i.e., whether
	 * <ol>
	 * <li>They contain the same number of notes.</li>
	 * <li>The notes are in the same order (in <code>notes</code> and in the <code>Tablature</code>'s 
	 * <code>basicTabSymbolProperties</code>).</li>
	 * </ol>
	 * 
	 * Adds a summary of the findings to <code>alignmentCheck</code>.<br><br>     
	 * 
	 * NB: Tablature case only; must be called after handling of SNUs and course crossings.
	 * 
	 * @param argTaggedNotes
	 * @param tab
	 * @return <code>true</code> if both conditions are met; else <code>false</code>.
	 */
	// NOT TESTED
	static boolean checkAlignment(List<TaggedNote> argTaggedNotes, Tablature tab) {
		Integer[][] btp = tab.getBasicTabSymbolProperties();

		// 1. Check equality of number of notes
		if (btp.length != argTaggedNotes.size()) {
			alignmentCheck = btp.length + " notes in Tablature, " + argTaggedNotes.size() + " in Transcription" + "\r\n";		
			return false;
		}

		// 2. Check alignment
		for (int i = 0; i < btp.length; i++) {	
			// Get the pitch and onset time of the TS at index i
			int pitchCurrTS = btp[i][Tablature.PITCH];
			Rational onsetTimeCurrTS = new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			onsetTimeCurrTS.reduce();
			// Get the pitch and onset time of the Note at index i
			Note currNote = argTaggedNotes.get(i).getNote();
			int pitchCurrNote = currNote.getMidiPitch();
			Rational onsetTimeCurrNote = currNote.getMetricTime();
			onsetTimeCurrNote.reduce();
			// Compare
			if (pitchCurrTS != pitchCurrNote || !onsetTimeCurrTS.equals(onsetTimeCurrNote)) {
				if (alignmentCheck == null) {
					alignmentCheck = "";
				}
				alignmentCheck = alignmentCheck.concat("Misalignment found at note index " + i + " (chord index " + 
					btp[i][Tablature.CHORD_SEQ_NUM] + "; " + "sequence number " + 
					btp[i][Tablature.NOTE_SEQ_NUM] + "): pitch and onset time in " + 
					"tablature are " + pitchCurrTS + " and " + onsetTimeCurrTS + "; pitch and onset " +
					"time in transcription are " + pitchCurrNote + " and " + onsetTimeCurrNote + "."  + "\r\n");
				return false;
			}
		}
		return true;
	}


	/**
	 * Handles unisons. Iterates through the given list of unhandled <code>Note</code>s and
	 * checks for unison note pairs. If a <code>Note</code> in the list is not part of a unison 
	 * note pair, it is added to the list of <code>TaggedNote</code>s returned as a simple 
	 * <code>TaggedNote</code>; else, the unison note pair is handled at once:
	 * 
	 * <ul>
	 * <li>The unison notes are added as full <code>TaggedNote</code>s 
	 *     <ul>
	 *     <li>If the unison notes have the same duration: the lower (lower-voice) unison note 
	 *         first.</li>
	 *     <li>If the unison notes have different durations: the unison note that has the longer
	 *         duration first.</li>
	 *     </ul>
	 * </li>
	 * <li>The unison voices are set in the full <code>TaggedNote</code>s
	 *     <ul>
	 *     <li>If the unison notes have the same duration: with the lower voice first.</li>
	 *     <li>If the unison notes have different durations: with the voice that contains 
	 *         the note that has the longer duration first.</li>
	 *     </ul>
	 * </li>         
	 * <li>The unison durations are set in the full <code>TaggedNote</code>s, with the longer 
	 *     duration first.</li>
	 * <li>The index of the respective complementary unison note is set in the full <code>TaggedNote</code>s.</li>
	 * </ul>
	 * 
	 * Adds a summary of the findings to <code>handledNotes</code>.<br><br>
	 * 
	 * NB1: This method presumes that a chord contains only one unison.<br>
	 * NB2: The notes in a unison note pair do not necessarily have successive indices.<br>
	 * NB3: Non-tablature case only.
	 * 
	 * @param argNotes
	 * @return
	 */
	// TESTED
	List<TaggedNote> handleUnisons(List<Note> argNotes) {
		List<TaggedNote> argTaggedNotes = new ArrayList<>();
		argNotes.forEach(n -> argTaggedNotes.add(new TaggedNote(n)));
//		argNotes.forEach(n -> System.out.println(n));
//		System.exit(0);

		List<List<Note>> argChords = getChordsFromNotes(argNotes);
		int notesPreceding = 0;
		for (int i = 0; i < argChords.size(); i++) {
			Integer[][] unisonInfo = getUnisonInfo(argChords.get(i));
			// If the chord contains a unison note pair
			if (unisonInfo != null) {
				// For each unison note pair in the chord (there should be only one)
				for (Integer[] in : unisonInfo) {
					// 1. Determine indices
					// a. Indices of the lower and upper unison note   
					int indLower = notesPreceding + in[1];
					int indUpper = notesPreceding + in[2];
					Note noteLower = argNotes.get(indLower);
					Note noteUpper = argNotes.get(indUpper);
					// b. Indices of the longer and shorter unison note
					Rational durLower = argNotes.get(indLower).getMetricDuration();
					Rational durUpper = argNotes.get(indUpper).getMetricDuration();

					// 2. Set argTaggedNotes
					// a. If the unison notes have the same duration
					if (durLower.isEqual(durUpper)) {
//						argVoicesUnison.set(indLower, new Integer[]{
//							findVoice(argNotes.get(indLower)), findVoice(argNotes.get(indUpper)),
//							indUpper, isEDU
//						});
//						argVoicesUnison.set(indUpper, new Integer[]{
//							findVoice(argNotes.get(indLower)), findVoice(argNotes.get(indUpper)),
//							indLower, isEDU
//						});
						Integer[] argVoicesUnison = 
							new Integer[]{findVoice(argNotes.get(indLower)), 
							findVoice(argNotes.get(indUpper))};
						Rational[] argDurationsUnison = new Rational[]{durLower, durUpper};
						argTaggedNotes.set(indLower, new TaggedNote(
							argNotes.get(indLower), argVoicesUnison, argDurationsUnison, indUpper)
						);
						argTaggedNotes.set(indUpper, new TaggedNote(
							argNotes.get(indUpper), argVoicesUnison, argDurationsUnison, indLower)
						);
						
					}
					// b. If the unison notes have different durations
					else {
						int indLonger = durLower.isGreater(durUpper) ? indLower : indUpper;
						int indShorter = durLower.isGreater(durUpper) ? indUpper : indLower;
//						argVoicesUnison.set(indLower, new Integer[]{
//							findVoice(argNotes.get(indLonger)), findVoice(argNotes.get(indShorter)),
//							indUpper, isEDU
//						});
//						argVoicesUnison.set(indUpper, new Integer[]{
//							findVoice(argNotes.get(indLonger)), findVoice(argNotes.get(indShorter)),
//							indLower, isEDU
//						});
						Integer[] argVoicesUnison = 
							new Integer[]{findVoice(argNotes.get(indLonger)), 
							findVoice(argNotes.get(indShorter))}; 
						Rational[] argDurationsUnison = 
							new Rational[]{durLower.max(durUpper), durLower.min(durUpper)};
						argTaggedNotes.set(indLower, new TaggedNote(
							argNotes.get(indLonger), argVoicesUnison, argDurationsUnison, indUpper));
						argTaggedNotes.set(indUpper, new TaggedNote(
							argNotes.get(indShorter), argVoicesUnison, argDurationsUnison, indLower));
						// TODO not needed (but clearer?)
						if (durLower.isLess(durUpper)) {
							Collections.swap(argNotes, indLower, indUpper);
						}
					}

					if (handledNotes == null) {
						handledNotes = "";
					}
					handledNotes = handledNotes.concat(
						"unison found in chord " + i + ": notes " + (indLower - notesPreceding) + 
						" (" + noteLower +	") and " + (indUpper - notesPreceding) + " (" + noteUpper +
						")" + (durLower.isLess(durUpper) ? " swapped in list of tagged notes" : 
						"in correct order") + "\n");
				}
			}
			notesPreceding += argChords.get(i).size();
		}
		return argTaggedNotes;
	}


	/**
	 * Gets information on the unison notes in the given chord. A unison occurs when two different 
	 * notes in the same chord have the same pitch.  
	 *
	 * NB1: This method presumes that a chord contains only one unison.<br>
	 * NB2: Non-tablature case only.
	 *
	 * @param argChord
	 * @return An Integer[][], each element of which represents a unison note pair (starting from below 
	 *         in the chord) containing
	 *         <ul>
	 *         <li>As element 0: the pitch (as a MIDInumber) of the unison notes.</li>
	 *         <li>As element 1: the sequence number in the chord of the lower unison note.</li>
	 *         <li>As element 2: the sequence number in the chord of the upper unison note.</li>
	 *         </ul>
	 *         or <code>null</code> if the chord does not contain any unisons.
	 */
	// TESTED
	static Integer[][] getUnisonInfo(List<Note> argChord) {
		Integer[][] unisonInfo = null;

		List<Integer> pitchesInChord = getPitchesInChord(argChord);
		List<Integer> uniquePitchesInChord = 
			pitchesInChord.stream().distinct().collect(Collectors.toList());
		int numUnisons = pitchesInChord.size() - uniquePitchesInChord.size();
		if (numUnisons > 0) {
			unisonInfo = new Integer[numUnisons][3];
			// For each pitch: search the remainder of pitchesInChord for the same pitch
			// (the upper unison note) 
			int currRow = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);        
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					if (pitchesInChord.get(j) == currentPitch) {
						unisonInfo[currRow] = new Integer[]{currentPitch, i, j};
						currRow++;
						break;
					}
				} 
			}
		}
		return unisonInfo;
	}


	void setNotes() {
		notes = makeNotes();
	}


	// TESTED
	List<Note> makeNotes() {
		List<Note> argNotes = new ArrayList<>();
		getTaggedNotes().forEach(tn -> argNotes.add(tn.getNote()));
//		System.out.println(argNotes.get(0));
//		System.out.println(argNotes.get(0).getPerformanceNote());
//		System.out.println(argNotes.get(0).getPerformanceNote() instanceof MidiNote);
//		System.exit(0);
		return argNotes;
	}


	void setChords() {
		chords = getChordsFromNotes(getNotes());
	}


	void setVoiceLabels(List<List<Double>> argVoiceLabels, boolean argIsTablatureCase) {
		voiceLabels = 
			argVoiceLabels == null ? makeVoiceLabels(argIsTablatureCase) : argVoiceLabels;
	}


	// TESTED
	List<List<Double>> makeVoiceLabels(boolean argIsTablatureCase) {
		List<List<Double>> argVoiceLabels = new ArrayList<List<Double>>(); 
		getTaggedNotes().forEach(tn -> {
			if (argIsTablatureCase) {
				argVoiceLabels.add(LabelTools.createVoiceLabel(
					tn.getVoices() == null ? new Integer[]{findVoice(tn.getNote())} : tn.getVoices(), 
					MAX_NUM_VOICES)
				);
			}
			else {
				argVoiceLabels.add(LabelTools.createVoiceLabel(
					new Integer[]{findVoice(tn.getNote())}, MAX_NUM_VOICES)
				);
			}
		});
		return argVoiceLabels;
	}


	// TODO delete after TestManager is simplified?
	public void setVoiceLabels(List<List<Double>> argVoiceLabels) {
		voiceLabels = argVoiceLabels;
	}


	void setChordVoiceLabels(Tablature tab) {
		chordVoiceLabels = makeChordVoiceLabels(
			getVoiceLabels(), tab != null ? tab.getChords() : null, getChords()
		);
	}


	// TESTED
	static List<List<List<Double>>> makeChordVoiceLabels(List<List<Double>> argVoiceLabels, 
		List<List<TabSymbol>> argTabChords, List<List<Note>> argChords) {
		List<List<List<Double>>> argChordVoiceLabels = new ArrayList<List<List<Double>>>();
		int lowestNoteInd = 0;
		for (int i = 0; i < (argTabChords != null ? argTabChords.size() : argChords.size()); i++) {
			int currChordSize = 
				argTabChords != null ? argTabChords.get(i).size() : argChords.get(i).size();
			argChordVoiceLabels.add(new ArrayList<List<Double>>(
				argVoiceLabels.subList(lowestNoteInd, lowestNoteInd + currChordSize)));
			lowestNoteInd += currChordSize;
		}
		return argChordVoiceLabels;
	}


	void setDurationLabels(List<List<Double>> argDurLabels) {
		durationLabels = argDurLabels == null ? makeDurationLabels() : argDurLabels;
	}


	// TESTED 
	List<List<Double>> makeDurationLabels() {
		List<List<Double>> argDurLabels = new ArrayList<List<Double>>(); 
		getTaggedNotes().forEach(tn -> 	
			argDurLabels.add(LabelTools.createDurationLabel(
				tn.getDurations() == null ? new Integer[]{TabSymbol.getTabSymbolDur(tn.getNote().getMetricDuration())} :
				new Integer[]{TabSymbol.getTabSymbolDur(tn.getDurations()[0]), 
					TabSymbol.getTabSymbolDur(tn.getDurations()[1])},
				MAX_TABSYMBOL_DUR
//				(Integer[]) Arrays.stream(tn.getDurations()).map(d -> Tablature.getTabSymbolDur(d)).toArray()
		)));
		return argDurLabels;
	}


	void setMinimumDurationLabels(Tablature tab, int maxTabSymDur) {
		minimumDurationLabels = makeMinimumDurationLabels(tab, maxTabSymDur);
	}


	// TESTED
	static List<List<Double>> makeMinimumDurationLabels(Tablature tab, int maxTabSymDur) {
		List<List<Double>> minDurLabels = new ArrayList<>();
		Arrays.stream(tab.getBasicTabSymbolProperties()).forEach(in -> 
			minDurLabels.add(LabelTools.createDurationLabel(new Integer[]{in[Tablature.MIN_DURATION]}, maxTabSymDur)));
//		for (Integer[] in : tab.getBasicTabSymbolProperties()) {
//			minDurLabels.add(createDurationLabel(new Integer[]{in[Tablature.MIN_DURATION]}));
//		}
		return minDurLabels;
	}


	void setVoicesSNU() {
		voicesSNU = makeVoicesSNU();
	}


	// TESTED
	List<Integer[]> makeVoicesSNU() {
		List<Integer[]> argVoicesSNU = new ArrayList<>();
		getTaggedNotes().forEach(tn -> argVoicesSNU.add(tn.getVoices()));
		return argVoicesSNU;
	}


	void setVoicesUnison() {
		voicesUnison = makeVoicesUnison();
	}


	// TESTED
	List<Integer[]> makeVoicesUnison() {
		List<Integer[]> argVoicesUnison = new ArrayList<>();
		getTaggedNotes().forEach(tn -> 
			argVoicesUnison.add(tn.getVoices() == null ? null :
				new Integer[]{tn.getVoices()[0], tn.getVoices()[1], 
				tn.getIndexOtherUnisonNote(), tn.isEDU() ? 1 : 0}));
		return argVoicesUnison;
	}


	void setVoicesEDU() {
		voicesEDU = makeVoicesEDU();
	}


	// TESTED
	List<Integer[]> makeVoicesEDU() {
		List<Integer[]> argVoicesEDU = new ArrayList<>();
		getVoicesUnison().forEach(in ->
			argVoicesEDU.add(in == null ? null : 
			(in[3] == 1 ? Arrays.copyOfRange(in, 0, 3) : null)));
		return argVoicesEDU;
	}


	void setVoicesIDU() {
		voicesIDU = makeVoicesIDU();
	}


	// TESTED
	List<Integer[]> makeVoicesIDU() {
		List<Integer[]> argVoicesIDU = new ArrayList<>();
		getVoicesUnison().forEach(in ->
			argVoicesIDU.add(in == null ? null : 
			(in[3] == 0 ? Arrays.copyOfRange(in, 0, 3) : null)));
		return argVoicesIDU;
	}


	void setBasicNoteProperties() {
		basicNoteProperties = makeBasicNoteProperties();
	}


	// TESTED
	Integer[][] makeBasicNoteProperties() {
		List<Note> argNotes = getNotes();
		List<List<Note>> argChords = getChords();
		Integer[][] argBnp = new Integer[argNotes.size()][NUM_BNP];

		int chordNum = 0;
		Rational mt = new Rational(argNotes.get(0).getMetricTime());
		for (int i = 0; i < argNotes.size(); i++) {
			Note currNote = argNotes.get(i);
			Integer[] bnpCurr = new Integer[8];

			// 0. Pitch
			bnpCurr[PITCH] = currNote.getMidiPitch();
			// 1-2. Metric time
			bnpCurr[ONSET_TIME_NUMER] = currNote.getMetricTime().getNumer();
			bnpCurr[ONSET_TIME_DENOM] = currNote.getMetricTime().getDenom();
			// 3-4. Duration
			bnpCurr[DUR_NUMER] = currNote.getMetricDuration().getNumer();
			bnpCurr[DUR_DENOM] = currNote.getMetricDuration().getDenom();
			// 5. Chord number
			if (currNote.getMetricTime().isGreater(mt)) {
				chordNum += 1;
				mt = currNote.getMetricTime();
			}
			bnpCurr[CHORD_SEQ_NUM] = chordNum;
			// 6. The size of the chord the Note is in
			int size = argChords.get(chordNum).size();
			bnpCurr[CHORD_SIZE_AS_NUM_ONSETS] = size;
			// 7. Sequence number in chord
			bnpCurr[NOTE_SEQ_NUM] = argChords.get(chordNum).indexOf(currNote);

			argBnp[i] = bnpCurr;
		}
		return argBnp;
	}


	void setNumberOfNewNotesPerChord() { 
		numberOfNewNotesPerChord = makeNumberOfNewNotesPerChord();
	}


	// TESTED
	List<Integer> makeNumberOfNewNotesPerChord() { 
		List<Integer> argNumNewNotesPerChord = new ArrayList<Integer>();
		getChords().forEach(ch -> argNumNewNotesPerChord.add(ch.size()));
		return argNumNewNotesPerChord;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public Type getType() {
		return type;
	}


	public ScorePiece getScorePiece() {
		return scorePiece;
	}


	public Piece getUnaugmentedScorePiece() {
		return unaugmentedScorePiece;
	}


	public String getName() {
		return name; 
	}


	/**
	 * Gets the meterInfo.
	 * 
	 * @return A list whose elements represent the meters in the piece. Each element contains<br>
	 *         <ul>
	 *         <li> As element 0: the numerator of the meter.</li>
	 *         <li> As element 1: the denominator of the meter.</li>
	 *         <li> As element 2: the first bar in the meter.</li>
	 *         <li> As element 3: the last bar in the meter.</li>
	 *         <li> As element 4: the numerator of the metric time of that first bar.</li>
	 *         <li> As element 5: the denominator of the metric time of that first bar.</li>
	 *         </ul>
	 *    
	 *         An anacrusis bar would be denoted with bar number 0; however, the current 
	 *         approach is to pre-pad an anacrusis bar with rests, making it a complete bar.
	 */
	public List<Integer[]> getMeterInfo() {
		return meterInfo;
	}


	/**
	 * Gets the <i>keyInfo</i>.
	 * 
	 * @return A list whose elements represent the keys in the piece. Each element contains<br>
	 *   <ul>
	 *   <li> As element 0: the key, as a number of sharps (positive) or flats (negative).</li>
	 *   <li> As element 1: the mode, where major = 0 and minor = 1.</li>
	 *   <li> As element 2: the first bar in the key.</li>
	 *   <li> As element 3: the last bar in the key.</li>
	 *   <li> As element 4: the numerator of the metric time of that first bar.</li>
	 *   <li> As element 5: the denominator of the metric time of that first bar.</li>
	 *   </ul>
	 */
	public List<Integer[]> getKeyInfo() {
		return keyInfo;
	}


	public List<TaggedNote> getTaggedNotes() {
		return taggedNotes;
	}


	public List<Note> getNotes() {
		return notes;
	}


	public List<List<Note>> getChords() {
		return chords == null ? getChordsFromNotes(getNotes()) : chords;
	}


	/**
	 * Gets the voice labels. Each voice label is a one-hot vector containing <code>MAX_NUM_VOICES</code>
	 * elements; the index of the 1.0 indicates the voice encoded (where 0 denotes the highest voice). 
	 * NB: In the case of a SNU, there are two voices encoded (i.e., the vector is a 'two-hot' vector). 
	 * 
	 * @return
	 */
	public List<List<Double>> getVoiceLabels() {
		return voiceLabels;
	}


	public List<List<List<Double>>> getChordVoiceLabels() {
		return chordVoiceLabels;
	}


	/**
	 * Gets the duration labels. Each duration label is a one-hot vector containing <code>MAX_DUR</code>
	 * elements; the index of the 1.0 indicates the full duration (as a TabSymbol duration) encoded 
	 * (where 0 denotes the shortest TabSymbol duration, i.e., that of a semifusa (3)). 
	 * NB: In the case of a SNU, if the SNU note has two durations, there are two durations encoded 
	 * (i.e., the vector is a 'two-hot' vector). 
	 * 
	 * @return
	 */
	public List<List<Double>> getDurationLabels() {
		return durationLabels;
	}


	public List<List<Double>> getMinimumDurationLabels() {
		return minimumDurationLabels;
	}


	/**
	 * Gets, for each SNU note, the voices, organised by the durations that the note has. 
	 *  
	 * @return A list containing for each non-SNU note <code>null</code>, and for each SNU note 
	 *         an Integer[] containing
	 * 	       <ul>
	 *         <li>If the SNU note has two durations
	 *             <ul>
	 *             <li>As element 0: the voice that contains the SNU note having its longer duration.</li>
	 *             <li>As element 1: the voice that contains the SNU note having its shorter duration.</li>
	 *             </ul>
	 *         </li>
	 *         <li>If the SNU note has one duration
	 * 		       <ul>
	 *             <li>As element 0: the lower voice.</li>
	 *             <li>As element 1: the higher voice.</li>
	 *             </ul>
	 *         </li> 
	 *         </ul>
	 */
	public List<Integer[]> getVoicesSNU() {
		return voicesSNU;
	}


	/**
	 * Gets, for each unison note, the voices for it and the complementary unison note, organised 
	 * by the durations that the notes have, as well as the index of the complementary unison note.
	 *   
	 * @return A list containing for each non-unison note <code>null</code>, and for each unison note
	 *         an Integer[] containing
	 *         <ul>
	 *         <li>If the unison note and its complement have different durations
	 *             <ul>
	 *             <li>As element 0: the voice that contains the unison note that has the longer duration.</li>
	 *             <li>As element 1: the voice that contains the unison note that has the shorter duration.</li>
	 *             <li>As element 2: the index of the complementary unison note.</li>
	 *             <li>As element 3: 0 (is not an EDU).</li>
	 *             </ul>
	 *         </li>
	 *         <li>If the unison note and its complement have the same duration
	 * 		       <ul>
	 *             <li>As element 0: the lower voice.</li>
	 *             <li>As element 1: the higher voice.</li>
	 *             <li>As element 2: the index of the complementary unison note.</li>
	 *             <li>As element 3: 1 (is an EDU).</li>
	 *             </ul>
	 *         </li>
	 *         </ul>
	 */
	public List<Integer[]> getVoicesUnison() {
		return voicesUnison;
	}


	/**
	 * Gets, for each equal-duration unison (EDU) note, the voices for it and the complementary 
	 * EDU note, as well as the index of the complementary EDU note.
	 *   
	 * @return A list containing for each non-EDU note <code>null</code>, and for each EDU note
	 *         an <code>Integer[]</code> containing
	 *         <ul>
	 *             <li>As element 0: the lower voice.</li>
	 *             <li>As element 1: the higher voice.</li>
	 *             <li>As element 2: the index of the complementary EDU note.</li>
	 *         </ul>
	 */
	public List<Integer[]> getVoicesEDU() {
		return voicesEDU;
	}


	/**
	 * Gets, for each inequal-duration unison (IDU) note, the voices for it and the complementary 
	 * IDU note, as well as the index of the complementary IDU note.
	 *   
	 * @return A list containing for each non-IDU note <code>null</code>, and for each IDU note
	 *         an <code>Integer[]</code> containing
	 *         <ul>
	 *             <li>As element 0: the voice that contains the IDU note that has the longer duration.</li>
	 *             <li>As element 1: the voice that contains the IDU note that has the shorter duration.</li>
	 *             <li>As element 2: the index of the complementary IDU note.</li>
	 *         </ul>
	 */
	public List<Integer[]> getVoicesIDU() {
		return voicesIDU;
	}


	/**
	 * Gets the <i>basicNoteProperties</i>.
	 *              
	 * NB: Non-tablature case only.
	 * 
	 * @return An Integer[][], each element of which represents a note, and contains 
	 *         <ul>
	 *         <li>As element 0   : the Note's pitch (as a MIDInumber).</li>
	 *         <li>As elements 1-2: the numerator and denominator of the Note's metric time 
	 *                              (both reduced as much as possible).</li>
	 *         <li>As elements 3-4: the numerator and denominator of the Note's metric duration 
	 *                              (both reduced as much as possible).</li>
	 *         <li>As element 5   : the sequence number of the chord the Note is in.</li>
	 *         <li>As element 6   : the size of the chord the Note is in (as new onsets only, so not 
	 *                              including any sustained Notes).</li>
	 *         <li>As element 7   : the Note's sequence number in the chord, not including any sustained 
	 *                              notes. The sequence number is based on pitch only; voice crossing are 
	 *                              left out of consideration. Where two notes have the same pitch, the one 
	 *                              with the longer duration is listed first.</li>
	 * </ul>
	 */
	public Integer[][] getBasicNoteProperties() {
		return basicNoteProperties;
	}


	/**
	 * Gets the number of notes (new onsets only) per chord. 
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @return
	 */
	public List<Integer> getNumberOfNewNotesPerChord() {
		return numberOfNewNotesPerChord;
	}


	public String getChordCheck() { // TODO remove?
		return chordCheck;
	}


	public String getAlignmentCheck() { // TODO remove?
		return alignmentCheck;
	}


	////////////////////////////////
	//
	//  C L A S S  M E T H O D S
	//
	public static void setMaxNumVoices(int arg) {
		MAX_NUM_VOICES = arg;
	}


	/**
	 * Gets the number of notes.
	 * 
	 * @return
	 */
	// TESTED
	public int getNumberOfNotes() {
		return getBasicNoteProperties().length;  
	}


	/**
	 * Gets the number of active voices.
	 * 
	 */
	// TESTED
	public int getNumberOfVoices() {
		NotationSystem system = getScorePiece().getScore();
		int numberOfVoices = system.size();

		// Check how many voices contain no Notes and decrement numberOfVoices accordingly 
		for (int i = 0; i < system.size(); i++) {
			NotationStaff staff = system.get(i);
			NotationVoice voice = staff.get(0);
			if (voice.size() == 0) {
				numberOfVoices--;
			}
		}
		return numberOfVoices;
	}


	// CLEAN UP TO HERE :)


	/**
	 * Returns the meter at the given metric time.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	// TODO make instance method
	public static Rational getMeter(Rational mt, List<Integer[]> meterInfo) {
		for (Integer[] in : meterInfo) {
			Rational curr = new Rational(in[MI_NUM], in[MI_DEN]);
			Rational start = new Rational(in[MI_NUM_MT_FIRST_BAR], in[MI_DEN_MT_FIRST_BAR]);
			int numBarsInMeter = (in[MI_LAST_BAR] - in[MI_FIRST_BAR]) + 1; 
			Rational end = start.add(curr.mul(numBarsInMeter));
			if (mt.isGreaterOrEqual(start) && mt.isLess(end)) {
				return curr;
			}
		}
		return null;
	}


	/**
	 * Returns the meter at the given bar.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	// TODO make instance method
	public static Rational getMeter(int bar, List<Integer[]> meterInfo) {
		for (Integer[] in : meterInfo) {
			if (bar >= in[MI_FIRST_BAR] && bar < in[MI_LAST_BAR]+1) {
				return new Rational(in[MI_NUM], in[MI_DEN]);
			}
		}
		return null;
	}


	/**
	 * Aligns the note indices of the Tablature (represented by the argument <code>btp</code>)
	 * and the Transcription (represented by the argument <code>bnp</code>). Due to the presence
	 * of SNUs, alignment is not always 1-to-1. Two lists are returned:
	 * <ul> 
	 * <li><code>tabToTransInd</code>, containing, for each tab index, the corresponding trans index (or, in 
	 *     the case of a SNU, indices)</li>
	 * <li><code>transToTabInd</code>, containing, for each trans index, the corresponding tab index (wrapped
	 *     in a list)</li>
	 * </ul> 
	 * 
	 * The method takes into account SNUs and course crossings -- but it is assumed that a chord 
	 * with a SNU has no course crossing, and vice versa.
	 * 
	 * @param btp
	 * @param bnp
	 * @return A list containing<br>
	 *         <ul> 
	 *         <li>as element 0: <code>tabToTransInd</code></li>
	 *         <li>as element 1: <code>transToTabInd</code></li>
	 *         </ul> 
	 */
	// TESTED
	public static List<List<List<Integer>>> alignTabAndTransIndices(Integer[][] btp, Integer[][] bnp) {
		List<List<List<Integer>>> res = new ArrayList<>();

		List<List<Integer[]>> pitchesPerChordTab = new ArrayList<>();
		List<Integer[]> chord = new ArrayList<>();
		for (int i = 0; i < btp.length; i++) {
			int pitch = btp[i][Tablature.PITCH];
			chord.add(new Integer[]{i, pitch});
			// Any but last note
			if (i < btp.length-1) {
				if (btp[i+1][Tablature.CHORD_SEQ_NUM] > btp[i][Tablature.CHORD_SEQ_NUM]) {
					pitchesPerChordTab.add(chord);
					chord = new ArrayList<>();
				}
			}
			// Last note
			else {
				pitchesPerChordTab.add(chord);
			}
		}

		List<List<Integer[]>> pitchesPerChordTrans = new ArrayList<>();
		chord = new ArrayList<>();
		for (int i = 0; i < bnp.length; i++) {
			int pitch = bnp[i][PITCH];
			chord.add(new Integer[]{i, pitch});
			// Any but last note
			if (i < bnp.length-1) {
				if (bnp[i+1][CHORD_SEQ_NUM] > bnp[i][CHORD_SEQ_NUM]) {
					pitchesPerChordTrans.add(chord);
					chord = new ArrayList<>();
				}
			}
			// Last note
			else {
				pitchesPerChordTrans.add(chord);
			}
		}

//		for (List<Integer[]> l : pitchesPerChordTab) {
//			System.out.println("-------");
//			l.forEach(in -> System.out.println(Arrays.asList(in)));
//		}
//		System.exit(0);

		// Make tabToTransInd; add indices to list per chord
		List<List<Integer>> tabToTransInd = new ArrayList<>();
		for (int i = 0; i < pitchesPerChordTab.size(); i++) {
			List<Integer[]> currChordTab = pitchesPerChordTab.get(i);
//			List<Integer> indicesTab = ToolBox.getItemsAtIndex(currChordTab, 0);
			List<Integer> pitchesTab = ToolBox.getItemsAtIndex(currChordTab, 1);
			List<Integer[]> currChordTrans = pitchesPerChordTrans.get(i);
			List<Integer> indicesTrans = ToolBox.getItemsAtIndex(currChordTrans, 0);
			List<Integer> pitchesTrans = ToolBox.getItemsAtIndex(currChordTrans, 1);
			
			// No SNU (but possibly unison)
			if (pitchesTab.size() == pitchesTrans.size()) {
				// Find the trans index for each pitch in pitchesTab and add it to the list
				List<Integer> pitchesAlreadyAdded = new ArrayList<>();
				for (int j = 0; j < pitchesTab.size(); j++) {
					int p = pitchesTab.get(j);
					int indOfP = pitchesTrans.indexOf(p);
					// Take into account possible unison; assumes that p occurs only twice
					if (pitchesAlreadyAdded.contains(p)) {
						indOfP = pitchesTrans.lastIndexOf(p);
					}
					tabToTransInd.add(Arrays.asList(new Integer[]{indicesTrans.get(indOfP)}));
					pitchesAlreadyAdded.add(p);
				}
			}
			// SNU (unison assumed not to be possible)
			else {
				// Find all trans indices for each pitch in pitchesTab and add them to the list
				for (int j = 0; j < pitchesTab.size(); j++) {
					int p = pitchesTab.get(j);
					List<Integer> curr = new ArrayList<>();
					for (Integer[] in : currChordTrans) {
						if (in[1] == p) {
							curr.add(in[0]);
						}
					}
					tabToTransInd.add(curr);
				}				
			}
		}

		// Make transToTabInd
		List<List<Integer>> transToTabInd = new ArrayList<>();
		for (int i = 0; i < bnp.length; i++) {
			transToTabInd.add(null);
		}
		for (int i = 0; i < tabToTransInd.size(); i++) {
			List<Integer> transInd = tabToTransInd.get(i);
			for (int ind : transInd) {
				transToTabInd.set(ind, Arrays.asList(new Integer[]{i}));
			}
		}

//		System.out.println("tabToTransInd");
//		for (int j = 0; j < tabToTransInd.size(); j++) {
//			System.out.println(j + " " + tabToTransInd.get(j));
//		}
//		System.out.println("transToTabInd");
//		for (int j = 0; j < transToTabInd.size(); j++) {
//			System.out.println(j + " " + transToTabInd.get(j));
//		}

//		int bnpInd = 0;
//		for (int i = 0; i < btp.length; i++) {
//			System.out.println("i = " + i);
//			List<Integer> currIndInTrans = new ArrayList<>();
//			currIndInTrans.add(bnpInd);
//			int currPitch = btp[i][Tablature.PITCH];
//			Rational currOnset = 
//				new Rational(btp[i][Tablature.ONSET_TIME], 
//				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//			int nextPitch = -1;
//			Rational nextOnset = null;
//			if (i+1 != btp.length) {
//				nextPitch = btp[i+1][Tablature.PITCH];
//				nextOnset = new Rational(btp[i+1][Tablature.ONSET_TIME], 
//					Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//			}
//			System.out.println("currPitch = " + currPitch);
//			System.out.println("currOnset = " + currOnset);
//			System.out.println("nextPitch = " + nextPitch);
//			System.out.println("nextOnset = " + nextOnset);
//			// Check for SNU notes
//			// a. not last tab note case: if not lower unison note (i.e., next tab note has 
//			// the same pitch and onset), check for SNU notes
//			// b. last tab note case: if not last note in bnp (i.e., bnp has one more element 
//			// at bnpInd+1), tab note is a SNU note
//			if ((nextOnset != null && 
//				!(nextOnset.equals(currOnset) && nextPitch == currPitch))
//				|| 
//				(nextOnset == null && bnpInd+1 == (bnp.length-1))) {
//				System.out.println("check for SNU");
//				// If the next MIDI note has the same pitch and onset: SNU
//				Rational nextOnsetMIDI = 
//					new Rational(bnp[bnpInd+1][Transcription.ONSET_TIME_NUMER], 
//					bnp[bnpInd+1][Transcription.ONSET_TIME_DENOM]);
//				if (nextOnsetMIDI.equals(currOnset)) { // && btp[i+1][Tablature.PITCH] != currPitch) {
//					for (int j = bnpInd+1; j < bnp.length; j++) {
//						// If the next note in bnp has the same pitch and onset: SNU
//						if (bnp[j][Transcription.PITCH] == currPitch && 
//							new Rational(bnp[j][Transcription.ONSET_TIME_NUMER],
//							bnp[j][Transcription.ONSET_TIME_DENOM]).equals(currOnset)) {
//							currIndInTrans.add(j);
//							bnpInd++;
//						}
//						else {
//							break;
//						}
//					}
//				}
//			}
//			tabToTransInd.add(currIndInTrans);
//			bnpInd++;
//		}
//		List<List<Integer>> transToTabInd = new ArrayList<>();
//		for (int i = 0; i < tabToTransInd.size(); i++) {
//			List<Integer> transInd = tabToTransInd.get(i);
//			for (int ind : transInd) {
//				transToTabInd.add(ind, i);
//			}
//		}

		res.add(tabToTransInd);
		res.add(transToTabInd);
		return res;
	}


	/**
	 * Gets the pitches in the chord. Element 0 of the List represents the lowest note's pitch, element 1 the
	 * second-lowest note's, etc. Sustained previous notes are NOT included. 
	 * 
	 * NB1: The List will always be in numerical order.
	 * NB2: This method applies only to the non-tablature case
	 * 
	 * @param bnp
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED
	public static List<Integer> getPitchesInChord(Integer[][] bnp, int lowestNoteIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();	
		int chordSize = bnp[lowestNoteIndex][CHORD_SIZE_AS_NUM_ONSETS];
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
			Integer[] currentBasicNoteProperties = bnp[i];
			int currentPitch = currentBasicNoteProperties[PITCH];
			pitchesInChord.add(currentPitch);
		}
		return pitchesInChord;
	}


	/**
	 * Gets the pitches in the given chord. Element 0 of the List represents the lowest note's pitch,
	 * element 1 the second-lowest note's, etc. Sustained notes are not included. 
	 * 
	 * @param chord
	 * @return
	 */
	// TESTED
	public static List<Integer> getPitchesInChord(List<Note> chord) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();
		chord.forEach(n -> pitchesInChord.add(n.getMidiPitch()));
		return pitchesInChord;
	}


	/**
	 * Convert input into a csv String, where each row is a 3D list representing a note (a list),
	 * which consists of voices (each a list or null), which consist of notes (each a list).
	 * 
	 * @param lastNNotesPerVoicePerNote
	 * @return
	 */
	public static String getLastNotesInVoicesString(List<List<List<Rational[]>>> 
		lastNNotesPerVoicePerNote, String meter) {
		StringBuffer testData = new StringBuffer();
		// For each note
		for (int i = 0; i < lastNNotesPerVoicePerNote.size(); i++) {
			testData.append("[");
			List<List<Rational[]>> allSeqsCurrNote = lastNNotesPerVoicePerNote.get(i);
			// For each voice
			for (int j = 0; j < allSeqsCurrNote.size(); j++) {
				if (allSeqsCurrNote.get(j) == null) {
					testData.append("None");
				}
				else {
					testData.append("[");
					List<Rational[]> seqCurrNote = allSeqsCurrNote.get(j);
					// For each note in the voice
					for (int k = 0; k < seqCurrNote.size(); k++) {
						Rational[] note = seqCurrNote.get(k);
						testData.append("[" + 
							note[0].getNumer() + "," + // pitch
							note[1] + "," + // onset
							note[2] + "," + // dur
							note[3] + "," + // metpos
							meter // meter
							);
						testData.append("]");
						if (k < seqCurrNote.size()-1) {
							testData.append(",");
						}
					}
					testData.append("]");
				}
				if (j < allSeqsCurrNote.size()-1) {
					testData.append(",");
				}
			}
			testData.append("]" + "\r\n");
		}
		return testData.toString();
	}


	/**
	 * Using the given voice labels, lists for each voice the notes that belong to that voice. Returns a 
	 * List<List>>, the first element of which corresponds to voice 0, the second to voice 1, etc. 
	 * 
	 * @param voiceLabels
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	public static List<List<Integer>> listNotesPerVoice(List<List<Double>> voiceLabels) {
		List<List<Integer>> notesPerVoice = new ArrayList<List<Integer>>();

		// For each voice
		int maxNumVoices = voiceLabels.get(0).size();
		for (int i = 0; i < maxNumVoices; i++) { // Schmier
//		for (int i = 0; i < MAX_NUM_VOICES; i++) { // Schmier
			int currentVoice = i;
			List<Integer> notesInCurrentVoice = new ArrayList<Integer>();
			// For each note: check whether the note at index j belongs to currentVoice. 
			// If so, add it to notesInCurrentVoice 
			for (int j = 0; j < voiceLabels.size(); j++) {
				List<Double> actualVoiceLabel = voiceLabels.get(j);
				List<Integer> actualVoices = LabelTools.convertIntoListOfVoices(actualVoiceLabel);
				if (actualVoices.contains(currentVoice)) {
					notesInCurrentVoice.add(j);
				}
			}
			notesPerVoice.add(notesInCurrentVoice);
		}		
		return notesPerVoice;
	}


	/**
	 * Verifies that either basicTabSymbolProperties or basicNoteProperties == null (i.e., 
	 * that the non-tablature case or the tablature case applies, respectively).
	 * 
	 * @param btp
	 * @param bnp
	 */
	public static void verifyCase(Integer[][] btp, Integer[][] bnp) {
		if ((btp != null && bnp != null) || (btp == null && bnp == null)) {
			System.out.println("ERROR: if btp == null, bnp must not be, and vice versa" + "\n");
			throw new RuntimeException("ERROR (see console for details)");
		}
	}


	/**
	 * Given a Note with a pitch and a metricTime (its metricDuration is not taken into consideration), determines the
	 * previous or next (depending on the value of the argument direction) Note in the given NotationVoice. There are 
	 * two possibilities: 
	 * 1. If voice contains note, the actual previous Note (or <code>null</code> if there is none) is returned;  
	 * 2. If voice does not contain note, a fictional previous Note, i.e., a Note closest in onset time to note 
	 *    (or <code>null</code> if there is none), is returned.
	 *    
	 * NB: This method presumes that a voice can contain only one Note at each metric time.
	 * 
	 * @param voice
	 * @param note
	 * @param directionIsLeft
	 * @return
	 */
	// TESTED (for both fwd and bwd model)
	public static Note getAdjacentNoteInVoice(NotationVoice voice, Note note, 
		/*Direction direction*/ boolean directionIsLeft) {
		Note adjacentNote = null;

		// List all the Notes in voice
		List<Note> notesInVoice = new ArrayList<Note>();
		int seqNumOfCurrNote = -1;
		for (int i = 0; i < voice.size(); i++) {
			Note currNote = voice.get(i).get(0); 
			notesInVoice.add(currNote);
			if (currNote.getMidiPitch() == note.getMidiPitch() && currNote.getMetricTime().equals(note.getMetricTime())) {
				seqNumOfCurrNote = i;
			}
		}

		// Get the adjacent Note. There are two possibilities:
		// a. If seqNumOfCurrNote is not -1, note is in voice; determine adjacentNote
		// b. If seqNumOfCurrNote is -1, note is not in voice. In this case, a fictional adjacent note, i.e., the note
		// closest in onset time to note, must be returned
		if (directionIsLeft) {
//		if (direction == Direction.LEFT) {
			// a.
			if (seqNumOfCurrNote != -1) {
				// adjacentNote only exists if note is not the first Note in the voice
				if (seqNumOfCurrNote != 0) {
					adjacentNote = notesInVoice.get(seqNumOfCurrNote - 1);
				}
			}
			// b.
			else {
				Note closest = null;
				for (Note n : notesInVoice) {
					if (n.getMetricTime().isLess(note.getMetricTime())) {
						closest = n;
					}
					// Break when the onset time of n becomes equal to or greater than that of note; closest is now the last 
					// smaller onset time
					else {
						break;
					}
				}
				adjacentNote = closest;
			}
		}
		else {
//		else if (direction == Direction.RIGHT) {
			// a.
			if (seqNumOfCurrNote != -1) {
				// adjacentNote only exists if note is not the last Note in the voice
				if (seqNumOfCurrNote != (voice.size() - 1)) {
					adjacentNote = notesInVoice.get(seqNumOfCurrNote + 1);
				}
			}
			// b. 
			else {
				Note closest = null;
				for (Note n : notesInVoice) {
					// Break when the onset time of n becomes greater than that of note; closest is now the first greater 
					// onset time
					if (n.getMetricTime().isGreater(note.getMetricTime())) {
						closest = n;
						break;
					}
				}
				adjacentNote = closest;
			}
		}
		return adjacentNote;
	}


	/**
	 * Gets the indices of all previous notes that are still sounding at the onset time of the
	 * note at noteIndex, i.e., all previous notes whose offset time is greater than the 
	 * onset time of the note at noteIndex.
	 * 
	 * @param btp
	 * @param durationLabels
	 * @param bnp
	 * @param noteIndex
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case) 
	public static List<Integer> getIndicesOfSustainedPreviousNotes(Integer[][] btp, 
		List<List<Double>> durationLabels, Integer[][] bnp, int noteIndex) {

		verifyCase(btp, bnp);
		List<Integer> indicesOfSustainedPreviousNotes = new ArrayList<Integer>();

		// 1. Determine the onset time of the current note and the index of the first note in the chord
		Rational onsetTimeCurrentNote = null;
		int lowestNoteIndex = -1;
		// a. In the tablature case
		if (btp != null) {
			onsetTimeCurrentNote = new Rational(btp[noteIndex][Tablature.ONSET_TIME],
				Tablature.SRV_DEN);
			lowestNoteIndex = noteIndex - btp[noteIndex][Tablature.NOTE_SEQ_NUM];
		}
		// b. In the non-tablature case
		else if (bnp != null) {
			onsetTimeCurrentNote = 
				new Rational(bnp[noteIndex][ONSET_TIME_NUMER], bnp[noteIndex][ONSET_TIME_DENOM]);
			lowestNoteIndex = noteIndex - bnp[noteIndex][NOTE_SEQ_NUM];		
		}

		// 2. For all notes in the previous chord(s): add indices of the notes with an offset
		// time greater than onsetTimeCurrentNote to indicesOfSustainedPreviousNotes
		for (int i = 0; i < lowestNoteIndex; i++) {
			// 1. Determine the metric time and the metric duration of the current previous note
			Rational metricTimeCurrentPreviousNote = null;
			Rational durationCurrentPreviousNote = null;
			// a. In the tablature case
			if (btp != null) {
				// Determine the metric time
				metricTimeCurrentPreviousNote = new Rational(btp[i][Tablature.ONSET_TIME],	
					Tablature.SRV_DEN);
				// Determine the duration. In the case of a CoD, the longer duration of the CoDnote must be considered:
				// if this duration does not cause note overlap, the CoDnote will not cause note overlap at all). In 
				// both cases this is the first element of durationCurrentPreviousNote
				List<Double> durationLabelCurrentPreviousNote = durationLabels.get(i);
				durationCurrentPreviousNote = 
					LabelTools.convertIntoDuration(durationLabelCurrentPreviousNote)[0];
			}
			// b. In the non-tablature case
			else if (bnp != null) {
				// Determine the metric time
				metricTimeCurrentPreviousNote = 
					new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM]);
				// Determine the duration
				durationCurrentPreviousNote = 
					new Rational(bnp[i][DUR_NUMER], bnp[i][DUR_DENOM]);
			}
			// 2. Determine the offset time of the current previous note; add i to indicesOfSustainedPreviousNotes if
			// the offset time is larger than the onset time of the note at noteIndex
			Rational offsetTimeCurrentPreviousNote = metricTimeCurrentPreviousNote.add(durationCurrentPreviousNote);
			if (offsetTimeCurrentPreviousNote.isGreater(onsetTimeCurrentNote)) {
				indicesOfSustainedPreviousNotes.add(i);
			}
		}
		return indicesOfSustainedPreviousNotes;
	}


	/**
	 * Gets the pitches of all previous notes that are still sounding at the onset time of the chord, i.e., all
	 * previous notes whose offset time is greater than the onset time of the note at lowestNoteIndex. These
	 * pitches are listed in the sequence in which the notes are encountered; thus, the List returned is not 
	 * necessarily sorted numerically.
	 *  
	 * @param btp
	 * @param durationLabels 
	 * @param bnp
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	public static List<Integer> getPitchesOfSustainedPreviousNotesInChord(Integer[][] btp, List<List<Double>> durationLabels,
		Integer[][] bnp, int lowestNoteIndex) {

		verifyCase(btp, bnp);

		List<Integer> pitchesOfSustainedPreviousNotes = new ArrayList<Integer>();

		// Get the indices of the sustained previous notes
		List<Integer> indicesOfSustainedPreviousNotes = getIndicesOfSustainedPreviousNotes(btp, durationLabels,
			bnp, lowestNoteIndex);

		// Create the list of pitches
		for (int i : indicesOfSustainedPreviousNotes) {
			// a. In the tablature case
			if (btp != null) {
				pitchesOfSustainedPreviousNotes.add(btp[i][Tablature.PITCH]);
			}
			// b. In the non-tablature case
			if (bnp != null) {
				pitchesOfSustainedPreviousNotes.add(bnp[i][PITCH]);
			}
		}
		return pitchesOfSustainedPreviousNotes;
	}


	/**
	 * Gets the voices of all previous Notes that are still sounding at the onset time of the chord, i.e., all
	 * previous Notes whose offset time is greater than the onset time of the Note at lowestNoteIndex. These
	 * pitches are listed in the sequence in which the Notes are encountered; thus, the List returned is not 
	 * necessarily sorted numerically.
	 * 
	 * @param btp
	 * @param durationLabels
	 * @param voicesCoDNotes
	 * @param bnp
	 * @param allVoiceLabels
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	public static List<Integer> getVoicesOfSustainedPreviousNotesInChord(Integer[][] btp, List<List<Double>> durationLabels,
		List<Integer[]> voicesCoDNotes, Integer[][] bnp, List<List<Double>> allVoiceLabels, int lowestNoteIndex) {
		List<Integer> voicesOfSustainedPreviousNotes = new ArrayList<Integer>();

		List<Integer> indicesOfSustainedPreviousNotes = 
			getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, lowestNoteIndex);

		for (int i : indicesOfSustainedPreviousNotes) {
			List<Double> currentVoiceLabel = allVoiceLabels.get(i);
			List<Integer> currentVoices = LabelTools.convertIntoListOfVoices(currentVoiceLabel);

			// Take into account CoD
			if (currentVoices.size() > 1) {
				Rational[] duration = LabelTools.convertIntoDuration(durationLabels.get(i));
				// If both CoDnotes have the same duration: offset time of both exceeds onset time of chord; add both
				if (duration.length == 1) {
					voicesOfSustainedPreviousNotes.add(currentVoices.get(0));
					voicesOfSustainedPreviousNotes.add(currentVoices.get(1));
				}
				// If both CoDnotes do not have the same duration: check offset times and add only voice(s) for the
				// note(s) whose offset time exceeds onset time of chord
				else {
					// If the offset of the shorter CoDnote (whose voice is listed second in voicesCoDNotes) exceeds the
					// chord's onset, the offset of both notes will; if not, only the offset of the longer CoDnote will
					// Determine the offset of the shorter CoDnote
					Rational shorter = duration[0];
					if (duration[1].isLess(duration[0])) {
						shorter = duration[1];
					}
					Rational onsetShorter = 
						new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
					Rational offsetShorter = onsetShorter.add(shorter);
					Rational onsetCurr = 
						new Rational(btp[lowestNoteIndex][Tablature.ONSET_TIME], Tablature.SRV_DEN);
					// If offsetShorter exceeds the onset of the currentChord: add both voices
					if (offsetShorter.isGreater(onsetCurr)) {
						voicesOfSustainedPreviousNotes.add(currentVoices.get(0));
						voicesOfSustainedPreviousNotes.add(currentVoices.get(1));
					}
					// If not: add only the voice for the longer CoDnote
					else {
						voicesOfSustainedPreviousNotes.add(voicesCoDNotes.get(i)[0]);
					}
				}
			}
			else {
				voicesOfSustainedPreviousNotes.add(currentVoices.get(0));
			}
		}	
		return voicesOfSustainedPreviousNotes;
	}


	/**
	 * Gets any sustained pitches and voices for the current chord and inserts them at the right position into
	 * pitchesInChord and voicesInChord. Returns a List<List<Integer>> the size of the complete chord (so including
	 * any sustained notes), containing
	 *   as element 0: a List of all pitches in the chord
	 *   as element 1: a List of all the corresponding voices 
	 * The List<List>> is ordered according to pitch, so with the lowest note first.
	 * 
	 * @param bnp
	 * @param pitchesInChord
	 * @param voicesInChord
	 * @param allVoiceLabels
	 * @param lowestNoteIndex
	 * @return
	 */
	// TESTED
	// TODO works only for the non-tablature case (and is currently only called in that case)
	static public List<List<Integer>> getAllPitchesAndVoicesInChord(Integer[][] bnp, List<Integer> pitchesInChord, 
		List<List<Integer>>	voicesInChord, List<List<Double>> allVoiceLabels, int lowestNoteIndex ) {

		// 1. For the pitches and voices for the new onsets in the chord
		// a. Unwrap voicesInChord, i.e., turn it into a List<Integer>. This is possible because in the non-tablature
		// case, a note will never contain more than one voice (i.e., there are no CoDs)
		List<Integer> voicesInChordUnwrapped = new ArrayList<Integer>();
		for (List<Integer> l : voicesInChord) {
			voicesInChordUnwrapped.add(l.get(0));
		}
		// b. Combine pitchesInChord and VoicesInChordUnwrapped into pitchesAndVoices. pitchesAndVoices contains as 
		// many elements as there are new onsets in the chord; each element contains the pitch (at position 0) and the
		// voice (as position 1) of each note in the chord. 
		// NB: Both pitchesInChord and VoicesInChordUnwrapped are based on the sequence of the notes as they appear in
		// the NoteSequence; therefore, they are always aligned
		List<List<Integer>> pitchesAndVoices = ToolBox.combineLists(pitchesInChord, voicesInChordUnwrapped);

		// 2. For the sustained pitches and voices
		// a. Get the sustained pitches and voices
		List<Integer> sustainedPitches = 
//			getPitchesOfSustainedPreviousNotesInChordMUSCI(bnp, lowestNoteIndex);
			getPitchesOfSustainedPreviousNotesInChord(null, null, bnp, lowestNoteIndex); // TODO all the nulls work because the method is only called in the non-tablature case
		List<Integer> sustainedVoices = 
//			getVoicesOfSustainedPreviousNotesInChordMUSCI(bnp, allVoiceLabels, lowestNoteIndex);
			getVoicesOfSustainedPreviousNotesInChord(null, null, null, bnp, allVoiceLabels, lowestNoteIndex);	

		// b. Combine sustainedPitches and sustainedVoices into sustainedPitchesAndVoices. sustainedPitchesAndVoices
		// contains as many elements as there are sustained notes in the chord; each element contains the pitch (at 
		// position 0) and the voice (as position 1) of each sustained note. 
		// NB: Both sustainedPitches and sustainedVoices are made with getIndicesOfSustainedPreviousNotes() (which in
		// turn uses the sequence of the notes as they appear in the NoteSequence); therefore, they are always aligned
		List<List<Integer>> sustainedPitchesAndVoices =	ToolBox.combineLists(sustainedPitches, sustainedVoices);

		// 3. Get the pitches and voices for the complete chord and sort them numerically using the pitch as guide
		List<List<Integer>> allPitchesAndVoices = new ArrayList<List<Integer>>(pitchesAndVoices);
		allPitchesAndVoices.addAll(sustainedPitchesAndVoices);
		allPitchesAndVoices = ToolBox.bubbleSort(allPitchesAndVoices, 0);

		// 4. Extract pitchesInChord and voicesInChord from allPitchesAndVoices. 
		List<Integer> pitches = new ArrayList<Integer>();
		List<Integer> voices = new ArrayList<Integer>();
		for (List<Integer> l : allPitchesAndVoices) {
			int currentPitch = l.get(0);
			pitches.add(currentPitch);
			int currentVoice = l.get(1);
			voices.add(currentVoice);
		}
		allPitchesAndVoices = new ArrayList<List<Integer>>();
		allPitchesAndVoices.add(pitches);
		allPitchesAndVoices.add(voices);

		return allPitchesAndVoices;
	}


	/**
	 * Calculates the voice crossing information for the chord represented by the lists of pitches and voices. 
	 * Returns an Integer[] containing
	 * as element 0: the voices involved in voice crossing
	 * as element 1: the voice crossing pairs
	 * as element 2: the pitch distances (in semitones) between the notes that go with each voice crossing pair
	 *
	 * Example 1: given a four-voice/four-note chord with pitches [10, 20, 30, 40] and voices
	 * [2, 1, 0, 3] (both from low to high), the method will return an Integer with: 
	 * as element 0: [2, 3, 1, 0]: all four voices are involved in a voice crossing
	 * as element 1: [2, 3, 1, 3, 0, 3]: there are three voice crossing pairs: 2-3, 1-3, and 0-3 (i.e., the lowest
	 *               voice (3) crosses voices 2, 1, and 0)
	 * as element 2: [30, 20, 10]: the pitch distances between the notes that go with each pair (2-3, 1-3, and 0-3) 
	 
	 * Example 2: given a four-voice/three-note chord (i.e., a chord with a CoD) with pitches [10, 20, 30] and
	 * voices [1, 2, 3/0] (both from low to high), the method will return an Integer with:
	 * as element 0: [1, 2, 3]: voices 1, 2, and 3 are involved in a voice crossing
	 * as element 1: [1, 2, 1, 3, 2, 3]: there are three voice crossing pairs: 1-2, 1-3, and 2-3 
	 * as element 2: [10, 20, 10]: the pitch distances between the notes that go with each pair (1-2, 1-3, and 2-3) 
	 *  
	 * Example 3: given a four-voice/three-note chord (i.e., a chord with a CoD) with pitches [10, 20, 30] and
	 * voices [0/1, 2, 3] (both from low to high), the method will return an Integer with:
	 * as element 0: [0, 2, 3, 1]: all four voices are involved in a voice crossing
	 * as element 1: [0, 2, 0, 3, 1, 2, 1, 3, 2, 3]: there are five voice crossing pairs: 0-2, 0-3, 1-2, 1-3, and 2-3 
	 * as element 2: [10, 20, 10, 20, 10]: the pitch distances between the notes that go with each pair (0-2, 0-3,
	 *               1-2, 1-3, and 2-3)
	 *  
	 * @param pitchesInChord
	 * @param voicesInChord
	 * @return 
	 */ 
	// TESTED (for both tablature- and non-tablature case)
	public static List<List<Integer>> getVoiceCrossingInformationInChord(List<Integer> pitchesInChord, 
		List<List<Integer>> voicesInChord) {

		List<List<Integer>> voiceCrossingInformation = new ArrayList<List<Integer>>();

		// Default values for chords consisting of a single note, when there is no voice crossing possible  
		List<Integer> voicesInvolvedInVoiceCrossing = new ArrayList<Integer>();
		List<Integer> voiceCrossingPairs = new ArrayList<Integer>();
		List<Integer> pitchDistancesOfVoiceCrossingPairs = new ArrayList<Integer>();

		// Determine the size of the chord; if it consists of multiple notes: calculate values
		int chordSize = pitchesInChord.size();
		if (chordSize > 1) {		
			// For each note, get the voice(s) assigned to it and its pitch, and compare them to those of the following
			// notes in the chord. Voice crossings occur when, compared to the current onset, a following note 
			// a) is assigned a voice that is higher, but has a pitch that is lower
			// b) is assigned a voice that is lower, but has a pitch that is higher
			for (int i = 0; i < chordSize; i++) {
				// Get the voice(s) and pitch of the current note
				List<Integer> currentVoices = voicesInChord.get(i);
				int currentPitch = pitchesInChord.get(i);
				// The current note may contain a CoD; check for each of its CoD voices
				for (int j = 0; j < currentVoices.size(); j++) {
					int currentVoice = currentVoices.get(j);
					// Compare the current note's pitch and current voice with those of all the following notes in the chord
					int indexOfNextOnset = i + 1; 
					for (int k = indexOfNextOnset; k < chordSize; k++) {
						List<Integer> currentNextVoices = voicesInChord.get(k);
						int currentNextPitch = pitchesInChord.get(k);
						// The current next note may contain a CoD; check for each of its CoD voices
						for (int l = 0; l < currentNextVoices.size(); l++) {
							int currentNextVoice = currentNextVoices.get(l);
							// If the current next note is assigned a voice that is higher, but it has a pitch that is lower, or 
							// vice versa: increase numberOfVoiceCrossings; add the pitch difference to totalSizeOfVoiceCrossings
							// NB: Since the highest voice has voice number 0, a higher voice implies a lower voice number   
							if ((currentNextVoice < currentVoice && currentNextPitch < currentPitch) || (currentNextVoice > 
								currentVoice && currentNextPitch > currentPitch)) {
								// Add currentVoice and currentNextVoice to voicesInvolvedInVoiceCrossing, but only if they have
								// not been added already
								if (!voicesInvolvedInVoiceCrossing.contains(currentVoice)) {
									voicesInvolvedInVoiceCrossing.add(currentVoice);
								}
								if (!voicesInvolvedInVoiceCrossing.contains(currentNextVoice)) {
									voicesInvolvedInVoiceCrossing.add(currentNextVoice);
								}
								// Add the current pair to voiceCrossingPairs
								voiceCrossingPairs.add(currentVoice);
								voiceCrossingPairs.add(currentNextVoice);
								// Add the pitch distance between the onsets that go with the current pair to pitchDistancesOfVoiceCrossingPairs
								pitchDistancesOfVoiceCrossingPairs.add(Math.abs(currentNextPitch - currentPitch));
							}
						}
					}				
				}
			}
		}
		// Set and return voiceCrossingInformation
		voiceCrossingInformation.add(voicesInvolvedInVoiceCrossing);
		voiceCrossingInformation.add(voiceCrossingPairs);
		voiceCrossingInformation.add(pitchDistancesOfVoiceCrossingPairs);
		return voiceCrossingInformation;
	}


	/**
	 * Augments the <code>Transcription</code>. There are three types of augmentation:
	 * <ul>
	 * <li>Reverse   : reverses the <code>Transcription</code>.</li>
	 * <li>Deornament: removes all sequences of single-note chords shorter than the given
	 *                 threshold duration from the <code>Transcription</code>, and lengthens 
	 *                 the duration of the chord preceding a sequence by the total length 
	 *                 of the removed sequence.</li>
	 * <li>Rescale   : rescales (up or down) the <code>Transcription</code> durationally by the 
	 *                 given rescale factor.</li>
	 * </ul>
	 * 
	 * NB: See also <code>Tablature.augment()</code>.<br><br>
	 * 
	 * @param argEncoding   An augmented <code>Encoding</code>.
	 * @param thresholdDur  Applies only if augmentation is "deornament". The threshold duration; 
	 *                      all single-note chords with a duration shorter than this duration are 
	 *                      considered ornamental and are removed.
	 * @param rescaleFactor Applies only if augmentation is "rescale". A positive value doubles
	 *                      all durations (e.g., 4/4 becomes 4/2); a negative value halves them 
	 *                      (4/4 becomes 4/8).
	 * @param augmentation  One of "reverse", "deornament", or "rescale".
	 */
	// NOT TESTED (wrapper method)
	public void augment(Encoding argEncoding, Rational thresholdDur, int rescaleFactor, String augmentation) {
		ScorePiece sp = getScorePiece();
		sp.augment(getMirrorPoint(), getChords(), getMetricPositionsChords(), thresholdDur, 
			rescaleFactor, augmentation);
		this.init(sp, argEncoding, null, null, false, null, Type.FROM_FILE);
	}


	/**
	 * Gets the <code>meterInfo</code> at the given metric time.
	 * 
	 * @param mt
	 * @return
	 */
	// TESTED
	public Integer[] getLocalMeterInfo(Rational mt) {
		List<Integer[]> mi = getMeterInfo();

		Integer[] currMi = mi.get(mi.size()-1);
		if (mi.size() > 1) {
			for (int i = 0; i < mi.size() - 1; i++) {
				Rational start = new Rational(
					mi.get(i)[MI_NUM_MT_FIRST_BAR], mi.get(i)[MI_DEN_MT_FIRST_BAR]
				);
				Rational end = new Rational(
					mi.get(i+1)[MI_NUM_MT_FIRST_BAR], mi.get(i+1)[MI_DEN_MT_FIRST_BAR]
				);
				if (mt.isGreaterOrEqual(start) && mt.isLess(end)) {
					currMi = mi.get(i);
					break;
				}
			}	
		}

		return currMi;
	}


	/**
	 * Gets the <code>keyInfo</code> at the given metric time.
	 * 
	 * @param mt
	 * @return
	 */
	// TESTED
	public Integer[] getLocalKeyInfo(Rational mt) {
		List<Integer[]> ki = getKeyInfo();

		Integer[] currKi = ki.get(ki.size()-1);
		if (ki.size() > 1) {
			for (int i = 0; i < ki.size() - 1; i++) {
				Rational start = new Rational(
					ki.get(i)[KI_NUM_MT_FIRST_BAR], ki.get(i)[KI_DEN_MT_FIRST_BAR]
				);
				Rational end = new Rational(
					ki.get(i+1)[KI_NUM_MT_FIRST_BAR], ki.get(i+1)[KI_DEN_MT_FIRST_BAR]
				);
				if (mt.isGreaterOrEqual(start) && mt.isLess(end)) {
					currKi = ki.get(i);
					break;
				}
			}	
		}

		return currKi;
	}


	/**
	 * Gets the mirrorPoint, i.e., the start time of the fictional bar after the last bar, which is 
	 * needed for reversing the piece. If the piece has an anacrusis, the last bar will be a full 
	 * bar (i.e., the original, shortened last bar to which the length of the anacrusis is added).
	 * 
	 * @return
	 */
	// TESTED
	public Rational getMirrorPoint() {
		Rational mirrorPoint = Rational.ZERO;

		List<Integer[]> mi = getMeterInfo();

		ScoreMetricalTimeLine smtl = getScorePiece().getScoreMetricalTimeLine();
//		MetricalTimeLine mtl = getScorePiece().getMetricalTimeLine();
		// For each voice
		for (NotationStaff ns : getScorePiece().getScore()) {	
			for (NotationVoice nv : ns) {
				Rational currMirrorPoint = Rational.ZERO;
				// 1. Determine the onset- and offset time of the last note in the voice
				NotationChord lastNc = nv.get(nv.size() - 1);
				Rational onsetTimeLastNote = lastNc.getMetricTime();
				Rational offsetTimeLastNote = Rational.ZERO;
				for (Note n : lastNc) {
					Rational offsetTime = n.getMetricTime().add(n.getMetricDuration());
					if (offsetTime.isGreater(offsetTimeLastNote)) {
						offsetTimeLastNote = offsetTime;	
					}
				}

				// 2. Get the metric position of the onset and offset of the last note and determine
				// the onset- and offset bar(s) as well as the onset meter 
				// NB: The offset time of the last note must be reduced to ensure that it falls with the bar 
				// (if it coincides with the final barline, offsetTimeLastNote will not have a metric position)
				Rational[] metPosOnset = 
					smtl.getMetricPosition(onsetTimeLastNote);
//					ScoreMetricalTimeLine.getMetricPosition(mtl, onsetTimeLastNote);
//					Utils.getMetricPosition(onsetTimeLastNote, mi);
				Rational reduction = new Rational(1, 128);
				Rational[] metPosOffset = 
					smtl.getMetricPosition(offsetTimeLastNote.sub(reduction));	
//					ScoreMetricalTimeLine.getMetricPosition(mtl, offsetTimeLastNote.sub(reduction));	
//					Utils.getMetricPosition(offsetTimeLastNote.sub(reduction), mi);
				// Onset- and offset bar(s)
				int barNumOnset = metPosOnset[0].getNumer();
				int barNumOffset = metPosOffset[0].getNumer();
				// Onset bar meter
				Rational meterOnset = null;
				for (Integer[] currMeter : mi) {
					if (barNumOnset >= currMeter[MI_FIRST_BAR] && barNumOnset <= currMeter[MI_LAST_BAR]) {
						meterOnset = new Rational(currMeter[MI_NUM], currMeter[MI_DEN]);
					}
				}

				// 3. Determine currMirrorPoint
				// Determine the distance of the note's onset to the beginning of the (possibly fictional) next bar 
				Rational distanceFromOnsetToNextBar = meterOnset.sub(metPosOnset[1]);
				// If the note's onset and offset are not in the same bar: determine the total duration of all 
				// succeeding bars	
				Rational durSucceedingBars = Rational.ZERO;
				if (barNumOffset > barNumOnset) {     	
					for (int i = barNumOnset + 1; i <= barNumOffset; i++) {
						for (Integer[] currMeter : mi) {
							if (i >= currMeter[MI_FIRST_BAR] && i <= currMeter[MI_LAST_BAR]) { 
								durSucceedingBars = 
									durSucceedingBars.add(new Rational(currMeter[MI_NUM], currMeter[MI_DEN]));
							}
						}
					}
				}
				currMirrorPoint = 
					onsetTimeLastNote.add(distanceFromOnsetToNextBar).add(durSucceedingBars);
				if (currMirrorPoint.isGreater(mirrorPoint)) {
					mirrorPoint = currMirrorPoint;
				}
			}
		}
		return mirrorPoint;
	}


	// TODO delete: only used in tab-as-non-tab case
	public void transposeNonTab(int transpositionInterval) {
		transpose(transpositionInterval);
		// Redo the part in createTranscription() from setBasicNoteProperties() on. 
		// setNumberOfNewNotesPerChord() and setChordVoiceLabels() need not be called again after
		// setTranscriptionChordsFinal(), as there are no pitches involved in these methods
		chords = null;
		setBasicNoteProperties();
		setChords();	
	}


	/**
	 * Transposes the given transcription by the given interval (in semitones).
	 * 
	 * NB: Tablature case only.
	 * 
	 * @param transposition
	 */
	// TESTED TODO delete: see transposeNonTab()
	void transpose(int transposition) {
		// 1. Transpose all the notes in noteSequence and reset noteSequence
		List<Note> notes = getNotes();
//		NoteSequence noteSeq = getNoteSequence();
		MetricalTimeLine mtl = getScorePiece().getMetricalTimeLine();
		for (int i = 0; i < notes.size(); i++) {
//		for (int i = 0; i < noteSeq.size(); i++) {
			Note originalNote = notes.get(i);
//			Note originalNote = noteSeq.getNoteAt(i);
			Note transposedNote = ScorePiece.createNote(originalNote.getMidiPitch() + transposition,
				originalNote.getMetricTime(), originalNote.getMetricDuration(), originalNote.getVelocity(), 
				mtl);
			notes.set(i, transposedNote);
//			noteSeq.replaceNoteAt(i, transposedNote);
		}
//		setNoteSequence(noteSeq);
		
		// 2. Transpose piece
		NotationSystem system = getScorePiece().getScore(); 
		for (int i = 0; i < system.size(); i++) {
			NotationStaff staff = system.get(i);
			NotationVoice voice = staff.get(0);  
			for (int j = 0; j < voice.size(); j++) {
				NotationChord notationChord = voice.get(j);
				for (int k = 0; k < notationChord.size(); k++) {
					Note originalNote = notationChord.get(k);
					Note transposedNote = ScorePiece.createNote(originalNote.getMidiPitch() + transposition,
						originalNote.getMetricTime(), originalNote.getMetricDuration(), 
						originalNote.getVelocity(), mtl);
					notationChord.remove(originalNote);
					notationChord.add(transposedNote);   	    
				}
			}
		}

		// 3. Transpose HarmonyTrack
		SortedContainer<Marker> keySigs = getScorePiece().getHarmonyTrack();
		for (int i = 0; i < keySigs.size(); i++) {
			KeyMarker km = (KeyMarker) keySigs.get(i);
			km.setAlterationNum(ScorePiece.transposeNumAccidentals(transposition, km.getAlterationNum()));
		}

//		// 3. In the non-tablature case
//		if (!isTablatureCase) {
//			transcriptionChordsFinal = null;
//			setBasicNoteProperties();
//			setTranscriptionChordsFinal();
//			// setNumberOfNewNotesPerChord() and setChordVoiceLabels() need not be
//			// called again after setTranscriptionChordsFinal() (as in (see createTranscription)), as there
//			// are no pitches involved in these methods
//		}
	}


	/**
	 * Gets the basicNoteProperties of the chord at the given index in the Transcription, i.e., the elements of 
	 * <i>basicNoteProperties</i> corresponding to the Notes within that chord. 
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @param chordIndex 
	 * @return
	 */
	// TESTED
	public Integer[][] getBasicNotePropertiesChord(int chordIndex) {

		// Determine the size of the chord at chordIndex and the index of the lowest note in it
		int chordSize = 0;
		int lowestNoteIndex = 0;
		for (int i = 0; i < basicNoteProperties.length; i++) {
			if (basicNoteProperties[i][CHORD_SEQ_NUM] == chordIndex) {
				lowestNoteIndex = i;
				chordSize = basicNoteProperties[i][CHORD_SIZE_AS_NUM_ONSETS];
				break;
			}
		}

		// Make basicNotePropertiesChord
		Integer[][] basicNotePropertiesChord = 
			Arrays.copyOfRange(basicNoteProperties, lowestNoteIndex, lowestNoteIndex + chordSize);    
		return basicNotePropertiesChord;
	}
	
	
	// VEEH :)

	public List<List<Integer>> determineVoiceEntriesHIGHLEVEL(Integer[][] btp, 
		List<List<Double>> durationLabels, Integer[][] bnp, int numVoices, int n) {
			
		verifyCase(btp, bnp);
			
		List<Integer> noteDensities = getNoteDensity(btp, durationLabels, bnp); // in 2020
		int leftDensity = noteDensities.get(0);

		// Find density increases
		List<Integer> densities = new ArrayList<Integer>();
		densities.add(leftDensity);
		// If the piece does not start with a fully-textured chord
		if (leftDensity < numVoices) {
			for (int i = 0; i < noteDensities.size(); i++) {
				int density = noteDensities.get(i);
				if (density > leftDensity) {
					densities.add(density);
					leftDensity = density;
					if (density == numVoices) {
						break;
					}
				}	
			}
//			System.out.println(noteDensities.subList(0, 50));
//			for (List<Double> l : durationLabels) {
//				System.out.println(l.size() + " - " + l);
//			}

			System.out.println("densities = " + densities);
			// If the voices enter successively: determine if the piece is imitative
			if (densities.size() == numVoices) {
				// Check whether there are enough notes of density 1 to contain a motif of n notes
				boolean enoughNotes = true;
				for (int i = 0; i < n; i++) {
					if (noteDensities.get(i) > 1) { // in 2020
//					if (getNoteDensity().get(i) > 1) { // in 2020	
						enoughNotes = false;
						System.out.println("not enough notes of density 1 for motif");
						break;
					}
				}
				// If so: check whether a motif is found (i.e., whether configurations does
				// not contain only -1s)
				if (enoughNotes) {
					List<List<Integer>> ve = 
						getImitativeVoiceEntries(btp, durationLabels, bnp, numVoices, n); // in 2020
//					List<Integer> configurations = ve.get(0);
//					if ((ToolBox.sumListInteger(configurations) != -configurations.size())) {
//						return ve;
//					}
					if (ve != null) {
						System.out.println("motif found");
						System.out.println("IMITATIVE MANNE.");
						return ve;
					}
					else {
//						System.out.println("IMITATIVE FAILED AT " + pieceName);
						System.out.println("no motif found");
						System.out.println("NON-IMITATIVE MANNE.");
						return getNonImitativeVoiceEntries(btp, durationLabels, bnp, numVoices, n); // in  2020
					}
				}
				else {
					System.out.println("NON-IMITATIVE MANNE.");
					return getNonImitativeVoiceEntries(btp, durationLabels, bnp, numVoices, n); // in 2020
				}
			}
			// If the voices do not enter successively: the piece is non-imitative
			else {
				System.out.println("NON-IMITATIVE MANNE.");
				return getNonImitativeVoiceEntries(btp, durationLabels, bnp, numVoices, n); // in 2020
			}
		}
		// If the piece starts with a fully-textured chord // TODO account for unisons
		else {
			System.out.println("NON-IMITATIVE MANNE.");
			List<List<Integer>> res = new ArrayList<List<Integer>>();
			List<Integer> indices = new ArrayList<Integer>();
			List<Integer> voices = new ArrayList<Integer>();
			for (int i = 0; i < numVoices; i++) {
				indices.add(i);
				voices.add(numVoices - (i+1));
			}
			res.add(null);
			res.add(indices);
			res.add(voices);
			return res;
		}
	}


	/**
	 * Gets, for each note, the number of notes that are sounding at that time (including 
	 * sustained notes, if applicable).
	 * 
	 * @btp
	 * @param durationLabels 
	 * @bnp	
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	public List<Integer> getNoteDensity(Integer[][] btp, List<List<Double>> durationLabels, 
		Integer[][] bnp) { // in 2020
		verifyCase(btp, bnp);
		
		List<Integer> activeNotes = new ArrayList<Integer>();
		
//		Integer bnp[][] = getBasicNoteProperties(); // in 2020
		Rational[][] tpm = getTimePitchMatrix(btp, durationLabels, bnp); // in 2020
		for (int i = 0; i < tpm.length; i++) {
			Rational onsetCurr = tpm[i][0];
			int active = 1;
			// Previous notes
			int numSusNotes = getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, i).size();
			for (int j = i-1; j >= 0; j--) {
				Rational onsetPrev = (tpm[j][0]);
				Rational offsetPrev = (tpm[j][1]);

				// Because of the decrementing j, onsetPrev can only be equal to onsetCurr
				// (lower chord note) or less than onsetCurr (previous note)
				// a. Lower chord note
				if (onsetPrev.isEqual(onsetCurr)) {
//					System.out.println("lower chord note counted");
					active++;
				}
				// b. Previous note (possibly sustained) 
				else if (onsetPrev.isLess(onsetCurr)) {
					// Only if there are still sustained previous notes
					if (numSusNotes > 0) {
						if (offsetPrev.isGreater(onsetCurr)) {
							active++;
							numSusNotes--;
//							System.out.println("sustained note counted");
						}
					}
					else {
						break;
					}	
				}
			}
			// Next notes
			for (int j = i+1; j < ((btp != null) ? btp.length : bnp.length); j++) {
				Rational onsetNext = (tpm[j][0]);
				// Because of the incrementing j, onsetNext can only be equal to onsetCurr
				// (higher chord note) or greater than onsetCurr (next note)
				// a. Higher chord note
				if (onsetNext.isEqual(onsetCurr)) {
					active++;
//					System.out.println("higher chord note counted");
				}
				// b. Next note (is dealt with at a next i)
				else {
					break;
				}	
			}
			activeNotes.add(active);
		}	
		return activeNotes;
	}


	/**
	 * Returns a matrix containing onset, offset, and pitch for each note in btp or bnp, 
	 * 
	 * @param btp
	 * @param durationLabels
	 * @param bnp
	 * @return
	 */
	// TESTED (for both tablature and non-tablature case)
	public static Rational[][] getTimePitchMatrix(Integer[][] btp, List<List<Double>> durationLabels,
		Integer[][] bnp) { // in 2020
//		Integer[][] bnp = getBasicNoteProperties(); // in 2020
		verifyCase(btp, bnp);

		Rational[][] tpm = new Rational[btp != null ? btp.length : bnp.length][3];
		if (btp != null) {
			for (int i = 0; i < btp.length; i++) {
				int pitchCurr = btp[i][0];
				Rational onsetCurr = new Rational(btp[i][3], Tablature.SRV_DEN);
//				Rational offsetCurr = onsetCurr.add(
//					new Rational(btp[i][4], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()));
				Rational offsetCurr = 
					onsetCurr.add(LabelTools.convertIntoDuration(durationLabels.get(i))[0]);
				tpm[i] = new Rational[]{onsetCurr, offsetCurr, new Rational(pitchCurr, 1)};
			}
		}
		if (bnp != null) {		
			for (int i = 0; i < bnp.length; i++) {
				int pitchCurr = bnp[i][0];
				Rational onsetCurr = new Rational(bnp[i][1], bnp[i][2]);
				Rational offsetCurr = onsetCurr.add(new Rational(bnp[i][3], bnp[i][4]));
				tpm[i] = new Rational[]{onsetCurr, offsetCurr, new Rational(pitchCurr, 1)};
			}
		}
		return tpm;
	}


	/**
	 * Determines the sequence of voice entries based primarily on the rhythmic profile,
	 * and secondarily on the melodic profile, of the first n notes of the fugue theme (the
	 * head motif). This is accomplished by locating the vertical position of that head motif
	 * - with which the newly entering voice starts - at each texture density increase (i.e.,
	 * from 1-2vv, from 2-3vv, etc.). 
	 * 
	 * The following assumptions are made. When a new voice enters for the first time, during
	 * the length of the head motif </br>
	 * (1) the head motif is rhythmically clearly distinct from the other active voices </br> 
	 * (2) all previous voices are active (i.e., do not have rests) </br>
	 *     (exceptions: BWV 872, all densities; BWV 863, density 4; BWV 871, density 4; 
	 *     BWV 886, density 4) </br> 
	 * (3) the head motif is not involved in voice crossings 
	 *     (exception: BWV 890, density 3) </br>
	 * (4) the rhythmic profile of the head motif is repeated literally 
	 *     (exception: BWV 881) </br> 
	 * 
	 * Exceptions to (1) and (2) are handled as follows.</br>
	 * (1) The head motif is doubled rhythmically in another voice, meaning that there may
	 *     be multiple motif candidates. In this case, the melodic profile (e.g., 'up, up') 
	 *     of the original head motif is used to identify the correct candidate. Doubling 
	 *     may occur:</br>
	 *     (a) in oblique or contrary motion (example: BWV 846, density 3, bar 4 1/8). In 
	 *         this case, the doubling resembles the head motif only rhythmically but not
	 *         melodically, and is therefore eliminated as candidate for the head motif.</br> 
	 *     (b) in parallel motion (example: BWV 859, density 4, bar 15 1/4). In this case,  
	 *         the doubling resembles the head motif both rhythmically and melodically, and
	 *         is therefore a candidate for the head motif. The chord (i.e., its pitches)
	 *         in which the head motif starts is compared to the previous chord, and the 
	 *         newly entering voice is determined based on the optimal connection of the 
	 *         pitches in both chords, where voices are assumed to continue in as small as 
	 *         possible steps.
	 *         Example: left chord = [67, 60], right chord = [67, 59, 55]. Cost per
	 *         connection = 0+1 to connect to upper two layers; 0+5 to connect to upper and
	 *         lower layer; 8+5 to connect to lower two layers. Connection 1 is the lowest-cost
	 *         and therefore optimal connection.</br>
	 * (2) Not all previous voices are active. The following scenarios are possible:</br>
	 *     (a) The new density takes effect during the new head motif:
	 *         (1) If the first chord in the new density does not contain the first head 
	 *         motif note (HMN) duration: a small time window to the left of this initial
	 *         first chord is searched for the chord sequence that does contain the HMN 
	 *         durations (example: BWV 863, density 4, bar 7 3/8) 
	 *         NB: this may yield a false candidate, to be removed </br>
	 *         (2) If the first chord in the new density by coincidence contains the first 
	 *         head motif note duration: a false cadidate, to be removed, is detected
	 *         (example: BWV 872, density 2, 3, bars 1, 2) </br> 
	 *     (b) The new density takes effect after the new head motif. This leads to:</br>
	 *         (1) a false candidate (i.e., n notes with the same durations as the HMN, but 
	 *             with different pitch movement), to be removed, being determined (example: 
	 *             BWV 871, density 4, bar 19 1/4 (missed entry at bar 7 1/8)).</br>
	 *         (2) a next actual entry of the head motif being determined (example: BWV 886, 
	 *             density 4, bar 22 1/8 (missed entry at bar 8 1/8)).</br>   
	 *     
	 *     When a false candidate is detected, the newly entering voice is simply added as 
	 *     the lowest of the already active voices; in case (b2) the newly entering voice is
	 *     derived from the next actual entry.
	 * 
	 * @param highestNumVoices
	 * @param n	The number of notes of the head motif to consider
	 * @return A list of lists, containing </br>
	 *         as element 0: the determined voice entry configurations, with -1 as a placeholder
	 *                       when the configuration could not be determined at that density 
	 *                       increase</br>
	 *         as element 1: for each density increase, the indices of the note(s) in the 
	 *                       first chord at that density increase </br>
	 *         as element 2: the voices that go with these indices </br>
	 *         Returns <code>null</code> if no motif was found.
	 */
	// TESTED TODO: implement full note duration case in tablature case
	List<List<Integer>> getImitativeVoiceEntries(Integer[][] btp, List<List<Double>> durationLabels, 
		Integer[][] bnp, int highestNumVoices, int n) { // in 2020

		verifyCase(btp, bnp);
		
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		List<Integer> configs = new ArrayList<Integer>();
		final int pitch = 0;
		final int durNum = 1;
		final int durDen = 2;
		final int isHMN = 3;
		
//		Integer[][] bnp = getBasicNoteProperties(); // in 2020

		List<Integer> lowestNoteIndicesFirstChords = new ArrayList<Integer>();
		lowestNoteIndicesFirstChords.add(0);
		List<List<Integer[]>> pitchesFirstChords = new ArrayList<List<Integer[]>>();
		List<Integer[]> firstPitch = new ArrayList<Integer[]>();
		firstPitch.add(new Integer[]{
			(btp != null) ? btp[0][Tablature.PITCH] : 
			bnp[0][PITCH], 0});
		pitchesFirstChords.add(firstPitch);

		// Determine the rhythmic head motif (first n notes of fugue theme)
		List<Rational> headMotif = new ArrayList<Rational>();
		Rational granularity = new Rational(1, 32);
		for (int i = 0; i < n; i++) {
			Rational curr = 
				(btp != null) ?	LabelTools.convertIntoDuration(durationLabels.get(i))[0] :
				new Rational(bnp[i][DUR_NUMER], bnp[i][DUR_DENOM]);
			headMotif.add(quantiseDuration(curr, granularity));
		}

		// Determine the inter-onset intervals, exact pitch movement, and general pitch 
		// movement of the head motif
		List<Rational> ioiHeadMotif = new ArrayList<Rational>();
		List<Integer> pitchMvmtHeadMotif = new ArrayList<Integer>();
		List<Double> mvmtHeadMotif = new ArrayList<Double>();
		for (int i = 0; i < n-1; i++) {
			int pitchMvmt;
			if (btp != null) {
				ioiHeadMotif.add(
					new Rational(btp[i+1][Tablature.ONSET_TIME], Tablature.SRV_DEN).sub(
					new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN)));
				pitchMvmt = btp[i+1][Tablature.PITCH] - btp[i][Tablature.PITCH];
			}
			else {
				ioiHeadMotif.add(
					new Rational(bnp[i+1][ONSET_TIME_NUMER], bnp[i+1][ONSET_TIME_DENOM]).sub(
					new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM])));
				pitchMvmt = bnp[i+1][PITCH] - bnp[i][PITCH];
			}
			pitchMvmtHeadMotif.add(pitchMvmt);
			mvmtHeadMotif.add(Math.signum((double)pitchMvmt));
		}

		// Find the next density increase and determine the position of the newly
		// entering voice
		List<Integer> noteDensities = getNoteDensity(btp, durationLabels, bnp); // in 2020
		int density = noteDensities.get(0);
		for (int i = 0; i < noteDensities.size(); i++) {
			if (noteDensities.get(i) > density) {
				System.out.println("density increase");
				System.out.println( 
					((btp != null) ? "onset time " + new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN) :
					"bar " + TimeMeterTools.getMetricPositionAsString(getMetricPositionsNotes().get(i))));
				System.out.println("new density = " + noteDensities.get(i));
				density = noteDensities.get(i);
				int config = -1;

				// Check whether the chord at i contains the first HMN
				boolean firstHMNInFirstChord = false;
				int chordSz = 
					((btp != null) ? btp[i][Tablature.CHORD_SIZE_AS_NUM_ONSETS] : 
					bnp[i][CHORD_SIZE_AS_NUM_ONSETS]);
				for (int j = i; j < i + chordSz ; j++) {
					Rational curr =
						(btp != null) ? LabelTools.convertIntoDuration(durationLabels.get(j))[0] :	
						new Rational(bnp[j][DUR_NUMER], bnp[j][DUR_DENOM]);
					curr = quantiseDuration(curr, granularity);
					if (curr.equals(headMotif.get(0))) {
						firstHMNInFirstChord = true;
						break;
					}
				}
				System.out.println("firstHMNInFirstChord = " + firstHMNInFirstChord);
				
				// If not: find the i that goes with the first sequence of chords to the left 
				// that contain the HMNs. Stop if no motif candidate has been found after the 
				// maximum time window has been covered, or when there are no more notes left
				if (!firstHMNInFirstChord) {
					Rational onsetOfInitialI = 
						(btp != null) ? new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN) :	
						new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM]);
					Rational timeWindowCovered = Rational.ZERO;
					Rational maxTimeWindow = new Rational(1, 4);
					boolean solved = false;
					while (!solved) {
						int newI = -1;
						Rational onsetOfNewI = null;
						// Find the new i, i.e., the lowest note index of the next left chord 
						// with the first HMN 
						for (int j = i-1; j >= 0; j--) {
							Rational curr = 
								(btp != null) ? LabelTools.convertIntoDuration(durationLabels.get(j))[0] : 
								new Rational(bnp[j][DUR_NUMER], bnp[j][DUR_DENOM]);
							curr = quantiseDuration(curr, granularity);
							if (curr.equals(headMotif.get(0))) {
								int chordInd = 
									(btp != null) ? btp[j][Tablature.CHORD_SEQ_NUM] :
									bnp[j][CHORD_SEQ_NUM];
								onsetOfNewI = 
									(btp != null) ? new Rational(btp[j][Tablature.ONSET_TIME], Tablature.SRV_DEN) :
									new Rational(bnp[j][ONSET_TIME_NUMER], bnp[j][ONSET_TIME_DENOM]);
								// Find the index of the lowest note in the chord at chordInd
								for (int k = j; k >=0; k--) {
									int chordSeqNr = 
										(btp != null) ? btp[k][Tablature.CHORD_SEQ_NUM] : 
										bnp[k][CHORD_SEQ_NUM]; 
									if (chordSeqNr == chordInd-1) {
										newI = k+1;
										break;
									}
								}	
								break;
							}
						}
						
						// If there are no more indices to try
						if (newI == -1) {
							return null;
						}

						Rational shift = onsetOfInitialI.sub(onsetOfNewI);
						timeWindowCovered = timeWindowCovered.add(shift);
						onsetOfInitialI = onsetOfNewI;

						// Check if HMN occurs from newI
						boolean nextFound = true;				
						int ind = 1;
						List<Boolean> hits = new ArrayList<Boolean>();
						Rational onsetOfChordAtNewI = 
							(btp != null) ? new Rational(btp[newI][Tablature.ONSET_TIME], Tablature.SRV_DEN) : 
							new Rational(bnp[newI][ONSET_TIME_NUMER], bnp[newI][ONSET_TIME_DENOM]);
						Rational nextOns = onsetOfChordAtNewI.add(ioiHeadMotif.get(ind - 1));
						System.out.println("nextOns = " + nextOns);
						for (int j = newI; j < ((btp != null) ? btp.length : bnp.length); j++) {
							Rational currOns = 
								(btp != null) ? new Rational(btp[j][Tablature.ONSET_TIME], Tablature.SRV_DEN) :
								new Rational(bnp[j][ONSET_TIME_NUMER], bnp[j][ONSET_TIME_DENOM]);
							if (currOns.equals(nextOns)) {
								Rational currDur = 
									(btp != null) ? LabelTools.convertIntoDuration(durationLabels.get(j))[0] :
									new Rational(bnp[j][DUR_NUMER], bnp[j][DUR_DENOM]);
								currDur = quantiseDuration(currDur, granularity);
								if (currDur.equals(headMotif.get(ind))) {
									hits.add(true);
								}
								else {
									hits.add(false);
								}
							}
							else if (currOns.isGreater(nextOns)) {
								if (!hits.contains(true)) { 
									nextFound = false;
									break;
								}
								else {
									ind++;
									if (ind == headMotif.size()) {
										break;
									}
									else {
										nextOns = nextOns.add(ioiHeadMotif.get(ind - 1));
										hits.clear(); 
										j--;
									}	
								}
							}
						}					
						i = newI;

						if (nextFound || timeWindowCovered.isGreater(maxTimeWindow)) {							
							solved = true;
						}
					}
				}

				// Determine the position of the newly entering voice
				// 1. HMN: onsets and lowest note indices
				// a. Determine the onsets of the HMNs
				List<Rational> onsetsOfHMNChords = new ArrayList<Rational>();	
				onsetsOfHMNChords.add(
					(btp != null) ? new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN) :	
					new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM]));
				for (int j = 0; j < ioiHeadMotif.size(); j++) { 
					Rational lastAdded = onsetsOfHMNChords.get(j);
					Rational toAdd = lastAdded.add(ioiHeadMotif.get(j));
					onsetsOfHMNChords.add(toAdd);
				}

				// b. Determine the indices of the lowest chord note at those onsets
				List<Integer> indOfMotifNotesChords = new ArrayList<Integer>();
				for (int j = i; j < ((btp != null) ? btp.length : bnp.length); j++) {
					Rational currOnset = 
						(btp != null) ?	new Rational(btp[j][Tablature.ONSET_TIME], Tablature.SRV_DEN) :
						new Rational(bnp[j][ONSET_TIME_NUMER], bnp[j][ONSET_TIME_DENOM]);	
					if (onsetsOfHMNChords.contains(currOnset)) {
						indOfMotifNotesChords.add(j);
						int toAdd = 
							(btp != null) ? btp[j][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
							bnp[j][CHORD_SIZE_AS_NUM_ONSETS];	
						j += toAdd - 1;
					}
					// Break if currOnset is after the last HMN onset
					if (currOnset.isGreater(onsetsOfHMNChords.get(n-1))) {
						break;
					}		
				}

				// 2. Make the skeleton, i.e., the chords at the onsets of the HMNs
				// a. Determine the raw skeleton
				List<List<List<Integer>>> skeleton = new ArrayList<List<List<Integer>>>();
				for (int j = 0; j < indOfMotifNotesChords.size(); j++) {					
					int ind = indOfMotifNotesChords.get(j);
					List<List<Integer>> noteInfo = getChordInfo(btp, durationLabels, bnp, ind);
					// For each note: add 1 if it has the same duration as the current HMN
					Rational currHMNDur = headMotif.get(j);
					for (List<Integer> l : noteInfo) {
						Rational currDur = null;
						if (l.get(1) != null) {
							currDur = new Rational(l.get(1), l.get(2));
							currDur = quantiseDuration(currDur, granularity);		
						}
						if (currDur != null && currDur.equals(currHMNDur)) {
							l.add(1);
						}
						else {
							l.add(0);
						}
					}					
					skeleton.add(noteInfo);
				}
				System.out.println("onsetsOfHMNChords = " + onsetsOfHMNChords);
				System.out.println("indOfMotifNotesChords = " + indOfMotifNotesChords);
				System.out.println("SKELETON");
				for (List<List<Integer>> l : skeleton) {
					System.out.println(l);
				}

				// b. Ensure that all chords in the skeleton have a size that is equal 
				// to the current density by patching smaller chords with null values
				List<List<List<Integer>>> completes = new ArrayList<List<List<Integer>>>();
				List<List<List<Integer>>> incompletes = new ArrayList<List<List<Integer>>>();
				List<Integer> indOfIncompletes = new ArrayList<Integer>();

				// Determine completes and incompletes
				for (int j = 0; j < skeleton.size(); j++) {
					List<List<Integer>> curr = skeleton.get(j);
					if (curr.size() < density) {
						incompletes.add(curr);
						indOfIncompletes.add(j);
					}
					else {
						completes.add(curr);
					}
				}
				// Patch incompletes with null values
				if (incompletes.size() != 0) {
					for (int j = 0; j < incompletes.size(); j++) {	
						List<List<Integer>> currIncomplete = incompletes.get(j);
						int currIncompleteInd = indOfIncompletes.get(j);
						List<List<Integer>> completed = new ArrayList<List<Integer>>();
						// If it is the first chord of the skeleton that is incomplete (which
						// is the case if i was set back): patch the chord by finding the 
						// optimal position to insert null
						if (currIncomplete == skeleton.get(0)) {
							currIncompleteInd = 0;
							// Get pitches in previous chord
							List<Integer> pitchesInPrev = new ArrayList<Integer>();
							int sizePrev = 
								(btp != null) ? btp[i-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :	
								bnp[i-1][CHORD_SIZE_AS_NUM_ONSETS];
							int lowestIndPrev = i - sizePrev;
							// Add pitches of all chord notes
							for (int k = lowestIndPrev; k < lowestIndPrev + sizePrev; k++) {
								pitchesInPrev.add(
									(btp != null) ? btp[k][Tablature.PITCH] :	
									bnp[k][PITCH]);
							}
							// Add pitches of any sustained previous notes
							for (int ind : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, lowestIndPrev)) {
								pitchesInPrev.add(
									(btp != null) ? btp[ind][Tablature.PITCH] :	
									bnp[ind][PITCH]);
							}
							Collections.sort(pitchesInPrev);

							// Get pitches in current chord
							List<Integer> pitchesInCurr = new ArrayList<Integer>();
							for (List<Integer> l : currIncomplete) {
								pitchesInCurr.add(l.get(pitch));
							}

							// Align optimally: place null at each position of pitchesInCurr
							// and determine lowest-cost alignment with pitchesInPrev
							int minCost = Integer.MAX_VALUE;
							int optInd = -1;
							for (int k = 0; k < density; k++) {
								List<Integer> curr = new ArrayList<Integer>(pitchesInCurr);
								curr.add(k, null);
								int currCost = 0;
								for (int l = 0; l < pitchesInPrev.size(); l++) {
									if (curr.get(l) != null) {
										currCost += Math.abs(curr.get(l) - pitchesInPrev.get(l));
									}
								}
								if (currCost < minCost) {
									minCost = currCost;
									optInd = k; 
								}
							}
							currIncomplete.add(optInd, null);
							completed = new ArrayList<List<Integer>>(currIncomplete);
						}
						// If it is not the first chord of the skeleton: patch the chord
						// using the average pitches of the previos chord(s)
						else {
							for (int k = 0; k < density; k++) {
								completed.add(null);
							}
							// Get the average pitches of all completes before incomplete
							// in skeleton
							List<Double> avgs = new ArrayList<Double>();
							for (int k = 0; k < density; k++) {
								double sum = 0; 
								int div = 0;
								for (List<List<Integer>> l : completes) {
									if (skeleton.indexOf(l) < skeleton.indexOf(currIncomplete)) {
										sum += l.get(k).get(pitch);
										div++;
									}
								}
								avgs.add(sum/div);
							}
							currIncompleteInd = indOfIncompletes.get(j);
							// For each element in currIncomplete: compare with the average
							// pitches to determine its optimal index in completed.
							// List the pitch differences with the average pitches
							List<List<Double>> allDiffs = new ArrayList<List<Double>>();
							for (List<Integer> element : currIncomplete) {
								List<Double> diffs = new ArrayList<Double>();
								for (int k = 0; k < avgs.size(); k++) {
									diffs.add(Math.abs(element.get(pitch) - avgs.get(k)));
								}
								allDiffs.add(diffs);
							}
							// Determine the optimal index in completed 
							for (int k = 0; k < allDiffs.size(); k++) {
								List<Double> currDiff = allDiffs.get(k);
								double currMin = Collections.min(currDiff);
								int currMinInd = currDiff.indexOf(currMin);
								// Compare with other diffs to determine whether currMin is the
								// lowest value at currMinInd
								boolean restart = false;
								for (int l = 0; l < allDiffs.size(); l++) {
									if (l != k) {
										List<Double> otherDiff = allDiffs.get(l);
										// If otherDiff has a lower value at currMinInd, and  
										// that value is the lowest in that diff: make the 
										// second-lowest value in currDiff the lowest restart
										double d = otherDiff.get(currMinInd);
										if (d < currMin && Collections.min(otherDiff) == d) {
											currDiff.set(currMinInd, Double.MAX_VALUE);
											allDiffs.set(k, currDiff);
											k--;
											restart = true;
											break;
										}
									}
								}
								// If not: set currMinInd in completed to the element in
								// currIncomplete that corresponds to currDiff 
								if (!restart) {
									completed.set(currMinInd, currIncomplete.get(k));
								}
							}
						}
						// Replace incomplete element in skeleton
						skeleton.set(currIncompleteInd, completed);					
					}
					System.out.println("SKELETON PATCHED");
					for (List<List<Integer>> l : skeleton) {
						System.out.println(l);
					}
				}

				// c. Handle correct vertical placement of unison notes in the skeleton
				// List unison notes per chord
				List<List<List<Integer>>> unisonsToHandlePerChord = new ArrayList<List<List<Integer>>>();
				for (List<List<Integer>> l : skeleton) {
					List<List<Integer>> unisonsToHandle = new ArrayList<List<Integer>>();
					List<Integer> pitchesCovered = new ArrayList<Integer>();
					// For each note in the HMN chord
					for (int j = 0; j < l.size(); j++) {
						List<Integer> currNote = l.get(j);
						if (currNote != null) {
							int currPitch = currNote.get(pitch);
							// Compare currNote to all other notes in l
							for (int k = 0; k < l.size(); k++) {
								if (k != j) {
									List<Integer> otherNote = l.get(k);
									if (otherNote != null) {
										int otherPitch = otherNote.get(pitch);
										// Same pitch? Add both notes to the list
										if (otherPitch == currPitch &&
											!pitchesCovered.contains(currPitch)) {
											unisonsToHandle.add(currNote);
											unisonsToHandle.add(otherNote);
											pitchesCovered.add(currPitch);
										}
									}
								}	
							}
						}
					}
					unisonsToHandlePerChord.add(unisonsToHandle);
				}
				// Handle unison notes (i) of different durations, (ii) one of which is 
				// a HMN duration.
				// Ad (i): if the unison notes have the same duration: do nothing. In this 
				// case, the unison pitches will either be both [p, num, den, 0] or both 
				// [p, num, den, 1]. A combination of the two will never occur: a pitch with
				// a certain duration either has the same duration as a head motif note or not.
				// Ad (ii): if none of the durations is the HMN duration: do nothing. In 
				// this case, correctness of the sequence of the unison notes does not
				// matter, as this does not affect the position in the chord of the HMN
				// (which will always be below or above the unison notes). 
				// NB: This approach assumes that the two consecutive chords are fully 
				// textured (which is only very rarely not true)
				for (int j = 0; j < unisonsToHandlePerChord.size(); j++) {
					List<List<Integer>> currUnisons = unisonsToHandlePerChord.get(j);
					List<List<Integer>> currHMNChord = skeleton.get(j);
					int lowestIndCurr = indOfMotifNotesChords.get(j);

					// Find the previous and next non-unison neighbour chord 
					List<List<Integer>> prevChord = 
						getNonUnisonNeighbourChord(btp, durationLabels, bnp, -1, lowestIndCurr);
					List<List<Integer>> nextChord = 
						getNonUnisonNeighbourChord(btp, durationLabels, bnp, 1, lowestIndCurr);

					// For each unison note pair satisfying criteria (i) and (ii) above 
					// (the pairs are always at consecutive indices) 
					for (int k = 0; k < currUnisons.size(); k++) {
						List<Integer> left = currUnisons.get(k);
						List<Integer> right = currUnisons.get(k+1);
						int currPitch = left.get(pitch);
						// Criterion (i): the unison notes must have the same duration 
						// (in which case they are the same)
						// NB: quantisation, which is only necessary to locate the HMNs 
						// (see above) is not necessary here
						if (!left.equals(right)) { 
							boolean leftIsHMN = (left.get(left.size()-1) == 1);
							boolean rightIsHMN = (right.get(right.size()-1) == 1);
							boolean leftIsNull = (left.get(durNum) == null);
							Rational leftDur = null;
							if (!leftIsNull) {
								leftDur = new Rational(left.get(durNum), left.get(durDen));
							}
							boolean rightIsNull = (right.get(durNum) == null);
							Rational rightDur = null;
							if (!rightIsNull) {
								rightDur = new Rational(right.get(durNum), right.get(durDen));
							}
							Rational HMNDur, otherDur; 
							int indOfHMN, indOfOther;
							// Criterion (ii): one of the unison durations must be the 
							// HMN duration
							if (leftIsHMN || rightIsHMN) {
								// (a) If the other duration is a non-null duration	 
								if (leftIsHMN && !rightIsNull || rightIsHMN && !leftIsNull) {
									if (leftIsHMN) {
										HMNDur = leftDur;
										otherDur = rightDur;
										indOfHMN = currHMNChord.indexOf(left);
										indOfOther = currHMNChord.indexOf(right);
									}
									else {
										HMNDur = rightDur;
										otherDur = leftDur;
										indOfHMN = currHMNChord.indexOf(right);
										indOfOther = currHMNChord.indexOf(left);
									}
									// Check the next chord for the note with the same 
									// pitch and a null duration
									for (int l = 0; l < nextChord.size(); l++) {
										List<Integer> nextChordNote = nextChord.get(l);
										if (nextChordNote.get(0) == currPitch &&
											nextChordNote.get(1) == null) {
											// If otherDur is longer than HMNdur: the position
											// of the found note determines the position of  
											// the longer-duration note
											if (otherDur.isGreater(HMNDur)) {
												if (indOfOther != l) {
													Collections.swap(currHMNChord, 
														indOfOther, indOfHMN);
												}
											}
											// If otherDur is shorter than HMNdur: the position
											// of the found note determines the position of
											// the head motif note
											else if (otherDur.isLess(HMNDur)) {
												if (indOfHMN != l) {
													Collections.swap(currHMNChord, 
														indOfOther, indOfHMN);
												}
											}
											skeleton.set(j, currHMNChord);
											break;
										}
									}
								}
								// (b) If the other duration is a null duration
								else if (leftIsHMN && rightIsNull || rightIsHMN && leftIsNull) {
									otherDur = null;
									if (leftIsHMN) {
										HMNDur = leftDur;
										indOfHMN = currHMNChord.indexOf(left);
										indOfOther = currHMNChord.indexOf(right);
									}
									else {
										HMNDur = rightDur;
										indOfHMN = currHMNChord.indexOf(right);
										indOfOther = currHMNChord.indexOf(left);
									}
									// Check the previous chord for the note with the same 
									// pitch (and either a null or a non-null duration); 
									// the position of the found note determines the 
									// position of the null-duration note
									for (int l = 0; l < prevChord.size(); l++) {
										List<Integer> prevChordNote = prevChord.get(l);
										if (prevChordNote.get(0) == currPitch) {
											if (indOfOther != l) {
												Collections.swap(currHMNChord, 
													indOfOther, indOfHMN);
											}
											break;
										}
									}
									skeleton.set(j, currHMNChord);
								}
							}
						}
						// Increment k to go to next pair
						k++;
					}
				}
				System.out.println("SKELETON PATCHED AND UNISONS FIXED");
				for (List<List<Integer>> l : skeleton) {
					System.out.println(l);
				}

				// 3. Determine the vertical position of the head motif in the skeleton
				// Determine all head motif candidates
				List<List<Integer>> motifCandidates = new ArrayList<List<Integer>>(); 
				for (int j = 0; j < density; j++) {
					int sum = 0;
					List<Integer> motifCandidate = new ArrayList<Integer>();
					for (List<List<Integer>> l : skeleton) {
						List<Integer> element = l.get(j); 
						if (element != null) {
							sum += element.get(isHMN);
							motifCandidate.add(element.get(pitch));
						}
					}
					if (sum == n) {
						motifCandidates.add(motifCandidate);
					}
					else {
						motifCandidates.add(null);
					}
				}
				System.out.println("motifCandidates = " + motifCandidates);

				// Filter out any false head motif candidates, i.e., candidates that
				// have the same rhythmic profile but are in contrary or oblique motion
				List<List<Integer>> filtered = new ArrayList<List<Integer>>();
				for (int j = 0; j < motifCandidates.size(); j++) {
					List<Integer> motifCandidate = motifCandidates.get(j);
					if (motifCandidate != null) {
						List<Integer> pitchMvmtMotifCand = new ArrayList<Integer>(); 
						List<Double> mvmtMotifCand = new ArrayList<Double>();
						for (int k = 0; k < motifCandidate.size()-1; k++) {
							int pitchMvmt = motifCandidate.get(k+1) - motifCandidate.get(k);
							pitchMvmtMotifCand.add(pitchMvmt);
							mvmtMotifCand.add(Math.signum((double)pitchMvmt));
						}
						// Confirm motif if: (i) it has exactly the same pitch movement
						// as pitchMvmtHeadMotif; (ii) it has the same general movement 
						// (up/down) as pitchMvmtHeadMotif. (i) will suffice in most cases;
						// if not, small difference in pitch movement necessitated by the 
						// harmony (minor vs major interval, fourth versus fifth) will be
						// caught by (ii).  
						// In some cases, however, it can happen that a correct motif 
						// candidate still does not satisfy criterion (ii) (see e.g. BWV 
						// 876, density 2 and 4). This is solved by allowing some leeway
						// in criterion (ii) by creating mvmtMotifCand.size() variants of
						// mvmtMotifCand, in each of which one element is replaced by 0.0.
						// (meaning that, to a certain extent, no movement (0.0) is 
						// considered equal to up/down (1.0/-1.0) movement). All variants
						// thus created are considered correct as well
						List<List<Double>> leewayCandidates = new ArrayList<List<Double>>();
						for (int k = 0; k < mvmtHeadMotif.size(); k++) {
							List<Double> leewayCand = new ArrayList<Double>(mvmtHeadMotif); 
							leewayCand.set(k, 0.0);
							leewayCandidates.add(leewayCand);
						}
						if (pitchMvmtMotifCand.equals(pitchMvmtHeadMotif) ||
							mvmtMotifCand.equals(mvmtHeadMotif) ||
							leewayCandidates.contains(mvmtMotifCand)) {
							filtered.add(motifCandidate);
						}
						else {
							filtered.add(null);
						}
					}
					else {
						filtered.add(null);
					}
				}
				motifCandidates = filtered;
				
				System.out.println("motiveCandidates filtered = " + motifCandidates);

				int numMotifCand = motifCandidates.size() - 
					Collections.frequency(motifCandidates, null);
				
				System.out.println("numMotifCand = " + numMotifCand);

				// 4. Determine the configuration 
				// The configuration equals the index (including sustained notes) of the 
				// HMN in the first HMN chord. Possibilities:
				// (0)   (1)   (2)   (3) 
				// x x     x  
				//   x   x x
				//   
				// x x   x x     x   
				// x x     x   x x
				//   x)   x x   x x
				//   
				// x x   x x   x x     x
				// x x   x x     x   x x
				// x x     x   x x   x x
				//   x   x x   x x   x x
				//
				// If there is only one motif candidate
				if (numMotifCand == 1) {
					for (int j = 0; j < motifCandidates.size(); j++) {
						if (motifCandidates.get(j) != null) {
							config = j;
							break;
						}
					}
				}
				// If there are multiple motif candidates (which will be in parallel 
				// motion): determine which one is in the newly inserted voice
				else if (numMotifCand > 1) {
					System.out.println("multiple motif candidates for " + name);
					// List all m left chords (i.e., those with the previous density), 
					// each as a list of pitches, and make average chord 
					List<List<Integer>> leftChords = new ArrayList<List<Integer>>();	
					List<Double> avgLeftChord = new ArrayList<Double>();
					for (int j = 0; j < density; j++) {
						avgLeftChord.add(null);
					}
					// Given the number of left chords to consider, find the lowest note
					// index of the leftmost chord
					int chordInd = 
						(btp != null) ? btp[i][Tablature.CHORD_SEQ_NUM] : 
						bnp[i][CHORD_SEQ_NUM];
					int m = 1; // TODO make method argument?
					int leftChordIndex = chordInd - m;
					int leftInd = i;
					int rightInd = i;
					while (chordInd > leftChordIndex) {
						int toSub = 
							(btp != null) ? btp[rightInd-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :	
							bnp[rightInd-1][CHORD_SIZE_AS_NUM_ONSETS];
						leftInd = rightInd - toSub;
						rightInd = leftInd;
						chordInd = 
							(btp != null) ? btp[leftInd][Tablature.CHORD_SEQ_NUM] :
							bnp[leftInd][CHORD_SEQ_NUM];
					}
					// List the left chords and prepare avgLeftChord
					for (int j = leftInd; j < i; j++) {
						int chordSize = 
							(btp != null) ? btp[j][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
							bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
						List<Integer> pitchesInChord = new ArrayList<Integer>();
						// Add pitches of all chord notes
						for (int k = j; k < j + chordSize; k++) {
							pitchesInChord.add(
								(btp != null) ? btp[k][Tablature.PITCH] :
								bnp[k][PITCH]);
						}
						// Add pitches of any sustained previous notes
						for (int ind : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, j)) {
							pitchesInChord.add(
								(btp != null) ? btp[ind][Tablature.PITCH] :
								bnp[ind][PITCH]);
						}
						Collections.sort(pitchesInChord);
						Collections.reverse(pitchesInChord);
						if (pitchesInChord.size() == density-1) {
							// Add placeholder
							pitchesInChord.add(null);
							leftChords.add(pitchesInChord);
							// Add to avgs
							for (int k = 0; k < pitchesInChord.size(); k++) {
								if (pitchesInChord.get(k) != null) { 
									double summedPitch = pitchesInChord.get(k);
									// avgLeftChord will only contain a non-null value 
									// for the second and higher chords
									if (j != leftInd) {
										summedPitch += avgLeftChord.get(k);
									}
									avgLeftChord.set(k, summedPitch);
								}
							}
						}
						// Increment j so that the next iteration starts from the next chord
						j = (j + chordSize) - 1;
					}
					// Calculate avgLeftChord
					for (int j = 0; j < avgLeftChord.size(); j++) {
						if (avgLeftChord.get(j) != null) {
							avgLeftChord.set(j, (avgLeftChord.get(j) / leftChords.size()));
						}
					}
					System.out.println("leftChords.size() = " + leftChords.size());
					System.out.println("avgLeftChord = " + avgLeftChord);

					// List the right chord
					List<Integer> rightChord = new ArrayList<Integer>();
					for (List<Integer> l : skeleton.get(0)) {
						rightChord.add(l.get(pitch));
					}
					Collections.reverse(rightChord);
					System.out.println("rightChord = " + rightChord);

					// Determine the optimal configuration
					// For each configuration: sum the movement over all voices that are 
					// not null in left chord. The number of configurations equals the 
					// number of notes in the right chord, i.e., the density
					List<Double> costPerConfigCurrTransition = new ArrayList<Double>();
					int currConfig = 0;
					while (currConfig < density) {
						double costCurrConfig = 0;
						for (int j = 0; j < avgLeftChord.size(); j++) {
							if (avgLeftChord.get(j) != null) {
								costCurrConfig += 
									Math.abs(rightChord.get(j) - avgLeftChord.get(j));
							}
						}
						costPerConfigCurrTransition.add(costCurrConfig);

						// Not last configuration? Shift placeholder one position back 
						// and do next config
						if (currConfig != density - 1) {
							int swapInd = avgLeftChord.indexOf(null);
							Collections.swap(avgLeftChord, swapInd, swapInd-1);
						}
						currConfig++;
					}
					double min = Collections.min(costPerConfigCurrTransition);
					config = costPerConfigCurrTransition.indexOf(min);
				}
				System.out.println("config = " + config);

				// NB: If no config was set, the value of config remains -1
				configs.add(config);
				lowestNoteIndicesFirstChords.add(i);
				List<Integer[]> pitchesInFirstChord = new ArrayList<Integer[]>();
				for (List<Integer> l : skeleton.get(0)) {
					// Ternary operator; see https://alvinalexander.com/java/edu/pj/pj010018
					if (l != null) {
						pitchesInFirstChord.add(new Integer[]{l.get(pitch), 
							(l.get(durNum) == null) ? 1 : 0});
					}
				}
				pitchesFirstChords.add(pitchesInFirstChord);

				if (density == highestNumVoices) {
					break;
				}
			}
		}
		System.out.println("lowestNoteIndicesFirstChords = " + lowestNoteIndicesFirstChords);

		// Determine the sequence of voice entries
		// Map all possible combinations of configurations to voicings
		Map<List<Integer>, List<Double>> dict = new LinkedHashMap<List<Integer>, List<Double>>();
		// 2vv
		dict.put(Arrays.asList(new Integer[]{0}), Arrays.asList(new Double[]{0.0, 1.0})); // SB
		dict.put(Arrays.asList(new Integer[]{1}), Arrays.asList(new Double[]{1.0, 0.0})); // BS
		// 3vv
		dict.put(Arrays.asList(new Integer[]{0, 0}), Arrays.asList(new Double[]{0.0, 1.0, 2.0})); // SAB
		dict.put(Arrays.asList(new Integer[]{0, 1}), Arrays.asList(new Double[]{0.0, 2.0, 1.0})); // SBA
		dict.put(Arrays.asList(new Integer[]{0, 2}), Arrays.asList(new Double[]{1.0, 2.0, 0.0})); // ABS
		dict.put(Arrays.asList(new Integer[]{1, 0}), Arrays.asList(new Double[]{1.0, 0.0, 2.0})); // ASB
		dict.put(Arrays.asList(new Integer[]{1, 1}), Arrays.asList(new Double[]{2.0, 0.0, 1.0})); // BSA
		dict.put(Arrays.asList(new Integer[]{1, 2}), Arrays.asList(new Double[]{2.0, 1.0, 0.0})); // BAS
		// 4vv
		dict.put(Arrays.asList(new Integer[]{0, 0, 0}), Arrays.asList(new Double[]{0.0, 1.0, 2.0, 3.0})); // SATB
		dict.put(Arrays.asList(new Integer[]{0, 0, 1}), Arrays.asList(new Double[]{0.0, 1.0, 3.0, 2.0})); // SABT
		dict.put(Arrays.asList(new Integer[]{0, 0, 2}), Arrays.asList(new Double[]{0.0, 2.0, 3.0, 1.0})); // STBA
		dict.put(Arrays.asList(new Integer[]{0, 0, 3}), Arrays.asList(new Double[]{1.0, 2.0, 3.0, 0.0})); // ATBS
		//
		dict.put(Arrays.asList(new Integer[]{0, 1, 0}), Arrays.asList(new Double[]{0.0, 2.0, 1.0, 3.0})); // STAB
		dict.put(Arrays.asList(new Integer[]{0, 1, 1}), Arrays.asList(new Double[]{0.0, 3.0, 1.0, 2.0})); // SBAT
		dict.put(Arrays.asList(new Integer[]{0, 1, 2}), Arrays.asList(new Double[]{0.0, 3.0, 2.0, 1.0})); // SBTA
		dict.put(Arrays.asList(new Integer[]{0, 1, 3}), Arrays.asList(new Double[]{1.0, 3.0, 2.0, 0.0})); // ABTS
		//
		dict.put(Arrays.asList(new Integer[]{0, 2, 0}), Arrays.asList(new Double[]{1.0, 2.0, 0.0, 3.0})); // ATSB
		dict.put(Arrays.asList(new Integer[]{0, 2, 1}), Arrays.asList(new Double[]{1.0, 3.0, 0.0, 2.0})); // ABST
		dict.put(Arrays.asList(new Integer[]{0, 2, 2}), Arrays.asList(new Double[]{2.0, 3.0, 0.0, 1.0})); // TBSA
		dict.put(Arrays.asList(new Integer[]{0, 2, 3}), Arrays.asList(new Double[]{2.0, 3.0, 1.0, 0.0})); // TBAS
		//
		dict.put(Arrays.asList(new Integer[]{1, 0, 0}), Arrays.asList(new Double[]{1.0, 0.0, 2.0, 3.0})); // ASTB
		dict.put(Arrays.asList(new Integer[]{1, 0, 1}), Arrays.asList(new Double[]{1.0, 0.0, 3.0, 2.0})); // ASBT
		dict.put(Arrays.asList(new Integer[]{1, 0, 2}), Arrays.asList(new Double[]{2.0, 0.0, 3.0, 1.0})); // TSBA
		dict.put(Arrays.asList(new Integer[]{1, 0, 3}), Arrays.asList(new Double[]{2.0, 1.0, 3.0, 0.0})); // TABS
		//
		dict.put(Arrays.asList(new Integer[]{1, 1, 0}), Arrays.asList(new Double[]{2.0, 0.0, 1.0, 3.0})); // TSAB
		dict.put(Arrays.asList(new Integer[]{1, 1, 1}), Arrays.asList(new Double[]{3.0, 0.0, 1.0, 2.0})); // BSAT
		dict.put(Arrays.asList(new Integer[]{1, 1, 2}), Arrays.asList(new Double[]{3.0, 0.0, 2.0, 1.0})); // BSTA
		dict.put(Arrays.asList(new Integer[]{1, 1, 3}), Arrays.asList(new Double[]{3.0, 1.0, 2.0, 0.0})); // BATS
		//
		dict.put(Arrays.asList(new Integer[]{1, 2, 0}), Arrays.asList(new Double[]{2.0, 1.0, 0.0, 3.0})); // TASB
		dict.put(Arrays.asList(new Integer[]{1, 2, 1}), Arrays.asList(new Double[]{3.0, 1.0, 0.0, 2.0})); // BAST
		dict.put(Arrays.asList(new Integer[]{1, 2, 2}), Arrays.asList(new Double[]{3.0, 2.0, 0.0, 1.0})); // BTSA
		dict.put(Arrays.asList(new Integer[]{1, 2, 3}), Arrays.asList(new Double[]{3.0, 2.0, 1.0, 0.0})); // BTAS
		// 5vv
		dict.put(Arrays.asList(new Integer[]{0, 0, 0, 0}), Arrays.asList(new Double[]{0.0, 1.0, 2.0, 3.0, 4.0}));
		dict.put(Arrays.asList(new Integer[]{1, 0, 0, 0}), Arrays.asList(new Double[]{1.0, 0.0, 2.0, 3.0, 4.0}));
		dict.put(Arrays.asList(new Integer[]{1, 2, 3, 4}), Arrays.asList(new Double[]{4.0, 3.0, 2.0, 1.0, 0.0}));
		// TODO

		// Return null if configs contains only -1s, i.e., if no motif was found, or
		// if not enough motifs (more than half of the new entries) were found to make a 
		// clear prediction
		// 2vv: configs.size() == 1: one -1 returns null (none) (there is one new entry; half of it = 1/2)
		// 3vv: configs.size() == 2: two -1s returns null (881) (there are two new entries; half of them = 2/2)
		// 4vv: configs.size() == 3: two or three -1s returns null (none) (there are three new entries; half of them = 3/2)
		// 5vv: configs.size() == 4: three or four -1s returns null (849_1) (there are four new entries; half of them = 4/2)
		int minusOnes = Collections.frequency(configs, -1); 
		if ((ToolBox.sumListInteger(configs) == -configs.size()) ||
			((double)minusOnes/configs.size() > 0.5)) {
			System.out.println(configs);
			System.out.println("==========>>> null: no or not enough motifs found for " + getName());
			return null;
		}
		// Get the voicing that corresponds to the (corrected) determined config
		List<Integer> corrConfigs = new ArrayList<Integer>();
		for (int i : configs) {
			// No config set? Assume that the voice is added as lowest (config 0)
			if (i == -1) {
				corrConfigs.add(0);
			}
			else {
				corrConfigs.add(i);
			}
		}
		// Return null if corrConfigs does not exist
		System.out.println("configs = " + configs);
		System.out.println("corrConfigs = " + corrConfigs);
		List<Double> voiceEntries = dict.get(corrConfigs);
		System.out.println("voiceEntries = " + voiceEntries);
		if (voiceEntries == null) {
			System.out.println("null: config does not exist.");
			System.exit(0);
			return null;
		}
		else {
			List<Integer> indices = new ArrayList<Integer>();
			List<Integer> voices = new ArrayList<Integer>();
			List<Integer> voicesAlreadyAdded = new ArrayList<Integer>();
			for (int i = 0; i < lowestNoteIndicesFirstChords.size(); i++) {		
				int currInd = lowestNoteIndicesFirstChords.get(i);
				// List indices
				List<Integer> currIndices = new ArrayList<Integer>();
				int chordSize = 
					(btp != null) ? btp[currInd][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :	
					bnp[currInd][CHORD_SIZE_AS_NUM_ONSETS];
				for (int j = currInd; j < currInd + chordSize; j++) {
					currIndices.add(j);
				}
				indices.addAll(currIndices);

				// List voices
				List<Integer> currVoices = new ArrayList<Integer>();
				int currVoice = voiceEntries.get(i).intValue();
				System.out.println("currVoice = " + currVoice);
				Collections.sort(voicesAlreadyAdded);
				Collections.reverse(voicesAlreadyAdded);
				if (currInd == 0) {
					currVoices.add(currVoice);
				}
				else {
					// Determine the previous chord
					int toSub = 
						(btp != null) ? btp[currInd-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
						bnp[currInd-1][CHORD_SIZE_AS_NUM_ONSETS];
					int prevLowestInd = currInd - toSub;
					List<Integer> prevChord = new ArrayList<Integer>();
					for (int k = prevLowestInd; k < currInd; k++) {
						prevChord.add(
							(btp != null) ? btp[k][Tablature.PITCH] : 
							bnp[k][PITCH]);
					}
					for (int ind : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, prevLowestInd)) {
						prevChord.add(
							(btp != null) ? btp[ind][Tablature.PITCH] :
							bnp[ind][PITCH]);
					}
					Collections.sort(prevChord);
					System.out.println("prevChord = " + prevChord);

					// Find any sustained notes in the first chord of the current 
					// density change 
					List<Integer[]> currFirst = pitchesFirstChords.get(i);
					System.out.println("currFirst = ");
					for (Integer[] in : currFirst) {
						System.out.println(Arrays.toString(in));
					}
					List<Integer> voicesSustained = new ArrayList<Integer>();
					for (int j = 0; j < currFirst.size(); j++) {
						Integer[] in = currFirst.get(j);
						// Sustained note?
						int voice = -1;
						// If the current note is a sustained note: find the position
						// of this note in the previous chord, and then its voice 
						// NB: it is assumed that there are no voice crossings in both chords
						if (in[1] == 1) {
							// Find position of note in previous chord
							voice = voicesAlreadyAdded.get(prevChord.indexOf(in[0]));
							voicesSustained.add(voice);
						}
					}
					System.out.println("voicesSustained = " + voicesSustained);
					System.out.println("voicesAlreadyAdded = " + voicesAlreadyAdded);
					
					// Determine the available voices for the notes in the current chord,
					// which are the active voices without any sustained voices and the 
					// current voice
					List<Integer> availableVoices = new ArrayList<Integer>();
					availableVoices.add(currVoice);
					for (int v : voicesAlreadyAdded) {
						if (!voicesSustained.contains(v)) {
							availableVoices.add(v);
						}
					}
					Collections.sort(availableVoices);
					Collections.reverse(availableVoices);
					System.out.println("availableVoices = " + availableVoices);
					currVoices.addAll(availableVoices);
				}
				System.out.println("currVoices = " + currVoices);
				voices.addAll(currVoices);
				// Update voicesAlreadyAdded
				voicesAlreadyAdded.add(currVoice);
			}
			res.add(configs);
			res.add(indices);
			res.add(voices);
			return res;
		}
//		return res;
	}


	/**
	 * Quantises the duration to the next multiple of the given granularity fraction.
	 * 
	 * @param curr
	 * @param granularity
	 * 
	 * @return  
	 */
	// TESTED
	static Rational quantiseDuration(Rational dur, Rational granularity) {
		boolean quantised = false;
		while (!quantised) {
			if (dur.isMultiple(granularity)) {
				quantised = true;
			}
			else {
				dur = new Rational(dur.getNumer() + 1, dur.getDenom());
			}
		}
		dur.reduce();
		return dur;
	}


	/**
	 * Gets, for each note in basicNoteProperties, the metric position.
	 * 
	 * Non-tablature case only.
	 * 
	 * @return
	 */
	// TESTED
	List<Rational[]> getMetricPositionsNotes() {
		List<Rational[]> mp = new ArrayList<Rational[]>();
		ScoreMetricalTimeLine smtl = getScorePiece().getScoreMetricalTimeLine();
		for (Integer[] b : getBasicNoteProperties()) {
			mp.add(
				smtl.getMetricPosition(new Rational(b[ONSET_TIME_NUMER], b[ONSET_TIME_DENOM]))
			); 
		}
		return mp;	
	}


	/**
	 * Returns a list of lists, where each list represents a note in the chord and contains </br>
	 * as element 0: the note's pitch </br>
	 * as element 1: the numerator of the note's duration as a Rational (or <code>null</code> if
	 *               it is a sustained previous note </br>
	 * as element 2: the denominator of the note's duration as a Rational (or <code>null</code> if
	 *               it is a sustained previous note </br>
	 * 
	 * The list returned is sorted in ascending order.
	 * 
	 * @param lowestNoteIndex
	 * @param btp
	 * @param bnp
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	List<List<Integer>> getChordInfo(Integer[][] btp, List<List<Double>> durationLabels, 
		Integer[][] bnp, int lowestNoteIndex) {
		
		verifyCase(btp, bnp);

		List<List<Integer>> noteInfo = new ArrayList<List<Integer>>(); 

//		Rational currMotifNoteDur = headMotif.get(j);
		int chordSize = 
			(btp != null) ? btp[lowestNoteIndex][Tablature.CHORD_SIZE_AS_NUM_ONSETS] : 
			bnp[lowestNoteIndex][CHORD_SIZE_AS_NUM_ONSETS];
		// Add pitches of all notes in the chord. NB: one-line initialisation of curr (using 
		// Arrays.asList()) is not possible, as this gives an UnsupportedOperationException 
		// downstream when adding to noteInfo
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
//			int pitch = bnp[i][PITCH];
//			int isDur = 0;
//			Rational currNoteDur = new Rational(bnp[i][DURATION_NUMER], bnp[i][DURATION_DENOM]);
//			if (currNoteDur.equals(currMotifNoteDur)) {
//				isDur = 1;
//			}
			if (btp != null) {
				Rational dur = LabelTools.convertIntoDuration(durationLabels.get(i))[0];
				List<Integer> curr = new ArrayList<Integer>();
				curr.add(btp[i][Tablature.PITCH]);
				curr.add(dur.getNumer());
				curr.add(dur.getDenom());
				noteInfo.add(curr);
			}
			if (bnp != null) {
				List<Integer> curr = new ArrayList<Integer>();
				curr.add(bnp[i][PITCH]);
				curr.add(bnp[i][DUR_NUMER]);
				curr.add(bnp[i][DUR_DENOM]);
				noteInfo.add(curr);			
			}
		}
		// Add pitches of any sustained previous notes
		for (int indSus : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, lowestNoteIndex)) {
			List<Integer> curr = new ArrayList<Integer>();
			curr.add((btp != null) ? btp[indSus][Tablature.PITCH] : bnp[indSus][PITCH]);
			curr.add(null);
			curr.add(null);
			noteInfo.add(curr);
		}
		noteInfo = ToolBox.bubbleSort(noteInfo, 0);
		return noteInfo;
	}


	/**
	 * Finds the first neighbour chord (as returned by getChordInfo()) that does not contain a 
	 * unison. 
	 * 
	 * @param btp
	 * @param durationLabels
	 * @param bnp
	 * @param direction +1 if looking for the right (next) neighbour chord; -1 
	 *        if looking for the left (previous) one
	 * @param lowestNoteIndex The index of the lowest note of the current chord
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case)
	List<List<Integer>> getNonUnisonNeighbourChord(Integer[][] btp, List<List<Double>> 
		durationLabels, Integer[][] bnp, int direction, int lowestIndCurr) {
		
		verifyCase(btp, bnp);

		if (direction == -1) {
			// Find the first previous chord without a unison
			List<List<Integer>> prevChord = null;
			// Only if the chord at lowestIndCurr is not the first chord  
			if (lowestIndCurr != 0) {
				int sizePrev = (btp != null) ? btp[lowestIndCurr-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
					bnp[lowestIndCurr-1][CHORD_SIZE_AS_NUM_ONSETS];
//				int lowestIndPrev = 
//					lowestIndCurr - bnp[lowestIndCurr-1][CHORD_SIZE_AS_NUM_ONSETS];
				int lowestIndPrev = lowestIndCurr - sizePrev;
				for (int i = lowestIndPrev; i >= 0 ; i--) {
					prevChord = getChordInfo(btp, durationLabels, bnp, lowestIndPrev);
					boolean unisonFound = false;
					// Compare pitches
					outer: for (int j = 0; j < prevChord.size(); j++) {
						for (int k = 0; k < prevChord.size(); k++) {
							if (k != j) {
								// Unison found? Determine chord before previous and break 
								if (prevChord.get(k).get(0) == prevChord.get(j).get(0)) {
									unisonFound = true;
									int sizeBeforePrev = 
										(btp != null) ? btp[lowestIndPrev-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
										bnp[lowestIndPrev-1][CHORD_SIZE_AS_NUM_ONSETS];
//									lowestIndPrev = lowestIndPrev - 
//										bnp[lowestIndPrev-1][CHORD_SIZE_AS_NUM_ONSETS];
									lowestIndPrev = lowestIndPrev - sizeBeforePrev;
									i = lowestIndPrev + 1;
									break outer;
								}
							}
						}
					}
					// No unison found in chord? Previous chord found 
					if (!unisonFound) {
						break;
					}
				}
			}
			return prevChord; 
		}

		// Find the first next chord without a unison
		else {
			List<List<Integer>> nextChord = null;
			// Only if the chord at lowestIndCurr is not the last chord
			int sizeCurr = (btp != null) ? btp[lowestIndCurr][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
				bnp[lowestIndCurr][CHORD_SIZE_AS_NUM_ONSETS];
			if ((lowestIndCurr + sizeCurr) != ((btp != null) ? btp.length : bnp.length)) {
//			if (lowestIndCurr + bnp[lowestIndCurr][CHORD_SIZE_AS_NUM_ONSETS] != bnp.length) {
				int lowestIndNext = lowestIndCurr + sizeCurr;
//				int lowestIndNext = 
//					lowestIndCurr + bnp[lowestIndCurr][CHORD_SIZE_AS_NUM_ONSETS];
				for (int i = lowestIndNext; i < ((btp != null) ? btp.length : bnp.length); i++) {
//				for (int i = lowestIndNext; i < bnp.length; i++) {
					nextChord = getChordInfo(btp, durationLabels, bnp, lowestIndNext);
					boolean unisonFound = false;
					// Compare pitches
					outer: for (int j = 0; j < nextChord.size(); j++) {
						for (int k = 0; k < nextChord.size(); k++) {
							if (k != j) {
								// Unison found? Determine chord after next and break 
								if (nextChord.get(k).get(0) == nextChord.get(j).get(0)) {
									unisonFound = true;
									int sizeNext = (btp != null) ? btp[lowestIndNext][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
										bnp[lowestIndNext][CHORD_SIZE_AS_NUM_ONSETS];
									lowestIndNext = lowestIndNext + sizeNext;
//									lowestIndNext = 
//										lowestIndNext + bnp[lowestIndNext][CHORD_SIZE_AS_NUM_ONSETS];
									i = lowestIndNext - 1;
									break outer;
								}
							}
						}
					}
					// No unison found in chord? Next chord found 
					if (!unisonFound) {
						break;
					}
				}
			}
			return nextChord;
		}
	}


	/**
	 * Determines the sequence of voice entries based on the cost of connecting two configurations.
	 * 
	 * @param btp
	 * @param durationLabels
	 * @param bnp
	 * @param numVoices
	 * @param n
	 * @return
	 */
	// TESTED TODO: implement full note duration case in tablature case
	// Return <code>null</code> if one or more voices are missing from the returned list of voices. 
	List<List<Integer>> getNonImitativeVoiceEntries(Integer[][] btp, List<List<Double>> durationLabels,
		Integer[][] bnp, int numVoices, int n) { // in 2020
		
		verifyCase(btp, bnp);
		
		List<Integer> allConfigs = new ArrayList<Integer>();
		List<Integer> allIndices = new ArrayList<Integer>();
		List<Integer> allVoices = new ArrayList<Integer>();
		
//		Integer[][] bnp = getBasicNoteProperties(); // in 2020
		
		// Determine the densities and the indices of the lowest note of the first
		// chord with the new density
		List<Integer> densities = new ArrayList<Integer>();
		List<Integer> indices = new ArrayList<Integer>();
//		List<List<Integer>> firstChordIndices = new ArrayList<List<Integer>>();
		List<Integer> noteDensities = getNoteDensity(btp, durationLabels, bnp); // in 2020
		int prevDensity = 0;
		for (int i = 0; i < noteDensities.size(); i++) {
			int d = noteDensities.get(i);
			if (d > prevDensity) {
				prevDensity = d;
				densities.add(d);
				indices.add(i);
				List<Integer> currFirstChordInd = new ArrayList<Integer>();
				int chordSize = 
					(btp != null) ? btp[i][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
					bnp[i][CHORD_SIZE_AS_NUM_ONSETS];	
				for (int j = i; j < i + chordSize; j++) {
					currFirstChordInd.add(j);
				}
//				firstChordIndices.add(currFirstChordInd);
				allIndices.addAll(currFirstChordInd);
			}
		}
		System.out.println("indices = " + indices);
		System.out.println("densities = " + densities);
//		System.out.println(firstChordIndices);
		
//		FeatureGenerator fg = new FeatureGenerator();
		// rightVoices is the list of voices available for the current right chord; it is 
		// initialised with all voices (starting with the highest)
		List<Integer> rightVoices = new ArrayList<Integer>();
		for (int i = 0; i < numVoices; i++) {
			rightVoices.add(i);
		}

		// For each density decrease, starting at the last: 
		// (1) add the voices in rightVoices that do not go with a sustained previous note to
		//     allVoices
		// (2) determine the optimal config and remove all newly entering voices from 
		//     rightVoices, thus setting it for the next iteration of the for-loop
		for (int i = densities.size()-1; i > 0; i--) {
			System.out.println("density increase index = " + indices.get(i));
			System.out.println("rightVoices = " + rightVoices);
			
			// Determine left/right densities and left/current/right density increase index
			int leftDensity = densities.get(i-1); 
			int rightDensity = densities.get(i);
			System.out.println("leftDensity = " + leftDensity);
			System.out.println("rightDensity = " + rightDensity);	
			int leftDensIncrInd = indices.get(i-1);
			int currDensIncrInd = indices.get(i);
			int rightDensIncrInd;
			if (i == densities.size()-1) {
				rightDensIncrInd = 
					(btp != null) ? btp.length - btp[btp.length-1][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
					bnp.length - bnp[bnp.length-1][CHORD_SIZE_AS_NUM_ONSETS];
			}
			else {
				rightDensIncrInd = indices.get(i+1);
			}
			System.out.println("-----");
			System.out.println("rightDensIncrInd = " + rightDensIncrInd);
			System.out.println("currDensIncrInd = " + currDensIncrInd);
			System.out.println("leftDensIncrInd = " + leftDensIncrInd);

			// 1. List all left chords (i.e., those of size leftDensity) and all right chords 
			// (i.e., those of size rightDensity)
			List<List<List<Integer>>> lAndR = new ArrayList<List<List<Integer>>>();
			lAndR.add(new ArrayList<List<Integer>>()); // leftChords
			lAndR.add(new ArrayList<List<Integer>>()); // rightChords
			for (int l = 0; l < lAndR.size(); l++) {
				int start = leftDensIncrInd;
				int end = currDensIncrInd;
				int dens = leftDensity;
				if (l == 1) {
					start = currDensIncrInd;
					end = rightDensIncrInd;
					dens = rightDensity;
				}
				for (int j = start; j < end; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = 
						(btp != null) ? btp[j][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
						bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
//					List<List<Integer>> pitchesAndSpnInChord = new ArrayList<List<Integer>>();
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches and sustained previous note-ness of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
//						pitchesAndSpnInChord.add(Arrays.asList(new Integer[]{bnp[k][PITCH], 0}));
						pitchesInChord.add(
							(btp != null) ? btp[k][Tablature.PITCH]	:
							bnp[k][PITCH]);
					}
					// Add pitches and sustained previous note-ness of any sustained previous notes
					for (int ind : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, j)) {
//						pitchesAndSpnInChord.add(Arrays.asList(new Integer[]{bnp[ind][PITCH], 1}));
						pitchesInChord.add(
							(btp != null) ? btp[ind][Tablature.PITCH] :	
							bnp[ind][PITCH]);
					}
//					pitchesAndSpnInChord = ToolBox.bubbleSort(pitchesAndSpnInChord);
//					Collections.reverse(pitchesAndSpnInChord);
//					List<List<Integer>> t = ToolBox.transposeListOfLists(pitchesAndSpnInChord);
//					List<Integer> pitchesInChord = t.get(0);
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);

					// Add only those chords of size dens
					if (pitchesInChord.size() == dens) { 
						// Complete left chords to size rightDensity by adding placeholder(s)
						if (l == 0) {
							while (pitchesInChord.size() < rightDensity) {
								pitchesInChord.add(null);
							}
						}
						lAndR.get(l).add(pitchesInChord);
					}

//					if (l == 0) {
//						// Add only those chords of size dens; complete them to size 
//						// rightDensity by adding placeholder(s) 
//						if (pitchesInChord.size() == dens) { 
//							while (pitchesInChord.size() < rightDensity) {
//								pitchesInChord.add(null);
//							}
//							lAndR.get(l).add(pitchesInChord);
//						}
//					}
//					if (l == 1) {
////						// If first chord: set spnInFirstRightChord
////						if (j == start) {
////							spnInFirstRightChord = t.get(1);
////						}
//						// Add only those chords of size dens
//						if (pitchesInChord.size() == dens) {
//							lAndR.get(l).add(pitchesInChord);
//						}
//					}
						
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
			}
			List<List<Integer>> leftChords = lAndR.get(0);
//			System.out.println("leftChords = " + leftChords);
//			for (List<Integer> l : leftChords) {System.out.println(l); }
			List<List<Integer>> rightChords = lAndR.get(1);
//			System.out.println("rightChords = " + rightChords);
//			for (List<Integer> l : rightChords) { System.out.println(l); }

			// 2. List any sustained previous notes in the first right chord  
			List<Integer> spnInFirstRightChord = new ArrayList<Integer>();
//			int sizeFirstRightCh = bnp[currDensIncrInd][CHORD_SIZE_AS_NUM_ONSETS];
			List<List<Integer>> spn = new ArrayList<List<Integer>>();
			// Add pitches and sustained previous note-ness of all notes in the chord
			int chrdSize = 
				(btp != null) ? btp[currDensIncrInd][Tablature.CHORD_SIZE_AS_NUM_ONSETS] :
				bnp[currDensIncrInd][CHORD_SIZE_AS_NUM_ONSETS];	
			for (int j = currDensIncrInd; j < (currDensIncrInd + chrdSize); j++) {
				System.out.println("j ==== " + j);
				int toAdd = (btp != null) ? btp[j][Tablature.PITCH] : bnp[j][PITCH];
				spn.add(Arrays.asList(new Integer[]{toAdd, 0}));
			}
			// Add pitches and sustained previous note-ness of any sustained previous notes
			for (int ind : getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, currDensIncrInd)) {
				int toAdd = (btp != null) ? btp[ind][Tablature.PITCH] : bnp[ind][PITCH]; 
				spn.add(Arrays.asList(new Integer[]{toAdd, 1}));
			}
			// Sort on pitch
			spn = ToolBox.bubbleSort(spn, 0);
			Collections.reverse(spn);
			spnInFirstRightChord = ToolBox.transposeListOfLists(spn).get(1);

//			// 1. List all left chords, i.e., those of size leftDensity
//			List<List<Integer>> leftChords = new ArrayList<List<Integer>>();
//			for (int j = leftDensIncrInd; j < currDensIncrInd; j++) {
//				// j is the index of the lowest note in the chord
//				int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
//				List<Integer> pitchesInChord = new ArrayList<Integer>();
//				// Add pitches of all notes in the chord
//				for (int k = j; k < j + chordSize; k++) {
//					pitchesInChord.add(bnp[k][PITCH]);
//					}
//					// Add pitches of any sustained previous notess
//					for (int ind : fg.getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
//						pitchesInChord.add(bnp[ind][PITCH]);
//					}
//					Collections.sort(pitchesInChord);
//					Collections.reverse(pitchesInChord);
//
//					// Add only those chords of size leftDensity; complete them to size 
//					// rightDensity by adding placeholder(s)
//					if (pitchesInChord.size() == leftDensity) {
//						while (pitchesInChord.size() < rightDensity) {
//							pitchesInChord.add(null);
//						}
//						leftChords.add(pitchesInChord);
//					}
//
//					// Increment j so that the next iteration starts from the next chord
//					j = (j + chordSize) - 1;
//				}
////				System.out.println("leftChords = " + leftChords);
////				for (List<Integer> l : leftChords) {System.out.println(l); }
//				
//				// 2. List all right chords, i.e., those of size rightDensity, and any sustained
//				// previous notes in the first right chord 
//				List<List<Integer>> rightChords = new ArrayList<List<Integer>>();
////				List<Boolean> spnInFirstRightChord = new ArrayList<Boolean>();
//				List<Integer> spnInFirstRightChord = new ArrayList<Integer>();
//				for (int j = currDensIncrInd; j < rightDensIncrInd; j++) {
//					// j is the index of the lowest note in the chord
//					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
//					List<Integer> pitchesInChord = new ArrayList<Integer>();
//					List<List<Integer>> pitchesInChordExt = new ArrayList<List<Integer>>();
//					// Add pitches and sustained previous note-ness of all notes in the chord
//					for (int k = j; k < j + chordSize; k++) {
//						pitchesInChordExt.add(Arrays.asList(new Integer[]{bnp[k][PITCH], 0}));
//					}
//					// Add pitches and sustained previous note-ness of any sustained previous notess
//					for (int ind : fg.getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
//						pitchesInChordExt.add(Arrays.asList(new Integer[]{bnp[ind][PITCH], 1}));
//					}
//					pitchesInChordExt = ToolBox.bubbleSort(pitchesInChordExt);
//					Collections.reverse(pitchesInChordExt);
//					List<List<Integer>> t = ToolBox.transposeListOfLists(pitchesInChordExt);
//					pitchesInChord = t.get(0);
//					if (j == currDensIncrInd) {
//						spnInFirstRightChord = t.get(1);
//					}
//
//					// Add only those chords of size rightDensity
//					if (pitchesInChord.size() == rightDensity) {
//						rightChords.add(pitchesInChord);
//					}
//					
//					// Increment j so that the next iteration starts from the next chord
//					j = (j + chordSize) - 1;
//				}
////				System.out.println("rightChords = " + rightChords);
////				for (List<Integer> l : rightChords) { System.out.println(l); }

			// 3. Add all voices in rightVoices that do not go with a sustained previous 
			// note to allVoices
			// NB: these voices must remain in rightVoices, as this list, which is only
			// needed for the config calculation, must be the size of rightDensity
			System.out.println("rightVoices = " + rightVoices);
			System.out.println("allVoices was = " + allVoices);
			for (int j = 0; j < rightVoices.size(); j++) {
				if (spnInFirstRightChord.get(j) == 0) {
					allVoices.add(rightVoices.get(j));
				}
			}						
			System.out.println("allVoices is  = " + allVoices);

			// 4. Determine the optimal configuration of the last n left chords and the 
			// first n right chords, and then the voices to remove from rightVoices
			List<List<Integer>> lastNLeft;	
			if (n >= leftChords.size()) {
				lastNLeft = new ArrayList<List<Integer>>(leftChords);
			}
			else {
				lastNLeft = leftChords.subList(leftChords.size()-n, leftChords.size()); 
			}
			List<List<Integer>> firstNRight;
			if (n >= rightChords.size()) {
				firstNRight = new ArrayList<List<Integer>>(rightChords);
			}
			else {
				firstNRight = rightChords.subList(0, n);
			}
			System.out.println("lastNLeft = " + lastNLeft);
			System.out.println("firstNRight = " + firstNRight);
			System.out.println("spnInFirstRightChord = " + spnInFirstRightChord);

//				// Determine the number of configurations
//				int numConfigs = configs.size();
//				int numNull = Collections.frequency(leftChords.get(0), null);
//				// If the left chord contains one note or one placeholder (1-2, 1-3, 1-4, 1-5, 
//				// 2-3, 3-4, 4-5)
//				if (leftDensity == 1 || numNull == 1) {
//					numConfigs = rightDensity;
//				}
//				// If the left chord contains more than one note and the right chord contains
//				// at least two more notes than the left chord (2-4; 2-5; 3-5)
//				else if (leftDensity > 1 && (rightDensity - leftDensity >= 2)) {
//				if (leftDensity == 2 && rightDensity == 4) {
//					numConfigs = 6;
//				}
//				if (leftDensity == 2 && rightDensity == 5) {
//					numConfigs = 10; 
//				}
//				if (leftDensity == 3 && rightDensity == 5) {
//					numConfigs = 9; 
//				}

			List<List<List<Integer>>> configs = 
				determineConfigs(leftDensity, rightDensity, lastNLeft);
//			System.out.println(configs.size());
//			for (List<List<Integer>> l : configs) {
//				System.out.println(l);
//			}
//			for (List<List<Integer>> l : configs) {
//				System.out.println(l);
//			}
			List<Integer> costPerConfig = new ArrayList<Integer>();
			List<Integer> costPerConfigNonLin = new ArrayList<Integer>();
			List<Integer> costPerConfigLin = new ArrayList<Integer>();
			for (List<List<Integer>> l : configs) {
				int costNonLin = calculateConfigCost(l, firstNRight, false);
				int costLin = calculateConfigCost(l, firstNRight, true);	
				costPerConfigNonLin.add(costNonLin);
				costPerConfigLin.add(costLin);
//				costPerConfig.add(costLin + costNonLin);
				costPerConfig.add(costNonLin);
			}
//				int config = 0;
//				while (config < numConfigs) {
//					System.out.println("config = " + config);
//					// Determine the cost for the current configuration 
//					int costNonLin = calculateConfigCost(lastNLeft, firstNRight, false);
//					int costLin = calculateConfigCost(lastNLeft, firstNRight, true);	
//					costPerConfigNonLin.add(costNonLin);
//					costPerConfigLin.add(costLin);
//					costPerConfig.add(2*costLin + 3*costNonLin);
//					
//					// Determine the next configuration
//					if (config != numConfigs - 1) {
//						List<List<Integer>> partialLeftNew = 
//							determineNextConfig(config, numConfigs, leftDensity, rightDensity,
//							lastNLeft);
//						lastNLeft = partialLeftNew;
//						config++;
//					}
//					else {
//						break;
//					}
//				}
			System.out.println("costPerConfig = " + costPerConfig);
			System.out.println("costPerConfigNonLin = " + costPerConfigNonLin);
			System.out.println("costPerConfigLin    = " + costPerConfigLin);
			int bestConfig = costPerConfig.indexOf(Collections.min(costPerConfig)); 
			System.out.println("bestConfig = " + bestConfig);
			allConfigs.add(bestConfig);

			// Determine the newly entering voice(s), i.e., the voice(s) that are in the 
			// right chord but not in the left, and remove them from rightVoices for the
			// next iteration of the outer for-loop
			// Config possibilities:
			// 1-2  1-3  1-4  1-5  1-6
			//      2-3  2-4  2-5  2-6
			//           3-4  3-5  3-6
			//                4-5  4-6
			//                     5-6
//			List<Integer> newVoices = new ArrayList<Integer>();
			// If the left chord contains one note or one placeholder
			if (leftDensity == 1 || leftDensity == (rightDensity - 1)) {
//			if (numConfigs == rightDensity) {
				System.out.println("rightVoices = " + rightVoices);
				System.out.println("bestConfig = " + bestConfig);
				// If the left chord contains one note (1-2, 1-3, 1-4, 1-5, 1-6)
				if (leftDensity == 1) {
					System.out.println("leftDensity contains one note");
					// The voice at index bestConfig in rightVoices is the already active 
					// voice, so the voices at all other indices are newly entering voices (NEV)
					// Example 1-4:
					//     (0)         (1)         (2)         (3)     
					//     x x           x           x           x
					//       x         x x           x           x
					//       x           x         x x           x
					//       x           x           x         x x
					// NEV 1,2,3       0,2,3       0,1,3       0,1,2
					List<Integer> toRemove = new ArrayList<Integer>(); 
					for (int j = 0; j < rightVoices.size(); j++) {
//						System.out.println(j);
						if (j != bestConfig) {
							toRemove.add(rightVoices.get(j));
						}
					}
					System.out.println("toRemove = " + toRemove);
					for (int p : toRemove) {
						rightVoices.remove((Integer) p);
					}	
				}
				// If the left chord contains one placeholder (2-3, 3-4, 4-5, 5-6)
				else if (leftDensity == (rightDensity - 1)) {
					System.out.println("leftDensity is one smaller than rightDensity");
//				else if (numNull == 1) {
					// The voice at index (rightDensity-1)-bestConfig in rightVoices is the
					// newly entering voice (NEV)
					// Example 3-4:
					//     (0)         (1)         (2)         (3)
					//     x x         x x         x x           x
					//     x x         x x           x         x x
					//     x x           x         x x         x x
					//       x         x x         x x         x x
					// NEV (4-1)-0=3   (4-1)-1=2   (4-1)-2=1   (4-1)-3=0
					rightVoices.remove((rightDensity-1)-bestConfig);
				}
			}
			// If the left chord contains more than one note and the right chord contains
			// at least two more notes than the left chord (2-4, 2-5, 2-6, 3-5, 3-6, 4-6)
			else {
				// 2-4:
				//     (0)      (1)      (2)      (3)      (4)      (5)
				//     x x      x x      x x        x        x        x
				//     x x        x        x      x x      x x        x
				//       x      x x        x      x x        x      x x
				//       x        x      x x        x      x x      x x
				// NEV 2,3      1,3      1,2      0,3      0,2      0,1
				if (leftDensity == 2 && rightDensity == 4) {
					List<Integer[]> toRemove = Arrays.asList(new Integer[][]{
						new Integer[]{2, 3},
						new Integer[]{1, 3},
						new Integer[]{1, 2},
						new Integer[]{0, 3},
						new Integer[]{0, 2},
						new Integer[]{0, 1}}
					);
					for (int p : toRemove.get(bestConfig)) {
						rightVoices.remove((Integer) p);
					}
				}
				else if (leftDensity == 2 && rightDensity == 5) { // TODO Fix! (does not happen currently)
					System.out.println("AAAAAAAAAaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
					System.exit(0);
				}
				else if (leftDensity == 3 && rightDensity == 5) { // TODO Fix! (does not happen currently)
					System.out.println("AAAAAAAAAaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
					System.exit(0);
				}
			}
			System.out.println("rightVoices = " + rightVoices);
		}	
		// Add remaining rightVoices and reverse allVoices
		allVoices.addAll(rightVoices);
		Collections.reverse(allVoices);
		// Reverse allConfigs
		Collections.reverse(allConfigs);
		System.out.println("HIERRRRR");
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		System.out.println("allVoices = " + allVoices);
		// Verify that each voice is in allVoices
		boolean valid = true;
		for (int i = 0; i < numVoices; i++) {
			if (!allVoices.contains(i)) {
				valid = false;
				break;
			}
		}
//		if (valid) {
			System.out.println(allConfigs);
			System.out.println(allIndices);
			System.out.println(allVoices);
			res.add(allConfigs);
			res.add(allIndices);
			res.add(allVoices);
			return res;
//		}
//		else {
//			System.out.println("NOT VALID!");
//			return null;
//		}

//		return res;
	}


//	static List<List<List<Integer>>> determineConfigsDEZE(int leftDensity, 
//		int rightDensity, List<List<Integer>> partialLeft) {
//			
//		List<List<List<Integer>>> configs = new ArrayList<List<List<Integer>>>();
//			
////		List<Integer> leftIndices = new ArrayList<Integer>();
////		for (int i = 0; i < leftDensity; i++) {
////			leftIndices.add(i);
////		}
//			
////		List<List<Integer>> currConfig = new ArrayList<List<Integer>>();
////		for (List<Integer> l : partialLeft) {
////			currConfig.add(new ArrayList<Integer>(l));
////		}
//
//		// Initialise the startIndices
//		List<Integer> hiIndices = new ArrayList<Integer>();
//		for (int i = 0; i <= rightDensity-leftDensity; i++) {
//			hiIndices.add(i);
//		}
//		System.out.println("startIndices = " + hiIndices);
//		List<List<Integer>> availableCovered = new ArrayList<List<Integer>>();
//		for (int i = 0; i < hiIndices.size(); i++) {
//			int indHi = hiIndices.get(i);
//			System.out.println("indHi = " + indHi);
//			
//			// Make the initial config for the current startIndex
//			List<List<Integer>> startConfig = new ArrayList<List<Integer>>();
//			for (List<Integer> l : partialLeft) {
//				List<Integer> curr = new ArrayList<Integer>(l);
//				// Shift pitches
//				for (int j = 0; j < indHi; j++) {
//					curr.add(j, null);
//					curr.remove(curr.size()-1);
//				}
//				startConfig.add(curr);
//			}
//			System.out.println("startConfig = " + startConfig);
//			
//			// Determine the available indices 
//			List<Integer> indAvailable = new ArrayList<Integer>();
//			List<Integer> firstChord = startConfig.get(0);
//			for (int j = 0; j < firstChord.size(); j++) {
//				if (firstChord.get(j) == null) {
//					indAvailable.add(j);
//				}
//			}
//			availableCovered.add(indAvailable);
//			System.out.println("available = " + availableCovered);
//
//			// Determine the lowest note index
//			int indLow = hiIndices.get((hiIndices.size() - 1) - i);	
//			int indPreLow = indLow -1;
////			int indLow = -1;
////			for (int j = firstChord.size() - 1; j >= 0; j--) {
////				if (firstChord.get(j) != null) {
////					indLow = j;
////					break;
////				}
////			}
//			System.out.println("indLow = " + indLow);
//			// Determine the next config(s) by swapping indLow with all available indices
//			
//			while (indPreLow + 1 < rightDensity) {
//				while (indLow < rightDensity) {
//					for (List<Integer> l : startConfig) { 
//						Collections.swap(l, indLow, indLow+1);
//					}
//					indLow++;
//				}
//				for (List<Integer> l : startConfig) { 
//					Collections.swap(l, indPreLow, indPreLow+1);
//				}
//				indPreLow++;
//				for (List<Integer> l : startConfig) { 
//					Collections.swap(l, indLow, indPreLow+1);
//				}
//				indLow = indPreLow + 1;
//			}
//				
//				while (indPostLow < rightDensity) {
////					List<List<Integer>> currConfig = new ArrayList<List<Integer>>();
////					for (List<Integer> l : startConfig) {
////						currConfig.add(new ArrayList<Integer>(l));
////					}
//					
//					
//				}
//				indPreLow--;
//				for (int j = 0; j < indAvailable.size(); j++) {
//					int indAv = indAvailable.get(j);
//					System.out.println("indAv = " + indAv);
//					
//					List<List<Integer>> currConfig = new ArrayList<List<Integer>>();
//					for (List<Integer> l : startConfig) {
//						currConfig.add(new ArrayList<Integer>(l));
//					}
//					
//					for (List<Integer> l : currConfig) { //currConfig) {
////						System.out.println("l = " + l);
//						Collections.swap(l, indLow, indAv);
//						System.out.println("l = " + l);
//					}
//					// Add only if the resulting available indices have not been seen before
//					List<Integer> resAv = new ArrayList<Integer>(indAvailable);
//					resAv.set(j, indLow);
//					Collections.sort(resAv);
//					System.out.println(resAv);
//					if (!availableCovered.contains(resAv)) {
//						configs.add(currConfig);
//						availableCovered.add(resAv);
//					}
//				}
//				indLow--;
//				System.out.println("indLow = " + indLow);
//			}
//			System.out.println(configs);
//			System.exit(0);
//		}	
//		return configs;
//	}

	
	/**
	 * Calculates all possible configurations of the given chords.
	 * 
	 * Example for leftDensity = 2 and rightDensity = 4 (six configs). Numbers indicate the 
	 * indices of the configs in the list returned.  
	 * 
	 * startConfig 0
	 * (0)          (1)  (2)         (3)  (4)
	 * o o | (o o)  o o  o o  (x o)    o    o   
	 * o o | (x o)    o    o  (o o)  o o  o o 
	 *   o | (  o)  x o    o  (  o)  x o    o 
	 *   o | (  o)    o  x o  (  o)    o  x o 
	 * 
	 * startConfig 1
	 *                                    (5)
	 *   o | (  o)  x o    o  (  o)  x o    o   
	 * o o | (o o)  o o  o o  (x o)    o    o 
	 * o o | (x o)    o    o  (o o)  o o  o o 
	 *   o | (  o)    o  x o  (  o)    o  x o   
	 * 
	 * startConfig 2
	 * 
	 *   o | (  o)  x o    o  (  o)  x o    o   
	 *   o | (  o)    o  x o  (  o)    o  x o 
	 * o o | (o o)  o o  o o  (x o)    o    o 
	 * o o | (x o)    o    o  (o o)  o o  o o
	 * 
	 * startConfig 3
	 *        
	 * o o | (o o)  o o  o o  (x o)    o    o   
	 *   o | (  o)  x o    o  (  o)  x o    o 
	 *   o | (  o)    o  x o  (  o)    o  x o 
	 * o o | (x o)    o    o  (o o)  o o  o o
	 *    
	 * @param leftDensity
	 * @param rightDensity
	 * @param leftChords
	 * @return
	 */
	// TESTED
	static List<List<List<Integer>>> determineConfigs(int leftDensity, int rightDensity, 
		List<List<Integer>> leftChords) {		
		List<List<List<Integer>>> configs = new ArrayList<List<List<Integer>>>();
		
		// Create the initial startConfig
		List<List<Integer>> currStartConfig = new ArrayList<List<Integer>>();
		for (List<Integer> l : leftChords) {
			currStartConfig.add(new ArrayList<Integer>(l));
		}
		
		// For each startConfig
		List<List<Integer>> availableCovered = new ArrayList<List<Integer>>();
		for (int i = 0; i < rightDensity; i++) {			
//			System.out.println("i = " + i);
			// Determine the available indices in the current startConfig 
			List<Integer> availableInd = new ArrayList<Integer>();
			List<Integer> firstChord = currStartConfig.get(0);
			for (int j = 0; j < firstChord.size(); j++) {
				if (firstChord.get(j) == null) {
					availableInd.add(j);
				}
			}

			// Add the current startConfig to configs (only if it has not been added before)
			if (!availableCovered.contains(availableInd)) {
				List<List<Integer>> currConfigToAdd = new ArrayList<List<Integer>>();
				for (List<Integer> l : currStartConfig) {
					currConfigToAdd.add(new ArrayList<Integer>(l));
				}
				configs.add(currConfigToAdd);
				availableCovered.add(availableInd);
//				System.out.println(currConfigToAdd + " added as startConfig");
			}

			// Determine the highest and lowest note index in the current startConfig and 
			// determine the permutations
			int indHi = -1;
			for (int j = 0; j < firstChord.size(); j ++) {
				if (firstChord.get(j) != null) {
					indHi = j;
					break;
				}
			}
			int indLow = -1;
			for (int j = firstChord.size() - 1; j >= 0; j--) {
				if (firstChord.get(j) != null) {
					indLow = j;
					break;
				}
			}
			while (indLow >= indHi) {
				// For each new indLow, reset currConfig to currStartConfig
				List<List<Integer>> currConfig = new ArrayList<List<Integer>>();
				for (List<Integer> l : currStartConfig) {
					currConfig.add(new ArrayList<Integer>(l));
				}
//				System.out.println("indLow = " + indLow);
//				System.out.println(currConfig);
				// Swap the note at indLow with all available indices
				int indLowSwap = indLow;
				List<Integer> availableIndSwap = new ArrayList<Integer>(availableInd);
				for (int j = 0; j < availableIndSwap.size(); j++) {
					int indAv = availableIndSwap.get(j);
					for (List<Integer> l : currConfig) {
						Collections.swap(l, indLowSwap, indAv);
						// The list must be in descending order
						List<Integer> pitches = new ArrayList<Integer>();
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								pitches.add(l.get(k));
							}
						}
						Collections.sort(pitches);
						Collections.reverse(pitches);
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								l.set(k, pitches.get(0));
								pitches.remove(0);
							}
						}
					}
					// Add currConfig to configs (only if it has not been added before)
					availableIndSwap.set(j, indLowSwap);
					List<Integer> sorted = new ArrayList<Integer>(availableIndSwap);
					Collections.sort(sorted);
					if (!availableCovered.contains(sorted)) {
//						System.out.println(currConfig + " added");
						List<List<Integer>> currConfigToAdd = new ArrayList<List<Integer>>();
						for (List<Integer> l : currConfig) {
							currConfigToAdd.add(new ArrayList<Integer>(l));
						}
						configs.add(currConfigToAdd);
						availableCovered.add(new ArrayList<Integer>(sorted));
					}
					indLowSwap = indAv;
				}
				// If indLow is indHi: break and go to next startConfig
				if (indLow == indHi) {
					break;
				}
				// If not: determine next indLow 
				else {
					for (int j = indLow-1; j >= 0; j--) {
						if (firstChord.get(j) != null) {
							indLow = j;
							break;
						}
					}
				}
			}

			// Make next startConfig
			for (List<Integer> l : currStartConfig) {
				l.add(0, l.get(l.size() - 1));
				l.remove(l.size()-1);
			}
		}
		return configs;
	}


	/**
	 * Calculates the cost of connecting the left and right chords. 
	 * 
	 * The cost for a configuration of left and right chords is calculated as follows:
	 * 
	 * l1    l3    l5    | r1    r4    r7
	 * l2    l4    l6    | r2    r5    r8
	 * null  null  null  | r3    r6    r9
	 * 
	 * cost = ( |l1-r1| + |l1-r4| + |l1-r7| ) + ( |l2-r2| + |l2-r5| + |l2-r8| )
	 *        +   
	 *        ( |l3-r1| + |l3-r4| + |l3-r7| ) + ( |l4-r2| + |l4-r5| + |l4-r8| )
	 *        +
	 *        ( |l5-r1| + |l5-r4| + |l5-r7| ) + ( |l6-r2| + |l6-r5| + |l6-r8| )
	 *        
	 * @param leftChords
	 * @param rightChords
	 * @param useLinear 
	 * @return
	 */
	// TESTED
	static int calculateConfigCost(List<List<Integer>> leftChords, List<List<Integer>> rightChords,
		boolean useLinear) {
		int costNonLinear = 0;

		for (List<Integer> lc : leftChords) {
			for (List<Integer> rc : rightChords) {
				for (int j = 0; j < lc.size(); j++) {
					if (lc.get(j) != null) {
						costNonLinear += Math.abs(rc.get(j) - lc.get(j));	
					}
				}
			}
		}

		List<List<Integer>> lAndR = new ArrayList<List<Integer>>();
		lAndR.addAll(leftChords);
		lAndR.addAll(rightChords);
		List<Integer> firstChord = lAndR.get(0);
		int costLinear = 0;
		for (int i = 0; i < firstChord.size(); i++) {
			// For each chord in lAndR
			for (int j = 0; j < lAndR.size() - 1; j++) {
				if (firstChord.get(i) != null) {
					List<Integer> ch = lAndR.get(j);
					List<Integer> nextCh = lAndR.get(j+1);
					int diff = nextCh.get(i) - ch.get(i);
					costLinear += Math.abs(diff);
				}
			}
		}

		if (!useLinear) {
			return costNonLinear;
		}
		else {
			return costLinear;
		}
	}


	/**
	 * Determines the sequence of voice entries, using pitch information only. 
	 * 
	 * At each point of density increase, the pitches in the last n left chords (i.e., 
	 * with the previous density) and those in the first n right chords (i.e., with the
	 * increased density) are listed. For each left chord, each configuration of that chord 
	 * is then aligned with each right chord, and the cost of the configuration is 
	 * calculated and added to the accumulated cost for that configuration. The accumulated
	 * cost thus contains the total cost per configuration for all left-right combinations. 
	 * The cost of a configuration is determined by the sum of the melodic movement (in 
	 * semitones) per 'entry layer'. 
	 * 
	 * The configurations are defined as follows (l = left pitch; r = right pitch;
	 * null = placeholder for empty voice)
	 * 1. Transition to 2vv (previous chord has 1 pitch, current chord 2)
	 * config 0: [l0, null] : [r0, r1]
	 * config 1: [null, l0] : [r0, r1]
	 *
	 * 2. Transition to 3vv (previous chord has 2 pitches, current chord 3)
	 * config 0: [l0, l1, null] : [r0, r1, r2]
	 * config 1: [l0, null, l1] : [r0, r1, r2]
	 * config 2: [null, l0, l1] : [r0, r1, r2]
	 * 
	 * 3. Transition to 4vv (previous chord has 3 pitches, current chord 4)
	 * config 0: [l0, l1, l2, null] : [r0, r1, r2, r3]
	 * config 1: [l0, l1, null, l2] : [r0, r1, r2, r3]
	 * config 2: [l0, null, l1, l2] : [r0, r1, r2, r3]
	 * config 3: [null, l0, l1, l2] : [r0, r1, r2, r3]
	 * 
	 * 4. Transition to 5vv (previous chord has 4 pitches, current chord 5)
	 * config 0: [l0, l1, l2, l3, null] : [r0, r1, r2, r3, r4]
	 * config 1: [l0, l1, l2, null, l3] : [r0, r1, r2, r3, r4]
	 * config 2: [l0, l1, null, l2, l3] : [r0, r1, r2, r3, r4]
	 * config 3: [l0, null, l1, l2, l3] : [r0, r1, r2, r3, r4]
	 * config 4: [null, p0, p1, p2, p3] : [c0, c1, c2, c3, c4]
	 * 
	 * Examples:
	 * Costs of transition from one to two voices:
	 * left chord = [62], right chord = [67, 60]: cost per configuration = [2, 5] (2 to
	 * connect to lower layer; 5 to connect to upper layer) 
	 * 
	 * Cost of transition from two to three voices: 
	 * left chord = [67, 60], right chord = [67, 59, 55]: cost per configuration = 
	 * [0+1,0+5, 8+5] (1 to connect to upper two layers; 5 to connect to upper and lower
	 * layer; 13 to connect to lower two layers)
	 * 
	 * NB: The following assumptions are made: 
	 *     (1) there are no voice crossings at the points of density increase
	 *     (2) the voices enter successively, one by one;
	 *     (3) the piece is always fully textured when the voices are entering
	 *     (4) at a point of density increase, melodic movement is as small as 
	 *         possible (i.e., the pitch with which the existing voice continues is
	 *         closer to the last pitch in that voice than the pitch with which the 
	 *         newly entering voice starts) 
	 *         
	 * @param highestNumVoices
	 * @param n	Determines the size of the decision window
	 * @param useAverage If <code>true</code>, the averages of the left n chords and the right n chords
	 *        are calculated, and those two chords are compared. 
	 * @return A list of lists, containing <br>
	 *         as element 0-(numTransitions-1): the total accumulated cost for each configuration
	 *                                          per transition <r>
	 *         as element numTransitions: the optimal configuration <br>
	 *         as element numTransitions+1: the optimal entries 
	 */
	// TESTED
	List<List<Double>> getVoiceEntriesOLDEST(int highestNumVoices, int n, boolean useAverage) {
		Integer[][] bnp = getBasicNoteProperties();		
//		FeatureGenerator fg = new FeatureGenerator();
		List<Integer> noteDensities = getNoteDensity(null, null, bnp); // in 2020
		
		List<List<Double>> res = new ArrayList<List<Double>>();

		int leftDensity = 1; //noteDensities.get(0);
		List<Double> optimalConfigs = new ArrayList<Double>();
		
		List<Integer> lowestNoteIndicesFirstChords = new ArrayList<Integer>();

		int leftInd = 0; // the index of the first note with the current density
		int middleInd; // the index of the first note with the increased density 
		int rightInd; // the index of the first note with the twice-increased density 
		for (int i = 0; i < noteDensities.size(); i++) {
			int density = noteDensities.get(i);
			int densityIncr = density - leftDensity;
			// Non-stepwise density increase
			if ((i == 0 && density > 1) || (i > 0 && densityIncr > 1)) {
				System.out.println("i = " + i);
				System.out.println("GRRRRRRRR");
				// Add -1 for each skipped density increase
				for (int j = 0; j < densityIncr; j++) {
					optimalConfigs.add(-1.0);
				}
				leftDensity = density;
			}
			// Stepwise density increase: determine the lowest-cost configuration and add
			// it to optimalConfigs
			else if (i != 0 && densityIncr == 1) { //if (density > leftDensity) {
				System.out.println("i = " + i);
				middleInd = i;
				rightInd = noteDensities.indexOf(density + 1);
				if (rightInd == -1) {
					rightInd = bnp.length;
				}
//				currDensity++;
				leftDensity = density;

				// 1. List all left chords (i.e., those with the previous density)
				List<List<Integer>> leftChords = new ArrayList<List<Integer>>();
				for (int j = leftInd; j < middleInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
					if (pitchesInChord.size() == density-1) {
						// Add placeholder
						pitchesInChord.add(null);
						leftChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
//				System.out.println("leftChords = " + leftChords);
//				for (List<Integer> l : leftChords) {
//					System.out.println(l);
//				}
				
				// 2. List all right chords (i.e., those with the current density)
				List<List<Integer>> rightChords = new ArrayList<List<Integer>>();
				for (int j = middleInd; j < rightInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
					// Add only those chords with the current density
					if (pitchesInChord.size() == density) {
						rightChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
//				System.out.println("rightChords = " + rightChords);
//				for (List<Integer> l : rightChords) {
//					System.out.println(l);
//				}
				
				// Make average left and right chord and double lists
				List<List<Integer>> partialLeft;	
				if (n > leftChords.size()) {
					partialLeft = new ArrayList<List<Integer>>(leftChords);
				}
				else {
					partialLeft = leftChords.subList(leftChords.size()-n, leftChords.size()); 
				}
				List<List<Integer>> partialRight;
				if (n > rightChords.size()) {
					partialRight = new ArrayList<List<Integer>>(rightChords);
				}
				else {
					partialRight = rightChords.subList(0, n);
				}
				List<Double> avgLeftChord = new ArrayList<Double>();
				List<Double> avgRightChord = new ArrayList<Double>();
				List<List<Double>> leftChordsDbl = new ArrayList<List<Double>>();
				List<List<Double>> rightChordsDbl = new ArrayList<List<Double>>();
				List<List<List<Integer>>> leftAndRightChords = new ArrayList<List<List<Integer>>>();
				leftAndRightChords.add(partialLeft);	
				leftAndRightChords.add(partialRight);
				for (int j = 0; j < leftAndRightChords.size(); j++) {
					List<List<Integer>> lOrR = leftAndRightChords.get(j);
					Integer[] sums = new Integer[leftDensity];
					Arrays.fill(sums, 0);
					// For each chord
					for (List<Integer> l : lOrR) {
						List<Double> lAsDbl = new ArrayList<Double>();
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								sums[k] += l.get(k);
								lAsDbl.add((double) l.get(k));
							}
							else {
								lAsDbl.add(null);
							}
						}
						if (j == 0) {
							leftChordsDbl.add(lAsDbl);
						}
						else {
							rightChordsDbl.add(lAsDbl);
						}
					}
					for (double d : sums) {
						if (j == 0) {
							avgLeftChord.add(d / partialLeft.size());
						}
						else {
							avgRightChord.add(d / partialRight.size());
						}
					}
					if (j == 0) {
						avgLeftChord.set(avgLeftChord.size() - 1, null);
					}
				}
				if (useAverage) {
					leftChordsDbl = new ArrayList<List<Double>>();
					leftChordsDbl.add(avgLeftChord);
					rightChordsDbl = new ArrayList<List<Double>>();
					rightChordsDbl.add(avgRightChord);
				}

				// 3. For each left chord: align each configuration of the chord with
				// each right chord, determine the cost of that configuration, and add it
				// to the accumulated cost for that configuration
				List<Double> accumCostPerConfigCurrTransition = new ArrayList<Double>();	
				for (int j = 0; j < leftDensity; j++) {
					accumCostPerConfigCurrTransition.add(0.0);
				}

				for (List<Double> leftChord : leftChordsDbl) {
//					List<Double> leftPitches = avgLeftChord;
					for (List<Double> rightChord : rightChordsDbl) {
						// Copy needed when rightChordsDbl contains more than one element
						List<Double> copyOfLeftChord = new ArrayList<Double>(leftChord);
						List<Double> costCurrChordComb = new ArrayList<Double>();
						int config = 0;
						int sizeCurrChord = rightChord.size();
					
						// Number of configurations = number of notes in the current chord
						while (config < sizeCurrChord) {
							// Sum pitch motion per voice 
							double cost = 0;
							for (int j = 0; j < sizeCurrChord; j++) {
								if (copyOfLeftChord.get(j) != null) {
									double c = 
										Math.abs(rightChord.get(j)-copyOfLeftChord.get(j));
									cost += c;
								}
							}
							costCurrChordComb.add(cost);

							// Last configuration? Break
							if (config == sizeCurrChord - 1) {
								break;
							}
							// Else, shift placeholder one position back and do next config 
							else {
								int swapInd = copyOfLeftChord.indexOf(null);	
								Collections.swap(copyOfLeftChord, swapInd, swapInd-1);
								config++;
							}
						}
						// Add costs for the current left chord-right chord combination
						for (int j = 0; j < costCurrChordComb.size(); j++) { 
							accumCostPerConfigCurrTransition.set(j, 
								accumCostPerConfigCurrTransition.get(j) + 
								costCurrChordComb.get(j));
						}
					}
				}

				leftInd = middleInd;

//				String close = "\t";
//				if (density == maxDensity) {
//					close = "\n";
//				}
//				String a = accumCostPerConfigCurrTransition.toString();
//				a = a.replace(",", "\t");
//				TranscriptionTest.optCost +=
//					a.substring(a.indexOf("[")+1, a.indexOf("]")) + close;
				
				res.add(accumCostPerConfigCurrTransition);

				// 4. Add the lowest-cost configuration to the list
				double optimalConfig = Collections.min(accumCostPerConfigCurrTransition);
				if (Collections.frequency(accumCostPerConfigCurrTransition, optimalConfig) > 1) {	
//					throw new RuntimeException("More than one configuration yield the "
//						+ "lowest cost: " + accumulatedCostPerConfig); 
				}
				optimalConfigs.add((double) accumCostPerConfigCurrTransition.indexOf(optimalConfig));

				if (density == highestNumVoices) {
					break;
				}
			}
		}
		res.add(optimalConfigs);
				
		// Map all possible combinations of configurations to voicings (L=lower, M=middle, H=higher, 
		// l=lower, u=upper)
		Map<List<Double>, Double[]> dict = new LinkedHashMap<List<Double>, Double[]>();
		// 3vv map
		dict.put(Arrays.asList(new Double[]{0.0, 0.0}), new Double[]{0.0, 1.0, 2.0}); // SAB (LL)
		dict.put(Arrays.asList(new Double[]{0.0, 1.0}), new Double[]{0.0, 2.0, 1.0}); // SBA (LM)
		dict.put(Arrays.asList(new Double[]{0.0, 2.0}), new Double[]{1.0, 2.0, 0.0}); // ABS (LH)
		dict.put(Arrays.asList(new Double[]{1.0, 0.0}), new Double[]{1.0, 0.0, 2.0}); // ASB (HL)
		dict.put(Arrays.asList(new Double[]{1.0, 1.0}), new Double[]{2.0, 0.0, 1.0}); // BSA (HM)
		dict.put(Arrays.asList(new Double[]{1.0, 2.0}), new Double[]{2.0, 1.0, 0.0}); // BAS (HH)
		// 4vv 
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 0.0}), new Double[]{0.0, 1.0, 2.0, 3.0}); // SATB
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 1.0}), new Double[]{0.0, 1.0, 3.0, 2.0}); // SABT
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 2.0}), new Double[]{0.0, 2.0, 3.0, 1.0}); // STBA
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 3.0}), new Double[]{1.0, 2.0, 3.0, 0.0}); // ATBS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 0.0}), new Double[]{0.0, 2.0, 1.0, 3.0}); // STAB
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 1.0}), new Double[]{0.0, 3.0, 1.0, 2.0}); // SBAT
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 2.0}), new Double[]{0.0, 3.0, 2.0, 1.0}); // SBTA
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 3.0}), new Double[]{1.0, 3.0, 2.0, 0.0}); // ABTS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 0.0}), new Double[]{1.0, 2.0, 0.0, 3.0}); // ATSB
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 1.0}), new Double[]{1.0, 3.0, 0.0, 2.0}); // ABST
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 2.0}), new Double[]{2.0, 3.0, 0.0, 1.0}); // TBSA
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 3.0}), new Double[]{2.0, 3.0, 1.0, 0.0}); // TBAS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 0.0}), new Double[]{1.0, 0.0, 2.0, 3.0}); // ASTB
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 1.0}), new Double[]{1.0, 0.0, 3.0, 2.0}); // ASBT
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 2.0}), new Double[]{2.0, 0.0, 3.0, 1.0}); // TSBA
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 3.0}), new Double[]{2.0, 1.0, 3.0, 0.0}); // TABS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 0.0}), new Double[]{2.0, 0.0, 1.0, 3.0}); // TSAB
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 1.0}), new Double[]{3.0, 0.0, 1.0, 2.0}); // BSAT
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 2.0}), new Double[]{3.0, 0.0, 2.0, 1.0}); // BSTA
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 3.0}), new Double[]{3.0, 1.0, 2.0, 0.0}); // BATS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 0.0}), new Double[]{2.0, 1.0, 0.0, 3.0}); // TASB
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 1.0}), new Double[]{3.0, 1.0, 0.0, 2.0}); // BAST
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 2.0}), new Double[]{3.0, 2.0, 0.0, 1.0}); // BTSA
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 3.0}), new Double[]{3.0, 2.0, 1.0, 0.0}); // BTAS
		// 5vv
		
		// Determine the voicing that goes with the determined optimal configurations
		List<Integer> optimalConfigs2 = new ArrayList<Integer>();
		for (double d : optimalConfigs) {
			optimalConfigs2.add((int) d);
		}
//		TranscriptionTest.opt += 
//			optimalConfigs2.toString().substring(optimalConfigs2.toString().indexOf("[")+1,
//			optimalConfigs2.toString().indexOf("]")) + "\n";
		System.out.println(optimalConfigs);
		res.add(Arrays.asList(dict.get(optimalConfigs)));
		return res;
	}


	List<List<Double>> getVoiceEntriesOLDER_EXT(int highestNumVoices, int n, boolean useAverage) {
		Integer[][] bnp = getBasicNoteProperties();
		List<Integer> noteDensities = getNoteDensity(null, null, bnp); // in 2020

		List<List<Double>> res = new ArrayList<List<Double>>();

		int leftDensity = noteDensities.get(0);
		List<Double> optimalConfigs = new ArrayList<Double>();
		List<Integer[]> densityIncreases = new ArrayList<Integer[]>();
		
		List<Integer> lowestNoteIndicesFirstChords = new ArrayList<Integer>();

		int leftInd = 0; // the index of the first note with the current density
		int middleInd; // the index of the first note with the increased density 
		int rightInd; // the index of the first note with the twice-increased density 
		for (int i = 0; i < noteDensities.size(); i++) {
			int density = noteDensities.get(i);
			int densityIncr = density - leftDensity;
//			// Non-stepwise density increase
//			if ((i == 0 && density > 1) || (i > 0 && densityIncr > 1)) {
//				System.out.println("i = " + i);
//				System.out.println("GRRRRRRRR");
//				// Add -1 for each skipped density increase
//				for (int j = 0; j < densityIncr; j++) {
//					optimalConfigs.add(-1.0);
//				}
//				leftDensity = density;
//			}
//			// Stepwise density increase: determine the lowest-cost configuration and add
//			// it to optimalConfigs
//			else if (i != 0 && densityIncr == 1) { //if (density > leftDensity) {
			if (density > leftDensity) {
				Integer[] currDensityIncrease = new Integer[]{leftDensity, density};
				densityIncreases.add(currDensityIncrease);

				System.out.println("i = " + i);
				middleInd = i;
				rightInd = noteDensities.indexOf(density + 1);
				if (rightInd == -1) {
					rightInd = bnp.length;
				}
//				currDensity++;
				leftDensity = density;

				// 1. List all left chords (i.e., those with the previous density)
				List<List<Integer>> leftChords = new ArrayList<List<Integer>>();
				for (int j = leftInd; j < middleInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
//					if (pitchesInChord.size() == density-1) {
					if (pitchesInChord.size() < density) {
						// Add placeholder(s)
						while (pitchesInChord.size() < density) {
							pitchesInChord.add(null);
						}
						leftChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
				System.out.println("leftChords = " + leftChords);
				for (List<Integer> l : leftChords) {
					System.out.println(l);
				}

				// 2. List all right chords (i.e., those with the current density)
				List<List<Integer>> rightChords = new ArrayList<List<Integer>>();
				for (int j = middleInd; j < rightInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
					// Add only those chords with the current density
					if (pitchesInChord.size() == density) {
						rightChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
//				System.out.println("rightChords = " + rightChords);
//				for (List<Integer> l : rightChords) {
//					System.out.println(l);
//				}
				
				// Make average left and right chord and double lists
				List<List<Integer>> partialLeft;	
				if (n > leftChords.size()) {
					partialLeft = new ArrayList<List<Integer>>(leftChords);
				}
				else {
					partialLeft = leftChords.subList(leftChords.size()-n, leftChords.size()); 
				}
				List<List<Integer>> partialRight;
				if (n > rightChords.size()) {
					partialRight = new ArrayList<List<Integer>>(rightChords);
				}
				else {
					partialRight = rightChords.subList(0, n);
				}
				List<Double> avgLeftChord = new ArrayList<Double>();
				List<Double> avgRightChord = new ArrayList<Double>();
				List<List<Double>> leftChordsDbl = new ArrayList<List<Double>>();
				List<List<Double>> rightChordsDbl = new ArrayList<List<Double>>();
				List<List<List<Integer>>> leftAndRightChords = new ArrayList<List<List<Integer>>>();
				leftAndRightChords.add(partialLeft);	
				leftAndRightChords.add(partialRight);
				for (int j = 0; j < leftAndRightChords.size(); j++) {
					List<List<Integer>> lOrR = leftAndRightChords.get(j);
					Integer[] sums = new Integer[leftDensity];
					Arrays.fill(sums, 0);
					// For each chord
					for (List<Integer> l : lOrR) {
						List<Double> lAsDbl = new ArrayList<Double>();
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								sums[k] += l.get(k);
								lAsDbl.add((double) l.get(k));
							}
							else {
								lAsDbl.add(null);
							}
						}
						if (j == 0) {
							leftChordsDbl.add(lAsDbl);
						}
						else {
							rightChordsDbl.add(lAsDbl);
						}
					}
					for (double d : sums) {
						if (j == 0) {
							avgLeftChord.add(d / partialLeft.size());
						}
						else {
							avgRightChord.add(d / partialRight.size());
						}
					}
					if (j == 0) {
						avgLeftChord.set(avgLeftChord.size() - 1, null);
					}
				}
				if (useAverage) {
					leftChordsDbl = new ArrayList<List<Double>>();
					leftChordsDbl.add(avgLeftChord);
					rightChordsDbl = new ArrayList<List<Double>>();
					rightChordsDbl.add(avgRightChord);
				}

				// 3. For each left chord: align each configuration of the chord with
				// each right chord, determine the cost of that configuration, and add it
				// to the accumulated cost for that configuration
				List<Double> accumCostPerConfigCurrTransition = new ArrayList<Double>();	
				for (int j = 0; j < leftDensity; j++) {
					accumCostPerConfigCurrTransition.add(0.0);
				}

				for (List<Double> leftChord : leftChordsDbl) {
//					List<Double> leftPitches = avgLeftChord;
					for (List<Double> rightChord : rightChordsDbl) {
						// Copy needed when rightChordsDbl contains more than one element
						List<Double> copyOfLeftChord = new ArrayList<Double>(leftChord);
						List<Double> costCurrChordComb = new ArrayList<Double>();
						int config = 0;
						int sizeCurrChord = rightChord.size();
						// Number of configurations = number of notes in the current chord
						int numConfigs = sizeCurrChord;
						// If the left chord contains more than one note, the density 
						// increase is 2 or more and the right chord contains at least
						// two more notes than the left chord (2-4; 2-5; 3-5)
						if (currDensityIncrease[1] != 1 && (currDensityIncrease[1] - 
							currDensityIncrease[0] >= 2)) {
							numConfigs = ToolBox.factorial(currDensityIncrease[1] - 1);
						}
						int indOfHighestNonNull = 0;
						int numNull = Collections.frequency(copyOfLeftChord, null);
						int indOfLowestNonNull = (copyOfLeftChord.size() - numNull) - 1;
						List<Integer> indOfMiddleNonNull = new ArrayList<Integer>();
						
						while (config < numConfigs) {
							// Sum pitch motion per voice 
							double cost = 0;
							for (int j = 0; j < sizeCurrChord; j++) {
								if (copyOfLeftChord.get(j) != null) {
									double c = 
										Math.abs(rightChord.get(j)-copyOfLeftChord.get(j));
									cost += c;
								}
							}
							costCurrChordComb.add(cost);

							// Determine next configuration
//							int numNull = Collections.frequency(copyOfLeftChord, null);
							// If the density increase is 1 (1-2; 2-3; 3-4) or if the left chord
							// contains only one note and the right more than two (1-3; 1-4; 1-5)
							if (numNull == 1 || (numNull > 1 && numNull == leftChord.size() - 1)) {
								// Last configuration? Break
//								if (config == sizeCurrChord - 1) {
								if (config == numConfigs - 1) {
									break;
								}
								else {
									// Density increase 1: shift placeholder back one position 
									if (numNull == 1) {
										int swapInd = copyOfLeftChord.indexOf(null);	
										Collections.swap(copyOfLeftChord, swapInd, swapInd-1);
										config++;
									}
									// Left chord contains one note: shift note forward one position 
									else if (numNull == leftChord.size() - 1) {
										int swapInd = config;
//										for (int j = 0; j < copyOfLeftChord.size(); j++) {
//											if (copyOfLeftChord.get(j) != null) {
//												swapInd = j;
//												break;
//											}
//										}
										Collections.swap(copyOfLeftChord, swapInd, swapInd+1);
										config++;
									}
								} 
							}
							// If the density increase is 2 or more and the right chord contains 
							// at least two more notes than the left chord (2-4; 2-5; 3-5)
							else {
								if (config == numConfigs - 1) {
									break;
								}
								else {
									// If not the last index: swap lowest note with next
									
									
									
									
									
									// If the last index: swap higher
								}
							}
						}
						// Add costs for the current left chord-right chord combination
						for (int j = 0; j < costCurrChordComb.size(); j++) { 
							accumCostPerConfigCurrTransition.set(j, 
								accumCostPerConfigCurrTransition.get(j) + 
								costCurrChordComb.get(j));
						}
					}
				}
				leftInd = middleInd;

//				String close = "\t";
//				if (density == maxDensity) {
//					close = "\n";
//				}
//				String a = accumCostPerConfigCurrTransition.toString();
//				a = a.replace(",", "\t");
//				TranscriptionTest.optCost +=
//					a.substring(a.indexOf("[")+1, a.indexOf("]")) + close;
				
				res.add(accumCostPerConfigCurrTransition);

				// 4. Add the lowest-cost configuration to the list
				double optimalConfig = Collections.min(accumCostPerConfigCurrTransition);
				if (Collections.frequency(accumCostPerConfigCurrTransition, optimalConfig) > 1) {	
//					throw new RuntimeException("More than one configuration yield the "
//						+ "lowest cost: " + accumulatedCostPerConfig); 
				}
				optimalConfigs.add((double) accumCostPerConfigCurrTransition.indexOf(optimalConfig));

				if (density == highestNumVoices) {
					break;
				}
			}
		}
		res.add(optimalConfigs);
				
		// Map all possible combinations of configurations to voicings (L=lower, M=middle, H=higher, 
		// l=lower, u=upper)
		Map<List<Double>, Double[]> dict = new LinkedHashMap<List<Double>, Double[]>();
		// 3vv map
		dict.put(Arrays.asList(new Double[]{0.0, 0.0}), new Double[]{0.0, 1.0, 2.0}); // SAB (LL)
		dict.put(Arrays.asList(new Double[]{0.0, 1.0}), new Double[]{0.0, 2.0, 1.0}); // SBA (LM)
		dict.put(Arrays.asList(new Double[]{0.0, 2.0}), new Double[]{1.0, 2.0, 0.0}); // ABS (LH)
		dict.put(Arrays.asList(new Double[]{1.0, 0.0}), new Double[]{1.0, 0.0, 2.0}); // ASB (HL)
		dict.put(Arrays.asList(new Double[]{1.0, 1.0}), new Double[]{2.0, 0.0, 1.0}); // BSA (HM)
		dict.put(Arrays.asList(new Double[]{1.0, 2.0}), new Double[]{2.0, 1.0, 0.0}); // BAS (HH)
		// 4vv 
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 0.0}), new Double[]{0.0, 1.0, 2.0, 3.0}); // SATB
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 1.0}), new Double[]{0.0, 1.0, 3.0, 2.0}); // SABT
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 2.0}), new Double[]{0.0, 2.0, 3.0, 1.0}); // STBA
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 3.0}), new Double[]{1.0, 2.0, 3.0, 0.0}); // ATBS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 0.0}), new Double[]{0.0, 2.0, 1.0, 3.0}); // STAB
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 1.0}), new Double[]{0.0, 3.0, 1.0, 2.0}); // SBAT
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 2.0}), new Double[]{0.0, 3.0, 2.0, 1.0}); // SBTA
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 3.0}), new Double[]{1.0, 3.0, 2.0, 0.0}); // ABTS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 0.0}), new Double[]{1.0, 2.0, 0.0, 3.0}); // ATSB
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 1.0}), new Double[]{1.0, 3.0, 0.0, 2.0}); // ABST
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 2.0}), new Double[]{2.0, 3.0, 0.0, 1.0}); // TBSA
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 3.0}), new Double[]{2.0, 3.0, 1.0, 0.0}); // TBAS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 0.0}), new Double[]{1.0, 0.0, 2.0, 3.0}); // ASTB
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 1.0}), new Double[]{1.0, 0.0, 3.0, 2.0}); // ASBT
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 2.0}), new Double[]{2.0, 0.0, 3.0, 1.0}); // TSBA
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 3.0}), new Double[]{2.0, 1.0, 3.0, 0.0}); // TABS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 0.0}), new Double[]{2.0, 0.0, 1.0, 3.0}); // TSAB
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 1.0}), new Double[]{3.0, 0.0, 1.0, 2.0}); // BSAT
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 2.0}), new Double[]{3.0, 0.0, 2.0, 1.0}); // BSTA
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 3.0}), new Double[]{3.0, 1.0, 2.0, 0.0}); // BATS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 0.0}), new Double[]{2.0, 1.0, 0.0, 3.0}); // TASB
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 1.0}), new Double[]{3.0, 1.0, 0.0, 2.0}); // BAST
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 2.0}), new Double[]{3.0, 2.0, 0.0, 1.0}); // BTSA
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 3.0}), new Double[]{3.0, 2.0, 1.0, 0.0}); // BTAS
		// 5vv
		
		// Determine the voicing that goes with the determined optimal configurations
		List<Integer> optimalConfigs2 = new ArrayList<Integer>();
		for (double d : optimalConfigs) {
			optimalConfigs2.add((int) d);
		}
		for (Integer[] l : densityIncreases) {
			System.out.println(Arrays.asList(l));
		}
//		TranscriptionTest.opt += 
//			optimalConfigs2.toString().substring(optimalConfigs2.toString().indexOf("[")+1,
//			optimalConfigs2.toString().indexOf("]")) + "\n";

		System.out.println(optimalConfigs);
		if (dict.get(optimalConfigs) != null) {
			res.add(Arrays.asList(dict.get(optimalConfigs)));
		}
		else {
			res.add(null);
		}
		return res;
	}


	/**
	 * Determines the sequence of voice entries, using pitch information only. 
	 * 
	 * At each point of density increase, the pitches in the last n left chords (i.e., 
	 * with the previous density) and those in the first n right chords (i.e., with the
	 * increased density) are listed. All n left chords, as a whole in all possible 
	 * configurations, are then aligned with all n right chords as a whole, and the cost 
	 * of each configuration is calculated. The cost of a configuration is determined by 
	 * the sum of the melodic movement (in semitones) per 'entry layer'. 
	 * 
	 * The configurations are defined as follows (l = left pitch; r = right pitch;
	 * null = placeholder for empty voice)
	 * 1. Transition to 2vv (previous chord has 1 pitch, current chord 2)
	 * config 0: [l0, null] : [r0, r1]
	 * config 1: [null, l0] : [r0, r1]
	 *
	 * 2. Transition to 3vv (previous chord has 2 pitches, current chord 3)
	 * config 0: [l0, l1, null] : [r0, r1, r2]
	 * config 1: [l0, null, l1] : [r0, r1, r2]
	 * config 2: [null, l0, l1] : [r0, r1, r2]
	 * 
	 * 3. Transition to 4vv (previous chord has 3 pitches, current chord 4)
	 * config 0: [l0, l1, l2, null] : [r0, r1, r2, r3]
	 * config 1: [l0, l1, null, l2] : [r0, r1, r2, r3]
	 * config 2: [l0, null, l1, l2] : [r0, r1, r2, r3]
	 * config 3: [null, l0, l1, l2] : [r0, r1, r2, r3]
	 * 
	 * 4. Transition to 5vv (previous chord has 4 pitches, current chord 5)
	 * config 0: [l0, l1, l2, l3, null] : [r0, r1, r2, r3, r4]
	 * config 1: [l0, l1, l2, null, l3] : [r0, r1, r2, r3, r4]
	 * config 2: [l0, l1, null, l2, l3] : [r0, r1, r2, r3, r4]
	 * config 3: [l0, null, l1, l2, l3] : [r0, r1, r2, r3, r4]
	 * config 4: [null, p0, p1, p2, p3] : [c0, c1, c2, c3, c4]
	 * 
	 * Examples:
	 * Costs of transition from one to two voices:
	 * left chord = [62], right chord = [67, 60]: cost per configuration = [2, 5] (2 to
	 * connect to lower layer; 5 to connect to upper layer) 
	 * 
	 * Cost of transition from two to three voices: 
	 * left chord = [67, 60], right chord = [67, 59, 55]: cost per configuration = 
	 * [0+1, 0+5, 8+5] (1 to connect to upper two layers; 5 to connect to upper and lower
	 * layer; 13 to connect to lower two layers)
	 * 
	 * NB: The following assumptions are made: 
	 *     (1) there are no voice crossings at the points of density increase
	 *     (2) the voices enter successively, one by one;
	 *     (3) the piece is always fully textured when the voices are entering
	 *     (4) at a point of density increase, melodic movement is as small as 
	 *         possible (i.e., the pitch with which the existing voice continues is
	 *         closer to the last pitch in that voice than the pitch with which the 
	 *         newly entering voice starts) 
	 *         
	 * @param highestNumVoices
	 * @param n	Determines the size of the decision window
	 * @param useAverage If <code>true</code>, the averages of the left n chords and the right n chords
	 *        are calculated, and those two chords are compared. 
	 * @return A list of lists, containing <br>
	 *         as element 0-(numTransitions-1): the cost for each configuration per transition <r>
	 *         as element numTransitions: the optimal configuration <br>
	 *         as element numTransitions+1: the optimal entries 
	 */
	// TESTED
	List<List<Double>> getVoiceEntriesOLD(int highestNumVoices, int n, boolean useAverage) {
		Integer[][] bnp = getBasicNoteProperties();		
//		FeatureGenerator fg = new FeatureGenerator();
		List<Integer> noteDensities = getNoteDensity(null, null, bnp); // in 2020
		
		List<List<Double>> res = new ArrayList<List<Double>>();

		int currDensity = noteDensities.get(0);
		List<Double> optimalConfigs = new ArrayList<Double>();

		int leftInd = 0; // the index of the first note with the current density
		int middleInd; // the index of the first note with the increased density 
		int rightInd; // the index of the first note with the twice-increased density 		
		for (int i = 0; i < noteDensities.size(); i++) {
			int density = noteDensities.get(i);
			// For each density increase: determine the lowest-cost configuration and add
			// it to optimalConfigs
			if (density > currDensity) {
				middleInd = i;
				rightInd = noteDensities.indexOf(density + 1);
				if (rightInd == -1) {
					rightInd = bnp.length;
				}
				currDensity++;

				// 1. List all left chords (i.e., those with the previous density)
				List<List<Integer>> leftChords = new ArrayList<List<Integer>>();
				for (int j = leftInd; j < middleInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
					if (pitchesInChord.size() == density-1) {
						// Add placeholder
						pitchesInChord.add(null);
						leftChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
//				System.out.println("leftChords = " + leftChords);
//				for (List<Integer> l : leftChords) {
//					System.out.println(l);
//				}
				
				// 2. List all right chords (i.e., those with the current density)
				List<List<Integer>> rightChords = new ArrayList<List<Integer>>();
				for (int j = middleInd; j < rightInd; j++) {
					// j is the index of the lowest note in the chord
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches of any sustained previous notess
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
						pitchesInChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(pitchesInChord);
					Collections.reverse(pitchesInChord);
					// Add only those chords with the current density
					if (pitchesInChord.size() == density) {
						rightChords.add(pitchesInChord);
					}
					// Increment j so that the next iteration starts from the next chord
					j = (j + chordSize) - 1;
				}
//				System.out.println("rightChords = " + rightChords);
//				for (List<Integer> l : rightChords) {
//					System.out.println(l);
//				}

				// Make average left and right chord and double lists
				List<List<Integer>> partialLeft;	
				if (n > leftChords.size()) {
					partialLeft = new ArrayList<List<Integer>>(leftChords);
				}
				else {
					partialLeft = leftChords.subList(leftChords.size()-n, leftChords.size()); 
				}
				List<List<Integer>> partialRight;
				if (n > rightChords.size()) {
					partialRight = new ArrayList<List<Integer>>(rightChords);
				}
				else {
					partialRight = rightChords.subList(0, n);
				}
				List<Double> avgLeftChord = new ArrayList<Double>();
				List<Double> avgRightChord = new ArrayList<Double>();
				List<List<Double>> leftChordsDbl = new ArrayList<List<Double>>();
				List<List<Double>> rightChordsDbl = new ArrayList<List<Double>>();
				List<List<List<Integer>>> leftAndRightChords = new ArrayList<List<List<Integer>>>();
				leftAndRightChords.add(partialLeft);	
				leftAndRightChords.add(partialRight);
				for (int j = 0; j < leftAndRightChords.size(); j++) {
					List<List<Integer>> lOrR = leftAndRightChords.get(j);
					Integer[] sums = new Integer[currDensity];
					Arrays.fill(sums, 0);
					// For each chord
					for (List<Integer> l : lOrR) {
						List<Double> lAsDbl = new ArrayList<Double>();
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								sums[k] += l.get(k);
								lAsDbl.add((double) l.get(k));
							}
							else {
								lAsDbl.add(null);
							}
						}
						if (j == 0) {
							leftChordsDbl.add(lAsDbl);
						}
						else {
							rightChordsDbl.add(lAsDbl);
						}
					}
					for (double d : sums) {
						if (j == 0) {
							avgLeftChord.add(d / partialLeft.size());
						}
						else {
							avgRightChord.add(d / partialRight.size());
						}
					}
					if (j == 0) {
						avgLeftChord.set(avgLeftChord.size() - 1, null);
					}
				}
				if (useAverage) {
					leftChordsDbl = new ArrayList<List<Double>>();
					leftChordsDbl.add(avgLeftChord);
					rightChordsDbl = new ArrayList<List<Double>>();
					rightChordsDbl.add(avgRightChord);
				}

				// 3. Align each configuration of all left chords with all right chords
				List<Double> costPerConfigCurrTransition = new ArrayList<Double>();
				int config  = 0;
				// Number of configurations = number of notes in the current chord
				while (config < density) {
					List<List<Double>> lAndR = new ArrayList<List<Double>>();
					lAndR.addAll(leftChordsDbl);
					lAndR.addAll(rightChordsDbl);
					List<Double> firstChord = lAndR.get(0);
					// For each configuration: sum the movement over all voices that are not 
					// null in left chord 
					double costCurrConfig = 0;
					for (int j = 0; j < density; j++) {
						for (int k = 0; k < lAndR.size() - 1; k++) {
							if (firstChord.get(j) != null) {
								List<Double> ch = lAndR.get(k);
								List<Double> nextCh = lAndR.get(k+1);
								double diff = nextCh.get(j) - ch.get(j);
								costCurrConfig += Math.abs(diff);
							}
						}
					}
					costPerConfigCurrTransition.add(Math.abs(costCurrConfig));
					
					// Last configuration? Reinstate prevPitches and break
					if (config == density - 1) {
						break;
					}
					// Else, shift placeholder one position back and do next config 
					else {
						int swapInd = firstChord.indexOf(null);
						for (List<Double> ch : leftChordsDbl) {
							Collections.swap(ch, swapInd, swapInd-1);
						}
						config++;
					}
				}

				leftInd = middleInd;

//				String close = "\t";
//				if (density == maxDensity) {
//					close = "\n";
//				}
//				String a = costPerConfigCurrTransition.toString();
//				a = a.replace(",", "\t");
//				TranscriptionTest.optCost +=
//					a.substring(a.indexOf("[")+1, a.indexOf("]")) + close;
				
				res.add(costPerConfigCurrTransition);
				
				// 4. Add the lowest-cost configuration to the list
				double optimalConfig = Collections.min(costPerConfigCurrTransition);
				if (Collections.frequency(costPerConfigCurrTransition, optimalConfig) > 1) {	
//					throw new RuntimeException("More than one configuration yield the "
//						+ "lowest cost: " + accumulatedCostPerConfig); 
				}
				optimalConfigs.add((double) costPerConfigCurrTransition.indexOf(optimalConfig));

				if (density == highestNumVoices) {
					break;
				}
			}
		}
		res.add(optimalConfigs);

		// Map all possible combinations of configurations to voicings (L=lower, M=middle, H=higher, 
		// l=lower, u=upper)
		Map<List<Double>, Double[]> dict = new LinkedHashMap<List<Double>, Double[]>();
		// 3vv map
		dict.put(Arrays.asList(new Double[]{0.0, 0.0}), new Double[]{0.0, 1.0, 2.0}); // SAB (LL)
		dict.put(Arrays.asList(new Double[]{0.0, 1.0}), new Double[]{0.0, 2.0, 1.0}); // SBA (LM)
		dict.put(Arrays.asList(new Double[]{0.0, 2.0}), new Double[]{1.0, 2.0, 0.0}); // ABS (LH)
		dict.put(Arrays.asList(new Double[]{1.0, 0.0}), new Double[]{1.0, 0.0, 2.0}); // ASB (HL)
		dict.put(Arrays.asList(new Double[]{1.0, 1.0}), new Double[]{2.0, 0.0, 1.0}); // BSA (HM)
		dict.put(Arrays.asList(new Double[]{1.0, 2.0}), new Double[]{2.0, 1.0, 0.0}); // BAS (HH)
		// 4vv 
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 0.0}), new Double[]{0.0, 1.0, 2.0, 3.0}); // SATB
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 1.0}), new Double[]{0.0, 1.0, 3.0, 2.0}); // SABT
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 2.0}), new Double[]{0.0, 2.0, 3.0, 1.0}); // STBA
		dict.put(Arrays.asList(new Double[]{0.0, 0.0, 3.0}), new Double[]{1.0, 2.0, 3.0, 0.0}); // ATBS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 0.0}), new Double[]{0.0, 2.0, 1.0, 3.0}); // STAB
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 1.0}), new Double[]{0.0, 3.0, 1.0, 2.0}); // SBAT
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 2.0}), new Double[]{0.0, 3.0, 2.0, 1.0}); // SBTA
		dict.put(Arrays.asList(new Double[]{0.0, 1.0, 3.0}), new Double[]{1.0, 3.0, 2.0, 0.0}); // ABTS
		//
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 0.0}), new Double[]{1.0, 2.0, 0.0, 3.0}); // ATSB
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 1.0}), new Double[]{1.0, 3.0, 0.0, 2.0}); // ABST
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 2.0}), new Double[]{2.0, 3.0, 0.0, 1.0}); // TBSA
		dict.put(Arrays.asList(new Double[]{0.0, 2.0, 3.0}), new Double[]{2.0, 3.0, 1.0, 0.0}); // TBAS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 0.0}), new Double[]{1.0, 0.0, 2.0, 3.0}); // ASTB
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 1.0}), new Double[]{1.0, 0.0, 3.0, 2.0}); // ASBT
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 2.0}), new Double[]{2.0, 0.0, 3.0, 1.0}); // TSBA
		dict.put(Arrays.asList(new Double[]{1.0, 0.0, 3.0}), new Double[]{2.0, 1.0, 3.0, 0.0}); // TABS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 0.0}), new Double[]{2.0, 0.0, 1.0, 3.0}); // TSAB
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 1.0}), new Double[]{3.0, 0.0, 1.0, 2.0}); // BSAT
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 2.0}), new Double[]{3.0, 0.0, 2.0, 1.0}); // BSTA
		dict.put(Arrays.asList(new Double[]{1.0, 1.0, 3.0}), new Double[]{3.0, 1.0, 2.0, 0.0}); // BATS
		//
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 0.0}), new Double[]{2.0, 1.0, 0.0, 3.0}); // TASB
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 1.0}), new Double[]{3.0, 1.0, 0.0, 2.0}); // BAST
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 2.0}), new Double[]{3.0, 2.0, 0.0, 1.0}); // BTSA
		dict.put(Arrays.asList(new Double[]{1.0, 2.0, 3.0}), new Double[]{3.0, 2.0, 1.0, 0.0}); // BTAS
		// 5vv
		
		// Determine the voicing that goes with the determined optimal configurations
//		List<Integer> optimalConfigs2 = new ArrayList<Integer>();
//		for (double d : optimalConfigs) {
//			optimalConfigs2.add((int) d);
//		}
//		TranscriptionTest.opt += 
//			optimalConfigs2.toString().substring(optimalConfigs2.toString().indexOf("[")+1,
//			optimalConfigs2.toString().indexOf("]")) + "\n";
		
		
		res.add(Arrays.asList(dict.get(optimalConfigs)));
		return res;
	}


	/**
	 * Determines the next configuration of the notes in each chord in the given list of chords.
	 * 
	 * @param config
	 * @param numConfigs
	 * @param leftDensity
	 * @param rightDensity
	 * @param partialLeft
	 * @return
	 */
	private static List<List<Integer>> determineNextConfig(int config, int numConfigs, int leftDensity, 
		int rightDensity, List<List<Integer>> partialLeft) {
		
		List<List<Integer>> partialLeftReordered = new ArrayList<List<Integer>>();
		for (List<Integer> l : partialLeft) {
			partialLeftReordered.add(new ArrayList<Integer>(l));
		}		
		List<Integer> firstChord = partialLeft.get(0);

		// If the left chord contains one note or one placeholder
		if (numConfigs == rightDensity) {
			int swapInd = -1;
			int swapWithInd = -1;
			// Density increase 1: shift placeholder back one position 
			if (rightDensity - leftDensity == 1) {
				swapInd = firstChord.indexOf(null);
				swapWithInd = swapInd-1;
			}
			// Left chord contains one note: shift note forward one position 
			else if (leftDensity == 1) {	
				swapInd = config;
				swapWithInd = swapInd+1;
			}
			// Swap 
			for (List<Integer> l : partialLeftReordered) {
				Collections.swap(l, swapInd, swapWithInd);
			}
		}
		// If the left chord contains more than one note and the right chord contains
		// at least two more notes than the left chord
		else {
			int indHi = -1;
			for (int i = 0; i < firstChord.size(); i++) {
				if (firstChord.get(i) != null) {
					indHi = i;
					break;
				}
			}
			int indLow = -1;
			for (int i = firstChord.size() - 1; i >= 0; i--) {
				if (firstChord.get(i) != null) {
					indLow = i;
					break;
				}
			}
			// The element at indMid must not be null, but the element at the next index must.
			// If there is no such index, indMid remains -1
			int indMid = -1;
			for (int i = indLow - 1; i > indHi; i--) {
				if (firstChord.get(i) != null && firstChord.get(i+1) == null) {
					indMid = i;
					break;
				}
			}

			int lastInd = rightDensity-1;
			int swapInd = -1;
			int swapWithInd = -1;							
			// If indLow can still be increased
			if (indLow != lastInd) {
				swapInd = indLow;
				swapWithInd = indLow + 1;
				for (List<Integer> l : partialLeftReordered) {
					Collections.swap(l, swapInd, swapWithInd);
				}
			}
			// If indLow can no longer be increased
			else if (indLow == lastInd) {
				// If there is a indMid to increase
				if (indMid != -1) {
					swapInd = indMid;
					swapWithInd = indMid + 1;
					for (List<Integer> l : partialLeftReordered) {
						Collections.swap(l, swapInd, swapWithInd);
					}
				}
				// If not: increment indHi and reorder with all elements adjacent
				else {
					indHi++;
					indLow = indHi + (leftDensity - 1);
					for (int j = 0; j < partialLeftReordered.size(); j++) {
						List<Integer> l = partialLeftReordered.get(j);
						List<Integer> reordered = new ArrayList<Integer>();
						for (int k = 0; k < indHi; k++) {
							reordered.add(null);
						}
						for (int k = 0; k < l.size(); k++) {
							if (l.get(k) != null) {
								reordered.add(l.get(k));
							}
						}
						for (int k = indHi + leftDensity; k < rightDensity; k++) {
							reordered.add(null);
						}
						partialLeftReordered.set(j, reordered);
					}
				}
			}
		}
		return partialLeftReordered;
	}
	
	// End of VEEH :)


	/**
	 * Gets, for each chord, the metric position.
	 * 
	 * @return
	 */
	// TESTED
	public List<Rational> getMetricPositionsChords() {
		List<Rational> mp = new ArrayList<Rational>();
		getChords().stream().forEach(c -> { 
			if (!mp.contains(c.get(0).getMetricTime())) { 
				mp.add(c.get(0).getMetricTime());
			} 
		});
		Collections.sort(mp);
		return mp;
	}


	/**
	 * Returns a List<List>> containing, for each chord, the indices in the Transcription of the notes in that chord.
	 * 
	 * @param isBwd
	 * @return
	 */
	// TESTED
	public List<List<Integer>> getIndicesPerChord(/*ProcessingMode procMode*/ boolean isBwd) {
		List<List<Integer>> indicesPerChord = new ArrayList<List<Integer>>();

		List<List<Note>> ch = getChords(); // conditions satisfied; external version OK
		if (isBwd) {
//		if (procMode == ProcessingMode.BWD) {
			Collections.reverse(ch);
		}

		int startIndex = 0;
		// For each chord
		int numChords = ch.size();
		for (int i = 0; i < numChords; i++) {
			List<Integer> indicesCurrChord = new ArrayList<Integer>();
			int endIndex = startIndex + ch.get(i).size();
			// For each note in the chord at index i: add its index to indicesCurrChord
			for (int j = startIndex; j < endIndex; j++) {
				indicesCurrChord.add(j);
			}
			// When the chord is traversed: add indicesCurrChord to indicesPerChord
			indicesPerChord.add(indicesCurrChord);
			startIndex = endIndex;	
		}
		return indicesPerChord;
	}


	/**
	 * Returns the size of the largest chord in the Transcription, taking into consideration any sustained 
	 * previous notes.
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @return
	 */
	// TESTED
	public int getLargestTranscriptionChord() {
		Integer[][] bnp = getBasicNoteProperties();
		int indexOfFinalChord = bnp[bnp.length - 1][CHORD_SEQ_NUM];
		int largestChord = 0;
		int lowestNoteIndex = 0;
		for (int i = lowestNoteIndex; i < bnp.length; i++) {
			Integer[] curr = bnp[lowestNoteIndex];
			int newOnsetsOnly = curr[CHORD_SIZE_AS_NUM_ONSETS];
			int numSustainedNotes = 
				getIndicesOfSustainedPreviousNotes(null, null, bnp, lowestNoteIndex).size();
			int sizeOfCurrChord = newOnsetsOnly + numSustainedNotes;
			if (sizeOfCurrChord > largestChord) {
				largestChord = sizeOfCurrChord;
			}
			// If the last chord is reached: break
			if (curr[CHORD_SEQ_NUM] == indexOfFinalChord) {
				break;
			}
			// If not: increment lowestNoteIndex
			else {
				lowestNoteIndex += newOnsetsOnly;	
			}
		}
		return largestChord;
	}


	/**
	 * Gets, for each note at onset time t in the Transcription, a list containing v elements,
	 * each of which corresponds to a voice and contains the last n notes (pitch, onset, 
	 * duration, metric position) up to t with an onset time < t in that voice. If 
	 * the final note in the list has an offset time > t, the element is set to <code>null</code>.
	 * 
	 * @param n
	 * @return
	 */
	// TESTED (for non-tablature case only) TODO
	public List<List<List<Rational[]>>> getLastNotesInVoices(int n) {
		List<List<List<Rational[]>>> res = new ArrayList<>();
		List<Rational> allOnsetTimes = getMetricPositionsChords();
		// Remove duplicates
		List<Rational> dedup = new ArrayList<>();
		for (Rational r : allOnsetTimes) {
			if (!dedup.contains(r)) {
				dedup.add(r);
			}
		}
		allOnsetTimes = dedup;
		
		// For each onset time: get the last n notes in each voice
		List<List<Rational[]>> notesPerVoice = listNotesPerVoice();
		for (int i = 0; i < allOnsetTimes.size(); i++) {
			Rational r = allOnsetTimes.get(i);
			int numNotesChord = getNumberOfNewNotesPerChord().get(i); 
			List<List<Rational[]>> lastNotesPerVoiceCurrOnset = new ArrayList<>();
			for (int v = 0; v < getNumberOfVoices(); v++) {
				List<Rational[]> notesCurrVoice = notesPerVoice.get(v);
				List<Rational[]> lastNotesCurrVoice = new ArrayList<>();
				for (int j = 0; j < notesCurrVoice.size(); j++) {
					Rational[] note = notesCurrVoice.get(j);
					Rational onset = note[1];
					if (onset.isLess(r)) {
						lastNotesCurrVoice.add(note);
					}
					else {
						break;
					}
				}
				// Get last n notes (skip first onset, where lastNotesCurrVoice is empty)
				if (i != 0) {
					int size = lastNotesCurrVoice.size();
					// Only if there are notes (else lastNotesCurrVoice remains empty) 
					if (size != 0) {
						// See https://stackoverflow.com/questions/14605999/getting-the-last-three-elements-from-a-list-arraylist
						List<Rational[]> lastNNotes = lastNotesCurrVoice.subList(Math.max(size - n, 0), size);
						// If last note in currVoice is sustained: set to null
						Rational[] lastNote = lastNNotes.get(lastNNotes.size()-1);
						Rational offsetLastNote = lastNote[1].add(lastNote[2]);
						if (offsetLastNote.isGreater(r)) {
							lastNNotes = null;
						}
						lastNotesCurrVoice = lastNNotes;
					}
				}
				lastNotesPerVoiceCurrOnset.add(v, lastNotesCurrVoice);
			}
			for (int j = 0; j < numNotesChord; j++) { 
				res.add(lastNotesPerVoiceCurrOnset);
			}
		}
		return res;
	}


	/**
	 * Returns, for each voice, a list containing a Rational[], each element of which represents
	 * a note and contains
	 * <ul>
	 * <li>as element 0: its pitch (with the MIDI number as the numerator and 1 as the denominator)</li>
	 * <li>as element 1: its onset</li>
	 * <li>as element 2: its duration</li>
	 * <li>as element 3: its metric position</li>
	 * </ul>
	 * 
	 * @return
	 */
	// TESTED (non-tab only, for one voice)
	List<List<Rational[]>> listNotesPerVoice() {
		List<List<Rational[]>> notesPerVoice = new ArrayList<>();
		NotationSystem nSys = getScorePiece().getScore();
//		MetricalTimeLine mtl = getScorePiece().getMetricalTimeLine();
		ScoreMetricalTimeLine smtl = getScorePiece().getScoreMetricalTimeLine();
		// For each voice i
		for (int i = 0; i < nSys.size(); i ++) {
			NotationVoice nv = nSys.get(i).get(0);
			List<Rational[]> notes = new ArrayList<>();
			for (NotationChord nc : nv) {
				for (Note n : nc) {
					notes.add(new Rational[]{
						new Rational(n.getMidiPitch(), 1), 
						n.getMetricTime(),
						n.getMetricDuration(),
						smtl.getMetricPosition(n.getMetricTime())[1]});
//						ScoreMetricalTimeLine.getMetricPosition(mtl, n.getMetricTime())[1]});
//						Utils.getMetricPosition(n.getMetricTime(), getMeterInfo())[1]});
				}
			}
			notesPerVoice.add(notes);
		}
		return notesPerVoice;
	}


	/**
	 * Gets, for each voice in the Transcription, the highest and lowest Notes. Returns a double[][], each element of
	 * which represents a voice (where the element at index 0 represents the top voice) and contains two numbers: at
	 * index 0 the lowest pitch in that voice, and at index 1 the highest.
	 *  
	 * @return
	 */
	// TESTED (for both tablature- and non-tablature case) 
	Integer[][] getLowestAndHighestPitchPerVoice() {
		Integer[][] lowestAndHighest = new Integer[MAX_NUM_VOICES][2];

		NotationSystem system = getScorePiece().getScore();
		// For every voice in the Transcription
		for (int i = 0; i < system.size(); i++) {
			NotationStaff staff = system.get(i);
			NotationVoice currentVoice = staff.get(0);
			// List all NotationChords in currentVoice
			List<NotationChord> allNotationChordsCurrentVoice = currentVoice.getContent();
			// Initialise the highest and lowest pitch
			int highestCurrentVoice = 0;
			int lowestCurrentVoice = Integer.MAX_VALUE;
			// If currenVoice is active in the Transcription: get, for every NotationChord in this voice, the Note it 
			// contains (which will be wrapped in a List, but this List will never contain more than one Note), determine 
			// the pitch of that Note, and determine whether highestCurrentVoice or lowestCurrentVoice need to be reset
			if (allNotationChordsCurrentVoice.size() != 0) {
				for (NotationChord nc : allNotationChordsCurrentVoice) {
					List<Note> currentNotes = nc.getContent();
					for (Note n : currentNotes) {
						if (n.getMidiPitch() > highestCurrentVoice) {
							highestCurrentVoice = n.getMidiPitch();
						}
						if (n.getMidiPitch() < lowestCurrentVoice) {
							lowestCurrentVoice = n.getMidiPitch();
						}
					}
				}
				lowestAndHighest[i][0] = lowestCurrentVoice;
				lowestAndHighest[i][1] = highestCurrentVoice;
			}
			// If currentVoice is not active in the Transcription: set highestAndLowest[i] to -1
			else {
				lowestAndHighest[i] = new Integer[]{-1, -1};
			}
		}
		return lowestAndHighest;
	}


	/**
	 * Gets information on the equal-pitch-pairs in the Transcription event at the given index in the given list. 
	 *
	 * NB: If a Transcription event contains three Notes with the same pitch, these will be listed as two
	 * separate equal-pitch-pairs.
	 *
	 * @param transcriptionEvents 
	 * @param eventIndex 
	 * @return A List<List<<Integer>>, in which each row represents an equal-pitch-pair (starting from below), 
	 * and each column at index (0) the pitch (as a MIDInumber) of that EPP, (1) the sequence number in the event
	 * of the lower note of the EPP, and (2) the sequence number in the event of the upper note of the EPP. If
	 * the event does not contain (an) EPP(s), null is returned.   
	 */
	private List<List<Integer>> getEqualPitchPairsInfo(List<List<Note>> transcriptionEvents, int eventIndex) {
		List<List<Integer>> equalPitchPairsInfo = new ArrayList<List<Integer>>();
		List<Note> event = transcriptionEvents.get(eventIndex);

		// Gather the pitches of the Notes in event in a list
		List<Integer> eventPitches = new ArrayList<Integer>();
		for (int i = 0; i < event.size(); i++) {
			int pitch = event.get(i).getMidiPitch();
			eventPitches.add(pitch);
		}
		// For each pitch in eventPitches 
		for (int i = 0; i < eventPitches.size(); i++) {
			int currentPitch = eventPitches.get(i);        
			// Search the remainder of eventPitches for an onset with the same pitch
			for (int j = i + 1; j < eventPitches.size(); j++) {
				// Same pitch found? Equal pitch pair found; create and fill currentEqualPitchPairInfo, add it to 
				// equalPitchPairsInfo, break from inner for, and continue with the next iteration of the outer for 
				// (the next pitch)
				if (eventPitches.get(j) == currentPitch) {
					List<Integer> currentEqualPitchPairInfo = new ArrayList<Integer>();
					currentEqualPitchPairInfo.add(currentPitch);
					currentEqualPitchPairInfo.add(i);
					currentEqualPitchPairInfo.add(j);
					equalPitchPairsInfo.add(currentEqualPitchPairInfo);
					break; 
				}
			} 
		}  	
		// Determine return value
		if (equalPitchPairsInfo.size() > 0) {
			return equalPitchPairsInfo;
		}
		else {
			return null;
		}
	}








	/**
	 * Gets the voice assignments, listed per chord. 
	 *  
	 * @param highestNumVoices Controls the size of the voice assignments returned.
	 * @return
	 */
	// TESTED
	public List<List<Integer>> getVoiceAssignments(int highestNumVoices) {
		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();

		List<List<List<Double>>> allChordVoiceLabels = getChordVoiceLabels();
		for (int i = 0; i < allChordVoiceLabels.size(); i++) {
			List<List<Double>> curr = allChordVoiceLabels.get(i);
			List<Integer> currVoiceAssignment = 
				LabelTools.getVoiceAssignment(curr, highestNumVoices);
			voiceAssignments.add(currVoiceAssignment);
		}		
		return voiceAssignments;
	}


	/**
	 * Gets the ranges (in MIDI pitches) of the individual voices, starting with the highest
	 * voice (voice 0).
	 *  
	 * @return
	 */
	// TESTED
	public List<Integer[]> getVoiceRangeInformation() {
		List<Integer[]> ranges = new ArrayList<>();
		
		NotationSystem nSys = getScorePiece().getScore();
		// For each voice i
		for (int i = 0; i < getScorePiece().getScore().size(); i ++) {
			int lowestPitch = Integer.MAX_VALUE;
			int highestPitch = Integer.MIN_VALUE;
			NotationVoice nv = nSys.get(i).get(0);
			for (NotationChord nc : nv) {
				for (Note n : nc) {
					if (n.getMidiPitch() < lowestPitch) {
						lowestPitch = n.getMidiPitch();
					}
					if (n.getMidiPitch() > highestPitch) {
						highestPitch = n.getMidiPitch();
					}
				}
			}
			ranges.add(new Integer[]{lowestPitch, highestPitch});
		}
		return ranges;
	}


	/**
	 * Gets information on the voice crossings in the Transcription. Returns an Integer[] containing
	 * <ul>
	 * <li>as element 0: the number of notes in the piece.</li>
	 * <li>as element 1: the number of voice crossings where the crossing voice and the crossed 
	 *                   voice have the same onset time (Type 1 vc)</li>
	 * <li>as element 2: the number of voice crossings where the crossing voice has a later onset 
	 *                   time than the crossed voice (Type 2 vc)</li>
	 * <li>as element 3: the total of Type 1 and 2 vc</li>
	 * <li>as element 4: for each voice (starting at 0): the number of instances this voice is 
	 *                   involved in a voice crossing. Instances are counted for each voice that 
	 *                   is crossed (e.g., the superius going under the altus and tenor are two 
	 *                	 voice crossings). In the case of Type 2 vc, a voice is involved both if 
	 *                	 it is the crossing and the crossed voice. </li>
	 * <li>as element 5: for each voice (starting at 0): the number of notes in that voice.</li>               
	 * </ul>
	 * @param tab Is <code>null</code> in the non-tablature case.
	 * @return
	 */
	public Integer[] getVoiceCrossingInformation(Tablature tab) {
		String voiceCrossingInformation = "voice crossing information for " + getName() + "\r\n";
		int mnv = MAX_NUM_VOICES;
		int totalTypeOne = 0;
		int totalTypeTwo = 0;

		// For each note
		List<Note> notes = getNotes();
//		NoteSequence noteSeq = getNoteSequence();
		List<List<List<Double>>> chordVoiceLabels = getChordVoiceLabels();
		List<List<Double>> voiceLabels = getVoiceLabels();
//		MetricalTimeLine mtl = getScorePiece().getMetricalTimeLine();
		ScoreMetricalTimeLine smtl = getScorePiece().getScoreMetricalTimeLine();

		Integer[][] basicTabSymbolProperties = null;
		Integer[][] basicNoteProperties = null;
		int numChords = 0;
		int numNotes = 0;
		// a. In the tablature case
		if (tab != null) {
			basicTabSymbolProperties = tab.getBasicTabSymbolProperties();
			numChords = tab.getChords().size();
			numNotes = tab.getNumberOfNotes();
		}
		// b. in the non-tablature case
		else {
			basicNoteProperties = getBasicNoteProperties();
			numChords = getChords().size(); // conditions satisfied; external version OK
			numNotes = getNumberOfNotes();
		}

		Integer[] timesInvolved = new Integer[getNumberOfVoices()];
		Arrays.fill(timesInvolved, 0);

		// For each chord
		int lowestNoteIndex = 0;
		for (int i = 0; i < numChords; i++) { // i is index of current chord  	
			// Get current chord size and meterinfo, and find current onset time
			int currentChordSize = 0; 
//			List<Integer[]> meterInfo = null;
			Rational onsetCurrNote = null;
			List<Integer> pitchesInChord;
			// a. in the tablature case
			if (tab != null) {
				currentChordSize = tab.getChords().get(i).size();
//				meterInfo = tab.getMeterInfo();
//				meterInfo = tablature.getTimeline().getMeterInfoOBS();
				for (Integer[] btp : basicTabSymbolProperties) {
					if (btp[Tablature.CHORD_SEQ_NUM] == i) {
						onsetCurrNote = 
							new Rational(btp[Tablature.ONSET_TIME], Tablature.SRV_DEN);
						break;
					}
				}
				pitchesInChord = tab.getPitchesInChord(i); 
			}
			// b. In the non-tablature case
			else {
				List<Note> currChord = getChords().get(i);
				currentChordSize = currChord.size(); // conditions satisfied; external version OK
//				meterInfo = getMeterInfo();
				for (Integer[] bnp : basicNoteProperties) {
					if (bnp[CHORD_SEQ_NUM] == i) {
						onsetCurrNote = new Rational(bnp[ONSET_TIME_NUMER], bnp[ONSET_TIME_DENOM]);
						break;
					}
				}
				pitchesInChord = getPitchesInChord(currChord);
			}

			Timeline tl = (tab != null ? tab.getEncoding().getTimeline() : null);
			System.out.println(tab);
			System.out.println(tl);
			System.out.println(tab != null);
			String currMeasure = "" + 
				tab != null ? 
				(tl.getMetricPosition((int) onsetCurrNote.mul(Tablature.SRV_DEN).toDouble())[0].getNumer() + " " +
				tl.getMetricPosition((int) onsetCurrNote.mul(Tablature.SRV_DEN).toDouble())[1]) // multiplication necessary because of division when making onsetCurrNote above		
				:
				(smtl.getMetricPosition(onsetCurrNote)[0].getNumer() + " " +
//				(ScoreMetricalTimeLine.getMetricPosition(mtl, onsetCurrNote)[0].getNumer() + " " +	
//				(Utils.getMetricPosition(onsetCurrNote, meterInfo)[0].getNumer() + " " +
				smtl.getMetricPosition(onsetCurrNote)[1]);
//				ScoreMetricalTimeLine.getMetricPosition(mtl, onsetCurrNote)[1]);
//				Utils.getMetricPosition(onsetCurrNote, meterInfo)[1]);

			// a. Get the voice crossing information within the chord (Type 1)
			List<List<Double>> currentChordVoiceLabels = chordVoiceLabels.get(i);
			List<List<Integer>> voicesInChord = LabelTools.getVoicesInChord(currentChordVoiceLabels);
			List<List<Integer>> vcInfo = 
				getVoiceCrossingInformationInChord(pitchesInChord, voicesInChord);
			if (vcInfo.get(0).size() != 0) {
				voiceCrossingInformation = 
					voiceCrossingInformation.concat("Type 1 voice crossing at chordindex " +
					i + " (b. " + currMeasure + "); voices involved are " + vcInfo.get(0) + "\n");
				for (int v : vcInfo.get(0)) {
					timesInvolved[v]++;
				}
//				for (int j = 0; j < vcInfo.get(1).size(); j++) {
//					if (Math.abs(vcInfo.get(1).get(j) - vcInfo.get(1).get(j + 1)) > 1) {
//						voiceCrossingInformation = voiceCrossingInformation.concat("  --> HIER1" + "\n");
//					}
//					j++;
//				}
				totalTypeOne++;
			}

			// b. For each note in the chord: get the voice crossing information with any previous 
			// sustained notes (Type 2)
			for (int j = lowestNoteIndex; j < lowestNoteIndex + currentChordSize; j++) { // j is index of current note
				int pitchCurrNote = 0;
				// a. In the tablature case
				if (tab != null) {
					pitchCurrNote = basicTabSymbolProperties[j][Tablature.PITCH];
				}
				// b. In the non-tablature case
				else {
					pitchCurrNote = basicNoteProperties[j][PITCH];
				}
				List<Double> voiceLabelCurrNote = voiceLabels.get(j);
				List<Integer> voicesCurrNote = LabelTools.convertIntoListOfVoices(voiceLabelCurrNote);
				// Find sustained notes
				for (int k = 0; k < notes.size(); k++) { // k is index of note before current note 
//				for (int k = 0; k < noteSeq.size(); k++) { // k is index of note before current note 
					Note n = notes.get(k);
//					Note n = noteSeq.getNoteAt(k);
					Rational onsetPrevNote = n.getMetricTime();
					Rational durationPrevNote = n.getMetricDuration();
					Rational offsetPrevNote = onsetPrevNote.add(durationPrevNote);
					// Stop searching if the note is no longer a previous note
					if (onsetPrevNote.isEqual(onsetCurrNote)) {
						break;
					}
					else {
						// Sustained note?
						if (offsetPrevNote.isGreater(onsetCurrNote)) {
							// Get pitch and voices of previous note
							int pitchPrevNote = n.getMidiPitch();
							List<Double> voiceLabelPrevNote = voiceLabels.get(k);
							List<Integer> voicesPrevNote = 
								LabelTools.convertIntoListOfVoices(voiceLabelPrevNote);
							// For each note in the chord: voice crossing with sustained note if that sustained note
							// -has a lower voice number (i.e., is in a higher voice) and a lower pitch
							// -has a higher voice number (i.e., is in a lower voice) and a higher pitch
							// Two for-loops necessary to take into account CoDs
							for (int currVoice : voicesCurrNote) {
								for (int prevVoice : voicesPrevNote) {
									if ((prevVoice < currVoice && pitchPrevNote < pitchCurrNote) ||
										(prevVoice > currVoice && pitchPrevNote > pitchCurrNote)) {
//										double prevMeasure = onsetPrevNote.toDouble() + 1.0;
										String prevMeasure = "" + 
											tab != null ? 
											(tl.getMetricPosition((int) onsetPrevNote.mul(Tablature.SRV_DEN).toDouble())[0].getNumer() + 
											" " + tl.getMetricPosition((int) onsetPrevNote.mul(Tablature.SRV_DEN).toDouble())[1])
											:
											(smtl.getMetricPosition(onsetPrevNote)[0].getNumer() + " " +	
//											(ScoreMetricalTimeLine.getMetricPosition(mtl, onsetPrevNote)[0].getNumer() + " " +	
//											(Utils.getMetricPosition(onsetPrevNote, meterInfo)[0].getNumer() + " " + 
											smtl.getMetricPosition(onsetPrevNote)[1]);											
//											ScoreMetricalTimeLine.getMetricPosition(mtl, onsetPrevNote)[1]);
//											Utils.getMetricPosition(onsetPrevNote, meterInfo)[1]);
										voiceCrossingInformation = 
											voiceCrossingInformation.concat("Type 2 voice crossing at chordIndex " + i + "; notes involved are:" + "\n" + 
											"  note at index " + j + " (m. " + currMeasure + "; pitch " + pitchCurrNote + "; voice " + currVoice + ")" + "\n" +  
											"  note at index " + k + " (m. " + prevMeasure + "; pitch " + pitchPrevNote + "; voice " + prevVoice + ")" + "\n");
										totalTypeTwo++;
										timesInvolved[currVoice]++;
										timesInvolved[prevVoice]++;
//										if (Math.abs(currVoice - prevVoice) > 1) {
//											voiceCrossingInformation = voiceCrossingInformation.concat("  --> HIER2" + "\n");
//										}
									}
								}
							}					
						}
					}
				}
			}
			lowestNoteIndex += currentChordSize;
		}
		voiceCrossingInformation += "total type 1: " + totalTypeOne + "\r\n";
		voiceCrossingInformation += "total type 2: " + totalTypeTwo + "\r\n";
		voiceCrossingInformation += "total       : " + (totalTypeOne+totalTypeTwo) + "\r\n";
		voiceCrossingInformation += "times each voice is involved" + "\r\n";
		voiceCrossingInformation += Arrays.toString(timesInvolved);
		System.out.println(voiceCrossingInformation);
		// res contains numNotes + totalTypeOne + totalTypeTwo + all + involved (per voice) + 
		// voice size (per voice)
		Integer[] res = new Integer[4 + timesInvolved.length + getNumberOfVoices()];
		res[0] = numNotes;
		res[1] = totalTypeOne;
		res[2] = totalTypeTwo;
		res[3] = totalTypeOne + totalTypeTwo;
		for (int i = 0; i < timesInvolved.length; i++) {
			res[4+i] = timesInvolved[i];
		}
		// Note per voice
		List<List<Integer>> notesPerVoice = listNotesPerVoice(getVoiceLabels());
		List<Integer> numNotesPerVoice = new ArrayList<>();
		for (int i = 0; i < notesPerVoice.size(); i++) {
			if (notesPerVoice.get(i).size() > 0) {
				numNotesPerVoice.add(notesPerVoice.get(i).size());
			}
			else {
				int numVoices = getNumberOfVoices();
				if (numVoices == 4) {
					// Only voice 4 is allowed to be empty
					if (i != mnv-1) {
						throw new RuntimeException("Voice " + i + " does not contain any notes.");
					}
				}
				if (numVoices == 3) {
					// Only voice 3 and 4 are allowed to be empty
					if (i != mnv-1 && i != mnv-2) {
						throw new RuntimeException("Voice " + i + " does not contain any notes.");
					}
				}
				if (numVoices == 2) {
					// Only voice 2, 3 and 4 are allowed to be empty
					if (i != mnv-1 && i != mnv-2 && i != mnv-3) {
						throw new RuntimeException("Voice " + i + " does not contain any notes.");
					}
				}
			}
		}
		for (int i = 0; i < numNotesPerVoice.size(); i++) {
			res[(res.length-numNotesPerVoice.size())+i] = numNotesPerVoice.get(i); 
		}
		System.out.println(Arrays.toString(res));
		return res;
	}


	/**
	 * Lists all the unique chords in the Transcription in the order they are encountered. Each chord is 
	 * represented as a series of pitches, with the lowest pitch listed first. Chords with voice crossings 
	 * are therefore rearranged so that their pitches are sorted numerically.
	 * 
	 * @return
	 */
	// TESTED
	public List<List<Integer>> generateChordDictionary() {
		List<List<Integer>> chordDictionary = new ArrayList<List<Integer>>();

		// For each chord 
//		List<List<Note>> chords = getTranscriptionChords();
		Integer[][] bnp = getBasicNoteProperties();
		int numChords = bnp[bnp.length - 1][CHORD_SEQ_NUM] + 1;
		List<List<Note>> ch = getChords();
//		int numChords = basicNoteProperties[basicNoteProperties.length - 1][CHORD_SEQ_NUM] + 1;
//		for (int i = 0; i < chords.size(); i++) {  		
		for (int i = 0; i < numChords; i++) {
			// List the pitches in the chord
			List<Integer> pitchesInCurrentChord = getPitchesInChord(ch.get(i));
//			List<Integer> pitchesInCurrentChord = getPitchesInChord(i);
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
	 * Lists all the unique (chord) voice assignments in the transcription in the order they are 
	 * encountered.
	 * 
	 * @param highestNumVoices Controls the size of the voice assignments returned.
	 * @return
	 */
	// TESTED (for both tablature and non-tablature case)
	public List<List<Integer>> generateVoiceAssignmentDictionary(int highestNumVoices) {
		List<List<Integer>> voiceAssignmentDictionary = new ArrayList<List<Integer>>();
		List<List<Integer>> voiceAssignments = getVoiceAssignments(highestNumVoices);

		// For each voice assignment
		for (int i = 0; i < voiceAssignments.size(); i++) {
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
//			if (currentVoiceAssignment.equals(Arrays.asList(new Integer[]{1, 0, -1, -1}))) {
//				System.out.println("num = " + i);
//				for (int j = 0; j < getBasicNoteProperties().length; j++) {
//					if (getBasicNoteProperties()[j][CHORD_SEQ_NUM] == 327) {
//						Rational metricTime = new Rational(getBasicNoteProperties()[j][Transcription.ONSET_TIME_NUMER],
//							getBasicNoteProperties()[j][Transcription.ONSET_TIME_DENOM]);	
//						Rational[] metricPosition = Tablature.getMetricPosition(metricTime, getMeterInfo());
//						System.out.println(metricPosition[0] + ", " + metricPosition[1]);
//						System.out.println(getBasicNoteProperties()[j][ONSET_TIME_NUMER] / 
//							getBasicNoteProperties()[j][ONSET_TIME_DENOM]);
//						System.out.println(getBasicNoteProperties()[j][PITCH]);
//					}
//				}
//
//			}
			// If voiceAssignmentDictionary does not contain currentVoiceAssignment: add
			if (!voiceAssignmentDictionary.contains(currentVoiceAssignment)) {
				voiceAssignmentDictionary.add(currentVoiceAssignment);
			}
		}		
		return voiceAssignmentDictionary;
	}


	// OBSOLETE FROM HERE :)
	
//	/**
//	 * Constructor for a derivation of a ground truth Transcription (used for data augmentation).
//	 * Creates a <code>Transcription</code> from an existing <code>Piece</code> and <code>Encoding</code>. 
//	 *                              
//	 * @param argPiece
//	 * @param argEncoding
//	 */
//	public Transcription(Piece argPiece, Encoding argEncoding) {
////		boolean normaliseTuning = false;
////		boolean isGroundTruthTranscription = true;
//		init(new ScorePiece(argPiece), argEncoding, /*null,*/ 
//			/*normaliseTuning, isGroundTruthTranscription,*/
//			null, null, Type.AUGMENTED);
//	}
	
	/**
	 * Visualises the Transcription. 
	 * 
	 * @param showAsScore
	 * @param numberOfVoices
	 */
	private JFrame visualise(TimeSignature timeSig, /*KeyMarker keyMarker,*/ boolean showAsScore, int numberOfVoices) {

		String windowTitle = getName();
		Piece argPiece = getScorePiece();

		Piece piece = new Piece(); 
		NotationSystem notationSystem = piece.createNotationSystem();
		
		// Add time and key signatures
		MetricalTimeLine metricalTimeLine = piece.getMetricalTimeLine();
		
		TimeSignatureMarker timeSigMarker = 
			new TimeSignatureMarker(timeSig.getNumerator(), timeSig.getDenominator(), new Rational(0, 1));
		timeSigMarker.setTimeSignature(timeSig);
		metricalTimeLine.add(timeSigMarker);
//		metricalTimeLine.add(keyMarker);
		
		int superius = 0;
		int altus = 1;
		int tenor = 2;
		int bassus = 3;
		
		int lowestVoice = -1; 
		if (numberOfVoices == 3) {
			lowestVoice = tenor;
		}
		else if (numberOfVoices == 4) {
			lowestVoice = bassus;
		}
		
		for (int voice = superius; voice <= lowestVoice; voice++) {
			NotationStaff notationStaff = null;
			if (voice == superius || voice == tenor) {
				notationStaff = new NotationStaff(notationSystem);
				if (voice == tenor) {
					notationStaff.setClefType('f', 1, 0);
				}
			}
			if (voice == altus || voice == bassus) {
				if (!showAsScore) {
					// If j == ALTUS, notationSystem contains one staff; if j == BASSUS it contains two staves
					notationStaff = notationSystem.get((notationSystem.size() - voice) * -1);
				}
				else {
					notationStaff = new NotationStaff(notationSystem);
					if (voice == bassus) {
						notationStaff.setClefType('f', 1, 0);
					}
				}
			}
			NotationVoice notationVoice = new NotationVoice(notationStaff);
			for (NotationChord nc : argPiece.getScore().get(voice).get(0)) {
				Note n = nc.get(0);	
//				if (!showAsScore) {
//					RenderingHints rh = new RenderingHints();   		  
//					if (voice == SUPERIUS || voice == TENOR) {
//						rh.registerHint("stem direction", "up");
//					}
//					else {
//						rh.registerHint("stem direction", "down");
//					}	
//					n.setRenderingHints(rh);
//				}
				notationVoice.add(n);
			}
		}
		
		// OUDE VERSIES
//		if (!showAsScore) {
////			RenderingHints rhUpper = new RenderingHints();
////			rhUpper.registerHint("stem direction", "up");
////			RenderingHints rhLower = new RenderingHints();
////			rhLower.registerHint("stem direction", "down");
//
//			NotationStaff upperStaff = new NotationStaff(notationSystem);
//			NotationStaff lowerStaff = new NotationStaff(notationSystem);
//			lowerStaff.setClefType('f', 1, 0);
//			for (int voice = SUPERIUS; voice <= BASSUS; voice++) {
//				NotationStaff ns = null;
//				if (voice == SUPERIUS || voice == ALTUS) {
//					ns = upperStaff;
//				}
//				else {
//					ns = lowerStaff;
//				}
//				NotationVoice nv = new NotationVoice(ns);
//				NotationVoice argNV = argPiece.getScore().get(voice).get(0);
//				for (NotationChord nc : argNV) {
//					Note n = nc.get(0);	  
////					if (voice == SUPERIUS || voice == TENOR) {
////						n.setRenderingHints(rhUpper);
////					}
////					else {
////						n.setRenderingHints(rhLower);
////					}
//					nv.add(n);
//				}
//			}
//		}
//		else {
//			for (int voice = SUPERIUS; voice <= BASSUS; voice++) {
//				NotationStaff ns = new NotationStaff(notationSystem);
//				if (voice == TENOR || voice == BASSUS) {
//					ns.setClefType('f', 1, 0);
//				}
//				NotationVoice nv = new NotationVoice(ns);
//				NotationVoice argNV = argPiece.getScore().get(voice).get(0);
//				for (NotationChord nc : argNV) {
//					Note n = nc.get(0);	  
//					nv.add(n);
//				}
//			}
//		}

		notationSystem.createBeams();
		NotationStaffConnector nsc = new NotationStaffConnector(CType.BRACKET);
		nsc.add(notationSystem.get(0));
		nsc.add(notationSystem.get(notationSystem.size() - 1));
		notationSystem.addStaffConnector(nsc);

		ScoreEditor scoreEditor = new ScoreEditor(piece.getScore());
//		ScoreEditor scoreEditor = new ScoreEditor(getPiece().getScore());
		scoreEditor.setModus(Mode.SELECT_AND_EDIT);	

		JFrame fullScoreFrame = new JFrame(windowTitle);
		fullScoreFrame.setJMenuBar(getMenubar());
		fullScoreFrame.add(new JScrollPane(scoreEditor));
		fullScoreFrame.setSize(800, 600);
//		fullScoreFrame.setSize(1200, 1200);
		fullScoreFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//		fullScoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fullScoreFrame.setVisible(true);
		scoreEditor.setSize(20000, 20000);

		return fullScoreFrame;
	}


	private JMenuBar getMenubar() {
		JMenuBar encodingWindowMenubar = null;
		if (encodingWindowMenubar == null) {
			encodingWindowMenubar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			encodingWindowMenubar.add(fileMenu);   
			
			JMenuItem openFile = new JMenuItem("Open"); 
			fileMenu.add(openFile); 
			openFile.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenuItem saveFile = new JMenuItem("Save");
			fileMenu.add(saveFile);
			saveFile.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					saveFileAction();
				}
			});

			JMenu editMenu = new JMenu("Edit");
			encodingWindowMenubar.add(editMenu);   

			JMenuItem blaFile = new JMenuItem("Bla"); 
			editMenu.add(blaFile); 
			blaFile.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenu viewMenu = new JMenu("View");
			encodingWindowMenubar.add(viewMenu);   

			JMenuItem scoreViewFile = new JMenuItem("Score View"); 
			viewMenu.add(scoreViewFile); 
			scoreViewFile.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenuItem grandStaffViewFile = new JMenuItem("Grand Staff View"); 
			viewMenu.add(grandStaffViewFile); 
			grandStaffViewFile.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

		}
		return encodingWindowMenubar;
	}


	/**
	 * Determines whether a chord contains a voice crossing.
	 * @return
	 */
	private boolean chordContainsVoiceCrossing(int chordIndex) {
		boolean chordContainsVoiceCrossing = false;
//		NotationSystem notationSystem = piece.getScore();
		List<Note> currentChord = getChords().get(chordIndex); // conditions satisfied; external version OK
		List<Integer> voicesInCurrentChord = new ArrayList<Integer>();
		// List the voices in the chord 
		for (int j = 0; j < currentChord.size(); j++) {
			Note currentNote = currentChord.get(j);
			int currentVoice = findVoice(currentNote);
//			int currentVoice = findVoice(currentNote, notationSystem);
			voicesInCurrentChord.add(currentVoice);
		}
//		System.out.println("chordIndex = " + chordIndex);
//		System.out.println("voices incurrent chord = " + voicesInCurrentChord);

		// Chords without voice crossings will be sorted in strict reverse numerical order (since their Notes
		// are added to the NoteSequence from low to high and the lowest voices has the highest voice number). 
		// 1. Make a list in which the voices in currentChord are ordered in reverse numerical order
		List<Integer> voicesInCurrentChordOrdered = new ArrayList<Integer>();
		voicesInCurrentChordOrdered.addAll(voicesInCurrentChord);
		// Order numerically and then reverse
		Collections.sort(voicesInCurrentChordOrdered);
		Collections.reverse(voicesInCurrentChordOrdered);
		// 2. Check whether voicesInCurrentChord and voicesInCurrentChordOrdered are equal. If not: voice crossing
		// found; add chordIndex to chordsWithVoiceCrossings
		if (!voicesInCurrentChordOrdered.equals(voicesInCurrentChord)) {
			chordContainsVoiceCrossing = true;
		}    	
		return chordContainsVoiceCrossing;
	}


	/**
	 * Gets the pitches in the chord at the given index. Element 0 of the List represents the lowest note's pitch,
	 * element 1 the second-lowest note's, etc. Sustained notes are not included. 
	 * 
	 * NB: This method applies only to the non-tablature case
	 * 
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	private List<Integer> getPitchesInChordOLD(int chordIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();

		List<Note> transcriptionChord = null;
		// Use the no-final version of the chords when called in Transcription creation
		if (getChords() == null) {
//		if (transcriptionChordsFinal == null) {
			transcriptionChord = getChordsFromNoteSequence().get(chordIndex);
		}
		// Otherwise use external version
		else {
			transcriptionChord = getChords().get(chordIndex);
		}

		for (Note n : transcriptionChord) {
			pitchesInChord.add(n.getMidiPitch());
		}
		return pitchesInChord;
	}


//	/**
//	 * Returns the size of the largest chord in the Transcription.
//	 * 
//	 * @return
//	 */
//	// TESTED
//	public int getLargestTranscriptionChordOLD() { 
//		int largestChord = 0;
//		List<List<Note>> transcriptionChords = getTranscriptionChords();
//		for (List<Note> l : transcriptionChords) {
//			int currentSize = l.size();
//			if (currentSize > largestChord) {
//				largestChord = currentSize;
//			}
//		}
//		return largestChord;
//	}


//	/**
//	 * Checks for each note in the Transcription whether it is part of a CoD. Returns a List<Integer[]> the 
//	 * size of the number of notes in the Transcription, containing for each element:
//	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
//	 *   b. If the the note at that index is a CoDnote: an Integer[] containing 
//	 *      as element 0: the voice the lower CoDnote is in;
//	 *      as element 1: the voice the upper coDnote is in.
//	 *      
//	 * NB: Tablature case only.
//	 *   
//	 * @param tablature  
//	 * @return
//	 */
//	//
//	public List<Integer[]> getCoDVoicesInfo(Tablature tablature) {
//	  List<Integer[]> coDVoicesInfo = new ArrayList<Integer[]>();
//	  
//	  List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//	  NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLabels = getVoiceLabels();
//		// Initialise coDVoicesInfo with all elements set to null
//		for (int i = 0; i < noteSeq.size(); i++) {
//			coDVoicesInfo.add(null);
//		}
//			
//		// For all chords
//		List<List<Note>> transcriptionChords = getTranscriptionChords();  
//		for (int i = 0; i < transcriptionChords.size(); i++) {
//			// If the chord contains a CoD
////			Integer[][] currentCoDInfo = getUnisonInfo(i);
//			Integer[][] currentCoDInfo = getCoDInfo(tablatureChords, i);
//			if (currentCoDInfo != null) {
//				// For each unison
//				for (int j = 0; j < currentCoDInfo.length; j++) {
//				  // 1. Determine the indices in noteSeq and voiceLabels of the lower and upper unison notes
//		      // a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
//		   		int notesPreceding = 0;
//		      for (int k = 0; k < i; k++) {
//		      	notesPreceding += transcriptionChords.get(k).size();
//		      }
//		      // b. Calculate the indices in the NoteSequence
//		      int indexOfLowerUnisonNote = notesPreceding + currentCoDInfo[j][1];
//		      int indexOfUpperUnisonNote = notesPreceding + currentCoDInfo[j][2];
////		      System.out.println("chordIndex = " + i + "; " +  indexOfLowerUnisonNote + " " + indexOfUpperUnisonNote);
//		      
//		      
//		      // 2. If the unison notes have the same duration
//		      Rational durationLower = noteSeq.getNoteAt(indexOfLowerUnisonNote).getMetricDuration();
//		      Rational durationUpper = noteSeq.getNoteAt(indexOfUpperUnisonNote).getMetricDuration();
//		      List<Double> voiceLabelLower = voiceLabels.get(indexOfLowerUnisonNote);
//		      List<Double> voiceLabelUpper = voiceLabels.get(indexOfUpperUnisonNote);
//		      if (durationLower.equals(durationUpper)) {
//		        // Combine the voice labels
//			      int indexOfOneInUpper = voiceLabelUpper.indexOf(1.0);
//			      List<Double> correctVoices = new ArrayList<Double>(voiceLabelLower);
//			      correctVoices.set(indexOfOneInUpper, 1.0);
//		      	equalDurationUnisons.set(indexOfLowerUnisonNote, correctVoices);
//		      	equalDurationUnisons.set(indexOfUpperUnisonNote, correctVoices);
//		      }
//				}
//			}
//		}
//	  return coDVoicesInfo;
//	}


//	/**
//	 * Sets <i>noteProperties</i>, a List containing the Note properties for each Note: (0) pitch; 
//	 * (1) duration; (2) size of the chord the Note is in; (3) whether the Note's duration is shorter than 
//	 * an 8th note. 
//	 * 
//	 */
//  public void setNoteProperties() {
//    noteProperties = new ArrayList<List<Double>>();
////    Integer[][] basicNoteProperties = getBasicNoteProperties();
//    
//    // Iterate through basicNoteProperties and create noteProperties for each Note   
//    for (int i = 0; i < basicNoteProperties.length; i++) {
//      Integer[] currentBasicNoteProperties = basicNoteProperties[i];
//      List<Double> currentNoteProperties = new ArrayList<Double>();
//    	// 0. Pitch
//      double pitch = currentBasicNoteProperties[PITCH];
//      currentNoteProperties.add(0, pitch);
//      
//      // 1. Duration
//      double duration = (double) currentBasicNoteProperties[DURATION_NUMER] / 
//      	currentBasicNoteProperties[DURATION_DENOM]; 
////      System.out.println("currentBasicNoteProperties[1] = " + currentBasicNoteProperties[1]);
////      System.out.println("currentBasicNoteProperties[2] = " + currentBasicNoteProperties[2]);
////      System.exit(0);
//      currentNoteProperties.add(1, duration);
//      
//      // 2. The size of the chord the current Note is in
//      double size = currentBasicNoteProperties[CHORD_SIZE_AS_NUM_ONSETS];
//      currentNoteProperties.add(2, size);
//      
//      // 3. Is the current Note's duration shorter than an 8th note? Flag as short note
//      if (duration < 1.0/8) {
//      	currentNoteProperties.add(3, 1.0);
//      }
//      else {
//      	currentNoteProperties.add(3, 0.0);
//      }
//      
//      // Add onsetProperties at index i to allProperties and proceed to the next TS 
//      noteProperties.add(i, currentNoteProperties);
//    }
//  }


//	/**
//	 * Gets  <i>noteProperties</i>.
//	 * 
//	 * @return
//	 */
//  public List<List<Double>> getNoteProperties() {
//  	return noteProperties;
//  }


	/**
	 * Constructor for a predicted Transcription.
	 * 
	 * @param argMidiFile
	 * @param argEncodingFile
	 * @param argPiece
	 * @param argVoiceLabels
	 * @param argDurationLabels
	 */
	private Transcription(String name, Encoding argEncoding, /*File argEncodingFile,*/ Integer[][] argBtp, 
		Integer[][] argBnp, int argHiNumVoices, List<List<Double>> argVoiceLabels, 
		List<List<Double>> argDurationLabels, MetricalTimeLine mtl, SortedContainer<Marker> ks) {
		
//		Encoding argEncoding = argEncodingFile != null ? new Encoding(argEncodingFile) : null;

		// Create and set the predicted Piece
		Piece predictedPiece = 
			createPiece(argBtp, argBnp, argVoiceLabels, argDurationLabels, argHiNumVoices, mtl, ks, name);
//		predictedPiece.setName(name);

		// Create the Transcription based on the predicted Piece
//		boolean normaliseTuning = true; // is only used in the tablature case
//		boolean isGroundTruthTranscription = false;
		init(new ScorePiece(predictedPiece), argEncoding, null, /*normaliseTuning, isGroundTruthTranscription,*/ 
			argVoiceLabels, argDurationLabels, Type.PREDICTED);
			
//		// Set the predicted class fields. When creating a ground truth Transcription, this happens inside
//		// handleCoDNotes() and handleCourseCrossings(), but when creating a predicted Transcription this step
//		// is skipped in those methods because the voice labels and duration labels are already ready-to-use. In 
//		// the tablature case, only voicesCoDNotes must still be created from them
//		setVoiceLabels(argVoiceLabels);
//		// a. In the tablature case
//		if (argEncodingFile != null) {
//			// Set durationLabels
//			// NB: The durationLabels created in createTranscription are overwritten by argDurationLabels. Thus, 
//			// when not modelling duration (when argDurationLabels == null), they are reset to null
//			setDurationLabels(argDurationLabels);
//			// Create voicesCoDNotes
//			// NB: currently, only one duration is always predicted; both CoDnotes thus have the same duration. In
//			// this case, the lower CoDnote (i.e., the one in the lower voice that comes first in the NoteSequence) 
//			// is placed at element 0 (see Javadoc handleCoDNotes())
//			List<Integer[]> voicesCoDNotes = new ArrayList<Integer[]>();
//			// For each predicted voiceLabel
//			for (int i = 0; i < argVoiceLabels.size(); i++) {
//				List<Double> currLabel = argVoiceLabels.get(i);
//				// IN case of a CoD, voices contain two elements: the highest predicted voice as element 0, 
//				// and the lowest predicted voice as element 1 
//				List<Integer> voices = DataConverter.convertIntoListOfVoices(currLabel);
//				// If a CoD is predicted
//				if (voices.size() > 1) {
//					Integer[] currVoicesCoDNotes = new Integer[2];
//					currVoicesCoDNotes[0] = voices.get(1); // lowest predicted voice
//					currVoicesCoDNotes[1] = voices.get(0); // highest predicted voice
//					voicesCoDNotes.add(currVoicesCoDNotes);
//				}
//				// If no CoD is predicted
//				else {
//					voicesCoDNotes.add(null);
//				}
//			}	
//			setVoicesSNU(voicesCoDNotes);
//		}	
//		// b. In the non-tablature case
//		else {
//			setEqualDurationUnisonsInfo(argEqualDurationUnisonsInfo);
//		}  	  	
	}


	private List<List<List<Double>>> makeChordVoiceLabelsOLD(Tablature tab) {
		List<List<List<Double>>> argChordVoiceLabels = new ArrayList<List<List<Double>>>();
		argChordVoiceLabels = new ArrayList<List<List<Double>>>();

		List<List<Double>> argVoiceLabels = getVoiceLabels();
	
		// a. In the tablature case
		if (tab != null) {
			// Get the tablature chords 
			List<List<TabSymbol>> argTabChords = tab.getChords();

			// Add the voice labels for each chord to chordVoiceLabels
			int lowestNoteIndex = 0;
			for (int j = 0; j < argTabChords.size(); j++) {
				List<TabSymbol> currentChord = argTabChords.get(j); 
				int currentChordSize = currentChord.size();
				argChordVoiceLabels.add(new ArrayList<List<Double>>(argVoiceLabels.subList(lowestNoteIndex, 
					lowestNoteIndex + currentChordSize)));
				lowestNoteIndex += currentChordSize;
			}
		}
		// b. In the non-tablature case
		else {
			List<List<Note>> argChords = getChords();

			// Add the voice labels for each chord to chordVoiceLabels
			int lowestNoteIndex = 0;
			for (int j = 0; j < argChords.size(); j++) {
//			for (int j = 0; j < getNumberOfChords(); j++) {
				List<Note> currentChord = argChords.get(j); 
				int currentChordSize = currentChord.size();
				argChordVoiceLabels.add(new ArrayList<List<Double>>(argVoiceLabels.subList(lowestNoteIndex, 
					lowestNoteIndex + currentChordSize)));
				lowestNoteIndex += currentChordSize;
			}
		}
		return argChordVoiceLabels;
	}


	/**
	 * Creates a duration label encoding the given durational value (in Tablature.SRV_DEN).
	 * 
	 * NB: Tablature case only.
	 *  
	 * @param duration
	 * @return
	 */
	// TESTED
	private static List<Double> createDurationLabelOLD(int duration, int maxTabSymDur) {		
		List<Double> durationLabel = new ArrayList<Double>();
		for (int i = 0; i < maxTabSymDur; i++) {
			durationLabel.add(0.0);
		}
		int posInLabel = (duration - 1) / 3;
//		durationLabel.set((duration - 1), 1.0);
		durationLabel.set(posInLabel, 1.0); // trp dur
		return durationLabel;
	}


	/**
	 * Determines whether the given voice label represents a CoD.
	 * 
	 * @param voiceLabel
	 * @return
	 */
	// TESTED
	private static boolean containsCoD(List<Double> voiceLabel) {
		if (Collections.frequency(voiceLabel, 1.0) == 2) {
			return true;
		}
		else {
			return false;
		}
	}


//  /**
//   * Creates a Piece from the given arguments.
//   *  
//   * @param voiceLabels
//   * @param numberOfVoices
//   */
//  // TESTED
//  public Piece createPiece(List<List<Double>> voiceLabels, int numberOfVoices) { 
//  	DataConverter dataConverter = new DataConverterTab();
//  	
//  	// Make an empty Piece with the given number of voices
//  	Piece piece = new Piece();
//  	NotationSystem system = piece.createNotationSystem();
//    for (int i = 0; i < numberOfVoices; i++) {
//      NotationStaff staff = new NotationStaff(system); 
//      system.add(staff); 
//      staff.add(new NotationVoice(staff));
//    }
//          
//    // Iterate through the Transcription, convert each note into a Note, and add it to the given voice
//  	Integer[][] bnp = getBasicNoteProperties();
//    for (int i = 0; i < bnp.length; i++) {
//    	// Create a Note from the note at index i
//    	int pitch = bnp[i][Transcription.PITCH];
//    	Rational metricTime =	
//    		new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], bnp[i][Transcription.ONSET_TIME_DENOM]);
//    	Rational metricDuration = 
//    		new Rational(bnp[i][Transcription.DURATION_NUMER], bnp[i][Transcription.DURATION_DENOM]);
//    	Note note = Transcription.createNote(pitch, metricTime, metricDuration);
//    	
//    	// Add the Note to each voice in currentVoices
//    	List<Integer> currentVoices = dataConverter.convertIntoListOfVoices(voiceLabels.get(i));
//    	for (int v : currentVoices) {
//    		NotationVoice voice = piece.getScore().get(v).get(0);
//    		voice.add(note);
//    	}	
//    }
//      
//    return piece;
//  }


//  /**
//  * Returns the number of CoDs the chord at the given index contains.
//  * 
//  * NB: Tablature case only; must be called before getCoDInfo().
//  * 
//  * @param tablatureChords
//  * @param chordIndex
//  * @return 
//  */
// // TESTED
// int getNumberOfCoDsInChord(List<List<TabSymbol>> tablatureChords, int chordIndex) {
// 	List<List<Note>> transcriptionChords = getTranscriptionChords();
// 	int numberOfCoDsInChord = 0;
// 	if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 1)) {
//			numberOfCoDsInChord = 1;
//		}
//		else if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 2)) {
//   	numberOfCoDsInChord = 2;
//		}
//		else if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 3)) {
//   	numberOfCoDsInChord = 3;
//		}
// 	
// 	return transcriptionChords.get(chordIndex).size() - tablatureChords.get(chordIndex).size();
//// 	return numberOfCoDsInChord;
// }


	/**
	 * Gets information on the single-note unison (SNU) notes in the chord at the given index in the given list. 
	 * A SNU occurs when a single Tablature note is shared by two Transcription Notes. Returns an Integer[][],
	 * each element of which represents a SNU pair (starting from below in the chord) containing
	 * <ul>
	 * <li>As element 0: the pitch (as a MIDInumber) of both SNU notes.</li>
	 * <li>As element 1: the sequence number in the chord of the lower SNU note.</li>
	 * <li>As element 2: the sequence number in the chord of the upper SNU note.</li> 
	 * </ul>
	 * If the chord does not contain any SNUs, <code>null</code> is returned.  
	 *
	 * NB1: This method presumes that a chord contains only one SNU, and neither a unison nor a course crossing.
	 * NB2: Tablature case only; must be called before handleSNUs(). 
	 *
	 * @param tablatureChords 
	 * @param chordIndex 
	 * @return    
	 */
	// TESTED
	private Integer[][] getSNUInfoOLD(List<List<TabSymbol>> tablatureChords, int chordIndex) {
		Integer[][] SNUInfo = null;

		List<List<Note>> ch = getChordsFromNoteSequence();
		// Determine the number of SNUs in the chord
		int numCoDs = ch.get(chordIndex).size() - tablatureChords.get(chordIndex).size();

		// If chord contains any SNUs
		if (numCoDs > 0) { 
//		if (getNumberOfCoDsInChord(tablatureChords, chordIndex) > 0) {
			SNUInfo = new Integer[numCoDs][3];
//			coDInfo = new Integer[getNumberOfCoDsInChord(tablatureChords, chordIndex)][3];

			List<Note> chord = ch.get(chordIndex);

			// Gather the pitches of the Notes in chord in a list
			List<Integer> pitchesInChord = new ArrayList<Integer>();
			for (int i = 0; i < chord.size(); i++) {
				int pitch = chord.get(i).getMidiPitch();
				pitchesInChord.add(pitch);
			}
			// For each pitch in pitchesInChord
			int currentRowInCoDInfo = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);        
				// Search the remainder of pitchesInChord for a note with the same pitch (the upper SNU note)
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					// Same pitch found? upper SNU note found; fill the currentRowInCoDInfo-th row of SNUInfo, increase
					// currentRowInCoDInfo, break from inner for, and continue with the next iteration of the outer
					// for (the next pitch)
					if (pitchesInChord.get(j) == currentPitch) {
						SNUInfo[currentRowInCoDInfo][0] = currentPitch;
						SNUInfo[currentRowInCoDInfo][1] = i;
						SNUInfo[currentRowInCoDInfo][2] = j;
						currentRowInCoDInfo++;
						break; 
					}
				} 
			}
		}
		return SNUInfo;
	}


// /**
//  * Determines the number of unisons in the chord at the given index. A unison occurs when two different notes
//  * in the same chord have the same pitch.  
//  * 
//  * NB: Non-tablature case only; must be called before getUnisonInfo().
//  * 
//  * @param chordIndex
//  * @return
//  */
// // TESTED
// int getNumberOfUnisonsInChord(int chordIndex) {
// 	int numberOfUnisons = 0;
// 	
//// 	List<List<Note>> transcriptionChords = getTranscriptionChords();
// 	List<Note> transcriptionChord = getTranscriptionChords().get(chordIndex);
//   
// 	// Only relevant if transcriptionChord contains multiple notes
//   if (transcriptionChord.size() > 1) {
//     // List all the unique pitches in tablatureChord
//     List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
//     List<Integer> uniquePitchesInChord = new ArrayList<Integer>();
//     for (int pitch : pitchesInChord) {
//     	if (!uniquePitchesInChord.contains(pitch)) {
//     		uniquePitchesInChord.add(pitch);
//     	}
//     }
////     // Compare the sizes of pitchesInChord and uniquePitchesInChord; if they are not the same the chord
////     // contains (a) unison(s)
////     if (pitchesInChord.size() == (uniquePitchesInChord.size() + 1)) {
////     	numberOfUnisons = 1;
////     }
////     if (pitchesInChord.size() == (uniquePitchesInChord.size() + 2)) {
////     	numberOfUnisons = 2;
////     }
////     if (pitchesInChord.size() == (uniquePitchesInChord.size() + 3)) {
////     	numberOfUnisons = 3;
////     }
//     numberOfUnisons = pitchesInChord.size() - uniquePitchesInChord.size();
//   }
// 	return numberOfUnisons;
// }


	/**
	 * Gets information on the unison(s) in the chord at the given index. A unison occurs when two different 
	 * notes in the same chord have the same pitch.  
	 *
	 * Returns an Integer[][], each element of which represents a unison pair (starting from below), each element
	 * of which contains:
	 *   as element 0: the pitch (as a MIDInumber) of the unison note
	 *   as element 1: the sequence number in the chord of the lower unison note (i.e., the one appearing first in the chord)
	 *   as element 2: the sequence number in the chord of the upper unison note 
	 * If the chord does not contain (a) unison(s), <code>null</code> is returned. 
	 *
	 * NB1: This method presumes that a chord will not contain more than two of the same pitches in a chord.
	 * NB2: Non-tablature case only; must be called before handleUnisons().
	 *
	 * @param chordIndex
	 * @return
	 */
	// TESTED
	private Integer[][] getUnisonInfoOLD(int chordIndex) {
		Integer[][] unisonInfo = null;

		// Determine the number of unisons in the chord
		List<Integer> pitchesInChord = getPitchesInChord(getChordsFromNoteSequence().get(chordIndex));
//		List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
		List<Integer> uniquePitchesInChord = new ArrayList<Integer>();
		for (int pitch : pitchesInChord) {
			if (!uniquePitchesInChord.contains(pitch)) {
				uniquePitchesInChord.add(pitch);
			}
		}
		int numUnisons = pitchesInChord.size() - uniquePitchesInChord.size();

		// If the chord at chordIndex contains (a) unison(s)
		if (numUnisons > 0) {
			unisonInfo = new Integer[numUnisons][3];

			// For each pitch in pitchesInChord 
			int currentRowInUnisonInfo = 0;
			for (int i = 0; i < pitchesInChord.size(); i++) {
				int currentPitch = pitchesInChord.get(i);        
				// Search the remainder of pitchesInChord for an onset with the same pitch
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					// Same pitch found? Unison found; fill the currentRowInUnisonInfo-th row of unisonInfo, increase
					// currentRowInUnisonInfo, break from inner for, and continue with the next iteration of the outer
					// for (the next pitch)
					if (pitchesInChord.get(j) == currentPitch) {
						unisonInfo[currentRowInUnisonInfo][0] = currentPitch;
						unisonInfo[currentRowInUnisonInfo][1] = i;
						unisonInfo[currentRowInUnisonInfo][2] = j;
						currentRowInUnisonInfo++;
						break; // See NB1 for reason of break
					}
				} 
			}
		}
		return unisonInfo;
	}


	/**
	 * Undiminutes the given Piece, i.e., sets the duration and onset values back to the values as 
	 * found in the tablature.
	 * 
	 * @param bnp Piece, diminuted (w.r.t. metric time and duration).
	 * @param mi meterInfo (from tablature), diminuted (w.r.t. metric time and duration).
	 * @return
	 */
	private static Piece undiminutePiece(Piece p, List<Integer[]> mi) {		
		return null;
	}


//	public NoteSequence getNoteSequence() {
//		return noteSequence;
//	}


	/**
	 * Undiminutes the given basic note properties, i.e., sets the duration and onset values back to 
	 * the values as found in the tablature.
	 * 
	 * @param bnp Basic note properties, diminuted (w.r.t. metric time and duration).
	 * @param mi meterInfo (from tablature), diminuted (w.r.t. metric time and duration).
	 * @return
	 */
	// TESTED
	private static Integer[][] undiminuteBasicNotePropertiesOBS(Integer[][] bnp, List<Integer[]> mi) {
		Integer[][] undiminutedBnp = new Integer[bnp.length][bnp[0].length];
		Rational prevMt = null;
		Rational prevMtDim = null;

		int prevDim = 0;
		// For each first note in a chord
		for (int i = 0 ; i < bnp.length; i++) {
			Integer[] currNote = bnp[i];
			int chordInd = currNote[CHORD_SEQ_NUM];
			int currChordSize = currNote[CHORD_SIZE_AS_NUM_ONSETS];
			// Get original metric time and diminution
			Rational currMt = 
				new Rational(currNote[ONSET_TIME_NUMER], currNote[ONSET_TIME_DENOM]);
			int currDim = TimeMeterTools.getDiminution(currMt, mi);
			// Get the diminuted metric time for the chord the note at index i is in
			Rational currMtDim;
			// If the chord is the first chord of the piece
			if (chordInd == 0) {
				if (currMt.equals(Rational.ZERO)) {
					currMtDim = currMt;
				}
				else {
					currMtDim = TimeMeterTools.diminute(currMt, currDim);
//						(currDim > 0) ? currMt.div(currDim) : currMt.mul(Math.abs(currDim));							
				}
			}
			// If the chord is a chord after the first: to get currMtDim, add the
			// diminuted difference between currMt and prevMt to prevMtDim
			else {
				Rational mtIncrease = TimeMeterTools.diminute(currMt.sub(prevMt), prevDim);
//					prevDim > 0 ? (currMt.sub(prevMt)).div(prevDim) : 
//					(currMt.sub(prevMt)).mul(Math.abs(prevDim));
				currMtDim = prevMtDim.add(mtIncrease);
			}

			// Adapt metric time and duration for all notes in the chord
			for (int j = i; j < i + currChordSize; j++) {
				Integer[] curr = bnp[j];
				// Metric time
				curr[ONSET_TIME_NUMER] = currMtDim.getNumer();
				curr[ONSET_TIME_DENOM] = currMtDim.getDenom();
				// Duration
				Rational currDur = new Rational(curr[DUR_NUMER], curr[DUR_DENOM]);
				Rational currDurDim = TimeMeterTools.diminute(currDur, currDim);
//					(currDim > 0) ? currDur.div(currDim) : currDur.mul(Math.abs(currDim));
				curr[DUR_NUMER] = currDurDim.getNumer();
				curr[DUR_DENOM] = currDurDim.getDenom();
				undiminutedBnp[j] = curr;
			}
			// Increment variables
			prevMt = currMt;
			prevDim = currDim;
			prevMtDim = currMtDim;
			i = (i + currChordSize) - 1;
		}
		return undiminutedBnp;
	}


	private void setChordsOLD() {
		chords = getChordsFromNoteSequence();
	}


	private static List<Integer> getChordSizesFromNoteSeq(NoteSequence noteSeq) {
		List<Integer> notesPerChord = new ArrayList<>();
		int chordSize = 0;
		Rational mt = noteSeq.get(0).getMetricTime();
		for (int i = 0; i < noteSeq.size(); i++) {
			Rational currMt = noteSeq.get(i).getMetricTime(); 
			if (currMt.equals(mt)) {
				chordSize++;
				if (i == noteSeq.size() - 1) {
					notesPerChord.add(chordSize);
				}
			}
			else {
				notesPerChord.add(chordSize);
				chordSize = 0;
				chordSize++;
				mt = currMt;
			}
		}
		return notesPerChord;
	}


	/**
	 * Handles unisons. For each unison note pair,
	 * 
	 * <ul>
	 * <li>Swaps the unison notes in the NoteSequence if they have different durations and the unison note 
	 *     that has the longer duration does not come first.</li>
	 * <li>Swaps the voice labels of the unison notes if they have different durations and the unison note 
	 *     that has the longer duration does not come first. 
	 *     NB: Not if t == Type.PREDICTED (in which case the labels already have their final form).</li>
	 * <li>Lists the unison voices with the voice that contains the note that has the longer duration (or, 
	 *     if they have the same duration, the lower voice) first. A consistent ordering of the unison 
	 *     voices is necessary for a consistent evaluation.</li>        
	 * </ul>
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @param t
	 * @return The list of unison voices.
	 */
	// TESTED
	private List<Integer[]> handleUnisons(Type t) {
		NoteSequence noteSeq = null; // getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<Integer[]> voicesUnison = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));
//		List<Integer[]> voicesEDU = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));
//		List<Integer[]> voicesIDU = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));

		boolean adaptLabels = t != Type.PREDICTED;
		
//		System.out.println(noteSeq.get(12));
//		System.out.println(noteSeq.get(13));

		// 1. Adapt NoteSequence, voice labels
		int notesPreceding = 0;
//		List<Integer> notesPerChord = getChordSizesFromNoteSeq(noteSeq);
		List<List<Note>> ch = getChords();
//		List<List<Note>> chords = getNoteSequenceChords();
		for (int i = 0; i < ch.size(); i++) {
			Integer[][] unisonInfo = getUnisonInfo(ch.get(i));
//			Integer[][] unisonInfo = getUnisonInfo(getPitchesInChord(i));
			// If the chord contains a unison note pair
			if (unisonInfo != null) {
//				Integer[][] unisonInfo = getUnisonInfo(getPitchesInChord(i));
				// For each unison note pair in the chord (there should be only one)
				for (int j = 0; j < unisonInfo.length; j++) {					
					// 1. Determine indices
					// a. Indices of the lower and upper unison note   
					int indLower = notesPreceding + unisonInfo[j][1];
					int indUpper = notesPreceding + unisonInfo[j][2];					
					// b. Indices of the longer and shorter unison note
					Rational durLower = noteSeq.getNoteAt(indLower).getMetricDuration();
					Rational durUpper = noteSeq.getNoteAt(indUpper).getMetricDuration();
					int indLonger = 
						durLower.isGreater(durUpper) ? indLower : (durLower.isLess(durUpper) ? indUpper : -1);
					int indShorter = 
						durLower.isGreater(durUpper) ? indUpper : (durLower.isLess(durUpper) ? indLower : -1);

					// 2. Set voicesUnison
					int first, second;
					int isEDU;
					if (!durLower.isEqual(durUpper)) {
						int indFirst = indLonger;
						int indSecond = indShorter;
						isEDU = 0;
						first = LabelTools.convertIntoListOfVoices(voiceLab.get(indFirst)).get(0);
						second = LabelTools.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
//						voicesIDU.set(indLower, new Integer[]{first, second, indUpper, 0});
//						voicesIDU.set(indUpper, new Integer[]{first, second, indLower, 0});
//						voicesUnison.set(indLower, new Integer[]{first, second, indUpper, 0});
//						voicesUnison.set(indUpper, new Integer[]{first, second, indLower, 0});
					}
					else {
						int indFirst = indLower;
						int indSecond = indUpper;
						isEDU = 1;
						first = LabelTools.convertIntoListOfVoices(voiceLab.get(indFirst)).get(0);
						second = LabelTools.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
//						voicesEDU.set(indLower, new Integer[]{first, second, indUpper, 1});
//						voicesEDU.set(indUpper, new Integer[]{first, second, indLower, 1});
//						voicesUnison.set(indLower, new Integer[]{first, second, indUpper, 1});
//						voicesUnison.set(indUpper, new Integer[]{first, second, indLower, 1});
					}
					voicesUnison.set(indLower, new Integer[]{first, second, indUpper, isEDU});
					voicesUnison.set(indUpper, new Integer[]{first, second, indLower, isEDU});

					// 3. Adapt NoteSequence
					if (durLower.isLess(durUpper)) {
						noteSeq.swapNotes(indLower, indUpper);
					}

					// 4. Adapt voice labels
					if (durLower.isLess(durUpper)) {
						if (adaptLabels) {
							Collections.swap(voiceLab, indLower, indUpper);
						}
					}
				}
			}
			notesPreceding += ch.get(i).size();
		}

		// Reset noteSequence, voice labels; set voicesEDU, voicesIDU, voicesUnison
//		System.out.println(noteSeq.get(12));
//		System.out.println(noteSeq.get(13));
//		System.out.println(getNoteSequence().get(12));
//		System.out.println(getNoteSequence().get(13));
//		setNoteSequence(noteSeq);
//		if (adaptLabels) {
//			setVoiceLabels(voiceLab);
//		}

//		setVoicesEDU(voicesEDU);
//		setVoicesIDU(voicesIDU);
//		for (int i = 0; i < voicesUnison.size(); i++) {
//			if (voicesEDU.get(i) != null) {
//				voicesUnison.set(i, voicesEDU.get(i));
//			}
//			else if (voicesIDU.get(i) != null) {
//				voicesUnison.set(i, voicesIDU.get(i));
//			}
//		}
//		setVoicesUnison(voicesUnison);

		return voicesUnison;
	}


	private List<Integer[]> makeVoicesUnisonOLD() {
		// Get indices of upper and lower unison note as above
		// Get notes (in noteseq) and voices (in voiceLabels)
		// If duration the same: lower voice first in noteSeq and voiceLabels
		// voicesUnison.set(indLower, new Integer[]{voiceLabels.get(indLower), voiceLabels.get(indUpper), indUpper, 1}); 
		// voicesUnison.set(indUpper, new Integer[]{voiceLabels.get(indLower), voiceLabels.get(indUpper), indLower, 1}); 
		// If duration different: voice with longer note first in noteSeq and voiceLabels
		// voicesUnison.set(indLower, new Integer[]{voiceLabels.get(indLower), voiceLabels.get(indUpper), indUpper, 0}); 
		// voicesUnison.set(indUpper, new Integer[]{voiceLabels.get(indLower), voiceLabels.get(indUpper), indLower, 0}); 
		NoteSequence noteSeq = null; //getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<Integer[]> voicesUnison = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));
		int notesPreceding = 0;
		List<List<Note>> ch = getChords();
		for (int i = 0; i < ch.size(); i++) {
			Integer[][] unisonInfo = getUnisonInfo(ch.get(i));
			// If the chord contains a unison note pair
			if (unisonInfo != null) {
				// For each unison note pair in the chord (there should be only one)
				for (int j = 0; j < unisonInfo.length; j++) {					
					// 1. Determine indices
					// a. Indices of the lower and upper unison note   
					int indLower = notesPreceding + unisonInfo[j][1];
					int indUpper = notesPreceding + unisonInfo[j][2];
					int voiceLower = LabelTools.convertIntoListOfVoices(voiceLab.get(indLower)).get(0);
					int voiceUpper = LabelTools.convertIntoListOfVoices(voiceLab.get(indUpper)).get(0); 
					boolean isEDU = 
						noteSeq.get(indLower).getMetricDuration().equals(noteSeq.get(indUpper).getMetricDuration());
					voicesUnison.set(indLower, new Integer[]{voiceLower, voiceUpper, indUpper, isEDU ? 1 : 0});
					voicesUnison.set(indUpper, new Integer[]{voiceLower, voiceUpper, indLower, isEDU ? 1 : 0});
				}
			}
			notesPreceding += ch.get(i).size();
		}
		return voicesUnison;
	}


	private void setVoicesEDU(List<Integer[]> arg) {
		voicesEDU = arg;
	}


	private void setVoicesIDU(List<Integer[]> arg) {
		voicesIDU = arg;
	}


	private void setMeterInfo(List<Integer[]> argMeterInfo) {
		meterInfo = argMeterInfo;
	}


	private void setVoicesSNU(List<Integer[]> argVoicesCoDNotes) {
		voicesSNU = argVoicesCoDNotes;
	}


	/** 
	 * Handles single-note unisons (SNUs). For each SNU note pair,
	 * 
	 * <ul>
	 * <li>Removes one SNU note from the NoteSequence
	 *     <ul>
	 *     <li>If the SNU notes have different durations: the SNU note that has the shorter duration.</li> 
	 *     <li>If the SNU notes have the same duration: the upper (higher-voice) SNU note.</li> 
	 *     </ul>
	 * </li>
	 * <li>Combines the voice labels of the SNU notes into one label and adapts the list of voice
	 *     labels accordingly: sets the label of the lower SNU note to the result, and removes the 
	 *     label of the upper SNU note. 
	 *     NB: Not if t == Type.PREDICTED (in which case the labels already have their final form).</li>
	 * <li>Combines the duration labels of the SNU notes into one label and adapts the list of duration
	 *     labels accordingly: sets the label of the lower SNU note to the result, and removes the 
	 *     label of the upper SNU note. 
	 *     NB: Not if t == Type.PREDICTED (in which case the labels already have their final form).</li>
	 * <li>Lists the SNU voices with the voice that contains the note that has the longer duration (or, 
	 *     if they have the same duration, the lower voice) first, and sets voicesSNU accordingly. A 
	 *     consistent ordering of the SNU voices is necessary for a consistent evaluation.</li>
	 * </ul>
	 * 
	 * NB1: This method presumes that a chord contains only one SNU, and neither a unison nor a 
	 *      course crossing.<br>
	 * NB2: Tablature case only; must be called before handleCourseCrossings().
	 * 
	 * @param tablature
	 * @param t
	 */
	// TESTED
	private void handleSNUsOLD(Tablature tablature, Type t) {
		NoteSequence noteSeq = null; //getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<Double>> durationLab = getDurationLabels();
		List<Integer[]> voicesSNU = 
			new ArrayList<Integer[]>(Collections.nCopies(tablature.getBasicTabSymbolProperties().length, null));

		boolean adaptLabels = t != Type.PREDICTED;

		// 1. Adapt NoteSequence, voice and duration labels; set voicesSNU
		int notesPreceding = 0;
		List<List<TabSymbol>> tablatureChords = tablature.getChords();
		// NB If any SNUs are found in the for-loop, chord becomes outdated. If it is to be used
		// after the for-loop, it must therefore be recalculated
		List<List<Note>> ch = getChords();
//		List<List<Note>> chords = getNoteSequenceChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			Integer[][] SNUInfo = getSNUInfo(ch.get(i), tablatureChords.get(i));
			// If the chord contains a SNU note pair
			if (SNUInfo != null) {
//			if (getSNUInfo(tablatureChords, i) != null) {
//				Integer[][] SNUInfo = getSNUInfo(tablatureChords, i);
				// For each SNU note pair in the chord (there should only be one)
				int notesRemovedFromChord = 0;
				for (int j = 0; j < SNUInfo.length; j++) {
					// 1. Determine indices
					// a. Indices of the lower and upper SNU note
					int indLower = notesPreceding + (SNUInfo[j][1] - notesRemovedFromChord);
					int indUpper = notesPreceding + (SNUInfo[j][2] - notesRemovedFromChord);
					// b. Indices of the longer and shorter SNU note
					Rational durLower = noteSeq.getNoteAt(indLower).getMetricDuration();
					Rational durUpper = noteSeq.getNoteAt(indUpper).getMetricDuration();
					int indLonger = 
						durLower.isGreater(durUpper) ? indLower : (durLower.isLess(durUpper) ? indUpper : -1);
					int indShorter = 
						durLower.isGreater(durUpper) ? indUpper : (durLower.isLess(durUpper) ? indLower: -1);

					// 2. Set voicesSNU
					int first, second;
					if (t != Type.PREDICTED) {
						// Determine first and second voice from uncombined voice labels. The SNU notes can 
						// have different durations.
						int indFirst = !durLower.equals(durUpper) ? indLonger : indLower;
						int indSecond = !durLower.equals(durUpper) ? indShorter : indUpper;
						first = LabelTools.convertIntoListOfVoices(voiceLab.get(indFirst)).get(0);
						second = LabelTools.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
					}
					else {
						// Determine first and second voice from combined voice label. The SNU notes always 
						// have the same duration (only one duration is predicted), so the first voice is the  
						// lower voice from the combined voice label
						int indFirst = indLower;
						int indSecond = indFirst;
						first = LabelTools.convertIntoListOfVoices(voiceLab.get(indFirst)).get(1);
						second = LabelTools.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
					}
					voicesSNU.set(indLower, new Integer[]{first, second});

					// 3. Adapt NoteSequence
					noteSeq.deleteNoteAt(!durLower.equals(durUpper) ? indShorter : indUpper);

					// 4. Adapt voice and duration labels
					if (adaptLabels) {
						voiceLab.set(indLower, combineLabels(voiceLab.get(indLower), voiceLab.get(indUpper)));
						voiceLab.remove(indUpper);
						durationLab.set(indLower, combineLabels(durationLab.get(indLower), durationLab.get(indUpper)));
						durationLab.remove(indUpper);
					}
					// In case the chord contains multiple SNUs 
					notesRemovedFromChord++;

					handledNotes = handledNotes.concat("  SNU found in chord " + i + ": note no. " + (indShorter	
						- notesPreceding) +	" (pitch " + SNUInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
						"list of voice labels and list of durations adapted accordingly." + "\n");
				}
			}
			notesPreceding += tablatureChords.get(i).size();
		}

		// Reset NoteSequence, voice labels, duration labels; set voicesSNU
//		setNoteSequence(noteSeq);
		if (adaptLabels) {
			setVoiceLabels(voiceLab);
			setDurationLabels(durationLab);
		}
		setVoicesSNU(voicesSNU);
	}


	/** 
	 * Handles course crossing notes. For each course crossing note pair,
	 * 
	 * <ul>
	 * <li>Swaps the course crossing notes in the NoteSequence so that the course crossing note that has 
	 *     the higher pitch (the lower-course course crossing note) comes first.</li>
	 * <li>Swaps the voice labels of the course crossing notes. 
	 *     NB: Not if t == Type.PREDICTED (in which case the labels already have their final form).</li>
	 * <li>Swaps the duration labels of the course crossing notes. 
	 *     NB: Not if t == Type.PREDICTED (in which case the labels already have their final form).</li>
	 * </ul>
	 *  
	 * NB1: This method presumes that a chord contains only one course crossing, and neither a SNU nor 
	 *      a unison.<br>
	 * NB2: Tablature case only; must be called after handleSNUs().
	 * 	  
	 * @param tablature
	 * @param t
	 */
	// TESTED
	private void handleCourseCrossingsOLD(Tablature tablature, Type t) {
		NoteSequence noteSeq = null; //getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<Double>> durationLab = getDurationLabels();

		boolean adaptLabels = t != Type.PREDICTED;

		// 1. Adapt NoteSequence, voice and duration labels
		int notesPreceding = 0;
		List<List<TabSymbol>> tabChords = tablature.getChords();
		for (int i = 0; i < tabChords.size(); i++) {
			List<Integer[]> courseCrossingInfo = null; //tablature.getCourseCrossingInfo(i);
			// If the chord contains a course crossing note pair
			if (courseCrossingInfo != null) {
				// For each course crossing note pair in the chord (there should be only one)
				for (int j = 0; j < courseCrossingInfo.size(); j++) {					
					// 1. Determine indices of the lower and upper course crossing note
					int indLower = notesPreceding + courseCrossingInfo.get(j)[2];
					int indUpper = notesPreceding + courseCrossingInfo.get(j)[3];

					// 2. Adapt NoteSequence
					noteSeq.swapNotes(indLower, indUpper);

					// 3. Adapt voice and duration labels
					if (adaptLabels) {
						Collections.swap(voiceLab, indLower, indUpper);
						Collections.swap(durationLab, indLower, indUpper);
					}

					handledNotes = handledNotes.concat("  Course crossing found in chord " + i + ": notes no. " + 
						courseCrossingInfo.get(j)[2] + " (pitch " + courseCrossingInfo.get(j)[0]	+ ") and " + courseCrossingInfo.get(j)[3] +
						" (pitch " + courseCrossingInfo.get(j)[1] + ") in that chord swapped in the NoteSequence; "+ "list of " + 
						"voice labels and list of durations adapted accordingly." + "\n");
				}
			}
			notesPreceding += tabChords.get(i).size();
		}
		// Reset NoteSequence, voice labels, duration labels
//		setNoteSequence(noteSeq);
		if (adaptLabels) {
			setVoiceLabels(voiceLab);
			setDurationLabels(durationLab);
		}
	}


	/** 
	 * Sets the primary and secondary voice for each SNU. If the two voices involved in the SNU have a note 
	 * with the same duration, the lower voice is set as the primary SNU voice; else, the voice that has the 
	 * note with the longer duration is set as the primary SNU voice.
	 *  
	 * @param vl
	 * @param dl
	 * @param voiceLongerShorter Contains, for each SNUnote, an Integer[] containing <br>
	 *        <ul>
	 *        <li>As element 0: of the two voices involved in the SNU, the voice that has the SNU note with the 
	 *                          longer duration</li>
	 *        <li>As element 1: of the two voices involved in the SNU, the voice that has the SNU note with the 
	 *                          shorter duration</li>
	 *        </ul>
	 * @return A List<Integer[]> containing, for each note <br>
	 *         <ul>
	 *         <li>if the note is not a SNU, <code>null</code></li>
	 *         <li>if the note is a SNU, an Integer[] containing</li>
	 * 	           <ul>
	 *             <li>As element 0: if the two voices involved in the SNU have a note with the same duration, 
	 *                               the lower voice; else, the voice that has the note with the longer duration</li>
	 *             <li>As element 1: if the two voices involved in the SNU have a note with the same duration, 
	 *                               the higher voice; else, the voice that has the note with the shorter duration</li>
	 *             </ul>
	 *         </ul>
	 */
	// TESTED
	private static List<Integer[]> determineVoicesSNU(List<List<Double>> vl, List<List<Double>> dl, 
		List<Integer[]> voiceLongerShorter) {
		List<Integer[]> voicesSNU = new ArrayList<>();
		for (int i = 0; i < vl.size(); i++) {
			List<Double> voiceLbl = vl.get(i);
			List<Double> durLbl = dl.get(i);
			Integer[] vls = voiceLongerShorter.get(i);
			// Default case
			if (Collections.frequency(voiceLbl, 1.0) == 1) {
				voicesSNU.add(null);
			}
			// SNU case
			else {
				// If the voices involved in the SNU have the same duration: add lower voice first
				if (Collections.frequency(durLbl, 1.0) == 1) {
					voicesSNU.add(new Integer[]{voiceLbl.lastIndexOf(1.0), voiceLbl.indexOf(1.0)});
				}
				// If not: add voice that has the note with the longer duration first 
				else {
					voicesSNU.add(vls);
//					voicesSNU.add(new Integer[]{vls[0], vls[1]});
				}
			}
		}
		return voicesSNU;
	}


	/** 
	 * Initialises the voice labels<br>
	 * <ul>
	 * <li>If no argument (i.e., <code>null</code>) is given: from the initialised NoteSequence.</li>
	 * <li>Else, with the given labels.</li>
	 * </ul> 
	 *
	 * NB: Must be called after initialiseNoteSequence().
	 * 
	 * @param argVl The voice labels to initialise with.
	 */
	// TESTED
	private void initialiseVoiceLabels(List<List<Double>> argVl) {
		if (argVl == null) {
			List<List<Double>> vl = new ArrayList<List<Double>>(); 
//			getNoteSequence().forEach(n -> vl.add(createVoiceLabel(findVoice(n))));
			setVoiceLabels(vl);
		}
		else {
			setVoiceLabels(argVl);
		}
	}


	/**
	 * Initialises the duration labels<br>
	 * <ul>
	 * <li>If no argument (i.e., <code>null</code>) is given: from the initialised NoteSequence.</li>
	 * <li>Else, with the given labels.</li>
	 * </ul> 
	 * 
	 * NB: Tablature case only; must be called after initialiseNoteSequence().
	 * 
	 * @param argDl The duration labels to initialise with.
	 */
	// TESTED
	private void initialiseDurationLabels(List<List<Double>> argDl) {
		if (argDl == null) {
			List<List<Double>> dl = new ArrayList<List<Double>>();
//			getNoteSequence().forEach(n -> 
//				dl.add(createDurationLabel(Tablature.getTabSymbolDur(n.getMetricDuration()))));
			setDurationLabels(dl);
		}
		else {
			setDurationLabels(argDl);
		}
	}


	/**
	 * Initialises the duration labels.
	 * 
	 * If <code>null</code> is given as argument, the duration labels are initiated from the initial, 
	 * unadapted NoteSequence; else, they are initialised with the given labels.
	 * 
	 * Each duration label is binary vector containing n = Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()/3 
	 * elements, where the position of the 1.0 indicates the full duration encoded (index 0 denotes a 
	 * duration of 1/n, position 1 a duration of 2/n, etc.).  
	 * 
	 * NB: Tablature case only; must be called after initialiseNoteSequence().
	 * 
	 * @param dl The duration labels to initialise with.
	 */
	// TESTED (for both tablature- and non-tablature case simultaneously)
	private void initialiseDurationLabelsOLD(List<List<Double>> dl) {
		if (dl == null) {
			List<List<Double>> initialDurationLabels = new ArrayList<List<Double>>();

			// Iterate through all notes in the initial NoteSequence, which for each CoD still contains both
			// CoDnotes. Lower CoDnotes are always in the lower voice and thus come first in the NoteSequence   
			NoteSequence initialNoteSeq = null; //getNoteSequence();
			for (Note n : initialNoteSeq) {
				Rational durationCurrentNote = n.getMetricDuration();
				int numer = durationCurrentNote.getNumer();
				int denom = durationCurrentNote.getDenom();
				// Determine the duration in 32nd/3 notes
				// NB: Tablature.SRV_DEN/denom will always be divisible by denom because denom 
				// will always be a fraction of Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()/3: 
				// 32, 16, 8, 4, 2, or 1
				int duration = numer * (Tablature.SRV_DEN/denom);
				// Create the durationLabel for n and add it to initialDurationLabels
//				initialDurationLabels.add(createDurationLabel(duration));
			}
			// Set durationLabels
			setDurationLabels(initialDurationLabels);
		}
		else {
			setDurationLabels(dl);
		}
	}


//	private void setNoteSequence(NoteSequence arg) {
//		noteSequence = arg;
//	}


	/**
	 * Transposes the given <code>Piece</code> by the given interval.
	 *  
	 * @param argPiece
	 * @param transposition
	 * @return
	 */
	private static Piece transposePiece(Piece argPiece, int transposition) {
		argPiece.setHarmonyTrack(transposeHarmonyTrack(argPiece.getHarmonyTrack(), 
			transposition));
		argPiece.setScore(transposeNotationSystem(argPiece.getScore(), transposition));
		return argPiece;
	}


	/**
	 * Diminutes the given <code>Piece</code> according to the given <code>Timeline</code> 
	 * from the <code>Tablature</code>.
	 * 
	 * @param argPiece
	 * @param tl
	 * @return
	 */
	private static Piece diminutePiece(Piece argPiece, Timeline tl) {
		Piece pieceDim = new Piece();
		MetricalTimeLine mtl = argPiece.getMetricalTimeLine();		
		MetricalTimeLine mtlDim = diminuteMetricalTimeLine(mtl, tl);
		argPiece.setMetricalTimeLine(mtlDim);
		argPiece.setHarmonyTrack(diminuteHarmonyTrack(argPiece.getHarmonyTrack(), tl, mtl, mtlDim));
		argPiece.setScore(diminuteNotationSystem(argPiece.getScore(), tl, mtl, mtlDim));
		return argPiece;
	}


	private void setNoteSequence() {
//		noteSequence = makeNoteSequence();
	}


	/**
	 * Makes the NoteSequence, in which all notes from the Piece are ordered hierarchically by<br>
	 * <ul>
	 * <li>(1) Onset time (lower first).</li>
	 * <li>(2) If two notes have the same onset time: pitch (lower first).</li>
	 * <li>(3) If two notes have the same onset time and the same pitch: voice (lower first).</li>
	 * </ul>
	 * 
	 * After initialisation, any SNU notes (tablature case), course crossing notes (tablature case), 
	 * and unison notes (non-tablature case) need further handling. 
	 * <ul>
	 * <li>SNU notes must be merged; this is done in <code>handleSNUs()</code>.</li> 
	 * <li>Course crossing notes must be swapped so that the course crossing note that has the 
	 *     higher pitch (the lower-course course crossing note) comes first; this is done 
	 *     in <code>handleCourseCrossings()</code>.</li>
	 * <li>Unison notes must be swapped so that the unison note that has the longer duration 
	 *     comes first; this is done in <code>handleUnisons()</code>.</li>
	 * </ul>
	 * 
	 * Any unison notes (tablature case) need no further handling, as the lower-course unison
	 * note automatically always comes first.
	 *               
	 * NB: Must be called before initialiseVoiceLabels().
	 */
	// TESTED
	private NoteSequence makeNoteSequence() {
		NoteSequence noteSeq = new NoteSequence(Comparator
			.comparing(Note::getMetricTime)
			.thenComparing(Note::getMidiPitch)
			.thenComparing(this::findVoice, Comparator.reverseOrder()));
		getScorePiece().getScore().getContentsRecursiveList(null).stream()
		.filter(c -> c instanceof Note)
		.forEach(c -> noteSeq.add((Note) c));
		return noteSeq;
	}


	/**
	 * Gets the chords from the NoteSequence.
	 * NB If the NoteSequence is in its final form, getChords() should be called.
	 * 
	 * @return
	 */
	// TESTED
	private List<List<Note>> getChordsFromNoteSequence() {
		List<List<Note>> ch = new ArrayList<List<Note>>();

		NoteSequence noteSeq = null; //getNoteSequence();
		List<Note> currChord = new ArrayList<Note>();
//		Note firstNote = noteSeq.getNoteAt(0);
//		Rational onsetFirstNote = firstNote.getMetricTime();
//		currChord.add(firstNote);
		Rational onsetPrevNote = noteSeq.getNoteAt(0).getMetricTime();
//		// For each Note in noteSeq. The Notes are ordered according to (1) onset time, (2) lowest pitch first (if
//		// notes have the same onset time), (3) if the Notes have the same onset time and pitch: a. lowest voice 
//		// first (if they have the same duration); b. longest duration first (if they have different durations)
		for (int i = 0; i < noteSeq.size(); i++) {
			Note currNote = noteSeq.getNoteAt(i);
			Rational onsetCurrNote = currNote.getMetricTime();
			if (onsetCurrNote.equals(onsetPrevNote)) {
				currChord.add(currNote);
				if (i == noteSeq.size() - 1) {
					ch.add(currChord);
				}
			}
			else {
				ch.add(currChord);
				currChord = new ArrayList<Note>();
				currChord.add(currNote);
			}
			onsetPrevNote = onsetCurrNote;
		}
//		// Add the last chord to ch
//		ch.add(currChord);
		return ch;
	}
	
	
	/**
	 * Initialises durationLabels with the initial, unadapted voice labels -- i.e., the ones that go with the notes
	 * in the initial, unadapted NoteSequence. Each duration label is a List<Double> containing Tablature.SMALLEST_
	 * RHYTHMIC_VALUE.getDenom() elements, one of which has value 1.0 and indicates the encoded full duration (where
	 * position 0 is a duration of 1/32, position 1 a duration of 2/32, etc.), while the others have value 0.0.  
	 * 
	 * NB1: Tablature case only; must be called after initialiseNoteSequence().
	 */
	private void initialiseDurationLabelsOLD() {
		List<List<List<Double>>> initialDurationLabels = new ArrayList<List<List<Double>>>();
		Double[] emptyLabelArray = new Double[Tablature.SRV_DEN];
		Arrays.fill(emptyLabelArray, 0.0);
		List<Double> emptyLabel = Arrays.asList(emptyLabelArray);

		// Iterate through all notes in the initial NoteSequence, which for each CoD still contains both
		// CoDnotes. Lower CoDnotes are always in the lower voice and thus come first in the NoteSequence   
		NoteSequence initialNoteSeq = null; //getNoteSequence();
		for (Note n : initialNoteSeq) {
			List<List<Double>> currentDurationLabels = new ArrayList<List<Double>>();
			List<Double> durationLabelCurrentNote = new ArrayList<Double>(emptyLabel);
			Rational durationCurrentNote = n.getMetricDuration();
			int numer = durationCurrentNote.getNumer();
			int denom = durationCurrentNote.getDenom();
			// Set the correct element of durationLabelCurrentNote to 1.0
			int indexToSet = numer - 1;
			if (denom != Tablature.SRV_DEN) {
				indexToSet = (numer * (Tablature.SRV_DEN/denom)) - 1;
			}
			durationLabelCurrentNote.set(indexToSet, 1.0);
			// Add durationLabelCurrentNote to currentDurationLabels; add currentDurationLabels to initialDurationLabels
			currentDurationLabels.add(durationLabelCurrentNote);
			initialDurationLabels.add(currentDurationLabels);
		}
		// Set durationLabels
		setDurationLabelsOLD(initialDurationLabels);
//		durationLabels = initialDurationLabels;
	}


	private void setDurationLabelsOLD(List<List<List<Double>>> argDurationLabels) {
//		durationLabelsOLD = argDurationLabels;
	}


//	private List<List<List<Double>>> getDurationLabelsOLD() {
//		return durationLabelsOLD;
//	}
	
	
	
	/**
	 * Constructor for the ground truth Transcription.
	 *                              
	 * @param argMidiFile
	 * @param argEncodingFile
	 */
	private Transcription(File argMidiFile, File argEncodingFile, boolean bla) {
		// Create and set the ground truth Piece
		Piece groundTruthPiece = MIDIImport.importMidiFile(argMidiFile);
//		long[][] rah = groundTruthPiece.getMetricalTimeLine().getTimeSignature();
//		for (long[] l : rah) {
//			System.out.println(Arrays.toString(l));
//		}
//		KeyMarker r = groundTruthPiece.getMetricalTimeLine().getKeyMarker(new Rational(0, 4));
//		System.out.println(r);
//		System.exit(0);		
		setPiece(groundTruthPiece);
		
		// Create the Transcription based on the ground truth Piece
		boolean normaliseTuning = false;
		boolean isGroundTruthTranscription = true;
//		createTranscription(argMidiFile.getName(), argEncodingFile, normaliseTuning, isGroundTruthTranscription);
	}


//	/**
//	 * Turns the given Tablature into a Transcription, using the given voices and durations.
//	 *  
//	 * @param tablature
//	 * @param voices
//	 * @param durations
//	 * @param numberOfVoices
//	 */
//	public Transcription(Tablature tablature, List<List<Integer>> voices, List<Rational[]> durations,
//		int numberOfVoices) {
//
//		// Make an empty Piece with the given number of voices
//		Piece piece = new Piece();
//		NotationSystem system = piece.createNotationSystem();
//		for (int i = 0; i < numberOfVoices; i++) {
//			NotationStaff staff = new NotationStaff(system); 
//			system.add(staff);
//			NotationVoice voice = new NotationVoice(staff); 
//			staff.add(voice);
//
//			Note voice0n0 = Transcription.createNote(67, new Rational(0, 4), new Rational(1, 2));
//			voice.add(voice0n0); 
//		}
//
//		// Iterate through the Tablature, convert each TabSymbol into a note, and add it to the given voice
//		Integer[][] btp = tablature.getBasicTabSymbolProperties();
//		for (int i = 0; i < btp.length; i++) {
//			// Create a Note from the TabSymbol at index i
//			int pitch = btp[i][Tablature.PITCH];
//			Rational metricTime = new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//			Rational metricDuration = durations.get(i)[0]; // [0] is possible because each element in durations currently contains only one Rational
//			Note note = createNote(pitch, metricTime, metricDuration);
//
//			// Add the Note to each voice in currentVoices
//			List<Integer> currentVoices = voices.get(i);
//			for (int v : currentVoices) {
//				NotationVoice voice = piece.getScore().get(v).get(0);
//				voice.add(note);
//			}	
//		}
//
//		// Set the Piece in the Transcription
//		setPiece(piece);
//	}


	/**
	 * Constructor. Creates a new Transcription out of the given arguments; sets the class fields pieceName, file, 
	 * piece, noteSequence, voiceLabels, and
	 *  In the tablature case: sets the class field durationLabels, aligns the Tablature and Transcription (and 
	 *     sets the class field voicesCoDNotes), checks the encoding for alignment errors (if any are found, 
	 *     a RuntimeException is thrown), and transposes the Transcription.
	 *   In the non-tablature case (where argEncodingFile is <code>null</code>): sets the class fields meterInfo,
	 *   handles unisons (and sets the class field equalDurationUnisonsInfo), and sets the class field 
	 *   basicNoteProperties.
	 *  
	 * Applies to the bi-directional model only.
	 *
	 * @param argMidiFile
	 * @param argEncodingFile
	 * @param argPiece
	 * @param argVoiceLabels
	 * @param argDurationLabels
	 * @param argVoicesCoDNotes
	 * @param argEqualDurationUnisonsInfo 
	 */
	private Transcription(File argMidiFile, File argEncodingFile, Piece argPiece, List<List<Double>> argVoiceLabels,
		List<List<Double>> argDurationLabels, List<Integer[]> argVoicesCoDNotes, List<Integer[]> 
		argEqualDurationUnisonsInfo, boolean bla) {

		setName(); 
//		setFile(argMidiFile);
		setPiece(argPiece);

		makeNoteSequence(); // needs piece    
		setVoiceLabels(argVoiceLabels);
		// a. In the tablature case
		if (argEncodingFile != null) {
			setDurationLabels(argDurationLabels);
			// 1. Check chords. The argument normaliseTuning must be set to true (because argPiece
			// has already been normalised) for the final alignment check below
			Tablature tablature = new Tablature(argEncodingFile, true);
			if (checkChords(tablature, null) == false) { // needs noteSequence
				System.out.println(chordCheck);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleSNUsOLD(tablature, null); // needs noteSequence, voiceLabels, and durationLabels 
//			handleCoDNotes(tablature, false); // needs noteSequence, voiceLabels, and durationLabels 
			setVoicesSNU(argVoicesCoDNotes);
			handleCourseCrossingsOLD(tablature, null); // needs noteSequence, voiceLabels, and durationLabels
//			handleCourseCrossings(tablature, false); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tablature, null) == false) {
				System.out.println(alignmentCheck);
					throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose
//			transpose(tablature.getTranspositionInterval());
		}
		// b. In the non-tablature case
		else {
//			setMeterInfo(argMidiFile); // needs file
			handleUnisons((List<Note>) null); // needs noteSequence and voiceLabels
//			handleUnisons(false); // needs noteSequence and voiceLabels
			setVoicesEDU(argEqualDurationUnisonsInfo);
			setBasicNoteProperties(); // needs noteSequence
		}   
	}


	/**
	 * Constructor. Creates a new Transcription out of the given arguments; sets the class fields piece, 
	 * noteSequence, voiceLabels, and
	 *  In the tablature case: sets the class field durationLabels, aligns the Tablature and Transcription (and 
	 *     sets the class field voicesCoDNotes), checks the encoding for alignment errors (if any are found, 
	 *     a runTimeException is thrown), and transposes the Transcription.
	 *   In the non-tablature case: sets the class fields meterInfo, handles unisons (and sets the class field
	 *     equalDurationUnisonsInfo), and sets the class field basicNoteProperties.
	 *  
	 * Applies to the bi-directional model only.
	 *
	 * @param tablature
	 * @param argPiece
	 * @param argVoiceLabels
	 * @param argDurationLabels
	 * @param argVoicesCoDNotes
	 * @param argMeterInfo
	 * @param argEqualDurationUnisonsInfo 
	 */
	private Transcription(Tablature tablature, Piece argPiece, List<List<Double>> argVoiceLabels, List<List<Double>> 
		argDurationLabels, List<Integer[]> argVoicesCoDNotes, List<Integer[]> argMeterInfo, List<Integer[]> 
		argEqualDurationUnisonsInfo) {
		setName();
		setPiece(argPiece);
		makeNoteSequence(); // needs piece    
		setVoiceLabels(argVoiceLabels);
		// a. In the tablature case
		if (tablature != null) {
			setDurationLabels(argDurationLabels);
			// 1. Check chords
			if (checkChords(tablature, null) == false) { // needs noteSequence
				System.out.println(chordCheck);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleSNUsOLD(tablature, null); // needs noteSequence, voiceLabels, and durationLabels 
//			handleCoDNotes(tablature, false); // needs noteSequence, voiceLabels, and durationLabels 
			setVoicesSNU(argVoicesCoDNotes);
			handleCourseCrossingsOLD(tablature, null); // needs noteSequence, voiceLabels, and durationLabels
//			handleCourseCrossings(tablature, false); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tablature, null) == false) {
				System.out.println(alignmentCheck);
				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose
			transpose(tablature.getTranspositionInterval());
		}
		// b. In the non-tablature case
		else {
			setMeterInfo(argMeterInfo);
			handleUnisons((List<Note>) null); // needs noteSequence and voiceLabels
//			handleUnisons(false); // needs noteSequence and voiceLabels
			setVoicesEDU(argEqualDurationUnisonsInfo);
			setBasicNoteProperties(); // needs noteSequence
		}   
	}


	private Transcription(Piece argPiece, List<List<Double>> voiceLabels, List<List<Double>> durationLabels) {
		setPiece(argPiece);
		makeNoteSequence();    
		setVoiceLabels(voiceLabels);
		setDurationLabels(durationLabels);
//		handleCoDNotes(tablature, false);
//		setVoicesCoDNotes(voicesCoDNotes);
//		handleCourseCrossings(tablature, false);
	}


	/**
	 * Creates a new Transcription from the given Piece and Encoding. 
	 *
	 * NBs for the tablature case 
	 * (1) If isGroundTruthTranscription is <code>true</code>, which is only not the case for a 
	 *     predicted Transcription, the Transcription is transposed.
	 * (2) The Tablature object as used in this method (which is NOT the Tablature object that 
	 *     forms a TablatureTranscriptionPair with the Transcription!), which serves for alignment
	 *     checking, must be in the same key as the Transcription's piece in order for the alignment
	 *     (done in handleCoDNotes(), handleCourseCrossings(), and checkAlignment()) to succeed.
	 *     Therefore, the argument normaliseTuning, needed to create this Tablature object, is 
	 *     <code>false</code> when creating the ground truth Transcription: the Transcription's
	 *       Piece is only normalised/transposed after the alignment (in transpose())
	 *     <code>true</code> when creating a predicted Transcription: the Transcription's Piece 
	 *       has already been normalised/transposed correctly because it was created from a 
	 *       normalised Tablature (in TrainingManager.prepareTraining())
	 * 
	 * @param p
	 * @param encoding
	 * @param normaliseTuning    
	 * @param isGroundTruthTranscription                        
	 */
	private void createTranscriptionOLD(Piece p, Encoding encoding, boolean normaliseTuning, 
		boolean isGroundTruthTranscription) {

		// TODO Make copy rather than store and retrieve
		String fPath = "C:/Users/Reinier/Desktop/copy.mid";
		fPath = "F:/research/" + "copy.mid";
//		MIDIExport.exportMidiFile(p, Arrays.asList(new Integer[]{MIDIExport.DEFAULT_INSTR}), fPath);
		Piece pUn = MIDIImport.importMidiFile(new File(fPath));
		new File(fPath).delete();

		setPiece(p);
		setUnaugmentedScorePiece();
		String pName = p.getName();
		setName();
//		setName(pName.contains(MIDIImport.EXTENSION) ? 
//			pName.substring(0, pName.indexOf(MIDIImport.EXTENSION)) : pName);
				
		makeNoteSequence(); // needs piece	
		initialiseVoiceLabels(null); // needs piece and noteSequence
		Tablature tab = null;
		// a. In the tablature case
		if (encoding != null) {
			// The duration labels have their final form when t == Type.PREDICTED
			if (isGroundTruthTranscription) {
				initialiseDurationLabels(null); // needs noteSequence
			}

			// 1. Check chords 
			// NB: normaliseTuning is false when creating a ground truth Transcription and true 
			// when creating a predicted Transcription (see Javadoc for this method)
			tab = new Tablature(encoding, normaliseTuning);
			if (checkChords(tab, null) == false) { // needs noteSequence
				System.out.println(chordCheck);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleSNUsOLD(tab, null); // needs noteSequence, voiceLabels, and durationLabels
			handleCourseCrossingsOLD(tab, null); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tab, null) == false) {
				System.out.println(alignmentCheck);
				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose (only if ground truth Transcription; see Javadoc for this method)
			if (isGroundTruthTranscription) {
				transpose(tab.getTranspositionInterval());
			}
			setMeterInfo(tab.getMeterInfo());
//			setMeterInfo(tab.getTimeline().getMeterInfoOBS());
			setKeyInfo(); // must be done after possible transpose()
			setChords(); // sets the final version of the transcription chords
			setMinimumDurationLabels(tab);
		}
		// b. In the non-tablature case
		else {
			setMeterInfo();
			setKeyInfo();
			handleUnisons((List<Note>) null); // needs noteSequence and voiceLabels
			setBasicNoteProperties(); // needs noteSequence	
			setChords(); // sets the final version of the transcription chords
			setNumberOfNewNotesPerChord(); // needs transcriptionChords
		}
		// c. In both
		if (isGroundTruthTranscription) {
			setChordVoiceLabels(tab); // needs transcriptionChords
		}
		else {
			// Currently no chordVoiceLabels needed in bidir model
		}
	}


	/**
	 * Populates the NoteSequence, i.e, adds the notes from the Piece ordered (1) by onset 
	 * time (lower first), and (2) by pitch (lower first).
	 */
	// TESTED
	private NoteSequence makeInitialNoteSequence() {
		// The NoteTimePitchComparator ensures that, when adding a note to the NoteSequence, 
		// it is ordered (1) by onset time (lower first), and (2) by pitch (lower first). 
		// NB If no Comparator is used, it is not guaranteed that the NoteSequence will 
		// contain the notes in the sequence they are added
		NoteSequence noteSeq = new NoteSequence(new NoteTimePitchComparator());
		getScorePiece().getScore().getContentsRecursiveList(null).stream()
			.filter(c -> c instanceof Note)
			.forEach(c -> noteSeq.add((Note) c));
		return noteSeq; 
	}


	/**
	 * Reverses the <code>NotationSystem</code>.
	 *  
	 * @param ns An existing <code>NotationSystem</code>.
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param mp The mirror point.
	 * @return
	 */
	private static NotationSystem reverseNotationSystemOLD(NotationSystem ns, MetricalTimeLine mtl, 
		Rational mp, String name) {
		// For each voice
		NotationSystem nsRev = new NotationSystem();
		for (NotationStaff nst : ns) {	
			NotationStaff nstRev = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvRev = new NotationVoice();
				for (NotationChord nc : nv) {
					NotationChord ncRev = new NotationChord();
					for (Note n : nc) {
						// Calculate the Note's new onset time (mirrorPoint - offset time)
						Rational dur = n.getMetricDuration();
//						// In tablature case: use minimum duration
//						if (tab != null) {
//							for (Rational[] item : onsetsAndMinDurs) {
//								if (item[0].equals(n.getMetricTime())) {
//									dur = item[1];
//									dur.reduce();
//									break;
//								}
//							}
//							
//							// NB: onsets and minDurs are corresponding lists and can be 
//							//     indexed concurrently
//							for (int i = 0; i < onsets.size(); i++) {
//								if (onsets.get(i).equals(n.getMetricTime())) {
//									dur = minDurs.get(i);
//									dur.reduce();
//									break;
//								}
//							}
//						}
						Rational offset = n.getMetricTime().add(dur);
						Rational mt = mp.sub(offset);
						
						// Error in barbetta-1582_1-il_nest.tbp: last chord should be co1 
						// (and not co2), leading to newOnsetTime being -1/2 
						if (name.equals("barbetta-1582_1-il_nest.mid") && mt.equals(new Rational(-1, 2))) {
//						if (pOrig.getName().equals("barbetta-1582_1-il_nest.mid") && 
//							mt.equals(new Rational(-1, 2))) {
							mt = Rational.ZERO;
							dur = new Rational(1, 2);
						}

						ncRev.add(ScorePiece.createNote(n.getMidiPitch(), mt, dur, mtl));
					}
					nc = ncRev;
					nvRev.add(ncRev);
				}
				nstRev.add(nvRev);
			}
			nsRev.add(nstRev);
		}
		return nsRev;
	}


	/**
	 * Removes all sequences of single-note events shorter than the given duration from the
	 * encoding, and lengthens the duration of the event preceding the sequence by the total 
	 * length of the removed sequence. NB: Handles the _unadapted_ Piece. 
	 *
	 * @param t
	 * @param tab
	 * @param dur
	 * @return
	 */
	private static NotationSystem deornamentNotationSystemOLD(NotationSystem nsCopy, MetricalTimeLine mtl, 
		/*Transcription t, Tablature tab,*/ 
		List<List<Note>> ch, List<Rational> onsetTimes, Rational dur) {
//		Piece pDeorn = new Piece();

//		MetricalTimeLine mtl = t.getPiece().getMetricalTimeLine();
//		List<Rational> onsetTimes = t.getAllOnsetTimes();
//		List<List<Note>> ch = t.getChords();
//		List<List<TabSymbol>> tabChords = null;
////		List<Rational[]> onsetsAndMinDurs = null;
//		List<Rational> minDurs = null;
//		if (tab != null) {
//			tabChords = tab.getChords(); 
////			onsetsAndMinDurs = tab.getAllOnsetTimesAndMinDurations();
//			minDurs = tab.getMinimumDurationPerChord();
//		}


//		Piece origP = t.getOriginalPiece();
//		// Make a copy of notationSystem so that pOrig is not affected
//		NotationSystem nsCopy = copyNotationSystem(origP.getScore());
		
		// For each voice
		NotationSystem nsDeorn = new NotationSystem();
//		List<Integer> removed = new ArrayList<>();
//		int voice = 0;
		for (NotationStaff nst : nsCopy) {
//			System.out.println("voice = " + voice);
			NotationStaff nstDeorn = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvDeorn = new NotationVoice();
				NotationChord pre = null;
				Rational durPre = null;
				for (int i = 0; i < nv.size(); i++) {
					NotationChord nc = nv.get(i);
//					System.out.println(nc);
//					Rational onset = nc.getMetricTime();
					int ind = onsetTimes.indexOf(nc.getMetricTime());

//					boolean isOrn; 
//					if (tabChords != null) { 
//						isOrn = tabChords.get(ind).size() == 1 && 
//							minDurs.get(ind).isLess(dur);
////							onsetsAndMinDurs.get(ind)[1].isLess(dur);
//					}
//					else {
//					isOrn = nc.size() == 1 && ch.get(ind).size() == 1 &&
//						nc.getMetricDuration().isLess(dur);
//					}
					boolean isOrn = 
						nc.size() == 1 && ch.get(ind).size() == 1 && 
						nc.getMetricDuration().isLess(dur);
					// If currNc is ornamental
					// NB: In case of a single-event SNU, the note will be removed from both voices
					if (isOrn) {
//						System.out.println(isOrn);
//						removed.add(ind);
						// Determine pre, if it has not yet been determined
						if (pre == null) {
							NotationChord ncPrev = nv.get(i-1);
							pre = ncPrev;
							durPre = ncPrev.getMetricDuration(); // all notes in a NotationChord have the same duration 
						}
						// Increment durPre
						durPre = durPre.add(nc.getMetricDuration());
//						System.out.println(durPre);
					}
					// If currNc is the first after a sequence of one or more ornamental notes
					// (i.e., it does not meet the if condition above but pre != null)
					else if (pre != null) {
//						// Adapt pre
//						pre.setMetricDuration(durPre);
//						for (int j = 0; j < pre.size(); j++) {
////						for (Note n : pre) {
//							Note n = pre.get(j);
//							pre.remove(j);
////							n.setScoreNote(new ScoreNote(new ScorePitch(n.getMidiPitch()), 
////								n.getMetricTime(), durPre));
//							n = createNote(n.getMidiPitch(), n.getMetricTime(), durPre, mtl);
//						}

						// Add adapted pre
						NotationChord preDeorn = new NotationChord();
						for (Note n : pre) {
							preDeorn.add(ScorePiece.createNote(n.getMidiPitch(), n.getMetricTime(), durPre, mtl));
						}
						nvDeorn.remove(pre);
						nvDeorn.add(preDeorn);
						
						// Add currNc
						nvDeorn.add(nc);
						System.out.println(pre);
						// Reset
						pre = null;
//						System.exit(0);
					}
					else {
						nvDeorn.add(nc);
					}
				}
				nstDeorn.add(nvDeorn);
//				nvDeorn.forEach(nc -> System.out.println(nc));
//				System.exit(0);
			}
//			voice++;
			nsDeorn.add(nstDeorn);
		}

		return nsDeorn;

//		pDeorn.setScore(nsDeorn);
//		pDeorn.setMetricalTimeLine(mtl);
//		pDeorn.setHarmonyTrack(origP.getHarmonyTrack());		
//		pDeorn.setName(t.getPiece().getName());
//		return pDeorn;
	}


	/**
	 * Reverses the given harmony track.
	 *  
	 * @param ht An existing (clean) harmony track.
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param mp The mirror point.
	 * @param ki An existing keyInfo.
	 * @return
	 */
	private static SortedContainer<Marker> reverseHarmonyTrackOLD(SortedContainer<Marker> ht, 
		MetricalTimeLine mtl, Rational mp, List<Integer[]> ki) {
		SortedContainer<Marker> htRev = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		int ind = 0; // equals index in ki
		long mpTime = mtl.getTime(mp);
		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				// Calculate reversed mt and t (mirror point - mt/t of next meter section)
				Rational mtNextKeySec = 
					ind == (ki.size() - 1) ? mp : 
					new Rational(ki.get(ind + 1)[KI_NUM_MT_FIRST_BAR], 
					ki.get(ind + 1)[KI_DEN_MT_FIRST_BAR]);
				Rational mtRev = mp.sub(mtNextKeySec);
				long tRev = mpTime - mtl.getTime(mtNextKeySec);
				km.setMetricTime(mtRev);
				km.setTime(tRev);
				htRev.add(km);
				ind++;
			}
		}
		return htRev;
	}


	/**
	 * Rescales the given harmony track.
	 *  
	 * @param ht An existing (clean) harmony track.
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param rescaleFactor
	 * @return
	 */
	private static SortedContainer<Marker> rescaleHarmonyTrackOLD(SortedContainer<Marker> ht, 
		MetricalTimeLine mtl, int rescaleFactor) {
		SortedContainer<Marker> htResc = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		for (Marker m : ht) {
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				Rational mt = km.getMetricTime();
				long t = mtl.getTime(mt);
				km.setMetricTime(
					rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor)) 
				);
				km.setTime(
					rescaleFactor > 0 ? t * rescaleFactor : t / Math.abs(rescaleFactor) 
				);
				htResc.add(km);
			}
		}
		return htResc;
	}


	/**
	 * Reverses the given <code>MetricalTimeLine</code>.
	 * 
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param mp The mirror point.
	 * @param mi An existing meterInfo.
	 * @return
	 */
	private static MetricalTimeLine reverseMetricalTimeLineOLD(MetricalTimeLine mtl, Rational mp, 
		List<Integer[]> mi) {
		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
		// zeroMarker, and endMarker) 
		MetricalTimeLine mtlReversed = new MetricalTimeLine();
		mtlReversed.clear();
		// Add zeroMarker
		mtlReversed.add((Marker) new TimedMetrical(0, Rational.ZERO));

		// Add TimeSignatureMarkers and TempoMarkers 
		int ind = 0; // equals index in mi
		Rational mtRevLastTimedMetrical = Rational.ZERO;
		long mpTime = mtl.getTime(mp);
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {				
				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				TimeSignature ts = tsm.getTimeSignature();
				// Calculate reversed mt and t (mirror point - mt/t of next meter section)
				Rational mtNextMeterSec = 
					ind == (mi.size() - 1) ? mp : 
					new Rational(mi.get(ind + 1)[MI_NUM_MT_FIRST_BAR], 
					mi.get(ind + 1)[MI_DEN_MT_FIRST_BAR]);
				Rational mtRev = mp.sub(mtNextMeterSec);
				long tRev = mpTime - mtl.getTime(mtNextMeterSec);
				mtlReversed.add(new TimeSignatureMarker(ts, mtRev));
				if (mtRev.isGreater(Rational.ZERO)) {
					mtlReversed.add(new TempoMarker(tRev, mtRev));
					if (mtRev.isGreater(mtRevLastTimedMetrical)) {
						mtRevLastTimedMetrical = mtRev;
					}
				}
				ind++;
			}
		}

		// If TempoMarkers (and through them, endMarker(s)) have been added: 
		// remove all TimedMetricals but the zeroMarker	
		if (mtRevLastTimedMetrical.isGreater(Rational.ZERO)) {
			mtlReversed = cleanTimedMetricals(mtlReversed);
		}

		// Add endMarker
		long tRevLastTimedMetrical = mtlReversed.getTime(mtRevLastTimedMetrical);
		TimedMetrical end = 
			calculateEndMarker(tRevLastTimedMetrical, mtl.getTempo(tRevLastTimedMetrical), 
			mtRevLastTimedMetrical, 1);
		mtlReversed.add((Marker) end);		

		return mtlReversed;
	}


	/**
	 * Rescales the given <code>MetricalTimeLine</code>.
	 * 
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param mp The rescaling factor.
	 * @return
	 */
	private static MetricalTimeLine rescaleMetricalTimeLineOLD(MetricalTimeLine mtl, int rescaleFactor) {
		// Start with an empty MetricalTimeLine (clear the default TimeSignatureMarker, 
		// zeroMarker, and endMarker) 
		MetricalTimeLine mtlRescaled = new MetricalTimeLine();
		mtlRescaled.clear();
		// Add zeroMarker
		mtlRescaled.add((Marker) new TimedMetrical(0, Rational.ZERO));

		// Add TimeSignatureMarkers and TempoMarkers 
		Rational mtRescLastTimedMetrical = Rational.ZERO;
		for (Marker m : mtl) {
			if (m instanceof TimeSignatureMarker) {				
				TimeSignatureMarker tsm = (TimeSignatureMarker) m;
				TimeSignature ts = tsm.getTimeSignature();

				// Calculate rescaled ts, mt and t
				Rational mt = m.getMetricTime();
				long t = mtl.getTime(mt);				
//				Rational meter = new Rational(ts.getNumerator(), ts.getDenominator());
//				Rational meterResc = rescaleFactor > 0 ? meter.mul(rescaleFactor) : meter.div(Math.abs(rescaleFactor));
				Rational meter = new Rational(ts.getNumerator(), ts.getDenominator());
				Rational meterResc;
				// Excpetion case where meter is x/1 and rescaling is lengthening
				if (ts.getDenominator() == 1 && rescaleFactor > 1) {
					meterResc = new Rational(meter.getNumer() * rescaleFactor, meter.getDenom());
				}
				else {
					meterResc = TimeMeterTools.undiminuteMeter(meter, rescaleFactor);
				}
				TimeSignature tsResc = new TimeSignature(meterResc);
				Rational mtResc = 
					rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor));
				long tResc = rescaleFactor > 0 ? t * rescaleFactor : t / Math.abs(rescaleFactor);
				
//				Rational mtNextMeterSec = 
//					ind == (mi.size() - 1) ? mp : 
//					new Rational(mi.get(ind + 1)[Timeline.MI_NUM_MT_FIRST_BAR], 
//					mi.get(ind + 1)[Timeline.MI_DEN_MT_FIRST_BAR]);
//				Rational mtRev = mp.sub(mtNextMeterSec);
//				long tRev = mpTime - mtl.getTime(mtNextMeterSec);
				mtlRescaled.add(new TimeSignatureMarker(tsResc, mtResc));
				if (mtResc.isGreater(Rational.ZERO)) {
					mtlRescaled.add(new TempoMarker(tResc, mtResc));
					if (mtResc.isGreater(mtRescLastTimedMetrical)) {
						mtRescLastTimedMetrical = mtResc;
					}
				}
//				ind++;
			}
		}

		// If TempoMarkers (and through them, endMarker(s)) have been added: 
		// remove all TimedMetricals but the zeroMarker	
		if (mtRescLastTimedMetrical.isGreater(Rational.ZERO)) {
			mtlRescaled = cleanTimedMetricals(mtlRescaled);
		}

		// Add endMarker
		long tRevLastTimedMetrical = mtlRescaled.getTime(mtRescLastTimedMetrical);
		TimedMetrical end = 
			calculateEndMarker(tRevLastTimedMetrical, mtl.getTempo(tRevLastTimedMetrical), 
			mtRescLastTimedMetrical, -rescaleFactor);
		mtlRescaled.add((Marker) end);		

		return mtlRescaled;
	}


	/**
	 * Reverses the <code>NotationSystem</code>.
	 *  
	 * @param ns An existing <code>NotationSystem</code>.
	 * @param mtl An existing (clean) <code>MetricalTimeLine</code>.
	 * @param mp The mirror point.
	 * @return
	 */
	private static NotationSystem reverseNotationSystemOLDBetter(NotationSystem ns, MetricalTimeLine mtl, 
		Rational mp, String name) {

		NotationSystem nsRev = new NotationSystem();
		for (NotationStaff nst : ns) {	
			NotationStaff nstRev = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvRev = new NotationVoice();
				for (NotationChord nc : nv) {
					NotationChord ncRev = new NotationChord();
					if (!name.equals("barbetta-1582_1-il_nest.mid")) {
						// Recalculate the onset time (mirrorPoint - offset time)
						nc.forEach(n -> { 
							Rational dur = n.getMetricDuration();
							ncRev.add(ScorePiece.createNote(n.getMidiPitch(), mp.sub(n.getMetricTime().add(dur)), dur, mtl));
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
							ncRev.add(ScorePiece.createNote(n.getMidiPitch(), mt, dur, mtl));
						}
					}
					nvRev.add(ncRev);
				}
				nstRev.add(nvRev);
			}
			nsRev.add(nstRev);
		}
		return nsRev;
	}


	/**
	 * Deornaments the <code>NotationSystem</code>.
	 * 
	 * @param ns
	 * @param mtl
	 * @param ch
	 * @param onsetTimes
	 * @param thresholdDur
	 * @return
	 */
	private static NotationSystem deornamentNotationSystemOLDBetter(NotationSystem ns, MetricalTimeLine mtl, 
		List<List<Note>> ch, List<Rational> onsetTimes, Rational thresholdDur) {

		NotationSystem nsDeorn = new NotationSystem();
		for (NotationStaff nst : ns) {
			NotationStaff nstDeorn = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvDeorn = new NotationVoice();
				for (int i = 0; i < nv.size(); i++) {
					NotationChord nc = nv.get(i);
					int ind = onsetTimes.indexOf(nc.getMetricTime());
					// If nc is ornamental and not part of an ornamental sequence at 
					// the beginning of nv (in which case nvDeorn is still empty):					
					// calculate the total duration of the ornamental sequence; create 
					// ncPrevDeorn with the result; replace ncPrev with ncPrevDeorn
					// NB: It is assumed that an ornamental sequence is not interrupted by (ornamental) rests 
					if ((ch.get(ind).size() == 1 && nc.size() == 1 && 
						nc.getMetricDuration().isLess(thresholdDur)) && nvDeorn.size() > 0) {
						Rational durOrnSeq = nc.getMetricDuration();
						for (int j = i+1; j < nv.size(); j++) {
							NotationChord ncNext = nv.get(j);
							int indNext = onsetTimes.indexOf(ncNext.getMetricTime());
							// If ncNext is ornamental: increment duration of ornamental sequence
							if (ch.get(indNext).size() == 1 && ncNext.size() == 1 && 
								ncNext.getMetricDuration().isLess(thresholdDur)) {
								durOrnSeq = durOrnSeq.add(ncNext.getMetricDuration());
							}
							// If not: replace ncPrev with ncPrevDeorn 
							else {
								NotationChord ncPrev = nv.get(i-1);
								NotationChord ncPrevDeorn = new NotationChord();
								for (Note n : ncPrev) {
									ncPrevDeorn.add(ScorePiece.createNote(n.getMidiPitch(), n.getMetricTime(), 
										ncPrev.getMetricDuration().add(durOrnSeq), mtl));
								}
								nvDeorn.remove(ncPrev);
								nvDeorn.add(ncPrevDeorn);
								i = j-1;
								break;
							}
						}
					}
					// If nc is not ornamental
					else {
						nvDeorn.add(nc);
					}
				}
				nstDeorn.add(nvDeorn);
			}
			nsDeorn.add(nstDeorn);
		}
		return nsDeorn;
	}


	/**
	 * Rescales the <code>NotationSystem</code>.
	 * 
	 * @param ns
	 * @param mtl
	 * @param rescaleFactor
	 * @return
	 */
	private static NotationSystem rescaleNotationSystemOLD(NotationSystem ns, MetricalTimeLine mtl, int rescaleFactor) {
		NotationSystem nsResc = new NotationSystem();
		for (NotationStaff nst : ns) {	
			NotationStaff nstResc = new NotationStaff();
			for (NotationVoice nv : nst) {
				NotationVoice nvResc = new NotationVoice();
				for (NotationChord nc : nv) {
					NotationChord ncResc = new NotationChord();
					nc.forEach(n -> { 
						Rational dur = n.getMetricDuration();
						Rational mt = n.getMetricTime();
						ncResc.add(ScorePiece.createNote(
							n.getMidiPitch(), 
							rescaleFactor > 0 ? mt.mul(rescaleFactor) : mt.div(Math.abs(rescaleFactor)),
							rescaleFactor > 0 ? dur.mul(rescaleFactor) : dur.div(Math.abs(rescaleFactor)), 
							mtl)
						);
					});
					nvResc.add(ncResc);
				}
				nstResc.add(nvResc);
			}
			nsResc.add(nstResc);
		}
		return nsResc;
	}


	/**
	 * Gets, for the given accidentals (i.e., the number of flats if negative, and the number of
	 * sharps if positive), the index of the root char and the root alteration.
	 * 
	 * @param accidentals
	 * @return An <code>Integer[]</code> containing
	 *         <ul>
	 *         <li>As element 0: the index in "ABCDEFG" of the root that goes with the number of accidentals.</li>
	 *         <li>As element 1: the root alteration, i.e., the number of alterations (sharps/flats) to 
	 *             be added to the root. If the given number of accidentals is negative, the alterations
	 *             are flats; else, they are sharps.</li>
	 *         </ul>
	 */
	private static Integer[] getRootIndexAndRootAlteration(int accidentals) {
		Map<Integer, Integer[]> rootMap = new LinkedHashMap<Integer, Integer[]>();
		rootMap.put(0, new Integer[]{2, 0});
		// Sharps
		rootMap.put(1, new Integer[]{6, 0}); // G
		rootMap.put(2, new Integer[]{3, 0}); // D
		rootMap.put(3, new Integer[]{0, 0}); // A
		rootMap.put(4, new Integer[]{4, 0}); // E
		rootMap.put(5, new Integer[]{1, 0}); // B
		rootMap.put(6, new Integer[]{5, 0}); // F#
		rootMap.put(7, new Integer[]{2, 1}); // C#
		rootMap.put(8, new Integer[]{6, 1}); // G#
		rootMap.put(9, new Integer[]{3, 1}); // D#
		rootMap.put(10, new Integer[]{0, 1}); // A#
		rootMap.put(11, new Integer[]{4, 1}); // E#
		// Flats
		rootMap.put(-1, new Integer[]{5, 0}); // F
		rootMap.put(-2, new Integer[]{1, 1}); // Bb
		rootMap.put(-3, new Integer[]{4, 1}); // Eb
		rootMap.put(-4, new Integer[]{0, 1}); // Ab
		rootMap.put(-5, new Integer[]{3, 1}); // Db
		rootMap.put(-6, new Integer[]{6, 1}); // Gb
		rootMap.put(-7, new Integer[]{2, 1}); // Cb
		rootMap.put(-8, new Integer[]{5, 1}); // Fb
		rootMap.put(-9, new Integer[]{1, 2}); // Bbb
		rootMap.put(-10, new Integer[]{4, 2}); // Ebb
		rootMap.put(-11, new Integer[]{0, 2}); // Abb

		return rootMap.get(accidentals);
	}


	/** 
	 * Adds a Note to the Transcription in the given voice at the given metricTime.
	 * 
	 * @param note
	 * @param voiceNumber 
	 * @param metricTime
	 */
	private void addNoteOLD(Note note, int voiceNumber, Rational metricTime) {
		NotationSystem system = getScorePiece().getScore();
		NotationStaff staff = system.get(voiceNumber);
		NotationVoice voice = staff.get(0);
		NotationChord chord = new NotationChord();
		chord.add(note);
		chord.setMetricTime(metricTime);
		voice.add(chord);
	}


	/** 
	 * Removes the Note with the given midiPitch from the NotationChord in the given voice at the given metric
	 * time. If no such Note exists, a RuntimeException is thrown. 
	 * 
	 * @param midiPitch
	 * @param voiceNumber  
	 * @param metricTime 
	 */
	private void removeNoteOLD(int midiPitch, int voiceNumber, Rational metricTime) {
		NotationSystem system = getScorePiece().getScore();
		NotationStaff staff = system.get(voiceNumber);
		NotationVoice voice = staff.get(0);
		int chordNumber = voice.find(metricTime); 
		if (chordNumber < 0) {
			throw new RuntimeException("No Note found at the given metricTime.");
		}
		NotationChord chord = voice.get(chordNumber);
		if (chord.size() == 1) {
			voice.remove(chordNumber);
		}
		else {
			// NB: getContent() does not always give the contents of the NotationChord in the same sequence; 
			// this has to do with the way the Notes are added when the Transcription is made 
			// TODO fix or just use solution comparing with pitch (as below)?
			List<Note> notesInChord = chord.getContent(); 
			boolean noteFound = false;
			for (int i = 0; i < notesInChord.size(); i++) {
				Note currentNote = notesInChord.get(i);
				if (currentNote.getMidiPitch() == midiPitch) {
					chord.remove(currentNote);
					noteFound = true;
					break;
				}
			}
			if (noteFound == false) {
				throw new RuntimeException("No Note found with the given midiPitch.");
			}
		}
	}


	/**
	 * Creates a Piece from the given arguments.
	 *  
	 * @param btp
	 * @param bnp 
	 * @param voiceLabels
	 * @param durLabels
	 * @param numVoices
	 * @param mtl
	 * @param ht
	 * @param name
	 */
	// TESTED (for both tablature- and non-tablature case)
	static Piece createPiece(Integer[][] btp, Integer[][] bnp, List<List<Double>> voiceLabels, 
		List<List<Double>> durLabels, int numVoices, MetricalTimeLine mtl, SortedContainer<Marker> ht, 
		String name) {

		Transcription.verifyCase(btp, bnp);

		Piece piece = new Piece();
		NotationSystem ns = piece.createNotationSystem();
		for (int i = 0; i < numVoices; i++) {
			NotationStaff nst = new NotationStaff(ns); 
			nst.add(new NotationVoice(nst));
			ns.add(nst);
		}

		if (btp != null) {
			for (int i = 0; i < btp.length; i++) {
				// Create Note
//				int pitch = btp[i][Tablature.PITCH];
				Rational mt = new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
				// When not modelling duration, durLabels == null
				Rational mDur = 
					durLabels == null ? new Rational(btp[i][Tablature.MIN_DURATION], Tablature.SRV_DEN) :
						LabelTools.convertIntoDuration(durLabels.get(i))[0]; // TODO [0] is possible because each element in durations currently contains only one Rational
				Note note = ScorePiece.createNote(btp[i][Tablature.PITCH], mt, mDur, -1, null);
				// Add Note to voice(s)
				LabelTools.convertIntoListOfVoices(voiceLabels.get(i)).forEach(v -> 
					piece.getScore().get(v).get(0).add(note));
//				List<Integer> currVoices = DataConverter.convertIntoListOfVoices(voiceLabels.get(i));
//				for (int v : currVoices) {
//					piece.getScore().get(v).get(0).add(note);
//				}	
			}
		}
		else {
			for (int i = 0; i < bnp.length; i++) {
				// Create Note 
//				int pitch = bnp[i][PITCH];
				Rational mt = new Rational(
					bnp[i][Transcription.ONSET_TIME_NUMER], 
					bnp[i][Transcription.ONSET_TIME_DENOM]);
				Rational mDur = new Rational(
					bnp[i][Transcription.DUR_NUMER], 
					bnp[i][Transcription.DUR_DENOM]);
				Note note = ScorePiece.createNote(bnp[i][Transcription.PITCH], mt, mDur, -1, null);
				// Add Note voice(s)
				LabelTools.convertIntoListOfVoices(voiceLabels.get(i)).forEach(v ->
					piece.getScore().get(v).get(0).add(note));
//				List<Integer> currentVoices = DataConverter.convertIntoListOfVoices(voiceLabels.get(i));
//				for (int v : currentVoices) {
//					NotationVoice voice = piece.getScore().get(v).get(0);
//					voice.add(note);
//				}
			}
		}
		piece.setMetricalTimeLine(mtl);
		piece.setHarmonyTrack(ht);
		piece.setName(name);

		return piece;
	}


//	/**
//	 * Returns a reversed version of the Transcription.
//	 * 
//	 * @param t
//	 * @param tab
//	 * @return
//	 */
//	// NOT TESTED (wrapper method)
//	public static Transcription reverse(Transcription t, Tablature tab) {
//		Piece pRev = reversePiece(t/*, tab*/);
//
//		Encoding eRev = null;
//		if (tab != null) {
//			eRev = tab.getEncoding().reverse(tab.getTimeline().getMeterInfo()); // NB The value of normaliseTuning is irrelevant
//		}
//		return new Transcription(pRev, eRev);
//	}


//	/**
//	 * Reverses the <code>Transcription</code>'s <code>Piece</code>. 
//	 * NB: Handles the _unadapted_ <code>Piece</code>. 
//	 * 
//	 * @param t
//	 * @return
//	 */
//	// NOT TESTED (wrapper method)
//	static Piece reversePiece(Transcription t/*, Tablature tab*/) { // TODO name reverse()
//		Piece pRev = new Piece();
//
//		MetricalTimeLine mtl = t.getScorePiece().getMetricalTimeLine();
//		Rational mp = t.getMirrorPoint();
////		List<Rational[]> onsetsAndMinDurs = null;
////		List<Rational> onsets = null;
////		List<Rational> minDurs = null;
////		if (tab != null) {
////			onsets = ToolBox.getItemsAtIndex(tab.getMetricTimePerChord(false), 0);
////			minDurs = tab.getMinimumDurationPerChord();
////			onsetsAndMinDurs = tab.getAllOnsetTimesAndMinDurations();
////		}
//
//		Piece pOrig = t.getOriginalPiece();
//		// Make a copy of notationSystem so that pOrig is not affected
//		NotationSystem nsCopy = copyNotationSystem(pOrig.getScore());
//
////		// For each voice
////		NotationSystem nsRev = new NotationSystem();
////		for (NotationStaff nst : nsCopy) {	
////			NotationStaff nstRev = new NotationStaff();
////			for (NotationVoice nv : nst) {
////				NotationVoice nvRev = new NotationVoice();
////				for (NotationChord nc : nv) {
////					NotationChord ncRev = new NotationChord();
////					for (Note n : nc) {
////						// Calculate the Note's new onset time (mirrorPoint - offset time)
////						Rational dur = n.getMetricDuration();
//////						// In tablature case: use minimum duration
//////						if (tab != null) {
//////							for (Rational[] item : onsetsAndMinDurs) {
//////								if (item[0].equals(n.getMetricTime())) {
//////									dur = item[1];
//////									dur.reduce();
//////									break;
//////								}
//////							}
//////							
//////							// NB: onsets and minDurs are corresponding lists and can be 
//////							//     indexed concurrently
//////							for (int i = 0; i < onsets.size(); i++) {
//////								if (onsets.get(i).equals(n.getMetricTime())) {
//////									dur = minDurs.get(i);
//////									dur.reduce();
//////									break;
//////								}
//////							}
//////						}
////						Rational offset = n.getMetricTime().add(dur);
////						Rational mt = mirrorPoint.sub(offset);
////						
////						// Error in barbetta-1582_1-il_nest.tbp: last chord should be co1 
////						// (and not co2), leading to newOnsetTime being -1/2 
////						if (pOrig.getName().equals("barbetta-1582_1-il_nest.mid") && 
////							mt.equals(new Rational(-1, 2))) {
////							mt = Rational.ZERO;
////							dur = new Rational(1, 2);
////						}
////
////						ncRev.add(createNote(n.getMidiPitch(), mt, dur, mtl));
////					}
////					nc = ncRev;
////					nvRev.add(ncRev);
////				}
////				nstRev.add(nvRev);
////			}
////			nsRev.add(nstRev);
////		}
//
//		pRev.setScore(reverseNotationSystem(nsCopy, mtl, mp, pOrig.getName()));
//		pRev.setMetricalTimeLine(reverseMetricalTimeLine(mtl, mp, t.getMeterInfo()));
//		pRev.setHarmonyTrack(reverseHarmonyTrack(pOrig.getHarmonyTrack(), mtl, mp, t.getKeyInfo()));
//		pRev.setName(t.getScorePiece().getName());
//		return pRev;
//	}
	
	
//	/**
//	 * Returns a deornamented version of the Transcription.
//	 * 
//	 * @param t
//	 * @param tab
//	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
//	 *            considered ornamental.
//	 * @return
//	 */
//	// NOT TESTED (wrapper method)
//	public static Transcription deornament(Transcription t, Tablature tab, Rational dur) {
//		Piece pDeorn = deornamentPiece(t, /*tab,*/ dur);
//		Encoding eDeorn = null;
//		if (tab != null) {
//			eDeorn = tab.getEncoding().deornament(Tablature.getTabSymbolDur(dur)); // NB The value of normaliseTuning is irrelevant
//		}
//		return new Transcription(pDeorn, eDeorn);
//	}


//	/**
//	 * Removes all sequences of single-note events shorter than the given duration from the
//	 * encoding, and lengthens the duration of the event preceding the sequence by the total 
//	 * length of the removed sequence. NB: Handles the _unadapted_ Piece.
//	 *
//	 * @param t
//	 * @param tab
//	 * @param dur
//	 * @return
//	 */
//	// TESTED
//	static Piece deornamentPiece(Transcription t, /*Tablature tab,*/ Rational dur) {
//		Piece pDeorn = new Piece();
//
//		MetricalTimeLine mtl = t.getScorePiece().getMetricalTimeLine();
//		List<Rational> onsetTimes = t.getAllOnsetTimes();
//		List<List<Note>> ch = t.getChords();
////		List<List<TabSymbol>> tabChords = null;
//////		List<Rational[]> onsetsAndMinDurs = null;
////		List<Rational> minDurs = null;
////		if (tab != null) {
////			tabChords = tab.getChords(); 
//////			onsetsAndMinDurs = tab.getAllOnsetTimesAndMinDurations();
////			minDurs = tab.getMinimumDurationPerChord();
////		}
//
//		Piece origP = t.getOriginalPiece();
//		// Make a copy of notationSystem so that pOrig is not affected
//		NotationSystem nsCopy = copyNotationSystem(origP.getScore());
//		
//		// For each voice
//		NotationSystem nsDeorn = new NotationSystem();
//		List<Integer> removed = new ArrayList<>();
////		int voice = 0;
//		for (NotationStaff nst : nsCopy) {
////			System.out.println("voice = " + voice);
//			NotationStaff nstDeorn = new NotationStaff();
//			for (NotationVoice nv : nst) {
//				NotationVoice nvDeorn = new NotationVoice();
//				NotationChord pre = null;
//				Rational durPre = null;
//				for (int i = 0; i < nv.size(); i++) {
//					NotationChord nc = nv.get(i);
//					Rational onset = nc.getMetricTime();
//					int ind = onsetTimes.indexOf(onset);
//
////					boolean isOrn; 
////					if (tabChords != null) { 
////						isOrn = tabChords.get(ind).size() == 1 && 
////							minDurs.get(ind).isLess(dur);
//////							onsetsAndMinDurs.get(ind)[1].isLess(dur);
////					}
////					else {
////					isOrn = nc.size() == 1 && ch.get(ind).size() == 1 &&
////						nc.getMetricDuration().isLess(dur);
////					}
//					boolean isOrn = 
//						nc.size() == 1 && ch.get(ind).size() == 1 && 
//						nc.getMetricDuration().isLess(dur);
//					// If currNc is ornamental
//					// NB: In case of a single-event SNU, the note will be removed from both voices
//					if (isOrn) {
//						removed.add(ind);
//						// Determine pre, if it has not yet been determined
//						if (pre == null) {
//							NotationChord ncPrev = nv.get(i-1);
//							pre = ncPrev;
//							durPre = ncPrev.getMetricDuration(); // all notes in a NotationChord have the same duration 
//						}
//						// Increment durPre
//						durPre = durPre.add(nc.getMetricDuration());
//					}
//					// If currNc is the first after a sequence of one or more ornamental notes
//					// (i.e., it does not meet the if condition above but pre != null)
//					else if (pre != null) {
//						// Adapt duration of pre
//						for (Note n : pre) {
////							n.setScoreNote(new ScoreNote(new ScorePitch(n.getMidiPitch()), 
////								n.getMetricTime(), durPre));
//							n = createNote(n.getMidiPitch(), n.getMetricTime(), durPre, mtl);
//						}
//						// Add currNc
//						nvDeorn.add(nc);
//						// Reset
//						pre = null;
//					}
//					else {
//						nvDeorn.add(nc);
//					}
//				}
//				nstDeorn.add(nvDeorn);
//			}
////			voice++;
//			nsDeorn.add(nstDeorn);
//		}
//
//		pDeorn.setScore(nsDeorn);
//		pDeorn.setMetricalTimeLine(mtl);
//		pDeorn.setHarmonyTrack(origP.getHarmonyTrack());		
//		pDeorn.setName(t.getScorePiece().getName());
//		return pDeorn;
//	}


//	/**
//	 * Sets equalDurationUnisonsInfo, a list the size of the number of notes in the Transcription, containing
//	 *   a. if the note at that index is not a unison note or if the note at that index is part of a unison
//	 *      whose notes are of inequal length: <code>null</code>  
//	 *   b. if the note at that index is part of a unison whose notes are of equal length: a voice label (i.e.,
//	 *      a List<Double>) containing two 1.0s, thus representing both correct voices.
//	 * 
//	 * NB: Non-tablature case only.
//	 */
//	void setEqualDurationUnisonsInfoBLA() {
//		List<List<Double>> equalDurationUnisons = new ArrayList<List<Double>>();
//		NoteSequence noteSeq = getNoteSequence(); 
//		List<List<Double>> voiceLabels = getVoiceLabels();
//		// Initialise equalDurationUnisons with all elements set to null
//		for (int i = 0; i < noteSeq.size(); i++) {
//			equalDurationUnisons.add(null);
//		}
//			
//		// For all chords
//		List<List<Note>> transcriptionChords = getTranscriptionChords();  
//		for (int i = 0; i < transcriptionChords.size(); i++) {
//			// If the chord contains a unison
//			Integer[][] currentUnisonInfo = getUnisonInfo(i);
//			if (currentUnisonInfo != null) {
//				// For each unison
//				for (int j = 0; j < currentUnisonInfo.length; j++) {
//				  // 1. Determine the indices in noteSeq and voiceLabels of the lower and upper unison notes
//		      // a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
//		   		int notesPreceding = 0;
//		      for (int k = 0; k < i; k++) {
//		      	notesPreceding += transcriptionChords.get(k).size();
//		      }
//		      // b. Calculate the indices in the NoteSequence
//		      int indexOfLowerUnisonNote = notesPreceding + currentUnisonInfo[j][1];
//		      int indexOfUpperUnisonNote = notesPreceding + currentUnisonInfo[j][2];
//		      		      
//		      // 2. If the unison notes have the same duration
//		      Rational durationLower = noteSeq.getNoteAt(indexOfLowerUnisonNote).getMetricDuration();
//		      Rational durationUpper = noteSeq.getNoteAt(indexOfUpperUnisonNote).getMetricDuration();
//		      List<Double> voiceLabelLower = voiceLabels.get(indexOfLowerUnisonNote);
//		      List<Double> voiceLabelUpper = voiceLabels.get(indexOfUpperUnisonNote);
//		      if (durationLower.equals(durationUpper)) {
//		        // Combine the voice labels
//			      int indexOfOneInUpper = voiceLabelUpper.indexOf(1.0);
//			      List<Double> correctVoices = new ArrayList<Double>(voiceLabelLower);
//			      correctVoices.set(indexOfOneInUpper, 1.0);
//		      	equalDurationUnisons.set(indexOfLowerUnisonNote, correctVoices);
//		      	equalDurationUnisons.set(indexOfUpperUnisonNote, correctVoices);
//		      }
//				}
//			}
//		}	
////		return equalDurationUnisons;
//		equalDurationUnisonsInfo = equalDurationUnisons;
//	}


//	/**
//	 * 
//	 * @return
//	 */
//	// TESTED
//	List<TaggedNote> makeNotes(Tablature tab) {
//		// Unhandled notes
//		List<Note> argNotes = makeUnhandledNotes();
//		List<Integer[]> argVoicesSNU = null;
//		List<Integer[]> argVoicesUnison = null;
//
//		// In the tablature case: handle SNUs and course crossings
//		if (tab != null) {
//			// a. SNUs
//			List<TaggedNote> SNUs = handleSNUs(argNotes, tab);
//			argNotes = (List<Note>) SNUs.get(0);
//			argVoicesSNU = (List<Integer[]>) SNUs.get(1);
//			// b. Course crossings
//			argNotes = handleCourseCrossings(argNotes, tab);
//		}
//		// In the non-tablature case: handle unisons
//		else {
//			List<Object> unisons = handleUnisons(argNotes);
//			argNotes = (List<Note>) unisons.get(0);
//			argVoicesUnison = (List<Integer[]>) unisons.get(1);
//		}	
//		return Arrays.asList(new Object[]{argNotes, argVoicesSNU, argVoicesUnison});
//	}


//	/**
//	 * Uniformises the NoteSequence, i.e., orders any equal-pitch notes that have the same 
//	 * onset time by voice (lower first).  
//	 */
//	// TESTED
//	NoteSequence uniformiseInitialNoteSequence() {
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Note>> chordsFromNoteSeq = getChordsFromNoteSequence();
//		for (List<Note> chord: chordsFromNoteSeq) {
//			// If the chord contains equal-pitch notes: swap incorrectly ordered ones. 
//			// Example for chord with pitches [10 20 10 10] and voices [1 0 2 3] (should be [3 0 2 1])
//			// i = 0, j = 1: OK
//			//        j = 2: swap to [2 0 1 3]; restart at i = 0
//			//        j = 1, 2: OK
//			//        j = 3: swap to [3 0 1 2]; restart at i = 0
//			//        j = 1, 2, 3: OK
//			// i = i, j = 2, 3: OK
//			// i = 2, j = 3: swap to: [3 0 2 1]; restart at i = 2 
//			// i = 2, j = 3: OK
//			if (chord.size() > getPitchesInChord(chord).stream().distinct().collect(Collectors.toList()).size()) {
//				for (int i = 0; i < chord.size() - 1; i++) {
//					Note n = chord.get(i);
//					int v = findVoice(n);
//					for (int j = i+1; j < chord.size(); j++) {
//						Note nextN = chord.get(j);
//						if (n.getMidiPitch() == nextN.getMidiPitch() && v < findVoice(nextN)) {
//							Collections.swap(chord, i, j);
//							noteSeq.swapNotes(noteSeq.indexOf(n), noteSeq.indexOf(nextN));
//							i = i-1; // start again at first note of chord 
//							break;
//						}
//					}				
//				}
//			}
//		}
//		return noteSeq;
//	}


//	/** 
//	 * Initialises the voice labels. 
//	 * 
//	 * If <code>null</code> is given as argument, the voice labels are initialised from the initial, 
//	 * unadapted NoteSequence; else, they are initialised with the given labels.
//	 * 
//	 * Each voice label is a binary vector containing MAXIMUM_NUMBER_OF_VOICES elements, where the 
//	 * position of the 1.0 indicates the voice encoded (index 0 denotes the highest voice).
//	 * 
//	 * NB: Must be called after initialiseNoteSequence().
//	 * 
//	 * @param vl The voice labels to initialise with.
//	 */
//	// TESTED (for both tablature- and non-tablature case simultaneously)
//	void initialiseVoiceLabelsOLD(List<List<Double>> vl) {
//		if (vl == null) {
//			List<List<Double>> voiceLabels = new ArrayList<List<Double>>(); 
//
//			NoteSequence noteSeq = getNoteSequence();
//			for (int i = 0; i < noteSeq.size(); i++) {
//				Note n = noteSeq.getNoteAt(i);
//				
//				// 1. Create a voice label for the Note
//				List<Double> currVoiceLabel = new ArrayList<Double>();
//				
//				// 2. Extract the voice the Note is in and fill currentVoiceLabel
//				int voice = findVoice(n);
//				for (int j = 0; j < MAXIMUM_NUMBER_OF_VOICES; j++) {
//					if (voice == j) {
//						currVoiceLabel.add(j, 1.0);
//					}
//					else {
//						currVoiceLabel.add(j, 0.0);
//					}
//				}
//
//				// 3. Add currentVoiceLabel to initialVoiceLabels 
//				voiceLabels.add(currVoiceLabel);
//			}
//			setVoiceLabels(voiceLabels);
//		}
//		else {
//			setVoiceLabels(vl);
//		}
//	}

//	/**
//	 * Initialises the NoteSequence, in which all notes from the Piece are ordered hierarchically by<br>
//	 * <ul>
//	 * <li>(1) Onset time (lower first).</li>
//	 * <li>(2) If two notes have the same onset time: pitch (lower first).</li>
//	 * <li>(3) If two notes have the same onset time and the same pitch: voice (lower first).</li>
//	 * </ul>
//	 * 
//	 * After initialisation, any SNU notes (tablature case), course crossing notes (tablature case), 
//	 * and unison notes (non-tablature case) need further handling. 
//	 * <ul>
//	 * <li>SNU notes must be merged; this is done in <code>handleSNUs()</code>.</li> 
//	 * <li>Course crossing notes must be swapped so that the course crossing note that has the 
//	 *     higher pitch (the lower-course course crossing note) comes first; this is done 
//	 *     in <code>handleCourseCrossings()</code>.</li>
//	 * <li>Unison notes must be swapped so that the unison note that has the longer duration 
//	 *     comes first; this is done in <code>handleUnisons()</code>.</li>
//	 * </ul>
//	 * 
//	 * Any unison notes (tablature case) need no further handling, as the lower-course unison
//	 * note automatically always comes first.
//	 *               
//	 * NB: Must be called before initialiseVoiceLabels().
//	 */
//	// TESTED (for both tablature- and non-tablature case simultaneously)
//	private void initialiseNoteSequenceOLD() {
//		// 1. Populate a NoteSequence that orders the Notes chord per chord, from low to high
//		// (ensured by the comparator)
//		NoteSequence initialNoteSeq = new NoteSequence(new NoteTimePitchComparator());
//		NotationSystem ns = getPiece().getScore();
////		Collection<Containable> contents = ns.getContentsRecursiveList(null);
//		ns.getContentsRecursiveList(null).stream().filter(c -> c instanceof Note).forEach(c -> initialNoteSeq.add((Note) c));		
////		for (Containable c : contents) {
////			if (c instanceof Note) { 
////				initialNoteSeq.add((Note) c);
////			}
////		}
//
//		// 2. Check for all equal-pitch pairs whether the Notes are added to the NoteSequence in the correct order.
//		// This is necessary because the NoteTimePitchComparator does not handle unisons and SNUs consistently in 
//		// that sometimes the note in the lower voice is added first, and sometimes the note in the upper voice
//		if (initialNoteSeq.size() != 0) {
//			// For each note but the last:
//			for (int currNoteInd = 0; currNoteInd < (initialNoteSeq.size() - 1); currNoteInd++) {
//				// 1. Get the Note's pitch, onsetTime, and the voice it belongs to 
//				Note currNote = initialNoteSeq.getNoteAt(currNoteInd);
//				int currNotePitch = currNote.getMidiPitch();
//				Rational currNoteOnsetTime = currNote.getMetricTime();
//				int currNoteVoice = findVoice(currNote);
//
//				// 2. Check the remainder of initialNoteSeq for another Note with the same onsetTime and pitch. Break 
//				// from inner for-loop when the onsetTime of nextNote becomes greater than that of currNote
//				for (int nextNoteInd = currNoteInd + 1; nextNoteInd < initialNoteSeq.size(); nextNoteInd++) {
//					Note nextNote = initialNoteSeq.getNoteAt(nextNoteInd);
//					// Same onsetTime? Check whether pitch is also the same  
//					if (nextNote.getMetricTime().equals(currNoteOnsetTime)) {
//						// Same pitch? nextNote is the complement sought; swap if necessary and break
//						// NB: since an event may contain more than one equal-pitch-pair, the ENTIRE process must be repeated
//						// from the start until the sequence is correct. (E.g., an event with three equal pitches that are
//						// added to the NoteSequence in voice order 1-2-3 becomes 2-1-3 after one iteration of both for-loops,
//						// then 3-1-2, and finally 3-2-1.) Thus, when notes are swapped: break from inner for-loop and start
//						// again in outer for-loop
//						if (nextNote.getMidiPitch() == currNotePitch) {
//							int nextNoteVoice = findVoice(nextNote);
//							// If currentNote is in the higher voice (has the lower voice number): swap 
//							if (currNoteVoice < nextNoteVoice) {								
//								initialNoteSeq.swapNotes(currNoteInd, nextNoteInd);
//								currNoteInd = -1;
//								break;
//							}    		    
//						}
//					}
//					// Is onsetTime of nextNote greater than that of currNote? Break and continue with the next currNote
//					else {
//						break; 
//					}
//				}
//			}
//		}
//		// 3. Set noteSequence 
//		setNoteSequence(initialNoteSeq);
//	}


//	/**
//	 * Finds the voice the given Note in the given NotationSystem belongs to.
//	 *  
//	 * @param note 
//	 * @param ns
//	 * @return
//	 */
//	private int findVoiceOLD(Note note, NotationSystem ns) {
//		int voice = -1;
//		// NB: A NotationSystem has as many Staffs as the Transcription it belongs to has voices; each Staff thus 
//		// represents a voice. The Staffs are numbered from top (no. 0) to bottom (no. 4, depending on 
//		// MAXIMUM_NUMBER_OF_VOICES).
//		// For each Staff in the NotationSystem: 
//		outerLoop: for (int i = 0; i < ns.size(); i++) {
//			NotationStaff staff = ns.get(i);
//			// a. Get the contents of the Staff
//			Containable[] contentsOfStaff = staff.getContentsRecursive();
//			// b. Look at each Containable in the contents. If a Containable matches the (unique) note: return i, the
//			// number of the Staff that note is on (and thus the number of the voice it belongs to), and break from the
//			// outer loop
//			for (int j = 0; j < contentsOfStaff.length; j++) {
//				if (contentsOfStaff[j] == note) {
//					voice = i;
//					break outerLoop;
//				}
//			}
//		}
//		return voice;
//	}

//	/** 
//	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in
//	 * the Tablature the corresponding Notes in the Transcription, and
//	 * (1) removes the CoDnote with the shorter duration from noteSequence
//	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets 
//	 *     the label of the lower CoDnote to the result in voiceLabels, and removes the label 
//	 *     of the upper from voiceLabels
//	 * (3) combines the duration labels of both CoDnotes into a List<Double> with two 1.0s, sets
//	 *     the label of the lower CoDnote to the result in durationLabels, and removes the label
//	 *     of the upper from durationLabels 
//	 *
//	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the 
//	 * Transcription, containing for each element:
//	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
//	 *   b. if the the note at that index is a CoDnote: an Integer[] containing 
//	 *        as element 0: the voice the longer CoDnote is in;
//	 *        as element 1: the voice the shorter CoDnote is in.
//	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in
//	 *      the lower voice that comes first in the NoteSequence) is placed at element 0.
//	 * 
//	 * If isGroundTruthTranscription is <code>false</code>, i.e., when the method is applied to
//	 * a predicted Transcription, only noteSequence is adapted. This is because the predicted 
//	 * voiceLabels and durationLabels are already ready-to-use (only the voicesCoDNotes still 
//	 * need to be created from them).
//	 * 
//	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor
//	 *      a course crossings.
//	 * NB2: Tablature case only; must be called before handleCourseCrossings().
//	 * 
//	 * @param tablature
//	 * @param isGroundTruthTranscription
//	 */
//	// TESTED
//	void handleCoDNotesOUD(Tablature tablature, boolean isGroundTruthTranscription) {
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//
//		List<List<Double>> voiceLab = new ArrayList<List<Double>>(); // getVoiceLabels();
//		List<List<Double>> durationLab = new ArrayList<List<Double>>(); // getDurationLabels();
//		List<Integer[]> voicesCoD = new ArrayList<Integer[]>();
//		if (isGroundTruthTranscription) {
//			voiceLab = getVoiceLabels();
//			durationLab = getDurationLabels();
//			// Initialise voicesCoD with all elements set to null
//			for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
//				voicesCoD.add(null);
//			}
//			// Set voicesCoD (in case the pieces contains no SNUs and the setting does not 
//			// happen in the for-loop below
//			setVoicesSNU(voicesCoD);
//		}
//
//		// For every chord
//		for (int i = 0; i < tablatureChords.size(); i++) {
//			// If the chord contains a CoD
//			if (getSNUInfo(tablatureChords, i) != null) {
//				Integer[][] coDInfo = getSNUInfo(tablatureChords, i);
//				// Get the (most recent! needed for calculating notesPreceding) transcription chords
//				List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
//				// For each CoD in the chord 
//				int notesAlreadyRemovedFromChord = 0;
//				for (int j = 0; j < coDInfo.length; j++) {
//					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
//					// a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
//					int notesPreceding = 0;
//					for (int k = 0; k < i; k++) {
//						notesPreceding += transcriptionChords.get(k).size();
//					}
//					// b. Calculate the indices
//					int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
//					int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);
//
//					// 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
//					// a. noteSeq: remove the CoDnote with the shorter duration
//					Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
//					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
//					// Assume that the lower note has the longer duration. If this is so or if both notes have the
//					// same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
//					// indexOfUpperCoDNote 
//					int indexOfLongerCoDNote = indexOfLowerCoDNote;
//					int indexOfShorterCoDNote = indexOfUpperCoDNote;
//					if (durationLower.isLess(durationUpper)) {
//						indexOfShorterCoDNote = indexOfLowerCoDNote;
//						indexOfLongerCoDNote = indexOfUpperCoDNote;
//					}
//					noteSeq.deleteNoteAt(indexOfShorterCoDNote);
//					if (isGroundTruthTranscription) { 
//						// The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must  
//						// be determined before voiceLab is adapted   
//						List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
//						int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
//						List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
//						int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
//						// b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to 
//						// the result, and remove the label of the upper CoDnote from voiceLab
//						List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
//						List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
//						List<Double> combinedVoiceLabel = combineLabels(voiceLabelOfLowerCoDNote, voiceLabelOfUpperCoDNote);
//						voiceLab.set(indexOfLowerCoDNote, combinedVoiceLabel);
//						voiceLab.remove(indexOfUpperCoDNote);
//						// c. durationLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
//						// result, and remove the label of the upper CoDnote from durationLab
//						List<Double> durationLabelOfLowerCoDNote = durationLab.get(indexOfLowerCoDNote);
//						List<Double> durationLabelOfUpperCoDNote = durationLab.get(indexOfUpperCoDNote);
//						List<Double> combinedDurationLabel = 
//							combineLabels(durationLabelOfLowerCoDNote, durationLabelOfUpperCoDNote);
//						durationLab.set(indexOfLowerCoDNote, combinedDurationLabel);
//						durationLab.remove(indexOfUpperCoDNote);
//						// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
//						// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
//						voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});
//					}
//					// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
//					// iteration through the inner for-loop is necessary
//					notesAlreadyRemovedFromChord++;
//
//					// 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
//					setNoteSequence(noteSeq);
//					if (isGroundTruthTranscription) {
//						setVoiceLabels(voiceLab);
//						setDurationLabels(durationLab);
//						setVoicesSNU(voicesCoD);
//					}
//
//					// 5. Concat information to adaptations
//					adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote	
//						- notesPreceding) +	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
//						"list of voice labels and list of durations adapted accordingly." + "\n");
//				}
//			}
//		}
//	}


//	/**
//	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in the Tablature the
//	 * corresponding Notes in the Transcription, and
//	 * (1) removes the CoDnote with the shorter duration from noteSequence
//	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the lower
//	 *     CoDnote to the result in voiceLabels, and removes the label of the upper from voiceLabels
//	 * (3) combines the duration labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the 
//	 *     lower CoDnote to the result in durationLabels, and removes the label of the upper from durationLabels 
//	 *
//	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the Transcription, 
//	 * containing for each element:
//	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
//	 *   b. If the the note at that index is a CoDnote: an Integer[] containing 
//	 *      as element 0: the voice the longer CoDnote is in;
//	 *      as element 1: the voice the shorter CoDnote is in.
//	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in the lower voice
//	 *      that comes first in the NoteSequence) is placed at element 0.
//	 * 
//	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor a course crossings.
//	 * NB2: Tablature case only; must be called before handleCourseCrossings().
//	 * 
//	 * @param tablature
//	 */
//	// TESTED
//	private void handleCoDNotesOUDSTE(Tablature tablature) {				
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLab = getVoiceLabels();
//		List<List<Double>> durationLab = getDurationLabels();
//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//		List<Integer[]> voicesCoD = new ArrayList<Integer[]>();  
//		// Initialise voicesCoD with all elements set to null
//		for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
//			voicesCoD.add(null);
//		}
//
//		// For every chord
//		for (int i = 0; i < tablatureChords.size(); i++) {
//			// If the chord contains a CoD
//			if (getSNUInfo(tablatureChords, i) != null) {
//				Integer[][] coDInfo = getSNUInfo(tablatureChords, i);
//				// Get the (most recent! needed for calculating notesPreceding) transcription chords
//				List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
//				// For each CoD in the chord 
//				int notesAlreadyRemovedFromChord = 0;
//				for (int j = 0; j < coDInfo.length; j++) {
//					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
//					// a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
//					int notesPreceding = 0;
//					for (int k = 0; k < i; k++) {
//						notesPreceding += transcriptionChords.get(k).size();
//					}
//					// b. Calculate the indices
//					int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
//					int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);
//
//					// 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
//					// a. noteSeq: remove the CoDnote with the shorter duration
//					Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
//					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
//					// Assume that the lower note has the longer duration. If this is so or if both notes have the
//					// same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
//					// indexOfUpperCoDNote 
//					int indexOfLongerCoDNote = indexOfLowerCoDNote;
//					int indexOfShorterCoDNote = indexOfUpperCoDNote;
//					if (durationLower.isLess(durationUpper)) {
//						indexOfShorterCoDNote = indexOfLowerCoDNote;
//						indexOfLongerCoDNote = indexOfUpperCoDNote;
//					}
//					noteSeq.deleteNoteAt(indexOfShorterCoDNote);
//					// The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must be 
//					// determined before voiceLab is adapted   
//					List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
//					int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
//					List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
//					int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
//					// b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
//					// result, and remove the label of the upper CoDnote from voiceLab
//					List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
//					List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
//					List<Double> combinedVoiceLabel = combineLabels(voiceLabelOfLowerCoDNote, voiceLabelOfUpperCoDNote);
//					voiceLab.set(indexOfLowerCoDNote, combinedVoiceLabel);
//					voiceLab.remove(indexOfUpperCoDNote);
//					// c. durationLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
//					// result, and remove the label of the upper CoDnote from durationLab
//					List<Double> durationLabelOfLowerCoDNote = durationLab.get(indexOfLowerCoDNote);
//					List<Double> durationLabelOfUpperCoDNote = durationLab.get(indexOfUpperCoDNote);
//					List<Double> combinedDurationLabel = combineLabels(durationLabelOfLowerCoDNote, durationLabelOfUpperCoDNote);
//					durationLab.set(indexOfLowerCoDNote, combinedDurationLabel);
//					durationLab.remove(indexOfUpperCoDNote);
//					// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
//					// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
//					voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});
//
//					// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
//					// iteration through the inner for-loop is necessary
//					notesAlreadyRemovedFromChord++;
//
//					// 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
//					setNoteSequence(noteSeq);
//					setVoiceLabels(voiceLab);
//					setDurationLabels(durationLab);
//					setVoicesSNU(voicesCoD);
//
//					// 5. Concat information to adaptations
//					adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote
//						- notesPreceding) +	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
//						"list of voice labels and list of durations adapted accordingly." + "\n");
//				}
//			} 
//		}
//	}


//	/**
//	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in the Tablature the
//	 * corresponding Notes in the Transcription, and
//	 * (1) removes the CoDnote with the shorter duration from noteSequence
//	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the 
//	 *     lower CoDnote to the result in voiceLabels, and removes the label of the upper from voiceLabels
//	 * (3) combines the duration labels of both CoDnotes into a List<List<Double>>, the first element of which 
//	 *     represents the duration of the longer note, sets the label of the lower CoDnote to the result in 
//	 *     durationLabels, and removes the label of the upper from durationLabels 
//	 *          
//	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the Transcription, 
//	 * containing for each element:
//	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
//	 *   b. If the the note at that index is a CoDnote: an Integer[] containing 
//	 *      as element 0: the voice the longer CoDnote is in;
//	 *      as element 1: the voice the shorter CoDnote is in.
//	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in the lower voice
//	 *      that comes first in the NoteSequence) is placed at element 0.
//	 * 
//	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor a course crossings.
//	 * NB2: Tablature case only; must be called before handleCourseCrossings().
//	 * 
//	 * @param tablature
//	 */
//	private void handleCoDNotesOLD(Tablature tablature) {				
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLab = getVoiceLabels();
//		List<List<List<Double>>> durationLab = getDurationLabelsOLD();
//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//		List<Integer[]> voicesCoD = new ArrayList<Integer[]>();  
//		// Initialise voicesCoD with all elements set to null
//		for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
//			voicesCoD.add(null);
//		}
//
//		// For every chord
//		for (int i = 0; i < tablatureChords.size(); i++) {
//			// If the chord contains a CoD
//			if (getSNUInfo(tablatureChords, i) != null) {
//				Integer[][] coDInfo = getSNUInfo(tablatureChords, i);
//				// Get the (most recent! needed for calculating notesPreceding) transcription chords
//				List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
//				// For each CoD in the chord 
//				int notesAlreadyRemovedFromChord = 0;
//				for (int j = 0; j < coDInfo.length; j++) {
//					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
//					// a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
//					int notesPreceding = 0;
//					for (int k = 0; k < i; k++) {
//						notesPreceding += transcriptionChords.get(k).size();
//					}
//					// b. Calculate the indices
//					int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
//					int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);
//
//					// 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
//					// a. noteSeq: remove the CoDnote with the shorter duration
//					Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
//					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
//					// Assume that the lower note has the longer duration. If this is so or if both notes have the
//					// same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
//					// indexOfUpperCoDNote 
//					int indexOfLongerCoDNote = indexOfLowerCoDNote;
//					int indexOfShorterCoDNote = indexOfUpperCoDNote;
//					if (durationLower.isLess(durationUpper)) {
//						indexOfShorterCoDNote = indexOfLowerCoDNote;
//						indexOfLongerCoDNote = indexOfUpperCoDNote;
//					}
//					noteSeq.deleteNoteAt(indexOfShorterCoDNote);
//					// The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must be 
//					// determined before voiceLab is adapted   
//					List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
//					int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
//					List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
//					int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
//					// b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
//					// result, and remove the label of the upper CoDnote from voiceLab
//					List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
//					List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
//					int voiceNumberToAdd = voiceLabelOfUpperCoDNote.indexOf(1.0);
//					voiceLabelOfLowerCoDNote.set(voiceNumberToAdd, 1.0);
//					voiceLab.set(indexOfLowerCoDNote, voiceLabelOfLowerCoDNote);
//					voiceLab.remove(indexOfUpperCoDNote);
//					// c. durationLab: add the label of the shorter CoDnote to that of the longer, set the label at 
//					// indexOfLowerCoDNote to the result, and remove the label of the upper CoDnote from durationLab
//					List<List<Double>> durationLabelOfLongerCoDNote = durationLab.get(indexOfLongerCoDNote);
//					List<List<Double>> durationLabelOfShorterCoDNote = durationLab.get(indexOfShorterCoDNote);
//					durationLabelOfLongerCoDNote.add(durationLabelOfShorterCoDNote.get(0)); 
//					durationLab.set(indexOfLowerCoDNote, durationLabelOfLongerCoDNote);
//					durationLab.remove(indexOfUpperCoDNote);
//					// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
//					// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
//					voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});
//
//					// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
//					// iteration through the inner for-loop is necessary
//					notesAlreadyRemovedFromChord++;
//
//					// 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
//					setNoteSequence(noteSeq);
//					setVoiceLabels(voiceLab);
//					setDurationLabelsOLD(durationLab);
//					setVoicesSNU(voicesCoD);
//
//					// 5. Concat information to adaptations
//					adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote	
//						- notesPreceding)	+	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
//						"list of voice labels and list of durations adapted accordingly." + "\n");
//				}
//			} 
//		}
//	}


//	/**
//	 * Finds for all course-crossing notes (i.e., notes pairs where the note on the lower course has the higher
//	 * pitch) in the Tablature the corresponding Notes in the Transcription, and 
//	 * (1) swaps these Notes in noteSequence;
//	 * (2) swaps the corresponding voice labels in voiceLabels;
//	 * (3) swaps the corresponding duration labels in durationLabels. 
//	 * 
//	 * If isGroundTruthTranscription is <code>false</code>, i.e., when the method is applied to a predicted 
//	 * Transcription, only noteSequence is adapted. This is because the predicted voiceLabels and durationLabels 
//	 * are already ready-to-use (only the voicesCoDNotes still need to be created from them).
//	 * 
//	 * NB1: This method presumes that a chord contains only one course crossing, and neither a CoD nor a unison.
//	 * NB2: Tablature case only; must be called after handleCoDNotes().
//	 * 
//	 * @param tablature
//	 * @param isGroundTruthTranscription
//	 */
//	// TESTED
//	void handleCourseCrossingsOLD(Tablature tablature, boolean isGroundTruthTranscription) {
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLab = new ArrayList<List<Double>>();
//		List<List<Double>> durationLab = new ArrayList<List<Double>>();
//		if (isGroundTruthTranscription) {
//			voiceLab = getVoiceLabels();
//			durationLab = getDurationLabels();
//		}
//		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
//
//		// For every chord
//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//		for (int i = 0; i < tablatureChords.size(); i++) {
//			// If the chord contains a course crossing
//			if (tablature.getCourseCrossingInfo(i) != null) {
//				List<Integer[]> chordCrossingInfo = tablature.getCourseCrossingInfo(i);
//				// For each course crossing in the chord
//				for (int j = 0; j < chordCrossingInfo.size(); j++) {
//					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CCnotes
//					// a. Calculate the number of Notes preceding the CC chord by summing the size of all previous chords
//					int notesPreceding = 0;
//					for (int k = 0; k < i; k++) {
//						notesPreceding += transcriptionChords.get(k).size();
//					}
//					// b. Calculate the indices
//					int indexOfLowerCCNote = notesPreceding + chordCrossingInfo.get(j)[2];
//					int indexOfUpperCCNote = notesPreceding + chordCrossingInfo.get(j)[3];
//
//					// 2. Swap
//					noteSeq.swapNotes(indexOfLowerCCNote, indexOfUpperCCNote);
//					if (isGroundTruthTranscription) {
//						Collections.swap(voiceLab, indexOfLowerCCNote, indexOfUpperCCNote);
//						Collections.swap(durationLab, indexOfLowerCCNote, indexOfUpperCCNote);
//					}
//
//					// 3. Concat information to adaptations
//					adaptations = adaptations.concat("  Course crossing found in chord " + i + ": notes no. " + 
//						chordCrossingInfo.get(j)[2] + " (pitch " + chordCrossingInfo.get(j)[0]	+ ") and " + chordCrossingInfo.get(j)[3] +
//						" (pitch " + chordCrossingInfo.get(j)[1] + ") in that chord swapped in the NoteSequence; "+ "list of " + 
//						"voice labels and list of durations adapted accordingly." + "\n");
//				}
//			}
//		}			
//		// Reset noteSequence, voiceLabels, and durationLabels
//		setNoteSequence(noteSeq);
//		if (isGroundTruthTranscription) {
//			setVoiceLabels(voiceLab);
//			setDurationLabels(durationLab);
//		}
//	}


//	/**
//	 * Finds for all course-crossing notes (i.e., notes pairs where the note on the lower course has the higher
//	 * pitch) in the Tablature the corresponding Notes in the Transcription, and 
//	 * (1) swaps these Notes in noteSequence;
//	 * (2) swaps the corresponding voice labels in voiceLabels;
//	 * (3) swaps the corresponding duration labels in durationLabels. 
//	 * 
//	 * NB1: This method presumes that a chord contains only one course crossing, and neither a CoD nor a unison.
//	 * NB2: Tablature case only; must be called after handleCoDNotes().
//	 * 
//	 * @param tablature
//	 */
//	// TESTED
//	private void handleCourseCrossingsOLD(Tablature tablature) {
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLab = getVoiceLabels();
//		List<List<List<Double>>> durationLab = getDurationLabelsOLD();
//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
//		
//		// For every chord
//		for (int i = 0; i < tablatureChords.size(); i++) {
//			// If the chord contains a course crossing
//			if (tablature.getCourseCrossingInfo(i) != null) {
//				List<Integer[]> chordCrossingInfo = tablature.getCourseCrossingInfo(i);
//				// For each course crossing in the chord
//				for (int j = 0; j < chordCrossingInfo.size(); j++) {
//					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CCnotes
//					// a. Calculate the number of Notes preceding the CC chord by summing the size of all previous chords
//					int notesPreceding = 0;
//					for (int k = 0; k < i; k++) {
//			 			notesPreceding += transcriptionChords.get(k).size();
//			 		}
//					// b. Calculate the indices
//					int indexOfLowerCCNote = notesPreceding + chordCrossingInfo.get(j)[2];
//					int indexOfUpperCCNote = notesPreceding + chordCrossingInfo.get(j)[3];
//
//					// 2. Swap
//					noteSeq.swapNotes(indexOfLowerCCNote, indexOfUpperCCNote);
//					Collections.swap(voiceLab, indexOfLowerCCNote, indexOfUpperCCNote);
//					Collections.swap(durationLab, indexOfLowerCCNote, indexOfUpperCCNote);
//
//					// 3. Concat information to adaptations
//					adaptations = adaptations.concat("  Course crossing found in chord " + i + ": notes no. " + 
//						chordCrossingInfo.get(j)[2] + " (pitch " + chordCrossingInfo.get(j)[0]	+ ") and " + chordCrossingInfo.get(j)[3] +
//						" (pitch " + chordCrossingInfo.get(j)[1]	+ ") in that chord swapped in the NoteSequence; "+ "list of " + 
//						"voice labels and list of durations adapted accordingly." + "\n");
//				}
//			}
//		}			
//		// Reset noteSequence, voiceLabels, and durationLabels
//		setNoteSequence(noteSeq);
//		setVoiceLabels(voiceLab);
//		setDurationLabelsOLD(durationLab);
//	}


//	/**
//	 * <ul>
//	 * <li>If the unison notes are of inequal duration
//	 *     <ul>
//	 *     <li>If they are not set in the correct order (i.e., with the unison note with the longer duration first): 
//	 *         swaps the unison notes in the NoteSequence.</li>
//	 *     <li>If they are not set in the correct order (i.e., with the unison note with the longer duration first): 
//	 *         swaps the voice labels of the unison notes. NB: Not if t == Type.PREDICTED (in which case the labels 
//	 *         already have their final form).</li>    
//	 *     <li>Determines the primary and secondary voice for the IDU.</li>
//	 *     </ul> 
//	 * </li>
//	 * <li>If the unison notes are of equal duration</li>
//	 *     <ul>
//	 *     <li>Determines the primary and secondary voice for the EDU.</li>
//	 *     </ul>
//	 * </li>
//	 * </ul>
//	 * 
//	 * When all Transcription notes are traversed, resets NoteSequence and voice labels, and sets 
//	 * the primary and secondary EDU and IDU voices.
//	 *  
//	 * NB: Non-tablature case only.
//	 * 
//	 * @param t
//	 */
//	// TESTED
//	void handleUnisonsOLD(Type t) {
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLab = getVoiceLabels();
////		List<Integer[]> equalDurUnisonsInfo = new ArrayList<Integer[]>();
////		if (t != Type.PREDICTED) {
////			// Initialise equalDurUnisonsInfo with all elements set to null
////			for (int i = 0; i < noteSeq.size(); i++) {
////				equalDurUnisonsInfo.add(null);
////			}
////		}
//		List<Integer[]> voicesEDU = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));
//		List<Integer[]> voicesIDU = new ArrayList<Integer[]>(Collections.nCopies(noteSeq.size(), null));
//
//		boolean adaptLabels = t != Type.PREDICTED;
//
//		// 1. Adapt NoteSequence, voice labels; set voicesEDU, voicesIDU 
//		int notesPreceding = 0;
//		List<Integer> notesPerChord = getChordSizesFromNoteSeq(noteSeq);
////		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
////		List<Integer> sizes = new ArrayList<>();
////		for (List<Note> l : transcriptionChords) {
////			sizes.add(l.size());
////		}
////		System.out.println(notesPerChord.equals(sizes));
//		
//		for (int i = 0; i < notesPerChord.size(); i++) {
////		for (int i = 0; i < transcriptionChords.size(); i++) {
//			// If the chord contains a unison
//			if (getUnisonInfo(i) != null) {
//				Integer[][] unisonInfo = getUnisonInfo(i);
//				// For each unison in the chord (there should be only one)
//				for (int j = 0; j < unisonInfo.length; j++) {
////					// 1. Determine indices of the lower and upper unison note
////					// a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
////					notesPreceding = 0;
////					for (int k = 0; k < i; k++) {
////						notesPreceding += transcriptionChords.get(k).size();
////					}
////					// b. Calculate the indices in the noteSeq
//					
//					// 1. Determine indices
//					// a. Indices of the lower and upper unison note   
//					int indLower = notesPreceding + unisonInfo[j][1];
//					int indUpper = notesPreceding + unisonInfo[j][2];
//
/////					// 2. Determine the duration of the lower and upper unison note
//					
//					// b. Indices of the longer and shorter unison note
//					Rational durLower = noteSeq.getNoteAt(indLower).getMetricDuration();
//					Rational durUpper = noteSeq.getNoteAt(indUpper).getMetricDuration();
//					int indLonger = 
//						durLower.isGreater(durUpper) ? indLower : (durLower.isLess(durUpper) ? indUpper : -1);
//					int indShorter = 
//						durLower.isGreater(durUpper) ? indUpper : (durLower.isLess(durUpper) ? indLower : -1);
//
//					// 2. Set IDU and EDU
//					int first, second;
//					if (!durLower.isEqual(durUpper)) {
//						int indFirst = indLonger;
//						int indSecond = indShorter;
//						first = DataConverter.convertIntoListOfVoices(voiceLab.get(indFirst)).get(0);
//						second = DataConverter.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
//						voicesIDU.set(indLower, new Integer[]{first, second, indUpper});
//						voicesIDU.set(indUpper, new Integer[]{first, second, indLower});
//					}
//					else {
//						int indFirst = indLower;
//						int indSecond = indUpper;
//						first = DataConverter.convertIntoListOfVoices(voiceLab.get(indFirst)).get(0);
//						second = DataConverter.convertIntoListOfVoices(voiceLab.get(indSecond)).get(0);
//						voicesEDU.set(indLower, new Integer[]{first, second, indUpper});
//						voicesEDU.set(indUpper, new Integer[]{first, second, indLower});
//					}
//					
//					// 3. Adapt NoteSequence (if necessary)
//					if (durLower.isLess(durUpper)) {
//						noteSeq.swapNotes(indLower, indUpper);
//					}
//					
//					// 4. Adapt voice labels (if necessary)
//					if (durLower.isLess(durUpper)) {
//						if (adaptLabels) {
//							Collections.swap(voiceLab, indLower, indUpper);
//						}
//					}
//					
//					boolean oud = false;
//					if (oud) {
//						// a. IDU
//						if (!durLower.isEqual(durUpper)) {
//							// Swap the lower and upper unison notes; swap lower and upper unison notes' labels
//							if (durLower.isLess(durUpper)) {
//								noteSeq.swapNotes(indLower, indUpper);
//								if (adaptLabels) {
//									Collections.swap(voiceLab, indLower, indUpper);
//								}
//							}
//							int voiceLower = voiceLab.get(indLower).indexOf(1.0);
//							int voiceUpper = voiceLab.get(indUpper).indexOf(1.0);
//							voicesIDU.set(indLower, new Integer[]{voiceLower, voiceUpper, indUpper});
//							voicesIDU.set(indUpper, new Integer[]{voiceLower, voiceUpper, indLower});
//						}
//						// b. EDU
//						else {
//							int voiceLower = voiceLab.get(indLower).indexOf(1.0);
//							int voiceUpper = voiceLab.get(indUpper).indexOf(1.0);
//							voicesEDU.set(indLower, new Integer[]{voiceLower, voiceUpper, indUpper});
//							voicesEDU.set(indUpper, new Integer[]{voiceLower, voiceUpper, indLower});
//						}
//					}
//
////					if (durationLower.isLess(durationUpper)) {
////						noteSeq.swapNotes(indLower, indUpper);
////						if (adaptLabels) {
////							Collections.swap(voiceLab, indLower, indUpper);
////						}
////						int voiceLower = voiceLab.get(indLower).indexOf(1.0);
////						int voiceUpper = voiceLab.get(indUpper).indexOf(1.0);
////						voicesNEDU.set(indLower, new Integer[]{voiceLower, voiceUpper, indUpper});
////						voicesNEDU.set(indUpper, new Integer[]{voiceLower, voiceUpper, indLower});
////						
////						// Concat information to adaptations
////						adaptations = adaptations.concat("  Unison found in chord " + i + ": notes no. " + unisonInfo[j][1] +
////							" (pitch " + unisonInfo[j][0] + ", duration " + durationLower + ") and " + unisonInfo[j][2] 
////							+ " (pitch " + unisonInfo[j][0] + ", duration " + durationUpper +
////								") in that chord swapped in the NoteSequence; list of voice labels adapted accordingly.");
////					}
////					// 4. Set voicesEDU
////					if (durationLower.equals(durationUpper)) {
//////						List<Double> voiceLabelLower = voiceLab.get(indexLower);
//////						List<Double> voiceLabelUpper = voiceLab.get(indUpper);
////						int voiceLower = voiceLab.get(indLower).indexOf(1.0);
////						int voiceUpper = voiceLab.get(indUpper).indexOf(1.0);
////						voicesEDU.set(indLower, new Integer[]{voiceLower, voiceUpper, indUpper});
////						voicesEDU.set(indUpper, new Integer[]{voiceLower, voiceUpper, indLower});
////					}					  
//				}
//			}
//			notesPreceding += notesPerChord.get(i);
////			notesPreceding += transcriptionChords.get(i).size();
//		}		
//		// Reset noteSequence, voice labels; set voicesEDU
//		setNoteSequence(noteSeq);
//		if (adaptLabels) {
//			setVoiceLabels(voiceLab);
////			setEqualDurationUnisonsInfo(equalDurUnisonsInfo);
//		}
//		setVoicesEDU(voicesEDU);
//		setVoicesIDU(voicesIDU);
//	}


//	/**
//	 * Checks for each note in the Transcription whether it is part of a unison of two notes with equal duration.
//	 * Returns a list the size of the number of notes in the Transcription, containing
//	 *   a. if the note at that index is not a unison note or if the note at that index is part of a unison
//	 *      whose notes are of inequal length: <code>null</code>  
//	 *   b. if the note at that index is part of a unison whose notes are of equal length: a voice label (i.e.,
//	 *      a List<Double>) containing two 1.0s, thus representing both correct voices.
//	 * 
//	 * NB: Non-tablature case only.
//	 *  
//	 * @return
//	 */
//	// TESTED
//	private List<List<Double>> getEqualDurationUnisonsInfoOLD() {
//		List<List<Double>> equalDurationUnisons = new ArrayList<List<Double>>();
//		NoteSequence noteSeq = getNoteSequence();
//		List<List<Double>> voiceLabels = getVoiceLabels();
//		// Initialise equalDurationUnisons with all elements set to null
//		for (int i = 0; i < noteSeq.size(); i++) {
//			equalDurationUnisons.add(null);
//		}
//			
//		// For all chords
//		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();  
//		for (int i = 0; i < transcriptionChords.size(); i++) {
//			// If the chord contains a unison
//			Integer[][] currentUnisonInfo = getUnisonInfo(i);
//			if (currentUnisonInfo != null) {
//				// For each unison
//				for (int j = 0; j < currentUnisonInfo.length; j++) {
//				  // 1. Determine the indices in noteSeq and voiceLabels of the lower and upper unison notes
//		      // a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
//		   		int notesPreceding = 0;
//		      for (int k = 0; k < i; k++) {
//		      	notesPreceding += transcriptionChords.get(k).size();
//		      }
//		      // b. Calculate the indices in the NoteSequence
//		      int indexOfLowerUnisonNote = notesPreceding + currentUnisonInfo[j][1];
//		      int indexOfUpperUnisonNote = notesPreceding + currentUnisonInfo[j][2];
//		      		      
//		      // 2. If the unison notes have the same duration
//		      Rational durationLower = noteSeq.getNoteAt(indexOfLowerUnisonNote).getMetricDuration();
//		      Rational durationUpper = noteSeq.getNoteAt(indexOfUpperUnisonNote).getMetricDuration();
//		      List<Double> voiceLabelLower = voiceLabels.get(indexOfLowerUnisonNote);
//		      List<Double> voiceLabelUpper = voiceLabels.get(indexOfUpperUnisonNote);
//		      if (durationLower.equals(durationUpper)) {
//		        // Combine the voice labels
//			      int indexOfOneInUpper = voiceLabelUpper.indexOf(1.0);
//			      List<Double> correctVoices = new ArrayList<Double>(voiceLabelLower);
//			      correctVoices.set(indexOfOneInUpper, 1.0);
//		      	equalDurationUnisons.set(indexOfLowerUnisonNote, correctVoices);
//		      	equalDurationUnisons.set(indexOfUpperUnisonNote, correctVoices);
//		      }
//				}
//			}
//		}	
//		return equalDurationUnisons;
//	}
	
	
//	public static MetricalTimeLine reverseMetricalTimeLineOLD(MetricalTimeLine mtl, Rational mp, List<Integer[]> mi) {
//	// This method is called on an existing Transcription, so meterInfo exists and mtl
//	// is clean. So arguments can be inside method and method non-static
//	
//	// mt: mp     - mt of timeSig + (bars in timeSig * timeSig) 
//	// t : mpTime - t  of (mt of timeSig + (bars in timeSig * timeSig))
//	// long time = mtl.getTime(m.getMetricTime());
//
////	List<Integer[]> mi = getMeterInfo();
////	MetricalTimeLine mtl = getPiece().getMetricalTimeLine();
////	Rational mp = getMirrorPoint();
//	long mpTime = mtl.getTime(mp);
//	System.out.println("---> " + mp);
//	System.out.println(mpTime);
//	
//	for (Marker m : mtl) {
//		System.out.println(m);
//	}
//	System.out.println("----");
//
//	for (Marker m : mtl) {
////		System.out.println(m);
//		Rational mt = m.getMetricTime();
//		int indInMi = -1;
//		for (int i = 0; i < mi.size(); i++) {
//			Rational currMt = 
//				new Rational(mi.get(i)[Timeline.MI_NUM_MT_FIRST_BAR], 
//				mi.get(i)[Timeline.MI_DEN_MT_FIRST_BAR]);
//			if (currMt.equals(mt)) {
//				indInMi = i;
//				break;
//			}
//		}
//		// Adapt mt
//		if (m instanceof TimeSignatureMarker) {
//			System.out.println("time sig");
//			Integer[] currMi = mi.get(indInMi);
//			int currBars = (currMi[Timeline.MI_LAST_BAR] - currMi[Timeline.MI_FIRST_BAR]) + 1;
//			Rational currMeter = new Rational(currMi[Timeline.MI_NUM], currMi[Timeline.MI_DEN]);
//			Rational mtRev = mp.sub(mt.add(currMeter.mul(currBars)));
//			((TimeSignatureMarker) m).setMetricTime(mtRev);
//			System.out.println("--> " + mtl.getTime(mt.add(currMeter.mul(currBars))));
//			System.out.println("--> " + (mpTime - mtl.getTime(mt.add(currMeter.mul(currBars)))));
//		}
//		// Adapt mt and t
//		if (m instanceof TimedMetrical) {
//			// Has mt and time
//			if (!(m instanceof TempoMarker)) {
////				System.out.println("timed metrical");
//			}
//			else {
////				System.out.println("tempomarker");
//				// t : mpTime - t  of (mt of timeSig + (bars in timeSig * timeSig))
//				
//			}
//		}
//	}
//	
//	
////	System.out.println(numTimeSigs == mi.size());
//	return null;
//}

}
