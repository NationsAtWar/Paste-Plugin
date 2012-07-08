package org.nationsatwar.paste.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.nationsatwar.paste.PasteAPI;

/**
 * @author Aculem, Jerik
 */

public class SetToken extends NationsCommand {
	
	public SetToken(Player commandSender, String[] command) {
		super(commandSender, command);
	} // SetToken()
	
	@Override
	public void run() {
		if(commandSender == null) {
			plugin.getLogger().info("commandsender is null, can't token");
		}
		// -token (String: tokenString)
		if (command.length == 0 || command[0].equalsIgnoreCase("Help")) {
			commandSender.sendMessage(ChatColor.DARK_RED + "["+ plugin.getName() +"]" + ChatColor.DARK_AQUA + " -=[TOKEN]=-");
			commandSender.sendMessage(ChatColor.DARK_RED + "["+ plugin.getName() +"]" + ChatColor.GREEN + " e.g. '/token [token]'");
			commandSender.sendMessage(ChatColor.YELLOW + "A token is needed to link your "+ plugin.getConfig().getString("community") +" account with your Minecraft " +
					"account. Required to use some commands.");
			return;
		}
		
		String newToken = command[0];//this.connectStrings(command, 1, command.length);
	
		for (int i=0; i<newToken.length(); i++) {
			char checkChar = newToken.charAt(i);
			if (!Character.isLetterOrDigit(checkChar)) {
				commandSender.sendMessage(ChatColor.RED + "Invalid token: " + ChatColor.YELLOW + "Tokens can only contain " +
						"letters and numbers");
				return;
			}
		}
		
		commandSender.sendMessage(ChatColor.GREEN + "Valid token: " + ChatColor.YELLOW + "Your new token is: " + ChatColor.DARK_GREEN + 
				newToken + ChatColor.YELLOW + ". Link your account at "+ plugin.getConfig().getString("urlregistration") +" to register.");
		
		
		Player player = (Player) commandSender;
		
		PasteAPI.setToken(player.getLocation(), player.getName(), newToken);
	}
}
