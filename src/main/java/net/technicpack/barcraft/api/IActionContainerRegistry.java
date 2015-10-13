package net.technicpack.barcraft.api;

public interface IActionContainerRegistry {
    ///Allows a mod to register a new action bar to be used in a mod.  This should be done in preInit, always.
    void registerActionContainer(IActionContainer container);
    IActionContainer getActionContainer(String key);
    Iterable<IActionContainer> getActionBars();
}
