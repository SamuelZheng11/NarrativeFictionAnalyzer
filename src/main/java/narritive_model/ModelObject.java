package narritive_model;

import java.util.Set;

public interface ModelObject {
    public void addModifier(Modifier modifier);

    public void addRelationship(Relationship relationship);

    public Set<Relationship> getRelationshipSet();

    public String getName();
}
