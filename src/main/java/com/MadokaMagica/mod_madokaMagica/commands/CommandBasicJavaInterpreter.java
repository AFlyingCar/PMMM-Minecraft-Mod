package com.MadokaMagica.mod_madokaMagica.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;
import com.MadokaMagica.mod_madokaMagica.util.BasicJavaInterpreter;
import com.MadokaMagica.mod_madokaMagica.util.MCJavaInterpreter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandBase;

/**********************************************************************************\
 *                              WARNING WARNING WARNING!                          *
 * DO NOT ALLOW THIS CLASS TO BE RELEASED WITH THE MOD                            *
 * IT IS ONLY FOR ME TO HAVE A NICER TIME DEBUGGING, BUT IS EXTREMELY DANGEROUS   *
 * WHATEVER HAPPENS, DO NOT LET THIS ESCAPE INTO THE WILD                         *
\**********************************************************************************/

// I have barely any idea how this damn class works, so if it doesn't work the first time, there is no way I'm going to maintain it
// If it does work though, then it will be so cool

// A class which allows basic execution of java code
public class CommandBasicJavaInterpreter extends CommandBase {
    private String name;
    private static CommandBasicJavaInterpreter instance;

    private CommandBasicJavaInterpreter(){
        this.name = "pmmm-java-intpret";
    }

    public static void test(){
        System.out.println("TEST");
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
        usage += "<code>";
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command){
        EntityPlayer player = (EntityPlayer)sender;

        // print<ClassName#Method#Method...
        // ClassName#Method#Method...
        // ClassName#Method<ClassName#Method#Method...
        // ClassName#$Field#Method#$Field#$Field...

        if(command.length < 1){
            sendChat(player,"Invalid parameters.");
        }

        String code="";
        for(int i=0;i<command.length;i++){
            code+=command[i];
        }
        code = code.trim(); // Removed unwanted whitespace

        // execute the command and print the result (if there is one)
        MCJavaInterpreter mcji = MCJavaInterpreter.getInstance();
        Object mcjiret = mcji.parseCommand(code);
        if(mcjiret != null){
            sendChat(player,"Output: " + mcjiret.toString());
        }else{
            sendChat(player,"Output: ");
        }
    }

    protected Class<?> parseClassName(String name){
        try{
            return Class.forName(name);
        }catch(ClassNotFoundException exception){
            return null;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] str, int index){
        return false;
    }

    public static void sendChat(EntityPlayer player, String message){
        ChatComponentText text = new ChatComponentText(message);
        player.addChatMessage(text);
    }

    public static CommandBasicJavaInterpreter getInstance(){
        if(instance == null)
            instance = new CommandBasicJavaInterpreter();
        return instance;
    }
}

