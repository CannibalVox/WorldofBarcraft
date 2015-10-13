package net.technicpack.barcraft.impl.playerdata;

import java.util.List;
import java.util.UUID;

public interface IPlayerReader {
    void read(UUID uuid, List<String> earnedActions);
}
