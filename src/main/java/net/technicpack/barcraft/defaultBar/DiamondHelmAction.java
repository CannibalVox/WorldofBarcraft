package net.technicpack.barcraft.defaultBar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

public class DiamondHelmAction extends DummyAction {
    public DiamondHelmAction(String key, String displayName, String iconPath) {
        super(key, displayName, iconPath);
    }

    @Override
    public String getConditionKey() {
        return "barcraft.equipment";
    }

    @Override
    public boolean calculateAccessCondition(EntityPlayer player) {
        return player.getEquipmentInSlot(4) != null && player.getEquipmentInSlot(4).getItem() == Items.diamond_helmet;
    }
}
