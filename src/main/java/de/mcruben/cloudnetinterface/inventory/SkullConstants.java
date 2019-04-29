package de.mcruben.cloudnetinterface.inventory;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import de.dytanic.cloudnet.lib.NetworkUtils;
import lombok.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class SkullConstants {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GameProfile.class, new GameProfileSerializer())
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
            .create();

    public static final GameProfile COMPUTER = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRhYWU3NmVmYzhjNTg3MjhiYjA2ZTg2NTJlMGE3ODNjZjYxNGJiNmI0ZTNhYmM0MThlNDM5OTM3Njg1ZTQ0In19fQ==");
    public static final GameProfile COMMAND_BLOCK = create("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ==");


    public static GameProfile create(String value) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), NetworkUtils.randomString(12));
        gameProfile.getProperties().put("textures", new Property(gameProfile.getName(), value));
        return gameProfile;
    }

    public static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {

        public GameProfile deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
            JsonObject object = (JsonObject) json;
            UUID id = object.has("id") ? (UUID) context.deserialize(object.get("id"), UUID.class) : null;
            String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            GameProfile profile = new GameProfile(id, name);

            if (object.has("properties")) {
                for (Map.Entry<String, Property> prop : ((PropertyMap) context.deserialize(object.get("properties"), PropertyMap.class)).entries()) {
                    profile.getProperties().put(prop.getKey(), prop.getValue());
                }
            }
            return profile;
        }

        public JsonElement serialize(GameProfile profile, Type type, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            if (profile.getId() != null)
                result.add("id", context.serialize(profile.getId()));
            if (profile.getName() != null)
                result.addProperty("name", profile.getName());
            if (!profile.getProperties().isEmpty())
                result.add("properties", context.serialize(profile.getProperties()));
            return result;
        }

    }

}
