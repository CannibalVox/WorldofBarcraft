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
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.technicpack.barcraft.WorldOfBarcraft;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IActionContainerRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiBarcraftConfig extends GuiScreen {
    private static final int LEADING = 0;
    private static final int CENTER = 1;
    private static final int TAILING = 2;

    private static final ResourceLocation backgroundTex = new ResourceLocation("barcraft", "textures/gui/actionConfigGui.png");

    private IActionContainerRegistry containerRegistry = null;
    private IActionContainer selectedBar = null;
    private BarcraftGuiStats barcraftGuiStats = null;
    private boolean hasMultipleBars = false;
    private int abilityIndexForPage = 0;
    private int selectedAbility = -1;
    private List<IAction> actionsForBar = new ArrayList<IAction>();
    private GuiButton actionsPageLeft = null;
    private GuiButton actionsPageRight = null;

    public GuiBarcraftConfig() {
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
            IActionContainer bar = getNextValidBar(container);
            if (getNextValidBar(container) != null)
                hasMultipleBars = true;
            selectedBar = bar;
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        super.initGui();
        actionsPageLeft = null;
        actionsPageRight = null;
        setSelectedBar(selectedBar);

        //If there's multiple bars, we need to set up the buttons.  They should be spaced out so the name of all
        //action bars can fit, so we'll get action bar with the widest name
        if (hasMultipleBars) {
            int biggestTitleWidth = 0;
            Iterator<IActionContainer> iterator = containerRegistry.getActionBars().iterator();
            IActionContainer nextContainer = getNextValidBar(iterator);
            while (nextContainer != null) {
                String title = nextContainer.getDisplayName();
                int titleWidth = this.fontRendererObj.getStringWidth(title);
                if (titleWidth > biggestTitleWidth)
                    biggestTitleWidth = titleWidth;
                nextContainer = getNextValidBar(iterator);
            }

            //Get offset from center
            double centerOffset = ((biggestTitleWidth + 12) / barcraftGuiStats.getGuiScale());
            //Draw the outside
            double y = barcraftGuiStats.getGuiY() + barcraftGuiStats.getGuiHeight() * 0.07;
            buttonList.add(new GuiButton(0, (int)(barcraftGuiStats.getGuiX() + barcraftGuiStats.getGuiWidth()/2 - centerOffset - (16)), (int)(y-10), 16, 20, "<"));
            buttonList.add(new GuiButton(1, (int)(barcraftGuiStats.getGuiX() + barcraftGuiStats.getGuiWidth()/2 + centerOffset), (int) (y-10), 16, 20, ">"));
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
        } else if (button.id == 2) {
            //Select action shee: left
            int realIndex = (abilityIndexForPage / barcraftGuiStats.getActionCount()) * barcraftGuiStats.getActionCount();
            setActionPage(realIndex - barcraftGuiStats.getActionCount());
        } else if (button.id == 3) {
            //Selection action sheet: right
            int realIndex = (abilityIndexForPage / barcraftGuiStats.getActionCount()) * barcraftGuiStats.getActionCount();
            setActionPage(realIndex + barcraftGuiStats.getActionCount());
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (selectedBar == null)
            return;

        double lockX = barcraftGuiStats.getActionLockX();
        double lockY = barcraftGuiStats.getActionLockY();
        for (int i = 0; i < selectedBar.getActionCount(); i++) {
            if (mouseX >= lockX && mouseY >= lockY && mouseX < lockX + barcraftGuiStats.getActionBarSpacing() && mouseY < lockY + barcraftGuiStats.getActionBarSpacing()) {
                selectedBar.setLocked(i, !selectedBar.isLocked(i));

                if (!selectedBar.isLocked(i) && selectedBar.getAction(i) != null && !WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, selectedBar.getAction(i)))
                    selectedBar.setAction(i, null);
                return;
            }
            if (selectedBar.getRenderData().isBarHorizontalOnScreen())
                lockX += barcraftGuiStats.getActionBarSpacing();
            else
                lockY += barcraftGuiStats.getActionBarSpacing();
        }

        for (int i = 0; i < barcraftGuiStats.getActionCount(); i++) {
            if ((i+abilityIndexForPage) >= actionsForBar.size())
                break;

            int actionColumn = i % barcraftGuiStats.getActionCountX();
            int actionRow = i / barcraftGuiStats.getActionCountX();
            int x = (int)(barcraftGuiStats.getAbilityAreaX() + barcraftGuiStats.getActionAreaGutterWidth() + (barcraftGuiStats.getScaledActionSlotSize() * actionColumn));
            int y = (int)(barcraftGuiStats.getAbilityAreaY() + barcraftGuiStats.getActionAreaGutterHeight() + (barcraftGuiStats.getScaledActionSlotSize() * actionRow));
            if (mouseX >= x && mouseY >= y && mouseX < x+barcraftGuiStats.getScaledActionSlotSize() && mouseY < y+barcraftGuiStats.getScaledActionSlotSize()) {
                selectedAbility = i+abilityIndexForPage;
                return;
            }
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
        if (this.selectedBar != bar)
            this.selectedAbility = -1;
        this.selectedBar = bar;
        this.actionsForBar.clear();

        if (actionsPageLeft != null)
            this.buttonList.remove(actionsPageLeft);
        if (actionsPageRight != null)
            this.buttonList.remove(actionsPageRight);

        if (this.selectedBar != null) {
            this.barcraftGuiStats = new BarcraftGuiStats(this.selectedBar);
            for (IAction action : WorldOfBarcraft.proxy.getApi().getActionRegistry().getActions()) {
                if (action != null && selectedBar.canAddAction(action) && action.canJoinBar(selectedBar) && WorldOfBarcraft.proxy.getApi().playerHasAction(Minecraft.getMinecraft().thePlayer, action))
                    actionsForBar.add(action);
            }

            int leftButtonX = (int)barcraftGuiStats.getAbilityAreaX() + 6;
            int buttonY = (int)(barcraftGuiStats.getAbilityAreaY() + (barcraftGuiStats.getAbilityAreaHeight()/2) - 10);
            this.actionsPageLeft = new GuiButton(2, leftButtonX, buttonY, 16, 20, "<");
            this.buttonList.add(this.actionsPageLeft);

            int rightButtonX = (int)(barcraftGuiStats.getAbilityAreaX() + barcraftGuiStats.getAbilityAreaWidth() - (22 / barcraftGuiStats.getGuiScale()));
            this.actionsPageRight = new GuiButton(3, rightButtonX, buttonY, 16, 20, ">");
            this.buttonList.add(this.actionsPageRight);
            setActionPage(abilityIndexForPage);
        }
    }

    private void setActionPage(int abilityIndex) {
        this.actionsPageLeft.visible = true;
        this.actionsPageRight.visible = true;

        this.abilityIndexForPage = abilityIndex;
        if (this.abilityIndexForPage < 0)
            this.abilityIndexForPage = 0;
        if (this.abilityIndexForPage >= this.actionsForBar.size())
            this.abilityIndexForPage = this.actionsForBar.size() - 1;

        if (this.abilityIndexForPage < barcraftGuiStats.getActionCount())
            this.actionsPageLeft.visible = false;
        if (this.abilityIndexForPage >= this.actionsForBar.size() - barcraftGuiStats.getActionCount())
            this.actionsPageRight.visible = false;
    }

    //Draws the background of the UI:
    // - The background
    // - the area boxes (action bar, action list, info)
    // - the action bar graphic & lock icons
    private void drawBackground(float partialTicks, int mouseX, int mouseY) {
        //Start drawing background
        mc.getTextureManager().bindTexture(backgroundTex);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        //Draw the outside
        drawImage(tessellator, barcraftGuiStats.getGuiX(), barcraftGuiStats.getGuiY(), 5, 5, 0, 0, 5, 5, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + 5, barcraftGuiStats.getGuiY(), barcraftGuiStats.getGuiWidth() - 9, 5, 5, 0, 15, 5, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + (barcraftGuiStats.getGuiWidth() - 4), barcraftGuiStats.getGuiY(), 4, 5, 15, 0, 20, 5, 36, 32);

        drawImage(tessellator, barcraftGuiStats.getGuiX(), barcraftGuiStats.getGuiY() + 5, 5, barcraftGuiStats.getGuiHeight() - 10, 0, 5, 5, 12, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + 5, barcraftGuiStats.getGuiY() + 5, barcraftGuiStats.getGuiWidth() - 9, barcraftGuiStats.getGuiHeight() - 10, 5, 5, 15, 12, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + (barcraftGuiStats.getGuiWidth() - 4), barcraftGuiStats.getGuiY() + 5, 4, barcraftGuiStats.getGuiHeight() - 10, 15, 5, 20, 12, 36, 32);

        drawImage(tessellator, barcraftGuiStats.getGuiX(), barcraftGuiStats.getGuiY() + (barcraftGuiStats.getGuiHeight() - 5), 5, 5, 0, 12, 5, 17, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + 5, barcraftGuiStats.getGuiY() + (barcraftGuiStats.getGuiHeight() - 5), barcraftGuiStats.getGuiWidth() - 9, 5, 5, 12, 15, 17, 36, 32);
        drawImage(tessellator, barcraftGuiStats.getGuiX() + (barcraftGuiStats.getGuiWidth() - 4), barcraftGuiStats.getGuiY() + (barcraftGuiStats.getGuiHeight() - 5), 4, 5, 15, 12, 20, 17, 36, 32);

        //Only draw the background & some crappy text if we somehow got here without a valid action bar
        if (selectedBar == null)
            return;

        drawBox(tessellator, barcraftGuiStats.getBarAreaX(), barcraftGuiStats.getBarAreaY(), barcraftGuiStats.getBarAreaWidth(), barcraftGuiStats.getBarAreaHeight());
        drawBox(tessellator, barcraftGuiStats.getAbilityAreaX(), barcraftGuiStats.getAbilityAreaY(), barcraftGuiStats.getAbilityAreaWidth(), barcraftGuiStats.getAbilityAreaHeight());
        drawBox(tessellator, barcraftGuiStats.getInfoAreaX(), barcraftGuiStats.getInfoAreaY(), barcraftGuiStats.getInfoAreaWidth(), barcraftGuiStats.getInfoAreaHeight());

        double spacing = selectedBar.getRenderData().getBarSpacing() + selectedBar.getRenderData().getActionWidth();
        double lockX = barcraftGuiStats.getActionLockX();
        double lockY = barcraftGuiStats.getActionLockY();
        for (int i = 0; i < selectedBar.getActionCount(); i++) {
            boolean isLocked = selectedBar.isLocked(i);
            drawImage(tessellator, lockX, lockY, 14 * barcraftGuiStats.getActionBarScale(), 14 * barcraftGuiStats.getActionBarScale(), 20, isLocked ? 0 : 16, 36, isLocked ? 16 : 32, 36, 32);

            if (selectedBar.getRenderData().isBarHorizontalOnScreen())
                lockX += spacing * barcraftGuiStats.getActionBarScale();
            else
                lockY += spacing * barcraftGuiStats.getActionBarScale();
        }

        //Draw a box around the selected action
        if (actionsForBar.size() > 0 && selectedAbility >= 0) {
            int i = selectedAbility - abilityIndexForPage;
            if (i >= 0 && i < barcraftGuiStats.getActionCount()) {
                int selectedColumn = i % barcraftGuiStats.getActionCountX();
                int selectedRow = i / barcraftGuiStats.getActionCountX();
                double thickness = 2 / barcraftGuiStats.getGuiScale();

                double actionX = barcraftGuiStats.getAbilityAreaX() + barcraftGuiStats.getActionAreaGutterWidth() + barcraftGuiStats.getScaledActionSlotSize() * selectedColumn;
                double actionY = barcraftGuiStats.getAbilityAreaY() + barcraftGuiStats.getActionAreaGutterHeight() + barcraftGuiStats.getScaledActionSlotSize() * selectedRow;

                drawImage(tessellator, actionX, actionY, barcraftGuiStats.getScaledActionSlotSize(), thickness, 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX, actionY + thickness, thickness, barcraftGuiStats.getScaledActionSlotSize() - (2 * thickness), 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX, actionY + barcraftGuiStats.getScaledActionSlotSize() - thickness, barcraftGuiStats.getScaledActionSlotSize(), thickness, 0, 31, 1, 32, 36, 32);
                drawImage(tessellator, actionX + barcraftGuiStats.getScaledActionSlotSize() - thickness, actionY + thickness, thickness, barcraftGuiStats.getScaledActionSlotSize() - (2 * thickness), 0, 31, 1, 32, 36, 32);
            }

            double descriptionSplitX = barcraftGuiStats.getInfoAreaX() + 3;
            double descriptionSplitY = barcraftGuiStats.getInfoAreaY() + (barcraftGuiStats.getInfoAreaGutterHeight() * 1.5) + (barcraftGuiStats.getScaledActionSize() * 1.2);
            drawImage(tessellator, descriptionSplitX, descriptionSplitY, barcraftGuiStats.getInfoAreaWidth() - 6, 1, 0, 31, 1, 32, 36, 32);
        }

        tessellator.draw();
        mc.getTextureManager().bindTexture(selectedBar.getRenderData().getBarArt());
        tessellator.startDrawingQuads();

        //Draw the action bar
        drawImage(tessellator, barcraftGuiStats.getActionBarX(), barcraftGuiStats.getActionBarY(), selectedBar.getRenderData().getPixelWidth() * barcraftGuiStats.getActionBarScale(), selectedBar.getRenderData().getPixelHeight() * barcraftGuiStats.getActionBarScale(), 0, 0, selectedBar.getRenderData().getPixelWidth(), selectedBar.getRenderData().getPixelHeight(), 256, 256);

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
        //
        String title = selectedBar.getDisplayName();
        double titleWidth = this.fontRendererObj.getStringWidth(title);
        GL11.glPushMatrix();
        GL11.glTranslated(barcraftGuiStats.getGuiX() + (barcraftGuiStats.getGuiWidth()/2), barcraftGuiStats.getGuiY() + (barcraftGuiStats.getGuiHeight() * 0.07), 0);
        double titleScale = 3/barcraftGuiStats.getGuiScale();
        GL11.glScaled(titleScale, titleScale, titleScale);
        this.fontRendererObj.drawString(title,1-fontRendererObj.getStringWidth(title)/2, 1-fontRendererObj.FONT_HEIGHT/2, 4210752);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);

        int abilityIndex = (abilityIndexForPage / barcraftGuiStats.getActionCount()) * barcraftGuiStats.getActionCount();
        for (int i = 0; i < barcraftGuiStats.getActionCount(); i++) {
            if (i + abilityIndex >= actionsForBar.size())
                break;
            int abilityRow = i / barcraftGuiStats.getActionCountX();
            int abilityColumn = i % barcraftGuiStats.getActionCountX();
            IAction action = actionsForBar.get(i+abilityIndexForPage);
            String abilityName = action.getDisplayName();
            double abilityTextX = barcraftGuiStats.getAbilityAreaX() + abilityColumn * barcraftGuiStats.getScaledActionSlotSize() + barcraftGuiStats.getActionAreaGutterWidth() + (barcraftGuiStats.getScaledActionSlotSize() - barcraftGuiStats.getScaledTextAreaWidth()) / 2;
            double abilityTextY = barcraftGuiStats.getAbilityAreaY() + abilityRow * barcraftGuiStats.getScaledActionSlotSize() + barcraftGuiStats.getScaledActionTopGutter() + barcraftGuiStats.getActionAreaGutterHeight() + barcraftGuiStats.getScaledActionSlotSize() - barcraftGuiStats.getScaledActionBottomGutter();
            drawScaledText(abilityTextX, abilityTextY, barcraftGuiStats.getScaledTextAreaWidth(), barcraftGuiStats.getScaledTextAreaHeight(), abilityName);
        }

        if (selectedAbility >= 0 && selectedAbility < actionsForBar.size()) {
            IAction action = actionsForBar.get(selectedAbility);

            double textx = barcraftGuiStats.getInfoAreaX() + (barcraftGuiStats.getInfoAreaGutterWidth() * 2) + barcraftGuiStats.getScaledActionSize();
            double texty = barcraftGuiStats.getInfoAreaY() + barcraftGuiStats.getInfoAreaGutterHeight();
            double textWidth = barcraftGuiStats.getInfoAreaWidth() - (barcraftGuiStats.getInfoAreaGutterWidth() * 3) - barcraftGuiStats.getScaledActionSize();
            double textHeight = barcraftGuiStats.getScaledActionSize() * 0.7;
            drawScaledText(textx, texty, textWidth, textHeight, action.getDisplayName());

            double descx = barcraftGuiStats.getInfoAreaX() + barcraftGuiStats.getInfoAreaGutterWidth();
            double descy = barcraftGuiStats.getInfoAreaY() + (barcraftGuiStats.getInfoAreaGutterHeight() * 2) + (barcraftGuiStats.getScaledActionSize() * 1.2);
            double descWidth = barcraftGuiStats.getInfoAreaWidth() - (barcraftGuiStats.getInfoAreaGutterWidth() * 2);
            double descHeight = barcraftGuiStats.getInfoAreaHeight() - (barcraftGuiStats.getInfoAreaGutterHeight() * 3) - (barcraftGuiStats.getScaledActionSize() * 1.2);
            drawScaledText(descx, descy, descWidth, descHeight, action.getDescription(), LEADING, LEADING);
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        mc.getTextureManager().bindTexture(new ResourceLocation("textures/atlas/abilities.png"));
        double actionX = barcraftGuiStats.getActionBarX() + (double) selectedBar.getRenderData().barGraphicInsets().getX() * barcraftGuiStats.getActionBarScale();
        double actionY = barcraftGuiStats.getActionBarY() + (double) selectedBar.getRenderData().barGraphicInsets().getY() * barcraftGuiStats.getActionBarScale();
        double actionSize = (double) selectedBar.getRenderData().getActionWidth() * barcraftGuiStats.getActionBarScale();
        for (int i = 0; i < selectedBar.getActionCount(); i++) {
            IAction action = selectedBar.getAction(i);

            if (action == null)
                continue;

            if (!WorldOfBarcraft.proxy.getApi().playerHasAction(mc.thePlayer, action))
                tessellator.setColorOpaque(50, 50, 50);
            else
                tessellator.setColorOpaque(255, 255, 255);

            IIcon icon = action.getIcon();
            drawImage(tessellator, actionX, actionY, actionSize, actionSize, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
            actionX += barcraftGuiStats.getActionBarSpacing();
        }

        for (int i = 0; i < barcraftGuiStats.getActionCount(); i++) {
            if (i + abilityIndex >= actionsForBar.size())
                break;
            int abilityRow = i / barcraftGuiStats.getActionCountX();
            int abilityColumn = i % barcraftGuiStats.getActionCountX();
            IAction action = actionsForBar.get(i+abilityIndexForPage);
            IIcon icon = action.getIcon();
            double abilityIconX = barcraftGuiStats.getAbilityAreaX() + abilityColumn * barcraftGuiStats.getScaledActionSlotSize() + barcraftGuiStats.getActionAreaGutterWidth() + barcraftGuiStats.getScaledActionLeftGutter();
            double abilityIconY = barcraftGuiStats.getAbilityAreaY() + abilityRow * barcraftGuiStats.getScaledActionSlotSize() + barcraftGuiStats.getActionAreaGutterHeight() + barcraftGuiStats.getScaledActionTopGutter();
            drawImage(tessellator, abilityIconX, abilityIconY, barcraftGuiStats.getScaledActionSize(), barcraftGuiStats.getScaledActionSize(), icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
        }

        if (selectedAbility >= 0 && selectedAbility < actionsForBar.size()) {
            IAction action = actionsForBar.get(selectedAbility);
            IIcon icon = action.getIcon();

            double x = barcraftGuiStats.getInfoAreaX() + barcraftGuiStats.getInfoAreaGutterWidth();
            double y = barcraftGuiStats.getInfoAreaY() + barcraftGuiStats.getInfoAreaGutterHeight();
            drawImage(tessellator, x, y, barcraftGuiStats.getScaledActionSize(), barcraftGuiStats.getScaledActionSize(), icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 1, 1);
        }
        tessellator.draw();
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
        this.zLevel = 0;
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
        this.zLevel = -100;
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

    private void drawScaledText(double x, double y, double width, double height, String text) {
        drawScaledText(x, y, width, height, text, CENTER, CENTER);
    }

    private void drawScaledText(double x, double y, double width, double height, String text, int hAlignment, int vAlignment) {
        String[] tokens = text.split("((?<=\\s)|(?=\\s))");

        int largestToken = 0;
        for (String token : tokens) {
            int tokenSize = fontRendererObj.getStringWidth(token);
            if (tokenSize > largestToken)
                largestToken = tokenSize;
        }

        double textWidth = fontRendererObj.getStringWidth(text);
        double minScale = Math.min(width / textWidth, height / (double)fontRendererObj.FONT_HEIGHT);

        int maxLines = (int) (height / ((double) fontRendererObj.FONT_HEIGHT * minScale));

        Object[] bestText = new String[]{text};
        double bestScale = minScale;
        int bestLines = 1;

        if (maxLines > 1) {
            for (int lineCount = 2; lineCount <= maxLines; lineCount++) {
                List<String> textLines = new ArrayList<String>(lineCount);
                double maxSingleWidth = 0;
                double targetWidth = Math.max(largestToken, textWidth / lineCount);
                int tokenIndex = 0;
                for (int i = 0; i < lineCount; i++) {
                    double currentWidth = 0;
                    String lineText = "";
                    boolean beginningOfLine = true;

                    while (tokenIndex < tokens.length && currentWidth < targetWidth) {
                        String thisToken = tokens[tokenIndex++];
                        if (beginningOfLine && thisToken.trim().equals(""))
                            continue;
                        beginningOfLine = false;

                        int tokenWidth = fontRendererObj.getStringWidth(thisToken);
                        if (thisToken.trim().equals("") && currentWidth + tokenWidth >= targetWidth) {
                            currentWidth += tokenWidth;
                            continue;
                        }

                        lineText = lineText.concat(thisToken);
                        currentWidth += tokenWidth;
                    }

                    if (maxSingleWidth < currentWidth)
                        maxSingleWidth = currentWidth;
                    if (lineText != null && !lineText.trim().equals(""))
                        textLines.add(lineText);
                }
                double horizontalScale = width / maxSingleWidth;
                double verticalScale = height / (textLines.size() * fontRendererObj.FONT_HEIGHT);
                double currentScale = Math.min(horizontalScale, verticalScale);
                if (currentScale > bestScale) {
                    bestScale = currentScale;
                    bestLines = textLines.size();
                    bestText = textLines.toArray();
                }
            }
        }

        double totalTextHeight = bestLines * fontRendererObj.FONT_HEIGHT * bestScale;

        GL11.glPushMatrix();
        double xTranslate = x;
        double yTranslate = y;
        if (hAlignment == CENTER)
            xTranslate += width / 2;
        else if (hAlignment == TAILING)
            xTranslate += width;
        if (vAlignment == CENTER)
            yTranslate += (height - totalTextHeight) / 2;
        else if (vAlignment == TAILING)
            yTranslate += (height - totalTextHeight);
        GL11.glTranslated(xTranslate, yTranslate, 0);
        GL11.glScaled(bestScale, bestScale, bestScale);
        for (int i = 0; i < bestLines; i++) {
            int lineX = 0;
            if (hAlignment == CENTER)
                lineX = (int) -(double) fontRendererObj.getStringWidth(bestText[i].toString()) / 2;
            else if (hAlignment == TAILING)
                lineX = (int) - (double) fontRendererObj.getStringWidth(bestText[i].toString());

            this.fontRendererObj.drawString(bestText[i].toString(), lineX, i * fontRendererObj.FONT_HEIGHT, 4210752);
        }
        GL11.glPopMatrix();
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        super.setWorldAndResolution(minecraft, width, height);
        setSelectedBar(selectedBar);
    }
}
