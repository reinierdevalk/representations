package tbp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import representations.Tablature.Tuning;


public class TabSymbol extends Symbol implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MAX_NUMBER_OF_COURSES = 8;
	public static final String FINGERING_DOT_ENCODING = "'";
	
	private int fret;
	private int course;
	private int fingeringDots;

	public static enum TabSymbolSet  {
		FRENCH("French", "French", MAX_NUMBER_OF_COURSES),
		ITALIAN("Italian", "Italian", MAX_NUMBER_OF_COURSES),
		SPANISH("Spanish", "Spanish", MAX_NUMBER_OF_COURSES),
		JUDENKUENIG_1523("Judenkuenig1523", "German", 6),
		NEWSIDLER_1536("Newsidler1536", "German", 6),
		OCHSENKUN_1558("Ochsenkun1558", "German", 6),
		HECKEL_1562("Heckel1562", "German", 6);

		private String name;
		private String type;
		private int maxNumberOfCourses;

		TabSymbolSet(String s, String t, int m) {
			name = s;
			type = t;
			maxNumberOfCourses = m;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public int getMaxNumberOfCourses() {
			return maxNumberOfCourses;
		}
	}


	public TabSymbol(String e, String s, int f, int c) {
		setEncoding(e);
		setSymbol(s);
		setFret(f);
		setCourse(c);
		setFingeringDots();
	}


	void setFret(int f) {
		fret = f;
	}


	void setCourse(int c) {
		course = c;
	}


	void setFingeringDots() {
		fingeringDots = (int) getEncoding().chars().filter(c -> c == FINGERING_DOT_ENCODING.charAt(0)).count();
	}


	public int getFret() {
		return fret;
	}


	public int getCourse() {
		return course;
	}


	public int getFingeringDots() {
		return fingeringDots;
	}


	// TESTED
	public static List<TabSymbol> listTabSymbols(TabSymbolSet tss) {
		List<TabSymbol> allTs = new ArrayList<TabSymbol>();

		List<String> frets = Arrays.asList(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"});
		Map<String, List<String>> sixthCourseGerman = new LinkedHashMap<String, List<String>>();
		sixthCourseGerman.put(TabSymbolSet.JUDENKUENIG_1523.getName(), 
			Arrays.asList(new String[]{"A", "B", "C", "D", "E", "F", "G", "H"}));
		sixthCourseGerman.put(TabSymbolSet.NEWSIDLER_1536.getName(), 
			Arrays.asList(new String[]{"+", "A", "B", "C", "D", "E", "F", "G", "H"}));
		sixthCourseGerman.put(TabSymbolSet.OCHSENKUN_1558.getName(), 
			Arrays.asList(new String[]{"+", "2-", "3-", "4-", "5-", "6-", "7-", "8-", "9-", "10-", "11-"}));
		sixthCourseGerman.put(TabSymbolSet.HECKEL_1562.getName(), 
			Arrays.asList(new String[]{"+", "A-", "F-", "L-", "Q-", "X-"}));
		List<List<String>> otherCoursesGerman = new ArrayList<List<String>>();
		otherCoursesGerman.add(Arrays.asList(new String[]{"5", "e", "k", "p", "v", "9", "e-", "k-", "p-", "v-", "9-"}));
		otherCoursesGerman.add(Arrays.asList(new String[]{"4", "d", "i", "o", "t", "7", "d-", "i-", "o-", "t-", "7-"}));
		otherCoursesGerman.add(Arrays.asList(new String[]{"3", "c", "h", "n", "s", "z", "c-", "h-", "n-", "s-", "z-"}));
		otherCoursesGerman.add(Arrays.asList(new String[]{"2", "b", "g", "m", "r", "y", "b-", "g-", "m-", "r-", "y-"}));
		otherCoursesGerman.add(Arrays.asList(new String[]{"1", "a", "f", "l", "q", "x", "a-", "f-", "l-", "q-", "x-"}));

		// For each course
		for (int c = 0; c < tss.getMaxNumberOfCourses(); c++) {
			String courseStr = String.valueOf(c + 1);
			// For each fret
			for (int f = 0; f < frets.size(); f++) {
				if (!tss.getType().equals("German")) {
					String fretStr = tss == TabSymbolSet.FRENCH ? frets.get(f) : String.valueOf(f);
					allTs.add(new TabSymbol(fretStr + courseStr, fretStr, f, c + 1));
				}
				else {
					// Not all frets are always encoded (for course (6))
					int numFretsEncoded = 
						c + 1 == 6 ? sixthCourseGerman.get(tss.getName()).size() :
						otherCoursesGerman.get(c).size();	
					if (f < numFretsEncoded) {
						String encoding = 
							c + 1 == 6 ? sixthCourseGerman.get(tss.getName()).get(f) :
							otherCoursesGerman.get(c).get(f);
						allTs.add(new TabSymbol(encoding, encoding, f, c + 1));	
					}
				}
			}
		}
		return allTs;
	}


	/**
	 * Makes a variant (RH-dotted) of the TS.
	 * 
	 * @param fingeringDots
	 * @return
	 */
	// TESTED
	public TabSymbol makeVariant(int fingeringDots) {
		String e = getEncoding() + FINGERING_DOT_ENCODING.repeat(fingeringDots); 
		return new TabSymbol(e, getSymbol(), getFret(), getCourse());
	}


	/**
	 * Returns the TabSymbol's pitch, as a MIDI number, in the given tuning.
	 * 
	 * @param t
	 * @return 
	 */
	// TESTED
	public int getPitch(Tuning t) {
		List<Integer> openCourses = t.getPitches();
		Collections.reverse(openCourses);
		return openCourses.get(getCourse() - 1) + getFret();
	}


//	/**
//	 * Searches the specified TabSymbolSet for the TabSymbol whose attribute encoding
//	 * equals the specified encoding. Returns null if the TabSymbolSet does not contain
//	 * such a TabSymbol.
//	 * 
//	 * @param anEncoding
//	 * @param aTabSymbolSet
//	 * @return
//	 */
//	public static TabSymbol getTabSymbol(String anEncoding, TabSymbolSet aTabSymbolSet) {
//		for (TabSymbol t: aTabSymbolSet) {
//			if (t.getEncoding().equals(anEncoding)) {
//				return t;
//			}
//		}
//		return null;
//	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof TabSymbol)) {
			return false;
		}
		TabSymbol t = (TabSymbol) o;
		return 
			getEncoding().equals(t.getEncoding()) &&
			getSymbol().equals(t.getSymbol()) &&
			getFret() == t.getFret() &&
			getCourse() == t.getCourse() &&
			getFingeringDots() == t.getFingeringDots();
	}


	/**
	 * Returns the TabSymbol's pitch, as a String, in the specified tuning.
	 * 
	 * @param t
	 * @return
	 */
	private String getPitchAsString (Tuning t) {
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


//	private static final List<Integer> OPEN_COURSES_G;
//	static {
//		// In case of seven courses, (7) is assumed to be a major second lower than (6)
//		// In case of eight courses, (7) is assumed to be a major second lower than (6), and (8) a perfect fourth
//		OPEN_COURSES_G = Arrays.asList(new Integer[]{67, 62, 57, 53, 48, 43, 41, 38});
//	}


//	private static final Map<String, List<String>> GERMAN_SIXTH_COURSE;
//	static {
//		GERMAN_SIXTH_COURSE = new LinkedHashMap<String, List<String>>();
//		GERMAN_SIXTH_COURSE.put("Ochsenkun1558", 
//			Arrays.asList(new String[]{"+", "2-", "3-", "4-", "5-", "6-", "7-", "8-", "9-", "10-", "11-"}));
//		GERMAN_SIXTH_COURSE.put("Judenkuenig1523", 
//			Arrays.asList(new String[]{"A", "B", "C", "D", "E", "F", "G", "H"}));
//		GERMAN_SIXTH_COURSE.put("Newsidler1536", 
//			Arrays.asList(new String[]{"+", "A", "B", "C", "D", "E", "F", "G", "H"}));
//		GERMAN_SIXTH_COURSE.put("Heckel1562", 
//			Arrays.asList(new String[]{"+", "A-", "F-", "L-", "Q-", "X-"}));
//	}


//	private static final List<List<String>> GERMAN_OTHER_COURSES;
//	static {
//		GERMAN_OTHER_COURSES = new ArrayList<List<String>>();
//		GERMAN_OTHER_COURSES.add(Arrays.asList(new String[]{"5", "e", "k", "p", "v", "9", "e-", "k-", "p-", "v-", "9-"}));
//		GERMAN_OTHER_COURSES.add(Arrays.asList(new String[]{"4", "d", "i", "o", "t", "7", "d-", "i-", "o-", "t-", "7-"}));
//		GERMAN_OTHER_COURSES.add(Arrays.asList(new String[]{"3", "c", "h", "n", "s", "z", "c-", "h-", "n-", "s-", "z-"}));
//		GERMAN_OTHER_COURSES.add(Arrays.asList(new String[]{"2", "b", "g", "m", "r", "y", "b-", "g-", "m-", "r-", "y-"}));
//		GERMAN_OTHER_COURSES.add(Arrays.asList(new String[]{"1", "a", "f", "l", "q", "x", "a-", "f-", "l-", "q-", "x-"}));
//	}


//	/**
//	 * Returns the TabSymbol's pitch, as a MIDI number, in the specified tunings.
//	 * 
//	 * @param t
//	 * @return 
//	 */
//	public int getPitchOLD(Tuning t) {
//		
//		int pitch = getPitch();
//		int semitone = 1;
//		
//		int drop = 0;
//		if (getCourse() == 6 && t.isDrop()) {
//			drop = 2;
//		}
//		pitch = pitch + ((t.getTransposition() - drop) * semitone);
////		// Upon creation of the TS, each TS is given the MIDI number that goes with the 
////		// G tuning, where any seventh course is assumed to be a major second below the sixth.
////		// pitch must thus only be adapted if one of these two settings is changed; else, 
////		// it retain its initial value (midiNumber)
////		switch (aTuning) {
////			case G:
////				break;
////			case G_AVALLEE:
////				if (getCourse() == 6) {
////					pitch -= 2*semitone;
////				}
////				break;
////			case F:
////				pitch -= 2*semitone;
////				break;
////			case A:
////				pitch += 2*semitone;
////				break;
////			case D:
////				pitch -= 5*semitone;
////				break;
////			case A_AVALLEE:
////				if (getCourse() == 6) {
////					pitch -= 0*semitone;
////				}
////				else {
////					pitch += 2*semitone;
////				}
////				break;
////			case C_AVALLEE:
////				if (getCourse() == 6) {
////					pitch -= 9*semitone;
////				}
////				else {
////					pitch -= 7*semitone;
////				}
////				break;
////		}
//
////		if (course > 6) {
////			switch (aTuningBassCourses) {
////				case SECOND:
////					break;	
////				case FOURTH:
////					pitch -= 3*semitone;
////					break;	
////				case P4M2:
////					// 
////					break;
////				case P5P4M3M2:
////					// 
////					break;
////				case P5P4m3M2:
////					// 
////					break;
////			}
////		}
//		return pitch;
//	}

}
