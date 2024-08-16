package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Worms extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static double speed; //blocks per tick
    private static int edgeCubeLength;

    public static void loadProperties(
            double speed1,
            int edgeCubeLength1
    ){
        speed = speed1;
        edgeCubeLength = edgeCubeLength1;
    }
    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void startWave(Arena arena) {

        HashMap<Player, Sheep> wormsList = new HashMap<>();
        for (PlayerModel playerModel : arena.getPlayers()){
            Sheep sheep = (Sheep) arena.getWorld().spawnEntity(arena.getCenterCore(), EntityType.SHEEP);
            sheep.setGravity(false);
            sheep.setInvulnerable(true);
            sheep.setAdult();
            sheep.setAI(false);
            sheep.setColor(DyeColor.BLACK);
            sheep.setCollidable(false);
            wormsList.put(playerModel.getPlayer(), sheep);
        }

        BukkitRunnable worms = new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                for (PlayerModel playerModel : arena.getPlayers()){
                    Sheep sheep = wormsList.get(playerModel.getPlayer());
                    Location sheepLocation = sheep.getLocation().clone();

                    //поворот
                    Location playerLocation = playerModel.getPlayer().getEyeLocation().clone();
                    sheep.getLocation().setDirection(new Vector(playerLocation.getX() - sheepLocation.getX(), playerLocation.getY() - sheepLocation.getY(), playerLocation.getZ() - sheepLocation.getZ()).normalize());

                    //движение вперед
                    Vector step = sheep.getLocation().getDirection().clone();
                    sheep.teleport(sheep.getLocation().add(step.multiply(speed)));

                    if (timer % 5 == 0 && !sheepLocation.getBlock().getBlockData().getMaterial().equals(Material.BEDROCK)){
                        sheep.getLocation().getBlock().setType(Material.OBSIDIAN);
                    }
                }
                timer++;
            }
        };
        worms.runTaskTimer(plugin,0,1);
    }
}
