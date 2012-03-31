package de.davboecki.multimodworld.plugin.reteleport;

import java.util.HashMap;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.ModLoaderMp;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.Packet70Bed;
import net.minecraft.server.Packet9Respawn;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerConfigurationManager;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import de.davboecki.multimodworld.plugin.CheckItem;
import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.RoomLocation;
import de.davboecki.multimodworld.plugin.commandhandler.CallableObjects;
import de.davboecki.multimodworld.plugin.commandhandler.ConfirmListener;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.api.ForgeLoginHooks;

public class TeleportHandler {
	
	private boolean isLocalTeleport = false;
	private PrivatChest plugin = null;
	
	public TeleportHandler(PrivatChest instance){
		plugin = instance;
	}
	
    private World getFirstModChangeWorld(){
	    World TelWorld = null;
		World pTelWorld = null;
		for(World pWorld : plugin.getServer().getWorlds()){
			if(pWorld.getGenerator() == plugin.Worldgen){
				if(pTelWorld == null){
					pTelWorld = pWorld;
				}
				if(plugin.getSettings().ExchangeWorlds.containsKey(pWorld.getName()) && plugin.getSettings().ExchangeWorlds.get(pWorld.getName()).WorldType.equalsIgnoreCase("Mod")){
					TelWorld = pWorld;
					break;
				}
			}
		}
		if(TelWorld == null){
			TelWorld = pTelWorld;
		}
		if(TelWorld == null){
			for(World pWorld : plugin.getServer().getWorlds()){
				if(!Settings.getWorldSetting(pWorld.getName()).CheckTeleport) {
					TelWorld = pWorld;
					break;
				}
			}
		}
		if(TelWorld == null){
			TelWorld = plugin.getServer().getWorlds().get(0);
		}
		return TelWorld;
    }
    
    public Location getPlayerExchangeWorldLocation(Player player) {
    	World firstworld = getFirstModChangeWorld();
    	if(firstworld.getGenerator() == plugin.Worldgen) {
    		if(plugin.RoomControl.playerhasRoomInWorld(player,firstworld)){
    			RoomLocation roomloc = plugin.RoomControl.getRoomlocationForWorld(player, firstworld);
    			return new Location(getFirstModChangeWorld(), (roomloc.x*7)+3.5, 2, (roomloc.z*7)+3.5);
    		} else {
    			return plugin.RoomControl.getPlayerLobbyLocation(player, firstworld);
    		}
    	} else {
    		return firstworld.getSpawnLocation();
    	}
    }
    
    /*public void teleportPlayerIntoExchangeWorld(Player player) {
    	isLocalTeleport = true;
    	player.teleport(getPlayerExchangeWorldLocation(player));
    	isLocalTeleport = false;
    }*/
    
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	if(isLocalTeleport) return;
        Player player = event.getPlayer();
        World world = event.getTo().getWorld();
    	if(Settings.getWorldSetting(world.getName()).CheckTeleport && !Settings.getInstance().ExchangeWorlds.containsKey(world.getName())) {
	    	if(!ForgeLoginHooks.isPlayerConfirmed(player)) {
	    		plugin.PrivatChestPlayerListener.addMoveTask(player.getName(),new CallableObjects(new Object[]{event.getPlayer(),plugin.confirmlistener},plugin){
    				long LastTime;
    	    		@Override
    				public Object call() throws Exception {
    	    			if(LastTime + 2000 > System.currentTimeMillis()) {
    	    				return false;
    	    			}
    	    			LastTime = System.currentTimeMillis();
						if(((ConfirmListener)args[1]).ExistTaskFor(((Player)args[0]).getName()))((Player)args[0]).sendMessage(ChatColor.AQUA+"Send ModLoaderMP Packet 230? <"+ChatColor.GREEN+"yes"+ChatColor.AQUA+"/"+ChatColor.RED+"no"+ChatColor.AQUA+">");
						return !((ConfirmListener)args[1]).ExistTaskFor(((Player)args[0]).getName());
					}
				});
	    		plugin.confirmlistener.addTask(new CallableObjects(new Object[]{event.getPlayer().getName(),event.getTo(),player},plugin){
					@Override
					public Object call() throws Exception {
			    		plugin.PlayerModPacketListener.TeleportDestination.put((String)args[0], (Location)args[1]);
			    		if(PrivatChest.debug()) plugin.log.info("Packet 230: "+((String)args[0])+": OnPlayerTeleport, World: "+((Location)args[1]).getWorld().getName());
			    		((Player)args[2]).sendMessage(ChatColor.GREEN+"Teleporting to destination ...");
			    		plugin.sendModLoaderPacket((Player)args[2]);
						return null;
					}
    			},player.getName());
	    		player.sendMessage(ChatColor.AQUA+"Send ModLoaderMP Packet 230? <"+ChatColor.GREEN+"yes"+ChatColor.AQUA+"/"+ChatColor.RED+"no"+ChatColor.AQUA+">");
	    		if(Settings.getWorldSetting(event.getFrom().getWorld().getName()).CheckTeleport) {
	    			Location loc = getPlayerExchangeWorldLocation(player);
	    			event.setTo(loc);
	    			if(PrivatChest.debug()) event.getPlayer().sendMessage("OnTeleport, set Target to:"+loc.toString());
    	    		ReTeleportThread.add(1,player,loc);
	    		} else {
	    			event.setCancelled(true);
	    		}
    		}
    	} else {
    		if((Settings.getWorldSetting(event.getFrom().getWorld().getName()).CheckTeleport || Settings.getInstance().ExchangeWorlds.containsKey(event.getFrom().getWorld().getName())) && !Settings.getInstance().ExchangeWorlds.containsKey(world.getName())){
	    		if(!new CheckItem(plugin, world.getName(), false).NoModItem(player, true)){
	    			player.sendMessage(ChatColor.RED+"Teleport denied.");
	    			if(!Settings.getWorldSetting(event.getFrom().getWorld().getName()).CheckTeleport){
		    			Location loc = new Location(getFirstModChangeWorld(), 6.5, 10, 6.5);
		    			event.setTo(loc);
	    	    		ReTeleportThread.add(5,player,loc);
		    		} else {
		    			event.setCancelled(true);
		    		}
	    		}
    		}
    	}
    }
    
	public void teleport(Player player, Location loc){
		isLocalTeleport = true;
		((CraftPlayer)player).teleport(loc);
		isLocalTeleport = false;
	}
    
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	if(event.getPlayer().getWorld().getGenerator() != plugin.Worldgen) {
    		Player player = event.getPlayer();
    		World world = event.getRespawnLocation().getWorld();
        	if(Settings.getWorldSetting(world.getName()).CheckTeleport) {
    	    	if(!ForgeLoginHooks.isPlayerConfirmed(player)){
    	    		plugin.PlayerModPacketListener.TeleportDestination.put(player.getName(), event.getRespawnLocation());
    	    		if(PrivatChest.debug()) plugin.log.info("Packet 230: "+event.getPlayer().getName()+": onPlayerRespawn, World: "+world.getName());
    	    		plugin.sendModLoaderPacket(player);
    	    		Location loc = new Location(getFirstModChangeWorld(), 6.5, 10, 6.5);
    	    		isLocalTeleport = true;
    	    		event.setRespawnLocation(loc);
    	    		isLocalTeleport = false;
    	    		ReTeleportThread.add(10,player,loc);
        		}
        	}
        	return;
    	}
        Player player = event.getPlayer();
        World world = player.getWorld();

        plugin.RoomControl.LoadRoom(player);

        Location playerLoc = plugin.RoomControl.playertospawn(player);
		isLocalTeleport = true;
        event.setRespawnLocation(playerLoc);
		isLocalTeleport = false;
    	ReTeleportThread.add(10,player,playerLoc);
    }
    
	public void HandleJoin(PlayerJoinEvent event, boolean SendPacket) {
		if(event.getPlayer().getWorld().getGenerator() != plugin.Worldgen){
    		Player player = event.getPlayer();
    		World world = player.getLocation().getWorld();
        	if(Settings.getWorldSetting(world.getName()).CheckTeleport) {
        		if(!ForgeLoginHooks.isPlayerConfirmed(player)) {
	    	    	Location Oldloc = player.getLocation();
	    	    	Location loc = getPlayerExchangeWorldLocation(player);
	    	    	event.getPlayer().sendMessage("Join Teleport to:"+ loc.toString());
	    	    	//Set Entity Pos
	    	    	((CraftPlayer)player).getHandle().dimension = ((CraftWorld)loc.getWorld()).getHandle().dimension;
	    	    	((CraftPlayer)player).getHandle().world = ((CraftWorld)loc.getWorld()).getHandle();
	    	    	((CraftPlayer)player).getHandle().locX = loc.getX();
	    	    	((CraftPlayer)player).getHandle().locY = loc.getY();
	    	    	((CraftPlayer)player).getHandle().locZ = loc.getZ();
	    	    	((CraftPlayer)player).getHandle().netServerHandler.sendPacket(new Packet51MapChunk(((CraftChunk)world.getChunkAt(player.getLocation())).getHandle(),true,0));
	    	    	//teleport(player,loc);
	    	    	//ReTeleportThread.add(10,player,loc);
		        	if(!SendPacket) {
		    	    	player.sendMessage(ChatColor.AQUA+"Send ModLoaderMP Packet 230? <"+ChatColor.GREEN+"yes"+ChatColor.AQUA+"/"+ChatColor.RED+"no"+ChatColor.AQUA+">");
		    	    	plugin.confirmlistener.addTask(new CallableObjects(new Object[]{player,Oldloc},plugin){
		    				@Override
		    				public Object call() throws Exception {
		    		    		plugin.PlayerModPacketListener.TeleportDestination.put(((Player)args[0]).getName(), (Location)args[1]);
		    		    		if(PrivatChest.debug())plugin.log.info("Packet 230: "+(((Player)args[0]).getName())+": OnJoinConfirm, World: "+((Location)args[1]).getWorld().getName());
		    					((Player)args[0]).sendMessage(ChatColor.GREEN+"Teleported to old world.");
		    					plugin.sendModLoaderPacket((Player)args[0]);
		    					return null;
		    				}
		        		},player.getName());
		    	    	plugin.PrivatChestPlayerListener.addMoveTask(player.getName(),new CallableObjects(new Object[]{event.getPlayer(),plugin.confirmlistener},plugin){
		    				long LastTime;
		    	    		@Override
		    				public Object call() throws Exception {
		    	    			if(LastTime + 2000 > System.currentTimeMillis()) {
		    	    				return false;
		    	    			}
		    	    			LastTime = System.currentTimeMillis();
		    					if(((ConfirmListener)args[1]).ExistTaskFor(((Player)args[0]).getName()))((Player)args[0]).sendMessage(ChatColor.AQUA+"Send ModLoaderMP Packet 230? <"+ChatColor.GREEN+"yes"+ChatColor.AQUA+"/"+ChatColor.RED+"no"+ChatColor.AQUA+">");
		    					return !((ConfirmListener)args[1]).ExistTaskFor(((Player)args[0]).getName());
		    				}
		    			});
		        	} else {
		        		plugin.PlayerModPacketListener.TeleportDestination.put(player.getName(), Oldloc);
			    		
		        	}
	        	}
	        	return;
        	}
    	}
	}
   }
