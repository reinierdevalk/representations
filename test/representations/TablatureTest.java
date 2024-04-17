package representations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.uos.fmt.musitech.data.performance.MidiNote;
import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import path.Path;
import representations.Tablature.Tuning;
import tbp.Encoding;
import tbp.Encoding.Stage;
import tbp.Symbol;
import tbp.TabSymbol;
import tbp.TabSymbol.TabSymbolSet;
import tools.ToolBox;

public class TablatureTest extends TestCase {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File encodingNewsidler;
	private File encodingNewsidlerCumSancto;
	private File encodingBarbetta;
	private File encodingNarvaez;

	private File midiTestpiece;
	private static final int TRANSP_INT = -2;
	private static final Rational THIRTY_SECOND = new Rational(1, 32);
	private static final Rational SIXTEENTH = new Rational(1, 16);
	private static final Rational EIGHTH = new Rational(1, 8);
	private static final Rational DOTTED_EIGHTH = new Rational(3, 16);
	private static final Rational QUARTER = new Rational(1, 4);


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String root = Path.ROOT_PATH_DEPLOYMENT_DEV;
		encodingTestpiece = new File(
			root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "testpiece.tbp"
		);
		encodingTestGetMeterInfo = new File(
			root + Path.ENCODINGS_REL_PATH + Path.TEST_DIR + "test_get_meter_info.tbp"
		);
		encodingNewsidler = new File(
			root + Path.ENCODINGS_REL_PATH + "/thesis-int/3vv/" + "newsidler-1544_2-nun_volget.tbp"
		);
		encodingNewsidlerCumSancto = new File(
			root + Path.ENCODINGS_REL_PATH_JOSQUINTAB + "4471_40_cum_sancto_spiritu.tbp"
		);
		encodingBarbetta = new File(
			root + Path.ENCODINGS_REL_PATH + "/thesis-int/4vv/" + "barbetta-1582_1-il_nest-corrected.tbp"
		);
		encodingNarvaez = new File(
			root + Path.ENCODINGS_REL_PATH_JOSQUINTAB + "5190_17_cum_spiritu_sanctu_from_missa_sine_nomine.tbp"
		);
		midiTestpiece = new File(
			root + Path.MIDI_REL_PATH + Path.TEST_DIR + "testpiece.mid"
		);
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	private Integer[][] getBtp(boolean normalise) {
		// For testpiece
		Integer[][] btp = new Integer[39][10];
		// Chord 0
		btp[0] = new Integer[]{50, 5, 0, 72, 24, 48, 0, 4, 0, 3};
		btp[1] = new Integer[]{57, 4, 2, 72, 24, 24, 0, 4, 1, 3};
		btp[2] = new Integer[]{65, 2, 1, 72, 24, 24, 0, 4, 2, 3};
		btp[3] = new Integer[]{69, 1, 0, 72, 24, 24, 0, 4, 3, 3};
		// Chord 1
		btp[4] = new Integer[]{45, 6, 0, 96, 18, 18, 1, 4, 0, 5};
		btp[5] = new Integer[]{57, 4, 2, 96, 18, 24, 1, 4, 1, 5};
		btp[6] = new Integer[]{72, 2, 8, 96, 18, 24, 1, 4, 2, 5};
		btp[7] = new Integer[]{69, 1, 0, 96, 18, 72, 1, 4, 3, 5};
		// Chord 2
		btp[8] = new Integer[]{48, 6, 3, 114, 6, 6, 2, 1, 0, 6};
		// Chord 3
		btp[9] = new Integer[]{47, 6, 2, 120, 12, 12, 3, 4, 0, 7};
		btp[10] = new Integer[]{50, 5, 0, 120, 12, 24, 3, 4, 1, 7};
		btp[11] = new Integer[]{59, 4, 4, 120, 12, 24, 3, 4, 2, 7};
		btp[12] = new Integer[]{65, 2, 1, 120, 12, 24, 3, 4, 3, 7};
		// Chord 4
		btp[13] = new Integer[]{45, 6, 0, 132, 12, 12, 4, 1, 0, 8};
		// Chord 5
		btp[14] = new Integer[]{45, 6, 0, 144, 24, 24, 5, 5, 0, 9};
		btp[15] = new Integer[]{57, 5, 7, 144, 24, 144, 5, 5, 1, 9};
		btp[16] = new Integer[]{57, 4, 2, 144, 24, 48, 5, 5, 2, 9};
		btp[17] = new Integer[]{60, 3, 1, 144, 24, 24, 5, 5, 3, 9};
		btp[18] = new Integer[]{69, 2, 5, 144, 24, 24, 5, 5, 4, 9};
		// Chord 6
		btp[19] = new Integer[]{45, 6, 0, 168, 12, 24, 6, 4, 0, 10};
		btp[20] = new Integer[]{60, 3, 1, 168, 12, 12, 6, 4, 1, 10};
		btp[21] = new Integer[]{64, 2, 0, 168, 12, 12, 6, 4, 2, 10};
		btp[22] = new Integer[]{69, 1, 0, 168, 12, 24, 6, 4, 3, 10};
		// Chord 7
		btp[23] = new Integer[]{59, 3, 0, 180, 12, 108, 7, 2, 0, 11};
		btp[24] = new Integer[]{68, 2, 4, 180, 12, 12, 7, 2, 1, 11};
		// Chord 8
		btp[25] = new Integer[]{45, 6, 0, 192, 6, 72, 8, 4, 0, 14};
		btp[26] = new Integer[]{57, 4, 2, 192, 6, 72, 8, 4, 1, 14};
		btp[27] = new Integer[]{64, 2, 0, 192, 6, 6, 8, 4, 2, 14};
		btp[28] = new Integer[]{69, 1, 0, 192, 6, 12, 8, 4, 3, 14};
		// Chord 9-14
		btp[29] = new Integer[]{68, 2, 4, 198, 6, 9, 9, 1, 0, 15};
		btp[30] = new Integer[]{69, 1, 0, 204, 3, 12, 10, 1, 0, 16};
		btp[31] = new Integer[]{68, 2, 4, 207, 3, 3, 11, 1, 0, 17};
		btp[32] = new Integer[]{66, 2, 2, 210, 3, 3, 12, 1, 0, 19};
		btp[33] = new Integer[]{68, 2, 4, 213, 3, 51, 13, 1, 0, 20};
		btp[34] = new Integer[]{69, 1, 0, 216, 24, 48, 14, 1, 0, 21};
		// Chord 15
		btp[35] = new Integer[]{45, 6, 0, 264, 24, 24, 15, 4, 0, 23};
		btp[36] = new Integer[]{57, 4, 2, 264, 24, 24, 15, 4, 1, 23};
		btp[37] = new Integer[]{64, 2, 0, 264, 24, 24, 15, 4, 2, 23};
		btp[38] = new Integer[]{69, 1, 0, 264, 24, 24, 15, 4, 3, 23};

		Integer[][] btpNormalised = new Integer[btp.length][btp[0].length];
		for (int i = 0; i < btp.length; i++) {
			for (int j = 0; j < btp[i].length; j++) {
				btpNormalised[i][j] = 
					(j == Tablature.PITCH) ? btp[i][j] + TRANSP_INT : btp[i][j];
			}
		}
		return !normalise ? btp : btpNormalised;
	}


	private List<List<Integer>> getPitchesInChord(boolean normalise) {
		// For testpiece
		List<List<Integer>> pitchesInChord = new ArrayList<List<Integer>>();
		pitchesInChord.add(Arrays.asList(new Integer[]{50, 57, 65, 69})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{45, 57, 72, 69})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{48})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{47, 50, 59, 65})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{45})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{45, 57, 57, 60, 69})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{45, 60, 64, 69})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{59, 68})); 
		pitchesInChord.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));
		pitchesInChord.add(Arrays.asList(new Integer[]{68}));
		pitchesInChord.add(Arrays.asList(new Integer[]{69}));
		pitchesInChord.add(Arrays.asList(new Integer[]{68}));
		pitchesInChord.add(Arrays.asList(new Integer[]{66}));
		pitchesInChord.add(Arrays.asList(new Integer[]{68}));
		pitchesInChord.add(Arrays.asList(new Integer[]{69}));
		pitchesInChord.add(Arrays.asList(new Integer[]{45, 57, 64, 69}));

		List<List<Integer>> pitchesInChordNormalised = new ArrayList<>();
		for (int i = 0; i < pitchesInChord.size(); i++) {
			pitchesInChordNormalised.add(pitchesInChord.get(i).stream().map(p -> 
				p + TRANSP_INT).collect(Collectors.toList()));
//			pitchesInChord.get(i).forEach(p -> p += TRANSP_INT);
//			pitchesInChordTransposed.add(pitchesInChord.get(i));
		}
		return !normalise ? pitchesInChord : pitchesInChordNormalised;
	}


	private List<List<Integer[]>> getOnsetTimesPerChord() {
		List<List<Integer[]>> allOnsetTimes = new ArrayList<>();

		// For testpiece 
		List<Integer[]> onsetTimesTestpiece = new ArrayList<>();
		onsetTimesTestpiece.add(new Integer[]{0, 0});
		onsetTimesTestpiece.add(new Integer[]{72, 1});
		//
		onsetTimesTestpiece.add(new Integer[]{96, 1});
		onsetTimesTestpiece.add(new Integer[]{114, 1});
		onsetTimesTestpiece.add(new Integer[]{120, 1});
		onsetTimesTestpiece.add(new Integer[]{132, 1});
		onsetTimesTestpiece.add(new Integer[]{144, 1});
		onsetTimesTestpiece.add(new Integer[]{168, 1});
		onsetTimesTestpiece.add(new Integer[]{180, 1});
		//
		onsetTimesTestpiece.add(new Integer[]{192, 1});
		onsetTimesTestpiece.add(new Integer[]{198, 1});
		onsetTimesTestpiece.add(new Integer[]{204, 1});
		onsetTimesTestpiece.add(new Integer[]{207, 1});
		onsetTimesTestpiece.add(new Integer[]{210, 1});
		onsetTimesTestpiece.add(new Integer[]{213, 1});
		onsetTimesTestpiece.add(new Integer[]{216, 1});
		onsetTimesTestpiece.add(new Integer[]{240, 0});
		onsetTimesTestpiece.add(new Integer[]{264, 1});

		// For testGetMeterInfo
		List<Integer[]> onsetTimesTestGetMeterInfo = new ArrayList<>();
		onsetTimesTestGetMeterInfo.add(new Integer[]{0, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{12, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{24, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{36, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{72, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{84, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{132, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{156, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{162, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{168, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{171, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{174, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{177, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{180, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{228, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{252, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{264, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{270, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{276, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{300, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{318, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{321, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{324, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{372, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{420, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{468, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{480, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{492, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{504, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{516, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{528, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{540, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{564, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{576, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{582, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{588, 1});
		//
		onsetTimesTestGetMeterInfo.add(new Integer[]{594, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{642, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{654, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{660, 1});
		onsetTimesTestGetMeterInfo.add(new Integer[]{666, 1});

		allOnsetTimes.add(onsetTimesTestpiece);
		allOnsetTimes.add(onsetTimesTestGetMeterInfo);

		return allOnsetTimes;
	}


	private List<List<Integer[]>> getOnsetTimesPerNote() {
		List<List<Integer[]>> allOnsetTimes = new ArrayList<>();
		// For testpiece
		List<Integer[]> onsetTimesTestpiece = new ArrayList<>();
		List<Integer[]> ot1 = getOnsetTimesPerChord().get(0);
		onsetTimesTestpiece.add(ot1.get(0));
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(1)));
		//
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(2)));
		onsetTimesTestpiece.addAll(Collections.nCopies(1, ot1.get(3)));
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(4)));
		onsetTimesTestpiece.addAll(Collections.nCopies(1, ot1.get(5)));
		onsetTimesTestpiece.addAll(Collections.nCopies(5, ot1.get(6)));
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(7)));
		onsetTimesTestpiece.addAll(Collections.nCopies(2, ot1.get(8)));
		// 
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(9)));
		onsetTimesTestpiece.add(ot1.get(10));
		onsetTimesTestpiece.add(ot1.get(11));
		onsetTimesTestpiece.add(ot1.get(12));
		onsetTimesTestpiece.add(ot1.get(13));
		onsetTimesTestpiece.add(ot1.get(14));
		onsetTimesTestpiece.add(ot1.get(15));
		onsetTimesTestpiece.add(ot1.get(16));
		onsetTimesTestpiece.addAll(Collections.nCopies(4, ot1.get(17)));

		// For testGetMeterInfo
		List<Integer[]> onsetTimesTestGetMeterInfo = new ArrayList<>();
		List<Integer[]> ot2 = getOnsetTimesPerChord().get(1);
		onsetTimesTestGetMeterInfo.add(ot2.get(0));
		onsetTimesTestGetMeterInfo.add(ot2.get(1));
		onsetTimesTestGetMeterInfo.add(ot2.get(2));
		//
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(3)));
		onsetTimesTestGetMeterInfo.add(ot2.get(4));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(5)));
		//
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(6)));
		onsetTimesTestGetMeterInfo.add(ot2.get(7));
		onsetTimesTestGetMeterInfo.add(ot2.get(8));
		onsetTimesTestGetMeterInfo.add(ot2.get(9));
		onsetTimesTestGetMeterInfo.add(ot2.get(10));
		onsetTimesTestGetMeterInfo.add(ot2.get(11));
		onsetTimesTestGetMeterInfo.add(ot2.get(12));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(13)));
		//
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(14)));
		onsetTimesTestGetMeterInfo.add(ot2.get(15));
		onsetTimesTestGetMeterInfo.add(ot2.get(16));
		onsetTimesTestGetMeterInfo.add(ot2.get(17));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(18)));
		//
		onsetTimesTestGetMeterInfo.add(ot2.get(19));
		onsetTimesTestGetMeterInfo.add(ot2.get(20));
		onsetTimesTestGetMeterInfo.add(ot2.get(21));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(22)));
		//
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(23)));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(24)));
		//
		onsetTimesTestGetMeterInfo.add(ot2.get(25));
		onsetTimesTestGetMeterInfo.add(ot2.get(26));
		onsetTimesTestGetMeterInfo.add(ot2.get(27));
		onsetTimesTestGetMeterInfo.add(ot2.get(28));
		onsetTimesTestGetMeterInfo.add(ot2.get(29));
		onsetTimesTestGetMeterInfo.add(ot2.get(30));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(31)));
		//
		onsetTimesTestGetMeterInfo.add(ot2.get(32));
		onsetTimesTestGetMeterInfo.add(ot2.get(33));
		onsetTimesTestGetMeterInfo.add(ot2.get(34));
		onsetTimesTestGetMeterInfo.add(ot2.get(35));
		//
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(36)));
		onsetTimesTestGetMeterInfo.add(ot2.get(37));
		onsetTimesTestGetMeterInfo.add(ot2.get(38));
		onsetTimesTestGetMeterInfo.add(ot2.get(39));
		onsetTimesTestGetMeterInfo.addAll(Collections.nCopies(2, ot2.get(40)));
		
		allOnsetTimes.add(onsetTimesTestpiece);
		allOnsetTimes.add(onsetTimesTestGetMeterInfo);
		return allOnsetTimes;
	}


	private List<List<Integer>> getMinimumDurationPerChord() {
		List<List<Integer>> allMinDurs = new ArrayList<>();
		// For testpiece 
		List<Integer> minDursTestpiece = Arrays.asList(new Integer[]{
			24, 
			18, 6, 12, 12, 24, 12, 12, 
			6, 6, 3, 3, 3, 3, 24, 24
		});

		// For testGetMeterInfo
		List<Integer> minDursTestGetMeterInfo = Arrays.asList(new Integer[]{
			12, 12, 12, 
			36, 12, 48, 
			24, 6, 6, 3, 3, 3, 3, 48, 
			24, 12, 6, 6, 24, 
			18, 3, 3, 48, 
			48, 48, 
			12, 12, 12, 12, 12, 12, 24, 
			12, 6, 6, 6, 
			48, 12, 6, 6, 24	
		});

		allMinDurs.add(minDursTestpiece);
		allMinDurs.add(minDursTestGetMeterInfo);

		return allMinDurs;
	}


	private List<List<Integer>> getMinimumDurationPerNote() {
		List<List<Integer>> allMinDurs = new ArrayList<>();
		// For testpiece
		List<Integer> minDursTestpiece = new ArrayList<>();
		List<Integer> md1 = getMinimumDurationPerChord().get(0);
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(0)));
		//
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(1)));
		minDursTestpiece.add(md1.get(2));
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(3)));
		minDursTestpiece.add(md1.get(4));
		minDursTestpiece.addAll(Collections.nCopies(5, md1.get(5)));
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(6)));
		minDursTestpiece.addAll(Collections.nCopies(2, md1.get(7)));
		//
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(8)));
		minDursTestpiece.add(md1.get(9));
		minDursTestpiece.add(md1.get(10));
		minDursTestpiece.add(md1.get(11));
		minDursTestpiece.add(md1.get(12));
		minDursTestpiece.add(md1.get(13));
		minDursTestpiece.add(md1.get(14));
		minDursTestpiece.addAll(Collections.nCopies(4, md1.get(15)));

		// For testGetMeterInfo
		List<Integer> minDursTestGetMeterInfo = new ArrayList<>();
		List<Integer> md2 = getMinimumDurationPerChord().get(1);
		minDursTestGetMeterInfo.add(md2.get(0));
		minDursTestGetMeterInfo.add(md2.get(1));
		minDursTestGetMeterInfo.add(md2.get(2));
		//
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(3)));
		minDursTestGetMeterInfo.add(md2.get(4));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(5)));
		//
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(6)));
		minDursTestGetMeterInfo.add(md2.get(7));
		minDursTestGetMeterInfo.add(md2.get(8));
		minDursTestGetMeterInfo.add(md2.get(9));
		minDursTestGetMeterInfo.add(md2.get(10));
		minDursTestGetMeterInfo.add(md2.get(11));
		minDursTestGetMeterInfo.add(md2.get(12));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(13)));
		//
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(14)));
		minDursTestGetMeterInfo.add(md2.get(15));
		minDursTestGetMeterInfo.add(md2.get(16));
		minDursTestGetMeterInfo.add(md2.get(17));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(18)));
		//
		minDursTestGetMeterInfo.add(md2.get(19));
		minDursTestGetMeterInfo.add(md2.get(20));
		minDursTestGetMeterInfo.add(md2.get(21));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(22)));
		//
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(23)));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(24)));
		//
		minDursTestGetMeterInfo.add(md2.get(25));
		minDursTestGetMeterInfo.add(md2.get(26));
		minDursTestGetMeterInfo.add(md2.get(27));
		minDursTestGetMeterInfo.add(md2.get(28));
		minDursTestGetMeterInfo.add(md2.get(29));
		minDursTestGetMeterInfo.add(md2.get(30));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(31)));
		//
		minDursTestGetMeterInfo.add(md2.get(32));
		minDursTestGetMeterInfo.add(md2.get(33));
		minDursTestGetMeterInfo.add(md2.get(34));
		minDursTestGetMeterInfo.add(md2.get(35));
		//
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(36)));
		minDursTestGetMeterInfo.add(md2.get(37));
		minDursTestGetMeterInfo.add(md2.get(38));
		minDursTestGetMeterInfo.add(md2.get(39));
		minDursTestGetMeterInfo.addAll(Collections.nCopies(2, md2.get(40)));

		allMinDurs.add(minDursTestpiece);
		allMinDurs.add(minDursTestGetMeterInfo);
		return allMinDurs;
	}


	private List<List<TabSymbol>> getChords() {
		// For testpiece
		TabSymbolSet tss = new Tablature(encodingTestpiece).getEncoding().getTabSymbolSet();
		List<List<TabSymbol>> chords = new ArrayList<List<TabSymbol>>();
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a5", tss),
			Symbol.getTabSymbol("c4", tss),
			Symbol.getTabSymbol("b2", tss),
			Symbol.getTabSymbol("a1", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss),
			Symbol.getTabSymbol("c4", tss),
			Symbol.getTabSymbol("i2", tss),
			Symbol.getTabSymbol("a1", tss)	
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("d6", tss)
		})); 
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("c6", tss),
			Symbol.getTabSymbol("a5", tss),
			Symbol.getTabSymbol("e4", tss),
			Symbol.getTabSymbol("b2", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss),
			Symbol.getTabSymbol("h5", tss),
			Symbol.getTabSymbol("c4", tss),
			Symbol.getTabSymbol("b3", tss),
			Symbol.getTabSymbol("f2", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss),
			Symbol.getTabSymbol("b3", tss),
			Symbol.getTabSymbol("a2", tss),
			Symbol.getTabSymbol("a1", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a3", tss),
			Symbol.getTabSymbol("e2", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss),
			Symbol.getTabSymbol("c4", tss),
			Symbol.getTabSymbol("a2", tss),
			Symbol.getTabSymbol("a1", tss)
		}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("e2", tss)}));
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("a1", tss)}));
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("e2", tss)}));
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("c2", tss)}));
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("e2", tss)}));
		chords.add(Arrays.asList(new TabSymbol[]{Symbol.getTabSymbol("a1", tss)}));
		//
		chords.add(Arrays.asList(new TabSymbol[]{
			Symbol.getTabSymbol("a6", tss),
			Symbol.getTabSymbol("c4", tss),
			Symbol.getTabSymbol("a2", tss),
			Symbol.getTabSymbol("a1", tss)
		}));
		return chords;
	}


	public void testMakeMeterInfo() {
		Tablature t1 = new Tablature();
		t1.setEncoding(new Encoding(encodingTestpiece));
		t1.setNormaliseTuning(false);
		t1.setName();
		Tablature t2 = new Tablature();
		t2.setEncoding(new Encoding(encodingTestGetMeterInfo));
		t2.setNormaliseTuning(false);
		t2.setName();
		Tablature t3 = new Tablature();
		t3.setEncoding(new Encoding(encodingNewsidler));
		t3.setNormaliseTuning(false);
		t3.setName();

		List<Integer[]> expected = new ArrayList<Integer[]>();
		// mi provided
		// t1
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});
		// t2
		expected.add(new Integer[]{3, 8, 1, 1, 0, 1, 2});
		expected.add(new Integer[]{2, 2, 2, 3, 3, 8, 2});
		expected.add(new Integer[]{3, 4, 4, 5, 19, 8, 4});
		expected.add(new Integer[]{2, 2, 6, 7, 31, 8, 1});
		expected.add(new Integer[]{5, 16, 8, 8, 47, 8, 1});
		expected.add(new Integer[]{2, 2, 9, 9, 99, 16, -2});
		// Agnostic of mi
		// t1
		expected.add(new Integer[]{2, 2, 1, 2, 0, 1, 1});
		expected.add(new Integer[]{3, 16, 3, 3, 2, 1, 1});
		expected.add(new Integer[]{13, 16, 4, 4, 35, 16, 1});
		// t3
		expected.add(new Integer[]{2, 2, 1, 41, 0, 1, 1});
		expected.add(new Integer[]{3, 4, 42, 49, 41, 1, 1});
		expected.add(new Integer[]{2, 2, 50, 96, 47, 1, 1});

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(t1.makeMeterInfo(false));
		actual.addAll(t2.makeMeterInfo(false));
		actual.addAll(t1.makeMeterInfo(true));
		actual.addAll(t3.makeMeterInfo(true));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeTunings() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();

		Tuning[] expected = new Tuning[]{Tuning.A, Tuning.G};
		Tuning[] actual = t.makeTunings();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	public void testMakeBasicTabSymbolProperties() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();
		t.setTunings();

		Integer[][] expected = getBtp(false);
		Integer[][] actual = t.makeBasicTabSymbolProperties();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}


	public void testGetDurationsAndOnsets() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();
		t.setTunings();
		
		List<List<Integer>> expected = new ArrayList<>(); 
		expected.add(getMinimumDurationPerNote().get(0));
		List<Integer> gridXNotes = new ArrayList<>();
		getOnsetTimesPerNote().get(0).forEach(item -> { if (item[1] != 0) { 
			gridXNotes.add(item[0]);}});
		expected.add(gridXNotes);

		List<List<Integer>> actual = t.getDurationsAndOnsets();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}


	public void testGetMaximumDuration() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();
		t.setTunings();

		List<Integer> expected = new ArrayList<>();
		for (Integer[] in : getBtp(false)) {
			expected.add(in[Tablature.MAX_DURATION]);
		}

		List<String> listOfTabSymbols = 
			t.getEncoding().getListsOfSymbols().get(Encoding.TAB_SYMBOLS_IND);
		List<Integer> gridXOfTabSymbols = new ArrayList<>();
		getOnsetTimesPerNote().get(0).forEach(item -> { if (item[1] != 0) { 
			gridXOfTabSymbols.add(item[0]);}});		
		List<Integer> durOfTabSymbols = getMinimumDurationPerNote().get(0);
		List<Integer> actual = new ArrayList<>();
		for (int i = 0; i < listOfTabSymbols.size(); i++) {
			actual.add(t.getMaximumDuration(durOfTabSymbols, gridXOfTabSymbols, i));	
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);		
	}


	public void testMakeChords() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();
		t.setTunings();
		t.setBasicTabSymbolProperties();
		
		List<List<TabSymbol>> expected = getChords();
		List<List<TabSymbol>> actual = t.makeChords();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testMakeNumberOfNotesPerChord() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setNormaliseTuning(false);
		t.setName();
		t.setMeterInfo();
		t.setTunings();
		t.setBasicTabSymbolProperties();
		t.setChords();
		
		List<Integer> expected = new ArrayList<>();
		getChords().forEach(item -> expected.add(item.size()));
		List<Integer> actual = t.makeNumberOfNotesPerChord();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetPitchesInChordStatic() {
		Tablature tablature = new Tablature(encodingTestpiece);

		List<List<Integer>> expected = getPitchesInChord(false);

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		List<List<TabSymbol>> chords = tablature.getChords();
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		int lowestNoteIndex = 0;
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Tablature.getPitchesInChord(btp, lowestNoteIndex));
			lowestNoteIndex += chords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	public void testGetMinimumDurationOfNote() {
		Tablature tab = new Tablature(encodingTestpiece);
		Transcription trans = new Transcription(midiTestpiece, encodingTestpiece);

		List<List<Rational>> expected = new ArrayList<List<Rational>>();
		List<Rational> expected0 = 
			Arrays.asList(new Rational[]{QUARTER, DOTTED_EIGHTH, EIGHTH, QUARTER, EIGHTH, EIGHTH,
			SIXTEENTH, SIXTEENTH, THIRTY_SECOND, THIRTY_SECOND, THIRTY_SECOND, THIRTY_SECOND, 
			QUARTER, QUARTER});
		List<Rational> expected1 = 
			Arrays.asList(new Rational[]{QUARTER, DOTTED_EIGHTH, EIGHTH, QUARTER, EIGHTH, 
			SIXTEENTH, QUARTER});
		List<Rational> expected2 = 
			Arrays.asList(new Rational[]{QUARTER, DOTTED_EIGHTH, EIGHTH, QUARTER, EIGHTH, EIGHTH,
			SIXTEENTH, QUARTER});
		List<Rational> expected3 = 
			Arrays.asList(new Rational[]{QUARTER, DOTTED_EIGHTH, SIXTEENTH, EIGHTH, QUARTER, 
			SIXTEENTH, QUARTER});
		List<Rational> expected4 = 
			Arrays.asList(new Rational[]{EIGHTH, EIGHTH, QUARTER, EIGHTH});
		expected.add(expected0); 
		expected.add(expected1); 
		expected.add(expected2); 
		expected.add(expected3);
		expected.add(expected4);

		List<List<Rational>> actual = new ArrayList<List<Rational>>();
		Integer[][] btp = tab.getBasicTabSymbolProperties();
		NotationSystem system = trans.getScorePiece().getScore();
		for (int i = 0; i < system.size(); i++) {
			List<Rational> curr = new ArrayList<Rational>();
			NotationVoice nv = system.get(i).get(0);
			for (NotationChord nc: nv) {
				curr.add(Tablature.getMinimumDurationOfNote(btp, nc.get(0)));
			}
			actual.add(curr);
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}		
	}


	public void testConvertTabSymbolToNote() {		
		List<Note> expected = new ArrayList<>();
		Integer[][] btp = getBtp(false);
		for (Integer[] in : btp) {
			Rational currDur = new Rational(in[Tablature.MIN_DURATION], Tablature.SRV_DEN);
			// Determine how often currDur fits in a quarter note 
			double quarterFits = (new Rational(1, 4).div(currDur)).toDouble();
			expected.add(new Note(
				new ScoreNote(
					new ScorePitch(in[Tablature.PITCH]), 
					new Rational(in[Tablature.ONSET_TIME], Tablature.SRV_DEN), 
					currDur
				), 
				MidiNote.convert(new PerformanceNote(
					0, // default instance variable value (?); see PerformanceNote()
					(int) (600000 / quarterFits), // 600000 equals a quarter note
					90, // default instance variable value; see PerformanceNote()
					in[Tablature.PITCH]) 
				)
			));
		}

		List<Note> actual = new ArrayList<>();
		for (int i = 0; i < btp.length; i++) {
			actual.add(Tablature.convertTabSymbolToNote(btp, i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assert(expected.get(i).isEquivalent(actual.get(i)));
		}
	}


	public void testGetPitchesInChordStaticAlt() {
		Tablature tab = new Tablature(encodingTestpiece);

		List<List<Integer>> expected = getPitchesInChord(false);

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		List<List<TabSymbol>> chords = tab.getChords();
		Tuning t = tab.getTunings()[0];
		for (int i = 0; i < chords.size(); i++) {
		 	actual.add(Tablature.getPitchesInChord(chords.get(i), t));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}


	public void testGetUnisonInfo() {
		Tablature tab = new Tablature(encodingTestpiece);
		List<List<TabSymbol>> chords = tab.getChords();
		Tuning t = tab.getNormaliseTuning() ? tab.getTunings()[Tablature.NORMALISED_TUNING_IND] : 
			tab.getTunings()[Tablature.ENCODED_TUNING_IND];

		List<List<Integer[]>> expected = new ArrayList<>();
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		List<Integer[]> chord4 = new ArrayList<>();
		chord4.add(new Integer[]{57, 1, 2});
		expected.add(chord4);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);

		List<List<Integer[]>> actual = new ArrayList<>();		
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Tablature.getUnisonInfo(chords.get(i), t));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					assertEquals(expected.get(i).get(j).length, actual.get(i).get(j).length);
					for (int k = 0; k < expected.get(i).get(j).length; k++) {
						assertEquals(expected.get(i).get(j)[k], actual.get(i).get(j)[k]);
					}
				}
			}
		}
	}


//	public void testGetNumberOfUnisonsInChord() {
//		Tablature tablature = new Tablature(encodingTestpiece);
//
//		List<Integer> expected = 
//			Arrays.asList(new Integer[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
//		List<Integer> actual = new ArrayList<Integer>();
//		for (int i = 0; i < tablature.getChords().size(); i++) {
//			actual.add(tablature.getNumberOfUnisonsInChord(i));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//	}


	public void testGetCourseCrossingInfo() {
		Tablature tab = new Tablature(encodingTestpiece);
		List<List<TabSymbol>> chords = tab.getChords();
		Tuning t = tab.getNormaliseTuning() ? tab.getTunings()[Tablature.NORMALISED_TUNING_IND] : 
			tab.getTunings()[Tablature.ENCODED_TUNING_IND];

		List<List<Integer[]>> expected = new ArrayList<>(Collections.nCopies(16, null));
		List<Integer[]> chord1 = new ArrayList<>();
		chord1.add(new Integer[]{72, 69, 2, 3});
		expected.set(1, chord1);

		List<List<Integer[]>> actual = new ArrayList<>();
		for (int i = 0; i < chords.size(); i++) {
			actual.add(Tablature.getCourseCrossingInfo(chords.get(i), t));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).size(), actual.get(i).size());
				for (int j = 0; j < expected.get(i).size(); j++) {
					assertEquals(expected.get(i).get(j).length, actual.get(i).get(j).length);
					for (int k = 0; k < expected.get(i).get(j).length; k++) {
						assertEquals(expected.get(i).get(j)[k], actual.get(i).get(j)[k]);
					}
				}
			}
		}
	}


//	public void testGetNumberOfCourseCrossingsInChord() {
//		Tablature tablature = new Tablature(encodingTestpiece);
//
//		List<Integer> expected = Arrays.asList(new Integer[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
//
//		List<Integer> actual = new ArrayList<Integer>();
//		List<List<TabSymbol>> chords = tablature.getChords();
//		for (int i = 0; i < chords.size(); i++) {
//			actual.add(tablature.getNumberOfCourseCrossingsInChord(chords.get(i)));
//		}
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i), actual.get(i));
//		}
//		assertEquals(expected, actual);
//	}


	public void testGetPitchesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece);

		List<List<Integer>> expected = getPitchesInChord(false);

		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (int i = 0; i < tablature.getChords().size(); i++) {
		 	actual.add(tablature.getPitchesInChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}


	public void testGenerateChordDictionary() {
		Tablature tablature = new Tablature(encodingTestpiece);

		List<List<Integer>> pitchesInChord = getPitchesInChord(false);
		pitchesInChord = ToolBox.removeDuplicateItems(pitchesInChord);
		pitchesInChord.forEach(chord -> Collections.sort(chord));
		
		List<List<Integer>> expected = new ArrayList<>(pitchesInChord);
		List<List<Integer>> actual = tablature.generateChordDictionary();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
		assertEquals(expected, actual);
	}


	public void testGetTranspositionInterval() {
		assertEquals(-2, new Tablature(encodingTestpiece).getTranspositionInterval());
		assertEquals(0, new Tablature(encodingTestGetMeterInfo).getTranspositionInterval());
	}


	public void testGetMetricTimePerChord() {
		Tablature t1 = new Tablature(encodingTestpiece);
		Tablature t2 = new Tablature(encodingTestGetMeterInfo);

		List<Rational[]> expected = new ArrayList<>();
		// a. Excluding rests
		// For a piece with no meter changes
		getOnsetTimesPerChord().get(0).forEach(item -> { if (item[1] != 0) { expected.add(
			new Rational[]{new Rational(item[0], Tablature.SRV_DEN), Rational.ONE});}});
		// For a piece with meter changes
		getOnsetTimesPerChord().get(1).forEach(item -> { if (item[1] != 0) { expected.add(
			new Rational[]{new Rational(item[0], Tablature.SRV_DEN), Rational.ONE});}});
		// b. Including rests
		// For a piece with no meter changes
		getOnsetTimesPerChord().get(0).forEach(item -> expected.add(new Rational[]{
			new Rational(item[0], Tablature.SRV_DEN), new Rational(item[1], 1)})); 
		// For a piece with meter changes
		getOnsetTimesPerChord().get(1).forEach(item -> expected.add(new Rational[]{
			new Rational(item[0], Tablature.SRV_DEN), new Rational(item[1], 1)})); 

		List<Rational[]> actual = new ArrayList<>();
		actual.addAll(t1.getMetricTimePerChord(false));
		actual.addAll(t2.getMetricTimePerChord(false));
		actual.addAll(t1.getMetricTimePerChord(true));
		actual.addAll(t2.getMetricTimePerChord(true));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetTripletOnsetPairs() {
		// No triplets
		Tablature tablature = new Tablature(encodingTestpiece);
		
		List<Rational[]> expected = new ArrayList<>();
		expected.add(null);
		List<Rational[]> actual = new ArrayList<>();
		if (tablature.getTripletOnsetPairs() == null) {
			actual.add(null);
		}
		else {
			actual.addAll(tablature.getTripletOnsetPairs());
		}

		// Triplets
		// Replace bar 2 (beat 2) and bar 3 (beat 3) with triplets
		String origEncoding = tablature.getEncoding().getRawEncoding();
		String bar2Beat2 = "sm.c6.a5.e4.b2.>.a6.>.";
		String bar2Beat2Triplets = "tr[sm.c6.a5.e4.b2.>.trsm.a6.>.tr]sm.a6.>.";
		origEncoding = origEncoding.replace(bar2Beat2, bar2Beat2Triplets);
		String bar3Beat3 = "mi.>.mi.a6.c4.a2.a1.>.";
		String bar3Beat3Triplets = "tr[mi.>.trmi.a6.c4.a2.a1.>.tr]mi.a6.c4.a2.a1.>.";
		origEncoding = origEncoding.replace(bar3Beat3, bar3Beat3Triplets);
		tablature = new Tablature(new Encoding(origEncoding, 
			new Encoding(encodingTestpiece).getPiecename(), Stage.RULES_CHECKED), false);
		
		expected.add(new Rational[]{new Rational(5, 4), new Rational(17, 12), 
			new Rational(Symbol.SEMIMINIM.getDuration(), 1)});
		expected.add(new Rational[]{new Rational(10, 4), new Rational(17, 6), 
			new Rational(Symbol.MINIM.getDuration(), 1)});
		if (tablature.getTripletOnsetPairs() == null) {
			actual.add(null);
		}
		else {
			actual.addAll(tablature.getTripletOnsetPairs());
		}
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null) {
				assertEquals(expected.get(i), actual.get(i));
			}
			else {
				assertEquals(expected.get(i).length, actual.get(i).length);
				for (int j = 0; j < expected.get(i).length; j++) {
					assertEquals(expected.get(i)[j], actual.get(i)[j]);
				}
			}			
		}
	}


	public void testGetMensurationSigns() {
		Tablature t1 = new Tablature(encodingTestpiece);
		Tablature t2 = new Tablature(encodingNewsidler);

		List<String[]> expected = new ArrayList<>();
		// t1
		expected.add(new String[]{"MC\\", "1", "0"});
		// t2
		expected.add(new String[]{"MO.M34", "42", "3936"}); // 41 * 2/2 * Tablature.SRV_DEN
		expected.add(new String[]{"MC\\", "50", String.valueOf(3936+576)}); // + (8 * 3/4 * Tablature.SRV_DEN) = 3936 + 576

		List<String[]> actual = new ArrayList<>();
		actual.addAll(t1.getMensurationSigns());
		actual.addAll(t2.getMensurationSigns());
		
		t2.getMensurationSigns().forEach(s -> System.out.println(Arrays.asList(s)));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetBasicTabSymbolPropertiesChord() {
		Tablature tablature = new Tablature(encodingTestpiece);
		Integer[][] btp = getBtp(false);

		List<Integer[][]> expected = new ArrayList<Integer[][]>();
		// Chord 0
		expected.add(Arrays.copyOfRange(btp, 0, 4));
		// Chord 1
		expected.add(Arrays.copyOfRange(btp, 4, 8));
		// Chord 2
		expected.add(Arrays.copyOfRange(btp, 8, 9));
		// Chord 3
		expected.add(Arrays.copyOfRange(btp, 9, 13));
		// Chord 4
		expected.add(Arrays.copyOfRange(btp, 13, 14));
		// Chord 5
		expected.add(Arrays.copyOfRange(btp, 14, 19));
		// Chord 6
		expected.add(Arrays.copyOfRange(btp, 19, 23));
		// Chord 7
		expected.add(Arrays.copyOfRange(btp, 23, 25));
		// Chord 8
		expected.add(Arrays.copyOfRange(btp, 25, 29));
		// Chord 9-14
		expected.add(Arrays.copyOfRange(btp, 29, 30));
		expected.add(Arrays.copyOfRange(btp, 30, 31));
		expected.add(Arrays.copyOfRange(btp, 31, 32));
		expected.add(Arrays.copyOfRange(btp, 32, 33));
		expected.add(Arrays.copyOfRange(btp, 33, 34));
		expected.add(Arrays.copyOfRange(btp, 34, 35));
		// Chord 15
		expected.add(Arrays.copyOfRange(btp, 35, 39));

		List<Integer[][]> actual = new ArrayList<Integer[][]>();
		for (int i = 0; i < expected.size(); i++) {
		  actual.add(tablature.getBasicTabSymbolPropertiesChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j].length, actual.get(i)[j].length);
				for (int k = 0; k < expected.get(i)[j].length; k++) {
					assertEquals(expected.get(i)[j][k], actual.get(i)[j][k]);
				}
			}
		}
	}


	public void testGetNumberOfNotes() {
		int expected = 39;
		int actual = new Tablature(encodingTestpiece).getNumberOfNotes();
		assertEquals(expected, actual);
		
	}


	public void testGetIndicesPerChord() {
		Tablature t = new Tablature(encodingTestpiece);

		List<List<List<Integer>>> expected = new ArrayList<List<List<Integer>>>();
		// fwd
		List<List<Integer>> expectedFwd = new ArrayList<List<Integer>>();
		expectedFwd.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedFwd.add(Arrays.asList(new Integer[]{4, 5, 6, 7}));
		expectedFwd.add(Arrays.asList(new Integer[]{8}));
		expectedFwd.add(Arrays.asList(new Integer[]{9, 10, 11, 12}));
		expectedFwd.add(Arrays.asList(new Integer[]{13}));
		expectedFwd.add(Arrays.asList(new Integer[]{14, 15, 16, 17, 18}));
		expectedFwd.add(Arrays.asList(new Integer[]{19, 20, 21, 22}));
		expectedFwd.add(Arrays.asList(new Integer[]{23, 24}));
		expectedFwd.add(Arrays.asList(new Integer[]{25, 26, 27, 28}));
		expectedFwd.add(Arrays.asList(new Integer[]{29}));
		expectedFwd.add(Arrays.asList(new Integer[]{30}));
		expectedFwd.add(Arrays.asList(new Integer[]{31}));
		expectedFwd.add(Arrays.asList(new Integer[]{32}));
		expectedFwd.add(Arrays.asList(new Integer[]{33}));
		expectedFwd.add(Arrays.asList(new Integer[]{34}));
		expectedFwd.add(Arrays.asList(new Integer[]{35, 36, 37, 38})); 
		expected.add(expectedFwd);

		// bwd
		List<List<Integer>> expectedBwd = new ArrayList<List<Integer>>();
		expectedBwd.add(Arrays.asList(new Integer[]{0, 1, 2, 3}));
		expectedBwd.add(Arrays.asList(new Integer[]{4}));
		expectedBwd.add(Arrays.asList(new Integer[]{5}));
		expectedBwd.add(Arrays.asList(new Integer[]{6}));
		expectedBwd.add(Arrays.asList(new Integer[]{7}));
		expectedBwd.add(Arrays.asList(new Integer[]{8}));
		expectedBwd.add(Arrays.asList(new Integer[]{9}));
		expectedBwd.add(Arrays.asList(new Integer[]{10, 11, 12, 13}));
		expectedBwd.add(Arrays.asList(new Integer[]{14, 15}));
		expectedBwd.add(Arrays.asList(new Integer[]{16, 17, 18, 19}));
		expectedBwd.add(Arrays.asList(new Integer[]{20, 21, 22, 23, 24}));
		expectedBwd.add(Arrays.asList(new Integer[]{25}));
		expectedBwd.add(Arrays.asList(new Integer[]{26, 27, 28, 29}));
		expectedBwd.add(Arrays.asList(new Integer[]{30}));
		expectedBwd.add(Arrays.asList(new Integer[]{31, 32, 33, 34}));
		expectedBwd.add(Arrays.asList(new Integer[]{35, 36, 37, 38})); 
		expected.add(expectedBwd);

		List<List<List<Integer>>> actual = new ArrayList<List<List<Integer>>>(); 
		actual.add(t.getIndicesPerChord(false));
		actual.add(t.getIndicesPerChord(true));

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
				}
			}
		}
		assertEquals(expected, actual);
	}
	
	
	public void testGetLargestTablatureChord() {
		int expected = 5;
		int actual = new Tablature(encodingTestpiece).getLargestTablatureChord();
		assertEquals(expected, actual);
	}


	// TESTED BUT NOT IN USE -->
	public void testGetMinimumDurationPerChord() {
		Tablature t1 = new Tablature(encodingTestpiece);
		Tablature t2 = new Tablature(encodingTestGetMeterInfo);

		List<Rational> expected = new ArrayList<>();
		// testpiece
		List<Rational> md1 = new ArrayList<>();
		getMinimumDurationPerChord().get(0).forEach(i -> md1.add(new Rational(i, Tablature.SRV_DEN)));
		expected.addAll(md1);
		
		// testGetMeterInfo
		List<Rational> md2 = new ArrayList<>();
		getMinimumDurationPerChord().get(1).forEach(i -> md2.add(new Rational(i, Tablature.SRV_DEN)));
		expected.addAll(md2);

		List<Rational> actual = t1.getMinimumDurationPerChord();
		actual.addAll(t2.getMinimumDurationPerChord());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetNumberOfBarlines() {
		List<Integer> expected = Arrays.asList(9, 4, 96);

		List<Integer> actual = new ArrayList<>();
		// No decorative opening barlines and non-metric barlines
		actual.add(new Tablature(encodingTestGetMeterInfo).getNumberOfBarlines());
		// Non-metric barlines
		actual.add(new Tablature(encodingTestpiece).getNumberOfBarlines());
		// Decorative opening barline and non-metric barlines 
		actual.add(new Tablature(encodingNewsidler).getNumberOfBarlines());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testMapBarsToMetricBars() {
		// One tab bar in one metric bar (tab bar:metric bar 1:1)
		Tablature t1 = new Tablature(encodingTestpiece);
		// Two tab bars in one metric bar (tab bar:metric bar n:1, n=2)
		Tablature t2 = new Tablature(encodingBarbetta);
		// Three tab bars in one metric bar (tab bar:metric bar n:1, n=3)
		Tablature t3 = new Tablature(encodingNarvaez);
		// Three tab bars in two metric bars (tab bar:metric bar 3:2)
		Tablature t4 = new Tablature(encodingNewsidlerCumSancto);

		List<Integer[]> expected = new ArrayList<>();
		// For encodingTestPiece
		expected.add(new Integer[]{1, 1, 1, 1});
		expected.add(new Integer[]{2, 2, 2, 1});
		expected.add(new Integer[]{3, 3, 3, 0});
		expected.add(new Integer[]{4, 3, 3, 1});

		// For encodingBarbetta
		int tabBar = 1;
		for (int i = 1; i <= 30; i++) {
			expected.add(new Integer[]{tabBar, i, i, 0});
			tabBar++;
			expected.add(new Integer[]{tabBar, i, i, 1});
			tabBar++;
		}

		// For encodingNarvaez
		tabBar = 1;
		for (int i = 1; i <= 30; i++) {
			expected.add(new Integer[]{tabBar, i, i, 0});
			tabBar++;
			expected.add(new Integer[]{tabBar, i, i, 0});
			tabBar++;
			expected.add(new Integer[]{tabBar, i, i, 1});
			tabBar++;
		}

		// For encodingNewsidlerCumSancto
		expected.add(new Integer[]{1, 1, 1, 0});
		expected.add(new Integer[]{2, 1, 2, 0});
		expected.add(new Integer[]{3, 2, 2, 1});
		//
		expected.add(new Integer[]{4, 3, 3, 0});
		expected.add(new Integer[]{5, 3, 4, 0});
		expected.add(new Integer[]{6, 4, 4, 1});
		//
		expected.add(new Integer[]{7, 5, 5, 0});
		expected.add(new Integer[]{8, 5, 6, 0});
		expected.add(new Integer[]{9, 6, 6, 1});
		//
		expected.add(new Integer[]{10, 7, 7, 0});
		expected.add(new Integer[]{11, 7, 8, 0});
		expected.add(new Integer[]{12, 8, 8, 1});
		//
		expected.add(new Integer[]{13, 9, 9, 0});
		expected.add(new Integer[]{14, 9, 10, 0});
		expected.add(new Integer[]{15, 10, 10, 1});
		//
		expected.add(new Integer[]{16, 11, 11, 0});
		expected.add(new Integer[]{17, 11, 12, 0});
		expected.add(new Integer[]{18, 12, 12, 1});
		//
		expected.add(new Integer[]{19, 13, 13, 0});
		expected.add(new Integer[]{20, 13, 14, 0});
		expected.add(new Integer[]{21, 14, 14, 1});
		//
		expected.add(new Integer[]{22, 15, 15, 0});
		expected.add(new Integer[]{23, 15, 16, 0});
		expected.add(new Integer[]{24, 16, 16, 1});
		//
		expected.add(new Integer[]{25, 17, 17, 0});
		expected.add(new Integer[]{26, 17, 18, 0});
		expected.add(new Integer[]{27, 18, 18, 1});
		//
		expected.add(new Integer[]{28, 19, 19, 0});
		expected.add(new Integer[]{29, 19, 20, 0});
		expected.add(new Integer[]{30, 20, 20, 1});
		//
		expected.add(new Integer[]{31, 21, 21, 0});
		expected.add(new Integer[]{32, 21, 22, 0});
		expected.add(new Integer[]{33, 22, 22, 1});
		//
		expected.add(new Integer[]{34, 23, 23, 0});
		expected.add(new Integer[]{35, 23, 24, 0});
		expected.add(new Integer[]{36, 24, 24, 1});
		//
		expected.add(new Integer[]{37, 25, 25, 0});
		expected.add(new Integer[]{38, 25, 26, 0});
		expected.add(new Integer[]{39, 26, 26, 1});
		//
		expected.add(new Integer[]{40, 27, 27, 0});
		expected.add(new Integer[]{41, 27, 28, 0});
		expected.add(new Integer[]{42, 28, 28, 1});

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(t1.mapBarsToMetricBars());
		actual.addAll(t2.mapBarsToMetricBars());
		actual.addAll(t3.mapBarsToMetricBars());
		actual.addAll(t4.mapBarsToMetricBars());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetBarInfo() {
		Tablature t1 = new Tablature(encodingTestpiece);
		Tablature t2 = new Tablature(encodingBarbetta);
		Tablature t3 = new Tablature(encodingNarvaez);
		Tablature t4 = new Tablature(encodingNewsidler);

		List<Integer[]> expected = new ArrayList<>();
		// For encodingTestPiece
		expected.add(new Integer[]{0, 96, 96});
		expected.add(new Integer[]{96, 96, 192});
		expected.add(new Integer[]{192, 18, 210});
		expected.add(new Integer[]{210, 78, 288});

		// For encodingBarbetta
		int onset = 0;
		int barlen = 48;
		for (int i = 1; i <= 60; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		// For encodingNarvaez
		onset = 0;
		barlen = 96;
		for (int i = 1; i <= 90; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		// For encodingNewsidler
		onset = 0;
		barlen = 96;
		for (int i = 1; i <= 41; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}
		barlen = 72; 
		for (int i = 42; i <= 49; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}
		barlen = 96; 
		for (int i = 50; i <= 96; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(t1.getBarInfo());
		actual.addAll(t2.getBarInfo());
		actual.addAll(t3.getBarInfo());
		actual.addAll(t4.getBarInfo());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetMetricBarInfo() {
		Tablature t1 = new Tablature(encodingTestpiece);
		Tablature t2 = new Tablature(encodingBarbetta);
		Tablature t3 = new Tablature(encodingNarvaez);
		Tablature t4 = new Tablature(encodingNewsidler);

		List<Integer[]> expected = new ArrayList<>();
		// For encodingTestPiece
		int onset = 0; 
		int barlen = 96;
		for (int i = 1; i <= 3; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		// For encodingBarbetta
		onset = 0;
		barlen = 96;
		for (int i = 1; i <= 30; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		// For encodingNarvaez
		onset = 0;
		barlen = 3*96;
		for (int i = 1; i <= 30; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		// For encodingNewsidler
		onset = 0;
		barlen = 96;
		for (int i = 1; i <= 41; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}
		barlen = 144; 
		for (int i = 42; i <= 45; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}
		barlen = 96; 
		for (int i = 46; i <= 92; i++) {
			expected.add(new Integer[]{onset, barlen, onset+barlen});
			onset += barlen; 
		}

		List<Integer[]> actual = new ArrayList<>();
		actual.addAll(t1.getMetricBarInfo());
		actual.addAll(t2.getMetricBarInfo());
		actual.addAll(t3.getMetricBarInfo());
		actual.addAll(t4.getMetricBarInfo());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


//	public void testAdaptToDiminutions() {
//		Tablature t1 = new Tablature();
//		t1.setEncoding(new Encoding(encodingTestpiece));
//		t1.setNormaliseTuning(true);
//		t1.setName();
//		t1.setTimeline();
//		t1.setTunings();
//		Tablature t2 = new Tablature();
//		t2.setEncoding(new Encoding(encodingTestGetMeterInfo));
//		t2.setNormaliseTuning(true);
//		t2.setName();
//		t2.setTimeline();
//		t2.setTunings();
//
//		// For encodingTestPiece
//		List<Integer> minDur1 = getMinimumDurationPerNote(false).get(0);
//		List<Integer> minDur1Exp = getMinimumDurationPerNote(true).get(0);
//		//
//		List<Integer> gridX1 = new ArrayList<>();
//		getOnsetTimesPerNote(false).get(0).forEach(item -> { if (item[1] != 0) { gridX1.add(item[0]);}});
//		List<Integer> gridX1Exp = new ArrayList<>();
//		getOnsetTimesPerNote(true).get(0).forEach(item -> { if (item[1] != 0) { gridX1Exp.add(item[0]);}});
//		
//		// For testGetMeterInfo
//		List<Integer> minDur2 = getMinimumDurationPerNote(false).get(1);
//		List<Integer> minDur2Exp = getMinimumDurationPerNote(true).get(1);
//		//
//		List<Integer> gridX2 = new ArrayList<>();
//		getOnsetTimesPerNote(false).get(1).forEach(item -> { if (item[1] != 0) { gridX2.add(item[0]);}});
//		List<Integer> gridX2Exp = new ArrayList<>();
//		getOnsetTimesPerNote(true).get(1).forEach(item -> { if (item[1] != 0) { gridX2Exp.add(item[0]);}});
//
//		List<List<List<Integer>>> expected = new ArrayList<>();
//		List<List<Integer>> expected1 = new ArrayList<>();
//		expected1.add(minDur1Exp);
//		expected1.add(gridX1Exp);
//		expected.add(expected1);
//		List<List<Integer>> expected2 = new ArrayList<>();
//		expected2.add(minDur2Exp);
//		expected2.add(gridX2Exp);
//		expected.add(expected2);
//
//		List<List<List<Integer>>> actual = new ArrayList<>();
//		actual.add(t1.adaptToDiminutions(minDur1, gridX1));
////		actual.add(Tablature.adaptToDiminutions(minDur1, gridX1, 
////			ToolBox.getItemsAtIndex(t1.getTimeline().getMeterInfo(), Timeline.MI_DIM), 
////			t1.getTimeline().getUndiminutedMeterInfo()));
//		actual.add(t2.adaptToDiminutions(minDur2, gridX2));
////		actual.add(Tablature.adaptToDiminutions(minDur2, gridX2, 
////			ToolBox.getItemsAtIndex(t2.getTimeline().getMeterInfo(), Timeline.MI_DIM), 
////			t2.getTimeline().getUndiminutedMeterInfo()));
//
//		assertEquals(expected.size(), actual.size());
//		for (int i = 0; i < expected.size(); i++) {
//			assertEquals(expected.get(i).size(), actual.get(i).size());
//			for (int j = 0; j < expected.get(i).size(); j++) {
//				assertEquals(expected.get(i).get(j).size(), actual.get(i).get(j).size());
//				for (int k = 0; k < expected.get(i).get(j).size(); k++) {
//					assertEquals(expected.get(i).get(j).get(k), actual.get(i).get(j).get(k));
//				}
//			}
//		}
//		assertEquals(expected, actual);
//	}


//	public void testReverse() {
//		Tablature t = new Tablature(encodingTestpiece, false);
//		
//		// btp
//		Integer[][] expectedBtp = new Integer[0][0];
//		Integer[][] btp = getBtp(false);
//		// Chord 0 (15)		
//		Integer[][] chord = Arrays.copyOfRange(btp, 35, 39);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 0;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{90, 90, 72, 48}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 0;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 1;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 1 (14)
//		chord = Arrays.copyOfRange(btp, 34, 35);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 48;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{33}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 1;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 3;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 2 (13)
//		chord = Arrays.copyOfRange(btp, 33, 34);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 72;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{3}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 2;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 4;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 3 (12)
//		chord = Arrays.copyOfRange(btp, 32, 33);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 75;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{3}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 3;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 5;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 4 (11)
//		chord = Arrays.copyOfRange(btp, 31, 32);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 78;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{6}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 4;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 7;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 5 (10)
//		chord = Arrays.copyOfRange(btp, 30, 31);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 81;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{9}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 5;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 8;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 6 (9)
//		chord = Arrays.copyOfRange(btp, 29, 30);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 84;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{6}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 6;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 9;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 7 (8)
//		chord = Arrays.copyOfRange(btp, 25, 29);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 90;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{18, 30, 6, 18}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 7;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 10;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 8 (7)
//		chord = Arrays.copyOfRange(btp, 23, 25);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 96;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{12, 12}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 8;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 13;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 9 (6)
//		chord = Arrays.copyOfRange(btp, 19, 23);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 108;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{12, 12, 12, 66}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 9;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 14;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 10 (5)
//		chord = Arrays.copyOfRange(btp, 14, 19);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 120;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{24, 36, 36, 96, 36}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 10;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 15;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 11 (4)
//		chord = Arrays.copyOfRange(btp, 13, 14);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 144;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{12}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 11;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 16;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 12 (3)
//		chord = Arrays.copyOfRange(btp, 9, 13);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 156;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{12, 36, 18, 18}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 12;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 17;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 13 (2)
//		chord = Arrays.copyOfRange(btp, 8, 9);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 168;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{6}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 13;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 18;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 14 (1)
//		chord = Arrays.copyOfRange(btp, 4, 8);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 174;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{42, 18, 18, 18}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 14;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 19;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 15 (0)
//		chord = Arrays.copyOfRange(btp, 0, 4);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.ONSET_TIME] = 192;
//			chord[i][Tablature.MAX_DURATION] = Arrays.asList(new Integer[]{24, 24, 24, 24}).get(i);
//			chord[i][Tablature.CHORD_SEQ_NUM] = 15;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 21;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//
//		// chords
//		List<List<TabSymbol>> expectedTc = getChords();
//		Collections.reverse(expectedTc);
//
//		t.reverse();
//		Integer[][] actualBtp = t.getBasicTabSymbolProperties();
//		List<List<TabSymbol>> actualTc = t.getChords();
//
//		assertEquals(expectedBtp.length, actualBtp.length);
//		for (int i = 0; i < expectedBtp.length; i++) {
//			assertEquals(expectedBtp[i].length, actualBtp[i].length);
//			for (int j = 0; j < expectedBtp[i].length; j++) {
//				assertEquals(expectedBtp[i][j], actualBtp[i][j]);
//			}
//		}
//
//		assertEquals(expectedTc.size(), actualTc.size());
//		for (int i = 0; i < expectedTc.size(); i++) {
//			assertEquals(expectedTc.get(i).size(), actualTc.get(i).size());
//			for (int j = 0; j < expectedTc.get(i).size(); j++) {
//				assertEquals(expectedTc.get(i).get(j), actualTc.get(i).get(j));
//			}
//		}
//		assertEquals(expectedTc, actualTc);
//	}


//	public void testDeornament() {
//		Tablature t = new Tablature(encodingTestpiece, false);
//
//		// btp
//		Integer[][] expectedBtp = new Integer[0][0];
//		Integer[][] btp = getBtp(false);	
//		// Chord 0 (0)
//		Integer[][] chord = Arrays.copyOfRange(btp, 0, 4);
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 1 (1)
//		chord = Arrays.copyOfRange(btp, 4, 8);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.MIN_DURATION] = 24;
//			if (i == 0) {
//				chord[i][Tablature.MAX_DURATION] = 24;
//			}
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 2 (3)
//		chord = Arrays.copyOfRange(btp, 9, 13);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 2;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 6;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 3 (4)
//		chord = Arrays.copyOfRange(btp, 13, 14);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 3;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 7;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 4 (5)
//		chord = Arrays.copyOfRange(btp, 14, 19);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 4;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 8;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 5 (6)
//		chord = Arrays.copyOfRange(btp, 19, 23);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 5;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 9;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 6 (7)
//		chord = Arrays.copyOfRange(btp, 23, 25);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 6;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 10;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 7 (8)
//		chord = Arrays.copyOfRange(btp, 25, 29);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.MIN_DURATION] = 24;
//			chord[i][Tablature.CHORD_SEQ_NUM] = 7;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 13;
//			if (i == 2) {
//				chord[i][Tablature.MAX_DURATION] = 72;
//			}
//			if (i == 3) {
//				chord[i][Tablature.MAX_DURATION] = 24;
//			}
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 8 (14)
//		chord = Arrays.copyOfRange(btp, 34, 35);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 8;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 15;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//		// Chord 9 (15)
//		chord = Arrays.copyOfRange(btp, 35, 39);
//		for (int i = 0; i < chord.length; i++) {
//			chord[i][Tablature.CHORD_SEQ_NUM] = 9;
//			chord[i][Tablature.TAB_EVENT_SEQ_NUM] = 17;
//		}
//		expectedBtp = ArrayUtils.addAll(expectedBtp, chord);
//
//		// chords
//		List<List<TabSymbol>> expectedTc = getChords();
//		expectedTc = 
//			ToolBox.removeItemsAtIndices(expectedTc, 
//			Arrays.asList(new Integer[]{2, 9, 10, 11, 12, 13}));
//
//		t.deornament(new Rational(RhythmSymbol.SEMIMINIM.getDuration(), Tablature.SRV_DEN));
//		Integer[][] actualBtp = t.getBasicTabSymbolProperties();
//		List<List<TabSymbol>> actualTc = t.getChords();
//
//		assertEquals(expectedBtp.length, actualBtp.length);
//		for (int i = 0; i < expectedBtp.length; i++) {
//			assertEquals(expectedBtp[i].length, actualBtp[i].length);
//			for (int j = 0; j < expectedBtp[i].length; j++) {
//				assertEquals(expectedBtp[i][j], actualBtp[i][j]);
//			}
//		}
//
//		assertEquals(expectedTc.size(), actualTc.size());
//		for (int i = 0; i < expectedTc.size(); i++) {
//			assertEquals(expectedTc.get(i).size(), actualTc.get(i).size());
//			for (int j = 0; j < expectedTc.get(i).size(); j++) {
//				assertEquals(expectedTc.get(i).get(j), actualTc.get(i).get(j));
//			}
//		}
//		assertEquals(expectedTc, actualTc);
//	}


//	public void testStretch() {
//		Tablature t = new Tablature(encodingTestpiece, false);
//		
//		Integer[][] expectedBtp = getBtp(false);
//		for (Integer[] in : expectedBtp) {
//			in[Tablature.ONSET_TIME] = in[Tablature.ONSET_TIME] * 2;
//			in[Tablature.MIN_DURATION] = in[Tablature.MIN_DURATION] * 2;
//			in[Tablature.MAX_DURATION] = in[Tablature.MAX_DURATION] * 2;
//		}
//
//		t.stretch(2);
//		Integer[][] actualBtp = t.getBasicTabSymbolProperties();
//
//		assertEquals(expectedBtp.length, actualBtp.length);
//		for (int i = 0; i < expectedBtp.length; i++) {
//			assertEquals(expectedBtp[i].length, actualBtp[i].length);
//			for (int j = 0; j < expectedBtp[i].length; j++) {
//				assertEquals(expectedBtp[i][j], actualBtp[i][j]);
//			}
//		}
//	}
}
