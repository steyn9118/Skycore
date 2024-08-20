package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.models.waves.*;

import java.util.Collection;
import java.util.Collections;
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
                wavesConfig.getOrSetDefault("maxSpreadRadius", 5),
                wavesConfig.getOrSetDefault("areaSize", 2.5),
                wavesConfig.getOrSetDefault("areaDuration", 10)
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
                wavesConfig.getOrSetDefault("totalCount", 360)
        );
        wavesDictionary.put("smallBeams", new SmallBeams());

        //конфиг лавы
        //wavesDictionary.put("lava", new Lava());

        wavesConfig.setPathPrefix("blackHole");
        Blackhole.loadProperties(
                wavesConfig.getOrSetDefault("weight", 3),
                wavesConfig.getOrSetDefault("maxPower", 1.0),
                wavesConfig.getOrSetDefault("maxRadius", 52)
        );
        wavesDictionary.put("blackHole", new Blackhole());
        // TODO все новые волны
    }

}
