package internal.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import conversion.imports.MIDIImport;
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
import external.Tablature;
import external.Transcription;
import external.TranscriptionTest;
import interfaces.CLInterface;
import internal.core.ScorePiece;
import internal.structure.ScoreMetricalTimeLine;
import tbp.symbols.Symbol;
import tools.labels.LabelTools;
import tools.music.TimeMeterTools;
import tools.music.TimeMeterToolsTest;

public class ScorePieceTest {
	
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File encodingMemorEsto;
	private File encodingQuiHabitat;
	private File encodingPreterRerum;
	private File encodingInExitu;
	private File midiTestpiece;
	private File midiTestGetMeterKeyInfo;
	private File midiMemorEsto;
	private File midiQuiHabitat;
	private File midiPreterRerum;
	private File midiInExitu;

	private static final Rational TWO_ONE = new Rational(2, 1);
	private static final Rational TWO_TWO = new Rational(2, 2);
	private static final Rational TWO_FOUR = new Rational(2, 4);
	private static final Rational THREE_ONE = new Rational(3, 1);
	private static final Rational THREE_TWO = new Rational(3, 2);
	private static final Rational THREE_FOUR = new Rational(3, 4);

	private static final Rational TH = new Rational(1, 32);
	private static final Rational S = new Rational(1, 16);
	private static final Rational E = new Rational(1, 8);
	private static final Rational DE = new Rational(3, 16);
	private static final Rational Q = new Rational(1, 4);
	private static final Rational DQ = new Rational(3, 8);
	private static final Rational H = new Rational(1, 2);
	
	private int mtsd;


	@Before
	public void setUp() throws Exception {
		mtsd = Transcription.MAX_TABSYMBOL_DUR;

		Map<String, String> paths = CLInterface.getPaths(true);
		String ep = paths.get("ENCODINGS_PATH");
		String td = CLInterface.getPathString(Arrays.asList("test", "5vv"));
		String mp = paths.get("MIDI_PATH");
		String mpj = CLInterface.getPathString(Arrays.asList(paths.get("MIDI_PATH_JOSQUINTAB")));
		String epj = CLInterface.getPathString(Arrays.asList(paths.get("ENCODINGS_PATH_JOSQUINTAB")));

		encodingTestpiece = new File(CLInterface.getPathString(
			Arrays.asList(ep, td)) + "testpiece.tbp"
		);
		encodingTestGetMeterInfo = new File(CLInterface.getPathString(
			Arrays.asList(ep, td)) + "test_get_meter_info.tbp"
		);
		encodingMemorEsto = new File(epj + "4465_33-34_memor_esto-2.tbp");
		encodingQuiHabitat = new File(epj + "5264_13_qui_habitat_in_adjutorio_desprez-2.tbp");
		encodingPreterRerum = new File(epj + "5694_03_motet_praeter_rerum_seriem_josquin-2.tbp");
		encodingInExitu = new File(epj + "5263_12_in_exitu_israel_de_egipto_desprez-3.tbp");
		
		midiTestpiece = new File(CLInterface.getPathString(
			Arrays.asList(mp, td)) + "testpiece.mid"
		);
		midiTestGetMeterKeyInfo = new File(CLInterface.getPathString(
			Arrays.asList(mp, td)) + "test_get_meter_key_info.mid"
		);
		midiMemorEsto = new File(mpj + "Jos1714-Memor_esto_verbi_tui-166-325.mid");
		midiQuiHabitat = new File(mpj + "Jos1807-Qui_habitat_in_adjutorio_altissimi-156-282.mid");
		midiPreterRerum = new File(mpj + "Jos2411-Preter_rerum_seriem-88-185.mid");
		midiInExitu = new File(mpj + "Jos1704-In_exitu_Israel_de_Egypto-281-401.mid");	
	}

	@After
	public void tearDown() throws Exception {
	}


	private MetricalTimeLine getCleanMetricalTimeLine(String piece) {
		// Uncomment to retrieve Marker times
		boolean check = false;
		if (check) {
			Transcription t = new Transcription(midiTestpiece);
//			Transcription t = new Transcription(midiTestGetMeterKeyInfo);
//			Transcription t = new Transcription(midiMemorEsto);
//			Transcription t = new Transcription(midiQuiHabitat);
//			Transcription t = new Transcription(midiPreterRerum);
//			Transcription t = new Transcription(midiInExitu);
			for (Marker m : ScorePiece.cleanMetricalTimeLine(t.getScorePiece().getScoreMetricalTimeLine())) {
				System.out.println(m);
			}
			System.exit(0);
		}

		MetricalTimeLine mtl = new MetricalTimeLine();
		mtl.clear();
		// One meter section: 2/2; tempo = t100
		if (piece.equals("testpiece")) {
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_TWO), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add endMarker (mt = 10/1)
			mtl.add((Marker) new TimedMetrical(TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_100), new Rational(10, 1)));
		}
		// Six meter sections: 3/4, 2/1, 3/1, 2/2, 5/16, 2/4 ; tempo = t100, t100, t100, t100, t100, t100
		if (piece.equals("testGetMeterKeyInfo")) {
			long s1 = getMeterSectionLength("testGetMeterKeyInfo", "1");
			long s2 = getMeterSectionLength("testGetMeterKeyInfo", "2");
			long s3 = getMeterSectionLength("testGetMeterKeyInfo", "3");
			long s4 = getMeterSectionLength("testGetMeterKeyInfo", "4");
			long s5 = getMeterSectionLength("testGetMeterKeyInfo", "5");
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_FOUR), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add TimeSignatureMarker + TempoMarker (meter section 2, mt = 3/4)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(3, 4)));			
			mtl.add(new TempoMarker(s1, new Rational(3, 4)));
			// Add TimeSignatureMarker + TempoMarker (meter section 3, mt = 19/4)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), new Rational(19, 4)));
			mtl.add(new TempoMarker(s1 + s2, new Rational(19, 4)));
			// Add TimeSignatureMarker + TempoMarker (meter section 4, mt = 43/4)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_TWO), new Rational(43, 4)));
			mtl.add(new TempoMarker(s1 + s2 + s3, new Rational(43, 4)));
			// Add TimeSignatureMarker + TempoMarker (meter section 5, mt = 51/4)
			mtl.add(new TimeSignatureMarker(new TimeSignature(new Rational(5, 16)), new Rational(51, 4)));
			mtl.add(new TempoMarker(s1 + s2 + s3 + s4, new Rational(51, 4)));
			// Add TimeSignatureMarker + TempoMarker (meter section 6, mt = 209/16)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_FOUR), new Rational(209, 16)));
			mtl.add(new TempoMarker(s1 + s2 + s3 + s4 + s5, new Rational(209, 16)));
			// Adapt endMarker (added through last TempoMarker) (mt = 369/16)
			TimedMetrical em = (TimedMetrical) mtl.get(mtl.size()-1);
			em.setMetricTime(new Rational(369, 16));
			em.setTime(s1 + s2 + s3 + s4 + s5 + TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_100));
		}
		// One meter section: 2/1; tempo = t289
		else if (piece.equals("memor esto")) {
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add endMarker (mt = 10/1)
			mtl.add((Marker) new TimedMetrical(TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289), new Rational(10, 1)));
		}
		// Two meter sections: 3/1, 2/1; tempo = t439, t289
		else if (piece.equals("qui habitat")) {
			long s1 = getMeterSectionLength("qui habitat", "1");
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add TimeSignatureMarker + TempoMarker (meter section 2, mt = 75/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(75, 1)));
			mtl.add(new TempoMarker(s1, new Rational(75, 1)));
			// Adapt endMarker (added through last TempoMarker) (mt = 85/1)
			TimedMetrical em = (TimedMetrical) mtl.get(mtl.size()-1);
			em.setMetricTime(new Rational(85, 1));
			em.setTime(s1 + TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289));
		}
		// Three meter sections: 2/1, 3/1, 2/1; tempo = t289, t439, t289
		else if (piece.equals("preter rerum")) {
			long s1 = getMeterSectionLength("preter rerum", "1");
			long s2 = getMeterSectionLength("preter rerum", "2");
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add TimeSignatureMarker + TempoMarker (meter section 2, mt = 104/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), new Rational(104, 1)));
			mtl.add(new TempoMarker(s1, new Rational(104, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 3, mt = 212/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(212, 1)));
			mtl.add(new TempoMarker(s1 + s2, new Rational(212, 1)));
			// Adapt endMarker (added through last TempoMarker) (mt = 222/1)
			TimedMetrical em = (TimedMetrical) mtl.get(mtl.size()-1);
			em.setMetricTime(new Rational(222, 1));
			em.setTime(s1 + s2 + TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289));
		}
		// Seven meter sections: 2/1, 3/1, 2/1, 3/1, 2/1, 3/1, 2/1; tempo = t99, t439, t289, t439, t289, t439, t289
		else if (piece.equals("in exitu")) {
			long s1 = getMeterSectionLength("in exitu", "1");
			long s2 = getMeterSectionLength("in exitu", "2");
			long s3 = getMeterSectionLength("in exitu", "3");
			long s4 = getMeterSectionLength("in exitu", "4");
			long s5 = getMeterSectionLength("in exitu", "5");
			long s6 = getMeterSectionLength("in exitu", "6");
			// Add TimeSignatureMarker + zeroMarker (meter section 1, mt = 0/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), Rational.ZERO));
			mtl.add((Marker) new TimedMetrical(0, Rational.ZERO));
			// Add TimeSignatureMarker + TempoMarker (meter section 2, mt = 28/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), new Rational(28, 1)));
			mtl.add(new TempoMarker(s1, new Rational(28, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 3, mt = 76/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(76, 1)));
			mtl.add(new TempoMarker(s1 + s2, new Rational(76, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 4, mt = 90/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), new Rational(90, 1)));
			mtl.add(new TempoMarker(s1 + s2 + s3, new Rational(90, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 5, mt = 180/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(180, 1)));
			mtl.add(new TempoMarker(s1 + s2 + s3 + s4, new Rational(180, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 6, mt = 230/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(THREE_ONE), new Rational(230, 1)));
			mtl.add(new TempoMarker(s1 + s2 + s3 + s4 + s5, new Rational(230, 1)));
			// Add TimeSignatureMarker + TempoMarker (meter section 7, mt = 248/1)
			mtl.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(248, 1)));
			mtl.add(new TempoMarker(s1 + s2 + s3 + s4 + s5 + s6, new Rational(248, 1)));
			// Adapt endMarker (added through last TempoMarker) (mt = 258/1)
			TimedMetrical em = (TimedMetrical) mtl.get(mtl.size()-1);
			em.setMetricTime(new Rational(258, 1));
			em.setTime(s1 + s2 + s3 + s4 + s5 + s6 + 
				TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289));
		}
		return mtl;
	}


	private SortedContainer<Marker> getCleanHarmonyTrack(String piece) {
		SortedContainer<Marker> ht = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());

		if (piece.equals("testpiece")) {
			KeyMarker km = new KeyMarker(Rational.ZERO, 0);
			km.setAlterationNumAndMode(0, KeyMarker.Mode.MODE_MINOR); km.setRoot('C'); km.setRootAlteration(0);
			ht.add(km);
		}
		else if (piece.equals("testGetMeterKeyInfo")) {
			KeyMarker km = new KeyMarker(Rational.ZERO, 0);
			km.setAlterationNumAndMode(0, KeyMarker.Mode.MODE_MAJOR); km.setRoot('C'); km.setRootAlteration(0);
			ht.add(km);
			km = new KeyMarker(new Rational(19, 4), TimeMeterTools.calculateTime(new Rational(19, 4), 100.0));
			km.setAlterationNumAndMode(3, KeyMarker.Mode.MODE_MINOR); km.setRoot('A'); km.setRootAlteration(0);
			ht.add(km);
			km = new KeyMarker(new Rational(43, 4), TimeMeterTools.calculateTime(new Rational(43, 4), 100.0));
			km.setAlterationNumAndMode(-2, KeyMarker.Mode.MODE_MAJOR); km.setRoot('B'); km.setRootAlteration(1);
			ht.add(km);
			km = new KeyMarker(new Rational(51, 4), TimeMeterTools.calculateTime(new Rational(51, 4), 100.0));
			km.setAlterationNumAndMode(1, KeyMarker.Mode.MODE_MINOR); km.setRoot('G'); km.setRootAlteration(0);
			ht.add(km);
		}
		else if (piece.equals("in exitu")) {
			KeyMarker km = new KeyMarker(Rational.ZERO, 0);
			km.setAlterationNumAndMode(-1, KeyMarker.Mode.MODE_MAJOR); km.setRoot('F'); km.setRootAlteration(0);
			ht.add(km);
		}
		return ht;
	}


	private long getMeterSectionLength(String piece, String section) {
		// Six meter sections
		if (piece.equals("testGetMeterKeyInfo")) {
			switch (section) {
				case "1": return TimeMeterTools.calculateTime(new Rational(3-0, 4), TimeMeterToolsTest.T_100); // 1800000
				case "2": return TimeMeterTools.calculateTime(new Rational(19-3, 4), TimeMeterToolsTest.T_100); // 9600000
				case "3": return TimeMeterTools.calculateTime(new Rational(43-19, 4), TimeMeterToolsTest.T_100); // 14400000
				case "4": return TimeMeterTools.calculateTime(new Rational(51-43, 4), TimeMeterToolsTest.T_100); // 4800000
				case "5": return TimeMeterTools.calculateTime(new Rational(209-204, 16), TimeMeterToolsTest.T_100); // 750000
				case "6": return TimeMeterTools.calculateTime(new Rational(217-209, 16), TimeMeterToolsTest.T_100); // 1200000
				default: return (long) -1.0;
			}
		}
		// Seven meter sections
		if (piece.equals("memor esto")) {
			switch (section) {
				case "1a": return TimeMeterTools.calculateTime(new Rational(126-0, 1), TimeMeterToolsTest.T_289); // 104276088
				case "1b": return TimeMeterTools.calculateTime(new Rational(148-126, 1), TimeMeterToolsTest.T_289); // 18206936 
				case "1c": return TimeMeterTools.calculateTime(new Rational(198-148, 1), TimeMeterToolsTest.T_289); // 41379400 
				case "1d": return TimeMeterTools.calculateTime(new Rational(204-198, 1), TimeMeterToolsTest.T_289); // 4965528 
				case "1e": return TimeMeterTools.calculateTime(new Rational(216-204, 1), TimeMeterToolsTest.T_289); // 9931056 
				case "1f": return TimeMeterTools.calculateTime(new Rational(226-216, 1), TimeMeterToolsTest.T_289); // 8275880
				default: return (long) -1.0;
			}
		}
		// Two meter sections
		else if (piece.equals("qui habitat")) {
			switch (section) {
				case "1": return TimeMeterTools.calculateTime(new Rational(75-0, 1), TimeMeterToolsTest.T_439); // 41002200
				default: return (long) -1.0;
			}
		}
		// Three meter sections
		else if (piece.equals("preter rerum")) {
			switch (section) {
				case "1": return TimeMeterTools.calculateTime(new Rational(104-0, 1), TimeMeterToolsTest.T_289); // 86069152
				case "2": return TimeMeterTools.calculateTime(new Rational(212-104, 1), TimeMeterToolsTest.T_439); // 59043168
				default: return (long) -1.0;
			}
		}
		// Nine meter sections
		else if (piece.equals("in exitu")) {
			switch (section) {
				case "1": return TimeMeterTools.calculateTime(new Rational(28-0, 1), TimeMeterToolsTest.T_99); // 67200000
				case "2": return TimeMeterTools.calculateTime(new Rational(76-28, 1), TimeMeterToolsTest.T_439); // 26241408
				case "3": return TimeMeterTools.calculateTime(new Rational(90-76, 1), TimeMeterToolsTest.T_289); // 11586232
				case "4": return TimeMeterTools.calculateTime(new Rational(180-90, 1), TimeMeterToolsTest.T_439); // 49202640
				case "5": return TimeMeterTools.calculateTime(new Rational(230-180, 1), TimeMeterToolsTest.T_289); // 41379400
				case "5a": return TimeMeterTools.calculateTime(new Rational(192-180, 1), TimeMeterToolsTest.T_289); // 9931056
				case "5b": return TimeMeterTools.calculateTime(new Rational(204-192, 1), TimeMeterToolsTest.T_289); // 9931056
				case "5c": return TimeMeterTools.calculateTime(new Rational(230-204, 1), TimeMeterToolsTest.T_289); // 21517288
				case "6": return TimeMeterTools.calculateTime(new Rational(248-230, 1), TimeMeterToolsTest.T_439); // 9840528
				default: return (long) -1.0;
			}
		}
		else {
			return (long) -1.0;
		}
	}


	private NotationSystem getNotationSystem(Piece p, String piece) {
		NotationSystem expected = new NotationSystem();
		List<Note> unhandled = TranscriptionTest.getUnhandledNotesFromPiece(p, piece);

		// Voice 0
		NotationStaff ns0 = new NotationStaff();
		NotationVoice nv0 = new NotationVoice();		
		List<Note> notesV0 = Arrays.asList(
			unhandled.get(3), unhandled.get(7), unhandled.get(13), unhandled.get(19), 
			unhandled.get(22), unhandled.get(25), unhandled.get(29), unhandled.get(30), 
			unhandled.get(31), unhandled.get(32), unhandled.get(33), unhandled.get(34),
			unhandled.get(35), unhandled.get(39)
		);
		notesV0.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv0.add(nc);
		});
		ns0.add(nv0);
		expected.add(ns0);
		// Voice 1
		NotationStaff ns1 = new NotationStaff();
		NotationVoice nv1 = new NotationVoice();
		List<Note> notesV1 = Arrays.asList(
			unhandled.get(2), unhandled.get(6), unhandled.get(12), unhandled.get(18), 
			unhandled.get(23), unhandled.get(28), unhandled.get(38)
		);
		notesV1.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv1.add(nc);
		});
		ns1.add(nv1);
		expected.add(ns1);
		// Voice 2
		NotationStaff ns2 = new NotationStaff();
		NotationVoice nv2 = new NotationVoice();
		List<Note> notesV2 = Arrays.asList(
			unhandled.get(1), unhandled.get(5), unhandled.get(11), unhandled.get(17), 
			unhandled.get(21), unhandled.get(24), unhandled.get(27), unhandled.get(37) 
		);
		notesV2.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv2.add(nc);
		});
		ns2.add(nv2);
		expected.add(ns2);
		// Voice 3
		NotationStaff ns3 = new NotationStaff();
		NotationVoice nv3 = new NotationVoice();
		List<Note> notesV3 = Arrays.asList(
			unhandled.get(0), unhandled.get(4), unhandled.get(8), unhandled.get(10), 
			unhandled.get(16), unhandled.get(26), unhandled.get(36) 
		);
		notesV3.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv3.add(nc);
		});
		ns3.add(nv3);
		expected.add(ns3);
		// Voice 4
		NotationStaff ns4 = new NotationStaff();
		NotationVoice nv4 = new NotationVoice();
		List<Note> notesV4 = Arrays.asList(
			unhandled.get(9), unhandled.get(14), unhandled.get(15), unhandled.get(20)
		);
		notesV4.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv4.add(nc);
		});
		ns4.add(nv4);
		expected.add(ns4);

		return expected;
	}


	private void assertPieceEquality(List<Piece> expected, List<Piece> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			Piece e = expected.get(i);
			Piece a = actual.get(i);
			assertMetricalTimeLineEquality(
				Arrays.asList(e.getMetricalTimeLine()), Arrays.asList(a.getMetricalTimeLine()));
			assertHarmonyTrackEquality(
				Arrays.asList(e.getHarmonyTrack()), Arrays.asList(a.getHarmonyTrack()));
			assertNotationSystemEquality(
				Arrays.asList(e.getScore()), Arrays.asList(a.getScore()));
			assertEquals(e.getName(), a.getName());
		}
	}


	private void assertNotationSystemEquality(List<NotationSystem> expected, List<NotationSystem> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k).size(), actual.get(i).get(j).get(k).size());
					for (int l = 0; l < expected.get(i).get(j).get(k).size(); l++) {
						assertEquals(expected.get(i).get(j).get(k).get(l).size(), 
							actual.get(i).get(j).get(k).get(l).size());
						for (int m = 0; m < expected.get(i).get(j).get(k).get(l).size(); m++) {
							assertTrue(expected.get(i).get(j).get(k).get(l).get(m).isEquivalent(
								actual.get(i).get(j).get(k).get(l).get(m)));
						}
					}
				}
			}
		}
	}


	private void assertMetricalTimeLineEquality(List<MetricalTimeLine> expected, List<MetricalTimeLine> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			MetricalTimeLine e = expected.get(i);
			MetricalTimeLine a = actual.get(i);
			assertEquals(e.size(), a.size());
			List<TimeSignatureMarker> tsmE = new ArrayList<>();
			List<TempoMarker> temE = new ArrayList<>();
			List<TimedMetrical> timE = new ArrayList<>();
			for (int j = 0; j < e.size(); j++) {
				Marker m = e.get(j);
				if (m instanceof TimeSignatureMarker) {
					tsmE.add((TimeSignatureMarker) m);
				}
				if (m instanceof TempoMarker) {
					temE.add((TempoMarker) m);
				}
				if (m instanceof TimedMetrical && !(m instanceof TempoMarker)) {
					timE.add((TimedMetrical) m);
				}
			}
			List<TimeSignatureMarker> tsmA = new ArrayList<>();
			List<TempoMarker> temA = new ArrayList<>();
			List<TimedMetrical> timA = new ArrayList<>();
			for (int j = 0; j < a.size(); j++) {
				Marker m = a.get(j);
				if (m instanceof TimeSignatureMarker) {
					tsmA.add((TimeSignatureMarker) m);
				}
				if (m instanceof TempoMarker) {
					temA.add((TempoMarker) m);
				}
				if (m instanceof TimedMetrical && !(m instanceof TempoMarker)) {
					timA.add((TimedMetrical) m);
				}
			}
			assertEquals(tsmE.size(), tsmA.size());
			for (int j = 0; j < tsmE.size(); j++) {
				TimeSignatureMarker exp = tsmE.get(j);
				TimeSignatureMarker act = tsmA.get(j);
				assertEquals(exp.getTimeSignature(), act.getTimeSignature());
				assertEquals(exp.getMetricTime(), act.getMetricTime());
			}
			assertEquals(temE.size(), temA.size());
			for (int j = 0; j < temE.size(); j++) {
				TempoMarker exp = temE.get(j);
				TempoMarker act = temA.get(j);
				assertEquals(exp.getTime(), act.getTime());
				assertEquals(exp.getMetricTime(), act.getMetricTime());
			}
			assertEquals(timE.size(), timA.size());
			for (int j = 0; j < timE.size(); j++) {
				TimedMetrical exp = timE.get(j);
				TimedMetrical act = timA.get(j);
				assertEquals(exp.getTime(), act.getTime());
				assertEquals(exp.getMetricTime(), act.getMetricTime());
			}
		}
	}


	private void assertHarmonyTrackEquality(List<SortedContainer<Marker>> expected, 
		List<SortedContainer<Marker>> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			SortedContainer<Marker> e = expected.get(i);
			SortedContainer<Marker> a = actual.get(i);
			assertEquals(e.size(), a.size());
			for (int j = 0; j < e.size(); j++) {
				KeyMarker kmExp = (KeyMarker) e.get(j);
				KeyMarker kmAct = (KeyMarker) a.get(j);
				assertEquals(kmExp.getMetricTime(), kmAct.getMetricTime());
				assertEquals(kmExp.getTime(), kmAct.getTime());
				assertEquals(kmExp.getAlterationNum(), kmAct.getAlterationNum());
				assertEquals(kmExp.getMode(), kmAct.getMode());
				assertEquals(kmExp.getRoot(), kmAct.getRoot());
				assertEquals(kmExp.getRootAlteration(), kmAct.getRootAlteration());
			}
		}
	}


	@Test
	public void testCleanMetricalTimeLine() {
		// Tablature/non-tablature case
		List<String> pieceNames = Arrays.asList(
			"testpiece", 
			"memor esto", 
			"qui habitat", 
			"preter rerum", 
			"in exitu"
		);
		List<Piece> pieces = Arrays.asList(
			MIDIImport.importMidiFile(midiTestpiece), 
			MIDIImport.importMidiFile(midiMemorEsto), 
			MIDIImport.importMidiFile(midiQuiHabitat), 
			MIDIImport.importMidiFile(midiPreterRerum), 
			MIDIImport.importMidiFile(midiInExitu)
		);

		List<MetricalTimeLine> expected = new ArrayList<>();
		for (String s : pieceNames) {
			expected.add(getCleanMetricalTimeLine(s));
		}

		List<MetricalTimeLine> actual = new ArrayList<>();
		for (Piece p : pieces) {
			MetricalTimeLine mtl = p.getMetricalTimeLine();
			actual.add(ScorePiece.cleanMetricalTimeLine(mtl));
		}

		assertMetricalTimeLineEquality(expected, actual);
	}


	@Test
	public void testCleanTimedMetricals() {
		// Tablature/non-tablature case
		List<File> files = Arrays.asList(
			midiTestpiece, 
			midiMemorEsto, 
			midiQuiHabitat, 
			midiPreterRerum, 
			midiInExitu
		);

		List<MetricalTimeLine> expected = new ArrayList<>();	
		// Remove endMarker from raw MetricalTimeLines
		for (File f : files) {
			MetricalTimeLine mtl = MIDIImport.importMidiFile(f).getMetricalTimeLine();
			mtl.remove(mtl.get(mtl.size()-1));
			expected.add(mtl);
		}

		List<MetricalTimeLine> actual = new ArrayList<>();
		for (File f : files) {
			MetricalTimeLine mtl = MIDIImport.importMidiFile(f).getMetricalTimeLine();
			actual.add(ScorePiece.cleanTimedMetricals(mtl));
		}

		assertMetricalTimeLineEquality(expected, actual);		
	}


	@Test
	public void testCalculateEndMarker() {
		// Tablature/non-tablature case
		List<String> pieceNames = Arrays.asList(
			"testpiece",
			"memor esto", 
			"qui habitat",
			"preter rerum",
			"in exitu"
		);
		List<Tablature> tabs = Arrays.asList(
			new Tablature(encodingTestpiece),
			new Tablature(encodingMemorEsto),
			new Tablature(encodingQuiHabitat),
			new Tablature(encodingPreterRerum),
			new Tablature(encodingInExitu)
		);
		long tMemor = getMeterSectionLength("memor esto", "1a") + getMeterSectionLength("memor esto", "1b") + 
			getMeterSectionLength("memor esto", "1c") + getMeterSectionLength("memor esto", "1d") +
			getMeterSectionLength("memor esto", "1e") + getMeterSectionLength("memor esto", "1f");
		long tQui = getMeterSectionLength("qui habitat", "1");
		long tPreter = getMeterSectionLength("preter rerum", "1") + getMeterSectionLength("preter rerum", "2"); 
		long tIn = getMeterSectionLength("in exitu", "1") + getMeterSectionLength("in exitu", "2") + 
			getMeterSectionLength("in exitu", "3") + getMeterSectionLength("in exitu", "4") + 
			getMeterSectionLength("in exitu", "5") + getMeterSectionLength("in exitu", "6");
		List<Long> ts = Arrays.asList(
			(long) 0, (long) 0, // per piece, values for non-aligned and aligned
			(long) 0, tMemor, 
			tQui, tQui, 
			tPreter, tPreter,
			tIn, tIn
		);
		List<Double> tmps = Arrays.asList(
			TimeMeterToolsTest.T_100, TimeMeterToolsTest.T_100, // per piece, values for non-aligned and aligned
			TimeMeterToolsTest.T_289, TimeMeterToolsTest.T_289, 
			TimeMeterToolsTest.T_289, TimeMeterToolsTest.T_289,
			TimeMeterToolsTest.T_289, TimeMeterToolsTest.T_289,
			TimeMeterToolsTest.T_289, TimeMeterToolsTest.T_289
		);
		List<Rational> mts = Arrays.asList(
			Rational.ZERO, Rational.ZERO, // per piece, values for non-aligned and aligned
			Rational.ZERO, new Rational(226, 1),  
			new Rational(75, 1), new Rational(75, 1),
			new Rational(212, 1), new Rational(212, 1),
			new Rational(248, 1), new Rational(248, 1)
		);

		List<TimedMetrical> expected = new ArrayList<>();
		for (int i = 0; i < pieceNames.size(); i++) {
			MetricalTimeLine mtl = getCleanMetricalTimeLine(pieceNames.get(i));
			MetricalTimeLine mtlAligned = ScorePiece.alignMetricalTimeLine(mtl, tabs.get(i).getMeterInfo());
			// Find the last TimedMetrical before the endMarker (which will be a 
			// TimedMetrical or a TempoMarker)
			for (MetricalTimeLine metl : Arrays.asList(new MetricalTimeLine[]{mtl, mtlAligned})) {
				for (int j = metl.size()-2; j >= 0; j--) {
					Marker m = metl.get(j);
					if (m instanceof TimedMetrical) {
						long t = ((TimedMetrical) m).getTime();
						Rational mt = ((TimedMetrical) m).getMetricTime();
						double tempo = metl.getTempo(t);
						expected.add(new TimedMetrical(
							t + TimeMeterTools.calculateTime(new Rational(10, 1), tempo), 
							mt.add(new Rational(10, 1))));
						break;
					}
				}
			}
		}

		List<TimedMetrical> actual = new ArrayList<>();
		for (int i = 0; i < ts.size(); i++) {
			actual.add(ScorePiece.calculateEndMarker(ts.get(i), tmps.get(i), mts.get(i), 1));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			TimedMetrical exp = expected.get(i);
			TimedMetrical act = actual.get(i);
			assertEquals(exp.getTime(), act.getTime());
			assertEquals(exp.getMetricTime(), act.getMetricTime());
		}
	}


	@Test
	public void testCleanHarmonyTrack() {
		// Tablature/non-tablature case
		List<String> pieceNames = Arrays.asList(
			"testpiece", 
			"testGetMeterKeyInfo",
			"in exitu"
		);
		List<Piece> pieces = Arrays.asList(
			MIDIImport.importMidiFile(midiTestpiece), 
			MIDIImport.importMidiFile(midiTestGetMeterKeyInfo), 
			MIDIImport.importMidiFile(midiInExitu)
		);

		List<SortedContainer<Marker>> expected = new ArrayList<>();
		for (String s : pieceNames) {
			expected.add(getCleanHarmonyTrack(s));
		}

		List<SortedContainer<Marker>> actual = new ArrayList<>();
		for (Piece p : pieces) {
			SortedContainer<Marker> ht = p.getHarmonyTrack();
			actual.add(ScorePiece.cleanHarmonyTrack(ht));
		}

		assertHarmonyTrackEquality(expected, actual);
	}


	@Test
	public void testMakeScore() {
		// Tablature case
		Tablature tab = new Tablature(encodingTestpiece);
		Integer[][] btp = tab.getBasicTabSymbolProperties(); 
		Transcription t1 = new Transcription(midiTestpiece, encodingTestpiece);
		MetricalTimeLine mtl1 = t1.getScorePiece().getMetricalTimeLine();
		List<List<Double>> vl1 = t1.getVoiceLabels();
		List<List<Double>> dl1 = t1.getDurationLabels();
		// Adapt dl of SNU note to have only one duration (only one duration is predicted for SNUs)
		dl1.set(12, LabelTools.createDurationLabel(new Integer[]{Symbol.MINIM.getDuration()}, mtsd));
		// Non-tablature case
		Transcription t2 = new Transcription(midiTestpiece);
		MetricalTimeLine mtl2 = t2.getScorePiece().getMetricalTimeLine();
		Integer[][] bnp2 = t2.getBasicNoteProperties();
		List<List<Double>> vl2 = t2.getVoiceLabels();

		List<NotationSystem> expected = new ArrayList<>();
		// Tablature case, not modelling duration
		NotationSystem expected1 = getNotationSystem(MIDIImport.importMidiFile(midiTestpiece), "testpiece");
		// Set velocity to default
		expected1.getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
			.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
//		// Transpose
//		ScorePiece.transposeNotationSystem(expected1, -2);
		// Set durations to minimum durations
		int whole = 4 * 600000;
		// Voice 0
		expected1.get(0).get(0).get(1).get(0).getScoreNote().setMetricDuration(new Rational(3, 16));
		expected1.get(0).get(0).get(1).get(0).getPerformanceNote().setDuration((long) new Rational(3, 16).mul(whole).toDouble());
		expected1.get(0).get(0).get(2).get(0).getScoreNote().setMetricDuration(new Rational(1, 8));
		expected1.get(0).get(0).get(2).get(0).getPerformanceNote().setDuration((long) new Rational(1, 8).mul(whole).toDouble());
		// Voice 1
		expected1.get(1).get(0).get(1).get(0).getScoreNote().setMetricDuration(new Rational(3, 16));
		expected1.get(1).get(0).get(1).get(0).getPerformanceNote().setDuration((long) new Rational(3, 16).mul(whole).toDouble());
		expected1.get(1).get(0).get(4).get(0).getScoreNote().setMetricDuration(new Rational(1, 8));
		expected1.get(1).get(0).get(4).get(0).getPerformanceNote().setDuration((long) new Rational(1, 8).mul(whole).toDouble());
		expected1.get(1).get(0).get(5).get(0).getScoreNote().setMetricDuration(new Rational(1, 16));
		expected1.get(1).get(0).get(5).get(0).getPerformanceNote().setDuration((long) new Rational(1, 16).mul(whole).toDouble());
		// Voice 2
		expected1.get(2).get(0).get(1).get(0).getScoreNote().setMetricDuration(new Rational(3, 16));
		expected1.get(2).get(0).get(1).get(0).getPerformanceNote().setDuration((long) new Rational(3, 16).mul(whole).toDouble());
		expected1.get(2).get(0).get(2).get(0).getScoreNote().setMetricDuration(new Rational(1, 8));
		expected1.get(2).get(0).get(2).get(0).getPerformanceNote().setDuration((long) new Rational(1, 8).mul(whole).toDouble());
		expected1.get(2).get(0).get(6).get(0).getScoreNote().setMetricDuration(new Rational(1, 16));
		expected1.get(2).get(0).get(6).get(0).getPerformanceNote().setDuration((long) new Rational(1, 16).mul(whole).toDouble());
		// Voice 3
		expected1.get(3).get(0).get(3).get(0).getScoreNote().setMetricDuration(new Rational(1, 8));
		expected1.get(3).get(0).get(3).get(0).getPerformanceNote().setDuration((long) new Rational(1, 8).mul(whole).toDouble());
		expected1.get(3).get(0).get(4).get(0).getScoreNote().setMetricDuration(new Rational(1, 4));
		expected1.get(3).get(0).get(4).get(0).getPerformanceNote().setDuration((long) new Rational(1, 4).mul(whole).toDouble());
		expected1.get(3).get(0).get(5).get(0).getScoreNote().setMetricDuration(new Rational(1, 16));
		expected1.get(3).get(0).get(5).get(0).getPerformanceNote().setDuration((long) new Rational(1, 16).mul(whole).toDouble());
		// Voice 4
		expected1.get(4).get(0).get(3).get(0).getScoreNote().setMetricDuration(new Rational(1, 8));
		expected1.get(4).get(0).get(3).get(0).getPerformanceNote().setDuration((long) new Rational(1, 8).mul(whole).toDouble());
		expected.add(expected1);

		// Tablature case, modelling duration
		NotationSystem expected2 = getNotationSystem(MIDIImport.importMidiFile(midiTestpiece), "testpiece");
		// Set velocity to default
		expected2.getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
			.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
//		// Transpose
//		ScorePiece.transposeNotationSystem(expected2, -2);
		// Adapt SNU note to have only one duration (only one duration is predicted for SNUs)
		expected2.get(1).get(0).get(2).get(0).getScoreNote().setMetricDuration(new Rational(1, 4));
		expected2.get(1).get(0).get(2).get(0).getPerformanceNote().setDuration((long) new Rational(1, 4).mul(whole).toDouble());
		expected.add(expected2);

		// Non-tablature case
		NotationSystem expected3 = getNotationSystem(MIDIImport.importMidiFile(midiTestpiece), "testpiece");
		// Set velocity to default
		expected3.getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
			.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
		expected.add(expected3);

		List<NotationSystem> actual = new ArrayList<>();
		actual.add(ScorePiece.makeScore(btp, null, vl1, null, mtl1, 5));
		actual.add(ScorePiece.makeScore(btp, null, vl1, dl1, mtl1, 5));
		actual.add(ScorePiece.makeScore(null, bnp2, vl2, null, mtl2, 5));

		assertNotationSystemEquality(expected, actual);
	}


	@Test
	public void testCreateNote() {
		// Tablature/non-tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		MetricalTimeLine mtl = sp.getMetricalTimeLine();

		// With mtl and velocity
		List<List<Note>> expected = new ArrayList<>();
		List<Note> expected1 = new ArrayList<>();
		sp.getScore().get(0).get(0).forEach(nc -> expected1.add(nc.get(0)));
		expected.add(expected1);
		// Without mtl and velocity
		List<Note> expected2 = new ArrayList<>();
		expected1.forEach(n -> 
			expected2.add(new Note(n.getScoreNote(), 
			new PerformanceNote(0, (long) n.getMetricDuration().mul(4 * 600000).toDouble(), 
			90, n.getMidiPitch()))));
		expected.add(expected2);

		List<Integer> p = Arrays.asList(
			69, 72, 65, 69, 64, 68, 69, 68, 69, 68, 66, 68, 69, 69
		);
		List<Rational> mt = Arrays.asList(
			new Rational(3, 4),
			new Rational(4, 4),
			new Rational(5, 4),
			new Rational(6, 4),
			new Rational(7, 4),
			new Rational(15, 8),
			new Rational(32, 16),
			new Rational(33, 16),
			new Rational(68, 32),
			new Rational(69, 32),
			new Rational(70, 32),
			new Rational(71, 32),
			new Rational(9, 4),
			new Rational(11, 4)
		);
		List<Rational> md = Arrays.asList(
			Q, Q, Q, Q, E, E, S, S, TH, TH, TH, TH, Q, Q
		);
		List<Integer> v = new ArrayList<>();
		expected1.forEach(n -> v.add(n.getVelocity()));

		List<List<Note>> actual = new ArrayList<>();
		List<Note> actual1 = new ArrayList<>();
		for (int i = 0; i < p.size(); i++) {
			actual1.add(ScorePiece.createNote(p.get(i), mt.get(i), md.get(i), v.get(i), mtl));
		}
		actual.add(actual1);
		List<Note> actual2 = new ArrayList<>();
		for (int i = 0; i < p.size(); i++) {
			actual2.add(ScorePiece.createNote(p.get(i), mt.get(i), md.get(i), -1, null));
		}
		actual.add(actual2);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertTrue(expected.get(i).get(j).isEquivalent(actual.get(i).get(j)));
			}
		}
	}


	@Test
	public void testTransposeHarmonyTrack() {
		// Tablature case
		List<String> pieceNames = Arrays.asList(
			"testpiece", 
			"testGetMeterKeyInfo",
			"in exitu"
		);
		List<ScorePiece> sPieces = Arrays.asList(
			new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), 
			new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), 
			new ScorePiece(MIDIImport.importMidiFile(midiInExitu))
		);
		List<Integer> transpositions = Arrays.asList(2, -2, -5);

		List<SortedContainer<Marker>> expected = new ArrayList<>(); 
		// midiTestpiece (transpose two semitones up)
		// A minor --> B minor (D major)
		SortedContainer<Marker> e1 = getCleanHarmonyTrack(pieceNames.get(0));
		KeyMarker km = (KeyMarker) e1.get(0);
		km.setAlterationNumAndMode(2, km.getMode()); km.setRoot('D'); km.setRootAlteration(0);
		expected.add(e1);
		// midiTestGetMeterKeyInfo (transpose two semitones down)
		// C major --> Bb major; F# minor --> E minor (G major); Bb major --> Ab major;
		// E minor --> D minor (F major)
		SortedContainer<Marker> e2 = getCleanHarmonyTrack(pieceNames.get(1));
		List<Integer> alterationNums = Arrays.asList(-2, 1, -4, -1);
		List<Character> roots = Arrays.asList('B', 'G', 'A', 'F');
		List<Integer> rootAlterations = Arrays.asList(1, 0, 1, 0);
		for (int i = 0; i < e2.size(); i++) {
			km = (KeyMarker) e2.get(i);
			km.setAlterationNumAndMode(alterationNums.get(i), km.getMode()); 
			km.setRoot(roots.get(i)); 
			km.setRootAlteration(rootAlterations.get(i));
		}
		expected.add(e2);
		// In exitu (transpose five semitones down)
		// F major --> C major)
		SortedContainer<Marker> e3 = getCleanHarmonyTrack(pieceNames.get(2));
		km = (KeyMarker) e3.get(0);
		km.setAlterationNumAndMode(0, km.getMode()); km.setRoot('C'); km.setRootAlteration(0);
		expected.add(e3);
		
		List<SortedContainer<Marker>> actual = new ArrayList<>();
		for (int i = 0; i < sPieces.size(); i++) {
			SortedContainer<Marker> ht = sPieces.get(i).getHarmonyTrack();
			actual.add(ScorePiece.transposeHarmonyTrack(ht, transpositions.get(i)));
		}

		assertHarmonyTrackEquality(expected, actual);
	}


	@Test
	public void testTransposeNumAccidentals() {		
		List<Integer> accid = Arrays.asList(0, -3, -2, 3, 2);
		List<Integer> transp = Arrays.asList(-1, -3, 4, -2, 0);

		List<Integer> expected = Arrays.asList(5, 0, 2, 1, 2);
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < accid.size(); i++) {
			actual.add(ScorePiece.transposeNumAccidentals(transp.get(i), accid.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testTransposeNotationSystem() {
		// Tablature case
		Piece p = MIDIImport.importMidiFile(midiTestpiece);
		int transposition = 2;

		NotationSystem expected = getNotationSystem(p, "testpiece");

		NotationSystem actual = ScorePiece.transposeNotationSystem(p.getScore(), transposition);

		assertNotationSystemEquality(Arrays.asList(expected), Arrays.asList(actual));
	}


	@Test
	public void testAlignMetricalTimeLine() {
		// Tablature case
		List<Tablature> tabs = Arrays.asList(
			new Tablature(encodingTestpiece),
			new Tablature(encodingMemorEsto),
			new Tablature(encodingQuiHabitat),
			new Tablature(encodingPreterRerum),
			new Tablature(encodingInExitu)
		);
		List<ScorePiece> sPieces = Arrays.asList(
			new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), 
			new ScorePiece(MIDIImport.importMidiFile(midiMemorEsto)), 
			new ScorePiece(MIDIImport.importMidiFile(midiQuiHabitat)), 
			new ScorePiece(MIDIImport.importMidiFile(midiPreterRerum)), 
			new ScorePiece(MIDIImport.importMidiFile(midiInExitu))
		);

		List<MetricalTimeLine> expected = new ArrayList<>();
		// One meter section, none added
		MetricalTimeLine mtlTestpiece = getCleanMetricalTimeLine("testpiece");
		expected.add(mtlTestpiece);
		// One meter section, six added (to end)
		MetricalTimeLine mtlMemorEsto = getCleanMetricalTimeLine("memor esto");
		long s1a = getMeterSectionLength("memor esto", "1a");
		long s1b = getMeterSectionLength("memor esto", "1b");
		long s1c = getMeterSectionLength("memor esto", "1c");
		long s1d = getMeterSectionLength("memor esto", "1d");
		long s1e = getMeterSectionLength("memor esto", "1e");
		long s1f = getMeterSectionLength("memor esto", "1f");
		// The original endMarker must be removed: adding a new TempoMarker 
		// *to the end* of the mtl renders it obsolete
		mtlMemorEsto.remove(mtlMemorEsto.size()-1);
		// Add TimeSignatureMarker + TempoMarker (meter section 1b, mt = 126/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(126, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a, new Rational(126, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 1c, mt = 148/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(148, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a + s1b, new Rational(148, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 1d, mt = 198/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(198, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a + s1b + s1c, new Rational(198, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 1e, mt = 204/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(204, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a + s1b + s1c + s1d, new Rational(204, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 1f, mt = 216/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(216, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a + s1b + s1c + s1d + s1e, new Rational(216, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 1g, mt = 226/1)
		mtlMemorEsto.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(226, 1)));
		mtlMemorEsto.add(new TempoMarker(s1a + s1b + s1c + s1d + s1e + s1f, new Rational(226, 1)));		
		// Adapt endMarker (added through last TempoMarker) (mt = 236/1)
		TimedMetrical em = (TimedMetrical) mtlMemorEsto.get(mtlMemorEsto.size()-1);
		em.setMetricTime(new Rational(236, 1));
		em.setTime(s1a + s1b + s1c + s1d + s1e + s1f + TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289));
		expected.add(mtlMemorEsto);
		// Two meter sections, none added
		MetricalTimeLine mtlQuiHabitat = getCleanMetricalTimeLine("qui habitat");
		expected.add(mtlQuiHabitat);
		// Three meter sections, none added
		MetricalTimeLine mtlPreterRerum = getCleanMetricalTimeLine("preter rerum");
		expected.add(mtlPreterRerum);
		// Seven meter sections, two added (to middle)
		MetricalTimeLine mtlInExitu = getCleanMetricalTimeLine("in exitu");
		long s1 = getMeterSectionLength("in exitu", "1");
		long s2 = getMeterSectionLength("in exitu", "2");
		long s3	= getMeterSectionLength("in exitu", "3");
		long s4	= getMeterSectionLength("in exitu", "4");
		long s5a = getMeterSectionLength("in exitu", "5a");
		long s5b = getMeterSectionLength("in exitu", "5b");
		// The original endMarker need not be removed: adding a new TempoMarker 
		// *to the middle* of the mtl does not render it obsolete
		//
		// Add TimeSignatureMarker + TempoMarker (meter section 5b, mt = 192/1)
		mtlInExitu.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(192, 1)));
		mtlInExitu.add(new TempoMarker(s1 + s2 + s3 + s4 + s5a, new Rational(192, 1)));
		// Add TimeSignatureMarker + TempoMarker (meter section 5c, mt = 204/1)
		mtlInExitu.add(new TimeSignatureMarker(new TimeSignature(TWO_ONE), new Rational(204, 1)));
		mtlInExitu.add(new TempoMarker(s1 + s2 + s3 + s4 + s5a + s5b, new Rational(204, 1)));
		expected.add(mtlInExitu);

		List<MetricalTimeLine> actual = new ArrayList<>();
		for (int i = 0; i < sPieces.size(); i++) {
			MetricalTimeLine mtl = sPieces.get(i).getMetricalTimeLine();
			actual.add(ScorePiece.alignMetricalTimeLine(mtl, tabs.get(i).getMeterInfo()));
		}

		assertMetricalTimeLineEquality(expected, actual);
	}


	@Test
	public void testDiminuteMetricalTimeLine() {
		// Tablature case
		List<Tablature> tabs = Arrays.asList(
			new Tablature(encodingTestpiece),
			new Tablature(encodingMemorEsto),
			new Tablature(encodingQuiHabitat),
			new Tablature(encodingPreterRerum),
			new Tablature(encodingInExitu)
		);
		List<ScorePiece> sPieces = Arrays.asList(
			new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), 
			new ScorePiece(MIDIImport.importMidiFile(midiMemorEsto)), 
			new ScorePiece(MIDIImport.importMidiFile(midiQuiHabitat)), 
			new ScorePiece(MIDIImport.importMidiFile(midiPreterRerum)), 
			new ScorePiece(MIDIImport.importMidiFile(midiInExitu))
		);

		List<MetricalTimeLine> expected = new ArrayList<>();
		// One meter section, diminution = 1
		MetricalTimeLine mtlTestpiece = getCleanMetricalTimeLine("testpiece");
		mtlTestpiece = ScorePiece.alignMetricalTimeLine(mtlTestpiece, tabs.get(0).getMeterInfo());
		expected.add(mtlTestpiece);
		// Seven meter sections, diminutions = 2, 4, 2, 4, 2, 4, 2
		MetricalTimeLine mtlMemorEsto = getCleanMetricalTimeLine("memor esto");
		mtlMemorEsto = ScorePiece.alignMetricalTimeLine(mtlMemorEsto, tabs.get(1).getMeterInfo());
		long s1a = getMeterSectionLength("memor esto", "1a");
		long s1b = getMeterSectionLength("memor esto", "1b");
		long s1c = getMeterSectionLength("memor esto", "1c");
		long s1d = getMeterSectionLength("memor esto", "1d");
		long s1e = getMeterSectionLength("memor esto", "1e");
		long s1f = getMeterSectionLength("memor esto", "1f");
		// Adapt TimeSignatureMarker (meter section 1, mt = 0/1)
		mtlMemorEsto.getTimeSignatureMarker(Rational.ZERO).setTimeSignature(new TimeSignature(TWO_TWO));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1b, mt = (126/1)/2 = 126/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(126, 1)).setTimeSignature(new TimeSignature(TWO_FOUR));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(126, 1)).setMetricTime(new Rational(126, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(126, 1)).setMetricTime(new Rational(126, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1c, mt = 126/2 + ((148-126)/1)/4 = 137/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(148, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(148, 1)).setMetricTime(new Rational(137, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(148, 1)).setMetricTime(new Rational(137, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1d, mt = 137/2 + ((198-148)/1)/2 = 187/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(198, 1)).setTimeSignature(new TimeSignature(TWO_FOUR));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(198, 1)).setMetricTime(new Rational(187, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(198, 1)).setMetricTime(new Rational(187, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1e, mt = 187/2 + ((204-198)/1)/4 = 190/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(204, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(204, 1)).setMetricTime(new Rational(190, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(204, 1)).setMetricTime(new Rational(190, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1f, mt = 190/2 + ((216-204)/1)/2 = 202/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(216, 1)).setTimeSignature(new TimeSignature(TWO_FOUR));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(216, 1)).setMetricTime(new Rational(202, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(216, 1)).setMetricTime(new Rational(202, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 1g, mt = 202/2 + ((226-216)/1)/4 = 207/2)
		mtlMemorEsto.getTimeSignatureMarker(new Rational(226, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlMemorEsto.getTimeSignatureMarker(new Rational(226, 1)).setMetricTime(new Rational(207, 2));
		mtlMemorEsto.getTimedMetrical(new Rational(226, 1)).setMetricTime(new Rational(207, 2));
		// Adapt endMarker (mt = 207/2 + (10/1)/2) = 217/2)
		TimedMetrical em = (TimedMetrical) mtlMemorEsto.get(mtlMemorEsto.size()-1);
		em.setMetricTime(new Rational(217, 2));
		em.setTime(s1a + s1b + s1c + s1d + s1e + s1f + TimeMeterTools.calculateTime(new Rational(10, 1).div(2), TimeMeterToolsTest.T_289/2));
		expected.add(mtlMemorEsto);		
		// Two meter sections, diminutions = 4, 2
		MetricalTimeLine mtlQuiHabitat = getCleanMetricalTimeLine("qui habitat");
		mtlQuiHabitat = ScorePiece.alignMetricalTimeLine(mtlQuiHabitat, tabs.get(2).getMeterInfo());
		long s1 = getMeterSectionLength("qui habitat", "1");
		// Adapt TimeSignatureMarker (meter section 1, mt = 0/1)
		mtlQuiHabitat.getTimeSignatureMarker(Rational.ZERO).setTimeSignature(new TimeSignature(THREE_FOUR));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 2, mt = (75/1)/4 = 75/4)
		mtlQuiHabitat.getTimeSignatureMarker(new Rational(75, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlQuiHabitat.getTimeSignatureMarker(new Rational(75, 1)).setMetricTime(new Rational(75, 4));
		mtlQuiHabitat.getTimedMetrical(new Rational(75, 1)).setMetricTime(new Rational(75, 4));
		// Adapt endMarker (mt = 75/4 + (10/1)/2 = 95/4)
		TimedMetrical emQuiHabitat = (TimedMetrical) mtlQuiHabitat.get(mtlQuiHabitat.size()-1);
		emQuiHabitat.setMetricTime(new Rational(95, 4));
		emQuiHabitat.setTime(s1 + TimeMeterTools.calculateTime(new Rational(10, 1).div(2), TimeMeterToolsTest.T_289/2));
		expected.add(mtlQuiHabitat);
		// Three meter sections, diminutions = 1, 2, 1
		MetricalTimeLine mtlPreterRerum = getCleanMetricalTimeLine("preter rerum");
		mtlPreterRerum = ScorePiece.alignMetricalTimeLine(mtlPreterRerum, tabs.get(3).getMeterInfo());
		s1 = getMeterSectionLength("preter rerum", "1");
		long s2 = getMeterSectionLength("preter rerum", "2");
		// Adapt TimeSignatureMarker (meter section 1, mt = 0/1)
//		mtlPreterRerum.getTimeSignatureMarker(Rational.ZERO).setTimeSignature(new TimeSignature(twoOne));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 2, mt = 104/1)
		mtlPreterRerum.getTimeSignatureMarker(new Rational(104, 1)).setTimeSignature(new TimeSignature(THREE_TWO));
//		mtlPreterRerum.getTimeSignatureMarker(new Rational(104, 1)).setMetricTime(new Rational(104, 1));
//		mtlPreterRerum.getTimedMetrical(new Rational(104, 1)).setMetricTime(new Rational(104, 1));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 3, mt = 104/1 + ((212-104)/1)/2 = 158/1)
//		mtlPreterRerum.getTimeSignatureMarker(new Rational(212, 1)).setTimeSignature(new TimeSignature(twoOne));
		mtlPreterRerum.getTimeSignatureMarker(new Rational(212, 1)).setMetricTime(new Rational(158, 1));
		mtlPreterRerum.getTimedMetrical(new Rational(212, 1)).setMetricTime(new Rational(158, 1));
		// Adapt endMarker (mt = 158/1 + (10/1)/1 = 168/1)
		em = (TimedMetrical) mtlPreterRerum.get(mtlPreterRerum.size()-1);
		em.setMetricTime(new Rational(168, 1));
		em.setTime(s1 + s2 + TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_289));
		expected.add(mtlPreterRerum);
		// Nine meter sections, diminutions = 2, 4, 2, 4, 2, 4, 2, 4, 2
		MetricalTimeLine mtlInExitu = getCleanMetricalTimeLine("in exitu");
		mtlInExitu = ScorePiece.alignMetricalTimeLine(mtlInExitu, tabs.get(4).getMeterInfo());
		s1 = getMeterSectionLength("in exitu", "1");
		s2 = getMeterSectionLength("in exitu", "2");
		long s3 = getMeterSectionLength("in exitu", "3");
		long s4 = getMeterSectionLength("in exitu", "4");
		long s5a = getMeterSectionLength("in exitu", "5a");
		long s5b = getMeterSectionLength("in exitu", "5b");
		long s5c = getMeterSectionLength("in exitu", "5c");
		long s6 = getMeterSectionLength("in exitu", "6");
		// Adapt TimeSignatureMarker (meter section 1, mt = 0/1)
		mtlInExitu.getTimeSignatureMarker(Rational.ZERO).setTimeSignature(new TimeSignature(TWO_TWO));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 2, mt = (28/1)/2 = 14/1)
		mtlInExitu.getTimeSignatureMarker(new Rational(28, 1)).setTimeSignature(new TimeSignature(THREE_FOUR));
		mtlInExitu.getTimeSignatureMarker(new Rational(28, 1)).setMetricTime(new Rational(14, 1));
		mtlInExitu.getTimedMetrical(new Rational(28, 1)).setMetricTime(new Rational(14, 1));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 3, mt = 14/1 + ((76-28)/1)/4 = 26/1)
		mtlInExitu.getTimeSignatureMarker(new Rational(76, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlInExitu.getTimeSignatureMarker(new Rational(76, 1)).setMetricTime(new Rational(26, 1));
		mtlInExitu.getTimedMetrical(new Rational(76, 1)).setMetricTime(new Rational(26, 1));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 4, mt = 26/1 + ((90-76/1))/2 = 33/1)
		mtlInExitu.getTimeSignatureMarker(new Rational(90, 1)).setTimeSignature(new TimeSignature(THREE_FOUR));
		mtlInExitu.getTimeSignatureMarker(new Rational(90, 1)).setMetricTime(new Rational(33, 1));
		mtlInExitu.getTimedMetrical(new Rational(90, 1)).setMetricTime(new Rational(33, 1));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 5a, mt = 33/1 + ((180-90)/1)/4 = 111/2)
		mtlInExitu.getTimeSignatureMarker(new Rational(180, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlInExitu.getTimeSignatureMarker(new Rational(180, 1)).setMetricTime(new Rational(111, 2));
		mtlInExitu.getTimedMetrical(new Rational(180, 1)).setMetricTime(new Rational(111, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 5b, mt = 111/2 + ((192-180)/1)/2 = 123/2)
		mtlInExitu.getTimeSignatureMarker(new Rational(192, 1)).setTimeSignature(new TimeSignature(TWO_FOUR));
		mtlInExitu.getTimeSignatureMarker(new Rational(192, 1)).setMetricTime(new Rational(123, 2));
		mtlInExitu.getTimedMetrical(new Rational(192, 1)).setMetricTime(new Rational(123, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 5c, mt = 123/2 + ((204-192)/1)/4 = 129/2)
		mtlInExitu.getTimeSignatureMarker(new Rational(204, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlInExitu.getTimeSignatureMarker(new Rational(204, 1)).setMetricTime(new Rational(129, 2));
		mtlInExitu.getTimedMetrical(new Rational(204, 1)).setMetricTime(new Rational(129, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 6, mt = 129/2 + ((230-204)/1)/2 = 155/2)
		mtlInExitu.getTimeSignatureMarker(new Rational(230, 1)).setTimeSignature(new TimeSignature(THREE_FOUR));
		mtlInExitu.getTimeSignatureMarker(new Rational(230, 1)).setMetricTime(new Rational(155, 2));
		mtlInExitu.getTimedMetrical(new Rational(230, 1)).setMetricTime(new Rational(155, 2));
		// Adapt TimeSignatureMarker + TempoMarker (meter section 7, mt = 155/2 + ((248-230)/1)/4 = 82/1)
		mtlInExitu.getTimeSignatureMarker(new Rational(248, 1)).setTimeSignature(new TimeSignature(TWO_TWO));
		mtlInExitu.getTimeSignatureMarker(new Rational(248, 1)).setMetricTime(new Rational(82, 1));
		mtlInExitu.getTimedMetrical(new Rational(248, 1)).setMetricTime(new Rational(82, 1));
		// Adapt endMarker (mt = 82/1 + (10/1)/2 = 87/1)
		em = (TimedMetrical) mtlInExitu.get(mtlInExitu.size()-1);
		em.setMetricTime(new Rational(87, 1));
		em.setTime(s1 + s2 + s3 + s4 + s5a + s5b + s5c + s6 + TimeMeterTools.calculateTime(new Rational(10, 1).div(2), TimeMeterToolsTest.T_289/2));
		expected.add(mtlInExitu);

		List<MetricalTimeLine> actual = new ArrayList<>();
		for (int i = 0; i < sPieces.size(); i++) {			
			List<Integer[]> mi = tabs.get(i).getMeterInfo();
			MetricalTimeLine mtl = sPieces.get(i).getMetricalTimeLine();
			mtl = ScorePiece.alignMetricalTimeLine(mtl, mi);
			actual.add(ScorePiece.diminuteMetricalTimeLine(mtl, mi));
		}

		assertMetricalTimeLineEquality(expected, actual);
	}


	@Test
	public void testDiminuteHarmonyTrack() {
		// Tablature case
		List<String> pieceNames = Arrays.asList(
			"testpiece", 
			"testGetMeterKeyInfo",
			"in exitu"
		);

		List<Tablature> tabs = Arrays.asList(
			new Tablature(encodingTestpiece),
			new Tablature(encodingTestGetMeterInfo),
			new Tablature(encodingInExitu)
		);

		List<ScorePiece> sPieces = Arrays.asList(
			new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)), 
			new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo)), 
			new ScorePiece(MIDIImport.importMidiFile(midiInExitu))
		);

		List<SortedContainer<Marker>> expected = new ArrayList<>(); 
		// midiTestpiece (no diminution)
		SortedContainer<Marker> e1 = getCleanHarmonyTrack(pieceNames.get(0));
		expected.add(e1);
		// midiTestGetMeterKeyInfo (diminutions 2, 2, 4, 1, 1, -2)
		SortedContainer<Marker> e2 = getCleanHarmonyTrack(pieceNames.get(1));
		List<Rational> metricTimes = Arrays.asList(
			Rational.ZERO, 
			new Rational(19, 8),
			new Rational(31, 8), // was 31/8
			new Rational(47, 8) // was 47/8
		);
		for (int i = 0; i < e2.size(); i++) {
			KeyMarker km = (KeyMarker) e2.get(i);
			km.setMetricTime(metricTimes.get(i));
		}
		expected.add(e2);
		// In exitu (diminutions 2, 4, 2, 4, 2, 4, 2, 4, 2)
		SortedContainer<Marker> e3 = getCleanHarmonyTrack(pieceNames.get(2));
		expected.add(e3);

		List<SortedContainer<Marker>> actual = new ArrayList<>();
		for (int i = 0; i < sPieces.size(); i++) {
			List<Integer[]> mi = tabs.get(i).getMeterInfo();
			MetricalTimeLine mtl = sPieces.get(i).getMetricalTimeLine();
			mtl = ScorePiece.alignMetricalTimeLine(mtl, mi);
			ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(mtl);
			MetricalTimeLine mtlDim = ScorePiece.diminuteMetricalTimeLine(mtl, mi);
			ScoreMetricalTimeLine smtlDim = new ScoreMetricalTimeLine(mtlDim);
			SortedContainer<Marker> ht = sPieces.get(i).getHarmonyTrack();
			actual.add(ScorePiece.diminuteHarmonyTrack(ht, mi, smtl, smtlDim));
		}

		assertHarmonyTrackEquality(expected, actual);	
	}


	@Test
	public void testDiminuteNotationSystem() {
		// Tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		Tablature t = new Tablature(encodingTestGetMeterInfo);
		MetricalTimeLine mtl = sp.getMetricalTimeLine();
		mtl = ScorePiece.alignMetricalTimeLine(mtl, t.getMeterInfo());
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(mtl);
		MetricalTimeLine mtlDim = ScorePiece.diminuteMetricalTimeLine(mtl, t.getMeterInfo());
		ScoreMetricalTimeLine smtlDim = new ScoreMetricalTimeLine(mtlDim);
		
		NotationSystem expected = new NotationSystem();
		List<Note> unhandled = TranscriptionTest.getUnhandledNotesFromPiece(sp, "testGetMeterKeyInfo");
		// Voice 0
		NotationStaff ns0 = new NotationStaff();
		NotationVoice nv0 = new NotationVoice();
		ScorePitch sp0 = new ScorePitch(69);
		List<Note> notesV0 = Arrays.asList(
			new Note(new ScoreNote(sp0, Rational.ZERO, E), unhandled.get(0).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(1, 8), E), unhandled.get(1).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(1, 4), E), unhandled.get(2).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(3, 8), DQ), unhandled.get(4).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(6, 8), E), unhandled.get(5).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(7, 8), H), unhandled.get(7).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(11, 8), Q), unhandled.get(9).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(13, 8), S), unhandled.get(10).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(27, 16), S), unhandled.get(11).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(14, 8), TH), unhandled.get(12).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(57, 32), TH), unhandled.get(13).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(29, 16), TH), unhandled.get(14).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(59, 32), TH), unhandled.get(15).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(15, 8), H), unhandled.get(17).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(19, 8), Q), unhandled.get(19).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(21, 8), E), unhandled.get(20).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(22, 8), S), unhandled.get(21).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(45, 16), S), unhandled.get(22).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(23, 8), Q), unhandled.get(24).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(25, 8), DE), unhandled.get(25).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(53, 16), TH), unhandled.get(26).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(107, 32), TH), unhandled.get(27).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(27, 8), H), unhandled.get(29).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(31, 8), H), unhandled.get(31).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(35, 8), H), unhandled.get(33).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(39, 8), E), unhandled.get(34).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(40, 8), E), unhandled.get(35).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(41, 8), E), unhandled.get(36).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(42, 8), E), unhandled.get(37).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(43, 8), E), unhandled.get(38).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(44, 8), E), unhandled.get(39).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(45, 8), Q), unhandled.get(41).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp0, new Rational(47, 8), E), unhandled.get(42).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(96, 16), S), unhandled.get(43).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(97, 16), S), unhandled.get(44).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(98, 16), S), unhandled.get(45).getPerformanceNote()),
			// 
			new Note(new ScoreNote(sp0, new Rational(99, 16), H), unhandled.get(47).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(107, 16), E), unhandled.get(48).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(109, 16), S), unhandled.get(49).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(110, 16), S), unhandled.get(50).getPerformanceNote()),
			new Note(new ScoreNote(sp0, new Rational(111, 16), E), unhandled.get(52).getPerformanceNote())
		);
		notesV0.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv0.add(nc);
		});
		ns0.add(nv0);
		expected.add(ns0);
		// Voice 1
		NotationStaff ns1 = new NotationStaff();
		NotationVoice nv1 = new NotationVoice();
		ScorePitch sp1 = new ScorePitch(64);
		List<Note> notesV1 = Arrays.asList(
			new Note(new ScoreNote(sp1, new Rational(3, 8), H), unhandled.get(3).getPerformanceNote()),
			new Note(new ScoreNote(sp1, new Rational(7, 8), H), unhandled.get(6).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp1, new Rational(11, 8), H), unhandled.get(8).getPerformanceNote()),
			new Note(new ScoreNote(sp1, new Rational(15, 8), H), unhandled.get(16).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp1, new Rational(19, 8), H), unhandled.get(18).getPerformanceNote()),
			new Note(new ScoreNote(sp1, new Rational(23, 8), H), unhandled.get(23).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp1, new Rational(27, 8), H), unhandled.get(28).getPerformanceNote()),
			//	
			new Note(new ScoreNote(sp1, new Rational(31, 8), H), unhandled.get(30).getPerformanceNote()),	
			new Note(new ScoreNote(sp1, new Rational(35, 8), H), unhandled.get(32).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp1, new Rational(45, 8), Q), unhandled.get(40).getPerformanceNote()),
			//
			new Note(new ScoreNote(sp1, new Rational(99, 16), H), unhandled.get(46).getPerformanceNote()),
			new Note(new ScoreNote(sp1, new Rational(111, 16), Q), unhandled.get(51).getPerformanceNote())
		);
		notesV1.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv1.add(nc);
		});
		ns1.add(nv1);
		expected.add(ns1);

		NotationSystem actual = 
			ScorePiece.diminuteNotationSystem(sp.getScore(), t.getMeterInfo(), smtl, smtlDim);

		assertNotationSystemEquality(Arrays.asList(expected), Arrays.asList(actual));		
	}


	@Test
	public void testAddNote() {
		// Tablature/non-tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		MetricalTimeLine mtl = sp.getMetricalTimeLine();
		int v = 1;
		NotationVoice voice = sp.getScore().get(v).get(0);
		List<Integer> velocities = new ArrayList<>();
		voice.forEach(nc -> velocities.add(nc.get(0).getVelocity()));
		// Add additional NotationChords with different durs at mt 8/4 and 11/4
		// NB: NotationChords appear in a NotationVoice in the reverse order in which they are added;
		// Notes appear in a NotationChord in the order in which they are added 
		voice.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl));
		voice.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 8), velocities.get(6), mtl));

		Note nAddedByMethodAt34 = ScorePiece.createNote(
			69, new Rational(3, 4), new Rational(1, 4), velocities.get(0), mtl);
		Note nAddedByMethodAt98 = ScorePiece.createNote(
			69, new Rational(9, 8), new Rational(1, 8), 90, mtl);
		Note nAddedByMethodAtAt84 = ScorePiece.createNote(
			69, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl);
		Note nAddedByMethodAt114 = ScorePiece.createNote(
			69, new Rational(11, 4), new Rational(1, 16), velocities.get(6), mtl);

		// 1. Add note at mt where there is no NotationChord (mt = 9/8)
		// 2. Add note at mt where there are one or more NotationChords, and
		//    a. A NotationChord with the same duration as the note (mt = 3/4, 8/4) 
		//    b. No NotationChord with the same duration as the note (mt = 11/4)
		List<NotationChord> expected = new ArrayList<>();
		// mt = 3/4
		// original nc at this mt --> addNote() adds to this
		NotationChord nc0 = new NotationChord(); 
		nc0.add(ScorePiece.createNote(65, new Rational(3, 4), new Rational(1, 4), velocities.get(0), mtl));
		nc0.add(nAddedByMethodAt34);
		expected.add(nc0);
		// mt = 4/4
		NotationChord nc1 = new NotationChord();
		nc1.add(ScorePiece.createNote(69, new Rational(4, 4), new Rational(1, 8), velocities.get(1), mtl));
		expected.add(nc1);
		// mt = 9/8
		// nc added by method at this mt --> addNote() adds to this
		NotationChord nc1a = new NotationChord();
		nc1a.add(nAddedByMethodAt98);
		expected.add(nc1a);
		// mt = 5/4
		NotationChord nc2 = new NotationChord();
		nc2.add(ScorePiece.createNote(65, new Rational(5, 4), new Rational(1, 8), velocities.get(2), mtl));
		expected.add(nc2);
		// mt = 6/4
		NotationChord nc3 = new NotationChord();
		nc3.add(ScorePiece.createNote(60, new Rational(6, 4), new Rational(1, 4), velocities.get(3), mtl));
		expected.add(nc3);
		// mt = 7/4
		NotationChord nc4 = new NotationChord();
		nc4.add(ScorePiece.createNote(69, new Rational(7, 4), new Rational(1, 4), velocities.get(4), mtl));
		expected.add(nc4);
		// mt = 8/4
		// First: nc added to have two ncs at this mt --> addNote() adds to this 
		NotationChord nc5Added = new NotationChord();
		nc5Added.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl));
		nc5Added.add(nAddedByMethodAtAt84);
		expected.add(nc5Added);
		// Second: original nc at this mt 
		NotationChord nc5 = new NotationChord();
		nc5.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 2), velocities.get(5), mtl));
		expected.add(nc5);
		// mt = 11/4
		// First: nc added by method at this mt --> addNote() adds to this
		NotationChord nc6AddedByMethodAt114 = new NotationChord();
		nc6AddedByMethodAt114.add(nAddedByMethodAt114);
		expected.add(nc6AddedByMethodAt114);
		// Second: nc added to have two ncs at this mt
		NotationChord nc6Added = new NotationChord();
		nc6Added.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 8), velocities.get(6), mtl));
		expected.add(nc6Added);
		// Third: original nc at this mt
		NotationChord nc6 = new NotationChord();
		nc6.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 4), velocities.get(6), mtl));
		expected.add(nc6);
		// Set channel to v
		for (NotationChord nc : expected) {
			for (Note n : nc) {
				MidiNote.convert(n.getPerformanceNote()).setChannel(v);
			}
		}

		List<NotationChord> actual = new ArrayList<>();
		sp.addNote(nAddedByMethodAt34, 1);
		sp.addNote(nAddedByMethodAt98, 1);
		sp.addNote(nAddedByMethodAtAt84, 1);
		sp.addNote(nAddedByMethodAt114, 1);
		voice = sp.getScore().get(v).get(0);
		voice.forEach(nc -> actual.add(nc));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertTrue(expected.get(i).get(j).isEquivalent(actual.get(i).get(j)));
			}
		}

//		// Visualise
//		JFrame transcriptionFrame = visualise(t, "test_add_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - b, d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);     
	}


	@Test
	public void testRemoveNote() {
		// Tablature/non-tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		MetricalTimeLine mtl = sp.getMetricalTimeLine();
		int v = 1;
		NotationVoice voice = sp.getScore().get(v).get(0);		
		List<Integer> velocities = new ArrayList<>();
		voice.forEach(nc -> velocities.add(nc.get(0).getVelocity()));
		// Add additional NotationChords with different durs at mt 8/4 and 11/4
		// NB: NotationChords appear in a NotationVoice in the reverse order in which they are added;
		// Notes appear in a NotationChord in the order in which they are added
		voice.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl));
		voice.add(ScorePiece.createNote(69, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl));
		voice.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 8), velocities.get(6), mtl));

		Note nRemovedByMethodAt34 = ScorePiece.createNote(
			65, new Rational(3, 4), new Rational(1, 4), velocities.get(0), mtl);
		Note nRemovedByMethodAt98 = ScorePiece.createNote(
			69, new Rational(9, 8), new Rational(1, 8), 90, mtl);
		Note nRemovedByMethodAt84 = ScorePiece.createNote(
			69, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl);
		Note nRemovedByMethodAt114 = ScorePiece.createNote(
			69, new Rational(11, 4), new Rational(1, 16), velocities.get(6), mtl);

		// 1. Remove note from mt where there is no NotationChord (i.e., do nothing) (mt = 9/8)
		// 2. Remove note from mt where there are one or more NotationChords, and
		//    a. A NotationChord with the same duration as the note 
		//       - with one note (mt = 3/4)
		//       - with two notes (mt = 8/4)
		//	  b. No NotationChord with the same duration as the note (i.e., do nothing) (mt = 11/4)		
		List<NotationChord> expected = new ArrayList<>();
		// mt = 3/4
		// original nc at this mt --> removeNote() removes from this
		// mt = 4/4
		NotationChord nc1 = new NotationChord();
		nc1.add(ScorePiece.createNote(69, new Rational(4, 4), new Rational(1, 8), velocities.get(1), mtl));
		expected.add(nc1);
		// mt = 9/8
		// (do nothing)
		// mt = 5/4
		NotationChord nc2 = new NotationChord();
		nc2.add(ScorePiece.createNote(65, new Rational(5, 4), new Rational(1, 8), velocities.get(2), mtl));
		expected.add(nc2);
		// mt = 6/4
		NotationChord nc3 = new NotationChord();
		nc3.add(ScorePiece.createNote(60, new Rational(6, 4), new Rational(1, 4), velocities.get(3), mtl));
		expected.add(nc3);
		// mt = 7/4
		NotationChord nc4 = new NotationChord();
		nc4.add(ScorePiece.createNote(69, new Rational(7, 4), new Rational(1, 4), velocities.get(4), mtl));
		expected.add(nc4);
		// mt = 8/4
		// First: nc added to have two ncs at this mt --> removeNote() removes from this
		NotationChord nc5Added = new NotationChord();
		nc5Added.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 4), velocities.get(5), mtl));
		expected.add(nc5Added);
		// Second: original nc at this mt
		NotationChord nc5 = new NotationChord();
		nc5.add(ScorePiece.createNote(64, new Rational(8, 4), new Rational(1, 2), velocities.get(5), mtl));
		expected.add(nc5);
		// mt = 11/4
		// (do nothing)
		// First: nc added to have two ncs at this mt 
		NotationChord nc6Added = new NotationChord();
		nc6Added.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 8), velocities.get(6), mtl));
		expected.add(nc6Added);
		// Second: original nc at this mt
		NotationChord nc6 = new NotationChord();
		nc6.add(ScorePiece.createNote(64, new Rational(11, 4), new Rational(1, 4), velocities.get(6), mtl));
		expected.add(nc6);
		// Set channel to v
		for (NotationChord nc : expected) {
			for (Note n : nc) {
				MidiNote.convert(n.getPerformanceNote()).setChannel(v);
			}
		}

		List<NotationChord> actual = new ArrayList<>();
		sp.removeNote(nRemovedByMethodAt34.getMidiPitch(), nRemovedByMethodAt34.getMetricTime(), 
			nRemovedByMethodAt34.getMetricDuration(), v);
		sp.removeNote(nRemovedByMethodAt98.getMidiPitch(), nRemovedByMethodAt98.getMetricTime(), 
			nRemovedByMethodAt98.getMetricDuration(), v);
		sp.removeNote(nRemovedByMethodAt84.getMidiPitch(), nRemovedByMethodAt84.getMetricTime(), 
			nRemovedByMethodAt84.getMetricDuration(), v);
		sp.removeNote(nRemovedByMethodAt114.getMidiPitch(), nRemovedByMethodAt114.getMetricTime(), 
			nRemovedByMethodAt114.getMetricDuration(), v);
		voice = sp.getScore().get(v).get(0);
		voice.forEach(nc -> actual.add(nc));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertTrue(expected.get(i).get(j).isEquivalent(actual.get(i).get(j)));
			}
		}

//		// Visualise
//		JFrame transcriptionFrame = visualise(transcription, "test_remove_note");
//		int answer = JOptionPane.showOptionDialog(transcriptionFrame, "Event 1 = G - g - d' - g'?", "Confirm", 
//			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//		assertEquals(answer, JOptionPane.YES_OPTION);
	}


	@Test
	public void testCompleteDurations() {
		// Tablature/non-tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		MetricalTimeLine mtl = sp.getMetricalTimeLine();

		// Expected: connect all unconnected notes
		ScorePiece expected = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		NotationSystem nsE = expected.getScore();
		// Voice 0, note 12
		NotationVoice nv0 = nsE.get(0).get(0);
		NotationChord v0nc12 = nv0.get(12);
		NotationChord v0nc12E = new NotationChord();
		v0nc12E.add(ScorePiece.createNote(
			v0nc12.get(0).getMidiPitch(), v0nc12.get(0).getMetricTime(), H, v0nc12.get(0).getVelocity(), mtl));
		nv0.remove(v0nc12);
		nv0.add(v0nc12E);
		// Voice 1, note 1
		NotationVoice nv1 = nsE.get(1).get(0);
		NotationChord v1nc1 = nv1.get(1);
		NotationChord v1nc1E = new NotationChord();
		v1nc1E.add(ScorePiece.createNote(
			v1nc1.get(0).getMidiPitch(), v1nc1.get(0).getMetricTime(), Q, v1nc1.get(0).getVelocity(), mtl));
		nv1.remove(v1nc1);
		nv1.add(v1nc1E);
		// Voice 1, note 2
		NotationChord v1nc2 = nv1.get(2);
		NotationChord v1nc2E = new NotationChord();
		v1nc2E.add(ScorePiece.createNote(
			v1nc2.get(0).getMidiPitch(), v1nc2.get(0).getMetricTime(), Q, v1nc2.get(0).getVelocity(), mtl));
		nv1.remove(v1nc2);
		nv1.add(v1nc2E);

		// Actual: set all Notes to have a duration of 1/32
		ScorePiece actual = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		NotationSystem nsA = actual.getScore();
		for (int i = 0; i < nsA.size(); i++) {
			NotationVoice currNv = nsA.get(i).get(0);
			for (int j = 0; j < currNv.size()-1; j++) {
				NotationChord currNc = currNv.get(j);
				NotationChord currNcA = new NotationChord();
				currNcA.add(ScorePiece.createNote(
					currNc.get(0).getMidiPitch(), currNc.get(0).getMetricTime(), TH, currNc.get(0).getVelocity(), mtl));
				currNv.remove(currNc);
				currNv.add(currNcA);
			}
		}
		actual.completeDurations(H);

		assertPieceEquality(Arrays.asList(expected), Arrays.asList(actual));
	}


	@Test
	public void testAugmentMetricalTimeLine() {
		// Tablature/non-tablature case
		Transcription t1 = new Transcription(midiTestGetMeterKeyInfo); // reverse
		ScorePiece sp1 = t1.getScorePiece();
		Transcription t2 = new Transcription(midiTestGetMeterKeyInfo); // rescale (rescaleFactor = -2)
		ScorePiece sp2 = t2.getScorePiece();
		Transcription t3 = new Transcription(midiTestGetMeterKeyInfo); // rescale (rescaleFactor = 2; to test x/1 meters) 
		ScorePiece sp3 = t3.getScorePiece();

		List<MetricalTimeLine> expected = new ArrayList<>();
		MetricalTimeLine expected1 = new MetricalTimeLine();
		expected1.clear();
		long s1Rev = getMeterSectionLength("testGetMeterKeyInfo", "6");
		long s2Rev = getMeterSectionLength("testGetMeterKeyInfo", "5");
		long s3Rev = getMeterSectionLength("testGetMeterKeyInfo", "4");
		long s4Rev = getMeterSectionLength("testGetMeterKeyInfo", "3");
		long s5Rev = getMeterSectionLength("testGetMeterKeyInfo", "2");		
		List<Long> timesRev = Arrays.asList(
			(long) 0, // 2/4
			s1Rev, // 5/16
			s1Rev + s2Rev, // 2/2
			s1Rev + s2Rev + s3Rev, // 3/1
			s1Rev + s2Rev + s3Rev + s4Rev, // 2/1
			s1Rev + s2Rev + s3Rev + s4Rev + s5Rev // 3/4
		);
		List<Rational> metricTimesRev = Arrays.asList(
			Rational.ZERO, // 2/4
			new Rational(2, 4), // 5/16
			new Rational(13, 16), // 2/2
			new Rational(45, 16), // 3/1
			new Rational(141, 16), // 2/1
			new Rational(205, 16) // 3/4
		);
		List<Rational> metricTimes = Arrays.asList(
			Rational.ZERO, // 3/4
			new Rational(3, 4), // 2/1
			new Rational(19, 4), // 3/1
			new Rational(43, 4), // 2/2
			new Rational(51, 4), // 5/16
			new Rational(209, 16) // 2/4
		);		
		MetricalTimeLine mtl1 = getCleanMetricalTimeLine("testGetMeterKeyInfo");
		int ind = -1;
		// Add zeroMarker (meter section 1, mt = 0/1)
		expected1.add((Marker) new TimedMetrical(0, Rational.ZERO));
		// Add TimeSignatureMarker + TempoMarker (meter section 1-6)
		// NB For 2/4 meter section (mt in mtl == 209/16), add no TempoMarker;
		//    for 3/4 meter section (mt in mtl == Rational.ZERO), add missing TempoMarker
		for (int i = mtl1.size() - 1; i >= 0; i--) {
			Marker m = mtl1.get(i);
			ind = (metricTimes.size() - 1) - metricTimes.indexOf(m.getMetricTime());
			if (m instanceof TimeSignatureMarker) {
				TimeSignatureMarker tsm = (TimeSignatureMarker) m; 
				tsm.setMetricTime(metricTimesRev.get(ind));
				expected1.add(tsm);
			}
			if (m instanceof TempoMarker && 
				!m.getMetricTime().equals(metricTimes.get(metricTimes.size() - 1))) {
				TempoMarker tm = (TempoMarker) m;
				tm.setMetricTime(metricTimesRev.get(ind));
				tm.setTime(timesRev.get(ind));
				expected1.add(tm);
			}
		}
		expected1.add(new TempoMarker(timesRev.get(ind), metricTimesRev.get(ind)));
		// Adapt endmarker (added through last TempoMarker) (mt = 365/16)
		TimedMetrical em1 = (TimedMetrical) expected1.get(expected1.size()-1);
		em1.setMetricTime(new Rational(365, 16));
		em1.setTime(s1Rev + s2Rev + s3Rev + s4Rev + s5Rev + 
			TimeMeterTools.calculateTime(new Rational(10, 1), TimeMeterToolsTest.T_100));
		expected.add(expected1);

		MetricalTimeLine expected2 = new MetricalTimeLine();
		expected2.clear();
		long s1Resc = getMeterSectionLength("testGetMeterKeyInfo", "1") / 2;
		long s2Resc = getMeterSectionLength("testGetMeterKeyInfo", "2") / 2;
		long s3Resc = getMeterSectionLength("testGetMeterKeyInfo", "3") / 2;
		long s4Resc = getMeterSectionLength("testGetMeterKeyInfo", "4") / 2;
		long s5Resc = getMeterSectionLength("testGetMeterKeyInfo", "5") / 2;		
		List<Long> timesResc = Arrays.asList(
			(long) 0, // 3/8
			s1Resc, // 2/2
			s1Resc + s2Resc, // 3/2
			s1Resc + s2Resc + s3Resc, // 2/4
			s1Resc + s2Resc + s3Resc + s4Resc, // 5/32
			s1Resc + s2Resc + s3Resc + s4Resc + s5Resc // 2/8
		);
		List<Rational> metricTimesResc = Arrays.asList(
			Rational.ZERO, // 3/8
			new Rational(3, 8), // 2/2
			new Rational(19, 8), // 3/2
			new Rational(43, 8), // 2/4
			new Rational(51, 8), // 5/32
			new Rational(209, 32) // 2/8
		);
		List<Rational> metersResc = Arrays.asList(
			new Rational(3, 8),
			new Rational(2, 2),
			new Rational(3, 2),
			new Rational(2, 4),
			new Rational(5, 32),
			new Rational(2, 8)
		);
		MetricalTimeLine mtl2 = getCleanMetricalTimeLine("testGetMeterKeyInfo");
		ind = -1;
		// Add zeroMarker (meter section 1, mt = 0/1)
		expected2.add((Marker) new TimedMetrical(0, Rational.ZERO));
		// Add TimeSignatureMarker + TempoMarker (meter section 1-6)
		for (int i = 0; i < mtl2.size(); i++) {
			Marker m = mtl2.get(i);
			ind = metricTimes.indexOf(m.getMetricTime());
			if (m instanceof TimeSignatureMarker) {
				TimeSignatureMarker tsm = (TimeSignatureMarker) m; 
				tsm.setTimeSignature(new TimeSignature(metersResc.get(ind)));
				tsm.setMetricTime(metricTimesResc.get(ind));
				expected2.add(tsm);
			}
			if (m instanceof TempoMarker && !m.getMetricTime().equals(Rational.ZERO)) {
				TempoMarker tm = (TempoMarker) m;
				tm.setMetricTime(metricTimesResc.get(ind));
				tm.setTime(timesResc.get(ind));
				expected2.add(tm);
			}
		}
		// Add endMarker (not added through last TempoMarker)
		expected2.add(mtl2.get(mtl2.size()-1));
		// Adapt endmarker (mt = 369/32)
		TimedMetrical em2 = (TimedMetrical) expected2.get(expected2.size()-1);
		em2.setMetricTime(new Rational(369, 32));
		em2.setTime(s1Resc + s2Resc + s3Resc + s4Resc + s5Resc + 
			TimeMeterTools.calculateTime(new Rational(10, 1).div(2), TimeMeterToolsTest.T_100));
		expected.add(expected2);

		MetricalTimeLine expected3 = new MetricalTimeLine();
		expected3.clear();
		List<Rational> metersResc3 = Arrays.asList(
			new Rational(3, 2),
			new Rational(4, 1),
			new Rational(6, 1),
			new Rational(2, 1),
			new Rational(5, 8),
			new Rational(2, 2)
		);
		MetricalTimeLine mtl3 = getCleanMetricalTimeLine("testGetMeterKeyInfo");
		ind = -1;
		// Add zeroMarker (meter section 1, mt = 0/1)
		expected3.add((Marker) new TimedMetrical(0, Rational.ZERO));
		// Add TimeSignatureMarker + TempoMarker (meter section 1-6)
		for (int i = 0; i < mtl3.size(); i++) {
			Marker m = mtl3.get(i);
			ind = metricTimes.indexOf(m.getMetricTime());
			if (m instanceof TimeSignatureMarker) {
				TimeSignatureMarker tsm = (TimeSignatureMarker) m; 
				tsm.setTimeSignature(new TimeSignature(metersResc3.get(ind)));
				tsm.setMetricTime(metricTimesResc.get(ind).mul(4));
				expected3.add(tsm);
			}
			if (m instanceof TempoMarker && !m.getMetricTime().equals(Rational.ZERO)) {
				TempoMarker tm = (TempoMarker) m;
				tm.setMetricTime(metricTimesResc.get(ind).mul(4));
				tm.setTime(timesResc.get(ind) * 4);
				expected3.add(tm);
			}
		}
		// Adapt endmarker (added through last TempoMarker) (mt = 369/8)
		TimedMetrical em3 = (TimedMetrical) expected3.get(expected3.size()-1);
		em3.setMetricTime(new Rational(369, 8));
		em3.setTime(s1Resc * 4 + s2Resc * 4 + s3Resc * 4 + s4Resc * 4 + s5Resc * 4 + 
			TimeMeterTools.calculateTime(new Rational(10, 1).mul(2), TimeMeterToolsTest.T_100));
		expected.add(expected3);

		List<MetricalTimeLine> actual = new ArrayList<>();
		actual.add(ScorePiece.augmentMetricalTimeLine(
			sp1.getScoreMetricalTimeLine(), 
			t1.getMirrorPoint(), 
			-1, 
			"reverse")
		);
		actual.add(ScorePiece.augmentMetricalTimeLine(
			sp2.getScoreMetricalTimeLine(), 
			null, 
			-2, 
			"rescale")
		);
		actual.add(ScorePiece.augmentMetricalTimeLine(
			sp3.getScoreMetricalTimeLine(), 
			null,  
			2, 
			"rescale")
		);

		assertMetricalTimeLineEquality(expected, actual);
	}


	@Test
	public void testAugmentHarmonyTrack() {
		// Tablature/non-tablature case
		Transcription t1 = new Transcription(midiTestGetMeterKeyInfo); // reverse
		ScorePiece sp1 = t1.getScorePiece();
		Transcription t2 = new Transcription(midiTestGetMeterKeyInfo); // rescale (rescaleFactor = -2)
		ScorePiece sp2 = t2.getScorePiece();
		
		List<SortedContainer<Marker>> expected = new ArrayList<>();
		SortedContainer<Marker> expected1 = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());
		long s1Rev = getMeterSectionLength("testGetMeterKeyInfo", "5") + 
			getMeterSectionLength("testGetMeterKeyInfo", "6");
		long s2Rev = getMeterSectionLength("testGetMeterKeyInfo", "4");
		long s3Rev = getMeterSectionLength("testGetMeterKeyInfo", "3");
		List<Long> timesRev = Arrays.asList(
			(long) 0, // E minor
			s1Rev, // Bb major
			s1Rev + s2Rev, // F# minor
			s1Rev + s2Rev + s3Rev // C major
		);
		List<Rational> metricTimesRev = Arrays.asList(
			Rational.ZERO, // E minor
			new Rational(13, 16), // Bb major
			new Rational(45, 16), // F# minor
			new Rational(141, 16) // C major
		);
		List<Rational> metricTimes = Arrays.asList(
			Rational.ZERO, // C major
			new Rational(19, 4), // F# minor
			new Rational(43, 4), // Bb major
			new Rational(51, 4) // E minor
		);
		SortedContainer<Marker> ht1 = getCleanHarmonyTrack("testGetMeterKeyInfo");
		int ind = -1;
		for (int i = ht1.size() - 1; i >= 0; i--) {
			Marker m = ht1.get(i);
			ind = (metricTimes.size() - 1) - metricTimes.indexOf(m.getMetricTime());
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				km.setMetricTime(metricTimesRev.get(ind));
				km.setTime(timesRev.get(ind));
				expected1.add(km);
			}
		}
		expected.add(expected1);

		SortedContainer<Marker> expected2 = 
			new SortedContainer<Marker>(null, Marker.class, new MetricalComparator());
		long s1Resc = (getMeterSectionLength("testGetMeterKeyInfo", "1") + 
			getMeterSectionLength("testGetMeterKeyInfo", "2")) / 2;
		long s2Resc = getMeterSectionLength("testGetMeterKeyInfo", "3") / 2;
		long s3Resc = getMeterSectionLength("testGetMeterKeyInfo", "4") / 2;
		List<Long> timesResc = Arrays.asList(
			(long) 0, // C major 
			s1Resc, // F# minor
			s1Resc + s2Resc, // Bb major 
			s1Resc + s2Resc + s3Resc // E minor
		);
		List<Rational> metricTimesResc = Arrays.asList(
			Rational.ZERO, // C major
			new Rational(19, 8), // F# minor
			new Rational(43, 8), // Bb major
			new Rational(51, 8) // E minor
		);
		SortedContainer<Marker> ht2 = getCleanHarmonyTrack("testGetMeterKeyInfo");
		ind = -1;
		for (int i = 0; i < ht2.size(); i++) {
			Marker m = ht2.get(i);
			ind = metricTimes.indexOf(m.getMetricTime());
			if (m instanceof KeyMarker) {
				KeyMarker km = (KeyMarker) m;
				km.setMetricTime(metricTimesResc.get(ind));
				km.setTime(timesResc.get(ind));
				expected2.add(km);
			}
		}
		expected.add(expected2);

		List<SortedContainer<Marker>> actual = new ArrayList<>();
		actual.add(ScorePiece.augmentHarmonyTrack(
			sp1.getHarmonyTrack(), sp1.getScoreMetricalTimeLine(), 
			t1.getMirrorPoint(), 
			-1, 
			"reverse")	
		);
		actual.add(ScorePiece.augmentHarmonyTrack(
			sp2.getHarmonyTrack(), sp2.getScoreMetricalTimeLine(), 
			null, 
			-2, 
			"rescale")	
		);

		assertHarmonyTrackEquality(expected, actual);
	}


	@Test
	public void testAugmentNotationSystem() {
		// Tablature/non-tablature case
		Transcription t1 = new Transcription(midiTestpiece); // reverse
		ScorePiece sp1 = t1.getScorePiece(); // reverse
		sp1.getScore().getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
		.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
		Transcription t2 = new Transcription(midiTestpiece); // deornament
		ScorePiece sp2 = t2.getScorePiece(); // deornament
		sp2.getScore().getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
		.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
		Transcription t3 = new Transcription(midiTestpiece); // rescale (rescaleFactor = -2)
		ScorePiece sp3 = t3.getScorePiece(); // rescale (rescaleFactor = -2)
		sp3.getScore().getContentsRecursiveList(null).stream().filter(c -> c instanceof Note)
		.forEach(c -> ((Note) c).getPerformanceNote().setVelocity(90));
		
		List<NotationSystem> expected = new ArrayList<>();
		NotationSystem expected1 = new NotationSystem();
		MetricalTimeLine mtl1 = getCleanMetricalTimeLine("testpiece");
		ScoreMetricalTimeLine smtl1 = new ScoreMetricalTimeLine(mtl1);
		mtl1 = 
			ScorePiece.augmentMetricalTimeLine(smtl1, 
			t1.getMirrorPoint(),
			-1, "reverse");
		// Voice 0
		NotationStaff ns01 = new NotationStaff();
		NotationVoice nv01 = new NotationVoice();		
		List<Note> notesV01 = Arrays.asList(
			ScorePiece.createNote(69, new Rational(0, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(69, new Rational(2, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(68, new Rational(3, 4), new Rational(1, 32), -1, mtl1),
			ScorePiece.createNote(66, new Rational(25, 32), new Rational(1, 32), -1, mtl1),
			ScorePiece.createNote(68, new Rational(13, 16), new Rational(1, 32), -1, mtl1),
			ScorePiece.createNote(69, new Rational(27, 32), new Rational(1, 32), -1, mtl1),
			ScorePiece.createNote(68, new Rational(7, 8), new Rational(1, 16), -1, mtl1),
			ScorePiece.createNote(69, new Rational(15, 16), new Rational(1, 16), -1, mtl1),
			ScorePiece.createNote(68, new Rational(4, 4), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(64, new Rational(9, 8), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(69, new Rational(5, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(65, new Rational(6, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(72, new Rational(7, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(69, new Rational(8, 4), new Rational(1, 4), -1, mtl1)
		);
		notesV01.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv01.add(nc);
		});
		ns01.add(nv01);
		expected1.add(ns01);
		// Voice 1
		NotationStaff ns11 = new NotationStaff();
		NotationVoice nv11 = new NotationVoice();
		List<Note> notesV11 = Arrays.asList(
			ScorePiece.createNote(64, new Rational(0, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(64, new Rational(2, 4), new Rational(1, 2), -1, mtl1),
			ScorePiece.createNote(69, new Rational(4, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(60, new Rational(5, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(65, new Rational(13, 8), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(69, new Rational(15, 8), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(65, new Rational(8, 4), new Rational(1, 4), -1, mtl1)
		);
		notesV11.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv11.add(nc);
		});
		ns11.add(nv11);
		expected1.add(ns11);
		// Voice 2
		NotationStaff ns21 = new NotationStaff();
		NotationVoice nv21 = new NotationVoice();
		List<Note> notesV21 = Arrays.asList(
			ScorePiece.createNote(57, new Rational(0, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(57, new Rational(2, 4), new Rational(1, 2), -1, mtl1),
			ScorePiece.createNote(59, new Rational(4, 4), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(60, new Rational(9, 8), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(57, new Rational(5, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(59, new Rational(6, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(57, new Rational(7, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(57, new Rational(8, 4), new Rational(1, 4), -1, mtl1)
		);
		notesV21.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv21.add(nc);
		});
		ns21.add(nv21);
		expected1.add(ns21);
		// Voice 3
		NotationStaff ns31 = new NotationStaff();
		NotationVoice nv31 = new NotationVoice();
		List<Note> notesV31 = Arrays.asList(
			ScorePiece.createNote(45, new Rational(0, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(45, new Rational(2, 4), new Rational(1, 2), -1, mtl1),
			ScorePiece.createNote(57, new Rational(4, 4), new Rational(1, 2), -1, mtl1),
			ScorePiece.createNote(50, new Rational(6, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(48, new Rational(7, 4), new Rational(1, 16), -1, mtl1),
			ScorePiece.createNote(45, new Rational(29, 16), new Rational(3, 16), -1, mtl1),
			ScorePiece.createNote(50, new Rational(8, 4), new Rational(1, 4), -1, mtl1)
		);
		notesV31.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv31.add(nc);
		});
		ns31.add(nv31);
		expected1.add(ns31);
		// Voice 4
		NotationStaff ns41 = new NotationStaff();
		NotationVoice nv41 = new NotationVoice();
		List<Note> notesV41 = Arrays.asList(
			ScorePiece.createNote(45, new Rational(4, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(45, new Rational(5, 4), new Rational(1, 4), -1, mtl1),
			ScorePiece.createNote(45, new Rational(6, 4), new Rational(1, 8), -1, mtl1),
			ScorePiece.createNote(47, new Rational(13, 8), new Rational(1, 8), -1, mtl1)
		);
		notesV41.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv41.add(nc);
		});
		ns41.add(nv41);
		expected1.add(ns41);
		expected.add(expected1);
		
		NotationSystem expected2 = new NotationSystem();
		MetricalTimeLine mtl2 = getCleanMetricalTimeLine("testpiece"); // does not have to be augmented
		ScoreMetricalTimeLine smtl2 = new ScoreMetricalTimeLine(mtl2);
		List<Note> notes2 = TranscriptionTest.getUnhandledNotesFromPiece(t2.getScorePiece(), "testpiece");
		List<Note> notesDeorn2 = new ArrayList<>(notes2);
		notesDeorn2.set(4, ScorePiece.createNote(notes2.get(4).getMidiPitch(), 
			notes2.get(4).getMetricTime(), new Rational(1, 4), -1, mtl2));
		notesDeorn2.set(29, ScorePiece.createNote(notes2.get(29).getMidiPitch(), 
			notes2.get(29).getMetricTime(), new Rational(1, 4), -1, mtl2));
		notes2.stream()
			.filter(n -> n.getMetricDuration().isLess(E))
			.forEach(n -> notesDeorn2.remove(n));
		// Voice 0
		NotationStaff ns02 = new NotationStaff();
		NotationVoice nv02 = new NotationVoice();		
		List<Note> notesV02 = Arrays.asList(
			notesDeorn2.get(3), notesDeorn2.get(7), notesDeorn2.get(12), notesDeorn2.get(18), notesDeorn2.get(21), 
			notesDeorn2.get(24), notesDeorn2.get(28), notesDeorn2.get(29), notesDeorn2.get(33)
		);
		notesV02.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv02.add(nc);
		});
		ns02.add(nv02);
		expected2.add(ns02);
		// Voice 1
		NotationStaff ns12 = new NotationStaff();
		NotationVoice nv12 = new NotationVoice();
		List<Note> notesV12 = Arrays.asList(
			notesDeorn2.get(2), notesDeorn2.get(6), notesDeorn2.get(11), notesDeorn2.get(17), notesDeorn2.get(22),
			notesDeorn2.get(27), notesDeorn2.get(32)
		);
		notesV12.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv12.add(nc);
		});
		ns12.add(nv12);
		expected2.add(ns12);
		// Voice 2
		NotationStaff ns22 = new NotationStaff();
		NotationVoice nv22 = new NotationVoice();
		List<Note> notesV22 = Arrays.asList(
			notesDeorn2.get(1), notesDeorn2.get(5), notesDeorn2.get(10), notesDeorn2.get(16), notesDeorn2.get(20),
			notesDeorn2.get(23), notesDeorn2.get(26), notesDeorn2.get(31)
		);
		notesV22.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv22.add(nc);
		});
		ns22.add(nv22);
		expected2.add(ns22);
		// Voice 3
		NotationStaff ns32 = new NotationStaff();
		NotationVoice nv32 = new NotationVoice();
		List<Note> notesV32 = Arrays.asList(
			notesDeorn2.get(0), notesDeorn2.get(4), notesDeorn2.get(9), notesDeorn2.get(15), notesDeorn2.get(25),
			notesDeorn2.get(30)
		);
		notesV32.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv32.add(nc);
		});
		ns32.add(nv32);
		expected2.add(ns32);
		// Voice 4
		NotationStaff ns42 = new NotationStaff();
		NotationVoice nv42 = new NotationVoice();
		List<Note> notesV42 = Arrays.asList(
			notesDeorn2.get(8), notesDeorn2.get(13), notesDeorn2.get(14), notesDeorn2.get(19)
		);
		notesV42.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv42.add(nc);
		});
		ns42.add(nv42);
		expected2.add(ns42);
		expected.add(expected2);

		NotationSystem expected3 = new NotationSystem();
		MetricalTimeLine mtl3 = getCleanMetricalTimeLine("testpiece");
		ScoreMetricalTimeLine smtl3 = new ScoreMetricalTimeLine(mtl3);
		mtl3 = ScorePiece.augmentMetricalTimeLine(smtl3, null, 2, "rescale");
		// Voice 0
		NotationStaff ns03 = new NotationStaff();
		NotationVoice nv03 = new NotationVoice();		
		List<Note> notesV03 = Arrays.asList(
			ScorePiece.createNote(69, new Rational(3, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(72, new Rational(4, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(65, new Rational(5, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(69, new Rational(6, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(64, new Rational(7, 8), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(68, new Rational(15, 16), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(69, new Rational(8, 8), new Rational(1, 32), -1, mtl3),
			ScorePiece.createNote(68, new Rational(33, 32), new Rational(1, 32), -1, mtl3),
			ScorePiece.createNote(69, new Rational(17, 16), new Rational(1, 64), -1, mtl3),
			ScorePiece.createNote(68, new Rational(69, 64), new Rational(1, 64), -1, mtl3),
			ScorePiece.createNote(66, new Rational(70, 64), new Rational(1, 64), -1, mtl3),
			ScorePiece.createNote(68, new Rational(71, 64), new Rational(1, 64), -1, mtl3),
			ScorePiece.createNote(69, new Rational(9, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(69, new Rational(11, 8), new Rational(1, 8), -1, mtl3)
		);
		notesV03.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv03.add(nc);
		});
		ns03.add(nv03);
		expected3.add(ns03);
		// Voice 1
		NotationStaff ns13 = new NotationStaff();
		NotationVoice nv13 = new NotationVoice();
		List<Note> notesV13 = Arrays.asList(
			ScorePiece.createNote(65, new Rational(3, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(69, new Rational(4, 8), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(65, new Rational(5, 8), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(60, new Rational(6, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(69, new Rational(7, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(64, new Rational(8, 8), new Rational(1, 4), -1, mtl3),
			ScorePiece.createNote(64, new Rational(11, 8), new Rational(1, 8), -1, mtl3)
		);
		notesV13.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv13.add(nc);
		});
		ns13.add(nv13);
		expected3.add(ns13);
		// Voice 2
		NotationStaff ns23 = new NotationStaff();
		NotationVoice nv23 = new NotationVoice();
		List<Note> notesV23 = Arrays.asList(
			ScorePiece.createNote(57, new Rational(3, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(57, new Rational(4, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(59, new Rational(5, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(57, new Rational(6, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(60, new Rational(7, 8), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(59, new Rational(15, 16), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(57, new Rational(8, 8), new Rational(1, 4), -1, mtl3),
			ScorePiece.createNote(57, new Rational(11, 8), new Rational(1, 8), -1, mtl3)
		);
		notesV23.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv23.add(nc);
		});
		ns23.add(nv23);
		expected3.add(ns23);
		// Voice 3
		NotationStaff ns33 = new NotationStaff();
		NotationVoice nv33 = new NotationVoice();
		List<Note> notesV33 = Arrays.asList(
			ScorePiece.createNote(50, new Rational(3, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(45, new Rational(4, 8), new Rational(3, 32), -1, mtl3),
			ScorePiece.createNote(48, new Rational(19, 32), new Rational(1, 32), -1, mtl3),
			ScorePiece.createNote(50, new Rational(5, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(57, new Rational(6, 8), new Rational(1, 4), -1, mtl3),
			ScorePiece.createNote(45, new Rational(8, 8), new Rational(1, 4), -1, mtl3),
			ScorePiece.createNote(45, new Rational(11, 8), new Rational(1, 8), -1, mtl3)
		);
		notesV33.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv33.add(nc);
		});
		ns33.add(nv33);
		expected3.add(ns33);
		// Voice 4
		NotationStaff ns43 = new NotationStaff();
		NotationVoice nv43 = new NotationVoice();
		List<Note> notesV43 = Arrays.asList(
			ScorePiece.createNote(47, new Rational(5, 8), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(45, new Rational(11, 16), new Rational(1, 16), -1, mtl3),
			ScorePiece.createNote(45, new Rational(6, 8), new Rational(1, 8), -1, mtl3),
			ScorePiece.createNote(45, new Rational(7, 8), new Rational(1, 8), -1, mtl3)
		);
		notesV43.forEach(n -> {
			NotationChord nc = new NotationChord(); nc.add(n); nv43.add(nc);
		});
		ns43.add(nv43);
		expected3.add(ns43);
		expected.add(expected3);

		List<NotationSystem> actual = new ArrayList<>();
		actual.add(ScorePiece.augmentNotationSystem(
			sp1.getScore(), smtl1,
			t1.getMirrorPoint(), 
			null, null, null, 
			-1, "reverse", "")
		);
		actual.add(ScorePiece.augmentNotationSystem(
			sp2.getScore(), smtl2, 
			null,
			t2.getChords(), t2.getMetricPositionsChords(), E, 
			-1, "deornament", "")	
		);
		actual.add(ScorePiece.augmentNotationSystem(
			sp3.getScore(), smtl3, 
			null, 
			null, null, null, 
			-2, "rescale", "")	
		);

		assertNotationSystemEquality(expected, actual);
	}


	@Test
	public void testCopyNotationSystem() {
		// Tablature/non-tablature case
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestpiece));
		NotationSystem expected = sp.getScore();
		NotationSystem actual = ScorePiece.copyNotationSystem(expected);
		assertNotationSystemEquality(Arrays.asList(expected), Arrays.asList(actual));
	}

}
