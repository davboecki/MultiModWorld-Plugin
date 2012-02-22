package de.davboecki.multimodworld.plugin;

import net.minecraft.server.Entity;
import de.davboecki.multimodworld.plugin.settings.Settings;
import de.davboecki.multimodworld.server.plugin.IModWorldHandlePlugin;

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
		} else {
			return plugin.Settings.getTag(WorldName,Setting);
		}
	}

	@Override
	public boolean isCraftingAllowed(String WorldName, int id) {
		return this.isIdAllowed(WorldName, id);
	}
}
