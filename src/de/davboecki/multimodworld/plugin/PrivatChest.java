package de.davboecki.multimodworld.plugin;

import net.minecraft.server.BaseMod;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.generator.*;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.davboecki.multimodworld.plugin.commandhandler.CommandHandler;
import de.davboecki.multimodworld.plugin.commandhandler.ConfirmListener;
import de.davboecki.multimodworld.plugin.handitemhelper.HandItemThread;
import de.davboecki.multimodworld.plugin.listener.*;
import de.davboecki.multimodworld.plugin.reteleport.ReTeleportThread;
import de.davboecki.multimodworld.plugin.reteleport.TeleportHandler;
import de.davboecki.multimodworld.plugin.yaml.MyYamlConstructor;
import de.davboecki.multimodworld.plugin.settings.ExchangeWorldSetting;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.plugin.settings.WorldSetting;
import de.davboecki.multimodworld.plugin.inventory.ModItemSaver;

import de.davboecki.multimodworld.api.ForgeLoginHooks;
import de.davboecki.multimodworld.api.ModChecker;
import de.davboecki.multimodworld.api.plugin.IModWorldHandlePlugin;

public class PrivatChest extends JavaPlugin {
	
    public static FlatWorldGenerator Worldgen = new FlatWorldGenerator();
    public PlayerPositionCheck PlayerPositionCheck = new PlayerPositionCheck(this);
    
    public Logger log = Logger.getLogger("Minecraft");
    public final PrivatChestPlayerListener PrivatChestPlayerListener = new PrivatChestPlayerListener(this);
    private final CreatueSpawnListener CreatueSpawnListener = new CreatueSpawnListener(this);
    private final PrivatChestBlockListener PrivatChestBlockListener = new PrivatChestBlockListener(this);
    private final WorldLoadListener WorldLoadListener = new WorldLoadListener(this);
    public final PlayerModPacketListener PlayerModPacketListener = new PlayerModPacketListener(this);
    private final FurnaceListener FurnaceListener = new FurnaceListener(this);
    private final PlayerPreCommandListener PlayerPreCommandListener = new PlayerPreCommandListener(this);
    public final ConfirmListener confirmlistener = new ConfirmListener(this);
    public RoomControler RoomControl = new RoomControler(this);
    //public ArrayList<String> ModPacketOK = new ArrayList<String>();
    public Settings Settings = new Settings(this);
    private Yaml yaml;
    private CommandHandler commandhandler = null;
    public static MultiModWorld MultiModWorld = null;
    public TeleportHandler teleporthandler = new TeleportHandler(this);
    public PacketListener PacketListener = new PacketListener(this);
    public ModItemSaver ModItemSaver = new ModItemSaver(this);
    private boolean init = false;
    
	//Debug Mode
    private static boolean debug = false;
    public static boolean debug(){
    	return debug;
    }
    
    public static void setdebug(Boolean flag){
    	debug = flag;
    }
    //Debug Mode
    
    public Settings getSettings(){
    	return Settings;
    }
    
	public void onEnable() {
    	if(!init) {
	    	boolean flag = false;
			try {
				Class HandleInterface = Class.forName("de.davboecki.multimodworld.api.plugin.IModWorldHandlePlugin");
				if(HandleInterface != null){
					flag = true;
				}
			} catch (Exception e) {}
			if(flag && MultiModWorld == null) {
		        log.info("[PrivatChest] IModWorldHandlePlugin class found!");
				MultiModWorld = new MultiModWorld(this);
				ModChecker.registerIModWorldHandlePlugin(MultiModWorld);
			}
    	}
	    PluginManager pm = this.getServer().getPluginManager();
	    pm.registerEvents(PrivatChestPlayerListener, this);
	    pm.registerEvents(CreatueSpawnListener, this);
	    pm.registerEvents(PrivatChestBlockListener, this);
	    pm.registerEvents(WorldLoadListener, this);
	    pm.registerEvents(FurnaceListener, this);
	    pm.registerEvents(PlayerPreCommandListener, this);
	    pm.registerEvents(confirmlistener, this);
	
	    this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new ReTeleportThread(this), 1, 1);

	    commandhandler = new CommandHandler(this);
	        
	    log.info("[PrivatChest] Plugin v"+this.getDescription().getVersion()+" has been enabled!");
	    if(!init) {
		    if(MultiModWorld != null) {
		    	String ModVersion = de.davboecki.multimodworld.api.ModChecker.getVersion();
		        String MultiModWorldVersion = "v1.2.0";
		        boolean correctversion = ModVersion.equalsIgnoreCase(MultiModWorldVersion);
		        if(!correctversion) {
		        	this.getServer().getPluginManager().disablePlugin(this);
		        	if(!correctversion)
		        		log.severe("[MultiModWorld] Mod "+ModVersion+" has been found. But "+MultiModWorldVersion+" is required.");
		        } else {
		        	log.info("[MultiModWorld] Mod "+ModVersion+" has been enabled.");
		        }
		    }
		init  = true;
	    }
        Settings.load();
        ModItemSaver.load();
    }

    public void save(){Settings.save();}
    public void load(){Settings.load();}
    
    public void onDisable() {
    	Settings.save();
        ModItemSaver.save();
        log.info("[PrivatChest] Plugin v"+this.getDescription().getVersion()+" has been disabled.");
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("multimodworld") || label.equalsIgnoreCase("mmw") || label.equalsIgnoreCase("privatchest")) {

        	//Op-only
        	if(!sender.isOp()) {
        		sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
        		return true;
        	}
        	
        	boolean flag = commandhandler.HandleMMWCommand(sender, args);
        	MultiModWorld.cachereset(); //Reset Cache
        	if(!flag && sender instanceof Player){
        		commandhandler.displayhelp(sender,true);
        		return true;
        	}
        	return flag;
        } else {
        	return false;
        }
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	log.info("[PrivatChest] WorldName: "+worldName);
    	if(!Settings.ExchangeWorlds.containsKey(worldName)){
    		log.info("[PrivatChest] New WorldName: "+worldName);
    		Settings.ExchangeWorlds.put(worldName,new ExchangeWorldSetting());
    		Settings.ExchangeWorlds.get(worldName).WorldType = "Creative";
    		Settings.save();
    	}
        return Worldgen;
    }

}
