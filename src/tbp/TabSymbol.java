package tbp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import representations.Tablature.Tuning;

public class TabSymbol extends Symbol implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FINGERING_DOT_ENCODING = "'";

	private int fret;
	private int course;
	private int fingeringDots;

	public static enum TabSymbolSet  {
		FRENCH("French", "French", 8, null),
		ITALIAN("Italian", "Italian", 8, null),
		SPANISH("Spanish", "Spanish", 8, null),
		JUDENKUENIG_1523("Judenkuenig1523", "German", 6, 
			new String[]{"A", "B", "C", "D", "E", "F", "G", "H"}),
		NEWSIDLER_1536("Newsidler1536", "German", 6, 
			new String[]{"+", "A", "B", "C", "D", "E", "F", "G", "H"}),
		OCHSENKUN_1558("Ochsenkun1558", "German", 6, 
			new String[]{"+", "2-", "3-", "4-", "5-", "6-", "7-", "8-", "9-", "10-", "11-"}),
		HECKEL_1562("Heckel1562", "German", 6, 
			new String[]{"+", "A-", "F-", "L-", "Q-", "X-"});

		public static final List<String[][]> FRETS;
		public static final int FRETS_FRENCH = 0;
		public static final int FRETS_GERMAN = 1;
		static {
			FRETS = new ArrayList<>();
			FRETS.add(new String[][] {
				{"a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"} // 11 frets per course
			});
			FRETS.add(new String[][] {
				{"5", "e", "k", "p", "v", "9", "e-", "k-", "p-", "v-", "9-"}, // 11 frets per course
				{"4", "d", "i", "o", "t", "7", "d-", "i-", "o-", "t-", "7-"},
				{"3", "c", "h", "n", "s", "z", "c-", "h-", "n-", "s-", "z-"},
				{"2", "b", "g", "m", "r", "y", "b-", "g-", "m-", "r-", "y-"},
				{"1", "a", "f", "l", "q", "x", "a-", "f-", "l-", "q-", "x-"}
			});
		}

		private String name;
		private String type;
		private int maxNumberOfCourses;
		private String[] fretsSixthCourse;

		TabSymbolSet(String s, String t, int m, String[] sc) {
			name = s;
			type = t;
			maxNumberOfCourses = m;
			fretsSixthCourse = sc;
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

		public String[] getFretsSixthCourse() {
			return fretsSixthCourse; 
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