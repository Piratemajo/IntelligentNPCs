package es.srjavierdev.intelligentNPCs;


import es.srjavierdev.intelligentNPCs.commands.NPCCommand;
import es.srjavierdev.intelligentNPCs.economy.EconomyManager;
import es.srjavierdev.intelligentNPCs.listeners.NPCListener;
import es.srjavierdev.intelligentNPCs.npc.NPCManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class IntelligentNPCs extends JavaPlugin {

    private static IntelligentNPCs instance;
    private NPCManager npcManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        instance = this;

        // Crear archivos de configuración si no existen
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();
        createAdditionalConfigs();

        // Inicializar economía
        this.economyManager = new EconomyManager(this);
        if (!economyManager.isEnabled()) {
            getLogger().log(Level.WARNING, "Vault no encontrado. Las recompensas económicas estarán desactivadas.");
        }

        // Verificar dependencias
        if (!checkDependencies()) {
            getLogger().log(Level.SEVERE, "Dependencias faltantes. Desactivando plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Inicializar administrador de NPCs
        this.npcManager = new NPCManager(this, economyManager);

        // Registrar comandos
        getCommand("intelligentnpc").setExecutor(new NPCCommand(this));

        // Registrar listeners
        getServer().getPluginManager().registerEvents(new NPCListener(this), this);

        // Cargar datos
        loadData();

        // Mensaje de activación
        getLogger().log(Level.INFO, "IntelligentNPCs v2.0 ha sido activado correctamente!");
        getLogger().log(Level.INFO, "NPCs cargados: " + npcManager.getLoadedNPCsCount());
    }

    @Override
    public void onDisable() {
        // Guardar datos
        try {
            saveData();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error al guardar los datos de NPCs: " + e.getMessage());
            e.printStackTrace();
        }

        // Cerrar recursos
        npcManager.shutdown();

        getLogger().log(Level.INFO, "IntelligentNPCs ha sido desactivado. Datos guardados.");
    }

    private boolean checkDependencies() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().log(Level.SEVERE, "Citizens no encontrado! Este plugin es requerido.");
            return false;
        }
        return true;
    }

    private void createAdditionalConfigs() {
        // Crear archivos YAML adicionales si no existen
        String[] configFiles = {
                "missions/config.yml",
                "personalities/default.yml",
                "dialogues/responses.yml",
                "data/npcs.yml",
                "data/reputations.yml"
        };

        for (String filePath : configFiles) {
            File file = new File(getDataFolder(), filePath);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    // Inicializar con contenido básico si es necesario
                    if (filePath.equals("personalities/default.yml")) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("personalities.warrior.description", "NPC belicoso y directo");
                        config.set("personalities.merchant.description", "NPC interesado en el comercio");
                        config.save(file);
                    }
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "No se pudo crear el archivo " + filePath + ": " + e.getMessage());
                }
            }
        }
    }

    private void loadData() {
        long startTime = System.currentTimeMillis();

        npcManager.loadData();
        getLogger().log(Level.INFO, "Datos cargados en " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void saveData() throws IOException {
        long startTime = System.currentTimeMillis();

        npcManager.saveData();
        getLogger().log(Level.INFO, "Datos guardados en " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void reloadPlugin() {
        reloadConfig();
        npcManager.loadData();
        getLogger().log(Level.INFO, "Plugin recargado correctamente");
    }

    // Métodos de acceso estático
    public static IntelligentNPCs getInstance() {
        return instance;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}