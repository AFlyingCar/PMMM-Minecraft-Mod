package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

public class PreMadokaMagicaWitchDeathEvent extends Event {
    public EntityPMWitch entity;
    public PreMadokaMagicaWitchDeathEvent(EntityPMWitch entity){
        super();
        this.entity = entity;
    }

    @Override
    public boolean isCancelable(){
        return true;
    }
}

