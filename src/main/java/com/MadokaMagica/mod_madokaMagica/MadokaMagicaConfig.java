package com.MadokaMagica.mod_madokaMagica;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class MadokaMagicaConfig{
    public static boolean useDebugModels;
    public static boolean enableCorruptionVisualEffects;

    public static void loadConfig(FMLPreInitializationEvent event){
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());

        try{
            cfg.load();

            useDebugModels = cfg.get("Debug","UseDebugModels",false).getBoolean(false);

            enableCorruptionVisualEffects = cfg.get("Effects","EnableVisualEffects",false).getBoolean(false);

        }catch(Exception e){
            FMLLog.severe("OH GOD WHY?!\n");
        }finally{
            cfg.save();
        }
    }
}

