package de.mcruben.cloudnetinterface.inventory.def;
/*
 * Created by Mc_Ruben on 30.11.2018
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.template.Template;
import de.mcruben.cloudnetinterface.CloudNetInterface;
import de.mcruben.cloudnetinterface.utility.gui.GUIClickableItem;
import de.mcruben.cloudnetinterface.utility.gui.InventoryGUI;
import de.mcruben.cloudnetinterface.utility.gui.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.function.Function;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ServerGroupsInventory {

    private static int currentSlot = 0;
    private static InventoryGUI inventoryGUI = InventoryGUI.createGUI(
            "servergroups",
            Bukkit.createInventory(null, 9 * 3, "§aCloudNet - Servers")
    );

    public static void handleServerAdd(ServerGroup serverInfo) {
        update(currentSlot++, serverInfo);
    }

    public static void handleServerRemove(ServerGroup serverInfo) {
        int slot = findSlot(serverInfo);
        if (slot != -1) {
            inventoryGUI.removeItem(slot);
            currentSlot--;
        }
    }

    private static int findSlot(ServerGroup serverInfo) {
        for (GUIClickableItem value : inventoryGUI.getItems().values()) {
            if (value.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(serverInfo.getName()))
                return value.getSlot();
        }
        return -1;
    }

    private static void update(int slot, ServerGroup serverInfo) {
        inventoryGUI.onClick(
                slot,
                ItemBuilder.builder(Material.EMERALD).displayName(serverInfo.getName())
                        .lore(
                                "§e ➤ " + serverInfo.getName(),
                                "§e Servers ➤ " + CloudNetInterface.getInstance().getOnlineServers(serverInfo.getName()),
                                "§e Wrapper ➤ " + toString(serverInfo.getWrapper(), s -> s),
                                "§e Memory ➤ " + serverInfo.getMemory(),
                                "§e DynamicMemory ➤ " + serverInfo.getDynamicMemory(),
                                "§e GroupMode ➤ " + serverInfo.getGroupMode(),
                                "§e JoinPower ➤ " + serverInfo.getJoinPower(),
                                "§e MinOnlineServers ➤ " + serverInfo.getMinOnlineServers(),
                                "§e MaxOnlineServers ➤ " + serverInfo.getMaxOnlineServers(),
                                "§e Templates ➤ " + toString(serverInfo.getTemplates(), Template::getName)
                        ),
                event -> {
                    String name = NetworkUtils.randomString(32);
                    InventoryGUI inventoryGUI = InventoryGUI.createGUI(
                            name,
                            Bukkit.createInventory(null, 9, "§e" + serverInfo.getName()),
                            new GUIClickableItem(
                                    event1 -> {
                                        CloudNetInterface.getInstance().getServers(serverInfo.getName())
                                                .forEach(info -> CloudAPI.getInstance().stopServer(info.getServiceId().getServerId()));
                                        event1.getWhoClicked().closeInventory();
                                    },
                                    ItemBuilder.builder(Material.BARRIER).displayName("§cStop group").build(),
                                    8
                            ),
                            new GUIClickableItem(
                                    event1 -> {
                                        CloudAPI.getInstance().startGameServer(serverInfo.toSimple());
                                    },
                                    ItemBuilder.builder(Material.WOOL, (short) 5).displayName("§aStart server").build(),
                                    0
                            )
                    );
                    inventoryGUI.onClose(player -> InventoryGUI.getGuiByName(name).unregister())
                            .open((Player) event.getWhoClicked());
                }
        );
    }

    private static <T> String toString(Collection<T> collection, Function<T, String> function) {
        StringBuilder builder = new StringBuilder();
        collection.forEach(t -> {
            builder.append(function.apply(t)).append(", ");
        });
        return collection.isEmpty() ? "" : builder.substring(0, builder.length() - 2);
    }

}
