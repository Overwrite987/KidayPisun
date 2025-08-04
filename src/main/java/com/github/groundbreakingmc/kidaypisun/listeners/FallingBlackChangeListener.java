package com.github.groundbreakingmc.kidaypisun.listeners;

import com.github.groundbreakingmc.kidaypisun.KidayPisun;
import com.github.groundbreakingmc.kidaypisun.utils.DickUtils;
import com.github.groundbreakingmc.kidaypisun.utils.config.ConfigValues;
import com.github.groundbreakingmc.mylib.collections.cases.Pair;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class FallingBlackChangeListener implements Listener {

    private final KidayPisun plugin;
    private final ConfigValues configValues;

    public FallingBlackChangeListener(KidayPisun plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof FallingBlock) {
            UUID entityUUID = entity.getUniqueId();
            if (DickUtils.isPluginFallingBlock(entityUUID)) {
                event.setCancelled(true);
                entity.remove();
                DickUtils.removeFromFallingBocks(entityUUID);
            } else {
                Pair<ConfigValues.Dick, Boolean> data = DickUtils.getFromPluginMainFallingBlocks(entityUUID);
                if (data == null) {
                    return;
                }

                event.setCancelled(true);
                entity.remove();

                Location location = event.getBlock().getLocation().clone();
                this.buildDick(location, data);

                DickUtils.removeFromMainFallingBocks(entityUUID);
            }
        }
    }

    private void buildDick(Location location,
                           Pair<ConfigValues.Dick, Boolean> data) {
        if (data.getRight()) {
            location.setX(location.getX() + DickUtils.OFFSET);
            this.build(location, data.getLeft().bodyBlockData());

            location.setX(location.getX() - (DickUtils.OFFSET * 2));
            this.build(location, data.getLeft().bodyBlockData());

            location.setX(location.getX() + DickUtils.OFFSET);
        } else {
            location.setZ(location.getZ() + DickUtils.OFFSET);
            this.build(location, data.getLeft().bodyBlockData());

            location.setZ(location.getZ() - (DickUtils.OFFSET * 2));
            this.build(location, data.getLeft().bodyBlockData());

            location.setZ(location.getZ() + DickUtils.OFFSET);
        }

        for (int i = 0; i < data.getLeft().length(); i++) {
            this.build(location, data.getLeft().bodyBlockData());
            location.setY(location.getY() + DickUtils.OFFSET);
        }

        this.build(location, data.getLeft().headBlockData());
    }

    private void build(Location location, BlockData blockData) {
        Block block = location.getBlock();

        CustomBlockData customBlockData = new CustomBlockData(block, this.plugin);
        Integer numb = customBlockData.get(KidayPisun.KEY, PersistentDataType.INTEGER);
        if (numb != null) {
            return;
        }

        BlockData originalBlockData = block.getBlockData();

        block.setType(blockData.getMaterial());
        customBlockData.set(KidayPisun.KEY, PersistentDataType.INTEGER, 1);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            block.setBlockData(originalBlockData);
            customBlockData.remove(KidayPisun.KEY);
        }, this.configValues.getResetTime());
    }
}
