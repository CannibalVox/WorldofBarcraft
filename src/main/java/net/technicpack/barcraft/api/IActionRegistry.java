package net.technicpack.barcraft.api;

public interface IActionRegistry {
    ///Allows a mod to register a new action to be added to action bars.  This should be done in preInit, always.
    void registerAction(IAction action);

    //Grabs actions & bars by their registration keys.
    IAction getAction(String key);
    Iterable<IAction> getActions();
}
