package com.meshine.mecamera;

import java.io.File;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.UserService;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_regist)
public class RegistActivity extends Activity {
	
	@ViewById(R.id.reg_topbar)
	Topbar topbar;


	@ViewById(R.id.reg_username)
	EditText txtUsername;

	@ViewById(R.id.reg_userpassword)
	EditText txtPassword;

	@ViewById(R.id.reg_submit)
	Button btnSubmint;

	@ViewById(R.id.reg_checkBox)
	CheckBox checkBox;
	
	@Extra("dispImage")
	String dispImage;
	@Extra("calImage")
	String calImage;
	



	@AfterViews
	void afterView() {
		initTopbar();
	}
	
	void initTopbar(){
		topbar.setOnTopbarClickListener(new TopbarClickListener() {

			@Override
			public void rightClick() {

			}

			@Override
			public void leftClick() {
				finish();
			}
		});
	}


	@ViewById(R.id.reg_progressBar)
	ProgressBar mProgressBar;
	
	@UiThread
	void prograssShow(){
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	@UiThread
	void prograssHide(){
		mProgressBar.setIndeterminate(false);
		mProgressBar.setVisibility(View.GONE);
	}
	
	@Click(R.id.reg_submit)
	void submit() {
		prograssShow();
		
		if (txtUsername.length() == 0) {
			showMessage("用户名不能为空");
			prograssHide();
			return;
		}

		if (txtPassword.length() == 0) {
			prograssHide();
			showMessage("密码不能为空");
			return;
		}
		
		if(!checkBox.isChecked()){
			prograssHide();
			showMessage("请同意用户协议");
			return;
		}

		String username = txtUsername.getText().toString();
		String password = txtPassword.getText().toString();
		checkUser(username, password);
	}

	@RestService
	UserService userService;

	@Background
	void checkUser(String username, String password) {
		String result = null;
		try {
			result = userService.checkUser(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			prograssHide();
			showMessage("无法连接到服务器");
			return;
		}
		if (!result.equals("first")) {
			showMessage("用户已存在");
			prograssHide();
			return;
		}
		addUser(username, password);
	}

	@UiThread
	void showMessage(String msg) {
		Toast.makeText(RegistActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Background
	void addUser(String username, String password) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("username", username);
			jo.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			prograssHide();
			e.printStackTrace();
		}

		String result;
		try {
			result = userService.addUser(jo.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			prograssHide();
			showMessage("注册失败");
			return;
		}

		if (!result.equals("success")) {
			prograssHide();
			showMessage("注册失败！\n" + result);
			return;
		}
		
		uploadHead(dispImage,calImage, username);

	}

	@Background
	void uploadHead(String dispImageUrl,String calImageUrl, String username) {

		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		FileSystemResource dispImage = new FileSystemResource(dispImageUrl);
		FileSystemResource image = new FileSystemResource(calImageUrl);
		data.set("dispImage", dispImage);
		data.set("image", image);
		String result = null;
		try {
			result = userService.updateHead(data, username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			prograssHide();
			showMessage("无法连接到服务器");
			return;
		}

		if (!result.equals("success")) {
			prograssHide();
			showMessage("头像上传失败");
		}

		finishRegist();

	}

	@UiThread
	void finishRegist() {
		
		// 保存注册数据
		User u = new User(txtUsername.getText().toString(), txtPassword.getText().toString(), dispImage);
		u.setHeadUrl(dispImage);
		u.setNickname("新用户");
		u.setTel("未设置");
		u.setEmail("未设置");
		u.setLocation("未设置");
		
		SharedPreUtil.saveUserInfo(RegistActivity.this, u);
		
		SystemSet set = new SystemSet();
		set.setRegisted(true);
		SharedPreUtil.saveSysSet(RegistActivity.this, set);
		
		prograssHide();
		
		AgentApplication.getInstance().exit();
		
		AgentApplication.getInstance().addActivity(this);
		
		Intent intent = new Intent(RegistActivity.this, CameraActivity_.class);
		startActivity(intent);
//		finish();
	}

}
