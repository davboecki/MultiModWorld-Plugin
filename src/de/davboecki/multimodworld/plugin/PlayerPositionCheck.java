package de.davboecki.multimodworld.plugin;

import org.bukkit.Location;

import org.bukkit.entity.Player;


public class PlayerPositionCheck {
    PrivatChest plugin;

    PlayerPositionCheck(PrivatChest pplugin) {
        plugin = pplugin;
    }

    public boolean PlayerinRoom(Player player) {
    	if(player.getWorld().getGenerator() != plugin.Worldgen) return false;
    	if(!player.hasPermission("privatchest.hasRoom")) return false;
    	if(!plugin.RoomControl.playerhasRoom(player)) return false;
        Location playerloc = player.getLocation();
        RoomLocation roomloc = plugin.RoomControl.getRoomlocation(player);

        if (
            //X-Pos
            ((playerloc.getX() > (roomloc.getX() * 7)) &&
                (playerloc.getX() < ((roomloc.getX() * 7) + 6)))) {

            //Y-Pos
            if ((playerloc.getY() > 1) && (playerloc.getY() < 4)) {

                //Z-Pos
                if ((playerloc.getZ() > (roomloc.getZ() * 7)) &&
                        (playerloc.getZ() < ((roomloc.getZ() * 7) + 6))) {

                    return true;
                }
            }
        }

        return false;
    }

    public boolean PlayerinLobby(Player player) {
        Location playerloc = player.getLocation();

        //Lobby Normal
        if ((playerloc.getX() > 2) && (playerloc.getX() < 11)) {
            if ((playerloc.getY() > 7) && (playerloc.getY() < 14)) {
                if ((playerloc.getZ() > 2) && (playerloc.getZ() < 11)) {
                    return true;
                }
            }
        } //Lobby Mod

        if ((playerloc.getX() > 18) && (playerloc.getX() < 27)) {
            if ((playerloc.getY() > 7) && (playerloc.getY() < 14)) {
                if ((playerloc.getZ() > 2) && (playerloc.getZ() < 11)) {
                    return true;
                }
            }
        }

        return false;
    }
}
