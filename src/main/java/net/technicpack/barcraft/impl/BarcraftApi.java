package net.technicpack.barcraft.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.technicpack.barcraft.api.*;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;

public class BarcraftApi implements IBarcraftApi {

    private BarcraftDatabase database;
    private IActionRegistry actionRegistry;

    public BarcraftApi(IActionRegistry actionRegistry, BarcraftDatabase database) {
        this.actionRegistry = actionRegistry;
        this.database = database;
    }

    @Override
    public IActionRegistry getActionRegistry() {
        return actionRegistry;
    }

    @Override
    public IActionContainerRegistry getActionContainerRegistry() {
        return null;
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
        for (IAction action : getActionRegistry().getActions()) {
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

    protected BarcraftDatabase getSideDatabase() { return database; }

    @Override
    public void grantPlayerAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = getSideDatabase().getPlayer(player);
        accessDatabase.addUnconditional(action.getKey());
    }

    @Override
    public void denyPlayerAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = getSideDatabase().getPlayer(player);
        accessDatabase.removeUnconditional(action.getKey());
    }

    @Override
    public boolean playerHasAction(EntityPlayer player, IAction action) {
        PlayerAccessDatabase accessDatabase = getSideDatabase().getPlayer(player);

        return accessDatabase.hasAction(action.getKey());
    }
}
