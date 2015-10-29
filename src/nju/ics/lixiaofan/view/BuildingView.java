package nju.ics.lixiaofan.view;

import nju.ics.lixiaofan.view.MapView.Coord;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class BuildingView extends View{
	public Coord coord = new Coord();
	
	public BuildingView(Context context) {
		super(context);
	}
	
	public BuildingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BuildingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
}
