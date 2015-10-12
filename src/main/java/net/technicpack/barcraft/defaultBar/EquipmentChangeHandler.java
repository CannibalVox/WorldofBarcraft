package net.technicpack.barcraft.defaultBar;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.technicpack.barcraft.WorldOfBarcraft;

public class EquipmentChangeHandler {

    @SubscribeEvent
    public void checkEquipment(TickEvent.PlayerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START || evt.player.getEntityWorld() == null || evt.player.getEntityWorld().isRemote)
            return;

        WorldOfBarcraft.proxy.getApi().recalculateActionConditions(evt.player, "barcraft.equipment");
    }
}
