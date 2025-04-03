package es.srjavierdev.intelligentNPCs.ai.nlp;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class NLPParser {
    private static StanfordCoreNLP pipeline;

    public NLPParser() {
        // Inicializar pipeline solo una vez
        if (pipeline == null) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
            props.setProperty("coref.algorithm", "neural");
            pipeline = new StanfordCoreNLP(props);
        }
    }

    public ParsedDialog parseInput(String text) {
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        ParsedDialog parsed = new ParsedDialog(text);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // Análisis de sentimiento
            String sentiment = sentence.get(CoreAnnotations.SentencesAnnotation.class).toString();
            parsed.setSentiment(sentiment);

            // Entidades nombradas
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if (!"O".equals(ne)) {
                    parsed.addNamedEntity(token.word(), ne);
                }
            }

            // Dependencias sintácticas
            parsed.setDependencies((edu.stanford.nlp.semgraph.SemanticGraph) sentence.get(CoreAnnotations.DependentsAnnotation.class));

            // Intención detectada
            parsed.setIntent(detectIntent(sentence.toString(), parsed.getNamedEntities()));
        }

        return parsed;
    }

    private String detectIntent(String sentence, Map<String, String> namedEntities) {
        sentence = sentence.toLowerCase();

        // Detección de intenciones basada en palabras clave y entidades
        if (sentence.contains("misión") || sentence.contains("tarea") || sentence.contains("quest")) {
            return "REQUEST_MISSION";
        }

        if (sentence.contains("hola") || sentence.contains("saludos") || sentence.contains("buenos días")) {
            return "GREETING";
        }

        if (sentence.contains("adiós") || sentence.contains("hasta luego") || sentence.contains("nos vemos")) {
            return "FAREWELL";
        }

        if (namedEntities.containsKey("MONEY") || sentence.contains("dinero") || sentence.contains("oro")) {
            return "MONEY_TALK";
        }

        return "CHAT";
    }
}