package com.example.simcity;

public class Car {
	public int id;
	public String name = null;
	public byte uid;
	public int state = 0;//0: still	1: moving	-1: uncertain
	public byte dir = -1;//0: N	1: S	2: W	3: E
	public Section loc = null;
	public int deliveryPhase = 0;
	public Section dest = null;
	public boolean isLoading = false;
	
	public static final String ORANGE = "Orange Car";
	public static final String GREEN = "Green Car";
	public static final String BLACK = "Black Car";
	public static final String WHITE = "White Car";
	public static final String SILVER = "Silver SUV";
	public static final String RED = "Red Car";
	
	public static Car carOf(String name){
		if(name == null)
			return null;
		if(TrafficMap.cars.containsKey(name))
			return TrafficMap.cars.get(name);
		else
			return null;
	}
	
	public String getDir(){
		switch(dir){
		case 0:
			return "N";
		case 1:
			return "S";
		case 2:
			return "W";
		case 3:
			return "E";
		}
		return "U";
	}
}
