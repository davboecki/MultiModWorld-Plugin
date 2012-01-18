package de.davboecki.multimodworld.plugin.commandhandler;

import java.util.concurrent.Callable;

import de.davboecki.multimodworld.plugin.PrivatChest;

public abstract class CallableObjects implements Callable {
	public Object[] args;
	public PrivatChest plugin;
	
	public CallableObjects(Object[] args,PrivatChest plugin) {
		this.args = args;
		this.plugin = plugin;
	}
}
