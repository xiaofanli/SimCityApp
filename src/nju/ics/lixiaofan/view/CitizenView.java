package nju.ics.lixiaofan.view;

import com.example.simcity.Citizen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class CitizenView extends View{
	public Citizen citizen = null;
	public static int SIZE;
	private Color color = null;
	public double ratioX, ratioY;
	
	public CitizenView(Context context) {
		super(context);
	}
	
	public CitizenView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CitizenView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(SIZE, SIZE);                
	}
}
