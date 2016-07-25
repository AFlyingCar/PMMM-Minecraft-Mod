package com.MadokaMagica.mod_madokaMagica.factories;

import java.util.List;
import java.util.ArrayList;

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
        public float sizeMultiplier; // Multiplies by the size of the labrynth (changes based on the strength of the witch, the stronger the witch the bigger the labrynth)
        public List<int[]> startingLocations;
    }

    public static LabrynthDetails createLabrynth(PMDataTracker tracker){
        int dimID = DimensionManager.getNextFreeDimId();
        String dimName = "Labrynth"; // TODO: Find a way to auto-generate labrynth names
        String worldType = ""; // TODO: Set this properly
        int providerID;

        WorldServer customWorldServer = LabrynthWorldServerFactory.createWorldServer(tracker,dimID);

        LabrynthDetails details = new LabrynthDetails();
        details.dimID = dimID;
        details.dimName = dimName;
        details.world = (LabrynthWorldServer)customWorldServer;
        details.sizeMultiplier = 1; // TODO: Set this value properly
        details.startingLocations = new ArrayList<int[]>();

        LabrynthManager.getInstance().registerDetailsWithDimensionManager(details);

        return details;
    }
}

