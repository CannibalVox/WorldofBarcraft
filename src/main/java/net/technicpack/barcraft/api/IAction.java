package net.technicpack.barcraft.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

public interface IAction {
    String getKey();
    String getDisplayName();
    void registerIcons(IIconRegister register);
    IIcon getIcon();
    boolean canJoinBar(IActionContainer container);

    boolean canTrigger(EntityPlayer source);
    void trigger(EntityPlayer source);
}
