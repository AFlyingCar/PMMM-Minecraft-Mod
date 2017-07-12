package com.MadokaMagica.mod_madokaMagica.commands;

import java.util.List;
import java.util.Arrays;
import java.util.UUID;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import net.minecraftforge.common.DimensionManager;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitchLabrynthEntrance;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory;

public class CommandPMTeleport extends CommandBase {
    private String name;
    private static CommandPMTeleport instance;

    private CommandPMTeleport() {
        this.name = "pmmm-teleport";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics) {
        return true;
    }

    @Override
    public final String getCommandName() {
        return name;
    }

    // /pmmm-teleport <Player name> <dimID> [<x> <y> <z>]
    @Override
    public final String getCommandUsage(ICommandSender sender) {
        String usage = "";
        usage += "/" + name + " <Player name> <dimID> [<x> <y> <z>]";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command) {
        EntityPlayer senderAsPlayer = (EntityPlayer)sender;
        EntityPlayer target = null;

        if(command.length < 2) {
            sendChat(senderAsPlayer, getCommandUsage(sender));
            return;
        }

        target = senderAsPlayer.worldObj.getPlayerEntityByName(command[0]);

        if(target == null){
            sendChat(senderAsPlayer, "Unknown player: " + command[0]);
            return;
        }

        int dimID = Integer.parseInt(command[1]);

        if(!Arrays.asList(DimensionManager.getIDs()).contains(dimID)) {
            sendChat(senderAsPlayer, "Invalid dimension ID " + dimID);
            sendChat(senderAsPlayer, "Valid dimensions are " + Arrays.toString(DimensionManager.getIDs()));
            return;
        }

        int xpos;
        int ypos;
        int zpos;

        if(command.length > 2) {
            if(command.length < 5) {
                sendChat(senderAsPlayer, getCommandUsage(sender));
                return;
            }

            xpos = Integer.parseInt(command[2]);
            ypos = Integer.parseInt(command[3]);
            zpos = Integer.parseInt(command[4]);
        } else {
            xpos = target.playerLocation.posX;
            ypos = target.playerLocation.posY;
            zpos = target.playerLocation.posZ;
        }

        target.travelToDimension(dimID);
        target.setPositionAndUpdate(xpos, ypos, zpos);
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return index == 1;
    }

    public static void sendChat(EntityPlayer player,String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandPMTeleport getInstance(){
        if(CommandPMTeleport.instance == null){
            CommandPMTeleport.instance = new CommandPMTeleport();
        }
        return CommandPMTeleport.instance;
    }
}

