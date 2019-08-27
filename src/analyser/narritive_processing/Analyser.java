package analyser.narritive_processing;

import analyser.narritive_model.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.*;

public class Analyser {
    private Model model;
    private Context currentContext = Context.getContext();
    private int segmentsAnalysed = 0;
    private boolean inQuotes = false;

    private static String adjective_tag = "JJ";
    private static List<String> noun_group_tags = Arrays.asList("NN", "NNS", "NNP", "NNPS", "PRP");
    private static List<String> noun_tags = Arrays.asList("NN", "NNS", "NNP", "NNPS");
    private static List<String> preferred_noun_tags = Arrays.asList("NNP", "NNPS", "PRP");
    private static List<String> proper_noun_tags = Arrays.asList("NNP", "NNPS");
    private static List<String> adverb_tags = Arrays.asList("RB", "RBS");
    private static String adjectival_modifier_relation = "amod";
    private static String adverbial_modifier_relation = "advmod";
    private static String noun_subject_relation = "nsubj";
    private static String noun_direct_object_relation = "dobj";
    private static String noun_indirect_object_relation = "iobj";
    private static String relative_clause_relation = "acl:relcl";
	private static List<String> adjective_relationsihps_to_dismiss = Arrays.asList("dobj", "xcomp", "ccomp", "nmod", "acl", "advmod", "advcl");
	private static List<String> noun_relationsihps_to_dismiss = Arrays.asList("conj");
	private String negation_relation = "neg";
	private String verb_tag = "VB";
	private String copula_relation= "cop";
	private String passive_auxiliary_relation = "auxpass";
	private String possessive_tag = "pos";
	private String possessive_noun_modifier_relation = "nmod:poss";
	private static String person_identifier = "PERSON";
	private static String location_identifier = "STATE_OR_PROVINCE";
	private static String misc_noun_identifier = "MISC";
	private static List<String> internal_personal_pronoun = Arrays.asList("I", "ME", "MY", "OUR", "US", "WE");
 	private static List<String> external_female_pronoun = Arrays.asList("SHE", "HER");
	private static List<String> external_male_pronoun = Arrays.asList("HE", "HIM", "HIS");
	private static List<String> external_other_pronoun = Arrays.asList("THEY", "IT", "THEM");
	public static List<String> genders = Arrays.asList("FEMALE", "MALE", "OTHER", "LOCATION");


	public Analyser(Model model) {
        this.model = model;
    }

    public void processParagraph(CoreDocument document) {
        this.currentContext.setSegmentsAnalysed(this.segmentsAnalysed);
        int sentenceIndex = 0;
        Scene current_scene = new Scene(new BookLocation(segmentsAnalysed, sentenceIndex));

        for (CoreEntityMention em : document.entityMentions()) {
            this.processEntities(em, current_scene);
        }

        for (CoreSentence sentence : document.sentences()) {
        	BookLocation bookLocation = new BookLocation(currentContext.getSegmentsAnalysed(), sentenceIndex);
			for (CoreEntityMention em: sentence.entityMentions()) {
				if(isEntity(em)) {
					this.currentContext.overrideContext((Entity) this.model.getModelObject(em.text()));
				}
			}

			RelationContainer sentenceRelations = this.findModifiersAndRelationships(sentence);

			for (Modifier mod : sentenceRelations.modifiers) {
                if(model.getModelObject(mod.subject.originalText()) != null) {
                    this.model.getModelObject(mod.subject.originalText()).addModifier(mod.modifier);
                } else if(mod.subject.tag().equals(preferred_noun_tags.get(2)) && internal_personal_pronoun.contains(mod.subject.originalText().toUpperCase())) {
                    this.model.getModelObject(this.currentContext.getMostRecentModelObjectUpdated().getName()).addModifier(mod.modifier);
                }
            }

            for (ProspectiveRelationship rel : sentenceRelations.relationships){
            	Relationship relationship = processRelationship(sentence, rel, bookLocation);
            	if (relationship!=null){
            		this.model.addRelationship(relationship);
				}
			}

            for (CoreLabel cl: sentence.tokens()) {
                Entity entity = (Entity) this.model.getModelObject(cl.originalText());
                if(entity != null) {
                    this.currentContext.addContextEntities(entity);
                }
            }

            currentContext.removeDuplicateContextEntities();
            sentenceIndex++;
        }
        sentenceIndex--;
		this.sceneMerge(current_scene, new BookLocation(this.currentContext.getSegmentsAnalysed(), sentenceIndex));
        this.segmentsAnalysed++;

        this.assignLinesOfDialogueToEntities(document.quotes());
    }

    private boolean isEntity(CoreEntityMention entityMention) {
        if ((entityMention.entityType().equals(this.person_identifier) && !entityMention.entityTypeConfidences().containsKey("O")) || entityMention.entityType().equals(this.misc_noun_identifier) || this.proper_noun_tags.contains(entityMention.entityType())) {
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
                this.currentContext.overrideContext(this.model.addEntity(em, entityGender));
            } else {
                model.addAlias(this.currentContext.getContextEntity(entityGender).getName(), em.text());
            }
            //retrieve entity object from model and add to scene
            Entity found_entity = (Entity) model.getModelObject(em.text());
            if (!current_scene.containsEntity(found_entity)){
                current_scene.addEntityToScene(found_entity);
            }
        } else if ((em.entityType().equals(this.location_identifier))) {
        	this.model.addEntity(em, genders.get(3));
		}
    }

    private void sceneMerge(Scene current_scene, BookLocation location){
        Scene contextScene = this.currentContext.getScene();
        if (current_scene.matches(contextScene)){
            return;
        }else{
        	current_scene.setSceneEnd(location);
        	this.model.addScene(current_scene);
        	this.currentContext.setScene(current_scene);
        	return;
        }
    }

    private RelationContainer findModifiersAndRelationships(CoreSentence sentence){
		SemanticGraph dependencies = sentence.dependencyParse();

		List<Modifier> modifiers = new ArrayList<Modifier>();
		List<ProspectiveRelationship> relationships = new ArrayList<ProspectiveRelationship>();

        HashMap<String, List<String>> adjustments = new HashMap<String, List<String>>();

        HashSet<String> wordsNotInSpeech = new HashSet<String>();
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
            else if (tag.equals(adjective_tag) || (relation.equals(relative_clause_relation) && noun_tags.contains(tag))){

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
			//Here begin looking for relationships
			//if the word is a verb and does not have the anti-relationships
			else if(tag.startsWith(verb_tag) && !(relation.equals(copula_relation) || relation.equals(passive_auxiliary_relation))){
				List<SemanticGraphEdge> wordChildren = dependencies.getOutEdgesSorted(word.getTarget());
				ProspectiveRelationship relationship = new ProspectiveRelationship();
				relationship.isPossessive = false;
				IndexedWord directObject = null;
				IndexedWord indirectObject = null;
				IndexedWord subject = null;
				for (SemanticGraphEdge child : wordChildren){
					if (child.getRelation().getShortName().equals(noun_subject_relation)){
						subject = child.getTarget();
					}else if (child.getRelation().getShortName().equals(noun_direct_object_relation)){
						directObject = child.getTarget();
					}else if (child.getRelation().getShortName().equals(noun_indirect_object_relation)){
						indirectObject = child.getTarget();
					}
				}
				if ((directObject != null || indirectObject != null) && subject != null){
					relationship.parentObject = subject;

					if (directObject == null){
						relationship.childObject = indirectObject;
					}else{
						relationship.childObject = directObject;
						relationship.usingObject = indirectObject;
					}

					String description = "";
					if (adjustments.containsKey(word.getTarget())){
						List<String> changes = adjustments.get(word.getSource().word());
						for (String change : changes){
							description += change + " ";
						}
					}
					description += word.getTarget().word();
					relationship.description = description;

					relationships.add(relationship);
				}
			}

			//if the word is a possessive, find the matching relationship
			else if(relation.equals(possessive_noun_modifier_relation)){
				ProspectiveRelationship relationship = new ProspectiveRelationship();
				relationship.parentObject = word.getTarget();
				relationship.childObject = word.getSource();
				relationship.isPossessive = true;
				String description = "";
				if (adjustments.containsKey(word.getSource())){
					List<String> changes = adjustments.get(word.getSource().word());
					for (String change : changes){
						description += change + " ";
					}
				}
				relationship.description = description;

				relationships.add(relationship);
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
        return new RelationContainer(modifiers, relationships);
    }

    private Relationship processRelationship(CoreSentence sentence, ProspectiveRelationship prospectiveRelationship, BookLocation location){
		ModelObject subject = findModelObjectFromReference(sentence, prospectiveRelationship.parentObject);
		ModelObject  object = findModelObjectFromReference(sentence, prospectiveRelationship.childObject);
		ModelObject  usingEntity = null;

		if (prospectiveRelationship.usingObject != null){
			usingEntity = findModelObjectFromReference(sentence, prospectiveRelationship.usingObject);
			if (usingEntity == null){
				prospectiveRelationship.description += prospectiveRelationship.usingObject.word();
			}
		}
		if (subject == null || object == null){
			return null;
		}
		if(prospectiveRelationship.isPossessive && !proper_noun_tags.contains(prospectiveRelationship.childObject.tag())){
			String amendedDescription = "has " + prospectiveRelationship.description + prospectiveRelationship.childObject.word();
			return new Relationship(subject, object, usingEntity, amendedDescription, location);
		}else{
			return new Relationship(subject, object, usingEntity, prospectiveRelationship.description, location);
		}
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

    private ModelObject findModelObjectFromReference(CoreSentence sentence, IndexedWord possibleReference){
		if(this.model.getModelObject(possibleReference.originalText()) == null && this.proper_noun_tags.contains(possibleReference.get(CoreAnnotations.PartOfSpeechAnnotation.class))) {

			this.model.addEntity(possibleReference.originalText(), this.genders.get(2));

			//retrieve entity object from model and add to scene
			Entity found_entity = (Entity) model.getModelObject(possibleReference.originalText());
			if (!this.currentContext.getScene().containsEntity(found_entity)){
				this.currentContext.getScene().addEntityToScene(found_entity);
			}

			return this.currentContext.getMostRecentModelObjectUpdated();
		}


		if(this.model.getModelObject(possibleReference.originalText()) != null) {
			return this.model.getModelObject(possibleReference.originalText());
		}

		String gender = null;
		if(this.external_female_pronoun.contains(possibleReference.originalText().toUpperCase())) {
			gender = this.genders.get(0);
		} else if (this.external_male_pronoun.contains(possibleReference.originalText().toUpperCase())) {
			gender = this.genders.get(1);
		} else if (this.external_other_pronoun.contains(possibleReference.originalText().toUpperCase())) {
			gender = this.genders.get(2);
		}

		if (gender == null) {
			return null;
		} else if (possibleReference.index() == 0 && gender.equals(this.genders.get(2))) {
			return this.currentContext.getMostRecentModelObjectUpdated();
		}

		for (int i = possibleReference.index(); i >= 0; i--) {
			CoreLabel word = sentence.tokens().get(i);
			Entity entity = (Entity) this.model.getModelObject(word.originalText());
			if(entity != null && entity.getGender().equals(gender)) {
				return entity;
			}
		}

		for (int i = 0; i < possibleReference.index(); i++) {
			CoreLabel word = sentence.tokens().get(i);
			Entity entity = (Entity) this.model.getModelObject(word.originalText());
			if(entity != null && entity.getGender().equals(this.genders.get(2))) {
				return entity;
			}
		}

		List<ModelObject> contextEntities = this.currentContext.getContextEntities();

		for (int i = contextEntities.size() - 1; i >= 0; i--) {
			Entity entity = (Entity) contextEntities.get(i);
			if(entity.getGender() == gender) {
				return entity;
			} else if (entity.getGender().equals(this.genders.get(2))) {
				return entity;
			}
		}

		return null;
	}

	public Model getModel() {
		return model;
	}

	private class NounCandidate{
    	public GrammaticalRelation relation;
    	public IndexedWord indexedWord;

		public NounCandidate(IndexedWord word, GrammaticalRelation relationship) {
			this.indexedWord = word;
			this.relation = relationship;
		}
	}

	private class RelationContainer{
		public List<Modifier> modifiers;
		public List<ProspectiveRelationship> relationships;

		public RelationContainer(List<Modifier> modifiers, List<ProspectiveRelationship> relationships){
			this.modifiers = modifiers;
			this.relationships = relationships;
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

	private class ProspectiveRelationship{
		public IndexedWord parentObject;
		public IndexedWord childObject;
		public IndexedWord usingObject;
		public String description;
		public boolean isPossessive;
	}
}
