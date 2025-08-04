package com.github.groundbreakingmc.kidaypisun;

import com.github.groundbreakingmc.kidaypisun.commands.CommandManager;
import com.github.groundbreakingmc.kidaypisun.listeners.DisconectListener;
import com.github.groundbreakingmc.kidaypisun.listeners.FallingBlackChangeListener;
import com.github.groundbreakingmc.kidaypisun.utils.config.ConfigValues;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.util.UUID;

@Getter
public final class KidayPisun extends JavaPlugin {

    public static final NamespacedKey KEY;
    public static final PersistentDataType<BlockData, BlockData> PERSISTENT_DATA_TYPE;

    private final ConfigValues configValues;
    private final Reference2ObjectMap<UUID, BukkitTask> spamming;

    public KidayPisun() {
        this.configValues = new ConfigValues(this);
        this.spamming = new Reference2ObjectOpenHashMap<>();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configValues.setupValues(getConfig());
        this.setupCommand();
        this.registerEvent(new FallingBlackChangeListener(this));
        this.registerEvent(new DisconectListener(this));
    }

    @Override
    public void onDisable() {
        super.getServer().getScheduler().cancelTasks(this);
    }

    private void setupCommand() {
        PluginCommand pisunCommand = super.getCommand("pisun");
        TabExecutor executor = new CommandManager(this);
        pisunCommand.setExecutor(executor);
    }

    private void registerEvent(Listener listener) {
        super.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void addTask(Player player, BukkitTask task) {
        this.spamming.put(player.getUniqueId(), task);
    }

    public BukkitTask removeTask(Player player) {
        return this.spamming.remove(player.getUniqueId());
    }

    public void cancel(Player player) {
        BukkitTask task = this.spamming.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    static {
        KEY = new NamespacedKey("kidaypisun", "kidaypisun");
        PERSISTENT_DATA_TYPE = createPersistentDataType();
    }

    @SuppressWarnings("Unchecked")
    private static PersistentDataType<BlockData, BlockData> createPersistentDataType() {
        try {
            Constructor<?> constructor = PersistentDataType.PrimitivePersistentDataType.class
                    .getDeclaredConstructor(Class.class);

            constructor.setAccessible(true);

            return (PersistentDataType<BlockData, BlockData>) constructor.newInstance(BlockData.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
