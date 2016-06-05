package com.MadokaMagica.mod_madokaMagica.renderers;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;
import com.MadokaMagica.mod_madokaMagica.util.RenderLivingFriend;

public class RenderPMWitchMinion extends RenderLivingFriend{
	public ResourceLocation minionTexture;

	public RenderPMWitchMinion(ModelBase base, float shadowSize){
		super(base,shadowSize);
		setEntityTexture();
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f){
		preRenderCallbackPMWitchMinion((EntityPMWitchMinion)entity,f);
	}

	protected void preRenderCallbackPMWitchMinion(EntityPMWitchMinion entity, float f){
		// This method is to be overridden by the factories
	}

	protected void setEntityTexture(){
		minionTexture = new ResourceLocation(MadokaMagicaMod.MODID+":textures/entity/minions/minionDEFAULT.png");
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e){
		return minionTexture;
	}
}

