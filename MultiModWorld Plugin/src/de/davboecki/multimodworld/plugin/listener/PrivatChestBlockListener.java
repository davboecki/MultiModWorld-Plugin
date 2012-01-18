package de.davboecki.multimodworld.plugin.listener;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.block.Block;

import org.bukkit.entity.Player;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.RoomGenerator;


public class PrivatChestBlockListener extends BlockListener {
    public static PrivatChest plugin;

    public PrivatChestBlockListener(PrivatChest pplugin) {
        plugin = pplugin;
    }

    private boolean checksignPos(Location loc, Player player) {
        if (loc.getY() != 3) {
            return false;
        }

        if ((loc.getX() != (plugin.RoomControl.getRoomlocation(player).getX() +
                1)) &&
                (loc.getX() != (plugin.RoomControl.getRoomlocation(player).getX() +
                3)) &&
                (loc.getX() != (plugin.RoomControl.getRoomlocation(player).getX() +
                5))) {
            return false;
        }

        if ((loc.getZ() != (plugin.RoomControl.getRoomlocation(player).getZ() +
                1)) &&
                (loc.getZ() != (plugin.RoomControl.getRoomlocation(player).getZ() +
                5))) {
            return false;
        }

        return true;
    }

    
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(event.getBlock().getWorld().getGenerator() != plugin.Worldgen) return;
    	event.setCancelled(true);
        Player player = event.getPlayer();
        if(player.hasPermission("privatchest.admin")){
        	event.setCancelled(false);
        	return;
        }
        Block block = event.getBlock();
        Material mat = block.getType();
        if ((mat.getId() == Material.WALL_SIGN.getId()) &&
                checksignPos(block.getLocation(), player)) {
        	event.setCancelled(false);
        } else {
        	player.sendMessage("You are only allowed to place/break signs above your chests.");
        }
    }

    public void onBlockBreak(BlockBreakEvent event) {
    	if(event.getBlock().getWorld().getGenerator() != plugin.Worldgen) return;
        Player player = event.getPlayer();
        if(player.hasPermission("privatchest.admin")){
        	return;
        }
        Block block = event.getBlock();
        Material mat = block.getType();
        event.setCancelled(true);
        if ((mat.getId() == Material.WALL_SIGN.getId()) &&
                checksignPos(block.getLocation(), player));
        else if(plugin.RoomControl== null){
        	player.sendMessage("You are only allowed to place/break signs above your chests.");
        	return;
        }
        else if(plugin.RoomControl.getRoomlocation(player)== null){
        	player.sendMessage("You are only allowed to place/break signs above your chests.");
        	return;
        }
        else if(new RoomGenerator(player, (int) plugin.RoomControl.getRoomlocation(player).getX()*7, (int) plugin.RoomControl.getRoomlocation(player).getZ()*7,plugin,player.getWorld().getName()).BlockDestroyable(block.getLocation())){
        	event.setCancelled(false);
        }
        else {
        	event.setCancelled(true);
        	player.sendMessage("You are only allowed to place/break signs above your chests.");
        }
    }
}
