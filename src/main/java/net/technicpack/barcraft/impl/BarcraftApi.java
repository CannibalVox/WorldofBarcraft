package net.technicpack.barcraft.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IBarcraftApi;

import java.util.HashMap;

public class BarcraftApi implements IBarcraftApi {

    private HashMap<String, IAction> barcraftActions = new HashMap<String, IAction>();
    private HashMap<String, IActionContainer> barcraftBars = new HashMap<String, IActionContainer>();

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

    @Override
    public boolean triggerAction(IAction action, EntityPlayer source) {
        if (!action.canTrigger(source))
            return false;

        action.trigger(source);
        return true;
    }
}
