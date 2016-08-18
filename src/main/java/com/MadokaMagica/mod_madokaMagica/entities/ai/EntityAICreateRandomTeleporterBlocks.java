package com.MadokaMagica.mod_madokaMagica.entities.ai;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.init.Blocks;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaBlocks;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

public class EntityAICreateRandomTeleporterBlocks extends EntityAIBase {
    // All entities within 15 blocks have the possibility of being teleported
    public static final double MAX_TELEPORT_DISTANCE = 15.0;
    public static final double MIN_TELEPORT_CHANCE = 0.05; // 5%
    public static final int COOLDOWN_WAIT_TIME = 30;
    public static final int MAX_TELEPS = 5; // Maximum telporters which can be done per-cooldown

    private final EntityCreature entity;
    private AxisAlignedBB bbox;
    private int cooldown = COOLDOWN_WAIT_TIME;
    private int teleps = 0;
    private int id;
    private Random rand;

    public EntityAICreateRandomTeleporterBlocks(EntityCreature entity){
        this.entity = entity;
        this.rand = new Random();
        this.id = LabrynthManager.getInstance().storeLabrynthEntranceToRetrieveLater((EntityPMWitchLabrynthEntrance)this.entity);
        setMutexBits(2);
    }

    @Override
    public boolean shouldExecute(){
        cooldown++;
        if(cooldown >= COOLDOWN_WAIT_TIME){
            cooldown = 0;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting(){
        System.out.println("Starting execution");
        bbox = AxisAlignedBB.getBoundingBox(entity.posX-MAX_TELEPORT_DISTANCE,
                entity.posY-MAX_TELEPORT_DISTANCE,
                entity.posZ-MAX_TELEPORT_DISTANCE,
                entity.posX+MAX_TELEPORT_DISTANCE,
                entity.posY+MAX_TELEPORT_DISTANCE,
                entity.posZ+MAX_TELEPORT_DISTANCE
            );
    }

    @Override
    public boolean continueExecuting(){
        int[] blockLocation = Helper.getRandomBlockWithinAABBOfType(bbox,entity.worldObj,rand,Blocks.air);

        entity.worldObj.setBlock(blockLocation[0],blockLocation[1],blockLocation[2],MadokaMagicaBlocks.labrynthTeleporter,id,1|2);

        if(teleps++ >= MAX_TELEPS)
            return false;
        return true;
    }

    protected boolean doesAABBContainAnyEntityOfTypeList(List< Class<? extends EntityLiving> > types){
        List entities = entity.worldObj.getEntitiesWithinAABB(EntityLiving.class,bbox);

        for(Object e : entities){
            Entity entity = (EntityLiving)e;

            for(Class<? extends EntityLiving> type : types){
                if(type.isInstance(entity)) return true;
            }
        }
        return false;
    }

    protected boolean doesAABBContainAnyEntityOfType(Class<? extends EntityLiving> type){
        List entities = entity.worldObj.getEntitiesWithinAABB(EntityLiving.class,bbox);

        for(Object e : entities){
            Entity entity = (EntityLiving)e;
            if(type.isInstance(entity)) return true;
        }
        return false;
    }
}

