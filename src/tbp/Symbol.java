package tbp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tbp.TabSymbol.TabSymbolSet;

public class Symbol {

	// I. Punctuation symbols
	public static final String SYMBOL_SEPARATOR = ".";  
	public static final String SYSTEM_BREAK_INDICATOR = "/";
	public static final String END_BREAK_INDICATOR = "//";
	public static final String OPEN_METADATA_BRACKET = "{";
	public static final String CLOSE_METADATA_BRACKET = "}";
		
	// II. Musical symbols
	// 1. Constant musical symbols
	public static final ConstantMusicalSymbol SPACE = new ConstantMusicalSymbol(">", ">");
	public static final ConstantMusicalSymbol BARLINE = new ConstantMusicalSymbol(ConstantMusicalSymbol.PIPE, ConstantMusicalSymbol.PIPE);
	public static final Map<String, ConstantMusicalSymbol> CONSTANT_MUSICAL_SYMBOLS;
	static {
		List<ConstantMusicalSymbol> cmss = Arrays.asList(new ConstantMusicalSymbol[]{
			SPACE, BARLINE,
			BARLINE.makeVariant(1, "left"),
			BARLINE.makeVariant(1, "right"),
			BARLINE.makeVariant(1, "both"),
			BARLINE.makeVariant(2, null),
			BARLINE.makeVariant(2, "left"),
			BARLINE.makeVariant(2, "right"),
			BARLINE.makeVariant(2, "both"),
		});
		CONSTANT_MUSICAL_SYMBOLS = new LinkedHashMap<String, ConstantMusicalSymbol>();
		cmss.forEach(cms -> CONSTANT_MUSICAL_SYMBOLS.put(cms.getEncoding(), cms));
	}

	// 2. Mensuration signs
	public static final MensurationSign TWO	= new MensurationSign("M2", "2", new Integer[]{2, 4});
	public static final MensurationSign THREE = new MensurationSign("M3", "3", new Integer[]{3, 4});
	public static final MensurationSign FOUR = new MensurationSign("M4", "4", new Integer[]{4, 4});
	public static final MensurationSign SIX	= new MensurationSign("M6", "6", new Integer[]{6, 4});
	public static final MensurationSign O = new MensurationSign("MO", "O", new Integer[]{3, 4});
	public static final MensurationSign C = new MensurationSign("MC", "C", new Integer[]{4, 4});
	public static final MensurationSign CUT_C = new MensurationSign("MC\\", "\u00A2", new Integer[]{2, 2});	
	public static final Map<String, MensurationSign> MENSURATION_SIGNS;
	static {
		List<MensurationSign> mss = Arrays.asList(new MensurationSign[]{
			TWO, THREE,FOUR, SIX, O, C, CUT_C,
			THREE.makeVariant(-1, 4),
			THREE.makeVariant(2, -1),
			FOUR.makeVariant(2, -1),
			SIX.makeVariant(2, -1),
			TWO.makeVariant(1, -1),
			THREE.makeVariant(1, -1)
		}); 
		MENSURATION_SIGNS = new LinkedHashMap<String, MensurationSign>();
		mss.forEach(ms -> MENSURATION_SIGNS.put(ms.getEncoding(), ms));
	}

	// 3. Rhythm symbols
	public static final RhythmSymbol LONGA = new RhythmSymbol("lo", "D", 192);
	public static final RhythmSymbol BREVIS = new RhythmSymbol("br", "W", 96); 
	public static final RhythmSymbol SEMIBREVIS = new RhythmSymbol("sb", "H", 48);
	public static final RhythmSymbol MINIM = new RhythmSymbol("mi", "Q", 24);
	public static final RhythmSymbol SEMIMINIM = new RhythmSymbol("sm", "E", 12);
	public static final RhythmSymbol FUSA = new RhythmSymbol("fu", "S", 6); 
	public static final RhythmSymbol SEMIFUSA = new RhythmSymbol("sf", "T", 3);
	public static final RhythmSymbol CORONA_BREVIS = new RhythmSymbol("cobr", "C", 96);
	public static final RhythmSymbol CORONA_SEMIBREVIS = new RhythmSymbol("cosb", "C", 48);
	public static final RhythmSymbol RHYTHM_DOT = new RhythmSymbol(RhythmSymbol.DOT_ENCODING, ".", -1);
	public static final Map<String, RhythmSymbol> RHYTHM_SYMBOLS;
	static {	
		List<RhythmSymbol> rss = new ArrayList<RhythmSymbol>();
		// Basic (lo-sf)
		Arrays.asList(new RhythmSymbol[]{LONGA, BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, SEMIFUSA, 
			CORONA_BREVIS, CORONA_SEMIBREVIS, RHYTHM_DOT}).forEach(rs -> rss.add(rs));
		// Dotted (lo-fu)
		Arrays.asList(new RhythmSymbol[]{LONGA, BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, CORONA_BREVIS, 
			CORONA_SEMIBREVIS}).forEach(rs -> rss.addAll(rs.makeVariant(true, false, false, 1)));
		rss.addAll(MINIM.makeVariant(true, false, false, 2));
		// Beamed (sm-sf)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA, SEMIFUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(false, true, false, 0)));
		// Dotted and beamed (sm-fu)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(true, true, false, 1)));
		// Triplets, basic (br-sf)
		Arrays.asList(new RhythmSymbol[]{BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, SEMIFUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(false, false, true, 0)));
		// Triplets, dotted (br-fu)
		Arrays.asList(new RhythmSymbol[]{BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(true, false, true, 1)));
		// Triplets, beamed (sm-sf)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA, SEMIFUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(false, true, true, 0)));
		// Triplets, dotted and beamed (sm-fu)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA}).
			forEach(rs -> rss.addAll(rs.makeVariant(true, true, true, 1)));
		RHYTHM_SYMBOLS = new LinkedHashMap<String, RhythmSymbol>();
		rss.forEach(rs -> RHYTHM_SYMBOLS.put(rs.getEncoding(), rs));
	}
	
	// 4. Tab symbols
	public static final List<TabSymbol> FRENCH = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.FRENCH);
	public static final List<TabSymbol> ITALIAN = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.ITALIAN);
	public static final List<TabSymbol> SPANISH = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.SPANISH);
	public static final List<TabSymbol> JUDENKUENIG_1523 = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.JUDENKUENIG_1523);
	public static final List<TabSymbol> NEWSIDLER_1536 = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.NEWSIDLER_1536);
	public static final List<TabSymbol> OCHSENKUN_1558 = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.OCHSENKUN_1558);
	public static final List<TabSymbol> HECKEL_1562 = TabSymbol.listTabSymbols(TabSymbol.TabSymbolSet.HECKEL_1562);	
	public static final Map<String, Map<String, TabSymbol>> TAB_SYMBOLS;
	static {
		Map<String, List<TabSymbol>> tss = new LinkedHashMap<String, List<TabSymbol>>();
		tss.put(TabSymbolSet.FRENCH.getName(), FRENCH);
		tss.put(TabSymbolSet.ITALIAN.getName(), ITALIAN);
		tss.put(TabSymbolSet.SPANISH.getName(), SPANISH);
		tss.put(TabSymbolSet.JUDENKUENIG_1523.getName(), JUDENKUENIG_1523);
		tss.put(TabSymbolSet.NEWSIDLER_1536.getName(), NEWSIDLER_1536);
		tss.put(TabSymbolSet.OCHSENKUN_1558.getName(), OCHSENKUN_1558);
		tss.put(TabSymbolSet.HECKEL_1562.getName(), HECKEL_1562);
		TAB_SYMBOLS = new LinkedHashMap<String, Map<String, TabSymbol>>();
		for (Entry<String, List<TabSymbol>> e : tss.entrySet()) {
			Map<String, TabSymbol> curr = new LinkedHashMap<String, TabSymbol>();
			e.getValue().forEach(ts -> curr.put(ts.getEncoding(), ts));
			TAB_SYMBOLS.put(e.getKey(), curr);
		}
	}


	private String encoding;
	private String symbol;


	void setEncoding(String e) {
		encoding = e;
	}


	void setSymbol(String s) {
		symbol = s;
	}


	public String getEncoding() {
		return encoding;
	}


	public String getSymbol() {
		return symbol;
	}


	public static ConstantMusicalSymbol getConstantMusicalSymbol(String e) {
		return !CONSTANT_MUSICAL_SYMBOLS.containsKey(e) ? null : CONSTANT_MUSICAL_SYMBOLS.get(e);
	}


	public static MensurationSign getMensurationSign(String e) {
		return !MENSURATION_SIGNS.containsKey(e) ? null : MENSURATION_SIGNS.get(e);
	}


	public static RhythmSymbol getRhythmSymbol(String e) {
		return !RHYTHM_SYMBOLS.containsKey(e) ? null : RHYTHM_SYMBOLS.get(e);
	}


	public static TabSymbol getTabSymbol(String e, TabSymbolSet tss) {
		String n = tss.getName(); 
		return !TAB_SYMBOLS.containsKey(n) ? null :
			(!TAB_SYMBOLS.get(n).containsKey(e) ? null : TAB_SYMBOLS.get(n).get(e));
	}


	/**
	 * Gets the equivalent of the given TabSymbol in the given TabSymbolSet. 
	 * 
	 * @param ts
	 * @param tss
	 * @return
	 */
	public static TabSymbol getTabSymbolEquivalent(TabSymbol ts, TabSymbolSet tss) {
		// Get new encoding
		int course = ts.getCourse();
		int fret = ts.getFret();
		if (tss == TabSymbolSet.FRENCH) {
			String e = Character.toString("abcdefghikl".charAt(fret)) + String.valueOf(course);
		}
		else if (tss == TabSymbolSet.ITALIAN || tss == TabSymbolSet.SPANISH) {
			String e = String.valueOf(fret) + String.valueOf(course);
		}
		else {
			
		}
		return TAB_SYMBOLS.get(tss.getName()).get(e);
		for (TabSymbol newTs : TAB_SYMBOLS.get(tss.getName()).values()) {
			if (newTs.getCourse() == ts.getCourse() && newTs.getFret() == ts.getFret()) {
				return newTs;
			}
		}
		return null;
	}

}