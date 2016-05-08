package com.MadokaMagica.mod_madokaMagica.world;

import net.minecraftforge.client.IRenderHandler;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.MadokaMagica.mod_madokaMagica.factories.LabrynthGeneratorFactory;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

public class LabrynthProvider extends WorldProvider{
    private BiomeGenBase labrynthbiome;
    public boolean canSnow;
    public boolean canLightning;
    public double horizon;
    public String witchName;
    public LabrynthGeneratorFactory labrynthGeneratorFactory;
    public IRenderHandler skyRenderer;
    public EntityPMWitch master;

    public LabrynthProvider(BiomeGenBase lb,LabrynthGeneratorFactory lgf,EntityPMWitch owner){
        this.labrynthbiome = lb;
        this.canSnow = false;
        this.hasNoSky = false;
        this.canLightning = false;
        this.labrynthGeneratorFactory = lgf;
        this.master = owner;
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

    @Override
    @SideOnly(Side.CLIENT)
    public String getSaveFolder(){
        // Not sure how we are going to save this yet
        return null;
    }

    @Override
    public double getHorizon(){
        return this.horizon;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z){
        return this.labrynthbiome;
    }

    @Override
    public IChunkProvider createChunkGenerator(){
        return this.labrynthGeneratorFactory.create(this);
    }

    @Override
    public String getDimensionName(){
        return this.witchName;
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint(){
        int x = MathHelper.getRandomIntegerInRange(this.worldObj.rand, this.master.posX+MIN_WITCH_DISTANCE, MAX_LABRYNTH_WORLD_SIZE);
        int z = MathHelper.getRandomIntegerInRange(this.worldObj.rand, this.master.posZ+MIN_WITCH_DISTANCE, MAX_LABRYNTH_WORLD_SIZE);
        // TODO: Find a better Y value
        return new ChunkCoordinates(x, 25, z);
    }
}
