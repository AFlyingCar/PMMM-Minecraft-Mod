package com.MadokaMagica.mod_madokaMagica.world;

import net.minecraftforge.client.IRenderHandler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.MadokaMagica.mod_madokaMagica.factories.LabrynthGeneratorFactory;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

public class LabrynthProvider extends WorldProvider{
    // The diameter of the labrynth
    public final static double MAX_LABRYNTH_WORLD_SIZE = 255.0; // TODO: Find better number than 255. Maybe generate a number?
    public final static double MIN_WITCH_DISTANCE = MAX_LABRYNTH_WORLD_SIZE/2.0;

    public boolean canSnow;
    public boolean canLightning;
    public double horizon;
    public String witchName;
    public LabrynthGeneratorFactory labrynthGeneratorFactory;
    public IRenderHandler skyRenderer;
    public EntityPMWitch owner;
    public PMDataTracker tracker;

    public LabrynthProvider(){
        this.canSnow = false;
        this.hasNoSky = false;
        this.canLightning = false;
    }

    @Override
    public boolean canSnowAt(int x,int y,int z,boolean checkLight){
        return this.canSnow;
    }

    @Override
    public boolean canBlockFreeze(int x,int y,int z,boolean byWater){
        return this.canSnow && byWater;
    }

    @Override
    public boolean canDoLightning(Chunk chunk){
        return this.canLightning;
    }

    @Override
    public boolean canRespawnHere(){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IRenderHandler getSkyRenderer(){
        return this.skyRenderer;
    }

    // Maybe do something about the LightBrightnessTable?

    @SideOnly(Side.CLIENT)
    public int getMoonPhase(long par1, float par3){
        return 4;
    }

    // The location to save our labrynths to
    @Override
    @SideOnly(Side.CLIENT)
    public String getSaveFolder(){
        try{
            if(this.dimensionId == 0)
                return "";
            return "MadokaMagicaLabrynths/" + this.dimensionId;
        }catch(Exception e){
            // Yolo
        }
        return "";
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z){
        return MadokaMagicaMod.witchlabrynthbiome;
    }

    @Override
    public IChunkProvider createChunkGenerator(){
        //WorldServer server = (WorldServer)MinecraftServer.getServer().getEntityWorld();
        return new LabrynthGenerator(worldObj,worldObj.getWorldInfo().getSeed(),false);
    }

    @Override
    public boolean isSurfaceWorld(){
        return false;
    }

    @Override
    public String getDimensionName(){
        //return this.witchName;
        return "Labrynth";
    }

    // TODO: Figure out if this method even exists anymore
    // @Override
    public static WorldProvider getProviderForDimension(int dimID){
        // Alright, so I've finally figured out what this method is supposed to do.
        // Based on net/minecraft/world/WorldProvider.java (lines:195-198) and net/minecraftforge/common/DimensionManager.java (lines:285-306)
        //  I've ascertained that this method is supposed to return a NEW WorldProvider object every time it is called
        //  Because I need a variable number of dimensions, I think I should implement my own dimension manager, and somehow modify this method to return that
        // So, TODO: Finish this method

        return null;
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint(){
        int x = MathHelper.getRandomIntegerInRange(this.worldObj.rand, (int)(this.owner.posX+MIN_WITCH_DISTANCE), (int)(MAX_LABRYNTH_WORLD_SIZE));
        int z = MathHelper.getRandomIntegerInRange(this.worldObj.rand, (int)(this.owner.posZ+MIN_WITCH_DISTANCE), (int)(MAX_LABRYNTH_WORLD_SIZE));
        // TODO: Find a better Y value
        return new ChunkCoordinates(x, 25, z);
    }
}
