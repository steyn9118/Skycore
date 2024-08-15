package lampteam.skycore.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class PlayerModel {

    private final Player player;
    private Arena currentArena;
    private int aliveTime = -1;
    private Location lastSafeLocation;
    private int lastViewedPlayerID;
    private int wavesSurvived = 0;

    public int getWavesSurvived() {
        return wavesSurvived;
    }

    public void survivedWave() {
        wavesSurvived += 1;
    }

    public void setCurrentArena(Arena currentArena) {
        this.currentArena = currentArena;
    }

    public Location getLastSafeLocation() {
        return lastSafeLocation;
    }

    public void setLastSafeLocation(Location lastSafeLocation) {
        this.lastSafeLocation = lastSafeLocation;
    }

    public int getLastViewedPlayerID() {
        return lastViewedPlayerID;
    }

    public void setLastViewedPlayerID(int lastViewedPlayerID) {
        this.lastViewedPlayerID = lastViewedPlayerID;
    }

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

    public Arena getCurrentArena() {
        return currentArena;
    }

    public Player getPlayer() {
        return player;
    }

    public void resetData(){
        currentArena = null;
        lastSafeLocation = null;
        lastViewedPlayerID = -1;
        wavesSurvived = 0;
    }
}
