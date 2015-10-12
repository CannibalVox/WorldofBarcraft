package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.impl.BarcraftClientApi;

public class ActionBarHandler {

    private TextureMap abilityAtlas;

    public ActionBarHandler(TextureMap abilityAtlas) {
        this.abilityAtlas = abilityAtlas;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyBindingPressed(KeyBindingPressedEvent event) {
        String pressedKeybind = event.keyBinding.getKeyDescription();

        if (KeyHelper.compatibleMods.containsKey("barcraft")) {
            String[] keybinds = KeyHelper.compatibleMods.get("barcraft");
            for (int i = 0; i < keybinds.length; i++) {
                if (keybinds[i].equalsIgnoreCase(pressedKeybind)) {
                    if (!event.keyBinding.getIsKeyPressed() && event.keyBinding.isPressed()) {
                        WorldOfBarcraft.proxy.triggerKeybind(event.keyBinding.getKeyDescription());
                    }
                }
            }

        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureLoad(TextureStitchEvent.Pre event) {
        if (event.map == abilityAtlas) {
            for (IAction action : WorldOfBarcraft.proxy.getApi().getActions()) {
                action.registerIcons(event.map);
            }
        }
    }
}
