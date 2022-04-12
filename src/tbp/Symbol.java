package tbp;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Symbol {

	// Contant musical symbols
	public static final ConstantMusicalSymbol SPACE = new ConstantMusicalSymbol(">", ">");
	public static final ConstantMusicalSymbol BARLINE = new ConstantMusicalSymbol(ConstantMusicalSymbol.PIPE, ConstantMusicalSymbol.PIPE);
	public static final List<ConstantMusicalSymbol> CONSTANT_MUSICAL_SYMBOLS;
	static {
		CONSTANT_MUSICAL_SYMBOLS = Arrays.asList(new ConstantMusicalSymbol[]{
			SPACE, BARLINE,
			BARLINE.makeBarlineVariant(1, "left"),
			BARLINE.makeBarlineVariant(1, "right"),
			BARLINE.makeBarlineVariant(1, "both"),
			BARLINE.makeBarlineVariant(2, null),
			BARLINE.makeBarlineVariant(2, "left"),
			BARLINE.makeBarlineVariant(2, "right"),
			BARLINE.makeBarlineVariant(2, "both"),
		});
	}
	public static final Map<String, ConstantMusicalSymbol> CMS_MAP;
	static {
		CMS_MAP = new LinkedHashMap<String, ConstantMusicalSymbol>();
		CONSTANT_MUSICAL_SYMBOLS.forEach(s -> CMS_MAP.put(s.getEncoding(), s));
	}

	// Mensuration signs
	public static final MensurationSign TWO	= new MensurationSign("M2", "2", new Integer[]{2, 4});
	public static final MensurationSign THREE = new MensurationSign("M3", "3", new Integer[]{3, 4});
	public static final MensurationSign FOUR = new MensurationSign("M4", "4", new Integer[]{4, 4});
	public static final MensurationSign SIX	= new MensurationSign("M6", "6", new Integer[]{6, 4});
	public static final MensurationSign O = new MensurationSign("MO", "O", new Integer[]{3, 4});
	public static final MensurationSign C = new MensurationSign("MC", "C", new Integer[]{4, 4});
	public static final MensurationSign CUT_C = new MensurationSign("MC\\", "\u00A2", new Integer[]{2, 2});	
	public static final List<MensurationSign> MENSURATION_SIGNS;
	static {
		MENSURATION_SIGNS = Arrays.asList(new MensurationSign[]{
			TWO, THREE,FOUR, SIX, O, C, CUT_C,
			THREE.makeVariant(-1, 4),
			THREE.makeVariant(2, -1),
			FOUR.makeVariant(2, -1),
			SIX.makeVariant(2, -1),
			TWO.makeVariant(1, -1),
			THREE.makeVariant(1, -1)
		});
	}	
	public static final Map<String, MensurationSign> MS_MAP;
	static {
		MS_MAP = new LinkedHashMap<String, MensurationSign>();
		MensurationSign.MENSURATION_SIGNS.forEach(s -> MS_MAP.put(s.getEncoding(), s));
	}

	// Rhythm symbols
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
	public static final List<RhythmSymbol> RHYTHM_SYMBOLS;
	static {
		RHYTHM_SYMBOLS = Arrays.asList(new RhythmSymbol[]{
			// Basic (lo-sf)
			LONGA, BREVIS, SEMIBREVIS, MINIM, SEMIMINIM, FUSA, SEMIFUSA, CORONA_BREVIS,
			CORONA_SEMIBREVIS, RHYTHM_DOT,
			// Dotted (lo-fu)
			LONGA.makeVariant(true, false, false, 1, null),
			BREVIS.makeVariant(true, false, false, 1, null),
			SEMIBREVIS.makeVariant(true, false, false, 1, null),
			MINIM.makeVariant(true, false, false, 1, null),
			MINIM.makeVariant(true, false, false, 2, null),
			SEMIMINIM.makeVariant(true, false, false, 1, null),
			FUSA.makeVariant(true, false, false, 1, null),
			CORONA_BREVIS.makeVariant(true, false, false, 1, null),
			CORONA_SEMIBREVIS.makeVariant(true, false, false, 1, null),
			// Beamed (sm-sf)
			SEMIMINIM.makeVariant(false, true, false, 0, null),
			FUSA.makeVariant(false, true, false, 0, null),
			SEMIFUSA.makeVariant(false, true, false, 0, null),
			// Dotted and beamed (sm-fu)
			SEMIMINIM.makeVariant(true, true, false, 1, null),
			FUSA.makeVariant(true, true, false, 1, null),
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
	}
	public static final Map<String, RhythmSymbol> RS_MAP;
	static {
		RS_MAP = new LinkedHashMap<String, RhythmSymbol>();
		RHYTHM_SYMBOLS.forEach(s -> RS_MAP.put(s.getEncoding(), s));
	}

	// Tab symbols
	// ...
	
	public static final List<TabSymbol> TAB_SYMBOLS;
	static {
		TAB_SYMBOLS = Arrays.asList(new TabSymbol[]{
		});
	}	
	public static final Map<String, TabSymbol> TS_MAP;
	static {
		TS_MAP = new LinkedHashMap<String, TabSymbol>();
		TAB_SYMBOLS.forEach(s -> TS_MAP.put(s.getEncoding(), s));
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
		return !CMS_MAP.containsKey(e) ? null : CMS_MAP.get(e);
	}


	public static MensurationSign getMensurationSign(String e) {
		return !MS_MAP.containsKey(e) ? null : MS_MAP.get(e);
	}


	public static RhythmSymbol getRhythmSymbol(String e) {
		return !RS_MAP.containsKey(e) ? null : RS_MAP.get(e);
	}


//	public static enum ConstantMusicalSymbolE {
//		SPACE(">", "-"),
//		BARLINE("|", "|"),
//		DOUBLE_BARLINE("||", "||"),
//		REPEAT_BARLINE_LEFT ("|:", "|:"),
//		REPEAT_BARLINE_RIGHT(":|", ":|"),
//		REPEAT_DOUBLE_BARLINE_LEFT("||:", "||:"),
//		REPEAT_DOUBLE_BARLINE_RIGHT(":||", ":||"),
//		REPEAT_BARLINE_BOTH(":|:", ":|:"),
//		REPEAT_DOUBLE_BARLINE_BOTH(":||:", ":||:");
//
//		private String encoding;
//		private String symbol;
//		public static final Map<String, ConstantMusicalSymbolE> CONSTANT_MUSICAL_SYMBOLS;
//		static {
//			CONSTANT_MUSICAL_SYMBOLS = new LinkedHashMap<String, ConstantMusicalSymbolE>();
//			Arrays.asList(ConstantMusicalSymbolE.values()).forEach(c -> CONSTANT_MUSICAL_SYMBOLS.put(c.getEncoding(), c));
//		}
//
//		ConstantMusicalSymbolE(String e, String s) {
//			encoding = e;
//			symbol = s;
//		}
//
//		public String getEncoding() {
//			return encoding;
//		}
//
//		public String getSymbol() {
//			return symbol;
//		}
//
//		public static ConstantMusicalSymbolE getConstantMusicalSymbol(String e) {
//			return !CONSTANT_MUSICAL_SYMBOLS.containsKey(e) ? null : CONSTANT_MUSICAL_SYMBOLS.get(e);
//		}
//
//		public static boolean isBarline(String e) {
//			ConstantMusicalSymbolE c = getConstantMusicalSymbol(e);
//			return (c != null && c != SPACE);
//		}
//	}


//	public static final List<Symbol> SYMBOLS; 
//	static {
//		SYMBOLS = new ArrayList<>(); 
//		Field[] declaredFields = Symbol.class.getDeclaredFields();
//		for (Field f : declaredFields) {
//			if (Modifier.isStatic(f.getModifiers())) {
//				if (Symbol.class.isAssignableFrom(f.getType())) {
//					try {
//						SYMBOLS.add((Symbol) f.get(null));
//					} catch (IllegalAccessException e) {
//						e.printStackTrace();
//					}
//				}
//			}
////			if (f.getType().isInstance(RhythmSymbol.class)) {
////				System.out.println(f.getName());
////				System.out.println(f.getType());
////			}
//		}
//	}


//	public static <T> Symbol getSymbol(String e, Class<?> c) {
//		if (!SYMBOLS_MAP.containsKey(e)) {
//			return null;
//		}
//		else {
//			Symbol s = SYMBOLS_MAP.get(e);
//			if (c == ConstantMusicalSymbol.class) {
//				return s instanceof ConstantMusicalSymbol ? s : null;
//			}
//			else if (c == RhythmSymbol.class) {
//				return s instanceof RhythmSymbol ? s : null;
//			}
////			else if (c == TabSymbol.class) {
////				return s instanceof TabSymbol ? (TabSymbol) s : null;
////			} 
//			else {
//				return null;
//			}
//		}
//	}


//	private static final Map<String, Symbol> SYMBOLS_MAP;
//	static {
//		SYMBOLS_MAP = new LinkedHashMap<String, Symbol>();
//		SYMBOLS.forEach(s -> SYMBOLS_MAP.put(s.getEncoding(), s));
//	}


//	private static RhythmSymbol getRhythmSymbolOLD(String e) {
//		if (!SYMBOLS_MAP.containsKey(e)) {
//			return null;
//		}
//		else {
//			Symbol s = SYMBOLS_MAP.get(e);
//			return s instanceof RhythmSymbol ? (RhythmSymbol) s : null;
//		}
//	}

}
