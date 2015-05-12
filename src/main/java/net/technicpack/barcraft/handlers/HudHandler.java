package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class HudHandler {

    private final ResourceLocation actionBar = new ResourceLocation("barcraft", "textures/gui/actionBar.png");

    public HudHandler() {

    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void hudRenderTick(TickEvent.RenderTickEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.START)
            return;

        if (!minecraft.inGameHasFocus || !minecraft.isGuiEnabled())
            return;

        EntityLivingBase entity = minecraft.renderViewEntity;
        if (entity == null || !(entity instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer)entity;

        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, sr.getScaledHeight() - 32, -2000.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        minecraft.getTextureManager().bindTexture(actionBar);

        GL11.glPushMatrix();
        //Draw a thing
        GL11.glTranslatef(sr.getScaledWidth()/2-212, 16, -90);
        GL11.glScaled(0.9, 0.9, 0.9);
        drawTexturedModalRect(8, -5, 0, 0, 122, 22);
        GL11.glPopMatrix();

        GL11.glDisable(3042);

        GL11.glPopMatrix();
    }

    private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), 0, (double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height),0, (double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), 0, (double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), 0, (double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1));
        tessellator.draw();
    }
}
