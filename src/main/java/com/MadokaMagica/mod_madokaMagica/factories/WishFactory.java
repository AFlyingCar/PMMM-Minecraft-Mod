package com.MadokaMagica.mod_madokaMagica.factories;

import java.util.ArrayList;
import java.util.regex.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class WishFactory{
	public final static String startPattern = "^(kyuubey|hey kyuubey|incubator|coobie|excuse me kyuubey).";
	public final static String commandPattern = ".+(can (%1$s) have ([0-9]+)|give ([0-9]+) to (%1$s)|give (%1$s) ([0-9]+)|teleport (%1$s) to (%1$s)|(%1$s) has a question).";
	public final static String parameterPattern = "*()*";

	public static Wish generateWish(EntityPlayer player, String message){
		return generateWish(player,message,false);
	}

	public static Wish generateWish(EntityPlayer player, String message, boolean test){
		Wish newWish = null;

		ArrayList<String> parts = generateParts(player, message.toLowerCase(),test);
		if(parts == null) return newWish;

        newWish = new Wish(parts.get(1),player,message);

		return newWish;
	}

	public static ArrayList<String> generateParts(EntityPlayer player, String message){
		return generateParts(player,message,false);
	}

	public static ArrayList<String> generateParts(EntityPlayer player, String message, boolean test){
		ArrayList<String> parts = new ArrayList<String>();
		Pattern start = Pattern.compile(startPattern);
		Pattern command = Pattern.compile(String.format(commandPattern,buildUsernamePattern()));
		//Pattern parameters = Pattern.compile(parameterPattern);
        Pattern targets = Pattern.compile("("+buildUsernamePattern()+")");

        // We check how it starts to see if message is even directed at the Incubator
		Matcher start_matches = start.matcher(message);
		Matcher command_matches = command.matcher(message);
		//Matcher parameters_matches = parameters.matcher(message);
        Matcher targets_matches = targets.matcher(message);

		// If any of them failed to match a string, do nothing.
        if(!(
                start_matches.matches()
                &&command_matches.matches()
                //&&parameters_matches.matches()
                &&targets_matches.matches()
            ))
        {
        	if(test){
        		for(int i=0;i<3;i++) parts.add("");
        		parts.set(0,start_matches.group());
        		parts.set(1,command_matches.group());
        		parts.set(2,targets_matches.group());
        		return parts;
        	}else
        		return null;
        }

		parts.add(start_matches.group());
		parts.add(command_matches.group());
        //parts.add(parameters_matches.group());
        parts.add(targets_matches.group());

		return parts;
	}

    public static String buildUsernamePattern(){
        String pattern = "";
        for(String name : MinecraftServer.getServer().getAllUsernames()){
            pattern += name + "|";
        }
        pattern += "me|i";
        return pattern;
    }
}

