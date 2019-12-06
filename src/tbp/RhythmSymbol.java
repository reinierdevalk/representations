package tbp;

import java.util.Arrays;
import java.util.List;

public class RhythmSymbol {

	// To enable triplets, a quarter note beat is divided into 24 bits. The smallest triplet
	// thus possible is the 32nd triplet (2-2-2)
	public static RhythmSymbol longa = new RhythmSymbol("lo", "D", 192); // 64
	public static RhythmSymbol brevis = new RhythmSymbol("br", "W", 96); // 32
	public static RhythmSymbol semibrevis = new RhythmSymbol("sb", "H", 48); // 16
	public static RhythmSymbol minim = new RhythmSymbol("mi", "Q", 24); // 8
	public static RhythmSymbol semiminim = new RhythmSymbol("sm", "E", 12); // 4
	public static RhythmSymbol fusa = new RhythmSymbol("fu", "S", 6); // 2
	public static RhythmSymbol semifusa = new RhythmSymbol("sf", "T", 3); // 1
	public static RhythmSymbol coronaBrevis = new RhythmSymbol("co2", "C", 96); // 32
	public static RhythmSymbol coronaDottedSemibrevis = new RhythmSymbol("co1*", "C", 72); // 24
	public static RhythmSymbol coronaSemibrevis = new RhythmSymbol("co1", "C", 48); // 16
	public static RhythmSymbol fermateDotted = new RhythmSymbol("fe*", "F", 144); // 48
	
	public static RhythmSymbol rhythmDot = new RhythmSymbol("*", ".", -1);
//	public static RhythmSymbol doubleRhythmDot = new RhythmSymbol(":", ".", -1);
	public static RhythmSymbol brevisDotted = new RhythmSymbol("br*", "W", 144); // 48
	public static RhythmSymbol semibrevisDotted = new RhythmSymbol("sb*", "H", 72); // 24
	public static RhythmSymbol minimDotted = new RhythmSymbol("mi*", "Q", 36); // 12
	public static RhythmSymbol minimDoubleDotted = new RhythmSymbol("mi**", "Q", 42); // 14
	public static RhythmSymbol semiminimDotted = new RhythmSymbol("sm*", "E", 18); // 6
	public static RhythmSymbol fusaDotted = new RhythmSymbol("fu*", "S", 9); // 3
	
	public static RhythmSymbol beamedSemiminimDotted = new RhythmSymbol("sm*-", "E", 18); // 6
	public static RhythmSymbol beamedSemiminim = new RhythmSymbol("sm-", "E", 12); // 4
	public static RhythmSymbol beamedFusa = new RhythmSymbol("fu-", "S", 6); // 2
	public static RhythmSymbol beamedSemifusa = new RhythmSymbol("sf-", "T", 3); // 1
	
	public static String tripletIndicator = "tr"; 
	public static RhythmSymbol semibrevisTriplet = new RhythmSymbol("trsb", "H", 32);
	public static RhythmSymbol minimTriplet = new RhythmSymbol("mi", "Q", 16);
	public static RhythmSymbol semiminimTriplet = new RhythmSymbol("sm", "E", 8);
	public static RhythmSymbol fusaTriplet = new RhythmSymbol("fu", "S", 4);
	public static RhythmSymbol semifusaTriplet = new RhythmSymbol("sf", "T", 2);
	
//	public static RhythmSymbol triplet = new RhythmSymbol("tr", "3", -1);
//	public static RhythmSymbol tripletMinim = new RhythmSymbol("trmi", "3", 6); // 6-5-5 instead of 8-8
	
	public static List<RhythmSymbol> rhythmSymbols;
	static { rhythmSymbols = Arrays.asList(new RhythmSymbol[]{
		longa,
		brevis,
		semibrevis,
		minim,
		semiminim,
		fusa,
		semifusa,
		coronaBrevis,
		coronaDottedSemibrevis,
		coronaSemibrevis,
		fermateDotted,
		//
		rhythmDot,
		brevisDotted,
		semibrevisDotted,
		minimDotted,
		minimDoubleDotted,
		semiminimDotted,
		fusaDotted,
		//
		beamedSemiminimDotted,
		beamedSemiminim,
		beamedFusa,
		beamedSemifusa,
		//
//		triplet,
//		tripletMinim,
		semibrevisTriplet,
		minimTriplet,
		semiminimTriplet,
		fusaTriplet,
		semifusaTriplet});
	}


	private String encoding;
	private String symbol;
	private int duration;


	/**
	 * Constructor. Creates a new RhythmSymbol with the specified 
	 * attributes and adds this to the specified list.
	 * 
	 * @param encoding
	 * @param symbol
	 * @param duration
	 * @return
	 */
	public RhythmSymbol (String encoding, String symbol, int duration) {
		this.encoding = encoding;
		this.symbol = symbol;
		this.duration = duration;
	}


	public static List<RhythmSymbol> getRhythmSymbols() {
		return rhythmSymbols;
	}


	/**
	 * Returns the RhythmSymbol's encoding.
	 * 
	 * @return
	 */  
	public String getEncoding() {
		return encoding;
	}
	
	
	/**
	 * Returns the values of a triplet of this RhythmSymbol.
	 * 
	 * @return
	 */
	public List<Integer> getTripletValues() {
		// Three notes in the time of two semibreves (2*16 semifusae)
		if (this.equals(semibrevis)) {
			return Arrays.asList(new Integer[]{11, 11, 10});
		}
		// Three notes in the time of two minims (2*8 semifusae)
		else if (this.equals(minim)) {
			return Arrays.asList(new Integer[]{6, 5, 5});
		}
		// Three notes in the time of semiminims (2*4 semifusae)
		else if (this.equals(semiminim)) {
			return Arrays.asList(new Integer[]{3, 3, 2});
		}
		else {
			return null;
		}
	}


	/**
	 * Returns the RhythmSymbol's tablature representation.
	 * 
	 * @return
	 */
	public String getSymbol() {
		return symbol;
	}


	/**
	 * Returns the RhythmSymbol's duration (in semifusa).
	 * 
	 * @return
	 */
	public int getDuration() {
		return duration;
	}


	/**
	 * Searches rhythmSymbols for the RhythmSymbol whose attribute
	 * encoding equals the specified encoding. Returns null if the list
	 * does not contain such a RhythmSymbol.
	 * 
	 * @param anEncoding
	 * @param aList
	 * @return
	 */
	public static RhythmSymbol getRhythmSymbol(String anEncoding) {
		for (RhythmSymbol r: rhythmSymbols) {
			if (r.encoding.equals(anEncoding)) {
				return r;
			}
		}
		return null;
	}

}
