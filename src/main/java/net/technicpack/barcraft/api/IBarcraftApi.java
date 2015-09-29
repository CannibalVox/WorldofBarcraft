package net.technicpack.barcraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IBarcraftApi {
    ///Allows a mod to register a new action to be added to action bars.  This should be done in preInit, always.
    void registerAction(IAction action);
    ///Allows a mod to register a new action bar to be used in a mod.  This should be done in preInit, always.
    void registerActionContainer(IActionContainer container);

    //Grabs actions & bars by their registration keys.
    IAction getAction(String key);
    IActionContainer getActionContainer(String key);

    Iterable<IAction> getActions();
    Iterable<IActionContainer> getActionBars();

    ///Interact with player action bars
    void setPlayerAction(Entity entity, IActionContainer container, int index, IAction action);
    IAction getAction(Entity entity, IActionContainer container, int index);
}
