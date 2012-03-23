package de.davboecki.multimodworld.plugin;

import org.bukkit.entity.Entity;

import org.bukkit.World;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatueSpawnListener implements Listener {
	public PrivatChest plugin;
	
	CreatueSpawnListener(PrivatChest pplugin){
		plugin = pplugin;
	}

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event){
    	Entity entity = event.getEntity();
    	World world = entity.getWorld();
    	if(world.getGenerator() == plugin.Worldgen){
    		entity.remove();
    	}
    }
    
}
