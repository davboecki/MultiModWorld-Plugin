package de.davboecki.multimodworld.plugin.listener;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet230ModLoader;
import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.server.packethandleevent.PacketHandleEventListener;
import de.davboecki.multimodworld.server.packethandleevent.PacketSendEvent;

public class PacketListener extends PacketHandleEventListener {
	
	PrivatChest plugin;
	public boolean AllowModLoaderPacket = false;
	
	public PacketListener(PrivatChest instance){
		plugin = instance;
	}
	
	public void onPacketSendEvent(PacketSendEvent event){
		Packet packet = event.packet;
		if(packet instanceof Packet230ModLoader){
			if(!plugin.ModPacketOK.contains(event.getPlayer().getName()) && !AllowModLoaderPacket){
				event.setCancelled(true);
			}
		} else if(!packet.getClass().getPackage().getName().equalsIgnoreCase("net.minecraft.server")) {
			event.setCancelled(true);
		}
	}
}
