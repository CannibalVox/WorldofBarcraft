package net.technicpack.barcraft.gui.mvc;

public interface IGuiController<Model extends IGuiModel, View extends IGuiView<Model>> {
    void setData(Model model, View view);
    void initController();
    void triggerAction(int id);
    void mouseClicked(int mouseX, int mouseY, int mouseButton);

    Object findDraggableObject(int mouseX, int mouseY);
    Object moveDraggedObject(Object dragObj, int startX, int startY, int mouseX, int mouseY, long timeSinceClick);
    void releaseDraggedObject(Object dragObj, int startX, int startY, int mouseX, int mouseY);
}
