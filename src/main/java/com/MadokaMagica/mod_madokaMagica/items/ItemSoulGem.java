package com.MadokaMagica.mod_madokaMagica.items;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.common.MinecraftForge;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.MadokaMagicaEventManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;

public class ItemSoulGem extends Item{
    public final static float MAX_DESPAIR = 100.0F;
    public final static float CREATE_WEAPON_DESPAIR = 10.0F;
    public final static float BUFF_PLAYER_DESPAIR = 20.0F;
    public final static float AMBIENT_DESPAIR = 1.0F;

    protected float despair;
    public EntityPlayer player;
    public PMDataTracker playerData;
    public Random random;

    public ItemSoulGem(){
        super();
        random = new Random();
        // setTextureName();
    }

    public ItemSoulGem(EntityPlayer entityPlayer, PMDataTracker pmPlayerData){
        super();
        player = entityPlayer;
        playerData = pmPlayerData;
        despair = 0;
        random = new Random();
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
        if(subtract > despair) subtract = despair;
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
        // MadokaMagicaWitchTransformationEvent.getInstance().activate(this.playerData);
        MinecraftForge.EVENT_BUS.post(new MadokaMagicaWitchTransformationEvent(this.playerData));
    }

    public void update(){
        float worldTime = player.worldObj.getTotalWorldTime();
        if(worldTime % 23000 == 0)
            despair += AMBIENT_DESPAIR;

        // TODO: Fix this random number thing
        if(this.canTransformIntoWitch() && (this.random.nextInt(100) == despair))
            this.transformIntoWitch();
    }
}
