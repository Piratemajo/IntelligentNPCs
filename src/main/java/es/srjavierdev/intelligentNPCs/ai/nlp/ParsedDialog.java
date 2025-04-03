package es.srjavierdev.intelligentNPCs.ai.nlp;


import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.HashMap;
import java.util.Map;

public class ParsedDialog {
    private  String originalText;
    private String sentiment;
    private String intent;
    private final Map<String, String> namedEntities;
    private SemanticGraph dependencies;

    public ParsedDialog(String originalText) {
        this.originalText = originalText;
        this.namedEntities = new HashMap<>();
    }


    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }


    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, String> getNamedEntities() {
        return namedEntities;
    }

    public void addNamedEntity(String word, String entityType) {
        namedEntities.put(word, entityType);
    }


    public void setDependencies(SemanticGraph dependencies) {
        this.dependencies = dependencies;
    }



}