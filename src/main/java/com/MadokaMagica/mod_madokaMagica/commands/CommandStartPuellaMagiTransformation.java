package com.MadokaMagica.mod_madokaMagica.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;
import net.minecraftforge.common.MinecraftForge;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaPuellaMagiTransformationEvent;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.factories.WishFactory;

public class CommandStartPuellaMagiTransformation extends CommandBase{
	private String name;
	private static CommandStartPuellaMagiTransformation instance;

	public CommandStartPuellaMagiTransformation(){
		this.name = name;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender ics){
		return true;
	}

	@Override
	public final String getCommandName(){
		return name;
	}

	@Override
	public final String getCommandUsage(ICommandSender ics){
		String usage = "";
		usage += "/"+name+" <wish> <user name>";
		return usage;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] command){
		EntityPlayer player = (EntityPlayer)sender;
		EntityPlayer target = null;
		String wishCommand;

		if(command.length < 1){
			sendChat(player,getCommandUsage(sender));
            return;
		}

		wishCommand = command[0];

        if(command.length == 2)
            target = player.worldObj.getPlayerEntityByName(command[1]);
        else
            target = player;

        if(target == null){
            sendChat(getCommandSenderAsPlayer(sender),"Unknown player " + command[0]);
            return;
        }
        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByUUID(target.getPersistentID());
        if(pmdt != null)
            for(String s : pmdt.toString().split("\n"))
                sendChat(target,s);
        else
            sendChat(player,"Error: the target does not have a data tracker!");

        Wish wish = WishFactory.generateWish(target,wishCommand);

        if(wish == null){
        	sendChat(getCommandSenderAsPlayer(sender),"An error occurred when generating a Wish object. Object is null!");
        	return;
        }

        MinecraftForge.EVENT_BUS.post(new MadokaMagicaPuellaMagiTransformationEvent(pmdt,wish));
	}

	@Override
	public boolean isUsernameIndex(String[] str, int index){
		return index == 1;
	}

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

	public static CommandStartPuellaMagiTransformation getInstance(){
		if(instance == null)
			instance = new CommandStartPuellaMagiTransformation();
		return instance;
	}
}
