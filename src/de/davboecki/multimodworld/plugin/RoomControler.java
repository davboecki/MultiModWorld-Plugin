package de.davboecki.multimodworld.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.server.ModLoaderMp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.server.ForgeLoginHooks;


public class RoomControler {

	PrivatChest plugin;
	HashMap<String,Boolean> Roomloaded = new HashMap<String,Boolean>();
	ArrayList<String> MessageSend = new ArrayList<String>();
	
    RoomControler(PrivatChest pplugin) {
    	plugin = pplugin;
    }
    
    public boolean playerhasRoom(Player player) {
    	if(!plugin.Settings.ExchangeWorlds.containsKey(player.getWorld().getName())) return false;
        return plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.containsKey(player.getName()) && player.hasPermission("privatchest.hasroom");
     }
    public boolean playerhasRoomInWorld(Player player,World world) {
    	if(!plugin.Settings.ExchangeWorlds.containsKey(world.getName())) return false;
        return plugin.Settings.ExchangeWorlds.get(world.getName()).ChestRooms.containsKey(player.getName()) && player.hasPermission("privatchest.hasroom");
     }

    public RoomLocation getRoomlocation(Player player){
    	if(!plugin.Settings.ExchangeWorlds.containsKey(player.getWorld().getName())) return null;
        return plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.get(player.getName());
    }

    public RoomLocation getRoomlocationForWorld(Player player,World world){
    	if(!plugin.Settings.ExchangeWorlds.containsKey(world.getName())) return null;
        return plugin.Settings.ExchangeWorlds.get(world.getName()).ChestRooms.get(player.getName());
    }
    
    public boolean RoomExists(HashMap<String,RoomLocation> ChestRooms,RoomLocation RoomLocation){
    	for(String Player: ChestRooms.keySet()){
    		if(ChestRooms.get(Player).getX() == RoomLocation.getX() && ChestRooms.get(Player).getZ() == RoomLocation.getZ()){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void LoadRoom(Player player) {
    	if(Roomloaded.containsKey(player.getName()) && playerhasRoom(player)){
    		if(Roomloaded.get(player.getName())){return;}
    	}
    	if(!player.hasPermission("privatchest.hasroom")){
    		if(!MessageSend.contains(player.getName())){
    			plugin.log.info("[PrivatChest] Player not allowed to have Room.");
    			MessageSend.add(player.getName());
    		}
    		return;
    	}
    	//MixedModeAuth
    	if(player.getName().equalsIgnoreCase("Player_" + player.getEntityId())){
    		if(!MessageSend.contains(player.getName())){
    			plugin.log.info("[PrivatChest] Player is MixedModeAuth Temp-Player");
    			MessageSend.add(player.getName());
    		}
    		return;
    	}
    	//MixedModeAuth end
        if (!playerhasRoom(player)) {
            long UPCounter = 1;
            long UPCounterMax = 1;
            long UPCounterDir = 1;
            long x = 0;
            long z = 0;
            
            RoomLocation RoomLoaction = new RoomLocation(x, z);
            
            while (RoomExists(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms,RoomLoaction)) {
                switch ((int) UPCounterDir) {
                case 0:
                    x++;
                    UPCounter--;

                    break;

                case 1:
                    z--;
                    UPCounter--;

                    break;

                case 2:
                    x--;
                    UPCounter--;

                    break;

                case 3:
                    z++;
                    UPCounter--;

                    break;
                }

                if (UPCounter <= 0) {
                    UPCounterDir++;

                    if (UPCounterDir > 3) {
                        UPCounterDir = 0;
                    }

                    if ((UPCounterDir == 0) || (UPCounterDir == 2)) {
                        UPCounter = UPCounterMax;
                    } else {
                        UPCounterMax++;
                        UPCounter = UPCounterMax;
                    }
                }
                RoomLoaction = new RoomLocation(x, z);
            }
            
            RoomGenerator roomgen = new RoomGenerator(player, (int) x * 7, (int) z * 7,plugin,player.getWorld().getName());
            roomgen.generate();
            roomgen.Clearroom();
            plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.put(player.getName(), RoomLoaction);
            plugin.save();
            Roomloaded.put(player.getName(),true);
            plugin.log.info("[PrivatChest] Room Generated.");
        } else {
        	RoomLocation loc = plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.get(player.getName());
        	RoomGenerator roomgen = new RoomGenerator(player, (int) loc.getX()*7, (int) loc.getZ()*7,plugin,player.getWorld().getName());
        	roomgen.Clearroom();
        	roomgen.checkFloor();
            Roomloaded.put(player.getName(),true);
            plugin.log.info("[PrivatChest] Room Loaded.");
        }
    }
    
    public Location getPlayerLobbyLocation(Player player, World world){
    	Location loc = new Location(world, 6.5, 8, 6.5);
    	CheckItem Checker = new CheckItem(plugin, world.getName());
    	if(plugin.getSettings().ExchangeWorlds.containsKey(world.getName())){
	        if (plugin.getSettings().ExchangeWorlds.get(world.getName()).WorldType.equalsIgnoreCase("Mod")) {
	            loc = new Location(world, 6.5 + 16, 8, 6.5);
	        } else{
	            loc = new Location(world, 6.5, 8, 6.5);
	        }
	        return loc;
    	} else {
    		return null;
    	}
    }
    
    public Location playertospawn(Player player) {
        Location loc = new Location(player.getWorld(), 6.5, 8, 6.5);
        CheckItem Checker = new CheckItem(plugin, player.getWorld().getName());

        if (!Checker.NoModItem(player,false) &&
                plugin.RoomControl.playerhasRoom(player) &&
                player.hasPermission("privatchest.hasroom")) {
            loc.setX((plugin.RoomControl.getRoomlocation(player).getX() * 7) +
                3.5);
            loc.setY(2);
            loc.setZ((plugin.RoomControl.getRoomlocation(player).getZ() * 7) +
                3.5);
        } else {
            if (plugin.getSettings().ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Mod")) {
                loc = new Location(player.getWorld(), 6.5 + 16, 8, 6.5);
            } else {
                loc = new Location(player.getWorld(), 6.5, 8, 6.5);
            }
        }
        return loc;
    }
    
    public boolean Teleport(Player player, String Target) {
    	if(Target.contains("IN_")){
            if (!playerhasRoom(player) && player.hasPermission("privatchest.hasroom")) {
            	LoadRoom(player);
            }
    	}
        if (Target == "IN_NORMAL") {
            if (playerhasRoom(player)) {

            	RoomLocation Roomloc = plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.get(player.getName());
            	RoomGenerator roomgen = new RoomGenerator(player, (int) Roomloc.getX()*7, (int) Roomloc.getZ()*7,plugin,player.getWorld().getName());
            	roomgen.Clearroom();
            	

                Location TeleportLoc = new Location(player.getWorld(), 0, 2, 0,
                        270, 0);

                TeleportLoc.setX((Roomloc.getX() * 7) + 1.5);
                TeleportLoc.setZ((Roomloc.getZ() * 7) + 3.5);
                player.teleport(TeleportLoc);
                player.sendMessage("§2Teleportiert");
                player.sendMessage("You are now in your §1PrivatChest§f Room");

                return true;
            } else if(!player.hasPermission("privatchest.hasroom")){
                player.sendMessage("§4You don't have Permission to do that.");
            } else {
                player.sendMessage("§1Room Not Generated");
                return false;
            }
        } else if (Target == "OUT_NORMAL") {
        	if(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Mod")){
	        	CheckItem Checker = new CheckItem(plugin,player.getWorld().getName());
	        	if(Checker.NoModItem(player)){
		            Location TeleportLoc = new Location(player.getWorld(), 10.5, 8,
		                    6.5, 90, 0);
		            player.teleport(TeleportLoc);
		            player.sendMessage("§2Teleportiert");
		            player.sendMessage("You are now in the §1Normal Stargate§f Room");
		            return true;
	        	} else {
	        		return true;
	        	}
        	} else if(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Creative")){
	            Location TeleportLoc = new Location(player.getWorld(), 10.5, 8,
	                    6.5, 90, 0);
	            player.teleport(TeleportLoc);
	            player.sendMessage("§2Teleportiert");
	            player.sendMessage("You are now in the §1Normal Stargate§f Room");
	            return true;
        	} else {
	            Location TeleportLoc = new Location(player.getWorld(), 10.5, 8,
	                    6.5, 90, 0);
	            player.teleport(TeleportLoc);
	            player.sendMessage("§2Teleportiert");
	            player.sendMessage("You are now in the §1Normal Stargate§f Room");
	            player.sendMessage("§4Wrong World Type.");
        		return true;
        	}
        } else if (Target == "IN_MOD") {
            if (playerhasRoom(player)) {
            	if(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Creative")){
            	player.getInventory().clear();
            	boolean Armor = false;
            	Material Boots = player.getInventory().getBoots().getType();
            	if(Boots != Material.AIR){
            		Armor = true;
            	}
            	Material Chestplate = player.getInventory().getChestplate().getType();
            	if(Chestplate != Material.AIR ){
            		Armor = true;
            	}
            	Material Helmet = player.getInventory().getHelmet().getType();
            	if(Helmet != Material.AIR){
            		Armor = true;
            	}
            	Material Leggings = player.getInventory().getLeggings().getType();
            	if(Leggings != Material.AIR){
            		Armor = true;
            	}
            	if(Armor){
            		player.sendMessage("§4You are still wearing Armor.");
            		return false;
            	}
            	}
            	RoomLocation Roomloc = plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).ChestRooms.get(player.getName());
            	RoomGenerator roomgen = new RoomGenerator(player, (int) Roomloc.getX()*7, (int) Roomloc.getZ()*7,plugin,player.getWorld().getName());
            	roomgen.Clearroom();

                Location TeleportLoc = new Location(player.getWorld(), 0, 2, 0,
                        90, 0);

                TeleportLoc.setX((Roomloc.getX() * 7) + 5.5);
                TeleportLoc.setZ((Roomloc.getZ() * 7) + 3.5);
                player.teleport(TeleportLoc);
                player.sendMessage("§2Teleportiert");
                player.sendMessage("You are now in your §1PrivatChest§f Room");

                return true;
            } else {
                player.sendMessage("§1Room Not Generated");

                return false;
            }
        } else if (Target == "OUT_MOD") {
        	if(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Mod")){
        		if(!ForgeLoginHooks.isPlayerConfirmed(player)){
        			plugin.PlayerModPacketListener.TeleportPlayer.add(player.getName());
        			if(plugin.debug())plugin.log.info("Packet 230: "+player.getName()+": RoomControler");
        			plugin.sendModLoaderPacket(player);
        		} else {
	    	        Location TeleportLoc = new Location(player.getWorld(), 26.5, 8,
	                    6.5, 90, 0);
	
	    	        player.teleport(TeleportLoc);
	    	        player.sendMessage("§2Teleportiert");
	    	        player.sendMessage("You are now in the §1Mod Stargate§f Room");
        		}
    	        return true;
        	} else if(plugin.Settings.ExchangeWorlds.get(player.getWorld().getName()).WorldType.equalsIgnoreCase("Creative")){
	        	CheckItem Checker = new CheckItem(plugin,player.getWorld().getName());
	        	if(Checker.NoItem(player)){
	        		Location TeleportLoc = new Location(player.getWorld(), 26.5, 8,
                        6.5, 90, 0);
        	        player.teleport(TeleportLoc);
        	        player.sendMessage("§2Teleportiert");
        	        player.sendMessage("You are now in the §1Creative Stargate§f Room");
        	        return true;
	        	} else {
	        		return false;
	        	}
        	} else {
    	        Location TeleportLoc = new Location(player.getWorld(), 26.5, 8,
                        6.5, 90, 0);

        	        player.teleport(TeleportLoc);
        	        player.sendMessage("§2Teleportiert");
        	        player.sendMessage("You are now in the §1??? Stargate§f Room");
    	            player.sendMessage("§4Wrong World Type.");
        	        return true;
        	}
        }
        return false;
    }
}
