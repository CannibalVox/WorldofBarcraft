package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;

public class ActionBarHandler {
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onKeyBindingPressed(KeyBindingPressedEvent event) {
        if (event.keyBinding.isPressed())
            Minecraft.getMinecraft().thePlayer.sendChatMessage("BUTT");
    }
}
