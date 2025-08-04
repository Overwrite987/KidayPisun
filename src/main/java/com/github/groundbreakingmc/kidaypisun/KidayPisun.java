package com.github.groundbreakingmc.kidaypisun;

import com.github.groundbreakingmc.kidaypisun.commands.CommandManager;
import com.github.groundbreakingmc.kidaypisun.listeners.DisconectListener;
import com.github.groundbreakingmc.kidaypisun.listeners.FallingBlackChangeListener;
import com.github.groundbreakingmc.kidaypisun.utils.config.ConfigValues;
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
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class KidayPisun extends JavaPlugin {

    public static final NamespacedKey KEY;
    public static final PersistentDataType<BlockData, BlockData> PERSISTENT_DATA_TYPE;

    private final ConfigValues configValues;
    private final Map<UUID, BukkitTask> spamming;

    public KidayPisun() {
        this.configValues = new ConfigValues(this);
        this.spamming = new IdentityHashMap<>();
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
        final PluginCommand pisunCommand = super.getCommand("pisun");
        final TabExecutor executor = new CommandManager(this);
        pisunCommand.setExecutor(executor);
    }

    private void registerEvent(final Listener listener) {
        super.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void addTask(final Player player, final BukkitTask task) {
        this.spamming.put(player.getUniqueId(), task);
    }

    public BukkitTask removeTask(final Player player) {
        return this.spamming.remove(player.getUniqueId());
    }

    public void cancel(final Player player) {
        final BukkitTask task = this.spamming.remove(player.getUniqueId());
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
            final Constructor<?> constructor = PersistentDataType.PrimitivePersistentDataType.class
                    .getDeclaredConstructor(Class.class);

            constructor.setAccessible(true);

            return (PersistentDataType<BlockData, BlockData>) constructor.newInstance(BlockData.class);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
