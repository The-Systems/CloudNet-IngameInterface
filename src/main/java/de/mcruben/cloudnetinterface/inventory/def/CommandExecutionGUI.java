package de.mcruben.cloudnetinterface.inventory.def;
/*
 * Created by Mc_Ruben on 11.12.2018
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.DefaultType;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.mcruben.cloudnetinterface.CloudNetInterface;
import lombok.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;

@ToString
@EqualsAndHashCode
public class CommandExecutionGUI {

    public CommandExecutionGUI(Player player, DefaultType defaultType, String serverId) {
        this.player = player;
        this.defaultType = defaultType;
        this.serverId = serverId;

        this.anvilGUI = new AnvilGUI(CloudNetInterface.getInstance(), player, "Command", (player1, s) -> {
            if (!player.getUniqueId().equals(player1.getUniqueId()))
                return "Command";

            CloudAPI.getInstance().sendConsoleMessage(this.defaultType, this.serverId, s);
            return "Command";
        });
    }

    public CommandExecutionGUI(Player player, ServerInfo serverInfo) {
        this(player, DefaultType.BUKKIT, serverInfo.getServiceId().getServerId());
    }

    public CommandExecutionGUI(Player player, ProxyInfo proxyInfo) {
        this(player, DefaultType.BUNGEE_CORD, proxyInfo.getServiceId().getServerId());
    }

    private DefaultType defaultType;
    private Player player;
    private String serverId;
    private AnvilGUI anvilGUI;

}
