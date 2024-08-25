package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SmallBeams extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static int totalCount;//Def: 60

    private BukkitRunnable wave;

    public static void loadProperties(
            int weight1,
            int totalCount1
    ){
        weight = weight1;
        totalCount = totalCount1;
    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        Random random = new Random();

        wave = new BukkitRunnable() {
            int counter = 0;
            int x;
            int z;
            boolean onlyAir;
            @Override
            public void run() {
                if (counter >= totalCount) wave.cancel();
                counter++;

                do {
                    onlyAir = true;
                    x = random.nextInt((int) arena.getBorders().getMinX(), (int) arena.getBorders().getMaxX());
                    z = random.nextInt((int) arena.getBorders().getMinZ(), (int) arena.getBorders().getMaxZ());
                    for (int y = (int) arena.getBorders().getMaxY(); y > arena.getBorders().getMinY(); y--) {
                        //проверка что столб блоков не пустой
                        if (!arena.getWorld().getBlockAt(x, y, z).isEmpty()) {
                            onlyAir = false;
                            //звук луча на первом блоке(если считать сверху)
                            arena.getWorld().playSound(new Location(arena.getWorld(), x, y, z), Sound.ITEM_TRIDENT_RETURN, SoundCategory.MASTER,8, 0.8f);
                            break;
                        }
                    }
                }while (onlyAir);

                for (int y = (int) arena.getBorders().getMinY(); y < arena.getBorders().getMaxY(); y++){
                    //частицы
                    arena.getWorld().spawnParticle(Particle.FLASH, x, y, z, 1, 0, 0, 0, 0, null, true);

                    arena.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x+1, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x-1, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x, y, z+1).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x, y, z-1).setType(Material.AIR);
                }
            }
        };
        wave.runTaskTimer(plugin, 0, (int) (20/((double) totalCount/(double) arena.getWavesInterval())));
    }

    @Override
    public void stopWave(){
        wave.cancel();
    }
}
