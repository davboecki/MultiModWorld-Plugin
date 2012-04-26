package de.davboecki.multimodworld.plugin.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.davboecki.multimodworld.plugin.RoomLocation;


public class ExchangeWorldSetting {

	public String WorldType;
	public String ItemList;
	public HashMap<String, RoomLocation> ChestRooms;
	
	public ExchangeWorldSetting(Object source){
		if(source instanceof ExchangeWorldSetting){
			ExchangeWorldSettinga((ExchangeWorldSetting)source);
		} else if(source instanceof LinkedHashMap){
			ExchangeWorldSettinga((LinkedHashMap)source);
		}

	}
	
	private void ExchangeWorldSettinga(ExchangeWorldSetting source){
		WorldType = source.WorldType;
		ItemList = source.ItemList;
		HashMap<?,?> ChestRoomsobject = source.ChestRooms;
		ChestRooms = new HashMap<String, RoomLocation>();
		for(Object key: ChestRoomsobject.keySet()){
			if(!(key instanceof String))continue;
			Object valueobject = ChestRoomsobject.get(key);
			RoomLocation loc = new RoomLocation(valueobject);
			ChestRooms.put((String)key, loc);
		}
		CheckVariables();
	}
	
	private void ExchangeWorldSettinga(LinkedHashMap source){
		WorldType = (String)source.get("WorldType");
		ItemList = (String)source.get("ItemList");
		HashMap<String, Object> ChestRoomsobject = (HashMap<String, Object>)source.get("ChestRooms");
		for(String key: ChestRoomsobject.keySet()){
			ChestRooms.put(key, new RoomLocation(ChestRoomsobject.get(key)));
		}
		CheckVariables();
	}
	
	private void CheckVariables(){
		if(WorldType == null) WorldType = "";
		if(ItemList == null){
			ItemList = "default";
		}
		if(ChestRooms == null)ChestRooms = new HashMap<String, RoomLocation>();
	}
	
	public ExchangeWorldSetting(){
		WorldType = "";
		ItemList = "default";
		ChestRooms = new HashMap<String, RoomLocation>();
	}
	
	public static ExchangeWorldSetting parse(Object object){
		return new ExchangeWorldSetting(object);
	}
}
