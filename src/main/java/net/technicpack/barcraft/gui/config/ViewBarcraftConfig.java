package net.technicpack.barcraft.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.gui.NineSquareRenderer;
import net.technicpack.barcraft.gui.TextWriter;
import net.technicpack.barcraft.gui.mvc.IGuiView;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewBarcraftConfig implements IGuiView<ModelBarcraftConfig> {

    private static final ResourceLocation backgroundTex = new ResourceLocation("barcraft", "textures/gui/actionConfigGui.png");

    private ModelBarcraftConfig model;
    private List buttonList;
    private TextWriter textWriter;
    private int zLevel = 0;

    private GuiButton actionsPageLeft = null;
    private GuiButton actionsPageRight = null;

    private NineSquareRenderer backing = new NineSquareRenderer(0, 0, 19, 17, 4, 4, 4, 4, 36, 32);
    private NineSquareRenderer grayBox = new NineSquareRenderer(0, 17, 12, 11, 4, 4, 4, 4, 36, 32);

    @Override
    public void setModel(ModelBarcraftConfig modelBarcraftConfig) {
        this.model = modelBarcraftConfig;
    }

    @Override
    public void initView(List buttonList, FontRenderer fontRenderer) {
        this.buttonList = buttonList;
        this.textWriter = new TextWriter(fontRenderer);
        actionsPageLeft = null;
        actionsPageRight = null;
        updateGuiStats();

        //If there's multiple bars, we need to set up the buttons.  They should be spaced out so the name of all
        //action bars can fit, so we'll get action bar with the widest name
        if (model.hasMultipleBars()) {
            int biggestTitleWidth = 0;
            for (IActionContainer bar : model) {
                String title = bar.getDisplayName();
                int titleWidth = this.textWriter.getStringWidth(title);
                if (titleWidth > biggestTitleWidth)
                    biggestTitleWidth = titleWidth;
            }

            //Get offset from center
            double centerOffset = ((biggestTitleWidth + 12) / model.getGuiStats().getGuiScale());
            //Draw the outside
            double y = model.getGuiStats().getGuiY() + model.getGuiStats().getGuiHeight() * 0.07;
            //buttonList.add(new GuiButton(0, (int)(model.getGuiStats().getGuiX() + model.getGuiStats().getGuiWidth()/2 - centerOffset - (16)), (int)(y-10), 16, 20, "<"));
            //buttonList.add(new GuiButton(1, (int)(model.getGuiStats().getGuiX() + model.getGuiStats().getGuiWidth()/2 + centerOffset), (int) (y-10), 16, 20, ">"));
        }

        pageUpdated();
    }

    @Override
    public void updateResolution(Minecraft minecraft, int width, int height) {
        updateGuiStats();
    }

    public void pageUpdated() {
        this.actionsPageLeft.visible = true;
        this.actionsPageRight.visible = true;
        if (model.getPageIndex() < model.getGuiStats().getActionCount())
            this.actionsPageLeft.visible = false;
        if (model.getPageIndex() >= model.getActionForBarCount() - model.getGuiStats().getActionCount())
            this.actionsPageRight.visible = false;
    }

    private void updateGuiStats() {
        if (actionsPageLeft != null)
            this.buttonList.remove(actionsPageLeft);
        if (actionsPageRight != null)
            this.buttonList.remove(actionsPageRight);

        if (model.getCurrentBar() != null) {
            int leftButtonX = (int)model.getGuiStats().getAbilityAreaX() + 6;
            int buttonY = (int)(model.getGuiStats().getAbilityAreaY() + (model.getGuiStats().getAbilityAreaHeight()/2) - 10);
            this.actionsPageLeft = new GuiButton(2, leftButtonX, buttonY, 16, 20, "<");
            this.buttonList.add(this.actionsPageLeft);

            int rightButtonX = (int)(model.getGuiStats().getAbilityAreaX() + model.getGuiStats().getAbilityAreaWidth() - (22 / model.getGuiStats().getGuiScale()));
            this.actionsPageRight = new GuiButton(3, rightButtonX, buttonY, 16, 20, ">");
            this.buttonList.add(this.actionsPageRight);
        }

        pageUpdated();
    }

    @Override
    public void preDrawBackground(int mouseX, int mouseY, float partialTicks) {

    }

    //Draws the background of the UI:
    // - The background
    // - the area boxes (action bar, action list, info)
    // - the action bar graphic & lock icons
    @Override
    public void drawBackground( int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        //Start drawing background
        mc.getTextureManager().bindTexture(backgroundTex);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        //Draw the outside
        backing.draw(tessellator, model.getGuiStats().getGuiX(), model.getGuiStats().getGuiY(), this.zLevel, model.getGuiStats().getGuiWidth(), model.getGuiStats().getGuiHeight());

        //Only draw the background & some crappy text if we somehow got here without a valid action bar
        if (model.getCurrentBar() == null)
            return;

        grayBox.draw(tessellator, model.getGuiStats().getBarAreaX(), model.getGuiStats().getBarAreaY(), this.zLevel, model.getGuiStats().getBarAreaWidth(), model.getGuiStats().getBarAreaHeight());
        grayBox.draw(tessellator, model.getGuiStats().getAbilityAreaX(), model.getGuiStats().getAbilityAreaY(),this.zLevel, model.getGuiStats().getAbilityAreaWidth(), model.getGuiStats().getAbilityAreaHeight());
        grayBox.draw(tessellator, model.getGuiStats().getInfoAreaX(), model.getGuiStats().getInfoAreaY(),this.zLevel, model.getGuiStats().getInfoAreaWidth(), model.getGuiStats().getInfoAreaHeight());

        double spacing = model.getCurrentBar().getRenderData().getBarSpacing() + model.getCurrentBar().getRenderData().getActionWidth();
        double lockX = model.getGuiStats().getActionLockX();
        double lockY = model.getGuiStats().getActionLockY();
        for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
            boolean isLocked = model.getCurrentBar().isLocked(i);
            drawImage(tessellator, lockX, lockY, 14 * model.getGuiStats().getActionBarScale(), 14 * model.getGuiStats().getActionBarScale(), 20, isLocked ? 0 : 16, 36, isLocked ? 16 : 32, 36, 32);

            if (model.getCurrentBar().getRenderData().isBarHorizontalOnScreen())
                lockX += spacing * model.getGuiStats().getActionBarScale();
            else
                lockY += spacing * model.getGuiStats().getActionBarScale();
        }

        //Draw a box around the selected action
        if (model.getActionForBarCount() > 0 && model.getCurrentAction() != null) {
            int i = model.getActionIndexOnPage();
            if (i >= 0 && i < model.getGuiStats().getActionCount()) {
                int selectedColumn = i % model.getGuiStats().getActionCountX();
                int selectedRow = i / model.getGuiStats().getActionCountX();
                double thickness = 2 / model.getGuiStats().getGuiScale();

                double actionX = model.getGuiStats().getAbilityAreaX() + model.getGuiStats().getActionAreaGutterWidth() + model.getGuiStats().getScaledActionSlotSize() * selectedColumn;
                double actionY = model.getGuiStats().getAbilityAreaY() + model.getGuiStats().getActionAreaGutterHeight() + model.getGuiStats().getScaledActionSlotSize() * selectedRow;

                drawImage(tessellator, actionX, actionY, model.getGuiStats().getScaledActionSlotSize(), thickness, 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX, actionY + thickness, thickness, model.getGuiStats().getScaledActionSlotSize() - (2 * thickness), 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX, actionY + model.getGuiStats().getScaledActionSlotSize() - thickness, model.getGuiStats().getScaledActionSlotSize(), thickness, 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX + model.getGuiStats().getScaledActionSlotSize() - thickness, actionY + thickness, thickness, model.getGuiStats().getScaledActionSlotSize() - (2 * thickness), 0, 31, 1, 32, 36, 32);
            }

            double descriptionSplitX = model.getGuiStats().getInfoAreaX() + 3;
            double descriptionSplitY = model.getGuiStats().getInfoAreaY() + (model.getGuiStats().getInfoAreaGutterHeight() * 1.5) + (model.getGuiStats().getScaledActionSize() * 1.2);
            drawImage(tessellator, descriptionSplitX, descriptionSplitY, model.getGuiStats().getInfoAreaWidth() - 6, 1, 0, 31, 1, 32, 36, 32);
        }

        tessellator.draw();
        mc.getTextureManager().bindTexture(model.getCurrentBar().getRenderData().getBarArt());
        tessellator.startDrawingQuads();

        //Draw the action bar
        drawImage(tessellator, model.getGuiStats().getActionBarX(), model.getGuiStats().getActionBarY(), model.getCurrentBar().getRenderData().getPixelWidth() * model.getGuiStats().getActionBarScale(), model.getCurrentBar().getRenderData().getPixelHeight() * model.getGuiStats().getActionBarScale(), 0, 0, model.getCurrentBar().getRenderData().getPixelWidth(), model.getCurrentBar().getRenderData().getPixelHeight(), 256, 256);

        tessellator.draw();
    }

    @Override
    public void postDrawBackground(int mouseX, int mouseY, float partialTicks) {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void preDrawForeground(int mouxeX, int mouseY, float partialTicks) {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        short short1 = 240;
        short short2 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) short1 / 1.0F, (float) short2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        if (model.getCurrentBar() == null)
            drawEmptyMessage();
        else
            drawGui();
    }


    //Called if no valid action bar is registered
    private void drawEmptyMessage() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        String title = I18n.format("gui.barcraft.noBars", new Object[0]);
        GL11.glPushMatrix();
        GL11.glScaled(2, 2, 2);
        this.textWriter.drawStaticText(sr.getScaledWidth_double()/4, sr.getScaledHeight_double()/4, title, TextWriter.CENTER, TextWriter.CENTER);
        GL11.glPopMatrix();
    }

    //Used to render actions & text if a valid bar is selected
    private void drawGui() {
        //
        String title = model.getCurrentBar().getDisplayName();
        GL11.glPushMatrix();
        GL11.glTranslated(model.getGuiStats().getGuiX() + (model.getGuiStats().getGuiWidth()/2), model.getGuiStats().getGuiY() + (model.getGuiStats().getGuiHeight() * 0.07), 0);
        double titleScale = 3 / model.getGuiStats().getGuiScale();
        GL11.glScaled(titleScale, titleScale, titleScale);
        this.textWriter.drawStaticText(1, 1, title, TextWriter.CENTER, TextWriter.CENTER);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);

        int abilityIndex = (model.getPageIndex() / model.getGuiStats().getActionCount()) * model.getGuiStats().getActionCount();
        for (int i = 0; i < model.getGuiStats().getActionCount(); i++) {
            if (i + abilityIndex >= model.getActionForBarCount())
                break;
            int abilityRow = i / model.getGuiStats().getActionCountX();
            int abilityColumn = i % model.getGuiStats().getActionCountX();
            IAction action = model.getAction(i + abilityIndex);
            String abilityName = action.getDisplayName();
            double abilityTextX = model.getGuiStats().getAbilityAreaX() + abilityColumn * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getActionAreaGutterWidth() + (model.getGuiStats().getScaledActionSlotSize() - model.getGuiStats().getScaledTextAreaWidth()) / 2;
            double abilityTextY = model.getGuiStats().getAbilityAreaY() + abilityRow * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getScaledActionTopGutter() + model.getGuiStats().getActionAreaGutterHeight() + model.getGuiStats().getScaledActionSlotSize() - model.getGuiStats().getScaledActionBottomGutter();
            textWriter.drawScaledText(abilityTextX, abilityTextY, model.getGuiStats().getScaledTextAreaWidth(), model.getGuiStats().getScaledTextAreaHeight(), abilityName);
        }

        if (model.getCurrentAction() != null) {
            IAction action = model.getCurrentAction();

            double textx = model.getGuiStats().getInfoAreaX() + (model.getGuiStats().getInfoAreaGutterWidth() * 2) + model.getGuiStats().getScaledActionSize();
            double texty = model.getGuiStats().getInfoAreaY() + model.getGuiStats().getInfoAreaGutterHeight();
            double textWidth = model.getGuiStats().getInfoAreaWidth() - (model.getGuiStats().getInfoAreaGutterWidth() * 3) - model.getGuiStats().getScaledActionSize();
            double textHeight = model.getGuiStats().getScaledActionSize() * 0.7;
            textWriter.drawScaledText(textx, texty, textWidth, textHeight, action.getDisplayName());

            double descx = model.getGuiStats().getInfoAreaX() + model.getGuiStats().getInfoAreaGutterWidth();
            double descy = model.getGuiStats().getInfoAreaY() + (model.getGuiStats().getInfoAreaGutterHeight() * 2) + (model.getGuiStats().getScaledActionSize() * 1.2);
            double descWidth = model.getGuiStats().getInfoAreaWidth() - (model.getGuiStats().getInfoAreaGutterWidth() * 2);
            double descHeight = model.getGuiStats().getInfoAreaHeight() - (model.getGuiStats().getInfoAreaGutterHeight() * 3) - (model.getGuiStats().getScaledActionSize() * 1.2);
            textWriter.drawScaledText(descx, descy, descWidth, descHeight, action.getDescription(), TextWriter.LEADING, TextWriter.LEADING);
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/atlas/abilities.png"));
        double actionX = model.getGuiStats().getActionBarX() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getX() * model.getGuiStats().getActionBarScale();
        double actionY = model.getGuiStats().getActionBarY() + (double) model.getCurrentBar().getRenderData().barGraphicInsets().getY() * model.getGuiStats().getActionBarScale();
        double actionSize = (double) model.getCurrentBar().getRenderData().getActionWidth() * model.getGuiStats().getActionBarScale();
        for (int i = 0; i < model.getCurrentBar().getActionCount(); i++) {
            IAction action = model.getCurrentBar().getAction(i);

            if (action == null)
                continue;

            if (!WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, action))
                tessellator.setColorOpaque(50, 50, 50);
            else
                tessellator.setColorOpaque(255, 255, 255);

            IIcon icon = action.getIcon();
            drawImage(tessellator, actionX, actionY, actionSize, actionSize, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
            actionX += model.getGuiStats().getActionBarSpacing();
        }

        for (int i = 0; i < model.getGuiStats().getActionCount(); i++) {
            if (i + abilityIndex >= model.getActionForBarCount())
                break;
            int abilityRow = i / model.getGuiStats().getActionCountX();
            int abilityColumn = i % model.getGuiStats().getActionCountX();
            IAction action = model.getAction(i + abilityIndex);
            IIcon icon = action.getIcon();
            double abilityIconX = model.getGuiStats().getAbilityAreaX() + abilityColumn * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getActionAreaGutterWidth() + model.getGuiStats().getScaledActionLeftGutter();
            double abilityIconY = model.getGuiStats().getAbilityAreaY() + abilityRow * model.getGuiStats().getScaledActionSlotSize() + model.getGuiStats().getActionAreaGutterHeight() + model.getGuiStats().getScaledActionTopGutter();
            drawImage(tessellator, abilityIconX, abilityIconY, model.getGuiStats().getScaledActionSize(), model.getGuiStats().getScaledActionSize(), icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
        }

        if (model.getCurrentAction() != null) {
            IAction action = model.getCurrentAction();
            IIcon icon = action.getIcon();

            double x = model.getGuiStats().getInfoAreaX() + model.getGuiStats().getInfoAreaGutterWidth();
            double y = model.getGuiStats().getInfoAreaY() + model.getGuiStats().getInfoAreaGutterHeight();
            drawImage(tessellator, x, y, model.getGuiStats().getScaledActionSize(), model.getGuiStats().getScaledActionSize(), icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
        }
        tessellator.draw();
    }

    @Override
    public void postDrawForeground(int mouseX, int mouseY, float partialTicks) {
        GL11.glEnable(GL11.GL_LIGHTING);

//        if (this.draggedAction != null) {
//
//        }

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    //Draw GUI art without forcing a 256x256 art size
    private void drawImage(Tessellator tessellator, double x, double y, double width, double height, double u, double v, double maxU, double maxV, double texWidth, double texHeight) {
        tessellator.addVertexWithUV(x, y + height, (double) this.zLevel, u / texWidth, maxV / texHeight);
        tessellator.addVertexWithUV(x + width, y + height, (double) this.zLevel, maxU / texWidth, maxV / texHeight);
        tessellator.addVertexWithUV(x + width, y, (double) this.zLevel, maxU / texWidth, v / texHeight);
        tessellator.addVertexWithUV(x, y, (double) this.zLevel, u / texWidth, v / texHeight);
    }
}
