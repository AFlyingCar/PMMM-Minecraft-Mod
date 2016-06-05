package com.MadokaMagica.mod_madokaMagica.renderers;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.util.RenderLivingFriend;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

public class RenderPMWitchMinionHolder extends RenderLiving{
	// shh bby is ok
	public static final class Friend{private Friend(){}}
	private static final Friend friend = new Friend();

	public Map<EntityLivingBase,RenderLivingFriend> renderHolders;
	protected static RenderPMWitchMinionHolder instance;

	protected RenderPMWitchMinionHolder(){
		super(null,0.0F); // TODO: This may cause some problems, but I'm not sure what to do about it right now
		renderHolders = new HashMap<EntityLivingBase,RenderLivingFriend>();
	}

	public RenderLivingFriend getRenderLivingFriend(EntityLivingBase entity){
		if(renderHolders.containsKey(entity))
			return renderHolders.get(entity);
		else
			System.out.println("Warning! Attempted to get a RenderLiving class for an entity RenderPMWitchMinionHolder does not contain!\nSkipping.");
		return null;
	}

	// Due to problems with protected methods, this class will ONLY accept objects of type RenderLivingFriend, which simply opens up methods for this class and this class ONLY
	public void addEntity(EntityLivingBase entity,RenderLivingFriend renderer){
		renderHolders.put(entity,renderer);
	}

	public Map<EntityLivingBase,RenderLivingFriend> getAllEntities(){
		return renderHolders;
	}

	/*********************************\
	 * RenderLiving method overrides *
	\*********************************/

	@Override
	public void doRender(EntityLiving e, double d1, double d2, double d3, float f1, float f2){
		if(renderHolders.containsKey(e))
			renderHolders.get(e).doRender(e,d1,d2,d3,f1,f2);
		else
			System.out.println("Warning! Attempted to call doRender on a RenderHolder class for an entity it does not contain!\nSkipping.");
	}

	@Override
	protected void func_110827_b(EntityLiving e, double d1, double d2, double d3, float f1, float f2){
		if(renderHolders.containsKey(e))
			renderHolders.get(e).func_110827_b(e,d1,d2,d3,f1,f2,this.friend);
		else
			System.out.println("Warning! Attempted to call func_110827_b on a RenderHolder class for an entity it does not contain!\nSkipping.");
	}

	@Override
	protected boolean func_110813_b(EntityLivingBase e){
		if(renderHolders.containsKey(e))
			return renderHolders.get(e).func_110813_b(e,this.friend);
		else
			System.out.println("Warning! Attempted to call func_110813_b on a RenderHolder class for an entity it does not contain!\nSkipping.");
		return false;
	}

	/********************************\
	 * RenderLivingEntity overrides *
	\********************************/

	@Override
    protected void renderModel(EntityLivingBase e, float f1, float f2, float f3, float f4, float f5, float f6){
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).renderModel(e,f1,f2,f3,f4,f5,f6,this.friend);
    	else
    		System.out.println("Warning! Attempted to call renderModel on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected void renderLivingAt(EntityLivingBase e, double d1, double d2, double d3){
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).renderLivingAt(e,d1,d2,d3,this.friend);
    	else
    		System.out.println("Warning! Attempted to call renderLivingAt on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected void rotateCorpse(EntityLivingBase e, float f1, float f2, float f3){
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).rotateCorpse(e,f1,f2,f3,this.friend);
    	else
    		System.out.println("Warning! Attempted to call rotateCorpse on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected float renderSwingProgress(EntityLivingBase e, float f){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).renderSwingProgress(e,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call renderSwingProgress on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase e, float f){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).handleRotationFloat(e,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call handleRotationFloat on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase e, float f) {
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).renderEquippedItems(e,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call renderEquippedItems on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected void renderArrowsStuckInEntity(EntityLivingBase e, float f){
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).renderArrowsStuckInEntity(e,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call renderArrowsStuckInEntity on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected int inheritRenderPass(EntityLivingBase e, int i, float f){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).inheritRenderPass(e,i,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call inheritRenderPass on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase e, int i, float f){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).shouldRenderPass(e,i,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call shouldRenderPass on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

    @Override
    protected void func_82408_c(EntityLivingBase e, int i, float f) {
    	if(renderHolders.containsKey(e))
    		renderHolders.get(e).func_82408_c(e,i,f,this.friend);
    	else
    		System.out.println("Warning! Attempted to call func_82408_c on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected float getDeathMaxRotation(EntityLivingBase e){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).getDeathMaxRotation(e,this.friend);
    	else
    		System.out.println("Warning! Attempted to call getDeathMaxRotation on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase e, float f, float f2){
    	if(renderHolders.containsKey(e))
    		return renderHolders.get(e).getColorMultiplier(e,f,f2,this.friend);
    	else
    		System.out.println("Warning! Attempted to call getColorMultiplier on a RenderHolder class for an entity it does not contain!\nSkipping.");
    	return 0;
    }

	@Override
	public void preRenderCallback(EntityLivingBase e, float f){
		if(renderHolders.containsKey(e))
			renderHolders.get(e).preRenderCallback(e,f,this.friend);
		else
			System.out.println("Warning! Attempted to call preRenderCallback on a RenderHolder class for an entity it does not contain!\nSkipping.");
	}

	@Override
    protected void passSpecialRender(EntityLivingBase e, double d1, double d2, double d3){
		if(renderHolders.containsKey(e))
			renderHolders.get(e).passSpecialRender(e,d1,d2,d3,this.friend);
		else
			System.out.println("Warning! Attempted to call passSpecialRender on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    @Override
    protected void func_96449_a(EntityLivingBase e, double d1, double d2, double d3, String s, float f, double d4){
		if(renderHolders.containsKey(e))
			renderHolders.get(e).func_96449_a(e,d1,d2,d3,s,f,d4,this.friend);
		else
			System.out.println("Warning! Attempted to call func_96449_a on a RenderHolder class for an entity it does not contain!\nSkipping.");
    }

    /***************************\
     * Render method overrides *
    \***************************/

    @Override
    protected ResourceLocation getEntityTexture(Entity e){
		if(renderHolders.containsKey((EntityLivingBase)e))
			return renderHolders.get((EntityLivingBase)e).getEntityTexture(e,this.friend);
		else
			System.out.println("Warning! Attempted to call getEntityTexture on a RenderHolder class for an entity it does not contain!\nSkipping.");
		return null;
    }

	public static RenderPMWitchMinionHolder getInstance(){
		if(instance == null){
			instance = new RenderPMWitchMinionHolder();
		}
		return instance;
	}
}

