package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandClearPMDataTrackerData extends CommandBase{
    private String name;
    private static CommandClearPMDataTrackerData instance;

    private CommandClearPMDataTrackerData(){
        this.name = "pmmm-clear-pmdt-data";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-display-info <user name>
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name;
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        PlayerDataTrackerManager.getInstance().unloadAllData();
    }

    public static CommandClearPMDataTrackerData getInstance(){
        if(CommandClearPMDataTrackerData.instance == null)
            CommandClearPMDataTrackerData.instance = new CommandClearPMDataTrackerData();
        return CommandClearPMDataTrackerData.instance;
    }
}
 
