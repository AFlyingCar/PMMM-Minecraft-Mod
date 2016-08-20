package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;

public class LabrynthWorldServerFactory{
    public static LabrynthWorldServer createWorldServer(PMDataTracker tracker,int dimID,LabrynthDetails details){
        World oworld = DimensionManager.getWorld(0);
        LabrynthWorldServer worldServer = new LabrynthWorldServer(MinecraftServer.getServer(),
                oworld.getSaveHandler(),
                "Labrynth",
                dimID,
                new WorldSettings(oworld.getWorldInfo()),
                MinecraftServer.getServer().theProfiler,
                tracker,
                details
        );
        // TODO: Do something to set the internal variables of LabrynthWorldServer

        return worldServer;
    }
}

