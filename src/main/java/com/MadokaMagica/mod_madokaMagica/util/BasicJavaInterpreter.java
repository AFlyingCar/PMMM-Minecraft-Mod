package com.MadokaMagica.mod_madokaMagica.util;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

public class BasicJavaInterpreter {
    private static BasicJavaInterpreter instance;

    protected BasicJavaInterpreter(){}

    public Object parseCommand(String command){
        String[] chain = command.split("<");
        String curr;
        Object currVal=null;
        Object prevVal=null;

        System.out.println(chain.length);

        // Go backwards since we pass stuff to the command written before it, so we need to make sure we parse the passed data first
        for(int i=chain.length-1; i>=0; i--){
            curr = chain[i];
            prevVal = parseAndExecute(curr,prevVal);
        }
        return prevVal;
    }

    /*
     * com = the command to execute
     * val = the value to pass to the last method called in comm
     */
    public Object parseAndExecute(String com, Object val){
        Class<?> instClass = null;
        String[] called;
        Object value = null;

        // If the passed command is null, then nothing will actually happen
        if(com != null){
            called = com.split("#");
            if(called.length < 2){
                called[0] = called[0].trim();

                if(isCommandNum(called[0])){
                    System.out.println("Found integer!");
                    value = Integer.valueOf(called[0]);
                }else if(isCommandString(called[0])){
                    System.out.println("Found String!");
                    if(called[0].endsWith("\"")){
                        called[0] = called[0].substring(0,called[0].length()-1);
                    }
                    value=called[0].substring(1,called[0].length());
                }else{
                    output("ERROR: Value is not a complete statement or a valid base-type.");
                }
                return value;
            }

            // If it is at least 2 elements, then we know that the first MUST be a class
            instClass = parseClassName(called[0]);
            if(instClass == null){
                output("ERROR: No type found '" + called[0] + "'");
                return value;
            }

            value = instClass;
            for(int i=1; i<called.length;i++){
                if(called[i].startsWith("$")){
                    try{
                        value = parseAndReturnField(instClass,called[i].substring(1),value);
                    }catch(RuntimeException exception){
                        output("ERROR: " + exception.getMessage());
                        return null;
                    }
                }else{
                    try{
                        value = parseAndReturnMethod(instClass,called[i],value,val,i==called.length-1);
                    }catch(RuntimeException exception){
                        output("ERROR: " + exception.getMessage());
                        return null;
                    }
                }
                if(value == null){
                    if(i!=called.length-1){
                        output("ERROR: type 'void' has no method '"+called[i+1]+"'");
                        return null;
                    }
                    break; // Sanity check
                }else{
                    instClass = value.getClass();
                }
            }
        }
        return value;
    }

    /*
     * klass = the class which holds the method
     * name = Name of method
     * calledFrom = the object from which the method is being called from
     * param = the parameters passed to the method
     * doPassParam = whether or not to worry about the parameter (if this is fault, then param is ignored
     */
    public Object parseAndReturnMethod(Class<?> klass, String name, Object calledFrom, Object param,boolean doPassParam) throws RuntimeException{
        if(klass == null){
            throw new RuntimeException("Passed Class is null!");
        }else if(name == null){
            throw new RuntimeException("Passed name is null!");
        }else if(calledFrom == null){
            throw new RuntimeException("Passed called from object is null!");
        }else if(param == null && doPassParam){
            throw new RuntimeException("Passed parameters is null!");
        }

        Method method;
        try{
            if(doPassParam){
                // Make sure that we are getting the method which has a string type (if one exists)
                // This means that we can't just pass values around willy-nilly, but this is mostly just supposed to be used to print values on the fly anyways
                method = klass.getMethod(name,String.class);
            }else{
                method = klass.getMethod(name);
            }
        }catch(NoSuchMethodException exception){
            throw new RuntimeException("NoSuchMethod: Type " + klass.getName() + " has no such method " + name + "(" + (doPassParam ? param.getClass().getName():"") + ")");
        }

        try{
            if(doPassParam){
                return method.invoke(calledFrom,param.toString());
            }else{
                return method.invoke(calledFrom);
            }
        }catch(IllegalAccessException exception){
            throw new RuntimeException("IllegalAccess: Method " + name + " has " + getModifierAsString(method.getModifiers()) + " access in class " + klass.getName());
        }catch(IllegalArgumentException exception){
            throw new RuntimeException("IllegalArgument: Illegal argument to method '" + name + "': " + exception.getMessage(),exception);
        }catch(InvocationTargetException exception){
            throw new RuntimeException("InvocationTarget: An error occurred when executing method '" + name + "': " + exception.getMessage(),exception.getCause());
        }
    }

    /*
     * klass = the class which holds the field
     * name = the name of the field
     * accessedFrom = the object from which the field is being accessed from
     */
    public Object parseAndReturnField(Class<?> klass, String name, Object accessedFrom) throws RuntimeException{
        Field field = null;
        try{
            field = klass.getField(name);
        }catch(NoSuchFieldException exception){
            throw new RuntimeException("NoSuchField: Type " + klass.getName() + " has no such field " + name);
        }

        try{
            return field.get(accessedFrom);
        }catch(NullPointerException exception){
            // Basically, this should only happen if we try to do as the error below specifies
            // If I find it occurrs other times, then I'll take another look at it
            throw new RuntimeException("NullPointer: Tried to reference non-static field '" + name + "' from a static context");
        }catch(IllegalArgumentException exception){
            // This should never happen
            throw new RuntimeException("IllegalArgument: Type " + klass.getName() + " has no such field " + name);
        }catch(IllegalAccessException exception){
            throw new RuntimeException("IllegalAccess: Field " + name + " has " + getModifierAsString(field.getModifiers()) + "access in class " + klass.getName());
        }
    }

    // Returns a Class<?> object of name
    // Returns null if a class named name does not exist
    public Class<?> parseClassName(String name){
        try{
            return Class.forName(name);
        }catch(ClassNotFoundException exception){
            return null;
        }
    }

    public String getModifierAsString(int modifierType){
        switch(modifierType){
            case Modifier.PUBLIC:
                return "public";
            case Modifier.PRIVATE:
                return "private";
            case Modifier.PROTECTED:
                return "protected";
            default:
                // This should never happen
                return "unknown";
        }
    }

    public boolean isCommandNum(String command){
        final String NUM_BASE = "1234567890.";
        for(int i=0;i<command.length();i++){
            if(NUM_BASE.indexOf(command.charAt(i)) == -1){
                return false;
            }
        }
        return true;
    }
    public boolean isCommandString(String command){
        return command.startsWith("\"");
    }

    protected void output(Object value){
        System.out.println(value);
    }

    public static BasicJavaInterpreter getInstance(){
        if(instance == null){
            instance = new BasicJavaInterpreter();
        }
        return instance;
    }
}

