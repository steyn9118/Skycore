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
        wavesDictionary.clear();

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
                wavesConfig.getOrSetDefault("radius", 30.0),
                wavesConfig.getOrSetDefault("xBound", 80),
                wavesConfig.getOrSetDefault("yUpBound", 55),
                wavesConfig.getOrSetDefault("yDownBound", 25)
        );
        wavesDictionary.put("blackHole", new Blackhole());

        wavesConfig.setPathPrefix("border");
        Border.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("areaSize", 101.0),
                wavesConfig.getOrSetDefault("maxCenterSpread", 85)
        );
        wavesDictionary.put("border", new Border());

        wavesConfig.setPathPrefix("stormWind");
        StormWind.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("changeCount", 4),
                wavesConfig.getOrSetDefault("mainPower", 0.04)
        );
        wavesDictionary.put("stormWind", new StormWind());

        wavesConfig.setPathPrefix("shreder");
        Shreder.loadProperties(
                wavesConfig.getOrSetDefault("weight", 4),
                wavesConfig.getOrSetDefault("lastElevationPoint", 38)
        );
        wavesDictionary.put("shreder", new Shreder());
        // TODO все новые волны
    }

}
