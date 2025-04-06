package es.srjavierdev.intelligentNPCs.npc;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.ai.nlp.NLPModel;
import es.srjavierdev.intelligentNPCs.memory.PlayerMemory;
import es.srjavierdev.intelligentNPCs.memory.NPCMemory;
import es.srjavierdev.intelligentNPCs.missions.Mission;
import es.srjavierdev.intelligentNPCs.missions.MissionType;
import es.srjavierdev.intelligentNPCs.missions.generator.ProceduralMissionGenerator;
import es.srjavierdev.intelligentNPCs.reputation.ReputationSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnhancedNeuralNPC {
    private final UUID npcId;
    private final String personality;
    private final NPCMemory memory;
    private final ReputationSystem reputationSystem;
    private final ProceduralMissionGenerator missionGenerator;

    // Nuevas Partes 1.6 - Modelos
    private  NLPModel nlpModel;

    private final Map<UUID, Conversation> activeConversations = new ConcurrentHashMap<>();
    private final Map<UUID, Mission> pendingMissions = new ConcurrentHashMap<>();

    public EnhancedNeuralNPC(UUID npcId, String personality, NPCMemory memory,
                             ReputationSystem reputationSystem) {
        this.npcId = npcId;
        this.personality = personality;
        this.memory = memory;
        this.reputationSystem = reputationSystem;
        this.missionGenerator = new ProceduralMissionGenerator();
    }

    public void startConversation(Player player) {
        UUID playerId = player.getUniqueId();

        // Crear nueva conversación o recuperar existente
        Conversation conversation = activeConversations.computeIfAbsent(playerId,
                k -> new Conversation(player, this));

        // Saludo inicial basado en reputación
        int reputation = reputationSystem.getReputation(playerId, npcId);
        String greeting = generateGreeting(reputation);

        player.sendMessage(getName() + ": " + greeting);
        memory.recordInteraction(playerId, "INIT_CONVERSATION", greeting);
    }



    public void offerMission(Player player) {
        int reputation = reputationSystem.getReputation(player.getUniqueId(), npcId);
        PlayerMemory playerMemory = memory.getPlayerMemory(player.getUniqueId());
        Location npcLocation = getLocation();

        Mission mission = missionGenerator.generateMission(player, playerMemory, reputation, npcLocation);
        pendingMissions.put(player.getUniqueId(), mission);

        mission.offer(player);
    }

    public void completeMission(Player player) {
        Mission mission = pendingMissions.get(player.getUniqueId());
        if (mission != null && !mission.isCompleted()) {
            mission.complete(player);
            reputationSystem.adjustReputation(player.getUniqueId(), npcId,
                    calculateReputationReward(mission.getDifficulty()));
            pendingMissions.remove(player.getUniqueId());
        }
    }

    public boolean isInConversationWith(Player player) {
        return activeConversations.containsKey(player.getUniqueId());
    }

    public void endConversation(Player player) {
        activeConversations.remove(player.getUniqueId());
    }

    private String generateGreeting(int reputation) {
        if (reputation > 80) return "¡Hola, viejo amigo! ¿Qué tal estás?";
        if (reputation > 50) return "¡Saludos! ¿En qué puedo ayudarte hoy?";
        if (reputation > 20) return "Hola. ¿Necesitas algo?";
        if (reputation > -20) return "Oh, eres tú... ¿Qué quieres?";
        if (reputation > -50) return "No tengo tiempo para ti...";
        return "Vete de aquí antes de que llame a los guardias.";
    }

    private ParsedMessage analyzeMessage(String message) {
        // Implementar análisis NLP del mensaje
        // (usando Stanford CoreNLP u otra librería)
        return new ParsedMessage(message);
    }

    private String generateResponse(Player player, ParsedMessage parsed) {
        // Lógica compleja para generar respuestas basadas en:
        // - Personalidad del NPC
        // - Historial de interacciones
        // - Reputación del jugador
        // - Intención detectada

        StringBuilder response = new StringBuilder();

        // Ejemplo básico:
        if (parsed.containsKeyword("misión") || parsed.getIntent() == Intent.REQUEST_MISSION) {
            response.append("Tengo una misión para ti. ");
            response.append("#MISSION#");
        } else {
            response.append(generateGenericResponse(parsed));
        }

        return response.toString();
    }

    private char[] generateGenericResponse(ParsedMessage parsed) {
        return null;
    }

    private void handleSpecialActions(Player player, String response) {
        if (response.contains("#MISSION#")) {
            Bukkit.getScheduler().runTask(IntelligentNPCs.getInstance(), () -> {
                offerMission(player);
            });
        }
    }

    private void sendResponse(Player player, String response) {
        String finalResponse = response.replace("#MISSION#", "");
        Bukkit.getScheduler().runTask(IntelligentNPCs.getInstance(), () -> {
            player.sendMessage(getName() + ": " + finalResponse);
        });
    }

    private int calculateReputationReward(int missionDifficulty) {
        return missionDifficulty / 2 + 1;
    }

    public String getName() {
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        return npc != null ? npc.getName() : "NPC";
    }

    public Location getLocation() {
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        return npc != null && npc.isSpawned() ? npc.getEntity().getLocation() : null;
    }

    public void processPlayerResponse(Player player, String message) {
        player.sendMessage("Dime tu respuesta");
        message.contains(message);
    }

    // Clases internas
    private static class Conversation {
        private final Player player;
        private final EnhancedNeuralNPC npc;
        private final List<String> messageHistory = new ArrayList<>();

        public Conversation(Player player, EnhancedNeuralNPC npc) {
            this.player = player;
            this.npc = npc;
        }

        public void addMessage(String message) {
            messageHistory.add(message);
            if (messageHistory.size() > 10) {
                messageHistory.remove(0);
            }
        }
    }

    private static class ParsedMessage {
        private final String original;
        private Intent intent;
        private Sentiment sentiment;
        private Set<String> keywords;

        public ParsedMessage(String original) {
            this.original = original;
            // Inicializar análisis NLP aquí
        }

        // Getters y métodos de análisis
        public Intent getIntent() { return intent; }
        public boolean containsKeyword(String keyword) { return keywords.contains(keyword); }
    }

    private enum Intent {
        GREETING, REQUEST_MISSION, CHAT, FAREWELL, INSULT, COMPLIMENT
    }

    private enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }

    public @Nullable Object getPersonality() {
        return personality;
    }

    public UUID getId() {
        return npcId;
    }

    public int getCitizensId() {
        NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcId);
        return npc != null ? npc.getId() : -1;
    }

    // Nuevas Partes 1.6 - Modelos

    public void setNLPModel(NLPModel model) {
        this.nlpModel = model;
    }

    public String processPlayerInput(Player player, String input) {
        if (nlpModel == null) {
            return "Mi sistema de lenguaje no está listo todavía...";
        }
        return nlpModel.processInput(input);
    }
}