package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import java.io.IOException;
import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.effects.PMEffects;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class PlayerDataTrackerManager{
	private HashMap<UUID,PMDataTracker> datatrackers;
	private static PlayerDataTrackerManager instance;

    private boolean hasLoaded;

	private PlayerDataTrackerManager(){
		datatrackers = new HashMap<UUID,PMDataTracker>();
        hasLoaded = false;
	}

    // WARNING! This method will not save all data! We must make sure that this data is saved before calling this method!
    public void unloadAllData(){
        unloadAllData(false);
    }

    public void unloadAllData(boolean safe) {
        // TODO: Add dirty safety checks
        System.out.println("Unloading data for PlayerDataTrackerManager");
        datatrackers.clear(); // Clear the list
    }

	public void addDataTracker(PMDataTracker tracker){
        datatrackers.put(tracker.getEntityUUID(),tracker);
	}

    // Will add the specified tracker to map if a tracker doesn't already exist
    // If the specified UUID already exists, then it is overwritten
    public void setDataTracker(UUID uuid, PMDataTracker tracker){
        UUID newUUID = uuid;
        for(Entry<UUID,PMDataTracker> entry : datatrackers.entrySet()){
            if(entry.getKey().equals(uuid)){
                newUUID = entry.getKey();
            }
        }
        datatrackers.put(newUUID,tracker);
    }

    public PMDataTracker getTrackerByUUID(UUID uuid){
        if(datatrackers.containsKey(uuid))
            return datatrackers.get(uuid);
        return null;
    }

    @Deprecated
    public PMDataTracker getTrackerByIdentifier(String identifier){
        if(datatrackers.containsKey(identifier))
            return datatrackers.get(identifier);
        return null;
    }

    public void saveAndRemoveTracker(PMDataTracker pmdt){
        this.saveTracker(pmdt);
        if(datatrackers.containsValue(pmdt))
            return;
    }

    public void saveTracker(PMDataTracker pmdt){
        // TODO: Find a better place to save this data (maybe in the persistent file?)
        // We cannot write to the entity's data if it is dead, and we want these to persist for as long as possible
        NBTTagCompound tags = pmdt.entity.getEntityData();
        pmdt.writeTags(tags);
    }

    /*
    public void saveAllTrackers(){
        for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            saveTracker(trackerset.getValue());
        }
        writePersistentFile();
    }
    */

    @Deprecated
    public PMDataTracker getTrackerByUsername(String name){
        if(datatrackers.containsKey(name))
            return datatrackers.get(name);
        return null;
    }

	public void manage(){
        List<UUID> uuidsToRemove = new ArrayList<UUID>();
		for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            if(trackerset.getKey() == null || trackerset.getValue() == null){
                System.out.println("Found null UUID or PMDataTracker in datatrackers. Removing.");
                uuidsToRemove.add(trackerset.getKey());
                continue;
            }
            trackerset.getValue().incrementDataTimer();

            if(trackerset.getValue().getUpdateDataTime() >= 10){
                trackerset.getValue().updateData();
                // PMEffects.applyPlayerEffects(trackerset.getValue());
            }
        }

        // Clean up the list if we've found bad values.
        for(UUID u : uuidsToRemove){
            datatrackers.remove(u);
        }
	}

    public HashMap<UUID,PMDataTracker> getTrackers(){
        return datatrackers;
    }

    // Returns true on data being successfully loaded, false otherwise
    // Will return true if the file is not found (not necessarily a fatal error, but needs to be checked anyways)
    public boolean loadPersistentFile(){
        System.out.println("Loading Persistent File.");
        File file = getPDTMDataFile();
        if(file == null){
            System.out.println("ERROR: getPDTMDataFile returned null. Unable to load Persistent Data for PlayerDataTrackerManager");
            return false;
        }

        // This should fix the issue of the file not existing on a new world being created.
        if(!file.exists()){
            System.out.println("No PMDataTracker Persistent Data File found.");
            this.setHasLoaded(true);
            return true; // return true here so that we don't try again (if we didn't find it the first time, obviously we aren't going to find it again
        }

        NBTTagCompound data = null;

        try{
            data = CompressedStreamTools.read(file);
        }catch(IOException exception){
            System.out.println("An error occurred when reading persistent data for PlayerDataTrackerManager.");
            exception.printStackTrace();
            return false;
        }

        if(data == null){
            System.out.println("OH GOD WHAT THE HELL JUST HAPPENED?! WHY THE FUCK IS data NULL?!");
            System.out.println("FUCK IT! ABORT ABORT!");
            return false;
        }

        if(!data.hasKey("TrackerAmount")){
            System.out.println("ERROR: PlayerDataTrackerManagerSaveData.dat does not contain a tag TrackerAmount. Unable to continue loading data.");
            return false;
        }
        int trackerAmount = data.getInteger("TrackerAmount");
        int c=0;

        System.out.println("Found " + trackerAmount + " possible data trackers.");

        for(int i=0; i<trackerAmount;i++){
            System.out.println("Attempting to load data tracker #"+(i+1));
            NBTTagCompound trackerNBT = data.getCompoundTag(""+i);
            if(trackerNBT.hasKey("UUID_MOST_SIG") && trackerNBT.hasKey("UUID_LEAST_SIG")){
                // Load up an empty data tracker, which will be filled later
                // This way, every UUID that has existed will have a data tracker in memory, even if that data tracker is empty
                // This is fine, since it will actually be loaded anyways (and they should never be used when a player isn't logged in/a witch doesn't exist)
                PMDataTracker tracker = new PMDataTracker();
                tracker.tagData = trackerNBT;
                if(!tracker.loadTagData()){
                    System.out.println("Loading tracker from disk failed: " + tracker.getFailureMsg());
                    tracker = null; // Make it null
                }
                this.setDataTracker(new UUID(trackerNBT.getLong("UUID_MOST_SIG"),
                                             trackerNBT.getLong("UUID_LEAST_SIG")
                                            ),
                                    tracker
                                   );
                c++;
            }else{
                System.out.println("ERROR: A saved data tracker is missing required tags UUID_MOST_SIG or UUID_LEAST_SIG. Unable to proceed with loading PMDataTracker #" + i);
            }
        }

        System.out.println("Found " + c + " valid Trackers.");
        return true;
    }

    public boolean writePersistentFile(){
        File file = getPDTMDataFile();
        if(file == null){
            System.out.println("ERROR: getPDTMDataFile returned null. Unable to save Persistent Data for PlayerDataTrackerManager");
            return false;
        }

        /*
         * NBT:
         *  0:
         *    UUID
         *    PMDataTracker
         *  1:
         *    UUID
         *    PMDataTracker
         *  etc...
         *  TrackerAmount
         */

        NBTTagCompound data = new NBTTagCompound();
        int i = 0;
        for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            NBTTagCompound trackerEntry = new NBTTagCompound();
            trackerEntry.setLong("UUID_MOST_SIG",trackerset.getKey().getMostSignificantBits());
            trackerEntry.setLong("UUID_LEAST_SIG",trackerset.getKey().getLeastSignificantBits());
            trackerset.getValue().writeTags(trackerEntry);
            data.setTag(""+i,trackerEntry);
            i++;
        }
        System.out.println("Saving Signatures for " + i + " valid data trackers.");
        data.setInteger("TrackerAmount",i);

        try{
            // Make sure that this file exists so that we don't lose our data
            file.createNewFile();
            CompressedStreamTools.write(data,file);
        }catch(IOException exception){
            System.out.println("An error occurred when writing persistent data for PlayerDataTrackerManager.");
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    public File getPDTMDataFile(){
        try{
            String rootPath = DimensionManager.getCurrentSaveRootDirectory().getCanonicalPath();
            File newFile = new File(rootPath+"/MadokaMagica/PlayerDataTrackerManagerSaveData.dat");
            return newFile;
        }catch(IOException exception){
            exception.printStackTrace();
            return null;
        }
    }

    public boolean havePMDataTrackersBeenLoaded(){
        return hasLoaded;
    }

    public void setHasLoaded(boolean loaded){
        hasLoaded = loaded;
    }

    public boolean isDirty(){
        for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            if(trackerset.getValue().isDirty())
                return true;
        }
        return false;
    }

	public static PlayerDataTrackerManager getInstance(){
		if(PlayerDataTrackerManager.instance == null)
			PlayerDataTrackerManager.instance = new PlayerDataTrackerManager();
		return PlayerDataTrackerManager.instance;
	}
}

