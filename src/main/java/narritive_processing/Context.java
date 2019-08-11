package narritive_processing;

import narritive_model.*;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private static Context context;
    private static Map<String, Entity> entities;
    private static Relationship relationship;
    private static Location location;
    private static Scene scene;
    private static ModelObject mostRecentModelObjectUpdated;
    private static ModelObject mostRecentFemale;
    private static ModelObject mostRecentMale;
    private static boolean mostRecentlyUpdatedIsOther;
    private int segmentsAnalysed = 0;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
            context.overrideContext(new Entity("Default", "MALE"));
        }

        return context;
    }

    private Context() {
        this.entities = new HashMap<String, Entity>();
        this.location = new Location("");
        this.relationship = null;
        this.scene = new Scene(new BookLocation(this.segmentsAnalysed, 0));
    }

    public void overrideContext(Entity entity) {
        entities.put(entity.getGender(), entity);
        mostRecentModelObjectUpdated = entity;

        if(entity.getGender().equals(Analyser.genders.get(0))) {
            mostRecentFemale = entity;
        } else if (entity.getGender().equals(Analyser.genders.get(1))) {
            mostRecentMale = entity;
        }

        if(entity.getGender().equals(Analyser.genders.get(2))) {
            mostRecentlyUpdatedIsOther = true;
        } else {
            mostRecentlyUpdatedIsOther = false;
        }
    }

    public Entity getContextEntity(String entityType) {
        return this.entities.get(entityType);
    }

    public Map getEntities() { return this.entities; }

    public Relationship getRelationship() {
        return relationship;
    }

    public Location getLocation() {
        return location;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
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

    public ModelObject getMostRecentModelObjectUpdated() {
        return this.mostRecentModelObjectUpdated;
    }

    public boolean isMostRecentlyUpdatedIsOther() { return this.mostRecentlyUpdatedIsOther; }

    public ModelObject getMostRecentFemale() { return this.mostRecentFemale; }

    public ModelObject getMostRecentMale() { return this.mostRecentMale; }
}
