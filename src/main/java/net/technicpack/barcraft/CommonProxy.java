package net.technicpack.barcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.handlers.PlayerConnectionHandler;
import net.technicpack.barcraft.impl.BarcraftApi;
import net.technicpack.barcraft.impl.BarcraftDatabase;

public class CommonProxy {
    public void registerClientKeys() {}

    private IBarcraftApi api;

    public void initApi() {
        BarcraftDatabase database = new BarcraftDatabase();
        this.api = new BarcraftApi(database);
        FMLCommonHandler.instance().bus().register(database);
        FMLCommonHandler.instance().bus().register(new PlayerConnectionHandler(database));
    }

    public IBarcraftApi getApi() {
        return this.api;
    }

    public void triggerKeybind(String keyBind) {}

    public EntityPlayer getNetworkPlayer(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public void createTextureAtlas() {}

    public void loadTextureAtlas() {}
}
