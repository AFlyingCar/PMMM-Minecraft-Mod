package com.MadokaMagica.mod_madokaMagica.factories;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

public class EntityPMWitchFactory {
	public EntityPMWitch createWitch(PMDataTracker tracker){
		// TODO: Finish this method
		EntityPMWitch witch = new EntityPMWitch(tracker);

		// choose AI
		switch(0){
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
		}

		// TODO: do something to create the model
		return witch;
	}

	public void clearAITasks(EntityPMWitch entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}
}


