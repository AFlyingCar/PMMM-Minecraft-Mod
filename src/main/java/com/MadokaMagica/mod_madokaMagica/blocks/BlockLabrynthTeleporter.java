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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaConfig;

// Basically it is an invisible block that teleports any player or villager touching it
public class BlockLabrynthTeleporter extends BlockContainer { 
    public static final int MAX_DECAY_WAIT_TIME = 10; // 10 block ticks

    public boolean wasPlacedByPlayer = false;

    public class TileEntityLabrynthTeleporter extends TileEntity{
        public int decayCounter = 0; // Counter until the time of decay

        public TileEntityLabrynthTeleporter(World world){
            this.worldObj = world;
        }

        @Override
        public void updateEntity(){
            decayCounter++;
            if(decayCounter >= MAX_DECAY_WAIT_TIME)
                this.worldObj.setBlockToAir(this.xCoord,this.yCoord,this.zCoord); 
        }
    }

    public BlockLabrynthTeleporter(){
        super(Material.portal);
        // The teleporters give off a soft glow
        this.setLightLevel(1.0F);
    }


    @Override
    public TileEntity createNewTileEntity(World world,int par2){
        return new TileEntityLabrynthTeleporter(world);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z){
        if(wasPlacedByPlayer) return;

        // This block can only exist in the Overworld and in Labrynths (the exit)
        if(world.provider.dimensionId != 0 || world.provider.dimensionId != MadokaMagicaConfig.labrynthDimensionID)
            world.setBlockToAir(x,y,z);
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
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass(){
        // Alpha block
        return 1;
    }

    @Override
    public boolean renderAsNormalBlock(){
        return false;
    }

    @Override
    public int quantityDropped(Random r){
        return 0;
    }

    @Override
    public boolean isOpaqueCube(){
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z){
        return Item.getItemById(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iiconregister){
        this.blockIcon = iiconregister.registerIcon("air"); // TODO: Does this work?
    }

    // We don't want to render this block at all
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side){
        return false;
    }

    @Override
    public MapColor getMapColor(int i){
        return MapColor.airColor;
    }

    public int getToDimension(World world){
        return world.provider.dimensionId == 0 ? MadokaMagicaConfig.labrynthDimensionID : 0;
    }
}

