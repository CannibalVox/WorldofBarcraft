package net.technicpack.barcraft.gui.mvc;

public interface IGuiController<Model extends IGuiModel, View extends IGuiView<Model>> {
    void setData(Model model, View view);
    void initController();
    void triggerAction(int id);
    boolean mouseClicked(int mouseX, int mouseY, int mouseButton);
}
