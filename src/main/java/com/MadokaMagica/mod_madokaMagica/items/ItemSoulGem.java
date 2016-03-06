package com.MadokaMagica.mod_madokaMagica.items;

import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.MadokaMagicaEventManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;

public class ItemSoulGem extends Item{
    public final static float MAX_DESPAIR = 100.0F;
    public final static float CREATE_WEAPON_DESPAIR = 0.10F;
    public final static float BUFF_PLAYER_DESPAIR = 0.20F;
    public final static float AMBIENT_DESPAIR = 0.01F;

    protected float despair;
    public EntityPlayer player;
    public PMDataTracker playerData;

    public ItemSoulGem(){
        super();
        // setTextureName();
    }

    public ItemSoulGem(EntityPlayer entityPlayer, PMDataTracker pmPlayerData, int id){
        player = entityPlayer;
        playerData = pmPlayerData;
        despair = 0;
    }

    public float getDespair(){
        return despair;
    }

    public void setDespair(float val){
        despair = val;
    }

/*
    public PMWeapon createWeapon(){
        // Do some stuff to create a weapon
        despair += ItemSoulGem.CREATE_WEAPON_DESPAIR;
    }
*/

    public void randomBuffPlayer(){
        // some stuff
        despair += ItemSoulGem.BUFF_PLAYER_DESPAIR;
    }

    protected void boostJump(){

    }

    protected void boostRun(){

    }

    public void cleanse(ItemGriefSeed gs){
        float subtract = ItemSoulGem.MAX_DESPAIR*0.1F; // 10% of MAX_DESPAIR
        if(subtract < despair) subtract = despair;
        gs.addDespair(subtract);
        despair -= subtract;
    }

    public void addDespair(float despair){
        this.despair += despair;
    }

    public boolean canTransformIntoWitch(){
        return despair >= ItemSoulGem.MAX_DESPAIR;
    }

    protected void transformIntoWitch(){
        // MadokaMagicaEventManager.getInstance().startEvent(new MadokaMagicaWitchTransformationEvent(this.playerData));
        MadokaMagicaWitchTransformationEvent.getInstance().activate(this.playerData);
    }

    public void update(){
        float worldTime = player.worldObj.getTotalWorldTime();
        if(worldTime % 23000 == 0)
            despair += AMBIENT_DESPAIR;
    }
}
