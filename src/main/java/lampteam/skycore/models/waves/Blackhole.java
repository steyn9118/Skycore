package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Blackhole extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static double radius;
    private static int xBound;
    private static int yUpBound;
    private static int yDownBound;

    public static void loadProperties(
            int weight1,
            double radius1,
            int xBound1,
            int yUpBound1,
            int yDownBound1
    ){
        weight = weight1;
        radius = radius1;
        xBound = xBound1;
        yUpBound = yUpBound1;
        yDownBound = yDownBound1;
    }

    private BukkitRunnable wave;

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        Set<Entity> list = new HashSet<>();
        Random random = new Random();

        wave = new BukkitRunnable() {
            int timer = 0;
            int x;
            int y;
            int z;
            Location location;

            @Override
            public void run() {

                if (timer == 0){
                    //x посторяется потому что в область квадратная
                    x = random.nextInt(-xBound, xBound);
                    y = random.nextInt(yDownBound, yUpBound);
                    z = random.nextInt(-xBound, xBound);
                    location = new Location(arena.getWorld(), x+0.5, y+0.5, z+0.5);
                }

                //частицы
                arena.getWorld().spawnParticle(Particle.PORTAL, location, 200, 0, 0, 0, 8,null, true);
                //звук
                arena.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 2, 0.1f);

                list.addAll(location.getNearbyEntitiesByType(LivingEntity.class, radius));
                list.addAll(location.getNearbyEntitiesByType(Projectile.class, radius));
                list.addAll(location.getNearbyEntitiesByType(Item.class, radius));

                for (Entity entity : list) {
                    if (entity.getType().equals(EntityType.PLAYER) && !((Player) entity).getGameMode().equals(GameMode.SURVIVAL)) continue;
                    if (entity.getLocation().subtract(location).toVector().length() > radius) continue;
                    double distance = entity.getLocation().distance(location);
                    Vector v = location.clone().toVector().subtract(entity.getLocation().clone().toVector()).normalize();
                    if (distance < 2) entity.setVelocity(v.clone().multiply(Math.pow(distance / 2, 2)));
                    else entity.setVelocity(entity.getVelocity().add(v.clone().multiply(2 / distance)));

                }
                list.clear();

                if (timer < (arena.getWavesInterval()*20)/2) timer+=2;
                else timer = 0;
            }
        };
        wave.runTaskTimer(plugin,0,2);
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }
}
