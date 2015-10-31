package com.example.simcity;

public abstract class Location {
	public int id;
	public String name;
	
	public static Location LocOf(String name){
		if(name == null)
			return null;
		for(Section s : TrafficMap.sections)
			if(s.name.equals(name))
				return s;
		for(Building b : TrafficMap.buildings)
			if(b.name.equals(name))
				return b;
		return null;
	}
}
