package nju.ics.lixiaofan.view;

import nju.ics.lixiaofan.view.MapView.Coord;

import com.example.simcity.Car;
import com.example.simcity.MainActivity;
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
		int n = crossing.cars.size();
		switch(n){
		case 0:
			paint.setColor(Color.GREEN); break;
		case 1:
			paint.setColor(Color.YELLOW); break;
		default:
			paint.setColor(Color.RED); break;
		}
		paint.setStyle(Style.FILL);
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
		if(MainActivity.focus == crossing){
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.RED);
			canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
		}
		
		if(n > 0){
			int x = (coord.w-n*CarView.SIZE-(n-1)*CarView.INSET) / 2;
			int y = (coord.h - CarView.SIZE) / 2;
			for(Car car : crossing.cars){
				if(car.isLoading && !TrafficMap.blink){
					x += CarView.SIZE + CarView.INSET;
					continue;
				}
				switch(car.name){
				case Car.ORANGE:
					paint.setColor(0xffffa500); break;
				case Car.BLACK:
					paint.setColor(Color.BLACK); break;
				case Car.WHITE:
					paint.setColor(Color.WHITE); break;
				case Car.RED:
					paint.setColor(Color.RED); break;
				case Car.GREEN:
					paint.setColor(Color.GREEN); break;
				case Car.SILVER:
					paint.setColor(0xffc0c0c0); break;
				}
				paint.setStyle(Style.FILL);
				canvas.drawRect(x, y, x+CarView.SIZE, y+CarView.SIZE, paint);
				if(MainActivity.focus != car)
					paint.setColor(Color.BLACK);
				else
					paint.setColor(Color.RED);
				paint.setStyle(Style.STROKE);
				canvas.drawRect(x, y, x+CarView.SIZE, y+CarView.SIZE, paint);
				x += CarView.SIZE + CarView.INSET;
			}
		}
		
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
		coord.w = coord.h = SIZE;
        setMeasuredDimension(SIZE, SIZE);                
	}
}
