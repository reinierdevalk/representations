package imports;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.uos.fmt.musitech.data.score.NotationStaff;
import de.uos.fmt.musitech.data.score.NotationSystem;
import de.uos.fmt.musitech.data.score.NotationVoice;
import de.uos.fmt.musitech.data.score.ScoreNote;
import de.uos.fmt.musitech.data.score.ScorePitch;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.time.MetricalTimeLine;
import de.uos.fmt.musitech.data.time.TimeSignatureMarker;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.score.ScoreEditor;
import de.uos.fmt.musitech.utility.math.Rational;
import tools.ToolBox;

public class MIDIImport {

	public static void main(String[] args) {
		File dir = new File("F:/research/data/MIDI/tests/testpiece");
//		MidiImport midiImport = new MidiImport();
		Piece fullScore = importMidiFile(dir);
		ScoreEditor scoreEditor = new ScoreEditor(fullScore.getScore());
		JFrame fullScoreFrame = new JFrame(dir.toString());
		fullScoreFrame.add(new JScrollPane(scoreEditor));
		fullScoreFrame.setSize(800, 600);
		fullScoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fullScoreFrame.setVisible(true);    
	}


	/**
	 * Imports a MIDI file and converts it into a Piece, where each MIDI track forms a 
	 * NotationStaff in the Piece. 
	 * 
	 * @param f
	 * @return 
	 */
	public static Piece importMidiFile(File f) {
		String fileName = f.getName();
		URL url;
		if (!fileName.endsWith(".mid")) {
			throw new RuntimeException("ERROR: the file is not a MIDI file.");
		}
		try {
			url = f.toURI().toURL();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		Sequence sequence = null;
		try {
			sequence = MidiSystem.getSequence(url);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		boolean useThis = false;
		if (useThis) {
			Track[] tracks = sequence.getTracks();
			byte key = -1;
			byte mode = -1;
			for (int i = 0; i < tracks.length; i++) {
				Track t = tracks[i];
				for (int j = 0; j < t.size(); j++) {
					MidiEvent me = t.get(j);
					byte[] b = me.getMessage().getMessage(); 
					if (b[1] == 0x59) { // key signature
						System.out.println("found in track " + i);
						key = b[b.length-2];
						mode = b[b.length-1];
						break;
					}
				}
			}
			System.out.println("key = " + key);
			System.out.println("mM  = " + mode);
		}
//-*-		System.out.println(url);
		
		Piece p = new MidiReader().getPiece(url);
		
		p.setName(f.getName());
//		KeyMarker keyMarker = new KeyMarker(Rational.ZERO, 0); 
//		if (mode == 0) {
//			keyMarker.setAlterationNumAndMode(key, KeyMarker.Mode.MODE_MAJOR);
//		}
//		else {
//			keyMarker.setAlterationNumAndMode(key, KeyMarker.Mode.MODE_MINOR);
//		}

		// Remove any empty NotationStaffs
		NotationSystem ns = p.getScore();
		List<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0; i < ns.size(); i++) {
			NotationStaff nst = ns.get(i); 
			// If the first (and thus any other) NotationVoice in nst is empty
//			System.out.println("i = " + i);
			if (nst.get(0).size() == 0) {
				toRemove.add(i);
			}
			// Special case for 2vv inventions that have one note in tr2, ch9 and tr3, ch9  
			else if (nst.get(0).size() == 1) {
				toRemove.add(i);
			}
		}
		int removed = 0;
		for (int i : toRemove) {
			ns.remove(i - removed);
			removed++;
		}
//		System.out.println(toRemove);
//		System.out.println(ns.size());
//		System.exit(0);

//		for (int i = 0; i < ns.size(); i++) {
//			NotationStaff nst = ns.get(i); 
//			// If the first (and thus any other) NotationVoice in nst is empty
//			if (nst.get(0).size() == 0) {
//				System.out.println(i + "  is leeg" );
//			}
//		}
	
		Rational mltpl = null;
//		Rational mltpl = new Rational(27, 32);
		if (mltpl != null) {
			// Adapt note durations
			Collection<Containable> notesInPart = p.getScore().getContentsRecursiveList(null);
			for (Containable c : notesInPart) {
				if (c instanceof Note) {	
					ScorePitch pitch = new ScorePitch(((Note) c).getMidiPitch());
					Rational ons = ((Note) c).getMetricTime();
					ons = ons.mul(mltpl);
					Rational dur = ((Note) c).getMetricDuration();
					dur = dur.mul(mltpl);
					((Note) c).setScoreNote(new ScoreNote(pitch, ons, dur));
				}	
			}
	
//			for (NotationStaff nos : p.getScore()) {
//				NotationVoice nv = nos.get(0);
//				for (NotationChord nc : nv) {
//					System.out.println(nc);
//				}
//			}
			
			// Adapt time signature positions
			MetricalTimeLine mtl = p.getMetricalTimeLine();
			for (long[] ts : mtl.getTimeSignature()) {
				Rational timeOld = new Rational(ts[3], ts[4]);
				Rational timeNew = timeOld.mul(mltpl);
				timeNew.reduce();
				TimeSignatureMarker tsm = mtl.getTimeSignatureMarker(timeOld);
				tsm.setMetricTime(timeNew);
			}
		}
		return p;
	}


	/**
	 * Combines the MIDIfiles of the separate voice parts in the given directory into a single Piece (score). 
	 * 
	 * @param f Is a directory when called in cross-validation; is a file when called on new data.
	 * @return 
	 */
	private static Piece importMidiFilesOLD(File f) {
		// NEW -->
		Rational mltpl = new Rational(27, 32);
		long[][] timeSigs = null;
		MetricalTimeLine mtl = new MetricalTimeLine();
		// <-- NEW
		
		// 1. Create a new Piece and make a NotationSystem for it 
		Piece piece = new Piece();    
		NotationSystem notationSystem = piece.createNotationSystem();

		// 2. Add each MIDIfile in dir to piece
		List<String> midiFileNames = new ArrayList<String>();
		// If f is a directory
		if (f.isDirectory()) {
			String[] fileNames = f.list();
			for (String s : fileNames) {
				if (s.endsWith(".mid")) {
					midiFileNames.add(s);  
				}
			}
			// Sort midiFileNames numerically (if it is already sorted, nothing changes)
			midiFileNames = ToolBox.bubbleSortString(midiFileNames);
			// If midiFileNames contains Strings not starting with a number between and including 0-9: throw Exception 
			if (midiFileNames == null) {
				throw new RuntimeException("ERROR: One of the file names does not start with a number.");
			}
		}
		// If f is not a directory, i.e., a MIDI file 
		else {
			String s = f.getName();
			if (s.endsWith(".mid")) {
				midiFileNames.add(s);  
			}
		}

		URL url;
		for (String midiFileName : midiFileNames) {
			// a. Make a Staff and add it to notationSystem
			NotationStaff staff = new NotationStaff(notationSystem);
			notationSystem.add(staff);
			// Ensure correct cleffing for each staff
			if (f.isDirectory()) {
				if (midiFileName.endsWith("S.mid") || midiFileName.endsWith("A.mid")) {
					staff.setClefType('g', -1, 0);
				}
				else {
					staff.setClefType('f', 1, 0);
				}
			}
			// b. Make a NotationVoice and add it to staff
			NotationVoice notationVoice = new NotationVoice(staff);
			staff.add(notationVoice);
			
			// c. Create a Piece from the MIDI file. The Piece is a single part
			if (!midiFileName.endsWith(".mid")) {
				continue;
			}
			try {
				if (f.isDirectory()) {
					url = new File(f + "/" + midiFileName).toURI().toURL();
				}
				else {
					url = f.toURI().toURL();
				}
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
//			MidiReader midiReader = new MidiReader();
			Piece part = new MidiReader().getPiece(url);

			// d. Add the Piece's notes to notationVoice
			Collection<Containable> notesInPart = part.getScore().getContentsRecursiveList(null);
			for (Containable c : notesInPart) {
				if (c instanceof Note) {	
					// NEW -->
					ScorePitch pitch = new ScorePitch(((Note) c).getMidiPitch());
					Rational ons = ((Note) c).getMetricTime();
					ons = ons.mul(mltpl);
					Rational dur = ((Note) c).getMetricDuration();
					dur = dur.mul(mltpl);
					((Note) c).setScoreNote(new ScoreNote(pitch, ons, dur));
//					System.out.println(c);				
					// <-- NEW
					notationVoice.add((Note) c); 	
				}	
			}
			
//			// NEW -->
			// Get time signature information 
//			long[][] ts = part.getMetricalTimeLine().getTimeSignature();
//			for (long[] l : ts) {
//				mtl.add(new TimeSignatureMarker((int) l[0], (int) l[1],	
//					new Rational((int) l[3]*mltpl.getNumer(), (int) l[4]*mltpl.getDenom())));
//				System.out.println(l.length);
//				System.out.println(Arrays.toString(l));
//			}
//			// <-- NEW
		}
		
		
		piece.setMetricalTimeLine(mtl);
		
		long[][] ts = piece.getMetricalTimeLine().getTimeSignature();
//		System.out.println("HIERRRRRRRRRRRRRRRRR");
		for (long[] l : ts) {
//			System.out.println(Arrays.toString(l));
		}
		
//		System.exit(0);
		return piece;
	}


	/**
	 * Combines the MIDIfiles of the separate voice parts in the given directory into a single Piece (score). 
	 * 
	 * @param dir
	 * @return 
	 */
	private static Piece importMidiFilesOLDEST(File dir) {
		// 1. Create a new Piece and make a NotationSystem for it 
		Piece piece = new Piece();    
		NotationSystem notationSystem = piece.createNotationSystem();

		// 2. Add each MIDIfile in dir to piece
		String[] fileNames = dir.list();
		// Make a list that contains only the MIDI file names in fileNames
		List<String> midiFileNames = new ArrayList<String>();
		for (String s : fileNames) {
			if (s.endsWith(".mid")) {
				midiFileNames.add(s);  
			}
		}
		// Sort midiFileNames numerically (if it is already sorted, nothing changes)
		midiFileNames = ToolBox.bubbleSortString(midiFileNames);
		// If midiFileNames contains Strings not starting with a number between and including 0-9: throw Exception 
		if (midiFileNames == null) {
			throw new RuntimeException("ERROR: One of the file names does not start with a number.");
		}

		URL url;
		for (String midiFileName : midiFileNames) {
			if (!midiFileName.endsWith(".mid")) {
				continue;
			}
			try {
				url = new File(dir + "/" + midiFileName).toURI().toURL();
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
			// b. Turn the MIDIfile into a Piece (a single part)
			MidiReader midiReader = new MidiReader();
			Piece singlePartPiece = midiReader.getPiece(url);
			// c. Make a Staff and add it to notationSystem
			NotationStaff staff = new NotationStaff(notationSystem);
			notationSystem.add(staff);
			// Ensure correct cleffing for each staff
			if (midiFileName.endsWith("S.mid") || midiFileName.endsWith("A.mid")) {
				staff.setClefType('g', -1, 0);
			}
			else {
				staff.setClefType('f', 1, 0);
			}
			// d. Make a NotationVoice and add it to staff
			NotationVoice notationVoice = new NotationVoice(staff);
			staff.add(notationVoice);
			// e. Get the notes from singlePartPiece and add them to notationVoice
			Collection<Containable> singlePartPieceNotes = singlePartPiece.getScore().getContentsRecursiveList(null);
			for (Containable c : singlePartPieceNotes) {
				if (c instanceof Note) {
					notationVoice.add((Note) c); 
				}
			}
		}
		return piece;
	}

}
  
    // ObjectPlayer player = ObjectPlayer.getInstance();
    // player.setContainer(MidiNoteSequence.convert(voice));
    // TransportButtons trans = new
    // TransportButtons(player.getPlayTimer());
    // trans.setPlayOnly(true);
    //
    // try {
    // Display display = EditorFactory.createDisplay(system);
    // player.getPlayTimer().registerMetricForPush((NotationDisplay)display);
    // JFrame frame = new JFrame(fileName);
    // frame.getContentPane().setLayout(new BorderLayout());
    // frame.getContentPane().add((JComponent)display);
    // frame.getContentPane().add(trans,BorderLayout.NORTH);
    // frame.pack();
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setVisible(true);
    // } catch (EditorConstructionException e) {
    // e.printStackTrace();
    // }
  

