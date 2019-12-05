package analysis;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AnalyserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetScientificNotation() {
		List<Integer> testVals = Arrays.asList(new Integer[]{
			12, // C0
			12+1, 12+2, // C#0, D0 
			24+3, 24+4, // Eb1, E1
			36+5, 36+6, // F2, F#2
			48+7, // G3
			60+8, // Ab4
			72+9, // A5
			84+10, // Bb6
			96+11, // B7
			108, // C8
		});
		
		List<String> expected = Arrays.asList(new String[]{
			"C0", "C#0", "D0", "Eb1", "E1", "F2", "F#2", "G3", "Ab4", "A5", "Bb6", "B7", "C8"
		});
		
		List<String> actual = new ArrayList<>();
		for (int i : testVals) {
			actual.add(Analyser.getScientificNotation(i));
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}

}
