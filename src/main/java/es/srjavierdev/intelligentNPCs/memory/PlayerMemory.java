package es.srjavierdev.intelligentNPCs.memory;


import es.srjavierdev.intelligentNPCs.missions.MissionType;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerMemory {
    private final List<Interaction> interactions;
    private final Map<MissionType, Integer> completedMissions;
    private final Map<String, Integer> keywords;
    private int totalMissionsCompleted;
    private long lastInteractionTime;

    public PlayerMemory() {
        this.interactions = new ArrayList<>();
        this.completedMissions = new EnumMap<>(MissionType.class);
        this.keywords = new HashMap<>();
        this.totalMissionsCompleted = 0;
        this.lastInteractionTime = System.currentTimeMillis();
    }

    public void addInteraction(String input, String response) {
        interactions.add(new Interaction(input, response));
        lastInteractionTime = System.currentTimeMillis();

        // Analizar palabras clave
        analyzeKeywords(input);
    }

    public void addCompletedMission(MissionType type) {
        completedMissions.put(type, completedMissions.getOrDefault(type, 0) + 1);
        totalMissionsCompleted++;
    }

    public ArrayList<Interaction> getInteractionHistory() {
        return new ArrayList<>(interactions);
    }

    public List<Interaction> getRecentInteractions(int count) {
        return interactions.stream()
                .skip(Math.max(0, interactions.size() - count))
                .collect(Collectors.toList());
    }

    public Map<MissionType, Integer> getMissionCountByType() {
        return new EnumMap<>(completedMissions);
    }

    public int getTotalMissionsCompleted() {
        return totalMissionsCompleted;
    }

    public int getMissionsCompletedOfType(MissionType type) {
        return completedMissions.getOrDefault(type, 0);
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }

    public boolean hasKeyword(String keyword) {
        return keywords.containsKey(keyword.toLowerCase());
    }

    public int getKeywordFrequency(String keyword) {
        return keywords.getOrDefault(keyword.toLowerCase(), 0);
    }

    public Set<String> getTopKeywords(int count) {
        return keywords.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void analyzeKeywords(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 3 && !isCommonWord(word)) {
                keywords.put(word, keywords.getOrDefault(word, 0) + 1);
            }
        }
    }

    private boolean isCommonWord(String word) {
        List<String> commonWords = Arrays.asList("que", "como", "donde", "cuando", "por", "para", "con", "los", "las", "una", "uno");
        return commonWords.contains(word);
    }

    public void saveData(UUID playerId) {
        // Implementar guardado de datos
    }

    public void loadData(UUID playerId) {
        // Implementar carga de datos
    }

    public boolean canAcceptMission(MissionType type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canAcceptMission'");
    }
}