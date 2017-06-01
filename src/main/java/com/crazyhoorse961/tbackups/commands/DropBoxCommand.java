package com.crazyhoorse961.tbackups.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.crazyhoorse961.tbackups.TethaBackup;

public class DropBoxCommand implements CommandExecutor{
	
	private final TethaBackup plugin;
	
	public DropBoxCommand(TethaBackup plugin) {
		this.plugin = plugin;
		this.plugin.getCommand("dropbox").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("dropbox")){
			return true;
		}
		switch(args.length){
		case 0:
			sender.sendMessage(ChatColor.GREEN + "Insert your access token via /dropbox <token> - Grab the token here: " + ChatColor.YELLOW + TethaBackup.getTetha().getDropBox().generateURLToken());
			return true;
		case 1:
			TethaBackup.getTetha().getDropBox().validate(args[0]);
			sender.sendMessage(ChatColor.GREEN + "Inserted token. Check your console log tho see if the token was accepted (No errors = Token should be accepted)");
		}
		return true;
	}

}
