package net.technicpack.barcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IActionContainerRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Iterator;

public class GuiBarcraftConfig extends GuiScreen {
    private static final ResourceLocation backgroundTex = new ResourceLocation("barcraft", "textures/gui/actionConfigGui.png");

    private IActionContainerRegistry containerRegistry = null;
    private IActionContainer selectedBar = null;
    private boolean hasMultipleBars = false;

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        containerRegistry = null;
        selectedBar = null;
        containerRegistry = WorldOfBarcraft.proxy.getApi().getActionContainerRegistry();

        //We need a handful of things to manage the bar selection: the current selected bar, and
        //we need to know if there's 1 or many bars to select from.  If we have multiple, the left/
        //right buttons will be visible & just loop through the options.  Only one, though, and we'll
        //hide the selection buttons.

        //Only pay attention to action bars with render data that are set to show up in the config gui
        if (containerRegistry != null) {
            Iterator<IActionContainer> container = containerRegistry.getActionBars().iterator();
            selectedBar = getNextValidBar(container);
            if (getNextValidBar(container) != null)
                hasMultipleBars = true;
        }

        //If there's multiple bars, we need to set up the buttons.  They should be spaced out so the name of all
        //action bars can fit, so we'll get action bar with the widest name
        if (hasMultipleBars) {
            int biggestTitleWidth = 0;
            Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();
            IActionContainer nextContainer = getNextValidBar(iterator);
            while (nextContainer != null) {
                String title = StatCollector.translateToLocal(nextContainer.getDisplayName());
                int titleWidth = this.fontRendererObj.getStringWidth(title);
                if (titleWidth > biggestTitleWidth)
                    biggestTitleWidth = titleWidth;
                nextContainer = getNextValidBar(iterator);
            }

            //Get offset from center
            int centerOffset = (biggestTitleWidth / 2) + 6;
            //Draw the outside
            double height = sr.getScaledHeight_double();
            double y = height * 0.05;
            buttonList.add(new GuiButton(0, (sr.getScaledWidth() / 2) - centerOffset - 16, (int) y + 5, 16, 20, "<"));
            buttonList.add(new GuiButton(1, (sr.getScaledWidth() / 2) + centerOffset, (int) y + 5, 16, 20, ">"));
        }
    }

    /// Given an action container iterator, pulls the next container that belongs in this menu
    private IActionContainer getNextValidBar(Iterator<IActionContainer> iterator) {
        while (iterator.hasNext()) {
            IActionContainer testBar = iterator.next();
            if (testBar.getRenderData() != null && testBar.getRenderData().appearsInMenu()) {
                return testBar;
            }
        }

        return null;
    }

    /// When buttons are clicked in the GUI they come here
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            //Select action bar: left
            selectPrevBar();
        } else if (button.id == 1) {
            //Select action bar: right
            selectNextBar();
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (selectedBar == null)
            return;

        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);

        //Draw the outside
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double();
        double x = width * 0.05;
        double y = height * 0.05;
        width *= 0.9;
        height *= 0.9;

        double barAreaWidth;
        double barAreaHeight;
        double barAreaX = x + width * 0.03;
        double barAreaY = y + height * 0.12;
        if (selectedBar.getRenderData().isBarHorizontalOnScreen()) {
            barAreaWidth = width * 0.94;
            barAreaHeight = height * 0.25;
        } else {
            barAreaWidth = width * 0.21;
            barAreaHeight = height * 0.85;
        }

        //Check for lock clicks
        //We need to calculate the correct scaling for the action bar.  We scale it to fit the bar area
        double pixelWidth = selectedBar.getRenderData().getPixelWidth();
        double pixelHeight = selectedBar.getRenderData().getPixelHeight();

        //Take into account the area taken up by the lock images.
        if (selectedBar.getRenderData().isBarHorizontalOnScreen())
            pixelHeight += 18;
        else
            pixelWidth += 18;

        //To make sure it gets scaled to fit the area, we take the correct scaling of the width & height, and then
        //use the smaller of the two
        double widthScale = (barAreaWidth - 8) / pixelWidth;
        double heightScale = (barAreaHeight - 8) / pixelHeight;
        double barScale = Math.min(widthScale, heightScale);

        //We want to leave some margin between the bottom & the top, so if we're using the width scaling, then
        //add some to the bar X, otherwise the bar Y
        double barXOffset = (widthScale == barScale) ? 3 : 0;
        double barYOffset = (heightScale == barScale) ? 3 : 0;
        double barX = barAreaX + (barAreaWidth / 2.0) - (barScale * pixelWidth * 0.5) + barXOffset;
        double barY = barAreaY + (barAreaHeight / 2.0) - (barScale * pixelHeight * 0.5) + barYOffset;

        //Draw the locks first so that they can be drawn before switching over to the bar's texture
        double lockX = barX+1;
        double lockY = barY+1;
        if (selectedBar.getRenderData().isBarHorizontalOnScreen()) {
            lockY += selectedBar.getRenderData().getPixelHeight() * barScale;
            lockX += selectedBar.getRenderData().barGraphicInsets().getX() * barScale;
        } else {
            lockX += selectedBar.getRenderData().getPixelWidth() * barScale;
            lockY += selectedBar.getRenderData().barGraphicInsets().getY() * barScale;
        }

        double lockSize = 14 * barScale;
        double lockSpacing = (selectedBar.getRenderData().getBarSpacing() + selectedBar.getRenderData().getActionWidth()) * barScale;
        for (int i = 0; i < selectedBar.getActionCount(); i++) {
            if (mouseX >= lockX && mouseY >= lockY && mouseX < lockX + lockSize && mouseY < lockY + lockSize) {
                selectedBar.setLocked(i, !selectedBar.isLocked(i));
                return;
            }
            if (selectedBar.getRenderData().isBarHorizontalOnScreen())
                lockX += lockSpacing;
            else
                lockY += lockSpacing;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    //Clicked the < button to select a different action bar (button only appears when multiple action bars are available
    //to select)
    private void selectPrevBar() {
        Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();
        IActionContainer current = getNextValidBar(iterator);

        //Loop through valid bars until you find the currently-selected one
        //then grab the previous
        IActionContainer previous = null;
        while (current != null) {
            if (current == selectedBar)
                break;
            previous = current;
            current = getNextValidBar(iterator);
        }

        //If we were already at the first action bar (or something went wrong), select the very last bar
        if (previous == null)
            selectLastBar();
        else
            setSelectedBar(previous);
    }

    //Clicked the > button to select a different action bar (button only appears hwen multiple action bars are available
    //to select)
    private void selectNextBar() {
        Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();

        //Loop through valid bars until you find the currently-selected one
        //then grab the very next
        IActionContainer current = getNextValidBar(iterator);
        while (current != null) {
            if (current == selectedBar) {
                IActionContainer next = getNextValidBar(iterator);
                //If we were already on the last bar (or something went wrong), grab the very first bar
                if (next == null)
                    selectFirstBar();
                else
                    setSelectedBar(next);
                return;
            }
            current = getNextValidBar(iterator);
        }


        selectFirstBar();
    }

    //Change selected action bar to the first registered valid action bar
    private void selectFirstBar() {
        Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();
        setSelectedBar(getNextValidBar(iterator));
    }

    //Change selected action bar to the last registered valid action bar
    private void selectLastBar() {
        Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();

        //Get the final valid bar in the list
        IActionContainer current = getNextValidBar(iterator);
        IActionContainer last = null;
        while (current != null) {
            last = current;
            current = getNextValidBar(iterator);
        }
        setSelectedBar(last);
    }

    //Changes the selected action bar & fires any GUI events necessary for fixing up the UI
    protected void setSelectedBar(IActionContainer bar) {
        this.selectedBar = bar;
    }

    //Draws the background of the UI:
    // - The background
    // - the area boxes (action bar, action list, info)
    // - the action bar graphic & lock icons
    private void drawBackground(float partialTicks, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);

        //Start drawing background
        mc.getTextureManager().bindTexture(backgroundTex);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        //Draw the outside
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double();
        double x = width * 0.05;
        double y = height * 0.05;
        width *= 0.9;
        height *= 0.9;

        drawImage(tessellator, x, y, 5, 5, 0, 0, 5, 5, 36, 32);
        drawImage(tessellator, x + 5, y, width - 9, 5, 5, 0, 15, 5, 36, 32);
        drawImage(tessellator, x + (width - 4), y, 4, 5, 15, 0, 20, 5, 36, 32);

        drawImage(tessellator, x, y + 5, 5, height - 10, 0, 5, 5, 12, 36, 32);
        drawImage(tessellator, x + 5, y + 5, width - 9, height - 10, 5, 5, 15, 12, 36, 32);
        drawImage(tessellator, x + (width - 4), y + 5, 4, height - 10, 15, 5, 20, 12, 36, 32);

        drawImage(tessellator, x, y + (height - 5), 5, 5, 0, 12, 5, 17, 36, 32);
        drawImage(tessellator, x + 5, y + (height - 5), width - 9, 5, 5, 12, 15, 17, 36, 32);
        drawImage(tessellator, x + (width - 4), y + (height - 5), 4, 5, 15, 12, 20, 17, 36, 32);

        //Only draw the background & some crappy text if we somehow got here without a valid action bar
        if (selectedBar == null)
            return;

        double barAreaWidth = 0;
        double barAreaHeight = 0;
        double abilityAreaWidth = 0;
        double abilityAreaHeight = 0;
        double abilityAreaX = 0;
        double abilityAreaY = 0;
        double infoAreaX = 0;
        double infoAreaY = 0;
        double infoAreaWidth = 0;
        double infoAreaHeight = 0;

        double barAreaX = x + width * 0.03;
        double barAreaY = y + height * 0.12;
        if (selectedBar.getRenderData().isBarHorizontalOnScreen()) {
            barAreaWidth = width * 0.94;
            barAreaHeight = height * 0.25;
            abilityAreaX = x + width * 0.38;
            abilityAreaY = y + height * 0.4;
            abilityAreaWidth = width * 0.59;
            abilityAreaHeight = height * 0.56;
            infoAreaX = x + width * 0.03;
            infoAreaY = y + height * 0.4;
            infoAreaWidth = width * 0.33;
            infoAreaHeight = height * 0.56;
        } else {
            barAreaWidth = width * 0.21;
            barAreaHeight = height * 0.85;
            abilityAreaX = x + width * 0.25;
            abilityAreaY = y + height * 0.12;
            abilityAreaWidth = width * 0.72;
            abilityAreaHeight = height * 0.5;
            infoAreaX = x + width * 0.25;
            infoAreaY = y + height * 0.65;
            infoAreaWidth = width * 0.72;
            infoAreaHeight = height * 0.32;
        }

        drawBox(tessellator, barAreaX, barAreaY, barAreaWidth, barAreaHeight);
        drawBox(tessellator, abilityAreaX, abilityAreaY, abilityAreaWidth, abilityAreaHeight);
        drawBox(tessellator, infoAreaX, infoAreaY, infoAreaWidth, infoAreaHeight);

        //We need to calculate the correct scaling for the action bar.  We scale it to fit the bar area
        double pixelWidth = selectedBar.getRenderData().getPixelWidth();
        double pixelHeight = selectedBar.getRenderData().getPixelHeight();

        //Take into account the area taken up by the lock images.
        if (selectedBar.getRenderData().isBarHorizontalOnScreen())
            pixelHeight += 18;
        else
            pixelWidth += 18;

        //To make sure it gets scaled to fit the area, we take the correct scaling of the width & height, and then
        //use the smaller of the two
        double widthScale = (barAreaWidth - 8) / pixelWidth;
        double heightScale = (barAreaHeight - 8) / pixelHeight;
        double barScale = Math.min(widthScale, heightScale);

        //We want to leave some margin between the bottom & the top, so if we're using the width scaling, then
        //add some to the bar X, otherwise the bar Y
        double barXOffset = (widthScale == barScale) ? 3 : 0;
        double barYOffset = (heightScale == barScale) ? 3 : 0;
        double barX = barAreaX + (barAreaWidth / 2.0) - (barScale * pixelWidth * 0.5) + barXOffset;
        double barY = barAreaY + (barAreaHeight / 2.0) - (barScale * pixelHeight * 0.5) + barYOffset;

        //Draw the locks first so that they can be drawn before switching over to the bar's texture
        double lockX = barX+1;
        double lockY = barY+1;
        if (selectedBar.getRenderData().isBarHorizontalOnScreen()) {
            lockY += selectedBar.getRenderData().getPixelHeight() * barScale;
            lockX += selectedBar.getRenderData().barGraphicInsets().getX() * barScale;
        } else {
            lockX += selectedBar.getRenderData().getPixelWidth() * barScale;
            lockY += selectedBar.getRenderData().barGraphicInsets().getY() * barScale;
        }

        double spacing = selectedBar.getRenderData().getBarSpacing() + selectedBar.getRenderData().getActionWidth();
        for (int i = 0; i < selectedBar.getActionCount(); i++) {
            boolean isLocked = selectedBar.isLocked(i);
            drawImage(tessellator, lockX, lockY, 14*barScale, 14*barScale, 20, isLocked?0:16, 36, isLocked?16:32, 36, 32);

            if (selectedBar.getRenderData().isBarHorizontalOnScreen())
                lockX += spacing * barScale;
            else
                lockY += spacing * barScale;
        }

        tessellator.draw();
        mc.getTextureManager().bindTexture(selectedBar.getRenderData().getBarArt());
        tessellator.startDrawingQuads();

        //Draw the action bar
        drawImage(tessellator, barX, barY, selectedBar.getRenderData().getPixelWidth() * barScale, selectedBar.getRenderData().getPixelHeight() * barScale, 0, 0, selectedBar.getRenderData().getPixelWidth(), selectedBar.getRenderData().getPixelHeight(), 256, 256);

        tessellator.draw();
    }

    //Draw actions & text
    private void drawForeground(int mouseX, int mouseY) {
        if (selectedBar == null)
            drawEmptyMessage();
        else
            drawGui();
    }

    //Called if no valid action bar is registered
    private void drawEmptyMessage() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        String title = I18n.format("gui.barcraft.noBars", new Object[0]);
        double titleWidth = this.fontRendererObj.getStringWidth(title);
        double titleHeight = this.fontRendererObj.FONT_HEIGHT;
        GL11.glPushMatrix();
        GL11.glScaled(2, 2, 2);
        this.fontRendererObj.drawString(title, (int) ((sr.getScaledWidth_double() - titleWidth) / 8), (int) ((sr.getScaledHeight_double() - titleHeight) / 4), 4210752);
        GL11.glPopMatrix();
    }

    //Used to render actions & text if a valid bar is selected
    private void drawGui() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);

        //Draw the outside
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double();
        double x = width * 0.05;
        double y = height * 0.05;

        String title = I18n.format("gui.barcraft.actionBar", new Object[0]);
        double titleWidth = this.fontRendererObj.getStringWidth(title);
        GL11.glPushMatrix();
        GL11.glScaled(1.2, 1.2, 1.2);
        this.fontRendererObj.drawString(title, (int) ((sr.getScaledWidth_double() - titleWidth) / 2.55), (int) y + 6, 4210752);
        GL11.glPopMatrix();
    }

    //Render a gray-bordered box in the content of the GUI
    private void drawBox(Tessellator tessellator, double x, double y, double width, double height) {
        drawImage(tessellator, x, y, 4, 4, 0, 17, 4, 21, 36, 32);
        drawImage(tessellator, x + 4, y, width - 8, 4, 4, 17, 8, 21, 36, 32);
        drawImage(tessellator, x + width - 4, y, 4, 4, 8, 17, 12, 21, 36, 32);

        drawImage(tessellator, x, y + 4, 4, height - 8, 0, 21, 4, 24, 36, 32);
        drawImage(tessellator, x + 4, y + 4, width - 8, height - 8, 4, 21, 8, 24, 36, 32);
        drawImage(tessellator, x + width - 4, y + 4, 4, height - 8, 8, 21, 12, 24, 36, 32);

        drawImage(tessellator, x, y + height - 4, 4, 4, 0, 24, 4, 28, 36, 32);
        drawImage(tessellator, x + 4, y + height - 4, width - 8, 4, 4, 24, 8, 28, 36, 32);
        drawImage(tessellator, x + width - 4, y + height - 4, 4, 4, 8, 24, 12, 28, 36, 32);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawBackground(partialTicks, mouseX, mouseY);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        short short1 = 240;
        short short2 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) short1 / 1.0F, (float) short2 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int k1;

        GL11.glDisable(GL11.GL_LIGHTING);
        this.drawForeground(mouseX, mouseY);
        GL11.glEnable(GL11.GL_LIGHTING);
        InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;

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
