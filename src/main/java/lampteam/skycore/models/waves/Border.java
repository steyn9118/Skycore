package lampteam.skycore.models.waves;

import lampteam.skycore.models.Arena;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.Random;

public class Border extends AWave {

    private static int weight;
    private Arena arena1;
    private static double areaSize;
    private static int maxCenterSpread;

    public static void loadProperties(
            int weight1,
            double areaSize1,
            int maxCenterSpread1

    ){
        weight = weight1;
        areaSize = areaSize1;
        maxCenterSpread = maxCenterSpread1;
    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        arena1 = arena;

        int x;
        int z;
        Location location;
        Random random = new Random();
        do {
            x = random.nextInt(-maxCenterSpread, maxCenterSpread);
            z = random.nextInt(-maxCenterSpread, maxCenterSpread);
            location = new Location(arena.getWorld(), x, arena.getCenterCore().getY(), z);
        }while (location.subtract(arena.getCenterCore()).toVector().length() >= maxCenterSpread);

        arena.getWorld().getWorldBorder().setSize(areaSize*1.5, 0);
        arena.getWorld().getWorldBorder().setCenter(location);
        arena.getWorld().getWorldBorder().setSize(areaSize, arena.getWavesInterval());
    }

    @Override
    public void stopWave() {
        arena1.getWorld().getWorldBorder().setCenter(arena1.getCenterCore());
        arena1.getWorld().getWorldBorder().setSize(arena1.getBorders().getWidthX());
    }
}
