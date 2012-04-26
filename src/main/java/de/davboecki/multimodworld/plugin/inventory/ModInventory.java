package de.davboecki.multimodworld.plugin.inventory;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

public class ModInventory {

    public ItemStack items[];
    public ItemStack armor[];
    private EntityPlayer player;
    
	ModInventory(EntityPlayer player) {
		items = new ItemStack[player.inventory.items.length];
    	armor = new ItemStack[player.inventory.armor.length];
    	this.player = player;
	}
	
	public boolean fromPlayer(EntityPlayer ePlayer) {
		return ePlayer.getBukkitEntity().getName().equalsIgnoreCase(player.getBukkitEntity().getName());
	}
	
	public void givePlayerItemsBack() {
		for(int i=0;i<armor.length;i++) {
			if(armor[i] == null) continue;
			PlayerInventory inventory = player.inventory;
			if(inventory.armor[i] == null) {
				inventory.armor[i] = armor[i];
				armor[i] = null;
			} else {
				for(int j=0;j<items.length;j++) {
					if(items[j] == null) {
						items[j] = armor[i];
						armor[i] = null;
						break;
					}
				}
			}
		}
		for(int i=0;i<items.length;i++) {
			if(items[i] == null) continue;
			PlayerInventory inventory = player.inventory;
			if(inventory.items[i] == null) {
				inventory.items[i] = items[i];
				items[i] = null;
			} else {
				for(int j=0;j<inventory.items.length;j++) {
					if(inventory.items[j] == null) {
						inventory.items[j] = items[i];
						items[i] = null;
						break;
					}
				}
			}
		}
	}
	
	public boolean isEmpty() {
		for(int i=0;i<armor.length;i++) {
			if(armor[i] != null) {
				return false;
			}
		}
		for(int i=0;i<items.length;i++) {
			if(items[i] != null) {
				return false;
			}
		}
		return true;
	}
}
