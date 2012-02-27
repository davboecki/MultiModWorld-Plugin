package de.davboecki.multimodworld.plugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet230ModLoader;
import net.minecraft.server.Packet5EntityEquipment;
import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.server.ForgeLoginHooks;

public class PacketListener{
	
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
			if(packet instanceof Packet230ModLoader){
				if(!ForgeLoginHooks.isPlayerConfirmed(player) && !AllowModLoaderPacket && !ForgeLoginHooks.isPlayerSended(player)){
					return false;
				}
			} else if(packet instanceof Packet5EntityEquipment){
				Packet5EntityEquipment Equipment = (Packet5EntityEquipment)packet;
				if(!ForgeLoginHooks.isPlayerConfirmed(player)) {
					if(plugin.MultiModWorld.isIdAllowed(player.getWorld().getName(), Equipment.c)) {
						Equipment.c = -1;
						Equipment.d = 0;
						//Player handplayer = getPlayerByEntityId(Equipment.a);
						//plugin.teleporthandler.teleportPlayerIntoExchangeWorld(handplayer);
					}
				}
			} else if(!packet.getClass().getPackage().getName().equalsIgnoreCase("net.minecraft.server")) {
				if(!ForgeLoginHooks.isPlayerConfirmed(player)){
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
