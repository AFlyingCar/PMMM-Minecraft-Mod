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

public class LabrynthFactory{
    public static class LabrynthDetails{
        public int dimID;
        public String dimName;
        public boolean markForDestruction;
        public LabrynthWorldServer world;
    }

    public static LabrynthDetails createLabrynth(PMDataTracker tracker){
        Integer dimID = DimensionManager.getNextFreeDimId();
        String dimName = "Labrynth"; // TODO: Find a way to auto-generate labrynth names
        String worldType = ""; // TODO: Set this properly
        int providerID;

        WorldServer customWorldServer = LabrynthWorldServerFactory.createWorldServer(tracker,dimID.intValue());

        /*
        if(!DimensionManager.registerProviderType(dimID,LabrynthProvider.class,false))
            throw new IllegalStateException("There is a provider ID conflict between LabrynthProvider from MadokaMagica and another provider type. Somehow we were unable to get a new Provider ID.");
        DimensionManager.registerDimension(dimID,dimID); // The dimension and provider share an ID
        DimensionManager.initDimension(dimID);
        DimensionManager.setWorld(dimID,customWorldServer);
        */

        LabrynthDetails details = new LabrynthDetails();
        details.dimID = dimID;
        details.dimName = dimName;
        details.world = (LabrynthWorldServer)customWorldServer;

        LabrynthManager.getInstance().registerDetailsWithDimensionManager(details);

        return details;
    }
}

