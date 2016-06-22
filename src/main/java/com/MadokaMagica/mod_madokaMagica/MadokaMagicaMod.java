package com.MadokaMagica.mod_madokaMagica;

import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaItems;

import com.MadokaMagica.mod_madokaMagica.commands.CommandStartWitchTransformation;
import com.MadokaMagica.mod_madokaMagica.commands.CommandStartPuellaMagiTransformation;
import com.MadokaMagica.mod_madokaMagica.commands.CommandDisplayInformation;
import com.MadokaMagica.mod_madokaMagica.commands.CommandTestWish;
import com.MadokaMagica.mod_madokaMagica.commands.CommandPlayerData;

import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.ItemSoulGemManager;

import com.MadokaMagica.mod_madokaMagica.proxies.CommonProxy;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.items.ItemGriefSeed;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.handlers.PMEventHandler;
import com.MadokaMagica.mod_madokaMagica.effects.PMEffects;

import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchMinion;
import com.MadokaMagica.mod_madokaMagica.entities.EntityIncubator;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

@Mod(modid=MadokaMagicaMod.MODID, version=MadokaMagicaMod.VERSION)
public class MadokaMagicaMod {
    public static final String MODID   = "MadokaMagicaMod";
    public static final String VERSION = "0.01a";

    @Instance
    public static MadokaMagicaMod instance;

    public static final CreativeTabs PMMMCreativeTab = new CreativeTabs("madokamagica"){
        @Override
        @SideOnly(Side.CLIENT)
        public String getTranslatedTabLabel(){
            return "Madoka Magica";
        }

        @Override
        public Item getTabIconItem(){
            return MadokaMagicaItems.item_soulgem;
        }
    };

    private static PlayerDataTrackerManager playerDataTrackerManager;
    private static ItemSoulGemManager itemSoulGemManager;
    // private static ListenerList madokaMagicaEventListener;

    private static ISound wtransform_music = null;

    public static int entityID=348;

    @SidedProxy(clientSide="com.MadokaMagica.mod_madokaMagica.proxies.ClientProxy",serverSide="com.MadokaMagica.mod_madokaMagica.proxies.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event){
        // Load configuration
        MadokaMagicaConfig.loadConfig(event);

        proxy.preinit(event);
    }

    @EventHandler
    public void onInitialization(FMLInitializationEvent event){
        proxy.init(event);

        MadokaMagicaItems.loadItems();

        wtransform_music = PositionedSoundRecord.func_147673_a(new ResourceLocation(MadokaMagicaMod.MODID + ":transformmusic"));

        playerDataTrackerManager = PlayerDataTrackerManager.getInstance();
        itemSoulGemManager = ItemSoulGemManager.getInstance();

        GameRegistry.registerItem(MadokaMagicaItems.item_soulgem,"Soul Gem");
        GameRegistry.registerItem(MadokaMagicaItems.item_griefseed,"Grief Seed");

        FMLCommonHandler.instance().bus().register(new PMEventHandler());
        MinecraftForge.EVENT_BUS.register(new PMEventHandler());

        // GameRegistry.addShapelessRecipe(new ItemStack(itemSoulGem,1,0),new ItemStack(itemSoulGem,1,0),new ItemStack(itemGriefSeed,1,0));

        /*
        EntityRegistry.registerModEntity(MobPMWitch.class, "PMWitch", properties.PMWitchEntityID,this,70,1,false);
        EntityRegistry.registerModEntity(MobPMLabrynthEntrance.class,"PMLabrynthEntrance",properties.PMLabrynthEntranceEntityID,this,70,1,true);
        */

        // Just do it the way the Touhou Items mod does things. Because I would like to have a spawn egg for now. Maybe later I'll switch it back
        // EntityRegistry.registerGlobalEntityID(EntityPMWitchMinion.class,"PMWitchMinion",entityID++,0x3F5505,0x4E6414);
        // EntityRegistry.registerGlobalEntityID(EntityIncubator.class,"Incubator",entityID++,0xFFFFFF,0xFF0000);

        registerEntityWithEgg(EntityIncubator.class,"Incubator",++entityID,80,3,false,0xFFFFFF,0xFF0000);
        GameRegistry.registerItem(MadokaMagicaItems.item_incubatormonsterplacer,"spawnEgg"+"Incubator");

        registerEntityWithEgg(EntityPMWitchLabrynthEntrance.class,"PMWitchLabrynthEntrance",++entityID,80,3,false,0xFFFFFF,0xFF0000);
        GameRegistry.registerItem(MadokaMagicaItems.item_labrynthentranceplacer,"spawnEgg"+"LabrynthEntrance");

        if(MadokaMagicaConfig.enableCorruptionVisualEffects){
            // Overwrite EntityRenderer so that activateNextShader does nothing if PMEffects is still active
            class OverriddenEntityRenderer extends EntityRenderer {
                public OverriddenEntityRenderer(){
                    super(Minecraft.getMinecraft(),Minecraft.getMinecraft().getResourceManager());
                }

                @Override
                public void activateNextShader(){
                    if(PMEffects.failureCount < PMEffects.MAXIMUM_FAILURE_COUNT) return;
                    super.activateNextShader();
                }
            }

            Minecraft.getMinecraft().entityRenderer = new OverriddenEntityRenderer();
            ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(Minecraft.getMinecraft().entityRenderer);
        }
    }

    @EventHandler
    public void onPostInitializationEvent(FMLPostInitializationEvent event){
        proxy.postinit(event);
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        // Load each player data thing and add it to playerDataTrackerManager
        event.registerServerCommand( CommandStartWitchTransformation.getInstance() );
        // event.registerServerCommand( CommandStartPuellaMagiTransformation.getInstance() );
        event.registerServerCommand( CommandDisplayInformation.getInstance() );
        event.registerServerCommand( CommandPlayerData.getInstance() );
        event.registerServerCommand( CommandTestWish.getInstance() );
    }

    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event){
        playerDataTrackerManager.manage();
        itemSoulGemManager.manage();
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

    public void registerEntityWithEgg(Class<? extends Entity> entityClass, String identifier, int id, int trackRange, int frequency, boolean sendsVelocityUpdates, int backgroundColor, int foregroundColor){
        EntityRegistry.registerModEntity(entityClass, identifier,id,MadokaMagicaMod.instance,trackRange,frequency,sendsVelocityUpdates);
        // Create a spawn egg (basically copied from EntityList.java)
        //EntityList.entityEggs.put(Integer.valueOf(id), new EntityList.EntityEggInfo(id, backgroundColor, foregroundColor));
    }
}
