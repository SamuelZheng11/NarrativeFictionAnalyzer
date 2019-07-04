import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.*;

import java.util.List;
import java.util.Properties;

public class Launcher {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument doc = new CoreDocument("Harry Potter is the son of James Potter.");
        pipeline.annotate(doc);

        List<?> entities = doc.entityMentions();
        System.exit(0);
    }

}
