package tbp;

import java.util.ArrayList;
import java.util.List;

public class RhythmSymbol extends Symbol {

	public static final String BEAM = "-";
	public static final String DOT_ENCODING = "*";
	public static final String TRIPLET_INDICATOR = "tr";
	public static final String TRIPLET_OPEN = "[";
	public static final String TRIPLET_CLOSE = "]";
	public static final int TRIPLET_OPEN_IND = 0;
	public static final int TRIPLET_MID_IND = 1;
	public static final int TRIPLET_CLOSE_IND = 2;

	private int duration;
	private int numberOfDots;
	private boolean beam;


	public RhythmSymbol(String e, String s, int d) {
		setEncoding(e);
		setSymbol(s);
		setDuration(d);
		setNumberOfDots();
		setBeam();
	}


	void setDuration(int d) {
		duration = d;
	}


	void setNumberOfDots() {
		numberOfDots = (int) getEncoding().chars().filter(c -> c == DOT_ENCODING.charAt(0)).count();
	}


	void setBeam() {
		beam = getEncoding().contains(BEAM);
	}


	public int getDuration() {
		return duration;
	}


	public int getNumberOfDots() {
		return numberOfDots;
	}


	public boolean getBeam() {
		return beam;
	}


	/**
	 * Makes a variant (dotted, beamed, tripletised, or any of these combined) of the RS.
	 * 
	 * @param numDots
	 * @param beam
	 * @param tripletise
	 * @return
	 */
	// TESTED
	public List<RhythmSymbol> makeVariant(int numDots, boolean beam, boolean tripletise) {
		List<RhythmSymbol> rss = new ArrayList<>();
		String suffix = "";
		if (numDots > 0) {
			suffix = DOT_ENCODING.repeat(numDots);
		}
		if (beam) {
			suffix += BEAM;
		}
		String enc = getEncoding() + suffix;

		int dur = getDuration();
		if (tripletise) {
			dur -= dur / 3.0; 
		}
		if (numDots > 0) {
			int dottedDur = dur;
			for (int i = 0; i < numDots; i++) {
				dottedDur += (1.0 / (2 * (i+1))) * dur;
			}
			dur = dottedDur;
		}
		// In case of triplet, return the open, mid, and close variant
		String s = getSymbol();
		if (tripletise) {
			rss.add(new RhythmSymbol(TRIPLET_INDICATOR + TRIPLET_OPEN + enc, s, dur));
			rss.add(new RhythmSymbol(TRIPLET_INDICATOR + enc, s, dur));
			rss.add(new RhythmSymbol(TRIPLET_INDICATOR + TRIPLET_CLOSE + enc, s, dur));
		}
		else {
			rss.add(new RhythmSymbol(enc, s, dur));
		}
		return rss;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof RhythmSymbol)) {
			return false;
		}
		RhythmSymbol r = (RhythmSymbol) o;
		return 
			getEncoding().equals(r.getEncoding()) &&
			getSymbol().equals(r.getSymbol()) &&
			getDuration() == r.getDuration() &
			getNumberOfDots() == r.getNumberOfDots() &&
			getBeam() == r.getBeam();
	}

}