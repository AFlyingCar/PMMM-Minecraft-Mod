package com.MadokaMagica.mod_madokaMagica.commands;

import java.util.List;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import net.minecraftforge.common.MinecraftForge;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaCreateLabrynthEvent;
import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory;

public class CommandCreateLabrynth extends CommandBase {
    private String name;
    private static CommandCreateLabrynth instance;

    private CommandCreateLabrynth(){
        this.name = "pmmm-create-labrynth";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics){
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-create-labrynth <Player name|Entity UUID> <x> <y> <z>
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/" + name + " <player name|entity UUID> <x> <y> <z>";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        PMDataTracker tracker = null;
        if(command.length < 1){
            sendChat((EntityPlayer)sender,"Invalid number of arguments.");
            return;
        }

        try{
            UUID IDNum = UUID.fromString(command[0]);
            tracker = PlayerDataTrackerManager.getInstance().getTrackerByUUID(IDNum);
            if(tracker == null){
                sendChat((EntityPlayer)sender,"Entity #" + IDNum + " does not have a data tracker.");
                return;
            }
        }catch(IllegalArgumentException except){
            EntityPlayer target = ((EntityPlayer)sender).worldObj.getPlayerEntityByName(command[0]);
            if(target == null){
                sendChat((EntityPlayer)sender,"Unknown player: " + command[0]);
                return;
            }
            tracker = PlayerDataTrackerManager.getInstance().getTrackerByUUID(target.getPersistentID());
            if(tracker == null){
                sendChat((EntityPlayer)sender,"Error: The player '" + command[0] + "' does not have a data tracker.");
                return;
            }
        }

        MinecraftForge.EVENT_BUS.post(new MadokaMagicaCreateLabrynthEvent(tracker));

/*
        LabrynthDetails details = LabrynthFactory.createLabrynth(tracker);
        EntityPMWitchLabrynthEntrance entrance = new EntityPMWitchLabrynthEntrance(((EntityPlayer)sender).worldObj,details);
        LabrynthManager.getInstance().registerLabrynthDetails(entrance,details);

        if(command.length >= 4){
            try{
                int x = Integer.valueOf(command[1]).intValue();
                int y = Integer.valueOf(command[2]).intValue();
                int z = Integer.valueOf(command[3]).intValue();

                entrance.setPosition(x,y,z);
                ((EntityPlayer)sender).worldObj.spawnEntityInWorld(entrance);
            }catch(NumberFormatException e){
                sendChat((EntityPlayer)sender,"Error: one of the passed coordinates is not an integer. Spawning randomly");
                Helper.spawnEntityRandomlyNearPlayer((EntityPlayer)sender,entrance);
            }
        }else{
            Helper.spawnEntityRandomlyNearPlayer((EntityPlayer)sender,entrance);
        }


        sendChat((EntityPlayer)sender,"A new Labrynth (ID#" + details.dimID + ") has been created. with a new LabrynthEntrance at (" + entrance.posX + ", " + entrance.posY + ", " + entrance.posZ + ")");
*/
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return false;
    }

    public static void sendChat(EntityPlayer player,String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandCreateLabrynth getInstance(){
        if(CommandCreateLabrynth.instance == null){
            CommandCreateLabrynth.instance = new CommandCreateLabrynth();
        }
        return CommandCreateLabrynth.instance;
    }
}

