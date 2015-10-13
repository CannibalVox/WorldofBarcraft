package net.technicpack.barcraft.impl.playerdata;

import java.util.List;
import java.util.UUID;

public interface IPlayerWriter {
    void write(UUID uuid, List<String> earnedActions);
}
