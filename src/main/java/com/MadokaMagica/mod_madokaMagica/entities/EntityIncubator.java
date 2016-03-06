package com.MadokaMagica.mod_madokaMagica.entities;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.factories.WishFactory;

public class EntityIncubator extends Entity{
	private Map<EntityPlayer,Wish> wishByPlayer;

	public EntityIncubator(World world){
		super(world);
	}

	public boolean processChat(EntityPlayer player, String message){
		Wish wish = WishFactory.generateWish(player,message);
		if(wish != null){
			wishByPlayer.put(player,wish);
			return true;
		}
		return false;
	}

	// TODO: Finish this method
	@Override
	public void entityInit(){
		
	}

	// TODO: Finish this method
	@Override
	public void writeEntityToNBT(NBTTagCompound tag){
		
	}

	// TODO: Finish this method
	@Override
	public void readEntityFromNBT(NBTTagCompound tag){
		
	}
}
