package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class PlayerDataTrackerManager{
	private HashMap<String,PMDataTracker> datatrackers;
	private static PlayerDataTrackerManager instance;

	private PlayerDataTrackerManager(){
		datatrackers = new HashMap<String,PMDataTracker>();
	}

	public void addDataTracker(PMDataTracker tracker){
        datatrackers.put(""+tracker.player.getDisplayName(),tracker);
	}

	public PMDataTracker getTrackerByPlayer(EntityPlayer p){
        if(datatrackers.containsKey(p.getDisplayName()))
            return datatrackers.get(""+p.getDisplayName());
        return null;
        /*
		for(PMDataTracker tracker : datatrackers)
			if(tracker.getPlayer() == p)
				return tracker;
		return null;
        */
	}

    public PMDataTracker getTrackerByUsername(String name){
        if(datatrackers.containsKey(name))
            return datatrackers.get(name);
        return null;
    }

	public void manage(){
		for(Entry<String,PMDataTracker> trackerset : datatrackers.entrySet())
			trackerset.getValue().updateData();
	}

	public static PlayerDataTrackerManager getInstance(){
		if(PlayerDataTrackerManager.instance == null)
			PlayerDataTrackerManager.instance = new PlayerDataTrackerManager();
		return PlayerDataTrackerManager.instance;
	}
}
