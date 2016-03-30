package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.util.Wish;

public class MadokaMagicaPuellaMagiTransformationEvent extends Event{
    public PMDataTracker tracker;
    public Wish wish;

    public MadokaMagicaPuellaMagiTransformationEvent(PMDataTracker tracker,Wish wish){
        super();
        this.tracker = tracker;
        this.wish = wish;
    }

    public MadokaMagicaPuellaMagiTransformationEvent(){
        super();
    }

    @Override
    public boolean isCancelable(){
        return true;
    }
}

