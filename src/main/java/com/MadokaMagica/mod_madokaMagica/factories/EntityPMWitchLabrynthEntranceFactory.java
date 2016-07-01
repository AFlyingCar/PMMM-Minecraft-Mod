package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;

public class EntityPMWitchLabrynthEntranceFactory {
	public static EntityPMWitchLabrynthEntrance createWitchLabrynthEntrance(LabrynthDetails details){
        // Simply use the overworld from DimensionManager (Overworld is always Dimension 0) because all LabrynthEntrance's should exist only in the overworld
		EntityPMWitchLabrynthEntrance entrance = new EntityPMWitchLabrynthEntrance(DimensionManager.getWorld(0),details);

		return entrance;
	}

	public void clearAITasks(EntityPMWitch entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}
}


