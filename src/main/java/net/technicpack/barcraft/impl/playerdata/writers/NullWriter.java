package net.technicpack.barcraft.impl.playerdata.writers;

import net.technicpack.barcraft.impl.playerdata.IPlayerWriter;

import java.util.List;
import java.util.UUID;

public class NullWriter implements IPlayerWriter {

    @Override
    public void write(UUID uuid, List<String> earnedActions) {

    }
}
