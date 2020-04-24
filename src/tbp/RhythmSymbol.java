package tbp;

import java.util.Arrays;
import java.util.Collections;
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
	public static RhythmSymbol semibrevisTriplet           = new RhythmSymbol("trsb",   "H", 32);
	public static RhythmSymbol semibrevisTripletOpen       = new RhythmSymbol("tr[sb",  "H", 32);
	public static RhythmSymbol semibrevisTripletClose      = new RhythmSymbol("tr]sb",  "H", 32);
	public static RhythmSymbol minimTriplet                = new RhythmSymbol("trmi",   "Q", 16);
	public static RhythmSymbol minimTripletOpen            = new RhythmSymbol("tr[mi",  "Q", 16);
	public static RhythmSymbol minimTripletClose           = new RhythmSymbol("tr]mi",  "Q", 16);
	public static RhythmSymbol minimDottedTriplet          = new RhythmSymbol("trmi*",  "Q", 24);
	public static RhythmSymbol minimDottedTripletOpen      = new RhythmSymbol("tr[mi*", "Q", 24);
	public static RhythmSymbol minimDottedTripletClose     = new RhythmSymbol("tr]mi*", "Q", 24);
	public static RhythmSymbol semiminimTriplet            = new RhythmSymbol("trsm",   "E", 8);
	public static RhythmSymbol semiminimTripletOpen        = new RhythmSymbol("tr[sm",  "E", 8);
	public static RhythmSymbol semiminimTripletClose       = new RhythmSymbol("tr]sm",  "E", 8);
	public static RhythmSymbol semiminimDottedTriplet      = new RhythmSymbol("trsm*",  "E", 12);
	public static RhythmSymbol semiminimDottedTripletOpen  = new RhythmSymbol("tr[sm*", "E", 12);
	public static RhythmSymbol semiminimDottedTripletClose = new RhythmSymbol("tr]sm*", "E", 12);
	public static RhythmSymbol fusaTriplet                 = new RhythmSymbol("trfu",   "S", 4);
	public static RhythmSymbol fusaTripletOpen             = new RhythmSymbol("tr[fu",  "S", 4);
	public static RhythmSymbol fusaTripletClose            = new RhythmSymbol("tr]fu",  "S", 4);
	public static RhythmSymbol fusaDottedTriplet           = new RhythmSymbol("trfu*",  "S", 6);
	public static RhythmSymbol fusaDottedTripletOpen       = new RhythmSymbol("tr[fu*", "S", 6);
	public static RhythmSymbol fusaDottedTripletClose      = new RhythmSymbol("tr]fu*", "S", 6);
	public static RhythmSymbol semifusaTriplet             = new RhythmSymbol("trsf",   "T", 2);
	public static RhythmSymbol semifusaTripletOpen         = new RhythmSymbol("tr[sf",  "T", 2);
	public static RhythmSymbol semifusaTripletClose        = new RhythmSymbol("tr]sf",  "T", 2);
	
	public static RhythmSymbol beamedSemiminimDottedTriplet      = new RhythmSymbol("trsm*-",  "E", 12); // 6
	public static RhythmSymbol beamedSemiminimDottedTripletOpen  = new RhythmSymbol("tr[sm*-", "E", 12); // 6
	public static RhythmSymbol beamedSemiminimDottedTripletClose = new RhythmSymbol("tr]sm*-", "E", 12); // 6
	public static RhythmSymbol beamedSemiminimTriplet            = new RhythmSymbol("trsm-",   "E", 8); // 4
	public static RhythmSymbol beamedSemiminimTripletOpen        = new RhythmSymbol("tr[sm-",  "E", 8); // 4
	public static RhythmSymbol beamedSemiminimTripletClose       = new RhythmSymbol("tr]sm-",  "E", 8); // 4
	public static RhythmSymbol beamedFusaDottedTriplet           = new RhythmSymbol("trfu*-",  "S", 6); // 2
	public static RhythmSymbol beamedFusaDottedTripletOpen       = new RhythmSymbol("tr[fu*-", "S", 6); // 2
	public static RhythmSymbol beamedFusaDottedTripletClose      = new RhythmSymbol("tr]fu*-", "S", 6); // 2
	public static RhythmSymbol beamedFusaTriplet                 = new RhythmSymbol("trfu-",   "S", 4); // 2
	public static RhythmSymbol beamedFusaTripletOpen             = new RhythmSymbol("tr[fu-",  "S", 4); // 2
	public static RhythmSymbol beamedFusaTripletClose            = new RhythmSymbol("tr]fu-",  "S", 4); // 2
	
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
		semibrevisTriplet, semibrevisTripletOpen, semibrevisTripletClose,
		minimTriplet, minimTripletOpen, minimTripletClose,
		minimDottedTriplet, minimDottedTripletOpen, minimDottedTripletClose,
		semiminimTriplet, semiminimTripletOpen, semiminimTripletClose,
		semiminimDottedTriplet, semiminimDottedTripletOpen, semiminimDottedTripletClose,
		fusaTriplet, fusaTripletOpen, fusaTripletClose,
		fusaDottedTriplet, fusaDottedTripletOpen, fusaDottedTripletClose,
		semifusaTriplet, semifusaTripletOpen, semifusaTripletClose,
		//
		beamedSemiminimDottedTriplet, beamedSemiminimDottedTripletOpen, beamedSemiminimDottedTripletClose,
		beamedSemiminimTriplet, beamedSemiminimTripletOpen, beamedSemiminimTripletClose,
		beamedFusaDottedTriplet, beamedFusaDottedTripletOpen, beamedFusaDottedTripletClose,
		beamedFusaTriplet, beamedFusaTripletOpen, beamedFusaTripletClose
	});
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
	private List<Integer> getTripletValues() {
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


	public int getNumDots() {
		int dots = 0;
		String encoding = getEncoding();
		if (encoding.contains(rhythmDot.getEncoding())) {
			for (int i = 0; i < encoding.length(); i++) {
				if (Character.toString(encoding.charAt(i)).equals(rhythmDot.getEncoding())) {
					dots++;
				}
			}
		}
		return dots;
	}


	/**
	 * Returns the undotted version of a dotted RS. If the RS is undotted, returns itself.  
	 * @return
	 */
	public RhythmSymbol getUndotted() {
		String encoding = getEncoding();
		String encodingUndotted = 
			getNumDots() == 0 ? encoding : 	
			encoding.substring(0, encoding.indexOf(rhythmDot.getEncoding()));
		return getRhythmSymbol(encodingUndotted);
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


	public static RhythmSymbol getTripletVariant(String anEncoding) {
		for (RhythmSymbol r: rhythmSymbols) {
			if (r.encoding.endsWith(anEncoding) && r.encoding.startsWith(tripletIndicator)) {
				return r;
			}
		}
		return null;
	}

}
