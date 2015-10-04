package net.technicpack.barcraft;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.impl.BarcraftApi;

public class CommonProxy {
    public void registerClientKeys() {}

    private IBarcraftApi api;

    public void initApi() {
        this.api = new BarcraftApi();
    }

    public IBarcraftApi getApi() {
        return this.api;
    }

    public void addActions() {}

    public void triggerKeybind(String keyBind) {}

    public EntityPlayer getNetworkPlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }
}
