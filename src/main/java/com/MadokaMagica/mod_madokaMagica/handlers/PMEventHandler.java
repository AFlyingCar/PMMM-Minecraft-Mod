package com.MadokaMagica.mod_madokaMagica.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.ServerChatEvent;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.IncubatorManager;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;

public class PMEventHandler{
    // Once the player has logged in, check if they have a data tracker
    // If they do not, create one
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        PMDataTracker oldTracker = PlayerDataTrackerManager.getInstance().getTrackerByUsername(event.player.getDisplayName());
        if(oldTracker != null){
                oldTracker.player = event.player;
                oldTracker.loadTagData();
                return;
        }
        System.out.println("getTrackerByUsername returned null. Calling new PMDataTracker(...);");
        PMDataTracker tracker = new PMDataTracker(event.player);
        PlayerDataTrackerManager.getInstance().addDataTracker(tracker);
        tracker.loadTagData();
    }

    @SubscribeEvent
    public boolean onPlayerWitchTransformation(MadokaMagicaWitchTransformationEvent event){
        PMDataTracker tracker = event.tracker;
        float size = 1.0F;

        // This should never happen
        if(tracker == null){
            System.out.println("Found null tracker. Cancelling");
            event.setCanceled(true);
            return false;
        }

        if(tracker.getPlayerState() == 2){
            event.setCanceled(true);
            size = 2.0F;
        }

        EntityPlayer player = tracker.getPlayer();

        // NOTE: Take a look at the explosion size. I'm not sure how to set that value, so let's just ignore it for now.
        Explosion exp = player.worldObj.newExplosion(player,player.posX,player.posY,player.posZ,size,false,false);

        // TODO: Do something to spawn the explosion, I can't test this method right now, so I don't know how to spawn the newly created explosion

        return true;
    }

    // The player doesn't actually transform into a witch until they die
    // That's why we need to prevent them from truly dying so that they become a witch instead.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public boolean onDeathWithHighestPriority(LivingDeathEvent event){
        Entity entity = event.entity;

        // If it is an EntityPlayer and if that player is currently turning into a witch
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)entity);
        if(entity instanceof EntityPlayer && pmdt != null && pmdt.isTransformingIntoWitch()){
            ItemSoulGem soulgem = ItemSoulGemManager.getInstance().getSoulGemByPlayer((EntityPlayer)entity);
            // Why the hell would soulgem even be null?
            if(soulgem == null){
                FMLLog.warning("Found null in itemSoulGemManager! This most likely means that the player is somehow turning into a witch without having a soulgem. Please consult a programmer.");
                return false;
            }
            pmdt.setPlayerState(2);
            // TODO: Do something here to make soulgem become an ItemGriefSeed
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public boolean onServerChat(ServerChatEvent event){
        if(IncubatorManager.getInstance().isPlayerNearIncubator(event.player)){
            return IncubatorManager.getInstance().getNearestIncubator(event.player.posX,event.player.posY,event.player.posZ).processChat(event.player,event.message);
        }
        return true;
    }
}

