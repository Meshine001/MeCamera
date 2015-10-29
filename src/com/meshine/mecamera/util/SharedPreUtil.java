package com.meshine.mecamera.util;

import java.util.HashMap;
import java.util.Map;

import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.model.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreUtil {
	
	
	public static boolean saveSysSet(Context context,SystemSet set){
		try {
			SharedPreferences sp = context.getSharedPreferences("sysSet",Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putInt("imageHeight", set.getImageHeight());
			editor.putInt("imageWidth",set.getImageWidth());
			editor.putInt("maxHeight", set.getMaxHeight());
			editor.putInt("maxWidth", set.getMaxWidth());
			editor.putBoolean("registed", set.isRegisted());
			editor.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static SystemSet getSysSet(Context context){
		SystemSet set = new SystemSet();
		SharedPreferences sysSet = context.getSharedPreferences("sysSet", Context.MODE_PRIVATE);
		set.setImageHeight(sysSet.getInt("imageHeight", 128));
		set.setImageWidth(sysSet.getInt("imageWidth", 96));
		set.setMaxHeight(sysSet.getInt("maxHeight", 0));
		set.setMaxWidth(sysSet.getInt("maxWidth", 0));
		set.setRegisted(sysSet.getBoolean("registed", false));
		return set;
	}
	
	public static void cleanUserInfo(Context context){
		User u = new User();
		saveUserInfo(context, u);
	}
	
	public static void cleanSysSet(Context context){
		SystemSet set = new SystemSet();
		saveSysSet(context, set);
	}
	
	

	public static boolean saveUserInfo(Context context,User user){
		try {
			SharedPreferences sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("preViewUrl", user.getPreViewUrl());
			editor.putString("headUrl", user.getHeadUrl());
			editor.putString("nickname", user.getNickname());
			editor.putString("username",user.getUsername());
			editor.putString("password", user.getUserpassword());
			editor.putString("tel", user.getTel());
			editor.putString("email", user.getEmail());
			editor.putString("location", user.getLocation());
			editor.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	
	public static User getUserInfo(Context context){
		User u = new User();
		SharedPreferences userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		u.setUsername(userInfo.getString("username", null));
		u.setUserpassword( userInfo.getString("password", null));
		u.setHeadUrl(userInfo.getString("headUrl", null));
		u.setNickname(userInfo.getString("nickname", null));
		u.setTel(userInfo.getString("tel", null));
		u.setEmail(userInfo.getString("email", null));
		u.setLocation(userInfo.getString("location", null));
		u.setPreViewUrl(userInfo.getString("preViewUrl", null));
		return u;
	}

}
