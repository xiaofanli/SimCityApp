package com.example.simcity;

import java.util.ArrayList;
import nju.ics.lixiaofan.monitor.AppPkg;
import nju.ics.lixiaofan.monitor.PkgHandler;
import nju.ics.lixiaofan.view.CitizenView;
import nju.ics.lixiaofan.view.MapView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private static Context appCtx, actCtx;
	private static MapView map;
	private static ViewPager pager;
	private static PagerTabStrip tabStrip;
	private static ArrayList<View> viewContainter = new ArrayList<View>();
	private static ArrayList<String> titleContainer = new ArrayList<String>();
	public static Spinner spinner;
	public static ArrayAdapter<String> spinnerAdapter;
	public static int selectedPage = 0;
	public static Car selectedCar = null;
	public static Button forwardButton, stopButton, delivButton, deleteButton;
	public static RadioButton[] dirButton = new RadioButton[4];
	public static RadioGroup dirRadioGroup = null;
	public static TextView delivSrc, delivDst;
	public static AlertDialog sectionDialog = null;
	
	public static final Handler msgHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case R.string.app_connected:
				Toast.makeText(MainActivity.getAppContext(), "Connected", Toast.LENGTH_LONG).show();
				break;
			case R.string.app_disconnected:
				Toast.makeText(MainActivity.getAppContext(), "Disconnected", Toast.LENGTH_LONG).show();
				break;
			case R.string.close_section_dialog:
				if(sectionDialog != null && sectionDialog.isShowing())
					sectionDialog.dismiss();
				break;
			case R.string.spinner_add_car:
				MainActivity.spinnerAdapter.add((String)msg.obj);
				break;
			case R.string.invalidate_view:
				((View)msg.obj).invalidate();
				break;
			case R.string.map_add_view:
				map.addView((View)msg.obj);
				break;
			case R.string.citizen_update_location:
				if(msg.obj instanceof CitizenView){
					CitizenView cv = (CitizenView) msg.obj;
					int x = (int) (cv.ratioX * map.getWidth());
					int y = (int) (cv.ratioY * map.getHeight());
					cv.layout(x, y, x+CitizenView.SIZE, y+CitizenView.SIZE);
				}
				break;
			}
		};
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		appCtx = getApplicationContext();
		actCtx = this;
		map = (MapView) findViewById(R.id.mapView);
		map.setWillNotDraw(false);
		new TrafficMap(map);
		pager = (ViewPager) findViewById(R.id.viewPager);
		tabStrip = (PagerTabStrip) findViewById(R.id.tabStrip);
		tabStrip.setDrawFullUnderline(false);
		viewContainter.add(LayoutInflater.from(this).inflate(R.layout.tab_car, null));
		viewContainter.add(LayoutInflater.from(this).inflate(R.layout.tab_delivery, null));
		titleContainer.add("Car");
		titleContainer.add("Delivery");
		
		pager.setAdapter(new PagerAdapter() {
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			public int getCount() {
				return viewContainter.size();
			}
			
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}
			
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
			}
			
			public CharSequence getPageTitle(int position) {
				return titleContainer.get(position);
			}
		});
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int arg0) {
				selectedPage = arg0;
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		spinner = (Spinner) viewContainter.get(0).findViewById(R.id.spinnerCar);
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				setselectedCar(position);
//				synchronized (spinner) {
//					spinner.notifyAll();
//					System.out.println("notify");
//				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				forwardButton.setEnabled(false);
				stopButton.setEnabled(false);
			}
		});
		
		dirRadioGroup = (RadioGroup) viewContainter.get(0).findViewById(R.id.radioGroupDir);
		dirButton[0] = (RadioButton) viewContainter.get(0).findViewById(R.id.radioNorth);
		dirButton[0].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedCar.dir = 0;
				AppPkg p = new AppPkg();
				p.setDir(selectedCar.name, selectedCar.dir);
				PkgHandler.send(p);
			}
		});
		dirButton[1] = (RadioButton) viewContainter.get(0).findViewById(R.id.radioSouth);
		dirButton[1].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedCar.dir = 1;
				AppPkg p = new AppPkg();
				p.setDir(selectedCar.name, selectedCar.dir);
				PkgHandler.send(p);
			}
		});
		dirButton[2] = (RadioButton) viewContainter.get(0).findViewById(R.id.radioWest);
		dirButton[2].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedCar.dir = 2;
				AppPkg p = new AppPkg();
				p.setDir(selectedCar.name, selectedCar.dir);
				PkgHandler.send(p);
			}
		});
		dirButton[3] = (RadioButton) viewContainter.get(0).findViewById(R.id.radioEast);
		dirButton[3].setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedCar.dir = 3;
				AppPkg p = new AppPkg();
				p.setDir(selectedCar.name, selectedCar.dir);
				PkgHandler.send(p);
			}
		});
		
		forwardButton = (Button) viewContainter.get(0).findViewById(R.id.button_forward);
		forwardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AppPkg p = new AppPkg();
				p.setCmd(selectedCar.name, (byte) 1);
				PkgHandler.send(p);
			}
		});
		stopButton = (Button) viewContainter.get(0).findViewById(R.id.button_stop);
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AppPkg p = new AppPkg();
				p.setCmd(selectedCar.name, (byte) 0);
				PkgHandler.send(p);
			}
		});
		if(spinner.getSelectedItem() == null){
			forwardButton.setEnabled(false);
			stopButton.setEnabled(false);
		}
		
		delivSrc = (TextView) viewContainter.get(1).findViewById(R.id.textView_src);
		delivDst = (TextView) viewContainter.get(1).findViewById(R.id.textView_dst);
		delivButton = (Button) viewContainter.get(1).findViewById(R.id.button_deliver);
		delivButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AppPkg p = new AppPkg();
				p.setDelivery(delivSrc.getText().toString(), delivDst.getText().toString());
				PkgHandler.send(p);
			}
		});
		deleteButton = (Button) viewContainter.get(1).findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!delivDst.getText().equals("")){
					delivDst.setText("");
					delivButton.setEnabled(false);
				}
				else if(!delivSrc.getText().equals("")){
					delivSrc.setText("");
					deleteButton.setEnabled(false);
				}
			}
		});
		new Thread(new PkgHandler()).start();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_sections:
			MapView.showSections = !MapView.showSections;
			map.invalidate();
			break;
		}
		return false;
	}

	public static void setselectedCar(int position){
		selectedCar = TrafficMap.cars.get(spinnerAdapter.getItem(position));
		if(selectedCar != null){
			forwardButton.setEnabled(true);
			stopButton.setEnabled(true);
			
			dirRadioGroup.clearCheck();
			for(RadioButton rb : dirButton){
				rb.setEnabled(false);
			}
			
//			System.out.println("selected");
			if(selectedCar.loc != null){
				dirButton[selectedCar.loc.dir[0]].setEnabled(true);
				if(selectedCar.loc.dir[1] >= 0)
					dirButton[selectedCar.loc.dir[1]].setEnabled(true);
				
				if(selectedCar.dir >= 0 && dirButton[selectedCar.dir].isEnabled()){
//					System.out.println(selectedCar.dir);
					dirButton[selectedCar.dir].setChecked(true);
				}
				else{
					dirButton[selectedCar.loc.dir[0]].setChecked(true);
					selectedCar.dir = (byte) selectedCar.loc.dir[0];
				}
			}
			else{
				selectedCar.dir = -1;
//				PkgHandler.send(new AppPkg(selectedCar.name, selectedCar.dir, null));
			}
		}
	}
	
	public static Context getAppContext(){
		return appCtx;
	}
	
	public static Context getActContext(){
		return actCtx;
	}
}
