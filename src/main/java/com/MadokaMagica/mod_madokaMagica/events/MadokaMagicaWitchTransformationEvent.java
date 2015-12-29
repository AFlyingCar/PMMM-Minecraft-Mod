package com.MadokaMagica.mod_madokaMagica.events;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaEvent;

public class MadokaMagicaWitchTransformationEvent extends MadokaMagicaEvent {
    private ArrayList<PMDataTracker> trackers;
    // private PMDataTracker pmdata;
    private static MadokaMagicaWitchTransformationEvent instance;

    private MadokaMagicaWitchTransformationEvent(){
        trackers = new ArrayList<PMDataTracker>();
        // pmdata = pmdt;
    }

    public ArrayList<PMDataTracker> getTrackers(){
        return trackers;
    }

    public PMDataTracker getPMDataTrackerByPlayer(EntityPlayer p){
        for(PMDataTracker pmdt : trackers)
            if(pmdt.getPlayer() == p)
                return pmdt;
        return null;
    }

    public PMDataTracker getPMDataTracker(int index){
        return trackers.get(index);
    }

    @Override
    public boolean isActive(){
        return !trackers.isEmpty();
    }

    public boolean isActive(PMDataTracker pmdt){
        return trackers.contains(pmdt);
    }

    @Override
    public void cancel(){
        super.cancel();
        trackers = new ArrayList<PMDataTracker>();
        // pmdata = null;
    }

    public void cancel(EntityPlayer p){
        PMDataTracker t = this.getPMDataTrackerByPlayer(p);
        if(t != null){
            int id = trackers.indexOf(t);
            trackers.remove(id);            
        }
    }

    public void cancel(PMDataTracker pmdt){
        if(trackers.contains(pmdt))
            trackers.remove(pmdt);
    }

    public static MadokaMagicaWitchTransformationEvent getInstance(){
        if(MadokaMagicaWitchTransformationEvent.instance == null)
            MadokaMagicaWitchTransformationEvent.instance = new MadokaMagicaWitchTransformationEvent();
        return MadokaMagicaWitchTransformationEvent.instance;
    }

    public void activate(PMDataTracker pmdt){
        trackers.add(pmdt);
    }
}
