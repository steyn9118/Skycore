package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class Shreder extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static int lastElevationPoint;

    public static void loadProperties(
            int weight1,
            int lastElevationPoint1
    ){
        weight = weight1;
        lastElevationPoint = lastElevationPoint1;
    }

    private BukkitRunnable wave;

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        int period = (arena.getWavesInterval()*20)/((int) arena.getBorders().getMaxY() - lastElevationPoint);

        wave = new BukkitRunnable() {
            int timer = 0;
            double y = arena.getBorders().getMaxY();

            @Override
            public void run() {
                if (timer % 2 == 0) {
                    for (int x = (int) arena.getBorders().getMinX(); x < arena.getBorders().getMaxX(); x++) {
                        for (int z = (int) arena.getBorders().getMinZ(); z < arena.getBorders().getMaxZ(); z++) {
                            //частицы
                            arena.getWorld().spawnParticle(Particle.CRIT, x + 0.5, y, z + 0.5, 1, 0, 0, 0, 0, null, false);

                            //удаление
                            if (timer % period == 0) {
                                if (!arena.getWorld().getBlockAt(x, (int) y, z).getType().equals(Material.BEDROCK)) {
                                    arena.getWorld().getBlockAt(x, (int) y, z).setType(Material.AIR);
                                }
                            }
                        }
                    }
                }

                //движение вверх и вниз
                if (timer < arena.getWavesInterval()*20) y -= (double) 1/period;
                else y += (double) 1/period;

                if (timer >= arena.getWavesInterval()*20*2) timer = 0;
                else timer++;
            }
        };
        wave.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void stopWave() {

    }
}
