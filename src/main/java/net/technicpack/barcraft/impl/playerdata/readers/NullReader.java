package net.technicpack.barcraft.impl.playerdata.readers;

import net.technicpack.barcraft.impl.playerdata.IPlayerReader;

import java.util.List;
import java.util.UUID;

public class NullReader implements IPlayerReader {

    @Override
    public void read(UUID uuid, List<String> earnedActions) {

    }
}
