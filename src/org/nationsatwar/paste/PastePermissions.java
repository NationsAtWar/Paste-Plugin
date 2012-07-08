package org.nationsatwar.paste;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginBase;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;


//A lot of this is stolen from PermissionsBukkit
//https://github.com/SpaceManiac/PermissionsBukkit/blob/master/src/main/java/com/platymuus/bukkit/permissions/PermissionsPlugin.java

//Haha joke, i stole from here instead
//https://github.com/MilkBowl/Vault/blob/master/src/net/milkbowl/vault/permission/plugins/Permission_PermissionsBukkit.java
public class PastePermissions {
	private PluginBase plugin = null;
	
	private final String name = "PermissionsBukkit";
	private PermissionsPlugin perms = null;
	
	public PastePermissions(PluginBase instance) {
		this.plugin = instance;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (perms == null) {
			Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (perms != null) {
				perms = (PermissionsPlugin) perms;
				plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	public class PermissionServerListener implements Listener {
		PastePermissions permission = null;

		public PermissionServerListener(PastePermissions permission) {
			this.permission = permission;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (permission.perms == null) {
				Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");

				if (perms != null) {
					if (perms.isEnabled()) {
						permission.perms = (PermissionsPlugin) perms;
						plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(),	permission.name));
					}
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (permission.perms != null) {
				if (event.getPlugin().getDescription().getName()
						.equals("PermissionsBukkit")) {
					permission.perms = null;
					plugin.getLogger().info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
				}
			}
		}
	}
	
	public boolean playerAddPerm(String player, String permission, String world) {
		//Player user = plugin.getServer().getPlayer(player);
		
		//this might be the actual way to do it, but below is a much easier way.
		//user.addAttachment(plugin, group, true);
		//this.refreshPermissions();
		if (world != null) {
			permission = world + ":" + permission;
		}
		
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
	}
	
	public boolean playerRemovePerm(String player, String permission, String world) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
	}
	
	public boolean playerAddGroup(String world, String player, String group) {
		if (world != null) {
			return false;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
	}

	public boolean playerRemoveGroup(String world, String player, String group) {
		if (world != null) {
			return false;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
	}
	
	public boolean playerInGroup(String world, String player, String group) {
		if (world != null) {
			for (Group g : perms.getPlayerInfo(player).getGroups()) {
				if (g.getName().equals(group)) {
					return g.getInfo().getWorlds().contains(world);
				}
			}
			return false;
		}
		Group g = perms.getGroup(group);
		if (g == null) {
			return false;
		}
		return g.getPlayers().contains(player);
	}
}
