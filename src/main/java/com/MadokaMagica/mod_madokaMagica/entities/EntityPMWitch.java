package com.MadokaMagica.mod_madokaMagica.entities;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer;

public class EntityPMWitch extends EntityMob{
    public PMDataTracker tracker;
    public EntityPMWitchLabrynthEntrance entrance;
    public boolean broken = false;

    public EntityPMWitch(PMDataTracker pd){
        super(pd.entity.worldObj);
    }

    public EntityPMWitch(World world){
        super(world);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound base){
        // TODO: Load a tracker based on this entity's getPersistentID()

        if(this.worldObj instanceof LabrynthWorldServer){
            LabrynthWorldServer world = (LabrynthWorldServer)this.worldObj;
            this.tracker = world.tracker;
            this.tracker.entity = this;
        }else{
            System.out.println("WARNING! The worldObj of " + this.getPersistentID() + " (EntityPMWitch) is not of type LabrynthWorldServer!");
            this.setDead();
            this.broken = true;
            return;
        }

        super.readEntityFromNBT(base);
    }
}

