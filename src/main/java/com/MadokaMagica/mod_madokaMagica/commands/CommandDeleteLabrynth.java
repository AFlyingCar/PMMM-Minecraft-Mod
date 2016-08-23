package com.MadokaMagica.mod_madokaMagica.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

import com.MadokaMagica.mod_madokaMagica.util.Helper;
import com.MadokaMagica.mod_madokaMagica.managers.LabrynthManager;
import com.MadokaMagica.mod_madokaMagica.world.LabrynthWorldServer;
import com.MadokaMagica.mod_madokaMagica.factories.LabrynthFactory.LabrynthDetails;

public class CommandDeleteLabrynth extends CommandBase {
    private String name;
    private static CommandDeleteLabrynth instance;

    private CommandDeleteLabrynth(){
        this.name = "pmmm-delete-labrynth";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender ics){
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-delete-labrynth <id>
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/" + name + "<id>";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        int id;
        EntityPlayer sap = (EntityPlayer)sender;

        if(command.length < 1){
            sendChat(sap,"Invalid number of arguments.");
            return;
        }

        try{
            id = Integer.valueOf(command[0]).intValue();
        }catch(NumberFormatException e){
            sendChat(sap,"Error: " + command[0] + " is not a valid integer.");
            return;
        }

        /*
        WorldServer world = Helper.requestDimensionWorld(id);
        if(world == null){
            sendChat(sap,"Error: Dimension " + id + " is not registered.");
            return;
        }

        if(!(world instanceof LabrynthWorldServer)){
            sendChat(sap,"Error: Dimension " + id + " is not a valid Labrynth");
            return;
        }
        
        for(Object obj : world.loadedEntityList){
            if(obj instanceof Entity){
                Entity e = (Entity)obj;
                // Move all entities out of the dimension before killing it
                Helper.transportEntity(e,e.posX,e.posY,e.posZ,0); 
            }
        }
        */

        // Delete it
        if(LabrynthManager.getInstance().deleteLabrynth(id,true))
            sendChat(sap,"Successfully deleted Dimension " + id + ".");
    }

    @Override
    public boolean isUsernameIndex(String[] str,int index){
        return false;
    }

    public static void sendChat(EntityPlayer player,String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandDeleteLabrynth getInstance(){
        if(CommandDeleteLabrynth.instance == null){
            CommandDeleteLabrynth.instance = new CommandDeleteLabrynth();
        }
        return CommandDeleteLabrynth.instance;
    }
}

