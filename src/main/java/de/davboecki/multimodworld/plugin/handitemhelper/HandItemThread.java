package de.davboecki.multimodworld.plugin.handitemhelper;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.ItemCheckHandler;
import de.davboecki.multimodworld.plugin.PrivatChest;

public class HandItemThread implements Runnable{
	
	PrivatChest plugin;
	public HandItemThread(PrivatChest instance){
		this.plugin = instance;
	}
	
	public void run() {
		for(Player player : plugin.getServer().getOnlinePlayers()){
			//if(player.getGameMode().equals(GameMode.CREATIVE)){
				if(!ItemCheckHandler.isItemAllowed(player.getWorld().getName(), player.getItemInHand().getTypeId()) && !plugin.PlayerPositionCheck.PlayerinRoom(player)) {
					ItemStack Move = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
	        		player.getInventory().setItemInHand(new ItemStack(1,1));
					player.getInventory().addItem(Move);
					player.getInventory().clear(player.getInventory().getHeldItemSlot());
				}
				ItemStack[] Armor = player.getInventory().getArmorContents();
				boolean changed = false;
				for(int i=0;i<player.getInventory().getArmorContents().length;i++){
					if(!ItemCheckHandler.isItemAllowed(player.getWorld().getName(), Armor[i].getTypeId()) && !plugin.PlayerPositionCheck.PlayerinRoom(player)) {
						player.getInventory().addItem(Armor[i]);
						Armor[i] = null;
						changed = true;
					}
				}
				if(changed) {
					player.getInventory().setArmorContents(Armor);
				}
			//}
		}
	}

}
