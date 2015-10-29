package com.meshine.mecamera;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.rest.RestService;

import android.app.Activity;
import android.text.InputType;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.UserService;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_info)
public class InfoActiviy extends Activity{
	@ViewById(R.id.info_topbar)
	Topbar mTopbar;
	@ViewById(R.id.info_item)
	EditText item;
	@ViewById(R.id.info_submit)
	Button btnSubmit;
	
	@Extra("title")
	String title;
	@Extra("username")
	String username;
	
	
	
	
	@AfterViews
	void afterExtras(){
		initUI();
	}
	
	void initUI(){
		initTopbar();
		initInputItem();
	}
	
	void initInputItem(){
		item.setHint("请输入"+title);
		if("Email".equals(title)){
			item.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		}
		if("手机".equals(title)){
			item.setInputType(InputType.TYPE_CLASS_PHONE);
		}
	}
	
	void initTopbar(){
		mTopbar.setTileText("设置"+title);
		mTopbar.setOnTopbarClickListener(new TopbarClickListener() {
			
			@Override
			public void rightClick() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void leftClick() {
				finishActivity();
			}
		});
	}
	
	@Click(R.id.info_submit)
	void submit(){
		if("昵称".equals(title)){
			updateNick2Server(username, item.getText().toString());
		}
		if("Email".equals(title)){
			System.out.println("asdasd:"+item.getText().toString());
			updateEmail2Server(username, item.getText().toString());
		}
		if("手机".equals(title)){
			updateTel2Server(username, item.getText().toString());
		}
		if("地区".equals(title)){
			updateLocation2Server(username, item.getText().toString());
		}
		
		if("图片大小".equals(title)){
			
			if(item.length() == 0){
				showMessage("请输入图片大小");
				return;
			}
			
			SystemSet set = SharedPreUtil.getSysSet(InfoActiviy.this);
			int maxHeight = set.getMaxHeight();
			int maxWidth = set.getMaxWidth();
			String size = item.getText().toString();
			int imageHeight = 0;
			int imageWidth = 0;
			try {
				imageHeight = Integer.parseInt(size.split("x")[0]);
				imageWidth = Integer.parseInt(size.split("x")[1]);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				showMessage("输入格式错误，请输入如 128x96 格式");
			}
			if((maxHeight < imageHeight) ||(maxWidth < imageWidth)){
				showMessage("本机支持最大尺寸:"+maxHeight+"x"+maxWidth);
				return;
			}
			set.setImageHeight(imageHeight);
			set.setImageWidth(imageWidth);
			SharedPreUtil.saveSysSet(InfoActiviy.this, set);
			finish();
		}
		
	}
	
	@UiThread
	void finishActivity(){
		finish();
	}
	
	@RestService
	UserService userService;
	
	@Background
	void updateNick2Server(String username, String nickname) {
		String result = userService.updateNickname(username, nickname);
		if (result.equals("success")) {
			User u = SharedPreUtil.getUserInfo(InfoActiviy.this);
			u.setNickname(nickname);
			SharedPreUtil.saveUserInfo(InfoActiviy.this, u);
			finishActivity();
		} else {
			showMessage("设置失败");
		}
	}
	
	@Background
	void updateEmail2Server(String username, String email) {
		String result = userService.updateEmail(username, email);
		if (result.equals("success")) {
			User u = SharedPreUtil.getUserInfo(InfoActiviy.this);
			u.setEmail(email);
			SharedPreUtil.saveUserInfo(InfoActiviy.this, u);
			finishActivity();
		} else {
			showMessage("设置失败");
		}
	}
	
	@Background
	void updateTel2Server(String username, String tel) {
		String result = userService.updateTel(username, tel);
		if (result.equals("success")) {
			User u = SharedPreUtil.getUserInfo(InfoActiviy.this);
			u.setTel(tel);
			SharedPreUtil.saveUserInfo(InfoActiviy.this, u);
			finishActivity();
		} else {
			showMessage("设置失败");
		}
	}
	
	@Background
	void updateLocation2Server(String username, String location) {
		String result = userService.updateLocation(username, location);
		if (result.equals("success")) {
			User u = SharedPreUtil.getUserInfo(InfoActiviy.this);
			u.setLocation(location);
			SharedPreUtil.saveUserInfo(InfoActiviy.this, u);
			finishActivity();
		} else {
			showMessage("设置失败");
		}
	}
	

	@UiThread
	void showMessage(String msg) {
		Toast.makeText(InfoActiviy.this, msg, Toast.LENGTH_SHORT).show();
	}

}
