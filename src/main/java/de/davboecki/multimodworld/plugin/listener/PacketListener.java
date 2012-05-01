package de.davboecki.multimodworld.plugin.listener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.*;
import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.api.plugin.PlayerStatus;

public class PacketListener{
	
	Logger logger = Logger.getLogger("Bukkit");
	PrivatChest plugin;
	public boolean AllowModLoaderPacket = false;
	
	public PacketListener(PrivatChest instance){
		plugin = instance;
	}
	
	
	public boolean PacketSend(Packet packet, Player player) {
		if(plugin.MultiModWorld == null) return true;
		try {
			if(packet == null) return true;
			if(player == null) return true;
			/*
			 if(log && !(packet instanceof Packet50PreChunk)) {
				StringBuilder logentry = new StringBuilder();
				logentry.append("Player:"+player.getName()+", Packet:"+packet.getClass().getName());
				logentry.append(", ");
				try {
					Field a = Packet.class.getDeclaredField("a");
					a.setAccessible(true);
					HashMap<Class<?>, Integer> aMap = (HashMap<Class<?>, Integer>)a.get(null);
					logentry.append("Real Number: "+aMap.get(packet.getClass())+", ");
				} catch(Exception e) {}
				for(Field f:packet.getClass().getDeclaredFields()) {
					try {
						try {
							f.setAccessible(true);
						} catch(Exception e) {}
						Object o = f.get(packet);
						try {
							Object[] oarray = (Object[])o;
							StringBuilder arrayentry = new StringBuilder();
							arrayentry.append("[");
							for(Object opart:oarray) {
								if(arrayentry.toString().equals("[")) {
									arrayentry.append(opart.toString());
								} else {
									arrayentry.append(", "+opart.toString());
								}
							logentry.append(f.getName()+": '"+arrayentry+"]', ");
							}
						} catch(Exception e) {
							logentry.append(f.getName()+": '"+o+"', ");
						}
					} catch(Exception e) {
						logentry.append(f.getName()+": 'ACCESS_DENIED', ");
					}
				}
				logger.info(logentry.toString());
			}
			*/
			if(packet instanceof Packet53BlockChange) {
				try {
					Field vanilla = packet.getClass().getDeclaredField("vanilla"); //Crash and go on if wrong version
					vanilla.set(packet, PlayerStatus.isVanilla(player.getName()));
				} catch(Exception e) {
					
				}
			} else if(packet instanceof Packet5EntityEquipment) {
				Packet5EntityEquipment Equipment = (Packet5EntityEquipment)packet;
				if(PlayerStatus.isVanilla(player.getName())) {
					if(plugin.MultiModWorld.isIdAllowed(player.getWorld().getName(), Equipment.c)) {
						Equipment.c = -1;
						Equipment.d = 0;
						//Player handplayer = getPlayerByEntityId(Equipment.a);
						//plugin.teleporthandler.teleportPlayerIntoExchangeWorld(handplayer);
					}
				}
			} else if(!packet.getClass().getPackage().getName().equalsIgnoreCase("net.minecraft.server")) {
				if(PlayerStatus.isVanilla(player.getName())){
					return false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private Player getPlayerByEntityId(int a) {
		for(Player player: Bukkit.getOnlinePlayers()){
			if(((CraftPlayer)player).getHandle().id == a){
				return player;
			}
		}
		return null;
	}
}
