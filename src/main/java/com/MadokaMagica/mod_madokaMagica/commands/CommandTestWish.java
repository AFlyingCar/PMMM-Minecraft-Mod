package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

import com.MadokaMagica.mod_madokaMagica.factories.WishFactory;
import com.MadokaMagica.mod_madokaMagica.util.Wish;

public class CommandTestWish extends CommandBase {
	private String name;
	private static CommandTestWish instance;

	public CommandTestWish(){
		this.name = "pmmm-test-wish";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender ics){
		return true;
	}

	@Override
	public String getCommandName(){
		return name;
	}

	@Override
	public final String getCommandUsage(ICommandSender ics){
		String usage = "";
		usage += "/"+name+" <wish>";
		return usage;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] command){
		EntityPlayer player = (EntityPlayer)sender;

		if(command.length == 0){
			sendChat(player,getCommandUsage(sender));
			return;
		}

		String fullCommand = "";
		for(int i=0; i < command.length; i++)
			fullCommand += command[i];

		Wish test = WishFactory.generateWish(player,fullCommand,true);
		if(test == null){
			sendChat(player,"Wish failed. Most likely one or more search requirements were missing.");
			return;
		}

		sendChat(player,"Command: " + test.getCommand());
		sendChat(player,"Message: " + test.getMessage());
		sendChat(player,"Is Question: " + test.isQuestion());
	}

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandTestWish getInstance(){
        if(instance == null)
            instance = new CommandTestWish();
        return instance;
    }
}
