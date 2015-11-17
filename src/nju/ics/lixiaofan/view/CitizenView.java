package nju.ics.lixiaofan.view;

import com.example.simcity.Citizen;
import com.example.simcity.MainActivity;

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
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(((MapView) getParent()).getScaleFactor(), ((MapView) getParent()).getScaleFactor());
		
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
//		canvas.drawRect(0, 0, SIZE, SIZE, paint);
		if(MainActivity.focus != citizen)
			paint.setColor(Color.BLACK);
		else
			paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(SIZE/2, SIZE/2, SIZE/2, paint);
//		canvas.drawRect(0, 0, SIZE, SIZE, paint);
		canvas.restore();
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(SIZE, SIZE);                
	}
}
