package net.technicpack.barcraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.settings.KeyBinding;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.api.IBarcraftClientApi;
import net.technicpack.barcraft.impl.BarcraftApi;
import net.technicpack.barcraft.impl.BarcraftClientApi;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {
    private IBarcraftClientApi clientApi;

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
                ClientRegistry.registerKeyBinding(binding);
                allBindings.add(binding.getKeyDescription());
            }
        }

        Api.registerMod("barcraft",  allBindings.toArray(new String[allBindings.size()]));
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
}
