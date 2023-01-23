package tbp;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import path.Path;
import representations.Tablature;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;

public class EncodingTest extends TestCase {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private String miniRawEncoding;


	@Before
	public void setUp() throws Exception {
		super.setUp();
		String root = Path.getRootPath() + Path.getDataDir(); 
		encodingTestpiece = 
			new File(root + Path.getEncodingsPath() + Path.getTestDir() + "testpiece.tbp");
		encodingTestGetMeterInfo = 
			new File(root + Path.getEncodingsPath() + Path.getTestDir() + "test_get_meter_info.tbp");
		miniRawEncoding = "{}{}{}{French}{}{}{}MC\\.>.sb.>.|.mi.a3.a2.a1.>.b1.>.|./MO.M34.>.mi.>.|.mi.a4.a3.a2.>.|.b2.>./|.MC\\.>.*.>.mi*.>.mi.b5.b4.b3.>.b3.>.||.//";
	}


	@After
	public void tearDown() throws Exception {
	}


	private List<Event> getEvents(boolean removeDecorativeBarlines) {
		List<Event> events = new ArrayList<>();
		// Bar 1
		events.add(new Event("MC\\.", 1, 1, null, null));
		events.add(new Event("sb.", 1, 1, null, null));
		events.add(new Event("mi.", 1, 1, null, null));
		events.add(new Event("mi.a5.c4.b2.a1.", 1, 1, null, null));
		events.add(new Event("|.", 1, 1, "@Footnote 1", "#1"));
		// Bar 2
		events.add(new Event("sm*.a6.c4.i2.a1.", 1, 2, null, null));
		events.add(new Event("fu.d6.", 1, 2, null, null));
		events.add(new Event("sm.c6.a5.e4.b2.", 1, 2, null, null));
		events.add(new Event("a6.", 1, 2, null, null));
		events.add(new Event("mi.a6.h5.c4.b3.f2.", 1, 2, "@'mi.a6.' in source", "#2"));
		events.add(new Event("sm.a6.b3.a2.a1.", 1, 2, null, null));
		events.add(new Event("a3.e2.", 1, 2, null, null));
		events.add(new Event("|.", 1, 2, null, null));
		// Bar 3
		if (!removeDecorativeBarlines) {
			events.add(new Event("||.", 2, 3, null, null));
		}
		events.add(new Event("fu.a6.c4.a2.a1.", 2, 3, null, null));
		events.add(new Event("e2.", 2, 3, null, null));
		events.add(new Event("sf.a1.", 2, 3, null, null));
		events.add(new Event("e2.", 2, 3, null, null));
		events.add(new Event("|.", 2, 3, null, null));
		// Bar 4
		events.add(new Event("c2.", 2, 4, null, null));
		events.add(new Event("e2.", 2, 4, null, null));
		events.add(new Event("mi.a1.", 2, 4, null, null));
		events.add(new Event("mi.", 2, 4, null, null));
		events.add(new Event("mi.a6.c4.a2.a1.", 2, 4, null, null));
		events.add(new Event("||.", 2, 4, null, null));

		return events;
	}


	private String getCleanEncoding() {
		return 	
			"MC\\.>.sb.>.mi.>.mi.a5.c4.b2.a1.>.|." +	
			"sm*.a6.c4.i2.a1.>.fu.d6.>.sm.c6.a5.e4.b2.>.a6.>.mi.a6.h5.c4.b3.f2.>." + 
				"sm.a6.b3.a2.a1.>.a3.e2.>.|." + 
			"/" +
			"||.fu.a6.c4.a2.a1.>.e2.>.sf.a1.>.e2.>.|." + 
			"c2.>.e2.>.mi.a1.>.mi.>.mi.a6.c4.a2.a1.>.||." +
			"//";
	}


	private String getHeader(String piece) {
		String header = null;
		if (piece.equals("testpiece")) {
			header =
				"{AUTHOR: Author}" + "\r\n" +
				"{TITLE: Title}" + "\r\n" +
				"{SOURCE: Source (year)}" + "\r\n" + 
				"{TABSYMBOLSET: French}" + "\r\n" +
				"{TUNING: A}" + "\r\n" +
				"{METER_INFO: 2/2 (1-3)}" + "\r\n" +
				"{DIMINUTION: 1}";
		}
		else if (piece.equals("testGetMeterInfo")) {
			header =
				"{AUTHOR: }" + "\r\n" +
				"{TITLE: test_get_meter_info}" + "\r\n" +
				"{SOURCE: }" + "\r\n" + 
				"{TABSYMBOLSET: French}" + "\r\n" +
				"{TUNING: G}" + "\r\n" +
				"{METER_INFO: 3/8 (1); 2/2 (2-3); 3/4 (4-5); 2/2 (6-7); 5/16 (8); 2/2 (9)}" + "\r\n" +
				"{DIMINUTION: 2; 2; 4; 1; 1; -2}";
		}
		return header;
	}


	private List<String> getDecomposedEvents(String piece, boolean complementRs) {
		List<String> l = null;
		if (piece.equals("testpiece")) {
			if (!complementRs) {
				l = Arrays.asList(
					"MC\\.>.", "sb.>.", "mi.>.", "mi.a5.c4.b2.a1.>.", 
					"|.",	
					"sm*.a6.c4.i2.a1.>.", "fu.d6.>.", "sm.c6.a5.e4.b2.>.", "a6.>.", 
						"mi.a6.h5.c4.b3.f2.>.", "sm.a6.b3.a2.a1.>.", "a3.e2.>.",
					"|.", 
					"/",
					"||.",
					"fu.a6.c4.a2.a1.>.", "e2.>.", "sf.a1.>.", "e2.>.", 
					"|.", 
					"c2.>.", "e2.>.", "mi.a1.>.", "mi.>.", "mi.a6.c4.a2.a1.>.", 
					"||.",
					"//"	
				);
			}
			else {
				l = Arrays.asList(
					"MC\\.>.", "sb.>.", "mi.>.", "mi.a5.c4.b2.a1.>.", 
					"|.",	
					"sm*.a6.c4.i2.a1.>.", "fu.d6.>.", "sm.c6.a5.e4.b2.>.", "sm.a6.>.",
						"mi.a6.h5.c4.b3.f2.>.", "sm.a6.b3.a2.a1.>.", "sm.a3.e2.>.",
					"|.", 
					"/",
					"||.",
					"fu.a6.c4.a2.a1.>.", "fu.e2.>.", "sf.a1.>.", "sf.e2.>.", 
					"|.", 
					"sf.c2.>.", "sf.e2.>.", "mi.a1.>.", "mi.>.", "mi.a6.c4.a2.a1.>.", 
					"||.",
					"//"
				);
			}	
		}
		if (piece.equals("testGetMeterInfo")) {
			l = Arrays.asList(
				"M3:8.>.", "sm.a1.>.", "sm.a1.>.", "sm.a1.>.",
				"|.",
				"MC\\.>.", "mi*.a2.a1.>.", "sm.a1.>.", "sb.a2.a1.>.",
				"|.",
				"mi.a2.a1.>.", "fu-.a1.>.", "fu-.a1.>.", "sf-.a1.>.", "sf-.a1.>.", "sf-.a1.>.", 
					"sf.a1.>.", "sb.a2.a1.>.",
				"|.",
				"M3.>.", "mi.a2.a1.>.", "sm-.a1.>.", "fu-.a1.>.", "fu.a1.>.", "mi.a2.a1.>.", 
				"|.",
				"sm*.a1.>.", "sf-.a1.>.", "sf.a1.>.", "sb.a2.a1.>.",
				"|.",
				"MC\\.>.", "sb.a2.a1.>.", "sb.a2.a1.>.", 
				"|.",
				"sm-.a1.>.", "sm-.a1.>.", "sm-.a1.>.", "sm.a1.>.", "sm-.a1.>.", "sm.a1.>.", 
					"mi.a2.a1.>.",
				"|.",
				"sm-.a1.>.", "fu-.a1.>.", "fu-.a1.>.", "fu.a1.>.",
				"|.",
				"MC\\.>.", "sb.a2.a1.>.", "sm-.a1.>.", "fu-.a1.>.", "fu.a1.>.", "mi.a2.a1.>.",
				"||.",
				"//"
			);
			
		}
		return new ArrayList<>(l);
	}


	@Test
	public void testMakeCleanEncoding() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));

		String expected = getCleanEncoding();
		String actual = encoding.makeCleanEncoding();

		assertEquals(expected, actual);
	}


	@Test
	public void testMakeMetadata() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();

		List<String> values = 
			Arrays.asList(new String[]{
				"Author", 
				"Title", 
				"Source (year)", 
				"FrenchTab", 
				"A", 
				"2/2 (1-3)", 
				"1"
		});

		Map<String, String> expected = new LinkedHashMap<String, String>();
		for (int i = 0; i < Encoding.METADATA_TAGS.length; i++) {
			expected.put(Encoding.METADATA_TAGS[i], values.get(i));
		}	
		Map<String, String> actual = encoding.makeMetadata();

		assert(expected.equals(actual));
	}


	@Test
	public void testMakeHeader() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();

		String expected = getHeader("testpiece");
		String actual = encoding.makeHeader();

		assertEquals(expected, actual);
	}


	@Test
	public void testMakeTabSymbolSet() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setHeader();

		TabSymbolSet expected = TabSymbolSet.FRENCH;
		TabSymbolSet actual = encoding.makeTabSymbolSet();

		assertEquals(expected, actual);
	}


	@Test
	public void testMakeEvents() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setHeader();
		encoding.setTabSymbolSet();

		List<Event> expected = getEvents(false);
		List<Event> actual = encoding.makeEvents();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testMakeListOfSymbols() {
		Encoding encoding = new Encoding();
		encoding.setPiecename("testpiece");
		encoding.setRawEncoding(ToolBox.readTextFile(encodingTestpiece));
		encoding.setCleanEncoding();
		encoding.setMetadata();
		encoding.setHeader();
		encoding.setTabSymbolSet();
		encoding.setEvents();

		List<List<String>> expected = new ArrayList<List<String>>();
		// allSymbols
		String cleanEnc = getCleanEncoding();
		cleanEnc = cleanEnc.substring(0, cleanEnc.indexOf(Symbol.END_BREAK_INDICATOR));
		List<String> allSymbols = new ArrayList<>();
		for (String system : cleanEnc.split(Symbol.SYSTEM_BREAK_INDICATOR)) {
			for (String symbol : system.split("\\" + Symbol.SYMBOL_SEPARATOR)) {
				if (!symbol.equals(Symbol.SPACE.getEncoding())) {
					allSymbols.add(symbol);
				}
			}
		}
		expected.add(allSymbols);
		// tabSymbols
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
		// rhythmSymbols
		expected.add(Arrays.asList(new String[]{
			"sb", "mi", "mi", 
			"sm*", "fu", "sm", "mi", "sm", 
			"fu", "sf", "mi", "mi", "mi"
		}));
		// mensurationSigns
		expected.add(Arrays.asList(new String[]{"MC\\"}));
		// barlines
		expected.add(Arrays.asList(new String[]{"|", "|", "||", "|", "||"}));

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
		encoding.setHeader();
		encoding.setTabSymbolSet();
		encoding.setEvents();
		encoding.setListsOfSymbols();

		List<List<Integer>> expected = new ArrayList<List<Integer>>();
		// isTabSymbolEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 1, 0, 
			1, 1, 1, 1, 1, 1, 1, 0, 
			0, 1, 1, 1, 1, 0, 
			1, 1, 1, 0, 1, 0}));
		// isRhythmSymbolEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 1, 1, 0, 
			1, 1, 1, 0, 1, 1, 0, 0, 
			0, 1, 0, 1, 0, 0, 
			0, 0, 1, 1, 1, 0}));
		// isRestEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 1, 1, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 
			0, 0, 0, 1, 0, 0}));
		// isMensurationSignEvent
		expected.add(Arrays.asList(new Integer[]{
			1, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0, 
			0, 0, 0, 0, 0, 0}));
		// isBarlineEvent
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 0, 1, 
			0, 0, 0, 0, 0, 0, 0, 1, 
			1, 0, 0, 0, 0, 1, 
			0, 0, 0, 0, 0, 1}));
		// sizeOfEvents
		expected.add(Arrays.asList(new Integer[]{
			0, 0, 0, 4, 0, 
			4, 1, 4, 1, 5, 4, 2, 0, 
			0, 4, 1, 1, 1, 0, 
			1, 1, 1, 0, 4, 0}));
		// horizontalPositionOfTabSymbols
		expected.add(Arrays.asList(new Integer[]{
			3, 3, 3, 3, 5, 5, 5, 5, 6, 7, 7, 7, 7, 8, 9, 9, 9, 9, 9, 10, 10, 10, 10, 11, 
			11, 14, 14, 14, 14, 15, 16, 17, 19, 20, 21, 23, 23, 23, 23}));
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


	@Test
	public void testAlignRawAndCleanEncoding() {	
		Encoding encoding = new Encoding(new Encoding(encodingTestpiece).getRawEncoding(), 
			"", Encoding.METADATA_CHECKED);

		boolean print = false;
		if (print) {
			System.out.println(encoding.getRawEncoding().indexOf("MC\\.")); // 146
			System.out.println(encoding.getRawEncoding().indexOf("sm*.a6.c4.i2.a1.")); // 205
			System.out.println(encoding.getRawEncoding().indexOf("sm.a6.b3.a2.a1")); // 294
			System.out.println(encoding.getRawEncoding().indexOf("||.fu.a6.c4.a2.a1.")); // 333
		}
		Integer[] expected = new Integer[encoding.getRawEncoding().length()];
		Arrays.fill(expected, -1);
		// Section 1 ("MC\." up until "." after "{@Footnote 1}"): 35 chars (in 
		// rawEncoding split up by footnote into 34 + 1)
		// indices 0-34 in cleanEncoding (= 146-179 and 193 in rawEncoding)
		for (int i = 0; i < 34; i++) {
			expected[146 + i] = 0 + i;
		}
		for (int i = 0; i < 1; i++) {
			expected[193 + i] = 0 + 34 + i;
		}
		// Section 2 ("sm*.a6.c4.i2.a1." up until "sm.a6.b3.a2.a1"): 68 chars (in 
		// rawEncoding split up by footnote into 65 + 3)
		// indices 35-102 in cleanEncoding (= 205-269 and 291-293 in rawEncoding)
		for (int i = 0; i < 65; i++) {
			expected[205 + i] = 0 + 34 + 1 + i;
		}
		for (int i = 0; i < 3; i++) {
			expected[291 + i] = 0 + 34 + 1 + 65 + i;
		}		
		// Section 3 ("sm.a6.b3.a2.a1" up until and including SBI): 28 chars
		// indices 103-130 in cleanEncoding (= 294-321 in rawEncoding)
		for (int i = 0; i < 28; i++) {
			expected[294 + i] = 0 + 34 + 1 + 65 + 3 + i;
		}		
		// Section 4 ("||.fu.a6.c4.a2.a1." up until and including EBI): 85 chars
		// indices 131-215 in cleanEncoding (= 333-417 in rawEncoding)
		for (int i = 0; i < 85; i++) {
			expected[333 + i] = 0 + 34 + 1 + 65 + 3 + 28 + i;
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
		rawEncodings.add(miniRawEncoding.replace("{}MC\\.>.", "{}/MC\\.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO.M34.>.", "//MO.M34.>."));
		rawEncodings.add(miniRawEncoding.replace("/|.MC\\.>.", "//|.MC\\.>."));
		rawEncodings.add(miniRawEncoding.replace("{}MC\\.>.", "{}.MC\\.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO.M34.>.", "/.MO.M34.>."));
		rawEncodings.add(miniRawEncoding.replace("/|.MC\\.>.", "/.|.MC\\.>."));
		// VR 4
		rawEncodings.add(miniRawEncoding.replace("b1.>.|./", "b1.>.|/"));
		rawEncodings.add(miniRawEncoding.replace("b2.>./", "b2.>/"));
		rawEncodings.add(miniRawEncoding.replace("||.//", "||//"));
		// All VR met
		rawEncodings.add(miniRawEncoding);

		List<String[]> expected = new ArrayList<String[]>();
		// VR 1
		expected.add(new String[]{"65", "67", "INVALID ENCODING ERROR -- Remove this whitespace.", 
			"See VALIDITY RULE 1: The encoding cannot contain whitespace."});
		// VR 2
		expected.add(new String[]{"-1", "-1", "INVALID ENCODING ERROR -- The encoding does not end with an end break indicator.",	
			"See VALIDITY RULE 2: The encoding must end with an end break indicator."});
		// VR 3
		String error3a = "INVALID ENCODING ERROR -- Remove this system break indicator.";
		String error3b = "INVALID ENCODING ERROR -- Remove this symbol separator.";
		String rule3 = "See VALIDITY RULE 3: A system cannot start with a punctuation symbol.";
		expected.add(new String[]{"20", "21", error3a, rule3});
		expected.add(new String[]{"55", "56", error3a, rule3});
		expected.add(new String[]{"93", "94", error3a, rule3});
		expected.add(new String[]{"20", "21", error3b, rule3});
		expected.add(new String[]{"55", "56", error3b, rule3});
		expected.add(new String[]{"93", "94", error3b, rule3});
		// VR 4
		String error4a = "INVALID ENCODING ERROR -- Insert a symbol separator before this system break indicator.";
		String error4b = "INVALID ENCODING ERROR -- Insert a symbol separator before this end break indicator.";
		String rule4 = "See VALIDITY RULE 4: Each system must end with a symbol separator."; 		
		expected.add(new String[]{"53", "54", error4a, rule4});
		expected.add(new String[]{"91", "92", error4a, rule4});
		expected.add(new String[]{"132", "134", error4b, rule4});
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
		expected.add(new String[]{"47", "48", errorMissing, rule5});
		expected.add(new String[]{"87", "88", errorMissing, rule5});
		expected.add(new String[]{"125", "126", errorMissing, rule5});
		// Unknown symbol
		String errorUnknown = "UNKNOWN SYMBOL ERROR -- Check for typos or missing symbol separators; check TabSymbolSet.";
		expected.add(new String[]{"47", "50", errorUnknown, rule5});
		expected.add(new String[]{"87", "90", errorUnknown, rule5});
		expected.add(new String[]{"125", "128", errorUnknown, rule5});
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
		rawEncodings.add(miniRawEncoding.replace("{}MC\\.>.", "{}>.MC\\.>."));
		rawEncodings.add(miniRawEncoding.replace("/MO.M34.>.", "/>.MO.M34.>."));
		rawEncodings.add(miniRawEncoding.replace("/|.MC\\.>.", "/>.|.MC\\.>."));
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
		rawEncodings.add(miniRawEncoding.replace("MC\\.>.sb.>.", "MC\\.sb.>."));
		rawEncodings.add(miniRawEncoding.replace("MO.M34.>.mi.>.", "MO.M34.mi.>."));
		rawEncodings.add(miniRawEncoding.replace("MC\\.>.*.>.", "MC\\.*.>."));
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
		expected.add(new String[]{"20", "21", error1, rule1});
		expected.add(new String[]{"55", "56", error1, rule1});
		expected.add(new String[]{"93", "94", error1, rule1});
		// LR 2
		String error2 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule2 = "See LAYOUT RULE 2: A system must end with a space, a barline, or some sort of repeat barline.";
		expected.add(new String[]{"47", "49", error2, rule2});
		expected.add(new String[]{"87", "89", error2, rule2});
		expected.add(new String[]{"125", "127", error2, rule2});
		// LR 3
		String error3 = "INVALID ENCODING ERROR -- Remove this space.";
		String rule3 = "See LAYOUT RULE 3: A constant musical symbol cannot be succeeded by a space.";
		expected.add(new String[]{"54", "55", error3, rule3});
		expected.add(new String[]{"92", "93", error3, rule3});
		expected.add(new String[]{"133", "134", error3, rule3});
		// LR 4
		String error4 = "INVALID ENCODING ERROR -- Insert a space after this TabSymbol.";
		String rule4 = "See LAYOUT RULE 4: A vertical sonority must be succeeded by a space."; 
		expected.add(new String[]{"47", "49", error4, rule4});
		expected.add(new String[]{"80", "82", error4, rule4});
		expected.add(new String[]{"125", "127", error4, rule4});
		// LR 5
		String error5 = "INVALID ENCODING ERROR -- Insert a space after this RhythmSymbol.";
		String rule5 = "See LAYOUT RULE 5: A rest (or rhythm dot at the beginning of a system or bar) must be succeeded by a space."; 
		expected.add(new String[]{"26", "28", error5, rule5});
		expected.add(new String[]{"64", "66", error5, rule5});
		expected.add(new String[]{"101", "102", error5, rule5});
		// LR 6
		String error6 = "INVALID ENCODING ERROR -- Insert a space after this MensurationSign.";
		String rule6 = "See LAYOUT RULE 6: A mensuration sign must be succeeded by a space.";
		expected.add(new String[]{"20", "23", error6, rule6});
		expected.add(new String[]{"58", "61", error6, rule6});
		expected.add(new String[]{"95", "98", error6, rule6});
		// LR 7
		String error7 = "INVALID ENCODING ERROR -- Remove duplicate TabSymbol(s).";
		String rule7 = "See LAYOUT RULE 7: A vertical sonority can contain only one TabSymbol per course.";
		expected.add(new String[]{"33", "44", error7, rule7});
		expected.add(new String[]{"71", "82", error7, rule7});
		expected.add(new String[]{"111", "122", error7, rule7});
		// LR 8
		String error8 = "INVALID ENCODING ERROR -- This vertical sonority is not encoded in the correct sequence."; 
		String rule8 = "See LAYOUT RULE 8: A vertical sonority must be encoded in a fixed sequence.";
		expected.add(new String[]{"33", "44", error8, rule8});
		expected.add(new String[]{"71", "82", error8, rule8});
		expected.add(new String[]{"111", "122", error8, rule8});
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


	@Test
	public void testGetIndicesOfPrecedingSymbol() {		
		List<Integer[]> expected = new ArrayList<>();
		// a6 (index 39)
		expected.add(new Integer[]{39, 35, 38}); // preceding is non-CMS (sm*)
		// a6 (index 78)
		expected.add(new Integer[]{78, 76, 77}); // preceding is CMS (>)
		// e2 (index 123)
		expected.add(new Integer[]{123, 120, 122}); // preceding is non-CMS (a3)
		// e2 (index 151)
		expected.add(new Integer[]{151, 149, 150}); // preceding is CMS (>)

		List<Integer[]> actual = new ArrayList<>();
		String cleanEnc = new Encoding(encodingTestpiece).getCleanEncoding();
		actual.add(Encoding.getIndicesOfPrecedingSymbol("a6", cleanEnc, false));
		actual.add(Encoding.getIndicesOfPrecedingSymbol("a6", cleanEnc, true));
		actual.add(Encoding.getIndicesOfPrecedingSymbol("e2", cleanEnc, false));
		actual.add(Encoding.getIndicesOfPrecedingSymbol("e2", cleanEnc, true));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testCombineSuccessiveRestEvents() {
		List<String> events = Arrays.asList(new String[]{
			"sb.c4.>.", "mi.>.", "mi.c4.>.", "|.",
			"mi.>.", "sm.>.", "sm.>.", "mi.>.", "mi.c4.>.", "|.",
			"mi.>.", "sm.>.", "sm.>.", "|.", "mi.>.", "mi.c4.>.", "||."
		});
		
		List<String> expected = Arrays.asList(new String[]{
			"sb.c4.>.", "mi.>.", "mi.c4.>.", "|.",
			"sb*.>.", "mi.c4.>.", "|.",
			"sb.>.", "|.", "mi.>.", "mi.c4.>.", "||."
		});
		
		List<String> actual = Encoding.combineSuccessiveRestEvents(events, TabSymbolSet.FRENCH);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testAssertEventType() {
		Encoding encoding = new Encoding(encodingTestpiece);
		
		List<List<Boolean>> expected = new ArrayList<>();
		// TS
		expected.add(Arrays.asList(new Boolean[]{
			false, false, false, true, false,
			true, true, true, true, true, true, true, false, 
			false, true, true, true, true, false,
			true, true, true, false, true, false
		}));
		// RS
		expected.add(Arrays.asList(new Boolean[]{
			false, true, true, true, false,
			true, true, true, false, true, true, false, false, 
			false, true, false, true, false, false,
			false, false, true, true, true, false
		}));
		// Rest
		expected.add(Arrays.asList(new Boolean[]{
			false, true, true, false, false,
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false,
			false, false, false, true, false, false,
		}));
		// MS
		expected.add(Arrays.asList(new Boolean[]{
			true, false, false, false, false,
			false, false, false, false, false, false, false, false, 
			false, false, false, false, false, false,
			false, false, false, false, false, false,
		}));
		// Barline
		expected.add(Arrays.asList(new Boolean[]{
			false, false, false, false, true, 
			false, false, false, false, false, false, false, true, 
			true, false, false, false, false, true, 
			false, false, false, false, false, true 
		}));

		List<List<Boolean>> actual = new ArrayList<>();
		List<String> allEvents = new ArrayList<>();
		encoding.getEvents().forEach(e -> allEvents.add(e.getEncoding()));
		List<Boolean> actualTs = new ArrayList<>();
		allEvents.forEach(e -> actualTs.add(Encoding.assertEventType(e, 
			encoding.getTabSymbolSet(), "TabSymbol")));
		actual.add(actualTs);
		List<Boolean> actualRs = new ArrayList<>();
		allEvents.forEach(e -> actualRs.add(Encoding.assertEventType(e, null, "RhythmSymbol")));
		actual.add(actualRs);
		List<Boolean> actualRest = new ArrayList<>();
		allEvents.forEach(e -> actualRest.add(Encoding.assertEventType(e, null, "rest")));
		actual.add(actualRest);
		List<Boolean> actualMs = new ArrayList<>();
		allEvents.forEach(e -> actualMs.add(Encoding.assertEventType(e, null, "MensurationSign")));
		actual.add(actualMs);
		List<Boolean> actualBl = new ArrayList<>();
		allEvents.forEach(e -> actualBl.add(Encoding.assertEventType(e, null, "barline")));
		actual.add(actualBl);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j) );
			}
		}
	}


	@Test
	public void testRemoveComment() {
		List<String> comments = Arrays.asList(new String[]{
			"|{@Footnote 1}.", 
			"|.{@Footnote 1}",
			"mi.a6.h5.c4.b3.f2{@'mi.a6.' in source}.", 
			"mi.a6.h{@'mi.a6.' in source}5.c4.b3.f2."});
	
		List<String> expected = Arrays.asList(new String[]{
			"|.", "|.", "mi.a6.h5.c4.b3.f2.", "mi.a6.h5.c4.b3.f2."	
		});

		List<String> actual = new ArrayList<>();
		for (String s : comments) {
			actual.add(Encoding.removeComment(s));
		}

		assertEquals(expected, actual);
	}


	@Test
	public void testRemoveDecorativeBarlineEvents() {
		List<Event> expected = getEvents(true);
		List<Event> actual = Encoding.removeDecorativeBarlineEvents(getEvents(false));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testRecompose() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<String> events = getDecomposedEvents("testpiece", true);
		// Add line break after barlines without one
		String expected = "";
		for (String s : events) {
			expected += s;
			if (s.equals(Symbol.SYSTEM_BREAK_INDICATOR) ||
				(!s.equals(Symbol.END_BREAK_INDICATOR) &&
				(Symbol.getConstantMusicalSymbol(s.substring(0, s.indexOf(Symbol.SYMBOL_SEPARATOR))) != null &&
				Symbol.getConstantMusicalSymbol(s.substring(0, s.indexOf(Symbol.SYMBOL_SEPARATOR))).isBarline()))) {
				expected += "\r\n"; 
			}				
		}
		System.out.println(expected);

		assertEquals(expected, Encoding.recompose(encoding.decompose(true, true)));
	}


	@Test
	public void testGetMetersBarsDiminutions() {
		Encoding e = new Encoding(encodingTestGetMeterInfo);

		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 1, 1, 2});
		expected.add(new Integer[]{2, 2, 2, 3, 2});
		expected.add(new Integer[]{3, 4, 4, 5, 4});
		expected.add(new Integer[]{2, 2, 6, 7, 1});
		expected.add(new Integer[]{5, 16, 8, 8, 1});
		expected.add(new Integer[]{2, 2, 9, 9, -2});

		List<Integer[]> actual = e.getMetersBarsDiminutions();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	@Test
	public void testAugmentHeader() {
		Encoding e1 = new Encoding(encodingTestpiece); // reverse
		Encoding e2 = new Encoding(encodingTestGetMeterInfo); // reverse
		Encoding e3 = new Encoding(encodingTestpiece); // rescale
		Encoding e4 = new Encoding(encodingTestGetMeterInfo); // rescale

		List<String> expected = new ArrayList<>();
		String expected1 = getHeader("testpiece");
		expected.add(expected1);
		String expected2 = getHeader("testGetMeterInfo");
		int start = expected2.indexOf(Encoding.METER_INFO_TAG) + 
			(Encoding.METER_INFO_TAG + ": ").length();
		expected2 = 
			expected2.replace(expected2.substring(start, 
			expected2.indexOf(Encoding.CLOSE_METADATA_BRACKET, start)), 
			"2/2 (1); 5/16 (2); 2/2 (3-4); 3/4 (5-6); 2/2 (7-8); 3/8 (9)");
		start = expected2.indexOf(Encoding.DIMINUTION_TAG) +  
			(Encoding.DIMINUTION_TAG + ": ").length();
		expected2 = 
			expected2.replace(expected2.substring(start, 
			expected2.indexOf(Encoding.CLOSE_METADATA_BRACKET, start)), 
			"-2; 1; 1; 4; 2; 2");
		expected.add(expected2);

		String expected3 = getHeader("testpiece");
		expected3 = expected3.replace("2/2", "2/1");
		expected.add(expected3);
		String expected4 = getHeader("testGetMeterInfo");
		expected4 = expected4.replace("3/8", "3/16");
		expected4 = expected4.replace("2/2", "2/4");
		expected4 = expected4.replace("3/4", "3/8");
		expected4 = expected4.replace("5/16", "5/32");
		expected.add(expected4);

		List<String> actual = Arrays.asList(
			Encoding.augmentHeader(e1.getHeader(), e1.getMetadata(), 
				e1.getMetersBarsDiminutions(), -1, "reverse"),
			Encoding.augmentHeader(e2.getHeader(), e2.getMetadata(), 
				e2.getMetersBarsDiminutions(), -1, "reverse"),
			Encoding.augmentHeader(e3.getHeader(), e3.getMetadata(), 
				e3.getMetersBarsDiminutions(), 2, "rescale"),
			Encoding.augmentHeader(e4.getHeader(), e4.getMetadata(), 
				e4.getMetersBarsDiminutions(), -2, "rescale")
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i <expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testAugmentEvents() {
		Encoding e1 = new Encoding(encodingTestpiece); // reverse
		Encoding e2 = new Encoding(encodingTestGetMeterInfo); // reverse (multiple MensurationSigns)
		Encoding e3 = new Encoding(encodingTestpiece); // deornament
		Encoding e4 = new Encoding(encodingTestpiece); // rescale

		String dblBarline = Symbol.BARLINE.makeVariant(2, null).getEncoding() + Symbol.SYMBOL_SEPARATOR;

		List<List<String>> expected = new ArrayList<>();
		List<String> expected1 = new ArrayList<>(getDecomposedEvents("testpiece", true));	
		expected1 = expected1.subList(0, expected1.lastIndexOf(dblBarline));
		expected1.remove(expected1.indexOf("MC\\.>."));
		Collections.reverse(expected1);
		expected1.add(0, "MC\\.>.");
		expected1.add(dblBarline);
		expected1.add(Symbol.END_BREAK_INDICATOR);
		expected.add(expected1);

		List<String> expected2 = new ArrayList<>(getDecomposedEvents("testGetMeterInfo", true));
		expected2 = expected2.subList(0, expected2.indexOf(dblBarline));
		for (String s : Arrays.asList("M3:8.>.", "MC\\.>.", "M3.>.", "MC\\.>.", "MC\\.>.")) {
			expected2.remove(expected2.indexOf(s));
		}
		Collections.reverse(expected2);
		expected2.add(0, "MC\\.>.");
		expected2.add(7, "M5:16.>.");
		expected2.add(13, "MC\\.>.");
		expected2.add(25, "M3.>.");
		expected2.add(37, "MC\\.>.");
		expected2.add(51, "M3:8.>.");
		expected2.add(dblBarline);
		expected2.add(Symbol.END_BREAK_INDICATOR);
		expected.add(expected2);

		List<String> events = getDecomposedEvents("testpiece", true);
		List<String> expected3 = new ArrayList<>(events);
		expected3.set(5, "mi.a6.c4.i2.a1.>.");
		expected3.set(15, "mi.a6.c4.a2.a1.>.");
		events.stream()
			.filter(ev -> ev.startsWith("fu") || ev.startsWith("sf"))
			.forEach(ev -> expected3.remove(ev));
		expected.add(expected3);

		List<String> expected4 = new ArrayList<>(getDecomposedEvents("testpiece", true));
		List<String[]> rsMap = Arrays.asList(
			new String[]{"sb", "br"},
			new String[]{"mi", "sb"},
			new String[]{"sm", "mi"},
			new String[]{"sm*", "mi*"},
			new String[]{"fu", "sm"},
			new String[]{"sf", "fu"}
		);
		expected4.stream()
			.filter(ev -> Encoding.assertEventType(ev, e4.getTabSymbolSet(), "RhythmSymbol"))
			.forEach(ev -> {
				String rs = ev.substring(0, ev.indexOf(Symbol.SYMBOL_SEPARATOR));
				expected4.set(
					expected4.indexOf(ev), 
					ev.replace(rs, rsMap.get(ToolBox.getItemsAtIndex(rsMap, 0).indexOf(rs))[1]));
			}
		);
		expected.add(expected4);

		List<List<String>> actual = Arrays.asList(
			Encoding.augmentEvents(e1.decompose(true, true), 
				e1.getMetersBarsDiminutions(), e1.getTabSymbolSet(), 
				-1, -1, "reverse"),
			Encoding.augmentEvents(e2.decompose(true, true), 
				e2.getMetersBarsDiminutions(), e2.getTabSymbolSet(), 
				-1, -1, "reverse"),
			Encoding.augmentEvents(e3.decompose(true, true), 
				null, null, 
				RhythmSymbol.SEMIMINIM.getDuration(), -1, "deornament"),
			Encoding.augmentEvents(e4.decompose(true, true), 
				null, null, 
				-1, 2, "rescale")
		);

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i <expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
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
		expected.add(Arrays.asList(new Integer[]{0, 10, 21}));
		
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
	public void testSystemBarNumbers() {
		Encoding encoding1 = new Encoding(encodingTestpiece);
		Encoding encoding2 = new Encoding(new File("F:/research/data/annotated/encodings/thesis-int/3vv/newsidler-1536_7-disant_adiu.tbp"));

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
		
		List<List<Integer>> actual = new ArrayList<>();
		actual.addAll(encoding1.getSystemBarNumbers());
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
	public void testDecompose() {
		Encoding encoding = new Encoding(encodingTestpiece);

		List<String> expected = new ArrayList<>();
		// Complementing RS 
		// a. With SBI/EBI
		expected.addAll(getDecomposedEvents("testpiece", true));
		// b. Without SBI/EBI
		for (String s : getDecomposedEvents("testpiece", true)) {
			if (!s.equals(Symbol.SYSTEM_BREAK_INDICATOR) &&
				!s.equals(Symbol.END_BREAK_INDICATOR)) {
				expected.add(s);
			}
		}
		// Not complementing RS 
		// a. With SBI/EBI
		expected.addAll(getDecomposedEvents("testpiece", false));
		// b. Without SBI/EBI
		for (String s : getDecomposedEvents("testpiece", false)) {
			if (!s.equals(Symbol.SYSTEM_BREAK_INDICATOR) &&
				!s.equals(Symbol.END_BREAK_INDICATOR)) {
				expected.add(s);
			}
		}

		List<String> actual = new ArrayList<>();
		actual.addAll(encoding.decompose(true, true));
		actual.addAll(encoding.decompose(true, false));
		actual.addAll(encoding.decompose(false, true));
		actual.addAll(encoding.decompose(false, false));

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

}
