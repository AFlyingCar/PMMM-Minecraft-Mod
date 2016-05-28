package com.MadokaMagica.mod_madokaMagica.entities;

import net.minecraft.entity.monster.EntityMob;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

public class EntityPMWitch extends EntityMob{
	public PMDataTracker tracker;
	public EntityPMWitchLabrynthEntrance entrance;
	public EntityPMWitch(PMDataTracker pd){
		super(pd.player.worldObj);
	}
}

