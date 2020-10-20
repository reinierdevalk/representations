package imports;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import representations.Encoding;
import representations.Encoding.Tuning;
import tbp.ConstantMusicalSymbol;
import tbp.MensurationSign;
import tbp.RhythmSymbol;
import tbp.SymbolDictionary;
import tbp.TabSymbolSet;
import tools.ToolBox;

public class TabImport {

	private static final String BARLINE_EVENT = " ||||||";
	private static final String REPEAT_DOTS_EVENT = " --**--";
	private static final String EMPTY_SEGMENT = " ------";
	private static final String RHYTHM_DOT_EVENT = ".------";
	private static final String TUNING_VOCAB = "ABCDEFG";
	private static final Map<String, String> MENSURATION_SIGNS;
	static { MENSURATION_SIGNS = new LinkedHashMap<String, String>();
		MENSURATION_SIGNS.put("3/4", MensurationSign.three.getEncoding());
		MENSURATION_SIGNS.put("3/4", MensurationSign.O.getEncoding());
		MENSURATION_SIGNS.put("2/2", MensurationSign.crossedC.getEncoding());
		MENSURATION_SIGNS.put("4/4", MensurationSign.C.getEncoding());

		MENSURATION_SIGNS.put("3/2", MensurationSign.threeTwo.getEncoding());
		MENSURATION_SIGNS.put("4/2", MensurationSign.fourTwo.getEncoding());
		MENSURATION_SIGNS.put("6/2", MensurationSign.sixTwo.getEncoding());		
	}
	
	private static final String TAB_LETTERS = "abcdefghiklmnopq";
	private static final String COURSE_NUMBERS = "123456789"; 
	private static final Map<String, String> RHYTHM_SYMBOLS;
	static { RHYTHM_SYMBOLS = new LinkedHashMap<String, String>();
		RHYTHM_SYMBOLS.put(" ", ""); // TODO why this?
		RHYTHM_SYMBOLS.put("F", RhythmSymbol.coronaBrevis.getEncoding());
		RHYTHM_SYMBOLS.put("D", RhythmSymbol.longa.getEncoding());
		RHYTHM_SYMBOLS.put("W", RhythmSymbol.brevis.getEncoding());
		RHYTHM_SYMBOLS.put("H", RhythmSymbol.semibrevis.getEncoding());
		RHYTHM_SYMBOLS.put("Q", RhythmSymbol.minim.getEncoding());
		RHYTHM_SYMBOLS.put("E", RhythmSymbol.semiminim.getEncoding());
		RHYTHM_SYMBOLS.put("S", RhythmSymbol.fusa.getEncoding());
		RHYTHM_SYMBOLS.put("T", RhythmSymbol.semifusa.getEncoding());
		RHYTHM_SYMBOLS.put("W.", RhythmSymbol.brevisDotted.getEncoding());
		RHYTHM_SYMBOLS.put("H.", RhythmSymbol.semibrevisDotted.getEncoding());
		RHYTHM_SYMBOLS.put("Q.", RhythmSymbol.minimDotted.getEncoding());
		RHYTHM_SYMBOLS.put("E.", RhythmSymbol.semiminimDotted.getEncoding());
		RHYTHM_SYMBOLS.put("S.", RhythmSymbol.fusaDotted.getEncoding());
		RHYTHM_SYMBOLS.put("3", RhythmSymbol.tripletIndicator);
	}


	public static void main(String[] args) {
		// To convert an ASCII tab or TabCode file into a tab+ file		
		List<String> pieces = Arrays.asList(new String[]{
//			"ah_golden_hairs-NEW",	
//			"an_aged_dame-II",
//			"as_caesar_wept-II",
//			"blame_i_confess-II",
////			"delight_is_dead-II",
//			"in_angels_weed-II",
//			"in_tower_most_high",
//			"o_lord_bow_down-II",
//			"o_that_we_woeful_wretches-NEW",
//			"quis_me_statim-II",
//			"rejoyce_unto_the_lord-NEW",
//			"sith_death-NEW",
//			"the_lord_is_only_my_support-NEW",
//			"the_man_is_blest-NEW",
//			"while_phoebus-II"

//			"1132_13_o_sio_potessi_donna_berchem_solo"	
				
//			"3610_033_inter_natos_mulierum_morales_T-rev"
//			"3618_041_benedictus_from_missa_de_l_homme_arme_morales_T"
				
			// Mass sections
//			"3584_001_pleni_missa_hercules_josquin",
//			"3585_002_benedictus_de_missa_pange_lingua_josquin",
//			"3643_066_credo_de_beata_virgine_jospuin_T-1",
//			"3643_066_credo_de_beata_virgine_jospuin_T-2",
//			"4471_40_cum_sancto_spiritu",
//			"5106_10_misa_de_faysan_regres_2_gloria",
//			"5107_11_misa_de_faysan_regres_pleni",
//			"5188_15_sanctus_and_hosanna_from_missa_hercules-1",
//			"5188_15_sanctus_and_hosanna_from_missa_hercules-2"	
//			"5189_16_sanctus_and_hosanna_from_missa_faisant_regrets-1",
//			"5189_16_sanctus_and_hosanna_from_missa_faisant_regrets-2",
//			"5190_17_cum_spiritu_sanctu_from_missa_sine_nomine",
//			"5266_15_cum_sancto_spiritu_desprez"
	
			// Motets
//			"5265_14_absalon_fili_me_desprez",
//			"3647_070_benedicta_est_coelorum_josquin_T",
//			"4964_01a_benedictum_es_coelorum_josquin"
//			"4965_01b_per_illud_ave_josquin",
//			"4966_01c_nunc_mater_josquin",
//trrr			"5254_03_benedicta_es_coelorum_desprez-1",
//			"5254_03_benedicta_es_coelorum_desprez-2",
//			"5254_03_benedicta_es_coelorum_desprez-3",
//			"5702_benedicta-1",
//			"5702_benedicta-2",
//			"5702_benedicta-3",
//			"3591_008_fecit_potentiam_josquin",	
//			"5263_12_in_exitu_israel_de_egipto_desprez-1",
//			"5263_12_in_exitu_israel_de_egipto_desprez-2",
//trrr			"5263_12_in_exitu_israel_de_egipto_desprez-3",
//			"5256_05_inviolata_integra_desprez-1",
//trrr			"5256_05_inviolata_integra_desprez-2",
//			"5256_05_inviolata_integra_desprez-3",
//			"4465_33-34_memor_esto-1", 
//trrr			"4465_33-34_memor_esto-2",
//			"932_milano_108_pater_noster_josquin-1",
//			"932_milano_108_pater_noster_josquin-2"
//			"5252_01_pater_noster_desprez-1",
//			"5252_01_pater_noster_desprez-2",
//			"3649_072_praeter_rerum_seriem_josquin_T",
//			"5253_02_praeter_rerum_seriem_desprez-1",
//			"5253_02_praeter_rerum_seriem_desprez-2",
//			"5694_03_motet_praeter_rerum_seriem_josquin-1",
//			"5694_03_motet_praeter_rerum_seriem_josquin-2",
//			"1274_12_qui_habitat_in_adjutorio-1",
//			"1274_12_qui_habitat_in_adjutorio-2"
//			"5264_13_qui_habitat_in_adjutorio_desprez-1",
//			"5264_13_qui_habitat_in_adjutorio_desprez-2",
//			"933_milano_109_stabat_mater_dolorosa_josquin",
//			"5255_04_stabat_mater_dolorosa_desprez-1",
//trrr			"5255_04_stabat_mater_dolorosa_desprez-2",
		
			// Chansons
			"4400_45_ach_unfall_was",
//			"4481_49_ach_unfal_wes_zeigst_du_mich",
//			"4406_51_adieu_mes_amours",
//			"4467_37_adieu_mes_amours",
//			"1025_adieu_mes_amours",
//			"1030_coment_peult_avoir_joye",
//			"1275_13_faulte_d_argent",
//			"3638_061_lauda_sion_gombert_T",
//			"5148_51_respice_in_me_deus._F#_lute_T",
//			"5260_09_date_siceram_morentibus_sermisy",
//			"4438_07_la_plus_des_plus",
//			"4443_12_la_bernardina",
//			"1033_la_bernadina_solo_orig",
//			"5191_18_mille_regres",
//			"4482_50_mille_regrets_P",
//			"4469_39_plus_nulz_regrets_P",	
//			"922_milano_098_que_voulez_vous_dire_de_moi"

//			"3610_033_inter_natos_mulierum_morales_T-rev"	
		});
				
		String tbp;
//		String path = "C:/Users/Reinier/Desktop/tab_reconstr-hector/tab/";
//		String path = "C:/Users/Reinier/Desktop/Byrd-Scores-notes-Aug19/preproc/tab/";
		String path = "C:/Users/Reinier/Desktop/tours/";
//		path = "C:/Users/Reinier/Desktop/2019-ISMIR/test/tab/";
		path = "F:/research/publications/conferences-workshops/2019-ISMIR/paper/josquintab/tab/";
		path = "F:/research/data/data/josquintab/tab/";
		
		// From ASCII
//		for (String s : pieces) {
//			tbp = ascii2Tbp(new File(path + s + ".tab"));
//			ToolBox.storeTextFile(tbp, new File(path + s + ".tbp"));
//		}
		
		// From TabCode
		for (String s : pieces) {
			tbp = tc2tbp(new File(path + s + ".tc"));
			ToolBox.storeTextFile(tbp, new File(path + s + ".tbp"));
		}
		
	}


	/**
	 * Creates a tab+ encoding from the given TabCode file.
	 * 
	 * @param tabcode
	 * @param metadata
	 * @param courses
	 * @return
	 */
	public static String tc2tbp(File tabcode) {
		String tc = ToolBox.readTextFile(tabcode).trim();

		Map<Integer, String> tunings = new LinkedHashMap<Integer, String>();
		tunings.put(65, "F");
		tunings.put(67, "G");
		tunings.put(69, "A");

		Map<String, String> notations = new LinkedHashMap<String, String>();
		notations.put("French", "FrenchTab");
		notations.put("Italian", "ItalianTab");		

		// Get rules. Rules looks like
		// {<rules>
		// 		<notation>French</notation>
		// 		<pitch>67</pitch>
		// 		<tuning-named>renaissance</tuning-named>
		// 		...
		// </rules>}
		String rules = tc.substring(tc.indexOf("<rules>") + "<rules>".length(), tc.indexOf("</rules>"));
		Map<String, String> rulesMap = new LinkedHashMap<String, String>();
		while (rules.contains("</")) {
			rulesMap.put(
				rules.substring(rules.indexOf("<") + 1, rules.indexOf(">")), 
				rules.substring(rules.indexOf(">") + 1, rules.indexOf("</")));
			rules = rules.substring(rules.indexOf(">", rules.indexOf("</")) + 1);
		}
		
		// Make encoding
		StringBuffer tbpEncoding = getEncoding(tc);
		
		// Make metadataString
		String tss = rulesMap.get("notation") != null ? 
			notations.get(rulesMap.get("notation")) : notations.get("French");
		String[] metadata = new String[]{
			rulesMap.get("author") != null ? rulesMap.get("author") : "", // author TODO
			rulesMap.get("title") != null ? rulesMap.get("title") : "", // title TODO
			rulesMap.get("source") != null ? rulesMap.get("source") : "", // source TODO
			tss, // TabSymbolSet  
			rulesMap.get("pitch") != null ? 
				tunings.get(Integer.parseInt(rulesMap.get("pitch"))) : tunings.get(67), // Tuning
			rulesMap.get("tuningSeventhCourse") != null ? 
				rulesMap.get("tuningSeventhCourse") : "", // TuningSeventhCourse TODO
			rulesMap.get("meterInfo") != null ? 
				rulesMap.get("meterInfo") : Encoding.createMeterInfoString(tbpEncoding.toString(), tss), // meterinfo
			rulesMap.get("durScale") != null ? 
				rulesMap.get("durScale") : "1" // diminution	
		};		
		StringBuffer metadataStr = new StringBuffer(Encoding.createMetadata(metadata));

		return metadataStr.append(tbpEncoding).toString();
	}


	private static StringBuffer getEncoding(String tc) {
		StringBuffer tbpEncoding = new StringBuffer("");
		
		Map<String, String> mensurationSigns = new LinkedHashMap<String, String>();
		mensurationSigns.put("C/", MensurationSign.crossedC.getEncoding());
		mensurationSigns.put("6:2", MensurationSign.sixTwo.getEncoding());
		mensurationSigns.put("3:2", MensurationSign.threeTwo.getEncoding());
		mensurationSigns.put("3:4", MensurationSign.three.getEncoding());
		mensurationSigns.put("2:4", MensurationSign.two.getEncoding());
		mensurationSigns.put("2:1", MensurationSign.twoOne.getEncoding());
		mensurationSigns.put("3:1", MensurationSign.threeOne.getEncoding());
		mensurationSigns.put("1:2", MensurationSign.oneTwo.getEncoding());
		
		String tcSysBreak = "{^}";
		String tcPageBreak = "{>}{^}";
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		
		// Remove all comments from the TabCode
		tc = tc.replace(tcSysBreak, "SysBr").replace(tcPageBreak, "PgBr");
		while (tc.contains("{")) {
			int open = tc.indexOf("{");
			tc = tc.replace(tc.substring(open, (tc.indexOf("}", open+1)+1)), "");
		}
		tc = tc.replace("SysBr", tcSysBreak).replace("PgBr", tcPageBreak);
		// Separate all tabwords by single line breaks only (in the TabCode they can be separated by 
		// spaces, tabs, or line breaks). See also 
		// https://stackoverflow.com/questions/22787000/how-to-remove-multiple-line-breaks-from-a-string
		tc = tc.trim().replace(" ", "\r\n").replace("\t", "\r\n").replaceAll("(\r?\n){2,}", "$1");

		int prevDur = 0; // TODO only used to add to durCurrRhythmGroup, which (in the end) is not used 
		int totalDur = 0;
		List<String> meters = new ArrayList<>();
		List<Integer> onsets = new ArrayList<>();
		String[] tabwords = tc.split("\r\n");
		// tripletActive is set to true when the first tabword of a triplet group is encountered,
		// and set to false again when the first barline following the triplet group is encountered
		// NB: triplets are assumed to be always followed by a barline TODO
		boolean tripletActive = false;
		int tripletLength = 0;
		for (int i = 0; i < tabwords.length; i++) {
			String tabword = tabwords[i];
			System.out.println("tabword = " + tabword);

			String asTbp = "";
			// A rhythmGroup is either a single RS or a group of beamed RS
			int durCurrRhythmGroup = 0; // TODO only used to add to totalDur, which is not used
			// Mensuration sign
			if (tabword.startsWith("M")) {
				asTbp += 
					mensurationSigns.get(tabword.substring(tabword.indexOf("(") + 1, 
					tabword.indexOf(")"))) + ss + ConstantMusicalSymbol.SPACE.getEncoding() + ss;
				meters.add(tabword);
//				onsets.add(totalDur);
				totalDur = 0;
			}
			// Constant musical symbol (barline etc.)
			else if (ConstantMusicalSymbol.getConstantMusicalSymbol(tabword) != null) {
				asTbp += tabword + ss + "\r\n";
//				// In case of a barline following a triplet group
//				if (ConstantMusicalSymbol.getConstantMusicalSymbol(tabword) != 
//					ConstantMusicalSymbol.SPACE && tripletActive) {
//					tripletActive = false;
//					System.out.println("UIT");
//				}
			}
			// System break
			else if (tabword.equals(tcSysBreak)) {
				if (!tbpEncoding.toString().endsWith("\r\n")) {
					asTbp += "\r\n";
				}
				asTbp += SymbolDictionary.SYSTEM_BREAK_INDICATOR + "\r\n";
			}
			// Tabword starting with RS
			else if (RHYTHM_SYMBOLS.containsKey(tabword.substring(0, 1))) {
//				System.out.println("starts with RS");
				String converted = convertTabword(tabword, false);
//				asTbp += converted;
//				int durFirst = durCurrRhythmGroup += RhythmSymbol.getRhythmSymbol(
//					converted.substring(0, converted.indexOf(SymbolDictionary.SYMBOL_SEPARATOR))).getDuration();
				String rs = converted.substring(0, converted.indexOf(ss));
				// In TabCode, only the first note of a triplet group is preceded by a 3, so in 
				// convertTabword() only that first note will be converted to a tbp triplet variant 
				if (rs.startsWith(RhythmSymbol.tripletIndicator)) {
					System.out.println("triplet AAN");
					tripletActive = true;
					String tripletUnitRs = 
						RhythmSymbol.tripletIndicator +
						RHYTHM_SYMBOLS.get(tabword.substring(tabword.indexOf("(")+1, 
						tabword.indexOf(")")));
					int durTripletUnit = RhythmSymbol.getRhythmSymbol(tripletUnitRs).getDuration();
					int dur = RhythmSymbol.getRhythmSymbol(rs).getDuration();
					System.out.println(durTripletUnit);
					System.out.println(rs);
					System.out.println(dur);
					System.out.println("full triplet length = " + (3 * durTripletUnit));
					tripletLength = (3 * durTripletUnit) - dur ;
					System.out.println("TL --> " + tripletLength);
					// Make triplet RS a tripletOpen RS
					converted = 
						RhythmSymbol.tripletIndicator + RhythmSymbol.tripletOpen +
						rs.substring(RhythmSymbol.tripletIndicator.length()) +	
						converted.substring(rs.length());
//					rs = rs.substring(RhythmSymbol.tripletIndicator.length(), rs.length());
				}
				// Use the triplet variant if the tabword is the second or higher tabword in a 
				// triplet group (in which case the rs will not start with the tripletIndicator)
				if (!rs.startsWith(RhythmSymbol.tripletIndicator) && tripletActive) {
					rs = RhythmSymbol.getTripletVariant(rs).getEncoding();
					tripletLength -= RhythmSymbol.getRhythmSymbol(rs).getDuration();
					System.out.println("TL --> " + tripletLength);
					// If last note of the triplet
					if (tripletLength == 0) {
						System.out.println("triplet UIT");
						tripletActive = false;
						// Make triplet RS a tripletClose RS
						rs = RhythmSymbol.tripletIndicator + RhythmSymbol.tripletClose +
							rs.substring(RhythmSymbol.tripletIndicator.length());
					}
					// If middle note of the triplet
					else {
						System.out.println("triplet MID");
					}
					converted = rs + ss + converted.substring(converted.indexOf(ss) + 1);
				}
				System.out.println("converted = " + converted);
				asTbp += converted;
				int durFirst = // TODO why add to durCurrRhythmGroup?
					durCurrRhythmGroup + RhythmSymbol.getRhythmSymbol(rs).getDuration();
				durCurrRhythmGroup += durFirst;
				prevDur = durFirst;
			}
			// Beamed tabword
			else if (tabword.startsWith("[")) {
				System.out.println("is beamed");
				String converted = convertTabword(tabword, false);
//				asTbp += converted;
				
				String rs = converted.substring(0, converted.indexOf(ss));
				System.out.println(rs);
				if (rs.startsWith(RhythmSymbol.tripletIndicator)) {
					System.out.println("triplet AAN (beamed)");
					tripletActive = true;
					String tripletUnitRs = 
						RhythmSymbol.tripletIndicator +	
						RHYTHM_SYMBOLS.get(tabword.substring(tabword.indexOf("(")+1, 
						tabword.indexOf(")")));
					int durTripletUnit = RhythmSymbol.getRhythmSymbol(tripletUnitRs).getDuration();
					int dur = RhythmSymbol.getRhythmSymbol(rs).getDuration();
					System.out.println("full triplet length = " + (3 * durTripletUnit));
					tripletLength = (3 * durTripletUnit) - dur ;
					System.out.println("TL --> " + tripletLength);
					// Make triplet RS a tripletOpen RS
					converted = 
						RhythmSymbol.tripletIndicator + RhythmSymbol.tripletOpen +
						rs.substring(RhythmSymbol.tripletIndicator.length()) +	
						converted.substring(rs.length());
				}
				if (!rs.startsWith(RhythmSymbol.tripletIndicator) && tripletActive) {
					rs = RhythmSymbol.getTripletVariant(rs).getEncoding();
					tripletLength -= RhythmSymbol.getRhythmSymbol(rs).getDuration();
					System.out.println("TL --> " + tripletLength);
					// If last note of the triplet
					if (tripletLength == 0) {
						System.out.println("triplet UIT (beamed)");
						tripletActive = false;
						// Make triplet RS a tripletClose RS
						rs = RhythmSymbol.tripletIndicator + RhythmSymbol.tripletClose +
							rs.substring(RhythmSymbol.tripletIndicator.length());
					}
					// If middle note of the triplet
					else {
						System.out.println("triplet MID (beamed)");
					}
					converted = rs + ss + converted.substring(converted.indexOf(ss) + 1);
				}
				System.out.println("converted = " + converted);
				asTbp += converted;
				int durFirst = RhythmSymbol.getRhythmSymbol(rs).getDuration();
				durCurrRhythmGroup += durFirst;
				prevDur = durFirst;

//				int indAfterRS = tabword.lastIndexOf("[")+1;
				String beamedRS = tabword.substring(0, (tabword.lastIndexOf("[") + 1));
				// List all tabwords up until (and including) closing beams
				for (int j = i+1; j < tabwords.length; j++) {
					String nextTabword = tabwords[j];

					// If system break in between
					if (nextTabword.startsWith(tcSysBreak)) {
						asTbp += "\r\n" + SymbolDictionary.SYSTEM_BREAK_INDICATOR + "\r\n";
					}
					// If last tabword in beaming group
					if (nextTabword.startsWith("]")) {
						String convertedNext = convertTabword(nextTabword, false);
						String rsNext = convertedNext.substring(0, convertedNext.indexOf(ss));
						// rsNext never starts with a tripletIndicator, but can be part of a 
						// triplet group
						if (tripletActive) {
							System.out.println("triplet LAST in non-final BG");
							rsNext = RhythmSymbol.getTripletVariant(rsNext).getEncoding();
//							System.out.println("rsNext = " + rsNext);
							tripletLength -= RhythmSymbol.getRhythmSymbol(rsNext).getDuration();
							System.out.println("TL --> " + tripletLength);
							if (tripletLength == 0) {
								tripletActive = false;
								// Make triplet RS a tripletClose RS
								rsNext = RhythmSymbol.tripletIndicator + RhythmSymbol.tripletClose +
									rsNext.substring(RhythmSymbol.tripletIndicator.length());
							}
							convertedNext = 
								rsNext + ss + convertedNext.substring(convertedNext.indexOf(ss) + 1);
//							System.out.println("nextTabword = " + nextTabword);
//							System.out.println("convertedNext = " + convertedNext);
						}
						System.out.println("convertedNext = " + convertedNext);
						asTbp += convertedNext;
						durCurrRhythmGroup += durFirst;
						i = j; 
						break;
					}
					// If tabword in middle of beaming group (which, in TabCode, has no RS)
					else {
						System.out.println("triplet (mid) in BG");
						String convertedNext = convertTabword(beamedRS + nextTabword, true);
						String rsNext = convertedNext.substring(0, convertedNext.indexOf(ss));
						// rsNext never starts with a tripletIndicator, but can be part of a
						// triplet group
						if (tripletActive) {
							rsNext = RhythmSymbol.getTripletVariant(rsNext).getEncoding();
							tripletLength -= RhythmSymbol.getRhythmSymbol(rsNext).getDuration();
							System.out.println("TL --> " + tripletLength);
//							System.out.println("rsNext = " + rsNext);
							if (tripletLength == 0) { // TODO remove: this never happens
								tripletActive = false;
								System.out.println("triplet UIT in BG");
							}
							convertedNext = 
								rsNext + ss + convertedNext.substring(convertedNext.indexOf(ss) + 1);
//							System.out.println("nextTabword = " + nextTabword);
//							System.out.println("convertedNext = " + convertedNext);
//							System.exit(0);
						}
						System.out.println("convertedNext = " + convertedNext);
						asTbp += convertedNext; 
						durCurrRhythmGroup += durFirst;
					}
				}
			}
			// Tabword without RS
			else {
				asTbp += convertTabword(tabword, false);
				durCurrRhythmGroup += prevDur;
			}
			tbpEncoding.append(asTbp);
			totalDur += durCurrRhythmGroup;
		}
		if (!tbpEncoding.toString().endsWith("\r\n")) {
			tbpEncoding.append("\r\n"); 
		}
		tbpEncoding.append(SymbolDictionary.END_BREAK_INDICATOR);
		
		return tbpEncoding;
	}


	/**
	 * Converts the given tabword into tab+ encoding.
	 * 
	 * @param tabword
	 * @param rsAdded Whether or not a rhythm symbol was added to the tabword as it is in the TabCode.
	 * @return
	 */
	private static String convertTabword(String tabword, boolean rsAdded) {
		String convertedTabWord = "";
		
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		int lenTripletUnit = "(Q)".length();

		Map<String, String> beams = new LinkedHashMap<String, String>();
		beams.put("[[", RhythmSymbol.beamedSemiminim.getEncoding());
		beams.put("[[[", RhythmSymbol.beamedFusa.getEncoding());
		beams.put("[[[[", RhythmSymbol.beamedSemifusa.getEncoding());
		beams.put("]]", RhythmSymbol.semiminim.getEncoding());
		beams.put("]]]", RhythmSymbol.fusa.getEncoding());
		beams.put("]]]]", RhythmSymbol.semifusa.getEncoding());
				
		// Separate RS and tabword; convert RS
		String rs = "";
		String convertedRS = "";
		String tabwordNoRS = "";
		// Regular RS
		if (RHYTHM_SYMBOLS.containsKey(tabword.substring(0, 1))) {
			// Take into account dotted RS and (dotted) triplet
			int indAfterRS = 1;
			boolean isTriplet = tabword.substring(0, 1).equals("3");
			boolean isDotted = tabword.contains(".");
//			if (tabword.length() > 1 && 
//				(tabword.substring(1, 2).equals(".") || tabword.substring(0, 1).equals("3"))) {
//				indAfterRS = 2;
//			}
			// Dotted, non-triplet
			if (tabword.length() > 1 && !isTriplet && isDotted) {
				indAfterRS = 2;
			}
			// Non-dotted, triplet
			if (tabword.length() > 1 && isTriplet && !isDotted) {
				indAfterRS = 2 + lenTripletUnit;
			}
			// Dotted, triplet
			if (tabword.length() > 1 && isTriplet && isDotted) {
				indAfterRS = 3 + lenTripletUnit;
			}
			System.out.println("indAfterRS = " + indAfterRS);
//			if (tabword.length() <= 2) {
//				tabwordNoRS = tabword.substring(indAfterRS); 
//			}
			rs = tabword.substring(0, indAfterRS);
			// Triplet
			if (isTriplet) {
//			if (rs.startsWith("3")) {
				// Non-dotted
				convertedRS = 
					RHYTHM_SYMBOLS.get(rs.substring(0, 1)) + 
					RHYTHM_SYMBOLS.get(rs.substring(1+lenTripletUnit, 2+lenTripletUnit));
				// If dotted: add rhythmDot
				if (isDotted) {
//				if (indAfterRS == 3) {
					convertedRS += RhythmSymbol.rhythmDot.getEncoding();
				}
				convertedRS += ss;
			}
			else {
				convertedRS = RHYTHM_SYMBOLS.get(rs) + ss;
			}
			tabwordNoRS = tabword.substring(indAfterRS);
		}
		// Beamed RS
		else if (tabword.startsWith("[") || tabword.startsWith("]")) {
			int indAfterRS = tabword.lastIndexOf("[") + 1;
			if (!tabword.contains("[")) {
				indAfterRS = tabword.lastIndexOf("]") + 1;
			}
			rs = tabword.substring(0, indAfterRS);
			convertedRS = beams.get(rs) + ss;
			tabwordNoRS = tabword.substring(indAfterRS);
		}
		// No RS
		else {
			tabwordNoRS = tabword;
		}

		// If tabword is not a rest only: convert tabword
		if (!tabwordNoRS.equals("")) {
			// Check for non-basic TabCode elements and remove them from tabwordNoRS
			String originalTabwordNoRS = tabwordNoRS;
			String clean = "";
			for (int i = 0; i < tabwordNoRS.length(); i++) {
				String currChar = tabwordNoRS.substring(i, i+1);
				// tabwordNoRS should consists of letter-number pairs exclusively
				if (TAB_LETTERS.contains(currChar) && COURSE_NUMBERS.contains(tabwordNoRS.substring(i+1, i+2))
					|| COURSE_NUMBERS.contains(currChar) && TAB_LETTERS.contains(tabwordNoRS.substring(i-1, i))) {
					clean += currChar;
				}
			}
			tabwordNoRS = clean;

			// Cut into fret-string combinations
			List<List<Integer>> fretString = new ArrayList<>();
			for (int i = 0; i < tabwordNoRS.length(); i++) {
				String currChar = tabwordNoRS.substring(i, i+1);
				if (COURSE_NUMBERS.contains(currChar)) {
					fretString.add(Arrays.asList(new Integer[]{
						TAB_LETTERS.indexOf(tabwordNoRS.substring(i-1, i)), // index of letter
						Integer.parseInt(currChar)})); // course
				}
			}
			// Sort and reverse so that courses are in descending order
			ToolBox.bubbleSort(fretString, 1);
			Collections.reverse(fretString);
			
			// Make convertedTabWord
			for (List<Integer> l : fretString) {
				convertedTabWord += 
					TAB_LETTERS.substring(l.get(0), l.get(0) + 1) + // letter
					String.valueOf(l.get(1)) + // course
					ss;
			}
			// Insert comment before last symbol separator in convertedTabword
			if (!tabwordNoRS.equals(originalTabwordNoRS)) {
				// Only prepend with originalRS if RS was part of the tabword in the TabCode
				if (!rsAdded) {
					originalTabwordNoRS = rs + originalTabwordNoRS;
				}
				convertedTabWord = convertedTabWord.substring(0, convertedTabWord.length()-1) + 
					"{@" + originalTabwordNoRS  + " in TabCode}" + ss;
			}
		}
		
		return convertedRS + convertedTabWord + ConstantMusicalSymbol.SPACE.getEncoding() + ss;
	}


	/**
	 * Creates a tab+ encoding from the given Sibelius ASCII file.
	 * 
	 * @param ascii
	 * @return
	 */
	public static String ascii2tbp(File ascii) {
		// Make encoding
		List<List<String>> systemContents = getSystemContents(getSystems(ascii));
		StringBuffer enc = getEncoding(systemContents);

		// Make metadataString
		String tss = systemContents.get(systemContents.size()-1).get(0);
		String tuning = systemContents.get(systemContents.size()-1).get(1);
		String[] metadata = new String[]{
			"", // author TODO
			"", // title TODO
			"", // source TODO
			tss, // TabSymbolSet  
			tuning, // Tuning
			"", // TuningSeventhCourse TODO
			Encoding.createMeterInfoString(enc.toString(), tss), // meterinfo 
			"" // diminution
		};

		StringBuffer metadataStr = new StringBuffer(Encoding.createMetadata(metadata));

		return metadataStr.append(enc).toString();
	}
	
	
	/**
	 * Returns the individual systems in the given file as a list of String[][].
	 * 
	 * @param f
	 * @return
	 */
	private static List<String[][]> getSystems(File f) {
		List<String[][]> systems = new ArrayList<String[][]>();
		
		String[] lines = ToolBox.readTextFile(f).split("\r\n");
		
		int numCourses = 0;
		for (String line : lines) {
			if (line.contains("|-") && !line.contains("Triplet")) {
				numCourses++;
			}
			else { 
				if (numCourses > 0) {
					break;
				}
			}
		} 

		int staffHeight = numCourses + 2; // +1 for line with rhythm signs; +1 for line with meter
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			// Assumes that each line contains |-, which for Sibelius-generated files is true 
			if (line.contains("|-") && !line.contains("Triplet")) {
				int lineLen = line.length();
				// Determine index of first char (tuning) (a system may be padded with one or more spaces)
				int firstCharInd = lineLen - line.trim().length();
				String[][] staff = new String[staffHeight][lineLen-firstCharInd];
				for (int j = 0; j < staffHeight; j++) {
					String currLine = lines[(i-2) + j]; // -1 for line with rhythm signs; -1 for line with meter
					// Complete meter/rhythm signs line with empty spaces
					if (j == 0 || j == 1) {
						int diff = lineLen - currLine.length();
						for (int k = 0; k < diff; k++) {
							currLine += " ";
						}
					}
					// Check if all staff lines have the same length
					if (j > 0 && currLine.length() != lineLen) {
						throw new RuntimeException("Error: Unequal staff line length (line " + ((i-1) + j) + ").");
					}
					// Add line to staff
					for (int k = firstCharInd; k < currLine.length(); k++) {
						staff[j][k-firstCharInd] = currLine.substring(k, k+1);
					}		
				}
				systems.add(staff);
				i += (numCourses - 1);
			}
		}

		return systems;
	}


	/**
	 * Retrieves the contents (vertical events) from each system. The last element in the list
	 * contains the tabsymbolset (element 0) and the tuning (element 1).
	 * 
	 * @param systems
	 * @return
	 */
	private static List<List<String>> getSystemContents(List<String[][]> systems) {
		List<List<String>> allChords = new ArrayList<List<String>>();

		String firstChord = null;
		for (String[][] system : systems) {
			// Slice into chords
			List<String> systemChords = new ArrayList<String>();			
			for (int i = 0; i < system[0].length; i++) {
				String currSlice = "";
				for (int j = 0; j < system.length; j++) {
					currSlice += system[j][i];
				}
				boolean isTuningSlice = true;
				for (int j = 2; j < currSlice.length(); j++) { // start at 2 to skip meter and RS lines 
					if (!TUNING_VOCAB.contains(currSlice.subSequence(j, j+1))) {
						isTuningSlice = false;
						break;
					}
				}

				// Check for meter
				String meter = "";
				if ("123456789".contains(currSlice.substring(0, 1)) && 
					!(system[0][i-1].equals("/") || system[0][i-2].equals("/"))) {	
					// Add two chars before and after i to meter
					for (int k = -2; k <= 2; k++) {
						meter += (system[0][i+k]).trim();
					}
					currSlice = meter;
				}
				
				// Remove meter line
				if (!currSlice.equals(meter)) {
					currSlice = currSlice.substring(1, currSlice.length());
				}
				
				// Add to systemChords (but not tuning, any slice with sharps after tuning, or empty segment)
				if (!isTuningSlice && !currSlice.contains("##") && !currSlice.equals(EMPTY_SEGMENT)) {
					if (firstChord == null && !currSlice.equals(BARLINE_EVENT)) {
						firstChord = currSlice;
					}
					systemChords.add(currSlice);
				}
			}
			allChords.add(systemChords);
		}

		// Determine tuning and tss
		List<String> tuningStringIndiv = new ArrayList<>();
		for (String[] s : systems.get(0)) {		
			String indiv = s[0];
			// Skip meter and RS lines
			if (!indiv.equals(" ")) {
				if (s[1].equals("#")) {
					indiv += s[1];
				}
				tuningStringIndiv.add(indiv);
			}
		}
		String tuningString = "";
		String tuningStringRev = "";
		for (int i = 0; i < tuningStringIndiv.size(); i++) {
			tuningString += tuningStringIndiv.get(i);
			tuningStringRev += tuningStringIndiv.get((tuningStringIndiv.size()-1)-i);
		}
		
		String tuning = null;
		String tss = null;
		for (Tuning t : Encoding.Tuning.values()) {	
			// currTuning lists the tuning for the courses in the sequence (6)-(1)
			String currTuning = String.join("", t.getCourseString());
			
			// It tuningString equals currTuning
			if (currTuning.equals(tuningString)) {
				tuning = t.getName();
				tss = TabSymbolSet.ITALIAN_TAB.toString();
				break;
			}
			// If tuningString equals the reverse of currTuning
			else if (currTuning.equals(tuningStringRev)) {
				tuning = t.getName();
				for (int i = 0; i < firstChord.length(); i++) {
					String symb = firstChord.substring(i, i+1);
					if (!symb.equals("-")) {
						if (COURSE_NUMBERS.contains(symb)) {
							tss = TabSymbolSet.SPANISH_TAB.toString(); 
							break;
						}
						else if (TAB_LETTERS.contains(symb)) {
							tss = TabSymbolSet.FRENCH_TAB.toString();
							break;
						}			
					}
				}
			}
		}
		allChords.add(Arrays.asList(new String[]{tss, tuning}));
		return allChords;
	}
	
	
	/**
	 * Given a list of system contents, retrieves the tab+ encoding.
	 * 
	 * @param systemContents
	 * @param courses
	 * @param tss
	 * @return
	 */
	private static StringBuffer getEncoding(List<List<String>> systemContents) {		
		int numCourses = systemContents.get(0).get(0).length() - 1;
		String tss = systemContents.get(systemContents.size()-1).get(0);
		
		StringBuffer tabPlusEncoding = new StringBuffer("");
		String ss = SymbolDictionary.SYMBOL_SEPARATOR;
		String space = ConstantMusicalSymbol.SPACE.getEncoding(); 
		// Per system
		// Remove last element from systemContents (list containing tss and tuning)
		List<List<String>> systemContentsNoLast = systemContents.subList(0, systemContents.size()-1);
		for (int i = 0; i < systemContentsNoLast.size(); i++) {
			List<String> currSystemContents = systemContentsNoLast.get(i);
			StringBuffer currSystem = new StringBuffer();
			boolean tieActive = false;
			for (int j = 0; j < currSystemContents.size(); j++) {
				String event = currSystemContents.get(j);
				String nextEvent = null;
				if (j+1 < currSystemContents.size()) {
					nextEvent = currSystemContents.get(j+1);
				}
				// Non-barline or repeat dots event
				if (!(event.equals(BARLINE_EVENT) || event.equals(REPEAT_DOTS_EVENT))) {
					if (!tieActive) {
						String tabPlusChord;
						// MS
						if (event.contains("/")) {
							tabPlusChord = MENSURATION_SIGNS.get(event) + ss;
						}
						// Chord
						else {
							String rs = RHYTHM_SYMBOLS.get(event.substring(0, 1));
							if (nextEvent.equals(RHYTHM_DOT_EVENT)) {
								rs += "*";
								j++; // to skip nextEvent
							}
							if (nextEvent.contains("+")) {
								String nextRs = "";	
								// Get RS of event tied to
								for (int k = j+2; k < currSystemContents.size(); k++) { // +2 to skip also nextEvent
									String next = currSystemContents.get(k);
									if (!(next.equals(BARLINE_EVENT) || next.equals(REPEAT_DOTS_EVENT))) {
										nextRs = RHYTHM_SYMBOLS.get(next.substring(0, 1));
										break;
									}
								}
								// Set rs to RS corresponding to sum rs and nextRs
								for (RhythmSymbol r : RhythmSymbol.getRhythmSymbols()) {
									if (r.getDuration() == RhythmSymbol.getRhythmSymbol(rs).getDuration() +
										RhythmSymbol.getRhythmSymbol(nextRs).getDuration()) {
										// Do not consider coronas/fermate
										if (!r.getSymbol().equals(RhythmSymbol.coronaBrevis.getSymbol()) &&
											!r.getSymbol().equals(RhythmSymbol.fermateDotted.getSymbol())) {
											rs = r.getEncoding();
											break;
										}
									}
								}
								j++; // to skip nextEvent
								tieActive = true; // skip event tied to
							}
							
							tabPlusChord = rs;
							if (!rs.isEmpty()) { 
								tabPlusChord += ss;
							}
							// Start from lowest course
							String chordOnly = event.substring(1);
							if (tss.equals(TabSymbolSet.ITALIAN_TAB.toString())) {
								chordOnly = new StringBuilder(chordOnly).reverse().toString();
							}				
							char[] chordAsArr = chordOnly.toCharArray();
							for (int k = numCourses-1; k >= 0; k--) {
								char currChar = chordAsArr[k];
								if (currChar != '-') {
									tabPlusChord += String.valueOf(currChar) + String.valueOf(k+1) + ss;
								}
							}
						}
						tabPlusChord += space + ss;
						currSystem.append(tabPlusChord);
					}
					else {
						tieActive = false;
					}
				}
				// Barline or repeat dots event
				else {
					// First barline is not followed by line break 
					if (currSystem.length() == 0) {
						currSystem.append(ConstantMusicalSymbol.BARLINE.getEncoding() + ss);
					}
					else {
						if (event.equals(BARLINE_EVENT)) {
							// If single barline
							if (nextEvent != null && !nextEvent.equals(BARLINE_EVENT) || nextEvent == null) {
								currSystem.append(ConstantMusicalSymbol.BARLINE.getEncoding() + ss + "\r\n");
							}
							// If double barline or start double repeat barline
							if (nextEvent != null && nextEvent.equals(BARLINE_EVENT)) {
								String afterNextEvent = null;
								if (j+2 < currSystemContents.size()) {
									afterNextEvent = currSystemContents.get(j+2);
								}
								String toAdd = ConstantMusicalSymbol.DOUBLE_BARLINE.getEncoding();
								j++; // to skip next event
								if (afterNextEvent != null && afterNextEvent.equals(REPEAT_DOTS_EVENT)) {
									toAdd = ConstantMusicalSymbol.DOUBLE_REPEAT_BARLINE_OPEN.getEncoding();
									j++; // to skip also event after next event
								}
								currSystem.append(toAdd + ss + "\r\n");
							}
						}
						// If end double repeat barline
						if (event.equals(REPEAT_DOTS_EVENT)) {
							currSystem.append(
								ConstantMusicalSymbol.DOUBLE_REPEAT_BARLINE_CLOSE.getEncoding() + ss + "\r\n");
							j += 2;
						}
					}
				}
			}
			if (i+1 < systemContentsNoLast.size()) {
				currSystem.append(SymbolDictionary.SYSTEM_BREAK_INDICATOR + "\r\n");
			}
			else {
				currSystem.append(SymbolDictionary.END_BREAK_INDICATOR);
			}
			tabPlusEncoding.append(currSystem);
		}
		return tabPlusEncoding;
	}

}
