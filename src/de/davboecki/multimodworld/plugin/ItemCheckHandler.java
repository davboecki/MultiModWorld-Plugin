package de.davboecki.multimodworld.plugin;

import de.davboecki.multimodworld.plugin.settings.Default;
import de.davboecki.multimodworld.plugin.settings.Settings;

public class ItemCheckHandler {
	
	public static boolean isItemAllowed(String WorldName, int id){
		if(Settings.getInstance().ExchangeWorlds.containsKey(WorldName)){
			return Default.ItemList().contains((long)id);
		}else{
			return Settings.getItemListExchangeWorld(WorldName).contains((long)id) || Settings.getWorldSetting(WorldName).AllIdsAllowed;
		}
	}
	
	public static boolean isModItem(int id){
		return !Default.ItemList().contains((long)id);
	}
}
