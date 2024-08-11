package lampteam.skycore.listeners;

import lampteam.skycore.managers.PlayerModelsManager;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerRelatedEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayerModelsManager.createPlayerModel(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        PlayerModel model = PlayerModelsManager.getModelOfPlayer(event.getPlayer());
        assert model != null;

        Arena arena = model.getCurrentArena();
        if (arena == null) return;

        arena.tryLeavePlayer(model);
        PlayerModelsManager.removePlayerModel(model.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        PlayerModel model = PlayerModelsManager.getModelOfPlayer(event.getPlayer());
        assert model != null;

        Arena arena = model.getCurrentArena();
        if (arena == null) return;

        arena.playerDied(model);
    }

}
