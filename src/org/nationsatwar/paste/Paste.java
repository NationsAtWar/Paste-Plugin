package org.nationsatwar.paste;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.nationsatwar.paste.commands.CommandManager;
import org.nationsatwar.paste.events.PasteUserListener;
import org.nationsatwar.paste.threads.ReloadThread;

public class Paste extends JavaPlugin {
	public PastePermissions perms;
	public CommandManager command = new CommandManager(this);
	public RequestHandler request = new RequestHandler(this);
	
	public String getVersion() {
		return "0.1";
	}
	
	public void onEnable() {
		new PasteUserListener(this);
		this.perms = new PastePermissions(this);
		
		this.getConfig().options().copyDefaults(true);
		
		if(!request.testConnection()) {
			this.getPluginLoader().disablePlugin(this);
			return;
		}
		if(!request.testTable()) {
			if(!request.createTable()) {
				this.getPluginLoader().disablePlugin(this);
				return;				
			}
		}
		this.getLogger().info(this.getVersion()+ " Loaded");
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new ReloadThread(this), 20L * 60 * 60 * 4);
	}
	
	public void onDisable() {
		this.saveConfig();
		this.getLogger().info(this.getVersion()+ " Unloaded");
	}
	
	public void reload(CommandSender sender) {
		if(sender != null) {
			sender.sendMessage("Reloading Paste");
			this.reloadConfig();
		}
		if(!request.resendCache()) {
			if(sender != null) {
				sender.sendMessage("Error resending cache");
			}
			this.getLogger().log(Level.WARNING, "Error resending cache");
		}
		if(!request.clean()) {
			if(sender != null) {
				sender.sendMessage("Error cleaning database");
			}
			this.getLogger().log(Level.WARNING, "Error cleaning database");
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return command.execute(sender, cmd, commandLabel, args);
	}
	
	public void messageAll(String message) {
		this.getServer().broadcastMessage(ChatColor.DARK_RED + "["+this.getName()+"]: " + message);
	}
}