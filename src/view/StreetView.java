package view;

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
	public static int WIDTH;
	public static int HEIGHT;
	private static RectF rect = new RectF();
	public boolean isVertical;
	
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
		paint.setStyle(Style.FILL);
		switch(street.cars.size()){
		case 0:
			paint.setColor(Color.GREEN); break;
		case 1:
			Car car = street.cars.peek();
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
		if(isVertical){
			if(id < 5 || id > 26)
				rect.set(0, 0, HEIGHT, (float) (WIDTH*1.375));
			else if(id == 5 || id == 10 || id == 21 || id == 26)
				rect.set(0, 0, HEIGHT, (float) (WIDTH*1.75));
			else
				rect.set(0, 0, HEIGHT, WIDTH);
		}
		else{
			if(id < 2 || id > 29)
				rect.set(0, 0, (float) (WIDTH*1.75), HEIGHT);
			else if(id%8 == 6 || id%8 == 1)
				rect.set(0, 0, (float) (WIDTH*1.375), HEIGHT);
			else
				rect.set(0, 0, WIDTH, HEIGHT);
		}
		canvas.drawRoundRect(rect, WIDTH/10, WIDTH/10, paint);
		if(MapView.showSections){
			paint.setColor(Color.BLACK);
			paint.setTextSize(Math.min(rect.width(), rect.height()));
			paint.setTextAlign(Align.CENTER);
			FontMetricsInt fontMetrics = paint.getFontMetricsInt();
			canvas.drawText(""+id, rect.centerX(), rect.centerY()-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top, paint);
		}
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = isVertical ? CrossingView.measureDimension(DEFAULT_VIEW_HEIGHT, widthMeasureSpec)
//				: CrossingView.measureDimension(DEFAULT_VIEW_WIDTH, widthMeasureSpec);
//		int height = isVertical ? CrossingView.measureDimension(DEFAULT_VIEW_WIDTH, widthMeasureSpec)
//				: CrossingView.measureDimension(DEFAULT_VIEW_HEIGHT, heightMeasureSpec);
		int pw = MeasureSpec.getSize(widthMeasureSpec);
		int ph = MeasureSpec.getSize(heightMeasureSpec);
		HEIGHT = (int) (Math.min(pw, ph)/25);
		WIDTH = 4 * HEIGHT;
		if(isVertical){
			if(id < 5 || id > 26)
				setMeasuredDimension(HEIGHT, (int) (WIDTH*1.375));
			else if(id == 5 || id == 10 || id == 21 || id == 26)
				setMeasuredDimension(HEIGHT, (int) (WIDTH*1.75));
			else
				setMeasuredDimension(HEIGHT, WIDTH);
		}
		else{
			if(id < 2 || id > 29)
				setMeasuredDimension((int) (WIDTH*1.75), HEIGHT);
			else if(id%8 == 6 || id%8 == 1)
				setMeasuredDimension((int) (WIDTH*1.375), HEIGHT);
			else
				setMeasuredDimension(WIDTH, HEIGHT);
		}
	}
}
