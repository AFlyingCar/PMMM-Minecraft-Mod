package com.MadokaMagica.mod_madokaMagica.entities;

import java.util.Random;
import java.util.List;

import net.minecraft.util.MathHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.DamageSource;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;
import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithChunkBias;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIWanderWithVillageBias;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAIRandomTeleportPlayerOrVillager;
import com.MadokaMagica.mod_madokaMagica.entities.ai.EntityAICreateRandomTeleporterBlocks;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer; 
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class EntityPMWitchLabrynthEntrance extends EntityCreature{
    private Random rand;

    public World linkedWorldObj; // TODO: Do we need this?
    public LabrynthDetails labrynthDetails;
    public EntityPMWitch witch;
    public boolean loadingFromFile = false;

    public EntityPMWitchLabrynthEntrance(World world, LabrynthDetails details){
        super(world);

        labrynthDetails = details;
        rand = new Random();

        setupAITasks();
    }

    public EntityPMWitchLabrynthEntrance(World world){
        super(world);
        labrynthDetails = new LabrynthDetails();
        rand = new Random();
        setupAITasks();

        //Helper.Debug.printCurrentStackTrace();

        //System.out.println("Size of Entrance task list: " + this.tasks.taskEntries.size());
        //System.out.println("Size of Entrance targetTask list: " + this.targetTasks.taskEntries.size());
        //System.out.println("Constructing.");
    }

    private void setupAITasks(){
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
        this.tasks.addTask(0,new EntityAIWanderWithVillageBias(this,0.05F));
        //this.tasks.addTask(1,new EntityAIRandomTeleportPlayerOrVillager(this));
        this.tasks.addTask(1,new EntityAICreateRandomTeleporterBlocks(this));
    }

    @Override
    public void onEntityUpdate(){
        super.onEntityUpdate();

        // If we don't have a witch or labrynth, then check if we should kill it or not
        // If not, then we just won't do anything
        // TODO: Somehow add this check to EntityAIRandomTeleportPlayerOrVillager
        if(this.witch == null || this.linkedWorldObj == null){
            if(!MadokaMagicaConfig.killLabrynthEntranceWithoutWitchOrLabrynth){
                return;
            }else{
                setDead();
                return;
            }
        }
    }

    @Override
    protected void entityInit(){
        super.entityInit();


        // Only do this if we're talking about the server
        if((!loadingFromFile) && labrynthDetails == null && this.worldObj.isRemote && MadokaMagicaConfig.createRandomizedLabrynthsIfNoneExist){
            //System.out.println("Preparing to call createRandomizedWitch()");
            createRandomizedWitch();
        }
    }

    // This method was almost completely shamelessly copied from StevenRS11's DimensionalDoors mod. Specifically, DDTeleporter.java
    // Not exactly copied though, since we don't need to worry about everything that StevenRS11 did.
    public void teleportEntity(Entity entity){
        System.out.println("WARNING! This method has not been finished yet. It requires Labrynths to be at least partially coded first!");
        // This is in place to trick the compiler
        if(true)
            return;



        if(entity == null){
            throw new IllegalArgumentException("entity is null.");
        }

        // TODO: Figure out if WorldServer really does extend from World, or if the documentation lied again
        WorldServer old = (WorldServer)entity.worldObj;// (entity.worldObj instanceof WorldServer) ? (WorldServer)entity.worldObj : entity.worldObj;
        WorldServer nwo = LabrynthManager.getInstance().loadLabrynth(this.witch);
        EntityPlayerMP player = (entity instanceof EntityPlayerMP) ? (EntityPlayerMP)entity : null;

        // Don't teleport both entities, make sure that the rider gets unmounted
        if(entity.riddenByEntity != null){
            entity.riddenByEntity.mountEntity(null); // unmount
        }

        if(player != null){
            // TODO: Find a way to set this value correctly
            // player.dimension = ?;

            // Sanity check
            old.getPlayerManager().removePlayer(player);
            nwo.getPlayerManager().addPlayer(player);
            ChunkCoordinates coords = this.witch.worldObj.provider.getRandomizedSpawnPoint();
            player.setPositionAndUpdate(coords.posX,coords.posY,coords.posZ);
        }

        nwo.spawnEntityInWorld(entity);
        entity.setWorld(nwo);
        
        entity.worldObj.updateEntityWithOptionalForce(entity,false);

        if(player != null){
            // Load the chunk that we are going to spawn in
            nwo.getChunkProvider().loadChunk(MathHelper.floor_double(entity.posX) >> 4,MathHelper.floor_double(entity.posZ) >> 4);

            // TODO: The Dimensional Doors mod calls this, but according to Forge, this method doesn't exist. I checked, and it doesn't. So I don't know what StevenRS11 was trying to do, but I can't copy it.
            // GameRegistry.onPlayerChangedDimension((EntityPlayer)entity);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound rootTag){
        // TODO: Finish this method
        if(this.labrynthDetails != null){
            rootTag.setInteger("LINKED_ID",this.labrynthDetails.dimID);
        }

        /*
        // Save LabrynthDetails object to NBT here, so we can rebuild it later
        NBTTagCompound detailsTag = new NBTTagCompound(); // Tag just for the Labrynth details
        NBTTagCompound worldTag = new NBTTagCompound(); // Tag just for the world shit
        detailsTag.setInteger("dimID",labrynthDetails.dimID);
        // Make sure we don't keep getting NullPointerExceptions and IllegalArugmentExceptions
        // We're still getting them...
        if(labrynthDetails.dimName != null && !labrynthDetails.dimName.equals(""))
            detailsTag.setString("dimName",labrynthDetails.dimName);
        else
            detailsTag.setString("dimName","Labrynth Details");
        labrynthDetails.world.writeToNBT(worldTag);
        detailsTag.setTag("world",worldTag);
        rootTag.setTag("PMMM LABRYNTH DETAILS",detailsTag);
        */
        super.writeEntityToNBT(rootTag);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound rootTag){
        System.out.println("");
        loadingFromFile = true;
        // TODO: Finish this method

        // Fail if we don't have this tag, because without it we don't know where to go
        if(!rootTag.hasKey("LINKED_ID")) return;
        int linkedDimID = rootTag.getInteger("LINKED_ID");
        this.labrynthDetails = LabrynthManager.getInstance().getDetailsByDimID(linkedDimID);
        // NOTE: Maybe we should throw an error if labrynthDetails is null?

        if(labrynthDetails == null && this.worldObj.isRemote && MadokaMagicaConfig.createRandomizedLabrynthsIfNoneExist){
            System.out.println("From readEntityFromNBT comes: ");
            createRandomizedWitch();
        }

        /*
        NBTTagCompound detailstag = rootTag.getCompoundTag("PMMM LABRYNTH DETAILS");
        if(!(detailstag.hasKey("dimID") && detailstag.hasKey("dimName") && detailstag.hasKey("world"))){
            System.out.println("Found LabrynthEntrance entity without any Labrynth data saved. Killing.");
            this.setDead();
            return;
        }
        int dimID = detailstag.getInteger("dimID");
        String dimName = detailstag.getString("dimName");
        NBTTagCompound worldTag = detailstag.getCompoundTag("world");
        LabrynthWorldServer worldServer = LabrynthWorldServer.loadFromNBT(worldTag);
        if(worldServer == null){
            System.out.println("Failed to load LabrynthWorldServer from NBT.");
            this.setDead();
            return;
        }
        LabrynthDetails details = new LabrynthDetails();
        details.dimID = dimID;
        details.dimName = dimName;
        details.world = worldServer;
        */

        super.readEntityFromNBT(rootTag);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float what){
        if(MadokaMagicaConfig.canDamageLabrynthEntrances){
            return super.attackEntityFrom(source,what);
        }
        return false;
    }

    // We don't want this to go to a dimension it shouldn't go to (Hint: It shouldn't go to any dimension, and should stay where it is, alone and unloved until its death)
    @Override
    public void travelToDimension(int dimID){
        return;
    }

    @Override
    public boolean isAIEnabled(){
        return true;
    }

    @Override
    public void onDeath(DamageSource dsource){
        if(this.worldObj.isRemote){
            // Make sure that if the Entrance is killed, the labrynth goes down with it (we don't want the labrynths to live on, constantly taking up space)
            labrynthDetails.markForDestruction = true;
            System.out.println("An EntityPMWitchLabrynthEntrance has died! Marking its linked Labrynth (#" + labrynthDetails.dimID + ") for destruction.");
            LabrynthManager.getInstance().markDirty(true); // Make sure to mark it as dirty, since we don't want to save this labrynth if it no longer exists.
        }
        super.onDeath(dsource);
    }

    // A method for creating a randomized witch upon creation
    protected void createRandomizedWitch(){
        System.out.println("Creating a randomized Labrynth and witch");
        // Create a randomized data tracker
        PMDataTracker randTracker = new PMDataTracker();
        randTracker.tagData = new NBTTagCompound(); // Create a new NBTTagCompound to prevent a NullPointerException
        randTracker.randomize();

        // Make sure that we don't accidentally generate a second one
        if(this.labrynthDetails != null){
            this.labrynthDetails.markForDestruction = true;
            LabrynthManager.getInstance().markDirty(true);
        }

        // Build a new labrynth
        LabrynthDetails randDetails = LabrynthFactory.createLabrynth(randTracker);

        // Make sure we don't continue if createLabrynth returns null
        if(randDetails == null) return;

        // Create a new witch
        EntityPMWitch randWitch = new EntityPMWitch(randDetails.world);
        randTracker.setEntity(randWitch);

        // Set our own data to this new randomized data
        this.labrynthDetails = randDetails;
        this.linkedWorldObj = randDetails.world;
        this.witch = randWitch;

        LabrynthManager.getInstance().registerLabrynthDetails(this,randDetails);
    }
}
