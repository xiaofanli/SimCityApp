package nju.ics.lixiaofan.view;

import com.example.simcity.Citizen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CitizenView extends View{
	public Citizen citizen = null;
	private Paint paint = new Paint();
	public static int SIZE;
	public int color;
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
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
//		canvas.drawRect(0, 0, SIZE, SIZE, paint);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
//		canvas.drawRect(0, 0, SIZE, SIZE, paint);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(SIZE, SIZE);                
	}
}
