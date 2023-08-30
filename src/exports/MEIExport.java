package exports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.utility.math.Rational;
import interfaces.PythonInterface;
import representations.Tablature;
import representations.Transcription;
import structure.ScoreMetricalTimeLine;
import structure.ScorePiece;
import structure.Timeline;
import structure.metric.Utils;
import tbp.Encoding;
import tbp.Event;
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

	public static final Rational TRIPLETISER = new Rational(3, 2);
	private static final Rational DETRIPLETISER = new Rational(2, 3);
	private static final int BREVE = -1;
	private static final int LONG = -2;
	
	private static boolean ONLY_TAB, ONLY_TRANS, TAB_AND_TRANS;
	
	private static boolean verbose = false;
	
//	// KEYS contains, for each key, the number of sharps (positive) or flats (negative) and 
//	// the MIDI pitch class of the tonic
//	private static final List<Integer[]> KEYS_OLD;
//	static {
//		KEYS_OLD = new ArrayList<Integer[]>();
//		KEYS_OLD.add(new Integer[]{-7, 11}); // Cb
//		KEYS_OLD.add(new Integer[]{-6, 6}); // Gb
//		KEYS_OLD.add(new Integer[]{-5, 1}); // Db
//		KEYS_OLD.add(new Integer[]{-4, 8}); // Ab
//		KEYS_OLD.add(new Integer[]{-3, 3}); // Eb
//		KEYS_OLD.add(new Integer[]{-2, 10}); // Bb
//		KEYS_OLD.add(new Integer[]{-1, 5}); // F
//		KEYS_OLD.add(new Integer[]{0, 0}); // C
//		KEYS_OLD.add(new Integer[]{1, 7}); // G
//		KEYS_OLD.add(new Integer[]{2, 2}); // D
//		KEYS_OLD.add(new Integer[]{3, 9}); // A
//		KEYS_OLD.add(new Integer[]{4, 4}); // E
//		KEYS_OLD.add(new Integer[]{5, 11}); // B
//		KEYS_OLD.add(new Integer[]{6, 6}); // F#
//		KEYS_OLD.add(new Integer[]{7, 1}); // C#
//	}

	// For each key, represented by number of flats (-) or sharps (+), the MIDI 
	// pitch class of the key and its minor parallel
	public static final Map<Integer, Integer[]> KEY_SIG_MPCS;
	static {
		KEY_SIG_MPCS = new LinkedHashMap<Integer, Integer[]>();
		KEY_SIG_MPCS.put(-7, new Integer[]{11, 8}); // Cb/ab
		KEY_SIG_MPCS.put(-6, new Integer[]{6, 3}); // Gb/eb
		KEY_SIG_MPCS.put(-5, new Integer[]{1, 10}); // Db/bb
		KEY_SIG_MPCS.put(-4, new Integer[]{8, 5}); // Ab/f
		KEY_SIG_MPCS.put(-3, new Integer[]{3, 0}); // Eb/c
		KEY_SIG_MPCS.put(-2, new Integer[]{10, 7}); // Bb/g
		KEY_SIG_MPCS.put(-1, new Integer[]{5, 2}); // F/d
		KEY_SIG_MPCS.put(0, new Integer[]{0, 9}); // C/a
		KEY_SIG_MPCS.put(1, new Integer[]{7, 4}); // G/e
		KEY_SIG_MPCS.put(2, new Integer[]{2, 11}); // D/b
		KEY_SIG_MPCS.put(3, new Integer[]{9, 6}); // A/f#
		KEY_SIG_MPCS.put(4, new Integer[]{4, 1}); // E/c#
		KEY_SIG_MPCS.put(5, new Integer[]{11, 8}); // B/g#
		KEY_SIG_MPCS.put(6, new Integer[]{6, 3}); // F#/d#
		KEY_SIG_MPCS.put(7, new Integer[]{1, 10}); // C#/a#
	}


//	public static final Map<Integer, Integer[]> KEY_SIGS;
//	static { KEY_SIGS = new LinkedHashMap<Integer, Integer[]>();
//		KEY_SIGS.put(4, new Integer[]{4, 1});   // E/c#
//		KEY_SIGS.put(3, new Integer[]{9, 6});   // A/f#
//		KEY_SIGS.put(2, new Integer[]{2, 11});  // D/b
//		KEY_SIGS.put(1, new Integer[]{7, 4});   // G/e
//		KEY_SIGS.put(0, new Integer[]{0, 9});   // C/a
//		KEY_SIGS.put(-1, new Integer[]{5, 2});  // F/d
//		KEY_SIGS.put(-2, new Integer[]{10, 7}); // Bb/g
//		KEY_SIGS.put(-3, new Integer[]{3, 0});  // Eb/c
//		KEY_SIGS.put(-4, new Integer[]{8, 5});  // Ab/f
//	}
	

//	private static final Map<Integer, Integer[]> KEY_ACCID;
//	static { KEY_ACCID = new LinkedHashMap<Integer, Integer[]>();
//		KEY_ACCID.put(4, new Integer[]{6, 1, 8, 3});   // E/c#
//		KEY_ACCID.put(3, new Integer[]{6, 1, 8});      // A/f#
//		KEY_ACCID.put(2, new Integer[]{6, 1});         // D/b
//		KEY_ACCID.put(1, new Integer[]{6});            // G/e
//		KEY_ACCID.put(0, new Integer[]{});             // C/a
//		KEY_ACCID.put(-1, new Integer[]{10});          // F/d
//		KEY_ACCID.put(-2, new Integer[]{10, 3});       // Bb/g
//		KEY_ACCID.put(-3, new Integer[]{10, 3, 8});    // Eb/c
//		KEY_ACCID.put(-4, new Integer[]{10, 3, 8, 1}); // Ab/f
//	}

	// F#, C#, G#, D#, A#, E#, B#
	private static final List<Integer> KEY_ACCID_MPC_SHARP = Arrays.asList(6, 1, 8, 3, 10, 5, 0); 
	private static final List<String> KEY_ACCID_PC_SHARP = Arrays.asList("f", "c", "g", "d", "a", "e", "b");
	// Bb, Eb, Ab, Db, Gb, Cb, Fb
	private static final List<Integer> KEY_ACCID_MPC_FLAT = Arrays.asList(10, 3, 8, 1, 6, 11, 4);
	private static final List<String> KEY_ACCID_PC_FLAT = Arrays.asList("b", "e", "a", "d", "g", "c", "f");

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

	// reduceCMNDur adapts the Transcription durations to those of the tablature; what we want 
	// is the reverse
	// a. Adapt the metric times and onsets in the transcription to fit those resulting from the 
	//    fixed rhythm symbol values in the tab
	// b. Adapt the values of the rhythm signs in the tab to fit the trans
	// Thesis MIDI: already made the MIDI to follow the RS values in the tab
	// JosquinTab MIDI: existing MIDI must be adapted (not tab) as to keep consistency in 
	// the meaning of the RS
	private static boolean adaptTransDur = true; // TODO remove once tab.dur.sym.ratio is in MEI schema
	
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
			Encoding.EXTENSION));
		
		testTab = new Tablature(new File(
			"C:/Users/Reinier/Desktop/test-capirola/tab/capirola-1520-et_in_terra_pax" + 
			Encoding.EXTENSION));
		
//		exportTabMEIFile(testTab, "C:/Users/Reinier/Desktop/test-capirola/capirola-1520-et_in_terra_pax" + "-tab");	
//		System.exit(0);
		
		String notationtypeStr = "tab.lute.italian"; // TODO give as param to method
		String tuningStr = "lute.renaissance.6"; // TODO give as param to method
		
		String path = "C:/Users/Reinier/Desktop/MEI/";
		path = "C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/";
		
		String tabFile = "thesis-int/3vv/newsidler-1544_2-nun_volget";
		tabFile = "1132_13_o_sio_potessi_donna_berchem_solo";

		String pieceName = "capirola-1520-et_in_terra_pax";
		pieceName = "1025_adieu_mes_amours";

		// This must be a created Transcription and the second argument must be null
		Transcription trans = 
//			null;
			new Transcription(
////			new File("F:/research/data/MIDI/thesis-int/4vv/rotta-1546_15-bramo_morir.mid")
////			new File("F:/research/data/annotated/MIDI/thesis-int/3vv/newsidler-1544_2-nun_volget.mid")
////			new File("F:/research/data/MIDI/" + tabFile + MIDIImport.EXTENSION)
////			new File("C:/Users/Reinier/Desktop/MEI/newsidler-1544_2-nun_volget-test.mid")
////			new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/mapped/3610_033_inter_natos_mulierum_morales_T-rev-mapped.mid")
////			new File("C:/Users/Reinier/Desktop/IMS-tours/fold_06-1025_adieu_mes_amours.mid")
////			new File("C:/Users/Reinier/Desktop/IMS-tours/example/MIDI/Berchem_-_O_s'io_potessi_donna.mid")
////			new File("C:/Users/Reinier/Desktop/test-capirola/mapped/" + pieceName + MIDIImport.EXTENSION)
//			new File("C:/Users/Reinier/Desktop/beaming/mapped/" + pieceName + MIDIImport.EXTENSION)
			new File("F:/research/experiments/thesis/exp_3.1/thesis-int/3vv/N/bwd/out/fold_03-judenkuenig-1523_2-elslein_liebes.mid")		
		);


//		Tablature tab = 
//			new Tablature(new File("F:/research/data/encodings/" + tabFile + Encoding.EXTENSION), true);
//		Tablature tab = 
////			new Tablature(new File("C:/Users/Reinier/Desktop/test-capirola/tab/" +
//			new Tablature(new File("C:/Users/Reinier/Desktop/beaming/tab/" +
//			pieceName + Encoding.EXTENSION), true);
		Tablature tab = 
			new Tablature(new File("F:/research/data/annotated/encodings/thesis-int/" + 
			"3vv/judenkuenig-1523_2-elslein_liebes" + 
//			"4vv/rotta-1546_15-bramo_morir" +
			Encoding.EXTENSION), true);
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
		boolean alignWithMetricBarring = true; // TODO remove because this is always true?
		String s = path + "newsidler-1544_2-nun_volget-test";
		s = path + "fold_06-1025_adieu_mes_amours";
		s = path + "Berchem_-_O_s'io_potessi_donna";
		s = "C:/Users/Reinier/Desktop/test-capirola/" + pieceName;
		s = "C:/Users/Reinier/Desktop/beaming/" + pieceName;
		s = "C:/Users/Reinier/Desktop/" + "judenkuenig-1523_2-elslein_liebes";
		
//		List<Integer[]> mi = (tab == null) ? trans.getMeterInfo() : tab.getMeterInfo();
		
		exportMEIFile(trans, tab, /*tab.getBasicTabSymbolProperties(), trans.getKeyInfo(), 
			tab.getTripletOnsetPairs(),*/ mismatchInds, grandStaff, tabOnTop, 
			alignWithMetricBarring, new String[]{s, ""});
//		System.out.println(ToolBox.readTextFile(new File(s)));

//		String scoreType = grandStaff ? "grand_staff" : "score";
//		ToolBox.storeTextFile(mei, 
//			new File(path + t.getNumberOfVoices() + Runner.voices + "/" + t.getPieceName() + "-" + 
//			scoreType + ".xml"));
//		System.out.println(Arrays.asList(data.get(0).get(0)));
	}


	public static Map<Integer, Integer[]> getKeySigMPCs() {
		return KEY_SIG_MPCS;
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
		if (midiPitch >= 24 && midiPitch < 36) {
			return 1; 
		}
		else if (midiPitch >= 36 && midiPitch < 48) {
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
			throw new RuntimeException("ERROR: r must be a multiple of 1/96 (ONLY_TAB case) " + 
				"or 1/128 (ONLY_TRANS/TAB_AND_TRANS cases) but is " + r.toString());
		}
		// If the numerator = 1: add to uf
		int num = r.getNumer();
		int den = r.getDenom();
		if (num == 1 || (num % 2 == 0 && num == num / (double) den)) {
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
				System.out.println();
				rounded.reduce();
				return rounded;
			}
		}
		return null;
	}


	/**
	 * Rounds (up or down) the given Rational to the closest value on the given grid.
	 * 
	 * @param r
	 * @param grid The grid values, including 0. The elements of the list are the grid values' numerators; 
	 *             the length-1 of the list gives the grid values' denominator. 
	 *             E.g., [0, 1, 2, 3, 4] = 0/4, 1/4, ..., 4/4
	 * @return
	 */
	// TESTED
	static Rational round(Rational r, List<Integer> gridNums) {
		int den = gridNums.size() -1;
		// If r falls on the grid
		if (r.mul(den).getDenom() == 1) {
			return r;
		}
		// If r does not fall on the grid
		else {
			// Separate r into base and fraction part (e.g., 144/96 = 1 48/96)
			int base = r.floor(); 
			Rational frac = new Rational(r.getNumer() - base * r.getDenom(), r.getDenom());
			for (int i = 0; i < gridNums.size()-1; i++) {
				Rational lowerGridVal = new Rational(gridNums.get(i), den);
				Rational upperGridVal = new Rational(gridNums.get(i+1), den);
				// If r falls between two grid values: check to which it is closest. In case
				// of a draw, return the larger grid value
				if (frac.isGreater(lowerGridVal) && frac.isLess(upperGridVal)) {
					double diffLower = Math.abs(frac.sub(lowerGridVal).toDouble());
					double diffUpper = Math.abs(frac.sub(upperGridVal).toDouble());
					if (diffLower < diffUpper) {
						return new Rational(base, 1).add(lowerGridVal);
					}
					else if (diffUpper < diffLower || diffUpper == diffLower) {
						return new Rational(base, 1).add(upperGridVal);
					}
				}
			}
		}
		return null;
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
		if (numVoices == 5) {
			staffAndLayer[0] = new Integer[]{1, 1};
			staffAndLayer[1] = new Integer[]{1, 2};
			staffAndLayer[2] = new Integer[]{2, 1};
			staffAndLayer[3] = new Integer[]{2, 2};
			staffAndLayer[4] = new Integer[]{2, 3};
		}
		if (numVoices == 6) {
			staffAndLayer[0] = new Integer[]{1, 1};
			staffAndLayer[1] = new Integer[]{1, 2};
			staffAndLayer[2] = new Integer[]{1, 3};
			staffAndLayer[3] = new Integer[]{2, 1};
			staffAndLayer[4] = new Integer[]{2, 2};
			staffAndLayer[5] = new Integer[]{2, 3};
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
	 * Gets the duration (in Tablature.SMALLEST_RHYTHMIC_VALUE) of the given XML duration with 
	 * the given number of dots.
	 * 
	 * @param XMLDur
	 * @param dots
	 * @return
	 */
	// TESTED
	static int getDur(int XMLDur, int dots) {
		int dur;
		
		// Get undotted duration
		// Breve (-1)
		if (XMLDur == -1) {
			dur = Symbol.LONGA.getDuration();
		}
		// W (1), H (2), Q (4), E (8), S (16), T (32)
		else {
			dur = Symbol.BREVIS.getDuration() / XMLDur;
		}
		
		// Get dotted duration
		if (dots > 0) {
			dur = getDottedNoteLength(dur,  dots);
		}	
		return dur;
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
			(!event.contains(ss)) ?	Symbol.getRhythmSymbol(event) : 
			Symbol.getRhythmSymbol(event.substring(0, event.indexOf(ss)));

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
			if (Symbol.getMensurationSign(currEventSplit[0]) == null) {
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
					TabSymbol ts = Symbol.getTabSymbol(currEventSplit[j], tss);
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
		System.out.println("\r\n>>> getStaffNum() called");
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
		System.out.println("\r\n>>> makeScoreDef() called");
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
		int count = currMi[Transcription.MI_NUM];
		int unit = currMi[Transcription.MI_DEN];
//		if (adaptTransDur) {
//			if (ONLY_TAB || TAB_AND_TRANS) {
//				diminution = currMi[Timeline.MI_DIM];
//				Rational undiminutedMeter = 
//					Timeline.diminuteMeter(new Rational(count, unit), diminution);
//				count = undiminutedMeter.getNumer();
//				unit = undiminutedMeter.getDenom();
//			}
//		}
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
//				"' tab.dur.sym.ratio='" + (adaptTransDur ? 1 : diminution) + "' xml:id='s1'>"); 
				"' tab.dur.sym.ratio='" + 1 + "' xml:id='s1'>"); 
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
				String ksStr = String.valueOf(Math.abs(ks)) + (ks == 0 ? "" : ((ks < 0) ? "f" : "s"));
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
	 * @param alignWithMetricBarring
	 * @param dict
	 */
	public static String exportMEIFile(Transcription trans, Tablature tab, List<List<Integer>> 
		mismatchInds, boolean grandStaff, boolean tabOnTop, boolean alignWithMetricBarring, 
		String[] dict) {
		System.out.println("\r\n>>> MEIExport.exportMEIFile() called");

		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template-MEI.xml"));
		String path = dict[0];
		String app = dict[1];
		
//		Integer[][] bnp = trans.getBasicNoteProperties();
//		for (int i : new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}) {
//			System.out.println(Arrays.toString(bnp[i]));
//		}
//		System.exit(0);
		
		if (tab != null) {
			if (trans == null) ONLY_TAB = true; else TAB_AND_TRANS = true;
		}
		else {
			ONLY_TRANS = true;
		}
		List<Integer[]> tabBarsToMetricBars = (tab != null) ? tab.mapTabBarsToMetricBars() : null;
		// mi from tab is the same as mi from trans in TAB_AND_TRANS case // TODO still true?
		List<Integer[]> mi = (tab != null) ? tab.getMeterInfo() : trans.getMeterInfo();
//		List<Integer[]> mi = (tab != null) ? tab.getTimeline().getMeterInfoOBS() : trans.getMeterInfo();
		int numMetricBars = mi.get(mi.size()-1)[Transcription.MI_LAST_BAR];
		int numTabBars = (tab != null) ? tabBarsToMetricBars.size() : -1;
		int numBars = !alignWithMetricBarring ? numTabBars : numMetricBars;
		Integer[][] btp = tab != null ? tab.getBasicTabSymbolProperties() : null;
		List<Integer[]> ki = trans != null ? trans.getKeyInfo() : null;
		if (ki != null && ki.size() == 0) { // TODO zondag
			ki.add(new Integer[]{-2, 0, 0, 0, 0, 0, 1});
		}
		List<Rational[]> tripletOnsetPairs = tab != null ? tab.getTripletOnsetPairs() : null; 
		String pieceName = tab != null ? tab.getName() : trans.getName();
		int numVoices = trans != null ? trans.getNumberOfVoices() : 0;
		List<String[]> tabMensSigns = tab != null ? tab.getMensurationSigns() : null;
		
		// List all bars in which the meter, or, if appropriate, the key changes 
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, Transcription.MI_FIRST_BAR);
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
			tab != null ? ToolBox.getItemsAtIndex(mi, Tablature.MI_DIM) : null;
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
		res = res.replace("date_placeholder", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		// TODO version
		res = res.replace("app_placeholder", app);

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
//			for (List<String> l : tabBars) {
//				for (String s : l) {
//					System.out.println(s);
//				}
//			}
			for (int i = 0; i < numBars; i++) {
				StringBuilder currTabBarAsStr = new StringBuilder();
//				System.out.println(tabBars.get(0));
//				System.out.println("...");
//				System.out.println(tabBars.get(1));
//				System.out.println("...");
//				System.out.println(tabBars.get(2));
//				System.out.println("...");
//				System.out.println(tabBars.get(3));
//				System.out.println("...");
//				System.out.println(tabBars.get(4));
//				System.out.println("...");
//				System.out.println(tabBars.get(5));
//				System.out.println("...");
//				System.out.println(tabBars.get(6));
//				System.out.println("...");
//				System.out.println(tabBars.get(7));
//				System.out.println("...");
//				tabBars.get(8).forEach(s -> System.out.println(s));
//				System.out.println("...");
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
//		System.out.println(tabBarsAsStr);
//		System.exit(0);
		// b. Trans bars
		List<String> transBarsAsStr = new ArrayList<>();
		if (ONLY_TRANS || TAB_AND_TRANS) {	
			List<Object> data = getData(tab, trans, mi, ki, tripletOnsetPairs);
			List<List<String>> transBars =
				getTransBars(data, tab, mi, tripletOnsetPairs, mismatchInds, grandStaff, tabOnTop,
				numVoices, dict[1]/*, path*/);
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
			String tabBar = tab != null ? tabBarsAsStr.get(i) : null;
			String transBar = ONLY_TAB ? null : transBarsAsStr.get(i);
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

		if (path != null) { 
			ToolBox.storeTextFile(
				res, new File(path + "-" + (grandStaff ? "grand_staff" : "score") + ".xml"));
			return null;
		}
		else {
			return res;
		}
	}


	/**
	 * Gets the duration, as a Rational, of the given undotted note when it is dotted with 
	 * the given number of dots.
	 * 
	 * @param undotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static Rational getDottedNoteLength(Rational undotted, int dots) {
		if (dots == 0) {
			return undotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1/2 + (1/2 * 1/2)
			// Two dots add 1/(2^2), e.g., H.. = 1/2 + (1/2 * 1/2) + (1/2 * 1/4)
			Rational dotted = undotted;
			for (int i = 1; i <= dots; i++) {
				Rational factor = new Rational(1, (int) Math.pow(2, i));
				Rational increment = undotted.mul(factor);
				dotted = dotted.add(increment);
			}
			return dotted;
		}
	}


	/**
	 * Gets the duration, as an int, of the given undotted note when it is dotted with 
	 * the given number of dots.
	 * 
	 * @param undotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static int getDottedNoteLength(int undotted, int dots) {
		if (dots == 0) {
			return undotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1 * H + (1/2 * H)             = 3/2 * H
			// Two dots add 1/(2^2), e.g., H.. = 1 * H + (1/2 * H) + (1/4 * H) = 7/4 * H 
			// The dotted length is the undotted length multiplied by this factor
			double factor = 1;
			for (int i = 1; i <= dots; i++) {
				factor += 1 / Math.pow(2, i);
			}
			return (int) (undotted * factor);
		}
	}


	/**
	 * Gets the duration, as an int, of the given dotted note when it is undotted with 
	 * the given number of dots.
	 * 
	 * @param dotted
	 * @param dots
	 * @return
	 */
	// TESTED
	public static int getUndottedNoteLength(int dotted, int dots) {
		if (dots == 0) {
			return dotted;
		}
		else {
			// Each dot d adds 1/(2^d) * the undotted length to the undotted note
			// One dot adds 1/(2^1), e.g., H.  = 1 * H + (1/2 * H)             = 3/2 * H
			// Two dots add 1/(2^2), e.g., H.. = 1 * H + (1/2 * H) + (1/4 * H) = 7/4 * H
			// The undotted length is the dotted length divided by this factor  
			double factor = 1;
			for (int i = 1; i <= dots; i++) {
				factor += 1 / Math.pow(2, i);
			}
			return (int) (dotted / factor);
		}
	}


	private static List<List<String>> getTabBars(Tablature tab, boolean alignWithMetricBarring,
		int staff) {
		System.out.println("\r\n>>> getTabBars() called");
		List<List<String>> tabBars = new ArrayList<>();
		
		String ss = Symbol.SYMBOL_SEPARATOR;
		String sp = Symbol.SPACE.getEncoding();
		TabSymbolSet tss = tab.getEncoding().getTabSymbolSet();
		List<Integer[]> mi = tab.getMeterInfo();
//		List<Integer[]> mi = tab.getTimeline().getMeterInfoOBS();
//		List<String[]> meters = new ArrayList<>();
//		int meterIndex = 0;
		List<Integer[]> tabBarsToMetricBars = tab.mapTabBarsToMetricBars();
		for (Integer[] in : tabBarsToMetricBars) {
			System.out.println(Arrays.toString(in));
		}

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
		
//		System.out.println(events.size());
//		for (Event e : events) {
//			System.out.println(e.getBar());
//			System.out.println(e.getEncoding());
//		}
//		System.exit(0);
		
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
//		System.out.println(eventsPerBar.size());
//		for (Event l : eventsPerBar.get(1)) {
//			System.out.println(l.getEncoding());
//		}
//		System.exit(0);
		int prevDur = -1;
		for (int i = 0; i < eventsPerBar.size(); i++) {
			if (startNewBar) {
				currBarAsXML = new ArrayList<>();
				currBarAsXML.add("<staff n='" + staff + "'>");
				currBarAsXML.add(TAB + "<layer n='1'>");
			}
			List<Event> currBarEvents = eventsPerBar.get(i);
			List<Integer> currBarDurs = new ArrayList<>();
//			System.out.println(("i = " + i));
			for (Event e : currBarEvents) {
//				System.out.println(e.getEncoding());
				if (Encoding.assertEventType(e.getEncoding(), tss, "RhythmSymbol") ||
					Encoding.assertEventType(e.getEncoding(), tss, "rest")) {
					RhythmSymbol rs = 
						Symbol.getRhythmSymbol(e.getEncoding().substring(0, 
						e.getEncoding().indexOf(Symbol.SYMBOL_SEPARATOR))); 
					currBarDurs.add(rs.getDuration());
					prevDur = rs.getDuration();
				}
				else if (Encoding.assertEventType(e.getEncoding(), tss, "TabSymbol") &&
					!(Encoding.assertEventType(e.getEncoding(), tss, "RhythmSymbol"))) {
					currBarDurs.add(prevDur);
				}
			}
//			System.out.println("currBarDurs:");
//			System.out.println(currBarDurs);

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
			int currMetricBar = tabBarsToMetricBars.get(currTabBar - 1)[Tablature.METRIC_BAR_IND];
			System.out.println(currMetricBar);
			int currDim = tab.getEncoding().getTimeline().getDiminution(currMetricBar);

			// For each event
			Rational barLen = Rational.ZERO;
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
				boolean isBarline = 
					Symbol.getConstantMusicalSymbol(currEvent) != null && Symbol.getConstantMusicalSymbol(currEvent).isBarline();
				if (!isBarline) {
//				if (Symbol.getConstantMusicalSymbol(currEvent) != null && !Symbol.getConstantMusicalSymbol(currEvent).isBarline()) {
//				if (!ConstantMusicalSymbol.isBarline(currEvent)) {
					// Get XML durations of currEvent, and, if applicable, currEventOrig
					Integer[] currDurXML = getXMLDur(currEvent);
					if (currDurXML == null) {
//						Rational currDurRat = new Rational(1, prevDurXML[0]);
						if (prevDurXML[1] != 0) {
//							currDurRat = currDurRat.mul(prevDurXML[1]);
						}
//						barLen = barLen.add(prevDurXML);
					}
					else {
//						barLen = barLen.add();
					}

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
							Symbol.getRhythmSymbol(sicEvent.substring(0, sicEvent.indexOf(ss)));
						int durSic; 
						if (rsSic != null) {
							durSic = rsSic.getDuration();
						}
						else {
							durSic = -1; // TODO get last specified duration before currEvent
						}
						RhythmSymbol rsCorr = 
							Symbol.getRhythmSymbol(corrEvent.substring(0, corrEvent.indexOf(ss)));
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
									Symbol.getRhythmSymbol(nextEvent.substring(0, 
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
				} // if (!isBarline)
			} // loop over currBarEvents

			if (alignWithMetricBarring) {
//				if (i < eventsPerBar.size() - 1) {
				Integer[] tbtmb = tabBarsToMetricBars.get(i);
				// Tab and metric bar overlap fully (tab bar:metric bar = 1:1 case)
				boolean equalToMetric = 
					tbtmb[Tablature.SECOND_METRIC_BAR_IND] == -1 &&	
					tbtmb[Tablature.TAB_BAR_REL_ONSET_IND] == 0 && 
					tbtmb[Tablature.METRIC_BAR_REMAINDER_IND] == 0;
				// Tab bar overlaps start of metric bar (tab bar:metric bar n:1 and 3:2 case)
				boolean overlapsStartMetric = 
					tbtmb[Tablature.SECOND_METRIC_BAR_IND] == -1 &&	
					tbtmb[Tablature.TAB_BAR_REL_ONSET_IND] == 0 && 
					tbtmb[Tablature.METRIC_BAR_REMAINDER_IND] > 0;
				// Tab bar overlaps middle of metric bar (tab bar:metric bar n:1 case)
				boolean overlapsMiddleMetric = 
					tbtmb[Tablature.SECOND_METRIC_BAR_IND] == -1 &&
					tbtmb[Tablature.TAB_BAR_REL_ONSET_IND] > 0 && 
					tbtmb[Tablature.METRIC_BAR_REMAINDER_IND] > 0;	
				// Tab bar overlaps end of current and start of next metric bar (tab bar:metric bar 3:2 case)
				boolean overlapsEndAndStartMetric = 
					tbtmb[Tablature.SECOND_METRIC_BAR_IND] != -1;
				// Tab bar overlaps end of metric bar (tab bar:metric bar n:1 and 3:2 case)
				boolean overlapsEndMetric = 
					tbtmb[Tablature.SECOND_METRIC_BAR_IND] == -1 &&
					tbtmb[Tablature.TAB_BAR_REL_ONSET_IND] > 0 && 
					tbtmb[Tablature.METRIC_BAR_REMAINDER_IND] == 0;
	
				// Place metric barline and tab barline
				if (equalToMetric || overlapsEndMetric) {
					System.out.println("equalToMetric || overlapsEndMetric");
					currBarAsXML.add(TAB + "</layer>");
					currBarAsXML.add("</staff>");
					tabBars.add(currBarAsXML);
					startNewBar = true;
				}
				// Place only tab barline 					
				else if (overlapsStartMetric || overlapsMiddleMetric) {
					System.out.println("overlapsStartMetric || overlapsMiddleMetric");
					currBarAsXML.add(TAB.repeat(2) + "<barLine/>");
					startNewBar = false;
				}
//				else if (overlapsMiddleMetric) {
////				System.out.println("i = " + i + ", overlapsMiddleMetric");
//					currBarAsXML.add(TAB.repeat(2) + "<barLine/>");
//					startNewBar = false;
//				}
				// Place only metric barline
				else if (overlapsEndAndStartMetric) {
					System.out.println("overlapsEndAndStartMetric");
					// Get the duration of the end part of the tab bar (belonging to the next metric bar) 
					int toNextMetricBarDur = tabBarsToMetricBars.get(i+1)[Tablature.TAB_BAR_REL_ONSET_IND];
//					System.out.println(toNextMetricBarDur);
					// Get the durations of the tabGrps that fall into the end part
					List<Integer> tabGrpDurs = new ArrayList<>();
					for (int k = currBarDurs.size() - 1; k > 0; k--) {
						tabGrpDurs.add(currBarDurs.get(k));
						if (ToolBox.sumListInteger(tabGrpDurs) == toNextMetricBarDur) {
							break;
						}
					}
//					System.out.println("numTabGroupsToNextMetricBar:");
//					System.out.println(tabGrpDurs.size());
					// Move the tabGrps that fall into the end part from currBarAsXML to nextCurrBarAsXML
					List<String> nextCurrBarAsXML = new ArrayList<>();
					for (int k = currBarAsXML.size() - 1; k > 0; k--) {
						nextCurrBarAsXML.add(0, currBarAsXML.get(k));
						if (StringUtils.countMatches(String.join(",", nextCurrBarAsXML), 
							"<tabGrp") == tabGrpDurs.size()) {
							break;
						}
					}
					currBarAsXML = currBarAsXML.subList(0, currBarAsXML.size() - nextCurrBarAsXML.size());
					// Complete currBarAsXML with staff and layer elements and add
					currBarAsXML.add(TAB + "</layer>");
					currBarAsXML.add("</staff>");
					tabBars.add(currBarAsXML);
						
//					for (String s : currBarAsXML) {
//						System.out.println(s);
//					}
//					System.out.println("-------");
						
					// Complete nextCurrBarAsXML with staff and layer elements and set currBarAsXML
					nextCurrBarAsXML.add(0, "<staff n='" + staff + "'>");
					nextCurrBarAsXML.add(1, TAB + "<layer n='1'>");
					nextCurrBarAsXML.add(TAB.repeat(2) + "<barLine/>");
					currBarAsXML = nextCurrBarAsXML;
					startNewBar = false;
						
//					for (String s : currBarAsXML) {
//						System.out.println(s);
//					}
						
//					for (int k = currBarAsXML.size() - 1; k > 0; k--) {
//						String s = currBarAsXML.get(k);
//						toNextMetricBar.add(0, s);
//						if (s.contains("<tabGrp")) {
//							int start = s.indexOf("dur='") + "dur='".length();
//							int XMLDurInt = Integer.parseInt(s.substring(start, s.indexOf("'", start)));
//							int dotsInt = 0;
//							if (s.contains("dots='")) {
//								start = s.indexOf("dots='") + "dots='".length();
//								dotsInt = Integer.parseInt(s.substring(start, s.indexOf("'", start)));
//							}
//							cumulativeDur += getDur(XMLDurInt, dotsInt);
//							if (cumulativeDur == toNextMetricBarDur) {
//								break;
//							}
//						}
//					}
				}
				
//				// Place metric barline and tab barline
//				else if (overlapsEndMetric) {
////					System.out.println("i = " + i + ", overlapsEndMetric");
//					currBarAsXML.add(TAB + "</layer>");
//					currBarAsXML.add("</staff>");
//					tabBars.add(currBarAsXML);
//					startNewBar = true;
//				}

//				if (i == 41) {
//					System.out.println(tabBars.size());
//					for (List<String> l : tabBars) {
//						System.out.println("TAB BARRRRRRRRRR");
//						for (String s : l) { 
//							System.out.println(s);
//						}
//					}
//					System.exit(0);
//				}

//				boolean combineTabBars = 			
////				tabBarsToMetricBars.get(i + 1)[Tablature.METRIC_BAR_IND] == currMetricBar;
//				tabBarsToMetricBars.get(i)[Tablature.METRIC_BAR_REMAINDER_IND] != 0;

//				}
				
//				else {
//					currBarAsXML.add(TAB + "</layer>");
//					currBarAsXML.add("</staff>");
//					tabBars.add(currBarAsXML);
//					startNewBar = true;
//				}
				
				boolean old = false;
				if (old) {
					boolean combineTabBars = 
						i < eventsPerBar.size() - 1 && 
//						tabBarsToMetricBars.get(i + 1)[Tablature.METRIC_BAR_IND] == currMetricBar;
						tabBarsToMetricBars.get(i)[Tablature.METRIC_BAR_REMAINDER_IND] != 0;
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

		} // loop over eventsPerBar

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


	public static List<String[]> ornFull = new ArrayList<>();
	private static List<List<String>> getTransBars(List<Object> data, Tablature tab, List<Integer[]> mi,
		List<Rational[]> tripletOnsetPairs, List<List<Integer>> mismatchInds, 
		boolean grandStaff, boolean tabOnTop, int numVoices, String app/*, String path*/) {			
		System.out.println("\r\n>>> getTransBars() called");
		List<List<String>> transBars = new ArrayList<>();
//		for (List<Integer> l : mismatchInds) {
//			System.out.println(l);
//		}
		Encoding enc = tab.getEncoding();
		List<Event> events = enc.getEvents();
		
		System.out.println("incorrect:");
		System.out.println(mismatchInds.get(1));
		System.out.println("overlooked:");
		System.out.println(mismatchInds.get(2));
		System.out.println("superfluous:");
		System.out.println(mismatchInds.get(3));
		System.out.println("half:");
		System.out.println(mismatchInds.get(4));
		
		// Composition of dataStr (and dataInt):
		// dataStr.size() = number of bars in piece
		// dataStr.get(b).size() = voices in bar b (and in whole piece) 
		// dataStr.get(b).get(v).size() = notes in bar b in voice v 
		List<List<List<Integer[]>>> dataInt = (List<List<List<Integer[]>>>) data.get(0);
		List<List<List<String[]>>> dataStr = (List<List<List<String[]>>>) data.get(1);

		// TODO münchen: move glossary extraction to own method
		boolean extractOrnaments = app.equals("halcyon") ? false : true;
		if (extractOrnaments) {
			// Reorganise: list, per voice, the notes in that voice
			List<List<Integer[]>> dataIntPerVoice = new ArrayList<>();
			List<List<String[]>> dataStrPerVoice = new ArrayList<>();
			int numBars = dataInt.size();
			for (int voice = 0; voice < numVoices; voice++) {
				List<Integer[]> notesCurrVoiceInt = new ArrayList<>();
				List<String[]> notesCurrVoiceStr = new ArrayList<>();
				for (int bar = 0; bar < numBars; bar++) {
					notesCurrVoiceInt.addAll(dataInt.get(bar).get(voice));
					notesCurrVoiceStr.addAll(dataStr.get(bar).get(voice));
				}
				dataIntPerVoice.add(notesCurrVoiceInt);
				dataStrPerVoice.add(notesCurrVoiceStr);
			}
		
			Integer[][] btp = tab.getBasicTabSymbolProperties();
			List<Integer> ornInds = mismatchInds.get(Transcription.ORNAMENTATION_IND);
			List<String[]> ornaments = new ArrayList<>();
			ornaments.add(new String[]{"piece="+tab.getName()});
			for (int i = 0 ; i < numVoices; i++) {
				System.out.println("voice " + i);
				List<Integer[]> currVoiceDataInt = dataIntPerVoice.get(i);
				int numNotes = currVoiceDataInt.size();
				for (int j = 0; j < numNotes - 1; j++) {
					System.out.println("note " + j);
					Integer[] currNoteDataInt = currVoiceDataInt.get(j);
					Integer[] nextNoteDataInt = currVoiceDataInt.get(j+1);

					// If the next note is ornamental: build ornament. ornament consists of 
					// (1) the opening non-ornamental note (left border note)
					// (2) the ornamental note(s)
					// (3) the closing non-ornamental note (right border note)
					if (ornInds.contains(nextNoteDataInt[INTS.indexOf("indTab")])) {
						List<String> ornament = new ArrayList<>();
						List<String> loc = new ArrayList<>();
						// Add left border note
						int currIndTab = currNoteDataInt[INTS.indexOf("indTab")];
						int currPitch;
						int startPitch;
						String currRs;
						// If currIndTab is -1, currNoteDataInt represents a rest, and
						// the ornament starts with a rest (+ interval 0)
						if (currIndTab == -1) {
							currPitch = -1;
							startPitch = -1;
							currRs = "R";
						}
						else {
							currPitch = btp[currIndTab][Tablature.PITCH];
							startPitch = currPitch;
							int dur = btp[currIndTab][Tablature.MIN_DURATION];
							int dots = currNoteDataInt[INTS.indexOf("dots")];
							// getRhythmSymbol() needs the undotted dur
							dur = getUndottedNoteLength(dur, dots);
							
							String e = events.get(btp[currIndTab][Tablature.TAB_EVENT_SEQ_NUM]).getEncoding();
							RhythmSymbol rs = Symbol.getRhythmSymbol(
								e.substring(0, e.indexOf(Symbol.SYMBOL_SEPARATOR))
							);
							System.out.println(e);
							System.out.println(rs);
							
							currRs = Symbol.getRhythmSymbol(
								dur, 
								e.startsWith(Symbol.CORONA_INDICATOR), 
								rs.getBeam(), 
								rs.isTriplet()
							).getSymbol() + ".".repeat(dots);
						}
						// Add current RS
						ornament.add(currRs);
						// Add ornamental note(s) and right border note
						for (int k = j + 1; k < numNotes; k++) {
							nextNoteDataInt = currVoiceDataInt.get(k);
							int nextIndTab = nextNoteDataInt[INTS.indexOf("indTab")];
							if (nextIndTab == -1) {
								System.out.println(Arrays.asList(nextNoteDataInt));
								System.out.println("k " + k);
								System.out.println(ornament);
							}
							// If nextIndTab is -1, nextNoteDataInt represents a rest, and
							// the ornament ends with a rest (+ interval 0)
							int nextPitch = nextIndTab == -1 ? -1 : btp[nextIndTab][Tablature.PITCH];
							if (startPitch == -1) {
								startPitch = nextPitch;
							}
							String nextRs;
							if (nextIndTab != -1) {
								int nextDur = btp[nextIndTab][Tablature.MIN_DURATION];
								int nextDots = nextNoteDataInt[INTS.indexOf("dots")];
								// getRhythmSymbol() needs the undotted dur
								nextDur = getUndottedNoteLength(nextDur, nextDots);
								System.out.println(nextDur);
								if (j == 223 || j == 224 || j == 225 || j == 226 || j == 227) {
									System.out.println(Arrays.asList(nextNoteDataInt));
									System.out.println(Arrays.asList(dataStrPerVoice.get(i).get(k)));
									String s = enc.getListsOfSymbols().get(1).get(nextIndTab);
									int eInd = btp[nextIndTab][Tablature.TAB_EVENT_SEQ_NUM];
									System.out.println(enc.getListsOfSymbols().get(0));
									String e = events.get(eInd).getEncoding();
									System.out.println(s);
									System.out.println(e);
								}

								String e = events.get(btp[nextIndTab][Tablature.TAB_EVENT_SEQ_NUM]).getEncoding();
								RhythmSymbol rs = Symbol.getRhythmSymbol(
									e.substring(0, e.indexOf(Symbol.SYMBOL_SEPARATOR))
								);

//								if (j == 227) {
//									System.out.println(rs.getEncoding());
//									System.out.println("->"+tripletType+"<-");
//									System.out.println(rs.getBeam());
////									System.exit(0);
//								}
								nextRs = Symbol.getRhythmSymbol(
									nextDur, 
									e.startsWith(Symbol.CORONA_INDICATOR), 
									rs.getBeam(), 
									rs.isTriplet()
								).getSymbol() + ".".repeat(nextDots);
							}
							else {
								nextRs = "R";
							}
							// Add interval from curr to next (or 0 if left/right border is a rest)
							// and next RS
							ornament.add(
								(currIndTab == -1 || nextIndTab == -1) ? String.valueOf(0) :
								String.valueOf(nextPitch - currPitch));
							ornament.add(nextRs);
							
							currIndTab = nextIndTab;
							currPitch = nextPitch;
							// Break if last note added is right border note
							if (!ornInds.contains(nextIndTab)) {
								// Make sure that the next iteration of the j for-loop starts at the right 
								// border note, which could be a new left border note
								loc.add("voice=" + i);
								loc.add("bar=" + currNoteDataInt[INTS.indexOf("bar")]);
								Rational mp = new Rational(
									currNoteDataInt[INTS.indexOf("metPosNum")], 
									currNoteDataInt[INTS.indexOf("metPosDen")]
								);
								mp.reduce();
								String mpStr = 
									mp.equals(Rational.ZERO) ? "0" :
									(mp.equals(Rational.ONE) ? "1" : mp.toString());									
								loc.add("metPos=" + mpStr);
								loc.add("startPitch=" + startPitch);
								j = k-1; 
								break;
							}
						}
						ornaments.add(new String[]{
							ornament.toString(), loc.toString()	
//							"[" + String.join(", ", ornament) + "]", 
//							"[" +  String.join(", ", loc) + "]"  
						});
					}
				}
			
			
//			for (int j = 0; j < numNotes; j++) {
//				Integer[] currNoteDataInt = currVoiceDataInt.get(j);
//				// If there is a next note
//				if (j != numNotes - 1) {
//					// If the next note is ornamental: build ornament. ornament consists of 
//					// (1) the opening non-ornamental note (left border note)
//					// (2) the ornamental note(s)
//					// (3) the closing non-ornamental note (right border note)
//					if (ornInds.contains(currVoiceDataInt.get(j + 1)[INTS.indexOf("indTab")])) {
//						List<String> ornament = new ArrayList<>();
//						List<String> ornamentAbs = new ArrayList<>();
//						// Add left border note
//						int currIndTab = currNoteDataInt[INTS.indexOf("indTab")];
////						String o = "";
//						String rs = "R";
//						int pitch = -1;
//						// If currIndTab is -1, currNoteDataInt represents a rest
//						if (currIndTab != -1) {
//							pitch = btp[currIndTab][Tablature.PITCH];
//							int dur = btp[currIndTab][Tablature.MIN_DURATION];
//							int dots = currNoteDataInt[INTS.indexOf("dots")];
//							// getRhythmSymbol() needs the undotted dur
//							dur = getUndottedNoteLength(dur, dots);
//							rs = Symbol.getRhythmSymbol(dur, false, false, null).getSymbol() + 
//								".".repeat(dots);
////							o = rs + pitch;
//						}
//						ornament.add(rs + pitch);
//						ornamentAbs.add(rs);
//						// Add ornamental note(s) and right border note
//						for (int k = j + 1; k < numNotes; k++) {
//							Integer[] nextNoteDataInt = currVoiceDataInt.get(k);
//							int nextIndTab = nextNoteDataInt[INTS.indexOf("indTab")];
////							String nextO = "";
//							String nextRs = "";
//							int nextPitch = -1;
//							// If nextIndTab is -1, nextNoteDataInt represents a rest
//							if (nextIndTab != -1) {
//								nextPitch = btp[nextIndTab][Tablature.PITCH];
//								int nextDur = btp[nextIndTab][Tablature.MIN_DURATION];
//								int nextDots = nextNoteDataInt[INTS.indexOf("dots")];
//								// getRhythmSymbol() needs the undotted dur
//								nextDur = getUndottedNoteLength(nextDur, nextDots);
//								nextRs = Symbol.getRhythmSymbol(nextDur, false, false, null).getSymbol() + 
//									".".repeat(nextDots);
////								nextO = nextRs + nextPitch;
//							}
//							ornament.add(nextRs + nextPitch);
//							ornamentAbs.add(currIndTab != -1 ? String.valueOf(0) : 
//								String.valueOf(nextPitch - pitch));
//							ornamentAbs.add(nextRs);
//	
//							// Break after right border note
//							if (!ornInds.contains(nextIndTab)) {
//								// Make sure that the next iteration of the j for-loop starts at the right 
//								// border note, which could a new left border note
//								ornament.add("voice=" + i);
//								ornament.add("bar=" + currNoteDataInt[INTS.indexOf("bar")]);
//								Rational mp = new Rational(
//									currNoteDataInt[INTS.indexOf("metPosNum")], 
//									currNoteDataInt[INTS.indexOf("metPosDen")]
//								);
//								mp.reduce();
//								String mpStr = 
//									mp.equals(Rational.ZERO) ? "0" :
//									(mp.equals(Rational.ONE) ? "1" : mp.toString());									
//								ornament.add("metPos=" + mpStr);
//								j = k-1; 
//								break;
//							}
//						}
//						ornaments.add(ornament);
//					}
//				}
//			}
		}
		ornFull.addAll(ornaments);
		System.out.println("xxxxxxxxxxxxxxxxxxxx");
		}

//		System.exit(0);
		
//		// dataStr for voice 0
//		List<List<String>> ornaments = new ArrayList<>();
////		int numBars = dataStr.size();
//		for (int voice = 0; voice < numVoices; voice++) {
//			
//			List<String> ornament = new ArrayList<>();
//			List<String> orn = new ArrayList<>();
//			List<Integer> ornInds = mismatchInds.get(Transcription.ORNAMENTATION_IND);
//			for (int bar = 0; bar < numBars; bar++) {
//				List<String[]> currBarStr = dataStr.get(bar).get(voice);
//				List<Integer[]> currBarInt = dataInt.get(bar).get(voice);
//				int numNotes = currBarStr.size();
//				for (int note = 0; note < numNotes; note++) {
//					String[] currNoteStr = currBarStr.get(note);
//					Integer[] currNoteInt = currBarInt.get(note);
//					int currIndTab = currNoteInt[INTS.indexOf("indTab")];
//					if (note < numNotes - 1) {
//
//						Integer[] nextNoteInt = currBarInt.get(note + 1);
//						int nextIndTab = currNoteInt[INTS.indexOf("indTab")];
//						if (ornInds.contains(nextNoteInt[INTS.indexOf("indTab")])) {
////							ornament.add(e);
//						}
//					}
//
//				}
//				System.out.println("|");		
//			}
//		}
//		System.exit(0);
		
//		for (List<String[]> voice : dataStr.get(29)) {
//			for (String[] note : voice) {
//				System.out.println(Arrays.asList(note));
//			}
//			System.out.println("- - - - - - ");
//		}
//		System.exit(0);
		
//		System.out.println("------------");
//		for (List<Integer[]> l : dataInt.get(2)) {
//			for (Integer[] in : l) {
//				System.out.println(Arrays.toString(in));
//			}
//		}
//		System.out.println("------------");
//		for (List<String[]> l : dataStr.get(2)) {
//			for (String[] in : l) {
//				System.out.println(Arrays.toString(in));
//			}
//		}
		
//		List<String[]> b61v2 = dataStr.get(61).get(2); 
//		System.out.println("XXXXXX");
//		for (String[] s : b61v2) {
//			System.out.println(Arrays.toString(s));
//		}
//		System.exit(0);
		
		// Apply beaming: set beamOpen and beamClose in dataInt
		System.out.println(Arrays.asList(dataInt));
		System.out.println(Arrays.asList(dataStr));
		dataInt = beam(dataInt, dataStr, tab, mi, tripletOnsetPairs, mismatchInds, numVoices/*, path*/);
		
//		for (List<Integer[]> l : dataInt.get(2)) {
//			for (Integer[] in : l) {
//				System.out.println(Arrays.toString(in));
//			}
//		}
//		System.out.println("------------");
//		for (List<String[]> l : dataStr.get(2)) {
//			for (String[] in : l) {
//				System.out.println(Arrays.toString(in));
//			}
//		}

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
					diminution = tab.getEncoding().getTimeline().getDiminution(bar);
//					diminution = Tablature.getDiminution(bar, mi);
				}
//				for (int z = 0; z < currBarCurrVoiceInt.size(); z++) {
//					System.out.println(Arrays.asList(currBarCurrVoiceStr.get(z)));
//					System.out.println(Arrays.asList(currBarCurrVoiceInt.get(z)));
//				}
//				System.exit(0);
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
		List<Rational[]> tripletOnsetPairs, List<List<Integer>> mismatchInds, int numVoices/*, String path*/) {
		System.out.println(">>> beam() called");
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
					diminution = tab.getEncoding().getTimeline().getDiminution(bar);
//					diminution = Tablature.getDiminution(bar, mi);
				}
//				System.out.println("-->");
//				System.out.println("bar i = " + i);
//				System.out.println("voice j = " + j);
//				for (Integer[] in : currBarCurrVoiceInt) {
//					System.out.println(Arrays.asList(in));
//				}
//				for (String[] in : currBarCurrVoiceStr) {
//					System.out.println(Arrays.asList(in));
//				}
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
		String filePath = MEITemplatePath; //path; // scriptPathPythonMEI;
		File notesFile = new File(filePath + "-notes.txt");
		ToolBox.storeTextFile(strb.toString(), notesFile);
//		System.out.println(strb.toString());

		// Call the beaming script and get output; delete
		// NB: the output of the beaming script does not end with a line break, but 
		// PythonInterface.getScriptOutput() adds one to the end of it
		String beamed = "";
//		try {
			beamed = 
				PythonInterface.runPythonFileAsScript(new String[]{
				"python", scriptPathPythonMEI + "beam.py", filePath + "-notes.txt"});
//			beamed = PythonInterface.getScriptOutput(new String[]{
//					"python", scriptPathPythonMEI + "beam.py", filePath + "-notes.txt"});
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("* * * * * *");
//		System.out.println(beamed);
//		System.out.println("* * * * * *");
//		System.exit(0);
			
//		System.out.println(notesFile);
		notesFile.delete(); // TODO find different solution?

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
//		for (List<String> l : barsPerVoice) {
//			System.out.println(l);
//		}
//		System.exit(0);
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
					if (noteCurrBarCurrVoice.endsWith("</beam>")) {
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
//		System.out.println(Arrays.toString(currBarCurrVoiceInt.get(0)));
//		System.out.println(INTS.indexOf("bar"));
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
				int indentsAdded = 0;
				for (int i = 0; i < currBarCurrVoiceStr.size(); i++) {
					String[] note = currBarCurrVoiceStr.get(i);
					Integer[] noteInt = currBarCurrVoiceInt.get(i);
					
					System.out.println(Arrays.asList(noteInt));

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

					// WAS: check in sequence triplet - chord - beam
					// Check for any beam to be added before noteStr
					if (noteInt[INTS.indexOf("beamOpen")] == 1) {
						barList.add(TAB.repeat(2) + "<beam>");
						beamActive = true;
						indentsAdded++;
					}

					// Check for any chord to be added before noteStr
					if (i < currBarCurrVoiceStr.size()-1) {
						Integer[] nextNoteInt = currBarCurrVoiceInt.get(i+1);
						if (new Rational(currOnsNum, currOnsDen).equals(new Rational(
							nextNoteInt[INTS.indexOf("onsetNum")], nextNoteInt[INTS.indexOf("onsetDen")]))
							&& !chordActive) {
							barList.add(
								TAB.repeat(2 + indentsAdded) + "<chord dur='" + noteInt[INTS.indexOf("dur")] + "'>"
							);
							chordActive = true;
							indentsAdded++;
						}
					}
					
					// Check for any tripletOpen to be added before noteStr
					if (noteInt[INTS.indexOf("tripletOpen")] == 1) {
						int tupletDur = -1;
//						Rational tupletDur = null;
						Rational onset = new Rational(currOnsNum, currOnsDen);
//							currBarCurrVoiceInt.get(i)[INTS.indexOf("onsetDen")]);
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
						barList.add(
							TAB.repeat(2 + indentsAdded) + "<tuplet dur='" + tupletDur + "' num='3' numbase='2'>"
						);
						tupletActive = true;
						indentsAdded++;
					}

					String noteStr = TAB.repeat(2 + indentsAdded);
//					// Add indent for any tuplet and/or chord
//					if (tupletActive) {
//						noteStr += TAB;
//					}
//					if (chordActive) {
//						noteStr += TAB;
//					}
//					if (beamActive) {
//						noteStr += TAB;
//					}
					noteStr = 
						(note[STRINGS.indexOf("pname")] == null) ? noteStr + "<rest " : 
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
						if (highlightNotes) {
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
					
					// Close any active triplet, chord, beam
					// Triplet
					if (noteInt[INTS.indexOf("tripletClose")] == 1) {
						indentsAdded--;
						barList.add(TAB.repeat(2 + indentsAdded) + "</tuplet>");
						tupletActive = false;
					}
					// Chord
					if (chordActive) {
						Rational currOnset = new Rational(currOnsNum, currOnsDen);
						Rational nextOnset = 
							(i == currBarCurrVoiceStr.size()-1) ? null :
							new Rational(currBarCurrVoiceInt.get(i+1)[INTS.indexOf("onsetNum")],
							currBarCurrVoiceInt.get(i+1)[INTS.indexOf("onsetDen")]);
						if ((i < currBarCurrVoiceStr.size()-1 && nextOnset.isGreater(currOnset)) ||
							i == currBarCurrVoiceStr.size()-1) {
							indentsAdded--;
							barList.add(TAB.repeat(2 + indentsAdded) + "</chord>");
							chordActive = false;
						}
					}
					// Beam
					if (noteInt[INTS.indexOf("beamClose")] == 1) {
						indentsAdded--;
						barList.add(TAB.repeat(2 + indentsAdded) + "</beam>");
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
	 * @param tab
	 * @param trans
	 * @param mi
	 * @param ki
	 * @param tripletOnsetPairs
	 * @returns A 
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> getData(Tablature tab, Transcription trans,
		List<Integer[]> mi, List<Integer[]> ki, List<Rational[]> tripletOnsetPairs) {
		System.out.println("\r\n>>> getData() called");

		ScorePiece p = trans.getScorePiece();
//		Piece p = trans.getScorePiece();
//		MetricalTimeLine mtl = trans.getScorePiece().getMetricalTimeLine();
		ScoreMetricalTimeLine smtl = trans.getScorePiece().getScoreMetricalTimeLine();
		int numVoices = p.getScore().size();
		Integer[][] btp = tab == null ? null : tab.getBasicTabSymbolProperties();
		Rational gridVal = 
//			(btp != null) ? Tablature.SMALLEST_RHYTHMIC_VALUE : new Rational(1, 128); // 14.03.2020 was 1/64
			ONLY_TAB || TAB_AND_TRANS ? Tablature.SMALLEST_RHYTHMIC_VALUE : new Rational(1, 128);
		List<Integer> gridNums = IntStream.rangeClosed(0, gridVal.getDenom()).boxed().collect(Collectors.toList());

		Integer[][] bnp = trans.getBasicNoteProperties();

//		// Undiminute bnp and Piece
//		if (TAB_AND_TRANS && adaptTransDur) {
//			System.out.println("mi:");
//			for (Integer [] in : mi) {
//				System.out.println(Arrays.toString(in));
//			}
//			System.exit(0);
//
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
//			
//			// Adapt complete bnp if any of the meters has a diminution other than 1
//			List<Integer> diminutions = ToolBox.getItemsAtIndex(mi, Timeline.MI_DIM);
//			if (Collections.frequency(diminutions, 1) != diminutions.size()) {
//				// Undiminute bnp using diminuted mi
//
//				bnp = Transcription.undiminuteBasicNotePropertiesOBS(bnp, mi);
//				p = Transcription.createPiece(btp, bnp mi);
//
//				// Reset mi to undiminuted 
//				mi = tab.getTimeline().getUndiminutedMeterInfoOBS();
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
//			}
//		}
//		System.out.println(bnp.length);
//		System.out.println(Arrays.toString(bnp[0]));
//		System.out.println(Arrays.toString(bnp[25]));
//		System.out.println(Arrays.toString(bnp[50]));
//		System.out.println(Arrays.toString(bnp[bnp.length-1]));
//		System.exit(0);
		
		// Get meter and key TODO assumed is a single key
		int numBars = mi.get(mi.size()-1)[Transcription.MI_LAST_BAR];
		Rational endOffset = Rational.ZERO;
		for (Integer[] m : mi) {
			Rational currMeter = new Rational(m[Transcription.MI_NUM], m[Transcription.MI_DEN]);
			int barsInCurrMeter = (m[Transcription.MI_LAST_BAR] - m[Transcription.MI_FIRST_BAR]) + 1;
			endOffset = endOffset.add(currMeter.mul(barsInCurrMeter));
		}
		Integer[] key = ki.get(0);
		int numAlt = key[Transcription.KI_KEY];
		int mode = key[Transcription.KI_MODE];

		// Set initial bar and meter
		Integer[] initMi = mi.get(0);
		Rational meter = new Rational(initMi[Transcription.MI_NUM], initMi[Transcription.MI_DEN]);
		Rational barEnd = meter;
		
		Timeline tl = tab != null ? tab.getEncoding().getTimeline() : null;

		// Get indices mapping
		List<List<Integer>> transToTabInd = null, tabToTransInd = null;
		if (btp != null) {
			tabToTransInd = Transcription.alignTabAndTransIndices(btp, bnp).get(0);
			transToTabInd = Transcription.alignTabAndTransIndices(btp, bnp).get(1);
		}

		// Set grids 
		List<Object> grids = createGrids(numAlt, mode);
		Integer[] mpcGrid = (Integer[]) grids.get(0);
		String[] altGrid = (String[]) grids.get(1);
		String[] pcGrid = (String[]) grids.get(2);
//		Integer[][] mpcg = makeMIDIPitchClassGrid(mode);
//		String[][] ag = makeAlterationGrid(mpcg);
//		String[][] pcg = makePitchClassGrid(mode);
//		int keyInd = numAlt + 7;
//		Integer[] mpcGrid = mpcg[keyInd];
//		String[] altGrid = ag[keyInd];
//		String[] pcGrid = pcg[keyInd];
		System.out.println("mpcGrid: " + Arrays.asList(mpcGrid));
		System.out.println("altGrid: " + Arrays.asList(altGrid));
		System.out.println("pcGrid:  " + Arrays.asList(pcGrid));
		
		List<List<String[]>> noteAttribPerVoiceStrings = new ArrayList<List<String[]>>();
		List<List<Integer[]>> noteAttribPerVoiceInts = new ArrayList<List<Integer[]>>();
		for (int i = 0; i < numVoices; i++) {
			noteAttribPerVoiceStrings.add(new ArrayList<String[]>());
			noteAttribPerVoiceInts.add(new ArrayList<Integer[]>());
		}
		List<Integer> naturalsAlreadyAdded = new ArrayList<Integer>();
		List<Integer> accidentalsAlreadyAdded = new ArrayList<Integer>();
//		List<List<Integer>> naturalsPcInEffect = new ArrayList<List<Integer>>();
//		List<List<Integer>> sharpsPcInEffect = new ArrayList<List<Integer>>();
//		List<List<Integer>> flatsPcInEffect = new ArrayList<List<Integer>>();
//		for (int i = 0; i < numVoices; i++) {
//			naturalsPcInEffect.add(new ArrayList<Integer>());
//			sharpsPcInEffect.add(new ArrayList<Integer>());
//			flatsPcInEffect.add(new ArrayList<Integer>());
//		}
		List<Integer> doubleFlatsInEffect = new ArrayList<>();
		List<Integer> flatsInEffect = new ArrayList<>();
		List<Integer> naturalsInEffect = new ArrayList<>();
		List<Integer> sharpsInEffect = new ArrayList<>();
		List<Integer> doubleSharpsInEffect = new ArrayList<>();

		String summ = "";
		List<String> summL = new ArrayList<>();
		List<String> pp = Arrays.asList(new String[]{"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "Bb", "B"});
		for (int i = 0; i < bnp.length; i++) {
			int iTab = -1;
			if (btp != null) {
				iTab = transToTabInd.get(i).get(0); // each element (list) contains only one element (int)
			}
			String[] curr = new String[STRINGS.size()];
			int voice = DataConverter.convertIntoListOfVoices(trans.getVoiceLabels().get(i)).get(0);
			Rational onset = 
				new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], bnp[i][Transcription.ONSET_TIME_DENOM]);
			System.out.println("iTab = " + iTab);
			System.out.println("voice = " + voice);

			if (ONLY_TAB) {
//			if (btp != null) {
				onset = new Rational(btp[iTab][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			}
			onset = round(onset, gridNums);
//\\			System.out.println("i = " + i + " (indTab = " + iTab + "); bar = " + 
//\\				Tablature.getMetricPosition(onset, mi)[0].getNumer() + "; pitch = " + bnp[i][0]);

			System.out.println("onset = " + new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], bnp[i][Transcription.ONSET_TIME_DENOM]));
			System.out.println("onsetRounded = " + round(new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], bnp[i][Transcription.ONSET_TIME_DENOM]), gridVal));
			System.out.println("onsetRoundedAlt = " + round(new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], bnp[i][Transcription.ONSET_TIME_DENOM]), gridNums));
			
//			Rational[] barMetPos = 
//				!adaptTransDur ? Tablature.getMetricPosition(onset, mi) :
//				Tablature.getMetricPosition(onset, tab.getUndiminutedMeterInfo());		
			Rational[] barMetPos = 
				tab != null ? 
				tl.getMetricPosition((int) onset.mul(Tablature.SRV_DEN).toDouble()) // multiplication necessary because of division when making onset above
				:
				smtl.getMetricPosition(onset);	
//				ScoreMetricalTimeLine.getMetricPosition(mtl, onset);	
//				Utils.getMetricPosition(onset, mi);	
				
//			System.out.println(Arrays.toString(tab.getUndiminutedMeterInfo().get(0)));
			int bar = barMetPos[0].getNumer();
//			System.out.println("i = " + i + "; bar = " + bar);
			Rational metPos = barMetPos[1];

			// If adaptTransDur, diminution has been made undone in bnp
			int diminution = 1; // TODO can be removed
//			int diminution = adaptTransDur ? 1 : Timeline.getDiminution(onset, mi); 
			
			// Increment barEnd and clear lists when new bar is reached
			if (onset.isGreaterOrEqual(barEnd)) {
				barEnd = (onset.sub(metPos)).add(Transcription.getMeter(bar, mi));
				naturalsAlreadyAdded.clear();
				accidentalsAlreadyAdded.clear();
//				for (int j = 0; j < numVoices; j++) {
//					naturalsPcInEffect.get(j).clear();
//					sharpsPcInEffect.get(j).clear();
//					flatsPcInEffect.get(j).clear();
//				}
				doubleFlatsInEffect.clear();
				flatsInEffect.clear();
				naturalsInEffect.clear();
				sharpsInEffect.clear();
				doubleSharpsInEffect.clear();
			}

			int pitch = bnp[i][Transcription.PITCH];
			
			Rational dur = 
				new Rational(bnp[i][Transcription.DUR_NUMER], bnp[i][Transcription.DUR_DENOM]);
//			Rational durRounded = round(new Rational(bnp[i][Transcription.DUR_NUMER], 
//				bnp[i][Transcription.DUR_DENOM]), gridVal);
			dur = round(dur, gridNums);
			System.out.println("dur = " + new Rational(bnp[i][Transcription.DUR_NUMER], bnp[i][Transcription.DUR_DENOM]));
			System.out.println("durRounded = " + round(new Rational(bnp[i][Transcription.DUR_NUMER], bnp[i][Transcription.DUR_DENOM]), gridVal));
			System.out.println("durRoundedAlt = " + round(new Rational(bnp[i][Transcription.DUR_NUMER], bnp[i][Transcription.DUR_DENOM]), gridNums));
//			if (iTab == 315) {
//				System.exit(0);
//			}
			if (ONLY_TAB) {
//			if (btp != null) {
				dur = new Rational(btp[iTab][Tablature.MIN_DURATION], Tablature.SRV_DEN);
			}

			Rational offset = onset.add(dur);
//			int keyInd = numAlt + 7;
//			Integer[] currMpcg = mpcg[keyInd];
//			String[] currAg = ag[keyInd];
//			String[] currPcg = pcg[keyInd];
//			System.out.println("currMpcg: " + Arrays.asList(currMpcg));
//			System.out.println("currAg:   " + Arrays.asList(currAg));
//			System.out.println("currPcg:  " + Arrays.asList(currPcg));
			
			if (verbose) {
				System.out.println("voice                    " + voice);
				System.out.println("bar                      " + bar);
				System.out.println("pitch                    " + pitch);
				System.out.println("midiPitchClass           " + (pitch % 12));
				System.out.println("onset                    " + onset);
				System.out.println("offset                   " + offset);
				System.out.println("metPos                   " + metPos);
				System.out.println("durRounded               " + dur);
				System.out.println("barEnd                   " + barEnd);
				System.out.println("currMpcg                 " + Arrays.asList(mpcGrid));
				System.out.println("currAg                   " + Arrays.asList(altGrid));
				System.out.println("currPcg                  " + Arrays.asList(pcGrid));
				System.out.println("------------------");	
			}
			if (!Arrays.asList(mpcGrid).contains(pitch % 12)) {
				Rational mp = metPos;
				mp.reduce();
				System.out.println(pitch);
				summ += pp.get(pitch % 12) + "(MIDI " + pitch + "), bar " + bar + ", voice " + voice + ", onset " + metPos + " not in key\r\n"; 
				if (!summL.contains(pp.get(pitch % 12))) {
					summL.add(pp.get(pitch % 12));
				}
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
					onsetPrev = 
						new Rational(prevNote[INTS.indexOf("onsetNum")], 
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
				metPosPrev = 
					tab != null ?
					tl.getMetricPosition((int) onsetPrev.mul(Tablature.SRV_DEN).toDouble())[1]
					:
					smtl.getMetricPosition(onsetPrev)[1];
//					ScoreMetricalTimeLine.getMetricPosition(mtl, onsetPrev)[1];	
//					Utils.getMetricPosition(onsetPrev, mi)[1]; 
				offsetPrev = onsetPrev.add(durPrev);

				// To tripletise is to give a note its nominal (shown) value instead of its 
				// actual value by multiplying it with TRIPLETISER (3/2). 
				// Example for a half note:
				// tripletised = 1/2, 1/2, 1/2
				// tripletised = 1/3, 1/3, 1/3 TODO should this be 'untripletised'?
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
						(tab != null ?
						tl.getMetricPosition((int) onsetRest.mul(Tablature.SRV_DEN).toDouble())[1]		
						:
						smtl.getMetricPosition(onsetRest)[1]);
//						ScoreMetricalTimeLine.getMetricPosition(mtl, onsetRest)[1]);	
//						Utils.getMetricPosition(onsetRest, mi)[1]);
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
					int beginBar = 
						tab != null ?
						tl.getMetricPosition((int) offsetPrev.mul(Tablature.SRV_DEN).toDouble())[0].getNumer()		
						: 
						smtl.getMetricPosition(offsetPrev)[0].getNumer();	
//						ScoreMetricalTimeLine.getMetricPosition(mtl, offsetPrev)[0].getNumer();	
//						Utils.getMetricPosition(offsetPrev, mi)[0].getNumer();
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
			List<List<Integer>> accidsInEffect = new ArrayList<>();
			accidsInEffect.add(doubleFlatsInEffect);
			accidsInEffect.add(flatsInEffect);
			accidsInEffect.add(naturalsInEffect);
			accidsInEffect.add(sharpsInEffect);
			accidsInEffect.add(doubleSharpsInEffect);
			List<Object> pitchSpell = spellPitch(
				pitch, bar, onset, numAlt, mpcGrid, altGrid, pcGrid, accidsInEffect
			);
			String[] pa = (String[]) pitchSpell.get(0);
			String pname = pa[0];
			String accid = pa[1];
//			String pname = "";
//			String accid = "";
			String oct = String.valueOf(getOctave(pitch));
			// Update accidLists
			List<List<Integer>> aie = (List<List<Integer>>) pitchSpell.get(1);
			doubleFlatsInEffect = aie.get(0);
			flatsInEffect = aie.get(1);
			naturalsInEffect = aie.get(2);
			sharpsInEffect = aie.get(3);
			doubleSharpsInEffect = aie.get(4);

//			int midiPitchClass = pitch % 12;
//			// No flat, sharp, or natural
//			if (Arrays.asList(mpcGrid).contains(midiPitchClass)) {
//				int pcInd = Arrays.asList(mpcGrid).indexOf(midiPitchClass);
//				pname = String.valueOf(pcGrid[pcInd]);
//				// If the note resets a previously altered (flat/sharp/natural) note: remove accidental
//				// Previously flat or sharp: add natural
//				if (flatsInEffect.contains(pitch-1)) {
//					accid="n";
//					flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
//				}
//				if (sharpsInEffect.contains(pitch+1)) {
//					accid="n";
//					sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
//				}
//				// Previously natural: add sharp or flat
//				boolean isFlatInKey = altGrid[pcInd] == "f";
//				boolean isSharpInKey = altGrid[pcInd] == "s";
//				if (isFlatInKey && naturalsInEffect.contains(pitch+1)) {
//					accid = "f";
//					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
//				}
//				if (isSharpInKey && naturalsInEffect.contains(pitch-1)) {
//					accid = "s";
//					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
//				}
//			}
//			// TODO münchen: move pitch spelling to own method
//			// Flat, sharp, or natural
//			else {
////				if (bar == 29 && voice == 1 && metPos.equals(new Rational(7, 4))) {
////					System.out.println(pitch);
////					System.out.println(flatsInEffect);
////					System.out.println(sharpsInEffect);
////					System.out.println(naturalsInEffect);
////					System.exit(0);
////				}
//				List<Integer> mpcKeySigs = getMIDIPitchClassKeySigs(numAlt/*key*/);
//				// If natural (flats/sharps)
//				if (numAlt < 0 && mpcKeySigs.contains((pitch-1) % 12) ||
//					numAlt > 0 && mpcKeySigs.contains((pitch+1) % 12)) {
//					if (verbose) System.out.println("is natural");
//					// Only if the natural has not already been indicated for this specific pitch in the bar
//					if (!naturalsInEffect.contains(pitch)) {
//						accid = "n";
//						naturalsInEffect.add(pitch);
//					}
//					// Natural for flat
//					int pcInd = -1;
//					if (mpcKeySigs.contains((pitch-1) % 12)) {
//						pcInd = Arrays.asList(mpcGrid).indexOf((pitch-1) % 12);
//					}
//					// Natural for sharp
//					else if (mpcKeySigs.contains((pitch+1) % 12)) {
//						pcInd = Arrays.asList(mpcGrid).indexOf((pitch+1) % 12);
//					}
//					pname = String.valueOf(pcGrid[pcInd]);
//				}
//				// If accidental
//				else {
//					if (verbose) System.out.println("is accidental");
//					// Find pitch of next note of different pitch. If there is none, the voice
//					// ends with a sequence of the same pitches (i.e., with a repetition of 
//					// the last pitch), and nextPitch remains -1
//					NotationVoice nv = p.getScore().get(voice).get(0);
//					int nextPitch = -1;	
//					// If not last note
//					if (!(nv.get(nv.size()-1).getMetricTime().equals(onset))) {
//						for (int j = 0; j < nv.size() && nextPitch == -1; j++) {
//							if (nv.get(j).getMetricTime().equals(onset)) {
//								for (int k = j+1; k < nv.size(); k++) {
//									int currNextPitch = nv.get(k).get(0).getMidiPitch();
//									if (currNextPitch != pitch) {
//										nextPitch = currNextPitch;
//										break;
//									}
//								}
//							}
//						}
//					}
//					System.out.println("pitch: " + pitch);
//					System.out.println("midiPitchClass: " + midiPitchClass);
//					System.out.println("nextPitch: " + nextPitch);
//					System.out.println("mpcGrid " + Arrays.asList(mpcGrid));
//					System.out.println("altGrid " + Arrays.asList(altGrid));
//					System.out.println("pcGrid  " + Arrays.asList(pcGrid));
//					boolean nextIsInKey = Arrays.asList(mpcGrid).contains(nextPitch % 12);
//					System.out.println("nextIsInKey " + nextIsInKey);
//					// a. Direct leading tone (next different pitch is in-key and a semitone lower/higher)
//					//    or last note in voice     
//					if (nextIsInKey && (nextPitch == (pitch + 1) || nextPitch == (pitch - 1) || nextPitch == -1)) {
//						// Sharps (if last note, assume sharp )
//						if ((nextPitch != -1 && nextPitch == (pitch + 1)) || nextPitch == -1) {
//							if (verbose) System.out.println("is sharp");
//							// pname is pc of un-sharpened pitch
//							int pcInd = Arrays.asList(mpcGrid).indexOf((pitch-1) % 12);
//							pname = String.valueOf(pcGrid[pcInd]);
//							// Only if the accidental has not already been indicated for this specific pitch in the bar
//							if (!sharpsInEffect.contains(pitch)) {
//								accid = "s";
//								// If pitch is already a sharp: double sharp
//								if (Arrays.asList(mpcGrid).contains(midiPitchClass) && 
//									KEY_ACCID_MPC_SHARP.contains(midiPitchClass)) {
//									accid = "x";
//								}
//								sharpsInEffect.add(pitch);
//							}
//						}
//						// Flats
//						else if (nextPitch != -1 && nextPitch == (pitch - 1)) {
//							if (verbose) System.out.println("is flat");
//							// pname is pc of un-flattened pitch
//							int pcInd = Arrays.asList(mpcGrid).indexOf((pitch+1) % 12);
//							pname = String.valueOf(pcGrid[pcInd]);
//							// Only if the accidental has not already been indicated for this specific pitch in the bar
//							if (!flatsInEffect.contains(pitch)) {
//								accid = "f";
//								// If pitch is already a flat: double flat
//								if (Arrays.asList(mpcGrid).contains(midiPitchClass) &&
//									KEY_ACCID_MPC_FLAT.contains(midiPitchClass)) {
//									accid = "ff";
//								}
//								flatsInEffect.add(pitch);
//							}
//						}
//					}
//					// b. Not a direct leading tone
//					else {
//						boolean isNextOrSecondNextKeyAccid = false;
//						boolean isLeadingToneForMinor = false;		
//						System.out.println("bar   " + bar);
//						System.out.println("voice " + voice);
//						System.out.println(nextPitch);
//						System.out.println(midiPitchClass);
//						System.out.println(sharpsInEffect);
//						// 1. If pitch is the next or second-next key accidental: spell as key accidental
//						// Sharps
//						if (numAlt > 0) {
//							int indLastKeyAccid = KEY_ACCID_MPC_SHARP.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
//							for (int incr : new Integer[]{1, 2}) {
//								if (KEY_ACCID_MPC_SHARP.get(indLastKeyAccid + incr) == midiPitchClass) {
//									pname = KEY_ACCID_PC_SHARP.get(indLastKeyAccid + incr);
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!sharpsInEffect.contains(pitch)) {
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) && 
//											KEY_ACCID_MPC_SHARP.contains(midiPitchClass)) {
//											accid = "x";
//										}
//										sharpsInEffect.add(pitch);
//									}
//									isNextOrSecondNextKeyAccid = true;
//									break;
//								}
//							}
//							
//						}
//						// Flats
//						else if (numAlt < 0) {
//							int indLastKeyAccid = KEY_ACCID_MPC_FLAT.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
//							for (int incr : new Integer[]{1, 2}) {
//								if (KEY_ACCID_MPC_FLAT.get(indLastKeyAccid + incr) == midiPitchClass) {
//									pname = KEY_ACCID_PC_FLAT.get(indLastKeyAccid + incr);
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!flatsInEffect.contains(pitch)) {
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) &&
//											KEY_ACCID_MPC_FLAT.contains(midiPitchClass)) {
//											accid = "ff";
//										}
//										flatsInEffect.add(pitch);
//									}
//									isNextOrSecondNextKeyAccid = true;
//									break;
//								}
//							}
//						}
//						// 2. If pitch is the leading tone for the minor parallel: spell as leading tone
//						if (!isNextOrSecondNextKeyAccid) {
//							int mpcMinor = KEY_SIG_MPCS.get(numAlt)[1];
//							// Upper leading tone
//							if (midiPitchClass == mpcMinor + 1) {
//								pname = pcGrid[Arrays.asList(mpcGrid).indexOf(mpcMinor) + 1];
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!flatsInEffect.contains(pitch)) {
//									accid = "f";
//									// If pitch is already a flat: double flat
//									if (Arrays.asList(mpcGrid).contains(midiPitchClass) &&
//										KEY_ACCID_MPC_FLAT.contains(midiPitchClass)) {
//										accid = "ff";
//									}
//									flatsInEffect.add(pitch);
//								}
//								isLeadingToneForMinor = true;
//							}
//							// Lower leading tone
//							if (midiPitchClass == mpcMinor - 1) {
//								pname = pcGrid[Arrays.asList(mpcGrid).indexOf(mpcMinor) - 1];
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!sharpsInEffect.contains(pitch)) {
//									accid = "s";
//									// If pitch is already a sharp: double sharp
//									if (Arrays.asList(mpcGrid).contains(midiPitchClass) && 
//										KEY_ACCID_MPC_SHARP.contains(midiPitchClass)) {
//										accid = "x";
//									}
//									sharpsInEffect.add(pitch);
//								}
//								isLeadingToneForMinor = true;
//							}
//						}
//						// 3. Else: spell as whichever flat of sharp has the earliest key accidental index, e.g.,
//						//    - C# (2nd sharp) preferred over Db (4th flat)
//						//    - Bb (1st flat) preferred over A# (5th sharp)) 
//						if (!isNextOrSecondNextKeyAccid && !isLeadingToneForMinor) {
//							int indInSharps = KEY_ACCID_MPC_SHARP.indexOf(midiPitchClass);
//							int indInFlats = KEY_ACCID_MPC_FLAT.indexOf(midiPitchClass);
//							System.out.println("la la la");
//							System.out.println(bar);
//							System.out.println(voice);
//							System.out.println(midiPitchClass);
//							System.out.println(indInFlats);
//							System.out.println(indInSharps);
//							if (indInSharps != indInFlats) {
////								pname = pcGrid[Arrays.asList(mpcGrid).indexOf(
////									(indInSharps < indInFlats ? midiPitchClass - 1 : midiPitchClass + 1))];
////								accid = indInSharps < indInFlats ? "s" : "f";
//								if (indInSharps < indInFlats) {
//									pname = pcGrid[Arrays.asList(mpcGrid).indexOf(midiPitchClass - 1)];
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!sharpsInEffect.contains(pitch)) {
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) && 
//											KEY_ACCID_MPC_SHARP.contains(midiPitchClass)) {
//											accid = "x";
//										}
//										sharpsInEffect.add(pitch);
//									}
//								}
//								else {
//									pname = pcGrid[Arrays.asList(mpcGrid).indexOf(midiPitchClass + 1)];
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!flatsInEffect.contains(pitch)) {
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) &&
//											KEY_ACCID_MPC_FLAT.contains(midiPitchClass)) {
//											accid = "ff";
//										}
//										flatsInEffect.add(pitch);
//									}
//								}
//							}
//							else {
//								if (numAlt > 0) {
//									pname = pcGrid[Arrays.asList(mpcGrid).indexOf(midiPitchClass - 1)];
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!sharpsInEffect.contains(pitch)) {
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) && 
//											KEY_ACCID_MPC_SHARP.contains(midiPitchClass)) {
//											accid = "x";
//										}
//										sharpsInEffect.add(pitch);
//									}
//								}
//								else {
//									pname = pcGrid[Arrays.asList(mpcGrid).indexOf(midiPitchClass + 1)];
//									// Only if the accidental has not already been indicated for this specific pitch in the bar
//									if (!flatsInEffect.contains(pitch)) {
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (Arrays.asList(mpcGrid).contains(midiPitchClass) &&
//											KEY_ACCID_MPC_FLAT.contains(midiPitchClass)) {
//											accid = "ff";
//										}
//										flatsInEffect.add(pitch);
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			
//			pname = String.valueOf(pcGrid[pcInd]);
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
			System.out.println(Arrays.asList(curr));

			// 2. Set tie, dur, dots
			Rational remainingInBar = barEnd.sub(onset);
			// Single-bar note
			if (dur.isLessOrEqual(remainingInBar)) {
//\\				System.out.println("CASE: single-bar note");
//				Rational durRoundedTripletised = durRounded;
				List<Boolean> tripletInfo = (tripletOnsetPairs == null) ? null : 
					isTripletOnset(tripletOnsetPairs, onset);
				List<Object> noteData = 
					getNoteData(i, iTab, diminution, curr, dur, gridVal, /*bar,*/ onset, metPos, 
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
				Rational remainder = dur.sub(remainingInBar);
				// In the case of a tablature with predicted durations, those of the final chord
				// can be incorrectly predicted too long, thus extending beyond endOffset 
				if (offset.isGreater(endOffset)) {
					offset = endOffset;
				}
				int endBar = 
					(offset.equals(endOffset)) ? mi.get(mi.size()-1)[Transcription.MI_LAST_BAR] : 
					(tab != null ?
					tl.getMetricPosition((int) offset.mul(Tablature.SRV_DEN).toDouble())[0].getNumer()		
					:
					smtl.getMetricPosition(offset)[0].getNumer());
//					ScoreMetricalTimeLine.getMetricPosition(mtl, offset)[0].getNumer());
//					Utils.getMetricPosition(offset, mi)[0].getNumer());
				
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

			// If the note is the voice's last and its offset does not equal the piece end: complete with rests
			NotationVoice nv = p.getScore().get(voice).get(0);
			NotationChord lastNc = nv.get(nv.size()-1);
			if ((lastNc.getMetricTime().equals(onset)) && !offset.equals(endOffset)) {
//			if ((nv.get(nv.size()-1).getMetricTime().equals(onset)) && !offset.equals(endOffset)) {
				// Take into acount any chords: add rest only after the highest-pitched note in lastNc 
				int pitchHighestChordNote = lastNc.get(lastNc.size() - 1).getMidiPitch();
				if (lastNc.size() == 1 || (lastNc.size() > 1 && pitchHighestChordNote == pitch)) {
					// Add rest to fill up current bar (if applicable) 
					Rational restCurrentBar = barEnd.sub(offset);
					if (restCurrentBar.isGreater(Rational.ZERO)) {
						Rational metPosRestCurrentBar = metPos.add(dur);
//						Rational restCurrentBarTripletised = restCurrentBar;

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
			}
			noteAttribPerVoiceStrings.get(voice).addAll(pitchOctAccTie);
			noteAttribPerVoiceInts.get(voice).addAll(indBarOnsMpDurDots);
		}
		System.out.println("= * = * = * = * = * = * = * = * = * = * = * = * =");
		System.out.println(summL);
		System.out.println(summ);
//		System.exit(0);

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


	public static List<Object> createGrids(int numAlt, int mode) {
		Integer[][] mpcg = makeMIDIPitchClassGrid(mode);
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid(mode);
		int keyInd = numAlt + 7;
		Integer[] mpcGrid = mpcg[keyInd];
		String[] altGrid = ag[keyInd];
		String[] pcGrid = pcg[keyInd];

		return Arrays.asList(new Object[]{mpcGrid, altGrid, pcGrid});
	}


	/**
	 * Spells the given pitch, considering the given key signature as number of alterations. 
	 * numAlt <= 0 indicates flats; numAlt > 0 indicates sharps (NB: Am/C is considered a key 
	 * signature with zero flats.)
	 * 
	 * The method works only for key signatures with no more than five key accidentals (KA), i.e., 
	 * key signatures without double flats or double sharps. However, alterations may occasionally 
	 * lead to double sharps or flats.<br><br>
	 * 
	 * Sequence of determination
	 * <ul>
	 * <li>1. pitch is the next or second next KA.</li>
	 * <li>2. pitch is a naturalised KA.</li>
	 * <li>3. pitch is the upper or lower leading tone (ULT/LLT) for minor (or the minor parallel).</li>
	 * <li>4. pitch is the raised third (R3) for minor (or the minor parallel).</li>
	 * <li>5. pitch is raised sixth (R6) for minor (or the minor parallel).</li>
	 * </ul>
	 * 
	 * This covers all non-in-key pitches within the octave. Examples
	 * <ul>
	 * <li>Am  (0b): A, Bb (1), B, C, C# (4), D, Eb (1), E, F, F# (5), G, G# (3), A</li>
	 * <li>Dm  (1b): D, Eb (1), E, F, F# (4), G, Ab (1), A, Bb, B (2), C, C# (3), D</li>
	 * <li>Gm  (2b): G, Ab (1), A, Bb, B (2), C, Db (1), D, Eb, E (2), F, F# (3), G</li>
	 * <li>F#m (3#): F#, G (2), G#, A, A# (4), B, C (2), C#, D, D# (1), E, E# (3), F#</li>
	 * </ul>
	 *  
	 * @param pitch
	 * @param bar
	 * @param onset
	 * @param numAlt
	 * @param mpcGrid
	 * @param altGrid
	 * @param pcGrid
	 * @param accidsInEffect
	 * @return A list containing
	 *         <ul>
	 *         <li>As element 0: a String[] containing pname and accid, in MEI terminology.</li>
	 *         <li>As element 1: the updated <code>accidsInEffect</code> (if it is not <code>null</code>).</li>
	 *         </ul>
	 */
	public static List<Object> spellPitch(int pitch, int bar, Rational onset, int numAlt, Integer[] mpcGrid, 
		String[] altGrid, String[] pcGrid, List<List<Integer>> accidsInEffect) {
		String pname = "";
		String accid = "";

		List<Integer> doubleFlatsInEffect = null;
		List<Integer> flatsInEffect = null; 
		List<Integer>  naturalsInEffect = null;
		List<Integer> sharpsInEffect = null;
		List<Integer> doubleSharpsInEffect = null;
		if (accidsInEffect != null) {
			doubleFlatsInEffect = accidsInEffect.get(0);
			flatsInEffect = accidsInEffect.get(1);
			naturalsInEffect = accidsInEffect.get(2);
			sharpsInEffect = accidsInEffect.get(3);
			doubleSharpsInEffect = accidsInEffect.get(4);
		}

		List<Integer> mpcGridList = Arrays.asList(mpcGrid);
		int mpc = pitch % 12; // value is between and including [0, 11]
		List<String> accids = Arrays.asList(new String[]{"ff", "f", "n", "s", "x"});
		boolean considerContext = accidsInEffect != null && !accidsInEffect.contains(null);
		boolean isMinor = mpcGrid[2] - mpcGrid[0] == 3 || Math.abs(mpcGrid[2] - mpcGrid[0]) == 9;

		// Get the grids for the nominal major of minor (or the minor parallel), e.g., 
		// E for Em/G; B for Bm/D; etc.		
		Integer[] mpcGridNomMajOfMin = 
			isMinor ? Arrays.copyOf(mpcGrid, mpcGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(mpcGrid, 5, 7), Arrays.copyOfRange(mpcGrid, 0, 5));
		mpcGridNomMajOfMin[2] = (mpcGridNomMajOfMin[2] + 1) % 12;
		mpcGridNomMajOfMin[5] = (mpcGridNomMajOfMin[5] + 1) % 12;
		mpcGridNomMajOfMin[6] = (mpcGridNomMajOfMin[6] + 1) % 12;
		String[] altGridNomMajOfMin = 	
			isMinor ? Arrays.copyOf(altGrid, altGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(altGrid, 5, 7), Arrays.copyOfRange(altGrid, 0, 5));
		altGridNomMajOfMin[2] = accids.get(accids.indexOf(altGridNomMajOfMin[2]) + 1);
		altGridNomMajOfMin[5] = accids.get(accids.indexOf(altGridNomMajOfMin[5]) + 1);
		altGridNomMajOfMin[6] = accids.get(accids.indexOf(altGridNomMajOfMin[6]) + 1);
		String[] pcGridNomMajOfMin =
			isMinor ? Arrays.copyOf(pcGrid, pcGrid.length) :
			ArrayUtils.addAll(Arrays.copyOfRange(pcGrid, 5, 7), Arrays.copyOfRange(pcGrid, 0, 5));

		// a. pitch is in key
		if (mpcGridList.contains(mpc)) {
			if (verbose) System.out.println("pitch is in key");
			int pcInd = mpcGridList.indexOf(mpc);
			pname = pcGrid[pcInd];
			if (!considerContext) {
				accid = altGrid[pcInd];
			}
			else {
				// Previously double flat (but must be flat)
				if (doubleFlatsInEffect.contains(pitch-1)) {
					accid = "f";
					doubleFlatsInEffect.remove(doubleFlatsInEffect.indexOf(pitch-1));
				}
				// Previously flat (but must be natural)
				else if (flatsInEffect.contains(pitch-1)) {
					accid = "n";
					flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
				}
				// Previously natural (but must be flat)
				else if (naturalsInEffect.contains(pitch+1) && altGrid[pcInd] == "f") {
					accid = "f";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
				}
				// Previously natural (but must be sharp)
				else if (naturalsInEffect.contains(pitch-1) && altGrid[pcInd] == "s") {
					accid = "s";
					naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
				}
				// Previously sharp (but must be natural)
				else if (sharpsInEffect.contains(pitch+1)) {
					accid = "n";
					sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
				}
				// Previously double sharp (but must be sharp)
				else if (doubleSharpsInEffect.contains(pitch+1)) {
					accid = "f";
					doubleSharpsInEffect.remove(doubleSharpsInEffect.indexOf(pitch+1));
				}
				// No accidental
				else {
					accid = "";
				}
			}
		}
		// b. pitch is not in key
		else {
			List<Integer> mpcKeySigs = getMIDIPitchClassKeySigs(numAlt);
			boolean isNextOrSecondNextKA = false;
			boolean isNaturalisedKA = false;
			boolean isLLTForMinor = false;
			boolean isR3ForMinor = false;

			// 1. pitch is the next or second-next KA
			// Flats
			if (numAlt <= 0) {
				String alt = altGrid[mpcGridList.indexOf((mpc+1) % 12)];
				int indLastKeyAccid = 
					(numAlt == 0) ? -1 : KEY_ACCID_MPC_FLAT.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
				for (int incr : new Integer[]{1, 2}) {
					if (KEY_ACCID_MPC_FLAT.get(indLastKeyAccid + incr) == mpc) {
						if (verbose) System.out.println("pitch is next or second-next KA (flats)");
						pname = KEY_ACCID_PC_FLAT.get(indLastKeyAccid + incr);
						accid = accids.get(accids.indexOf(alt) - 1);
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "f"
							if (!flatsInEffect.contains(pitch)) {
								flatsInEffect.add(pitch);	
							}
							else {
								accid = "";
							}
						}
						isNextOrSecondNextKA = true;
						break;
					}
				}
			}
			// Sharps
			else if (numAlt > 0) {
				String alt = altGrid[mpcGridList.indexOf(((mpc-1) + 12) % 12)];
				int indLastKeyAccid = 
					/*(numAlt == 0) ? -1 : */ KEY_ACCID_MPC_SHARP.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
				for (int incr : new Integer[]{1, 2}) {
					if (KEY_ACCID_MPC_SHARP.get(indLastKeyAccid + incr) == mpc) {
						if (verbose) System.out.println("pitch is next or second-next KA (sharps)");
						pname = KEY_ACCID_PC_SHARP.get(indLastKeyAccid + incr);
						accid = accids.get(accids.indexOf(alt) + 1);
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "s"
							if (!sharpsInEffect.contains(pitch)) {
								sharpsInEffect.add(pitch);
							}
							else {
								accid = "";
							}
						}
						isNextOrSecondNextKA = true;
						break;
					}
				}
			}
			if (!isNextOrSecondNextKA) {
				// 2. pitch is a naturalised KA
				if (numAlt < 0 && mpcKeySigs.contains((pitch-1) % 12) ||
					numAlt > 0 && mpcKeySigs.contains((pitch+1) % 12)) {
					// Exception for LLT; continue to 3. below  
					if (numAlt == 3 && mpc == 5 || numAlt == 4 && mpc == 0 || numAlt == 5 && mpc == 7) {
						if (verbose) System.out.println("pitch is LLT (sharps) (hardcoded)");
					}
					else {
						if (verbose) System.out.println("pitch is naturalised KA");
						int pcInd = -1;
						// Flats
						if (numAlt < 0) {
							if (mpcKeySigs.contains((pitch-1) % 12)) {
								pcInd = mpcGridList.indexOf((pitch-1) % 12);
							}
						}
						// Sharps
						else {
							if (mpcKeySigs.contains((pitch+1) % 12)) {
								pcInd = mpcGridList.indexOf((pitch+1) % 12);
							}
						}
						pname = pcGrid[pcInd];
						accid = "n";
						if (considerContext) {
							// Since keys sigs with double KAs are not considered, accid can only be "n"
							if (!naturalsInEffect.contains(pitch)) {
								naturalsInEffect.add(pitch);
							}
							else { 
								accid = "";
							}
						}
						isNaturalisedKA = true;
					}
				}
				if (!isNaturalisedKA) {
					// 3. pitch is the upper or lower leading tone (ULT/LLT) for minor (or the minor parallel)
					// a. The ULT case is fully covered above
					//    - flats: the ULT is the next KA
					//    - sharps: the ULT is the last KA, naturalised
					// b. The LLT case is partly covered above
					//	  - zero, one, or two flats: the LLT still has to be calculated
					//    - three flats or more: the LLT is the third-last KA, naturalised   
					//	  - one or two sharps: the LLT still has to be calculated
					//    - three sharps or more: the LLT is the *enharmonic equivalent* of the
					//      third-last KA, naturalised (E# = F; B# = C; Fx = G), and therefore 
					//      caught at 2. above and hardcoded at 3.
					if (mpc == mpcGridNomMajOfMin[mpcGridNomMajOfMin.length-1]) {
						if (verbose) System.out.println("pitch is LLT for minor");
						if (numAlt == 3 && mpc == 5 || numAlt == 4 && mpc == 0 || numAlt == 5 && mpc == 7) {
							pname = numAlt == 3 ? "e" : (numAlt == 4 ? "b" : "f");
							accid = numAlt == 3 ? "s" : (numAlt == 4 ? "s" : "x");
						}
						else {
							pname = pcGridNomMajOfMin[pcGrid.length-1];
							accid = altGridNomMajOfMin[pcGrid.length-1];
							if (considerContext) {
								// Since keys sigs with double KAs are not considered, accid can only be
								// "n", "s", or "x"
								if (accid.equals("n")) {
									if (!naturalsInEffect.contains(pitch)) {
										naturalsInEffect.add(pitch);
									}
									else { 
										accid = "";
									}
								}
								else if (accid.equals("s")) {
									if (!sharpsInEffect.contains(pitch)) {
										sharpsInEffect.add(pitch);
									}
									else {
										accid = "";
									}
								}
								else if (accid.equals("x")) {
									if (!doubleSharpsInEffect.contains(pitch)) {
										doubleSharpsInEffect.add(pitch);
									}
									else {
										accid = "";
									}
								}
							}
							isLLTForMinor = true;
						}
					}
					if (!isLLTForMinor) {
						// 4. pitch is the raised third (R3) for minor (or the minor parallel)
						//    This is partly covered above
						//    - zero or one flats: the R3 still has to be calculated
						//    - two flats or more: the R3 is the second-last KA, naturalised
						//    - sharps: the R3 is the second-next KA
						if (mpc == mpcGridNomMajOfMin[2]) {
							if (verbose) System.out.println("pitch is R3 for minor");
							pname = pcGridNomMajOfMin[2];
							accid = altGridNomMajOfMin[2];
							if (considerContext) {
								// Since keys sigs with double KAs are not considered, accid can only be 
								// "n" or "s"
								if (accid.equals("n")) {
									if (!naturalsInEffect.contains(pitch)) {
										naturalsInEffect.add(pitch);
									}
									else {
										accid = "";
									}
								}
								else if (accid.equals("s")) {
									if (!sharpsInEffect.contains(pitch)) {
										sharpsInEffect.add(pitch);
									}
									else {
										accid = "";
									}
								}
							}
							isR3ForMinor = true;							
						}
						if (!isR3ForMinor) {
							// 5. pitch is the raised sixth (R6) for minor (or the minor parallel)
							//    This is partly covered above
							//    - zero flats: the R6 still has to be calculated
							//    - one flat or more: the R6 is the last KA, naturalised
							//    - sharps: the R6 is the next KA
							if (mpc == mpcGridNomMajOfMin[5]) {
								if (verbose) System.out.println("pitch is R6 for minor");
								pname = pcGridNomMajOfMin[5];
								accid = altGridNomMajOfMin[5];
								if (considerContext) {
									// Since keys sigs with double KAs are not considered, accid can only be
									// "n" or "s"
									if (accid.equals("n")) {	
										if (!naturalsInEffect.contains(pitch)) {
											naturalsInEffect.add(pitch);
										}
										else { 
											accid = "";
										}
									}
									else if (accid.equals("s")) {
										if (!sharpsInEffect.contains(pitch)) {
											sharpsInEffect.add(pitch);
										}
										else { 
											accid = "";
										}
									}
								}
							}
						}
					}
				}
			}
		}
		String[] pa = new String[]{pname, accid};
		return Arrays.asList(new Object[]{pa, accidsInEffect});
	}


	public static List<Object> spellPitchOLD(int pitch, int numAlt, Integer[] mpcGrid, String[] altGrid, 
			String[] pcGrid, List<List<Integer>> accidsInEffect, NotationVoice nv, Rational onset) {
			String pname = "";
			String accid = "";

			List<Integer> doubleFlatsInEffect = accidsInEffect.get(0);
			List<Integer> flatsInEffect = accidsInEffect.get(1);
			List<Integer> naturalsInEffect = accidsInEffect.get(2);
			List<Integer> sharpsInEffect = accidsInEffect.get(3);
			List<Integer> doubleSharpsInEffect = accidsInEffect.get(4);
			
			boolean considerContext = 
				doubleFlatsInEffect != null && flatsInEffect != null && naturalsInEffect != null && 
				sharpsInEffect != null && doubleSharpsInEffect != null;
			boolean isMinor = mpcGrid[2] - mpcGrid[0] == 3 || Math.abs(mpcGrid[2] - mpcGrid[0]) == 9;
			System.out.println(pitch % 12);
			System.out.println(Arrays.asList(mpcGrid));
			System.out.println(Arrays.asList(altGrid));
			System.out.println(Arrays.asList(pcGrid));
			
			List<String> accids = Arrays.asList(new String[]{"ff", "f", "n", "s", "x"});
			
			Integer[] mpcGridNomMaj = null;
			if (isMinor) {
				// Get the nominal major 
				mpcGridNomMaj = Arrays.copyOf(mpcGrid, mpcGrid.length);
				mpcGridNomMaj[2] = (mpcGridNomMaj[2] + 1) % 12;
				mpcGridNomMaj[5] = (mpcGridNomMaj[5] + 1) % 12;
				mpcGridNomMaj[6] = (mpcGridNomMaj[6] + 1) % 12;
			}
			
			List<Integer> mpcGridList = Arrays.asList(mpcGrid);
			int mpc = pitch % 12;

			// pitch is in key
			if (mpcGridList.contains(mpc)) {
				System.out.println("in key");
				int pcInd = mpcGridList.indexOf(mpc);
				pname = pcGrid[pcInd];
				if (!considerContext) {
					accid = altGrid[pcInd];
				}
				if (considerContext) {
					// Previously double flat (but must be flat)
					if (doubleFlatsInEffect.contains(pitch-1)) {
						accid = "f";
						doubleFlatsInEffect.remove(doubleFlatsInEffect.indexOf(pitch-1));
					}
					// Previously flat (but must be natural)
					else if (flatsInEffect.contains(pitch-1)) {
						accid = "n";
						flatsInEffect.remove(flatsInEffect.indexOf(pitch-1));
					}
					// Previously natural (but must be flat)
					else if (altGrid[pcInd] == "f" && naturalsInEffect.contains(pitch+1)) {
						accid = "f";
						naturalsInEffect.remove(naturalsInEffect.indexOf(pitch+1));
					}
					// Previously natural (but must be sharp)
					else if (altGrid[pcInd] == "s" && naturalsInEffect.contains(pitch-1)) {
						accid = "s";
						naturalsInEffect.remove(naturalsInEffect.indexOf(pitch-1));
					}
					// Previously sharp (but must be natural)
					else if (sharpsInEffect.contains(pitch+1)) {
						accid = "n";
						sharpsInEffect.remove(sharpsInEffect.indexOf(pitch+1));
					}
					// Previously double sharp (but must be sharp)
					else if (doubleSharpsInEffect.contains(pitch+1)) {
						accid = "f";
						doubleSharpsInEffect.remove(doubleSharpsInEffect.indexOf(pitch+1));
					}
					// No accidental
					else {
						accid = "";
					}
				}
			}
			// pitch is not in key
			else {
				System.out.println("NOT in key");
				List<Integer> mpcKeySigs = getMIDIPitchClassKeySigs(numAlt);
				boolean isNaturalisedKA = false;
				boolean isNextOrSecondNextKA = false;
				boolean isLLTForMinor = false;
								
				// 1. If pitch is a naturalised key accidental (KA)
				// NB: Since keys sigs with double flats/sharps are not considered, accid is 
				// always "n" (and never "f" (from "ff") or "s" (from "ss"))
				if (numAlt < 0 && mpcKeySigs.contains((pitch-1) % 12) ||
					numAlt > 0 && mpcKeySigs.contains((pitch+1) % 12)) {
					if (verbose) System.out.println("is naturalised KA");
					int pcInd = -1;
					// if-else needed to consistently spell double flats and sharps as naturalised KAs
					// (see test examples for Bbm and G#m)
					if (numAlt <= 0) {
						// Natural for flat
						if (mpcKeySigs.contains((pitch-1) % 12)) {
							pcInd = mpcGridList.indexOf((pitch-1) % 12);
						}
						// Natural for sharp
						else if (mpcKeySigs.contains((pitch+1) % 12)) {
							pcInd = mpcGridList.indexOf((pitch+1) % 12);
						}
					}
					else {
						// Natural for sharp
						if (mpcKeySigs.contains((pitch+1) % 12)) {
							pcInd = mpcGridList.indexOf((pitch+1) % 12);
						}
						// Natural for flat
						else if (mpcKeySigs.contains((pitch-1) % 12)) {
							pcInd = mpcGridList.indexOf((pitch-1) % 12);
						}
					}
					pname = pcGrid[pcInd];
					if (!considerContext) {
						accid = "n";
					}
					if (considerContext) {
						// Only if the natural has not already been indicated for this specific pitch in the bar
						if (!naturalsInEffect.contains(pitch)) {
							accid = "n";
							naturalsInEffect.add(pitch);
						}
					}
					isNaturalisedKA = true;
				}
				// If not 1.: continue
				if (!isNaturalisedKA) {
//				else {
					if (verbose) System.out.println("is accidentalised");

//					// Find pitch of next note of different pitch. If there is none, the voice
//					// ends with a sequence of the same pitches (i.e., with a repetition of 
//					// the last pitch), and nextPitch remains -1
//					int nextPitch = -1;	
//					if (nv != null) {
//						// If not last note
//						if (!(nv.get(nv.size()-1).getMetricTime().equals(onset))) {
//							for (int j = 0; j < nv.size() && nextPitch == -1; j++) {
//								if (nv.get(j).getMetricTime().equals(onset)) {
//									for (int k = j+1; k < nv.size(); k++) {
//										int currNextPitch = nv.get(k).get(0).getMidiPitch();
//										if (currNextPitch != pitch) {
//											nextPitch = currNextPitch;
//											break;
//										}
//									}
//								}
//							}
//						}
//					}
//					boolean nextIsInKey = mpcGridList.contains(nextPitch % 12);
//					// a. Direct leading tone (next different pitch is in-key and a semitone lower/higher)
//					//    or last note in voice (do only if nv is not null)    
//					if (nv != null && nextIsInKey && 
//						(nextPitch == (pitch + 1) || nextPitch == (pitch - 1) || nextPitch == -1)) {
//						// Sharps (if last note, assume sharp )
//						if ((nextPitch != -1 && nextPitch == (pitch + 1)) || nextPitch == -1) {
//							if (verbose) System.out.println("DLT, sharp");
//							// pname is pc of un-sharpened pitch
//							int pcInd = mpcGridList.indexOf((pitch-1) % 12);
//							pname = pcGrid[pcInd];
//							if (!considerContext) {
//								accid = "s";
//								// If pitch is already a sharp: double sharp
//								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//									accid = "x";
//								}
//							}
//							if (considerContext) {
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!sharpsInEffect.contains(pitch)) {
//									accid = "s";
//									// If pitch is already a sharp: double sharp
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//										accid = "x";
//									}
//									sharpsInEffect.add(pitch);
//								}
//							}
//						}
//						// Flats
//						else if (nextPitch != -1 && nextPitch == (pitch - 1)) {
//							if (verbose) System.out.println("DLT, flat");
//							// pname is pc of un-flattened pitch
//							int pcInd = mpcGridList.indexOf((pitch+1) % 12);
//							pname = String.valueOf(pcGrid[pcInd]);
//							if (!considerContext) {
//								accid = "f";
//								// If pitch is already a flat: double flat
//								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//									accid = "ff";
//								}
//							}
//							if (considerContext) {
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!flatsInEffect.contains(pitch)) {
//									accid = "f";
//									// If pitch is already a flat: double flat
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//										accid = "ff";
//									}
//									flatsInEffect.add(pitch);
//								}
//							}
//						}
//					}
//					// b. Not a direct leading tone
//					else {
						 
					// 2. If pitch is the next or second-next KA
					// NB: It is assumed that double flats or double sharps are not needed, i.e., that the 
					// key sigs used have no more than five KAs
					// - Bb Eb Ab Db Gb Cb Fb
					//   - 6 flats would require Fb and Bbb as next and second-next KA; 7 would require Bbb and Ebb; etc. 
					// - F# C# G# D# A# E# B#
					//   - 6 sharps would require B# and Fx as next and second-next KA; 7 would require Fx and Cx; etc.
					// Flats (or no KA and minor)
					if (numAlt < 0 || (numAlt == 0 && isMinor)) {
						String alt = altGrid[mpcGridList.indexOf(mpc+1)];
						int indLastKeyAccid = 
							(numAlt == 0 && isMinor) ? -1 :
							KEY_ACCID_MPC_FLAT.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
						for (int incr : new Integer[]{1, 2}) {
							if (KEY_ACCID_MPC_FLAT.get(indLastKeyAccid + incr) == mpc) {
								System.out.println("is KA, flat");
								pname = KEY_ACCID_PC_FLAT.get(indLastKeyAccid + incr);
								if (!considerContext) {
									accid = accids.get(accids.indexOf(alt) - 1);
//									accid = "f";
//									// If pitch is already a flat: double flat
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//										accid = "ff";
//									}
								}
								if (considerContext) {
									// Only if the accidental has not already been indicated for this specific pitch in the bar
									if (!flatsInEffect.contains(pitch)) {
										accid = accids.get(accids.indexOf(alt) - 1);
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//											accid = "ff";
//										}
										// TODO add to correct list
										flatsInEffect.add(pitch);
									}
								}
								isNextOrSecondNextKA = true;
								break;
							}
						}
					}
					// Sharps (or no KA and major)
					else if (numAlt > 0 || (numAlt == 0 && !isMinor)) {
						String alt = altGrid[mpcGridList.indexOf(mpc-1)];
						int indLastKeyAccid = 
							(numAlt == 0 && !isMinor) ? -1 :	
							KEY_ACCID_MPC_SHARP.indexOf(mpcKeySigs.get(mpcKeySigs.size()-1));
						for (int incr : new Integer[]{1, 2}) {
							if (KEY_ACCID_MPC_SHARP.get(indLastKeyAccid + incr) == mpc) {
								pname = KEY_ACCID_PC_SHARP.get(indLastKeyAccid + incr);
								System.out.println("is KA, sharp");
								if (!considerContext) {
									accid = accids.get(accids.indexOf(alt) + 1);
//									accid = "s";
//									// If pitch is already a sharp: double sharp
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//										accid = "x";
//									}
								}
								if (considerContext) {
									// Only if the accidental has not already been indicated for this specific pitch in the bar
									if (!sharpsInEffect.contains(pitch)) {
										accid = accids.get(accids.indexOf(alt) + 1);
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//											accid = "x";
//										}
										// TODO add to correct list
										sharpsInEffect.add(pitch);
									}
								}
								isNextOrSecondNextKA = true;
								break;
							}
						}	
					}
					// If not 2.: continue
					if (!isNextOrSecondNextKA) {					
//						int mpcMinor = KEY_SIG_MPCS.get(numAlt)[1];
//						if (mpc == mpcMinor + 1) {
//							System.out.println("is ULT");
//							// If mpcMinor is not the last element of mpcGridList: take next
//							if (mpcGridList.indexOf(mpcMinor) != (mpcGridList.size() - 1)) {
//								pname = pcGrid[mpcGridList.indexOf(mpcMinor) + 1];
//							}
//							// Else: next is first element
//							else {
//								pname = pcGrid[0];
//							}
//							if (!considerContext) {
//								accid = "f";
//								// If pitch is already a flat: double flat
//								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//									accid = "ff";
//								}
//							}
//							if (considerContext) {
//								// Only if the accidental has not already been indicated for this specific pitch in the bar
//								if (!flatsInEffect.contains(pitch)) {
//									accid = "f";
//									// If pitch is already a flat: double flat
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//										accid = "ff";
//									}
//									flatsInEffect.add(pitch);
//								}
//							}
//							isLeadingToneForMinor = true;
//						}
						
						// 3. If pitch is the lower leading tone (LLT) for the minor parallel
						// NB: the upper leading tone (ULT) for minor is always covered above
						//     - in case of flats, ULT is next KA: Bb for Am, Eb for Dm; Ab for Gm; Db for Cm; ...
						//     - in case of sharps, ULT is last KA naturalised: F for Em; C for Bm; G for F#m; ...
						// NB2: double flats (for ULT) and double sharps (for LLT) will be caught (wrongly!) above 
						//      as naturalised KA, e.g.,
						//      - Cb in Bbm will be caught as B
						//      - Fx in G#m will be caught as G
						if (isMinor && mpc == mpcGridNomMaj[mpcGridNomMaj.length-1]) {
//						if (mpc == mpcMinor - 1) {
							System.out.println("is LLT");
							pname = pcGrid[pcGrid.length-1];
							String alt = altGrid[pcGrid.length-1];
							
//							// If mpcMinor is not the first element of mpcGridList: take previous
//							if (mpcGridList.indexOf(mpcMinor) != 0) {
//								pname = pcGrid[mpcGridList.indexOf(mpcMinor) - 1];
//							}
//							// Else: previous is last element
//							else {
//								pname = pcGrid[mpcGridList.size() - 1];
//							}
							if (!considerContext) {
								accid = accids.get(accids.indexOf(alt) + 1);
//								accid = "s";
//								// If pitch is already a sharp: double sharp
//								if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//									accid = "x";
//								}
							}
							if (considerContext) {
								// Only if the accidental has not already been indicated for this specific pitch in the bar
								if (!sharpsInEffect.contains(pitch)) {
									accid = accids.get(accids.indexOf(alt) + 1);
//									accid = "s";
//									// If pitch is already a sharp: double sharp
//									if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//										accid = "x";
//									}
									// TODO add to correct list
									sharpsInEffect.add(pitch);
								}
							}
							isLLTForMinor = true;
						}
						// If not 3.: continue
						if (!isLLTForMinor) {
							// 4. If pitch is 
						}
					}
						// 3. Else: spell as whichever flat or sharp has the earliest key accidental index, e.g.,
						//    - C# (2nd sharp) preferred over Db (4th flat)
						//    - Bb (1st flat) preferred over A# (5th sharp)) 
						if (!isNextOrSecondNextKA && !isLLTForMinor) {
							int indInSharps = KEY_ACCID_MPC_SHARP.indexOf(mpc);
							int indInFlats = KEY_ACCID_MPC_FLAT.indexOf(mpc);
//							System.out.println(bar);
//							System.out.println(voice);
//							System.out.println(mpc);
//							System.out.println(indInFlats);
//							System.out.println(indInSharps);
							
							// NB: The KA index in flats and sharps being the same occurs only in the case of 
							// G# == Ab, and this accidental is always covered above
							// - in case of no KA: G# is LLT for Am
							// - in case of flats: Ab is second-next KA for Dm; next KA for Gm; KA for Cm and up 
							// - in case of sharps: G# is second-next KA for G; next KA for D; KA for A and up
							if (indInSharps != indInFlats) {
//								pname = pcGrid[mpcGridList.indexOf(
//									(indInSharps < indInFlats ? midiPitchClass - 1 : midiPitchClass + 1))];
//								accid = indInSharps < indInFlats ? "s" : "f";
								if (indInSharps < indInFlats) {
									System.out.println("index in sharps earlier");
									pname = pcGrid[mpcGridList.indexOf(mpc - 1)];
									if (!considerContext) {
										accid = "s";
										// If pitch is already a sharp: double sharp
										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
											accid = "x";
										}
									}
									if (considerContext) {
										// Only if the accidental has not already been indicated for this specific pitch in the bar
										if (!sharpsInEffect.contains(pitch)) {
											accid = "s";
											// If pitch is already a sharp: double sharp
											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
												accid = "x";
											}
											sharpsInEffect.add(pitch);
										}
									}
								}
								else {
									System.out.println("index in flats earlier");
									pname = pcGrid[mpcGridList.indexOf(mpc + 1)];
									if (!considerContext) {
										accid = "f";
										// If pitch is already a flat: double flat
										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
											accid = "ff";
										}
									}
									if (considerContext) {
										// Only if the accidental has not already been indicated for this specific pitch in the bar
										if (!flatsInEffect.contains(pitch)) {
											accid = "f";
											// If pitch is already a flat: double flat
											if (mpcGridList.contains(mpc) &&
												KEY_ACCID_MPC_FLAT.contains(mpc)) {
												accid = "ff";
											}
											flatsInEffect.add(pitch);
										}
									}
								}
							}
//							else {
//								if (numAlt > 0) {
//									System.out.println("index in flats/sharps same, KS has sharps");
//									pname = pcGrid[mpcGridList.indexOf(mpc - 1)];
//									if (!considerContext) {
//										accid = "s";
//										// If pitch is already a sharp: double sharp
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//											accid = "x";
//										}
//									}
//									if (considerContext) {
//										// Only if the accidental has not already been indicated for this specific pitch in the bar
//										if (!sharpsInEffect.contains(pitch)) {
//											accid = "s";
//											// If pitch is already a sharp: double sharp
//											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_SHARP.contains(mpc)) {
//												accid = "x";
//											}
//											sharpsInEffect.add(pitch);
//										}
//									}
//								}
//								else {
//									System.out.println("index in flats/sharps same, KS has flats");
//									pname = pcGrid[mpcGridList.indexOf(mpc + 1)];
//									if (!considerContext) {
//										accid = "f";
//										// If pitch is already a flat: double flat
//										if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//											accid = "ff";
//										}
//									}
//									if (considerContext) {
//										// Only if the accidental has not already been indicated for this specific pitch in the bar
//										if (!flatsInEffect.contains(pitch)) {
//											accid = "f";
//											// If pitch is already a flat: double flat
//											if (mpcGridList.contains(mpc) && KEY_ACCID_MPC_FLAT.contains(mpc)) {
//												accid = "ff";
//											}
//											flatsInEffect.add(pitch);
//										}
//									}
//								}
//							}
						}
					}
				}
			
			String[] pa = new String[]{pname, accid};
			return Arrays.asList(new Object[]{pa, accidsInEffect});
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
			int currBar = Utils.getMetricPosition(currOnset, mi)[0].getNumer(); // TODO pass tab and trans as args to be able to use tab/trans.getMetricPosition()
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
					metPosTripletOpen = Utils.getMetricPosition(currTripletOpenOnset, mi)[1];
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


	/**
	 * Returns, for the given key, the MIDI pitch classes of the key signature for that key. 
	 * A MIDI pitch class is a note's MIDI pitch % 12, and has one of the values [0-11]. 
	 * 
	 * Example Ab major: [10, 3, 8, 1] (= Bb, Eb, Ab, Dd)
	 * Example A major: [6, 1, 8] (= F#, C#, G#)
	 * 
	 * @param key
	 * @return
	 */
	// TESTED
	static List<Integer> getMIDIPitchClassKeySigs(int numAlt/*Integer[] key*/) {
		List<Integer> mpcKeySigs = new ArrayList<Integer>();
		
//		int numAlt = key[Transcription.KI_KEY];
		// Flats
		if (numAlt < 0) {
			mpcKeySigs.addAll(KEY_ACCID_MPC_FLAT.subList(0, -numAlt));
		}
		// Sharps
		else if (numAlt > 0) {
			mpcKeySigs.addAll(KEY_ACCID_MPC_SHARP.subList(0, numAlt));
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
	 * @param mode
	 * @return
	 */
	// TESTED
	static Integer[][] makeMIDIPitchClassGrid(int mode) {
		List<Integer> semitones = Arrays.asList(new Integer[]{2, 2, 1, 2, 2, 2, 1});
		
		Integer[][] mpcGrid = new Integer[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currBeginPitch = entry.getValue()[0];
			List<Integer> asList = new ArrayList<Integer>();
			asList.add(currBeginPitch);
			for (int j = 0; j < semitones.size()-1; j++) {
				asList.add((asList.get(j) + semitones.get(j)) % 12);
			}
			mpcGrid[i] = asList.toArray(new Integer[asList.size()]);
			i++;
		}
		if (mode == 1) {
			for (int j = 0; j < mpcGrid.length; j++) {
				mpcGrid[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(mpcGrid[j], 5, 7), Arrays.copyOfRange(mpcGrid[j], 0, 5)
				);
			}
		}
		return mpcGrid;
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
		String[][] altGrid = new String[KEY_SIG_MPCS.size()][7];
		int i = 0;
		for (Entry<Integer, Integer[]> entry : KEY_SIG_MPCS.entrySet()) {
			int currKey = entry.getKey();
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
			i++;
		}
		return altGrid;
	}


	/**
	 * Returns, for each key (starting at 7 flats and ending at 7 sharps), the pitch classes 
	 * for that key. 
	 * A pitch class is a note's nominal pitch, and has one of the values 
	 * ["c", "d", "e", "f", "g", "a", "b"].  
	 * 
	 * @mode 
	 * @return
	 */
	// TESTED
	static String[][] makePitchClassGrid(int mode) {
		String[] pitchCl = new String[]{"c", "d", "e", "f", "g", "a", "b"};		
		List<String> pitchClasses = new ArrayList<String>();
		for (String s : pitchCl) {
			pitchClasses.add(s);
		}

		int fromInd = 0;
		String[][] pcGrid = new String[KEY_SIG_MPCS.size()][7];
		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
			// Reorder pitchClasses: split at fromIdex and paste the first part after the second
			List<String> asList = 
				new ArrayList<String>(pitchClasses.subList(fromInd, pitchClasses.size())); 
			List<String> secondHalf = pitchClasses.subList(0, fromInd);
			asList.addAll(secondHalf);
			pcGrid[i] = asList.toArray(new String[asList.size()]);
			// Increment fromInd to be the index of the note a fifth higher
			fromInd = (fromInd + 4) % 7;
		}
		if (mode == 1) {
			for (int j = 0; j < pcGrid.length; j++) {
				pcGrid[j] = ArrayUtils.addAll(
					Arrays.copyOfRange(pcGrid[j], 5, 7), Arrays.copyOfRange(pcGrid[j], 0, 5)
				);
			}
		}
		return pcGrid;
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
			int currBar = Utils.getMetricPosition(currOnset, mi)[0].getNumer();
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
					metPosTripletOpen = Utils.getMetricPosition(currTripletOpenOnset, mi)[1];
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
		meiHead[MEI_HEAD.indexOf("title")] = tab.getName();
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		List<Integer[]> mi = tab.getMeterInfo();
//		List<Integer[]> mi = tab.getTimeline().getMeterInfoOBS();
		List<String[]> meters = new ArrayList<>();
		for (Integer[] in : mi) {
			String sym = "";
			if (in[Transcription.MI_NUM] == 4 && in[Transcription.MI_DEN] == 4) {
				sym = " meter.sym='common'";
			}
			else if (in[Transcription.MI_NUM] == 2 && in[Transcription.MI_DEN] == 2) {
				sym = " meter.sym='cut'";
			}
			meters.add(new String[]{
				"meter.count='" + in[Transcription.MI_NUM] + "'", 
				"meter.unit='" + in[Transcription.MI_DEN] + "'",
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
							Symbol.getRhythmSymbol(sicEvent.substring(0, sicEvent.indexOf(ss)));
						int durSic; 
						if (rsSic != null) {
							durSic = rsSic.getDuration();
						}
						else {
							durSic = -1; // TODO get last specified duration before currEvent
						}
						RhythmSymbol rsCorr = 
							Symbol.getRhythmSymbol(corrEvent.substring(0, corrEvent.indexOf(ss)));
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
									Symbol.getRhythmSymbol(nextEvent.substring(0, 
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
				if (Symbol.getMensurationSign(firstEventNextSplit[0]) != null) {
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


	private List<String[][]> combine(int mode) {
		List<String[][]> combined = new ArrayList<String[][]>();
		Integer[][] mpcg = makeMIDIPitchClassGrid(mode);
		String[][] ag = makeAlterationGrid(mpcg);
		String[][] pcg = makePitchClassGrid(mode);
		
		for (int i = 0; i < KEY_SIG_MPCS.size(); i++) {
			String[][] curr = new String[3][7];
			// MIDI pitch classes
			 
		}
		
		return combined;
	}

}
