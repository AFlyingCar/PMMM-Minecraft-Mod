package com.MadokaMagica.mod_madokaMagica.managers;

import net.minecraft.world.WorldServer;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

// TODO: Finish this file

public class LabrynthManager{
	private static LabrynthManager instance;

    private EntityPMWitchLabrynthEntrance[] entrances;
    private int next;

	private LabrynthManager(){
        entrances = new EntityPMWitchLabrynthEntrance[4096];
        next = 0;
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

    public int storeLabrynthEntranceToRetrieveLater(EntityPMWitchLabrynthEntrance entity){
        entrances[++next] = entity;
        return next;
    }

    public EntityPMWitchLabrynthEntrance retrieveEntrance(int id){
        EntityPMWitchLabrynthEntrance entrance = entrances[id];
        // Set it to null so we don't fill up entrances
        entrances[id] = null;
        checkNextAndUpdate();
        return entrance;
    }

    protected void checkNextAndUpdate(){
        // Make sure to reduce next whenever we can so we don't get an empty array where we are at the end and can't add any more entrances
        if(entrances[next] == null){
            for(int i=next-1;i>=0;i--){
                if(entrances[i] == null)
                    next = i; // Keep reducing next until the item at i is not null
                else
                    return;
            }
        }
    }
}
