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
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IOnScreenBar;
import net.technicpack.barcraft.impl.BarcraftClientApi;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import javax.vecmath.Vector3d;

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

        BarcraftClientApi api = (BarcraftClientApi)WorldOfBarcraft.proxy.getApi();

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

        for (IActionContainer container : api.getActionContainerRegistry().getActionBars()) {
            if (container.getRenderData() == null || !container.getRenderData().appearsOnScreen())
                continue;

            IOnScreenBar renderData = container.getRenderData();

            minecraft.getTextureManager().bindTexture(renderData.getBarArt());
            GL11.glPushMatrix();
            Vector3d position = renderData.barScreenPosition(sr);
            GL11.glTranslated(position.x, position.y, position.z);
            double scale = renderData.getOnScreenScale();
            GL11.glScaled(scale, scale, scale);
            drawTexturedModalRect(0, 0, 0, 0, renderData.getPixelWidth(), renderData.getPixelHeight());
            GL11.glPopMatrix();
        }

        minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/atlas/abilities.png"));
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();

        for (IActionContainer container : api.getActionContainerRegistry().getActionBars()) {
            if (container.getRenderData() == null || !container.getRenderData().appearsOnScreen())
                continue;

            IOnScreenBar renderData = container.getRenderData();

            double barSize = renderData.getActionWidth();
            double targetSize = barSize;
            Rectangle insets = renderData.barGraphicInsets();
            double spacing = renderData.getBarSpacing() + barSize;

            GL11.glPushMatrix();
            Vector3d position = renderData.barScreenPosition(sr);
            GL11.glTranslated(position.x, position.y, position.z);
            double scale = renderData.getOnScreenScale();
            GL11.glScaled(scale, scale, scale);

            for (int i = 0; i < container.getActionCount(); i++) {
                IAction action = container.getAction(i);

                if (action == null)
                    continue;

                IIcon icon = action.getIcon();
                double sourceSize = icon.getIconWidth();
                double iconScale = targetSize / sourceSize;
                GL11.glPushMatrix();
                double translateX = insets.getX()*scale;
                double translateY = insets.getY()*scale;
                double offset = i*spacing;

                if (renderData.isBarHorizontalOnScreen())
                    translateX += offset;
                else
                    translateY += offset;
                GL11.glTranslated(translateX, translateY, 0);
                GL11.glScaled(iconScale, iconScale, iconScale);
                renderIcon(0, 0, action.getIcon(), action.getIcon().getIconWidth(), action.getIcon().getIconHeight());

                GL11.glPopMatrix();
            }


            GL11.glPopMatrix();
        }

        for (IActionContainer container : api.getActionContainerRegistry().getActionBars()) {
            if (container.getRenderData() == null || !container.getRenderData().appearsOnScreen() || !container.getRenderData().drawKeyboardData())
                continue;

            IOnScreenBar renderData = container.getRenderData();

            double barSize = renderData.getActionWidth();
            double targetSize = barSize;
            Rectangle insets = renderData.barGraphicInsets();
            double spacing = renderData.getBarSpacing() + barSize;

            GL11.glPushMatrix();
            Vector3d position = renderData.barScreenPosition(sr);
            GL11.glTranslated(position.x, position.y, position.z);
            double scale = renderData.getOnScreenScale();
            GL11.glScaled(scale, scale, scale);

            for (int i = 0; i < container.getActionCount(); i++) {
                IAction action = container.getAction(i);

                if (action == null)
                    continue;

                GL11.glPushMatrix();
                double translateX = insets.getX()*scale;
                double translateY = insets.getY()*scale;
                double offset = i*spacing;

                if (renderData.isBarHorizontalOnScreen())
                    translateX += offset;
                else
                    translateY += offset;
                GL11.glTranslated(translateX, translateY, 0);
                KeyBinding bind = container.getKeybindingForAction(i);

                if (bind == null)
                    continue;

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

                double textScale = targetSize / 22.0;
                GL11.glScaled(textScale, textScale, textScale);

                minecraft.fontRendererObj.drawString(keyBindText, 21 - width, 13, 0);
                minecraft.fontRendererObj.drawString(keyBindText, 21 - width - 1, 14, 0xFFFFFF);
                GL11.glColor4f(1, 1, 1, 1);

                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
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
