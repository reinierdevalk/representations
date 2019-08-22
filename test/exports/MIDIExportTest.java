package exports;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import representations.Transcription;

public class MIDIExportTest extends TestCase {

	private File midiTestGetMeterInfo; // = new File(Runner.midiPathTest + "test_get_meter_info.mid");
	
	protected void setUp() throws Exception {
		super.setUp();
//		Runner.setPathsToCodeAndData(UI.getRootDir(), false);
//		midiTestGetMeterInfo = new File(Runner.midiPathTest + "test_get_meter_key_info.mid");
		midiTestGetMeterInfo = new File(MEIExport.rootDir + "data/MIDI/test/" + "test_get_meter_key_info.mid");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testGetTimeSigTicks() {
		Transcription t = new Transcription(midiTestGetMeterInfo, null);
		
		List<Integer[]> expected = new ArrayList<Integer[]>();
		expected.add(new Integer[]{0, 384});
		expected.add(new Integer[]{384, 2432});
		expected.add(new Integer[]{2432, 3968});
		expected.add(new Integer[]{3968, 6016});
		expected.add(new Integer[]{6016, 6336});
		expected.add(new Integer[]{6336, 7360});
		
		List<Integer[]> actual = MIDIExport.getTimeSigTicks(t.getMeterInfo(), 256);
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}

}
