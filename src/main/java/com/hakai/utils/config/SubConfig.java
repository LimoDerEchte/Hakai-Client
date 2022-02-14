package com.hakai.utils.config;

import com.google.gson.JsonObject;

public interface SubConfig {

    boolean load(JsonObject element);

    void save(JsonObject element);

}
