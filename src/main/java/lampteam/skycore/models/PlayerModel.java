package lampteam.skycore.models;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerModel {

    private Player player;
    private Arena currentArena;
    private int aliveTime = -1;

    public int getAliveTime() {
        return aliveTime;
    }

    public void incrementAliveTime(int increment){
        aliveTime += increment;
    }

    public void resetAliveTime(){
        aliveTime = -1;
    }

    public PlayerModel(Player player){
        this.player = player;
    }

    @Nullable
    public Arena getCurrentArena() {
        return currentArena;
    }

    public Player getPlayer() {
        return player;
    }
}
