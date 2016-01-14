package com.example.simcity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import android.os.Message;
import android.view.View;

import nju.ics.lixiaofan.view.BalloonView;
import nju.ics.lixiaofan.view.CrossingView;
import nju.ics.lixiaofan.view.StreetView;

public class Section extends Location{
	public int[] dir = {-1, -1};	//dir[1] only for crossings
	public Queue<Car> cars = new LinkedList<Car>();
	public Set<String> realCars = new HashSet<String>();
	public boolean isCombined = false;
	public Set<Section> combined = null;
	public Queue<Car> waitingCars = new LinkedList<Car>();
//	public List<Sensor> sensors = new ArrayList<Sensor>();
	public BalloonView balloon = null;
	
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
	
	public void displayBalloon(int type, String sensor, String car, boolean isResolutionEnabled) {
		balloon.type = type;
		balloon.sensor = sensor;
		balloon.car = car;
		balloon.duration = 3000;//display for 3s
		balloon.setIcon(isResolutionEnabled);
		Message msg = MainActivity.msgHandler.obtainMessage();
		msg.arg1 = R.string.set_visibility;
		msg.arg2 = View.VISIBLE;
		msg.obj = this;
		MainActivity.msgHandler.sendMessage(msg);
	}
	
	public static class Crossing extends Section{
		public CrossingView view = null;
	}
	
	public static class Street extends Section{
		public StreetView view = null;
	}
}
