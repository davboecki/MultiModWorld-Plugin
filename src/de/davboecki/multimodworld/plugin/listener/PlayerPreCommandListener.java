package de.davboecki.multimodworld.plugin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;

import de.davboecki.multimodworld.plugin.ItemCheckHandler;
import de.davboecki.multimodworld.plugin.PrivatChest;

public class PlayerPreCommandListener extends PlayerListener {
	
	private PrivatChest plugin;
	public PlayerPreCommandListener(PrivatChest instance){
		plugin = instance;
	}

    @EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		String Message = event.getMessage();
		if(Message.indexOf(" ") < 1) return;
		String Command = Message.substring(1,Message.indexOf(" "));
		String Arg = Message.substring(Message.indexOf(" ")+1);
		String Backslash = Message.substring(0,1);
		if(!Backslash.equals("/"))return;
		if(Command.equalsIgnoreCase("i") || Command.equalsIgnoreCase("item")){
			String itemString;
			if(Arg.indexOf(":") < 0){
				itemString = Arg;
			} else {
				itemString = Arg.substring(0,Arg.indexOf(":"));
			}
			int item = 0;
			try {
				item = Integer.valueOf(itemString);
			}catch(Exception e){return;}
			if(!ItemCheckHandler.isItemAllowed(event.getPlayer().getWorld().getName(), item)) {
				event.setCancelled(true);
			}
		} else if(Command.equalsIgnoreCase("give")) {
			if(Arg.indexOf(" ") < 0)return;
			String PlayerString = Arg.substring(0,Arg.indexOf(" "));
			String itemString = Arg.substring(Arg.indexOf(" ") + 1);
			if(itemString.indexOf(" ") > 0){
				itemString = itemString.substring(0,itemString.indexOf(" "));
			}
			int item = 0;
			try {
				item = Integer.valueOf(itemString);
			}catch(Exception e){return;}
			try {
				Player player = plugin.getServer().getPlayer(PlayerString);
				if(!ItemCheckHandler.isItemAllowed(player.getWorld().getName(), item)) {
					event.setCancelled(true);
				}
			}catch(Exception e){return;}
		}
	}
}
