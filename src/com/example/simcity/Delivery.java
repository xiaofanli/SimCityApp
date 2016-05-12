package com.example.simcity;

import java.util.HashMap;

public class Delivery {
	public static HashMap<Integer, DeliveryTask> dtasks = new HashMap<Integer, DeliveryTask>();
	
	public static class DeliveryTask {
		public int id;
		public Section src = null, dst = null;
		public Car car = null;
		public String goods;
		public int phase;//0: search car; 1: to src 2: to dest
	}
}
