package net.technicpack.barcraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.technicpack.barcraft.api.IActionContainer;

public class BarcraftGuiStats {
    private IActionContainer actionBar;

    private double guiX, guiY, guiWidth, guiHeight;
    private double barAreaX, barAreaY, barAreaWidth, barAreaHeight;
    private double abilityAreaX, abilityAreaY, abilityAreaWidth, abilityAreaHeight;
    private double infoAreaX, infoAreaY, infoAreaWidth, infoAreaHeight;
    private double actionBarScale;
    private double actionBarX, actionBarY;
    private double actionLockX, actionLockY;
    private double actionBarSpacing;
    private int actionCount, actionCountX, actionCountY;
    private double actionAreaGutterWidth, actionAreaGutterHeight;
    private double infoAreaGutterWidth, infoAreaGutterHeight;
    private double scaledActionSize, scaledActionSlotSize, scaledActionLeftGutter, scaledActionTopGutter, scaledActionBottomGutter;
    private double scaledTextAreaWidth, scaledTextAreaHeight;
    private double guiScale;

    public BarcraftGuiStats(IActionContainer bar) {
        this.actionBar = bar;
        initStats();
    }

    public double getGuiX() { return guiX; }
    public double getGuiY() { return guiY; }
    public double getGuiWidth() { return guiWidth; }
    public double getGuiHeight() { return guiHeight; }
    public double getBarAreaX() { return barAreaX; }
    public double getBarAreaY() { return barAreaY; }
    public double getBarAreaWidth() { return barAreaWidth; }
    public double getBarAreaHeight() { return barAreaHeight; }
    public double getActionBarScale() { return actionBarScale; }
    public double getActionBarX() { return actionBarX; }
    public double getActionBarY() { return actionBarY; }
    public double getActionLockX() { return actionLockX; }
    public double getActionLockY() { return actionLockY; }
    public double getActionBarSpacing() { return actionBarSpacing; }
    public double getAbilityAreaX() { return abilityAreaX; }
    public double getAbilityAreaY() { return abilityAreaY; }
    public double getAbilityAreaWidth() { return abilityAreaWidth; }
    public double getAbilityAreaHeight() { return abilityAreaHeight; }
    public double getInfoAreaX() { return infoAreaX; }
    public double getInfoAreaY() { return infoAreaY; }
    public double getInfoAreaWidth() { return infoAreaWidth; }
    public double getInfoAreaHeight() { return infoAreaHeight; }
    public int getActionCount() { return actionCount; }
    public int getActionCountX() { return actionCountX; }
    public int getActionCountY() { return actionCountY;}
    public double getActionAreaGutterWidth() { return actionAreaGutterWidth; }
    public double getActionAreaGutterHeight() { return actionAreaGutterHeight; }
    public double getInfoAreaGutterWidth() { return infoAreaGutterWidth; }
    public double getInfoAreaGutterHeight() { return infoAreaGutterHeight; }
    public double getScaledActionSize() { return scaledActionSize; }
    public double getScaledActionSlotSize() { return scaledActionSlotSize; }
    public double getScaledActionLeftGutter() { return scaledActionLeftGutter; }
    public double getScaledActionTopGutter() { return scaledActionTopGutter; }
    public double getScaledActionBottomGutter() { return scaledActionBottomGutter; }
    public double getScaledTextAreaWidth() { return scaledTextAreaWidth; }
    public double getScaledTextAreaHeight() { return scaledTextAreaHeight; }
    public double getGuiScale() { return guiScale; }

    private void initStats() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);

        //Draw the outside
        guiWidth = sr.getScaledWidth_double();
        guiHeight = sr.getScaledHeight_double();
        guiX = guiWidth * 0.05;
        guiY = guiHeight * 0.05;
        guiWidth *= 0.9;
        guiHeight *= 0.9;
        guiScale = sr.getScaleFactor();

        barAreaX = guiX + guiWidth * 0.03;
        barAreaY = guiY + guiHeight * 0.12;
        if (actionBar.getRenderData().isBarHorizontalOnScreen()) {
            barAreaWidth = guiWidth * 0.94;
            barAreaHeight = guiHeight * 0.25;
            abilityAreaX = guiX + guiWidth * 0.38;
            abilityAreaY = guiY + guiHeight * 0.4;
            abilityAreaWidth = guiWidth * 0.59;
            abilityAreaHeight = guiHeight * 0.56;
            infoAreaX = guiX + guiWidth * 0.03;
            infoAreaY = guiY + guiHeight * 0.4;
            infoAreaWidth = guiWidth * 0.33;
            infoAreaHeight = guiHeight * 0.56;
        } else {
            barAreaWidth = guiWidth * 0.21;
            barAreaHeight = guiHeight * 0.85;
            abilityAreaX = guiX + guiWidth * 0.25;
            abilityAreaY = guiY + guiHeight * 0.12;
            abilityAreaWidth = guiWidth * 0.72;
            abilityAreaHeight = guiHeight * 0.5;
            infoAreaX = guiX + guiWidth * 0.25;
            infoAreaY = guiY + guiHeight * 0.65;
            infoAreaWidth = guiWidth * 0.72;
            infoAreaHeight = guiHeight * 0.32;
        }

        //Check for lock clicks
        //We need to calculate the correct scaling for the action bar.  We scale it to fit the bar area
        double pixelWidth = actionBar.getRenderData().getPixelWidth();
        double pixelHeight = actionBar.getRenderData().getPixelHeight();

        //Take into account the area taken up by the lock images.
        if (actionBar.getRenderData().isBarHorizontalOnScreen())
            pixelHeight += 18;
        else
            pixelWidth += 18;

        //To make sure it gets scaled to fit the area, we take the correct scaling of the width & height, and then
        //use the smaller of the two
        double widthScale = (barAreaWidth - 8) / pixelWidth;
        double heightScale = (barAreaHeight - 8) / pixelHeight;
        actionBarScale = Math.min(widthScale, heightScale);

        //We want to leave some margin between the bottom & the top, so if we're using the width scaling, then
        //add some to the bar X, otherwise the bar Y
        double barXOffset = (widthScale == actionBarScale) ? 3 : 0;
        double barYOffset = (heightScale == actionBarScale) ? 3 : 0;
        actionBarX = barAreaX + (barAreaWidth / 2.0) - (actionBarScale * pixelWidth * 0.5) + barXOffset;
        actionBarY = barAreaY + (barAreaHeight / 2.0) - (actionBarScale * pixelHeight * 0.5) + barYOffset;

        //Draw the locks first so that they can be drawn before switching over to the bar's texture
        actionLockX = actionBarX+1;
        actionLockY = actionBarY+1;
        if (actionBar.getRenderData().isBarHorizontalOnScreen()) {
            actionLockY += actionBar.getRenderData().getPixelHeight() * actionBarScale;
            actionLockX += actionBar.getRenderData().barGraphicInsets().getX() * actionBarScale;
        } else {
            actionLockX += actionBar.getRenderData().getPixelWidth() * actionBarScale;
            actionLockY += actionBar.getRenderData().barGraphicInsets().getY() * actionBarScale;
        }

        double lockSize = 14 * actionBarScale;
        actionBarSpacing = (actionBar.getRenderData().getBarSpacing() + actionBar.getRenderData().getActionWidth()) * actionBarScale;

        //We need to see how many abilities we can fit in here
        scaledActionSize = 64 / (double)sr.getScaleFactor();
        scaledActionSlotSize = 96 / (double)sr.getScaleFactor();
        scaledActionLeftGutter = 16 / (double)sr.getScaleFactor();
        scaledActionTopGutter = 5 / (double)sr.getScaleFactor();
        scaledActionBottomGutter = 27 / (double)sr.getScaleFactor();
        scaledTextAreaHeight = 22 / (double)sr.getScaleFactor();
        scaledTextAreaWidth = 86 / (double)sr.getScaleFactor();

        double minimumGutter = 3 / (double)sr.getScaleFactor();

        actionCountX = (int)((abilityAreaWidth - minimumGutter*2) / scaledActionSlotSize);
        actionCountY = (int)((abilityAreaHeight - minimumGutter*2) / scaledActionSlotSize);
        actionCount = actionCountX * actionCountY;

        actionAreaGutterWidth = (abilityAreaWidth - (actionCountX * scaledActionSlotSize))/2;
        actionAreaGutterHeight = (abilityAreaHeight - (actionCountY * scaledActionSlotSize))/2;
        infoAreaGutterWidth = 12 / (double)sr.getScaleFactor();
        infoAreaGutterHeight = 12 / (double)sr.getScaleFactor();
    }
}
