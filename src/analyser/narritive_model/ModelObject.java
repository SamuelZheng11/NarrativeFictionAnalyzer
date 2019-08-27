package analyser.narritive_model;

import java.util.Set;

public interface ModelObject {
    public void addModifier(String modifier);

    public void addRelationship(Relationship relationship);

    public Set<Relationship> getRelationshipSet();

    public String getName();
}
