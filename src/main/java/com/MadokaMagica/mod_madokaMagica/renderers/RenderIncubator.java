package com.MadokaMagica.mod_madokaMagica.renderers;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;
import com.MadokaMagica.mod_madokaMagica.entities.EntityIncubator;

public class RenderIncubator extends RenderLiving {
	protected ResourceLocation incubatorTexture;
	public RenderIncubator(ModelBase base, float shadowSize){
		super(base,shadowSize);
		setEntityTexture();
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entity, float f){
		preRenderCallbackIncubator((EntityIncubator)entity,f);
	}

	protected void preRenderCallbackIncubator(EntityIncubator entity, float f){
		super.preRenderCallback(entity,f);
	}

	protected void setEntityTexture(){
		incubatorTexture = new ResourceLocation(MadokaMagicaMod.MODID+":textures/entity/incubator.png");
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return incubatorTexture;
	}
}
