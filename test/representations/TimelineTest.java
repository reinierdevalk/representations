package representations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import paths.Paths;

public class TimelineTest extends TestCase {
	
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		String root = Paths.getRootPath() + Paths.getDataDir(); 
		encodingTestpiece = 
			new File(root + Paths.getEncodingsPath() + Paths.getTestDir() + "testpiece.tbp");
		encodingTestGetMeterInfo = 
			new File(root + Paths.getEncodingsPath() + Paths.getTestDir() + "test_get_meter_info.tbp");
		midiTestpiece = 
			new File(root + Paths.getMIDIPath() + Paths.getTestDir() + "testpiece.mid");
	}


	@After
	public void tearDown() throws Exception {
	}


	public void testMakeDiminutions() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline tl = new Timeline(e);

		List<Integer> expected = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
		List<Integer> actual = tl.makeDiminutions(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testMakeUndiminutedMeterInfo() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline tl = new Timeline(e);
		tl.setDiminutions(e);

		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});

		List<Integer[]> actual = tl.makeUndiminutedMeterInfo(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeMeterInfo() {
		Encoding e1 = new Encoding(encodingTestGetMeterInfo);
		Timeline tl1 = new Timeline(e1);
		tl1.setDiminutions(e1);
		tl1.setUndiminutedMeterInfo(e1);

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);
		tl2.setDiminutions(e2);
		tl2.setUndiminutedMeterInfo(e2);

		List<Integer[]> expected = new ArrayList<Integer[]>();
		// t1
		expected.add(new Integer[]{3, 4, 0, 0, 0, 1, 2});
		expected.add(new Integer[]{2, 1, 1, 2, 3, 4, 2});
		expected.add(new Integer[]{3, 1, 3, 4, 19, 4, 4});
		expected.add(new Integer[]{2, 2, 5, 6, 43, 4, 1});
		expected.add(new Integer[]{5, 16, 7, 7, 51, 4, 1});
		expected.add(new Integer[]{2, 4, 8, 8, 209, 16, -2});
		// t2		
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});

		List<Integer[]> actual = tl1.makeMeterInfo();
		actual.addAll(tl2.makeMeterInfo());

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
		tl1.setDiminutions(e1);
		tl1.setUndiminutedMeterInfo(e1);
		tl1.setMeterInfo();

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);
		tl2.setDiminutions(e2);
		tl2.setUndiminutedMeterInfo(e2);
		tl2.setMeterInfo();

		// For testGetMeterInfo
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{0, 2});
		expected.add(new Integer[]{1, 2});
		expected.add(new Integer[]{2, 2});
		expected.add(new Integer[]{3, 4});
		expected.add(new Integer[]{4, 4});
		expected.add(new Integer[]{5, 1});
		expected.add(new Integer[]{6, 1});
		expected.add(new Integer[]{7, 1});
		expected.add(new Integer[]{8, -2});
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


	public void testDiminuteMeter() {
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
			actual.add(Timeline.diminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testUndiminuteMeter() {
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
			actual.add(Timeline.undiminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}

}
