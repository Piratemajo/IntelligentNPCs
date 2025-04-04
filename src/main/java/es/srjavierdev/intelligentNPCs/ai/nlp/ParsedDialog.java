package es.srjavierdev.intelligentNPCs.ai.nlp;


import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.HashMap;
import java.util.Map;

public class ParsedDialog {
    private final Map<String, String> namedEntities;

    public ParsedDialog(String originalText) {
        this.namedEntities = new HashMap<>();

    }


    public void setSentiment(String sentiment) {
    }


    public void setIntent(String intent) {
    }

    public Map<String, String> getNamedEntities() {
        return namedEntities;
    }

    public void addNamedEntity(String word, String entityType) {
        namedEntities.put(word, entityType);
    }


    public void setDependencies(SemanticGraph dependencies) {
    }



}