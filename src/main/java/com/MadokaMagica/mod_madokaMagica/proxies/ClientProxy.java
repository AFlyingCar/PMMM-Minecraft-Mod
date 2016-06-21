package com.MadokaMagica.mod_madokaMagica.proxies;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.client.registry.RenderingRegistry;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;
import com.MadokaMagica.mod_madokaMagica.proxies.CommonProxy;
import com.MadokaMagica.mod_madokaMagica.models.ModelPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.models.ModelIncubator;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityIncubator;
import com.MadokaMagica.mod_madokaMagica.renderers.RenderPMWitchMinionHolder;
import com.MadokaMagica.mod_madokaMagica.renderers.RenderIncubator;

public class ClientProxy extends CommonProxy {
	public void preinit(FMLPreInitializationEvent event){
		//RenderingRegistry.registerEntityRenderingHandler(EntityPMWitchMinion.class,RenderPMWitchMinionHolder.getInstance());
		System.out.println("ClientProxy.preinit(FMLPreInitializationEvent)");
		RenderingRegistry.registerEntityRenderingHandler(EntityIncubator.class,new RenderIncubator(new ModelIncubator(),0.5F));

        // Should we render known-working models for entities that don't have models? (So we can test non-visual properties of entities)
        if(MadokaMagicaConfig.useDebugModels){
            System.out.println("Using debug models (ModelBiped)");
            RenderingRegistry.registerEntityRenderingHandler(EntityPMWitchLabrynthEntrance.class,new RenderBiped(new ModelBiped(),0.5F));
        }

		super.preinit(event);
	}
	public void init(FMLInitializationEvent event){
		super.init(event);
	}
	public void postinit(FMLPostInitializationEvent event){
		super.postinit(event);
	}
}
