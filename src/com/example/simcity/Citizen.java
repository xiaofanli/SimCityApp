package com.example.simcity;

import nju.ics.lixiaofan.view.CitizenView;

public class Citizen {
	public String name;
	public String gender = null;
	public String job = null;
	public String act = "None";
	public Location loc = null, dest = null;
	public Car car = null;
	public CitizenView view = null;
	public int color;
	
	public Citizen(String citizen, String gender, String job, int color) {
		this.name = citizen;
		this.gender = gender;
		this.job = job;
		this.color = color;
	}
}
