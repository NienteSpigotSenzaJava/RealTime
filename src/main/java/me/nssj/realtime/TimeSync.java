package me.nssj.realtime;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeSync extends BukkitRunnable {

    private final Plugin plugin;

    public TimeSync(Plugin plugin) {

        this.plugin = plugin;

    }


    @Override
    public void run() {

        FileConfiguration config = plugin.getConfig();

        ZoneId timezone = ZoneId.of(config.getString("timezone"));
        ZonedDateTime now = ZonedDateTime.now(timezone);

        Instant midnight = now.toLocalDate().atStartOfDay(timezone).toInstant();
        Duration duration = Duration.between(midnight, Instant.now());

        long seconds = (duration.getSeconds() * 24000) / 86400;

        for (String worldName : config.getStringList("worlds")) {

            World world = plugin.getServer().getWorld(worldName);

            if (world != null) {

                world.setTime(seconds < 6000 ? seconds + 18000 : seconds - 6000);

            }

        }

    }

}
