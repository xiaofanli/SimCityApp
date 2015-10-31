package com.example.simcity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import nju.ics.lixiaofan.view.CrossingView;
import nju.ics.lixiaofan.view.StreetView;

public class Section extends Location{
	public int[] dir = {-1, -1};	//dir[1] only for crossings
	public Queue<Car> cars = new LinkedList<Car>();
	public boolean isCombined = false;
	public Set<Section> combined = null;
	public Queue<Car> waitingCars = new LinkedList<Car>();
//	public List<Sensor> sensors = new ArrayList<Sensor>();
	
	public static Section sectionOf(String name){
		if(name == null)
			return null;
		String[] strs = name.split(" ");
		int id = Integer.parseInt(strs[1]);
		if(strs[0].equals("Crossing"))
			return TrafficMap.crossings[id];
		else if(strs[0].equals("Street"))
			return TrafficMap.streets[id];
		else
			return null;
	}
	
	public boolean isOccupied(){
		return !cars.isEmpty();
	}
	
	public static class Crossing extends Section{
		CrossingView view = null;
	}
	
	public static class Street extends Section{
		StreetView view = null;
	}
}
