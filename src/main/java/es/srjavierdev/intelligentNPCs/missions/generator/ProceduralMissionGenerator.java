package es.srjavierdev.intelligentNPCs.missions.generator;

import es.srjavierdev.intelligentNPCs.memory.PlayerMemory;
import es.srjavierdev.intelligentNPCs.missions.Mission;
import es.srjavierdev.intelligentNPCs.missions.MissionType;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ProceduralMissionGenerator {
    private static final Random random = new Random();
    private static final Map<MissionType, List<Material>> COLLECT_ITEMS = new EnumMap<>(MissionType.class);
    private static final Map<MissionType, List<EntityType>> HUNT_ENTITIES = new EnumMap<>(MissionType.class);
    private static final Map<MissionType, List<String>> DELIVERY_LOCATIONS = new EnumMap<>(MissionType.class);
    private static final Map<MissionType, List<String>> EXPLORATION_AREAS = new EnumMap<>(MissionType.class);
    private static final Map<MissionType, List<String>> CONSTRUCTION_TARGETS = new EnumMap<>(MissionType.class);
    private static final Map<MissionType, List<String>> BOSS_TYPES = new EnumMap<>(MissionType.class);

    static {
        // Configuración de materiales para misiones de recolección
        COLLECT_ITEMS.put(MissionType.
                COLLECTION, Arrays.asList(
                Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND,
                Material.EMERALD, Material.REDSTONE, Material.LAPIS_LAZULI,
                Material.COAL, Material.NETHERITE_SCRAP, Material.QUARTZ
        ));

        // Configuración de entidades para misiones de caza
        HUNT_ENTITIES.put(MissionType.HUNTING, Arrays.asList(
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
                EntityType.CREEPER, EntityType.ENDERMAN, EntityType.WITCH,
                EntityType.BLAZE, EntityType.GHAST
        ));

        // Configuración de ubicaciones para misiones de entrega
        DELIVERY_LOCATIONS.put(MissionType.DELIVERY, Arrays.asList(
                "el pueblo cercano", "las minas abandonadas", "el templo del bosque",
                "la torre del hechicero", "el puerto", "la aldea de los pescadores"
        ));

        // Configuración de áreas para misiones de exploración
        EXPLORATION_AREAS.put(MissionType.EXPLORATION, Arrays.asList(
                "las ruinas antiguas", "la cueva de cristal", "el pico de la montaña",
                "el bosque encantado", "el lago subterráneo", "el cráter volcánico"
        ));

        // Configuración de objetivos para misiones de construcción
        CONSTRUCTION_TARGETS.put(MissionType.CONSTRUCTION, Arrays.asList(
                "un monumento", "una casa segura", "un altar",
                "una granja automática", "un puente", "una torre de vigilancia"
        ));

        // Configuración de jefes para misiones de boss
        BOSS_TYPES.put(MissionType.BOSS, Arrays.asList(
                "el Dragón Ender", "el Wither", "el Guardián Ancestral",
                "la Hidra del Nether", "el Golem de Piedra"
        ));
    }

    public Mission generateMission(Player player, PlayerMemory memory, int reputation, Location npcLocation) {
        MissionType type = determineMissionType(player, memory, reputation);
        int difficulty = calculateDifficulty(player, reputation);

        switch (type) {
            case COLLECTION:
                return generateCollectionMission(difficulty, npcLocation);
            case HUNTING:
                return generateHuntingMission(difficulty);
            case DELIVERY:
                return generateDeliveryMission(difficulty, npcLocation);
            case EXPLORATION:
                return generateExplorationMission(difficulty, npcLocation);
            case CONSTRUCTION:
                return generateConstructionMission(difficulty);
            case ESCORT:
                return generateEscortMission(difficulty, npcLocation);
            case BOSS:
                return generateBossMission(difficulty);
            default:
                return generateCollectionMission(difficulty, npcLocation);
        }
    }

    private int calculateDifficulty(Player player, int reputation) {
        int playerLevel = player.getLevel(); // Nivel del jugador
        int baseDifficulty = Math.max(1, (int) Math.floor((playerLevel + reputation) / 10.0)); // Dificultad base
        return Math.min(baseDifficulty, 10); // Dificultad máxima de 10
    }

    private MissionType determineMissionType(Player player, PlayerMemory memory, int reputation) {
        // Lógica para determinar el tipo de misión basado en la memoria del jugador y la reputación
        List<MissionType> availableTypes = new ArrayList<>(Arrays.asList(MissionType.values()));
        Collections.shuffle(availableTypes); // Mezclar tipos de misión para aleatoriedad

        for (MissionType type : availableTypes) {
            if (memory.canAcceptMission(type)) {
                return type; // Retornar el primer tipo de misión que el jugador puede aceptar
            }
        }

        return MissionType.COLLECTION; // Por defecto, si no se encuentra ninguno
    }

    private Mission generateDeliveryMission(int difficulty, Location npcLocation) {
        String destination = DELIVERY_LOCATIONS.get(MissionType.DELIVERY)
                .get(random.nextInt(DELIVERY_LOCATIONS.get(MissionType.DELIVERY).size()));

        String objective = "Entrega el paquete a " + destination;

        // Recompensas por entrega
        List<ItemStack> rewards = new ArrayList<>();
        if (difficulty >= 3) {
            rewards.add(new ItemStack(Material.GOLD_INGOT, difficulty * 2));
        }

        double moneyReward = difficulty * 4.0 + random.nextDouble() * 8.0;

        return new Mission(MissionType.DELIVERY, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateHuntingMission(int difficulty) {
        EntityType entityType = HUNT_ENTITIES.get(MissionType.HUNTING)
                .get(random.nextInt(HUNT_ENTITIES.get(MissionType.HUNTING).size()));

        String objective = "Caza " + entityType.name().toLowerCase().replace("_", " ");

        // Recompensas por caza
        List<ItemStack> rewards = new ArrayList<>();
        if (difficulty >= 4) {
            rewards.add(new ItemStack(Material.BOW, 1));
            rewards.add(new ItemStack(Material.ARROW, difficulty * 5));
        }

        double moneyReward = difficulty * 6.0 + random.nextDouble() * 10.0;

        return new Mission(MissionType.HUNTING, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateCollectionMission(int difficulty, Location npcLocation) {
        Material item = COLLECT_ITEMS.get(MissionType.COLLECTION)
                .get(random.nextInt(COLLECT_ITEMS.get(MissionType.COLLECTION).size()));

        String objective = "Recolecta " + difficulty * 5 + " " + item.name().toLowerCase().replace("_", " ");

        // Recompensas por recolección
        List<ItemStack> rewards = new ArrayList<>();
        if (difficulty >= 2) {
            rewards.add(new ItemStack(item, difficulty * 3));
        }

        double moneyReward = difficulty * 3.0 + random.nextDouble() * 5.0;

        return new Mission(MissionType.COLLECTION, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateEscortMission(int difficulty, Location npcLocation) {
        String destination = DELIVERY_LOCATIONS.get(MissionType.DELIVERY)
                .get(random.nextInt(DELIVERY_LOCATIONS.get(MissionType.DELIVERY).size()));

        String objective = "Acompaña al mercader hasta " + destination;

        // Recompensas por escolta
        List<ItemStack> rewards = new ArrayList<>();
        if (difficulty >= 5) {
            rewards.add(new ItemStack(Material.EMERALD, difficulty * 2));
        }

        double moneyReward = difficulty * 8.0 + random.nextDouble() * 12.0;

        return new Mission(MissionType.ESCORT, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateBossMission(int difficulty) {
        String bossName = BOSS_TYPES.get(MissionType.BOSS)
                .get(random.nextInt(BOSS_TYPES.get(MissionType.BOSS).size()));

        String objective = "Derrota a " + bossName;

        // Recompensas especiales por jefe
        List<ItemStack> rewards = new ArrayList<>();
        rewards.add(getBossReward(bossName));

        double moneyReward = difficulty * 15.0 + random.nextDouble() * 20.0;

        return new Mission(MissionType.BOSS, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateConstructionMission(int difficulty) {
        String target = CONSTRUCTION_TARGETS.get(MissionType.CONSTRUCTION)
                .get(random.nextInt(CONSTRUCTION_TARGETS.get(MissionType.CONSTRUCTION).size()));

        String objective = "Construye " + target;

        // Materiales de ayuda para la construcción
        List<ItemStack> rewards = new ArrayList<>();
        if (difficulty >= 4) {
            rewards.add(new ItemStack(Material.IRON_PICKAXE, 1));
            rewards.add(new ItemStack(Material.STONE, 64));
        }

        double moneyReward = difficulty * 5.0 + random.nextDouble() * 10.0;

        return new Mission(MissionType.CONSTRUCTION, objective, difficulty, rewards, moneyReward);
    }

    private Mission generateExplorationMission(int difficulty, Location npcLocation) {
        String area = EXPLORATION_AREAS.get(MissionType.EXPLORATION)
                .get(random.nextInt(EXPLORATION_AREAS.get(MissionType.EXPLORATION).size()));

        String objective = "Explora " + area + " y trae pruebas de tu hazaña";

        // Recompensas por exploración
        List<ItemStack> rewards = new ArrayList<>();
        rewards.add(new ItemStack(Material.MAP, 1));

        if (difficulty >= 6) {
            rewards.add(new ItemStack(Material.COMPASS, 1));
        }

        double moneyReward = difficulty * 7.0 + random.nextDouble() * 15.0;

        return new Mission(MissionType.EXPLORATION, objective, difficulty, rewards, moneyReward);
    }

    private ItemStack getBossReward(String bossName) {
        switch (bossName) {
            case "el Dragón Ender":
                return new ItemStack(Material.DRAGON_EGG, 1);
            case "el Wither":
                return new ItemStack(Material.NETHER_STAR, 1);
            case "el Guardián Ancestral":
                return new ItemStack(Material.PRISMARINE_CRYSTALS, 8);
            case "la Hidra del Nether":
                return new ItemStack(Material.BLAZE_ROD, 5);
            default:
                return new ItemStack(Material.DIAMOND, 3);
        }
    }

}