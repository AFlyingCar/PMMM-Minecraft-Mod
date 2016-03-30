package com.MadokaMagica.mod_madokaMagica.util;

import java.util.HashMap;
import java.util.ArrayList;

import net.minecraft.command.ICommandSender;

public class Wish{
    private String command;
    private String message;
    private ICommandSender sender;
    private HashMap<String,ArrayList<String>> wishParts;
	public Wish(String command, ICommandSender sender,String message,HashMap<String,ArrayList<String>> wishParts){
        this.wishParts = wishParts;
        this.command = command;
        this.message = message;
        this.sender = sender;
	}
    public Wish(){
    }

    public String getCommand(){
        return command;
    }
    public String getMessage(){
        return message;
    }
    public ICommandSender getSender(){
        return sender;
    }
    public HashMap<String,ArrayList<String>> getParts(){
        return wishParts;
    }
    public boolean isQuestion(){
        return message.endsWith("?");
    }
}
