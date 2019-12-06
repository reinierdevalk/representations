package tbp;


public class Staff { 
  
	private int numberOfSegments;
	private String[][] staffData; 
	private static final String STAFF_SEGMENT = "-";
	private static final int STAFF_LINES = 9;  
	private static final int RHYTHM_LINE = 0;
	private static final int DIAPASONS_LINE_ITALIAN = 1;
	private static final int TOP_TABLATURE_LINE = 2;
	private static final int UPPER_MIDDLE_TABLATURE_LINE = 4;
	private static final int LOWER_MIDDLE_TABLATURE_LINE = 5;
	private static final int BOTTOM_TABLATURE_LINE = 7;
	private static final int DIAPASONS_LINE_OTHER = 8;
	private static final int NECESSARY_LINE_SHIFT = 1;


	/**
	 * Constructor. Creates an empty Staff of the specified length.
	 * 
	 * @param numberOfSegments
	 * @return 
	 */
	public Staff(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments; 
		this.staffData = new String[STAFF_LINES][numberOfSegments];
		final String rhythmSegment = " ";
		final String spaceAroundStaffSegment = " ";
		// Construct the empty Staff line by line, segment by segment 
		for (int staffLine = RHYTHM_LINE; staffLine < STAFF_LINES; staffLine++) {  
			for (int segment = 0; segment < numberOfSegments; segment++) { 
				switch (staffLine) {
					// staffLine 0: for RS
					case RHYTHM_LINE:
						staffData[staffLine][segment] = rhythmSegment;
						break;
						// staffLine 1: for any diaposons in Italian tablature
					case DIAPASONS_LINE_ITALIAN: 
						staffData[staffLine][segment] = spaceAroundStaffSegment;
						break;
						// staffLine 8: for any diapasons in French and Spanish tablature
					case DIAPASONS_LINE_OTHER:
						staffData[staffLine][segment] = spaceAroundStaffSegment;
						break;
						// staffLines 2-7: for the tablature staff itself
					default:
						staffData[staffLine][segment] = STAFF_SEGMENT;
				}
			}
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
		
		final String rhythmDot = ".";
		final String beam = "-";
		String symbol = aRhythmSymbol.getSymbol();
		staffData[RHYTHM_LINE][segment] = symbol; 
		// Dotted RS? Add dot in next segment
		if (!aRhythmSymbol.getEncoding().startsWith(RhythmSymbol.tripletIndicator) && 
			aRhythmSymbol.getDuration() % 3 == 0) {
//		if (aRhythmSymbol.getDuration() % 3 == 0) {
			staffData[RHYTHM_LINE][segment + 1] = rhythmDot; 
		}
		// Beamed RS? Add beam in next segment
		if (aRhythmSymbol.getEncoding().endsWith(beam) && showBeam == true) {
			staffData[RHYTHM_LINE][segment + 1] = beam;
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
