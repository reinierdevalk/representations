package representations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import junit.framework.TestCase;
import representations.Encoding.Tuning;
import tbp.RhythmSymbol;
import tbp.TabSymbol;
import tbp.TabSymbolSet;

public class TablatureTest extends TestCase {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File midiTestpiece;

	protected void setUp() throws Exception {
		super.setUp();
//		Runner.setPathsToCodeAndData(UI.getRootDir(), false);
//		encodingTestpiece1 = new File(Runner.encodingsPathTest + "testpiece.tbp");
//		encodingTestGetMeterInfo = new File(Runner.encodingsPathTest + "test_get_meter_info.tbp");
//		midiTestpiece1 = new File(Runner.midiPathTest + "testpiece.mid");
		encodingTestpiece = new File(MEIExport.rootDir + "data/annotated/encodings/test/"  + "testpiece.tbp");
		encodingTestGetMeterInfo = new File(MEIExport.rootDir + "data/annotated/encodings/test/" + "test_get_meter_info.tbp");
		midiTestpiece = new File(MEIExport.rootDir + "data/annotated/MIDI/test/" + "testpiece.mid");	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testGetOriginalMeterInfo() {
		Tablature tablature = new Tablature();
		tablature.setEncoding(new Encoding(encodingTestGetMeterInfo));
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});

		List<Integer[]> actual = tablature.getOriginalMeterInfo();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetDiminutions() {
		Tablature tablature = new Tablature();
		tablature.setEncoding(new Encoding(encodingTestGetMeterInfo));
		List<Integer> expected = Arrays.asList(new Integer[]{2, 2, 1, 4, 1, -2});
		List<Integer> actual = tablature.getDiminutions();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testCreateMeterInfo() {
		Tablature tablature1 = new Tablature();
		tablature1.setEncoding(new Encoding(encodingTestGetMeterInfo));
		Tablature tablature2 = new Tablature();
		tablature2.setEncoding(new Encoding(encodingTestpiece));
				
		List<Integer[]> expected = new ArrayList<Integer[]>();
		// tablature1
		expected.add(new Integer[]{3, 4, 0, 0, 0, 1, 2});
		expected.add(new Integer[]{2, 1, 1, 2, 3, 4, 2});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 4, 1});
		expected.add(new Integer[]{4, 1, 5, 6, 25, 4, 4});
		expected.add(new Integer[]{5, 16, 7, 7, 57, 4, 1});
		expected.add(new Integer[]{1, 2, 8, 8, 233, 16, -2});
		// tablature2		
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});

		List<Integer[]> actual = 
			tablature1.createMeterInfo(tablature1.getDiminutions());
		actual.addAll(tablature2.createMeterInfo(tablature2.getDiminutions()));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testAdaptToDiminutions() {
		List<Integer[]> unadaptedMeterInfo = Arrays.asList(new Integer[][]{
			new Integer[]{2, 2, 1, 1},
			new Integer[]{3, 2, 2, 2},
			new Integer[]{2, 2, 3, 3}
		});
		
		// Testpiece1 (anacrusis; diminutions = [2, 1, 2])
		// 2/2: Q(r) H    Q    | 3/2: H    H    H    | 2/2: H    Q(r) Q    ||	
		//                4                                           13
		//           1    3           6         9                     12   
		//           0    2           5    7    8           10        11
		List<Integer> diminutions1 = Arrays.asList(new Integer[]{2, 1, 2});
		List<Integer> durationOfTabSymbols1 = Arrays.asList(new Integer[]{
			48, 48, 24, 24, 24, 
			48, 48, 48, 48, 48,
			48, 24, 24, 24
		});
		List<Integer> gridXOfTabSymbols1 = Arrays.asList(new Integer[]{
			24, 24, 72, 72, 72, 
			96, 96, 144, 192, 192,
			240, 312, 312, 312
		});
		List<Integer> newDurationOfTabSymbols1 = Arrays.asList(new Integer[]{
			96, 96, 48, 48, 48,
			48, 48, 48, 48, 48,
			96, 48, 48, 48 
		});
		List<Integer> newGridXOfTabSymbols1 = Arrays.asList(new Integer[]{
			48, 48, 144, 144, 144,
			192, 192, 240, 288, 288,
			336, 480, 480, 480 
		});

		// Testpiece2 (no anacrusis; diminutions = [1, -2, 1])
		// 2/2: Q    H    Q    | 3/2: H    H    H    | 2/2: H    Q(r) Q    ||	
		//                5                                           14
		//           2    4           7         10                    13   
		//      0    1    3           6    8    9           11        12
		List<Integer> diminutions2 = Arrays.asList(new Integer[]{1, -2, 1});
		List<Integer> durationOfTabSymbols2 = Arrays.asList(new Integer[]{
			24, 48, 48, 24, 24, 24,
			48, 48, 48, 48, 48,
			48, 24, 24, 24
		});
		List<Integer> gridXOfTabSymbols2 = Arrays.asList(new Integer[]{
			0, 24, 24, 72, 72, 72,
			96, 96, 144, 192, 192,
			240, 312, 312, 312
		});
		List<Integer> newDurationOfTabSymbols2 = Arrays.asList(new Integer[]{
			24, 48, 48, 24, 24, 24,
			24, 24, 24, 24, 24,
			48, 24, 24, 24	
		});
		List<Integer> newGridXOfTabSymbols2 = Arrays.asList(new Integer[]{
			0, 24, 24, 72, 72, 72,	
			96, 96, 120, 144, 144,
			168, 240, 240, 240
		});

		List<List<Integer>> expected = new ArrayList<>();
		expected.add(newDurationOfTabSymbols1);
		expected.add(newGridXOfTabSymbols1);
		expected.add(newDurationOfTabSymbols2);
		expected.add(newGridXOfTabSymbols2);
		
		List<List<Integer>> actual = new ArrayList<>(); 
		actual.addAll(Tablature.adaptToDiminutions(durationOfTabSymbols1, gridXOfTabSymbols1, 
			diminutions1, unadaptedMeterInfo));
		actual.addAll(Tablature.adaptToDiminutions(durationOfTabSymbols2, gridXOfTabSymbols2, 
			diminutions2, unadaptedMeterInfo));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}

	
	public void testSetAndGetNumberOfNotesPerChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Integer> expected = Arrays.asList(new Integer[]{4, 4, 1, 4, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});
		List<Integer> actual = tablature.getNumberOfNotesPerChord();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testSetAndGetBasicTabSymbolProperties() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		Integer[][] expected = new Integer[39][10];
		// Chord 0
		expected[0] = new Integer[]{50, 5, 0, 72, 24, 48, 0, 4, 0, 3};
		expected[1] = new Integer[]{57, 4, 2, 72, 24, 24, 0, 4, 1, 3};
		expected[2] = new Integer[]{65, 2, 1, 72, 24, 24, 0, 4, 2, 3};
		expected[3] = new Integer[]{69, 1, 0, 72, 24, 24, 0, 4, 3, 3};
		// Chord 1
		expected[4] = new Integer[]{45, 6, 0, 96, 18, 18, 1, 4, 0, 5};
		expected[5] = new Integer[]{57, 4, 2, 96, 18, 24, 1, 4, 1, 5};
		expected[6] = new Integer[]{72, 2, 8, 96, 18, 24, 1, 4, 2, 5};
		expected[7] = new Integer[]{69, 1, 0, 96, 18, 72, 1, 4, 3, 5};
		// Chord 2
		expected[8] = new Integer[]{48, 6, 3, 114, 6, 6, 2, 1, 0, 6};
		// Chord 3
		expected[9] = new Integer[]{47, 6, 2, 120, 12, 12, 3, 4, 0, 7};
		expected[10] = new Integer[]{50, 5, 0, 120, 12, 24, 3, 4, 1, 7};
		expected[11] = new Integer[]{59, 4, 4, 120, 12, 24, 3, 4, 2, 7};
		expected[12] = new Integer[]{65, 2, 1, 120, 12, 24, 3, 4, 3, 7};
		// Chord 4
		expected[13] = new Integer[]{45, 6, 0, 132, 12, 12, 4, 1, 0, 8};
		// Chord 5
		expected[14] = new Integer[]{45, 6, 0, 144, 24, 24, 5, 5, 0, 9};
		expected[15] = new Integer[]{57, 5, 7, 144, 24, 144, 5, 5, 1, 9};
		expected[16] = new Integer[]{57, 4, 2, 144, 24, 48, 5, 5, 2, 9};
		expected[17] = new Integer[]{60, 3, 1, 144, 24, 24, 5, 5, 3, 9};
		expected[18] = new Integer[]{69, 2, 5, 144, 24, 24, 5, 5, 4, 9};
		// Chord 6
		expected[19] = new Integer[]{45, 6, 0, 168, 12, 24, 6, 4, 0, 10};
		expected[20] = new Integer[]{60, 3, 1, 168, 12, 12, 6, 4, 1, 10};
		expected[21] = new Integer[]{64, 2, 0, 168, 12, 12, 6, 4, 2, 10};
		expected[22] = new Integer[]{69, 1, 0, 168, 12, 24, 6, 4, 3, 10};
		// Chord 7
		expected[23] = new Integer[]{59, 3, 0, 180, 12, 108, 7, 2, 0, 11};
		expected[24] = new Integer[]{68, 2, 4, 180, 12, 12, 7, 2, 1, 11};
		// Chord 8
		expected[25] = new Integer[]{45, 6, 0, 192, 6, 72, 8, 4, 0, 13};
		expected[26] = new Integer[]{57, 4, 2, 192, 6, 72, 8, 4, 1, 13};
		expected[27] = new Integer[]{64, 2, 0, 192, 6, 6, 8, 4, 2, 13};
		expected[28] = new Integer[]{69, 1, 0, 192, 6, 12, 8, 4, 3, 13};
		// Chord 9-14
		expected[29] = new Integer[]{68, 2, 4, 198, 6, 9, 9, 1, 0, 14};
		expected[30] = new Integer[]{69, 1, 0, 204, 3, 12, 10, 1, 0, 15};
		expected[31] = new Integer[]{68, 2, 4, 207, 3, 3, 11, 1, 0, 16};
		expected[32] = new Integer[]{66, 2, 2, 210, 3, 3, 12, 1, 0, 18};
		expected[33] = new Integer[]{68, 2, 4, 213, 3, 51, 13, 1, 0, 19};
		expected[34] = new Integer[]{69, 1, 0, 216, 24, 48, 14, 1, 0, 20};
		// Chord 15
		expected[35] = new Integer[]{45, 6, 0, 264, 24, 24, 15, 4, 0, 22};
		expected[36] = new Integer[]{57, 4, 2, 264, 24, 24, 15, 4, 1, 22};
		expected[37] = new Integer[]{64, 2, 0, 264, 24, 24, 15, 4, 2, 22};
		expected[38] = new Integer[]{69, 1, 0, 264, 24, 24, 15, 4, 3, 22};
		
		Integer[][] actual = tablature.getBasicTabSymbolProperties();
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}


	public void testGetBasicTabSymbolPropertiesChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		
		// Determine expected
		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		// Chord 0
		Integer[][] expected0 = new Integer[4][10];
		expected0[0] = new Integer[]{50, 5, 0, 72, 24, 48, 0, 4, 0, 3};
		expected0[1] = new Integer[]{57, 4, 2, 72, 24, 24, 0, 4, 1, 3};
		expected0[2] = new Integer[]{65, 2, 1, 72, 24, 24, 0, 4, 2, 3};
		expected0[3] = new Integer[]{69, 1, 0, 72, 24, 24, 0, 4, 3, 3};
		// Chord 1
		Integer[][] expected1 = new Integer[4][10];
		expected1[0] = new Integer[]{45, 6, 0, 96, 18, 18, 1, 4, 0, 5};
		expected1[1] = new Integer[]{57, 4, 2, 96, 18, 24, 1, 4, 1, 5};
		expected1[2] = new Integer[]{72, 2, 8, 96, 18, 24, 1, 4, 2, 5};
		expected1[3] = new Integer[]{69, 1, 0, 96, 18, 72, 1, 4, 3, 5};
		// Chord 2
		Integer[][] expected2 = new Integer[1][10];
		expected2[0] = new Integer[]{48, 6, 3, 114, 6, 6, 2, 1, 0, 6};
		// Chord 3
		Integer[][] expected3 = new Integer[4][10];
		expected3[0] = new Integer[]{47, 6, 2, 120, 12, 12, 3, 4, 0, 7 };
		expected3[1] = new Integer[]{50, 5, 0, 120, 12, 24, 3, 4, 1, 7};
		expected3[2] = new Integer[]{59, 4, 4, 120, 12, 24, 3, 4, 2, 7};
		expected3[3] = new Integer[]{65, 2, 1, 120, 12, 24, 3, 4, 3, 7};
		// Chord 4
		Integer[][] expected4 = new Integer[1][10];
		expected4[0] = new Integer[]{45, 6, 0, 132, 12, 12, 4, 1, 0, 8};
		// Chord 5
		Integer[][] expected5 = new Integer[5][10];
		expected5[0] = new Integer[]{45, 6, 0, 144, 24, 24, 5, 5, 0, 9};
		expected5[1] = new Integer[]{57, 5, 7, 144, 24, 144, 5, 5, 1, 9};
		expected5[2] = new Integer[]{57, 4, 2, 144, 24, 48, 5, 5, 2, 9};
		expected5[3] = new Integer[]{60, 3, 1, 144, 24, 24, 5, 5, 3, 9};
		expected5[4] = new Integer[]{69, 2, 5, 144, 24, 24, 5, 5, 4, 9};
		// Chord 6
		Integer[][] expected6 = new Integer[4][10];
		expected6[0] = new Integer[]{45, 6, 0, 168, 12, 24, 6, 4, 0, 10};
		expected6[1] = new Integer[]{60, 3, 1, 168, 12, 12, 6, 4, 1, 10};
		expected6[2] = new Integer[]{64, 2, 0, 168, 12, 12, 6, 4, 2, 10};
		expected6[3] = new Integer[]{69, 1, 0, 168, 12, 24, 6, 4, 3, 10};
		// Chord 7
		Integer[][] expected7 = new Integer[2][10];
		expected7[0] = new Integer[]{59, 3, 0, 180, 12, 108, 7, 2, 0, 11};
		expected7[1] = new Integer[]{68, 2, 4, 180, 12, 12, 7, 2, 1, 11};
		// Chord 8
		Integer[][] expected8 = new Integer[4][10];
		expected8[0] = new Integer[]{45, 6, 0, 192, 6, 72, 8, 4, 0, 13};
		expected8[1] = new Integer[]{57, 4, 2, 192, 6, 72, 8, 4, 1, 13};
		expected8[2] = new Integer[]{64, 2, 0, 192, 6, 6, 8, 4, 2, 13};
		expected8[3] = new Integer[]{69, 1, 0, 192, 6, 12, 8, 4, 3, 13};
		// Chord 9-14
		Integer[][] expected9 = new Integer[1][10];
		expected9[0] = new Integer[]{68, 2, 4, 198, 6, 9, 9, 1, 0, 14};
		Integer[][] expected10 = new Integer[1][10];
		expected10[0] = new Integer[]{69, 1, 0, 204, 3, 12, 10, 1, 0, 15};
		Integer[][] expected11 = new Integer[1][10];
		expected11[0] = new Integer[]{68, 2, 4, 207, 3, 3, 11, 1, 0, 16};
		Integer[][] expected12 = new Integer[1][10];
		expected12[0] = new Integer[]{66, 2, 2, 210, 3, 3, 12, 1, 0, 18};
		Integer[][] expected13 = new Integer[1][10];
		expected13[0] = new Integer[]{68, 2, 4, 213, 3, 51, 13, 1, 0, 19};
		Integer[][] expected14 = new Integer[1][10];
		expected14[0] = new Integer[]{69, 1, 0, 216, 24, 48, 14, 1, 0, 20};
		// Chord 15
		Integer[][] expected15 = new Integer[4][10];
		expected15[0] = new Integer[]{45, 6, 0, 264, 24, 24, 15, 4, 0, 22};
		expected15[1] = new Integer[]{57, 4, 2, 264, 24, 24, 15, 4, 1, 22};
		expected15[2] = new Integer[]{64, 2, 0, 264, 24, 24, 15, 4, 2, 22};
		expected15[3] = new Integer[]{69, 1, 0, 264, 24, 24, 15, 4, 3, 22};
	
		expected.add(expected0); expected.add(expected1); expected.add(expected2); expected.add(expected3);
		expected.add(expected4); expected.add(expected5); expected.add(expected6); expected.add(expected7); 
		expected.add(expected8); expected.add(expected9); expected.add(expected10); expected.add(expected11);
		expected.add(expected12); expected.add(expected13); expected.add(expected14); expected.add(expected15);

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		for (int i = 0; i < expected.size(); i++) {
		  actual.add(tablature.getBasicTabSymbolPropertiesChord(i));
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
	
	
	// ********************************************************************************
	

	public void testGetTranpositionInterval() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		assertEquals(-2, tablature.getTranspositionInterval());
	}
	
	
	public void testNormaliseTuning() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		Tuning expectedTuning = Tuning.G;
		List<Integer> expectedGridYValues = Arrays.asList(new Integer[]{
			48, 55, 63, 67, 43, 55, 70, 67, 46, 45, 48,	57, 63, 43, 43, 55, 55, 58, 67, 43, 
			58, 62, 67, 57, 66, 43, 55, 62, 67, 66, 67, 66, 64, 66, 67, 43, 55, 62, 67});
		Integer[][] btp = tablature.getBasicTabSymbolProperties();	
		Integer[][] expectedBasicTabSymbolProperties = Arrays.copyOfRange(btp, 0, btp.length); 
		int transpositionInterval = -2;
		for (int i = 0; i < btp.length; i++) {
			expectedBasicTabSymbolProperties[i][Tablature.PITCH] = 
				btp[i][Tablature.PITCH] + transpositionInterval;
		}

		tablature.normaliseTuning();
		Encoding enc = tablature.getEncoding();
		Tuning actualTuning = enc.getTunings()[Encoding.NEW_TUNING_IND];
		List<Integer> actualGridYValues = enc.getListsOfStatistics().get(Encoding.GRID_Y_IND);
		Integer[][] actualBasicTabSymbolProperties = tablature.getBasicTabSymbolProperties();

		assertEquals(expectedTuning, actualTuning);
		assertEquals(expectedGridYValues.size(), actualGridYValues.size());
		for (int i = 0; i < expectedGridYValues.size(); i++) {
			assertEquals(expectedGridYValues.get(i), actualGridYValues.get(i));
		}
		assertEquals(expectedBasicTabSymbolProperties.length, actualBasicTabSymbolProperties.length);
		for (int i = 0; i < expectedBasicTabSymbolProperties.length; i++) {
			assertEquals(expectedBasicTabSymbolProperties[i].length, actualBasicTabSymbolProperties[i].length);
			for (int j = 0; j < expectedBasicTabSymbolProperties[i].length; j++) {
				assertEquals(expectedBasicTabSymbolProperties[i][j], actualBasicTabSymbolProperties[i][j]);
			}
		}
	}


	public void testRationalToIntDur() {
		List<Rational> rs = Arrays.asList(new Rational[]{				
			new Rational(1, 2),
			new Rational(3, 4),
			new Rational(15, 16),
			new Rational(5, 4)
		});
		List<Integer> expected = Arrays.asList(new Integer[]{48, 72, 90, 120});
		
		List<Integer> actual = new ArrayList<>();
		for (Rational r : rs) {
			actual.add(Tablature.rationalToIntDur(r));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetAllMetricPositions() {
		// a. For a piece with meter changes
		Tablature tablature = new Tablature(encodingTestGetMeterInfo, false);

		// Determine expected (once for testing getMetricPosition() and once for testing 
		// getAllMetricPositions())
		List<Rational[]> expected = new ArrayList<Rational[]>();
		for (int i = 0; i < 2; i++) {
			// Bar 0 (meter = 2/2): anacrusis length is 3/8 
			expected.add(new Rational[]{new Rational(0, 1), new Rational(5, 4)});
			expected.add(new Rational[]{new Rational(0, 1), new Rational(3, 2)});    
			expected.add(new Rational[]{new Rational(0, 1), new Rational(7, 4)});
			// Bar 1 (meter = 2/2): onset time beat 0 = 3/8 = 12/32
			expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});    
			expected.add(new Rational[]{new Rational(1, 1), new Rational(3, 4)});
			expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 1)});
			expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 1)});
			// Bar 2 (meter = 2/2): onset time beat 0 = 11/8 = 44/32 
			expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 2)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(5, 8)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(3, 4)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(13, 16)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(7, 8)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(15, 16)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 1)});
			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 1)});
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
			expected.add(new Rational[]{new Rational(5, 1), new Rational(2, 1)});
			expected.add(new Rational[]{new Rational(5, 1), new Rational(2, 1)});
			// Bar 6 (meter = 2/2): onset time beat 0 = 39/8 = 156/32
			expected.add(new Rational[]{new Rational(6, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 2)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 1)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 2)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(2, 1)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(5, 2)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 1)});
			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 1)});
			// Bar 7 (meter = 5/16): onset time beat 0 = 47/8 = 188/32
			expected.add(new Rational[]{new Rational(7, 1), new Rational(0, 512)}); 
			expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 8)}); // new Rational(2, 5)});
			expected.add(new Rational[]{new Rational(7, 1), new Rational(3, 16)}); // new Rational(3, 5)});
			expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 4)}); // new Rational(4, 5)});
			// Bar 8 (meter = 2/2): onset time beat 0 = 99/16 = 198/32
			expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(1, 4)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(5, 16)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(11, 32)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 8)});
			expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 8)});
		}
		List<Rational[]> actual = new ArrayList<Rational[]>();
		Integer[][] basicTabsymbolProperties = tablature.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo = tablature.getMeterInfo();
		for (int i = 0; i < basicTabsymbolProperties.length; i++) {
			Rational currentMetricTime = new Rational(basicTabsymbolProperties[i][Tablature.ONSET_TIME],
				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			currentMetricTime.reduce();
			actual.add(Tablature.getMetricPosition(currentMetricTime, meterInfo));
		}
		actual.addAll(tablature.getAllMetricPositions());
		
		// b. For a piece with no meter changes
		tablature = new Tablature(encodingTestpiece, false);

		// Determine expected (once for testing getMetricPosition() and once for testing 
		// getAllMetricPositions())
		for (int i = 0; i < 2; i++) {
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
			Rational[] chord4 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
			expected.add(chord4); 
			Rational[] chord5 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
			expected.add(chord5); expected.add(chord5); expected.add(chord5); expected.add(chord5); expected.add(chord5);
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
		}

		basicTabsymbolProperties = tablature.getBasicTabSymbolProperties(); 
		meterInfo = tablature.getMeterInfo();
		for (int i = 0; i < basicTabsymbolProperties.length; i++) {
			Rational currentMetricTime = 
				new Rational(basicTabsymbolProperties[i][Tablature.ONSET_TIME],
				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			actual.add(Tablature.getMetricPosition(currentMetricTime, meterInfo));
		}
		actual.addAll(tablature.getAllMetricPositions());

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
	
	
//	public void testGetAllMetricPositions() {
//		// a. For a piece with meter changes
//		Tablature tablature = new Tablature(encodingTestGetMeterInfo, false);
//
//		// Determine expected (once for testing getMetricPosition() and once for testing 
//		// getAllMetricPositions())
//		List<Rational[]> expected = new ArrayList<Rational[]>();
//		for (int i = 0; i < 2; i++) {
//			// Bar 0 (meter = 2/2): anacrusis length is 3/8 
//			expected.add(new Rational[]{new Rational(0, 1), new Rational(5, 8)});
//			expected.add(new Rational[]{new Rational(0, 1), new Rational(3, 4)});    
//			expected.add(new Rational[]{new Rational(0, 1), new Rational(7, 8)});
//			// Bar 1 (meter = 2/2): onset time beat 0 = 3/8 = 12/32
//			expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(1, 1), new Rational(0, 512)});    
//			expected.add(new Rational[]{new Rational(1, 1), new Rational(3, 8)});
//			expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(1, 1), new Rational(1, 2)});
//			// Bar 2 (meter = 2/2): onset time beat 0 = 11/8 = 44/32 
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 4)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(5, 16)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(3, 8)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(13, 32)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(7, 16)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(15, 32)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 2)});
//			// Bar 3 (meter = 3/4): onset time beat 0 = 19/8 = 76/32
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 4)}); // new Rational(1, 3)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(3, 8)}); // new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(7, 16)}); // new Rational(7, 12)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 2)}); // new Rational(2, 3)});
//			expected.add(new Rational[]{new Rational(3, 1), new Rational(1, 2)}); // new Rational(2, 3)});
//			// Bar 4 (meter = 3/4): onset time beat 0 = 25/8 = 100/32
//			expected.add(new Rational[]{new Rational(4, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(4, 1), new Rational(3, 16)}); // new Rational(1, 4)});
//			expected.add(new Rational[]{new Rational(4, 1), new Rational(7, 32)}); // new Rational(7, 24)});
//			expected.add(new Rational[]{new Rational(4, 1), new Rational(1, 4)}); // new Rational(1, 3)});
//			expected.add(new Rational[]{new Rational(4, 1), new Rational(1, 4)}); // new Rational(1, 3)});
//			// Bar 5 (meter = 2/2): onset time beat 0 = 31/8 = 124/32
//			expected.add(new Rational[]{new Rational(5, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(5, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(5, 1), new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(5, 1), new Rational(1, 2)});
//			// Bar 6 (meter = 2/2): onset time beat 0 = 39/8 = 156/32
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 8)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 4)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 8)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(5, 8)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 4)});
//			expected.add(new Rational[]{new Rational(6, 1), new Rational(3, 4)});
//			// Bar 7 (meter = 5/16): onset time beat 0 = 47/8 = 188/32
//			expected.add(new Rational[]{new Rational(7, 1), new Rational(0, 512)}); 
//			expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 8)}); // new Rational(2, 5)});
//			expected.add(new Rational[]{new Rational(7, 1), new Rational(3, 16)}); // new Rational(3, 5)});
//			expected.add(new Rational[]{new Rational(7, 1), new Rational(1, 4)}); // new Rational(4, 5)});
//			// Bar 8 (meter = 2/2): onset time beat 0 = 99/16 = 198/32
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(0, 512)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(1, 2)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(5, 8)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(11, 16)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 4)});
//			expected.add(new Rational[]{new Rational(8, 1), new Rational(3, 4)});
//		}
//		List<Rational[]> actual = new ArrayList<Rational[]>();
//		Integer[][] basicTabsymbolProperties = tablature.getBasicTabSymbolProperties();
//		List<Integer[]> meterInfo = tablature.getMeterInfo();
//		for (int i = 0; i < basicTabsymbolProperties.length; i++) {
//			Rational currentMetricTime = new Rational(basicTabsymbolProperties[i][Tablature.ONSET_TIME],
//				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//			actual.add(Tablature.getMetricPosition(currentMetricTime, meterInfo));
//		}
//		actual.addAll(tablature.getAllMetricPositions());
//		
//		// b. For a piece with no meter changes
//		tablature = new Tablature(encodingTestpiece, false);
//
//		// Determine expected (once for testing getMetricPosition() and once for testing 
//		// getAllMetricPositions())
//		for (int i = 0; i < 2; i++) {
//			// Bar 1: onset time beat 0 = 0/32
//			Rational[] chord0 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
//			expected.add(chord0); expected.add(chord0); expected.add(chord0); expected.add(chord0);
//			// Bar 2: onset time beat 0 = 32/32
//			Rational[] chord1 = new Rational[]{new Rational(2, 1), new Rational(0, 64)};
//			expected.add(chord1); expected.add(chord1); expected.add(chord1); expected.add(chord1);    
//			Rational[] chord2 = new Rational[]{new Rational(2, 1), new Rational(3, 16)};
//			expected.add(chord2); 
//			Rational[] chord3 = new Rational[]{new Rational(2, 1), new Rational(1, 4)};
//			expected.add(chord3); expected.add(chord3); expected.add(chord3); expected.add(chord3);
//			Rational[] chord4 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
//			expected.add(chord4); 
//			Rational[] chord5 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
//			expected.add(chord5); expected.add(chord5); expected.add(chord5); expected.add(chord5); expected.add(chord5);
//			Rational[] chord6 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
//			expected.add(chord6); expected.add(chord6); expected.add(chord6); expected.add(chord6);
//			Rational[] chord7 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
//			expected.add(chord7); expected.add(chord7);
//			// Bar 3: onset time beat 0 = 64/32
//			Rational[] chord8 = new Rational[]{new Rational(3, 1), new Rational(0, 64)};
//			expected.add(chord8); expected.add(chord8); expected.add(chord8); expected.add(chord8);
//			Rational[] chord9 = new Rational[]{new Rational(3, 1), new Rational(1, 16)};
//			expected.add(chord9); 
//			Rational[] chord10 = new Rational[]{new Rational(3, 1), new Rational(1, 8)};
//			expected.add(chord10); 
//			Rational[] chord11 = new Rational[]{new Rational(3, 1), new Rational(5, 32)};
//			expected.add(chord11); 
//			Rational[] chord12 = new Rational[]{new Rational(3, 1), new Rational(3, 16)};
//			expected.add(chord12); 
//			Rational[] chord13 = new Rational[]{new Rational(3, 1), new Rational(7, 32)};
//			expected.add(chord13); 
//			Rational[] chord14 = new Rational[]{new Rational(3, 1), new Rational(1, 4)};
//			expected.add(chord14); 
//			Rational[] chord15 = new Rational[]{new Rational(3, 1), new Rational(3, 4)};
//			expected.add(chord15); expected.add(chord15); expected.add(chord15); expected.add(chord15); 
//		}
//
//		basicTabsymbolProperties = tablature.getBasicTabSymbolProperties(); 
//		meterInfo = tablature.getMeterInfo();
//		for (int i = 0; i < basicTabsymbolProperties.length; i++) {
//			Rational currentMetricTime = 
//				new Rational(basicTabsymbolProperties[i][Tablature.ONSET_TIME],
//				Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
//			actual.add(Tablature.getMetricPosition(currentMetricTime, meterInfo));
//		}
//		actual.addAll(tablature.getAllMetricPositions());
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).length, actual.get(i).length);
//			for (int j = 0; j < expected.get(i).length; j++) {
////				assertEquals(expected.get(i)[j].getNumer(), actual.get(i)[j].getNumer());
////				assertEquals(expected.get(i)[j].getDenom(), actual.get(i)[j].getDenom());
//				assertEquals(expected.get(i)[j], actual.get(i)[j]);
//			}
//		}
//	}
	
	
	public void testGetAllOnsetTimes() {
		// a. For a piece with meter changes
		Tablature tablature = new Tablature(encodingTestGetMeterInfo, false);
		List<Rational> expected = new ArrayList<Rational>();
		expected.add(new Rational(0, 4));
		expected.add(new Rational(1, 4));
		expected.add(new Rational(1, 2));
		//
		expected.add(new Rational(3, 4));
		expected.add(new Rational(6, 4));
		expected.add(new Rational(7, 4));
		//
		expected.add(new Rational(11, 4));
		expected.add(new Rational(13, 4));
		expected.add(new Rational(27, 8));
		expected.add(new Rational(7, 2));
		expected.add(new Rational(57, 16));
		expected.add(new Rational(29, 8));
		expected.add(new Rational(59, 16));
		expected.add(new Rational(15, 4));
		//
		expected.add(new Rational(19, 4));
		expected.add(new Rational(20, 4));
		expected.add(new Rational(41, 8));
		expected.add(new Rational(83, 16));
		expected.add(new Rational(21, 4));
		//
		expected.add(new Rational(22, 4));
		expected.add(new Rational(91, 16));
		expected.add(new Rational(183, 32));
		expected.add(new Rational(23, 4));
		//
		expected.add(new Rational(25, 4));
		expected.add(new Rational(33, 4));
		//
		expected.add(new Rational(41, 4));
		expected.add(new Rational(43, 4));
		expected.add(new Rational(45, 4));
		expected.add(new Rational(47, 4));
		expected.add(new Rational(49, 4));
		expected.add(new Rational(51, 4));
		expected.add(new Rational(53, 4));
		//
		expected.add(new Rational(57, 4));
		expected.add(new Rational(115, 8));
		expected.add(new Rational(231, 16));
		expected.add(new Rational(232, 16));
		//
		expected.add(new Rational(233, 16));
		expected.add(new Rational(237, 16));
		expected.add(new Rational(238, 16));
		expected.add(new Rational(477, 32));
		expected.add(new Rational(478, 32));
		
		List<Rational> actual = tablature.getAllOnsetTimes();
		
		// b. For a piece with no meter changes
		tablature = new Tablature(encodingTestpiece, false);
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

		actual.addAll(tablature.getAllOnsetTimes());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	
//	public void testGetAllOnsetTimes() {
//		// a. For a piece with meter changes
//		Tablature tablature = new Tablature(encodingTestGetMeterInfo, false);
//		List<Rational> expected = new ArrayList<Rational>();
//		expected.add(new Rational(0, 4));
//		expected.add(new Rational(1, 8));
//		expected.add(new Rational(1, 4));
//		//
//		expected.add(new Rational(3, 8));
//		expected.add(new Rational(3, 4));
//		expected.add(new Rational(7, 8));
//		//
//		expected.add(new Rational(11, 8));
//		expected.add(new Rational(13, 8));
//		expected.add(new Rational(27, 16));
//		expected.add(new Rational(7, 4));
//		expected.add(new Rational(57, 32));
//		expected.add(new Rational(29, 16));
//		expected.add(new Rational(59, 32));
//		expected.add(new Rational(15, 8));
//		//
//		expected.add(new Rational(19, 8));
//		expected.add(new Rational(21, 8));
//		expected.add(new Rational(11, 4));
//		expected.add(new Rational(45, 16));
//		expected.add(new Rational(23, 8));
//		//
//		expected.add(new Rational(25, 8));
//		expected.add(new Rational(53, 16));
//		expected.add(new Rational(107, 32));
//		expected.add(new Rational(27, 8));
//		//
//		expected.add(new Rational(31, 8));
//		expected.add(new Rational(35, 8));
//		//
//		expected.add(new Rational(39, 8));
//		expected.add(new Rational(5, 1));
//		expected.add(new Rational(41, 8));
//		expected.add(new Rational(21, 4));
//		expected.add(new Rational(43, 8));
//		expected.add(new Rational(11, 2));
//		expected.add(new Rational(45, 8));
//		//
//		expected.add(new Rational(47, 8));
//		expected.add(new Rational(6, 1));
//		expected.add(new Rational(97, 16));
//		expected.add(new Rational(49, 8));
//		//
//		expected.add(new Rational(99, 16));
//		expected.add(new Rational(107, 16));
//		expected.add(new Rational(109, 16));
//		expected.add(new Rational(55, 8));
//		expected.add(new Rational(111, 16));
//		
//		List<Rational> actual = tablature.getAllOnsetTimes();
//		
//		// b. For a piece with no meter changes
//		tablature = new Tablature(encodingTestpiece, false);
//		// Chord 0
//		expected.add(new Rational(3, 4));
//		// Chord 1
//		expected.add(new Rational(1, 1));
//		// Chord 2
//		expected.add(new Rational(19, 16));
//		// Chord 3
//		expected.add(new Rational(5, 4));
//		// Chord 4
//		expected.add(new Rational(11, 8));
//		// Chord 5
//		expected.add(new Rational(3, 2));
//		// Chord 6
//		expected.add(new Rational(7, 4));
//		// Chord 7
//		expected.add(new Rational(15, 8));
//		// Chord 8
//		expected.add(new Rational(2, 1));
//		// Chords 9-14
//		expected.add(new Rational(33, 16));
//		expected.add(new Rational(17, 8));
//		expected.add(new Rational(69, 32));
//		expected.add(new Rational(35, 16));
//		expected.add(new Rational(71, 32));
//		expected.add(new Rational(9, 4));
//		// Chord 15
//		expected.add(new Rational(11, 4));
//
//		actual.addAll(tablature.getAllOnsetTimes());
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}
	
	
	public void testGetAllOnsetTimesRestsInclusive() {		
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Rational[]> expected = new ArrayList<>();
		// Rest
		expected.add(new Rational[]{new Rational(0, 4), Rational.ZERO});
		// Chord 0
		expected.add(new Rational[]{new Rational(3, 4), Rational.ONE});
		// Chord 1
		expected.add(new Rational[]{new Rational(1, 1), Rational.ONE});
		// Chord 2
		expected.add(new Rational[]{new Rational(19, 16), Rational.ONE});
		// Chord 3
		expected.add(new Rational[]{new Rational(5, 4), Rational.ONE});
		// Chord 4
		expected.add(new Rational[]{new Rational(11, 8), Rational.ONE});
		// Chord 5
		expected.add(new Rational[]{new Rational(3, 2), Rational.ONE});
		// Chord 6
		expected.add(new Rational[]{new Rational(7, 4), Rational.ONE});
		// Chord 7
		expected.add(new Rational[]{new Rational(15, 8), Rational.ONE});
		// Chord 8
		expected.add(new Rational[]{new Rational(2, 1), Rational.ONE});
		// Chords 9-14
		expected.add(new Rational[]{new Rational(33, 16), Rational.ONE});
		expected.add(new Rational[]{new Rational(17, 8), Rational.ONE});
		expected.add(new Rational[]{new Rational(69, 32), Rational.ONE});
		expected.add(new Rational[]{new Rational(35, 16), Rational.ONE});
		expected.add(new Rational[]{new Rational(71, 32), Rational.ONE});
		expected.add(new Rational[]{new Rational(9, 4), Rational.ONE});
		// Rest
		expected.add(new Rational[]{new Rational(10, 4), Rational.ZERO});
		// Chord 15
		expected.add(new Rational[]{new Rational(11, 4), Rational.ONE});

		List<Rational[]> actual = tablature.getAllOnsetTimesRestsInclusive();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetAllOnsetTimesAndMinDurations() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		List<Rational[]> expected = new ArrayList<Rational[]>();
		// Chord 0
		expected.add(new Rational[]{new Rational(3, 4), new Rational(1, 4)});
		// Chord 1
		expected.add(new Rational[]{new Rational(1, 1), new Rational(3, 16)});
		// Chord 2
		expected.add(new Rational[]{new Rational(19, 16), new Rational(1, 16)});
		// Chord 3
		expected.add(new Rational[]{new Rational(5, 4), new Rational(1, 8)});
		// Chord 4
		expected.add(new Rational[]{new Rational(11, 8), new Rational(1, 8)});
		// Chord 5
		expected.add(new Rational[]{new Rational(3, 2), new Rational(1, 4)});
		// Chord 6
		expected.add(new Rational[]{new Rational(7, 4), new Rational(1, 8)});
		// Chord 7
		expected.add(new Rational[]{new Rational(15, 8), new Rational(1, 8)});
		// Chord 8
		expected.add(new Rational[]{new Rational(2, 1), new Rational(1, 16)});
		// Chords 9-14
		expected.add(new Rational[]{new Rational(33, 16), new Rational(1, 16)});
		expected.add(new Rational[]{new Rational(17, 8), new Rational(1, 32)});
		expected.add(new Rational[]{new Rational(69, 32), new Rational(1, 32)});
		expected.add(new Rational[]{new Rational(35, 16), new Rational(1, 32)});
		expected.add(new Rational[]{new Rational(71, 32), new Rational(1, 32)});
		expected.add(new Rational[]{new Rational(9, 4), new Rational(1, 4)});
		// Chord 15
		expected.add(new Rational[]{new Rational(11, 4), new Rational(1, 4)});

		List<Rational[]> actual = tablature.getAllOnsetTimesAndMinDurations();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testSetAndGetTablatureChords() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		TabSymbolSet tss = TabSymbolSet.getTabSymbolSet("FrenchTab");
		
		List<List<TabSymbol>> expected = new ArrayList<List<TabSymbol>>();
		// Chord 0
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a5", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("b2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 1
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("i2", tss),
			TabSymbol.getTabSymbol("a1", tss)	
		}));
		// Chord 2
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("d6", tss)
		})); 
		// Chord 3
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("c6", tss),
			TabSymbol.getTabSymbol("a5", tss),
			TabSymbol.getTabSymbol("e4", tss),
			TabSymbol.getTabSymbol("b2", tss)
		}));
		// Chord 4
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss)
		}));
		// Chord 5
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("h5", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("b3", tss),
			TabSymbol.getTabSymbol("f2", tss)
		}));
		// Chord 6
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("b3", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 7
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a3", tss),
			TabSymbol.getTabSymbol("e2", tss)
		}));
		// Chord 8
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 9-14
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("a1", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("c2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("a1", tss)}));
		// Chord 15
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));

		List<List<TabSymbol>> actual = tablature.getTablatureChords();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testGetMinimumDurationOfNote() {
		Tablature tablature = new Tablature(encodingTestpiece, true);
		Transcription transcription = new Transcription(midiTestpiece, encodingTestpiece);
		
		Rational thirtysecond = new Rational(1, 32);
		Rational sixteenth = new Rational(1, 16);
		Rational eighth = new Rational(1, 8);
		Rational dottedEight = new Rational(3, 16);
		Rational quarter = new Rational(1, 4);
		
		List<List<Rational>> expected = new ArrayList<List<Rational>>();
		List<Rational> expected0 = Arrays.asList(new Rational[]{quarter, dottedEight, eighth, quarter, eighth, eighth,
			sixteenth, sixteenth, thirtysecond,	thirtysecond, thirtysecond, thirtysecond, quarter, quarter});
		List<Rational> expected1 = Arrays.asList(new Rational[]{quarter, dottedEight, eighth, quarter, eighth, 
			sixteenth, quarter});
		List<Rational> expected2 = Arrays.asList(new Rational[]{quarter, dottedEight, eighth, quarter, eighth, eighth,
			sixteenth, quarter});
		List<Rational> expected3 = Arrays.asList(new Rational[]{quarter, dottedEight, sixteenth, eighth, quarter, 
			sixteenth, quarter});
		List<Rational> expected4 = Arrays.asList(new Rational[]{eighth, eighth, quarter, eighth});
		expected.add(expected0); expected.add(expected1); expected.add(expected2); expected.add(expected3);
		expected.add(expected4);
		
		List<List<Rational>> actual = new ArrayList<List<Rational>>();
		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
		NotationSystem system = transcription.getPiece().getScore();
		for (int i = 0; i < system.size(); i++) {
			List<Rational> currentActual = new ArrayList<Rational>();
			NotationVoice nv = system.get(i).get(0);
			for (NotationChord nc: nv) {
				currentActual.add(Tablature.getMinimumDurationOfNote(basicTabSymbolProperties, nc.get(0)));
			}
			actual.add(currentActual);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}		
	}


	public void testGetPitchesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
 
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 72, 69})); 
		expected.add(Arrays.asList(new Integer[]{48})); 
		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65})); 
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
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
		 	actual.add(tablature.getPitchesInChord(i));
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
		Tablature tablature = new Tablature(encodingTestpiece, false);
		 
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		expected.add(Arrays.asList(new Integer[]{45, 57, 72, 69})); 
		expected.add(Arrays.asList(new Integer[]{48})); 
		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65}));
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
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
		int lowestNoteIndex = 0;
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(Tablature.getPitchesInChord(basicTabSymbolProperties, lowestNoteIndex));
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


	public void testGetMinimumDurationLabels() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		
		List<List<Double>> expected = new ArrayList<>();
		//
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		//
		expected.add(Transcription.DOTTED_EIGHTH);
		expected.add(Transcription.DOTTED_EIGHTH);
		expected.add(Transcription.DOTTED_EIGHTH);
		expected.add(Transcription.DOTTED_EIGHTH);
		//
		expected.add(Transcription.SIXTEENTH);
		//
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		//
		expected.add(Transcription.EIGHTH);
		//
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		//
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		//
		expected.add(Transcription.EIGHTH);
		expected.add(Transcription.EIGHTH);
		//
		expected.add(Transcription.SIXTEENTH);
		expected.add(Transcription.SIXTEENTH);
		expected.add(Transcription.SIXTEENTH);
		expected.add(Transcription.SIXTEENTH);
		//
		expected.add(Transcription.SIXTEENTH);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		expected.add(Transcription.THIRTYSECOND);
		//
		expected.add(Transcription.QUARTER);
		//
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		expected.add(Transcription.QUARTER);
		
		List<List<Double>> actual = tablature.getMinimumDurationLabels();
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size()); 
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testGetNumberOfUnisonsInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Integer> expected = Arrays.asList(new Integer[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

		List<Integer> actual = new ArrayList<Integer>();
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			actual.add(tablature.getNumberOfUnisonsInChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testGetUnisonInfo() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
	  
		// Determine expected
		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(new Integer[][]{{57, 1, 2}});
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		  
		// Calculate actual
		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			actual.add(tablature.getUnisonInfo(i));
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


	public void testGenerateChordDictionary() {
		Tablature tablature = new Tablature(encodingTestpiece, true);
			
		// Determine expected
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
//		expected.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
//		expected.add(Arrays.asList(new Integer[]{45, 57, 69, 72})); 
//		expected.add(Arrays.asList(new Integer[]{48})); 
//		expected.add(Arrays.asList(new Integer[]{47, 50, 59, 65})); 
//		expected.add(Arrays.asList(new Integer[]{45})); 
//		expected.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69})); 
//		expected.add(Arrays.asList(new Integer[]{45, 60, 64, 69})); 
//		expected.add(Arrays.asList(new Integer[]{59, 68})); 
//		expected.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
//		expected.add(Arrays.asList(new Integer[]{68}));
//		expected.add(Arrays.asList(new Integer[]{69}));
//		expected.add(Arrays.asList(new Integer[]{66}));
		
		expected.add(Arrays.asList(new Integer[]{48, 55, 63, 67})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 67, 70})); 
		expected.add(Arrays.asList(new Integer[]{46})); 
		expected.add(Arrays.asList(new Integer[]{45, 48, 57, 63})); 
		expected.add(Arrays.asList(new Integer[]{43})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 55, 58, 67})); 
		expected.add(Arrays.asList(new Integer[]{43, 58, 62, 67})); 
		expected.add(Arrays.asList(new Integer[]{57, 66})); 
		expected.add(Arrays.asList(new Integer[]{43, 55, 62, 67}));
		expected.add(Arrays.asList(new Integer[]{66}));
		expected.add(Arrays.asList(new Integer[]{67}));
		expected.add(Arrays.asList(new Integer[]{64}));
		
		// Calculate actual
		List<List<Integer>> actual = tablature.generateChordDictionary();
				
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
	
	
	public void testGetNumberOfNotes() {
		int expected = 39;
		Tablature tablature = new Tablature(encodingTestpiece, false);
		int actual = tablature.getNumberOfNotes();
		assertEquals(expected, actual);
		
	}
	
	
	public void testGetIndicesPerChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		
		// Determine expected
		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// fwd
		List<List<Integer>> expectedFwd = new ArrayList<List<Integer>>();
		expectedFwd.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedFwd.add(Arrays.asList(new Integer[]{4, 5, 6, 7}));
		expectedFwd.add(Arrays.asList(new Integer[]{8}));
		expectedFwd.add(Arrays.asList(new Integer[]{9, 10, 11, 12}));
		expectedFwd.add(Arrays.asList(new Integer[]{13}));
		expectedFwd.add(Arrays.asList(new Integer[]{14, 15, 16, 17, 18}));
		expectedFwd.add(Arrays.asList(new Integer[]{19, 20, 21, 22}));
		expectedFwd.add(Arrays.asList(new Integer[]{23, 24}));
		expectedFwd.add(Arrays.asList(new Integer[]{25, 26, 27, 28}));
		expectedFwd.add(Arrays.asList(new Integer[]{29}));
		expectedFwd.add(Arrays.asList(new Integer[]{30}));
		expectedFwd.add(Arrays.asList(new Integer[]{31}));
		expectedFwd.add(Arrays.asList(new Integer[]{32}));
		expectedFwd.add(Arrays.asList(new Integer[]{33}));
		expectedFwd.add(Arrays.asList(new Integer[]{34}));
		expectedFwd.add(Arrays.asList(new Integer[]{35, 36, 37, 38})); 
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
		expectedBwd.add(Arrays.asList(new Integer[]{26, 27, 28, 29}));
		expectedBwd.add(Arrays.asList(new Integer[]{30}));
		expectedBwd.add(Arrays.asList(new Integer[]{31, 32, 33, 34}));
		expectedBwd.add(Arrays.asList(new Integer[]{35, 36, 37, 38})); 
		expected.add(expectedBwd);
		
		// Calculate actual
		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>(); 
		actual.add(tablature.getIndicesPerChord(false));
		actual.add(tablature.getIndicesPerChord(true));
		
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
	
	
	public void testGetLargestTablatureChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
			
		// Determine expected 
		int expected = 5; 
		  
		// Calculate actual
		int actual = tablature.getLargestTablatureChord();
			
		// Assert equality
		assertEquals(expected, actual);
	}
	
	
//	public void testCreatePiece() {
//		Tablature tablature = new Tablature(encodingTestpiece1, true);
//		Transcription transcription = new Transcription(midiTestpiece1, encodingTestpiece1);
//		List<List<Double>> voiceLabels = transcription.getVoiceLabels();
//		List<List<Double>> durationLabels = transcription.getDurationLabels();
//		
//		// Determine expected 
//		Piece expected = transcription.getPiece();
//		// Adapt the CoD at index 12 (the third note in voice 1) so that both notes have the same duration (necessary 
//		// because currently CoDs can only have one duration)
//		ScoreNote adaptedScoreNote = new ScoreNote(new ScorePitch(65), new Rational(5, 4), new Rational(1, 4));
//		expected.getScore().get(1).get(0).get(2).get(0).setScoreNote(adaptedScoreNote);
//		// Also adapt durationLabels
//		durationLabels.set(12, Transcription.createDurationLabel(8));
//		
//		// Calculate actual
//		Piece actual = tablature.createPiece(voiceLabels, durationLabels, 5);
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
	
	
	public void testGetNumberOfCourseCrossingsInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		
		// Determine expected
		List<Integer> expected = Arrays.asList(new Integer[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		
		// Calculate actual
		List<Integer> actual = new ArrayList<Integer>();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(tablature.getNumberOfCourseCrossingsInChord(i));
		}
		
		// Assert equality
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetCourseCrossingInfo() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		expected.add(null);
		expected.add(new Integer[][]{{72, 69, 2, 3}});
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(tablature.getCourseCrossingInfo(i));
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


	public void testGetTripletOnsetPairs() {
		Tablature tablature = new Tablature(encodingTestpiece, true);
		// No triplets
		List<Rational[]> expected = new ArrayList<>();
		expected.add(null);
		List<Rational[]> actual = new ArrayList<>();
		if (tablature.getTripletOnsetPairs() == null) {
			actual.add(null);
		}
		else {
			actual.addAll(tablature.getTripletOnsetPairs());
		}

		// Replace bar 2 (beat 2) and bar 3 (beat 3) with triplets
		String origEncoding = tablature.getEncoding().getRawEncoding();
		String bar2Beat2 = "sm.c6.a5.e4.b2.>.a6.>.";
		String bar2Beat2Triplets = "tr[sm.c6.a5.e4.b2.>.trsm.a6.>.tr]sm.a6.>.";
		origEncoding = origEncoding.replace(bar2Beat2, bar2Beat2Triplets);
		String bar3Beat3 = "mi.>.mi.a6.c4.a2.a1.>.";
		String bar3Beat3Triplets = "tr[mi.>.trmi.a6.c4.a2.a1.>.tr]mi.a6.c4.a2.a1.>.";
		origEncoding = origEncoding.replace(bar3Beat3, bar3Beat3Triplets);
		tablature = new Tablature(new Encoding(origEncoding, 
			new Encoding(encodingTestpiece).getName(), true), true);
		
		expected.add(new Rational[]{new Rational(5, 4), new Rational(17, 12), 
			new Rational(RhythmSymbol.semiminim.getDuration(), 1)});
		expected.add(new Rational[]{new Rational(10, 4), new Rational(17, 6), 
			new Rational(RhythmSymbol.minim.getDuration(), 1)});

//		List<Rational[]> actual = tablature.getTripletOnsetPairs();
		actual.addAll(tablature.getTripletOnsetPairs());
		
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

}
