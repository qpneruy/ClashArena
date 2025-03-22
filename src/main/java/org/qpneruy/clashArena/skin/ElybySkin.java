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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.qpneruy.clashArena.skin.SkullUtils.getSkullByBase64EncodedTextureUrl;
import static org.qpneruy.clashArena.skin.SkullUtils.getSkullByName;

public class ElybySkin {
    private static final Gson GSON = new Gson();
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 5000;


    private static final Map<String, CachedSignature> SIGNATURE_CACHE = new ConcurrentHashMap<>();

    private static class CachedSignature {
        final String value;
        final long expirationTime;

        CachedSignature(String value) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    public static CompletableFuture<String> getSignature(String playerName) {
        // Check cache first
        CachedSignature cached = SIGNATURE_CACHE.get(playerName);
        if (cached != null && !cached.isExpired()) {
            return CompletableFuture.completedFuture(cached.value);
        }

        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://skinsystem.ely.by/profile/" + playerName);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    ClashArenaLogger.info("Failed to get skin: HTTP " + responseCode);
                    return null;
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {

                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }

                    JsonObject jsonObject = GSON.fromJson(content.toString(), JsonObject.class);
                    if (jsonObject == null) return null;

                    JsonArray properties = jsonObject.getAsJsonArray("properties");
                    if (properties == null) return null;

                    for (JsonElement property : properties) {
                        JsonObject propertyObject = property.getAsJsonObject();
                        if ("textures".equals(propertyObject.get("name").getAsString())) {
                            String value = propertyObject.get("value").getAsString();
                            // Cache the result
                            SIGNATURE_CACHE.put(playerName, new CachedSignature(value));
                            return value;
                        }
                    }
                }
            } catch (IOException e) {
                ClashArenaLogger.info("Network error getting skin: " + e.getMessage());
            } catch (Exception e) {
                ClashArenaLogger.printStacktrace("Failed to get skin signature", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        });
    }


    public static CompletableFuture<Void> getPlayerHead(ItemStack is, String playerName, String prefix) {
        return getSignature(playerName)
                .thenAccept(base64 -> {
                    ItemStack skull;
                    if (base64 == null) {
                        skull = getSkullByName(playerName);
                    } else {
                        skull = getSkullByBase64EncodedTextureUrl(base64);
                    }
                    changeDisplayName(skull, playerName);
                    if (is.getItemMeta() instanceof SkullMeta destMeta &&
                            skull.getItemMeta() instanceof SkullMeta sourceMeta) {
                        destMeta.setPlayerProfile(sourceMeta.getPlayerProfile());
                        destMeta.displayName(Component.text(prefix + playerName));
                        is.setItemMeta(destMeta);
                    }
                });
    }

    private static void changeDisplayName(ItemStack is, String displayName) {
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName));
            is.setItemMeta(meta);
        }
    }
}