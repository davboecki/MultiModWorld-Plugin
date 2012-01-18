package de.davboecki.multimodworld.plugin.listener;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ModLoaderMp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
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
import de.davboecki.multimodworld.plugin.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrivatChestPlayerListener extends PlayerListener {
    public static PrivatChest plugin;
    
    private boolean isLocalTeleport=false;
    
    private static Map<String, Boolean> Teleport = new HashMap<String, Boolean>();
    private static Map<String, Double> Timer = new HashMap<String, Double>();
    private HashMap<String,CallableObjects> MoveTasks = new HashMap<String,CallableObjects>();
    
    public PrivatChestPlayerListener(PrivatChest instance) {
        plugin = instance;
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	plugin.teleporthandler.onPlayerTeleport(event);
    }

    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(plugin.ModPacketOK.contains(player.getName())){
        	ArrayList<String> newModPackOK = new ArrayList<String>();
        	Iterator ModPackOKIterator = plugin.ModPacketOK.iterator();
        	while(ModPackOKIterator.hasNext()) {
        		String PlayerName = (String)ModPackOKIterator.next();
        		if(!PlayerName.equalsIgnoreCase(player.getName())){
        			newModPackOK.add(PlayerName);
        		}
        	}
        	plugin.ModPacketOK = newModPackOK;
        }
    }
    
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

    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	plugin.teleporthandler.onPlayerRespawn(event);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location playerLoc = null;
        if(world.getGenerator() == plugin.Worldgen) {
        	plugin.RoomControl.LoadRoom(player);
        	playerLoc = plugin.RoomControl.playertospawn(player);
        }
        boolean SendPacket = false;
        ArrayList<ItemStack> ModItems = new ArrayList<ItemStack>();
        for(int i=0;i < player.getInventory().getSize();i++){
        	ItemStack Item = player.getInventory().getItem(i);
        	if(Item != null && ItemCheckHandler.isModItem(Item.getTypeId())){
        		ModItems.add(Item);
            	player.getInventory().clear(i);
            	SendPacket = true;
        	}
        }
        if(player.getInventory().getHelmet() != null && ItemCheckHandler.isModItem(player.getInventory().getHelmet().getTypeId())){
    		plugin.PlayerModPacketListener.ModInventoryHelmet.put(player.getName(),player.getInventory().getHelmet());
    		player.getInventory().setHelmet(null);
    		SendPacket = true;
        }
        if(player.getInventory().getChestplate() != null && ItemCheckHandler.isModItem(player.getInventory().getChestplate().getTypeId())){
        	plugin.PlayerModPacketListener.ModInventoryChestplate.put(player.getName(),player.getInventory().getChestplate());
    		player.getInventory().setChestplate(null);
    		SendPacket = true;
        }
        if(player.getInventory().getLeggings() != null && ItemCheckHandler.isModItem(player.getInventory().getLeggings().getTypeId())){
        	plugin.PlayerModPacketListener.ModInventoryLeggings.put(player.getName(),player.getInventory().getLeggings());
    		player.getInventory().setLeggings(null);
    		SendPacket = true;
        }
        if(player.getInventory().getBoots() != null && ItemCheckHandler.isModItem(player.getInventory().getBoots().getTypeId())){
        	plugin.PlayerModPacketListener.ModInventoryBoots.put(player.getName(),player.getInventory().getBoots());
    		player.getInventory().setBoots(null);
    		SendPacket = true;
        }
        if(SendPacket) {
        	plugin.PlayerModPacketListener.ModInventory.put(player.getName(), ModItems);
			plugin.log.info("Packet 230: "+event.getPlayer().getName()+": ModInventory");
        	ModLoaderMp.HandleAllLogins(((CraftPlayer) player).getHandle(),"ModInventory");
        }
        if(playerLoc != null) {
            player.teleport(playerLoc);
	    	ReTeleportThread.add(10,player,playerLoc);
        } else {
        	plugin.teleporthandler.HandleJoin(event);
        }
    }

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
                    if ((Timer.get(player.getName()) + 10) < world.getTime()) {
                        Teleport.put(player.getName(), true);
                    }
                }

                if (plugin.getSettings().ExchangeWorlds.get(player.getWorld().getName()).WorldType == "Mod") {
                    if (!new CheckItem(plugin,event.getPlayer().getWorld().getName()).CheckaItem(player.getItemInHand()) &&plugin.PlayerPositionCheck.PlayerinLobby(player)) {
                        Location playerSpawn = plugin.RoomControl.playertospawn(player);
                        playerSpawn.setX((plugin.RoomControl.getRoomlocation(player).getX() * 7) + 3.5);
                        playerSpawn.setY(2);
                        playerSpawn.setZ((plugin.RoomControl.getRoomlocation(player).getZ() * 7) + 3.5);
                        player.teleport(playerSpawn);
                    }
                }
            }
        }
    }
    
    private void handleMoveTaskCall(PlayerMoveEvent event) {
    	if(event.getFrom().getX()!=event.getTo().getX() || event.getFrom().getZ()!=event.getTo().getZ()) {
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
		    							event.setCancelled(true);
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

	public void onPlayerInteract(PlayerInteractEvent event){
    	if(event.getPlayer().getWorld().getGenerator() != plugin.Worldgen) return;
    	if(!plugin.PlayerPositionCheck.PlayerinRoom(event.getPlayer())) return;
    	if(plugin.ModPacketOK.contains(event.getPlayer().getName())) return;
    	if(event.getClickedBlock() == null) return;
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
    			plugin.log.info("Packet 230: "+event.getPlayer().getName()+": ModChest");
    			ModLoaderMp.HandleAllLogins(((CraftPlayer) event.getPlayer()).getHandle(),"ModChest");
    			event.setCancelled(true);
    			plugin.PlayerModPacketListener.CallableAction.put(event.getPlayer().getName(), new CallableObjects(new Object[]{((CraftWorld)event.getPlayer().getWorld()).getHandle(),event.getClickedBlock().getX(),event.getClickedBlock().getY(),event.getClickedBlock().getZ(),((CraftPlayer) event.getPlayer()).getHandle()},plugin){
					@Override
					public Object call() throws Exception {
						net.minecraft.server.Block.CHEST.interact((net.minecraft.server.World)args[0], (Integer)args[1], (Integer)args[2], (Integer)args[3], (EntityHuman)args[4]);
						return null;
					}
    			});
    		}
    	}
    }
}
