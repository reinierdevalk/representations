//Bij het aanmaken van een JComboBox geef je een lijst Objects door, in 
//dit geval een lijst ArrayLists. Wanneer het programma de namen van de 
//opties in je JComboBox wilt laten zien, wordt de methode "toString()" 
//van elk Object aangeroept. Lijstklassen zoals ArrayList hebben 
//normaliter geen speciale methode hiervoor, dus ze gebruiken toString() 
//van Object, die een String teruggeeft met de naam van de klasse en een 
//soort ID-nummer van het object.
//
//Jij moet dus objecten aan de constructor doorgeven die wel een eigen 
//methode toString() hebben. Zou ik doen met een nieuwe klasse, zo:

package tbp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabSymbolSet extends ArrayList<TabSymbol>{

	private static final long serialVersionUID = 1L;
	private String name;
	private String type;

	public static final TabSymbolSet OCHSENKUN_1558 = new TabSymbolSet("Ochsenkun1558", "German", SymbolDictionary.ochsenkun1558Content);
	public static final TabSymbolSet NEWSIDLER_1536 = new TabSymbolSet("Newsidler1536", "German", SymbolDictionary.newsidler1536Content);
	public static final TabSymbolSet HECKEL_1562 = new TabSymbolSet("Heckel1562", "German", SymbolDictionary.heckel1562Content);
	public static final TabSymbolSet JUDENKUENIG_1523 = new TabSymbolSet("Judenkuenig1523", "German", SymbolDictionary.judenkuenig1523Content);
	public static final TabSymbolSet FRENCH_TAB = new TabSymbolSet("FrenchTab", "French", SymbolDictionary.frenchTabContent);  
	public static final TabSymbolSet ITALIAN_TAB = new TabSymbolSet("ItalianTab", "Italian", SymbolDictionary.italianTabContent);  
	public static final TabSymbolSet SPANISH_TAB = new TabSymbolSet("SpanishTab", "Spanish", SymbolDictionary.spanishTabContent); 
	
	private static List<TabSymbolSet> tabSymbolSets;
	static { tabSymbolSets = Arrays.asList(new TabSymbolSet[]{
		OCHSENKUN_1558, 
		NEWSIDLER_1536, 
		HECKEL_1562, 
		JUDENKUENIG_1523, 
		FRENCH_TAB, 
		ITALIAN_TAB, 
		SPANISH_TAB});
	}


	/**
	 * Constructor. Creates a new TabSymbolSet and populates it with the given list of
	 * TabSymbols.
	 * 
	 * @param name
	 * @param aList
	 * @return
	 */
	public TabSymbolSet(String name, String type, List<TabSymbol> aList) {
		super();
		this.name = name;
		this.type = type;
		for (TabSymbol t : aList) {
			this.add(t);
		}
	}


	public static List<TabSymbolSet> getTabSymbolSets() {
		return tabSymbolSets;
	}


	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}


	/**
	 * Populates the TabSymbolSet with the given TabSymbols.
	 * @param argList
	 */
	public void populate(List<TabSymbol> argList) {
		for (TabSymbol t : argList) {
			this.add(t);
		}
	}


	/**
	 * Returns the TabSymbolSet's name.
	 * 
	 * @return
	 */
	public String toString() {
		return getName();
	}


	/**
	 * Given a TabSymbol from another TabSymbolSet, returns its equivalent in the current
	 * TabSymbolSet. 
	 * 
	 * @param other
	 * @return
	 */
	public TabSymbol getTabSymbolEquivalent(TabSymbol other) {
		for (TabSymbol curr : this) {
			if (curr.getCourse() == other.getCourse() && curr.getFret() == other.getFret()) {
				return curr;
			}
		}
		return null;
	}


	/**
	 * Searches the specified list for the TabSymbolSet whose attribute name equals the 
	 * specified name. Returns null if the list does not contain such a TabSymbolSet.
	 * 
	 * @param aName
	 * @param tabSymbolSets
	 * @return
	 */
	public static TabSymbolSet getTabSymbolSet(String aName) {
		for (TabSymbolSet tss : getTabSymbolSets()) {
			if (tss.getName().equals(aName)) {
				return tss;
			}
		}
		return null; 
	}

}
