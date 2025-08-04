package com.github.groundbreakingmc.kidaypisun.utils.config;

import com.github.groundbreakingmc.kidaypisun.KidayPisun;
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
public class ConfigValues {

    private int resetTime;
    private Dick defaultDick;
    private Map<String, Dick> dicks;

    @Getter(AccessLevel.NONE)
    private final KidayPisun plugin;

    public ConfigValues(KidayPisun plugin) {
        this.plugin = plugin;
    }

    public void setupValues(FileConfiguration config) {

        this.resetTime = config.getInt("reset-in");

        ConfigurationSection defaultDickSection = config.getConfigurationSection("default-dick");
        this.defaultDick = this.getDick(defaultDickSection);

        Map<String, Dick> tempMap = new HashMap<>();

        ConfigurationSection dickSection = config.getConfigurationSection("dicks");
        for (String key : dickSection.getKeys(false)) {
            if (key.equalsIgnoreCase("reload")) {
                this.plugin.getLogger().warning("Cannot create dick with the key \"reload\"!");
                continue;
            }

            ConfigurationSection keySection = dickSection.getConfigurationSection(key);
            Dick dick = this.getDick(keySection);
            if (dick.length < 1) {
                this.plugin.getLogger().warning("Cannot create dick with the length less then 1!");
                continue;
            }

            tempMap.put(key, dick);
        }

        this.dicks = ImmutableMap.copyOf(tempMap);
    }

    private Dick getDick(ConfigurationSection section) {
        return new Dick(
                this.getBlockData(section, true),
                this.getBlockData(section, false),
                defaultDick == null
                        ? section.getInt("length")
                        : section.getInt("length", this.defaultDick.length())
        );
    }

    private BlockData getBlockData(ConfigurationSection section, boolean head) {
        String materialName = section.getString(head ? "head-material" : "body-material");

        if (materialName == null) {
            materialName = head
                    ? this.defaultDick.headBlockData().getMaterial().name()
                    : this.defaultDick.bodyBlockData().getMaterial().name();
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
