package com.MadokaMagica.mod_madokaMagica.util;

import net.minecraft.command.ICommandSender;

public class Wish{
    private String command;
    private String message;
    private ICommandSender sender;
	public Wish(String command, ICommandSender sender,String message){
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
    public boolean isQuestion(){
        return message.endsWith("?");
    }
}
