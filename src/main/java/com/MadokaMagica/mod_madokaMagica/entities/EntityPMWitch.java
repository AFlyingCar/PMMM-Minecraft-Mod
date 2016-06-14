package com.MadokaMagica.mod_madokaMagica.entities;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

public class EntityPMWitch extends EntityMob{
	public PMDataTracker tracker;
	public EntityPMWitchLabrynthEntrance entrance;
	public EntityPMWitch(PMDataTracker pd){
		super(pd.entity.worldObj);
	}

	public EntityPMWitch(World world){
		super(world);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound base){
		// TODO: Load a tracker based on this entity's getPersistentID()
		super.readEntityFromNBT(base);
	}
}

