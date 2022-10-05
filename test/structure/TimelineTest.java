package structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import path.Path;
import representations.Tablature;
import representations.Transcription;
import structure.Timeline;
import tbp.Encoding;

public class TimelineTest extends TestCase {
	
	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;

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
	}


	@After
	public void tearDown() throws Exception {
	}


	private List<List<Rational[]>> getMetricPositions() {
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
		chord0 = new Rational[]{new Rational(0, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord0); 
		chord1 = new Rational[]{new Rational(0, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord1);
		chord2 = new Rational[]{new Rational(0, 1), new Rational(7, 8)};
		expectedTestGetMeterInfo.add(chord2);
		// Bar 1 (meter = 2/2; diminution = 2)
		chord3 = new Rational[]{new Rational(1, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord3); expectedTestGetMeterInfo.add(chord3);
		chord4 = new Rational[]{new Rational(1, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord4); 
		chord5 = new Rational[]{new Rational(1, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord5); expectedTestGetMeterInfo.add(chord5);
		// Bar 2 (meter = 2/2; diminution = 2)
		chord6 = new Rational[]{new Rational(2, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord6); expectedTestGetMeterInfo.add(chord6);
		chord7 = new Rational[]{new Rational(2, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord7);
		chord8 = new Rational[]{new Rational(2, 1), new Rational(5, 16)};
		expectedTestGetMeterInfo.add(chord8);
		chord9 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord9);
		chord10 = new Rational[]{new Rational(2, 1), new Rational(13, 32)};
		expectedTestGetMeterInfo.add(chord10);
		chord11 = new Rational[]{new Rational(2, 1), new Rational(7, 16)};
		expectedTestGetMeterInfo.add(chord11);
		chord12 = new Rational[]{new Rational(2, 1), new Rational(15, 32)};
		expectedTestGetMeterInfo.add(chord12);
		chord13 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord13); expectedTestGetMeterInfo.add(chord13);
		// Bar 3 (meter = 3/4; diminution = 4)
		chord14 = new Rational[]{new Rational(3, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord14); expectedTestGetMeterInfo.add(chord14);
		chord15 = new Rational[]{new Rational(3, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord15); // new Rational(1, 4)});
		Rational[] chord16 = new Rational[]{new Rational(3, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord16); // new Rational(3, 8)});
		Rational[] chord17 = new Rational[]{new Rational(3, 1), new Rational(7, 16)};
		expectedTestGetMeterInfo.add(chord17); // new Rational(7, 16)});
		Rational[] chord18 = new Rational[]{new Rational(3, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord18); expectedTestGetMeterInfo.add(chord18);
		// Bar 4 (meter = 3/4; diminution = 4)
		Rational[] chord19 = new Rational[]{new Rational(4, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord19);
		Rational[] chord20 = new Rational[]{new Rational(4, 1), new Rational(3, 16)};
		expectedTestGetMeterInfo.add(chord20);
		Rational[] chord21 = new Rational[]{new Rational(4, 1), new Rational(7, 32)};
		expectedTestGetMeterInfo.add(chord21);
		Rational[] chord22 = new Rational[]{new Rational(4, 1), new Rational(1, 4)};
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
		Rational[] chord37 = new Rational[]{new Rational(8, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord37);
		Rational[] chord38 = new Rational[]{new Rational(8, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord38);
		Rational[] chord39 = new Rational[]{new Rational(8, 1), new Rational(11, 16)};
		expectedTestGetMeterInfo.add(chord39);
		Rational[] chord40 = new Rational[]{new Rational(8, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord40); expectedTestGetMeterInfo.add(chord40);

		all.add(expectedTestPiece);
		all.add(expectedTestGetMeterInfo);
		return all;
	}


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


	public void testMakeMeterInfo() {
		Encoding e1 = new Encoding(encodingTestGetMeterInfo);
		Timeline tl1 = new Timeline(e1);

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);

		List<Integer[]> expected = new ArrayList<Integer[]>();
		// t1
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1, 2});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8, 2});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8, 4});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8, 1});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8, 1});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16, -2});

		// t2		
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(tl1.makeMeterInfo(e1));
		actual.addAll(tl2.makeMeterInfo(e2));

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
		tl1.setMeterInfo(e1);
//		tl1.setUndiminutedMeterInfoOBS(e1);
//		tl1.setMeterInfoOBS(e1);

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);
		tl2.setMeterInfo(e2);
//		tl2.setUndiminutedMeterInfoOBS(e2);
//		tl2.setMeterInfoOBS(e2);

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
			actual.add(Timeline.diminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


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
			actual.add(Timeline.undiminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


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
			actual.add(Timeline.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testDiminuteAlt() {
		List<Double> expected = Arrays.asList(new Double[]{1.5, 1.0, 1.0, 1.5, 0.375});

		List<Double> undim = Arrays.asList(new Double[]{0.375, 0.5, 1.0, 3.0, 1.5});
		List<Integer> diminutions = Arrays.asList(new Integer[]{-4, -2, 1, 2, 4});
		List<Double> actual = new ArrayList<>();
		for (int i = 0; i < diminutions.size(); i++) {
			actual.add(Timeline.diminute(undim.get(i), diminutions.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetMeterSection() {
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
		List<Rational> meterSectionOnsets = Arrays.asList(new Rational[]{
			new Rational(0, 4),
			new Rational(3, 4),
			new Rational(19, 4),
			new Rational(43, 4),
			new Rational(51, 4),
			new Rational(209, 16)
		});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < mps.size(); i++) {		
			actual.add(Timeline.getMeterSection(mps.get(i), meterSectionOnsets));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);	
	}


	public void testGetMeterSectionAlt() {
		long quarter = 600000;

		// For testGetMeterInfo
		List<Integer> expected = Arrays.asList(new Integer[]{
			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5	
		});

		List<Long> times = Arrays.asList(new Long[]{
			(long) 0, (long) 0 + quarter, // meter section 0
			(long) 1800000, (long) 1800000 + quarter, // meter section 1
			(long) 11400000, (long) 11400000 + quarter, // meter section 2
			(long) 25800000, (long) 25800000 + quarter, // meter section 3
			(long) 30600000, (long) 30600000 + quarter, // meter section 4
			(long) 31350000, (long) 31350000 + (quarter/4) // meter section 5
		});
		List<Long> meterSectionTimes = Arrays.asList(new Long[]{
			(long) 0, 
			(long) 1800000, 
			(long) 11400000, 
			(long) 25800000, 
			(long) 30600000, 
			(long) 31350000
		});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < times.size(); i++) {		
			actual.add(Timeline.getMeterSection(times.get(i), meterSectionTimes));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	public void testGetDiminutedMetricPosition() {
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
		List<Rational> meterSectionOnsets = Arrays.asList(new Rational[]{
			new Rational(0, 4),
			new Rational(3, 4),
			new Rational(19, 4),
			new Rational(43, 4),
			new Rational(51, 4),
			new Rational(209, 16)
		});
		List<Rational> meterSectionOnsetsDim = Arrays.asList(new Rational[]{
			new Rational(0, 8),
			new Rational(3, 8),
			new Rational(19, 8),
			new Rational(31, 8),
			new Rational(47, 8),
			new Rational(99, 16)
		});
		List<Integer> diminutions = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < mps.size(); i++) {
			actual.add(Timeline.getDiminutedMetricPosition(mps.get(i), meterSectionOnsets, 
				meterSectionOnsetsDim, diminutions));
		}
	
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	public void testGetDiminutedTime() {
		long quarter = 600000;

		// For testGetMeterInfo
		List<Long> expected = Arrays.asList(new Long[]{
			(long) 0, (long) 0 + (quarter/2), // meter section 0
			(long) 900000, (long) 900000 + (quarter/2), // meter section 1
			(long) 5700000, (long) 5700000 + (quarter/4), // meter section 2
			(long) 9300000, (long) 9300000 + quarter, // meter section 3
			(long) 14100000, (long) 14100000 + quarter, // meter section 4
			(long) 14850000, (long) 14850000 + ((quarter/4)*2), // meter section 5
		});

		List<Long> times = Arrays.asList(new Long[]{
			(long) 0, (long) 0 + quarter, // meter section 0
			(long) 1800000, (long) 1800000 + quarter, // meter section 1
			(long) 11400000, (long) 11400000 + quarter, // meter section 2
			(long) 25800000, (long) 25800000 + quarter, // meter section 3
			(long) 30600000, (long) 30600000 + quarter, // meter section 4
			(long) 31350000, (long) 31350000 + (quarter/4), // meter section 5
		});
		List<Long> meterSectionTimes = Arrays.asList(new Long[]{
			(long) 0, 
			(long) 1800000, 
			(long) 11400000, 
			(long) 25800000, 
			(long) 30600000, 
			(long) 31350000
		});
		List<Long> meterSectionTimesDim = Arrays.asList(new Long[]{
			(long) 0,
			(long) 900000,
			(long) 5700000,
			(long) 9300000,
			(long) 14100000,
			(long) 14850000
		});
		List<Integer> diminutions = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
		List<Long> actual = new ArrayList<>();
		for (int i = 0; i < times.size(); i++) {
			actual.add(Timeline.getDiminutedTime(times.get(i), meterSectionTimes, 
				meterSectionTimesDim, diminutions));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	public void testGetMetricPosition() {
		// For a piece with meter changes
		Tablature t1 = new Tablature(encodingTestGetMeterInfo, false);
		List<Rational[]> expected = getMetricPositions().get(1);

		// For a piece with no meter changes
		Tablature t2 = new Tablature(encodingTestpiece, false);
		expected.addAll(getMetricPositions().get(0));

		List<Rational[]> actual = new ArrayList<Rational[]>();
		Integer[][] btp1 = t1.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo1 = t1.getTimeline().getMeterInfo();
//		List<Integer[]> meterInfo1 = t1.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp1.length; i++) {
			Rational currMetricTime = 
				new Rational(btp1[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Timeline.getMetricPosition(currMetricTime, meterInfo1));
		}
		Integer[][] btp2 = t2.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo2 = t2.getTimeline().getMeterInfo();
//		List<Integer[]> meterInfo2 = t2.getTimeline().getMeterInfoOBS();
		for (int i = 0; i < btp2.length; i++) {
			Rational currMetricTime = 
				new Rational(btp2[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Timeline.getMetricPosition(currMetricTime, meterInfo2));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetDiminutionStatic() {
		List<List<Rational>> allMetricTimes = new ArrayList<>();
		// For testGetMeterInfo
		List<Rational> metricTimes1 = new ArrayList<>();
		metricTimes1.add(new Rational(0, 1)); // start bar 0 (anacrusis)
		metricTimes1.add(new Rational(5, 8)); // bar 1 1/4
		metricTimes1.add(new Rational(11, 8)); // start bar 2
		metricTimes1.add(new Rational(21, 8)); // bar 3 1/4
		metricTimes1.add(new Rational(25, 8)); // start bar 4
		metricTimes1.add(new Rational(33, 8)); // bar 5 1/4
		metricTimes1.add(new Rational(39, 8)); // start bar 6
		metricTimes1.add(new Rational(48, 8)); // in bar 7 2/16
		metricTimes1.add(new Rational(99, 16)); // start bar 8
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
		List<Timeline> tls = Arrays.asList(new Timeline[]{
			new Timeline(new Encoding(encodingTestGetMeterInfo)),
			new Timeline(new Encoding(encodingTestpiece))});
		for (int i = 0; i < tls.size(); i++) {
			Timeline tl = tls.get(i);
			for (Rational mt : allMetricTimes.get(i)) {
				actual.add(Timeline.getDiminution(mt, tl.getMeterInfo()));
//				actual.add(Timeline.getDiminution(mt, tl.getMeterInfoOBS()));
			}
		}

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
		expected.add(new Integer[]{8, 1});
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


	public void testMakeUndiminutedMeterInfoOBS() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);
		Timeline tl = new Timeline(e);

		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});

		List<Integer[]> actual = tl.makeUndiminutedMeterInfoOBS(e);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeMeterInfoOBS() {
		Encoding e1 = new Encoding(encodingTestGetMeterInfo);
		Timeline tl1 = new Timeline(e1);
		tl1.setUndiminutedMeterInfoOBS(e1);

		Encoding e2 = new Encoding(encodingTestpiece);
		Timeline tl2 = new Timeline(e2);
		tl2.setUndiminutedMeterInfoOBS(e2);

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

		List<Integer[]> actual = tl1.makeMeterInfoOBS(e1);
		actual.addAll(tl2.makeMeterInfoOBS(e2));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}

}
