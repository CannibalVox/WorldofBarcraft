package net.technicpack.barcraft.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;
import net.technicpack.barcraft.network.handlers.ActionDiffHandler;
import net.technicpack.barcraft.network.handlers.TriggerActionHandler;
import net.technicpack.barcraft.network.packets.ActionDiffPacket;
import net.technicpack.barcraft.network.packets.TriggerActionPacket;

public class BarcraftNetwork {
    private static final BarcraftNetwork INSTANCE = new BarcraftNetwork();
    private SimpleNetworkWrapper networkWrapper;

    public static void init() {
        INSTANCE.networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(WorldOfBarcraft.MODID);
        INSTANCE.networkWrapper.registerMessage(TriggerActionHandler.class, TriggerActionPacket.class, 0, Side.SERVER);
        INSTANCE.networkWrapper.registerMessage(ActionDiffHandler.class, ActionDiffPacket.class, 1, Side.CLIENT);
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        INSTANCE.networkWrapper.sendTo(message, player);
    }

    public static void sendToServer(IMessage message) {
        INSTANCE.networkWrapper.sendToServer(message);
    }

    public static void sendPlayerDiffs(EntityPlayerMP player, PlayerAccessDatabase accessDatabase) {
        INSTANCE.networkWrapper.sendTo(new ActionDiffPacket(accessDatabase), player);
    }
}
