package es.srjavierdev.intelligentNPCs.economy;



import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyManager {
    private final JavaPlugin plugin;
    private Economy economy;
    private boolean enabled;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = setupEconomy();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void giveReward(Player player, double amount) {
        if (enabled) {
            economy.depositPlayer(player, amount);
            player.sendMessage("§aHas recibido §e" + economy.format(amount) + "§a!");
        }
    }

    public void takeMoney(Player player, double amount) {
        if (enabled) {
            economy.withdrawPlayer(player, amount);
        }
    }

    public boolean hasMoney(Player player, double amount) {
        return !enabled || economy.has(player, amount);
    }

    public double getBalance(Player player) {
        return enabled ? economy.getBalance(player) : 0;
    }

    public String format(double amount) {
        return enabled ? economy.format(amount) : String.valueOf(amount);
    }
}