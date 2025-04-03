package es.srjavierdev.intelligentNPCs.missions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class Mission {
    private final MissionType type;
    private final String objective;
    private final int difficulty;
    private final List<ItemStack> rewards;
    private final double moneyReward;
    private boolean completed;

    public Mission(MissionType type, String objective, int difficulty,
                   List<ItemStack> rewards, double moneyReward) {
        this.type = type;
        this.objective = objective;
        this.difficulty = difficulty;
        this.rewards = rewards;
        this.moneyReward = moneyReward;
        this.completed = false;
    }

    // Getters y setters
    public MissionType getType() { return type; }
    public String getObjective() { return objective; }
    public int getDifficulty() { return difficulty; }
    public List<ItemStack> getRewards() { return rewards; }
    public double getMoneyReward() { return moneyReward; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getDifficultyStars() {
        return "★".repeat(difficulty) + "☆".repeat(5 - difficulty);
    }

    public void complete(Player player) {
        player.sendMessage("Has completado la mision");
    }

    public void offer(Player player) {

    }
}