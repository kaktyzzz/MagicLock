package com.magiclock;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/*
File:              RoundKnobButton
Version:           1.0.0
Release Date:      November, 2013
License:           GPL v2
Description:	   A round knob button to control volume and toggle between two states

****************************************************************************
Copyright (C) 2013 Radu Motisan  <radu.motisan@gmail.com>

http://www.pocketmagic.net

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
****************************************************************************/

public class RoundKnobButton extends RelativeLayout implements OnGestureListener {

	private GestureDetector 	gestureDetector;
	private float 				mAngleDown , mAngleUp;
	private Float				oldAngle = null;
	private ImageView			ivRotor;
	private Bitmap 				bmpRotorOn , bmpRotorOff;
	private boolean 			mState = false;
	private int					m_nWidth = 0, m_nHeight = 0;
	
	private float 				angle = 0;
	private static final float  range = 50;
	private static final float	step = 360f / range;
	private static final float	stepDiv2 = step / 2;
	
	interface RoundKnobButtonListener {
		public void onStateChange(boolean newstate) ;
		public void onRotate(int percentage);
	}
	
	private RoundKnobButtonListener m_listener;
	
	public void SetListener(RoundKnobButtonListener l) {
		m_listener = l;
	}
	
	public void SetState(boolean state) {
		mState = state;
		ivRotor.setImageBitmap(state?bmpRotorOn:bmpRotorOff);
	}
	
	public RoundKnobButton(Context context, int back, int rotoron, int rotoroff, final int w, final int h) {
		super(context);
		// we won't wait for our size to be calculated, we'll just store out fixed size
		m_nWidth = w; 
		m_nHeight = h;
		// create stator
		ImageView ivBack = new ImageView(context);
		ivBack.setImageResource(back);
		RelativeLayout.LayoutParams lp_ivBack = new RelativeLayout.LayoutParams(
				w,h);
		lp_ivBack.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(ivBack, lp_ivBack);
		// load rotor images
		Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotoron);
		Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(), rotoroff);
	    float scaleWidth = ((float) w) / srcon.getWidth();
	    float scaleHeight = ((float) h) / srcon.getHeight();
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);
		    
		bmpRotorOn = Bitmap.createBitmap(
				srcon, 0, 0, 
				srcon.getWidth(),srcon.getHeight() , matrix , true);
		bmpRotorOff = Bitmap.createBitmap(
				srcoff, 0, 0, 
				srcoff.getWidth(),srcoff.getHeight() , matrix , true);
		// create rotor
		ivRotor = new ImageView(context);
		ivRotor.setImageBitmap(bmpRotorOn);
		RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(w,h);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(ivRotor, lp_ivKnob);
		// set initial state
		SetState(mState);
		// enable gesture detector
		gestureDetector = new GestureDetector(getContext(), this);
		
		angle = new Random().nextInt((int)range) * step;
		setRotorPosAngle(angle); // Начальная установка
	}
	
	/**
	 * math..
	 * @param x
	 * @param y
	 * @return
	 */
	private float cartesianToPolar(float x, float y) {
		return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
	}

	
	@Override public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) return true;
		else return super.onTouchEvent(event);
	}
	
	public boolean onDown(MotionEvent event) {
		float x = event.getX() / ((float) getWidth());
		float y = event.getY() / ((float) getHeight());
		mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
		return true;
	}
	
	
	public boolean onSingleTapUp(MotionEvent e) {
		float x = e.getX() / ((float) getWidth());
		float y = e.getY() / ((float) getHeight());
		mAngleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
		
		// if we click up the same place where we clicked down, it's just a button press
		if (! Float.isNaN(mAngleDown) && ! Float.isNaN(mAngleUp) && Math.abs(mAngleUp-mAngleDown) < 10) {
			SetState(!mState);
			if (m_listener != null) m_listener.onStateChange(mState);
		}
		return true;
	}

	public void setRotorPosAngle(float deg) {
		if (deg > 180) deg = deg - 360;
		Matrix matrix=new Matrix();
		ivRotor.setScaleType(ScaleType.MATRIX);   
		matrix.postRotate((float) deg, m_nWidth/2, m_nHeight/2);//getWidth()/2, getHeight()/2);
		ivRotor.setImageMatrix(matrix);
	}
	
	public void setRotorPercentage(int percentage) {
		int posDegree = percentage * 3 - 150;
		if (posDegree < 0) posDegree = 360 + posDegree;
		setRotorPosAngle(posDegree);
	}
	
	public float normalizeAngle(float angle) {
		if (angle < 0) angle += 360;
		if (angle >= 360) angle -= 360;
		return angle;
	}
	
	public float plusnormalizeAngle (float angle) {
		if (angle < 0) angle += 360;
		return angle;
	}
		
	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		float x = e2.getX() / ((float) getWidth());
		float y = e2.getY() / ((float) getHeight());
		float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction 
		rotDegrees = plusnormalizeAngle(rotDegrees);
		if (! Float.isNaN(rotDegrees)) {
			
			float length = rotDegrees - mAngleDown;
			if (length > 180)
				length -= 360;
			if (length < -180)
				length += 360;
			
			
			
			if (Math.abs(length) > stepDiv2) {
				//Log.d("!!", length+" "+angle+" "+rotDegrees+" "+mAngleDown+" " + step);
				if (length < 0) { 
					angle -= step;
					mAngleDown -= step;
				}
				else {
					angle += step;
					mAngleDown += step;
				}
				
				angle = normalizeAngle(angle);
				mAngleDown = normalizeAngle(mAngleDown);
				
			
				setRotorPosAngle(angle);
				
				//if (m_listener != null) m_listener.onRotate((int)(angle/step));
				float position = Math.abs(Math.round(angle/step) - range);
				if (position == range)
					position = 0;
				if (m_listener != null) m_listener.onRotate((int)position);
			}
			return true; //consumed
		} else
			return false; // not consumed
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) { return false; }

	public void onLongPress(MotionEvent e) {	}

	



}
