package narritive_model;

import java.util.HashSet;
import java.util.Set;

public class Relationship {
    private Entity subjectEntity;
    private Entity objectEntity;
    Set<String> description = new HashSet<String>();
    boolean bidirectional = false;

    public void setBidirectional() {
        this.bidirectional = true;
    }

    public boolean isBidirecitonal() {
        return this.bidirectional;
    }

    public void setObjectEntity(Entity objectEntity) {
        this.objectEntity = objectEntity;
    }

    public void setSubjectEntity(Entity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    public void addDescription(String description) {
        this.description.add(description);
    }
}
