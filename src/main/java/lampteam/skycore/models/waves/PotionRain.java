package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PotionRain extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int totalCount;
    private static int maxSpreadRadius;
    private static double areaSize;
    private static int areaDuration;

    public static void loadProperties(
            int totalCount1,
            int maxSpreadRadius1,
            double areaSize1,
            int areaDuration1
    ){
        totalCount = totalCount1;
        maxSpreadRadius = maxSpreadRadius1;
        areaSize = areaSize1;
        areaDuration = areaDuration1;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void startWave(Arena arena) {
        Random random = new Random();

        BukkitRunnable wave = new BukkitRunnable() {
            double x;
            double z;
            @Override
            public void run() {
                for (PlayerModel model : arena.getPlayers()) {
                    x = random.nextDouble(-maxSpreadRadius, maxSpreadRadius);
                    z = random.nextDouble(-maxSpreadRadius, maxSpreadRadius);
                    Location location = new Location(arena.getWorld(), model.getPlayer().getLocation().getX() + x, arena.getBorders().getMaxY(), model.getPlayer().getLocation().getZ() + z);
                    ThrownPotion potion = (ThrownPotion) arena.getWorld().spawnEntity(location, EntityType.POTION);
                    potion.setItem(new ItemStack(Material.LINGERING_POTION));
                    int r = random.nextInt(1, 8);
                    switch (r) {
                        case 1:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 1, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.GREEN);
                        case 2:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.BLACK);
                        case 3:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 2, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.GRAY);
                        case 4:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.WHITE);
                        case 5:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 99, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.OLIVE);
                        case 6:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 10, 1, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.AQUA);
                        case 7:
                            potion.getPotionMeta().addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 1, false, true, true), true);
                            potion.getPotionMeta().setColor(Color.NAVY);
                    }
                }
            }
        };
        wave.runTaskTimer(plugin, 0, 20/(totalCount/arena.getWavesInterval()));
    }
}
