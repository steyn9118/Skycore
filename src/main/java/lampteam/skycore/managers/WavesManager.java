package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.models.waves.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WavesManager {

    private static final HashMap<String, AWave> wavesDictionary = new HashMap<>();

    public static Collection<AWave> getAllWaves(){
        return wavesDictionary.values();
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
                wavesConfig.getOrSetDefault("weight", 1),
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("potionEffectDuration", 8),
                wavesConfig.getOrSetDefault("mobSpeed", 0.3),
                wavesConfig.getOrSetDefault("mobFuse", 12)
        );
        wavesDictionary.put("creeperRain", new CreepRain());

        wavesConfig.setPathPrefix("potionRain");
        PotionRain.loadProperties(
                wavesConfig.getOrSetDefault("weight", 1),
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("maxSpreadRadius", 5)
        );
        wavesDictionary.put("potionRain", new PotionRain());

        wavesConfig.setPathPrefix("worms");
        Worms.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("speed", 0.1),
                wavesConfig.getOrSetDefault("edgeOfCubeLength", 9)
        );
        wavesDictionary.put("worms", new Worms());

        wavesConfig.setPathPrefix("smallBeams");
        SmallBeams.loadProperties(
                wavesConfig.getOrSetDefault("weight", 2),
                wavesConfig.getOrSetDefault("totalCount", 320)
        );
        wavesDictionary.put("smallBeams", new SmallBeams());

        wavesConfig.setPathPrefix("lava");
        Lava.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("lastElevationPoint", 43)
        );
        wavesDictionary.put("lava", new Lava());

        wavesConfig.setPathPrefix("blackHole");
        Blackhole.loadProperties(
                wavesConfig.getOrSetDefault("weight", 4),
                wavesConfig.getOrSetDefault("maxPower", 1.0),
                wavesConfig.getOrSetDefault("maxRadius", 52)
        );
        wavesDictionary.put("blackHole", new Blackhole());

        wavesConfig.setPathPrefix("border");
        Border.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("areaSize", 120.0)
        );
        wavesDictionary.put("border", new Border());

        wavesConfig.setPathPrefix("stormWind");
        StormWind.loadProperties(
                wavesConfig.getOrSetDefault("weight", 2),
                wavesConfig.getOrSetDefault("changeCount", 4),
                wavesConfig.getOrSetDefault("mainPower", 0.4)
        );
        wavesDictionary.put("stormWind", new StormWind());
        // TODO все новые волны
    }

}
