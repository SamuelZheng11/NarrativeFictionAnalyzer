import stanfordnlp;

from narritive_analyser.Analyser import Analyser
from narritive_objects.Model import Model

stanfordnlp.download('en');

if __name__ == '__main__':
    nlp = stanfordnlp.Pipeline();
    doc = nlp("Dorothy jumped down from her bed and ran to the door. When she opened it, she gave a cry of surprise. "
              "She was not looking at the gray prairie anymore. The house was in a beautiful country that was covered "
              "with green grass and tall trees. There were flowers of every color in the grass and the trees were full "
              "of delicious fruit. Birds sang and there was a little stream of clear water. \"How different this "
              "country is from Kansas!\" Dorothy cried. \"I have never seen such a beautiful place. Where am I ? "
              "And how did I get here?\"");

    model = Model()
    analyser = Analyser(model)
    for sentence in doc.sentences:
        analyser.processSentence(sentence._words)
    item = model.getEntities()
    print(item)
