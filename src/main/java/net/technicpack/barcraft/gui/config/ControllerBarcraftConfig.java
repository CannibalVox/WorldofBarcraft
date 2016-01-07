package net.technicpack.barcraft.gui.config;

import net.minecraft.client.Minecraft;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.gui.BarcraftGuiStats;
import net.technicpack.barcraft.gui.mvc.IGuiController;

public class ControllerBarcraftConfig implements IGuiController<ModelBarcraftConfig, ViewBarcraftConfig> {

    private ModelBarcraftConfig model;
    private ViewBarcraftConfig view;

    @Override
    public void setData(ModelBarcraftConfig model, ViewBarcraftConfig view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void initController() {

    }

    @Override
    public void triggerAction(int id) {
        switch(id) {
            case 0:
                model.selectPrevBar();
                break;
            case 1:
                model.selectNextBar();
                break;
            case 2:
                //Select action sheet: left
                int realIndex = (model.getPageIndex() / model.getGuiStats().getActionCount()) * model.getGuiStats().getActionCount();
                model.setActionPage(realIndex - model.getGuiStats().getActionCount());
                view.pageUpdated();
                break;
            case 3:
                //Selection action sheet: right
                int currentIndex = (model.getPageIndex() / model.getGuiStats().getActionCount()) * model.getGuiStats().getActionCount();
                model.setActionPage(currentIndex + model.getGuiStats().getActionCount());
                view.pageUpdated();
                break;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (model.getCurrentBar() == null)
            return false;

        double lockX = model.getGuiStats().getActionLockX();
        double lockY = model.getGuiStats().getActionLockY();
        for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
            if (mouseX >= lockX && mouseY >= lockY && mouseX < lockX + model.getGuiStats().getActionBarSpacing() && mouseY < lockY + model.getGuiStats().getActionBarSpacing()) {
                model.getCurrentBar().setLocked(i, !model.getCurrentBar().isLocked(i));

                if (!model.getCurrentBar().isLocked(i) && model.getCurrentBar().getAction(i) != null && !WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, model.getCurrentBar().getAction(i)))
                    model.getCurrentBar().setAction(i, null);
                return true;
            }
            if (model.getCurrentBar().getRenderData().isBarHorizontalOnScreen())
                lockX += model.getGuiStats().getActionBarSpacing();
            else
                lockY += model.getGuiStats().getActionBarSpacing();
        }

        for (int i = 0; i < model.getGuiStats().getActionCount(); i++) {
            if ((i+model.getPageIndex()) >= model.getActionForBarCount())
                break;

            int actionColumn = i % model.getGuiStats().getActionCountX();
            int actionRow = i / model.getGuiStats().getActionCountX();
            int x = (int)(model.getGuiStats().getAbilityAreaX() + model.getGuiStats().getActionAreaGutterWidth() + (model.getGuiStats().getScaledActionSlotSize() * actionColumn));
            int y = (int)(model.getGuiStats().getAbilityAreaY() + model.getGuiStats().getActionAreaGutterHeight() + (model.getGuiStats().getScaledActionSlotSize() * actionRow));
            if (mouseX >= x && mouseY >= y && mouseX < x+model.getGuiStats().getScaledActionSlotSize() && mouseY < y+model.getGuiStats().getScaledActionSlotSize()) {
                model.selectAbility(i+model.getPageIndex());
                return true;
            }
        }

        return false;
    }
}
