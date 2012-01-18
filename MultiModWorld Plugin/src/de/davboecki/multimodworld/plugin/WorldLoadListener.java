package de.davboecki.multimodworld.plugin;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

import de.davboecki.multimodworld.plugin.settings.WorldSetting;


public class WorldLoadListener extends WorldListener{
	PrivatChest plugin;
	
	WorldLoadListener(PrivatChest instance){
		plugin = instance;
	}
	
	public void onWorldLoad(WorldLoadEvent event){
		if(event.getWorld().getGenerator() == plugin.Worldgen) return;
		String WorldName = event.getWorld().getName();
		if(!plugin.Settings.WorldSettings.containsKey(WorldName)){
			WorldSetting WorldSetting = new WorldSetting();
			for(Object Tag: plugin.Settings.WorldTagList.toArray()){
				if(!(Tag instanceof String)) continue;
				WorldSetting.Tags.put((String)Tag, false);
			}
			plugin.Settings.WorldSettings.put(WorldName,WorldSetting);
			plugin.save();
		}
	}
}
