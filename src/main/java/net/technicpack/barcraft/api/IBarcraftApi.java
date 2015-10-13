package net.technicpack.barcraft.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IBarcraftApi {
    ///Used to register & retrieve IActions
    IActionRegistry getActionRegistry();
    ///Used to register & retrieve action bars (null on server-side)
    IActionContainerRegistry getActionContainerRegistry();

    boolean triggerAction(IAction action, EntityPlayer source);

    ///Recalculate player's access to all abilities with the given condition key.  Abilities with similar conditions
    ///should have the same condition key and then you can hook up a single forge event to recalculate a player's access
    ///to those abilities with one key.
    void recalculateActionConditions(EntityPlayer player, String conditionKey);
    ///This is here for if you want to grant ability access to players based on some server-wide condition.  Don't abuse
    ///this because the potential for performance issues is there.
    void recalculateActionConditions(String conditionKey);

    ///Control player access for unconditional abilities
    void grantPlayerAction(EntityPlayer player, IAction action);
    void denyPlayerAction(EntityPlayer player, IAction action);

    ///This determines whether the player currently has access to the ability.  This does not mean that the player
    ///SHOULD have access to the ability.  For instance, if there is a conditional ability that the player should not
    ///have access to, but recalculateActionConditions has not yet been called, this method will return true.
    boolean playerHasAction(EntityPlayer player, IAction action);
}
