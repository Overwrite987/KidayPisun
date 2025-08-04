package com.github.groundbreakingmc.kidaypisun.listeners;

import com.github.groundbreakingmc.kidaypisun.KidayPisun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconectListener implements Listener {

    private final KidayPisun plugin;

    public DisconectListener(KidayPisun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.cancel(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        this.plugin.cancel(event.getPlayer());
    }
}
