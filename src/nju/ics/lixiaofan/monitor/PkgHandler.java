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

import com.example.simcity.Car;
import com.example.simcity.Delivery;
import com.example.simcity.Delivery.DeliveryTask;
import com.example.simcity.MainActivity;
import com.example.simcity.R;
import com.example.simcity.Section;
import com.example.simcity.TrafficMap;

public class PkgHandler implements Runnable{
	private static Queue<AppPkg> queue = new LinkedList<AppPkg>();
	private static Sender sender = new Sender();
	private static Receiver receiver = new Receiver(queue);
	private static Socket socket = null;
	private static ObjectInputStream in = null;
	private static ObjectOutputStream out = null;
	private static boolean isConnected = false;
	private static final String IP = "192.168.1.100";
	private static final int PORT = 11111;
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
				if(car != null)
					car.dir = p.dir;
				break;
			}
			case 3:{
				Car car = Car.carOf(p.car);
				if(car == null){
					car = new Car();
					car.name = p.car;
					Message msg = MainActivity.msgHandler.obtainMessage();
					msg.what = R.string.spinner_add_car;
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
					dtask.src = Section.sectionOf(p.src);
					dtask.dst = Section.sectionOf(p.dst);
					dtask.phase = p.phase;
					Delivery.dtasks.put(dtask.id, dtask);
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
						msg.what = R.string.app_disconnected;
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
						socket = new Socket(IP, PORT);
						in = new ObjectInputStream(socket.getInputStream());
						out = new ObjectOutputStream(socket.getOutputStream());
						isConnected = true;
						Message msg = MainActivity.msgHandler.obtainMessage();//new Message();
						msg.what = R.string.app_connected;
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
							msg.what = R.string.app_disconnected;
							MainActivity.msgHandler.sendMessage(msg);
						}
						break;
					}
				}
			}
		}
	};
}
