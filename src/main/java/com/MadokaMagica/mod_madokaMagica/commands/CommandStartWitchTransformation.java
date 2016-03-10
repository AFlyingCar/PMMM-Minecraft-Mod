package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandStartWitchTransformation extends CommandBase{
    private String name;
    private static CommandStartWitchTransformation instance;

    private CommandStartWitchTransformation(){
        this.name = "pmmm-start-transformation-witch";
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name+" "+"<user name>";
        return usage;
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] command){
        EntityPlayer senderAsPlayer = (EntityPlayer)sender;
        if(command.length != 1){
            sendChat(senderAsPlayer,getCommandUsage(sender));
            return;
        }
        EntityPlayer target = senderAsPlayer.worldObj.getPlayerEntityByName(command[0]);
        
        if(target == null){
            sendChat(getCommandSenderAsPlayer(sender),"Unknown player: " + command[0]);
            return;
        }
        PMDataTracker tracker = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(target);
        if(!tracker.isTransformingIntoWitch()){
            MinecraftForge.EVENT_BUS.post(new MadokaMagicaWitchTransformationEvent(tracker));
        }
    }

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandStartWitchTransformation getInstance(){
        if(CommandStartWitchTransformation.instance == null)
            CommandStartWitchTransformation.instance = new CommandStartWitchTransformation();
        return CommandStartWitchTransformation.instance;
    }
}

