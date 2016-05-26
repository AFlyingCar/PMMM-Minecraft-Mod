package com.MadokaMagica.mod_madokaMagica.entities;

import java.util.Random;
import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import cpw.mods.fml.common.registry.GameRegistry;

import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithChunkBias;

public class EntityPMWitchLabrynthEntrance extends EntityCreature{
    private Random rand;

    public World linkedWorldObj; // TODO: Do we need this?
    public EntityPMWitch witch;

    // All entities within 15 blocks have the possibility of being teleported
    public static double MAX_TELEPORT_DISTANCE = 15.0;
    public static double MIN_TELEPORT_CHANCE = 0.05; // 5%

    public EntityPMWitchLabrynthEntrance(World worldObj){
        super(worldObj);

        setupAITasks();
    }

    private void setupAITasks(){
        this.tasks.taskEntries.clear();
        this.tasks.addTask(0,new EntityAIWanderWithChunkBias(this,
            this.worldObj.villageCollectionObj.findNearestVillage(this.chunkCoordX,
                this.chunkCoordY,
                this.chunkCoordZ,
                this.dimension
            ).getCenter(), // We are just trying to get the chunk coordinates of the village, not the village itself. So who cares if we are there yet or not.
            0.05F)); // I'm just assuming that this is a good speed. Don't quote me on it though...
    }

    @Override
    public void onEntityUpdate(){
        super.onEntityUpdate();

        double chance = MIN_TELEPORT_CHANCE;

        // Get value
        double val = rand.nextDouble();

        // Don't even bother if the chance is less than what is possible
        if(val > chance) return;

        AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(this.posX-MAX_TELEPORT_DISTANCE,
            this.posY-MAX_TELEPORT_DISTANCE,
            this.posZ-MAX_TELEPORT_DISTANCE,
            this.posX+MAX_TELEPORT_DISTANCE,
            this.posY+MAX_TELEPORT_DISTANCE,
            this.posZ+MAX_TELEPORT_DISTANCE);

        // Get all nearby living entities
        List entities = this.worldObj.getEntitiesWithinAABB(EntityLiving.class,boundingBox);

        for(Object e : entities){
            Entity entity = (EntityLiving)e;

            // Only do anything if the entity is a player or villager. No monsters or animals
            if(entity instanceof EntityPlayer){
                // Basically, their potential as a percentage
                PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)entity);
                chance += (double)(tracker.getPotential()/100F);
                chance *= ((tracker.isPuellaMagi()) ? 0 : 1); // Puella Magi cannot be randomly sucked into a labrynth
            }else if(entity instanceof EntityVillager){
                // TODO: Maybe put this in an AI Task?
                chance += (double)(rand.nextInt(20)/100.0); // Anywhere from a 5% chance, to a 25% chance
            }else{
                continue;
            }

            // Transport the entity if the chance is high enough
            if(val < chance)
                this.teleportEntity(entity);
        }
    }

    @Override
    protected void entityInit(){
        super.entityInit();
    }

    // This method was almost completely shamelessly copied from StevenRS11's DimensionalDoors mod. Specifically, DDTeleporter.java
    // Not exactly copied though, since we don't need to worry about everything that StevenRS11 did.
    public void teleportEntity(Entity entity){
        System.out.println("WARNING! This method has not been finished yet. It requires Labrynths to be at least partially coded first!");
        // This is in place to trick the compiler
        if(true)
            return;



        if(entity == null){
            throw new IllegalArgumentException("entity is null.");
        }

        // TODO: Figure out if WorldServer really does extend from World, or if the documentation lied again
        WorldServer old = (WorldServer)entity.worldObj;// (entity.worldObj instanceof WorldServer) ? (WorldServer)entity.worldObj : entity.worldObj;
        WorldServer nwo = LabrynthManager.getInstance().loadLabrynth(this.witch);
        EntityPlayerMP player = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP)entity : null;

        // Don't teleport both entities, make sure that the rider gets unmounted
        if(entity.riddenByEntity != null){
            entity.riddenByEntity.mountEntity(null); // unmount
        }

        if(player != null){
            // TODO: Find a way to set this value correctly
            // player.dimension = ?;

            // Sanity check
            old.getPlayerManager().removePlayer(player);
            nwo.getPlayerManager().addPlayer(player);
            ChunkCoordinates coords = this.witch.worldObj.provider.getRandomizedSpawnPoint();
            player.setPositionAndUpdate(coords.posX,coords.posY,coords.posZ);
        }

        nwo.spawnEntityInWorld(entity);
        entity.setWorld(nwo);
        
        entity.worldObj.updateEntityWithOptionalForce(entity,false);

        if(player != null){
            // Load the chunk that we are going to spawn in
            nwo.getChunkProvider().loadChunk(MathHelper.floor_double(entity.posX) >> 4,MathHelper.floor_double(entity.posZ) >> 4);

            // TODO: The Dimensional Doors mod calls this, but according to Forge, this method doesn't exist. I checked, and it doesn't. So I don't know what StevenRS11 was trying to do, but I can't copy it.
            // GameRegistry.onPlayerChangedDimension((EntityPlayer)entity);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound rootTag){
        // TODO: Finish this method
        super.writeEntityToNBT(rootTag);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound rootTag){
        // TODO: Finish this method
        super.readEntityFromNBT(rootTag);
    }
}