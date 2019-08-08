package narritive_model;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private Map<String, Location> sceneLocations = new HashMap<String, Location>();
    private Map<String, Entity> sceneEntities = new HashMap<String, Entity>();
    private BookLocation sceneBeginning;
    private BookLocation sceneEnd;

    public Scene(BookLocation sceneBeginning) {
        this.sceneBeginning = sceneBeginning;
    }

    public void addLocationToScene(Location location) {
        this.sceneLocations.put(location.getName(), location);
    }

    public void addEntityToScene(Entity entity) {
        this.sceneEntities.put(entity.getName(), entity);
    }

    public boolean containsEntity(Entity entity){
     return sceneEntities.containsKey(entity.getName());
    }

	public void setSceneEnd(BookLocation sceneEnd) {
		this.sceneEnd = sceneEnd;
	}

	public boolean equals (Scene scene){
        if(this.sceneBeginning != scene.sceneBeginning || !this.sceneBeginning.equals(scene.sceneBeginning)){
            return false;
        }else if (this.sceneEnd != scene.sceneEnd || !this.sceneEnd.equals(scene.sceneEnd)){
            return false;
        }else if (!this.sceneLocations.keySet().equals(scene.sceneLocations.keySet())){
            return false;
        }else if (!this.sceneEntities.keySet().equals(scene.sceneEntities.keySet())){
            return false;
        }
        return true;
    }
}
