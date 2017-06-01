package com.crazyhoorse961.tbackups.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.crazyhoorse961.tbackups.TethaBackup;

public class Sequence implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(TethaBackup.getTetha().getDropBox().sequence());
		return true;
	}
}
