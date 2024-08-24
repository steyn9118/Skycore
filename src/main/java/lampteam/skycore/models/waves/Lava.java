package lampteam.skycore.models.waves;

import com.fastasyncworldedit.core.function.mask.AirMask;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.mask.RegionMask;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import lampteam.skycore.Skycore;
import lampteam.skycore.models.Arena;
import lampteam.skycore.models.LinearDirection;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

public class Lava extends AWave {
    Skycore plugin = Skycore.getPlugin();

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

            int shift = 0;
            final BoundingBox borders = arena.getBorders();
            final World world = BukkitAdapter.adapt(arena.getWorld());

            @Override
            public void run() {


                BlockVector3 pos1 = BlockVector3.at(borders.getMaxX(), borders.getMinY() + shift, borders.getMaxZ());
                BlockVector3 pos2 = BlockVector3.at(borders.getMinX(), borders.getMinY() + shift, borders.getMinZ());

                CuboidRegion region = new CuboidRegion(world, pos1, pos2);

                Pattern pattern = new BlockPattern(BlockTypes.LAVA.getDefaultState());
                BlockMask mask = new BlockMask();
                mask.add(BlockTypes.AIR.getDefaultState());

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    editSession.replaceBlocks(region, mask, pattern);

                }
                shift += LinearDirection.FORWARDS.getValue();

            }
        };
        wave.runTaskTimer(plugin, 0, (int) (20/((arena.getBorders().getHeight() - (arena.getBorders().getCenterY() - elevationPoint)) / (double) arena.getWavesInterval())));
    }

    @Override
    public void stopWave() {
        wave.cancel();
    }

}
