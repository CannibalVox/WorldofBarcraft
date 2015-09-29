package net.technicpack.barcraft.api;

import net.minecraft.client.renderer.texture.IIconRegister;

public interface IAction {
    String getKey();
    String getDisplayName();
    void registerIcons(IIconRegister register);
}
