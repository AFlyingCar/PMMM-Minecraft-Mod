package com.MadokaMagica.mod_madokaMagica.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.village.Village;

import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithChunkBias;

public class EntityAIWanderWithVillageBias extends EntityAIWanderWithChunkBias{
    // updateSpeed = number of ticks in between checking for the nearest village
    public int updateSpeed;
    private int currentTick=0;
    protected Village village;

    public EntityAIWanderWithVillageBias(EntityCreature e,double s){
        super(e,null,s);
        this.updateSpeed = 100;
    }

    public EntityAIWanderWithVillageBias(EntityCreature e, double s, int updateSpeed){
        super(e,null,s);
        this.updateSpeed = updateSpeed;
    }

    @Override
    public boolean shouldExecute(){
        if(this.currentTick%this.updateSpeed == 0)
            checkForNewVillage();
        this.currentTick++;
        return super.shouldExecute();
    }

    private void checkForNewVillage(){
        Village vobj = this.entity.worldObj.villageCollectionObj.findNearestVillage(this.entity.chunkCoordX,
                this.entity.chunkCoordY,
                this.entity.chunkCoordZ,
                this.entity.dimension
        );
        if(vobj != this.village){
            this.village = vobj; // We can just throw out the old one, since it should still be held by the VillageCollection object
            this.chunk = vobj.getCenter();
        }
    }

    public int getCurrentTick(){
        return currentTick;
    }

    public Village getVillage(){
        return village;
    }
}

