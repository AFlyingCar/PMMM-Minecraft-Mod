package com.MadokaMagica.mod_madokaMagica.entities.ai;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIWanderWithChunkBias extends EntityAIBase {
	private double speed;
	private EntityCreature entity;
	private ChunkCoordinates chunk;

	private double x;
	private double y;
	private double z;

	public EntityAIWanderWithChunkBias(EntityCreature e,ChunkCoordinates c,double s){
		speed = s;
		chunk = c;
		entity = e;

		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute(){
		if (this.entity.getRNG().nextInt(120) != 0){
            return false;
        }else{
            Vec3 vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);

            if (vec3 == null){
                return false;
            }else{
                this.x = vec3.xCoord;
                this.y = vec3.yCoord;
                this.z = vec3.zCoord;

                this.applyBias();

                return true;
            }
        }
	}

	@Override
	public void startExecuting(){
        this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
	}

	@Override
	public boolean continueExecuting(){
        return !this.entity.getNavigator().noPath();
	}

	public void applyBias(){
		// Find difference in location
		double xdiff = Math.abs(chunk.posX - entity.posX);
		double ydiff = Math.abs(chunk.posY - entity.posY);
		double zdiff = Math.abs(chunk.posZ - entity.posZ);

		// Choose a random percentage
		double rx = Math.random();
		double ry = Math.random();
		double rz = Math.random();

		// What is the sign we need to make sure we go in the right direction
		int sign = chunk.posX < entity.posX ? -1 : 1;

		// Apply random numbers
		x += rx > 0.05F ? 0 : (rx*xdiff*sign);
		y += ry > 0.05F ? 0 : (ry*ydiff*sign);
		z += rz > 0.05F ? 0 : (rz*zdiff*sign);
	}
}
