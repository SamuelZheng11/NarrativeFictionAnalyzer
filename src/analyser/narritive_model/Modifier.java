package analyser.narritive_model;

import java.io.Serializable;

public class Modifier implements Serializable {

	private String _description;
	private BookLocation _location;

	public Modifier (String description, BookLocation timeStamp){
		this._description = description;
		this._location = timeStamp;
	}

	public String get_description() {
		return _description;
	}

	public BookLocation get_location() {
		return _location;
	}
}
