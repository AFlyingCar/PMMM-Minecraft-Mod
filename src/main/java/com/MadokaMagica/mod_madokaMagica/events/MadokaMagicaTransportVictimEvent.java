package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

public class MadokaMagicaTransportVictimEvent extends Event {
    public EntityLivingBase victim;
    public EntityPMWitchLabrynthEntrance entrance;

    public MadokaMagicaTransportVictimEvent(EntityPMWitchLabrynthEntrance entrance, EntityVillager villager){
        super();
        victim = villager;
        this.entrance = entrance;
    }

    public MadokaMagicaTransportVictimEvent(EntityPMWitchLabrynthEntrance entrance, EntityPlayer player){
        super();
        victim = player;
        this.entrance = entrance;
    }

    @Override
    public boolean isCancelable(){
        return true;
    }
}
