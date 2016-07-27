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

	private PlayerDataTrackerManager(){
		datatrackers = new HashMap<UUID,PMDataTracker>();
	}

	public void addDataTracker(PMDataTracker tracker){
        datatrackers.put(tracker.getEntityUUID(),tracker);
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

    public void saveAllTrackers(){
        for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            saveTracker(trackerset.getValue());
        }
        writePersistentFile();
    }

    @Deprecated
    public PMDataTracker getTrackerByUsername(String name){
        if(datatrackers.containsKey(name))
            return datatrackers.get(name);
        return null;
    }

	public void manage(){
		for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            trackerset.getValue().incrementDataTimer();

            if(trackerset.getValue().getUpdateDataTime() >= 10){
                trackerset.getValue().updateData();
                // PMEffects.applyPlayerEffects(trackerset.getValue());
            }
        }
	}

    public HashMap<UUID,PMDataTracker> getTrackers(){
        return datatrackers;
    }

    public boolean loadPersistentFile(){
        System.out.println("Loading Persistent File.");
        File file = getPDTMDataFile();
        if(file == null){
            System.out.println("ERROR: getPDTMDataFile returned null. Unable to load Persistent Data for PlayerDataTrackerManager");
            return false;
        }
        NBTTagCompound data;

        try{
            data = CompressedStreamTools.read(file);
        }catch(IOException exception){
            System.out.println("An error occurred when reading persistent data for PlayerDataTrackerManager.");
            exception.printStackTrace();
            return false;
        }

        if(!data.hasKey("TrackerAmount")){
            System.out.println("ERROR: PlayerDataTrackerManagerSaveData.dat does not contain a tag TrackerAmount. Unable to continue loading data.");
            return false;
        }
        int trackerAmount = data.getInteger("TrackerAmount");
        int c=0;

        for(int i=0; i<trackerAmount;i++){
            NBTTagCompound trackerNBT = data.getCompoundTag(""+i);
            if(trackerNBT.hasKey("UUID_MOST_SIG") && trackerNBT.hasKey("UUID_LEAST_SIG")){
                // Load up an empty data tracker, which will be filled later
                // This way, every UUID that has existed will have a data tracker in memory, even if that data tracker is empty
                // This is fine, since it will actually be loaded anyways (and they should never be used when a player isn't logged in/a witch doesn't exist)
                datatrackers.put(new UUID(trackerNBT.getLong("UUID_MOST_SIG"),
                                          trackerNBT.getLong("UUID_LEAST_SIG")
                                         ),
                                 new PMDataTracker()
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

        NBTTagCompound data = new NBTTagCompound();
        int i = 0;
        for(Entry<UUID,PMDataTracker> trackerset : datatrackers.entrySet()){
            NBTTagCompound trackerEntry = new NBTTagCompound();
            trackerEntry.setLong("UUID_MOST_SIG",trackerset.getKey().getMostSignificantBits());
            trackerEntry.setLong("UUID_LEAST_SIG",trackerset.getKey().getLeastSignificantBits());
            trackerset.getValue().writeTags(trackerEntry);
            data.setTag(""+i,trackerEntry);
        }
        System.out.println("Saving Signatures for " + i + " valid data trackers.");
        data.setInteger("TrackerAmount",i);

        try{
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

	public static PlayerDataTrackerManager getInstance(){
		if(PlayerDataTrackerManager.instance == null)
			PlayerDataTrackerManager.instance = new PlayerDataTrackerManager();
		return PlayerDataTrackerManager.instance;
	}
}

