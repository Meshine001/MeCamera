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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.Constant;
import com.meshine.mecamera.util.MediaFileUtil;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.UserService;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_head)
public class HeadActivity extends Activity {

	@ViewById(R.id.head_progressBar)
	ProgressBar mProgressBar;
	
	@ViewById(R.id.head_topbar)
	Topbar mTopbar;
	@ViewById(R.id.head_img)
	ImageView mHead;
	@ViewById(R.id.head_submit)
	Button btnSubmit;

	@Extra("username")
	String username;
	
	File temp;
	Bitmap photo;

	String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@AfterViews
	void afterViews() {
		initTopbar();
		showIMGDialog();
	}

	@Click(R.id.head_img)
	void headImageClick() {
		showIMGDialog();
	}
	
	@Click(R.id.head_submit)
	void btnSubmitClick(){
		startUpload();
		uploadHead(photo, username);
	} 
	@UiThread
	void startUpload(){
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
		btnSubmit.setEnabled(false);
	}
	
	@UiThread
	void endUpload(){
		mProgressBar.setIndeterminate(false);
		mProgressBar.setVisibility(View.GONE);
		btnSubmit.setEnabled(true);
	}
	void initTopbar() {
		mTopbar.setOnTopbarClickListener(new TopbarClickListener() {

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

	void showIMGDialog() {
		new AlertDialog.Builder(HeadActivity.this).setTitle("设置头像")
				.setItems(items, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;

						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							if (MediaFileUtil.hasSdcard()) {
								temp = MediaFileUtil
										.getOutputMediaFile(Constant.MEDIA_TYPE_IMAGE_HEAD);
								Uri uri = Uri.fromFile(temp);
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT, uri);
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						default:
							break;
						}

					}
				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (MediaFileUtil.hasSdcard()) {
					// File tempFile = new File(uri);
					startPhotoZoom(Uri.fromFile(temp));
				} else {
					Toast.makeText(HeadActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", Constant.HEAD_ASPECT_X);
		intent.putExtra("aspectY", Constant.HEAD_ASPECT_Y);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", Constant.HEAD_OUTPUT_X);
		intent.putExtra("outputY", Constant.HEAD_OUTPUT_Y);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			mHead.setImageDrawable(drawable);
			startUpload();
			uploadHead(photo, username);
		}
	}

	@RestService
	UserService userService;

	@Background
	void uploadHead(Bitmap bitmap, String username) {
		File file = MediaFileUtil.saveMediaFile(bitmap,Constant.MEDIA_TYPE_IMAGE_HEAD_DISPLAY);
		MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
		FileSystemResource image = new FileSystemResource(file);
		data.set("dispImage", image);
		String result = null;
		try {
			result = userService.updateHeadOnly(data, username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			endUpload();
			showMessage("无法连接到服务器");
			return;
		}
		
		showResult(result);

	}

	@UiThread
	void showResult(String result){
		if("success".equals(result)){
			User u = SharedPreUtil.getUserInfo(HeadActivity.this);
			File file = MediaFileUtil.saveMediaFile(photo,Constant.MEDIA_TYPE_IMAGE_HEAD_DISPLAY);
			u.setHeadUrl(file.getAbsolutePath());
			SharedPreUtil.saveUserInfo(HeadActivity.this, u);
			endUpload();
			finish();
		}else {
			showMessage("修改头像失败");
			endUpload();
		}
	}
	
	@UiThread
	void showMessage(String msg) {
		Toast.makeText(HeadActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}
