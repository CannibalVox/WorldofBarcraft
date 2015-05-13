package net.technicpack.barcraft;

import cpw.mods.fml.client.registry.ClientRegistry;
import modwarriors.notenoughkeys.api.Api;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    private KeyBinding[] actionBarBindings = new KeyBinding[6];

    @Override
    public void registerClientKeys() {
        Api.registerMod("barcraft", "barcraft.action1", "barcraft.action2", "barcraft.action3", "barcraft.action4", "barcraft.action5", "barcraft.action6");

        for (int i = 0; i < 6; i++) {
            String keyName = "barcraft.action"+Integer.toString(i+1);
            actionBarBindings[i] = new KeyBinding(keyName, Keyboard.KEY_1+i, "barcraft");
            ClientRegistry.registerKeyBinding(actionBarBindings[i]);
        }
    }

    public KeyBinding getActionBarBinding(int index) {
        return actionBarBindings[index];
    }
}
