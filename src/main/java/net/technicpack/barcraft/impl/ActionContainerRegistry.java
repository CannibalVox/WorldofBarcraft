package net.technicpack.barcraft.impl;

import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IActionContainerRegistry;

import java.util.HashMap;

public class ActionContainerRegistry implements IActionContainerRegistry {
    private HashMap<String, IActionContainer> barcraftBars = new HashMap<String, IActionContainer>();

    public ActionContainerRegistry() {}

    @Override
    public Iterable<IActionContainer> getActionBars() {
        return barcraftBars.values();
    }

    @Override
    public IActionContainer getActionContainer(String key) {
        if (!barcraftBars.containsKey(key))
            return null;
        return barcraftBars.get(key);
    }

    @Override
    public void registerActionContainer(IActionContainer container) {
        barcraftBars.put(container.getKey(), container);
    }
}
