package com.meshine.mecamera.util;

import java.util.Comparator;

import android.hardware.Camera;
import android.hardware.Camera.Size;

public class CameraSizeComparator implements Comparator<Camera.Size>{

	@Override
	public int compare(Size lhs, Size rhs) {
		// TODO Auto-generated method stub
		if(lhs.width == rhs.width){
			return 0;
		}
		else if(lhs.width > rhs.width){
			return 1;
		}
		else{
			return -1;
		}
	}

}
