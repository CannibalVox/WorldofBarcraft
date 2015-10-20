package net.technicpack.barcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.technicpack.barcraft.WorldOfBarcraft;
import org.lwjgl.opengl.GL11;

public class BarcraftInventoryButton extends GuiButton {
    private static final ResourceLocation buttonTex = new ResourceLocation("barcraft","textures/gui/inventoryButton.png");

    public BarcraftInventoryButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public BarcraftInventoryButton(int stateName, int id, int p_i1021_3_, int p_i1021_4_, int p_i1021_5_, String p_i1021_6_) {
        super(stateName, id, p_i1021_3_, p_i1021_4_, p_i1021_5_, p_i1021_6_);
        if (!WorldOfBarcraft.proxy.getApi().getActionContainerRegistry().getActionBars().iterator().hasNext())
            this.visible = false;
    }

    @Override
    public void drawButton(Minecraft mc, int xx, int yy) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(buttonTex);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = xx >= this.xPosition && yy >= this.yPosition && xx < this.xPosition + this.width && yy < this.yPosition + this.height;
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV((double)(this.xPosition + 0), (double)(this.yPosition + height), (double)this.zLevel, 0, 1.0);
            tessellator.addVertexWithUV((double)(this.xPosition + width), (double)(this.yPosition + height), (double)this.zLevel, 1.0, 1.0);
            tessellator.addVertexWithUV((double)(this.xPosition + width), (double)(this.yPosition + 0), (double)this.zLevel, 1.0, 0);
            tessellator.addVertexWithUV((double)(this.xPosition + 0), (double)(this.yPosition + 0), (double)this.zLevel, 0, 0);
            tessellator.draw();

            this.mouseDragged(mc, xx, yy);
        }
    }
}
