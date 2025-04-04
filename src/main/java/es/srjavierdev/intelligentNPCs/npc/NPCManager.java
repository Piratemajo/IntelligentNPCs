package es.srjavierdev.intelligentNPCs.npc;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.economy.EconomyManager;
import es.srjavierdev.intelligentNPCs.memory.NPCMemory;
import es.srjavierdev.intelligentNPCs.reputation.ReputationSystem;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NPCManager {
    private  IntelligentNPCs plugin;
    private final EconomyManager economyManager;

    private  NPCMemory npcMemory;
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

    public void saveData() {
        // npcMemory.saveAllData(); Por comprobar los errores causados
        saveNPCData();
        saveReputationData();
        plugin.getLogger().info("Datos de NPCs guardados correctamente.");
    }


    private void saveReputationData() {


    }

    // Guardadp de datos del npc en la configuracion
    private void saveNPCData()  {
        for (EnhancedNeuralNPC npc : npcs.values()) {
            FileConfiguration config = new YamlConfiguration();

            config.set("npcs." + npc.getId() + ".name", npc.getName());
            config.set("npcs." + npc.getId() + ".personality", npc.getPersonality());
            try {
                config.save(new File(IntelligentNPCs.getInstance().getDataFolder(), "data/config.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            plugin.getLogger().info("NPC " + npc.getId() + " guardado en el archivo de configuración.");
        }
    }
    // Recarga los datos cargados
    public void loadData() {
        loadNPCData();
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