package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.Skycore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigsManager {

    private static final Skycore plugin = Skycore.getPlugin();

    private static final List<Yaml> arenasConfigs = new ArrayList<>();
    private static Yaml wavesConfig;
    private static Yaml pluginConfig;

    public static void loadArenasConfigs(){
        arenasConfigs.clear();

        File arenasFolder = new File(plugin.getDataFolder() + "/Arenas");
        if (!arenasFolder.exists()) arenasFolder.mkdir();

        File[] configFiles = arenasFolder.listFiles();
        assert configFiles != null;
        if (configFiles.length == 0){
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

    public static void loadWavesConfig(){
        wavesConfig = new Yaml("waves.yml", plugin.getDataFolder().getPath());
    }

    public static Yaml getWavesConfig(){
        return wavesConfig;
    }

    public static void loadPluginConfig(){
        pluginConfig = new Yaml("config.yml", plugin.getDataFolder().getPath());
    }

    public static Yaml getPluginConfig(){
        return pluginConfig;
    }

    public static void savePluginConfig(){
        pluginConfig.set("lastArenaID", ArenasManager.getLastArenaID());
    }
}
