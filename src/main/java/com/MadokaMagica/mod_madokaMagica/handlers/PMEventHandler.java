package com.MadokaMagica.mod_madokaMagica.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;

public class PMEventHandler{
    // Once the player has logged in, check if they have a data tracker
    // If they do not, create one
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        //CommandDisplayInformation.getInstance().sendChat(event.player,"onPlayerLoggedIn called!");
        System.out.println("onPlayerLoggedIn called!");
        PMDataTracker oldTracker = PlayerDataTrackerManager.getInstance().getTrackerByUsername(event.player.getDisplayName());
        if(oldTracker != null){
                oldTracker.player = event.player;
                oldTracker.loadTagData();
                return;
        }
        //CommandDisplayInformation.getInstance().sendChat(event.player,"getTrackerByUsername returned null. Calling new PMDataTracker(...);");
        System.out.println("getTrackerByUsername returned null. Calling new PMDataTracker(...);");
        PMDataTracker tracker = new PMDataTracker(event.player);
        PlayerDataTrackerManager.getInstance().addDataTracker(tracker);
        tracker.loadTagData();
    }

    // The player doesn't actually transform into a witch until they die
    // That's why we need to prevent them from truly dying so that they become a witch instead.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public boolean onDeathWithHighestPriority(LivingDeathEvent event){
        Entity entity = event.entity;

        // If it is an EntityPlayer and if that player is currently turning into a witch
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)entity);
        if(entity instanceof EntityPlayer && pmdt != null && MadokaMagicaWitchTransformationEvent.getInstance().isActive(pmdt)){
            ItemSoulGem soulgem = ItemSoulGemManager.getInstance().getSoulGemByPlayer((EntityPlayer)entity);
            // Why the hell would soulgem even be null?
            if(soulgem == null){
                FMLLog.warning("Found null in itemSoulGemManager! This most likely means that the player is somehow turning into a witch without having a soulgem. Please consult a programmer.");
                return false;
            }
            pmdt.setPlayerState(2);
        }
        return true;
    }
}

