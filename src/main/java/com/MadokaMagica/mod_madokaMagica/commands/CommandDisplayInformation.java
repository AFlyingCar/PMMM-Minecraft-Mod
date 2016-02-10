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
    public final String getCommandName(){
        return name;
    }

    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name+" "+"<user name>";
        return usage;
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] command){
        EntityPlayer target = MinecraftServer.getServer().getConfigurationManager().func_152612_a(command[0]);
        if(target == null){
            sendChat(getCommandSenderAsPlayer(sender),"Unknown player " + command[0]);
            return;
        }
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(target);
        sendChat(target,pmdt.toString());
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

