package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class PlayerDataTrackerManager{
	private List<PMDataTracker> datatrackers;
	private static PlayerDataTrackerManager instance;

	private PlayerDataTrackerManager(){
		datatrackers = new ArrayList<PMDataTracker>();
	}

	public void addDataTracker(PMDataTracker tracker){
		datatrackers.add(tracker);
	}

	public PMDataTracker getTrackerByPlayer(EntityPlayer p){
		for(PMDataTracker tracker : datatrackers)
			if(tracker.getPlayer() == p)
				return tracker;
		return null;
	}

	public void manage(){
		for(PMDataTracker tracker : datatrackers)
			tracker.updateData();
	}

	public static PlayerDataTrackerManager getInstance(){
		if(PlayerDataTrackerManager.instance == null)
			PlayerDataTrackerManager.instance = new PlayerDataTrackerManager();
		return PlayerDataTrackerManager.instance;
	}
}
