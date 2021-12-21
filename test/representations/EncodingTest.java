package representations;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import representations.Encoding.TuningBassCourses;
import tbp.TabSymbolSet;


public class EncodingTest {

	private File encodingTestpiece1 = new File("F:/research/data/annotated/encodings/test/testpiece.tbp");

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testSetAndGetName() {		
		assertEquals("testpiece", new Encoding(encodingTestpiece1).getName());
	}


	@Test
	public void testSetAndGetRawEncoding() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" + 
			"{TITLE:Title}" + "\r\n" + 
			"{SOURCE:Source (year)}" + "\r\n" +	"\r\n" + 
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" + 
			"{TUNING:A}" + "\r\n" + 
			"{TUNING_SEVENTH_COURSE: }" + "\r\n" +
			"{METER_INFO:2/2 (1-3)}" + "\r\n" + 
			"{DIMINUTION:1}" + "\r\n" + "\r\n" +
			"{bar 1}" + "\r\n" +
			"McC3.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|{@Footnote 1}." + "\r\n" +
			"{bar 2}" + "\r\n" +
			"sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2{@'mi.a6.' in source}.>.sm.a6.b3.a2.a1.>.a3.e2.>.|./" + "\r\n" + 
			"{bar 3}" + "\r\n" +
			"fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|.c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||.//";

		String actual = encoding.getRawEncoding();

		assertEquals(expected, actual);
	}


	@Test
	public void testSetAndGetCleanEncoding() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		encoding.setCleanEncoding();

		String expected = 
			"McC3.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|." + 
		  "sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2.>.sm.a6.b3.a2.a1.>.a3.e2.>.|./" +
			"fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|.c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||.//";

		String actual = encoding.getCleanEncoding();

		assertEquals(expected, actual);
	}


	@Test
	public void testSetAndGetEventsBarlinesFootnotes() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		encoding.setCleanEncoding();
		encoding.setEventsBarlinesFootnotes();
		
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
		
		List<List<String[]>> actual = encoding.getEventsBarlinesFootnotes();
		
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


	@Test
	public void testSetAndGetInfoAndSettings() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		encoding.setInfoAndSettings();

		List<String> expected = 
			Arrays.asList(new String[]{"Author", "Title", "Source (year)", "FrenchTab", "A", "", "2/2 (1-3)", "1"}); 

		List<String> actual = encoding.getInfoAndSettings();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetAllMetadata() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		
		List<String> expected = Arrays.asList(new String[]{
			Encoding.AUTHOR_TAG + " Author ", Encoding.TITLE_TAG + "Title", 
			Encoding.SOURCE_TAG + "Source (year)", Encoding.TABSYMBOLSET_TAG + "FrenchTab", 
			Encoding.TUNING_TAG + "A", Encoding.TUNING_SEVENTH_COURSE_TAG + " ", 
			Encoding.METER_INFO_TAG + "2/2 (1-3)", Encoding.DIMINUTION_TAG + "1",	
//			"AUTHOR: Author ", "TITLE:Title", "SOURCE:Source (year)",
//			"TABSYMBOLSET:FrenchTab", "TUNING:A", "TUNING_SEVENTH_COURSE: ", 
//			"METER_INFO:2/2 (1-3)", "DIMINUTION:1",
			"bar 1", "@Footnote 1", "bar 2", "@'mi.a6.' in source", "bar 3"});

		List<String> actual = encoding.getAllMetadata();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testSetAndGetTunings() {
		Encoding encoding = new Encoding(encodingTestpiece1);

		Encoding.Tuning[] expected = new Encoding.Tuning[]{Encoding.Tuning.A, Encoding.Tuning.A};

		Encoding.Tuning[] actual = encoding.getTunings();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	@Test
	public void testSetAndGetListsOfSymbols() {
		Encoding encoding = new Encoding(encodingTestpiece1);

		List<List<String>> expected = new ArrayList<List<String>>();
		// listOfAllSymbols
		expected.add(Arrays.asList(new String[]{
			"McC3", ">", "sb", ">", "mi", ">", "mi", "a5", "c4", "b2", "a1",">", "|", "sm*", 
			"a6", "c4", "i2", "a1", ">", "fu", "d6", ">", "sm", "c6", "a5",	"e4", "b2", ">", 
			"a6", ">", "mi", "a6", "h5", "c4",	"b3", "f2", ">", "sm", "a6", "b3", "a2", "a1",
			">", "a3", "e2", ">", "|", "fu", "a6", "c4", "a2", "a1", ">", "e2", ">", "sf", 
			"a1", ">", "e2", ">", "|", "c2",	">", "e2", ">", "mi", "a1", ">", "mi", ">", 
			"mi", "a6", "c4", "a2", "a1", ">", "||"
		}));
		// listOfTabSymbols
		expected.add(Arrays.asList(new String[]{"a5", "c4", "b2", "a1", "a6", "c4", "i2", "a1",
			"d6", "c6", "a5", "e4", "b2", "a6", "a6", "h5", "c4", "b3", "f2", "a6", "b3", "a2",
			"a1", "a3", "e2",	"a6", "c4", "a2", "a1", "e2", "a1", "e2", "c2", "e2", "a1", 
			"a6", "c4", "a2", "a1"
		}));
		// listOfRhythmSymbols
		expected.add(Arrays.asList(new String[]{"sb", "mi", "mi",	"sm*", "fu", "sm", "mi", "sm", "fu", "sf", "mi", 
			"mi", "mi"}));
		// listOfMensurationSigns
		expected.add(Arrays.asList(new String[]{"McC3"}));
		// listOfBarlines
		expected.add(Arrays.asList(new String[]{"|", "|", "|", "||"}));
		// listOfAllEvents
		expected.add(Arrays.asList(new String[]{
			"McC3", "sb", "mi", "mi.a5.c4.b2.a1", "|", "sm*.a6.c4.i2.a1", "fu.d6",
			"sm.c6.a5.e4.b2", "a6", "mi.a6.h5.c4.b3.f2", "sm.a6.b3.a2.a1", "a3.e2", "|", 
			"fu.a6.c4.a2.a1", "e2", "sf.a1", "e2", "|", "c2", "e2", "mi.a1", "mi", 
			"mi.a6.c4.a2.a1", "||"
		}));

		List<List<String>> actual = encoding.getListsOfSymbols();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	@Test
	public void testSetAndGetListsOfStatistics() {
		Encoding encoding = new Encoding(encodingTestpiece1);

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
		// durationOfEvents
//		expected.add(Arrays.asList(new Integer[]{
//			0, 16, 8, 8, 0, 6, 2, 4, 4, 8, 4, 4, 0, 2, 2, 1, 1, 0, 1, 1, 8, 8, 8, 0}));
		expected.add(Arrays.asList(new Integer[]{
			0, 48, 24, 24, 0, 18, 6, 12, 12, 24, 12, 12, 0, 6, 6, 3, 3, 0, 3, 3, 24, 24, 24, 0}));
		// horizontalPositionOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			3, 3, 3, 3, 5, 5, 5, 5, 6, 7, 7, 7, 7, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10, 11, 
			11, 13, 13, 13, 13, 14, 15, 16, 18, 19, 20, 22, 22, 22, 22}));
		// verticalPositionOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 2, 3, 0, 1, 2, 3, 0, 0, 1, 2, 3, 0, 0, 1, 2, 3, 4, 0, 1, 2, 3, 0, 1, 0, 1,
			2, 3, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3}));
		// durationOfTabSymbols
//		expected.add(Arrays.asList(new Integer[]{
//			8, 8, 8, 8, 6, 6, 6, 6, 2, 4, 4, 4, 4, 4, 8, 8, 8, 8, 8, 4, 4, 4, 4, 4, 4, 2, 2,
//			2, 2, 2, 1, 1, 1, 1, 8, 8, 8, 8, 8}));
		expected.add(Arrays.asList(new Integer[]{
			24, 24, 24, 24, 18, 18, 18, 18, 6, 12, 12, 12, 12, 12, 24, 24, 24, 24, 24, 12, 12, 
			12, 12, 12, 12, 6, 6, 6, 6, 6, 3, 3, 3, 3, 24, 24, 24, 24, 24}));
		// gridXOfTabSymbols
//		expected.add(Arrays.asList(new Integer[]{
//			24, 24, 24, 24, 32, 32, 32, 32, 38, 40, 40, 40, 40, 44, 48, 48, 48, 48, 48, 56,
//			56, 56, 56, 60, 60, 64, 64, 64, 64, 66, 68, 69, 70, 71, 72, 88, 88,	88, 88}));
		expected.add(Arrays.asList(new Integer[]{
			72, 72, 72, 72, 96, 96, 96, 96, 114, 120, 120, 120, 120, 132, 144, 144, 144, 144, 
			144, 168, 168, 168, 168, 180, 180, 192, 192, 192, 192, 198, 204, 207, 210, 213, 216, 
			264, 264, 264, 264}));
		// gridYOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			50, 57, 65, 69, 45, 57, 72, 69, 48, 47, 50, 59, 65, 45, 45, 57, 57, 60, 69, 45,
			60, 64, 69, 59, 68, 45, 57, 64, 69, 68, 69, 68, 66, 68, 69, 45, 57, 64, 69}));
		// horizontalPositionInTabSymbolEventsOnly
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 0, 1, 1, 1, 1, 2, 3, 3, 3, 3, 4, 5, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 8, 8,
			8, 8, 9, 10, 11, 12, 13, 14, 15, 15, 15, 15}));

		List<List<Integer>> actual = encoding.getListsOfStatistics();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	@Test
	public void testGetTuningBassCourses() {
		Encoding encoding1 = new Encoding(encodingTestpiece1);
		Encoding encoding2 = new Encoding(new File("F:/research/data/annotated/encodings/tab-int/5vv/adriansen-1584_6-d_vn_si.tbp"));

		List<TuningBassCourses> expected = 
			Arrays.asList(new TuningBassCourses[]{null, TuningBassCourses.SECOND});

		List<TuningBassCourses> actual = Arrays.asList(new TuningBassCourses[]{
			encoding1.getTuningBassCourses(), encoding2.getTuningBassCourses()});

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testCheckForMetaDataErrors() {
		List<String> rawEncodings = new ArrayList<String>();
		// Missing tag
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}{DIMINUTION:d}\nencoding");
		// Wrong sequence of tags
		rawEncodings.add("{AUTHOR:a}{METER_INFO:m}{SOURCE:s}{TABSYMBOLSET:t}{TUNING_SEVENTH_COURSE:t}{TUNING:t}{TITLE:t}\nencoding");
		// OPEN_ or CLOSE_INFO_BRACKET errors
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\nencoding"); // CLOSE_INFO_BRACKET missing in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\nencoding"); // OPEN_INFO_BRACKET missing in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\nencoding"); // Double CLOSE_INFO_BRACKET in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\nencoding"); // Double OPEN_INFO_BRACKET in tags
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m\nencoding"); // CLOSE_INFO_BRACKET missing after last tag
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\ne{n}c{od{i}n{g}"); // CLOSE_INFO_BRACKET missing in remainder 
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\ne{n}c{o}di}n{g}"); // OPEN_INFO_BRACKET missing in remainder 
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}\ne{n}c{o}d{i}n{g"); // CLOSE_INFO_BRACKET missing at end
		// Correct encoding
		rawEncodings.add("{AUTHOR:a}{TITLE:t}{SOURCE:s}{TABSYMBOLSET:t}{TUNING:t}{TUNING_SEVENTH_COURSE:t}{METER_INFO:m}{DIMINUTION:d}\ne{n}c{o}d{i}n{g}");

		List<Boolean> expected = Arrays.asList(new Boolean[]{true, true, true, true, true, true, true, true, true, true, false}); 

		List<Boolean> actual = new ArrayList<Boolean>();
		for (int i = 0; i < rawEncodings.size(); i++) {
			Encoding encoding = new Encoding();
			encoding.setRawEncoding(rawEncodings.get(i));
			actual.add(encoding.checkForMetadataErrors());
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testCheckForEncodingErrors() {
		List<String> encodings = new ArrayList<String>();
		// VR 3 not met (first system starts with SBI)
		encodings.add("{}{}{}{}{}{}{}{}/MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// Unknown symbol (in first system)
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.bs1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// LR 7 not met (invalid chord in first system)
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a2.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// Correct encoding
		String corrEncoding = "";
		try {
			corrEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encodings.add(corrEncoding);

		List<String[]> expected = new ArrayList<String[]>(); 
		expected.add(new String[]{"16", "17", "INVALID ENCODING ERROR -- Remove this system break indicator.",
			"See VALIDITY RULE 3: A system cannot start with a punctuation symbol."});
		expected.add(new String[]{"52", "55", "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.",
			"See VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator."});
		expected.add(new String[]{"38", "49", "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).", 
			"See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course."});
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (int i = 0; i < encodings.size(); i++) {
			Encoding encoding = new Encoding(); 
			encoding.setRawEncoding(encodings.get(i));
			encoding.setCleanEncoding();
			encoding.setInfoAndSettings();
			actual.add(encoding.checkForEncodingErrors()); 	
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
	public void testAlignRawAndCleanEncoding() {
		Encoding encoding = new Encoding(); 
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		encoding.setCleanEncoding();
		boolean print = false;
		if (print) {
			System.out.println(encoding.getRawEncoding().indexOf("McC3.")); // 176
			System.out.println(encoding.getRawEncoding().indexOf("sm*.a6.c4.i2.a1.")); // 236
			System.out.println(encoding.getRawEncoding().indexOf("sm.a6.b3.a2.a1")); // 325
			System.out.println(encoding.getRawEncoding().indexOf("fu.a6.c4.a2.a1.")); // 364
		}
		Integer[] expected = new Integer[encoding.getRawEncoding().length()];
		Arrays.fill(expected, -1);
		// Section 1 ("McC3." up until "." after "{@Footnote 1}"): 36 chars (in 
		// rawEncoding split up by footnote into 35 + 1)
		// indices 0-35 in cleanEncoding (= 176-210 and 224 in rawEncoding)
		for (int i = 0; i < 35; i++) {
			expected[176 + i] = 0 + i;
		}
		for (int i = 0; i < 1; i++) {
			expected[224 + i] = 0 + 35 + i;
		}
		// Section 2 ("sm*.a6.c4.i2.a1." up until "sm.a6.b3.a2.a1"): 68 chars (in 
		// rawEncoding split up by footnote into 65 + 3)
		// indices 36-103 in cleanEncoding (= 236-300 and 322-324 in rawEncoding)
		for (int i = 0; i < 65; i++) {
			expected[236 + i] = 0 + 35 + 1 + i;
		}
		for (int i = 0; i < 3; i++) {
			expected[322 + i] = 0 + 35 + 1 + 65 + i;
		}		
		// Section 3 ("sm.a6.b3.a2.a1" up until and including SBI): 28 chars
		// indices 104-131 in cleanEncoding (= 325-352 in rawEncoding)
		for (int i = 0; i < 28; i++) {
			expected[325 + i] = 0 + 35 + 1 + 65 + 3 + i;
		}		
		// Section 4 ("fu.a6.c4.a2.a1." up until and including EBI): 82 chars
		// indices 132-213 in cleanEncoding (= 364-445 in rawEncoding)
		for (int i = 0; i < 82; i++) {
			expected[364 + i] = 0 + 35 + 1 + 65 + 3 + 28 + i;
		}

		Integer[] actual = encoding.alignRawAndCleanEncoding();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	@Test
	public void testCheckValidityRules() {
		List<String> encodings = new ArrayList<String>();
		// VR 1
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.m  i.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// VR 2
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.");
		// VR 3
		encodings.add("{}{}{}{}{}{}{}/MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|.//MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>.//MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}.MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./.MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./.MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// VR 4
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|/MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>/MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||//");
		// All VR met
		encodings.add("{}{}{}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");

		List<String[]> expected = new ArrayList<String[]>();
		// VR 1
		expected.add(new String[]{"60", "61", "INVALID ENCODING ERROR -- Remove this whitespace.", 
			"See VALIDITY RULE 1: The encoding cannot contain whitespace."});
		// VR 2
		expected.add(new String[]{"-1", "-1", "INVALID ENCODING ERROR -- The encoding does not end with an end break indicator.",	
			"See VALIDITY RULE 2: The encoding must end with an end break indicator."});
		// VR 3
		String error3a = "INVALID ENCODING ERROR -- Remove this system break indicator.";
		String error3b = "INVALID ENCODING ERROR -- Remove this symbol separator.";
		String rule3 = "See VALIDITY RULE 3: A system cannot start with a punctuation symbol.";
		expected.add(new String[]{"14", "15", error3a, rule3});
		expected.add(new String[]{"49", "50", error3a, rule3});
		expected.add(new String[]{"88", "89", error3a, rule3});
		expected.add(new String[]{"14", "15", error3b, rule3});
		expected.add(new String[]{"49", "50", error3b, rule3});
		expected.add(new String[]{"88", "89", error3b, rule3});
		// VR 4
		String error4a = "INVALID ENCODING ERROR -- Insert a symbol separator before this system break indicator.";
		String error4b = "INVALID ENCODING ERROR -- Insert a symbol separator before this end break indicator.";
		String rule4 = "See VALIDITY RULE 4: Each system must end with a symbol separator."; 		
		expected.add(new String[]{"47", "48", error4a, rule4});
		expected.add(new String[]{"86", "87", error4a, rule4});
		expected.add(new String[]{"125", "127", error4b, rule4});
		// All VR met
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String e : encodings) {
			Encoding encoding = new Encoding();
			encoding.setRawEncoding(e);
			encoding.setCleanEncoding();
			actual.add(encoding.checkValidityRules(encoding.alignRawAndCleanEncoding()));
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
		List<String> encodings = new ArrayList<String>();
		// Missing symbol
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>..>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|..>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>..>.||.//");
		// Unknown symbol
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.bs1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.bs2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.bs3.>.||.//");
		// No missing or unknown symbols
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");

		List<String[]> expected = new ArrayList<String[]>();
		// Missing symbol
		String errorMissing = "MISSING SYMBOL ERROR -- Remove symbol separator or insert symbol before.";
		String rule5 = "See VALIDITY RULE 5: Each musical symbol must be succeeded directly by a symbol separator.";
		expected.add(new String[]{"52", "53", errorMissing, rule5});
		expected.add(new String[]{"93", "94", errorMissing, rule5});
		expected.add(new String[]{"129", "130", errorMissing, rule5});
		// Unknown symbol
		String errorUnknown = "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.";
		expected.add(new String[]{"52", "55", errorUnknown, rule5});
		expected.add(new String[]{"93", "96", errorUnknown, rule5});
		expected.add(new String[]{"129", "132", errorUnknown, rule5});
		// No missing or unknown symbols
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String e : encodings) {
			Encoding encoding = new Encoding();
			encoding.setRawEncoding(e);
			encoding.setCleanEncoding();
			encoding.setInfoAndSettings();
			actual.add(encoding.checkSymbols(encoding.alignRawAndCleanEncoding()));
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
		List<String> encodings = new ArrayList<String>();
		// LR 1
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}>.MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./>.MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./>.MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		// LR 2
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.//"); 
		// LR 3
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|.>./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.>.//"); 		
		// LR 4
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.||.//"); 
		// LR 5
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.mi*.>.mi.b5.b4.b3.>.b3.>.||.//"); 
		// LR 6
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		// LR 7
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a2.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a3.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b4.>.b3.>.||.//");
		// LR 8
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a1.a2.a3.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a2.a3.a4.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b3.b4.b5.>.b3.>.||.//");
		// All LR met
		encodings.add("{}{}{}{FrenchTab}{}{}{}{}MC3.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO4.M33.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./MC3.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//");

		List<String[]> expected = new ArrayList<String[]>(); 
		// LR 1
		String error1 = "INVALID ENCODING ERROR -- Remove this space."; 
		String rule1 = "See LAYOUT RULE 1: A system can start with any event but a space.";
		expected.add(new String[]{"25", "26", error1, rule1});
		expected.add(new String[]{"60", "61", error1, rule1});
		expected.add(new String[]{"99", "100", error1, rule1});
		// LR 2
		String error2 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule2 = "See LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline.";
		expected.add(new String[]{"52", "54", error2, rule2});
		expected.add(new String[]{"93", "95", error2, rule2});
		expected.add(new String[]{"129", "131", error2, rule2});
		// LR 3
		String error3 = "INVALID ENCODING ERROR -- Remove this space.";
		String rule3 = "See LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space.";
		expected.add(new String[]{"59", "60", error3, rule3});
		expected.add(new String[]{"98", "99", error3, rule3});
		expected.add(new String[]{"137", "138", error3, rule3});
		// LR 4
		String error4 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule4 = "See LAYOUT RULE 4: A vertical sonority must be succeeded by a space."; 
		expected.add(new String[]{"52", "54", error4, rule4});
		expected.add(new String[]{"86", "88", error4, rule4});
		expected.add(new String[]{"129", "131", error4, rule4});
		// LR 5
		String error5 = "INVALID ENCODING ERROR -- Insert a space after this RhythmSymbol.";
		String rule5 = "See LAYOUT RULE 5: A rest (or rhythm dot at the beginning of a system or bar) must be succeeded by a space."; 
		expected.add(new String[]{"31", "33", error5, rule5});
		expected.add(new String[]{"70", "72", error5, rule5});
		expected.add(new String[]{"105", "106", error5, rule5});
		// LR 6
		String error6 = "INVALID ENCODING ERROR -- Insert a space after this MensurationSign.";
		String rule6 = "See LAYOUT RULE 6: A mensuration sign must be succeeded by a space.";
		expected.add(new String[]{"25", "28", error6, rule6});
		expected.add(new String[]{"64", "67", error6, rule6});
		expected.add(new String[]{"99", "102", error6, rule6});
		// LR 7
		String error7 = "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).";
		String rule7 = "See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course.";
		expected.add(new String[]{"38", "49", error7, rule7});
		expected.add(new String[]{"77", "88", error7, rule7});
		expected.add(new String[]{"115", "126", error7, rule7});
		// LR 8
		String error8 = "INVALID ENCODING ERROR -- This vertical sonority is not encoded in the correct sequence."; 
		String rule8 = "See LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence.";
		expected.add(new String[]{"38", "49", error8, rule8});
		expected.add(new String[]{"77", "88", error8, rule8});
		expected.add(new String[]{"115", "126", error8, rule8});
		// All LR met
		expected.add(null);

		List<String[]> actual = new ArrayList<String[]>();
		for (String e : encodings) {
			Encoding encoding = new Encoding();
			encoding.setRawEncoding(e);
			encoding.setCleanEncoding();
			encoding.setInfoAndSettings();
			actual.add(encoding.checkLayoutRules(encoding.alignRawAndCleanEncoding()));
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
	public void testGetIndexInRawEncoding() {
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		encoding.setRawEncoding(rawEncoding);
		encoding.setCleanEncoding();
		
		List<Integer> expected = new ArrayList<Integer>();
		// Section 1 (until footnote 1, including SS following it):
		// indices 0-35 in cleanEncoding (= 176-210 and 224 in rawEncoding)
		for (int i = 176; i <= 210; i++) {
			expected.add(i);
		}
		for (int i = 224; i <= 224; i++) {
			expected.add(i);
		}
		// Section 2 (until footnote 2, including SS+space+SS following it): 
		// indices 36-103 in cleanEncoding (= 236-300 and 322-324 in rawEncoding)
		for (int i = 236; i <= 300; i++) {
			expected.add(i);
		}
		for (int i = 322; i <= 324; i++) {
			expected.add(i);
		}
		// Section 3 (until SBI, including it):
		// indices 104-131 in cleanEncoding (= 325-352 in rawEncoding)
		for (int i = 325; i <= 352; i++) {
			expected.add(i);
		}
		// Section 4 (until end): 
		// indices 132-213 in cleanEncoding (= 364-445 in rawEncoding)
		for (int i = 364; i <= 445; i++) {
			expected.add(i);
		}

		List<Integer> actual = new ArrayList<Integer>();
		for (int i = 0; i < encoding.getCleanEncoding().length(); i++) {
			actual.add(encoding.getIndexInRawEncoding(encoding.alignRawAndCleanEncoding(), i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetTabSymbolSet() {
		assertEquals(TabSymbolSet.FRENCH_TAB, 
			new Encoding(encodingTestpiece1).getTabSymbolSet());
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
		Encoding encoding1 = new Encoding(encodingTestpiece1);
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
//		Encoding encoding = new Encoding(encodingTestpiece1);
		
		Encoding encoding = new Encoding();
		String rawEncoding = "";
		try {
			rawEncoding = new String(Files.readAllBytes(Paths.get(encodingTestpiece1.getAbsolutePath())));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
		Encoding encoding = new Encoding(encodingTestpiece1);

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
	public void testReverseEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece1);
		Tablature tab = new Tablature(encodingTestpiece1, true);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{TUNING_SEVENTH_COURSE: }" + "\r\n" +
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

		String actual = encoding.reverseEncoding(tab.getMeterInfo()).getRawEncoding();

		assertEquals(expected, actual);		
	}


	@Test
	public void testDeornamentEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece1);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{TUNING_SEVENTH_COURSE: }" + "\r\n" +
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
		Encoding encoding = new Encoding(encodingTestpiece1);
		Tablature tab = new Tablature(encodingTestpiece1, true);

		String expected = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" + 
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{TUNING_SEVENTH_COURSE: }" + "\r\n" +
			"{METER_INFO:2/2 (1-6)}" + "\r\n" +
			"{DIMINUTION:1}" + "\r\n" +
			"\r\n" +
			
			"McC3.>.br.>.sb.>.sb.a5.c4.b2.a1.>.|." + "\r\n" +
			"mi*.a6.c4.i2.a1.>.sm.d6.>.mi.c6.a5.e4.b2.>.mi.a6.>.sb.a6.h5.c4.b3.f2.>.mi.a6.b3.a2.a1.>.mi.a3.e2.>.|." + 
			"\r\n" + "/" + "\r\n" +
			"sm.a6.c4.a2.a1.>.sm.e2.>.fu.a1.>.fu.e2.>.|." + "\r\n" + 
			"fu.c2.>.fu.e2.>.sb.a1.>.sb.>.sb.a6.c4.a2.a1.>.||." + "\r\n" + 
			"//";

		String actual = encoding.stretchEncoding(tab.getMeterInfo(), 2).getRawEncoding();

		assertEquals(expected, actual);		
	}


	@Test
	public void testSplitHeaderAndEncoding() {
		Encoding encoding = new Encoding(encodingTestpiece1);

		String header = 
			"{AUTHOR: Author }" + "\r\n" +
			"{TITLE:Title}" + "\r\n" +
			"{SOURCE:Source (year)}" + "\r\n" +
			"\r\n" +
			"{TABSYMBOLSET:FrenchTab}" + "\r\n" +
			"{TUNING:A}" + "\r\n" +
			"{TUNING_SEVENTH_COURSE: }" + "\r\n" +
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
		Encoding encoding = new Encoding(encodingTestpiece1);

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
		Encoding encoding = new Encoding(encodingTestpiece1);
		
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
		Encoding encoding = new Encoding(encodingTestpiece1);
		
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
		Encoding encoding = new Encoding(encodingTestpiece1);
		int expected = 24;
		int actual = encoding.getStaffLength();
		assertEquals(expected, actual);
	}


	@Test
	public void testSystemBarNumber() {
		Encoding encoding1 = new Encoding(encodingTestpiece1);
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
	public void testGetMetadata() {
		Encoding encoding = new Encoding(encodingTestpiece1);

		List<String> expected = 
			Arrays.asList(new String[]{"Author", "Title", "Source (year)"});
		List<String> actual = encoding.getMetadata();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetFootnotes() {
		Encoding encoding = new Encoding(encodingTestpiece1);

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
