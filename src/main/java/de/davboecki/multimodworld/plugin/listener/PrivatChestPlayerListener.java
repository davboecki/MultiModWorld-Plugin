package de.davboecki.multimodworld.plugin.listener;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.CheckItem;
import de.davboecki.multimodworld.plugin.ItemCheckHandler;
import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.commandhandler.CallableObjects;
import de.davboecki.multimodworld.plugin.reteleport.ReTeleportThread;
import de.davboecki.multimodworld.plugin.reteleport.TeleportHandler;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.api.ForgeLoginHooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrivatChestPlayerListener implements Listener {
    public static PrivatChest plugin;
    
    private static Map<String, Boolean> Teleport = new HashMap<String, Boolean>();
    private static Map<String, Double> Timer = new HashMap<String, Double>();
    private HashMap<String,CallableObjects> MoveTasks = new HashMap<String,CallableObjects>();
    
    public PrivatChestPlayerListener(PrivatChest instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	plugin.teleporthandler.onPlayerTeleport(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        ForgeLoginHooks.removePlayer(player);
		ForgeLoginHooks.removeSended(player);
		if(plugin.confirmlistener.ExistTaskFor(player.getName())) {
			plugin.confirmlistener.removeTask(player.getName());
		}
    }
    
    //Disabled Because of the new Packet Checking
/*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if(!ItemCheckHandler.isItemAllowed(event.getPlayer().getWorld().getName(), event.getPlayer().getInventory().getItem(event.getNewSlot()).getTypeId())  && !plugin.PlayerPositionCheck.PlayerinRoom(event.getPlayer())) {
        	ItemStack Old = event.getPlayer().getInventory().getItem(event.getNewSlot());
        	if(event.getPlayer().getInventory().getItem(event.getPreviousSlot()).getTypeId() == 0){
        		event.getPlayer().getInventory().clear(event.getNewSlot());
        	} else if(!ItemCheckHandler.isItemAllowed(event.getPlayer().getWorld().getName(), event.getPlayer().getInventory().getItem(event.getPreviousSlot()).getTypeId())){
        		ItemStack Move = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        		event.getPlayer().getInventory().setItem(event.getNewSlot(),new ItemStack(1,1));
        		event.getPlayer().getInventory().addItem(Move);
        		event.getPlayer().getInventory().clear(event.getNewSlot());
        	} else {
        		event.getPlayer().getInventory().setItem(event.getNewSlot(), event.getPlayer().getInventory().getItem(event.getPreviousSlot()));
        	}
        	event.getPlayer().getInventory().setItem(event.getPreviousSlot(), Old);
        }
    }
*/
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	plugin.teleporthandler.onPlayerRespawn(event);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location playerLoc = null;
        if(world.getGenerator() == plugin.Worldgen) {
        	plugin.RoomControl.LoadRoom(player);
        	playerLoc = plugin.RoomControl.playertospawn(player);
        }
        
        
        if(!ForgeLoginHooks.isPlayerConfirmed(player)) {
        	plugin.ModItemSaver.exportModItems(player);
        }
        
        plugin.teleporthandler.HandleJoin(event);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
    	
    	handleMoveTaskCall(event);
    	
    	if(event.isCancelled()) return;
    	
    	Player player = event.getPlayer();
        World world = player.getWorld();

        if (!Teleport.containsKey(player.getName())) {
            Teleport.put(player.getName(), true);
        }

        if (!Timer.containsKey(player.getName())) {
            Timer.put(player.getName(), (double) 0);
        }

        if (world.getGenerator() == plugin.Worldgen) {
        	plugin.RoomControl.LoadRoom(player);

            Location playerLoc = player.getLocation();

            if ((Math.floor(playerLoc.getX()) == 10) &&
                    (Math.floor(playerLoc.getY()) == 8) &&
                    (Math.floor(playerLoc.getZ()) == 6)) {
                if (Teleport.get(player.getName())) {
                    if (plugin.RoomControl.Teleport(player, "IN_NORMAL")) {
                        Teleport.put(player.getName(), false);
                        Timer.put(player.getName(), (double) world.getTime());
                    }
                }
            } else if ((Math.floor(playerLoc.getX()) == 26) &&
                    (Math.floor(playerLoc.getY()) == 8) &&
                    (Math.floor(playerLoc.getZ()) == 6)) {
                if (Teleport.get(player.getName())) {
                    if (plugin.RoomControl.Teleport(player, "IN_MOD")) {
                        Teleport.put(player.getName(), false);
                        Timer.put(player.getName(), (double) world.getTime());
                    }
                }
            } else if (plugin.RoomControl.playerhasRoom(player) &&
                    (Math.floor(playerLoc.getX()) == ((plugin.RoomControl.getRoomlocation(
                        player).getX() * 7) + 1)) &&
                    (Math.floor(playerLoc.getY()) == 2) &&
                    (Math.floor(playerLoc.getZ()) == ((plugin.RoomControl.getRoomlocation(
                        player).getZ() * 7) + 3))) {
                if (Teleport.get(player.getName())) {
                    if (plugin.RoomControl.Teleport(player, "OUT_NORMAL")) {
                        Teleport.put(player.getName(), false);
                        Timer.put(player.getName(), (double) world.getTime());
                    }
                }
            } else if (plugin.RoomControl.playerhasRoom(player) &&
                    (Math.floor(playerLoc.getX()) == ((plugin.RoomControl.getRoomlocation(
                        player).getX() * 7) + 5)) &&
                    (Math.floor(playerLoc.getY()) == 2) &&
                    (Math.floor(playerLoc.getZ()) == ((plugin.RoomControl.getRoomlocation(
                        player).getZ() * 7) + 3))) {
                if (Teleport.get(player.getName())) {
                    if (plugin.RoomControl.Teleport(player, "OUT_MOD")) {
                        Teleport.put(player.getName(), false);
                        Timer.put(player.getName(), (double) world.getTime());
                    }
                }
            } else if (Math.floor(playerLoc.getY()) < -1) {
                Location playerSpawn = plugin.RoomControl.playertospawn(player);
                player.teleport(playerSpawn);
                player.sendMessage(
                    "You were not supposed to be there. (Underground)");
            } else if ((player.getWorld()
                                  .getBlockAt(new Location(world,
                            player.getLocation().getX(),
                            player.getLocation().getY() - 1,
                            player.getLocation().getZ())).getType() == Material.BEDROCK) ||
                    (player.getWorld()
                               .getBlockAt(new Location(world,
                            player.getLocation().getX(),
                            player.getLocation().getY() - 1,
                            player.getLocation().getZ())).getType() == Material.STONE)) {
                Location playerSpawn = plugin.RoomControl.playertospawn(player);
                player.teleport(playerSpawn);
                player.sendMessage("You were not supposed to be there. (On wrong Material)");
            } else if (!plugin.PlayerPositionCheck.PlayerinLobby(player) &&
                    !plugin.PlayerPositionCheck.PlayerinRoom(player)) {
                Location playerSpawn = plugin.RoomControl.playertospawn(player);
                player.teleport(playerSpawn);
                player.sendMessage("You were not supposed to be there. (Position Check)");
            } else {
                if (!Teleport.get(player.getName())) {
                    if ((Timer.get(player.getName()) + 2) < world.getTime()) {
                        Teleport.put(player.getName(), true);
                    }
                }

                /*
                if (plugin.getSettings().ExchangeWorlds.get(player.getWorld().getName()).WorldType == "Mod") {
                    if (!new CheckItem(plugin,event.getPlayer().getWorld().getName()).CheckaItem(player.getItemInHand()) && plugin.PlayerPositionCheck.PlayerinLobby(player)) {
                        Location playerSpawn = plugin.RoomControl.playertospawn(player);
                        playerSpawn.setX((plugin.RoomControl.getRoomlocation(player).getX() * 7) + 3.5);
                        playerSpawn.setY(2);
                        playerSpawn.setZ((plugin.RoomControl.getRoomlocation(player).getZ() * 7) + 3.5);
                        player.teleport(playerSpawn);
                    }
                }
                */
            }
        }
    }
    
    private void handleMoveTaskCall(PlayerMoveEvent event) {
       	if(event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ()!=event.getTo().getZ()) {
			if(MoveTasks.size() > 0) {
	    		for(String PlayerName:MoveTasks.keySet()) {
	    				try {
	    					if(PlayerName.equalsIgnoreCase(event.getPlayer().getName())){
	        					CallableObjects CallObject = MoveTasks.get(PlayerName);
	    	    				Object flagobject = CallObject.call();
	    						if(flagobject instanceof Boolean){
		    						boolean flag = (Boolean)flagobject;
		    						if(flag) {
		    							MoveTasks.remove(PlayerName);
		    						} else {
		    							//event.getPlayer().teleport(event.getFrom());
		    						}
		    					}
	    					}
	    				} catch(Exception e){
	    					e.printStackTrace();
	    				}
	    		}
	    		//MoveTasks.clear();
	    	}
    	}
	}
    
    public boolean addMoveTask(String name, CallableObjects CallableObject){
    	if(MoveTasks.containsKey(name)) return false;
    	MoveTasks.put(name, CallableObject);
    	return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event){
    	if(event.getClickedBlock() == null) return;
    	if(event.getPlayer().getWorld().getGenerator() != plugin.Worldgen) return;
    	if(ForgeLoginHooks.isPlayerConfirmed(event.getPlayer())) return;
    	if(!plugin.PlayerPositionCheck.PlayerinRoom(event.getPlayer())) return;
    	if(event.getClickedBlock().getTypeId() == Block.CHEST.id) {
    		boolean ModItems = false;
    		Chest chest = (Chest)event.getClickedBlock().getState();
    		Inventory inventory = chest.getInventory();
    		for(int i = 0; i < inventory.getSize(); i++){
    			int id = inventory.getItem(i).getTypeId();
    			if(!ItemCheckHandler.isItemAllowed(event.getPlayer().getWorld().getName(), id)){
    				ModItems = true;
    			}
    		}
    		if(ModItems){
    			event.setCancelled(true);
    		}
    	}
    }
}
