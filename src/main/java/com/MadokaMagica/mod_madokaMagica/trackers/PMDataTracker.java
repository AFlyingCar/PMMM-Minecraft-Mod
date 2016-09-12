package com.MadokaMagica.mod_madokaMagica.trackers;

import java.util.UUID;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.HashMap;

import net.minecraft.util.DamageSource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

// NOTE: These classes may quickly fill up save-files if there are lots of witches
//       We should come up with a way to clean up these objects if they start to get out of control

public class PMDataTracker {
    public static final int MAX_POTENTIAL = 100;
    public static final int SWING_TOLERANCE = 3; // 3 seconds
    public static final double RUNNING_AWAY_RADIUS = 50.0D; // 50 block radius
    public static final double ANGLE_TO_RANGE_END_DEG = 15.0D; // In degrees
    public static final double ANGLE_TO_RANGE_END_RAD = 0.26D; // In radians

    public Entity entity; // The entity being tracked
    private ItemStack playerSoulGem = null;

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
     * 0 - Normal human
     * 1 - Puella Magi
     * 2 - Witch
     * 3 - Minion
     * -1 - Unknown
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

    private boolean dirty;

    // Some simple
    private static int currID = 0;
    private int ID; // Was going to make this final, but java is fucking stupid

    public NBTTagCompound tagData;

    private String failureMsg;

    public boolean hasLoadedData;

    public PMDataTracker(EntityPlayer nplayer){
        entity = nplayer;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();

        potential = calculatePotential();
        playerswinglasttime = entity.worldObj.getTotalWorldTime();
        player_state = 0;

        ready = true;
        this.dirty = true;
        ID=currID++;

        failureMsg = "";
        hasLoadedData = false;
    }

    public PMDataTracker(EntityPlayer nplayer, ItemStack nplayerSG){
        this(nplayer);
        playerSoulGem = nplayerSG;
        this.dirty = true;

        ID=currID++;

        failureMsg = "";
        hasLoadedData = false;
    }

    public PMDataTracker(EntityPMWitchMinion minion){
        // COPY-PASTED CODE!
        // This is done because we can't just call the other constructor with a 'null' argument, as this throws an 'ambiguous argument' error.
        // I think it's because, since null doesn't have a type, the compiler doesn't know which constructor to call. Oh well, I fixed it.
        entity = minion;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();

        potential = 0;//calculatePotential();
        playerswinglasttime = entity.worldObj.getTotalWorldTime();
        player_state = 3;

        ready = true;
        this.dirty = true;

        ID=currID++;
        failureMsg = "";
        hasLoadedData = false;
    }

    public PMDataTracker(EntityPMWitch witch){
        entity = witch;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();

        potential = calculatePotential();
        playerswinglasttime = entity.worldObj.getTotalWorldTime();
        player_state = 2;

        ready = true;
        this.dirty = true;

        ID=currID++;
        failureMsg = "";
        hasLoadedData = false;
    }

    public PMDataTracker(){
        entity = null;
        like_entity_type = new HashMap<Integer,Float>();
        liked_entities = new HashMap<Integer,Float>();
        like_level = new HashMap<Integer,Float>();
        nearbyEntitiesMap = new HashMap<Entity,Float>();
        playerswinglasttime = 0;
        this.ready = false;

        ID=currID++;
        failureMsg = "";
        hasLoadedData = false;
    }

    public void setEntity(Entity entity){
        this.entity = entity;
        playerswinglasttime = entity.worldObj.getTotalWorldTime();
        loadTagData();
        if(entity instanceof EntityPlayer){
            player_state = 0;
            potential = calculatePotential();
        }else if(entity instanceof EntityPMWitch){
            player_state = 2;
        }else if(entity instanceof EntityPMWitchMinion){
            player_state = 3;
        }else{
            player_state = -1;
            failureMsg += "Unknown Entity type.";
        }
        ready = true;
    }

    public boolean isReady(){
        return ready;
    }

    // Returns true if successfully loaded, false otherwise
    public boolean loadTagData(){
        // Return successfully if we have already loaded the data (don't want to load twice by accident, and we don't want to count it as a failure)
        if(hasLoadedData) return true;

        //NBTTagCompound tags = entity.getEntityData();
        NBTTagCompound tags = this.tagData; // Do this for backwards-compatibility

        String failure = "Failed to load PMDataTracker tags! Save data is missing ";

        // We shouldn't need to do this anymore, since the entity is actually set later
        /*
        if(tags.hasKey("ENTITY_UUID_MOST_SIG") && tags.hasKey("ENTITY_UUID_LEAST_SIG")){
            UUID uuid = new UUID(tags.getLong("ENTITY_UUID_MOST_SIG"),
                                 tags.getLong("ENTITY_UUID_LEAST_SIG")
                                );
            entity = Helper.getEntityFromUUID(uuid);
        }else{
            System.out.println(failure + "ENTITY_UUID_MOST_SIG or ENITTY_UUID_LEAST_SIG!");
            return;
        }
        */

        // Get the state first
        if(tags.hasKey("PM_PLAYER_STATE")){
            player_state = tags.getInteger("PM_PLAYER_STATE");
        }else{
            failureMsg += (failure + "PM_PLAYER_STATE!");
            return false;
        }

        // Get the player's potential
        if(tags.hasKey("PM_POTENTIAL")){
            potential = tags.getFloat("PM_POTENTIAL");
        }else if(player_state > 1){
            // Only fail if they aren't a player or puella magi (those are the only states were potential is needed or allowed)
            failureMsg += (failure + "PM_POTENTIAL!");
            return false;
        }

        // Get Hero/Villain scores
        if(tags.hasKey("PM_HERO_SCORE")){
            heroScore = tags.getFloat("PM_HERO_SCORE");
        }else{
            failureMsg += (failure + "PM_HERO_SCORE!");
            return false;
        }
        if(tags.hasKey("PM_VILLAIN_SCORE")){
            villainScore = tags.getFloat("PM_VILLAIN_SCORE");
        }else{
            failureMsg += (failure + "PM_VILLAIN_SCORE!");
            return false;
        }

        // Get Aggressive/Passive score
        if(tags.hasKey("PM_AGGRESSIVE_SCORE")){
            aggressiveScore = tags.getFloat("PM_AGGRESSIVE_SCORE");
        }else{
            failureMsg += (failure + "PM_AGGRESSIVE_SCORE!");
            return false;
        }
        if(tags.hasKey("PM_PASSIVE_SCORE")){
            passiveScore = tags.getFloat("PM_PASSIVE_SCORE");
        }else{
            failureMsg += (failure + "PM_PASSIVE_SCORE!");
            return false;
        }

        // Enviroment-based scores
        if(tags.hasKey("PM_NATURE_SCORE")){
            natureScore = tags.getFloat("PM_NATURE_SCORE");
        }else{
            failureMsg += (failure + "PM_NATURE_SCORE!");
            return false;
        }
        if(tags.hasKey("PM_DAY_SCORE")){
            dayScore = tags.getFloat("PM_DAY_SCORE");
        }else{
            failureMsg += (failure + "PM_DAY_SCORE!");
            return false;
        }
        if(tags.hasKey("PM_NIGHT_SCORE")){
            dayScore = tags.getFloat("PM_NIGHT_SCORE");
        }else{
            failureMsg += (failure + "PM_NIGHT_SCORE!");
            return false;
        }

        // engineering-type score
        if(tags.hasKey("PM_ENGINEERING_SCORE")){
            engineeringScore = tags.getFloat("PM_ENGINEERING_SCORE");
        }else{
            failureMsg += (failure + "PM_ENGINEERING_SCORE!");
            return false;
        }

        if(tags.hasKey("PM_ARCHITECT_SCORE")){
            architectScore = tags.getFloat("PM_ARCHITECT_SCORE");
        }else{
            failureMsg += (failure + "PM_ARCHITECT_SCORE!");
            return false;
        }

        if(tags.hasKey("PM_GREED_SCORE")){
            greedScore = tags.getFloat("PM_GREED_SCORE");
        }else{
            failureMsg += (failure + "PM_GREED_SCORE!");
            return false;
        }

        if(tags.hasKey("PM_LIKES_LEVEL")){
            int[] level_data = tags.getIntArray("PM_LIKES_LEVEL");
            float like_amt = Helper.unpackFloat(level_data[1]);
            like_level.put(new Integer(level_data[0]),new Float(like_amt));
        }else{
            failureMsg += (failure + "PM_LIKES_LEVEL!");
            return false;
        }

        // TODO: Rewrite this so that it actually fucking works (also re-enable it
        if(tags.hasKey("PM_LIKES_ENTITY") && false){
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
        }else{
            failureMsg += (failure + "PM_LIKES_ENTITY!");
            return false;
        }

        // TODO: Rewrite this so that it actually fucking works
        if(tags.hasKey("PM_LIKES_ENTITY_TYPE") && false){
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
        }else{
            failureMsg += (failure + "PM_LIKES_ENTITY_TYPE!");
            return false;
        }

        // Do it down here, because we don't want to say we've loaded the data if we failed to do so
        // Don't want to lie now do we? That would be rude.
        hasLoadedData = true;

        return true;
    }

    public void writeTags(NBTTagCompound tags){
        tags.setFloat("PM_POTENTIAL",        getPotential()         );
        tags.setFloat("PM_HERO_SCORE",       getHeroScore()         );
        tags.setFloat("PM_VILLAIN_SCORE",    getVillainScore()      );
        tags.setFloat("PM_AGGRESSIVE_SCORE", getAggressiveScore()   );
        tags.setFloat("PM_PASSIVE_SCORE",    getPassiveScore()      );
        tags.setFloat("PM_NATURE_SCORE",     getNatureScore()       );
        tags.setFloat("PM_DAY_SCORE",        getDayScore()          );
        tags.setFloat("PM_NIGHT_SCORE",      getNightScore()        );
        tags.setFloat("PM_ENGINEERING_SCORE",getEngineeringScore()  );
        tags.setFloat("PM_ARCHITECT_SCORE",  getArchitectScore()    );
        tags.setFloat("PM_GREED_SCORE",      getGreedScore()        );
        tags.setInteger("PM_PLAYER_STATE",   getPlayerState()       );
        
        tags.setTag("PM_LIKES_ENTITY",new NBTTagCompound());
        tags.setTag("PM_LIKES_ENTITY_TYPE",new NBTTagCompound());
        tags.setTag("PM_LIKES_LEVEL",new NBTTagCompound());

        /*
        // Save the Entity's UUID number
        tags.setLong("ENTITY_UUID_MOST_SIG",  entity.getUniqueID().getMostSignificantBits()  );
        tags.setLong("ENTITY_UUID_LEAST_SIG", entity.getUniqueID().getLeastSignificantBits() );
        */
        this.dirty = false; // We are no longer dirty if we have finished writing our data
    }

    public void updateData(){
        // We don't track anything anymore if the player has turned into a witch
        if(isWitch() || isMinion())
            return;

        EntityPlayer player = (EntityPlayer)entity; // Do this so we can refer to the entity as a player, since this method is only for players

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
        


        this.playerswinging = player.isSwingInProgress;
        if(this.playerswinging)
            this.playerswinglasttime = player.worldObj.getTotalWorldTime();

        // DOES THE PLAYER LIKE MINING OR FARMING
        // ======================================
        if(Helper.isEntityUnderground(player)){
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
            if(Helper.isEntityOutside(player)){
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

        this.dirty = true;
    }

    // NOTE: Maybe do something here if Entity e is in liked_entities?
    private void cleanNearbyEntitiesMap(){
        float ctime = entity.worldObj.getTotalWorldTime();
        for(Entry<Entity,Float> e : nearbyEntitiesMap.entrySet())
            // If the player has not been near Entity e for longer than twice the tolerance length...
            if(nearbyEntitiesMap.get(e.getKey()).floatValue() >= (Helper.timeTolerance*2))
                nearbyEntitiesMap.remove(e.getKey());
    }

    private float calculatePotential(){
        // TODO: This method returns some very high numbers. Need to figure out a way to decrease this
        MinecraftServer server = MinecraftServer.getServer();
        int pAmt = server.getCurrentPlayerCount(); // Everybody's potential is dependent on the number of people on the server (doesn't really mean anything in single player)
        float worldAge = server.getEntityWorld().getTotalWorldTime();

        int pexp = 0;
        if(player_state == 1 || player_state == 0)
            pexp = ((EntityPlayer)entity).experienceLevel; // Got to check if the entity is a player first, and not a witch or minion

        int dimModifier = getDimensionModifier();
        float poten = ((MAX_POTENTIAL/pAmt) - (worldAge%(MAX_POTENTIAL/10))) + pexp + dimModifier;

        return (poten > MAX_POTENTIAL) ? MAX_POTENTIAL : poten; // Ensure that the potential doesn't exceed the maximum
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
        this.dirty = true;
    }

    public boolean isMinion(){
        return player_state == 3;
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

    public Entity getEntity(){
        return this.entity;
    }

    public ItemStack getSoulGem(){
        return playerSoulGem;
    }

    @Deprecated
    public EntityPlayer getPlayer(){
        System.out.println("WARNING! Attempted to call deprecated method getPlayer()! Please use getEntity() instead.");
        return null;
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
        // TODO: Make the math that determines this a bit more sophisticated than what is the highest
        int idx = Helper.getMaxInArray(scores);
        switch(idx){
            case 0:
                return "ARCHITECT";
            case 1:
                return "ENGINEERING";
            case 2:
                return "GREED";
            case 3:
                return "WATER";
            case 4:
                return "NATURE";
            case 5:
                return "DAY";
            case 6:
                return "NIGHT";
            case 7:
                return "VILLAIN";
            case 8:
                return "HERO";
            case 9:
                return "PASSIVE";
            case 10:
                return "AGGRESSIVE";
            default:
                return "DEFAULT";
        }
    }

    public String getSecondHighestScoreIden(){
        float[] scores = {getArchitectScore(), getEngineeringScore(),getGreedScore(),getWaterScore(),getNatureScore(),
            getDayScore(),getNightScore(),getHeroScore(),getVillainScore(),getPassiveScore(),getAggressiveScore()};

        int idx = Helper.getSecondMaxInArray(scores);
        switch(idx){
            case 0:
                return "ARCHITECT";
            case 1:
                return "ENGINEERING";
            case 2:
                return "GREED";
            case 3:
                return "WATER";
            case 4:
                return "NATURE";
            case 5:
                return "DAY";
            case 6:
                return "NIGHT";
            case 7:
                return "VILLAIN";
            case 8:
                return "HERO";
            case 9:
                return "PASSIVE";
            case 10:
                return "AGGRESSIVE";
            default:
                return "DEFAULT";
        }
    }

    public String[] getScoreIdensFromLowestToHighest(){
        float[] scores = {getArchitectScore(), getEngineeringScore(),getGreedScore(),getWaterScore(),getNatureScore(),
            getDayScore(),getNightScore(),getHeroScore(),getVillainScore(),getPassiveScore(),getAggressiveScore()};
        String[] idens = new String[scores.length];

        for(int i=0; i < scores.length; i++){

        }
        // TODO: FInish this method
        return idens;
    }

    public float getCorruption(){
        if(playerSoulGem != null && playerSoulGem.getTagCompound() != null)
            return playerSoulGem.getTagCompound().getFloat("SG_DESPAIR");
        else
            return -1.0F;
    }

    public void setPotential(float val){
        potential = val;
        this.dirty = true;
    }

    public void setCorruption(float val){
        if(playerSoulGem != null && playerSoulGem.getTagCompound() != null){
            playerSoulGem.getTagCompound().setFloat("SG_DESPAIR",val);
            this.dirty = true;
        }
    }

    public void setArchitectScore(float val){
        architectScore = val;
        this.dirty = true;
    }
    public void setEngineeringScore(float val){
        engineeringScore = val;
        this.dirty = true;
    }
    public void setGreedScore(float val){
        greedScore = val;
        this.dirty = true;
    }

    public void setWaterScore(float val){
        waterScore = val;
        this.dirty = true;
    }
    public void setNatureScore(float val){
        natureScore = val;
        this.dirty = true;
    }
    public void setDayScore(float val){
        dayScore = val;
        this.dirty = true;
    }
    public void setNightScore(float val){
        nightScore = val;
        this.dirty = true;
    }

    public void setHeroScore(float val){
        heroScore = val;
        this.dirty = true;
    }
    public void setVillainScore(float val){
        villainScore = val;
        this.dirty = true;
    }

    public void setPassiveScore(float val){
        passiveScore = val;
        this.dirty = true;
    }

    public void setAggressiveScore(float val){
        aggressiveScore = val;
        this.dirty = true;
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

    public long getPlayerSwingLastTime(){ 
        return playerswinglasttime;
    }

    public boolean getPlayerSwinging(){
        return playerswinging;
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
        this.dirty = true;
    }

    public boolean isTransformingIntoWitch(){
        return this.currentlyTransformingIntoWitch;
    }

    public void randomize(){
        this.architectScore = ((float)Math.random())*50F;
        this.engineeringScore = ((float)Math.random())*50F;
        this.greedScore = ((float)Math.random())*50F;
        this.waterScore = ((float)Math.random())*50F;
        this.natureScore = ((float)Math.random())*50F;
        this.dayScore = ((float)Math.random())*50F;
        this.nightScore = ((float)Math.random())*50F;
        this.heroScore = ((float)Math.random())*50F;
        this.villainScore = ((float)Math.random())*50F;
        this.passiveScore = ((float)Math.random())*50F;
        this.aggressiveScore = ((float)Math.random())*50F;
    }

    public String getIdentifierName(){
        if(player_state <= 1){
            return ((EntityPlayer)entity).getDisplayName();
        }else{
            return entity.getPersistentID().toString();
        }
    }

    public String getEntityName(){
        if(player_state <= 1){
            return ((EntityPlayer)entity).getDisplayName();
        }else{
            return ((EntityLiving)entity).getCustomNameTag();
        }
    }

    public UUID getEntityUUID(){
        return entity.getPersistentID();
    }

    public boolean isDirty(){
        return dirty;
    }

    public void markDirty(){
        this.dirty = true;
    }

    public String getFailureMsg(){
        String msg = this.failureMsg;
        this.failureMsg = "";
        return msg;
    }

    protected boolean isRunningAwayFromSingularEnemyReal(EntityMob enemy){
        // Player's current position (corner3)
        double posX = this.entity.posX;
        double posY = this.entity.posY;
        double posZ = this.entity.posZ;
        // Player's last position
        double lastPosX = this.entity.lastTickPosX;
        double lastPosY = this.entity.lastTickPosY;
        double lastPosZ = this.entity.lastTickPosZ;

        // Enemy's real position
        double ePosX = enemy.posX;
        double ePosY = enemy.posY;
        double ePosZ = enemy.posZ;

        // It is a perfect sphere, so we only need to take the radius in one dimension, no need to factor in Z position
        double radius = Math.sqrt((ePosX*ePosX)+(ePosY*ePosY)); // TODO: FINISH THIS SHIT

        // TODO: Does arccos use radians or degrees?
        double eAngleFrom00 = Math.acos((ePosX-posX)/radius);
        double leftAngle = eAngleFrom00 + 30.0D; // Degrees
        double rightAngle = eAngleFrom00 - 30.0D;

        double xzl = radius * Math.cos(leftAngle);
        double xzr = radius * Math.cos(rightAngle);

        double yl = radius * Math.sin(leftAngle);
        double yr = radius * Math.sin(rightAngle);

        // Get enemy's position to the left and right by 30 degrees (create a triangle, where the enemy is in the center of the hypotenuse)

        // Corner1
        double eLeftX = posX + xzl;
        double eLeftZ = posZ + xzl;
        double eLeftY = posY + yl;

        // Corner2
        double eRightX = posX + xzr;
        double eRightZ = posZ + xzr;
        double eRightY = posY + yr;


        // Player's velocity in each one of the 3 velocities
        double vX = (lastPosX-posX);
        double vY = (lastPosY-posY);
        double vZ = (lastPosZ-posZ);

        // Player's position 1000 ticks from now
        double testLocX = (1000*vX)+posX;
        double testLocY = (1000*vY)+posY;
        double testLocZ = (1000*vZ)+posZ;


        // Do same calc twice with XZ and YZ
        boolean xzInTri = Helper.isInTriangle(testLocX,testLocZ,
                                              posX,posZ,
                                              eLeftX,eLeftZ,
                                              eRightX,eLeftZ);
        boolean yzInTri = Helper.isInTriangle(testLocY,testLocZ,
                                              posY,posZ,
                                              eLeftY,eLeftZ,
                                              eRightY,eLeftZ);
        return !(xzInTri && yzInTri);
    }

    protected boolean isRunningAwayFromSingularEnemy(EntityMob enemy){
        // Player's current position
        double posX = this.entity.posX;
        double posY = this.entity.posY;
        double posZ = this.entity.posZ;
        // Player's last position
        double lastPosX = this.entity.lastTickPosX;
        double lastPosY = this.entity.lastTickPosY;
        double lastPosZ = this.entity.lastTickPosZ;

        // Enemy's real position
        double ePosX = enemy.posX;
        double ePosY = enemy.posY;
        double ePosZ = enemy.posZ;

        // It is a perfect sphere, so we only need to take the radius in one dimension, no need to factor in Z position
        double circRadiusXY = Math.sqrt((ePosX*ePosX)+(ePosY*ePosY)); // TODO: FINISH THIS SHIT

        // Find 2 positions from a certain degrees around the enemy
        // This will give us our other two points for the triangle

        // Position x degrees to the left of Enemy
        double leftDegEPosX = circRadiusXY*Math.sin(ANGLE_TO_RANGE_END_RAD) + ePosX;
        double leftDegEPosY = circRadiusXY*Math.cos(ANGLE_TO_RANGE_END_RAD) + ePosY;
        double leftDegEPosZ = circRadiusXY*Math.sin(ANGLE_TO_RANGE_END_RAD) + ePosZ; // NOTE: Does this work for Z coordinate?
        // Position x degrees to the right of Enemy
        double rightDegEPosX = circRadiusXY*Math.sin(-ANGLE_TO_RANGE_END_RAD) + ePosX;
        double rightDegEPosY = circRadiusXY*Math.cos(-ANGLE_TO_RANGE_END_RAD) + ePosY;
        double rightDegEPosZ = circRadiusXY*Math.sin(-ANGLE_TO_RANGE_END_RAD) + ePosZ; // NOTE: Does this work for Z coordinate?

        // Player's velocity in each one of the 3 velocities
        double vX = (lastPosX-posX);
        double vY = (lastPosY-posY);
        double vZ = (lastPosZ-posZ);

        // Player's position 1000 ticks from now
        double px2 = (1000*vX)+posX;
        double py2 = (1000*vY)+posY;
        double pz2 = (1000*vZ)+posZ;

        double[] playerFuturePointXY = {px2,py2};
        double[] playerFuturePointZY = {pz2,py2};

        double[] playerPosXYAsArray = {posX,posY};
        double[] enemyShiftLeftPosXYAsArray = {leftDegEPosX,leftDegEPosY};
        double[] enemyShiftRightPosXYAsArray = {rightDegEPosX,rightDegEPosY};

        double[] playerPosZYAsArray = {posZ,posY};
        double[] enemyShiftLeftPosZYAsArray = {leftDegEPosZ,leftDegEPosY};
        double[] enemyShiftRightPosZYAsArray = {rightDegEPosZ,rightDegEPosY};

        // TODO: Add other 
        boolean dim1 = Helper.calcBarycentricCoords(playerPosXYAsArray,enemyShiftLeftPosXYAsArray,enemyShiftRightPosXYAsArray,playerFuturePointXY);
        boolean dim2 = Helper.calcBarycentricCoords(playerPosZYAsArray,enemyShiftLeftPosZYAsArray,enemyShiftRightPosZYAsArray,playerFuturePointZY);

        // The final calculations
        // According to StackOverflow, we need to use barycentric coordinates
        // This will allow us to determine if a single point (our position 1000 ticks from now) is still within the range to be considered 'running towards' the enemy
        return dim1 && dim2;
    }

    protected boolean isRunningAway(){
        int minX = (int)(this.entity.posX-RUNNING_AWAY_RADIUS);
        int minY = (int)(this.entity.posY-RUNNING_AWAY_RADIUS);
        int minZ = (int)(this.entity.posZ-RUNNING_AWAY_RADIUS);
        int maxX = (int)(this.entity.posX+RUNNING_AWAY_RADIUS);
        int maxY = (int)(this.entity.posY+RUNNING_AWAY_RADIUS);
        int maxZ = (int)(this.entity.posZ+RUNNING_AWAY_RADIUS);

        AxisAlignedBB checkRadius = AxisAlignedBB.getBoundingBox(minX,minY,minZ,maxX,maxY,maxZ);

        List all_entities = this.entity.worldObj.getEntitiesWithinAABB(EntityMob.class,checkRadius);
        for(Object obj : all_entities){
            EntityMob e = (EntityMob)obj;
            if(!isRunningAwayFromSingularEnemyReal(e))
                return false;
        }
        return true;
    }
}

