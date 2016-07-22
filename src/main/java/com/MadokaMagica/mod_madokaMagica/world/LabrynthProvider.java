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
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

/*****************\
 * IMPORTANT IDEA*
\*****************/

// TODO: Maybe have each labrynth act as a giant structure, with randomly generated entrances that are chosen at random when a plyaer enters
// Basically, randomly generate a maze, with a few entrances and the witch at the center

public class LabrynthProvider extends WorldProvider{
    // The diameter of the labrynth
    public final static double MAX_LABRYNTH_WORLD_SIZE = 255.0; // TODO: Find better number than 255. Maybe generate a number?
    public final static double MIN_WITCH_DISTANCE = MAX_LABRYNTH_WORLD_SIZE/2.0;

    public boolean canSnow;
    public boolean canLightning;
    public boolean canRSI; // Can Rain, Snow, Ice
    public boolean perpetualRainAndThunder;
    public boolean hasWeather;
    public boolean hasSun;
    public boolean hasStars;
    public double horizon;
    public String witchName;
    public LabrynthGeneratorFactory labrynthGeneratorFactory;
    public IRenderHandler skyRenderer;
    public EntityPMWitch owner;
    public PMDataTracker tracker;

    public LabrynthProvider(){
        // TODO: Move this stuff into LabrynthWorldServer, and have that class handle this shit
        // By default, labrynths have no weather
        this.canSnow = false;
        this.hasNoSky = false;
        this.canLightning = false;
        this.canRSI = false;
        this.perpetualRainAndThunder = false;
        this.hasWeather = false;
        this.hasSun = false;
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
    public boolean canDoRainSnowIce(Chunk chunk){
        return this.canRSI;
    }

    @Override
    public float getSunBrightness(float par1){
        if(this.hasSun){
            return super.getSunBrightness(par1);
        }
        return 0.0F;
    }

    @Override
    public float getStarBrightness(float par1){
        if(this.hasStars){
            return super.getStarBrightness(par1);
        }
        return 0.0F;
    }

    @Override
    public float getSunBrightnessFactor(float par1){
        if(this.hasSun){
            return super.getSunBrightnessFactor(par1);
        }
        return 0.0F;
    }

    @Override
    public boolean shouldMapSpin(String p1, double p2, double p3, double p4){ 
        return true;
    }

    @Override
    public void resetRainAndThunder(){
        if(!this.perpetualRainAndThunder){
            super.resetRainAndThunder();
        }
    }

    @Override
    public void updateWeather(){
        if(this.hasWeather){
            super.updateWeather();
        }
    }

    @Override
    public void calculateInitialWeather(){
        if(this.hasWeather){
            super.calculateInitialWeather();
        }
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
        WorldServer server = (WorldServer)MinecraftServer.getServer().getEntityWorld();
        if(server instanceof LabrynthWorldServer){
            return ((LabrynthWorldServer)server).createChunkProvider();
        }
        // This method shouldn't ever be called if we aren't in a Labrynth, and a Labrynth should never have a WorldServer that isn't a LabrynthWorldServer
        return null;
    }

    @Override
    public boolean isSurfaceWorld(){
        return false;
    }

    @Override
    public String getDimensionName(){
        return this.witchName;
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint(){
        /*
        int x = MathHelper.getRandomIntegerInRange(this.worldObj.rand, (int)(this.owner.posX+MIN_WITCH_DISTANCE), (int)(MAX_LABRYNTH_WORLD_SIZE));
        int z = MathHelper.getRandomIntegerInRange(this.worldObj.rand, (int)(this.owner.posZ+MIN_WITCH_DISTANCE), (int)(MAX_LABRYNTH_WORLD_SIZE));
        int y = this.worldObj.getTopSolidOrLiquidBlock(x,z);
        return new ChunkCoordinates(x,y,z);
        */
        return LabrynthManager.getInstance().getRandomStartingLocation();
    }

}
