package de.davboecki.multimodworld.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.ItemCheckHandler;
import de.davboecki.multimodworld.plugin.PrivatChest;

public class FurnaceListener implements Listener {
	PrivatChest plugin;
	
	public FurnaceListener(PrivatChest plugin){
		this.plugin = plugin;
	}

    @EventHandler(priority = EventPriority.HIGHEST)
	public void onFurnaceSmelt(FurnaceSmeltEvent event) {
		ItemStack Result = event.getResult();
		if(Result != null){
			  int id = Result.getTypeId();
			  if(!ItemCheckHandler.isItemAllowed(event.getFurnace().getWorld().getName(), id)){
				  //event.setResult(new ItemStack(0,0));
				  //event.setResult(null);
				  event.setCancelled(true);
			  }
		  }
	}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
		ItemStack Fuel = event.getFuel();
		if(Fuel != null){
			  int id = Fuel.getTypeId();
			  if(!ItemCheckHandler.isItemAllowed(event.getFurnace().getWorld().getName(), id)){
				  event.setCancelled(true);
			  }
		  }
    }
}
