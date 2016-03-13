package com.MadokaMagica.mod_madokaMagica.trackers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.HashMap;

import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;

import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

public class PMDataTracker {
    public static final int MAX_POTENTIAL = 100;
    public static final int SWING_TOLERANCE = 3; // 3 seconds
    public EntityPlayer player; // The player being tracked
    private ItemSoulGem playerSoulGem = null;

    // Track the Player's likes and dislikes
    private Map<Integer,Float> like_entity_type = null; // A certain type of entity
    private Map<Integer,Float> liked_entities = null; // A specific entity
    private Map<Integer,Float> like_level = null;

    private float architectScore = 0;
    private float engineeringScore = 0;
    private float greedScore = 0;

    private float waterScore = 0;
    private float natureScore = 0;
    private float dayScore = 0;
    private float nightScore = 0;

    private float heroScore = 0;
    private float villainScore = 0;

    private float passiveScore = 0;
    private float aggressiveScore = 0;

    /*
    private float like_building = 0;
    private float like_fighting = 0;
    private float like_water = 0;
    private float like_night = 0;
    private float like_day = 0;
    private float hero = 0;
    */

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

    private long playerswinglasttime;
    private boolean playerswinging;

    private int updatedatatimer = 0;
    private int updateeffectstimer = 0;

    private boolean currentlyTransformingIntoWitch;

    public PMDataTracker(EntityPlayer nplayer){
        player = nplayer;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();

        potential = calculatePotential();
        playerswinglasttime = player.worldObj.getTotalWorldTime();

        loadTagData();
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
        // Get the player's potential
        if(tags.hasKey("PM_POTENTIAL")){
            potential = tags.getFloat("PM_POTENTIAL");
        }

        // Get Hero/Villain scores
        if(tags.hasKey("PM_HERO_SCORE")){
            heroScore = tags.getFloat("PM_HERO_SCORE");
        }
        if(tags.hasKey("PM_VILLAIN_SCORE")){
            villainScore = tags.getFloat("PM_VILLAIN_SCORE");
        }

        // Get Aggressive/Passive score
        if(tags.hasKey("PM_AGGRESSIVE_SCORE")){
            aggressiveScore = tags.getFloat("PM_AGGRESSIVE_SCORE");
        }
        if(tags.hasKey("PM_PASSIVE_SCORE")){
            passiveScore = tags.getFloat("PM_PASSIVE_SCORE");
        }

        // Enviroment-based scores
        if(tags.hasKey("PM_NATURE_SCORE")){
            natureScore = tags.getFloat("PM_NATURE_SCORE");
        }
        if(tags.hasKey("PM_DAY_SCORE")){
            dayScore = tags.getFloat("PM_DAY_SCORE");
        }
        if(tags.hasKey("PM_NIGHT_SCORE")){
            dayScore = tags.getFloat("PM_NIGHT_SCORE");
        }

        // engineering-type score
        if(tags.hasKey("PM_ENGINEERING_SCORE")){
            engineeringScore = tags.getFloat("PM_ENGINEERING_SCORE");
        }
        if(tags.hasKey("PM_ARCHITECT_SCORE")){
            architectScore = tags.getFloat("PM_ARCHITECT_SCORE");
        }
        if(tags.hasKey("PM_GREED_SCORE")){
            greedScore = tags.getFloat("PM_GREED_SCORE");
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

        if(tags.hasKey("PM_PLAYER_STATE")){
            player_state = tags.getInteger("PM_PLAYER_STATE");
        }
    }

    public void updateData(){
        // We don't track anything anymore if the player has turned into a witch
        if(isWitch())
            return;

        // TODO: Rewrite this section
        
        /*
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
        */

        // DOES THE PLAYER LIKE WATER
        // ==========================
        if(player.worldObj.isRaining()){
            if(!countingRain)
                rainStartTime = player.worldObj.getTotalWorldTime();
            countingRain = true;
        }else{
            // Not sure if we want to add to waterScore if not raining
            // this.waterScore += (rainStartTime/player.worldObj.getTotalWorldTime());
            rainStartTime = 0;
            countingRain = false;
        }

        // Subtract from water score if the player tries to skip the rain
        if(player.isPlayerFullyAsleep()){
            if(countingRain)
                this.waterScore -= 1;
        }

        // TODO: Add something here to check if player is near water
        


        this.playerswinging = this.player.isSwingInProgress;
        if(this.playerswinging)
            this.playerswinglasttime = player.worldObj.getTotalWorldTime();

        // DOES THE PLAYER LIKE MINING OR FARMING
        // ======================================
        if(Helper.isEntityUnderground((Entity)player)){
            // TODO: Replace `true` with a check for if the player is breaking blocks
            if(!this.playerswinging){
                // Is the player hiding from the night (or, generally just being active)
                // Only change it by a miniscule amount (1%)
                if(player.worldObj.isDaytime())
                    dayScore -= 0.01;
                else
                    nightScore -= 0.01;
            }
        }else{
            if(Helper.isEntityOutside((Entity)player)){
                // TODO: Add stuff here about whether the player likes to farm
                //  (If so, add 1 to natureScore)
            }
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

        if(countingNight){
            // Is more than half of night left
            // TODO: Change 12000 to whatever half of the night time is
            if((player.worldObj.getTotalWorldTime()-nightStartTime) > 12000){ 
                if(player.isPlayerFullyAsleep()){
                    this.nightScore-=1;
                    this.dayScore+=1;
                }
            }else{
                if((player.worldObj.getTotalWorldTime()-nightStartTime) > (24000-100)){
                    this.nightScore+=1;
                }
            }
        }

        // WHY ARE WE YELLING?

        
        // Saving
        PlayerDataTrackerManager.getInstance().saveTracker(this);
        updatedatatimer = 0;
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event){
        if(event.entityPlayer == this.player){
            // Is the time since last swinging less than the tolerance level
            if(Math.abs(this.player.worldObj.getTotalWorldTime()-this.playerswinglasttime) <= PMDataTracker.SWING_TOLERANCE){
                // Was it actually an ore?
                if(Helper.isItemOre(event.item.getEntityItem())){
                    // How many things were in the stack?
                    this.greedScore += 1*(event.item.getEntityItem().stackSize);
                }
            }
        }
    }

    // We MUST make sure that we check for this, unless of course we aren't checking for it
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityLivingAttacked(LivingAttackEvent event){
        if(event.source.getSourceOfDamage() == this.player){
            if(Helper.isPlayerInVillage(event.source.getSourceOfDamage())){
                if(event.source.getEntity() instanceof IMob){
                    this.heroScore += (((EntityLiving)event.source.getEntity()).getMaxHealth()/event.ammount);
                }else if(event.source.getEntity() instanceof EntityVillager){
                    this.villainScore += (((EntityLiving)event.source.getEntity()).getMaxHealth()/event.ammount);
                }else if(event.source.getEntity() instanceof EntityPlayer){
                    PMDataTracker targetTracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)event.source.getEntity());
                    this.heroScore += (((EntityLiving)event.source.getEntity()).getMaxHealth()/event.ammount) + Math.abs(1/(targetTracker.getHeroScore() - this.heroScore));
                    this.villainScore += (((EntityLiving)event.source.getEntity()).getMaxHealth()/event.ammount) + Math.abs(1/(targetTracker.getVillainScore() - this.villainScore));
                }
            }
            // NOTE: Maybe add something here to check for the player's home?
            // As in, they are defending their property?
            // Their home could just be considered the chunks that they spend the most time in on average
            this.aggressiveScore += (((EntityLiving)event.source.getEntity()).getMaxHealth()/event.ammount);
        }else if(event.source.getEntity() == this.player){
            // TODO: Add something here to check if the player is running away
            // And we can't just check if the player is getting hit
            // Maybe check if the player is near the monster and hasn't attacked at all?
        }
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
        int pAmt = server.getCurrentPlayerCount(); // Everybody's potential is dependent on the number of people on the server (doesn't really mean anything in single player)
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

    public float getArchitectScore(){
        return architectScore;
    }
    public float getEngineeringScore(){
        return engineeringScore;
    }
    public float getGreedScore(){
        return greedScore;
    }

    public float getWaterScore(){
        return waterScore;
    }
    public float getNatureScore(){
        return natureScore;
    }
    public float getDayScore(){
        return dayScore;
    }
    public float getNightScore(){
        return nightScore;
    }

    public float getHeroScore(){
        return heroScore;
    }
    public float getVillainScore(){
        return villainScore;
    }

    public float getPassiveScore(){
        return passiveScore;
    }

    public float getAggressiveScore(){
        return aggressiveScore;
    }

    public float getPotential(){
        return potential;
    }

    public String getHighestScoreIden(){
        float[] scores = {getArchitectScore(), getEngineeringScore(),getGreedScore(),getWaterScore(),getNatureScore(),
            getDayScore(),getNightScore(),getHeroScore(),getVillainScore(),getPassiveScore(),getAggressiveScore()};
        int idx = Helper.getMaxInArray(scores);
        switch(idx){
            case 3:
                return "WATER";
            case 4:
                return "NATURE";
            case 6:
                return "NIGHT";
            case 8:
                return "HERO";
            case 10:
                return "AGGRESSIVE";
            default:
                return "DEFAULT";
        }
    }

    public float getCorruption(){
        if(playerSoulGem != null)
            return playerSoulGem.getDespair();
        else
            return -1;
    }

    public void setPotential(float val){
        potential = val;
    }

    public void setCorruption(float val){
        if(playerSoulGem != null)
            playerSoulGem.setDespair(val);
    }

    public void setArchitectScore(float val){
        architectScore = val;
    }
    public void setEngineeringScore(float val){
        engineeringScore = val;
    }
    public void setGreedScore(float val){
        greedScore = val;
    }

    public void setWaterScore(float val){
        waterScore = val;
    }
    public void setNatureScore(float val){
        natureScore = val;
    }
    public void setDayScore(float val){
        dayScore = val;
    }
    public void setNightScore(float val){
        nightScore = val;
    }

    public void setHeroScore(float val){
        heroScore = val;
    }
    public void setVillainScore(float val){
        villainScore = val;
    }

    public void setPassiveScore(float val){
        passiveScore = val;
    }

    public void setAggressiveScore(float val){
        aggressiveScore = val;
    }

    public void incrementDataTimer(){
        updatedatatimer++;
    }

    public int getUpdateDataTime(){
        return updatedatatimer;
    }

    public void incrementEffectsTimer(){
        updateeffectstimer++;
    }

    public int getUpdateEffectsTime(){
        return updateeffectstimer;
    }

    public void resetEffectsTimer(){
        updateeffectstimer = 0;
    }

    public String toString(){
        String str = "PMDataTracker\n=============\n";
        str+= "State: " + getPlayerState() + "\n";
        str += "Potential: " + potential + "\n";
        str += "Corruption: " + getCorruption() + "\n";
        str += "Architect: " + getArchitectScore() + "\n";
        str += "Engineering: " + getEngineeringScore() + "\n";
        str += "Greed: " + getGreedScore() + "\n";
        str += "Water: " + getWaterScore() + "\n";
        str += "Nature: " + getNatureScore() + "\n";
        str += "Day: " + getDayScore() + "\n";
        str += "Night: " + getNightScore() + "\n";
        str += "Hero: " + getHeroScore() + "\n";
        str += "Villain: " + getVillainScore() + "\n";
        str += "Passive: " + getPassiveScore() + "\n";
        str += "Aggressive: " + getAggressiveScore() + "\n";
        return str;
    }

    public void setIsTransformingIntoWitch(boolean newValue){
        this.currentlyTransformingIntoWitch = newValue;
    }

    public boolean isTransformingIntoWitch(){
        return this.currentlyTransformingIntoWitch;
    }
}
