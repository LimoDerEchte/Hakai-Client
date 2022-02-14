package com.hakai.utils;

import com.hakai.main.HakaiClient;

public class TPS {

	private static String tpsDisplay = "§a*20";
	private static long lastTime = 0;

	private static int counter = -1;

	public static void registerHUD() {
		HakaiClient.getInstance().registerHUDElement(() -> {
			return tpsDisplay;
		});
	}

	public static void serverWorldTimePacket() {
		long diff = lastTime == 0 ? 0 : (System.currentTimeMillis() - lastTime);
		if(diff <= 5000){
			if(diff > 20000)
				diff = 20000;
			double tps = 1000D / diff * 20;

			tpsDisplay = "TPS§8: " + ((tps > 18.0D) ? "§a" : ((tps > 16.0D) ? "§e" : "§c")) +
					((tps > 21.0D) ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D);
		}
		lastTime = System.currentTimeMillis();
	}

	public static void tick(){
		long diff = lastTime == 0 ? 0 : (System.currentTimeMillis() - lastTime);
		if(diff > 5000){
			double tps = 1000D / diff * 20;

			tpsDisplay = "TPS§8: " + ((tps > 18.0D) ? "§a" : ((tps > 16.0D) ? "§e" : "§c")) +
					((tps > 21.0D) ? "*" : "") + Math.min(Math.round(tps * 100.0D) / 100.0D, 20.0D) +
					"\n\u27A5 Last tick: " + (int)diff / 1000D + "s";
		}
	}
}
