package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.Utils;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ArenasManager {

    private static List<Arena> arenas = new ArrayList<>();

    public static void loadArenasFromConfig(){
        for (Yaml config : ConfigsManager.getArenasConfigs()){
            int id = config.getOrSetDefault("id", Utils.generateArenaID());
            String name = config.getOrSetDefault("name", "---");
            int wavesInterval = config.getOrSetDefault("wavesInterval", 120);

            arenas.add(new Arena(id, wavesInterval));
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
            arena.
        }
    }
}
