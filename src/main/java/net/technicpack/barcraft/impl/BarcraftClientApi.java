package net.technicpack.barcraft.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.entity.Entity;
import net.technicpack.barcraft.api.ActionClientState;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IBarcraftClientApi;

import java.util.HashMap;

public class BarcraftClientApi extends BarcraftApi implements IBarcraftClientApi {
    @Override
    public void appendPlayerAction(IAction action) {
        String key = action.getKey();
        IActionContainer containerToUse=null;
        int indexToUse = -1;
        for (IActionContainer container : getActionBars()) {
            boolean canUse = container.canAddAction(action);
            for (int i = 0; i < container.getActionCount(); i++) {
                IAction currentAction = container.getAction(i);
                if (currentAction == null && canUse && containerToUse == null && !container.isLocked(i)) {
                    containerToUse = container;
                    indexToUse = i;
                } else if (currentAction != null && key.equals(currentAction.getKey()))
                    //The action is already somewhere on the player's bars
                    return;
            }
        }

        if (containerToUse != null && indexToUse >= 0)
            containerToUse.setAction(indexToUse, action);
    }

    @Override
    public void removePlayerAction(IAction action) {
        String key = action.getKey();
        for (IActionContainer container : getActionBars()) {
            for (int i = 0; i < container.getActionCount(); i++) {
                IAction currentAction = container.getAction(i);

                if (currentAction != null && currentAction.getKey().equals(key) && !container.isLocked(i)) {
                    container.setAction(i, null);
                }
            }
        }
    }
}
