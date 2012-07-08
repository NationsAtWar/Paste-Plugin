package org.nationsatwar.paste.threads;

import org.bukkit.plugin.PluginBase;
import org.nationsatwar.paste.Paste;

public class ReloadThread implements Runnable {
	private PluginBase plugin;
	
	/**
	 * 
	 * @param name Name for the thread -- this same value must be given to the 
	 * @param delay
	 */
	public ReloadThread(PluginBase instance) {
		super();
		
		plugin = instance;
		

	}

	@Override
	public void run() {
		if(plugin instanceof Paste) {
			((Paste) plugin).reload(null);
		}
	}
}
