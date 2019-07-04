package narritive_model;

import java.util.HashSet;
import java.util.Set;

public class Entity {
    private String name;
    private Set<Relationship> relationshipSet = new HashSet<Relationship>();
    private Set<Scene> sceneAppearances = new HashSet<Scene>();
    private Set<String> modifiers = new HashSet<String>();

    public Entity(String name) {
        this.name = name;
    }

    public void addRelationship(Relationship relationship) {
        this.relationshipSet.add(relationship);
    }

    public void addSceneApperance(Scene scene) {
        this.sceneAppearances.add(scene);
    }

    public void addModifier(String modifier) {
        this.modifiers.add(modifier);
    }
}
