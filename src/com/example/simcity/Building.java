package com.example.simcity;

import nju.ics.lixiaofan.view.BuildingView;

public class Building extends Location{
	public String type = null;
	public BuildingView view = null;
	public int block;//block number
//	public Set<Section> addrs = null;
	
	public Building(String building, String type, int block) {
		name = building;
		this.type = type;
		this.block = block;
	}
}