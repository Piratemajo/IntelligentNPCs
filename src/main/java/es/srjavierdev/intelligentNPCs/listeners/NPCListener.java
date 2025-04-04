package es.srjavierdev.intelligentNPCs.listeners;


import es.srjavierdev.intelligentNPCs.IntelligentNPCs;
import es.srjavierdev.intelligentNPCs.npc.NPCManager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCListener implements Listener {
    private final NPCManager npcManager;

    public NPCListener(IntelligentNPCs plugin) {
        this.npcManager = plugin.getNPCManager();
    }

    @EventHandler
    public void onNPCInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof NPC) {
            NPC npc = (NPC) event.getRightClicked();
            npcManager.handlePlayerInteraction(event.getPlayer(), npc);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Verificar si el jugador está en conversación con un NPC
        if (npcManager.isInConversation(player)) {
            event.setCancelled(true); // Cancelar el mensaje público

            // Procesar el mensaje con el NPC
            npcManager.processPlayerResponse(player, event.getMessage());
        }
    }
    
}