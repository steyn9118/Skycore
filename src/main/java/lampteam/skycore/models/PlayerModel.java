package lampteam.skycore.models;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerModel {

    private Player player;
    private Arena currentArena;

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
