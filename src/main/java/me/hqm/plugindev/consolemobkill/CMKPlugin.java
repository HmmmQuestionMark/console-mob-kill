package me.hqm.plugindev.consolemobkill;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Iterator;

public class CMKPlugin extends JavaPlugin implements CommandExecutor {
    @Override
    public void onEnable() {
        getCommand("mobkill").setExecutor(this);
        getCommand("mobtypes").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            if (command.getName().equalsIgnoreCase("mobkill")) {
                if (args.length > 0) {
                    int killCount = 0;
                    EntityType typeToKill = getType(args[0]);
                    if (typeToKill != null) {
                        if (args.length == 1) {
                            for (World world : Bukkit.getWorlds()) {
                                killCount += killEntities(typeToKill, world);
                            }
                        } else {
                            World world = Bukkit.getWorld(args[1]);
                            if (world != null) {
                                killCount += killEntities(typeToKill, world);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Not a valid world.");
                                return false;
                            }
                        }
                        getLogger().info(ChatColor.YELLOW + "Removed " + killCount + " " + typeToKill.getName() + " entities.");
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Not a valid mob type. Use /mobtypes for a list.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Not enough arguments.");
                }
            } else if (command.getName().equalsIgnoreCase("mobtypes")) {
                StringBuilder builder = new StringBuilder();
                Iterator<EntityType> it = Arrays.asList(EntityType.values()).iterator();

                EntityType next = it.next();
                while(it.hasNext()) {
                    if(next.getName() != null) {
                        builder.append(ChatColor.YELLOW).append(next.getName());
                    }
                    if(it.hasNext()) {
                        next = it.next();
                        if(next.getName() != null) {
                            builder.append(ChatColor.WHITE).append(", ");
                        }
                    }
                }

                sender.sendMessage(ChatColor.GOLD + "Entity types: ");
                sender.sendMessage(ChatColor.YELLOW + builder.toString());
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command is for the console only.");
        }

        return false;
    }

    private EntityType getType(String name) {
        try {
            return EntityType.fromName(name);
        } catch (Exception ignored) {
        }
        return null;
    }

    private int killEntities(EntityType type, World world) {
        int killCount = 0;
        for(Entity entity : world.getEntities()) {
            if(type.equals(entity.getType())) {
                entity.remove();
                killCount++;
            }
        }
        return killCount;
    }
}
