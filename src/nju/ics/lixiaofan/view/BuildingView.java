package nju.ics.lixiaofan.view;

import java.io.InputStream;

import com.example.simcity.Building;
import com.example.simcity.MainActivity;
import com.example.simcity.R;

import nju.ics.lixiaofan.view.MapView.Coord;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BuildingView extends ImageView{
	public Building building = null;
	public static int SIZE;
	public static double SIZE_PERCENT;
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
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int pw = MeasureSpec.getSize(widthMeasureSpec);
		int ph = MeasureSpec.getSize(heightMeasureSpec);
		SIZE = (int) (Math.min(pw, ph)*SIZE_PERCENT);
        setMeasuredDimension(SIZE, SIZE);
	}

	public void setIcon() {
		switch (building.type) {
		case "StarkIndustries":
//			setImageResource(R.drawable.stark_industries);
			readBitmap(MainActivity.getAppContext(), R.drawable.stark_industries);
			break;
		case "Hospital":
//			setImageResource(R.drawable.hospital);
			readBitmap(MainActivity.getAppContext(), R.drawable.hospital);
			break;
		case "School":
//			setImageResource(R.drawable.nju);
			readBitmap(MainActivity.getAppContext(), R.drawable.nju);
			break;
		case "PoliceStation":
//			setImageResource(R.drawable.shield);
			readBitmap(MainActivity.getAppContext(), R.drawable.shield);
			break;
		case "Restaurant":
//			setImageResource(R.drawable.java);
			readBitmap(MainActivity.getAppContext(), R.drawable.java);
			break;
		default:
			break;
		}
	}
	
	private void readBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		setImageBitmap(BitmapFactory.decodeStream(is, null, opt));
	}
}
