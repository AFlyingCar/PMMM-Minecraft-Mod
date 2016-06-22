package com.MadokaMagica.mod_madokaMagica.items.placers;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.renderer.texture.IIconRegister;

import net.minecraft.util.IIcon;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import com.MadokaMagica.mod_madokaMagica.MadokaMagicaMod;

public class EntityPlacer extends ItemMonsterPlacer {
    @SideOnly(Side.CLIENT)
    protected IIcon icon;
    protected int colorBack;
    protected int colorSpot;
    protected String spawnName;
    protected String spawnNameFull;
    protected EntityLiving entity;

    public EntityPlacer(IIcon icon, int colorBack, int colorSpot, String spawnName, EntityLiving entity){
        super();

        setHasSubtypes(false);
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setEntitySpawnName(spawnName);

        this.icon = icon;
        this.colorBack = colorBack;
        this.colorSpot = colorSpot;
        this.spawnName = spawnName;
        this.entity = entity;
    }

    // Override this method if you would like to do anything special before spawning the entity
    protected void custonSpawnRoutines(){
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int what4, float what5, float what6, float what7){
        if(world.isRemote){ // Check if server side
            return true;
        }else{
            Block block = world.getBlock(x,y,z);
            x+=Facing.offsetsXForSide[what4];
            y+=Facing.offsetsYForSide[what4];
            z+=Facing.offsetsZForSide[what4];
            double yoffset = 0.0D;

            if(what4 == 1 && block.getRenderType() == 11){
                yoffset = 0.5D;
            }

            Entity entity = spawnEntity(world,x+0.5D,y+yoffset,z+0.5D);

            if(entity != null){
                // Set a custom name if it is the egg has one
                if(entity instanceof EntityLivingBase && stack.hasDisplayName())
                    ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());

                // decrease the size of the spawn stack if and ONLY if the player is not in creative mode
                if(!player.capabilities.isCreativeMode)
                    --stack.stackSize;
            }
            return true;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        if(world.isRemote){ // Check if server side
            return stack;
        }else{
            MovingObjectPosition position = getMovingObjectPositionFromPlayer(world,player,true);

            if(position == null){
                return stack;
            }else{
                if(position.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
                    int x = position.blockX;
                    int y = position.blockY;
                    int z = position.blockZ;

                    if(!world.canMineBlock(player,x,y,z)){
                        return stack;
                    }

                    if(!player.canPlayerEdit(x,y,z,position.sideHit,stack)){
                        return stack;
                    }

                    if(world.getBlock(x,y,z) instanceof BlockLiquid){
                        Entity entity = spawnEntity(world,x,y,z);
                        if(entity != null){
                            if (entity instanceof EntityLivingBase && stack.hasDisplayName()){
                                ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
                            }

                            if (!player.capabilities.isCreativeMode){
                                --stack.stackSize;
                            }
                        }
                    }
                }
                return stack;
            }
        }
    }

    public Entity spawnEntity(World world, double x, double y, double z){
        if(!world.isRemote){
            spawnNameFull = MadokaMagicaMod.MODID+"."+spawnName;
            if(EntityList.stringToClassMapping.containsKey(spawnNameFull)){
                entity = (EntityLiving)EntityList.createEntityByName(spawnNameFull,world);
                entity.setLocationAndAngles(x,y,z,MathHelper.wrapAngleTo180_float(world.rand.nextFloat()*360.0F),0.0F);
                world.spawnEntityInWorld(entity);
                entity.onSpawnWithEgg((IEntityLivingData)null);
                entity.playLivingSound();
            }else{
                System.out.println("Unable to find entity " + spawnNameFull + "!");
            }
        }

        return entity;
    }

    // List of items with the same ID, but different meta-data (i.e: dye has 16 different items, all with the same ID)
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item,CreativeTabs tab,List list){
        list.add(new ItemStack(item,1,0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int colorType){
        return (colorType == 0) ? colorBack : colorSpot;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses(){
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack){
        return "Spawn " + spawnName;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister){
        super.registerIcons(iconRegister);
        icon = iconRegister.registerIcon(getIconString()+"_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damageValue, int renderPass){
        return renderPass > 0 ? icon : super.getIconFromDamageForRenderPass(damageValue,renderPass);
    }

    public void setColors(int colorBack,int colorSpot){
        this.colorBack = colorBack;
        this.colorSpot = colorSpot;
    }

    public int getColorBack(){
     return colorBack;
    }
    
    public int getColorSpot(){
     return colorSpot;
    }
    
    public void setEntitySpawnName(String spawnName){
        this.spawnName = spawnName;
        this.spawnNameFull = MadokaMagicaMod.MODID+"."+this.spawnName; 
    }
}

