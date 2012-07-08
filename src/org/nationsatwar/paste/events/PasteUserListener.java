package org.nationsatwar.paste.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginBase;
import org.nationsatwar.paste.PasteAPI;

public class PasteUserListener implements Listener {
	
	private PluginBase plugin;

	public PasteUserListener(PluginBase instance) {
		plugin = instance;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public synchronized void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		String input = "";
		
		if (event.getMessage().length() > 4)
			input = event.getMessage().substring(0, 5);
		if (input.equalsIgnoreCase("/tell"))
			event.setCancelled(true);
	}

	@EventHandler
	public synchronized void onPlayerChat(PlayerChatEvent event) {
		//String input = event.getMessage();
	 }

	@EventHandler
	public synchronized void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();

		if(PasteAPI.confirmToken(player.getName())) {
			
		} else {
            List<String> warning = plugin.getConfig().getStringList("notokenwarning");
            for (String s : warning) {
                player.sendMessage(s);
            }
            return;
		}
	}
}
