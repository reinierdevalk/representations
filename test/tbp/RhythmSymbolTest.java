package tbp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RhythmSymbolTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


//	@Test
//	public void testMakeNumberOfDots() {
//		RhythmSymbol none = new RhythmSymbol("sb", "H", 48);
//		RhythmSymbol one = new RhythmSymbol("sb*", "H", 72);
//		RhythmSymbol two = new RhythmSymbol("sb**", "H", 84);
//		RhythmSymbol alsoTwo = none.makeVariant(true, false, false, 2, null);
//
//		List<Integer> expected = Arrays.asList(new Integer[]{0, 1, 2, 2});
//		List<Integer> actual = new ArrayList<>();
//		for (RhythmSymbol r : Arrays.asList(new RhythmSymbol[]{none, one, two, alsoTwo})) {
//			actual.add(RhythmSymbol.makeNumberOfDots(r.getEncoding()));
//		}
//
//		assertEquals(expected, actual);
//	}


	@Test
	public void testMakeVariant() {
		List<RhythmSymbol> expected = Arrays.asList(new RhythmSymbol[]{
			new RhythmSymbol("sb*", "H", 72), // sb dotted 
			new RhythmSymbol("mi**", "Q", 42), // mi double dotted
			new RhythmSymbol("sm-", "E", 12), // sm beamed
			new RhythmSymbol("fu*-", "S", 9), // fu dotted and beamed
			new RhythmSymbol("trsb", "H", 32), // sb tripletised (mid)
			new RhythmSymbol("tr[mi*", "Q", 24), // mi tripletised (open) and dotted
			new RhythmSymbol("tr]sm-", "E", 8), // sm tripletised (close) and beamed
			new RhythmSymbol("trfu*-", "S", 6), // fu tripletised (mid), dotted, and beamed
			new RhythmSymbol("trsb*", "H", 48), // sb dotted tripletised (mid)
		});

		List<RhythmSymbol> actual = new ArrayList<>();
		actual.add(Symbol.SEMIBREVIS.makeVariant(true, false, false, 1, null));
		actual.add(Symbol.MINIM.makeVariant(true, false,  false, 2, null));
		actual.add(Symbol.SEMIMINIM.makeVariant(false, true, false, 0, null));
		actual.add(Symbol.FUSA.makeVariant(true, true, false, 1, null));
		actual.add(Symbol.SEMIBREVIS.makeVariant(false, false, true, 0, ""));
		actual.add(Symbol.MINIM.makeVariant(true, false, true, 1, "open"));
		actual.add(Symbol.SEMIMINIM.makeVariant(false, true, true, 0, "close"));
		actual.add(Symbol.FUSA.makeVariant(true, true, true, 1, ""));
		actual.add(new RhythmSymbol("sb*", "H", 72).makeVariant(false, false, true, 1, ""));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}

}
