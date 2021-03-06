package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.technicpack.barcraft.gui.BarcraftInventoryButton;
import net.technicpack.barcraft.gui.config.ControllerBarcraftConfig;
import net.technicpack.barcraft.gui.config.ModelBarcraftConfig;
import net.technicpack.barcraft.gui.config.ViewBarcraftConfig;
import net.technicpack.barcraft.gui.mvc.MVCGui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InventoryModHandler {

    private Method isHiddenMethod;

    public InventoryModHandler() {
        try {
            Class config = Class.forName("codechicken.nei.NEIClientConfig");
            isHiddenMethod = config.getMethod("isHidden");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    @SideOnly(value = Side.CLIENT)
    @SubscribeEvent
    public void initInventory(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiInventory) {
            int xSize = 176;
            int ySize = 166;

            int guiLeft = (event.gui.width - xSize) / 2;
            int guiTop = (event.gui.height - ySize) / 2;

            if (!event.gui.mc.thePlayer.getActivePotionEffects().isEmpty() && isNEIHidden()) {
                guiLeft = 160 + (event.gui.width - xSize - 200) / 2;
            }

            event.buttonList.add(new BarcraftInventoryButton(7391, guiLeft + 149, guiTop + 6, 20, 20, "", false));
        } else if (event.gui instanceof GuiContainerCreative) {
            GuiContainerCreative creative = (GuiContainerCreative) event.gui;
            int guiCenterX = event.gui.width / 2;
            int guiCenterY = event.gui.height / 2;

            event.buttonList.add(new BarcraftInventoryButton(7391, guiCenterX+72, guiCenterY-62, 20, 20, "", true));
        }
    }

    @SideOnly(value = Side.CLIENT)
    @SubscribeEvent
    public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.gui instanceof GuiInventory || event.gui instanceof GuiContainerCreative) {
            if (event.button.id == 7391) {
                //Clicked barcraft button
                Minecraft.getMinecraft().displayGuiScreen(new MVCGui<ModelBarcraftConfig, ViewBarcraftConfig>(new ViewBarcraftConfig(), new ModelBarcraftConfig(), new ControllerBarcraftConfig()));
            }
        }
    }

    private boolean isNEIHidden() {
        if (isHiddenMethod == null)
            return true;

        try {
            return (Boolean) isHiddenMethod.invoke(null);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return true;
    }
}
