package de.davboecki.multimodworld.plugin.inventory;

import java.util.ArrayList;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemStack;
import net.minecraft.server.PlayerInventory;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.davboecki.multimodworld.plugin.ItemCheckHandler;
import de.davboecki.multimodworld.plugin.PrivatChest;

public class ModItemSaver {
	private PrivatChest plugin;
	private ArrayList<ModInventory> ModInventoryList = new ArrayList<ModInventory>();
	
	public ModItemSaver(PrivatChest instance){
		plugin = instance;
	}
	
	public void returnModItems(Player player) {
		EntityPlayer ePlayer = ((CraftPlayer)player).getHandle();
		ModInventory toRemove = null;
		for(Object invObject: ModInventoryList.toArray()) {
			if(!(invObject instanceof ModInventory)) continue;
			ModInventory inv = (ModInventory)invObject;
			if(inv.fromPlayer(ePlayer)){
				inv.givePlayerItemsBack();
				if(inv.isEmpty()){
					toRemove = inv;
				}
			}
		}
		if(toRemove != null) {
			ArrayList<ModInventory> newModInventoryList = new ArrayList<ModInventory>();
			for(Object invObject: ModInventoryList.toArray()) {
				if(!(invObject instanceof ModInventory)) continue;
				ModInventory inv = (ModInventory)invObject;
				if(toRemove != inv) {
					newModInventoryList.add(inv);
				}
			}
			ModInventoryList = newModInventoryList;
		}
	}
	
	public boolean exportModItems(Player player) {
		EntityPlayer ePlayer = ((CraftPlayer)player).getHandle();
		PlayerInventory pInventory = ePlayer.inventory;
		boolean ChangedInventory = false;
		
		ModInventory inv = new ModInventory(ePlayer);
		
		for(int i = 0;i < pInventory.armor.length;i++){
			ItemStack item = pInventory.armor[i];
			if(item == null) continue;
			if(ItemCheckHandler.isModItem(item.id)) {
				inv.armor[i] = pInventory.armor[i];
				pInventory.armor[i] = null;
				ChangedInventory = true;
			}
		}
		for(int i = 0;i < pInventory.items.length;i++){
			ItemStack item = pInventory.items[i];
			if(item == null) continue;
			if(ItemCheckHandler.isModItem(item.id)) {
				inv.items[i] = pInventory.items[i];
				pInventory.items[i] = null;
				ChangedInventory = true;
			}
		}
		if(ChangedInventory) {
			ModInventoryList.add(inv);
		}
        return ChangedInventory;
	}

	public void save() {
		//TODO: Coming Soon
	}
	
	public void load() {
		//TODO: Coming Soon
	}
}
