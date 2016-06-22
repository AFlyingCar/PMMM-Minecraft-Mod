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
import net.minecraft.util.DamageSource;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithChunkBias;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithVillageBias;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIRandomTeleportPlayerOrVillager;

public class EntityPMWitchLabrynthEntrance extends EntityCreature{
    private Random rand;

    public World linkedWorldObj; // TODO: Do we need this?
    public EntityPMWitch witch;

    public EntityPMWitchLabrynthEntrance(World worldObj){
        super(worldObj);

        rand = new Random();

        setupAITasks();
    }

    private void setupAITasks(){
        this.tasks.taskEntries.clear();
        /*
        this.tasks.addTask(0,new EntityAIWanderWithChunkBias(this,
            this.worldObj.villageCollectionObj.findNearestVillage(this.chunkCoordX,
                this.chunkCoordY,
                this.chunkCoordZ,
                this.dimension
            ).getCenter(), // We are just trying to get the chunk coordinates of the village, not the village itself. So who cares if we are there yet or not.
            0.05F)); // I'm just assuming that this is a good speed. Don't quote me on it though...
        */
        this.tasks.addTask(0,new EntityAIWanderWithVillageBias(this,0.05F));
        this.tasks.addTask(0,new EntityAIRandomTeleportPlayerOrVillager(this));
    }

    @Override
    public void onEntityUpdate(){
        super.onEntityUpdate();

        // If we don't have a witch or labrynth, then check if we should kill it or not
        // If not, then we just won't do anything
        if(this.witch == null || this.linkedWorldObj == null){
            if(!MadokaMagicaConfig.killLabrynthEntranceWithoutWitchOrLabrynth){
                return;
            }else{
                setDead();
                return;
            }
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

    @Override
    public boolean attackEntityFrom(DamageSource source, float what){
        // TODO: Find out what the second parameter is supposed to represent
        return false;
    }
}
