package de.mcruben.cloudnetinterface.listener;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.mcruben.cloudnetinterface.utility.gui.InventoryGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIListeners implements Listener {

    @EventHandler
    public void handleInventoryClose(InventoryCloseEvent event) {
        InventoryGUI.onInventoryClose(event);
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        InventoryGUI.onInventoryClick(event);
    }

}
