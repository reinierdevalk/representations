package structure;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import path.Path;
import representations.Tablature;
import structure.Timeline;
import structure.metric.Utils;
import tbp.Encoding;

public class TimelineTest extends TestCase {
	
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;
	private File midiTestGetMeterKeyInfo; 

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
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


	@Override
	@After
	public void tearDown() throws Exception {
	}


	public static List<Rational[]> getMetricPositions(String piece, boolean tablatureCase) {
		List<Rational[]> mp = new ArrayList<>();

		if (piece.equals("testpiece")) {
			if (tablatureCase) {
				// Bar 1 (meter = 2/2; diminution = 1)
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(1, 1), new Rational(3, 4)}));
				// Bar 2
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(2, 1), new Rational(0, 64)}));
				mp.add(new Rational[]{new Rational(2, 1), new Rational(3, 16)});
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(2, 1), new Rational(1, 4)}));
				mp.add(new Rational[]{new Rational(2, 1), new Rational(3, 8)});
				mp.addAll(Collections.nCopies(5, new Rational[]{new Rational(2, 1), new Rational(1, 2)}));
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(2, 1), new Rational(3, 4)}));
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(2, 1), new Rational(7, 8)}));
				// Bar 3
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(3, 1), new Rational(0, 64)}));
				mp.add(new Rational[]{new Rational(3, 1), new Rational(1, 16)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(1, 8)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(5, 32)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(3, 16)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(7, 32)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(1, 4)});
				mp.addAll(Collections.nCopies(4, new Rational[]{new Rational(3, 1), new Rational(3, 4)}));
			}
			else {
				mp = getMetricPositions("testpiece", true);
				mp.add(13, new Rational[]{new Rational(2, 1), new Rational(1, 4)}); // missing SNU note
			}
		}
		if (piece.equals("testGetMeterInfo")) {
			if (tablatureCase) {
				// Bar 1 (meter = 2/2; diminution = 2)
				mp.add(new Rational[]{new Rational(1, 1), new Rational(0, 8)});
				mp.add(new Rational[]{new Rational(1, 1), new Rational(1, 8)});
				mp.add(new Rational[]{new Rational(1, 1), new Rational(2, 8)});
				// Bar 2 (meter = 2/2; diminution = 2)
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(2, 1), new Rational(0, 512)}));
				mp.add(new Rational[]{new Rational(2, 1), new Rational(3, 8)}); 
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(2, 1), new Rational(1, 2)}));
				// Bar 3 (meter = 2/2; diminution = 2)
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(3, 1), new Rational(0, 512)}));
				mp.add(new Rational[]{new Rational(3, 1), new Rational(1, 4)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(5, 16)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(3, 8)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(13, 32)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(7, 16)});
				mp.add(new Rational[]{new Rational(3, 1), new Rational(15, 32)});
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(3, 1), new Rational(1, 2)}));
				// Bar 4 (meter = 3/4; diminution = 4)
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(4, 1), new Rational(0, 512)}));
				mp.add(new Rational[]{new Rational(4, 1), new Rational(1, 4)}); // new Rational(1, 4)});
				mp.add(new Rational[]{new Rational(4, 1), new Rational(3, 8)}); // new Rational(3, 8)});
				mp.add(new Rational[]{new Rational(4, 1), new Rational(7, 16)}); // new Rational(7, 16)});
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(4, 1), new Rational(1, 2)}));
				// Bar 5 (meter = 3/4; diminution = 4)
				mp.add(new Rational[]{new Rational(5, 1), new Rational(0, 512)});
				mp.add(new Rational[]{new Rational(5, 1), new Rational(3, 16)});
				mp.add(new Rational[]{new Rational(5, 1), new Rational(7, 32)});
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(5, 1), new Rational(1, 4)}));
				// Bar 6 (meter = 2/2; diminution = 1)
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(6, 1), new Rational(0, 512)}));
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(6, 1), new Rational(1, 2)}));
				// Bar 7 (meter = 2/2; diminution = 1)
				mp.add(new Rational[]{new Rational(7, 1), new Rational(0, 512)});
				mp.add(new Rational[]{new Rational(7, 1), new Rational(1, 8)});
				mp.add(new Rational[]{new Rational(7, 1), new Rational(1, 4)});
				mp.add(new Rational[]{new Rational(7, 1), new Rational(3, 8)});
				mp.add(new Rational[]{new Rational(7, 1), new Rational(1, 2)});
				mp.add(new Rational[]{new Rational(7, 1), new Rational(5, 8)});
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(7, 1), new Rational(3, 4)}));
				// Bar 8 (meter = 5/16; diminution = 1)
				mp.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
				mp.add(new Rational[]{new Rational(8, 1), new Rational(1, 8)});
				mp.add(new Rational[]{new Rational(8, 1), new Rational(3, 16)});
				mp.add(new Rational[]{new Rational(8, 1), new Rational(1, 4)});
				// Bar 9 (meter = 2/2; diminution = -2)
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(9, 1), new Rational(0, 512)}));
				mp.add(new Rational[]{new Rational(9, 1), new Rational(1, 2)});
				mp.add(new Rational[]{new Rational(9, 1), new Rational(5, 8)});
				mp.add(new Rational[]{new Rational(9, 1), new Rational(11, 16)});
				mp.addAll(Collections.nCopies(2, new Rational[]{new Rational(9, 1), new Rational(3, 4)}));
			}
			else {
				mp = getMetricPositions("testGetMeterInfo", true);
				List<Integer> diminutions = Arrays.asList(
					2, 2, 2, 
					2, 2, 2, 2, 2,
					2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
					4, 4, 4, 4, 4, 4, 4,
					4, 4, 4, 4, 4,
					1, 1, 1, 1, 
					1, 1, 1, 1, 1, 1, 1, 1, 
					1, 1, 1, 1,
					-2, -2, -2, -2, -2, -2, -2
				);
				for (int i = 0; i < diminutions.size(); i++) {
					int dim = diminutions.get(i);
					mp.set(i, new Rational[]{mp.get(i)[0], 
						dim > 1 ? mp.get(i)[1].mul(dim) : 
						mp.get(i)[1].div(Math.abs(dim))});
				}
			}
		}
		return mp;
	}


	public void testMakeBars() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline t = new Timeline();

		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{1, 1});
		expected.add(new Integer[]{2, 3});
		expected.add(new Integer[]{4, 5});
		expected.add(new Integer[]{6, 7});
		expected.add(new Integer[]{8, 8});
		expected.add(new Integer[]{9, 9});

		List<Integer[]> actual = t.makeBars(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeDiminutions() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline t = new Timeline();
		t.setBars(e);

		List<Integer> expected = Arrays.asList(2, 2, 4, 1, 1, -2);

		List<Integer> actual = t.makeDiminutions(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);	
	}


	public void testMakeTimeSignatures() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline t = new Timeline();
		t.setBars(e);
		t.setDiminutions(e);

		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 0}); // mt = 0; length = 36
		expected.add(new Integer[]{2, 2, 0 + 36}); // mt = 36; length = 192
		expected.add(new Integer[]{3, 4, 0 + 36 + 192}); // mt = 228; length = 144
		expected.add(new Integer[]{2, 2, 0 + 36 + 192 + 144}); // mt = 372;  length = 192
		expected.add(new Integer[]{5, 16, 0 + 36 + 192 + 144 + 192}); // mt = 564; length = 30
		expected.add(new Integer[]{2, 2, 0 + 36 + 192 + 144 + 192 + 30}); // mt = 594

		List<Integer[]> actual = t.makeTimeSignatures(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeDiminutionPerBar() {
		Encoding e1 = new Encoding(encodingTestGetMeterInfo);
		Timeline tl1 = new Timeline(e1);
		tl1.setBars(e1);
		tl1.setDiminutions(e1);
//		tl1.setMeterInfo(e1);
		tl1.setTimeSignatures(e1);

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);
		tl2.setBars(e2);
		tl2.setDiminutions(e2);
//		tl2.setMeterInfo(e2);
		tl2.setTimeSignatures(e2);

		// For testGetMeterInfo
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{1, 2});
		expected.add(new Integer[]{2, 2});
		expected.add(new Integer[]{3, 2});
		expected.add(new Integer[]{4, 4});
		expected.add(new Integer[]{5, 4});
		expected.add(new Integer[]{6, 1});
		expected.add(new Integer[]{7, 1});
		expected.add(new Integer[]{8, 1});
		expected.add(new Integer[]{9, -2});
		// For testPiece
		expected.add(new Integer[]{1, 1});
		expected.add(new Integer[]{2, 1});
		expected.add(new Integer[]{3, 1});

		List<Integer[]> actual = tl1.makeDiminutionPerBar();
		actual.addAll(tl2.makeDiminutionPerBar());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetTimeSignatureOnset() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		List<Integer[]> ts = e.getTimeline().getTimeSignatures();
		List<Integer[]> b = e.getTimeline().getBars();

		List<Integer[]> ts1 = new ArrayList<>(); 
		ts1.add(ts.get(0));
		List<Integer[]> ts2 = new ArrayList<>();
		ts2.add(ts.get(0)); ts2.add(ts.get(1));
		List<Integer[]> ts3 = new ArrayList<>();
		ts3.add(ts.get(0)); ts3.add(ts.get(1)); ts3.add(ts.get(2));
		List<Integer[]> ts4 = new ArrayList<>();
		ts4.add(ts.get(0)); ts4.add(ts.get(1)); ts4.add(ts.get(2));
		ts4.add(ts.get(3));
		List<Integer[]> ts5 = new ArrayList<>();
		ts5.add(ts.get(0)); ts5.add(ts.get(1)); ts5.add(ts.get(2));
		ts5.add(ts.get(3)); ts5.add(ts.get(4));

		List<Integer[]> b1 = new ArrayList<>();
		b1.add(b.get(0));
		List<Integer[]> b2 = new ArrayList<>();
		b2.add(b.get(0)); b2.add(b.get(1));
		List<Integer[]> b3 = new ArrayList<>();
		b3.add(b.get(0)); b3.add(b.get(1)); b3.add(b.get(2));
		List<Integer[]> b4 = new ArrayList<>();
		b4.add(b.get(0)); b4.add(b.get(1)); b4.add(b.get(2));
		b4.add(b.get(3));
		List<Integer[]> b5 = new ArrayList<>();
		b5.add(b.get(0)); b5.add(b.get(1)); b5.add(b.get(2));
		b5.add(b.get(3)); b5.add(b.get(4));

		List<Integer> expected = Arrays.asList(36, 228, 372, 564, 594);

		List<Integer> actual = Arrays.asList(
			Timeline.getTimeSignatureOnset(1, ts1, b1),
			Timeline.getTimeSignatureOnset(2, ts2, b2),
			Timeline.getTimeSignatureOnset(3, ts3, b3),
			Timeline.getTimeSignatureOnset(4, ts4, b4),
			Timeline.getTimeSignatureOnset(5, ts5, b5)
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	public void testGetDiminution() {				
		List<Integer> expected = new ArrayList<>();
		// For testGetMeterInfo
		expected.addAll(Arrays.asList(new Integer[]{2, 2, 2, 4, 4, 1, 1, 1, -2}));
		// For testpiece
		expected.addAll(Arrays.asList(new Integer[]{1, 1, 1}));

		List<Integer> actual = new ArrayList<>();
		List<Timeline> tls = Arrays.asList(new Timeline[]{
			new Timeline(new Encoding(encodingTestGetMeterInfo)),
			new Timeline(new Encoding(encodingTestpiece))});
		for (Timeline tl : tls) {
			// Take into account anacrusis
			boolean anacrusis = tl.getNumberOfMetricBars()[1] == 1;
			int startBar = anacrusis ? 0 : 1;			
			int stopBar = tl.getNumberOfMetricBars()[0];
			for (int bar = startBar; bar <= stopBar; bar++) {
				actual.add(tl.getDiminution(bar));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetNumberOfMetricBars() {		
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{9, 0});
		expected.add(new Integer[]{3, 0});

		List<Integer[]> actual = new ArrayList<>();
		actual.add(new Timeline(new Encoding(encodingTestGetMeterInfo)).getNumberOfMetricBars());
		actual.add(new Timeline(new Encoding(encodingTestpiece)).getNumberOfMetricBars());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetMetricPosition() {
		List<Tablature> tabs = Arrays.asList(
			new Tablature(encodingTestpiece),
			new Tablature(encodingTestGetMeterInfo)
		);

		List<Rational[]> expected = getMetricPositions("testpiece", true);
		expected.addAll(getMetricPositions("testGetMeterInfo", true));
		
		List<Rational[]> actual = new ArrayList<>();
		for (Tablature t : tabs) {
			Timeline tl = t.getEncoding().getTimeline();
			for (Integer[] in : t.getBasicTabSymbolProperties()) {
				actual.add(tl.getMetricPosition(in[Tablature.ONSET_TIME]));
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


//	public void testGetMeterSectionOLD() {
//		Rational quarter = new Rational(1, 4);
//
//		// For testGetMeterInfo
//		List<Integer> expected = Arrays.asList(new Integer[]{
//			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5	
//		});
//
//		List<Rational> mps = Arrays.asList(new Rational[]{
//			new Rational(0, 4), new Rational(0, 4).add(quarter), // meter section 0
//			new Rational(3, 4), new Rational(3, 4).add(quarter), // meter section 1
//			new Rational(19, 4), new Rational(19, 4).add(quarter), // meter section 2
//			new Rational(43, 4), new Rational(43, 4).add(quarter), // meter section 3
//			new Rational(51, 4), new Rational(51, 4).add(quarter), // meter section 4
//			new Rational(209, 16), new Rational(209, 16).add(quarter.div(4)) // meter section 5
//		});
//		List<Rational> meterSectionOnsets = Arrays.asList(new Rational[]{
//			new Rational(0, 4),
//			new Rational(3, 4),
//			new Rational(19, 4),
//			new Rational(43, 4),
//			new Rational(51, 4),
//			new Rational(209, 16)
//		});
//		List<Integer> actual = new ArrayList<>();
//		for (int i = 0; i < mps.size(); i++) {		
//			actual.add(Timeline.getMeterSectionOLD(mps.get(i), meterSectionOnsets));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));		
//		}
//		assertEquals(expected, actual);
//	}


//	public void testGetMeterSectionAltOLD() {
//		long quarter = 600000;
//
//		// For testGetMeterInfo
//		List<Integer> expected = Arrays.asList(new Integer[]{
//			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5	
//		});
//
//		List<Long> times = Arrays.asList(new Long[]{
//			(long) 0, (long) 0 + quarter, // meter section 0
//			(long) 1800000, (long) 1800000 + quarter, // meter section 1
//			(long) 11400000, (long) 11400000 + quarter, // meter section 2
//			(long) 25800000, (long) 25800000 + quarter, // meter section 3
//			(long) 30600000, (long) 30600000 + quarter, // meter section 4
//			(long) 31350000, (long) 31350000 + (quarter/4) // meter section 5
//		});
//		List<Long> meterSectionTimes = Arrays.asList(new Long[]{
//			(long) 0, 
//			(long) 1800000, 
//			(long) 11400000, 
//			(long) 25800000, 
//			(long) 30600000, 
//			(long) 31350000
//		});
//		List<Integer> actual = new ArrayList<>();
//		for (int i = 0; i < times.size(); i++) {		
//			actual.add(Timeline.getMeterSectionOLD(times.get(i), meterSectionTimes));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));		
//		}
//		assertEquals(expected, actual);
//	}


//	public void testGetDiminutedMetricPositionOLD() {
//		Rational quarter = new Rational(1, 4);
//		
//		// For testGetMeterInfo
//		List<Rational> expected = Arrays.asList(new Rational[]{
//			new Rational(0, 8), new Rational(0, 8).add(quarter.div(2)), // meter section 0
//			new Rational(3, 8), new Rational(3, 8).add(quarter.div(2)), // meter section 1
//			new Rational(19, 8), new Rational(19, 8).add(quarter.div(4)), // meter section 2
//			new Rational(31, 8), new Rational(31, 8).add(quarter.div(1)), // meter section 3
//			new Rational(47, 8), new Rational(47, 8).add(quarter.div(1)), // meter section 4
//			new Rational(99, 16), new Rational(99, 16).add(new Rational(1, 16).mul(2)) // meter section 5
//		});
//
//		List<Rational> mps = Arrays.asList(new Rational[]{
//			new Rational(0, 4), new Rational(0, 4).add(quarter), // meter section 0
//			new Rational(3, 4), new Rational(3, 4).add(quarter), // meter section 1
//			new Rational(19, 4), new Rational(19, 4).add(quarter), // meter section 2
//			new Rational(43, 4), new Rational(43, 4).add(quarter), // meter section 3
//			new Rational(51, 4), new Rational(51, 4).add(quarter), // meter section 4
//			new Rational(209, 16), new Rational(209, 16).add(quarter.div(4)) // meter section 5
//		});
//		List<Rational> meterSectionOnsets = Arrays.asList(new Rational[]{
//			new Rational(0, 4),
//			new Rational(3, 4),
//			new Rational(19, 4),
//			new Rational(43, 4),
//			new Rational(51, 4),
//			new Rational(209, 16)
//		});
//		List<Rational> meterSectionOnsetsDim = Arrays.asList(new Rational[]{
//			new Rational(0, 8),
//			new Rational(3, 8),
//			new Rational(19, 8),
//			new Rational(31, 8),
//			new Rational(47, 8),
//			new Rational(99, 16)
//		});
//		List<Integer> diminutions = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
//		List<Rational> actual = new ArrayList<>();
//		for (int i = 0; i < mps.size(); i++) {
//			actual.add(Timeline.getDiminutedMetricPositionOLD(mps.get(i), meterSectionOnsets, 
//				meterSectionOnsetsDim, diminutions));
//		}
//	
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));		
//		}
//		assertEquals(expected, actual);
//	}


//	public void testGetDiminutedTimeNOTINUSE() {
//		long quarter = 600000;
//
//		// For testGetMeterInfo
//		List<Long> expected = Arrays.asList(new Long[]{
//			(long) 0, (long) 0 + (quarter/2), // meter section 0
//			(long) 900000, (long) 900000 + (quarter/2), // meter section 1
//			(long) 5700000, (long) 5700000 + (quarter/4), // meter section 2
//			(long) 9300000, (long) 9300000 + quarter, // meter section 3
//			(long) 14100000, (long) 14100000 + quarter, // meter section 4
//			(long) 14850000, (long) 14850000 + ((quarter/4)*2), // meter section 5
//		});
//
//		List<Long> times = Arrays.asList(new Long[]{
//			(long) 0, (long) 0 + quarter, // meter section 0
//			(long) 1800000, (long) 1800000 + quarter, // meter section 1
//			(long) 11400000, (long) 11400000 + quarter, // meter section 2
//			(long) 25800000, (long) 25800000 + quarter, // meter section 3
//			(long) 30600000, (long) 30600000 + quarter, // meter section 4
//			(long) 31350000, (long) 31350000 + (quarter/4), // meter section 5
//		});
//		List<Long> meterSectionTimes = Arrays.asList(new Long[]{
//			(long) 0, 
//			(long) 1800000, 
//			(long) 11400000, 
//			(long) 25800000, 
//			(long) 30600000, 
//			(long) 31350000
//		});
//		List<Long> meterSectionTimesDim = Arrays.asList(new Long[]{
//			(long) 0,
//			(long) 900000,
//			(long) 5700000,
//			(long) 9300000,
//			(long) 14100000,
//			(long) 14850000
//		});
//		List<Integer> diminutions = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
//		List<Long> actual = new ArrayList<>();
//		for (int i = 0; i < times.size(); i++) {
//			actual.add(Timeline.getDiminutedTimeNOTINUSE(times.get(i), meterSectionTimes, 
//				meterSectionTimesDim, diminutions));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));		
//		}
//		assertEquals(expected, actual);
//	}


	private List<List<Rational[]>> getMetricPositionsOBS() {
		List<List<Rational[]>> all = new ArrayList<>();

		// For testPiece (meter = 2/2; diminution = 1)
		List<Rational[]> expectedTestPiece = new ArrayList<Rational[]>();
		// Bar 1
		Rational[] chord0 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord0); expectedTestPiece.add(chord0); expectedTestPiece.add(chord0); expectedTestPiece.add(chord0);
		// Bar 2
		Rational[] chord1 = new Rational[]{new Rational(2, 1), new Rational(0, 64)};
		expectedTestPiece.add(chord1); expectedTestPiece.add(chord1); expectedTestPiece.add(chord1); expectedTestPiece.add(chord1);    
		Rational[] chord2 = new Rational[]{new Rational(2, 1), new Rational(3, 16)};
		expectedTestPiece.add(chord2); 
		Rational[] chord3 = new Rational[]{new Rational(2, 1), new Rational(1, 4)};
		expectedTestPiece.add(chord3); expectedTestPiece.add(chord3); expectedTestPiece.add(chord3); expectedTestPiece.add(chord3);
		Rational[] chord4 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
		expectedTestPiece.add(chord4); 
		Rational[] chord5 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5);
		Rational[] chord6 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord6); expectedTestPiece.add(chord6); expectedTestPiece.add(chord6); expectedTestPiece.add(chord6);
		Rational[] chord7 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
		expectedTestPiece.add(chord7); expectedTestPiece.add(chord7);
		// Bar 3
		Rational[] chord8 = new Rational[]{new Rational(3, 1), new Rational(0, 64)};
		expectedTestPiece.add(chord8); expectedTestPiece.add(chord8); expectedTestPiece.add(chord8); expectedTestPiece.add(chord8);
		Rational[] chord9 = new Rational[]{new Rational(3, 1), new Rational(1, 16)};
		expectedTestPiece.add(chord9); 
		Rational[] chord10 = new Rational[]{new Rational(3, 1), new Rational(1, 8)};
		expectedTestPiece.add(chord10); 
		Rational[] chord11 = new Rational[]{new Rational(3, 1), new Rational(5, 32)};
		expectedTestPiece.add(chord11); 
		Rational[] chord12 = new Rational[]{new Rational(3, 1), new Rational(3, 16)};
		expectedTestPiece.add(chord12); 
		Rational[] chord13 = new Rational[]{new Rational(3, 1), new Rational(7, 32)};
		expectedTestPiece.add(chord13); 
		Rational[] chord14 = new Rational[]{new Rational(3, 1), new Rational(1, 4)};
		expectedTestPiece.add(chord14); 
		Rational[] chord15 = new Rational[]{new Rational(3, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); 

		// For testGetMeterInfo
		List<Rational[]> expectedTestGetMeterInfo = new ArrayList<Rational[]>();
		// Bar 0 (meter = 2/2; diminution = 2): anacrusis length is 3/8 
		chord0 = new Rational[]{new Rational(0, 1), new Rational(5, 4)};
		expectedTestGetMeterInfo.add(chord0); 
		chord1 = new Rational[]{new Rational(0, 1), new Rational(3, 2)};
		expectedTestGetMeterInfo.add(chord1);
		chord2 = new Rational[]{new Rational(0, 1), new Rational(7, 4)};
		expectedTestGetMeterInfo.add(chord2);
		// Bar 1 (meter = 2/2; diminution = 2)
		chord3 = new Rational[]{new Rational(1, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord3); expectedTestGetMeterInfo.add(chord3);
		chord4 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord4); 
		chord5 = new Rational[]{new Rational(1, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord5); expectedTestGetMeterInfo.add(chord5);
		// Bar 2 (meter = 2/2; diminution = 2)
		chord6 = new Rational[]{new Rational(2, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord6); expectedTestGetMeterInfo.add(chord6);
		chord7 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord7);
		chord8 = new Rational[]{new Rational(2, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord8);
		chord9 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord9);
		chord10 = new Rational[]{new Rational(2, 1), new Rational(13, 16)};
		expectedTestGetMeterInfo.add(chord10);
		chord11 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
		expectedTestGetMeterInfo.add(chord11);
		chord12 = new Rational[]{new Rational(2, 1), new Rational(15, 16)};
		expectedTestGetMeterInfo.add(chord12);
		chord13 = new Rational[]{new Rational(2, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord13); expectedTestGetMeterInfo.add(chord13);
		// Bar 3 (meter = 3/4; diminution = 4)
		chord14 = new Rational[]{new Rational(3, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord14); expectedTestGetMeterInfo.add(chord14);
		chord15 = new Rational[]{new Rational(3, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord15); // new Rational(1, 4)});
		Rational[] chord16 = new Rational[]{new Rational(3, 1), new Rational(3, 2)};
		expectedTestGetMeterInfo.add(chord16); // new Rational(3, 8)});
		Rational[] chord17 = new Rational[]{new Rational(3, 1), new Rational(7, 4)};
		expectedTestGetMeterInfo.add(chord17); // new Rational(7, 16)});
		Rational[] chord18 = new Rational[]{new Rational(3, 1), new Rational(2, 1)};
		expectedTestGetMeterInfo.add(chord18); expectedTestGetMeterInfo.add(chord18);
		// Bar 4 (meter = 3/4; diminution = 4)
		Rational[] chord19 = new Rational[]{new Rational(4, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord19);
		Rational[] chord20 = new Rational[]{new Rational(4, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord20);
		Rational[] chord21 = new Rational[]{new Rational(4, 1), new Rational(7, 8)};
		expectedTestGetMeterInfo.add(chord21);
		Rational[] chord22 = new Rational[]{new Rational(4, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord22); expectedTestGetMeterInfo.add(chord22);
		// Bar 5 (meter = 2/2; diminution = 1)
		Rational[] chord23 = new Rational[]{new Rational(5, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord23); expectedTestGetMeterInfo.add(chord23);
		Rational[] chord24 = new Rational[]{new Rational(5, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord24); expectedTestGetMeterInfo.add(chord24);
		// Bar 6 (meter = 2/2; diminution = 1)
		Rational[] chord25 = new Rational[]{new Rational(6, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord25);
		Rational[] chord26 = new Rational[]{new Rational(6, 1), new Rational(1, 8)};
		expectedTestGetMeterInfo.add(chord26);
		Rational[] chord27 = new Rational[]{new Rational(6, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord27);
		Rational[] chord28 = new Rational[]{new Rational(6, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord28);
		Rational[] chord29 = new Rational[]{new Rational(6, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord29);
		Rational[] chord30 = new Rational[]{new Rational(6, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord30);
		Rational[] chord31 = new Rational[]{new Rational(6, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord31); expectedTestGetMeterInfo.add(chord31);
		// Bar 7 (meter = 5/16; diminution = 1)
		Rational[] chord32 = new Rational[]{new Rational(7, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord32);
		Rational[] chord33 = new Rational[]{new Rational(7, 1), new Rational(1, 8)};
		expectedTestGetMeterInfo.add(chord33);
		Rational[] chord34 = new Rational[]{new Rational(7, 1), new Rational(3, 16)};
		expectedTestGetMeterInfo.add(chord34);
		Rational[] chord35 = new Rational[]{new Rational(7, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord35);
		// Bar 8 (meter = 2/2; diminution = -2)
		Rational[] chord36 = new Rational[]{new Rational(8, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord36); expectedTestGetMeterInfo.add(chord36);
		Rational[] chord37 = new Rational[]{new Rational(8, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord37);
		Rational[] chord38 = new Rational[]{new Rational(8, 1), new Rational(5, 16)};
		expectedTestGetMeterInfo.add(chord38);
		Rational[] chord39 = new Rational[]{new Rational(8, 1), new Rational(11, 32)};
		expectedTestGetMeterInfo.add(chord39);
		Rational[] chord40 = new Rational[]{new Rational(8, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord40); expectedTestGetMeterInfo.add(chord40);

		all.add(expectedTestPiece);
		all.add(expectedTestGetMeterInfo);
		return all;
	}

//	public void testMakeUndiminutedMeterInfoOBS() {
//		Encoding e = new Encoding(encodingTestGetMeterInfo);
//		Timeline tl = new Timeline(e);
//
//		List<Integer[]> expected = new ArrayList<>();
//		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
//		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
//		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
//		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
//		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
//		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});
//
//		List<Integer[]> actual = tl.makeUndiminutedMeterInfoOBS(e);
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//	  		assertEquals(expected.get(i).length, actual.get(i).length);
//	  		for (int j = 0; j < expected.get(i).length; j++) {
//	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
//	  		}
//		}
//	}


//	public void testMakeMeterInfoOBS() {
//		Encoding e1 = new Encoding(encodingTestGetMeterInfo);
//		Timeline tl1 = new Timeline(e1);
//		tl1.setUndiminutedMeterInfoOBS(e1);
//
//		Encoding e2 = new Encoding(encodingTestpiece);
//		Timeline tl2 = new Timeline(e2);
//		tl2.setUndiminutedMeterInfoOBS(e2);
//
//		List<Integer[]> expected = new ArrayList<Integer[]>();
//		// t1
//		expected.add(new Integer[]{3, 4, 0, 0, 0, 1, 2});
//		expected.add(new Integer[]{2, 1, 1, 2, 3, 4, 2});
//		expected.add(new Integer[]{3, 1, 3, 4, 19, 4, 4});
//		expected.add(new Integer[]{2, 2, 5, 6, 43, 4, 1});
//		expected.add(new Integer[]{5, 16, 7, 7, 51, 4, 1});
//		expected.add(new Integer[]{2, 4, 8, 8, 209, 16, -2});
//		// t2		
//		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});
//
//		List<Integer[]> actual = tl1.makeMeterInfoOBS(e1);
//		actual.addAll(tl2.makeMeterInfoOBS(e2));
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//	  		assertEquals(expected.get(i).length, actual.get(i).length);
//	  		for (int j = 0; j < expected.get(i).length; j++) {
//	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
//	  		}
//		}
//	}

}
