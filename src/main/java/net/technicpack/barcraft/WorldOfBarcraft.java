package net.technicpack.barcraft;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.technicpack.barcraft.defaultBar.DefaultActionBar;
import net.technicpack.barcraft.defaultBar.DummyAction;
import net.technicpack.barcraft.handlers.ActionBarHandler;
import net.technicpack.barcraft.handlers.HudHandler;
import net.technicpack.barcraft.network.BarcraftNetwork;

@Mod(modid = WorldOfBarcraft.MODID, version = WorldOfBarcraft.VERSION)
public class WorldOfBarcraft {

    public static final String MODID = "barcraft";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static WorldOfBarcraft instance;

    @SidedProxy(clientSide="net.technicpack.barcraft.ClientProxy", serverSide = "net.technicpack.barcraft.CommonProxy")
    public static CommonProxy proxy;

    public TextureMap abilityAtlas;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        proxy.initApi();

        proxy.getApi().registerActionContainer(new DefaultActionBar());
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy1", "barcraft.action1", "barcraft:action1"));
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy2", "barcraft.action2", "barcraft:action2"));
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy3", "barcraft.action3", "barcraft:action3"));
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy4", "barcraft.action4", "barcraft:action4"));
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy5", "barcraft.action5", "barcraft:action5"));
        proxy.getApi().registerAction(new DummyAction("barcraft:dummy6", "barcraft.action6", "barcraft:action6"));

        FMLCommonHandler.instance().bus().register(new HudHandler());
        MinecraftForge.EVENT_BUS.register(new ActionBarHandler());

        proxy.registerClientKeys();
        abilityAtlas = new TextureMap(57, "textures/abilities");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BarcraftNetwork.init();
        Minecraft.getMinecraft().renderEngine.loadTextureMap(new ResourceLocation("textures/atlas/abilities.png"), abilityAtlas);
        proxy.addActions();
    }
}
