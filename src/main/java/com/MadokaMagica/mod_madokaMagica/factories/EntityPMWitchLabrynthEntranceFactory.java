package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.World;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;

public class EntityPMWitchLabrynthEntranceFactory {
	public EntityPMWitchLabrynthEntrance createWitchLabrynthEntrance(LabrynthDetails details){
		// TODO: Finish this method
		EntityPMWitchLabrynthEntrance entrance = new EntityPMWitchLabrynthEntrance(details);

		// TODO: do something to create the model
		return entrance;
	}

	public void clearAITasks(EntityPMWitch entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}
}


