package com.MadokaMagica.mod_madokaMagica.items;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

public class ItemGriefSeed extends ItemSoulGem{
	public ItemGriefSeed(){
        super();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        super.onItemRightClick(stack,world,player);

        PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByUUID(player.getPersistentID());
        if(tracker == null){
            System.out.println("getTrackerByUUID returned NULL!");
            return stack;
        }

        // Don't do anything if the player isn't a puella magi
        if(!tracker.isPuellaMagi())
            return stack;

        if(tracker.getSoulGem() == null){
            System.out.println("tracker.playerSoulGem is null despite the fact that tracker.isPuellaMagi() returned true!");
            return stack;
        }

        if(Helper.doesPlayerHaveItemStack(player,tracker.getSoulGem()))
            ItemSoulGem.cleanse(tracker.getSoulGem(),stack);

        return stack;
    }

    @Override
    protected void addDespairTooHighInformation(List list){
        list.add("Despair is at dangerous levels! Safely dispose of this Grief Seed before it hatches a new witch!");
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z,EntityLivingBase entity){
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity){
        return true;
    }

    public static PMDataTracker getOwner(ItemStack griefseed){
        // Loooooooooong sanity check
        if(griefseed == null ||
           griefseed.getTagCompound() == null ||
           !griefseed.getTagCompound().hasKey("PLAYER_UUID_LEAST_SIG") ||
           !griefseed.getTagCompound().hasKey("PLAYER_UUID_MOST_SIG"))
                return null;
        return PlayerDataTrackerManager.getInstance().getTrackerByUUID(
                    new UUID(
                        griefseed.getTagCompound().getLong("PLAYER_UUID_LEAST_SIG"),
                        griefseed.getTagCompound().getLong("PLAYER_UUID_MOST_SIG")
                    )
               );
    }

    // Create an EntityItem from a EntityPMWitch object
    public Entity createEntity(EntityPMWitch entity){
        Entity item = new EntityItem(entity.worldObj,entity.posX,entity.posY,entity.posZ);
        NBTTagCompound nbt = item.getEntityData();
        // Just another sanity check. 
        if(nbt == null){
            System.out.println("ERROR! FAILED TO CREATE EntityItem from EntityPMWitch!");
            return null;
        }

        NBTTagCompound data = new NBTTagCompound();
        // TODO: Somehow make it so that this can keep track of the EntityPMWitch object's PMDataTracker object
        // It's simple, we track the data tracker
        //      - The Entity "TrackerMan"

        nbt.setTag("EntityPMWitchData",data);
        nbt.setBoolean("HAS_ENTITYPMWITCH_DATA",false); // TODO: Set this back to true once the above TODO has been completed

        return item;
    }
}

