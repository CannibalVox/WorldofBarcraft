package net.technicpack.barcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.technicpack.barcraft.handlers.ActionBarHandler;
import net.technicpack.barcraft.handlers.HudHandler;

@Mod(modid = WorldOfBarcraft.MODID, version = WorldOfBarcraft.VERSION)
public class WorldOfBarcraft {

    public static final String MODID = "barcraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public WorldOfBarcraft instance;

    @SidedProxy(clientSide="net.technicpack.barcraft.ClientProxy", serverSide = "net.technicpack.barcraft.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new HudHandler());
        MinecraftForge.EVENT_BUS.register(new ActionBarHandler());
        proxy.registerClientKeys();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }
}
