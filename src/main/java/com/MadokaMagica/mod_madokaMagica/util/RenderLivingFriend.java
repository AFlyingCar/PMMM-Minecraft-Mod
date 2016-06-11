package com.MadokaMagica.mod_madokaMagica.util;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.MadokaMagica.mod_madokaMagica.renderers.RenderPMWitchMinionHolder;

public abstract class RenderLivingFriend extends RenderLiving{
    public RenderLivingFriend(ModelBase model, float shadowSize){
        super(model,shadowSize);
    }

    // Not going to explain myself much on this one. Just know that this is supposed to make these methods 'protected' while still allowing one outside class to play around
    // If you want information on why this works (though, why on earth would you want to?!), see this stackoverflow thread:
    //              http://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java
    public void func_110827_b(EntityLiving e, double d1, double d2, double d3, float f1, float f2,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        func_110827_b(e,d1,d2,d3,f1,f2);
    }

    public boolean func_110813_b(EntityLivingBase e,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return func_110813_b(e);
    }

    /********************************\
     * RenderLivingEntity overrides *
    \********************************/
    
    public void renderModel(EntityLivingBase e, float f1, float f2, float f3, float f4, float f5, float f6,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        renderModel(e,f1,f2,f3,f4,f5,f6);
    }
    
    public void renderLivingAt(EntityLivingBase e, double d1, double d2, double d3,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        renderLivingAt(e,d1,d2,d3);
    }
    
    public void rotateCorpse(EntityLivingBase e, float f1, float f2, float f3,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        rotateCorpse(e,f1,f2,f3);
    }
    
    public float renderSwingProgress(EntityLivingBase e, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return renderSwingProgress(e,f);
    }
    
    public float handleRotationFloat(EntityLivingBase e, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return handleRotationFloat(e,f);
    }
    
    public void renderEquippedItems(EntityLivingBase e, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend) {
        renderEquippedItems(e,f);
    }
    
    public void renderArrowsStuckInEntity(EntityLivingBase e, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        renderArrowsStuckInEntity(e,f);
    }
    
    public int inheritRenderPass(EntityLivingBase e, int i, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return inheritRenderPass(e,i,f);
    }
    
    public int shouldRenderPass(EntityLivingBase e, int i, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return shouldRenderPass(e,i,f);
    }
    
    public void func_82408_c(EntityLivingBase e, int i, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend) {
        func_82408_c(e,i,f);
    }
    
    public float getDeathMaxRotation(EntityLivingBase e,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return getDeathMaxRotation(e);
    }
    
    public int getColorMultiplier(EntityLivingBase e, float f, float f2,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return getColorMultiplier(e,f,f2);
    }

    public void passSpecialRender(EntityLivingBase e, double d1, double d2, double d3,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        passSpecialRender(e,d1,d2,d3);
    }
    
    public void func_96449_a(EntityLivingBase e, double d1, double d2, double d3, String s, float f, double d4,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        func_96449_a(e,d1,d2,d3,s,f,d4);
    }

    public ResourceLocation getEntityTexture(Entity e,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        return getEntityTexture(e);
    }

    public void preRenderCallback(EntityLivingBase e, float f,RenderPMWitchMinionHolder.Friend renderPMWitchMinionHolderDOTFriend){
        preRenderCallback(e,f);
    }
}
