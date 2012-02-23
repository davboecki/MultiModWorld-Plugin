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
import de.davboecki.multimodworld.server.packethandleevent.PacketSendEvent;

public class PacketListener implements Listener {
	
	PrivatChest plugin;
	public boolean AllowModLoaderPacket = false;
	
	public PacketListener(PrivatChest instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPacketSendEvent(PacketSendEvent event) {
		if(plugin.MultiModWorld == null) return;
		try {
			Packet packet = event.packet;
			if(packet == null) return;
			if(event.getPlayer() == null) return;
			if(packet instanceof Packet230ModLoader){
				if(!ForgeLoginHooks.isPlayerConfirmed(event.getPlayer()) && !AllowModLoaderPacket && !ForgeLoginHooks.isPlayerSended(event.getPlayer())){
					event.setCancelled(true);
				}
			} else if(packet instanceof Packet5EntityEquipment){
				Packet5EntityEquipment Equipment = (Packet5EntityEquipment)packet;
				if(!ForgeLoginHooks.isPlayerConfirmed(event.getPlayer())) {
					if(plugin.MultiModWorld.isIdAllowed(event.getPlayer().getWorld().getName(), Equipment.c)) {
						Equipment.c = -1;
						Equipment.d = 0;
						//Player handplayer = getPlayerByEntityId(Equipment.a);
						//plugin.teleporthandler.teleportPlayerIntoExchangeWorld(handplayer);
					}
				}
			} else if(!packet.getClass().getPackage().getName().equalsIgnoreCase("net.minecraft.server")) {
				if(!ForgeLoginHooks.isPlayerConfirmed(event.getPlayer())){
					event.setCancelled(true);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
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
