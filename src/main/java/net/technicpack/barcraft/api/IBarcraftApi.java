package net.technicpack.barcraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IBarcraftApi {
    ///Allows a mod to register a new action to be added to action bars.  This should be done in preInit, always.
    void registerAction(IAction action);

    //Grabs actions & bars by their registration keys.
    IAction getAction(String key);

    Iterable<IAction> getActions();

    ///Allows a mod to register a new action bar to be used in a mod.  This should be done in preInit, always.
    void registerActionContainer(IActionContainer container);
    IActionContainer getActionContainer(String key);
    Iterable<IActionContainer> getActionBars();

    boolean triggerAction(IAction action, EntityPlayer source);
}
