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
	// 1. Contant musical symbols
	public static final ConstantMusicalSymbol SPACE = new ConstantMusicalSymbol(">", ">");
	public static final ConstantMusicalSymbol BARLINE = new ConstantMusicalSymbol(ConstantMusicalSymbol.PIPE, ConstantMusicalSymbol.PIPE);
	public static final Map<String, ConstantMusicalSymbol> CONSTANT_MUSICAL_SYMBOLS;
	static {
		List<ConstantMusicalSymbol> cmss = Arrays.asList(new ConstantMusicalSymbol[]{
			SPACE, BARLINE,
			BARLINE.makeBarlineVariant(1, "left"),
			BARLINE.makeBarlineVariant(1, "right"),
			BARLINE.makeBarlineVariant(1, "both"),
			BARLINE.makeBarlineVariant(2, null),
			BARLINE.makeBarlineVariant(2, "left"),
			BARLINE.makeBarlineVariant(2, "right"),
			BARLINE.makeBarlineVariant(2, "both"),
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
		// Basic (lo-sf)
		List<RhythmSymbol> rss = Arrays.asList(new RhythmSymbol[]{
			LONGA, BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, SEMIFUSA, CORONA_BREVIS,
			CORONA_SEMIBREVIS, RHYTHM_DOT});
		// Dotted (lo-fu)
		Arrays.asList(new RhythmSymbol[]{LONGA, BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, CORONA_BREVIS, CORONA_SEMIBREVIS}).
			forEach(rs -> rss.add(rs.makeVariant(true, false, false, 1, null)));
		rss.add(MINIM.makeVariant(true, false, false, 2, null));
//			LONGA.makeVariant(true, false, false, 1, null),
//			BREVIS.makeVariant(true, false, false, 1, null),
//			SEMIBREVIS.makeVariant(true, false, false, 1, null),
//			MINIM.makeVariant(true, false, false, 1, null),
//			MINIM.makeVariant(true, false, false, 2, null),
//			SEMIMINIM.makeVariant(true, false, false, 1, null),
//			FUSA.makeVariant(true, false, false, 1, null),
//			CORONA_BREVIS.makeVariant(true, false, false, 1, null),
//			CORONA_SEMIBREVIS.makeVariant(true, false, false, 1, null),
		// Beamed (sm-sf)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA, SEMIFUSA}).
			forEach(rs -> rss.add(rs.makeVariant(false, true, false, 0, null)));
//			SEMIMINIM.makeVariant(false, true, false, 0, null),
//			FUSA.makeVariant(false, true, false, 0, null),
//			SEMIFUSA.makeVariant(false, true, false, 0, null),
		// Dotted and beamed (sm-fu)
		Arrays.asList(new RhythmSymbol[]{SEMIMINIM, FUSA}).
			forEach(rs -> rss.add(rs.makeVariant(true, true, false, 1, null)));
//			SEMIMINIM.makeVariant(true, true, false, 1, null),
//			FUSA.makeVariant(true, true, false, 1, null),
			
		// Triplets, basic (br-sf)
			BREVIS.makeVariant(false, false, true, 0, ""),
			BREVIS.makeVariant(false, false, true, 0, "open"),
			BREVIS.makeVariant(false, false, true, 0, "close"),
			SEMIBREVIS.makeVariant(false, false, true, 0, ""),
			SEMIBREVIS.makeVariant(false, false, true, 0, "open"),
			SEMIBREVIS.makeVariant(false, false, true, 0, "close"),
			MINIM.makeVariant(false, false, true, 0, ""),
			MINIM.makeVariant(false, false, true, 0, "open"),
			MINIM.makeVariant(false, false, true, 0, "close"),
			SEMIMINIM.makeVariant(false, false, true, 0, ""),
			SEMIMINIM.makeVariant(false, false, true, 0, "open"),
			SEMIMINIM.makeVariant(false, false, true, 0, "close"),
			FUSA.makeVariant(false, false, true, 0, ""),
			FUSA.makeVariant(false, false, true, 0, "open"),
			FUSA.makeVariant(false, false, true, 0, "close"),
			SEMIFUSA.makeVariant(false, false, true, 0, ""),
			SEMIFUSA.makeVariant(false, false, true, 0, "open"),
			SEMIFUSA.makeVariant(false, false, true, 0, "close"),
			// Triplets, dotted (br-fu)
			BREVIS.makeVariant(true, false, true, 1, ""),
			BREVIS.makeVariant(true, false, true, 1, "open"),
			BREVIS.makeVariant(true, false, true, 1, "close"),
			SEMIBREVIS.makeVariant(true, false, true, 1, ""),
			SEMIBREVIS.makeVariant(true, false, true, 1, "open"),
			SEMIBREVIS.makeVariant(true, false, true, 1, "close"),
			MINIM.makeVariant(true, false, true, 1, ""),
			MINIM.makeVariant(true, false, true, 1, "open"),
			MINIM.makeVariant(true, false, true, 1, "close"),
			SEMIMINIM.makeVariant(true, false, true, 1, ""),
			SEMIMINIM.makeVariant(true, false, true, 1, "open"),
			SEMIMINIM.makeVariant(true, false, true, 1, "close"),
			FUSA.makeVariant(true, false, true, 1, ""),
			FUSA.makeVariant(true, false, true, 1, "open"),
			FUSA.makeVariant(true, false, true, 1, "close"),
			// Triplets, beamed (sm-sf)
			SEMIMINIM.makeVariant(false, true, true, 0, ""),
			SEMIMINIM.makeVariant(false, true, true, 0, "open"),
			SEMIMINIM.makeVariant(false, true, true, 0, "close"),
			FUSA.makeVariant(false, true, true, 0, ""),
			FUSA.makeVariant(false, true, true, 0, "open"),
			FUSA.makeVariant(false, true, true, 0, "close"),
			SEMIFUSA.makeVariant(false, true, true, 0, ""),
			SEMIFUSA.makeVariant(false, true, true, 0, "open"),
			SEMIFUSA.makeVariant(false, true, true, 0, "close"),
			// Triplets, dotted and beamed (sm-fu)
			SEMIMINIM.makeVariant(true, true, true, 1, ""),
			SEMIMINIM.makeVariant(true, true, true, 1, "open"),
			SEMIMINIM.makeVariant(true, true, true, 1, "close"),
			FUSA.makeVariant(true, true, true, 1, ""),
			FUSA.makeVariant(true, true, true, 1, "open"),
			FUSA.makeVariant(true, true, true, 1, "close")		
		});
		RHYTHM_SYMBOLS = new LinkedHashMap<String, RhythmSymbol>();
		rss.forEach(s -> RHYTHM_SYMBOLS.put(s.getEncoding(), s));
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
			e.getValue().forEach(s -> curr.put(s.getEncoding(), s));
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
		if (!TAB_SYMBOLS.containsKey(tss.getName())) {
			return null;
		}
		else {
			Map<String, TabSymbol> m = TAB_SYMBOLS.get(tss.getName());
			return !m.containsKey(e) ? null : m.get(e);
		}
	}

}
