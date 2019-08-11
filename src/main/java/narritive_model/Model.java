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

    public Entity addEntity(CoreEntityMention em, String gender) {
        return this.addEntity(em.text(), gender);
    }

    public Entity addEntity(String emName, String gender) {
        Entity newEntity = new Entity(emName, gender);
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

    public ModelObject getModelObject(String name){
        if (this.entities.containsKey(name)){
            return this.entities.get(name);
        }else if(this.aliasMap.containsKey(name)){
            return this.entities.get(this.aliasMap.get(name));
        }else if(this.locations.containsKey(name)){
            return this.locations.get(name);
        }else{
            return null;
        }
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene);
    }

    public void addRelationship(Relationship relationship) {
        ModelObject subject;
        ModelObject object;
        ModelObject using;

        //get subject
        if (this.entities.containsKey(relationship.getSubjectModelObject().getName())){
            subject = this.entities.get(relationship.getSubjectModelObject().getName());
        }else if (this.locations.containsKey(relationship.getSubjectModelObject().getName())){
            subject = this.locations.get(relationship.getSubjectModelObject().getName());
        }else{
            return;
        }

        //get object
        if (this.entities.containsKey(relationship.getObjectModelObject().getName())){
            object = this.entities.get(relationship.getObjectModelObject().getName());
        }else if (this.locations.containsKey(relationship.getObjectModelObject().getName())){
            object = this.locations.get(relationship.getObjectModelObject().getName());
        }else{
            return;
        }

        //get using
        if (this.entities.containsKey(relationship.getUsingModelObject().getName())){
            using = this.entities.get(relationship.getUsingModelObject().getName());
        }else if (this.locations.containsKey(relationship.getUsingModelObject().getName())){
            using = this.locations.get(relationship.getUsingModelObject().getName());
        }else{
            return;
        }

        //add relationship to model
        subject.addRelationship(relationship);
        object.addRelationship(relationship);
        using.addRelationship(relationship);
        this.relationships.add(relationship);
    }

    public Entity getEntity(String name){
    	if (entities.containsKey(name)){
    		return entities.get(name);
		}else if(aliasMap.containsKey(name)){
    		return entities.get(aliasMap.get(name));
		}else{
    		return null;
		}
	}

	public Location getLocation(String name){
    	if (locations.containsKey(name)){
    		return locations.get(name);
		}else if(aliasMap.containsKey(name)){
    		return locations.get(aliasMap.get(name));
		}else{
    		return null;
		}
	}

    public Map<String, Entity> getEntities() {
        return entities;
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public Map<String, String> getAliasMap() {
        return aliasMap;
    }
}
