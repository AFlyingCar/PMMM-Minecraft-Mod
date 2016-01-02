package com.MadokaMagica.mod_madokaMagica.factories;

import com.MadokaMagica.mod_madokaMagica.factories.PMWitchMinionFactoryFactory;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthProviderFactory;
import com.MadokaMagica.mod_madokaMagica.factories.PMWitchMinionFactory;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthProvider;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

public class PMWitchFactory{
    public static EntityPMWitch generateWitch(PMDataTracker pd){
    	EntityPMWitch witch = new EntityPMWitch(pd);
    	PMWitchMinionFactory witchMinionFactory = PMWitchMinionFactoryFactory.generate(pd,witch);
    	LabrynthProvider labrynthProvider = LabrynthProviderFactory.generate(pd,witch);
//         w.setLabrynth(l);
//         for(IWitchMinion m : mtypes){
//             w.addWitchMinionType(m.class);
//         }
//         return w;
    	return witch;
    }
}

