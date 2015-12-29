package com.MadokaMagica.mod_madokaMagica.events;

import java.util.Map;
import java.util.HashMap;

public abstract class MadokaMagicaEvent{
    // private static MadokaMagicaEvent instance = null;
    // protected static Map<String,MadokaMagicaEvent> instances = null;
    private boolean active;

    public MadokaMagicaEvent(){
        // instances = new HashMap<String,MadokaMagicaEvent>();
        active = false;
    }

    public boolean isActive(){
        return active;
    }

    public void cancel(){
        active = false;
    }

    public void activate(){
        active = true;
    }
}
