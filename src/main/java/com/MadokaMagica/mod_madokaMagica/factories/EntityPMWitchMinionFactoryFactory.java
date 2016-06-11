package com.MadokaMagica.mod_madokaMagica.factories;

import com.MadokaMagica.mod_madokaMagica.factories.EntityPMWitchMinionFactory;
import com.MadokaMagica.mod_madokaMagica.entities.EntityPMWitch;
import com.MadokaMagica.mod_madokaMagica.trackers.PMDataTracker;

public class EntityPMWitchMinionFactoryFactory {
    public static EntityPMWitchMinionFactory createMinionFactory(EntityPMWitch witch){
        EntityPMWitchMinionFactory epmwmf = new EntityPMWitchMinionFactory();
        epmwmf.witch = witch;
        epmwmf.home = witch.entrance;
        epmwmf.aggressiveLevel = getRandomAggressiveLevel(witch);
        epmwmf.tracker = witch.tracker;

        return epmwmf;
    }

    /*
     * Returns the number for the aggression score
     * Determines this based on the highest score or a random number
     * Scores:
     *   0  -  Highest possible aggression value. Acts similar to a zombie, seeking out villagers/victims to bring back to the entrance of their witch's labrynth
     *   1  -  Second highest aggression value. Guard the entrance to the labrynth, and chase intruders a short distance
     *   2  -  Third highest aggression value. Guard the witch itself. Acts completely passive until provoked or the witch is attacked.
     *   3  -  Fourth and lowest aggression value. Acts completely passively. Doesn't react if attacked or its witch is attacked.
     *   -1 -  An error has occurred.
     */
    public static int getRandomAggressiveLevel(EntityPMWitch witch){
        PMDataTracker tracker = witch.tracker;
        String highestScore = tracker.getHighestScoreIden();
        // String secondHighestScore = tracker.getSecondHighestScoreIden();
        if(highestScore.equals("AGGRESSIVE"))
            return 0; // Most aggressive
        if(highestScore.equals("PASSIVE"))
            return 3; // Most passive
        /*
        if(secondHighestScore.equals("PASSIVE"))
            return 2; // second most passive
        */
        return 1;
    }
}
