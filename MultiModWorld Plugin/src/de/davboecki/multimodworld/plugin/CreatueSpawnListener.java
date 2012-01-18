package de.davboecki.multimodworld.plugin;

import org.bukkit.entity.Entity;

import org.bukkit.World;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

public class CreatueSpawnListener extends EntityListener{
	public PrivatChest plugin;
	
	CreatueSpawnListener(PrivatChest pplugin){
		plugin = pplugin;
	}
	
    public void onCreatureSpawn(CreatureSpawnEvent event){
    	Entity entity = event.getEntity();
    	World world = entity.getWorld();
    	if(world.getGenerator() == plugin.Worldgen){
    		entity.remove();
    	}
    }
    
}
