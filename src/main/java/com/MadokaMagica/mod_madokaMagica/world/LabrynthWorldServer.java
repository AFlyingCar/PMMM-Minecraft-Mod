package com.MadokaMagica.mod_madokaMagica.world;

import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class LabrynthWorldServer extends WorldServer{
    public PMDataTracker tracker; // The tracker for the owner
    public boolean isCold;
    public int time; // 0 - Permanent day-time, 1 - Permanent night time, 2 - Permanent evening, 3 - Permanent morning
    public int stormy; // 0 - nothing, 1 - raining, 2 - thundering
    public IChunkProvider provider;

    public LabrynthWorldServer(MinecraftServer server, ISaveHandler saveHandler, String name, int dimID, WorldSettings settings, Profiler profiler,PMDataTracker tracker){
        super(server,saveHandler,name,dimID,settings,profiler);

        this.tracker = tracker;
    }

    protected IChunkProvider createChunkProvider(){
        return provider;
    }

    public static boolean verifyNBTIntegrity(NBTTagCompound nbt){
        return (nbt.hasKey("dimID") &&
                nbt.hasKey("ENTITY_UUID_MOST_SIG") &&
                nbt.hasKey("ENTITY_UUID_LEAST_SIG")
               );
    }

    public static LabrynthWorldServer loadFromNBT(NBTTagCompound nbt){
        if(!verifyNBTIntegrity(nbt))
            return null;

        EntityLiving entity = Helper.getEntityLivingByUUID(new UUID(nbt.getLong("ENTITY_UUID_MOST_SIG"),nbt.getLong("ENTITY_UUID_LEAST_SIG")));
        PMDataTracker tracker = new PMDataTracker(entity);
        PlayerDataTrackerManager.getInstance().addTracker(tracker);

        LabrynthWorldServer server = new LabrynthWorldServer(MinecraftServer.getServer(),
                DimensionManager.getWorld(0).getSaveHandler(),
                "Labrynth",
                nbt.getInteger("dimID"),
                new WorldSettings(DimensionManager.getWorld(0).getWorldInfo()),
                MinecraftServer.getServer().theProfiler,
                tracker
                );
        return server;
    }

    public void writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("dimID",this.dimensionId);
        nbt.setInteger("ENTITY_UUID_MOST_SIG",this.tracker.entity.getPersistentID().getMostSignificantBits());
        nbt.setInteger("ENITTY_UUID_LEAST_SIG",this.tracker.entity.getPersistentID().getLeastSignificantBits());
    }
}

