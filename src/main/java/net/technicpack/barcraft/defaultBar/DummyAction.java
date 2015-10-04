package net.technicpack.barcraft.defaultBar;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;

public class DummyAction implements IAction {

    private String key;
    private String displayName;
    private String iconPath;

    private IIcon icon;

    public DummyAction(String key, String displayName, String iconPath) {
        this.key = key;
        this.displayName = displayName;
        this.iconPath = iconPath;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        icon = register.registerIcon(iconPath);
    }

    @Override
    public boolean canJoinBar(IActionContainer container) {
        return true;
    }

    @Override
    public IIcon getIcon() { return icon; }
}
