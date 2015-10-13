package net.technicpack.barcraft.impl.playerdata.writers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.server.MinecraftServer;
import net.technicpack.barcraft.impl.playerdata.IPlayerWriter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JsonFileWriter implements IPlayerWriter {

    private static Gson gson = new Gson();
    
    @Override
    public void write(UUID uuid, List<String> earnedActions) {
        File file = getPlayerFile(uuid);

        if (file == null)
            return;

        JsonObject obj = new JsonObject();
        JsonArray actions = new JsonArray();
        for (String action : earnedActions) {
            actions.add(new JsonPrimitive(action));
        }
        obj.add("earnedActions", actions);

        String data = gson.toJson(obj);

        try {
            FileUtils.writeStringToFile(file, data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private File getPlayerFile(UUID uuid) {
        MinecraftServer server = MinecraftServer.getServer();

        if (server == null)
            return null;

        return new File(new File(server.getEntityWorld().getSaveHandler().getWorldDirectory(), "worldofbarcraft"), uuid.toString() + ".json");
    }
}
