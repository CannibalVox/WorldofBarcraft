package net.technicpack.barcraft.impl.playerdata.readers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.technicpack.barcraft.impl.playerdata.DiffType;
import net.technicpack.barcraft.impl.playerdata.IPlayerReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JsonFileReader implements IPlayerReader {

    private static Gson gson = new Gson();

    @Override
    public void read(UUID uuid, List<String> earnedActions) {
        File file = getPlayerFile(uuid);

        if (file == null)
            return;

        if (!file.exists())
            return;

        try {
            String data = FileUtils.readFileToString(file);
            JsonObject obj = gson.fromJson(data, JsonObject.class);
            if (obj.has("earnedActions")) {
                JsonArray actions = obj.get("earnedActions").getAsJsonArray();
                for (JsonElement action : actions) {
                    if (action.isJsonPrimitive()) {
                        earnedActions.add(action.getAsString());
                    }
                }
            }
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
