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


	@Test
	public void testMakeVariant() {
		List<RhythmSymbol> expected = Arrays.asList(new RhythmSymbol[]{
			new RhythmSymbol("sb*", "H", 72), // sb dotted 
			new RhythmSymbol("mi**", "Q", 42), // mi double dotted
			new RhythmSymbol("sm-", "E", 12), // sm beamed
			new RhythmSymbol("fu*-", "S", 9), // fu dotted and beamed
			new RhythmSymbol("tr[sb", "H", 32), // sb tripletised
			new RhythmSymbol("trsb", "H", 32),
			new RhythmSymbol("tr]sb", "H", 32),
			new RhythmSymbol("tr[mi*", "Q", 24), // mi tripletised and dotted
			new RhythmSymbol("trmi*", "Q", 24),
			new RhythmSymbol("tr]mi*", "Q", 24),
			new RhythmSymbol("tr[sm-", "E", 8), // sm tripletised and beamed
			new RhythmSymbol("trsm-", "E", 8),
			new RhythmSymbol("tr]sm-", "E", 8),
			new RhythmSymbol("tr[fu*-", "S", 6), // fu tripletised, dotted, and beamed
			new RhythmSymbol("trfu*-", "S", 6),
			new RhythmSymbol("tr]fu*-", "S", 6),
			new RhythmSymbol("tr[sb*", "H", 48), // sb dotted tripletised
			new RhythmSymbol("trsb*", "H", 48),
			new RhythmSymbol("tr]sb*", "H", 48)
		});

		List<RhythmSymbol> actual = new ArrayList<>();
		actual.addAll(Symbol.SEMIBREVIS.makeVariant(true, false, false, 1));
		actual.addAll(Symbol.MINIM.makeVariant(true, false,  false, 2));
		actual.addAll(Symbol.SEMIMINIM.makeVariant(false, true, false, 0));
		actual.addAll(Symbol.FUSA.makeVariant(true, true, false, 1));
		actual.addAll(Symbol.SEMIBREVIS.makeVariant(false, false, true, 0));
		actual.addAll(Symbol.MINIM.makeVariant(true, false, true, 1));
		actual.addAll(Symbol.SEMIMINIM.makeVariant(false, true, true, 0));
		actual.addAll(Symbol.FUSA.makeVariant(true, true, true, 1));
		actual.addAll(new RhythmSymbol("sb*", "H", 72).makeVariant(false, false, true, 1));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}

}