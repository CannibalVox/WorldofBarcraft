package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.impl.BarcraftDatabase;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;
import net.technicpack.barcraft.network.BarcraftNetwork;

public class PlayerConnectionHandler {

    private BarcraftDatabase database;

    public PlayerConnectionHandler(BarcraftDatabase database) {
        this.database = database;
    }

    @SubscribeEvent
    public void playerConnected(PlayerEvent.PlayerLoggedInEvent evt) {
        PlayerAccessDatabase freshPlayer = database.openPlayer(evt.player);

        for (IAction action : WorldOfBarcraft.proxy.getApi().getActions()) {
            if (action.getConditionKey() != null && !freshPlayer.hasAction(action.getKey())) {
                if (action.calculateAccessCondition(evt.player))
                    freshPlayer.addConditional(action.getKey());
            }
        }
    }

    @SubscribeEvent
    public void playerDisconnected(PlayerEvent.PlayerLoggedOutEvent evt) {
        database.closePlayer(evt.player);
    }
}
