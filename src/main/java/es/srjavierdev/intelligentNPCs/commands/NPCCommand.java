package es.srjavierdev.intelligentNPCs.commands;

/*
* Falta la conexión con la ia atraves  de los NeuralNetwork (contiene varios errors) - Codigo de error INPC-001
*
*/

import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.ai.NeuralNetwork;
import es.srjavierdev.intelligentNPCs.npc.EnhancedNeuralNPC;
import es.srjavierdev.intelligentNPCs.npc.NPCManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Optional;

public class NPCCommand implements CommandExecutor {
    private final IntelligentNPCs plugin;
    private final NPCManager npcManager;
   // private  NeuralNetwork network;

    public NPCCommand(IntelligentNPCs plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
       // this.network = new NeuralNetwork(); (INPC-001)
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

    private boolean handleCreateCommand(Player player, String[] args)  {
        if (!player.hasPermission("intelligentnpcs.admin")) {
            player.sendMessage("§cNo tienes permiso para crear NPCs.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("§cUso: /intelligentnpc create <nombre> <personalidad>");
            return false;
        }

        String name = args[1];
        // Carga el modelo de la ia
        String personality = args[2];
        //network.initialize(personality); Inicializar el npc (error INPC-001)

        npcManager.createNPC(player, name, personality, player.getLocation());
        npcManager.saveData();

        return true;
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