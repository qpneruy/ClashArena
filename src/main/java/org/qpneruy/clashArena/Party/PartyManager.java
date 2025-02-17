package org.qpneruy.clashArena.Party;

import com.alessiodp.parties.api.interfaces.Party;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.qpneruy.clashArena.ClashArena;
import org.qpneruy.clashArena.menu.Gui.mainMenu.MainMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.qpneruy.clashArena.ClashArena.parties;

// This class is responsible for managing the parties for "ClashArena" .
// so that mean if you create a party in PARTIES plugin, you can't use this class to manage it.
// if you want to manage the party, you need to import or create it form here not PARTIES plugin.
// parties in this sentence refers to the "party owner" NOT "member of party"

public class PartyManager {
    @Getter private final List<UUID> partyPlayers = new ArrayList<>();
    public MainMenu registeredListenerMenu;

    /**
     * Creates a new party with the specified name and player as the leader.
     *
     * @param player the player who will be the leader of the party
     */
    public void createParty(Player player) {
        ClashArena.parties.createParty(player.getName(), parties.getPartyPlayer(player.getUniqueId()));
        partyPlayers.add(player.getUniqueId());
        registeredListenerMenu.addParty(parties.getPartyOfPlayer(player.getUniqueId()));
    }

    /**
     * Removes the specified player party
     *
     * @param player the player to be removed.
     */
    public void removePlayer(Player player) {
        Objects.requireNonNull(parties.getPartyOfPlayer(player.getUniqueId())).delete();
        partyPlayers.remove(player.getUniqueId());
        registeredListenerMenu.removeParty(parties.getPartyOfPlayer(player.getUniqueId()));
    }

    /**
     * Removes the specified party.
     *
     * @param party the party to be removed
     */
    public void removeParty(Party party) {
        partyPlayers.remove(party.getLeader());
        registeredListenerMenu.removeParty(party);
        party.delete();
    }

    /**
     * for case player is already in party
     * so doesn't need to create new party
     *
     * @param party the party to be imported
     */
    public void importParty(Party party) {
        partyPlayers.add(party.getLeader());
        registeredListenerMenu.addParty(party);
    }
    /**
     * Registers the listener menu to be used for party management.
     *
     * @param menu the menu to be registered
     */
    public void registerListenerMenu(MainMenu menu) {
        registeredListenerMenu = menu;
    }
}
