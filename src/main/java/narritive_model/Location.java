package narritive_model;

import java.util.HashSet;
import java.util.Set;

public class Location implements ModelObject {
    private String name;
    private Set<Relationship> relationshipSet = new HashSet<Relationship>();
    private Set<Scene> sceneAppearances = new HashSet<Scene>();
    private Set<String> modifiers = new HashSet<String>();

    public Location(String name) {
        this.name = name;
    }

    public void addRelationship(Relationship relationship) {
        this.relationshipSet.add(relationship);
    }

    public void addSceneApperance(Scene scene) {
        this.sceneAppearances.add(scene);
    }

    public void addModifier(Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public String getName() {
        return name;
    }

    public Set<Relationship> getRelationshipSet() {
        return relationshipSet;
    }

    public Set<Scene> getSceneAppearances() {
        return sceneAppearances;
    }

    public Set<String> getModifiers() {
        return modifiers;
    }
}
