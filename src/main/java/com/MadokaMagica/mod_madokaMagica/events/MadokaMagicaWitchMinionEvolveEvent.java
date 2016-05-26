package com.MadokaMagica.mod_madokaMagica.events;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;

import cpw.mods.fml.common.eventhandler.Event;

public class MadokaMagicaWitchMinionEvolveEvent extends Event {
	public EntityPMWitchMinion entity;
	public MadokaMagicaWitchMinionEvolveEvent(EntityPMWitchMinion entity){
		super();
		this.entity = entity;
	}
}
