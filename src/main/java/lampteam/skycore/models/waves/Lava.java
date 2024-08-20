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
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

public class Lava extends AWave {
    Skycore plugin = Skycore.getPlugin();

    private static int speed;
    private static int standingDuration;
    private static double areaSize;

    private BukkitRunnable wave;

    public static void loadProperties(
            int speed1,
            int standingDuration1,
            int areaSize1
    ){
        speed = speed1;
        standingDuration = standingDuration1;
        areaSize = areaSize1;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void startWave(Arena arena) {

        wave = new BukkitRunnable() {
            int shift = 0;
            @Override
            public void run() {

                BoundingBox borders = arena.getBorders();
                BlockVector3 pos1 = BlockVector3.at(borders.getMinX(), borders.getMinY() + shift, borders.getMinZ());
                BlockVector3 pos2 = BlockVector3.at(borders.getMinX(), borders.getMinY() + shift, borders.getMinZ());

                World world = BukkitAdapter.adapt(arena.getWorld());
                CuboidRegion region = new CuboidRegion(world, pos1, pos2);

                Pattern pattern = new BlockPattern(BlockTypes.LAVA.getDefaultState());
                BlockMask mask = new BlockMask();
                mask.add(BlockTypes.AIR.getDefaultState());

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                    editSession.replaceBlocks(region, mask, pattern);

                }
                shift += 1;

            }
        };wave.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void stopWave() {

    }

}
