package org.qpneruy.clashArena.skin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.qpneruy.clashArena.utils.ClashArenaLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static org.qpneruy.clashArena.skin.SkullUtils.getSkullByBase64EncodedTextureUrl;
import static org.qpneruy.clashArena.skin.SkullUtils.getSkullByName;

public class ElybySkin {
    private static final Gson gson = new Gson();

    public static CompletableFuture<String> getSignature(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("http://skinsystem.ely.by/profile/" + playerName);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);
                if (jsonObject == null) return null;
                JsonArray properties = jsonObject.getAsJsonArray("properties");
                for (JsonElement property : properties) {
                    JsonObject propertyObject = property.getAsJsonObject();
                    if (propertyObject.get("name").getAsString().equals("textures")) {
                        return propertyObject.get("value").getAsString();
                    }
                }
            } catch (Exception e) {
                ClashArenaLogger.printStacktrace("Failed to get skin signature", e);
            }
            return null;
        });
    }

    public static CompletableFuture<ItemStack> getPlayerHead(String playerName, String prefix) {
        return getSignature(playerName)
                .thenApply(base64 -> {
                    ItemStack is;
                    if (base64 == null) {
                        is = getSkullByName(playerName);
                    } else {
                        is = getSkullByBase64EncodedTextureUrl(base64);
                    }
                    return changeDisplayerName(is, prefix + playerName);
                });
    }

    private static ItemStack changeDisplayerName(ItemStack is, String displayerName) {
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.displayName(Component.text(displayerName));
        is.setItemMeta(meta);
        return is;
    }

    private static String getSkullSignature(SkullMeta meta) {
        try {
            JsonObject texture = MojangSkin.generateFromURL(String.valueOf(meta.getPlayerProfile().getTextures().getSkin()), false);
            JsonObject texture1 = texture.getAsJsonObject("texture");
            String value = texture1.get("value").getAsString();
            return value;
        } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
            System.out.println(e);
        }
        return null;
    }
}