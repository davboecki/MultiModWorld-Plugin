package de.davboecki.multimodworld.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ModLoader;
import net.minecraft.server.Packet;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.api.plugin.IModWorldHandlePlugin;
import forge.MinecraftForge;
import forge.NetworkMod;
import forge.packets.PacketModList;

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

	public boolean hasWorldSetting(String WorldName, String Setting) {
		if(Setting.equals("UseVanillaRecipes")) {
			return Settings.getWorldSetting(WorldName).UseVanillaRecipes;
		} else if(Setting.equals("PopulateChunk")) {
			return Settings.getWorldSetting(WorldName).PopulateChunk;
		} else {
			return plugin.Settings.getTag(WorldName,Setting);
		}
	}

	public boolean isCraftingAllowed(String WorldName, int id) {
		return this.isIdAllowed(WorldName, id);
	}

	public boolean PacketSend(Packet packet, Player player) {
		return plugin.PacketListener.PacketSend(packet, player);
	}

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

	
	
	//plugin.PlayerModPacketListener.onModsMissingHandle(var1.getBukkitEntity(), var14);
	//plugin.PlayerModPacketListener.onModsOKHandle(var1.getBukkitEntity())

	public boolean handleModPacketResponse(EntityPlayer eplayer, PacketModList pkt) {
		NetworkMod[] serverMods = MinecraftForge.getNetworkMods();
        ArrayList<String> missing = new ArrayList<String>();
        for (NetworkMod mod : serverMods)
        {
            if (!mod.clientSideRequired())
            {
                continue;
            }
            boolean found = false;
            for (String modName : pkt.Mods)
            {
                if (modName.equals(mod.toString()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                missing.add(mod.toString());
            }
        }
        if (missing.size() > 0)
        {
        	plugin.PlayerModPacketListener.onModsMissingHandle(eplayer.getBukkitEntity(), missing);
        }
        else
        {
        	plugin.PlayerModPacketListener.onModsOKHandle(eplayer.getBukkitEntity());
        }
		return true;
	}

	public List replaceRecipies(List recipies, String Worldname) {
		// TODO Auto-generated method stub
		return null;
	}
}
