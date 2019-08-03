package narritive_processing;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.*;
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
	private static String person_identifier = "PERSON";
	private static String misc_noun_identifier = "MISC";
	private static List<String> internal_personal_pronoun = Arrays.asList("I", "ME");
    private static List<String> external_personal_pronoun = Arrays.asList("THEY", "HE", "SHE", "HIM", "HER", "IT", "THEM");
    public static List<String> genders = Arrays.asList("FEMALE", "MALE", "OTHER");


	public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        this.currentContext.setSegmentsAnalysed(this.segmentsAnalysed);
        Scene current_scene = new Scene(new BookLocation(segmentsAnalysed, 0, 0));
        
        for (CoreEntityMention em : document.entityMentions()) {
            this.processEntities(em, current_scene);
        }

        for (CoreSentence sentence : document.sentences()) {
			List<Modifier> sentenceModifiers = this.findModifiers(sentence);
			this.linkEntityToModifier(sentence, sentenceModifiers);
        }

        this.sceneMerge(current_scene);
        this.segmentsAnalysed++;

        this.assignLinesOfDialogueToEntities(document.quotes());
    }

    private void linkEntityToModifier(CoreSentence sentence, List<Modifier> sentenceModifiers) {
        for (Modifier mod : sentenceModifiers) {
            if(model.getModelObject(mod.subject.originalText()) != null) {
                this.model.getModelObject(mod.subject.originalText()).addModifier(mod.modifier);
            } else if(mod.subject.tag().equals(preferred_noun_tags.get(2)) && internal_personal_pronoun.contains(mod.subject.originalText().toUpperCase())) {
                this.model.getModelObject(this.currentContext.getMostRecentModelObjectUpdated().getName()).addModifier(mod.modifier);
            } else if(mod.subject.tag().equals(preferred_noun_tags.get(2)) && external_personal_pronoun.contains(mod.subject.originalText().toUpperCase())) {
                if(this.currentContext.isMostRecentlyUpdatedIsOther()) {
                    this.model.getModelObject(this.currentContext.getContextEntity(genders.get(2)).getName()).addModifier(mod.modifier);
                }
            }
        }

        for (CoreEntityMention em: sentence.entityMentions()) {
            if(isEntity(em)) {
                this.setContext((Entity) this.model.getModelObject(em.text()), this.currentContext.getLocation(), this.currentContext.getRelationship(), this.currentContext.getScene());
            }
        }
    }

    private boolean isEntity(CoreEntityMention entityMention) {
        if ((entityMention.entityType().equals(this.person_identifier) && !entityMention.entityTypeConfidences().containsKey("O")) || entityMention.entityType().equals(this.misc_noun_identifier)) {
            return true;
        }

        return false;
    }

    private String identifyGender(CoreEntityMention entity) {
        if(entity.coreMap().get(CoreAnnotations.GenderAnnotation.class) == null) {
            return this.genders.get(2);

        }

        for (String gender: this.genders) {
            if(entity.coreMap().get(CoreAnnotations.GenderAnnotation.class).equals(gender)) {
                return gender;
            }
        }

        return this.genders.get(2);
    }

    private void processEntities(CoreEntityMention em, Scene current_scene){
        if(this.isEntity(em)) {
            String entityGender = this.identifyGender(em);
            // if the model does not contain this character, add it
            if ( this.currentContext.getContextEntity(entityGender) == null || !this.currentContext.getContextEntity(entityGender).getName().contains(em.text())) {
                this.setContext(this.model.addEntity(em, entityGender), this.currentContext.getLocation(), this.currentContext.getRelationship(), this.currentContext.getScene());
            } else {
                model.addAlias(this.currentContext.getContextEntity(entityGender).getName(), em.text());
            }
            //retrieve entity object from model and add to scene
            Entity found_entity = (Entity) model.getModelObject(em.text());
            if (!current_scene.containsEntity(found_entity)){
                current_scene.addEntityToScene(found_entity);
            }
        }
    }

    private void sceneMerge(Scene current_scene){
        Scene contextScene = this.currentContext.getScene();
        if (current_scene.equals(contextScene)){
            return;
        }else{
            this.model.addScene(current_scene);
            this.currentContext.setContext(this.currentContext.getContextEntity("female"), this.currentContext.getLocation(), this.currentContext.getRelationship(), current_scene);
            return;
        }
    }

    private void setContext(Entity entity, Location location, Relationship relationship, Scene scene) {
        this.currentContext.setContext(entity, location, relationship, scene);
    }

    private List<Modifier>  findModifiers(CoreSentence sentence){
		SemanticGraph dependencies = sentence.dependencyParse();
        List<Modifier> modifiers = new ArrayList<Modifier>();
        HashMap<String, List<String>> adjustments = new HashMap<String, List<String>>();

        HashSet<String> wordsNotInSpeech = new HashSet<String>();
        boolean inQuotes = false;
        for (CoreLabel token : sentence.tokens()){
        	if (token.originalText().equals("\"")){
        		inQuotes = !inQuotes;
			}else if(!inQuotes){
        		wordsNotInSpeech.add(token.originalText());
			}
		}

        //get all word edges in the dep graph
        List<SemanticGraphEdge> words = dependencies.edgeListSorted();
        for (SemanticGraphEdge word : words){
			if (!wordsNotInSpeech.contains(word.getTarget().originalText())){
				continue;
			}

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
                if(this.model.getModelObject(quote.speaker().get()) != null) {
                    ((Entity) this.model.getModelObject(quote.speaker().get())).increaseLinesOfDialogue();
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
