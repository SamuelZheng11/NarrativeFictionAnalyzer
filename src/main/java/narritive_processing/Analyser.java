package narritive_processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import narritive_model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Analyser {
    private Model model;
    private Context currentContext = Context.getContext();
    private int segmentsAnalysed = 0;

    private static String adjective_tag = "JJ";
    private static String[] relative_adjective_tags = {"JJR", "JJS"};
    private static List<String> noun_group_tags = Arrays.asList(new String[]{"NN", "NNS", "NNP", "NNPS", "PRP"});
    private static List<String> noun_tags = Arrays.asList(new String[]{"NN", "NNS", "NNP", "NNPS"});
    private static List<String> adverb_tags = Arrays.asList(new String[]{"RB", "RBS"});
    private static String[] comp_relations = {"ccomp", "xcomp"};
    private static String adverbial_modifier_relation = "advmod";
    private static String adjectival_modifier_relation = "amod";
    private static String relative_clause_relation = "acl:relcl";




    public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        this.currentContext.setSegmentsAnalysed(this.segmentsAnalysed);
        Scene current_scene = new Scene(new BookLocation(segmentsAnalysed, 0, 0));

        this.processEntities(document, current_scene);
        for (CoreSentence sentence : document.sentences()) {
            System.out.println(sentence.text());
            this.findModifiers(sentence.dependencyParse());
        }

            this.sceneMerge(current_scene);
        this.segmentsAnalysed++;
    }

    private void processEntities(CoreDocument document, Scene current_scene){
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

    private List<IndexedWord[]>  findModifiers(SemanticGraph dependencies){
        List<IndexedWord[]> modifiers = new ArrayList<IndexedWord[]>();

        //get all word edges in the dep graph
        List<SemanticGraphEdge> words = dependencies.edgeListSorted();
        for (SemanticGraphEdge word : words){

            String relation = word.getRelation().getShortName();
            String tag = word.getTarget().tag();

            //if word is adjective find matching noun group word
            if (tag.equals(adjective_tag) || (relation.equals(relative_clause_relation) && noun_tags.contains(tag))){

                if (relation.equals(adjectival_modifier_relation)){
                    // TODO: merge with parent if JJ*
                }

                List<IndexedWord> nounCandidates = new ArrayList<IndexedWord>();

                //Check children for noun candidates
                List<SemanticGraphEdge> outgoingEdges = dependencies.getOutEdgesSorted(word.getTarget());
                for (SemanticGraphEdge edge : outgoingEdges){
                    if (noun_group_tags.contains(edge.getTarget().tag())){
                        nounCandidates.add(edge.getTarget());
                    }
                }
                if (!nounCandidates.isEmpty()){
                    IndexedWord candidate = this.pickCandidate(nounCandidates);
                    System.out.println(word.getTarget());
                    for (IndexedWord guy : nounCandidates){
                        System.out.print(guy.word() + ", ");
                    }
                    //TODO ADD TO MODIFIERS
                    continue;
                }

                //if no noun candidate check parents
                List<SemanticGraphEdge> incomingEdges = dependencies.getIncomingEdgesSorted(word.getTarget());
                for (SemanticGraphEdge incomingEdge : incomingEdges){
                    if (noun_group_tags.contains(incomingEdge.getSource().tag())){
                        nounCandidates.add(incomingEdge.getSource());
                    }
                }
                if (!nounCandidates.isEmpty()){
                    IndexedWord candidate = this.pickCandidate(nounCandidates);
                    System.out.println(word.getTarget());
                    for (IndexedWord guy : nounCandidates){
                        System.out.print(guy.word() + ", ");
                    }
                    //TODO ADD TO MODIFIERS
                    continue;
                }

                //finally check siblings
                for (SemanticGraphEdge parentEdge : incomingEdges){
                    List<SemanticGraphEdge> outgoingEdgesFromParent = dependencies.getOutEdgesSorted(parentEdge.getSource());
                    for (SemanticGraphEdge siblingEdge : outgoingEdgesFromParent){
                        if (noun_group_tags.contains(siblingEdge.getTarget().tag())){
                            nounCandidates.add(siblingEdge.getTarget());
                        }
                    }
                }
                if (!nounCandidates.isEmpty()){
                    IndexedWord candidate = this.pickCandidate(nounCandidates);
                    System.out.println(word.getTarget());
                    for (IndexedWord guy : nounCandidates){
                        System.out.print(guy.word() + ", ");
                    }
                    //TODO ADD TO MODIFIERS
                    continue;
                }
            }else if (adverb_tags.contains(relation)){
                // TODO: adverb stuff
            }
        }


        return modifiers;
    }

    private IndexedWord pickCandidate(List<IndexedWord> nounCandidates){
        //TODO: Pick candidate
        return null;
    }
}
