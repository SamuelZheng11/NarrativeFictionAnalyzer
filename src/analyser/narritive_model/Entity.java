package analyser.narritive_model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Entity implements ModelObject, Serializable {
    private String name;
    private String gender;
    private Set<String> aliases = new HashSet<String>();
    private transient Set<Relationship> relationshipSet = new HashSet<Relationship>();
    private transient Set<Scene> sceneAppearances = new HashSet<Scene>();
    private Set<Modifier> modifiers = new HashSet<Modifier>();
    private Long linesOfDialogue = new Long(0);

    public Entity(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return this.name;
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
    }

    public void addRelationship(Relationship relationship) {
        this.relationshipSet.add(relationship);
    }

    public Set<Relationship> getRelationshipSet() {
        return relationshipSet;
    }

    public void addSceneApperance(Scene scene) {
        this.sceneAppearances.add(scene);
    }

    public void addModifier(Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public Long getLinesOfDialogue() {
        return linesOfDialogue;
    }

    public String getGender() { return this.gender; }

    public void increaseLinesOfDialogue() {
        this.linesOfDialogue = ++this.linesOfDialogue;
    }
}
