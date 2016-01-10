package net.technicpack.barcraft.gui.config;

import net.minecraft.client.Minecraft;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.gui.BarcraftGuiStats;
import net.technicpack.barcraft.gui.mvc.IGuiController;

public class ControllerBarcraftConfig implements IGuiController<ModelBarcraftConfig, ViewBarcraftConfig> {

    private ModelBarcraftConfig model;
    private ViewBarcraftConfig view;
    private double startScrollPos = 0;

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
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (model.getCurrentBar() == null)
            return;

        double lockX = model.getGuiStats().getActionLockX();
        double lockY = model.getGuiStats().getActionLockY();
        for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
            if (mouseX >= lockX && mouseY >= lockY && mouseX < lockX + model.getGuiStats().getActionBarSpacing() && mouseY < lockY + model.getGuiStats().getActionBarSpacing()) {
                model.getCurrentBar().setLocked(i, !model.getCurrentBar().isLocked(i));

                if (!model.getCurrentBar().isLocked(i) && model.getCurrentBar().getAction(i) != null && !WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, model.getCurrentBar().getAction(i)))
                    model.getCurrentBar().setAction(i, null);
                return;
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
                view.refreshScrollbar();
                return;
            }
        }

        double scrollWidth = 4 * model.getGuiStats().getGuiScale();
        double scrollX = model.getGuiStats().getInfoAreaX() + model.getGuiStats().getInfoAreaWidth() - model.getGuiStats().getInfoAreaGutterWidth() - scrollWidth;
        double scrollTrackY = model.getGuiStats().getInfoAreaY() + model.getGuiStats().getInfoAreaGutterHeight();
        double scrollTrackHeight = model.getGuiStats().getInfoAreaHeight() - (2 * model.getGuiStats().getInfoAreaGutterHeight());

        double trackPct = 1 - model.getScrollPct();
        double scrollThumbY = scrollTrackY + (scrollTrackHeight * trackPct * model.getScrollPos());
        double scrollThumbHeight = scrollTrackHeight * model.getScrollPct();

        if (mouseX >= scrollX && mouseX < scrollX + scrollWidth) {
            if (mouseY >= scrollTrackY && mouseY < scrollThumbY) {
                punchScrollUp();
                return;
            }

            if (mouseY >= (scrollThumbY + scrollThumbHeight) && mouseY < (scrollTrackY + scrollTrackHeight)) {
                punchScrollDown();
                return;
            }
        }
    }

    private void punchScrollUp() {
        double trackPct = 1 - model.getScrollPct();
        double thumbAsTrackPct = model.getScrollPct() / trackPct;
        double newPos = Math.max(0, model.getScrollPos() - thumbAsTrackPct);
        model.setScrollPos(newPos);
    }

    private void punchScrollDown() {
        double trackPct = 1 - model.getScrollPct();
        double thumbAsTrackPct = model.getScrollPct() / trackPct;
        double newPos = Math.min(1, model.getScrollPos() + thumbAsTrackPct);
        model.setScrollPos(newPos);
    }

    private void dragScroll(int deltaY) {
        double trackPct = 1 - model.getScrollPct();
        double descHeight = model.getGuiStats().getInfoAreaHeight() - (model.getGuiStats().getInfoAreaGutterHeight() * 3) - (model.getGuiStats().getScaledActionSize() * 1.2);

        double newScrollPos = startScrollPos + (deltaY / (descHeight * trackPct*model.getGuiStats().getGuiScale()));
        newScrollPos = Math.max(0, Math.min(1, newScrollPos));
        model.setScrollPos(newScrollPos);
    }

    private class RemovalDragObj {
        private int index;

        public RemovalDragObj(int index) {
            this.index = index;
        }

        public int getActionIndex() { return index; }
    }

    @Override
    public Object findDraggableObject(int mouseX, int mouseY) {

        double scrollY = model.getGuiStats().getInfoAreaY() + model.getGuiStats().getInfoAreaGutterHeight();
        double scrollHeight = model.getGuiStats().getInfoAreaHeight() - (2 * model.getGuiStats().getInfoAreaGutterHeight());
        double thumbHeight = scrollHeight * model.getScrollPct();
        double trackPct = 1 - model.getScrollPct();
        double thumbY = scrollY + (scrollHeight * trackPct * model.getScrollPos());

        double scrollAreaWidth = 4 * model.getGuiStats().getGuiScale();
        double scrollX = model.getGuiStats().getInfoAreaX() + model.getGuiStats().getInfoAreaWidth() - model.getGuiStats().getInfoAreaGutterWidth();
        scrollX -= scrollAreaWidth;

        if (mouseX >= scrollX && mouseX < scrollX + scrollAreaWidth && mouseY >= thumbY && mouseY < (thumbY + thumbHeight)) {
            startScrollPos = model.getScrollPos();
            return "scroll";
        }

        double actionX = model.getGuiStats().getActionBarX() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getX() * model.getGuiStats().getActionBarScale();
        double actionY = model.getGuiStats().getActionBarY() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getY() * model.getGuiStats().getActionBarScale();
        double actionSize = (double) model.getCurrentBar().getRenderData().getActionWidth() * model.getGuiStats().getActionBarScale();

        if (model.getCurrentBar() != null) {
            for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
                if (mouseX >= actionX && mouseY >= actionY && mouseX < actionX + actionSize && mouseY < actionY + actionSize) {
                    return new RemovalDragObj(i);
                }
                actionX += model.getGuiStats().getActionBarSpacing();
            }
        }

        int abilityIndex = (model.getPageIndex() / model.getGuiStats().getActionCount()) * model.getGuiStats().getActionCount();
        for (int i = 0; i < model.getGuiStats().getActionCount(); i++) {
            if (i + abilityIndex >= model.getActionForBarCount())
                break;
            int abilityRow = i / model.getGuiStats().getActionCountX();
            int abilityColumn = i % model.getGuiStats().getActionCountX();
            double abilityIconX = model.getGuiStats().getAbilityAreaX() + abilityColumn * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getActionAreaGutterWidth() + model.getGuiStats().getScaledActionLeftGutter();
            double abilityIconY = model.getGuiStats().getAbilityAreaY() + abilityRow * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getActionAreaGutterHeight() + model.getGuiStats().getScaledActionTopGutter();
            if (mouseX >= abilityIconX && mouseY >= abilityIconY && mouseX < abilityIconX + model.getGuiStats().getScaledActionSize() && mouseY < abilityIconY + model.getGuiStats().getScaledActionSize()) {
                return model.getAction(i + abilityIndex);
            }
        }

        return null;
    }

    @Override
    public Object moveDraggedObject(Object dragObj, int startX, int startY, int mouseX, int mouseY, long timeSinceClick) {
        if (dragObj.equals("scroll")) {
            dragScroll(mouseY-startY);
        } else if (dragObj instanceof RemovalDragObj) {
            if (Math.abs(startX - mouseX) > 5 || Math.abs(startY - mouseY) > 5) {
                if (model.getCurrentBar() != null) {
                    model.getCurrentBar().setAction(((RemovalDragObj)dragObj).getActionIndex(), null);
                }
                return null;
            }
        }
        return dragObj;
    }

    @Override
    public void releaseDraggedObject(Object dragObj, int startX, int startY, int mouseX, int mouseY) {
        if (dragObj instanceof IAction && model.getCurrentBar() != null) {
            double actionX = model.getGuiStats().getActionBarX() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getX() * model.getGuiStats().getActionBarScale();
            double actionY = model.getGuiStats().getActionBarY() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getY() * model.getGuiStats().getActionBarScale();
            double actionSize = (double) model.getCurrentBar().getRenderData().getActionWidth() * model.getGuiStats().getActionBarScale();

            if (model.getCurrentBar() != null) {
                for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
                    if (mouseX >= actionX && mouseY >= actionY && mouseX < actionX + actionSize && mouseY < actionY + actionSize) {
                        model.getCurrentBar().setAction(i, (IAction)dragObj);
                        return;
                    }
                    actionX += model.getGuiStats().getActionBarSpacing();
                }
            }
        }
    }
}
