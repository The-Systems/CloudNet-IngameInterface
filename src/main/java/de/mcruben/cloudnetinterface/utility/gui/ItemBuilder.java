package de.mcruben.cloudnetinterface.utility.gui;
/*
 * Created by Mc_Ruben on 29.10.2018
 */

import com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder displayName(String displayName) {
        meta().setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        meta().setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        meta().setLore(lore);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta().addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder itemFlag(ItemFlag... flags) {
        meta().addItemFlags(flags);
        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder skull(GameProfile gameProfile) {
        ItemMeta itemMeta = meta();
        if (itemMeta instanceof SkullMeta) {

            try {
                Field field = itemMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(itemMeta, gameProfile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    private ItemMeta meta() {
        if (this.itemMeta == null)
            this.itemMeta = this.itemStack.getItemMeta();
        return this.itemMeta;
    }

    public ItemStack build() {
        if (this.itemMeta != null)
            this.itemStack.setItemMeta(this.itemMeta);
        return this.itemStack;
    }

    public static ItemBuilder builder(Material material, int amount, short damage, byte data) {
        return new ItemBuilder(new ItemStack(material, amount, damage, data));
    }

    public static ItemBuilder builder(Material material, int amount, short damage) {
        return new ItemBuilder(new ItemStack(material, amount, damage));
    }

    public static ItemBuilder builder(Material material, int amount, byte data) {
        return new ItemBuilder(new ItemStack(material, amount, data));
    }

    public static ItemBuilder builder(Material material, int amount) {
        return new ItemBuilder(new ItemStack(material, amount));
    }

    public static ItemBuilder builder(Material material, byte data) {
        return new ItemBuilder(new ItemStack(material, 1, (short) 0, data));
    }

    public static ItemBuilder builder(Material material, short damage) {
        return new ItemBuilder(new ItemStack(material, 1, damage));
    }

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

}
