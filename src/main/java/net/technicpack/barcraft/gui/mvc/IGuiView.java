package net.technicpack.barcraft.gui.mvc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

public interface IGuiView<Model extends IGuiModel> {
    void setModel(Model model);
    void initView(List buttonList, FontRenderer fontRenderer);
    void updateResolution(Minecraft minecraft, int width, int height);

    void preDrawBackground(int mouseX, int mouseY, float partialTicks);
    void drawBackground(int mouseX, int mouseY, float partialTicks);
    void postDrawBackground(int mouseX, int mouseY, float partialTicks);

    void preDrawForeground(int mouseX, int mouseY, float partialTicks);
    void drawForeground(int mouseX, int mouseY, float partialTicks);
    void postDrawForeground(int mouseX, int mouseY, float partialTicks);

    void drawDraggedObject(Object draggedObj, int mouseX, int mouseY);
}
