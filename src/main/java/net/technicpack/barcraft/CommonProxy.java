package net.technicpack.barcraft;

import net.minecraft.client.settings.KeyBinding;
import net.technicpack.barcraft.api.IBarcraftApi;
import net.technicpack.barcraft.impl.BarcraftApi;

public class CommonProxy {
    public void registerClientKeys() {}

    private IBarcraftApi api;

    public void initApi() {
        this.api = new BarcraftApi();
    }

    public IBarcraftApi getApi() {
        return this.api;
    }

    public void addActions() {}
}
