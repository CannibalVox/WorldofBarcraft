package net.technicpack.barcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.technicpack.barcraft.defaultBar.DefaultActionBar;
import net.technicpack.barcraft.defaultBar.DiamondHelmAction;
import net.technicpack.barcraft.defaultBar.DummyAction;
import net.technicpack.barcraft.defaultBar.EquipmentChangeHandler;
import net.technicpack.barcraft.handlers.ActionBarHandler;
import net.technicpack.barcraft.handlers.HudHandler;
import net.technicpack.barcraft.impl.ActionRegistry;
import net.technicpack.barcraft.network.BarcraftNetwork;

@Mod(modid = WorldOfBarcraft.MODID, version = WorldOfBarcraft.VERSION)
public class WorldOfBarcraft {

    public static final String MODID = "barcraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static WorldOfBarcraft instance;

    @SidedProxy(clientSide="net.technicpack.barcraft.ClientProxy", serverSide = "net.technicpack.barcraft.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        proxy.initApi(new ActionRegistry());
        proxy.registerDefaultBars();

        proxy.getApi().getActionRegistry().registerAction(new DummyAction("barcraft:dummy1", "barcraft.action1", "barcraft:action1"));
        proxy.getApi().getActionRegistry().registerAction(new DummyAction("barcraft:dummy2", "barcraft.action2", "barcraft:action2"));
        proxy.getApi().getActionRegistry().registerAction(new DummyAction("barcraft:dummy3", "barcraft.action3", "barcraft:action3"));
        proxy.getApi().getActionRegistry().registerAction(new DummyAction("barcraft:dummy4", "barcraft.action4", "barcraft:action4"));
        proxy.getApi().getActionRegistry().registerAction(new DummyAction("barcraft:dummy5", "barcraft.action5", "barcraft:action5"));
        proxy.getApi().getActionRegistry().registerAction(new DiamondHelmAction("barcraft:dummy6", "barcraft.action6", "barcraft:action6"));

        FMLCommonHandler.instance().bus().register(new HudHandler());
        FMLCommonHandler.instance().bus().register(new EquipmentChangeHandler());

        proxy.registerClientKeys();
        proxy.createTextureAtlas();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BarcraftNetwork.init();
        proxy.loadTextureAtlas();
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event) {
        proxy.initServerDatabase();
    }

    @Mod.EventHandler
    public void serverStoppingEvent(FMLServerStoppedEvent event) {
        proxy.shutdownServerDatabase();
    }
}
