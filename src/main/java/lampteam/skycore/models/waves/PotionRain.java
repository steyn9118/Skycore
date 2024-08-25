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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PotionRain extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static int totalCount;//Def: 120
    private static int maxSpreadRadius;//Def: 8
    private static double areaSize;//Def: 3
    private static int areaDuration;//Def: 5*20

    private BukkitRunnable wave;

    public static void loadProperties(
            int weight1,
            int totalCount1,
            int maxSpreadRadius1,
            double areaSize1,
            int areaDuration1
    ){
        weight = weight1;
        totalCount = totalCount1;
        maxSpreadRadius = maxSpreadRadius1;
        areaSize = areaSize1;
        areaDuration = areaDuration1;
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
            double x;
            double z;
            @Override
            public void run() {
                if (counter >= totalCount) wave.cancel();
                counter++;

                for (PlayerModel model : arena.getPlayers()) {
                    x = random.nextDouble(-maxSpreadRadius, maxSpreadRadius);
                    z = random.nextDouble(-maxSpreadRadius, maxSpreadRadius);
                    Location location = new Location(arena.getWorld(), model.getPlayer().getLocation().getX() + x, arena.getBorders().getMaxY(), model.getPlayer().getLocation().getZ() + z);
                    ThrownPotion potion = (ThrownPotion) arena.getWorld().spawnEntity(location, EntityType.POTION);
                    potion.setItem(new ItemStack(Material.LINGERING_POTION));
                    PotionMeta potionMeta = potion.getPotionMeta().clone();
                    int r = random.nextInt(1, 8);
                    switch (r) {
                        case 1 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 1, false, true, true), true);
                            potionMeta.setColor(Color.GREEN);
                            potion.setPotionMeta(potionMeta);
                        }
                        case 2 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1, false, true, true), true);
                            potionMeta.setColor(Color.BLACK);
                            potion.setPotionMeta(potionMeta);
                        }
                        case 3 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 2, false, true, true), true);
                            potionMeta.setColor(Color.GRAY);
                            potion.setPotionMeta(potionMeta);
                        }
                        case 4 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0, false, true, true), true);
                            potionMeta.setColor(Color.WHITE);
                            potion.setPotionMeta(potionMeta);;
                        }
                        case 5 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 99, false, true, true), true);
                            potionMeta.setColor(Color.OLIVE);
                            potion.setPotionMeta(potionMeta);
                        }
                        case 6 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 10, 1, false, true, true), true);
                            potionMeta.setColor(Color.AQUA);
                            potion.setPotionMeta(potionMeta);
                        }
                        case 7 -> {
                            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 1, false, true, true), true);
                            potionMeta.setColor(Color.NAVY);
                            potion.setPotionMeta(potionMeta);
                        }
                    }
                }
            }
        };
        wave.runTaskTimer(plugin, 0, (int) (20/((double) totalCount/(double) arena.getWavesInterval())));
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }
}
