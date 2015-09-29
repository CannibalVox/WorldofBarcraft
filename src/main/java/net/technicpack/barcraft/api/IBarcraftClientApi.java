package net.technicpack.barcraft.api;

public interface IBarcraftClientApi extends IBarcraftApi {
    void setActionState(IAction action, ActionClientState state);
    void renderAction(IAction action, double x, double y, double z);
}
