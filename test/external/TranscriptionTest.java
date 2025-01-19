package external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import conversion.imports.MIDIImport;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.math.Rational;
import external.Transcription.TaggedNote;
import external.Transcription.Type;
import interfaces.CLInterface;
import internal.core.Encoding;
import internal.core.ScorePiece;
import internal.structure.TimelineTest;
import tbp.symbols.TabSymbol;
import tools.labels.LabelTools;
//import tools.music.NoteTimePitchComparator;

public class TranscriptionTest {

	private Map<String, String> paths;
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
//	private File encodingMemorEsto;
//	private File encodingQuiHabitat;
//	private File encodingPreterRerum;
//	private File encodingInExitu;
	private File encodingLasOn;
	private File encodingElslein;
	private File encodingHerrGott;
	private File midiTestpiece;
	private File midiTestGetMeterKeyInfo;
//	private File midiTestGetMeterKeyInfoDiminuted;
//	private File midiTestGetMeterKeyInfoDiminutedNoAnacrusis;
//	private File midiMemorEsto;
//	private File midiQuiHabitat;
//	private File midiPreterRerum;
//	private File midiInExitu;
	private File midiLasOn;
	private File midiElslein;
	private File midiHerrGott;
//	private File midiBWV846;
	
	private List<Double> v0;
	private List<Double> v01;
	private List<Double> v1;
	private List<Double> v2;
	private List<Double> v3;
	private List<Double> v4;

	private List<Double> thirtysecond;
	private List<Double> sixteenth;
	private List<Double> eighth;
	private List<Double> eighthQuarterSNU;
	private List<Double> quarter;
	private List<Double> dottedEighth;
	private List<Double> half;

	private int mnv;
	private int mtsd;
	// TODO: where appropriate, test for both tab and non-tab case 
	// TODO: methods from init(): check sequence of previous methods 
	

	@Before
	public void setUp() throws Exception {
		mnv = Transcription.MAX_NUM_VOICES;
		mtsd = Transcription.MAX_TABSYMBOL_DUR;

		v0 = LabelTools.createVoiceLabel(new Integer[]{0}, mnv);
		v01 = LabelTools.createVoiceLabel(new Integer[]{0, 1}, mnv);
		v1 = LabelTools.createVoiceLabel(new Integer[]{1}, mnv);
		v2 = LabelTools.createVoiceLabel(new Integer[]{2}, mnv);
		v3 = LabelTools.createVoiceLabel(new Integer[]{3}, mnv);
		v4 = LabelTools.createVoiceLabel(new Integer[]{4}, mnv);
		
		thirtysecond = LabelTools.createDurationLabel(new Integer[]{1*3}, mtsd);
		sixteenth = LabelTools.createDurationLabel(new Integer[]{2*3}, mtsd);
		eighth = LabelTools.createDurationLabel(new Integer[]{4*3}, mtsd);
		eighthQuarterSNU = LabelTools.createDurationLabel(new Integer[]{4*3, 8*3}, mtsd);
		quarter = LabelTools.createDurationLabel(new Integer[]{8*3}, mtsd);
		dottedEighth = LabelTools.createDurationLabel(new Integer[]{6*3}, mtsd);
		half = LabelTools.createDurationLabel(new Integer[]{16*3}, mtsd);

		paths = CLInterface.getPaths(true);
		String ep = paths.get("ENCODINGS_PATH");
		String epj = paths.get("ENCODINGS_PATH_JOSQUINTAB");
		String mp = paths.get("MIDI_PATH");
		String mpj = paths.get("MIDI_PATH_JOSQUINTAB");
		String td = "test/5vv/";
//		String mpJos = PathTools.getPathString(Arrays.asList(mpj));
//		String epJos = PathTools.getPathString(Arrays.asList(epj));
//		String s = MEIExport.rootDir;
///		String mp = p + Path.MIDI_PATH; // = F:/research/data/annotated/MIDI/
///		String ep = p + Path.ENCODINGS_PATH; // = F:/research/data/annotated/encodings/
///		String mpJos = p + Path.MIDI_PATH_JOSQUINTAB; // = F:/research/data/annotated/josquintab/MIDI/
///		String epJos = p + Path.ENCODINGS_PATH_JOSQUINTAB; // = F:/research/data/annotated/josquintab/tab/
		
		encodingTestpiece = new File(CLInterface.getPathString(
			Arrays.asList(ep, td)) + "testpiece.tbp"
		);
		encodingTestGetMeterInfo = new File(CLInterface.getPathString(
			Arrays.asList(ep, td)) + "test_get_meter_info.tbp"
		);
//		encodingMemorEsto = new File(jtpTab + "4465_33-34_memor_esto-2.tbp");
//		encodingQuiHabitat = new File(jtpTab + "5264_13_qui_habitat_in_adjutorio_desprez-2.tbp");
//		encodingPreterRerum = new File(jtpTab + "5694_03_motet_praeter_rerum_seriem_josquin-2.tbp");
//		encodingInExitu = new File(jtpTab + "5263_12_in_exitu_israel_de_egipto_desprez-3.tbp");
		encodingLasOn = new File(CLInterface.getPathString(
			Arrays.asList(ep, "thesis-int", "4vv")) + "phalese-1563_12-las_on.tbp"
		);
		encodingElslein = new File(CLInterface.getPathString(
			Arrays.asList(ep, "thesis-int", "3vv")) + "judenkuenig-1523_2-elslein_liebes.tbp"
		);
		encodingHerrGott = new File(CLInterface.getPathString(
			Arrays.asList(ep, "thesis-int", "4vv")) + "ochsenkun-1558_5-herr_gott.tbp"
		);
		midiLasOn = new File(CLInterface.getPathString(
			Arrays.asList(mp, "thesis-int", "4vv")) + "phalese-1563_12-las_on.mid"
		);
		midiElslein = new File(CLInterface.getPathString(
			Arrays.asList(mp, "thesis-int","3vv")) + "judenkuenig-1523_2-elslein_liebes.mid"
		);
		midiHerrGott = new File(CLInterface.getPathString(
			Arrays.asList(mp, "thesis-int", "4vv")) + "ochsenkun-1558_5-herr_gott.mid"
		);
//		midiBWV846 = new File(s + "data/annotated/MIDI/bach-WTC/thesis/4vv/" + "bach-WTC1-fuga_12-BWV_857.mid");

		midiTestpiece = new File(CLInterface.getPathString(
			Arrays.asList(mp, td)) + "testpiece.mid"
		);
		midiTestGetMeterKeyInfo = new File(CLInterface.getPathString(
			Arrays.asList(mp, td)) + "test_get_meter_key_info.mid"
		);
//		midiTestGetMeterKeyInfoDiminuted = new File(mp + "test/" + "test_get_meter_key_info_diminuted.mid");
//		midiTestGetMeterKeyInfoDiminutedNoAnacrusis = new File(s + "data/annotated/MIDI/test/" + "test_get_meter_key_info_diminuted_no_anacrusis.mid");
//		midiMemorEsto = new File(jtp + "Jos1714-Memor_esto_verbi_tui-166-325.mid");
//		midiQuiHabitat = new File(jtp + "Jos1807-Qui_habitat_in_adjutorio_altissimi-156-282.mid");
//		midiPreterRerum = new File(jtp + "Jos2411-Preter_rerum_seriem-88-185.mid");
//		midiInExitu = new File(jtp + "Jos1704-In_exitu_Israel_de_Egypto-281-401.mid");
	}


	@After
	public void tearDown() throws Exception {
	}


	private List<List<List<Double>>> getChordVoiceLabels(boolean isTablatureCase) {
		if (isTablatureCase) {
			return Arrays.asList(
				Arrays.asList(v3, v2, v1, v0),
				Arrays.asList(v3, v2, v0, v1),
				Arrays.asList(v3),
				Arrays.asList(v4, v3, v2, v01),
				Arrays.asList(v4),
				Arrays.asList(v4, v3, v2, v1, v0),
				Arrays.asList(v4, v2, v0, v1),
				Arrays.asList(v2, v0),
				Arrays.asList(v3, v2, v1, v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v3, v2, v1, v0)
			);
		}
		else {
			return Arrays.asList(
				Arrays.asList(v3, v2, v1, v0),
				Arrays.asList(v3, v2, v1, v0),
				Arrays.asList(v3),
				Arrays.asList(v4, v3, v2, v0, v1),
				Arrays.asList(v4),
				Arrays.asList(v4, v3, v2, v1, v0),
				Arrays.asList(v4, v2, v0, v1),
				Arrays.asList(v2, v0),
				Arrays.asList(v3, v2, v1, v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v0),
				Arrays.asList(v3, v2, v1, v0)
			);
		}
	}


	private List<List<Integer>> getVoiceAssignments(boolean isTablatureCase) {
		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();
		if (isTablatureCase) {
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{2, 3, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 3, 2, 1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));	 
		}
		else {
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 4, 2, 1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
			voiceAssignments.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
			voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
		}
		return voiceAssignments;
	}


//	private List<List<Integer>> getVoiceAssignmentsNonTab() {
//		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();
//		voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{3, 4, 2, 1, 0}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
//		voiceAssignments.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
//		return voiceAssignments;
//	}


	static public List<Note> getUnhandledNotesFromPiece(Piece p, String piece) {
		List<Note> notes = new ArrayList<>();
		if (piece.equals("testpiece")) {
			NotationSystem ns = p.getScore();
			// Chord 0
			notes.add(ns.get(3).get(0).get(0).get(0)); // D3
			notes.add(ns.get(2).get(0).get(0).get(0)); // A3
			notes.add(ns.get(1).get(0).get(0).get(0)); // F4
			notes.add(ns.get(0).get(0).get(0).get(0)); // A4
			// Chord 1
			notes.add(ns.get(3).get(0).get(1).get(0)); // A2
			notes.add(ns.get(2).get(0).get(1).get(0)); // A3
			notes.add(ns.get(1).get(0).get(1).get(0)); // A4
			notes.add(ns.get(0).get(0).get(1).get(0)); // C5
			// Chord 2
			notes.add(ns.get(3).get(0).get(2).get(0)); // C3
			// Chord 3
			notes.add(ns.get(4).get(0).get(0).get(0)); // B2
			notes.add(ns.get(3).get(0).get(3).get(0)); // D3
			notes.add(ns.get(2).get(0).get(2).get(0)); // B3
			notes.add(ns.get(1).get(0).get(2).get(0)); // F4
			notes.add(ns.get(0).get(0).get(2).get(0)); // F4
			// Chord 4
			notes.add(ns.get(4).get(0).get(1).get(0)); // A2
			// Chord 5
			notes.add(ns.get(4).get(0).get(2).get(0)); // A2
			notes.add(ns.get(3).get(0).get(4).get(0)); // A3
			notes.add(ns.get(2).get(0).get(3).get(0)); // A3
			notes.add(ns.get(1).get(0).get(3).get(0)); // C4
			notes.add(ns.get(0).get(0).get(3).get(0)); // A4
			// Chord 6
			notes.add(ns.get(4).get(0).get(3).get(0)); // A2
			notes.add(ns.get(2).get(0).get(4).get(0)); // C4
			notes.add(ns.get(0).get(0).get(4).get(0)); // E4
			notes.add(ns.get(1).get(0).get(4).get(0)); // A4
			// Chord 7
			notes.add(ns.get(2).get(0).get(5).get(0)); // B3
			notes.add(ns.get(0).get(0).get(5).get(0)); // G#4
			// Chord 8
			notes.add(ns.get(3).get(0).get(5).get(0)); // A2
			notes.add(ns.get(2).get(0).get(6).get(0)); // A3
			notes.add(ns.get(1).get(0).get(5).get(0)); // E4
			notes.add(ns.get(0).get(0).get(6).get(0)); // A4
			// Chord 9-14
			notes.add(ns.get(0).get(0).get(7).get(0)); // G#4
			notes.add(ns.get(0).get(0).get(8).get(0)); // A4
			notes.add(ns.get(0).get(0).get(9).get(0)); // G#4
			notes.add(ns.get(0).get(0).get(10).get(0)); // F#4
			notes.add(ns.get(0).get(0).get(11).get(0)); // G#4
			notes.add(ns.get(0).get(0).get(12).get(0)); // A4
			// Chord 15
			notes.add(ns.get(3).get(0).get(6).get(0)); // A2
			notes.add(ns.get(2).get(0).get(7).get(0)); // A3
			notes.add(ns.get(1).get(0).get(6).get(0)); // E4
			notes.add(ns.get(0).get(0).get(13).get(0)); // A4
		}
		else if (piece.equals("testGetMeterKeyInfo")) {
			NotationSystem ns = p.getScore();
			// Bar 1
			notes.add(ns.get(0).get(0).get(0).get(0));
			notes.add(ns.get(0).get(0).get(1).get(0));
			notes.add(ns.get(0).get(0).get(2).get(0));
			// Bar 2
			notes.add(ns.get(1).get(0).get(0).get(0));
			notes.add(ns.get(0).get(0).get(3).get(0));
			notes.add(ns.get(0).get(0).get(4).get(0));
			notes.add(ns.get(1).get(0).get(1).get(0));
			notes.add(ns.get(0).get(0).get(5).get(0));
			// Bar 3
			notes.add(ns.get(1).get(0).get(2).get(0));
			notes.add(ns.get(0).get(0).get(6).get(0));
			notes.add(ns.get(0).get(0).get(7).get(0));
			notes.add(ns.get(0).get(0).get(8).get(0));
			notes.add(ns.get(0).get(0).get(9).get(0));
			notes.add(ns.get(0).get(0).get(10).get(0));
			notes.add(ns.get(0).get(0).get(11).get(0));
			notes.add(ns.get(0).get(0).get(12).get(0));
			notes.add(ns.get(1).get(0).get(3).get(0));
			notes.add(ns.get(0).get(0).get(13).get(0));
			// Bar 4
			notes.add(ns.get(1).get(0).get(4).get(0));
			notes.add(ns.get(0).get(0).get(14).get(0));
			notes.add(ns.get(0).get(0).get(15).get(0));
			notes.add(ns.get(0).get(0).get(16).get(0));
			notes.add(ns.get(0).get(0).get(17).get(0));
			notes.add(ns.get(1).get(0).get(5).get(0));
			notes.add(ns.get(0).get(0).get(18).get(0));
			// Bar 5
			notes.add(ns.get(0).get(0).get(19).get(0));
			notes.add(ns.get(0).get(0).get(20).get(0));
			notes.add(ns.get(0).get(0).get(21).get(0));
			notes.add(ns.get(1).get(0).get(6).get(0));
			notes.add(ns.get(0).get(0).get(22).get(0));
			// Bar 6
			notes.add(ns.get(1).get(0).get(7).get(0));
			notes.add(ns.get(0).get(0).get(23).get(0));
			notes.add(ns.get(1).get(0).get(8).get(0));
			notes.add(ns.get(0).get(0).get(24).get(0));
			// Bar 7
			notes.add(ns.get(0).get(0).get(25).get(0));
			notes.add(ns.get(0).get(0).get(26).get(0));
			notes.add(ns.get(0).get(0).get(27).get(0));
			notes.add(ns.get(0).get(0).get(28).get(0));
			notes.add(ns.get(0).get(0).get(29).get(0));
			notes.add(ns.get(0).get(0).get(30).get(0));
			notes.add(ns.get(1).get(0).get(9).get(0));
			notes.add(ns.get(0).get(0).get(31).get(0));
			// Bar 8
			notes.add(ns.get(0).get(0).get(32).get(0));
			notes.add(ns.get(0).get(0).get(33).get(0));
			notes.add(ns.get(0).get(0).get(34).get(0));
			notes.add(ns.get(0).get(0).get(35).get(0));
			// Bar 9
			notes.add(ns.get(1).get(0).get(10).get(0));
			notes.add(ns.get(0).get(0).get(36).get(0));
			notes.add(ns.get(0).get(0).get(37).get(0));
			notes.add(ns.get(0).get(0).get(38).get(0));
			notes.add(ns.get(0).get(0).get(39).get(0));
			notes.add(ns.get(1).get(0).get(11).get(0));
			notes.add(ns.get(0).get(0).get(40).get(0));
		}
		return notes;
	}


	private List<Note> getHandledNotesFromPiece(Piece p, String piece, boolean isTablatureCase) {
		List<Note> handled = new ArrayList<>();
		List<Note> unhandled = new ArrayList<Note>(getUnhandledNotesFromPiece(p, piece));
		unhandled.forEach(n -> handled.add(n));
		if (piece.equals("testpiece")) {
			if (isTablatureCase) {
				// Handle SNU 
				handled.remove(12);
				// Handle CC
				Collections.swap(handled, 6, 7);
			}
			else {
				// Handle unisons
				Collections.swap(handled, 12, 13);
			}
		}
		return handled;
	}


//	private NoteSequence getNoteSequence(Piece p, String piece) {
//		NoteSequence noteSeq = new NoteSequence(new NoteTimePitchComparator());
//		if (piece.equals("testpiece")) {
//			NotationSystem ns = p.getScore();
//			// Chord 0
//			noteSeq.add(ns.get(3).get(0).get(0).get(0)); // D3
//			noteSeq.add(ns.get(2).get(0).get(0).get(0)); // A3
//			noteSeq.add(ns.get(1).get(0).get(0).get(0)); // F4
//			noteSeq.add(ns.get(0).get(0).get(0).get(0)); // A4
//			// Chord 1
//			noteSeq.add(ns.get(3).get(0).get(1).get(0)); // A2
//			noteSeq.add(ns.get(2).get(0).get(1).get(0)); // A3
//			noteSeq.add(ns.get(1).get(0).get(1).get(0)); // A4
//			noteSeq.add(ns.get(0).get(0).get(1).get(0)); // C5
//			// Chord 2
//			noteSeq.add(ns.get(3).get(0).get(2).get(0)); // C3
//			// Chord 3
//			noteSeq.add(ns.get(4).get(0).get(0).get(0)); // B2
//			noteSeq.add(ns.get(3).get(0).get(3).get(0)); // D3
//			noteSeq.add(ns.get(2).get(0).get(2).get(0)); // B3
//			noteSeq.add(ns.get(1).get(0).get(2).get(0)); // F4
//			noteSeq.add(ns.get(0).get(0).get(2).get(0)); // F4
//			// Chord 4
//			noteSeq.add(ns.get(4).get(0).get(1).get(0)); // A2
//			// Chord 5
//			noteSeq.add(ns.get(4).get(0).get(2).get(0)); // A2
//			noteSeq.add(ns.get(3).get(0).get(4).get(0)); // A3
//			noteSeq.add(ns.get(2).get(0).get(3).get(0)); // A3
//			noteSeq.add(ns.get(1).get(0).get(3).get(0)); // C4
//			noteSeq.add(ns.get(0).get(0).get(3).get(0)); // A4
//			// Chord 6
//			noteSeq.add(ns.get(4).get(0).get(3).get(0)); // A2
//			noteSeq.add(ns.get(2).get(0).get(4).get(0)); // C4
//			noteSeq.add(ns.get(0).get(0).get(4).get(0)); // E4
//			noteSeq.add(ns.get(1).get(0).get(4).get(0)); // A4
//			// Chord 7
//			noteSeq.add(ns.get(2).get(0).get(5).get(0)); // B3
//			noteSeq.add(ns.get(0).get(0).get(5).get(0)); // G#4
//			// Chord 8
//			noteSeq.add(ns.get(3).get(0).get(5).get(0)); // A2
//			noteSeq.add(ns.get(2).get(0).get(6).get(0)); // A3
//			noteSeq.add(ns.get(1).get(0).get(5).get(0)); // E4
//			noteSeq.add(ns.get(0).get(0).get(6).get(0)); // A4
//			// Chord 9-14
//			noteSeq.add(ns.get(0).get(0).get(7).get(0)); // G#4
//			noteSeq.add(ns.get(0).get(0).get(8).get(0)); // A4
//			noteSeq.add(ns.get(0).get(0).get(9).get(0)); // G#4
//			noteSeq.add(ns.get(0).get(0).get(10).get(0)); // F#4
//			noteSeq.add(ns.get(0).get(0).get(11).get(0)); // G#4
//			noteSeq.add(ns.get(0).get(0).get(12).get(0)); // A4
//			// Chord 15
//			noteSeq.add(ns.get(3).get(0).get(6).get(0)); // A2
//			noteSeq.add(ns.get(2).get(0).get(7).get(0)); // A3
//			noteSeq.add(ns.get(1).get(0).get(6).get(0)); // E4
//			noteSeq.add(ns.get(0).get(0).get(13).get(0)); // A4
//
//			// The NoteSequence orders equal-pitch notes randomly; make sure that
//			// both pairs are ordered correctly
//			if (noteSeq.get(12).getMetricDuration().equals(new Rational(1, 4)) &&
//				noteSeq.get(13).getMetricDuration().equals(new Rational(1, 8))) {
//				noteSeq.swapNotes(12, 13);
//			}
//			if (noteSeq.get(16).getMetricDuration().equals(new Rational(1, 4)) &&
//				noteSeq.get(17).getMetricDuration().equals(new Rational(1, 2))) {
//				noteSeq.swapNotes(16, 17);
//			}
//		}
//		return noteSeq;
//	}


	@Test
	public void testMakeMeterInfo() {
		// Tablature case (always not diminuted)
		Transcription t1 = new Transcription();
		Tablature tab1 = new Tablature(encodingTestpiece);
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab1, null);
		t1.setName();
		Transcription t2 = new Transcription();
		Tablature tab2 = new Tablature(encodingTestGetMeterInfo);
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), tab2, null);
		t2.setName();
		// Non-tablature case 
		// a. Not diminuted
		Transcription t3 = new Transcription();
		t3.setType(Type.FROM_FILE);
		t3.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t3.setName();
		Transcription t4 = new Transcription();
		t4.setType(Type.FROM_FILE);
		t4.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), null, null);
		t4.setName();
		// b. Diminuted
		Transcription t5 = new Transcription();
		Tablature tab5 = new Tablature(encodingTestGetMeterInfo);
		t5.setType(Type.FROM_FILE);
		t5.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), null, tab5.getMeterInfo());
		t5.setName();

		List<Integer[]> expected = Arrays.asList(
			new Integer[]{2, 2, 1, 3, 0, 1},
			//
			new Integer[]{3, 4, 1, 1, 0, 1},
			new Integer[]{2, 1, 2, 3, 3, 4},
			new Integer[]{3, 1, 4, 5, 19, 4},
			new Integer[]{2, 2, 6, 7, 43, 4},
			new Integer[]{5, 16, 8, 8, 51, 4},
			new Integer[]{2, 4, 9, 9, 209, 16},
			//
			new Integer[]{2, 2, 1, 3, 0, 1},
			//
			new Integer[]{3, 4, 1, 1, 0, 1},
			new Integer[]{2, 1, 2, 3, 3, 4},
			new Integer[]{3, 1, 4, 5, 19, 4},
			new Integer[]{2, 2, 6, 7, 43, 4},
			new Integer[]{5, 16, 8, 8, 51, 4},
			new Integer[]{2, 4, 9, 9, 209, 16},
			//
			new Integer[]{3, 8, 1, 1, 0, 1},
			new Integer[]{2, 2, 2, 3, 3, 8},
			new Integer[]{3, 4, 4, 5, 19, 8},
			new Integer[]{2, 2, 6, 7, 31, 8},
			new Integer[]{5, 16, 8, 8, 47, 8},
			new Integer[]{2, 2, 9, 9, 99, 16}
		);

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(t1.makeMeterInfo());
		actual.addAll(t2.makeMeterInfo());
		actual.addAll(t3.makeMeterInfo());
		actual.addAll(t4.makeMeterInfo());
		actual.addAll(t5.makeMeterInfo());
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testMakeKeyInfo() {
		// Tablature case (always not diminuted, always transposed)
		Transcription t1 = new Transcription();
		Tablature tab1 = new Tablature(encodingTestpiece);
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab1, null);
		t1.setName();
		t1.setMeterInfo();
		Transcription t2 = new Transcription();
		Tablature tab2 = new Tablature(encodingTestGetMeterInfo);
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), tab2, null);
		t2.setName();
		t2.setMeterInfo();
		// Non-tablature case (always not transposed)
		// a. Not diminuted
		Transcription t3 = new Transcription();
		t3.setType(Type.FROM_FILE);
		t3.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t3.setName();
		t3.setMeterInfo();
		Transcription t4 = new Transcription();
		t4.setType(Type.FROM_FILE);
		t4.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), null, null);
		t4.setName();
		t4.setMeterInfo();
		// b. Diminuted
		Transcription t5 = new Transcription();
		Tablature tab5 = new Tablature(encodingTestGetMeterInfo);
		t5.setType(Type.FROM_FILE);
		t5.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), null, tab5.getMeterInfo());
		t5.setName();
		t5.setMeterInfo();

		List<Integer[]> expected = Arrays.asList(
			new Integer[]{0, 1, 1, 3, 0, 1},
//			new Integer[]{-2, 1, 1, 3, 0, 1}, // A minor --> G minor (because of A-tuning)
			//
			new Integer[]{0, 0, 1, 3, 0, 1}, // C major 
			new Integer[]{3, 1, 4, 5, 19, 4}, // F# minor
			new Integer[]{-2, 0, 6, 7, 43, 4}, // Bb major
			new Integer[]{1, 1, 8, 9, 51, 4}, // E minor
			//
			new Integer[]{0, 1, 1, 3, 0, 1}, // A minor
			//
			new Integer[]{0, 0, 1, 3, 0, 1}, // C major
			new Integer[]{3, 1, 4, 5, 19, 4}, // F# minor
			new Integer[]{-2, 0, 6, 7, 43, 4}, // Bb major
			new Integer[]{1, 1, 8, 9, 51, 4}, // E minor
			//
			new Integer[]{0, 0, 1, 3, 0, 1}, // C major
			new Integer[]{3, 1, 4, 5, 19, 8}, // F# minor
			new Integer[]{-2, 0, 6, 7, 31, 8}, // Bb major
			new Integer[]{1, 1, 8, 9, 47, 8} // E minor
		);

		List<Integer[]> actual = t1.makeKeyInfo();
		actual.addAll(t2.makeKeyInfo());
		actual.addAll(t3.makeKeyInfo());
		actual.addAll(t4.makeKeyInfo());
		actual.addAll(t5.makeKeyInfo());
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


//	public void testMakeTaggedNotes() {
//		// Tablature case
//		Transcription t1 = new Transcription();
//		Tablature tab = new Tablature(encodingTestpiece, true);
//		t1.setPiece(MIDIImport.importMidiFile(midiTestpiece), tab, null, Type.GROUND_TRUTH);
//		t1.setName();
//		t1.setMeterInfo();
//		t1.setKeyInfo();
//		// Non-tablature case
//		Transcription t2 = new Transcription();
//		t2.setPiece(MIDIImport.importMidiFile(midiTestpiece), null, null, Type.GROUND_TRUTH);
//		t2.setName();
//		t2.setMeterInfo();
//		t2.setKeyInfo();
//		
//		List<List<TaggedNote>> expected = new ArrayList<>(); 
//		List<TaggedNote> expected1 = new ArrayList<>();
//		List<Note> unhandled1 = getUnhandledNotesFromPiece(t1.getPiece(), "testpiece");
//		unhandled1.forEach(n -> expected1.add(t1.new TaggedNote(n)));
//		// Handle SNU
//		expected1.set(13, t1.new TaggedNote(unhandled1.get(13), new Integer[]{0, 1}, 
//			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, -1));
//		expected1.remove(12);
//		// Handle CC
//		Collections.swap(expected1, 6, 7);
//		expected.add(expected1);
//		List<TaggedNote> expected2 = new ArrayList<>();
//		List<Note> unhandled2 = getUnhandledNotesFromPiece(t2.getPiece(), "testpiece");
//		unhandled2.forEach(n -> expected2.add(t2.new TaggedNote(n)));
//		// Handle unisons
//		expected2.set(12, t2.new TaggedNote(unhandled2.get(13), new Integer[]{0, 1}, 
//			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, 13));
//		expected2.set(13, t2.new TaggedNote(unhandled2.get(12), new Integer[]{0, 1}, 
//			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, 12));
//		expected2.set(16, t2.new TaggedNote(unhandled2.get(16), new Integer[]{3, 2}, 
//			new Rational[]{new Rational(1, 2), new Rational(1, 4)}, 17));
//		expected2.set(17, t2.new TaggedNote(unhandled2.get(17), new Integer[]{3, 2}, 
//			new Rational[]{new Rational(1, 2), new Rational(1, 4)}, 16));
//		expected.add(expected2);
//
//		List<List<TaggedNote>> actual = new ArrayList<>();
//		actual.add(t1.makeTaggedNotes(tab));
//		actual.add(t2.makeTaggedNotes(null));
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}
//	}


	@Test
	public void testMakeUnhandledNotes() {
		// Tablature/non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();

		List<Note> expected = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");

		List<Note> actual = t.makeUnhandledNotes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testFindVoice() {
		// Tablature/non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();

		List<Integer> expected = Arrays.asList(new Integer[]{
			// Chord 0
			3, 2, 1, 0,
			// Chord 1 (course crossing voices 0-1 unhandled)
			3, 2, 1, 0, 
			// Chord 2
			3,
			// Chord 3 (SNU voices 0-1 unhandled)
			4, 3, 2, 1, 0, 
			// Chord 4
			4,
			// Chord 5 (unison voices 3-2 correct by default)	
			4, 3, 2, 1, 0,
			// Chord 6 (voice crossing voices 0-1)
			4, 2, 0, 1, 
			// Chord 7
			2, 0,
			// Chord 8
			3, 2, 1, 0,
			// Chord 9-14	
			0, 0, 0, 0, 0, 0,
			// Chord 15
			3, 2, 1, 0
		});

		List<Integer> actual = new ArrayList<>();		
		for (Note n : t.makeUnhandledNotes()) {
			actual.add(t.findVoice(n));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetPitchFrequency() {
		Note c4 = ScorePiece.createNote(60, new Rational(1, 4), new Rational(1, 4), -1, null);
		Note e4 = ScorePiece.createNote(64, new Rational(1, 4), new Rational(1, 4), -1, null);
		Note g4 = ScorePiece.createNote(67, new Rational(1, 4), new Rational(1, 4), -1, null);
		Note c5 = ScorePiece.createNote(72, new Rational(1, 4), new Rational(1, 4), -1, null);
		Note e5 = ScorePiece.createNote(76, new Rational(1, 4), new Rational(1, 4), -1, null);

		List<List<Note>> chords = Arrays.asList(
			Arrays.asList(c4, e4, g4, c5, e5),
			Arrays.asList(c4, e4, g4, g4, c5),
			Arrays.asList(c4, g4, g4, g4, c5),
			Arrays.asList(g4, g4, g4, g4, g4)
		);

		List<List<Integer>> expected = Arrays.asList(
			Arrays.asList(1, 1, 1, 1, 1),
			Arrays.asList(1, 1, 2, 2, 1),
			Arrays.asList(1, 3, 3, 3, 1),
			Arrays.asList(5, 5, 5, 5, 5)
		);

		List<List<Integer>> actual = new ArrayList<>();
		for (List<Note> l : chords) {
			actual.add(Transcription.getPitchFrequency(l));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetChordsFromNotes() {
		// Tablature/non-tablature case
		Transcription t = new Transcription(midiTestpiece);
//		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, Type.GROUND_TRUTH);

		List<List<Note>> expected = new ArrayList<>();
		List<Integer> chordSizes = 
			Arrays.asList(new Integer[]{4, 4, 1, 5, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});
		List<Note> notes = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");
		int start = 0;
		for (int chordSize : chordSizes) {
			List<Note> currChord = new ArrayList<>();
			for (int j = start; j < start + chordSize; j++) {
				currChord.add(notes.get(j));
			}
			expected.add(currChord);
			start += chordSize;
		}

		List<List<Note>> actual = Transcription.getChordsFromNotes(notes);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testHandleSNUs() {
		// Tablature case
		Transcription t = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		List<Note> argNotes = t.makeUnhandledNotes();

		List<TaggedNote> expected = new ArrayList<>();
		List<Note> unhandled = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");
		unhandled.forEach(n -> expected.add(t.new TaggedNote(n)));
		// Handle SNU
		expected.set(13, t.new TaggedNote(unhandled.get(13), new Integer[]{0, 1}, 
			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, -1));
		expected.remove(12);

		List<TaggedNote> actual = t.handleSNUs(argNotes, tab);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetSNUInfo() {
		// Tablature case
		Transcription t = new Transcription(midiTestpiece, encodingTestpiece);
//		Transcription t = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
//		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, Type.GROUND_TRUTH);
//		t.setName();
//		t.setMeterInfo();
//		t.setKeyInfo();
//		List<Note> argNotes = t.makeUnhandledNotes();
		List<Note> argNotes = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");

		List<Integer[][]> expected = new ArrayList<Integer[][]>(Collections.nCopies(16, null));
		expected.set(3, new Integer[][]{{65, 3, 4}});

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		List<List<TabSymbol>> tablatureChords = tab.getChords();
		List<List<Note>> chords = Transcription.getChordsFromNotes(argNotes);
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Transcription.getSNUInfo(chords.get(i), tablatureChords.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j].length, actual.get(i)[j].length);
					for (int k = 0; k < expected.get(i)[j].length; k++) {
						assertEquals(expected.get(i)[j][k], actual.get(i)[j][k]);
					}
				}
			}
		}
	}


	@Test
	public void testHandleCourseCrossings() {
		// Tablature case
		Transcription t = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		List<Note> argNotes = t.makeUnhandledNotes();
		List<TaggedNote> argTaggedNotes = t.handleSNUs(argNotes, tab);

		List<TaggedNote> expected = new ArrayList<>();
		List<Note> unhandled = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");
		unhandled.forEach(n -> expected.add(t.new TaggedNote(n)));
		// Handle SNU 
		expected.set(13, t.new TaggedNote(unhandled.get(13), new Integer[]{0, 1}, 
			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, -1));
		expected.remove(12);
		// Handle CC
		Collections.swap(expected, 6, 7);

		List<TaggedNote> actual = t.handleCourseCrossings(argTaggedNotes, tab);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testCheckAlignment() { // TODO remove?
		// Tablature case
		Tablature tab = new Tablature(encodingTestpiece);
		
		// One note lacking in Transcription
		Transcription t1 = new Transcription();
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t1.setName();
		t1.setMeterInfo();
		t1.setKeyInfo();
		List<Note> argNotes = t1.makeUnhandledNotes();
		List<TaggedNote> argTaggedNotes = t1.handleSNUs(argNotes, tab);
		argTaggedNotes = t1.handleCourseCrossings(argTaggedNotes, tab);
		argTaggedNotes.remove(9);
		
		// Pitch misalignment
		Transcription t2 = new Transcription();
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t2.setName();
		t2.setMeterInfo();
		t2.setKeyInfo();
		List<Note> argNotes2 = t2.makeUnhandledNotes();
		List<TaggedNote> argTaggedNotes2 = t2.handleSNUs(argNotes2, tab);
		argTaggedNotes2 = t2.handleCourseCrossings(argTaggedNotes2, tab);
		argTaggedNotes2.get(9).getNote().setPitch(37); // should be 45
		
		// Onset time misalignment
		Transcription t3 = new Transcription();
		t3.setType(Type.FROM_FILE);
		t3.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t3.setName();
		t3.setMeterInfo();
		t3.setKeyInfo();
		List<Note> argNotes3 = t3.makeUnhandledNotes();
		List<TaggedNote> argTaggedNotes3 = t3.handleSNUs(argNotes3, tab);
		argTaggedNotes3 = t3.handleCourseCrossings(argTaggedNotes3, tab);
		argTaggedNotes3.get(9).getNote().getScoreNote().setMetricTime(new Rational(6, 4)); // should be 5/4
		
		// Correctly aligned
		Transcription t4 = new Transcription();
		t4.setType(Type.FROM_FILE);
		t4.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t4.setName();
		t4.setMeterInfo();
		t4.setKeyInfo();
		List<Note> argNotes4 = t4.makeUnhandledNotes();
		List<TaggedNote> argTaggedNotes4 = t4.handleSNUs(argNotes4, tab);
		argTaggedNotes4 = t4.handleCourseCrossings(argTaggedNotes4, tab);

		List<Boolean> expected = Arrays.asList(new Boolean[]{false, false, false, true});

		List<List<TaggedNote>> allTaggedNotes = new ArrayList<>();
		allTaggedNotes.add(argTaggedNotes);
		allTaggedNotes.add(argTaggedNotes2);
		allTaggedNotes.add(argTaggedNotes3);
		allTaggedNotes.add(argTaggedNotes4);
		
		List<Boolean> actual = new ArrayList<Boolean>();
		for (List<TaggedNote> tn : allTaggedNotes) {
			actual.add(Transcription.checkAlignment(tn, tab));
		}
		System.out.println(Transcription.alignmentCheck);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual .get(i));
		}
	}


	@Test
	public void testHandleUnisons() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		List<Note> argNotes = t.makeUnhandledNotes();

		List<TaggedNote> expected = new ArrayList<>();
		List<Note> unhandled = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");
		unhandled.forEach(n -> expected.add(t.new TaggedNote(n)));
		// Handle unisons
		expected.set(12, t.new TaggedNote(unhandled.get(13), new Integer[]{0, 1}, 
			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, 13));
		expected.set(13, t.new TaggedNote(unhandled.get(12), new Integer[]{0, 1}, 
			new Rational[]{new Rational(1, 4), new Rational(1, 8)}, 12));
		expected.set(16, t.new TaggedNote(unhandled.get(16), new Integer[]{3, 2}, 
			new Rational[]{new Rational(1, 2), new Rational(1, 4)}, 17));
		expected.set(17, t.new TaggedNote(unhandled.get(17), new Integer[]{3, 2}, 
			new Rational[]{new Rational(1, 2), new Rational(1, 4)}, 16));

		List<TaggedNote> actual = t.handleUnisons(argNotes);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetUnisonInfo() {
		// Non-tablature case
		Transcription t = new Transcription(midiTestpiece);
//		Transcription t = new Transcription();
//		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, Type.GROUND_TRUTH);
//		t.setName();
//		t.setMeterInfo();
//		t.setKeyInfo();
		List<Note> argNotes = getUnhandledNotesFromPiece(t.getScorePiece(), "testpiece");
//		List<Note> argNotes = t.makeUnhandledNotes();

		List<Integer[][]> expected = new ArrayList<Integer[][]>(Collections.nCopies(16, null));
		expected.set(3, new Integer[][]{{65, 3, 4}});
		expected.set(5, new Integer[][]{{57, 1, 2}});

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		List<List<Note>> chords = Transcription.getChordsFromNotes(argNotes);
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Transcription.getUnisonInfo(chords.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j].length, actual.get(i)[j].length);
					for (int k = 0; k < expected.get(i)[j].length; k++) {
						assertEquals(expected.get(i)[j][k], actual.get(i)[j][k]);
					}
				}
			}
		}
	}


	@Test
	public void testMakeNotes() {
		// Tablature case
		Transcription t1 = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t1.setName();
		t1.setMeterInfo();
		t1.setKeyInfo();
		t1.setTaggedNotes(tab);
		// Non-tablature case
		Transcription t2 = new Transcription();
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t2.setName();
		t2.setMeterInfo();
		t2.setKeyInfo();
		t2.setTaggedNotes(null);

		List<List<Note>> expected = new ArrayList<>(); 
		List<Note> expected1 = getHandledNotesFromPiece(t1.getScorePiece(), "testpiece", true);
		expected.add(expected1);
		List<Note> expected2 = getHandledNotesFromPiece(t2.getScorePiece(), "testpiece", false);
		expected.add(expected2);

		List<List<Note>> actual = new ArrayList<>();
		actual.add(t1.makeNotes());
		actual.add(t2.makeNotes());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testMakeVoiceLabels() {
		// Tablature case
		Transcription t1 = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t1.setName();
		t1.setMeterInfo();
		t1.setKeyInfo();
		t1.setTaggedNotes(tab);
		t1.setNotes();
		t1.setChords();
		// Non-tablature case
		Transcription t2 = new Transcription();
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t2.setName();
		t2.setMeterInfo();
		t2.setKeyInfo();
		t2.setTaggedNotes(null);
		t2.setNotes();
		t1.setChords();

		List<List<List<Double>>> expected = new ArrayList<>();
		List<List<Double>> expected1 = new ArrayList<>();
		getChordVoiceLabels(true).forEach(ch -> expected1.addAll(ch));
		expected.add(expected1);

		List<List<Double>> expected2 = new ArrayList<>();
		getChordVoiceLabels(false).forEach(ch -> expected2.addAll(ch));
		expected.add(expected2);

		List<List<List<Double>>> actual = new ArrayList<>();
		actual.add(t1.makeVoiceLabels(true));
		actual.add(t2.makeVoiceLabels(false));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k), 1.0E-6);
				}
			}
		}		
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeChordVoiceLabels() {
		// Tablature case
		Transcription t1 = new Transcription(midiTestpiece, encodingTestpiece);
//		Transcription t1 = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
//		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, Type.GROUND_TRUTH);
//		t1.setName();
//		t1.setMeterInfo();
//		t1.setKeyInfo();
//		t1.setTaggedNotes(tab);
//		t1.setNotes();
//		t1.setChords();
//		t1.setVoiceLabels(null, true);
		// Non-tablature case
		Transcription t2 = new Transcription(midiTestpiece);
//		Transcription t2 = new Transcription();
//		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, Type.GROUND_TRUTH);
//		t2.setName();
//		t2.setMeterInfo();
//		t2.setKeyInfo();
//		t2.setTaggedNotes(null);
//		t2.setNotes();
//		t2.setChords();
//		t2.setVoiceLabels(null, false);

		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		List<List<List<Double>>> expected1 = getChordVoiceLabels(true);
		expected.addAll(expected1);
		List<List<List<Double>>> expected2 = getChordVoiceLabels(false);
		expected.addAll(expected2);

		List<List<List<Double>>> actual = 
			Transcription.makeChordVoiceLabels(t1.getVoiceLabels(), tab.getChords(), t1.getChords());
		actual.addAll(Transcription.makeChordVoiceLabels(t2.getVoiceLabels(), null, t2.getChords()));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
	}


	@Test
	public void testMakeDurationLabels() {
		// Tablature case
		Transcription t = new Transcription();
		Tablature tab = new Tablature(encodingTestpiece);
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(tab);
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, true);
		t.setChordVoiceLabels(tab);

		List<List<Double>> expected = Arrays.asList(
			quarter, quarter, quarter, quarter, 
			dottedEighth, quarter, quarter, eighth,
			sixteenth,
			eighth, quarter, quarter, eighthQuarterSNU,
			eighth,
			quarter, half, quarter, quarter, quarter,
			quarter, eighth, eighth, quarter,
			eighth, eighth,
			half, half, half, sixteenth,
			sixteenth,
			thirtysecond,
			thirtysecond,
			thirtysecond,
			thirtysecond,
			quarter,
			quarter, quarter, quarter, quarter
		);

		List<List<Double>> actual = t.makeDurationLabels();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeMinimumDurationLabels() {
		// Tablature case
		Tablature tab = new Tablature(encodingTestpiece);

		List<List<Double>> expected = Arrays.asList(
			quarter, quarter, quarter, quarter, 
			dottedEighth, dottedEighth, dottedEighth, dottedEighth,
			sixteenth,
			eighth, eighth, eighth, eighth,
			eighth,
			quarter, quarter, quarter, quarter, quarter,
			eighth, eighth, eighth, eighth,
			eighth, eighth,
			sixteenth, sixteenth, sixteenth, sixteenth,
			sixteenth,
			thirtysecond,
			thirtysecond,
			thirtysecond,
			thirtysecond,
			quarter,
			quarter, quarter, quarter, quarter
		);

		List<List<Double>> actual = Transcription.makeMinimumDurationLabels(tab, mtsd);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeVoicesSNU() {
		// Tablature case
		Transcription t1 = new Transcription();
		Tablature tab1 = new Tablature(encodingTestpiece);
		t1.setType(Type.FROM_FILE);
		t1.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), tab1, null);
		t1.setName();
		t1.setMeterInfo();
		t1.setKeyInfo();
		t1.setTaggedNotes(tab1);
		t1.setNotes();
		t1.setChords();
		t1.setVoiceLabels(null, true);
		t1.setChordVoiceLabels(tab1);
		t1.setDurationLabels(null);
		t1.setMinimumDurationLabels(tab1, mtsd);
		Transcription t2 = new Transcription();
		Tablature tab2 = new Tablature(encodingLasOn);
		t2.setType(Type.FROM_FILE);
		t2.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiLasOn)), tab2, null);
		t2.setName();
		t2.setMeterInfo();
		t2.setKeyInfo();
		t2.setTaggedNotes(tab2);
		t2.setNotes();
		t2.setChords();
		t2.setVoiceLabels(null, true);
		t2.setChordVoiceLabels(tab2);
		t2.setDurationLabels(null);
		t2.setMinimumDurationLabels(tab2, mtsd);

		List<Integer[]> expected = new ArrayList<Integer[]>();
		// a. For a piece with one CoD
		List<Integer[]> expected1 = 
			new ArrayList<>(Collections.nCopies(tab1.getBasicTabSymbolProperties().length, null));
		// CoD at metric position 2 1/4
		expected1.set(12, new Integer[]{0, 1});
		expected.addAll(expected1);
		// b. For a piece with multiple CoDs
		List<Integer[]> expected2 = 
			new ArrayList<>(Collections.nCopies(tab2.getBasicTabSymbolProperties().length, null));
		// CoD at metric position 7
		expected2.set(86, new Integer[]{3, 2});
		// CoD at metric position 7 1/4
		expected2.set(91, new Integer[]{1, 2});
		// CoD at metric position 14 3/4
		expected2.set(256, new Integer[]{3, 2});
		// CoD at metric position 17
		expected2.set(301, new Integer[]{3, 2});
		// CoD at metric position 17 1/4
		expected2.set(306, new Integer[]{1, 2});
		// CoD at metric position 21 1/2
		expected2.set(391, new Integer[]{3, 2});
		// CoD at metric position 23 1/2
		expected2.set(422, new Integer[]{3, 2});
		// CoD at metric position 29 1/2
		expected2.set(524, new Integer[]{1, 0});
		// CoD at metric position 33 3/4
		expected2.set(579, new Integer[]{2, 0});
		// CoD at metric position 35 1/4
		expected2.set(600, new Integer[]{2, 1});
		// CoD at metric position 42 1/4
		expected2.set(716, new Integer[]{2, 1});
		expected.addAll(expected2);

		List<Integer[]> actual = new ArrayList<Integer[]>();
		actual.addAll(t1.makeVoicesSNU());
		actual.addAll(t2.makeVoicesSNU());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testMakeVoicesUnison() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(null);
		// Make the second unison note pair an EDU note pair with duration 1/4
		TaggedNote tn16 = t.getTaggedNotes().get(16);
		TaggedNote tn17 = t.getTaggedNotes().get(17);
		Rational[] durations = new Rational[]{new Rational(1, 4), new Rational(1, 4)};
		t.getTaggedNotes().set(16, t.new TaggedNote(tn16.getNote(), tn16.getVoices(), 
			durations, tn16.getIndexOtherUnisonNote()));
		t.getTaggedNotes().set(17, t.new TaggedNote(tn17.getNote(), tn17.getVoices(), 
			durations, tn17.getIndexOtherUnisonNote()));
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, false);
		t.setChordVoiceLabels(null);

		List<Integer[]> expected = 
			new ArrayList<>(Collections.nCopies(t.getNotes().size(), null));
		expected.set(12, new Integer[]{0, 1, 13, 0});
		expected.set(13, new Integer[]{0, 1, 12, 0});
		expected.set(16, new Integer[]{3, 2, 17, 1});
		expected.set(17, new Integer[]{3, 2, 16, 1});

		List<Integer[]> actual = t.makeVoicesUnison();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testMakeVoicesEDU() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(null);
		// Make the second unison note pair an EDU note pair with duration 1/4
		TaggedNote tn16 = t.getTaggedNotes().get(16);
		TaggedNote tn17 = t.getTaggedNotes().get(17);
		Rational[] durations = new Rational[]{new Rational(1, 4), new Rational(1, 4)};
		t.getTaggedNotes().set(16, t.new TaggedNote(tn16.getNote(), tn16.getVoices(), 
			durations, tn16.getIndexOtherUnisonNote()));
		t.getTaggedNotes().set(17, t.new TaggedNote(tn17.getNote(), tn17.getVoices(), 
			durations, tn17.getIndexOtherUnisonNote()));
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, false);
		t.setChordVoiceLabels(null);
		t.setVoicesUnison();

		List<Integer[]> expected = 
			new ArrayList<>(Collections.nCopies(t.getNotes().size(), null));
		expected.set(16, new Integer[]{3, 2, 17});
		expected.set(17, new Integer[]{3, 2, 16});

		List<Integer[]> actual = t.makeVoicesEDU();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testMakeVoicesIDU() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(null);
		// Make the second unison note pair an EDU note pair with duration 1/4
		TaggedNote tn16 = t.getTaggedNotes().get(16);
		TaggedNote tn17 = t.getTaggedNotes().get(17);
		Rational[] durations = new Rational[]{new Rational(1, 4), new Rational(1, 4)};
		t.getTaggedNotes().set(16, t.new TaggedNote(tn16.getNote(), tn16.getVoices(), 
			durations, tn16.getIndexOtherUnisonNote()));
		t.getTaggedNotes().set(17, t.new TaggedNote(tn17.getNote(), tn17.getVoices(), 
			durations, tn17.getIndexOtherUnisonNote()));
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, false);
		t.setChordVoiceLabels(null);
		t.setVoicesUnison();
		t.setVoicesEDU();

		List<Integer[]> expected = 
			new ArrayList<>(Collections.nCopies(t.getNotes().size(), null));
		expected.set(12, new Integer[]{0, 1, 13});
		expected.set(13, new Integer[]{0, 1, 12});

		List<Integer[]> actual = t.makeVoicesIDU();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertNull(expected.get(i));
				assertNull(actual.get(i));
//				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testMakeBasicNoteProperties() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(null);
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, false);
		t.setChordVoiceLabels(null);
		t.setVoicesUnison();
		t.setVoicesEDU();
		t.setVoicesIDU();

		Integer[][] expected = new Integer[][] {
			// Chord 0
			{50, 3, 4, 1, 4, 0, 4, 0}, 
			{57, 3, 4, 1, 4, 0, 4, 1},
			{65, 3, 4, 1, 4, 0, 4, 2},
			{69, 3, 4, 1, 4, 0, 4, 3},
			// Chord 1
			{45, 1, 1, 3, 16, 1, 4, 0},
			{57, 1, 1, 1, 4, 1, 4, 1},
			{69, 1, 1, 1, 8, 1, 4, 2},
			{72, 1, 1, 1, 4, 1, 4, 3},
			// Chord 2
			{48, 19, 16, 1, 16, 2, 1, 0},
			// Chord 3
			{47, 5, 4, 1, 8, 3, 5, 0},
			{50, 5, 4, 1, 4, 3, 5, 1},
			{59, 5, 4, 1, 4, 3, 5, 2},
			{65, 5, 4, 1, 4, 3, 5, 3},
			{65, 5, 4, 1, 8, 3, 5, 4},
			// Chord 4
			{45, 11, 8, 1, 8, 4, 1, 0},
			// Chord 5
			{45, 3, 2, 1, 4, 5, 5, 0},
			{57, 3, 2, 1, 2, 5, 5, 1},
			{57, 3, 2, 1, 4, 5, 5, 2},
			{60, 3, 2, 1, 4, 5, 5, 3},
			{69, 3, 2, 1, 4, 5, 5, 4},
			// Chord 6
			{45, 7, 4, 1, 4, 6, 4, 0},
			{60, 7, 4, 1, 8, 6, 4, 1},
			{64, 7, 4, 1, 8, 6, 4, 2},
			{69, 7, 4, 1, 4, 6, 4, 3},
			// Chord 7
			{59, 15, 8, 1, 8, 7, 2, 0},
			{68, 15, 8, 1, 8, 7, 2, 1},
			// Chord 8
			{45, 2, 1, 1, 2, 8, 4, 0},
			{57, 2, 1, 1, 2, 8, 4, 1},
			{64, 2, 1, 1, 2, 8, 4, 2},
			{69, 2, 1, 1, 16, 8, 4, 3},
			// Chords 9-14
			{68, 33, 16, 1, 16, 9, 1, 0},
			{69, 17, 8, 1, 32, 10, 1, 0},
			{68, 69, 32, 1, 32, 11, 1, 0},
			{66, 35, 16, 1, 32, 12, 1, 0},
			{68, 71, 32, 1, 32, 13, 1, 0},
			{69, 9, 4, 1, 4, 14, 1, 0},
			// Chord 15
			{45, 11, 4, 1, 4, 15, 4, 0},
			{57, 11, 4, 1, 4, 15, 4, 1},
			{64, 11, 4, 1, 4, 15, 4, 2},
			{69, 11, 4, 1, 4, 15, 4, 3}
		};

		Integer[][] actual = t.makeBasicNoteProperties();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}


	@Test
	public void testMakeNumberOfNewNotesPerChord() {
		// Non-tablature case
		Transcription t = new Transcription();
		t.setType(Type.FROM_FILE);
		t.setScorePiece(new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), null, null);
		t.setName();
		t.setMeterInfo();
		t.setKeyInfo();
		t.setTaggedNotes(null);
		t.setNotes();
		t.setChords();
		t.setVoiceLabels(null, false);
		t.setChordVoiceLabels(null);
		t.setVoicesUnison();
		t.setVoicesEDU();
		t.setVoicesIDU();
		t.setBasicNoteProperties();

		List<Integer> expected = Arrays.asList(new Integer[]{4, 4, 1, 5, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});

		List<Integer> actual = t.makeNumberOfNewNotesPerChord();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNumberOfNotes() {
		// Non-tablature case
		Transcription t = new Transcription(midiTestpiece);
		int expected = 40;
		int actual = t.getNumberOfNotes();
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNumberOfVoices() {
		List<Transcription> transcriptions = Arrays.asList(		
			// Tablature case
			new	Transcription(midiElslein, encodingElslein), 
			new Transcription(midiHerrGott, encodingHerrGott),
			new Transcription(midiTestpiece, encodingTestpiece),
			// Non-tablature case			
			new	Transcription(midiElslein), 
			new Transcription(midiHerrGott),
			new Transcription(midiTestpiece)			
		);

		List<Integer> expected = Arrays.asList(new Integer[]{3, 4, 5, 3, 4, 5});

		List<Integer> actual = new ArrayList<Integer>();
		for (int i = 0; i < transcriptions.size(); i++) {
			actual.add(transcriptions.get(i).getNumberOfVoices());
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetMetricPositionsNotes() {
		// Non-tablature case
		// a. For a piece with no meter changes
		Transcription t1 = new Transcription(midiTestpiece);
		// b. For a piece with meter changes
		Transcription t2 = new Transcription(midiTestGetMeterKeyInfo);

		List<Rational[]> expected = TimelineTest.getMetricPositions("testpiece", false);
		expected.addAll(TimelineTest.getMetricPositions("testGetMeterInfo", false));

		List<Rational[]> actual = t1.getMetricPositionsNotes();
		actual.addAll(t2.getMetricPositionsNotes());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetMetricPositionsChords() {
		// Tablature/non-tablature case
		Transcription transcription = new Transcription(midiTestpiece);

		List<Rational> expected = Arrays.asList(
			new Rational(3, 4),
			new Rational(1, 1),
			new Rational(19, 16),
			new Rational(5, 4),
			new Rational(11, 8),
			new Rational(3, 2),
			new Rational(7, 4),
			new Rational(15, 8),
			new Rational(2, 1),
			new Rational(33, 16),
			new Rational(17, 8),
			new Rational(69, 32),
			new Rational(35, 16),
			new Rational(71, 32),
			new Rational(9, 4),
			new Rational(11, 4)
		);

		List<Rational> actual = transcription.getMetricPositionsChords();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
 	}


	@Test
	public void testGetVoiceAssignments() {
		List<List<Integer>> expected = new ArrayList<>();
		// Tablature case
		Transcription t1 = new Transcription(midiTestpiece, encodingTestpiece);
		List<List<Integer>> expected1 = Arrays.asList(
			Arrays.asList(3, 2, 1, 0, -1),
			Arrays.asList(2, 3, 1, 0, -1),
			Arrays.asList(-1, -1, -1, 0, -1),
			Arrays.asList(3, 3, 2, 1, 0),
			Arrays.asList(-1, -1, -1, -1, 0),
			Arrays.asList(4, 3, 2, 1, 0),
			Arrays.asList(2, 3, 1, -1, 0),
			Arrays.asList(1, -1, 0, -1, -1),
			Arrays.asList(3, 2, 1, 0, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(0, -1, -1, -1, -1),
			Arrays.asList(3, 2, 1, 0, -1)
		);
		expected.addAll(expected1);

		// Non-tablature case
		Transcription t2 = new Transcription(midiTestpiece);
		List<List<Integer>> expected2 = new ArrayList<>(expected1);
		expected2.set(1, Arrays.asList(3, 2, 1, 0, -1));
		expected2.set(3, Arrays.asList(3, 4, 2, 1, 0));
		expected.addAll(expected2);

		List<List<Integer>> actual = t1.getVoiceAssignments(t1.getNumberOfVoices());
		actual.addAll(t2.getVoiceAssignments(t2.getNumberOfVoices()));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}

	// CLEAN UP TO HERE :)
	@Test
	public void testGetLocalMeterInfo() {
		Transcription tr = new Transcription(midiTestGetMeterKeyInfo);

		List<Rational> mts = Arrays.asList(
			Rational.ZERO,
			new Rational(2, 4),
			new Rational(3, 4),
			new Rational(18, 4),
			new Rational(19, 4),
			new Rational(42, 4),
			new Rational(43, 4),
			new Rational(50, 4),
			new Rational(51, 4),
			new Rational(205, 16),
			new Rational(209, 16),
			new Rational(213, 16)
		);

		List<Integer[]> expected = Arrays.asList(
			new Integer[]{3, 4, 1, 1, 0, 1},
			new Integer[]{3, 4, 1, 1, 0, 1},
			new Integer[]{2, 1, 2, 3, 3, 4},
			new Integer[]{2, 1, 2, 3, 3, 4},
			new Integer[]{3, 1, 4, 5, 19, 4},
			new Integer[]{3, 1, 4, 5, 19, 4},
			new Integer[]{2, 2, 6, 7, 43, 4},
			new Integer[]{2, 2, 6, 7, 43, 4},
			new Integer[]{5, 16, 8, 8, 51, 4},
			new Integer[]{5, 16, 8, 8, 51, 4},
			new Integer[]{2, 4, 9, 9, 209, 16},
			new Integer[]{2, 4, 9, 9, 209, 16}
		);

		List<Integer[]> actual = new ArrayList<>();
		mts.forEach(mt -> actual.add(tr.getLocalMeterInfo(mt)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetLocalKeyInfo() {
		Transcription tr = new Transcription(midiTestGetMeterKeyInfo);

		List<Rational> mts = Arrays.asList(
			Rational.ZERO,
			new Rational(18, 4),
			new Rational(19, 4),
			new Rational(42, 4),
			new Rational(43, 4),
			new Rational(50, 4),
			new Rational(51, 4),
			new Rational(52, 4)
		);

		List<Integer[]> expected = Arrays.asList(
			new Integer[]{0, 0, 1, 3, 0, 1}, // C major 
			new Integer[]{0, 0, 1, 3, 0, 1}, // C major 
			new Integer[]{3, 1, 4, 5, 19, 4}, // F# minor
			new Integer[]{3, 1, 4, 5, 19, 4}, // F# minor
			new Integer[]{-2, 0, 6, 7, 43, 4}, // Bb major
			new Integer[]{-2, 0, 6, 7, 43, 4}, // Bb major
			new Integer[]{1, 1, 8, 9, 51, 4}, // E minor
			new Integer[]{1, 1, 8, 9, 51, 4} // E minor
		);

		List<Integer[]> actual = new ArrayList<>();
		mts.forEach(mt -> actual.add(tr.getLocalKeyInfo(mt)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetMirrorPoint() {
		// Tablature/non-tablature case
		String path = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-WTC",
			"thesis"
		));
		List<Transcription> trans = Arrays.asList(
			// a. No anacrusis
			// Onset/offset of last note in last bar in all voices but the lowest, which ends
			// one bar before the others
			new Transcription(midiTestpiece),
			// Onset/offset of last note in last bar in upper three voices and with bars in between 
			// in lowest voice
			new Transcription(new File(path + "4vv/" + "bach-WTC1-fuga_1-BWV_846.mid")),
			// Onset/offset of last note either in penultimate or in last bar, differing per voice;
			// all voices ending with rests
			new Transcription(new File(path + "4vv/" + "bach-WTC2-fuga_16-BWV_885.mid")),
			// b. With anacrusis
			// Onset/offset of last note in last bar in all voices
			new Transcription(new File(path + "3vv/" + "bach-WTC1-fuga_11-BWV_856.mid")),
			// Onset/offset of last note in last bar in all voices
			new Transcription(new File(path + "3vv/" + "bach-WTC2-fuga_10-BWV_879.mid")),
			// Onset/offset of last note in last bar in all voices
			new Transcription(new File(path + "3vv/" + "bach-WTC2-fuga_12-BWV_881.mid")),
			// Onset/offset of last note in last bar in all voices 
			new Transcription(new File(path + "3vv/" + "bach-WTC2-fuga_13-BWV_882.mid")),
			// Onset/offset of last note in penultimate/last bar in all voices
			new Transcription(new File(path + "3vv/" + "bach-WTC2-fuga_24-BWV_893.mid"))
		);

		List<Rational> expected = Arrays.asList(
			new Rational(3*2, 2),
			new Rational(27*4, 4),
			new Rational(84*3, 4),
			new Rational((1+72)*3, 8),
			new Rational((1+86)*4, 4), // = 12/8
			new Rational((1+85)*2, 4), 
			new Rational((1+84)*2, 2), 
			new Rational((1+101)*3, 8)
		);

		List<Rational> actual = new ArrayList<Rational>();
		trans.forEach(t -> actual.add(t.getMirrorPoint()));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testAlignTabAndTransIndices() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece);
		
		List<List<List<Integer>>> expected = new ArrayList<>();
		List<List<Integer>> tabToTrans = new ArrayList<>();
		// Chord 0
		tabToTrans.add(Arrays.asList(new Integer[]{0}));
		tabToTrans.add(Arrays.asList(new Integer[]{1}));
		tabToTrans.add(Arrays.asList(new Integer[]{2}));
		tabToTrans.add(Arrays.asList(new Integer[]{3}));
		// Chord 1
		tabToTrans.add(Arrays.asList(new Integer[]{4}));
		tabToTrans.add(Arrays.asList(new Integer[]{5}));
		tabToTrans.add(Arrays.asList(new Integer[]{7}));
		tabToTrans.add(Arrays.asList(new Integer[]{6}));
		// Chord 2
		tabToTrans.add(Arrays.asList(new Integer[]{8}));
		// Chord 3
		tabToTrans.add(Arrays.asList(new Integer[]{9}));
		tabToTrans.add(Arrays.asList(new Integer[]{10}));
		tabToTrans.add(Arrays.asList(new Integer[]{11}));
		tabToTrans.add(Arrays.asList(new Integer[]{12, 13}));
		// Chord 4
		tabToTrans.add(Arrays.asList(new Integer[]{14}));
		// Chord 5
		tabToTrans.add(Arrays.asList(new Integer[]{15}));
		tabToTrans.add(Arrays.asList(new Integer[]{16}));
		tabToTrans.add(Arrays.asList(new Integer[]{17}));
		tabToTrans.add(Arrays.asList(new Integer[]{18}));
		tabToTrans.add(Arrays.asList(new Integer[]{19}));
		// Chord 6
		tabToTrans.add(Arrays.asList(new Integer[]{20}));
		tabToTrans.add(Arrays.asList(new Integer[]{21}));
		tabToTrans.add(Arrays.asList(new Integer[]{22}));
		tabToTrans.add(Arrays.asList(new Integer[]{23}));
		// Chord 7
		tabToTrans.add(Arrays.asList(new Integer[]{24}));
		tabToTrans.add(Arrays.asList(new Integer[]{25}));
		// Chord 8
		tabToTrans.add(Arrays.asList(new Integer[]{26}));
		tabToTrans.add(Arrays.asList(new Integer[]{27}));
		tabToTrans.add(Arrays.asList(new Integer[]{28}));
		tabToTrans.add(Arrays.asList(new Integer[]{29}));
		// Chord 9-14
		tabToTrans.add(Arrays.asList(new Integer[]{30}));
		tabToTrans.add(Arrays.asList(new Integer[]{31}));
		tabToTrans.add(Arrays.asList(new Integer[]{32}));
		tabToTrans.add(Arrays.asList(new Integer[]{33}));
		tabToTrans.add(Arrays.asList(new Integer[]{34}));
		tabToTrans.add(Arrays.asList(new Integer[]{35}));
		// Chord 15
		tabToTrans.add(Arrays.asList(new Integer[]{36}));
		tabToTrans.add(Arrays.asList(new Integer[]{37}));
		tabToTrans.add(Arrays.asList(new Integer[]{38}));
		tabToTrans.add(Arrays.asList(new Integer[]{39}));
		
		List<List<Integer>> transToTab = new ArrayList<>();
		// Chord 0
		transToTab.add(Arrays.asList(new Integer[]{0}));
		transToTab.add(Arrays.asList(new Integer[]{1}));
		transToTab.add(Arrays.asList(new Integer[]{2}));
		transToTab.add(Arrays.asList(new Integer[]{3}));
		// Chord 1
		transToTab.add(Arrays.asList(new Integer[]{4}));
		transToTab.add(Arrays.asList(new Integer[]{5}));
		transToTab.add(Arrays.asList(new Integer[]{7}));
		transToTab.add(Arrays.asList(new Integer[]{6}));
		// Chord 2
		transToTab.add(Arrays.asList(new Integer[]{8}));
		// Chord 3
		transToTab.add(Arrays.asList(new Integer[]{9}));
		transToTab.add(Arrays.asList(new Integer[]{10}));
		transToTab.add(Arrays.asList(new Integer[]{11}));
		transToTab.add(Arrays.asList(new Integer[]{12}));
		transToTab.add(Arrays.asList(new Integer[]{12}));
		// Chord 4
		transToTab.add(Arrays.asList(new Integer[]{13}));
		// Chord 5
		transToTab.add(Arrays.asList(new Integer[]{14}));
		transToTab.add(Arrays.asList(new Integer[]{15}));
		transToTab.add(Arrays.asList(new Integer[]{16}));
		transToTab.add(Arrays.asList(new Integer[]{17}));
		transToTab.add(Arrays.asList(new Integer[]{18}));
		// Chord 6
		transToTab.add(Arrays.asList(new Integer[]{19}));
		transToTab.add(Arrays.asList(new Integer[]{20}));
		transToTab.add(Arrays.asList(new Integer[]{21}));
		transToTab.add(Arrays.asList(new Integer[]{22}));
		// Chord 7
		transToTab.add(Arrays.asList(new Integer[]{23}));
		transToTab.add(Arrays.asList(new Integer[]{24}));
		// Chord 8
		transToTab.add(Arrays.asList(new Integer[]{25}));
		transToTab.add(Arrays.asList(new Integer[]{26}));
		transToTab.add(Arrays.asList(new Integer[]{27}));
		transToTab.add(Arrays.asList(new Integer[]{28}));
		// Chord 9-14
		transToTab.add(Arrays.asList(new Integer[]{29}));
		transToTab.add(Arrays.asList(new Integer[]{30}));
		transToTab.add(Arrays.asList(new Integer[]{31}));
		transToTab.add(Arrays.asList(new Integer[]{32}));
		transToTab.add(Arrays.asList(new Integer[]{33}));
		transToTab.add(Arrays.asList(new Integer[]{34}));
		// Chord 15
		transToTab.add(Arrays.asList(new Integer[]{35}));
		transToTab.add(Arrays.asList(new Integer[]{36}));
		transToTab.add(Arrays.asList(new Integer[]{37}));
		transToTab.add(Arrays.asList(new Integer[]{38}));

		expected.add(tabToTrans);
		expected.add(transToTab);

		List<List<List<Integer>>> actual = 
			Transcription.alignTabAndTransIndices(tablature.getBasicTabSymbolProperties(), 
			transcription.getBasicNoteProperties());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetTimePitchMatrix() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		Rational[][] expected = new Rational[39][3];
//		expected[0] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(50,1)};
//		expected[1] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(57,1)};
//		expected[2] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(65,1)};
//		expected[3] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(69,1)};
//		//
//		expected[4] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(45,1)};
//		expected[5] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(57,1)};
//		expected[6] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(72,1)};
//		expected[7] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(69,1)};
//		//
//		expected[8] = new Rational[]{new Rational(19,16), new Rational(5,4), new Rational(48,1)};
//		
//		expected[9] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(47,1)};
//		expected[10] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(50,1)};
//		expected[11] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(59,1)};
//		expected[12] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(65,1)};
//		//
//		expected[13] = new Rational[]{new Rational(11,8), new Rational(6,4), new Rational(45,1)};
//		//
//		expected[14] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(45,1)};
//		expected[15] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(57,1)};
//		expected[16] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(57,1)};
//		expected[17] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(60,1)};
//		expected[18] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(69,1)};
//		//
//		expected[19] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(45,1)};
//		expected[20] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(60,1)};
//		expected[21] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(64,1)};
//		expected[22] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(69,1)};
//		//
//		expected[23] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(59,1)};
//		expected[24] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(68,1)};
//		//
//		expected[25] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(45,1)};
//		expected[26] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(57,1)};
//		expected[27] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(64,1)};
//		expected[28] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(69,1)};
//		//
//		expected[29] = new Rational[]{new Rational(33,16), new Rational(34,16), new Rational(68,1)};
//		expected[30] = new Rational[]{new Rational(34,16), new Rational(69,32), new Rational(69,1)};
//		expected[31] = new Rational[]{new Rational(69,32), new Rational(70,32), new Rational(68,1)};
//		expected[32] = new Rational[]{new Rational(70,32), new Rational(71,32), new Rational(66,1)};
//		expected[33] = new Rational[]{new Rational(71,32), new Rational(9,4), new Rational(68,1)};
//		expected[34] = new Rational[]{new Rational(9,4), new Rational(10,4), new Rational(69,1)};
//		//
//		expected[35] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(45,1)};
//		expected[36] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(57,1)};
//		expected[37] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(64,1)};
//		expected[38] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(69,1)};
		
		expected[0] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(50,1)};
		expected[1] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(57,1)};
		expected[2] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(65,1)};
		expected[3] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(69,1)};
		//
		expected[4] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(45,1)};
		expected[5] = new Rational[]{new Rational(4,4), new Rational(5,4), new Rational(57,1)};
		expected[6] = new Rational[]{new Rational(4,4), new Rational(5,4), new Rational(72,1)};
		expected[7] = new Rational[]{new Rational(4,4), new Rational(9,8), new Rational(69,1)};
		//
		expected[8] = new Rational[]{new Rational(19,16), new Rational(5,4), new Rational(48,1)};
		
		expected[9] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(47,1)};
		expected[10] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(50,1)};
		expected[11] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(59,1)};
		expected[12] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(65,1)};
		//
		expected[13] = new Rational[]{new Rational(11,8), new Rational(6,4), new Rational(45,1)};
		//
		expected[14] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(45,1)};
		expected[15] = new Rational[]{new Rational(6,4), new Rational(8,4), new Rational(57,1)};
		expected[16] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(57,1)};
		expected[17] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(60,1)};
		expected[18] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(69,1)};
		//
		expected[19] = new Rational[]{new Rational(7,4), new Rational(8,4), new Rational(45,1)};
		expected[20] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(60,1)};
		expected[21] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(64,1)};
		expected[22] = new Rational[]{new Rational(7,4), new Rational(8,4), new Rational(69,1)};
		//
		expected[23] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(59,1)};
		expected[24] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(68,1)};
		//
		expected[25] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(45,1)};
		expected[26] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(57,1)};
		expected[27] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(64,1)};
		expected[28] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(69,1)};
		//
		expected[29] = new Rational[]{new Rational(33,16), new Rational(34,16), new Rational(68,1)};
		expected[30] = new Rational[]{new Rational(34,16), new Rational(69,32), new Rational(69,1)};
		expected[31] = new Rational[]{new Rational(69,32), new Rational(70,32), new Rational(68,1)};
		expected[32] = new Rational[]{new Rational(70,32), new Rational(71,32), new Rational(66,1)};
		expected[33] = new Rational[]{new Rational(71,32), new Rational(9,4), new Rational(68,1)};
		expected[34] = new Rational[]{new Rational(9,4), new Rational(10,4), new Rational(69,1)};
		//
		expected[35] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(45,1)};
		expected[36] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(57,1)};
		expected[37] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(64,1)};
		expected[38] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(69,1)};

		Rational[][] actual = Transcription.getTimePitchMatrix(
			tablature.getBasicTabSymbolProperties(), transcription.getDurationLabels(), null);
//		for (int i = 0; i < tpmTab.length; i++) {
//			actual[i] = tpmTab[i];
//		}
//		Rational[][] tpmTrans = transcription.getTimePitchMatrix(null, transcription.getBasicNoteProperties()); 
//		for (int i = 0; i < tpmTrans.length; i++) {
//			actual[tpmTab.length + i] = tpmTrans[i];
//		}

		assertEquals(expected.length, actual.length);
	    for (int i = 0; i < expected.length; i++) {
	    	assertEquals(expected[i].length, actual[i].length);
	    	for (int j = 0; j < expected[i].length; j++) {
	    		assertEquals(expected[i][j], actual[i][j]);
	    	}
	    }
	}


	@Test
	public void testGetTimePitchMatrixNonTab() {
		Transcription transcription = new Transcription(midiTestpiece, null);

		Rational[][] expected = new Rational[40][3];
		expected[0] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(50,1)};
		expected[1] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(57,1)};
		expected[2] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(65,1)};
		expected[3] = new Rational[]{new Rational(3,4), new Rational(4,4), new Rational(69,1)};
		//
		expected[4] = new Rational[]{new Rational(4,4), new Rational(19,16), new Rational(45,1)};
		expected[5] = new Rational[]{new Rational(4,4), new Rational(5,4), new Rational(57,1)};
		expected[6] = new Rational[]{new Rational(4,4), new Rational(9,8), new Rational(69,1)};
		expected[7] = new Rational[]{new Rational(4,4), new Rational(5,4), new Rational(72,1)};
		//
		expected[8] = new Rational[]{new Rational(19,16), new Rational(5,4), new Rational(48,1)};		
		expected[9] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(47,1)};
		expected[10] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(50,1)};
		expected[11] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(59,1)};
		expected[12] = new Rational[]{new Rational(5,4), new Rational(6,4), new Rational(65,1)};
		expected[13] = new Rational[]{new Rational(5,4), new Rational(11,8), new Rational(65,1)};
		//
		expected[14] = new Rational[]{new Rational(11,8), new Rational(6,4), new Rational(45,1)};
		//
		expected[15] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(45,1)};
		expected[16] = new Rational[]{new Rational(6,4), new Rational(8,4), new Rational(57,1)};
		expected[17] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(57,1)};
		expected[18] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(60,1)};
		expected[19] = new Rational[]{new Rational(6,4), new Rational(7,4), new Rational(69,1)};
		//
		expected[20] = new Rational[]{new Rational(7,4), new Rational(8,4), new Rational(45,1)};
		expected[21] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(60,1)};
		expected[22] = new Rational[]{new Rational(7,4), new Rational(15,8), new Rational(64,1)};
		expected[23] = new Rational[]{new Rational(7,4), new Rational(8,4), new Rational(69,1)};
		//
		expected[24] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(59,1)};
		expected[25] = new Rational[]{new Rational(15,8), new Rational(8,4), new Rational(68,1)};
		//
		expected[26] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(45,1)};
		expected[27] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(57,1)};
		expected[28] = new Rational[]{new Rational(8,4), new Rational(10,4), new Rational(64,1)};
		expected[29] = new Rational[]{new Rational(8,4), new Rational(33,16), new Rational(69,1)};
		//
		expected[30] = new Rational[]{new Rational(33,16), new Rational(34,16), new Rational(68,1)};
		expected[31] = new Rational[]{new Rational(34,16), new Rational(69,32), new Rational(69,1)};
		expected[32] = new Rational[]{new Rational(69,32), new Rational(70,32), new Rational(68,1)};
		expected[33] = new Rational[]{new Rational(70,32), new Rational(71,32), new Rational(66,1)};
		expected[34] = new Rational[]{new Rational(71,32), new Rational(9,4), new Rational(68,1)};
		expected[35] = new Rational[]{new Rational(9,4), new Rational(10,4), new Rational(69,1)};
		//
		expected[36] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(45,1)};
		expected[37] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(57,1)};
		expected[38] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(64,1)};
		expected[39] = new Rational[]{new Rational(11,4), new Rational(12,4), new Rational(69,1)};

		Rational[][] actual = 
			Transcription.getTimePitchMatrix(null, null, transcription.getBasicNoteProperties()); 

		assertEquals(expected.length, actual.length);
	    for (int i = 0; i < expected.length; i++) {
	    	assertEquals(expected[i].length, actual[i].length);
	    	for (int j = 0; j < expected[i].length; j++) {
	    		assertEquals(expected[i][j], actual[i][j]);
	    	}
	    }
	}


	@Test
	public void testGetNoteDensity() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<Integer> expected = Arrays.asList(new Integer[]{
			4, 4, 4, 4, 
			4, 4, 4, 4, 
			3, 
			4, 4, 4, 4, 
			4, 
			5, 5, 5, 5, 5, 
			5, 5, 5, 5,
			5, 5, 
			4, 4, 4, 4, 
			4, 4, 4, 4, 4, 4, 
			4, 4, 4, 4
		});

		List<Integer> actual = 
			transcription.getNoteDensity(tablature.getBasicTabSymbolProperties(), 
			transcription.getDurationLabels(), null);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNoteDensityNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<Integer> expected = Arrays.asList(new Integer[]{
			4, 4, 4, 4, 
			4, 4, 4, 4, 
			3, 
			5, 5, 5, 5, 5, 
			4, 
			5, 5, 5, 5, 5, 
			5, 5, 5, 5,
			5, 5, 
			4, 4, 4, 4, 
			4, 4, 4, 4, 4, 4, 
			4, 4, 4, 4
		});

		List<Integer> actual = 
			transcription.getNoteDensity(null, null, transcription.getBasicNoteProperties());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


//	public static String opt = "O P T\n"; 
//	public static String optCost = "C O S T\n"; 
	@Test
	public void testGetVoiceEntriesOLDEST() {		
		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		// BWV 847, n=1 (ASB)
		List<List<Double>> bwv847n1 = new ArrayList<List<Double>>();
		bwv847n1.add(Arrays.asList(new Double[]{7.0, 1.0}));
		bwv847n1.add(Arrays.asList(new Double[]{2.0, 14.0, 17.0}));
		bwv847n1.add(Arrays.asList(new Double[]{1.0, 0.0}));
		bwv847n1.add(Arrays.asList(new Double[]{1.0, 0.0, 2.0}));
		expected.add(bwv847n1);
		List<List<Double>> bwv847n1avg = new ArrayList<List<Double>>(bwv847n1);
		expected.add(bwv847n1avg);
		// BWV 848, n=2 (SAB)
		List<List<Double>> bwv848n2 = new ArrayList<List<Double>>();
		bwv848n2.add(Arrays.asList(new Double[]{14.0, 52.0}));
		bwv848n2.add(Arrays.asList(new Double[]{8.0, 52.0, 84.0}));
		bwv848n2.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv848n2.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv848n2);
		List<List<Double>> bwv848n2avg = new ArrayList<List<Double>>();
		bwv848n2avg.add(Arrays.asList(new Double[]{3.5, 13.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{1.0, 13.0, 21.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv848n2avg);
		// BWV 872, n=3 (BSA)
		List<List<Double>> bwv872n3 = new ArrayList<List<Double>>();
		bwv872n3.add(Arrays.asList(new Double[]{135.0, 67.0}));
		bwv872n3.add(Arrays.asList(new Double[]{136.0, 41.0, 88.0}));
		bwv872n3.add(Arrays.asList(new Double[]{1.0, 1.0}));
		bwv872n3.add(Arrays.asList(new Double[]{2.0, 0.0, 1.0}));
		expected.add(bwv872n3);
		List<List<Double>> bwv872n3avg = new ArrayList<List<Double>>();
		bwv872n3avg.add(Arrays.asList(new Double[]{15.0, 5.0}));
		bwv872n3avg.add(Arrays.asList(new Double[]{40.0/3, 1.0, 8.0}));
		bwv872n3avg.add(Arrays.asList(new Double[]{1.0, 1.0}));
		bwv872n3avg.add(Arrays.asList(new Double[]{2.0, 0.0, 1.0}));
		expected.add(bwv872n3avg);
		// BWV 888, n=4 (BAS)
		List<List<Double>> bwv888n4 = new ArrayList<List<Double>>();
		bwv888n4.add(Arrays.asList(new Double[]{84.0, 44.0}));
		bwv888n4.add(Arrays.asList(new Double[]{188.0, 148.0, 52.0}));
		bwv888n4.add(Arrays.asList(new Double[]{1.0, 2.0}));
		bwv888n4.add(Arrays.asList(new Double[]{2.0, 1.0, 0.0}));
		expected.add(bwv888n4);
		List<List<Double>> bwv888n4avg = new ArrayList<List<Double>>();
		bwv888n4avg.add(Arrays.asList(new Double[]{5.25, 2.75}));
		bwv888n4avg.add(Arrays.asList(new Double[]{11.75, 6.75, 0.75}));
		bwv888n4avg.add(Arrays.asList(new Double[]{1.0, 2.0}));
		bwv888n4avg.add(Arrays.asList(new Double[]{2.0, 1.0, 0.0}));
		expected.add(bwv888n4avg);
		// BWV 858, n=2 (SAB), incorrect result (but not when using avgs)
		List<List<Double>> bwv858n2 = new ArrayList<List<Double>>();
		bwv858n2.add(Arrays.asList(new Double[]{24.0, 22.0}));
		bwv858n2.add(Arrays.asList(new Double[]{22.0, 72.0, 96.0}));
		bwv858n2.add(Arrays.asList(new Double[]{1.0, 0.0})); // should be [0, 0]
		bwv858n2.add(Arrays.asList(new Double[]{1.0, 0.0, 2.0})); // should be [0, 1, 2]
		expected.add(bwv858n2);
		List<List<Double>> bwv858n2avg = new ArrayList<List<Double>>();
		bwv858n2avg.add(Arrays.asList(new Double[]{0.5, 5.5}));
		bwv858n2avg.add(Arrays.asList(new Double[]{1.0, 18.0, 24.0}));
		bwv858n2avg.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv858n2avg.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv858n2avg);

		List<List<List<Double>>> actual = new ArrayList<List<List<Double>>>();
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-WTC",
			"thesis",
			"3vv"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			"bach-WTC1-fuga_2-BWV_847",	
			"bach-WTC1-fuga_2-BWV_847",	
			"bach-WTC1-fuga_3-BWV_848",
			"bach-WTC1-fuga_3-BWV_848",
			"bach-WTC2-fuga_3-BWV_872",
			"bach-WTC2-fuga_3-BWV_872",
			"bach-WTC2-fuga_19-BWV_888",
			"bach-WTC2-fuga_19-BWV_888",
			"bach-WTC1-fuga_13-BWV_858",
			"bach-WTC1-fuga_13-BWV_858",
		});
		List<Integer> ns = Arrays.asList(new Integer[]{				
			1, 1, 2, 2, 3, 3, 4, 4, 2, 2
		});
		List<Boolean> useAvgs = Arrays.asList(new Boolean[]{
			false, true, false, true, false, true, false, true, false, true
		});
		for (int i = 0; i < fileNames.size(); i++) {
			System.out.println(fileNames.get(i));
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + MIDIImport.EXTENSION));
			actual.add(t.getVoiceEntriesOLDEST(t.getNumberOfVoices(), ns.get(i), useAvgs.get(i)));
		}
//		System.out.println(opt);
//		System.out.println(optCost);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k), 1.0E-6);
				}
			}
		}
	}


	@Test
	public void testGetVoiceEntriesOLD() {		
		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		// BWV 847, n=1 (ASB)
		List<List<Double>> bwv847n1 = new ArrayList<List<Double>>();
		bwv847n1.add(Arrays.asList(new Double[]{7.0, 1.0}));
		bwv847n1.add(Arrays.asList(new Double[]{2.0, 14.0, 17.0}));
		bwv847n1.add(Arrays.asList(new Double[]{1.0, 0.0}));
		bwv847n1.add(Arrays.asList(new Double[]{1.0, 0.0, 2.0}));
		expected.add(bwv847n1);
		List<List<Double>> bwv847n1avg = new ArrayList<List<Double>>(bwv847n1);
		expected.add(bwv847n1avg);
		// BWV 848, n=2 (SAB)
		List<List<Double>> bwv848n2 = new ArrayList<List<Double>>();
		bwv848n2.add(Arrays.asList(new Double[]{5.0, 16.0}));
		bwv848n2.add(Arrays.asList(new Double[]{8.0, 16.0, 26.0}));
		bwv848n2.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv848n2.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv848n2);
		List<List<Double>> bwv848n2avg = new ArrayList<List<Double>>();
		bwv848n2avg.add(Arrays.asList(new Double[]{3.5, 13.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{1.0, 13.0, 21.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv848n2avg.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv848n2avg);
		// BWV 872, n=3 (BSA), incorrect result (but not when using avgs)
		List<List<Double>> bwv872n3 = new ArrayList<List<Double>>();
		bwv872n3.add(Arrays.asList(new Double[]{24.0, 36.0}));
		bwv872n3.add(Arrays.asList(new Double[]{33.0, 18.0, 21.0}));
		bwv872n3.add(Arrays.asList(new Double[]{0.0, 1.0})); // should be [1, 1]
		bwv872n3.add(Arrays.asList(new Double[]{0.0, 2.0, 1.0})); // should be [2, 0, 1]
		expected.add(bwv872n3);
		List<List<Double>> bwv872n3avg = new ArrayList<List<Double>>();
		bwv872n3avg.add(Arrays.asList(new Double[]{15.0, 5.0}));
		bwv872n3avg.add(Arrays.asList(new Double[]{40.0/3, 1.0, 8.0}));
		bwv872n3avg.add(Arrays.asList(new Double[]{1.0, 1.0})); 
		bwv872n3avg.add(Arrays.asList(new Double[]{2.0, 0.0, 1.0})); 
		expected.add(bwv872n3avg);
		// BWV 888, n=4 (BAS)
		List<List<Double>> bwv888n4 = new ArrayList<List<Double>>();
		bwv888n4.add(Arrays.asList(new Double[]{13.0, 10.0}));
		bwv888n4.add(Arrays.asList(new Double[]{23.0, 26.0, 15.0}));
		bwv888n4.add(Arrays.asList(new Double[]{1.0, 2.0}));
		bwv888n4.add(Arrays.asList(new Double[]{2.0, 1.0, 0.0}));
		expected.add(bwv888n4);
		List<List<Double>> bwv888n4avg = new ArrayList<List<Double>>();
		bwv888n4avg.add(Arrays.asList(new Double[]{5.25, 2.75}));
		bwv888n4avg.add(Arrays.asList(new Double[]{11.75, 6.75, 0.75}));
		bwv888n4avg.add(Arrays.asList(new Double[]{1.0, 2.0}));
		bwv888n4avg.add(Arrays.asList(new Double[]{2.0, 1.0, 0.0}));
		expected.add(bwv888n4avg);
		// BWV 858, n=2 (SAB), incorrect result (but not when using avgs)
		List<List<Double>> bwv858n2 = new ArrayList<List<Double>>();
		bwv858n2.add(Arrays.asList(new Double[]{19.0, 7.0}));
		bwv858n2.add(Arrays.asList(new Double[]{18.0, 20.0, 42.0}));
		bwv858n2.add(Arrays.asList(new Double[]{1.0, 0.0})); // should be [0, 0]
		bwv858n2.add(Arrays.asList(new Double[]{1.0, 0.0, 2.0})); // should be [0, 1, 2]
		expected.add(bwv858n2);
		List<List<Double>> bwv858n2avg = new ArrayList<List<Double>>();
		bwv858n2avg.add(Arrays.asList(new Double[]{0.5, 5.5}));
		bwv858n2avg.add(Arrays.asList(new Double[]{1.0, 18.0, 24.0}));
		bwv858n2avg.add(Arrays.asList(new Double[]{0.0, 0.0}));
		bwv858n2avg.add(Arrays.asList(new Double[]{0.0, 1.0, 2.0}));
		expected.add(bwv858n2avg);

		List<List<List<Double>>> actual = new ArrayList<List<List<Double>>>();
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-WTC",
			"thesis",
			"3vv"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			"bach-WTC1-fuga_2-BWV_847",	
			"bach-WTC1-fuga_2-BWV_847",	
			"bach-WTC1-fuga_3-BWV_848",
			"bach-WTC1-fuga_3-BWV_848",
			"bach-WTC2-fuga_3-BWV_872",
			"bach-WTC2-fuga_3-BWV_872",
			"bach-WTC2-fuga_19-BWV_888",
			"bach-WTC2-fuga_19-BWV_888",
			"bach-WTC1-fuga_13-BWV_858",
			"bach-WTC1-fuga_13-BWV_858",
		});
		List<Integer> ns = Arrays.asList(new Integer[]{				
			1, 1, 2, 2, 3, 3, 4, 4, 2, 2
		});
		List<Boolean> useAvgs = Arrays.asList(new Boolean[]{
			false, true, false, true, false, true, false, true, false, true
		});
		for (int i = 0; i < fileNames.size(); i++) {
			System.out.println(fileNames.get(i));
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + MIDIImport.EXTENSION));
			actual.add(t.getVoiceEntriesOLD(t.getNumberOfVoices(), ns.get(i), useAvgs.get(i)));
		}
//		System.out.println(opt);
//		System.out.println(optCost);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k), 1.0E-6);
				}
			}
		}
	}


	@Test
	public void testDetermineVoiceEntries() { // TODO remove?
		String prefix = "F:/research/data/MIDI/bach-INV/thesis/3vv/";
		List<String> pieceNames = Arrays.asList(new String[]{
			"bach-INV-inventio_1-BWV_787",
			"bach-INV-inventio_2-BWV_788",
			"bach-INV-inventio_3-BWV_789",
			"bach-INV-inventio_4-BWV_790",
			"bach-INV-inventio_5-BWV_791",
			"bach-INV-inventio_6-BWV_792",
			"bach-INV-inventio_7-BWV_793",
			"bach-INV-inventio_8-BWV_794",
			"bach-INV-inventio_9-BWV_795",
			"bach-INV-inventio_10-BWV_796",
			"bach-INV-inventio_11-BWV_797",
			"bach-INV-inventio_12-BWV_798",
			"bach-INV-inventio_13-BWV_799",
			"bach-INV-inventio_14-BWV_800",
			"bach-INV-inventio_15-BWV_801"	
		});
		
//		for (String s : pieceNames) {
//			Transcription t = new Transcription(new File(prefix + s + MIDIImport.EXTENSION), null);
//			System.out.println(s);
//			System.out.println(t.determineVoiceEntries(4));
//		}

//		Dataset ds = new Dataset(DatasetID.valueOf("tINT_3vv"));
		Map<String, Double> modelParams = new LinkedHashMap<String, Double>();
//		modelParams.put(Runner.APPL_TO_NEW_DATA, (double) ToolBox.toInt(false));
//		ds.populateDataset("thesis", null, false);
//		List<Transcription> allTr = ds.getAllTranscriptions();
//		for (Transcription t : allTr) {
//			System.out.println(t.getPieceName());
//			System.out.println(t.determineVoiceEntries(3));
//		}
	}
	
	
//	public void testBla() {
//		String prefixTab = "F:/research/data/encodings/thesis-int/";
//		String prefix = "F:/research/data/MIDI/thesis-int/";
//		prefixTab = "F:/research/data/encodings/byrd-int/4vv/";
//		prefix = "F:/research/data/MIDI/byrd-int/4vv/";
//
//		List<String> fileNames = Arrays.asList(new String[]{ 
////			"3vv/newsidler-1536_7-disant_adiu", // non
////			"3vv/newsidler-1536_7-mess_pensees", // imi
////			"3vv/pisador-1552_7-pleni_de", // imi
////			"3vv/judenkuenig-1523_2-elslein_liebes", // non
////			"3vv/newsidler-1544_2-nun_volget", // imi
////			"3vv/phalese-1547_7-tant_que-3vv" // non
//				
////			"4vv/ochsenkun-1558_5-absolon_fili", // n=2: imi
////			"4vv/ochsenkun-1558_5-in_exitu", // n=2: imi
////			"4vv/ochsenkun-1558_5-qui_habitat", // imi 
////			"4vv/rotta-1546_15-bramo_morir", // non
////			"4vv/phalese-1547_7-tant_que-4vv", // non
////			"4vv/ochsenkun-1558_5-herr_gott", // non
////			"4vv/abondante-1548_1-mais_mamignone", // non
////			"4vv/phalese-1563_12-las_on", // non
////			"4vv/barbetta-1582_1-il_nest", // non
//				
////			"ah_golden_hairs-NEW",
////			"an_aged_dame-II", //
////			"as_caesar_wept-II",
//			"blame_i_confess-II", //
////			"in_angels_weed-II",
////			"o_lord_bow_down-II", //
////			"o_that_we_woeful_wretches-NEW", //
////			"quis_me_statim-II", //
////			"rejoyce_unto_the_lord-NEW", // 
////			"sith_death-NEW", //
////			"the_lord_is_only_my_support-NEW", //
////			"the_man_is_blest-NEW", //
////			"while_phoebus-II" //	
//		});
//		
//		for (String s : fileNames) {
//			File encoding = new File(prefixTab + s + Encoding.EXTENSION);
//			Tablature t = new Tablature(encoding, false);
//			Transcription tr = new Transcription(new File(prefix + s + MIDIImport.EXTENSION), encoding);
//			System.out.println("@-@-@-@-@" + s);
//			tr.determineVoiceEntriesHIGHLEVEL(t.getBasicTabSymbolProperties(), 
//				tr.getMinimumDurationLabels(), null, 4, 3);
//		}
//	}


	@Test
	public void testCalculateConfigCost() {
		// Example taken from Inventio 13 a3 (BWV 799) 
		// Config 0
		List<List<Integer>> l0 = new ArrayList<List<Integer>>();
		List<List<Integer>> r0 = new ArrayList<List<Integer>>();
		l0.add(Arrays.asList(new Integer[]{74, 59, null,}));
		l0.add(Arrays.asList(new Integer[]{76, 60, null}));
		l0.add(Arrays.asList(new Integer[]{78, 57, null}));
		r0.add(Arrays.asList(new Integer[]{79, 64, 64}));
		r0.add(Arrays.asList(new Integer[]{78, 64, 64}));
		r0.add(Arrays.asList(new Integer[]{79, 64, 52}));
		
		// Config 1
		List<List<Integer>> l1 = new ArrayList<List<Integer>>();
		List<List<Integer>> r1 = new ArrayList<List<Integer>>();
		l1.add(Arrays.asList(new Integer[]{74, null, 59}));
		l1.add(Arrays.asList(new Integer[]{76, null, 60}));
		l1.add(Arrays.asList(new Integer[]{78, null, 57}));
		r1.add(Arrays.asList(new Integer[]{79, 64, 64}));
		r1.add(Arrays.asList(new Integer[]{78, 64, 64}));
		r1.add(Arrays.asList(new Integer[]{79, 64, 52}));
		
		// Config 2
		List<List<Integer>> l2 = new ArrayList<List<Integer>>();
		List<List<Integer>> r2 = new ArrayList<List<Integer>>();
		l2.add(Arrays.asList(new Integer[]{null, 74, 59}));
		l2.add(Arrays.asList(new Integer[]{null, 76, 60}));
		l2.add(Arrays.asList(new Integer[]{null, 78, 57}));
		r2.add(Arrays.asList(new Integer[]{79, 64, 64}));
		r2.add(Arrays.asList(new Integer[]{78, 64, 64}));
		r2.add(Arrays.asList(new Integer[]{79, 64, 52}));
		
		List<List<List<Integer>>> lefts = new ArrayList<List<List<Integer>>>();
		lefts.add(l0);
		lefts.add(l1);
		lefts.add(l2);
		List<List<List<Integer>>> rights = new ArrayList<List<List<Integer>>>();
		rights.add(r0);
		rights.add(r1);
		rights.add(r2);

		// Non-linear
		int nli0 = ((5+4+5)+(5+5+5)) + ((3+2+3)+(4+4+4)) + ((1+0+1)+(7+7+7));
		int nli1 = ((5+4+5)+(5+5+7)) + ((3+2+3)+(4+4+8)) + ((1+0+1)+(7+7+5));
		int nli2 = ((10+10+10)+(5+5+7)) + ((12+12+12)+(4+4+8)) + ((14+14+14)+(7+7+5));
		int li0 = ((2+2+1+1+1) + (1+3+7+0+0));
		int li1 = ((2+2+1+1+1) + (1+3+7+0+12));
		int li2 = ((2+2+14+0+0) + (1+3+7+0+12));
		List<Integer> expected = Arrays.asList(new Integer[]{
			nli0, nli1, nli2, 
			li0, li1, li2,	
//			nli0 + li0, nli1 + li1, nli2 + li2
		});

		List<Integer> actual = new ArrayList<Integer>();
		for (int i = 0; i < lefts.size(); i++) {
			actual.add(Transcription.calculateConfigCost(lefts.get(i), rights.get(i), false));
		}
		for (int i = 0; i < lefts.size(); i++) {
			actual.add(Transcription.calculateConfigCost(lefts.get(i), rights.get(i), true));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testDetermineConfigs() {
		List<List<Integer>> leftCh= new ArrayList<List<Integer>>();
		leftCh.add(Arrays.asList(new Integer[]{20, 10, null, null}));
//		leftCh.add(Arrays.asList(new Integer[]{20, 10, null, null}));
		System.out.println(Transcription.determineConfigs(2, 4, leftCh));
//		System.exit(0);
		
		List<List<Integer>> c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14;
		List<List<List<Integer>>> leftChords = new ArrayList<List<List<Integer>>>();
		
		// a. If numConfigs == rightDensity
		// Left density = 1; right density = 4
		List<List<Integer>> L1R4 = new ArrayList<List<Integer>>();
		L1R4.add(Arrays.asList(new Integer[]{10, null, null, null}));
		leftChords.add(L1R4);
		//
		List<List<List<Integer>>> expectedL1R4 = new ArrayList<List<List<Integer>>>();
		expectedL1R4.add(L1R4);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{null, 10, null, null}));
		expectedL1R4.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{null, null, 10, null}));
		expectedL1R4.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{null, null, null, 10}));
		expectedL1R4.add(c3);
		
		// Left density = 3; right density = 4
		List<List<Integer>> L3R4 = new ArrayList<List<Integer>>();
		L3R4.add(Arrays.asList(new Integer[]{30, 20, 10, null}));
		leftChords.add(L3R4);
		//
		List<List<List<Integer>>> expectedL3R4 = new ArrayList<List<List<Integer>>>();
		expectedL3R4.add(L3R4);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{30, 20, null, 10}));
		expectedL3R4.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{30, null, 20, 10}));
		expectedL3R4.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{null, 30, 20, 10}));
		expectedL3R4.add(c3);
		
		// b. If numConfigs > rightDensity
		// Left density = 2; right density = 4
		List<List<Integer>> L2R4 = new ArrayList<List<Integer>>();
		L2R4.add(Arrays.asList(new Integer[]{20, 10, null, null}));
		leftChords.add(L2R4);
		//
		List<List<List<Integer>>> expectedL2R4 = new ArrayList<List<List<Integer>>>();
		expectedL2R4.add(L2R4);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{20, null, 10, null}));
		expectedL2R4.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{20, null, null, 10}));
		expectedL2R4.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{null, 20, 10, null}));
		expectedL2R4.add(c3);
		c4 = new ArrayList<List<Integer>>(); 
		c4.add(Arrays.asList(new Integer[]{null, 20, null, 10}));
		expectedL2R4.add(c4);
		c5 = new ArrayList<List<Integer>>(); 
		c5.add(Arrays.asList(new Integer[]{null, null, 20, 10}));
		expectedL2R4.add(c5);
		
		// Left density = 2; right density = 5	
		List<List<Integer>> L2R5 = new ArrayList<List<Integer>>();
		L2R5.add(Arrays.asList(new Integer[]{20, 10, null, null, null}));
		leftChords.add(L2R5);
		//
		List<List<List<Integer>>> expectedL2R5 = new ArrayList<List<List<Integer>>>();
		expectedL2R5.add(L2R5);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{20, null, 10, null, null}));
		expectedL2R5.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{20, null, null, 10, null}));
		expectedL2R5.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{20, null, null, null, 10}));
		expectedL2R5.add(c3);
		c4 = new ArrayList<List<Integer>>(); 
		c4.add(Arrays.asList(new Integer[]{null, 20, 10, null, null}));
		expectedL2R5.add(c4);
		c5 = new ArrayList<List<Integer>>(); 
		c5.add(Arrays.asList(new Integer[]{null, 20, null, 10, null}));
		expectedL2R5.add(c5);
		c6 = new ArrayList<List<Integer>>(); 
		c6.add(Arrays.asList(new Integer[]{null, 20, null, null, 10}));
		expectedL2R5.add(c6);
		c7 = new ArrayList<List<Integer>>(); 
		c7.add(Arrays.asList(new Integer[]{null, null, 20, 10, null}));
		expectedL2R5.add(c7);
		c8 = new ArrayList<List<Integer>>(); 
		c8.add(Arrays.asList(new Integer[]{null, null, 20, null, 10}));
		expectedL2R5.add(c8);
		c9 = new ArrayList<List<Integer>>(); 
		c9.add(Arrays.asList(new Integer[]{null, null, null, 20, 10}));
		expectedL2R5.add(c9);
		
		// Left density = 3; right density = 5
		List<List<Integer>> L3R5 = new ArrayList<List<Integer>>();
		L3R5.add(Arrays.asList(new Integer[]{30, 20, 10, null, null}));
		leftChords.add(L3R5);
		//
		List<List<List<Integer>>> expectedL3R5 = new ArrayList<List<List<Integer>>>();
		expectedL3R5.add(L3R5);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{30, 20, null, 10, null}));
		expectedL3R5.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{30, 20, null, null, 10}));
		expectedL3R5.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{30, null, 20, 10, null}));
		expectedL3R5.add(c3);
		c4 = new ArrayList<List<Integer>>(); 
		c4.add(Arrays.asList(new Integer[]{30, null, 20, null, 10}));
		expectedL3R5.add(c4);
		c5 = new ArrayList<List<Integer>>(); 
		c5.add(Arrays.asList(new Integer[]{null, 30, 20, 10, null}));
		expectedL3R5.add(c5);
		c6 = new ArrayList<List<Integer>>(); 
		c6.add(Arrays.asList(new Integer[]{null, 30, 20, null, 10}));
		expectedL3R5.add(c6);
		c7 = new ArrayList<List<Integer>>(); 
		c7.add(Arrays.asList(new Integer[]{null, 30, null, 20, 10}));
		expectedL3R5.add(c7);
		c8 = new ArrayList<List<Integer>>(); 
		c8.add(Arrays.asList(new Integer[]{null, null, 30, 20, 10}));
		expectedL3R5.add(c8);
		c9 = new ArrayList<List<Integer>>(); 
		c9.add(Arrays.asList(new Integer[]{30, null, null, 20, 10}));
		expectedL3R5.add(c9);
		
		// Left density = 4; right density = 6
		List<List<Integer>> L4R6 = new ArrayList<List<Integer>>();
		L4R6.add(Arrays.asList(new Integer[]{40, 30, 20, 10, null, null}));
		leftChords.add(L4R6);
		//
		List<List<List<Integer>>> expectedL4R6 = new ArrayList<List<List<Integer>>>();
		expectedL4R6.add(L4R6);
		c1 = new ArrayList<List<Integer>>(); 
		c1.add(Arrays.asList(new Integer[]{40, 30, 20, null, 10, null}));
		expectedL4R6.add(c1);
		c2 = new ArrayList<List<Integer>>(); 
		c2.add(Arrays.asList(new Integer[]{40, 30, 20, null, null, 10}));
		expectedL4R6.add(c2);
		c3 = new ArrayList<List<Integer>>(); 
		c3.add(Arrays.asList(new Integer[]{40, 30, null, 20, 10, null}));
		expectedL4R6.add(c3);
		c4 = new ArrayList<List<Integer>>(); 
		c4.add(Arrays.asList(new Integer[]{40, 30, null, 20, null, 10}));
		expectedL4R6.add(c4);
		c5 = new ArrayList<List<Integer>>(); 
		c5.add(Arrays.asList(new Integer[]{40, null, 30, 20, 10, null}));
		expectedL4R6.add(c5);
		c6 = new ArrayList<List<Integer>>(); 
		c6.add(Arrays.asList(new Integer[]{40, null, 30, 20, null, 10}));
		expectedL4R6.add(c6);
		c7 = new ArrayList<List<Integer>>(); 
		c7.add(Arrays.asList(new Integer[]{null, 40, 30, 20, 10, null}));
		expectedL4R6.add(c7);
		c8 = new ArrayList<List<Integer>>(); 
		c8.add(Arrays.asList(new Integer[]{null, 40, 30, 20, null, 10}));
		expectedL4R6.add(c8);
		c9 = new ArrayList<List<Integer>>(); 
		c9.add(Arrays.asList(new Integer[]{null, 40, 30, null, 20, 10}));
		expectedL4R6.add(c9);		
		c10 = new ArrayList<List<Integer>>(); 
		c10.add(Arrays.asList(new Integer[]{null, 40, null, 30, 20, 10}));
		expectedL4R6.add(c10);
		c11 = new ArrayList<List<Integer>>(); 
		c11.add(Arrays.asList(new Integer[]{null, null, 40, 30, 20, 10}));
		expectedL4R6.add(c11);
		c12 = new ArrayList<List<Integer>>(); 
		c12.add(Arrays.asList(new Integer[]{40, null, 30, null, 20, 10}));
		expectedL4R6.add(c12);
		c13 = new ArrayList<List<Integer>>(); 
		c13.add(Arrays.asList(new Integer[]{40, null, null, 30, 20, 10}));
		expectedL4R6.add(c13);
		c14 = new ArrayList<List<Integer>>(); 
		c14.add(Arrays.asList(new Integer[]{40, 30, null, null, 20, 10}));
		expectedL4R6.add(c14);
		
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		expected.addAll(expectedL1R4);
		expected.addAll(expectedL3R4);
		expected.addAll(expectedL2R4);
		expected.addAll(expectedL2R5);
		expected.addAll(expectedL3R5);
		expected.addAll(expectedL4R6);
		
		List<List<Integer>> densities = new ArrayList<List<Integer>>();
		densities.add(Arrays.asList(new Integer[]{1, 4}));
		densities.add(Arrays.asList(new Integer[]{3, 4}));
		densities.add(Arrays.asList(new Integer[]{2, 4}));
		densities.add(Arrays.asList(new Integer[]{2, 5}));
		densities.add(Arrays.asList(new Integer[]{3, 5}));
		densities.add(Arrays.asList(new Integer[]{4, 6}));

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		for (int i = 0; i < densities.size(); i++) {
			List<Integer> dens = densities.get(i);
			actual.addAll(Transcription.determineConfigs(dens.get(0), dens.get(1), 
				leftChords.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


//	public void testDetermineNexConfig() {
//		List<List<Integer>> c0, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11;
//
//		// a. If numConfigs == rightDensity
//		// Left density = 1; right density = 4
//		List<List<List<Integer>>> configsL1R4 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{10, null, null, null}));
//		configsL1R4.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{null, 10, null, null}));
//		configsL1R4.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{null, null, 10, null}));
//		configsL1R4.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{null, null, null, 10}));
//		configsL1R4.add(c3);
//		
//		// Left density = 3; right density = 4
//		List<List<List<Integer>>> configsL3R4 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{30, 20, 10, null}));
//		configsL3R4.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{30, 20, null, 10}));
//		configsL3R4.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{30, null, 20, 10}));
//		configsL3R4.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{null, 30, 20, 10}));
//		configsL3R4.add(c3);
//		
//		// b. If numConfigs > rightDensity
//		// Left density = 2; right density = 4
//		List<List<List<Integer>>> configsL2R4 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{20, 10, null, null}));
//		configsL2R4.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{20, null, 10, null}));
//		configsL2R4.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{20, null, null, 10}));
//		configsL2R4.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{null, 20, 10, null}));
//		configsL2R4.add(c3);
//		c4 = new ArrayList<List<Integer>>();
//		c4.add(Arrays.asList(new Integer[]{null, 20, null, 10}));
//		configsL2R4.add(c4);
//		c5 = new ArrayList<List<Integer>>();
//		c5.add(Arrays.asList(new Integer[]{null, null, 20, 10}));
//		configsL2R4.add(c5);
//		
//		// Left density = 2; right density = 5		
//		List<List<List<Integer>>> configsL2R5 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{20, 10, null, null, null}));
//		configsL2R5.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{20, null, 10, null, null}));
//		configsL2R5.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{20, null, null, 10, null}));
//		configsL2R5.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{20, null, null, null, 10}));
//		configsL2R5.add(c3);
//		c4 = new ArrayList<List<Integer>>();
//		c4.add(Arrays.asList(new Integer[]{null, 20, 10, null, null}));
//		configsL2R5.add(c4);
//		c5 = new ArrayList<List<Integer>>();
//		c5.add(Arrays.asList(new Integer[]{null, 20, null, 10, null}));
//		configsL2R5.add(c5);
//		c6 = new ArrayList<List<Integer>>();
//		c6.add(Arrays.asList(new Integer[]{null, 20, null, null, 10}));
//		configsL2R5.add(c6);
//		c7 = new ArrayList<List<Integer>>();
//		c7.add(Arrays.asList(new Integer[]{null, null, 20, 10, null}));
//		configsL2R5.add(c7);
//		c8 = new ArrayList<List<Integer>>();
//		c8.add(Arrays.asList(new Integer[]{null, null, 20, null, 10}));
//		configsL2R5.add(c8);
//		c9 = new ArrayList<List<Integer>>();
//		c9.add(Arrays.asList(new Integer[]{null, null, null, 20, 10}));
//		configsL2R5.add(c9);
//		
//		// Left density = 3; right density = 5		
//		List<List<List<Integer>>> configsL3R5 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{30, 20, 10, null, null}));
//		configsL3R5.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{30, 20, null, 10, null}));
//		configsL3R5.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{30, 20, null, null, 10}));
//		configsL3R5.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{30, null, 20, null, 10}));
//		configsL3R5.add(c3);
//		c4 = new ArrayList<List<Integer>>();
//		c4.add(Arrays.asList(new Integer[]{30, null, null, 20, 10}));
//		configsL3R5.add(c4);
//		c5 = new ArrayList<List<Integer>>();
//		c5.add(Arrays.asList(new Integer[]{null, 30, 20, 10, null}));
//		configsL3R5.add(c5);
//		c6 = new ArrayList<List<Integer>>();
//		c6.add(Arrays.asList(new Integer[]{null, 30, 20, null, 10}));
//		configsL3R5.add(c6);
//		c7 = new ArrayList<List<Integer>>();
//		c7.add(Arrays.asList(new Integer[]{null, 30, null, 20, 10}));
//		configsL3R5.add(c7);
//		c8 = new ArrayList<List<Integer>>();
//		c8.add(Arrays.asList(new Integer[]{null, null, 30, 20, 10}));
//		configsL3R5.add(c8);
//		
//		// Left density = 4; right density = 6		
//		List<List<List<Integer>>> configsL4R6 = new ArrayList<List<List<Integer>>>();
//		c0 = new ArrayList<List<Integer>>();
//		c0.add(Arrays.asList(new Integer[]{40, 30, 20, 10, null, null}));
//		configsL4R6.add(c0);
//		c1 = new ArrayList<List<Integer>>();
//		c1.add(Arrays.asList(new Integer[]{40, 30, 20, null, 10, null}));
//		configsL4R6.add(c1);
//		c2 = new ArrayList<List<Integer>>();
//		c2.add(Arrays.asList(new Integer[]{40, 30, 20, null, null, 10}));
//		configsL4R6.add(c2);
//		c3 = new ArrayList<List<Integer>>();
//		c3.add(Arrays.asList(new Integer[]{40, 30, null, 20, null, 10}));
//		configsL4R6.add(c3);
//		c4 = new ArrayList<List<Integer>>();
//		c4.add(Arrays.asList(new Integer[]{40, 30, null, null, 20, 10}));
//		configsL4R6.add(c4);
//		c5 = new ArrayList<List<Integer>>();
//		c5.add(Arrays.asList(new Integer[]{40, null, 30, null, 20, 10}));
//		configsL4R6.add(c5);
//		c6 = new ArrayList<List<Integer>>();
//		c6.add(Arrays.asList(new Integer[]{40, null, null, 30, 20, 10}));
//		configsL4R6.add(c6);
//		c7 = new ArrayList<List<Integer>>();
//		c7.add(Arrays.asList(new Integer[]{null, 40, 30, 20, 10, null}));
//		configsL4R6.add(c7);
//		c8 = new ArrayList<List<Integer>>();
//		c8.add(Arrays.asList(new Integer[]{null, 40, 30, 20, null, 10}));
//		configsL4R6.add(c8);
//		c9 = new ArrayList<List<Integer>>();
//		c9.add(Arrays.asList(new Integer[]{null, 40, 30, null, 20, 10}));
//		configsL4R6.add(c9);
//		c10 = new ArrayList<List<Integer>>();
//		c10.add(Arrays.asList(new Integer[]{null, 40, null, 30, 20, 10}));
//		configsL4R6.add(c10);
//		c11 = new ArrayList<List<Integer>>();
//		c11.add(Arrays.asList(new Integer[]{null, null, 40, 30, 20, 10}));
//		configsL4R6.add(c11);
//		
//		List<List<List<List<Integer>>>> allConfigs = new ArrayList<List<List<List<Integer>>>>();
//		allConfigs.add(configsL1R4);
//		allConfigs.add(configsL3R4);
//		allConfigs.add(configsL2R4);
//		allConfigs.add(configsL2R5);
//		allConfigs.add(configsL3R5);
//		allConfigs.add(configsL4R6);
//		
//		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL1R4.subList(1, configsL1R4.size())));
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL3R4.subList(1, configsL3R4.size())));
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL2R4.subList(1, configsL2R4.size())));
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL2R5.subList(1, configsL2R5.size())));
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL3R5.subList(1, configsL3R5.size())));
//		expected.addAll( 
//			new ArrayList<List<List<Integer>>>(configsL4R6.subList(1, configsL4R6.size())));
//		
//		List<List<Integer>> densities = new ArrayList<List<Integer>>();
//		densities.add(Arrays.asList(new Integer[]{1, 4}));
//		densities.add(Arrays.asList(new Integer[]{3, 4}));
//		densities.add(Arrays.asList(new Integer[]{2, 4}));
//		densities.add(Arrays.asList(new Integer[]{2, 5}));
//		densities.add(Arrays.asList(new Integer[]{3, 5}));
//		densities.add(Arrays.asList(new Integer[]{4, 6}));
//		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
//		for (int i = 0; i < allConfigs.size(); i++) {
//			List<List<List<Integer>>> conf = allConfigs.get(i);
//			List<Integer> dens = densities.get(i);
//			for (int j = 0; j < conf.size() - 1; j++) {
//				actual.add(Transcription.determineNextConfig(j, conf.size(), dens.get(0), 
//					dens.get(1), conf.get(j)));
//			}
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) != null && actual.get(i) != null) {
//				assertEquals(expected.get(i).size(), actual.get(i).size());
//				for (int j = 0; j < expected.get(i).size(); j++) {
//					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
//						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
//						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
//							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
//						}
//					}
//				}
//			}
//		}
//		assertEquals(expected, actual);		
//	}


	@Test
	public void testGetVoiceEntriesOLDER_EXT() {
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-inv",
			"thesis"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			"3vv/bach-INV-inventio_1-BWV_787",
			"3vv/bach-INV-inventio_2-BWV_788",
			"3vv/bach-INV-inventio_3-BWV_789",
			"3vv/bach-INV-inventio_4-BWV_790",
			"3vv/bach-INV-inventio_5-BWV_791",
			"3vv/bach-INV-inventio_6-BWV_792",
			"3vv/bach-INV-inventio_7-BWV_793",
			"3vv/bach-INV-inventio_8-BWV_794",
			"3vv/bach-INV-inventio_9-BWV_795",
			"3vv/bach-INV-inventio_10-BWV_796",
			"3vv/bach-INV-inventio_11-BWV_797",
			"3vv/bach-INV-inventio_12-BWV_798",
			"3vv/bach-INV-inventio_13-BWV_799",
			"3vv/bach-INV-inventio_14-BWV_800",
			"3vv/bach-INV-inventio_15-BWV_801"	
		});
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4});
		for (int i = 0; i < fileNames.size(); i++) { 
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + MIDIImport.EXTENSION));
			List<List<Double>> res = t.getVoiceEntriesOLDER_EXT(3, 3, false);
			System.out.println(t.getName());
			for (List<Double> l : res) {
				System.out.println(l);
			}
//			actual.add(t.getImitatingVoiceEntries(3, 3));
		}
	}


	@Test
	public void testGetImitativeVoiceEntries() {
		String prefixTab = CLInterface.getPathString(Arrays.asList(
			paths.get("ENCODINGS_PATH"),
			"thesis-int"
		));
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"thesis-int"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			// 3vv (using full durations)
			"3vv/newsidler-1536_7-mess_pensees", // correct
			"3vv/newsidler-1544_2-nun_volget", // correct
			"3vv/pisador-1552_7-pleni_de",	// incorrect: voice crossing at density 2
			// 4vv
			"4vv/ochsenkun-1558_5-absolon_fili", // correct after correcting -1 to 0 at density 4
//			"4vv/ochsenkun-1558_5-in_exitu", // TODO
			"4vv/ochsenkun-1558_5-qui_habitat", // correct after re-establishing HMN at density 4
//			"4vv/abondante-1548_1-mais_mamignone" // TODO
		});

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// 3vv
		List<List<Integer>> messPensees = new ArrayList<List<Integer>>();
		messPensees.add(Arrays.asList(new Integer[]{1, 1}));
		messPensees.add(Arrays.asList(new Integer[]{0, 10, 11, 26, 27, 28}));
		messPensees.add(Arrays.asList(new Integer[]{2, 2, 0, 2, 1, 0}));
		expected.add(messPensees);
		//
		List<List<Integer>> nunVolget = new ArrayList<List<Integer>>();
		nunVolget.add(Arrays.asList(new Integer[]{0, 0}));
		nunVolget.add(Arrays.asList(new Integer[]{0, 3, 4, 33, 34, 35}));
		nunVolget.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1, 0}));
		expected.add(nunVolget);
		//
		List<List<Integer>> pleniDe = new ArrayList<List<Integer>>();
		pleniDe.add(Arrays.asList(new Integer[]{-1, 2})); // voice crossing at density 2 
		pleniDe.add(Arrays.asList(new Integer[]{0, 5, 15, 16}));
		pleniDe.add(Arrays.asList(new Integer[]{1, 2, 2, 0}));
		expected.add(pleniDe);

		// 4vv
		List<List<Integer>> absolon = new ArrayList<List<Integer>>();
		absolon.add(Arrays.asList(new Integer[]{0, 0, -1})); // motif at density 4 not repeated literally
		absolon.add(Arrays.asList(new Integer[]{0, 16, 17, 29, 30, 31, 56, 57, 58, 59}));
		absolon.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1, 0, 3, 2, 1, 0}));
		expected.add(absolon);
		List<List<Integer>> quiHabitat = new ArrayList<List<Integer>>();
		quiHabitat.add(Arrays.asList(new Integer[]{0, 0, 0}));
		quiHabitat.add(Arrays.asList(new Integer[]{0, 11, 12, 30, 31, 32, 66, 67, 68}));
		quiHabitat.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1, 0, 3, 2, 1, 0})); // TODO fix CoD in last chord (4 voices, 3 notes)
		expected.add(quiHabitat);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{3, 3, 3, 4, 4});
		List<Integer> ns = Arrays.asList(new Integer[]{3, 3, 3, 2, 3});
		for (int i = 0; i < fileNames.size(); i++) {
			String piece = fileNames.get(i);
			File enc = new File(prefixTab + piece + Encoding.EXTENSION);
			Tablature tab = new Tablature(enc);
			Transcription t = new Transcription(new File(prefix + piece + MIDIImport.EXTENSION), enc);
			actual.add(t.getImitativeVoiceEntries(
				tab.getBasicTabSymbolProperties(), t.getDurationLabels(), null, voices.get(i), 
				ns.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
//			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);		
	}


	@Test
	public void testGetImitativeVoiceEntriesNonTab() {
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-WTC",
			"thesis"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			// 3vv
			"3vv/bach-WTC1-fuga_2-BWV_847", // correct
			"3vv/bach-WTC1-fuga_3-BWV_848", // correct
			"3vv/bach-WTC1-fuga_6-BWV_851",	// correct
			"3vv/bach-WTC2-fuga_3-BWV_872", // incorrect: false candidate at density 2
			"3vv/bach-WTC2-fuga_12-BWV_881", // incorrect: head motif not repeated literally
			"3vv/bach-WTC2-fuga_21-BWV_890", // incorrect: voice crossing at density 3
			// 4vv 
			"4vv/bach-WTC1-fuga_1-BWV_846", // correct 
			"4vv/bach-WTC1-fuga_5-BWV_850", // correct 
			"4vv/bach-WTC1-fuga_12-BWV_857", // correct 
			"4vv/bach-WTC1-fuga_18-BWV_863", // correct after re-establishing HMN at density 4
			"4vv/bach-WTC2-fuga_2-BWV_871", // incorrect: false candidate at density 4
			"4vv/bach-WTC2-fuga_17-BWV_886", // incorrect: wrong motif at density 4
		});

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// 3vv
		List<List<Integer>> bwv847 = new ArrayList<List<Integer>>();
		bwv847.add(Arrays.asList(new Integer[]{1, 0}));
		bwv847.add(Arrays.asList(new Integer[]{0, 21, 22, 101, 102}));
		bwv847.add(Arrays.asList(new Integer[]{1, 1, 0, 2, 0}));
		expected.add(bwv847);
		//
		List<List<Integer>> bwv848 = new ArrayList<List<Integer>>();
		bwv848.add(Arrays.asList(new Integer[]{0, 0}));
		bwv848.add(Arrays.asList(new Integer[]{0, 21, 22, 68, 69}));
		bwv848.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1}));
		expected.add(bwv848);
		//
		List<List<Integer>> bwv851 = new ArrayList<List<Integer>>();
		bwv851.add(Arrays.asList(new Integer[]{0, 0}));
		bwv851.add(Arrays.asList(new Integer[]{0, 19, 69, 70}));
		bwv851.add(Arrays.asList(new Integer[]{0, 1, 2, 0}));
		expected.add(bwv851);
		//
		List<List<Integer>> bwv872 = new ArrayList<List<Integer>>();
		bwv872.add(Arrays.asList(new Integer[]{-1, 1}));
		bwv872.add(Arrays.asList(new Integer[]{0, 5, 6, 13, 14}));
		bwv872.add(Arrays.asList(new Integer[]{0, 2, 0, 1, 0}));
		expected.add(bwv872);
		//
		List<List<Integer>> bwv881 = new ArrayList<List<Integer>>();
//		bwv881.add(Arrays.asList(new Integer[]{-1, -1}));
//		bwv881.add(Arrays.asList(new Integer[]{0, 28, 29, 115, 116, 117}));
//		bwv881.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1, 0}));
		bwv881 = null; // null because more than half of the new entries (of which there are two) are -1
		expected.add(bwv881);
		//
		List<List<Integer>> bwv890 = new ArrayList<List<Integer>>();
		bwv890.add(Arrays.asList(new Integer[]{1, -1}));
		bwv890.add(Arrays.asList(new Integer[]{0, 24, 92}));
		bwv890.add(Arrays.asList(new Integer[]{1, 0, 2}));
		expected.add(bwv890);
		// 4vv
		List<List<Integer>> bwv846 = new ArrayList<List<Integer>>();
		bwv846.add(Arrays.asList(new Integer[]{1, 0, 0}));
		bwv846.add(Arrays.asList(new Integer[]{0, 15, 16, 44, 45, 46, 86, 87, 88}));
		bwv846.add(Arrays.asList(new Integer[]{1, 1, 0, 2, 1, 0, 3, 2, 1}));
		expected.add(bwv846);
		//
		List<List<Integer>> bwv850 = new ArrayList<List<Integer>>();
		bwv850.add(Arrays.asList(new Integer[]{1, 2, 3}));
		bwv850.add(Arrays.asList(new Integer[]{0, 14, 15, 50, 51, 52, 75, 76, 77, 78}));
		bwv850.add(Arrays.asList(new Integer[]{3, 3, 2, 3, 2, 1, 3, 2, 1, 0}));
		expected.add(bwv850);
		//
		List<List<Integer>> bwv857 = new ArrayList<List<Integer>>();
		bwv857.add(Arrays.asList(new Integer[]{1, 0, 3}));
		bwv857.add(Arrays.asList(new Integer[]{0, 34, 35, 98, 99, 100, 243, 244, 245, 246}));
		bwv857.add(Arrays.asList(new Integer[]{2, 2, 1, 3, 2, 1, 3, 2, 1, 0}));
		expected.add(bwv857);
		//
		List<List<Integer>> bwv863 = new ArrayList<List<Integer>>();
		bwv863.add(Arrays.asList(new Integer[]{1, 2, 0}));
		bwv863.add(Arrays.asList(new Integer[]{0, 16, 17, 46, 47, 48, 85, 86, 87}));
		bwv863.add(Arrays.asList(new Integer[]{2, 2, 1, 2, 1, 0, 3, 2, 1, 0}));
		expected.add(bwv863);
		//
		List<List<Integer>> bwv871 = new ArrayList<List<Integer>>();
		bwv871.add(Arrays.asList(new Integer[]{1, 0, -1}));
		bwv871.add(Arrays.asList(new Integer[]{0, 9, 43, 44, 426, 427, 428}));
		bwv871.add(Arrays.asList(new Integer[]{1, 0, 2, 0, 3, 2, 1}));
		expected.add(bwv871);
		//
		List<List<Integer>> bwv886 = new ArrayList<List<Integer>>();
		bwv886.add(Arrays.asList(new Integer[]{1, 0, 3}));
		bwv886.add(Arrays.asList(new Integer[]{0, 21, 22, 76, 77, 516, 517}));
		bwv886.add(Arrays.asList(new Integer[]{2, 2, 1, 3, 2, 2, 0}));
		expected.add(bwv886);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4});
		for (int i = 0; i < fileNames.size(); i++) {
			Transcription t = 
				new Transcription(new File(prefix + fileNames.get(i) + MIDIImport.EXTENSION));
			actual.add(t.getImitativeVoiceEntries(null, null, t.getBasicNoteProperties(), 
				voices.get(i), 3));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
//			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	// determineVoiceEntriesHIGHLEVEL() flags the intabulations as follows:
	// int 3vv --> n=3: all non-imitative except mess_pensees, nun_volget, pleni_de (all correct)
	// int 4vv --> n=3: all non-imitative except absolon_fili (at n=2), in_exitu (at n=2),
	//					qui_habitat (correct apart from mais_mamignone (which is also imitative)) 
	// 					absolon_fili is flagged as non-imitative at n=3 because
	//						no motif is found at density 2 (not enough motif notes)
	//						no motif is found at density 3, 4 (non-literal motif repetition)
	//					in_exitu is flagged as non-imitative at n=3 because
	// 						there are not enough notes of density 1 for a motif
	//					mais_mamignone is flagged as non-imitative at n=2 and n=3 because 
	//						at n=2, no motif is found at density 2, 3, 4 (non-literal motif repetition)
	//						at n=3, there are not enough notes of density 1 for a motif 
	//
	// determineVoiceEntriesHIGHLEVEL() flags the inventions and fugues as follows:
	// inv 2vv --> n=3: all non-imitative except 772, 773, 774, 775, 779, 781 (all correct)
	// inv 3vv --> n=3: all non-imitative (all correct)
	// WTC 3vv --> n=3: all imitative except 881 (correct apart from 881 (which is also imitative))
	//					881 is  flagged as non-imitative at n=3 because 
	//						no motif is found at density 2, 3 (non-literal motif repetition)
	// WTC 4vv --> n=3: all imitative (all correct)
	@Test
	public void testGetNonImitativeVoiceEntries() {
		String prefixTab = CLInterface.getPathString(Arrays.asList(
			paths.get("ENCODINGS_PATH"),
			"thesis-int"
		));
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"thesis-int"
		));
		
		List<String> fileNames = Arrays.asList(new String[]{
			// 3vv
			"3vv/newsidler-1536_7-disant_adiu", // correct (full & minimum) TODO fix SNU
			"3vv/judenkuenig-1523_2-elslein_liebes", // correct (full & minimum) TODO fix SNU
			"3vv/phalese-1547_7-tant_que-3vv", // correct correct (full & minimum)
			// 4vv
			"4vv/rotta-1546_15-bramo_morir", // correct correct (full & minimum)
			"4vv/phalese-1547_7-tant_que-4vv", // correct (full & minimum)
			"4vv/ochsenkun-1558_5-herr_gott", // correct (full & minimum)
			"4vv/abondante-1548_1-mais_mamignone", // incorrect (full & minimum)): voice crossing at density 4 TODO fix SNU  
			"4vv/phalese-1563_12-las_on", // correct (full & minimum)
			"4vv/barbetta-1582_1-il_nest", // correct (full & minimum)
		});

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Full durations
		// int 3vv
		List<List<Integer>> disantAdiu = new ArrayList<List<Integer>>();
		disantAdiu.add(Arrays.asList(new Integer[]{1})); 
		disantAdiu.add(Arrays.asList(new Integer[]{0, 2, 3, 4}));
		disantAdiu.add(Arrays.asList(new Integer[]{1, 2, 1, 0}));
		expected.add(disantAdiu);
		//
		List<List<Integer>> elsleinLiebes = new ArrayList<List<Integer>>();
		elsleinLiebes.add(Arrays.asList(new Integer[]{1})); 
		elsleinLiebes.add(Arrays.asList(new Integer[]{0, 1, 2, 3, 4}));
		elsleinLiebes.add(Arrays.asList(new Integer[]{2, 0, 2, 1, 0}));
		expected.add(elsleinLiebes);
		//
		List<List<Integer>> tantQue3vv = new ArrayList<List<Integer>>();
		tantQue3vv.add(Arrays.asList(new Integer[]{})); 
		tantQue3vv.add(Arrays.asList(new Integer[]{0, 1, 2}));
		tantQue3vv.add(Arrays.asList(new Integer[]{2, 1, 0}));
		expected.add(tantQue3vv);
		// 4vv
		List<List<Integer>> bramoMorir = new ArrayList<List<Integer>>();
		bramoMorir.add(Arrays.asList(new Integer[]{})); 
		bramoMorir.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		bramoMorir.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(bramoMorir);
		//
		List<List<Integer>> tantQue4vv = new ArrayList<List<Integer>>();
		tantQue4vv.add(Arrays.asList(new Integer[]{})); 
		tantQue4vv.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		tantQue4vv.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(tantQue4vv);
		//
		List<List<Integer>> herrGott = new ArrayList<List<Integer>>();
		herrGott.add(Arrays.asList(new Integer[]{})); 
		herrGott.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		herrGott.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(herrGott);
		//
		List<List<Integer>> maisMamignone = new ArrayList<List<Integer>>();
		maisMamignone.add(Arrays.asList(new Integer[]{1, 1, 2})); 
		maisMamignone.add(Arrays.asList(new Integer[]{0, 2, 6, 26}));
		maisMamignone.add(Arrays.asList(new Integer[]{3, 0, 2, 1}));
		expected.add(maisMamignone);	
		//
		List<List<Integer>> lasOn = new ArrayList<List<Integer>>();
		lasOn.add(Arrays.asList(new Integer[]{})); 
		lasOn.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		lasOn.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(lasOn);
		//
		List<List<Integer>> ilNest = new ArrayList<List<Integer>>();
		ilNest.add(Arrays.asList(new Integer[]{})); 
		ilNest.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		ilNest.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(ilNest);	

		// Minimum durations
		// int 3vv
		disantAdiu = new ArrayList<List<Integer>>();
		disantAdiu.add(Arrays.asList(new Integer[]{1})); 
		disantAdiu.add(Arrays.asList(new Integer[]{0, 2, 3, 4}));
		disantAdiu.add(Arrays.asList(new Integer[]{1, 2, 1, 0}));
		expected.add(disantAdiu);
		//
		elsleinLiebes = new ArrayList<List<Integer>>();
		elsleinLiebes.add(Arrays.asList(new Integer[]{1})); 
		elsleinLiebes.add(Arrays.asList(new Integer[]{0, 1, 2, 3, 4}));
		elsleinLiebes.add(Arrays.asList(new Integer[]{2, 0, 2, 1, 0}));
		expected.add(elsleinLiebes);
		//
		tantQue3vv = new ArrayList<List<Integer>>();
		tantQue3vv.add(Arrays.asList(new Integer[]{})); 
		tantQue3vv.add(Arrays.asList(new Integer[]{0, 1, 2}));
		tantQue3vv.add(Arrays.asList(new Integer[]{2, 1, 0}));
		expected.add(tantQue3vv);
		// 4vv
		bramoMorir = new ArrayList<List<Integer>>();
		bramoMorir.add(Arrays.asList(new Integer[]{})); 
		bramoMorir.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		bramoMorir.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(bramoMorir);
		//
		tantQue4vv = new ArrayList<List<Integer>>();
		tantQue4vv.add(Arrays.asList(new Integer[]{})); 
		tantQue4vv.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		tantQue4vv.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(tantQue4vv);
		//
		herrGott = new ArrayList<List<Integer>>();
		herrGott.add(Arrays.asList(new Integer[]{})); 
		herrGott.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		herrGott.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(herrGott);
		//
		maisMamignone = new ArrayList<List<Integer>>();
		maisMamignone.add(Arrays.asList(new Integer[]{1, 1, 2})); 
		maisMamignone.add(Arrays.asList(new Integer[]{0, 4, 5, 8, 9, 10, 30, 31, 32, 33}));
		maisMamignone.add(Arrays.asList(new Integer[]{3, 3, 0, 3, 2, 0, 3, 2, 1, 0}));
		expected.add(maisMamignone);	
		//
		lasOn = new ArrayList<List<Integer>>();
		lasOn.add(Arrays.asList(new Integer[]{})); 
		lasOn.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		lasOn.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(lasOn);
		//
		ilNest = new ArrayList<List<Integer>>();
		ilNest.add(Arrays.asList(new Integer[]{})); 
		ilNest.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		ilNest.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		expected.add(ilNest);
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{3, 3, 3, 4, 4, 4, 4, 4, 4});
		// Full durations
		for (int i = 0; i < fileNames.size(); i++) {
			String piece = fileNames.get(i);
			File enc = new File(prefixTab + piece + Encoding.EXTENSION);
			Tablature tab = new Tablature(enc);
			Transcription t = new Transcription(new File(prefix + piece + MIDIImport.EXTENSION), enc);
			actual.add(t.getNonImitativeVoiceEntries(tab.getBasicTabSymbolProperties(), 
				t.getDurationLabels(), null, voices.get(i), 3));
		}
		// Minimum durations
		for (int i = 0; i < fileNames.size(); i++) {
			String piece = fileNames.get(i);
			File enc = new File(prefixTab + piece + Encoding.EXTENSION);
			Tablature tab = new Tablature(enc);
			Transcription t = new Transcription(new File(prefix + piece + MIDIImport.EXTENSION), enc);
			actual.add(t.getNonImitativeVoiceEntries(tab.getBasicTabSymbolProperties(), 
				t.getMinimumDurationLabels(), null, voices.get(i), 3));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
//			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNonImitativeVoiceEntriesNonTab() {
		String prefix = CLInterface.getPathString(Arrays.asList(
			paths.get("MIDI_PATH"),
			"bach-inv",
			"thesis"
		));
		List<String> fileNames = Arrays.asList(new String[]{
			// inv 2vv
			"2vv/bach-INV-inventio_5-BWV_776", // correct
			"2vv/bach-INV-inventio_6-BWV_777", // correct
			"2vv/bach-INV-inventio_7-BWV_778", // correct
			"2vv/bach-INV-inventio_9-BWV_780", // correct
			"2vv/bach-INV-inventio_11-BWV_782", // correct
			"2vv/bach-INV-inventio_12-BWV_783", // correct
			"2vv/bach-INV-inventio_13-BWV_784", // correct
			"2vv/bach-INV-inventio_14-BWV_785", // correct 
			"2vv/bach-INV-inventio_15-BWV_786",	// incorrect: rest at density 2 (PROBLEM IN ALG: no rest assumption does not hold --> wrong first rightChord --> config calc gives incorrect result)
			// inv 3vv
			"3vv/bach-INV-inventio_1-BWV_787", // correct
			"3vv/bach-INV-inventio_2-BWV_788", // incorrect: voice crossing at density 3 (PROBLEM IN DATA: unison not correctly encoded in MIDI --> wrong first rightChord --> vc assumption does not hold --> config calc gives incorrect result) (would not occur if unison would be correct)
			"3vv/bach-INV-inventio_3-BWV_789", // correct
			"3vv/bach-INV-inventio_4-BWV_790", // correct
			"3vv/bach-INV-inventio_5-BWV_791", // correct
			"3vv/bach-INV-inventio_6-BWV_792", // correct
			"3vv/bach-INV-inventio_7-BWV_793", // correct
			"3vv/bach-INV-inventio_8-BWV_794", // correct
			"3vv/bach-INV-inventio_9-BWV_795", // correct
			"3vv/bach-INV-inventio_10-BWV_796", // correct
			"3vv/bach-INV-inventio_11-BWV_797", // correct
			"3vv/bach-INV-inventio_12-BWV_798", // correct
			"3vv/bach-INV-inventio_13-BWV_799", // incorrect (PROBLEM IN DATA and ALG: unison not correctly encoded in MIDI --> wrong first rightChord --> config calc gives incorrect result) (would also occur if unison would be correct (but only just: 74 vs 72))
			"3vv/bach-INV-inventio_14-BWV_800", // correct
			"3vv/bach-INV-inventio_15-BWV_801", // incorrect (PROBLEM IN ALG: config calc gives incorrect result)	
			// WTC 3vv
			"3vv/bach-WTC2-fuga_12-BWV_881" // incorrect: at density 2 (PROBLEM IN ALG: config calc gives incorrect result) 
		});

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// inv 2vv
		List<List<Integer>> bwv776 = new ArrayList<List<Integer>>();
		bwv776.add(Arrays.asList(new Integer[]{1})); 
		bwv776.add(Arrays.asList(new Integer[]{0, 3}));
		bwv776.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv776);
		//
		List<List<Integer>> bwv777 = new ArrayList<List<Integer>>();
		bwv777.add(Arrays.asList(new Integer[]{1})); 
		bwv777.add(Arrays.asList(new Integer[]{0, 1}));
		bwv777.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv777);
		//
		List<List<Integer>> bwv778 = new ArrayList<List<Integer>>();
		bwv778.add(Arrays.asList(new Integer[]{1})); 
		bwv778.add(Arrays.asList(new Integer[]{0, 3, 4}));
		bwv778.add(Arrays.asList(new Integer[]{1, 1, 0}));
		expected.add(bwv778);
		//
		List<List<Integer>> bwv780 = new ArrayList<List<Integer>>();
		bwv780.add(Arrays.asList(new Integer[]{})); 
		bwv780.add(Arrays.asList(new Integer[]{0, 1}));
		bwv780.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv780);
		//
		List<List<Integer>> bwv782 = new ArrayList<List<Integer>>();
		bwv782.add(Arrays.asList(new Integer[]{1})); 
		bwv782.add(Arrays.asList(new Integer[]{0, 1}));
		bwv782.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv782);
		//
		List<List<Integer>> bwv783 = new ArrayList<List<Integer>>();
		bwv783.add(Arrays.asList(new Integer[]{})); 
		bwv783.add(Arrays.asList(new Integer[]{0, 1}));
		bwv783.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv783);
		//
		List<List<Integer>> bwv784 = new ArrayList<List<Integer>>();
		bwv784.add(Arrays.asList(new Integer[]{1})); 
		bwv784.add(Arrays.asList(new Integer[]{0, 2, 3}));
		bwv784.add(Arrays.asList(new Integer[]{1, 1, 0}));
		expected.add(bwv784);	
		//
		List<List<Integer>> bwv785 = new ArrayList<List<Integer>>();
		bwv785.add(Arrays.asList(new Integer[]{1})); 
		bwv785.add(Arrays.asList(new Integer[]{0, 1}));
		bwv785.add(Arrays.asList(new Integer[]{1, 0}));
		expected.add(bwv785);
		//
		List<List<Integer>> bwv786 = new ArrayList<List<Integer>>();
		bwv786.add(Arrays.asList(new Integer[]{0})); 
		bwv786.add(Arrays.asList(new Integer[]{0, 3, 4}));
		bwv786.add(Arrays.asList(new Integer[]{0, 1, 0}));
		expected.add(bwv786);	
		
		// inv 3vv
		List<List<Integer>> bwv787 = new ArrayList<List<Integer>>();
		bwv787.add(Arrays.asList(new Integer[]{1, 1}));
		bwv787.add(Arrays.asList(new Integer[]{0, 1, 24}));
		bwv787.add(Arrays.asList(new Integer[]{2, 0, 1}));
		expected.add(bwv787);
		//
		List<List<Integer>> bwv788 = new ArrayList<List<Integer>>();
		bwv788.add(Arrays.asList(new Integer[]{1}));
		bwv788.add(Arrays.asList(new Integer[]{0, 1, 38}));
		bwv788.add(Arrays.asList(new Integer[]{2, 0, 2}));
		expected.add(bwv788);
		//
		List<List<Integer>> bwv789 = new ArrayList<List<Integer>>();
		bwv789.add(Arrays.asList(new Integer[]{1, 1}));
		bwv789.add(Arrays.asList(new Integer[]{0, 1, 47, 48}));
		bwv789.add(Arrays.asList(new Integer[]{2, 0, 2, 1}));
		expected.add(bwv789);
		//
		List<List<Integer>> bwv790 = new ArrayList<List<Integer>>();
		bwv790.add(Arrays.asList(new Integer[]{1, 1}));
		bwv790.add(Arrays.asList(new Integer[]{0, 1, 19, 20}));
		bwv790.add(Arrays.asList(new Integer[]{2, 0, 1, 0}));
		expected.add(bwv790);
		//
		List<List<Integer>> bwv791 = new ArrayList<List<Integer>>();
		bwv791.add(Arrays.asList(new Integer[]{1, 1}));
		bwv791.add(Arrays.asList(new Integer[]{0, 4, 20, 21}));
		bwv791.add(Arrays.asList(new Integer[]{2, 0, 1, 0}));
		expected.add(bwv791);
		//
		List<List<Integer>> bwv792 = new ArrayList<List<Integer>>();
		bwv792.add(Arrays.asList(new Integer[]{1, 1}));
		bwv792.add(Arrays.asList(new Integer[]{0, 1, 10, 11, 12}));
		bwv792.add(Arrays.asList(new Integer[]{2, 0, 2, 1, 0}));
		expected.add(bwv792);
		//
		List<List<Integer>> bwv793 = new ArrayList<List<Integer>>();
		bwv793.add(Arrays.asList(new Integer[]{1, 1}));
		bwv793.add(Arrays.asList(new Integer[]{0, 1, 2, 21, 22}));
		bwv793.add(Arrays.asList(new Integer[]{2, 2, 0, 1, 0}));
		expected.add(bwv793);
		//
		List<List<Integer>> bwv794 = new ArrayList<List<Integer>>();
		bwv794.add(Arrays.asList(new Integer[]{1, 2}));
		bwv794.add(Arrays.asList(new Integer[]{0, 1, 20, 21, 22}));
		bwv794.add(Arrays.asList(new Integer[]{2, 1, 2, 1, 0}));
		expected.add(bwv794);
		//
		List<List<Integer>> bwv795 = new ArrayList<List<Integer>>();
		bwv795.add(Arrays.asList(new Integer[]{1, 2}));
		bwv795.add(Arrays.asList(new Integer[]{0, 1, 25}));
		bwv795.add(Arrays.asList(new Integer[]{2, 1, 0}));
		expected.add(bwv795);
		//
		List<List<Integer>> bwv796 = new ArrayList<List<Integer>>();
		bwv796.add(Arrays.asList(new Integer[]{1, 1})); 
		bwv796.add(Arrays.asList(new Integer[]{0, 1, 28}));
		bwv796.add(Arrays.asList(new Integer[]{2, 0, 1}));
		expected.add(bwv796);
		//
		List<List<Integer>> bwv797 = new ArrayList<List<Integer>>();
		bwv797.add(Arrays.asList(new Integer[]{1, 1})); 
		bwv797.add(Arrays.asList(new Integer[]{0, 1, 8}));
		bwv797.add(Arrays.asList(new Integer[]{2, 0, 1}));
		expected.add(bwv797);
		//
		List<List<Integer>> bwv798 = new ArrayList<List<Integer>>();
		bwv798.add(Arrays.asList(new Integer[]{1})); 
		bwv798.add(Arrays.asList(new Integer[]{0, 1, 37, 38, 39}));
		bwv798.add(Arrays.asList(new Integer[]{2, 0, 2, 1, 0}));
		expected.add(bwv798);
		//
		List<List<Integer>> bwv799 = new ArrayList<List<Integer>>();
		bwv799.add(Arrays.asList(new Integer[]{0})); 
		bwv799.add(Arrays.asList(new Integer[]{0, 1, 39, 40}));
		bwv799.add(Arrays.asList(new Integer[]{1, 0, 2, 0}));
		expected.add(bwv799);
		//
		List<List<Integer>> bwv800 = new ArrayList<List<Integer>>();
		bwv800.add(Arrays.asList(new Integer[]{2})); 
		bwv800.add(Arrays.asList(new Integer[]{0, 1, 20, 21}));
		bwv800.add(Arrays.asList(new Integer[]{2, 1, 1, 0}));
		expected.add(bwv800);
		//
		List<List<Integer>> bwv801 = new ArrayList<List<Integer>>();
		bwv801.add(Arrays.asList(new Integer[]{2})); 
		bwv801.add(Arrays.asList(new Integer[]{0, 1, 117, 118, 119}));
		bwv801.add(Arrays.asList(new Integer[]{2, 1, 2, 1, 0}));
		expected.add(bwv801);
		
		// WTC 3vv
		List<List<Integer>> bwv881 = new ArrayList<List<Integer>>();
		bwv881.add(Arrays.asList(new Integer[]{1, 0})); 
		bwv881.add(Arrays.asList(new Integer[]{0, 28, 29, 115, 116, 117}));
		bwv881.add(Arrays.asList(new Integer[]{1, 1, 0, 2, 1, 0}));
		expected.add(bwv881);
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{
			2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3});
		for (int i = 0; i < fileNames.size(); i++) {
			if (fileNames.get(i).contains("WTC")) {
				prefix = CLInterface.getPathString(Arrays.asList(
					paths.get("MIDI_PATH"),
					"bach-WTC",
					"thesis"
				));
			}
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + MIDIImport.EXTENSION));
			actual.add(t.getNonImitativeVoiceEntries(null, null, t.getBasicNoteProperties(), 
				voices.get(i), 3));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
//			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testQuantiseDuration() {
		List<Rational> with16th = Arrays.asList(new Rational[]{
			new Rational(4, 64), // to 1/16 (with 1/16)
			new Rational(5, 64), // to 1/8 (with 1/16)
			new Rational(6, 64), // to 1/8 (with 1/16)
			new Rational(7, 64), // to 1/8 (with 1/16)
			new Rational(8, 64)  // to 1/8 (with 1/16)
		});

		List<Rational> with32nd = Arrays.asList(new Rational[]{
			new Rational(4, 64), // to 1/16 (with 1/32)
			new Rational(5, 64), // to 3/32 (with 1/32)
			new Rational(6, 64), // to 3/32 (with 1/32)
			new Rational(7, 64), // to 1/8 (with 1/32)
			new Rational(8, 64)	 // to 1/8 (with 1/32)
		});

		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(1, 16),
			new Rational(1, 8),
			new Rational(1, 8),
			new Rational(1, 8),
			new Rational(1, 8),
			new Rational(1, 16),
			new Rational(3, 32),
			new Rational(3, 32),
			new Rational(1, 8),
			new Rational(1, 8),
		});
		
		List<Rational> actual = new ArrayList<Rational>();
		for (Rational r : with16th) {
			actual.add(Transcription.quantiseDuration(r, new Rational(1, 16)));
		}
		for (Rational r : with32nd) {
			actual.add(Transcription.quantiseDuration(r, new Rational(1, 32))); 
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNonUnisonNeighbourChord() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
		
		// a. Previous
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Chord 0: preceded by nothing
		expected.add(null);
//		// Chord 6: preceded by unison chord, previous is chord 4
		List<List<Integer>> chord4 = new ArrayList<List<Integer>>();
		chord4.add(Arrays.asList(new Integer[]{45, 1, 8}));
		chord4.add(Arrays.asList(new Integer[]{50, null, null}));
		chord4.add(Arrays.asList(new Integer[]{59, null, null}));
		chord4.add(Arrays.asList(new Integer[]{65, null, null}));
		expected.add(chord4);
		// Chord 5: not preceded by unison chord, previous is also chord 4 
		expected.add(chord4);
		
		// b. Next
		// Chord 4: followed by unison chord, next is chord 6
		List<List<Integer>> chord6 = new ArrayList<List<Integer>>();
		chord6.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord6.add(Arrays.asList(new Integer[]{57, null, null}));
		chord6.add(Arrays.asList(new Integer[]{60, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{64, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord6);
		// Chord 5: not followed by unison chord, next is also chord 6 
		expected.add(chord6);
		// Chord 15: followed by nothing
		expected.add(null);
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> lowestNoteInd = Arrays.asList(new Integer[]{0, 19, 14});
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		List<List<Double>> durationLabels = transcription.getDurationLabels();
		for (int i : lowestNoteInd) {
			actual.add(transcription.getNonUnisonNeighbourChord(btp, durationLabels, null, -1, i));
		}
		lowestNoteInd = Arrays.asList(new Integer[]{13, 14, 35});
		for (int i : lowestNoteInd) {
			actual.add(transcription.getNonUnisonNeighbourChord(btp, durationLabels, null, 1, i));
		}
			
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetNonUnisonNeighbourChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);
		
		// a. Previous
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Chord 0: preceded by nothing
		expected.add(null);
		// Chord 4: preceded by unison chord, previous is chord 2
		List<List<Integer>> chord2 = new ArrayList<List<Integer>>();
		chord2.add(Arrays.asList(new Integer[]{48, 1, 16}));
		chord2.add(Arrays.asList(new Integer[]{57, null, null}));
		chord2.add(Arrays.asList(new Integer[]{72, null, null}));
		expected.add(chord2);
		// Chord 3: not preceded by unison chord, previous is also chord 2 
		expected.add(chord2);
		
		// b. Next
		// Chord 4: followed by unison chord, next is chord 6
		List<List<Integer>> chord6 = new ArrayList<List<Integer>>();
		chord6.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord6.add(Arrays.asList(new Integer[]{57, null, null}));
		chord6.add(Arrays.asList(new Integer[]{60, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{64, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord6);
		// Chord 5: not followed by unison chord, next is also chord 6 
		expected.add(chord6);
		// Chord 15: followed by nothing
		expected.add(null);
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> lowestNoteInd = Arrays.asList(new Integer[]{0, 14, 9});
		Integer[][] bnp = transcription.getBasicNoteProperties();
		for (int i : lowestNoteInd) {
			actual.add(transcription.getNonUnisonNeighbourChord(null, null, bnp, -1, i));
		}
		lowestNoteInd = Arrays.asList(new Integer[]{14, 15, 36});
		for (int i : lowestNoteInd) {
			actual.add(transcription.getNonUnisonNeighbourChord(null, null, bnp, 1, i));
		}
			
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null && actual.get(i) != null) {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					if (expected.get(i).get(j) != null && actual.get(i).get(j) != null) {
						assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
						for (int k = 0; k < expected.get(i).get(j).size(); k++) {
							assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
						}
					}
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetChordInfo() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();

		List<List<Integer>> chord0 = new ArrayList<List<Integer>>();
		chord0.add(Arrays.asList(new Integer[]{50, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{65, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord0);
		//
		List<List<Integer>> chord1 = new ArrayList<List<Integer>>();
		chord1.add(Arrays.asList(new Integer[]{45, 3, 16}));
		chord1.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord1.add(Arrays.asList(new Integer[]{69, 1, 8}));
		chord1.add(Arrays.asList(new Integer[]{72, 1, 4}));
		expected.add(chord1);
		//
		List<List<Integer>> chord2 = new ArrayList<List<Integer>>();
		chord2.add(Arrays.asList(new Integer[]{48, 1, 16}));
		chord2.add(Arrays.asList(new Integer[]{57, null, null}));
		chord2.add(Arrays.asList(new Integer[]{72, null, null}));
		expected.add(chord2);
		// 
		List<List<Integer>> chord3 = new ArrayList<List<Integer>>();
		chord3.add(Arrays.asList(new Integer[]{47, 1, 8}));
		chord3.add(Arrays.asList(new Integer[]{50, 1, 4}));
		chord3.add(Arrays.asList(new Integer[]{59, 1, 4}));
		chord3.add(Arrays.asList(new Integer[]{65, 1, 4}));
		expected.add(chord3);
		//
		List<List<Integer>> chord4 = new ArrayList<List<Integer>>();
		chord4.add(Arrays.asList(new Integer[]{45, 1, 8}));
		chord4.add(Arrays.asList(new Integer[]{50, null, null}));
		chord4.add(Arrays.asList(new Integer[]{59, null, null}));
		chord4.add(Arrays.asList(new Integer[]{65, null, null}));
		expected.add(chord4);
		//
		List<List<Integer>> chord5 = new ArrayList<List<Integer>>();
		chord5.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{57, 1, 2}));
		chord5.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{60, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord5);
		// 
		List<List<Integer>> chord6 = new ArrayList<List<Integer>>();
		chord6.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord6.add(Arrays.asList(new Integer[]{57, null, null}));
		chord6.add(Arrays.asList(new Integer[]{60, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{64, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord6);
		//
		List<List<Integer>> chord7 = new ArrayList<List<Integer>>();
		chord7.add(Arrays.asList(new Integer[]{45, null, null}));
		chord7.add(Arrays.asList(new Integer[]{57, null, null}));
		chord7.add(Arrays.asList(new Integer[]{59, 1, 8}));
		chord7.add(Arrays.asList(new Integer[]{68, 1, 8}));
		chord7.add(Arrays.asList(new Integer[]{69, null, null}));
		expected.add(chord7);
		//
		List<List<Integer>> chord8 = new ArrayList<List<Integer>>();
		chord8.add(Arrays.asList(new Integer[]{45, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{57, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{64, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{69, 1, 16}));
		expected.add(chord8);
		//
		List<List<Integer>> chord9 = new ArrayList<List<Integer>>();
		chord9.add(Arrays.asList(new Integer[]{45, null, null}));
		chord9.add(Arrays.asList(new Integer[]{57, null, null}));
		chord9.add(Arrays.asList(new Integer[]{64, null, null}));
		chord9.add(Arrays.asList(new Integer[]{68, 1, 16}));
		expected.add(chord9);
		List<List<Integer>> chord10 = new ArrayList<List<Integer>>();
		chord10.add(Arrays.asList(new Integer[]{45, null, null}));
		chord10.add(Arrays.asList(new Integer[]{57, null, null}));
		chord10.add(Arrays.asList(new Integer[]{64, null, null}));
		chord10.add(Arrays.asList(new Integer[]{69, 1, 32}));
		expected.add(chord10);
		List<List<Integer>> chord11 = new ArrayList<List<Integer>>();
		chord11.add(Arrays.asList(new Integer[]{45, null, null}));
		chord11.add(Arrays.asList(new Integer[]{57, null, null}));
		chord11.add(Arrays.asList(new Integer[]{64, null, null}));
		chord11.add(Arrays.asList(new Integer[]{68, 1, 32}));
		expected.add(chord11);
		List<List<Integer>> chord12 = new ArrayList<List<Integer>>();
		chord12.add(Arrays.asList(new Integer[]{45, null, null}));
		chord12.add(Arrays.asList(new Integer[]{57, null, null}));
		chord12.add(Arrays.asList(new Integer[]{64, null, null}));
		chord12.add(Arrays.asList(new Integer[]{66, 1, 32}));
		expected.add(chord12);
		List<List<Integer>> chord13 = new ArrayList<List<Integer>>();
		chord13.add(Arrays.asList(new Integer[]{45, null, null}));
		chord13.add(Arrays.asList(new Integer[]{57, null, null}));
		chord13.add(Arrays.asList(new Integer[]{64, null, null}));
		chord13.add(Arrays.asList(new Integer[]{68, 1, 32}));
		expected.add(chord13);
		List<List<Integer>> chord14 = new ArrayList<List<Integer>>();
		chord14.add(Arrays.asList(new Integer[]{45, null, null}));
		chord14.add(Arrays.asList(new Integer[]{57, null, null}));
		chord14.add(Arrays.asList(new Integer[]{64, null, null}));
		chord14.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord14);
		//
		List<List<Integer>> chord15 = new ArrayList<List<Integer>>();
		chord15.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{64, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord15);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> lowestNoteInd = Arrays.asList(new Integer[]{0, 4, 8, 9, 13, 14, 19, 23, 
			25, 29, 30, 31, 32, 33, 34, 35});
		for (int i : lowestNoteInd) {
			actual.add(transcription.getChordInfo(tablature.getBasicTabSymbolProperties(), 
				transcription.getDurationLabels(), null, i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);	
	}


	@Test
	public void testGetChordInfoNonTab() {	
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();

		List<List<Integer>> chord0 = new ArrayList<List<Integer>>();
		chord0.add(Arrays.asList(new Integer[]{50, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{65, 1, 4}));
		chord0.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord0);
		//
		List<List<Integer>> chord1 = new ArrayList<List<Integer>>();
		chord1.add(Arrays.asList(new Integer[]{45, 3, 16}));
		chord1.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord1.add(Arrays.asList(new Integer[]{69, 1, 8}));
		chord1.add(Arrays.asList(new Integer[]{72, 1, 4}));
		expected.add(chord1);
		//
		List<List<Integer>> chord2 = new ArrayList<List<Integer>>();
		chord2.add(Arrays.asList(new Integer[]{48, 1, 16}));
		chord2.add(Arrays.asList(new Integer[]{57, null, null}));
		chord2.add(Arrays.asList(new Integer[]{72, null, null}));
		expected.add(chord2);
		// 
		List<List<Integer>> chord3 = new ArrayList<List<Integer>>();
		chord3.add(Arrays.asList(new Integer[]{47, 1, 8}));
		chord3.add(Arrays.asList(new Integer[]{50, 1, 4}));
		chord3.add(Arrays.asList(new Integer[]{59, 1, 4}));
		chord3.add(Arrays.asList(new Integer[]{65, 1, 4}));
		chord3.add(Arrays.asList(new Integer[]{65, 1, 8}));
		expected.add(chord3);
		//
		List<List<Integer>> chord4 = new ArrayList<List<Integer>>();
		chord4.add(Arrays.asList(new Integer[]{45, 1, 8}));
		chord4.add(Arrays.asList(new Integer[]{50, null, null}));
		chord4.add(Arrays.asList(new Integer[]{59, null, null}));
		chord4.add(Arrays.asList(new Integer[]{65, null, null}));
		expected.add(chord4);
		//
		List<List<Integer>> chord5 = new ArrayList<List<Integer>>();
		chord5.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{57, 1, 2}));
		chord5.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{60, 1, 4}));
		chord5.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord5);
		// 
		List<List<Integer>> chord6 = new ArrayList<List<Integer>>();
		chord6.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord6.add(Arrays.asList(new Integer[]{57, null, null}));
		chord6.add(Arrays.asList(new Integer[]{60, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{64, 1, 8}));
		chord6.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord6);
		//
		List<List<Integer>> chord7 = new ArrayList<List<Integer>>();
		chord7.add(Arrays.asList(new Integer[]{45, null, null}));
		chord7.add(Arrays.asList(new Integer[]{57, null, null}));
		chord7.add(Arrays.asList(new Integer[]{59, 1, 8}));
		chord7.add(Arrays.asList(new Integer[]{68, 1, 8}));
		chord7.add(Arrays.asList(new Integer[]{69, null, null}));
		expected.add(chord7);
		//
		List<List<Integer>> chord8 = new ArrayList<List<Integer>>();
		chord8.add(Arrays.asList(new Integer[]{45, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{57, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{64, 1, 2}));
		chord8.add(Arrays.asList(new Integer[]{69, 1, 16}));
		expected.add(chord8);
		//
		List<List<Integer>> chord9 = new ArrayList<List<Integer>>();
		chord9.add(Arrays.asList(new Integer[]{45, null, null}));
		chord9.add(Arrays.asList(new Integer[]{57, null, null}));
		chord9.add(Arrays.asList(new Integer[]{64, null, null}));
		chord9.add(Arrays.asList(new Integer[]{68, 1, 16}));
		expected.add(chord9);
		List<List<Integer>> chord10 = new ArrayList<List<Integer>>();
		chord10.add(Arrays.asList(new Integer[]{45, null, null}));
		chord10.add(Arrays.asList(new Integer[]{57, null, null}));
		chord10.add(Arrays.asList(new Integer[]{64, null, null}));
		chord10.add(Arrays.asList(new Integer[]{69, 1, 32}));
		expected.add(chord10);
		List<List<Integer>> chord11 = new ArrayList<List<Integer>>();
		chord11.add(Arrays.asList(new Integer[]{45, null, null}));
		chord11.add(Arrays.asList(new Integer[]{57, null, null}));
		chord11.add(Arrays.asList(new Integer[]{64, null, null}));
		chord11.add(Arrays.asList(new Integer[]{68, 1, 32}));
		expected.add(chord11);
		List<List<Integer>> chord12 = new ArrayList<List<Integer>>();
		chord12.add(Arrays.asList(new Integer[]{45, null, null}));
		chord12.add(Arrays.asList(new Integer[]{57, null, null}));
		chord12.add(Arrays.asList(new Integer[]{64, null, null}));
		chord12.add(Arrays.asList(new Integer[]{66, 1, 32}));
		expected.add(chord12);
		List<List<Integer>> chord13 = new ArrayList<List<Integer>>();
		chord13.add(Arrays.asList(new Integer[]{45, null, null}));
		chord13.add(Arrays.asList(new Integer[]{57, null, null}));
		chord13.add(Arrays.asList(new Integer[]{64, null, null}));
		chord13.add(Arrays.asList(new Integer[]{68, 1, 32}));
		expected.add(chord13);
		List<List<Integer>> chord14 = new ArrayList<List<Integer>>();
		chord14.add(Arrays.asList(new Integer[]{45, null, null}));
		chord14.add(Arrays.asList(new Integer[]{57, null, null}));
		chord14.add(Arrays.asList(new Integer[]{64, null, null}));
		chord14.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord14);
		//
		List<List<Integer>> chord15 = new ArrayList<List<Integer>>();
		chord15.add(Arrays.asList(new Integer[]{45, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{57, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{64, 1, 4}));
		chord15.add(Arrays.asList(new Integer[]{69, 1, 4}));
		expected.add(chord15);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> lowestNoteInd = Arrays.asList(new Integer[]{0, 4, 8, 9, 14, 15, 20, 24, 
			26, 30, 31, 32, 33, 34, 35, 36});
		for (int i : lowestNoteInd) {
			actual.add(transcription.getChordInfo(null, null, transcription.getBasicNoteProperties(), i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);	
	}


	@Test
	public void testGetMeter() {
		Transcription tr = new Transcription(midiTestGetMeterKeyInfo);
		
		List<Rational> all = Arrays.asList(new Rational[]{
			new Rational(1, 4), // b0, Q in
			new Rational(3, 4), // begin b1 
			new Rational(12, 4), // b2, Q in
			new Rational(19, 4), // begin b3
			new Rational(32, 4), // b4, Q in
			new Rational(43, 4), // begin b5
			new Rational(48, 4), // b6, Q in
			new Rational(51, 4), // begin b7
			new Rational(213, 16) // b8, Q in
		});
		
		List<Rational> expected = Arrays.asList(
			new Rational(3, 4),
			new Rational(2, 1),
			new Rational(2, 1),
			new Rational(3, 1),
			new Rational(3, 1),
			new Rational(2, 2),
			new Rational(2, 2),
			new Rational(5, 16),
			new Rational(2, 4)
		);
		
		List<Rational> actual = new ArrayList<Rational>();
		List<Integer[]> mi = tr.getMeterInfo();
		for (Rational r : all) {
			actual.add(Transcription.getMeter(r, mi));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetMeterBar() {
		Transcription tr = new Transcription(midiTestGetMeterKeyInfo);
		
		List<Rational> expected = Arrays.asList(
			new Rational(3, 4),
			new Rational(2, 1),
			new Rational(2, 1),
			new Rational(3, 1),
			new Rational(3, 1),
			new Rational(2, 2),
			new Rational(2, 2),
			new Rational(5, 16),
			new Rational(2, 4)
		);

		List<Integer[]> mi = tr.getMeterInfo();
		List<Rational> actual = new ArrayList<Rational>();
		for (int i = 1; i < 10; i++) {
			actual.add(Transcription.getMeter(i, mi));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetBasicNotePropertiesChord() {		
		Transcription transcription = new Transcription(midiTestpiece);

		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		// Chord 0
		Integer[][] expected0 = new Integer[4][8];
		expected0[0] = new Integer[]{50, 3, 4, 1, 4, 0, 4, 0}; 
		expected0[1] = new Integer[]{57, 3, 4, 1, 4, 0, 4, 1};
		expected0[2] = new Integer[]{65, 3, 4, 1, 4, 0, 4, 2};
		expected0[3] = new Integer[]{69, 3, 4, 1, 4, 0, 4, 3};
		// Chord 1
		Integer[][] expected1 = new Integer[4][8];
		expected1[0] = new Integer[]{45, 1, 1, 3, 16, 1, 4, 0};
		expected1[1] = new Integer[]{57, 1, 1, 1, 4, 1, 4, 1};
		expected1[2] = new Integer[]{69, 1, 1, 1, 8, 1, 4, 2};
		expected1[3] = new Integer[]{72, 1, 1, 1, 4, 1, 4, 3};
		// Chord 2
		Integer[][] expected2 = new Integer[1][8];
		expected2[0] = new Integer[]{48, 19, 16, 1, 16, 2, 1, 0};
		// Chord 3
		Integer[][] expected3 = new Integer[5][8];
		expected3[0] = new Integer[]{47, 5, 4, 1, 8, 3, 5, 0};
		expected3[1] = new Integer[]{50, 5, 4, 1, 4, 3, 5, 1};
		expected3[2] = new Integer[]{59, 5, 4, 1, 4, 3, 5, 2};
		expected3[3] = new Integer[]{65, 5, 4, 1, 4, 3, 5, 3};
		expected3[4] = new Integer[]{65, 5, 4, 1, 8, 3, 5, 4};
		// Chord 4
		Integer[][] expected4 = new Integer[1][8];
		expected4[0] = new Integer[]{45, 11, 8, 1, 8, 4, 1, 0};
		// Chord 5
		Integer[][] expected5 = new Integer[5][8];
		expected5[0] = new Integer[]{45, 3, 2, 1, 4, 5, 5, 0};
		expected5[1] = new Integer[]{57, 3, 2, 1, 2, 5, 5, 1};
		expected5[2] = new Integer[]{57, 3, 2, 1, 4, 5, 5, 2};
		expected5[3] = new Integer[]{60, 3, 2, 1, 4, 5, 5, 3};
		expected5[4] = new Integer[]{69, 3, 2, 1, 4, 5, 5, 4};
		// Chord 6
		Integer[][] expected6 = new Integer[4][8];
		expected6[0] = new Integer[]{45, 7, 4, 1, 4, 6, 4, 0};
		expected6[1] = new Integer[]{60, 7, 4, 1, 8, 6, 4, 1};
		expected6[2] = new Integer[]{64, 7, 4, 1, 8, 6, 4, 2};
		expected6[3] = new Integer[]{69, 7, 4, 1, 4, 6, 4, 3};
		// Chord 7
		Integer[][] expected7 = new Integer[2][8];
		expected7[0] = new Integer[]{59, 15, 8, 1, 8, 7, 2, 0};
		expected7[1] = new Integer[]{68, 15, 8, 1, 8, 7, 2, 1};
		// Chord 8
		Integer[][] expected8 = new Integer[4][8];
		expected8[0] = new Integer[]{45, 2, 1, 1, 2, 8, 4, 0};
		expected8[1] = new Integer[]{57, 2, 1, 1, 2, 8, 4, 1};
		expected8[2] = new Integer[]{64, 2, 1, 1, 2, 8, 4, 2};
		expected8[3] = new Integer[]{69, 2, 1, 1, 16, 8, 4, 3};
		// Chords 9-14
		Integer[][] expected9 = new Integer[1][8];
		expected9[0] = new Integer[]{68, 33, 16, 1, 16, 9, 1, 0};
		Integer[][] expected10 = new Integer[1][8];
		expected10[0] = new Integer[]{69, 17, 8, 1, 32, 10, 1, 0};
		Integer[][] expected11 = new Integer[1][8];
		expected11[0] = new Integer[]{68, 69, 32, 1, 32, 11, 1, 0};
		Integer[][] expected12 = new Integer[1][8];
		expected12[0] = new Integer[]{66, 35, 16, 1, 32, 12, 1, 0};
		Integer[][] expected13 = new Integer[1][8];
		expected13[0] = new Integer[]{68, 71, 32, 1, 32, 13, 1, 0};
		Integer[][] expected14 = new Integer[1][8];
		expected14[0] = new Integer[]{69, 9, 4, 1, 4, 14, 1, 0};
		// Chord 15
		Integer[][] expected15 = new Integer[4][8];
		expected15[0] = new Integer[]{45, 11, 4, 1, 4, 15, 4, 0};
		expected15[1] = new Integer[]{57, 11, 4, 1, 4, 15, 4, 1};
		expected15[2] = new Integer[]{64, 11, 4, 1, 4, 15, 4, 2};
		expected15[3] = new Integer[]{69, 11, 4, 1, 4, 15, 4, 3};

		expected.add(expected0); expected.add(expected1); expected.add(expected2); expected.add(expected3);
		expected.add(expected4); expected.add(expected5); expected.add(expected6); expected.add(expected7);
		expected.add(expected8); expected.add(expected9); expected.add(expected10); expected.add(expected11);
		expected.add(expected12); expected.add(expected13); expected.add(expected14); expected.add(expected15);

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		for (int i = 0; i < expected.size(); i++) {
			actual.add(transcription.getBasicNotePropertiesChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j].length, actual.get(i)[j].length);
				for (int k = 0; k < expected.get(i)[j].length; k++) {
					assertEquals(expected.get(i)[j][k], actual.get(i)[j][k]);
				}
			}
		}
	}


	@Test
	public void testGetPitchesInChord() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Chord 0
		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		// Chord 1
		expected.add(Arrays.asList(new Integer[]{45, 57, 69, 72})); 
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{48})); 
		// Chord 3
		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65, 65}));
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{45})); 
		// Chord 5
		expected.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69})); 
		// Chord 6
		expected.add(Arrays.asList(new Integer[]{45, 60, 64, 69})); 
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{59, 68})); 
		// Chord 8
		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{69}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{66}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{69}));
		// Chord 15
		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		List<List<Note>> chords = transcription.getChords();
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Transcription.getPitchesInChord(chords.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetPitchesInChordWithLowestNoteIndex() {	  	  
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 69, 72})); 
		expected.add(Arrays.asList(new Integer[]{48})); 
		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65, 65}));
		expected.add(Arrays.asList(new Integer[]{45}));
		expected.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 60, 64, 69})); 
		expected.add(Arrays.asList(new Integer[]{59, 68})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{69}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{66}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{69}));
		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69})); 

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		List<List<Note>> chords = transcription.getChords();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int lowestNoteIndex = 0;
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Transcription.getPitchesInChord(basicNoteProperties, lowestNoteIndex));
			lowestNoteIndex += chords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetIndicesPerChord() {
		Transcription transcription = new Transcription(midiTestpiece);
		
		// Determine expected
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// fwd
		List<List<Integer>> expectedFwd = new ArrayList<List<Integer>>();
		expectedFwd.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedFwd.add(Arrays.asList(new Integer[]{4, 5, 6, 7}));
		expectedFwd.add(Arrays.asList(new Integer[]{8}));
		expectedFwd.add(Arrays.asList(new Integer[]{9, 10, 11, 12, 13}));
		expectedFwd.add(Arrays.asList(new Integer[]{14}));
		expectedFwd.add(Arrays.asList(new Integer[]{15, 16, 17, 18, 19}));
		expectedFwd.add(Arrays.asList(new Integer[]{20, 21, 22, 23}));
		expectedFwd.add(Arrays.asList(new Integer[]{24, 25}));
		expectedFwd.add(Arrays.asList(new Integer[]{26, 27, 28, 29}));
		expectedFwd.add(Arrays.asList(new Integer[]{30}));
		expectedFwd.add(Arrays.asList(new Integer[]{31}));
		expectedFwd.add(Arrays.asList(new Integer[]{32}));
		expectedFwd.add(Arrays.asList(new Integer[]{33}));
		expectedFwd.add(Arrays.asList(new Integer[]{34}));
		expectedFwd.add(Arrays.asList(new Integer[]{35}));
		expectedFwd.add(Arrays.asList(new Integer[]{36, 37, 38, 39})); 
		expected.add(expectedFwd);
		
		// bwd
		List<List<Integer>> expectedBwd = new ArrayList<List<Integer>>();
		expectedBwd.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedBwd.add(Arrays.asList(new Integer[]{4}));
		expectedBwd.add(Arrays.asList(new Integer[]{5}));
		expectedBwd.add(Arrays.asList(new Integer[]{6}));
		expectedBwd.add(Arrays.asList(new Integer[]{7}));
		expectedBwd.add(Arrays.asList(new Integer[]{8}));
		expectedBwd.add(Arrays.asList(new Integer[]{9}));
		expectedBwd.add(Arrays.asList(new Integer[]{10, 11, 12, 13}));
		expectedBwd.add(Arrays.asList(new Integer[]{14, 15}));
		expectedBwd.add(Arrays.asList(new Integer[]{16, 17, 18, 19}));
		expectedBwd.add(Arrays.asList(new Integer[]{20, 21, 22, 23, 24}));
		expectedBwd.add(Arrays.asList(new Integer[]{25}));
		expectedBwd.add(Arrays.asList(new Integer[]{26, 27, 28, 29, 30}));
		expectedBwd.add(Arrays.asList(new Integer[]{31}));
		expectedBwd.add(Arrays.asList(new Integer[]{32, 33, 34, 35}));
		expectedBwd.add(Arrays.asList(new Integer[]{36, 37, 38, 39})); 
		expected.add(expectedBwd);
		
		// Calculate actual
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>(); 
		actual.add(transcription.getIndicesPerChord(false));
		actual.add(transcription.getIndicesPerChord(true));
		
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetLargestTranscriptionChord() {
		Transcription transcription = new Transcription(midiTestpiece);

		int expected = 5;
		int actual = transcription.getLargestTranscriptionChord();

		assertEquals(expected, actual);
	}

//	public void testCombineLabels() {
//		// Determine expected
//	  List<List<Double>> expected = new ArrayList<List<Double>>();
//	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 1.0}));
//	  expected.add(Arrays.asList(new Double[]{0.0, 1.0, 0.0, 1.0, 0.0}));
//	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 1.0, 0.0, 0.0}));
//	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 1.0, 0.0, 0.0}));
//	  expected.add(Arrays.asList(new Double[]{0.0, 1.0, 0.0, 1.0, 0.0}));
//	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 1.0}));
//	  
//	  // Calculate actual
//	  List<List<Double>> actual = new ArrayList<List<Double>>();
//	  actual.add(Transcription.combineLabels(V_0, V_4));
//	  actual.add(Transcription.combineLabels(V_1, V_3));
//	  actual.add(Transcription.combineLabels(V_0, V_2));
//	  actual.add(Transcription.combineLabels(V_2, V_0));
//	  actual.add(Transcription.combineLabels(V_3, V_1));
//	  actual.add(Transcription.combineLabels(V_4, V_0));
//	  
//	  // Assert equality
//	  assertEquals(expected.size(), actual.size());
//	  for (int i = 0; i < expected.size(); i++) {
//	  	assertEquals(expected.get(i).size(), actual.get(i).size());
//	  	for (int j = 0; j < expected.get(i).size(); j++) {
//	  		assertEquals(expected.get(j), actual.get(j));
//	  	}
//	  }  
//	}


	@Test
	public void testGetLowestAndHighestPitchPerVoice() {
//    Tablature tablature = new Tablature(encodingTestpiece1, true);
    Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
 
    // Determine expected 
    Integer[][]expected = new Integer[mnv][2];
    expected[0] = new Integer[]{64, 72};
    expected[1] = new Integer[]{60, 69};
    expected[2] = new Integer[]{57, 60};
    expected[3] = new Integer[]{45, 57};
    expected[4] = new Integer[]{45, 47};
    	
    // Calculate actual
    Integer[][] actual = transcription.getLowestAndHighestPitchPerVoice();
    	
    // Assert equality
    assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
    	assertEquals(expected[i].length, actual[i].length);
    	for (int j = 0; j < expected[i].length; j++) {
    		assertEquals(expected[i][j], actual[i][j]);
    	}
    }
	}


	@Test
	public void testGetLowestAndHighestPitchPerVoiceNonTab() {
    Transcription transcription = new Transcription(midiTestpiece);
   
    // Determine expected 
    Integer[][]expected = new Integer[mnv][2];
    expected[0] = new Integer[]{64, 72};
    expected[1] = new Integer[]{60, 69};
    expected[2] = new Integer[]{57, 60};
    expected[3] = new Integer[]{45, 57};
    expected[4] = new Integer[]{45, 47};
    	
    // Calculate actual
    Integer[][] actual = transcription.getLowestAndHighestPitchPerVoice();
    	
    // Assert equality
    assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
    	assertEquals(expected[i].length, actual[i].length);
    	for (int j = 0; j < expected[i].length; j++) {
    		assertEquals(expected[i][j], actual[i][j]);
    	}
    }
	}


	@Test
	public void testListNotesPerVoice() {
//    Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Voice 0
		expected.add(Arrays.asList(new Integer[]{3, 6, 12, 18, 21, 24, 28, 29, 30, 31, 32, 33, 34, 38}));
		// Voice 1
		expected.add(Arrays.asList(new Integer[]{2, 7, 12, 17, 22, 27, 37}));
		// Voice 2
		expected.add(Arrays.asList(new Integer[]{1, 5, 11, 16, 20, 23, 26, 36}));
		// Voice 3
		expected.add(Arrays.asList(new Integer[]{0, 4, 8, 10, 15, 25, 35}));
		// Voice 4
		expected.add(Arrays.asList(new Integer[]{9, 13, 14, 19}));

		List<List<Double>> groundTruthVoiceLabels = transcription.getVoiceLabels();
		List<List<Integer>> actual = Transcription.listNotesPerVoice(groundTruthVoiceLabels);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testListNotesPerVoiceNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Voice 0
		expected.add(Arrays.asList(new Integer[]{3, 7, 12, 19, 22, 25, 29, 30, 31, 32, 33, 34, 35, 39}));
		// Voice 1
		expected.add(Arrays.asList(new Integer[]{2, 6, 13, 18, 23, 28, 38}));
		// Voice 2
		expected.add(Arrays.asList(new Integer[]{1, 5, 11, 17, 21, 24, 27, 37}));
		// Voice 3
		expected.add(Arrays.asList(new Integer[]{0, 4, 8, 10, 16, 26, 36}));
		// Voice 4
		expected.add(Arrays.asList(new Integer[]{9, 14, 15, 20}));

		List<List<Double>> groundTruthVoiceLabels = transcription.getVoiceLabels();
		List<List<Integer>> actual = Transcription.listNotesPerVoice(groundTruthVoiceLabels);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testListNotesPerVoiceAltNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<Rational[]> expected = new ArrayList<>();
		expected.add(new Rational[]{
			new Rational(65, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)
		});
		//
		expected.add(new Rational[]{
			new Rational(69, 1), new Rational(4, 4), new Rational(1, 8), new Rational(0, 4)
		});
		expected.add(new Rational[]{
			new Rational(65, 1), new Rational(5, 4), new Rational(1, 8), new Rational(1, 4)
		});
		expected.add(new Rational[]{
			new Rational(60, 1), new Rational(6, 4), new Rational(1, 4), new Rational(2, 4)
		});
		expected.add(new Rational[]{
			new Rational(69, 1), new Rational(7, 4), new Rational(1, 4), new Rational(3, 4)
		});
		//
		expected.add(new Rational[]{
			new Rational(64, 1), new Rational(8, 4), new Rational(1, 2), new Rational(0, 4)
		});
		expected.add(new Rational[]{
			new Rational(64, 1), new Rational(11, 4), new Rational(1, 4), new Rational(3, 4)
		});
		
		List<Rational[]> actual = transcription.listNotesPerVoice().get(1);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetLastNotesInVoices() {
		Transcription transcription = new Transcription(midiTestpiece);
		
		List<List<List<Rational[]>>> expected = new ArrayList<>();
		List<Rational[]> empty = new ArrayList<>();
		// Onset 0 (3/4)
		List<List<Rational[]>> onset0 = new ArrayList<>();
		onset0.add(empty); onset0.add(empty); onset0.add(empty); onset0.add(empty); onset0.add(empty);
		// Add for each note at this onset
		expected.add(onset0);
		expected.add(onset0);
		expected.add(onset0);
		expected.add(onset0);
		// Onset 3 (5/4)
		List<List<Rational[]>> onset3 = new ArrayList<>();
		// Voice 0
		Rational[] onset3v0note0 = new Rational[]{new Rational(69, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)};
		Rational[] onset3v0note1 = new Rational[]{new Rational(72, 1), new Rational(4, 4), new Rational(1, 4), new Rational(0, 4)};
		List<Rational[]> onset3v0 = new ArrayList<>();
		onset3v0.add(onset3v0note0); onset3v0.add(onset3v0note1);
		// Voice 1
		Rational[] onset3v1note0 = new Rational[]{new Rational(65, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)};
		Rational[] onset3v1note1 = new Rational[]{new Rational(69, 1), new Rational(4, 4), new Rational(1, 8), new Rational(0, 4)};
		List<Rational[]> onset3v1 = new ArrayList<>();
		onset3v1.add(onset3v1note0); onset3v1.add(onset3v1note1);
		// Voice 2
		Rational[] onset3v2note0 = new Rational[]{new Rational(57, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)};
		Rational[] onset3v2note1 = new Rational[]{new Rational(57, 1), new Rational(4, 4), new Rational(1, 4), new Rational(0, 4)};
		List<Rational[]> onset3v2 = new ArrayList<>();
		onset3v2.add(onset3v2note0); onset3v2.add(onset3v2note1);
		// Voice 3
		Rational[] onset3v3note0 = new Rational[]{new Rational(50, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)};
		Rational[] onset3v3note1 = new Rational[]{new Rational(45, 1), new Rational(4, 4), new Rational(3, 16), new Rational(0, 4)};
		Rational[] onset3v3note2 = new Rational[]{new Rational(48, 1), new Rational(19, 16), new Rational(1, 16), new Rational(3, 16)};
		List<Rational[]> onset3v3 = new ArrayList<>();
		onset3v3.add(onset3v3note0); onset3v3.add(onset3v3note1); onset3v3.add(onset3v3note2);
		// Voice 4
		List<Rational[]> onset3v4 = empty;
		onset3.add(onset3v0); onset3.add(onset3v1); onset3.add(onset3v2); onset3.add(onset3v3); onset3.add(onset3v4);
		// Add for each note at this onset
		expected.add(onset3);
		expected.add(onset3);
		expected.add(onset3);
		expected.add(onset3);
		expected.add(onset3);
		// Onset 4 (11/8)
		List<List<Rational[]>> onset4 = new ArrayList<>();
		// Voice 0
		List<Rational[]> onset4v0 = null; //new ArrayList<>();
		// Voice 1
		Rational[] onset4v1note0 = new Rational[]{new Rational(65, 1), new Rational(3, 4), new Rational(1, 4), new Rational(3, 4)};
		Rational[] onset4v1note1 = new Rational[]{new Rational(69, 1), new Rational(4, 4), new Rational(1, 8), new Rational(0, 4)};
		Rational[] onset4v1note2 = new Rational[]{new Rational(65, 1), new Rational(5, 4), new Rational(1, 8), new Rational(1, 4)};
		List<Rational[]> onset4v1 = new ArrayList<>();
		onset4v1.add(onset4v1note0); onset4v1.add(onset4v1note1); onset4v1.add(onset4v1note2);
		// Voice 2
		List<Rational[]> onset4v2 = null; // new ArrayList<>();
		// Voice 3
		List<Rational[]> onset4v3 = null; // new ArrayList<>();
		// Voice 4
		Rational[] onset4v4note0 = new Rational[]{new Rational(47, 1), new Rational(5, 4), new Rational(1, 8), new Rational(1, 4)};
		List<Rational[]> onset4v4 = new ArrayList<>();
		onset4v4.add(onset4v4note0);
		onset4.add(onset4v0); onset4.add(onset4v1); onset4.add(onset4v2); onset4.add(onset4v3); onset4.add(onset4v4);
		// Add for each note at this onset
		expected.add(onset4);
		// Onset 7
		List<List<Rational[]>> onset7 = new ArrayList<>();
		// Voice 0
		Rational[] onset7v0note0 = new Rational[]{new Rational(65, 1), new Rational(5, 4), new Rational(1, 4), new Rational(1, 4)};
		Rational[] onset7v0note1 = new Rational[]{new Rational(69, 1), new Rational(6, 4), new Rational(1, 4), new Rational(2, 4)};
		Rational[] onset7v0note2 = new Rational[]{new Rational(64, 1), new Rational(7, 4), new Rational(1, 8), new Rational(3, 4)};
		List<Rational[]> onset7v0 = new ArrayList<>();
		onset7v0.add(onset7v0note0); onset7v0.add(onset7v0note1); onset7v0.add(onset7v0note2);
		// Voice 1
		List<Rational[]> onset7v1 = null;
		// Voice 2
		Rational[] onset7v2note0 = new Rational[]{new Rational(59, 1), new Rational(5, 4), new Rational(1, 4), new Rational(1, 4)};
		Rational[] onset7v2note1 = new Rational[]{new Rational(57, 1), new Rational(6, 4), new Rational(1, 4), new Rational(2, 4)};
		Rational[] onset7v2note2 = new Rational[]{new Rational(60, 1), new Rational(7, 4), new Rational(1, 8), new Rational(3, 4)};
		List<Rational[]> onset7v2 = new ArrayList<>();
		onset7v2.add(onset7v2note0); onset7v2.add(onset7v2note1); onset7v2.add(onset7v2note2);
		// Voice 3
		List<Rational[]> onset7v3 = null;
		// Voice 4
		List<Rational[]> onset7v4 = null;
		onset7.add(onset7v0); onset7.add(onset7v1); onset7.add(onset7v2); onset7.add(onset7v3); onset7.add(onset7v4);
		// Add for each note at this onset
		expected.add(onset7); 
		expected.add(onset7);

		List<List<List<Rational[]>>> actualFull = transcription.getLastNotesInVoices(3);
		List<List<List<Rational[]>>> actual = new ArrayList<>();
		// Onset 0
		actual.add(actualFull.get(0));
		actual.add(actualFull.get(1));
		actual.add(actualFull.get(2));
		actual.add(actualFull.get(3));
		// Onset 3
		actual.add(actualFull.get(9));
		actual.add(actualFull.get(10));
		actual.add(actualFull.get(11));
		actual.add(actualFull.get(12));
		actual.add(actualFull.get(13));
		// Onset 4
		actual.add(actualFull.get(14));
		// Onset 7
		actual.add(actualFull.get(24));
		actual.add(actualFull.get(25));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				if (expected.get(i).get(j) == null) {
					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
				}
				else {
					assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
					for (int k = 0; k < expected.get(i).get(j).size(); k++) {
						assertEquals(expected.get(i).get(j).get(k).length, actual.get(i).get(j).get(k).length);
						for (int l = 0; l < expected.get(i).get(j).get(k).length; l++) {
							assertEquals(expected.get(i).get(j).get(k)[l], actual.get(i).get(j).get(k)[l]);
						}
					}
				}
			}
		}
	}


	@Test
	public void testGetAdjacentNoteInVoice() {
		Transcription transcription = new Transcription(midiTestpiece);

		NotationSystem ns = transcription.getScorePiece().getScore();
		//
		NotationVoice nv0 = ns.get(0).get(0);
		Note nv0n0 = nv0.get(0).get(0); Note nv0n1 = nv0.get(1).get(0); Note nv0n2 = nv0.get(2).get(0);
		Note nv0n3 = nv0.get(3).get(0); Note nv0n4 = nv0.get(4).get(0); Note nv0n5 = nv0.get(5).get(0);
		Note nv0n6 = nv0.get(6).get(0); Note nv0n7 = nv0.get(7).get(0); Note nv0n8 = nv0.get(8).get(0);
		Note nv0n9 = nv0.get(9).get(0); Note nv0n10 = nv0.get(10).get(0); Note nv0n11 = nv0.get(11).get(0);
		Note nv0n12 = nv0.get(12).get(0); Note nv0n13 = nv0.get(13).get(0);   		
		//
		NotationVoice nv1 = ns.get(1).get(0);
		Note nv1n0 = nv1.get(0).get(0); Note nv1n1 = nv1.get(1).get(0); Note nv1n2 = nv1.get(2).get(0);
		Note nv1n3 = nv1.get(3).get(0); Note nv1n4 = nv1.get(4).get(0); Note nv1n5 = nv1.get(5).get(0);
		Note nv1n6 = nv1.get(6).get(0);
		//
		NotationVoice nv2 = ns.get(2).get(0);
		Note nv2n0 = nv2.get(0).get(0); Note nv2n1 = nv2.get(1).get(0); Note nv2n2 = nv2.get(2).get(0);
		Note nv2n3 = nv2.get(3).get(0); Note nv2n4 = nv2.get(4).get(0); Note nv2n5 = nv2.get(5).get(0);
		Note nv2n6 = nv2.get(6).get(0); Note nv2n7 = nv2.get(7).get(0);
		//
		NotationVoice nv3 = ns.get(3).get(0);
		Note nv3n0 = nv3.get(0).get(0); Note nv3n1 = nv3.get(1).get(0); Note nv3n2 = nv3.get(2).get(0);
		Note nv3n3 = nv3.get(3).get(0); Note nv3n4 = nv3.get(4).get(0); Note nv3n5 = nv3.get(5).get(0);
		Note nv3n6 = nv3.get(6).get(0);
		//
		NotationVoice nv4 = ns.get(4).get(0);
		Note nv4n0 = nv4.get(0).get(0); Note nv4n1 = nv4.get(1).get(0); Note nv4n2 = nv4.get(2).get(0);
		Note nv4n3 = nv4.get(3).get(0);

		List<Note> expected = new ArrayList<Note>();
		// a. Direction.LEFT
		List<Note> expectedLeft = new ArrayList<Note>();
		// Chord 0
		expectedLeft.addAll(Arrays.asList(new Note[]{null, null, null, null}));
		// Chord 1
		expectedLeft.addAll(Arrays.asList(new Note[]{nv3n0, nv2n0, nv1n0, nv0n0}));
		// Chord 2
		expectedLeft.addAll(Arrays.asList(new Note[]{nv3n1}));
		// Chord 3
		expectedLeft.addAll(Arrays.asList(new Note[]{null, nv3n2, nv2n1, nv0n1, nv1n1}));
		// Chord 4
		expectedLeft.addAll(Arrays.asList(new Note[]{nv4n0}));
		// Chord 5
		expectedLeft.addAll(Arrays.asList(new Note[]{nv4n1, nv3n3, nv2n2, nv1n2, nv0n2}));
		// Chord 6
		expectedLeft.addAll(Arrays.asList(new Note[]{nv4n2, nv2n3, nv0n3, nv1n3}));
		// Chord 7
		expectedLeft.addAll(Arrays.asList(new Note[]{nv2n4, nv0n4}));
		// Chord 8
		expectedLeft.addAll(Arrays.asList(new Note[]{nv3n4, nv2n5, nv1n4, nv0n5}));
		// Chord 9-14
		expectedLeft.addAll(Arrays.asList(new Note[]{nv0n6, nv0n7, nv0n8, nv0n9, nv0n10, nv0n11}));
		// Chord 15
		expectedLeft.addAll(Arrays.asList(new Note[]{nv3n5, nv2n6, nv1n5, nv0n12}));
		expected.addAll(expectedLeft);

		// b. Direction.RIGHT
		List<Note> expectedRight = new ArrayList<Note>();
		// Chord 15
		expectedRight.addAll(Arrays.asList(new Note[]{null, null, null, null}));
		 // Chord 14-9
		expectedRight.addAll(Arrays.asList(new Note[]{nv0n13, nv0n12, nv0n11, nv0n10, nv0n9, nv0n8}));
		 // Chord 8
		expectedRight.addAll(Arrays.asList(new Note[]{nv3n6, nv2n7, nv1n6, nv0n7}));
		 // Chord 7
		expectedRight.addAll(Arrays.asList(new Note[]{nv2n6, nv0n6}));
		 // Chord 6
		expectedRight.addAll(Arrays.asList(new Note[]{null, nv2n5, nv0n5, nv1n5}));
		 // Chord 5
		expectedRight.addAll(Arrays.asList(new Note[]{nv4n3, nv3n5, nv2n4, nv1n4, nv0n4}));
		 // Chord 4
		expectedRight.addAll(Arrays.asList(new Note[]{nv4n2}));
		 // Chord 3
		expectedRight.addAll(Arrays.asList(new Note[]{nv4n1, nv3n4, nv2n3, nv0n3, nv1n3}));
		 // Chord 2
		expectedRight.addAll(Arrays.asList(new Note[]{nv3n3}));
		 // Chord 1
		expectedRight.addAll(Arrays.asList(new Note[]{nv3n2, nv2n2, nv1n2, nv0n2}));
		 // Chord 0
		expectedRight.addAll(Arrays.asList(new Note[]{nv3n1, nv2n1, nv1n1, nv0n1}));
		expected.addAll(expectedRight);

		List<Note> actual = new ArrayList<Note>();
//		NoteSequence noteSeq = transcription.getNoteSequence();
		List<Note> notes = transcription.getNotes();
		// a. Direction.LEFT
		for (int i = 0; i < notes.size(); i++) {
//		for (int i = 0; i < noteSeq.size(); i++) {
			Note n = notes.get(i);
//			Note n = noteSeq.getNoteAt(i);
			int voice = transcription.findVoice(n);
			NotationVoice nv = ns.get(voice).get(0);
			actual.add(Transcription.getAdjacentNoteInVoice(nv, n, true));
		}
		// b. Direction.RIGHT
		List<Integer> backwardsMapping = // FeatureGenerator.getBackwardsMapping(transcription.getNumberOfNewNotesPerChord());		
			Arrays.asList(new Integer[]{36, 37, 38, 39, 35, 34, 33, 32, 31, 30, 26, 27, 28, 29, 
			24, 25, 20, 21, 22, 23, 15, 16, 17, 18, 19, 14, 9, 10, 11, 12, 13, 8, 4, 5, 6, 7, 0, 1, 2, 3});
		
		for (int i : backwardsMapping) {
			Note n = notes.get(i);
//			Note n = noteSeq.getNoteAt(i);
			int voice = transcription.findVoice(n);
			NotationVoice nv = ns.get(voice).get(0);
			actual.add(Transcription.getAdjacentNoteInVoice(nv, n, false));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetIndicesOfSustainedPreviousNotes() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		List<Integer> emptyList = Arrays.asList(new Integer[]{});
		// Chord 0
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chord 1
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{5, 6}));
		// Chord 3
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{10, 11, 12}));
		// Chord 5
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		expected.add(emptyList);
		// Chord 6
		expected.add(Arrays.asList(new Integer[]{15}));
		expected.add(Arrays.asList(new Integer[]{15}));
		expected.add(Arrays.asList(new Integer[]{15}));
		expected.add(Arrays.asList(new Integer[]{15}));
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{15, 19, 22}));
		expected.add(Arrays.asList(new Integer[]{15, 19, 22}));
		// Chord 8
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		expected.add(Arrays.asList(new Integer[]{25, 26, 27}));
		// Chord 15
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		List<List<Double>> durationLabels = transcription.getDurationLabels();
		System.out.println(durationLabels.get(0).size());
		for (List<Double> l : durationLabels) {
			System.out.println(l);
		}
		for (int i = 0; i < btp.length; i++) {
			actual.add(Transcription.getIndicesOfSustainedPreviousNotes(
				btp, durationLabels, null, i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetIndicesOfSustainedPreviousNotesNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		List<Integer> emptyList = Arrays.asList(new Integer[]{});
		// Chord 0
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chord 1
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{5, 7}));
		// Chord 3
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		expected.add(emptyList);
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{10, 11, 12}));
		// Chord 5
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		expected.add(emptyList); 		
		// Chord 6
		expected.add(Arrays.asList(new Integer[]{16}));
		expected.add(Arrays.asList(new Integer[]{16}));
		expected.add(Arrays.asList(new Integer[]{16}));
		expected.add(Arrays.asList(new Integer[]{16}));
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{16, 20, 23}));
		expected.add(Arrays.asList(new Integer[]{16, 20, 23}));
		// Chord 8
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{26, 27, 28}));
		// Chord 15
		expected.add(emptyList); expected.add(emptyList); expected.add(emptyList); expected.add(emptyList);

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] bnp = transcription.getBasicNoteProperties();
		for (int i = 0; i < bnp.length; i++) {
			actual.add(Transcription.getIndicesOfSustainedPreviousNotes(null, null, bnp, i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetPitchesOfSustainedPreviousNotesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{57, 72})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{50, 59, 65})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{57})); 
		expected.add(Arrays.asList(new Integer[]{57, 45, 69})); 
		expected.add(Arrays.asList(new Integer[]{}));
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64}));
		expected.add(Arrays.asList(new Integer[]{}));

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
		List<List<TabSymbol>> tablatureChords = tablature.getChords();
		List<List<Double>> durationLabels = transcription.getDurationLabels();
		int lowestNoteIndex = 0;
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(Transcription.getPitchesOfSustainedPreviousNotesInChord(basicTabSymbolProperties,
				durationLabels,	null, lowestNoteIndex));
			lowestNoteIndex += tablatureChords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetPitchesOfSustainedPreviousNotesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{57, 72})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{50, 59, 65})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{57})); 
		expected.add(Arrays.asList(new Integer[]{57, 45, 69})); 
		expected.add(Arrays.asList(new Integer[]{}));
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64}));
		expected.add(Arrays.asList(new Integer[]{}));

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		List<List<Note>> chords = transcription.getChords();
		int lowestNoteIndex = 0;
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Transcription.getPitchesOfSustainedPreviousNotesInChord(null, null, basicNoteProperties,
				lowestNoteIndex));
			lowestNoteIndex += chords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetVoicesOfSustainedPreviousNotesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Chord 0
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 1
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{2, 0})); 
		// Chord 3
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{3, 2, 0}));
		// Chord 5
		expected.add(Arrays.asList(new Integer[]{})); 
		// chord 6
		expected.add(Arrays.asList(new Integer[]{3})); 
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{3, 4, 1})); 
		// Chord 8
		expected.add(Arrays.asList(new Integer[]{}));
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		// Chord 15
		expected.add(Arrays.asList(new Integer[]{}));

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
		List<List<Double>> durationLabels = transcription.getDurationLabels();
		List<Integer[]> voicesCoDNotes=  transcription.getVoicesSNU();
		int lowestNoteIndex = 0;
		for (int i = 0; i < tablature.getChords().size(); i++) {
			actual.add(Transcription.getVoicesOfSustainedPreviousNotesInChord(btp, durationLabels, 
				voicesCoDNotes, null, voiceLabels, lowestNoteIndex));
			lowestNoteIndex += tablature.getChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetVoicesOfSustainedPreviousNotesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Chord 0
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 1
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{2, 0})); 
		// Chord 3
		expected.add(Arrays.asList(new Integer[]{})); 
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{3, 2, 0}));
		// Chord 5
		expected.add(Arrays.asList(new Integer[]{})); 
		// chord 6
		expected.add(Arrays.asList(new Integer[]{3})); 
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{3, 4, 1})); 
		// Chord 8
		expected.add(Arrays.asList(new Integer[]{}));
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		expected.add(Arrays.asList(new Integer[]{3, 2, 1}));
		// Chord 15
		expected.add(Arrays.asList(new Integer[]{}));

		// For each chord: calculate the actual sustained pitches and add them to actual
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] bnp = transcription.getBasicNoteProperties();
		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
		int lowestNoteIndex = 0;
		for (int i = 0; i < transcription.getChords().size(); i++) {
			actual.add(Transcription.getVoicesOfSustainedPreviousNotesInChord(null, null, null, bnp,
				voiceLabels, lowestNoteIndex));
			lowestNoteIndex += transcription.getChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	@Test
	public void testGetAllPitchesAndVoicesInChord() {
		// TODO?
	}


	@Test
	public void testGetAllPitchesAndVoicesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);

		List<List<Integer>> voiceAssignments = getVoiceAssignments(false);

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Chord 0
		List<List<Integer>> expected0 = new ArrayList<List<Integer>>();
		expected0.add(Arrays.asList(new Integer[]{50, 57, 65, 69}));
		expected0.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		// Chord 1
		List<List<Integer>> expected1 = new ArrayList<List<Integer>>();
		expected1.add(Arrays.asList(new Integer[]{45, 57, 69, 72}));
		expected1.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		// Chord 2
		List<List<Integer>> expected2 = new ArrayList<List<Integer>>();
		expected2.add(Arrays.asList(new Integer[]{48, 57, 72}));
		expected2.add(Arrays.asList(new Integer[]{3, 2, 0}));
		// Chord 3
		List<List<Integer>> expected3 = new ArrayList<List<Integer>>();		  
		expected3.add(Arrays.asList(new Integer[]{47, 50, 59, 65, 65}));
		expected3.add(Arrays.asList(new Integer[]{4, 3, 2, 0, 1}));
		// Chord 4
		List<List<Integer>> expected4 = new ArrayList<List<Integer>>();		  
		expected4.add(Arrays.asList(new Integer[]{45, 50, 59, 65}));
		expected4.add(Arrays.asList(new Integer[]{4, 3, 2, 0}));
		// Chord 5
		List<List<Integer>> expected5 = new ArrayList<List<Integer>>();		  
		expected5.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69}));
		expected5.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
		// Chord 6
		List<List<Integer>> expected6 = new ArrayList<List<Integer>>();
		expected6.add(Arrays.asList(new Integer[]{45, 57, 60, 64, 69}));
		expected6.add(Arrays.asList(new Integer[]{4, 3, 2, 0, 1}));
		// Chord 7
		List<List<Integer>> expected7 = new ArrayList<List<Integer>>();
		expected7.add(Arrays.asList(new Integer[]{45, 57, 59, 68, 69}));
		expected7.add(Arrays.asList(new Integer[]{4, 3, 2, 0, 1}));
		// Chord 8
		List<List<Integer>> expected8 = new ArrayList<List<Integer>>();
		expected8.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected8.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		// Chord 9-14
		List<List<Integer>> expected9 = new ArrayList<List<Integer>>();		  
		expected9.add(Arrays.asList(new Integer[]{45, 57, 64, 68}));
		expected9.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		List<List<Integer>> expected10 = new ArrayList<List<Integer>>();		  
		expected10.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected10.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		List<List<Integer>> expected11 = new ArrayList<List<Integer>>();		  
		expected11.add(Arrays.asList(new Integer[]{45, 57, 64, 68}));
		expected11.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		List<List<Integer>> expected12 = new ArrayList<List<Integer>>();		  
		expected12.add(Arrays.asList(new Integer[]{45, 57, 64, 66}));
		expected12.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		List<List<Integer>> expected13 = new ArrayList<List<Integer>>();		  
		expected13.add(Arrays.asList(new Integer[]{45, 57, 64, 68}));
		expected13.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		List<List<Integer>> expected14 = new ArrayList<List<Integer>>();		  
		expected14.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected14.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));
		// Chord 15
		List<List<Integer>> expected15 = new ArrayList<List<Integer>>();
		expected15.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected15.add(Arrays.asList(new Integer[]{3, 2, 1, 0}));

		expected.add(expected0); expected.add(expected1); expected.add(expected2); expected.add(expected3);
		expected.add(expected4); expected.add(expected5); expected.add(expected6); expected.add(expected7); 
		expected.add(expected8); expected.add(expected9); expected.add(expected10); expected.add(expected11);
		expected.add(expected12); expected.add(expected13); expected.add(expected14); expected.add(expected15);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<List<Double>> allVoiceLabels = transcription.getVoiceLabels();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int lowestNoteIndex = 0;
		List<List<Note>> chords = transcription.getChords();
		for (int i = 0; i < chords.size(); i++) {
			List<Integer> currentPitchesInChord = Transcription.getPitchesInChord(chords.get(i));
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
			List<List<Double>> currentVoiceLabels = 
				LabelTools.getChordVoiceLabels(currentVoiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentVoiceLabels);
			actual.add(Transcription.getAllPitchesAndVoicesInChord(basicNoteProperties, currentPitchesInChord,
				currentVoicesInChord, allVoiceLabels, lowestNoteIndex));
			lowestNoteIndex += transcription.getChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetVoiceCrossingInformationInChordExtended() {    
		// Make the basicTabSymbolProperties for a fictional piece consisting of three chords  
		// a. A chord without CoD: sb.a6.a4.b2.g1
		List<Integer> pitchesInChordNoCoD = Arrays.asList(new Integer[]{43, 53, 63, 73});
		// b. A chord with one CoD: sb.a6.a4.b2
		List<Integer> pitchesInChordOneCoD = Arrays.asList(new Integer[]{43, 53, 63});
		// c. A chord with two CoDs: sb.a6.a4
		List<Integer> pitchesInChordTwoCoDs = Arrays.asList(new Integer[]{43, 53});

		// For each chord: make all possible voice assignments and voices
		// a. Without CoD
		List<List<Integer>> allPossibleVoiceAssignmentsNoCoD = new ArrayList<List<Integer>>();
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 1, 2, 3, -1})); // 0, 1, 2, 3 (onset 0 with pitch 43 has voice 0, onset 1 with pitch 53 has voice 1, ... onset 3 with pitch 73 has voice 3)
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 1, 3, 2, -1})); // 0, 1, 3, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 2, 1, 3, -1})); // 0, 2, 1, 3
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 3, 1, 2, -1})); // 0, 2, 3, 1
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 2, 3, 1, -1})); // 0, 3, 1, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{0, 3, 2, 1, -1})); // 0, 3, 2, 1

		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 0, 2, 3, -1})); // 1, 0, 2, 3
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 0, 3, 2, -1})); // 1, 0, 3, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 0, 1, 3, -1})); // 1, 2, 0, 3
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 0, 1, 2, -1})); // 1, 2, 3, 0
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 0, 3, 1, -1})); // 1, 3, 0, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 0, 2, 1, -1})); // 1, 3, 2, 0

		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 2, 0, 3, -1})); // 2, 0, 1, 3
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 3, 0, 2, -1})); // 2, 0, 3, 1
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 1, 0, 3, -1})); // 2, 1, 0, 3
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 1, 0, 2, -1})); // 2, 1, 3, 0
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 3, 0, 1, -1})); // 2, 3, 0, 1
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 2, 0, 1, -1})); // 2, 3, 1, 0

		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 2, 3, 0, -1})); // 3, 0, 1, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{1, 3, 2, 0, -1})); // 3, 0, 2, 1
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 1, 3, 0, -1})); // 3, 1, 0, 2
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 1, 2, 0, -1})); // 3, 1, 2, 0
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{2, 3, 1, 0, -1})); // 3, 2, 0, 1
		allPossibleVoiceAssignmentsNoCoD.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1})); // 3, 2, 1, 0

		List<List<List<Integer>>> allPossibleVoicesNoCoD = new ArrayList<List<List<Integer>>>();
		for (List<Integer> voiceAssignment : allPossibleVoiceAssignmentsNoCoD) {
			List<List<Double>> currentVoiceLabels = 
				LabelTools.getChordVoiceLabels(voiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentVoiceLabels);
			allPossibleVoicesNoCoD.add(currentVoicesInChord);
		}

		// b. With one CoD
		List<List<Integer>> allPossibleVoiceAssignmentsOneCoD = new ArrayList<List<Integer>>();
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 0, 1, 2, -1})); // 0/1, 2, 3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 0, 2, 1, -1})); // 0/1, 3, 2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 1, 0, 2, -1})); // 0/2, 1, 3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 2, 0, 1, -1})); // 0/2, 3, 1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 1, 2, 0, -1})); // 0/3, 1, 2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 2, 1, 0, -1})); // 0/3, 2, 1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 0, 0, 2, -1})); // 1/2, 0, 3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 0, 0, 1, -1})); // 1/2, 3, 0
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 0, 2, 0, -1})); // 1/3, 0, 2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 0, 1, 0, -1})); // 1/3, 2, 0
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 2, 0, 0, -1})); // 2/3, 0, 1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 1, 0, 0, -1})); // 2/3, 1, 0

		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 1, 0, 2, -1})); // 2, 0/1, 3 
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 1, 2, 0, -1})); // 3, 0/1, 2 
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 0, 1, 2, -1})); // 1, 0/2, 3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 2, 1, 0, -1})); // 3, 0/2, 1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 0, 2, 1, -1})); // 1, 0/3, 2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 2, 0, 1, -1})); // 2, 0/3, 1   
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 1, 1, 2, -1})); // 0, 1/2, 3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 1, 1, 0, -1})); // 3, 1/2, 0
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 1, 2, 1, -1})); // 0, 1/3, 2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 1, 0, 1, -1})); // 2, 1/3, 0
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 2, 1, 1, -1})); // 0, 2/3, 1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 0, 1, 1, -1})); // 1, 2/3, 0

		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 2, 0, 1, -1})); // 2, 3, 0/1  
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 2, 1, 0, -1})); // 3, 2, 0/1
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 0, 2, 1, -1})); // 1, 3, 0/2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 1, 2, 0, -1})); // 3, 1, 0/2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 0, 1, 2, -1})); // 1, 2, 0/3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{2, 1, 0, 2, -1})); // 2, 1, 0/3 
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 2, 2, 1, -1})); // 0, 3, 1/2 
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 2, 2, 0, -1})); // 3, 0, 1/2
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 2, 1, 2, -1})); // 0, 2, 1/3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 2, 0, 2, -1})); // 2, 0, 1/3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{0, 1, 2, 2, -1})); // 0, 1, 2/3
		allPossibleVoiceAssignmentsOneCoD.add(Arrays.asList(new Integer[]{1, 0, 2, 2, -1})); // 1, 0, 2/3

		List<List<List<Integer>>> allPossibleVoicesOneCoD = new ArrayList<List<List<Integer>>>();
		for (List<Integer> voiceAssignment : allPossibleVoiceAssignmentsOneCoD) {
			List<List<Double>> currentVoiceLabels = 
				LabelTools.getChordVoiceLabels(voiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentVoiceLabels);
			allPossibleVoicesOneCoD.add(currentVoicesInChord);
		}

		// c. With two CoDs
		List<List<Integer>> allPossibleVoiceAssignmentsTwoCoDs = new ArrayList<List<Integer>>();
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{0, 0, 1, 1, -1})); // 0/1, 2/3
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{0, 1, 0, 1, -1})); // 0/2, 1/3
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{0, 1, 1, 0, -1})); // 0/3, 1/2
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{1, 0, 0, 1, -1})); // 1/2, 0/3
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{1, 0, 1, 0, -1})); // 1/3, 0/2
		allPossibleVoiceAssignmentsTwoCoDs.add(Arrays.asList(new Integer[]{1, 1, 0, 0, -1})); // 2/3, 0/1

		List<List<List<Integer>>> allPossibleVoicesTwoCoDs = new ArrayList<List<List<Integer>>>();
		for (List<Integer> voiceAssignment : allPossibleVoiceAssignmentsTwoCoDs) {
			List<List<Double>> currentVoiceLabels = 
				LabelTools.getChordVoiceLabels(voiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentVoiceLabels);
			allPossibleVoicesTwoCoDs.add(currentVoicesInChord);
		}

		// Expected
		// a. Without CoD
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		List<List<List<Integer>>> expNoCoD = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> expNoCoD0 = new ArrayList<List<Integer>>();
		expNoCoD0.add(Arrays.asList(new Integer[] {0, 1, 2, 3}));
		expNoCoD0.add(Arrays.asList(new Integer[] {0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3}));
		expNoCoD0.add(Arrays.asList(new Integer[] {10, 20, 30, 10, 20, 10}));
		List<List<Integer>> expNoCoD1 = new ArrayList<List<Integer>>();
		expNoCoD1.add(Arrays.asList(new Integer[] {0, 1, 3, 2}));
		expNoCoD1.add(Arrays.asList(new Integer[] {0, 1, 0, 3, 0, 2, 1, 3, 1, 2}));
		expNoCoD1.add(Arrays.asList(new Integer[] {10, 20, 30, 10, 20}));
		List<List<Integer>> expNoCoD2 = new ArrayList<List<Integer>>();
		expNoCoD2.add(Arrays.asList(new Integer[] {0, 2, 1, 3}));
		expNoCoD2.add(Arrays.asList(new Integer[] {0, 2, 0, 1, 0, 3, 2, 3, 1, 3}));
		expNoCoD2.add(Arrays.asList(new Integer[] {10, 20, 30, 20, 10}));
		List<List<Integer>> expNoCoD3 = new ArrayList<List<Integer>>();
		expNoCoD3.add(Arrays.asList(new Integer[] {0, 2, 3, 1}));
		expNoCoD3.add(Arrays.asList(new Integer[] {0, 2, 0, 3, 0, 1, 2, 3}));
		expNoCoD3.add(Arrays.asList(new Integer[] {10, 20, 30, 10}));
		List<List<Integer>> expNoCoD4 = new ArrayList<List<Integer>>();
		expNoCoD4.add(Arrays.asList(new Integer[] {0, 3, 1, 2}));
		expNoCoD4.add(Arrays.asList(new Integer[] {0, 3, 0, 1, 0, 2, 1, 2}));
		expNoCoD4.add(Arrays.asList(new Integer[] {10, 20, 30, 10}));
		List<List<Integer>> expNoCoD5 = new ArrayList<List<Integer>>();
		expNoCoD5.add(Arrays.asList(new Integer[] {0, 3, 2, 1}));
		expNoCoD5.add(Arrays.asList(new Integer[] {0, 3, 0, 2, 0, 1}));
		expNoCoD5.add(Arrays.asList(new Integer[] {10, 20, 30}));

		List<List<Integer>> expNoCoD6 = new ArrayList<List<Integer>>();
		expNoCoD6.add(Arrays.asList(new Integer[] {1, 2, 3, 0}));
		expNoCoD6.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 0, 2, 0, 3, 2, 3}));
		expNoCoD6.add(Arrays.asList(new Integer[] {20, 30, 10, 20, 10}));
		List<List<Integer>> expNoCoD7 = new ArrayList<List<Integer>>();
		expNoCoD7.add(Arrays.asList(new Integer[] {1, 3, 2, 0}));
		expNoCoD7.add(Arrays.asList(new Integer[] {1, 3, 1, 2, 0, 3, 0, 2}));
		expNoCoD7.add(Arrays.asList(new Integer[] {20, 30, 10, 20}));
		List<List<Integer>> expNoCoD8 = new ArrayList<List<Integer>>();
		expNoCoD8.add(Arrays.asList(new Integer[] {1, 2, 3, 0}));
		expNoCoD8.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 2, 3, 0, 3}));
		expNoCoD8.add(Arrays.asList(new Integer[] {10, 30, 20, 10}));
		List<List<Integer>> expNoCoD9 = new ArrayList<List<Integer>>();
		expNoCoD9.add(Arrays.asList(new Integer[] {1, 2, 3}));
		expNoCoD9.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 2, 3}));
		expNoCoD9.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expNoCoD10 = new ArrayList<List<Integer>>();
		expNoCoD10.add(Arrays.asList(new Integer[] {1, 3, 2, 0}));
		expNoCoD10.add(Arrays.asList(new Integer[] {1, 3, 1, 2, 0, 2}));
		expNoCoD10.add(Arrays.asList(new Integer[] {10, 30, 10}));
		List<List<Integer>> expNoCoD11 = new ArrayList<List<Integer>>();
		expNoCoD11.add(Arrays.asList(new Integer[] {1, 3, 2}));
		expNoCoD11.add(Arrays.asList(new Integer[] {1, 3, 1, 2}));
		expNoCoD11.add(Arrays.asList(new Integer[] {10, 20}));

		List<List<Integer>> expNoCoD12 = new ArrayList<List<Integer>>();
		expNoCoD12.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expNoCoD12.add(Arrays.asList(new Integer[] {2, 3, 0, 1, 0, 3, 1, 3}));
		expNoCoD12.add(Arrays.asList(new Integer[] {30, 10, 20, 10}));
		List<List<Integer>> expNoCoD13 = new ArrayList<List<Integer>>();
		expNoCoD13.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expNoCoD13.add(Arrays.asList(new Integer[] {2, 3, 0, 3, 0, 1}));
		expNoCoD13.add(Arrays.asList(new Integer[] {20, 10, 20}));
		List<List<Integer>> expNoCoD14 = new ArrayList<List<Integer>>();
		expNoCoD14.add(Arrays.asList(new Integer[] {2, 3, 1, 0}));
		expNoCoD14.add(Arrays.asList(new Integer[] {2, 3, 1, 3, 0, 3}));
		expNoCoD14.add(Arrays.asList(new Integer[] {30, 20, 10}));
		List<List<Integer>> expNoCoD15 = new ArrayList<List<Integer>>();
		expNoCoD15.add(Arrays.asList(new Integer[] {2, 3, 1}));
		expNoCoD15.add(Arrays.asList(new Integer[] {2, 3, 1, 3}));
		expNoCoD15.add(Arrays.asList(new Integer[] {20, 10}));
		List<List<Integer>> expNoCoD16 = new ArrayList<List<Integer>>();
		expNoCoD16.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expNoCoD16.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expNoCoD16.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expNoCoD17 = new ArrayList<List<Integer>>();
		expNoCoD17.add(Arrays.asList(new Integer[] {2, 3}));
		expNoCoD17.add(Arrays.asList(new Integer[] {2, 3}));
		expNoCoD17.add(Arrays.asList(new Integer[] {10}));

		List<List<Integer>> expNoCoD18 = new ArrayList<List<Integer>>();
		expNoCoD18.add(Arrays.asList(new Integer[] {0, 1, 2}));
		expNoCoD18.add(Arrays.asList(new Integer[] {0, 1, 0, 2, 1, 2}));
		expNoCoD18.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expNoCoD19 = new ArrayList<List<Integer>>();
		expNoCoD19.add(Arrays.asList(new Integer[] {0, 2, 1}));
		expNoCoD19.add(Arrays.asList(new Integer[] {0, 2, 0, 1}));
		expNoCoD19.add(Arrays.asList(new Integer[] {10, 20}));
		List<List<Integer>> expNoCoD20 = new ArrayList<List<Integer>>();
		expNoCoD20.add(Arrays.asList(new Integer[] {1, 2, 0}));
		expNoCoD20.add(Arrays.asList(new Integer[] {1, 2, 0, 2}));
		expNoCoD20.add(Arrays.asList(new Integer[] {20, 10}));
		List<List<Integer>> expNoCoD21 = new ArrayList<List<Integer>>();
		expNoCoD21.add(Arrays.asList(new Integer[] {1, 2}));
		expNoCoD21.add(Arrays.asList(new Integer[] {1, 2}));
		expNoCoD21.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expNoCoD22 = new ArrayList<List<Integer>>();
		expNoCoD22.add(Arrays.asList(new Integer[] {0, 1}));
		expNoCoD22.add(Arrays.asList(new Integer[] {0, 1}));
		expNoCoD22.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expNoCoD23 = new ArrayList<List<Integer>>();
		expNoCoD23.add(Arrays.asList(new Integer[] {}));
		expNoCoD23.add(Arrays.asList(new Integer[] {}));
		expNoCoD23.add(Arrays.asList(new Integer[] {}));

		expNoCoD.add(expNoCoD0); expNoCoD.add(expNoCoD1); expNoCoD.add(expNoCoD2); expNoCoD.add(expNoCoD3);
		expNoCoD.add(expNoCoD4); expNoCoD.add(expNoCoD5); expNoCoD.add(expNoCoD6); expNoCoD.add(expNoCoD7);
		expNoCoD.add(expNoCoD8); expNoCoD.add(expNoCoD9); expNoCoD.add(expNoCoD10); expNoCoD.add(expNoCoD11);
		expNoCoD.add(expNoCoD12); expNoCoD.add(expNoCoD13); expNoCoD.add(expNoCoD14); expNoCoD.add(expNoCoD15);
		expNoCoD.add(expNoCoD16); expNoCoD.add(expNoCoD17); expNoCoD.add(expNoCoD18); expNoCoD.add(expNoCoD19); 
		expNoCoD.add(expNoCoD20); expNoCoD.add(expNoCoD21); expNoCoD.add(expNoCoD22); expNoCoD.add(expNoCoD23);

		// b. With one CoD
		List<List<List<Integer>>> expOneCoD = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> expOneCoD0 = new ArrayList<List<Integer>>();
		expOneCoD0.add(Arrays.asList(new Integer[] {0, 2, 3, 1}));
		expOneCoD0.add(Arrays.asList(new Integer[] {0, 2, 0, 3, 1, 2, 1, 3, 2, 3}));
		expOneCoD0.add(Arrays.asList(new Integer[] {10, 20, 10, 20, 10}));
		List<List<Integer>> expOneCoD1 = new ArrayList<List<Integer>>();
		expOneCoD1.add(Arrays.asList(new Integer[] {0, 3, 2, 1}));
		expOneCoD1.add(Arrays.asList(new Integer[] {0, 3, 0, 2, 1, 3, 1, 2}));
		expOneCoD1.add(Arrays.asList(new Integer[] {10, 20, 10, 20}));
		List<List<Integer>> expOneCoD2 = new ArrayList<List<Integer>>();
		expOneCoD2.add(Arrays.asList(new Integer[] {0, 1, 3, 2}));
		expOneCoD2.add(Arrays.asList(new Integer[] {0, 1, 0, 3, 2, 3, 1, 3}));
		expOneCoD2.add(Arrays.asList(new Integer[] {10, 20, 20, 10}));
		List<List<Integer>> expOneCoD3 = new ArrayList<List<Integer>>();
		expOneCoD3.add(Arrays.asList(new Integer[] {0, 3, 1, 2}));
		expOneCoD3.add(Arrays.asList(new Integer[] {0, 3, 0, 1, 2, 3}));
		expOneCoD3.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expOneCoD4 = new ArrayList<List<Integer>>();
		expOneCoD4.add(Arrays.asList(new Integer[] {0, 1, 2}));
		expOneCoD4.add(Arrays.asList(new Integer[] {0, 1, 0, 2, 1, 2}));
		expOneCoD4.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expOneCoD5 = new ArrayList<List<Integer>>();
		expOneCoD5.add(Arrays.asList(new Integer[] {0, 2, 1}));
		expOneCoD5.add(Arrays.asList(new Integer[] {0, 2, 0, 1}));
		expOneCoD5.add(Arrays.asList(new Integer[] {10, 20}));
		List<List<Integer>> expOneCoD6 = new ArrayList<List<Integer>>();
		expOneCoD6.add(Arrays.asList(new Integer[] {1, 3, 2, 0}));
		expOneCoD6.add(Arrays.asList(new Integer[] {1, 3, 2, 3, 0, 3}));
		expOneCoD6.add(Arrays.asList(new Integer[] {20, 20, 10}));
		List<List<Integer>> expOneCoD7 = new ArrayList<List<Integer>>();
		expOneCoD7.add(Arrays.asList(new Integer[] {1, 3, 2}));
		expOneCoD7.add(Arrays.asList(new Integer[] {1, 3, 2, 3}));
		expOneCoD7.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expOneCoD8 = new ArrayList<List<Integer>>();
		expOneCoD8.add(Arrays.asList(new Integer[] {1, 2, 0}));
		expOneCoD8.add(Arrays.asList(new Integer[] {1, 2, 0, 2}));
		expOneCoD8.add(Arrays.asList(new Integer[] {20, 10}));
		List<List<Integer>> expOneCoD9 = new ArrayList<List<Integer>>();
		expOneCoD9.add(Arrays.asList(new Integer[] {1, 2}));
		expOneCoD9.add(Arrays.asList(new Integer[] {1, 2}));
		expOneCoD9.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expOneCoD10 = new ArrayList<List<Integer>>();
		expOneCoD10.add(Arrays.asList(new Integer[] {0, 1}));
		expOneCoD10.add(Arrays.asList(new Integer[] {0, 1}));
		expOneCoD10.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expOneCoD11 = new ArrayList<List<Integer>>();
		expOneCoD11.add(Arrays.asList(new Integer[] {}));
		expOneCoD11.add(Arrays.asList(new Integer[] {}));
		expOneCoD11.add(Arrays.asList(new Integer[] {}));

		List<List<Integer>> expOneCoD12 = new ArrayList<List<Integer>>();
		expOneCoD12.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expOneCoD12.add(Arrays.asList(new Integer[] {2, 3, 0, 3, 1, 3}));
		expOneCoD12.add(Arrays.asList(new Integer[] {20, 10, 10}));
		List<List<Integer>> expOneCoD13 = new ArrayList<List<Integer>>();
		expOneCoD13.add(Arrays.asList(new Integer[] {0, 2, 1}));
		expOneCoD13.add(Arrays.asList(new Integer[] {0, 2, 1, 2}));
		expOneCoD13.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expOneCoD14 = new ArrayList<List<Integer>>();
		expOneCoD14.add(Arrays.asList(new Integer[] {1, 2, 3, 0}));
		expOneCoD14.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 0, 3, 2, 3}));
		expOneCoD14.add(Arrays.asList(new Integer[] {10, 20, 10, 10}));
		List<List<Integer>> expOneCoD15 = new ArrayList<List<Integer>>();
		expOneCoD15.add(Arrays.asList(new Integer[] {0, 1}));
		expOneCoD15.add(Arrays.asList(new Integer[] {0, 1}));
		expOneCoD15.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expOneCoD16 = new ArrayList<List<Integer>>();
		expOneCoD16.add(Arrays.asList(new Integer[] {1, 3, 2, 0}));
		expOneCoD16.add(Arrays.asList(new Integer[] {1, 3, 1, 2, 0, 2}));
		expOneCoD16.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expOneCoD17 = new ArrayList<List<Integer>>();
		expOneCoD17.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expOneCoD17.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expOneCoD17.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expOneCoD18 = new ArrayList<List<Integer>>();
		expOneCoD18.add(Arrays.asList(new Integer[] {0, 1, 2, 3}));
		expOneCoD18.add(Arrays.asList(new Integer[] {0, 1, 0, 2, 0, 3, 1, 3, 2, 3}));
		expOneCoD18.add(Arrays.asList(new Integer[] {10, 10, 20, 10, 10}));
		List<List<Integer>> expOneCoD19 = new ArrayList<List<Integer>>();
		expOneCoD19.add(Arrays.asList(new Integer[] {}));
		expOneCoD19.add(Arrays.asList(new Integer[] {}));
		expOneCoD19.add(Arrays.asList(new Integer[] {}));
		List<List<Integer>> expOneCoD20 = new ArrayList<List<Integer>>();
		expOneCoD20.add(Arrays.asList(new Integer[] {0, 1, 3, 2}));
		expOneCoD20.add(Arrays.asList(new Integer[] {0, 1, 0, 3, 0, 2, 1, 2}));
		expOneCoD20.add(Arrays.asList(new Integer[] {10, 10, 20, 10}));
		List<List<Integer>> expOneCoD21 = new ArrayList<List<Integer>>();
		expOneCoD21.add(Arrays.asList(new Integer[] {2, 3}));
		expOneCoD21.add(Arrays.asList(new Integer[] {2, 3}));
		expOneCoD21.add(Arrays.asList(new Integer[] {10})); 
		List<List<Integer>> expOneCoD22 = new ArrayList<List<Integer>>();
		expOneCoD22.add(Arrays.asList(new Integer[] {0, 2, 3, 1}));
		expOneCoD22.add(Arrays.asList(new Integer[] {0, 2, 0, 3, 0, 1}));
		expOneCoD22.add(Arrays.asList(new Integer[] {10, 10, 20}));
		List<List<Integer>> expOneCoD23 = new ArrayList<List<Integer>>();
		expOneCoD23.add(Arrays.asList(new Integer[] {1, 2, 3}));
		expOneCoD23.add(Arrays.asList(new Integer[] {1, 2, 1, 3}));
		expOneCoD23.add(Arrays.asList(new Integer[] {10, 10}));

		List<List<Integer>> expOneCoD24 = new ArrayList<List<Integer>>();
		expOneCoD24.add(Arrays.asList(new Integer[] {2, 3}));
		expOneCoD24.add(Arrays.asList(new Integer[] {2, 3}));
		expOneCoD24.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expOneCoD25 = new ArrayList<List<Integer>>();
		expOneCoD25.add(Arrays.asList(new Integer[] {}));
		expOneCoD25.add(Arrays.asList(new Integer[] {}));
		expOneCoD25.add(Arrays.asList(new Integer[] {}));
		List<List<Integer>> expOneCoD26 = new ArrayList<List<Integer>>();
		expOneCoD26.add(Arrays.asList(new Integer[] {1, 3, 2}));
		expOneCoD26.add(Arrays.asList(new Integer[] {1, 3, 1, 2}));
		expOneCoD26.add(Arrays.asList(new Integer[] {10, 20}));
		List<List<Integer>> expOneCoD27 = new ArrayList<List<Integer>>();
		expOneCoD27.add(Arrays.asList(new Integer[] {1, 2}));
		expOneCoD27.add(Arrays.asList(new Integer[] {1, 2}));
		expOneCoD27.add(Arrays.asList(new Integer[] {10}));
		List<List<Integer>> expOneCoD28 = new ArrayList<List<Integer>>();
		expOneCoD28.add(Arrays.asList(new Integer[] {1, 2, 3}));
		expOneCoD28.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 2, 3}));
		expOneCoD28.add(Arrays.asList(new Integer[] {10, 20, 10}));
		List<List<Integer>> expOneCoD29 = new ArrayList<List<Integer>>();
		expOneCoD29.add(Arrays.asList(new Integer[] {2, 3, 1}));
		expOneCoD29.add(Arrays.asList(new Integer[] {2, 3, 1, 3}));
		expOneCoD29.add(Arrays.asList(new Integer[] {20, 10}));
		List<List<Integer>> expOneCoD30 = new ArrayList<List<Integer>>();
		expOneCoD30.add(Arrays.asList(new Integer[] {0, 3, 1, 2}));
		expOneCoD30.add(Arrays.asList(new Integer[] {0, 3, 0, 1, 0, 2}));
		expOneCoD30.add(Arrays.asList(new Integer[] {10, 20, 20}));
		List<List<Integer>> expOneCoD31 = new ArrayList<List<Integer>>();
		expOneCoD31.add(Arrays.asList(new Integer[] {0, 1, 2}));
		expOneCoD31.add(Arrays.asList(new Integer[] {0, 1, 0, 2}));
		expOneCoD31.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expOneCoD32 = new ArrayList<List<Integer>>();
		expOneCoD32.add(Arrays.asList(new Integer[] {0, 2, 1, 3}));
		expOneCoD32.add(Arrays.asList(new Integer[] {0, 2, 0, 1, 0, 3, 2, 3}));
		expOneCoD32.add(Arrays.asList(new Integer[] {10, 20, 20, 10}));
		List<List<Integer>> expOneCoD33 = new ArrayList<List<Integer>>();
		expOneCoD33.add(Arrays.asList(new Integer[] {2, 3, 0, 1}));
		expOneCoD33.add(Arrays.asList(new Integer[] {2, 3, 0, 1, 0, 3}));
		expOneCoD33.add(Arrays.asList(new Integer[] {20, 10, 10})); 
		List<List<Integer>> expOneCoD34 = new ArrayList<List<Integer>>();
		expOneCoD34.add(Arrays.asList(new Integer[] {0, 1,  2, 3}));
		expOneCoD34.add(Arrays.asList(new Integer[] {0, 1, 0, 2, 0, 3, 1, 2, 1, 3}));
		expOneCoD34.add(Arrays.asList(new Integer[] {10, 20, 20, 10, 10}));
		List<List<Integer>> expOneCoD35 = new ArrayList<List<Integer>>();
		expOneCoD35.add(Arrays.asList(new Integer[] {1, 2, 3, 0}));
		expOneCoD35.add(Arrays.asList(new Integer[] {1, 2, 1, 3, 0, 2, 0, 3}));
		expOneCoD35.add(Arrays.asList(new Integer[] {20, 20, 10, 10}));

		expOneCoD.add(expOneCoD0); expOneCoD.add(expOneCoD1); expOneCoD.add(expOneCoD2); expOneCoD.add(expOneCoD3);
		expOneCoD.add(expOneCoD4); expOneCoD.add(expOneCoD5); expOneCoD.add(expOneCoD6); expOneCoD.add(expOneCoD7);
		expOneCoD.add(expOneCoD8); expOneCoD.add(expOneCoD9); expOneCoD.add(expOneCoD10); expOneCoD.add(expOneCoD11);
		expOneCoD.add(expOneCoD12); expOneCoD.add(expOneCoD13); expOneCoD.add(expOneCoD14); expOneCoD.add(expOneCoD15);
		expOneCoD.add(expOneCoD16); expOneCoD.add(expOneCoD17); expOneCoD.add(expOneCoD18); expOneCoD.add(expOneCoD19);
		expOneCoD.add(expOneCoD20); expOneCoD.add(expOneCoD21); expOneCoD.add(expOneCoD22); expOneCoD.add(expOneCoD23);
		expOneCoD.add(expOneCoD24); expOneCoD.add(expOneCoD25); expOneCoD.add(expOneCoD26); expOneCoD.add(expOneCoD27); 
		expOneCoD.add(expOneCoD28); expOneCoD.add(expOneCoD29); expOneCoD.add(expOneCoD30); expOneCoD.add(expOneCoD31); 
		expOneCoD.add(expOneCoD32); expOneCoD.add(expOneCoD33); expOneCoD.add(expOneCoD34); expOneCoD.add(expOneCoD35);

		// c. With two CoDs
		List<List<List<Integer>>> expTwoCoDs = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> expTwoCoDs0 = new ArrayList<List<Integer>>();
		expTwoCoDs0.add(Arrays.asList(new Integer[] {0, 2, 3, 1}));
		expTwoCoDs0.add(Arrays.asList(new Integer[] {0, 2, 0, 3, 1, 2, 1, 3}));
		expTwoCoDs0.add(Arrays.asList(new Integer[] {10, 10, 10, 10}));
		List<List<Integer>> expTwoCoDs1 = new ArrayList<List<Integer>>();
		expTwoCoDs1.add(Arrays.asList(new Integer[] {0, 1, 3, 2}));
		expTwoCoDs1.add(Arrays.asList(new Integer[] {0, 1, 0, 3, 2, 3}));
		expTwoCoDs1.add(Arrays.asList(new Integer[] {10, 10, 10}));
		List<List<Integer>> expTwoCoDs2 = new ArrayList<List<Integer>>();
		expTwoCoDs2.add(Arrays.asList(new Integer[] {0, 1, 2}));
		expTwoCoDs2.add(Arrays.asList(new Integer[] {0, 1, 0, 2}));
		expTwoCoDs2.add(Arrays.asList(new Integer[] {10, 10})); 
		List<List<Integer>> expTwoCoDs3 = new ArrayList<List<Integer>>();
		expTwoCoDs3.add(Arrays.asList(new Integer[] {1, 3, 2}));
		expTwoCoDs3.add(Arrays.asList(new Integer[] {1, 3, 2, 3}));
		expTwoCoDs3.add(Arrays.asList(new Integer[] {10, 10}));
		List<List<Integer>> expTwoCoDs4 = new ArrayList<List<Integer>>();
		expTwoCoDs4.add(Arrays.asList(new Integer[] {1, 2}));
		expTwoCoDs4.add(Arrays.asList(new Integer[] {1, 2}));
		expTwoCoDs4.add(Arrays.asList(new Integer[] {10}));   
		List<List<Integer>> expTwoCoDs5 = new ArrayList<List<Integer>>();
		expTwoCoDs5.add(Arrays.asList(new Integer[] {}));
		expTwoCoDs5.add(Arrays.asList(new Integer[] {}));
		expTwoCoDs5.add(Arrays.asList(new Integer[] {}));

		expTwoCoDs.add(expTwoCoDs0); expTwoCoDs.add(expTwoCoDs1); expTwoCoDs.add(expTwoCoDs2);
		expTwoCoDs.add(expTwoCoDs3); expTwoCoDs.add(expTwoCoDs4); expTwoCoDs.add(expTwoCoDs5);

		// d. Combine the three Lists into expected
		expected.addAll(expNoCoD);
		expected.addAll(expOneCoD);
		expected.addAll(expTwoCoDs);

		// Actual
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		for (int i = 0; i < expNoCoD.size(); i++) {
			List<List<Integer>> currentVoicesInChord = allPossibleVoicesNoCoD.get(i);
			List<List<Integer>> currentActual =	
				Transcription.getVoiceCrossingInformationInChord(pitchesInChordNoCoD, currentVoicesInChord); 
			actual.add(currentActual);
		}
		for (int i = 0; i < expOneCoD.size(); i++) {
			List<List<Integer>> currentVoicesInChord = allPossibleVoicesOneCoD.get(i);
			List<List<Integer>> currentActual =	
				Transcription.getVoiceCrossingInformationInChord(pitchesInChordOneCoD, currentVoicesInChord);
			actual.add(currentActual);
		}
		for (int i = 0; i < expTwoCoDs.size(); i++) {
			List<List<Integer>> currentVoicesInChord = allPossibleVoicesTwoCoDs.get(i);
			List<List<Integer>> currentActual =	
				Transcription.getVoiceCrossingInformationInChord(pitchesInChordTwoCoDs, currentVoicesInChord);
			actual.add(currentActual);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);  	
	}


	@Test
	public void testGetVoiceCrossingInformationInChord() {
		Tablature tablature = new Tablature(encodingTestpiece);

		List<List<Integer>> voiceAssignments = getVoiceAssignments(true);

		List<List<Integer>> empty = new ArrayList<List<Integer>>();
		empty.add(new ArrayList<Integer>()); empty.add(new ArrayList<Integer>()); empty.add(new ArrayList<Integer>());
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Chord 0-5
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty); 
		expected.add(empty); expected.add(empty);
		// Chord 6
		// pitchesInChord = [43, 58, 62, 67]; voiceAssignment = [2, 3, 1, -1, 0]; voicesInChord = [4, 2, 0, 1]
		// [43, 58, 62, 67]
		// [4 , 2 , 0 , 1 ]
		// Start with index 0 and compare elements in both lists with elements at all higher other indices; the 
		// higher voice number should have the lower pitch. If not: add details to expected. Then go to index 1, etc.
		List<List<Integer>> expected6 = new ArrayList<List<Integer>>();
		expected6.add(Arrays.asList(new Integer[]{0, 1}));
		expected6.add(Arrays.asList(new Integer[]{0, 1}));
		expected6.add(Arrays.asList(new Integer[]{5}));
		expected.add(expected6);
		// Chords 7-15
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty);
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty);
		expected.add(empty);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
//		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
//		int lowestNoteIndex = 0;
		for (int i = 0; i < tablature.getChords().size(); i++) {
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
//			List<Integer> currentPitchesInChord = 
//				FeatureGenerator.getPitchesInChord(basicTabSymbolProperties, null, lowestNoteIndex);
			List<Integer> currentPitchesInChord = tablature.getPitchesInChord(i);
			List<List<Double>> currentChordVoiceLabels = 
				LabelTools.getChordVoiceLabels(currentVoiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentChordVoiceLabels);
			actual.add(Transcription.getVoiceCrossingInformationInChord(currentPitchesInChord, 
				currentVoicesInChord));
//			lowestNoteIndex += tablature.getTablatureChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual); 
	}


	@Test
	public void testGetVoiceCrossingInformationInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece);
		List<List<Integer>> voiceAssignments = getVoiceAssignments(false);

		List<List<Integer>> empty = new ArrayList<List<Integer>>();
		empty.add(new ArrayList<Integer>()); empty.add(new ArrayList<Integer>()); empty.add(new ArrayList<Integer>());
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// Chord 0-5
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty);
		expected.add(empty);
		// Chord 6
		// pitchesInChord = [43, 55, 58, 62, 67]; voiceAssignment = [2, 3, 1, -1, 0]; voicesInChord = [4, 3, 2, 0, 1]
		// [43, 55, 58, 62, 67]
		// [4 , 3,  2 , 0 , 1 ]
		// Start with index 0 and compare elements in both lists with elements at all higher other indices; the 
		// higher voice number should have the lower pitch. If not: add details to expected. Then go to index 1, etc.
		List<List<Integer>> expected6 = new ArrayList<List<Integer>>();
		expected6.add(Arrays.asList(new Integer[]{0, 1}));
		expected6.add(Arrays.asList(new Integer[]{0, 1}));
		expected6.add(Arrays.asList(new Integer[]{5}));
		expected.add(expected6);
		// Chord 7
		// pitchesInChord = [43, 55, 57, 66, 67]; voiceAssignment = [1, -1, 0, -1, -1]; voicesInChord = [4, 3, 2, 0, 1]
		// [43, 55, 57, 66, 67]
		// [4 , 3,  2 , 0 , 1 ]
		// Start with index 0 and compare elements in both lists with elements at all higher other indices; the 
		// higher voice number should have the lower pitch. If not: add details to expected. Then go to index 1, etc.
		List<List<Integer>> expected7 = new ArrayList<List<Integer>>();
		expected7.add(Arrays.asList(new Integer[]{0, 1}));
		expected7.add(Arrays.asList(new Integer[]{0, 1}));
		expected7.add(Arrays.asList(new Integer[]{1}));
		expected.add(expected7);
		// Chords 8-15
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty);
		expected.add(empty); expected.add(empty); expected.add(empty); expected.add(empty);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int lowestNoteIndex = 0;
		List<List<Note>> chords = transcription.getChords();
		for (int i = 0; i < transcription.getChords().size(); i++) {
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
//			List<Integer> currentPitchesInChord = 
//				FeatureGenerator.getPitchesInChord(null, basicNoteProperties, lowestNoteIndex);
			List<Integer> currentPitchesInChord = Transcription.getPitchesInChord(chords.get(i));
			List<List<Double>> currentChordVoiceLabels = 
				LabelTools.getChordVoiceLabels(currentVoiceAssignment, mnv);
			List<List<Integer>> currentVoicesInChord = 
				LabelTools.getVoicesInChord(currentChordVoiceLabels);
			List<List<Integer>> currentAllPitchesAndVoicesInChord = 
				Transcription.getAllPitchesAndVoicesInChord(basicNoteProperties, currentPitchesInChord, 
				currentVoicesInChord, voiceLabels, lowestNoteIndex);   
			// voicesInChord must be a List<List>>
			currentPitchesInChord = currentAllPitchesAndVoicesInChord.get(0);
			currentVoicesInChord = new ArrayList<List<Integer>>();
			for (int j : currentAllPitchesAndVoicesInChord.get(1)) {
				int currentVoice = j;
				List<Integer> voiceWrapped = Arrays.asList(new Integer[]{currentVoice});
				currentVoicesInChord.add(voiceWrapped);
			}
			actual.add(Transcription.getVoiceCrossingInformationInChord(currentPitchesInChord, currentVoicesInChord));
			lowestNoteIndex += transcription.getChords().get(i).size();
		}
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual); 
	}


	@Test
	public void testGetVoiceRangeInformation() {
		Transcription transcription = new Transcription(midiTestpiece);
		
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{64, 72});
		expected.add(new Integer[]{60, 69});
		expected.add(new Integer[]{57, 60});
		expected.add(new Integer[]{45, 57});
		expected.add(new Integer[]{45, 47});
		
		List<Integer[]> actual = transcription.getVoiceRangeInformation();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGenerateChordDictionary() {
		Transcription transcription = new Transcription(midiTestpiece);
			
		// Determine expected
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 69, 72})); 
		expected.add(Arrays.asList(new Integer[]{48})); 
		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65, 65})); 
		expected.add(Arrays.asList(new Integer[]{45})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 60, 64, 69})); 
		expected.add(Arrays.asList(new Integer[]{59, 68})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		expected.add(Arrays.asList(new Integer[]{68}));
		expected.add(Arrays.asList(new Integer[]{69}));
		expected.add(Arrays.asList(new Integer[]{66}));
				
		// Calculate actual
		List<List<Integer>> actual = transcription.generateChordDictionary();
				
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGenerateVoiceAssignmentDictionary(){
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);

		// Determine expected. Add each voice assignment only once
 		List<List<Integer>> expected = new ArrayList<List<Integer>>();  		
 		expected.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
		expected.add(Arrays.asList(new Integer[]{2, 3, 1, 0, -1}));
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
		expected.add(Arrays.asList(new Integer[]{3, 3, 2, 1, 0}));		
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
		expected.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
		expected.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
		expected.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));

		// Calculate actual
		int largestNumberOfVoices = transcription.getNumberOfVoices();
		List<List<Integer>> actual = 
			transcription.generateVoiceAssignmentDictionary(/*tablature,*/ largestNumberOfVoices);

		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGenerateVoiceAssignmentDictionaryNonTab(){
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece);

		// Determine expected. Add each voice assignment only once
 		List<List<Integer>> expected = new ArrayList<List<Integer>>();  		
 		expected.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
//		expected.add(Arrays.asList(new Integer[]{2, 3, 1, 0, -1}));
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
		expected.add(Arrays.asList(new Integer[]{3, 4, 2, 1, 0}));		
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
		expected.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
		expected.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
		expected.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));

		// Calculate actual
		int largestNumberOfVoices = transcription.getNumberOfVoices();
		List<List<Integer>> actual = 
			transcription.generateVoiceAssignmentDictionary(/*null,*/ largestNumberOfVoices);
		
		for (List<Integer> l : actual) {
			System.out.println(l);
		}

		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	// OLD STUFF
//	public void testDetermineVoicesSNU() {
//		List<List<Double>> vl = new ArrayList<>();
//		vl.add(V_0); 
//		vl.add(V_1);
//		vl.add(Transcription.combineLabels(V_0, V_2));
//		vl.add(Transcription.combineLabels(V_0, V_2));
//		vl.add(Transcription.combineLabels(V_0, V_2));
//		vl.add(Transcription.combineLabels(V_0, V_2));
//
//		List<List<Double>> dl = new ArrayList<>();
//		dl.add(SIXTEENTH);
//		dl.add(EIGHTH);
//		dl.add(Transcription.combineLabels(EIGHTH, QUARTER));
//		dl.add(Transcription.combineLabels(EIGHTH, QUARTER));
//		dl.add(QUARTER);
//		dl.add(HALF);
//
//		List<Integer[]> vls = new ArrayList<>();
//		vls.add(null);
//		vls.add(new Integer[]{0, 2});
//		vls.add(new Integer[]{2, 0});
//		// NB: Doesn't happen in practice: the case where both voices have a note with the same 
//		// duration is treated like the case where the lowest voice has the note with the longest 
//		// duration -- meaning that vls would contain [2, 0] here
//		vls.add(new Integer[]{0, 2});
//		vls.add(new Integer[]{2, 0});
//
//		List<Integer[]> expected = new ArrayList<>();
//		expected.add(null);
//		expected.add(null);
//		// Different duration, voice 0 has longest note: voice 0 first
//		expected.add(new Integer[]{0, 2});
//		// Different duration, voice 2 has longest note: voice 2 first
//		expected.add(new Integer[]{2, 0});
//		// Same duration: always lowest voice first
//		expected.add(new Integer[]{2, 0});
//		// Same duration: always lowest voice first
//		expected.add(new Integer[]{2, 0});
//		
//		List<Integer[]> actual = Transcription.determineVoicesSNU(vl, dl, vls);
//		
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) == null) {
//				assertEquals(expected.get(i), actual.get(i));
//			}
//			else {
//				assertEquals(expected.get(i).length, actual.get(i).length);
//				for (int j = 0; j < expected.get(i).length; j++) {
//					assertEquals(expected.get(i)[j], actual.get(i)[j]);
//				}
//			}
//		}	
//	}
//
//
//	public void testHandleCourseCrossingsOLD() {
//		Tablature tablature = new Tablature(encodingTestpiece, true);
//		Transcription transcription = new Transcription();
//		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		transcription.makeNoteSequence();
//		transcription.initialiseVoiceLabels(null); 
//		transcription.initialiseDurationLabels(null);
//		if (transcription.checkChords(tablature) == false) {
//			throw new RuntimeException("ERROR: Chord error (see console).");
//		}
//		transcription.handleSNUsOLD(tablature, Type.FROM_FILE);
//
//		// a. NoteSequence
//		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
//		// with the same pitch and onset time randomly
//		List<Note> expectedNotes = new ArrayList<Note>();
//		for (Note n : transcription.getNoteSequence()) {
//			expectedNotes.add(n);
//		}
//		expectedNotes.set(6, Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4), null));
//		expectedNotes.set(7, Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8), null));  
//		// b. Voice labels
//		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(transcription.getVoiceLabels());
//		expectedVoiceLabels.set(6, V_0);
//		expectedVoiceLabels.set(7, V_1);
//		// c. Duration labels
//		List<List<Double>> expectedDurationLabels = new ArrayList<List<Double>>(transcription.getDurationLabels());
//		expectedDurationLabels.set(6, QUARTER);
//		expectedDurationLabels.set(7, EIGHTH);
//
//		// Calculate actual
//		transcription.handleCourseCrossingsOLD(tablature, Type.FROM_FILE);
//		List<Note> actualNotes = new ArrayList<Note>();
//		for (Note n : transcription.getNoteSequence()) {
//			actualNotes.add(n);
//		}
//		List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
//		List<List<Double>> actualDurationLabels = transcription.getDurationLabels();
//
//		// a. NoteSequence 
//		assertEquals(expectedNotes.size(), actualNotes.size());
//		for (int i = 0; i < expectedNotes.size(); i++) {
//			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//			// objects: therefore check that pitch, metricTime, and metricDuration are the same
//			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
//			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
//			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
//		}
//		// b. Voice labels
//		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
//		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
//			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
//			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
//				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
//			}
//		}
//		assertEquals(expectedVoiceLabels, actualVoiceLabels);
//		// c. Duration labels
//		assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
//		for (int i = 0; i < expectedDurationLabels.size(); i++) {
//			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
//			for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
//			assertEquals(expectedDurationLabels.get(i).get(j), actualDurationLabels.get(i).get(j));
//			}
//		}
//		assertEquals(expectedDurationLabels, actualDurationLabels);
//	}
//
//
////	public void testHandleCourseCrossingsOLD() {
////		Tablature tablature = new Tablature(encodingTestpiece1);
////		Transcription transcription = new Transcription();
////    transcription.setFile(midiTestpiece1);
////    transcription.setPiece();
////    transcription.initialiseNoteSequence();
////    transcription.initialiseVoiceLabels(); 
////    transcription.initialiseDurationLabelsOLD();
////    if (transcription.checkChords(tablature) == false) {
////     	throw new RuntimeException("ERROR: Chord error (see console).");
////    }
////    transcription.handleCoDNotesOLD(tablature);
////    
////    // Determine expected
////    // a. NoteSequence
////    // NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
////    // with the same pitch and onset time randomly
////    List<Note> expectedNotes = new ArrayList<Note>();
////    for (Note n : transcription.getNoteSequence()) {
////    	expectedNotes.add(n);
////    }
////    expectedNotes.set(6, Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
////    expectedNotes.set(7, Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));  
////    // b. Voice labels
////    List<List<Double>> expectedVoiceLabels = transcription.getVoiceLabels();
////    expectedVoiceLabels.set(6, Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 0.0}));
////    expectedVoiceLabels.set(7, Arrays.asList(new Double[]{0.0, 1.0, 0.0, 0.0, 0.0}));
////    // c. Duration labels
////    List<List<List<Double>>> expectedDurationLabels = transcription.getDurationLabelsOLD();
////    List<List<Double>> quarterDurationLabel = new ArrayList<List<Double>>();
////    quarterDurationLabel.add(Transcription.QUARTER);
////    List<List<Double>> eighthDurationLabel = new ArrayList<List<Double>>();
////    quarterDurationLabel.add(Transcription.EIGHTH);
////    expectedDurationLabels.set(6, quarterDurationLabel);
////    expectedDurationLabels.set(7, eighthDurationLabel);
////    
////    // Calculate actual
////    transcription.handleCourseCrossingsOLD(tablature);
////    List<Note> actualNotes = new ArrayList<Note>();
////    for (Note n : transcription.getNoteSequence()) {
////    	actualNotes.add(n);
////    }
////    List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
////    List<List<List<Double>>> actualDurationLabels = transcription.getDurationLabelsOLD();
////    
////    // Assert equality
////    // a. NoteSequence 
////    assertEquals(expectedNotes.size(), actualNotes.size());
////    for (int i = 0; i < expectedNotes.size(); i++) {
////    	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
////    	// objects: therefore check that pitch, metricTime, and metricDuration are the same
////    	assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
////    	assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
////    	assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
////    }
////    // b. Voice labels
////    assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
////    for (int i = 0; i < expectedVoiceLabels.size(); i++) {
////    	assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
////    	for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
////    		assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
////    	}
////    }
////    assertEquals(expectedVoiceLabels, actualVoiceLabels);
////    // c. Duration labels
////    assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
////		for (int i = 0; i < expectedDurationLabels.size(); i++) {
////			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
////	  		for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
////   			assertEquals(expectedDurationLabels.get(i).get(j).size(), actualDurationLabels.get(i).get(j).size());
////  			for (int k = 0; k < expectedDurationLabels.get(i).get(j).size(); k++) {
////    			assertEquals(expectedDurationLabels.get(i).get(j).get(k), actualDurationLabels.get(i).get(j).get(k));
////	  		}
////  		}
////  	}
////  	assertEquals(expectedDurationLabels, actualDurationLabels);
////	}
//
//
//	public void testSetAndGetEqualDurationUnisonsInfo() {
//		Transcription transcription = new Transcription();
////		transcription.setFile(midiTestpiece1);
//		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		transcription.setPiece(null);
//		transcription.makeNoteSequence();
//		// The voice labels must be initialised before the NoteSeqeunce is adapted, as initialiseVoiceLabels() needs
//		// the actual Note objects from the NoteSequence and not the adaptations made below
//		transcription.initialiseVoiceLabels(null); 
////		transcription.setMeterInfo(midiTestpiece1);
//		transcription.setMeterInfo();
//		// Before calling handleUnisons(): adapt transcription so that all unisons become EDUs
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		// Give all unison notes a duration of a quarter
//		// NB: Before handleUnisons() is called, all unison notes have been added to noteSeq with the one in the 
//		// lower voice first. That means that the notes at indices 12 and 16 need to be adapted
//		Note n12 = noteSeq.getNoteAt(12);
//		noteSeq.replaceNoteAt(12, Transcription.createNote(n12.getMidiPitch(), n12.getMetricTime(), new Rational(1, 4), null));
//		Note n16 = noteSeq.getNoteAt(16);
//		noteSeq.replaceNoteAt(16, Transcription.createNote(n16.getMidiPitch(), n16.getMetricTime(), new Rational(1, 4), null));  
//		transcription.handleUnisons(Type.FROM_FILE);
//		transcription.setBasicNoteProperties();
//
//		List<Integer[]> expected = new ArrayList<Integer[]>();
//		for (int i = 0; i < noteSeq.size(); i++) {
//			expected.add(null);
//		}
////		expected.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////		expected.set(13, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////		expected.set(16, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
////		expected.set(17, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
//		expected.set(12, new Integer[]{1, 0, 13});
//		expected.set(13, new Integer[]{1, 0, 12});
//		expected.set(16, new Integer[]{3, 2, 17});
//		expected.set(17, new Integer[]{3, 2, 16});
//
//		List<Integer[]> actual = transcription.getVoicesEDU();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) == null) {
//				assertEquals(expected.get(i), actual.get(i));
//			}
////			else {
////				assertEquals(expected.get(i).size(), actual.get(i).size());
////				for (int j = 0; j < expected.get(i).size(); j++) {
////					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
////				}
////			}
//			else {
//				assertEquals(expected.get(i).length, actual.get(i).length);
//				for (int j = 0; j < expected.get(i).length; j++) {
//					assertEquals(expected.get(i)[j], actual.get(i)[j]);
//				}
//			}
//		}
//	}
//
//
////	@SuppressWarnings("unchecked")
////	public void testMakeNotes() {
////		Transcription t1 = new Transcription();
////		Tablature tab = new Tablature(encodingTestpiece, false); 
////		t1.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		t1.setName();
////		Transcription t2 = new Transcription();
////		t2.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		t2.setName();
////
////		List<List<Note>> expected = new ArrayList<>();
////		List<Note> expected1 = getUnhandledNotesFromPiece(t1.getPiece(), "testpiece");
////		Collections.swap(expected1, 6, 7); // course crossing
////		expected1.remove(12); // SNU
////		expected.add(expected1);
////		List<Note> expected2 = getUnhandledNotesFromPiece(t2.getPiece(), "testpiece");
////		Collections.swap(expected2, 12, 13); // unison
////		expected.add(expected2);
////
////		List<List<Note>> actual = new ArrayList<>();
////		actual.add((List<Note>) t1.makeNotes(tab).get(0));
////		actual.add((List<Note>) t2.makeNotes(null).get(0));
////
////		assertEquals(expected.size(), actual.size());
////		for (int i = 0; i < expected.size(); i++) {
////			assertEquals(expected.get(i).size(), actual.get(i).size()); 
////			for (int j = 0; j < expected.get(i).size(); j++) {
////				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
////			}
////		}
////	}
//
//
//	public void testMakeNoteSequence() {
//		Transcription t = new Transcription();
//		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t.setName();
//
//		NoteSequence expected = getNoteSequence(t.getScorePiece(), "testpiece");
//
//		NoteSequence actual = t.makeNoteSequence();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}
//
//
////	public void testMakeInitialNoteSequence() {
////		Transcription t = new Transcription();
////		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		t.setName();
////
////		NoteSequence expected = getNoteSequence(t.getPiece(), "testpiece");
////
////		NoteSequence actual = t.makeInitialNoteSequence();
////		// Account for inconsistent behaviour in ordering of equal-pitch notes
////		if (actual.get(12).getMetricDuration().equals(new Rational(1, 4)) && 
////			actual.get(13).getMetricDuration().equals(new Rational(1, 8))) {
////			actual.swapNotes(12, 13);
////			System.out.println("12 and 13 swapped");
////		}
////		if (actual.get(16).getMetricDuration().equals(new Rational(1, 4)) && 
////			actual.get(17).getMetricDuration().equals(new Rational(1, 2))) {
////			actual.swapNotes(16, 17);
////			System.out.println("16 and 17 swapped");
////		}
////
////		assertEquals(expected.size(), actual.size());
////		for (int i = 0; i < expected.size(); i++) {
////			assertEquals(expected.get(i), actual.get(i));
////		}
////	}
//
//
////	public void testUniformiseInitialNoteSequence() {
////		Transcription t = new Transcription();
////		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		t.setName();
////		NoteSequence noteSeq = t.makeInitialNoteSequence(); 
////
////		// Account for inconsistent behaviour in ordering of equal-pitch notes
////		if (noteSeq.get(12).getMetricDuration().equals(new Rational(1, 4)) &&
////			noteSeq.get(13).getMetricDuration().equals(new Rational(1, 8))) {
////			noteSeq.swapNotes(12, 13);
////			System.out.println("12 and 13 swapped");
////		}
////		if (noteSeq.get(16).getMetricDuration().equals(new Rational(1, 4)) && 
////			noteSeq.get(17).getMetricDuration().equals(new Rational(1, 2))) {
////			noteSeq.swapNotes(16, 17);
////			System.out.println("16 and 17 swapped");
////		}
////		// Order equal-pitch notes 12 and 13 incorrectly
////		noteSeq.swapNotes(12, 13);
////
////		NoteSequence expected = getNoteSequence(t.getPiece(), "testpiece");
////		
////		NoteSequence actual = t.uniformiseInitialNoteSequence(noteSeq);
////
////		assertEquals(expected.size(), actual.size());
////		for (int i = 0; i < expected.size(); i++) {
////			assertEquals(expected.get(i), actual.get(i));
////		}
////	}
//
//
////	public void testInitialiseNoteSequenceOLD() {
////		Transcription transcription = new Transcription();
//////		transcription.setFile(midiTestpiece1);
////		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//////		transcription.setPiece(null);
////		transcription.initialiseNoteSequence();
////
////		// Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
////		// the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
////		List<Note> expected = new ArrayList<Note>();
////		// Chord 0
////		expected.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4)));
////		// Chord 1
////		expected.add(Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16)));
////		expected.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));
////		expected.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
////		// Chord 2
////		expected.add(Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16)));
////		// Chord 3
////		expected.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8)));
////		expected.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));
////		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
////		// Chord 4
////		expected.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8)));
////		// Chord 5
////		expected.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2)));
////		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4)));
////		// Chord 6
////		expected.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8)));
////		expected.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8)));
////		expected.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4)));
////		// Chord 7
////		expected.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8)));
////		expected.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8)));
////		// Chord 8
////		expected.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2)));
////		expected.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2)));
////		expected.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2)));
////		expected.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16)));
////		// Chords 9-14
////		expected.add(Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16)));
////		expected.add(Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32)));
////		expected.add(Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32)));
////		expected.add(Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32)));
////		expected.add(Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32)));
////		expected.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4)));
////		// Chord 15
////		expected.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4)));
////		expected.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4)));
////
////		NoteSequence noteSeq = transcription.getNoteSequence();
////		List<Note> actual = new ArrayList<Note>();
////		for (Note n : noteSeq) {
////			actual.add(n);
////		}
////
////		assertEquals(expected.size(), actual.size());
////		for (int i = 0; i < expected.size(); i++) {
//////			assert(expected.get(i).isEquivalent(actual.get(i)));
////			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
////			// objects: therefore check that pitch, metricTime, and metricDuration are the same
////			assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
////			assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
////			assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
////		}
////	}
//
//
//	public void testGetChordsFromNoteSequence() {
////		// Populated NoteSequence
//		Transcription t1 = new Transcription();
//		t1.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t1.setName();
//		t1.setNoteSequence();
////		// Populated and uniformised (i.e., initialised) NoteSequence
////		Transcription t2 = new Transcription();
////		t2.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		t2.setName();
////		t2.makeNoteSequence();
//		// Populated, uniformised, and unisons handled
//		// TODO
//		// Populated, uniformised, and SNUs and course crossings handled
//		// TODO
//
//		List<Integer> chordSizes = Arrays.asList(new Integer[]{4, 4, 1, 5, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});
//
//		List<List<Note>> expected = new ArrayList<>();
////		// Populated NoteSequence
//		NoteSequence noteSeq1 = getNoteSequence(t1.getScorePiece(), "testpiece");
//		List<List<Note>> expected1 = new ArrayList<>();
//		int start = 0;
//		for (int chordSize : chordSizes) {
//			List<Note> currChord = new ArrayList<>();
//			for (int j = start; j < start + chordSize; j++) {
//				currChord.add(noteSeq1.get(j));
//			}
//			expected1.add(currChord);
//			start += chordSize;
//		}
//		expected.addAll(expected1);
////		// Populated and uniformised (i.e., initialised) NoteSequence
////		NoteSequence noteSeq2 = getNoteSequence(t2.getPiece(), "testpiece");
////		List<List<Note>> expected2 = new ArrayList<>();
////		start = 0;
////		for (int chordSize : chordSizes) {
////			List<Note> currChord = new ArrayList<>();
////			for (int j = start; j < start + chordSize; j++) {
////				currChord.add(noteSeq2.get(j));
////			}
////			expected2.add(currChord);
////			start += chordSize;
////		}
////		// Swap the incorrectly ordered notes in chord 3
////		Collections.swap(expected2.get(3), 3, 4);
////		expected.addAll(expected2);
//
//		List<List<Note>> actual = t1.getChordsFromNoteSequence();
////		actual.addAll(t2.getChordsFromNoteSequence());
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}	
//	}
//
//
//	public void testInitialiseVoiceLabels() {
//		Transcription t = new Transcription();
//		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t.setName();
//		t.makeNoteSequence();
//		t.initialiseVoiceLabels(null);
//
//		List<Double> v0 = V_0;
//		List<Double> v1 = V_1;
//		List<Double> v2 = V_2;
//		List<Double> v3 = V_3;
//		List<Double> v4 = V_4;
//
//		List<List<Double>> expected = new ArrayList<List<Double>>();
//		// Chord 0
//		expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//		// Chord 1 
//		expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//		// Chord 2 
//		expected.add(v3); 
//		// Chord 3 
//		expected.add(v4); expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//		// Chord 4
//		expected.add(v4); 
//		// Chord 5 
//		expected.add(v4); expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//		// Chord 6 
//		expected.add(v4); expected.add(v2); expected.add(v0); expected.add(v1);
//		// Chord 7 
//		expected.add(v2); expected.add(v0); 
//		// Chord 8 
//		expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//		// Chords 9-14
//		expected.add(v0);
//		expected.add(v0);
//		expected.add(v0);
//		expected.add(v0);
//		expected.add(v0);
//		expected.add(v0);
//		// Chord 15
//		expected.add(v3); expected.add(v2); expected.add(v1); expected.add(v0);
//
//		List<List<Double>> actual = t.getVoiceLabels();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size());
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}
//		assertEquals(expected, actual);
//	}
//
//
//	public void testInitialiseDurationLabels() {
//		Transcription t = new Transcription();
//		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t.setName();
//		t.makeNoteSequence();
//		t.initialiseVoiceLabels(null);
//		t.initialiseDurationLabels(null);
//
//		List<Double> th = THIRTYSECOND;
//		List<Double> s = SIXTEENTH;
//		List<Double> e = EIGHTH;
//		List<Double> de = DOTTED_EIGHTH;
//		List<Double> q = QUARTER;
//		List<Double> h = HALF;
//		
//		List<List<Double>> expected = new ArrayList<List<Double>>();
//		// Chord 0
//		expected.add(q); expected.add(q); expected.add(q); expected.add(q);
//		// Chord 1
//		expected.add(de); expected.add(q); expected.add(e); expected.add(q);
//		// Chord 2
//		expected.add(s);
//		// Chord 3
//		expected.add(e); expected.add(q); expected.add(q); expected.add(e); expected.add(q);
//		// Chord 4
//		expected.add(e);
//		// Chord 5
//		expected.add(q); expected.add(h); expected.add(q); expected.add(q); expected.add(q);
//		// Chord 6
//		expected.add(q); expected.add(e); expected.add(e); expected.add(q);
//		// Chord 7
//		expected.add(e); expected.add(e);
//		// Chord 8
//		expected.add(h); expected.add(h); expected.add(h); expected.add(s);
//		// Chords 9-14
//		expected.add(s);
//		expected.add(th);
//		expected.add(th);
//		expected.add(th);
//		expected.add(th);
//		expected.add(q);
//		// Chord 15
//		expected.add(q); expected.add(q); expected.add(q); expected.add(q);
//
//		List<List<Double>> actual = t.getDurationLabels();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}
//		assertEquals(expected, actual);
//	}
//
//
//	public void testCheckChords() {
//	}
//
//
//	public void testHandleSNUsOLD() {
//		Tablature tab = new Tablature(encodingTestpiece, true);
//		Transcription t = new Transcription();
//		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t.setName();
//		t.setNotes(tab);
//		t.setNoteSequence();
//		t.initialiseVoiceLabels(null); 
//		t.initialiseDurationLabels(null);
//		if (t.checkChords(tab) == false) {
//			throw new RuntimeException("ERROR: Chord error (see console).");
//		}
//
//		// a. NoteSequence
//		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
//		// with the same pitch and onset time randomly
//		List<Note> expectedNotes = new ArrayList<Note>();
//		for (Note n : t.getNoteSequence()) {
//			expectedNotes.add(n);
//		}
//		expectedNotes.remove(12); 
//		// b. Voice labels
//		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(t.getVoiceLabels());
//		expectedVoiceLabels.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//		expectedVoiceLabels.remove(13);
//		// c. Duration labels
//		List<List<Double>> expectedDurationLabels = new ArrayList<List<Double>>(t.getDurationLabels());
//		List<Double> adaptedDurationLabel = new ArrayList<Double>(QUARTER);
//		adaptedDurationLabel.set(3, 1.0);
//		expectedDurationLabels.set(12, adaptedDurationLabel);
//		expectedDurationLabels.remove(13);
//
//		t.handleSNUsOLD(tab, Type.FROM_FILE);
//		List<Note> actualNotes = new ArrayList<Note>();
//		for (Note n : t.getNoteSequence()) {
//			actualNotes.add(n);
//		}
//		List<List<Double>> actualVoiceLabels = t.getVoiceLabels();
//		List<List<Double>> actualDurationLabels = t.getDurationLabels();
//
//		// a. NoteSequence 
//		assertEquals(expectedNotes.size(), actualNotes.size());
//		for (int i = 0; i < expectedNotes.size(); i++) {
//			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//			// objects: therefore check that pitch, metricTime, and metricDuration are the same
//			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
//			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
//			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
//		}
//		// b. Voice labels
//		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
//		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
//			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
//			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
//				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
//			}
//		}
//		assertEquals(expectedVoiceLabels, actualVoiceLabels);
//		// c. Duration labels
//		assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
//		for (int i = 0; i < expectedDurationLabels.size(); i++) {
//			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
//	  		for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
//	  			assertEquals(expectedDurationLabels.get(i).get(j), actualDurationLabels.get(i).get(j));
//	  		}
//		}
//		assertEquals(expectedDurationLabels, actualDurationLabels);  	
//	}
//
//
//	public void testSetAndGetChordVoiceLabelsNonTabOLD() {		    
//		Transcription transcription = new Transcription(midiTestpiece);
//
//		List<Double> voice0 = V_0;
//		List<Double> voice1 = V_1;
//		List<Double> voice2 = V_2;
//		List<Double> voice3 = V_3;
//		List<Double> voice4 = V_4;
//
//		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
//		// Chord 0
//		List<List<Double>> chord0 = new ArrayList<List<Double>>(); 
//		chord0.add(voice3); chord0.add(voice2); chord0.add(voice1); chord0.add(voice0);
//		// Chord 1
//		List<List<Double>> chord1 = new ArrayList<List<Double>>();
//		chord1.add(voice3); chord1.add(voice2); chord1.add(voice1); chord1.add(voice0);
//		// Chord 2
//		List<List<Double>> chord2 = new ArrayList<List<Double>>();
//		chord2.add(voice3);
//		// Chord 3
//		List<List<Double>> chord3 = new ArrayList<List<Double>>();
//		chord3.add(voice4); chord3.add(voice3); chord3.add(voice2); chord3.add(voice0); chord3.add(voice1);
//		// Chord 4
//		List<List<Double>> chord4 = new ArrayList<List<Double>>();
//		chord4.add(voice4);
//		// Chord 5
//		List<List<Double>> chord5 = new ArrayList<List<Double>>();
//		chord5.add(voice4); chord5.add(voice3); chord5.add(voice2); chord5.add(voice1); chord5.add(voice0);
//		// Chord 6
//		List<List<Double>> chord6 = new ArrayList<List<Double>>();
//		chord6.add(voice4); chord6.add(voice2); chord6.add(voice0); chord6.add(voice1);
//		// Chord 7
//		List<List<Double>> chord7 = new ArrayList<List<Double>>();
//		chord7.add(voice2); chord7.add(voice0); 
//		// Chord 8
//		List<List<Double>> chord8 = new ArrayList<List<Double>>();
//		chord8.add(voice3); chord8.add(voice2); chord8.add(voice1); chord8.add(voice0);
//		// Chord 9-14
//		List<List<Double>> chord9 = new ArrayList<List<Double>>();
//		chord9.add(voice0);
//		List<List<Double>> chord10 = new ArrayList<List<Double>>();
//		chord10.add(voice0);
//		List<List<Double>> chord11 = new ArrayList<List<Double>>();
//		chord11.add(voice0);
//		List<List<Double>> chord12 = new ArrayList<List<Double>>();
//		chord12.add(voice0);
//		List<List<Double>> chord13 = new ArrayList<List<Double>>();
//		chord13.add(voice0);
//		List<List<Double>> chord14 = new ArrayList<List<Double>>();
//		chord14.add(voice0);
//		// Chord 15
//		List<List<Double>> chord15 = new ArrayList<List<Double>>();
//		chord15.add(voice3); chord15.add(voice2); chord15.add(voice1); chord15.add(voice0);
//
//		expected.add(chord0); expected.add(chord1); expected.add(chord2); expected.add(chord3); expected.add(chord4); 
//		expected.add(chord5); expected.add(chord6); expected.add(chord7); expected.add(chord8); expected.add(chord9);
//		expected.add(chord10); expected.add(chord11); expected.add(chord12); expected.add(chord13); 
//		expected.add(chord14); expected.add(chord15);
//
//		// Calculate actual
////		List<List<List<Double>>> actual = transcription.getChordVoiceLabels(null);
//		List<List<List<Double>>> actual = transcription.getChordVoiceLabels();
//
//		// Assert equality
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
//				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
//					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
//				}
//			}
//		}
//	}
//
//
//	public void testDeornamentPiece() {
//		// Tablature case
//		Transcription t1 = new Transcription();
//		Tablature tab1 = new Tablature(encodingTestpiece, true); 
//		t1.setPiece(MIDIImport.importMidiFile(midiTestpiece), tab1, null, Type.FROM_FILE);
//		Piece p1 = t1.getScorePiece();
//		List<Note> notes1 = getUnhandledNotesFromPiece(p1, "testpiece");
//		MetricalTimeLine mt1 = p1.getMetricalTimeLine();
//		SortedContainer<Marker> ht1 = p1.getHarmonyTrack();
//		// Non-tablature case
//		Transcription t2 = new Transcription();
//		t2.setPiece(MIDIImport.importMidiFile(midiTestpiece), null, null, Type.FROM_FILE);
//		Piece p2 = t2.getScorePiece();
//		MetricalTimeLine mt2 = p2.getMetricalTimeLine();
//		SortedContainer<Marker> ht2 = p2.getHarmonyTrack();
//
//		List<Piece> expected = new ArrayList<>();
//		Piece expected1 = new Piece();
//		NotationSystem ns1 = new NotationSystem();
//		// Voice 0
//		NotationStaff ns01 = new NotationStaff();
//		NotationVoice nv01 = new NotationVoice();		
//		List<Note> notesV01 = Arrays.asList(
//			notes1.get(3),
//			notes1.get(7),
//			notes1.get(13),
//			notes1.get(19),
//			notes1.get(22),
//			notes1.get(25),
//			Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4), mt1),
//			notes1.get(35),
//			notes1.get(39)
//			
////			expected0.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null));
////			expected0.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null));
////			expected0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null));
////			expected0.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null));
//			
//			
////			Transcription.createNote(67, new Rational(0, 4), new Rational(1, 4), mt1),
////			Transcription.createNote(67, new Rational(2, 4), new Rational(1, 4), mt1),
////			Transcription.createNote(66, new Rational(3, 4), new Rational(1, 32), mt1),
////			Transcription.createNote(64, new Rational(25, 32), new Rational(1, 32), mt1),
////			Transcription.createNote(66, new Rational(13, 16), new Rational(1, 32), mt1),
////			Transcription.createNote(67, new Rational(27, 32), new Rational(1, 32), mt1),
////			Transcription.createNote(66, new Rational(7, 8), new Rational(1, 16), mt1),
////			Transcription.createNote(67, new Rational(15, 16), new Rational(1, 16), mt1),
////			Transcription.createNote(66, new Rational(4, 4), new Rational(1, 8), mt1),
////			Transcription.createNote(62, new Rational(9, 8), new Rational(1, 8), mt1),
////			Transcription.createNote(67, new Rational(5, 4), new Rational(1, 4), mt1),
////			Transcription.createNote(63, new Rational(13, 8), new Rational(1, 8), mt1),
////			Transcription.createNote(70, new Rational(29, 16), new Rational(3, 16), mt1),
////			Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4), mt1)
//		);
//		notesV01.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv01.add(nc);
//		});
//		ns01.add(nv01);
//		ns1.add(ns01);
//		// Voice 1
//		NotationStaff ns11 = new NotationStaff();
//		NotationVoice nv11 = new NotationVoice();
//		List<Note> notesV11 = Arrays.asList(
//			Transcription.createNote(62, new Rational(0, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(62, new Rational(15, 16), new Rational(1, 16), mt1),
//			Transcription.createNote(67, new Rational(9, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(58, new Rational(5, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(63, new Rational(13, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(67, new Rational(29, 16), new Rational(3, 16), mt1),
//			Transcription.createNote(63, new Rational(8, 4), new Rational(1, 4), mt1)
//		);
//		notesV11.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv11.add(nc);
//		});
//		ns11.add(nv11);
//		ns1.add(ns11);
//		// Voice 2
//		NotationStaff ns21 = new NotationStaff();
//		NotationVoice nv21 = new NotationVoice();
//		List<Note> notesV21 = Arrays.asList(
//			Transcription.createNote(55, new Rational(0, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(55, new Rational(15, 16), new Rational(1, 16), mt1),
//			Transcription.createNote(57, new Rational(4, 4), new Rational(1, 8), mt1),
//			Transcription.createNote(58, new Rational(9, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(55, new Rational(5, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(57, new Rational(13, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(55, new Rational(29, 16), new Rational(3, 16), mt1),
//			Transcription.createNote(55, new Rational(8, 4), new Rational(1, 4), mt1)
//		);
//		notesV21.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv21.add(nc);
//		});
//		ns21.add(nv21);
//		ns1.add(ns21);
//		// Voice 3
//		NotationStaff ns31 = new NotationStaff();
//		NotationVoice nv31 = new NotationVoice();
//		List<Note> notesV31 = Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(0, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(43, new Rational(15, 16), new Rational(1, 16), mt1),
//			Transcription.createNote(55, new Rational(5, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(48, new Rational(13, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(46, new Rational(7, 4), new Rational(1, 16), mt1),
//			Transcription.createNote(43, new Rational(29, 16), new Rational(3, 16), mt1),
//			Transcription.createNote(48, new Rational(8, 4), new Rational(1, 4), mt1)
//		});
//		notesV31.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv31.add(nc);
//		});
//		ns31.add(nv31);
//		ns1.add(ns31);
//		// Voice 4
//		NotationStaff ns41 = new NotationStaff();
//		NotationVoice nv41 = new NotationVoice();
//		List<Note> notesV41 = Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(9, 8), new Rational(1, 8), mt1),
//			Transcription.createNote(43, new Rational(5, 4), new Rational(1, 4), mt1),
//			Transcription.createNote(43, new Rational(6, 4), new Rational(1, 8), mt1),
//			Transcription.createNote(45, new Rational(13, 8), new Rational(1, 8), mt1)
//		});
//		notesV41.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv41.add(nc);
//		});
//		ns41.add(nv41);
//		ns1.add(ns41);
//		expected1.setScore(ns1);
//		expected1.setMetricalTimeLine(mt1);
//		expected1.setHarmonyTrack(ht1);
//		expected1.setName(p1.getName());
//		expected.add(expected1);
//
//		Piece expected2 = new Piece();
//		NotationSystem ns2 = new NotationSystem();
//		// Voice 0
//		NotationStaff ns02 = new NotationStaff();
//		NotationVoice nv02 = new NotationVoice();		
//		List<Note> notesV02 = Arrays.asList(
//			Transcription.createNote(69, new Rational(0, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(69, new Rational(2, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(68, new Rational(3, 4), new Rational(1, 32), mt2),
//			Transcription.createNote(66, new Rational(25, 32), new Rational(1, 32), mt2),
//			Transcription.createNote(68, new Rational(13, 16), new Rational(1, 32), mt2),
//			Transcription.createNote(69, new Rational(27, 32), new Rational(1, 32), mt2),
//			Transcription.createNote(68, new Rational(7, 8), new Rational(1, 16), mt2),
//			Transcription.createNote(69, new Rational(15, 16), new Rational(1, 16), mt2),
//			Transcription.createNote(68, new Rational(4, 4), new Rational(1, 8), mt2),
//			Transcription.createNote(64, new Rational(9, 8), new Rational(1, 8), mt2),
//			Transcription.createNote(69, new Rational(5, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(65, new Rational(6, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(72, new Rational(7, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(69, new Rational(8, 4), new Rational(1, 4), mt2)
//		);
//		notesV02.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv02.add(nc);
//		});
//		ns02.add(nv02);
//		ns2.add(ns02);
//		// Voice 1
//		NotationStaff ns12 = new NotationStaff();
//		NotationVoice nv12 = new NotationVoice();
//		List<Note> notesV12 = Arrays.asList(
//			Transcription.createNote(64, new Rational(0, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(64, new Rational(2, 4), new Rational(1, 2), mt2),
//			Transcription.createNote(69, new Rational(4, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(60, new Rational(5, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(65, new Rational(13, 8), new Rational(1, 8), mt2),
//			Transcription.createNote(69, new Rational(15, 8), new Rational(1, 8), mt2),
//			Transcription.createNote(65, new Rational(8, 4), new Rational(1, 4), mt2)
//		);
//		notesV12.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv12.add(nc);
//		});
//		ns12.add(nv12);
//		ns2.add(ns12);
//		// Voice 2
//		NotationStaff ns22 = new NotationStaff();
//		NotationVoice nv22 = new NotationVoice();
//		List<Note> notesV22 = Arrays.asList(
//			Transcription.createNote(57, new Rational(0, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(57, new Rational(2, 4), new Rational(1, 2), mt2),
//			Transcription.createNote(59, new Rational(4, 4), new Rational(1, 8), mt2),
//			Transcription.createNote(60, new Rational(9, 8), new Rational(1, 8), mt2),
//			Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(59, new Rational(6, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(57, new Rational(7, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(57, new Rational(8, 4), new Rational(1, 4), mt2)
//		);
//		notesV22.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv22.add(nc);
//		});
//		ns22.add(nv22);
//		ns2.add(ns22);
//		// Voice 3
//		NotationStaff ns32 = new NotationStaff();
//		NotationVoice nv32 = new NotationVoice();
//		List<Note> notesV32 = Arrays.asList(new Note[]{
//			Transcription.createNote(45, new Rational(0, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(45, new Rational(2, 4), new Rational(1, 2), mt2),
//			Transcription.createNote(57, new Rational(4, 4), new Rational(1, 2), mt2),
//			Transcription.createNote(50, new Rational(6, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(48, new Rational(7, 4), new Rational(1, 16), mt2),
//			Transcription.createNote(45, new Rational(29, 16), new Rational(3, 16), mt2),
//			Transcription.createNote(50, new Rational(8, 4), new Rational(1, 4), mt2)
//		});
//		notesV32.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv32.add(nc);
//		});
//		ns32.add(nv32);
//		ns2.add(ns32);
//		// Voice 4
//		NotationStaff ns42 = new NotationStaff();
//		NotationVoice nv42 = new NotationVoice();
//		List<Note> notesV42 = Arrays.asList(new Note[]{
//			Transcription.createNote(45, new Rational(4, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(45, new Rational(5, 4), new Rational(1, 4), mt2),
//			Transcription.createNote(45, new Rational(6, 4), new Rational(1, 8), mt2),
//			Transcription.createNote(47, new Rational(13, 8), new Rational(1, 8), mt2)
//		});
//		notesV42.forEach(n -> {
//			NotationChord nc = new NotationChord(); nc.add(n); nv42.add(nc);
//		});
//		ns42.add(nv42);
//		ns2.add(ns42);
//		expected2.setScore(ns2);
//		expected2.setMetricalTimeLine(mt2);
//		expected2.setHarmonyTrack(ht2);
//		expected2.setName(p2.getName());
//		expected.add(expected2);
//	}
//
//
//	public void testDeornament() {
//		// a. Tablature case
//		Tablature tab = new Tablature(encodingTestpiece, false);
//		Transcription t = new Transcription(midiTestpiece, encodingTestpiece);
//		Transcription deornTrans = Transcription.deornament(t, tab, new Rational(1, 8));
//		List<List<Note>> expected = new ArrayList<List<Note>>();
//		// Voice 0
//		List<Note> expected0 = new ArrayList<Note>();
//		expected0.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null));
//		expected0.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null));
//		expected0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null));
//		expected0.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 1
//		List<Note> expected1 = new ArrayList<Note>();
//		expected1.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4), null));
//		expected1.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8), null));
//		expected1.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 8), null));
//		expected1.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4), null));
//		expected1.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4), null));
//		expected1.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2), null));
//		expected1.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 2
//		List<Note> expected2 = new ArrayList<Note>();
//		expected2.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4), null));
//		expected2.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4), null));
//		expected2.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), null));
//		expected2.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4), null));
//		expected2.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8), null));
//		expected2.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8), null));
//		expected2.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2), null));
//		expected2.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 3
//		List<Note> expected3 = new ArrayList<Note>();
//		expected3.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4), null));
//		expected3.add(Transcription.createNote(43, new Rational(4, 4), new Rational(1, 4), null));
//		expected3.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4), null));
//		expected3.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2), null));
//		expected3.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2), null));
//		expected3.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 4
//		List<Note> expected4 = new ArrayList<Note>();
//		expected4.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8), null));
//		expected4.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8), null));
//		expected4.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4), null));
//		expected4.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4), null));
//
//		expected.add(expected0); expected.add(expected1); expected.add(expected2);
//		expected.add(expected3); expected.add(expected4);
//
//		List<List<Note>> actual = new ArrayList<List<Note>>();
//		for (NotationStaff notationStaff: deornTrans.getScorePiece().getScore()) {
//			for (NotationVoice notationVoice : notationStaff) {
//				List<Note> currentActual = new ArrayList<Note>();
//				for (NotationChord notationChord : notationVoice) {
//					currentActual.add(notationChord.get(0));
//				}
//				actual.add(currentActual);
//			}
//		}
//
//		// b. Non-tablature case
//		tab = null;
//		t = new Transcription(midiTestpiece, null);
//		deornTrans = Transcription.deornament(t, tab, new Rational(1, 8));
//		// Voice 0
//		List<Note> expected5 = new ArrayList<Note>();
//		expected5.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8), null));
//		expected5.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8), null));
//		expected5.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4), null));
//		expected5.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 1
//		List<Note> expected6 = new ArrayList<Note>();
//		expected6.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4), null));
//		expected6.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8), null));
//		expected6.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8), null));
//		expected6.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4), null));
//		expected6.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4), null));
//		expected6.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2), null));
//		expected6.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 2
//		List<Note> expected7 = new ArrayList<Note>();
//		expected7.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4), null));
//		expected7.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4), null));
//		expected7.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4), null));
//		expected7.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4), null));
//		expected7.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8), null));
//		expected7.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8), null));
//		expected7.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2), null));
//		expected7.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 3
//		List<Note> expected8 = new ArrayList<Note>();
//		expected8.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4), null));
//		expected8.add(Transcription.createNote(45, new Rational(4, 4), new Rational(1, 4), null));
//		expected8.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4), null));
//		expected8.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2), null));
//		expected8.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2), null));
//		expected8.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 4
//		List<Note> expected9 = new ArrayList<Note>();
//		expected9.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8), null));
//		expected9.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8), null));
//		expected9.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4), null));
//		expected9.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4), null));
//
//		expected.add(expected5); expected.add(expected6); expected.add(expected7);
//		expected.add(expected8); expected.add(expected9);
//
//		for (NotationStaff notationStaff: deornTrans.getScorePiece().getScore()) {
//			for (NotationVoice notationVoice : notationStaff) {
//				List<Note> currentActual = new ArrayList<Note>();
//				for (NotationChord notationChord : notationVoice) {
//					currentActual.add(notationChord.get(0));
//				}
//				actual.add(currentActual);
//			}
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size());
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				// assertEquals(expected.get(i).get(j), actual.get(i).get(j)) does not work because the Notes are not 
//				// the same objects: therefore check that pitch, metricTime, and metricDuration are the same
//				assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
//				assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
//				assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
//			}
//		}
//	}
//
//
//	public void testTransposeOLD() {      
//		Tablature tablature = new Tablature(encodingTestpiece, false);
//		Transcription transcription = new Transcription();
////		transcription.setFile(midiTestpiece1);
//		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		transcription.setPiece(null);
//		transcription.makeNoteSequence();
//		transcription.initialiseVoiceLabels(null); 
//		transcription.initialiseDurationLabels(null);
//		if (transcription.checkChords(tablature) == false) {
//			throw new RuntimeException("ERROR: Chord error (see console).");
//		}
//		transcription.handleSNUsOLD(tablature, Type.FROM_FILE);
//		transcription.handleCourseCrossingsOLD(tablature, Type.FROM_FILE);
//		if (transcription.checkAlignment(tablature) == false) {
//			throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
//		}
//
//		// a. NoteSequence
//		// NB: expectedNotes cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
//		// with the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
//		List<Note> expectedNotes = new ArrayList<Note>();
//		// Chord 0
//		expectedNotes.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null));
//		// Chord 1
//		expectedNotes.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8), null));
//		// Chord 2
//		expectedNotes.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16), null));
//		// Chord 3
//		expectedNotes.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8), null));
//		expectedNotes.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null));
//		// Chord 4
//		expectedNotes.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8), null));
//		// Chord 5
//		expectedNotes.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null));
//		// Chord 6
//		expectedNotes.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8), null));
//		expectedNotes.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4), null));
//		// Chord 7
//		expectedNotes.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8), null));
//		expectedNotes.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null));
//		// Chord 8
//		expectedNotes.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2), null));
//		expectedNotes.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16), null));
//		// Chords 9-14
//		expectedNotes.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32), null));
//		expectedNotes.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32), null));
//		expectedNotes.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32), null));
//		expectedNotes.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null));
//		// Chord 14
//		expectedNotes.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4), null));
//		expectedNotes.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null));
//
//		// b. Piece
//		Piece expectedPiece = new Piece();
//		NotationSystem system = expectedPiece.createNotationSystem();
//		// Voice 0
//		NotationStaff staff0 = new NotationStaff(system); system.add(staff0);
//		NotationVoice voice0 = new NotationVoice(staff0); staff0.add(voice0);
//		voice0.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null));
////		NotationChord nc00 = new NotationChord(); nc00.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null)); voice0.add(nc00);
//		voice0.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null));
//		voice0.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null));
//		voice0.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null));
//		voice0.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null));
//		voice0.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null));
//		voice0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16), null));
//		voice0.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16), null));
//		voice0.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32), null));
//		voice0.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32), null));
//		voice0.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32), null));
//		voice0.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32), null));
//		voice0.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null));
//		voice0.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null));    
//		// Voice 1
//		NotationStaff staff1 = new NotationStaff(system); system.add(staff1); 
//		NotationVoice voice1 = new NotationVoice(staff1); staff1.add(voice1);
//		voice1.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4), null));
//		voice1.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8), null));
//		voice1.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 8), null));
//		voice1.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4), null));
//		voice1.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4), null));
//		voice1.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2), null));
//		voice1.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 2
//		NotationStaff staff2 = new NotationStaff(system); system.add(staff2); 
//		NotationVoice voice2 = new NotationVoice(staff2); staff2.add(voice2);
//		voice2.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4), null));
//		voice2.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4), null));
//		voice2.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), null));
//		voice2.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4), null));
//		voice2.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8), null));
//		voice2.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8), null));
//		voice2.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2), null));
//		voice2.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 3
//		NotationStaff staff3 = new NotationStaff(system); system.add(staff3); 
//		NotationVoice voice3 = new NotationVoice(staff3); staff3.add(voice3);
//		voice3.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4), null));
//		voice3.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16), null));
//		voice3.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16), null));
//		voice3.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4), null));
//		voice3.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2), null));
//		voice3.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2), null));
//		voice3.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4), null));
//		// Voice 4
//		NotationStaff staff4 = new NotationStaff(system); system.add(staff4);
//		NotationVoice voice4 = new NotationVoice(staff4); staff4.add(voice4);
//		voice4.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8), null));
//		voice4.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8), null));
//		voice4.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4), null));
//		voice4.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4), null));
//
//		transcription.transpose(tablature.getTranspositionInterval());
//		// a. noteSequence
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		List<Note> actualNotes = new ArrayList<Note>();
//		for (Note n : noteSeq) {
//			actualNotes.add(n);
//		}
//		// b. piece
//		Piece actualPiece = transcription.getScorePiece();
//
//		// Assert equality
//		// a. noteSequence
//		assertEquals(expectedNotes.size(), actualNotes.size());
//		for (int i = 0; i < expectedNotes.size(); i++) {
//			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//			// objects: therefore check that pitch, metricTime, and metricDuration are the same
//			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
//			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
//			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
//		} 
//		// b. piece
//		NotationSystem expectedNotationSystem = expectedPiece.getScore();
//		NotationSystem actualNotationSystem = actualPiece.getScore();
//		assertEquals(expectedNotationSystem.size(), actualNotationSystem.size());
//		for (int i = 0; i < expectedNotationSystem.size(); i++) {
//			// NotationStaff
//			NotationStaff expectedNotationStaff = expectedNotationSystem.get(i);
//			NotationStaff actualNotationStaff = actualNotationSystem.get(i);
//			assertEquals(expectedNotationStaff.size(), actualNotationStaff.size());
//			// NotationVoice
//			NotationVoice expectedNotationVoice = expectedNotationStaff.get(0);
//			NotationVoice actualNotationVoice = actualNotationStaff.get(0);
//			assertEquals(expectedNotationVoice.size(), actualNotationVoice.size());
//			for (int j = 0; j < expectedNotationVoice.size(); j++) {
//				// NotationChord
//				NotationChord expectedNotationChord = expectedNotationVoice.get(j);
//				NotationChord actualNotationChord = actualNotationVoice.get(j);
//				assertEquals(expectedNotationChord.size(), actualNotationChord.size());
//				for (int k = 0; k < expectedNotationChord.size(); k++) {
//					assertEquals(expectedNotationChord.get(k).getMidiPitch(), actualNotationChord.get(k).getMidiPitch());
//					assertEquals(expectedNotationChord.get(k).getMetricTime(), actualNotationChord.get(k).getMetricTime());
//					assertEquals(expectedNotationChord.get(k).getMetricDuration(), actualNotationChord.get(k).getMetricDuration());   	    
//				}
//			}
//		}
//	}
//
//
//	public void testHandleUnisonsOLD() {
//		Transcription t = new Transcription();
//		t.setPiece(MIDIImport.importMidiFile(midiTestpiece));
//		t.setName();
//		t.setNotes(null);
////		t.setNoteSequence();
//		t.initialiseVoiceLabels(null);
//		t.setMeterInfo();
//		t.setKeyInfo();
//
//		NoteSequence expected = getNoteSequence(t.getScorePiece(), "testpiece");
//		expected.swapNotes(12, 13);
//		
//		// a. NoteSequence
////		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
////		// with the same pitch and onset time randomly
////		List<Note> expectedNotes = new ArrayList<Note>();
////		for (Note n : t.getNoteSequence()) {
////			expectedNotes.add(n);
////		}
////		expectedNotes.set(12, Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
////		expectedNotes.set(13, Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));  
//		// b. Voice labels
//		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(t.getVoiceLabels());
//		expectedVoiceLabels.set(12, V_0);
//		expectedVoiceLabels.set(13, V_1);
//
//		t.handleUnisons(Type.FROM_FILE);
//		
//		NoteSequence actual = t.getNoteSequence();
////		List<Note> actualNotes = new ArrayList<Note>();
////		for (Note n : t.getNoteSequence()) {
////			actualNotes.add(n);
////		}
//		List<List<Double>> actualVoiceLabels = t.getVoiceLabels();
//		
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//
//		// a. NoteSequence
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
////		assertEquals(expectedNotes.size(), actualNotes.size());
////		for (int i = 0; i < expectedNotes.size(); i++) {
////			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
////			// objects: therefore check that pitch, metricTime, and metricDuration are the same
////			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
////			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
////			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
////		}
//		// b. Voice labels
//		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
//		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
//			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
//			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
//				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
//			}
//		}
//		assertEquals(expectedVoiceLabels, actualVoiceLabels);
//	}
//	
//	
//	public void testSetAndGetNoteSequence() {
//		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
//
//		// NB: Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
//		// the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
//		List<Note> expected = new ArrayList<Note>();
//		// Chord 0
//		expected.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null));
//		// Chord 1
//		expected.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16), null));
//		expected.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8), null));
//		// Chord 2
//		expected.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16), null));
//		// Chord 3
//		expected.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null));
//		// Chord 4
//		expected.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8), null));
//		// Chord 5
//		expected.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null));
//		// Chord 6
//		expected.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4), null));
//		// Chord 7
//		expected.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null));
//		// Chord 8
//		expected.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16), null));
//		// Chords 9-14
//		expected.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16), null));
//		expected.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null));
//		// Chord 14
//		expected.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null));
//
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		List<Note> actual = new ArrayList<Note>();
//		for (Note n : noteSeq) {
//			actual.add(n);
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//			// objects: therefore check that pitch, metricTime, and metricDuration are the same
//			assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
//			assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
//			assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
//		}
//	}
//
//
//	public void testSetAndGetNoteSequenceNonTab() {
//		Transcription transcription = new Transcription(midiTestpiece);
//		// Determine expected
//
//		// NB: Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
//		// the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
//		List<Note> expected = new ArrayList<Note>();
//		// Chord 0
//		expected.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4), null));
//		// Chord 1
//		expected.add(Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16), null));
//		expected.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4), null));
//		// Chord 2
//		expected.add(Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16), null));
//		// Chord 3
//		expected.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8), null));
//		// Chord 4
//		expected.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8), null));
//		// Chord 5
//		expected.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4), null));
//		// Chord 6
//		expected.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4), null));
//		// Chord 7
//		expected.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8), null));
//		expected.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8), null));
//		// Chord 8
//		expected.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2), null));
//		expected.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16), null));
//		// Chords 9-14
//		expected.add(Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16), null));
//		expected.add(Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32), null));
//		expected.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4), null));
//		// Chord 15
//		expected.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4), null));
//		expected.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4), null));
//	
//		// Calculate actual
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		List<Note> actual = new ArrayList<Note>();
//		for (Note n : noteSeq) {
//			actual.add(n);
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//			// objects: therefore check that pitch, metricTime, and metricDuration are the same
//			assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
//			assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
//			assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
//		}
//	}
//
//
//	public void testSetAndGetVoiceLabels() {
//		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
//
//		List<Double> voice0 = V_0;
//		List<Double> voice1 = V_1;
//		List<Double> voice2 = V_2;
//		List<Double> voice3 = V_3;
//		List<Double> voice4 = V_4;
//		List<Double> voice0And1 = Transcription.combineLabels(voice0, voice1);
//
//		List<List<Double>> expected = new ArrayList<List<Double>>();
//		// Chord 0
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chord 1
//		expected.add(voice3); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
//		// Chord 2
//		expected.add(voice3);
//		// Chord 3
//		expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice0And1); 
//		// Chord 4
//		expected.add(voice4);
//		// Chord 5
//		expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chord 6
//		expected.add(voice4); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
//		// Chord 7
//		expected.add(voice2); expected.add(voice0);  
//		// Chord 8
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chords 9-14
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		// Chord 15
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//
//		List<List<Double>> actual = transcription.getVoiceLabels();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size());
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}
//		assertEquals(expected, actual);
//	}
//	
//	
//	public void testSetAndGetVoiceLabelsNonTab() {
//		Transcription transcription = new Transcription(midiTestpiece);
//
//		List<Double> voice0 = V_0;
//		List<Double> voice1 = V_1;
//		List<Double> voice2 = V_2;
//		List<Double> voice3 = V_3;
//		List<Double> voice4 = V_4;
//
//		List<List<Double>> expected = new ArrayList<List<Double>>();
//		// Chord 0
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chord 1
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chord 2
//		expected.add(voice3);
//		// Chord 3
//		expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
//		// Chord 4
//		expected.add(voice4);
//		// Chord 5
//		expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chord 6
//		expected.add(voice4); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
//		// Chord 7
//		expected.add(voice2); expected.add(voice0);  
//		// Chord 8
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//		// Chords 9-14
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		expected.add(voice0);
//		// Chord 15
//		expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
//
//		List<List<Double>> actual = transcription.getVoiceLabels();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size());
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//			}
//		}
//		assertEquals(expected, actual);
//	}
//
//
//	public void testSetAndGetDurationLabels() {
//		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
//
//		List<Double> quarterAndEighth = new ArrayList<Double>(QUARTER);
//		quarterAndEighth.set(3, 1.0);
//
//		List<List<Double>> expected = new ArrayList<List<Double>>();
//		// Chord 0
//		expected.add(QUARTER); expected.add(QUARTER); expected.add(QUARTER);
//		expected.add(QUARTER);
//		// Chord 1
//		expected.add(DOTTED_EIGHTH); expected.add(QUARTER); expected.add(QUARTER);
//		expected.add(EIGHTH);
//		// Chord 2
//		expected.add(SIXTEENTH);
//		// Chord 3
//		expected.add(EIGHTH); expected.add(QUARTER); expected.add(QUARTER); 
//		expected.add(quarterAndEighth);
//		// Chord 4
//		expected.add(EIGHTH);
//		// Chord 5
//		expected.add(QUARTER); expected.add(HALF); expected.add(QUARTER); 
//		expected.add(QUARTER); expected.add(QUARTER);
//		// Chord 6
//		expected.add(QUARTER); expected.add(EIGHTH); expected.add(EIGHTH); 
//		expected.add(QUARTER);
//		// Chord 7
//		expected.add(EIGHTH); expected.add(EIGHTH);
//		// Chord 8
//		expected.add(HALF); expected.add(HALF); expected.add(HALF); 
//		expected.add(SIXTEENTH);
//		// Chords 9-14
//		expected.add(SIXTEENTH); 
//		expected.add(THIRTYSECOND); 
//		expected.add(THIRTYSECOND);
//		expected.add(THIRTYSECOND); 
//		expected.add(THIRTYSECOND); 
//		expected.add(QUARTER);
//		// Chord 15
//		expected.add(QUARTER); expected.add(QUARTER); expected.add(QUARTER);
//		expected.add(QUARTER);
//
//		List<List<Double>> actual = transcription.getDurationLabels();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
////				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
////					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
////				}
//			}
//		}
//		assertEquals(expected, actual);
//	}
//
//
////	public void testGetNumberOfCoDsInChord() {    
////	Tablature tablature = new Tablature(encodingTestpiece1, true);
////	Transcription transcription = new Transcription();
//////transcription.setFile(midiTestpiece1);
////transcription.setPiece(MidiImport.importMidiFiles(midiTestpiece1));
//////transcription.setPiece(null);
////transcription.initialiseNoteSequence();
////transcription.initialiseVoiceLabels(); 
////transcription.initialiseDurationLabels();
////if (transcription.checkChords(tablature) == false) {
//// 	throw new RuntimeException("ERROR: Chord error (see console).");
////}
////
////// Determine expected
////List<Integer> expected = Arrays.asList(new Integer[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
////
////// Calculate actual
////List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
////List<Integer> actual = new ArrayList<Integer>();
////for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
////	actual.add(transcription.getNumberOfCoDsInChord(tablatureChords, i));
////}
////
////// Assert equality
////assertEquals(expected.size(), actual.size());
////	for (int i = 0; i < expected.size(); i++) {
////		assertEquals(expected.get(i), actual.get(i));		
////	}
////	assertEquals(expected, actual);
////}
//
//
////public void testGetEqualDurationUnisonsInfoOLD() {
////	Transcription transcription = new Transcription(midiTestpiece1, null);
////	
////	// Adapt transcription so that all unisons become EDUs
////	// NB: listEqualDurationUnisons() gets the information from the NoteSequence only; therefore, the 
////	// basicNoteProperties need not be adapted
////	NoteSequence noteSeq = transcription.getNoteSequence();
////	Note n13 = noteSeq.getNoteAt(13);
////  noteSeq.replaceNoteAt(13, Transcription.createNote(n13.getMidiPitch(), n13.getMetricTime(), new Rational(1, 4)));
////  // Swap so that the lower voice (now at index 13) gets the lower index, as must be the case with EDUnotes 
////  noteSeq.swapNotes(12, 13);
////  Note n16 = noteSeq.getNoteAt(16);
////  noteSeq.replaceNoteAt(16, Transcription.createNote(n16.getMidiPitch(), n16.getMetricTime(), new Rational(1, 4)));
////  
////  // Determine expected
////  List<List<Double>> expected = new ArrayList<List<Double>>();
////  for (int i = 0; i < noteSeq.size(); i++) {
////  	expected.add(null);
////  }
////  expected.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////  expected.set(13, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////  expected.set(16, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
////  expected.set(17, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
////  	  
////  // Calculate actual
////  List<List<Double>> actual = transcription.getEqualDurationUnisonsInfoOLD();
////  
////  // Assert equality
////  assertEquals(expected.size(), actual.size());
////  for (int i = 0; i < expected.size(); i++) {
////  	if (expected.get(i) == null) {
////  		assertEquals(expected.get(i), actual.get(i));
////  	}
////  	else {
////		    assertEquals(expected.get(i).size(), actual.get(i).size());
////		    for (int j = 0; j < expected.get(i).size(); j++) {
////		    	assertEquals(expected.get(i).get(j), actual.get(i).get(j));
////		    }
////		}
////  }
////}
//
//
////public void testGetNumberOfUnisonsInChord() {
////	Transcription transcription = new Transcription();
//////transcription.setFile(midiTestpiece1);
////transcription.setPiece(MidiImport.importMidiFiles(midiTestpiece1));
//////transcription.setPiece(null);
////transcription.initialiseNoteSequence();
////transcription.initialiseVoiceLabels();
////transcription.setMeterInfo(midiTestpiece1);
////	
////	// Determine expected
////	List<Integer> expected = Arrays.asList(new Integer[]{0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
////	
////	// Calculate actual
////	List<Integer> actual = new ArrayList<Integer>();
////	for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
////		actual.add(transcription.getNumberOfUnisonsInChord(i));
////	}
////	
////	// Assert equality
////	assertEquals(expected.size(), actual.size());
////	for (int i = 0; i < expected.size(); i++) {
////		assertEquals(expected.get(i), actual.get(i));
////	}
////}
//
//
//	public void testSetAndGetVoicesCoDNotes() {
//		Tablature tablature1 = new Tablature(encodingTestpiece, true);
//		Transcription transcription1 = new Transcription(midiTestpiece, encodingTestpiece);
//		
////		File encoding2 = 
////			new File(Runner.encodingsPath + DatasetID.INT_4vv.getName() + "/4vv/phalese-1563_12-las_on.tbp");
////		File midi2 = 
////			new File(Runner.midiPath + DatasetID.INT_4vv.getName() + "/4vv/phalese-1563_12-las_on.mid");
//		File encoding2 = 
//			new File(MEIExport.rootDir + "data/annotated/encodings/" + "thesis-int" + "/4vv/phalese-1563_12-las_on.tbp");
//		File midi2 = 
//			new File(MEIExport.rootDir + "data/annotated/MIDI/" + "thesis-int" + "/4vv/phalese-1563_12-las_on.mid");
//		
//		Tablature tablature2 = new Tablature(encoding2, true);
//		Transcription transcription2 = new Transcription(midi2, encoding2);
//				
//		List<Integer[]> expected = new ArrayList<Integer[]>();
//		// a. For a piece with one CoD
//		List<Integer[]> expected1 = new ArrayList<Integer[]>();
//		for (int i = 0; i < tablature1.getBasicTabSymbolProperties().length; i++) {
//			expected1.add(null);
//		}
//		// CoD at metric position 2 1/4
//		expected1.set(12, new Integer[]{0, 1});
//		expected.addAll(expected1);
//		
//		// b. For a piece with multiple CoDs
//		List<Integer[]> expected2 = new ArrayList<Integer[]>();
//		for (int i = 0; i < tablature2.getBasicTabSymbolProperties().length; i++) {
//			expected2.add(null);
//		}
//		// CoD at metric position 7
//		expected2.set(86, new Integer[]{3, 2});
//		// CoD at metric position 7 1/4
//		expected2.set(91, new Integer[]{1, 2});
//		// CoD at metric position 14 3/4
//		expected2.set(256, new Integer[]{3, 2});
//		// CoD at metric position 17
//		expected2.set(301, new Integer[]{3, 2});
//		// CoD at metric position 17 1/4
//		expected2.set(306, new Integer[]{1, 2});
//		// CoD at metric position 21 1/2
//		expected2.set(391, new Integer[]{3, 2});
//		// CoD at metric position 23 1/2
//		expected2.set(422, new Integer[]{3, 2});
//		// CoD at metric position 29 1/2
//		expected2.set(524, new Integer[]{1, 0});
//		// CoD at metric position 33 3/4
//		expected2.set(579, new Integer[]{2, 0});
//		// CoD at metric position 35 1/4
//		expected2.set(600, new Integer[]{2, 1});
//		// CoD at metric position 42 1/4
//		expected2.set(716, new Integer[]{2, 1});
//		expected.addAll(expected2);
//
//		List<Integer[]> actual = new ArrayList<Integer[]>();
//		actual.addAll(transcription1.getVoicesSNU());
//		actual.addAll(transcription2.getVoicesSNU());
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) == null) {
//				assertEquals(expected.get(i), actual.get(i));
//			}
//			else {
//				assertEquals(expected.get(i).length, actual.get(i).length);
//				for (int j = 0; j < expected.get(i).length; j++) {
//					assertEquals(expected.get(i)[j], actual.get(i)[j]);
//				}
//			}
//		}
//	}
//
//
//	public void testSetAndGetEqualDurationUnisonsInfo() {
//		Transcription transcription = new Transcription();
////		transcription.setFile(midiTestpiece1);
//		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece));
////		transcription.setPiece(null);
//		transcription.makeNoteSequence();
//		// The voice labels must be initialised before the NoteSeqeunce is adapted, as initialiseVoiceLabels() needs
//		// the actual Note objects from the NoteSequence and not the adaptations made below
//		transcription.initialiseVoiceLabels(null); 
////		transcription.setMeterInfo(midiTestpiece1);
//		transcription.setMeterInfo();
//		// Before calling handleUnisons(): adapt transcription so that all unisons become EDUs
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		// Give all unison notes a duration of a quarter
//		// NB: Before handleUnisons() is called, all unison notes have been added to noteSeq with the one in the 
//		// lower voice first. That means that the notes at indices 12 and 16 need to be adapted
//		Note n12 = noteSeq.getNoteAt(12);
//		noteSeq.replaceNoteAt(12, Transcription.createNote(n12.getMidiPitch(), n12.getMetricTime(), new Rational(1, 4), null));
//		Note n16 = noteSeq.getNoteAt(16);
//		noteSeq.replaceNoteAt(16, Transcription.createNote(n16.getMidiPitch(), n16.getMetricTime(), new Rational(1, 4), null));  
//		transcription.handleUnisons(Type.FROM_FILE);
//		transcription.setBasicNoteProperties();
//
//		List<Integer[]> expected = new ArrayList<Integer[]>();
//		for (int i = 0; i < noteSeq.size(); i++) {
//			expected.add(null);
//		}
////		expected.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////		expected.set(13, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
////		expected.set(16, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
////		expected.set(17, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
//		expected.set(12, new Integer[]{1, 0, 13});
//		expected.set(13, new Integer[]{1, 0, 12});
//		expected.set(16, new Integer[]{3, 2, 17});
//		expected.set(17, new Integer[]{3, 2, 16});
//
//		List<Integer[]> actual = transcription.getVoicesEDU();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) == null) {
//				assertEquals(expected.get(i), actual.get(i));
//			}
////			else {
////				assertEquals(expected.get(i).size(), actual.get(i).size());
////				for (int j = 0; j < expected.get(i).size(); j++) {
////					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
////				}
////			}
//			else {
//				assertEquals(expected.get(i).length, actual.get(i).length);
//				for (int j = 0; j < expected.get(i).length; j++) {
//					assertEquals(expected.get(i)[j], actual.get(i)[j]);
//				}
//			}
//		}
//	}
//
//
//	public void testGetAndSetChords(){
////		Tablature tablature = new Tablature(encodingTestpiece1, true);
//		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
//
//		List<List<Note>> expected = new ArrayList<List<Note>>();
//		// Chord 0
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4), null), 
//			Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4), null),
//			Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4), null),
//			Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4), null)}));
//		// Chord 1      
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16), null),
//			Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4), null),
//			Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4), null),
//			Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8), null)}));
//		// Chord 2
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16), null)}));
//		// Chord 3
//		expected.add(Arrays.asList(new Note[]{	
//			Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8), null),
//			Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4), null),
//			Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4), null),
//			Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4), null)}));
//		// Chord 4
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8), null)}));
//		// Chord 5
//		expected.add(Arrays.asList(new Note[]{	
//			Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4), null),
//			Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2), null),
//			Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4), null),
//			Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4), null),
//			Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4), null)}));
//		// Chord 6
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4), null),
//			Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8), null),
//			Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8), null),
//			Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4), null)}));
//		// Chord 7
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8), null),
//			Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8), null)}));	
//		// Chord 8
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2), null),
//			Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2), null),
//			Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2), null),
//			Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16), null)}));
//		// Chords 9-14
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16), null)}));
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4), null)}));
//		// Chord 15
//		expected.add(Arrays.asList(new Note[]{
//			Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4), null),
//			Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4), null),
//			Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4), null),
//			Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4), null)}));
//
//		List<List<Note>> actual = transcription.getChords();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//				// objects: therefore check that pitch, metricTime, and metricDuration are the same
//				assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
//				assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
//				assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
//			}
//		}
//	}
//
//
//	public void testGetAndSetTranscriptionChordsNonTab(){
//		Transcription transcription = new Transcription(midiTestpiece);
//
//		List<List<Note>> expected = new ArrayList<List<Note>>();
//		// Chord 0
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4), null),
//		Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4), null),
//		Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4), null),
//		Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4), null)}));  
//		// Chord 1
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16), null),
//		Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4), null),
//		Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8), null),
//		Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4), null)}));
//		// Chord 2
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16), null)}));
//		// Chord 3
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8), null),
//		Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4), null),
//		Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4), null),
//		Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4), null),
//		Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8), null)}));
//		// Chord 4
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8), null)}));
//		// Chord 5
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4), null),
//		Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2), null),
//		Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4), null),
//		Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4), null),
//		Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4), null)}));
//		// Chord 6
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4), null),
//		Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8), null),
//		Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8), null),
//		Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4), null)}));
//		// Chord 7
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8), null),
//		Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8), null)}));
//		// Chord 8
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2), null),
//		Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2), null),
//		Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2), null),
//		Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16), null)}));
//		// Chords 9-14
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16), null)}));
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32), null)}));
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4), null)}));
//		// Chord 15
//		expected.add(Arrays.asList(new Note[]{
//		Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4), null),
//		Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4), null),
//		Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4), null),
//		Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4), null)}));
//
//		List<List<Note>> actual = transcription.getChords();
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size()); 
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//				// objects: therefore check that pitch, metricTime, and metricDuration are the same
//				assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
//				assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
//				assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
//			}
//		}
//	}
//
//
//	public void testContainsCoD() {
//		List<List<Double>> voiceLabels = new ArrayList<List<Double>>();
//		voiceLabels.add(Transcription.combineLabels(V_0, V_1));
//		voiceLabels.add(V_0);
//		voiceLabels.add(Transcription.combineLabels(V_0, V_2));
//		voiceLabels.add(V_2);
//		
//		List<Boolean> expected = Arrays.asList(new Boolean[]{true, false, true, false});
//		
//		List<Boolean> actual = new ArrayList<Boolean>();
//		for (List<Double> l : voiceLabels) {
//			actual.add(Transcription.containsCoD(l));
//		}
//		
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}
//
//
//	private JFrame visualise(Transcription t, String windowTitle) {
//		ScoreEditor scoreEditor = new ScoreEditor(t.getScorePiece().getScore());
//		scoreEditor.setModus(Mode.SELECT_AND_EDIT);
//
//		JFrame fullScoreFrame = new JFrame(windowTitle);
//		fullScoreFrame.add(new JScrollPane(scoreEditor));
//		fullScoreFrame.setSize(800, 600);
////		fullScoreFrame.setSize(1200, 1200);
//	    fullScoreFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//	    fullScoreFrame.setVisible(true);
//	    scoreEditor.setSize(20000, 20000);
//	    return fullScoreFrame;
//	}
//
//	
//	public void testAddNoteOLD() {
//		Transcription t = new Transcription(midiTestpiece, encodingTestpiece);
////			new Transcription(new File(MEIExport.rootDir + "data/annotated/MIDI/test/" + "test_add_note.mid"),	
////			new File(MEIExport.rootDir + "data/annotated/encodings/test/" + "test_add_note.tbp"));
//
//		// A Note is added to voice 1 in event 1. Assert that the corresponding NotationChord contains only one
//		// Note (d') before adding 
//		int voiceToAddNoteTo = 1;
//		Rational onsetTime = new Rational(1, 2);
//		NotationSystem system = t.getScorePiece().getScore();
//		NotationStaff staff = system.get(voiceToAddNoteTo);
//		NotationVoice voice = staff.get(0);
//		int chordNumber = voice.find(onsetTime);
//		NotationChord chord = voice.get(chordNumber);
//		assertEquals(1, chord.size());
//		assertEquals(62, chord.get(0).getMidiPitch());
//
//		// Make a new Note (b)
//		Note note = ScorePiece.createNote(59, onsetTime, new Rational(1, 2), -1, null);
//
//		// Add the Note to voice 1 at onsetTime
//		t.addNote(note, voiceToAddNoteTo, onsetTime);
//
//		// Assert that the NotationChord contains two Notes (b and d') after adding
//		assertEquals(2, chord.size());
//		assertEquals(59, chord.getLowestNote().getMidiPitch());
//		assertEquals(62, chord.getUppermostNote().getMidiPitch());
//
//		// Visualise
////		transcription.setPieceName("test_add_note");
//		JFrame transcriptionFrame = visualise(t, "test_add_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - b, d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);     
//	}
//
//
//	public void testAddNoteNonTab() {
////		Transcription transcription = 
////			new Transcription(new File(Runner.midiPathTest + "test_add_note.mid"), null); 
//		Transcription transcription = 
//			new Transcription(new File(MEIExport.rootDir + "data/annotated/MIDI/test/" + "test_add_note.mid")); 
//
//		// A Note is added to voice 1 in event 1. Assert that the corresponding NotationChord contains only one
//		// Note (d') before adding 
//		int voiceToAddNoteTo = 1;
//		Rational onsetTime = new Rational(1, 2);
//		NotationSystem system = transcription.getScorePiece().getScore();
//		NotationStaff staff = system.get(voiceToAddNoteTo);
//		NotationVoice voice = staff.get(0);
//		int chordNumber = voice.find(onsetTime);
//		NotationChord chord = voice.get(chordNumber);
//		assertEquals(1, chord.size());
//		assertEquals(62, chord.get(0).getMidiPitch());
//
//		// Make a new Note (b)
//		Note note = ScorePiece.createNote(59, onsetTime, new Rational(1, 2), -1, null);
//
//		// Add the Note to voice 1 at onsetTime
//		transcription.addNote(note, voiceToAddNoteTo, onsetTime);
//
//		// Assert that the NotationChord contains two Notes (b and d') after adding
//		assertEquals(2, chord.size());
//		assertEquals(59, chord.getLowestNote().getMidiPitch());
//		assertEquals(62, chord.getUppermostNote().getMidiPitch());
//
//		// Visualise
//		JFrame transcriptionFrame = visualise(transcription, "test_add_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - b, d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);     
//	}
//
//
//	public void testRemoveNoteOLD() {
////		Transcription transcription = 
////			new Transcription(new File(Runner.midiPathTest + "test_remove_note.mid"), 
////			new File(Runner.encodingsPathTest + "test_remove_note.tbp"));
//		Transcription transcription = 
//			new Transcription(new File(MEIExport.rootDir + "data/annotated/MIDI/test/" + "test_remove_note.mid"), 
//			new File(MEIExport.rootDir + "data/annotated/encodings/test/" + "test_remove_note.tbp"));
//
//		// Two Notes are removed from voice 2 in event 1. Assert that the corresponding NotationChord contains three
//		// Notes (e, b, g) before removing 
//		int voiceNumber = 2;
//		Rational onsetTime = new Rational(1, 2);
//		NotationSystem system = transcription.getScorePiece().getScore();
//		NotationStaff staff = system.get(voiceNumber);
//		NotationVoice voice = staff.get(0);
//		int chordNumber = voice.find(onsetTime);
//		NotationChord chord = voice.get(chordNumber);
//		assertEquals(3, chord.size());
//
//		// Remove the Notes e and b from voice 2 at onsetTime
//		transcription.removeNote(52, voiceNumber, onsetTime);
//		transcription.removeNote(59, voiceNumber, onsetTime);
//
//		// Assert that the NotationChord contains only one Note (g) after removing
//		assertEquals(1, chord.size());
//		assertEquals(55, chord.get(0).getMidiPitch());
//
//		// Visualise
//		JFrame transcriptionFrame = visualise(transcription, "test_remove_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);     
//	}
//
//
//	public void testRemoveNoteNonTab() {
////		Transcription transcription = 
////			new Transcription(new File(Runner.midiPathTest + "test_remove_note.mid"),	null);
//		Transcription transcription = 
//			new Transcription(new File(MEIExport.rootDir + "data/annotated/MIDI/test/" + "test_remove_note.mid"));
//
//		// Two Notes are removed from voice 2 in event 1. Assert that the corresponding NotationChord contains three
//		// Notes (e, b, g) before removing 
//		int voiceNumber = 2;
//		Rational onsetTime = new Rational(1, 2);
//		NotationSystem system = transcription.getScorePiece().getScore();
//		NotationStaff staff = system.get(voiceNumber);
//		NotationVoice voice = staff.get(0);
//		int chordNumber = voice.find(onsetTime);
//		NotationChord chord = voice.get(chordNumber);
//		assertEquals(3, chord.size());
//
//		// Remove the Notes e and b from voice 2 at onsetTime
//		transcription.removeNote(52, voiceNumber, onsetTime);
//		transcription.removeNote(59, voiceNumber, onsetTime);
//
//		// Assert that the NotationChord contains only one Note (g) after removing
//		assertEquals(1, chord.size());
//		assertEquals(55, chord.get(0).getMidiPitch());
//
//		// Visualise
//		JFrame transcriptionFrame = visualise(transcription, "test_remove_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);     
//	}
//
//
//	public void testCreatePiece() {
//		Tablature tablature = new Tablature(encodingTestpiece, true);
//		Integer[][] btp = tablature.getBasicTabSymbolProperties();
//		Integer[][] bnp = null;
//		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece); // not modelling duration
//		Transcription transcription2 = new Transcription(midiTestpiece, encodingTestpiece); // modelling duration
//		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
//		List<List<Double>> durationLabels = transcription.getDurationLabels();
//
//		List<Piece> expected = new ArrayList<Piece>();
//		// a. Not modelling duration
//		Piece expectedNonDur = transcription.getScorePiece();
//
//		// Where necessary, adapt durations to their minimum duration
//		// Voice 0
//		NotationVoice v0 = expectedNonDur.getScore().get(0).get(0);
//		// Note 1
//		v0.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(72), new Rational(4, 4), new Rational(3, 16)));
//		// Note 2
//		v0.get(2).get(0).setScoreNote(new ScoreNote(new ScorePitch(65), new Rational(5, 4), new Rational(1, 8)));
//		// Voice 1
//		NotationVoice v1 = expectedNonDur.getScore().get(1).get(0);
//		// Note 1
//		v1.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(69), new Rational(4, 4), new Rational(3, 16)));
//		// Note 4
//		v1.get(4).get(0).setScoreNote(new ScoreNote(new ScorePitch(69), new Rational(7, 4), new Rational(1, 8)));
//		// Note 5
//		v1.get(5).get(0).setScoreNote(new ScoreNote(new ScorePitch(64), new Rational(8, 4), new Rational(1, 16)));
//		// Voice 2
//		NotationVoice v2 = expectedNonDur.getScore().get(2).get(0);
//		// Note 1
//		v2.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(4, 4), new Rational(3, 16)));
//		// Note 2 
//		v2.get(2).get(0).setScoreNote(new ScoreNote(new ScorePitch(59), new Rational(5, 4), new Rational(1, 8)));
//		// Note 6
//		v2.get(6).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(8, 4), new Rational(1, 16)));
//		// Voice 3
//		NotationVoice v3 = expectedNonDur.getScore().get(3).get(0);
//		// Note 3 
//		v3.get(3).get(0).setScoreNote(new ScoreNote(new ScorePitch(50), new Rational(5, 4), new Rational(1, 8)));
//		// Note 4
//		v3.get(4).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(6, 4), new Rational(1, 4)));
//		// Note 5
//		v3.get(5).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(8, 4), new Rational(1, 16)));
//		// Voice 4
//		NotationVoice v4 = expectedNonDur.getScore().get(4).get(0);
//		// Note 3
//		v4.get(3).get(0).setScoreNote(new ScoreNote(new ScorePitch(45), new Rational(7, 4), new Rational(1, 8)));
//		
//		expected.add(expectedNonDur);
//		
//		// b. Modelling duration
//		Piece expectedDur = transcription2.getScorePiece();
//		// Adapt the CoD at index 12 (the third note in voice 1) so that both notes have the same duration 
//		// (necessary because currently CoDs can only have one duration)
//		ScoreNote adaptedScoreNote = new ScoreNote(new ScorePitch(65), new Rational(5, 4), new Rational(1, 4));
//		expectedDur.getScore().get(1).get(0).get(2).get(0).setScoreNote(adaptedScoreNote);
//		// Also adapt durationLabels
//		durationLabels.set(12, QUARTER); // trp dur
////		durationLabels.set(12, Transcription.createDurationLabel(8*3)); // trp dur
////		durationLabels.set(12, Transcription.createDurationLabel(8));
//
//		expected.add(expectedDur);
//		
//		List<Piece> actual = new ArrayList<Piece>();
//		// a. Not modelling duration
//		actual.add(Transcription.createPiece(btp, bnp, voiceLabels, null, 5, 
//				expectedNonDur.getMetricalTimeLine(), expectedNonDur.getHarmonyTrack(), ""));
//		// b. Modelling duration
//		actual.add(Transcription.createPiece(btp, bnp, voiceLabels, durationLabels, 5,
//			expectedDur.getMetricalTimeLine(), expectedDur.getHarmonyTrack(), ""));
//
//		assertEquals(expected.size(), actual.size());
//		for (int num = 0; num < expected.size(); num++) {
//			NotationSystem systemExpected = expected.get(num).getScore();
//			NotationSystem systemActual = actual.get(num).getScore();
//			assertEquals(systemExpected.size(), systemActual.size());
//			// For each NotationStaff at index i
//	 		for (int i = 0; i < systemExpected.size(); i++) {
//				assertEquals(systemExpected.get(i).size(), systemActual.get(i).size());
//				// For each NotationVoice at index j
//				for (int j = 0; j < systemExpected.get(i).size(); j++) {
//					assertEquals(systemExpected.get(i).get(j).size(), systemActual.get(i).get(j).size());
//					// For each NotationChord at index k
//					for (int k = 0; k < systemExpected.get(i).get(j).size(); k++) {	
//						assertEquals(systemExpected.get(i).get(j).get(k).size(), systemActual.get(i).get(j).get(k).size());
//						// For each Note at index l
//						for (int l = 0; l < systemExpected.get(i).get(j).get(k).size(); l++) {
////							assertEquals(pieceExpected.getScore().get(i).get(j).get(k).get(l), pieceActual.getScore().get(i).get(j).get(k).get(l));
//							// OR if assertEquals(expected.get(i).get(j), actual.get(i).get(j).get(k) does not work because the Notes 
//							// are not the same objects: check that pitch, metricTime, and metricDuration are the same
//							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMidiPitch(), 
//								systemActual.get(i).get(j).get(k).get(l).getMidiPitch());
//							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricTime(), 
//								systemActual.get(i).get(j).get(k).get(l).getMetricTime());
//							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricDuration(), 
//								systemActual.get(i).get(j).get(k).get(l).getMetricDuration());		
//						}
//					}		
//				}
//			}
//		}
//	}
//
//
//	public void testCreatePieceNonTab() {
//		Transcription transcription = new Transcription(midiTestpiece);
//		Integer[][] btp = null;
//		Integer[][] bnp = transcription.getBasicNoteProperties();
//		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
//		List<List<Double>> durationLabels = null;
//
//		Piece expected = transcription.getScorePiece();
//
//		Piece actual = Transcription.createPiece(btp, bnp, voiceLabels, durationLabels, 5,
//			expected.getMetricalTimeLine(), expected.getHarmonyTrack(), "");
//
//		NotationSystem systemExpected = expected.getScore();
//		NotationSystem systemActual = actual.getScore();
//		assertEquals(systemExpected.size(), systemActual.size());
//		// For each NotationStaff at index i
// 		for (int i = 0; i < systemExpected.size(); i++) {
//			assertEquals(systemExpected.get(i).size(), systemActual.get(i).size());
//			// For each NotationVoice at index j
//			for (int j = 0; j < systemExpected.get(i).size(); j++) {
//				assertEquals(systemExpected.get(i).get(j).size(), systemActual.get(i).get(j).size());
//				// For each NotationChord at index k
//				for (int k = 0; k < systemExpected.get(i).get(j).size(); k++) {	
//					assertEquals(systemExpected.get(i).get(j).get(k).size(), systemActual.get(i).get(j).get(k).size());
//					// For each Note at index l
//					for (int l = 0; l < systemExpected.get(i).get(j).get(k).size(); l++) {
////						assertEquals(pieceExpected.getScore().get(i).get(j).get(k).get(l), pieceActual.getScore().get(i).get(j).get(k).get(l));
//						// OR if assertEquals(expected.get(i).get(j), actual.get(i).get(j).get(k) does not work because the Notes 
//						// are not the same objects: check that pitch, metricTime, and metricDuration are the same
//						assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMidiPitch(), 
//								systemActual.get(i).get(j).get(k).get(l).getMidiPitch());
//		     	  assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricTime(), 
//		     	  		systemActual.get(i).get(j).get(k).get(l).getMetricTime());
//		    	  assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricDuration(), 
//		    	  		systemActual.get(i).get(j).get(k).get(l).getMetricDuration());		
//					}
//				}		
//			}
//		}	
//	}

}
