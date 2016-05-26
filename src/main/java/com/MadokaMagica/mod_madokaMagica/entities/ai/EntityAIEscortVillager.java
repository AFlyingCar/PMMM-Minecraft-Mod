package com.MadokaMagica.mod_madokaMagica.entities.ai;

import java.util.List;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIFollowEntity;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

public class EntityAIEscortVillager extends EntityAIBase {
	private final EntityPMWitchMinion entity;
	public EntityAIEscortVillager(EntityPMWitchMinion e){
		entity = e;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute(){
		// Holy shit!
		return entity.getTargets() != null && entity.getTargets().get(0) instanceof EntityVillager && !entity.isHome();
	}

	@Override
	public void startExecuting(){ }

	@Override
	public boolean continueExecuting(){
		boolean ishome = entity.isHome();
		if(ishome){
			// we can assume the target is a villager now (we couldn't start executing if it wasn't)
			List<EntityVillager> lev = entity.getTargets();
			entity.clearTargets();
			for(EntityVillager villager : lev){
				// I don't think anything bad will happen if it returns null...
				villager.tasks.removeTask(findBewitchedAI(villager.tasks.taskEntries));
			}
		}
		return !(ishome);
	}

	public EntityAIFollowEntity findBewitchedAI(List entries){
		// List tasks = villager.tasks.taskEntries;
		List tasks = entries; 
		for(Object taskEntry : tasks){
			EntityAITasks.EntityAITaskEntry entry = (EntityAITasks.EntityAITaskEntry)taskEntry;
			if(entry.action instanceof EntityAIFollowEntity)
				return (EntityAIFollowEntity)(entry.action);
		}
		return null;
	}
}
