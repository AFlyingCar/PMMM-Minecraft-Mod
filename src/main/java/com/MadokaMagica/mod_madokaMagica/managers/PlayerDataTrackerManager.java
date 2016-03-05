package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
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

    public void saveAndRemoveTracker(PMDataTracker pmdt){
        this.saveTracker(pmdt);
        if(datatrackers.containsValue(pmdt))
            return;
    }

    public void saveTracker(PMDataTracker pmdt){
        NBTTagCompound tags = pmdt.player.getEntityData();
        tags.setFloat("PM_POTENTIAL",pmdt.getPotential());
        tags.setFloat("PM_HERO_SCORE",pmdt.getHeroScore());
        tags.setFloat("PM_VILLAIN_SCORE",pmdt.getVillainScore());
        tags.setFloat("PM_AGGRESSIVE_SCORE",pmdt.getAggressiveScore());
        tags.setFloat("PM_PASSIVE_SCORE",pmdt.getPassiveScore());
        tags.setFloat("PM_NATURE_SCORE",pmdt.getNatureScore());
        tags.setFloat("PM_DAY_SCORE",pmdt.getDayScore());
        tags.setFloat("PM_NIGHT_SCORE",pmdt.getNightScore());
        tags.setFloat("PM_ENGINEERING_SCORE",pmdt.getEngineeringScore());
        tags.setFloat("PM_ARCHITECT_SCORE",pmdt.getArchitectScore());
        tags.setFloat("PM_GREED_SCORE",pmdt.getGreedScore());
        tags.setFloat("PM_PLAYER_STATE",pmdt.getPlayerState());
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
