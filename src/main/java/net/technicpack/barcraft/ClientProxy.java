package net.technicpack.barcraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.api.IBarcraftClientApi;
import net.technicpack.barcraft.handlers.ActionBarHandler;
import net.technicpack.barcraft.impl.BarcraftApi;
import net.technicpack.barcraft.impl.BarcraftClientApi;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientProxy extends CommonProxy {
    private IBarcraftClientApi clientApi;
    private HashMap<String, Integer> keybindIndices = new HashMap<String, Integer>();
    private HashMap<String, IActionContainer> keybindContainers = new HashMap<String, IActionContainer>();
    public TextureMap abilityAtlas;

    public void initApi() {
        this.clientApi = new BarcraftClientApi();
    }

    public IBarcraftApi getApi() {
        return this.clientApi;
    }

    @Override
    public void registerClientKeys() {
        List<String> allBindings = new ArrayList<String>();

        for (IActionContainer bar : clientApi.getActionBars()) {
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
    public void addActions() {
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy1"));
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy2"));
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy3"));
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy4"));
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy5"));
        clientApi.appendPlayerAction(clientApi.getAction("barcraft:dummy6"));
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
}
