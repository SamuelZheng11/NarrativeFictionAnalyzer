from narritive_objects.Model import Model


class Analyser:

    model: Model;

    def __init__(self, model):
        self.model = model

    def processSentence(self, wordArray):
        for wordInfo in wordArray:
            if wordInfo[0] == "nsubj":
                self.model.addEntity(wordInfo[0])
