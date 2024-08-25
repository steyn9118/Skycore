package lampteam.skycore.models.waves;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.LinearDirection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.Set;

public class Lava extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int weight;
    private static double elevationPoint;

    private BukkitRunnable wave;

    public static void loadProperties(
            int weight1,
            int elevationPoint1
    ){
        weight = weight1;
        elevationPoint = elevationPoint1;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void startWave(Arena arena) {

        wave = new BukkitRunnable() {

            int y = (int) arena.getBorders().getMinY();
            final BoundingBox borders = arena.getBorders();
            final World world = BukkitAdapter.adapt(arena.getWorld());

            @Override
            public void run() {


                BlockVector3 pos1 = BlockVector3.at(borders.getMaxX(), y, borders.getMaxZ());
                BlockVector3 pos2 = BlockVector3.at(borders.getMinX(), y, borders.getMinZ());

                CuboidRegion region = new CuboidRegion(world, pos1, pos2);

                Pattern pattern = new BlockPattern(BlockTypes.LAVA.getDefaultState());
                BlockMask mask = new BlockMask().add(BlockTypes.AIR.getDefaultState());

                BukkitRunnable lavaSet = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                            //editSession.setMask(mask);
                            editSession.replaceBlocks(region, mask, pattern);
                        }
                    }
                };
                lavaSet.runTaskAsynchronously(plugin);

                if (y >= arena.getBorders().getMinY() && y <= elevationPoint) y++;
                else wave.cancel();

            }
        };
        wave.runTaskTimer(plugin, 0, (int) (20/((elevationPoint - arena.getBorders().getMinY()) / arena.getWavesInterval())));
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }

}
