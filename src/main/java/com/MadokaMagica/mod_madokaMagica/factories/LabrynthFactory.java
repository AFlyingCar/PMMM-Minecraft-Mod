package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.world.LabrynthProvider;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthProviderFactory;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthWorldServerFactory;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;

public class LabrynthFactory{
    public static class LabrynthDetails{
        public int dimID;
        public String dimName;
        public boolean markForDestruction;
        public LabrynthWorldServer world;
    }

    public static LabrynthDetails createLabrynth(PMDataTracker tracker){
        int dimID = DimensionManager.getNextFreeDimId();
        String dimName = "Labrynth"; // TODO: Find a way to auto-generate labrynth names
        String worldType = ""; // TODO: Set this properly
        int providerID;


        LabrynthDetails details = new LabrynthDetails();
        details.dimID = dimID;
        details.dimName = dimName;

        DimensionManager.registerDimension(details.dimID,MadokaMagicaConfig.labrynthProviderID);
        DimensionManager.initDimension(details.dimID);

        // Do this after to fix a runtime error 
        WorldServer customWorldServer = LabrynthWorldServerFactory.createWorldServer(tracker,dimID);
        details.world = (LabrynthWorldServer)customWorldServer;
        DimensionManager.setWorld(details.dimID,details.world);

        return details;
    }
}

