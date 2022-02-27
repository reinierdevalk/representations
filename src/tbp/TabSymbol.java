package tbp;

import java.io.Serializable;

import representations.Tablature.Tuning;


public class TabSymbol implements Serializable {

	private String encoding;
	private String symbol;
	private int fret;
	private int course;
	private int midiNumber;


	/**
	 * Constructor. Creates a new TabSymbol with the specified attributes and adds this 
	 * to the specified TabSymbolSet.
	 * 
	 * @param encoding
	 * @param symbol
	 * @param fret
	 * @param course
	 * @param midiNumber
	 * @return
	 */
	public TabSymbol(String encoding, String symbol, int fret, int course, int midiNumber) {
		this.encoding = encoding;
		this.symbol = symbol;
		this.fret = fret;
		this.course = course;
		this.midiNumber = midiNumber;
	}


	/**
	 * Returns the TabSymbol's encoding.
	 * 
	 * @return
	 */  
	public String getEncoding() {
		return encoding;
	}


	/**
	 * Returns the TabSymbol's symbol as shown on the staff line.
	 * @return
	 */
	public String getSymbol() {
		return symbol;
	}


	/**
	 * Returns the position (fret) the TabSymbol is in.
	 * 
	 * @return 
	 */
	public int getFret() {
		return fret;
	}


	/**
	 * Returns the course the TabSymbol is on.
	 * 
	 * @return 
	 */
	public int getCourse() {
		return course;
	}


	/**
	 * Returns the TabSymbol's pitch as a MIDI number.
	 * 
	 * @param t
	 * @return 
	 */
	// TODO test
	public int getPitch() {
		return midiNumber;
	}


	/**
	 * Returns the TabSymbol's pitch, as a MIDI number, in the specified tunings.
	 * 
	 * @param aTuning
	 * @param aTuningBassCourses
	 * @return 
	 */
	// TODO test
	public int getPitch(Tuning t) {
		int pitch = midiNumber;
		final int semitone = 1;
		
		int drop = 0;
		if (getCourse() == 6 && t.isDrop()) {
			drop = 2;
		}
		
		pitch = pitch + ((t.getTransposition() - drop) * semitone);
//		// Upon creation of the TS, each TS is given the MIDI number that goes with the 
//		// G tuning, where any seventh course is assumed to be a major second below the sixth.
//		// pitch must thus only be adapted if one of these two settings is changed; else, 
//		// it retain its initial value (midiNumber)
//		switch (aTuning) {
//			case G:
//				break;
//			case G_AVALLEE:
//				if (getCourse() == 6) {
//					pitch -= 2*semitone;
//				}
//				break;
//			case F:
//				pitch -= 2*semitone;
//				break;
//			case A:
//				pitch += 2*semitone;
//				break;
//			case D:
//				pitch -= 5*semitone;
//				break;
//			case A_AVALLEE:
//				if (getCourse() == 6) {
//					pitch -= 0*semitone;
//				}
//				else {
//					pitch += 2*semitone;
//				}
//				break;
//			case C_AVALLEE:
//				if (getCourse() == 6) {
//					pitch -= 9*semitone;
//				}
//				else {
//					pitch -= 7*semitone;
//				}
//				break;
//		}

//		if (course > 6) {
//			switch (aTuningBassCourses) {
//				case SECOND:
//					break;	
//				case FOURTH:
//					pitch -= 3*semitone;
//					break;	
//				case P4M2:
//					// TODO
//					break;
//				case P5P4M3M2:
//					// TODO
//					break;
//				case P5P4m3M2:
//					// TODO
//					break;
//			}
//		}
		return pitch;
	}


	/**
	 * Returns the TabSymbol's pitch, as a String, in the specified tunings.
	 * 
	 * @param aTuning
	 * @param aTuningSeventhCourse
	 * @return
	 */
	public String getPitchAsString (Tuning t) {
		final String[] pitches = {
			"G1", "G#1", "A1", "Bb1", "B1", "C", "C#", "D", "Eb", "E", "F", "F#", 
			"G", "G#", "A", "Bb", "B", "c", "c#", "d", "eb", "e", "f", "f#", 
			"g", "g#", "a", "bb", "b", "c1", "c#1", "d1", "eb1", "e1", "f1", "f#1", 
			"g1", "g#1", "a1", "bb1", "b1", "c2", "c#2", "d2", "eb2", "e2"}; 

		// Correction necessary to set MIDI number equal to index in array
		final int correction = 31;
		int pitch = getPitch(t); 
		String pitchAsString = pitches[pitch - correction]; 
		return pitchAsString;    
	}


	/**
	 * Searches the specified TabSymbolSet for the TabSymbol whose attribute encoding
	 * equals the specified encoding. Returns null if the TabSymbolSet does not contain
	 * such a TabSymbol.
	 * 
	 * @param anEncoding
	 * @param aTabSymbolSet
	 * @return
	 */
	public static TabSymbol getTabSymbol(String anEncoding, TabSymbolSet aTabSymbolSet) {
		for (TabSymbol t: aTabSymbolSet) {
			if (t.encoding.equals(anEncoding)) {
				return t;
			}
		}
		return null;
	}

}
