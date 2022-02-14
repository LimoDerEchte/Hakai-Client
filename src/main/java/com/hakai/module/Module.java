package com.hakai.module;

public class Module {

	private final String displayName;
	private final String description;

	public Module(String displayName, String description) {
		this.displayName = displayName;
		this.description = description;

	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

}
