package com.example.simcity;

import nju.ics.lixiaofan.view.CitizenView;

public class Citizen {
	public String name;
	public String gender = null;
	public String job = null;
	public String act = null;
	public Location loc = null, dest = null;
	public Car car = null;
	public CitizenView view = null;
	
	public Citizen(String citizen, String gender, String job) {
		this.name = citizen;
		this.gender = gender;
		this.job = job;
	}
}
