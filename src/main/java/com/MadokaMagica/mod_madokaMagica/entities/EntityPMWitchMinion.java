package com.MadokaMagica.mod_madokaMagica.entities;

import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityFlying;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.World;

// We don't actually know if the minion can fly or not.
// It's all up in the air
public class EntityPMWitchMinion extends EntityFlying implements IMob{
    // Minions can be default mobs
    public boolean isDefaultMob;
    public EntityLiving mob;
	public EntityPMWitchMinion(World w){
		super(w);
        isDefaultMob = false;
        mob = this;
	}
    public EntityPMWitchMinion(World w,EntityLiving mob){
        super(w);
        this.isDefaultMob = true;
        this.mob = mob;
    }
    public void setExperienceLevel(int level){
        this.experienceValue = level;
    }

    public void setChaseTicks(int ticks){
        this.numTicksToChaseTarget = ticks;
    }
    public EntityLiving getMob(){
        return mob;
    }

    public Class<? extends EntityLiving> getMobType(){
        return mob.getClass();
    }
    
    // I don't think it matters
    // Everybody can be pushed
    @Override
    public boolean canBePushed(){
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
        super.onEntityUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound rootTag){
        super.writeEntityToNBT(rootTag);
        rootTag.setBoolean("IsDefaultMob",this.isDefaultMob);
        if(this.isDefaultMob){
            NBTBase mobtag = new NBTTagCompound();
            this.mob.writeEntityToNBT((NBTTagCompound)mobtag);
            rootTag.setTag("DefaultMob",mobtag);
            // Have some way of storing klass as a byte array
            // Or something else.
            // Basically, we need to somehow write this.mob's class to rootTag
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound rootTag){
        super.readEntityFromNBT(rootTag);
        this.isDefaultMob = rootTag.getBoolean("IsDefaultMob");
        if(this.isDefaultMob){
            // Class<? extends EntityLiving> klass = 
            // this.mob = new klass(this.worldObj);
            // this.mob.readEntityFromNBT(rootTag);
        }else{
            this.mob = this;
        }
    }
}

