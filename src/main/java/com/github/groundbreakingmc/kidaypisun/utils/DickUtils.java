package com.github.groundbreakingmc.kidaypisun.utils;

import com.github.groundbreakingmc.kidaypisun.utils.config.ConfigValues;
import com.github.groundbreakingmc.mylib.collections.cases.Pair;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@UtilityClass
public class DickUtils {

    private static final Set<UUID> FALLING_BLOCKS;
    private static final Map<UUID, Pair<ConfigValues.Dick, Boolean>> MAIN_FALLING_BLOCKS;
    public static final double OFFSET;

    public static void spawn(Player player, ConfigValues.Dick dick) {
        Location playerLocation = player.getLocation();
        Location location = playerLocation.clone();
        World world = location.getWorld();
        Vector vector = player.getEyeLocation().getDirection().multiply(1.0);

        boolean horizontal = YawDirection.isSouthOrNorth(playerLocation);
        if (horizontal) {
            location.setX(location.getX() + OFFSET);
            spawnFalling(world, vector, location, dick.bodyBlockData(), false);
            location.setX(location.getX() - (OFFSET * 2));
            spawnFalling(world, vector, location, dick.bodyBlockData(), false);
            location.setX(location.getX() + OFFSET);
        } else {
            location.setZ(location.getZ() + OFFSET);
            spawnFalling(world, vector, location, dick.bodyBlockData(), false);
            location.setZ(location.getZ() - (OFFSET * 2));
            spawnFalling(world, vector, location, dick.bodyBlockData(), false);
            location.setZ(location.getZ() + OFFSET);
        }

        for (int i = 0; i < dick.length(); i++) {
            boolean isMain = i == 0;
            FallingBlock fallingBlock = spawnFalling(
                    world,
                    vector,
                    location,
                    dick.bodyBlockData(),
                    isMain
            );

            location.setY(location.getY() + OFFSET);
            if (isMain) {
                MAIN_FALLING_BLOCKS.put(fallingBlock.getUniqueId(), new Pair<>(dick, horizontal));
            }
        }

        spawnFalling(world, vector, location, dick.headBlockData(), false);
    }

    private FallingBlock spawnFalling(World world,
                                      Vector vector,
                                      Location location,
                                      BlockData blockData,
                                      boolean main) {
        FallingBlock fallingBlock = world.spawnFallingBlock(location, blockData);
        if (!main) {
            FALLING_BLOCKS.add(fallingBlock.getUniqueId());
        }
        fallingBlock.setVelocity(vector);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);

        return fallingBlock;
    }

    public static boolean isPluginFallingBlock(UUID entityUUID) {
        return !FALLING_BLOCKS.isEmpty() && FALLING_BLOCKS.contains(entityUUID);
    }

    public static void removeFromFallingBocks(UUID entityUUID) {
        FALLING_BLOCKS.remove(entityUUID);
    }

    @Nullable
    public static Pair<ConfigValues.Dick, Boolean> getFromPluginMainFallingBlocks(UUID entityUUID) {
        if (MAIN_FALLING_BLOCKS.isEmpty()) {
            return null;
        }

        return MAIN_FALLING_BLOCKS.get(entityUUID);
    }

    public static void removeFromMainFallingBocks(UUID entityUUID) {
        MAIN_FALLING_BLOCKS.remove(entityUUID);
    }

    static {
        FALLING_BLOCKS = new IdentityHashMap<UUID, Object>().keySet();
        MAIN_FALLING_BLOCKS = new IdentityHashMap<>();
        OFFSET = 1.0D;
    }
}
