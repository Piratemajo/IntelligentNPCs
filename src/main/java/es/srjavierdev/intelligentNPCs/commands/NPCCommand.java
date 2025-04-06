package es.srjavierdev.intelligentNPCs.commands;

/*
* Solucion de los codigos he creado nueva parte de exepciones
*
*/

import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.ai.NeuralNetwork;
import es.srjavierdev.intelligentNPCs.ai.excepciones.NLPInitializationException;
import es.srjavierdev.intelligentNPCs.ai.nlp.NLPModel;
import es.srjavierdev.intelligentNPCs.npc.EnhancedNeuralNPC;
import es.srjavierdev.intelligentNPCs.npc.NPCManager;
import es.srjavierdev.intelligentNPCs.npc.excepciones.NPCException;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class NPCCommand implements CommandExecutor {
    private final IntelligentNPCs plugin;
    private final NPCManager npcManager;
   // private  NeuralNetwork network;

    public NPCCommand(IntelligentNPCs plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                return handleCreateCommand(player, args);
            case "delete":
                return handleDeleteCommand(player);
            case "talk":
                return handleTalkCommand(player);
            case "list":
                return handleListCommand(player);
            case "reload":
                return handleReloadCommand(player);
            default:
                sendUsage(player);
                return true;
        }
    }

    // Nuevas Partes 1.6 - Modelos
    private boolean handleCreateCommand(Player player, String[] args) {
        // Verificación de permisos
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para crear NPCs.");
            return true;
        }

        // Validación de argumentos
        if (args.length < 3) {
            player.sendMessage("§cUso: /intelligentnpc create <nombre> <personalidad>");
            player.sendMessage("§6Personalidades disponibles: " + String.join(", ", getAvailablePersonalities()));
            return false;
        }

        String name = args[1];
        String personality = args[2].toLowerCase();

        // Validar personalidad
        if (!isValidPersonality(personality)) {
            player.sendMessage("§cPersonalidad no válida. Opciones: " + String.join(", ", getAvailablePersonalities()));
            return false;
        }

        // Crear NPC con inicialización NLP
        EnhancedNeuralNPC npc = npcManager.createNPC(player, name, personality, player.getLocation());

        // Inicializar modelo NLP (esto podría ser asíncrono para no bloquear el hilo principal)
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                NLPModel model = new NLPModel();
                model.initialize(personality); // Carga el modelo específico para esta personalidad
                npc.setNLPModel(model);

                player.sendMessage("§aModelo NLP cargado para personalidad: §e" + personality);
            } catch (NLPInitializationException e) {
                player.sendMessage("§cError cargando modelo NLP: " + e.getMessage());
                plugin.getLogger().log(Level.SEVERE, "Error inicializando NLP para NPC " + name, e);
            }
        });

        player.sendMessage("§aNPC §e" + name + " §acreado con personalidad §6" + personality);
        npcManager.saveData();

        return true;
    }

    // Métodos auxiliares
    private List<String> getAvailablePersonalities() {
        return Arrays.asList("warrior", "merchant", "scholar", "neutral", "friendly", "aggressive");
    }

    private boolean isValidPersonality(String personality) {
        return getAvailablePersonalities().contains(personality.toLowerCase());
    }

    private boolean handleDeleteCommand(Player player) {
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para eliminar NPCs.");
            return true;
        }

        NPC npc = getTargetNPC(player);
        if (npc == null) {
            player.sendMessage("§cNo estás mirando a un NPC válido.");
            return true;
        }

        npcManager.deleteNPC(npc.getId());
        player.sendMessage("§aNPC eliminado correctamente.");
        return false;
    }
    // Por revisar.
    private boolean handleTalkCommand(Player player) {
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para hablar con NPCs.");
            return true;
        }

        NPC npc = getTargetNPC(player);
        if (npc == null) {
            player.sendMessage("§cNo estás mirando a un NPC válido.");
            return true;
        }
        npcManager.processPlayerResponse(player,"");
        Optional<String> message = npcManager.getNPCMessage(npc.getId());
        message.ifPresentOrElse(
                msg -> player.sendMessage("§a" + msg),
                () -> player.sendMessage("§cEl NPC no tiene un mensaje configurado.")
        );
        return true;
    }
    // Listado de todos los npcs creados con su naturaleza y nlp
    private boolean handleListCommand(Player player) {
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para listar NPCs.");
            npcManager.listNPCs();
            return true;
        }
        return false ;
    }

    private boolean handleReloadCommand(Player player) {
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para recargar el plugin.");
            return false;
        }

        plugin.reloadConfig();
        npcManager.loadData();
        player.sendMessage("§aConfiguración y datos de NPCs recargados.");
        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage("§6=== IntelligentNPCs Comandos ===");
        player.sendMessage("§e/intelligentnpc create <nombre> <personalidad> §7- Crea un nuevo NPC");
        player.sendMessage("§e/intelligentnpc delete §7- Elimina el NPC objetivo");
        player.sendMessage("§e/intelligentnpc talk §7- Habla con el NPC objetivo");
        player.sendMessage("§e/intelligentnpc list §7- Lista todos los NPCs");
        player.sendMessage("§e/intelligentnpc reload §7- Recarga la configuración");
    }

    private NPC getTargetNPC(Player player) {
        return npcManager.getNPCInSight(player);
    }
}