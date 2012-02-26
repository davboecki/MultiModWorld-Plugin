package de.davboecki.multimodworld.plugin.settings;

import java.util.HashMap;

public class WorldSetting {
	public WorldSetting(){
		ItemList = "default";
		EntityList = "default";
		Tags = new HashMap<String,Boolean>();
		AllIdsAllowed = false;
		AllEntitiesAllowed = false;
		CheckTeleport = false;
		UseVanillaRecipes = false;
		PopulateChunk = false;
	}
	
	public String ItemList;
	public String EntityList;
	public Boolean AllIdsAllowed;
	public Boolean AllEntitiesAllowed;
	public Boolean CheckTeleport;
	public Boolean UseVanillaRecipes;
	public Boolean PopulateChunk;
	public HashMap<String,Boolean> Tags;
}
