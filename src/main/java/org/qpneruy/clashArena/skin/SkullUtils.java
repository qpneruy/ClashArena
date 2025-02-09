package org.qpneruy.clashArena.skin;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.qpneruy.clashArena.utils.ClashArenaLogger;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class SkullUtils {
  private static Field profileField;
  public static final Gson GSON = new Gson();
  static {
    try {
      // Chỉ lấy field một lần khi class được load
      Class<?> skullMetaClass = Class.forName("org.bukkit.craftbukkit."
              + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]
              + ".inventory.CraftMetaSkull");
      profileField = skullMetaClass.getDeclaredField("profile");
      profileField.setAccessible(true);
    } catch (Exception e) {
      ClashArenaLogger.printStacktrace("Failed to initialize SkullUtils", e);
    }
  }

  @NotNull
  public static ItemStack getSkullByBase64EncodedTextureUrl(@NotNull final String base64Url) {
    if (base64Url == null || base64Url.isEmpty()) {
      return new ItemStack(Material.PLAYER_HEAD);
    }

    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta headMeta = (SkullMeta) head.getItemMeta();

    if (headMeta == null) return head;

    try {
      GameProfile profile = new GameProfile(UUID.randomUUID(), "");
      profile.getProperties().put("textures", new Property("textures", base64Url));

      if (profileField != null) {
        synchronized (headMeta) {
          profileField.set(headMeta, profile);
        }
      }

      head.setItemMeta(headMeta);
    } catch (Exception e) {
      ClashArenaLogger.printStacktrace("Failed to set skull texture", e);
    }

    return head;
  }
  @NotNull
  public static String getEncoded(@NotNull final String url) {
    final byte[] encodedData = Base64.getEncoder().encode(String
            .format("{textures:{SKIN:{url:\"%s\"}}}", "https://textures.minecraft.net/texture/" + url)
            .getBytes());
    return new String(encodedData);
  }
  @NotNull
  public static ItemStack getSkullByName(@NotNull final String playerName) {
    if (playerName.isEmpty()) {
      return new ItemStack(Material.PLAYER_HEAD);
    }

    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta headMeta = (SkullMeta) head.getItemMeta();

    if (headMeta == null) return head;

    try {
      headMeta.setOwner(playerName);
      if (!headMeta.hasOwner()) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), playerName);

        if (profileField != null) {
          synchronized (headMeta) {
            profileField.set(headMeta, profile);
          }
        }
      }

      head.setItemMeta(headMeta);
    } catch (Exception e) {
      ClashArenaLogger.printStacktrace("Failed to set skull owner", e);
    }

    return head;
  }

  private static String getSafeBase64(String input) {
    try {
      return input.replaceAll("=", "");
    } catch (Exception e) {
      return input;
    }
  }
}