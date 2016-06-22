package com.MadokaMagica.mod_madokaMagica.items;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;

import com.MadokaMagica.mod_madokaMagica.items.ItemSoulGem;
import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

public class ItemGriefSeed extends ItemSoulGem{
	public ItemGriefSeed(){
        super();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
        super.onItemRightClick(stack,world,player);

        PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(player);
        if(tracker == null){
            System.out.println("getTrackerByPlayer returned NULL!");
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
}
