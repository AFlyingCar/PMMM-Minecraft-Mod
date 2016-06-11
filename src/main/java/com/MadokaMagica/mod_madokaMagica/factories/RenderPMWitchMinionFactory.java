package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.client.model.ModelBase;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.renderers.RenderPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.factories.ModelPMWitchMinionFactory;
import com.MadokaMagica.mod_madokaMagica.models.ModelPMWitchMinion;

public class RenderPMWitchMinionFactory {
    public static RenderPMWitchMinion createRenderer(EntityPMWitchMinion entity){
        ModelBase model = ModelPMWitchMinionFactory.createModel(entity);
        float shadowSize = 1.0F; // TODO: Figure out how we can create dynamic shadow sizes

        RenderPMWitchMinion renderer = new RenderPMWitchMinion(model,shadowSize);
        renderer.minionTexture = ModelPMWitchMinionFactory.createTexture(entity,(ModelPMWitchMinion)model);

        return renderer;
    }
}
