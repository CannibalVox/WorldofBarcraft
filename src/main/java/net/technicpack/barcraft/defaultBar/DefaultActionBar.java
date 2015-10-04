package net.technicpack.barcraft.defaultBar;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.technicpack.barcraft.api.ActionClientState;
import net.technicpack.barcraft.api.IAction;
import net.technicpack.barcraft.api.IActionContainer;
import net.technicpack.barcraft.api.IOnScreenBar;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;

import javax.vecmath.Vector3d;

public class DefaultActionBar implements IActionContainer, IOnScreenBar {

    private IAction[] actions = new IAction[6];
    private ActionClientState[] states = new ActionClientState[6];
    private boolean[] locks = new boolean[6];

    public DefaultActionBar() {

    }

    @Override
    public String getKey() {
        return "barcraft:defaultBar";
    }

    @Override
    public String getDisplayName() {
        return "barcraft.gui.bar.defaultBar";
    }

    @Override
    public IOnScreenBar getRenderData() {
        return this;
    }

    @Override
    public KeyBinding getKeybindingForAction(int actionIndex) {
        String keyName = "barcraft.action"+Integer.toString(actionIndex+1);
        return new KeyBinding(keyName, Keyboard.KEY_1+actionIndex, "barcraft");
    }

    @Override
    public boolean canAddAction(IAction action) {
        return true;
    }

    @Override
    public int getActionCount() {
        return 6;
    }

    @Override
    public IAction getAction(int index) {
        return actions[index];
    }

    @Override
    public void setAction(int index, IAction action) {
        actions[index] = action;
    }

    @Override
    public ActionClientState getClientState(int index) {
        return states[index];
    }

    @Override
    public boolean isLocked(int index) {
        return locks[index];
    }

    @Override
    public boolean appearsOnScreen() {
        return true;
    }

    @Override
    public boolean allowsOnScreenClick() {
        return true;
    }

    @Override
    public boolean appearsInMenu() {
        return true;
    }

    @Override
    public boolean drawKeyboardData() {
        return true;
    }

    @Override
    public Vector3d barScreenPosition(ScaledResolution scaledResolution) {
        return new Vector3d(scaledResolution.getScaledWidth() / 2 - 210, 12, -90);
    }

    @Override
    public int getPixelWidth() {
        return 122;
    }

    @Override
    public int getPixelHeight() {
        return 22;
    }

    @Override
    public boolean shouldBlockMouseClick(int x, int y) {
        return false;
    }

    private Rectangle insets = new Rectangle(3, 3, 3, 3);
    @Override
    public Rectangle barGraphicInsets() {
        return insets;
    }

    @Override
    public boolean isBarHorizontalOnScreen() {
        return true;
    }

    @Override
    public int getActionWidth() {
        return 16;
    }

    @Override
    public int getBarSpacing() {
        return 4;
    }

    @Override
    public double getOnScreenScale() {
        return 0.9;
    }

    @Override
    public double getInMenuScale() {
        return 1;
    }

    @Override
    public ResourceLocation getBarArt() {
        return new ResourceLocation("barcraft", "textures/gui/actionBar.png");
    }
}
