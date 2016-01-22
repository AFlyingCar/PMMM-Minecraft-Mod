package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaWitchTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

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
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name+" "+"<user name>";
        return usage;
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] command){
        EntityPlayer target = MinecraftServer.getServer().getConfigurationManager().func_152612_a(command[0]);
        if(target == null){
            // TODO: We should really add some more descriptive error message.
            sendChat(getCommandSenderAsPlayer(sender),"Invalid use of command.");
            return;
        }
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByPlayer(target);
        MadokaMagicaWitchTransformationEvent.getInstance().activate(pmdt);
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

