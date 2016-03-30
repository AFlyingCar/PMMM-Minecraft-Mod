package com.MadokaMagica.mod_madokaMagica.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class WishFactory{
	public final static String startPattern = "^(kyuubey|hey kyuubey|incubator|coobie|excuse me kyuubey)+( |\t|,|\\.|\\?)*";
	public final static String commandPattern = "(give)( |\t)+";
	public final static String parameterPattern = "(1%s|[0-9]+ of id [0-9]+|[0-9]+)( |\t|,|\\.|\\?)*";

	public static Wish generateWish(EntityPlayer player, String message){
		return generateWish(player,message,false);
	}

	public static Wish generateWish(EntityPlayer player, String message, boolean test){
		Wish newWish = null;

        // ArrayList<String> parts = generateParts(player, message.toLowerCase(),test);
		HashMap<String,ArrayList<String>> parts = generatePartsHM(player, message.toLowerCase(),test);
		if(parts == null) return newWish;

        newWish = new Wish("",player,message,parts);

		return newWish;
	}

	public static ArrayList<String> generateParts(EntityPlayer player, String message){
		return generateParts(player,message,false);
	}

    public static HashMap<String,ArrayList<String>> generatePartsHM(EntityPlayer player,String message, boolean test){
        HashMap<String,ArrayList<String>> parts = new HashMap<String,ArrayList<String>>();

        parts.put("START",new ArrayList<String>());
        parts.put("COMM",new ArrayList<String>());
        parts.put("PARAM",new ArrayList<String>());

        Pattern start;
        Pattern command;
        Pattern params;
        Pattern targets;

        Matcher s_matches;
        Matcher c_matches;
        Matcher p_matches;
        Matcher t_matches;

        start = Pattern.compile(startPattern);
        s_matches = start.matcher(message);

        // Return null if the player wasn't addressing the incubator
        if(!s_matches.matches() && !test) return null;

        while(s_matches.find()){
            parts.get("START").add(s_matches.group(1));
        }

        command = Pattern.compile(commandPattern);
        c_matches = command.matcher(message);

        if(!c_matches.matches() && !test) return null;

        while(c_matches.find()){
            parts.get("COMM").add(c_matches.group(1));
        }

        params = Pattern.compile(String.format(parameterPattern,buildUsernamePattern()));
        p_matches = params.matcher(message);

        // Don't return null here, because some commands may not have parameters
        // if(p_matches.matches()){
            while(p_matches.find()){
                parts.get("PARAM").add(p_matches.group(1));
            }
        // }

        return parts;
    }

	public static ArrayList<String> generateParts(EntityPlayer player, String message, boolean test){
		ArrayList<String> parts = new ArrayList<String>();
		Pattern start = Pattern.compile(startPattern);
		Pattern command = Pattern.compile(String.format(commandPattern,buildUsernamePattern()));
		Pattern parameters = Pattern.compile(parameterPattern);
        Pattern targets = Pattern.compile("("+buildUsernamePattern()+")");

        // We check how it starts to see if message is even directed at the Incubator
		Matcher start_matches = start.matcher(message);
		Matcher command_matches = command.matcher(message);
		Matcher parameters_matches = parameters.matcher(message);
        Matcher targets_matches = targets.matcher(message);

		// If any of them failed to match a string, do nothing.
        if(!(
                start_matches.matches()
                &&command_matches.matches()
                &&parameters_matches.matches()
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
        pattern += "me";
        return pattern;
    }
}

