package analyser.narritive_model;

public class Modifier {

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
