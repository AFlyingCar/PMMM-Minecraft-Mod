package com.MadokaMagica.mod_madokaMagica.util;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.Math;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.village.Village;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

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

    public static boolean isEntityUnderground(Entity e){
        int i = MathHelper.floor_double(e.posX);
        int j = MathHelper.floor_double(e.posY);
        int k = MathHelper.floor_double(e.posZ);

        // Is the dimension not the Nether
        // Is the player lower than level 48 
        // Is the block the player is exposed to not exposed to the sky
        return (!e.worldObj.provider.isHellWorld) && e.posY <= 48 && (!e.worldObj.canBlockSeeTheSky(i,j,k));
    }

    public static boolean isEntityOutside(Entity e){
        int i = MathHelper.floor_double(e.posX);
        int j = MathHelper.floor_double(e.posY);
        int k = MathHelper.floor_double(e.posZ);

        return e.worldObj.canBlockSeeTheSky(i,j,k);
    }

    public static boolean doesArrayContain_address(Object[] arr,Object obj){
        for(Object o : arr)
            if(o==obj) return true;
        return false;
    }

    public static boolean doesArrayContain_equals(Object[] arr,Object obj){
        for(Object o : arr)
            if(o.equals(obj)) return true;
        return false;
    }

    public static boolean isItemOre(ItemStack it){
        return doesArrayContain_equals(OreDictionary.getOreNames(),it.getDisplayName());
    }

    // This method is beautiful
    public static int[] HexToRGB(int hex){
        return new int[] {(hex>>16),(hex>>8)&0xFF,hex&0xFF};
    }

    public static int RGBToHex(int[] rgb){
        // TODO: We should really throw some sort of error here
        if(rgb.length < 3) return -1;
        return ((rgb[0]<<16)|(rgb[1]<<8)|rgb[2]);
    }

    public static IntBuffer getScreenPixels(int type, int format, int amt_multiplier){
        ScaledResolution sres = new ScaledResolution(Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        // http://wiki.lwjgl.org/index.php?title=Taking_Screen_Shots
        //int bpp = 4; // I guess we need this too, otherwise amt is too small. Actually I guess we can ignore it for now
        int amt = sres.getScaledWidth()*sres.getScaledHeight()*amt_multiplier;
        // IntBuffer buf = IntBuffer.allocate(amt);
        // Need to do this weird hoop thing because LWJGL says that the original line was "not direct"
        // I still don't understand what is wrong, as google turned up almost no results on the subject
        // The only reason this got fixed is because JBullet had a problem a while ago as well.
        // IntBuffer buf = ByteBuffer.allocateDirect(amt).asIntBuffer();

        ByteBuffer buf = ByteBuffer.allocateDirect(amt);

        // NOTE: I have no idea what some of these values mean to glReadPixels (or to glDrawPixels for that matter)
        // By 'these values', I of course mean things like GL_STENCIL_INDEX and GL_UNSIGNED_BYTE
        // Looks like I fixed it, but I don't know if it fucked up the values I was expecting.
        // Oh well, fingers crossed.
        GL11.glReadPixels(0,0,sres.getScaledWidth(),sres.getScaledHeight(),
                type,format,buf);
        return buf.asIntBuffer();
    }

    public static IntBuffer getScreenPixels(){
        return getScreenPixels(GL11.GL_STENCIL_INDEX,GL11.GL_UNSIGNED_BYTE,1);
    }

    public static IntBuffer getScreenPixels(int type){
        return getScreenPixels(type,GL11.GL_UNSIGNED_BYTE,1);
    }

    public static IntBuffer getScreenPixels(int type, int format){
        return getScreenPixels(type,format,1);
    }

    public static double getStandardDeviation(IntBuffer buf){
        // Get average
        int[] arr = buf.array();
        int total=0;
        for(int i:arr)
            total+=i;
        int avg = total/arr.length;
        return getStandardDeviation(buf,avg);
    }

    public static double getStandardDeviation(IntBuffer buf, double avg){
        int[] arr = buf.array();
        // Get deviations of each item
        int ntotal=0;
        for(int i:arr)
            ntotal+=Math.pow(i-avg,2);
        // Return the final deviation
        return Math.sqrt((double)(ntotal/arr.length));
    }

    /*
     * Pg(z) = 1/(stdDev*sqrt(2PI))*e^-(((z-u)^2)/2stdDev^2)
     * where z represents grey level, u represents mean (average), and stdDev represents standard deviation
     */
    public static double getGaussianNoise(int grey, double avg, double stdDev){
        double pow = -(Math.pow(grey-avg,2)/(2*stdDev*stdDev));
        double denom = stdDev*Math.sqrt(2*Math.PI);
        return (1/denom)*Math.pow(Math.E,pow);
    }

    /*
     * getMaxInArray
     * Accepts an int[]
     * Returns the index of the largest value in the array.
     */
    public static int getMaxInArray(int[] arr){
        int max = 0;
        for(int i=1;i<arr.length;i++){
            if(arr[i] > arr[max]) max = i;
        }
        return max;
    }

    /*
     * getMaxInArray
     * Accepts an float[]
     * Returns the index of the largest value in the array.
     */
    public static int getMaxInArray(float[] arr){
        int max = 0;
        for(int i=1;i<arr.length;i++){
            if(arr[i] > arr[max]) max = i;
        }
        return max;
    }

    /*
     * getSecondMaxInArray
     * Accepts a float[]
     * Returns the index of the second largest value in the array
     */
    public static int getSecondMaxInArray(float[] arr){
        int max = 0;
        int secondMax = 0;
        for(int i=1;i<arr.length;i++){
            if(arr[i] > arr[max]) max = i;
        }
        for(int i=1;i<arr.length;i++){
            if(arr[i] > arr[secondMax] && arr[i] < arr[max]) secondMax = i;
        }
        return secondMax;
    }

    public static int getLowestInArray(float[] arr){
        int low = 0;
        for(int i=1;i<arr.length;i++){
            if(arr[i] < arr[low]) low = i;
        }
        return low;
    }

    public static EntityPlayer getPlayerOnServerByUUID(UUID uuid){
        if(uuid == null)
            return null;
        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for(EntityPlayerMP player : players)
            if(player.getUniqueID().equals(uuid))
                return player;
        return null;
    }

    public static PMDataTracker generateRandomizedTracker(EntityPMWitchMinion entity){
        PMDataTracker tracker = new PMDataTracker(entity); // TODO: Pass entity to this class
        tracker.randomize();
        return tracker;
    }

    public static boolean doesPlayerHaveItemStack(EntityPlayer player, ItemStack stack){
        return player.inventoryContainer.getInventory().contains(stack);
    }

    public static boolean doesPlayerHaveItem(EntityPlayer player, Item item){
        for(Object obj : player.inventoryContainer.getInventory())
            if(((ItemStack)obj).getItem() == item) return true;
        return false;
    }

    public static boolean doesPlayerHaveItemType(EntityPlayer player, Class<? extends Item> type){
        for(Object obj : player.inventoryContainer.getInventory())
            if(type.isInstance(((ItemStack)obj).getItem())) return true;
        return false;
    }

    public static int getNextUnusedBiomeID(){
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
        for(int i=0;i<biomes.length;i++)
            if(biomes[i] == null)
                return i;
        System.out.println("Warning! Reached end of biomeList; No more biome IDs available! Everybody panic!");
        return -1; // If there are no more biome IDs left available
    }

    public static int[] getRandomBlockWithinAABB(AxisAlignedBB box,World world,Random rand){
        int randX = (int)((rand.nextDouble()*(box.maxX-box.minX))+box.minX);
        int randY = (int)((rand.nextDouble()*(box.maxY-box.minY))+box.minY);
        int randZ = (int)((rand.nextDouble()*(box.maxZ-box.minZ))+box.minZ);

        return new int[]{randX,randY,randZ};
    }

    public static int[] getRandomBlockWithinAABBOfType(AxisAlignedBB box, World world, Random rand, Block block){
        Block b = null;
        int[] point;
        int counter=0;
        int maxTryRate = (int)((box.maxX-box.minX)*
                               (box.maxY-box.minY)*
                               (box.maxZ-box.minZ)
                              );
        
        while(b == null && counter < maxTryRate){
            point = getRandomBlockWithinAABB(box,world,rand);
            if(world.getBlock(point[0],point[1],point[2]) == block)
                return point;
        }
        return null;
    }

    public static boolean getPercentageChance(double chance, Random rand){
        double num = rand.nextDouble()*100;
        return num < chance;
    }

    public static Entity getEntityFromUUID(UUID uuid,World world){
        for(Object e : world.getLoadedEntityList()){
            if(!(e instanceof Entity)){
                System.out.println("WARNING! FOUND UNKNOWN TYPE IN World#getLoadedEntityList()! ALL TYPES MUST BE OF Entity!");
                return null;
            }
            if(((Entity)e).getPersistentID() == uuid)
                return (Entity)e;
        }
        System.out.println("WARNING! Could not find Entity with persistent ID matching " + uuid);
        return null;
    }

    public static Entity getEntityFromUUID(UUID uuid){
        return getEntityFromUUID(uuid,MinecraftServer.getServer().getEntityWorld());
    }
}

