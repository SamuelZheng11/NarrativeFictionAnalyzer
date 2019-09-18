package analyser.narritive_model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Location implements ModelObject, Serializable {
    private String name;
    private transient Set<Relationship> relationshipSet = new HashSet<Relationship>();
    private transient Set<Scene> sceneAppearances = new HashSet<Scene>();
    private Set<Modifier> modifiers = new HashSet<Modifier>();

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

    public Set<Modifier> getModifiers() {
        return modifiers;
    }
}
