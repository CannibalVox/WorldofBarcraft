package net.technicpack.barcraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.technicpack.barcraft.impl.playerdata.AccessDiff;
import net.technicpack.barcraft.impl.playerdata.DiffType;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;

import java.util.ArrayList;
import java.util.List;

public class ActionDiffPacket implements IMessage {
    List<AccessDiff> accessDiffs = new ArrayList<AccessDiff>();

    public ActionDiffPacket(PlayerAccessDatabase db) {
        for (AccessDiff diff : db.getAccessDiffs()) {
            accessDiffs.add(diff);
        }
    }

    public ActionDiffPacket() {

    }

    public Iterable<AccessDiff> getDiffs() { return accessDiffs; }

    @Override
    public void fromBytes(ByteBuf buf) {
        accessDiffs.clear();
        int length = buf.readInt();

        for (int i = 0; i < length; i++) {
            DiffType type = DiffType.values()[buf.readByte()];
            String action = readString(buf);
            accessDiffs.add(new AccessDiff(type, action));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(accessDiffs.size());

        for (AccessDiff diff : accessDiffs) {
            buf.writeByte(diff.getType().ordinal());
            writeString(buf, diff.getAction());
        }
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
