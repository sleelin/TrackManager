package com.sleelin.TrackManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

	TrackManager TrackManager;

	/*
	 * Initialiser allowing P2Tracks data to be used in this class
	 */
	public CommandHandler(TrackManager trackManager) {
		this.TrackManager = trackManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args) {
		if (!command.getName().equalsIgnoreCase("track")) return false;
		TrackManMethods trackManMethods = new TrackManMethods(TrackManager);
		if (args.length == 0){
			showUsage(sender, "");
			return true;
		}
		if (args[0].equalsIgnoreCase("reload")){
			if (sender instanceof Player && !TrackManager.hasPerm((Player)sender, "p2.tracks.reload", sender.isOp())) {
				sender.sendMessage(TrackManager.minorColor+"["+TrackManager.majorColor+"P2Tracks"+TrackManager.minorColor+"]"+TrackManager.errorColor+" Permission Denied");
			} else {
				TrackManager.worlds.clear();
				TrackManager.defaulttracks.clear();
				TrackManager.loadConfig();				
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("add")){
			if (TrackManager.hasPerm((Player) sender, "trackman.add", sender.isOp())){
				if (!(args.length==4)){
					showUsage(sender, "add");
					return true;
				} else {
					sender.sendMessage(trackManMethods.add(args[1], args[2], args[3]));
				}
			} else {
				sender.sendMessage(trackManMethods.failMessage+"You don't have permission to do that!");
			}
		}
		if ((args[0].equalsIgnoreCase("remove"))||(args[0].equalsIgnoreCase("delete"))){
			if (TrackManager.hasPerm((Player) sender, "trackman.remove", sender.isOp())){
				if (!(args.length==3)){
					showUsage(sender, "remove");
					return true;
				} else {
					sender.sendMessage(trackManMethods.remove(args[1], args[2]));
				}
			} else {
				sender.sendMessage(trackManMethods.failMessage+"You don't have permission to do that!");
			}
		}
		if ((args[0].equalsIgnoreCase("modify"))||(args[0].equalsIgnoreCase("update"))){
			if (TrackManager.hasPerm((Player) sender, "trackman.modify", sender.isOp())){
				if (!(args.length==4)){
					showUsage(sender, "modify");
					return true;
				} else {
					sender.sendMessage(trackManMethods.modify(args[1], args[2], args[3]));
				}
			} else {
				sender.sendMessage(trackManMethods.failMessage+"You don't have permission to do that!");
			}
		}
		if (args[0].equalsIgnoreCase("default")){
			if (TrackManager.hasPerm((Player) sender, "trackman.default", sender.isOp())){
				if (!(args.length==3)){
					showUsage(sender, "default");
					return true;
				} else {
					sender.sendMessage(trackManMethods.mdefault(args[1], args[2]));
				}
			}
		}
		return false;
	}

	/**
	 * Shows the usage of the tracks command
	 * @param sender - Command sender
	 * @param command - What command to show the usage of
	 */
	private void showUsage(CommandSender sender, String command) {
		if (command.equals("add")){
			sender.sendMessage("usage: /track add [trackname] [world] [groups (comma separated)]");
		} else if (command.equals("remove")){
			sender.sendMessage("usage: /track remove [trackname] [world]");
		} else if (command.equals("modify")){
			sender.sendMessage("usage: /track modify [trackname] [world] [groups (comma separated)]");
		} else if (command.equals("default")){
			sender.sendMessage("usage: /track default [world] [groups (comma separated)]");
		} else {
			sender.sendMessage("usage: /track [add|remove|modify]");
		}
	}

}
