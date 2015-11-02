package nju.ics.lixiaofan.view;

import nju.ics.lixiaofan.view.MapView.Coord;

import com.example.simcity.Car;
import com.example.simcity.TrafficMap;
import com.example.simcity.Section.Street;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class StreetView  extends View{
	public int id;
	private Paint paint = new Paint();
	public Street street = null;
	public static double WIDTH_PERCENT;
	public static double WIDTH_RATIO1;
	public static double WIDTH_RATIO2;
	public static float ARC_RATIO;
	public static double HEIGHT_PERCENT;
	public static int WIDTH;
	public static int HEIGHT;
	private RectF rect = new RectF();
	public boolean isVertical;
	public Coord coord = new Coord();
	
	public StreetView(Context context) {
		super(context);
	}
	
	public StreetView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public StreetView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = street.cars.size();
		switch(n){
		case 0:
			paint.setColor(Color.GREEN); break;
		case 1:
			paint.setColor(Color.YELLOW); break;
		default:
			paint.setColor(Color.RED); break;
		}
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(rect, coord.arcw, coord.arch, paint);
		
		if(n > 0){
			int x = isVertical ? (coord.w - CarView.SIZE) / 2 : (coord.w-n*CarView.SIZE-(n-1)*CarView.INSET) / 2;
			int y = isVertical ? (coord.h-n*CarView.SIZE-(n-1)*CarView.INSET) / 2 : (coord.h - CarView.SIZE) / 2;
			for(Car car : street.cars){
				if(car.isLoading && !TrafficMap.blink){
					if(isVertical)
						y += CarView.SIZE + CarView.INSET;
					else
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
				paint.setColor(Color.BLACK);
				paint.setStyle(Style.STROKE);
				canvas.drawRect(x, y, x+CarView.SIZE, y+CarView.SIZE, paint);
				if(isVertical)
					y += CarView.SIZE + CarView.INSET;
				else
					x += CarView.SIZE + CarView.INSET;
			}
		}
		
		if(MapView.showSections){
			paint.setColor(Color.BLACK);
			paint.setTextSize(Math.min(rect.width(), rect.height()));
			paint.setTextAlign(Align.CENTER);
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			canvas.drawText(""+id, rect.centerX(), rect.centerY()-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top, paint);
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pw = MeasureSpec.getSize(widthMeasureSpec);
		int ph = MeasureSpec.getSize(heightMeasureSpec);
		HEIGHT = (int) (Math.min(pw, ph) * HEIGHT_PERCENT);
		WIDTH = (int) (Math.min(pw, ph) * WIDTH_PERCENT);
		CarView.SIZE = (int) (0.8 * HEIGHT);
		CarView.INSET = (int) (0.2 * HEIGHT);
		CitizenView.SIZE = (int) (0.64 * HEIGHT);
		coord.arcw = coord.arch = WIDTH*ARC_RATIO;
		
		if(isVertical){
			coord.w = HEIGHT;
			if(id < 5 || id > 26)
				coord.h = (int) (WIDTH*WIDTH_RATIO1);
			else if(id == 5 || id == 10 || id == 21 || id == 26){
				coord.h = (int) (WIDTH*WIDTH_RATIO2);
			}
			else
				coord.h = WIDTH;
		}
		else{
			coord.h = HEIGHT;
			if(id < 2 || id > 29)
				coord.w = (int) (WIDTH*WIDTH_RATIO2);
			else if(id%8 == 6 || id%8 == 1)
				coord.w = (int) (WIDTH*WIDTH_RATIO1);
			else
				coord.w = WIDTH;
		}
		rect.set(0, 0, coord.w, coord.h);
		setMeasuredDimension(coord.w, coord.h);
	}
}
