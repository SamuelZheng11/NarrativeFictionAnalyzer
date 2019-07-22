package narritive_processing;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreQuote;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import narritive_model.*;

import java.util.*;

public class Analyser {
    private Model model;
    private Context currentContext = Context.getContext();
    private int segmentsAnalysed = 0;

    private static String adjective_tag = "JJ";
    private static List<String> noun_group_tags = Arrays.asList("NN", "NNS", "NNP", "NNPS", "PRP");
    private static List<String> noun_tags = Arrays.asList("NN", "NNS", "NNP", "NNPS");
    private static List<String> preferred_noun_tags = Arrays.asList("NNP", "NNPS", "PRP");
    private static List<String> adverb_tags = Arrays.asList("RB", "RBS");
    private static String adjectival_modifier_relation = "amod";
    private static String adverbial_modifier_relation = "advmod";
    private static String noun_subject_relation = "nsubj";
    private static String relative_clause_relation = "acl:relcl";
	private static List<String> adjective_relationsihps_to_dismiss = Arrays.asList("dobj", "xcomp", "ccomp", "nmod", "acl", "advmod", "advcl");
	private static List<String> noun_relationsihps_to_dismiss = Arrays.asList("conj");
	private String negation_relation = "neg";


	public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        this.currentContext.setSegmentsAnalysed(this.segmentsAnalysed);
        Scene current_scene = new Scene(new BookLocation(segmentsAnalysed, 0, 0));

        this.processEntities(document, current_scene);
        for (CoreSentence sentence : document.sentences()) {
			System.out.println(sentence.text());
			List<Modifier> sentenceModifiers = this.findModifiers(sentence.dependencyParse());
        }

            this.sceneMerge(current_scene);
        this.segmentsAnalysed++;

        this.assignLinesOfDialogueToEntities(document.quotes());
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

    private List<Modifier>  findModifiers(SemanticGraph dependencies){
        List<Modifier> modifiers = new ArrayList<Modifier>();
        HashMap<String, List<String>> adjustments = new HashMap<String, List<String>>();

        //get all word edges in the dep graph
        List<SemanticGraphEdge> words = dependencies.edgeListSorted();
        for (SemanticGraphEdge word : words){

            String relation = word.getRelation().getShortName();
            String tag = word.getTarget().tag();

            // if word is an adverb, attach to word to be adjusted
			if (adverb_tags.contains(tag)){
				if (relation.equals(negation_relation) || relation.equals(adverbial_modifier_relation)){
					if (noun_group_tags.contains(word.getSource().tag()) || word.getSource().tag().equals(adjective_tag) || adverb_tags.contains(word.getSource().tag())){
						if (!adjustments.containsKey(word.getSource().word())){
							adjustments.put(word.getSource().word(), new ArrayList<String>());
						}
						adjustments.get(word.getSource().word()).add(word.getTarget().word());
					}
				}
				continue;
			}

            //if word is adjective find matching noun group word
            if (tag.equals(adjective_tag) || (relation.equals(relative_clause_relation) && noun_tags.contains(tag))){

                if (relation.equals(adjectival_modifier_relation)){
                	if (word.getSource().tag().matches("JJ.") || adverb_tags.contains(word.getSource().tag())){
						if (!adjustments.containsKey(word.getSource().word())){
							adjustments.put(word.getSource().word(), new ArrayList<String>());
						}
						adjustments.get(word.getSource().word()).add(word.getTarget().word());
					}
					continue;
                }

                List<NounCandidate> nounCandidates = new ArrayList<NounCandidate>();

                //Check children for noun candidates
                List<SemanticGraphEdge> outgoingEdges = dependencies.getOutEdgesSorted(word.getTarget());
                for (SemanticGraphEdge edge : outgoingEdges){
                    if (noun_group_tags.contains(edge.getTarget().tag())){
                        nounCandidates.add(new NounCandidate(edge.getTarget(), edge.getRelation()));
                    }
                }
				if (addCandidate(modifiers, word, nounCandidates))
					continue;

				//if no noun candidate check parents
                List<SemanticGraphEdge> incomingEdges = dependencies.getIncomingEdgesSorted(word.getTarget());
                for (SemanticGraphEdge incomingEdge : incomingEdges){
                    if (noun_group_tags.contains(incomingEdge.getSource().tag())){
                        nounCandidates.add(new NounCandidate(incomingEdge.getSource(), incomingEdge.getRelation()));
                    }
                }
				if (addCandidate(modifiers, word, nounCandidates))
					continue;

				//finally check siblings
                for (SemanticGraphEdge parentEdge : incomingEdges){
                    List<SemanticGraphEdge> outgoingEdgesFromParent = dependencies.getOutEdgesSorted(parentEdge.getSource());
                    for (SemanticGraphEdge siblingEdge : outgoingEdgesFromParent){
                        if (noun_group_tags.contains(siblingEdge.getTarget().tag())){
                            nounCandidates.add(new NounCandidate(siblingEdge.getTarget(), siblingEdge.getRelation()));
                        }
                    }
                }
				if (addCandidate(modifiers, word, nounCandidates))
					continue;
			}
        }
        for (Modifier mod : modifiers){
        	if (adjustments.containsKey(mod.modifier)){
        		Collections.reverse(adjustments.get((mod.modifier)));
        		for (String change : adjustments.get(mod.modifier)){
        			mod.modifier = change + " " + mod.modifier;
				}
			}
		}
        return modifiers;
    }

	private boolean addCandidate(List<Modifier> modifiers, SemanticGraphEdge word, List<NounCandidate> nounCandidates) {
		if (!nounCandidates.isEmpty()){
			IndexedWord candidate = this.pickCandidate(nounCandidates, word);
			if (candidate != null){
				modifiers.add(new Modifier(candidate, word.getTarget().word()));
			}
			return true;
		}
		return false;
	}

	private IndexedWord pickCandidate(List<NounCandidate> nounCandidates, SemanticGraphEdge word){
        if (nounCandidates.size() == 1){
            return nounCandidates.get(0).indexedWord;
        }
        //If the adjective is the wrong type it is irrelevant and should be ignored
        for (String rel : adjective_relationsihps_to_dismiss){
        	if (word.getRelation().toString().startsWith(rel)){
        		return null;
			}
		}

        NounCandidate bestCandidate = null;

        for (NounCandidate candidate : nounCandidates){

        	//If the noun is the wrong type it is not a candidate
        	for (String rel : noun_relationsihps_to_dismiss) {
				if (candidate.relation.toString().startsWith(rel)) {
					continue;
				}
			}

			//If this is the first viable candidate, set it
			if (bestCandidate == null){
				bestCandidate = candidate;
				continue;
			}

			//If the candidate is a subject it is likely the best candidate
			if(candidate.relation.toString().startsWith(noun_subject_relation)){
				if (!bestCandidate.relation.toString().startsWith(noun_subject_relation)){
					bestCandidate = candidate;
					continue;
				}
			}
			//If neither the existing best nor the candidate are subjects, compare on if they have a preferred tag
			if (!bestCandidate.relation.toString().startsWith(noun_subject_relation)){
				if(!preferred_noun_tags.contains(bestCandidate.indexedWord.tag()) && preferred_noun_tags.contains(candidate.indexedWord.tag())){
					bestCandidate = candidate;
				}
			}
		}
        return bestCandidate.indexedWord;
    }

    private void assignLinesOfDialogueToEntities(List<CoreQuote> sentences) {
        for (CoreQuote quote: sentences) {
            if(quote.hasSpeaker) {
                if(this.model.getEntity(quote.speaker().get()) != null) {
                    this.model.getEntity(quote.speaker().get()).increaseLinesOfDialogue();
                }
            }
        }
    }

    private class NounCandidate{
    	public GrammaticalRelation relation;
    	public IndexedWord indexedWord;

		public NounCandidate(IndexedWord word, GrammaticalRelation relationship) {
			this.indexedWord = word;
			this.relation = relationship;
		}
	}

	private class Modifier {
		public IndexedWord subject;
		public String modifier;
		public Modifier(IndexedWord subject, String modifier) {
			this.subject = subject;
			this.modifier = modifier;
		}
	}
}
