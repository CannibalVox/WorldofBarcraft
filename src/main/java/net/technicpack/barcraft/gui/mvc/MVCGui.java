package net.technicpack.barcraft.gui.mvc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class MVCGui<Model extends IGuiModel, View extends IGuiView<Model>> extends GuiScreen {
    private View view;
    private Model model;
    private IGuiController controller;

    private int startDragX, startDragY;
    private Object draggedObject = null;

    public MVCGui(View view, Model model, IGuiController<Model, View> controller) {
        this.view = view;
        this.model = model;
        this.controller = controller;

        this.view.setModel(model);
        this.controller.setData(model, view);
    }

    @Override
    public void initGui() {
        super.initGui();

        model.initModel();
        view.initView(this.buttonList, this.fontRendererObj);
        controller.initController();
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);
        this.view.updateResolution(minecraft, width, height);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.zLevel = 0;
        view.preDrawBackground(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        view.drawBackground(mouseX, mouseY, partialTicks);
        view.postDrawBackground(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
        view.preDrawForeground(mouseX, mouseY, partialTicks);
        this.zLevel = -100;
        view.drawForeground(mouseX, mouseY, partialTicks);
        view.postDrawForeground(mouseX, mouseY, partialTicks);
    }

    /// When buttons are clicked in the GUI they come here
    @Override
    protected void actionPerformed(GuiButton button) {
        controller.triggerAction(button.id);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (controller.mouseClicked(mouseX, mouseY, mouseButton))
            return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && this.draggedObject == null) {
            this.draggedObject = controller.findDraggableObject(mouseX, mouseY);
            if (this.draggedObject != null) {
                startDragX = mouseX;
                startDragY = mouseY;
            }
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButton, long timeSinceClick) {
        super.mouseClickMove(mouseX, mouseY, lastButton, timeSinceClick);
        if (lastButton == 0 && this.draggedObject != null) {
            Object draggedObj = this.draggedObject;
            this.draggedObject = controller.moveDraggedObject(this.draggedObject, startDragX, startDragY, mouseX, mouseY, timeSinceClick);
            if (this.draggedObject == null)
                controller.releaseDraggedObject(draggedObj);
        }
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);

        if (mouseButton == 0 && this.draggedObject != null) {
            controller.releaseDraggedObject(this.draggedObject);
            this.draggedObject = null;
        }
    }
}
