package com.hakai.irc.client;

import com.google.gson.JsonObject;
import com.hakai.utils.config.SubConfig;

import java.util.Base64;

public class IRCClientConfig implements SubConfig {

    protected IRCClientConfig() { }

    private boolean chatEnabled = true;
    private byte[] token = null;

    @Override
    public boolean load(JsonObject element) {
        if(element.has("chatEnabled")) {
            chatEnabled = element.get("chatEnabled").getAsBoolean();
        } else {
            chatEnabled = true;
        }
        if(element.has("token")) {
            token = Base64.getDecoder().decode(element.get("token").getAsString());
        } else {
            token = null;
        }
        return true;
    }

    @Override
    public void save(JsonObject element) {
        element.addProperty("chatEnabled", chatEnabled);
        if(token != null)
            element.addProperty("token", Base64.getEncoder().encodeToString(token));
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean b) {
        this.chatEnabled = b;
    }

    public byte[] getToken() {
        return token;
    }

    public void setToken(byte[] token) {
        this.token = token;
    }

}
