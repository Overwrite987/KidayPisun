package com.github.groundbreakingmc.kidaypisun.utils.config;

import com.github.groundbreakingmc.kidaypisun.KidayPisun;
import com.github.groundbreakingmc.mylib.logger.console.LoggerFactory;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class ConfigValues {

    private int resetTime;
    private Dick defaultDick;
    private Map<String, Dick> dicks;

    @Getter(AccessLevel.NONE)
    private final KidayPisun plugin;

    public ConfigValues(final KidayPisun plugin) {
        this.plugin = plugin;
    }

    public void setupValues(FileConfiguration config) {

        this.resetTime = config.getInt("reset-in");

        final ConfigurationSection defaultDickSection = config.getConfigurationSection("default-dick");
        this.defaultDick = this.getDick(defaultDickSection);

        final HashMap<String, Dick> tempMap = new HashMap<>();

        final ConfigurationSection dickSection = config.getConfigurationSection("dicks");
        for (final String key : dickSection.getKeys(false)) {
            if (key.equalsIgnoreCase("reload")) {
                this.plugin.getLogger().warning("Cannot create dick with the key \"reload\"!");
                continue;
            }

            final ConfigurationSection keySection = dickSection.getConfigurationSection(key);
            final Dick dick = this.getDick(keySection);
            if (dick.length < 1) {
                this.plugin.getLogger().warning("Cannot create dick with the length less then 1!");
                continue;
            }

            tempMap.put(key, dick);
        }

        this.dicks = ImmutableMap.copyOf(tempMap);
    }

    private Dick getDick(final ConfigurationSection section) {
        return new Dick(
                this.getBlockData(section, true),
                this.getBlockData(section, false),
                defaultDick == null
                        ? section.getInt("length")
                        : section.getInt("length", this.defaultDick.length)
        );
    }

    private BlockData getBlockData(final ConfigurationSection section, final boolean head) {
        String materialName = section.getString(head ? "head-material" : "body-material");

        if (materialName == null) {
            materialName = head
                    ? this.defaultDick.headBlockData.getMaterial().name()
                    : this.defaultDick.bodyBlockData.getMaterial().name();
        }

        return Material.getMaterial(materialName.toUpperCase()).createBlockData();
    }

    public record Dick(
            BlockData headBlockData,
            BlockData bodyBlockData,
            int length
    ) {
    }
}
