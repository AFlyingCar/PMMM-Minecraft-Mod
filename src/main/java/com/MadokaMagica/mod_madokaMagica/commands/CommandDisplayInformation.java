package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandDisplayInformation extends CommandBase{
    private String name;
    private static CommandDisplayInformation instance;

    private CommandDisplayInformation(){
        this.name = "pmmm-display-info";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics){
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-display-info <user name>
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name+" "+"<user name>";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        EntityPlayer player = (EntityPlayer)sender;//(EntityPlayer)(CommandBase.getCommandSenderAsPlayer(sender));
        EntityPlayer target = null;
        if(command.length == 1)
            target = player.worldObj.getPlayerEntityByName(command[0]);
        else
            target = player;

        // This should only ever happen if a username is passed as an argument
        if(target == null){
            sendChat(getCommandSenderAsPlayer(sender),"Unknown player " + command[0]);
            return;
        }
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(target);
        if(pmdt != null)
            for(String s : pmdt.toString().split("\n"))
                sendChat(target,s);
        else
            sendChat(player,"Error: the target does not have a data tracker!");
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return index == 1;
    }

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandDisplayInformation getInstance(){
        if(CommandDisplayInformation.instance == null)
            CommandDisplayInformation.instance = new CommandDisplayInformation();
        return CommandDisplayInformation.instance;
    }
}

