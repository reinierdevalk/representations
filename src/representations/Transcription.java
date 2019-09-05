package representations;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

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
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.NoteSequence;
import de.uos.fmt.musitech.data.structure.container.SortedContainer;
import de.uos.fmt.musitech.data.structure.harmony.KeyMarker;
import de.uos.fmt.musitech.data.time.Marker;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignature;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.score.ScoreEditor;
import de.uos.fmt.musitech.score.ScoreEditor.Mode;
import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import exports.MIDIExport;
import imports.MIDIImport;
import tbp.TabSymbol;
import tools.ToolBox;
import utility.DataConverter;
import utility.NoteTimePitchComparator;

public class Transcription implements Serializable {
	
//	private static final long serialVersionUID = -8586909984652950201L;
	public static int MAXIMUM_NUMBER_OF_VOICES = 5;
	public static final int DURATION_LABEL_SIZE = Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()*1; // 3 for JosquIntab
//	public static final int DURATION_LABEL_SIZE = Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom();
	public static final int INCORRECT_IND = 0;
	public static final int ORNAMENTATION_IND = 1;
	public static final int REPETITION_IND = 2;
	public static final int FICTA_IND = 3;
	public static final int OTHER_IND = 4;
	
	private List<File> files;
	private Piece piece;
	private Piece unadaptedGTPiece;
	private String pieceName;
	private NoteSequence noteSequence;
	private List<List<Double>> voiceLabels;
	private List<List<List<Double>>> chordVoiceLabels;
	private List<List<Double>> durationLabels;
	private List<List<List<Double>>> durationLabelsOLD;
	private List<Integer[]> voicesCoDNotes = null;
	private List<Integer[]> equalDurationUnisonsInfo;
	private Integer[][] basicNoteProperties;
	List<List<Note>> transcriptionChordsFinal;
	List<Integer> numberOfNewNotesPerChord;
	private List<Integer[]> meterInfo;
	private List<Integer[]> keyInfo;
	private List<List<Integer>> colourIndices;

	public static final int PITCH = 0;
	public static final int ONSET_TIME_NUMER = 1;
	public static final int ONSET_TIME_DENOM = 2;
	public static final int DUR_NUMER = 3;
	public static final int DUR_DENOM = 4;
	public static final int CHORD_SEQ_NUM = 5;
	public static final int CHORD_SIZE_AS_NUM_ONSETS = 6;
	public static final int NOTE_SEQ_NUM = 7;

	private String adaptations = "Adaptations:" + "\n";
	private String chordsSpecification = "Chord error details:" + "\n";
	private String alignmentDetails = "Alignment details:" + "\n"; 
	private static boolean reversePiece = false;

	public static final List<Double> THIRTYSECOND = createDurationLabel(1);
	public static final List<Double> SIXTEENTH = createDurationLabel(2);
	public static final List<Double> DOTTED_SIXTEENTH = createDurationLabel(3);
	public static final List<Double> EIGHTH = createDurationLabel(4);
	public static final List<Double> EIGHTH_AND_THIRTYSECOND = createDurationLabel(5);
	public static final List<Double> DOTTED_EIGHTH = createDurationLabel(6);
	public static final List<Double> DOUBLE_DOTTED_EIGHTH = createDurationLabel(7);
	public static final List<Double> QUARTER = createDurationLabel(8);
	public static final List<Double> QUARTER_AND_THIRTYSECOND = createDurationLabel(9);
	public static final List<Double> QUARTER_AND_SIXTEENTH = createDurationLabel(10);
	public static final List<Double> QUARTER_AND_DOTTED_SIXTEENTH = createDurationLabel(11);
	public static final List<Double> DOTTED_QUARTER = createDurationLabel(12);
	public static final List<Double> DOTTED_QUARTER_AND_THIRTYSECOND = createDurationLabel(13);
	public static final List<Double> DOUBLE_DOTTED_QUARTER = createDurationLabel(14);
	public static final List<Double> TRIPLE_DOTTED_QUARTER = createDurationLabel(15);
	public static final List<Double> HALF = createDurationLabel(16);
	public static final List<Double> DOTTED_HALF = createDurationLabel(24);
	public static final List<Double> WHOLE = createDurationLabel(32);

	private static final List<Double> VOICE_EMPTY = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0});
	private static final List<Double> VOICE_EMPTY_SIX = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
	public static final List<Double> VOICE_0 = Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 0.0});
	public static final List<Double> VOICE_1 = Arrays.asList(new Double[]{0.0, 1.0, 0.0, 0.0, 0.0});
	public static final List<Double> VOICE_2 = Arrays.asList(new Double[]{0.0, 0.0, 1.0, 0.0, 0.0});
	public static final List<Double> VOICE_3 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 1.0, 0.0});
	public static final List<Double> VOICE_4 = Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 1.0});

	private final static int SUPERIUS = 0;
	private final static int ALTUS = 1;
	private final static int TENOR = 2;
	private final static int BASSUS = 3;

//  /**
//   * Constructor. Creates a new Transcription out of the encoding in the given File. Sets the class fields
//   * file, piece, noteSequence and voiceLabels, and 
//   *   In the tablature case: sets the class field durationLabels, aligns the Tablature and Transcription,
//   *     and checks the encoding for alignment errors. If any are found, a runTimeException is thrown;
//   *   In the non-tablature case: sets the class fields meterInfo and basicNoteProperties. In this case,
//   *     argEncodingFile is <code>null</code>.
//   *                              
//   * @param argMidiFile
//   * @param argPiece
//   * @param argEncodingFile
//   */
//  public Transcription(File argMidiFile, Piece argPiece, File argEncodingFile) {
//  	
//    // Verify that either argMidiFile or argPiece == null
// 		if ((argMidiFile != null && argPiece != null) ||
// 		  (argMidiFile == null && argPiece == null)) {
// 		  System.out.println("ERROR: if argMidiFile == null, argPiece must not be, and vice versa" + "\n");
// 		   throw new RuntimeException("ERROR (see console for details)");
// 		}
//  	
//  	if (argMidiFile != null) {
//  	  setFile(argMidiFile);
//  	  setPiece(); // needs file
//  	}
//  	else {
//  		setPiece(argPiece);
//  	}
//  	
//  	if (reversePiece) {
//  		setMeterInfo(); // needs file
//  	  reversePiece(); // needs piece and meterInfo
//  	}
//    initialiseNoteSequence(); // needs piece
//    initialiseVoiceLabels(); // needs piece and noteSequence
//    // a. In the tablature case
//    if (argEncodingFile != null) {
//    	initialiseDurationLabels(); // needs noteSequence
//    	// 1. Check chords
//    	Tablature tablature = new Tablature(argEncodingFile);
//    	if (checkChords(tablature) == false) { // needs noteSequence
//    		System.out.println(chordsSpecification);
//      	throw new RuntimeException("ERROR: Chord error (see console).");
//      }
//      // 2. Align tablature and transcription
//      handleCoDNotes(tablature); // needs noteSequence, voiceLabels, and durationLabels
//      handleCourseCrossings(tablature); // needs noteSequence, voiceLabels, and durationLabels
//      // 3. Do final alignment check
//      if (checkAlignment(tablature) == false) {
//      	System.out.println(alignmentDetails);
//    		throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
//      }
//    }
//    // b. In the non-tablature case
//    else {
//    	setMeterInfo(); // needs file
//    	handleUnisons(); // needs noteSequence and voiceLabels (WAS: and basicNoteProperties??)
//    	setBasicNoteProperties(); // needs noteSequence
//    }
//  }

	public static void setMaxNumVoices(int num) {
		MAXIMUM_NUMBER_OF_VOICES = num;
	}


	// a. Constructors and methods that have to do with the completion of a fully operational Transcription 
	public Transcription() {
	}


//	private void prepareTranscription(Piece groundTruthPiece, Encoding encoding) {
//		setPiece(groundTruthPiece);
//
//		// Create the Transcription based on the ground truth Piece
//		boolean normaliseTuning = false;
//		boolean isGroundTruthTranscription = true;
////		createTranscription(argMidiFile.getName(), argEncodingFile, normaliseTuning, isGroundTruthTranscription);
//		createTranscription(groundTruthPiece.getName(), encoding, normaliseTuning, isGroundTruthTranscription);
//	}


	/**
	 * Constructor for the ground truth Transcription.
	 *                              
	 * @param argMidiFile
	 * @param argEncodingFile
	 */
	public Transcription(File argMidiFile, File argEncodingFile) {
		setFiles(Arrays.asList(new File[]{argMidiFile, argEncodingFile}));

		Piece groundTruthPiece = MIDIImport.importMidiFile(argMidiFile);
		Encoding encoding = null;
		if (argEncodingFile != null) {
			encoding = new Encoding(argEncodingFile);
		}
//		long[][] rah = groundTruthPiece.getMetricalTimeLine().getTimeSignature();
//		for (long[] l : rah) {
//			System.out.println(Arrays.toString(l));
//		}
//		KeyMarker r = groundTruthPiece.getMetricalTimeLine().getKeyMarker(new Rational(0, 4));
//		System.out.println(r);
//		System.exit(0);

		// Create the Transcription based on the ground truth Piece
		boolean normaliseTuning = false;
		boolean isGroundTruthTranscription = true;
		createTranscription(groundTruthPiece, encoding, normaliseTuning, isGroundTruthTranscription);	
	}


	/**
	 * Constructor for a derivation of the ground truth Transcription (used for data augmentation).
	 *                              
	 * @param groundTruthPiece
	 * @param encoding
	 */
	public Transcription(Piece groundTruthPiece, Encoding encoding) {
		// Create the Transcription based on the ground truth Piece
		boolean normaliseTuning = false;
		boolean isGroundTruthTranscription = true;
		createTranscription(groundTruthPiece, encoding, normaliseTuning, isGroundTruthTranscription);
	}


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


	/**
	 * Constructor for a predicted Transcription.
	 * 
	 * @param argMidiFile
	 * @param argEncodingFile
	 * @param argPiece
	 * @param argVoiceLabels
	 * @param argDurationLabels
	 */
	public Transcription(String name, File argEncodingFile, Integer[][] argBtp, 
		Integer[][] argBnp, int argHiNumVoices, List<List<Double>> argVoiceLabels, 
		List<List<Double>> argDurationLabels, MetricalTimeLine mtl, SortedContainer<Marker> ks) {
		
		Encoding encoding = null;
		if (argEncodingFile != null) {
			new Encoding(argEncodingFile);
		}
		// Create and set the predicted Piece
		Piece predictedPiece = 
			createPiece(argBtp, argBnp, argVoiceLabels, argDurationLabels, argHiNumVoices, mtl, ks);
/////		setPiece(predictedPiece);
		predictedPiece.setName(name);

		// Create the Transcription based on the predicted Piece
		boolean normaliseTuning = true; // is only used in the tablature case
		boolean isGroundTruthTranscription = false;
		createTranscription(predictedPiece, encoding, normaliseTuning,
			isGroundTruthTranscription);
		
		// Set the predicted class fields. When creating a ground truth Transcription, this happens inside
		// handleCoDNotes() and handleCourseCrossings(), but when creating a predicted Transcription this step
		// is skipped in those methods because the voice labels and duration labels are already ready-to-use. In 
		// the tablature case, only voicesCoDNotes must still be created from them
		setVoiceLabels(argVoiceLabels);
		// a. In the tablature case
		if (argEncodingFile != null) {
			// Set durationLabels
			// NB: The durationLabels created in createTranscription are overwritten by argDurationLabels. Thus, 
			// when not modelling duration (when argDurationLabels == null), they are reset to null
			setDurationLabels(argDurationLabels);
			// Create voicesCoDNotes
			// NB: currently, only one duration is always predicted; both CoDnotes thus have the same duration. In
			// this case, the lower CoDnote (i.e., the one in the lower voice that comes first in the NoteSequence) 
			// is placed at element 0 (see Javadoc handleCoDNotes()) TODO 
			List<Integer[]> voicesCoDNotes = new ArrayList<Integer[]>();
			// For each predicted voiceLabel
			for (int i = 0; i < argVoiceLabels.size(); i++) {
				List<Double> currLabel = argVoiceLabels.get(i);
				List<Integer> voices = 
					DataConverter.convertIntoListOfVoices(currLabel);
				// If a CoD is predicted
				if (voices.size() > 1) {
					Integer[] currVoicesCoDNotes = new Integer[2];
					// Voices will contain two elements: the highest predicted voice will be the first element, and
					// the lowest predicted voice the second
					currVoicesCoDNotes[0] = voices.get(1);
					currVoicesCoDNotes[1] = voices.get(0);
					voicesCoDNotes.add(currVoicesCoDNotes);
				}
				// If no CoD is predicted
				else {
					voicesCoDNotes.add(null);
				}
			}	
			setVoicesCoDNotes(voicesCoDNotes);
		}
		// b. In the non-tablature case
//		else {
//			setEqualDurationUnisonsInfo(argEqualDurationUnisonsInfo); // TODO 1 mei
//		}  	  	
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
	 * @param encodingFile
	 * @param normaliseTuning    
	 * @param isGroundTruthTranscription                        
	 */
	private void createTranscription(Piece p, Encoding encoding, boolean normaliseTuning, 
		boolean isGroundTruthTranscription) {
				
		// TODO make copy rather than store and retrieve
		String fPath = "C:/Users/Reinier/Desktop/copy.mid";
		fPath = MEIExport.rootDir + "copy.mid";
		MIDIExport.exportMidiFile(p, Arrays.asList(new Integer[]{MIDIExport.DEFAULT_INSTR}),
			fPath);
		Piece pUn = MIDIImport.importMidiFile(new File(fPath));
		new File(fPath).delete();

		setPiece(p);
		setUnadaptedGTPiece(pUn);
		String pName = p.getName();
		
//		setPieceName(fName.substring(0, fName.indexOf(".mid")));
		setPieceName(pName.contains(".mid") ? pName.substring(0, pName.indexOf(".mid")) : pName);		
				
		initialiseNoteSequence(); // needs piece
		initialiseVoiceLabels(); // needs piece and noteSequence
		Tablature tab = null;
		// a. In the tablature case
		if (encoding != null) {
			initialiseDurationLabels(); // needs noteSequence
			// 1. Check chords 
			// NB: normaliseTuning is false when creating a ground truth Transcription and true 
			// when creating a predicted Transcription (see Javadoc for this method)
			tab = new Tablature(encoding, normaliseTuning);
			if (checkChords(tab) == false) { // needs noteSequence
				System.out.println(chordsSpecification);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleCoDNotes(tab, isGroundTruthTranscription); // needs noteSequence, voiceLabels, and durationLabels
			handleCourseCrossings(tab, isGroundTruthTranscription); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tab) == false) {
				System.out.println(alignmentDetails);
				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose (only if ground truth Transcription; see Javadoc for this method)
			if (isGroundTruthTranscription) {
				transpose(tab.getTranspositionInterval());
			}
			setMeterInfo(tab.getMeterInfo());
			setKeyInfo(/*tab.getMeterInfo()*/); // must be done after possible transpose()
			setTranscriptionChordsFinal(); // sets the final version of the transcription chords
		}
		// b. In the non-tablature case
		else {
			setMeterInfo();
			setKeyInfo();
//			setKeyInfo(null);
			handleUnisons(isGroundTruthTranscription); // needs noteSequence and voiceLabels
			setBasicNoteProperties(); // needs noteSequence	
			// Added 3-9-2015
			setTranscriptionChordsFinal(); // sets the final version of the transcription chords
			setNumberOfNewNotesPerChord(); // needs transcriptionChords
		}
		// c. In both
		if (isGroundTruthTranscription) {
			setChordVoiceLabels(tab); // needs transcriptionChords
		}
		else {
			// TODO Currently no chordVoiceLabels needed in bidir model
		}
	}


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

		setPieceName(argMidiFile.getName()); 
//		setFile(argMidiFile);
		setPiece(argPiece);

		initialiseNoteSequence(); // needs piece    
		setVoiceLabels(argVoiceLabels);
		// a. In the tablature case
		if (argEncodingFile != null) {
			setDurationLabels(argDurationLabels);
			// 1. Check chords. The argument normaliseTuning must be set to true (because argPiece
			// has already been normalised) for the final alignment check below
			Tablature tablature = new Tablature(argEncodingFile, true);
			if (checkChords(tablature) == false) { // needs noteSequence
				System.out.println(chordsSpecification);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleCoDNotes(tablature, false); // needs noteSequence, voiceLabels, and durationLabels 
			setVoicesCoDNotes(argVoicesCoDNotes);
			handleCourseCrossings(tablature, false); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tablature) == false) {
				System.out.println(alignmentDetails);
					throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose
//			transpose(tablature.getTranspositionInterval());
		}
		// b. In the non-tablature case
		else {
//			setMeterInfo(argMidiFile); // needs file
			handleUnisons(false); // needs noteSequence and voiceLabels
			setEqualDurationUnisonsInfo(argEqualDurationUnisonsInfo);
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
		setPieceName("");
		setPiece(argPiece);
		initialiseNoteSequence(); // needs piece    
		setVoiceLabels(argVoiceLabels);
		// a. In the tablature case
		if (tablature != null) {
			setDurationLabels(argDurationLabels);
			// 1. Check chords
			if (checkChords(tablature) == false) { // needs noteSequence
				System.out.println(chordsSpecification);
				throw new RuntimeException("ERROR: Chord error (see console).");
			}
			// 2. Align tablature and transcription
			handleCoDNotes(tablature, false); // needs noteSequence, voiceLabels, and durationLabels 
			setVoicesCoDNotes(argVoicesCoDNotes);
			handleCourseCrossings(tablature, false); // needs noteSequence, voiceLabels, and durationLabels
			// 3. Do final alignment check
			if (checkAlignment(tablature) == false) {
				System.out.println(alignmentDetails);
				throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
			}
			// 4. Transpose
			transpose(tablature.getTranspositionInterval());
		}
		// b. In the non-tablature case
		else {
			setMeterInfo(argMeterInfo);
			handleUnisons(false); // needs noteSequence and voiceLabels
			setEqualDurationUnisonsInfo(argEqualDurationUnisonsInfo);
			setBasicNoteProperties(); // needs noteSequence
		}   
	}


	public Transcription(Piece argPiece, List<List<Double>> voiceLabels, List<List<Double>> durationLabels) {
		setPiece(argPiece);
		initialiseNoteSequence();    
		setVoiceLabels(voiceLabels);
		setDurationLabels(durationLabels);
//		handleCoDNotes(tablature, false);
//		setVoicesCoDNotes(voicesCoDNotes);
//		handleCourseCrossings(tablature, false);
	}


//  /**
//   * Turns the given Tablature into a Transcription, using the given voices and durations.
//   *  
//   * @param tablature
//   * @param voices
//   * @param durations
//   * @param numberOfVoices
//   */
//  //
//  public Transcription(Tablature tablature, List<List<Integer>> voices, List<Rational[]> durations,
//  	int numberOfVoices) {
//  	
//  	// Make an empty Piece with the given number of voices
//  	Piece piece = new Piece();
//    NotationSystem system = piece.createNotationSystem();
//    for (int i = 0; i < numberOfVoices; i++) {
//      NotationStaff staff = new NotationStaff(system); 
//      system.add(staff);
//      NotationVoice voice = new NotationVoice(staff); 
//      staff.add(voice);
//      
//      Note voice0n0 = Transcription.createNote(67, new Rational(0, 4), new Rational(1, 2));
//      voice.add(voice0n0); 
//    }
//    
//    // Iterate through the Tablature, convert each TabSymbol into a note, and add it to the given voice
//    Integer[][] btp = tablature.getBasicTabSymbolProperties();
//    for (int i = 0; i < btp.length; i++) {
//    	// Create a Note from the TabSymbol at index i
//    	int pitch = btp[i][Tablature.PITCH];
//    	Rational metricTime = new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//    	Rational metricDuration = durations.get(i)[0]; // [0] is possible because each element in durations currently contains only one Rational
//    	Note note = createNote(pitch, metricTime, metricDuration);
//    	
//    	// Add the Note to each voice in currentVoices
//    	List<Integer> currentVoices = voices.get(i);
//    	for (int v : currentVoices) {
//    		NotationVoice voice = piece.getScore().get(v).get(0);
//    		voice.add(note);
//    	}	
//    }
//    
//    // Set the Piece in the Transcription
//    setPiece(piece);
//  }


	public static void setMaximumNumberOfVoices(int arg) {
		MAXIMUM_NUMBER_OF_VOICES = arg;
	}


	public void setFiles(List<File> arg) {
		files = arg;
	}


	public void setPieceName(String argString) {
		this.pieceName = argString;
	}


	public void setPiece(Piece argPiece) {
		piece = argPiece;
	}


	public void setUnadaptedGTPiece(Piece argPiece) {
		unadaptedGTPiece = argPiece;
	}


	public Piece getUnadaptedGTPiece() {
		return unadaptedGTPiece;
	}


	/**
	 * Returns a reversed version of the Transcription.
	 * 
	 * @param trans
	 * @param tab
	 * @return
	 */
	// TESTED
	public static Transcription reverse(Transcription trans, Tablature tab) {
		Piece pRev = reversePiece(trans, tab);

		Encoding eRev = null;
		if (tab != null) {
			eRev = Tablature.reverseEncoding(tab); // NB The value of normaliseTuning is irrelevant
		}
		return new Transcription(pRev, eRev);
	}


	/**
	 * Returns a deornamented version of the Transcription.
	 * 
	 * @param trans
	 * @param tab
	 * @param dur Only (single-event) notes with a duration shorter than this duration are 
	 *            considered ornamental.
	 * @return
	 */
	// TESTED
	public static Transcription deornament(Transcription trans, Tablature tab, Rational dur) {
		Piece pDeorn = deornamentPiece(trans, tab, dur);
		Encoding eDeorn = null;
		if (tab != null) {
			eDeorn = Tablature.deornamentEncoding(tab, Tablature.rationalToIntDur(dur)); // NB The value of normaliseTuning is irrelevant
		}
		return new Transcription(pDeorn, eDeorn);
	}


	/**
	 * Returns a copy of the given NotationSystem.
	 * @param ns
	 * @return
	 */
	private static NotationSystem copyNotationSystem(NotationSystem ns) {
		NotationSystem copy = new NotationSystem();

		for (NotationStaff notationStaff : ns) {
			NotationStaff copyNs = new NotationStaff();
			for (NotationVoice nv : notationStaff) {
				NotationVoice copyNv = new NotationVoice();
				for (NotationChord nc : nv) {
					NotationChord copyNc = new NotationChord();
					for (Note n : nc) {
						copyNc.add(Transcription.createNote(n.getMidiPitch(), n.getMetricTime(), n.getMetricDuration()));
					}
					copyNv.add(copyNc);
				}
				copyNs.add(copyNv);
			}
			copy.add(copyNs);
		}
		return copy;
	}


	/**
	 * Reverses each voice in the Transcription's Piece. NB: Handles the _unadapted_ Piece. 
	 * 
	 * @param trans
	 * @param tab
	 * @return
	 */
	// TESTED (through reverse())
	static Piece reversePiece(Transcription trans, Tablature tab) {
		Piece reversedPiece = new Piece();

		Rational mirrorPoint = trans.getMirrorPoint(trans.getMeterInfo());
		List<Rational[]> onsetsAndMinDurs = null;
		if (tab != null) {
			onsetsAndMinDurs = tab.getAllOnsetTimesAndMinDurations(); // NB The value of normaliseTuning is irrelevant
		}
		
		Piece origP = trans.getUnadaptedGTPiece();
		// Make a copy of notationSystem so that origP does not get affected
		NotationSystem copyOfNotationSystem = copyNotationSystem(origP.getScore());

		// For each voice
		NotationSystem reversedNotationSystem = new NotationSystem();
		for (NotationStaff notationStaff : copyOfNotationSystem) {	
			NotationStaff reversedNotationStaff = new NotationStaff();
			for (NotationVoice notationVoice : notationStaff) {
				NotationVoice reversedNotationVoice = new NotationVoice();
				for (NotationChord notationChord : notationVoice) {
					NotationChord newNotationChord = new NotationChord();
					for (Note n : notationChord) {
						// Calculate the Note's new onset time (mirrorPoint - offset time)
						Rational duration = n.getMetricDuration();
						// In tablature case: use minimum duration
						if (tab != null) {
							for (Rational[] item : onsetsAndMinDurs) {
								if (item[0].equals(n.getMetricTime())) {
									duration = item[1];
									duration.reduce();
									break;
								}
							}
						}
						Rational offsetTime = n.getMetricTime().add(duration);
						Rational newOnsetTime = mirrorPoint.sub(offsetTime); 
						
						// TODO Error in barbetta-1582-il_nest.tbp: last chord should be co1 
						// (and not co2), leading to newOnsetTime being -1/2 
						if (origP.getName().equals("barbetta-1582-il_nest.mid") && 
							newOnsetTime.equals(new Rational(-1, 2))) {
							newOnsetTime = Rational.ZERO;
							duration = new Rational(1, 2);
						}

						newNotationChord.add(Transcription.createNote(n.getMidiPitch(), newOnsetTime, duration));
					}
					notationChord = newNotationChord;
					reversedNotationVoice.add(newNotationChord);
				}
				reversedNotationStaff.add(reversedNotationVoice);
			}
			reversedNotationSystem.add(reversedNotationStaff);
		}

		reversedPiece.setHarmonyTrack(origP.getHarmonyTrack());
		reversedPiece.setScore(reversedNotationSystem);
		reversedPiece.setName(trans.getPiece().getName());
		return reversedPiece;
	}


	/**
	 * Removes all sequences of single-note events shorter than the given duration from the
	 * encoding, and lengthens the duration of the event preceding the sequence by the total 
	 * length of the removed sequence. NB: Handles the _unadapted_ Piece.
	 *
	 * @param trans
	 * @param tab
	 * @param dur
	 * @return
	 */
	// TESTED (through deornament())
	static Piece deornamentPiece(Transcription trans, Tablature tab, Rational dur) {
		Piece deornamentedPiece = new Piece();

		List<Rational> onsetTimes = trans.getAllOnsetTimes();
		List<List<Note>> chords = trans.getTranscriptionChords();
		List<List<TabSymbol>> tabChords = null;
		List<Rational[]> onsetsAndMinDurs = null;
		if (tab != null) {
			tabChords = tab.getTablatureChords(); 
			onsetsAndMinDurs = tab.getAllOnsetTimesAndMinDurations();
		}

		Piece origP = trans.getUnadaptedGTPiece();
		// Make a copy of notationSystem so that origP does not get affected
		NotationSystem copyOfNotationSystem = copyNotationSystem(origP.getScore());
		
		// For each voice
		NotationSystem deornamentedNotationSystem = new NotationSystem();
		List<Integer> removed = new ArrayList<>();
		int voice = 0;
		for (NotationStaff notationStaff : copyOfNotationSystem) {
//			System.out.println("voice = " + voice);
			NotationStaff deornamentedNs = new NotationStaff();
			for (NotationVoice nv : notationStaff) {
				NotationVoice deornamentedNv = new NotationVoice();
				NotationChord pre = null;
				Rational durPre = null;
				for (int i = 0; i < nv.size(); i++) {
					NotationChord currNc = nv.get(i);
					Rational onset = currNc.getMetricTime();
					int ind = onsetTimes.indexOf(onset);

					boolean isOrn; 
					if (tabChords != null) { 
						isOrn = tabChords.get(ind).size() == 1 && onsetsAndMinDurs.get(ind)[1].isLess(dur);
					}
					else {
						isOrn = currNc.size() == 1 && chords.get(ind).size() == 1 &&
							currNc.getMetricDuration().isLess(dur);
					}
					// If currNc is ornamental
					// NB In case of a single-event SNU, the note will be removed from both voices
					if (isOrn) {
						removed.add(ind);
						// Determine pre, if it has not yet been determined
						if (pre == null) {
							NotationChord ncPrev = nv.get(i-1);
							pre = ncPrev;
							durPre = ncPrev.getMetricDuration(); // all notes in a NotationChord need to have the same duration 
						}
						// Increment durPre
						durPre = durPre.add(currNc.getMetricDuration());
					}
					// If currNc is the first after a sequence of one or more ornamental notes
					// (i.e., it does not meet the if conditions above but pre != null)
					else if (pre != null) {
						// Adapt duration of pre
						for (Note n : pre) {
							n.setScoreNote(new ScoreNote(new ScorePitch(n.getMidiPitch()), 
								n.getMetricTime(), durPre));
						}
						// Add currNc
						deornamentedNv.add(currNc);
						// Reset
						pre = null;
					}
					else {
						deornamentedNv.add(currNc);
					}
				}
				deornamentedNs.add(deornamentedNv);
			}
			voice++;
			deornamentedNotationSystem.add(deornamentedNs);
		}

		deornamentedPiece.setHarmonyTrack(origP.getHarmonyTrack());
		deornamentedPiece.setScore(deornamentedNotationSystem);
		deornamentedPiece.setName(trans.getPiece().getName());
		return deornamentedPiece;
	}


	/**
	 * Finds the voice the given Note in the given NotationSystem belongs to.
	 *  
	 * @param note 
	 * @param notationSystem
	 * @return
	 */
	// TODO test
	public int findVoice(Note note, NotationSystem notationSystem) {
		int voice = -1;
		// NB: A NotationSystem has as many Staffs as the Transcription it belongs to has voices; each Staff thus 
		// represents a voice. The Staffs are numbered from top (no. 0) to bottom (no. 4, depending on 
		// MAXIMUM_NUMBER_OF_VOICES).
		// For each Staff in the NotationSystem: 
		outerLoop: for (int i = 0; i < notationSystem.size(); i++) {
			NotationStaff staff = notationSystem.get(i);
			// a. Get the contents of the Staff
			Containable[] contentsOfStaff = staff.getContentsRecursive();
			// b. Look at each Containable in the contents. If a Containable matches the (unique) note: return i, the
			// number of the Staff that note is on (and thus the number of the voice it belongs to), and break from the
			// outer loop
			for (int j = 0; j < contentsOfStaff.length; j++) {
				if (contentsOfStaff[j] == note) {
					voice = i;
					break outerLoop;
				}
			}
		}
		return voice;
	}


	/**
	 * Initialises noteSequence with the initial, unadapted NoteSequence from the Transcription,
	 * in which the Notes are ordered hierarchically according to
	 * (1) onset time (lower first);
	 * (2) if two Notes have the same onset time: pitch (lower first);
	 * (3) if two Notes have the same onset time and the same pitch: voice (lower first)
	 *     Thus, 
	 *       a. in the tablature case: 
	 *            unison notes are automatically ordered correctly (the one on the lower course comes first)
	 *            CoDnotes are not (i.e., with the one with the longer duration first); these are handled in
	 *            handleCoDNotes()      
	 *       b. in the non-tablature case:
	 *            unison notes are not necessarily ordered correctly (i.e., with the one with the longer duration
	 *            first; these are handled in handleUnisons(). 
	 *              
	 * NB: Must be called before initialiseVoiceLabels().
	 *   
	 */
	// TESTED (for both tablature- and non-tablature case simultaneously)
	public void initialiseNoteSequence() {
		NoteSequence initialNoteSeq = new NoteSequence(new NoteTimePitchComparator());
		NotationSystem notationSystem = piece.getScore();

		// 1. Fill initialNoteSeq; the NoteTimePitchComparator orders the Notes chord per chord,
		// from low to high
		Collection<Containable> contents = notationSystem.getContentsRecursiveList(null);
		for (Containable c : contents) {
			if (c instanceof Note) { 
				initialNoteSeq.add((Note) c);
			}
		}	

		// 2. Check for all equal-pitch-pairs whether the Notes are added to the NoteSequence in the correct order.
		// This is necessary because the NoteTimePitchComparator does not handle unisons and CoDs consistently in 
		// that sometimes the note in the lower voice is added first, and sometimes the note in the upper voice
		if (initialNoteSeq.size() != 0) {
			// For each note but the last:
			for (int currentNoteIndex = 0; currentNoteIndex < (initialNoteSeq.size() - 1); currentNoteIndex++) {
				// 1. Get the Note's pitch, onsetTime, and the voice it belongs to 
				Note currentNote = initialNoteSeq.getNoteAt(currentNoteIndex);
				int currentNotePitch = currentNote.getMidiPitch();
				Rational currentNoteOnsetTime = currentNote.getMetricTime();
				double currentNoteVoice = findVoice(currentNote, notationSystem);

				// 2. Check the remainder of initialNoteSeq for another Note with the same onsetTime and pitch. Break 
				// from inner for-loop when the onsetTime of nextNote becomes greater than that of currentNote
				for (int nextNoteIndex = currentNoteIndex + 1; nextNoteIndex < initialNoteSeq.size(); nextNoteIndex++) {
					Note nextNote = initialNoteSeq.getNoteAt(nextNoteIndex);
					// Same onsetTime? Check whether pitch is also the same  
					if (nextNote.getMetricTime().equals(currentNoteOnsetTime)) {
						// Same pitch? nextNote is the complement sought; swap if necessary and break
						// NB: since an event may contain more than one equal-pitch-pair, the ENTIRE process must be repeated
						// from the start until the sequence is correct. (E.g., an event with three equal pitches that are
						// added to the NoteSequence in voice order 1-2-3 becomes 2-1-3 after one iteration of both for-loops,
						// then 3-1-2, and finally 3-2-1.) Thus, when notes are swapped: break from inner for-loop and start
						// again in outer for-loop
						if (nextNote.getMidiPitch() == currentNotePitch) {
							int nextNoteVoice = findVoice(nextNote, notationSystem);	  
							// If currentNote is in the higher voice (has the lower voice number): swap 
							if (currentNoteVoice < nextNoteVoice) {
								initialNoteSeq.swapNotes(currentNoteIndex, nextNoteIndex);
								currentNoteIndex = -1;
								break;
							}    		    
						}
					}
					// Is onsetTime of nextNote greater than that of currentNote? Break and continue with the next currentNote
					else {
						break; 
					}
				}
			}
		}
		// 3. Set noteSequence 
		setNoteSequence(initialNoteSeq);
	}


	/** 
	 * Initialises the voice labels from the NoteSequence. Each voice label is a binary
	 * double vector containing MAXIMUM_NUMBER_OF_VOICES elements, where the position of
	 * the 1.0 indicates the voice encoded.  
	 * 
	 * NB1: The voice labels are initialised using the initial, unadapted NoteSequence. 
	 * NB2: Must be called after initialiseNoteSequence(). 
	 */
	// TESTED (for both tablature- and non-tablature case simultaneously)
	public void initialiseVoiceLabels() {
		NoteSequence initialNoteSeq = getNoteSequence();
		List<List<Double>> initialVoiceLabels = new ArrayList<List<Double>>(); 

		// For every Note in noteSeq: 
		for (int i = 0; i < initialNoteSeq.size(); i++) {
			Note note = initialNoteSeq.getNoteAt(i);
			// 1. Create a voice label for the Note
			List<Double> currentVoiceLabel = new ArrayList<Double>();
			
			// 2. Extract the voice the Note is in and fill currentVoiceLabel
			NotationSystem system = piece.getScore();
			int voice = findVoice(note, system);
			for (int j = 0; j < MAXIMUM_NUMBER_OF_VOICES; j++) {
				if (voice == j) {
					currentVoiceLabel.add(j, 1.0);
				}
				else {
					currentVoiceLabel.add(j, 0.0);
				}
			}

			// 3. Add currentVoiceLabel to initialVoiceLabels 
			initialVoiceLabels.add(currentVoiceLabel);
		}
		// Set voiceLabels
		setVoiceLabels(initialVoiceLabels);
//		voiceLabels = initialVoiceLabels;
	}


	/**
	 * Initialises durationLabels with the initial, unadapted voice labels -- i.e., the ones that go with the notes
	 * in the initial, unadapted NoteSequence. Each duration label is a List<Double> containing Tablature.SMALLEST_
	 * RHYTHMIC_VALUE.getDenom() elements, one of which has value 1.0 and indicates the encoded full duration (where
	 * position 0 is a duration of 1/32, position 1 a duration of 2/32, etc.), while the others have value 0.0.  
	 * 
	 * NB1: Tablature case only; must be called after initialiseNoteSequence().
	 */
	// TESTED (for both tablature- and non-tablature case simultaneously)
	void initialiseDurationLabels() {
		List<List<Double>> initialDurationLabels = new ArrayList<List<Double>>();

		// Iterate through all notes in the initial NoteSequence, which for each CoD still contains both
		// CoDnotes. Lower CoDnotes are always in the lower voice and thus come first in the NoteSequence   
		NoteSequence initialNoteSeq = getNoteSequence();
		for (Note n : initialNoteSeq) {
			Rational durationCurrentNote = n.getMetricDuration();
			int numer = durationCurrentNote.getNumer();
			int denom = durationCurrentNote.getDenom();
			// Determine the duration in 32nd notes
			// NB: Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()/denom will always be divisible by denom because denom will
			// always be a fraction of Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom(): 32, 16, 8, 4, 2, or 1
			int duration = numer * (Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()/denom);
//			System.out.println(duration);
//			System.out.println(n.getMidiPitch());
//			System.out.println(n.getMetricTime());
			// Create the durationLabel for n and add it to initialDurationLabels
			initialDurationLabels.add(createDurationLabel(duration));
		}
		// Set durationLabels
		setDurationLabels(initialDurationLabels);
	}


	void setNoteSequence(NoteSequence argNoteSequence) {
		noteSequence = argNoteSequence;
	}


	public void setVoiceLabels(List<List<Double>> argVoiceLabels) {
		voiceLabels = argVoiceLabels;
	}


	void setDurationLabels(List<List<Double>> argDurationLabels) {
		durationLabels = argDurationLabels;
	}


	/**
	 * (1) Checks whether the Tablature and Transcription contain the same number of chords.
	 * (2) Checks whether the Tablature contains no chords with more than one CoD, unison, or course crossing, or 
	 *     with combinations of these. 
	 * Returns <code>true</code> if both (1) and (2) are met, and <code>false</code> if not. If either (1) or (2)
	 * are not met, sets chordErrorDetails with further information on
	 *     when (1) is not met: the number of chords;
	 *     when (2) is not met:
	 *       a. Information on the number of notes, chords, and rest events;
	 *       b. Information on all special chords, i.e., all chords containing one or more CoDs, unisons, or course 
	 *          crossings; 
	 *       c. Information on all chords containing duplicates or combinations of CoDs, unisons, or course crossings. 
	 *  
	 * NB: Tablature case only.
	 *  
	 * @param tablature
	 * @return
	 */
	// TODO test
	boolean checkChords(Tablature tablature) {  
		boolean checkPassed = true;

		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();

		// 0. Check for equality of chord numbers
		if (tablatureChords.size() != transcriptionChords.size()) {
			chordsSpecification = chordsSpecification.concat("The Tablature contains " + tablatureChords.size() +
				" chords, and the Transcription " + transcriptionChords.size() + ".");
			return false;
		}

		// 1. Create noteAndChordNumbers
		List<Integer> isRestEvent = 
			tablature.getEncoding().getListsOfStatistics().get(Encoding.IS_REST_EVENT_INDEX);
		int numberOfRestEvents = Collections.frequency(isRestEvent, 1);
		String noteAndChordNumbers = "Note and chord numbers:" + "\n";
		noteAndChordNumbers = noteAndChordNumbers.concat("  Number of notes: " + tablature.getNumberOfNotes() + "\n" +
			"  Number of chords: " + tablatureChords.size() + "\n" +
			"  Number of restEvents: " + numberOfRestEvents + "\n");

		// 2. For each chord: check whether it is a special chord. If so, list it
		List<String> chordsWithOneEqualPitchPair = new ArrayList<String>();
		List<String> chordsWithTwoEqualPitchPairs = new ArrayList<String>();
		List<String> chordsWithEqualPitchTriplets = new ArrayList<String>();
		List<String> chordsWithOneCoD = new ArrayList<String>();
		List<String> chordsWithTwoCoDs = new ArrayList<String>();
		List<String> chordsWithThreeCoDs = new ArrayList<String>();
		List<String> chordsWithOneUnison = new ArrayList<String>();
		List<String> chordsWithTwoUnisons = new ArrayList<String>();
		List<String> chordsWithThreeUnisons = new ArrayList<String>();
		List<String> chordsWithOneCourseCrossing = new ArrayList<String>();
		List<String> chordsWithTwoCourseCrossings = new ArrayList<String>();
		List<String> chordsWithThreeCourseCrossings = new ArrayList<String>();

//		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
		for (int i = 0; i < tablatureChords.size(); i++) {
			// Get the metric position of the chord
			Integer[][] basicTabSymbolPropertiesChord = tablature.getBasicTabSymbolPropertiesChord(i);
			Rational onsetTime = new Rational(basicTabSymbolPropertiesChord[0][Tablature.ONSET_TIME], 
				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			Rational[] metricPosition = Tablature.getMetricPosition(onsetTime, tablature.getMeterInfo());
			String bar = String.valueOf(metricPosition[0].getNumer());
			String positionWithinBar = " " + String.valueOf(metricPosition[1]);
			if (metricPosition[1].getNumer() == 0) {
				positionWithinBar = ""; 
			}
			String metricPosAsString = bar + positionWithinBar;

			// a. Check for equal-pitch-pairs. If occurrences contains the number 2 twice, there is one equal-pitch-pair;
			// if it contains the number 2 four times, there are two equal-pitch-pairs; if it contains the number 3, 
			// there is an equal-pitch-triplet.
			List<Integer> occurrences = getOccurrencesOfPitchesInChord(i);
			if (Collections.frequency(occurrences, 2) == 2) {
				chordsWithOneEqualPitchPair.add(metricPosAsString);
			}
			if (Collections.frequency(occurrences, 2) == 4) {
				chordsWithTwoEqualPitchPairs.add(metricPosAsString);
			}
			if (occurrences.contains(3)) {
				chordsWithEqualPitchTriplets.add(metricPosAsString);
			} 	  
			// b. Check for (a) CoD(s)
			if (transcriptionChords.get(i).size() != tablatureChords.get(i).size()) {
				if (transcriptionChords.get(i).size() == (tablatureChords.get(i).size() + 1)) {					
					chordsWithOneCoD.add(metricPosAsString);
				}
				if (transcriptionChords.get(i).size() == (tablatureChords.get(i).size() + 2)) {
					chordsWithTwoCoDs.add(metricPosAsString);
				}
				if (transcriptionChords.get(i).size() == (tablatureChords.get(i).size() + 3)) {
					chordsWithThreeCoDs.add(metricPosAsString);
				}
			}	     
			// c. Check for (a) unison(s)
			if (tablature.getUnisonInfo(i) != null) {
				if (tablature.getUnisonInfo(i).length == 1) {
					chordsWithOneUnison.add(metricPosAsString);
				}
				if (tablature.getUnisonInfo(i).length == 2) {
					chordsWithTwoUnisons.add(metricPosAsString);
				}
				if (tablature.getUnisonInfo(i).length == 3) {
					chordsWithThreeUnisons.add(metricPosAsString);
				}
			}
			// d. Check for (a) course crossing(s)
			if (tablature.getCourseCrossingInfo(i) != null) {
				if (tablature.getCourseCrossingInfo(i).length == 1) {
					chordsWithOneCourseCrossing.add(metricPosAsString);
				}
				if (tablature.getCourseCrossingInfo(i).length == 2) {
					chordsWithTwoCourseCrossings.add(metricPosAsString);
				}
				if (tablature.getCourseCrossingInfo(i).length == 3) {
					chordsWithThreeCourseCrossings.add(metricPosAsString);
				}
			}
		}

		// 3. Find any illegal duplicates or combinations. Chords cannot contain 
		// (i) more than one unison, CoD, or course crossing
		// (ii) any combinations of unisons, CoDs, or course crossings
		String details = "";
		// a. Does the piece contain any chords with more than one CoD, unison, or course crossing?  
		if (chordsWithTwoCoDs.size() != 0 || chordsWithThreeCoDs.size() != 0 || chordsWithTwoUnisons.size() != 0 ||
			chordsWithThreeUnisons.size() != 0 ||	chordsWithTwoCourseCrossings.size() != 0 || chordsWithThreeCourseCrossings.size() != 0) {
			checkPassed = false;
			// Add to details
			if (chordsWithTwoCoDs.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithTwoCoDs + " contain(s) two CoDs \n");
			}
			if (chordsWithThreeCoDs.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithThreeCoDs + " contain(s) three CoDs \n");
			}
			if (chordsWithTwoUnisons.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithTwoUnisons + " contain(s) two unisons \n");
			}
			if (chordsWithThreeUnisons.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithThreeUnisons + " contain(s) three unisons \n");
			}
			if (chordsWithTwoCourseCrossings.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithTwoCourseCrossings + " contain(s) two course crossings \n");
			}
			if (chordsWithThreeCourseCrossings.size() != 0) {
				details = details.concat("      Chord(s) at bar(s) " + chordsWithThreeCourseCrossings + " contain(s) three course crossings \n");
			}
		}

		// b. Does the piece contain any chords with any combination of CoDs, unisons, and course crossings? 
		// Turn all the Lists into one big List  
		List<String> allSpecialChords = new ArrayList<String>();
		allSpecialChords.addAll(chordsWithOneCoD); allSpecialChords.addAll(chordsWithTwoCoDs);
		allSpecialChords.addAll(chordsWithThreeCoDs); 
		allSpecialChords.addAll(chordsWithOneUnison);	allSpecialChords.addAll(chordsWithTwoUnisons);
		allSpecialChords.addAll(chordsWithThreeUnisons);
		allSpecialChords.addAll(chordsWithOneCourseCrossing);	allSpecialChords.addAll(chordsWithTwoCourseCrossings);
		allSpecialChords.addAll(chordsWithThreeCourseCrossings);

		// Find which chord appears more than once in allSpecialChords and add that to chordsWithCombinations
		List<String> chordsWithCombination = new ArrayList<String>();
		for (int i = 0; i < allSpecialChords.size(); i++) {
			String specialChordIndex = allSpecialChords.get(i);
			if (Collections.frequency(allSpecialChords, specialChordIndex) > 1) {
				checkPassed = false;
				// To avoid unnecessary duplicates: add to chordsWithCombination only once 
				if (!chordsWithCombination.contains(specialChordIndex)) {
					chordsWithCombination.add(specialChordIndex);
				}
			}
		}

		// If combinations are found: add to details
		if (chordsWithCombination.size() != 0) {
			// Combine the sublists
			List<String> chordsWithCoDs = new ArrayList<String>();
			chordsWithCoDs.addAll(chordsWithOneCoD); chordsWithCoDs.addAll(chordsWithTwoCoDs);
			chordsWithCoDs.addAll(chordsWithThreeCoDs);
			List<String> chordsWithUnisons = new ArrayList<String>();
			chordsWithUnisons.addAll(chordsWithOneUnison); chordsWithUnisons.addAll(chordsWithTwoUnisons);
			chordsWithUnisons.addAll(chordsWithThreeUnisons);
			List<String> chordsWithCourseCrossings = new ArrayList<String>();
			chordsWithCourseCrossings.addAll(chordsWithOneCourseCrossing); chordsWithCourseCrossings.addAll(chordsWithTwoCourseCrossings);
			chordsWithCourseCrossings.addAll(chordsWithThreeCourseCrossings);

			for (int j = 0; j < chordsWithCombination.size(); j++) {	
				String metricPosition = chordsWithCombination.get(j);

				// There are four possible combinations:
				// 1. CoDs and unisons (= unisons and CoDs)  
				// 2. CoDs and course crossings (= course crossings and CoDs)
				// 3. unisons and course crossings (= course crossings and unisons)
				// 4. CoDs, unisons, and course crossings
				if (chordsWithCoDs.contains(metricPosition) && chordsWithUnisons.contains(metricPosition)) {
					details = details.concat("      Chord at bar " + metricPosition + " contains (a) CoD(s) and (a) unison(s)." + "\n");
				}
				if (chordsWithCoDs.contains(metricPosition) && chordsWithCourseCrossings.contains(metricPosition)) {
					details = details.concat("      Chord at bar " + metricPosition + " contains (a) CoD(s) and (a) course crossing(s)." + "\n");	
				}
				if (chordsWithUnisons.contains(metricPosition) && chordsWithCourseCrossings.contains(metricPosition)) {
					details = details.concat("      Chord at bar " + metricPosition + " contains (a) unison(s) and (a) course crossing(s)." + "\n");
				}	 
				if (chordsWithCoDs.contains(metricPosition) && chordsWithUnisons.contains(metricPosition) &&
					chordsWithCourseCrossings.contains(metricPosition)) {
					details = details.concat("      Chord at bar " + metricPosition + " contains (a) CoD(s), (a) unison(s), and (a) course crossing(s)." + "\n");
				}
			}
		}

		// 4. Create listsOfSpecialChords
		String listsOfSpecialChords = "Special chords:" + "\n"; 
		listsOfSpecialChords = listsOfSpecialChords.concat(
			"  Equal pitch:" + "\n" +	
			"    Transcription chords with one equal-pitch-pair at bar(s):    " + chordsWithOneEqualPitchPair + "\n" +
			"    Transcription chords with two equal-pitch-pairs at bar(s):   " + chordsWithTwoEqualPitchPairs + "\n" +  	
			"    Transcription chords with three equal-pitch-pairs at bar(s): " + chordsWithEqualPitchTriplets + "\n" +
			"    (a) CoDs" + "\n" +
			"    Transcription chords with one CoD at bar(s):                 " + chordsWithOneCoD + "\n" +
			"    Transcription chords with two CoDs at bar(s):                " + chordsWithTwoCoDs + "\n" +
			"    Transcription chords with three CoDs at bar(s):              " + chordsWithThreeCoDs + "\n" +
			"    (b) Unisons" + "\n" +
			"    Tablature chords with one unison at bar(s):                  " + chordsWithOneUnison + "\n" +
			"    Tablature chords with two unisons at bar(s):                 " + chordsWithTwoUnisons + "\n" +
			"    Tablature chords with three unisons at bar(s):               " + chordsWithThreeUnisons + "\n" +
			"  Course crossing:" + "\n" +
			"    Tablature chords with one course crossing at bar(s):         " + chordsWithOneCourseCrossing + "\n" +
			"    Tablature chords with two course crossings at bar(s):        " + chordsWithTwoCourseCrossings + "\n" +
			"    Tablature chords with three course crossings at bar(s):      " + chordsWithThreeCourseCrossings + "\n" +
			"  COMBINATIONS:" + "\n" + 
			"    Chords with duplicates or combinations of the above: \n" + details + "\n");

		chordsSpecification = chordsSpecification.concat(noteAndChordNumbers + listsOfSpecialChords);

		return checkPassed; 
	}


	public String getChordsSpecification() {
		return chordsSpecification;
	}


	/** 
	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in
	 * the Tablature the corresponding Notes in the Transcription, and
	 * (1) removes the CoDnote with the shorter duration from noteSequence
	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets 
	 *     the label of the lower CoDnote to the result in voiceLabels, and removes the label 
	 *     of the upper from voiceLabels
	 * (3) combines the duration labels of both CoDnotes into a List<Double> with two 1.0s, sets
	 *     the label of the lower CoDnote to the result in durationLabels, and removes the label
	 *     of the upper from durationLabels 
	 *
	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the 
	 * Transcription, containing for each element:
	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
	 *   b. if the the note at that index is a CoDnote: an Integer[] containing 
	 *        as element 0: the voice the longer CoDnote is in;
	 *        as element 1: the voice the shorter CoDnote is in.
	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in
	 *      the lower voice that comes first in the NoteSequence) is placed at element 0.
	 * 
	 * If isGroundTruthTranscription is <code>false</code>, i.e., when the method is applied to
	 * a predicted Transcription, only noteSequence is adapted. This is because the predicted 
	 * voiceLabels and durationLabels are already ready-to-use (only the voicesCoDNotes still 
	 * need to be created from them).
	 * 
	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor
	 *      a course crossings.
	 * NB2: Tablature case only; must be called before handleCourseCrossings().
	 * 
	 * @param tablature
	 * @param isGroundTruthTranscription
	 */
	// TESTED
	void handleCoDNotes(Tablature tablature, boolean isGroundTruthTranscription) {
		NoteSequence noteSeq = getNoteSequence();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();

		List<List<Double>> voiceLab = new ArrayList<List<Double>>(); // getVoiceLabels();
		List<List<Double>> durationLab = new ArrayList<List<Double>>(); // getDurationLabels();
		List<Integer[]> voicesCoD = new ArrayList<Integer[]>();
		if (isGroundTruthTranscription) {
			voiceLab = getVoiceLabels();
			durationLab = getDurationLabels();
			// Initialise voicesCoD with all elements set to null
			for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
				voicesCoD.add(null);
			}
			// Set voicesCoD (in case the pieces contains no SNUs and the setting does not 
			// happen in the for-loop below
			setVoicesCoDNotes(voicesCoD);
		}

		// For every chord
		for (int i = 0; i < tablatureChords.size(); i++) {
			// If the chord contains a CoD
			if (getCoDInfo(tablatureChords, i) != null) {
				Integer[][] coDInfo = getCoDInfo(tablatureChords, i);
				// Get the (most recent! needed for calculating notesPreceding) transcription chords
				List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
				// For each CoD in the chord 
				int notesAlreadyRemovedFromChord = 0;
				for (int j = 0; j < coDInfo.length; j++) {
					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
					// a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
					int notesPreceding = 0;
					for (int k = 0; k < i; k++) {
						notesPreceding += transcriptionChords.get(k).size();
					}
					// b. Calculate the indices
					int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
					int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);

					// 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
					// a. noteSeq: remove the CoDnote with the shorter duration
					Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
					// Assume that the lower note has the longer duration. If this is so or if both notes have the
					// same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
					// indexOfUpperCoDNote 
					int indexOfLongerCoDNote = indexOfLowerCoDNote;
					int indexOfShorterCoDNote = indexOfUpperCoDNote;
					if (durationLower.isLess(durationUpper)) {
						indexOfShorterCoDNote = indexOfLowerCoDNote;
						indexOfLongerCoDNote = indexOfUpperCoDNote;
					}
					noteSeq.deleteNoteAt(indexOfShorterCoDNote);
					if (isGroundTruthTranscription) { 
						// The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must  
						// be determined before voiceLab is adapted   
						List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
						int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
						List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
						int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
						// b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to 
						// the result, and remove the label of the upper CoDnote from voiceLab
						List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
						List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
						List<Double> combinedVoiceLabel = combineLabels(voiceLabelOfLowerCoDNote, voiceLabelOfUpperCoDNote);
						voiceLab.set(indexOfLowerCoDNote, combinedVoiceLabel);
						voiceLab.remove(indexOfUpperCoDNote);
						// c. durationLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
						// result, and remove the label of the upper CoDnote from durationLab
						List<Double> durationLabelOfLowerCoDNote = durationLab.get(indexOfLowerCoDNote);
						List<Double> durationLabelOfUpperCoDNote = durationLab.get(indexOfUpperCoDNote);
						List<Double> combinedDurationLabel = 
							combineLabels(durationLabelOfLowerCoDNote, durationLabelOfUpperCoDNote);
						durationLab.set(indexOfLowerCoDNote, combinedDurationLabel);
						durationLab.remove(indexOfUpperCoDNote);
						// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
						// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
						voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});
					}
					// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
					// iteration through the inner for-loop is necessary
					notesAlreadyRemovedFromChord++;

					// 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
					setNoteSequence(noteSeq);
					if (isGroundTruthTranscription) {
						setVoiceLabels(voiceLab);
						setDurationLabels(durationLab);
						setVoicesCoDNotes(voicesCoD);
					}

					// 5. Concat information to adaptations
					adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote	
						- notesPreceding) +	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
						"list of voice labels and list of durations adapted accordingly." + "\n");
				}
			}
		}
	}


	/**
	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in the Tablature the
	 * corresponding Notes in the Transcription, and
	 * (1) removes the CoDnote with the shorter duration from noteSequence
	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the lower
	 *     CoDnote to the result in voiceLabels, and removes the label of the upper from voiceLabels
	 * (3) combines the duration labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the 
	 *     lower CoDnote to the result in durationLabels, and removes the label of the upper from durationLabels 
	 *
	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the Transcription, 
	 * containing for each element:
	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
	 *   b. If the the note at that index is a CoDnote: an Integer[] containing 
	 *      as element 0: the voice the longer CoDnote is in;
	 *      as element 1: the voice the shorter CoDnote is in.
	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in the lower voice
	 *      that comes first in the NoteSequence) is placed at element 0.
	 * 
	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor a course crossings.
	 * NB2: Tablature case only; must be called before handleCourseCrossings().
	 * 
	 * @param tablature
	 */
	// TESTED
	private void handleCoDNotesOUD(Tablature tablature) {				
		NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<Double>> durationLab = getDurationLabels();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		List<Integer[]> voicesCoD = new ArrayList<Integer[]>();  
		// Initialise voicesCoD with all elements set to null
		for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
			voicesCoD.add(null);
		}

		// For every chord
		for (int i = 0; i < tablatureChords.size(); i++) {
			// If the chord contains a CoD
			if (getCoDInfo(tablatureChords, i) != null) {
				Integer[][] coDInfo = getCoDInfo(tablatureChords, i);
				// Get the (most recent! needed for calculating notesPreceding) transcription chords
				List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
				// For each CoD in the chord 
				int notesAlreadyRemovedFromChord = 0;
				for (int j = 0; j < coDInfo.length; j++) {
					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
					// a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
					int notesPreceding = 0;
					for (int k = 0; k < i; k++) {
						notesPreceding += transcriptionChords.get(k).size();
					}
					// b. Calculate the indices
					int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
					int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);

					// 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
					// a. noteSeq: remove the CoDnote with the shorter duration
					Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
					// Assume that the lower note has the longer duration. If this is so or if both notes have the
					// same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
					// indexOfUpperCoDNote 
					int indexOfLongerCoDNote = indexOfLowerCoDNote;
					int indexOfShorterCoDNote = indexOfUpperCoDNote;
					if (durationLower.isLess(durationUpper)) {
						indexOfShorterCoDNote = indexOfLowerCoDNote;
						indexOfLongerCoDNote = indexOfUpperCoDNote;
					}
					noteSeq.deleteNoteAt(indexOfShorterCoDNote);
					// The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must be 
					// determined before voiceLab is adapted   
					List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
					int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
					List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
					int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
					// b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
					// result, and remove the label of the upper CoDnote from voiceLab
					List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
					List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
					List<Double> combinedVoiceLabel = combineLabels(voiceLabelOfLowerCoDNote, voiceLabelOfUpperCoDNote);
					voiceLab.set(indexOfLowerCoDNote, combinedVoiceLabel);
					voiceLab.remove(indexOfUpperCoDNote);
					// c. durationLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
					// result, and remove the label of the upper CoDnote from durationLab
					List<Double> durationLabelOfLowerCoDNote = durationLab.get(indexOfLowerCoDNote);
					List<Double> durationLabelOfUpperCoDNote = durationLab.get(indexOfUpperCoDNote);
					List<Double> combinedDurationLabel = combineLabels(durationLabelOfLowerCoDNote, durationLabelOfUpperCoDNote);
					durationLab.set(indexOfLowerCoDNote, combinedDurationLabel);
					durationLab.remove(indexOfUpperCoDNote);
					// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
					// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
					voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});

					// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
					// iteration through the inner for-loop is necessary
					notesAlreadyRemovedFromChord++;

					// 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
					setNoteSequence(noteSeq);
					setVoiceLabels(voiceLab);
					setDurationLabels(durationLab);
					setVoicesCoDNotes(voicesCoD);

					// 5. Concat information to adaptations
					adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote
						- notesPreceding) +	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
						"list of voice labels and list of durations adapted accordingly." + "\n");
				}
			} 
		}
	}


	// TESTED (together with getVoicesCoDNotes())
	public void setVoicesCoDNotes(List<Integer[]> argVoicesCoDNotes) {
		voicesCoDNotes = argVoicesCoDNotes;
	}


	/**
	 * Finds for all course-crossing notes (i.e., notes pairs where the note on the lower course has the higher
	 * pitch) in the Tablature the corresponding Notes in the Transcription, and 
	 * (1) swaps these Notes in noteSequence;
	 * (2) swaps the corresponding voice labels in voiceLabels;
	 * (3) swaps the corresponding duration labels in durationLabels. 
	 * 
	 * If isGroundTruthTranscription is <code>false</code>, i.e., when the method is applied to a predicted 
	 * Transcription, only noteSequence is adapted. This is because the predicted voiceLabels and durationLabels 
	 * are already ready-to-use (only the voicesCoDNotes still need to be created from them).
	 * 
	 * NB1: This method presumes that a chord contains only one course crossing, and neither a CoD nor a unison.
	 * NB2: Tablature case only; must be called after handleCoDNotes().
	 * 
	 * @param tablature
	 * @param isGroundTruthTranscription
	 */
	// TESTED
	void handleCourseCrossings(Tablature tablature, boolean isGroundTruthTranscription) {
		NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLab = new ArrayList<List<Double>>(); // getVoiceLabels();
		List<List<Double>> durationLab = new ArrayList<List<Double>>(); // getDurationLabels();
		if (isGroundTruthTranscription) {
			voiceLab = getVoiceLabels();
			durationLab = getDurationLabels();
		}
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();

		// For every chord
		for (int i = 0; i < tablatureChords.size(); i++) {
			// If the chord contains a course crossing
			if (tablature.getCourseCrossingInfo(i) != null) {
				Integer[][] chordCrossingInfo = tablature.getCourseCrossingInfo(i);
				// For each course crossing in the chord
				for (int j = 0; j < chordCrossingInfo.length; j++) {
					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CCnotes
					// a. Calculate the number of Notes preceding the CC chord by summing the size of all previous chords
					int notesPreceding = 0;
					for (int k = 0; k < i; k++) {
						notesPreceding += transcriptionChords.get(k).size();
					}
					// b. Calculate the indices
					int indexOfLowerCCNote = notesPreceding + chordCrossingInfo[j][2];
					int indexOfUpperCCNote = notesPreceding + chordCrossingInfo[j][3];

					// 2. Swap
					noteSeq.swapNotes(indexOfLowerCCNote, indexOfUpperCCNote);
					if (isGroundTruthTranscription) {
						Collections.swap(voiceLab, indexOfLowerCCNote, indexOfUpperCCNote);
						Collections.swap(durationLab, indexOfLowerCCNote, indexOfUpperCCNote);
					}

					// 3. Concat information to adaptations
					adaptations = adaptations.concat("  Course crossing found in chord " + i + ": notes no. " + 
						chordCrossingInfo[j][2] + " (pitch " + chordCrossingInfo[j][0]	+ ") and " + chordCrossingInfo[j][3] +
						" (pitch " + chordCrossingInfo[j][1] + ") in that chord swapped in the NoteSequence; "+ "list of " + 
						"voice labels and list of durations adapted accordingly." + "\n");
				}
			}
		}			
		// Reset noteSequence, voiceLabels, and durationLabels
		setNoteSequence(noteSeq);
		if (isGroundTruthTranscription) {
			setVoiceLabels(voiceLab);
			setDurationLabels(durationLab);
		}
	}


	/**
	 * Checks whether the Transcription and Tablature are aligned by 
	 * (1) comparing their number of notes;
	 * (2) comparing the order of pitches and onset times in the Transcription's NoteSequence and in the 
	 *     Tablature's basicTabSymbolProperties.
	 * Returns <code>true</code> if both (1) and (2) are met, and <code>false</code> if not.     
	 * 
	 * NB: Tablature case only; must be called after handleCourseCrossings().
	 * 
	 * @param tablature
	 * @return
	 */
	// TESTED
	boolean checkAlignment(Tablature tablature) {
		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
		NoteSequence noteSeq = getNoteSequence();

		// 1. Check equality of number of notes
		if (basicTabSymbolProperties.length != noteSeq.size()) {
			alignmentDetails = alignmentDetails.concat("The Tablature contains " + basicTabSymbolProperties.length + 
				" notes, and the Transcription contains " + noteSeq.size() + " notes.");		
			return false;
		}

		// 2. Check alignment
		for (int i = 0; i < basicTabSymbolProperties.length; i++) {	
			// Get the pitch and onset time of the TS at index i
			int pitchCurrentTabSymbol = basicTabSymbolProperties[i][Tablature.PITCH];
			Rational onsetTimeCurrentTabSymbol = 
				new Rational(basicTabSymbolProperties[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			onsetTimeCurrentTabSymbol.reduce();
			// Get the pitch and onset time of the Note at index i in noteSeq
			Note currentNote = noteSeq.getNoteAt(i);
			int pitchCurrentNote = currentNote.getMidiPitch();
			Rational onsetTimeCurrentNote = currentNote.getMetricTime();
			onsetTimeCurrentNote.reduce();
			// Compare
			if (pitchCurrentTabSymbol != pitchCurrentNote || !onsetTimeCurrentTabSymbol.equals(onsetTimeCurrentNote)) {
				alignmentDetails = alignmentDetails.concat("Misalignment found at note index " + i + " (chord index " + 
					basicTabSymbolProperties[i][Tablature.CHORD_SEQ_NUM] + "; " + "sequence number " + 
					basicTabSymbolProperties[i][Tablature.NOTE_SEQ_NUM] + "): pitch and onset time in " + 
					"tablature are " + pitchCurrentTabSymbol + " and " + onsetTimeCurrentTabSymbol + "; pitch and onset " +
					"time in transcription are " + pitchCurrentNote + " and " + onsetTimeCurrentNote + ".");
				return false;
			}
		}
		return true;
	}


	/**
	 * Sets meterInfo.
	 * 
	 * NB: Non-tablature case only.
	 */
	private void setMeterInfo(File file) {
//		File file = getFile();
		String[] contents = file.list();
		for (String s: contents) {
			if (s.endsWith(".txt")) {
				File meterFile = new File(file + "/" + s);
				String meterInfoString = ToolBox.readTextFile(meterFile);
//				meterInfo = Tablature.createMeterInfo(meterInfoString);
			}
		}
	}
	
	
	/**
	 * Sets meterInfo.
	 * 
	 * NB: Non-tablature case only.
	 */
	void setMeterInfo() {
		meterInfo = createMeterInfo(getPiece());
	}
	
	
	/**
	 * Sets keyInfo.
	 * 
	 * @param argMeterInfo <code>null</code> in the non-tablature case.
	 */
	void setKeyInfo(/*List<Integer[]> argMeterInfo*/) {
////		if (meterInfo == null) {
////			keyInfo = createKeyInfo(getPiece(), getMeterInfo());
////		}
//		keyInfo = (argMeterInfo == null) ? createKeyInfo(getPiece(), getMeterInfo()) : 
//			createKeyInfo(getPiece(), argMeterInfo);
		keyInfo = createKeyInfo(getPiece(), getMeterInfo());
	}


	public void setColourIndices(List<List<Integer>> arg) {
		colourIndices = arg;
	}


	public List<List<Integer>> getColourIndices() {
		return colourIndices;
	}


	/**
	 * Returns the meter at the given metric time.
	 * 
	 * @param mt
	 * @param meterInfo
	 * @return
	 */
	// TESTED
	public static Rational getMeter(Rational mt, List<Integer[]> meterInfo) {
		for (Integer[] i : meterInfo) {
			Rational curr = new Rational(i[0], i[1]);
			Rational start = new Rational(i[4], i[5]);
			int numBarsInMeter = (i[3] - i[2]) + 1; 
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
	public static Rational getMeter(int bar, List<Integer[]> meterInfo) {
		for (Integer[] i : meterInfo) {
			if (bar >= i[2] && bar < i[3]+1) {
				return new Rational(i[0], i[1]);
			}
		}
		return null;
	}


	/**
	 * Creates the meterInfo from the Transcription's Piece.
	 *  
	 * @return A list, each element of which represents a meter in the piece and contains:
	 *   <ul>
	 *   <li> as element 0: the numerator of the meter </li>
	 *   <li> as element 1: the denominator of the meter </li>
	 *   <li> as element 2: the first bar in the meter </li>
	 *   <li> as element 3: the last bar in the meter </li>
	 *   <li> as element 4: the numerator of the metric time of that first bar </li>
	 *   <li> as element 5: the denominator of the metric time of that first bar </li>
	 *   </ul>
	 *   An anacrusis will be denoted with bar numbers 0-0.
	 */
	// TESTED
	public static List<Integer[]> createMeterInfo(Piece piece) {		
//		long[][] timeSigs = getPiece().getMetricalTimeLine().getTimeSignature();
		long[][] timeSigs = piece.getMetricalTimeLine().getTimeSignature();
		
		int numTimeSigs = timeSigs.length;
		int start = 1;
		// If there is an anacrusis, start should be 0
		if (numTimeSigs > 1) {
			Rational firstTimeSig = new Rational((int)timeSigs[0][0], (int)timeSigs[0][1]);
			Rational secondMetricTime = new Rational((int)timeSigs[1][3], (int)timeSigs[1][4]);
			Rational secondTimeSig = new Rational((int)timeSigs[1][0], (int)timeSigs[1][1]);
			// An anacrusis is assumed when first time sig is smaller than the second and the 
			// metric time of the second time sig equals the first time sig
			// NB When exporting a .sib file with a real anacrusis to MIDI, the anacrusis bar
			// is padded with rests. To get a real anacrusis in a MIDI file, the anacrusis bar
			// must thus be given its own meter
			if (firstTimeSig.isLess(secondTimeSig) && secondMetricTime.equals(firstTimeSig)) {
				start = 0;
			}
		}
		List<Integer[]> mInfo = new ArrayList<Integer[]>();
		
		int numBars;
		for (int i = 0; i < numTimeSigs; i++) {
			long[] curr = timeSigs[i];
			Rational currMeter = new Rational(curr[0], curr[1]);
			Rational currMetricTime = new Rational(curr[3], curr[4]);
			// If there is a next timesig
			if ((i+1) < numTimeSigs) {
				long[] next = timeSigs[i+1];
				Rational nextMetricTime = new Rational(next[3], next[4]);
				numBars = (int) (nextMetricTime.sub(currMetricTime)).div(currMeter).toDouble();
			}
			// If there is no next time sig
			else {
				// Determine the offset of the last note of the piece
				Rational end = Rational.ZERO;
//				for (NotationStaff ns : getPiece().getScore()) {
				for (NotationStaff ns : piece.getScore()) {
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
			mInfo.add(new Integer[]{(int)curr[0], (int)curr[1], start, start + (numBars - 1),
				currMetricTime.getNumer(), currMetricTime.getDenom()});
			start += numBars;
		}		
		return mInfo;
	}


	/**
	 * Creates the keyInfo from the Transcription's Piece.
	 *  
	 * @return A list, each element of which represents a key in the piece and contains:
	 *   <ul>
	 *   <li> as element 0: the key, as a number of sharps (positive) or flats (negative) </li>
	 *   <li> as element 1: the mode, where major = 0 and minor = 1 </li>
	 *   <li> as element 2: the first bar in the key </li>
	 *   <li> as element 3: the last bar in the key </li>
	 *   <li> as element 4: the numerator of the metric time of that first bar </li>
	 *   <li> as element 5: the denominator of the metric time of that first bar </li>
	 *   </ul>
	 */
	// TESTED
	public static List<Integer[]> createKeyInfo(Piece p, List<Integer[]> meterInfo) {
		List<Integer[]> keyInfo = new ArrayList<Integer[]>();
		
		SortedContainer<Marker> keySigs = p.getHarmonyTrack();
		int numKeySigs = keySigs.size();
		for (int i = 0; i < numKeySigs; i++) {
			KeyMarker km = (KeyMarker) keySigs.get(i);
			int key = km.getAlterationNum(); 
			// Reverse KeyMarker.Mode labels (minor = 0; major = 1)
			int mode = Math.abs(km.getMode().getCode() -1);
			Rational mt = km.getMetricTime();
			// It is assumed that key signature changes only occur at the beginning of a bar
			int firstBar = Tablature.getMetricPosition(mt, meterInfo)[0].getNumer();
			int lastBar = -1;
			// If there is a next keysig
			if ((i+1) < numKeySigs) {
				KeyMarker next = (KeyMarker) keySigs.get(i+1);
				Rational mtNext = next.getMetricTime();
				int firstBarNext = Tablature.getMetricPosition(mtNext, meterInfo)[0].getNumer();
				lastBar = firstBarNext - 1;
			}
			else {
				lastBar = meterInfo.get(meterInfo.size() -1)[3];
			}
			keyInfo.add(new Integer[]{key, mode, firstBar, lastBar, mt.getNumer(), mt.getDenom()});
		}
		return keyInfo;
	}


	void setMeterInfo(List<Integer[]> argMeterInfo) {
		meterInfo = argMeterInfo;
	}


	/** 
	 * Finds all unison notes in the Transcription, and if these are not listed in the correct order (i.e., 
	 * with the unison note with the longer duration first)
	 * (1) swaps these Notes in noteSequence;
	 * (2) swaps the corresponding voice labels in voiceLabels.
	 *      
	 * Also sets equalDurationUnisonsInfo, a list the size of the number of notes in the Transcription, containing
	 * for each element:
	 *   a. if the note at that index is not a unison note or if the note at that index is part of a unison
	 *      whose notes are of inequal length: <code>null</code>  
	 *   b. if the note at that index is part of a unison whose notes are of equal length: an Integer[] containing
	 *        as element 0: the voice of the lower unison note (which will be the lower voice; see initialiseNoteSequence())
	 *        as element 1: the voice of the upper unison note (which will be the higher voice; see initialiseNoteSequence())
	 *        as element 2: the index of the complementary (i.e., upper/lower) unison note    
	 *  
	 * If isGroundTruthTranscription is <code>false</code>, i.e., when the method is applied to a predicted 
	 * Transcription, only noteSequence is adapted. 
	 *  
	 * NB: Non-tablature case only.
	 */
	// TESTED
	void handleUnisons(boolean isGroundTruthTranscription) {
		NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();

		List<Integer[]> equalDurUnisonsInfo = new ArrayList<Integer[]>();
		if (isGroundTruthTranscription) {
			// Initialise equalDurUnisonsInfo with all elements set to null
			for (int i = 0; i < noteSeq.size(); i++) {
				equalDurUnisonsInfo.add(null);
			}
		}

		// For every chord
		for (int i = 0; i < transcriptionChords.size(); i++) {
			// If the chord contains a unison
			if (getUnisonInfo(i) != null) {
				Integer[][] unisonChordInfo = getUnisonInfo(i);
				// For each unison in the chord
				for (int j = 0; j < unisonChordInfo.length; j++) {
					// 1. Determine the indices in noteSeq and voiceLab of the lower and upper unison notes
					// a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
					int notesPreceding = 0;
					for (int k = 0; k < i; k++) {
						notesPreceding += transcriptionChords.get(k).size();
					}
					// b. Calculate the indices in the noteSeq
					int indexOfLowerUnisonNote = notesPreceding + unisonChordInfo[j][1];
					int indexOfUpperUnisonNote = notesPreceding + unisonChordInfo[j][2];

					// 2. Determine the durations of the unison notes
					Rational durationLower = noteSeq.getNoteAt(indexOfLowerUnisonNote).getMetricDuration();
					Rational durationUpper = noteSeq.getNoteAt(indexOfUpperUnisonNote).getMetricDuration();
					// a. If the durations are the same: set appropriate elements of equalDurUnisonsInfo 
					if (durationLower.equals(durationUpper)) {
						if (isGroundTruthTranscription) { // EEND
							List<Double> voiceLabelLower = voiceLab.get(indexOfLowerUnisonNote);
							List<Double> voiceLabelUpper = voiceLab.get(indexOfUpperUnisonNote);
							int voiceLower = voiceLabelLower.indexOf(1.0);
							int voiceUpper = voiceLabelUpper.indexOf(1.0);
							equalDurUnisonsInfo.set(indexOfLowerUnisonNote, new Integer[]{voiceLower, voiceUpper, indexOfUpperUnisonNote});
							equalDurUnisonsInfo.set(indexOfUpperUnisonNote, new Integer[]{voiceLower, voiceUpper, indexOfLowerUnisonNote});
						}
					}
					// b. If not, and the lower unison note is not the one with the longer duration: swap
					else if (durationLower.isLess(durationUpper)) {
						noteSeq.swapNotes(indexOfLowerUnisonNote, indexOfUpperUnisonNote);
						if (isGroundTruthTranscription) {
							Collections.swap(voiceLab, indexOfLowerUnisonNote, indexOfUpperUnisonNote);
						}
						// Concat information to adaptations
						adaptations = adaptations.concat("  Unison found in chord " + i + ": notes no. " + unisonChordInfo[j][1] +
							" (pitch " + unisonChordInfo[j][0] + ", duration " + durationLower + ") and " + unisonChordInfo[j][2] 
							+ " (pitch " + unisonChordInfo[j][0] + ", duration " + durationUpper +
								") in that chord swapped in the NoteSequence; list of voice labels adapted accordingly.");
					}  
				}
			}
		}		
		// Reset noteSequence, voiceLabels; set equalDurationUnisonsInfo
		setNoteSequence(noteSeq);
		if (isGroundTruthTranscription) {
			setVoiceLabels(voiceLab);
			setEqualDurationUnisonsInfo(equalDurUnisonsInfo);
		}
	}


	//TESTED (together with getEqualDurationUnisonsInfo())
	void setEqualDurationUnisonsInfo(List<Integer[]> argEqualDurationUnisonsInfo) {
		equalDurationUnisonsInfo = argEqualDurationUnisonsInfo;
	}


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


	/**
	 * Sets <i>basicNoteProperties</i>, a two-dimensional Array in which the basic Note properties are stored.
	 * It contains for each Note in row i the following properties:
	 * in column 0: the Note's pitch (as a MIDInumber)
	 * in column 1-2: the numerator and denominator of the Note's MetricTime (both reduced as much as possible)
	 * in column 3-4: the numerator and denominator of the Note's MetricDuration (both reduced as much as possible)
	 * in column 5: the sequence number of the chord the Note is in
	 * in column 6: the size of the chord the Note is in (as new onsets only, so not including any sustained Notes)
	 * in column 7: the Note's sequence number in the chord, not including any sustained notes. The sequence number
	 *              is based on pitch only; voice crossing are left out of consideration. Where two notes have the 
	 *              same pitch, the one with the longer duration is listed first.
	 *              
	 * NB: Non-tablature case only.
	 */
	// TESTED (together with getBasicNoteProperties())
	void setBasicNoteProperties() {
		NoteSequence noteSeq = getNoteSequence();
		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
		basicNoteProperties = new Integer[noteSeq.size()][8];

		// For each Note in noteSeq
		for (int i = 0; i < noteSeq.size(); i++) {
			Note currentNote = noteSeq.get(i);
			Integer[] bnpCurrent = new Integer[8];

			// 0. Pitch
			bnpCurrent[PITCH] = currentNote.getMidiPitch();
			// 1-2. MetricTime (i.e., the numerator and the denominator of the Rational MetricTime)
			bnpCurrent[ONSET_TIME_NUMER] = currentNote.getMetricTime().getNumer();
			bnpCurrent[ONSET_TIME_DENOM] = currentNote.getMetricTime().getDenom();
			// 3-4. Duration (i.e., the numerator and the denominator of the Rational MetricDuration)
			bnpCurrent[DUR_NUMER] = currentNote.getMetricDuration().getNumer();
			bnpCurrent[DUR_DENOM] = currentNote.getMetricDuration().getDenom();
			// 5. Chord number			
			int chordNum = 0;
			int seqNum = 0;
			// Find the chord n is in; then determine chordNum (and seqNum)
			for (int j = 0; j < transcriptionChords.size(); j++) {
				List<Note> currentChord = transcriptionChords.get(j);
				if (currentChord.contains(currentNote)) {
					chordNum = j;
					seqNum = currentChord.indexOf(currentNote);
					break;
				}
			}
			bnpCurrent[CHORD_SEQ_NUM] = chordNum;
			// 6. The size of the chord the Note is in
			int size = transcriptionChords.get(chordNum).size();
			bnpCurrent[CHORD_SIZE_AS_NUM_ONSETS] = size;
			// 7. Sequence number in chord
			bnpCurrent[NOTE_SEQ_NUM] = seqNum;

			// Add to currentBasicNoteProperties to basicNoteProperties
			basicNoteProperties[i] = bnpCurrent;
		}
	}


	/**
	 * Arranges all Notes in the Transcription in chords. The Transcription is traversed from left to right, 
	 * and the chords themselves are arranged starting with the lowest-voice Note. Rest events are not included
	 * in the returned list.
	 * NB: Is only called in the Transcription creation. On a complete Transcription, getTranscriptionChords()
	 *     must be called. 
	 *  
	 * @return
	 */
	// TESTED (through getTranscriptionChords())
	List<List<Note>> getTranscriptionChordsInternal() {
		List<List<Note>> transcriptionChords = new ArrayList<List<Note>>();
		NoteSequence noteSeq = getNoteSequence();

		List<Note> currentChord = new ArrayList<Note>();
		Note firstNote = noteSeq.getNoteAt(0);
		Rational onsetTimeOfFirstNote = firstNote.getMetricTime();
		currentChord.add(firstNote);
		Rational onsetTimeOfPreviousNote = onsetTimeOfFirstNote;
		// For each Note in noteSeq. The Notes are ordered according to (1) onset time, (2) lowest pitch first (if
		// notes have the same onset time), (3) if the Notes have the same onset time and pitch: a. lowest voice 
		// first (if thy have the same duration); b. longest duration first (if they have different durations
		for (int i = 1; i < noteSeq.size(); i++) {
			Note currentNote = noteSeq.getNoteAt(i);
			Rational onsetTimeOfCurrentNote = currentNote.getMetricTime();
			// If currentNote has the same onset time as previousNote, they belong to the same chord: add 
			// currentNote to currentChord
			if (onsetTimeOfCurrentNote.equals(onsetTimeOfPreviousNote)) {
				currentChord.add(currentNote);
			}
			// If currentNote has a different onset time than previousNote, currentNote is the first note of the 
			// next chord. Add currentChord to transcriptionChords, create a new currentChord, and add currentNote to it  
			else {
				transcriptionChords.add(currentChord);
				currentChord = new ArrayList<Note>();
				currentChord.add(currentNote);
			}
			onsetTimeOfPreviousNote = onsetTimeOfCurrentNote;
		}
		// Add the last chord to transcriptionChords
		transcriptionChords.add(currentChord);
		return transcriptionChords;
	}


	// TESTED (together with getTranscriptionChords())
	void setTranscriptionChordsFinal() {
		transcriptionChordsFinal = getTranscriptionChordsInternal();
	}


	/**
	 * Gets the final version of the transcription chords. This method must only be used when the 
	 * following conditions are satisified:
	 * a) it is called directly on a completed transcription;
	 * b) it is called after setTranscriptionChordsFinal() in createTranscription(). 
	 * Otherwise, getTranscriptionChordsInternal() must be used. 
	 * 
	 * @return
	 */
	// TESTED (together with setTranscriptionChordsFinal()) (for both tablature- and non-tablature case)
	public List<List<Note>> getTranscriptionChords() {
		return transcriptionChordsFinal;
	}


	/**
	 * Sets numberOfNewOnsetsPerChord, a List containing the number of new notes (new note onsets) in each 
	 * Transcription chord.
	 * NB: Non-tablature case only.
	 * 
	 * @return 
	 */
	// TESTED (together with getNumberOfNewNotesPerChord())
	void setNumberOfNewNotesPerChord() { 
//		public List<Integer> getNumberOfNewNotesPerChord() {
		numberOfNewNotesPerChord = new ArrayList<Integer>();

		List<List<Note>> transcriptionChords = getTranscriptionChords(); // conditions satisfied; external version OK
		for (List<Note> l : transcriptionChords) {
			numberOfNewNotesPerChord.add(l.size());
		}
//		return numberOfNewNotesPerChord;
	}


	/**
	 * Gets the number of notes (new onsets) per chord. 
	 * NB: Non-tablature case only.
	 * 
	 * @return
	 */
	// TESTED (together with setNumberOfNewNotesPerChord())
	public List<Integer> getNumberOfNewNotesPerChord() {
		return numberOfNewNotesPerChord;
	}


	/**
	 * Sets chordVoiceLabels, the voice labels grouped per chord.
	 * 
	 * @param tablature Is <code>null</code> in the non-tablature case
	 * @return
	 */
	// TESTED (together with getChordVoiceLabels()) (for both tablature- and non-tablature case)
	void setChordVoiceLabels(Tablature tablature) {
//		public List<List<List<Double>>> getChordVoiceLabels(Tablature tablature) {
//		List<List<List<Double>>> chordVoiceLabels = new ArrayList<List<List<Double>>>();
		chordVoiceLabels = new ArrayList<List<List<Double>>>();

		// Get the most recent voice labels
		List<List<Double>> voiceLabels = getVoiceLabels();

		// a. In the tablature case
		if (tablature != null) {
			// Get the tablature chords 
			List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();

			// Add the voice labels for each chord to chordVoiceLabels
			int lowestNoteIndex = 0;
			for (int j = 0; j < tablatureChords.size(); j++) {
				List<TabSymbol> currentChord = tablatureChords.get(j); 
				int currentChordSize = currentChord.size();
				chordVoiceLabels.add(new ArrayList<List<Double>>(voiceLabels.subList(lowestNoteIndex, 
					lowestNoteIndex + currentChordSize)));
				lowestNoteIndex += currentChordSize;
			}
		}
		// b. In the non-tablature case
		else {
			List<List<Note>> transcriptionChords = getTranscriptionChords(); // conditions satisfied; external version OK

			// Add the voice labels for each chord to chordVoiceLabels
			int lowestNoteIndex = 0;
			for (int j = 0; j < transcriptionChords.size(); j++) {
//			for (int j = 0; j < getNumberOfChords(); j++) {
				List<Note> currentChord = transcriptionChords.get(j); 
				int currentChordSize = currentChord.size();
				chordVoiceLabels.add(new ArrayList<List<Double>>(voiceLabels.subList(lowestNoteIndex, 
					lowestNoteIndex + currentChordSize)));
				lowestNoteIndex += currentChordSize;
			}
		}
//			return chordVoiceLabels;
	}


	// 2. Methods to be called when the Transcription is fully operational
	// a. Methods applying to the tablature case only
	// b. Methods applying to both the tablature and the non-tablature case
	// c. Methods applying to the non-tablature case only
	public Piece getPiece() {
		return piece;
	}


	public String getPieceName() {
		return pieceName; 
	}


	// TESTED (for both tablature- and non-tablature case)
	public NoteSequence getNoteSequence() {
		return noteSequence;
	}


	// TESTED (for both tablature- and non-tablature case)
	public List<List<Double>> getVoiceLabels() {
		return voiceLabels;
	}


	// TESTED (together with setChordVoiceLabels()) (for both tablature- and non-tablature case)
	public List<List<List<Double>>> getChordVoiceLabels() {
		return chordVoiceLabels;
	}


	// TESTED
	public List<List<Double>> getDurationLabels() {
		return durationLabels;
	}


	public List<Integer[]> getMeterInfo() {
		return meterInfo;
	}
	
	
	public List<Integer[]> getKeyInfo() {
		return keyInfo;
	}


	// TESTED (together with setBasicNoteProperties)
	public Integer[][] getBasicNoteProperties() {
		return basicNoteProperties;
	}


	// TESTED (together with setVoicesCoDNotes())
	public List<Integer[]> getVoicesCoDNotes() {
		return voicesCoDNotes;
	}


	//TESTED (together with setEqualDurationUnisonsInfo())
	public List<Integer[]> getEqualDurationUnisonsInfo() {
		return equalDurationUnisonsInfo;
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
	Integer[][] getBasicNotePropertiesChord(int chordIndex) { // TODO not in use

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
	
	
	public void transposeNonTab(int transpositionInterval) {
		transpose(transpositionInterval);
		// Redo the part in createTranscription() from setBasicNoteProperties() on. 
		// setNumberOfNewNotesPerChord() and setChordVoiceLabels() need not be called again after
		// setTranscriptionChordsFinal(), as there are no pitches involved in these methods
		transcriptionChordsFinal = null;
		setBasicNoteProperties();
		setTranscriptionChordsFinal();	
	}


	/**
	 * Gives each note its maximum duration. Given a note n_t and an note n_t+1 in a voice, the duration of
	 * n_t is
	 * (1) if the inter-onset time between n_t and n_t+1 <= the given maxDur: the inter-onset time
	 * (2) if the inter-onset time between n_t and n_t+1 >  the given maxDur: the given maxDur
	 * 
	 * @param p
	 * @param maxDur
	 * @return
	 */
	// TODO test
	public static Piece completeDurations(Piece p, Rational maxDur) {
		NotationSystem ns = p.getScore();
		for (int i = 0; i < ns.size(); i++) {
			NotationVoice nv = ns.get(i).get(0);
			for (int j = 0; j < nv.size(); j++) {
				Note n = nv.get(j).get(0);
				Rational onset = n.getMetricTime();
				if (j+1 < nv.size()) {
					Note nextN = nv.get(j+1).get(0);
					Rational ioi = nextN.getMetricTime().sub(onset);
					Rational newDur = ioi;
					if (ioi.isGreater(maxDur)) {
						newDur = maxDur;
					}
					n.setScoreNote(new ScoreNote(new ScorePitch(n.getMidiPitch()), onset, newDur));
				}
			}
		}
		return p;
	}


	// TESTED
	public Rational[][] getTimePitchMatrix() {
		Integer[][] bnp = getBasicNoteProperties();
		Rational[][] tpm = new Rational[bnp.length][3];
		for (int i = 0; i < bnp.length; i++) {
			int pitchCurr = bnp[i][0];
			Rational onsetCurr = new Rational(bnp[i][1], bnp[i][2]);
			Rational offsetCurr = onsetCurr.add(new Rational(bnp[i][3], bnp[i][4]));
			tpm[i] = new Rational[]{onsetCurr, offsetCurr, new Rational(pitchCurr, 1)};
		}
		return tpm;
	}
	
	/**
	 * Gets, for each note, the number of notes that are sounding at that time (including 
	 * sustained notes).
	 * @return
	 */
	// TESTED
	public List<Integer> getNoteDensity() {
		List<Integer> activeNotes = new ArrayList<Integer>();
		
		Integer bnp[][] = getBasicNoteProperties(); 
//		FeatureGenerator fg = new FeatureGenerator();
		Rational[][] tpm = getTimePitchMatrix();
		for (int i = 0; i < tpm.length; i++) {
//			System.out.println(i);
			Rational onsetCurr = tpm[i][0];
			int active = 1;
			// Previous notes
			int numSusNotes = getIndicesOfSustainedPreviousNotes(null, null, bnp, i).size();
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
			for (int j = i+1; j < bnp.length; j++) {
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
		List<Integer> noteDensities = getNoteDensity();
		
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
//		FeatureGenerator fg = new FeatureGenerator();
		List<Integer> noteDensities = getNoteDensity();
		
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
		res.add(Arrays.asList(dict.get(optimalConfigs)));
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
		List<Integer> noteDensities = getNoteDensity();
		
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
	// TESTED
	public List<List<Integer>> getImitativeVoiceEntries(int highestNumVoices, int n) {
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		List<Integer> configs = new ArrayList<Integer>();
		final int pitch = 0;
		final int durNum = 1;
		final int durDen = 2;
		final int isHMN = 3;

		Integer[][] bnp = getBasicNoteProperties();
//		FeatureGenerator fg = new FeatureGenerator();

		List<Integer> lowestNoteIndicesFirstChords = new ArrayList<Integer>();
		lowestNoteIndicesFirstChords.add(0);
		List<List<Integer[]>> pitchesFirstChords = new ArrayList<List<Integer[]>>();
		List<Integer[]> firstPitch = new ArrayList<Integer[]>();
		firstPitch.add(new Integer[]{bnp[0][PITCH], 0});
		pitchesFirstChords.add(firstPitch);

		// Determine the rhythmic head motif (first n notes of fugue theme)
		List<Rational> headMotif = new ArrayList<Rational>();
		Rational granularity = new Rational(1, 32);
		for (int i = 0; i < n; i++) {
			Rational curr = new Rational(bnp[i][DUR_NUMER], bnp[i][DUR_DENOM]);
			headMotif.add(quantiseDuration(curr, granularity));
		}

		// Determine the inter-onset intervals, exact pitch movement, and general pitch 
		// movement of the head motif
		List<Rational> ioiHeadMotif = new ArrayList<Rational>();
		List<Integer> pitchMvmtHeadMotif = new ArrayList<Integer>();
		List<Double> mvmtHeadMotif = new ArrayList<Double>();
		for (int i = 0; i < n-1; i++) {
			ioiHeadMotif.add(
				new Rational(bnp[i+1][ONSET_TIME_NUMER], bnp[i+1][ONSET_TIME_DENOM]).sub(
				new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM])));
			int pitchMvmt = bnp[i+1][PITCH] - bnp[i][PITCH];
			pitchMvmtHeadMotif.add(pitchMvmt);
			mvmtHeadMotif.add(Math.signum((double)pitchMvmt));
		}

		// Find the next density increase and determine the position of the newly
		// entering voice
		List<Integer> noteDensities = getNoteDensity();
		int density = noteDensities.get(0);
		for (int i = 0; i < noteDensities.size(); i++) {
			if (noteDensities.get(i) > density) {
				System.out.println("density increase");
				System.out.println("bar = " + ToolBox.getMetricPositionAsString(
					getAllMetricPositions().get(i)));
				System.out.println("new density = " + noteDensities.get(i));
				density = noteDensities.get(i);
				int config = -1;

				// Check whether the chord at i contains the first HMN
				boolean firstHMNInFirstChord = false;
				for (int j = i; j < i + bnp[i][CHORD_SIZE_AS_NUM_ONSETS]; j++) {
					Rational curr = 
						new Rational(bnp[j][DUR_NUMER], bnp[j][DUR_DENOM]);
					curr = quantiseDuration(curr, granularity);
					if (curr.equals(headMotif.get(0))) {
						firstHMNInFirstChord = true;
						break;
					}
				}
				
				// If not: find the i that goes with the first sequence of chords to the left 
				// that contain the HMNs. Stop if no motif candidate has been found after the 
				// maximum time window has been covered, or when there are no more notes left
				if (!firstHMNInFirstChord) {
					Rational onsetOfInitialI = 
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
							Rational curr = new Rational(bnp[j][DUR_NUMER], bnp[j][DUR_DENOM]);
							curr = quantiseDuration(curr, granularity);
							if (curr.equals(headMotif.get(0))) {
								int chordInd = bnp[j][CHORD_SEQ_NUM];
								onsetOfNewI = new Rational(bnp[j][ONSET_TIME_NUMER],
									bnp[j][ONSET_TIME_DENOM]);
								// Find the index of the lowest note in the chord at chordInd
								for (int k = j; k >=0; k--) {
									if (bnp[k][CHORD_SEQ_NUM] == chordInd-1) {
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
						Rational onsetOfChordAtNewI = new Rational(bnp[newI][ONSET_TIME_NUMER], 
							bnp[newI][ONSET_TIME_DENOM]);
						Rational nextOns = onsetOfChordAtNewI.add(ioiHeadMotif.get(ind - 1));
						System.out.println("nextOns = " + nextOns);
						for (int j = newI; j < bnp.length; j++) {
							Rational currOns = new Rational(bnp[j][ONSET_TIME_NUMER], 
								bnp[j][ONSET_TIME_DENOM]);
							if (currOns.equals(nextOns)) {
								Rational currDur = new Rational(bnp[j][DUR_NUMER], 
									bnp[j][DUR_DENOM]);
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
				onsetsOfHMNChords.add(new Rational(bnp[i][ONSET_TIME_NUMER], 
					bnp[i][ONSET_TIME_DENOM]));
				for (int j = 0; j < ioiHeadMotif.size(); j++) { 
					Rational lastAdded = onsetsOfHMNChords.get(j);
					Rational toAdd = lastAdded.add(ioiHeadMotif.get(j));
					onsetsOfHMNChords.add(toAdd);
				}

				// b. Determine the indices of the lowest chord note at those onsets
				List<Integer> indOfMotifNotesChords = new ArrayList<Integer>();
				for (int j = i; j < bnp.length; j++) {
					Rational currOnset = 
						new Rational(bnp[j][ONSET_TIME_NUMER], bnp[j][ONSET_TIME_DENOM]);	
					if (onsetsOfHMNChords.contains(currOnset)) {
						indOfMotifNotesChords.add(j);
						j += bnp[j][CHORD_SIZE_AS_NUM_ONSETS] - 1;
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
					List<List<Integer>> noteInfo = getChordInfo(ind, null, bnp);
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
//				System.out.println("onsetsOfHMNChords = " + onsetsOfHMNChords);
//				System.out.println("indOfMotifNotesChords = " + indOfMotifNotesChords);
//				System.out.println("SKELETON");
//				for (List<List<Integer>> l : skeleton) {
//					System.out.println(l);
//				}

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
							int sizePrev = bnp[i-1][CHORD_SIZE_AS_NUM_ONSETS];
							int lowestIndPrev = i - sizePrev;
							// Add pitches of all chord notes
							for (int k = lowestIndPrev; k < lowestIndPrev + sizePrev; k++) {
								pitchesInPrev.add(bnp[k][PITCH]);
							}
							// Add pitches of any sustained previous notes
							for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, lowestIndPrev)) {
								pitchesInPrev.add(bnp[ind][PITCH]);
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
//					System.out.println("SKELETON PATCHED");
//					for (List<List<Integer>> l : skeleton) {
//						System.out.println(l);
//					}
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
						getNonUnisonNeighbourChord(lowestIndCurr, null, bnp, -1);
					List<List<Integer>> nextChord = 
						getNonUnisonNeighbourChord(lowestIndCurr, null, bnp, 1);

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
//				System.out.println("SKELETON PATCHED AND UNISONS FIXED");
//				for (List<List<Integer>> l : skeleton) {
//					System.out.println(l);
//				}

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

				int numMotifCand = motifCandidates.size() - 
					Collections.frequency(motifCandidates, null);

				// 4. Determine the configuration 
				// The configuration equals the index (including sustained notes) of the 
				// HMN in the first HMN chord. Possibilities:
				// (0)   (1)   (2)   (3) 
				// x x     x  
				//   x   x x
				//   
				// x x   x x     x   
				// x x     x   x x
				//   x   x x   x x
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
					System.out.println("multiple motif candidates for " + pieceName);
					// List all m left chords (i.e., those with the previous density), 
					// each as a list of pitches, and make average chord 
					List<List<Integer>> leftChords = new ArrayList<List<Integer>>();	
					List<Double> avgLeftChord = new ArrayList<Double>();
					for (int j = 0; j < density; j++) {
						avgLeftChord.add(null);
					}
					// Given the number of left chords to consider, find the lowest note
					// index of the leftmost chord
					int chordInd = bnp[i][CHORD_SEQ_NUM];
					int m = 1; // TODO make method argument?
					int leftChordIndex = chordInd - m;
					int leftInd = i;
					int rightInd = i;
					while (chordInd > leftChordIndex) {
						leftInd = rightInd - bnp[rightInd-1][CHORD_SIZE_AS_NUM_ONSETS];
						rightInd = leftInd;
						chordInd = bnp[leftInd][CHORD_SEQ_NUM];
					}
					// List the left chords and prepare avgLeftChord
					for (int j = leftInd; j < i; j++) {
						int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
						List<Integer> pitchesInChord = new ArrayList<Integer>();
						// Add pitches of all chord notes
						for (int k = j; k < j + chordSize; k++) {
							pitchesInChord.add(bnp[k][PITCH]);
						}
						// Add pitches of any sustained previous notes
						for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
							pitchesInChord.add(bnp[ind][PITCH]);
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

					// List the right chord
					List<Integer> rightChord = new ArrayList<Integer>();
					for (List<Integer> l : skeleton.get(0)) {
						rightChord.add(l.get(pitch));
					}
					Collections.reverse(rightChord);

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
		// 2vv: configs.size() == 1: one -1 returns null (none)
		// 3vv: configs.size() == 2: two -1s returns null (881)
		// 4vv: configs.size() == 3: two or three -1s returns null (none)
		// 5vv: configs.size() == 4: three or four -1s returns null (849_1)
		int minusOnes = Collections.frequency(configs, -1); 
		if ((ToolBox.sumListInteger(configs) == -configs.size()) ||
			((double)minusOnes/configs.size() > 0.5)) {
			System.out.println(configs);
			System.out.println("==========>>> null: no or not enough motifs found for " + getPieceName());
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
				int chordSize = bnp[currInd][CHORD_SIZE_AS_NUM_ONSETS];
				for (int j = currInd; j < currInd + chordSize; j++) {
					currIndices.add(j);
				}
				indices.addAll(currIndices);

				// List voices
				List<Integer> currVoices = new ArrayList<Integer>();
				int currVoice = voiceEntries.get(i).intValue();
				Collections.sort(voicesAlreadyAdded);
				Collections.reverse(voicesAlreadyAdded);
				if (currInd == 0) {
					currVoices.add(currVoice);
				}
				else {
					// Determine the previous chord
					int prevLowestInd = currInd-(bnp[currInd-1][CHORD_SIZE_AS_NUM_ONSETS]);
					List<Integer> prevChord = new ArrayList<Integer>();
					for (int k = prevLowestInd; k < currInd; k++) {
						prevChord.add(bnp[k][PITCH]);
					}
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, prevLowestInd)) {
						prevChord.add(bnp[ind][PITCH]);
					}
					Collections.sort(prevChord);

					// Find any sustained notes in the first chord of the current 
					// density change 
					List<Integer[]> currFirst = pitchesFirstChords.get(i);
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
					currVoices.addAll(availableVoices);
				}
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


	// Return <code>null</code> if one or more voices are missing from the returned list of voices. 
	List<List<Integer>> getNonImitativeVoiceEntries(int numVoices, int n) {
		List<Integer> allConfigs = new ArrayList<Integer>();
		List<Integer> allIndices = new ArrayList<Integer>();
		List<Integer> allVoices = new ArrayList<Integer>();
		
		Integer[][] bnp = getBasicNoteProperties();
		
		// Determine the densities and the indices of the lowest note of the first
		// chord with the new density
		List<Integer> densities = new ArrayList<Integer>();
		List<Integer> indices = new ArrayList<Integer>();
//		List<List<Integer>> firstChordIndices = new ArrayList<List<Integer>>();
		List<Integer> noteDensities = getNoteDensity();
		int prevDensity = 0;
		for (int i = 0; i < noteDensities.size(); i++) {
			int d = noteDensities.get(i);
			if (d > prevDensity) {
				prevDensity = d;
				densities.add(d);
				indices.add(i);
				List<Integer> currFirstChordInd = new ArrayList<Integer>();
				for (int j = i; j < i + bnp[i][CHORD_SIZE_AS_NUM_ONSETS]; j++) {
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
				rightDensIncrInd = bnp.length - bnp[bnp.length-1][CHORD_SIZE_AS_NUM_ONSETS];
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
					int chordSize = bnp[j][CHORD_SIZE_AS_NUM_ONSETS];
//					List<List<Integer>> pitchesAndSpnInChord = new ArrayList<List<Integer>>();
					List<Integer> pitchesInChord = new ArrayList<Integer>();
					// Add pitches and sustained previous note-ness of all notes in the chord
					for (int k = j; k < j + chordSize; k++) {
//						pitchesAndSpnInChord.add(Arrays.asList(new Integer[]{bnp[k][PITCH], 0}));
						pitchesInChord.add(bnp[k][PITCH]);
					}
					// Add pitches and sustained previous note-ness of any sustained previous notes
					for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, j)) {
//						pitchesAndSpnInChord.add(Arrays.asList(new Integer[]{bnp[ind][PITCH], 1}));
						pitchesInChord.add(bnp[ind][PITCH]);
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

			for (int j = currDensIncrInd; j < (currDensIncrInd + bnp[currDensIncrInd][CHORD_SIZE_AS_NUM_ONSETS]); j++) {
				System.out.println("j ==== " + j);
				spn.add(Arrays.asList(new Integer[]{bnp[j][PITCH], 0}));
			}
			// Add pitches and sustained previous note-ness of any sustained previous notes
			for (int ind : getIndicesOfSustainedPreviousNotes(null, null, bnp, currDensIncrInd)) {
				spn.add(Arrays.asList(new Integer[]{bnp[ind][PITCH], 1}));
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
			// right chords but not in the left, and remove them from rightVoices for the
			// next iteration of the outer for-loop
//			List<Integer> newVoices = new ArrayList<Integer>();
			// If the left chord contains one note or one placeholder (1-2, 1-3, 1-4, 1-5, 
//			// 2-3, 3-4, 4-5)
			if (leftDensity == 1 || leftDensity == (rightDensity - 1)) {
//			if (numConfigs == rightDensity) {
				System.out.println("BLUUARRRRGGHHH");
				System.out.println("rightVoices = " + rightVoices);
				System.out.println("bestConfig = " + bestConfig);
				if (leftDensity == 1) {
					// The voice at index bestConfig in rightVoices is the already active 
					// voice, so the voices at all other indices are newly entering voices
					List<Integer> toRemove = new ArrayList<Integer>(); 
					for (int j = 0; j < rightVoices.size(); j++) {
						System.out.println(j);
						if (j != bestConfig) {
							toRemove.add(rightVoices.get(j));
//							rightVoices.remove(j);
						}
					}
					System.out.println("toRemove = " + toRemove);
					for (int p : toRemove) {
						rightVoices.remove((Integer) p);
					}	
				}
				else if (leftDensity == (rightDensity - 1)) {
//				else if (numNull == 1) {
					// The voice at index (rightDensity-1)-bestConfig in rightVoices is the
					// newly entering voice
					rightVoices.remove((rightDensity-1)-bestConfig);
				}
			}
			// If the left chord contains more than one note and the right chord contains
			// at least two more notes than the left chord (2-4, 2-5, 3-5)
			// TODO Fix! (does not happen currently)
			else {
				System.out.println("AAAAAAAAAaaaaaaaaaahhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
				System.exit(0);
			}
			System.out.println("rightVoices = " + rightVoices);
		}	
		// Add remaining rightVoices and reverse allVoices
		allVoices.addAll(rightVoices);
		Collections.reverse(allVoices);
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


	public List<List<Integer>> determineVoiceEntriesHIGHLEVEL(Integer[][] bnp, int numVoices, int n) {
		List<Integer> noteDensities = getNoteDensity();
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

			// If the voices enter successively: determine if the piece is imitative
			if (densities.size() == numVoices) {
				// Check whether there are enough notes of density 1 to contain a motif of 
				// n notes
				boolean enoughNotes = true;
				for (int i = 0; i < n; i++) {
					if (getNoteDensity().get(i) > 1) {
						enoughNotes = false;
						break;
					}
				}
				// If so: check whether a motif is found (i.e., whether configurations does
				// not contain only -1s)
				if (enoughNotes) {
					List<List<Integer>> ve = getImitativeVoiceEntries(numVoices, n); 
//					List<Integer> configurations = ve.get(0);
//					if ((ToolBox.sumListInteger(configurations) != -configurations.size())) {
//						return ve;
//					}
					if (ve != null) {
						System.out.println("IMITATIVE MANNE.");
						return ve;
					}
					else {
//						System.out.println("IMITATIVE FAILED AT " + pieceName);
						System.out.println("NON-IMITATIVE MANNE.");
						return getNonImitativeVoiceEntries(numVoices, n);
					}
				}
				else {
					System.out.println("NON-IMITATIVE MANNE.");
					return getNonImitativeVoiceEntries(numVoices, n);
				}
			}
			// If the voices do not enter successively: the piece is non-imitative
			else {
				System.out.println("NON-IMITATIVE MANNE.");
				return getNonImitativeVoiceEntries(numVoices, n);
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
	 * Quantises the duration to the next multiple of the given granularity fraction.
	 * 
	 * @param curr
	 * @param multiplier
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
	 * Finds the first neighbour chord that does not contain a unison. 
	 * 
	 * @param lowestNoteIndex The index of the lowest note of the current chord
	 * @param btp
	 * @param bnp
	 * @param direction +1 if looking for the right (next) neighbour chord; -1 
	 *        if looking for the left (previous) one
	 *  
	 * @return
	 */
	// TESTED
	List<List<Integer>> getNonUnisonNeighbourChord(int lowestIndCurr, Integer[][] btp,
		Integer[][] bnp, int direction) {

		if (direction == -1) {
			// Find the first previous chord without a unison
			List<List<Integer>> prevChord = null;
			// Only if the chord at lowestIndCurr is not the first chord  
			if (lowestIndCurr != 0) {
				int lowestIndPrev = 
					lowestIndCurr - bnp[lowestIndCurr-1][CHORD_SIZE_AS_NUM_ONSETS];
				for (int i = lowestIndPrev; i >= 0 ; i--) {
					prevChord = getChordInfo(lowestIndPrev, null, bnp);
					boolean unisonFound = false;
					// Compare pitches
					outer: for (int j = 0; j < prevChord.size(); j++) {
						for (int k = 0; k < prevChord.size(); k++) {
							if (k != j) {
								// Unison found? Determine chord before previous and break 
								if (prevChord.get(k).get(0) == prevChord.get(j).get(0)) {
									unisonFound = true;
									lowestIndPrev = lowestIndPrev - 
										bnp[lowestIndPrev-1][CHORD_SIZE_AS_NUM_ONSETS];
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
			if (lowestIndCurr + bnp[lowestIndCurr][CHORD_SIZE_AS_NUM_ONSETS] != bnp.length) {
				int lowestIndNext = 
					lowestIndCurr + bnp[lowestIndCurr][CHORD_SIZE_AS_NUM_ONSETS];
				for (int i = lowestIndNext; i < bnp.length; i++) {
					nextChord = getChordInfo(lowestIndNext, null, bnp);
					boolean unisonFound = false;
					// Compare pitches
					outer: for (int j = 0; j < nextChord.size(); j++) {
						for (int k = 0; k < nextChord.size(); k++) {
							if (k != j) {
								// Unison found? Determine chord after next and break 
								if (nextChord.get(k).get(0) == nextChord.get(j).get(0)) {
									unisonFound = true;
									lowestIndNext = 
										lowestIndNext + bnp[lowestIndNext][CHORD_SIZE_AS_NUM_ONSETS];
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
	// TESTED
	List<List<Integer>> getChordInfo(int lowestNoteIndex, Integer[][] btp, Integer[][] bnp) {
//		int lowestNoteIndex = indOfMotifNotesChords.get(j);
		List<List<Integer>> noteInfo = new ArrayList<List<Integer>>(); 
		
//		FeatureGenerator fg = new FeatureGenerator();
//		Rational currMotifNoteDur = headMotif.get(j);
		int chordSize = bnp[lowestNoteIndex][CHORD_SIZE_AS_NUM_ONSETS];
		// Add pitches of all notes in the chord
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
//			int pitch = bnp[i][PITCH];
//			int isDur = 0;
//			Rational currNoteDur = new Rational(bnp[i][DURATION_NUMER], bnp[i][DURATION_DENOM]);
//			if (currNoteDur.equals(currMotifNoteDur)) {
//				isDur = 1;
//			}
//			noteInfo.add(Arrays.asList(new Integer[]{bnp[i][PITCH],	bnp[i][DURATION_NUMER], 
//				bnp[i][DURATION_DENOM]}));
			List<Integer> curr = new ArrayList<Integer>();
			curr.add(bnp[i][PITCH]); 
			curr.add(bnp[i][DUR_NUMER]);
			curr.add(bnp[i][DUR_DENOM]);
			noteInfo.add(curr);
		}
		// Add pitches of any sustained previous notes
		for (int indSus : getIndicesOfSustainedPreviousNotes(null, null, bnp, lowestNoteIndex)) {
//			noteInfo.add(Arrays.asList(new Integer[]{bnp[indSus][PITCH], null, null}));
			List<Integer> curr = new ArrayList<Integer>();
			curr.add(bnp[indSus][PITCH]); 
			curr.add(null);
			curr.add(null);
			noteInfo.add(curr);
		}
		noteInfo = ToolBox.bubbleSort(noteInfo, 0);
		return noteInfo;
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
		if (Math.abs(optionA) <= Math.abs(optionB)) {
			return optionA;
		}
		else {
			return optionB;
		}
	}


	/**
	 * Transposes the given transcription by the given interval (in semitones).
	 * 
	 * NB: Tablature case only.
	 * 
	 * @param transposition
	 */
	// TESTED
	void transpose(int transposition) {
		// 1. Transpose all the notes in noteSequence and reset noteSequence
		NoteSequence noteSeq = getNoteSequence();
		for (int i = 0; i < noteSeq.size(); i++) {
			Note originalNote = noteSeq.getNoteAt(i);
			Note transposedNote = Transcription.createNote(originalNote.getMidiPitch() + transposition,
				originalNote.getMetricTime(), originalNote.getMetricDuration());
			noteSeq.replaceNoteAt(i, transposedNote);
		}
		setNoteSequence(noteSeq);
		
		// 2. Transpose piece
		NotationSystem system = getPiece().getScore(); 
		for (int i = 0; i < system.size(); i++) {
			NotationStaff staff = system.get(i);
			NotationVoice voice = staff.get(0);  
			for (int j = 0; j < voice.size(); j++) {
				NotationChord notationChord = voice.get(j);
				for (int k = 0; k < notationChord.size(); k++) {
					Note originalNote = notationChord.get(k);
					Note transposedNote = Transcription.createNote(originalNote.getMidiPitch() + transposition,
						originalNote.getMetricTime(), originalNote.getMetricDuration());
					notationChord.remove(originalNote);
					notationChord.add(transposedNote);   	    
				}
			}
		}

		// 3. Transpose HarmonyTrack
		SortedContainer<Marker> keySigs = getPiece().getHarmonyTrack();
		for (int i = 0; i < keySigs.size(); i++) {
			KeyMarker km = (KeyMarker) keySigs.get(i);
			km.setAlterationNum(transposeNumAccidentals(transposition, km.getAlterationNum()));
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
	 * Lists the individual onset times of the notes in the Transcription, in ascending order.
	 * 
	 * @return
	 */
	// TESTED (for both the tablature and the non-tablature case)
	public List<Rational> getAllOnsetTimes() {
		List<Rational> onsetTimes = new ArrayList<Rational>();

		NotationSystem notationSystem = piece.getScore();

		// For every voice
		for (int i = 0; i < notationSystem.size(); i++) {
			NotationStaff staff = notationSystem.get(i);
			NotationVoice currentVoice = staff.get(0);
			for (NotationChord notationChord : currentVoice) {
				// Each NotationChord contains a list of Notes; in practice, this list will only be one Note
				for (Note n : notationChord) {
					Rational currentOnsetTime = n.getMetricTime();
					if (!onsetTimes.contains(currentOnsetTime)) {
						onsetTimes.add(currentOnsetTime);
					}
				}
			}
		}
		Collections.sort(onsetTimes);
		return onsetTimes;
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

	/**
	 * NB: Non-tablature case only.
	 * 
	 * @return
	 */
	public int getNumberOfNotes() {
		return basicNoteProperties.length;  
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

		List<List<Note>> transcriptionChords = getTranscriptionChords(); // conditions satisfied; external version OK
		if (isBwd) {
//		if (procMode == ProcessingMode.BWD) {
			Collections.reverse(transcriptionChords);
		}

		int startIndex = 0;
		// For each chord
		int numChords = transcriptionChords.size();
		for (int i = 0; i < numChords; i++) {
			List<Integer> indicesCurrChord = new ArrayList<Integer>();
			int endIndex = startIndex + transcriptionChords.get(i).size();
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
//		FeatureGenerator featureGenerator = new FeatureGenerator();
//		int indexOfFinalChord = getTranscriptionChords().size() - 1;
		int indexOfFinalChord = basicNoteProperties[basicNoteProperties.length - 1][CHORD_SEQ_NUM];
//		System.out.println(indexOfFinalChord); // DBLCHK
//		System.out.println(indexOfFinalChord2);
		int largestChord = 0;
		int lowestNoteIndex = 0;
		for (int i = lowestNoteIndex; i < basicNoteProperties.length; i++) {
			Integer[] currentBasicNoteProperties = basicNoteProperties[lowestNoteIndex];
			int newOnsetsOnly = currentBasicNoteProperties[CHORD_SIZE_AS_NUM_ONSETS];
			int numSustainedNotes = 
				getIndicesOfSustainedPreviousNotes(null, null, basicNoteProperties, lowestNoteIndex).size();
			int sizeOfCurrentChord = newOnsetsOnly + numSustainedNotes;
			if (sizeOfCurrentChord > largestChord) {
				largestChord = sizeOfCurrentChord;
//				if (largestChord > 3) {
//					System.out.println(i);
//					System.out.println("largest chord = " + largestChord);
//					System.out.println(Arrays.toString(currentBasicNoteProperties));
//				}
			}
			// If the last chord is reached: break
			if (currentBasicNoteProperties[CHORD_SEQ_NUM] == indexOfFinalChord) {
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
		for (int i = 0; i < Transcription.MAXIMUM_NUMBER_OF_VOICES; i++) {
			int currentVoice = i;
			List<Integer> notesInCurrentVoice = new ArrayList<Integer>();
			// For each note: check whether the note at index j belongs to currentVoice. If so, add it to notesInCurrentVoice 
			for (int j = 0; j < voiceLabels.size(); j++) {
				List<Double> actualVoiceLabel = voiceLabels.get(j);
				List<Integer> actualVoices = DataConverter.convertIntoListOfVoices(actualVoiceLabel);
				if (actualVoices.contains(currentVoice)) {
					notesInCurrentVoice.add(j);
				}
			}
			notesPerVoice.add(notesInCurrentVoice);
		}		
		return notesPerVoice;
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
	public List<Integer> getPitchesInChord(int chordIndex) {
		List<Integer> pitchesInChord = new ArrayList<Integer>();

		List<Note> transcriptionChord = null;
		// Use internal version of getTranscriptionChords() when the method is called in Transcription creation
		if (transcriptionChordsFinal == null) {
			transcriptionChord = getTranscriptionChordsInternal().get(chordIndex);
		}
		// Otherwise use external version
		else {
			transcriptionChord = getTranscriptionChords().get(chordIndex);
		}

		for (Note n : transcriptionChord) {
			pitchesInChord.add(n.getMidiPitch());
		}
		return pitchesInChord;
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
		int chordSize = bnp[lowestNoteIndex][Transcription.CHORD_SIZE_AS_NUM_ONSETS];
		for (int i = lowestNoteIndex; i < lowestNoteIndex + chordSize; i++) {
			Integer[] currentBasicNoteProperties = bnp[i];
			int currentPitch = currentBasicNoteProperties[Transcription.PITCH];
			pitchesInChord.add(currentPitch);
		}
		return pitchesInChord;
	}


//  /**
//   * Returns the number of CoDs the chord at the given index contains.
//   * 
//   * NB: Tablature case only; must be called before getCoDInfo().
//   * 
//   * @param tablatureChords
//   * @param chordIndex
//   * @return 
//   */
//  // TESTED
//  int getNumberOfCoDsInChord(List<List<TabSymbol>> tablatureChords, int chordIndex) {
//  	List<List<Note>> transcriptionChords = getTranscriptionChords();
//  	int numberOfCoDsInChord = 0;
//  	if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 1)) {
//			numberOfCoDsInChord = 1;
//		}
//		else if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 2)) {
//    	numberOfCoDsInChord = 2;
//		}
//		else if (transcriptionChords.get(chordIndex).size() == (tablatureChords.get(chordIndex).size() + 3)) {
//    	numberOfCoDsInChord = 3;
//		}
//  	
//  	return transcriptionChords.get(chordIndex).size() - tablatureChords.get(chordIndex).size();
////  	return numberOfCoDsInChord;
//  }


	/**
	 * Gets information on the concept-of-duality-Note(s) in the chord at the given index in the given list. A
	 * CoD occurs when a single Tablature note is shared by two Transcription Notes. Returns an Integer[][]
	 * in which each row represents a CoD-pair (starting from below in the chords), and each column
	 * at index (0) the pitch (as a MIDInumber) of both CoDnotes;
	 * at index (1) the sequence number in the chord of the lower CoDnote;
	 * at index (2) the sequence number in the chord of the upper CoDnote. 
	 * If the chord does not contain (a) CoD(s), <code>null</code> is returned.  
	 *
	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor a course crossing.
	 * NB2: Tablature case only; must be called before handleCoDNotes(). 
	 *
	 * @param tablatureChords 
	 * @param chordIndex 
	 * @return    
	 */
	// TESTED
	public Integer[][] getCoDInfo(List<List<TabSymbol>> tablatureChords, int chordIndex) {
		Integer[][] coDInfo = null;

		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
		// Determine the number of CoDs in the chord
		int numCoDs = transcriptionChords.get(chordIndex).size() - tablatureChords.get(chordIndex).size();

		// If chord contains (a) CoD(s)
		if (numCoDs > 0) { 
//		if (getNumberOfCoDsInChord(tablatureChords, chordIndex) > 0) {
			coDInfo = new Integer[numCoDs][3];
//			coDInfo = new Integer[getNumberOfCoDsInChord(tablatureChords, chordIndex)][3];

//			List<Note> chord = getTranscriptionChordsInternal().get(chordIndex);
			List<Note> chord = transcriptionChords.get(chordIndex);

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
				// Search the remainder of pitchesInChord for a note with the same pitch (the upper CoDnote)
				for (int j = i + 1; j < pitchesInChord.size(); j++) {
					// Same pitch found? upper CoDnote found; fill the currentRowInCoDInfo-th row of coDInfo, increase
					// currentRowInCoDInfo, break from inner for, and continue with the next iteration of the outer
					// for (the next pitch)
					if (pitchesInChord.get(j) == currentPitch) {
						coDInfo[currentRowInCoDInfo][0] = currentPitch;
						coDInfo[currentRowInCoDInfo][1] = i;
						coDInfo[currentRowInCoDInfo][2] = j;
						currentRowInCoDInfo++;
						break; 
					}
				} 
			}
		}
		return coDInfo;
	}


	public static void setReversePiece(boolean argReverse) {
		reversePiece = argReverse;
	}


//  /**
//   * Determines the number of unisons in the chord at the given index. A unison occurs when two different notes
//   * in the same chord have the same pitch.  
//   * 
//   * NB: Non-tablature case only; must be called before getUnisonInfo().
//   * 
//   * @param chordIndex
//   * @return
//   */
//  // TESTED
//  int getNumberOfUnisonsInChord(int chordIndex) {
//  	int numberOfUnisons = 0;
//  	
////  	List<List<Note>> transcriptionChords = getTranscriptionChords();
//  	List<Note> transcriptionChord = getTranscriptionChords().get(chordIndex);
//    
//  	// Only relevant if transcriptionChord contains multiple notes
//    if (transcriptionChord.size() > 1) {
//      // List all the unique pitches in tablatureChord
//      List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
//      List<Integer> uniquePitchesInChord = new ArrayList<Integer>();
//      for (int pitch : pitchesInChord) {
//      	if (!uniquePitchesInChord.contains(pitch)) {
//      		uniquePitchesInChord.add(pitch);
//      	}
//      }
////      // Compare the sizes of pitchesInChord and uniquePitchesInChord; if they are not the same the chord
////      // contains (a) unison(s)
////      if (pitchesInChord.size() == (uniquePitchesInChord.size() + 1)) {
////      	numberOfUnisons = 1;
////      }
////      if (pitchesInChord.size() == (uniquePitchesInChord.size() + 2)) {
////      	numberOfUnisons = 2;
////      }
////      if (pitchesInChord.size() == (uniquePitchesInChord.size() + 3)) {
////      	numberOfUnisons = 3;
////      }
//      numberOfUnisons = pitchesInChord.size() - uniquePitchesInChord.size();
//    }
//  	return numberOfUnisons;
//  }


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
	public Integer[][] getUnisonInfo(int chordIndex) {
		Integer[][] unisonInfo = null;

		// Determine the number of unisons in the chord  
		List<Integer> pitchesInChord = getPitchesInChord(chordIndex);
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
	 * Sets <i>noteProperties</i>, a List containing the Note properties for each Note: (0) pitch; 
	 * (1) duration; (2) size of the chord the Note is in; (3) whether the Note's duration is shorter than 
	 * an 8th note. 
	 * 
	 */
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


	/**
	 * Gets  <i>noteProperties</i>.
	 * 
	 * @return
	 */
//  public List<List<Double>> getNoteProperties() {
//  	return noteProperties;
//  }


	/**
	 * Returns a list of the same size of the event containing the number of occurrences of each pitch within
	 * the event. E.g., an event with the pitches G-G-b-d-d will yield [2, 2, 1, 2, 2]. 
	 *
	 * @param transcriptionChords
	 * @param chordIndex
	 * @return
	 */
	public List<Integer> getOccurrencesOfPitchesInChord(int chordIndex) {
		List<Note> chord = getTranscriptionChordsInternal().get(chordIndex);
		// List the pitches of every Note in chord
		List<Integer> pitchesInChord = new ArrayList<Integer>();
		for (int i = 0; i < chord.size(); i++) {
			int pitch = chord.get(i).getMidiPitch();
			pitchesInChord.add(pitch);
		}

		// Create occurrencesOfPitches with default values 1
		List<Integer> occurrencesOfPitches = new ArrayList<Integer>();
		for (int j = 0; j < pitchesInChord.size(); j++) {
			occurrencesOfPitches.add(1);
		}
		// Determine how often each individual pitch appears in chord
		for (int j = 0; j < pitchesInChord.size(); j++) {
			int currentPitch = pitchesInChord.get(j);
			int occurrencesOfCurrentPitch = 1;
			// Set index j of pitchesInChord temporarily to 0 and search pitchesInChord for other occurrences of 
			// currentPitch
			pitchesInChord.set(j, 0);
			for (int k = 0; k < pitchesInChord.size(); k++) {
				if (pitchesInChord.get(k) == currentPitch) {
					occurrencesOfCurrentPitch++;
				}
			}
			// Set element j of occurencesOfPitches to occurenceOfCurrentPitch
			occurrencesOfPitches.set(j, occurrencesOfCurrentPitch);
			// Reset index j of pitchesInChord and proceed with the next iteration of the outer for
			pitchesInChord.set(j, currentPitch);
		}
		return occurrencesOfPitches;
	}


	/**
	 * Determines whether a chord contains a voice crossing.
	 * @return
	 */
	private boolean chordContainsVoiceCrossing(int chordIndex) {
		boolean chordContainsVoiceCrossing = false;
		NotationSystem notationSystem = piece.getScore();
		List<Note> currentChord = getTranscriptionChords().get(chordIndex); // conditions satisfied; external version OK
		List<Integer> voicesInCurrentChord = new ArrayList<Integer>();
		// List the voices in the chord 
		for (int j = 0; j < currentChord.size(); j++) {
			Note currentNote = currentChord.get(j);
			int currentVoice = findVoice(currentNote, notationSystem);
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
	 * Gets, for each voice in the Transcription, the highest and lowest Notes. Returns a double[][], each element of
	 * which represents a voice (where the element at index 0 represents the top voice) and contains two numbers: at
	 * index 0 the lowest pitch in that voice, and at index 1 the highest.
	 *  
	 * @return
	 */
	// TESTED (for both tablature and non-tablature case) 
	Integer[][] getLowestAndHighestPitchPerVoice() {
		Integer[][] lowestAndHighest = new Integer[MAXIMUM_NUMBER_OF_VOICES][2];

		NotationSystem system = piece.getScore();
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
	public List<List<Integer>> getEqualPitchPairsInfo(List<List<Note>> transcriptionEvents, int eventIndex) {
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
	 * Gets the mirrorPoint, i.e., the start time of the fictional bar after the last bar, which is needed for
	 * reversing the piece. If the piece has an anacrusis, the last bar will be a full bar (i.e., the original,
	 * shortened bar to which the length of the anacrusis is added).
	 * 
	 * @return
	 */
	// TESTED
	Rational getMirrorPoint(List<Integer[]> meterInfo) {
		Rational mirrorPoint = Rational.ZERO;

		NotationSystem notationSystem = getPiece().getScore();
		// For each voice
		for (NotationStaff notationStaff : notationSystem) {	
			for (NotationVoice notationVoice : notationStaff) {
				Rational currentMirrorPoint = Rational.ZERO;
				// 1. Determine the onset- and offset time and the duration of the last note in the voice
				NotationChord lastNotationChord = notationVoice.get(notationVoice.size() - 1);
				Rational onsetTimeLastNote = lastNotationChord.getMetricTime();
				Rational offsetTimeLastNote = Rational.ZERO;
				for (Note n : lastNotationChord) {
					Rational duration = n.getMetricDuration();
					Rational offsetTime = n.getMetricTime().add(duration);
					if (offsetTime.isGreater(offsetTimeLastNote)) {
						offsetTimeLastNote = offsetTime;	
					}
				}
				// 2. Get the metric position of the onset and offset of the last note and determine
				// the onset- and offset bar(s) as well as the onset meter 
				// NB: The offset time of the last note must be reduced to ensure that it falls with the bar 
				// (if it coincides with the final barline, offsetTimeLastNote will not have a metric position)
				Rational[] metricPositionOnset = Tablature.getMetricPosition(onsetTimeLastNote, meterInfo);
				Rational reduction = new Rational(1, 128);
				Rational[] metricPositionOffset = Tablature.getMetricPosition(offsetTimeLastNote.sub(reduction), meterInfo);
				// Onset- and offset bar(s)
				int barNumberOnset = metricPositionOnset[0].getNumer();
				int barNumberOffset = metricPositionOffset[0].getNumer();
				// Onset bar meter
				Rational meterOnset = null;
				for (Integer[] currentMeter : meterInfo) {
					if (barNumberOnset >= currentMeter[2] && barNumberOnset <= currentMeter[3]) {
						meterOnset = new Rational(currentMeter[0], currentMeter[1]);
					}
				}
				// 3. Determine currentMirrorPoint
				// Determine the distance of the note's onset to the beginning of the (possibly fictional) next bar 
				Rational distanceFromOnsetToNextBar = meterOnset.sub(metricPositionOnset[1]);
				// If the note's onset and offset are not in the same bar: determine the total duration of all 
				// succeeding bars	
				Rational durationSucceedingBars = Rational.ZERO;
				if (barNumberOffset > barNumberOnset) {     	
					for (int i = barNumberOnset + 1; i <= barNumberOffset; i++) {
						for (Integer[] currentMeter : meterInfo) {
							if (i >= currentMeter[2] && i <= currentMeter[3]) { 
								durationSucceedingBars = durationSucceedingBars.add(new Rational(currentMeter[0], currentMeter[1]));
							}
						}
					}
				}
				// Determine currentMirrorPoint; reset mirrorPoint if necessary
				currentMirrorPoint = onsetTimeLastNote.add(distanceFromOnsetToNextBar).add(durationSucceedingBars);
				if (currentMirrorPoint.isGreater(mirrorPoint)) {
					mirrorPoint = currentMirrorPoint;
				}
			}
		}
		return mirrorPoint;
	}


	/**
	 * Gets the number of active voices in the Transcription.
	 */
	// TESTED (for both tablature and non-tablature case)
	public int getNumberOfVoices() {
		NotationSystem system = piece.getScore();
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


	/**
	 * Combines the two given labels into one label, containing the value 1.0 twice.
	 * 
	 * @param labelOne
	 * @param labelTwo
	 * @return
	 */
	// TESTED
	public static List<Double> combineLabels(List<Double> labelOne, List<Double> labelTwo) {
		List<Double> combined = new ArrayList<Double>(labelOne);
		int voiceTwo = labelTwo.indexOf(1.0);
		combined.set(voiceTwo, 1.0);
		return combined;
	}


	/**
	 * Non-tablature case only.
	 * 
	 * @return
	 */
	//TESTED
	public List<Rational[]> getAllMetricPositions() {
		List<Rational[]> allMetricPositions = new ArrayList<Rational[]>();
		List<Integer[]> meterInf = getMeterInfo();
		Integer[][] bnp = getBasicNoteProperties();
		for (Integer[] b : bnp) {
			Rational currentMetricTime = 
				new Rational(b[ONSET_TIME_NUMER], b[ONSET_TIME_DENOM]);
			allMetricPositions.add(Tablature.getMetricPosition(currentMetricTime, meterInf)); 
		}
		return allMetricPositions;	
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
			System.out.println("ERROR: if basicTabSymbolProperties == null, basicNoteProperties must not be, and vice versa" + "\n");
			throw new RuntimeException("ERROR (see console for details)");
		}
	}


	/**
	 * Creates a duration label encoding the given durational value (in thirtysecond notes).
	 * 
	 * NB: Tablature case only.
	 *  
	 * @param duration
	 * @return
	 */
	// TESTED
	public static List<Double> createDurationLabel(int duration) {
		List<Double> durationLabel = new ArrayList<Double>();
		for (int i = 0; i < DURATION_LABEL_SIZE; i++) {
			durationLabel.add(0.0);
		}
		durationLabel.set((duration - 1), 1.0);
		return durationLabel;
	}


	/**
	 * Determines whether the given voice label represents a CoD.
	 * 
	 * @param voiceLabel
	 * @return
	 */
	// TESTED
	public static boolean containsCoD(List<Double> voiceLabel) {
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


	/**
	 * Creates a Piece from the given arguments.
	 *  
	 * @param btp
	 * @param bnp 
	 * @param voiceLabels
	 * @param durationLabels
	 * @param numberOfVoices
	 */
	// TESTED for both tablature and non-tablature case
	public static Piece createPiece(Integer[][] btp, Integer[][] bnp, List<List<Double>> voiceLabels, 
		List<List<Double>> durationLabels, int numberOfVoices, MetricalTimeLine mtl,
		SortedContainer<Marker> keySigs) {

		verifyCase(btp, bnp);

		// Make an empty Piece with the given number of voices
		Piece piece = new Piece();
		NotationSystem system = piece.createNotationSystem();
		for (int i = 0; i < numberOfVoices; i++) {
			NotationStaff staff = new NotationStaff(system); 
			system.add(staff); 
			staff.add(new NotationVoice(staff));
		}
		piece.setMetricalTimeLine(mtl);
		piece.setHarmonyTrack(keySigs);

		// a. In the tablature case
		if (btp != null) {
			// Iterate through the Tablature, convert each TabSymbol into a note, and add it to the given voice    
			for (int i = 0; i < btp.length; i++) {
				// Create a Note from the TabSymbol at index i
				int pitch = btp[i][Tablature.PITCH];
				Rational metricTime = 
					new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
				Rational metricDuration = null;
				// If durationLabels == null (which is the case when not modelling duration): give notes their 
				// minimum duration 
				if (durationLabels == null) {
					metricDuration = 
						new Rational(btp[i][Tablature.MIN_DURATION], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
				}
				// Else: give notes their predicted duration
				else {
					metricDuration = DataConverter.convertIntoDuration(durationLabels.get(i))[0]; // TODO [0] is possible because each element in durations currently contains only one Rational
				}
				Note note = Transcription.createNote(pitch, metricTime, metricDuration);
			
				// Add the Note to each voice in currentVoices
				List<Integer> currentVoices = DataConverter.convertIntoListOfVoices(voiceLabels.get(i)); //voices.get(i);
				for (int v : currentVoices) {
					NotationVoice voice = piece.getScore().get(v).get(0);
					voice.add(note);
				}	
			}
		}
		// b. In the non-tablature case
		else if (bnp != null) {
			// Iterate through the Transcription, convert each note into a Note, and add it to 
			// the given voice
			for (int i = 0; i < bnp.length; i++) {
				// Create a Note from the note at index i
				int pitch = bnp[i][PITCH];
				Rational metricTime =	
					new Rational(bnp[i][ONSET_TIME_NUMER], bnp[i][ONSET_TIME_DENOM]);
				Rational metricDuration = 
					new Rational(bnp[i][DUR_NUMER], bnp[i][DUR_DENOM]);
				Note note = Transcription.createNote(pitch, metricTime, metricDuration);
				// Add the Note to each voice in currentVoices
				List<Integer> currentVoices = DataConverter.convertIntoListOfVoices(voiceLabels.get(i));
				for (int v : currentVoices) {
					NotationVoice voice = piece.getScore().get(v).get(0);
					voice.add(note);
				}
			}
		}
		return piece;
	}


  /**
   * Creates a Note with the given pitch, MetricTime, and MetricDuration. 
   * 
   * @param pitch
   * @param metricTime
   * @param metricDuration
   * @return
   */
  public static Note createNote(int pitch, Rational metricTime, Rational metricDuration) {
  	// A Note consists of a ScoreNote and a PerformanceNote; each need to be created separately first 
  	// 1. Create the ScoreNote
  	ScorePitch scorePitch = new ScorePitch(pitch);
  	ScoreNote scoreNote = new ScoreNote(scorePitch, metricTime, metricDuration);
	  // 2. Create the PerformanceNote. The argumentless constructor can be used; after the creation only the
   	// object variable pitch needs to be set: the others (duration, velocity, and generated) are irrelevant here
  	PerformanceNote performanceNote = new PerformanceNote();
	  performanceNote.setPitch(pitch);
	  
	  Note note = new Note(scoreNote, performanceNote);
    // TODO? OR, as PerformanceNote does not really apply in our case:
//	  MidiNote midiNote = MidiNote.convert(performanceNote);
//    Note note = new Note(scoreNote, midiNote);
	  
		return note;
	}
  
  
  /** Adds a Note to the Transcription in the given voice at the given metricTime.
	 * 
	 * @param note
	 * @param voiceNumber 
	 * @param metricTime
	 */
  // TESTED (for both the tablature and the non-tablature case)
	public void addNote(Note note, int voiceNumber, Rational metricTime) {
	  NotationSystem system = piece.getScore();
	  NotationStaff staff = system.get(voiceNumber);
	  NotationVoice voice = staff.get(0);
		NotationChord chord = new NotationChord();
		chord.add(note);
		chord.setMetricTime(metricTime);
		voice.add(chord);
	} 
	
		 
  /** Removes the Note with the given midiPitch from the NotationChord in the given voice at the given metric
   *  time. If no such Note exists, a RuntimeException is thrown. 
	 * 
	 * @param midiPitch
	 * @param voiceNumber  
	 * @param metricTime 
	 */
	// TESTED (for both tablature and non-tablature case)
	public void removeNote(int midiPitch, int voiceNumber, Rational metricTime) {
		NotationSystem system = piece.getScore();
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
	 * Visualises the Transcription. 
	 * 
	 * @param showAsScore
	 * @param numberOfVoices
	 */
	public JFrame visualise(TimeSignature timeSig, /*KeyMarker keyMarker,*/ boolean showAsScore, int numberOfVoices) {

		String windowTitle = getPieceName();
		Piece argPiece = getPiece();

		Piece piece = new Piece(); 
		NotationSystem notationSystem = piece.createNotationSystem();
		
		// Add time and key signatures
		MetricalTimeLine metricalTimeLine = piece.getMetricalTimeLine();
		
		TimeSignatureMarker timeSigMarker = 
			new TimeSignatureMarker(timeSig.getNumerator(), timeSig.getDenominator(), new Rational(0, 1));
		timeSigMarker.setTimeSignature(timeSig);
		metricalTimeLine.add(timeSigMarker);
//		metricalTimeLine.add(keyMarker);
		
		
		int lowestVoice = -1; 
		if (numberOfVoices == 3) {
			lowestVoice = TENOR;
		}
		else if (numberOfVoices == 4) {
			lowestVoice = BASSUS;
		}
		
		for (int voice = SUPERIUS; voice <= lowestVoice; voice++) {
			NotationStaff notationStaff = null;
			if (voice == SUPERIUS || voice == TENOR) {
				notationStaff = new NotationStaff(notationSystem);
				if (voice == TENOR) {
					notationStaff.setClefType('f', 1, 0);
				}
			}
			if (voice == ALTUS || voice == BASSUS) {
				if (!showAsScore) {
					// If j == ALTUS, notationSystem contains one staff; if j == BASSUS it contains two staves
					notationStaff = notationSystem.get((notationSystem.size() - voice) * -1);
				}
				else {
					notationStaff = new NotationStaff(notationSystem);
					if (voice == BASSUS) {
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
		fullScoreFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenuItem saveFile = new JMenuItem("Save");
			fileMenu.add(saveFile);
			saveFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					saveFileAction();
				}
			});

			JMenu editMenu = new JMenu("Edit");
			encodingWindowMenubar.add(editMenu);   

			JMenuItem blaFile = new JMenuItem("Bla"); 
			editMenu.add(blaFile); 
			blaFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenu viewMenu = new JMenu("View");
			encodingWindowMenubar.add(viewMenu);   

			JMenuItem scoreViewFile = new JMenuItem("Score View"); 
			viewMenu.add(scoreViewFile); 
			scoreViewFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

			JMenuItem grandStaffViewFile = new JMenuItem("Grand Staff View"); 
			viewMenu.add(grandStaffViewFile); 
			grandStaffViewFile.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
//					openFileAction();
				}
			});

		}
		return encodingWindowMenubar;
	}


	/**
	 * Visualises the Transcription; the given name is the one shown in the JFrame. 
	 * 
	 * @param windowTitle
	 */
	public JFrame visualise(String windowTitle) {
		ScoreEditor scoreEditor = new ScoreEditor(getPiece().getScore());
		scoreEditor.setModus(Mode.SELECT_AND_EDIT);

		JFrame fullScoreFrame = new JFrame(windowTitle);
		fullScoreFrame.add(new JScrollPane(scoreEditor));
		fullScoreFrame.setSize(800, 600);
//		fullScoreFrame.setSize(1200, 1200);
	    fullScoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    fullScoreFrame.setVisible(true);
	    scoreEditor.setSize(20000, 20000);
	    return fullScoreFrame;
	}


	/**
	 * Returns the Transcription's voice assignments, listed per chord. The parameter highestNumberOfVoices
	 * controls the size of the voice assignments returned.
	 *  
	 * @param highestNumberOfVoices
	 * @return
	 */
	// TESTED
	public List<List<Integer>> getVoiceAssignments(int highestNumberOfVoices) {
		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();

//		List<List<List<Double>>> allChordVoiceLabels = getChordVoiceLabels(tablature);	
		List<List<List<Double>>> allChordVoiceLabels = getChordVoiceLabels(); // VANDAAG	
		// For each chord
		for (int i = 0; i < allChordVoiceLabels.size(); i++) {
			// Get the voice labels and convert them into a voice assignment
			List<List<Double>> currentChordVoiceLabels = allChordVoiceLabels.get(i);
			List<Integer> currentVoiceAssignment = 
				DataConverter.getVoiceAssignment(currentChordVoiceLabels, highestNumberOfVoices);
			// Add currentVoiceAssignment to voiceAssignments
			voiceAssignments.add(currentVoiceAssignment);
		}		
		return voiceAssignments;
	}


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
	// TESTED for both fwd and bwd model
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
	public static List<Integer> getIndicesOfSustainedPreviousNotes(Integer[][] btp, List<List<Double>> durationLabels,
		Integer[][] bnp, int noteIndex) {

		verifyCase(btp, bnp);
		List<Integer> indicesOfSustainedPreviousNotes = new ArrayList<Integer>();

		// 1. Determine the onset time of the current note and the index of the first note in the chord
		Rational onsetTimeCurrentNote = null;
		int lowestNoteIndex = -1;
		// a. In the tablature case
		if (btp != null) {
			onsetTimeCurrentNote = new Rational(btp[noteIndex][Tablature.ONSET_TIME],
				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			lowestNoteIndex =	noteIndex - btp[noteIndex][Tablature.NOTE_SEQ_NUM];
		}
		// b. In the non-tablature case
		else if (bnp != null) {
			onsetTimeCurrentNote = new Rational(bnp[noteIndex][Transcription.ONSET_TIME_NUMER],
				bnp[noteIndex][Transcription.ONSET_TIME_DENOM]);
			lowestNoteIndex = noteIndex - bnp[noteIndex][Transcription.NOTE_SEQ_NUM];		
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
					Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
				// Determine the duration. In the case of a CoD, the longer duration of the CoDnote must be considered:
				// if this duration does not cause note overlap, the CoDnote will not cause note overlap at all). In 
				// both cases this is the first element of durationCurrentPreviousNote
				List<Double> durationLabelCurrentPreviousNote = durationLabels.get(i);
				durationCurrentPreviousNote = 
					DataConverter.convertIntoDuration(durationLabelCurrentPreviousNote)[0];
			}
			// b. In the non-tablature case
			else if (bnp != null) {
				// Determine the metric time
				metricTimeCurrentPreviousNote = new Rational(bnp[i][Transcription.ONSET_TIME_NUMER],	
					bnp[i][Transcription.ONSET_TIME_DENOM]);
				// Determine the duration
				durationCurrentPreviousNote = new Rational(bnp[i][Transcription.DUR_NUMER],
					bnp[i][Transcription.DUR_DENOM]);
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
				pitchesOfSustainedPreviousNotes.add(bnp[i][Transcription.PITCH]);
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
	// TESTED for both tablature- and non-tablature case
	public static List<Integer> getVoicesOfSustainedPreviousNotesInChord(Integer[][] btp, List<List<Double>> durationLabels,
		List<Integer[]> voicesCoDNotes, Integer[][] bnp, List<List<Double>> allVoiceLabels, int lowestNoteIndex) {
		List<Integer> voicesOfSustainedPreviousNotes = new ArrayList<Integer>();

		List<Integer> indicesOfSustainedPreviousNotes = 
			getIndicesOfSustainedPreviousNotes(btp, durationLabels, bnp, lowestNoteIndex);

		for (int i : indicesOfSustainedPreviousNotes) {
			List<Double> currentVoiceLabel = allVoiceLabels.get(i);
			List<Integer> currentVoices = DataConverter.convertIntoListOfVoices(currentVoiceLabel);

			// Take into account CoD
			if (currentVoices.size() > 1) {
				Rational[] duration = DataConverter.convertIntoDuration(durationLabels.get(i));
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
						new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
					Rational offsetShorter = onsetShorter.add(shorter);
					Rational onsetCurr = 
						new Rational(btp[lowestNoteIndex][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
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
	 * Gets the ranges (in MIDI pitches) of the individual voices, starting with the highest
	 * voice (voice 0).
	 *  
	 * @return
	 */
	// TESTED
	public List<Integer[]> getVoiceRangeInformation() {
		List<Integer[]> ranges = new ArrayList<>();
		
		NotationSystem nSys = getPiece().getScore();
		// For each voice i
		for (int i = 0; i < getPiece().getScore().size(); i ++) {
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
	 * Gets information on the voice crossings in the Transcription. Returns a String containing
	 * (1) information on each voice crossing between notes with the same onset time (Type 1 vc) encountered 
	 * (2) information on a voice crossing between notes with different onset times (Type 2 vc) encountered
	 * 
	 * @param tablature Is <code>null</code> in the non-tablature case.
	 * @return
	 */
	public Integer[] getVoiceCrossingInformation(Tablature tablature) {
		String voiceCrossingInformation = "";
		int totalTypeOne = 0;
		int totalTypeTwo = 0;

		// For each note
		NoteSequence noteSeq = getNoteSequence();
//		List<List<List<Double>>> chordVoiceLabels = getChordVoiceLabels(tablature);
		List<List<List<Double>>> chordVoiceLabels = getChordVoiceLabels(); // VANDAAG
		List<List<Double>> voiceLabels = getVoiceLabels();

		Integer[][] basicTabSymbolProperties = null;
		Integer[][] basicNoteProperties = null;
		int numberOfChords = 0;
		// a. In the tablature case
		if (tablature != null) {
			basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
			numberOfChords = tablature.getTablatureChords().size(); 
		}
		// b. in the non-tablature case
		else {
			basicNoteProperties = getBasicNoteProperties();
			numberOfChords = getTranscriptionChords().size(); // conditions satisfied; external version OK
		}
		
		Integer[] timesInvolved = new Integer[MAXIMUM_NUMBER_OF_VOICES];
		Arrays.fill(timesInvolved, 0);

		// For each chord
		int lowestNoteIndex = 0;
		for (int i = 0; i < numberOfChords; i++) { // i is index of current chord  	
			// Get current chord size and meterinfo, and find current onset time
			int currentChordSize = 0; 
			List<Integer[]> meterInfo = null;
			Rational onsetCurrNote = null;
			List<Integer> pitchesInChord;
			// a. in the tablature case
			if (tablature != null) {
				currentChordSize = tablature.getTablatureChords().get(i).size();
				meterInfo = tablature.getMeterInfo();
				for (Integer[] btp : basicTabSymbolProperties) {
					if (btp[Tablature.CHORD_SEQ_NUM] == i) {
						onsetCurrNote = new Rational(btp[Tablature.ONSET_TIME], 
							Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
						break;
					}
				}
				pitchesInChord = tablature.getPitchesInChord(i); 
			}
			// b. In the non-tablature case
			else {
				currentChordSize = getTranscriptionChords().get(i).size(); // conditions satisfied; external version OK
				meterInfo = getMeterInfo();
				for (Integer[] bnp : basicNoteProperties) {
					if (bnp[Transcription.CHORD_SEQ_NUM] == i) {
						onsetCurrNote = new Rational(bnp[Transcription.ONSET_TIME_NUMER], 
							bnp[Transcription.ONSET_TIME_DENOM]);
						break;
					}
				}
				pitchesInChord = getPitchesInChord(i);
			}

			String currMeasure = 
				"" + Tablature.getMetricPosition(onsetCurrNote, meterInfo)[0].getNumer() + " " +
				Tablature.getMetricPosition(onsetCurrNote, meterInfo)[1];

			// a. Get the voice crossing information within the chord
			List<List<Double>> currentChordVoiceLabels = chordVoiceLabels.get(i);
			List<List<Integer>> voicesInChord = DataConverter.getVoicesInChord(currentChordVoiceLabels);
			List<List<Integer>> vcInfo = 
				getVoiceCrossingInformationInChord(pitchesInChord, voicesInChord);
			if (vcInfo.get(0).size() != 0) {
				voiceCrossingInformation = 
					voiceCrossingInformation.concat("Type 1 voice crossing at chordindex " +
					i + " (m. " + currMeasure + "); voices involved are " + vcInfo.get(0) + "\n");
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

			// b. For each note in the chord: get the voice crossing information with any previous sustained notes 
			for (int j = lowestNoteIndex; j < lowestNoteIndex + currentChordSize; j++) { // j is index of current note
				int pitchCurrNote = 0;
				// a. In the tablature case
				if (tablature != null) {
					pitchCurrNote = basicTabSymbolProperties[j][Tablature.PITCH];
				}
				// b. In the non-tablature case
				else {
					pitchCurrNote = basicNoteProperties[j][Transcription.PITCH];
				}
				List<Double> voiceLabelCurrNote = voiceLabels.get(j);
				List<Integer> voicesCurrNote = DataConverter.convertIntoListOfVoices(voiceLabelCurrNote);
				// Find sustained notes
				for (int k = 0; k < noteSeq.size(); k++) { // k is index of note before current note 
					Note n = noteSeq.getNoteAt(k);
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
								DataConverter.convertIntoListOfVoices(voiceLabelPrevNote);
							// For each note in the chord: voice crossing with sustained note if that sustained note
							// -has a lower voice number (i.e., is in a higher voice) and a lower pitch
							// -has a higher voice number (i.e., is in a lower voice) and a higher pitch
							// Two for-loops necessary to take into account CoDs
							for (int currVoice : voicesCurrNote) {
								for (int prevVoice : voicesPrevNote) {
									if ((prevVoice < currVoice && pitchPrevNote < pitchCurrNote) ||
										(prevVoice > currVoice && pitchPrevNote > pitchCurrNote)) {
//										double prevMeasure = onsetPrevNote.toDouble() + 1.0;
										String prevMeasure = "" + Tablature.getMetricPosition(onsetPrevNote, meterInfo)[0].getNumer() + 
											" " + Tablature.getMetricPosition(onsetPrevNote, meterInfo)[1];
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
//		System.out.println(voiceCrossingInformation);
		Integer[] res = new Integer[3 + timesInvolved.length];
		res[0] = totalTypeOne;
		res[1] = totalTypeTwo;
		res[2] = totalTypeOne + totalTypeTwo;
		for (int i = 0; i < timesInvolved.length; i++) {
			res[3+i] = timesInvolved[i];
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
		int numChords = basicNoteProperties[basicNoteProperties.length - 1][CHORD_SEQ_NUM] + 1;
//		for (int i = 0; i < chords.size(); i++) {  		
		for (int i = 0; i < numChords; i++) {
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
	 * Lists all the unique (chord) voice assignments in the transcription in the order they are encountered.
	 * The parameter highestNumberOfVoices controls the size of the voice assignments returned.
	 * 
	 * @param highestNumberOfVoices
	 * @return
	 */
	// TESTED (for both tablature and non-tablature case)
	public List<List<Integer>> generateVoiceAssignmentDictionary(int highestNumberOfVoices) {
		List<List<Integer>> voiceAssignmentDictionary = new ArrayList<List<Integer>>();
		List<List<Integer>> voiceAssignments = getVoiceAssignments(/*tablature,*/ highestNumberOfVoices);

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
    Double[] emptyLabelArray = new Double[Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()];
    Arrays.fill(emptyLabelArray, 0.0);
    List<Double> emptyLabel = Arrays.asList(emptyLabelArray);
	              
    // Iterate through all notes in the initial NoteSequence, which for each CoD still contains both
    // CoDnotes. Lower CoDnotes are always in the lower voice and thus come first in the NoteSequence   
    NoteSequence initialNoteSeq = getNoteSequence();
    for (Note n : initialNoteSeq) {
  	  List<List<Double>> currentDurationLabels = new ArrayList<List<Double>>();
    	List<Double> durationLabelCurrentNote = new ArrayList<Double>(emptyLabel);
    	Rational durationCurrentNote = n.getMetricDuration();
    	int numer = durationCurrentNote.getNumer();
	    int denom = durationCurrentNote.getDenom();
	    // Set the correct element of durationLabelCurrentNote to 1.0
	    int indexToSet = numer - 1;
	    if (denom != Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()) {
	    	indexToSet = (numer * (Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom()/denom)) - 1;
	    }
  	  durationLabelCurrentNote.set(indexToSet, 1.0);
  	  // Add durationLabelCurrentNote to currentDurationLabels; add currentDurationLabels to initialDurationLabels
  	  currentDurationLabels.add(durationLabelCurrentNote);
  	  initialDurationLabels.add(currentDurationLabels);
    }
    // Set durationLabels
    setDurationLabelsOLD(initialDurationLabels);
//    durationLabels = initialDurationLabels;
  }
  
  
  private void setDurationLabelsOLD(List<List<List<Double>>> argDurationLabels) {
  	durationLabelsOLD = argDurationLabels;
  }
  
  
  private List<List<List<Double>>> getDurationLabelsOLD() {
	  return durationLabelsOLD;
  }
  
  
  /**
	 * Finds for all CoDnotes (i.e., notes representing a Note that is shared by two voices) in the Tablature the
	 * corresponding Notes in the Transcription, and
	 * (1) removes the CoDnote with the shorter duration from noteSequence
	 * (2) combines the voice labels of both CoDnotes into a List<Double> with two 1.0s, sets the label of the 
	 *     lower CoDnote to the result in voiceLabels, and removes the label of the upper from voiceLabels
	 * (3) combines the duration labels of both CoDnotes into a List<List<Double>>, the first element of which 
	 *     represents the duration of the longer note, sets the label of the lower CoDnote to the result in 
	 *     durationLabels, and removes the label of the upper from durationLabels 
	 *          
	 * Also sets voicesCoDNotes, a List<Integer[]> the size of the number of notes in the Transcription, 
	 * containing for each element:
	 *   a. if the note at that index is not a CoDnote: <code>null</code>; 
	 *   b. If the the note at that index is a CoDnote: an Integer[] containing 
	 *      as element 0: the voice the longer CoDnote is in;
	 *      as element 1: the voice the shorter CoDnote is in.
	 *      In case both CoDnotes have the same duration, the lower CoDnote (i.e., the one in the lower voice
	 *      that comes first in the NoteSequence) is placed at element 0.
	 * 
	 * NB1: This method presumes that a chord contains only one CoD, and neither a unison nor a course crossings.
	 * NB2: Tablature case only; must be called before handleCourseCrossings().
	 * 
	 * @param tablature
	 */
	private void handleCoDNotesOLD(Tablature tablature) {				
		NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<List<Double>>> durationLab = getDurationLabelsOLD();
    List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
    List<Integer[]> voicesCoD = new ArrayList<Integer[]>();  
    // Initialise voicesCoD with all elements set to null
    for (int i = 0; i < tablature.getBasicTabSymbolProperties().length; i++) {
    	voicesCoD.add(null);
    }
    				
	  // For every chord
		for (int i = 0; i < tablatureChords.size(); i++) {
			// If the chord contains a CoD
			if (getCoDInfo(tablatureChords, i) != null) {
			  Integer[][] coDInfo = getCoDInfo(tablatureChords, i);
			  // Get the (most recent! needed for calculating notesPreceding) transcription chords
			  List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
			  // For each CoD in the chord 
			  int notesAlreadyRemovedFromChord = 0;
			  for (int j = 0; j < coDInfo.length; j++) {
			  	// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CoDnotes
			    // a. Calculate the number of Notes preceding the CoD chord by summing the size of all previous chords
			    int notesPreceding = 0;
			    for (int k = 0; k < i; k++) {
		  	  	notesPreceding += transcriptionChords.get(k).size();
	  		  }
			    // b. Calculate the indices
			    int indexOfLowerCoDNote = notesPreceding + (coDInfo[j][1] - notesAlreadyRemovedFromChord);
			    int indexOfUpperCoDNote = notesPreceding + (coDInfo[j][2] - notesAlreadyRemovedFromChord);
			    
			    // 2. Adapt noteSeq, voiceLab, and durationLab; also adapt voicesCoD
			    // a. noteSeq: remove the CoDnote with the shorter duration
			    Rational durationLower = noteSeq.getNoteAt(indexOfLowerCoDNote).getMetricDuration();
			    Rational durationUpper = noteSeq.getNoteAt(indexOfUpperCoDNote).getMetricDuration();
			    // Assume that the lower note has the longer duration. If this is so or if both notes have the
			    // same duration, indexOfLongerCoDNote == indexOfLowerCoDNote; otherwise, indexOfLongerCoDNote == 
			    // indexOfUpperCoDNote 
			    int indexOfLongerCoDNote = indexOfLowerCoDNote;
			    int indexOfShorterCoDNote = indexOfUpperCoDNote;
		  	  if (durationLower.isLess(durationUpper)) {
		  	  	indexOfShorterCoDNote = indexOfLowerCoDNote;
		  	  	indexOfLongerCoDNote = indexOfUpperCoDNote;
		  	  }
		  	  noteSeq.deleteNoteAt(indexOfShorterCoDNote);
		  	  // The voices that go with the longer and shorter CoDnote, needed for setting voicesCoD, must be 
		  	  // determined before voiceLab is adapted   
		  	  List<Double> voiceLabelOfLongerCoDNote = new ArrayList<Double>(voiceLab.get(indexOfLongerCoDNote));
		  	  int voiceLonger = DataConverter.convertIntoListOfVoices(voiceLabelOfLongerCoDNote).get(0);
		  	  List<Double> voiceLabelOfShorterCoDNote = new ArrayList<Double>(voiceLab.get(indexOfShorterCoDNote));
		  	  int voiceShorter = DataConverter.convertIntoListOfVoices(voiceLabelOfShorterCoDNote).get(0);
			    // b. voiceLab: combine the labels of both CoDnotes, set the label at indexOfLowerCoDNote to the 
		  	  // result, and remove the label of the upper CoDnote from voiceLab
			    List<Double> voiceLabelOfLowerCoDNote = voiceLab.get(indexOfLowerCoDNote);			    
		  	  List<Double> voiceLabelOfUpperCoDNote = voiceLab.get(indexOfUpperCoDNote);
		  	  int voiceNumberToAdd = voiceLabelOfUpperCoDNote.indexOf(1.0);
			    voiceLabelOfLowerCoDNote.set(voiceNumberToAdd, 1.0);
			    voiceLab.set(indexOfLowerCoDNote, voiceLabelOfLowerCoDNote);
		  		voiceLab.remove(indexOfUpperCoDNote);
			    // c. durationLab: add the label of the shorter CoDnote to that of the longer, set the label at 
		  		// indexOfLowerCoDNote to the result, and remove the label of the upper CoDnote from durationLab
			    List<List<Double>> durationLabelOfLongerCoDNote = durationLab.get(indexOfLongerCoDNote);
		  		List<List<Double>> durationLabelOfShorterCoDNote = durationLab.get(indexOfShorterCoDNote);
			    durationLabelOfLongerCoDNote.add(durationLabelOfShorterCoDNote.get(0)); 
			    durationLab.set(indexOfLowerCoDNote, durationLabelOfLongerCoDNote);
		  		durationLab.remove(indexOfUpperCoDNote);
		  		// d. Set the element at index indexOfLowerCoDNote in voicesCoD: set the first element to the
		  		// voice that goes with the longer CoDnote, and the second to the voice that goes with the shorter 
		  		voicesCoD.set(indexOfLowerCoDNote, new Integer[]{voiceLonger, voiceShorter});
		  		
		  		// 3. Increase notesAlreadyRemovedFromChord in case the chord contains more than one CoD and another
	  			// iteration through the inner for-loop is necessary
	  			notesAlreadyRemovedFromChord++;
												
	  		  // 4. Reset noteSequence, voiceLabels, and durationLabels; set voicesCoDNotes
	  			setNoteSequence(noteSeq);
	  			setVoiceLabels(voiceLab);
	  			setDurationLabelsOLD(durationLab);
	  			setVoicesCoDNotes(voicesCoD);
				
	  			// 5. Concat information to adaptations
	  			adaptations = adaptations.concat("  CoD found in chord " + i + ": note no. " + (indexOfShorterCoDNote	
	  				- notesPreceding)	+	" (pitch " + coDInfo[j][0]	+	") in that chord removed from the NoteSequence; " + 
	  				"list of voice labels and list of durations adapted accordingly." + "\n");
	  	  }
	  	} 
		}
	}
	
	
	/**
	 * Finds for all course-crossing notes (i.e., notes pairs where the note on the lower course has the higher
	 * pitch) in the Tablature the corresponding Notes in the Transcription, and 
	 * (1) swaps these Notes in noteSequence;
	 * (2) swaps the corresponding voice labels in voiceLabels;
	 * (3) swaps the corresponding duration labels in durationLabels. 
	 * 
	 * NB1: This method presumes that a chord contains only one course crossing, and neither a CoD nor a unison.
	 * NB2: Tablature case only; must be called after handleCoDNotes().
	 * 
	 * @param tablature
	 */
	// TESTED
	private void handleCourseCrossingsOLD(Tablature tablature) {
	  NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLab = getVoiceLabels();
		List<List<List<Double>>> durationLab = getDurationLabelsOLD();
	  List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
	  List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();
	    		
	  // For every chord
		for (int i = 0; i < tablatureChords.size(); i++) {
			// If the chord contains a course crossing
			if (tablature.getCourseCrossingInfo(i) != null) {
				Integer[][] chordCrossingInfo = tablature.getCourseCrossingInfo(i);
			 	// For each course crossing in the chord
				for (int j = 0; j < chordCrossingInfo.length; j++) {
					// 1. Determine the indices in noteSeq, voiceLab, and durationLab of the lower and upper CCnotes
			    // a. Calculate the number of Notes preceding the CC chord by summing the size of all previous chords
			 		int notesPreceding = 0;
			    for (int k = 0; k < i; k++) {
			    	notesPreceding += transcriptionChords.get(k).size();
			    }
			    // b. Calculate the indices
			    int indexOfLowerCCNote = notesPreceding + chordCrossingInfo[j][2];
			    int indexOfUpperCCNote = notesPreceding + chordCrossingInfo[j][3];
			    
			    // 2. Swap
			    noteSeq.swapNotes(indexOfLowerCCNote, indexOfUpperCCNote);
			    Collections.swap(voiceLab, indexOfLowerCCNote, indexOfUpperCCNote);
			    Collections.swap(durationLab, indexOfLowerCCNote, indexOfUpperCCNote);
					
			    // 3. Concat information to adaptations
					adaptations = adaptations.concat("  Course crossing found in chord " + i + ": notes no. " + 
			      chordCrossingInfo[j][2] + " (pitch " + chordCrossingInfo[j][0]	+ ") and " + chordCrossingInfo[j][3] +
			      " (pitch " + chordCrossingInfo[j][1]	+ ") in that chord swapped in the NoteSequence; "+ "list of " + 
			      "voice labels and list of durations adapted accordingly." + "\n");
			  }
			}
		}			
	  // Reset noteSequence, voiceLabels, and durationLabels
		setNoteSequence(noteSeq);
		setVoiceLabels(voiceLab);
		setDurationLabelsOLD(durationLab);
	}
	
	
	/**
	 * Checks for each note in the Transcription whether it is part of a unison of two notes with equal duration.
	 * Returns a list the size of the number of notes in the Transcription, containing
	 *   a. if the note at that index is not a unison note or if the note at that index is part of a unison
	 *      whose notes are of inequal length: <code>null</code>  
	 *   b. if the note at that index is part of a unison whose notes are of equal length: a voice label (i.e.,
	 *      a List<Double>) containing two 1.0s, thus representing both correct voices.
	 * 
	 * NB: Non-tablature case only.
	 *  
	 * @return
	 */
	// TESTED
	private List<List<Double>> getEqualDurationUnisonsInfoOLD() {
		List<List<Double>> equalDurationUnisons = new ArrayList<List<Double>>();
		NoteSequence noteSeq = getNoteSequence();
		List<List<Double>> voiceLabels = getVoiceLabels();
		// Initialise equalDurationUnisons with all elements set to null
		for (int i = 0; i < noteSeq.size(); i++) {
			equalDurationUnisons.add(null);
		}
			
		// For all chords
		List<List<Note>> transcriptionChords = getTranscriptionChordsInternal();  
		for (int i = 0; i < transcriptionChords.size(); i++) {
			// If the chord contains a unison
			Integer[][] currentUnisonInfo = getUnisonInfo(i);
			if (currentUnisonInfo != null) {
				// For each unison
				for (int j = 0; j < currentUnisonInfo.length; j++) {
				  // 1. Determine the indices in noteSeq and voiceLabels of the lower and upper unison notes
		      // a. Calculate the number of Notes preceding the unison chord by summing the size of all previous chords
		   		int notesPreceding = 0;
		      for (int k = 0; k < i; k++) {
		      	notesPreceding += transcriptionChords.get(k).size();
		      }
		      // b. Calculate the indices in the NoteSequence
		      int indexOfLowerUnisonNote = notesPreceding + currentUnisonInfo[j][1];
		      int indexOfUpperUnisonNote = notesPreceding + currentUnisonInfo[j][2];
		      		      
		      // 2. If the unison notes have the same duration
		      Rational durationLower = noteSeq.getNoteAt(indexOfLowerUnisonNote).getMetricDuration();
		      Rational durationUpper = noteSeq.getNoteAt(indexOfUpperUnisonNote).getMetricDuration();
		      List<Double> voiceLabelLower = voiceLabels.get(indexOfLowerUnisonNote);
		      List<Double> voiceLabelUpper = voiceLabels.get(indexOfUpperUnisonNote);
		      if (durationLower.equals(durationUpper)) {
		        // Combine the voice labels
			      int indexOfOneInUpper = voiceLabelUpper.indexOf(1.0);
			      List<Double> correctVoices = new ArrayList<Double>(voiceLabelLower);
			      correctVoices.set(indexOfOneInUpper, 1.0);
		      	equalDurationUnisons.set(indexOfLowerUnisonNote, correctVoices);
		      	equalDurationUnisons.set(indexOfUpperUnisonNote, correctVoices);
		      }
				}
			}
		}	
		return equalDurationUnisons;
	}
    
}
