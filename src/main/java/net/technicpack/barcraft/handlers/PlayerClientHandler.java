package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.technicpack.barcraft.impl.BarcraftDatabase;

public class PlayerClientHandler {

    private BarcraftDatabase database;

    public PlayerClientHandler(BarcraftDatabase database) {
        this.database = database;
    }

    @SubscribeEvent
    public void connectingToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        database.init();
    }

    @SubscribeEvent
    public void disconnectingFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        database.shutdown();
    }
}
