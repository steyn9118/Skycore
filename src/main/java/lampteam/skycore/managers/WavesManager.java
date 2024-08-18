package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.models.waves.*;

import java.util.HashMap;
import java.util.List;

public class WavesManager {

    private static final HashMap<String, AWave> wavesDictionary = new HashMap<>();

    public static List<AWave> getAllWaves(){
        return wavesDictionary.values().stream().toList();
    }

    public static AWave getWaveByName(String name){
        return wavesDictionary.get(name);
    }

    public static List<String> getWavesNames(){
        return wavesDictionary.keySet().stream().toList();
    }

    public static void loadWavesFromConfig(){
        Yaml wavesConfig = ConfigsManager.getWavesConfig();

        wavesConfig.setPathPrefix("creeperRain");
        CreepRain.loadProperties(
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("potionEffectDuration", 10),
                wavesConfig.getOrSetDefault("mobSpeed", 0.1),
                wavesConfig.getOrSetDefault("mobFuse", 20)
        );
        wavesDictionary.put("creeperRain", new CreepRain());

        wavesConfig.setPathPrefix("potionRain");
        PotionRain.loadProperties(
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("maxSpreadRadius", 5),
                wavesConfig.getOrSetDefault("areaSize", 2.5),
                wavesConfig.getOrSetDefault("areaDuration", 10)
        );
        wavesDictionary.put("potionRain", new PotionRain());

        wavesConfig.setPathPrefix("worms");
        Worms.loadProperties(
                wavesConfig.getOrSetDefault("speed", 1),
                wavesConfig.getOrSetDefault("edgeOfCubeLength", 9)
        );
        wavesDictionary.put("worms", new Worms());

        wavesConfig.setPathPrefix("smallBeams");
        SmallBeams.loadProperties(
                wavesConfig.getOrSetDefault("totalCount", 60)
        );
        wavesDictionary.put("smallBeams", new SmallBeams());

        // TODO все новые волны
    }

}
