package org.nationsatwar.paste;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.nationsatwar.paste.Request.RequestStatus;
import org.nationsatwar.paste.Request.RequestType;

/* Things in here should be exposed for use in other plugins. */
public class PasteAPI {

	public String getUserClass(String username) {
		return null;
	}
	
	public ArrayList<String> getUserPermissions(String username) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean setToken(String username, String token) {
		Paste plugin = (Paste) Bukkit.getServer().getPluginManager().getPlugin("Paste");
		
		JSONObject data = new JSONObject();
		data.put("token", token);
		data.put("username", username);

		return plugin.request.sendRequest(new Request(0, RequestType.SETTOKEN, RequestStatus.UNSENT, username, data));
	}
	
	@SuppressWarnings("unchecked")
	public static boolean setToken(Location loc, String username, String token) {
		Paste plugin = (Paste) Bukkit.getServer().getPluginManager().getPlugin("Paste");
		
		JSONObject data = new JSONObject();
		data.put("token", token);
		data.put("username", username);
		data.put("location", loc.toString());

		return plugin.request.sendRequest(new Request(0, RequestType.SETTOKEN, RequestStatus.UNSENT, username, data));
	}
	
	public static boolean confirmToken(String username) {
		Paste plugin = (Paste) Bukkit.getServer().getPluginManager().getPlugin("Paste");		

		String defaultgroup = plugin.getConfig().getString("tokengroup");
		String defaultworld = plugin.getConfig().getString("tokenworld");
		if(defaultgroup == null) {
			defaultgroup = "default";
		}
		if(defaultworld != null && defaultworld.equalsIgnoreCase("false")) {
			defaultworld = null;
		}
		if(plugin.perms.playerInGroup(defaultworld, username, defaultgroup)) {
			return true;
		}
		
		Request request = plugin.request.getResponse(new Request(0, RequestType.CONFIRMTOKEN, null, username, null));
		
		if(request == null) {
			return false;
		}
		JSONObject result = request.Data;
		if(result == null) {
			return false;
		}
		if(!((String) result.get("status")).equalsIgnoreCase("confirmed")) {
			return false;
		}
		//check to see if result = success here.
		if(plugin.perms.playerAddGroup(defaultworld, username, defaultgroup)) {
			plugin.getServer().getPlayerExact(username).sendMessage("Your token has been confirmed.");
			request.Status = RequestStatus.COMPLETED;
			plugin.request.setRequestStatus(request);
			return true;
		} else {
			plugin.getServer().getPlayerExact(username).sendMessage("Something went wrong with your token.");
			plugin.getLogger().warning("Couldn't add "+username+" to "+defaultgroup+".");
			request.Status = RequestStatus.ERROR;
			plugin.request.setRequestStatus(request);
			return false;
		}
	}
}
