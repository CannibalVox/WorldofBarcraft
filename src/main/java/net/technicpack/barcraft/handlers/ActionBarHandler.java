package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.api.KeyBindingPressedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.technicpack.barcraft.WorldOfBarcraft;

public class ActionBarHandler {

    public static IIcon[] actionIcons;

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
            actionIcons = new IIcon[6];
            actionIcons[0] = event.map.registerIcon("barcraft:action1");
            actionIcons[1] = event.map.registerIcon("barcraft:action2");
            actionIcons[2] = event.map.registerIcon("barcraft:action3");
            actionIcons[3] = event.map.registerIcon("barcraft:action4");
            actionIcons[4] = event.map.registerIcon("barcraft:action5");
            actionIcons[5] = event.map.registerIcon("barcraft:action6");
        }
    }
}
