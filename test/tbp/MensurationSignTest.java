package tbp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MensurationSignTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testMakeVariant() {
		List<MensurationSign> expected = Arrays.asList(new MensurationSign[]{
			new MensurationSign("M3", "3", new Integer[]{3, 4}), // M3, beatUnit same, staffLine same  (== Symbol.THREE)
			new MensurationSign("M34", "3", new Integer[]{3, 4}), // M3, beatUnit same, staffLine diff
			new MensurationSign("M3:1", "3", new Integer[]{3, 1}), // M3, beatUnit diff, staffLine same
			new MensurationSign("M3:14", "3", new Integer[]{3, 1}), // M3, beatUnit diff, staffLine diff
			new MensurationSign("MC\\", "\u00A2", new Integer[]{2, 2}), // MC\, beatUnit same, staffLine same  (== Symbol.CUT_C)
			new MensurationSign("MC\\4", "\u00A2", new Integer[]{2, 2}), // MC\, beatUnit same, staffLine diff
			new MensurationSign("MC\\:1", "\u00A2", new Integer[]{2, 1}), // MC\, beatUnit diff, staffLine same
			new MensurationSign("MC\\:14", "\u00A2", new Integer[]{2, 1}), // MC\, beatUnit diff, staffLine diff
		});

		List<MensurationSign> actual = new ArrayList<>();
		actual.add(Symbol.THREE.makeVariant(-1, -1));
		actual.add(Symbol.THREE.makeVariant(-1, 4));
		actual.add(Symbol.THREE.makeVariant(1, -1));
		actual.add(Symbol.THREE.makeVariant(1, 4));
		actual.add(Symbol.CUT_C.makeVariant(-1, -1));
		actual.add(Symbol.CUT_C.makeVariant(-1, 4));
		actual.add(Symbol.CUT_C.makeVariant(1, -1));
		actual.add(Symbol.CUT_C.makeVariant(1, 4));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}

}