package net.technicpack.barcraft.impl;

import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionRegistry;

import java.util.HashMap;

public class ActionRegistry implements IActionRegistry {
    private HashMap<String, IAction> barcraftActions = new HashMap<String, IAction>();

    public ActionRegistry() {}

    @Override
    public void registerAction(IAction action) {
        barcraftActions.put(action.getKey(), action);
    }

    @Override
    public IAction getAction(String key) {
        if (!barcraftActions.containsKey(key))
            return null;
        return barcraftActions.get(key);
    }

    @Override
    public Iterable<IAction> getActions() {
        return barcraftActions.values();
    }
}
