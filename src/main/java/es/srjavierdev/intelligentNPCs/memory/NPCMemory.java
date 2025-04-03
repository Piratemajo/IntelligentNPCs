package es.srjavierdev.intelligentNPCs.memory;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.missions.MissionType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class NPCMemory {
    private final Map<UUID, PlayerMemory> playerMemories;
    private final Map<UUID, Set<UUID>> knownPlayers;
    private final File dataFolder;

    public NPCMemory(IntelligentNPCs plugin) {
        this.playerMemories = new HashMap<>();
        this.knownPlayers = new HashMap<>();
        this.dataFolder = new File(plugin.getDataFolder(), "data/memory");

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PlayerMemory getPlayerMemory(UUID playerId) {
        return playerMemories.computeIfAbsent(playerId, k -> {
            PlayerMemory memory = new PlayerMemory();
            memory.loadData(playerId);
            return memory;
        });
    }

    public void rememberInteraction(UUID playerId, String input, String response) {
        PlayerMemory memory = getPlayerMemory(playerId);
        memory.addInteraction(input, response);

        // Registrar que este NPC conoce al jugador
        knownPlayers.computeIfAbsent(playerId, k -> new HashSet<>()).add(playerId);
    }

    public void recordMissionCompletion(UUID playerId, MissionType type) {
        PlayerMemory memory = getPlayerMemory(playerId);
        memory.addCompletedMission(type);
    }

    public boolean knowsPlayer(UUID npcId, UUID playerId) {
        return knownPlayers.getOrDefault(npcId, Collections.emptySet()).contains(playerId);
    }

    public void saveAllData() {
        playerMemories.forEach((playerId, memory) -> {
            memory.saveData(playerId);
        });

        // Guardar relaciones NPC-Jugador
        File relationsFile = new File(dataFolder, "relations.yml");
        FileConfiguration config = new YamlConfiguration();

        knownPlayers.forEach((npcId, players) -> {
            String playerList = players.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));
            config.set(npcId.toString(), playerList);
        });

        try {
            config.save(relationsFile);
        } catch (IOException e) {
            IntelligentNPCs.getInstance().getLogger().warning("Error guardando relaciones NPC-Jugador: " + e.getMessage());
        }
    }

    public void loadAllData() {
        // Cargar relaciones NPC-Jugador
        File relationsFile = new File(dataFolder, "relations.yml");
        if (relationsFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(relationsFile);

            for (String npcKey : config.getKeys(false)) {
                try {
                    UUID npcId = UUID.fromString(npcKey);
                    String[] playerIds = config.getString(npcKey).split(",");

                    Set<UUID> players = Arrays.stream(playerIds)
                            .map(UUID::fromString)
                            .collect(Collectors.toSet());

                    knownPlayers.put(npcId, players);
                } catch (IllegalArgumentException e) {
                    IntelligentNPCs.getInstance().getLogger().warning("UUID inválido en relaciones: " + npcKey);
                }
            }
        }
    }

    public void savePlayerData(UUID playerId) {
        PlayerMemory memory = playerMemories.get(playerId);
        if (memory != null) {
            memory.saveData(playerId);
        }
    }

    public void saveData(UUID uniqueId) {
    }

    public void loadData(UUID uniqueId) {
    }

    public void recordInteraction(UUID playerId, String message, String response) {
        return;
    }
}