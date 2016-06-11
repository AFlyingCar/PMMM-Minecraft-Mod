package com.MadokaMagica.mod_madokaMagica.models;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

@SideOnly(Side.CLIENT)
public class ModelPMWitchMinion extends ModelBase {
    public Map<String,ModelRenderer> parts;
    protected double distanceMoved = 0.0D;
    protected static final double CYCLES_PER_BLOCK = 3.0D; // Number of animation cycles to do per block

    public int textureWidth = 64;
    public int textureHeight = 32;

    public Runnable animation;

    public ModelPMWitchMinion(){
        parts = new HashMap<String,ModelRenderer>();
    }

    public ModelRenderer getModelRenderer(String iden){
        return parts.get(iden);
    }

    public void addModelRenderer(String name, ModelRenderer part){
        parts.put(name,part);
        part.setTextureSize(textureWidth,textureHeight);
    }

    public Map<String,ModelRenderer> getAllRenderers(){
        return parts;
    }

    @Override
    public void render(Entity entity, float time, float swingsupress,float what, float headAngleY, float headAngleX, float what2){
        // TODO: Find out what the 4th and 7th parameters signify
        renderPMWitchMinion((EntityPMWitchMinion)entity,time,swingsupress,what,headAngleY,headAngleX,what2);
    }

    public void renderPMWitchMinion(EntityPMWitchMinion entity, float time, float swingsupress, float what, float headAngleY, float headAngleX, float what2){
        setRotationAngles(time,swingsupress,what,headAngleY,headAngleX,what2,entity);

        GL11.glPushMatrix();
        // TODO: Figure out where getScaleFactor comes from (in the mod-tutorial's code)
        // GL11.glScalef(entity.getScaleFactor(),entity.getScaleFactor(),entity.getScaleFactor());

        if(this.isChild){
            float csf = 0.5F; // child scale factor
            GL11.glPushMatrix();
            GL11.glScalef(1.0F*csf,1.0F*csf,1.0F*csf);
            GL11.glTranslatef(0.0F,24.0F*what2,0.0F); // TODO: figure out what these magic numbers are
            // TODO: Do we need to render the head first regardless? Based on the examples, we do it anyways, so can I just cut out the middle man?
            /*
            if(parts.containsKey("HEAD"))
                parts.get("HEAD").render(what2);
                */
        }
        for(Map.Entry<String,ModelRenderer> entry : parts.entrySet()){
            entry.getValue().render(what2);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void setRotationAngles(float time, float swingsupress, float what, float headAngleY, float headAngleX, float what2, Entity entity){
        updateDistanceMoved(entity);
        doAnimation();
    }

    protected void updateDistanceMoved(Entity entity){
        distanceMoved += entity.getDistance(entity.prevPosX,entity.prevPosY,entity.prevPosZ);
    }

    public double getDistanceMoved(){
        return distanceMoved;
    }

    protected float degToRad(float degrees){
        return degrees*(float)Math.PI/180;
    }

    protected void setRotation(ModelRenderer model,float x, float y, float z){
        model.rotateAngleX = degToRad(x);
        model.rotateAngleY = degToRad(y);
        model.rotateAngleZ = degToRad(z);
    }

    protected void setRotation(String iden, float x, float y, float z){
        setRotation(this.parts.get(iden),x,y,z);
    }

    public void doAnimation(){
        this.animation.run();
    }
}

