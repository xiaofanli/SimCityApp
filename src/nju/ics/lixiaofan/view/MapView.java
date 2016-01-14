package nju.ics.lixiaofan.view;

import com.example.simcity.Section.Crossing;
import com.example.simcity.Section.Street;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

public class MapView extends ViewGroup{
	public static boolean showSections = false;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;
	
	public MapView(Context context) {
		super(context);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		System.out.println(l+" "+t+" "+r+" "+b);
		int cw = 0, ch = 0, x = 0, y = 0, childCount = getChildCount();
		for(int i = 0;i < childCount;i++){
			View child = getChildAt(i);
			cw = child.getMeasuredWidth();
			ch = child.getMeasuredHeight();
			if(child instanceof StreetView){
				StreetView view = (StreetView) child;
				int quotient = view.id / 8;
				int remainder = view.id % 8;
				//vertical streets
				if(view.isVertical){
					switch (quotient) {
					case 0:case 2:
						x = (remainder-1) * (CrossingView.SIZE+StreetView.WIDTH);
						y = (quotient==0) ? 0:(CrossingView.SIZE+StreetView.WIDTH)*2+(StreetView.HEIGHT+CrossingView.SIZE)/2;
						if(view.id == 21)
							y -= (StreetView.HEIGHT+CrossingView.SIZE)/2;
						break;
					case 1:case 3:
						x = (remainder-2) * (CrossingView.SIZE+StreetView.WIDTH);
						y = quotient*(CrossingView.SIZE+StreetView.WIDTH)+(StreetView.HEIGHT+CrossingView.SIZE)/2;
						if(view.id == 10 || view.id == 26)
							y -=(StreetView.HEIGHT+CrossingView.SIZE)/2;
						break;
					}
				}
				//horizontal streets
				else{
					switch(remainder){
					case 6:
						x = 0;
						y = (quotient+1) * (CrossingView.SIZE+StreetView.WIDTH);
						break;
					case 7:
						x = (remainder-6)*(CrossingView.SIZE+StreetView.WIDTH)+(StreetView.HEIGHT+CrossingView.SIZE)/2;
						y = (quotient+1) * (CrossingView.SIZE+StreetView.WIDTH);
						if(view.id == 31)
							x += StreetView.WIDTH+(CrossingView.SIZE-StreetView.HEIGHT)/2;
						break;
					case 0:case 1:
						if(view.id > 1){
							x = (remainder+2)*(CrossingView.SIZE+StreetView.WIDTH)+(StreetView.HEIGHT+CrossingView.SIZE)/2;
							y = quotient * (CrossingView.SIZE+StreetView.WIDTH);
						}
						else{
							x = (remainder*2+1)*(CrossingView.SIZE+StreetView.WIDTH);
							y = 0;
						}
						break;
					}
				}
				view.coord.x = x;
				view.coord.y = y;
				view.coord.centerX = view.coord.x + view.coord.w / 2;
				view.coord.centerY = view.coord.y + view.coord.h / 2;
			}
			else if(child instanceof CrossingView){
				CrossingView view = (CrossingView) child;
				x = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH + 
						(view.id%3) * (CrossingView.SIZE + StreetView.WIDTH);
				y = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH + 
						(view.id/3) * (CrossingView.SIZE + StreetView.WIDTH);
				view.coord.x = x;
				view.coord.y = y;
				view.coord.centerX = view.coord.x + view.coord.w / 2;
				view.coord.centerY = view.coord.y + view.coord.h / 2;
			}
			else if(child instanceof BuildingView){
				int block = ((BuildingView) child).building.block;
				x = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH - BuildingView.SIZE;
				y = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH - BuildingView.SIZE;
				int u = BuildingView.SIZE + CrossingView.SIZE;
				x += (block % 4) * u;
				y += (block / 4) * u;
			}
			else if(child instanceof CitizenView){
				x = (int) (((CitizenView) child).ratioX * getWidth());
				y = (int) (((CitizenView) child).ratioY * getHeight());
			}
			else if(child instanceof BalloonView){
				BalloonView view = (BalloonView) child;
				Coord coord = view.section instanceof Crossing ? ((Crossing) view.section).view.coord
						: ((Street) view.section).view.coord;
				x = coord.centerX - BalloonView.SIZE / 2;
				y = coord.centerY - BalloonView.SIZE;
			}
			
			x += xOffset;
			y += yOffset;
			child.layout((int) (x * mScaleFactor), (int) (y * mScaleFactor),
					(int) ((x + cw) * mScaleFactor), (int) ((y + ch) * mScaleFactor));
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
	}
	
	private int xOffset = 0, yOffset = 0;
	private float dX, dY;
	
	public void setOffset(int x, int y){
		xOffset = x;
		yOffset = y;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
	    switch (event.getActionMasked()) {
        case MotionEvent.ACTION_MOVE:
        	if(touchPoint == 1){
	        	xOffset += event.getX() - dX;
	        	yOffset += event.getY() - dY;
	        	dX = event.getX();
	        	dY = event.getY();
	        	requestLayout();
        	}
            break;
	    }
		return true;
	}
	
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_MOVE:
			return true;
		default:
			break;
		}
		return false;
	}
	
	private int touchPoint = 0;
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
//			Log.i("touchPoint", "ACTION_DOWN");
//			Log.i("touchPoint", event.getX()+" "+event.getY());
			dX = event.getX();
            dY = event.getY();
			touchPoint = 1;
//			Log.i("touchPoint", touchPoint+"");
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			touchPoint++;
//			Log.i("touchPoint", touchPoint+"");
			break;
		case MotionEvent.ACTION_POINTER_UP:
//			Log.i("touchPoint", "ACTION_POINTER_UP");
//			Log.i("touchPoint", event.getX()+" "+event.getY());
			touchPoint--;
			if(touchPoint == 1){
				dX = event.getX(1-event.getActionIndex());
	            dY = event.getY(1-event.getActionIndex());
			}
//			Log.i("touchPoint", touchPoint+"");
			break;
		case MotionEvent.ACTION_UP:
			touchPoint = 0;
//			Log.i("touchPoint", touchPoint+"");
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    public boolean onScale(ScaleGestureDetector detector) {
//	    	Log.i("SCALE", "onScale " + detector.getScaleFactor());
	        mScaleFactor *= detector.getScaleFactor();
	        // Don't let the object get too small or too large.
	        mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 2.0f));
	        requestLayout();
//	        invalidate();
	        return true;
	    }
	}
	
	public float getScaleFactor(){
		return mScaleFactor;
	}
	
	public void setScaleFactor(float factor){
		mScaleFactor = factor;
	}
	
	public void drawSections(){
		int childCount = getChildCount();
		for(int i = 0;i < childCount;i++){
			View child = getChildAt(i);
			if(child instanceof StreetView || child instanceof CrossingView)
				child.invalidate();
		}
	}
	
	public static class Coord{
		public int x, y, w, h;
		public float arcw, arch;
		public int centerX, centerY;
	}
}
