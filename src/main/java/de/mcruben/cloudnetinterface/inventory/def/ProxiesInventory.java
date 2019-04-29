package de.mcruben.cloudnetinterface.inventory.def;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.mcruben.cloudnetinterface.inventory.SkullConstants;
import de.mcruben.cloudnetinterface.utility.gui.GUIClickableItem;
import de.mcruben.cloudnetinterface.utility.gui.InventoryGUI;
import de.mcruben.cloudnetinterface.utility.gui.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ProxiesInventory {

    private static int currentSlot = 0;
    private static InventoryGUI inventoryGUI = InventoryGUI.createGUI(
            "proxies",
            Bukkit.createInventory(null, 9 * 3, "§aCloudNet - Proxies")
    );

    public static InventoryGUI create() {
        return inventoryGUI;
    }

    public static void handleServerAdd(ProxyInfo serverInfo) {
        update(currentSlot++, serverInfo);
    }

    public static void handleServerRemove(ProxyInfo serverInfo) {
        int slot = findSlot(serverInfo);
        if (slot != -1) {
            inventoryGUI.removeItem(slot);
            currentSlot--;
        }
    }

    public static void handleServerUpdate(ProxyInfo serverInfo) {
        int slot = findSlot(serverInfo);
        if (slot != -1) {
            update(slot, serverInfo);
        } else {
            handleServerAdd(serverInfo);
        }
    }

    private static int findSlot(ProxyInfo serverInfo) {
        for (GUIClickableItem value : inventoryGUI.getItems().values()) {
            if (value.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(serverInfo.getServiceId().getServerId()))
                return value.getSlot();
        }
        return -1;
    }

    private static void update(int slot, ProxyInfo serverInfo) {
        inventoryGUI.onClick(
                slot,
                ItemBuilder.builder(Material.EMERALD).displayName(serverInfo.getServiceId().getServerId())
                        .lore(
                                "§e ➤ " + serverInfo.getServiceId().getServerId(),
                                "§e Players ➤ " + serverInfo.getOnlineCount(),
                                "§e Wrapper ➤ " + serverInfo.getServiceId().getWrapperId(),
                                "§e GameId ➤ " + serverInfo.getServiceId().getGameId(),
                                "§e Host ➤ @" + serverInfo.getHost() + ":" + serverInfo.getPort()
                        ),
                event -> {
                    String name = NetworkUtils.randomString(32);
                    InventoryGUI inventoryGUI = InventoryGUI.createGUI(
                            name,
                            Bukkit.createInventory(null, 9, "§e" + serverInfo.getServiceId().getServerId()),
                            new GUIClickableItem(
                                    event1 -> {
                                        CloudAPI.getInstance().stopProxy(serverInfo.getServiceId().getServerId());
                                        event1.getWhoClicked().closeInventory();
                                    },
                                    ItemBuilder.builder(Material.BARRIER).displayName("§cStop proxy").build(),
                                    4
                            ),
                            new GUIClickableItem(
                                    event1 -> {
                                        new CommandExecutionGUI((Player) event.getWhoClicked(), serverInfo);
                                    },
                                    ItemBuilder.builder(Material.SKULL_ITEM, (short) 3).skull(SkullConstants.COMMAND_BLOCK).displayName("§cCommands").build(),
                                    3
                            )
                    );
                    inventoryGUI.onClose(player -> inventoryGUI.unregister())
                            .open((Player) event.getWhoClicked());
                }
        );
    }

}
