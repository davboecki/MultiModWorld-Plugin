package de.davboecki.multimodworld.plugin.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.reteleport.ReTeleportThread;
import de.davboecki.multimodworld.server.ForgeLoginHooks;
import de.davboecki.multimodworld.server.modhandleevent.ModsMissingHandleEvent;
import de.davboecki.multimodworld.server.modhandleevent.ModsOKHandleEvent;


public class PlayerModPacketListener implements Listener{
	
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
	
	@EventHandler
	public void onModsMissingHandle(ModsMissingHandleEvent event) {
		Player player = event.getPlayer();
		ForgeLoginHooks.removeSended(player);
		ArrayList<String> Missing = event.getMissingModList();
		player.sendMessage("§4You are missing the following mods:");
		for(String Mod: Missing){
			player.sendMessage(Mod);
		}
	}
	
	@EventHandler
	public void onModsOKHandle(ModsOKHandleEvent event){
		Player player = event.getPlayer();
		
		//xAuth
		boolean isxAuthGuest = false;
		Object xPlayer = null;
		if(plugin.isxAuth()) {
			try {
				xPlayer = ((com.cypherx.xauth.xAuth)plugin.getxAuth()).getPlayer(player.getName());
				isxAuthGuest = ((com.cypherx.xauth.xAuthPlayer)xPlayer).isGuest();
			} catch(Exception e) {}
			if(isxAuthGuest) {
				try {
					((com.cypherx.xauth.xAuth)plugin.getxAuth()).removeGuest((com.cypherx.xauth.xAuthPlayer) xPlayer);
				} catch(Exception e) {}
			}
		}
		
		if(!ForgeLoginHooks.isPlayerConfirmed(player)) {
			ForgeLoginHooks.confirmPlayer(player);
		}
		
		if(IDBoolean.containsKey(player.getName())){
			if(!IDBoolean.get(player.getName())){
				player.sendMessage("§bIds worng Linkes.");
				for(Object MessageObject:IDMessage.get(player.getName()).split("\n")){
					player.sendMessage((String)MessageObject);
				}
				if(isxAuthGuest) {
					try {	
						((com.cypherx.xauth.xAuth)plugin.getxAuth()).createGuest((com.cypherx.xauth.xAuthPlayer) xPlayer);
					} catch(Exception e) {}
				}
				return;
			}
		}
		
		ForgeLoginHooks.removeSended(player);
		if(ModInventory.containsKey(player.getName())){
			for(ItemStack Item:ModInventory.get(player.getName())){
				player.getInventory().addItem(Item);
			}
			ModInventory.remove(player.getName());
		}
		if(ModInventoryHelmet.containsKey(player.getName())){
			player.getInventory().setHelmet(ModInventoryHelmet.get(player.getName()));
			ModInventoryHelmet.remove(player.getName());
		}
		if(ModInventoryChestplate.containsKey(player.getName())){
			player.getInventory().setChestplate(ModInventoryChestplate.get(player.getName()));
			ModInventoryChestplate.remove(player.getName());
		}
		if(ModInventoryLeggings.containsKey(player.getName())){
			player.getInventory().setLeggings(ModInventoryLeggings.get(player.getName()));
			ModInventoryLeggings.remove(player.getName());
		}
		if(ModInventoryBoots.containsKey(player.getName())){
			player.getInventory().setBoots(ModInventoryBoots.get(player.getName()));
			ModInventoryBoots.remove(player.getName());
		}
		if(TeleportPlayer.contains(player.getName())){
			Location playerLoc = player.getLocation();
			if(player.getWorld().getGenerator() != plugin.Worldgen) return;
			if (Math.floor(playerLoc.getX()) != ((plugin.RoomControl.getRoomlocation(player).getX() * 7) + 5) || Math.floor(playerLoc.getY()) != 2 || Math.floor(playerLoc.getZ()) != ((plugin.RoomControl.getRoomlocation(player).getZ() * 7) + 3)) return;
			
			Location TeleportLoc = new Location(player.getWorld(), 26.5, 8, 6.5, 90, 0);
			plugin.teleporthandler.teleport(player,TeleportLoc);
			if(!isxAuthGuest) {
				ReTeleportThread.add(20,player,TeleportLoc);
			}
		    player.sendMessage("§2Teleportiert");
		    player.sendMessage("You are now in the §1Mod Stargate§f Room");
		    TeleportPlayer.remove(player);
		} else if(TeleportDestination.containsKey(player.getName())) {
			Location loc = TeleportDestination.get(player.getName());
			if(loc.getWorld().getGenerator() == plugin.Worldgen){
				loc = plugin.RoomControl.playertospawn(player);
			} else {
				loc.add(0.0D, -1.0D, 0.0D);
				Location drueber = loc;
				drueber.add(0.0D, 1.0D, 0.0D);
				while((loc.getY() < 4 || !loc.getBlock().isEmpty() || !drueber.getBlock().isEmpty()) && loc.getY() < 128 ){
					loc.add(0.0D, 1.0D, 0.0D);
					drueber.add(0.0D, 1.0D, 0.0D);
				}
			}
			if(PrivatChest.debug()) player.sendMessage("ModLoaderMP OK, Teleporting to:"+loc.toString());
			plugin.teleporthandler.teleport(player,loc);
			if(!isxAuthGuest) {
				ReTeleportThread.add(20,player,loc);
			}
			TeleportDestination.remove(player.getName());
		} else if(CallableAction.containsKey(player.getName())){
			try{
				CallableAction.get(player.getName()).call();
			} catch(Exception e){
				player.sendMessage(ChatColor.RED+"Error: Could not execute callable object.");
				e.printStackTrace();
			}
		}
		
		if(isxAuthGuest) {
			try {
				((com.cypherx.xauth.xAuth)plugin.getxAuth()).createGuest((com.cypherx.xauth.xAuthPlayer) xPlayer);
			} catch(Exception e) {}
		}
		
	}
}
