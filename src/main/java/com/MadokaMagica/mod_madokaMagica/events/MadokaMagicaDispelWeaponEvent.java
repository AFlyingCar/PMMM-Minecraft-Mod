package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class MadokaMagicaDispelWeaponEvent extends Event {
    public PMDataTracker tracker;

    public MadokaMagicaDispelWeaponEvent(PMDataTracker newTracker){
        super();
        tracker = newTracker;
    }

    public MadokaMagicaDispelWeaponEvent(){
        super();
    }

    @Override
    public boolean isCancelable(){
        return true;
    }
}

