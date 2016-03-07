package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class MadokaMagicaPuellaMagiTransformationEvent extends Event{
    public PMDataTracker tracker;

    public MadokaMagicaPuellaMagiTransformationEvent(PMDataTracker tracker){
        super();
        this.tracker = tracker;
    }

    public MadokaMagicaPuellaMagiTransformationEvent(){
        super();
    }

    @Override
    public boolean isCancelable(){
        return true;
    }
}

