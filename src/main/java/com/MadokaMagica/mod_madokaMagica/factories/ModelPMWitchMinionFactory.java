package com.MadokaMagica.mod_madokaMagica.factories;

import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelRenderer;

import com.MadokaMagica.mod_madokaMagica.models.ModelPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

public class ModelPMWitchMinionFactory {
    public static ModelPMWitchMinion createModel(EntityPMWitchMinion entity){
        // TODO: Add some code here which determines how to build the model
        //   By this, I mean, determine the head, body, arms, legs, and extra.
        int headType = 0;
        int bodyType = 0;
        int armLType = 0;
        int armRType = 0;
        int legLType = 0;
        int legRType = 0;

        ModelPMWitchMinion model = new ModelPMWitchMinion();

        // Lets just get around that little issue
        // NOTE: This line HAS to be here. The reason is that we can't assign _model to the newly created ModelPMWitchMinion object unless it is final.
        // This declaration fixes that little problem. I think.
        final ModelPMWitchMinion m = model;
        model.animation = new Runnable(){
            ModelPMWitchMinion _model = m;
            public void run(){

            }
        };

        // Create body first, since everything else should be a child of it
        createBodyModel(model,bodyType);
        createHeadModel(model,headType);
        createArmLModel(model,armLType);
        createArmRModel(model,armRType);
        createLegLModel(model,legLType);
        createLegRModel(model,legRType);

        return model;
    }

    /*********************************************\
     * DECIDE WHICH MODEL TO BUILD FOR EACH PART *
    \*********************************************/

    protected static void createHeadModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
                buildHumanHead(model);
        }

        // Check if we have a body, and if so, make the head a child of it
        if(model.getAllRenderers().containsKey("BODY") && model.getAllRenderers().containsKey("HEAD"))
            model.getAllRenderers().get("BODY").addChild(model.getAllRenderers().get("HEAD"));
    }

    protected static void createBodyModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
                buildHumanBody(model);
        }
    }

    protected static void createLegLModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
                buildHumanLegL(model);
        }
        // Check if we have a body, and if so, make the legs a child of it
        if(model.getAllRenderers().containsKey("BODY") && model.getAllRenderers().containsKey("LEGL"))
            model.getAllRenderers().get("BODY").addChild(model.getAllRenderers().get("LEGL"));
    }

    protected static void createLegRModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
            buildHumanLegR(model);
        }
        // Check if we have a body, and if so, make the legs a child of it
        if(model.getAllRenderers().containsKey("BODY") && model.getAllRenderers().containsKey("LEGR"))
            model.getAllRenderers().get("BODY").addChild(model.getAllRenderers().get("LEGR"));
    }

    protected static void createArmLModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
                buildHumanArmL(model);
        }
        // Check if we have a body, and if so, make the arms a child of it
        if(model.getAllRenderers().containsKey("BODY") && model.getAllRenderers().containsKey("ARML"))
            model.getAllRenderers().get("BODY").addChild(model.getAllRenderers().get("ARML"));
    }

    protected static void createArmRModel(ModelPMWitchMinion model, int type){
        switch(type){
            default:
                buildHumanArmR(model);
        }
        // Check if we have a body, and if so, make the legs a child of it
        if(model.getAllRenderers().containsKey("BODY") && model.getAllRenderers().containsKey("ARMR"))
            model.getAllRenderers().get("BODY").addChild(model.getAllRenderers().get("ARMR"));
    }

    /*******************************\
     * BUILD MODELRENDERER OBJECTS *
    \*******************************/

    // The following methods were built from copy-pasted from net/minecraft/client/model/ModelBiped.java
    protected static void buildHumanHead(ModelPMWitchMinion model){
        ModelRenderer head = new ModelRenderer(model, 0, 0);
        head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);

        model.addModelRenderer("HEAD",head);
    }

    protected static void buildHumanLegL(ModelPMWitchMinion model){
        ModelRenderer lleg = new ModelRenderer(model, 0, 16);
        lleg.mirror = true;
        lleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        lleg.setRotationPoint(1.9F, 12.0F, 0.0F);

        model.addModelRenderer("LEGL",lleg);
    }

    protected static void buildHumanLegR(ModelPMWitchMinion model){
        ModelRenderer rleg = new ModelRenderer(model, 0, 16);
        rleg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4);
        rleg.setRotationPoint(-1.9F, 12.0F, 0.0F);

        model.addModelRenderer("LEGR",rleg);
    }

    protected static void buildHumanArmL(ModelPMWitchMinion model){
        ModelRenderer larm = new ModelRenderer(model, 40, 16);
        larm.mirror = true;
        larm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4);
        larm.setRotationPoint(5.0F, 2.0F, 0.0F);

        model.addModelRenderer("ARML",larm);
    }

    protected static void buildHumanArmR(ModelPMWitchMinion model){
        ModelRenderer rarm = new ModelRenderer(model, 40, 16);
        rarm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4);
        rarm.setRotationPoint(-5.0F, 2.0F, 0.0F);

        model.addModelRenderer("ARMR",rarm);
    }

    protected static void buildHumanBody(ModelPMWitchMinion model){
        ModelRenderer body = new ModelRenderer(model, 16, 16);
        body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
        body.setRotationPoint(0.0F, 0.0F, 0.0F);

        model.addModelRenderer("BODY",body);
    }

    /*************************************\
     * TOP-LEVEL TEXTURE BUILDING METHOD *
    \*************************************/

    public static ResourceLocation createTexture(EntityPMWitchMinion entity, ModelPMWitchMinion model){
        // TODO: Finish this method
        return null;
    }
}
