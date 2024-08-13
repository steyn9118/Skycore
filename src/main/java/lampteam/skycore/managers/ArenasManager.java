package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class ArenasManager {

    private static final List<Arena> arenas = new ArrayList<>();

    private static int lastArenaID;

    public static int getLastArenaID(){
        return lastArenaID;
    }

    private static int generateArenaID(){
        lastArenaID += 1;
        return lastArenaID;
    }

    public static void loadArenasFromConfig(){
        lastArenaID = ConfigsManager.getPluginConfig().getOrSetDefault("lastArenaID", 0);

        for (Yaml config : ConfigsManager.getArenasConfigs()){
            int id = config.getOrSetDefault("id", generateArenaID());
            int wavesInterval = config.getOrSetDefault("wavesInterval", 120);
            String name = config.getOrSetDefault("name", "---");
            World world = Bukkit.getWorld(config.getOrSetDefault("worldName", "world"));
            Location lobbyLocation = config.getOrSetDefault("lobbyLocation", new Location(world, 0,0,0));
            Location spectatorsSpawnPoint = config.getOrSetDefault("spectatorsSpawnPoint", new Location(world, 0,0,0));
            List<Location> playerSpawnLocations = config.getOrSetDefault("playerSpawnLocations", new ArrayList<>());

            Location corner1 = config.getOrSetDefault("corner1", new Location(world, 0, 0, 0));
            Location corner2 = config.getOrSetDefault("corner1", new Location(world, 0, 0, 0));
            BoundingBox borders = new BoundingBox(corner1.x(), corner1.y(), corner1.z(), corner2.x(), corner2.y(), corner2.z());

            Location hubLocation = config.getOrSetDefault("hubLocation", new Location(world, 0,0,0));

            arenas.add(new Arena(
                    id,
                    wavesInterval,
                    name,
                    world,
                    lobbyLocation,
                    spectatorsSpawnPoint,
                    playerSpawnLocations,
                    borders,
                    hubLocation));
        }
    }

    public static void joinArena(PlayerModel playerModel, int arenaID){
        Arena arena = getArenaByID(arenaID);
        if (arena == null){
            playerModel.getPlayer().sendMessage(Component.text("Арена не найдена! Обратитесь к администрации"));
            return;
        }
        arena.tryJoinPlayer(playerModel);
    }

    public static void leaveArena(PlayerModel playerModel){
        Arena arena = playerModel.getCurrentArena();
        if (arena == null) return;
        arena.tryLeavePlayer(playerModel);
    }

    private static Arena getArenaByID(int id){
        for (Arena arena : arenas){
            if (arena.getId() == id) return arena;
        }
        return null;
    }

    public static void stopEverything(){
        for (Arena arena : arenas){
            arena.forceStop();
        }
    }

    public static List<Arena> getArenas(){
        return arenas;
    }
}
