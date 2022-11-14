package analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uos.fmt.musitech.data.score.NotationChord;
import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.container.NoteSequence;
import de.uos.fmt.musitech.utility.math.Rational;
import exports.MEIExport;
import imports.MIDIImport;
import representations.Tablature;
import representations.Transcription;
import structure.Timeline;
import tbp.Encoding;
import tbp.TabSymbol;
import tools.ToolBox;
import utility.DataConverter;

public class Analyser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> pieces = Arrays.asList(new String[]{
			"ah_golden_hairs-NEW",
			"an_aged_dame-II",
			"as_caesar_wept-II",
			"blame_i_confess-II",
			"in_angels_weed-II",
			"o_lord_bow_down-II",
			"o_that_we_woeful_wretches-NEW",
			"quis_me_statim-II",
			"rejoyce_unto_the_lord-NEW",
			"sith_death-NEW",
			"the_lord_is_only_my_support-NEW",
			"the_man_is_blest-NEW",
			"while_phoebus-II"
		});

		// 3vv
//		pieces = Arrays.asList(new String[]{
//			"bach-WTC1-fuga_2-BWV_847",
//			"bach-WTC1-fuga_3-BWV_848",
//			"bach-WTC1-fuga_6-BWV_851",
//			"bach-WTC1-fuga_7-BWV_852",
//			"bach-WTC1-fuga_8-BWV_853",
//			"bach-WTC1-fuga_9-BWV_854",
//			"bach-WTC1-fuga_11-BWV_856",
//			"bach-WTC1-fuga_13-BWV_858",
//			"bach-WTC1-fuga_15-BWV_860",
//			"bach-WTC1-fuga_19-BWV_864",
//			"bach-WTC1-fuga_21-BWV_866",
//			"bach-WTC2-fuga_1-BWV_870",
//			"bach-WTC2-fuga_3-BWV_872",
//			"bach-WTC2-fuga_4-BWV_873",
//			"bach-WTC2-fuga_6-BWV_875",
//			"bach-WTC2-fuga_10-BWV_879",
//			"bach-WTC2-fuga_11-BWV_880",
//			"bach-WTC2-fuga_12-BWV_881",
//			"bach-WTC2-fuga_13-BWV_882",
//			"bach-WTC2-fuga_14-BWV_883",
//			"bach-WTC2-fuga_15-BWV_884",
//			"bach-WTC2-fuga_18-BWV_887",
//			"bach-WTC2-fuga_19-BWV_888",
//			"bach-WTC2-fuga_20-BWV_889",
//			"bach-WTC2-fuga_21-BWV_890",
//			"bach-WTC2-fuga_24-BWV_893"
//		});
		
		// 4vv
//		pieces = Arrays.asList(new String[]{
//			"bach-WTC1-fuga_1-BWV_846",
//			"bach-WTC1-fuga_5-BWV_850",
//			"bach-WTC1-fuga_12-BWV_857",
//			"bach-WTC1-fuga_14-BWV_859",
//			"bach-WTC1-fuga_16-BWV_861",
//			"bach-WTC1-fuga_17-BWV_862",
//			"bach-WTC1-fuga_18-BWV_863",
//			"bach-WTC1-fuga_20-BWV_865",
//			"bach-WTC1-fuga_23-BWV_868",
//			"bach-WTC1-fuga_24-BWV_869",
//			"bach-WTC2-fuga_2-BWV_871",
//			"bach-WTC2-fuga_5-BWV_874",
//			"bach-WTC2-fuga_7-BWV_876",
//			"bach-WTC2-fuga_8-BWV_877",
//			"bach-WTC2-fuga_9-BWV_878",
//			"bach-WTC2-fuga_16-BWV_885",
//			"bach-WTC2-fuga_17-BWV_886",
//			"bach-WTC2-fuga_22-BWV_891",
//			"bach-WTC2-fuga_23-BWV_892"
//		});

		// 5vv
//		pieces = Arrays.asList(new String[]{
//			"bach-WTC1-fuga_4-BWV_849-split_at_44-65-86_1", 
//			"bach-WTC1-fuga_4-BWV_849-split_at_44-65-86_2",
//			"bach-WTC1-fuga_4-BWV_849-split_at_44-65-86_3",
//			"bach-WTC1-fuga_4-BWV_849-split_at_44-65-86_4",
//			"bach-WTC1-fuga_22-BWV_867-split_at_37_1",
//			"bach-WTC1-fuga_22-BWV_867-split_at_37_2",
//		});
		
		// inv 2vv
//		pieces = Arrays.asList(new String[]{
//			"bach-inv-inventio_1-BWV_772",
//			"bach-inv-inventio_2-BWV_773",
//			"bach-inv-inventio_3-BWV_774",
//			"bach-inv-inventio_4-BWV_775",
//			"bach-inv-inventio_5-BWV_776",
//			"bach-inv-inventio_6-BWV_777",
//			"bach-inv-inventio_7-BWV_778",
//			"bach-inv-inventio_8-BWV_779",
//			"bach-inv-inventio_9-BWV_780",
//			"bach-inv-inventio_10-BWV_781",
//			"bach-inv-inventio_11-BWV_782",
//			"bach-inv-inventio_12-BWV_783",
//			"bach-inv-inventio_13-BWV_784",
//			"bach-inv-inventio_14-BWV_785",
//			"bach-inv-inventio_15-BWV_786"
//		});
		
		// int 3vv
		pieces = Arrays.asList(new String[]{
			"bach-inv-inventio_1-BWV_787",
			"bach-inv-inventio_2-BWV_788",
			"bach-inv-inventio_3-BWV_789",
			"bach-inv-inventio_4-BWV_790",
			"bach-inv-inventio_5-BWV_791",
			"bach-inv-inventio_6-BWV_792",
			"bach-inv-inventio_7-BWV_793",
			"bach-inv-inventio_8-BWV_794",
			"bach-inv-inventio_9-BWV_795",
			"bach-inv-inventio_10-BWV_796",
			"bach-inv-inventio_11-BWV_797",
			"bach-inv-inventio_12-BWV_798",
			"bach-inv-inventio_13-BWV_799",
			"bach-inv-inventio_14-BWV_800",
			"bach-inv-inventio_15-BWV_801"	
		});

		String path = "F:/research/projects/byrd/MIDI/"; // for models
		path = "F:/research/projects/byrd/mapped/"; // for mapped ground truth
//		path = "F:/research/data/MIDI/bach-WTC/thesis/3vv/";
//		path = "F:/research/data/MIDI/bach-WTC/thesis/4vv/";
//		path = "F:/research/data/MIDI/bach-WTC/thesis/5vv/";
//		path = "F:/research/data/MIDI/bach-inv/thesis/2vv/";
		path = "F:/research/data/MIDI/bach-inv/thesis/3vv/";
		
		boolean asMIDIPitches = true;
//		List<String> voiceNames = Arrays.asList(new String[]{"medius", "contra", "tenor", "bassus"});
		List<String> voiceNames = Arrays.asList(new String[]{"0", "1", "2"});
		List<String> voicePairNames = Arrays.asList(new String[]{"M/C", "C/T", "T/B"});
		List<String> res;
//		res = analyseVoiceRanges(path, pieces, voiceNames, asMIDIPitches);
//		res = analyseVoiceRangeOverlap(path, pieces, voicePairNames);
		res = analyseVoiceCrossings(path, pieces, voiceNames);
		System.out.println(res.get(0));
		System.out.println();
		System.out.println(res.get(1));
		System.out.println();
		System.out.println(res.get(2));
		System.out.println();
		System.exit(0);

//		// 1. Tablature data
//		List<String> pieceNames = new ArrayList<String>();    
//		pieceNames.add("Ochsenkun 1558 - Absolon fili mi");
//		pieceNames.add("Ochsenkun 1558 - In exitu Israel de Egipto");
//		pieceNames.add("Ochsenkun 1558 - Qui habitat");
//		pieceNames.add("Rotta 1546 - Bramo morir per non patir piu morte");
//		pieceNames.add("Phalese 1547 - Tant que uiuray [a4]");
//		pieceNames.add("Ochsenkun 1558 - Herr Gott lass dich erbarmen");
//		pieceNames.add("Abondante 1548 - mais mamignone"); 
//		pieceNames.add("Phalese 1563 - LAs on peult");
//		pieceNames.add("Barbetta 1582 - Il nest plaisir");

//		pieceNames.add("Judenkunig 1523 - Elslein liebes Elslein");
//		pieceNames.add("Newsidler 1536 - Disant adiu madame");
//		pieceNames.add("Newsidler 1544 - Nun volget Lalafete");
//		pieceNames.add("Phalese 1547 - Tant que uiuray [a3]");
//		pieceNames.add("Pisador 1552 - Pleni de la missa misma");
//		pieceNames.add("Ochsenkun 1558 - Cum Sancto spiritu");
//		pieceNames.add("Adriansen 1584 - D'Vn si bel foco");
//		pieceNames.add("Ochsenkun 1558 - Inuiolata integra");

//		for (String s : pieceNames) {
//			File encodingFile = new File(ExperimentRunner.encodingsPath + s + ".txt"); 
//			File midiFile = new File(ExperimentRunner.tabMidiPath + s);		
//			Tablature tablature = new Tablature(encodingFile);
//			Transcription transcription = new Transcription(midiFile, encodingFile);
//			System.out.println(transcription.getVoiceCrossingInformation(tablature));
//		}
//		Integer[] result = hasChordsAtGivenDistances(pieceNames);    
//		System.out.println(getDurationsInfo(pieceNames));
//		System.exit(0);

		// 2. Bach data
		String folderName = null;
		String pieceName = null;		
		// WTC, Book I
//		folderName = ExperimentRunner.bachMidiPath + "WTC/2vv/";
//		pieceName = "Bach - WTC1, Fuga 10 in e minor (BWV 855)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 10 in e minor (BWV 855)";

//		folderName = ExperimentRunner.bachMidiPath + "WTC/3vv/";
//		pieceName = "Bach - WTC1, Fuga 2 in c minor (BWV 847)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 2 in c minor (BWV 847)";
//		pieceName = "Bach - WTC1, Fuga 3 in C# major (BWV 848)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 3 in C# major (BWV 848)";
//		pieceName = "Bach - WTC1, Fuga 6 in d minor (BWV 851)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 6 in d minor (BWV 851)";
//		pieceName = "Bach - WTC1, Fuga 7 in Eb major (BWV 852)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 7 in Eb major (BWV 852)";
//		pieceName = "Bach - WTC1, Fuga 8 in d# minor (BWV 853)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 8 in d# minor (BWV 853)";
//		pieceName = "Bach - WTC1, Fuga 9 in E major (BWV 854)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 9 in E major (BWV 854)";
//		pieceName = "Bach - WTC1, Fuga 11 in F major (BWV 856)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 11 in F major (BWV 856)";
//		pieceName = "Bach - WTC1, Fuga 13 in F# major (BWV 858)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 13 in F# major (BWV 858)";
//		pieceName = "Bach - WTC1, Fuga 15 in G major (BWV 860)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 15 in G major (BWV 860)";
//		pieceName = "Bach - WTC1, Fuga 19 in A major (BWV 864)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 19 in A major (BWV 864)";
//		pieceName = "Bach - WTC1, Fuga 21 in Bb major (BWV 866)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 21 in Bb major (BWV 866)";

//		folderName = ExperimentRunner.bachMidiPath + "WTC/4vv/";
//		pieceName = "Bach - WTC1, Fuga 1 in C major (BWV 846)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 1 in C major (BWV 846)";
//		pieceName = "Bach - WTC1, Fuga 5 in D major (BWV 850)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 5 in D major (BWV 850)";
//		pieceName = "Bach - WTC1, Fuga 12 in f minor (BWV 857)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 12 in f minor (BWV 857)";
//		pieceName = "Bach - WTC1, Fuga 14 in f# minor (BWV 859)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 14 in f# minor (BWV 859)";
//		pieceName = "Bach - WTC1, Fuga 16 in g minor (BWV 861)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 16 in g minor (BWV 861)";
//		pieceName = "Bach - WTC1, Fuga 17 in Ab major (BWV 862)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 17 in Ab major (BWV 862)";
//		pieceName = "Bach - WTC1, Fuga 18 in g# minor (BWV 863)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 18 in g# minor (BWV 863)";
//		pieceName = "Bach - WTC1, Fuga 20 in a minor (BWV 865)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 20 in a minor (BWV 865)";
//		pieceName = "Bach - WTC1, Fuga 23 in B major (BWV 868)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 23 in B major (BWV 868)";
//		pieceName = "Bach - WTC1, Fuga 24 in b minor (BWV 869)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 24 in b minor (BWV 869)";

//		folderName = ExperimentRunner.bachMidiPath + "WTC/5vv/";
//		pieceName = "Bach - WTC1, Fuga 4 in c# minor (BWV 849)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 4 in c# minor (BWV 849)";
//		pieceName = "Bach - WTC1, Fuga 22 in bb minor (BWV 867)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC1, Fuga 22 in bb minor (BWV 867)"; 

		// WTC, Book II
//		folderName = ExperimentRunner.bachMidiPath + "WTC/3vv/";
//		pieceName = "Bach - WTC2, Fuga 1 in C major (BWV 870)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 1 in C major (BWV 870)";
//		pieceName = "Bach - WTC2, Fuga 3 in C# major (BWV 872)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 3 in C# major (BWV 872)";
//		pieceName = "Bach - WTC2, Fuga 4 in c# minor (BWV 873)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 4 in c# minor (BWV 873)";
//		pieceName = "Bach - WTC2, Fuga 6 in d minor (BWV 875)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 6 in d minor (BWV 875)";
//		pieceName = "Bach - WTC2, Fuga 10 in e minor (BWV 879)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 10 in e minor (BWV 879)";
//		pieceName = "Bach - WTC2, Fuga 11 in F major (BWV 880)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 11 in F major (BWV 880)";
//		pieceName = "Bach - WTC2, Fuga 12 in f minor (BWV 881)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 12 in f minor (BWV 881)";
//		pieceName = "Bach - WTC2, Fuga 13 in F# major (BWV 882)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 13 in F# major (BWV 882)";
//		pieceName = "Bach - WTC2, Fuga 14 in f# minor (BWV 883)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 14 in f# minor (BWV 883)";
//		pieceName = "Bach - WTC2, Fuga 15 in G major (BWV 884)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 15 in G major (BWV 884)";
//		pieceName = "Bach - WTC2, Fuga 18 in g# minor (BWV 887)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 18 in g# minor (BWV 887)";
//		pieceName = "Bach - WTC2, Fuga 19 in A major (BWV 888)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 19 in A major (BWV 888)";
//		pieceName = "Bach - WTC2, Fuga 20 in a minor (BWV 889)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 20 in a minor (BWV 889)";
//		pieceName = "Bach - WTC2, Fuga 21 in Bb major (BWV 890)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 21 in Bb major (BWV 890)";
//		pieceName = "Bach - WTC2, Fuga 24 in b minor (BWV 893)/musedata.org/Unedited";
//		pieceName = "Bach - WTC2, Fuga 24 in b minor (BWV 893)";

		folderName = MEIExport.rootDir + "data/MIDI/bach-WTC/thesis/4vv/";

//		pieceName = "Bach - WTC2, Fuga 2 in c minor (BWV 871)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 2 in c minor (BWV 871)";
//		pieceName = "Bach - WTC2, Fuga 5 in D major (BWV 874)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 5 in D major (BWV 874)";
//		pieceName = "Bach - WTC2, Fuga 7 in Eb major (BWV 876)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 7 in Eb major (BWV 876)";
//		pieceName = "Bach - WTC2, Fuga 8 in d# minor (BWV 877)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 8 in d# minor (BWV 877)";
//		pieceName = "Bach - WTC2, Fuga 9 in E major (BWV 878)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 9 in E major (BWV 878)";
//		pieceName = "Bach - WTC2, Fuga 16 in g minor (BWV 885)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 16 in g minor (BWV 885)";
//		pieceName = "Bach - WTC2, Fuga 17 in Ab major (BWV 886)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 17 in Ab major (BWV 886)";
//		pieceName = "Bach - WTC2, Fuga 22 in bb minor (BWV 891)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 22 in bb minor (BWV 891)";
//		pieceName = "Bach - WTC2, Fuga 23 in B major (BWV 892)/musedata.org/Unedited/";
//		pieceName = "Bach - WTC2, Fuga 23 in B major (BWV 892)";

		// Inventions
//		folderName = ExperimentRunner.bachMidiPath + "inventions/2vv/";
//		pieceName = "Bach - Inventio 1 in C major (BWV 772)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 1 in C major (BWV 772)";
//		pieceName = "Bach - Inventio 2 in c minor (BWV 773)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 2 in c minor (BWV 773)";
//		pieceName = "Bach - Inventio 3 in D major (BWV 774)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 3 in D major (BWV 774)";
//		pieceName = "Bach - Inventio 4 in d minor (BWV 775)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 4 in d minor (BWV 775)";
//		pieceName = "Bach - Inventio 5 in Eb major (BWV 776)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 5 in Eb major (BWV 776)";
//		pieceName = "Bach - Inventio 6 in E major (BWV 777)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 6 in E major (BWV 777)";
//		pieceName = "Bach - Inventio 7 in e minor (BWV 778)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 7 in e minor (BWV 778)";
//		pieceName = "Bach - Inventio 8 in F major (BWV 779)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 8 in F major (BWV 779)";
//		pieceName = "Bach - Inventio 9 in f minor (BWV 780)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 9 in f minor (BWV 780)";
//		pieceName = "Bach - Inventio 10 in G major (BWV 781)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 10 in G major (BWV 781)";
//		pieceName = "Bach - Inventio 11 in g minor (BWV 782)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 11 in g minor (BWV 782)";
		pieceName = "Bach - Inventio 12 in A major (BWV 783)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 12 in A major (BWV 783)";
//		pieceName = "Bach - Inventio 13 in a minor (BWV 784)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 13 in a minor (BWV 784)";
//		pieceName = "Bach - Inventio 14 in Bb major (BWV 785)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 14 in Bb major (BWV 785)";
//		pieceName = "Bach - Inventio 15 in b minor (BWV 786)/musedata.org/Unedited/";
//		pieceName = "Bach - Inventio 15 in b minor (BWV 786)";

//		checkOnsetTimeLastNote(folderName, pieceName);
//		Transcription transcription = new Transcription(new File(TrainingManager.prefix + folderName + pieceName), null);
//		System.out.println(transcription.getVoiceCrossingInformation(null));
//		System.out.println(hasDoubleNote(folderName, pieceName));
//		System.out.println(chordSizeChecker(folderName, pieceName));
//		System.exit(0);

		List<String> threeVoiceFuguesPieceNames = null; // Dataset.getBachThreeVoiceFugues();
		List<String> fourVoiceFuguesPieceNames = null; // Dataset.getBachFourVoiceFugues();
		List<String> threeVoiceIntabsPieceNames = null; // Dataset.getTabThreeVoices();
		List<String> fourVoiceIntabsPieceNames = null; // Dataset.getTabFourVoices();

		List<String> bla = new ArrayList<String>();
		bla.add("Abondante 1548 - mais mamignone");

		folderName = MEIExport.rootDir + "data/MIDI/thesis-int/4vv/"; 

		for (String s : threeVoiceFuguesPieceNames) {
			getNumberOfVoiceCrossingPairs(folderName, s, 0);
		}
		System.exit(0);

//		folderName = "MIDI/Bach/WTC/Four-voice fugues/";
//		pieceName = "Bach - WTC1, Fuga 20 in a minor (BWV 865)";

//		String results = "";
//		results = results.concat(hasMoreThanOneUnison(folderName, pieceName));
//		for (String s : fourVoiceFuguesPieceNames) {
//			results = results.concat(hasMoreThanOneUnison(folderName, s));
//		}
//		System.out.println(results);
	}


	/**
	 * Returns a List containing
	 * <ul>
	 * <li>as element 0: a spreadsheet table</li>
	 * <li>as element 1: a LaTeX table</li>
	 * <li>as element 2: the data for a Matplotlib stacked grouped bar chart</li>
	 * </ul>
	 * 
	 * @param path
	 * @param pieces
	 * @param voiceNames
	 * @param asMIDIPitches
	 */
	static List<String> analyseVoiceRanges(String path, List<String> pieces, List<String> voiceNames, 
		boolean asMIDIPitches) {

		List<String> shortPieceNames = new ArrayList<>();
		for (String piece : pieces) {
			shortPieceNames.add(ToolBox.getShortName(piece));
		}
		boolean includeAvg = true;

		// Spreadsheet output
		String outputSpreadsheet = 
			"piece " + "\t" + voiceNames.get(0) + "\t\t" + voiceNames.get(1) + "\t\t" + 
			voiceNames.get(2) + "\t\t" + voiceNames.get(3) + "\t\t" + "\r\n" +
			"" + "\t" + "min " + "\t" + "max" + "\t" + "min " + "\t" + "max" + "\t" +
				"min " + "\t" + "max" + "\t" + "min " + "\t" + "max" + "\t" + "\r\n";

		// LaTeX output
		int numCols = 1 + (voiceNames.size()*2);
		int numRows = !includeAvg ? pieces.size() : (pieces.size() + 1);
		String[][] dataArrLaTeX = new String[numRows][numCols];

		// Python output
		String outputPython = "pieces = [";
		for (int j = 0; j < shortPieceNames.size(); j++) {
			outputPython += "'" + shortPieceNames.get(j) + "'" +
				((j != shortPieceNames.size()-1) ? "," : "," + "'avg'" + "]" + "\r\n");
		}
		List<String> outputPythonPerVoice = new ArrayList<>();
		for (String s : voiceNames) {
			outputPythonPerVoice.add(s + "_vals = [");
		}

		// Create average arrays
		List<Integer> doubleInds = new ArrayList<>();
		List<Integer> colsToSkip = Arrays.asList(new Integer[]{0});
		List<Object> listsToAvg = getListsToAvg(numCols, doubleInds, colsToSkip);
		Integer[] intsToAvg = (Integer[]) listsToAvg.get(0);
		Double[] doublesToAvg = (Double[]) listsToAvg.get(1);

		// Create output
		for (int i = 0; i < pieces.size(); i++) {
			Transcription trans = new Transcription(new File(path + pieces.get(i) + MIDIImport.EXTENSION), null);
			String shortName = shortPieceNames.get(i);

			outputSpreadsheet += shortName + "\t";
			dataArrLaTeX[i][0] = shortName;

			List<Integer[]> voiceRanges = trans.getVoiceRangeInformation();
			for (int j = 0; j < voiceRanges.size(); j++) {
				Integer[] in = voiceRanges.get(j);
				int min = in[0];
				int max = in[1];
				String minStr = asMIDIPitches ? "" + min : Analyser.getScientificNotation(min);
				String maxStr = asMIDIPitches ? "" + max : Analyser.getScientificNotation(max);

				outputSpreadsheet += minStr + "\t" + maxStr + ((j != voiceRanges.size()-1 ) ? "\t" : "\r\n");

				dataArrLaTeX[i][((j*2)+1)] = asMIDIPitches ? minStr : latexify(minStr);
				dataArrLaTeX[i][((j*2)+2)] = asMIDIPitches ? maxStr : latexify(maxStr);
				intsToAvg[((j*2)+1)] += min;
				intsToAvg[((j*2)+2)] += max;

				outputPythonPerVoice.set(j, outputPythonPerVoice.get(j) + "[" + minStr + ", " + 
					maxStr + "]" + ",");
			}
		}
		String[] avgs = 
			ToolBox.getAveragesForMixedList(intsToAvg, doublesToAvg, pieces.size(), 2, 5);
		// Add values for average over all pieces to Python output
		for (int i = 1; i < avgs.length; i+=2) {
			int voice = (i-1)/2;
			outputPythonPerVoice.set(voice, outputPythonPerVoice.get(voice) + 
				"[" + avgs[i] + ", " + avgs[i+1] + "]" + "]");
		}

		// Finalise LaTeX output 
		String latexTable = 
			ToolBox.createLaTeXTable(dataArrLaTeX, intsToAvg, doublesToAvg, 2, 5, includeAvg);
		// If using pitch names: replace avg values with pitch names 
		if (!asMIDIPitches) {
			String avgsAsPitches = avgs[0] + " & ";
			for (int j = 1; j < avgs.length; j++) {
				avgsAsPitches += latexify(getScientificNotation(Integer.parseInt(avgs[j]))) 
					+ ((j != avgs.length-1) ? " & " : " \\" + "\\" + "\r\n");
			}
			latexTable = latexTable.substring(0, latexTable.indexOf("\\hline"));
			latexTable += "\\hline" + "\r\n" + avgsAsPitches;
		}

		// Finalise Python output
		for (String s : outputPythonPerVoice) {
			outputPython += s + "\r\n";
		}

		return Arrays.asList(
			new String[]{outputSpreadsheet.trim(), latexTable.trim(), outputPython.trim()});
	}


	/**
	 * Returns a List containing
	 * <ul>
	 * <li>as element 0: a spreadsheet table</li>
	 * <li>as element 1: a LaTeX table</li>
	 * <li>as element 2: the data for a Matplotlib grouped bar chart</li>
	 * </ul>
	 * 
	 * @param path
	 * @param pieces
	 * @param voicePairNames
	 */
	static List<String> analyseVoiceRangeOverlap(String path, List<String> pieces, 
		List<String> voicePairNames) {

		List<String> shortPieceNames = new ArrayList<>();
		for (String piece : pieces) {
			shortPieceNames.add(ToolBox.getShortName(piece));
		}
		boolean includeAvg = true;

		// Spreadsheet output
		String outputSpreadsheet = 
			"piece " + "\t" + voicePairNames.get(0) + "\t" + voicePairNames.get(1) + "\t" + 
			voicePairNames.get(2) + "\t" + "\r\n";

		// LaTeX output
		int numCols = 1 + (voicePairNames.size());
		int numRows = !includeAvg ? pieces.size() : (pieces.size() + 1);
		String[][] dataArrLaTeX = new String[numRows][numCols];		

		// Python output
		String outputPython = "pieces = [";
		for (int j = 0; j < shortPieceNames.size(); j++) {
			outputPython += "'" + shortPieceNames.get(j) + "'" +
				((j != shortPieceNames.size()-1) ? "," : "," + "'avg'" + "]" + "\r\n");
		}
		List<String> outputPythonPerVoice = new ArrayList<>();
		for (String s : voicePairNames) {
			outputPythonPerVoice.add(s.replace("/", "") + "_vals = [");
		}

		// Create average arrays
		List<Integer> doubleInds = 
			IntStream.rangeClosed(1, numCols-1).boxed().collect(Collectors.toList());	
		List<Integer> colsToSkip = Arrays.asList(new Integer[]{0});
		List<Object> listsToAvg = getListsToAvg(numCols, doubleInds, colsToSkip);
		Integer[] intsToAvg = (Integer[]) listsToAvg.get(0);
		Double[] doublesToAvg = (Double[]) listsToAvg.get(1);

		// Create output
		for (int i = 0; i < pieces.size(); i++) {
			Transcription trans = new Transcription(new File(path + pieces.get(i) + MIDIImport.EXTENSION), null);
			String shortName = shortPieceNames.get(i); 

			outputSpreadsheet += shortName + "\t";
			dataArrLaTeX[i][0] = shortName;

			List<Integer[]> voiceRanges = trans.getVoiceRangeInformation();
			for (int j = 0; j < voiceRanges.size(); j++) {
				Integer[] in = voiceRanges.get(j);
				int min = in[0];
				int max = in[1];

				if (j+1 < voiceRanges.size()) {
					Integer[] inNext = voiceRanges.get(j+1);
					// Get ranges of current and next voices
					List<Integer> range =  
						IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
					List<Integer> rangeNext =  
						IntStream.rangeClosed(inNext[0], inNext[1]).boxed().collect(Collectors.toList());
					// Get overall lowest and highest value and combined range
					int totalLo = Collections.min(Arrays.asList(new Integer[]{
						Collections.min(range), Collections.min(rangeNext)}));
					int totalHi = Collections.max(Arrays.asList(new Integer[]{
						Collections.max(range), Collections.max(rangeNext)}));
					List<Integer> combRange = 	
						IntStream.rangeClosed(totalLo, totalHi).boxed().collect(Collectors.toList());
					// Check both ranges for pitches that are in both (add only once)
					List<Integer> inBoth = new ArrayList<>();
					for (int k : range) {
						if (rangeNext.contains(k)) {
							inBoth.add(k);
						}
					}
					double perc = 100 * (inBoth.size() / (double) combRange.size());
					String formatted = ToolBox.formatDouble(perc, 2, 5);
					outputSpreadsheet += formatted + ((j+1 < voiceRanges.size()-1) ? "\t" : "\r\n");
					dataArrLaTeX[i][j+1] = formatted;
					doublesToAvg[j+1] += perc;

					outputPythonPerVoice.set(j, outputPythonPerVoice.get(j) + formatted + ",");
				}
			}
		}
		String[] avgs = ToolBox.getAveragesForMixedList(intsToAvg, doublesToAvg, pieces.size(), 2, 5);
		// Add values for average over all pieces to Python output
		for (int i = 1; i < avgs.length; i++) {
			int voicePair = i-1;
			outputPythonPerVoice.set(voicePair, outputPythonPerVoice.get(voicePair) + avgs[i] + "]");
		}

		// Finalise LaTeX output 
		String latexTable = 
			ToolBox.createLaTeXTable(dataArrLaTeX, intsToAvg, doublesToAvg, 2, 5, includeAvg);

		// Finalise Python output
		for (String s : outputPythonPerVoice) {
			outputPython += s + "\r\n";
		}

		return Arrays.asList(new String[]{outputSpreadsheet.trim(), latexTable.trim(), outputPython.trim()});
	}


	/**
	 * Returns a List containing
	 * <ul>
	 * <li>as element 0: a spreadsheet table</li>
	 * <li>as element 1: a LaTeX table</li>
	 * <li>as element 2: the data for a Matplotlib grouped bar chart</li>
	 * </ul>
	 * 
	 * @param path
	 * @param pieces
	 * @param voiceNames
	 */
	static List<String> analyseVoiceCrossings(String path, List<String> pieces, 
		List<String> voiceNames) {

		List<String> shortPieceNames = new ArrayList<>();
		for (String piece : pieces) {
			shortPieceNames.add(ToolBox.getShortName(piece));
		}
		boolean includeAvg = true;
		int numVoices = voiceNames.size();
//		int numVoices = // it is assumed that every piece in the dataset has the same number of voices 
//			new Transcription(new File(path + pieces.get(0) + MIDIImport.EXTENSION), null).getNumberOfVoices();

		// Spreadsheet output
		String outputSpreadsheet = 
			"piece " + "\t" + "N" + "\t" + "type" + "\t\t\t" + "notes involved in voice crossing" + 
				"\t\t\t\t\t" + "\r\n" +
			"\t" + "\t" + "1" + "\t" + "2" + "\t" + "all" + "\t" + "counts" + "\t\t\t\t" + 
				"percentages" + "\t\t\t\t" + "\r\n" +
			"\t\t\t\t\t" + "M" + "\t" + "C" + "\t" + "T" + "\t" + "B" + "\t" +
				"M" + "\t" + "C" + "\t" + "T" + "\t" + "B" + "\t" + "avg" + "\t" + "\r\n";

		// LaTeX output
		int numCols = (1 + 1 + 3 + (2*numVoices) + 1);
		int numRows = !includeAvg ? pieces.size() : (pieces.size() + 1);
		String[][] dataArrLaTeX = new String[numRows][numCols];

		// Python output
		String outputPython = "pieces = [";
		for (int j = 0; j < shortPieceNames.size(); j++) {
			outputPython += "'" + shortPieceNames.get(j) + "'" +
				((j != shortPieceNames.size()-1) ? "," : "," + "'avg'" + "]" + "\r\n");
		}
		List<String> outputPythonPerVoice = new ArrayList<>();
		for (String s : voiceNames) {
			outputPythonPerVoice.add(s + "_vals = [");
		}
		outputPythonPerVoice.add("avg_vals = [");

		// Create average arrays
		List<Integer> doubleInds = 
			IntStream.rangeClosed((numCols-(numVoices+1)), numCols-1).boxed().collect(Collectors.toList());
		List<Integer> colsToSkip = Arrays.asList(new Integer[]{0});
		List<Object> listsToAvg = getListsToAvg(numCols, doubleInds, colsToSkip);
		Integer[] intsToAvg = (Integer[]) listsToAvg.get(0);
		Double[] doublesToAvg = (Double[]) listsToAvg.get(1);
		
		System.out.println(Arrays.toString(intsToAvg));
		System.out.println(Arrays.toString(doublesToAvg));

		// Create output
		for (int i = 0; i < pieces.size(); i++) {
			Transcription trans = new Transcription(new File(path + pieces.get(i) + MIDIImport.EXTENSION), null);
			String shortName = shortPieceNames.get(i);

			outputSpreadsheet += shortName + "\t";
			dataArrLaTeX[i][0] = shortName;

			Integer[] vcInfo = trans.getVoiceCrossingInformation(null);
			double percSum = 0;
			for (int j = 0; j < vcInfo.length + 1; j++) {
				// ints (counts); indices 0-7
				if (j < vcInfo.length - numVoices) {
					int val = vcInfo[j];
					intsToAvg[j+1] += val;
					outputSpreadsheet += val + "\t";
					dataArrLaTeX[i][j+1] = String.valueOf(val); 
				}
				// doubles (percentages); indices 8-11
				else if (j < vcInfo.length) {
					double perc = 100*(vcInfo[j-numVoices] / (double) vcInfo[j]);
					doublesToAvg[j+1] += perc;
					percSum += perc;
					String formatted = ToolBox.formatDouble(perc, 2, 5); 
					outputSpreadsheet += formatted + "\t";
					dataArrLaTeX[i][j+1] = formatted;
					int voice = j - 2*numVoices;
//					outputPythonPerVoice.set(
//						voice, outputPythonPerVoice.get(voice) + formatted + ", ");
				}
				// double (avg percentage)
				else {
					double avgPerc = percSum / (double) numVoices;
					doublesToAvg[j+1] += avgPerc;
					String formatted = ToolBox.formatDouble(avgPerc, 2, 5); 
					outputSpreadsheet += formatted + "\r\n";
					dataArrLaTeX[i][j+1] = formatted;
					int voice = numVoices;
					outputPythonPerVoice.set(
						voice, outputPythonPerVoice.get(voice) + formatted + ", ");
				}
			}
		}
		for (String s : outputPythonPerVoice) {
			System.out.println(s);
		}
		String[] avgs = ToolBox.getAveragesForMixedList(intsToAvg, doublesToAvg, pieces.size(), 2, 5);
		// Add values for average over all pieces to Python output
		System.out.println(Arrays.toString(avgs));
		System.out.println(avgs.length);
		for (int i = avgs.length - (numVoices+1); i < avgs.length; i++) {
			System.out.println(i);
			int voice = i - ((2*numVoices)+1);
			System.out.println(voice);
//			outputPythonPerVoice.set(voice, outputPythonPerVoice.get(voice) + avgs[i] + "]");
		}

		// Finalise LaTeX output
		String latexTable = 
			ToolBox.createLaTeXTable(dataArrLaTeX, intsToAvg, doublesToAvg, 2, 5, includeAvg);

		// Finalise Python output
		for (String s : outputPythonPerVoice) {
			outputPython += s + "\r\n";
		}

		return Arrays.asList(new String[]{outputSpreadsheet.trim(), latexTable.trim(), outputPython.trim()});
	}


	public static List<Object> getListsToAvg(int numCols, List<Integer> doubleIndices, List<Integer> colsToSkip) {
		Integer[] intsToAvg = new Integer[numCols];
		Arrays.fill(intsToAvg, 0);
		Double[] doublesToAvg = new Double[numCols];
		Arrays.fill(doublesToAvg, 0.0);
		// Set to null values for the first element (name) and all double indices (intsToAvg) or 
		// al int indices (doublesToAvg)
		for (int i = 0; i < intsToAvg.length; i++) {
			if (colsToSkip.contains(i) || doubleIndices.contains(i)) {
				intsToAvg[i] = null;
			}
			if (colsToSkip.contains(i) || !doubleIndices.contains(i)) {
				doublesToAvg[i] = null;
			}
		}
		return Arrays.asList(new Object[]{intsToAvg, doublesToAvg});
	}


	public static String latexify(String s) {
		// Escape #
		s = s.replace("#", "\\#");
		// Make octaves subscript
		s = s.substring(0, s.length()-1) + "$_" + s.charAt(s.length()-1) + "$";
		return s; 
	}


	/**
	 * Returns the scientific pitch notation for the given MIDI pitch. Covers C0 (MIDI pitch 12) up
	 * to and including C8 (MIDI pitch = 108).
	 * See https://en.wikipedia.org/wiki/Scientific_pitch_notation
	 * 
	 * @param midiPitch
	 * @return
	 */
	// TESTED
	public static String getScientificNotation(int midiPitch) {
		List<Integer> pitchesInt = IntStream.rangeClosed(12, 108).boxed().collect(Collectors.toList());
		List<String> pitchesStr = Arrays.asList(new String[]{
			"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"});
		Map<Integer, String> scientific = new LinkedHashMap<Integer, String>();
		for (int i : pitchesInt) {
			// Example: 61 is in octave 4: ( (61-1)/12 ) - 1 = 4
			int octave = ((i-(i%12))/12) - 1;
			scientific.put(i, pitchesStr.get(i%12) + "" + octave);
		}
		return scientific.get(midiPitch);
	}


	/**
	 * Checks whether the given piece has 
	 * (1) Any chords with more than one unison
	 * (2) Any chords with more than two of the same pitches.
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @param pieceName
	 * @return
	 */
	private static String hasMoreThanOneUnison(String folderName, String pieceName) {
		String results = folderName + pieceName + ":" + "\n";

		Transcription transcription = new Transcription(new File(folderName + pieceName), null);
		List<Integer[]> meterInfo = transcription.getMeterInfo();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int totalNumUnisons = 0;
		int numUnisonsEqualDuration = 0;
		int numUnisonsDifferentDuration = 0;

//		System.out.println(transcription.getNoteSequence().size());
//		System.out.println(transcription.getVoiceLabels().size());
//		System.out.println(transcription.getBasicNoteProperties().length);
//		List<Integer[]> eqDur = transcription.getEqualDurationUnisonsInfo();
//		System.out.println(eqDur.size());
//
//		for (int i = 0; i < eqDur.size(); i++) {
//			System.out.println(i + " " + eqDur.get(i));
//		}
//		System.exit(0);

		List<List<Note>> chords = transcription.getChords();
		for (int i = 0; i < chords.size(); i++) {
			List<Note> currentChord = chords.get(i);

			Rational currentOnsetTime = currentChord.get(0).getMetricTime();
			Rational[] metricPos = Timeline.getMetricPosition(currentOnsetTime, meterInfo);
			String barNum = String.valueOf(metricPos[0].getNumer());
			String posInBar = "";
			if (metricPos[1].getNumer() != 0) {
				posInBar = " " + metricPos[1];
			}
			String metricPosAsString = barNum + posInBar;

			// a. Determine number of unisons
			Integer[][] unisonInfo = Transcription.getUnisonInfo(currentChord);
			if (unisonInfo != null) {
				totalNumUnisons++;
				if (unisonInfo.length == 1) {
					results = results.concat("  chord at index " + i + " (metric position " + metricPosAsString + 
//						") has more than one unison." + "\n");
						") has a unison." + "\n");
				}
				int indexLower = unisonInfo[0][1];
				int indexUpper = unisonInfo[0][2];
				int notesPreceding = 0;
				for (int j = 0; j < basicNoteProperties.length; j++) {
					int num = basicNoteProperties[j][Transcription.ONSET_TIME_NUMER];
					int denom = basicNoteProperties[j][Transcription.ONSET_TIME_DENOM];
					Rational onsetTime = new Rational(num, denom);
					if (onsetTime.equals(currentOnsetTime)) {
						notesPreceding = j;
						break;
					}
				}
				indexLower += notesPreceding;
				indexUpper += notesPreceding;
				Rational durationLower = transcription.getNoteSequence().getNoteAt(indexLower).getMetricDuration();
				Rational durationUpper = transcription.getNoteSequence().getNoteAt(indexUpper).getMetricDuration();
				if (durationLower.equals(durationUpper)) {
					numUnisonsEqualDuration++;
				}
				else {
					numUnisonsDifferentDuration++;
				}

				//b. Determine number of equal pitches
				List<Integer> pitchesInChord = new ArrayList<Integer>();
				for (Note n : currentChord) {
					pitchesInChord.add(n.getMidiPitch());
				}
				for (int pitch : pitchesInChord) {
					if (Collections.frequency(pitchesInChord, pitch) == 2) {
						results = results.concat("  chord at index " + i + " (metric position " + metricPosAsString + 
//							") has more than two notes with pitch " + pitch + "." + "\n");
							") has two notes with pitch " + pitch + "." + "\n");}	
				}
			}
		}
		results = results.concat("  Total number of unisons: " + totalNumUnisons + " (" + numUnisonsEqualDuration + 
			" with equal durations; " + numUnisonsDifferentDuration + " with different durations)." + "\n");	
		results = results.concat("\n");
		return results;
	}


	/**
	 * Checks the size of each chord and returns a String with information on which chords contain more notes
	 * (including sustained notes) than the maximum number of voices. 
	 * 
	 * Non-tablature case only.
	 * 
	 * @param folderName
	 * @param pieceName
	 * @return
	 */
	private static String chordSizeChecker(String folderName, String pieceName) {	
		String chordSizeInformation = "";

		Transcription transcription = new Transcription(new File(folderName + pieceName), null);
//		preprocessor.preprocess(null, transcription, false, new Integer(0));

		List<Integer[]> meterInfo = transcription.getMeterInfo();

		int maxNumVoices = -1;
//		if (folderName.contains("fugues")) { // for checking for overlap in individual voices
//			maxNumVoices = 1;
//		}
		if (folderName.contains("Two") || folderName.contains("Inventiones")) {
			maxNumVoices = 2;
		}	
		else if (folderName.contains("Three")) {
			maxNumVoices = 3;
		}
		else if (folderName.contains("Four")) {
			maxNumVoices = 4;
		}
		else if (folderName.contains("Five")) {
			maxNumVoices = 5;
		}

		// For each chord
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		List<List<Note>> transcriptionChords = transcription.getChords();
//		int numChords = transcription.getNumberOfChords();
		chordSizeInformation = chordSizeInformation.concat(pieceName + "\r\n");
		chordSizeInformation = chordSizeInformation.concat("number of notes = " + basicNoteProperties.length + "\r\n");
		chordSizeInformation = chordSizeInformation.concat("number of chords = " + transcriptionChords.size() + "\r\n");
//		chordSizeInformation = chordSizeInformation.concat("number of chords = " + numChords + "\r\n");
		int lowestNoteIndex = 0;
		int numNotes = 0;
		boolean addedVoiceFound = false;
//		for (int i = 0; i < numChords; i++) {
		for (int i = 0; i < transcriptionChords.size(); i++) {
			int onsetTimeNum = basicNoteProperties[lowestNoteIndex][Transcription.ONSET_TIME_NUMER];
			int onsetTimeDenom = basicNoteProperties[lowestNoteIndex][Transcription.ONSET_TIME_DENOM]; 
			Rational onsetTimeCurrentNote = new Rational(onsetTimeNum, onsetTimeDenom);     	
			int numNewOnsets = basicNoteProperties[lowestNoteIndex][Transcription.CHORD_SIZE_AS_NUM_ONSETS];
			int numSustainedNotes = 
				Transcription.getIndicesOfSustainedPreviousNotes(null, null, basicNoteProperties, lowestNoteIndex).size();
			int size = numNewOnsets + numSustainedNotes;
			numNotes += numNewOnsets;

			if (size > maxNumVoices) {
				// Determine onsetTimeAsString
				Rational[] metricPosition = Timeline.getMetricPosition(onsetTimeCurrentNote, meterInfo);
				String onsetTimeAsString = "" + metricPosition[0].getNumer();
				if (metricPosition[1].getNumer() != 0) {
					onsetTimeAsString += " " + metricPosition[1]; 
				}
				chordSizeInformation = chordSizeInformation.concat("chord of size " + size + " at chordIndex " + i +
					" (bar " + onsetTimeAsString + ")" + "\r\n");
				addedVoiceFound = true;
			}
			// Go to next chord
			lowestNoteIndex += numNewOnsets;
		}
		if (addedVoiceFound == false) {
			chordSizeInformation = chordSizeInformation.concat("No temporarily added voices.");
		}
		System.out.println("numNotes = " + numNotes);
		return chordSizeInformation;
	}


	/**
	 * Gets the onset time of the last note of the piece. 
	 * 
	 * Non-tablature case only.
	 * 
	 * @param folderName
	 * @param pieceName
	 */
	private static void checkOnsetTimeLastNote(String folderName, String pieceName) {
		Transcription transcription = new Transcription(new File(folderName + pieceName), null);
//		preprocessor.preprocess(null, transcription, false, new Integer(0));

		List<Integer[]> meterInfo = transcription.getMeterInfo();

		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		int numNotes = basicNoteProperties.length;

		Rational mt = new Rational(basicNoteProperties[numNotes - 1][Transcription.ONSET_TIME_NUMER],
			basicNoteProperties[numNotes - 1][Transcription.ONSET_TIME_DENOM]);

		Rational[] metricPosition = Timeline.getMetricPosition(mt, meterInfo);
		System.out.println("bar no   = " + (double) metricPosition[0].getNumer() / metricPosition[0].getDenom());
		System.out.println("position = " + (double) metricPosition[1].getNumer() / metricPosition[1].getDenom());
	}


	/**
	 * Finds the position of any double notes in a voice. A double note occurs where two notes in
	 *  a voice have the same onset time (but not necessarily the same offset time).
	 * 
	 * NB: Non-tablature case only.
	 * 
	 * @param folderName
	 * @param pieceName
	 * @param meter
	 */
	private static String hasDoubleNote(String folderName, String pieceName) {	
		String doubleNoteInformation = "";
		Transcription transcription = new Transcription(new File(folderName + pieceName), null);
//		preprocessor.preprocess(null, transcription, false, new Integer(0));

		List<Integer[]> meterInfo = transcription.getMeterInfo();

		// For each voice
		NotationSystem notationSystem = transcription.getPiece().getScore();
		for (int i = 0; i < notationSystem.size(); i++) {
			NotationStaff staff = notationSystem.get(i);
			if (staff.size() != 1) {
				System.out.println("STAFF SIZE ERROR");
				System.exit(0);
			}
			NotationVoice voice = staff.get(0);

			// List for each NotationChord all the Notes in it as well as all their metric times
			List<Note> allNotes = new ArrayList<Note>();
			List<Rational> allMetricTimesCurrentVoice = new ArrayList<Rational>();
			for (int j = 0; j < voice.size(); j++) {
				NotationChord notationChord = voice.get(j);
				for (Note n : notationChord) {
					allNotes.add(n);
					allMetricTimesCurrentVoice.add(n.getMetricTime());
				}
			}
			// Determine which notes have the same metric time, these are the double notes sought
			List<Note> metricTimeMoreThanOnce = new ArrayList<Note>();
			for (Note n : allNotes) {
				Rational metricTime = n.getMetricTime();
				if (Collections.frequency(allMetricTimesCurrentVoice, metricTime) > 1) {
					metricTimeMoreThanOnce.add(n);
				}
			}
			// Print out
			doubleNoteInformation += "Voice = " + i + "\r\n";
			for (Note n: metricTimeMoreThanOnce) {
				doubleNoteInformation += "More than one note in voice " + i + " in bar " + 
					Timeline.getMetricPosition(n.getMetricTime(), meterInfo)[0].getNumer() + ", beat " + 
					Timeline.getMetricPosition(n.getMetricTime(), meterInfo)[1] + " (pitch = " + n.getMidiPitch() + "))" + "\r\n";
			}    
		}
		return doubleNoteInformation;
	}


	/**
	 * Returns a String containing duration information of the list of given pieces.
	 * 
	 * @param pieceNames
	 * @return
	 */
	private static String getDurationsInfo(List<String> pieceNames) {
		String durationsInfo = "";

		List<Rational> allDurations = new ArrayList<Rational>();
		List<Rational> durationsEncountered = new ArrayList<Rational>();
//		List<Rational[]> allDurationsInclCoDs = new ArrayList<Rational[]>();
		Integer[] freqOfAllDurations = new Integer[32]; 
		Arrays.fill(freqOfAllDurations, 0);

		int numNotes = 0;
		for (int i = 0; i < pieceNames.size(); i++) {
			String pieceName = pieceNames.get(i);
			String folderName = "dataset/";
			if (pieceName.equals("testpiece")) {
				folderName = "tests/"; 
			}
			File midiFile = new File("F/PhD/data" + "MIDI/" + folderName + pieceName);
			Transcription transcription = new Transcription(midiFile, null);

			NoteSequence noteSeq = transcription.getNoteSequence();
			Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();

			// Security check
			if (noteSeq.size() != basicNoteProperties.length) {
				System.out.println("noteSeq and basicNoteProperties do not have the same size");
				System.exit(0);
			}

			for (int j = 0; j < noteSeq.size(); j++) {
				Note currentNote = noteSeq.getNoteAt(j);
				numNotes++;
				Rational currentDuration = currentNote.getMetricDuration();
				Integer[] currentBNP = basicNoteProperties[j];
				Rational currentDurationFromBNP = new Rational(currentBNP[Transcription.DUR_NUMER],
					currentBNP[Transcription.DUR_DENOM]);
				// Security check
				if (!currentDuration.equals(currentDurationFromBNP)) {
					System.out.println(j);
					System.out.println(pieceName);
					System.out.println(currentNote);
					System.out.println(currentBNP[0] + " " + currentBNP[1] + " " + currentBNP[2] + " " + 
						currentBNP[3] + " " + currentBNP[4] + " " + currentBNP[5] + " " + currentBNP[6] + " " +
						currentBNP[7]);
					System.out.println(currentDuration);
					System.out.println(currentDurationFromBNP);
					System.out.println("Duration from NoteSequence and basicNoteProperties is not the same");
					System.exit(0);
				}
				// Get the duration for each note and add it to allDurations (if it is not in there yet). Also
				// keep track of how often each duration occurs  
				else {
//					if (currentDuration.equals(new Rational(3, 4))) {
//						System.out.println("3/4 found in " + pieceName + " at metric time " + 
//						new Rational(currentBNP[Transcription.ONSET_TIME_NUMER],	currentBNP[Transcription.ONSET_TIME_DENOM]));
//					}
					if (!allDurations.contains(currentDuration)) {
						allDurations.add(currentDuration);
					}

					int numer = currentDuration.getNumer();
					int denom = currentDuration.getDenom();
					if (denom != Tablature.SRV_DEN) {
						numer *= Tablature.SRV_DEN / denom;
					}
					freqOfAllDurations[numer - 1]++; 	  	
				}	  
			}
		}

		Collections.sort(allDurations);
//		String durationsEncounteredAsString = "";
		durationsInfo = durationsInfo.concat("\r\n" + "pieces = " + pieceNames + "\r\n");
		durationsInfo = durationsInfo.concat("total number of notes: " + numNotes + "\r\n");
		durationsInfo = durationsInfo.concat("different durations encountered:") + "\r\n";
//		for (Rational r : durationsEncountered) {
//			durationsEncounteredAsString += (r + "\t");
//		}	
//		durationsInfo = durationsInfo.concat(durationsEncounteredAsString) + "\r\n";

//		durationsInfo = durationsInfo.concat("Frequency of different durations encountered:" + "\r\n");
		durationsInfo = durationsInfo.concat("duration" + "\t" + "frequency" + "\t" + "percentage (frequency/total " 
			+ "number of notes)" + "\r\n");
		for (int j = 0; j < freqOfAllDurations.length; j++) {
			int freq = freqOfAllDurations[j];
			Rational r = new Rational(j+1, 32);
			r.reduce();
			if (freq != 0) {
				durationsInfo = durationsInfo.concat(r + "\t\t" + freq + "\t\t" + ((double)freq/numNotes)*100 + "\r\n");
			}
		}

//		String allDurationsAsString = "";
//		durationsInfo = durationsInfo.concat("Durations per note:" + "\r\n");
//		int counter = 0;
//		for (Rational[] durations : allDurationsInclCoDs) {
//			String curr = "note " + counter + ": ";
//			for (Rational r : durations) {
//				curr += r + "  ";
//			}
//			allDurationsAsString += curr + "\n";
//			counter++;
//		}
//		for (Rational r : allDurations) {
//			String curr = "note " + counter + ": ";    	
//			allDurationsAsString += curr + r + "\n";
//			counter++;
//		}
//		durationsInfo = durationsInfo.concat(allDurationsAsString + "\r\n");

//		durationsInfo = durationsInfo.concat(durationsInfo + "number of notes: " + numNotes + "\r\n" + "\r\n");
		return durationsInfo;
	}


	/**
	 * Checks the given piece for the presence of chords with more than the specified number of voice crossing
	 * pairs.
	 */
	private static void getNumberOfVoiceCrossingPairs(String folderName, String pieceName, int maxNumVoiceCrossingPairs) { 
		System.out.println(pieceName);

		File midiFile = new File(folderName + pieceName);
		Transcription transcription = new Transcription(midiFile, null);
		List<Integer[]> meterInfo = transcription.getMeterInfo();
		Integer[][] basicNoteProperties = transcription.getBasicNoteProperties();
		List<List<Note>> transChords = transcription.getChords();
		List<List<Double>> allVoiceLabels = transcription.getVoiceLabels();
		List<List<List<Double>>> chordVoiceLabels = transcription.getChordVoiceLabels();
		int lowestNoteIndex = 0;
		for (int i = 0; i < transChords.size(); i++) {
			List<Integer> pitchesInChord = Transcription.getPitchesInChord(basicNoteProperties, lowestNoteIndex);
			List<List<Double>> currentChordVoiceLabels = chordVoiceLabels.get(i);
			List<List<Integer>> voicesInChord = DataConverter.getVoicesInChord(currentChordVoiceLabels);
			List<List<Integer>> pAndV = Transcription.getAllPitchesAndVoicesInChord(basicNoteProperties, pitchesInChord, 
				voicesInChord, allVoiceLabels, lowestNoteIndex);
			List<Integer> currentPitchesInChord = pAndV.get(0);
			// currentVoicesInChord must be a List<List>>
			List<List<Integer>> currentVoicesInChord = new ArrayList<List<Integer>>();
			for (int j : pAndV.get(1)) {
				int currentVoice = j;
				List<Integer> voiceWrapped = Arrays.asList(new Integer[]{currentVoice});
				currentVoicesInChord.add(voiceWrapped);
			}
			List<List<Integer>> voiceCrossingInfo = 	
				Transcription.getVoiceCrossingInformationInChord(currentPitchesInChord, currentVoicesInChord);
			if ((voiceCrossingInfo.get(1).size() / 2) > maxNumVoiceCrossingPairs) {	
				int noteIndex = -1;
				for (int j = 0; j < basicNoteProperties.length; j++) {
					if (basicNoteProperties[j][Transcription.CHORD_SEQ_NUM] == i) {
						noteIndex = j;
					}
				}
				Rational onsetTime = new Rational(basicNoteProperties[noteIndex][Transcription.ONSET_TIME_NUMER],
					basicNoteProperties[noteIndex][Transcription.ONSET_TIME_DENOM]);	
				Rational[] metricPos = Timeline.getMetricPosition(onsetTime, meterInfo);
				System.out.println("More than " + maxNumVoiceCrossingPairs + " voice crossing pair(s) in chord " + i + " (bar " + metricPos[0].getNumer() + 
					" " + metricPos[1] + ")");	
			}
			lowestNoteIndex += transChords.get(i).size();
		}
	}


	/**
	 * Checks for all the given pieces how many chords there are between each chord and the next 
	 * chord at a semibreve distance.
	 * 
	 * @param pieceNames
	 * @return
	 */
	private static Integer[] hasChordsAtGivenDistances(List<String> pieceNames) {
		Integer[] result = new Integer[33];

		int numberOfChords = 0;
//		int numberOfChordsWithChordsAtGivenDistances = 0;

		Integer[] chordsBetween = new Integer[32];
		Arrays.fill(chordsBetween, 0);      

		for (int i = 0; i < pieceNames.size(); i++) {
			String pieceName = pieceNames.get(i);
			File tablatureEncoding = 
				new File(MEIExport.rootDir + "data/encodings/" + pieceName + Encoding.EXTENSION);

			Tablature tablature = new Tablature(tablatureEncoding, false);
//			preprocessor.prepareInitialInformation(tablature, null, true);
			Integer[][] basicTabSymbolProperties = tablature.getBasicTabSymbolProperties();
			List<List<TabSymbol>> tablatureChords = tablature.getChords();

			// For each chord
			int lowestNoteIndex = 0;
			for (int j = 0; j < tablatureChords.size() - 1; j++) {      	
				numberOfChords++;     	
				int numberOfChordsBetween = 0;

				int currentOnsetTime = basicTabSymbolProperties[lowestNoteIndex][Tablature.ONSET_TIME];
				// For the next chords within a semibreve
				for (int k = j + 1; k < tablatureChords.size(); k++) {
					// Find onset time of chord at index k
					int nextOnsetTime = 0;
					for (int l = 0; l < basicTabSymbolProperties.length; l++) {
						if (basicTabSymbolProperties[l][Tablature.CHORD_SEQ_NUM] == k) {
							nextOnsetTime = basicTabSymbolProperties[l][Tablature.ONSET_TIME];
							break;
						}
					}
					if (nextOnsetTime > currentOnsetTime && nextOnsetTime < (currentOnsetTime + 16)) {
						numberOfChordsBetween++;
					}
					else if (nextOnsetTime >= currentOnsetTime + 16) {
						break;
					}
				}
				chordsBetween[numberOfChordsBetween]++;

				int currentChordSize = basicTabSymbolProperties[lowestNoteIndex][Tablature.CHORD_SIZE_AS_NUM_ONSETS];
				lowestNoteIndex += currentChordSize;
			}
		}

		result[0] = numberOfChords;
		for (int i = 0; i < 32; i++) {
			result[i + 1] = chordsBetween[i];
		}
		return result;
	}

}
