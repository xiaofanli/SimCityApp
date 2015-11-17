package nju.ics.lixiaofan.monitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import android.os.Message;
import android.view.View;

import com.example.simcity.Building;
import com.example.simcity.Car;
import com.example.simcity.Citizen;
import com.example.simcity.Delivery;
import com.example.simcity.Delivery.DeliveryTask;
import com.example.simcity.MainActivity;
import com.example.simcity.R;
import com.example.simcity.Section;
import com.example.simcity.TrafficMap;

public class PkgHandler implements Runnable{
	private static String pc = "114.212.85.205";//"192.168.1.100";
	private static Queue<AppPkg> queue = new LinkedList<AppPkg>();
	private static Sender sender = new Sender();
	private static Receiver receiver = new Receiver(queue);
	private static Socket socket = null;
	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;
	private static boolean isConnected = false;
	public PkgHandler() {
		new Thread(receiver).start();
		new Thread(sender).start();
	}
	
	public void run() {
		while(true){
			while(queue.isEmpty()){
				synchronized (queue) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			AppPkg p = null;
			synchronized (queue) {
				p = queue.poll();
				if(p == null)
					continue;
			}
				
			switch (p.type) {
			case 1:{
				Car car = Car.carOf(p.car);
				if(car != null){
					car.dir = p.dir;
					if(car == MainActivity.focus){
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.update_focus;
						msg.obj = MainActivity.focus;
						MainActivity.msgHandler.sendMessage(msg);
					}
				}
				break;
			}
			case 3:{
				Car car = Car.carOf(p.car);
				if(car == null){
					car = new Car();
					car.name = p.car;
					Message msg = MainActivity.msgHandler.obtainMessage();
					msg.arg1 = R.string.spinner_add_car;
					msg.obj = car.name;
					MainActivity.msgHandler.sendMessage(msg);
					TrafficMap.cars.put(car.name, car);
				}
				
				Section sect = Section.sectionOf(p.loc);
				if(sect != null){
					if(sect.cars.contains(car)){
						car.dir = -1;
						TrafficMap.carLeave(car, sect);
					}
					else{
						car.dir = p.dir;
						TrafficMap.carEnter(car, sect);
					}
					
					if(car == MainActivity.focus || sect == MainActivity.focus || sect.isCombined && sect.combined.contains(MainActivity.focus)){
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.update_focus;
						msg.obj = MainActivity.focus;
						MainActivity.msgHandler.sendMessage(msg);
					}
				}
				break;
			}
			case 5:{
				Car car = Car.carOf(p.car);
				if(car != null)
					car.isLoading = p.loading;
				break;
			}
			case 6:{
				Car car = Car.carOf(p.car);
				if(car != null)
					car.isLoading = p.unloading;
				break;
			}
			case 7:
				if(Delivery.dtasks.containsKey(p.delivid)){
					if(p.phase == 3)
						Delivery.dtasks.remove(p.delivid);
				}
				else{
					DeliveryTask dtask = new DeliveryTask();
					dtask.id = p.delivid;
					dtask.car = Car.carOf(p.car);
					dtask.src = p.src;
					dtask.dest = p.dest;
					dtask.phase = p.phase;
					Delivery.dtasks.put(dtask.id, dtask);
				}
				break;
			case 9:{
				boolean exist = false;
				for(Building b : TrafficMap.buildings)
					if(b.name.equals(p.building)){
						exist = true;
						break;
					}
				if(!exist)
					TrafficMap.add(new Building(p.building, p.btype, p.block));
				break;
			}
			case 10:{
				boolean exist = false;
				for(Citizen c : TrafficMap.citizens)
					if(c.name.equals(p.citizen)){
						exist = true;
						break;
					}
				if(!exist)
					TrafficMap.add(new Citizen(p.citizen, p.gender, p.job, p.color));
				break;
			}
			case 11:
				for(Citizen c : TrafficMap.citizens)
					if(c.name.equals(p.citizen)){
						c.act = p.act;
						if(c == MainActivity.focus){
							Message msg = MainActivity.msgHandler.obtainMessage();
							msg.arg1 = R.string.update_focus;
							msg.obj = MainActivity.focus;
							MainActivity.msgHandler.sendMessage(msg);
						}
						break;
					}
				break;
			case 12:
				for(Citizen c : TrafficMap.citizens)
					if(c.name.equals(p.citizen)){
						c.view.ratioX = p.ratioX;
						c.view.ratioY = p.ratioY;
						
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.citizen_update_location;
						msg.obj = c.view;
						MainActivity.msgHandler.sendMessage(msg);
						break;
					}
				break;
			case 13:
				for(Citizen c : TrafficMap.citizens)
					if(c.name.equals(p.citizen)){
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.citizen_set_visibility;
						msg.arg2 = p.isVisible ? View.VISIBLE : View.INVISIBLE;
						msg.obj = c.view;
						MainActivity.msgHandler.sendMessage(msg);
//						System.out.println("visible: " + p.isVisible);
						break;
					}
				break;
			}
		}
	}
	
	public void add(AppPkg p){
		synchronized (queue) {
			queue.add(p);
			queue.notify();
		}		
	}

	public static void send(AppPkg p){
		sender.add(p);
	}
	
	private static class Sender implements Runnable {
		Queue<AppPkg> queue = new LinkedList<AppPkg>();
		
		public void run() {
			while(true){
				while(!isConnected){
					synchronized (queue) {
						queue.clear();
					}
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				while(queue.isEmpty()){
					synchronized (queue) {
						try {
							queue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if(!isConnected)
					continue;
				
				AppPkg p = null;
				synchronized (queue) {
					p = queue.poll();
					if(p == null)
						continue;
				}
				try {
					out.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
					if(isConnected){
						isConnected = false;
						Message msg = MainActivity.msgHandler.obtainMessage();
						msg.arg1 = R.string.app_disconnected;
						MainActivity.msgHandler.sendMessage(msg);
					}
				}
			}			
		}
		
		public void add(AppPkg p){
			if(isConnected)
				synchronized (queue) {
					queue.add(p);
					queue.notify();
				}
		}
	};
	
	private static class Receiver implements Runnable {
		Queue<AppPkg> queue;
		
		public Receiver(Queue<AppPkg> queue) {
			this.queue = queue;
		}

		public void run() {
			while(true){
				while(!isConnected){
					try {
						socket = new Socket(pc, 11111);
						in = new ObjectInputStream(socket.getInputStream());
						out = new ObjectOutputStream(socket.getOutputStream());
						isConnected = true;
						Message msg = MainActivity.msgHandler.obtainMessage();//new Message();
						msg.arg1 = R.string.app_connected;
						MainActivity.msgHandler.sendMessage(msg);
						synchronized (queue) {
							queue.clear();
						}
						synchronized (sender) {
							sender.notify();
						}
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while(true){
					try {
						AppPkg p = (AppPkg) in.readObject();
						synchronized (queue) {
							queue.add(p);
							queue.notify();
						}
					} catch (OptionalDataException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
						if(isConnected){
							isConnected = false;
							Message msg = MainActivity.msgHandler.obtainMessage();
							msg.arg1 = R.string.app_disconnected;
							MainActivity.msgHandler.sendMessage(msg);
						}
						break;
					}
				}
			}
		}
	};
}
