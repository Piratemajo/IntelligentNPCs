package es.srjavierdev.intelligentNPCs.npc;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.economy.EconomyManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NPCManager {
    private final IntelligentNPCs plugin;
    private final EconomyManager economyManager;
    private final Map<Integer, EnhancedNeuralNPC> npcs;

    public NPCManager(IntelligentNPCs plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.npcs = new HashMap<>();
    }

    public EnhancedNeuralNPC createNPC(Player creator, String name, String personality, Location location) {
        NPC citizensNPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        citizensNPC.spawn(location);

        EnhancedNeuralNPC neuralNPC = new EnhancedNeuralNPC(citizensNPC.getUniqueId(), personality, null, null);
        npcs.put(citizensNPC.getId(), neuralNPC);

        creator.sendMessage("§aNPC inteligente creado con éxito!");
        return neuralNPC;
    }

    public void deleteNPC(int npcId) {
        npcs.remove(npcId);
        NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc != null) {
            npc.destroy();
        }
    }

    public EnhancedNeuralNPC getNeuralNPC(int npcId) {
        return npcs.get(npcId);
    }

    public void handlePlayerInteraction(Player player, NPC npc) {
        EnhancedNeuralNPC neuralNPC = npcs.get(npc.getId());
        if (neuralNPC != null) {
            neuralNPC.startConversation(player);
        }
    }

    public int getLoadedNPCsCount() {
        return npcs.size();
    }

    public Collection<EnhancedNeuralNPC> getAllNPCs() {
        return npcs.values();
    }

    public boolean isInConversation(Player player) {
        return npcs.values().stream()
                .anyMatch(npc -> npc.isInConversationWith(player));
    }

    public void processPlayerResponse(Player player, String message) {
        npcs.values().stream()
                .filter(npc -> npc.isInConversationWith(player))
                .findFirst()
                .ifPresent(npc -> npc.processPlayerResponse(player, message));
    }

    public void saveData() throws IOException {
        saveNPCData();
        saveReputationData();
        plugin.getLogger().info("Datos de NPCs guardados correctamente.");
    }
    /*
    * Faltaria los datos del npcs y su reputacion
    * */
    private void saveReputationData() {

    }

    private void saveNPCData() throws IOException {
        for (EnhancedNeuralNPC npc : npcs.values()) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("npcs." + npc.getId() + ".name", npc.getName());
            config.set("npcs." + npc.getId() + ".personality", npc.getPersonality());
            config.save(new File(plugin.getDataFolder(), "data/npcs.yml"));
            plugin.getLogger().info("NPC " + npc.getId() + " guardado en el archivo de configuración.");
        }
    }

    public void loadData() {
        loadNPCData();
        loadReputationData();
    }

    private void loadNPCData() {
    }

    private void loadReputationData() {
    }

    public void shutdown() {
        for (EnhancedNeuralNPC npc : npcs.values()) {
            npc.endConversation(null); // Finalizar conversaciones activas
            NPC citizensNPC = CitizensAPI.getNPCRegistry().getById(npc.getCitizensId());
            if (citizensNPC != null) {
                citizensNPC.destroy(); // Destruir el NPC de Citizens
            }
        }
        npcs.clear(); // Limpiar la lista de NPCs
        plugin.getLogger().info("Todos los NPCs han sido destruidos y la memoria ha sido liberada.");
    }


    public Iterable<Object> listNPCs() {
        return (Iterable<Object>) npcs;
    }

    public NPC getNPCInSight(Player player) {
        player.sendMessage("");
        return null;
    }

    public Optional<String> getNPCMessage(int id) {
        EnhancedNeuralNPC neuralNPC = npcs.get(id);
        if (neuralNPC != null) {
            return Optional.of(neuralNPC.getName());
        }
        return Optional.empty();
    }
}