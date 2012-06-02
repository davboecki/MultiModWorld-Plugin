package de.davboecki.multimodworld.plugin;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import org.bukkit.entity.Player;


public class RoomGenerator {
    private int x;
    private int z;
    private Player player;
    private String Clearvar[][] = new String[3][7];
    PrivatChest plugin;
    String WorldName;
    
    public RoomGenerator(Player pplayer, int px, int pz,PrivatChest pplugin,String pWorldName) {
        x = px;
        z = pz;
        player = pplayer;
        plugin = pplugin;
        WorldName = pWorldName;

        
    	Clearvar[0][0] = "       ";
    	Clearvar[0][1] = "   X   ";
    	Clearvar[0][2] = " XXXXX ";
    	Clearvar[0][3] = "  XXX  ";
    	Clearvar[0][4] = " XXXXX ";
    	Clearvar[0][5] = "   X   ";
    	Clearvar[0][6] = "       ";
        
    	Clearvar[1][0] = "       ";
    	Clearvar[1][1] = " O X O ";
    	Clearvar[1][2] = " XXXXX ";
    	Clearvar[1][3] = " OXXXO ";
    	Clearvar[1][4] = " XXXXX ";
    	Clearvar[1][5] = " O X O ";
    	Clearvar[1][6] = "       ";
        
    	Clearvar[2][0] = "       ";
    	Clearvar[2][1] = "       ";
    	Clearvar[2][2] = " X   X ";
    	Clearvar[2][3] = "  XXX  ";
    	Clearvar[2][4] = " X   X ";
    	Clearvar[2][5] = "       ";
    	Clearvar[2][6] = "       ";
    	

    }

    public void generate() {
    	//Signs in Lobby
        Sign Sign_Lobby;
        Block Signa_Lobby = BlockTo(Material.WALL_SIGN,9, 10, 6);
        Signa_Lobby.setData((byte)4);
        try{
	        Sign_Lobby = (Sign) Signa_Lobby.getState();
	        Sign_Lobby.setLine(0, "PrivatChest");
	        Sign_Lobby.setLine(1, "Room");
	        Sign_Lobby.setLine(2, "From");
	        Sign_Lobby.setLine(3, "Normal Side");
	        Sign_Lobby.update();
        } catch(Exception e){
        	plugin.log.info("[Privat Chest] Error Sign in lobby. (1)");
        	e.printStackTrace();
        }
        try{
	        Block Signb_Lobby = BlockTo(Material.WALL_SIGN,9+16, 10, 6);
	        Signb_Lobby.setData((byte)4);
	        Sign_Lobby = (Sign) Signb_Lobby.getState();
	        Sign_Lobby.setLine(0, "PrivatChest");
	        Sign_Lobby.setLine(1, "Room");
	        Sign_Lobby.setLine(2, "From");
	        if(plugin.Settings.ExchangeWorlds.get(WorldName).WorldType.equalsIgnoreCase("Mod")){
	        	Sign_Lobby.setLine(3, "Mod Side");
	        } else {
	            Sign_Lobby.setLine(3, "Creative Side");
	        }
	        Sign_Lobby.update();
	    } catch(Exception e){
	    	plugin.log.info("[Privat Chest] Error Sign in lobby. (2)");
	    	e.printStackTrace();
	    }
    	//*/
    	
    	//Room
        for (int i = 0; i < 7; i++) {
            for (int h = 0; h < 3; h++) {
                BlockTo(Material.STONE, x + i, h + 2, z);
                BlockTo(Material.STONE, x + i, h + 2, z + 6);
            }
        }

        for (int i = 0; i < 7; i++) {
            for (int h = 0; h < 3; h++) {
                BlockTo(Material.STONE, x, h + 2, z + i);
                BlockTo(Material.STONE, x + 6, h + 2, z + i);
            }
        }

        for (int i = 0; i < 7; i++) {
            for (int t = 0; t < 7; t++) {
                BlockTo(Material.STONE, x + i, 5, z + t);
                BlockTo(Material.DOUBLE_STEP, x + i, 1, z + t);
            }
        }
        //Tor 1
        BlockTo(Material.STONE, x + 1, 2, z + 2);
        BlockTo(Material.STONE, x + 1, 3, z + 2);
        BlockTo(Material.STONE, x + 1, 4, z + 2);
        BlockTo(Material.STONE, x + 1, 4, z + 3);
        BlockTo(Material.STONE, x + 1, 4, z + 4);
        BlockTo(Material.STONE, x + 1, 3, z + 4);
        BlockTo(Material.STONE, x + 1, 2, z + 4);
        //Tor 2
        BlockTo(Material.STONE, x + 5, 2, z + 2);
        BlockTo(Material.STONE, x + 5, 3, z + 2);
        BlockTo(Material.STONE, x + 5, 4, z + 2);
        BlockTo(Material.STONE, x + 5, 4, z + 3);
        BlockTo(Material.STONE, x + 5, 4, z + 4);
        BlockTo(Material.STONE, x + 5, 3, z + 4);
        BlockTo(Material.STONE, x + 5, 2, z + 4);
        //Schilder
        Sign Sign;
        Block Signa = BlockTo(Material.WALL_SIGN,x + 2, 4, z + 3);
        Signa.setData((byte)5);
        try{
        	Sign = (Sign) Signa.getState();
        	Sign.setLine(0, "Normal");
        	Sign.setLine(1, "Area");
        	Sign.update();
        }catch(Exception e){}
        try{
        Block Signb = BlockTo(Material.WALL_SIGN,x + 4, 4, z + 3);
        Signb.setData((byte)4);
        Sign = (Sign) Signb.getState();
        if(plugin.Settings.ExchangeWorlds.get(WorldName).WorldType.equalsIgnoreCase("Mod")){
        Sign.setLine(0, "Modloader");
        } else {
        Sign.setLine(0, "Creative");
        }
        Sign.setLine(1, "Area");
        Sign.update();
        }catch(Exception e){}
        //Chests
        BlockTo(Material.CHEST, x + 1, 2, z + 1);
        BlockTo(Material.CHEST, x + 1, 2, z + 5);
        BlockTo(Material.CHEST, x + 3, 2, z + 1);
        BlockTo(Material.CHEST, x + 3, 2, z + 5);
        BlockTo(Material.CHEST, x + 5, 2, z + 1);
        BlockTo(Material.CHEST, x + 5, 2, z + 5);
        //Torch Chest
        BlockTo(Material.TORCH, x + 1, 4, z + 1);
        BlockTo(Material.TORCH, x + 1, 4, z + 5);
        BlockTo(Material.TORCH, x + 3, 4, z + 1);
        BlockTo(Material.TORCH, x + 3, 4, z + 5);
        BlockTo(Material.TORCH, x + 5, 4, z + 1);
        BlockTo(Material.TORCH, x + 5, 4, z + 5);
        //Torch Tor
        BlockTo(Material.TORCH,x + 2, 4, z + 2);
        BlockTo(Material.TORCH,x + 2, 4, z + 4);
        BlockTo(Material.TORCH,x + 4, 4, z + 2);
        BlockTo(Material.TORCH,x + 4, 4, z + 4);
    	plugin.log.info("[Privat Chest] New Room Generated");
    }
    
    public void Clearroom(){
    	for(int a=0;a<3;a++){
    		for(int b=0;b<7;b++){
    			for(int c=0;c<7;c++){
    				if(Clearvar[a][b].charAt(c) == 'X'){
    					BlockToS(x+(b-6)*-1,a+2,z+c);
    				}
    			}
    		}
    	}
    }
    
    public void checkFloor(){
    	boolean regen = false;
    	for(int b=0;b<7;b++){
    		for(int c=0;c<7;c++){
    			if(BlockGet(x,2,z) != Material.DOUBLE_STEP){
    				regen = true;
    			}
    		}
    	}
    	if(regen){
    		generate();
    	}
    }
    
    public boolean BlockDestroyable(Location loc){
    	try{
    	return (Clearvar[(int)loc.getY()-2][(int)((loc.getX()-x)-6)*-1].charAt((int)loc.getZ()-z) == 'X') || (Clearvar[(int)loc.getY()-2][(int)((loc.getX()-x)-6)*-1].charAt((int)loc.getZ()-z) == 'O');
    	}catch(Exception e){return false;}
    }
    
    private Block BlockTo(Material type, int pX, int pY, int pZ) {
        Block blockToChange = player.getWorld().getBlockAt(pX, pY, pZ);
        blockToChange.setType(type);
        return blockToChange;
    }
    
    private Material BlockGet(int pX, int pY, int pZ) {
        Block blockToChange = player.getWorld().getBlockAt(pX, pY, pZ);
        return blockToChange.getType();
    }
    
    private void BlockToS(int pX, int pY, int pZ) {
    	BlockTo(Material.AIR,pX, pY, pZ);
    }
}
