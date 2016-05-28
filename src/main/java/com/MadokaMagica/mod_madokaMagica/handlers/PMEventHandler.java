package com.MadokaMagica.mod_madokaMagica.handlers;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.world.Explosion;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.effects.PMEffects;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.IncubatorManager;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaCreateWitchEvent;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaPuellaMagiTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.factories.EntityPMWitchFactory;
import com.MadokaMagica.mod_madokaMagica.factories.EntityPMWitchLabrynthEntranceFactory;

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
    public void onWorldSave(WorldEvent.Save event){
        if(PlayerDataTrackerManager.getInstance() != null)
            PlayerDataTrackerManager.getInstance().saveAllTrackers();
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

    @SubscribeEvent
    public boolean onPlayerPuellaMagiTransformation(MadokaMagicaPuellaMagiTransformationEvent event){
        // TODO: Finish this method.
        return false;
    }

    // The player doesn't actually transform into a witch until they die
    // That's why we need to prevent them from truly dying so that they become a witch instead.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public boolean onDeathWithHighestPriority(LivingDeathEvent event){
        Entity entity = event.entity;

        // If it is an EntityPlayer and if that player is currently turning into a witch
        if(entity instanceof EntityPlayer){
            PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer((EntityPlayer)entity);
            if(pmdt != null && pmdt.isTransformingIntoWitch()){
                ItemSoulGem soulgem = ItemSoulGemManager.getInstance().getSoulGemByPlayer((EntityPlayer)entity);
                // Why the hell would soulgem even be null?
                if(soulgem == null){
                    FMLLog.warning("Found null in itemSoulGemManager! This most likely means that the player is somehow turning into a witch without having a soulgem. Please consult a programmer.");
                    return false;
                }
                pmdt.setPlayerState(2);
                
                // TODO: Do something here to make soulgem become an ItemGriefSeed

                MinecraftForge.EVENT_BUS.post(new MadokaMagicaCreateWitchEvent(pmdt));
            }
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

    @SubscribeEvent
    public boolean onRenderWorld(RenderWorldEvent.Post event){
        for(Entry<String,PMDataTracker> trackerset : PlayerDataTrackerManager.getInstance().getTrackers().entrySet()){
            //if(trackerset.getValue() >= )
            // NOTE: We should test this, because maybe we want to only update these every x render events?
            //      The problem with this though is that that could make the effects flicker due to not updating often enough, which we don't want
            //      Maybe we could just update the next event every x render events, and apply them every event?
            //      I would have to rework a lot of stuff though, so I'm not sure
            // Disabled for now since it prints that annoying as hell error-message
            // PMEffects.applyPlayerEffects(trackerset.getValue());

            // Leave the timer stuff here, in case we decide to use it.
            // Also, if we do use a value here, we should make sure to deccrement it by a lot, since 10 is way to high
            // I'm just leaving it as 10 for now
            if(trackerset.getValue().getUpdateEffectsTime() >= 10)
                trackerset.getValue().incrementEffectsTimer();
            else
                trackerset.getValue().resetEffectsTimer();
        }

        return true;
    }

    // This is the method where we will spawn a witch based on a certain player.
    // We put this triggered by a seperate event so that we can call it elsewhere if we ever decide to
    // That and it just looks so much nicer by itself.
    @SubscribeEvent
    public boolean onCreateWitch(MadokaMagicaCreateWitchEvent event){
        PMDataTracker tracker = event.playerTracker;

        EntityPMWitchFactory pmwf = new EntityPMWitchFactory();
        EntityPMWitch witch = pmwf.createWitch(tracker);

        EntityPMWitchLabrynthEntranceFactory pmwlef = new EntityPMWitchLabrynthEntranceFactory();
        // It spawns in whatever world the player transformed in
        EntityPMWitchLabrynthEntrance entrance = pmwlef.createWitchLabrynthEntrance(witch,tracker.player.worldObj);
        if(entrance == null){
            System.out.println("An error occurred in onCreateWitch(MadokaMagicaCreateWitchEvent)! EntityPMWitchLabrynthEntranceFactory.createWitchLabrynthEntrance(...) returned null!");
            return false;
        }

        // TODO: Somehow generate the world inside the labrynth and set it to the entrance BEFORE we spawn everything

        tracker.player.worldObj.spawnEntityInWorld(entrance);
        entrance.linkedWorldObj.spawnEntityInWorld(witch);
        return true;
    }
}

