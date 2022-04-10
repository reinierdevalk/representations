package tbp;

public class Event {

	private String encoding;
	private int system;
	private int bar;
	private String footnote;
	private String footnoteID;


	public Event(String e, int s, int b, String f, String fID) {
		encoding = e;
		system = s;
		bar = b;
		footnote = f;
		footnoteID = fID;
	}


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
