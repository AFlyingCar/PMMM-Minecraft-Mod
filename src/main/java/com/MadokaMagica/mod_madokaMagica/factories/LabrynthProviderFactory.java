package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthProvider;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthGeneratorFactoryFactory;

public class LabrynthProviderFactory{
    public static LabrynthProvider generate(PMDataTracker pd,EntityPMWitch witch){
        final EntityPMWitch fwitch = witch;
        final PMDataTracker ftracker = pd;

        LabrynthProvider provider = new LabrynthProvider(){
            {
                owner = fwitch;
                tracker = ftracker;
            }
        };
        provider.labrynthGeneratorFactory = LabrynthGeneratorFactoryFactory.create(provider);

        // TODO: Add code which modifies variables in provider based on data in PMDataTracker

        if(!DimensionManager.registerProviderType(DimensionManager.getNextFreeDimId(),provider.getClass(),false))
            // This should never happen (unless somehow you have 4 billion+ dimensions)
            throw new IllegalStateException("There is a provider ID conflict between LabrynthProvider from MadokaMagica and another provider type. Somehow we were unable to get a new Provider ID.");

        return provider;
    }
}

