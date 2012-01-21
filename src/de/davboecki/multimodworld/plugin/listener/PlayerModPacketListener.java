package de.davboecki.multimodworld.plugin.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.reteleport.ReTeleportThread;
import de.davboecki.multimodworld.server.modhandleevent.ModHandleEventListener;
import de.davboecki.multimodworld.server.modhandleevent.ModsMissingHandleEvent;
import de.davboecki.multimodworld.server.modhandleevent.ModsOKHandleEvent;


public class PlayerModPacketListener extends ModHandleEventListener{
	
	PrivatChest plugin;
	public ArrayList<String> TeleportPlayer = new ArrayList<String>();
	public HashMap<String,Location> TeleportDestination = new HashMap<String,Location>();
	public HashMap<String,Callable> CallableAction = new HashMap<String,Callable>();
	public HashMap<String,ArrayList<ItemStack>> ModInventory = new HashMap<String,ArrayList<ItemStack>>();
	public HashMap<String,ItemStack> ModInventoryHelmet = new HashMap<String,ItemStack>();
	public HashMap<String,ItemStack> ModInventoryChestplate = new HashMap<String,ItemStack>();
	public HashMap<String,ItemStack> ModInventoryLeggings = new HashMap<String,ItemStack>();
	public HashMap<String,ItemStack> ModInventoryBoots = new HashMap<String,ItemStack>();
	public static HashMap<String,String> IDMessage = new HashMap<String,String>();
	public static HashMap<String,Boolean> IDBoolean = new HashMap<String,Boolean>();
	
	public PlayerModPacketListener(PrivatChest instance){
		plugin = instance;
	}

	public void onModsMissingHandle(ModsMissingHandleEvent event) {
		Player player = event.getPlayer();
		ArrayList<String> Missing = event.getMissingModList();
		player.sendMessage("§4You are missing the following mods:");
		for(String Mod: Missing){
			player.sendMessage(Mod);
		}
	}
	
	public void onModsOKHandle(ModsOKHandleEvent event){
		if(IDBoolean.containsKey(event.getPlayer().getName())){
			if(!IDBoolean.get(event.getPlayer().getName())){
				event.getPlayer().sendMessage("§bIds worng Linkes.");
				for(Object MessageObject:IDMessage.get(event.getPlayer().getName()).split("\n")){
					event.getPlayer().sendMessage((String)MessageObject);
				}
				return;
			}
		}
		if(!plugin.ModPacketOK.contains(event.getPlayer().getName())) {
			plugin.ModPacketOK.add(event.getPlayer().getName());
			if(ModInventory.containsKey(event.getPlayer().getName())){
				for(ItemStack Item:ModInventory.get(event.getPlayer().getName())){
					event.getPlayer().getInventory().addItem(Item);
				}
				ModInventory.remove(event.getPlayer().getName());
			}
			if(ModInventoryHelmet.containsKey(event.getPlayer().getName())){
				event.getPlayer().getInventory().setHelmet(ModInventoryHelmet.get(event.getPlayer().getName()));
				ModInventoryHelmet.remove(event.getPlayer().getName());
			}
			if(ModInventoryChestplate.containsKey(event.getPlayer().getName())){
				event.getPlayer().getInventory().setChestplate(ModInventoryChestplate.get(event.getPlayer().getName()));
				ModInventoryChestplate.remove(event.getPlayer().getName());
			}
			if(ModInventoryLeggings.containsKey(event.getPlayer().getName())){
				event.getPlayer().getInventory().setLeggings(ModInventoryLeggings.get(event.getPlayer().getName()));
				ModInventoryLeggings.remove(event.getPlayer().getName());
			}
			if(ModInventoryBoots.containsKey(event.getPlayer().getName())){
				event.getPlayer().getInventory().setBoots(ModInventoryBoots.get(event.getPlayer().getName()));
				ModInventoryBoots.remove(event.getPlayer().getName());
			}
		}
		if(TeleportPlayer.contains(event.getPlayer().getName())){
			Player player = event.getPlayer();
			Location playerLoc = player.getLocation();
			if(player.getWorld().getGenerator() != plugin.Worldgen) return;
			if (Math.floor(playerLoc.getX()) != ((plugin.RoomControl.getRoomlocation(player).getX() * 7) + 5) || Math.floor(playerLoc.getY()) != 2 || Math.floor(playerLoc.getZ()) != ((plugin.RoomControl.getRoomlocation(player).getZ() * 7) + 3)) return;
			
		       Location TeleportLoc = new Location(player.getWorld(), 26.5, 8,
		               6.5, 90, 0);
					    plugin.teleporthandler.teleport(player,TeleportLoc);
		    ReTeleportThread.add(20,player,TeleportLoc);
		    player.sendMessage("§2Teleportiert");
		    player.sendMessage("You are now in the §1Mod Stargate§f Room");
		    TeleportPlayer.remove(event.getPlayer());
		} else if(TeleportDestination.containsKey(event.getPlayer().getName())) {
			Location loc = TeleportDestination.get(event.getPlayer().getName());
			if(loc.getWorld().getGenerator() == plugin.Worldgen){
				loc = plugin.RoomControl.playertospawn(event.getPlayer());
			} else {
				loc.add(0.0D, -1.0D, 0.0D);
				Location drueber = loc;
				drueber.add(0.0D, 1.0D, 0.0D);
				while((loc.getY() < 4 || !loc.getBlock().isEmpty() || !drueber.getBlock().isEmpty()) && loc.getY() < 128 ){
					loc.add(0.0D, 1.0D, 0.0D);
					drueber.add(0.0D, 1.0D, 0.0D);
				}
			}
			if(PrivatChest.debug()) event.getPlayer().sendMessage("ModLoaderMP OK, Teleporting to:"+loc.toString());
			plugin.teleporthandler.teleport(event.getPlayer(),loc);
		    ReTeleportThread.add(20,event.getPlayer(),loc);
			TeleportDestination.remove(event.getPlayer().getName());
		} else if(CallableAction.containsKey(event.getPlayer().getName())){
			try{
				CallableAction.get(event.getPlayer().getName()).call();
			} catch(Exception e){
				event.getPlayer().sendMessage(ChatColor.RED+"Error: Could not execute callable object.");
				e.printStackTrace();
			}
		}
	}
}
