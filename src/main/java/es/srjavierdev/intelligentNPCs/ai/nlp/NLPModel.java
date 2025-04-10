package es.srjavierdev.intelligentNPCs.ai.nlp;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import es.srjavierdev.intelligentNPCs.ai.excepciones.NLPInitializationException;

import java.util.Properties;

public class NLPModel {
    private StanfordCoreNLP pipeline;
    private String personality;
    private boolean initialized = false;

    private static boolean NLP_AVAILABLE = false;

    static {
        try {
            Class.forName("edu.stanford.nlp.pipeline.StanfordCoreNLP");
            NLP_AVAILABLE = true;
        } catch (ClassNotFoundException e) {
            NLP_AVAILABLE = false;
        }
    }
    public void initialize(String personality) throws NLPInitializationException {
        this.personality = personality;

        try {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
            props.setProperty("coref.algorithm", "neural");

            // Configuración específica por personalidad
            configureForPersonality(props, personality);

            this.pipeline = new StanfordCoreNLP(props);
            this.initialized = true;

        } catch (Exception e) {
            throw new NLPInitializationException("Failed to initialize NLP model for personality: " + personality, e);
        }
    }

    private void configureForPersonality(Properties props, String personality) {
        switch (personality) {
            case "warrior":
                props.setProperty("ner.applyNumericClassifiers", "false");
                break;
            case "scholar":
                props.setProperty("ner.applyNumericClassifiers", "true");
                break;
            // Configuraciones adicionales...
        }
    }

    public String processInput(String input) {
        if (!initialized) {
            throw new IllegalStateException("NLP model not initialized");
        }
        // Por hacer
        return "Respuesta generada";
    }

    public static boolean isNlpAvailable() {
        return NLP_AVAILABLE;
    }
}