package nju.ics.lixiaofan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MapView extends ViewGroup{
	public static boolean showSections = false;
	
	public MapView(Context context) {
		super(context);
	}
	
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		System.out.println(l+" "+t+" "+r+" "+b);
		int cw = 0, ch = 0, x = 0, y = 0, id, childCount = getChildCount();
		for(int i = 0;i < childCount;i++){
			View child = getChildAt(i);
			cw = child.getMeasuredWidth();
			ch = child.getMeasuredHeight();
			if(child instanceof StreetView){
				id = ((StreetView)child).id;
				int quotient = id / 8;
				int remainder = id % 8;
				//vertical streets
				if(((StreetView)child).isVertical){
					switch (quotient) {
					case 0:case 2:
						x = (remainder-1) * (CrossingView.SIZE+StreetView.WIDTH);
						y = (quotient==0) ? 0:(CrossingView.SIZE+StreetView.WIDTH)*2+(StreetView.HEIGHT+CrossingView.SIZE)/2;
						if(id == 21)
							y -= (StreetView.HEIGHT+CrossingView.SIZE)/2;
						break;
					case 1:case 3:
						x = (remainder-2) * (CrossingView.SIZE+StreetView.WIDTH);
						y = quotient*(CrossingView.SIZE+StreetView.WIDTH)+(StreetView.HEIGHT+CrossingView.SIZE)/2;
						if(id == 10 || id == 26)
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
						if(id == 31)
							x += StreetView.WIDTH+(CrossingView.SIZE-StreetView.HEIGHT)/2;
						break;
					case 0:case 1:
						if(id > 1){
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
			}
			else if(child instanceof CrossingView){
				x = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH + 
						(((CrossingView)child).id%3) * (CrossingView.SIZE + StreetView.WIDTH);
				y = (StreetView.HEIGHT+CrossingView.SIZE)/2 + StreetView.WIDTH + 
						(((CrossingView)child).id/3) * (CrossingView.SIZE + StreetView.WIDTH);
			}
			child.layout(x, y, x+cw, y+ch);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		setMeasuredDimension(size, size);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int count = getChildCount();
		for(int i = 0;i < count;i++)
			getChildAt(i).invalidate();
	}
	
	public static class Coord{
		public int x, y, w, h;
		public int arcw ,arch;
		public int centerX, centerY;
	}
}
