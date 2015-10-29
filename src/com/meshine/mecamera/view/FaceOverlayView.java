package com.meshine.mecamera.view;

import java.util.Timer;
import java.util.TimerTask;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

public class FaceOverlayView extends View{

	private Paint mPaint;
	private PointF midPoint;
	private float eyeDistance;
	
	private RectF mRect;
	
	public FaceOverlayView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
	}
	
	private void initialize() {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(5);
	}
	
	public void setFaceParameters(PointF midPoint,float eyeDistance){
		this.midPoint = midPoint;
		this.eyeDistance = eyeDistance;
		invalidate();
	}
	
	public RectF getRect(){
		
		return mRect; 
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//Rect
		int mWidth = canvas.getWidth();
		int mHeight = canvas.getHeight();
		int mCentreX = mWidth/2;
		int mCentreY = mHeight/2;
		int dX = mWidth/3;
		int dY = dX*4/3;
		int rectX1 = mCentreX-dX;
		int rectY1 = mCentreY-dY;
		int rectX2 = mCentreX+dX;
		int rectY2 = mCentreY+dY;
		mRect = new RectF(rectX1, rectY1, rectX2, rectY2);
		canvas.drawRect(mRect, mPaint);
//		canvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);
		
		if((midPoint != null) && (eyeDistance != 0) ){
			canvas.save();
			float lEyeX = (midPoint.x-eyeDistance/2)+rectX1;
			float lEyeY = midPoint.y+rectY1;
			float rEyeX = lEyeX+eyeDistance;
			float rEyeY = lEyeY;
			canvas.drawCircle(lEyeX, lEyeY, 25f, mPaint);
			canvas.drawCircle(rEyeX, rEyeY, 25f, mPaint);
		}
		
		canvas.restore();
		
	}

}
