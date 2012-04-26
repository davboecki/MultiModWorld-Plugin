package de.davboecki.multimodworld.plugin;

import java.util.LinkedHashMap;

public class RoomLocation {
	
	public RoomLocation(){}
	
	public RoomLocation(long px,long pz){
		x = px;
		z = pz;
	}

	public RoomLocation(Object source){
		if(source instanceof RoomLocation){
			RoomLocationa((RoomLocation)source);
		} else if(source instanceof LinkedHashMap){
			RoomLocationa((LinkedHashMap)source);
		}
	}

	private void RoomLocationa(RoomLocation pRoomLocation){
		x = pRoomLocation.x;
		z = pRoomLocation.z;
	}
	
	private void RoomLocationa(LinkedHashMap Map){
		x = (long)((Integer)Map.get("x"));
		z = (long)((Integer)Map.get("z"));
	}
	
	public long x;
	public long z;
	public long getX(){
		return x;
	}
	public long getZ(){
		return z;
	}
}
