package narritive_model;

import edu.stanford.nlp.pipeline.CoreEntityMention;

import java.util.*;
import java.util.HashMap;

public class Model {
    private Map<String, Entity> entities = new HashMap<String, Entity>();
    private Map<String, Location> locations = new HashMap<String, Location>();
    private List<Relationship> relationships = new ArrayList<Relationship>();
    private List<Scene> scenes = new ArrayList<Scene>();
    private Map<String, String> aliasMap = new HashMap();


    public Entity addEntity(CoreEntityMention em) {
        Entity newEntity = new Entity(em.text());
        this.entities.put(newEntity.getName(), newEntity);
        return newEntity;
    }

    public void addAlias(String entityName, String alias) {
        for (Entity entity : this.entities.values()) {
            if(entity.getName() == entityName) {
                entity.addAlias(alias);
                aliasMap.put(alias, entityName);
            }
        }
    }

    public Entity getEntity(String name){
        if (this.entities.containsKey(name)){
            return this.entities.get(name);
        }else if(this.aliasMap.containsKey(name)){
            return this.entities.get(this.aliasMap.get(name));
        }else {
            return null;
        }
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene);
    }
}
