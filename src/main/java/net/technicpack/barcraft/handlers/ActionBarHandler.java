package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;

public class ActionBarHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyBindingPressed(KeyBindingPressedEvent event) {
        if (event.keyBinding.isPressed())
            Minecraft.getMinecraft().thePlayer.sendChatMessage("BUTT");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureLoad(TextureStitchEvent.Pre event) {
        if (event.map == WorldOfBarcraft.instance.abilityAtlas) {
            for (IAction action : WorldOfBarcraft.proxy.getApi().getActions()) {
                action.registerIcons(event.map);
            }
        }
    }
}
