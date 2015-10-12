package net.technicpack.barcraft.api;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

public interface IAction {
    String getKey();
    ///This is the unlocalized string used as the ability name
    String getDisplayName();
    ///Called during the texture stitching stage to build a separate ability icon atlas.  Abilities should
    ///use this to turn their art into IIcons
    void registerIcons(IIconRegister register);
    ///Called during rendering to get the icon for this ability.  The API, Minecraft.getMinecraft().thePlayer etc.
    ///can be used to determine ability state.
    IIcon getIcon();
    ///Whether this ability is allowed to be added to the given ability container.  The ability container also gets
    ///a choice in the matter, separately, so only bother yourself with whether this ability only wants to be added
    ///to certain bars.
    boolean canJoinBar(IActionContainer container);

    ///Whether the given player can currently use this.  If client returns false, the attempt activation is never
    ///passed to the server.
    boolean canTrigger(EntityPlayer source);
    ///Called on both client & server when an ability is permitted to be used.  Check client vs. server by checking
    ///the player's world remote status.
    void trigger(EntityPlayer source);

    ///If this is a conditional ability, that means the player's ownership of the ability is based on whether some
    ///condition is met, like whether a player is wearing a set of armor or perhaps in a certain place.  If not,
    ///the player's access is manually imbued to them by leveling up or consuming a scroll or something.  Non-conditional
    ///ability access is stored on the hard drive and retrieved when the player logs in.  It can be given or taken
    ///away manually through the API.  Conditional ability access is calculated for all conditional abilities on login,
    ///and then access for individual abilities is recalculated by trigger an event through the API.
    String getConditionKey();
    ///If this ability has a condition key, should return true if player should have access, false otherwise.
    ///If not, should return true always.
    boolean calculateAccessCondition(EntityPlayer player);
}
