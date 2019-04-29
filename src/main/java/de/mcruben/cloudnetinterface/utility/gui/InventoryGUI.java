package de.mcruben.cloudnetinterface.utility.gui;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryGUI {

    InventoryGUI() { }

    protected static final Map<String, InventoryGUI> registeredGuis = new HashMap<>();

    String name;
    List<InventoryImpl> inventories = new ArrayList<>();
    @Getter
    Map<Integer, GUIClickableItem> items = new HashMap<>();
    Consumer<Player> onClose;
    private boolean registered = true;

    public static InventoryGUI createGUI(String name, Inventory inventory, GUIClickableItem... items) {
        InventoryGUI inventoryGUI = new InventoryGUI();
        inventoryGUI.name = name;
        inventoryGUI.inventories.add(new InventoryImpl(inventory, 0));

        for (GUIClickableItem item : items) {
            inventoryGUI.onClick(item);
        }

        registeredGuis.put(name, inventoryGUI);

        return inventoryGUI;
    }

    public void unregister() {
        registered = false;
        this.inventories.forEach(inventory -> {
            new ArrayList<>(inventory.inventory.getViewers()).forEach(HumanEntity::closeInventory);
        });
        this.inventories.clear();
        this.items.clear();
        registeredGuis.remove(this.name);
    }

    public InventoryGUI onClose(Consumer<Player> consumer) {
        this.onClose = consumer;
        return this;
    }

    public void open(Player player) {
        player.openInventory(this.inventories.get(0));
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public InventoryGUI onClick(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        return this.onClick(new GUIClickableItem(consumer, itemStack, slot));
    }

    public InventoryGUI onClick(int slot, ItemBuilder itemBuilder, Consumer<InventoryClickEvent> consumer) {
        return this.onClick(slot, itemBuilder.build(), consumer);
    }

    public InventoryGUI onClick(GUIClickableItem item) {
        int id = 0;
        InventoryImpl inventory = this.inventories.get(id++);
        while (inventory != null && this.inventories.size() >= id && item.getSlot() >= inventory.inventory.getSize()) {
            inventory = this.inventories.get(id++);
            ItemStack nextPage = inventory.inventory.getItem(inventory.inventory.getSize() - 1);
            ItemStack lastPage = inventory.inventory.getItem(inventory.inventory.getSize() - 9);

        }
        if (inventory == null)
            return this;
        this.items.put(item.getSlot(), item);
        this.inventory.setItem(item.getSlot(), item.getItemStack());
        return this;
    }

    public InventoryGUI removeItem(int slot) {
        this.items.remove(slot);
        this.inventory.setItem(slot, null);
        return this;
    }

    public ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    public static InventoryGUI getGuiByName(String name) {
        return registeredGuis.get(name);
    }

    public static boolean openGuiByName(String name, Player player) {
        InventoryGUI gui = getGuiByName(name);
        if (gui == null)
            return false;
        gui.open(player);
        return true;
    }

    private ItemStack createNextPageItem() {

    }

    private ItemStack createLastPageItem() {

    }

    protected boolean checkEqualInventory(Inventory inventory) {
        return inventory.equals(this.inventory);
    }

    public static void onInventoryClick(InventoryClickEvent event) {
        if (!registeredGuis.isEmpty() && event.getWhoClicked() instanceof Player && event.getClickedInventory() != null && event.getCurrentItem() != null) {
            for (InventoryGUI inventoryGUI : registeredGuis.values()) {
                if (inventoryGUI.checkEqualInventory(event.getClickedInventory())) {
                    event.setCancelled(true);
                    GUIClickableItem item = inventoryGUI.items.get(event.getRawSlot());
                    if (item != null) {
                        item.getConsumer().accept(event);
                    }
                    break;
                }
            }
        }
    }

    public static void onInventoryClose(InventoryCloseEvent event) {
        if (!registeredGuis.isEmpty() && event.getPlayer() instanceof Player && event.getInventory() != null) {
            for (InventoryGUI inventoryGUI : registeredGuis.values()) {
                if (inventoryGUI.registered && inventoryGUI.onClose != null && inventoryGUI.checkEqualInventory(event.getInventory())) {
                    inventoryGUI.onClose.accept((Player) event.getPlayer());
                    break;
                }
            }
        }
    }

    @AllArgsConstructor
    private final static class InventoryImpl {
        private Inventory inventory;
        private int id;
    }

}
