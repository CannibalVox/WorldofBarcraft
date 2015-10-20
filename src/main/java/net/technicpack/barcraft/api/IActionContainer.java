package net.technicpack.barcraft.api;

import net.minecraft.client.settings.KeyBinding;

///These should only be used on the client side.  Basically, barcraft is a generalized API for activatable
///abilities, and a client-side mod for organizing these abilities around action bars.  The server does not
///give a crap about whether an ability is on a bar or not, so we just have each bar manage a single copy
///of its actions and tell modders "this won't work properly on the server".
///We do keep the Action Bars in the common API, because sometimes IActions want to be somewhat aware of action
///bars (for instance, we have the capabilities for actions to be picky about what bars they can join on the
///client side), and we don't want to complicate client/server splits.  That said, you should not expect
///action bars to be manipulable from the server side or really even be particularly useful at all.
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

    IAction getAction(int index);
    void setAction(int index, IAction action);
    ///The client state should cover most client-side-only states like hovered, clicked, etc.
    ActionClientState getClientState(int index);
    ///If locked, barcraft won't attempt to autofill/autoremove a slot.  This is good for both giving users
    ///more control over their bars through the default barcraft menu, and also for bars fully managed by their
    ///mods, to prevent barcraft from messing with them.
    boolean isLocked(int index);
    void setLocked(int index, boolean locked);
}
