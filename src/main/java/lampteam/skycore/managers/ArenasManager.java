package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.Utils;
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

    public static void loadArenasFromConfig() throws Exception {
        lastArenaID = ConfigsManager.getPluginConfig().getOrSetDefault("lastArenaID", 0);
        arenas.clear();

        Location defLocation = new Location(Bukkit.getWorld("world"), 0,0,0);

        for (Yaml config : ConfigsManager.getArenasConfigs()){
            int id = config.getInt("id");
            if (id == 0) id = generateArenaID();

            int wavesInterval = config.getOrSetDefault("wavesInterval", 120);
            String name = config.getOrSetDefault("name", "none");
            World world = Bukkit.getWorld(config.getOrSetDefault("worldName", "world"));
            Location lobbyLocation = Utils.Converter.LocFromStr(config.getOrSetDefault("lobbyLocation", Utils.Converter.LocToStr(defLocation, true)));
            Location spectatorsSpawnPoint = Utils.Converter.LocFromStr(config.getOrSetDefault("spectatorsSpawnPoint", Utils.Converter.LocToStr(defLocation, true)));
            List<Location> playerSpawnLocations = Utils.Converter.LocsListFromStrsList(config.getOrSetDefault("playerSpawnLocations", new ArrayList<>()));

            Location centerCore = Utils.Converter.LocFromStr(config.getOrSetDefault("centerCore", Utils.Converter.LocToStr(defLocation, false)));
            Location corner1 = Utils.Converter.LocFromStr(config.getOrSetDefault("corner1", Utils.Converter.LocToStr(defLocation, false)));
            Location corner2 = Utils.Converter.LocFromStr(config.getOrSetDefault("corner2", Utils.Converter.LocToStr(defLocation, false)));
            BoundingBox borders = new BoundingBox(corner1.x(), corner1.y(), corner1.z(), corner2.x(), corner2.y(), corner2.z());

            Location hubLocation = Utils.Converter.LocFromStr(config.getOrSetDefault("hubLocation", Utils.Converter.LocToStr(defLocation, true)));
            int minPlayers = config.getOrSetDefault("minPlayers", 2);
            int lobbyTime = config.getOrSetDefault("lobbyTimerDuration", 60);
            String arenaSchemName = config.getOrSetDefault("arenaSchemName", "arena.schem");

            arenas.add(new Arena(
                    id,
                    wavesInterval,
                    name,
                    world,
                    lobbyLocation,
                    spectatorsSpawnPoint,
                    playerSpawnLocations,
                    centerCore,
                    borders,
                    hubLocation,
                    lobbyTime,
                    minPlayers,
                    arenaSchemName));
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

    public static Arena getArenaByID(int id){
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

    public static List<String> getAllIDs(){
        List<String> ids = new ArrayList<>();
        for (Arena arena : arenas){
            ids.add(String.valueOf(arena.getId()));
        }
        return ids;
    }
}
