package com.MadokaMagica.mod_madokaMagica.factories;

import java.util.ArrayList;
import java.util.regex.*;

import net.minecraft.entity.player.EntityPlayer;

import com.MadokaMagica.mod_madokaMagica.util.Wish;

public class WishFactory{
	public final static String startPattern = "^(kyuubey|hey kyuubey|incubator|coobie|excuse me kyuubey)*";
	public final static String commandPattern = "";
	public final static String parameterPattern = "";

	public static Wish generateWish(EntityPlayer player, String message){
		Wish newWish = null;

		ArrayList<String> parts = generateParts(player, message);
		if(parts != null){
			newWish = new Wish(parts.get(0),parts.get(1),parts.get(2));
		}

		return newWish;
	}

	public static ArrayList<String> generateParts(EntityPlayer player, String message){
		ArrayList<String> parts = new ArrayList<String>();
		Pattern start = Pattern.compile(startPattern);
		Pattern command = Pattern.compile(commandPattern);
		Pattern parameters = Pattern.compile(parameterPattern);

		Matcher start_matches = start.matcher(message);
		Matcher command_matches = command.matcher(message);
		Matcher parameters_matches = parameters.matcher(message);

		// If any of them failed to match a string, do nothing.
		if(!(start_matches.matches()&&command_matches.matches()&&parameters_matches.matches())) return null;

		parts.add(start_matches.group());
		parts.add(command_matches.group());
		parts.add(parameters_matches.group());

		return parts;
	}
}

