package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SmallBeams extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int totalCount;//Def: 60

    public static void loadProperties(
            int totalCount1
    ){
        totalCount = totalCount1;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        Random random = new Random();

        BukkitRunnable wave = new BukkitRunnable() {
            int x;
            int z;
            boolean onlyAir;
            @Override
            public void run() {
                do {
                    onlyAir = true;
                    x = random.nextInt((int) arena.getBorders().getMinX(), (int) arena.getBorders().getMaxX());
                    z = random.nextInt((int) arena.getBorders().getMinZ(), (int) arena.getBorders().getMaxZ());
                    for (int y = (int) arena.getBorders().getMinY(); y < arena.getBorders().getMaxY(); y++) {
                        //проверка что столб блоков не пустой
                        if (!arena.getWorld().getBlockAt(x, y, z).isEmpty()) {
                            onlyAir = false;
                            break;
                        }
                    }
                }while (!onlyAir);;

                for (int y = (int) arena.getBorders().getMinY(); y < arena.getBorders().getMaxY(); y++){
                    arena.getWorld().spawnParticle(Particle.FLASH, x, y, z, 1);

                    arena.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x+1, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x-1, y, z).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x, y, z+1).setType(Material.AIR);
                    arena.getWorld().getBlockAt(x, y, z-1).setType(Material.AIR);
                }
            }
        };
        wave.runTaskTimer(plugin, 0,20/(totalCount/arena.getWavesInterval()));
    }
}
