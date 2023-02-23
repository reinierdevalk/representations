package structure.metric;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uos.fmt.musitech.utility.math.Rational;
import path.Path;
import representations.Tablature;
import structure.TimelineTest;

public class UtilsTest {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;
	private File midiTestGetMeterKeyInfo; 
	public static final double T_99 = 99.99999999999999;
	public static final double T_100 = 100.0;
	public static final double T_289 = 289.99937166802806;
	public static final double T_439 = 439.0008341015848;
	
	@Before
	public void setUp() throws Exception {
		String root = Path.getRootPath() + Path.getDataDir(); 
		encodingTestpiece = 
			new File(root + Path.getEncodingsPath() + Path.getTestDir() + "testpiece.tbp");
		encodingTestGetMeterInfo = 
			new File(root + Path.getEncodingsPath() + Path.getTestDir() + "test_get_meter_info.tbp");
		midiTestpiece = 
			new File(root + Path.getMIDIPath() + Path.getTestDir() + "testpiece.mid");
		midiTestGetMeterKeyInfo = 
			new File(root + Path.getMIDIPath() + Path.getTestDir() + "test_get_meter_key_info.mid");
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDiminuteMeter() {
		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);

		List<Rational> expected = new ArrayList<>();
		expected.add(twoTwo); // 2/1, dim = 2
		expected.add(fourFour); // 4/2, dim = 2
		expected.add(fourFour); // 4/1, dim = 4
		expected.add(twoTwo); // 2/4, dim = -2
		expected.add(fourFour); // 4/8, dim = -2
		expected.add(fourFour); // 4/16, dim = -4

		List<Rational> meters = Arrays.asList(new Rational[]{
			new Rational(2, 1), new Rational(4, 2), new Rational(4, 1), 
			new Rational(2, 4), new Rational(4, 8), new Rational(4, 16)
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(Utils.diminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	@Test
	public void testUndiminuteMeter() {
		List<Rational> expected = new ArrayList<>();
		expected.add(new Rational(2, 1)); // 2/2, dim = 2
		expected.add(new Rational(4, 2)); // 4/4, dim = 2
		expected.add(new Rational(4, 1)); // 4/4, dim = 4
		expected.add(new Rational(2, 4)); // 2/2, dim = -2
		expected.add(new Rational(4, 8)); // 4/4, dim = -2
		expected.add(new Rational(4, 16)); // 4/4, dim = -4

		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);
		List<Rational> meters = Arrays.asList(new Rational[]{
			twoTwo, fourFour, fourFour, twoTwo, fourFour, fourFour
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(Utils.undiminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	@Test
	public void testDiminute() {
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(3, 2),
			new Rational(2, 2), 
			new Rational(2, 2),
			new Rational(3, 2),
			new Rational(3, 8),
		});

		List<Rational> undim = Arrays.asList(new Rational[]{
			new Rational(3, 8),
			new Rational(2, 4),
			new Rational(2, 2),
			new Rational(3, 1),
			new Rational(3, 2),
		});
		List<Integer> diminutions = Arrays.asList(new Integer[]{-4, -2, 1, 2, 4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < diminutions.size(); i++) {
			actual.add(Utils.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testDiminuteAlt() {
		List<Double> expected = Arrays.asList(new Double[]{1.5, 1.0, 1.0, 1.5, 0.375});

		List<Double> undim = Arrays.asList(new Double[]{0.375, 0.5, 1.0, 3.0, 1.5});
		List<Integer> diminutions = Arrays.asList(new Integer[]{-4, -2, 1, 2, 4});
		List<Double> actual = new ArrayList<>();
		for (int i = 0; i < diminutions.size(); i++) {
			actual.add(Utils.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testCalculateTime() {
		// Tablature/non-tablature case
		List<Rational> durs = Arrays.asList(new Rational[]{
			new Rational(10, 1),
			new Rational(10, 1),
			new Rational(10, 1),
			new Rational(10, 1),
		});
		List<Double> tempi = Arrays.asList(new Double[]{
			T_99,
			T_100,
			T_289,
			T_439,
		});

		List<Long> expected = Arrays.asList(new Long[]{
			(long) 24000000, (long) 12000000,
			(long) 24000000, (long) 12000000,
			(long) 8275880, (long) 4137940,
			(long) 5466960, (long) 2733480
		});

		List<Long> actual = new ArrayList<>();
		for (int i = 0; i < tempi.size(); i++) {
			actual.add(Utils.calculateTime(durs.get(i), tempi.get(i)));
			actual.add(Utils.calculateTime(durs.get(i).div(2), tempi.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetMetricPosition() {
		// For a piece with meter changes
		Tablature t1 = new Tablature(encodingTestGetMeterInfo, false);
		List<Rational[]> expected = TimelineTest.getMetricPositions("testGetMeterInfo", true);

		// For a piece with no meter changes
		Tablature t2 = new Tablature(encodingTestpiece, false);
		expected.addAll(TimelineTest.getMetricPositions("testpiece", true));

		List<Rational[]> actual = new ArrayList<Rational[]>();
		Integer[][] btp1 = t1.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo1 = t1.getMeterInfo();
//		List<Integer[]> meterInfo1 = t1.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp1.length; i++) {
			Rational currMetricTime = 
				new Rational(btp1[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Utils.getMetricPosition(currMetricTime, meterInfo1));
		}
		Integer[][] btp2 = t2.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo2 = t2.getMeterInfo();
//		List<Integer[]> meterInfo2 = t2.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp2.length; i++) {
			Rational currMetricTime = 
				new Rational(btp2[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Utils.getMetricPosition(currMetricTime, meterInfo2));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testGetDiminution() {
		List<List<Rational>> allMetricTimes = new ArrayList<>();
		// For testGetMeterInfo
		List<Rational> metricTimes1 = new ArrayList<>();
		metricTimes1.add(new Rational(0, 1)); // start bar 1 
		metricTimes1.add(new Rational(5, 8)); // bar 2 1/4
		metricTimes1.add(new Rational(11, 8)); // start bar 3
		metricTimes1.add(new Rational(21, 8)); // bar 4 1/4
		metricTimes1.add(new Rational(25, 8)); // start bar 5
		metricTimes1.add(new Rational(33, 8)); // bar 6 1/4
		metricTimes1.add(new Rational(39, 8)); // start bar 7
		metricTimes1.add(new Rational(48, 8)); // in bar 8 2/16
		metricTimes1.add(new Rational(99, 16)); // start bar 9
		allMetricTimes.add(metricTimes1);
		// For testpiece
		List<Rational> metricTimes2 = new ArrayList<>();
		metricTimes2.add(new Rational(0, 1)); // start bar 1
		metricTimes2.add(new Rational(7, 4)); // in bar 2
		metricTimes2.add(new Rational(8, 4)); // start bar 3
		metricTimes2.add(new Rational(10, 4)); // in bar 3
		allMetricTimes.add(metricTimes2);

		List<Integer> expected = new ArrayList<>();
		// For testGetMeterInfo
		expected.addAll(Arrays.asList(new Integer[]{2, 2, 2, 4, 4, 1, 1, 1, -2}));
		// For testPiece
		expected.addAll(Arrays.asList(new Integer[]{1, 1, 1, 1}));

		List<Integer> actual = new ArrayList<>();
		List<Tablature> tabs = Arrays.asList(new Tablature[]{
			new Tablature(encodingTestGetMeterInfo, true),
			new Tablature(encodingTestpiece, true)});
		for (int i = 0; i < tabs.size(); i++) {
			for (Rational mt : allMetricTimes.get(i)) {
				actual.add(Utils.getDiminution(mt, tabs.get(i).getMeterInfo()));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}

}
