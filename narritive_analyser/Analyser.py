from narritive_objects.Model import Model


class Analyser:

    model: Model;

    def __init__(self, model):
        self.model = model

    def processSentence(self, wordArray):
        for wordInfo in wordArray:
            if wordInfo._dependency_relation == "nsubj" and wordInfo._upos == "PROPN":
                self.model.addEntity(wordInfo._lemma)
