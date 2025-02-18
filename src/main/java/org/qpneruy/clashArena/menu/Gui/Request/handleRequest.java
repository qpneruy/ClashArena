package org.qpneruy.clashArena.menu.Gui.Request;

import com.alessiodp.parties.api.interfaces.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.qpneruy.clashArena.ClashArena;

import java.util.HashMap;

public class handleRequest implements Listener {
    private final HashMap<Party, Request> requestMap = new HashMap<>();

    public handleRequest() {
        ClashArena.instance.getServer().getPluginManager().registerEvents(this, ClashArena.instance);
    }

    @EventHandler
    public void onPartyRequest(PlayerCommandPreprocessEvent event) {
        event.getMessage().split("");
    }

}
