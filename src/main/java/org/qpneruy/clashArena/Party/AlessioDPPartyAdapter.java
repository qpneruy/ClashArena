package org.qpneruy.clashArena.Party;

import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AlessioDPPartyAdapter implements IPartyAdapter {
    private final PartiesAPI partiesAPI;

    public AlessioDPPartyAdapter(PartiesAPI partiesAPI) {
        this.partiesAPI = partiesAPI;
    }

    @Override
    public @Nullable PartyPlayer getPartyPlayer(UUID playerUUID) {
        return partiesAPI.getPartyPlayer(playerUUID);
    }

    @Override
    public @Nullable Party getParty(UUID playerUUID) {
        return partiesAPI.getParty(playerUUID);
    }

    @Override
    public @Nullable Party getPartyOfPlayer(UUID playerUUID) {
        return partiesAPI.getPartyOfPlayer(playerUUID);
    
    }
    
    @Override
    public void createParty(String partyName, PartyPlayer Leader) {
        partiesAPI.createParty(partyName, Leader);
    }
}
