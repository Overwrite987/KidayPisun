package com.github.groundbreakingmc.kidaypisun.commands;

import com.github.groundbreakingmc.kidaypisun.KidayPisun;
import com.github.groundbreakingmc.kidaypisun.utils.DickUtils;
import com.github.groundbreakingmc.kidaypisun.utils.config.ConfigValues;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final KidayPisun plugin;
    private final ConfigValues configValues;

    public CommandManager(KidayPisun plugin) {
        this.plugin = plugin;
        this.configValues = plugin.getConfigValues();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command for players only!");
            return true;
        }

        ConfigValues.Dick dick = args.length > 0 && !args[0].equalsIgnoreCase("default")
                ? this.configValues.getDicks().get(args[0])
                : this.configValues.getDefaultDick();

        if (dick == null) {
            sender.sendMessage("Dick \"" + args[0] + "\" wasn't found");
            return true;
        }

        if (label.equalsIgnoreCase("spampisun")) {
            BukkitTask task = this.plugin.removeTask(player);
            if (task != null) {
                task.cancel();
                return true;
            }

            try {
                int period = args.length > 1 ? Integer.parseInt(args[1]) : 2;
                task = Bukkit.getScheduler().runTaskTimer(this.plugin, () ->
                        DickUtils.spawn(player, dick), 0L, period
                );
                this.plugin.addTask(player, task);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cIncorrect numb: " + args[1]);
            }
        } else {
            DickUtils.spawn(player, dick);
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("kidaypisun.use")) {
            if (args.length == 1) {
                List<String> dicks = new ArrayList<>(this.configValues.getDicks().keySet());
                dicks.add("default");
                return dicks;
            } else if (args.length == 2) {
                return List.of("<period>");
            }
        }

        return List.of();
    }
}
