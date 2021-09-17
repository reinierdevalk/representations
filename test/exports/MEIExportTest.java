package exports;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import junit.framework.TestCase;
import representations.Encoding;
import tbp.SymbolDictionary;

public class MEIExportTest extends TestCase {

	private File encodingTestpiece;
	private final Rational r128 = new Rational(1, 128);
	private final Rational r64 = new Rational(1, 64);
	private final Rational r32 = new Rational(1, 32);
	private final Rational r16 = new Rational(1, 16);
	private final Rational r8 = new Rational(1, 8);
	private final Rational r4 = new Rational(1, 4);
	private final Rational r2 = new Rational(1, 2);
	private final Rational r1 = new Rational(1, 1);
		
	protected void setUp() throws Exception {
		super.setUp();
		encodingTestpiece = new File(MEIExport.rootDir + "data/annotated/encodings/test/"  + "testpiece.tbp");
	}


	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testGetOctave() {	
		List<Integer> pitches = Arrays.asList(new Integer[]{47, 48, 61, 75, 85});
		List<Integer> expected = Arrays.asList(new Integer[]{2, 3, 4, 5, 6});
		
		List<Integer> actual = new ArrayList<Integer>();
		for (int p : pitches) {
			actual.add(MEIExport.getOctave(p));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));		
		}
		assertEquals(expected, actual);
	}


	public void testGetMIDIPitchClassKeySigs() {		
		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11, 4}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1, 6}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8, 1}));
		expected.add(Arrays.asList(new Integer[]{10, 3, 8}));
		expected.add(Arrays.asList(new Integer[]{10, 3}));
		expected.add(Arrays.asList(new Integer[]{10}));
		expected.add(Arrays.asList(new Integer[]{}));
		expected.add(Arrays.asList(new Integer[]{6}));
		expected.add(Arrays.asList(new Integer[]{6, 1}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5}));
		expected.add(Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5, 0}));
		
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (Integer[] key : MEIExport.getKeys()) {
			actual.add(MEIExport.getMIDIPitchClassKeySigs(key));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		
	};


	public void testMakeMIDIPitchClassesGrid() {
		Integer[][] expected = new Integer[15][8];
		expected[0] = new Integer[]{11, 1, 3, 4, 6, 8, 10, }; // Cb
		expected[1] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // Gb
		expected[2] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // Db
		expected[3] = new Integer[]{8, 10, 0, 1, 3, 5, 7}; // Ab
		expected[4] = new Integer[]{3, 5, 7, 8, 10, 0, 2}; // Eb
		expected[5] = new Integer[]{10, 0, 2, 3, 5, 7, 9}; // Bb
		expected[6] = new Integer[]{5, 7, 9, 10, 0, 2, 4}; // F
		expected[7] = new Integer[]{0, 2, 4, 5, 7, 9, 11}; // C 
		expected[8] = new Integer[]{7, 9, 11, 0, 2, 4, 6}; // G
		expected[9] = new Integer[]{2, 4, 6, 7, 9, 11, 1}; // D
		expected[10] = new Integer[]{9, 11, 1, 2, 4, 6, 8}; // A
		expected[11] = new Integer[]{4, 6, 8, 9, 11, 1, 3}; // E
		expected[12] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // B
		expected[13] = new Integer[]{6, 8, 10, 11, 1, 3, 5}; // F#
		expected[14] = new Integer[]{1, 3, 5, 6, 8, 10, 0}; // C#
		
		Integer[][] actual = MEIExport.makeMIDIPitchClassGrid();
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}


	private List<List<Rational>> getTestFractions() {
		List<List<Rational>> testFractions = new ArrayList<List<Rational>>();
		testFractions.add(Arrays.asList(new Rational[]{r128}));
		testFractions.add(Arrays.asList(new Rational[]{r64}));
		testFractions.add(Arrays.asList(new Rational[]{r64, r128})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r32}));
		testFractions.add(Arrays.asList(new Rational[]{r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r32, r64})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r32, r64, r128})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r16}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r32})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r64})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r16, r32, r64, r128})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r8}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r32, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16})); // 1 dot
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r128})); 
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r64})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r8, r16, r32, r64, r128})); // 4 dots
		testFractions.add(Arrays.asList(new Rational[]{r4}));
		//
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8})); // 2 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r32, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16})); // 3 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r64}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r64, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32})); // 4 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r128}));
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r64})); // 5 dots
		testFractions.add(Arrays.asList(new Rational[]{r2, r4, r8, r16, r32, r64, r128})); // 6 dots
		testFractions.add(Arrays.asList(new Rational[]{r1}));
		
		return testFractions;
	}


	public void testGetXMLDur() {
		Encoding encoding = new Encoding(encodingTestpiece);
		
		List<Integer[]> expected = new ArrayList<>();
		// Bar 1
		expected.add(null);
		expected.add(new Integer[]{2, 0});
		expected.add(new Integer[]{4, 0});
		expected.add(new Integer[]{4, 0});
		expected.add(null);
		// Bar 2
		expected.add(new Integer[]{8, 1});
		expected.add(new Integer[]{16, 0});
		expected.add(new Integer[]{8, 0});
		expected.add(null);
		expected.add(new Integer[]{4, 0});
		expected.add(new Integer[]{8, 0});
		expected.add(null);
		expected.add(null);
		// Bar 3
		expected.add(new Integer[]{16, 0});
		expected.add(null);
		expected.add(new Integer[]{32, 0});
		expected.add(null);
		expected.add(null);
		// Bar 4
		expected.add(null);
		expected.add(null);
		expected.add(new Integer[]{4, 0});		
		expected.add(new Integer[]{4, 0});
		expected.add(new Integer[]{4, 0});
		expected.add(null);

		List<Integer[]> actual = new ArrayList<>();
		List<String> events = new ArrayList<>();
		List<List<String[]>> ebl = encoding.getEventsBarlinesFootnotesPerBar();
		for (List<String[]> l : ebl) {
			for (String[] s : l) {
				events.add(s[0].substring(0, s[0].lastIndexOf(SymbolDictionary.SYMBOL_SEPARATOR)));
			}
		}
		for (String event : events) {
			if (!event.equals(SymbolDictionary.SYSTEM_BREAK_INDICATOR) &&
				!event.equals(SymbolDictionary.END_BREAK_INDICATOR)) {
				actual.add(MEIExport.getXMLDur(event));
			}
		}

		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) != null) {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
			else {
				assertEquals(expected.get(i), actual.get(i));
			}
		}
	}


	public void testGetUnitFractions() {
		List<List<Rational>> expected = new ArrayList<List<Rational>>(getTestFractions());
		List<List<Rational>> actual = new ArrayList<List<Rational>>();
		for (int i = 1; i <= 32; i++) {
			actual.add(MEIExport.getUnitFractions(new Rational(i, 128), new Rational(1, 128)));
		}
		for (int i = 112; i <= 128; i++) {
			actual.add(MEIExport.getUnitFractions(new Rational(i, 128), new Rational(1, 128)));
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
	
	
	public void testRound() {
		List<Rational> all = Arrays.asList(new Rational[]{
			new Rational(1, 32),
			new Rational(7, 128),
			new Rational(57, 128),
			new Rational(131, 128)
		});
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(1, 32), // 1/32
			new Rational(1, 16), // 7/128
			new Rational(29, 64), // 57/128
			new Rational(33, 32) // 131/128
		});
		List<Rational> actual = new ArrayList<Rational>();
		for (Rational r : all) {
			actual.add(MEIExport.round(r, new Rational(1, 64)));
		}
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}
	
	
	public void testGetNumDots() {
		List<List<Rational>> all = getTestFractions();
		
		List<Integer> expected = new ArrayList<Integer>();
		for (int i = 0; i < all.size(); i++) {
			expected.add(0);
		}
		expected.set(2, 1); expected.set(5, 1); expected.set(11, 1); expected.set(23, 1);
		expected.set(6, 2); expected.set(13, 2); expected.set(27, 2); expected.set(32, 2);
		expected.set(14, 3); expected.set(29, 3); expected.set(40, 3);
		expected.set(30, 4); expected.set(44, 4); expected.set(46, 5); expected.set(47, 6); 
		
		List<Integer> actual = new ArrayList<Integer>();
		for (List<Rational> l : all) {
			actual.add(MEIExport.getNumDots(l));		
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}

	
	public void testGetUnitFractionSequences() {
		List<List<Rational>> all = new ArrayList<List<Rational>>();
		
		all.add(Arrays.asList(new Rational[]{r2})); // 1 list
		all.add(Arrays.asList(new Rational[]{r2, r4, r8, r16})); // 1 list
		all.add(Arrays.asList(new Rational[]{r2, r4, r16, r32})); // 2 lists
		all.add(Arrays.asList(new Rational[]{r2, r4, r8, r32})); // 2 lists
		all.add(Arrays.asList(new Rational[]{r2, r8, r32, r128})); // 4 lists
		
		List<List<List<Rational>>> expected = new ArrayList<List<List<Rational>>>();
		List<List<Rational>> exp0 = new ArrayList<List<Rational>>();
		exp0.add(Arrays.asList(new Rational[]{r2}));
		expected.add(exp0);
		List<List<Rational>> exp1 = new ArrayList<List<Rational>>();
		exp1.add(Arrays.asList(new Rational[]{r2, r4, r8, r16}));
		expected.add(exp1);
		List<List<Rational>> exp2 = new ArrayList<List<Rational>>();
		exp2.add(Arrays.asList(new Rational[]{r2, r4}));
		exp2.add(Arrays.asList(new Rational[]{r16, r32}));
		expected.add(exp2);
		List<List<Rational>> exp3 = new ArrayList<List<Rational>>();
		exp3.add(Arrays.asList(new Rational[]{r2, r4, r8}));
		exp3.add(Arrays.asList(new Rational[]{r32}));
		expected.add(exp3);
		List<List<Rational>> exp4 = new ArrayList<List<Rational>>();
		exp4.add(Arrays.asList(new Rational[]{r2}));
		exp4.add(Arrays.asList(new Rational[]{r8}));
		exp4.add(Arrays.asList(new Rational[]{r32}));
		exp4.add(Arrays.asList(new Rational[]{r128}));
		expected.add(exp4);
		
		List<List<List<Rational>>> actual = new ArrayList<List<List<Rational>>>();
		for (List<Rational> l : all) {
			actual.add(MEIExport.getUnitFractionSequences(l));
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


	public void testGetDuration() {
		List<Rational> all = new ArrayList<Rational>();
		for (int i = 1; i <= 128; i++) {
			all.add(new Rational(i, 128));
		}

		List<Integer[]> expected = new ArrayList<Integer[]>();
		expected.add(new Integer[]{128, 0});
		expected.add(new Integer[]{64, 0});
		expected.add(new Integer[]{64, 1}); 
		expected.add(new Integer[]{32, 0}); // 4 ---
		expected.add(new Integer[]{}); // 1/32 + n 
		expected.add(new Integer[]{32, 1});
		expected.add(new Integer[]{32, 2}); 
		expected.add(new Integer[]{16, 0}); // 8 ---
		expected.add(new Integer[]{}); // 1/16 + n
		expected.add(new Integer[]{}); // 1/16 + 1/64
		expected.add(new Integer[]{}); // 1/16 + 1/64d
		expected.add(new Integer[]{16, 1}); // 12
		expected.add(new Integer[]{}); // 
		expected.add(new Integer[]{16, 2});
		expected.add(new Integer[]{16, 3});
		expected.add(new Integer[]{8, 0}); // 16 ---
		expected.add(new Integer[]{}); // 1/8 + n
		expected.add(new Integer[]{}); // 1/8 + 1/64
		expected.add(new Integer[]{}); // 1/8 + 1/64d
		expected.add(new Integer[]{}); // 20
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{8, 1}); // 24
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{8, 2}); // 28
		expected.add(new Integer[]{});
		expected.add(new Integer[]{8, 3});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{4, 0}); // 32 ---
		expected.add(new Integer[]{}); // 1/4 + n
		expected.add(new Integer[]{}); // 1/4 + 1/64
		expected.add(new Integer[]{}); // 1/4 + 1/64d
		expected.add(new Integer[]{}); // 36
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 40
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 44
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{4, 1}); // 48
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 52
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{4, 2}); // 56
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{4, 3}); // 60
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{2, 0}); // 64 ---
		expected.add(new Integer[]{}); // 1/2 + n
		expected.add(new Integer[]{}); // 1/2 + 1/64
		expected.add(new Integer[]{}); // 1/2 + 1/64d
		expected.add(new Integer[]{}); // 68
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 72
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 76
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 80
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 84
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 88
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 92
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{2, 1}); // 96
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 100
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 104
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 108
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{2, 2}); // 112
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{}); // 116
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{2, 3}); // 120
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{2, 4}); // 124
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{});
		expected.add(new Integer[]{1, 0}); // 128 --- 
	}
	
	
	public void testMakeAlterationGrid() {
		String[][] expected = new String[15][7];
		expected[0] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Cb
		expected[1] = new String[]{"f", "f", "f", "f", "f", "f", "n"}; // Gb
		expected[2] = new String[]{"f", "f", "n", "f", "f", "f", "n"}; // Db
		expected[3] = new String[]{"f", "f", "n", "f", "f", "n", "n"}; // Ab
		expected[4] = new String[]{"f", "n", "n", "f", "f", "n", "n"}; // Eb
		expected[5] = new String[]{"f", "n", "n", "f", "n", "n", "n"}; // Bb
		expected[6] = new String[]{"n", "n", "n", "f", "n", "n", "n"}; // F
		expected[7] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // C
		expected[8] = new String[]{"n", "n", "n", "n", "n", "n", "s"}; // G
		expected[9] = new String[]{"n", "n", "s", "n", "n", "n", "s"}; // D
		expected[10] = new String[]{"n", "n", "s", "n", "n", "s", "s"}; // A
		expected[11] = new String[]{"n", "s", "s", "n", "n", "s", "s"}; // E
		expected[12] = new String[]{"n", "s", "s", "n", "s", "s", "s"}; // B
		expected[13] = new String[]{"s", "s", "s", "n", "s", "s", "s"}; // F#
		expected[14] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // C#
		
		String[][] actual = MEIExport.makeAlterationGrid(MEIExport.makeMIDIPitchClassGrid());
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}
	
	
	public void testMakePitchClassGrid() {
		String[][] expected = new String[15][7];
		expected[0] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cb
		expected[1] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gb
		expected[2] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Db
		expected[3] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Ab
		expected[4] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Eb
		expected[5] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bb
		expected[6] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F
		expected[7] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C
		expected[8] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G
		expected[9] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D
		expected[10] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A
		expected[11] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // E
		expected[12] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // B
		expected[13] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#
		expected[14] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // C#
		
		String[][] actual = MEIExport.makePitchClassGrid();
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}

}
