package me.nssj.realtime;

import me.nssj.realtime.commands.RealTimeCommand;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public final class RealTime extends JavaPlugin {

    private static FileConfiguration config;
    private ConsoleCommandSender console;
    private static int taskId;

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        console = getServer().getConsoleSender();
        config = getConfig();

        console.sendMessage("§6╔═══════════════════════════════════════════════════╗");
        console.sendMessage("§6║                  §aRealTime enabled                 §6║");
        console.sendMessage("§6║ §bhttps://github.com/NienteSpigotSenzaJava/RealTime §6║");
        console.sendMessage("§6╚═══════════════════════════════════════════════════╝");

        getCommand("rtime").setExecutor(new RealTimeCommand(this));

        if (config.getBoolean("activated")) {

            startTask(this);

        }

    }

    @Override
    public void onDisable() {

        console.sendMessage("§6╔═══════════════════════════════════════════════════╗");
        console.sendMessage("§6║                 §cRealTime disabled                 §6║");
        console.sendMessage("§6║ §bhttps://github.com/NienteSpigotSenzaJava/RealTime §6║");
        console.sendMessage("§6╚═══════════════════════════════════════════════════╝");

    }

    public static void startTask(Plugin plugin) {

        BukkitRunnable timeSync = new TimeSync(plugin);
        BukkitTask task = timeSync.runTaskTimer(plugin, 0L, 1200L);
        taskId = task.getTaskId();

        for (String worldName : config.getStringList("worlds")) {

            World world = plugin.getServer().getWorld(worldName);

            if (world != null) {

                if (world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {

                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

                }

            }

        }

    }

    public static void cancelTask(Plugin plugin) {

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.cancelTask(taskId);

        for (String worldName : config.getStringList("worlds")) {

            World world = plugin.getServer().getWorld(worldName);

            if (world != null) {

                if (!world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {

                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

                }

            }

        }

    }

    public static void restartTask(Plugin plugin) {

        cancelTask(plugin);
        startTask(plugin);

    }

}
