package net.technicpack.barcraft.api;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Rectangle;

import javax.vecmath.Vector3d;

public interface IOnScreenBar {
    ///Whether World of Barcraft should draw this action bar for you.  If you want to draw it yourself
    ///or don't want it to appear in the HUD, this should be false.
    boolean appearsOnScreen();
    ///Whether players can click the action bar buttons manually in the HUD.  You can use this to allow
    ///World of Barcraft to handle mouse clicks for you even if you want to draw it yourself.
    boolean allowsOnScreenClick();
    ///Even if you don't want your bar to appear in the HUD, if you want players to manipulate the bar from
    ///the action menu, this should be true.
    boolean appearsInMenu();

    //If all of the above 3 are false, none of the below matter.

    ///Whether to draw the keyboard binding key on the actions.  Only works if Barcraft is handling the keybindings for
    ///you.
    boolean drawKeyboardData();

    ///Where on the screen this bar is for purposes of drawing & collecting clicks
    Vector3d barScreenPosition(ScaledResolution scaledResolution);
    ///How big in pixels the bar is
    int getPixelWidth();
    int getPixelHeight();

    ///If the user clicks in this bar's rectangle, but not on an action, the mouse position will be passed to this method.
    ///If it returns true, the mouse click will be blocked from passing through to the rest of the UI
    boolean shouldBlockMouseClick(int x, int y);

    ///The number of pixels in the bar graphic between the top/left/right/bottom and the nearest edge of the nearest action
    Rectangle barGraphicInsets();
    ///True if a horizontal bar, false if vertical
    boolean isBarHorizontalOnScreen();
    //How wide action buttons are in the action bar, in pixels
    int getActionWidth();
    //How many pixels are between action buttons in the bar
    int getBarSpacing();

    //The scale of the action bar when drawn on screen- the above values are all multiplied by this one when drawn.
    double getOnScreenScale();
    //The scale of the action bar when drawn in the config menu- the above values are all multiplied by this one when drawn
    double getInMenuScale();

    ResourceLocation getBarArt();
}
