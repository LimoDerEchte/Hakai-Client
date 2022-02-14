package com.hakai.utils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class YellowSnowConfig {

    private static YellowSnowConfig instance;

    public static YellowSnowConfig get() {
        if(instance == null)
            instance = new YellowSnowConfig(new File(MinecraftClient.getInstance().runDirectory, "yellowsnow/config.json"));
        return instance;
    }

    private final HashMap<String, SubConfig> map = new HashMap<>();
    private final Gson gson;
    private final File file;

    private YellowSnowConfig(File file) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.file = file;
    }

    public void registerSubConfig(String key, SubConfig config) {
        /*
        String className = config.getClass().getName();
        String fixedClassName;
        for(int i=0; map.containsKey(fixedClassName = (className + ("[" + i + "]"))); i++);
         */
        map.put(key, config);
    }

    public boolean load() throws IOException {
        if(!file.exists())
            return false;
        JsonObject root = gson.fromJson(new FileReader(file), JsonObject.class);
        if(root == null)
            return false;
        SubConfig subConfig;
        boolean complete = true;
        for(Map.Entry<String, JsonElement> entry : root.entrySet()) {
            if((subConfig = map.get(entry.getKey())) == null || !entry.getValue().isJsonObject()) {
                complete = false;
            } else {
                try {
                    if(!subConfig.load(entry.getValue().getAsJsonObject()))
                        complete = false;
                } catch(Exception e) {
                    LogManager.getLogger().error("Failed to load config part" + e);
                    complete = false;
                }
            }
        }
        return root.size() == map.size() && complete;
    }

    public void save() throws IOException {
        file.getParentFile().mkdirs();
        if(!file.exists())
            file.createNewFile();
        JsonObject root = new JsonObject();
        for(Map.Entry<String, SubConfig> entry : map.entrySet()) {
            JsonObject element = new JsonObject();
            entry.getValue().save(element);
            root.add(entry.getKey(), element);
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(gson.toJson(root));
        fileWriter.close();
    }

}
