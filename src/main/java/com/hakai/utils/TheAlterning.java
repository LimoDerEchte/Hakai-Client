package com.hakai.utils;

import com.mojang.authlib.Environment;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

public class TheAlterning {

    public static final byte MOJANG = 0;
    public static final byte THE_ALTERNING = 1;

    private static final String AUTH_THEALTERNING = "http://authserver.thealtening.com";
    private static final String SESSION_THEALTERNING = "http://sessionserver.thealtening.com";
    private static final String SESSION_MOJANG = "https://sessionserver.mojang.com";

    private static final Environment ENVIRONMENT_MOJANG = YggdrasilEnvironment.PROD.getEnvironment();
    private static final Environment ENVIRONMENT_THEALTERNING = Environment.create(AUTH_THEALTERNING, ENVIRONMENT_MOJANG.getAccountsHost(), SESSION_THEALTERNING, ENVIRONMENT_MOJANG.getServicesHost(), ENVIRONMENT_MOJANG.getName());

    public static final void switchAuth(byte type) {
        switch (type) {
            case MOJANG:
                switchToMojang();
                break;
            case THE_ALTERNING:
                switchToAlterning();
                break;
            default:
                throw new IllegalArgumentException("'" + type + "' is no type");
        }
    }

    private static final void switchToAlterning() {
        MinecraftSessionService service = MinecraftClient.getInstance().getSessionService();
        if(service instanceof YggdrasilMinecraftSessionService) {
            setBaseUrl((YggdrasilMinecraftSessionService) service, SESSION_THEALTERNING + "/session/minecraft/");
            setJoinUrl((YggdrasilMinecraftSessionService) service, SESSION_THEALTERNING + "/session/minecraft/join");
            setCheckUrl((YggdrasilMinecraftSessionService) service, SESSION_THEALTERNING + "/session/minecraft/hasJoined");
        }
        setEnvironment(YggdrasilEnvironment.PROD, ENVIRONMENT_THEALTERNING);
    }

    private static final void switchToMojang() {
        MinecraftSessionService service = MinecraftClient.getInstance().getSessionService();
        if (service instanceof YggdrasilMinecraftSessionService) {
            setBaseUrl((YggdrasilMinecraftSessionService) service, SESSION_MOJANG + "/session/minecraft/");
            setJoinUrl((YggdrasilMinecraftSessionService) service, SESSION_MOJANG + "/session/minecraft/join");
            setCheckUrl((YggdrasilMinecraftSessionService) service, SESSION_MOJANG + "/session/minecraft/hasJoined");
        }
        setEnvironment(YggdrasilEnvironment.PROD, ENVIRONMENT_MOJANG);
    }

    public static void setBaseUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(service, url);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void setJoinUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("joinUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void setCheckUrl(YggdrasilMinecraftSessionService service, String url) {
        try {
            Field field = service.getClass().getDeclaredField("checkUrl");
            field.setAccessible(true);
            field.set(service, new URL(url));
        } catch (IllegalAccessException | NoSuchFieldException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void setEnvironment(YggdrasilEnvironment yggdrasilEnvironment, Environment environment) {
        try {
            Field field = yggdrasilEnvironment.getClass().getDeclaredField("environment");
            field.setAccessible(true);
            field.set(yggdrasilEnvironment, environment);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
