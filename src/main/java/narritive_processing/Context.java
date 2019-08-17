package narritive_processing;

import narritive_model.*;

import java.util.ArrayList;
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
    private static ArrayList<ModelObject> contextEntities = new ArrayList<ModelObject>();
    private int segmentsAnalysed = 0;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
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
    }

    public void addContextEntities(Entity entity) {
        if(contextEntities.size() == 0 || contextEntities.get(contextEntities.size() - 1) != entity) {
            this.contextEntities.add(entity);

            if(entity.getGender().equals(Analyser.genders.get(0))) {
                mostRecentFemale = entity;
            } else if (entity.getGender().equals(Analyser.genders.get(1))) {
                mostRecentMale = entity;
            }
        }
    }

    public void removeDuplicateContextEntities() {
        if(contextEntities.size() < 2) {
            return;
        }

        for (int i = 0; i < contextEntities.size(); i++) {
            for (int j = i + 1; j < contextEntities.size(); j++) {
                if (contextEntities.get(i) == contextEntities.get(j)) {
                    contextEntities.remove(i);
                }
            }
        }
    }

    public ArrayList<ModelObject> getContextEntities() { return this.contextEntities; }

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

    public ModelObject getMostRecentFemale() { return this.mostRecentFemale; }

    public ModelObject getMostRecentMale() { return this.mostRecentMale; }
}
