package me.nssj.realtime.commands;

import me.nssj.realtime.RealTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RealTimeCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;

    public RealTimeCommand(Plugin plugin) {

        this.plugin = plugin;

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        FileConfiguration config = plugin.getConfig();

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length == 1) {

                if (args[0].equals("activate")) {

                    if (config.getBoolean("activated")) {

                        player.sendMessage(config.getString("messages.already_activated"));

                    } else {

                        config.set("activated", true);
                        RealTime.startTask(plugin);

                        player.sendMessage(config.getString("messages.activated"));

                    }

                } else if (args[0].equals("deactivate")) {

                    if (!config.getBoolean("activated")) {

                        player.sendMessage(config.getString("messages.already_deactivated"));

                    } else {

                        config.set("activated", false);
                        RealTime.cancelTask(plugin);

                        player.sendMessage(config.getString("messages.deactivated"));

                    }

                } else if (args[0].equals("timezone")) {

                    player.sendMessage(config.getString("messages.current_timezone").replace("%timezone%", config.getString("timezone")));

                } else if (args[0].equals("time")) {

                    ZoneId timezone = ZoneId.of(config.getString("timezone"));
                    ZonedDateTime now = ZonedDateTime.now(timezone);

                    player.sendMessage(config.getString("messages.current_time").replace("%time%", now.toLocalTime().format(DateTimeFormatter.ofPattern("H:m:s"))).replace("%timezone%", config.getString("timezone")));

                }

            } else if (args.length == 2) {

                if (args[0].equals("timezone")) {

                    if (!ZoneId.getAvailableZoneIds().contains(args[1])) {

                        player.sendMessage(config.getString("messages.wrong_timezone").replace("%timezone%", config.getString("timezone")));

                    } else {

                        config.set("timezone", args[1]);
                        RealTime.restartTask(plugin);

                        player.sendMessage(config.getString("messages.timezone_updated").replace("%timezone%", config.getString("timezone")));

                    }

                }

            } else {

                player.sendMessage(config.getString("messages.wrong_syntax"));

            }

        }

        return true;

    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {

        if (args.length == 1) {

            return StringUtil.copyPartialMatches(args[0], Arrays.asList("activate", "deactivate", "time", "timezone"), new ArrayList<>());

        } else if (args.length == 2 && args[0].equalsIgnoreCase("timezone")) {

            return StringUtil.copyPartialMatches(args[1], ZoneId.getAvailableZoneIds(), new ArrayList<>());

        }

        return null;

    }

}
