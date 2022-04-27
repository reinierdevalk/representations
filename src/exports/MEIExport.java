package exports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.math.Rational;
import imports.MIDIImport;
import representations.Tablature;
import representations.Transcription;
import structure.Timeline;
import tbp.ConstantMusicalSymbol;
import tbp.Encoding;
import tbp.Event;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.Symbol;
import tbp.TabSymbol;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;
import utility.DataConverter;

public class MEIExport {
	
	public static String rootDir = "F:/research/"; // TODO also defined in UI; should be in one place only
	public static String MEITemplatePath = rootDir + "data/" + "templates/"; // TODO suffix data/defined inside Runner.setPathsToCodeAndData() 
	public static String scriptPathPythonMEI = rootDir + "software/code/" + "eclipse/formats-representations/py/";
	
	private static List<Integer> mpcFlats = 
		Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11, 4}); // Bb, Eb, Ab, Db, Gb, Cb, Fb,
	private static List<Integer> mpcSharps = 
		Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5, 0}); // F#, C#, G#, D#, A#, E#, B#
	
	public static final Rational TRIPLETISER = new Rational(3, 2);
	private static final Rational DETRIPLETISER = new Rational(2, 3);
	private static final int BREVE = -1;
	private static final int LONG = -2;
	
	private static boolean ONLY_TAB, ONLY_TRANS, TAB_AND_TRANS;
	
	private static boolean verbose = false;
	
	// Keys contains, for each key, the number of sharps (positive) or flats (negative) and 
	// the MIDI pitch class of the tonic
	private static List<Integer[]> keys;
	static {
		keys = new ArrayList<Integer[]>();
		keys.add(new Integer[]{-7, 11}); // Cb
		keys.add(new Integer[]{-6, 6}); // Gb
		keys.add(new Integer[]{-5, 1}); // Db
		keys.add(new Integer[]{-4, 8}); // Ab
		keys.add(new Integer[]{-3, 3}); // Eb
		keys.add(new Integer[]{-2, 10}); // Bb
		keys.add(new Integer[]{-1, 5}); // F
		keys.add(new Integer[]{0, 0}); // C
		keys.add(new Integer[]{1, 7}); // G
		keys.add(new Integer[]{2, 2}); // D
		keys.add(new Integer[]{3, 9}); // A
		keys.add(new Integer[]{4, 4}); // E
		keys.add(new Integer[]{5, 11}); // B
		keys.add(new Integer[]{6, 6}); // F#
		keys.add(new Integer[]{7, 1}); // C#
	}
	
	private static final List<String> MEI_HEAD = Arrays.asList(new String[]{"title"});
	private static final List<String> STRINGS = 
		Arrays.asList(new String[]{"pname", "oct", "accid", "tie", "dur", "dots", "ID", "metPos"});
	private static final List<String> INTS = Arrays.asList(new String[]{
		"ind", "indTab", "bar", "onsetNum", "onsetDen", "metPosNum", "metPosDen", "dur", "dots", 
		"tripletOpen", "tripletMid", "tripletClose", "beamOpen", "beamClose"});
	private static final String TAB = "    ";
	private static final String INDENT_ONE = TAB.repeat(5);
	private static final String INDENT_TWO = TAB.repeat(6);
	private static final int XML_DUR_IND = 0;
	private static final int XML_DOTS_IND = 1;
	
	static boolean adaptCMNDur = true; // TODO remove once tab.dur.sym.ratio is in MEI schema
	
	public static void main(String[] args) {
		
		String testTabFile = "4471_40_cum_sancto_spiritu";
//		testTabFile = "5263_12_in_exitu_israel_de_egipto_desprez-3";
//		testTabFile = "4465_33-34_memor_esto-2";
//		testTabFile = "5255_04_stabat_mater_dolorosa_desprez-2";
//		testTabFile = "5254_03_benedicta_es_coelorum_desprez-1";
//		testTabFile = "5256_05_inviolata_integra_desprez-2";
//		testTabFile = "5256_05_inviolata_integra_desprez-3";
//		testTabFile = "4vv/BSB-mus.ms._272-mille_regres";

//		testTabFile = "3vv/newsidler-1536_7-disant_adiu";
//		testTabFile = "3vv/newsidler-1536_7-mess_pensees";
//		testTabFile = "3vv/pisador-1552_7-pleni_de"; // TODO remove every second barline
//		testTabFile = "3vv/judenkuenig-1523_2-elslein_liebes";
//		testTabFile = "3vv/newsidler-1544_2-nun_volget"; // TODO remove every second barline in ternary part
//		testTabFile = "3vv/phalese-1547_7-tant_que-3vv";
		
		testTabFile = "4vv/rotta-1546_15-bramo_morir";
		
		Tablature testTab = new Tablature(new File(
			"F:/research/data/annotated/encodings/thesis-int/" + testTabFile + 
			Encoding.EXTENSION), false);
		
		testTab = new Tablature(new File(
			"C:/Users/Reinier/Desktop/test-capirola/tab/capirola-1520-et_in_terra_pax" + 
			Encoding.EXTENSION), false);
		
//		exportTabMEIFile(testTab, "C:/Users/Reinier/Desktop/test-capirola/capirola-1520-et_in_terra_pax" + "-tab");	
//		System.exit(0);
		
		String notationtypeStr = "tab.lute.italian"; // TODO give as param to method
		String tuningStr = "lute.renaissance.6"; // TODO give as param to method
		
		String path = "C:/Users/Reinier/Desktop/MEI/";
		path = "C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/";
		
		String tabFile = "thesis-int/3vv/newsidler-1544_2-nun_volget";
		tabFile = "1132_13_o_sio_potessi_donna_berchem_solo";
		
		String pieceName = "capirola-1520-et_in_terra_pax";
		
		// This must be a created Transcription and the second argument must be null
		Transcription trans = 
			new Transcription(
//			new File("F:/research/data/MIDI/thesis-int/4vv/rotta-1546_15-bramo_morir.mid"),
//			new File("F:/research/data/annotated/MIDI/thesis-int/3vv/newsidler-1544_2-nun_volget.mid"),
//			new File("F:/research/data/MIDI/" + tabFile + MIDIImport.EXTENSION),
//			new File("C:/Users/Reinier/Desktop/MEI/newsidler-1544_2-nun_volget-test.mid"),
//			new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/mapped/3610_033_inter_natos_mulierum_morales_T-rev-mapped.mid"),
//			new File("C:/Users/Reinier/Desktop/IMS-tours/fold_06-1025_adieu_mes_amours.mid"),
//			new File("C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/Berchem_-_O_s'io_potessi_donna.mid"),
			new File("C:/Users/Reinier/Desktop/test-capirola/mapped/" + pieceName + MIDIImport.EXTENSION),
			null);
//		trans = null;
		
//		Tablature tab = 
//			new Tablature(new File("F:/research/data/encodings/" + tabFile + Encoding.EXTENSION), false);
		Tablature tab = 
			new Tablature(new File("C:/Users/Reinier/Desktop/test-capirola/tab/" + 
			pieceName + Encoding.EXTENSION), false);
//		Tablature tab = 
//			new Tablature(new File("F:/research/data/annotated/encodings/thesis-int/" + 
//			"3vv/newsidler-1544_2-nun_volget" + 
//			"4vv/rotta-1546_15-bramo_morir" +
//			Encoding.EXTENSION), false);
//		tab = null;
		
//		List<List<String[]>> data = getData(t);
//		List<Object> data = getData(t);
//		List<List<Integer>> mismatchInds = 
//			ToolBox.getStoredObjectBinary(new ArrayList<List<Integer>>(), 
//			new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/mapped/" + 
//			"3610_033_inter_natos_mulierum_morales_T-rev" + "-mismatchInds" + ".ser"));
		List<List<Integer>> mismatchInds = new ArrayList<>();
		mismatchInds.add(new ArrayList<Integer>());
		mismatchInds.add(new ArrayList<Integer>());
		mismatchInds.add(new ArrayList<Integer>());
		mismatchInds.add(new ArrayList<Integer>());
		mismatchInds.add(new ArrayList<Integer>());
		
		boolean grandStaff = true;
		boolean tabOnTop = true;
		boolean alignWithMetricBarring = true;
		String s = path + "newsidler-1544_2-nun_volget-test";
		s = path + "fold_06-1025_adieu_mes_amours";
		s = path + "Berchem_-_O_s'io_potessi_donna";
		s = "C:/Users/Reinier/Desktop/test-capirola/" + pieceName;
//		List<Integer[]> mi = (tab == null) ? trans.getMeterInfo() : tab.getMeterInfo();
		
		exportMEIFile(trans, tab, /*tab.getBasicTabSymbolProperties(), trans.getKeyInfo(), 
			tab.getTripletOnsetPairs(),*/ mismatchInds, grandStaff, tabOnTop, 
			alignWithMetricBarring, s);
//		System.out.println(ToolBox.readTextFile(new File(s)));

//		String scoreType = grandStaff ? "grand_staff" : "score";
//		ToolBox.storeTextFile(mei, 
//			new File(path + t.getNumberOfVoices() + Runner.voices + "/" + t.getPieceName() + "-" + 
//			scoreType + ".xml"));
//		System.out.println(Arrays.asList(data.get(0).get(0)));
	}


	public static List<Integer[]> getKeys() {
		return keys;
	}


	public static void setRootDir(String arg) {
		rootDir = arg;
	}


	/**
	 * Returns the octave of the given midiPitch. Middle C (midiPitch = 60) marks octave 4.
	 * 
	 * @param midiPitch
	 * @return
	 */
	// TESTED
	static int getOctave(int midiPitch) {
		if (midiPitch >= 36 && midiPitch < 48) {
			return 2; 
		}
		else if (midiPitch >= 48 && midiPitch < 60) {
			return 3; 
		}
		else if (midiPitch >= 60 && midiPitch < 72) {
			return 4; 
		}
		else if (midiPitch >= 72 && midiPitch < 84) {
			return 5;
		}
		else if (midiPitch >= 84 && midiPitch < 96) {
			return 6;
		}
		else {
			return -1;
		}
	}


	/**
	 * Splits the given fraction into its unit fractions (from large to small). The fraction must
	 * be a multiple of 1/96 (tablature case) or 1/128 (non-tablature case).
	 *  
	 * @param r
	 * @param mul
	 * @return
	 */
	// TESTED
	static public List<Rational> getUnitFractions(Rational r, Rational mul) {
		List<Rational> uf = new ArrayList<Rational>();
		r.reduce();
		// r must be a multiple of 1/96 or 1/128
		if (r.mul(mul.getDenom()).getDenom() != 1) {
			throw new RuntimeException("ERROR: r must be a multiple of 1/96 (tablature case) " + 
				"or 1/128 (non-tablature case) but is " + r.toString());
		}
		// If the numerator = 1: add to uf
		int num = r.getNumer();
		int den = r.getDenom();
		if (num == 1 || (num % 2 == 0 && (double) num == num / (double) den)) {
//			System.out.println("if");
//			System.out.println(r.getNumer());
//			System.out.println(r.getNumer() / (double) r.getDenom());
			uf.add(r);
		}
		// If not: split into a fraction of num-1/den and 1/den
		else {
//			System.out.println("else");
			// Add 1/den to uf
			uf.add(new Rational(1, den));
			// Call method on num-1/den
			uf.addAll(getUnitFractions(new Rational(num-1, den), mul));
		}
		Collections.sort(uf);
		Collections.reverse(uf);

		return uf;
	}


	/**
	 * Determines whether the given list of unit fractions represents a dotted note.
	 *  
	 * @param r
	 * @return The number of dots, or, if not applicable, -1.
	 */
	// TESTED
	static int getNumDots(List<Rational> r) {
		// The number of dots n lengthens a note by l its value, where l = ((2^n)-1)/2^n
		// (see https://en.wikipedia.org/wiki/Note_value)
		// Example for double dotted half note: the note is lengthened by ((2^2)-1)/2^2 = 
		// 3/4 times its original value, i.e., it is lengthened by 1/2 * 3/4 = 3/8.  
		// If r represents a dotted note, n is equal to the number of fractions added to the 
		// base fraction, the first element of r. Thus, if the sum of the remaining elements of 
		// r (the lengthening) equals base * l, the note is dotted, and n is returned 
		int n = r.size()-1; 
		// Do only when there are additional fractions
		if (n > 0) {
			Rational base = r.get(0);
			Rational sumRemaining = ToolBox.sumListRational(r).sub(base);
			Rational l = new Rational((int) Math.pow(2, n) - 1, (int) Math.pow(2, n));
			if (sumRemaining.equals(base.mul(l))) {
				return n;
			}
		}
		return 0;
	}


	/**
	 * Rounds the given Rational up to the closest value on the grid of the given grid value.
	 * 
	 * @param r
	 * @param grid
	 * @return
	 */
	// TESTED
	static Rational round(Rational r, Rational grid) {
		Rational rounded;
		for (int i = 0; i < grid.getDenom(); i++) {
			rounded = new Rational(r.getNumer()+i, r.getDenom());
			// r must be a multiple of grid
			if (rounded.mul(grid.getDenom()).getDenom() == 1) {
				rounded.reduce();
				return rounded;
			}
		}
		return null;
	}


	/**
	 * Returns the duration of the given Rational. Returns an Integer[] with as element 0 the 
	 * duration as the smallest denominator, and as element the number of dots.  
	 *  
	 * @param dur
	 * @return
	 */
	private Integer[] getDuration(Rational durRat) {
		Integer[] dur = null;
		durRat.reduce();
		System.out.println("BLA");
		List<Rational> all = new ArrayList<Rational>();
		for (int i = 1; i <= 128; i++) {
			all.add(new Rational(i, 128));
		}
		
		// Quantize
		if (!all.contains(durRat)) {
			dur = null;
		}
		else {	
			// Quadruple dotted = 1.9375
			Rational basicNoteValQdrD = durRat.div(new Rational(31, 16));
			basicNoteValQdrD.reduce();
			if (all.contains(basicNoteValQdrD)) {
				dur = new Integer[]{basicNoteValQdrD.getDenom(), 4};
			}
			// Triple dotted = 1.875 * basic note value
			Rational basicNoteValTrpD = durRat.div(new Rational(15, 8));
			basicNoteValTrpD.reduce();
			if (all.contains(basicNoteValTrpD)) {
				dur = new Integer[]{basicNoteValTrpD.getDenom(), 3};
			}
			// Double dotted = 1.75 * basic note value
			Rational basicNoteValDblD = durRat.div(new Rational(7, 4));
			basicNoteValDblD.reduce();
			if (all.contains(basicNoteValDblD)) {
				dur = new Integer[]{basicNoteValDblD.getDenom(), 2};
			}
			// Dotted = 1.5 * basic note value 
			Rational basicNoteValD = durRat.div(new Rational(3, 2));
			basicNoteValD.reduce();
			if (all.contains(basicNoteValD)) {
				dur = new Integer[]{basicNoteValD.getDenom(), 1};
			}
			// Undotted
			Rational basicNoteVal = durRat.div(new Rational(1, 1));
			basicNoteVal.reduce();
			if (dur != null && all.contains(basicNoteVal)) {
				dur = new Integer[]{basicNoteVal.getDenom(), 0};
			}
		}
		return dur;
	}


	/**
	 * Returns from the given list of unit fractions the sublists of unit fractions that 
	 * represent dotted notes.
	 * 
	 * Example: when given the list [1/2, 1/4, 1/16, 1/32, 1/64], the method returns the 
	 * sublists [1/2, 1/4] and [1/16, 1/32, 1/64]. 
	 * 
	 * @param uf
	 * @return
	 */
	// TESTED
	public static List<List<Rational>> getUnitFractionSequences(List<Rational> uf) {
		List<List<Rational>> res = new ArrayList<List<Rational>>();
		for (int i = 0; i < uf.size(); i++) {
			List<Rational> temp = new ArrayList<Rational>();
			temp.add(uf.get(i));
			for (int j = i+1; j < uf.size(); j++) {
				Rational curr = uf.get(j);
				if (curr.equals(uf.get(j-1).div(2))) {
					temp.add(curr);
					i = j;
				}
				else { 
					break;
				}
			}
			res.add(temp);
		}
		return res;
	}


	private static Integer[][] getStaffAndLayer(int numVoices) {
		Integer[][] staffAndLayer = new Integer[numVoices][2];
		// Each element represents a voice and contains the staff and the layer for it
		if (numVoices == 2) {
			staffAndLayer[0] = new Integer[]{1, 1};
			staffAndLayer[1] = new Integer[]{2, 1};
		}
		if (numVoices == 3) {
			staffAndLayer[0] = new Integer[]{1, 1};
			staffAndLayer[1] = new Integer[]{2, 1};
			staffAndLayer[2] = new Integer[]{2, 2};
		}
		if (numVoices == 4) {
			staffAndLayer[0] = new Integer[]{1, 1};
			staffAndLayer[1] = new Integer[]{1, 2};
			staffAndLayer[2] = new Integer[]{2, 1};
			staffAndLayer[3] = new Integer[]{2, 2};
		}
		return staffAndLayer;
	}


	private static String removeTrailingSymbolSeparator(String s) {
		if (s.endsWith(Symbol.SYMBOL_SEPARATOR)) {
			s = s.substring(0, s.lastIndexOf(Symbol.SYMBOL_SEPARATOR)); 
		}
		return s;
	}


	private static String getNotationTypeStr(TabSymbolSet tss) {
		if (tss == TabSymbolSet.FRENCH) {
			return "tab.lute.french";
		}
		else if (tss == TabSymbolSet.ITALIAN) {
			return "tab.lute.italian";
		}
		else if (tss == TabSymbolSet.SPANISH) {
			return "tab.lute.spanish";
		} 
		else {
			return "tab.lute.french";
//			return "tab.lute.german";
		}
	}


	/**
	 * Gets the clef for the given voice with the given number of voices. Cleffing is
	 * fixed as follows
	 * <ul>
	 * <li>2vv: G F</li>
	 * <li>3vv: G F F</li>
	 * <li>4vv: G G F F</li>
	 * <li>5vv: G G F F F</li>
	 * <li>6vv: G G G F F F</li>
	 * </ul>
	 * 
	 * @param voice
	 * @param numVoices
	 * @param grandStaff
	 * @return
	 */
	private static String[] getCleffing(int voice, int numVoices, boolean grandStaff) {
		String[] gClef = new String[]{"G", "2"};
		String[] fClef = new String[]{"F", "4"};
		if (!grandStaff) {
			if ((voice == 1 && (numVoices == 2 || numVoices == 3)) ||
				(voice == 2 && (numVoices == 3 || numVoices == 4 || numVoices == 5)) ||
				(voice >= 3)) {
				return fClef;
			}
			else {
				return gClef;
			}
		}
		else {
			if (voice == 0) {
				return gClef;
			}
			else {
				return fClef;
			}
		}
	}


	/**
	 * Exports the given tablature as a TabMEI file, saved at the given path.
	 * 
	 * @param tab
	 * @param path
	 */
	private static void exportTabMEIFile(Tablature tab, String path) {

		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template-MEI.xml"));		
		String tuningStr = "lute.renaissance.6";
		TabSymbolSet tss = tab.getEncoding().getTabSymbolSet();
		String notationtypeStr = getNotationTypeStr(tss);

		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();

		// 1. Make meiHead
		String[] meiHead = new String[MEI_HEAD.size()];
		meiHead[MEI_HEAD.indexOf("title")] = tab.getPiecename();
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		List<Integer[]> mi = tab.getTimeline().getMeterInfo();
		List<String[]> meters = new ArrayList<>();
		for (Integer[] in : mi) {
			String sym = "";
			if (in[Timeline.MI_NUM] == 4 && in[Timeline.MI_DEN] == 4) {
				sym = " meter.sym='common'";
			}
			else if (in[Timeline.MI_NUM] == 2 && in[Timeline.MI_DEN] == 2) {
				sym = " meter.sym='cut'";
			}
			meters.add(new String[]{
				"meter.count='" + in[Timeline.MI_NUM] + "'", 
				"meter.unit='" + in[Timeline.MI_DEN] + "'",
				sym}
			);
		}

		// 2. Make music
		// a. Make scoreDef. The scoreDef contains the initial meter (if any); any additional 
		// ones are stored in nonInitMeters
		String[] initMeter = meters.get(0); 
		String scoreDefStr = initMeter[0] + " " + initMeter[1] + initMeter[2];
		res = res.replace("scoreDef_placeholder", scoreDefStr.trim());
		int meterIndex = 0;

		// b. Make staffGrp (goes inside scoreDef)
		String staffGrpAtt = "";
		res = res.replace(" staffGrp_placeholder", staffGrpAtt);
		String staffGrpStr = 
			"<staffDef n='1'" + " xml:id='s1'" + " lines='6'" + " " + "notationtype='" + 
			notationtypeStr + "'" + ">" + "\r\n"; 
		staffGrpStr += INDENT_TWO + TAB + TAB + "<tuning tuning.standard='" + tuningStr + "'" + "/>" + "\r\n";		
		staffGrpStr += INDENT_TWO + TAB + "</staffDef>";

		res = res.replace("staffGrp_content_placeholder", staffGrpStr);
		
		// Get events; remove any decorative opening barlines (which affect the XML bar numbering)
		List<Event> events = 
			Encoding.removeDecorativeBarlineEvents(tab.getEncoding().getEvents());
//		List<List<String[]>> ebf = tab.getEncoding().getExtendedEventsPerBar(true);

//		List<List<String[]>> ebfPruned = new ArrayList<>();
//		for (int i = 0; i < ebf.size(); i++) {
//			List<String[]> bar = ebf.get(i);
//			// Add bar only if it is not one containing only one element that is a barline
//			String f = removeTrailingSymbolSeparator(bar.get(0)[Encoding.EVENT_IND]);
//			if (!(bar.size() == 1 && ConstantMusicalSymbol.isBarline(f))) {
//				ebfPruned.add(bar);
//			}
//		}
//		ebf = ebfPruned;

		// 3. Make bars
		// Organise the information per bar
		StringBuilder sb = new StringBuilder();
		List<String> measuresTab = new ArrayList<>();
		Integer[] prevDurXML = new Integer[]{0, 0};
		for (int i = 0; i < events.size(); i++) {
//			List<String[]> currBar = events.get(i);
			
			int currTabBar = events.get(i).getBar();
//			int currTabBar = currBarEvents.get(0).getBar();
			List<Event> currBar = new ArrayList<>();
			for (int j = i; j < events.size(); j++) {
				if (events.get(j).getBar() == currTabBar + 1) {
					i = j - 1;
					break;
				}
				currBar.add(events.get(j));
			}
			System.out.println("bar  : " + (i+1));

			StringBuilder sbBar = new StringBuilder();
			
			// Make XML content for currBar
			String currBarXMLAsString = "";
			String barline = "";
			// For each event
			for (int j = 0; j < currBar.size(); j++) {
				Event currEventFull = currBar.get(j);
//				String[] currEventFull = currBar.get(j);
				String currEvent = 
					removeTrailingSymbolSeparator(currEventFull.getEncoding());
//				String currEvent = 
//					removeTrailingSymbolSeparator(currEventFull[Encoding.EVENT_IND]);
				String currEventOrig = currEventFull.getFootnote();
//				String currEventOrig = currEventFull[Encoding.FOOTNOTE_IND];
				System.out.println(currEventFull);
				System.out.println("event: " + currEvent);
				// Extract correction
				boolean isCorrected = false;
				if (currEventOrig != null) {
					if (currEventOrig.contains("'")) {
						currEventOrig = currEventOrig.substring(currEventOrig.indexOf("'")+1,
							currEventOrig.lastIndexOf("'"));
						isCorrected = true;
					}
					else {
						currEventOrig = 
							currEventOrig.substring(currEventOrig.indexOf(Encoding.FOOTNOTE_INDICATOR) + 1);
					}
					currEventOrig = removeTrailingSymbolSeparator(currEventOrig);
				}

				// Barline? End of bar reached; set barline if not single
				if (Symbol.getConstantMusicalSymbol(currEvent) != null && Symbol.getConstantMusicalSymbol(currEvent).isBarline()) {
//				if (ConstantMusicalSymbol.isBarline(currEvent)) {
					// TODO currently only single and double barline possible in MEI
					if (currEvent.equals(Symbol.BARLINE.makeVariant(2, null).getEncoding())) {
						barline = " right='dbl'";
					}
					if (i == events.size()-1) {
						barline = " right='end'";
					}
				}
				// Not a barline?
				else {
					// Get XML durations of currEvent, and, if applicable, currEventOrig
					Integer[] currDurXML = getXMLDur(currEvent);

					String sicEvent = !isCorrected ? currEvent : currEventOrig;
					String corrEvent = !isCorrected ? null : currEvent;

					boolean defaultCase = !isCorrected; 
					boolean oneReplacedByMultiple = isCorrected && sicEvent.endsWith(sp);
					boolean multipleReplacedByOne = 
						isCorrected && sicEvent.contains(sp) && !sicEvent.endsWith(sp);
					boolean defaultCorrectedCase = 
						isCorrected && !oneReplacedByMultiple && !multipleReplacedByOne;

					List<String> sicList = new ArrayList<>();
					List<String> corrList = new ArrayList<>();
					// No <sic> and <corr>
					if (defaultCase) {
						sicList.add(sicEvent);
					}
					// Both <sic> and <corr> contain one <tabGrp>
					if (defaultCorrectedCase) {
						sicList.add(sicEvent);
						corrList.add(corrEvent);
					}
					// <sic> contains multiple <tabGrp>s, <corr> one 
					if (multipleReplacedByOne) {
						for (String s : sicEvent.split(sp + ss)) {
							sicList.add(removeTrailingSymbolSeparator(s));
						}
						corrList.add(corrEvent);
					}
					// <sic> contains one <tabGrp>, <corr> multiple 
					// --> corrList contains corrEvent plus all events following that 
					// together have the same duration as durSic
					// NB It is assumed that the replacement is within the bar
					if (oneReplacedByMultiple) {
						sicEvent = sicEvent.substring(0, sicEvent.indexOf(sp));
						sicList.add(removeTrailingSymbolSeparator(sicEvent));
						corrList.add(corrEvent);
						RhythmSymbol rsSic = 
							RhythmSymbol.getRhythmSymbol(sicEvent.substring(0, sicEvent.indexOf(ss)));
						int durSic; 
						if (rsSic != null) {
							durSic = rsSic.getDuration();
						}
						else {
							durSic = -1; // TODO get last specified duration before currEvent
						}
						RhythmSymbol rsCorr = 
							RhythmSymbol.getRhythmSymbol(corrEvent.substring(0, corrEvent.indexOf(ss)));
						int durCorr;
						if (rsCorr != null) {
							durCorr = rsCorr.getDuration();
						}
						else {
							durCorr = -1; // TODO get last specified duration before currEvent
						}
						int durCorrToTrack = durCorr;
						// Iterate through next events until durCorr equals durSic
						for (int l = j+1; l < currBar.size(); l++) {
							Event nextEventFull = currBar.get(l);
//							String[] nextEventFull = currBar.get(l);
							String nextEvent = 
								removeTrailingSymbolSeparator(nextEventFull.getEncoding());
//							String nextEvent = 
//								removeTrailingSymbolSeparator(nextEventFull[Encoding.EVENT_IND]);
							String nextEventOrig = nextEventFull.getFootnote();
//							String nextEventOrig = nextEventFull[Encoding.FOOTNOTE_IND];
							// If the next element has a footnote
							if (nextEventOrig != null) {
								nextEventOrig = removeTrailingSymbolSeparator(nextEventOrig);
								corrList.add(nextEvent);
								// Determine duration of corrected event, increment durCorr,
								// and update durrCorrToTrack
								RhythmSymbol nextEventRS = 
									RhythmSymbol.getRhythmSymbol(nextEvent.substring(0, 
									nextEvent.indexOf(ss)));
								int durCorrNext;
								if (nextEventRS != null) {
									durCorrNext = nextEventRS.getDuration();
									durCorrToTrack = durCorrNext;
								}
								else {
									durCorrNext = durCorrToTrack;
								}
								durCorr += durCorrNext;		
							}
							if (durCorr == durSic) {
								int eventsToSkip = corrList.size() - 1;
								j += eventsToSkip;
								break;
							}
						}
					}

					String eventAsXML = "";
					if (isCorrected) {
						eventAsXML += INDENT_TWO + TAB.repeat(3) + "<choice>" + "\r\n";
						eventAsXML += INDENT_TWO + TAB.repeat(4) + "<sic>" + "\r\n";
					}
					eventAsXML += getTabGrps(sicList, prevDurXML, isCorrected, tss);
					if (isCorrected) {
						eventAsXML += INDENT_TWO + TAB.repeat(4) + "</sic>" + "\r\n";
					}
					if (isCorrected) {
						eventAsXML += INDENT_TWO + TAB.repeat(4) + "<corr>" + "\r\n";
						eventAsXML += getTabGrps(corrList, prevDurXML, isCorrected, tss);
						eventAsXML += INDENT_TWO + TAB.repeat(4) + "</corr>" + "\r\n";
					}
					if (isCorrected) {
						eventAsXML += INDENT_TWO + TAB.repeat(3) + "</choice>" + "\r\n";
					}
					currBarXMLAsString += eventAsXML;
					
					// Update prevDurXML
					// a. Set prevDurXML to currDurXML (or, if it is null, to the last 
					// XML duration)
					if (defaultCase || defaultCorrectedCase || multipleReplacedByOne) {
						if (currDurXML != null) {
							prevDurXML = currDurXML;
						}
					}
					// b. Set prevDurXML to the last specified duration in corrList  
					if (oneReplacedByMultiple) {
						List<String> corrListRev = new ArrayList<>(corrList);
						Collections.reverse(corrListRev);
						for (String event : corrListRev) {
							Integer[] lastDurXML = getXMLDur(event);
							if (lastDurXML != null) {
								prevDurXML = lastDurXML;
								break;
							}
						}
					}
				}
			}
			// Wrap currBar in measure-staff-layer elements
//			// First bar: no indentation required because of section_content_placeholder placement
//			if (i > 0) {
			sbBar.append(INDENT_TWO);
//			}
			sbBar.append("<measure n='" + (i+1) + "'" + barline + ">" + "\r\n");
			sbBar.append(INDENT_TWO + TAB + "<staff n='1'" + ">" + "\r\n");
			sbBar.append(INDENT_TWO + TAB.repeat(2) + "<layer n='1'" + ">" + "\r\n");
			sbBar.append(currBarXMLAsString);
			sbBar.append(INDENT_TWO + TAB.repeat(2) + "</layer>" + "\r\n");
			sbBar.append(INDENT_TWO + TAB + "</staff>" + "\r\n");
			sbBar.append(INDENT_TWO + "</measure>");
//			System.out.println(sb.toString());
			if (i < events.size()-1) {
				sbBar.append("\r\n");
			}
			
			sb.append(sbBar);
			measuresTab.add(sbBar.toString());

			// Append meter change (if applicable)
			if (i < events.size()-1) {
				// Check for meter change in first event of next bar
				List<Event> nextBar = null; //events.get(i+1);
//				List<String[]> nextBar = events.get(i+1);
				Event firstEventNextFull = nextBar.get(0);
//				String[] firstEventNextFull = nextBar.get(0);
				String firstEventNext = firstEventNextFull.getEncoding();
//				String firstEventNext = firstEventNextFull[Encoding.EVENT_IND];
				// Remove final ss
				firstEventNext = firstEventNext.substring(0, firstEventNext.lastIndexOf(ss));
				String[] firstEventNextSplit = 
					(!firstEventNext.contains(ss)) ? new String[]{firstEventNext} : 
					firstEventNext.split("\\" + ss);
				// Meter change found? Add scoreDef after bar
				if (MensurationSign.getMensurationSign(firstEventNextSplit[0]) != null) {
					String meterStr = "";
					for (String s : meters.get(meterIndex+1)) {
						if (!s.equals("")) {
							meterStr += s + " ";
						}
					}
					meterStr = meterStr.trim();
					String scoreDef = INDENT_TWO + "<scoreDef" + " " + meterStr + "/>" + "\r\n";
					sb.append(scoreDef);
					measuresTab.add(scoreDef);
					meterIndex++;
				}
			}
		}

//		System.out.println("= = = = = = = = = = = = = =");
//		for (String s : measures) {
//			System.out.println(s);
//		}
//		System.out.println("= = = = = = = = = = = = = =");
//		System.exit(0);
		res = res.replace(INDENT_TWO + "section_content_placeholder", sb.toString());
		ToolBox.storeTextFile(res, new File(path + ".xml"));
	}


	/**
	 * Gets the XML duration (1 for a whole note, 2 for a half note, 4 for a quarter note, etc.)
	 * of the given event.
	 * 
	 * @param event
	 * @return An <code>Integer[]</code> containing
	 *         <ul> 
	 *         <li>as element 0: the XML duration</li>
	 *         <li>as element 1: the XML dots</li>
	 *         </ul> 
	 *         or <code>null</code> if the given event does not start with a RhythmSymbol.
	 */
	// TESTED
	static Integer[] getXMLDur(String event) {
		Integer[] XMLDur = null;

		String ss = Symbol.SYMBOL_SEPARATOR;
		// To make sure that it always works if the trailing SS has been removed from event
		RhythmSymbol rs = 
			(!event.contains(ss)) ?	RhythmSymbol.getRhythmSymbol(event) : 
			RhythmSymbol.getRhythmSymbol(event.substring(0, event.indexOf(ss)));

		if (rs != null) {
			int dots = rs.getNumberOfDots();
			int dottedDur = rs.getDuration();
			int undottedDur;
			// Get undotted version if applicable 
			if (dots != 0) {
//				rs = rs.getUndotted();
				// one dot:  dur = 1 * origDur + 1/2 * origDur 
				//           --> origDur = dur / (1 + 1/2) 
				// two dots: dur = 1 * origDur + 1/2 * origDur + 1/4 * origDur
				//           --> origDur = dur / (1 + 1/2 + 1/4)
				double multiplier = 1;
				for (int i = 0; i < dots; i++) {
					multiplier += (1.0 / (2 * (i+1)));
				}
				undottedDur = (int) (dottedDur / multiplier); 
			}
			else {
				undottedDur = dottedDur;
			}
			Rational rsAsRat = Tablature.SMALLEST_RHYTHMIC_VALUE.mul(undottedDur);
//			Rational rsAsRat = Tablature.SMALLEST_RHYTHMIC_VALUE.mul(rs.getDuration());
			int durXML = rsAsRat.getDenom();
			XMLDur = new Integer[]{durXML, dots};
		}
		return XMLDur;
	}


	/**
	 * Converts each event in the given list of events into an XML tabGrp, and concatenates
	 * this tabGrp as a list of <code>String</code>s to the List that is returned.
	 * 
	 * @param events The list of events.
	 * @param prevXMLDur An <code>Integer[]</code> containing the last XML duration (element 0)
	 *                   and number of dots (element 1) encountered before the events in the 
	 *                   given list.
	 * @param isCorrected Whether or not the tabGrp is part of a corrected tabGrp (i.e., is
	 *        embedded in a <sic> or <corr> tag)
	 * @param tss The TabSymbolSet.
	 * @return A list of tabGrps, each of them formatted as a list of <code>String</code>s.
	 */
	// TODO test
	private static List<String> getTabGrps(List<String> events, Integer[] prevXMLDur, 
		boolean isCorrected, TabSymbolSet tss) {
		List<String> tabGrpList = new ArrayList<>();
		
		// If the tabGrp is part of a <sic>/<corr> pair, two extra tabs must be added: 
		// one for the <choice> tag, and one for the <sic>/<corr> tag
		int addedTabs = isCorrected ? 2 : 0;

		String ss = Symbol.SYMBOL_SEPARATOR;
		for (int i = 0; i < events.size(); i++) {
//			System.out.println("i = " + i);
			String e = events.get(i);
			String[] currEventSplit = 
				(!e.contains(ss)) ? new String[]{e} : e.split("\\" + ss);
//			System.out.println(Arrays.asList("css = " + Arrays.toString(currEventSplit)));
			// If the event is not an MS event (which consist of one or multiple MS)
			if (MensurationSign.getMensurationSign(currEventSplit[0]) == null) {
				// Determine previous (last active) duration
				int dur = prevXMLDur[XML_DUR_IND];
				int dots = prevXMLDur[XML_DOTS_IND];

				// Determine current duration and update prevDurXML accordingly
				Integer[] currXMLDur = getXMLDur(String.join(ss, currEventSplit));
				if (currXMLDur != null) {
					dur = currXMLDur[XML_DUR_IND];
					dots = currXMLDur[XML_DOTS_IND];
					prevXMLDur = currXMLDur;
				}

				// Make tabGrp
				// 1. <tabGrp>
				String tabGrpID = "";
				tabGrpList.add( 
					TAB.repeat(2 + addedTabs) + "<" + 
					String.join(" ", "tabGrp", "xml:id='" + tabGrpID + "'", "dur='" + dur + "'") + 
					((dots > 0) ? (" dots='" + dots + "'") : "") + ">");
				// 2. <tabDurSym> (also covers rests)
				if (currXMLDur != null) {
					String tabDurSymID = "";
					tabGrpList.add( 
						TAB.repeat(3 + addedTabs) + "<" + 
						String.join(" ", "tabDurSym", "xml:id='" + tabDurSymID + "'") + "/>");
				}
				// 3. <note>s (rests are covered by the tabDurSym)
				for (int j = ((currXMLDur != null) ? 1 : 0); j < currEventSplit.length; j++) {
					TabSymbol ts = TabSymbol.getTabSymbol(currEventSplit[j], tss);
//					System.out.println("j = " + j);
//					System.out.println(currEventSplit[j]);
//					System.out.println(ts);
					String noteID = "";
					tabGrpList.add( 
						TAB.repeat(3 + addedTabs) + "<" + 
						String.join(" ", "note", "xml:id='" + noteID + "'", "tab.course='" + 
						ts.getCourse() + "'", "tab.fret='" + ts.getFret() + "'") + "/>");			
				}
				tabGrpList.add(TAB.repeat(2 + addedTabs) + "</" + "tabGrp" + ">");
			}
		}
		return tabGrpList;
	}


	private static int getStaffNum(boolean tablatureCase, boolean grandStaff, boolean tabOnTop, 
		int numVoices, int voice) {
		int staff;
		// Options: ONLY_TAB, TAB_AND_TRANS && tabOnTop, TAB_AND_TRANS && !tabOnTop
		if (tablatureCase) {
			if (ONLY_TAB || (TAB_AND_TRANS && tabOnTop)) {
				staff = 1;
			}
			else {
				staff = grandStaff ? 3 : numVoices + 1;
			}
//			return ONLY_TAB ? 1 : (tabOnTop ? 1 : (grandStaff ? 3 : numVoices + 1));
		}
		// Options: ONLY_TRANS, TAB_AND_TRANS && tabOnTop, TAB_AND_TRANS && !tabOnTop
		else {
			staff = grandStaff ? getStaffAndLayer(numVoices)[voice][0] : voice+1;
			if (TAB_AND_TRANS && tabOnTop) {
				staff++;
			}
		}
		return staff;
	}


	/**
	 * Makes a scoreDef for the given mi and ki.
	 * 
	 * @param currMi
	 * @param currKi
	 * @param tss
	 * @param tabMs 
	 * @param grandStaff
	 * @boolean tabOnTop
	 * @param bar
	 * @param numVoices
	 * @return
	 */
	private static List<String> makeScoreDef(Integer[] currMi, Integer[] currKi, TabSymbolSet 
		tss, String tabMs, boolean grandStaff, boolean tabOnTop, int bar, int numVoices) {
		// The <scoreDef> contains a <staffGrp>, which contains one (TAB_ONLY case) or more 
		// (other cases) multiple <staffDef>s. In the TAB_AND_TRANS case, the <staffDef>s for 
		// the CMN are wrapped in another <staffGrp> as to enable across-staff barlines
		//
		// TAB_ONLY case
		// <scoreDef>
		//     <staffGrp>
		//         <staffDef> ... </staffDef>
		//     </staffGrp>
		// <scoreDef>/
		//
		// TRANS_ONLY case
		// <scoreDef>
		//     <staffGrp symbol='bracket' bar.thru='true'>
		//         <staffDef> ... </staffDef>
		//         ...
		//         <staffDef> ... </staffDef>
		//     </staffGrp>
		// <scoreDef>/
		//
		// TAB_AND_TRANS case (with tab on top)
		// <scoreDef>
		//     <staffGrp>
		//         <staffDef> ... </staffDef>
		//         <staffGrp symbol='bracket' bar.thru='true'>
		//             <staffDef> ... </staffDef> 
		//             ...
		//             <staffDef> ... </staffDef>
		//         </staffGrp>
		//     </staffGrp>
		// <scoreDef>/	
		boolean includeKey = currKi != null; 
		boolean includeMeter = currMi != null;

		int diminution = 0;
		int count = currMi[Timeline.MI_NUM];
		int unit = currMi[Timeline.MI_DEN];
		if (adaptCMNDur) {
			if (ONLY_TAB || TAB_AND_TRANS) {
				diminution = currMi[Timeline.MI_DIM];
				Rational undiminutedMeter = 
					Timeline.undiminuteMeter(new Rational(count, unit), diminution);
				count = undiminutedMeter.getNumer();
				unit = undiminutedMeter.getDenom();
			}
		}
		String sym = "";
		if (includeMeter) {
			if (count == 4 && unit == 4) {
//			if (currMi[Transcription.MI_NUM] == 4 && currMi[Transcription.MI_DEN] == 4) {
				sym = " sym='common'";
			}
			else if (count == 2 && unit == 2) {
//			else if (currMi[Transcription.MI_NUM] == 2 && currMi[Transcription.MI_DEN] == 2) {
				sym = " sym='cut'";
			}
		}

		List<String> scoreDefContent = new ArrayList<>();
		// Make staffGrpTab
		List<String> staffGrpTab = new ArrayList<>();
		if (ONLY_TAB || TAB_AND_TRANS) {
			String notationtypeStr = getNotationTypeStr(tss);
			String tuningStr = "lute.renaissance.6"; // TODO parameterise
			int staff = getStaffNum(true, grandStaff, tabOnTop, numVoices, -1);
			staffGrpTab.add("<staffDef n='" + staff + "' lines='6' notationtype='" + notationtypeStr + 
				"' tab.dur.sym.ratio='" + diminution + "' xml:id='s1'>"); 
			// Add tuning (assumed not to change throughout the piece)
			if (bar == 1) {
				staffGrpTab.add(TAB + "<tuning tuning.standard='" + tuningStr + "'/>");
			}
			// Add meterSig (if appropriate) 
			if (includeMeter) {
				if (tabMs != null) {
//					int num = currMi[Transcription.MI_NUM];
//					int den = currMi[Transcription.MI_DEN];
					// In case of a triple meter, show only the count (always 3)
					if (count == 6) {
						count /= 2;
						unit /= 2;
					}
					staffGrpTab.add(TAB + "<meterSig " + "count='" + count + "'" + " " + 
						"unit='" + unit + "'" + (count != 3 ? sym : " form='num'") + "/>");
//					staffGrpTab.add(TAB + "<mensur " + "num='" + currMi[Transcription.MI_NUM] + "'" +
//						" " + "numbase='" + currMi[Transcription.MI_DEN] + "'" + "/>");
				}
			}
			staffGrpTab.add("</staffDef>");
		}
		// Make staffGrpTrans
		List<String> staffGrpTrans = new ArrayList<>(); 
		if (ONLY_TRANS || TAB_AND_TRANS) {
			int numStaffs = grandStaff ? 2 : numVoices;
			int firstStaff = getStaffNum(false, grandStaff, tabOnTop, numVoices, 0);
			String keySig = null;
			if (includeKey) {
				int ks = currKi[Transcription.KI_KEY];
				String ksStr = String.valueOf(ks) + (ks == 0 ? "" : ((ks < 0) ? "f" : "s"));
				keySig = 
					"<keySig " + "sig='" + ksStr + "'" + " " + "mode='" + 
					(currKi[Transcription.KI_MODE] == 0 ? "major" : "minor") + "'" + "/>";
			}
			String meterSig = null;
			if (includeMeter) {
				meterSig = 
					"<meterSig " + "count='" + count + "'" + " " + "unit='" + unit + "'" + 
					sym + "/>";
			}	
			staffGrpTrans.add("<staffGrp symbol='bracket' bar.thru='true'>");
			for (int i = 0; i < numStaffs; i++) {
				staffGrpTrans.add(TAB + "<staffDef n='" + (firstStaff + i) + "' lines='5'>");
				// Add clef (assumed not to change throughout the piece)
				if (bar == 1) {
					String[] clef = getCleffing(i, numStaffs, grandStaff);	
					staffGrpTrans.add(TAB + TAB +
						"<clef shape='" + clef[0] + "' line='" + clef[1] + "'/>");
				}
				// Add keySig (if appropriate) 
				if (includeKey) {
					staffGrpTrans.add(TAB + TAB + keySig);
				}
				// Add meterSig (if appropriate) 
				if (includeMeter) {
					staffGrpTrans.add(TAB + TAB + meterSig);		
				}
				staffGrpTrans.add(TAB + "</staffDef>");
			}
			staffGrpTrans.add("</staffGrp>");
		}

		// Shift lines and wrap total into <staffGrp> (if appropriate) and <scoreDef>
		if (ONLY_TAB) {
			for (int i = 0; i < staffGrpTab.size(); i++) {
				staffGrpTab.set(i, TAB.repeat(2) + staffGrpTab.get(i));
			}
			scoreDefContent = staffGrpTab;
			scoreDefContent.add(0, TAB + "<staffGrp>");
			scoreDefContent.add(TAB + "</staffGrp>");
		}
		else if (ONLY_TRANS) {
			for (int i = 0; i < staffGrpTrans.size(); i++) {
				staffGrpTrans.set(i, TAB + staffGrpTrans.get(i));
			}
			scoreDefContent = staffGrpTrans;
		}
		else if (TAB_AND_TRANS) {
			for (int i = 0; i < staffGrpTab.size(); i++) {
				staffGrpTab.set(i, TAB.repeat(2) + staffGrpTab.get(i));
			}
			for (int i = 0; i < staffGrpTrans.size(); i++) {
				staffGrpTrans.set(i, TAB.repeat(2) + staffGrpTrans.get(i));
			}
			if (tabOnTop) {
				scoreDefContent.addAll(staffGrpTab);
				scoreDefContent.addAll(staffGrpTrans);
			}
			else {
				scoreDefContent.addAll(staffGrpTrans);
				scoreDefContent.addAll(staffGrpTab);
			}
			scoreDefContent.add(0, TAB + "<staffGrp>");
			scoreDefContent.add(TAB + "</staffGrp>");
		}
		scoreDefContent.add(0, "<scoreDef>");
		scoreDefContent.add("</scoreDef>");

		return scoreDefContent;
	}


	/**
	 * Exports the given Transcription as an MEI file, saved at the given path.
	 * 
	 * @param trans Must be a Transcription created setting the encodingFile argument to null
	 *              (i.e., one that has basicNoteProperties).
	 * @param tab
	 * @param mismatchInds
	 * @param grandStaff
	 * @param tabOnTop
	 * @param path
	 */
	public static void exportMEIFile(Transcription trans, Tablature tab, List<List<Integer>> 
		mismatchInds, boolean grandStaff, boolean tabOnTop, boolean alignWithMetricBarring, 
		String path) {
//\\		System.out.println("\r\n>>> MEIExport.exportMEIFile() called");

		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template-MEI.xml"));

//		Integer[][] bnp = trans.getBasicNoteProperties();
//		for (int i : new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}) {
//			System.out.println(Arrays.toString(bnp[i]));
//		}
//		System.exit(0);
		
		if (tab != null) {
			if (trans == null) ONLY_TAB = true ; else TAB_AND_TRANS = true;
		}
		else {
			ONLY_TRANS = true;
		}
		List<Integer[]> tabBarsToMetricBars = (tab != null) ? tab.mapTabBarsToMetricBars() : null;
		// mi from tab is the same as mi from trans in TAB_AND_TRANS case
		List<Integer[]> mi = (tab != null) ? tab.getTimeline().getMeterInfo() : trans.getMeterInfo();
		int numMetricBars = mi.get(mi.size()-1)[Timeline.MI_LAST_BAR];
		int numTabBars = (tab != null) ? tabBarsToMetricBars.size() : -1;
		int numBars = !alignWithMetricBarring ? numTabBars : numMetricBars;
		Integer[][] btp = tab != null ? tab.getBasicTabSymbolProperties() : null;
		List<Integer[]> ki = trans != null ? trans.getKeyInfo() : null;
		if (ki != null && ki.size() == 0) { // TODO zondag
			ki.add(new Integer[]{-2, 0, 0, 0, 0, 0, 1});
		}
		List<Rational[]> tripletOnsetPairs = tab != null ? tab.getTripletOnsetPairs() : null; 
		String pieceName = tab != null ? tab.getPiecename() : trans.getPieceName();
		int numVoices = trans != null ? trans.getNumberOfVoices() : 0;
		List<String[]> tabMensSigns = tab != null ? tab.getMensurationSigns() : null;
		
		// List all bars in which the meter, or, if appropriate, the key changes 
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, Timeline.MI_FIRST_BAR);
		// Adapt meterChangeBars if needed
		if (ONLY_TAB && !alignWithMetricBarring) {
			List<Integer> meterChangeBarsTab = new ArrayList<>();
			for (int i : meterChangeBars) {
				int indexInTbtmb = ToolBox.getItemsAtIndex(tabBarsToMetricBars, 1).indexOf(i);
				meterChangeBarsTab.add(tabBarsToMetricBars.get(indexInTbtmb)[Tablature.TAB_BAR_IND]);
			}
			meterChangeBars = meterChangeBarsTab;
		}
		List<Integer> diminutions = 
			tab != null ? ToolBox.getItemsAtIndex(mi, Timeline.MI_DIM) : null;
		List<Integer> keyChangeBars = 
			ki != null ? ToolBox.getItemsAtIndex(ki, Transcription.KI_FIRST_BAR) : 
			new ArrayList<>();
		// Combine and remove any duplicates
		List<Integer> sectionBars = new ArrayList<>(meterChangeBars);
		sectionBars.addAll(new ArrayList<>(keyChangeBars));
		sectionBars = sectionBars.stream().distinct().collect(Collectors.toList());

		// Make the <meiHead> 
		String[] meiHead = new String[MEI_HEAD.size()];
		meiHead[MEI_HEAD.indexOf("title")] = pieceName;
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		// Make the <music> 
		// 1. Make the <scoreDef>s as strings (one for each time the meter or key changes) 
		List<String> scoreDefsAsStr = new ArrayList<>();
		for (int i = 0; i < sectionBars.size(); i++) {
			int bar = sectionBars.get(i);
			Integer[] currMi = 
				meterChangeBars.contains(bar) ? mi.get(meterChangeBars.indexOf(bar)) : null;
			Integer[] currKi = 
				keyChangeBars.contains(bar) ? ki.get(keyChangeBars.indexOf(bar)) : null;
			
			String tabMs = null;
			if (tabMensSigns != null) {
				for (String[] s : tabMensSigns) {
					String barAsStr = ONLY_TAB && !alignWithMetricBarring ? s[1] : s[2];
					if (Integer.parseInt(barAsStr) == bar) {
						tabMs = s[0];
						break;
					}
				}
			} 	
			List<String> currScoreDef = 
				makeScoreDef(currMi, currKi, tab != null ? tab.getEncoding().getTabSymbolSet() : 
				null, tabMs, grandStaff, tabOnTop, bar, numVoices);
			StringBuilder currScoreDefStr = new StringBuilder();
			for (String s : currScoreDef) {
				// Any <scoreDef> after the first is placed inside its <section> instead of
				// before it, and therefore must be shifted by INDENT_ONE + TAB 
				currScoreDefStr.append((i == 0 ? INDENT_ONE : INDENT_ONE + TAB) + s + "\r\n");
			}
//			for (int j = 0; j < currScoreDef.size(); j++) {
//				currScoreDefStr.append(INDENT_ONE + currScoreDef.get(j) + 
//				(j < currScoreDef.size()-1 ? "\r\n" : ""));
//			}
			scoreDefsAsStr.add(currScoreDefStr.toString());
		}
//		for (String s : scoreDefsAsStr) {
//			System.out.println("--------------------------");
//			System.out.println(s);
//		}
//		System.exit(0);

//		// List any successive meters and key signatures, which go as attributes into 
//		// a new <scoreDef>
//		List<String[]> nonInitMeters = new ArrayList<>();
//		for (Integer[] in : mi.subList(1, mi.size())) {
//			String sym = "";
//			if (in[Transcription.MI_NUM] == 4 && in[Transcription.MI_DEN] == 4) {
//				sym = " meter.sym='common'";
//			}
//			else if (in[Transcription.MI_NUM] == 2 && in[Transcription.MI_DEN] == 2) {
//				sym = " meter.sym='cut'";
//			}
//			nonInitMeters.add(new String[]{
//				"meter.count='" + in[Transcription.MI_NUM] + "'", 
//				"meter.unit='" + in[Transcription.MI_DEN] + "'",
//				sym}
//			);
//		}

//		Integer[] kiInit = (ki.size() == 0) ? new Integer[]{-2, 0, 0, 0, 0, 0, 1} : ki.get(0);
//		Integer[] kiInit = ki.get(0);
		
//		String ksInit = 
//			Math.abs(kiInit[Transcription.KI_KEY]) + 
//			(kiInit[Transcription.KI_KEY] < 0 ? "f" : "s");
		
//		List<String[]> nonInitKeys = new ArrayList<>();
//		for (Integer[] in : ki.subList(1, mi.size())) {
//			nonInitKeys.add(new String[]{
//				"key.sig='" + Math.abs(in[Transcription.KI_KEY]) + 
//				(in[Transcription.KI_KEY] < 0 ? "f" : "s") + "'",
//				"key.mode='major'"}
//			);
//		}
//		List<Integer> keyChangeBars = 
//			ToolBox.getItemsAtIndex(ki, Transcription.KI_FIRST_BAR).subList(1, ki.size());

		// 2. Make the bars as strings
		// a. Tab bars
		List<String> tabBarsAsStr = new ArrayList<>();
		if (ONLY_TAB || TAB_AND_TRANS) {
			List<List<String>> tabBars = 
				getTabBars(tab, alignWithMetricBarring, getStaffNum(true, grandStaff, 
				tabOnTop, numVoices, -1));
			for (int i = 0; i < numBars; i++) {
				StringBuilder currTabBarAsStr = new StringBuilder();
				for (String s : tabBars.get(i)) {
					currTabBarAsStr.append(INDENT_TWO + TAB + s + "\r\n");
				}
//				for (int j = 0; j < tabBars.get(i).size(); j++) {
//					currTabBarAsStr.append(
//						INDENT_TWO + TAB + tabBars.get(i).get(j) + 
//						(j < tabBars.get(i).size() - 1 ? "\r\n" : ""));
//				}
				tabBarsAsStr.add(currTabBarAsStr.toString());
			}
//			System.out.println(tabBarsAsStr.size());
//			for (String s : tabBarsAsStr) {
//				System.out.println(s);
//				System.out.println("===================");
//			}
//			System.exit(0);			
		}
		// b. Trans bars
		List<String> transBarsAsStr = new ArrayList<>();
		if (ONLY_TRANS || TAB_AND_TRANS) {
			List<Object> data = getData(tab, trans, mi, ki, tripletOnsetPairs);
			List<List<String>> transBars =
				getTransBars(data, tab, mi, tripletOnsetPairs, mismatchInds, grandStaff, tabOnTop,
				numVoices);	
			for (int i = 0; i < numMetricBars; i++) {
				StringBuilder currTransBarAsStr = new StringBuilder();
				for (String s : transBars.get(i)) {
					currTransBarAsStr.append(INDENT_TWO + TAB + s + "\r\n");
				}
//				for (int j = 0; j < transBars.get(i).size(); j++) {
//					currTransBarAsStr.append(
//						INDENT_TWO + TAB + transBars.get(i).get(j) + 
//						(j < transBars.get(i).size() - 1 ? "\r\n" : ""));
//				}
				transBarsAsStr.add(currTransBarAsStr.toString());
			}					
//			System.out.println(transBarsAsStr.size());
//			for (String s : transBarsAsStr) {
//				System.out.println(s);
//				System.out.println("===================");
//			}
//			System.exit(0);
		}

		// 3. Make the <section>s as strings. Each <section> consists of a <scoreDef> 
		// followed by its bars (as made above) wrapped in <measure>s. The first <section> 
		// has the (main) <scoreDef> preceding it; any subsequent <section>s have their 
		// <scoreDef> as their first child		
		List<String> sectionsAsStr = new ArrayList<>();
		StringBuilder currSection = new StringBuilder();
		for (int i = 0; i < numBars; i++) {
			int currBar = i + 1;
			int nextBar = currBar + 1;
			boolean lastBarInSection = sectionBars.contains(nextBar) || currBar == numBars;
			if (sectionBars.contains(currBar)) {
				int sectionInd = sectionBars.indexOf(currBar);
				// First <scoreDef> and <section>: <scoreDef> precedes <section>
				if (sectionInd == 0) {
					currSection.append(scoreDefsAsStr.get(sectionInd));
					currSection.append(INDENT_ONE + "<section n='" + (sectionInd+1) + "'>" + "\r\n");
				}
				// Subsequent <scoreDef>s and <section>s: <scoreDef> first child of <section>
				else {
					currSection.append(INDENT_ONE + "<section n='" + (sectionInd+1) + "'>" + "\r\n");
					currSection.append(scoreDefsAsStr.get(sectionInd));
				}
			}
			String barline = 
				lastBarInSection ? (currBar == numBars ? " right='end'" : " right='dbl'") : "";
			currSection.append(INDENT_TWO + "<measure n='" + (i+1) + "'" + barline + ">" + "\r\n");
			String tabBar = tabBarsAsStr.get(i);
			String transBar = transBarsAsStr.get(i);
			if (ONLY_TAB) {
				currSection.append(tabBar);
			}
			else if (ONLY_TRANS) {
				currSection.append(transBar);
			}
			else if (TAB_AND_TRANS) {
				if (tabOnTop) {
					currSection.append(tabBar);
					currSection.append(transBar);
				}
				else {
					currSection.append(transBar);
					currSection.append(tabBar);
				}
			}
			currSection.append(INDENT_TWO + "</measure>" + "\r\n");
			// Last bar in section or last bar in piece? Add to sectionsAsStr
			if (lastBarInSection) {
				currSection.append(INDENT_ONE + "</section>" + "\r\n");
				sectionsAsStr.add(currSection.toString());
				currSection = new StringBuilder();
			}
		}
//		for (String s : sectionsAsStr) {
//			System.out.println(s);
//			System.out.println("------------------");
//		}

		// 4. Combine the <section>s into the <score> content
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sectionBars.size(); i++) {
			sb.append(sectionsAsStr.get(i));
		}
		res = res.replace(INDENT_ONE + "score_placeholder" + "\r\n", sb.toString());

		ToolBox.storeTextFile(res, 
			new File(path + "-" + (grandStaff ? "grand_staff" : "score") + ".xml"));
	}


	private static List<List<String>> getTabBars(Tablature tab, boolean alignWithMetricBarring,
		int staff) {
		List<List<String>> tabBars = new ArrayList<>();
		
		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();
		TabSymbolSet tss = tab.getEncoding().getTabSymbolSet();
		List<Integer[]> mi = tab.getTimeline().getMeterInfo();
//		List<String[]> meters = new ArrayList<>();
//		int meterIndex = 0;
		List<Integer[]> tabBarsToMetricBars = tab.mapTabBarsToMetricBars();

//		List<Integer> xmlDurPerBar = new ArrayList<>();
//		Rational prevDur = null;
//		for (List<String[]> bar : ebf) {
//			Rational currXmlDur = Rational.ZERO;
//			int barNum = Integer.parseInt(bar.get(0)[1]);
//			for (String[] eventInfo : bar) {
//				String event = eventInfo[0];
//				System.out.println(event);
//				Integer[] xmlDurDots = getXMLDur(event);
//				String first = event.substring(0, event.indexOf(ss));
//				// If the event is not a barline event or a MS event
//				if (!ConstantMusicalSymbol.isBarline(first)
//					&& MensurationSign.getMensurationSign(first) == null) {
//					// If the event starts with a RS
//					Rational xmlDur = null;
//					if (RhythmSymbol.getRhythmSymbol(first) != null) {
//						xmlDur = new Rational(1, xmlDurDots[0]);
//						// Add the value for any dots
//						for (int i = 0; i < xmlDurDots[1]; i++) {
//							xmlDur = xmlDur.add(xmlDur.mul(new Rational(1, 2)));
//						}
//						prevDur = xmlDur;
//					}
//					// If the event does not start with a RS
//					else {
//						xmlDur = prevDur; 
//					}
//					currXmlDur = currXmlDur.add(xmlDur);
//				}
//			}
//			// Apply diminution
//			int finalXmlDur = (int) (currXmlDur.div(new Rational(1, 8)).toDouble());
//			List<Integer> diminutions = ToolBox.getItemsAtIndex(mi, Tablature.MI_DIM);
//			List<Rational[]> meterChangesByMetricPos = new ArrayList<>();
//			Rational firstMetPos = Rational.ZERO;
//			for (Integer[] in : mi) {
//				int numBarsInMeter = 
//					in[Transcription.MI_LAST_BAR] - in[Transcription.MI_FIRST_BAR] + 1;
//				Rational meter = new Rational(in[Transcription.MI_NUM], in[Transcription.MI_DEN]);
//				System.out.println(meter);
//				System.out.println(numBarsInMeter);
//				Rational lastMetPos = firstMetPos.add(meter.mul(numBarsInMeter));
//				meterChangesByMetricPos.add(new Rational[]{firstMetPos, lastMetPos});
//				firstMetPos = lastMetPos;
//			}
//			for (Rational[] r : meterChangesByMetricPos) {
//				System.out.println(Arrays.toString(r));
//			}
//			int dim = 1;
//			
//			xmlDurPerBar.add(finalXmlDur);
//		}
//		System.out.println(xmlDurPerBar);

		// For each bar
		List<String> currBarAsXML = new ArrayList<>();
		Integer[] prevDurXML = new Integer[]{0, 0};
		boolean startNewBar = true;
		// Get events; remove any decorative opening barlines (affecting XML bar numbering)
		List<Event> events = 
			Encoding.removeDecorativeBarlineEvents(tab.getEncoding().getEvents());
		// Organise events per bar
		List<List<Event>> eventsPerBar = new ArrayList<>();
		int currBar = events.get(0).getBar();
		List<Event> eventsBar = new ArrayList<>();
		for (int i = 0; i < events.size(); i++) {
			// Next bar or final event: add
			if (events.get(i).getBar() == currBar + 1 || i == events.size() - 1) {
				eventsPerBar.add(eventsBar);
				eventsBar = new ArrayList<>();
				currBar++;
			}
			eventsBar.add(events.get(i));		
		}
//		for (Event l : eventsPerBar.get(0)) {
//			System.out.println(l.getEncoding());
//		}
//		System.exit(0);
		for (int i = 0; i < eventsPerBar.size(); i++) {
			if (startNewBar) {
				currBarAsXML = new ArrayList<>();
				currBarAsXML.add("<staff n='" + staff + "'>");
				currBarAsXML.add(TAB + "<layer n='1'>");
			}
			List<Event> currBarEvents = eventsPerBar.get(i);
//			int currTabBar = events.get(i).getBar();
			int currTabBar = currBarEvents.get(0).getBar();
//			List<Event> currBarEvents = new ArrayList<>();
//			for (int j = i; j < events.size(); j++) {
//				if (events.get(j).getBar() == currTabBar + 1) {
//					i = j - 1;
//					break;
//				}
//				currBarEvents.add(events.get(j));
//			}
			int currMetricBar = tabBarsToMetricBars.get(currTabBar-1)[Tablature.METRIC_BAR_IND];
			int currDim = tab.getTimeline().getDiminution(currMetricBar);

			// For each event		
			for (int j = 0; j < currBarEvents.size(); j++) {
				Event currEventFull = currBarEvents.get(j);
//				String[] currEventFull = currBarEvents.get(j);
				String currEvent = 
					removeTrailingSymbolSeparator(currEventFull.getEncoding());
//				String currEvent = 
//					removeTrailingSymbolSeparator(currEventFull[Encoding.EVENT_IND]);
				String currEventOrig = currEventFull.getFootnote();
//				String currEventOrig = currEventFull[Encoding.FOOTNOTE_IND];
				// Extract correction
				boolean isCorrected = false;
				if (currEventOrig != null) {
					if (currEventOrig.contains("'")) {
						currEventOrig = currEventOrig.substring(currEventOrig.indexOf("'")+1,
							currEventOrig.lastIndexOf("'"));
						isCorrected = true;
					}
					else {
						currEventOrig = 
							currEventOrig.substring(currEventOrig.indexOf(Encoding.FOOTNOTE_INDICATOR) + 1);
					}
					currEventOrig = removeTrailingSymbolSeparator(currEventOrig);
				}

//				// Barline? End of bar reached; set barline if not single
//				if (ConstantMusicalSymbol.isBarline(currEvent)) {
//					// TODO currently only single and double barline possible in MEI
//					if (currEvent.equals(ConstantMusicalSymbol.DOUBLE_BARLINE.getEncoding())) {
//						barline = " right='dbl'";
//					}
//					if (i == ebf.size()-1) {
//						barline = " right='end'";
//					}
//				}
				if (Symbol.getConstantMusicalSymbol(currEvent) != null && Symbol.getConstantMusicalSymbol(currEvent).isBarline()) {
//				if (!ConstantMusicalSymbol.isBarline(currEvent)) {
					// Get XML durations of currEvent, and, if applicable, currEventOrig
					Integer[] currDurXML = getXMLDur(currEvent);

					String sicEvent = !isCorrected ? currEvent : currEventOrig;
					String corrEvent = !isCorrected ? null : currEvent;

					boolean defaultCase = !isCorrected; 
					boolean oneReplacedByMultiple = isCorrected && sicEvent.endsWith(sp);
					boolean multipleReplacedByOne = 
						isCorrected && sicEvent.contains(sp) && !sicEvent.endsWith(sp);
					boolean defaultCorrectedCase = 
						isCorrected && !oneReplacedByMultiple && !multipleReplacedByOne;

					List<String> sicList = new ArrayList<>();
					List<String> corrList = new ArrayList<>();
					// No <sic> and <corr>
					if (defaultCase) {
						sicList.add(sicEvent);
					}
					// Both <sic> and <corr> contain one <tabGrp>
					if (defaultCorrectedCase) {
						sicList.add(sicEvent);
						corrList.add(corrEvent);
					}
					// <sic> contains multiple <tabGrp>s, <corr> one 
					if (multipleReplacedByOne) {
						for (String s : sicEvent.split(sp + ss)) {
							sicList.add(removeTrailingSymbolSeparator(s));
						}
						corrList.add(corrEvent);
					}
					// <sic> contains one <tabGrp>, <corr> multiple: corrList contains corrEvent 
					// plus all events following that together have the same duration as durSic
					// NB It is assumed that the replacement is within the bar
					if (oneReplacedByMultiple) {
						sicEvent = sicEvent.substring(0, sicEvent.indexOf(sp));
						sicList.add(removeTrailingSymbolSeparator(sicEvent));
						corrList.add(corrEvent);
						RhythmSymbol rsSic = 
							RhythmSymbol.getRhythmSymbol(sicEvent.substring(0, sicEvent.indexOf(ss)));
						int durSic; 
						if (rsSic != null) {
							durSic = rsSic.getDuration();
						}
						else {
							durSic = -1; // TODO get last specified duration before currEvent
						}
						RhythmSymbol rsCorr = 
							RhythmSymbol.getRhythmSymbol(corrEvent.substring(0, corrEvent.indexOf(ss)));
						int durCorr;
						if (rsCorr != null) {
							durCorr = rsCorr.getDuration();
						}
						else {
							durCorr = -1; // TODO get last specified duration before currEvent
						}
						int durCorrToTrack = durCorr;
						// Iterate through next events until durCorr equals durSic
						for (int l = j+1; l < currBarEvents.size(); l++) {
							Event nextEventFull = currBarEvents.get(l);
//							String[] nextEventFull = currBarEvents.get(l);
							String nextEvent = 
								removeTrailingSymbolSeparator(nextEventFull.getEncoding());
//							String nextEvent = 
//								removeTrailingSymbolSeparator(nextEventFull[Encoding.EVENT_IND]);
							String nextEventOrig = nextEventFull.getFootnote();
//							String nextEventOrig = nextEventFull[Encoding.FOOTNOTE_IND];
							// If the next element has a footnote
							if (nextEventOrig != null) {
								nextEventOrig = removeTrailingSymbolSeparator(nextEventOrig);
								corrList.add(nextEvent);
								// Determine duration of corrected event, increment durCorr,
								// and update durrCorrToTrack
								RhythmSymbol nextEventRS = 
									RhythmSymbol.getRhythmSymbol(nextEvent.substring(0, 
									nextEvent.indexOf(ss)));
								int durCorrNext;
								if (nextEventRS != null) {
									durCorrNext = nextEventRS.getDuration();
									durCorrToTrack = durCorrNext;
								}
								else {
									durCorrNext = durCorrToTrack;
								}
								durCorr += durCorrNext;		
							}
							if (durCorr == durSic) {
								int eventsToSkip = corrList.size() - 1;
								j += eventsToSkip;
								break;
							}
						}
					}

					List<String> currEventAsXML = new ArrayList<>();
					if (isCorrected) {
						currEventAsXML.add(TAB.repeat(2) + "<choice>");
						currEventAsXML.add(TAB.repeat(3) + "<sic>");
					}
					currEventAsXML.addAll(getTabGrps(sicList, prevDurXML, isCorrected, tss));
					if (isCorrected) {
						currEventAsXML.add(TAB.repeat(3) + "</sic>");
					}
					if (isCorrected) {
						currEventAsXML.add(TAB.repeat(3) + "<corr>");
						currEventAsXML.addAll(getTabGrps(corrList, prevDurXML, isCorrected, tss));
						currEventAsXML.add(TAB.repeat(3) + "</corr>");
					}
					if (isCorrected) {
						currEventAsXML.add(TAB.repeat(2) + "</choice>");
					}
					currBarAsXML.addAll(currEventAsXML);
					
//					// Add duration
//					if (eventAsXML.contains("dur='")) {
//						// Remove any <sic> part 
//						if (eventAsXML.contains("<sic>")) {
//							int firstInd = eventAsXML.indexOf("<sic>");
//							int secondInd = eventAsXML.indexOf("</sic>" + "\r\n");
//							eventAsXML = null;
//						}
//						int firstInd = eventAsXML.indexOf("dur='") + "dur='".length();
//						int secondInd = eventAsXML.indexOf("'", firstInd); 
//						// As whole note
//						double dur = 1.0 / Integer.parseInt(eventAsXML.substring(firstInd, secondInd));
//						double durFinal = dur;
//						if (eventAsXML.contains("dots='")) {
//							firstInd = eventAsXML.indexOf("dots='") + "dots='".length();
//							secondInd = eventAsXML.indexOf("'", firstInd);
//							int dots = Integer.parseInt(eventAsXML.substring(firstInd, secondInd));
//							for (int k = 0; k < dots; k++) {
//								durFinal += durFinal * 0.5;
//							}
//						}
//						System.out.println("-->" + durFinal + "<--");
//						if (eventAsXML.contains("<corr>")) {
//	//						get corr part and then all durs
//	//						System.exit(0);
//						}
//						// As eighth note
////						durFinal = durFinal / 0.125;
//						barLenInEighths += (int) (durFinal / 0.125);
////						System.out.println(durFinal);
////						System.exit(0);
//					}

					// Update prevDurXML
					// a. Set prevDurXML to currDurXML (or, if it is null, to the last 
					// XML duration)
					if (defaultCase || defaultCorrectedCase || multipleReplacedByOne) {
						if (currDurXML != null) {
							prevDurXML = currDurXML;
						}
					}
					// b. Set prevDurXML to the last specified duration in corrList  
					if (oneReplacedByMultiple) {
						List<String> corrListRev = new ArrayList<>(corrList);
						Collections.reverse(corrListRev);
						for (String event : corrListRev) {
							Integer[] lastDurXML = getXMLDur(event);
							if (lastDurXML != null) {
								prevDurXML = lastDurXML;
								break;
							}
						}
					}
				}
			}

			if (alignWithMetricBarring) {
				boolean combineTabBars = 
					i < eventsPerBar.size() - 1 && 
					tabBarsToMetricBars.get(i+1)[Tablature.METRIC_BAR_IND] == currMetricBar;
				// tabBars follows tablature 'barring'; combine any tab bars to follow metric barring
				// In case of (i) last tab bar or (ii) where there is a next tab bar that does not 
				// have currMetricBar
				if (i == eventsPerBar.size() - 1 || !combineTabBars) {
					currBarAsXML.add(TAB + "</layer>");
					currBarAsXML.add("</staff>");
					tabBars.add(currBarAsXML);
					startNewBar = true;
				}
				// In case where there is a next tab bar that has currMetricBar
				else if (combineTabBars) {				
					currBarAsXML.add(TAB.repeat(2) + "<barLine/>");
					startNewBar = false;
				}
			}
			else {
				currBarAsXML.add(TAB + "</layer>");
				currBarAsXML.add("</staff>");
				tabBars.add(currBarAsXML);
				startNewBar = true;
			}

//			// Append meter change (if applicable)
//			boolean doThis = false;
//			if (doThis) {
//				if (i < ebf.size()-1) {
//					// Check for meter change in first event of next bar
//					List<String[]> nextBar = ebf.get(i+1);
//					String[] firstEventNextFull = nextBar.get(0);
//					String firstEventNext = firstEventNextFull[Encoding.EVENT_IND];
//					// Remove final ss
//					firstEventNext = firstEventNext.substring(0, firstEventNext.lastIndexOf(ss));
//					String[] firstEventNextSplit = 
//						(!firstEventNext.contains(ss)) ? new String[]{firstEventNext} : 
//						firstEventNext.split("\\" + ss);
//					// Meter change found? Add scoreDef after bar
//					if (MensurationSign.getMensurationSign(firstEventNextSplit[0]) != null) {
//						String meterStr = "";
//						for (String s : meters.get(meterIndex+1)) {
//							if (!s.equals("")) {
//								meterStr += s + " ";
//							}
//						}
//						meterStr = meterStr.trim();
//						String scoreDef = INDENT_TWO + "<scoreDef" + " " + meterStr + "/>" + "\r\n";
////						sb.append(scoreDef);
////						measuresTab.addAll(scoreDef);
//						meterIndex++;
//					}
//				}
//			}
		}

//		// tabBars follows tablature 'barring'; combine any tab bars to follow metric barring
//		if (alignWithMetricBarring) {
//			List<List<String>> tabBarsCombined = new ArrayList<>();
//			int tabBarInd = 0;
//			int numMetricBars = mi.get(mi.size()-1)[Transcription.MI_LAST_BAR];
//			for (int metricBarInd = 0; metricBarInd < numMetricBars; metricBarInd++) {
//				List<String> currTabBar = new ArrayList<>(tabBars.get(tabBarInd));
//				int currMetricBar = metricBarInd + 1;
////				String currTabBarAsStr = "";
////				int metricBarInd = j;
////				currMeasureTabAsStr += INDENT_TWO + "<measure n='" + currMeasure + "'" + 
////					((j == numBars-1) ? " right='end'" : "") + ">" + "\r\n";
////				// The elements in tabBars start at <tabGrp> level
////				currTabBarAsStr += INDENT_TWO + TAB + "<staff n='1'>" + "\r\n";
////				currTabBarAsStr += INDENT_TWO + TAB.repeat(2) + "<layer n='1'>" + "\r\n";
////				for (String line : tabBars.get(tabBarInd)) {
////					currTabBarAsStr += INDENT_TWO + TAB.repeat(3) + line + "\r\n";
////				}
//			
//				// Add any next tab bars with the same metric bar
//				int startInd = tabBarInd + 1;
////				if (startInd != tabBarsToMetricBars.size()) {
//				for (int j = startInd; j < tabBarsToMetricBars.size(); j++) {
//					if (tabBarsToMetricBars.get(j)[1] == currMetricBar) {
//						currTabBar.add(TAB.repeat(2) + "<barline/>");
////						currTabBarAsStr += INDENT_TWO + TAB.repeat(3) + "<barline/>" + "\r\n";
//						for (String line : tabBars.get(j)) {
//							currTabBar.add(line);
////							currTabBarAsStr += INDENT_TWO + TAB.repeat(3) + line + "\r\n";
//						}
//						// Additional increment of tabBarInd
//						tabBarInd++;
//					}
//					else {
//						break;
//					}
//				}
//				tabBarsCombined.add(currTabBar);
////				}
//				// Normal increment of tabBarInd (together with metricBarInd)
//				tabBarInd++;
////				currTabBarAsStr += INDENT_TWO + TAB.repeat(2) + "</layer>" + "\r\n";
////				currTabBarAsStr += INDENT_TWO + TAB + "</staff>";
////				tabBarsAsStr.add(currTabBarAsStr);
//			}
//			tabBars = tabBarsCombined;
//		}

		return tabBars;
	}


	private static List<List<String>> getTransBars(List<Object> data, Tablature tab, List<Integer[]> mi,
		List<Rational[]> tripletOnsetPairs, List<List<Integer>> mismatchInds, 
		boolean grandStaff, boolean tabOnTop, int numVoices) {			

		List<List<String>> transBars = new ArrayList<>();
		
		// Composition of dataStr (and dataInt):
		// dataStr.size() = number of bars in piece
		// dataStr.get(b).size() = voices in bar b (and in whole piece) 
		// dataStr.get(b).get(v).size() = notes in bar b in voice v 
		List<List<List<Integer[]>>> dataInt = (List<List<List<Integer[]>>>) data.get(0);
		List<List<List<String[]>>> dataStr = (List<List<List<String[]>>>) data.get(1);
		
		System.out.println("------------");
		for (List<Integer[]> l : dataInt.get(2)) {
			for (Integer[] in : l) {
				System.out.println(Arrays.toString(in));
			}
		}
		System.out.println("------------");
		for (List<String[]> l : dataStr.get(2)) {
			for (String[] in : l) {
				System.out.println(Arrays.toString(in));
			}
		}
//		System.exit(0);
		
		// Apply beaming: set beamOpen and beamClose in dataInt
		dataInt = beam(dataInt, dataStr, tab, mi, tripletOnsetPairs, mismatchInds, numVoices);
		
		for (List<Integer[]> l : dataInt.get(2)) {
			for (Integer[] in : l) {
				System.out.println(Arrays.toString(in));
			}
		}
		System.out.println("------------");
		for (List<String[]> l : dataStr.get(2)) {
			for (String[] in : l) {
				System.out.println(Arrays.toString(in));
			}
		}
//		System.exit(0);

		// For each bar
		for (int i = 0; i < dataStr.size(); i++) {
			List<String> currBarAsXML = new ArrayList<>();
			int bar = i+1;
			if (verbose) System.out.println("bar = " + bar);
			List<List<Integer[]>> currBarInt = dataInt.get(i);
			List<List<String[]>> currBarStr = dataStr.get(i);

			// For each voice
			for (int j = 0; j < currBarInt.size(); j++) {
				if (verbose) System.out.println("voice = " + j);
				List<Integer[]> currBarCurrVoiceInt = currBarInt.get(j);
				List<String[]> currBarCurrVoiceStr = currBarStr.get(j);
				if (verbose) {
					System.out.println("contents of currBarCurrVoiceInt");
					for (Integer[] in : currBarCurrVoiceInt) {
						System.out.println(Arrays.toString(in));
					}
					System.out.println("contents of currBarCurrVoiceStr");
					for (String[] in : currBarCurrVoiceStr) {
						System.out.println(Arrays.toString(in));
					}
				}
//				Integer[][] vsl = getStaffAndLayer(numVoices);
//				int staff, layer;
//				if (!grandStaff) {
//					staff = j+1;
//					layer = 1;
//				}
//				else {
//					staff = vsl[j][1];
//					layer = vsl[j][2];
//				}
//				if (TAB_AND_TRANS && tabOnTop) {
//					staff += 1;
//				}
				Integer[][] sl = getStaffAndLayer(numVoices);
				int staff = getStaffNum(false, grandStaff, tabOnTop, numVoices, j);
				int layer = !grandStaff ? 1 : sl[j][1];  
				// Non-grand staff case: add staff
				if (!grandStaff) {
					currBarAsXML.add("<staff n='" + staff + "'>");
				}
				// Grand staff case: only add staff at first voice if previous voice has staff-1
				else {		
					if (j == 0 || (j > 0 && sl[j][0] == (sl[j-1][0] + 1))) {
						currBarAsXML.add("<staff n='" + staff + "'>");
					}
				}
				currBarAsXML.add(TAB + "<layer n='" + layer + "'>");

				// For each note
				int diminution = 1;
				if (TAB_AND_TRANS) {
//				if (mi.get(0).length == Tablature.MI_SIZE) {
					diminution = tab.getTimeline().getDiminution(bar);
//					diminution = Tablature.getDiminution(bar, mi);
				}
				List<String> currNotesAsXML = 
					getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, tripletOnsetPairs,
					mismatchInds, j, diminution);
				currBarAsXML.addAll(currNotesAsXML);
				currBarAsXML.add(TAB + "</layer>");

				// Non-grand staff case: add staff
				if (!grandStaff) {
					currBarAsXML.add("</staff>");
				}
				// Grand staff case: only add staff if last voice or if next voice has staff+1
				else {
					if (j == numVoices-1 || (j < numVoices-1 && sl[j][0] == (sl[j+1][0] - 1))) {
						currBarAsXML.add("</staff>");
					}
				}
			}
			transBars.add(currBarAsXML);
		}
		return transBars;
	}


	private static List<List<List<Integer[]>>> beam(List<List<List<Integer[]>>> dataInt, 
		List<List<List<String[]>>> dataStr, Tablature tab, List<Integer[]> mi, 
		List<Rational[]> tripletOnsetPairs, List<List<Integer>> mismatchInds, int numVoices) {
		
		// Organise the information (i) per voice, (ii) per bar for the python beaming script
		List<List<String>> unbeamedBarsPerVoice = new ArrayList<>();
		for (int j = 0; j < numVoices; j++) {
			List<String> empty = new ArrayList<>();
			empty.add("voice=" + j + "\r\n");
			unbeamedBarsPerVoice.add(empty);
		}
		// For each bar
		for (int i = 0; i < dataStr.size(); i++) {
			int bar = i+1;
			if (verbose) System.out.println("bar = " + (i+1));
			List<List<Integer[]>> currBarInt = dataInt.get(i);
			List<List<String[]>> currBarStr = dataStr.get(i);
			// For each voice
			for (int j = 0; j < currBarInt.size(); j++) {
				if (verbose) System.out.println("voice = " + j);
				List<Integer[]> currBarCurrVoiceInt = currBarInt.get(j);
				List<String[]> currBarCurrVoiceStr = currBarStr.get(j);
				// Add current bar to list corresponding to current voice
				int diminution = 1;
				if (TAB_AND_TRANS) {
//				if (mi.get(0).length == Tablature.MI_SIZE) {
					diminution = tab.getTimeline().getDiminution(bar);
//					diminution = Tablature.getDiminution(bar, mi);
				}
				List<String> barList = 
					getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, tripletOnsetPairs,
					mismatchInds, j, diminution);
				String barListAsStr = "";
				Rational currMeter = Transcription.getMeter(bar, mi); 
				barListAsStr += 
					"meter='" + currMeter.getNumer() + "/" + currMeter.getDenom() + "'" + "\r\n";
				for (int k = 0; k < barList.size(); k++) {
					barListAsStr += barList.get(k) + "\r\n";
//					// Add line break after all but final note
//					if (!(i == dataStr.size()-1 && j == currBarInt.size()-1 && 
//						k == barList.size()-1)) {
//						barListAsStr += "\r\n";
//					}
				}
				unbeamedBarsPerVoice.get(j).add(barListAsStr);
			}
		}
		// Store unbeamedBarsPerVoice
		// NB: the stored file ends with a line break
		StringBuilder strb = new StringBuilder();
		for (List<String> l : unbeamedBarsPerVoice) {
			for (String s : l) {
				strb.append(s);
			}
		}
		String filePath = scriptPathPythonMEI;
		File notesFile = new File(filePath + "notes.txt");
		ToolBox.storeTextFile(strb.toString(), notesFile);
		
		// Call the beaming script and get output; delete
		// NB: the output of the beaming script does not end with a line break, but 
		// PythonInterface.getScriptOutput() adds one to the end of it
		String beamed = "";
		try {
			beamed = PythonInterface.getScriptOutput(new String[]{
				"python", filePath + "beam.py", filePath + "notes.txt"});
		} catch (IOException e) {
			e.printStackTrace();
		}
		notesFile.delete();

		// Re-organise the information (i) per bar, (ii) per voice so that it is the same
		// again as dataStr and dataInt
		List<List<List<String>>> beamedReorganised = new ArrayList<>();
		// Get each voice as a single string (a list)
		List<String> voices = Arrays.asList(beamed.split("end of voice" + "\r\n"));
		// Get, for each voice, each bar as a single string (a list)
		List<List<String>> barsPerVoice = new ArrayList<>();
		for (String currVoice : voices) {
			barsPerVoice.add(Arrays.asList(currVoice.split("end of bar" + "\r\n")));
		}
		// Make, per bar, the empty voices
		int numBars = barsPerVoice.get(0).size();
		for (int i = 0; i < numBars; i++) {
			beamedReorganised.add(new ArrayList<>());
		}
		// Fill, per bar, the voices
		for (int bar = 0; bar < numBars; bar++) {
			for (int voice = 0; voice < numVoices; voice++) {
				String notesCurrBarCurrVoice = barsPerVoice.get(voice).get(bar);
//				// Remove final line break
//				notesCurrBarCurrVoice = 
//					notesCurrBarCurrVoice.substring(0, notesCurrBarCurrVoice.lastIndexOf("\r\n"));
				List<String> notesAsOneString = 
					Arrays.asList(notesCurrBarCurrVoice.split("\r\n"));
				beamedReorganised.get(bar).add(notesAsOneString);
			}
		}
		
		// Align beamedReorganised and dataInt and set beamOpen and beamClose
		for (int bar = 0; bar < beamedReorganised.size(); bar++) {
			List<List<String>> voicesCurrBar = beamedReorganised.get(bar);
			for (int voice = 0; voice < voicesCurrBar.size(); voice++) {
				List<String> notesCurrBarCurrVoice = voicesCurrBar.get(voice);
				for (int note = 0; note < notesCurrBarCurrVoice.size(); note++) {
					String noteCurrBarCurrVoice = notesCurrBarCurrVoice.get(note);
					if (noteCurrBarCurrVoice.startsWith("<beam>")) {
						dataInt.get(bar).get(voice).get(note)[INTS.indexOf("beamOpen")] = 1;
					}
					if (noteCurrBarCurrVoice.startsWith("</beam>")) {
						dataInt.get(bar).get(voice).get(note)[INTS.indexOf("beamClose")] = 1;
					}
				}
			}
		}
		return dataInt;
	}


	/**
	 * Returns the XML for the bar represented by the given lists <code>String[]</code> and 
	 * <ode>Integer[]</code>.
	 * 
	 * @param currBarCurrVoiceInt
	 * @param currBarCurrVoiceStr
	 * @param tripletOnsetPairs
	 * @param mismatchInds
	 * @param argVoice
	 * @param diminution
	 * @return
	 */
	private static List<String> getBar(List<Integer[]> currBarCurrVoiceInt, List<String[]> 
		currBarCurrVoiceStr, /*List<Integer[]> mi,*/ List<Rational[]> tripletOnsetPairs, 
		List<List<Integer>> mismatchInds, int argVoice, int diminution) {
		List<String> barList = new ArrayList<>();
//		int bar = currBarCurrVoiceInt.get(0)[INTS.indexOf("bar")];
		String barRestStr = 
			TAB.repeat(2) + "<mRest " + "xml:id='" + argVoice + "." + 
			currBarCurrVoiceInt.get(0)[INTS.indexOf("bar")] + "." + "0.r.0'" + "/>";

//		int diminution = 1;
//		if (mi.get(0).length == Tablature.MI_SIZE) {
//			diminution = Tablature.getDiminution(bar, mi);
//		}

		// If applied to new data (mismatchInds is an empty list), no notes need to be colored
		boolean highlightNotes = mismatchInds.size() != 0;
		boolean isMappingCase = highlightNotes && mismatchInds.get(0) == null; // zondag
//		boolean isMappingCase = mismatchInds.get(0) == null;
		List<Integer> orn = null;
		List<Integer> rep = null;
		List<Integer> ficta = null;
		List<Integer> other = null;
		List<Integer> inc = null;
		List<Integer> over = null;
		List<Integer> sup = null;
		List<Integer> half = null;
		// Mapping case
		if (isMappingCase) {
			orn = mismatchInds.get(Transcription.ORNAMENTATION_IND);
			rep = mismatchInds.get(Transcription.REPETITION_IND);
			ficta = mismatchInds.get(Transcription.FICTA_IND);
			other = mismatchInds.get(Transcription.OTHER_IND);
		}
		// Modelling case
		else {
			if (highlightNotes) { // zondag
				inc = mismatchInds.get(1); // TODO was ErrorCalculator.INCORRECT_VOICE
				over = mismatchInds.get(2); // TODO was ErrorCalculator.OVERLOOKED_VOICE
				sup = mismatchInds.get(3); // TODO was ErrorCalculator.SUPERFLUOUS_VOICE
				half = mismatchInds.get(4); // TODO was ErrorCalculator.HALF_VOICE
			}
		}

		// If the bar does not contain any notes or rests (this happens when a voice ends
		// with one or more bars of rests)
		if (currBarCurrVoiceInt.size() == 0) {
			barList.add(barRestStr);
		}
		// If the bar contains notes/rests
		else {
			boolean isFullBarRest = true;
			for (int i = 0; i < currBarCurrVoiceStr.size(); i++) {
				String[] note = currBarCurrVoiceStr.get(i);
				if (note[STRINGS.indexOf("pname")] != null) {
					isFullBarRest = false;
					break;
				}
			}
			if (isFullBarRest) {
				barList.add(barRestStr);
			}
			else {
//				int bar = currBarCurrVoiceInt.get(0)[INTS.indexOf("bar")];
//				Rational meter = Transcription.getMeter(bar, mi);
				
//				List<String> barListTupletWrapped = new ArrayList<>();
//				for (int k = 0; k < currBarCurrVoiceInt.size(); k++) {
//					Integer[] in = currBarCurrVoiceInt.get(k);
//					if (in[INTS.indexOf("tripletOpen")] == 1) {
//						barListTupletWrapped.add
//					}
//				}
	
//				List<List<String>> perBeat = new ArrayList<>();
				boolean chordActive = false;
				boolean tupletActive = false;
				boolean beamActive = false; 
				// For each note
				for (int i = 0; i < currBarCurrVoiceStr.size(); i++) {
					String[] note = currBarCurrVoiceStr.get(i);
					Integer[] noteInt = currBarCurrVoiceInt.get(i);
//					Rational mp = new Rational(noteInt[INTS.indexOf("metPosNum")], 
//						noteInt[)INTS.indexOf("metPosDen")]);
//					Rational durRat = new Rational(Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom(),
//						noteInt[INTS.indexOf("dur")]);
//					int dur = (int) durRat.toDouble();
					int currOnsNum = currBarCurrVoiceInt.get(i)[INTS.indexOf("onsetNum")];
					int currOnsDen = currBarCurrVoiceInt.get(i)[INTS.indexOf("onsetDen")];
					
//					// Close previous/open next beaming group if metPos is on the quarter note grid
//					if (mp.toDouble() == 0.25 || mp.toDouble() == 0.5 || mp.toDouble() == 0.75) { 
//						// Only if the note at index k is not the last note in the bar
//						sb.append(indent + tab.repeat(3) + "</beam>" + "\r\n");
//						sb.append(indent + tab.repeat(3) + "<beam>" + "\r\n");
//					}

					// Check for any tripletOpen to be added before noteStr
					if (noteInt[INTS.indexOf("tripletOpen")] == 1) {
						System.out.println("diminution = " + diminution);
						int tupletDur = -1;
//						Rational tupletDur = null;
						Rational onset = new Rational(currOnsNum, 
							currBarCurrVoiceInt.get(i)[INTS.indexOf("onsetDen")]);
						System.out.println("onset = " + onset);
						for (Rational[] r : tripletOnsetPairs) {
							if (onset.equals(r[0])) {
//								tupletDur = r[2].getNumer();
								Rational tupletDurRat = r[2];
								// tupletDurRat is expressed in RhythmSymbol duration (e.g., 
								// minim = 24): convert to CMN equivalent (e.g., 24 * 1/96 = 1/4) 
//								if (noteInt[INTS.indexOf("indTab")] != -1) {
									tupletDurRat = tupletDurRat.mul(Tablature.SMALLEST_RHYTHMIC_VALUE);
//								}
								// Apply diminution and get MEI dur value (e.g., 1/4 * 2 = 1/2;
								// MEI dur value is denom of 1/2)
								tupletDurRat = 
									(diminution > 0) ? tupletDurRat.mul(diminution) :
									tupletDurRat.div(diminution);
								tupletDur = tupletDurRat.getDenom();
								break;
							}
						}
						barList.add("<tuplet dur='" + tupletDur + "' num='3' numbase='2'>");
						tupletActive = true;
					}

					// Check for any chord to be added before noteStr
					if (i < currBarCurrVoiceStr.size()-1) {
						if (currOnsNum == currBarCurrVoiceInt.get(i+1)[INTS.indexOf("onsetNum")]) {
							barList.add("<chord dur='" + noteInt[INTS.indexOf("dur")] + "'>");
							chordActive = true;
							System.out.println(Arrays.toString(currBarCurrVoiceInt.get(i+1)));
							System.out.println(currOnsNum);
							System.out.println("chordActive");
						}
					}
					// Check for any beams to be added before noteStr
					if (noteInt[INTS.indexOf("beamOpen")] == 1) {
						barList.add(TAB.repeat(2) + "<beam>");
						beamActive = true;
					}
					
					String noteStr = TAB.repeat(2); //"";
					// Add indent for any tuplet and/or chord
					if (tupletActive) {
						noteStr += TAB;
					}
					if (chordActive) {
						noteStr += TAB;
					}
					if (beamActive) {
						noteStr += TAB;
					}
					noteStr = (note[STRINGS.indexOf("pname")] == null) ? noteStr + "<rest " :
						noteStr + "<note ";
//					if (note[STRINGS.indexOf("pname")] == null) {
//						noteStr += "<rest "; 
//					}
//					else {
//						// Check for in-voice chords
//						if (k < currBarCurrVoiceStr.size()-1) {
//							if (currOnsNum == currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")]) {
//								barList.add("<chord>");
//								chordActive = true;
//							}
//						}
//						noteStr += "<note ";
//					}

//					Arrays.asList(note).forEach(a -> {if (a != null) {fromNote += (a + " ");}});
					for (String s : note) {
						if (s != null) { 
							noteStr += (s + " ");
						}
					}
					String ID = note[STRINGS.indexOf("ID")];
					ID = ID.substring(ID.indexOf("'") + 1, ID.lastIndexOf("'"));
					int tabInd = -1;
					if (note[STRINGS.indexOf("pname")] != null) {
						tabInd = noteInt[INTS.indexOf("indTab")];
					}
					if (isMappingCase) {
						if (orn.contains(tabInd)) {
							noteStr += "color='blue'" + " ";
						}
						else if (rep.contains(tabInd)) {
							noteStr += "color='lime'" + " ";
						}
						else if (ficta.contains(tabInd)) {
							noteStr += "color='orange'" + " ";
						}
						else if (other.contains(tabInd)) {
							noteStr += "color='red'" + " ";
						}
					}
					else {
						if (highlightNotes) { // zondag
							int index = (tabInd != -1) ? tabInd : noteInt[INTS.indexOf("ind")];
							if (inc.contains(index)) {
								noteStr += "color='red'" + " ";
							}
							else if (over.contains(index)) {
								noteStr += "color='orange'" + " ";
							}
							else if (sup.contains(index)) {
								noteStr += "color='lime'" + " ";
							}
							else if (half.contains(index)) {
								noteStr += "color='blue'" + " ";
							}
						}
					}
					// Trim trailing spaces (not leading spaces, as there may be TABs)
					// (see https://stackoverflow.com/questions/12106757/removing-spaces-at-the-end-of-a-string-in-java) 
					noteStr = noteStr.replaceAll("\\s+$", "");
					noteStr = noteStr + "/>";
					barList.add(noteStr);
					// If there is an active in-voice chord: check if it must be closed
					if (chordActive) {
						Rational currOnset = new Rational(currOnsNum, currOnsDen);
						System.out.println(currOnset);
						// pitch oct acc tie dur dots ID
						// 
						Rational nextOnset = new Rational(
							currBarCurrVoiceInt.get(i+1)[INTS.indexOf("onsetNum")],
							currBarCurrVoiceInt.get(i+1)[INTS.indexOf("onsetDen")]);
						if ((i < currBarCurrVoiceStr.size()-1 &&
//							currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")] > currOnsNum) 
							nextOnset.isGreater(currOnset))
							|| i == currBarCurrVoiceStr.size()-1) {
							barList.add("</chord>");
							chordActive = false;
						}
					}
					// Check for any tripletClose to be placed after noteStr
					if (noteInt[INTS.indexOf("tripletClose")] == 1) {
						barList.add("</tuplet>");
						tupletActive = false;
					}
					// Check for any beamClose to be placed after noteStr
					if (noteInt[INTS.indexOf("beamClose")] == 1) {
						barList.add(TAB.repeat(2) + "</beam>");
						beamActive = false;
					}
				}
			}
		}
		return barList;
	}


	/**
	 * Determines whether the given onset is a tripletOnset, i.e., falls within a triplet.
	 * This is the case when, for a tripletOnsetPair in the given list, the given onset is
	 * >= element 0 (the onset of the opening triplet note) and <= element 1 (the onset of 
	 * the closing triplet note) in the tripletOnsetPair.
	 * 
	 * @param tripletOnsetPairs
	 * @param onset
	 * @return A List specifiying whether the onset is a tripletOpen, tripletMid, or tripletClose
	 *         onset.
	 */
	// TODO test
	static private List<Boolean> isTripletOnset(List<Rational[]> tripletOnsetPairs, Rational onset) {
		boolean tripletOpen = false, tripletMid = false, tripletClose = false;
		for (Rational[] r : tripletOnsetPairs) {
			if (onset.equals(r[0])) {
				tripletOpen = true;
				break;
			}
			if (onset.isGreater(r[0]) && onset.isLess(r[1])) {
				tripletMid = true;
				break;
			}
			if (onset.equals(r[1])) {
				tripletClose = true;
				break;
			}
		}
		return Arrays.asList(new Boolean[]{tripletOpen, tripletMid, tripletClose}); 
	}


	/**
	 * Returns the tripletOnsetPair at the given onset, with the triplet length added.
	 * 
	 * @param onset
	 * @param tripletOnsetPairs
	 * @param mi
	 * @return A Rational[] containing
	 * 		   <ul>
	 *         <li>as element 0: the tripletOpen onset</li>
	 *         <li>as element 1: the tripletClose onset</li>
	 *         <li>as element 2: the tripletUnit</li>
	 *         <li>as element 3: the tripletLen</li>
	 *         </ul>
	 */
	// TODO test
	static Rational[] getExtendedTripletOnsetPair(Rational onset, List<Rational[]> 
		tripletOnsetPairs, List<Integer[]> mi, int diminution) {

		Rational[] pairAndLen = null;
//		int bar = Tablature.getMetricPosition(onset, mi)[0].getNumer();
//		int diminution = 1;
//		if (ONLY_TAB || TAB_AND_TRANS) {
//		if (mi.get(0).length == Tablature.MI_SIZE) {
//			diminution = Tablature.getDiminution(bar, mi);
//		}

		for (Rational[] r : tripletOnsetPairs) {
			Rational tripletOpen = r[0];
			// Calculate tripletLen
			// 1. Undiminished, i.e., length (in multiples of Tablature.SRV) * Tablature.SRV 
			// * 2 (there are three notes of this unit in the time of two)
			// Example for a mi (length = 24): (24 * 1/96) * 2 = 1/2
			Rational currTripletLenUndim = r[2].mul(Tablature.SMALLEST_RHYTHMIC_VALUE).mul(2);
			// 2. Diminished
			Rational currTripletLen = 
				diminution > 0 ? currTripletLenUndim.mul(diminution) :
				currTripletLenUndim.div(Math.abs(diminution));	
//			Rational currTripletLen = currTripletLenUndim.mul(diminution);

			// If onset falls within the triplet time
			if (onset.isGreaterOrEqual(tripletOpen) && onset.isLess(tripletOpen.add(currTripletLen))) {
				pairAndLen = new Rational[4];
				for (int i = 0; i < r.length; i++) {
					pairAndLen[i] = r[i];
				}
				pairAndLen[3] = currTripletLen;
				
				break;
			}
		}
		return pairAndLen;
	}


	/**
	 * Extracts from the given Transcription the data needed to create an MEI file.
	 * 
	 * @param trans
	 * @param btp
	 * @param mi
	 * @param ki
	 * @param tripletOnsetPairs
	 * @returns A 
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> getData(Tablature tab, Transcription trans,
		List<Integer[]> mi, List<Integer[]> ki, List<Rational[]> tripletOnsetPairs) {

		Piece p = trans.getPiece();
		int numVoices = p.getScore().size();
		Integer[][] btp = tab.getBasicTabSymbolProperties();
		Rational gridVal = 
			(btp != null) ? Tablature.SMALLEST_RHYTHMIC_VALUE : // new Rational(1, 96);
			new Rational(1, 128); // // 14.03.2020 was 1/64
		Integer[][] bnp = trans.getBasicNoteProperties();
		// pitch, metric time num, metric time den, metric dur num, metric dur den, chord seq no, chord size, note seq num in chord
//		System.out.println("bnp:");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(Arrays.toString(bnp[i]));
//		}

		// Undiminute bnp
		if (TAB_AND_TRANS && adaptCMNDur) {
//			System.out.println("mi:");
//			for (Integer [] in : mi) {
//				System.out.println(Arrays.toString(in));
//			}
//			System.exit(0);

//			List<Integer[]> meterChangeOnsets = new ArrayList<>();
//			for (Integer [] in : mi) {
//				meterChangeOnsets.add(new Integer[]{
//				in[Transcription.MI_NUM_MT_FIRST_BAR], 
//				in[Transcription.MI_DEN_MT_FIRST_BAR]});
//			}
//			System.out.println(diminutions);
//			for (Integer[] in : meterChangeOnsets) {
//				System.out.println(Arrays.toString(in));
//			}
//			int initialDiminution = 0;
//			Rational initialMeterChangeOnset = Rational.ZERO;
			
			// Adapt complete bnp if any of the meters has a dimunition other than 1
			List<Integer> diminutions = ToolBox.getItemsAtIndex(mi, Timeline.MI_DIM);
			if (Collections.frequency(diminutions, 1) != diminutions.size()) {
				// Undiminute bnp using diminuted mi
				for (Integer[] in : mi) {
					System.out.println(Arrays.toString(in));
				}
//				System.exit(0);
				bnp = Transcription.undiminuteBasicNoteProperties(bnp, mi);
				// Reset mi to undiminuted 
				mi = tab.getTimeline().getUndiminutedMeterInfo();
//				Integer[][] undiminutedBnp = new Integer[bnp.length][bnp[0].length];
//				Rational prevMt = null;
//				Rational prevMtDim = null;
//				
//				int prevDim = 0;
//				// For each first note in a chord
//				for (int i = 0 ; i < bnp.length; i++) {
////					System.out.println("i = " + i);
//					Integer[] currNote = bnp[i];
////					System.out.println("currNote = " + Arrays.toString(currNote));
////					Integer[] currCopy = Arrays.copyOf(currNote, currNote.length);
//					int chordInd = currNote[Transcription.CHORD_SEQ_NUM];
//					int currChordSize = currNote[Transcription.CHORD_SIZE_AS_NUM_ONSETS];
////					System.out.println("chordInd = " + chordInd);
//					// Get original metric time and diminution
//					Rational currMt = 
//						new Rational(currNote[Transcription.ONSET_TIME_NUMER],
//						currNote[Transcription.ONSET_TIME_DENOM]);
////					System.out.println("currMt = " + currMt);
//					int currDim = Tablature.getDiminution(currMt, mi);
////					System.out.println("currDim = " + currDim);
//					// Get the diminuted metric time for the chord the note at index i is in
//					Rational currMtDim;
//					// If the chord is the first chord of the piece
//					if (chordInd == 0) {
//						if (currMt.equals(Rational.ZERO)) {
//							currMtDim = currMt;
//						}
//						else {
//							currMtDim = 
//								(currDim > 0) ? currMt.div(currDim) : 
//								currMt.mul(Math.abs(currDim));							
//						}
//					}
//					// If the chord is a chord after the first: to get currMtDim, add the
//					// diminuted difference between currMt and prevMt to prevMtDim
//					else {
//						Rational mtIncrease = 
//							prevDim > 0 ? (currMt.sub(prevMt)).div(prevDim) : 
//							(currMt.sub(prevMt)).mul(Math.abs(prevDim));
//						currMtDim = prevMtDim.add(mtIncrease);
//					}
////					System.out.println("currMtDim = " + currMtDim);
//					
//					// Adapt metric time and duration for all notes in the chord
////					int newI = -1;
//					for (int j = i; j < i + currChordSize; j++) {
////					for (int j = i; j < bnp.length; j++) {
////						System.out.println("j = " + j);
//						Integer[] curr = bnp[j];
//						// Metric time
//						curr[Transcription.ONSET_TIME_NUMER] = currMtDim.getNumer();
//						curr[Transcription.ONSET_TIME_DENOM] = currMtDim.getDenom();
//						// Duration
//						Rational currDur = 
//							new Rational(curr[Transcription.DUR_NUMER],
//							curr[Transcription.DUR_DENOM]);
//						Rational currDurDim = 
//							(currDim > 0) ? currDur.div(currDim) : currDur.mul(currDim);
//						curr[Transcription.DUR_NUMER] = currDurDim.getNumer();
//						curr[Transcription.DUR_DENOM] = currDurDim.getDenom();
//						undiminutedBnp[j] = curr;
////						System.out.println("curr = " + Arrays.toString(curr));
////						if (j+i < bnp.length) {
////							if (bnp[j+1][Transcription.CHORD_SEQ_NUM] == chordInd+1) {
////						newI = j;
////								break;
////							}
////						}
//					}
//					// Increment variables
//					prevMt = currMt;
//					prevDim = currDim;
//					prevMtDim = currMtDim;
////					i = newI;
//					i = (i + currChordSize) - 1;
////					System.out.println("prevMt = " + prevMt);
////					System.out.println("currDim = " + currDim);
////					System.out.println("prevMtDim = " + prevMtDim);
////					System.out.println("i = " + i);
//				}
//				bnp = undiminutedBnp;
			}
		}
//		System.out.println(bnp.length);
//		System.out.println(Arrays.toString(bnp[0]));
//		System.out.println(Arrays.toString(bnp[25]));
//		System.out.println(Arrays.toString(bnp[50]));
//		System.out.println(Arrays.toString(bnp[bnp.length-1]));
//		System.exit(0);
		
		// Get meter and key TODO assumed is a single key
		int numBars = mi.get(mi.size()-1)[Timeline.MI_LAST_BAR];
		Rational endOffset = Rational.ZERO;
		for (Integer[] m : mi) {
			Rational currMeter = new Rational(m[Timeline.MI_NUM], m[Timeline.MI_DEN]);
			int barsInCurrMeter = (m[Timeline.MI_LAST_BAR] - m[Timeline.MI_FIRST_BAR]) + 1;
			endOffset = endOffset.add(currMeter.mul(barsInCurrMeter));
		}
		Integer[] key = ki.get(0);
		int numAlt = key[Transcription.KI_KEY];
		// Set initial bar and meter
		Integer[] initMi = mi.get(0);
		Rational meter = new Rational(initMi[Timeline.MI_NUM], initMi[Timeline.MI_DEN]);
		Rational barEnd = meter;

		// Get indices mapping
		List<List<Integer>> transToTabInd = null, tabToTransInd = null;
		if (btp != null) {
			tabToTransInd = Transcription.alignTabAndTransIndices(btp, bnp).get(0);
			transToTabInd = Transcription.alignTabAndTransIndices(btp, bnp).get(1);
		}

		// Set grids 
		Integer[][] mpcg = makeMIDIPitchClassGrid();
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid();

		List<List<String[]>> noteAttribPerVoiceStrings = new ArrayList<List<String[]>>();
		List<List<Integer[]>> noteAttribPerVoiceInts = new ArrayList<List<Integer[]>>();
		for (int i = 0; i < numVoices; i++) {
			noteAttribPerVoiceStrings.add(new ArrayList<String[]>());
			noteAttribPerVoiceInts.add(new ArrayList<Integer[]>());
		}
		List<Integer> naturalsAlreadyAdded = new ArrayList<Integer>();
		List<Integer> accidentalsAlreadyAdded = new ArrayList<Integer>();
		List<Integer> naturalsInEffect = new ArrayList<Integer>();
		List<Integer> sharpsInEffect = new ArrayList<Integer>();
		List<Integer> flatsInEffect = new ArrayList<Integer>();

		for (int i = 0; i < bnp.length; i++) {
			int iTab = -1;
			if (btp != null) {
				iTab = transToTabInd.get(i).get(0); // each element (list) contains only one element (int)
			}
			String[] curr = new String[STRINGS.size()];
			int voice = DataConverter.convertIntoListOfVoices(trans.getVoiceLabels().get(i)).get(0);
			Rational onset = new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], 
				bnp[i][Transcription.ONSET_TIME_DENOM]);
			System.out.println("iTab = " + iTab);
			System.out.println("onset = " + onset);
//			if (i == 10)
//				System.exit(0);
			if (ONLY_TAB) {
//			if (btp != null) {
				onset = new Rational(btp[iTab][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			}
//\\			System.out.println("i = " + i + " (indTab = " + iTab + "); bar = " + 
//\\				Tablature.getMetricPosition(onset, mi)[0].getNumer() + "; pitch = " + bnp[i][0]);

//			Rational[] barMetPos = 
//				!adaptCMNDur ? Tablature.getMetricPosition(onset, mi) :
//				Tablature.getMetricPosition(onset, tab.getUndiminutedMeterInfo());		
			Rational[] barMetPos = Timeline.getMetricPosition(onset, mi);	
				
//			System.out.println(Arrays.toString(tab.getUndiminutedMeterInfo().get(0)));
			int bar = barMetPos[0].getNumer();
//			System.out.println("i = " + i + "; bar = " + bar);
			Rational metPos = barMetPos[1];
//			System.out.println(bar);
//			System.out.println(metPos);
//			if (i == 10) {
//				System.out.println("blaaaa");
//				System.exit(0);
//			}
			
			// If adaptCMNDur, diminution has been made undone in bnp
			int diminution = adaptCMNDur ? 1 : Timeline.getDiminution(onset, mi);

			// Increment barEnd and clear lists when new bar is reached
			if (onset.isGreaterOrEqual(barEnd)) {
				barEnd = (onset.sub(metPos)).add(Transcription.getMeter(bar, mi));
				naturalsAlreadyAdded.clear();
				accidentalsAlreadyAdded.clear();
				naturalsInEffect.clear();
				sharpsInEffect.clear();
				flatsInEffect.clear();
			}

			int pitch = bnp[i][Transcription.PITCH];
			int midiPitchClass = pitch % 12;
			Rational durRounded = round(new Rational(bnp[i][Transcription.DUR_NUMER], 
				bnp[i][Transcription.DUR_DENOM]), gridVal);
			if (ONLY_TAB) {
//			if (btp != null) {
				durRounded = new Rational(btp[iTab][Tablature.MIN_DURATION], Tablature.SRV_DEN);
			}

			Rational offset = onset.add(durRounded);
			int keyInd = numAlt + 7;
			Integer[] currMpcg = mpcg[keyInd];
			String[] currAg = ag[keyInd];
			String[] currPcg = pcg[keyInd];
			if (verbose) {
				System.out.println("voice                    " + voice);
				System.out.println("bar                      " + bar);
				System.out.println("pitch                    " + pitch);
				System.out.println("midiPitchClass           " + midiPitchClass);
				System.out.println("onset                    " + onset);
				System.out.println("offset                   " + offset);
				System.out.println("metPos                   " + metPos);
				System.out.println("durRounded               " + durRounded);
				System.out.println("barEnd                   " + barEnd);
				System.out.println("currMpcg                 " + Arrays.asList(currMpcg));
				System.out.println("currAg                   " + Arrays.asList(currAg));
				System.out.println("currPcg                  " + Arrays.asList(currPcg));
				System.out.println("------------------");
			}
//			if (bar == 3) {
//				System.exit(0);
//			}
			// Check for preceding rests
			List<String[]> pitchOctAccTie = new ArrayList<String[]>();
			List<Integer[]> indBarOnsMpDurDots = new ArrayList<Integer[]>();
			List<String[]> currVoiceStrings = noteAttribPerVoiceStrings.get(voice);
			List<Integer[]> currVoiceInts = noteAttribPerVoiceInts.get(voice);
			Rational durPrev, metPosPrev, offsetPrev; 
			
			// First note in voice?
			if (currVoiceStrings.size() == 0) { // || isUpperInInVoiceChord) {
				durPrev = Rational.ZERO;
				metPosPrev = Rational.ZERO;
				offsetPrev = Rational.ZERO;
			}
			else {
				// prevNote is the last item in currVoiceInts 
				// (1) whose onset is less than onset
				// (2) whose onset equals onset (if prevNote is a lower note in an in-voice chord)
				Integer[] prevNote = null;
				Rational onsetPrev = null;
				for (int j = currVoiceInts.size()-1; j >= 0; j--) {
					prevNote = currVoiceInts.get(j);
					onsetPrev = new Rational(prevNote[INTS.indexOf("onsetNum")], 
						prevNote[INTS.indexOf("onsetDen")]);
					// If previous onset is less than current (but is not a rest (onset = -1/-1))
					if (onsetPrev.getNumer() != -1 && onsetPrev.isLess(onset)) {
						break;
					}
					// If previous onset equals current (but is not a rest (onset = -1/-1))
					if (onsetPrev.getNumer() != -1 && onsetPrev.equals(onset)) {
						break;
					}
				}

				int durPrevInt = prevNote[INTS.indexOf("dur")]; 
				durPrev = 
					(durPrevInt > 0) ? new Rational(1, durPrevInt) :
					((durPrevInt == BREVE ? new Rational(2, 1) : new Rational(4, 1)));
				int dotsPrev = prevNote[INTS.indexOf("dots")];
				// The number of dots n lengthens a note by l its value, where l = ((2^n)-1)/2^n
				// (see https://en.wikipedia.org/wiki/Note_value)
				Rational l = new Rational((int)Math.pow(2, dotsPrev) - 1, (int)Math.pow(2, dotsPrev));
				durPrev = durPrev.add(durPrev.mul(l));
				metPosPrev = Timeline.getMetricPosition(onsetPrev, mi)[1]; 
				offsetPrev = onsetPrev.add(durPrev);

				// To tripletise is to give a note its nominal (shown) value instead of its 
				// actual value by multiplying it with TRIPLETISER (3/2). 
				// Example for a half note:
				// tripletised = 1/2, 1/2, 1/2
				// tripletised = 1/3, 1/3, 1/3
				// E.g., 1/3 * 3/2 = 1/2; 1/6 * 32 = 1/4; etc.
				// Untripletised variables calculated from prevNote: onsetPrev, metPosPrev
				// Tripletised variables calculated from prevNote: durPrev, dotsPrev, offsetPrev
				// If onsetPrev is within a triplet, durPrev, dotsPrev, and offsetPrev (needed
				// to calculate rests) must be reverted from their tripletised value to their 
				// untripletised value
				if (prevNote[INTS.indexOf("tripletOpen")] == 1 ||
					prevNote[INTS.indexOf("tripletMid")] == 1 ||
					prevNote[INTS.indexOf("tripletClose")] == 1) {
					// Recalculate durPrev by multiplying it by 2/3; then recalculate dotsPrev
					// and offsetPrev
					durPrevInt = prevNote[INTS.indexOf("dur")];
					durPrev = 
						(durPrevInt > 0) ? new Rational(1, durPrevInt) :
						((durPrevInt == BREVE ? new Rational(2, 1) : new Rational(4, 1)));
					durPrev = durPrev.mul(DETRIPLETISER);
					dotsPrev = prevNote[INTS.indexOf("dots")];
					l = new Rational((int)Math.pow(2, dotsPrev) - 1, (int)Math.pow(2, dotsPrev));
					durPrev = durPrev.add(durPrev.mul(l));
					offsetPrev = onsetPrev.add(durPrev);
				}
			}

			// Rests
			Rational durRest = onset.sub(offsetPrev);
			if (durRest.isGreater(Rational.ZERO)) {
				Rational precedingInBar = metPos;
				// Single-bar rest in the same bar
				if (durRest.isLessOrEqual(precedingInBar)) {
//\\					System.out.println("CASE: single-bar rest");
					Rational metPosRest = null;
					Rational onsetRest = offsetPrev; // herr man
					// If the bar starts with a rest
					if (durRest.equals(precedingInBar)) {
						metPosRest = Rational.ZERO;
					}
					else {
						metPosRest = 
							(currVoiceStrings.size() == 0) ? Rational.ZERO : 
							metPosPrev.add(durPrev);
					}
//					Rational durRestTripletised = durRest;
					List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null :
						isTripletOnset(tripletOnsetPairs, onsetRest);
					List<Object> noteData =
						getNoteData(i, iTab, diminution, curr, durRest, gridVal, /*bar,*/
						onsetRest, metPosRest, mi, tripletInfo, tripletOnsetPairs);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Single-bar rest in the previous bar (onset is 0/x)
				else if (precedingInBar.equals(Rational.ZERO) && 
					durRest.isLessOrEqual(Transcription.getMeter(bar-1, mi))) {
//\\					System.out.println("CASE: single-bar rest in previous bar");
					Rational onsetRest = offsetPrev;
					Rational metPosRest = 
						(currVoiceStrings.size() == 0) ? Rational.ZERO :
						Timeline.getMetricPosition(onsetRest, mi)[1];
//					Rational durRestTripletised = durRest;
					List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null :
						isTripletOnset(tripletOnsetPairs, onsetRest);
					List<Object> noteData = 
						getNoteData(i, iTab, diminution, curr, durRest, gridVal, /*bar-1,*/ onsetRest, 
						metPosRest, mi, tripletInfo, tripletOnsetPairs);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Multi-bar rest
				else {
//\\					System.out.println("CASE: multi-bar rest");
					// Check how many bars the note spans
					List<Rational> subNoteDurs = new ArrayList<>();
					// subNoteDursOnsets contains the onsets of the subnotes
					List<Rational> subNoteDursOnsets = new ArrayList<>(); // herr man
					if (!precedingInBar.equals(Rational.ZERO)) {
						subNoteDurs.add(precedingInBar);
						subNoteDursOnsets.add(onset.sub(precedingInBar)); // herr man
					}
					Rational remainder = durRest.sub(precedingInBar);
					int beginBar = Timeline.getMetricPosition(offsetPrev, mi)[0].getNumer();
					List<Integer> bars = 
						IntStream.rangeClosed(beginBar, bar).boxed().collect(Collectors.toList());
					for (int j = bar-1; j >= beginBar; j--) {
						Rational currBarLen = Transcription.getMeter(j, mi);
						if (remainder.isGreaterOrEqual(currBarLen)) {
							subNoteDurs.add(currBarLen);
							// The onset of this subnote is onset of the previous subnote 
							// (i.e., the one added last to the list) minus the bar length
							if (subNoteDursOnsets.size() == 0) {
								// No remainderInBar, so onset is at the beginning of the bar
								subNoteDursOnsets.add(onset.sub(currBarLen));
							}
							else {
								subNoteDursOnsets.add(
									subNoteDursOnsets.get(subNoteDursOnsets.size()-1).sub(
									currBarLen));
							}
							remainder = remainder.sub(currBarLen);
						}
						else {
							if (!remainder.equals(Rational.ZERO)) {
								subNoteDurs.add(remainder);
								// The onset of this subnote is onset of the previous subnote 
								// (i.e.,  the one added last to the list) minus the remainder
								// NB subNoteDursOnsets.size()-1 always exists: in the case of 
								// a multi-bar rest, either a rest with length remainingInBar 
								// or one with length barLength has already been added
								subNoteDursOnsets.add(subNoteDursOnsets.get(
									subNoteDursOnsets.size()-1).sub(remainder));
							}
						}
					}
					Collections.reverse(subNoteDurs);
					Collections.reverse(subNoteDursOnsets);
					// For each subnote
					Rational currOnset = offsetPrev;
					Rational currMetPosRest = 
						(currVoiceStrings.size() == 0) ? Rational.ZERO : metPosPrev.add(durPrev);
					// If currMetPosRest equals the length of the bar before beginBar, this means 
					// that the rest start at the beginning of beginBar, and that currMetPosRest
					// must be set to Rational.ZERO
					if (currMetPosRest.equals(Transcription.getMeter(beginBar-1, mi))) {
						currMetPosRest = Rational.ZERO;
					}
					for (int j = 0; j < subNoteDurs.size(); j++) {
						Rational currSubNoteDur = subNoteDurs.get(j);
						Rational currSubNoteDurOnset = subNoteDursOnsets.get(j);
//						Rational currSubNoteDurTripletised = currSubNoteDur;
						List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null :
							isTripletOnset(tripletOnsetPairs, currSubNoteDurOnset);
						List<Object> noteData = 
							getNoteData(i, iTab, diminution, curr, currSubNoteDur, gridVal, /*bars.get(j),*/ 
							currOnset, currMetPosRest, mi, tripletInfo,
							tripletOnsetPairs);
						List<String[]> subNote = (List<String[]>) noteData.get(0);
						pitchOctAccTie.addAll(subNote);
						indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
						currOnset = currOnset.add(currSubNoteDur);
						currMetPosRest = Rational.ZERO;
					}
				}
			}

			// Notes
			// 1. Set pname, accid, oct
			String pname = "";
			String accid = "";
			String oct = String.valueOf(getOctave(pitch));
			// Find gridRowInd to determine pname
			int gridRowInd = -1;
			// No flat, sharp, or natural
			if (Arrays.asList(currMpcg).contains(midiPitchClass)) {
				gridRowInd = Arrays.asList(currMpcg).indexOf(midiPitchClass);
				// TODO If the note restores a previously altered (flat/sharp/natural) note
			}
			// Flat, sharp, or natural
			else {
				List<Integer> keySigs = getMIDIPitchClassKeySigs(key);
				// If natural (flats/sharps)
				if (numAlt < 0 && keySigs.contains((pitch-1) % 12) ||
					numAlt > 0 && keySigs.contains((pitch+1) % 12)) {
					if (verbose) System.out.println("is natural");
					// Only if the natural has not already been indicated in the bar
					if (!naturalsInEffect.contains(midiPitchClass)) {
						accid = "n";
						naturalsInEffect.add(midiPitchClass);
					}
					// Natural for flat
					if (keySigs.contains((pitch-1) % 12)) {
						gridRowInd = Arrays.asList(currMpcg).indexOf((pitch-1) % 12);
					}
					// Natural for sharp
					else if (keySigs.contains((pitch+1) % 12)) {
						gridRowInd = Arrays.asList(currMpcg).indexOf((pitch+1) % 12);
					}
				}
				// If accidental
				else {
					if (verbose) System.out.println("is accidental");
					// Find pitch of next note of different pitch. If there is none, the voice
					// ends with a sequence of the same pitches (i.e., with a repetition of 
					// the last pitch), and nextPitch remains -1
					NotationVoice nv = p.getScore().get(voice).get(0);
					int nextPitch = -1;	
					// If not last note
					if (!(nv.get(nv.size()-1).getMetricTime().equals(onset))) {
						for (int j = 0; j < nv.size(); j++) {
							if (nv.get(j).getMetricTime().equals(onset)) {
								int currNextPitch = nv.get(j+1).get(0).getMidiPitch();
								if (currNextPitch != pitch) {
									nextPitch = currNextPitch;
									break;
								}
							}
						}
					}
					// Sharps (if last note, assume sharp )
					if ((nextPitch != -1 && nextPitch > pitch) || nextPitch == -1) {
						if (verbose) System.out.println("is sharp");
						gridRowInd = Arrays.asList(currMpcg).indexOf((pitch-1) % 12);
						// Only if the accidental has not already been indicated in the bar
						if (!sharpsInEffect.contains(midiPitchClass)) {
							accid = "s";
							// If pitch is already a sharp: double sharp
							if (Arrays.asList(currMpcg).contains(midiPitchClass) && 
								mpcSharps.contains(midiPitchClass)) {
								accid = "x";
							}
							sharpsInEffect.add(midiPitchClass);
						}
					}
					// Flats
					else if (nextPitch != -1 && nextPitch < pitch) {
						if (verbose) System.out.println("is flat");
						gridRowInd = Arrays.asList(currMpcg).indexOf((pitch+1) % 12);
						// Only if the accidental has not already been indicated in the bar
						if (!flatsInEffect.contains(midiPitchClass)) {
							accid = "f";
							// If pitch is already a flat: double flat
							if (Arrays.asList(currMpcg).contains(midiPitchClass) &&
								mpcFlats.contains(midiPitchClass)) {
								accid = "ff";
							}
							flatsInEffect.add(midiPitchClass);
						}
					}
					else {
						System.out.println("dit dus");
					}
				}
			}
			pname = String.valueOf(currPcg[gridRowInd]);
			if (verbose) {
				System.out.println("pname                    " + pname);
				System.out.println("accid                    " + accid);
				System.out.println("oct                      " + oct);
			}
			curr[STRINGS.indexOf("pname")] = "pname='" + pname + "'"; 
			curr[STRINGS.indexOf("oct")] = "oct='" + oct + "'";
			if (!accid.equals("")) {
				curr[STRINGS.indexOf("accid")] = "accid='" + accid + "'";
			}

			// 2. Set tie, dur, dots
			Rational remainingInBar = barEnd.sub(onset);
			// Single-bar note
			if (durRounded.isLessOrEqual(remainingInBar)) {
//\\				System.out.println("CASE: single-bar note");
//				Rational durRoundedTripletised = durRounded;
				List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null : 
					isTripletOnset(tripletOnsetPairs, onset);
				List<Object> noteData = 
					getNoteData(i, iTab, diminution, curr, durRounded, gridVal, /*bar,*/ onset, metPos, 
					mi, tripletInfo, tripletOnsetPairs);
				pitchOctAccTie.addAll((List<String[]>) noteData.get(0));
				indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
			}	
			// Multi-bar note
			else {
//\\				System.out.println("CASE: multi-bar note");
				// Check how many bars the note spans
				List<Rational> subNoteDurs = new ArrayList<>();
				List<Rational> subNoteDursOnsets = new ArrayList<>();
				subNoteDurs.add(remainingInBar);
				subNoteDursOnsets.add(onset);
				subNoteDursOnsets.add(onset.add(remainingInBar));
				Rational remainder = durRounded.sub(remainingInBar);
				// In the case of a tablature with predicted durations, those of the final chord
				// can be incorrectly predicted too long, thus extending beyond endOffset 
				if (offset.isGreater(endOffset)) {
					offset = endOffset;
				}
				int endBar = (offset.equals(endOffset)) ? mi.get(mi.size()-1)[Timeline.MI_LAST_BAR] : 
					Timeline.getMetricPosition(offset, mi)[0].getNumer();
				
				List<Integer> bars = 
					IntStream.rangeClosed(bar, endBar).boxed().collect(Collectors.toList());
				for (int j = bar+1; j <= endBar; j++) {
					Rational currBarLen = Transcription.getMeter(j, mi);
					if (remainder.isGreaterOrEqual(currBarLen)) {
						subNoteDurs.add(currBarLen);
						subNoteDursOnsets.add( // herr man
							subNoteDursOnsets.get(subNoteDursOnsets.size()-1).add(currBarLen)); 
						remainder = remainder.sub(currBarLen);
					}
					else {
						if (!remainder.equals(Rational.ZERO)) {
							subNoteDurs.add(remainder);
						}
					}
				}
				// For each subnote
				Rational currOnset = onset;
				Rational currMetPos = metPos;
				for (int j = 0; j < subNoteDurs.size(); j++) {
					Rational currSubNoteDur = subNoteDurs.get(j);
					Rational currSubNoteDurOnset = subNoteDursOnsets.get(j);
//					Rational currSubNoteDurTripletised = currSubNoteDur;
					List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null : 
						isTripletOnset(tripletOnsetPairs, currSubNoteDurOnset);
					List<Object> noteData = 
						getNoteData(i, iTab, diminution, curr, currSubNoteDur, gridVal, /*bars.get(j),*/ 
						currOnset, currMetPos, mi, tripletInfo, tripletOnsetPairs);
					List<String[]> subNote = (List<String[]>) noteData.get(0);
					int subNoteInd = 0;
					String tie = "";
					if (j == 0) {
						tie = (subNote.size() == 1) ? "i" : "m";
						// Set subNoteInd to that of the last sub-subnote
						subNoteInd = subNote.size()-1;
					}
					else if (j == subNoteDurs.size()-1) {
						tie = (subNote.size() == 1) ? "t" : "m";
					}
					else {
						tie = "m";
					}
					subNote.get(subNoteInd)[STRINGS.indexOf("tie")] = "tie='" + tie + "'";
					pitchOctAccTie.addAll(subNote);
					indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
					currOnset = currOnset.add(currSubNoteDur);
					currMetPos = Rational.ZERO;
				}
			}

			// If the note is the voice's last and its offset does not equal the piece end
			NotationVoice nv = p.getScore().get(voice).get(0);
			// If last note
			if ((nv.get(nv.size()-1).getMetricTime().equals(onset)) && !offset.equals(endOffset)) {
				// Add rest to fill up current bar (if applicable) 
				Rational restCurrentBar = barEnd.sub(offset);
				if (restCurrentBar.isGreater(Rational.ZERO)) {
					Rational metPosRestCurrentBar = metPos.add(durRounded);
//					Rational restCurrentBarTripletised = restCurrentBar;
					List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null : 
						isTripletOnset(tripletOnsetPairs, offset);
					List<Object> noteData = 
						getNoteData(-1, -1, diminution, new String[STRINGS.size()], restCurrentBar, gridVal,
						/*bar,*/ offset, metPosRestCurrentBar, mi, tripletInfo, tripletOnsetPairs);
					pitchOctAccTie.addAll((List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
				}
				// Add bar rests for all remaining bars
				for (int b = bar+1; b <= numBars; b++) {
					String[] restStr = new String[STRINGS.size()];
					restStr[STRINGS.indexOf("dur")] = "dur='bar'";
					pitchOctAccTie.add(restStr);
					Integer[] restInt = new Integer[INTS.size()];
					Arrays.fill(restInt, -1);
					restInt[INTS.indexOf("bar")] = b;
					restInt[INTS.indexOf("metPosNum")] = 0;
					restInt[INTS.indexOf("metPosDen")] = 1;
					restInt[INTS.indexOf("dur")] = -1; // n/a (bar rest)
					restInt[INTS.indexOf("dots")] = -1; // n/a (bar rest)
					indBarOnsMpDurDots.add(restInt);
				}	
			}
			noteAttribPerVoiceStrings.get(voice).addAll(pitchOctAccTie);
			noteAttribPerVoiceInts.get(voice).addAll(indBarOnsMpDurDots);
		}

		// Add unique IDs to all notes and rests
		// Note: voice, bar, seq number in bar, pitch+oct, metPos, index in trans, index in tab)
		// Rest: voice, bar, seq number in bar, r, metPos
		for (int v = 0; v < numVoices; v++) {
			List<String[]> strings = noteAttribPerVoiceStrings.get(v);
			List<Integer[]> ints = noteAttribPerVoiceInts.get(v);
			int initBar = ints.get(0)[INTS.indexOf("bar")];
			int seq = 0;
			for (int j = 0; j < strings.size(); j++) {
				String[] currStr = strings.get(j);
				Integer[] currInt = ints.get(j);
				int currBar = currInt[INTS.indexOf("bar")]; 
				if (currBar > initBar) {
					seq = 0;
					initBar = currBar;
				}
				String ID = v + "." + currBar + "." + seq + ".";
				String pi = currStr[STRINGS.indexOf("pname")];
				String metPos = 
					currInt[INTS.indexOf("metPosNum")] + "/" + currInt[INTS.indexOf("metPosDen")];
				currStr[STRINGS.indexOf("metPos")] = metPos;
				// In case of a note
				if (pi != null) {
					pi = String.valueOf(pi.charAt(pi.indexOf("'") + 1));
					String oct = currStr[STRINGS.indexOf("oct")];
					oct = String.valueOf(oct.charAt(oct.indexOf("'") + 1));
					ID += pi + oct + "." + metPos + "." + currInt[INTS.indexOf("ind")] + "." + 
						currInt[INTS.indexOf("indTab")];
				}
				// In case of a rest
				else {
					ID += "r" + "." + metPos;
				}
				currStr[STRINGS.indexOf("ID")] = "xml:id='" + ID + "'";
				seq++;				
			}
		}

		int voice = 0;
		List<String[]> voiceStr = noteAttribPerVoiceStrings.get(voice);
		List<Integer[]> voiceInt = noteAttribPerVoiceInts.get(voice);
		if (verbose) {
			for (int i = 0; i < voiceStr.size(); i++) {
				System.out.println(Arrays.toString(voiceInt.get(i)) + " -- " + Arrays.toString(voiceStr.get(i)));
			}
		}
		// Initialise empty lists organised per bar, per voice
		// List<List<List<?>>>			superlist: each element is a bar
		// List<List<?>>				1st get() = bar: each element is a voice
		// List<?>						2nd get() = voice: each element is a note
		List<List<List<Integer[]>>> noteAttributesPerBarPerVoiceInt = new ArrayList<>();
		List<List<List<String[]>>> noteAttributesPerBarPerVoiceStr = new ArrayList<>();
		for (int b = 0; b < numBars; b++) {
			List<List<Integer[]>> voicesForThisBarInt = new ArrayList<>();
			List<List<String[]>> voicesForThisBarStr = new ArrayList<>();
			for (int v = 0; v < numVoices; v++) {
				voicesForThisBarInt.add(new ArrayList<Integer[]>());
				voicesForThisBarStr.add(new ArrayList<String[]>());
			}
			noteAttributesPerBarPerVoiceInt.add(voicesForThisBarInt);
			noteAttributesPerBarPerVoiceStr.add(voicesForThisBarStr);
		}
		// Populate lists
		for (int i = 0; i < numVoices; i++) {
			for (int j = 0; j < noteAttribPerVoiceInts.get(i).size(); j++) {
				Integer[] currInt = noteAttribPerVoiceInts.get(i).get(j);
				String[] currStr = noteAttribPerVoiceStrings.get(i).get(j);
				int currBar = currInt[INTS.indexOf("bar")];
				noteAttributesPerBarPerVoiceInt.get(currBar-1).get(i).add(currInt);
				noteAttributesPerBarPerVoiceStr.get(currBar-1).get(i).add(currStr);
			}
		}
		
		// Sort the content for each voice per bar by onset (for correct rendering of 
		// in-voice chords) set metPos in String[] to null
		// For each bar
		for (int i = 0; i < noteAttributesPerBarPerVoiceInt.size(); i++) {
			List<List<Integer[]>> currVoicesPerBarInt = noteAttributesPerBarPerVoiceInt.get(i);
			List<List<String[]>> currVoicesPerBarStr = noteAttributesPerBarPerVoiceStr.get(i);
			// For each voice
			for (int j = 0; j < currVoicesPerBarInt.size(); j++) {
				List<Integer[]> notesForCurrVoiceInt = currVoicesPerBarInt.get(j);
				List<String[]> notesForCurrVoiceStr = currVoicesPerBarStr.get(j);

				// Sort
				List<String[]> sortedStr = new ArrayList<String[]>(notesForCurrVoiceStr);
				ToolBox.sortByString(sortedStr, STRINGS.indexOf("metPos"), "Rational");
				List<Integer> newInds = new ArrayList<>();
				sortedStr.forEach(s -> newInds.add(notesForCurrVoiceStr.indexOf(s)));
				List<Integer[]> sortedInt = new ArrayList<>();
				newInds.forEach(ind -> sortedInt.add(notesForCurrVoiceInt.get(ind)));
				// set metPos to null
				sortedStr.forEach(s -> s[STRINGS.indexOf("metPos")] = null);
				// Replace with sorted lists 
				currVoicesPerBarInt.set(j, sortedInt);
				currVoicesPerBarStr.set(j, sortedStr);
			}
		}

		List<Object> res = new ArrayList<>();
		res.add(noteAttributesPerBarPerVoiceInt);
		res.add(noteAttributesPerBarPerVoiceStr);		
		return res;
	}


	/**
	 * Gets the note information for the note at index i, given as input a String[] containing
	 * only the attributes pname, oct, and accid (or only <code>null</code> in case of a rest). 
	 *               
	 * @param i
	 * @param iTab
	 * @param curr
	 * @param argDur Is tripletised (has nominal value).
	 * @param gridVal
	 * @param onset Is untripletised (has actual value).
	 * @param metPos Is untripletised (has actual value).
	 * @param mi
	 * @param tripletInfo
	 * @param tripletOnsetPairs
	 * @return <ul>
	 *         <li>As element 0: a List<String[]> containing, for each unit fraction the note can be divided 
	 *               into, a String[] containing the attributes as listed in STRINGS.</li>
	 *         <li>As element 1: a List<Integer[]> containing, for each unit fraction the note can be divided 
	 *               into, an Integer[] containing the attributes as listed in INTS.</li>
	 *         </ul>      
	 * In case of a simple (non-dotted, non-compound) or dotted note, the lists returned have
	 * only one element; in case of a non-dotted compound note (e.g., a half tied to an eighth)
	 * the lists have more than one element.
	 */
	private static List<Object> getNoteData(int i, int iTab, int diminution, String[] curr, Rational argDur, 
		Rational gridVal, Rational onset, Rational metPos, List<Integer[]> mi, 
		List<Boolean> tripletInfo, List<Rational[]> tripletOnsetPairs) {

		List<String[]> currPitchOctAccTie = new ArrayList<>();
		List<Integer[]> currIndBarOnsMpDurDots = new ArrayList<>();

		// Determine the unit fractions (which, if the note or rest has a triplet onset
		// time, are tripletised) 
		// NB: argDur is tripletised, meaning that uf and durAsRat below are as well
		if (tripletInfo != null && tripletInfo.contains(Boolean.TRUE)) {
			argDur = argDur.mul(TRIPLETISER);
		}
		List<Rational> uf = getUnitFractions(argDur, gridVal);

		int numDots = getNumDots(uf);
		boolean isRest = curr[STRINGS.indexOf("pname")] == null;
		boolean isSimple = (uf.size() == 1 && numDots == 0);
		boolean isDotted = numDots > 0;
		boolean isNonDottedCompound = (uf.size() > 1 && numDots == 0);

		Rational currOnset = onset;
		Rational currMetPos = metPos;
		// Iterate through the unit fractions. In the case of a simple or dotted note, the 
		// for loop breaks at the end of k = 0  
		for (int k = 0; k < uf.size(); k++) {
			if (verbose) System.out.println("k = " + k);
			int currBar = Timeline.getMetricPosition(currOnset, mi)[0].getNumer();
			String[] copyOfCurr = Arrays.copyOf(curr, curr.length);
			Rational durAsRat = uf.get(k);
			if (isDotted) {
				durAsRat = durAsRat.add(uf.get(1));
			}
			// Allow for breve (2/1) and long (4/1)
			int dur = -1;
			if (isSimple || isDotted) {
				if (uf.get(k).isLessOrEqual(Rational.ONE)) {
					dur = uf.get(k).getDenom();
				}
				else if (uf.get(k).equals(new Rational(2, 1))) {
					dur = BREVE;
				}
				else if (uf.get(k).equals(new Rational(4, 1))) {
					dur = LONG;
				}
			}
			else {
				if (durAsRat.isLessOrEqual(Rational.ONE)) {
					dur = durAsRat.getDenom();
				}
				else if (durAsRat.equals(new Rational(2, 1))) {
					dur = BREVE;
				}
				else if (durAsRat.equals(new Rational(4, 1))) {
					dur = LONG;
				}
			}
			String durStr = 
				(dur > 0) ? Integer.toString(dur) : ((dur == BREVE ? "breve" : "long"));
			copyOfCurr[STRINGS.indexOf("dur")] = "dur='" + durStr + "'";
			if (isSimple || isDotted) {
				if (numDots != 0) {
					copyOfCurr[STRINGS.indexOf("dots")] = "dots='" + numDots + "'";
				}
			}
			if (isNonDottedCompound) { // in nonDottedCompound case, numDots is always 0 
				if (!isRest) {
					String tie = (k == 0) ? "i" : ((k > 0 && k < uf.size()-1) ? "m" : "t");
					copyOfCurr[STRINGS.indexOf("tie")] = "tie='" + tie + "'";
				}
			}
			currPitchOctAccTie.add(copyOfCurr);

			Integer[] in = new Integer[INTS.size()];
			Arrays.fill(in, -1);
			Integer[] secondIn = null;
			in[INTS.indexOf("bar")] = currBar;
			in[INTS.indexOf("metPosNum")] = currMetPos.getNumer();
			in[INTS.indexOf("metPosDen")] = currMetPos.getDenom();
			in[INTS.indexOf("dur")] = dur;
			in[INTS.indexOf("dots")] = numDots;
			if (!isRest) {
				in[INTS.indexOf("ind")] = i;
				in[INTS.indexOf("indTab")] = iTab;				
			}
			in[INTS.indexOf("onsetNum")] = currOnset.getNumer();
			in[INTS.indexOf("onsetDen")] = currOnset.getDenom();
			
			// Set tripletOpen, tripletMid, and tripletClose
			in[INTS.indexOf("tripletOpen")] = 0;
			in[INTS.indexOf("tripletMid")] = 0;
			in[INTS.indexOf("tripletClose")] = 0;
			
			List<Boolean> openMidClose = (tripletOnsetPairs == null) ? null : 
				isTripletOnset(tripletOnsetPairs, currOnset);
			if (tripletOnsetPairs != null) {
				// Get any triplet information
				String[] last = currPitchOctAccTie.get(currPitchOctAccTie.size()-1);
				Rational[] top = 
					getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi, diminution);
				Rational currTripletLen = null;
				Rational currTripletOpenOnset = null;
				Rational metPosTripletOpen = null;
				Rational tripletBorder = null;
				Rational onsetTripletBorder = null;
				Rational offset = null;
				if (top != null) {
					currTripletOpenOnset = 
						getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi, diminution)[0];
					metPosTripletOpen = Timeline.getMetricPosition(currTripletOpenOnset, mi)[1];
					currTripletLen = top[3];
					tripletBorder = metPosTripletOpen.add(currTripletLen);
					onsetTripletBorder = currTripletOpenOnset.add(currTripletLen);
					offset = currMetPos.add(durAsRat.mul(DETRIPLETISER));
				}
				// If currOnset is the second part of a non-dotted compound note or rest that 
				// falls after tripletCloseOnset: set tripletCloseOnset to true 
				// NB: This does not apply to isSimple or isDotted, where there is no second part
				// E.g.: last H in voice 0 = tripletCloseOnset; last Q in voice 1, which
				// is tied to the W before, falls after it 
				// voice 0: W       H   H   H   H     |
				// voice 1: W       H.    W       (Q) |
				// (This happens in 5263_12_in_exitu_israel_de_egipto_desprez-3, bar 77, voice 2)
				if (isNonDottedCompound) {
					Rational[] topOnset = 
						getExtendedTripletOnsetPair(onset, tripletOnsetPairs, mi, diminution);
					Rational tripletOpenOnset = (topOnset != null) ? topOnset[0] : null;			
					Rational tripletCloseOnset = (topOnset != null) ? topOnset[1] : null;
					if (topOnset != null && currOnset.isGreater(tripletCloseOnset) && 
						currOnset.isLess(tripletOpenOnset.add(currTripletLen))) {
						if (verbose) System.out.println("within triplet but after tripletCloseOnset --> tripletClose");
						openMidClose.set(2, true);
					}
				}

				// If the note is tripletOpen
				if (openMidClose.get(0) == true) {
					if (verbose) System.out.println("is tripletOpen");
					in[INTS.indexOf("tripletOpen")] = 1;					
					// a. If note ends on triplet border: set to tripletClose
					if (offset.equals(tripletBorder)) {
						if (verbose) System.out.println("ends on tripletBorder --> tripletClose");
						in[INTS.indexOf("tripletClose")] = 1;
					}
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if (offset.isGreater(tripletBorder)) {
						if (verbose) System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen, 
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
					}					
				}
				// If the note is tripletMid
				if (openMidClose.get(1) == true) {
					if (verbose) System.out.println("is tripletMid");
					in[INTS.indexOf("tripletMid")] = 1;	
					// a. If note ends on triplet border: set to tripletClose
					if (offset.equals(tripletBorder)) {
						if (verbose) System.out.println("ends on tripletBorder --> tripletClose");
						in[INTS.indexOf("tripletClose")] = 1;
					}
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if (offset.isGreater(tripletBorder)) {
						if (verbose) System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen,
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
					}
				}
				// If the note is tripletClose
				if (openMidClose.get(2) == true) {
					if (verbose) System.out.println("is tripletClose");
					in[INTS.indexOf("tripletClose")] = 1;
					// a. If note ends on triplet border: no action required
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if(offset.isGreater(tripletBorder)) {
						if (verbose) System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen,
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
					}
				}
			}
			currIndBarOnsMpDurDots.add(in);
			if (secondIn != null) {
				currIndBarOnsMpDurDots.add(secondIn);
			}
			if (isSimple || isDotted) {
				if (verbose) System.out.println("break: isSimple || isDotted");
				break;
			}
			else {
				if (openMidClose != null && openMidClose.contains(true)) {
					durAsRat = durAsRat.mul(DETRIPLETISER);
				}
				currOnset = currOnset.add(durAsRat);
				currMetPos = currMetPos.add(durAsRat);
			}
		}
		// If the last element in currIndBarOnsMpDurDots is a tripletClose: check if the elements
		// up to tripletOpen are a single note occupying the whole tripletLen, all rests, or all
		// tied notes. If so, replace the triplet value by its non-triplet value with tripletLen
		int indLast = currIndBarOnsMpDurDots.size()-1;
		Integer[] last = currIndBarOnsMpDurDots.get(indLast);
		if (last[INTS.indexOf("tripletClose")] == 1) { // && currIndBarOnsMpDurDots.size() > 1) {
			List<Boolean> rests = new ArrayList<>();
			List<Boolean> ties = new ArrayList<>();
			// Find accompanying tripletOpen
			int indOpen = -1;
			for (int j = indLast; j >= 0; j--) { // was indLast-1
				boolean isTripletOpen = 
					currIndBarOnsMpDurDots.get(j)[INTS.indexOf("tripletOpen")] == 1;
				// If the element at index j is a rest: add
				if (currPitchOctAccTie.get(j)[STRINGS.indexOf("pname")] == null) {
					rests.add(true);
				}
				else {
					rests.add(false);
				}
				// If the element at index j is a tie: add
				String tieStr = currPitchOctAccTie.get(j)[STRINGS.indexOf("tie")]; 
				if (tieStr != null) {
					// In order for the triplet to be fully tied, the last element cannot be i;
					// the middle element cannot be i or t; and the first element cannot be t 
					if (j == indLast && (tieStr.equals("tie='m'") || tieStr.equals("tie='t'")) ||
						(j < indLast && !isTripletOpen) && tieStr.equals("tie='m'") ||
						isTripletOpen && (tieStr.equals("tie='i'") || tieStr.equals("tie='m'"))) {
						ties.add(true);
					}
					else {
						ties.add(false);
					}
				}
				// Break when tripleOpen is reached
				if (isTripletOpen) {
					indOpen = j;
					break;
				}
			}
			// If indOpen is reached and the triplet consists of a single note occupying the 
			// whole tripletLen, only of rests, or only of tied notes
			if (indOpen != -1 && !rests.contains(false) || indOpen != -1 && !ties.contains(false)) {
				if (verbose) System.out.println("rest/note of tripletLen replaced by untripletised version");
				boolean isTripletLen = indOpen == indLast; 
				Rational tripletisedDur = Rational.ZERO;
				for (int j = indOpen; j <= indLast; j++) {
					int durAsInt = currIndBarOnsMpDurDots.get(j)[INTS.indexOf("dur")];
					Rational toAdd = 
						(durAsInt > 0) ? new Rational(1, durAsInt) : // whole or shorter
						new Rational(durAsInt*-2, 1); // breve or long	
					tripletisedDur = tripletisedDur.add(toAdd);
				}
				// If the rest/note is not of tripletLen, it must be detripletised
				if (!isTripletLen) {
					tripletisedDur = tripletisedDur.mul(DETRIPLETISER);
				}
				// Change items at indOpen
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("dur")] = tripletisedDur.getDenom();
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("dots")] = -1;
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("tripletOpen")] = 0;
				// If the rest/note is of tripletLen, tripletClose, which will be set to 1,
				// must be reset too
				if (isTripletLen) {
					currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("tripletClose")] = 0;
				}
				//
				currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("dur")] = 
					"dur='" + tripletisedDur.getDenom() + "'";
				currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("dots")] = null;
				if (ties.size() > 0 && !ties.contains(false)) {
					// Tie for the first element (which can only be i or m) must be retained 
					// only if tie for the last element (which can only be m or t) is m
					if (currPitchOctAccTie.get(indLast)[STRINGS.indexOf("tie")].equals("tie='t'")) {
						currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("tie")] = null;
					}
				}
				// Remove items at indices after indOpen
				currIndBarOnsMpDurDots = currIndBarOnsMpDurDots.subList(0, indOpen+1);
				currPitchOctAccTie = currPitchOctAccTie.subList(0, indOpen+1);
			}
		}
		return Arrays.asList(new Object[]{currPitchOctAccTie, currIndBarOnsMpDurDots});
	}


	/**
	 * Splits a note or rest that crosses the triplet border in the part before the triplet 
	 * border (which is set to tripletClose) and the part after the triplet border (which is 
	 * set to tripletOpen).
	 * 
	 * NB: It is assumed that the splitting leads to no more complex notes than dotted notes.
	 *   
	 * @param firstPart
	 * @param remainder
	 * @param tripletLen
	 * @param tripletBorder
	 * @param onsetTripletBorder
	 * @param last
	 * @param curr
	 * @param in
	 * @param gridVal
	 * @param isRest
	 * 
	 * @return <ul>
	 *         <li>A List of two String[], the first element of which replaces the last of 
	 *             currPitchOctAccTie, and the second is to be added to currPitchOctAccTie</li>
	 *         <li>A List of two Integer[], whose elements are are to be added to currIndBarOnsMpDurDots</li>
	 *         </ul>     
	 */
	private static List<Object> handleNoteAcrossTripletBorder(Rational firstPart, 
		Rational remainder, Rational tripletLen, Rational tripletBorder, 
		Rational onsetTripletBorder, String[] last, String[] curr, Integer[] in, 
		Rational gridVal, boolean isRest) {
		
		// 1. Handle firstPart
		// a. In copyOfCurr (already added to currPitchOctAccTie)
		int durMEIFirstPart, numDotsFirstPart;
		if (!firstPart.equals(tripletLen)) {
			List<Rational> ufFirstPart = getUnitFractions(firstPart.mul(TRIPLETISER), gridVal);
			durMEIFirstPart = ufFirstPart.get(0).getDenom();
			numDotsFirstPart = getNumDots(ufFirstPart);
		}
		else {
			firstPart.reduce();
			durMEIFirstPart = firstPart.getDenom();
			numDotsFirstPart = 0;
		}
		// Adapt dur, dots, tie in last element of currPitchOctAccTie
		last[STRINGS.indexOf("dur")] = "dur='" + durMEIFirstPart + "'";
		if (numDotsFirstPart > 0 ) {
			last[STRINGS.indexOf("dots")] = "dots='" + numDotsFirstPart + "'";
		}
		if (!isRest) {
			// If last element of currPitchOctAccTie has 
			// (a) tie='i' or 'm': OK (remainder set to 'm')
			// (b) tie='t': NOK, set to 'm' (remainder set to 't')
			System.out.println(Arrays.toString(last));
			if (last[STRINGS.indexOf("tie")].equals("tie='t'")) {
				last[STRINGS.indexOf("tie")] = "tie='m'";
			}
		}
		// b. In in (still to be added to currIndBarOnsMpDurDots)
		// Adapt dur, dots, tripletClose
		in[INTS.indexOf("dur")] = durMEIFirstPart;
		if (numDotsFirstPart > 0 ) {
			in[INTS.indexOf("dots")] = numDotsFirstPart;
		}
		if (!firstPart.equals(tripletLen)) {
			in[INTS.indexOf("tripletOpen")] = 0;
			in[INTS.indexOf("tripletMid")] = 0;
			in[INTS.indexOf("tripletClose")] = 1;
		}
		else {
			in[INTS.indexOf("tripletOpen")] = 0;
			in[INTS.indexOf("tripletMid")] = 0;
			in[INTS.indexOf("tripletClose")] = 0;
		}

		// 2. Handle remainder
		// a. In secondCopyOfCurr (still to add to currPitchOctAccTie)
		String[] secondCopyOfCurr = Arrays.copyOf(curr, curr.length);
		int durMEIRemainder, numDotsRemainder;
		if (!remainder.equals(tripletLen)) {
			List<Rational> ufRemainder = getUnitFractions(remainder.mul(TRIPLETISER), gridVal);
			durMEIRemainder = ufRemainder.get(0).getDenom();
			numDotsRemainder = getNumDots(ufRemainder);
		}
		else {
			remainder.reduce();
			durMEIRemainder = remainder.getDenom();
			numDotsRemainder = 0;
		}
		// Adapt dur, dots, tie, metPos in secondCopyOfCurr
		secondCopyOfCurr[STRINGS.indexOf("dur")] = "dur='" + durMEIRemainder + "'";
		if (numDotsRemainder > 0 ) {
			secondCopyOfCurr[STRINGS.indexOf("dots")] = "dots='" + numDotsRemainder + "'";
		}
		if (!isRest) {
			// If last element of currPitchOctAccTie has 
			// (a) tie='i' or 'm': NOK, set to 'm'
			// (b) tie='t': OK
			if (last[STRINGS.indexOf("tie")].equals("tie='i'")) {
				secondCopyOfCurr[STRINGS.indexOf("tie")] = "tie='m'";
			}
		}

		if (!isRest) {
			secondCopyOfCurr[STRINGS.indexOf("metPos")] = "metPos='" + tripletBorder + "'";
		}

		// b. In secondIn (still to be added to currIndBarOnsMpDurDots)
		Integer[] secondIn = Arrays.copyOf(in, in.length);
		// Adapt onsetNum, onsetDen, metPosNum, metPosDen, dur, dots, tripletOpen
		secondIn[INTS.indexOf("dur")] = durMEIRemainder;
		if (numDotsFirstPart > 0 ) {
			secondIn[INTS.indexOf("dots")] = numDotsRemainder;
		}
		if (!remainder.equals(tripletLen)) {
			secondIn[INTS.indexOf("tripletOpen")] = 1;
			secondIn[INTS.indexOf("tripletMid")] = 0;
			secondIn[INTS.indexOf("tripletClose")] = 0;
		}
		else {
			secondIn[INTS.indexOf("tripletOpen")] = 0;
			secondIn[INTS.indexOf("tripletMid")] = 0;
			secondIn[INTS.indexOf("tripletClose")] = 0;
		}
		Rational o = onsetTripletBorder;
		o.reduce();
		Rational m = tripletBorder;
		m.reduce();
		secondIn[INTS.indexOf("onsetNum")] = o.getNumer();
		secondIn[INTS.indexOf("onsetDen")] = o.getDenom();
		secondIn[INTS.indexOf("metPosNum")] = m.getNumer();
		secondIn[INTS.indexOf("metPosDen")] = m.getDenom();
		
		return Arrays.asList(new Object[]{
			Arrays.asList(new String[][]{last, secondCopyOfCurr}),
			Arrays.asList(new Integer[][]{in, secondIn})}
		);
	}


	private List<String[][]> combine() {
		List<String[][]> combined = new ArrayList<String[][]>();
		Integer[][] mpcg = makeMIDIPitchClassGrid();
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid();
		
		for (int i = 0; i < keys.size(); i++) {
			String[][] curr = new String[3][7];
			// MIDI pitch classes
			 
		}
		
		return combined;
	}


	/**
	 * Returns, for the given key, the MIDI pitch classes of the key signature for that key. 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example Ab major: [10, 3, 8, 1] (= Bb, Eb, Ab, Dd)
	 * Example A major: [6, 1 8] (= F#, C#, G#)
	 * 
	 * @param key
	 * @return
	 */
	// TESTED
	static List<Integer> getMIDIPitchClassKeySigs(Integer[] key) {
		List<Integer> mpcKeySigs = new ArrayList<Integer>();
		
		int numAlt = key[Transcription.KI_KEY];
		// Flats
		if (numAlt < 0) {
			mpcKeySigs.addAll(mpcFlats.subList(0, -numAlt));
		}
		// Sharps
		else if (numAlt > 0) {
			mpcKeySigs.addAll(mpcSharps.subList(0, numAlt));
		}
		
		return mpcKeySigs;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the MIDI pitch classes
	 * for that key. 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example C major: [0, 2, 4, 5, 7, 9, 11]
	 * Example A major: [9, 11, 1, 2, 4, 6, 8]
	 * 
	 * @return
	 */
	// TESTED
	static Integer[][] makeMIDIPitchClassGrid() {
		List<Integer> semiTones = Arrays.asList(new Integer[]{2, 2, 1, 2, 2, 2, 1});
		
		Integer[][] keyGrid = new Integer[keys.size()][7];
		for (int i = 0; i < keys.size(); i++) {
			int currBeginPitch = keys.get(i)[1];
			List<Integer> asList = new ArrayList<Integer>();
			asList.add(currBeginPitch);
			for (int j = 0; j < semiTones.size()-1; j++) {
				asList.add((asList.get(j) + semiTones.get(j)) % 12);
			}
//			Collections.sort(asList);
			keyGrid[i] = asList.toArray(new Integer[asList.size()]);
		}
		return keyGrid;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the alterations going
	 * with the pitch classes for that key. 
	 * An alteration is either a flat ("f"), a sharp ("s"); no alteration is indicated by "n". 
	 * 
	 * @param MIDIPitchClassGrid
	 * @return
	 */
	// TESTED
	static String[][] makeAlterationGrid(Integer[][] MIDIPitchClassGrid) {
		List<Integer> diatonicPitchCl = Arrays.asList(new Integer[]{0, 2, 4, 5, 7, 9, 11});
		String[][] altGrid = new String[keys.size()][7];
		for (int i = 0; i < keys.size(); i++) {
			int currKey = keys.get(i)[0];
			String alt = "s";
			if (currKey < 0) {
				alt = "f";
			}
			List<String> asList = new ArrayList<String>();
			for (int p : MIDIPitchClassGrid[i]) {
				// Add sharp or flat of altered note
				// If there are no harmmonic equivalents
				if (Math.abs(currKey) < 6) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else {
						asList.add("n");
					}
				}
				// If there are harmonic equivalents 
				else if	(Math.abs(currKey) > 5) {
					if (!diatonicPitchCl.contains(p)) {
						asList.add(alt);
					}
					else if (diatonicPitchCl.contains(p)) {
						// cb, fb
						if (alt.equals("f") && (p == 11 || p == 4)) {
							asList.add(alt);
						}
						// e#, b#
						else if (alt.equals("s") && (p == 5 || p == 0)) {
							asList.add(alt);
						}
						else {
							asList.add("n");
						}
					}
				}
			}
			altGrid[i] = asList.toArray(new String[asList.size()]); 
		}
		return altGrid;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the pitch classes 
	 * for that key. 
	 * A pitch class is a note's nominal pitch, and has one of the values 
	 * ["c", "d", "e", "f", "g", "a", "b"].  
	 * 
	 * @return
	 */
	// TESTED
	static String[][] makePitchClassGrid() {
		String[] pitchCl = new String[]{"c", "d", "e", "f", "g", "a", "b"};		
		List<String> pitchClasses = new ArrayList<String>();
		for (String s : pitchCl) {
			pitchClasses.add(s);
		}

		int fromInd = 0;
		String[][] pitchClassGrid = new String[keys.size()][7];
		for (int i = 0; i < keys.size(); i++) {
			// Reorder pitchClasses: split at fromIdex and paste the first part after the second
			List<String> asList = 
				new ArrayList<String>(pitchClasses.subList(fromInd, pitchClasses.size())); 
			List<String> secondHalf = pitchClasses.subList(0, fromInd);
			asList.addAll(secondHalf);
			pitchClassGrid[i] = asList.toArray(new String[asList.size()]);
			// Increment fromInd to be the index of the note a fifth higher
			fromInd = (fromInd + 4) % 7;
		}
		return pitchClassGrid;
	}


	/**
	 * Represent the given tablature as a list of bars, each of which itself is a list of events
	 * in that bar.
	 *  
	 * @param tab
	 * @return
	 */
	private static List<List<String>> getTabData(Tablature tab) {
		List<List<String>> bars = new ArrayList<>();

		String ss = Symbol.SYMBOL_SEPARATOR;
		// Split into bars
		String cleanEncoding = tab.getEncoding().getCleanEncoding();
		// Remove EBI and split into systems
		cleanEncoding = cleanEncoding.replace(Symbol.END_BREAK_INDICATOR, "");
		String[] cleanEncodingSystems = cleanEncoding.split(Symbol.SYSTEM_BREAK_INDICATOR);
		// Remove leading barline (of any kind) for each system
		for (int i = 0; i < cleanEncodingSystems.length; i++) {
			String system = cleanEncodingSystems[i];
			String first = system.substring(0, system.indexOf(ss));
			// If barline
			if (Symbol.getConstantMusicalSymbol(first) != null && Symbol.getConstantMusicalSymbol(first).isBarline()) {
//			if (ConstantMusicalSymbol.isBarline(first)) {
				cleanEncodingSystems[i] = system.substring(system.indexOf(ss) + 1, system.length());
			}
		}

		for (String system : cleanEncodingSystems) {
			List<String> bar = new ArrayList<>();
			int start = 0;
			String event = "";
			// Split into bars, which themselves are split into event // TODO make method in Tablature
			for (int i = 0; i < system.length(); i++) {
				if (system.substring(i, i+1).equals(ss)) {
					String curr = system.substring(start, i);
					boolean isSpace = curr.equals(Symbol.SPACE.getEncoding());
					boolean isBarline = 
						Symbol.getConstantMusicalSymbol(curr) != null && Symbol.getConstantMusicalSymbol(curr).isBarline();
//					boolean isBarline = ConstantMusicalSymbol.isBarline(curr);
					if (!isSpace) {
						event = (event.length() == 0) ? event + curr : event + ss + curr;
					}
					if (isSpace || isBarline) {
						bar.add(event);
						event = "";
					}
					start = i+1;

					// If barline: add bar to bars
					if (isBarline) {
						bars.add(bar);
						bar = new ArrayList<>();
					}
				}
			}
		}
		return bars;
	}


	/**
	 * Gets the note information for the note at index i. As input is given a String[] containing
	 * only the attributes pname, oct, and accid (or only <code>null</code> in case of a rest). 
	 * Returns
	 * as element 0: a List<String[]> containing, for each unit fraction the note can be divided 
	 *               into, the original String[] completed with the dur and, if applicable, tie
	 *               and dots attributes (or only dur and, if applicable, dots in case of a rest)
	 * as element 1: a List<Integer[]> containing, for each unit fraction the note can be divided 
	 *               into, an Integer[] containing the note's index, bar, onset (numerator), onset 
	 *               (denominator), metric position (numerator), metric position (denominator),
	 *               duration, dots, tripletOpen, tripletMid, and tripletClose attributes 
	 *               (or only bar, dur, dots, tripletOpen, -Mid, and -Close in case of a rest)
	 *               
	 * In case of a simple (non-dotted, non-compound) or dotted note, the lists returned have
	 * only one element; in case of a non-dotted compound note (e.g., a half tied to an eighth)
	 * the lists have more than one element.
	 *               
	 * @param i
	 * @param iTab
	 * @param argDur Is tripletised (has nominal value).
	 * @param gridVal
	 * @param onset Is untripletised (has actual value).
	 * @param metPos Is untripletised (has actual value).
	 * @param mi
	 * @param tripletInfo
	 * @param tripletOnsetPairs
	 * @return
	 */
	private static List<Object> getNoteDataOLD(int i, int iTab, String[] curr, Rational argDur, 
		Rational gridVal, Rational onset, Rational metPos, List<Integer[]> mi, 
		List<Boolean> tripletInfo, List<Rational[]> tripletOnsetPairs) {

		int diminution = 0;
		List<String[]> currPitchOctAccTie = new ArrayList<>();
		List<Integer[]> currIndBarOnsMpDurDots = new ArrayList<>();

		// Determine the unit fractions (which, if the note or rest has a triplet onset
		// time, are tripletised) 
		// NB: argDur is tripletised, meaning that uf and durAsRat below are as well
		if (tripletInfo != null && tripletInfo.contains(Boolean.TRUE)) {
			argDur = argDur.mul(TRIPLETISER);
		}
		List<Rational> uf = getUnitFractions(argDur, gridVal);

		int numDots = getNumDots(uf);
		boolean isRest = curr[STRINGS.indexOf("pname")] == null;
		boolean isSimple = (uf.size() == 1 && numDots == 0);
		boolean isDotted = numDots > 0;
		boolean isNonDottedCompound = (uf.size() > 1 && numDots == 0);

		Rational currOnset = onset;
		Rational currMetPos = metPos;
		// Iterate through the unit fractions. In the case of a simple or dotted note, the 
		// for loop breaks at the end of k = 0  
		for (int k = 0; k < uf.size(); k++) {
			System.out.println("k = " + k);
			int currBar = Timeline.getMetricPosition(currOnset, mi)[0].getNumer();
			String[] copyOfCurr = Arrays.copyOf(curr, curr.length);
			Rational durAsRat = uf.get(k);
			if (isDotted) {
				durAsRat = durAsRat.add(uf.get(1));
			}
			// Allow for breve (2/1) and long (4/1)
			int dur = -1;
			if (isSimple || isDotted) {
				if (uf.get(k).isLessOrEqual(Rational.ONE)) {
					dur = uf.get(k).getDenom();
				}
				else if (uf.get(k).equals(new Rational(2, 1))) {
					dur = BREVE;
				}
				else if (uf.get(k).equals(new Rational(4, 1))) {
					dur = LONG;
				}
			}
			else {
				if (durAsRat.isLessOrEqual(Rational.ONE)) {
					dur = durAsRat.getDenom();
				}
				else if (durAsRat.equals(new Rational(2, 1))) {
					dur = BREVE;
				}
				else if (durAsRat.equals(new Rational(4, 1))) {
					dur = LONG;
				}
			}
			String durStr = 
				(dur > 0) ? Integer.toString(dur) : ((dur == BREVE ? "breve" : "long"));
			copyOfCurr[STRINGS.indexOf("dur")] = "dur='" + durStr + "'";
			if (isSimple || isDotted) {
				if (numDots != 0) {
					copyOfCurr[STRINGS.indexOf("dots")] = "dots='" + numDots + "'";
				}
			}
			if (isNonDottedCompound) { // in nonDottedCompound case, numDots is always 0 
				if (!isRest) {
					String tie = (k == 0) ? "i" : ((k > 0 && k < uf.size()-1) ? "m" : "t");
					copyOfCurr[STRINGS.indexOf("tie")] = "tie='" + tie + "'";
				}
			}
			currPitchOctAccTie.add(copyOfCurr);

			Integer[] in = new Integer[INTS.size()];
			Arrays.fill(in, -1);
			Integer[] secondIn = null;
			in[INTS.indexOf("bar")] = currBar;
			in[INTS.indexOf("metPosNum")] = currMetPos.getNumer();
			in[INTS.indexOf("metPosDen")] = currMetPos.getDenom();
			in[INTS.indexOf("dur")] = dur;
			in[INTS.indexOf("dots")] = numDots;
			if (!isRest) {
				in[INTS.indexOf("ind")] = i;
				in[INTS.indexOf("indTab")] = iTab;				
			}
			in[INTS.indexOf("onsetNum")] = currOnset.getNumer();
			in[INTS.indexOf("onsetDen")] = currOnset.getDenom();
			
			// Set tripletOpen, tripletMid, and tripletClose
			in[INTS.indexOf("tripletOpen")] = 0;
			in[INTS.indexOf("tripletMid")] = 0;
			in[INTS.indexOf("tripletClose")] = 0;
			
			List<Boolean> openMidClose = (tripletOnsetPairs == null) ? null : 
				isTripletOnset(tripletOnsetPairs, currOnset);
			if (tripletOnsetPairs != null) {
				// Get any triplet information
				String[] last = currPitchOctAccTie.get(currPitchOctAccTie.size()-1);
				Rational[] top = 
					getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi, diminution);
				Rational currTripletLen = null;
				Rational currTripletOpenOnset = null;
				Rational metPosTripletOpen = null;
				Rational tripletBorder = null;
				Rational onsetTripletBorder = null;
				Rational offset = null;
				if (top != null) {
					currTripletOpenOnset = 
						getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi, diminution)[0];
					metPosTripletOpen = Timeline.getMetricPosition(currTripletOpenOnset, mi)[1];
					currTripletLen = top[3];
					tripletBorder = metPosTripletOpen.add(currTripletLen);
					onsetTripletBorder = currTripletOpenOnset.add(currTripletLen);
					offset = currMetPos.add(durAsRat.mul(DETRIPLETISER));
				}
				// If currOnset is the second part of a non-dotted compound note or rest that 
				// falls after tripletCloseOnset: set tripletCloseOnset to true 
				// NB: This does not apply to isSimple or isDotted, where there is no second part
				// E.g.: last H in voice 0 = tripletCloseOnset; last Q in voice 1, which
				// is tied to the W before, falls after it 
				// voice 0: W       H   H   H   H     |
				// voice 1: W       H.    W       (Q) |
				// (This happens in 5263_12_in_exitu_israel_de_egipto_desprez-3, bar 77, voice 2)
				if (isNonDottedCompound) {
					Rational[] topOnset = 
						getExtendedTripletOnsetPair(onset, tripletOnsetPairs, mi, diminution);
					Rational tripletOpenOnset = (topOnset != null) ? topOnset[0] : null;			
					Rational tripletCloseOnset = (topOnset != null) ? topOnset[1] : null;
					if (topOnset != null && currOnset.isGreater(tripletCloseOnset) && 
						currOnset.isLess(tripletOpenOnset.add(currTripletLen))) {
						System.out.println("within triplet but after tripletCloseOnset --> tripletClose");
						openMidClose.set(2, true);
					}
				}

				// If the note is tripletOpen
				if (openMidClose.get(0) == true) {
					System.out.println("is tripletOpen");
					in[INTS.indexOf("tripletOpen")] = 1;
//nu				// If the note or rest equals tripletLen: remove dot in last and in
//					if (ToolBox.sumListRational(uf).mul(DETRIPLETISER).isEqual(currTripletLen)) {
//						last[STRINGS.indexOf("dots")] = null;
//						in[INTS.indexOf("dots")] = -1;
//					}
//					// If the note or rest does not equal tripletLen: set tripletOpen
//					else {
//						in[INTS.indexOf("tripletOpen")] = 1;
//					}
//nu				// Determine tripletOffsets
//					Rational currBarLen = Rational.ZERO;
//					for (Integer[] m : mi) {
//						if (currBar >= m[2] && currBar <= m[3]) {
//							currBarLen = new Rational(m[0], m[1]);
//							break;
//						}
//					}
//					List<Rational> tripletOffsets = new ArrayList<>();
//					int times = (int) currBarLen.div(currTripletLen).toDouble();
//					for (int j = 1; j <= times; j++) {
//						tripletOffsets.add(currTripletLen.mul(j));
//					}
//nu				// Check offset of remainder to check if it is in tripletOffsets.
//					// Example from 5256_05_inviolata_integra_desprez-2, bar 19, voice 0 (n = note; r = rest)
//					// --3-- --3--   
//					// H W   W   H | H
//					// n r   r   r   n
//					// uf for the rest will be [1/1, 1/1, 1/2]; but at k = 1, the remainder of
//					// uf does not need to be a triplet as it equals tripletLen, but can be a W
//					Rational offsetRemainder = null;
//					if (k != uf.size() - 1) {
//						Rational remainder = ToolBox.sumListRational(uf.subList(k+1, uf.size()));
//						offsetRemainder = offset.add(remainder.mul(DETRIPLETISER));
//					}
					
					// a. If note ends on triplet border: set to tripletClose
					if (offset.equals(tripletBorder)) {
						System.out.println("ends on tripletBorder --> tripletClose");
						in[INTS.indexOf("tripletClose")] = 1;
					}
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if (offset.isGreater(tripletBorder)) {
//nu					// a. If note ends on (one of the) triplet border(s): no triplet necessary
//						if (tripletOffsets.contains(offset) || tripletOffsets.contains(offsetRemainder)) {
//							in[INTS.indexOf("tripletOpen")] = 0;
//							if (isDotted || isSimple) {
//								// Reset dot (which tripletises the note) in last element of 
//								// currPitchOctAccTie and in
//								last[STRINGS.indexOf("dots")] = null;
//								in[INTS.indexOf("dots")] = -1;
//							}
//							if (isNonDottedCompound) {
//								// Reset dur and tie in last element of currPitchOctAccTie and in
//								// NB: there will be no dots, as the elements of uf are all non-dotted
//								last[STRINGS.indexOf("dur")] = "dur='" + currTripletLen.getDenom() + "'";
//								if (!isRest) {
//									// Adapt if there is a tie that is not 't'
//									if (last[STRINGS.indexOf("tie")].equals("tie='i'")) {
//										last[STRINGS.indexOf("tie")] = null;
//									}
//									if (last[STRINGS.indexOf("tie")].equals("tie='m'")) {
//										last[STRINGS.indexOf("tie")] = "tie='t'";
//									}
//								}
//								in[INTS.indexOf("dur")] = currTripletLen.getDenom();
//							}
//							// Add in to currIndBarOnsMpDurDots and break from for loop
//							currIndBarOnsMpDurDots.add(in);
//							break;
//						}

						System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen, 
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
					}					
				}
				// If the note is tripletMid
				if (openMidClose.get(1) == true) {
					System.out.println("is tripletMid");
					in[INTS.indexOf("tripletMid")] = 1;
					
					// a. If note ends on triplet border: set to tripletClose
					if (offset.equals(tripletBorder)) {
						System.out.println("ends on tripletBorder --> tripletClose");
						in[INTS.indexOf("tripletClose")] = 1;
					}
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if (offset.isGreater(tripletBorder)) {
//					if (offset.isGreaterOrEqual(tripletBorder)) {					
//nu					// a. If note ends on triplet border: set to tripletClose
//						if (offset.equals(tripletBorder)) {
//							System.out.println("ends on tripletBorder --> tripletClose");
//							in[INTS.indexOf("tripletClose")] = 1;
//						}

//nu						// b. If note ends after triplet border: split at border and set the first
//						// part to tripletClose and the second to tripletOpen
//						else {
						System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen,
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
//						}
					}
//nu				else {
//						in[INTS.indexOf("tripletMid")] = 1;
//					}
				}
				// If the note is tripletClose
				if (openMidClose.get(2) == true) {
					System.out.println("is tripletClose");
					in[INTS.indexOf("tripletClose")] = 1;
					// a. If note ends on triplet border: no action required
					// b. If note ends after triplet border: split at border and set the first
					// part to tripletClose and the second to tripletOpen
					if(offset.isGreater(tripletBorder)) {
						System.out.println("across triplet border --> split");
						Rational firstPart = tripletBorder.sub(currMetPos);
						Rational remainder = offset.sub(tripletBorder);
						List<Object> res = 
							handleNoteAcrossTripletBorder(firstPart, remainder, currTripletLen,
							tripletBorder, onsetTripletBorder, last, curr, in, gridVal, isRest);
						List<String[]> lastTwoStr = (List<String[]>) res.get(0);
						List<Integer[]> lastTwoInt = (List<Integer[]>) res.get(1);
						currPitchOctAccTie.set(currPitchOctAccTie.size()-1, lastTwoStr.get(0));
						currPitchOctAccTie.add(lastTwoStr.get(1));
						in = lastTwoInt.get(0);
						secondIn = lastTwoInt.get(1);
					}
				}
			}
			currIndBarOnsMpDurDots.add(in);
			if (secondIn != null) {
				currIndBarOnsMpDurDots.add(secondIn);
			}
			if (isSimple || isDotted) {
				System.out.println("break: isSimple || isDotted");
				break;
			}
			else {
				if (openMidClose != null && openMidClose.contains(true)) {
					durAsRat = durAsRat.mul(DETRIPLETISER);
				}
				currOnset = currOnset.add(durAsRat);
				currMetPos = currMetPos.add(durAsRat);
			}
		}
		// If the last element in currIndBarOnsMpDurDots is a tripletClose: check if the elements
		// up to tripletOpen are a single note occupying the whole tripletLen, all rests, or all
		// tied notes. If so, replace the triplet value by its non-triplet value with tripletLen
		int indLast = currIndBarOnsMpDurDots.size()-1;
		Integer[] last = currIndBarOnsMpDurDots.get(indLast);
		if (last[INTS.indexOf("tripletClose")] == 1) { // && currIndBarOnsMpDurDots.size() > 1) {
			List<Boolean> rests = new ArrayList<>();
			List<Boolean> ties = new ArrayList<>();
			// Find accompanying tripletOpen
			int indOpen = -1;
			for (int j = indLast; j >= 0; j--) { // was indLast-1
				boolean isTripletOpen = 
					currIndBarOnsMpDurDots.get(j)[INTS.indexOf("tripletOpen")] == 1;
				// If the element at index j is a rest: add
				if (currPitchOctAccTie.get(j)[STRINGS.indexOf("pname")] == null) {
					rests.add(true);
				}
				else {
					rests.add(false);
				}
				// If the element at index j is a tie: add
				String tieStr = currPitchOctAccTie.get(j)[STRINGS.indexOf("tie")]; 
				if (tieStr != null) {
					// In order for the triplet to be fully tied, the last element cannot be i;
					// the middle element cannot be i or t; and the first element cannot be t 
					if (j == indLast && (tieStr.equals("tie='m'") || tieStr.equals("tie='t'")) ||
						(j < indLast && !isTripletOpen) && tieStr.equals("tie='m'") ||
						isTripletOpen && (tieStr.equals("tie='i'") || tieStr.equals("tie='m'"))) {
						ties.add(true);
					}
					else {
						ties.add(false);
					}
				}
				// Break when tripleOpen is reached
				if (isTripletOpen) {
					indOpen = j;
					break;
				}
			}
			// If indOpen is reached and the triplet consists of a single note occupying the 
			// whole tripletLen, only of rests, or only of tied notes
			if (indOpen != -1 && !rests.contains(false) || indOpen != -1 && !ties.contains(false)) {
				System.out.println("triplet rest/note replaced by untripletised rest/note");
				boolean isTripletLen = indOpen == indLast; 
				Rational tripletisedDur = Rational.ZERO;
				for (int j = indOpen; j <= indLast; j++) {
					int durAsInt = currIndBarOnsMpDurDots.get(j)[INTS.indexOf("dur")];
					Rational toAdd = 
						(durAsInt > 0) ? new Rational(1, durAsInt) : // whole or shorter
						new Rational(durAsInt*-2, 1); // breve or long	
					tripletisedDur = tripletisedDur.add(toAdd);
				}
				// If the rest/note is not of tripletLen, it must be detripletised
				if (!isTripletLen) {
					tripletisedDur = tripletisedDur.mul(DETRIPLETISER);
				}
				// Change items at indOpen
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("dur")] = tripletisedDur.getDenom();
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("dots")] = -1;
				currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("tripletOpen")] = 0;
				// If the rest/note is of tripletLen, tripletClose, which will be set to 1,
				// must be reset too
				if (isTripletLen) {
					currIndBarOnsMpDurDots.get(indOpen)[INTS.indexOf("tripletClose")] = 0;
				}
				//
				currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("dur")] = 
					"dur='" + tripletisedDur.getDenom() + "'";
				currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("dots")] = null;
				if (ties.size() > 0 && !ties.contains(false)) {
					// Tie for the first element (which can only be i or m) must be retained 
					// only if tie for the last element (which can only be m or t) is m
					if (currPitchOctAccTie.get(indLast)[STRINGS.indexOf("tie")].equals("tie='t'")) {
						currPitchOctAccTie.get(indOpen)[STRINGS.indexOf("tie")] = null;
					}
				}
				// Remove items at indices after indOpen
				currIndBarOnsMpDurDots = currIndBarOnsMpDurDots.subList(0, indOpen+1);
				currPitchOctAccTie = currPitchOctAccTie.subList(0, indOpen+1);
			}
		}
		return Arrays.asList(new Object[]{currPitchOctAccTie, currIndBarOnsMpDurDots});
	}

}
