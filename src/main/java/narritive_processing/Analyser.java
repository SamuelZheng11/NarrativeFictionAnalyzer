package narritive_processing;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import narritive_model.*;

public class Analyser {
    private Model model;
    private Context currentContext = Context.getContext();
    private int segmentsAnalysed = 0;

    public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        this.currentContext.setSegmentsAnalysed(this.segmentsAnalysed);
        Scene current_scene = new Scene(new BookLocation(segmentsAnalysed, 0, 0));
        for (CoreEntityMention em : document.entityMentions()) {
            if(em.entityType().equals("PERSON") && !em.entityTypeConfidences().containsKey("O")) {
                // if the model does not contain this character, add it
                if (!this.currentContext.getEntity().getName().contains(em.text())) {
                    this.setContext(this.model.addEntity(em), this.currentContext.getLocation(), this.currentContext.getRelationship(), this.currentContext.getScene());
                } else {
                    model.addAlias(this.currentContext.getEntity().getName(), em.text());
                }
                //retrieve entity object from model and add to scene
                Entity found_entity = model.getEntity(em.text());
                if (!current_scene.containsEntity(found_entity)){
                    current_scene.addEntityToScene(found_entity);
                }
            }
        }
        this.sceneMerge(current_scene);
        this.segmentsAnalysed++;
    }

    private void sceneMerge(Scene current_scene){
        Scene contextScene = this.currentContext.getScene();
        if (current_scene.equals(contextScene)){
            return;
        }else{
            this.model.addScene(current_scene);
            this.currentContext.setContext(this.currentContext.getEntity(), this.currentContext.getLocation(), this.currentContext.getRelationship(), current_scene);
            return;
        }
    }

    private void setContext(Entity entity, Location location, Relationship relationship, Scene scene) {
        this.currentContext.setContext(entity, location, relationship, scene);
    }
}
