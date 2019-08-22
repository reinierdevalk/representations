package tbp;

import java.util.Arrays;
import java.util.List;

public class MensurationSign {

	private String encoding;
	private String symbol;
	private int staffLine;
	private Integer[] meter;

	public static MensurationSign O = new MensurationSign("MO", "O", 3, new Integer[]{3, 4});
	public static MensurationSign three = new MensurationSign("M3", "3", 3, new Integer[]{3, 4});
	public static MensurationSign two = new MensurationSign("M2", "2", 3, new Integer[]{2, 4});
	public static MensurationSign crossedC = new MensurationSign("McC", "\u00A2", 3, new Integer[]{2, 2}); 
	public static MensurationSign C = new MensurationSign("MC", "C", 3, new Integer[]{4, 4});
	public static MensurationSign doubleCrossedC = new MensurationSign("McC2", "\u00A2", 3, new Integer[]{2, 1});
	
	
	public static MensurationSign threeTwo = new MensurationSign("M3:2", "3", 3, new Integer[]{3, 2});
	public static MensurationSign threeOne = new MensurationSign("M3:1", "3", 3, new Integer[]{3, 1});
	public static MensurationSign oneTwo = new MensurationSign("M1:2", "1", 3, new Integer[]{1, 2});
	public static MensurationSign fourTwo = new MensurationSign("M4:2", "4", 3, new Integer[]{4, 2});
	public static MensurationSign sixTwo = new MensurationSign("M6:2", "6", 3, new Integer[]{6, 2});
	public static MensurationSign twoOne = new MensurationSign("M2:1", "\u00A2", 3, new Integer[]{2, 1});

	public static List<MensurationSign> mensurationSigns;
	static { mensurationSigns = Arrays.asList(new MensurationSign[]{
		// C
		MensurationSign.C,
		new MensurationSign("MC6", "C", 6, new Integer[]{4, 4}),
		new MensurationSign("MC5", "C", 5, new Integer[]{4, 4}),
		new MensurationSign("MC4", "C", 4, new Integer[]{4, 4}),
		new MensurationSign("MC3", "C", 3, new Integer[]{4, 4}),
		new MensurationSign("MC2", "C", 2, new Integer[]{4, 4}),
		new MensurationSign("MC1", "C", 1, new Integer[]{4, 4}),
		// O
		MensurationSign.O,
		new MensurationSign("MO6", "O", 6, new Integer[]{3, 4}),
		new MensurationSign("MO5", "O", 5, new Integer[]{3, 4}),
		new MensurationSign("MO4", "O", 4, new Integer[]{3, 4}),
		new MensurationSign("MO3", "O", 3, new Integer[]{3, 4}),
		new MensurationSign("MO2", "O", 2, new Integer[]{3, 4}),
		new MensurationSign("MO1", "O", 1, new Integer[]{3, 4}),
		// 3
		MensurationSign.three,
		new MensurationSign("M36", "3", 6, new Integer[]{3, 4}),
		new MensurationSign("M35", "3", 5, new Integer[]{3, 4}),
		new MensurationSign("M34", "3", 4, new Integer[]{3, 4}),
		new MensurationSign("M33", "3", 3, new Integer[]{3, 4}),
		new MensurationSign("M32", "3", 2, new Integer[]{3, 4}),
		new MensurationSign("M31", "3", 1, new Integer[]{3, 4}),
		// Crossed C
		MensurationSign.crossedC,
		new MensurationSign("McC6", "\u00A2", 6, new Integer[]{2, 2}),
		new MensurationSign("McC5", "\u00A2", 5, new Integer[]{2, 2}),
		new MensurationSign("McC4", "\u00A2", 4, new Integer[]{2, 2}),
		new MensurationSign("McC3", "\u00A2", 3, new Integer[]{2, 2}),
		new MensurationSign("McC2", "\u00A2", 2, new Integer[]{2, 2}),
		new MensurationSign("McC1", "\u00A2", 1, new Integer[]{2, 2}),
		// 3/2, 4/2, 6/2, 2/1
		MensurationSign.threeTwo,
		MensurationSign.fourTwo,
		MensurationSign.sixTwo,
		MensurationSign.twoOne,
		MensurationSign.threeOne,
		MensurationSign.oneTwo, 
		// 2
		MensurationSign.two});
	}
	

	/**
	 * Constructor. Creates a new MensurationSign with the specified attributes and 
	 * adds this to the specified list.
	 * 
	 * @param encoding
	 * @param symbol
	 * @param staffLine
	 * @return
	 */
	public MensurationSign (String encoding, String symbol, int staffLine, Integer[] meter) {
		this.encoding = encoding;
		this.symbol = symbol;
		this.staffLine = staffLine;
		this.meter = meter;
	}


	/**
	 * Returns the MensurationSign's encoding.
	 * 
	 * @return
	 */  
	public String getEncoding() {
		return encoding;
	}


	/**
	 * Returns the MensurationSign's tablature representation.
	 * 
	 * @return
	 */
	public String getSymbol() {
		return symbol;  
	}
	
	
	/**
	 * Returns the MensurationSign's meter.
	 * 
	 * @return
	 */
	public Integer[] getMeter() {
		return meter;  
	}


	/**
	 * Returns the staff line the MensurationSign is placed on.
	 * 
	 * @return
	 */
	public int getStaffLine() {
		return staffLine;
	}


	/**
	 * Searches the specified list for the MensurationSign whose attribute encoding
	 * equals the specified encoding. Returns null if the list does not contain such
	 * a MensurationSign.
	 * 
	 * @param anEncoding
	 * @param aList
	 * @return
	 */
	public static MensurationSign getMensurationSign(String anEncoding) {
		for (MensurationSign m: mensurationSigns) {
			if (m.encoding.equals(anEncoding)) {
				return m;
			}
		}
		return null;
	}

}
