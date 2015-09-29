package net.technicpack.barcraft.api;

import net.minecraft.client.settings.KeyBinding;

public interface IActionContainer {
    String getKey();

    ///Text name for bar- we will attempt to localize this
    String getDisplayName();

    ///Data for drawing the action bar in the HUD, making it clickable, adding it to the Barcraft config menu.
    ///If you don't want any of those things, this value can be null.
    IOnScreenBar getRenderData();
    ///Data for autogenerating key bindings.  If you want to handle keys yourself, this value can be null.
    ///If you're returning something here, don't register the key anywhere, just instantiate & return it
    KeyBinding getKeybindingForAction(int actionIndex);
    ///Allow containers to filter valid actions
    boolean canAddAction(IAction action);
    ///Number of actions that fit in the bar
    int getActionCount();
}
