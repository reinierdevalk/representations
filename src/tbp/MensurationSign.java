package tbp;

import java.util.Arrays;

public class MensurationSign extends Symbol {

	public static final int DEFAULT_STAFFLINE = 3; 
	private Integer[] meter;
	private int staffLine;


	public MensurationSign (String e, String s, Integer[] m) {
		// The basic type (M2, M3, ..., MC\) has beat unit 4 and staffline 3, and is encoded as  
		// M<n> or M<n>\ (cut MS), (where <n> is a number or a symbol)
		// A variant type may have a different beat unit or staffline, and is encoded as 
		// - M<n>:<b><l> or M<n>\\:<b><l> (cut MS), where <b> is the beat unit and <l> the staffline  
		encoding = e;
		symbol = s;
		meter = m;
		staffLine = setStaffLine();
	}


	public int setStaffLine() {
		return makeStaffLine();
	}
	
	
	// 
	int makeStaffLine() {
		String e = getEncoding();
		String last = e.substring(e.length()-1);
		int sl = DEFAULT_STAFFLINE;
		if (!e.contains("\\") && e.length() > 2) {
			sl = Integer.parseInt(last);
		}
		if (e.contains("\\") && !e.endsWith("\\")) {
			if (!e.contains(":")) {
				sl = Integer.parseInt(last);
			}
			else {
				String beat = String.valueOf(getMeter()[1]);
				if (e.endsWith(":" + beat)) {
					sl = DEFAULT_STAFFLINE;
				}
				else {
					sl = Integer.parseInt(e.substring(e.indexOf(":" + beat) + (":" + beat).length()));
				}
			}
		}
		return sl;
	}


	public Integer[] getMeter() {
		return meter;  
	}


	public int getStaffLine() {
		return staffLine;  
	}


	/**
	 * Makes a variant (beat unit, staffline, or both) of the MS.
	 * 
	 * @param beatUnit
	 * @param staffLine
	 * @param customEncoding If <code>null</code>, the default encoding convention is used. 
	 * @return
	 */
	// TESTED
	public MensurationSign makeVariant(int beatUnit, int staffLine/*, String customEncoding*/) {
		String e = getEncoding();
		Integer[] m = getMeter();
		Integer[] newM = Arrays.copyOf(m, m.length);
		if (beatUnit != -1) {
			newM[1] = beatUnit;
		}
		if (beatUnit != -1) {
			e += ":" + String.valueOf(beatUnit);
		}
		if (staffLine != -1) {
			e += String.valueOf(staffLine);
		}
		return new MensurationSign(e, getSymbol(), newM);
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof MensurationSign)) {
			return false;
		}
		MensurationSign m = (MensurationSign) o;
		return 
			getEncoding().equals(m.getEncoding()) &&
			getSymbol().equals(m.getSymbol()) &&
			getStaffLine() == m.getStaffLine() &&
			Arrays.equals(getMeter(), m.getMeter());
	}

}
