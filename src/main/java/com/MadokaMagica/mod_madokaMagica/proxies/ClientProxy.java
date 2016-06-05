package com.MadokaMagica.mod_madokaMagica.proxies;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.client.registry.RenderingRegistry;

import com.MadokaMagica.mod_madokaMagica.proxies.CommonProxy;
import com.MadokaMagica.mod_madokaMagica.models.ModelPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.renderers.RenderPMWitchMinionHolder;

public class ClientProxy extends CommonProxy {
	public void preinit(FMLPreInitializationEvent event){
		RenderingRegistry.registerEntityRenderingHandler(EntityPMWitchMinion.class,RenderPMWitchMinionHolder.getInstance());

		super.preinit(event);
	}
	public void init(FMLInitializationEvent event){
		super.init(event);
	}
	public void postinit(FMLPostInitializationEvent event){
		super.postinit(event);
	}
}
