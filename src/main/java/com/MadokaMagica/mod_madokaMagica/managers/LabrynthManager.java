package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.HashMap;
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
import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;

// TODO: Finish this file

public class LabrynthManager{
	private static LabrynthManager instance;

    private EntityPMWitchLabrynthEntrance[] entrances;
    private int next;

    private List<LabrynthDetails> allDetails;
    private Map<UUID,LabrynthDetails> entranceToDetailsMap;

    private boolean dirty;
    private boolean hasLoaded;

	private LabrynthManager(){
        entrances = new EntityPMWitchLabrynthEntrance[4096];
        allDetails = new ArrayList<LabrynthDetails>();
        entranceToDetailsMap = new HashMap<UUID,LabrynthDetails>();
        dirty = true; // Save the first time it is loaded no matter what (This is for testing purposes)
        next = 0;
        hasLoaded = false;
		// TODO: Finish this method
	}

    public List<LabrynthDetails> getDetailsList(){
        return allDetails;
    }

    public LabrynthDetails getDetailsByDimID(int id){
        for(LabrynthDetails details : allDetails){
            if(details.dimID == id){
                return details;
            }
        }
        return null;
    }

    public LabrynthDetails getDetailsByEntrance(EntityPMWitchLabrynthEntrance epmwle){
        if(entranceToDetailsMap.containsKey(epmwle.getPersistentID())){
            return entranceToDetailsMap.get(epmwle.getPersistentID());
        }
        return null;
    }

    public LabrynthDetails getDetailsByEntranceUUID(UUID uuid){
        if(entranceToDetailsMap.containsKey(uuid)){
            return entranceToDetailsMap.get(uuid);
        }
        return null;
    }

    public WorldServer loadLabrynth(EntityPMWitch epmw){
        // TODO: Delete this method
        // Here because EntityPMWitchLabrynthEntrance depends on it, but it shouldn't
        // However I don't feel like fixing it yet
        // Do this soon future me pls
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
        entranceToDetailsMap.clear();
        dirty = false;
    }

	public static LabrynthManager getInstance(){
		if(instance == null)
			instance = new LabrynthManager();
		return instance;
	}

    /*
     * Labrynth Details - related methods
     */

    @Deprecated
    public void registerLabrynthDetails(LabrynthDetails details){
        allDetails.add(details);
        this.markDirty(true);
    }

    public void registerLabrynthDetails(EntityPMWitchLabrynthEntrance epmwle, LabrynthDetails details){
        entranceToDetailsMap.put(epmwle.getPersistentID(),details);
        allDetails.add(details);
        this.markDirty(true);
    }

    public void registerLabrynthDetails(UUID uuid, LabrynthDetails details){
        entranceToDetailsMap.put(uuid,details);
        this.markDirty(true);
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

        System.out.println("Building LabrynthDetails NBT structure.");
        int count = 0;
        int i=-1;
        for(Map.Entry<UUID,LabrynthDetails> entry : entranceToDetailsMap.entrySet()){
            i++;
            UUID entranceUUID = entry.getKey();
            LabrynthDetails details = entry.getValue();

            if(details.markForDestruction){
                System.out.println("Labrynth #" + i + " has been found to be marked for destruction. Deleting.");
                deleteLabrynth(details.dimID,true);
                continue;
            }

            NBTTagCompound uld_nbt = new NBTTagCompound();

            NBTTagCompound u_nbt = new NBTTagCompound();
            u_nbt.setLong("MOST_SIG",entranceUUID.getMostSignificantBits());
            u_nbt.setLong("LEAST_SIG",entranceUUID.getLeastSignificantBits());
            uld_nbt.setTag("UUID",u_nbt);

            NBTTagCompound ld_nbt = new NBTTagCompound();
            ld_nbt.setInteger("dimID",details.dimID);
            ld_nbt.setString("dimName",details.dimName);
            uld_nbt.setTag("LAB_DETAILS",ld_nbt);

            nbt.setTag(""+i,uld_nbt);
            count++;
        }
        System.out.println("Built an NBT Structure of " + count + " Labrynths");

        /*
        for(int i=0; i<allDetails.size(); i++){
            LabrynthDetails details = allDetails.get(i);
            // Do not save the labrynth if it has been marked to be destroyed
            if(details.markForDestruction){
                // Attempt to delete the labrynth's save file too
                deleteLabrynth(details.dimID,true);
                continue;
            }

            NBTTagCompound dnbt = new NBTTagCompound();
            save(details,dnbt);

            nbt.setTag(""+i,dnbt);

            // Do this here because we don't want to count the details marked for destruction
            count++;
        }
        */
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
        NBTTagCompound ld_nbt = nbt.getCompoundTag("LAB_DETAILS");

        LabrynthDetails details = new LabrynthDetails();
        details.dimID = ld_nbt.getInteger("dimID");
        details.dimName = ld_nbt.getString("dimName");

        System.out.println("Attempting to load dimension #" + details.dimID + "("+details.dimName+")");

        details.world = LabrynthWorldServer.loadFromNBT(ld_nbt);

        if(details.world != null){
            DimensionManager.registerDimension(details.dimID,MadokaMagicaConfig.labrynthProviderID);
            DimensionManager.setWorld(details.dimID,details.world);
        }

        NBTTagCompound u_nbt = nbt.getCompoundTag("UUID");
        UUID uuid = new UUID(u_nbt.getLong("MOST_SIG"),u_nbt.getLong("LEAST_SIG"));

        this.entranceToDetailsMap.put(uuid,details);

        this.allDetails.add(details);
    }

    /*
     * Methods related to Labrynth Entrances
     */

    public int storeLabrynthEntranceToRetrieveLater(EntityPMWitchLabrynthEntrance entity){
        // Search for the next non-null slot
        while(entrances[++next] != null)
            if(next == entrances.length-1)
                return -1;
        entrances[next] = entity;
        // If we've reached the end, head back to the beginning so we can restart
        if(next == entrances.length-1){
            next = 0;
            return entrances.length-1;
        }
        return next;
    }

    public EntityPMWitchLabrynthEntrance retrieveEntrance(int id){
        EntityPMWitchLabrynthEntrance entrance = entrances[id];
        // Set it to null so we don't fill up entrances
        entrances[id] = null;
        return entrance;
    }

    // NOTE: I don't think we need this method any more
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

    public boolean deleteLabrynth(int id,boolean deleteFolder){
        // NOTE: We must be very careful here, we don't want to delete the wrong dimension after all
        // Make sure we cast this so that it will throw an error if the world isn't a LabrynthWorldServer, which all Labrynths should have and which nothing else should have
        /*
        LabrynthWorldServer world = (LabrynthWorldServer)DimensionManager.getWorld(id);
        LabrynthDetails details = world.details;
        */
        LabrynthDetails details = this.getDetailsByDimID(id);

        // Make sure to fail if the id is not a valid dimension
        if(details == null){
            return false;
        }


        // Call this method out here because it unloads the dimension on purpose.
        deleteData(details);

        // Make sure the world isn't in use before we attempt to delete it
        if(DimensionManager.getWorld(details.dimID) == null){
            if(deleteFolder){
                deleteFiles(details);
            }

            markDirty(true);
            return true;
        }

        return false;
    }

    // Deletes data from disk
    private void deleteFiles(LabrynthDetails details){
        String rootSavePath = DimensionManager.getCurrentSaveRootDirectory().getAbsolutePath();
        File savePath = new File(rootSavePath + "/MadokaMagicaLabrynths/" + details.dimID);
        Helper.deleteFolderRecursively(savePath);
    }

    // Deletes the data from memory
    private void deleteData(LabrynthDetails details){
        // TODO: Should we check if any players are in this dimension and teleport them out?
        //  Or should we assume that the methods which mark this for deletion will do that for us? 

        allDetails.remove(details); // Not many places to delete it from

        // Don't attempt to unregister it unless we know it is registered
        if(DimensionManager.isDimensionRegistered(details.dimID)){
            DimensionManager.setWorld(details.dimID,null); // Make sure the world is unloaded first
            DimensionManager.unregisterDimension(details.dimID);
        }

        UUID mine=null;
        for(Map.Entry<UUID,LabrynthDetails> entry : entranceToDetailsMap.entrySet()){
            if(entry.getValue() == details){
                mine = entry.getKey();
                break;
            }
        }
        if(mine != null)
            entranceToDetailsMap.remove(mine);
    }
}
