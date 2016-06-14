package com.MadokaMagica.mod_madokaMagica.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityCreature;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

public class MadokaMagicaCreateModelEvent extends Event {
	public EntityCreature entity;
	public int entityType; // 0 = Minion, 1 = Witch

	public MadokaMagicaCreateModelEvent(EntityPMWitchMinion entity){
		super();
		this.entity = entity;
		entityType = 0;
	}

	public MadokaMagicaCreateModelEvent(EntityPMWitch entity){
		super();
		this.entity = entity;
		entityType = 1;
	}

	@Override
	public boolean isCancelable(){
		return true;
	}
}
