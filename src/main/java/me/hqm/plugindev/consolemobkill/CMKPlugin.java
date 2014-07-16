// The MIT License (MIT)
//
// Copyright © 2014 Alexander Chauncey (aka HmmmQuestionMark)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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

/**
 * Console Mob Kill (kill mobs of a certain type from the console).
 */
@SuppressWarnings("deprecation")
public class CMKPlugin extends JavaPlugin implements CommandExecutor {
    /**
     * Enable the plugin and register the commands.
     */
    @Override
    public void onEnable() {
        // Enable commands
        getCommand("mobkill").setExecutor(this);
        getCommand("mobtypes").setExecutor(this);
    }

    /**
     * Bukkit command executor method.
     *
     * @param sender  The command sender.
     * @param command The command itself.
     * @param label   The label/alias being used.
     * @param args    The arguments following the command.
     * @return The command executed correctly, or not.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // These commands are console commands only
        if (sender instanceof ConsoleCommandSender) {
            // Command: /mobkill <type> [world]
            if (command.getName().equalsIgnoreCase("mobkill")) {
                // This command needs arguments
                if (args.length > 0) {
                    // Start the killcount
                    int killCount = 0;

                    // Get the entity type
                    EntityType typeToKill = EntityType.fromName(args[0]);

                    // Make sure the type isn't null (actually exists)
                    if (typeToKill != null) {
                        // No world specified
                        if (args.length == 1) {
                            // Iterate over every world
                            for (World world : Bukkit.getWorlds()) {
                                // Kill all entities of the type in this world
                                killCount += killEntities(typeToKill, world);
                            }
                            // One world specified
                        } else {
                            // Get the world
                            World world = Bukkit.getWorld(args[1]);

                            // Make sure the world exists
                            if (world != null) {
                                // Kill all entities of the type in this world
                                killCount += killEntities(typeToKill, world);
                            } else {
                                // Oops, no world by that name
                                sender.sendMessage(ChatColor.RED + "Not a valid world.");
                                return false;
                            }
                        }
                        // Let the log know that this plugin killed some entities
                        getLogger().info("Removed " + killCount + " \'" + typeToKill.getName() + "\' entities.");
                        return true;
                    } else {
                        // Oops, no entity type known by that name.
                        sender.sendMessage(ChatColor.RED + "Not a valid mob type. Use /mobtypes for a list.");
                    }
                } else {
                    // Oops, no arguments given
                    sender.sendMessage(ChatColor.RED + "Not enough arguments.");
                }
                // Command: /mobtypes
            } else if (command.getName().equalsIgnoreCase("mobtypes")) {
                // Start a string builder for the list of types
                StringBuilder builder = new StringBuilder();

                // Define an iterator of the different entity types
                Iterator<EntityType> it = Arrays.asList(EntityType.values()).iterator();

                // Declare a 'next' variable for use in the while loop
                EntityType next = it.next();
                while (it.hasNext()) {
                    // If this type has a name, add it to the list
                    if (next.getName() != null) {
                        builder.append(ChatColor.YELLOW).append(next.getName());
                    }
                    // If there are more after this type...
                    if (it.hasNext()) {
                        // Set the next 'next' type
                        next = it.next();
                        // If the next 'next' type has a name add a comma
                        if (next.getName() != null) {
                            builder.append(ChatColor.WHITE).append(", ");
                        }
                    }
                }

                // Print the list out to the sender
                sender.sendMessage(ChatColor.GOLD + "Entity types: ");
                sender.sendMessage(ChatColor.YELLOW + builder.toString());
                return true;
            }
        } else {
            // Oops, this command is for the console only
            sender.sendMessage(ChatColor.RED + "This command is for the console only.");
        }

        // Something went wrong if it doesn't return before here
        return false;
    }

    /**
     * Kill all entities in a world of a specific type.
     *
     * @param type  The entity type.
     * @param world The world.
     * @return The number of entities killed.
     */
    private int killEntities(EntityType type, World world) {
        // Start a killcount
        int killCount = 0;

        // Iterate over all of the entities in the world
        for (Entity entity : world.getEntities()) {
            // If the type matches, remove it (and add to killcount)
            if (type.equals(entity.getType())) {
                entity.remove();
                killCount++;
            }
        }

        // Return the number of kills made
        return killCount;
    }
}
