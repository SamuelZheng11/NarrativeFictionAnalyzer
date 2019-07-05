package narritive_processing;

import narritive_model.*;

public class Context {
    private static Context context;
    private static Entity entity;
    private static Relationship relationship;
    private static Location location;
    private static Scene scene;
    private int segmentsAnalysed = 0;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
        }

        return context;
    }

    private Context() {
        this.entity = new Entity("");
        this.location = new Location("");
        this.relationship = null;
        this.scene = new Scene(new BookLocation(this.segmentsAnalysed, 0, 0));
    }

    public void setContext(Entity entity, Location location, Relationship relationship, Scene scene) {
        this.entity = entity;
        this.location = location;
        this.relationship = relationship;
        this.scene = scene;
    }

    public Entity getEntity() {
        return entity;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Location getLocation() {
        return location;
    }

    public Scene getScene() {
        return scene;
    }

    public int getSegmentsAnalysed() {
        return segmentsAnalysed;
    }

    public void setSegmentsAnalysed(int segmentsAnalysed) {
        this.segmentsAnalysed = segmentsAnalysed;
    }
}
