package com.sleelin.TrackManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import com.sleelin.TrackManager.TrackManager.Track;

public final class TrackManMethods {
	
	TrackManager TrackManager;
	
	public String successMessage;
	public String failMessage;
	
	/*
	 * Initialiser allowing P2Tracks data to be used in this class
	 * additionally sets up some prefixes for success and failure messages
	 */
	public TrackManMethods(TrackManager trackManager){
		this.TrackManager = trackManager;
		successMessage = TrackManager.minorColor+"["+TrackManager.majorColor+"P2Tracks"+TrackManager.minorColor+"] ";
		failMessage = TrackManager.minorColor+"["+TrackManager.majorColor+"P2Tracks"+TrackManager.minorColor+"] "+TrackManager.errorColor;
	}
	
	/**
	 * Adds a new track to the world's groups.yml file
	 * @param name - Name of the new track to add
	 * @param world - World to add the track to
	 * @param groups - Groups to add to the new track
	 * @return - Result of the command as a string
	 */	
	public String add(String name, String world, String groups){
		//check all parameters have been passed into the function
		if ((!TrackManager.worlds.containsKey(world))&&(!world.equals("*"))){
			return failMessage+"World not found!";
		}
		if (name.equalsIgnoreCase("")){
			return failMessage+"Track name cannot be blank!";
		}
		if (groups.equalsIgnoreCase("")){
			return failMessage+"Groups cannot be blank!";
		}
		
		//create new track to be added
		Track newtrack = TrackManager.new Track();
		//split groups into array by comma and add each to new track
		String[] newgroups = groups.split(","); 
		for (String group : newgroups){
			newtrack.groups.add(group);
		}
		newtrack.name = name;
		
		//check if adding to all worlds or not
		if (world.equals("*")){
			//if so, iterate through each one and add the track
			
			//list for storing worlds in which track already exists
			List<String> trackExists = new ArrayList<String>();
			
			//iterate through and add track
			for (World tmpworld : TrackManager.getServer().getWorlds()){
				//fetch the world's tracks
				List<Track> tracks = TrackManager.worlds.get(tmpworld.getName());
				//check whether the track already exists
				boolean exists = false;
				for (Track track : tracks){
					if (track.name.equalsIgnoreCase(name)){
						trackExists.add(tmpworld.getName());
						exists = true;
					}
				}
				//if track already exists, skip to next world
				if (exists) continue;
				
				//load the permissions group file
				File groupfile = new File("plugins/Permissions/"+tmpworld.getName()+"/groups.yml");
				if (groupfile.exists()){
					//if it exists, add the new track
					Configuration groupconfig = new Configuration(groupfile);
					groupconfig.load();
					groupconfig.setProperty("tracks."+newtrack.name, newtrack.groups);
					groupconfig.save();
				} else {
					//otherwise, throw an error
					return failMessage+"Permissions group file not found for "+tmpworld.getName()+"!"; 
				}
				
				//add the track to the internal record of tracks
				TrackManager.worlds.remove(tmpworld.getName());
				tracks.add(newtrack);
				TrackManager.worlds.put(tmpworld.getName(), tracks);
				
			}
			if (trackExists.size() == 0){
				return successMessage+"Successfully added new track "+TrackManager.majorColor+name+TrackManager.minorColor+" to "+TrackManager.majorColor+"all worlds";
			} else {
				String outstring = "";
				for (String worldname : trackExists){
					outstring = outstring + worldname +", ";
				}
				return failMessage+"Track already existed in worlds "+outstring+TrackManager.minorColor+" however was successfully added to all other worlds";
			}
		} else {
			//otherwise, simply add the track

			//fetch world's tracks
			List<Track> tracks = TrackManager.worlds.get(world);
			//check whether the track already exists
			for (Track track : tracks){
				if (track.name.equalsIgnoreCase(name)){
					//if track already exists, return error
					return failMessage+"Track already exists in "+world+"!";
				}
			}
			
			//first load the file and check it exists
			File groupfile = new File("plugins/Permissions/"+world+"/groups.yml");
			if (groupfile.exists()){
				//if so, add the new track
				Configuration groupconfig = new Configuration(groupfile);
				groupconfig.load();
				groupconfig.setProperty("tracks."+newtrack.name, newtrack.groups);
				groupconfig.save();
			} else {
				//otherwise, throw an error
				return failMessage+"Permissions group file not found for "+world+"!"; 
			}
			
			//add the track to the internal record of tracks
			TrackManager.worlds.remove(world);
			tracks.add(newtrack);
			TrackManager.worlds.put(world, tracks);
			return successMessage+"Successfully added new track "+TrackManager.majorColor+name+TrackManager.minorColor+" to world "+TrackManager.majorColor+world;
		}
	}
	
	/**
	 * Removes a track from a world's groups.yml file
	 * @param name - Name of the track to remove
	 * @param world - World to remove from
	 * @return - Result of the command as a string
	 */	
	public String remove(String name, String world){
		//check all parameters have been passed into the function
		if ((!TrackManager.worlds.containsKey(world))&&(!world.equals("*"))){
			return failMessage+"World not found!";
		}
		if (name.equalsIgnoreCase("")){
			return failMessage+"Track name cannot be blank!";
		}
		if (world.equals("*")){
			//if so, iterate through each one and remove the track
			for (World tmpworld : TrackManager.getServer().getWorlds()){
				//load the permissions group file
				File groupfile = new File("plugins/Permissions/"+tmpworld.getName()+"/groups.yml");
				if (groupfile.exists()){
					//if it exists, remove the track
					Configuration groupconfig = new Configuration(groupfile);
					groupconfig.load();
					groupconfig.removeProperty("tracks."+name);
					groupconfig.save();
				} else {
					//otherwise, throw an error
					return failMessage+"Permissions group file not found for "+tmpworld.getName()+"!"; 
				}
				
				//remove the track from the internal record of tracks
				List<Track> tracks = TrackManager.worlds.get(tmpworld.getName());
				//find the right track
				int i = 0;
				for (Track track : tracks){
					if (track.name.equalsIgnoreCase(name)){
						break;
					}
					i++;
				}
				tracks.remove(i);
				//update track record
				TrackManager.worlds.remove(tmpworld.getName());
				TrackManager.worlds.put(tmpworld.getName(), tracks);
			}
			return successMessage+"Successfully removed track "+TrackManager.majorColor+name+TrackManager.minorColor+" from "+TrackManager.majorColor+"all worlds";
		} else {
			//load the permissions group file
			File groupfile = new File("plugins/Permissions/"+world+"/groups.yml");
			if (groupfile.exists()){
				//if it exists, remove the track
				Configuration groupconfig = new Configuration(groupfile);
				groupconfig.load();
				groupconfig.removeProperty("tracks."+name);
				groupconfig.save();
			} else {
				//otherwise, throw an error
				return failMessage+"Permissions group file not found for "+world+"!"; 
			}
			
			//remove the track from the internal record of tracks
			List<Track> tracks = TrackManager.worlds.get(world);
			//find the right track
			int i = 0;
			for (Track track : tracks){
				if (track.name.equalsIgnoreCase(name)){
					break;
				}
				i++;
			}
			tracks.remove(i);
			//update track record
			TrackManager.worlds.remove(world);
			TrackManager.worlds.put(world, tracks);
			return successMessage+"Successfully removed track "+TrackManager.majorColor+name+TrackManager.minorColor+" from world "+TrackManager.majorColor+world;
		}
	}
	
	/**
	 * Modifies a track in the specified world
	 * @param name - The name of the track to modify
	 * @param world - The world to modify the track in
	 * @param groups - Comma separated list of groups to add to the track
	 * @return - Result of the command as a string
	 */
	public String modify(String name, String world, String groups){
		//check all parameters have been passed into the function
		if ((!TrackManager.worlds.containsKey(world))&&(!world.equals("*"))){
			return failMessage+"World not found!";
		}
		if (name.equalsIgnoreCase("")){
			return failMessage+"Track name cannot be blank!";
		}
		if (groups.equalsIgnoreCase("")){
			return failMessage+"Groups cannot be blank!";
		}
		
		//create new track to be added
		Track newtrack = TrackManager.new Track();
		//split groups into array by comma and add each to new track
		String[] newgroups = groups.split(","); 
		for (String group : newgroups){
			newtrack.groups.add(group);
		}
		newtrack.name = name;
		
		//check if adding to all worlds or not
		if (world.equals("*")){
			//if so, iterate through each one and add the track
			
			//iterate through and add track
			for (World tmpworld : TrackManager.getServer().getWorlds()){				
				//load the permissions group file
				File groupfile = new File("plugins/Permissions/"+tmpworld.getName()+"/groups.yml");
				if (groupfile.exists()){
					//if it exists, update the track
					Configuration groupconfig = new Configuration(groupfile);
					groupconfig.load();
					groupconfig.setProperty("tracks."+newtrack.name, newtrack.groups);
					groupconfig.save();
				} else {
					//otherwise, throw an error
					return failMessage+"Permissions group file not found for "+tmpworld.getName()+"!"; 
				}
				
				//update the track in the internal record of tracks
				List<Track> tracks = TrackManager.worlds.get(tmpworld.getName());
				TrackManager.worlds.remove(tmpworld.getName());
				tracks.add(newtrack);
				TrackManager.worlds.put(tmpworld.getName(), tracks);
				
			}
			return successMessage+"Successfully updated track "+TrackManager.majorColor+name+TrackManager.minorColor+" in "+TrackManager.majorColor+"all worlds";
		} else {
			//otherwise, simply add the track

			//first load the file and check it exists
			File groupfile = new File("plugins/Permissions/"+world+"/groups.yml");
			if (groupfile.exists()){
				//if so, add the new track
				Configuration groupconfig = new Configuration(groupfile);
				groupconfig.load();
				groupconfig.setProperty("tracks."+newtrack.name, newtrack.groups);
				groupconfig.save();
			} else {
				//otherwise, throw an error
				return failMessage+"Permissions group file not found for "+world+"!"; 
			}
			
			//add the track to the internal record of tracks
			List<Track> tracks = TrackManager.worlds.get(world);
			TrackManager.worlds.remove(world);
			tracks.add(newtrack);
			TrackManager.worlds.put(world, tracks);
			return successMessage+"Successfully updated track "+TrackManager.majorColor+name+TrackManager.minorColor+" in world "+TrackManager.majorColor+world;
		}
	}

}
