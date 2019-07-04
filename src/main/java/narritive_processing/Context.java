package narritive_processing;

import narritive_model.Entity;
import narritive_model.Location;
import narritive_model.Relationship;
import narritive_model.Scene;

public class Context {
    private static Context context;
    private static Entity entity;
    private static Relationship relationship;
    private static Location location;
    private static Scene scene;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
        }

        return context;
    }

    private Context() {
        this.entity = new Entity("");
        this.location = new Location("");
        this.relationship = new Relationship();
        this.scene = new Scene();
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

}
