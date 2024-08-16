package lampteam.skycore.listeners;

import lampteam.skycore.managers.ArenasManager;
import lampteam.skycore.managers.PlayerModelsManager;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.LinearDirection;
import lampteam.skycore.models.PlayerModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler
    public void onSpectatorInteract(PlayerInteractEvent event) {
        PlayerModel spectator = PlayerModelsManager.getModelOfPlayer(event.getPlayer());
        if (!spectator.getCurrentArena().getSpectators().contains(spectator)) {
            return;
        }
        switch (event.getPlayer().getActiveItem().getType()) {
            case GREEN_BANNER -> {
                spectator.getCurrentArena().spectatePlayer(spectator, LinearDirection.FORWARDS);
            }
            case RED_BANNER -> {
                spectator.getCurrentArena().spectatePlayer(spectator, LinearDirection.BACKWARDS);
            }
            case IRON_DOOR -> {
                ArenasManager.leaveArena(spectator);
            }
        }
    }

    @EventHandler
    public void onSpectatorDropItem(PlayerDropItemEvent event) {
        PlayerModel spectator = PlayerModelsManager.getModelOfPlayer(event.getPlayer());
        if (!spectator.getCurrentArena().getSpectators().contains(spectator)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onSpectatorClickItem(InventoryClickEvent event) {
        PlayerModel spectator = PlayerModelsManager.getModelOfPlayer((Player) event.getWhoClicked());
        if (!spectator.getCurrentArena().getSpectators().contains(spectator)) {
            return;
        }
        event.setCancelled(true);
    }
}
