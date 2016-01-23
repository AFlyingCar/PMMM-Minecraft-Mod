package com.MadokaMagica.mod_madokaMagica.util;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.lang.Math;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;

public class Helper{
    private final static long INT_ALTER_AMT = (long)(Math.pow(10,16));

    public static float timeTolerance;

    /*
     * Only keeps the first INT_ALTER_AMT decimal places
     */
    public static int packFloat(float f){
        return (int)(f*INT_ALTER_AMT);
    }

    public static int unpackFloat(int i){
        return (int)(i/INT_ALTER_AMT);
    }

    public static boolean isPlayerUnderground(EntityPlayer p){
        // Can the player see the sky?
        // Round player position up.
        if(!p.worldObj.canBlockSeeTheSky((int)(0.55+p.posX),(int)(0.55+p.posY),(int)(0.55+p.posZ))){
            // The player is not underground if they are inside a building, so...
            // if(p.)
        }
        return false;
    }

    // is the amount of entities near EntityPlayer p of EnumCreatureType type greater than or equal to int amount.
    public static boolean isPlayerNearEntitiesOfType(EntityPlayer p, EnumCreatureType type, int amount){
        // int entityAmt = p.worldObj.countEntities(type,false);
        ChunkCoordinates chunk = p.playerLocation;
        // Get all mobs in the current chunk, but only within 4 blocks of the player's Y coordinate
        /*
         * Chunk X coord
         * Player y coord - 4
         * Chunk Z coord
         */
        int total = 0;
        List mobList = p.worldObj.getEntitiesWithinAABBExcludingEntity(p,AxisAlignedBB.getBoundingBox(chunk.posX,p.posY-16,chunk.posZ,chunk.posX+16,p.posY+16,chunk.posZ+16));
        Iterator iter = mobList.iterator();

        while(iter.hasNext()){
            Entity mob = (Entity)iter.next();
            // Who gives a shit if the entity is an instance of a non-mob (like an item for instance)
            if(!(mob instanceof EntityLivingBase)) continue;
            // Is the mob of the type we want to check for?
            if(!(mob.isCreatureType(type,false))) continue;
            total++;
        }

        return total >= amount;
    }

    public static List getEntitiesInSameChunk(Entity entity){
        // Return all entities within the same chunk as entity
        ChunkCoordinates chunk = new ChunkCoordinates(entity.chunkCoordX,entity.chunkCoordY,entity.chunkCoordZ);
        return entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity,AxisAlignedBB.getBoundingBox(chunk.posX,0,chunk.posZ,chunk.posX+16,255,chunk.posZ+16));
    }

    public static List getNearbyEntitiesInSameChunk(Entity entity){
        ChunkCoordinates chunk = new ChunkCoordinates(entity.chunkCoordX,entity.chunkCoordY,entity.chunkCoordZ);
        return entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity,AxisAlignedBB.getBoundingBox(chunk.posX,entity.posY-16,chunk.posZ,chunk.posX+16,entity.posY+16,chunk.posZ+16));
    }

    public static List getNearbyEntities(Entity entity){
        return entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity,AxisAlignedBB.getBoundingBox(entity.posX-16,entity.posY-16,entity.posZ-16,entity.posX+16,entity.posY+16,entity.posZ+16));
    }

    public static boolean nearEntityRecently(Entity entity,Map<Entity,Float> lastNearbyMap){
        if(!lastNearbyMap.containsKey(entity)) return false;
        float time = lastNearbyMap.get(entity).intValue();
        float diff = entity.worldObj.getTotalWorldTime()-time;
        return diff >= timeTolerance;
    }

    public static void initStaticValues(){
        // This does stuff with configs
        // But I don't feel like working on that at the moment.
        // So this is here so that we all know that it will happen later
        // It's a placeholder

        timeTolerance = 23000; // The standard MC day
    }

    public static float getDistanceBetweenEntities(Entity e1, Entity e2){
        float xdist = Math.abs((float)(e1.posX - e2.posX));
        float zdist = Math.abs((float)(e1.posZ - e2.posZ));
        float ydist = Math.abs((float)(e1.posY - e2.posY));
        // distance on a 3d plane is:
        // sqrt(xd^2 + zd^2 + yd^2)
        // Because it throws an error if we pass it a double
        return (float)(Math.sqrt(Math.pow(xdist,2)+Math.pow(ydist,2)+Math.pow(zdist,2)));
    }

    public static boolean isPlayerInVillage(Entity e){
        // TODO: Figure out what the last parameter is
        //   I'm just assuming that it is dimension
        //   Also, find out if the first three are chunk coordinates or something else
        Village nearest = e.worldObj.villageCollectionObj.findNearestVillage(e.chunkCoordX,e.chunkCoordY,e.chunkCoordZ,e.dimension);
        // Is the player within the village's radius. Ignore Y coordinate
        return (Math.abs(nearest.getCenter().posX - e.posX) < nearest.getVillageRadius()) && (Math.abs(nearest.getCenter().posZ - e.posZ) < nearest.getVillageRadius());
    }
}
