package net.technicpack.barcraft.impl.playerdata;

import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerAccessDatabase {
    private UUID playerUUID;

    private List<String> cachedActions = new ArrayList<String>();
    private List<String> earnedActions = new ArrayList<String>();

    private List<AccessDiff> diffs = new ArrayList<AccessDiff>();

    private IPlayerWriter writer;

    public PlayerAccessDatabase(UUID uuid, IPlayerReader reader, IPlayerWriter writer) {
        this.playerUUID = uuid;
        this.writer = writer;
        load(reader);
    }

    private void load(IPlayerReader reader) {
        for (int i = cachedActions.size()-1; i >= 0; i--) {
            createDiff(DiffType.REMOVE, cachedActions.get(i));
        }
        earnedActions.clear();
        cachedActions.clear();
        reader.read(playerUUID, earnedActions);
        for (String action : earnedActions) {
            cachedActions.add(action);
            createDiff(DiffType.ADD, action);
        }
    }

    public void save() {
        writer.write(playerUUID, earnedActions);
    }

    public boolean hasAction(String key) {
        return cachedActions.contains(key);
    }

    public void addConditional(String key) {
        cachedActions.add(key);
        createDiff(DiffType.ADD, key);
    }

    public void removeConditional(String key) {
        if (cachedActions.contains(key)) {
            cachedActions.remove(key);
            createDiff(DiffType.REMOVE, key);
        }
    }

    public void addUnconditional(String key) {
        cachedActions.add(key);
        earnedActions.add(key);
        createDiff(DiffType.ADD, key);
    }

    public void removeUnconditional(String key) {
        if (cachedActions.contains(key)) {
            cachedActions.remove(key);
            createDiff(DiffType.REMOVE, key);
        }

        if (earnedActions.contains(key))
            earnedActions.remove(key);
    }

    private void createDiff(DiffType type, String action) {
        boolean tryRemoveDiff = false;
        DiffType removeType = DiffType.ADD;

        switch (type) {
            case ADD:
                tryRemoveDiff = true;
                removeType = DiffType.REMOVE;
                break;
            case REMOVE:
                tryRemoveDiff = true;
                removeType = DiffType.ADD;
                break;
        }

        if (tryRemoveDiff && deleteLastDiff(removeType, action))
            return;

        diffs.add(new AccessDiff(type, action));
    }

    private boolean deleteLastDiff(DiffType type, String action) {
        for (int i = diffs.size()-1; i >= 0; i--) {
            AccessDiff testDiff = diffs.get(i);

            if (testDiff.getType() == type && testDiff.getAction().equals(action)) {
                diffs.remove(i);
                return true;
            }
        }

        return false;
    }

    public boolean hasDiffs() { return !diffs.isEmpty();}
    public Iterable<AccessDiff> getAccessDiffs() { return diffs; }
    public void resolveDiffs() { diffs.clear(); }
}
