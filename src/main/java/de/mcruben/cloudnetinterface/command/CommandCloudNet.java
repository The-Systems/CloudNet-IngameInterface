package de.mcruben.cloudnetinterface.command;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.mcruben.cloudnetinterface.CloudNetInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCloudNet implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command");
            return false;
        }

        if (!sender.hasPermission("cloudnet.interface.open")) {
            sender.sendMessage("&cYou don't have permission to use this command");
            return false;
        }

        CloudNetInterface.getInstance().getInventoryManager().getMainGUI().open((Player) sender);

        return false;
    }
}
