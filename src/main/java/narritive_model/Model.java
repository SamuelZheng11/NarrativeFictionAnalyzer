package narritive_model;

import edu.stanford.nlp.pipeline.CoreEntityMention;

import java.util.HashSet;
import java.util.Set;

public class Model {
    private Set<Entity> entities = new HashSet<Entity>();
    private Set<Location> locations = new HashSet<Location>();
    private Set<Relationship> relationships = new HashSet<Relationship>();
    private Set<Scene> scenes = new HashSet<Scene>();


    public Entity addEntity(CoreEntityMention em) {
        Entity newEntity = new Entity(em.text());
        this.entities.add(newEntity);
        return newEntity;
    }

    public void addAlias(String entityName, String alias) {
        for (Entity entity : this.entities) {
            if(entity.getName() == entityName) {
                entity.addAlias(alias);
            }
        }
    }
}
