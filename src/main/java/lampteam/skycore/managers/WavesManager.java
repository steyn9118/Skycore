package lampteam.skycore.managers;

import de.leonhard.storage.Yaml;
import lampteam.skycore.models.waves.CreepRain;
import lampteam.skycore.models.waves.PotionRain;

public class WavesManager {

    public static void loadWavesFromConfig(){
        Yaml wavesConfig = ConfigsManager.getWavesConfig();

        wavesConfig.setPathPrefix("creeperRain");
        CreepRain.loadProperties(
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("potionEffectDuration", 10),
                wavesConfig.getOrSetDefault("mobSpeed", 0.1),
                wavesConfig.getOrSetDefault("mobFuse", 20)
        );

        wavesConfig.setPathPrefix("potionRain");
        PotionRain.loadProperties(
                wavesConfig.getOrSetDefault("totalCount", 240),
                wavesConfig.getOrSetDefault("maxSpreadRadius", 5),
                wavesConfig.getOrSetDefault("areaSize", 2.5),
                wavesConfig.getOrSetDefault("areaDuration", 10)
        );
    }

}
