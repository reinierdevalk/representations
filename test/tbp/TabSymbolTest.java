package tbp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import representations.Tablature.Tuning;
import tbp.TabSymbol.TabSymbolSet;

public class TabSymbolTest {

	@Before
	public void setUp() throws Exception {
	}


	@After
	public void tearDown() throws Exception {
	}


	private List<TabSymbol> getOtherCoursesGerman() {
		return Arrays.asList(new TabSymbol[]{
			new TabSymbol("5", "5", 0, 1),
			new TabSymbol("e", "e", 1, 1),
			new TabSymbol("k", "k", 2, 1),
			new TabSymbol("p", "p", 3, 1),
			new TabSymbol("v", "v", 4, 1),
			new TabSymbol("9", "9", 5, 1),
			new TabSymbol("e-", "e-", 6, 1),
			new TabSymbol("k-", "k-", 7, 1),
			new TabSymbol("p-", "p-", 8, 1),
			new TabSymbol("v-", "v-", 9, 1),
			new TabSymbol("9-", "9-", 10, 1),
			//
			new TabSymbol("4", "4", 0, 2),
			new TabSymbol("d", "d", 1, 2),
			new TabSymbol("i", "i", 2, 2),
			new TabSymbol("o", "o", 3, 2),
			new TabSymbol("t", "t", 4, 2),
			new TabSymbol("7", "7", 5, 2),
			new TabSymbol("d-", "d-", 6, 2),
			new TabSymbol("i-", "i-", 7, 2),
			new TabSymbol("o-", "o-", 8, 2),
			new TabSymbol("t-", "t-", 9, 2),
			new TabSymbol("7-", "7-", 10, 2),
			//
			new TabSymbol("3", "3", 0, 3),
			new TabSymbol("c", "c", 1, 3),
			new TabSymbol("h", "h", 2, 3),
			new TabSymbol("n", "n", 3, 3),
			new TabSymbol("s", "s", 4, 3),
			new TabSymbol("z", "z", 5, 3),
			new TabSymbol("c-", "c-", 6, 3),
			new TabSymbol("h-", "h-", 7, 3),
			new TabSymbol("n-", "n-", 8, 3),
			new TabSymbol("s-", "s-", 9, 3),
			new TabSymbol("z-", "z-", 10, 3),
			//
			new TabSymbol("2", "2", 0, 4),
			new TabSymbol("b", "b", 1, 4),
			new TabSymbol("g", "g", 2, 4),
			new TabSymbol("m", "m", 3, 4),
			new TabSymbol("r", "r", 4, 4),
			new TabSymbol("y", "y", 5, 4),
			new TabSymbol("b-", "b-", 6, 4),
			new TabSymbol("g-", "g-", 7, 4),
			new TabSymbol("m-", "m-", 8, 4),
			new TabSymbol("r-", "r-", 9, 4),
			new TabSymbol("y-", "y-", 10, 4),
			//
			new TabSymbol("1", "1", 0, 5),
			new TabSymbol("a", "a", 1, 5),
			new TabSymbol("f", "f", 2, 5),
			new TabSymbol("l", "l", 3, 5),
			new TabSymbol("q", "q", 4, 5),
			new TabSymbol("x", "x", 5, 5),
			new TabSymbol("a-", "a-", 6, 5),
			new TabSymbol("f-", "f-", 7, 5),
			new TabSymbol("l-", "l-", 8, 5),
			new TabSymbol("q-", "q-", 9, 5),
			new TabSymbol("x-", "x-", 10, 5)
		});
	}


	@Test
	public void testListTabSymbols() {
		List<TabSymbol> expected = new ArrayList<>();
		List<TabSymbol> expectedFrench = Arrays.asList(new TabSymbol[]{
			new TabSymbol("a1", "a", 0, 1),
			new TabSymbol("b1", "b", 1, 1),
			new TabSymbol("c1", "c", 2, 1),
			new TabSymbol("d1", "d", 3, 1),
			new TabSymbol("e1", "e", 4, 1),
			new TabSymbol("f1", "f", 5, 1),
			new TabSymbol("g1", "g", 6, 1),
			new TabSymbol("h1", "h", 7, 1),
			new TabSymbol("i1", "i", 8, 1),
			new TabSymbol("k1", "k", 9, 1),
			new TabSymbol("l1", "l", 10, 1),
			//
			new TabSymbol("a2", "a", 0, 2),
			new TabSymbol("b2", "b", 1, 2),
			new TabSymbol("c2", "c", 2, 2),
			new TabSymbol("d2", "d", 3, 2),
			new TabSymbol("e2", "e", 4, 2),
			new TabSymbol("f2", "f", 5, 2),
			new TabSymbol("g2", "g", 6, 2),
			new TabSymbol("h2", "h", 7, 2),
			new TabSymbol("i2", "i", 8, 2),
			new TabSymbol("k2", "k", 9, 2),
			new TabSymbol("l2", "l", 10, 2),
			//
			new TabSymbol("a3", "a", 0, 3),
			new TabSymbol("b3", "b", 1, 3),
			new TabSymbol("c3", "c", 2, 3),
			new TabSymbol("d3", "d", 3, 3),
			new TabSymbol("e3", "e", 4, 3),
			new TabSymbol("f3", "f", 5, 3),
			new TabSymbol("g3", "g", 6, 3),
			new TabSymbol("h3", "h", 7, 3),
			new TabSymbol("i3", "i", 8, 3),
			new TabSymbol("k3", "k", 9, 3),
			new TabSymbol("l3", "l", 10, 3),
			//
			new TabSymbol("a4", "a", 0, 4),
			new TabSymbol("b4", "b", 1, 4),
			new TabSymbol("c4", "c", 2, 4),
			new TabSymbol("d4", "d", 3, 4),
			new TabSymbol("e4", "e", 4, 4),
			new TabSymbol("f4", "f", 5, 4),
			new TabSymbol("g4", "g", 6, 4),
			new TabSymbol("h4", "h", 7, 4),
			new TabSymbol("i4", "i", 8, 4),
			new TabSymbol("k4", "k", 9, 4),
			new TabSymbol("l4", "l", 10, 4),
			//
			new TabSymbol("a5", "a", 0, 5),
			new TabSymbol("b5", "b", 1, 5),
			new TabSymbol("c5", "c", 2, 5),
			new TabSymbol("d5", "d", 3, 5),
			new TabSymbol("e5", "e", 4, 5),
			new TabSymbol("f5", "f", 5, 5),
			new TabSymbol("g5", "g", 6, 5),
			new TabSymbol("h5", "h", 7, 5),
			new TabSymbol("i5", "i", 8, 5),
			new TabSymbol("k5", "k", 9, 5),
			new TabSymbol("l5", "l", 10, 5),
			//
			new TabSymbol("a6", "a", 0, 6),    
			new TabSymbol("b6", "b", 1, 6),
			new TabSymbol("c6", "c", 2, 6),
			new TabSymbol("d6", "d", 3, 6),
			new TabSymbol("e6", "e", 4, 6),
			new TabSymbol("f6", "f", 5, 6),
			new TabSymbol("g6", "g", 6, 6),
			new TabSymbol("h6", "h", 7, 6),
			new TabSymbol("i6", "i", 8, 6),
			new TabSymbol("k6", "k", 9, 6),
			new TabSymbol("l6", "l", 10, 6),
			//
			new TabSymbol("a7", "a", 0, 7),    
			new TabSymbol("b7", "b", 1, 7),
			new TabSymbol("c7", "c", 2, 7),
			new TabSymbol("d7", "d", 3, 7),
			new TabSymbol("e7", "e", 4, 7),
			new TabSymbol("f7", "f", 5, 7),
			new TabSymbol("g7", "g", 6, 7),
			new TabSymbol("h7", "h", 7, 7),
			new TabSymbol("i7", "i", 8, 7),
			new TabSymbol("k7", "k", 9, 7),
			new TabSymbol("l7", "l", 10, 7),
			//
			new TabSymbol("a8", "a", 0, 8),    
			new TabSymbol("b8", "b", 1, 8),
			new TabSymbol("c8", "c", 2, 8),
			new TabSymbol("d8", "d", 3, 8),
			new TabSymbol("e8", "e", 4, 8),
			new TabSymbol("f8", "f", 5, 8),
			new TabSymbol("g8", "g", 6, 8),
			new TabSymbol("h8", "h", 7, 8),
			new TabSymbol("i8", "i", 8, 8),
			new TabSymbol("k8", "k", 9, 8),
			new TabSymbol("l8", "l", 10, 8),
		});
		expected.addAll(expectedFrench);

		List<TabSymbol> expectedItalianSpanish = Arrays.asList(new TabSymbol[]{
			new TabSymbol("01", "0", 0, 1),
			new TabSymbol("11", "1", 1, 1),
			new TabSymbol("21", "2", 2, 1),
			new TabSymbol("31", "3", 3, 1),
			new TabSymbol("41", "4", 4, 1),
			new TabSymbol("51", "5", 5, 1),
			new TabSymbol("61", "6", 6, 1),
			new TabSymbol("71", "7", 7, 1),
			new TabSymbol("81", "8", 8, 1),
			new TabSymbol("91", "9", 9, 1),
			new TabSymbol("101", "10", 10, 1),
			//
			new TabSymbol("02", "0", 0, 2),
			new TabSymbol("12", "1", 1, 2),
			new TabSymbol("22", "2", 2, 2),
			new TabSymbol("32", "3", 3, 2),
			new TabSymbol("42", "4", 4, 2),
			new TabSymbol("52", "5", 5, 2),
			new TabSymbol("62", "6", 6, 2),
			new TabSymbol("72", "7", 7, 2),
			new TabSymbol("82", "8", 8, 2),
			new TabSymbol("92", "9", 9, 2),
			new TabSymbol("102", "10", 10, 2),
			//
			new TabSymbol("03", "0", 0, 3),
			new TabSymbol("13", "1", 1, 3),
			new TabSymbol("23", "2", 2, 3),
			new TabSymbol("33", "3", 3, 3),
			new TabSymbol("43", "4", 4, 3),
			new TabSymbol("53", "5", 5, 3),
			new TabSymbol("63", "6", 6, 3),
			new TabSymbol("73", "7", 7, 3),
			new TabSymbol("83", "8", 8, 3),
			new TabSymbol("93", "9", 9, 3),
			new TabSymbol("103", "10", 10, 3),
			//
			new TabSymbol("04", "0", 0, 4),
			new TabSymbol("14", "1", 1, 4),
			new TabSymbol("24", "2", 2, 4),
			new TabSymbol("34", "3", 3, 4),
			new TabSymbol("44", "4", 4, 4),
			new TabSymbol("54", "5", 5, 4),
			new TabSymbol("64", "6", 6, 4),
			new TabSymbol("74", "7", 7, 4),
			new TabSymbol("84", "8", 8, 4),
			new TabSymbol("94", "9", 9, 4),
			new TabSymbol("104", "10", 10, 4),
			//
			new TabSymbol("05", "0", 0, 5),
			new TabSymbol("15", "1", 1, 5),
			new TabSymbol("25", "2", 2, 5),
			new TabSymbol("35", "3", 3, 5),
			new TabSymbol("45", "4", 4, 5),
			new TabSymbol("55", "5", 5, 5),
			new TabSymbol("65", "6", 6, 5),
			new TabSymbol("75", "7", 7, 5),
			new TabSymbol("85", "8", 8, 5),
			new TabSymbol("95", "9", 9, 5),
			new TabSymbol("105", "10", 10, 5),
			//
			new TabSymbol("06", "0", 0, 6),    
			new TabSymbol("16", "1", 1, 6),
			new TabSymbol("26", "2", 2, 6),
			new TabSymbol("36", "3", 3, 6),
			new TabSymbol("46", "4", 4, 6),
			new TabSymbol("56", "5", 5, 6),
			new TabSymbol("66", "6", 6, 6),
			new TabSymbol("76", "7", 7, 6),
			new TabSymbol("86", "8", 8, 6),
			new TabSymbol("96", "9", 9, 6),
			new TabSymbol("106", "10", 10, 6),
			//
			new TabSymbol("07", "0", 0, 7),    
			new TabSymbol("17", "1", 1, 7),
			new TabSymbol("27", "2", 2, 7),
			new TabSymbol("37", "3", 3, 7),
			new TabSymbol("47", "4", 4, 7),
			new TabSymbol("57", "5", 5, 7),
			new TabSymbol("67", "6", 6, 7),
			new TabSymbol("77", "7", 7, 7),
			new TabSymbol("87", "8", 8, 7),
			new TabSymbol("97", "9", 9, 7),
			new TabSymbol("107", "10", 10, 7),
			//
			new TabSymbol("08", "0", 0, 8),    
			new TabSymbol("18", "1", 1, 8),
			new TabSymbol("28", "2", 2, 8),
			new TabSymbol("38", "3", 3, 8),
			new TabSymbol("48", "4", 4, 8),
			new TabSymbol("58", "5", 5, 8),
			new TabSymbol("68", "6", 6, 8),
			new TabSymbol("78", "7", 7, 8),
			new TabSymbol("88", "8", 8, 8),
			new TabSymbol("98", "9", 9, 8),
			new TabSymbol("108", "10", 10, 8)
		});
		expected.addAll(expectedItalianSpanish);
		expected.addAll(expectedItalianSpanish);

		List<TabSymbol> expectedJudenkuenig = new ArrayList<TabSymbol>(getOtherCoursesGerman());
		expectedJudenkuenig.addAll(Arrays.asList(new TabSymbol[]{
			new TabSymbol("A", "A", 0, 6),    
			new TabSymbol("B", "B", 1, 6),
			new TabSymbol("C", "C", 2, 6),
			new TabSymbol("D", "D", 3, 6),
			new TabSymbol("E", "E", 4, 6),
			new TabSymbol("F", "F", 5, 6),
			new TabSymbol("G", "G", 6, 6),
			new TabSymbol("H", "H", 7, 6)
		}));
		expected.addAll(expectedJudenkuenig);

		List<TabSymbol> expectedNewsidler = new ArrayList<TabSymbol>(getOtherCoursesGerman());
		expectedNewsidler.addAll(Arrays.asList(new TabSymbol[]{
			new TabSymbol("+", "+", 0, 6),
			new TabSymbol("A", "A", 1, 6),
			new TabSymbol("B", "B", 2, 6),
			new TabSymbol("C", "C", 3, 6),
			new TabSymbol("D", "D", 4, 6),
			new TabSymbol("E", "E", 5, 6),
			new TabSymbol("F", "F", 6, 6),
			new TabSymbol("G", "G", 7, 6),
			new TabSymbol("H", "H", 8, 6)
		}));
		expected.addAll(expectedNewsidler);

		List<TabSymbol> expectedOchsenkun = new ArrayList<TabSymbol>(getOtherCoursesGerman());
		expectedOchsenkun.addAll(Arrays.asList(new TabSymbol[]{
			new TabSymbol("+", "+", 0, 6),    
			new TabSymbol("2-", "2-", 1, 6),
			new TabSymbol("3-", "3-", 2, 6),
			new TabSymbol("4-", "4-", 3, 6),
			new TabSymbol("5-", "5-", 4, 6),
			new TabSymbol("6-", "6-", 5, 6),
			new TabSymbol("7-", "7-", 6, 6),
			new TabSymbol("8-", "8-", 7, 6),
			new TabSymbol("9-", "9-", 8, 6),
			new TabSymbol("10-", "10-", 9, 6),
			new TabSymbol("11-", "11-", 10, 6)
		}));
		expected.addAll(expectedOchsenkun);

		List<TabSymbol> expectedHeckel = new ArrayList<TabSymbol>(getOtherCoursesGerman());
		expectedHeckel.addAll(Arrays.asList(new TabSymbol[]{
			new TabSymbol("+", "+", 0, 6),    
			new TabSymbol("A-", "A-", 1, 6),
			new TabSymbol("F-", "F-", 2, 6),
			new TabSymbol("L-", "L-", 3, 6),
			new TabSymbol("Q-", "Q-", 4, 6),
			new TabSymbol("X-", "X-", 5, 6)
		}));
		expected.addAll(expectedHeckel);

		List<TabSymbol> actual = new ArrayList<>();
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.FRENCH));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.ITALIAN));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.SPANISH));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.JUDENKUENIG_1523));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.NEWSIDLER_1536));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.OCHSENKUN_1558));
		actual.addAll(TabSymbol.listTabSymbols(TabSymbolSet.HECKEL_1562));
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	@Test
	public void testMakeVariant() {
		List<TabSymbol> expected = Arrays.asList(new TabSymbol[]{
			new TabSymbol("f1" + TabSymbol.FINGERING_DOT_ENCODING, "f", 5, 1), // French
			new TabSymbol("+" + TabSymbol.FINGERING_DOT_ENCODING.repeat(1), "+", 0, 6), // Ochsenkun1558
			new TabSymbol("27" + TabSymbol.FINGERING_DOT_ENCODING.repeat(2), "2", 2, 7), // Spanish
			new TabSymbol("08" + TabSymbol.FINGERING_DOT_ENCODING.repeat(3), "0", 0, 8) // Italian 
		});
		
		List<TabSymbol> actual = new ArrayList<>();
		actual.add(Symbol.getTabSymbol("f1", TabSymbolSet.FRENCH).makeVariant(1));
		actual.add(Symbol.getTabSymbol("+", TabSymbolSet.OCHSENKUN_1558).makeVariant(1));
		actual.add(Symbol.getTabSymbol("27", TabSymbolSet.SPANISH).makeVariant(2));
		actual.add(Symbol.getTabSymbol("08", TabSymbolSet.ITALIAN).makeVariant(3));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	@Test
	public void testGetPitch() {
		TabSymbol ts1 = new TabSymbol("f1", "f", 5, 1); // French, G
		TabSymbol ts2 = new TabSymbol("+", "+", 0, 6); // Ochsenkun1558, A6G
		TabSymbol ts3 = new TabSymbol("27", "2", 2, 7); // Spanish, G7D
		TabSymbol ts4 = new TabSymbol("08", "0", 0, 8); // Italian, A8

		List<Integer> expected = Arrays.asList(new Integer[]{72, 43, 40, 40});
		List<Integer> actual = new ArrayList<>();
		actual.add(ts1.getPitch(Tuning.G));
		actual.add(ts2.getPitch(Tuning.A6G));
		actual.add(ts3.getPitch(Tuning.G7D));
		actual.add(ts4.getPitch(Tuning.A8));

		assertEquals(expected, actual);
	}

}