package view;

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
	public static int SIZE = -1;
	
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
//		System.out.println(pw + " " + ph);
//		int width  = measureDimension(SIZE, widthMeasureSpec);
//        int height = measureDimension(SIZE, heightMeasureSpec);
		SIZE = (int) (Math.min(pw, ph)/12.5);
        setMeasuredDimension(SIZE, SIZE);                
	}
	
    protected static int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
                
        //1. layout给出了确定的值，比如：100dp
        //2. layout使用的是match_parent，但父控件的size已经可以确定了，比如设置的是具体的值或者match_parent
        if (specMode == MeasureSpec.EXACTLY) {      
            result = specSize; //建议：result直接使用确定值
        } 
        //1. layout使用的是wrap_content
        //2. layout使用的是match_parent,但父控件使用的是确定的值或者wrap_content
        else if (specMode == MeasureSpec.AT_MOST) {            
            result = Math.min(defaultSize, specSize); //建议：result不能大于specSize
        } 
        //UNSPECIFIED,没有任何限制，所以可以设置任何大小
        //多半出现在自定义的父控件的情况下，期望由自控件自行决定大小
        else {      
            result = defaultSize; 
        }
        
        return result;
    }
}
