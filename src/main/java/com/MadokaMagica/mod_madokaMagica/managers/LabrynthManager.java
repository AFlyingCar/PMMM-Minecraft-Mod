package com.MadokaMagica.mod_madokaMagica.managers;

import net.minecraft.world.WorldServer;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;

// TODO: Finish this file

public class LabrynthManager{
	private static LabrynthManager instance;

	private LabrynthManager(){
		// TODO: Finish this method
	}

	public WorldServer loadLabrynth(EntityPMWitch witch){
		// TODO: Finish this method
		return null;
	}

	public static LabrynthManager getInstance(){
		if(instance == null)
			instance = new LabrynthManager();
		return instance;
	}
}
