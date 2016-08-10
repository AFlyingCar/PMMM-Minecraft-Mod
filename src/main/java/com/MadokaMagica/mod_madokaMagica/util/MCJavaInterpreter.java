package com.MadokaMagica.mod_madokaMagica.util;

import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;

import com.MadokaMagica.mod_madokaMagica.util.BasicJavaInterpreter;

public class MCJavaInterpreter extends BasicJavaInterpreter{
    private static MCJavaInterpreter instance = null;
    private MCJavaInterpreter(){}

    protected void output(Object value){
        // Not sure if this will work on multiplayer...
        // Maybe we should check?
        ChatComponentText text = new ChatComponentText(value.toString());
        MinecraftServer.getServer().addChatMessage(text);
    }

    public static MCJavaInterpreter getInstance(){
        if(instance == null){
            instance = new MCJavaInterpreter();
        }
        return instance;
    }
}

