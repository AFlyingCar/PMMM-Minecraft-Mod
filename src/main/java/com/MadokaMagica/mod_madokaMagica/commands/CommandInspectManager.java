package com.MadokaMagica.mod_madokaMagica.commands;

import java.util.UUID;
import java.util.Map;
import java.util.Map.Entry;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandInspectManager extends CommandBase{
    private String name;
    private static CommandInspectManager instance;

    private CommandInspectManager(){
        this.name = "pmmm-inspect";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics){
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-inspect <pdtm|lm|im|isgm> [--deep]
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/"+name+" "+"<pdtm|lm|im|isgm> [--deep]";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        EntityPlayer player = (EntityPlayer)sender;//(EntityPlayer)(CommandBase.getCommandSenderAsPlayer(sender));
        boolean deep = false;

        if(command.length < 1){
            sendChat(player,"Invalid command length");
        }

        if(command.length >= 2){
            if(command[1].equals("--deep")){
                deep = true;
            }
        }

        if(command[0].equals("pdtm")){
            inspectPDTM(player,deep);
        }else if(command[0].equals("lm")){
            inspectLM(player,deep);
        }else if(command[0].equals("im")){
            inspectIM(player,deep);
        }else if(command[0].equals("isgm")){
            inspectISGM(player,deep);
        }else{
            sendChat(player,"Invalid parameter: " + command[0]);
        }
    }

    // Inspect PlayerDataTrackerManager
    protected void inspectPDTM(EntityPlayer player,boolean deep){
        PlayerDataTrackerManager pdtm = PlayerDataTrackerManager.getInstance();
        sendChat(player,"hasLoaded:"+pdtm.havePMDataTrackersBeenLoaded());

        Map<UUID,PMDataTracker> tmap = pdtm.getTrackers();

        sendChat(player,"# of Trackers available:"+tmap.entrySet().size());

        int i=0;
        for(Entry<UUID,PMDataTracker> trackerset : tmap.entrySet()){
            // Because PMDataTracker overrides toString, we have to redo what Object.toString does (not that hard in actuality, since it doesn't overide those method either)
            sendChat(player,"Tracker <" + trackerset.getKey() + ":" + trackerset.getValue().getClass().getName() + "@" + Integer.toHexString(trackerset.getValue().hashCode()));

            if(deep){
                String[] lines = trackerset.getValue().toString().split("\n");
                for(int j=1; j<lines.length;j++){
                    sendChat(player,"  " + lines[j]);
                }
            }

            // Don't print everything, as that would really badly fill up the chat in certain cases
            i++;
            if(i>5){
                sendChat(player,"...");
                return;
            }
        }
    }

    // Inspect LabrynthManager
    protected void inspectLM(EntityPlayer player,boolean deep){
        sendChat(player,"Not implemented.");
    }

    // Inspect IncubatorManager
    protected void inspectIM(EntityPlayer player,boolean deep){
        sendChat(player,"Not implemented.");
    }

    // Inspect ItemSoulGemManager
    protected void inspectISGM(EntityPlayer player,boolean deep){
        sendChat(player,"Not implemented.");
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return false;
    }

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandInspectManager getInstance(){
        if(CommandInspectManager.instance == null)
            CommandInspectManager.instance = new CommandInspectManager();
        return CommandInspectManager.instance;
    }
}

