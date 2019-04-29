package de.mcruben.cloudnetinterface.utility.gui;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import com.google.common.base.Preconditions;
import de.mcruben.cloudnetinterface.utility.countdown.SimpleCountdown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AnimatedInventoryGUI extends InventoryGUI {

    private Inventory[] layers;
    private long openTimePerTime;
    private long openTimes;
    private Plugin plugin;
    private BiConsumer<Player, Integer> onAnimationPlayed;

    private AnimatedInventoryGUI() { }

    public static AnimatedInventoryGUI createGUI(Plugin plugin, String name, Inventory[] layers, long openTimePerTime, long openTimes, ItemStack fillItem) {
        Preconditions.checkArgument(layers.length >= 1, "layers.length must be greater or equal than 1");
        Preconditions.checkArgument(openTimes >= layers.length, "openTimes must be greater or equal than layers.length");

        AnimatedInventoryGUI inventoryGUI = new AnimatedInventoryGUI();
        inventoryGUI.plugin = plugin;
        inventoryGUI.name = name;
        inventoryGUI.layers = layers;
        inventoryGUI.openTimes = openTimes;
        inventoryGUI.openTimePerTime = openTimePerTime;

        int size = layers[0].getSize();
        for (Inventory layer : layers) {
            Preconditions.checkArgument(size == layer.getSize(), "size must be exactly the same on each layer");
            if (fillItem != null && fillItem.getType() != null && !fillItem.getType().equals(Material.AIR)) {
                int i = 0;
                for (ItemStack itemStack : layer.getContents()) {
                    if (itemStack == null || itemStack.getType() == null || itemStack.getType().equals(Material.AIR)) {
                        layer.setItem(i, fillItem);
                    }
                    i++;
                }
            }
        }

        registeredGuis.put(name, inventoryGUI);

        return inventoryGUI;
    }

    public static AnimatedInventoryGUI createGUI(Plugin plugin, String name, Inventory[] layers, long openTimePerTime, long openTimes) {
        return createGUI(plugin, name, layers, openTimePerTime, openTimes, ItemBuilder.builder(Material.STAINED_GLASS_PANE, (short) 7).displayName(" ").build());
    }

    public static AnimatedInventoryGUI createGUI(Plugin plugin, String name, Inventory[] layers, long openTimePerTime) {
        return createGUI(plugin, name, layers, openTimePerTime, layers.length, ItemBuilder.builder(Material.STAINED_GLASS_PANE, (short) 7).displayName(" ").build());
    }

    public static AnimatedInventoryGUI createSideComingGUI(Plugin plugin, String name, Inventory inventory, ItemStack fillItem) {
        Inventory[] layers = new Inventory[9 - findFirstLayerVertical(inventory, fillItem)];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = Bukkit.createInventory(null, inventory.getSize(), inventory.getTitle());
            Inventory layer = layers[i];
            for (int j = 0; j < layer.getSize(); j++) {
                int newSlot = newSlot(inventory.getSize(), j, i);
                if (newSlot != -1) {
                    layer.setItem(newSlot, inventory.getItem(j));
                }
            }
        }

        long openTimePerTime = 1;
        long openTimes = layers.length;

        Inventory[] newLayers = new Inventory[layers.length];

        int a = 0;
        for (int i = layers.length - 1; i >= 0; i--) {
            newLayers[i] = layers[a++];
        }

        newLayers[layers.length - 1] = inventory;

        return createGUI(plugin, name, newLayers, openTimePerTime, openTimes);
    }

    @Override
    public AnimatedInventoryGUI onClose(Consumer<Player> consumer) {
        super.onClose(consumer);
        return this;
    }

    public AnimatedInventoryGUI onAnimationPlayed(BiConsumer<Player, Integer> onAnimationPlayed) {
        this.onAnimationPlayed = onAnimationPlayed;
        return this;
    }

    private static int findFirstLayerVertical(Inventory inventory, ItemStack fillItem) {
        int i = 0;
        int j;
        int a = -1;
        for (ItemStack itemStack : inventory.getContents()) {
            j = i % 9;
            if (itemStack != null && itemStack.getType() != null && !itemStack.getType().equals(Material.AIR) && !itemStack.equals(fillItem)) {
                if (j < a || a == -1)
                    a = j;
            }
            i++;
        }
        if (a == -1)
            return 0;
        return a;
    }

    private static int newSlot(int size, int from, int right) {
        if (from == 27 || (from + right) / 9 != from / 9)
            return -1;
        return from + right;
    }

    public static AnimatedInventoryGUI createSideComingGUI(Plugin plugin, String name, Inventory inventory) {
        return createSideComingGUI(plugin, name, inventory, ItemBuilder.builder(Material.STAINED_GLASS_PANE, (short) 7).displayName(" ").build());
    }

    public AnimatedInventoryGUI onClick(int slot, Consumer<InventoryClickEvent> consumer) {
        return this.onClick(new GUIClickableItem(consumer, null, slot));
    }

    @Override
    public AnimatedInventoryGUI onClick(GUIClickableItem item) {
        this.items.put(item.getSlot(), item);
        return this;
    }

    @Override
    public AnimatedInventoryGUI onClick(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> consumer) {
        return this.onClick(slot, consumer);
    }

    @Override
    public AnimatedInventoryGUI onClick(int slot, ItemBuilder itemBuilder, Consumer<InventoryClickEvent> consumer) {
        return this.onClick(slot, consumer);
    }

    public ItemStack[] getLayer(int index) {
        return index >= layers.length ? new ItemStack[0] : layers[index].getContents();
    }

    @Override
    protected boolean checkEqualInventory(Inventory inventory) {
        return layers[0].getTitle().equals(inventory.getTitle()) && layers[0].getSize() == inventory.getSize();
    }

    private void fillLayer(int index, Inventory inventory) {
        int i = 0;
        for (ItemStack itemStack : getLayer(index)) {
            inventory.setItem(i++, itemStack);
        }
    }

    @Override
    public void open(Player player) {
        AtomicInteger integer = new AtomicInteger(0);
        Inventory inventory = Bukkit.createInventory(null, layers[0].getSize(), layers[0].getTitle());
        fillLayer(0, inventory);
        player.openInventory(inventory);
        new SimpleCountdown(this.plugin, 0, openTimePerTime, openTimes, () -> {
            int i = integer.getAndIncrement();
            fillLayer(i, inventory);
            if (onAnimationPlayed != null)
                onAnimationPlayed.accept(player, i);
        });
    }
}
