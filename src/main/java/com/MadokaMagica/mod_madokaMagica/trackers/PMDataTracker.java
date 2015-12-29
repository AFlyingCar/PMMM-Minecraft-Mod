package com.MadokaMagica.mod_madokaMagica.trackers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;

public class PMDataTracker {
    public static final int MAX_POTENTIAL = 100;
	private EntityPlayer player; // The player being tracked
    private ItemSoulGem playerSoulGem = null;

	// Track the Player's likes and dislikes
	private Map<Integer,Float> like_entity_type = null; // A certain type of entity
	private Map<Integer,Float> liked_entities = null; // A specific entity
	private Map<Integer,Float> like_level = null;
	private float like_building = 0;
	private float like_fighting = 0;
	private float like_water = 0;
	private float like_night = 0;
	private float like_day = 0;
    private float hero = 0;

    /*
     * 0 - Normal human
     * 1 - Puella Magi
     * 2 - Witch
     */
    private int player_state = 0;

	private float potential;

	private boolean ready;

    private boolean countingRain = false;
    private float rainStartTime;
    private boolean countingNight = false;
    private float nightStartTime;
    // private boolean countingDay = false;
    // private long dayStartTime;

    private float underground_start_time = -1;
    private Map<Entity,Float> nearbyEntitiesMap;

	public PMDataTracker(EntityPlayer nplayer){
		player = nplayer;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();

        potential = calculatePotential();
	}

    public PMDataTracker(EntityPlayer nplayer, ItemSoulGem nplayerSG){
        this(nplayer);
        playerSoulGem = nplayerSG;
    }

	public boolean isReady(){
		return ready;
	}

	public void loadTagData(){
		NBTTagCompound tags = player.getEntityData();
		if(tags.hasKey("PM_POTENTIAL")){
			potential = tags.getFloat("PM_POTENTIAL");
		}
        if(tags.hasKey("PM_LIKES_BUILDING")){
			like_building = tags.getFloat("PM_LIKES_BUILDING");
		}
        if(tags.hasKey("PM_LIKES_FIGHTING")){
			like_fighting = tags.getFloat("PM_LIKES_FIGHTING");
		}
        if(tags.hasKey("PM_LIKES_WATER")){
			like_water = tags.getFloat("PM_LIKES_WATER");
		}
        if(tags.hasKey("PM_LIKES_NIGHT")){
			like_night = tags.getFloat("PM_LIKES_NIGHT");
		}
        if(tags.hasKey("PM_LIKES_DAY")){
			like_day = tags.getFloat("PM_LIKES_DAY");
		}
        if(tags.hasKey("PM_LIKES_LEVEL")){
			int[] level_data = tags.getIntArray("PM_LIKES_LEVEL");
            float like_amt = Helper.unpackFloat(level_data[1]);
            like_level.put(new Integer(level_data[0]),new Float(like_amt));
		}
        if(tags.hasKey("PM_LIKES_ENTITY")){
            // list = [Integer,Float,Integer,Float,Integer,Float,...]
            int[] list = tags.getIntArray("PM_LIKES_ENTITY");
            // Maybe implement a sanity check here?
            //   list MUST have an even number of elements
            for(int i=0; i < list.length; i++){
                int id=0;
                float val=0.0F;
                if(i%2 == 0)
                    id=list[i];
                else
                    val=Helper.unpackFloat(list[i]);
                liked_entities.put(new Integer(id),new Float(val));
            }
        }
        if(tags.hasKey("PM_LIKES_ENTITY_TYPE")){
            int[] list = tags.getIntArray("PM_LIKES_ENTITY_TYPE");
            // Maybe implement a sanity check here?
            //   list MUST have an even number of elements
            for(int i=0; i<list.length;i++){
                int id=0;
                float val=0.0F;
                if(i%2==0)
                    id=list[i];
                else
                    val=Helper.unpackFloat(list[i]);
                like_entity_type.put(new Integer(id),new Float(val));
            }
        }
        if(tags.hasKey("PM_HERO")){
            hero = tags.getFloat("PM_HERO");
        }
        if(tags.hasKey("PM_PLAYER_STATE")){
            player_state = tags.getInteger("PM_PLAYER_STATE");
        }
	}

    public void updateData(){
        // We don't track anything anymore if the player has turned into a witch
        if(isWitch())
            return;

        // DOES THE PLAYER LIKE ENTITIES
        // =============================
        cleanNearbyEntitiesMap();

        List allentities = Helper.getNearbyEntities(player);

        for(Object entity : allentities){
            if(!(entity instanceof EntityLivingBase)) continue;

            EntityLivingBase e = (EntityLivingBase)entity;
            if(Helper.nearEntityRecently(e,nearbyEntitiesMap)){
                // Do something to determine how long we've been near them
                // Then add to liked_entities
            }else{
                nearbyEntitiesMap.put(e,new Float(e.worldObj.getTotalWorldTime()));
            }
        }

        // DOES THE PLAYER LIKE WATER
        // ==========================
        if(player.worldObj.isRaining()){
            if(!countingRain)
                rainStartTime = player.worldObj.getTotalWorldTime();
            countingRain = true;
        }else{
            countingRain = false;
        }

        if(player.isPlayerFullyAsleep()){
            if(countingRain){
                // do something negatively to the player's like_water score
            }
            countingRain=false; // regardless of whether or not we are already counting rain, make it false anyway.
        }
        if(!countingRain){
            // Do something here to calculate like_water based on how long rain has been happening.
            // Do some multiply by rainStartTime to make sure that we don't do some weird calculations if it wasn't raining.
            rainStartTime = 0;
        }

        // DOES THE PLAYER LIKE THE NIGHT
        // ==============================
        if(!player.worldObj.isDaytime()){
            if(!countingNight)
                nightStartTime = player.worldObj.getTotalWorldTime();
            countingNight = true;
        }else{
            countingNight = false;
        }

        if(player.isPlayerFullyAsleep()){
            if(countingNight){
                // do something negatively to the player's like_night score
            }
            countingNight = false;
        }
        if(!countingNight){
            // Do something here to calculate like_night based on how long night has been happening.
            // Do some multiply by nightStartTime to make sure that we don't do some weird calculations if it wasn't night.
        }

        // DOES THE PLAYER LIKE TO FIGHT
        // =============================
        // I don't even know how to start with this one.
        // I'll leave it for another time.
        // Just know that it will have something to do with the player's like_night score

        // IS THE PLAYER A HERO
        // ====================
        // It would have something to do with villages
        // Does the player fight while inside a village
    }

    // NOTE: Maybe do something here if Entity e is in liked_entities?
    private void cleanNearbyEntitiesMap(){
        float ctime = player.worldObj.getTotalWorldTime();
        for(Entry<Entity,Float> e : nearbyEntitiesMap.entrySet())
            // If the player has not been near Entity e for longer than twice the tolerance length...
            if(nearbyEntitiesMap.get(e.getKey()).floatValue() >= (Helper.timeTolerance*2))
                nearbyEntitiesMap.remove(e.getKey());
    }

    private float calculatePotential(){
        MinecraftServer server = MinecraftServer.getServer();
        int pAmt = server.getCurrentPlayerCount();
        float worldAge = server.getEntityWorld().getTotalWorldTime();
        int pexp = player.experienceLevel;
        int dimModifier = getDimensionModifier();
        return ((MAX_POTENTIAL/pAmt) - (worldAge%(MAX_POTENTIAL/10))) + pexp + dimModifier;
    }

    private int getDimensionModifier(){
        int overworldImportance = 10;
        int netherImportance = 20;
        int theendImportance = 50;
        int total = 0;

        // Do something to determine if the player has been to each dimension
        // I can't figure it out right now.
        return overworldImportance;
    }

    public void setPlayerState(int state){
        player_state = state;
    }

    public boolean isWitch(){
        return player_state == 2;
    }

    public boolean isPuellaMagi(){
        return player_state == 1;
    }

    public int getPlayerState(){
        return player_state;
    }

    public EntityPlayer getPlayer(){
        return this.player;
    }
}
