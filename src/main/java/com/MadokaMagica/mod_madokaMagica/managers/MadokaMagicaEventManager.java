package com.MadokaMagica.mod_madokaMagica.managers;

import java.util.ArrayList;

import com.MadokaMagica.mod_madokaMagica.events.MadokaMagicaEvent;

public class MadokaMagicaEventManager{
	private ArrayList<MadokaMagicaEvent> events;
	private ArrayList<MadokaMagicaEvent> active_events;

	private static MadokaMagicaEventManager instance;

	private MadokaMagicaEventManager(){
		events = new ArrayList<MadokaMagicaEvent>();
		active_events = new ArrayList<MadokaMagicaEvent>();
	}

	public void register(MadokaMagicaEvent event){
		events.add(event);
	}

	public void unregister(MadokaMagicaEvent event){
		events.remove(event);
	}

	// public void startEvent(MadokaMagicaEvent event){
	// 	// events.get(events.indexOf(event)).activate();
	// 	active_events.add(event);
	// 	event.activate();
	// }

	public void cancelEvent(MadokaMagicaEvent event){
		events.get(events.indexOf(event)).cancel();
	}

	public void manage(){
		for(MadokaMagicaEvent e : events)
			if(e.isActive())
				active_events.add(e);
			else
				active_events.remove(events);
	}

	public boolean isEventActive(MadokaMagicaEvent mmevent){
		// return active_events.contains(eventType.getInstance());
		for(MadokaMagicaEvent event : active_events)
			// if(event.getClass().equals(eventType))
			if(event == mmevent)
				return true;
		return false;
	}

	public ArrayList<MadokaMagicaEvent> getRegisteredEvents(){
		return events;
	}

	public ArrayList<MadokaMagicaEvent> getActiveEvents(){
		return active_events;
	}

	public static MadokaMagicaEventManager getInstance(){
		if(MadokaMagicaEventManager.instance == null)
			MadokaMagicaEventManager.instance = new MadokaMagicaEventManager();
		return MadokaMagicaEventManager.instance;
	}
}
