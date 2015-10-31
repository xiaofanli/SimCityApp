package nju.ics.lixiaofan.view;

import nju.ics.lixiaofan.view.MapView.Coord;

import com.example.simcity.Car;
import com.example.simcity.Section.Crossing;
import com.example.simcity.TrafficMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CrossingView extends View{
	public int id;
	private Paint paint = new Paint();
	public Crossing crossing = null;
	public static double SIZE_PERCENT;
	public static int SIZE;
	public Coord coord = new Coord();
	
	public CrossingView(Context context) {
		super(context);
	}
	
	public CrossingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CrossingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setStyle(Style.FILL);
		switch(crossing.cars.size()){
		case 0:
			paint.setColor(Color.GREEN); break;
		case 1:
			Car car = crossing.cars.peek();
			if(car.isLoading && !TrafficMap.blink)
				return;
			switch(car.name){
			case Car.ORANGE:
				paint.setColor(Color.rgb(255, 97, 0)); break;
			case Car.BLACK:
				paint.setColor(Color.BLACK); break;
			case Car.WHITE:
				paint.setColor(Color.rgb(255, 222, 173)); break;
			case Car.RED:
				paint.setColor(Color.rgb(255, 0, 255)); break;
			case Car.GREEN:
				paint.setColor(Color.rgb(34, 139, 34)); break;
			case Car.SILVER:
				paint.setColor(Color.GRAY); break;
			}
			break;
		default:
			if(!TrafficMap.blink)
				return;
			paint.setColor(Color.RED);
			break;
		}
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
		if(MapView.showSections){
			paint.setColor(Color.BLACK);
			paint.setTextSize(SIZE);
			paint.setTextAlign(Align.CENTER);
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			canvas.drawText(""+id, SIZE/2, SIZE/2-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top, paint);
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pw = MeasureSpec.getSize(widthMeasureSpec);
		int ph = MeasureSpec.getSize(heightMeasureSpec);
		SIZE = (int) (Math.min(pw, ph)*SIZE_PERCENT);
        setMeasuredDimension(SIZE, SIZE);                
	}
}
