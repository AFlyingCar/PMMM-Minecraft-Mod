package com.MadokaMagica.mod_madokaMagica.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.MadokaMagica.mod_madokaMagica.proxies.CommonProxy;

public class ClientProxy extends CommonProxy {
	public void preinit(FMLPreInitializationEvent event){
		super.preinit(event);
	}
	public void init(FMLInitializationEvent event){
		super.init(event);
	}
	public void postinit(FMLPostInitializationEvent event){
		super.postinit(event);
	}
}
