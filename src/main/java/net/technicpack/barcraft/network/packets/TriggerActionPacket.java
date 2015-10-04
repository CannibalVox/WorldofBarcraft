package net.technicpack.barcraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;

public class TriggerActionPacket implements IMessage {

    private IAction action;

    public TriggerActionPacket() {}

    public TriggerActionPacket(IAction action) {
        this.action = action;
    }

    public IAction getAction() { return this.action; }

    @Override
    public void fromBytes(ByteBuf buf) {
        String actionKey = readString(buf);
        this.action = WorldOfBarcraft.proxy.getApi().getAction(actionKey);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeString(buf, this.action.getKey());
    }

    private String readString(ByteBuf buf) {
        boolean hasString = buf.readBoolean();
        if (!hasString)
            return null;

        return ByteBufUtils.readUTF8String(buf);
    }

    private void writeString(ByteBuf buf, String string) {
        if (string == null) {
            buf.writeBoolean(false);
            return;
        }

        buf.writeBoolean(true);
        ByteBufUtils.writeUTF8String(buf, string);
    }
}
