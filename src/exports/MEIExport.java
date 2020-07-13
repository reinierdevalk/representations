package exports;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.w3c.dom.svg.GetSVGDocument;

import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.math.Rational;
import representations.Tablature;
import representations.Transcription;
import tbp.ConstantMusicalSymbol;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.SymbolDictionary;
import tbp.TabSymbol;
import tbp.TabSymbolSet;
import tools.ToolBox;
import utility.DataConverter;

public class MEIExport {
	
	public static String rootDir = "F:/research/"; // TODO also defined in UI; should be in one place only
	public static String MEITemplatePath = rootDir + "data/" + "templates/MEI/"; // TODO suffix "data/" is defined inside Runner.setPathsToCodeAndData() 
	
	private static List<Integer> mpcFlats = 
		Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11, 4}); // Bb, Eb, Ab, Db, Gb, Cb, Fb,
	private static List<Integer> mpcSharps = 
		Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5, 0}); // F#, C#, G#, D#, A#, E#, B#
	
	private static final Rational TRIPLETISER = new Rational(3, 2);
	private static final Rational DETRIPLETISER = new Rational(2, 3);
	private static final int BREVE = -1;
	private static final int LONG = -2;
	
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
		"tripletOpen", "tripletMid", "tripletClose"});
	private static final String TAB = "    ";
	private static final String INDENT = TAB.repeat(6);
	
	public static void main(String[] args) {
		
		String testTabFile = "4471_40_cum_sancto_spiritu";
		testTabFile = "5263_12_in_exitu_israel_de_egipto_desprez-3";
//		testTabFile = "4465_33-34_memor_esto-2";
//		testTabFile = "5255_04_stabat_mater_dolorosa_desprez-2";
//		testTabFile = "5254_03_benedicta_es_coelorum_desprez-1";
//		testTabFile = "5256_05_inviolata_integra_desprez-2";
//		testTabFile = "5256_05_inviolata_integra_desprez-3";
		
		Tablature testTab = new Tablature(new File(
			"F:/research/publications/conferences-workshops/2019-ISMIR/paper/josquintab/tab/" +
			testTabFile + ".tbp"), false);
		exportTabMEIFile(testTab, "C:/Users/Reinier/Desktop/" + testTab.getPieceName() + "-tab");
		System.exit(0);
		
		String path = "C:/Users/Reinier/Desktop/MEI/";
		path = "C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/";
		
		String tabFile = "tab-int/3vv/newsidler-1544_2-nun_volget";
		tabFile = "1132_13_o_sio_potessi_donna_berchem_solo";
		
		// This must be a created Transcription and the second argument must be null
		Transcription trans = 
			new Transcription(
//			new File("F:/research/data/MIDI/tab-int/4vv/rotta-1546_15-bramo_morir.mid"),
//			new File("F:/research/data/MIDI/tab-int/3vv/newsidler-1544_2-nun_volget.mid"),
//			new File("F:/research/data/MIDI/" + tabFile + ".mid"),
//			new File("C:/Users/Reinier/Desktop/MEI/newsidler-1544_2-nun_volget-test.mid"),
//			new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/mapped/3610_033_inter_natos_mulierum_morales_T-rev-mapped.mid"),
//			new File("C:/Users/Reinier/Desktop/IMS-tours/fold_06-1025_adieu_mes_amours.mid"),
			new File("C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/Berchem_-_O_s'io_potessi_donna.mid"),
			null);
		
		Tablature tab = null; //new Tablature(new File("F:/research/data/encodings/" + tabFile + ".tbp"), false);
		tab = new Tablature(new File("F:/research/data/encodings/" + tabFile + ".tbp"), false);
//		Tablature tab = 
//			new Tablature(new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/tab/" + 
//			"3610_033_inter_natos_mulierum_morales_T-rev" + ".tbp"), false);

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
		String s = path + "newsidler-1544_2-nun_volget-test";
		s = path + "fold_06-1025_adieu_mes_amours";
		s = path + "Berchem_-_O_s'io_potessi_donna";
		List<Integer[]> mi = (tab == null) ? trans.getMeterInfo() : tab.getMeterInfo();
		
		exportMEIFile(trans, tab.getBasicTabSymbolProperties(), mi, trans.getKeyInfo(), 
			tab.getTripletOnsetPairs(), mismatchInds, grandStaff, s);
//		System.out.println(ToolBox.readTextFile(new File(s)));

//		String scoreType = grandStaff ? "grand_staff" : "score";
//		ToolBox.storeTextFile(mei, 
//			new File(path + t.getNumberOfVoices() + "vv/" + t.getPieceName() + "-" + 
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


	private static Integer[][] getStaffAndLayer(int numVoices, int voice) {
		Integer[][] voiceStaffLayer = new Integer[numVoices][3];
		if (numVoices == 3) {
			voiceStaffLayer[0] = new Integer[]{0, 1, 1};
			voiceStaffLayer[1] = new Integer[]{1, 2, 2};
			voiceStaffLayer[2] = new Integer[]{2, 2, 1};
		}
		if (numVoices == 4) {
			voiceStaffLayer[0] = new Integer[]{0, 1, 2};
			voiceStaffLayer[1] = new Integer[]{1, 1, 1};
			voiceStaffLayer[2] = new Integer[]{2, 2, 2};
			voiceStaffLayer[3] = new Integer[]{3, 2, 1};
		}
		return voiceStaffLayer;
	}


	/**
	 * Represent the given tablature as a list of bars, each of whih itself is a list of events
	 * in that bar.
	 *  
	 * @param tab
	 * @return
	 */
	private static List<List<String>> getTabData(Tablature tab) {
		List<List<String>> bars = new ArrayList<>();

		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		// Split into bars
		String cleanEncoding = tab.getEncoding().getCleanEncoding();
		// Remove EBI and split into systems
		cleanEncoding = cleanEncoding.replace(SymbolDictionary.END_BREAK_INDICATOR, "");
		String[] cleanEncodingSystems = cleanEncoding.split(SymbolDictionary.SYSTEM_BREAK_INDICATOR);
		// Remove leading barline (of any kind) for each system
		for (int i = 0; i < cleanEncodingSystems.length; i++) {
			String system = cleanEncodingSystems[i];
			String first = system.substring(0, system.indexOf(ss));
			// If barline
			if (ConstantMusicalSymbol.isBarline(first)) {
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
					boolean isSpace = curr.equals(ConstantMusicalSymbol.SPACE.getEncoding());
					boolean isBarline = ConstantMusicalSymbol.isBarline(curr);
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


	public static void exportTabMEIFile(Tablature tab, String path) {

		List<Integer[]> mi = tab.getMeterInfo();

		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template.xml"));
		String notationtypeStr = "tab.lute.italian"; // TODO give as param to method
		String tuningStr = "lute.renaissance.6"; // TODO give as param to method
		TabSymbolSet tss = TabSymbolSet.FRENCH_TAB; // TODO give as param to method
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;

		// 1. Make meiHead
		String[] meiHead = new String[MEI_HEAD.size()];
		meiHead[MEI_HEAD.indexOf("title")] = tab.getPieceName();
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		List<String[]> meters = new ArrayList<>();
		for (Integer[] in : mi) {
			meters.add(new String[]{
				"meter.count='" + in[0] + "'", 
				"meter.unit='" + in[1] + "'",
				(in[0] == 4 && in[1] == 4 || in[0] == 2 && in[1] == 2) ? " meter.sym='common'" : ""});
		}
		for (String[] s : meters) {
			System.out.println(Arrays.toString(s));
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
		staffGrpStr += INDENT + TAB + TAB + "<tuning tuning.standard='" + tuningStr + "'" + "/>" + "\r\n";		
		staffGrpStr += INDENT + TAB + "</staffDef>";

		res = res.replace("staffGrp_content_placeholder", staffGrpStr);

		List<List<String>> tabDataStr = getTabData(tab);

		// 3. Make bars
		// Organise the information per bar
		StringBuilder sb = new StringBuilder();
		int prevDur = 0; 
		for (int i = 0; i < tabDataStr.size(); i++) {
			List<String> currBar = tabDataStr.get(i);
			
			// Make XML content for currBar
			String currBarXML = "";
			String barline = "";
			// For each event
			for (int j = 0; j < currBar.size(); j++) {
				String currEvent = currBar.get(j);
				System.out.println("currEvent = " + currEvent);
				String[] currEventSplit = 
					(!currEvent.contains(ss)) ? new String[]{currEvent} : currEvent.split("\\" + ss);
				System.out.println("currEventSplit = " + Arrays.toString(currEventSplit));
				
				// Barline? End of bar reached; set barline if not single
				if (ConstantMusicalSymbol.isBarline(currEvent)) {
					// TODO currently only single and double barline possible
					if (currEvent.equals(ConstantMusicalSymbol.DOUBLE_BARLINE.getEncoding())) {
						barline = " right='dbl'";
					}
					if (i == tabDataStr.size()-1) {
						barline = " right='end'";
					}
				}
				else {
					// Remove any MS (the first has already been taken care of above; any other
					// are handled below)
					if (MensurationSign.getMensurationSign(currEventSplit[0]) != null) {
						currEventSplit = Arrays.copyOfRange(currEventSplit, 1, currEventSplit.length);
					}
					if (currEventSplit.length != 0) {
//						boolean isTriplet = false;
						// Determine dur
						int dur = prevDur;
						int dots = 0;
						RhythmSymbol rs = RhythmSymbol.getRhythmSymbol(currEventSplit[0]);
						if (rs != null) {
//							if (rs.getEncoding().startsWith(RhythmSymbol.tripletIndicator)) {
//								isTriplet = true;
//								System.out.println("starts with tripletIndicator");
//								System.out.println(Arrays.toString(meters.get(meterIndex)));
//								System.exit(0);
//							}
//							else {
							dots = rs.getNumDots();
							// Get undotted version if applicable 
							if (dots != 0) {
								rs = rs.getUndotted();
							}
							Rational durAsRat = Tablature.SMALLEST_RHYTHMIC_VALUE.mul(rs.getDuration());
							dur = durAsRat.getDenom();
							prevDur = dur;
//							}
						}
						// tabGrp
						String tabGrpID = "";
						currBarXML += INDENT + TAB.repeat(3) +
							"<tabGrp xml:id='" + tabGrpID + "'" + " " + "dur='" + dur + "'" +
							((dots > 0) ? " " + "dots='" + dots + "'" : "") + ">" + "\r\n";
						// tabRhythm
						if (rs != null) {
							String tabRhythmID = "";
							currBarXML += INDENT + TAB.repeat(4) + 
								"<tabRhythm xml:id='" + tabRhythmID + "'/>" + "\r\n"; 
						}
						// Rests are covered by the tabRhythm
						int start = (rs != null) ? 1 : 0; 
						// notes
						for (int k = start; k < currEventSplit.length; k++) {
							TabSymbol ts = TabSymbol.getTabSymbol(currEventSplit[k], tss);
							String noteID = "";
							currBarXML += INDENT + TAB.repeat(4) +
								"<note xml:id='" + noteID + "'" + " " + 
								"tab.course='" + ts.getCourse() + "'" + " " + 
								"tab.fret='" + ts.getFret() + "'" + "/>" + "\r\n";			
						}
						currBarXML += INDENT + TAB.repeat(3) + "</tabGrp>" + "\r\n"; 	
					}
				}
			}
			// Wrap currBar in measure-staff-layer elements
			if (i > 0) {
				sb.append(INDENT);
			}
			sb.append("<measure n='" + (i+1) + "'" + barline + ">" + "\r\n");
			sb.append(INDENT + TAB + "<staff n='1'" + ">" + "\r\n");
			sb.append(INDENT + TAB.repeat(2) + "<layer n='1'" + ">" + "\r\n");
			sb.append(currBarXML);
			sb.append(INDENT + TAB.repeat(2) + "</layer>" + "\r\n");
			sb.append(INDENT + TAB + "</staff>" + "\r\n");
			sb.append(INDENT + "</measure>");
			if (i < tabDataStr.size()-1) {
				sb.append("\r\n");
			}

			// Append meter change (if applicable)
			if (i < tabDataStr.size()-1) {
				// Check for meter change in first event of next bar
				List<String> nextBar = tabDataStr.get(i+1);
				String firstEventNext = nextBar.get(0);
				System.out.println("firstEventNext = " + firstEventNext);
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
					sb.append(INDENT + "<scoreDef" + " " + meterStr + "/>" + "\r\n");
					meterIndex++;
				}
			}
		}
		res = res.replace("section_content_placeholder", sb.toString());
		ToolBox.storeTextFile(res, new File(path + ".xml"));
	}


	private static boolean verbose = false;
	/**
	 * 
	 * @param trans Must be a Transcription created setting the encodingFile argument to null
	 *              (i.e., one that has basicNoteProperties).
	 * @param btp
	 * @param mi
	 * @param ki
	 * @param tripletOnsetPairs
	 * @param mismatchInds
	 * @param grandStaff
	 * @param path
	 */
	public static void exportMEIFile(Transcription trans, Integer[][] btp, List<Integer[]> mi,
		List<Integer[]> ki, List<Rational[]> tripletOnsetPairs, List<List<Integer>> mismatchInds, 
		boolean grandStaff, String path) {
		System.out.println("\r\n>>> MEIExport.exportMEIFile() called");

		List<Object> data = getData(trans, /*tab,*/ btp, mi, ki, tripletOnsetPairs);
		// Composition of dataInt (and dataStr):
		// dataStr.size() = number of bars in piece
		// dataStr.get(b).size() = voices in bar b (and in whole piece) 
		// dataStr.get(b).get(v).size() = notes in voice v
		List<List<List<Integer[]>>> dataInt = (List<List<List<Integer[]>>>) data.get(0);
		List<List<List<String[]>>> dataStr = (List<List<List<String[]>>>) data.get(1);
		int numVoices = dataStr.get(0).size();

//		Runner.setPathsToCodeAndData(UI.getRootDir(), false); // TODO only necessary for MEITemplatePath
//		String res = ToolBox.readTextFile(new File(Runner.MEITemplatePath + "template.xml"));
		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template.xml"));

		// 1. Make meiHead
		String[] meiHead = new String[MEI_HEAD.size()];
		meiHead[MEI_HEAD.indexOf("title")] = trans.getPieceName();
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		// 2. Make music
		// a. Make scoreDef. The scoreDef contains the initial meter (and key) sigs; any additional 
		// ones are stored in nonInitMeters and nonInitKeys
		Integer[] miInit = mi.get(0);
		Integer[] kiInit = ki.get(0);
		String scoreDefStr = 
			"key.sig='" + Math.abs(kiInit[0]) + (kiInit[0] < 0 ? "f" : "s") + "'" + " " +
			"meter.count='" + miInit[0] + "'" + " " + 
			"meter.unit='" + miInit[1] + "'" + 
			(miInit[0] == 4 && miInit[1] == 4 || miInit[0] == 2 && miInit[1] == 2 ? 
			" " + "meter.sym='common'" : "");
		res = res.replace("scoreDef_placeholder", scoreDefStr.trim());

		// List any successive meters
		List<String[]> nonInitMeters = new ArrayList<>();
		for (Integer[] in : mi.subList(1, mi.size())) {
			nonInitMeters.add(new String[]{
				"meter.count='" + in[0] + "'", 
				" meter.unit='" + in[1] + "'",
				(in[0] == 4 && in[1] == 4 || in[0] == 2 && in[1] == 2) ? " meter.sym='common'" : ""});
		}
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, 2).subList(1, mi.size());

		// b. Make staffGrp (goes inside scoreDef)
		String staffGrpAtt = "symbol='brace' barthru='true'";
		res = res.replace("staffGrp_placeholder", staffGrpAtt);
		String staffGrpStr = "";
		String[] gClef = new String[]{"G", "2"};
		String[] fClef = new String[]{"F", "4"};
		List<String[]> clefs = Arrays.asList(new String[][]{gClef, gClef, fClef, fClef});
		if (numVoices == 2) {
			clefs = Arrays.asList(new String[][]{gClef, fClef});
		}
		if (numVoices == 3) {
			clefs = Arrays.asList(new String[][]{gClef, fClef, fClef});
		}
		if (numVoices == 5) {
			clefs = Arrays.asList(new String[][]{gClef, gClef, fClef, fClef, fClef});
		}
		if (numVoices == 6) {
			clefs = Arrays.asList(new String[][]{gClef, gClef, gClef, fClef, fClef, fClef});
		}
		if (grandStaff) {
			staffGrpStr += 
				"<staffDef n='1' xml:id='s1' lines='5' clef.shape='G' clef.line='2'/>" + "\r\n";
			staffGrpStr += 
				INDENT + TAB + 
				"<staffDef n='2' xml:id='s2' lines='5' clef.shape='F' clef.line='4'/>";
		}
		else {
			for (int i = 0; i < numVoices; i++) {
				if (i > 0) {
					staffGrpStr += INDENT + TAB;
				}
				staffGrpStr += 
					"<staffDef n='" + (i+1) + "'" +" xml:id='s" + (i+1) + "'" + " lines='5'" +
					" clef.shape='" + clefs.get(i)[0] + "'" + " clef.line='" + clefs.get(i)[1] + 
					"'" + "/>"; 
				if (i < numVoices-1) {
					staffGrpStr += "\r\n";
				}
			}
		}
		res = res.replace("staffGrp_content_placeholder", staffGrpStr);

		// 3. Make bars
		// Organise the information (i) per voice, (ii) per bar for the python beaming script
		List<List<String>> unbeamedBarsPerVoice = new ArrayList<>();
		for (int j = 0; j < numVoices; j++) {
			unbeamedBarsPerVoice.add(new ArrayList<>());
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
				List<String> barList = 
					getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, mi, tripletOnsetPairs,
					mismatchInds, /*(i+1),*/ j);
				String barListAsStr = "";
				Rational currMeter = Transcription.getMeter(bar, mi); 
				barListAsStr += 
					"meter='" + currMeter.getNumer() + "/" + currMeter.getDenom() + "'" + "\r\n";
				for (int k = 0; k < barList.size(); k++) {
					barListAsStr += barList.get(k);
					if (k < barList.size()-1) {
						barListAsStr += "\r\n";
					}
				}
				unbeamedBarsPerVoice.get(j).add(barListAsStr);
			}
		}

		// TODO Give unbeamedBarsPerVoice to Python script and add beaming; save as res.txt
		// or better: read from console
		String beamed = ToolBox.readTextFile(new File("C:/Users/Reinier/Desktop/res.txt"));
		// Organise beamed as a List (per voice, per bar)
		List<List<String>> beamedOrg = new ArrayList<>();
		for (String voice : beamed.split("end of voice")) {
			beamedOrg.add(Arrays.asList(voice.split("end of bar")));
		}

		// For each bar
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dataStr.size(); i++) {
			if (verbose) System.out.println("bar = " + (i+1));
			List<List<Integer[]>> currBarInt = dataInt.get(i);
			List<List<String[]>> currBarStr = dataStr.get(i);
			String barline = "";
			if (meterChangeBars.contains(i+1)) {
				int ind = meterChangeBars.indexOf(i+1);
				if (verbose) System.out.println("ind = " + ind);
				sb.append(INDENT + "<scoreDef " +
					nonInitMeters.get(ind)[0] + nonInitMeters.get(ind)[1] + 
					nonInitMeters.get(ind)[2] + "/>" + "\r\n");
			}
			if (i > 0) {
				sb.append(INDENT);
			}
			if (i == dataStr.size()-1) {
				barline = " right='end'";
			}
			sb.append("<measure n='" + (i+1) + "'" + barline + ">" + "\r\n");

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
				Integer[][] vsl = getStaffAndLayer(numVoices, j);
				int staff, layer;
				if (!grandStaff) {
					staff = j+1;
					layer = 1;
				}
				else {
					staff = vsl[j][1];
					layer = vsl[j][2];
				}
				// Non-grand staff case: add staff
				if (!grandStaff) {
					sb.append(INDENT + TAB + "<staff n='" + staff + "'>" + "\r\n");
				}
				// Grand staff case: only add staff at first voice if previous voice has staff-1
				else {		
					if (j == 0 || (j > 0 && vsl[j][1] == (vsl[j-1][1] + 1))) {
						sb.append(INDENT + TAB + "<staff n='" + staff + "'>" + "\r\n");
					}
				}
				sb.append(INDENT + TAB.repeat(2) + "<layer n='" + layer + "'>" + "\r\n");
				
				boolean doUnbeamed = true;
				if (doUnbeamed) {
					// For each note
					List<String> barList = 
						getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, mi, tripletOnsetPairs,
						mismatchInds, /*(i+1),*/ j);
					
					String barListAsStr = "";
					for (int k = 0; k < barList.size(); k++) {
						barListAsStr += INDENT + TAB.repeat(3) + barList.get(k);
						if (k < barList.size()-1) {
							barListAsStr += "\r\n";
						}
					}
					sb.append(barListAsStr + "\r\n");
				}
				// Append (beamed) notes in the bar
				else {
					String currBar = beamedOrg.get(j).get(i);
					for (String note : currBar.trim().split("\r\n")) {
						sb.append(INDENT + TAB.repeat(3) + note + "\r\n");
					}
				}

				sb.append(INDENT + TAB.repeat(2) + "</layer>" + "\r\n");
				// Non-grand staff case: add staff
				if (!grandStaff) {
					sb.append(INDENT + TAB + "</staff>" + "\r\n");
				}
				// Grand staff case: only add staff if last voice or if next voice has staff+1
				else {
					if (j == numVoices-1 || (j < numVoices-1 && vsl[j][1] == (vsl[j+1][1] - 1))) {
						sb.append(INDENT + TAB + "</staff>" + "\r\n");
					}
				}
			}
			sb.append(INDENT + "</measure>");
			if (i < dataStr.size()-1) {
				sb.append("\r\n");
			}
		}
		res = res.replace("section_content_placeholder", sb.toString());

		ToolBox.storeTextFile(res, 
			new File(path + "-" + (grandStaff ? "grand_staff" : "score") + ".xml"));
	}


	/**
	 * Returns the XML for the bar represented by the given List<String[]> and List<Integer[]>.
	 * 
	 * @param currBarCurrVoiceInt
	 * @param currBarCurrVoiceStr
	 * @param mi
	 * @param mismatchInds
	 * @param tripletOnsetPairs
	 * @param argVoice
	 * @return
	 */
	static List<String> getBar(List<Integer[]> currBarCurrVoiceInt, List<String[]> 
		currBarCurrVoiceStr, List<Integer[]> mi, List<Rational[]> tripletOnsetPairs, 
		List<List<Integer>> mismatchInds, int argVoice) {
		List<String> barList = new ArrayList<>();
		int bar = currBarCurrVoiceInt.get(0)[INTS.indexOf("bar")];
		String barRestStr = 
			"<mRest " + "xml:id='" + argVoice + "."  + bar/*argBar*/ + "." + "0.r.0'" + "/>";

		int diminution = 1;
		if (mi.get(0).length == 5) {
			diminution = Tablature.getDiminution(bar, mi);
		}

		boolean isMappingCase = mismatchInds.get(0) == null;
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
			inc = mismatchInds.get(1); // TODO was ErrorCalculator.INCORRECT_VOICE
			over = mismatchInds.get(2); // TODO was ErrorCalculator.OVERLOOKED_VOICE
			sup = mismatchInds.get(3); // TODO was ErrorCalculator.SUPERFLUOUS_VOICE
			half = mismatchInds.get(4); // TODO was ErrorCalculator.HALF_VOICE
		}

		// If the bar does not contain any notes or rests (this happens when a voice ends
		// with one or more bars of rests)
		if (currBarCurrVoiceInt.size() == 0) {
			barList.add(barRestStr);
		}
		// If the bar contains notes/rests
		else {
			boolean isFullBarRest = true;
			for (int k = 0; k < currBarCurrVoiceStr.size(); k++) {
				String[] note = currBarCurrVoiceStr.get(k);
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
				// For each note
				for (int k = 0; k < currBarCurrVoiceStr.size(); k++) {
					String[] note = currBarCurrVoiceStr.get(k);
					Integer[] noteInt = currBarCurrVoiceInt.get(k);
//					Rational mp = new Rational(noteInt[INTS.indexOf("metPosNum")], 
//						noteInt[)INTS.indexOf("metPosDen")]);
//					Rational durRat = new Rational(Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom(),
//						noteInt[INTS.indexOf("dur")]);
//					int dur = (int) durRat.toDouble();
					int currOnsNum = currBarCurrVoiceInt.get(k)[INTS.indexOf("onsetNum")];
					int currOnsDen = currBarCurrVoiceInt.get(k)[INTS.indexOf("onsetDen")];
					
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
							currBarCurrVoiceInt.get(k)[INTS.indexOf("onsetDen")]);
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
					if (k < currBarCurrVoiceStr.size()-1) {
						if (currOnsNum == currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")]) {
							barList.add("<chord dur='" + noteInt[INTS.indexOf("dur")] + "'>");
							chordActive = true;
						}
					}
					
					String noteStr = "";
					// Add indent for any tuplet and/or chord
					if (tupletActive) {
						noteStr += TAB;
					}
					if (chordActive) {
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
					// Trim trailing spaces (not leading spaces, as there may be TABs)
					// (see https://stackoverflow.com/questions/12106757/removing-spaces-at-the-end-of-a-string-in-java) 
					noteStr = noteStr.replaceAll("\\s+$", "");
					noteStr = noteStr + "/>";
					barList.add(noteStr);
					// If there is an active in-voice chord: check if it must be closed
					if (chordActive) {
						Rational currOnset = new Rational(currOnsNum, currOnsDen);
						Rational nextOnset = new Rational(
							currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")],
							currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetDen")]);
						if ((k < currBarCurrVoiceStr.size()-1 &&
//							currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")] > currOnsNum) 
							nextOnset.isGreater(currOnset))
							|| k == currBarCurrVoiceStr.size()-1) {
							barList.add("</chord>");
							chordActive = false;
						}
					}
					// Check for any tripletClose to be placed after noteStr
					if (noteInt[INTS.indexOf("tripletClose")] == 1) {
						barList.add("</tuplet>");
						tupletActive = false;
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
		tripletOnsetPairs, List<Integer[]> mi) {

		Rational[] pairAndLen = null;
		int bar = Tablature.getMetricPosition(onset, mi)[0].getNumer();
		int diminution = 1;
		if (mi.get(0).length == 5) {
			diminution = Tablature.getDiminution(bar, mi);
		}

		for (Rational[] r : tripletOnsetPairs) {
			Rational tripletOpen = r[0];
			// Calculate tripletLen
			// 1. Undiminished, i.e., length (in multiples of Tablature.SRV) * Tablature.SRV 
			// * 2 (there are three notes of this unit in the time of two)
			// Example for a mi (length = 24): (24 * 1/96) * 2 = 1/2
			Rational currTripletLenUndim = r[2].mul(Tablature.SMALLEST_RHYTHMIC_VALUE).mul(2);
			// 2. Diminished
			Rational currTripletLen = currTripletLenUndim.mul(diminution);

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
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> getData(Transcription trans, Integer[][] btp, 
		List<Integer[]> mi, List<Integer[]> ki, List<Rational[]> tripletOnsetPairs) {

		Piece p = trans.getPiece();
		int numVoices = p.getScore().size();
		Rational gridVal = 
			(btp != null) ? Tablature.SMALLEST_RHYTHMIC_VALUE : // new Rational(1, 96);
			new Rational(1, 128); // // 14.03.2020 was 1/64
		Integer[][] bnp = trans.getBasicNoteProperties();

		// Get meter and key TODO assumed is a single key
		int numBars = mi.get(mi.size()-1)[3];
		Rational endOffset = Rational.ZERO;
		for (Integer[] m : mi) {
			Rational currMeter = new Rational(m[0], m[1]);
			int barsInCurrMeter = (m[3] - m[2]) + 1;
			endOffset = endOffset.add(currMeter.mul(barsInCurrMeter));
		}
		Integer[] key = ki.get(0);
		int numAlt = key[0];
		// Set initial bar and meter
		Integer[] initMi = mi.get(0);
		Rational meter = new Rational(initMi[0], initMi[1]);
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
			if (btp != null) {
				onset = new Rational(btp[iTab][Tablature.ONSET_TIME], 
					Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			}
			System.out.println("i = " + i + " (indTab = " + iTab + "); bar = " + 
				Tablature.getMetricPosition(onset, mi)[0].getNumer() + "; pitch = " + bnp[i][0]);
			Rational[] barMetPos = Tablature.getMetricPosition(onset, mi);
			int bar = barMetPos[0].getNumer();
			Rational metPos = barMetPos[1];

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
			if (btp != null) {
				durRounded = new Rational(btp[iTab][Tablature.MIN_DURATION], 
					Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
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
			}
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
				metPosPrev = Tablature.getMetricPosition(onsetPrev, mi)[1]; 
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
					System.out.println("CASE: single-bar rest");
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
						getNoteData(i, iTab, curr, durRest, gridVal, /*bar,*/
						onsetRest, metPosRest, mi, tripletInfo, tripletOnsetPairs);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Single-bar rest in the previous bar (onset is 0/x)
				else if (precedingInBar.equals(Rational.ZERO) && 
					durRest.isLessOrEqual(Transcription.getMeter(bar-1, mi))) {
					System.out.println("CASE: single-bar rest in previous bar");
					Rational onsetRest = offsetPrev;
					Rational metPosRest = 
						(currVoiceStrings.size() == 0) ? Rational.ZERO :
						Tablature.getMetricPosition(onsetRest, mi)[1];
//					Rational durRestTripletised = durRest;
					List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null :
						isTripletOnset(tripletOnsetPairs, onsetRest);
					List<Object> noteData = 
						getNoteData(i, iTab, curr, durRest, gridVal, /*bar-1,*/ onsetRest, 
						metPosRest, mi, tripletInfo, tripletOnsetPairs);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Multi-bar rest
				else {
					System.out.println("CASE: multi-bar rest");
					// Check how many bars the note spans
					List<Rational> subNoteDurs = new ArrayList<>();
					// subNoteDursOnsets contains the onsets of the subnotes
					List<Rational> subNoteDursOnsets = new ArrayList<>(); // herr man
					if (!precedingInBar.equals(Rational.ZERO)) {
						subNoteDurs.add(precedingInBar);
						subNoteDursOnsets.add(onset.sub(precedingInBar)); // herr man
					}
					Rational remainder = durRest.sub(precedingInBar);
					int beginBar = Tablature.getMetricPosition(offsetPrev, mi)[0].getNumer();
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
							getNoteData(i, iTab, curr, currSubNoteDur, gridVal, /*bars.get(j),*/ 
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
				System.out.println("CASE: single-bar note");
//				Rational durRoundedTripletised = durRounded;
				List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null : 
					isTripletOnset(tripletOnsetPairs, onset);
				List<Object> noteData = 
					getNoteData(i, iTab, curr, durRounded, gridVal, /*bar,*/ onset, metPos, 
					mi, tripletInfo, tripletOnsetPairs);
				pitchOctAccTie.addAll((List<String[]>) noteData.get(0));
				indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
			}	
			// Multi-bar note
			else {
				System.out.println("CASE: multi-bar note");
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
				int endBar = (offset.equals(endOffset)) ? mi.get(mi.size()-1)[3] : 
					Tablature.getMetricPosition(offset, mi)[0].getNumer();
				
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
						getNoteData(i, iTab, curr, currSubNoteDur, gridVal, /*bars.get(j),*/ 
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
						getNoteData(-1, -1, new String[STRINGS.size()], restCurrentBar, gridVal,
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
	static List<Object> getNoteData(int i, int iTab, String[] curr, Rational argDur, 
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
			int currBar = Tablature.getMetricPosition(currOnset, mi)[0].getNumer();
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
				Rational[] top = getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi);
				Rational currTripletLen = null;
				Rational currTripletOpenOnset = null;
				Rational metPosTripletOpen = null;
				Rational tripletBorder = null;
				Rational onsetTripletBorder = null;
				Rational offset = null;
				if (top != null) {
					currTripletOpenOnset = getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi)[0];
					metPosTripletOpen = Tablature.getMetricPosition(currTripletOpenOnset, mi)[1];
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
					Rational[] topOnset = getExtendedTripletOnsetPair(onset, tripletOnsetPairs, mi);
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
			int currBar = Tablature.getMetricPosition(currOnset, mi)[0].getNumer();
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
				Rational[] top = getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi);
				Rational currTripletLen = null;
				Rational currTripletOpenOnset = null;
				Rational metPosTripletOpen = null;
				Rational tripletBorder = null;
				Rational onsetTripletBorder = null;
				Rational offset = null;
				if (top != null) {
					currTripletOpenOnset = getExtendedTripletOnsetPair(currOnset, tripletOnsetPairs, mi)[0];
					metPosTripletOpen = Tablature.getMetricPosition(currTripletOpenOnset, mi)[1];
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
					Rational[] topOnset = getExtendedTripletOnsetPair(onset, tripletOnsetPairs, mi);
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


	/**
	 * Splits a note or rest that crosses the triplet border in the part before the triplet 
	 * border (which is set to tripletClose) and the part after the triplet border (which is 
	 * set to tripletOpen).
	 * 
	 * NB: It is assumed that the splitting leads to no more complex notes than dotted notes.
	 *   
	 * @param firstPart
	 * @param remainder
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
//		List<Rational> ufFirstPart = getUnitFractions(firstPart.mul(TRIPLETISER), gridVal);
//		int durMEIFirstPart = ufFirstPart.get(0).getDenom();
//		int numDotsFirstPart = getNumDots(ufFirstPart);
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
//		String[] last = currPitchOctAccTie.get(currPitchOctAccTie.size()-1);
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
//		List<Rational> ufRemainder = getUnitFractions(remainder.mul(TRIPLETISER), gridVal);
//		int durMEIRemainder = ufRemainder.get(0).getDenom();
//		int numDotsRemainder = getNumDots(ufRemainder);
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
		
//		secondCopyOfCurr[STRINGS.indexOf("metPos")] = 
//			"metPos='" + metPosTripletOpen.add(tripletLen) + "'";
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
//		Rational o = currTripletOpenOnset.add(tripletLen);
		Rational o = onsetTripletBorder;
//		Rational o = tripletOpenOnset.add(tripletLen);
		o.reduce();
//		Rational m = metPosTripletOpen.add(tripletLen);
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
	 * 
	 * Example Ab major: [10, 3, 8, 1] (= Bb, Eb, Ab, Dd)
	 * Example A major: [6, 1 8] (= F#, C#, G#)
	 * 
	 * 
	 * @param key
	 * @return
	 */
	// TESTED
	static List<Integer> getMIDIPitchClassKeySigs(Integer[] key) {
		List<Integer> mpcKeySigs = new ArrayList<Integer>();
		
		int numAlt = key[0];
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

}
