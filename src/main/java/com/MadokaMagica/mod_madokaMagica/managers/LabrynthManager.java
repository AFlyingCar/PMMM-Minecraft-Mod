package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import net.minecraftforge.common.DimensionManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.WorldServer;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;

// TODO: Finish this file

public class LabrynthManager{
	private static LabrynthManager instance;

    private EntityPMWitchLabrynthEntrance[] entrances;
    private int next;

    private List<LabrynthDetails> allDetails;

	private LabrynthManager(){
        entrances = new EntityPMWitchLabrynthEntrance[4096];
        next = 0;
		// TODO: Finish this method
	}

    public LabrynthDetails getDetailsByDimID(int id){
        for(LabrynthDetails details : allDetails){
            if(details.dimID == id){
                return details;
            }
        }
        return null;
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

    /*
     * Labrynth Details - related methods
     */

    public void registerLabrynthDetails(LabrynthDetails details){
        allDetails.add(details);
    }

    public void saveAll(){
        File file = getLabrynthDataFile();
        NBTTagCompound nbt = new NBTTagCompound();

        int count = 0;

        for(int i=0; i<allDetails.size(); i++){
            LabrynthDetails details = allDetails.get(i);
            // Do not save the labrynth if it has been marked to be destroyed
            if(details.markForDestruction) continue;

            NBTTagCompound dnbt = new NBTTagCompound();
            save(details,dnbt);

            nbt.setTag(""+i,dnbt);

            // Do this here because we don't want to count the details marked for destruction
            count++;
        }
        nbt.setInteger("MAX DETAILS",count);

        try{
            CompressedStreamTools.write(nbt,file);
        }catch(IOException exception){
            System.out.println("FATAL ERROR: UNABLE TO WRITE LABRYNTH DATA");
            exception.printStackTrace();
            return;
            //throw exception;
            // Simply return rather than rethrowing the exception because Java won't let us unless we handle it at some point.
            // Well what if I don't want to handle this exception Java?
            // What if I want Minecraft to handle it for me
            // What if I want to create a crash report for the player?!
            // DID YOU EVER THINK OF THAT?!
        }
    }

    public void save(LabrynthDetails details,NBTTagCompound nbt){
        nbt.setString("dimName",details.dimName);
        nbt.setInteger("dimID",details.dimID);
        details.world.writeToNBT(nbt);
    }

    // From file
    public void loadAll(){
        File file = getLabrynthDataFile();
        if(!file.exists()){
            // We don't need to bother trying to read labrynth data if none was written
            // That would just be silly
            System.out.println("No Labrynth data file found.");
            return;
        }
        NBTTagCompound nbt;

        try{
            nbt = CompressedStreamTools.read(file);
        }catch(IOException exception){
            System.out.println("FATAL ERROR: UNABLE TO READ LABRYNTH DATA");
            exception.printStackTrace();
            //throw exception;
            return;
        }

        int detailsNum = nbt.getInteger("MAX DETAILS");

        for(int i=0; i<detailsNum; i++){
            String id = ""+i;
            NBTTagCompound dnbt = nbt.getCompoundTag(id);
            load(dnbt);
        }
    }

    public File getLabrynthDataFile(){
        String rootPath = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath();
        File newFile = new File(rootPath+"MadokaMagica/LabrynthSaveData.dat");
        return newFile;
    }

    public void load(NBTTagCompound nbt){
        LabrynthDetails details = new LabrynthDetails();
        details.dimID = nbt.getInteger("dimID");
        details.dimName = nbt.getString("dimName");

        details.world = LabrynthWorldServer.loadFromNBT(nbt);

        registerDetailsWithDimensionManager(details);

        this.allDetails.add(details);
    }

    public void registerDetailsWithDimensionManager(LabrynthDetails details){
        DimensionManager.registerDimension(details.dimID,MadokaMagicaConfig.labrynthProviderID);
        DimensionManager.initDimension(details.dimID);
        DimensionManager.setWorld(details.dimID,details.world);
    }

    /*
     * Methods related to Labrynth Entrances
     */

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
