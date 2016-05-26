package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class MadokaMagicaCreateWitchEvent extends Event {
	public PMDataTracker playerTracker;

	public MadokaMagicaCreateWitchEvent(PMDataTracker tracker){
		super();
		playerTracker = tracker;
	}

	@Override
	public boolean isCancelable(){
		return true;
	}
}
