package tbp;

import java.util.Arrays;
import java.util.List;

public class RhythmSymbol {

	public static RhythmSymbol longa = new RhythmSymbol("lo", "D", 64);
	public static RhythmSymbol brevis = new RhythmSymbol("br", "W", 32);
	public static RhythmSymbol semibrevis = new RhythmSymbol("sb", "H", 16);
	public static RhythmSymbol minim = new RhythmSymbol("mi", "Q", 8);
	public static RhythmSymbol semiminim = new RhythmSymbol("sm", "E", 4);
	public static RhythmSymbol fusa = new RhythmSymbol("fu", "S", 2);
	public static RhythmSymbol semifusa = new RhythmSymbol("sf", "T", 1);
	public static RhythmSymbol coronaBrevis = new RhythmSymbol("co2", "C", 32);
	public static RhythmSymbol coronaDottedSemibrevis = new RhythmSymbol("co1*", "C", 24);
	public static RhythmSymbol coronaSemibrevis = new RhythmSymbol("co1", "C", 16);
	public static RhythmSymbol fermateDotted = new RhythmSymbol("fe*", "F", 48);
	
	public static RhythmSymbol rhythmDot = new RhythmSymbol("*", ".", -1);
//	public static RhythmSymbol doubleRhythmDot = new RhythmSymbol(":", ".", -1);
	public static RhythmSymbol brevisDotted = new RhythmSymbol("br*", "W", 48);
	public static RhythmSymbol semibrevisDotted = new RhythmSymbol("sb*", "H", 24);
	public static RhythmSymbol minimDotted = new RhythmSymbol("mi*", "Q", 12);
	public static RhythmSymbol minimDoubleDotted = new RhythmSymbol("mi**", "Q", 14);
	public static RhythmSymbol semiminimDotted = new RhythmSymbol("sm*", "E", 6);
	public static RhythmSymbol fusaDotted = new RhythmSymbol("fu*", "S", 3);
	
	public static RhythmSymbol beamedSemiminimDotted = new RhythmSymbol("sm*-", "E", 6);
	public static RhythmSymbol beamedSemiminim = new RhythmSymbol("sm-", "E", 4);
	public static RhythmSymbol beamedFusa = new RhythmSymbol("fu-", "S", 2);
	public static RhythmSymbol beamedSemifusa = new RhythmSymbol("sf-", "T", 1);
	
	public static RhythmSymbol triplet = new RhythmSymbol("tr", "3", -1);
	public static RhythmSymbol tripletMinim = new RhythmSymbol("trmi", "3", 6); // 6-5-5 instead of 8-8
	
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
		triplet,
		tripletMinim});
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
