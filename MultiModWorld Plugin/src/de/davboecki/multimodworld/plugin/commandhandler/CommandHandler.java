package de.davboecki.multimodworld.plugin.commandhandler;

import java.awt.Color;
import java.util.concurrent.Callable;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.davboecki.multimodworld.plugin.PrivatChest;
import de.davboecki.multimodworld.plugin.settings.Default;
import de.davboecki.multimodworld.plugin.settings.ExchangeWorldSetting;
import de.davboecki.multimodworld.plugin.settings.WorldSetting;

public class CommandHandler {
	
	PrivatChest plugin;
	public CommandHandler(PrivatChest instance){
		plugin = instance;
	}
	
	private boolean IsCommand(String[] args,int i,String command){
		return IsCommand(args,i,new String[]{command});
	}
	private boolean IsCommand(String[] args,int i,String[] commands){
				if(args.length < i) return false;
		for(String command:commands){
			if(args[i].equalsIgnoreCase(command))return true;
		}
		return false;
	}
	
    public boolean HandleMMWCommand(CommandSender sender, String[] args){
    	String name = sender instanceof Player ? ((CraftPlayer)sender).getName() : "[Console]";
    	if(args.length >= 1) {
    		if(IsCommand(args,0,new String[]{"worldsetting","ws","worldsetting/ws"})) {
    			return HandleWSCommand(sender,args);
    		} else if(IsCommand(args,0,new String[]{"exchangeworld","ew","exchangeworld/ew"})){
    			return HandleEWCommand(sender,args);
    		} else if(IsCommand(args,0,"known")) {
    			return HandleKnownCommand(sender,args,name);
    		} else if(IsCommand(args,0,"list")) {
    			return HandleListCommand(sender,args,name);
    		} else if(IsCommand(args,0,"reload")){
    			plugin.Settings.load();
    			sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Done.");
    			return true;
    		} else if(IsCommand(args,0,"save")){
    			plugin.Settings.save();
    			sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Done.");
    			return true;
    		} else if(IsCommand(args,0,"yes")){
    			if(plugin.confirmlistener.handleAnswer(true,sender instanceof Player ? ((CraftPlayer)sender).getName() : "[Console]")){
    				sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Error: Could not handle answer.");
    			}
    			return true;
    		} else if(IsCommand(args,0,"no")){
    			if(plugin.confirmlistener.handleAnswer(false,sender instanceof Player ? ((CraftPlayer)sender).getName() : "[Console]")){
    				sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Error: Could not handle answer.");
    			}
    			return true;
    		} else if(IsCommand(args,0,"help")) {
    			displayhelp(sender);
    			return true;
    		} else if(IsCommand(args,0,"?")) {
    			return HandleHelpCommand(sender, args,name);
    		} else {
    			return false;
    		}
    	} else {
			if(sender instanceof Player)
				sender.sendMessage(ChatColor.BLUE + "MultiModWorld" + ChatColor.WHITE + " by davboecki");
			else
				sender.sendMessage(Color.BLUE + "MultiModWorld" + Color.WHITE + " by davboecki");
    		return false;
    	}
    }

    private boolean HandleHelpCommand(CommandSender sender, String[] args, String name) {
		if(IsCommand(args,1,new String[]{"worldsetting","ws","worldsetting/ws"})){
			MorePageDisplay display = new MorePageDisplay(new String[]{
					"< Help Worldsettings %/$ >"
				},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" worldsetting/ws <world>");
			display.append(info+" Display Settings for world <world>",true);
			display.append(mmw+" worldsetting/ws <world> <tag>");
			display.append("          <true/false>",true);
			display.append(info+" Set <tag> for world <world> to <true/false>",true);
			display.append(mmw+" worldsetting/ws <world> list");
			display.append("          <entity/item> <name>",true);
			display.append(info+" Set <entity/item> list for world <world> to <name>",true);
			display.append(mmw+" worldsetting/ws <world> allowall");
			display.append("          <entity/item> <true/false>",true);
			display.append(info+" Set flag allowall <entity/item> for world <world> to",true);
			display.append("<true/false>",true);
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,new String[]{"exchangeworld","ew","exchangeworld/ew"})) {
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help Exchangeworld %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" exchangeworld/ew <world>");
			display.append(info+" Display Settings for exchangeworld <world>",true);
			display.append(mmw+" exchangeworld/ew <world>");
			display.append("          <Mod/Creative>",true);
			display.append(info+" Set exchangeworld type for exchangeworld <world>",true);
			display.append("          to <Mod/Creative>",true);
			display.append(mmw+" exchangeworld/ew <world> item <name>");
			display.append(info+" Set item list for exchangeworld <world> to <name>",true);
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"known")) {
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help Known %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" known <entity/item>");
			display.append(info+" Display all known <entity/item>",true);
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"list")) {
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help List %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" list <entity/item>");
			display.append(info+" Display all listnames of type <entity/item>",true);
			display.append(mmw+" list <entity/item> add <listname>");
			display.append(info+" Create list <name> of type <entity/item>",true);
			display.append(mmw+" list <entity/item> list <listname>");
			display.append(info+" Display list <name> of type <entity/item>",true);
			display.append(mmw+" list <entity/item> modify <listname>");
			display.append("          <add/remove> <value>",true);
			display.append(info+" <Add/Remove> <value> to/from list <name> of type <entity/item>",true);
			display.append(mmw+" list <entity/item> modify <listname>");
			display.append("          add last",true);
			display.append(info+" Add last blocked <entity/item> to list <name>",true);
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"reload")){
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help Reload %/$ >"
			}, name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" reload");
			display.append(info+" Reload all settings from files.");
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"save")){
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help Save %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" save");
			display.append(info+" Save all settings to files.");
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"yes")){
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help Yes %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" yes");
			display.append(info+" Confirm a question.");
			display.display(sender);
			return true;
		} else if(IsCommand(args,1,"no")){
			MorePageDisplay display = new MorePageDisplay(new String[]{
				"< Help No %/$ >"
			},name);
			String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
			String info = ChatColor.RED+"Info"+ChatColor.WHITE+":";
			display.append(mmw+" no");
			display.append(info+" Deny a question.");
			display.display(sender);
			return true;
		} else {
			return false;
		}
	}

	private boolean HandleListCommand(CommandSender sender, String[] args,String name) {
		if(args.length < 2 || args.length == 3 || args.length == 5 || args.length > 6 ) return false;
		if(!IsCommand(args,1,new String[]{"item","entity"})) return false;
		if(args.length == 2) {
			return HandleListNameCommand(sender,args,name);
		} else if(args.length == 4) {
			if(!args[1].equalsIgnoreCase("item") && !args[1].equalsIgnoreCase("entity"))return false;
			if(!args[2].equalsIgnoreCase("add") && !args[2].equalsIgnoreCase("list"))return false;
			
			args[3] = args[3].toLowerCase();
			
			if(args[2].equalsIgnoreCase("add")){
				if(args[1].equalsIgnoreCase("item")){
					plugin.Settings.GroupItemLists.put(args[3], Default.ItemList());
				} else if(args[1].equalsIgnoreCase("entity")){
					plugin.Settings.GroupEntityList.put(args[3], Default.EntityList());
				}
    			return true;
			} else if(args[2].equalsIgnoreCase("list")) {
				if(args[1].equalsIgnoreCase("item")) {
					if(!plugin.Settings.GroupItemLists.containsKey(args[3])){
						if(sender instanceof Player)
		    				sender.sendMessage(ChatColor.RED + "List not found.");
		    			else
		    				sender.sendMessage(Color.RED + "List not found.");
						return true;
					}
					StringBuilder response = new StringBuilder();
					MorePageDisplay display = new MorePageDisplay(new String[]{"< Itemlist "+args[3]+": %/$ >"},name);
    				boolean first = true;
    				for(Object ido : plugin.Settings.GroupItemLists.get(args[3]).toArray()) {
    					long id = -1;
    					if(ido instanceof Long){
    						id = (Long)ido;
    					} else if(ido instanceof Integer){
    						id = Long.valueOf((Integer)ido);
    					} else continue;
    					if(first){
    						first = false;
    						response.append(id);
    					} else {
    						StringBuilder preadd = response;
    						response.append(", "+id);
    						if(sender instanceof Player) {
    							if(response.toString().length() > display.getZeichen()){
    								response = new StringBuilder();
    								response.append(id);
    								display.append(preadd.toString());
    							}
    						}
    					}
    				}
    				if(sender instanceof Player) {
    					display.display(sender);
    				} else {
    					sender.sendMessage("Itemlists "+args[3]+":");
    					sender.sendMessage(response.toString());
    				}
    				return true;
				} else if(args[1].equalsIgnoreCase("entity")){
					if(!plugin.Settings.GroupEntityList.containsKey(args[3])){
						if(sender instanceof Player)
		    				sender.sendMessage(ChatColor.RED + "List not found.");
		    			else
		    				sender.sendMessage(Color.RED + "List not found.");
						return true;
					}
   					StringBuilder response = new StringBuilder();
   					MorePageDisplay display = new MorePageDisplay(new String[]{"< Entitylist "+args[3]+": %/$ >"},name);
    				boolean first = true;
    				for(Object nameo : plugin.Settings.GroupEntityList.get(args[3]).toArray()) {
    					String Lname = (String)nameo;
    					if(sender instanceof Player) {
    						display.append(Lname);
    					} else if(first){
    						first = false;
    						response.append(Lname);
    					} else {
    						response.append("\n"+Lname);
    						
    					}
    				}
    				if(sender instanceof Player) {
    					display.display(sender);
    				} else {
    					sender.sendMessage("Entitylist "+args[3]+":");
    					sender.sendMessage(response.toString());
					}
        			return true;
				} else {
        			return false;
				}
			} else {
				return false;
			}
		} else  if(args.length == 6) {
			if(!args[2].equalsIgnoreCase("modify"))return false;
			if(!args[4].equalsIgnoreCase("add") && !args[4].equalsIgnoreCase("remove"))return false;
			
			args[3] = args[3].toLowerCase();
			if(args[1].equalsIgnoreCase("item")) {
				return HandleListItemModifyCommand(sender,args,name);
			} else if(args[1].equalsIgnoreCase("entity")) {
				return HandleListEntityModifyCommand(sender,args,name);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean HandleListNameCommand(CommandSender sender, String[] args, String name) {
		if(args[1].equalsIgnoreCase("item")){
			StringBuilder response = new StringBuilder();
			MorePageDisplay display = new MorePageDisplay(new String[]{"< Itemlists: %/$ >"},name);
			boolean first = true;
			for(String Lname : plugin.Settings.GroupItemLists.keySet()) {
				if(sender instanceof Player) {
					display.append(Lname);
				} else if(first){
					first = false;
					response.append(Lname);
				} else {
					response.append("\n "+Lname);
				}
			}
			if(sender instanceof Player) {
				display.display(sender);
			} else {
				sender.sendMessage("Itemlists:");
				sender.sendMessage(response.toString());
			}
		} else if(args[1].equalsIgnoreCase("entity")){
			StringBuilder response = new StringBuilder();
			MorePageDisplay display = new MorePageDisplay(new String[]{"< Entitylists: %/$ >"},name);
			boolean first = true;
			for(String Lname : plugin.Settings.GroupEntityList.keySet()) {
				if(sender instanceof Player) {
					display.append(Lname);
				} else if(first){
					first = false;
					response.append(Lname);
				} else {
					response.append("\n"+Lname);
				}
			}
			if(sender instanceof Player) {
				display.display(sender);
			} else {
				sender.sendMessage("Entitylist:");
				sender.sendMessage(response.toString());
			}
		}
		return true;
	}

	private boolean HandleListEntityModifyCommand(CommandSender sender, String[] args, String name) {
		if(!plugin.Settings.GroupEntityList.containsKey(args[3])){
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "List not found.");
			return true;
		}

		boolean confirm = false;
		
		if(args[5].equalsIgnoreCase("last") && args[4].equalsIgnoreCase("add")) {
			if(!GetLastEntity(sender,args)) {
				return true;
			}
			confirm = true;
		}
		
		String value = args[5];
		if(args[4].equalsIgnoreCase("remove")){
			if(plugin.Settings.GroupEntityList.get(args[3]).contains(value)){
				plugin.Settings.GroupEntityList.get(args[3]).remove(value);
				sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Entity removed.");
				return true;
			} else {
    			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Entity not listed.");
				return true;
			}
		} else if(args[4].equalsIgnoreCase("add")){
			if(plugin.Settings.GroupEntityList.get(args[3]).contains(value)){
				plugin.Settings.GroupEntityList.get(args[3]).remove(value);
    			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Entity already listed.");
				return true;
			} else {
				try {
					value = replacenms(value);
					if(Class.forName(value) != null){			
						if(!confirm){
							plugin.Settings.GroupEntityList.get(args[3]).add(value);
							sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Entity added.");
						} else {
							if(sender instanceof Player) {
								sender.sendMessage("Add entity "+ChatColor.BLUE+ value +ChatColor.WHITE+" ? <"+ChatColor.GREEN+"Yes"+ChatColor.WHITE+"/"+ChatColor.RED+"No"+ChatColor.WHITE+">");
							} else {
								sender.sendMessage("Add entity "+Color.BLUE+ value +Color.WHITE+" ? /multimodworld <"+Color.GREEN+"yes"+Color.WHITE+"/"+Color.RED+"no"+Color.WHITE+">");
							}
							plugin.confirmlistener.addTask(new CallableObjects(new Object[]{args[3],value,sender},plugin){
								public Object call(){
									plugin.Settings.GroupEntityList.get((String)args[0]).add((String)args[1]);
									((CommandSender)args[2]).sendMessage((args[2] instanceof Player?ChatColor.GREEN:Color.GREEN) + "Entity added.");
									return null;
								}
							},name);
						}
						return true;
					} else {
						sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Entityclass not found.");
						return true;
					}
				} catch (ClassNotFoundException e) {
					sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Entityclass not found.");
					return true;
				} catch(Exception e1) {
					sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Entityclass could not be checked.");
					e1.printStackTrace();
					return true;
				}
			}
		} else {
			return false;
		}
	}

	private boolean HandleListItemModifyCommand(CommandSender sender, String[] args, String name) {
		if(!isNumber(args[5])) {
			if(sender instanceof Player)
				sender.sendMessage(ChatColor.RED + "Input is not a number.");
			else
				sender.sendMessage(Color.RED + "Input is not a number.");
			return true;
		}
		
		boolean confirm = false;
		
		if(args[5].equalsIgnoreCase("last") && args[4].equalsIgnoreCase("add")) {
			if(!GetLastItem(sender,args)) {
				return true;
			}
			confirm = true;
		}
		
		int value = toNumber(args[5]);
		
		if(!plugin.Settings.GroupItemLists.containsKey(args[3])){
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "List not found.");
			return true;
		}
		if(args[4].equalsIgnoreCase("remove")){
			if(plugin.Settings.GroupItemLists.get(args[3]).contains((long)value)){
				plugin.Settings.GroupItemLists.get(args[3]).remove((long)value);
				sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Id removed.");
				return true;
			} else {
    			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Id not listed.");
				return true;
			}
		} else if(args[4].equalsIgnoreCase("add")){
			if(plugin.Settings.GroupItemLists.get(args[3]).contains((long)value)){
    			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Id already listed.");
				return true;
			} else {
				if(!confirm){
					plugin.Settings.GroupItemLists.get(args[3]).add((long)value);
					sender.sendMessage((sender instanceof Player?ChatColor.GREEN:Color.GREEN) + "Id added.");
				} else {
					if(sender instanceof Player) {
						sender.sendMessage("Add Id "+ChatColor.BLUE+ value +ChatColor.WHITE+" ? <"+ChatColor.GREEN+"Yes"+ChatColor.WHITE+"/"+ChatColor.RED+"No"+ChatColor.WHITE+">");
					} else {
						sender.sendMessage("Add Id "+Color.BLUE+ value +Color.WHITE+" ? /multimodworld <"+Color.GREEN+"yes"+Color.WHITE+"/"+Color.RED+"no"+Color.WHITE+">");
					}
					plugin.confirmlistener.addTask(new CallableObjects(new Object[]{args[3],value,sender},plugin){
						public Object call(){
							plugin.Settings.GroupItemLists.get((String)args[0]).add((long)((Long)args[1]));
							((CommandSender)args[2]).sendMessage((args[2] instanceof Player?ChatColor.GREEN:Color.GREEN) + "Id added.");
							return null;
						}
					},name);
				}
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean HandleKnownCommand(CommandSender sender, String[] args, String name) {
    	if(args[1].equalsIgnoreCase("item")) {
    		MorePageDisplay display = new MorePageDisplay(new String[]{
					"< KnownItem list %/$ >"
			},name);
    		StringBuilder response = new StringBuilder();
			boolean first = true;
			for(Object nameo:plugin.Settings.KnownItems.toArray()) {
				String Lname = (String)String.valueOf(nameo);
				if(first){
					first = false;
					StringBuilder preadd = response;
					response.append(Lname);

				} else {
					StringBuilder preadd = response;
					response.append(", "+Lname);
					if(sender instanceof Player) {
						if(response.toString().length() > display.getZeichen()){
							response = new StringBuilder();
							response.append(Lname);
							display.append(preadd.toString());
						}
					}
				}
			}
			if(sender instanceof Player){
				display.display(sender);
			} else {
				sender.sendMessage("Knownitems:");
				sender.sendMessage(response.toString());
			}
			return true;
		} else if(args[1].equalsIgnoreCase("entity")){
    		MorePageDisplay display = new MorePageDisplay(new String[]{
					"< KnownItem list %/$ >"
			},name);			StringBuilder response = new StringBuilder();
			boolean first = true;
			for(Object nameo:plugin.Settings.KnownEntities.toArray()) {
				String Lname = (String)nameo;
				if(first){
					first = false;
					if(sender instanceof Player) {
						display.append(Lname);
					} else {
						response.append(Lname);
					}
				} else {
					if(sender instanceof Player) {
						display.append(Lname);
					} else {
						response.append("\n "+Lname);
					}
				}
			}
			if(sender instanceof Player) {
				display.display(sender);
			} else {
				sender.sendMessage("Knownentities:");
				sender.sendMessage(response.toString());
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean GetLastItem(CommandSender sender, String[] args) {
    	if(plugin.MultiModWorld == null){
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Servermod not installed.");
    		return false;
    	}
    	int last = plugin.MultiModWorld.getLastBlockedID();
    	if(last == 0) {
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "No last blocked id.");
    		return false;
    	} else {
    		args[5] = Integer.toString(last);
    		return true;
    	}
    }

    private boolean GetLastEntity(CommandSender sender, String[] args) {
    	if(plugin.MultiModWorld == null){
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "Servermod not installed.");
    		return false;
    	}
    	String last = plugin.MultiModWorld.getLastBlockedEntity();
    	if(last == "") {
			sender.sendMessage((sender instanceof Player?ChatColor.RED:Color.RED) + "No last blocked entity.");
    		return false;
    	} else {
    		args[5] = last;
    		return true;
    	}
    }
    
    private String replacenms(String input){
    	boolean ClassFound = false;
    	try {
    		if(Class.forName(input) != null){
    			ClassFound = true;
    		}
    	} catch(Exception e){}
		if(input.startsWith("nms.") && !ClassFound){
			return "net.minecraft.setver." + input.substring(4);
		} else {
			return input;
		}
    }
    
    public static boolean isNumber(String input){
    	try{
    		int number = Integer.parseInt(input);
    		String numstring = String.valueOf(number);
    		if(numstring.equalsIgnoreCase(input)){
    			return true;
    		}
    	} catch(Exception e){}
		return false;
    }
    
    public static int toNumber(String input){
    	try{
    		return Integer.parseInt(input);
    	} catch(Exception e){}
		return -1;
    }
    
    private boolean HandleEWCommand(CommandSender sender, String[] args) {
    	if(args.length == 1){
    		ListExchangeWorlds(sender);
    		return true;
    	} else if(args.length > 4) { return false;
    	} else {
    		World world = getWorld(args[1]);
    		if(world == null || !plugin.Settings.ExchangeWorlds.containsKey((world == null ? args[1] : world.getName()))) {
    			if(sender instanceof Player) {
    				sender.sendMessage(ChatColor.RED + "Error:" + ChatColor.WHITE + " World " + args[1] + " could not be found.");
    			} else {
    				sender.sendMessage(Color.RED + "Error:" + Color.WHITE + " World "  +args[1] + " could not be found.");
    			}
    			return false;
    		}
    		args[1] = world.getName();
    		if(args.length == 2) {
    			DisplayExchangeWorldSettings(sender,args[1],plugin.Settings.ExchangeWorlds.get(args[1]));
    			return true;
    		} else if(args.length == 3){
    			if(args[2].equalsIgnoreCase("Mod") || args[2].equalsIgnoreCase("Creative")){
    				plugin.Settings.ExchangeWorlds.get(args[1]).WorldType = args[2].equalsIgnoreCase("Mod") ? "Mod" : "Creative";
    				if(sender instanceof Player) {
        				sender.sendMessage(ChatColor.GREEN + "Type set.");
        			} else {
        				sender.sendMessage(Color.GREEN + "Type set.");
        			}
    				return true;
    			} else {
    				return false;
    			}
    		} else if(args.length == 4){
    			if(args[2].equalsIgnoreCase("item")){
	    			if(!plugin.Settings.GroupItemLists.containsKey(args[1])){
	    				if(sender instanceof Player) {
	        				sender.sendMessage(ChatColor.RED + "Error:" + ChatColor.WHITE + " List " + args[1] + " does not exist.");
	        			} else {
	        				sender.sendMessage(Color.RED + "Error:" + Color.WHITE + " List "  +args[1] + " does not exist.");
	        			}
	    				return false;
	    			}
	    			plugin.Settings.ExchangeWorlds.get(args[1]).ItemList = args[1];
	    			if(sender instanceof Player) {
        				sender.sendMessage(ChatColor.GREEN + "List set.");
        			} else {
        				sender.sendMessage(Color.GREEN + "List set.");
        			}
	    			return true;
    			} else {
    				return false;
    			}
    		} else {
    			return false;
    		}
    	}
    }
    
    private void DisplayExchangeWorldSettings(CommandSender sender, String Worldname,ExchangeWorldSetting setting){
    	if(sender instanceof Player) {
    		sender.sendMessage(ChatColor.AQUA+"Exchangeworld " + Worldname + ":");
    	} else {
    		sender.sendMessage(Color.BLUE+"Exchangeworld " + Worldname + ":");
    	}
    	sender.sendMessage("The id list is: "+setting.ItemList);
		sender.sendMessage("The exchangeworld type is: "+setting.WorldType);
    }
    
    private void ListExchangeWorlds(CommandSender sender){
    	if(sender instanceof Player) {
    		sender.sendMessage(ChatColor.AQUA+"List of all exchangeworlds:");
    	} else {
    		sender.sendMessage(Color.BLUE+"List of all exchangeworlds:");
    	}
    	for(String WorldName:plugin.Settings.ExchangeWorlds.keySet()){
    		sender.sendMessage(WorldName+": "+plugin.Settings.ExchangeWorlds.get(WorldName).WorldType);
    	}
    }
    
    private boolean HandleWSCommand(CommandSender sender, String[] args){
    	if(args.length < 2){return false;}
    	if(args.length == 3){return false;}
    	if(args.length > 5){return false;}
    	World world = getWorld(args[1]);
		if(world == null) {
			if(sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "Error:" + ChatColor.WHITE + " World " + args[1] + " could not be found.");
			} else {
				sender.sendMessage(Color.RED + "Error:" + Color.WHITE + " World "  +args[1] + " could not be found.");
			}
			return false;
		}
		args[1] = world.getName();
		if(args.length == 2){
    		return DisplayWorldSettings(sender,world);
    	} else if(args.length == 4){
    		return HandleTagWorldCommand(sender,args);
    	} else if(args.length == 5){
    		return HandleAllowingCommand(sender,args);
    	} else {
    		return false;
    	}
    }
    
    private boolean DisplayWorldSettings(CommandSender sender, World world){
    	if(!plugin.Settings.WorldSettings.containsKey(world.getName())) return false;
    	WorldSetting setting = plugin.Settings.WorldSettings.get(world.getName());
		if(sender instanceof Player){
			sender.sendMessage(ChatColor.BLUE+"World: "+world.getName());
			sender.sendMessage("Teleporting into this world is " + (setting.CheckTeleport?"":(ChatColor.RED+"not "+ChatColor.WHITE)) + "checked.");
			sender.sendMessage((setting.AllIdsAllowed?"A":ChatColor.RED+"Not"+ChatColor.WHITE+" a")+"ll ids are allowed in this world");
			sender.sendMessage((setting.AllEntitiesAllowed?"A":ChatColor.RED+"Not"+ChatColor.WHITE+" a")+"ll entities are allowed in this world");
			sender.sendMessage("The id list is: "+setting.ItemList);
			sender.sendMessage("The entity list is: "+setting.EntityList);
			sender.sendMessage("Tags:");
			for(String key:setting.Tags.keySet()){
				sender.sendMessage((setting.Tags.get(key)?ChatColor.GREEN:ChatColor.RED)+"  "+key+": "+(setting.Tags.get(key)?"true":"false"));
			}
		} else {
			sender.sendMessage(Color.BLUE+"World: "+world.getName());
			sender.sendMessage("Teleporting into this world is " + (setting.CheckTeleport?"":(Color.RED+"not "+Color.WHITE)) + "checked.");
			sender.sendMessage((setting.AllIdsAllowed?"A":Color.RED+"Not"+Color.WHITE+" a")+"ll ids are allowed in this world");
			sender.sendMessage((setting.AllEntitiesAllowed?"A":Color.RED+"Not"+Color.WHITE+" a")+"ll entities are allowed in this world");
			sender.sendMessage("The id list is: "+setting.ItemList);
			sender.sendMessage("The entity list is: "+setting.EntityList);
			sender.sendMessage("Tags:");
			for(String key:setting.Tags.keySet()){
				sender.sendMessage((setting.Tags.get(key)?Color.GREEN:Color.RED)+"  "+key+": "+(setting.Tags.get(key)?"true":"false"));
			}
		}
    	return true;
    }
    
    private boolean HandleTagWorldCommand(CommandSender sender, String[] args){
    	if(!args[3].equalsIgnoreCase("true") && !args[3].equalsIgnoreCase("false") && !args[3].equalsIgnoreCase("on") && !args[3].equalsIgnoreCase("off") && !args[3].equalsIgnoreCase("0") && !args[3].equalsIgnoreCase("1") && !args[3].equalsIgnoreCase("yes") && !args[3].equalsIgnoreCase("no")) return false;
		boolean flag = args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("on") || args[3].equalsIgnoreCase("1") || args[3].equalsIgnoreCase("yes");
		if(!plugin.Settings.WorldSettings.get(args[1]).Tags.containsKey(args[2])){
			plugin.Settings.WorldSettings.get(args[1]).Tags.put(args[2],flag);
			sender.sendMessage((sender instanceof Player ? ChatColor.GREEN : Color.GREEN) + "Tag created.");
		} else {
			plugin.Settings.WorldSettings.get(args[1]).Tags.remove(args[2]);
			plugin.Settings.WorldSettings.get(args[1]).Tags.put(args[2],flag);
			sender.sendMessage((sender instanceof Player ? ChatColor.GREEN : Color.GREEN) + "Tag set.");
		}
    	return true;
    }
    
    private boolean HandleAllowingCommand(CommandSender sender, String[] args){
		if(!args[2].equalsIgnoreCase("list") && !args[2].equalsIgnoreCase("allowall")) return false;
		if(!args[3].equalsIgnoreCase("item") && !args[3].equalsIgnoreCase("entity")) return false;
		if(args[2].equalsIgnoreCase("list")) {
			if(args[3].equalsIgnoreCase("item")){
				plugin.Settings.WorldSettings.get(args[1]).ItemList = args[4];
			} else if(args[3].equalsIgnoreCase("entity")){
				plugin.Settings.WorldSettings.get(args[1]).EntityList = args[4];
			} else {
				return false;
			}
			sender.sendMessage((sender instanceof Player ? ChatColor.GREEN : Color.GREEN) + "List set.");
		} else if(args[2].equalsIgnoreCase("allowall")) {
			if(!args[4].equalsIgnoreCase("true") && !args[4].equalsIgnoreCase("false") && !args[4].equalsIgnoreCase("on") && !args[4].equalsIgnoreCase("off") && !args[4].equalsIgnoreCase("0") && !args[4].equalsIgnoreCase("1") && !args[4].equalsIgnoreCase("yes") && !args[4].equalsIgnoreCase("no")) return false;
			boolean flag = args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("on") || args[4].equalsIgnoreCase("1") || args[4].equalsIgnoreCase("yes");
			if(args[3].equalsIgnoreCase("item")){
				plugin.Settings.WorldSettings.get(args[1]).AllIdsAllowed = flag;
			} else if(args[3].equalsIgnoreCase("entity")){
				plugin.Settings.WorldSettings.get(args[1]).AllEntitiesAllowed = flag;
			} else {
				return false;
			}
			sender.sendMessage((sender instanceof Player ? ChatColor.GREEN : Color.GREEN) + "Tag set.");
		} else {
			return false;
		}
    	return true;
    }
    
    private World getWorld(String worldname) {
    	for(Object world : plugin.getServer().getWorlds().toArray()){
    		if(!(world instanceof World))continue;
    		if(((World)world).getName().equalsIgnoreCase(worldname)){
    			return ((World)world);
    		}
    	}
    	return null;
    }
    
	public void displayhelp(CommandSender sender) {
		displayhelp(sender,false);
	}
		
	public void displayhelp(CommandSender sender,boolean cnf) {
    	String name = sender instanceof Player ? ((CraftPlayer)sender).getName() : "[Console]";
		String[] header;
		if(cnf){
			header = new String[]{
				"< Help %/$ >",
				ChatColor.RED+"(Command could not be found)"
			};		
		} else {
			header = new String[]{
				"< Help %/$ >"
			};
		}
		MorePageDisplay display = new MorePageDisplay(header,name);
		String mmw = ChatColor.AQUA+"/multimodworld"+ChatColor.WHITE;
		display.append(mmw+" worldsetting/ws <world>");
		display.append(mmw+" worldsetting/ws <world> <tag>");
		display.append("          <true/false>",true);
		display.append(mmw+" worldsetting/ws <world> list");
		display.append("          <entity/item> <name>",true);
		display.append(mmw+" worldsetting/ws <world> allowall");
		display.append("          <entity/item> <true/false>",true);
		display.append(mmw+" exchangeworld/ew <world>");
		display.append(mmw+" exchangeworld/ew <world>");
		display.append("          <Mod/Creative>",true);
		display.append(mmw+" exchangeworld/ew <world> item <name>");
		display.append(mmw+" known <entity/item>");
		display.append(mmw+" list <entity/item>");
		display.append(mmw+" list <entity/item> add <listname>");
		display.append(mmw+" list <entity/item> list <listname>");
		display.append(mmw+" list <entity/item> modify <listname>");
		display.append("          <add/remove> <value>",true);
		display.append(mmw+" list <entity/item> modify <listname>");
		display.append("          add last",true);
		display.append(mmw+" save");
		display.append(mmw+" reload");
		display.append(mmw+" yes");
		display.append(mmw+" no");
		display.append(mmw+" help");
		display.append(mmw+" ? <command>");

		display.display(sender);
	}
    
}
