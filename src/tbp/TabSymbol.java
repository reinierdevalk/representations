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

		public static TabSymbolSet getTabSymbolSet(String s) {
			for (TabSymbolSet t : TabSymbolSet.values()) { 
				if (t.getName().equals(s)) {
					return t;
				}
			}
			return null;
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

}