package representations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.NoteSequence;
import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import imports.MIDIImport;
import junit.framework.TestCase;
import tbp.TabSymbol;
import utility.DataConverter;

public class TranscriptionTest extends TestCase {

	private File encodingTestpiece1;
	private File midiTestpiece1;
	private File midiTestGetMeterInfo;
	private File midiTestGetMeterInfoNoAna;

	public TranscriptionTest(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
		super.setUp();
//		Runner.setPathsToCodeAndData(UI.getRootDir(), false);
////		testPaths = new String[]{Runner.encodingsPathTest, Runner.midiPathTest, Runner.midiPathTest};
//		encodingTestpiece1 = new File(Runner.encodingsPathTest + "testpiece.tbp");
//		midiTestpiece1 = new File(Runner.midiPathTest + "testpiece.mid");
//		midiTestGetMeterInfo = new File(Runner.midiPathTest + "test_get_meter_key_info.mid");
//		midiTestGetMeterInfoNoAna = new File(Runner.midiPathTest + "test_get_meter_key_info_no_anacrusis.mid");
		
		String s = MEIExport.rootDir;
		encodingTestpiece1 = new File(s + "data/encodings/test/" + "testpiece.tbp");
		midiTestpiece1 = new File(s + "data/MIDI/test/" + "testpiece.mid");
		midiTestGetMeterInfo = new File(s + "data/MIDI/test/" + "test_get_meter_key_info.mid");
		midiTestGetMeterInfoNoAna = new File(s + "data/MIDI/test/" + "test_get_meter_key_info_no_anacrusis.mid");
	}


	protected void tearDown() throws Exception {
		super.tearDown();
	}


	private List<List<Integer>> getVoiceAssignments() {
		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();
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
		return voiceAssignments;
	}


	private List<List<Integer>> getVoiceAssignmentsNonTab() {
		List<List<Integer>> voiceAssignments = new ArrayList<List<Integer>>();
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
		return voiceAssignments;
	}


	public void testReverse() {
		// a. Tablature case
		Tablature tab = new Tablature(encodingTestpiece1, false);
		Transcription t = new Transcription(midiTestpiece1, encodingTestpiece1);
		Transcription revTrans = Transcription.reverse(t, tab);
		List<List<Note>> expected = new ArrayList<List<Note>>();
		// Voice 0
		List<Note> expected0 = new ArrayList<Note>();
		expected0.add(Transcription.createNote(67, new Rational(0, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(67, new Rational(2, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(66, new Rational(3, 4), new Rational(1, 32)));
		expected0.add(Transcription.createNote(64, new Rational(25, 32), new Rational(1, 32)));
		expected0.add(Transcription.createNote(66, new Rational(13, 16), new Rational(1, 32)));
		expected0.add(Transcription.createNote(67, new Rational(27, 32), new Rational(1, 32)));
		expected0.add(Transcription.createNote(66, new Rational(7, 8), new Rational(1, 16)));
		expected0.add(Transcription.createNote(67, new Rational(15, 16), new Rational(1, 16)));
		expected0.add(Transcription.createNote(66, new Rational(4, 4), new Rational(1, 8)));
		expected0.add(Transcription.createNote(62, new Rational(9, 8), new Rational(1, 8)));
		expected0.add(Transcription.createNote(67, new Rational(5, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(63, new Rational(13, 8), new Rational(1, 8)));
		expected0.add(Transcription.createNote(70, new Rational(29, 16), new Rational(3, 16)));
		expected0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4)));
		// Voice 1
		List<Note> expected1 = new ArrayList<Note>();
		expected1.add(Transcription.createNote(62, new Rational(0, 4), new Rational(1, 4)));
		expected1.add(Transcription.createNote(62, new Rational(15, 16), new Rational(1, 16)));
		expected1.add(Transcription.createNote(67, new Rational(9, 8), new Rational(1, 8)));
		expected1.add(Transcription.createNote(58, new Rational(5, 4), new Rational(1, 4)));
		expected1.add(Transcription.createNote(63, new Rational(13, 8), new Rational(1, 8)));
		expected1.add(Transcription.createNote(67, new Rational(29, 16), new Rational(3, 16)));
		expected1.add(Transcription.createNote(63, new Rational(8, 4), new Rational(1, 4)));
		// Voice 2
		List<Note> expected2 = new ArrayList<Note>();
		expected2.add(Transcription.createNote(55, new Rational(0, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(55, new Rational(15, 16), new Rational(1, 16)));
		expected2.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 8)));
		expected2.add(Transcription.createNote(58, new Rational(9, 8), new Rational(1, 8)));
		expected2.add(Transcription.createNote(55, new Rational(5, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(57, new Rational(13, 8), new Rational(1, 8)));
		expected2.add(Transcription.createNote(55, new Rational(29, 16), new Rational(3, 16)));
		expected2.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 4)));
		// Voice 3
		List<Note> expected3 = new ArrayList<Note>();
		expected3.add(Transcription.createNote(43, new Rational(0, 4), new Rational(1, 4)));
		expected3.add(Transcription.createNote(43, new Rational(15, 16), new Rational(1, 16)));
		expected3.add(Transcription.createNote(55, new Rational(5, 4), new Rational(1, 4)));
		expected3.add(Transcription.createNote(48, new Rational(13, 8), new Rational(1, 8)));
		expected3.add(Transcription.createNote(46, new Rational(7, 4), new Rational(1, 16)));
		expected3.add(Transcription.createNote(43, new Rational(29, 16), new Rational(3, 16)));
		expected3.add(Transcription.createNote(48, new Rational(8, 4), new Rational(1, 4)));
		// Voice 4
		List<Note> expected4 = new ArrayList<Note>();
		expected4.add(Transcription.createNote(43, new Rational(9, 8), new Rational(1, 8)));
		expected4.add(Transcription.createNote(43, new Rational(5, 4), new Rational(1, 4)));
		expected4.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 8)));
		expected4.add(Transcription.createNote(45, new Rational(13, 8), new Rational(1, 8)));

		expected.add(expected0); expected.add(expected1); expected.add(expected2);
		expected.add(expected3); expected.add(expected4);
		
		List<List<Note>> actual = new ArrayList<List<Note>>();
		for (NotationStaff notationStaff: revTrans.getPiece().getScore()) {
			for (NotationVoice notationVoice : notationStaff) {
				List<Note> currentActual = new ArrayList<Note>();
				for (NotationChord notationChord : notationVoice) {
					currentActual.add(notationChord.get(0));
				}
				actual.add(currentActual);
			}
		}
		
		// b. Non-tablature case
		tab = null;
		t = new Transcription(midiTestpiece1, null);
		revTrans = Transcription.reverse(t, tab);
		// Voice 0
		List<Note> expected5 = new ArrayList<Note>();
		expected5.add(Transcription.createNote(69, new Rational(0, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(69, new Rational(2, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(68, new Rational(3, 4), new Rational(1, 32)));
		expected5.add(Transcription.createNote(66, new Rational(25, 32), new Rational(1, 32)));
		expected5.add(Transcription.createNote(68, new Rational(13, 16), new Rational(1, 32)));
		expected5.add(Transcription.createNote(69, new Rational(27, 32), new Rational(1, 32)));
		expected5.add(Transcription.createNote(68, new Rational(7, 8), new Rational(1, 16)));
		expected5.add(Transcription.createNote(69, new Rational(15, 16), new Rational(1, 16)));
		expected5.add(Transcription.createNote(68, new Rational(4, 4), new Rational(1, 8)));
		expected5.add(Transcription.createNote(64, new Rational(9, 8), new Rational(1, 8)));
		expected5.add(Transcription.createNote(69, new Rational(5, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(65, new Rational(6, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(72, new Rational(7, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 4)));
		// Voice 1
		List<Note> expected6 = new ArrayList<Note>();
		expected6.add(Transcription.createNote(64, new Rational(0, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(64, new Rational(2, 4), new Rational(1, 2)));
		expected6.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(60, new Rational(5, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(65, new Rational(13, 8), new Rational(1, 8)));
		expected6.add(Transcription.createNote(69, new Rational(15, 8), new Rational(1, 8)));
		expected6.add(Transcription.createNote(65, new Rational(8, 4), new Rational(1, 4)));
		// Voice 2
		List<Note> expected7 = new ArrayList<Note>();
		expected7.add(Transcription.createNote(57, new Rational(0, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(57, new Rational(2, 4), new Rational(1, 2)));
		expected7.add(Transcription.createNote(59, new Rational(4, 4), new Rational(1, 8)));
		expected7.add(Transcription.createNote(60, new Rational(9, 8), new Rational(1, 8)));
		expected7.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(59, new Rational(6, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(57, new Rational(7, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 4)));
		// Voice 3
		List<Note> expected8 = new ArrayList<Note>();
		expected8.add(Transcription.createNote(45, new Rational(0, 4), new Rational(1, 4)));
		expected8.add(Transcription.createNote(45, new Rational(2, 4), new Rational(1, 2)));
		expected8.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 2)));
		expected8.add(Transcription.createNote(50, new Rational(6, 4), new Rational(1, 4)));
		expected8.add(Transcription.createNote(48, new Rational(7, 4), new Rational(1, 16)));
		expected8.add(Transcription.createNote(45, new Rational(29, 16), new Rational(3, 16)));
		expected8.add(Transcription.createNote(50, new Rational(8, 4), new Rational(1, 4)));
		// Voice 4
		List<Note> expected9 = new ArrayList<Note>();
		expected9.add(Transcription.createNote(45, new Rational(4, 4), new Rational(1, 4)));
		expected9.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 4)));
		expected9.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 8)));
		expected9.add(Transcription.createNote(47, new Rational(13, 8), new Rational(1, 8)));

		expected.add(expected5); expected.add(expected6); expected.add(expected7);
		expected.add(expected8); expected.add(expected9);

		for (NotationStaff notationStaff: revTrans.getPiece().getScore()) {
			for (NotationVoice notationVoice : notationStaff) {
				List<Note> currentActual = new ArrayList<Note>();
				for (NotationChord notationChord : notationVoice) {
					currentActual.add(notationChord.get(0));
				}
				actual.add(currentActual);
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				// assertEquals(expected.get(i).get(j), actual.get(i).get(j)) does not work because the Notes are not 
				// the same objects: therefore check that pitch, metricTime, and metricDuration are the same
				assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
				assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
				assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
			}
		}
	}


	public void testDeornament() {
		// a. Tablature case
		Tablature tab = new Tablature(encodingTestpiece1, false);
		Transcription t = new Transcription(midiTestpiece1, encodingTestpiece1);
		Transcription deornTrans = Transcription.deornament(t, tab, new Rational(1, 8));
		List<List<Note>> expected = new ArrayList<List<Note>>();
		// Voice 0
		List<Note> expected0 = new ArrayList<Note>();
		expected0.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8)));
		expected0.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8)));
		expected0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4)));
		expected0.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4)));
		// Voice 1
		List<Note> expected1 = new ArrayList<Note>();
		expected1.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4)));
		expected1.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8)));
		expected1.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 8)));
		expected1.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4)));
		expected1.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4)));
		expected1.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2)));
		expected1.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4)));
		// Voice 2
		List<Note> expected2 = new ArrayList<Note>();
		expected2.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4)));
		expected2.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8)));
		expected2.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8)));
		expected2.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2)));
		expected2.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4)));
		// Voice 3
		List<Note> expected3 = new ArrayList<Note>();
		expected3.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4)));
		expected3.add(Transcription.createNote(43, new Rational(4, 4), new Rational(1, 4)));
		expected3.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4)));
		expected3.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2)));
		expected3.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2)));
		expected3.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4)));
		// Voice 4
		List<Note> expected4 = new ArrayList<Note>();
		expected4.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8)));
		expected4.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8)));
		expected4.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4)));
		expected4.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4)));

		expected.add(expected0); expected.add(expected1); expected.add(expected2);
		expected.add(expected3); expected.add(expected4);

		List<List<Note>> actual = new ArrayList<List<Note>>();
		for (NotationStaff notationStaff: deornTrans.getPiece().getScore()) {
			for (NotationVoice notationVoice : notationStaff) {
				List<Note> currentActual = new ArrayList<Note>();
				for (NotationChord notationChord : notationVoice) {
					currentActual.add(notationChord.get(0));
				}
				actual.add(currentActual);
			}
		}

		// b. Non-tablature case
		tab = null;
		t = new Transcription(midiTestpiece1, null);
		deornTrans = Transcription.deornament(t, tab, new Rational(1, 8));
		// Voice 0
		List<Note> expected5 = new ArrayList<Note>();
		expected5.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8)));
		expected5.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8)));
		expected5.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4)));
		expected5.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4)));
		// Voice 1
		List<Note> expected6 = new ArrayList<Note>();
		expected6.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));
		expected6.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));
		expected6.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4)));
		expected6.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2)));
		expected6.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4)));
		// Voice 2
		List<Note> expected7 = new ArrayList<Note>();
		expected7.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4)));
		expected7.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8)));
		expected7.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8)));
		expected7.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2)));
		expected7.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4)));
		// Voice 3
		List<Note> expected8 = new ArrayList<Note>();
		expected8.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4)));
		expected8.add(Transcription.createNote(45, new Rational(4, 4), new Rational(1, 4)));
		expected8.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4)));
		expected8.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2)));
		expected8.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2)));
		expected8.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4)));
		// Voice 4
		List<Note> expected9 = new ArrayList<Note>();
		expected9.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8)));
		expected9.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8)));
		expected9.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4)));
		expected9.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4)));

		expected.add(expected5); expected.add(expected6); expected.add(expected7);
		expected.add(expected8); expected.add(expected9);

		for (NotationStaff notationStaff: deornTrans.getPiece().getScore()) {
			for (NotationVoice notationVoice : notationStaff) {
				List<Note> currentActual = new ArrayList<Note>();
				for (NotationChord notationChord : notationVoice) {
					currentActual.add(notationChord.get(0));
				}
				actual.add(currentActual);
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				// assertEquals(expected.get(i).get(j), actual.get(i).get(j)) does not work because the Notes are not 
				// the same objects: therefore check that pitch, metricTime, and metricDuration are the same
				assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
				assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
				assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
			}
		}
	}


	public void testFindVoice() {
		// TODO
	}


	public void testInitialiseNoteSequence() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();

		// Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
		// the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
		List<Note> expected = new ArrayList<Note>();
		// Chord 0
		expected.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4)));
		// Chord 1
		expected.add(Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16)));
		expected.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));
		expected.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
		// Chord 2
		expected.add(Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16)));
		// Chord 3
		expected.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8)));
		expected.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));
		expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
		// Chord 4
		expected.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8)));
		// Chord 5
		expected.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2)));
		expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4)));
		// Chord 6
		expected.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8)));
		expected.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8)));
		expected.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4)));
		// Chord 7
		expected.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8)));
		expected.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8)));
		// Chord 8
		expected.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2)));
		expected.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2)));
		expected.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2)));
		expected.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16)));
		// Chords 9-14
		expected.add(Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16)));
		expected.add(Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32)));
		expected.add(Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32)));
		expected.add(Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32)));
		expected.add(Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32)));
		expected.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4)));
		// Chord 15
		expected.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4)));
		expected.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4)));

		NoteSequence noteSeq = transcription.getNoteSequence();
		List<Note> actual = new ArrayList<Note>();
		for (Note n : noteSeq) {
			actual.add(n);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
			// objects: therefore check that pitch, metricTime, and metricDuration are the same
			assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
			assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
			assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
		}
	}


	public void testInitialiseVoiceLabels() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels();

		List<List<Double>> expected = new ArrayList<List<Double>>();
		// Chord 0
		expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_1);
		expected.add(Transcription.VOICE_0);
		// Chord 1 
		expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_1); 
		expected.add(Transcription.VOICE_0);
		// Chord 2 
		expected.add(Transcription.VOICE_3); 
		// Chord 3 
		expected.add(Transcription.VOICE_4); expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2);
		expected.add(Transcription.VOICE_1); expected.add(Transcription.VOICE_0);
		// Chord 4
		expected.add(Transcription.VOICE_4); 
		// Chord 5 
		expected.add(Transcription.VOICE_4); expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2);
		expected.add(Transcription.VOICE_1); expected.add(Transcription.VOICE_0);
		// Chord 6 
		expected.add(Transcription.VOICE_4); expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_1);
		// Chord 7 
		expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_0); 
		// Chord 8 
		expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_1);
		expected.add(Transcription.VOICE_0);
		// Chords 9-14
		expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_0);
		expected.add(Transcription.VOICE_0);
		// Chord 15
		expected.add(Transcription.VOICE_3); expected.add(Transcription.VOICE_2); expected.add(Transcription.VOICE_1); 
		expected.add(Transcription.VOICE_0);

		List<List<Double>> actual = transcription.getVoiceLabels();

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


	public void testInitialiseDurationLabels() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels(); // not strictly necessary
		transcription.initialiseDurationLabels();

		List<List<Double>> expected = new ArrayList<List<Double>>();
		// Chord 0
		expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); 
		expected.add(Transcription.QUARTER);
		// Chord 1
		expected.add(Transcription.DOTTED_EIGHTH); expected.add(Transcription.QUARTER); expected.add(Transcription.EIGHTH); 
		expected.add(Transcription.QUARTER);
		// Chord 2
		expected.add(Transcription.SIXTEENTH);
		// Chord 3
		expected.add(Transcription.EIGHTH); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); 
		expected.add(Transcription.EIGHTH); expected.add(Transcription.QUARTER);
		// Chord 4
		expected.add(Transcription.EIGHTH);
		// Chord 5
		expected.add(Transcription.QUARTER); expected.add(Transcription.HALF); expected.add(Transcription.QUARTER); 
		expected.add(Transcription.QUARTER); 
		expected.add(Transcription.QUARTER);
		// Chord 6
		expected.add(Transcription.QUARTER); expected.add(Transcription.EIGHTH); expected.add(Transcription.EIGHTH); 
		expected.add(Transcription.QUARTER);
		// Chord 7
		expected.add(Transcription.EIGHTH); expected.add(Transcription.EIGHTH);
		// Chord 8
		expected.add(Transcription.HALF); expected.add(Transcription.HALF); expected.add(Transcription.HALF); 
		expected.add(Transcription.SIXTEENTH);
		// Chords 9-14
		expected.add(Transcription.SIXTEENTH);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.QUARTER);
		// Chord 15
		expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); 
		expected.add(Transcription.QUARTER);

		List<List<Double>> actual = transcription.getDurationLabels();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testCheckChords() {
		// TODO
	}


	public void testHandleCoDNotes() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels(); 
		transcription.initialiseDurationLabels();
		if (transcription.checkChords(tablature) == false) {
			throw new RuntimeException("ERROR: Chord error (see console).");
		}

		// a. NoteSequence
		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
		// with the same pitch and onset time randomly
		List<Note> expectedNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			expectedNotes.add(n);
		}
		expectedNotes.remove(12); 
		// b. Voice labels
		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(transcription.getVoiceLabels());
		expectedVoiceLabels.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
		expectedVoiceLabels.remove(13);
		// c. Duration labels
		List<List<Double>> expectedDurationLabels = new ArrayList<List<Double>>(transcription.getDurationLabels());
		List<Double> adaptedDurationLabel = new ArrayList<Double>(Transcription.QUARTER);
		adaptedDurationLabel.set(3, 1.0);
		expectedDurationLabels.set(12, adaptedDurationLabel);
		expectedDurationLabels.remove(13);

		transcription.handleCoDNotes(tablature, true);
		List<Note> actualNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			actualNotes.add(n);
		}
		List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
		List<List<Double>> actualDurationLabels = transcription.getDurationLabels();

		// a. NoteSequence 
		assertEquals(expectedNotes.size(), actualNotes.size());
		for (int i = 0; i < expectedNotes.size(); i++) {
			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
			// objects: therefore check that pitch, metricTime, and metricDuration are the same
			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
		}
		// b. Voice labels
		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
			}
		}
		assertEquals(expectedVoiceLabels, actualVoiceLabels);
		// c. Duration labels
		assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
		for (int i = 0; i < expectedDurationLabels.size(); i++) {
			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
	  		for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
	  			assertEquals(expectedDurationLabels.get(i).get(j), actualDurationLabels.get(i).get(j));
	  		}
		}
		assertEquals(expectedDurationLabels, actualDurationLabels);  	
	}


//	public void testHandleCoDNotesOLD() {
//		Tablature tablature = new Tablature(encodingTestpiece1);
//		Transcription transcription = new Transcription();
//    transcription.setFile(midiTestpiece1);
//    transcription.setPiece();
//    transcription.initialiseNoteSequence();
//    transcription.initialiseVoiceLabels(); 
//    transcription.initialiseDurationLabelsOLD();
//    if (transcription.checkChords(tablature) == false) {
//     	throw new RuntimeException("ERROR: Chord error (see console).");
//    }
//    
//    // Determine expected
//    // a. NoteSequence
//    // NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
//    // with the same pitch and onset time randomly
//    List<Note> expectedNotes = new ArrayList<Note>();
//    for (Note n : transcription.getNoteSequence()) {
//    	expectedNotes.add(n);
//    }
//    expectedNotes.remove(12); 
//    // b. Voice labels
//    List<List<Double>> expectedVoiceLabels = transcription.getVoiceLabels();
//    expectedVoiceLabels.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//    expectedVoiceLabels.remove(13);
//    // c. Duration labels
//    List<List<List<Double>>> expectedDurationLabels = transcription.getDurationLabelsOLD();
//    List<List<Double>> adaptedDurationLabel = new ArrayList<List<Double>>();
//    adaptedDurationLabel.add(Transcription.QUARTER); adaptedDurationLabel.add(Transcription.EIGHTH); 
//    expectedDurationLabels.set(12, adaptedDurationLabel);
//    expectedDurationLabels.remove(13);
//    
//    // Calculate actual
//    transcription.handleCoDNotesOLD(tablature);
//    List<Note> actualNotes = new ArrayList<Note>();
//    for (Note n : transcription.getNoteSequence()) {
//    	actualNotes.add(n);
//    }
//    List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
//    List<List<List<Double>>> actualDurationLabels = transcription.getDurationLabelsOLD();
//    
//    // Assert equality
//    // a. NoteSequence 
//    assertEquals(expectedNotes.size(), actualNotes.size());
//    for (int i = 0; i < expectedNotes.size(); i++) {
//    	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//    	// objects: therefore check that pitch, metricTime, and metricDuration are the same
//    	assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
//    	assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
//    	assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
//    }
//    // b. Voice labels
//    assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
//    for (int i = 0; i < expectedVoiceLabels.size(); i++) {
//    	assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
//    	for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
//    		assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
//    	}
//    }
//    assertEquals(expectedVoiceLabels, actualVoiceLabels);
//    // c. Duration labels
//    assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
//		for (int i = 0; i < expectedDurationLabels.size(); i++) {
//			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
//	  		for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
//   			assertEquals(expectedDurationLabels.get(i).get(j).size(), actualDurationLabels.get(i).get(j).size());
//  			for (int k = 0; k < expectedDurationLabels.get(i).get(j).size(); k++) {
//    			assertEquals(expectedDurationLabels.get(i).get(j).get(k), actualDurationLabels.get(i).get(j).get(k));
//	  		}
//  		}
//  	}
//  	assertEquals(expectedDurationLabels, actualDurationLabels);
//	}


	public void testHandleCourseCrossings() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels(); 
		transcription.initialiseDurationLabels();
		if (transcription.checkChords(tablature) == false) {
			throw new RuntimeException("ERROR: Chord error (see console).");
		}
		transcription.handleCoDNotes(tablature, true);

		// a. NoteSequence
		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
		// with the same pitch and onset time randomly
		List<Note> expectedNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			expectedNotes.add(n);
		}
		expectedNotes.set(6, Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
		expectedNotes.set(7, Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));  
		// b. Voice labels
		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(transcription.getVoiceLabels());
		expectedVoiceLabels.set(6, Transcription.VOICE_0);
		expectedVoiceLabels.set(7, Transcription.VOICE_1);
		// c. Duration labels
		List<List<Double>> expectedDurationLabels = new ArrayList<List<Double>>(transcription.getDurationLabels());
		expectedDurationLabels.set(6, Transcription.QUARTER);
		expectedDurationLabels.set(7, Transcription.EIGHTH);

		// Calculate actual
		transcription.handleCourseCrossings(tablature, true);
		List<Note> actualNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			actualNotes.add(n);
		}
		List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
		List<List<Double>> actualDurationLabels = transcription.getDurationLabels();

		// a. NoteSequence 
		assertEquals(expectedNotes.size(), actualNotes.size());
		for (int i = 0; i < expectedNotes.size(); i++) {
			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
			// objects: therefore check that pitch, metricTime, and metricDuration are the same
			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
		}
		// b. Voice labels
		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
			}
		}
		assertEquals(expectedVoiceLabels, actualVoiceLabels);
		// c. Duration labels
		assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
		for (int i = 0; i < expectedDurationLabels.size(); i++) {
			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
			for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
			assertEquals(expectedDurationLabels.get(i).get(j), actualDurationLabels.get(i).get(j));
			}
		}
		assertEquals(expectedDurationLabels, actualDurationLabels);
	}


//	public void testHandleCourseCrossingsOLD() {
//		Tablature tablature = new Tablature(encodingTestpiece1);
//		Transcription transcription = new Transcription();
//    transcription.setFile(midiTestpiece1);
//    transcription.setPiece();
//    transcription.initialiseNoteSequence();
//    transcription.initialiseVoiceLabels(); 
//    transcription.initialiseDurationLabelsOLD();
//    if (transcription.checkChords(tablature) == false) {
//     	throw new RuntimeException("ERROR: Chord error (see console).");
//    }
//    transcription.handleCoDNotesOLD(tablature);
//    
//    // Determine expected
//    // a. NoteSequence
//    // NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
//    // with the same pitch and onset time randomly
//    List<Note> expectedNotes = new ArrayList<Note>();
//    for (Note n : transcription.getNoteSequence()) {
//    	expectedNotes.add(n);
//    }
//    expectedNotes.set(6, Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
//    expectedNotes.set(7, Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));  
//    // b. Voice labels
//    List<List<Double>> expectedVoiceLabels = transcription.getVoiceLabels();
//    expectedVoiceLabels.set(6, Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 0.0}));
//    expectedVoiceLabels.set(7, Arrays.asList(new Double[]{0.0, 1.0, 0.0, 0.0, 0.0}));
//    // c. Duration labels
//    List<List<List<Double>>> expectedDurationLabels = transcription.getDurationLabelsOLD();
//    List<List<Double>> quarterDurationLabel = new ArrayList<List<Double>>();
//    quarterDurationLabel.add(Transcription.QUARTER);
//    List<List<Double>> eighthDurationLabel = new ArrayList<List<Double>>();
//    quarterDurationLabel.add(Transcription.EIGHTH);
//    expectedDurationLabels.set(6, quarterDurationLabel);
//    expectedDurationLabels.set(7, eighthDurationLabel);
//    
//    // Calculate actual
//    transcription.handleCourseCrossingsOLD(tablature);
//    List<Note> actualNotes = new ArrayList<Note>();
//    for (Note n : transcription.getNoteSequence()) {
//    	actualNotes.add(n);
//    }
//    List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();
//    List<List<List<Double>>> actualDurationLabels = transcription.getDurationLabelsOLD();
//    
//    // Assert equality
//    // a. NoteSequence 
//    assertEquals(expectedNotes.size(), actualNotes.size());
//    for (int i = 0; i < expectedNotes.size(); i++) {
//    	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
//    	// objects: therefore check that pitch, metricTime, and metricDuration are the same
//    	assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
//    	assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
//    	assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
//    }
//    // b. Voice labels
//    assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
//    for (int i = 0; i < expectedVoiceLabels.size(); i++) {
//    	assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
//    	for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
//    		assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
//    	}
//    }
//    assertEquals(expectedVoiceLabels, actualVoiceLabels);
//    // c. Duration labels
//    assertEquals(expectedDurationLabels.size(), actualDurationLabels.size());
//		for (int i = 0; i < expectedDurationLabels.size(); i++) {
//			assertEquals(expectedDurationLabels.get(i).size(), actualDurationLabels.get(i).size()); 
//	  		for (int j = 0; j < expectedDurationLabels.get(i).size(); j++) {
//   			assertEquals(expectedDurationLabels.get(i).get(j).size(), actualDurationLabels.get(i).get(j).size());
//  			for (int k = 0; k < expectedDurationLabels.get(i).get(j).size(); k++) {
//    			assertEquals(expectedDurationLabels.get(i).get(j).get(k), actualDurationLabels.get(i).get(j).get(k));
//	  		}
//  		}
//  	}
//  	assertEquals(expectedDurationLabels, actualDurationLabels);
//	}

	
	public void testGetTimePitchMatrix() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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

		Rational[][] actual = transcription.getTimePitchMatrix();
		
		assertEquals(expected.length, actual.length);
	    for (int i = 0; i < expected.length; i++) {
	    	assertEquals(expected[i].length, actual[i].length);
	    	for (int j = 0; j < expected[i].length; j++) {
	    		assertEquals(expected[i][j], actual[i][j]);
	    	}
	    }
	}
	
	
	public void testGetNoteDensity() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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
		
		List<Integer> actual = transcription.getNoteDensity();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}
	
	
//	public static String opt = "O P T\n"; 
//	public static String optCost = "C O S T\n"; 
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
		String prefix = "F:/research/data/MIDI/bach-WTC/thesis/3vv/";
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
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + ".mid"), null);
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
		String prefix = "F:/research/data/MIDI/bach-WTC/thesis/3vv/";
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
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + ".mid"), null);
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
//			Transcription t = new Transcription(new File(prefix + s + ".mid"), null);
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
	
	
	public void bla3() {
		String prefix = "F:/research/data/MIDI/bach-INV/thesis/";
		List<String> fileNames = Arrays.asList(new String[]{
			"3vv/bach-INV-inventio_1-BWV_787",
//			"3vv/bach-INV-inventio_1-BWV_787-copy",
			"3vv/bach-INV-inventio_2-BWV_788", // PROBLEM IN DATA: unison not correctly encoded in MIDI --> wrong first rightChord --> vc assumption does not hold (vc at 2-3 gives wrong addition to allVoices 2-3) (would not occur if unison would be correct)
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
			"3vv/bach-INV-inventio_13-BWV_799", // PROBLEM IN ALG/DATA: config calc gives incorrect result, also with correct unison (but only just: 74 vs 72)
			"3vv/bach-INV-inventio_14-BWV_800",
			"3vv/bach-INV-inventio_15-BWV_801" // PROBLEM IN ALG: config calc gives incorrect result	
		});
		String prefix2 = "F:/research/data/MIDI/bach-INV/thesis/";
		List<String> fileNames2 = Arrays.asList(new String[]{
//			"4vv/bach-WTC1-fuga_1-BWV_846",
//			"4vv/bach-WTC1-fuga_5-BWV_850",
//			"4vv/bach-WTC1-fuga_12-BWV_857",
//			"4vv/bach-WTC1-fuga_14-BWV_859",
//			"4vv/bach-WTC1-fuga_16-BWV_861",
//			"4vv/bach-WTC1-fuga_17-BWV_862",
//			"4vv/bach-WTC1-fuga_18-BWV_863",
//			"4vv/bach-WTC1-fuga_20-BWV_865",
//			"4vv/bach-WTC1-fuga_23-BWV_868",
//			"4vv/bach-WTC1-fuga_24-BWV_869",
//			"4vv/bach-WTC2-fuga_2-BWV_871",
//			"4vv/bach-WTC2-fuga_5-BWV_874",
//			"4vv/bach-WTC2-fuga_7-BWV_876",
//			"4vv/bach-WTC2-fuga_8-BWV_877",
//			"4vv/bach-WTC2-fuga_9-BWV_878",
//			"4vv/bach-WTC2-fuga_16-BWV_885",
//			"4vv/bach-WTC2-fuga_17-BWV_886",
//			"4vv/bach-WTC2-fuga_22-BWV_891",
//			"4vv/bach-WTC2-fuga_23-BWV_892"
				
//			"3vv/bach-WTC1-fuga_2-BWV_847",
//			"3vv/bach-WTC1-fuga_3-BWV_848",
//			"3vv/bach-WTC1-fuga_6-BWV_851",
//			"3vv/bach-WTC1-fuga_7-BWV_852",
//			"3vv/bach-WTC1-fuga_8-BWV_853",
//			"3vv/bach-WTC1-fuga_9-BWV_854",
//			"3vv/bach-WTC1-fuga_11-BWV_856",
//			"3vv/bach-WTC1-fuga_13-BWV_858",
//			"3vv/bach-WTC1-fuga_15-BWV_860",
//			"3vv/bach-WTC1-fuga_19-BWV_864",
//			"3vv/bach-WTC1-fuga_21-BWV_866",
//			"3vv/bach-WTC2-fuga_1-BWV_870",
//			"3vv/bach-WTC2-fuga_3-BWV_872",
//			"3vv/bach-WTC2-fuga_4-BWV_873",
//			"3vv/bach-WTC2-fuga_6-BWV_875",
//			"3vv/bach-WTC2-fuga_10-BWV_879",
//			"3vv/bach-WTC2-fuga_11-BWV_880",
//			"3vv/bach-WTC2-fuga_12-BWV_881",
//			"3vv/bach-WTC2-fuga_13-BWV_882",
//			"3vv/bach-WTC2-fuga_14-BWV_883",
//			"3vv/bach-WTC2-fuga_15-BWV_884",
//			"3vv/bach-WTC2-fuga_18-BWV_887",
//			"3vv/bach-WTC2-fuga_19-BWV_888",
//			"3vv/bach-WTC2-fuga_20-BWV_889",
//			"3vv/bach-WTC2-fuga_21-BWV_890",
//			"3vv/bach-WTC2-fuga_24-BWV_893"
			
			"2vv/bach-INV-inventio_1-BWV_772",
			"2vv/bach-INV-inventio_2-BWV_773",
			"2vv/bach-INV-inventio_3-BWV_774",
			"2vv/bach-INV-inventio_4-BWV_775",
			"2vv/bach-INV-inventio_5-BWV_776",
			"2vv/bach-INV-inventio_6-BWV_777",
			"2vv/bach-INV-inventio_7-BWV_778",
			"2vv/bach-INV-inventio_8-BWV_779",
			"2vv/bach-INV-inventio_9-BWV_780",
			"2vv/bach-INV-inventio_10-BWV_781",
			"2vv/bach-INV-inventio_11-BWV_782",
			"2vv/bach-INV-inventio_12-BWV_783",
			"2vv/bach-INV-inventio_13-BWV_784",
			"2vv/bach-INV-inventio_14-BWV_785",
			"2vv/bach-INV-inventio_15-BWV_786"	
		});
		
		for (String s : fileNames2) {
			Transcription t = new Transcription(new File(prefix2 + s + ".mid"), null);
			System.out.println(s);
			t.getImitativeVoiceEntries(2, 3);
			System.out.println(t.getImitativeVoiceEntries(2, 3));
			
//			t.determineVoiceEntriesHIGHLEVEL(t.getBasicNoteProperties(), 3, 3);
		}
	}
	
	
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


	// DEZE
	public void testGetVoiceEntriesOLDER_EXT() {
		String prefix = "F:/research/data/MIDI/bach-INV/thesis/";
		List<String> fileNames = Arrays.asList(new String[]{
//			"3vv/bach-INV-inventio_1-BWV_787",
			"3vv/bach-INV-inventio_1-BWV_787-copy",
//			"3vv/bach-INV-inventio_2-BWV_788",
//			"3vv/bach-INV-inventio_3-BWV_789",
//			"3vv/bach-INV-inventio_4-BWV_790",
//			"3vv/bach-INV-inventio_5-BWV_791",
//			"3vv/bach-INV-inventio_6-BWV_792",
//			"3vv/bach-INV-inventio_7-BWV_793",
//			"3vv/bach-INV-inventio_8-BWV_794",
//			"3vv/bach-INV-inventio_9-BWV_795",
//			"3vv/bach-INV-inventio_10-BWV_796",
//			"3vv/bach-INV-inventio_11-BWV_797",
//			"3vv/bach-INV-inventio_12-BWV_798",
//			"3vv/bach-INV-inventio_13-BWV_799",
//			"3vv/bach-INV-inventio_14-BWV_800",
//			"3vv/bach-INV-inventio_15-BWV_801"	
		});
		
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>();
		List<Integer> voices = Arrays.asList(new Integer[]{3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4});
		for (int i = 0; i < fileNames.size(); i++) { 
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + ".mid"), null);
			List<List<Double>> res = t.getVoiceEntriesOLDER_EXT(3, 3, false);
			System.out.println(t.getPieceName());
			for (List<Double> l : res) {
				System.out.println(l);
			}
//			actual.add(t.getImitatingVoiceEntries(3, 3));
		}
	}

	// DEZE
	public void testGetImitativeVoiceEntries() {
		String prefix = "F:/research/data/MIDI/bach-WTC/thesis/";
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
		bwv881.add(Arrays.asList(new Integer[]{-1, -1}));
		bwv881.add(Arrays.asList(new Integer[]{0, 28, 29, 115, 116, 117}));
		bwv881.add(Arrays.asList(new Integer[]{0, 1, 0, 2, 1, 0}));
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
			Transcription t = new Transcription(new File(prefix + fileNames.get(i) + ".mid"), null);
			actual.add(t.getImitativeVoiceEntries(voices.get(i), 3));
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


	public void testGetNonUnisonNeighbourChord() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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
			actual.add(transcription.getNonUnisonNeighbourChord(i, null, bnp, -1));
		}
		lowestNoteInd = Arrays.asList(new Integer[]{14, 15, 36});
		for (int i : lowestNoteInd) {
			actual.add(transcription.getNonUnisonNeighbourChord(i, null, bnp, 1));
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
	
	
	public void testGetChordInfo() {	
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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
		Integer[][] bnp = transcription.getBasicNoteProperties();
		for (int i : lowestNoteInd) {
			actual.add(transcription.getChordInfo(i, null, bnp));
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
	
	
	public void testTransposeNumAccidentals() {		
		List<Integer> accid = Arrays.asList(new Integer[]{0, -3, -2, 3, 2});
		List<Integer> transp = Arrays.asList(new Integer[]{-1, -3, 4, -2, 0});

		List<Integer> expected = Arrays.asList(new Integer[]{5, 0, 2, 1, 2});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < accid.size(); i++) {
			actual.add(Transcription.transposeNumAccidentals(transp.get(i), accid.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testTranspose() {      
		Tablature tablature = new Tablature(encodingTestpiece1, false);
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels(); 
		transcription.initialiseDurationLabels();
		if (transcription.checkChords(tablature) == false) {
			throw new RuntimeException("ERROR: Chord error (see console).");
		}
		transcription.handleCoDNotes(tablature, true);
		transcription.handleCourseCrossings(tablature, true);
		if (transcription.checkAlignment(tablature) == false) {
			throw new RuntimeException("ERROR: Misalignment in Tablature and Transcription (see console).");      	
		}

		// a. NoteSequence
		// NB: expectedNotes cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
		// with the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
		List<Note> expectedNotes = new ArrayList<Note>();
		// Chord 0
		expectedNotes.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4)));
		// Chord 1
		expectedNotes.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16)));
		expectedNotes.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8)));
		// Chord 2
		expectedNotes.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16)));
		// Chord 3
		expectedNotes.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8)));
		expectedNotes.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4)));
		// Chord 4
		expectedNotes.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8)));
		// Chord 5
		expectedNotes.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2)));
		expectedNotes.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4)));
		// Chord 6
		expectedNotes.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8)));
		expectedNotes.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8)));
		expectedNotes.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4)));
		// Chord 7
		expectedNotes.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8)));
		expectedNotes.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8)));
		// Chord 8
		expectedNotes.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2)));
		expectedNotes.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2)));
		expectedNotes.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2)));
		expectedNotes.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16)));
		// Chords 9-14
		expectedNotes.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16)));
		expectedNotes.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32)));
		expectedNotes.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32)));
		expectedNotes.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32)));
		expectedNotes.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32)));
		expectedNotes.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4)));
		// Chord 14
		expectedNotes.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4)));
		expectedNotes.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4)));

		// b. Piece
		Piece expectedPiece = new Piece();
		NotationSystem system = expectedPiece.createNotationSystem();
		// Voice 0
		NotationStaff staff0 = new NotationStaff(system); system.add(staff0);
		NotationVoice voice0 = new NotationVoice(staff0); staff0.add(voice0);
		voice0.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4)));
//		NotationChord nc00 = new NotationChord(); nc00.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4))); voice0.add(nc00);
		voice0.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4)));
		voice0.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4)));
		voice0.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4)));
		voice0.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8)));
		voice0.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8)));
		voice0.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16)));
		voice0.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16)));
		voice0.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32)));
		voice0.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32)));
		voice0.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32)));
		voice0.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32)));
		voice0.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4)));
		voice0.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4)));    
		// Voice 1
		NotationStaff staff1 = new NotationStaff(system); system.add(staff1); 
		NotationVoice voice1 = new NotationVoice(staff1); staff1.add(voice1);
		voice1.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4)));
		voice1.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8)));
		voice1.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 8)));
		voice1.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4)));
		voice1.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4)));
		voice1.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2)));
		voice1.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4)));
		// Voice 2
		NotationStaff staff2 = new NotationStaff(system); system.add(staff2); 
		NotationVoice voice2 = new NotationVoice(staff2); staff2.add(voice2);
		voice2.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4)));
		voice2.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4)));
		voice2.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)));
		voice2.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4)));
		voice2.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8)));
		voice2.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8)));
		voice2.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2)));
		voice2.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4)));
		// Voice 3
		NotationStaff staff3 = new NotationStaff(system); system.add(staff3); 
		NotationVoice voice3 = new NotationVoice(staff3); staff3.add(voice3);
		voice3.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4)));
		voice3.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16)));
		voice3.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16)));
		voice3.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4)));
		voice3.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2)));
		voice3.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2)));
		voice3.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4)));
		// Voice 4
		NotationStaff staff4 = new NotationStaff(system); system.add(staff4);
		NotationVoice voice4 = new NotationVoice(staff4); staff4.add(voice4);
		voice4.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8)));
		voice4.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8)));
		voice4.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4)));
		voice4.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4)));

		transcription.transpose(tablature.getTranspositionInterval());
		// a. noteSequence
		NoteSequence noteSeq = transcription.getNoteSequence();
		List<Note> actualNotes = new ArrayList<Note>();
		for (Note n : noteSeq) {
			actualNotes.add(n);
		}
		// b. piece
		Piece actualPiece = transcription.getPiece();

		// Assert equality
		// a. noteSequence
		assertEquals(expectedNotes.size(), actualNotes.size());
		for (int i = 0; i < expectedNotes.size(); i++) {
			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
			// objects: therefore check that pitch, metricTime, and metricDuration are the same
			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
		} 
		// b. piece
		NotationSystem expectedNotationSystem = expectedPiece.getScore();
		NotationSystem actualNotationSystem = actualPiece.getScore();
		assertEquals(expectedNotationSystem.size(), actualNotationSystem.size());
		for (int i = 0; i < expectedNotationSystem.size(); i++) {
			// NotationStaff
			NotationStaff expectedNotationStaff = expectedNotationSystem.get(i);
			NotationStaff actualNotationStaff = actualNotationSystem.get(i);
			assertEquals(expectedNotationStaff.size(), actualNotationStaff.size());
			// NotationVoice
			NotationVoice expectedNotationVoice = expectedNotationStaff.get(0);
			NotationVoice actualNotationVoice = actualNotationStaff.get(0);
			assertEquals(expectedNotationVoice.size(), actualNotationVoice.size());
			for (int j = 0; j < expectedNotationVoice.size(); j++) {
				// NotationChord
				NotationChord expectedNotationChord = expectedNotationVoice.get(j);
				NotationChord actualNotationChord = actualNotationVoice.get(j);
				assertEquals(expectedNotationChord.size(), actualNotationChord.size());
				for (int k = 0; k < expectedNotationChord.size(); k++) {
					assertEquals(expectedNotationChord.get(k).getMidiPitch(), actualNotationChord.get(k).getMidiPitch());
					assertEquals(expectedNotationChord.get(k).getMetricTime(), actualNotationChord.get(k).getMetricTime());
					assertEquals(expectedNotationChord.get(k).getMetricDuration(), actualNotationChord.get(k).getMetricDuration());   	    
				}
			}
		}
	}


	public void testCheckAlignment() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);

		// Make a Transcription with a NoteSequence lacking one note
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
		NoteSequence noteSeqInequal = transcription.getNoteSequence();
		noteSeqInequal.deleteNoteAt(9);
		Transcription inequal = new Transcription();
		inequal.setNoteSequence(noteSeqInequal);
		// Make a Transcription with a pitch misalignment
		transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
		NoteSequence noteSeqMisalignedPitch = transcription.getNoteSequence();
		noteSeqMisalignedPitch.replaceNoteAt(9, Transcription.createNote(37, new Rational(5, 4), new Rational(1, 8)));
		Transcription misalignedPitch = new Transcription();
		misalignedPitch.setNoteSequence(noteSeqMisalignedPitch);
		// Make a Transcription with an onset time misalignment
		transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
		NoteSequence noteSeqMisalignedOnsetTime = transcription.getNoteSequence();
		noteSeqMisalignedOnsetTime.replaceNoteAt(9, Transcription.createNote(47, new Rational(6, 4), new Rational(1, 8)));
		Transcription misalignedOnsetTime = new Transcription();
		misalignedOnsetTime.setNoteSequence(noteSeqMisalignedOnsetTime);
		// Make a Transcription that is correctly aligned
		transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
		// Add all Transcriptions to transcriptions
		List<Transcription> transcriptions = 
			Arrays.asList(new Transcription[]{inequal, misalignedPitch, misalignedOnsetTime, transcription});

		List<Boolean> expected = Arrays.asList(new Boolean[]{false, false, false, true});

		List<Boolean> actual = new ArrayList<Boolean>();
		for (Transcription t : transcriptions) {
			actual.add(t.checkAlignment(tablature));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual .get(i));
		}
	}


	public void testGetMeter() {
		Transcription tr = new Transcription(midiTestGetMeterInfo, null);
		
		List<Rational> all = Arrays.asList(new Rational[]{
			new Rational(2, 8), // b0
			new Rational(3, 8), // begin b1 
			new Rational(12, 8), // b2
			new Rational(19, 8), // begin b3
			new Rational(28, 8), // b4
			new Rational(31, 8), // begin b5
			new Rational(45, 8), // b6
			new Rational(47, 8), // begin b7
			new Rational(100, 16) // b8
		});
		
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(3, 8), // b0
			new Rational(2, 2), // begin b1 
			new Rational(2, 2), // b2
			new Rational(3, 4), // begin b3
			new Rational(3, 4), // b4
			new Rational(2, 2), // begin b5
			new Rational(2, 2), // b6
			new Rational(5, 16), // begin b7
			new Rational(2, 2) // b8
		});
		
		List<Rational> actual = new ArrayList<Rational>();
		List<Integer[]> mi = Transcription.createMeterInfo(tr.getPiece());
		for (Rational r : all) {
			actual.add(Transcription.getMeter(r, mi));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	
	public void testGetMeterBar() {
		Transcription tr = new Transcription(midiTestGetMeterInfo, null);
		
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(3, 8), // b0
			new Rational(2, 2), // begin b1 
			new Rational(2, 2), // b2
			new Rational(3, 4), // begin b3
			new Rational(3, 4), // b4
			new Rational(2, 2), // begin b5
			new Rational(2, 2), // b6
			new Rational(5, 16), // begin b7
			new Rational(2, 2) // b8
		});
		
		List<Integer[]> mi = Transcription.createMeterInfo(tr.getPiece());
		List<Rational> actual = new ArrayList<Rational>();
		for (int i = 0; i < 9; i++) {
			actual.add(Transcription.getMeter(i, mi));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testCreateMeterInfo() {
		Transcription tr = new Transcription(midiTestGetMeterInfo, null);
		Transcription tr2 = new Transcription(midiTestGetMeterInfoNoAna, null);

		List<Integer[]> expected = new ArrayList<Integer[]>();
		// With anacrusis
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});
		// Without anacrusis
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1});
		expected.add(new Integer[]{3, 4, 4, 5, 3, 1});
		expected.add(new Integer[]{2, 2, 6, 7, 9, 2});
		expected.add(new Integer[]{5, 16, 8, 8, 13, 2});
		expected.add(new Integer[]{2, 2, 9, 9, 109, 16});

		List<Integer[]> actual = Transcription.createMeterInfo(tr.getPiece());
		actual.addAll(Transcription.createMeterInfo(tr2.getPiece()));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testCreateKeyInfo() {
		Transcription tr = new Transcription(midiTestGetMeterInfo, null);
		Transcription tr2 = new Transcription(midiTestGetMeterInfoNoAna, null);
		
		List<Integer[]> expected = new ArrayList<Integer[]>();
		// With anacrusis
		expected.add(new Integer[]{0, 0, 0, 2, 0, 1024}); // C major
		expected.add(new Integer[]{3, 1, 3, 4, 19, 8}); // f# minor
		expected.add(new Integer[]{-2, 0, 5, 6, 31, 8}); // Bb major
		expected.add(new Integer[]{1, 1, 7, 8, 47, 8}); // e minor
		// Without anacrusis
		expected.add(new Integer[]{0, 0, 1, 3, 0, 1024}); // C major
		expected.add(new Integer[]{3, 1, 4, 5, 3, 1}); // f# minor
		expected.add(new Integer[]{-2, 0, 6, 7, 9, 2}); // Bb major
		expected.add(new Integer[]{1, 1, 8, 9, 13, 2}); // e minor

		List<Integer[]> actual = Transcription.createKeyInfo(tr.getPiece(), tr.getMeterInfo());
		actual.addAll(Transcription.createKeyInfo(tr2.getPiece(), tr2.getMeterInfo()));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testHandleUnisons() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels();
//		transcription.setMeterInfo(midiTestpiece1);
		transcription.setMeterInfo();

		// a. NoteSequence
		// NB: expectedNoteSeq cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes
		// with the same pitch and onset time randomly
		List<Note> expectedNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			expectedNotes.add(n);
		}
		expectedNotes.set(12, Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
		expectedNotes.set(13, Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));  
		// b. Voice labels
		List<List<Double>> expectedVoiceLabels = new ArrayList<List<Double>>(transcription.getVoiceLabels());
		expectedVoiceLabels.set(12, Transcription.VOICE_0);
		expectedVoiceLabels.set(13, Transcription.VOICE_1);

		transcription.handleUnisons(true);
		List<Note> actualNotes = new ArrayList<Note>();
		for (Note n : transcription.getNoteSequence()) {
			actualNotes.add(n);
		}
		List<List<Double>> actualVoiceLabels = transcription.getVoiceLabels();

		// a. NoteSequence 
		assertEquals(expectedNotes.size(), actualNotes.size());
		for (int i = 0; i < expectedNotes.size(); i++) {
			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
			// objects: therefore check that pitch, metricTime, and metricDuration are the same
			assertEquals(expectedNotes.get(i).getMidiPitch(), actualNotes.get(i).getMidiPitch());
			assertEquals(expectedNotes.get(i).getMetricTime(), actualNotes.get(i).getMetricTime());
			assertEquals(expectedNotes.get(i).getMetricDuration(), actualNotes.get(i).getMetricDuration());
		}
		// b. Voice labels
		assertEquals(expectedVoiceLabels.size(), actualVoiceLabels.size());
		for (int i = 0; i < expectedVoiceLabels.size(); i++) {
			assertEquals(expectedVoiceLabels.get(i).size(), actualVoiceLabels.get(i).size());
			for (int j = 0; j < expectedVoiceLabels.get(i).size(); j++) {
				assertEquals(expectedVoiceLabels.get(i).get(j), actualVoiceLabels.get(i).get(j));
			}
		}
		assertEquals(expectedVoiceLabels, actualVoiceLabels);
	}
	
	
	public void testSetAndGetNoteSequence() {
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

    // Determine expected
    // NB: Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
    // the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
    List<Note> expected = new ArrayList<Note>();
    // Chord 0
    expected.add(Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4)));
    // Chord 1
    expected.add(Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16)));
    expected.add(Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8)));
    // Chord 2
    expected.add(Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16)));
    // Chord 3
    expected.add(Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4)));
    // Chord 4
    expected.add(Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8)));
    // Chord 5
    expected.add(Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4)));
    // Chord 6
    expected.add(Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4)));
    // Chord 7
    expected.add(Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8)));
    expected.add(Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8)));
    // Chord 8
    expected.add(Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16)));
    // Chords 9-14
    expected.add(Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16)));
    expected.add(Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32)));
    expected.add(Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32)));
    expected.add(Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32)));
    expected.add(Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32)));
    expected.add(Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4)));
    // Chord 14
    expected.add(Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4)));
    
    // Calculate actual
    NoteSequence noteSeq = transcription.getNoteSequence();
    List<Note> actual = new ArrayList<Note>();
    for (Note n : noteSeq) {
    	actual.add(n);
    }
         
    // Assert equality
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
    	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
    	// objects: therefore check that pitch, metricTime, and metricDuration are the same
    	assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
    	assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
    	assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
    }
	}
	
	
	public void testSetAndGetNoteSequenceNonTab() {
    Transcription transcription = new Transcription(midiTestpiece1, null);
    
    // Determine expected
    // NB: Expected cannot be a NoteSequence, as the NoteTimePitchComparator in the constructor adds notes with
    // the same pitch and onset time randomly -- now that in the lower voice first, then that in the higher
    List<Note> expected = new ArrayList<Note>();
    // Chord 0
    expected.add(Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4)));
    // Chord 1
    expected.add(Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16)));
    expected.add(Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4)));
    // Chord 2
    expected.add(Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16)));
    // Chord 3
    expected.add(Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8)));
    // Chord 4
    expected.add(Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8)));
    // Chord 5
    expected.add(Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4)));
    // Chord 6
    expected.add(Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8)));
    expected.add(Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4)));
    // Chord 7
    expected.add(Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8)));
    expected.add(Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8)));
    // Chord 8
    expected.add(Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2)));
    expected.add(Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16)));
    // Chords 9-14
    expected.add(Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16)));
    expected.add(Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32)));
    expected.add(Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32)));
    expected.add(Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32)));
    expected.add(Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32)));
    expected.add(Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4)));
    // Chord 15
    expected.add(Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4)));
    expected.add(Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4)));
    
    // Calculate actual
    NoteSequence noteSeq = transcription.getNoteSequence();
    List<Note> actual = new ArrayList<Note>();
    for (Note n : noteSeq) {
    	actual.add(n);
    }
        
    // Assert equality
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
    	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
    	// objects: therefore check that pitch, metricTime, and metricDuration are the same
    	assertEquals(expected.get(i).getMidiPitch(), actual.get(i).getMidiPitch());
    	assertEquals(expected.get(i).getMetricTime(), actual.get(i).getMetricTime());
    	assertEquals(expected.get(i).getMetricDuration(), actual.get(i).getMetricDuration());
    }
	}
	
	
	public void testSetAndGetVoiceLabels() {
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

    List<Double> voice0 = Transcription.VOICE_0;
    List<Double> voice1 = Transcription.VOICE_1;
    List<Double> voice2 = Transcription.VOICE_2;
    List<Double> voice3 = Transcription.VOICE_3;
    List<Double> voice4 = Transcription.VOICE_4;
    List<Double> voice0And1 = Transcription.combineLabels(voice0, voice1);
     
    // Determine expected
    List<List<Double>> expected = new ArrayList<List<Double>>();
    // Chord 0
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chord 1
    expected.add(voice3); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
    // Chord 2
    expected.add(voice3);
    // Chord 3
    expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice0And1); 
    // Chord 4
    expected.add(voice4);
    // Chord 5
    expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chord 6
    expected.add(voice4); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
    // Chord 7
    expected.add(voice2); expected.add(voice0);  
    // Chord 8
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chords 9-14
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    // Chord 15
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
             
    // Calculate actual
    List<List<Double>> actual = transcription.getVoiceLabels();
    
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
	
	
	public void testSetAndGetVoiceLabelsNonTab() {
    Transcription transcription = new Transcription(midiTestpiece1, null);

    List<Double> voice0 = Transcription.VOICE_0;
    List<Double> voice1 = Transcription.VOICE_1;
    List<Double> voice2 = Transcription.VOICE_2;
    List<Double> voice3 = Transcription.VOICE_3;
    List<Double> voice4 = Transcription.VOICE_4;
      
    // Determine expected
    List<List<Double>> expected = new ArrayList<List<Double>>();
    // Chord 0
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chord 1
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chord 2
    expected.add(voice3);
    // Chord 3
    expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
    // Chord 4
    expected.add(voice4);
    // Chord 5
    expected.add(voice4); expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chord 6
    expected.add(voice4); expected.add(voice2); expected.add(voice0); expected.add(voice1); 
    // Chord 7
    expected.add(voice2); expected.add(voice0);  
    // Chord 8
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
    // Chords 9-14
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    expected.add(voice0);
    // Chord 15
    expected.add(voice3); expected.add(voice2); expected.add(voice1); expected.add(voice0); 
           
    // Calculate actual
    List<List<Double>> actual = transcription.getVoiceLabels();
   
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
	
	 
	public void testSetAndGetChordVoiceLabels() {		
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

		List<Double> voice0 = Transcription.VOICE_0;
		List<Double> voice1 = Transcription.VOICE_1;
		List<Double> voice2 = Transcription.VOICE_2;
		List<Double> voice3 = Transcription.VOICE_3;
		List<Double> voice4 = Transcription.VOICE_4;
		List<Double> voice0And1 = Transcription.combineLabels(voice0, voice1);

		// Determine expected TODO fix superfluous variables
		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		// Chord 0
		List<List<Double>> chord0 = new ArrayList<List<Double>>(); 
		chord0.add(voice3); chord0.add(voice2); chord0.add(voice1); chord0.add(voice0);      
		// Chord 1
		List<List<Double>> chord1 = new ArrayList<List<Double>>();
		chord1.add(voice3); chord1.add(voice2); chord1.add(voice0); chord1.add(voice1);
		// Chord 2
		List<List<Double>> chord2 = new ArrayList<List<Double>>();
		chord2.add(voice3);
		// Chord 3
		List<List<Double>> chord3 = new ArrayList<List<Double>>();
		chord3.add(voice4); chord3.add(voice3); chord3.add(voice2); chord3.add(voice0And1);
		// Chord 4
		List<List<Double>> chord4 = new ArrayList<List<Double>>();
		chord4.add(voice4); 
		// Chord 5
		List<List<Double>> chord5 = new ArrayList<List<Double>>();
		chord5.add(voice4); chord5.add(voice3); chord5.add(voice2); chord5.add(voice1); chord5.add(voice0);
		// Chord 6
		List<List<Double>> chord6 = new ArrayList<List<Double>>();
		chord6.add(voice4); chord6.add(voice2); chord6.add(voice0); chord6.add(voice1);
		// Chord 7
		List<List<Double>> chord7 = new ArrayList<List<Double>>();
		chord7.add(voice2); chord7.add(voice0); 
		// Chord 8
		List<List<Double>> chord8 = new ArrayList<List<Double>>();
		chord8.add(voice3); chord8.add(voice2); chord8.add(voice1); chord8.add(voice0);
		// Chords 9-14
		List<List<Double>> chord9 = new ArrayList<List<Double>>();
		chord9.add(voice0);
		List<List<Double>> chord10 = new ArrayList<List<Double>>();
		chord10.add(voice0);
		List<List<Double>> chord11 = new ArrayList<List<Double>>();
		chord11.add(voice0);
		List<List<Double>> chord12 = new ArrayList<List<Double>>();
		chord12.add(voice0);
		List<List<Double>> chord13 = new ArrayList<List<Double>>();
		chord13.add(voice0);
		List<List<Double>> chord14 = new ArrayList<List<Double>>();
		chord14.add(voice0);
		// Chord 15
		List<List<Double>> chord15 = new ArrayList<List<Double>>();
		chord15.add(voice3); chord15.add(voice2); chord15.add(voice1); chord15.add(voice0);

		expected.add(chord0); expected.add(chord1); expected.add(chord2); expected.add(chord3); expected.add(chord4);
		expected.add(chord5); expected.add(chord6); expected.add(chord7); expected.add(chord8); expected.add(chord9);
		expected.add(chord10); expected.add(chord11); expected.add(chord12); expected.add(chord13); 
		expected.add(chord14); expected.add(chord15);

		// Calculate actual
//		List<List<List<Double>>> actual = transcription.getChordVoiceLabels(tablature);
		List<List<List<Double>>> actual = transcription.getChordVoiceLabels();

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
	}


	public void testSetAndGetChordVoiceLabelsNonTab() {		    
		Transcription transcription = new Transcription(midiTestpiece1, null);

		List<Double> voice0 = Transcription.VOICE_0;
		List<Double> voice1 = Transcription.VOICE_1;
		List<Double> voice2 = Transcription.VOICE_2;
		List<Double> voice3 = Transcription.VOICE_3;
		List<Double> voice4 = Transcription.VOICE_4;

		// Determine expected TODO fix superfluous vaiables
		List<List<List<Double>>> expected = new ArrayList<List<List<Double>>>();
		// Chord 0
		List<List<Double>> chord0 = new ArrayList<List<Double>>(); 
		chord0.add(voice3); chord0.add(voice2); chord0.add(voice1); chord0.add(voice0);
		// Chord 1
		List<List<Double>> chord1 = new ArrayList<List<Double>>();
		chord1.add(voice3); chord1.add(voice2); chord1.add(voice1); chord1.add(voice0);
		// Chord 2
		List<List<Double>> chord2 = new ArrayList<List<Double>>();
		chord2.add(voice3);
		// Chord 3
		List<List<Double>> chord3 = new ArrayList<List<Double>>();
		chord3.add(voice4); chord3.add(voice3); chord3.add(voice2); chord3.add(voice0); chord3.add(voice1);
		// Chord 4
		List<List<Double>> chord4 = new ArrayList<List<Double>>();
		chord4.add(voice4);
		// Chord 5
		List<List<Double>> chord5 = new ArrayList<List<Double>>();
		chord5.add(voice4); chord5.add(voice3); chord5.add(voice2); chord5.add(voice1); chord5.add(voice0);
		// Chord 6
		List<List<Double>> chord6 = new ArrayList<List<Double>>();
		chord6.add(voice4); chord6.add(voice2); chord6.add(voice0); chord6.add(voice1);
		// Chord 7
		List<List<Double>> chord7 = new ArrayList<List<Double>>();
		chord7.add(voice2); chord7.add(voice0); 
		// Chord 8
		List<List<Double>> chord8 = new ArrayList<List<Double>>();
		chord8.add(voice3); chord8.add(voice2); chord8.add(voice1); chord8.add(voice0);
		// Chord 9-14
		List<List<Double>> chord9 = new ArrayList<List<Double>>();
		chord9.add(voice0);
		List<List<Double>> chord10 = new ArrayList<List<Double>>();
		chord10.add(voice0);
		List<List<Double>> chord11 = new ArrayList<List<Double>>();
		chord11.add(voice0);
		List<List<Double>> chord12 = new ArrayList<List<Double>>();
		chord12.add(voice0);
		List<List<Double>> chord13 = new ArrayList<List<Double>>();
		chord13.add(voice0);
		List<List<Double>> chord14 = new ArrayList<List<Double>>();
		chord14.add(voice0);
		// Chord 15
		List<List<Double>> chord15 = new ArrayList<List<Double>>();
		chord15.add(voice3); chord15.add(voice2); chord15.add(voice1); chord15.add(voice0);

		expected.add(chord0); expected.add(chord1); expected.add(chord2); expected.add(chord3); expected.add(chord4); 
		expected.add(chord5); expected.add(chord6); expected.add(chord7); expected.add(chord8); expected.add(chord9);
		expected.add(chord10); expected.add(chord11); expected.add(chord12); expected.add(chord13); 
		expected.add(chord14); expected.add(chord15);

		// Calculate actual
//		List<List<List<Double>>> actual = transcription.getChordVoiceLabels(null);
		List<List<List<Double>>> actual = transcription.getChordVoiceLabels();

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
	}


	public void testSetAndGetDurationLabels() {
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
    
    List<Double> quarterAndEighth = new ArrayList<Double>(Transcription.QUARTER);
    quarterAndEighth.set(3, 1.0);
    
    // Determine expected
    List<List<Double>> expected = new ArrayList<List<Double>>();
    // Chord 0
    expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER);
    expected.add(Transcription.QUARTER);
    // Chord 1
    expected.add(Transcription.DOTTED_EIGHTH); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER);
    expected.add(Transcription.EIGHTH);
    // Chord 2
    expected.add(Transcription.SIXTEENTH);
    // Chord 3
    expected.add(Transcription.EIGHTH); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); 
    expected.add(quarterAndEighth);
    // Chord 4
    expected.add(Transcription.EIGHTH);
    // Chord 5
    expected.add(Transcription.QUARTER); expected.add(Transcription.HALF); expected.add(Transcription.QUARTER); 
    expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER);
    // Chord 6
    expected.add(Transcription.QUARTER); expected.add(Transcription.EIGHTH); expected.add(Transcription.EIGHTH); 
    expected.add(Transcription.QUARTER);
    // Chord 7
    expected.add(Transcription.EIGHTH); expected.add(Transcription.EIGHTH);
    // Chord 8
    expected.add(Transcription.HALF); expected.add(Transcription.HALF); expected.add(Transcription.HALF); 
    expected.add(Transcription.SIXTEENTH);
    // Chords 9-14
    expected.add(Transcription.SIXTEENTH); 
    expected.add(Transcription.THIRTYSECOND); 
    expected.add(Transcription.THIRTYSECOND);
    expected.add(Transcription.THIRTYSECOND); 
    expected.add(Transcription.THIRTYSECOND); 
    expected.add(Transcription.QUARTER);
    // Chord 15
    expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER); expected.add(Transcription.QUARTER);
    expected.add(Transcription.QUARTER);
    
    // Calculate actual
    List<List<Double>> actual = transcription.getDurationLabels();
    
    // Assert equality
    assertEquals(expected.size(), actual.size());
  	for (int i = 0; i < expected.size(); i++) {
  		assertEquals(expected.get(i).size(), actual.get(i).size()); 
 	    for (int j = 0; j < expected.get(i).size(); j++) {
	   		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//	  		for (int k = 0; k < expected.get(i).get(j).size(); k++) {
//	    		assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
//		  	}
	  	}
	  }
	  assertEquals(expected, actual);
	}
	
	
	public void testSetAndGetVoicesCoDNotes() {
		Tablature tablature1 = new Tablature(encodingTestpiece1, true);
		Transcription transcription1 = new Transcription(midiTestpiece1, encodingTestpiece1);
		
//		File encoding2 = 
//			new File(Runner.encodingsPath + DatasetID.INT_4vv.getName() + "/4vv/phalese-1563_12-las_on.tbp");
//		File midi2 = 
//			new File(Runner.midiPath + DatasetID.INT_4vv.getName() + "/4vv/phalese-1563_12-las_on.mid");
		File encoding2 = 
			new File(MEIExport.rootDir + "data/encodings/" + "tab-int" + "/4vv/phalese-1563_12-las_on.tbp");
		File midi2 = 
			new File(MEIExport.rootDir + "data/MIDI/" + "tab-int" + "/4vv/phalese-1563_12-las_on.mid");
		
		Tablature tablature2 = new Tablature(encoding2, true);
		Transcription transcription2 = new Transcription(midi2, encoding2);
				
		List<Integer[]> expected = new ArrayList<Integer[]>();
		// a. For a piece with one CoD
		List<Integer[]> expected1 = new ArrayList<Integer[]>();
		for (int i = 0; i < tablature1.getBasicTabSymbolProperties().length; i++) {
			expected1.add(null);
		}
		// CoD at metric position 2 1/4
		expected1.set(12, new Integer[]{0, 1});
		expected.addAll(expected1);
		
		// b. For a piece with multiple Cods
		List<Integer[]> expected2 = new ArrayList<Integer[]>();
		for (int i = 0; i < tablature2.getBasicTabSymbolProperties().length; i++) {
			expected2.add(null);
		}
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
		actual.addAll(transcription1.getVoicesCoDNotes());
		actual.addAll(transcription2.getVoicesCoDNotes());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}
	
	
	public void testSetAndGetMeterInfo() {
		// TODO
	}


	public void testSetAndGetEqualDurationUnisonsInfo() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		// The voice labels must be initialised before the NoteSeqeunce is adapted, as initialiseVoiceLabels() needs
		// the actual Note objects from the NoteSequence and not the adaptations made below
		transcription.initialiseVoiceLabels(); 
//		transcription.setMeterInfo(midiTestpiece1);
		transcription.setMeterInfo();
		// Before calling handleUnisons(): adapt transcription so that all unisons become EDUs
		NoteSequence noteSeq = transcription.getNoteSequence();
		// Give all unison notes a duration of a quarter
		// NB: Before handleUnisons() is called, all unison notes have been added to noteSeq with the one in the 
		// lower voice first. That means that the notes at indices 12 and 16 need to be adapted
		Note n12 = noteSeq.getNoteAt(12);
		noteSeq.replaceNoteAt(12, Transcription.createNote(n12.getMidiPitch(), n12.getMetricTime(), new Rational(1, 4)));
		Note n16 = noteSeq.getNoteAt(16);
		noteSeq.replaceNoteAt(16, Transcription.createNote(n16.getMidiPitch(), n16.getMetricTime(), new Rational(1, 4)));  
		transcription.handleUnisons(true);
		transcription.setBasicNoteProperties();

		List<Integer[]> expected = new ArrayList<Integer[]>();
		for (int i = 0; i < noteSeq.size(); i++) {
			expected.add(null);
		}
//		expected.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//		expected.set(13, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//		expected.set(16, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
//		expected.set(17, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
		expected.set(12, new Integer[]{1, 0, 13});
		expected.set(13, new Integer[]{1, 0, 12});
		expected.set(16, new Integer[]{3, 2, 17});
		expected.set(17, new Integer[]{3, 2, 16});

		List<Integer[]> actual = transcription.getEqualDurationUnisonsInfo();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
//			else {
//				assertEquals(expected.get(i).size(), actual.get(i).size());
//				for (int j = 0; j < expected.get(i).size(); j++) {
//					assertEquals(expected.get(i).get(j), actual.get(i).get(j));
//				}
//			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	public void testSetAndGetBasicNoteProperties() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
	  // Determine expected
    Integer[][] expected = new Integer[40][8];
    // Chord 0
    expected[0] = new Integer[]{50, 3, 4, 1, 4, 0, 4, 0}; 
    expected[1] = new Integer[]{57, 3, 4, 1, 4, 0, 4, 1};
    expected[2] = new Integer[]{65, 3, 4, 1, 4, 0, 4, 2};
    expected[3] = new Integer[]{69, 3, 4, 1, 4, 0, 4, 3};
    // Chord 1
    expected[4] = new Integer[]{45, 1, 1, 3, 16, 1, 4, 0};
    expected[5] = new Integer[]{57, 1, 1, 1, 4, 1, 4, 1};
    expected[6] = new Integer[]{69, 1, 1, 1, 8, 1, 4, 2};
    expected[7] = new Integer[]{72, 1, 1, 1, 4, 1, 4, 3};
    // Chord 2
    expected[8] = new Integer[]{48, 19, 16, 1, 16, 2, 1, 0};
    // Chord 3
    expected[9] = new Integer[]{47, 5, 4, 1, 8, 3, 5, 0};
    expected[10] = new Integer[]{50, 5, 4, 1, 4, 3, 5, 1};
    expected[11] = new Integer[]{59, 5, 4, 1, 4, 3, 5, 2};
    expected[12] = new Integer[]{65, 5, 4, 1, 4, 3, 5, 3};
    expected[13] = new Integer[]{65, 5, 4, 1, 8, 3, 5, 4};
    // Chord 4
    expected[14] = new Integer[]{45, 11, 8, 1, 8, 4, 1, 0};
    // Chord 5
    expected[15] = new Integer[]{45, 3, 2, 1, 4, 5, 5, 0};
    expected[16] = new Integer[]{57, 3, 2, 1, 2, 5, 5, 1};
    expected[17] = new Integer[]{57, 3, 2, 1, 4, 5, 5, 2};
    expected[18] = new Integer[]{60, 3, 2, 1, 4, 5, 5, 3};
    expected[19] = new Integer[]{69, 3, 2, 1, 4, 5, 5, 4};
    // Chord 6
    expected[20] = new Integer[]{45, 7, 4, 1, 4, 6, 4, 0};
    expected[21] = new Integer[]{60, 7, 4, 1, 8, 6, 4, 1};
    expected[22] = new Integer[]{64, 7, 4, 1, 8, 6, 4, 2};
    expected[23] = new Integer[]{69, 7, 4, 1, 4, 6, 4, 3};
    // Chord 7
    expected[24] = new Integer[]{59, 15, 8, 1, 8, 7, 2, 0};
    expected[25] = new Integer[]{68, 15, 8, 1, 8, 7, 2, 1};
    // Chord 8
    expected[26] = new Integer[]{45, 2, 1, 1, 2, 8, 4, 0};
    expected[27] = new Integer[]{57, 2, 1, 1, 2, 8, 4, 1};
    expected[28] = new Integer[]{64, 2, 1, 1, 2, 8, 4, 2};
    expected[29] = new Integer[]{69, 2, 1, 1, 16, 8, 4, 3};
    // Chords 9-14
    expected[30] = new Integer[]{68, 33, 16, 1, 16, 9, 1, 0};
    expected[31] = new Integer[]{69, 17, 8, 1, 32, 10, 1, 0};
    expected[32] = new Integer[]{68, 69, 32, 1, 32, 11, 1, 0};
    expected[33] = new Integer[]{66, 35, 16, 1, 32, 12, 1, 0};
    expected[34] = new Integer[]{68, 71, 32, 1, 32, 13, 1, 0};
    expected[35] = new Integer[]{69, 9, 4, 1, 4, 14, 1, 0};
    // Chord 15
    expected[36] = new Integer[]{45, 11, 4, 1, 4, 15, 4, 0};
    expected[37] = new Integer[]{57, 11, 4, 1, 4, 15, 4, 1};
    expected[38] = new Integer[]{64, 11, 4, 1, 4, 15, 4, 2};
    expected[39] = new Integer[]{69, 11, 4, 1, 4, 15, 4, 3};
    
    // Calculate actual
    transcription.setBasicNoteProperties();
    Integer[][] actual = transcription.getBasicNoteProperties();
    
    // Assert equality
    assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++) {
    	assertEquals(expected[i].length, actual[i].length);
    	for (int j = 0; j < expected[i].length; j++) {
    		assertEquals(expected[i][j], actual[i][j]);
    	}
    }
	}
		
	
	public void testGetBasicNotePropertiesChord() {		
		Transcription transcription = new Transcription(midiTestpiece1, null);

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


	public void testGetPitchesInChord() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

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
		List<List<Note>> transcriptionChords = transcription.getTranscriptionChords();
		for (int i = 0; i < transcriptionChords.size(); i++) {
		 	actual.add(transcription.getPitchesInChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}


	public void testGetPitchesInChordWithLowestNoteIndex() {	  	  
		Transcription transcription = new Transcription(midiTestpiece1, null);

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
		List<List<Note>> transcriptionChords = transcription.getTranscriptionChords();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int lowestNoteIndex = 0;
		for (int i = 0; i < transcriptionChords.size(); i++) {
			actual.add(Transcription.getPitchesInChord(basicNoteProperties, lowestNoteIndex));
			lowestNoteIndex += transcriptionChords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


//	public void testGetNumberOfCoDsInChord() {    
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
//		Transcription transcription = new Transcription();
////    transcription.setFile(midiTestpiece1);
//    transcription.setPiece(MidiImport.importMidiFiles(midiTestpiece1));
////    transcription.setPiece(null);
//    transcription.initialiseNoteSequence();
//    transcription.initialiseVoiceLabels(); 
//    transcription.initialiseDurationLabels();
//    if (transcription.checkChords(tablature) == false) {
//     	throw new RuntimeException("ERROR: Chord error (see console).");
//    }
//    
//    // Determine expected
//    List<Integer> expected = Arrays.asList(new Integer[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
//    
//    // Calculate actual
//    List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
//    List<Integer> actual = new ArrayList<Integer>();
//    for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
//    	actual.add(transcription.getNumberOfCoDsInChord(tablatureChords, i));
//    }
//    
//    // Assert equality
//    assertEquals(expected.size(), actual.size());
//  	for (int i = 0; i < expected.size(); i++) {
//  		assertEquals(expected.get(i), actual.get(i));		
//  	}
//  	assertEquals(expected, actual);
//	}
	
	
	public void testGetCoDInfo() {    
    Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription();
//    transcription.setFile(midiTestpiece1);
    transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//    transcription.setPiece(null);
    transcription.initialiseNoteSequence();
    transcription.initialiseVoiceLabels(); 
    transcription.initialiseDurationLabels();
    if (transcription.checkChords(tablature) == false) {
     	throw new RuntimeException("ERROR: Chord error (see console).");
    }
    
    // Determine expected
    List<Integer[][]> expected = new ArrayList<Integer[][]>();
    // Chord 0-2
    expected.add(null); expected.add(null); expected.add(null);
    // Chord 3
    expected.add(new Integer[][]{{65, 3, 4}});
    // Chord 4-15
    expected.add(null); expected.add(null); expected.add(null); expected.add(null); expected.add(null);
    expected.add(null); expected.add(null); expected.add(null); expected.add(null); expected.add(null);
    expected.add(null); expected.add(null);
    	
    // Calculate actual
    List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
    List<Integer[][]> actual = new ArrayList<Integer[][]>();
    for (int i = 0; i < transcription.getTranscriptionChordsInternal().size(); i++) {
    	actual.add(transcription.getCoDInfo(tablatureChords, i));
    }
    	
    // Assert equality
  	assertEquals(expected.size(), actual.size());
  	for (int i = 0; i < expected.size(); i++) {
  		if (expected.get(i) == null) {
  			assertEquals(expected.get(i), actual.get(i));
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
	
	
//	public void testGetEqualDurationUnisonsInfoOLD() {
//		Transcription transcription = new Transcription(midiTestpiece1, null);
//		
//		// Adapt transcription so that all unisons become EDUs
//		// NB: listEqualDurationUnisons() gets the information from the NoteSequence only; therefore, the 
//		// basicNoteProperties need not be adapted
//		NoteSequence noteSeq = transcription.getNoteSequence();
//		Note n13 = noteSeq.getNoteAt(13);
//	  noteSeq.replaceNoteAt(13, Transcription.createNote(n13.getMidiPitch(), n13.getMetricTime(), new Rational(1, 4)));
//	  // Swap so that the lower voice (now at index 13) gets the lower index, as must be the case with EDUnotes 
//	  noteSeq.swapNotes(12, 13);
//	  Note n16 = noteSeq.getNoteAt(16);
//	  noteSeq.replaceNoteAt(16, Transcription.createNote(n16.getMidiPitch(), n16.getMetricTime(), new Rational(1, 4)));
//	  
//	  // Determine expected
//	  List<List<Double>> expected = new ArrayList<List<Double>>();
//	  for (int i = 0; i < noteSeq.size(); i++) {
//	  	expected.add(null);
//	  }
//	  expected.set(12, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//	  expected.set(13, Arrays.asList(new Double[]{1.0, 1.0, 0.0, 0.0, 0.0}));
//	  expected.set(16, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
//	  expected.set(17, Arrays.asList(new Double[]{0.0, 0.0, 1.0, 1.0, 0.0}));
//	  	  
//	  // Calculate actual
//	  List<List<Double>> actual = transcription.getEqualDurationUnisonsInfoOLD();
//	  
//	  // Assert equality
//	  assertEquals(expected.size(), actual.size());
//	  for (int i = 0; i < expected.size(); i++) {
//	  	if (expected.get(i) == null) {
//	  		assertEquals(expected.get(i), actual.get(i));
//	  	}
//	  	else {
// 		    assertEquals(expected.get(i).size(), actual.get(i).size());
// 		    for (int j = 0; j < expected.get(i).size(); j++) {
// 		    	assertEquals(expected.get(i).get(j), actual.get(i).get(j));
// 		    }
//  		}
//	  }
//	}
	
	
//	public void testGetNumberOfUnisonsInChord() {
//		Transcription transcription = new Transcription();
////    transcription.setFile(midiTestpiece1);
//    transcription.setPiece(MidiImport.importMidiFiles(midiTestpiece1));
////    transcription.setPiece(null);
//    transcription.initialiseNoteSequence();
//    transcription.initialiseVoiceLabels();
//    transcription.setMeterInfo(midiTestpiece1);
//		
//		// Determine expected
//		List<Integer> expected = Arrays.asList(new Integer[]{0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
//		
//		// Calculate actual
//		List<Integer> actual = new ArrayList<Integer>();
//		for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
//			actual.add(transcription.getNumberOfUnisonsInChord(i));
//		}
//		
//		// Assert equality
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}
	
	
	public void testGetUnisonInfo() {
		Transcription transcription = new Transcription();
//		transcription.setFile(midiTestpiece1);
		transcription.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
//		transcription.setPiece(null);
		transcription.initialiseNoteSequence();
		transcription.initialiseVoiceLabels();
//		transcription.setMeterInfo(midiTestpiece1);
		transcription.setMeterInfo();

		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		// Chord 0-2
		expected.add(null); expected.add(null); expected.add(null);
		// Chord 3
		expected.add(new Integer[][]{{65, 3, 4}});
		// Chord 4
		expected.add(null); 
		// Chord 5
		expected.add(new Integer[][]{{57, 1, 2}});
		// Chord 6-15
		expected.add(null); expected.add(null); expected.add(null); expected.add(null); expected.add(null);
		expected.add(null); expected.add(null); expected.add(null); expected.add(null); expected.add(null);

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		for (int i = 0; i < transcription.getTranscriptionChordsInternal().size(); i++) {
			actual.add(transcription.getUnisonInfo(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
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


	public void testGetAndSetTranscriptionChords(){
//    Tablature tablature = new Tablature(encodingTestpiece1, true);
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
    
    // Determine expected
    List<List<Note>> expected = new ArrayList<List<Note>>();
    // Chord 0
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(48, new Rational(3, 4), new Rational(1, 4)), 
    	Transcription.createNote(55, new Rational(3, 4), new Rational(1, 4)),
    	Transcription.createNote(63, new Rational(3, 4), new Rational(1, 4)),
    	Transcription.createNote(67, new Rational(3, 4), new Rational(1, 4))}));
    // Chord 1      
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(43, new Rational(4, 4), new Rational(3, 16)),
      Transcription.createNote(55, new Rational(4, 4), new Rational(1, 4)),
      Transcription.createNote(70, new Rational(4, 4), new Rational(1, 4)),
      Transcription.createNote(67, new Rational(4, 4), new Rational(1, 8))}));
    // Chord 2
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(46, new Rational(19, 16), new Rational(1, 16))}));
    // Chord 3
    expected.add(Arrays.asList(new Note[]{	
      Transcription.createNote(45, new Rational(5, 4), new Rational(1, 8)),
      Transcription.createNote(48, new Rational(5, 4), new Rational(1, 4)),
      Transcription.createNote(57, new Rational(5, 4), new Rational(1, 4)),
      Transcription.createNote(63, new Rational(5, 4), new Rational(1, 4))}));
    // Chord 4
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(43, new Rational(11, 8), new Rational(1, 8))}));
    // Chord 5
    expected.add(Arrays.asList(new Note[]{	
      Transcription.createNote(43, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(55, new Rational(6, 4), new Rational(1, 2)),
      Transcription.createNote(55, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(58, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(67, new Rational(6, 4), new Rational(1, 4))}));
    // Chord 6
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(43, new Rational(7, 4), new Rational(1, 4)),
      Transcription.createNote(58, new Rational(7, 4), new Rational(1, 8)),
      Transcription.createNote(62, new Rational(7, 4), new Rational(1, 8)),
      Transcription.createNote(67, new Rational(7, 4), new Rational(1, 4))}));
    // Chord 7
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(57, new Rational(15, 8), new Rational(1, 8)),
      Transcription.createNote(66, new Rational(15, 8), new Rational(1, 8))}));	
    // Chord 8
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(43, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(55, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(62, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(67, new Rational(8, 4), new Rational(1, 16))}));
    // Chords 9-14
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(66, new Rational(33, 16), new Rational(1, 16))}));
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(67, new Rational(17, 8), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(66, new Rational(69, 32), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(64, new Rational(35, 16), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(66, new Rational(71, 32), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(67, new Rational(9, 4), new Rational(1, 4))}));
    // Chord 15
    expected.add(Arrays.asList(new Note[]{
    	Transcription.createNote(43, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(55, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(62, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(67, new Rational(11, 4), new Rational(1, 4))}));
            
    // Calculate actual
    List<List<Note>> actual = transcription.getTranscriptionChords();
    	
    // Assert equality
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
    	assertEquals(expected.get(i).size(), actual.get(i).size()); 
    	for (int j = 0; j < expected.get(i).size(); j++) {
       	// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
       	// objects: therefore check that pitch, metricTime, and metricDuration are the same
    		assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
    		assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
    		assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
    	}
    }
	}
	
	
	public void testGetAndSetTranscriptionChordsNonTab(){
    Transcription transcription = new Transcription(midiTestpiece1, null);

    // Determine expected
    List<List<Note>> expected = new ArrayList<List<Note>>();
    // Chord 0
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(50, new Rational(3, 4), new Rational(1, 4)),
      Transcription.createNote(57, new Rational(3, 4), new Rational(1, 4)),
      Transcription.createNote(65, new Rational(3, 4), new Rational(1, 4)),
      Transcription.createNote(69, new Rational(3, 4), new Rational(1, 4))}));  
    // Chord 1
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(4, 4), new Rational(3, 16)),
      Transcription.createNote(57, new Rational(4, 4), new Rational(1, 4)),
      Transcription.createNote(69, new Rational(4, 4), new Rational(1, 8)),
      Transcription.createNote(72, new Rational(4, 4), new Rational(1, 4))}));
    // Chord 2
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(48, new Rational(19, 16), new Rational(1, 16))}));
    // Chord 3
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(47, new Rational(5, 4), new Rational(1, 8)),
      Transcription.createNote(50, new Rational(5, 4), new Rational(1, 4)),
      Transcription.createNote(59, new Rational(5, 4), new Rational(1, 4)),
      Transcription.createNote(65, new Rational(5, 4), new Rational(1, 4)),
      Transcription.createNote(65, new Rational(5, 4), new Rational(1, 8))}));
    // Chord 4
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(11, 8), new Rational(1, 8))}));
    // Chord 5
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(57, new Rational(6, 4), new Rational(1, 2)),
      Transcription.createNote(57, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(60, new Rational(6, 4), new Rational(1, 4)),
      Transcription.createNote(69, new Rational(6, 4), new Rational(1, 4))}));
    // Chord 6
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(7, 4), new Rational(1, 4)),
      Transcription.createNote(60, new Rational(7, 4), new Rational(1, 8)),
      Transcription.createNote(64, new Rational(7, 4), new Rational(1, 8)),
      Transcription.createNote(69, new Rational(7, 4), new Rational(1, 4))}));
    // Chord 7
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(59, new Rational(15, 8), new Rational(1, 8)),
      Transcription.createNote(68, new Rational(15, 8), new Rational(1, 8))}));
    // Chord 8
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(57, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(64, new Rational(8, 4), new Rational(1, 2)),
      Transcription.createNote(69, new Rational(8, 4), new Rational(1, 16))}));
    // Chords 9-14
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(68, new Rational(33, 16), new Rational(1, 16))}));
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(69, new Rational(17, 8), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(68, new Rational(69, 32), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(66, new Rational(35, 16), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(68, new Rational(71, 32), new Rational(1, 32))}));
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(69, new Rational(9, 4), new Rational(1, 4))}));
    // Chord 15
    expected.add(Arrays.asList(new Note[]{
      Transcription.createNote(45, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(57, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(64, new Rational(11, 4), new Rational(1, 4)),
      Transcription.createNote(69, new Rational(11, 4), new Rational(1, 4))}));
           
    // Calculate actual
    List<List<Note>> actual = transcription.getTranscriptionChords();
    	
  	// Assert equality
  	assertEquals(expected.size(), actual.size());
  	for (int i = 0; i < expected.size(); i++) {
  		assertEquals(expected.get(i).size(), actual.get(i).size()); 
  		for (int j = 0; j < expected.get(i).size(); j++) {
  			// assertEquals(expected.get(i), actual.get(i)) does not work because the Notes are not the same
      	// objects: therefore check that pitch, metricTime, and metricDuration are the same
  			assertEquals(expected.get(i).get(j).getMidiPitch(), actual.get(i).get(j).getMidiPitch());
  			assertEquals(expected.get(i).get(j).getMetricTime(), actual.get(i).get(j).getMetricTime());
  			assertEquals(expected.get(i).get(j).getMetricDuration(), actual.get(i).get(j).getMetricDuration());
  		}
  	}
	}
	
	
	public void testSetAndGetNumberOfNewNotesPerChord() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

		// Determine expected
		List<Integer> expected = Arrays.asList(new Integer[]{4, 4, 1, 5, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});

		// Calculate actual
		List<Integer> actual = transcription.getNumberOfNewNotesPerChord();

		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}
	
	
	public void testGetNumberOfNotes() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		int expected = 40;
		int actual = transcription.getNumberOfNotes();
		assertEquals(expected, actual);
	}
	
	
	public void testGetIndicesPerChord() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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
		
	
	public void testGetLargestTranscriptionChord() {
	 	Transcription transcription = new Transcription(midiTestpiece1, null);

    // Determine expected
    int expected = 5;
      
    // Calculate actual
    int actual = transcription.getLargestTranscriptionChord();
    	
  	// Assert equality
  	assertEquals(expected, actual);
	}
	
	
	public void testGetNumberOfVoices() {
		List<Transcription> transcriptions = new ArrayList<Transcription>(); 
		String set = "tab-int"; //DatasetID.INT_4vv.getName();
//		Transcription transcription0 = new Transcription(
//			new File(Runner.midiPath + set + "/3vv/judenkuenig-1523_2-elslein_liebes.mid"), 
//			new File(Runner.encodingsPath + set + "/3vv/judenkuenig-1523_2-elslein_liebes.tbp"));
		Transcription transcription0 = new Transcription(
			new File(MEIExport.rootDir + "data/MIDI/" + set + "/3vv/judenkuenig-1523_2-elslein_liebes.mid"), 
			new File(MEIExport.rootDir + "data/encodings/" + set + "/3vv/judenkuenig-1523_2-elslein_liebes.tbp"));
		
//		Transcription transcription1 = new Transcription(
//			new File(Runner.midiPath + set + "/4vv/ochsenkun-1558_5-herr_gott.mid"), 
//			new File(Runner.encodingsPath + set + "/4vv/ochsenkun-1558_5-herr_gott.tbp"));
		Transcription transcription1 = new Transcription(
			new File(MEIExport.rootDir + "data/MIDI/" + set + "/4vv/ochsenkun-1558_5-herr_gott.mid"), 
			new File(MEIExport.rootDir + "data/encodings/" + set + "/4vv/ochsenkun-1558_5-herr_gott.tbp"));
		Transcription transcription2 = 
			new Transcription(midiTestpiece1, encodingTestpiece1);
		transcriptions.add(transcription0); transcriptions.add(transcription1); transcriptions.add(transcription2); 

		List<Integer> expected = Arrays.asList(new Integer[]{3, 4, 5});

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


	public void testGetNumberOfVoicesNonTab() {
		List<Transcription> transcriptions = new ArrayList<Transcription>(); 
		String set = "tab-int"; // DatasetID.INT_4vv.getName();
//		Transcription transcription0 = new Transcription(
//			new File(Runner.midiPath + set + "/3vv/judenkuenig-1523_2-elslein_liebes.mid"), null);
//		Transcription transcription1 = new Transcription(
//			new File(Runner.midiPath + set + "/4vv/ochsenkun-1558_5-herr_gott.mid"), null); 
		Transcription transcription0 = new Transcription(
			new File(MEIExport.rootDir + "data/MIDI/" + set + "/3vv/judenkuenig-1523_2-elslein_liebes.mid"), null);
		Transcription transcription1 = new Transcription(
			new File(MEIExport.rootDir + "data/MIDI/" + set + "/4vv/ochsenkun-1558_5-herr_gott.mid"), null); 
		Transcription transcription2 = new Transcription(midiTestpiece1, null);
		transcriptions.add(transcription0); transcriptions.add(transcription1); transcriptions.add(transcription2); 

		List<Integer> expected = Arrays.asList(new Integer[]{3, 4, 5});

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


	public void testGetMirrorPoint() {
		List<Transcription> trans = new ArrayList<>();
		// No anacrusis
		// a. Onset/offset of last note in last bar in all voices but the lowest, which ends
		// one bar before the others
		Transcription transcription1 = new Transcription();
		transcription1.setPiece(MIDIImport.importMidiFile(midiTestpiece1));
		transcription1.setMeterInfo();
		trans.add(transcription1);
		// b. Onset/offset of last note in last bar in upper three voices and with bars in between 
		// in lowest voice
		Transcription transcription2 = new Transcription();
		File file2 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/4vv/" +
			"bach-WTC1-fuga_1-BWV_846.mid");
		transcription2.setPiece(MIDIImport.importMidiFile(file2));
		transcription2.setMeterInfo();
		trans.add(transcription2);
		// c. Onset/offset of last note either in penultimate or in last bar, differing per voice;
		// all voices ending with rests
		Transcription transcription3 = new Transcription();
		File file3 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/4vv/" + 
			"bach-WTC2-fuga_16-BWV_885.mid");
		transcription3.setPiece(MIDIImport.importMidiFile(file3));
		transcription3.setMeterInfo();
		trans.add(transcription3);
		// With anacrusis
		// d. Onset/offset of last note in last bar in all voices
		Transcription transcription4 = new Transcription();
		File file4 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/3vv/" + 
			"bach-WTC1-fuga_11-BWV_856.mid");
		transcription4.setPiece(MIDIImport.importMidiFile(file4));
		transcription4.setMeterInfo();
		trans.add(transcription4);
		// e. Onset/offset of last note in last bar in all voices
		Transcription transcription5 = new Transcription();
		File file5 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/3vv/" + 
			"bach-WTC2-fuga_10-BWV_879.mid");
		transcription5.setPiece(MIDIImport.importMidiFile(file5));
		transcription5.setMeterInfo();
		trans.add(transcription5);
		// f. Onset/offset of last note in last bar in all voices
		Transcription transcription6 = new Transcription();
		File file6 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/3vv/" + 
			"bach-WTC2-fuga_12-BWV_881.mid");
		transcription6.setPiece(MIDIImport.importMidiFile(file6));
		transcription6.setMeterInfo();
		trans.add(transcription6);
		// g. Onset/offset of last note in last bar in all voices
		Transcription transcription7 = new Transcription();
		File file7 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/3vv/" + 
			"bach-WTC2-fuga_13-BWV_882.mid");
		transcription7.setPiece(MIDIImport.importMidiFile(file7));
		transcription7.setMeterInfo();
		trans.add(transcription7);
		// h. Onset/offset of last note in penultimate/last bar in all voices
		Transcription transcription8 = new Transcription();
		File file8 = new File(MEIExport.rootDir + "data/MIDI/" + "bach-WTC" + "/thesis/3vv/" + 
			"bach-WTC2-fuga_24-BWV_893.mid");
		transcription8.setPiece(MIDIImport.importMidiFile(file8));
		transcription8.setMeterInfo();
		trans.add(transcription8);

		List<Rational> expected;
		Rational expected1 = new Rational((3*4), 4);
		Rational expected2 = new Rational((27*4), 4);
		Rational expected3 = new Rational((84*3), 4);
		Rational expected4 = new Rational((73*3), 8);
		Rational expected5 = new Rational((87*4), 4); // = 12/8
		Rational expected6 = new Rational((86*2), 4); 
		Rational expected7 = new Rational((85*2), 2); 
		Rational expected8 = new Rational(102*3, 8);
		expected = Arrays.asList(new Rational[]{
			expected1, expected2, expected3, expected4, 
			expected5, expected6, expected7, expected8});

		List<Rational> actual = new ArrayList<Rational>();
		for (Transcription t : trans) {
			actual.add(t.getMirrorPoint(t.getMeterInfo()));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testCombineLabels() {
		// Determine expected
	  List<List<Double>> expected = new ArrayList<List<Double>>();
	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 1.0}));
	  expected.add(Arrays.asList(new Double[]{0.0, 1.0, 0.0, 1.0, 0.0}));
	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 1.0, 0.0, 0.0}));
	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 1.0, 0.0, 0.0}));
	  expected.add(Arrays.asList(new Double[]{0.0, 1.0, 0.0, 1.0, 0.0}));
	  expected.add(Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 1.0}));
	  
	  // Calculate actual
	  List<List<Double>> actual = new ArrayList<List<Double>>();
	  actual.add(Transcription.combineLabels(Transcription.VOICE_0, Transcription.VOICE_4));
	  actual.add(Transcription.combineLabels(Transcription.VOICE_1, Transcription.VOICE_3));
	  actual.add(Transcription.combineLabels(Transcription.VOICE_0, Transcription.VOICE_2));
	  actual.add(Transcription.combineLabels(Transcription.VOICE_2, Transcription.VOICE_0));
	  actual.add(Transcription.combineLabels(Transcription.VOICE_3, Transcription.VOICE_1));
	  actual.add(Transcription.combineLabels(Transcription.VOICE_4, Transcription.VOICE_0));
	  
	  // Assert equality
	  assertEquals(expected.size(), actual.size());
	  for (int i = 0; i < expected.size(); i++) {
	  	assertEquals(expected.get(i).size(), actual.get(i).size());
	  	for (int j = 0; j < expected.get(i).size(); j++) {
	  		assertEquals(expected.get(j), actual.get(j));
	  	}
	  }  
	}
	
	
	public void testGetAllMetricPositions() {
		// a. For a piece with meter changes
//		Tablature tablature = new Tablature(encodingTestGetMeterInfo, true);
		Transcription transcription = new Transcription(midiTestGetMeterInfo, null);

		List<Rational[]> expected = new ArrayList<Rational[]>();
		
		// Bar 0 (meter = 2/2): anacrusis length is 3/8 
		expected.add(new Rational[]{new Rational(0, 1), new Rational(5, 8)});
		expected.add(new Rational[]{new Rational(0, 1), new Rational(3, 4)});    
		expected.add(new Rational[]{new Rational(0, 1), new Rational(7, 8)});
		// Bar 1 (meter = 2/2): onset time beat 0 = 3/8 = 12/32
		expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});    
		expected.add(new Rational[]{new Rational(1, 1), new Rational(3, 8)});
		expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 2)});
		// Bar 2 (meter = 2/2): onset time beat 0 = 11/8 = 44/32 
		expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 4)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(5, 16)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(3, 8)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(13, 32)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(7, 16)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(15, 32)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 2)});
		// Bar 3 (meter = 3/4): onset time beat 0 = 19/8 = 76/32
		expected.add(new Rational[]{new Rational(3, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 4)}); // new Rational(1, 3)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(3, 8)}); // new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(7, 16)}); // new Rational(7, 12)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 2)}); // new Rational(2, 3)});
		expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 2)}); // new Rational(2, 3)});
		// Bar 4 (meter = 3/4): onset time beat 0 = 25/8 = 100/32
		expected.add(new Rational[]{new Rational(4, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(4, 1), new Rational(3, 16)}); // new Rational(1, 4)});
		expected.add(new Rational[]{new Rational(4, 1), new Rational(7, 32)}); // new Rational(7, 24)});
		expected.add(new Rational[]{new Rational(4, 1), new Rational(1, 4)}); // new Rational(1, 3)});
		expected.add(new Rational[]{new Rational(4, 1), new Rational(1, 4)}); // new Rational(1, 3)});
		// Bar 5 (meter = 2/2): onset time beat 0 = 31/8 = 124/32
		expected.add(new Rational[]{new Rational(5, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(5, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(5, 1), new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(5, 1), new Rational(1, 2)});
		// Bar 6 (meter = 2/2): onset time beat 0 = 39/8 = 156/32
		expected.add(new Rational[]{new Rational(6, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 8)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 4)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 8)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(5, 8)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 4)});
		expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 4)});
		// Bar 7 (meter = 5/16): onset time beat 0 = 47/8 = 188/32
		expected.add(new Rational[]{new Rational(7, 1), new Rational(0, 512)}); 
		expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 8)}); // new Rational(2, 5)});
		expected.add(new Rational[]{new Rational(7, 1), new Rational(3, 16)}); // new Rational(3, 5)});
		expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 4)}); // new Rational(4, 5)});
		// Bar 8 (meter = 2/2): onset time beat 0 = 99/16 = 198/32
		expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(1, 2)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(5, 8)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(11, 16)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 4)});
		expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 4)});

		List<Rational[]> actual = transcription.getAllMetricPositions();
		
		// b. For a piece with no meter changes
//		tablature = new Tablature(encodingTestpiece1, true);
		transcription = new Transcription(midiTestpiece1, null);
		
		// Bar 1: onset time beat 0 = 0/32
		Rational[] chord0 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
		expected.add(chord0); expected.add(chord0); expected.add(chord0); expected.add(chord0);
		// Bar 2: onset time beat 0 = 32/32
		Rational[] chord1 = new Rational[]{new Rational(2, 1), new Rational(0, 64)};
		expected.add(chord1); expected.add(chord1); expected.add(chord1); expected.add(chord1);    
		Rational[] chord2 = new Rational[]{new Rational(2, 1), new Rational(3, 16)};
		expected.add(chord2); 
		Rational[] chord3 = new Rational[]{new Rational(2, 1), new Rational(1, 4)};
		expected.add(chord3); expected.add(chord3); expected.add(chord3); expected.add(chord3);
		expected.add(chord3);
		Rational[] chord4 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
		expected.add(chord4); 
		Rational[] chord5 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expected.add(chord5); expected.add(chord5); expected.add(chord5); expected.add(chord5); 
		expected.add(chord5);
		Rational[] chord6 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
		expected.add(chord6); expected.add(chord6); expected.add(chord6); expected.add(chord6);
		Rational[] chord7 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
		expected.add(chord7); expected.add(chord7);
		// Bar 3: onset time beat 0 = 64/32
		Rational[] chord8 = new Rational[]{new Rational(3, 1), new Rational(0, 64)};
		expected.add(chord8); expected.add(chord8); expected.add(chord8); expected.add(chord8);
		Rational[] chord9 = new Rational[]{new Rational(3, 1), new Rational(1, 16)};
		expected.add(chord9); 
		Rational[] chord10 = new Rational[]{new Rational(3, 1), new Rational(1, 8)};
		expected.add(chord10); 
		Rational[] chord11 = new Rational[]{new Rational(3, 1), new Rational(5, 32)};
		expected.add(chord11); 
		Rational[] chord12 = new Rational[]{new Rational(3, 1), new Rational(3, 16)};
		expected.add(chord12); 
		Rational[] chord13 = new Rational[]{new Rational(3, 1), new Rational(7, 32)};
		expected.add(chord13); 
		Rational[] chord14 = new Rational[]{new Rational(3, 1), new Rational(1, 4)};
		expected.add(chord14); 
		Rational[] chord15 = new Rational[]{new Rational(3, 1), new Rational(3, 4)};
		expected.add(chord15); expected.add(chord15); expected.add(chord15); expected.add(chord15); 

		actual.addAll(transcription.getAllMetricPositions());

		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
//				assertEquals(expected.get(i)[j].getNumer(), actual.get(i)[j].getNumer());
//				assertEquals(expected.get(i)[j].getDenom(), actual.get(i)[j].getDenom());
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testCreateDurationLabel() {
		List<List<Double>> expected = new ArrayList<List<Double>>(); 		
		// 32nd
		expected.add(Arrays.asList(new Double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));
		// 16th
		expected.add(Arrays.asList(new Double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));	
		// 8th
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));
		// Dotted 8th
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));		
		// Quarter
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));	
		// Half
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));
		// Dotted half
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}));
		// Whole
		expected.add(Arrays.asList(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0}));
	
		List<List<Double>> actual = new ArrayList<List<Double>>();
		List<Integer> durations = Arrays.asList(new Integer[]{1, 2, 4, 6, 8, 16, 24, 32});
		for (int i = 0; i < expected.size(); i++) {
			actual.add(Transcription.createDurationLabel(durations.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}
	
	
	public void testContainsCoD() {
		List<List<Double>> voiceLabels = new ArrayList<List<Double>>();
		voiceLabels.add(Transcription.combineLabels(Transcription.VOICE_0, Transcription.VOICE_1));
		voiceLabels.add(Transcription.VOICE_0);
		voiceLabels.add(Transcription.combineLabels(Transcription.VOICE_0, Transcription.VOICE_2));
		voiceLabels.add(Transcription.VOICE_2);
		
		List<Boolean> expected = Arrays.asList(new Boolean[]{true, false, true, false});
		
		List<Boolean> actual = new ArrayList<Boolean>();
		for (List<Double> l : voiceLabels) {
			actual.add(Transcription.containsCoD(l));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	
	public void testGetLowestAndHighestPitchPerVoice() {
//    Tablature tablature = new Tablature(encodingTestpiece1, true);
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
 
    // Determine expected 
    Integer[][]expected = new Integer[Transcription.MAXIMUM_NUMBER_OF_VOICES][2];
    expected[0] = new Integer[]{62, 70};
    expected[1] = new Integer[]{58, 67};
    expected[2] = new Integer[]{55, 58};
    expected[3] = new Integer[]{43, 55};
    expected[4] = new Integer[]{43, 45};
    	
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
	
	
	public void testGetLowestAndHighestPitchPerVoiceNonTab() {
    Transcription transcription = new Transcription(midiTestpiece1, null);
   
    // Determine expected 
    Integer[][]expected = new Integer[Transcription.MAXIMUM_NUMBER_OF_VOICES][2];
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
	
	
	public void testCreatePiece() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		Integer[][] bnp = null;
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1); // not modelling duration
		Transcription transcription2 = new Transcription(midiTestpiece1, encodingTestpiece1); // modelling duration
		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
		List<List<Double>> durationLabels = transcription.getDurationLabels();

		List<Piece> expected = new ArrayList<Piece>();
		// a. Not modelling duration
		Piece expectedNonDur = transcription.getPiece();
				
		// Where necessary, adapt durations to their minimum duration
		// Voice 0
		NotationVoice v0 = expectedNonDur.getScore().get(0).get(0);
		// Note 1
		v0.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(72), new Rational(4, 4), new Rational(3, 16)));
		// Note 2
		v0.get(2).get(0).setScoreNote(new ScoreNote(new ScorePitch(65), new Rational(5, 4), new Rational(1, 8)));
		// Voice 1
		NotationVoice v1 = expectedNonDur.getScore().get(1).get(0);
		// Note 1
		v1.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(69), new Rational(4, 4), new Rational(3, 16)));
		// Note 4
		v1.get(4).get(0).setScoreNote(new ScoreNote(new ScorePitch(69), new Rational(7, 4), new Rational(1, 8)));
		// Note 5
		v1.get(5).get(0).setScoreNote(new ScoreNote(new ScorePitch(64), new Rational(8, 4), new Rational(1, 16)));
		// Voice 2
		NotationVoice v2 = expectedNonDur.getScore().get(2).get(0);
		// Note 1
		v2.get(1).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(4, 4), new Rational(3, 16)));
		// Note 2 
		v2.get(2).get(0).setScoreNote(new ScoreNote(new ScorePitch(59), new Rational(5, 4), new Rational(1, 8)));
		// Note 6
		v2.get(6).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(8, 4), new Rational(1, 16)));
		// Voice 3
		NotationVoice v3 = expectedNonDur.getScore().get(3).get(0);
		// Note 3 
		v3.get(3).get(0).setScoreNote(new ScoreNote(new ScorePitch(50), new Rational(5, 4), new Rational(1, 8)));
		// Note 4
		v3.get(4).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(6, 4), new Rational(1, 4)));
		// Note 5
		v3.get(5).get(0).setScoreNote(new ScoreNote(new ScorePitch(57), new Rational(8, 4), new Rational(1, 16)));
		// Voice 4
		NotationVoice v4 = expectedNonDur.getScore().get(4).get(0);
		// Note 3
		v4.get(3).get(0).setScoreNote(new ScoreNote(new ScorePitch(45), new Rational(7, 4), new Rational(1, 8)));
		
		expected.add(expectedNonDur);
		
		// b. Modelling duration
		Piece expectedDur = transcription2.getPiece();
		// Adapt the CoD at index 12 (the third note in voice 1) so that both notes have the same duration (necessary 
		// because currently CoDs can only have one duration)
		ScoreNote adaptedScoreNote = new ScoreNote(new ScorePitch(65), new Rational(5, 4), new Rational(1, 4));
		expectedDur.getScore().get(1).get(0).get(2).get(0).setScoreNote(adaptedScoreNote);
		// Also adapt durationLabels
		durationLabels.set(12, Transcription.createDurationLabel(8));
		expected.add(expectedDur);

		List<Piece> actual = new ArrayList<Piece>();
		// a. Not modelling duration
		actual.add(Transcription.createPiece(btp, bnp, voiceLabels, null, 5, 
			expectedNonDur.getMetricalTimeLine(), expectedNonDur.getHarmonyTrack()));
		// b. Modelling duration
		actual.add(Transcription.createPiece(btp, bnp, voiceLabels, durationLabels, 5,
			expectedDur.getMetricalTimeLine(), expectedDur.getHarmonyTrack()));

		assertEquals(expected.size(), actual.size());
		for (int num = 0; num < expected.size(); num++) {
			NotationSystem systemExpected = expected.get(num).getScore();
			NotationSystem systemActual = actual.get(num).getScore();
			assertEquals(systemExpected.size(), systemActual.size());
			// For each NotationStaff at index i
	 		for (int i = 0; i < systemExpected.size(); i++) {
				assertEquals(systemExpected.get(i).size(), systemActual.get(i).size());
				// For each NotationVoice at index j
				for (int j = 0; j < systemExpected.get(i).size(); j++) {
					assertEquals(systemExpected.get(i).get(j).size(), systemActual.get(i).get(j).size());
					// For each NotationChord at index k
					for (int k = 0; k < systemExpected.get(i).get(j).size(); k++) {	
						assertEquals(systemExpected.get(i).get(j).get(k).size(), systemActual.get(i).get(j).get(k).size());
						// For each Note at index l
						for (int l = 0; l < systemExpected.get(i).get(j).get(k).size(); l++) {
	//						assertEquals(pieceExpected.getScore().get(i).get(j).get(k).get(l), pieceActual.getScore().get(i).get(j).get(k).get(l));
							// OR if assertEquals(expected.get(i).get(j), actual.get(i).get(j).get(k) does not work because the Notes 
							// are not the same objects: check that pitch, metricTime, and metricDuration are the same
							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMidiPitch(), 
								systemActual.get(i).get(j).get(k).get(l).getMidiPitch());
							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricTime(), 
								systemActual.get(i).get(j).get(k).get(l).getMetricTime());
							assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricDuration(), 
								systemActual.get(i).get(j).get(k).get(l).getMetricDuration());		
						}
					}		
				}
			}
		}
	}
	
	
	public void testCreatePieceNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		Integer[][] btp = null;
		Integer[][] bnp = transcription.getBasicNoteProperties();
		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
		List<List<Double>> durationLabels = null;

		Piece expected = transcription.getPiece();

		Piece actual = Transcription.createPiece(btp, bnp, voiceLabels, durationLabels, 5,
			expected.getMetricalTimeLine(), expected.getHarmonyTrack());

		NotationSystem systemExpected = expected.getScore();
		NotationSystem systemActual = actual.getScore();
		assertEquals(systemExpected.size(), systemActual.size());
		// For each NotationStaff at index i
 		for (int i = 0; i < systemExpected.size(); i++) {
			assertEquals(systemExpected.get(i).size(), systemActual.get(i).size());
			// For each NotationVoice at index j
			for (int j = 0; j < systemExpected.get(i).size(); j++) {
				assertEquals(systemExpected.get(i).get(j).size(), systemActual.get(i).get(j).size());
				// For each NotationChord at index k
				for (int k = 0; k < systemExpected.get(i).get(j).size(); k++) {	
					assertEquals(systemExpected.get(i).get(j).get(k).size(), systemActual.get(i).get(j).get(k).size());
					// For each Note at index l
					for (int l = 0; l < systemExpected.get(i).get(j).get(k).size(); l++) {
//						assertEquals(pieceExpected.getScore().get(i).get(j).get(k).get(l), pieceActual.getScore().get(i).get(j).get(k).get(l));
						// OR if assertEquals(expected.get(i).get(j), actual.get(i).get(j).get(k) does not work because the Notes 
						// are not the same objects: check that pitch, metricTime, and metricDuration are the same
						assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMidiPitch(), 
								systemActual.get(i).get(j).get(k).get(l).getMidiPitch());
		     	  assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricTime(), 
		     	  		systemActual.get(i).get(j).get(k).get(l).getMetricTime());
		    	  assertEquals(systemExpected.get(i).get(j).get(k).get(l).getMetricDuration(), 
		    	  		systemActual.get(i).get(j).get(k).get(l).getMetricDuration());		
					}
				}		
			}
		}	
	}
	
	
//	public void testCreatePiece() {
//		Transcription transcription = new Transcription(midiTestpiece1, null);
//		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
//		
//		// Determine expected 
//		Piece expected = transcription.getPiece();
//		
//		// Calculate actual
//		Piece actual = transcription.createPiece(voiceLabels, 5);
//		
//		// Assert equality
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


	public void testAddNote() { // TODO check without visualisation needed 		
//		Transcription transcription = 
//			new Transcription(new File(Runner.midiPathTest + "test_add_note.mid"),	
//			new File(Runner.encodingsPathTest + "test_add_note.tbp"));
		Transcription transcription = 
			new Transcription(new File(MEIExport.rootDir + "data/MIDI/test/" + "test_add_note.mid"),	
			new File(MEIExport.rootDir + "data/encodings/test/" + "test_add_note.tbp"));

		// A Note is added to voice 1 in event 1. Assert that the corresponding NotationChord contains only one
		// Note (d') before adding 
		int voiceToAddNoteTo = 1;
		Rational onsetTime = new Rational(1, 2);
		NotationSystem system = transcription.getPiece().getScore();
		NotationStaff staff = system.get(voiceToAddNoteTo);
		NotationVoice voice = staff.get(0);
		int chordNumber = voice.find(onsetTime);
		NotationChord chord = voice.get(chordNumber);
		assertEquals(1, chord.size());
		assertEquals(62, chord.get(0).getMidiPitch());

		// Make a new Note (b)
		Note note = Transcription.createNote(59, onsetTime, new Rational(1, 2));

		// Add the Note to voice 1 at onsetTime
		transcription.addNote(note, voiceToAddNoteTo, onsetTime);

		// Assert that the NotationChord contains two Notes (b and d') after adding
		assertEquals(2, chord.size());
		assertEquals(59, chord.getLowestNote().getMidiPitch());
		assertEquals(62, chord.getUppermostNote().getMidiPitch());

		// Visualise
//		transcription.setPieceName("test_add_note");
		JFrame transcriptionFrame = transcription.visualise("test_add_note");
		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - b, d' - g'?", "Confirm", 
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		assertEquals(answer, JOptionPane.YES_OPTION);     
	}


	public void testAddNoteNonTab() {	// TODO check without visualisation needed
//		Transcription transcription = 
//			new Transcription(new File(Runner.midiPathTest + "test_add_note.mid"), null); 
		Transcription transcription = 
			new Transcription(new File(MEIExport.rootDir + "data/MIDI/test/" + "test_add_note.mid"), null); 

		// A Note is added to voice 1 in event 1. Assert that the corresponding NotationChord contains only one
		// Note (d') before adding 
		int voiceToAddNoteTo = 1;
		Rational onsetTime = new Rational(1, 2);
		NotationSystem system = transcription.getPiece().getScore();
		NotationStaff staff = system.get(voiceToAddNoteTo);
		NotationVoice voice = staff.get(0);
		int chordNumber = voice.find(onsetTime);
		NotationChord chord = voice.get(chordNumber);
		assertEquals(1, chord.size());
		assertEquals(62, chord.get(0).getMidiPitch());

		// Make a new Note (b)
		Note note = Transcription.createNote(59, onsetTime, new Rational(1, 2));

		// Add the Note to voice 1 at onsetTime
		transcription.addNote(note, voiceToAddNoteTo, onsetTime);

		// Assert that the NotationChord contains two Notes (b and d') after adding
		assertEquals(2, chord.size());
		assertEquals(59, chord.getLowestNote().getMidiPitch());
		assertEquals(62, chord.getUppermostNote().getMidiPitch());

		// Visualise
		JFrame transcriptionFrame = transcription.visualise("test_add_note");
		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - b, d' - g'?", "Confirm", 
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		assertEquals(answer, JOptionPane.YES_OPTION);     
	}


	public void testRemoveNote() { // TODO check without visualisation needed		
//		Transcription transcription = 
//			new Transcription(new File(Runner.midiPathTest + "test_remove_note.mid"), 
//			new File(Runner.encodingsPathTest + "test_remove_note.tbp"));
		Transcription transcription = 
			new Transcription(new File(MEIExport.rootDir + "data/MIDI/test/" + "test_remove_note.mid"), 
			new File(MEIExport.rootDir + "data/encodings/test/" + "test_remove_note.tbp"));

		// Two Notes are removed from voice 2 in event 1. Assert that the corresponding NotationChord contains three
		// Notes (e, b, g) before removing 
		int voiceNumber = 2;
		Rational onsetTime = new Rational(1, 2);
		NotationSystem system = transcription.getPiece().getScore();
		NotationStaff staff = system.get(voiceNumber);
		NotationVoice voice = staff.get(0);
		int chordNumber = voice.find(onsetTime);
		NotationChord chord = voice.get(chordNumber);
		assertEquals(3, chord.size());

		// Remove the Notes e and b from voice 2 at onsetTime
		transcription.removeNote(52, voiceNumber, onsetTime);
		transcription.removeNote(59, voiceNumber, onsetTime);

		// Assert that the NotationChord contains only one Note (g) after removing
		assertEquals(1, chord.size());
		assertEquals(55, chord.get(0).getMidiPitch());

		// Visualise
		JFrame transcriptionFrame = transcription.visualise("test_remove_note");
		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - d' - g'?", "Confirm", 
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		assertEquals(answer, JOptionPane.YES_OPTION);     
	}


	public void testRemoveNoteNonTab() { // TODO check without visualisation needed		
//		Transcription transcription = 
//			new Transcription(new File(Runner.midiPathTest + "test_remove_note.mid"),	null);
		Transcription transcription = 
			new Transcription(new File(MEIExport.rootDir + "data/MIDI/test/" + "test_remove_note.mid"),	null);

		// Two Notes are removed from voice 2 in event 1. Assert that the corresponding NotationChord contains three
		// Notes (e, b, g) before removing 
		int voiceNumber = 2;
		Rational onsetTime = new Rational(1, 2);
		NotationSystem system = transcription.getPiece().getScore();
		NotationStaff staff = system.get(voiceNumber);
		NotationVoice voice = staff.get(0);
		int chordNumber = voice.find(onsetTime);
		NotationChord chord = voice.get(chordNumber);
		assertEquals(3, chord.size());

		// Remove the Notes e and b from voice 2 at onsetTime
		transcription.removeNote(52, voiceNumber, onsetTime);
		transcription.removeNote(59, voiceNumber, onsetTime);

		// Assert that the NotationChord contains only one Note (g) after removing
		assertEquals(1, chord.size());
		assertEquals(55, chord.get(0).getMidiPitch());

		// Visualise
		JFrame transcriptionFrame = transcription.visualise("test_remove_note");
		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - d' - g'?", "Confirm", 
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		assertEquals(answer, JOptionPane.YES_OPTION);     
	}


	public void testGetAllOnsetTimes() {
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

		List<Rational> expected = new ArrayList<Rational>();
		// Chord 0
		expected.add(new Rational(3, 4));
		// Chord 1
		expected.add(new Rational(1, 1));
		// Chord 2
		expected.add(new Rational(19, 16));
		// Chord 3
		expected.add(new Rational(5, 4));
		// Chord 4
		expected.add(new Rational(11, 8));
		// Chord 5
		expected.add(new Rational(3, 2));
		// Chord 6
		expected.add(new Rational(7, 4));
		// Chord 7
		expected.add(new Rational(15, 8));
		// Chord 8
		expected.add(new Rational(2, 1));
		// Chords 9-14
		expected.add(new Rational(33, 16));
		expected.add(new Rational(17, 8));
		expected.add(new Rational(69, 32));
		expected.add(new Rational(35, 16));
		expected.add(new Rational(71, 32));
		expected.add(new Rational(9, 4));
		// Chord 15
		expected.add(new Rational(11, 4));

		List<Rational> actual = transcription.getAllOnsetTimes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
 	}


	public void testGetAllOnsetTimesNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

		List<Rational> expected = new ArrayList<Rational>();
		// Chord 0
		expected.add(new Rational(3, 4));
		// Chord 1
		expected.add(new Rational(1, 1));
		// Chord 2
		expected.add(new Rational(19, 16));
		// Chord 3
		expected.add(new Rational(5, 4));
		// Chord 4
		expected.add(new Rational(11, 8));
		// Chord 5
		expected.add(new Rational(3, 2));
		// Chord 6
		expected.add(new Rational(7, 4));
		// Chord 7
		expected.add(new Rational(15, 8));
		// Chord 8
		expected.add(new Rational(2, 1));
		// Chords 9-14
		expected.add(new Rational(33, 16));
		expected.add(new Rational(17, 8));
		expected.add(new Rational(69, 32));
		expected.add(new Rational(35, 16));
		expected.add(new Rational(71, 32));
		expected.add(new Rational(9, 4));
		// Chord 15
		expected.add(new Rational(11, 4));

		List<Rational> actual = transcription.getAllOnsetTimes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
 	}


	public void testListNotesPerVoice() {
//    Tablature tablature = new Tablature(encodingTestpiece1, true);
    Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
   
   	// Determine expected
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
    	
    // Calculate actual
    List<List<Double>> groundTruthVoiceLabels = transcription.getVoiceLabels();
    List<List<Integer>> actual = Transcription.listNotesPerVoice(groundTruthVoiceLabels);
    	
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
	
	
	public void testListNotesPerVoiceNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
    
    // Determine expected
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
    
    // Calculate actual
    List<List<Double>> groundTruthVoiceLabels = transcription.getVoiceLabels();
    List<List<Integer>> actual = Transcription.listNotesPerVoice(groundTruthVoiceLabels);
    	
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
	
	
	public void testGetVoiceAssignments() {
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// Chord 0
		expected.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
		// Chord 1
		expected.add(Arrays.asList(new Integer[]{2, 3, 1, 0, -1}));
		// Chord 2
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, 0, -1}));
		// Chord 3
		expected.add(Arrays.asList(new Integer[]{3, 3, 2, 1, 0}));
		// Chord 4
		expected.add(Arrays.asList(new Integer[]{-1, -1, -1, -1, 0}));
		// Chord 5
		expected.add(Arrays.asList(new Integer[]{4, 3, 2, 1, 0}));
		// Chord 6
		expected.add(Arrays.asList(new Integer[]{2, 3, 1, -1, 0}));
		// Chord 7
		expected.add(Arrays.asList(new Integer[]{1, -1, 0, -1, -1}));
		// Chord 8
		expected.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));
		// Chords 9-14
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		expected.add(Arrays.asList(new Integer[]{0, -1, -1, -1, -1}));
		// Chord 15
		expected.add(Arrays.asList(new Integer[]{3, 2, 1, 0, -1}));

		int largestNumberOfVoices = transcription.getNumberOfVoices();
		List<List<Integer>> actual = transcription.getVoiceAssignments(/*tablature,*/ largestNumberOfVoices);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}
	
	
	public void testGetAdjacentNoteInVoice() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

		NotationSystem ns = transcription.getPiece().getScore();
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
		NoteSequence noteSeq = transcription.getNoteSequence();
		// a. Direction.LEFT
		for (int i = 0; i < noteSeq.size(); i++) {
			Note n = noteSeq.getNoteAt(i);
			int voice = transcription.findVoice(n, ns);
			NotationVoice nv = ns.get(voice).get(0);
			actual.add(Transcription.getAdjacentNoteInVoice(nv, n, true));
		}
		// b. Direction.RIGHT
		List<Integer> backwardsMapping = // FeatureGenerator.getBackwardsMapping(transcription.getNumberOfNewNotesPerChord());		
			Arrays.asList(new Integer[]{36, 37, 38, 39, 35, 34, 33, 32, 31, 30, 26, 27, 28, 29, 
			24, 25, 20, 21, 22, 23, 15, 16, 17, 18, 19, 14, 9, 10, 11, 12, 13, 8, 4, 5, 6, 7, 0, 1, 2, 3});
		
		for (int i : backwardsMapping) {
			Note n = noteSeq.getNoteAt(i);
			int voice = transcription.findVoice(n, ns);
			NotationVoice nv = ns.get(voice).get(0);
			actual.add(Transcription.getAdjacentNoteInVoice(nv, n, false));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testGetIndicesOfSustainedPreviousNotes() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

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


	public void testGetIndicesOfSustainedPreviousNotesNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

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


	public void testGetPitchesOfSustainedPreviousNotesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{55, 70})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{48, 57, 63})); 
		expected.add(Arrays.asList(new Integer[]{})); 
		expected.add(Arrays.asList(new Integer[]{55})); 
		expected.add(Arrays.asList(new Integer[]{55, 43, 67})); 
		expected.add(Arrays.asList(new Integer[]{}));
		expected.add(Arrays.asList(new Integer[]{43, 55, 62})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62}));
		expected.add(Arrays.asList(new Integer[]{}));

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
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


	public void testGetPitchesOfSustainedPreviousNotesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

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
		List<List<Note>> transcriptionChords = transcription.getTranscriptionChords();
		int lowestNoteIndex = 0;
		for (int i = 0; i < transcriptionChords.size(); i++) {
			actual.add(Transcription.getPitchesOfSustainedPreviousNotesInChord(null, null, basicNoteProperties,
				lowestNoteIndex));
			lowestNoteIndex += transcriptionChords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}
	
	
	public void testGetVoicesOfSustainedPreviousNotesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

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
		List<Integer[]> voicesCoDNotes=  transcription.getVoicesCoDNotes();
		int lowestNoteIndex = 0;
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			actual.add(Transcription.getVoicesOfSustainedPreviousNotesInChord(btp, durationLabels, 
				voicesCoDNotes, null, voiceLabels, lowestNoteIndex));
			lowestNoteIndex += tablature.getTablatureChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	public void testGetVoicesOfSustainedPreviousNotesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

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
		for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
			actual.add(Transcription.getVoicesOfSustainedPreviousNotesInChord(null, null, null, bnp,
				voiceLabels, lowestNoteIndex));
			lowestNoteIndex += transcription.getTranscriptionChords().get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	public void testGetAllPitchesAndVoicesInChord() {
		// TODO?
	}


	public void testGetAllPitchesAndVoicesInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);

		List<List<Integer>> voiceAssignments = getVoiceAssignmentsNonTab();

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
		for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
			List<Integer> currentPitchesInChord = transcription.getPitchesInChord(i);
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
			List<List<Double>> currentVoiceLabels = 
				DataConverter.getChordVoiceLabels(currentVoiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentVoiceLabels);
			actual.add(Transcription.getAllPitchesAndVoicesInChord(basicNoteProperties, currentPitchesInChord,
				currentVoicesInChord, allVoiceLabels, lowestNoteIndex));
			lowestNoteIndex += transcription.getTranscriptionChords().get(i).size();
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
				DataConverter.getChordVoiceLabels(voiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentVoiceLabels);
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
				DataConverter.getChordVoiceLabels(voiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentVoiceLabels);
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
				DataConverter.getChordVoiceLabels(voiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentVoiceLabels);
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


	public void testGetVoiceCrossingInformationInChord() {
		Tablature tablature = new Tablature(encodingTestpiece1, false);

		List<List<Integer>> voiceAssignments = getVoiceAssignments();

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
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
//			List<Integer> currentPitchesInChord = 
//				FeatureGenerator.getPitchesInChord(basicTabSymbolProperties, null, lowestNoteIndex);
			List<Integer> currentPitchesInChord = tablature.getPitchesInChord(i);
			List<List<Double>> currentChordVoiceLabels = 
				DataConverter.getChordVoiceLabels(currentVoiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentChordVoiceLabels);
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


	public void testGetVoiceCrossingInformationInChordNonTab() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		List<List<Integer>> voiceAssignments = getVoiceAssignmentsNonTab();

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
		for (int i = 0; i < transcription.getTranscriptionChords().size(); i++) {
			List<Integer> currentVoiceAssignment = voiceAssignments.get(i);
//			List<Integer> currentPitchesInChord = 
//				FeatureGenerator.getPitchesInChord(null, basicNoteProperties, lowestNoteIndex);
			List<Integer> currentPitchesInChord = transcription.getPitchesInChord(i);
			List<List<Double>> currentChordVoiceLabels = 
				DataConverter.getChordVoiceLabels(currentVoiceAssignment);
			List<List<Integer>> currentVoicesInChord = 
				DataConverter.getVoicesInChord(currentChordVoiceLabels);
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
			lowestNoteIndex += transcription.getTranscriptionChords().get(i).size();
		}
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual); 
	}


	public void testGetVoiceRangeInformation() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
		
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


	public void testGenerateChordDictionary() {
		Transcription transcription = new Transcription(midiTestpiece1, null);
			
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
	
	
	public void testGenerateVoiceAssignmentDictionary(){
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);

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


	public void testGenerateVoiceAssignmentDictionaryNonTab(){
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
		Transcription transcription = new Transcription(midiTestpiece1, null);

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
			
}
