package de.davboecki.multimodworld.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.minecraft.server.BaseModMp;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet230ModLoader;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.api.plugin.IModWorldHandlePlugin;

public class MultiModWorld implements IModWorldHandlePlugin{
	
	PrivatChest plugin;
    protected String IdAllowedLastWorld = "";
    protected int IdAllowedLastid = 0;
    protected boolean LastValue = false;
    protected int LastBlockedID = 0;
    protected String LastBlockedEntity = "";
    

	MultiModWorld(PrivatChest instance){
		plugin = instance;
	}
    
    void cachereset(){
        IdAllowedLastWorld = "";
        IdAllowedLastid = 0;
        LastValue = false;
    }

	public int getLastBlockedID() {
		return LastBlockedID;
	}
	
	public String getLastBlockedEntity() {
		return LastBlockedEntity;
	}
	
	@Override
	public boolean isIdAllowed(String WorldName, int id) {
		if(IdAllowedLastWorld.equals(WorldName) && id == IdAllowedLastid){
			return LastValue;
		}
		plugin.Settings.ListItem(id);
		IdAllowedLastWorld = WorldName;
		IdAllowedLastid = id;
		LastValue = ItemCheckHandler.isItemAllowed(WorldName,id);
		if(!LastValue){
			LastBlockedID = id;
		}
		return LastValue;
	}

	@Override
	public boolean isEntityAllowed(String WorldName, Entity entity) {
		boolean flag;
		String Class = entity.getClass().getName();
		plugin.Settings.ListEntity(Class);
		flag = plugin.Settings.getEntityList(WorldName).contains(Class) || plugin.Settings.getWorldSetting(WorldName).AllEntitiesAllowed;
		if(!flag){
			plugin.log.info("[PrivatChest] Entity Blocked: "+Class);
			LastBlockedEntity = Class;
		}
		return flag;
	}

	@Override
	public boolean hasWorldSetting(String WorldName, String Setting) {
		if(Setting.equals("UseVanillaRecipes")) {
			return Settings.getWorldSetting(WorldName).UseVanillaRecipes;
		} else if(Setting.equals("PopulateChunk")) {
			return Settings.getWorldSetting(WorldName).PopulateChunk;
		} else {
			return plugin.Settings.getTag(WorldName,Setting);
		}
	}

	@Override
	public boolean isCraftingAllowed(String WorldName, int id) {
		return this.isIdAllowed(WorldName, id);
	}

	@Override
	public boolean PacketSend(Packet packet, Player player) {
		return plugin.PacketListener.PacketSend(packet, player);
	}

	@Override
	public Entity ReplaceEntity(String WorldName, Entity entity) {
		if(true) {
			try {
				if(Class.forName("railcraft.common.carts.EntityCartChest").isInstance(entity)) {
					entity = new net.minecraft.server.EntityMinecart(entity.world, entity.locX, entity.locY, entity.locZ, 1);
				} else if(Class.forName("railcraft.common.carts.EntityCartSteam").isInstance(entity)) {
					entity = new net.minecraft.server.EntityMinecart(entity.world, entity.locX, entity.locY, entity.locZ, 2);
				}
			} catch (Exception e) {}
		}
		return entity;
	}

	@Override
	public boolean handleModPacketResponse(Packet230ModLoader var0, EntityPlayer var1, List bannedMods) {
	    StringBuilder var2 = new StringBuilder();
	    if (var0.dataString.length != 0) {
	      for (int var3 = 0; var3 < var0.dataString.length; var3++)
	        if (var0.dataString[var3].lastIndexOf("mod_") != -1) {
	          if (var2.length() != 0) {
	            var2.append(", ");
	          }

	          var2.append(var0.dataString[var3].substring(var0.dataString[var3].lastIndexOf("mod_")));
	        }
	    }
	    else {
	      var2.append("no mods");
	    }

	    ArrayList var11 = new ArrayList();

	    for (int var5 = 0; var5 < bannedMods.size(); var5++) {
	      for (int var4 = 0; var4 < var0.dataString.length; var4++) {
	        if ((var0.dataString[var4].lastIndexOf("mod_") != -1) && (var0.dataString[var4].substring(var0.dataString[var4].lastIndexOf("mod_")).startsWith((String)bannedMods.get(var5)))) {
	          var11.add(var0.dataString[var4]);
	        }
	      }
	    }

	    ArrayList var12 = new ArrayList();

	    for (int var4 = 0; var4 < ModLoader.getLoadedMods().size(); var4++) {
	      BaseModMp var6 = (BaseModMp)ModLoader.getLoadedMods().get(var4);
	      if ((var6.hasClientSide()) && (var6.toString().lastIndexOf("mod_") != -1)) {
	        String var7 = var6.toString().substring(var6.toString().lastIndexOf("mod_"));
	        boolean var8 = false;

	        for (int var9 = 0; var9 < var0.dataString.length; var9++) {
	          if (var0.dataString[var9].lastIndexOf("mod_") != -1) {
	            String var10 = var0.dataString[var9].substring(var0.dataString[var9].lastIndexOf("mod_"));
	            if (var7.equals(var10)) {
	              var8 = true;
	              break;
	            }
	          }
	        }

	        if (!var8) {
	          var12.add(var7);
	        }

	      }

	    }

	    if (var11.size() != 0) {
	      StringBuilder var14 = new StringBuilder();

	      for (int var13 = 0; var13 < var11.size(); var13++) {
	        if (((String)var11.get(var13)).lastIndexOf("mod_") != -1) {
	          if (var14.length() != 0) {
	            var14.append(", ");
	          }

	          var14.append(((String)var11.get(var13)).substring(((String)var11.get(var13)).lastIndexOf("mod_")));
	        }
	      }

	      StringBuilder var15 = new StringBuilder();

	      for (int var9 = 0; var9 < var11.size(); var9++) {
	        if (((String)var11.get(var9)).lastIndexOf("mod_") != -1) {
	          var15.append("\n");
	          var15.append(((String)var11.get(var9)).substring(((String)var11.get(var9)).lastIndexOf("mod_")));
	        }
	      }

	      var1.netServerHandler.disconnect("The following mods are banned on this server:" + var15.toString());
	    } else if (var12.size() != 0) {
	    	ArrayList<String> var14 = new ArrayList<String>();

	      for (int var13 = 0; var13 < var12.size(); var13++) {
	        if (((String)var12.get(var13)).lastIndexOf("mod_") != -1) {
	          var14.add(((String)var12.get(var13)).substring(((String)var12.get(var13)).lastIndexOf("mod_")));
	        }
	      }
	      plugin.PlayerModPacketListener.onModsMissingHandle(var1.getBukkitEntity(), var14);
	      var1.netServerHandler.disconnect("You are missing the following mods:" + var14.toString());
	    }
	    plugin.PlayerModPacketListener.onModsOKHandle(var1.getBukkitEntity());
		return true;
	}
}
