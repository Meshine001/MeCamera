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
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera.CameraInfo;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.CameraUtil;
import com.meshine.mecamera.util.Constant;
import com.meshine.mecamera.util.MediaFileUtil;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.UserService;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;


@WindowFeature({Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_display)
public class DisplayActivity extends Activity{
	@ViewById(R.id.disp_progressBar)
	ProgressBar mProgressBar;
	
	@ViewById(R.id.disp_topbar)
	Topbar topbar;
	@ViewById(R.id.disp_pictrue)
	ImageView dispView;
	@ViewById(R.id.disp_send)
	ImageButton submit;
	@ViewById(R.id.disp_result)
	TextView tvResult;
	@ViewById(R.id.disp_tip)
	TextView tvTip;
	@Extra("picture")
	byte[] picture;
	@Extra("isRegist")
	boolean isRegist;
	@Extra("currentCamera")
	int cameraId;
	@Extra("auto")
	boolean auto;
	
//	@Extra("picture")
//	String picture;
	Bitmap dispImage;
	Bitmap calImage;
	
	@AfterViews
	void afterViews(){
		AgentApplication.getInstance().addActivity(this);
		initTopbar();
		previewPicture();
	}
	
	@Click(R.id.disp_send)
	void submitClick(){
		if(isRegist){
			Bitmap bm = Bitmap.createScaledBitmap(dispImage, 300, 300, true);
			File file = MediaFileUtil.saveMediaFile(bm, Constant.MEDIA_TYPE_IMAGE_HEAD_DISPLAY);
			calImage = Bitmap.createScaledBitmap(dispImage, 96, 128, true);
			File file1 = MediaFileUtil.saveMediaFile(calImage, Constant.MEDIA_TYPE_IMAGE_HEAD);
			Intent intent = new Intent(DisplayActivity.this,RegistActivity_.class);
			intent.putExtra("dispImage", file.getAbsolutePath());
			intent.putExtra("calImage", file1.getAbsolutePath());
			startActivity(intent);
		}else {
			Bitmap bm = Bitmap.createScaledBitmap(dispImage, 300, 400, true);
			File file = MediaFileUtil.saveMediaFile(bm, Constant.MEDIA_TYPE_IMAGE_NORMAL_DISPLAY);
			calImage = Bitmap.createScaledBitmap(dispImage, 96, 128, true);
			File file1 = MediaFileUtil.saveMediaFile(calImage, Constant.MEDIA_TYPE_IMAGE_NORMAL);
			
			
			startUpload();
			upload(file.getAbsolutePath(), file1.getAbsolutePath());
		}
	}
	
	@RestService
	UserService userService;
	
	@Background
	void upload(String dispFile,String file) {
		
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		
		FileSystemResource dispImage = new FileSystemResource(dispFile);
		FileSystemResource image = new FileSystemResource(file);
//		String description = "游客图片";
//		String title = "游客图片";

		data.set("dispImage", dispImage);
		data.set("image", image);
//		data.set("description", description);
//		data.set("title", title);
		
		User u = SharedPreUtil
				.getUserInfo(DisplayActivity.this);
		String username = u.getUsername();
		
		String result = null;
		try {
			 result = userService.upload(data,username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			showMessage("上传失败");
			endUpload();
			return;
		}
		
		showResult(result);
		
	}
	
	void previewPicture(){
		Bitmap bmp = BitmapFactory.decodeByteArray(picture, 0, picture.length);
		Bitmap tmp;
		Bitmap bitmap;
		if(cameraId == CameraInfo.CAMERA_FACING_FRONT && auto != true){
			Matrix matrix = new Matrix();
			matrix.postScale(-1, 1);
			tmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			bmp.recycle();
			
			bitmap = cropPicture(tmp);
			if(bitmap == null){
				dispView.setImageBitmap(tmp);
				tvTip.setVisibility(View.VISIBLE);
			}else {
				dispView.setImageBitmap(bitmap);
				dispImage = bitmap;
			}
			
		}else {
			bitmap = cropPicture(bmp);
			if(bitmap == null){
				dispView.setImageBitmap(bmp);
				tvTip.setVisibility(View.VISIBLE);
			}else {
				dispView.setImageBitmap(bitmap);
				dispImage = bitmap;
			}
		}
		
//		Bitmap bmp = BitmapFactory.decodeFile(picture);
//		dispView.setImageBitmap(bmp);
	}
	
	@UiThread
	void startUpload(){
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
		submit.setEnabled(false);
	}
	
	@UiThread
	void endUpload(){
		mProgressBar.setIndeterminate(false);
		mProgressBar.setVisibility(View.GONE);
		submit.setEnabled(true);
	}
	
	@UiThread
	void showResult(String result){
		endUpload();
		topbar.setTileText("结果");
		dispView.setVisibility(View.GONE);
		submit.setVisibility(View.GONE);
		tvResult.setText(result);
		tvResult.setVisibility(View.VISIBLE);
	}
	
	@UiThread
	void showMessage(String msg){
		Toast.makeText(DisplayActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	Bitmap cropPicture(Bitmap bmp){
		Bitmap tmp = bmp.copy(Bitmap.Config.RGB_565, true);
//		System.out.println(tmp.getWidth()+","+tmp.getHeight());
		int N_MAX = 1;
		FaceDetector faceDetector = new FaceDetector(tmp.getWidth(), tmp.getHeight(), N_MAX);
		Face[] faces = new Face[N_MAX];
		faceDetector.findFaces(tmp, faces);
		for(Face f:faces){
			PointF midPoint = new PointF();
			try {
				if(f.confidence() < 0.3)
					return null;
				
				f.getMidPoint(midPoint);
				Face face = f;
				float d = face.eyesDistance();
//				float x = (float) (midPoint.x - d*1.7);
//				float x = (float) (midPoint.x - d*0.8);
				float x = (float) (midPoint.x - d*1);
				float y = (float) (midPoint.y - d*1.2);
				float width = (float) (d*2);
				float height = (float) (d*2.7);
				Bitmap pictrue = Bitmap.createBitmap(tmp, (int)x, (int)y, (int)width, (int)height);
				return pictrue;
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				return null;
			}
		}
		return null;
		
		
	}
	
	void initTopbar(){
		topbar.setOnTopbarClickListener(new TopbarClickListener() {
			
			@Override
			public void rightClick() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void leftClick() {
				finish();
			}
		});
	}
}
