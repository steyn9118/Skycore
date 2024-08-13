package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class CreepRain extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int totalCount;
    private static int potionEffectDuration;
    private static double mobSpeed;
    private static int mobFuse;

    public static void loadProperties(
        int totalCount1,
        int potionEffectDuration1,
        double mobSpeed1,
        int mobFuse1
    ){
        totalCount = totalCount1;
        potionEffectDuration = potionEffectDuration1;
        mobSpeed = mobSpeed1;
        mobFuse = mobFuse1;
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
                }while (!onlyAir);
                Location location = new Location(arena.getWorld(), x, arena.getBorders().getMaxY(), z);

                Creeper creeper = (Creeper) arena.getWorld().spawnEntity(location, EntityType.CREEPER);
                creeper.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mobSpeed);
                creeper.setFuseTicks(mobFuse);
                creeper.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, potionEffectDuration, 1, false, false, false));
            }
        };
        wave.runTaskTimer(plugin, 0,20/(totalCount/arena.getWavesInterval()));

    }
}
