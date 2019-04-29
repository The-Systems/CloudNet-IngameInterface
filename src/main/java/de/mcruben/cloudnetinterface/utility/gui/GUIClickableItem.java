package de.mcruben.cloudnetinterface.utility.gui;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GUIClickableItem {
    private Consumer<InventoryClickEvent> consumer;
    private ItemStack itemStack;
    private int slot;
}
