package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StormWind extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static int changeCount;//Def: 4
    private static double mainPower;//Def: 0.05

    private BukkitRunnable wave;

    public static void loadProperties(
            int weight1,
            int changeCount1,
            double mainPower1
    ){
        weight = weight1;
        changeCount = changeCount1;
        mainPower = mainPower1;;
    }

    @Override
    public int getWeight(){
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        Set<Entity> list = new HashSet<>();
        Location center = arena.getCenterCore();

        wave = new BukkitRunnable() {
            int timer = 0;

            Random random = new Random();
            Vector direction = new Vector(1,0,0).rotateAroundY(Math.toRadians(random.nextInt(360)));

            @Override
            public void run() {
                if (timer <= arena.getWavesInterval()*20) timer++;
                else wave.cancel();

                list.addAll(center.getNearbyEntitiesByType(LivingEntity.class, arena.getBorders().getWidthX()+2));
                list.addAll(center.getNearbyEntitiesByType(Projectile.class, arena.getBorders().getWidthX()+2));
                list.addAll(center.getNearbyEntitiesByType(Item.class, arena.getBorders().getWidthX()+2));

                for (Entity entity : list) {
                    if (entity.getType().equals(EntityType.PLAYER) && !((Player) entity).getGameMode().equals(GameMode.SURVIVAL)) continue;
                    entity.setVelocity(entity.getVelocity().add(direction.clone().multiply(mainPower)));
                }

                if (timer % ((arena.getWavesInterval()*20)/changeCount) == 0){
                    direction = direction.rotateAroundY(Math.toRadians(random.nextInt(360)));
                }
            }
        };
        wave.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }
}
