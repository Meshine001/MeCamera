package com.meshine.mecamera.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class MediaFileUtil {
	private static final String TAG = "MediaFileUtil";

	/**
	 * 将彩色图转换为纯黑白二色
	 * 
	 * @param 位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				// 分离三原色
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				// 转化成灰度像素
				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		// 新建图片
		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
		// 设置图片数据
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
		return resizeBmp;
	}

	/** Bitmap encodes to Base64Code **/
	public static String Bitmap2StrByBase64(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 40, out);
		byte[] bytes = out.toByteArray();
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				Constant.TAG);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Mepicture", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		// .format(new Date());
		File mediaFile;
		switch (type) {
		case Constant.MEDIA_TYPE_IMAGE_HEAD:
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "USER_HEAD" + ".jpg");
			break;

		case Constant.MEDIA_TYPE_IMAGE_NORMAL:
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "USER_IMG" + ".jpg");
			break;
		case Constant.MEDIA_TYPE_IMAGE_HEAD_DISPLAY:
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "USER_HEAD_DISP" + ".jpg");
			break;
		case Constant.MEDIA_TYPE_IMAGE_NORMAL_DISPLAY:
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "USER_IMG_DISP" + ".jpg");
			break;
		default:
			return null;
		}

		return mediaFile;
	}

	public static File saveMediaFile(Bitmap bitmap, int type) {
		File file = MediaFileUtil.getOutputMediaFile(type);
		if (file == null) {
			Log.d(TAG, "Error creating media file, check storage permissions");
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, out);
		byte[] picBytes = out.toByteArray();
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(picBytes);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "File not found: " + e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Error accessing file: " + e.getMessage());
			return null;
		}

		return file;
	}

	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
