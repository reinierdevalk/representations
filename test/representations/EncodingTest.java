package representations;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import paths.Paths;
import tbp.TabSymbolSet;
import tools.ToolBox;

public class EncodingTest extends TestCase {

	private File encodingTestpiece;
	private String miniRawEncoding;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		String root = Paths.getRootPath() + Paths.getDataDir(); 
		encodingTestpiece = 
			new File(root + Paths.getEncodingsPath() + Paths.getTestDir() + "testpiece.tbp");
		miniRawEncoding = "{}{}{}{FrenchTab}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"; 
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testMakeCleanEncoding() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));

		String expected = 
			"McC3.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|." + 
		  "sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2.>.sm.a6.b3.a2.a1.>.a3.e2.>.|./" +
			"fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|.c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||.//";
		String actual = encoding.makeCleanEncoding();

		assertEquals(expected, actual);
	}


	@Test
	public void testMakeMetadata() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();

		List<String> expected = 
			Arrays.asList(new String[]{
				"Author", 
				"Title", 
				"Source (year)", 
				"FrenchTab", 
				"A", 
				"2/2 (1-3)", 
				"1"
		}); 

		List<String> actual = encoding.makeMetadata();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testMakeEventsBarlinesFootnotes() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setTabSymbolSet();

		List<List<String[]>> expected = new ArrayList<>();
		List<String[]> system1 = new ArrayList<>();
		system1.add(new String[]{"McC3.", "1", null, null});
		system1.add(new String[]{"sb.", "1", null, null});
		system1.add(new String[]{"mi.", "1", null, null});
		system1.add(new String[]{"mi.a5.c4.b2.a1.", "1", null, null});
		system1.add(new String[]{"|.", "1", "@Footnote 1", "#1"});
		system1.add(new String[]{"sm*.a6.c4.i2.a1.", "2", null, null});
		system1.add(new String[]{"fu.d6.", "2", null, null});
		system1.add(new String[]{"sm.c6.a5.e4.b2.", "2", null, null});
		system1.add(new String[]{"a6.", "2", null, null});
		system1.add(new String[]{"mi.a6.h5.c4.b3.f2.", "2", "@'mi.a6.' in source", "#2"});
		system1.add(new String[]{"sm.a6.b3.a2.a1.", "2", null, null});
		system1.add(new String[]{"a3.e2.", "2", null, null});
		system1.add(new String[]{"|.", "2", null, null});
		expected.add(system1);
		List<String[]> system2 = new ArrayList<>();
		system2.add(new String[]{"fu.a6.c4.a2.a1.", "3", null, null});
		system2.add(new String[]{"e2.", "3", null, null});
		system2.add(new String[]{"sf.a1.", "3", null, null});
		system2.add(new String[]{"e2.", "3", null, null});
		system2.add(new String[]{"|.", "3", null, null});
		system2.add(new String[]{"c2.", "4", null, null});
		system2.add(new String[]{"e2.", "4", null, null});
		system2.add(new String[]{"mi.a1.", "4", null, null});
		system2.add(new String[]{"mi.", "4", null, null});
		system2.add(new String[]{"mi.a6.c4.a2.a1.", "4", null, null});
		system2.add(new String[]{"||.", "4", null, null});
		expected.add(system2);

		List<List<String[]>> actual = encoding.makeEventsBarlinesFootnotes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {			
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).length, actual.get(i).get(j).length);
				for (int k = 0; k < expected.get(i).get(j).length; k++) {
					assertEquals(expected.get(i).get(j)[k], actual.get(i).get(j)[k]);
				}
			}
		}
	}


	public void testMakeListOfSymbols() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setTabSymbolSet();
		encoding.setEventsBarlinesFootnotes();
		
		List<List<String>> expected = new ArrayList<List<String>>();
		// listOfAllSymbols
		expected.add(Arrays.asList(new String[]{
			"McC3", ">", "sb", ">", "mi", ">", "mi", "a5", "c4", "b2", "a1",">", 
			"|", 
			"sm*", "a6", "c4", "i2", "a1", ">", "fu", "d6", ">", "sm", "c6", "a5", "e4", 
			"b2", ">", "a6", ">", "mi", "a6", "h5", "c4", "b3", "f2", ">", "sm", "a6", 
			"b3", "a2", "a1", ">", "a3", "e2", ">", 
			"|", 
			"fu", "a6", "c4", "a2", "a1", ">", "e2", ">", "sf", "a1", ">", "e2", ">", 
			"|", 
			"c2", ">", "e2", ">", "mi", "a1", ">", "mi", ">", "mi", "a6", "c4", "a2", 
			"a1", ">", 
			"||"
		}));
		// listOfTabSymbols
		expected.add(Arrays.asList(new String[]{
			"a5", "c4", "b2", "a1", 
			"a6", "c4", "i2", "a1",
			"d6", 
			"c6", "a5", "e4", "b2", 
			"a6", 
			"a6", "h5", "c4", "b3", "f2", 
			"a6", "b3", "a2", "a1", 
			"a3", "e2", 
			"a6", "c4", "a2", "a1", 
			"e2", "a1", "e2", "c2", "e2", "a1", 
			"a6", "c4", "a2", "a1"
		}));
		// listOfRhythmSymbols
		expected.add(Arrays.asList(new String[]{
			"sb", "mi", "mi", 
			"sm*", "fu", "sm", "mi", "sm", 
			"fu", "sf", "mi", "mi", "mi"
		}));
		// listOfMensurationSigns
		expected.add(Arrays.asList(new String[]{"McC3"}));
		// listOfBarlines
		expected.add(Arrays.asList(new String[]{"|", "|", "|", "||"}));
		// listOfAllEvents
		expected.add(Arrays.asList(new String[]{
			"McC3", "sb", "mi", "mi.a5.c4.b2.a1", 
			"|", 
			"sm*.a6.c4.i2.a1", "fu.d6", "sm.c6.a5.e4.b2", "a6", "mi.a6.h5.c4.b3.f2", 
			"sm.a6.b3.a2.a1", "a3.e2", 
			"|", 
			"fu.a6.c4.a2.a1", "e2", "sf.a1", "e2", 
			"|", 
			"c2", "e2", "mi.a1", "mi", "mi.a6.c4.a2.a1", 
			"||"
		}));

		List<List<String>> actual = encoding.makeListsOfSymbols();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	public void testMakeListOfStatistics() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setTabSymbolSet();
		encoding.setEventsBarlinesFootnotes();
		encoding.setListsOfSymbols();

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// isTabSymbolEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0}));
		// isRhythmSymbolEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0}));
		// isRestEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}));
		// isMensurationSignEvent
		expected.add(Arrays.asList(new Integer[]{
			1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
		// isBarlineEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}));
		// sizeOfEvents
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 4, 0, 4, 1, 4, 1, 5, 4, 2, 0, 4, 1, 1, 1, 0, 1, 1, 1, 0, 4, 0}));
		// horizontalPositionOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			3, 3, 3, 3, 5, 5, 5, 5, 6, 7, 7, 7, 7, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10, 11, 
			11, 13, 13, 13, 13, 14, 15, 16, 18, 19, 20, 22, 22, 22, 22}));
		// verticalPositionOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 2, 3, 0, 1, 2, 3, 0, 0, 1, 2, 3, 0, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 0, 1,
			2, 3, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3}));
		// horizontalPositionInTabSymbolEventsOnly
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 0, 1, 1, 1, 1, 2, 3, 3, 3, 3, 4, 5, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8,
			8, 8, 9, 10, 11, 12, 13, 14, 15, 15, 15, 15}));

		List<List<Integer>> actual = encoding.makeListsOfStatistics();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}





//	@Test
//	public void testSetAndGetTunings() {
//		Encoding encoding = new Encoding(encodingTestpiece);
//
//		Tuning[] expected = new Tuning[]{Tuning.A, Tuning.A};
//
//		Tuning[] actual = encoding.getTunings();
//
//		assertEquals(expected.length, actual.length);
//		for (int i = 0; i < expected.length; i++) {
//			assertEquals(expected[i], actual[i]);
//		}
//	}



//	@Test
//	public void testGetTuningBassCourses() {
//		Encoding encoding1 = new Encoding(encodingTestpiece);
//		Encoding encoding2 = new Encoding(new File("F:/research/data/annotated/encodings/tab-int/5vv/adriansen-1584_6-d_vn_si.tbp"));
//
//		List<TuningBassCourses> expected = 
//			Arrays.asList(new TuningBassCourses[]{null, TuningBassCourses.SECOND});
//
//		List<TuningBassCourses> actual = Arrays.asList(new TuningBassCourses[]{
//			encoding1.getTuningBassCourses(), encoding2.getTuningBassCourses()});
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//		assertEquals(expected, actual);
//	}


	@Test
	public void testCheckForMetaDataErrors() {
		String correct = "{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{METER_INFO:m}{DIMINUTION:d}\ne{n}c{o}d{i}n{g}";
		List<String> rawEncodings = new ArrayList<String>();
		// Missing tag
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TUNING:t}{METER_INFO:m}{DIMINUTION:d}\nencoding");
		// Wrong sequence of tags
		rawEncodings.add("{AUTHOR:a}{METER_INFO:m}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TITLE:t}{DIMINUTION:d}\nencoding");
		// OPEN_ or CLOSE_METADATA_BRACKET errors
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t{METER_INFO:m}{DIMINUTION:d}\nencoding"); // CLOSE_METADATA_BRACKET missing in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}METER_INFO:m}{DIMINUTION:d}\nencoding"); // OPEN_METADATA_BRACKET missing in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}}{METER_INFO:m}{DIMINUTION:d}\nencoding"); // Double CLOSE_METADATA_BRACKET in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{{METER_INFO:m}{DIMINUTION:d}\nencoding"); // Double OPEN_METADATA_BRACKET in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{METER_INFO:m}{DIMINUTION:d\nencoding"); // CLOSE_METADATA_BRACKET missing after last tag		
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{METER_INFO:m}{DIMINUTION:d}\ne{n}c{od{i}n{g}"); // CLOSE_METADATA_BRACKET missing in remainder
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{METER_INFO:m}{DIMINUTION:d}\ne{n}c{o}di}n{g}"); // OPEN_METADATA_BRACKET missing in remainder 
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{METER_INFO:m}{DIMINUTION:d}\ne{n}c{o}d{i}n{g"); // CLOSE_METADATA_BRACKET missing at end
		// Correct encoding
		rawEncodings.add(correct);

		List<Boolean> expected = Arrays.asList(new Boolean[]{
			true, true, true, true, true, true, true, true, true, true, false}); 

		List<Boolean> actual = new ArrayList<Boolean>();
		for (int i = 0; i < rawEncodings.size(); i++) {
			actual.add(Encoding.checkForMetadataErrors(rawEncodings.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


//	@Test
//	public void testCheckForEncodingErrors() {
//		List<String> encodings = new ArrayList<String>();
//		// VR 3 not met (first system starts with SBI)
//		encodings.add("{}{}{}{FrenchTab}{}{}{}/MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
//		// Unknown symbol (in first system)
//		encodings.add("{}{}{}{FrenchTab}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.bs1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
//		// LR 7 not met (invalid chord in first system)
//		encodings.add("{}{}{}{FrenchTab}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a2.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
//		// Correct encoding
//		encodings.add(ToolBox.readTextFile(encodingTestpiece));
//
//		List<String[]> expected = new ArrayList<String[]>(); 
//		expected.add(new String[]{"23", "24", "INVALID ENCODING ERROR -- Remove this system break indicator.",
//			"See VALIDITY RULE 3: A system cannot start with a punctuation symbol."});
//		expected.add(new String[]{"50", "53", "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.",
//			"See VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator."});
//		expected.add(new String[]{"36", "47", "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).", 
//			"See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course."});
//		expected.add(null);
//
//		List<String[]> actual = new ArrayList<String[]>();
//		for (int i = 0; i < encodings.size(); i++) {
//			Encoding e = new Encoding(); 
//			e.setRawEncoding(encodings.get(i));
//			e.setCleanEncoding();
//			e.setMetadata();
//			e.setTabSymbolSet();
//			actual.add(Encoding.checkForEncodingErrors(e.getRawEncoding(), 
//				e.getCleanEncoding(), e.getTabSymbolSet())); 	
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			if (expected.get(i) == null) {
//				assertArrayEquals(expected.get(i), actual.get(i));
//			}
//			else {
//				assertEquals(expected.get(i).length, actual.get(i).length);
//				for (int j = 0; j < expected.get(i).length; j++) {
//					assertEquals(expected.get(i)[j], actual.get(i)[j]);
//				}
//			}
//		}
//	}


	@Test
	public void testAlignRawAndCleanEncoding() {	
		Encoding encoding = new Encoding(new Encoding(encodingTestpiece).getRawEncoding(), 
			"", Encoding.METADATA_CHECKED);

		boolean print = false;
		if (print) {
			System.out.println(encoding.getRawEncoding().indexOf("McC3.")); // 149
			System.out.println(encoding.getRawEncoding().indexOf("sm*.a6.c4.i2.a1.")); // 209
			System.out.println(encoding.getRawEncoding().indexOf("sm.a6.b3.a2.a1")); // 298
			System.out.println(encoding.getRawEncoding().indexOf("fu.a6.c4.a2.a1.")); // 337
		}
		Integer[] expected = new Integer[encoding.getRawEncoding().length()];
		Arrays.fill(expected, -1);
		// Section 1 ("McC3." up until "." after "{@Footnote 1}"): 36 chars (in 
		// rawEncoding split up by footnote into 35 + 1)
		// indices 0-35 in cleanEncoding (= 149-183 and 197 in rawEncoding)
		for (int i = 0; i < 35; i++) {
			expected[149 + i] = 0 + i;
		}
		for (int i = 0; i < 1; i++) {
			expected[197 + i] = 0 + 35 + i;
		}
		// Section 2 ("sm*.a6.c4.i2.a1." up until "sm.a6.b3.a2.a1"): 68 chars (in 
		// rawEncoding split up by footnote into 65 + 3)
		// indices 36-103 in cleanEncoding (= 209-273 and 295-297 in rawEncoding)
		for (int i = 0; i < 65; i++) {
			expected[209 + i] = 0 + 35 + 1 + i;
		}
		for (int i = 0; i < 3; i++) {
			expected[295 + i] = 0 + 35 + 1 + 65 + i;
		}		
		// Section 3 ("sm.a6.b3.a2.a1" up until and including SBI): 28 chars
		// indices 104-131 in cleanEncoding (= 298-325 in rawEncoding)
		for (int i = 0; i < 28; i++) {
			expected[298 + i] = 0 + 35 + 1 + 65 + 3 + i;
		}		
		// Section 4 ("fu.a6.c4.a2.a1." up until and including EBI): 82 chars
		// indices 132-213 in cleanEncoding (= 337-418 in rawEncoding)
		for (int i = 0; i < 82; i++) {
			expected[337 + i] = 0 + 35 + 1 + 65 + 3 + 28 + i;
		}

		Integer[] actual = 
			Encoding.alignRawAndCleanEncoding(encoding.getRawEncoding(), encoding.getCleanEncoding());

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	@Test
	public void testCheckValidityRules() { 
		List<String> rawEncodings = new ArrayList<String>();
		// VR 1
		rawEncodings.add(miniRawEncoding.replace("mi.>.|.", "m  i.>.|."));
		// VR 2
		rawEncodings.add(miniRawEncoding.replace("//", ""));
		// VR 3
		rawEncodings.add(miniRawEncoding.replace("{}MC3.>.", "{}/MC3.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO4.M33.>.", "//MO4.M33.>."));
		rawEncodings.add(miniRawEncoding.replace("/MC3.>.", "//MC3.>."));
		rawEncodings.add(miniRawEncoding.replace("{}MC3.>.", "{}.MC3.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO4.M33.>.", "/.MO4.M33.>."));
		rawEncodings.add(miniRawEncoding.replace("/MC3.>.", "/.MC3.>."));
		// VR 4
		rawEncodings.add(miniRawEncoding.replace("b1.>.|./", "b1.>.|/"));
		rawEncodings.add(miniRawEncoding.replace("b2.>./", "b2.>/"));
		rawEncodings.add(miniRawEncoding.replace("||.//", "||//"));
		// All VR met
		rawEncodings.add(miniRawEncoding);

		List<String[]> expected = new ArrayList<String[]>();
		// VR 1
		expected.add(new String[]{"69", "70", "INVALID ENCODING ERROR -- Remove this whitespace.", 
			"See VALIDITY RULE 1: The encoding cannot contain whitespace."});
		// VR 2
		expected.add(new String[]{"-1", "-1", "INVALID ENCODING ERROR -- The encoding does not end with an end break indicator.",	
			"See VALIDITY RULE 2: The encoding must end with an end break indicator."});
		// VR 3
		String error3a = "INVALID ENCODING ERROR -- Remove this system break indicator.";
		String error3b = "INVALID ENCODING ERROR -- Remove this symbol separator.";
		String rule3 = "See VALIDITY RULE 3: A system cannot start with a punctuation symbol.";
		expected.add(new String[]{"23", "24", error3a, rule3});
		expected.add(new String[]{"58", "59", error3a, rule3});
		expected.add(new String[]{"97", "98", error3a, rule3});
		expected.add(new String[]{"23", "24", error3b, rule3});
		expected.add(new String[]{"58", "59", error3b, rule3});
		expected.add(new String[]{"97", "98", error3b, rule3});
		// VR 4
		String error4a = "INVALID ENCODING ERROR -- Insert a symbol separator before this system break indicator.";
		String error4b = "INVALID ENCODING ERROR -- Insert a symbol separator before this end break indicator.";
		String rule4 = "See VALIDITY RULE 4: Each system must end with a symbol separator."; 		
		expected.add(new String[]{"56", "57", error4a, rule4});
		expected.add(new String[]{"95", "96", error4a, rule4});
		expected.add(new String[]{"134", "136", error4b, rule4});
		// All VR met
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String s : rawEncodings) {
			Encoding e = new Encoding(s, "", Encoding.METADATA_CHECKED);
			actual.add(Encoding.checkValidityRules(
				e.getCleanEncoding(), Encoding.alignRawAndCleanEncoding(s, e.getCleanEncoding())));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertArrayEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testCheckSymbols() {
		List<String> rawEncodings = new ArrayList<String>();
		// Missing symbol
		rawEncodings.add(miniRawEncoding.replace("b1.>.", ".>."));
		rawEncodings.add(miniRawEncoding.replace("b2.>.", ".>."));
		rawEncodings.add(miniRawEncoding.replace("b3.>.||.", ".>.||."));
		// Unknown symbol
		rawEncodings.add(miniRawEncoding.replace("b1.>.", "bs1.>."));
		rawEncodings.add(miniRawEncoding.replace("b2.>.", "bs2.>."));
		rawEncodings.add(miniRawEncoding.replace("b3.>.||.", "bs3.>.||."));
		// No missing or unknown symbols
		rawEncodings.add(miniRawEncoding);

		List<String[]> expected = new ArrayList<String[]>();
		// Missing symbol
		String errorMissing = "MISSING SYMBOL ERROR -- Remove symbol separator or insert symbol before.";
		String rule5 = "See VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator.";
		expected.add(new String[]{"50", "51", errorMissing, rule5});
		expected.add(new String[]{"91", "92", errorMissing, rule5});
		expected.add(new String[]{"127", "128", errorMissing, rule5});
		// Unknown symbol
		String errorUnknown = "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.";
		expected.add(new String[]{"50", "53", errorUnknown, rule5});
		expected.add(new String[]{"91", "94", errorUnknown, rule5});
		expected.add(new String[]{"127", "130", errorUnknown, rule5});
		// No missing or unknown symbols
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String s : rawEncodings) {
			Encoding e = new Encoding(s, "", Encoding.METADATA_CHECKED);
			actual.add(Encoding.checkSymbols(
				e.getCleanEncoding(), e.getTabSymbolSet(), 
				Encoding.alignRawAndCleanEncoding(s, e.getCleanEncoding())));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertArrayEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


	@Test
	public void testCheckLayoutRules() {
		List<String> rawEncodings = new ArrayList<String>();
		// LR 1
		rawEncodings.add(miniRawEncoding.replace("{}MC3.>.", "{}>.MC3.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO4.M33.>.", "/>.MO4.M33.>."));
		rawEncodings.add(miniRawEncoding.replace("/MC3.>.", "/>.MC3.>."));
		// LR 2
		rawEncodings.add(miniRawEncoding.replace("b1.>.|./", "b1./"));
		rawEncodings.add(miniRawEncoding.replace("b2.>./", "b2./"));
		rawEncodings.add(miniRawEncoding.replace("b3.>.||.//", "b3.//"));
		// LR 3
		rawEncodings.add(miniRawEncoding.replace("b1.>.|.", "b1.>.|.>."));
		rawEncodings.add(miniRawEncoding.replace("b2.>.", "b2.>.>."));
		rawEncodings.add(miniRawEncoding.replace("||.", "||.>."));
		// LR 4
		rawEncodings.add(miniRawEncoding.replace("b1.>.", "b1."));
		rawEncodings.add(miniRawEncoding.replace("mi.a4.a3.a2.>.", "mi.a4.a3.a2."));
		rawEncodings.add(miniRawEncoding.replace("b3.>.||.", "b3.||."));
		// LR 5
		rawEncodings.add(miniRawEncoding.replace("sb.>.", "sb."));
		rawEncodings.add(miniRawEncoding.replace("mi.>.", "mi."));
		rawEncodings.add(miniRawEncoding.replace("*.>.mi*.>.", "*.mi*.>."));
		// LR 6
		rawEncodings.add(miniRawEncoding.replace("MC3.>.sb.>.", "MC3.sb.>."));
		rawEncodings.add(miniRawEncoding.replace("MO4.M33.>.", "MO4.M33.mi."));
		rawEncodings.add(miniRawEncoding.replace("MC3.>.*.>.", "MC3.*.>."));
		// LR 7
		rawEncodings.add(miniRawEncoding.replace("mi.a3.a2.a1.>.", "mi.a3.a2.a2.>."));
		rawEncodings.add(miniRawEncoding.replace("mi.a4.a3.a2.>.", "mi.a4.a3.a3.>."));
		rawEncodings.add(miniRawEncoding.replace("mi.b5.b4.b3.>.", "mi.b5.b4.b4.>."));
		// LR 8
		rawEncodings.add(miniRawEncoding.replace("mi.a3.a2.a1.>.", "mi.a1.a2.a3.>."));	
		rawEncodings.add(miniRawEncoding.replace("mi.a4.a3.a2.>.", "mi.a2.a3.a4.>."));
		rawEncodings.add(miniRawEncoding.replace("mi.b5.b4.b3.>.", "mi.b3.b4.b5.>."));
		// All LR met
		rawEncodings.add(miniRawEncoding);

		List<String[]> expected = new ArrayList<String[]>(); 
		// LR 1
		String error1 = "INVALID ENCODING ERROR -- Remove this space."; 
		String rule1 = "See LAYOUT RULE 1: A system can start with any event but a space.";
		expected.add(new String[]{"23", "24", error1, rule1});
		expected.add(new String[]{"58", "59", error1, rule1});
		expected.add(new String[]{"97", "98", error1, rule1});
		// LR 2
		String error2 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule2 = "See LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline.";
		expected.add(new String[]{"50", "52", error2, rule2});
		expected.add(new String[]{"91", "93", error2, rule2});
		expected.add(new String[]{"127", "129", error2, rule2});
		// LR 3
		String error3 = "INVALID ENCODING ERROR -- Remove this space.";
		String rule3 = "See LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space.";
		expected.add(new String[]{"57", "58", error3, rule3});
		expected.add(new String[]{"96", "97", error3, rule3});
		expected.add(new String[]{"135", "136", error3, rule3});
		// LR 4
		String error4 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule4 = "See LAYOUT RULE 4: A vertical sonority must be succeeded by a space."; 
		expected.add(new String[]{"50", "52", error4, rule4});
		expected.add(new String[]{"84", "86", error4, rule4});
		expected.add(new String[]{"127", "129", error4, rule4});
		// LR 5
		String error5 = "INVALID ENCODING ERROR -- Insert a space after this RhythmSymbol.";
		String rule5 = "See LAYOUT RULE 5: A rest (or rhythm dot at the beginning of a system or bar) must be succeeded by a space."; 
		expected.add(new String[]{"29", "31", error5, rule5});
		expected.add(new String[]{"68", "70", error5, rule5});
		expected.add(new String[]{"103", "104", error5, rule5});
		// LR 6
		String error6 = "INVALID ENCODING ERROR -- Insert a space after this MensurationSign.";
		String rule6 = "See LAYOUT RULE 6: A mensuration sign must be succeeded by a space.";
		expected.add(new String[]{"23", "26", error6, rule6});
		expected.add(new String[]{"62", "65", error6, rule6});
		expected.add(new String[]{"97", "100", error6, rule6});
		// LR 7
		String error7 = "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).";
		String rule7 = "See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course.";
		expected.add(new String[]{"36", "47", error7, rule7});
		expected.add(new String[]{"75", "86", error7, rule7});
		expected.add(new String[]{"113", "124", error7, rule7});
		// LR 8
		String error8 = "INVALID ENCODING ERROR -- This vertical sonority is not encoded in the correct sequence."; 
		String rule8 = "See LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence.";
		expected.add(new String[]{"36", "47", error8, rule8});
		expected.add(new String[]{"75", "86", error8, rule8});
		expected.add(new String[]{"113", "124", error8, rule8});
		// All LR met
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String s : rawEncodings) {
			Encoding e = new Encoding(s, "", Encoding.METADATA_CHECKED);
			actual.add(Encoding.checkLayoutRules(
				e.getCleanEncoding(), e.getTabSymbolSet(), 
				Encoding.alignRawAndCleanEncoding(s, e.getCleanEncoding())));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertArrayEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}
		}
	}


//	@Test
//	public void testGetIndexInRawEncoding() {
//		Encoding encoding = new Encoding();
//		String rawEncoding = ToolBox.readTextFile(encodingTestpiece);
//		encoding.setRawEncoding(rawEncoding);
//		encoding.setCleanEncoding();
//
//		List<Integer> expected = new ArrayList<Integer>();
//		// Section 1 (until footnote 1, including SS following it):
//		// indices 0-35 in cleanEncoding (= 149-183 and 197 in rawEncoding)
//		for (int i = 149; i <= 183; i++) {
//			expected.add(i);
//		}
//		for (int i = 197; i <= 197; i++) {
//			expected.add(i);
//		}
//		// Section 2 (until footnote 2, including SS+space+SS following it):
//		// indices 36-103 in cleanEncoding (= 209-273 and 295-297 in rawEncoding)
//		for (int i = 209; i <= 273; i++) {
//			expected.add(i);
//		}
//		for (int i = 295; i <= 297; i++) {
//			expected.add(i);
//		}
//		// Section 3 (until SBI, including it):
//		// indices 104-131 in cleanEncoding (= 298-325 in rawEncoding)
//		for (int i = 298; i <= 325; i++) {
//			expected.add(i);
//		}
//		// Section 4 (until end): 
//		// indices 132-213 in cleanEncoding (= 337-418 in rawEncoding)
//		for (int i = 337; i <= 418; i++) {
//			expected.add(i);
//		}
//
//		List<Integer> actual = new ArrayList<Integer>();
//		for (int i = 0; i < encoding.getCleanEncoding().length(); i++) {
//			actual.add(encoding.getIndexInRawEncoding(encoding.alignRawAndCleanEncoding(), i));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}


	@Test
	public void testGetTabSymbolSet() {
		assertEquals(TabSymbolSet.FRENCH_TAB, 
			new Encoding(encodingTestpiece).getTabSymbolSet());
	}


	@Test
	public void testCombineSuccessiveRestEvents() {
		List<String> events = Arrays.asList(new String[]{
			"sb.c4.>.", 
			"mi.>.",	
			"mi.c4.>.",
			"mi.>.", "sm.>.", "sm.>.", "mi.>.",
			"mi.c4.>.",	
		});
		
		List<String> expected = Arrays.asList(new String[]{
			"sb.c4.>.", 
			"mi.>.",	
			"mi.c4.>.",
			"sb*.>.",
			"mi.c4.>."
		});
		
		List<String> actual = Encoding.combineSuccessiveRestEvents(events);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testContainsTriplets() {
		Encoding encoding1 = new Encoding(encodingTestpiece);
		Encoding encoding2 = new Encoding(new File(
		"F:/research/data/annotated/josquintab/tab/5254_03_benedicta_es_coelorum_desprez-1.tbp"));
	
		List<Boolean> expected = Arrays.asList(new Boolean[]{false, true});
		
		List<Boolean> actual = Arrays.asList(new Boolean[]{
			encoding1.containsTriplets(), encoding2.containsTriplets()});
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetEventsBarlinesFootnotesPerBar() {
//		Encoding encoding = new Encoding(encodingTestpiece);
		
		Encoding encoding = new Encoding();
		String rawEncoding = ToolBox.readTextFile(encodingTestpiece);
//		try {
//			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece.getAbsolutePath())));
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		// Insert decorative opening barlines at the beginning of each system
		rawEncoding = rawEncoding.replace("McC3.", "|.McC3.");
		rawEncoding = rawEncoding.replace("fu.a6.c4.a2.a1.", "||.fu.a6.c4.a2.a1.");

		encoding.setRawEncoding(rawEncoding);
		encoding.setCleanEncoding();
		encoding.setEventsBarlinesFootnotes();

		List<List<String[]>> expectedNoBarlines = new ArrayList<>();
		List<List<String[]>> expectedBarlines = new ArrayList<>();
		List<String[]> bar1Barline = new ArrayList<>();
		bar1Barline.add(0, new String[]{"|.", "1", null, null});
		expectedBarlines.add(bar1Barline);
		List<String[]> bar1 = new ArrayList<>();
		bar1.add(new String[]{"McC3.", "1", null, null});
		bar1.add(new String[]{"sb.", "1", null, null});
		bar1.add(new String[]{"mi.", "1", null, null});
		bar1.add(new String[]{"mi.a5.c4.b2.a1.", "1", null, null});
		bar1.add(new String[]{"|.", "1", "@Footnote 1", "#1"});
		expectedNoBarlines.add(bar1);
		expectedBarlines.add(bar1);
		//
		List<String[]> bar2 = new ArrayList<>();
		bar2.add(new String[]{"sm*.a6.c4.i2.a1.", "2", null, null});
		bar2.add(new String[]{"fu.d6.", "2", null, null});
		bar2.add(new String[]{"sm.c6.a5.e4.b2.", "2", null, null});
		bar2.add(new String[]{"a6.", "2", null, null});
		bar2.add(new String[]{"mi.a6.h5.c4.b3.f2.", "2", "@'mi.a6.' in source", "#2"});
		bar2.add(new String[]{"sm.a6.b3.a2.a1.", "2", null, null});
		bar2.add(new String[]{"a3.e2.", "2", null, null});
		bar2.add(new String[]{"|.", "2", null, null});
		expectedNoBarlines.add(bar2);
		expectedBarlines.add(bar2);
		//
		List<String[]> bar3Barline = new ArrayList<>();
		bar3Barline.add(0, new String[]{"||.", "3", null, null});
		expectedBarlines.add(bar3Barline);
		List<String[]> bar3 = new ArrayList<>();
		bar3.add(new String[]{"fu.a6.c4.a2.a1.", "3", null, null});
		bar3.add(new String[]{"e2.", "3", null, null});
		bar3.add(new String[]{"sf.a1.", "3", null, null});
		bar3.add(new String[]{"e2.", "3", null, null});
		bar3.add(new String[]{"|.", "3", null, null});
		expectedNoBarlines.add(bar3);
		expectedBarlines.add(bar3);
		//
		List<String[]> bar4 = new ArrayList<>();
		bar4.add(new String[]{"c2.", "4", null, null});
		bar4.add(new String[]{"e2.", "4", null, null});
		bar4.add(new String[]{"mi.a1.", "4", null, null});
		bar4.add(new String[]{"mi.", "4", null, null});
		bar4.add(new String[]{"mi.a6.c4.a2.a1.", "4", null, null});
		bar4.add(new String[]{"||.", "4", null, null});
		expectedNoBarlines.add(bar4);
		expectedBarlines.add(bar4);
		
		List<List<String[]>> actualNoBarlines = encoding.getEventsBarlinesFootnotesPerBar(true);
		List<List<String[]>> actualBarlines = encoding.getEventsBarlinesFootnotesPerBar(false);

		for (int i = 0; i < 2; i++) {
			List<List<String[]>> actual, expected;
			if (i == 0) {
				actual = actualNoBarlines;
				expected = expectedNoBarlines;
			}
			else {
				actual = actualBarlines;
				expected = expectedBarlines;
			}
			
			assertEquals(expected.size(), actual.size());
			for (int j = 0; j < expected.size(); j++) {			
				assertEquals(expected.get(j).size(), actual.get(j).size());
				for (int k = 0; k < expected.get(j).size(); k++) {
					assertEquals(expected.get(j).get(k).length, actual.get(j).get(k).length);
					for (int l = 0; l < expected.get(j).get(k).length; l++) {
						assertEquals(expected.get(j).get(k)[l], actual.get(j).get(k)[l]);
					}
				}
			}
		}
	}


	@Test
	public void testGetEvents() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<String> expected = Arrays.asList(new String[]{
			"McC3.>.", 
			"sb.>.", 
			"mi.>.", 
			"mi.a5.c4.b2.a1.>.", 
			"|.",	
			"sm*.a6.c4.i2.a1.>.", 
			"fu.d6.>.", 
			"sm.c6.a5.e4.b2.>.", 
			"sm.a6.>.", 
			"mi.a6.h5.c4.b3.f2.>.", 
			"sm.a6.b3.a2.a1.>.", 
			"sm.a3.e2.>.", 
			"|.", 
			"/",
			"fu.a6.c4.a2.a1.>.", 
			"fu.e2.>.", 
			"sf.a1.>.", 
			"sf.e2.>.", 
			"|.", 
			"sf.c2.>.", 
			"sf.e2.>.", 
			"mi.a1.>.", 
			"mi.>.", 
			"mi.a6.c4.a2.a1.>.", 
			"||."	
		});

		List<String> actual = encoding.getEvents();
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testIsRhythmSymbolEvent() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<Boolean> expected = Arrays.asList(new Boolean[]{
			false, true, true, true, 
			false,
			true, true, true, false, true, true, false, 
			false, 
			true, false, true, false,
			false,
			false, false, true, true, true,
			false
		});

		List<Boolean> actual = new ArrayList<>();
		encoding.getListsOfSymbols().get(Encoding.ALL_EVENTS_IND).forEach(e -> 
			actual.add(Encoding.isRhythmSymbolEvent(e)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testIsTabSymbolEvent() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<Boolean> expected = Arrays.asList(new Boolean[]{
			false, false, false, true, 
			false,
			true, true, true, true, true, true, true, 
			false, 
			true, true, true, true,
			false,
			true, true, true, false, true,
			false
		});

		List<Boolean> actual = new ArrayList<>();
		encoding.getListsOfSymbols().get(Encoding.ALL_EVENTS_IND).forEach(e -> 
			actual.add(Encoding.isTabSymbolEvent(e, encoding.getTabSymbolSet())));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testReverseEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece);
		Tablature tab = new Tablature(encodingTestpiece, true);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{METER_INFO:2/2 (1-3)}" + "\r\n" +
			"{DIMINUTION:1}" + "\r\n" +
			"\r\n" +
			"||." + "\r\n" +
			"mi.a6.c4.a2.a1.>." + "mi.>." + "mi.a1.>." + "sf.e2.>." + "sf.c2.>." +  
			"|." + "\r\n" +
			"sf.e2.>." + "sf.a1.>." + "fu.e2.>." + "fu.a6.c4.a2.a1.>." +
			"/" + "\r\n" +
			"|." + "\r\n" +
			"sm.a3.e2.>." + "sm.a6.b3.a2.a1.>." + "mi.a6.h5.c4.b3.f2.>." + "sm.a6.>." + 
				"sm.c6.a5.e4.b2.>." + "fu.d6.>." + "sm*.a6.c4.i2.a1.>." +
			"|." + "\r\n" +
			"mi.a5.c4.b2.a1.>." + "mi.>." + "sb.>." + "McC3.>." +
			"//";

		String actual = 
			encoding.reverseEncoding(tab.getTimeline().getMeterInfo()).getRawEncoding();

		assertEquals(expected, actual);		
	}


	@Test
	public void testDeornamentEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{METER_INFO:2/2 (1-3)}" + "\r\n" +
			"{DIMINUTION:1}" + "\r\n" +
			"\r\n" +
			
			"McC3.>." + "sb.>." + "mi.>." + "mi.a5.c4.b2.a1.>." + 
			"|." + "\r\n" +
			"mi.a6.c4.i2.a1.>." + 
			"sm.c6.a5.e4.b2.>." +
			"sm.a6.>." +			
			"mi.a6.h5.c4.b3.f2.>." +
			"sm.a6.b3.a2.a1.>." +
			"sm.a3.e2.>." +		
			"|." + "\r\n" +
			"/" + "\r\n" +
			"mi.a6.c4.a2.a1.>." +
			"|." + "\r\n" +
			"mi.a1.>." +
			"mi.>." +
			"mi.a6.c4.a2.a1.>." +
			"||." + "\r\n" +
			"//";

		String actual = encoding.deornamentEncoding(12).getRawEncoding();

		assertEquals(expected, actual);		
	}


	@Test
	public void testStretchEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece);
		Tablature tab = new Tablature(encodingTestpiece, true);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{METER_INFO:2/2 (1-6)}" + "\r\n" +
			"{DIMINUTION:1}" + "\r\n" +
			"\r\n" +
			
			"McC3.>.br.>.sb.>.sb.a5.c4.b2.a1.>.|." + "\r\n" +
			"mi*.a6.c4.i2.a1.>.sm.d6.>.mi.c6.a5.e4.b2.>.mi.a6.>.sb.a6.h5.c4.b3.f2.>.mi.a6.b3.a2.a1.>.mi.a3.e2.>.|." + 
			"\r\n" + "/" + "\r\n" +
			"sm.a6.c4.a2.a1.>.sm.e2.>.fu.a1.>.fu.e2.>.|." + "\r\n" + 
			"fu.c2.>.fu.e2.>.sb.a1.>.sb.>.sb.a6.c4.a2.a1.>.||." + "\r\n" + 
			"//";

		String actual = 
			encoding.stretchEncoding(tab.getTimeline().getMeterInfo(), 2).getRawEncoding();

		assertEquals(expected, actual);		
	}


	@Test
	public void testSplitHeaderAndEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece);

		String header = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" +
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{METER_INFO:2/2 (1-3)}" + "\r\n" +
			"{DIMINUTION:1}";
		String enc =
			"McC3.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|." +  
			"sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2.>.sm.a6.b3.a2.a1.>.a3.e2.>.|./" + 
			"fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|.c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||.";

		String[] expected = new String[]{header, enc};
		
		String[] actual = encoding.splitHeaderAndEncoding();
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	@Test
	public void testRecombineEvents() {
		Encoding encoding = new Encoding(encodingTestpiece);

		String expected = 
			"McC3.>." + "sb.>." + "mi.>." + "mi.a5.c4.b2.a1.>." + 
			"|." + "\r\n" +	
			"sm*.a6.c4.i2.a1.>." + "fu.d6.>." + "sm.c6.a5.e4.b2.>." + "sm.a6.>." + 
			"mi.a6.h5.c4.b3.f2.>." + "sm.a6.b3.a2.a1.>." + "sm.a3.e2.>." + 
			"|." + "\r\n" + 
			"/" + "\r\n" +
			"fu.a6.c4.a2.a1.>." + "fu.e2.>." + "sf.a1.>." + "sf.e2.>." + 
			"|." + "\r\n" + 
			"sf.c2.>." + "sf.e2.>." + "mi.a1.>." + "mi.>." + "mi.a6.c4.a2.a1.>." + 
			"||." + "\r\n";	

		assertEquals(expected, encoding.recombineEvents(encoding.getEvents()));
	}


	@Test
	public void testEventIsBarlineOrSBI() {
		Encoding encoding = new Encoding(encodingTestpiece);
		
		List<Boolean> expected = Arrays.asList(new Boolean[]{
			false, false, false, false, true, 
			false, false, false, false, false, false, false, true, 
			true,
			false, false, false, false, true, 
			false, false, false, false, false, true 
		});

		List<Boolean> actual = new ArrayList<>();
		for (String s : encoding.getEvents()) {
			actual.add(Encoding.eventIsBarlineOrSBI(s));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testGetStaffSegmentIndices() {
		Encoding encoding = new Encoding(encodingTestpiece);
		
		List<List<Integer>> expected = new ArrayList<>();
		// Footnotes
		// System 1
		expected.add(Arrays.asList(new Integer[]{8, 17}));
		// System 2
		expected.add(new ArrayList<>());
		// Barlines
		// System 1
		expected.add(Arrays.asList(new Integer[]{8, 23}));
		// System 2
		expected.add(Arrays.asList(new Integer[]{8, 19}));
		
		List<List<Integer>> actual = encoding.getStaffSegmentIndices("footnote");
		actual.addAll(encoding.getStaffSegmentIndices("barline"));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	@Test
	public void testGetStaffLength() {
		Encoding encoding = new Encoding(encodingTestpiece);
		int expected = 24;
		int actual = encoding.getStaffLength();
		assertEquals(expected, actual);
	}


	@Test
	public void testSystemBarNumber() {
		Encoding encoding1 = new Encoding(encodingTestpiece);
		Encoding encoding2 = new Encoding(new File("F:/research/data/annotated/encodings/tab-int/3vv/newsidler-1536_7-disant_adiu.tbp"));

		List<List<Integer>> expected = new ArrayList<>();
		// encoding1
		expected.add(Arrays.asList(new Integer[]{1, 2}));
		expected.add(Arrays.asList(new Integer[]{3, 4}));
		// encoding2
		expected.add(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6}));
		expected.add(Arrays.asList(new Integer[]{7, 8, 9, 10, 11}));
		expected.add(Arrays.asList(new Integer[]{12, 13, 14, 15, 16}));
		expected.add(Arrays.asList(new Integer[]{17, 18, 19, 20, 21}));
		expected.add(Arrays.asList(new Integer[]{22, 23, 24, 25, 26, 27, 28}));
		expected.add(Arrays.asList(new Integer[]{28, 29, 30, 31, 32, 33}));
		
		List<List<Integer>> actual = encoding1.getSystemBarNumbers();
		actual.addAll(encoding2.getSystemBarNumbers());
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	@Test
	public void testGetMetadataHead() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<String> expected = 
			Arrays.asList(new String[]{"Author", "Title", "Source (year)"});
		List<String> actual = encoding.getMetadataHead();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetFootnotes() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<String[]> expected = new ArrayList<>();
		expected.add(new String[]{"|.", "1", "@Footnote 1", "#1"});
		expected.add(new String[]{"mi.a6.h5.c4.b3.f2.", "2", "@'mi.a6.' in source", "#2"});

		List<String[]> actual = encoding.getFootnotes();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}

}
