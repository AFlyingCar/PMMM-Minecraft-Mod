package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.World;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

public class EntityPMWitchLabrynthEntranceFactory {
	public EntityPMWitchLabrynthEntrance createWitchLabrynthEntrance(EntityPMWitch witch, World worldObj){
		// TODO: Finish this method
		EntityPMWitchLabrynthEntrance entrance = new EntityPMWitchLabrynthEntrance(worldObj);
		entrance.witch = witch;
	    entrance.linkedWorldObj = worldObj; // TODO: This isn't right (obviously), but I need to set it to *something*

		// TODO: do something to create the model
		return entrance;
	}

	public void clearAITasks(EntityPMWitch entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}
}


