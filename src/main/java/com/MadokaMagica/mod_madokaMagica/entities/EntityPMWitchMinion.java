package com.MadokaMagica.mod_madokaMagica.entities;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;
import net.minecraft.village.Village;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.MinecraftForge;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchMinionEvolveEvent;

public class EntityPMWitchMinion extends EntityCreature{
    public boolean undead;
    private EntityPMWitchLabrynthEntrance entrance;
    private EntityPMWitch witch;
    public List<EntityVillager> targets;
    public int maxBewitchableEntities;
    public float influence; // radius around this entity that it can bewitch villagers
    public float influenceHold; // radius around this entity that it can hold onto bewitched villagers
    public boolean pushable;

	public EntityPMWitchMinion(World worldObj, EntityPMWitch witch, EntityPMWitchLabrynthEntrance entrance){
		super(worldObj);
        this.witch = witch;
        this.entrance = entrance;
        this.pushable = true;
        this.influence = 5.0F; // TODO: Find a better value for this
	}

    public void setExperienceLevel(int level){
        this.experienceValue = level;
    }

    public void setChaseTicks(int ticks){
        this.numTicksToChaseTarget = ticks;
    }

    @Override
    public boolean getCanSpawnHere(){
        return isInHomeDimension() || isHome();
    }

    public boolean isHome(){
        return hasHome() && isWithinHomeDistanceCurrentPosition();
    }

    public boolean isInHomeDimension(){
        // TODO: Finish this method
        //  It has to wait until the dimensions have been implemented
        return true;
    }

    public List<EntityVillager> getTargets(){
        return this.targets;
    }

    public void addTarget(EntityVillager villager){
        if(this.targets == null) return;
        if(this.targets.size() < this.maxBewitchableEntities)
            this.targets.add(villager);
    }

    public void clearTargets(){
        // because fuck it
        this.targets = null;
    }

    public boolean canEvolve(){
        // TODO: Finish this method.
        return false;
    }

    public boolean isInOrNearVillage(){
        Village nearestVillage = this.worldObj.villageCollectionObj.findNearestVillage(
            this.chunkCoordX,
            this.chunkCoordY,
            this.chunkCoordZ,
            this.dimension);
        ChunkCoordinates nearestVillageChunkCoords = nearestVillage.getCenter();

        // Can be 5 blocks away from a village to count as nearby
        float furthestPoint = nearestVillage.getVillageRadius() + 5.0F;

        // Entity must be within 5 blocks of the village
        return nearestVillageChunkCoords.getDistanceSquared(this.chunkCoordX,this.chunkCoordY,this.chunkCoordZ) <= furthestPoint;
    }

    @Override
    public boolean canBePushed(){
        return pushable;
    }

    @Override
    public boolean isAIEnabled(){
        return true;
    }

    @Override
    public boolean isEntityAlive(){
        return true;
    }

    @Override
    protected void entityInit(){
        super.entityInit();
    }

    @Override
    public void onEntityUpdate(){ 
        if(entrance != null)
            setHomeArea(entrance.chunkCoordX,
                        entrance.chunkCoordY,
                        entrance.chunkCoordZ,
                        entrance.dimension
                );

        if(canEvolve())
            MinecraftForge.EVENT_BUS.post( new MadokaMagicaWitchMinionEvolveEvent(this) );
        super.onEntityUpdate();
    }

    @Override
    public boolean isEntityUndead(){
        return undead;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound rootTag){
        super.writeEntityToNBT(rootTag);
        rootTag.setInteger("maxBewitchableEntities",this.maxBewitchableEntities);
        rootTag.setBoolean("pushable",this.pushable);
        // TODO: Add something here to save targets
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound rootTag){
        super.readEntityFromNBT(rootTag);
        this.maxBewitchableEntities = rootTag.getInteger("maxBewitchableEntities");
        this.pushable = rootTag.getBoolean("pushable");
    }
}

