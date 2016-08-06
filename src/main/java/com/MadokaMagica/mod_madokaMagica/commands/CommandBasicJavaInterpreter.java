package com.MadokaMagica.mod_madokaMagica.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;
import com.MadokaMagica.mod_madokaMagica.managers.PlayerDataTrackerManager;

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

        String preInstruction = "";
        String instruction=code;
        if(code.indexOf('<') >= 0){
            preInstruction = code.substring(0,code.indexOf('<'));
            instruction = code.substring(code.indexOf('<')+1,code.length());
        }

        Object value=null;
        String instClassName="";
        Class<?> instClass=null;
        String[] called = instruction.split("#");
        if(instruction != ""){
            // Must be at least one class and one method
            if(called.length < 2) return;

            instClassName = called[0];

            instClass = parseClassName(instClassName);
            if(instClass == null){
                sendChat(player,"ERROR: no type found '"+instClassName+"'");
                return;
            }

            value = instClass; // Start the value as the class object

            for(int i=1; i<called.length;i++){
                if(called[i].startsWith("$")){
                    Field field = null;
                    try{
                        field = instClass.getField(called[i].substring(1));
                    }catch(NoSuchFieldException exception){
                        sendChat(player,"ERROR: Type '" + instClass.getName() + "' has no such field '" + called[i].substring(1) + "'");
                        return;
                    }

                    try{
                        value = field.get(value);
                    }catch(NullPointerException exception){
                        sendChat(player,"ERROR: Tried to reference non-static field '" + called[i].substring(1) + "' from non-static context.");
                        return;
                    }catch(IllegalArgumentException exception){
                        sendChat(player,"ERROR: Type '" + instClass.getName() + "' has no such field '" + called[i].substring(1) + "'");
                        return;
                    }catch(IllegalAccessException exception){
                        sendChat(player,"ERROR: Illegal access to field '" + called[i] + "': " + exception.getCause());
                        return;
                    }
                }else{
                    Method method;

                    try{
                        method = instClass.getMethod(called[i]);
                    }catch(NoSuchMethodException exception){
                        sendChat(player,"ERROR: Type '" + instClass.getName() + "' has no such method '" + called[i] + "'");
                        return;
                    }

                    try{
                        value = method.invoke(instClass); // Can only work on Static methods
                    }catch(IllegalAccessException exception){
                        sendChat(player,"ERROR: Illegal access to method '" + called[i] + "': " + exception.getCause());
                        return;
                    }catch(IllegalArgumentException exception){
                        sendChat(player,"ERROR: Illegal argument to method '" + called[i] + "': " + exception.getCause());
                        return;
                    }catch(InvocationTargetException exception){
                        sendChat(player,"ERROR: An error occurred in method '" + called[i] + "'");
                        sendChat(player,exception.toString());
                        return;
                    }
                }

                if(value == null){
                    if(i!=called.length-1){
                        sendChat(player,"ERROR: type 'void' has no method '"+called[i+1]+"'");
                        return;
                    }
                    break; // Sanity check
                }else{
                    instClass = value.getClass();
                }
            }
        }else{
            System.out.println("ERROR: No default instruction provided.");
        }

        Object value2=null;
        String preInstClassName="";
        Class<?> preInstClass=null;
        String[] preCalled = preInstruction.split("#");
        if(preInstruction != ""){
            // TODO: CTRL-C + CTRL-V the Field shit in here
            if(preCalled.length == 0) return;

            if(preInstruction == "print"){
                preInstruction = "java.lang.System#$out#println";
                preCalled = preInstruction.split("#"); // redo just in case
                preInstClassName = "java.lang.System";
            }else{
                preInstClassName = preCalled[0];
            }
            
            preInstClass = parseClassName(preInstClassName);
            if(preInstClass == null){
                sendChat(player,"ERROR: no type found '"+instClassName+"'");
                return;
            }
            
            for(int i=1; i<preCalled.length;i++){
                Method method;
                // Pass the last value to this method if it is the last one
                if(i == preCalled.length-1){
                    try{
                        method = preInstClass.getMethod(preCalled[i],value.getClass());
                    }catch(NoSuchMethodException exception){
                        sendChat(player,"ERROR: type '" + preInstClass.getName() + "' has no such method '" + preCalled[i] + "'");
                        return;
                    }

                    try{
                        value2 = method.invoke(preInstClass,value);
                    }catch(IllegalAccessException exception){
                        sendChat(player,"ERROR: Illegal access to method '" + preCalled[i] + "': " + exception.getCause());
                        return;
                    }catch(IllegalArgumentException exception){
                        sendChat(player,"ERROR: Illegal argument to method '" + preCalled[i] + "': " + exception.getCause());
                        return;
                    }catch(InvocationTargetException exception){
                        sendChat(player,"ERROR: An error occurred in method '" + preCalled[i] + "'");
                        sendChat(player,exception.toString());
                        return;
                    }
                }else{
                    try{
                        method = preInstClass.getMethod(preCalled[i]);
                    }catch(NoSuchMethodException exception){
                        sendChat(player,"ERROR: type '" + preInstClass.getName() + "' has no such method '" + preCalled[i] + "'");
                        return;
                    }

                    try{
                        value2 = method.invoke(preInstClass);
                    }catch(IllegalAccessException exception){
                        sendChat(player,"ERROR: Illegal access to method '" + preCalled[i] + "': " + exception.getCause());
                        return;
                    }catch(IllegalArgumentException exception){
                        sendChat(player,"ERROR: Illegal argument to method '" + preCalled[i] + "': " + exception.getCause());
                        return;
                    }catch(InvocationTargetException exception){
                        sendChat(player,"ERROR: An error occurred in method '" + preCalled[i] + "'");
                        sendChat(player,exception.toString());
                        return;
                    }
                }

                if(value2 == null){
                    if(i!=preCalled.length-1){
                        sendChat(player,"ERROR: type 'void' has no method '"+preCalled[i+1]+"'");
                        return;
                    }
                    break; // Sanity check
                }else{
                    preInstClass = value2.getClass();
                }
            }
            // Print out the last value as output, if it wasn't null
            if(value2 != null){
                System.out.println(value2);
            }
            return;
        }

        if(value != null){
            System.out.println(value);
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
