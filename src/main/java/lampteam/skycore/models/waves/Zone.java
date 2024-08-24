package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.LinearDirection;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

public class Zone extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static double areaSize;

    private BukkitRunnable wave;

    public static void loadProperties(
            int weight1,
            double areaSize1

    ){
        weight = weight1;
        areaSize = areaSize1;

    }

    @Override
    public void startWave(Arena arena) {
        arena.getWorld().getWorldBorder().setSize(areaSize, arena.getWavesInterval());
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }
}
