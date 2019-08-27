package analyser.narritive_model;

public class Relationship {
    private ModelObject subjectModelObject;
    private ModelObject objectModelObject;
    private ModelObject usingModelObject;
    private String description;
    private BookLocation location;

    public Relationship(ModelObject subjectModelObject, ModelObject objectModelObject, ModelObject usingModelObject, String description, BookLocation location) {
        this.subjectModelObject = subjectModelObject;
        this.objectModelObject = objectModelObject;
        this.usingModelObject = usingModelObject;
        this.description = description;
        this.location = location;
    }


    public ModelObject getSubjectModelObject() {
        return subjectModelObject;
    }

    public ModelObject getObjectModelObject() {
        return objectModelObject;
    }

    public String getDescription() {
        return description;
    }

    public BookLocation getLocation() {
        return location;
    }

	public ModelObject getUsingModelObject() {
		return usingModelObject;
	}
}
