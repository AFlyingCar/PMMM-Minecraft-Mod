package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.effects.PMEffects;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class PlayerDataTrackerManager{
	private HashMap<String,PMDataTracker> datatrackers;
	private static PlayerDataTrackerManager instance;

	private PlayerDataTrackerManager(){
		datatrackers = new HashMap<String,PMDataTracker>();
	}

	public void addDataTracker(PMDataTracker tracker){
        datatrackers.put(""+tracker.getIdentifierName(),tracker);
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

    public PMDataTracker getTrackerByUUID(UUID uuid){
        return getTrackerByPlayer(Helper.getPlayerOnServerByUUID(uuid));
    }

    public void saveAndRemoveTracker(PMDataTracker pmdt){
        this.saveTracker(pmdt);
        if(datatrackers.containsValue(pmdt))
            return;
    }

    public void saveTracker(PMDataTracker pmdt){
        NBTTagCompound tags = pmdt.entity.getEntityData();
        pmdt.writeTags(tags);
    }

    public void saveAllTrackers(){
        for(Entry<String,PMDataTracker> trackerset : datatrackers.entrySet()){
            saveTracker(trackerset.getValue());
        }
    }

    public PMDataTracker getTrackerByUsername(String name){
        if(datatrackers.containsKey(name))
            return datatrackers.get(name);
        return null;
    }

	public void manage(){
		for(Entry<String,PMDataTracker> trackerset : datatrackers.entrySet()){
            trackerset.getValue().incrementDataTimer();

            if(trackerset.getValue().getUpdateDataTime() >= 10){
                trackerset.getValue().updateData();
                // PMEffects.applyPlayerEffects(trackerset.getValue());
            }
        }
	}

    public HashMap<String,PMDataTracker> getTrackers(){
        return datatrackers;
    }

	public static PlayerDataTrackerManager getInstance(){
		if(PlayerDataTrackerManager.instance == null)
			PlayerDataTrackerManager.instance = new PlayerDataTrackerManager();
		return PlayerDataTrackerManager.instance;
	}
}
