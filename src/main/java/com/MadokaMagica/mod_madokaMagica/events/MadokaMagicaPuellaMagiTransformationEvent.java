package com.MadokaMagica.mod_madokaMagica.events;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaEvent;

public class MadokaMagicaPuellaMagiTransformationEvent extends MadokaMagicaEvent{
    private static MadokaMagicaPuellaMagiTransformationEvent instance;

    public static MadokaMagicaPuellaMagiTransformationEvent getInstance(){
        if(MadokaMagicaPuellaMagiTransformationEvent.instance == null) 
            MadokaMagicaPuellaMagiTransformationEvent.instance = new MadokaMagicaPuellaMagiTransformationEvent();
        return MadokaMagicaPuellaMagiTransformationEvent.instance;
    }
}

