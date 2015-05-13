package net.technicpack.barcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modwarriors.notenoughkeys.keys.KeyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.technicpack.barcraft.WorldOfBarcraft;
import org.lwjgl.input.Keyboard;
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
        GL11.glTranslatef(sr.getScaledWidth() / 2 - 212, 16, -90);
        GL11.glScaled(0.9, 0.9, 0.9);
        drawTexturedModalRect(8, -5, 0, 0, 122, 22);
        GL11.glScaled(0.6, 0.6, 0.6);
        for (int i = 0; i < 6; i++) {
            GL11.glTranslatef(33.3f, 0, 0);

            minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/atlas/abilities.png"));
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glPushMatrix();
            GL11.glScaled(1.6667, 1.6667, 1.6667);
            renderIcon(-9, -2, ActionBarHandler.actionIcons[i], 16, 16);
            GL11.glPopMatrix();

            KeyBinding bind = WorldOfBarcraft.proxy.getActionBarBinding(i);
            boolean[] modifiers = KeyHelper.alternates.get(bind.getKeyDescription());
            String keyBindText = "";
            if (modifiers[0])
                keyBindText += "S";
            if (modifiers[1])
                keyBindText += "C";
            if (modifiers[2])
                keyBindText += "A";
            keyBindText += Keyboard.getKeyName(bind.getKeyCode());
            int width = minecraft.fontRendererObj.getStringWidth(keyBindText);
            minecraft.fontRendererObj.drawString(keyBindText, 12 - width, 14, 0);
            minecraft.fontRendererObj.drawString(keyBindText, 11 - width, 13, 0xFFFFFF);
            GL11.glColor4f(1, 1, 1, 1);
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
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

    private void renderIcon(int p_94149_1_, int p_94149_2_, IIcon p_94149_3_, int p_94149_4_, int p_94149_5_)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_94149_1_ + 0.5), (double)(p_94149_2_ + p_94149_5_), (double)0, (double)p_94149_3_.getMinU(), (double)p_94149_3_.getMaxV());
        tessellator.addVertexWithUV((double)(p_94149_1_ + 0.5 + p_94149_4_), (double)(p_94149_2_ + p_94149_5_), (double)0, (double)p_94149_3_.getMaxU(), (double)p_94149_3_.getMaxV());
        tessellator.addVertexWithUV((double)(p_94149_1_ + 0.5 + p_94149_4_), (double)(p_94149_2_ + 0), (double)0, (double)p_94149_3_.getMaxU(), (double)p_94149_3_.getMinV());
        tessellator.addVertexWithUV((double)(p_94149_1_ + 0.5), (double)(p_94149_2_ + 0), (double)0, (double)p_94149_3_.getMinU(), (double)p_94149_3_.getMinV());
        tessellator.draw();
    }
}
