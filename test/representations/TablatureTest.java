package representations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import de.uos.fmt.musitech.data.performance.PerformanceNote;
import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.utility.math.Rational;
import junit.framework.TestCase;
import paths.Paths;
import representations.Encoding.Tuning;
import tbp.RhythmSymbol;
import tbp.TabSymbol;
import tbp.TabSymbolSet;
import tools.ToolBox;

public class TablatureTest extends TestCase {

	private File encodingTestpiece;
	private File encodingTestGetMeterInfo;
	private File encodingNewsidler;
	private File midiTestpiece;
	private static final int TRANSP_INT = -2;
	private static final Rational THIRTY_SECOND = new Rational(1, 32);
	private static final Rational SIXTEENTH = new Rational(1, 16);
	private static final Rational EIGHTH = new Rational(1, 8);
	private static final Rational DOTTED_EIGHTH = new Rational(3, 16);
	private static final Rational QUARTER = new Rational(1, 4);
	private static final Rational HALF = new Rational(1, 2);
	private static final Rational DOTTED_HALF = new Rational(3, 4);
	private static final Rational WHOLE = new Rational(1, 1);
	private static final Rational BREVE = new Rational(2, 1);

	protected void setUp() throws Exception {
		super.setUp();
		String root = Paths.getRootPath() + Paths.getDataDir(); 
		encodingTestpiece = 
			new File(root + Paths.getEncodingsPath() + Paths.getTestDir() + "testpiece.tbp");
		encodingTestGetMeterInfo = 
			new File(root + Paths.getEncodingsPath() + Paths.getTestDir() + "test_get_meter_info.tbp");
		midiTestpiece = 
			new File(root + Paths.getMIDIPath() + Paths.getTestDir() + "testpiece.mid");
		encodingNewsidler = 
			new File(root + Paths.getEncodingsPath() + "/tab-int/3vv/" + "newsidler-1544_2-nun_volget" + ".tbp");
	}


	protected void tearDown() throws Exception {
		super.tearDown();
	}


	private Integer[][] getBtp() {
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
		btp[25] = new Integer[]{45, 6, 0, 192, 6, 72, 8, 4, 0, 13};
		btp[26] = new Integer[]{57, 4, 2, 192, 6, 72, 8, 4, 1, 13};
		btp[27] = new Integer[]{64, 2, 0, 192, 6, 6, 8, 4, 2, 13};
		btp[28] = new Integer[]{69, 1, 0, 192, 6, 12, 8, 4, 3, 13};
		// Chord 9-14
		btp[29] = new Integer[]{68, 2, 4, 198, 6, 9, 9, 1, 0, 14};
		btp[30] = new Integer[]{69, 1, 0, 204, 3, 12, 10, 1, 0, 15};
		btp[31] = new Integer[]{68, 2, 4, 207, 3, 3, 11, 1, 0, 16};
		btp[32] = new Integer[]{66, 2, 2, 210, 3, 3, 12, 1, 0, 18};
		btp[33] = new Integer[]{68, 2, 4, 213, 3, 51, 13, 1, 0, 19};
		btp[34] = new Integer[]{69, 1, 0, 216, 24, 48, 14, 1, 0, 20};
		// Chord 15
		btp[35] = new Integer[]{45, 6, 0, 264, 24, 24, 15, 4, 0, 22};
		btp[36] = new Integer[]{57, 4, 2, 264, 24, 24, 15, 4, 1, 22};
		btp[37] = new Integer[]{64, 2, 0, 264, 24, 24, 15, 4, 2, 22};
		btp[38] = new Integer[]{69, 1, 0, 264, 24, 24, 15, 4, 3, 22};

		return btp;
	}


	private Integer[][] getBtpTransposed() {
		// For testpiece
		Integer[][] btpTransposed = getBtp();
		for (int i = 0; i < btpTransposed.length; i++) {
			btpTransposed[i][Tablature.PITCH] = btpTransposed[i][Tablature.PITCH] + TRANSP_INT;
		}
		return btpTransposed;
	}


	private List<List<Rational[]>> getMetricPositions() {
		List<List<Rational[]>> all = new ArrayList<>();

		// For testPiece (meter = 2/2; diminution = 1)
		List<Rational[]> expectedTestPiece = new ArrayList<Rational[]>();
		// Bar 1
		Rational[] chord0 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord0); expectedTestPiece.add(chord0); expectedTestPiece.add(chord0); expectedTestPiece.add(chord0);
		// Bar 2
		Rational[] chord1 = new Rational[]{new Rational(2, 1), new Rational(0, 64)};
		expectedTestPiece.add(chord1); expectedTestPiece.add(chord1); expectedTestPiece.add(chord1); expectedTestPiece.add(chord1);    
		Rational[] chord2 = new Rational[]{new Rational(2, 1), new Rational(3, 16)};
		expectedTestPiece.add(chord2); 
		Rational[] chord3 = new Rational[]{new Rational(2, 1), new Rational(1, 4)};
		expectedTestPiece.add(chord3); expectedTestPiece.add(chord3); expectedTestPiece.add(chord3); expectedTestPiece.add(chord3);
		Rational[] chord4 = new Rational[]{new Rational(2, 1), new Rational(3, 8)};
		expectedTestPiece.add(chord4); 
		Rational[] chord5 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5); expectedTestPiece.add(chord5);
		Rational[] chord6 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord6); expectedTestPiece.add(chord6); expectedTestPiece.add(chord6); expectedTestPiece.add(chord6);
		Rational[] chord7 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
		expectedTestPiece.add(chord7); expectedTestPiece.add(chord7);
		// Bar 3
		Rational[] chord8 = new Rational[]{new Rational(3, 1), new Rational(0, 64)};
		expectedTestPiece.add(chord8); expectedTestPiece.add(chord8); expectedTestPiece.add(chord8); expectedTestPiece.add(chord8);
		Rational[] chord9 = new Rational[]{new Rational(3, 1), new Rational(1, 16)};
		expectedTestPiece.add(chord9); 
		Rational[] chord10 = new Rational[]{new Rational(3, 1), new Rational(1, 8)};
		expectedTestPiece.add(chord10); 
		Rational[] chord11 = new Rational[]{new Rational(3, 1), new Rational(5, 32)};
		expectedTestPiece.add(chord11); 
		Rational[] chord12 = new Rational[]{new Rational(3, 1), new Rational(3, 16)};
		expectedTestPiece.add(chord12); 
		Rational[] chord13 = new Rational[]{new Rational(3, 1), new Rational(7, 32)};
		expectedTestPiece.add(chord13); 
		Rational[] chord14 = new Rational[]{new Rational(3, 1), new Rational(1, 4)};
		expectedTestPiece.add(chord14); 
		Rational[] chord15 = new Rational[]{new Rational(3, 1), new Rational(3, 4)};
		expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); expectedTestPiece.add(chord15); 

		// For testGetMeterInfo
		List<Rational[]> expectedTestGetMeterInfo = new ArrayList<Rational[]>();
		// Bar 0 (meter = 2/2; diminution = 2): anacrusis length is 3/8 
		chord0 = new Rational[]{new Rational(0, 1), new Rational(5, 4)};
		expectedTestGetMeterInfo.add(chord0); 
		chord1 = new Rational[]{new Rational(0, 1), new Rational(3, 2)};
		expectedTestGetMeterInfo.add(chord1);
		chord2 = new Rational[]{new Rational(0, 1), new Rational(7, 4)};
		expectedTestGetMeterInfo.add(chord2);
		// Bar 1 (meter = 2/2; diminution = 2)
		chord3 = new Rational[]{new Rational(1, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord3); expectedTestGetMeterInfo.add(chord3);
		chord4 = new Rational[]{new Rational(1, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord4); 
		chord5 = new Rational[]{new Rational(1, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord5); expectedTestGetMeterInfo.add(chord5);
		// Bar 2 (meter = 2/2; diminution = 2)
		chord6 = new Rational[]{new Rational(2, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord6); expectedTestGetMeterInfo.add(chord6);
		chord7 = new Rational[]{new Rational(2, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord7);
		chord8 = new Rational[]{new Rational(2, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord8);
		chord9 = new Rational[]{new Rational(2, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord9);
		chord10 = new Rational[]{new Rational(2, 1), new Rational(13, 16)};
		expectedTestGetMeterInfo.add(chord10);
		chord11 = new Rational[]{new Rational(2, 1), new Rational(7, 8)};
		expectedTestGetMeterInfo.add(chord11);
		chord12 = new Rational[]{new Rational(2, 1), new Rational(15, 16)};
		expectedTestGetMeterInfo.add(chord12);
		chord13 = new Rational[]{new Rational(2, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord13); expectedTestGetMeterInfo.add(chord13);
		// Bar 3 (meter = 3/4; diminution = 4)
		chord14 = new Rational[]{new Rational(3, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord14); expectedTestGetMeterInfo.add(chord14);
		chord15 = new Rational[]{new Rational(3, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord15); // new Rational(1, 4)});
		Rational[] chord16 = new Rational[]{new Rational(3, 1), new Rational(3, 2)};
		expectedTestGetMeterInfo.add(chord16); // new Rational(3, 8)});
		Rational[] chord17 = new Rational[]{new Rational(3, 1), new Rational(7, 4)};
		expectedTestGetMeterInfo.add(chord17); // new Rational(7, 16)});
		Rational[] chord18 = new Rational[]{new Rational(3, 1), new Rational(2, 1)};
		expectedTestGetMeterInfo.add(chord18); expectedTestGetMeterInfo.add(chord18);
		// Bar 4 (meter = 3/4; diminution = 4)
		Rational[] chord19 = new Rational[]{new Rational(4, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord19);
		Rational[] chord20 = new Rational[]{new Rational(4, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord20);
		Rational[] chord21 = new Rational[]{new Rational(4, 1), new Rational(7, 8)};
		expectedTestGetMeterInfo.add(chord21);
		Rational[] chord22 = new Rational[]{new Rational(4, 1), new Rational(1, 1)};
		expectedTestGetMeterInfo.add(chord22); expectedTestGetMeterInfo.add(chord22);
		// Bar 5 (meter = 2/2; diminution = 1)
		Rational[] chord23 = new Rational[]{new Rational(5, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord23); expectedTestGetMeterInfo.add(chord23);
		Rational[] chord24 = new Rational[]{new Rational(5, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord24); expectedTestGetMeterInfo.add(chord24);
		// Bar 6 (meter = 2/2; diminution = 1)
		Rational[] chord25 = new Rational[]{new Rational(6, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord25);
		Rational[] chord26 = new Rational[]{new Rational(6, 1), new Rational(1, 8)};
		expectedTestGetMeterInfo.add(chord26);
		Rational[] chord27 = new Rational[]{new Rational(6, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord27);
		Rational[] chord28 = new Rational[]{new Rational(6, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord28);
		Rational[] chord29 = new Rational[]{new Rational(6, 1), new Rational(1, 2)};
		expectedTestGetMeterInfo.add(chord29);
		Rational[] chord30 = new Rational[]{new Rational(6, 1), new Rational(5, 8)};
		expectedTestGetMeterInfo.add(chord30);
		Rational[] chord31 = new Rational[]{new Rational(6, 1), new Rational(3, 4)};
		expectedTestGetMeterInfo.add(chord31); expectedTestGetMeterInfo.add(chord31);
		// Bar 7 (meter = 5/16; diminution = 1)
		Rational[] chord32 = new Rational[]{new Rational(7, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord32);
		Rational[] chord33 = new Rational[]{new Rational(7, 1), new Rational(1, 8)};
		expectedTestGetMeterInfo.add(chord33);
		Rational[] chord34 = new Rational[]{new Rational(7, 1), new Rational(3, 16)};
		expectedTestGetMeterInfo.add(chord34);
		Rational[] chord35 = new Rational[]{new Rational(7, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord35);
		// Bar 8 (meter = 2/2; diminution = -2)
		Rational[] chord36 = new Rational[]{new Rational(8, 1), new Rational(0, 512)};
		expectedTestGetMeterInfo.add(chord36); expectedTestGetMeterInfo.add(chord36);
		Rational[] chord37 = new Rational[]{new Rational(8, 1), new Rational(1, 4)};
		expectedTestGetMeterInfo.add(chord37);
		Rational[] chord38 = new Rational[]{new Rational(8, 1), new Rational(5, 16)};
		expectedTestGetMeterInfo.add(chord38);
		Rational[] chord39 = new Rational[]{new Rational(8, 1), new Rational(11, 32)};
		expectedTestGetMeterInfo.add(chord39);
		Rational[] chord40 = new Rational[]{new Rational(8, 1), new Rational(3, 8)};
		expectedTestGetMeterInfo.add(chord40); expectedTestGetMeterInfo.add(chord40);

		all.add(expectedTestPiece);
		all.add(expectedTestGetMeterInfo);
		return all;
	}


	private List<List<Integer>> getPitchesInChord() {
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
		return pitchesInChord;
	}


	private List<List<Integer>> getPitchesInChordTransposed() {
		// For testpiece
		List<List<Integer>> pitchesInChord = getPitchesInChord();
		List<List<Integer>> pitchesInChordTransposed = new ArrayList<>();
		for (int i = 0; i < pitchesInChord.size(); i++) {
			pitchesInChordTransposed.add(pitchesInChord.get(i).stream().map(p -> 
				p + TRANSP_INT).collect(Collectors.toList()));
//			pitchesInChord.get(i).forEach(p -> p += TRANSP_INT);
//			pitchesInChordTransposed.add(pitchesInChord.get(i));
		}
		return pitchesInChordTransposed;
	}


	private List<List<Rational[]>> getOnsetTimes() {
		List<List<Rational[]>> allOnsetTimes = new ArrayList<>();
		// For testpiece
		List<Rational[]> onsetTimesTestpiece = new ArrayList<>();
		// Rest
		onsetTimesTestpiece.add(new Rational[]{new Rational(0, 4), Rational.ZERO});
		// Chord 0
		onsetTimesTestpiece.add(new Rational[]{new Rational(3, 4), Rational.ONE});
		// Chord 1
		onsetTimesTestpiece.add(new Rational[]{new Rational(1, 1), Rational.ONE});
		// Chord 2
		onsetTimesTestpiece.add(new Rational[]{new Rational(19, 16), Rational.ONE});
		// Chord 3
		onsetTimesTestpiece.add(new Rational[]{new Rational(5, 4), Rational.ONE});
		// Chord 4
		onsetTimesTestpiece.add(new Rational[]{new Rational(11, 8), Rational.ONE});
		// Chord 5
		onsetTimesTestpiece.add(new Rational[]{new Rational(3, 2), Rational.ONE});
		// Chord 6
		onsetTimesTestpiece.add(new Rational[]{new Rational(7, 4), Rational.ONE});
		// Chord 7
		onsetTimesTestpiece.add(new Rational[]{new Rational(15, 8), Rational.ONE});
		// Chord 8
		onsetTimesTestpiece.add(new Rational[]{new Rational(2, 1), Rational.ONE});
		// Chords 9-14
		onsetTimesTestpiece.add(new Rational[]{new Rational(33, 16), Rational.ONE});
		onsetTimesTestpiece.add(new Rational[]{new Rational(17, 8), Rational.ONE});
		onsetTimesTestpiece.add(new Rational[]{new Rational(69, 32), Rational.ONE});
		onsetTimesTestpiece.add(new Rational[]{new Rational(35, 16), Rational.ONE});
		onsetTimesTestpiece.add(new Rational[]{new Rational(71, 32), Rational.ONE});
		onsetTimesTestpiece.add(new Rational[]{new Rational(9, 4), Rational.ONE});
		// Rest
		onsetTimesTestpiece.add(new Rational[]{new Rational(10, 4), Rational.ZERO});
		// Chord 15
		onsetTimesTestpiece.add(new Rational[]{new Rational(11, 4), Rational.ONE});
		
		// For encodingTestPiece
		List<Rational[]> onsetTimesTestGetMeterInfo = new ArrayList<>();
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(0, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(1, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(1, 2), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(3, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(6, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(7, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(11, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(13, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(27, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(7, 2), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(57, 16), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(29, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(59, 16), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(15, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(19, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(23, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(25, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(26, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(27, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(31, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(34, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(69, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(35, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(43, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(45, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(47, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(95, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(48, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(97, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(49, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(99, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(50, 4), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(51, 4), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(103, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(207, 16), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(104, 8), Rational.ONE});
		//
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(209, 16), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(213, 16), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(107, 8), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(429, 32), Rational.ONE});
		onsetTimesTestGetMeterInfo.add(new Rational[]{new Rational(215, 16), Rational.ONE});
		
		allOnsetTimes.add(onsetTimesTestpiece);
		allOnsetTimes.add(onsetTimesTestGetMeterInfo);
		return allOnsetTimes;
	}


	public void testMakeUndiminutedMeterInfo() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestGetMeterInfo));
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{3, 8, 0, 0, 0, 1});
		expected.add(new Integer[]{2, 2, 1, 2, 3, 8});
		expected.add(new Integer[]{3, 4, 3, 4, 19, 8});
		expected.add(new Integer[]{2, 2, 5, 6, 31, 8});
		expected.add(new Integer[]{5, 16, 7, 7, 47, 8});
		expected.add(new Integer[]{2, 2, 8, 8, 99, 16});

		List<Integer[]> actual = t.makeUndiminutedMeterInfo();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeDiminutions() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestGetMeterInfo));

		List<Integer> expected = Arrays.asList(new Integer[]{2, 2, 4, 1, 1, -2});
		List<Integer> actual = t.makeDiminutions();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testMakeMeterInfo() {
		Tablature t1 = new Tablature();
		t1.setEncoding(new Encoding(encodingTestGetMeterInfo));
		t1.setUndiminutedMeterInfo();
		t1.setDiminutions();

		Tablature t2 = new Tablature();
		t2.setEncoding(new Encoding(encodingTestpiece));
		t2.setUndiminutedMeterInfo();
		t2.setDiminutions();
	
		List<Integer[]> expected = new ArrayList<Integer[]>();
		// t1
		expected.add(new Integer[]{3, 4, 0, 0, 0, 1, 2});
		expected.add(new Integer[]{2, 1, 1, 2, 3, 4, 2});
		expected.add(new Integer[]{3, 1, 3, 4, 19, 4, 4});
		expected.add(new Integer[]{2, 2, 5, 6, 43, 4, 1});
		expected.add(new Integer[]{5, 16, 7, 7, 51, 4, 1});
		expected.add(new Integer[]{2, 4, 8, 8, 209, 16, -2});
		// t2		
		expected.add(new Integer[]{2, 2, 1, 3, 0, 1, 1});

		List<Integer[]> actual = t1.makeMeterInfo();
		actual.addAll(t2.makeMeterInfo());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeDiminutionPerBar() {
		Tablature t1 = new Tablature();
		t1.setEncoding(new Encoding(encodingTestGetMeterInfo));
		t1.setUndiminutedMeterInfo();
		t1.setDiminutions();
		t1.setMeterInfo();

		Tablature t2 = new Tablature();
		t2.setEncoding(new Encoding(encodingTestpiece));
		t2.setUndiminutedMeterInfo();
		t2.setDiminutions();
		t2.setMeterInfo();

		// For testGetMeterInfo
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{0, 2});
		expected.add(new Integer[]{1, 2});
		expected.add(new Integer[]{2, 2});
		expected.add(new Integer[]{3, 4});
		expected.add(new Integer[]{4, 4});
		expected.add(new Integer[]{5, 1});
		expected.add(new Integer[]{6, 1});
		expected.add(new Integer[]{7, 1});
		expected.add(new Integer[]{8, -2});
		// For testPiece
		expected.add(new Integer[]{1, 1});
		expected.add(new Integer[]{2, 1});
		expected.add(new Integer[]{3, 1});

		List<Integer[]> actual = t1.makeDiminutionPerBar();
		actual.addAll(t2.makeDiminutionPerBar());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMakeBasicTabSymbolProperties() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setUndiminutedMeterInfo();
		t.setDiminutions();
		t.setMeterInfo();

		Integer[][] expected = getBtp();
		Integer[][] actual = t.makeBasicTabSymbolProperties();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}


	public void testAdaptToDiminutions() {
		List<Integer[]> unadaptedMeterInfo = Arrays.asList(new Integer[][]{
			new Integer[]{2, 2, 1, 1},
			new Integer[]{3, 2, 2, 2},
			new Integer[]{2, 2, 3, 3}
		});
		
		// Testpiece1 (anacrusis; diminutions = [2, 1, 2])
		// 2/2: Q(r) H    Q    | 3/2: H    H    H    | 2/2: H    Q(r) Q    ||	
		//                4                                           13
		//           1    3           6         9                     12   
		//           0    2           5    7    8           10        11
		List<Integer> diminutions1 = Arrays.asList(new Integer[]{2, 1, 2});
		List<Integer> durationOfTabSymbols1 = Arrays.asList(new Integer[]{
			48, 48, 24, 24, 24, 
			48, 48, 48, 48, 48,
			48, 24, 24, 24
		});
		List<Integer> gridXOfTabSymbols1 = Arrays.asList(new Integer[]{
			24, 24, 72, 72, 72, 
			96, 96, 144, 192, 192,
			240, 312, 312, 312
		});
		List<Integer> newDurationOfTabSymbols1 = Arrays.asList(new Integer[]{
			96, 96, 48, 48, 48,
			48, 48, 48, 48, 48,
			96, 48, 48, 48 
		});
		List<Integer> newGridXOfTabSymbols1 = Arrays.asList(new Integer[]{
			48, 48, 144, 144, 144,
			192, 192, 240, 288, 288,
			336, 480, 480, 480 
		});

		// Testpiece2 (no anacrusis; diminutions = [1, -2, 1])
		// 2/2: Q    H    Q    | 3/2: H    H    H    | 2/2: H    Q(r) Q    ||	
		//                5                                           14
		//           2    4           7         10                    13   
		//      0    1    3           6    8    9           11        12
		List<Integer> diminutions2 = Arrays.asList(new Integer[]{1, -2, 1});
		List<Integer> durationOfTabSymbols2 = Arrays.asList(new Integer[]{
			24, 48, 48, 24, 24, 24,
			48, 48, 48, 48, 48,
			48, 24, 24, 24
		});
		List<Integer> gridXOfTabSymbols2 = Arrays.asList(new Integer[]{
			0, 24, 24, 72, 72, 72,
			96, 96, 144, 192, 192,
			240, 312, 312, 312
		});
		List<Integer> newDurationOfTabSymbols2 = Arrays.asList(new Integer[]{
			24, 48, 48, 24, 24, 24,
			24, 24, 24, 24, 24,
			48, 24, 24, 24	
		});
		List<Integer> newGridXOfTabSymbols2 = Arrays.asList(new Integer[]{
			0, 24, 24, 72, 72, 72,	
			96, 96, 120, 144, 144,
			168, 240, 240, 240
		});

		List<List<Integer>> expected = new ArrayList<>();
		expected.add(newDurationOfTabSymbols1);
		expected.add(newGridXOfTabSymbols1);
		expected.add(newDurationOfTabSymbols2);
		expected.add(newGridXOfTabSymbols2);
		
		List<List<Integer>> actual = new ArrayList<>(); 
		actual.addAll(Tablature.adaptToDiminutions(durationOfTabSymbols1, gridXOfTabSymbols1, 
			diminutions1, unadaptedMeterInfo));
		actual.addAll(Tablature.adaptToDiminutions(durationOfTabSymbols2, gridXOfTabSymbols2, 
			diminutions2, unadaptedMeterInfo));
		
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
		 	assertEquals(expected.get(i).size(), actual.get(i).size());
		 	for (int j = 0; j < expected.get(i).size(); j++) {
		 		assertEquals(expected.get(i).get(j), actual.get(i).get(j));
		 	}
		}
	}


	public void testNormaliseTuning() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setUndiminutedMeterInfo();
		t.setDiminutions();
		t.setMeterInfo();
		t.setBasicTabSymbolProperties();

		Tuning expectedTuning = Tuning.G;
		List<Integer> expectedGridYValues = new ArrayList<>();
		getPitchesInChordTransposed().forEach(expectedGridYValues::addAll); 
		Integer[][] expectedBtp = getBtpTransposed(); 

		t.normaliseTuning(true);

		Tuning actualTuning = t.getEncoding().getTunings()[Encoding.NEW_TUNING_IND];
		assertEquals(expectedTuning, actualTuning);
		//		
		List<Integer> actualGridYValues = t.getEncoding().getListsOfStatistics().get(Encoding.GRID_Y_IND);
		assertEquals(expectedGridYValues.size(), actualGridYValues.size());
		for (int i = 0; i < expectedGridYValues.size(); i++) {
			assertEquals(expectedGridYValues.get(i), actualGridYValues.get(i));
		}
		//
		Integer[][] actualBtp = t.getBasicTabSymbolProperties();
		assertEquals(expectedBtp.length, actualBtp.length);
		for (int i = 0; i < expectedBtp.length; i++) {
			assertEquals(expectedBtp[i].length, actualBtp[i].length);
			for (int j = 0; j < expectedBtp[i].length; j++) {
				assertEquals(expectedBtp[i][j], actualBtp[i][j]);
			}
		}
	}


	public void testMakeTablatureChords() {
		Tablature t = new Tablature();
		t.setEncoding(new Encoding(encodingTestpiece));
		t.setUndiminutedMeterInfo();
		t.setDiminutions();
		t.setMeterInfo();
		t.setBasicTabSymbolProperties();
		t.setNormaliseTuning(false);

		List<List<TabSymbol>> expected = new ArrayList<List<TabSymbol>>();
		TabSymbolSet tss = t.getEncoding().getTabSymbolSet();
		// Chord 0
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a5", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("b2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 1
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("i2", tss),
			TabSymbol.getTabSymbol("a1", tss)	
		}));
		// Chord 2
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("d6", tss)
		})); 
		// Chord 3
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("c6", tss),
			TabSymbol.getTabSymbol("a5", tss),
			TabSymbol.getTabSymbol("e4", tss),
			TabSymbol.getTabSymbol("b2", tss)
		}));
		// Chord 4
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss)
		}));
		// Chord 5
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("h5", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("b3", tss),
			TabSymbol.getTabSymbol("f2", tss)
		}));
		// Chord 6
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("b3", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 7
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a3", tss),
			TabSymbol.getTabSymbol("e2", tss)
		}));
		// Chord 8
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));
		// Chord 9-14
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("a1", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("c2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("e2", tss)}));
		expected.add(Arrays.asList(new TabSymbol[]{TabSymbol.getTabSymbol("a1", tss)}));
		// Chord 15
		expected.add(Arrays.asList(new TabSymbol[]{
			TabSymbol.getTabSymbol("a6", tss),
			TabSymbol.getTabSymbol("c4", tss),
			TabSymbol.getTabSymbol("a2", tss),
			TabSymbol.getTabSymbol("a1", tss)
		}));

		List<List<TabSymbol>> actual = t.makeTablatureChords();

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
		t.setUndiminutedMeterInfo();
		t.setDiminutions();
		t.setMeterInfo();
		t.setBasicTabSymbolProperties();
		t.setNormaliseTuning(false);
		t.setTablatureChords();

		List<Integer> expected = 
			Arrays.asList(new Integer[]{4, 4, 1, 4, 1, 5, 4, 2, 4, 1, 1, 1, 1, 1, 1, 4});
		List<Integer> actual = t.makeNumberOfNotesPerChord();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetPitchesInChordStatic() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<List<Integer>> expected = getPitchesInChord();
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		Integer[][] btp = tablature.getBasicTabSymbolProperties();
		int lowestNoteIndex = 0;
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(Tablature.getPitchesInChord(btp, lowestNoteIndex));
			lowestNoteIndex += tablatureChords.get(i).size();
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).size(), actual.get(i).size());
			for (int j = 0; j < expected.get(i).size(); j++) {
				assertEquals(expected.get(i).get(j), actual.get(i).get(j));
			}
		}
	}


	public void testGetMetricPosition() {
		// For a piece with meter changes
		Tablature t1 = new Tablature(encodingTestGetMeterInfo, false);
		List<Rational[]> expected = getMetricPositions().get(1);

		// For a piece with no meter changes
		Tablature t2 = new Tablature(encodingTestpiece, false);
		expected.addAll(getMetricPositions().get(0));

		List<Rational[]> actual = new ArrayList<Rational[]>();
		Integer[][] btp1 = t1.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo1 = t1.getMeterInfo();
		for (int i = 0; i < btp1.length; i++) {
			Rational currMetricTime = 
				new Rational(btp1[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Tablature.getMetricPosition(currMetricTime, meterInfo1));
		}
		Integer[][] btp2 = t2.getBasicTabSymbolProperties();
		List<Integer[]> meterInfo2 = t2.getMeterInfo();
		for (int i = 0; i < btp2.length; i++) {
			Rational currMetricTime = 
				new Rational(btp2[i][Tablature.ONSET_TIME], Tablature.SRV_DEN);
			currMetricTime.reduce();
			actual.add(Tablature.getMetricPosition(currMetricTime, meterInfo2));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetMinimumDurationOfNote() {
		Tablature tab = new Tablature(encodingTestpiece, true);
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
		NotationSystem system = trans.getPiece().getScore();
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


	public void testRationalToIntDur() {
		List<Rational> rs = Arrays.asList(new Rational[]{				
			new Rational(1, 2), new Rational(3, 4), new Rational(15, 16), new Rational(5, 4)
		});
		List<Integer> expected = Arrays.asList(new Integer[]{48, 72, 90, 120});

		List<Integer> actual = new ArrayList<>();
		for (Rational r : rs) {
			actual.add(Tablature.rationalToIntDur(r));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testDiminuteMeter() {
		List<Rational> expected = new ArrayList<>();
		expected.add(new Rational(2, 1)); // 2/2, dim = 2
		expected.add(new Rational(4, 2)); // 4/4, dim = 2
		expected.add(new Rational(4, 1)); // 4/4, dim = 4
		expected.add(new Rational(2, 4)); // 2/2, dim = -2
		expected.add(new Rational(4, 8)); // 4/4, dim = -2
		expected.add(new Rational(4, 16)); // 4/4, dim = -4

		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);
		List<Rational> meters = Arrays.asList(new Rational[]{
			twoTwo, fourFour, fourFour, twoTwo, fourFour, fourFour
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(Tablature.diminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testUndiminutedMeter() {
		Rational twoTwo = new Rational(2, 2);
		Rational fourFour = new Rational(4, 4);

		List<Rational> expected = new ArrayList<>();
		expected.add(twoTwo); // 2/1, dim = 2
		expected.add(fourFour); // 4/2, dim = 2
		expected.add(fourFour); // 4/1, dim = 4
		expected.add(twoTwo); // 2/4, dim = -2
		expected.add(fourFour); // 4/8, dim = -2
		expected.add(fourFour); // 4/16, dim = -4

		List<Rational> meters = Arrays.asList(new Rational[]{
			new Rational(2, 1), new Rational(4, 2), new Rational(4, 1), 
			new Rational(2, 4), new Rational(4, 8), new Rational(4, 16)
		});
		List<Integer> dims = Arrays.asList(new Integer[]{2, 2, 4, -2, -2, -4});
		List<Rational> actual = new ArrayList<>();
		for (int i = 0; i < meters.size(); i++) {
			actual.add(Tablature.undiminuteMeter(meters.get(i), dims.get(i)));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetDiminutionStatic() {				
		List<List<Rational>> allMetricTimes = new ArrayList<>();
		// For testGetMeterInfo
		List<Rational> metricTimes1 = new ArrayList<>();
		metricTimes1.add(new Rational(0, 1)); // start bar 0 (anacrusis)
		metricTimes1.add(new Rational(4, 4)); // in bar 1
		metricTimes1.add(new Rational(11, 4)); // start bar 2
		metricTimes1.add(new Rational(23, 4)); // in bar 3
		metricTimes1.add(new Rational(31, 4)); // start bar 4
		metricTimes1.add(new Rational(45, 4)); // in bar 5
		metricTimes1.add(new Rational(47, 4)); // start bar 6
		metricTimes1.add(new Rational(52, 4)); // in bar 7
		metricTimes1.add(new Rational(209, 16)); // start bar 8
		allMetricTimes.add(metricTimes1);
		// For testpiece
		List<Rational> metricTimes2 = new ArrayList<>();
		metricTimes2.add(new Rational(0, 1)); // start bar 1
		metricTimes2.add(new Rational(7, 4)); // in bar 2
		metricTimes2.add(new Rational(8, 4)); // start bar 3
		metricTimes2.add(new Rational(10, 4)); // in bar 3
		allMetricTimes.add(metricTimes2);

		List<Integer> expected = new ArrayList<>();
		// For testGetMeterInfo
		expected.addAll(Arrays.asList(new Integer[]{2, 2, 2, 4, 4, 1, 1, 1, -2}));
		// For testPiece
		expected.addAll(Arrays.asList(new Integer[]{1, 1, 1, 1}));

		List<Integer> actual = new ArrayList<>();
		List<Tablature> tabs = Arrays.asList(new Tablature[]{
			new Tablature(encodingTestGetMeterInfo, false),
			new Tablature(encodingTestpiece, false)});		
		for (int i = 0; i < tabs.size(); i++) {
			Tablature t = tabs.get(i);
			for (Rational mt : allMetricTimes.get(i)) {
				actual.add(Tablature.getDiminution(mt, t.getMeterInfo()));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testConvertTabSymbolToNote() {		
		List<Note> expected = new ArrayList<>();
		Integer[][] btp = getBtp();
		for (Integer[] in : btp) {
			expected.add(new Note(
				new ScoreNote(
					new ScorePitch(in[Tablature.PITCH]), 
					new Rational(in[Tablature.ONSET_TIME], Tablature.SRV_DEN), 
					new Rational(in[Tablature.MIN_DURATION], Tablature.SRV_DEN)
				), 
				new PerformanceNote(
					0, 120000, 90, // default instance variable values; see PerformanceNote()
					in[Tablature.PITCH] 
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


	public void testGetPitchesInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<List<Integer>> expected = getPitchesInChord();
		List<List<Integer>> actual = new ArrayList<List<Integer>>();
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
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
	
	
	public void testGetUnisonInfo() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

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
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			actual.add(tablature.getUnisonInfo(i));
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


	public void testGetNumberOfUnisonsInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Integer> expected = 
			Arrays.asList(new Integer[]{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		List<Integer> actual = new ArrayList<Integer>();
		for (int i = 0; i < tablature.getTablatureChords().size(); i++) {
			actual.add(tablature.getNumberOfUnisonsInChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
	}


	public void testGetCourseCrossingInfo() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<List<Integer[]>> expected = new ArrayList<>();
		expected.add(null);
		List<Integer[]> chord1 = new ArrayList<>();
		chord1.add(new Integer[]{72, 69, 2, 3});
		expected.add(chord1);
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
		expected.add(null);
		expected.add(null);
		expected.add(null);
		expected.add(null);

		List<List<Integer[]>> actual = new ArrayList<>();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(tablature.getCourseCrossingInfo(i));
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


	public void testGetNumberOfCourseCrossingsInChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);

		List<Integer> expected = Arrays.asList(new Integer[]{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

		List<Integer> actual = new ArrayList<Integer>();
		List<List<TabSymbol>> tablatureChords = tablature.getTablatureChords();
		for (int i = 0; i < tablatureChords.size(); i++) {
			actual.add(tablature.getNumberOfCourseCrossingsInChord(i));
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGenerateChordDictionary() {
		Tablature tablature = new Tablature(encodingTestpiece, true);

		List<List<Integer>> pitchesInChord = getPitchesInChordTransposed();
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
		assertEquals(-2, new Tablature(encodingTestpiece, false).getTranspositionInterval());
	}


	public void testGetMetricTimePerChord() {
		Tablature t1 = new Tablature(encodingTestpiece, false);
		Tablature t2 = new Tablature(encodingTestGetMeterInfo, false);

		List<List<Rational[]>> allOnsetTimes = getOnsetTimes();
		List<Rational[]> expected = new ArrayList<>();
		// a. Excluding rests
		// For a piece with no meter changes
		allOnsetTimes.get(0).forEach(item -> { 
			if (!item[1].equals(Rational.ZERO)) { expected.add(item);}});
		// For a piece with meter changes
		allOnsetTimes.get(1).forEach(item -> {
			if (!item[1].equals(Rational.ZERO)) { expected.add(item);}});
		// b. Including rests
		// For a piece with no meter changes
		allOnsetTimes.get(0).forEach(item -> expected.add(item));
		// For a piece with meter changes
		allOnsetTimes.get(1).forEach(item -> expected.add(item));

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


	public void testGetMinimumDurationPerChord() {
		Tablature t1 = new Tablature(encodingTestpiece, false);
		Tablature t2 = new Tablature(encodingTestGetMeterInfo, false);

		List<Rational> expected = new ArrayList<>();
		// testpiece
		expected.add(QUARTER);
		//
		expected.add(DOTTED_EIGHTH); expected.add(SIXTEENTH); 
		expected.addAll(Collections.nCopies(2, EIGHTH));
		expected.add(QUARTER); 
		expected.addAll(Collections.nCopies(2, EIGHTH));
		//
		expected.addAll(Collections.nCopies(2, SIXTEENTH));
		expected.addAll(Collections.nCopies(4, THIRTY_SECOND));
		expected.add(QUARTER); 
		expected.add(QUARTER);
		// testGetMeterInfo
		expected.addAll(Collections.nCopies(3, QUARTER));
		//
		expected.add(DOTTED_HALF); expected.add(QUARTER);
		expected.add(WHOLE);
		//
		expected.add(HALF); expected.addAll(Collections.nCopies(2, EIGHTH));
		expected.addAll(Collections.nCopies(4, SIXTEENTH));
		expected.add(WHOLE);
		//
		expected.add(WHOLE);
		expected.add(HALF); expected.addAll(Collections.nCopies(2, QUARTER));
		expected.add(WHOLE);
		// 
		expected.add(DOTTED_HALF); expected.addAll(Collections.nCopies(2, EIGHTH));
		expected.add(BREVE);
		//
		expected.add(HALF);
		expected.add(HALF);
		//
		expected.addAll(Collections.nCopies(4, EIGHTH));
		expected.addAll(Collections.nCopies(2, EIGHTH)); expected.add(QUARTER);
		//
		expected.add(EIGHTH);
		expected.addAll(Collections.nCopies(3, SIXTEENTH));
		//
		expected.add(QUARTER);
		expected.add(SIXTEENTH); expected.addAll(Collections.nCopies(2, THIRTY_SECOND));
		expected.add(EIGHTH);
		
		List<Rational> actual = t1.getMinimumDurationPerChord();
		actual.addAll(t2.getMinimumDurationPerChord());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		} 
		assertEquals(expected, actual);
	}


	public void testGetTripletOnsetPairs() {
		// No triplets
		Tablature tablature = new Tablature(encodingTestpiece, true);
		
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
			new Encoding(encodingTestpiece).getName(), true), true);
		
		expected.add(new Rational[]{new Rational(5, 4), new Rational(17, 12), 
			new Rational(RhythmSymbol.semiminim.getDuration(), 1)});
		expected.add(new Rational[]{new Rational(10, 4), new Rational(17, 6), 
			new Rational(RhythmSymbol.minim.getDuration(), 1)});
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
		Tablature t1 = new Tablature(encodingTestpiece, true);
		Tablature t2 = new Tablature(encodingNewsidler, true);

		List<String[]> expected = new ArrayList<>();
		// t1
		expected.add(new String[]{"McC3", "1", "1"});
		// t2
		expected.add(new String[]{"MO3.M34", "42", "42"});
		expected.add(new String[]{"McC3", "50", "46"});

		List<String[]> actual = t1.getMensurationSigns();
		actual.addAll(t2.getMensurationSigns());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).length, actual.get(i).length);
			for (int j = 0; j < expected.get(i).length; j++) {
				assertEquals(expected.get(i)[j], actual.get(i)[j]);
			}
		}
	}


	public void testGetDiminution() {				
		List<Integer> expected = new ArrayList<>();
		// For testGetMeterInfo
		expected.addAll(Arrays.asList(new Integer[]{2, 2, 2, 4, 4, 1, 1, 1, -2}));
		// For testpiece
		expected.addAll(Arrays.asList(new Integer[]{1, 1, 1}));

		List<Integer> actual = new ArrayList<>();
		List<Tablature> tabs = Arrays.asList(new Tablature[]{
			new Tablature(encodingTestGetMeterInfo, false),
			new Tablature(encodingTestpiece, false)});
		for (Tablature t : tabs) {
			// Take into account anacrusis
			boolean anacrusis = t.getNumberOfMetricBars()[1] == 1;
			int startBar = anacrusis ? 0 : 1;			
			int stopBar = t.getNumberOfMetricBars()[0];
			for (int bar = startBar; bar <= stopBar; bar++) {
				actual.add(t.getDiminution(bar));
			}
		}

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetNumberOfTabBars() {
		List<Integer> expected = Arrays.asList(new Integer[]{9, 4, 96});
		List<Integer> actual = new ArrayList<>();
		// No decorative opening barlines and non-metric barlines
		actual.add(new Tablature(encodingTestGetMeterInfo, false).getNumberOfTabBars());
		// Non-metric barlines
		actual.add(new Tablature(encodingTestpiece, false).getNumberOfTabBars());
		// Decorative opening barline and non-metric barlines 
		actual.add(new Tablature(encodingNewsidler, true).getNumberOfTabBars());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}
		assertEquals(expected, actual);
	}


	public void testGetNumberOfMetricBars() {		
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{8, 1});
		expected.add(new Integer[]{3, 0});

		List<Integer[]> actual = new ArrayList<>();
		actual.add(new Tablature(encodingTestGetMeterInfo, false).getNumberOfMetricBars());
		actual.add(new Tablature(encodingTestpiece, false).getNumberOfMetricBars());

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testMapTabBarsToMetricBars() {		
		List<Integer[]> expected = new ArrayList<>();
		expected.add(new Integer[]{1, 1});
		expected.add(new Integer[]{2, 2});
		expected.add(new Integer[]{3, 3});
		expected.add(new Integer[]{4, 3});

		List<Integer[]> actual = 
			new Tablature(encodingTestpiece, true).mapTabBarsToMetricBars();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
	  		assertEquals(expected.get(i).length, actual.get(i).length);
	  		for (int j = 0; j < expected.get(i).length; j++) {
	  			assertEquals(expected.get(i)[j], actual.get(i)[j]);
	  		}
		}
	}


	public void testGetBasicTabSymbolPropertiesChord() {
		Tablature tablature = new Tablature(encodingTestpiece, false);
		Integer[][] btp = getBtp();

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
		int actual = new Tablature(encodingTestpiece, false).getNumberOfNotes();
		assertEquals(expected, actual);
		
	}


	public void testGetIndicesPerChord() {
		Tablature t = new Tablature(encodingTestpiece, false);

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
		int actual = new Tablature(encodingTestpiece, false).getLargestTablatureChord();
		assertEquals(expected, actual);
	}


	public void testReverse() {
		Tablature t = new Tablature(encodingTestpiece, false);
		Integer[][] btp = getBtp();
		
		List<List<Integer>> list1 = ...
		list1.forEach(item -> {Collections.sort(item);});

		List<Integer> list2 = ...
		list2.forEach(item -> item += 2);
		
		// Chord 15
		Integer[][] expected = new Integer[btp.length][btp[0].length];
		expected[0] = btp[35];
		expected[0][Tablature.ONSET_TIME] = 0;
		expected[0][Tablature.MAX_DURATION] = 0;
		expected[0][Tablature.CHORD_SEQ_NUM] = 0;
		expected[0][Tablature.TAB_EVENT_SEQ_NUM] = 0;
		
		expected0[Tablature.ONSET_TIME] = 0;
		Integer[] expected01 = btp[36];
		expected0[Tablature.ONSET_TIME] = 0;
		Integer[] expected02 = btp[37];
		expected0[Tablature.ONSET_TIME] = 0;
		Integer[] expected03 = btp[38];
		expected0[Tablature.ONSET_TIME] = 0;
//		Integer[][] expected0 = Arrays.copyOfRange(btp, 35, 39);
		// Chord 14-9
		Integer[][] expected1 = ArrayUtils.addAll(expected0, Arrays.copyOfRange(btp, 34, 35));
		Integer[][] expected2 = ArrayUtils.addAll(expected1, Arrays.copyOfRange(btp, 33, 34));
		Integer[][] expected3 = ArrayUtils.addAll(expected2, Arrays.copyOfRange(btp, 32, 33));
		Integer[][] expected4 = ArrayUtils.addAll(expected3, Arrays.copyOfRange(btp, 31, 32));
		Integer[][] expected5 = ArrayUtils.addAll(expected4, Arrays.copyOfRange(btp, 30, 31));
		Integer[][] expected6 = ArrayUtils.addAll(expected5, Arrays.copyOfRange(btp, 29, 30));
		// Chord 8
		Integer[][] expected7 = ArrayUtils.addAll(expected6, Arrays.copyOfRange(btp, 25, 29));
		// Chord 7
		Integer[][] expected8 = ArrayUtils.addAll(expected7, Arrays.copyOfRange(btp, 23, 25));		
		// Chord 6
		Integer[][] expected9 = ArrayUtils.addAll(expected8, Arrays.copyOfRange(btp, 19, 23));
		// Chord 5
		Integer[][] expected10 = ArrayUtils.addAll(expected9, Arrays.copyOfRange(btp, 14, 19));
		// Chord 4
		Integer[][] expected11 = ArrayUtils.addAll(expected10, Arrays.copyOfRange(btp, 13, 14));		
		// Chord 3
		Integer[][] expected12 = ArrayUtils.addAll(expected11, Arrays.copyOfRange(btp, 9, 13));
		// Chord 2
		Integer[][] expected13 = ArrayUtils.addAll(expected12, Arrays.copyOfRange(btp, 8, 9));
		// Chord 1
		Integer[][] expected14 = ArrayUtils.addAll(expected13, Arrays.copyOfRange(btp, 4, 8));
		// Chord 0
		Integer[][] expected = ArrayUtils.addAll(expected14, Arrays.copyOfRange(btp, 0, 4));

		Integer[][] actual = Tablature.reverse(t).getBasicTabSymbolProperties();
		
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++) {
			System.out.println("i = " + i);
			assertEquals(expected[i].length, actual[i].length);
			for (int j = 0; j < expected[i].length; j++) {
				System.out.println("j = " + j);
				assertEquals(expected[i][j], actual[i][j]);
			}
		}
	}

}
