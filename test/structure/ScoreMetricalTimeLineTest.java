package structure;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.utility.math.Rational;
import imports.MIDIImport;
import path.Path;
import representations.Tablature;
import representations.Transcription;
import representations.Transcription.Type;

public class ScoreMetricalTimeLineTest {
	
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;
	private File midiTestGetMeterKeyInfo; 

	@Before
	public void setUp() throws Exception {
		String root = Path.ROOT_PATH_DEPLOYMENT_DEV; 
		encodingTestpiece = 
			new File(root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "testpiece.tbp");
		encodingTestGetMeterInfo = 
			new File(root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "test_get_meter_info.tbp");
		midiTestpiece = 
			new File(root + Path.MIDI_REL_PATH + Path.TEST_DIR + "testpiece.mid");
		midiTestGetMeterKeyInfo = 
			new File(root + Path.MIDI_REL_PATH + Path.TEST_DIR + "test_get_meter_key_info.mid");
	}


	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testMakeMeterSectionOnsets() {
		// There is no empty constructor, so the init() methods cannot be tested with the instance 
		// variables added incrementally to an empty ScoreMetricalTimeLine. But since the instance 
		// variables do not depend on each other, the init() methods can be tested with a completed 
		// ScoreMetricalTimeLine
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.getMetricalTimeLine());

		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(0, 4),
			new Rational(3, 4),
			new Rational(19, 4),
			new Rational(43, 4),
			new Rational(51, 4),
			new Rational(209, 16)
		});
		
		List<Rational> actual = smtl.makeMeterSectionOnsets();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeMeterSectionTimes() {
		// There is no empty constructor, so the init() methods cannot be tested with the instance 
		// variables added incrementally to an empty ScoreMetricalTimeLine. But since the instance 
		// variables do not depend on each other, the init() methods can be tested with a completed 
		// ScoreMetricalTimeLine
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.getMetricalTimeLine());

		List<Long> expected = Arrays.asList(new Long[]{
			(long) 0, 
			(long) 1800000, 
			(long) 11400000, 
			(long) 25800000, 
			(long) 30600000, 
			(long) 31350000
		});

		List<Long> actual = smtl.makeMeterSectionTimes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetMeterSection() {
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.getScoreMetricalTimeLine());
		Rational quarter = new Rational(1, 4);

		// For testGetMeterInfo
		List<Integer> expected = Arrays.asList(new Integer[]{
			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5	
		});

		List<Rational> mps = Arrays.asList(new Rational[]{
			new Rational(0, 4), new Rational(0, 4).add(quarter), // meter section 0
			new Rational(3, 4), new Rational(3, 4).add(quarter), // meter section 1
			new Rational(19, 4), new Rational(19, 4).add(quarter), // meter section 2
			new Rational(43, 4), new Rational(43, 4).add(quarter), // meter section 3
			new Rational(51, 4), new Rational(51, 4).add(quarter), // meter section 4
			new Rational(209, 16), new Rational(209, 16).add(quarter.div(4)) // meter section 5
		});

		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < mps.size(); i++) {		
			actual.add(smtl.getMeterSection(mps.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetMeterSectionAlt() {
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.getScoreMetricalTimeLine());
		long quarter = 600000;

		// For testGetMeterInfo
		List<Integer> expected = Arrays.asList(new Integer[]{
			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5	
		});

		List<Long> times = Arrays.asList(new Long[]{
			(long) 0, 0 + quarter, // meter section 0
			(long) 1800000, 1800000 + quarter, // meter section 1
			(long) 11400000, 11400000 + quarter, // meter section 2
			(long) 25800000, 25800000 + quarter, // meter section 3
			(long) 30600000, 30600000 + quarter, // meter section 4
			(long) 31350000, 31350000 + (quarter/4) // meter section 5
		});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < times.size(); i++) {
			actual.add(smtl.getMeterSection(times.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetDiminutedMetricTime() {
		ScorePiece sp = new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo));
		ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.getScoreMetricalTimeLine());
		Tablature tab = new Tablature(encodingTestGetMeterInfo);
		MetricalTimeLine mtl = sp.getMetricalTimeLine();
		MetricalTimeLine mtlDim = ScorePiece.diminuteMetricalTimeLine(mtl, tab.getMeterInfo());
		ScoreMetricalTimeLine smtlDim = new ScoreMetricalTimeLine(mtlDim);
		Rational quarter = new Rational(1, 4);

		// For testGetMeterInfo
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(0, 8), new Rational(0, 8).add(quarter.div(2)), // meter section 0
			new Rational(3, 8), new Rational(3, 8).add(quarter.div(2)), // meter section 1
			new Rational(19, 8), new Rational(19, 8).add(quarter.div(4)), // meter section 2
			new Rational(31, 8), new Rational(31, 8).add(quarter.div(1)), // meter section 3
			new Rational(47, 8), new Rational(47, 8).add(quarter.div(1)), // meter section 4
			new Rational(99, 16), new Rational(99, 16).add(new Rational(1, 16).mul(2)) // meter section 5
		});

		List<Rational> mps = Arrays.asList(new Rational[]{
			new Rational(0, 4), new Rational(0, 4).add(quarter), // meter section 0
			new Rational(3, 4), new Rational(3, 4).add(quarter), // meter section 1
			new Rational(19, 4), new Rational(19, 4).add(quarter), // meter section 2
			new Rational(43, 4), new Rational(43, 4).add(quarter), // meter section 3
			new Rational(51, 4), new Rational(51, 4).add(quarter), // meter section 4
			new Rational(209, 16), new Rational(209, 16).add(quarter.div(4)) // meter section 5
		});
		List<Integer> diminutions = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < mps.size(); i++) {
			actual.add(smtl.getDiminutedMetricTime(mps.get(i), smtlDim, diminutions));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetMetricPosition() {
		List<ScorePiece> sp = Arrays.asList(
			new ScorePiece(MIDIImport.importMidiFile(midiTestpiece)),
			new ScorePiece(MIDIImport.importMidiFile(midiTestGetMeterKeyInfo))
		);
		List<Integer[][]> bnp = Arrays.asList(
			new Transcription(midiTestpiece).getBasicNoteProperties(),
			new Transcription(midiTestGetMeterKeyInfo).getBasicNoteProperties()
		);

		List<Rational[]> expected = new ArrayList<>();
		expected.addAll(TimelineTest.getMetricPositions("testpiece", false));
		expected.addAll(TimelineTest.getMetricPositions("testGetMeterInfo", false));

		List<Rational[]> actual = new ArrayList<>();
		for (int i = 0; i < sp.size(); i++) {
			ScoreMetricalTimeLine smtl = new ScoreMetricalTimeLine(sp.get(i).getMetricalTimeLine());
			for (Integer[] in : bnp.get(i)) {
				actual.add(smtl.getMetricPosition(
					new Rational(in[Transcription.ONSET_TIME_NUMER], 
					in[Transcription.ONSET_TIME_DENOM])));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}

}
