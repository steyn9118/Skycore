package lampteam.skycore.models.waves;

import lampteam.skycore.Skycore;
import lampteam.skycore.managers.PlayerModelsManager;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.PlayerModel;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Hashtable;

public class Worms extends AWave{
    Skycore plugin = Skycore.getPlugin();

    private static double speed; //Def: 0.05(blocks per tick)
    private static int edgeCubeLength;//Def: 9

    private BukkitRunnable wave;

    public static void loadProperties(
            double speed1,
            int edgeCubeLength1
    ){
        speed = speed1;
        edgeCubeLength = edgeCubeLength1;
    }

    @Override
    public void startWave(Arena arena) {

        Hashtable<Sheep, Player> wormsList = new Hashtable<>();
        for (PlayerModel playerModel : arena.getPlayers()){
            Sheep sheep = (Sheep) arena.getWorld().spawnEntity(arena.getCenterCore(), EntityType.SHEEP);
            sheep.setGravity(false);
            sheep.setInvulnerable(true);
            sheep.setAdult();
            sheep.setAI(false);
            sheep.setColor(DyeColor.BLACK);
            sheep.setCollidable(false);
            wormsList.put(sheep,playerModel.getPlayer());
        }

        wave = new BukkitRunnable() {
            int timer = 0;
            @Override
            public void run() {
                for (Sheep sheep : wormsList.keySet()){
                    Player player = wormsList.get(sheep);
                    Location sheepLocation = sheep.getLocation().clone();

                    if (!arena.getPlayers().contains(PlayerModelsManager.getModelOfPlayer(player))){
                        sheep.remove();
                        //можно ли изменять хешмапу во время итерации?
                        wormsList.remove(sheep, player);
                        continue;
                    }

                    if (sheep.isSheared()){
                        //звук
                        arena.getWorld().playSound(sheepLocation, Sound.ENTITY_SHEEP_AMBIENT, 1, 0.1f);
                        sheep.remove();
                        wormsList.remove(sheep, player);
                        //я не знаю как по другому сетать много блоков
                        int halfLenght = (edgeCubeLength-1)/2;
                        for (int x = -halfLenght; x < halfLenght; x++){
                            for (int z = -halfLenght; z < halfLenght; z++){
                                sheepLocation.clone().add(x, -halfLenght, z).getBlock().setType(Material.OBSIDIAN);
                                sheepLocation.clone().add(x, halfLenght, z).getBlock().setType(Material.OBSIDIAN);
                            }
                        }
                        for (int x = -halfLenght; x < halfLenght; x++){
                            for (int y = -halfLenght; y < halfLenght; y++){
                                sheepLocation.clone().add(x, y, halfLenght).getBlock().setType(Material.OBSIDIAN);
                                sheepLocation.clone().add(x, y, -halfLenght).getBlock().setType(Material.OBSIDIAN);
                            }
                        }
                        for (int y = -halfLenght; y < halfLenght; y++){
                            for (int z = -halfLenght; z < halfLenght; z++){
                                sheepLocation.clone().add(halfLenght, y, z).getBlock().setType(Material.OBSIDIAN);
                                sheepLocation.clone().add(-halfLenght, y, z).getBlock().setType(Material.OBSIDIAN);
                            }
                        }
                    }

                    //поворот
                    Vector step = player.getEyeLocation().subtract(sheep.getLocation()).toVector().normalize();
                    sheep.getLocation().setDirection(step);
                    sheep.getEyeLocation().setDirection(step);
                    //движение вперед
                    sheep.teleport(sheep.getLocation().add(step.multiply(speed)));

                    if (timer % 10 == 0 && !sheepLocation.getBlock().getBlockData().getMaterial().equals(Material.BEDROCK)){
                        sheep.getLocation().getBlock().setType(Material.OBSIDIAN);
                    }
                }
                timer++;
            }
        };
        wave.runTaskTimer(plugin,0,1);
    }

    @Override
    public void stopWave(){
        wave.cancel();
    }
}
