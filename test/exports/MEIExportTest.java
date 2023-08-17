package exports;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import tbp.Encoding;
import tbp.Event;
import tbp.Symbol;

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
		
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		encodingTestpiece = new File(MEIExport.rootDir + "data/annotated/encodings/test/"  + "testpiece.tbp");
	}


	@Override
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
		for (Integer key : MEIExport.getKeySigMPCs().keySet()) {
//		for (Entry<Integer, Integer[]> entry : MEIExport.getKeys().entrySet()) {
//		for (Integer[] key : MEIExport.getKeys()) {
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


	public void testMakeMIDIPitchClassGrid() {
		Integer[][] expected = new Integer[30][7];
		expected[0] = new Integer[]{11, 1, 3, 4, 6, 8, 10}; // Cb
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
		//
		expected[15] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // Abm
		expected[16] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // Ebm
		expected[17] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // Bbm
		expected[18] = new Integer[]{5, 7, 8, 10, 0, 1, 3}; // Fm
		expected[19] = new Integer[]{0, 2, 3, 5, 7, 8, 10}; // Cm
		expected[20] = new Integer[]{7, 9, 10, 0, 2, 3, 5}; // Gm
		expected[21] = new Integer[]{2, 4, 5, 7, 9, 10, 0}; // Dm
		expected[22] = new Integer[]{9, 11, 0, 2, 4, 5, 7}; // Am 
		expected[23] = new Integer[]{4, 6, 7, 9, 11, 0, 2}; // Em
		expected[24] = new Integer[]{11, 1, 2, 4, 6, 7, 9}; // Bm
		expected[25] = new Integer[]{6, 8, 9, 11, 1, 2, 4}; // F#m
		expected[26] = new Integer[]{1, 3, 4, 6, 8, 9, 11}; // C#m
		expected[27] = new Integer[]{8, 10, 11, 1, 3, 4, 6}; // G#m
		expected[28] = new Integer[]{3, 5, 6, 8, 10, 11, 1}; // D#m
		expected[29] = new Integer[]{10, 0, 1, 3, 5, 6, 8}; // A#m

		Integer[][] maj = MEIExport.makeMIDIPitchClassGrid(0);
		Integer[][] min = MEIExport.makeMIDIPitchClassGrid(1);
		Integer[][] actual = new Integer[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
		}

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}


	public void testMakeAlterationGrid() {
		String[][] expected = new String[30][7];
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
		//
		expected[15] = new String[]{"f", "f", "f", "f", "f", "f", "f"}; // Abm
		expected[16] = new String[]{"f", "n", "f", "f", "f", "f", "f"}; // Ebm
		expected[17] = new String[]{"f", "n", "f", "f", "n", "f", "f"}; // Bbm
		expected[18] = new String[]{"n", "n", "f", "f", "n", "f", "f"}; // Fm
		expected[19] = new String[]{"n", "n", "f", "n", "n", "f", "f"}; // Cm
		expected[20] = new String[]{"n", "n", "f", "n", "n", "f", "n"}; // Gm
		expected[21] = new String[]{"n", "n", "n", "n", "n", "f", "n"}; // Dm
		expected[22] = new String[]{"n", "n", "n", "n", "n", "n", "n"}; // Am
		expected[23] = new String[]{"n", "s", "n", "n", "n", "n", "n"}; // Em
		expected[24] = new String[]{"n", "s", "n", "n", "s", "n", "n"}; // Bm
		expected[25] = new String[]{"s", "s", "n", "n", "s", "n", "n"}; // F#m
		expected[26] = new String[]{"s", "s", "n", "s", "s", "n", "n"}; // C#m
		expected[27] = new String[]{"s", "s", "n", "s", "s", "n", "s"}; // G#m
		expected[28] = new String[]{"s", "s", "s", "s", "s", "n", "s"}; // D#m
		expected[29] = new String[]{"s", "s", "s", "s", "s", "s", "s"}; // A#m
		
		String[][] maj = MEIExport.makeAlterationGrid(MEIExport.makeMIDIPitchClassGrid(0));
		String[][] min = MEIExport.makeAlterationGrid(MEIExport.makeMIDIPitchClassGrid(1));
		String[][] actual = new String[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
		}
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
	    }
	}
	
	
	public void testMakePitchClassGrid() {
		String[][] expected = new String[30][7];
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
		//
		expected[15] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Abm
		expected[16] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Ebm
		expected[17] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bbm
		expected[18] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // Fm
		expected[19] = new String[]{"c", "d", "e", "f", "g", "a", "b"}; // Cm
		expected[20] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // Gm
		expected[21] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // Dm
		expected[22] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // Am
		expected[23] = new String[]{"e", "f", "g", "a", "b", "c", "d"}; // Em
		expected[24] = new String[]{"b", "c", "d", "e", "f", "g", "a"}; // Bm
		expected[25] = new String[]{"f", "g", "a", "b", "c", "d", "e"}; // F#m
		expected[26] = new String[]{"c", "d", "e", "f", "g", "a", "b", }; // C#m
		expected[27] = new String[]{"g", "a", "b", "c", "d", "e", "f"}; // G#m
		expected[28] = new String[]{"d", "e", "f", "g", "a", "b", "c"}; // D#m
		expected[29] = new String[]{"a", "b", "c", "d", "e", "f", "g"}; // A#m

		String[][] maj = MEIExport.makePitchClassGrid(0);
		String[][] min = MEIExport.makePitchClassGrid(1);
		String[][] actual = new String[30][7];
		for (int i = 0; i < maj.length; i++) {
			actual[i] = maj[i];
		}
		for (int i = 0; i < min.length; i++) {
			actual[i+15] = min[i];
		}
		
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


	public void testGetDur() {
		List<Integer> expected = Arrays.asList(new Integer[]{
			96, 144, 168, // brevis; 0, 1, 2 dots
			48, 72, 84, // semibrevis; 0, 1, 2 dots 
			24, 36, 42, // minim; 0, 1, 2 dots
			12, 18, 21 // semiminim; 0, 1, 2 dots
		});

		List<Integer> XMLDurs = Arrays.asList(new Integer[]{
			1, 1, 1, 2, 2, 2, 4, 4, 4, 8, 8, 8});
		
		List<Integer> dots = Arrays.asList(new Integer[]{
			0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2
		});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < XMLDurs.size(); i++) {
			actual.add(MEIExport.getDur(XMLDurs.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
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
//		List<List<String[]>> ebl = encoding.getExtendedEventsPerBar(true);
		List<Event> ebl = Encoding.removeDecorativeBarlineEvents(encoding.getEvents());
//		for (List<String[]> l : ebl) {
		for (Event e : ebl) {
			events.add(e.getEncoding().substring(0, 
				e.getEncoding().lastIndexOf(Symbol.SYMBOL_SEPARATOR)));
		}
//		}
		for (String event : events) {
			if (!event.equals(Symbol.SYSTEM_BREAK_INDICATOR) &&
				!event.equals(Symbol.END_BREAK_INDICATOR)) {
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
		assertEquals(expected, actual);
	}


	public void testRoundAlt() {
		Rational two = new Rational(2, 1);
		List<Rational> all = Arrays.asList(new Rational[]{
			// On grid
			new Rational(0, 96),
			new Rational(48, 96),
			new Rational(96, 96),
			// Between 0/96 and 1/96
			new Rational(1, 4*96), // closest to 0/96
			new Rational(2, 4*96), // equally close to both
			new Rational(3, 4*96), // closest to 1/96
			// Between 47/96 and 48/96
			new Rational(47, 96).add(new Rational(1, 4*96)), // closest to 47/96
			new Rational(47, 96).add(new Rational(2, 4*96)), // equally close to both
			new Rational(47, 96).add(new Rational(3, 4*96)), // closest to 48/96
			// Between 95/96 and 96/96
			new Rational(95, 96).add(new Rational(1, 4*96)), // closest to 95/96
			new Rational(95, 96).add(new Rational(2, 4*96)), // equally close to both
			new Rational(95, 96).add(new Rational(3, 4*96)), // closest to 96/96
			
			// Between 2 and 2 1/96
			two.add(new Rational(1, 4*96)), // closest to 2
			two.add(new Rational(2, 4*96)), // equally close to both
			two.add(new Rational(3, 4*96)), // closest to 2 1/96
			// Between 2 47/96 and 2 48/96
			two.add(new Rational(47, 96).add(new Rational(1, 4*96))), // closest to 2 47/96
			two.add(new Rational(47, 96).add(new Rational(2, 4*96))), // equally close to both
			two.add(new Rational(47, 96).add(new Rational(3, 4*96))), // closest to 2 48/96
			// Between 2 95/96 and 3
			two.add(new Rational(95, 96).add(new Rational(1, 4*96))), // closest to 2 95/96
			two.add(new Rational(95, 96).add(new Rational(2, 4*96))), // equally close to both
			two.add(new Rational(95, 96).add(new Rational(3, 4*96))), // closest to 3
		});
		List<Rational> expected = Arrays.asList(new Rational[]{
			new Rational(0, 96),
			new Rational(1, 2),
			new Rational(1, 1),
			//
			new Rational(0, 96), 
			new Rational(1, 96), 
			new Rational(1, 96),
			//
			new Rational(47, 96), 
			new Rational(48, 96), 
			new Rational(48, 96),
			//
			new Rational(95, 96), 
			new Rational(96, 96), 
			new Rational(96, 96),
			//
			two.add(new Rational(0, 96)), 
			two.add(new Rational(1, 96)), 
			two.add(new Rational(1, 96)),
			//
			two.add(new Rational(47, 96)), 
			two.add(new Rational(48, 96)), 
			two.add(new Rational(48, 96)),
			//
			two.add(new Rational(95, 96)), 
			two.add(new Rational(96, 96)), 
			two.add(new Rational(96, 96)),
		});
		List<Integer> gridNums = IntStream.rangeClosed(0, 96).boxed().collect(Collectors.toList());
		List<Rational> actual = new ArrayList<Rational>();
		for (Rational r : all) {
			actual.add(MEIExport.round(r, gridNums));
		}
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
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


	public void testGetDottedNoteLength() {
		List<Rational> expected = Arrays.asList(new Rational[]{
			// 1/1
			new Rational(1, 1), // 0 dots
			new Rational(3, 2), // 1 dot
			new Rational(7, 4), // 2 dots
			new Rational(15, 8), // 3 dots
			// 1/2
			new Rational(1, 2), // 0 dots
			new Rational(3, 4), // 1 dot
			new Rational(7, 8), // 2 dots
			new Rational(15, 16), // 3 dots
			// 1/4
			new Rational(1, 4), // 0 dots
			new Rational(3, 8), // 1 dot
			new Rational(7, 16), // 2 dots
			new Rational(15, 32), // 3 dots

		});

		List<Rational> undotted = Arrays.asList(new Rational[]{
			new Rational(1, 1), new Rational(1, 1), new Rational(1, 1), new Rational(1, 1),
			new Rational(1, 2), new Rational(1, 2), new Rational(1, 2), new Rational(1, 2),
			new Rational(1, 4), new Rational(1, 4), new Rational(1, 4), new Rational(1, 4)
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < undotted.size(); i++) {
			actual.add(MEIExport.getDottedNoteLength(undotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetDottedNoteLengthAlt() {
		List<Integer> expected = Arrays.asList(new Integer[]{
			// 1/1
			96, 144, 168, 180, // 0, 1, 2, 3 dots
			// 1/2
			48, 72, 84, 90, // 0, 1, 2, 3 dots
			// 1/4
			24, 36, 42, 45 // 0, 1, 2, 3 dots
		});

		List<Integer> undotted = Arrays.asList(new Integer[]{
			96, 96, 96, 96, 48, 48, 48, 48, 24, 24, 24, 24
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < undotted.size(); i++) {
			actual.add(MEIExport.getDottedNoteLength(undotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetUndottedNoteLength() {
		List<Integer> expected = Arrays.asList(new Integer[]{
			96, 96, 96, 96, // 1/1 
			48, 48, 48, 48, // 1/2 
			24, 24, 24, 24 // 1/4 
		});

		List<Integer> dotted = Arrays.asList(new Integer[]{
			96, 144, 168, 180, // 0, 1, 2, 3 dots 
			48, 72, 84, 90, // 0, 1, 2, 3 dots 
			24, 36, 42, 45 // 0, 1, 2, 3 dots
		});
		List<Integer> dots = Arrays.asList(new Integer[]{0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3});
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < dotted.size(); i++) {
			actual.add(MEIExport.getUndottedNoteLength(dotted.get(i), dots.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
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


	public void testSpellPitch() {
		Integer[][] mpcGridMaj = MEIExport.makeMIDIPitchClassGrid(0);
		String[][] altGridMaj = MEIExport.makeAlterationGrid(mpcGridMaj);
		String[][] pcGridMaj = MEIExport.makePitchClassGrid(0);
		Integer[][] mpcGridMin = MEIExport.makeMIDIPitchClassGrid(1);
		String[][] altGridMin = MEIExport.makeAlterationGrid(mpcGridMin);
		String[][] pcGridMin = MEIExport.makePitchClassGrid(1);

		int Bbm = 2;
		int Fm = 3;
		int Cm = 4;
		int Gm = 5;		
		int Dm = 6;
		int Am = 7; // int C = 7;
		int Em = 8; // int G = 8;
		int Bm = 9; // int D = 9;
		int Fsm = 10;
		int Csm = 11;
		int Gsm = 12;

		List<List<Integer>> aie = null;

		List<String[]> expected = new ArrayList<>();
		// pitch is in key
		// Flats
		expected.add(new String[]{"b", "n"}); // 71 in Am
		expected.add(new String[]{"b", "f"}); // 70 in Dm
		expected.add(new String[]{"e", "f"}); // 63 in Gm
		expected.add(new String[]{"a", "f"}); // 68 in Cm
		expected.add(new String[]{"d", "f"}); // 61 in Fm
		expected.add(new String[]{"g", "f"}); // 66 in Bbm
		// Sharps
		expected.add(new String[]{"f", "s"}); // 66 in Em
		expected.add(new String[]{"c", "s"}); // 61 in Bm
		expected.add(new String[]{"g", "s"}); // 68 in F#m
		expected.add(new String[]{"d", "s"}); // 63 in C#m
		expected.add(new String[]{"a", "s"}); // 70 in G#m
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		expected.add(new String[]{"b", "f"}); // 70 in Am
		expected.add(new String[]{"a", "f"}); // 68 in Dm
		expected.add(new String[]{"a", "f"}); // 68 in Gm
		expected.add(new String[]{"g", "f"}); // 66 in Cm
		expected.add(new String[]{"g", "f"}); // 66 in Fm
		expected.add(new String[]{"f", "f"}); // 64 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s"}); // 68 in Em
		expected.add(new String[]{"g", "s"}); // 68 in Bm
		expected.add(new String[]{"a", "s"}); // 70 in F#m
		expected.add(new String[]{"a", "s"}); // 70 in C#m
		expected.add(new String[]{"b", "s"}); // 60 in G#m
		// 2. naturalised KA
		// Flats
		expected.add(new String[]{"b", "n"}); // 71 in Dm
		expected.add(new String[]{"e", "n"}); // 64 in Gm
		expected.add(new String[]{"a", "n"}); // 69 in Cm
		expected.add(new String[]{"d", "n"}); // 62 in Fm
		expected.add(new String[]{"g", "n"}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"f", "n"}); // 65 in Em
		expected.add(new String[]{"c", "n"}); // 60 in Bm
		expected.add(new String[]{"g", "n"}); // 67 in F#m
		expected.add(new String[]{"d", "n"}); // 62 in C#m
		expected.add(new String[]{"a", "n"}); // 69 in G#m		
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		expected.add(new String[]{"b", "f"}); // 70 in Am
		expected.add(new String[]{"e", "f"}); // 63 in Dm
		expected.add(new String[]{"a", "f"}); // 68 in Gm
		expected.add(new String[]{"d", "f"}); // 61 in Cm
		expected.add(new String[]{"g", "f"}); // 66 in Fm
		expected.add(new String[]{"c", "f"}); // 71 in Bbm
		// ULT, sharps
		expected.add(new String[]{"f", "n"}); // 65 in Em
		expected.add(new String[]{"c", "n"}); // 60 in Bm
		expected.add(new String[]{"g", "n"}); // 67 in F#m
		expected.add(new String[]{"d", "n"}); // 62 in C#m
		expected.add(new String[]{"a", "n"}); // 69 in G#m
		// LLT, flats
		expected.add(new String[]{"g", "s"}); // 68 in Am
		expected.add(new String[]{"c", "s"}); // 61 in Dm
		expected.add(new String[]{"f", "s"}); // 66 in Gm
		expected.add(new String[]{"b", "n"}); // 71 in Cm
		expected.add(new String[]{"e", "n"}); // 64 in Fm
		expected.add(new String[]{"a", "n"}); // 69 in Bbm
		// LLT, sharps
		expected.add(new String[]{"d", "s"}); // 63 in Em
		expected.add(new String[]{"a", "s"}); // 70 in Bm
		expected.add(new String[]{"e", "s"}); // 65 in F#m
		expected.add(new String[]{"b", "s"}); // 60 in C#m
		expected.add(new String[]{"f", "x"}); // 67 in G#m
		// 4. R3 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"c", "s"}); // 61 in Am
		expected.add(new String[]{"f", "s"}); // 66 in Dm
		expected.add(new String[]{"b", "n"}); // 71 in Gm
		expected.add(new String[]{"e", "n"}); // 64 in Cm
		expected.add(new String[]{"a", "n"}); // 69 in Fm
		expected.add(new String[]{"d", "n"}); // 62 in Bbm
		// Sharps
		expected.add(new String[]{"g", "s"}); // 68 in Em
		expected.add(new String[]{"d", "s"}); // 63 in Bm
		expected.add(new String[]{"a", "s"}); // 70 in F#m
		expected.add(new String[]{"e", "s"}); // 65 in C#m
		expected.add(new String[]{"b", "s"}); // 60 in G#m
		// 5. R6 for minor (or minor parallel)
		// Flats
		expected.add(new String[]{"f", "s"}); // 66 in Am
		expected.add(new String[]{"b", "n"}); // 71 in Dm
		expected.add(new String[]{"e", "n"}); // 64 in Gm
		expected.add(new String[]{"a", "n"}); // 69 in Cm
		expected.add(new String[]{"d", "n"}); // 62 in Fm
		expected.add(new String[]{"g", "n"}); // 67 in Bbm
		// Sharps
		expected.add(new String[]{"c", "s"}); // 61 in Em
		expected.add(new String[]{"g", "s"}); // 68 in Bm
		expected.add(new String[]{"d", "s"}); // 63 in F#m
		expected.add(new String[]{"a", "s"}); // 70 in C#m
		expected.add(new String[]{"e", "s"}); // 65 in G#m	

		List<String[]> actual = new ArrayList<>();
		Rational o = new Rational(1, 2);
		// pitch is in key
		// Flats
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, -2, mpcGridMaj[Gm], altGridMaj[Gm], pcGridMaj[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -5, mpcGridMaj[Bbm], altGridMaj[Bbm], pcGridMaj[Bbm], aie).get(0));
		// Sharps
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// pitch is not in key
		// 1. next or second-next KA
		// Flats
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, -2, mpcGridMaj[Gm], altGridMaj[Gm], pcGridMaj[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(64, 1, o, -5, mpcGridMaj[Bbm], altGridMaj[Bbm], pcGridMaj[Bbm], aie).get(0));
		// Sharps
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(60, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// 2. naturalised KA
		// Flats
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(64, 1, o, -2, mpcGridMaj[Gm], altGridMaj[Gm], pcGridMaj[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(62, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(67, 1, o, -5, mpcGridMaj[Bbm], altGridMaj[Bbm], pcGridMaj[Bbm], aie).get(0));
		// Sharps
		actual.add((String[]) MEIExport.spellPitch(65, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(60, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(67, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(62, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// 3. ULT/LLT for minor (or minor parallel)
		// ULT, flats
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, -2, mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, -5, mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm], aie).get(0));
		// ULT, sharps
		actual.add((String[]) MEIExport.spellPitch(65, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(60, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(67, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(62, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// LLT, flats
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -2, mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(64, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, -5, mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm], aie).get(0));
		// LLT, sharps
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(65, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(60, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(67, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// 4. R3 for minor (or minor parallel)
		// Flats
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, -2, mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(64, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(62, 1, o, -5, mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm], aie).get(0));
		// Sharps
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(65, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(60, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));
		// 5. R6 for minor (or minor parallel)
		// Flats
		actual.add((String[]) MEIExport.spellPitch(66, 1, o, 0, mpcGridMin[Am], altGridMin[Am], pcGridMin[Am], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(71, 1, o, -1, mpcGridMin[Dm], altGridMin[Dm], pcGridMin[Dm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(64, 1, o, -2, mpcGridMin[Gm], altGridMin[Gm], pcGridMin[Gm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(69, 1, o, -3, mpcGridMin[Cm], altGridMin[Cm], pcGridMin[Cm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(62, 1, o, -4, mpcGridMin[Fm], altGridMin[Fm], pcGridMin[Fm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(67, 1, o, -5, mpcGridMin[Bbm], altGridMin[Bbm], pcGridMin[Bbm], aie).get(0));
		// Sharps
		actual.add((String[]) MEIExport.spellPitch(61, 1, o, 1, mpcGridMin[Em], altGridMin[Em], pcGridMin[Em], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(68, 1, o, 2, mpcGridMin[Bm], altGridMin[Bm], pcGridMin[Bm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(63, 1, o, 3, mpcGridMin[Fsm], altGridMin[Fsm], pcGridMin[Fsm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(70, 1, o, 4, mpcGridMin[Csm], altGridMin[Csm], pcGridMin[Csm], aie).get(0));
		actual.add((String[]) MEIExport.spellPitch(65, 1, o, 5, mpcGridMin[Gsm], altGridMin[Gsm], pcGridMin[Gsm], aie).get(0));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}

}
