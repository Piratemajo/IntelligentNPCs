package es.srjavierdev.intelligentNPCs.reputation;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ReputationSystem {
    private  Map<UUID, Integer> globalReputations;
    private  Map<UUID, Map<UUID, Integer>> npcReputations;
    private  Map<Integer, String> reputationTitles;

    public ReputationSystem() {
        this.globalReputations = new HashMap<>();
        this.npcReputations = new HashMap<>();
        this.reputationTitles = new HashMap<>();

        initializeReputationTitles();
    }

    public ReputationSystem( UUID uniqueId, UUID npcId) {
    }

    private void initializeReputationTitles() {
        reputationTitles.put(-100, "Enemigo Despiadado");
        reputationTitles.put(-50, "Enemigo");
        reputationTitles.put(-20, "Desconfiable");
        reputationTitles.put(0, "Neutral");
        reputationTitles.put(20, "Conocido");
        reputationTitles.put(50, "Aliado");
        reputationTitles.put(80, "Héroe");
        reputationTitles.put(100, "Leyenda");
    }

    public void adjustReputation(@NotNull UUID player, UUID npcId, int amount) {
        UUID playerId = player;

        // Ajustar reputación global
        int currentGlobal = globalReputations.getOrDefault(playerId, 0);
        globalReputations.put(playerId, currentGlobal + amount);

        // Ajustar reputación específica con NPC
        Map<UUID, Integer> playerNPCRep = npcReputations.computeIfAbsent(playerId, k -> new HashMap<>());
        int currentNPCRep = playerNPCRep.getOrDefault(npcId, 0);
        playerNPCRep.put(npcId, currentNPCRep + amount);

        // Notificar al jugador si el cambio es significativo
        if (Math.abs(amount) >= 10) {
            String title = getReputationTitle(currentNPCRep + amount);
        }
    }

    public int getGlobalReputation(Player player) {
        return globalReputations.getOrDefault(player.getUniqueId(), 0);
    }

    public int getNPCReputation(Player player, UUID npcId) {
        return npcReputations.getOrDefault(player.getUniqueId(), new HashMap<>())
                .getOrDefault(npcId, 0);
    }

    public String getReputationTitle(int reputationValue) {
        int closestKey = 0;
        int minDiff = Integer.MAX_VALUE;

        for (Integer key : reputationTitles.keySet()) {
            int diff = Math.abs(key - reputationValue);
            if (diff < minDiff) {
                minDiff = diff;
                closestKey = key;
            }
        }

        return reputationTitles.get(closestKey);
    }

    public void saveData(IntelligentNPCs plugin) {
        File file = new File(plugin.getDataFolder(), "data/reputations.yml");
        FileConfiguration config = new YamlConfiguration();

        // Guardar reputaciones globales
        for (Map.Entry<UUID, Integer> entry : globalReputations.entrySet()) {
            config.set("global." + entry.getKey().toString(), entry.getValue());
        }

        // Guardar reputaciones específicas de NPCs
        for (Map.Entry<UUID, Map<UUID, Integer>> playerEntry : npcReputations.entrySet()) {
            for (Map.Entry<UUID, Integer> npcEntry : playerEntry.getValue().entrySet()) {
                String path = "npc." + playerEntry.getKey() + "." + npcEntry.getKey();
                config.set(path, npcEntry.getValue());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error al guardar reputaciones: " + e.getMessage());
        }
    }

    public void loadData(IntelligentNPCs plugin) {
        File file = new File(plugin.getDataFolder(), "data/reputations.yml");
        if (!file.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Cargar reputaciones globales
        if (config.contains("global")) {
            for (String key : config.getConfigurationSection("global").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(key);
                    int reputation = config.getInt("global." + key);
                    globalReputations.put(playerId, reputation);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID inválido en reputaciones: " + key);
                }
            }
        }

        // Cargar reputaciones específicas de NPCs
        if (config.contains("npc")) {
            for (String playerKey : config.getConfigurationSection("npc").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(playerKey);
                    Map<UUID, Integer> npcReps = new HashMap<>();

                    for (String npcKey : config.getConfigurationSection("npc." + playerKey).getKeys(false)) {
                        try {
                            UUID npcId = UUID.fromString(npcKey);
                            int reputation = config.getInt("npc." + playerKey + "." + npcKey);
                            npcReps.put(npcId, reputation);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("UUID de NPC inválido: " + npcKey);
                        }
                    }

                    npcReputations.put(playerId, npcReps);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("UUID de jugador inválido: " + playerKey);
                }
            }
        }
    }

    /*Recoger la reputacion del red neuronal y del jugador*/
    public int getReputation(@NotNull UUID uniqueId, UUID npcId) {
        Map<UUID, Integer> playerNPCRep = npcReputations.get(uniqueId);
        if (playerNPCRep != null) {
            return playerNPCRep.getOrDefault(npcId, 0);
        }
        return 0;
    }
}