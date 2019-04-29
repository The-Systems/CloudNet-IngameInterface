package de.mcruben.cloudnetinterface.inventory.def;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.mcruben.cloudnetinterface.inventory.SkullConstants;
import de.mcruben.cloudnetinterface.utility.gui.GUIClickableItem;
import de.mcruben.cloudnetinterface.utility.gui.InventoryGUI;
import de.mcruben.cloudnetinterface.utility.gui.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MainInventory {

    public static InventoryGUI create() {
        return InventoryGUI.createGUI(
                "main_gui",
                Bukkit.createInventory(null, 9 * 7, "§aCloudNet - Interface"),
                new GUIClickableItem(
                        event -> InventoryGUI.openGuiByName("servers", (Player) event.getWhoClicked()),
                        ItemBuilder.builder(Material.SKULL_ITEM, (short) SkullType.PLAYER.ordinal()).skull(SkullConstants.COMPUTER).displayName("§aServers").build(),
                        0
                ),
                new GUIClickableItem(
                        event -> InventoryGUI.openGuiByName("proxies", (Player) event.getWhoClicked()),
                        ItemBuilder.builder(Material.SKULL_ITEM, (short) SkullType.PLAYER.ordinal()).skull(SkullConstants.COMPUTER).displayName("§aProxies").build(),
                        1
                ),
                new GUIClickableItem(
                        event -> InventoryGUI.openGuiByName("servergroups", (Player) event.getWhoClicked()),
                        ItemBuilder.builder(Material.SKULL_ITEM, (short) SkullType.PLAYER.ordinal()).skull(SkullConstants.COMPUTER).displayName("§aServerGroups").build(),
                        7
                )
        );
    }
}
