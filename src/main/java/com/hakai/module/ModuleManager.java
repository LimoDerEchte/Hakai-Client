package com.hakai.module;

import java.util.HashMap;

public class ModuleManager {

	private static ModuleManager instance = null;

	public static ModuleManager get() {
		if(instance == null)
			instance = new ModuleManager();
		return instance;
	}

	private HashMap<String, Module> moduleMap = new HashMap<>();

	private ModuleManager() {
	}

	public void registerModule(String name, Module module) {
		moduleMap.put(name.replace(" ", "").toLowerCase(), module);
	}
	
	public HashMap<String, Module> getModuleMap() {
		return moduleMap;
	}
	
	public Module getModule(String module) {
		return moduleMap.get(module.toLowerCase());
	}

	public void loadModuleConfig() {

	}

	public void saveModuleConfig() {

	}

}
