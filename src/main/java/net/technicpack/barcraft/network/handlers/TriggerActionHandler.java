package net.technicpack.barcraft.network.handlers;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.network.packets.TriggerActionPacket;

public class TriggerActionHandler implements IMessageHandler<TriggerActionPacket, IMessage> {

    public TriggerActionHandler() {}

    @Override
    public IMessage onMessage(TriggerActionPacket message, MessageContext ctx) {
        WorldOfBarcraft.proxy.getApi().triggerAction(message.getAction(), WorldOfBarcraft.proxy.getNetworkPlayer(ctx));
        return null;
    }
}
