package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.World;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIEscortVillager;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIBewitchVillager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class EntityPMWitchMinionFactory {
	public int aggressiveLevel;
	public EntityPMWitch witch;
	public EntityPMWitchLabrynthEntrance home;
	public PMDataTracker tracker;

	public EntityPMWitchMinion createMinion(World worldObj){
		EntityPMWitchMinion minion = new EntityPMWitchMinion(worldObj,witch,home,tracker);
		// choose AI
		switch(aggressiveLevel){
			case 0:
				this.setupZombieLikeAITasks(minion);
				break;
			case 1:
				// guard labrynth entrance
				this.setupGuardEntranceAITasks(minion);
				break;
			case 2:
				// guard witch
				this.setupGuardWitchAITasks(minion);
				break;
		}

		// TODO: do something to create the model

		return minion;
	}

	public void setupZombieLikeAITasks(EntityPMWitchMinion minion){
		clearAITasks(minion);
		minion.tasks.addTask(0,new EntityAIEscortVillager(minion));
		minion.tasks.addTask(1,new EntityAIMoveTowardsTarget(minion,0.05,100)); // speed and maximum distance (in blocks)
		minion.targetTasks.addTask(2,new EntityAIBewitchVillager(minion));
	}

	public void setupGuardEntranceAITasks(EntityPMWitchMinion minion){
		// TODO: Finish this method
	}

	public void setupGuardWitchAITasks(EntityPMWitchMinion minion){
		// TODO: Finish this method
	}

	public void clearAITasks(EntityPMWitchMinion entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
	}
}

