package nju.ics.lixiaofan.view;

import java.io.InputStream;
import java.util.HashMap;

import com.example.simcity.MainActivity;
import com.example.simcity.R;
import com.example.simcity.Section;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BalloonView extends ImageView{
	public static int SIZE;
	public static double SIZE_PERCENT;
//	public Coord coord = new Coord();
	public Section section;
	Paint paint = new Paint();
	private static HashMap<String, Bitmap> balloons = new HashMap<String, Bitmap>();
	public int duration = 0;
	public int type;
	public String sensor = "", car = "";
	
	public BalloonView(Context context) {
		super(context);
	}
	
	public BalloonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BalloonView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(((MapView) getParent()).getScaleFactor(), ((MapView) getParent()).getScaleFactor());
		
		String str = "";
		if(type == 1)
			str = "-FP-";
		else if(type == 2)
			str = "-FN-";
		paint.setColor(Color.BLACK);
		paint.setTextSize(SIZE/6);
		paint.setTextAlign(Align.CENTER);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		canvas.drawText(str, SIZE/2, SIZE/2-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top-2*paint.getTextSize(), paint);
		canvas.drawText(sensor, SIZE/2, SIZE/2-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top-paint.getTextSize(), paint);
		canvas.drawText(car, SIZE/2, SIZE/2-(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.top, paint);
		canvas.restore();
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pw = MeasureSpec.getSize(widthMeasureSpec);
		int ph = MeasureSpec.getSize(heightMeasureSpec);
		SIZE = (int) (Math.min(pw, ph)*SIZE_PERCENT);
        setMeasuredDimension(SIZE, SIZE);
	}
	
	public void setIcon(boolean resolutionEnabled){
		setImageBitmap(resolutionEnabled ? balloons.get("green") : balloons.get("red"));
	}
	
	public static void readBalloonImage(){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = MainActivity.getAppCtx().getResources().openRawResource(R.drawable.green_balloon);
		balloons.put("green", BitmapFactory.decodeStream(is, null, opt));
		is = MainActivity.getAppCtx().getResources().openRawResource(R.drawable.red_balloon);
		balloons.put("red", BitmapFactory.decodeStream(is, null, opt));
	}
}
