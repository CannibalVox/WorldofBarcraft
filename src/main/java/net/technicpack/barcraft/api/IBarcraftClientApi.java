package net.technicpack.barcraft.api;

import net.minecraft.entity.Entity;

public interface IBarcraftClientApi extends IBarcraftApi {
    ///Append will add the action to the first player bar with a free space that will accept it, or do nothing
    ///if one does not exist.
    void appendPlayerAction(IAction action);
    ///Remove will remove the action from any bar that it is not locked to
    void removePlayerAction(IAction action);
}
