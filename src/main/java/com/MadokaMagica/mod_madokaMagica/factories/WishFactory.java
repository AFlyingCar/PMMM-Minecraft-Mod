package com.MadokaMagica.mod_madokaMagica.factories;

import java.util.ArrayList;
import java.util.regex.*;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;
import com.MadokaMagica.mod_madokaMagica.util.Helper;

public class WishFactory{
	public final static String startPattern = "^(kyuubey|hey kyuubey|incubator|coobie|excuse me kyuubey)*";
	public final static String commandPattern = "*(can (%1$s) have ([0-9]+)|give ([0-9]+) to (%1$s)|give (%1$s) ([0-9]+)|teleport (%1$s) to (%1$s)|(%1$s) has a question)*";
	public final static String parameterPattern = "*()*";

	public static Wish generateWish(EntityPlayer player, String message){
		Wish newWish = null;

		ArrayList<String> parts = generateParts(player, message.toLowerCase());
		if(parts != null){
			newWish = new Wish(parts.get(1),player,message);
		}

		return newWish;
	}

	public static ArrayList<String> generateParts(EntityPlayer player, String message){
		ArrayList<String> parts = new ArrayList<String>();
		Pattern start = Pattern.compile(startPattern);
		Pattern command = Pattern.compile(String.format(commandPattern,buildUsernamePattern(player)));
		//Pattern parameters = Pattern.compile(parameterPattern);
        Pattern targets = Pattern.compile("("+buildUsernamePattern(player)+")");

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
            return null;

		parts.add(start_matches.group());
		parts.add(command_matches.group());
        //parts.add(parameters_matches.group());
        parts.add(targets_matches.group());

		return parts;
	}

    public static String buildUsernamePattern(EntityPlayer player){
        String pattern = "";
        for(String name : Helper.getListOfUsernames(player.worldObj)){
            pattern += name + "|";
        }
        pattern += "me|i";
        return pattern;
    }
}

