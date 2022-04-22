package tbp;

public class ConstantMusicalSymbol extends Symbol {

	public static final String PIPE = "|";
	public static final String REPEAT_DOTS = ":";


	public ConstantMusicalSymbol(String e, String s) {
		setEncoding(e);
		setSymbol(s);
	}


	public static boolean isBarline(String e) {
		return e.contains(PIPE) ? true : false;
	}


	/**
	 * Makes a variant (barline) of the CMS.
	 * 
	 * @param numBarlines
	 * @param repeatDots A String with value "left", "right", "both", or <code>null</code> (if not applicable).
	 * @return
	 */
	// TESTED
	public ConstantMusicalSymbol makeVariant(int numBarlines, String repeatDots) {
		String e = getEncoding();
		e = e.repeat(numBarlines);
		if (repeatDots != null) {
			if (repeatDots.equals("left")) {
				e = e + REPEAT_DOTS;
			}
			else if (repeatDots.equals("right")) {
				e = REPEAT_DOTS + e;
			}
			else {
				e = REPEAT_DOTS + e + REPEAT_DOTS;
			}
		}
		return new ConstantMusicalSymbol(e, e);
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ConstantMusicalSymbol)) {
			return false;
		}
		ConstantMusicalSymbol c = (ConstantMusicalSymbol) o;
		return 
			getEncoding().equals(c.getEncoding()) && 
			getSymbol().equals(c.getSymbol());
	}

}