package com.MadokaMagica.mod_madokaMagica.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;


public class EntityAIFollowEntity extends EntityAIBase {
	private final EntityCreature entity;
	private PathNavigate pathfinder;
	private double speed;
	private float maxSQDistance;

	public EntityLiving target;

	public EntityAIFollowEntity(EntityCreature e, EntityLiving target, double speed, float maxDistanceSquared){
		super();
		this.entity = e;
		this.target = target;
		this.speed = speed;
		this.pathfinder = e.getNavigator();
		this.maxSQDistance = maxDistanceSquared;
		setMutexBits(3);
	}

	@Override
	public boolean shouldExecute(){
		// Don't start executing if they aren't even in the same dimension
		return target != null && target.dimension == entity.dimension;
	}

	@Override
	public void startExecuting(){}

	@Override
	public boolean continueExecuting(){
		if(!this.pathfinder.tryMoveToEntityLiving(this.target,this.speed)){
			if(this.entity.getDistanceSqToEntity(this.target) >= maxSQDistance){
				this.target = null;
			}
		}
		// stop executing if the entities are no longer in the same dimension
		if(target.dimension != entity.dimension)
			this.target = null;
		return target != null;
	}
}
