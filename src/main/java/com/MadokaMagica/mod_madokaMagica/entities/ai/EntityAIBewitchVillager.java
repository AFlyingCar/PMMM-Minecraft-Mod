package com.MadokaMagica.mod_madokaMagica.entities.ai;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.EntityVillager;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIFollowEntity;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

public class EntityAIBewitchVillager extends EntityAIBase {
	private final EntityPMWitchMinion entity;
	private int counter;
	public EntityAIBewitchVillager(EntityPMWitchMinion e){
		entity = e;
		counter = 0;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute(){
		if((Math.random()*100) > getBewitchingChance() || counter < getCooldown())
			return false;

		if(entity.isInOrNearVillage())
			return (entity.worldObj.countEntities(EntityVillager) > 0 &&
				   ((entity.getTargets() == null) ||
				   	(entity.getTargets().size() >= entity.maxBewitchableEntities)
				   ));
	}

	@Override
	public void startExecuting(){
		counter++;
		if(entity.getTargets() == null)
			entity.targets = new ArrayList<EntityVillager>();
	}

	@Override
	public boolean continueExecuting(){
		if(entity.getTargets().size() >= entity.maxBewitchableEntities)
			return false;
		List entities = entity.worldObj.getEntitiesWithinAABB(EntityVillager,
					AxisAlignedBB.getBoundingBox(entity.posX-entity.influence,
											     entity.posY-entity.influence,
											     entity.posZ-entity.influence,
											     entity.posX+entity.influence,
											     entity.posY+entity.influence,
												 entity.posZ+entity.influence
					));

		EntityVillager ev = entities.get(Math.random()*(entities.size()-1));
		ev.tasks.addTask(0,EntityAIFollowEntity(ev,entity));
		entity.targets.add(ev);
		return false;
	}

	public float getBewitchingChance(){
		// TODO: Get the chance to bewitch a villager (this should be done through a config file or something)
		return 0.0F;
	}

	public int getCooldown(){
		// TODO: Get cooldown
		//   Maybe it is supposed to be some constant, or a setting in a config?
		return 0;
	}
}
