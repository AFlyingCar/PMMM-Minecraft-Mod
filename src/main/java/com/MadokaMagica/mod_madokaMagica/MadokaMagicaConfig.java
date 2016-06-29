package com.MadokaMagica.mod_madokaMagica;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class MadokaMagicaConfig{
    public static boolean useDebugModels;
    public static boolean enableCorruptionVisualEffects;
    public static boolean killLabrynthEntranceWithoutWitchOrLabrynth;
    public static boolean deleteBlockLabrynthTeleporterWithoutTileEntityOrLinkedWorld;
    public static int labrynthDimensionID;
    public static int labrynthProviderID;

    public static void loadConfig(FMLPreInitializationEvent event){
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try{
            cfg.load();

            /*
             * Debugging
             */
            useDebugModels = cfg.get("Debug","UseDebugModels",false).getBoolean(false);
            killLabrynthEntranceWithoutWitchOrLabrynth = cfg.get("Debug","KillLabrynthEntranceWithoutWitchOrLabrynth",false).getBoolean(false);
            deleteBlockLabrynthTeleporterWithoutTileEntityOrLinkedWorld = cfg.get("Debug","DeleteBlockLabrynthTeleporterWithoutTileEntityOrLinkedWorld",true).getBoolean(true);

            /*
             * Effects
             */
            enableCorruptionVisualEffects = cfg.get("Effects","EnableVisualEffects",false).getBoolean(false);

            /*
             * Dimensions
             */
            labrynthDimensionID = cfg.get("Dimension","LabrynthDimensionID",180).getInt(180);
            labrynthProviderID = cfg.get("Dimension","LabrynthProviderID",180).getInt(180);

        }catch(Exception e){
            FMLLog.severe("OH GOD WHY?!\n");
        }finally{
            cfg.save();
        }
    }
}

