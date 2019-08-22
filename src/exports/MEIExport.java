package exports;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.utility.math.Rational;

import representations.Tablature;
import representations.Transcription;
import tools.ToolBox;
import utility.DataConverter;

public class MEIExport {
	
	public static String rootDir = "F:/research/"; // TODO also defined in UI; should be in one place only
	public static String MEITemplatePath = rootDir + "data/" + "templates/MEI/"; // TODO suffix "data/" is defined inside Runner.setPathsToCodeAndData() 
	
	private static List<Integer> mpcFlats = 
		Arrays.asList(new Integer[]{10, 3, 8, 1, 6, 11, 4}); // Bb, Eb, Ab, Db, Gb, Cb, Fb,
	private static List<Integer> mpcSharps = 
		Arrays.asList(new Integer[]{6, 1, 8, 3, 10, 5, 0}); // F#, C#, G#, D#, A#, E#, B#
	
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
		"ind", "indTab", "bar", "onsetNum", "onsetDen", "metPosNum", "metPosDen", "dur", "dots"});
	private static final String TAB = "    ";
	private static final String INDENT = TAB.repeat(6);
	
	public static void main(String[] args) {
		
		String path = "C:/Users/Reinier/Desktop/MEI/";
		String tabFile = "tab-int/3vv/newsidler-1544_2-nun_volget";
		// This must be a created Transcription and the second argument must be null
		Transcription trans = 
			new Transcription(
//			new File("F:/research/data/MIDI/tab-int/4vv/rotta-1546_15-bramo_morir.mid"),
//			new File("F:/research/data/MIDI/tab-int/3vv/newsidler-1544_2-nun_volget.mid"),
//			new File("F:/research/data/MIDI/" + tabFile + ".mid"),
			new File("C:/Users/Reinier/Desktop/MEI/newsidler-1544_2-nun_volget-test.mid"),
//			new File("C:/Users/Reinier/Desktop/2019-ISMIR/test/mapped/3610_033_inter_natos_mulierum_morales_T-rev-mapped.mid"),
			null);
		
		Tablature tab = null; //new Tablature(new File("F:/research/data/encodings/" + tabFile + ".tbp"), false);
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
		
		boolean grandStaff = false;
		String s = path + "newsidler-1544_2-nun_volget-test";
		exportMEIFile(trans, tab, mismatchInds, grandStaff, s);
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
	 * be a multiple of 1/128.
	 *  
	 * @param r
	 * @param mul
	 * @return
	 */
	// TESTED
	static public List<Rational> getUnitFractions(Rational r, Rational mul) {
		List<Rational> uf = new ArrayList<Rational>();
		
		r.reduce();
		// r must be a multiple of 1/128
		if (r.mul(mul.getDenom()).getDenom() != 1) {
			throw new RuntimeException("ERROR: r must be a multiple of 1/128 but is " + 
				r.toString());
		}
		// If the numerator = 1: add to uf
		if (r.getNumer() == 1) {
			uf.add(r);
		}
		// If not: split into a fraction of num-1/den and 1/den
		else {
			// Add 1/den to uf
			uf.add(new Rational(1, r.getDenom()));
			// Call method on num-1/den
			uf.addAll(getUnitFractions(new Rational(r.getNumer()-1, r.getDenom()), mul));
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
	 * 
	 * @param trans Must be a Transcription created setting the encodingFile argument to null
	 *              (i.e., one that has basicNoteProperties).
	 * @param tab
	 * @param mismatchInds
	 * @param grandStaff
	 * @param path
	 */
	public static void exportMEIFile(Transcription trans, Tablature tab, 
		List<List<Integer>> mismatchInds, boolean grandStaff, String path) {
//-*-		System.out.println(">>> MEIExport.exportMEIFile() called");
		List<Object> data = getData(trans, tab);
		List<List<List<Integer[]>>> dataInt = (List<List<List<Integer[]>>>) data.get(0);
		List<List<List<String[]>>> dataStr = (List<List<List<String[]>>>) data.get(1);

		int numVoices = dataStr.get(0).size();
		List<Integer[]> mi = (tab == null) ? trans.getMeterInfo() : tab.getMeterInfo();
		List<Integer[]> ki = trans.getKeyInfo();

//		Runner.setPathsToCodeAndData(UI.getRootDir(), false); // TODO only necessary for MEITemplatePath
//		String res = ToolBox.readTextFile(new File(Runner.MEITemplatePath + "template.xml"));
		String res = ToolBox.readTextFile(new File(MEITemplatePath + "template.xml"));
		
		// Make meiHead
		String[] meiHead = new String[MEI_HEAD.size()];
		meiHead[MEI_HEAD.indexOf("title")] = trans.getPieceName();
		res = res.replace("title_placeholder", meiHead[MEI_HEAD.indexOf("title")]);

		// Make scoreDef. The scoreDef contains the initial meter (and key) sigs; any additional 
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
		List<Integer> meterChangeBars = ToolBox.getItemsAtIndex(mi, 2).subList(1,  mi.size());

		// Make staffGrp
		String staffGrpStr = "";
//		String[] clefs = new String[]{"G", "G", "F", "F"};
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
		res = res.replace("staffGrp_placeholder", staffGrpStr);

		// Organise the information (i) per voice, (ii) per bar, as the python beaming script 
		// takes it
		List<List<String>> unbeamedBarsPerVoice = new ArrayList<>();
		for (int j = 0; j < numVoices; j++) {
			unbeamedBarsPerVoice.add(new ArrayList<>());
		}
		// For each bar
		for (int i = 0; i < dataStr.size(); i++) {
//-*-			System.out.println("bar = " + (i+1));
			List<List<Integer[]>> currBarInt = dataInt.get(i);
			List<List<String[]>> currBarStr = dataStr.get(i);
			// For each voice
			for (int j = 0; j < currBarInt.size(); j++) {
//-*-				System.out.println("voice = " + j);
				List<Integer[]> currBarCurrVoiceInt = currBarInt.get(j);
				List<String[]> currBarCurrVoiceStr = currBarStr.get(j);
				// Add current bar to list coresponding to current voice
				List<String> barList = 
					getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, mi, mismatchInds, (i+1), j);
				String barListAsStr = "";
				for (int k = 0; k < barList.size(); k++) {
					barListAsStr += barList.get(k);
					if (k < barList.size()-1) {
						barListAsStr += "\r\n";
					}
				}
				unbeamedBarsPerVoice.get(j).add(barListAsStr);
			}
		}

//		System.out.println(unbeamedBarsPerVoice.get(0).size());
//		System.out.println(unbeamedBarsPerVoice.get(1).size());
//		System.out.println(unbeamedBarsPerVoice.get(2).size());
		for (int i = 0; i < unbeamedBarsPerVoice.get(1).size(); i++) {
//			System.out.println("bar = " + (i+1));
//-*-			System.out.println(unbeamedBarsPerVoice.get(1).get(i));
		}
//		System.exit(0);
		
		
		// For each bar
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dataStr.size(); i++) {
//-*-			System.out.println("bar = " + (i+1));
			List<List<Integer[]>> currBarInt = dataInt.get(i);
			List<List<String[]>> currBarStr = dataStr.get(i);
			String barline = "";
			if (meterChangeBars.contains(i+1)) {
				int ind = meterChangeBars.indexOf(i+1);
				System.out.println("ind = " + ind);
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
//-*-				System.out.println("voice = " + j);
				List<Integer[]> currBarCurrVoiceInt = currBarInt.get(j);
				List<String[]> currBarCurrVoiceStr = currBarStr.get(j);
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
				// For each note
//-*-				System.out.println("notes in bar " + (i+1) + ", voice " + j);
				List<String> barList = 
					getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, mi, mismatchInds, (i+1), j);
				String barListAsStr = "";
				for (int k = 0; k < barList.size(); k++) {
					barListAsStr += barList.get(k);
					if (k < barList.size()-1) {
						barListAsStr += "\r\n";
					}
				}
//				sb.append(getBar(currBarCurrVoiceInt, currBarCurrVoiceStr, mi, mismatchInds, (i+1), j));
				sb.append(barListAsStr + "\r\n");
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
		res = res.replace("music_placeholder", sb.toString());

		ToolBox.storeTextFile(res, 
			new File(path + "-" + (grandStaff ? "grand_staff" : "score") + ".xml"));

//		return res;
	}


	/**
	 * Returns the XML for the bar represented by the given List<String[]> and List<Integer[]>.
	 * 
	 * @param currBarCurrVoiceInt
	 * @param currBarCurrVoiceStr
	 * @param mi
	 * @param mismatchInds
	 * @return
	 */
	static List<String> getBar(List<Integer[]> currBarCurrVoiceInt, List<String[]> 
		currBarCurrVoiceStr, List<Integer[]> mi, List<List<Integer>> mismatchInds, 
		int argBar, int argVoice) {
		List<String> barList = new ArrayList<>();
		String barRestStr = 
			"<mRest " + "xml:id='" + argVoice + "."  + argBar + "." + "0.r.0'" + "/>";
		int bar = currBarCurrVoiceInt.get(0)[INTS.indexOf("bar")];
		Rational meter = Transcription.getMeter(bar, mi);
//		sbBar.append("meter='" + meter + "'" + "\r\n");
//		barList.add("meter='" + meter + "'" + "\r\n");
		
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
				List<List<String>> perBeat = new ArrayList<>();
				boolean chordActive = false;
				for (int k = 0; k < currBarCurrVoiceStr.size(); k++) {
					String[] note = currBarCurrVoiceStr.get(k);
					Integer[] noteInt = currBarCurrVoiceInt.get(k);
					Rational mp = new Rational(noteInt[INTS.indexOf("metPosNum")], 
						noteInt[INTS.indexOf("metPosDen")]);
					Rational durRat = new Rational(Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom(),
						noteInt[INTS.indexOf("dur")]);
					int dur = (int) durRat.toDouble();
					int currOnsNum = currBarCurrVoiceInt.get(k)[INTS.indexOf("onsetNum")];
					
//					// Close previous/open next beaming group if metPos is on the quarter note grid
//					if (mp.toDouble() == 0.25 || mp.toDouble() == 0.5 || mp.toDouble() == 0.75) { 
//						// Only if the note at index k is not the last note in the bar
//						sb.append(indent + tab.repeat(3) + "</beam>" + "\r\n");
//						sb.append(indent + tab.repeat(3) + "<beam>" + "\r\n");
//					}
					String noteStr = "";
					if (note[STRINGS.indexOf("pname")] == null) {
						noteStr += "<rest ";
					}
					else {
						// Check for in-voice chords
						if (k < currBarCurrVoiceStr.size()-1) {
							if (currOnsNum == currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")]) {
								barList.add("<chord>");
								chordActive = true;
							}
						}
						noteStr += "<note ";
					}
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
						if (inc.contains(tabInd)) {
							noteStr += "color='red'" + " ";
						}
						else if (over.contains(tabInd)) {
							noteStr += "color='orange'" + " ";
						}
						else if (sup.contains(tabInd)) {
							noteStr += "color='lime'" + " ";
						}
						else if (half.contains(tabInd)) {
							noteStr += "color='blue'" + " ";
						}
					}
					noteStr = noteStr.trim() + "/>";
					barList.add(noteStr);
					// If there is an active in-voice chord: check if it must be closed
					if (chordActive) {
						if ((k < currBarCurrVoiceStr.size()-1 &&
							currBarCurrVoiceInt.get(k+1)[INTS.indexOf("onsetNum")] > currOnsNum) 
							|| k == currBarCurrVoiceStr.size()-1) {
							barList.add("</chord>");
							chordActive = false;
						}
					}
				}
			}
		}
		return barList;
	}


	/**
	 * Extracts from the given Transcription the data needed to create an MEI file.
	 * 
	 * @param trans
	 * @param tab
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> getData(Transcription trans, Tablature tab) {
//-*-		System.out.println(">>> MEIExport.getData() called");
		Piece p = trans.getPiece();
		int numVoices = p.getScore().size();
		Rational gridVal = new Rational(1, 64);
		Integer[][] bnp = trans.getBasicNoteProperties();

		// Get meterInfo and keyInfo TODO assumed is a single key
		List<Integer[]> mi = (tab == null) ? trans.getMeterInfo() : tab.getMeterInfo();
		int numBars = mi.get(mi.size()-1)[3];
		Rational endOffset = Rational.ZERO;
		for (Integer[] l : mi) {
			Rational currMeter = new Rational(l[0], l[1]);
			int barsInCurrMeter = (l[3] - l[2]) + 1;
			endOffset = endOffset.add(currMeter.mul(barsInCurrMeter));
		}
		List<Integer[]> ki = trans.getKeyInfo();
		Integer[] key = ki.get(0);
		int numAlt = key[0];

		List<Integer> transToTabInd = new ArrayList<>();
		if (tab != null) {
			// tabToTransInd contains, for each tab index, the corresponding trans index 
			// (or, in the case of a SNU, indices)
			List<List<Integer>> tabToTransInd = new ArrayList<>();
			int bnpInd = 0;
			Integer[][] btp = tab.getBasicTabSymbolProperties();
			for (int i = 0; i < btp.length; i++) {
				List<Integer> currIndInTrans = new ArrayList<>();
				currIndInTrans.add(bnpInd);
				int currPitch = btp[i][Tablature.PITCH];
				Rational currOnset = 
					new Rational(btp[i][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
				if (i+1 != btp.length) {
//					Rational nextOnset = 
//						new Rational(btp[i+1][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
					// If not lower unison note (i.e., next tab note has the same pitch and 
					// onset) check for any SNU notes 
					Rational nextOnset = 
						new Rational(btp[i+1][Tablature.ONSET_TIME], Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
					int nextPitch = btp[i+1][Tablature.PITCH];
//					if ((nextOnset.equals(currOnset) && nextPitch == currPitch)) {
//						System.out.println(i);
//						System.out.println(currOnset);
//						System.out.println(currPitch);
//						System.exit(0);
//					}
					if (!(nextOnset.equals(currOnset) && nextPitch == currPitch)) {
//-*-						System.out.println("grr " + i);
						// If the next MIDI note has the same pitch and onset: SNU
						Rational nextOnsetMIDI = 
							new Rational(bnp[bnpInd+1][Transcription.ONSET_TIME_NUMER], 
							bnp[bnpInd+1][Transcription.ONSET_TIME_DENOM]);

						if (nextOnsetMIDI.equals(currOnset)) { // && btp[i+1][Tablature.PITCH] != currPitch) {
							for (int j = bnpInd+1; j < bnp.length; j++) {
								// If the next note in bnp has the same pitch and onset: SNU
								if (bnp[j][Transcription.PITCH] == currPitch && 
									new Rational(bnp[j][Transcription.ONSET_TIME_NUMER],
										bnp[j][Transcription.ONSET_TIME_DENOM]).equals(currOnset)) {
									currIndInTrans.add(j);
									bnpInd++;
								}
								else {
									break;
								}
							}
						}
					}
				}
				tabToTransInd.add(currIndInTrans);
				bnpInd++;
			}

			// transToTabInd contains, for each trans index, the corresponding tab index
			transToTabInd = new ArrayList<>();
			for (int i = 0; i <  tabToTransInd.size(); i++) {
				List<Integer> transInd = tabToTransInd.get(i); 
				for (int ind : transInd) {
					transToTabInd.add(ind, i);
				}
			}
//-*-			System.out.println("tabToTransInd");
//-*-			for (int j = 0; j < tabToTransInd.size(); j++) {
//-*-				System.out.println(j + " " + tabToTransInd.get(j));
//-*-			}
//-*-			System.out.println("transToTabInd");
//-*-			for (int j = 0; j < transToTabInd.size(); j++) {
//-*-				System.out.println(j + " " + transToTabInd.get(j));
//-*-			}
		}

		// Set initial bar and meter
		Integer[] initMi = mi.get(0);
		Rational meter = new Rational(initMi[0], initMi[1]);
		Rational barEnd = meter;

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
			if (tab != null) {
//-*-				System.out.println(bnp.length);
				iTab = transToTabInd.get(i);
			}
//-*-			System.out.println("note at ind = " + i + " (indTab = " + iTab + ")");
			String[] curr = new String[STRINGS.size()];
			int voice = DataConverter.convertIntoListOfVoices(trans.getVoiceLabels().get(i)).get(0);
			Rational onset = new Rational(bnp[i][Transcription.ONSET_TIME_NUMER], 
				bnp[i][Transcription.ONSET_TIME_DENOM]); 
			Rational[] barMetPos = Tablature.getMetricPosition(onset, mi);
			int bar = barMetPos[0].getNumer();
			Rational metPos = barMetPos[1];
			
			// Increment barEnd and clear lists when new bar is reached
			if (onset.isGreaterOrEqual(barEnd)) {
//				barEnd = barEnd.add(Transcription.getMeter(bar, mi));
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
			Rational offset = onset.add(durRounded);
			int keyInd = numAlt + 7;
			Integer[] currMpcg = mpcg[keyInd];
			String[] currAg = ag[keyInd];
			String[] currPcg = pcg[keyInd];
//-*-			System.out.println("voice                    " + voice);
//-*-			System.out.println("bar                      " + bar);
//-*-			System.out.println("pitch                    " + pitch);
//-*-			System.out.println("midiPitchClass           " + midiPitchClass);
//-*-			System.out.println("onset                    " + onset);
//-*-			System.out.println("offset                   " + offset);
//-*-			System.out.println("metPos                   " + metPos);
//-*-			System.out.println("durRounded               " + durRounded);
//-*-			System.out.println("barEnd                   " + barEnd);
//-*-			System.out.println("currMpcg                 " + Arrays.asList(currMpcg));
//-*-			System.out.println("currAg                   " + Arrays.asList(currAg));
//-*-			System.out.println("currPcg                  " + Arrays.asList(currPcg));

			// Check for preceding rests
			List<String[]> pitchOctAccTie = new ArrayList<String[]>();
			List<Integer[]> indBarOnsMpDurDots = new ArrayList<Integer[]>();
			List<String[]> currVoiceStrings = noteAttribPerVoiceStrings.get(voice);
			List<Integer[]> currVoiceInts = noteAttribPerVoiceInts.get(voice);
			Rational durPrev, metPosPrev, offsetPrev; 
			// First note in voice?
			if (currVoiceStrings.size() == 0) {
				durPrev = Rational.ZERO;
				metPosPrev = Rational.ZERO;
				offsetPrev = Rational.ZERO;
			}
			else {
				// prevNote is the last item in currVoiceInts whose onset is less than onset
				Integer[] prevNote = null;
				Rational onsetPrev = null;
//				Integer[] prevNote = currVoiceInts.get(currVoiceInts.size()-1);
//				Rational onsetPrev = new Rational(prevNote[INTS.indexOf("onsetNum")], 
//					prevNote[INTS.indexOf("onsetDen")]);
				for (int j = currVoiceInts.size()-1; j >= 0; j--) {
					prevNote = currVoiceInts.get(j);
					onsetPrev = new Rational(prevNote[INTS.indexOf("onsetNum")], 
						prevNote[INTS.indexOf("onsetDen")]);
					// Skip rests (onset is -1/-1)
					if (onsetPrev.getNumer() != -1 && onsetPrev.isLess(onset)) {
						break;
					}
				}
				int dotsPrev = prevNote[INTS.indexOf("dots")];
				durPrev = new Rational(1, prevNote[INTS.indexOf("dur")]);
				// The number of dots n lengthens a note by l its value, where l = ((2^n)-1)/2^n
				// (see https://en.wikipedia.org/wiki/Note_value)
				Rational l = new Rational((int)Math.pow(2, dotsPrev) - 1, (int)Math.pow(2, dotsPrev));
				durPrev = durPrev.add(durPrev.mul(l));
				metPosPrev = Tablature.getMetricPosition(onsetPrev, mi)[1]; 
				offsetPrev = onsetPrev.add(durPrev);
			}
			// Rests
			Rational durRest = onset.sub(offsetPrev);
			if (durRest.isGreater(Rational.ZERO)) {
				Rational precedingInBar = metPos;
				// Single-bar rest in the same bar
				if (durRest.isLessOrEqual(precedingInBar)) {
//-*-					System.out.println("GEVAL: single-bar rest");
					Rational metPosRest = null;
					// If the bar starts with a rest
					if (durRest.equals(precedingInBar)) {
						metPosRest = Rational.ZERO;
					}
					else {
						metPosRest = 
//						(currVoiceStrings.size() == 0) ? Rational.ZERO : onset.sub(offsetPrev);
						(currVoiceStrings.size() == 0) ? Rational.ZERO : metPosPrev.add(durPrev);
					}
					List<Object> noteData = 
						getNoteData(i, iTab, curr, getUnitFractions(durRest, gridVal), bar,
						null, metPosRest, mi);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Single-bar rest in the previous bar (onset is 0/x)
				else if (precedingInBar.equals(Rational.ZERO) && 
					durRest.isLessOrEqual(Transcription.getMeter(bar-1, mi))) {
//-*-					System.out.println("GEVAL: single-bar rest in previous bar");
					Rational metPosRest = 
						(currVoiceStrings.size() == 0) ? Rational.ZERO : metPosPrev.add(durPrev);
					List<Object> noteData = 
						getNoteData(i, iTab, curr, getUnitFractions(durRest, gridVal), bar-1,
						null, metPosRest, mi);
					pitchOctAccTie.addAll(0, (List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll(0, (List<Integer[]>) noteData.get(1));
				}
				// Multi-bar rest
				else {
//-*-					System.out.println("GEVAL: multi-bar rest");
					// Check how many bars the note spans
					List<Rational> subNoteDurs = new ArrayList<>();
					if (!precedingInBar.equals(Rational.ZERO)) {
						subNoteDurs.add(precedingInBar);
					}
					Rational remainder = durRest.sub(precedingInBar);
					int beginBar = Tablature.getMetricPosition(offsetPrev, mi)[0].getNumer();
					List<Integer> bars = 
						IntStream.rangeClosed(beginBar, bar).boxed().collect(Collectors.toList());
					for (int j = bar-1; j >= beginBar; j--) {
						Rational currBarLen = Transcription.getMeter(j, mi);
						if (remainder.isGreaterOrEqual(currBarLen)) {
							subNoteDurs.add(currBarLen);
							remainder = remainder.sub(currBarLen);
						}
						else {
							subNoteDurs.add(remainder);
						}
					}
					Collections.reverse(subNoteDurs);

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
						List<Object> noteData = 
							getNoteData(i, iTab, curr, getUnitFractions(currSubNoteDur, gridVal), 
							bars.get(j), null, currMetPosRest, mi);
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
//-*-					System.out.println("is natural");
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
//-*-					System.out.println("is accidental");
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
//-*-						System.out.println("is sharp");
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
//-*-						System.out.println("is flat");
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
//-*-						System.out.println("dit dus");
					}
				}
			}
			pname = String.valueOf(currPcg[gridRowInd]);
//-*-			System.out.println("pname                    " + pname);
//-*-			System.out.println("accid                    " + accid);
//-*-			System.out.println("oct                      " + oct);
			curr[STRINGS.indexOf("pname")] = "pname='" + pname + "'"; 
			curr[STRINGS.indexOf("oct")] = "oct='" + oct + "'";
			if (!accid.equals("")) {
				curr[STRINGS.indexOf("accid")] = "accid='" + accid + "'";
			}

			// 2. Set tie, dur, dots
			Rational remainingInBar = barEnd.sub(onset);
			// Single-bar note
			if (durRounded.isLessOrEqual(remainingInBar)) {
//				System.out.println("GEVAL: single-bar note");
				List<Object> noteData = 
					getNoteData(i, iTab, curr, getUnitFractions(durRounded, gridVal), bar,
					onset, metPos, mi);
				pitchOctAccTie.addAll((List<String[]>) noteData.get(0));
				indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
			}	
			// Multi-bar note
			else {
//				System.out.println("GEVAL: multi-bar note");
				// Check how many bars the note spans
				List<Rational> subNoteDurs = new ArrayList<>();
				subNoteDurs.add(remainingInBar);
				Rational remainder = durRounded.sub(remainingInBar);
				int endBar = (offset.equals(endOffset)) ? mi.get(mi.size()-1)[3] : 
					Tablature.getMetricPosition(offset, mi)[0].getNumer();
				
				List<Integer> bars = 
					IntStream.rangeClosed(bar, endBar).boxed().collect(Collectors.toList());
				for (int j = bar+1; j <= endBar; j++) {
					Rational currBarLen = Transcription.getMeter(j, mi);
					if (remainder.isGreaterOrEqual(currBarLen)) {
						subNoteDurs.add(currBarLen);
						remainder = remainder.sub(currBarLen);
					}
					else {
						subNoteDurs.add(remainder);
					}
				}
				// For each subnote
				Rational currOnset = onset;
				Rational currMetPos = metPos;
				for (int j = 0; j < subNoteDurs.size(); j++) {
					Rational currSubNoteDur = subNoteDurs.get(j);
					List<Object> noteData = 
						getNoteData(i, iTab, curr, getUnitFractions(currSubNoteDur, gridVal), 
						bars.get(j), currOnset, currMetPos, mi);
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
			if ((nv.get(nv.size()-1).getMetricTime().equals(onset))) {
				// Add rest to fill up current bar (if applicable)
				Rational restCurrentBar = barEnd.sub(offset);
				if (restCurrentBar.isGreater(Rational.ZERO)) {
					Rational metPosRestCurrentBar = metPos.add(durRounded);
					List<Object> noteData = 
						getNoteData(-1, -1, new String[STRINGS.size()], 
						getUnitFractions(restCurrentBar, gridVal), bar, null, 
						metPosRestCurrentBar, mi);
					pitchOctAccTie.addAll((List<String[]>) noteData.get(0));
					indBarOnsMpDurDots.addAll((List<Integer[]>) noteData.get(1));
				}
				// Add bar rests for all remaining bars
				for (int b = bar + 1; b <= numBars; b++) {
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
			for (int j = 0; j < pitchOctAccTie.size(); j++) {
				String[] str = pitchOctAccTie.get(j);
				Integer[] ints = indBarOnsMpDurDots.get(j);
				if (str[STRINGS.indexOf("pname")] == null) {
//-*-					System.out.println("dur (preceding rest)     " + ints[INTS.indexOf("dur")]);
//-*-					System.out.println("dots (preceding rest)    " + ints[INTS.indexOf("dots")]);
				}
				else {
					String tie = str[STRINGS.indexOf("tie")];
					if (tie != null) {
//-*-						System.out.println("tie                      " + tie);
					}
//-*-					System.out.println("dur                      " + ints[INTS.indexOf("dur")]);
//-*-					System.out.println("dots                     " + ints[INTS.indexOf("dots")]);
				}
			}
			noteAttribPerVoiceStrings.get(voice).addAll(pitchOctAccTie);
//			System.out.println("iTab = " + iTab + "; added is "  ); 
//			for (Integer[] in : indBarOnsMpDurDots) {
//				System.out.println(Arrays.toString(in));
//			}
//			System.out.println("-------------------");
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
//				String metPos = (currInt[INTS.indexOf("metPosNum")] == 0) ? "0" :
//					currInt[INTS.indexOf("metPosNum")] + "/" + currInt[INTS.indexOf("metPosDen")];
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
//-*-		for (int i = 0; i < voiceStr.size(); i++) {
//-*-			System.out.println(Arrays.toString(voiceInt.get(i)) + " -- " + Arrays.toString(voiceStr.get(i)));
//-*-		}

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
				
//				if (i == 12 && j == 0) {
//					for (String[] s : notesForCurrVoiceStr) {
//						System.out.println(Arrays.toString(s));
//					}
//					System.out.println("---------------");
//					for (Integer[] s : notesForCurrVoiceInt) {
//						System.out.println(Arrays.toString(s));
//					}
//					System.out.println(". . . . . . . . . ");
//				}
				
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

//				if (i == 12 && j == 0) {
//					for (String[] s : sortedStr) {
//						System.out.println(Arrays.toString(s));
//					}
//					System.out.println("------------------------");
//					for (Integer[] s : sortedInt) {
//						System.out.println(Arrays.toString(s));
//					}
//					System.exit(0);
//				}
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
	 *               duration, dots attributes (or only bar, dur, and dots in case of a rest)
	 *               
	 * In case of a simple (non-dotted, non-compound) or dotted note, the lists returned have
	 * only one element; in case of a non-dotted compound note (e.g., a half tied to an eighth)
	 * the lists have more than one element.
	 *               
	 * @param i
	 * @param iTab
	 * @param curr
	 * @param uf
	 * @param bar
	 * @param onset Only used in the note case
	 * @param metPos
	 * @param mi
	 * @return
	 */
	static List<Object> getNoteData(int i, int iTab, String[] curr, List<Rational> uf, int bar, 
		Rational onset, Rational metPos, List<Integer[]> mi) {
		List<String[]> currPitchOctAccTie = new ArrayList<>();
		List<Integer[]> currIndBarOnsMpDurDots = new ArrayList<>();

		int numDots = getNumDots(uf);
		boolean isRest = curr[STRINGS.indexOf("pname")] == null;
		boolean isSimple = (uf.size() == 1 && numDots == 0);
		boolean isDotted = numDots > 0;
		boolean isNonDottedCompound = (uf.size() > 1 && numDots == 0);
		// Simple or dotted: complete curr with dur and any dots; add to lists
		if (isSimple || isDotted) {
			String[] copyOfCurr = Arrays.copyOf(curr, curr.length);
			Rational durAsRat = uf.get(0);
			if (isDotted) {
				durAsRat = durAsRat.add(uf.get(1));
			}
			int dur = uf.get(0).getDenom();
			copyOfCurr[STRINGS.indexOf("dur")] = "dur='" + dur + "'";
//			copyOfCurr[STRINGS.indexOf("metPos")] = metPos.toString(); //"metPos='" + metPos.toString() + "'";
			if (numDots != 0) {
				copyOfCurr[STRINGS.indexOf("dots")] = "dots='" + numDots + "'";
			}
			currPitchOctAccTie.add(copyOfCurr);
			Integer[] in = new Integer[INTS.size()];
			Arrays.fill(in, -1);
			in[INTS.indexOf("bar")] = bar;
			in[INTS.indexOf("metPosNum")] = metPos.getNumer();
			in[INTS.indexOf("metPosDen")] = metPos.getDenom();
			in[INTS.indexOf("dur")] = dur;
			in[INTS.indexOf("dots")] = numDots;
			if (!isRest) {
				in[INTS.indexOf("ind")] = i;
				in[INTS.indexOf("indTab")] = iTab;				
				in[INTS.indexOf("onsetNum")] = onset.getNumer();
				in[INTS.indexOf("onsetDen")] = onset.getDenom();
			}
			currIndBarOnsMpDurDots.add(in);
		}
		// Non-dotted compound: complete curr with dur and tie for each uf; add to lists
		else if (isNonDottedCompound) {
			Rational currOnset = onset;
			Rational currMetPos = metPos;
			for (int k = 0; k < uf.size(); k++) {
				String[] copyOfCurr = Arrays.copyOf(curr, curr.length);
				Rational durAsRat = uf.get(k);
				int dur = durAsRat.getDenom();
				copyOfCurr[STRINGS.indexOf("dur")] = "dur='" + dur + "'";
//				copyOfCurr[STRINGS.indexOf("metPos")] = metPos.toString(); //"metPos='" + currMetPos.toString() + "'";
				if (!isRest) {
					String tie = (k == 0) ? "i" : ((k > 0 && k < uf.size()-1) ? "m" : "t");
					copyOfCurr[STRINGS.indexOf("tie")] = "tie='" + tie + "'";
				}
				currPitchOctAccTie.add(copyOfCurr);
				Integer[] in = new Integer[INTS.size()];
				Arrays.fill(in, -1);
				in[INTS.indexOf("bar")] = bar;
				in[INTS.indexOf("metPosNum")] = currMetPos.getNumer();
				in[INTS.indexOf("metPosDen")] = currMetPos.getDenom();
				in[INTS.indexOf("dur")] = dur;
				in[INTS.indexOf("dots")] = numDots;
				if (!isRest) {
					in[INTS.indexOf("ind")] = i;
					in[INTS.indexOf("indTab")] = iTab;
					in[INTS.indexOf("onsetNum")] = currOnset.getNumer();
					in[INTS.indexOf("onsetDen")] = currOnset.getDenom();
					currOnset = currOnset.add(durAsRat);
				}
				currIndBarOnsMpDurDots.add(in);
				currMetPos = currMetPos.add(durAsRat);
			}
		}
		return Arrays.asList(new Object[]{currPitchOctAccTie, currIndBarOnsMpDurDots});
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
