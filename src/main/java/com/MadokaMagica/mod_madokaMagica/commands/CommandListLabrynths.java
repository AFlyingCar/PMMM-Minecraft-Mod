package com.MadokaMagica.mod_madokaMagica.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;

public class CommandListLabrynths extends CommandBase {
    private String name;
    private static CommandListLabrynths instance;

    private CommandListLabrynths(){
        this.name = "pmmm-list-labrynths";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics){
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-list-labrynths
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/" + name;
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        List<LabrynthDetails> details = LabrynthManager.getInstance().getDetailsList();
        for(int i=0;i<details.size();i++){
            sendChat((EntityPlayer)sender,"Index " + i + ": DimID="+details.get(i).dimID+",DimName="+details.get(i).dimName);
        }
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return false;
    }

    public static void sendChat(EntityPlayer player,String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandListLabrynths getInstance(){
        if(CommandListLabrynths.instance == null){
            CommandListLabrynths.instance = new CommandListLabrynths();
        }
        return CommandListLabrynths.instance;
    }
}

