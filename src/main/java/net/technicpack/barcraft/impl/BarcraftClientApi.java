package net.technicpack.barcraft.impl;

import com.sun.org.apache.xpath.internal.operations.Bool;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.technicpack.barcraft.api.*;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;
import net.technicpack.barcraft.impl.playerdata.readers.NullReader;
import net.technicpack.barcraft.impl.playerdata.writers.NullWriter;
import net.technicpack.barcraft.network.BarcraftNetwork;
import net.technicpack.barcraft.network.packets.TriggerActionPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarcraftClientApi extends BarcraftApi {
    private IActionContainerRegistry actionContainerRegistry;

    private BarcraftDatabase clientSideDatabase;

    public BarcraftClientApi(IActionRegistry actionRegistry, IActionContainerRegistry actionContainerRegistry, BarcraftDatabase serverDatabase, BarcraftDatabase clientDatabase) {
        super(actionRegistry, serverDatabase);
        this.actionContainerRegistry = actionContainerRegistry;
        this.clientSideDatabase = clientDatabase;
    }

    @Override
    public IActionContainerRegistry getActionContainerRegistry() { return actionContainerRegistry; }

    private void appendPlayerAction(IAction action) {
        String key = action.getKey();
        IActionContainer containerToUse=null;
        int indexToUse = -1;
        for (IActionContainer container : getActionContainerRegistry().getActionBars()) {
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

    private void removePlayerAction(IAction action) {
        String key = action.getKey();
        for (IActionContainer container : getActionContainerRegistry().getActionBars()) {
            for (int i = 0; i < container.getActionCount(); i++) {
                IAction currentAction = container.getAction(i);

                if (currentAction != null && currentAction.getKey().equals(key) && !container.isLocked(i)) {
                    container.setAction(i, null);
                }
            }
        }
    }

    @Override
    public boolean triggerAction(IAction action, EntityPlayer source) {
        if (super.triggerAction(action, source)) {
            if (source.getEntityWorld().isRemote)
                BarcraftNetwork.sendToServer(new TriggerActionPacket(action));
            return true;
        }
        return false;
    }


    @Override
    public void grantPlayerAction(EntityPlayer player, IAction action) {
        if (clientSideDatabase.getPlayer(player) == null)
            clientSideDatabase.addPlayer(player, new PlayerAccessDatabase(player.getUniqueID(), new NullReader(), new NullWriter()));
        super.grantPlayerAction(player, action);
        appendPlayerAction(action);
    }

    @Override
    public void denyPlayerAction(EntityPlayer player, IAction action) {
        if (clientSideDatabase.getPlayer(player) == null)
            clientSideDatabase.addPlayer(player, new PlayerAccessDatabase(player.getUniqueID(), new NullReader(), new NullWriter()));
        super.denyPlayerAction(player, action);
        removePlayerAction(action);
    }

    @Override
    protected BarcraftDatabase getSideDatabase() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return clientSideDatabase;
        return super.getSideDatabase();
    }
}
