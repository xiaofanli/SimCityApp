package com.example.simcity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import nju.ics.lixiaofan.monitor.AppPkg;
import nju.ics.lixiaofan.monitor.PkgHandler;
import nju.ics.lixiaofan.view.BalloonView;
import nju.ics.lixiaofan.view.BuildingView;
import nju.ics.lixiaofan.view.CitizenView;
import nju.ics.lixiaofan.view.CrossingView;
import nju.ics.lixiaofan.view.MapView;
import nju.ics.lixiaofan.view.StreetView;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import com.example.simcity.Section.Crossing;
import com.example.simcity.Section.Street;

public class TrafficMap {
	public static boolean dir = true;
	public static Crossing[] crossings = new Crossing[9];
	public static Street[] streets = new Street[32];
	public static Section[] sections = new Section[crossings.length+streets.length];
	public static ConcurrentHashMap<String,Car> cars = new ConcurrentHashMap<String, Car>();
	public static List<Citizen> citizens = new ArrayList<Citizen>();
	public static List<Building> buildings = new ArrayList<Building>();
	public static boolean blink = false;
//	public static List<List<Sensor>> sensors = new ArrayList<List<Sensor>>();
	
	public static final int sh = 2;//street height
	private static final int sw = sh * 2;//street width
	private static final int cw = (int) (sh * 1.5);//crossing width
	private static final int aw = cw / 2;
	
	private static SoundPool soundPool;
	private static HashMap<String, Integer> soundMap;
	
	public TrafficMap(MapView map) {
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundMap = new HashMap<String, Integer>();
		soundMap.put("crash", soundPool.load(MainActivity.getAppCtx(), R.raw.crash, 1));
		soundMap.put("oh no", soundPool.load(MainActivity.getAppCtx(), R.raw.oh_no, 1));
		
		int sectIdx = 0;
		for(int i = 0;i < crossings.length;i++){
			crossings[i] = new Crossing();
			sections[sectIdx++] = crossings[i];
			crossings[i].id = i;
			crossings[i].name = "Crossing "+i;
			crossings[i].view = new CrossingView(MainActivity.getAppCtx());
			crossings[i].view.id = i;
			crossings[i].view.crossing = crossings[i];
			MyClickListener listener = new MyClickListener(crossings[i]);
			crossings[i].view.setOnClickListener(listener);
			crossings[i].view.setOnLongClickListener(listener);
			map.addView(crossings[i].view);
		}
		
		for(int i = 0;i < streets.length;i++){
			streets[i] = new Street();
			sections[sectIdx++] = streets[i];
			streets[i].id = i;
			streets[i].name = "Street "+i;
			streets[i].view = new StreetView(MainActivity.getAppCtx());
			streets[i].view.id = i;
			streets[i].view.street = streets[i];
			streets[i].view.isVertical = ((i%8)>1 && (i%8)<6);
			MyClickListener listener = new MyClickListener(streets[i]);
			streets[i].view.setOnClickListener(listener);
			streets[i].view.setOnLongClickListener(listener);
			map.addView(streets[i].view);
		}
		
		BalloonView.readBalloonImage();
		for(Section section : sections){
			section.balloon = new BalloonView(MainActivity.getAppCtx());
			section.balloon.section = section;
			map.addView(section.balloon);
//			section.displayBalloon(1, "B1S2", Car.ORANGE, false);
		}
		
		setCombined();
		setDir();
		
		StreetView.HEIGHT_PERCENT = (double )sh / (sh+4*sw+4*cw);
		StreetView.WIDTH_RATIO1 = 1 + 0.5*(sh+cw)/sw;
		StreetView.WIDTH_RATIO2 = 1 + (double )(sh+cw)/sw;
		StreetView.ARC_RATIO = (float )aw / sw;
		StreetView.WIDTH_PERCENT = 2 * StreetView.HEIGHT_PERCENT;
		BuildingView.SIZE_PERCENT = StreetView.WIDTH_PERCENT;
		CrossingView.SIZE_PERCENT = 1.5 * StreetView.HEIGHT_PERCENT;
		BalloonView.SIZE_PERCENT = StreetView.WIDTH_PERCENT;
		
		new Thread(blinkThread).start();
	}
	
	public static void playCrashSound() {
		soundPool.play(soundMap.get("crash"), 1.0f, 1.0f, 1, 0, 1.0f);
	}
	
	public static void playOhNoSound() {
		soundPool.play(soundMap.get("oh no"), 1.0f, 1.0f, 1, 0, 1.0f);
	}
	
	private static class MyClickListener implements OnClickListener, OnLongClickListener{
		private Location loc = null;
		public MyClickListener(Location loc) {
			this.loc = loc;
		}
		
		public void onClick(View v) {
			if(MainActivity.selectedPage == 0){
//				String str = "";
//				if(v instanceof CrossingView || v instanceof StreetView){
//					Section section = (Section )loc;
//					if(!section.cars.isEmpty()){
//						str = "Cars:\n";
//						for(Car car : section.cars){
//							str += car.name + " " + car.getDir();
//							str += "\n";
//						}
//					}
//				}
//				MainActivity.sectionDialog = new AlertDialog.Builder(MainActivity.getActContext())
//				.setTitle(loc.name)
//				.setMessage(str)
//				.show();
				MainActivity.updateFocus(loc);
			}
			else if(MainActivity.selectedPage == 1){
				if(MainActivity.delivSrc.getText().equals("")){
					MainActivity.delivSrc.setText(loc.name);
					MainActivity.deleteButton.setEnabled(true);
				}
				else if(MainActivity.delivDst.getText().equals("")){
					String src = MainActivity.delivSrc.getText().toString();
					if(loc.name.equals(src))
						return;
					else if(v instanceof CrossingView || v instanceof StreetView){
						Section section = (Section )loc;
						if(section.isCombined && section.combined.contains(Section.sectionOf(src)))
							return;
					}
					
					MainActivity.delivDst.setText(loc.name);
					MainActivity.delivButton.setEnabled(true);
				}
			}
		}
		
		private SingleChoiceListener scl = new SingleChoiceListener();
		public boolean onLongClick(View v) {
			if(v instanceof CrossingView || v instanceof StreetView){
				Section section = (Section )loc;
				if(MainActivity.selectedPage == 0){
					if(MainActivity.spinnerAdapter.getCount() > 0){
						scl.set(section);
	//					System.out.println(section.name);
						new AlertDialog.Builder(MainActivity.getActCtx())
						.setTitle(section.name).setSingleChoiceItems(MainActivity.spinnerAdapter, -1, scl)
						.show();
					}
				}
				else if(MainActivity.selectedPage == 1)
					return false;
			}
			else if(v instanceof BuildingView)
				return false;
			
			return true; //avoid triggering click event
		}
		
		private class SingleChoiceListener implements DialogInterface.OnClickListener{
			Section section = null;
			public void onClick(DialogInterface dialog, int which) {
				Car car = cars.get(MainActivity.spinnerAdapter.getItem(which));
//				System.out.println(car.name);
				if(section.cars.contains(car)){
					PkgHandler.send(new AppPkg().setCar(car.name, car.dir, car.loc.name));
					car.dir = -1;
					carLeave(car, car.loc);
				}
				else{
					carEnter(car, section);
					if(MainActivity.selectedCar != car)
						MainActivity.spinner.setSelection(which);
						
					MainActivity.setSelectedCar(which);
					//send car setting
					PkgHandler.send(new AppPkg().setCar(car.name, car.dir, car.loc.name));
				}
				dialog.dismiss();
			}
			
			public void set(Section s){
				section = s;
			}
		}
	}
	
	public static void carEnter(Car car, Section section){
		if(car == null || section == null)
			return;
		carLeave(car, car.loc);
		
		section.cars.add(car);
		car.loc = section;
		
		Message msg = MainActivity.msgHandler.obtainMessage();
		msg.arg1 = R.string.invalidate_view;
		if(section instanceof Crossing)
			msg.obj = ((Crossing) section).view;
		else if(section instanceof Street)
			msg.obj = ((Street) section).view;
		MainActivity.msgHandler.sendMessage(msg);
		
		if(section.isCombined){
			for(Section s : section.combined){
				msg = MainActivity.msgHandler.obtainMessage();
				msg.arg1 = R.string.invalidate_view;
				if(s instanceof Crossing)
					msg.obj = ((Crossing) s).view;
				else if(s instanceof Street)
					msg.obj = ((Street) s).view;
				MainActivity.msgHandler.sendMessage(msg);
			}
		}
	}
	
	public static void carLeave(Car car, Section section){
		if(car == null || section == null)
			return;
		section.cars.remove(car);
		if(car.loc == section)
			car.loc = null;
		
		Message msg = MainActivity.msgHandler.obtainMessage();
		msg.arg1 = R.string.invalidate_view;
		if(section instanceof Crossing)
			msg.obj = ((Crossing) section).view;
		else if(section instanceof Street)
			msg.obj = ((Street) section).view;
		MainActivity.msgHandler.sendMessage(msg);
		
		if(section.isCombined){
			for(Section s : section.combined){
				msg = MainActivity.msgHandler.obtainMessage();
				msg.arg1 = R.string.invalidate_view;
				if(s instanceof Crossing)
					msg.obj = ((Crossing) s).view;
				else if(s instanceof Street)
					msg.obj = ((Street) s).view;
				MainActivity.msgHandler.sendMessage(msg);
			}
		}
	}
	
	public static void add(Building building){
		buildings.add(building);
		building.view = new BuildingView(MainActivity.getAppCtx());
		building.view.building = building;
		building.view.setOnClickListener(new MyClickListener(building));
		building.view.setIcon();
//		map.addView(buidling.view);
		
		Message msg = MainActivity.msgHandler.obtainMessage();
		msg.arg1 = R.string.map_add_view;
		msg.obj = building.view;
		MainActivity.msgHandler.sendMessage(msg);
	}
	
	public static void add(Citizen citizen){
		citizens.add(citizen);
		citizen.view = new CitizenView(MainActivity.getAppCtx());
		citizen.view.citizen = citizen;
		citizen.view.color = citizen.color;
		citizen.view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Citizen c = ((CitizenView ) v).citizen;
//				new AlertDialog.Builder(MainActivity.getActContext())
//				.setTitle(c.name)
//				.setMessage(c.act)
//				.show();
				MainActivity.updateFocus(c);
			}
		});
//		citizen.view.setOnClickListener(new MyClickListener(building));
//		map.addView(buidling.view);
		
		Message msg = MainActivity.msgHandler.obtainMessage();
		msg.arg1 = R.string.map_add_view;
		msg.obj = citizen.view;
		MainActivity.msgHandler.sendMessage(msg);
	}
	
	private void setDir(){
		Set<Section> set = new HashSet<Section>();
		set.add(streets[8]);
		set.add(crossings[1]);
		set.add(streets[7]);
		set.add(crossings[0]);
		set.add(streets[17]);
		set.add(crossings[8]);
		set.add(streets[24]);
		set.add(crossings[7]);
		set.add(streets[23]);
		setDir(set, dir?2:3);
		
		set.clear();
		set.add(streets[6]);
		set.add(crossings[3]);
		set.add(streets[15]);
		set.add(crossings[4]);
		set.add(streets[16]);
		set.add(crossings[5]);
		setDir(set, dir?3:2);
		
		set.clear();
		set.add(streets[18]);
		set.add(crossings[3]);
		set.add(streets[11]);
		set.add(crossings[0]);
		set.add(streets[28]);
		set.add(crossings[8]);
		set.add(streets[20]);
		set.add(crossings[5]);
		set.add(streets[13]);
		setDir(set, dir?0:1);
		
		set.clear();
		set.add(streets[0]);
		set.add(crossings[1]);
		set.add(streets[12]);
		set.add(crossings[4]);
		set.add(streets[19]);
		set.add(crossings[7]);
		setDir(set, dir?1:0);
		
		set.clear();
		set.add(crossings[2]);
		setDir(set, dir?2:1);
		set.clear();
		set.add(crossings[6]);
		setDir(set, dir?0:3);
	}
	
	private void setDir(Set<Section> set, int dir){
		for(Section s : set){
			if(s.dir[0] < 0)
				s.dir[0] = dir;
			else
				s.dir[1] = dir;
			
			if(s.isCombined)
				for(Section s2 : s.combined)
					if(s2.dir[0] < 0)
						s2.dir[0] = dir;
					else
						s2.dir[1] = dir;
		}
	}
	
	private void setCombined(){
		Set<Section> sections = new HashSet<Section>();
		sections.add(streets[0]);
		sections.add(streets[2]);
		sections.add(streets[3]);
		setCombined(sections);
		
		sections.clear();
		sections.add(streets[6]);
		sections.add(streets[10]);
		sections.add(streets[14]);
		setCombined(sections);
		
		sections.clear();
		sections.add(streets[17]);
		sections.add(streets[21]);
		sections.add(streets[25]);
		setCombined(sections);
		
		sections.clear();
		sections.add(streets[28]);
		sections.add(streets[29]);
		sections.add(streets[31]);
		setCombined(sections);
		
		sections.clear();
		sections.add(streets[1]);
		sections.add(streets[4]);
		sections.add(streets[5]);
		sections.add(streets[9]);
		sections.add(crossings[2]);
		setCombined(sections);
		
		sections.clear();
		sections.add(streets[22]);
		sections.add(streets[26]);
		sections.add(streets[27]);
		sections.add(streets[30]);
		sections.add(crossings[6]);
		setCombined(sections);
	}
	
	private void setCombined(Set<Section> sections){
		for(Section s : sections){
			s.isCombined = true;
			if(s.combined == null)
				s.combined = new HashSet<Section>();
			for(Section other : sections)
				if(other != s){
					s.combined.add(other);
					other.cars = s.cars;
					other.waitingCars = s.waitingCars;
					other.dir = s.dir;
				}
		}
	}
	
	private static Runnable blinkThread = new Runnable() {
		private int duration = 500;
		public void run() {
			while(true){
				blink = !blink;
				for(Section s : sections){
					if(s.balloon.duration > 0){
						if(s.balloon.getVisibility() != View.VISIBLE){
							Message msg = MainActivity.msgHandler.obtainMessage();
							msg.arg1 = R.string.set_visibility;
							msg.arg2 = View.VISIBLE;
							msg.obj = s.balloon;
							MainActivity.msgHandler.sendMessage(msg);
						}
						s.balloon.duration -= duration;
					}
					else if(s.balloon.getVisibility() == View.VISIBLE){
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.set_visibility;
						msg.arg2 = View.INVISIBLE;
						msg.obj = s.balloon;
						MainActivity.msgHandler.sendMessage(msg);
					}
					
					if(s.cars.isEmpty())
						continue;
					if(s.cars.size() > 1 || s.cars.peek().isLoading){
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.invalidate_view;
						msg.obj = s instanceof Crossing ? ((Crossing) s).view : ((Street) s).view;
						MainActivity.msgHandler.sendMessage(msg);
					}
				}
				
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
