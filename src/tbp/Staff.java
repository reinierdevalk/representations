package tbp;

import java.util.List;

public class Staff { 
  
	private int numberOfSegments;
	private String[][] staffData;
	private static final String STAFF_SEGMENT = "-";
	public static final int STAFF_LINES = 11;
	public static final int BAR_NUMS_LINE = 0;
	private static final int RHYTHM_LINE = 1;
	private static final int DIAPASONS_LINE_ITALIAN = 2;
	private static final int TOP_TABLATURE_LINE = 3;
	public static final int UPPER_MIDDLE_TABLATURE_LINE = 5;
	private static final int LOWER_MIDDLE_TABLATURE_LINE = 6;
	private static final int BOTTOM_TABLATURE_LINE = 8;
	private static final int DIAPASONS_LINE_OTHER = 9;
	public static final int FOOTNOTES_LINE = 10;
	private static final int NECESSARY_LINE_SHIFT = 2;
	public static final String SPACE_BETWEEN_STAFFS = "\n";
	

	/**
	 * Constructor. Creates an empty Staff of the specified length.
	 * 
	 * @param numberOfSegments
	 * @return 
	 */
	public Staff(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments; 
		this.staffData = new String[STAFF_LINES][numberOfSegments+2];
		final String spaceSegment = " ";
		// Construct the empty Staff line by line, segment by segment 
		for (int staffLine = BAR_NUMS_LINE; staffLine < STAFF_LINES; staffLine++) {  
			for (int segment = 0; segment < numberOfSegments; segment++) { 
				switch (staffLine) {
					// staffLine 0: for bar numbers
					case BAR_NUMS_LINE:
						staffData[staffLine][segment] = spaceSegment;
						break;
					// staffLine 1: for RS
					case RHYTHM_LINE:
						staffData[staffLine][segment] = spaceSegment;
						break;
					// staffLine 2: for any diaposons in Italian tablature
					case DIAPASONS_LINE_ITALIAN: 
						staffData[staffLine][segment] = spaceSegment;
						break;
					// staffLine 9: for any diapasons in French and Spanish tablature
					case DIAPASONS_LINE_OTHER:
						staffData[staffLine][segment] = spaceSegment;
						break;
					// staffLine 10: for any footnote indicators
					case FOOTNOTES_LINE:
						staffData[staffLine][segment] = spaceSegment;
						break;
					// staffLines 3-8: for the tablature staff itself
					default:
						staffData[staffLine][segment] = STAFF_SEGMENT;
				}
			}
		}
		// Allow for bar numbers up to three digits above the final barline of a staff
		// by adding two spaces to each staff line
		for (int staffLine = BAR_NUMS_LINE; staffLine < STAFF_LINES; staffLine++) {
			staffData[staffLine][numberOfSegments] = spaceSegment;
			staffData[staffLine][numberOfSegments+1] = spaceSegment;
		}
	}


	/** 
	 * Returns the Staff.
	 * 
	 * @return 
	 */
	public String getStaff() { 
		String result = "";   
		for (String[] staffLine: staffData) {
			for (String segment: staffLine) {
				result += segment;
			}
			result += "\n";
		}  
		return result;
	}
	
	
	public int getNumberOfSegments() {
		return numberOfSegments;
	}


	/**
	 * Adds the specified ConstantMusicalSymbol to the staff on the specified segment(s)
	 * 
	 * @param anEncoding
	 * @param segment
	 */
	public void addConstantMusicalSymbol(String anEncoding, int segment) {
		final String repeatDot = ".";
		final String repeatIndicator = ":"; 
		ConstantMusicalSymbol c = ConstantMusicalSymbol.getConstantMusicalSymbol(anEncoding);
		String subSymbol; 
		// Space, barline and double barline
		if (!c.getEncoding().contains(repeatIndicator)) {
			for (int staffLine = TOP_TABLATURE_LINE; staffLine <= BOTTOM_TABLATURE_LINE; staffLine++) {
				// Add subsymbols one by one to the Staff
				for (int i = 0; i < c.getSymbol().length(); i++) {
					subSymbol = Character.toString(c.getSymbol().charAt(i));
					staffData[staffLine][segment + i] = subSymbol;
				}
			}
		}
		// All kinds of repeat barlines
		else {
			for (int staffLine = TOP_TABLATURE_LINE; staffLine <= BOTTOM_TABLATURE_LINE; staffLine++) {
				// Add subsymbols one by one to the Staff
				// NB: if the subsymbol is a repeatIndicator, the inner two lines of the staff
				// itself must be filled differently than the outer four
				for (int i = 0; i < c.getSymbol().length(); i++) {
					subSymbol = Character.toString(c.getSymbol().charAt(i));
					if (subSymbol.equals(repeatIndicator)) {
						if ((staffLine == UPPER_MIDDLE_TABLATURE_LINE) || 
							(staffLine == LOWER_MIDDLE_TABLATURE_LINE)) {
							staffData[staffLine][segment + i] = repeatDot;
						}
						else {
							staffData[staffLine][segment + i] = STAFF_SEGMENT;
						}
					}
					else {
						staffData[staffLine][segment + i] = subSymbol;
					}
				}
			}
		}    
	}


	/**
	 * Adds a MensurationSign to the Staff on the specified segment.
	 * 
	 * @param aMensurationSign
	 * @param segment
	 */
	public void addMensurationSign(MensurationSign aMensurationSign, int segment) {
		int staffLine = aMensurationSign.getStaffLine() + NECESSARY_LINE_SHIFT;
		String symbol = aMensurationSign.getSymbol();
		staffData[staffLine][segment] = symbol;
	}


	/**
	 * Adds a RhythmSymbol to the Staff on the specified segment.
	 * 
	 * @param aRhythmSymbol
	 * @param segment
	 * @param showBeam
	 */ 
	public void addRhythmSymbol(RhythmSymbol aRhythmSymbol, int segment, boolean showBeam) {
//		final String rhythmDot = ".";
		final String beam = "-";
		String symbol = aRhythmSymbol.getSymbol();
		staffData[RHYTHM_LINE][segment] = symbol; 
		// Dotted RS? Add dot in next segment
		if (aRhythmSymbol.getEncoding().contains(RhythmSymbol.rhythmDot.getEncoding())) {
//		if (!aRhythmSymbol.getEncoding().startsWith(RhythmSymbol.tripletIndicator) && 
//			aRhythmSymbol.getDuration() % 3 == 0) {
//		if (aRhythmSymbol.getDuration() % 3 == 0) {
			staffData[RHYTHM_LINE][segment + 1] = RhythmSymbol.rhythmDot.getSymbol(); //rhythmDot; 
		}
		// Beamed RS? Add beam in next segment
		if (aRhythmSymbol.getEncoding().endsWith(beam) && showBeam == true) {
			staffData[RHYTHM_LINE][segment + 1] = beam;
		}
	}


	/** 
	 * Adds the footnote indicators at the positions in the list given 
	 * 
	 * @param indices The indices of the segments containing footnotes events.
	 */
	public void addFootnoteIndicators(List<Integer> indices) {
		String footnotesIndicator = "*";
		for (int ind : indices) {
			staffData[FOOTNOTES_LINE][ind] = footnotesIndicator; 
		}
	}


	/** 
	 * Adds every fifth bar number at the positions in the list given. Bar numbers are 
	 * added above the barline that starts the bar, or, in those cases where this barline
	 * is the last event in a staff, at the start of the next staff.
	 * 
	 * @param indices The indices of the segments containing barline events.
	 * @param firstBar The number of the bar with which the staff begins (this bar can be
	 *                 a continuation of the last (unfinished) bar in the previous staff).
	 * @param startsWithUnfinished Whether or not the system starts with an unfinished bar.
	 * @param endsWithBarline Whether or not the system ends with a barline.                 
	 */
	public void addBarNumbers(List<Integer> indices, int firstBar, boolean startsWithUnfinished,
		boolean endsWithBarline) {
		int freq = 5;
		
		// a. Handle start of staff (if applicable) (no barline index in indices)
		// Add a bar number at the start if the first bar is a multiple-of-freq bar that 
		// is not an unfinished bar from the previous system. Example for freq = 5:
		//                           [5]     
		// ... | ... | ... | ... | / ... | ... | ... | ... |
		if (firstBar % freq == 0 && !startsWithUnfinished) {
			String asStr = "[" + String.valueOf(firstBar) + "]";
			// Add each char in the bar number at index 0
			for (int j = 0; j < asStr.length(); j++) {
				staffData[BAR_NUMS_LINE][0 + j] = 
					Character.toString(asStr.charAt(j)); 
			}
		}

		// b. Handle rest of staff (barlines indeices in indices)
		// Add a bar number at the barline index if the barline closes a bar with
		// barCount (n*freq)-1 (which opens a bar with barCount n*freq). Example:
		//                       [5]
		// ... | ... | ... | ... | ... | / ... | ... | etc.
		//
		// This approach ensures correct bar numbering in those cases where a bar
		// continues on the next system. Example numbering on upper staff:
		//                       [5]
		// ... | ... | ... | ... | ... / ... | ... | etc.
		// 
		// Example numbering on lower staff
		//                             [5]
		// ... | ... | ... | ... / ... | ... | etc.
		// 
		// Exception: if a barline closes a bar with barCount (n*freq)-1 but it is the last 
		// event in the staff, the bar number goes to the start of the next staff (see a.)
		int barCount = firstBar;
		// Remove any decorative opening barline index
		if (indices.get(0) == 0) {
			indices = indices.subList(1, indices.size());
		}
		for (int i = 0; i < indices.size(); i++) {
			int ind = indices.get(i);
			// If the barline at ind closes a bar with barCount (n*freq)-1
			if (barCount % freq == (freq-1)) {			
				// Add bar number only if the barline is not the last event in the staff
				// (in which case it will be added at the beginning of the next staff; 
				// see a. above)			
				if (!(i == indices.size()-1 && endsWithBarline)) {
					String asStr = "[" + String.valueOf(barCount+1) + "]";
					// Add each char in the bar number at ind
					for (int j = 0; j < asStr.length(); j++) {
						staffData[BAR_NUMS_LINE][ind + j] = 
							Character.toString(asStr.charAt(j)); 
					}
				}
			}
			barCount++;
		}
	}


	/**
	 * Adds a TabSymbol's French tablature representation to the Staff on the specified
	 * segment.
	 * 
	 * @param aTabSymbol
	 * @param segment 
	 */ 
	public void addTabSymbolFrench(TabSymbol aTabSymbol, int segment) {
		final String[] frenchSymbols = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "k", "l"};
		int lineNumberFrench = aTabSymbol.getCourse() + NECESSARY_LINE_SHIFT; 
		int fret = aTabSymbol.getFret();
		String symbolFrench = frenchSymbols[fret];
		staffData[lineNumberFrench][segment] = symbolFrench; 
	}


	/**
	 * Adds a TabSymbol's Italian tablature representation to the Staff on the specified
	 * segment.
	 * 
	 * @param aTabSymbol
	 * @param segment
	 */ 
	public void addTabSymbolItalian(TabSymbol aTabSymbol, int segment) {
		int reversalNumber = 7;
		int lineNumberItalian = (reversalNumber - aTabSymbol.getCourse()) + NECESSARY_LINE_SHIFT;
		int fret = aTabSymbol.getFret(); 
		String symbolItalian = Integer.toString(fret);
		staffData[lineNumberItalian][segment] = symbolItalian;
	}


	/**
	 * Adds a TabSymbol's Spanish tablature representation to the Staff on the specified
	 * segment. 
	 * 
	 * @param aTabSymbol
	 * @param segment
	 */ 
	public void addTabSymbolSpanish(TabSymbol aTabSymbol, int segment) {
		int lineNumberSpanish = aTabSymbol.getCourse() + NECESSARY_LINE_SHIFT;
		int fret = aTabSymbol.getFret(); 
		String symbolSpanish = Integer.toString(fret);
		staffData[lineNumberSpanish][segment] = symbolSpanish;
	}

}
