package com.sleelin.TrackManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import net.codej.permissionsplus.PermissionsPlus;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijikokun.bukkit.Permissions.Permissions;

public class TrackManager extends JavaPlugin {
	
	public Permissions permissions = null;
	public PermissionsPlus p2 = null;
	
	private PluginManager pm;
	//private Logger log;
	public ColouredConsoleSender console = null;
	Configuration config;
	
	// Config variables
	public HashMap<String, List<Track>> worlds = new HashMap<String, List<Track>>();
	public HashMap<String, List<String>> defaulttracks = new HashMap<String, List<String>>();
	public String minorColor = "&e";
	public String majorColor = "&b";
	public String errorColor = "&c";
	
	// External interface
	public static TrackManager TrackManager = null;
	
	public final class Track {
		List<String> groups = new ArrayList<String>();
		String name = "";
	}
	
	@SuppressWarnings("static-access")
	public void onEnable() {
		pm = getServer().getPluginManager();
		console = new ColouredConsoleSender((CraftServer)getServer());
		config = new Configuration(new File("plugins/PermissionsPlus/config.yml"));
		
		permissions = (Permissions)checkPlugin("Permissions");
		p2 = (PermissionsPlus)checkPlugin("PermissionsPlus");
		
		// We now depend on Permissions, so disable here if it's not found for some reason
		if (permissions == null || !checkVersion(permissions, '3')) {
			console.sendMessage("[" + getDescription().getName() + "] Permissions plugin not found or wrong version. Disabling");
			pm.disablePlugin(this);
			return;
		}
		
		loadConfig();
		
		// Register events
		getCommand("track").setExecutor(new CommandHandler(this));
		
		// Setup external interface
		TrackManager.TrackManager = this;
		
		console.sendMessage(minorColor+"["+majorColor+getDescription().getName()+minorColor+"] v" + getDescription().getVersion() + " enabled");
	}
	
	public void onDisable() {
		console.sendMessage(minorColor+"["+majorColor+getDescription().getName()+minorColor+"] Disabled!");
	}
	
	public final void loadConfig() {
		config.load();
		minorColor = config.getString("MinorColor");
		majorColor = config.getString("MajorColor");
		errorColor = config.getString("ErrorColor");
		for (World world : getServer().getWorlds()){
			File groups = new File("plugins/Permissions/"+ world.getName() +"/groups.yml");
			if (groups.exists()){
				Configuration groupfile = new Configuration(groups);
				groupfile.load();
				List<Track> tracks = new ArrayList<Track>();
				List<String> tmptracks = groupfile.getKeys("tracks");
				for (String track : tmptracks){
					Track newtrack = new Track();
					newtrack.groups = groupfile.getKeys("tracks."+track);
					newtrack.name = track;
					tracks.add(newtrack);
				}
				worlds.put(world.getName(), tracks);
				defaulttracks.put(world.getName(), groupfile.getKeys("track"));
			}
		}		
	}
	
	/*
	 * Check if a plugin is loaded/enabled already. Returns the plugin if so, null otherwise
	 */
	private Plugin checkPlugin(String p) {
		Plugin plugin = pm.getPlugin(p);
		return checkPlugin(plugin);
	}
	
	private Plugin checkPlugin(Plugin plugin) {
		if (plugin != null && plugin.isEnabled()) {
			console.sendMessage("[iChat] Found " + plugin.getDescription().getName() + " (v" + plugin.getDescription().getVersion() + ")");
			return plugin;
		}
		return null;
	}
	
	private boolean checkVersion(Plugin plugin, char Ver) {
		return (plugin.getDescription().getVersion().charAt(0) == Ver);
	}
	
	/*
	 * Check whether the player has the given permissions.
	 */
	public boolean hasPerm(Player player, String perm, boolean def) {
		if (permissions != null) {
			return permissions.getHandler().has(player, perm);
		} else {
			return def;
		}
	}
}
