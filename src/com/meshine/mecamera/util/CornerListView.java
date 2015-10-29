package com.meshine.mecamera.util;

import com.meshine.mecamera.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class CornerListView extends ListView {

	public CornerListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CornerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CornerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		 switch (ev.getAction()) {  
	        case MotionEvent.ACTION_DOWN:  
	            int x = (int) ev.getX();  
	            int y = (int) ev.getY();  
	            int itemnum = pointToPosition(x, y);  
	  
	            if (itemnum == 0) {  
	                if (itemnum == (getAdapter().getCount() - 1)) {  
	                    // 只有一项  
	                    setSelector(R.drawable.list_corner_round);  
	                } else {  
	                    // 第一项  
	                    setSelector(R.drawable.list_corner_round_top);  
	                }  
	            } else if (itemnum == (getAdapter().getCount() - 1))  
	                // 最后一项  
	                setSelector(R.drawable.list_corner_round_bottom);  
	            else {  
	                // 中间一项  
	                setSelector(R.drawable.list_corner_round_mid);  
	            }  
	  
	            break;  
	        case MotionEvent.ACTION_UP:  
	            break;  
	        }  
		return super.onInterceptTouchEvent(ev);
	}

}
