package net.technicpack.barcraft.impl;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.technicpack.barcraft.impl.playerdata.IPlayerReader;
import net.technicpack.barcraft.impl.playerdata.IPlayerWriter;
import net.technicpack.barcraft.impl.playerdata.PlayerAccessDatabase;
import net.technicpack.barcraft.impl.playerdata.readers.JsonFileReader;
import net.technicpack.barcraft.impl.playerdata.writers.JsonFileWriter;
import net.technicpack.barcraft.network.BarcraftNetwork;

import java.util.HashMap;
import java.util.UUID;

public class BarcraftDatabase {
    private HashMap<UUID, PlayerAccessDatabase> onlinePlayers = new HashMap<UUID, PlayerAccessDatabase>();

    private IPlayerReader reader = new JsonFileReader();
    private IPlayerWriter writer = new JsonFileWriter();

    public BarcraftDatabase() {}

    public void addPlayer(EntityPlayer player, PlayerAccessDatabase database) {
        onlinePlayers.put(player.getUniqueID(), database);
    }

    public PlayerAccessDatabase openPlayer(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        PlayerAccessDatabase db = new PlayerAccessDatabase(uuid, reader, writer);
        onlinePlayers.put(uuid, db);
        return db;
    }

    public void closePlayer(EntityPlayer player) {
        if (onlinePlayers.containsKey(player.getUniqueID())) {
            onlinePlayers.get(player.getUniqueID()).save();
            onlinePlayers.remove(player.getUniqueID());
        }
    }

    public PlayerAccessDatabase getPlayer(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        if (onlinePlayers.containsKey(uuid))
            return onlinePlayers.get(uuid);
        return null;
    }

    public void init() {
        onlinePlayers.clear();
    }

    public void shutdown() {
        onlinePlayers.clear();
    }

    @SubscribeEvent
    public void sendAbilityDiffs(TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.START)
            return;
        if (MinecraftServer.getServer() == null)
            return;

        for (Object playerObj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
            EntityPlayerMP player = (EntityPlayerMP)playerObj;

            PlayerAccessDatabase accessDatabase = getPlayer(player);

            if (accessDatabase != null && accessDatabase.hasDiffs()) {
                BarcraftNetwork.sendPlayerDiffs(player, accessDatabase);
                accessDatabase.resolveDiffs();
            }
        }
    }
}
