package tbp;

import java.util.Arrays;
import java.util.List;

public class SymbolDictionary {

	// I. Punctuation symbols
	public static final String SYMBOL_SEPARATOR = ".";  
	public static final String SYSTEM_BREAK_INDICATOR = "/";
	public static final String END_BREAK_INDICATOR = "//";
	public static final String OPEN_INFO_BRACKET = "{";
	public static final String CLOSE_INFO_BRACKET = "}";
	
	// II. Musical symbols
	// 1. Constant musical symbols
	// (see ContantMusicalSymbol)
	// 2. Variable musical symbols
	// a. RhythmSymbols	
	// (see RhythmSymbol)
	// b. MensurationSigns
	// (see MensurationSign)
	// c. TabSymbols and TabSymbolSets
	// ochsenkun1558
	public static List<TabSymbol> ochsenkun1558Content = Arrays.asList(new TabSymbol[]{
		new TabSymbol("+", 0, 6, 43),    
		new TabSymbol("2-", 1, 6, 44),
		new TabSymbol("3-", 2, 6, 45),
		new TabSymbol("4-", 3, 6, 46),
		new TabSymbol("5-", 4, 6, 47),
		new TabSymbol("6-", 5, 6, 48),
		new TabSymbol("7-", 6, 6, 49),
		new TabSymbol("8-", 7, 6, 50),
		new TabSymbol("9-", 8, 6, 51),
		new TabSymbol("10-", 9, 6, 52),
		new TabSymbol("11-", 10, 6, 53),
		//
		new TabSymbol("1", 0, 5, 48),
		new TabSymbol("a", 1, 5, 49),
		new TabSymbol("f", 2, 5, 50),
		new TabSymbol("l", 3, 5, 51),
		new TabSymbol("q", 4, 5, 52),
		new TabSymbol("x", 5, 5, 53),
		new TabSymbol("a-", 6, 5, 54),
		new TabSymbol("f-", 7, 5, 55),
		new TabSymbol("l-", 8, 5, 56),
		new TabSymbol("q-", 9, 5, 57),
		new TabSymbol("x-", 10, 5, 58),
		//
		new TabSymbol("2", 0, 4, 53),
		new TabSymbol("b", 1, 4, 54),
		new TabSymbol("g", 2, 4, 55),
		new TabSymbol("m", 3, 4, 56),
		new TabSymbol("r", 4, 4, 57),
		new TabSymbol("y", 5, 4, 58),
		new TabSymbol("b-", 6, 4, 59),
		new TabSymbol("g-", 7, 4, 60),
		new TabSymbol("m-", 8, 4, 61),
		new TabSymbol("r-", 9, 4, 62),
		new TabSymbol("y-", 10, 4, 63),
		//
		new TabSymbol("3", 0, 3, 57),
		new TabSymbol("c", 1, 3, 58),
		new TabSymbol("h", 2, 3, 59),
		new TabSymbol("n", 3, 3, 60),
		new TabSymbol("s", 4, 3, 61),
		new TabSymbol("z", 5, 3, 62),
		new TabSymbol("c-", 6, 3, 63),
		new TabSymbol("h-", 7, 3, 64),
		new TabSymbol("n-", 8, 3, 65),
		new TabSymbol("s-", 9, 3, 66),
		new TabSymbol("z-", 10, 3, 67),
		//
		new TabSymbol("4", 0, 2, 62),
		new TabSymbol("d", 1, 2, 63),
		new TabSymbol("i", 2, 2, 64),
		new TabSymbol("o", 3, 2, 65),
		new TabSymbol("t", 4, 2, 66),
		new TabSymbol("7", 5, 2, 67),
		new TabSymbol("d-", 6, 2, 68),
		new TabSymbol("i-", 7, 2, 69),
		new TabSymbol("o-", 8, 2, 70),
		new TabSymbol("t-", 9, 2, 71),
		new TabSymbol("7-", 10, 2, 72),
		//
		new TabSymbol("5", 0, 1, 67),
		new TabSymbol("e", 1, 1, 68),
		new TabSymbol("k", 2, 1, 69),
		new TabSymbol("p", 3, 1, 70),
		new TabSymbol("v", 4, 1, 71),
		new TabSymbol("9", 5, 1, 72),
		new TabSymbol("e-", 6, 1, 73),
		new TabSymbol("k-", 7, 1, 74),
		new TabSymbol("p-", 8, 1, 75),
		new TabSymbol("v-", 9, 1, 76),
		new TabSymbol("9-", 10, 1, 77)
	});
	
	// judenkuenig1523 
	public static List<TabSymbol> judenkuenig1523Content = Arrays.asList(new TabSymbol[]{
		new TabSymbol("A", 0, 6, 43),    
		new TabSymbol("B", 1, 6, 44),
		new TabSymbol("C", 2, 6, 45),
		new TabSymbol("D", 3, 6, 46),
		new TabSymbol("E", 4, 6, 47),
		new TabSymbol("F", 5, 6, 48),
		new TabSymbol("G", 6, 6, 49),
		new TabSymbol("H", 7, 6, 50),
		//
		new TabSymbol("1", 0, 5, 48),
		new TabSymbol("a", 1, 5, 49),
		new TabSymbol("f", 2, 5, 50),
		new TabSymbol("l", 3, 5, 51),
		new TabSymbol("q", 4, 5, 52),
		new TabSymbol("x", 5, 5, 53),
		new TabSymbol("a-", 6, 5, 54),
		new TabSymbol("f-", 7, 5, 55),
	
		new TabSymbol("2", 0, 4, 53),
		new TabSymbol("b", 1, 4, 54),
		new TabSymbol("g", 2, 4, 55),
		new TabSymbol("m", 3, 4, 56),
		new TabSymbol("r", 4, 4, 57),
		new TabSymbol("y", 5, 4, 58),
		new TabSymbol("b-", 6, 4, 59),
		new TabSymbol("g-", 7, 4, 60),
		//
		new TabSymbol("3", 0, 3, 57),
		new TabSymbol("c", 1, 3, 58),
		new TabSymbol("h", 2, 3, 59),
		new TabSymbol("n", 3, 3, 60),
		new TabSymbol("s", 4, 3, 61),
		new TabSymbol("z", 5, 3, 62),
		new TabSymbol("c-", 6, 3, 63),
		new TabSymbol("h-", 7, 3, 64),
		//
		new TabSymbol("4", 0, 2, 62),
		new TabSymbol("d", 1, 2, 63),
		new TabSymbol("i", 2, 2, 64),
		new TabSymbol("o", 3, 2, 65),
		new TabSymbol("t", 4, 2, 66),
		new TabSymbol("7", 5, 2, 67),
		new TabSymbol("d-", 6, 2, 68),
		new TabSymbol("i-", 7, 2, 69),
		//
		new TabSymbol("5", 0, 1, 67),
		new TabSymbol("e", 1, 1, 68),
		new TabSymbol("k", 2, 1, 69),
		new TabSymbol("p", 3, 1, 70),
		new TabSymbol("v", 4, 1, 71),
		new TabSymbol("9", 5, 1, 72),
		new TabSymbol("e-", 6, 1, 73),
		new TabSymbol("k-", 7, 1, 74),
	});

	// newsidler1536
	public static List<TabSymbol> newsidler1536Content = Arrays.asList(new TabSymbol[]{
		new TabSymbol("+", 0, 6, 43),
		new TabSymbol("A", 1, 6, 44),
		new TabSymbol("B", 2, 6, 45),
		new TabSymbol("C", 3, 6, 46),
		new TabSymbol("D", 4, 6, 47),
		new TabSymbol("E", 5, 6, 48),
		new TabSymbol("F", 6, 6, 49),
		new TabSymbol("G", 7, 6, 50),
		new TabSymbol("H", 8, 6, 51),
		//
		new TabSymbol("1", 0, 5, 48),
		new TabSymbol("a", 1, 5, 49),
		new TabSymbol("f", 2, 5, 50),
		new TabSymbol("l", 3, 5, 51),
		new TabSymbol("q", 4, 5, 52),
		new TabSymbol("x", 5, 5, 53),
		new TabSymbol("a-", 6, 5, 54),
		new TabSymbol("f-", 7, 5, 55),
		new TabSymbol("l-", 8, 5, 56),
		//
		new TabSymbol("2", 0, 4, 53),
		new TabSymbol("b", 1, 4, 54),
		new TabSymbol("g", 2, 4, 55),
		new TabSymbol("m", 3, 4, 56),
		new TabSymbol("r", 4, 4, 57),
		new TabSymbol("y", 5, 4, 58),
		new TabSymbol("b-", 6, 4, 59),
		new TabSymbol("g-", 7, 4, 60),
		new TabSymbol("m-", 8, 4, 61),
		//
		new TabSymbol("3", 0, 3, 57),
		new TabSymbol("c", 1, 3, 58),
		new TabSymbol("h", 2, 3, 59),
		new TabSymbol("n", 3, 3, 60),
		new TabSymbol("s", 4, 3, 61),
		new TabSymbol("z", 5, 3, 62),
		new TabSymbol("c-", 6, 3, 63),
		new TabSymbol("h-", 7, 3, 64),
		new TabSymbol("n-", 8, 3, 65),
		//
		new TabSymbol("4", 0, 2, 62),
		new TabSymbol("d", 1, 2, 63),
		new TabSymbol("i", 2, 2, 64),
		new TabSymbol("o", 3, 2, 65),
		new TabSymbol("t", 4, 2, 66),
		new TabSymbol("7", 5, 2, 67),
		new TabSymbol("d-", 6, 2, 68),
		new TabSymbol("i-", 7, 2, 69),
		new TabSymbol("o-", 8, 2, 70),
		//
		new TabSymbol("5", 0, 1, 67),
		new TabSymbol("e", 1, 1, 68),
		new TabSymbol("k", 2, 1, 69),
		new TabSymbol("p", 3, 1, 70),
		new TabSymbol("v", 4, 1, 71),
		new TabSymbol("9", 5, 1, 72),
		new TabSymbol("e-", 6, 1, 73),
		new TabSymbol("k-", 7, 1, 74),
		new TabSymbol("p-", 8, 1, 75),
	});

	// heckel1562
	// NB: only first five frets encoded
	public static List<TabSymbol> heckel1562Content = Arrays.asList(new TabSymbol[]{
		new TabSymbol("+", 0, 6, 43),    
		new TabSymbol("a-", 1, 6, 44),
		new TabSymbol("f-", 2, 6, 45),
		new TabSymbol("l-", 3, 6, 46),
		new TabSymbol("q-", 4, 6, 47),
		new TabSymbol("x-", 5, 6, 48),
		//
		new TabSymbol("1", 0, 5, 48),    
		new TabSymbol("a", 1, 5, 49),
		new TabSymbol("f", 2, 5, 50),
		new TabSymbol("l", 3, 5, 51),
		new TabSymbol("q", 4, 5, 52),
		new TabSymbol("x", 5, 5, 53),
		//
		new TabSymbol("2", 0, 4, 53),
		new TabSymbol("b", 1, 4, 54),
		new TabSymbol("g", 2, 4, 55),
		new TabSymbol("m", 3, 4, 56),
		new TabSymbol("r", 4, 4, 57),
		new TabSymbol("y", 5, 4, 58),
		//
		new TabSymbol("3", 0, 3, 57),
		new TabSymbol("c", 1, 3, 58),
		new TabSymbol("h", 2, 3, 59),
		new TabSymbol("n", 3, 3, 60),
		new TabSymbol("s", 4, 3, 61),
		new TabSymbol("z", 5, 3, 62),
		//
		new TabSymbol("4", 0, 2, 62),
		new TabSymbol("d", 1, 2, 63),
		new TabSymbol("i", 2, 2, 64),
		new TabSymbol("o", 3, 2, 65),
		new TabSymbol("t", 4, 2, 66),
		new TabSymbol("7", 5, 2, 67),
		//
		new TabSymbol("5", 0, 1, 67),
		new TabSymbol("e", 1, 1, 68),
		new TabSymbol("k", 2, 1, 69),
		new TabSymbol("p", 3, 1, 70),
		new TabSymbol("v", 4, 1, 71),
		new TabSymbol("9", 5, 1, 72),
	});
			
	// frenchTab
	public static List<TabSymbol> frenchTabContent = Arrays.asList(new TabSymbol[]{
		new TabSymbol("a7", 0, 7, 41),    
		new TabSymbol("b7", 1, 7, 42),
		new TabSymbol("c7", 2, 7, 43),
		new TabSymbol("d7", 3, 7, 44),
		new TabSymbol("e7", 4, 7, 45),
		new TabSymbol("f7", 5, 7, 46),
		new TabSymbol("g7", 6, 7, 47),
		new TabSymbol("h7", 7, 7, 48),
		new TabSymbol("i7", 8, 7, 49),
		new TabSymbol("k7", 9, 7, 50),
		new TabSymbol("l7", 10, 7, 51),
		//
		new TabSymbol("a6", 0, 6, 43),    
		new TabSymbol("b6", 1, 6, 44),
		new TabSymbol("c6", 2, 6, 45),
		new TabSymbol("d6", 3, 6, 46),
		new TabSymbol("e6", 4, 6, 47),
		new TabSymbol("f6", 5, 6, 48),
		new TabSymbol("g6", 6, 6, 49),
		new TabSymbol("h6", 7, 6, 50),
		new TabSymbol("i6", 8, 6, 51),
		new TabSymbol("k6", 9, 6, 52),
		new TabSymbol("l6", 10, 6, 53),
		//
		new TabSymbol("a5", 0, 5, 48),
		new TabSymbol("b5", 1, 5, 49),
		new TabSymbol("c5", 2, 5, 50),
		new TabSymbol("d5", 3, 5, 51),
		new TabSymbol("e5", 4, 5, 52),
		new TabSymbol("f5", 5, 5, 53),
		new TabSymbol("g5", 6, 5, 54),
		new TabSymbol("h5", 7, 5, 55),
		new TabSymbol("i5", 8, 5, 56),
		new TabSymbol("k5", 9, 5, 57),
		new TabSymbol("l5", 10, 5, 58),
		//
		new TabSymbol("a4", 0, 4, 53),
		new TabSymbol("b4", 1, 4, 54),
		new TabSymbol("c4", 2, 4, 55),
		new TabSymbol("d4", 3, 4, 56),
		new TabSymbol("e4", 4, 4, 57),
		new TabSymbol("f4", 5, 4, 58),
		new TabSymbol("g4", 6, 4, 59),
		new TabSymbol("h4", 7, 4, 60),
		new TabSymbol("i4", 8, 4, 61),
		new TabSymbol("k4", 9, 4, 62),
		new TabSymbol("l4", 10, 4, 63),
		//
		new TabSymbol("a3", 0, 3, 57),
		new TabSymbol("b3", 1, 3, 58),
		new TabSymbol("c3", 2, 3, 59),
		new TabSymbol("d3", 3, 3, 60),
		new TabSymbol("e3", 4, 3, 61),
		new TabSymbol("f3", 5, 3, 62),
		new TabSymbol("g3", 6, 3, 63),
		new TabSymbol("h3", 7, 3, 64),
		new TabSymbol("i3", 8, 3, 65),
		new TabSymbol("k3", 9, 3, 66),
		new TabSymbol("l3", 10, 3, 67),
		//
		new TabSymbol("a2", 0, 2, 62),
		new TabSymbol("b2", 1, 2, 63),
		new TabSymbol("c2", 2, 2, 64),
		new TabSymbol("d2", 3, 2, 65),
		new TabSymbol("e2", 4, 2, 66),
		new TabSymbol("f2", 5, 2, 67),
		new TabSymbol("g2", 6, 2, 68),
		new TabSymbol("h2", 7, 2, 69),
		new TabSymbol("i2", 8, 2, 70),
		new TabSymbol("k2", 9, 2, 71),
		new TabSymbol("l2", 10, 2, 72),
		//
		new TabSymbol("a1", 0, 1, 67),
		new TabSymbol("b1", 1, 1, 68),
		new TabSymbol("c1", 2, 1, 69),
		new TabSymbol("d1", 3, 1, 70),
		new TabSymbol("e1", 4, 1, 71),
		new TabSymbol("f1", 5, 1, 72),
		new TabSymbol("g1", 6, 1, 73),
		new TabSymbol("h1", 7, 1, 74),
		new TabSymbol("i1", 8, 1, 75),
		new TabSymbol("k1", 9, 1, 76),
		new TabSymbol("l1", 10, 1, 77),
	});

	// italianTab
	public static List<TabSymbol> italianTabContent = Arrays.asList(new TabSymbol[]{
		new TabSymbol("07", 0, 7, 41),    
		new TabSymbol("17", 1, 7, 42),
		new TabSymbol("27", 2, 7, 43),
		new TabSymbol("37", 3, 7, 44),
		new TabSymbol("47", 4, 7, 45),
		new TabSymbol("57", 5, 7, 46),
		new TabSymbol("67", 6, 7, 47),
		new TabSymbol("77", 7, 7, 48),
		new TabSymbol("87", 8, 7, 49),
		new TabSymbol("97", 9, 7, 50),
		//
		new TabSymbol("06", 0, 6, 43),    
		new TabSymbol("16", 1, 6, 44),
		new TabSymbol("26", 2, 6, 45),
		new TabSymbol("36", 3, 6, 46),
		new TabSymbol("46", 4, 6, 47),
		new TabSymbol("56", 5, 6, 48),
		new TabSymbol("66", 6, 6, 49),
		new TabSymbol("76", 7, 6, 50),
		new TabSymbol("86", 8, 6, 51),
		new TabSymbol("96", 9, 6, 52),
		//
		new TabSymbol("05", 0, 5, 48),
		new TabSymbol("15", 1, 5, 49),
		new TabSymbol("25", 2, 5, 50),
		new TabSymbol("35", 3, 5, 51),
		new TabSymbol("45", 4, 5, 52),
		new TabSymbol("55", 5, 5, 53),
		new TabSymbol("65", 6, 5, 54),
		new TabSymbol("75", 7, 5, 55),
		new TabSymbol("85", 8, 5, 56),
		new TabSymbol("95", 9, 5, 57),
		//
		new TabSymbol("04", 0, 4, 53),
		new TabSymbol("14", 1, 4, 54),
		new TabSymbol("24", 2, 4, 55),
		new TabSymbol("34", 3, 4, 56),
		new TabSymbol("44", 4, 4, 57),
		new TabSymbol("54", 5, 4, 58),
		new TabSymbol("64", 6, 4, 59),
		new TabSymbol("74", 7, 4, 60),
		new TabSymbol("84", 8, 4, 61),
		new TabSymbol("94", 9, 4, 62),
		//
		new TabSymbol("03", 0, 3, 57),
		new TabSymbol("13", 1, 3, 58),
		new TabSymbol("23", 2, 3, 59),
		new TabSymbol("33", 3, 3, 60),
		new TabSymbol("43", 4, 3, 61),
		new TabSymbol("53", 5, 3, 62),
		new TabSymbol("63", 6, 3, 63),
		new TabSymbol("73", 7, 3, 64),
		new TabSymbol("83", 8, 3, 65),
		new TabSymbol("93", 9, 3, 66),
		//
		new TabSymbol("02", 0, 2, 62),
		new TabSymbol("12", 1, 2, 63),
		new TabSymbol("22", 2, 2, 64),
		new TabSymbol("32", 3, 2, 65),
		new TabSymbol("42", 4, 2, 66),
		new TabSymbol("52", 5, 2, 67),
		new TabSymbol("62", 6, 2, 68),
		new TabSymbol("72", 7, 2, 69),
		new TabSymbol("82", 8, 2, 70),
		new TabSymbol("92", 9, 2, 71),
		//
		new TabSymbol("01", 0, 1, 67),
		new TabSymbol("11", 1, 1, 68),
		new TabSymbol("21", 2, 1, 69),
		new TabSymbol("31", 3, 1, 70),
		new TabSymbol("41", 4, 1, 71),
		new TabSymbol("51", 5, 1, 72),
		new TabSymbol("61", 6, 1, 73),
		new TabSymbol("71", 7, 1, 74),
		new TabSymbol("81", 8, 1, 75),
		new TabSymbol("91", 9, 1, 76),
	});

	// spanishTab
	public static List<TabSymbol> spanishTabContent = Arrays.asList(new TabSymbol[]{
		new TabSymbol("07", 0, 7, 41),    
		new TabSymbol("17", 1, 7, 42),
		new TabSymbol("27", 2, 7, 43),
		new TabSymbol("37", 3, 7, 44),
		new TabSymbol("47", 4, 7, 45),
		new TabSymbol("57", 5, 7, 46),
		new TabSymbol("67", 6, 7, 47),
		new TabSymbol("77", 7, 7, 48),
		new TabSymbol("87", 8, 7, 49),
		new TabSymbol("97", 9, 7, 50),
		//
		new TabSymbol("06", 0, 6, 43),    
		new TabSymbol("16", 1, 6, 44),
		new TabSymbol("26", 2, 6, 45),
		new TabSymbol("36", 3, 6, 46),
		new TabSymbol("46", 4, 6, 47),
		new TabSymbol("56", 5, 6, 48),
		new TabSymbol("66", 6, 6, 49),
		new TabSymbol("76", 7, 6, 50),
		new TabSymbol("86", 8, 6, 51),
		new TabSymbol("96", 9, 6, 52),
		//
		new TabSymbol("05", 0, 5, 48),
		new TabSymbol("15", 1, 5, 49),
		new TabSymbol("25", 2, 5, 50),
		new TabSymbol("35", 3, 5, 51),
		new TabSymbol("45", 4, 5, 52),
		new TabSymbol("55", 5, 5, 53),
		new TabSymbol("65", 6, 5, 54),
		new TabSymbol("75", 7, 5, 55),
		new TabSymbol("85", 8, 5, 56),
		new TabSymbol("95", 9, 5, 57),
		//
		new TabSymbol("04", 0, 4, 53),
		new TabSymbol("14", 1, 4, 54),
		new TabSymbol("24", 2, 4, 55),
		new TabSymbol("34", 3, 4, 56),
		new TabSymbol("44", 4, 4, 57),
		new TabSymbol("54", 5, 4, 58),
		new TabSymbol("64", 6, 4, 59),
		new TabSymbol("74", 7, 4, 60),
		new TabSymbol("84", 8, 4, 61),
		new TabSymbol("94", 9, 4, 62),
		//
		new TabSymbol("03", 0, 3, 57),
		new TabSymbol("13", 1, 3, 58),
		new TabSymbol("23", 2, 3, 59),
		new TabSymbol("33", 3, 3, 60),
		new TabSymbol("43", 4, 3, 61),
		new TabSymbol("53", 5, 3, 62),
		new TabSymbol("63", 6, 3, 63),
		new TabSymbol("73", 7, 3, 64),
		new TabSymbol("83", 8, 3, 65),
		new TabSymbol("93", 9, 3, 66),
		//
		new TabSymbol("02", 0, 2, 62),
		new TabSymbol("12", 1, 2, 63),
		new TabSymbol("22", 2, 2, 64),
		new TabSymbol("32", 3, 2, 65),
		new TabSymbol("42", 4, 2, 66),
		new TabSymbol("52", 5, 2, 67),
		new TabSymbol("62", 6, 2, 68),
		new TabSymbol("72", 7, 2, 69),
		new TabSymbol("82", 8, 2, 70),
		new TabSymbol("92", 9, 2, 71),
		//
		new TabSymbol("01", 0, 1, 67),
		new TabSymbol("11", 1, 1, 68),
		new TabSymbol("21", 2, 1, 69),
		new TabSymbol("31", 3, 1, 70),
		new TabSymbol("41", 4, 1, 71),
		new TabSymbol("51", 5, 1, 72),
		new TabSymbol("61", 6, 1, 73),
		new TabSymbol("71", 7, 1, 74),
		new TabSymbol("81", 8, 1, 75),
		new TabSymbol("91", 9, 1, 76)
	});

}
