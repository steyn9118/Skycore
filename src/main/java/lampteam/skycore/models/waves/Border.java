package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.scheduler.BukkitRunnable;

public class Border extends AWave {

    private static int weight;
    private Arena arena1;
    private static double areaSize;

    public static void loadProperties(
            int weight1,
            double areaSize1

    ){
        weight = weight1;
        areaSize = areaSize1;

    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        arena1 = arena;
        arena.getWorld().getWorldBorder().setSize(areaSize, arena.getWavesInterval());
    }

    @Override
    public void stopWave() {
        arena1.getWorld().getWorldBorder().setSize(arena1.getBorders().getWidthX());
    }
}
