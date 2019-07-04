package launcher;

import narritive_processing.Analyser;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import narritive_model.Model;
import java.util.Properties;

public class Launcher {

    public static void main(String[] args) {
        // Setup NLP and System Objects
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

        // Prepare objects and paragraph for processing
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument("Lyman Frank Baum was born in 1856 near Syracuse, in New York " +
                "State, in the Eastern part of the United States. His father made a lot of money in the oil business " +
                "and Frank grew up with his brothers and sisters in a beautiful house in the countryside.");
        Model model = new Model();
        Analyser analyser = new Analyser(model);

        pipeline.annotate(document);

        analyser.processParagraph(document);
        System.exit(0);
    }
}
