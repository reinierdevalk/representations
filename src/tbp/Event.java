package tbp;

import java.io.Serializable;

/**
 * @author Reinier de Valk
 * @version 12.04.2023 (last well-formedness check)
 */
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	private String encoding;
	private int system;
	private int bar;
	private String footnote;
	private String footnoteID;


	///////////////////////////////
	//
	//  C O N S T R U C T O R S
	//
	public Event(String e, int s, int b, String f, String fID) {
		encoding = e;
		system = s;
		bar = b;
		footnote = f;
		footnoteID = fID;
	}


	//////////////////////////////
	//
	//  G E T T E R S
	//  for instance variables
	//
	public String getEncoding() {
		return encoding;
	}


	public int getSystem() {
		return system;
	}


	public int getBar() {
		return bar;
	}


	public String getFootnote() {
		return footnote;
	}


	public String getFootnoteID() {
		return footnoteID;
	}


	//////////////////////////////////////
	//
	//  I N S T A N C E  M E T H O D S
	//
	@Override
	public boolean equals(Object o) {
		if (o == this) {
            return true;
        }
		if (!(o instanceof Event)) {
            return false;
        }
		Event e = (Event) o;
		return 
			getEncoding().equals(e.getEncoding()) &&
			getSystem() == e.getSystem() &&
			getBar() == e.getBar() &&
			getFootnote() == null ? getFootnote() == e.getFootnote() : 
				getFootnote().equals(e.getFootnote()) &&
			getFootnoteID() == null ? getFootnoteID() == e.getFootnoteID() : 
				getFootnoteID().equals(e.getFootnoteID());
	}
}