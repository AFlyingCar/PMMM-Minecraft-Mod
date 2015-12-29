// package com.MadokaMagica.mod_madokaMagica.factories;

// import com.MadokaMagica.mod_madokaMagica.interfaces.IPMWitch;
// import com.MadokaMagica.mod_madokaMagica.interfaces.IWitchMinion;
// import com.MadokaMagica.mod_madokaMagica.interfaces.IWitchLabrynth;
// import com.MadokaMagica.mod_madokaMagica.factories.PMWitchLabrynthFactory;
// import com.MadokaMagica.mod_madokaMagica.factories.PMWitchMinionFactory;
// import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

// public class PMWitchFactory{
//     public static IPMWitch generateWitch(PMDataTracker pd){
//         IPMWitch w = new EntityPMWitch(pd.player);
//         IWitchMinion[] mtypes = PMWitchMinionFactory.generateMinionTypes(pd);
//         IWitchLabrynth labryn = PMWitchLabrynthFactory.generateLabrynth(pd);
//         w.setLabrynth(l);
//         for(IWitchMinion m : mtypes){
//             w.addWitchMinionType(m.class);
//         }
//         return w;
//     }
// }

