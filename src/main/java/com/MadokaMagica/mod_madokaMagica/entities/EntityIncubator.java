package com.MadokaMagica.mod_madokaMagica.entities;

import java.util.Map;

import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.factories.WishFactory;

public class EntityIncubator extends EntityCreature{
    private Map<EntityPlayer,Wish> wishByPlayer;

    public EntityIncubator(World world){
        super(world);
        System.out.println("EntityIncubator(World) - Constructor");
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
        System.out.println("EntityIncubator.entityInit()");
        super.entityInit();
    }

    // TODO: Finish this method
    @Override
    public void writeEntityToNBT(NBTTagCompound tag){
        System.out.println("EntityIncubator.writeEntityToNBT(NBTTagCompound)");
        super.writeEntityToNBT(tag);
    }

    // TODO: Finish this method
    @Override
    public void readEntityFromNBT(NBTTagCompound tag){
        System.out.println("EntityIncubator.readEntityFromNBT(NBTTagCompound)");
        super.readEntityFromNBT(tag);
    }
}
