package de.davboecki.multimodworld.plugin.reteleport;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.davboecki.multimodworld.plugin.PrivatChest;

public class ReTeleportThread implements Runnable {
	
	private static HashMap<String,PlayerSettings> ReTeleportTasks = new HashMap<String,PlayerSettings>();
	private static HashMap<String,PlayerSettings> AfterTick = new HashMap<String,PlayerSettings>();
	
	private static int currentTick;
	private PrivatChest plugin = null;
	
	public ReTeleportThread(PrivatChest instance) {
		plugin = instance;
	}
	
	public static void add(int Ticks,Player player ,Location target) {
		PlayerSettings value = new PlayerSettings();
		value.Player = player;
		value.Target = target;
		value.Targettick = currentTick+Ticks;
		value.Name = player.getName();
		if(ReTeleportTasks.containsKey(player.getName())){
			ReTeleportTasks.remove(player.getName());
		}
		ReTeleportTasks.put(player.getName(), value);
		if(AfterTick.containsKey(player.getName())) {
			AfterTick.remove(player.getName());
		}
	}
	
	public void run(){
		/*
		for(PlayerSettings setting: AfterTick.values()){
			if(setting.Target.getWorld().getName() != setting.Player.getWorld().getName() || setting.Target.distance(setting.Player.getLocation()) > 5){
				plugin.teleporthandler.teleport(setting.Player,setting.Target);
			} else {
				AfterTick.remove(setting.Name);
			}
		}
		*/
		
		currentTick++;
		for(PlayerSettings setting: ReTeleportTasks.values()){
			if(currentTick > setting.Targettick) {
				if(setting.Player.isOnline()){
					if((setting.Target.getWorld().getName() != setting.Player.getWorld().getName() || setting.Target.distance(setting.Player.getLocation()) > 5)) {
						setting.Player.sendMessage("Reteleporting, Form:"+setting.Player.getLocation().toString()+" To:"+setting.Target.toString());
						if(PrivatChest.debug()) plugin.teleporthandler.teleport(setting.Player,setting.Target);
						if(AfterTick.containsKey(setting.Name)){
							AfterTick.remove(setting.Name);
						}
						AfterTick.put(setting.Name,setting);
					}
					ReTeleportTasks.remove(setting.Name);
				}
			}
		}
	}
}
