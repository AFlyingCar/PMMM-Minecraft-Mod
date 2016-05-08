package com.MadokaMagica.mod_madokaMagica.entitites;

import java.util.Random;
import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.registry.GameRegistry;

import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

class EntityPMWitchLabrynthEntrance extends Entity{
    private Random rand;

    // All entities within 15 blocks have the possibility of being teleported
    public static double MAX_TELEPORT_DISTANCE = 15.0;
    public static double MIN_TELEPORT_CHANCE = 0.05; // 5%

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
        List entities = this.worldObj.getEntitiesWithinAABB(EntityLiving,boundingBox);

        for(Object e : entities){
            EntityLiving entity = (EntityLiving)e;

            // Only do anything if the entity is a player or villager. No monsters or animals
            if(entity instanceof EntityPlayer){
                // Basically, their potential as a percentage
                PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)entity);
                chance += (double)(tracker.getPotential()/100F);
                chance *= ((tracker.isPuellaMagi()) ? 0 : 1); // Puella Magi cannot be randomly sucked into a labrynth
            }else if(entity instanceof EntityVillager){
                chance += (double)(rand.nextInt(20)/100.0); // Anywhere from a 5% chance, to a 25% chance
            }else{
                continue;
            }

            // Transport the entity if the chance is high enough
            if(val < chance)
                this.teleportEntity(entity);
        }

        // This method was almost completely shamelessly copied from StevenRS11's DimensionalDoors mod. Specifically, DDTeleporter.java
        // Not exactly copied though, since we don't need to worry about everything that StevenRS11 did.
        public void teleportEntity(Entity entity){
            System.out.println("WARNING! This method has not been finished yet. It requires Labrynths to be at least partially coded first!");
            return;



            if(entity == null){
                throw new IllegalArgumentException("entity is null.");
            }

            WorldServer old = entity.worldObj;
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
                player.setPositionAndUpdate(coords.posX,coords.posY,coords.posZ)
            }

            nwo.spawnEntityInWorld(entity);
            entity.setWorld(nwo);
            
            entity.worldObj.updateEntityWithOptionalForce(entity,false);

            if(player != null){
                // Load the chunk that we are going to spawn in
                nwo.getChunkProvider().loadChunk(MathHelper.floor_double(entity.posX) >> 4,MathHelper.floor_double(entity.posZ) >> 4);
                GameRegistry.onPlayerChangedDimension((EntityPlayer)entity);
            }
        }
    }
}
