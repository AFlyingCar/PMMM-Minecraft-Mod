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

    private boolean dirty;
    private boolean hasLoaded;

	private LabrynthManager(){
        entrances = new EntityPMWitchLabrynthEntrance[4096];
        allDetails = new ArrayList<LabrynthDetails>();
        dirty = true; // Save the first time it is loaded no matter what (This is for testing purposes)
        next = 0;
        hasLoaded = false;
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

    // WARNING! This method will not save all data! We must make sure that this data is saved before calling this method!
    public void unloadAllData(){
        System.out.println("Unloading data for LabrynthManager.");
        // Clear the list
        for(int i=0; i<entrances.length; i++){
            entrances[i] = null;
        }
        next = 0;
        // Clear the other list
        allDetails.clear();
        dirty = false;
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
        this.markDirty(false);
    }

    public void markDirty(boolean dirty){
        this.dirty = dirty;
    }

    public boolean isDirty(){
        return this.dirty;
    }

    public void saveAll(){
        this.saveAll(false);
    }

    public void saveAll(boolean force){
        // Don't save if the data isn't dirty or if we aren't supposed to force it
        if(!this.dirty && !force) return;

        File file = getLabrynthDataFile();
        NBTTagCompound nbt = new NBTTagCompound();

        // See Java, now this try-catch makes sense
        try{
            // Create the file if it doesn't already exist
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        }catch(IOException exception){
            String msg = "FATAL ERROR: UNABLE TO WRITE TO ";
            // This one though, does not
            try{
                msg += file.getCanonicalPath();
            }catch(IOException e){
                msg+="LABRYNTH SAVE FILE FILE";
                System.out.println(e.getMessage());
            }
            System.out.println(msg);
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return;
        }

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

        this.dirty = false;
    }

    // Don't check dirty here, because this doesn't actually write any data to a file
    public void save(LabrynthDetails details,NBTTagCompound nbt){
        nbt.setString("dimName",details.dimName);
        nbt.setInteger("dimID",details.dimID);
        details.world.writeToNBT(nbt);
    }

    // From file
    public boolean loadAll(){
        System.out.println("Loading all saved Labrynths...");

        File file = getLabrynthDataFile();
        if(!file.exists()){
            // We don't need to bother trying to read labrynth data if none was written
            // That would just be silly
            System.out.println("No Labrynth data file found.");
            return false;
        }
        NBTTagCompound nbt;

        try{
            nbt = CompressedStreamTools.read(file);
        }catch(IOException exception){
            System.out.println("FATAL ERROR: UNABLE TO READ LABRYNTH DATA");
            exception.printStackTrace();
            //throw exception;
            return false;
        }

        int detailsNum = nbt.getInteger("MAX DETAILS");

        this.allDetails.clear(); // Clear the array if it already has stuff in it (It shouldn't, as we are loading up a new world)

        for(int i=0; i<detailsNum; i++){
            String id = ""+i;
            NBTTagCompound dnbt = nbt.getCompoundTag(id);
            load(dnbt);
        }

        return true;
    }

    public File getLabrynthDataFile(){
        try{
            String rootPath = DimensionManager.getCurrentSaveRootDirectory().getCanonicalPath();
            File newFile = new File(rootPath+"/MadokaMagica/LabrynthSaveData.dat");
            return newFile;
        }catch(IOException e){
            // Why the fuck do I need a whole fucking exception handling just for a fucking canonical path java?!
        }
        // http://imgur.com/QIvjr2b
        System.out.println("OH GOD! WHAT THE FUCK JUST HAPPENED?! AAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        return null;
    }

    public void load(NBTTagCompound nbt){
        LabrynthDetails details = new LabrynthDetails();
        details.dimID = nbt.getInteger("dimID");
        details.dimName = nbt.getString("dimName");

        System.out.println("Attempting to load dimension #" + details.dimID + "("+details.dimName+")");

        details.world = LabrynthWorldServer.loadFromNBT(nbt);

        if(details.world != null){
            DimensionManager.registerDimension(details.dimID,MadokaMagicaConfig.labrynthProviderID);
            DimensionManager.setWorld(details.dimID,details.world);
        }

        this.allDetails.add(details);
    }

    /*
    public void registerDetailsWithDimensionManager(LabrynthDetails details){
        DimensionManager.registerDimension(details.dimID,MadokaMagicaConfig.labrynthProviderID);
        DimensionManager.initDimension(details.dimID);
    }
    */

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

    public boolean haveLabrynthsLoaded(){
        return hasLoaded;
    }

    public void setHasLoaded(boolean loaded){
        hasLoaded = loaded;
    }
}
