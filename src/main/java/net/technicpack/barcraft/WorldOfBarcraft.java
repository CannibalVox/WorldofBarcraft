package net.technicpack.barcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.technicpack.barcraft.handlers.HudHandler;

@Mod(modid = WorldOfBarcraft.MODID, version = WorldOfBarcraft.VERSION)
public class WorldOfBarcraft {

    public static final String MODID = "barcraft";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new HudHandler());
    }
}
