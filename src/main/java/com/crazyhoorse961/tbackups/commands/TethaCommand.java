package com.crazyhoorse961.tbackups.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.crazyhoorse961.tbackups.TethaBackup;

public class TethaCommand implements CommandExecutor {

	private final TethaBackup pl;
	
	public TethaCommand(TethaBackup pl){
		this.pl = pl;
		pl.getCommand("tethabackup").setExecutor(this);
		
	}
	
	public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
		switch(args.length){
		case 0:
			if(!s.hasPermission("tethabackup.help")) return true;
			s.sendMessage(ChatColor.GREEN + "TethaBackups 1.0.0 by crazyhoorse961");
			s.sendMessage(ChatColor.GREEN + "Commands: /tethabackup reload -" + ChatColor.GREEN + " Reloads the configuration file");
			s.sendMessage(ChatColor.GREEN + "/tethabackup backup - " + ChatColor.YELLOW + "Creates a backup");
			return true;
		case 1:
			switch(args[0]){
			default:
				s.sendMessage(ChatColor.RED + "Unknown argument, do /tethabackup for more help!");
				return true;
			case "reload":
				if(!s.hasPermission("tethabackup.reload")) return true;
				pl.reloadConfig();
				s.sendMessage(ChatColor.GREEN + "The configuration has been loaded with success!");
				return true;
			case "backup":
				try {
					if(!s.hasPermission("tethabackup.backup")) return true;
					s.sendMessage(ChatColor.GREEN + "Starting backup..");
					TethaBackup.getTetha().getManager().executeBackup();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
			}
		}
		return false;
	}

}
