package com.MadokaMagica.mod_madokaMagica.managers;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.entities.EntityIncubator;

public class IncubatorManager{
	private static IncubatorManager instance;
	private IncubatorManager(){

	}

	public static IncubatorManager getInstance(){
		if(instance == null)
			instance = new IncubatorManager();
		return instance;
	}

	// TODO: Finish this method.
	public boolean isPlayerNearIncubator(EntityPlayer player){
		return false;
	}

	// TODO: Finish this method.
	public EntityIncubator getNearestIncubator(double posX, double posY, double posZ){
		return null;
	}
}
