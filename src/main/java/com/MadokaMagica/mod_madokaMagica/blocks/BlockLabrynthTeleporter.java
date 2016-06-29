package com.MadokaMagica.mod_madokaMagica.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.common.util.ForgeDirection;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;

// Basically it is an invisible block that teleports any player or villager touching it
public class BlockLabrynthTeleporter extends BlockContainer { 
    public static final int MAX_DECAY_WAIT_TIME = 10; // 10 block ticks

    public boolean wasPlacedByPlayer = false;

    public class TileEntityLabrynthTeleporter extends TileEntity{
        public int decayCounter = 0; // Counter until the time of decay
        public boolean placedByPlayer;
        public boolean stabilized;
        public World linked;

        public TileEntityLabrynthTeleporter(World world,boolean placedByPlayer){
            this.worldObj = world;
            this.placedByPlayer = placedByPlayer;
            this.stabilized = false;
        }

        @Override
        public void updateEntity(){
            if(placedByPlayer || stabilized) return;

            decayCounter++;
            if(decayCounter >= MAX_DECAY_WAIT_TIME)
                this.worldObj.setBlockToAir(this.xCoord,this.yCoord,this.zCoord); 
        }

        public void setLink(World linked){
            this.linked = linked;
        }
    }

    public BlockLabrynthTeleporter(){
        super(Material.portal);
        // The teleporters give off a soft glow
        this.setLightLevel(0.5F);
    }

    @Override
    public TileEntity createNewTileEntity(World world,int metadata){
        TileEntity e = new TileEntityLabrynthTeleporter(world,this.wasPlacedByPlayer);
        wasPlacedByPlayer = false;
        if(e == null){
            System.out.println("We have a NULL TileEntity! EVERYBODY PANIC!");
            return null;
        }
        EntityPMWitchLabrynthEntrance entrance = LabrynthManager.getInstance().retrieveEntrance(metadata);
        if(entrance == null){
            System.out.println("retrieveEntrance returned NULL. This means either we were placed by a player in Creative Mode (fucking cheater) or something has gone horribly horribly wrong.");
            return e;
        }
        ((TileEntityLabrynthTeleporter)e).setLink(entrance.linkedWorldObj);
        return e;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z){
        // This block can only exist in the Overworld and in Labrynths (the exit)
        if((world.provider.dimensionId != 0 || world.provider.dimensionId != MadokaMagicaConfig.labrynthDimensionID) && !wasPlacedByPlayer){
            world.setBlockToAir(x,y,z);
            return;
        }

        // Create the tile entity
        // Check if this creates the TileEntity properly
        //world.setTileEntity(x,y,z,this.createNewTileEntity(world,world.getBlockMetadata(x,y,z)));

        // Stabilize it if it was placed by a player
        if(wasPlacedByPlayer){
            stabilizeEntrance(world,x,y,z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
        PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(player);
        if(tracker != null && tracker.isPuellaMagi()){
            ItemStack current_item = player.inventory.getCurrentItem();
            if(current_item != null && current_item.getItem() instanceof ItemSoulGem){
                // TODO: Do something which shows a visual effect of an entrance opening
                // Also allow the player to travel through it
                // Stabilize the entrance
                this.stabilizeEntrance(world,x,y,z);
                System.out.println("Player meets all the requirements to open entrance, but this hasn't been implemented yet!");
            }
        }
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
        // NULL check
        if(entity == null) return;

        if(entity instanceof EntityPlayer || entity instanceof EntityVillager){
            if(entity.ridingEntity != null)
                entity.ridingEntity = null;
            // Try to teleport the rider (if one exists) as well.
            // There is no reason that this should cause infinite recursion (I think)
            // If there is no riddenByEntity (null), then it should immediately exit
            onEntityCollidedWithBlock(world,x,y,z,entity.riddenByEntity);

            entity.travelToDimension(getToDimension(world));
        }
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitx, float hity, float hitz, int bmeta){
        wasPlacedByPlayer = true;
        return super.onBlockPlaced(world,x,y,z,side,hitx,hity,hitz,bmeta);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random r){
        super.updateTick(world,x,y,z,r);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass(){
        // Alpha block
        return MadokaMagicaConfig.useDebugModels ? 0 : 1;
    }

    @Override
    public boolean renderAsNormalBlock(){
        return !MadokaMagicaConfig.useDebugModels;
    }

    @Override
    public int quantityDropped(Random r){
        return 0;
    }

    @Override
    public boolean isOpaqueCube(){
        return !MadokaMagicaConfig.useDebugModels;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z){
        return Item.getItemById(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iiconregister){
        if(MadokaMagicaConfig.useDebugModels)
            this.blockIcon = iiconregister.registerIcon("stone");
        else
            this.blockIcon = iiconregister.registerIcon("air"); // TODO: Fix this
    }

    // We don't want to render this block at all
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side){
        if(!MadokaMagicaConfig.useDebugModels)
            return iba.isSideSolid(x,y,z,ForgeDirection.getOrientation(side),true);
        else
            return super.shouldSideBeRendered(iba,x,y,z,side);
    }

    @Override
    public MapColor getMapColor(int i){
        if(MadokaMagicaConfig.useDebugModels)
            return MapColor.airColor;
        else
            return MapColor.pinkColor; // So that is easily visible
    }

    public int getToDimension(World world){
        return world.provider.dimensionId == 0 ? MadokaMagicaConfig.labrynthDimensionID : 0;
    }

    public void stabilizeEntrance(World world, int x, int y, int z){
        TileEntity te = world.getTileEntity(x,y,z);
        if(te instanceof TileEntityLabrynthTeleporter){
            TileEntityLabrynthTeleporter telt = (TileEntityLabrynthTeleporter)te;
            telt.stabilized = true;
        }
    }
}

