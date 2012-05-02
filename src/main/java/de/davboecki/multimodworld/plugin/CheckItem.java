package de.davboecki.multimodworld.plugin;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;

import de.davboecki.multimodworld.plugin.settings.Settings;

public class CheckItem {
	PrivatChest plugin;
	String WorldName;
	boolean ExchangeWorld = true;

    public CheckItem(PrivatChest pplugin,String pWorldName) {
    	plugin = pplugin;
    	WorldName =  pWorldName;
    	ExchangeWorld = true;
    }
    
    public CheckItem(PrivatChest pplugin,String pWorldName, boolean ExchangeWorld) {
    	plugin = pplugin;
    	WorldName =  pWorldName;
    	this.ExchangeWorld = ExchangeWorld;
    }
    
    public boolean NoItem(Player player) {
    	return NoItem(player, true);
    }
    
    private String GetNameOutOfPath(String Name){
    	if(Name.indexOf(".") <= 0){
    		return Name;
    	}
    	String Result = "";
    	ArrayList<String> Parts = new ArrayList<String>();
    	String tmp = Name;
    	String partstring = "";
    	while(!tmp.isEmpty()){
    		char local = tmp.charAt(0);
    		tmp = tmp.substring(1);
    		if(local == '.'){
    			Parts.add(partstring);
    			partstring = "";
    		} else {
    			partstring += local;
    		}
    	}
		Parts.add(partstring);
    	Object[] NameList = Parts.toArray();
    	int count = NameList.length;
    	Result = (String)NameList[count-1];
    	if(count > 1 && Result.equalsIgnoreCase("name")){
        	Result = (String)NameList[count-2];
        	Result = Result.toUpperCase().substring(0, 1) + Result.substring(1);
    	} else {
    		Result = Name;
    	}
    	return Result;
    }
    
    private void Message(Player player,ItemStack item){
    	if(item.getType().toString().equalsIgnoreCase("MODLOADERMP_ITEM")){
    		if(net.minecraft.server.Item.byId.length > item.getTypeId() && net.minecraft.server.Item.byId[item.getTypeId()] != null){
    			String Name = net.minecraft.server.Item.byId[item.getTypeId()].l();
    			if(Name.indexOf(".") > 0){
    				if(net.minecraft.server.Block.byId.length > item.getTypeId() && net.minecraft.server.Block.byId[item.getTypeId()] != null){
    		    		Name = net.minecraft.server.Block.byId[item.getTypeId()].getName();
    				}
    				if(Name.indexOf(".") > 0){
    					Name = GetNameOutOfPath(Name);
    				}
    			}
    	    	if(Name.equalsIgnoreCase("MODLOADERMP_ITEM") || Name.equalsIgnoreCase("null")){
        			player.sendMessage(ChatColor.DARK_BLUE+"Not Allowed "+ChatColor.DARK_RED+"Moded "+ChatColor.DARK_BLUE+"Item");
    	    	} else {
    	    		player.sendMessage(ChatColor.DARK_BLUE+"Not Allowed Item:"+ChatColor.DARK_RED+" " + Name);
    	    	}
    		} else {
    			player.sendMessage(ChatColor.DARK_BLUE+"Not Allowed "+ChatColor.DARK_RED+"Moded "+ChatColor.DARK_BLUE+"Item");
    		}
    	} else {
    		player.sendMessage(ChatColor.DARK_BLUE+"Not Allowed Item:"+ChatColor.DARK_RED+" " + item.getType().toString());
    	}
    }
    
    public boolean NoItem(Player player,boolean Ausgabe) {
        boolean NoItem = true;

        for (int i = 0; i < 36; i++) {
            boolean tmp = player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR;
            NoItem &= tmp;

            if (!tmp && Ausgabe) {
            	Message(player, player.getInventory().getItem(i));
            }
        }
        //Helm
        boolean tmp;
        tmp = player.getInventory().getHelmet() == null || player.getInventory().getHelmet().getType() == Material.AIR;
        NoItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getHelmet());
        }
        //Torso
        tmp = player.getInventory().getChestplate() == null || player.getInventory().getChestplate().getType() == Material.AIR;
        NoItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getChestplate());
        }
        //Beine
        tmp = player.getInventory().getLeggings() == null || player.getInventory().getLeggings().getType() == Material.AIR;
        NoItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getLeggings());
        }
        //Legs
        tmp = player.getInventory().getBoots() == null || player.getInventory().getBoots().getType() == Material.AIR;
        NoItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getBoots());
        }
        return NoItem;
    }
    
    public boolean NoModItem(Player player) {
    	return NoModItem(player,true);
    }
    public boolean NoModItem(Player player,boolean Ausgabe) {
    	boolean NoModItem = true;

        for (int i = 0; i < 36; i++) {
            boolean tmp = CheckaItem(player.getInventory().getItem(i));
            NoModItem &= tmp;

            if (!tmp && Ausgabe) {
            	Message(player, player.getInventory().getItem(i));
            }
        }
        //Helm
        boolean tmp;
        tmp = CheckaItem(player.getInventory().getHelmet());
        NoModItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getHelmet());
        }
        //Torso
        tmp = CheckaItem(player.getInventory().getChestplate());
        NoModItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getChestplate());
        }
        //Beine
        tmp = CheckaItem(player.getInventory().getLeggings());
        NoModItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getLeggings());
        }
        //Legs
        tmp = CheckaItem(player.getInventory().getBoots());
        NoModItem &= tmp;

        if (!tmp && Ausgabe) {
        	Message(player, player.getInventory().getBoots());
        }
        return NoModItem;
    }

    public boolean CheckaItem(ItemStack Item) {
    	return Item == null || (ExchangeWorld ? Settings.getItemListExchangeWorld(WorldName).contains(Item.getTypeId()) : Settings.getItemList(WorldName).contains(Item.getTypeId())) || Item.getTypeId() == 0 || Item.getType() == Material.AIR;
    }
}
