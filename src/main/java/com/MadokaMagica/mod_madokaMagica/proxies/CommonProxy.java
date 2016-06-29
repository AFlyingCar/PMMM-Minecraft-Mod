package com.MadokaMagica.mod_madokaMagica.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaBlocks;

public class CommonProxy {
	public void preinit(FMLPreInitializationEvent event){}
	public void init(FMLInitializationEvent event){}
	public void postinit(FMLPostInitializationEvent event){}

    public void registerBlocks(){
        GameRegistry.registerBlock(MadokaMagicaBlocks.labrynthTeleporter,"labrynthTeleporter");
    }
}
