package net.technicpack.barcraft.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.impl.playerdata.AccessDiff;
import net.technicpack.barcraft.network.packets.ActionDiffPacket;

public class ActionDiffHandler implements IMessageHandler<ActionDiffPacket, IMessage> {
    @Override
    public IMessage onMessage(ActionDiffPacket message, MessageContext ctx) {
        EntityPlayer player = WorldOfBarcraft.proxy.getNetworkPlayer(ctx);
        IBarcraftApi api = WorldOfBarcraft.proxy.getApi();
        for (AccessDiff diff : message.getDiffs()) {
            switch (diff.getType()) {
                case ADD:
                    api.grantPlayerAction(player, api.getActionRegistry().getAction(diff.getAction()));
                    break;
                case REMOVE:
                    api.denyPlayerAction(player, api.getActionRegistry().getAction(diff.getAction()));
                    break;
            }
        }

        return null;
    }
}
