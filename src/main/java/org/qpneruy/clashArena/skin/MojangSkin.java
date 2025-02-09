package org.qpneruy.clashArena.skin;

import com.google.common.io.CharStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.qpneruy.clashArena.skin.SkullUtils.GSON;

public class MojangSkin {
    public static JSONObject generateFromPNG(final byte[] png, boolean slim)
            throws InterruptedException, ExecutionException {
        return EXECUTOR.submit(() -> {
            DataOutputStream out = null;
            InputStreamReader reader = null;
            try {
                URL target = new URI("https://api.mineskin.org/generate/upload" + (slim ? "?model=slim" : "")).toURL();
                HttpURLConnection con = (HttpURLConnection) target.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("User-Agent", "Citizens/2.0");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");
                con.setConnectTimeout(2000);
                con.setReadTimeout(30000);
                out = new DataOutputStream(con.getOutputStream());
                out.writeBytes("--*****\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\n");
                out.writeBytes("Content-Type: image/png\r\n\r\n");
                out.write(png);
                out.writeBytes("\r\n");
                out.writeBytes("--*****\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"name\";\r\n\r\n\r\n");
                if (slim) {
                    out.writeBytes("--*****\r\n");
                    out.writeBytes("Content-Disposition: form-data; name=\"variant\";\r\n\r\n");
                    out.writeBytes("slim\r\n");
                }
                out.writeBytes("--*****--\r\n");
                out.flush();
                out.close();
                reader = new InputStreamReader(con.getInputStream());
                String str = CharStreams.toString(reader);
                if (con.getResponseCode() != 200)
                    return null;

                JSONObject output = (JSONObject) new JSONParser().parse(str);
                JSONObject data = (JSONObject) output.get("data");
                con.disconnect();
                return data;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).get();
    }

    public static JsonObject generateFromURL(final String url, boolean slim)
            throws InterruptedException, ExecutionException {
        return EXECUTOR.submit(() -> {
            DataOutputStream out = null;
            BufferedReader reader = null;
            try {
                URL target = new URI("https://api.mineskin.org/generate/url").toURL();
                HttpURLConnection con = (HttpURLConnection) target.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.setRequestProperty("User-Agent", "Citizens/2.0");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(2000);
                con.setReadTimeout(30000);
                out = new DataOutputStream(con.getOutputStream());

                // Create request body
                JsonObject req = new JsonObject();
                req.addProperty("url", url);
                req.addProperty("name", "");
                if (slim) {
                    req.addProperty("variant", "slim");
                }

                // Write request body
                out.writeBytes(GSON.toJson(req));
                out.flush();
                out.close();

                // Read response
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Check response code
                if (con.getResponseCode() != 200) {
                    return null;
                }

                // Parse response
                JsonObject output = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonObject data = output.getAsJsonObject("data");

                con.disconnect();
                return data;
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).get();
    }

    public static GameProfile getFilledGameProfileByXUID(String name, long xuid)
            throws InterruptedException, ExecutionException {
        return EXECUTOR.submit(() -> {
            InputStreamReader reader = null;
            try {
                URL target = new URI("https://api.geysermc.org/v2/skin/" + xuid).toURL();
                HttpURLConnection con = (HttpURLConnection) target.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Citizens/2.0");
                con.setRequestProperty("Accept", "application/json");
                con.setConnectTimeout(2000);
                con.setReadTimeout(20000);
                reader = new InputStreamReader(con.getInputStream());
                String str = CharStreams.toString(reader);
                if (con.getResponseCode() != 200)
                    return null;

                JSONObject output = (JSONObject) new JSONParser().parse(str);
                con.disconnect();
                String hex = Long.toHexString(xuid);
                GameProfile profile = new GameProfile(
                        UUID.fromString("00000000-0000-0000-" + hex.substring(0, 4) + "-" + hex.substring(4)), name);
                return profile;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).get();
    }

    public static Long getXUIDFromName(String name) throws InterruptedException, ExecutionException {
        return EXECUTOR.submit(() -> {
            InputStreamReader reader = null;
            try {
                URL target = new URI("https://api.geysermc.org/v2/xbox/xuid/" + name).toURL();
                HttpURLConnection con = (HttpURLConnection) target.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Citizens/2.0");
                con.setRequestProperty("Accept", "application/json");
                con.setConnectTimeout(2000);
                con.setReadTimeout(10000);
                reader = new InputStreamReader(con.getInputStream());
                String str = CharStreams.toString(reader);
                if (con.getResponseCode() != 200)
                    return null;

                JSONObject output = (JSONObject) new JSONParser().parse(str);
                con.disconnect();
                if (!output.containsKey("xuid"))
                    return null;

                return ((Number) output.get("xuid")).longValue();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).get();
    }

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
}
