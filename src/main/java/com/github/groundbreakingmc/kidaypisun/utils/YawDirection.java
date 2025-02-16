package com.github.groundbreakingmc.kidaypisun.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public final class YawDirection {

    public static boolean isSouthOrNorth(final Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0.0F) {
            yaw += 360.0F;
        }

        if (yaw >= 315.0F || yaw < 45.0F) {
            return true;
        }
        if (yaw < 135.0F) {
            return false;
        }
        if (yaw < 225.0F) {
            return true;
        }

        return false;
    }
}
