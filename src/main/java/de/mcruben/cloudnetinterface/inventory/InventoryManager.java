package de.mcruben.cloudnetinterface.inventory;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import com.google.gson.*;
import de.mcruben.cloudnetinterface.CloudNetInterface;
import de.mcruben.cloudnetinterface.inventory.def.MainInventory;
import de.mcruben.cloudnetinterface.utility.gui.GUIClickableItem;
import de.mcruben.cloudnetinterface.utility.gui.InventoryGUI;
import de.mcruben.cloudnetinterface.utility.gui.ItemBuilder;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

@Getter
public class InventoryManager {

    private final InventoryGUI mainGUI;
    private final Map<String, InventoryGUI> guis = new HashMap<>();
    private final Map<String, Items> items = new HashMap<>();

    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();

    public InventoryManager() {
        /*{
            try (InputStream inputStream = CloudNetInterface.class.getClassLoader().getResourceAsStream("files/items.json");
                 Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonObject jsonArray = this.parser.parse(reader).getAsJsonObject();
                jsonArray.entrySet().forEach(entry -> {
                    String name = entry.getKey();
                    JsonObject inv = entry.getValue().getAsJsonObject();
                    String title = inv.get("title").getAsString();
                    int size = inv.get("size").getAsInt();
                    JsonArray items = inv.get("items").getAsJsonArray();
                    Item[] itemStacks = new Item[items.size()];
                    int i = 0;
                    for (JsonElement jsonElement : items) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        Material type = Material.getMaterial(jsonObject.get("type").getAsString());
                        int amount = jsonObject.has("amount") ? jsonObject.get("amount").getAsInt() : 1;
                        short damage = jsonObject.has("damage") ? jsonObject.get("damage").getAsShort() : (short) 0;
                        ItemBuilder itemBuilder = ItemBuilder.builder(type, amount, damage);
                        if (jsonObject.has("displayName"))
                            itemBuilder.displayName(jsonObject.get("displayName").getAsString());
                        if (jsonObject.has("lore")) {
                            JsonArray loreArray = jsonObject.get("lore").getAsJsonArray();
                            List<String> lore = new ArrayList<>(loreArray.size());
                            for (JsonElement element : loreArray) {
                                lore.add(element.getAsString());
                            }
                            itemBuilder.lore(lore);
                        }
                        if (jsonObject.has("enchants")) {
                            JsonArray enchantments = jsonObject.get("enchantments").getAsJsonArray();
                            for (JsonElement enchantment : enchantments) {
                                JsonObject object = enchantment.getAsJsonObject();
                                itemBuilder.enchant(Enchantment.getByName(object.get("name").getAsString()), object.get("level").getAsInt());
                            }
                        }

                        int slot = jsonObject.get("slot").getAsInt();
                        itemStacks[i++] = new Item(slot, itemBuilder.build());
                    }
                    this.items.put(name, new Items(title, size, itemStacks));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        this.mainGUI = MainInventory.create();
    }

    private InventoryGUI loadGUI(String name) {
        Items items = this.items.get(name);
        if (items == null)
            return null;
        GUIClickableItem[] clickableItems = new GUIClickableItem[items.items.length];
        for (int i = 0; i < clickableItems.length; i++) {
            clickableItems[i] = new GUIClickableItem(event -> {

            }, items.items[i].itemStack, items.items[i].slot);
        }
        return InventoryGUI.createGUI(
                name,
                Bukkit.createInventory(null, items.size, items.title),
                clickableItems
        );
    }

    private Consumer<InventoryClickEvent> openGUIConsumer(InventoryGUI inventoryGUI) {
        return event -> inventoryGUI.open((Player) event.getWhoClicked());
    }

    @AllArgsConstructor
    private final class Items {
        private String title;
        private int size;
        private Item[] items;
    }

    @AllArgsConstructor
    private final class Item {
        private int slot;
        private ItemStack itemStack;
    }

}
