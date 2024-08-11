package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.Skycore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigsManager {

    private static final Skycore plugin = Skycore.getPlugin();

    private static final List<Yaml> arenasConfigs = new ArrayList<>();

    public static void loadArenasConfigs(){
        File arenasFolder = new File(plugin.getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) arenasFolder.mkdir();

        File[] configFiles = arenasFolder.listFiles();
        if (configFiles == null){
            plugin.getLogger().warning("Конфигурации арен отсутсвуют.");
            return;
        }

        for (File file : configFiles){
            arenasConfigs.add(new Yaml(file));
        }
    }

    public static List<Yaml> getArenasConfigs(){
        return arenasConfigs;
    }
}
