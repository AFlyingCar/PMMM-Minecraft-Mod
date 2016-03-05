package com.MadokaMagica.mod_madokaMagica;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.MadokaMagica.mod_madokaMagica.commands.CommandStartWitchTransformation;
import com.MadokaMagica.mod_madokaMagica.commands.CommandDisplayInformation;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaEvent;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaPuellaMagiTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.MadokaMagicaEventManager;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.handlers.PMEventHandler;

@Mod(modid=MadokaMagicaMod.MODID, version=MadokaMagicaMod.VERSION)
public class MadokaMagicaMod {
    public static final String MODID   = "MadokaMagicaMod";
    public static final String VERSION = "0.01a";

    @Instance
    public static MadokaMagicaMod instance;

    private static PlayerDataTrackerManager playerDataTrackerManager;
    private static ItemSoulGemManager itemSoulGemManager;
    private static MadokaMagicaEventManager madokaMagicaEventManager;
    // private static ListenerList madokaMagicaEventListener;

    private static ISound wtransform_music = null;


    public static Item itemSoulGem;
    public static Item itemGriefSeed;

    @EventHandler
    public void onInitialization(FMLInitializationEvent event){
        wtransform_music = PositionedSoundRecord.func_147673_a(new ResourceLocation(MadokaMagicaMod.MODID + ":transformmusic"));
        itemSoulGem = (new ItemSoulGem()).setUnlocalizedName("itemSoulGem");
        itemGriefSeed = (new ItemGriefSeed()).setUnlocalizedName("itemGriefSeed");

        playerDataTrackerManager = PlayerDataTrackerManager.getInstance();
        madokaMagicaEventManager = MadokaMagicaEventManager.getInstance();
        itemSoulGemManager = ItemSoulGemManager.getInstance();

        GameRegistry.registerItem(itemSoulGem,"Soul Gem");
        GameRegistry.registerItem(itemGriefSeed,"Grief Seed");

        FMLCommonHandler.instance().bus().register(new PMEventHandler());

        // GameRegistry.addShapelessRecipe(new ItemStack(itemSoulGem,1,0),new ItemStack(itemSoulGem,1,0),new ItemStack(itemGriefSeed,1,0));

        /*
        EntityRegistry.registerModEntity(MobPMWitch.class, "PMWitch", properties.PMWitchEntityID,this,70,1,false);
        EntityRegistry.registerModEntity(MobPMLabrynthEntrance.class,"PMLabrynthEntrance",properties.PMLabrynthEntranceEntityID,this,70,1,true);
        EntityRegistry.registerModEntity(MobPMMinion.class,"PMMinion",properties.PMMinionEntityID,this,70,1,true);
        */

        madokaMagicaEventManager.register((MadokaMagicaEvent)MadokaMagicaWitchTransformationEvent.getInstance());
        madokaMagicaEventManager.register((MadokaMagicaEvent)MadokaMagicaPuellaMagiTransformationEvent.getInstance());
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        // Load each player data thing and add it to playerDataTrackerManager
        event.registerServerCommand( CommandStartWitchTransformation.getInstance() );
        event.registerServerCommand( CommandDisplayInformation.getInstance() );
    }

    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event){
        playerDataTrackerManager.manage();
        itemSoulGemManager.manage();
        madokaMagicaEventManager.manage();

        for(MadokaMagicaEvent e : madokaMagicaEventManager.getActiveEvents()){
            if(e instanceof MadokaMagicaWitchTransformationEvent)
                this.onPlayerWitchTransformation((MadokaMagicaWitchTransformationEvent)e);
            else if(e instanceof MadokaMagicaPuellaMagiTransformationEvent)
                this.onPlayerPuellaMagiTransformation((MadokaMagicaPuellaMagiTransformationEvent)e);
        }
    }

    public void onPlayerWitchTransformation(MadokaMagicaWitchTransformationEvent event){
        ArrayList<PMDataTracker> trackers = event.getTrackers();
        for(PMDataTracker tracker : trackers){
            EntityPlayer player = tracker.getPlayer();
            float size = 1.0f;

            // If the player is done transforming
            if(tracker.getPlayerState() == 2){
                event.cancel(tracker);
                size = 2.0f;
            }
            // NOTE: Take a look at the explosion size. I'm not sure how to set that value, so let's just ignore it for now.
            Explosion exp = player.worldObj.newExplosion(player,player.posX,player.posY,player.posZ,size,false,false);
        }
    }

    public void onPlayerPuellaMagiTransformation(MadokaMagicaPuellaMagiTransformationEvent event){
        // stuff
    }

    // Jesus Christ this method looks like shit.
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSoundEffectResult(PlaySoundEvent17 event){
        ResourceLocation playingSound = event.sound.getPositionedSoundLocation();
        if(playingSound != null && playingSound.getResourceDomain().equals("minecraft") &&
           (playingSound.getResourcePath().equals("music.game") || 
            playingSound.getResourcePath().equals("music.game.creative")
           )
          )
        {
            if(false){//madokaMagicaEventManager.isEventActive(MadokaMagicaWitchTransformationEvent)){
                // ResourceLocation sound = new ResourceLocation(mod_madokaMagica.modid + ":transformmusic");
                if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(wtransform_music))
                    event.result = wtransform_music;
                else
                    event.setResult(Event.Result.DENY);
            }
        }
    }
}

