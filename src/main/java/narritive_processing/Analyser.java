package narritive_processing;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import narritive_model.*;

public class Analyser {
    private Model model;
    private Context currentContext = Context.getContext();

    public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        for (CoreEntityMention em : document.entityMentions()) {
            System.out.println(em.entityTypeConfidences());
            if(em.entityType().equals("PERSON") && !em.entityTypeConfidences().containsKey("O")) {
                if (!this.currentContext.getEntity().getName().contains(em.text())) {
                    this.setContext(this.model.addEntity(em), this.currentContext.getLocation(), this.currentContext.getRelationship(), this.currentContext.getScene());
                } else {
                    model.addAlias(this.currentContext.getEntity().getName(), em.text());
                }
            }
        }
    }

    private void setContext(Entity entity, Location location, Relationship relationship, Scene scene) {
        this.currentContext.setContext(entity, location, relationship, scene);
    }
}
