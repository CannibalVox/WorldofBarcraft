package net.technicpack.barcraft.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;

import java.util.HashMap;

public class BarcraftApi implements IBarcraftApi {

    private HashMap<String, IAction> barcraftActions = new HashMap<String, IAction>();
    private HashMap<String, IActionContainer> barcraftBars = new HashMap<String, IActionContainer>();
    private BarcraftDatabase database;

    public BarcraftApi(BarcraftDatabase database) {
        this.database = database;
    }

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

    @Override
    public void recalculateActionConditions(EntityPlayer player, String conditionKey) {
        PlayerAccessDatabase accessDb = database.getPlayer(player);
        for (IAction action : getActions()) {
            if (action.getConditionKey() == null || conditionKey == null)
                continue;

            if (!conditionKey.equals(action.getConditionKey()))
                continue;

            boolean access = action.calculateAccessCondition(player);

            if (access != accessDb.hasAction(action.getKey())) {
                if (access)
                    accessDb.addConditional(action.getKey());
                else
                    accessDb.removeConditional(action.getKey());
            }
        }
    }

    @Override
    public void recalculateActionConditions(String conditionKey) {
        if (MinecraftServer.getServer() == null)
            return;

        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            EntityPlayer player = (EntityPlayer)playerObj;
            recalculateActionConditions(player, conditionKey);
        }
    }

    @Override
    public void grantPlayerAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = database.getPlayer(player);
        accessDatabase.addUnconditional(action.getKey());
    }

    @Override
    public void denyPlayerAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = database.getPlayer(player);
        accessDatabase.removeUnconditional(action.getKey());
    }

    @Override
    public boolean playerHasAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = database.getPlayer(player);

        return accessDatabase.hasAction(action.getKey());
    }
}
