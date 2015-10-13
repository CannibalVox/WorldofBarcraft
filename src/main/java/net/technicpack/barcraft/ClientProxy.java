package net.technicpack.barcraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import modwarriors.notenoughkeys.api.Api;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IActionRegistry;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.defaultBar.DefaultActionBar;
import net.technicpack.barcraft.handlers.ActionBarHandler;
import net.technicpack.barcraft.impl.ActionContainerRegistry;
import net.technicpack.barcraft.impl.BarcraftClientApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy extends CommonProxy {
    private IBarcraftApi clientApi;
    private HashMap<String, Integer> keybindIndices = new HashMap<String, Integer>();
    private HashMap<String, IActionContainer> keybindContainers = new HashMap<String, IActionContainer>();
    public TextureMap abilityAtlas;

    @Override
    public void initApi(IActionRegistry actionRegistry) {
        super.initApi(actionRegistry);
        this.clientApi = new BarcraftClientApi(actionRegistry, new ActionContainerRegistry());
    }

    @Override
    public IBarcraftApi getApi() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return this.clientApi;
        else
            return super.getApi();
    }

    @Override
    public void registerClientKeys() {
        List<String> allBindings = new ArrayList<String>();

        for (IActionContainer bar : clientApi.getActionContainerRegistry().getActionBars()) {
            for (int i = 0; i < bar.getActionCount(); i++) {
                KeyBinding binding = bar.getKeybindingForAction(i);

                if (binding != null) {
                    ClientRegistry.registerKeyBinding(binding);
                    allBindings.add(binding.getKeyDescription());
                    keybindIndices.put(binding.getKeyDescription(), i);
                    keybindContainers.put(binding.getKeyDescription(), bar);
                }
            }
        }

        Api.registerMod("barcraft",  allBindings.toArray(new String[allBindings.size()]));
    }

    @Override
    public void triggerKeybind(String keybindDesc) {
        if (!keybindIndices.containsKey(keybindDesc) || !keybindContainers.containsKey(keybindDesc))
            return;

        IAction triggerAction = keybindContainers.get(keybindDesc).getAction(keybindIndices.get(keybindDesc));

        if (triggerAction == null)
            return;

        getApi().triggerAction(triggerAction, Minecraft.getMinecraft().thePlayer);
    }

    @Override
    public EntityPlayer getNetworkPlayer(MessageContext ctx) {
        if (ctx.side == Side.CLIENT)
            return Minecraft.getMinecraft().thePlayer;
        else
            return super.getNetworkPlayer(ctx);
    }

    @Override
    public void createTextureAtlas() {
        abilityAtlas = new TextureMap(57, "textures/abilities");
        MinecraftForge.EVENT_BUS.register(new ActionBarHandler(abilityAtlas));
    }

    @Override
    public void loadTextureAtlas() {
        Minecraft.getMinecraft().renderEngine.loadTextureMap(new ResourceLocation("textures/atlas/abilities.png"), abilityAtlas);
    }

    @Override
    public void registerDefaultBars() {
        clientApi.getActionContainerRegistry().registerActionContainer(new DefaultActionBar());
    }
}
