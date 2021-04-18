package tbp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Staff { 
  
	private int numberOfSegments;
	private String[][] staffData;
	public static final String STAFF_SEGMENT = "-";
	public static final int STAFF_LINES = 11;
	public static final int BAR_NUMS_LINE = 0;
	public static final int RHYTHM_LINE = 1;
	public static final int DIAPASONS_LINE_ITALIAN = 2;
	public static final int TOP_TABLATURE_LINE = 3;
	public static final int UPPER_MIDDLE_TABLATURE_LINE = 5;
	private static final int LOWER_MIDDLE_TABLATURE_LINE = 6;
	public static final int BOTTOM_TABLATURE_LINE = 8;
	public static final int DIAPASONS_LINE_OTHER = 9;
	public static final int FOOTNOTES_LINE = 10;
	private static final int NECESSARY_LINE_SHIFT = 2;
	public static final String SPACE_BETWEEN_STAFFS = "\n";

	public static final String OPEN_BAR_NUM_BRACKET = "[";
	public static final String CLOSE_BAR_NUM_BRACKET = "]";
	private static final String OPEN_FOOTNOTE_PAR = "(";
	private static final String CLOSE_FOOTNOTE_PAR = ")";

	public static final int LEFT_MARGIN = 1; // must be >= 1
	public static final int RIGHT_MARGIN = 2;
	private static final int BAR_NUM_FREQ = 5;


	/**
	 * Constructor. Creates an empty Staff of the specified length.
	 * 
	 * @param numberOfSegments
	 * @return 
	 */
	public Staff(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments; 
		this.staffData = new String[STAFF_LINES][numberOfSegments+RIGHT_MARGIN];
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
		// by adding two RIGHT_MARGIN to each staff line
		for (int staffLine = BAR_NUMS_LINE; staffLine < STAFF_LINES; staffLine++) {
			for (int i = 0; i < RIGHT_MARGIN; i++) { 
				staffData[staffLine][numberOfSegments + i] = spaceSegment;
			}
		}
	}


	/** 
	 * Returns the Staff.
	 * 
	 * @return 
	 */
	public String getStaff() { 
		String staffStr = "";

		// If the first non-empty string is not an OPEN_BAR_NUM_BRACKET, the staff 
		// starts with a bar number. Only if the staff contains bar numbers
		List<String> bnlAsList = Arrays.asList(staffData[BAR_NUMS_LINE]);
		boolean startsWithBarNum =
			bnlAsList.contains(CLOSE_BAR_NUM_BRACKET) && !String.join("", 
			bnlAsList).trim().substring(0, 1).equals(OPEN_BAR_NUM_BRACKET) ? true : 
			false;

		// If the first non-empty string is not an OPEN_FOOTNOTE_PAR, the staff 
		// starts with a footnote. Only if the staff contains footnotes
		List<String> flAsList = Arrays.asList(staffData[FOOTNOTES_LINE]);
		boolean startsWithFootnote =
			flAsList.contains(CLOSE_FOOTNOTE_PAR) && !String.join("", 
			flAsList).trim().substring(0, 1).equals(OPEN_FOOTNOTE_PAR) ? true : 
			false;

		boolean startsWithDecorBarline = 
			ConstantMusicalSymbol.isBarline(staffData[TOP_TABLATURE_LINE][0]);

		for (int i = 0; i < staffData.length; i++) {
			String[] staffLine = staffData[i];
			String staffLineStr = "";
			// Create the string for staffLine
			for (String segment: staffLine) {
				staffLineStr += segment;
			}
			// Shift. If the staff starts with a decorative barline, reduce shift with 1
			int shift = !startsWithDecorBarline ? LEFT_MARGIN: LEFT_MARGIN - 1;
			// a. Shift rhythm symbol line and tablature lines
			if (i >= RHYTHM_LINE && i <= DIAPASONS_LINE_OTHER) {
				staffLineStr = " ".repeat(shift) + staffLineStr;
			}
			// b. Shift bar numbers line
			if (i == BAR_NUMS_LINE) {
				staffLineStr = 
					!startsWithBarNum ? " ".repeat(shift) + staffLineStr :
					" ".repeat(LEFT_MARGIN-1) + OPEN_BAR_NUM_BRACKET + 
					(!startsWithDecorBarline ? staffLineStr : staffLineStr.substring(1));
			}
			// c. Shit footnotes line
			if (i == FOOTNOTES_LINE) {
				staffLineStr = 
					!startsWithFootnote ? " ".repeat(shift) + staffLineStr :
					" ".repeat(LEFT_MARGIN-1) + OPEN_FOOTNOTE_PAR + 
					(!startsWithDecorBarline ? staffLineStr : staffLineStr.substring(1));
			}
			staffStr += staffLineStr + "\n";
		}
		return staffStr;
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
	 * Adds every fifth bar number at the positions in the list given. Bar numbers are 
	 * added above the barline that starts the bar, or, in those cases where this barline
	 * is the last event in a staff, at the start of the next staff.
	 * 
	 * @param indices The indices of the segments containing barline events.
	 * @param firstBar The number of the bar with which the staff begins (this bar can be
	 *                 a continuation of the last (unfinished) bar in the previous staff).
	 * @param startsWithUnfinished Whether or not the system starts with an unfinished bar.
	 * @param startsWithBarline Whether or not the system starts with a barline.
	 * @param endsWithBarline Whether or not the system ends with a barline.                 
	 */
	public void addBarNumbers(List<Integer> indices, int firstBar, boolean startsWithUnfinished,
		boolean startsWithBarline, boolean endsWithBarline) {

		// a. Handle start of staff (if applicable) (no barline index in indices)
		// Add a bar number at the start if the first bar is a multiple-of-freq bar that 
		// is not an unfinished bar from the previous system. Example for BAR_NUM_FREQ = 5:
		//                           [5]     
		// ... | ... | ... | ... | / ... | ... | ... | ... |
		if (firstBar % BAR_NUM_FREQ == 0 && !startsWithUnfinished) {
			// Do not add OPEN_BAR_NUM_BRACKET, which, if the staff does not start with a
			// decorative barline, falls outside of it (at index -1)
			String asStr = String.valueOf(firstBar) + CLOSE_BAR_NUM_BRACKET;
			// Add each char in the bar number at ind
			int ind = !startsWithBarline ? 0 : 1;
			for (int j = 0; j < asStr.length(); j++) {
				staffData[BAR_NUMS_LINE][ind + j] = asStr.substring(j, j+1); 
			}
		}

		// b. Handle rest of staff (barline indices in indices)
		// Add a bar number at the barline index if the barline closes a bar with barCount
		// (n*BAR_NUM_FREQ)-1 (which opens a bar with barCount n*BAR_NUM_FREQ). Example:
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
		// Exception: if a barline closes a bar with barCount (n*BAR_NUM_FREQ)-1 but it
		// is the last event in the staff, the bar number goes to the start of the next 
		// staff (see a.)
		int barCount = firstBar;
		// Remove any decorative opening barline index
		if (indices.get(0) == 0) {
			indices = indices.subList(1, indices.size());
		}
		for (int i = 0; i < indices.size(); i++) {
			int ind = indices.get(i);
			// If the barline at ind closes a bar with barCount (n*BAR_NUM_FREQ)-1
			if (barCount % BAR_NUM_FREQ == (BAR_NUM_FREQ-1)) {			
				// Add bar number only if the barline is not the last event in the staff
				// (in which case it will be added at the beginning of the next staff; 
				// see a. above)			
				if (!(i == indices.size()-1 && endsWithBarline)) {
					String asStr = 
						OPEN_BAR_NUM_BRACKET + String.valueOf(barCount+1) + CLOSE_BAR_NUM_BRACKET;
					// Add each char in the bar number at ind
					for (int j = 0; j < asStr.length(); j++) {
						staffData[BAR_NUMS_LINE][ind + j] = asStr.substring(j, j+1);
					}
				}
			}
			barCount++;
		}
	}


	/** 
	 * Adds the footnote numbers at the positions in the list given 
	 * 
	 * @param indices The indices of the segments containing footnotes events.
	 */
	public void addFootnoteNumbers(List<Integer> indices, int firstFootnoteNum) {
		int footnoteNum = firstFootnoteNum;
		List<Integer> indsPrevFootnote = new ArrayList<>();
		for (int ind : indices) {
			List<Integer> indsCurrFootnote = new ArrayList<>();
			String footnoteNumAsStr = String.valueOf(footnoteNum);
			if (ind != 0) {
				staffData[FOOTNOTES_LINE][ind-1] = OPEN_FOOTNOTE_PAR;
				indsCurrFootnote.add(ind-1);
			}
			for (int j = 0; j < footnoteNumAsStr.length(); j++) {
				staffData[FOOTNOTES_LINE][ind+j] = footnoteNumAsStr.substring(j, j+1);
				indsCurrFootnote.add(ind+j);
			}
			staffData[FOOTNOTES_LINE][ind+footnoteNumAsStr.length()] = CLOSE_FOOTNOTE_PAR;
			indsCurrFootnote.add(ind+footnoteNumAsStr.length());
			
			// If there is overlap between indsCurrFootnote and indsPrevFootnote: correct.
			// There are two minimal event distance scenarios, (1) and (2), which require
			// two types of correction, (a) and (b).
			// (1) shows the minimal distance between two *event* footnotes (this is 
			// because successive footnotes within a bar are grouped together). Assuming 
			// that a piece always has fewer than 100 footnotes, this is never a problem. 
			// (2) shows the minimal distance between an *event* footnote and a *barline* 
			// footnote. This becomes a problem if the index of the first footnote is 
			// greater than 9.
			//
			//      H  H             H  H        
			// (1) |a-|a-|        (2) |a-|a-|    
			//     |--|--|            |--|--|    
			//     |b-|b-|            |b-|b-|        
			//     |c-|c-|            |c-|c-|        
			//     |--|--|            |--|--|    
			//     |--|--|            |--|--| 
			//      *  *               * *
			//     (1)(2) --> OK      (1 2)   --> (a)
			//     (10 11)--> (a)     (1011)  --> NOK (b)
			
			// See https://stackoverflow.com/questions/2400838/efficient-intersection-of-two-liststring-in-java
			List<Integer> intersection = 
				indsPrevFootnote.stream().filter(c -> 
				indsCurrFootnote.contains(c)).collect(Collectors.toList());
			// Correction {(a) / (b)} is needed when footnote n+1 overwrites the last
			// {index / two indices} taken by footnote n (i.e., indsPrevFootnote and
			// indsCurrFootnote have {one index / two indices} in common). The solution 
			// implies replacing the OPEN_FOOTNOTE_PAR at the first index in indsCurrFootnote 
			// with {whitespace / the last digit of the previous footnote number} 
			if (intersection.size() == 1) {
				staffData[FOOTNOTES_LINE][indsCurrFootnote.get(0)] = " ";
			}

			if (intersection.size() == 2) {
				String lastDigit = String.valueOf(footnoteNum  - 1);
				staffData[FOOTNOTES_LINE][indsCurrFootnote.get(0)] = 
					lastDigit.substring(lastDigit.length()-1);
			}
			// Update
			indsPrevFootnote = indsCurrFootnote;
			footnoteNum++;
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
