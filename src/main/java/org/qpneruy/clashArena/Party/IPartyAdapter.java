package org.qpneruy.clashArena.Party;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IPartyAdapter {
    @Nullable PartyPlayer getPartyPlayer(UUID playerUUID);
    @Nullable Party getParty(UUID playerUUID);
    @Nullable Party getPartyOfPlayer(UUID playerUUID);
    void createParty(String partyName, PartyPlayer Leader);
}
