package com.MadokaMagica.mod_madokaMagica.commands;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

public class CommandPlayerData extends CommandBase {
    private String name;
    private static CommandPlayerData instance;

    private CommandPlayerData(){
        this.name = "pmmm-player-data";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender){
        // TODO: Somehow check if the sender is an admin/CommandBlock before returning true
        return true;
    }

    @Override
    public final String getCommandName(){
        return name;
    }

    // /pmmm-player-data <user name> <get|set> <var> | <value>
    @Override
    public final String getCommandUsage(ICommandSender sender){
        String usage = "";
        usage += "/" + name + " ";
        usage += "<user name> <get|set> <var> | <value>";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        EntityPlayer senderAsPlayer = (EntityPlayer)sender;
        EntityPlayer target = null;

        if(command.length < 3){
            sendChat(senderAsPlayer,getCommandUsage(sender));
            return;
        }

        target = senderAsPlayer.worldObj.getPlayerEntityByName(command[0]);

        if(target == null){
            sendChat(senderAsPlayer,"Unknown player: "+command[0]);
            return;
        }

        PMDataTracker pmdt = PlayerDataTrackerManager.getInstance().getTrackerByUUID(senderAsPlayer.worldObj.getPlayerEntityByName(command[0]).getPersistentID());

        if(pmdt == null){
            sendChat(senderAsPlayer,"Error: The target does not have a data tracker!");
            return;
        }

        if(command[1].equals("get")){
            sendChat(senderAsPlayer,getValue(pmdt,command[2]));
            return;
        }else if(command[1].equals("set")){
            if(command.length < 4){
                sendChat(senderAsPlayer,getCommandUsage(sender));
                return;
            }

            setValue(pmdt,command[2],command[3]);
        }else{
            sendChat(senderAsPlayer,getCommandUsage(sender));
            return;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] str, int index){
        return index == 1;
    }

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandPlayerData getInstance(){
        if(instance == null)
            instance = new CommandPlayerData();
        return instance;
    }

    private String getValue(PMDataTracker tracker, String nvar){
        String var = nvar.toLowerCase();
        if(var.equals("potential"))          return (""+tracker.getPotential());
        else if(var.equals("corruption"))    return (""+tracker.getCorruption());
        else if(var.equals("architect"))     return (""+tracker.getArchitectScore());
        else if(var.equals("engineering"))   return (""+tracker.getEngineeringScore());
        else if(var.equals("greed"))         return (""+tracker.getGreedScore());
        else if(var.equals("water"))         return (""+tracker.getWaterScore());
        else if(var.equals("nature"))        return (""+tracker.getNatureScore());
        else if(var.equals("day"))           return (""+tracker.getDayScore());
        else if(var.equals("night"))         return (""+tracker.getNightScore());
        else if(var.equals("hero"))          return (""+tracker.getHeroScore());
        else if(var.equals("villain"))       return (""+tracker.getVillainScore());
        else if(var.equals("passive"))       return (""+tracker.getPassiveScore());
        else if(var.equals("aggressive"))    return (""+tracker.getAggressiveScore());
        else                                 return "Invalid var: " + nvar;
    }

    private void setValue(PMDataTracker tracker, String nvar, String nvalue){
        String var = nvar.toLowerCase();
        if(var.equals("potential"))          tracker.setPotential(Float.parseFloat(nvalue));
        else if(var.equals("corruption"))    tracker.setCorruption(Float.parseFloat(nvalue));
        else if(var.equals("architect"))     tracker.setArchitectScore(Float.parseFloat(nvalue));
        else if(var.equals("engineering"))   tracker.setEngineeringScore(Float.parseFloat(nvalue));
        else if(var.equals("greed"))         tracker.setGreedScore(Float.parseFloat(nvalue));
        else if(var.equals("water"))         tracker.setWaterScore(Float.parseFloat(nvalue));
        else if(var.equals("nature"))        tracker.setNatureScore(Float.parseFloat(nvalue));
        else if(var.equals("day"))           tracker.setDayScore(Float.parseFloat(nvalue));
        else if(var.equals("night"))         tracker.setNightScore(Float.parseFloat(nvalue));
        else if(var.equals("hero"))          tracker.setHeroScore(Float.parseFloat(nvalue));
        else if(var.equals("villain"))       tracker.setVillainScore(Float.parseFloat(nvalue));
        else if(var.equals("passive"))       tracker.setPassiveScore(Float.parseFloat(nvalue));
        else if(var.equals("aggressive"))    tracker.setAggressiveScore(Float.parseFloat(nvalue));
        else                                 System.out.println("Invalid var: " + nvar);
    }
}
