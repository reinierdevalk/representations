package tbp;

public class RhythmSymbol extends Symbol {

	public static final String BEAM = "-";
	public static final String DOT_ENCODING = "*";
	public static final String TRIPLET_INDICATOR = "tr";
	public static final String TRIPLET_OPEN = "[";
	public static final String TRIPLET_CLOSE = "]";

	private int duration;
	private int numberOfDots;
	private boolean beam;


	public RhythmSymbol (String e, String s, int d) {
		encoding = e;
		symbol = s;
		duration = d;
		numberOfDots = (int) e.chars().filter(c -> c == DOT_ENCODING.charAt(0)).count();
		beam = e.contains(BEAM);
	}


//	void setNumberOfDots() {
//		numberOfDots = makeNumberOfDots(getEncoding());
//	}


//	// TESTED
//	static int makeNumberOfDots(String e) {
//		return (int) e.chars().filter(c -> c == DOT_ENCODING.charAt(0)).count();
//	}


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
	 * Makes a variant (dotted, beamed, tripletised or any of these combined) of the RS.
	 * 
	 * @param dot
	 * @param beam
	 * @param tripletise
	 * @param numDots
	 * @param openMidClose A String with value "open", "", "close", or <code>null</code> (if not applicable).
	 * @return
	 */
	// TESTED
	public RhythmSymbol makeVariant(boolean dot, boolean beam, boolean tripletise, int numDots, String openMidClose) {
		String prefix = "";
		if (tripletise) {
			String omc = openMidClose.equals("") ? "" : 
				(openMidClose.equals("open") ? TRIPLET_OPEN : TRIPLET_CLOSE);
			prefix = TRIPLET_INDICATOR + omc;
		}
		String suffix = "";
		if (dot) {
			suffix = RHYTHM_DOT.getEncoding().repeat(numDots);
		}
		if (beam) {
			suffix += BEAM;
		}
		String enc = prefix + getEncoding() + suffix;

		int dur = getDuration();
		if (tripletise) {
			dur -= dur / 3.0; 
		}
		if (dot) {
			int dottedDur = dur;
			for (int i = 0; i < numDots; i++) {
				dottedDur += (1.0 / (2 * (i+1))) * dur;
			}
			dur = dottedDur;
		}
		return new RhythmSymbol(enc, getSymbol(), dur);
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
			getDuration() == r.getDuration();
	}


	private static RhythmSymbol getTripletVariant(String e) {
		for (RhythmSymbol r: RHYTHM_SYMBOLS) {
			if (r.getEncoding().endsWith(e) && r.getEncoding().startsWith(TRIPLET_INDICATOR)) {
				return r;
			}
		}
		return null;
	}


	private static RhythmSymbol getNonTripletVariant(String anEncoding) {
		int indRS = TRIPLET_INDICATOR.length(); 
		if (anEncoding.contains(TRIPLET_OPEN) || anEncoding.contains(TRIPLET_CLOSE)) {
			indRS += 1;
		}
		String onlyRS = anEncoding.substring(indRS);
		
		for (RhythmSymbol r: RHYTHM_SYMBOLS) {
			if (r.encoding.equals(onlyRS)) {	
				return r;
			}
		}
		return null;
	}


	/**
	 * Returns the undotted version of a dotted RS. If the RS is undotted, returns itself.  
	 * @return
	 */
	private RhythmSymbol getUndotted() {
		String encoding = getEncoding();
		String encodingUndotted = 
			getNumberOfDots() == 0 ? encoding : 	
			encoding.substring(0, encoding.indexOf(RHYTHM_DOT.getEncoding()));
		return getRhythmSymbol(encodingUndotted);
	}

}
