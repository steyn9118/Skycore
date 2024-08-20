package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Blackhole extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static double maxPower;
    private static double maxRadius;

    public static void loadProperties(
            double maxPower1,
            double maxRadius1
    ){
        maxPower = maxPower1;
        maxRadius = maxRadius1;
    }

    private BukkitRunnable wave;

    @Override
    public void startWave(Arena arena) {
        Set<Entity> list = new HashSet<>();

        Location center = arena.getCenterCore().add(0.5,2.5,0.5);

        wave = new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                //частицы
                Bukkit.getWorld("world").spawnParticle(Particle.PORTAL, center, (400*(timer/(arena.getWavesInterval()*20))), 0, 0, 0, (5d + 5d*((double) timer /arena.getWavesInterval()*20d)),null, true);
                //звук
                arena.getWorld().playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 10*(timer /(float) (arena.getWavesInterval()*20)), 0.1f);

                list.addAll(center.getNearbyEntitiesByType(LivingEntity.class, maxRadius*((double) timer / (arena.getWavesInterval()*20))));
                list.addAll(center.getNearbyEntitiesByType(Projectile.class, maxRadius*((double) timer / (arena.getWavesInterval()*20))));
                list.addAll(center.getNearbyEntitiesByType(Item.class, maxRadius*((double) timer / (arena.getWavesInterval()*20))));

                for (Entity entity : list) {
                    double distance = entity.getLocation().distance(center);
                    Vector v = center.clone().toVector().subtract(entity.getLocation().clone().toVector()).normalize();
                    if (distance < 3) entity.setVelocity(v.multiply(Math.pow(distance / 3, 2)));
                    else entity.setVelocity(entity.getVelocity().add(v.multiply(maxPower / distance)));

                }
                list.clear();

                if (timer < arena.getWavesInterval()*20) timer+=2;
            }
        };
        wave.runTaskTimer(plugin,0,2);
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }
}
