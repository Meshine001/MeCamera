package com.meshine.mecamera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.CameraUtil;
import com.meshine.mecamera.view.FaceOverlayView;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

@WindowFeature({ Window.FEATURE_NO_TITLE})
@Fullscreen
@EActivity(R.layout.activity_camera_regist)
public class RegistCameraActivity extends Activity implements SurfaceHolder.Callback,
PreviewCallback{
	Camera mCamera = null;
	int CURRENT_CAMERA = CameraInfo.CAMERA_FACING_FRONT;

	SurfaceView mSurfaceView;
	SurfaceHolder mSurfaceHolder;
	int mDisplayRotation;
	int mDisplayOrientation;
	int mCameraOrientation;
	float rate;

	FaceOverlayView mFaceView;

	@ViewById(R.id.regist_capture)
	ImageView mCapture;
	@ViewById(R.id.regist_topbar)
	Topbar topbar;
	@ViewById(R.id.regist_switch)
	Switch modeSwitch;
	
	
	@AfterViews
	void afterViews() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		AgentApplication.getInstance().addActivity(this);
		initTopbar();
		initSurface();
	}
	
	@Click(R.id.regist_capture)
	void captureClick(){
		mCamera.takePicture(null, null, pictureJpeg);
	}
	
	@CheckedChange(R.id.regist_switch)
	void modeSwitch(){
//		if(modeSwitch.isChecked()){
//			mCapture.setVisibility(View.GONE);
//		}else{
//			mCapture.setVisibility(View.VISIBLE);
//		}
	}
	

	
	PictureCallback pictureJpeg = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			Intent intent = new Intent(RegistCameraActivity.this, DisplayActivity_.class);
			intent.putExtra("picture", data);
			intent.putExtra("isRegist", true);
			intent.putExtra("currentCamera", CURRENT_CAMERA);
			startActivity(intent);
		}
	}; 
	
//	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
//		
//		@Override
//		public void onAutoFocus(boolean success, Camera camera) {
//			// TODO Auto-generated method stub
//			if(success){
//				
//			}else{
//				
//			}
//		}
//	};
//	
	void initTopbar(){
		topbar.setOnTopbarClickListener(new TopbarClickListener() {
			
			@Override
			public void rightClick() {
				// TODO Auto-generated method stub
				switchCamera();
			}
			
			@Override
			public void leftClick() {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	void initSurface() {
		mSurfaceView = (SurfaceView) findViewById(R.id.regist_surfacerview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mFaceView = new FaceOverlayView(this);
		addContentView(mFaceView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}
	
	void switchCamera(){
		releaseCamera();
		if (CURRENT_CAMERA == CameraInfo.CAMERA_FACING_FRONT){
			CURRENT_CAMERA = CameraInfo.CAMERA_FACING_BACK;
		}else {
			CURRENT_CAMERA = CameraInfo.CAMERA_FACING_FRONT;
		}
		mCamera = CameraUtil.getCameraInstance(CURRENT_CAMERA);
		initCameraParameters();
		startPreview();
	}

	void releaseCamera() {
		if (mCamera != null) {
			try {
				mCamera.stopPreview();
				mCamera.setPreviewDisplay(null);
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	void initCameraParameters() {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPictureFormat(ImageFormat.JPEG);
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		// for(Size s:previewSizes){
		// System.out.println("("+s.width+","+s.height+")");
		// }
		rate = CameraUtil.getScreenRate(RegistCameraActivity.this);
		Size previewSize = CameraUtil.getPropPreviewSize(previewSizes, rate,
				800);
		parameters.setPreviewSize(previewSize.width, previewSize.height);
		List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
		Size pictureSize = CameraUtil.getPropPictureSize(pictureSizes, rate,
				800);
		parameters.setPictureSize(pictureSize.width, pictureSize.height);
		if (CURRENT_CAMERA == CameraInfo.CAMERA_FACING_FRONT) {
			mCameraOrientation = 270;
		} else {
			mCameraOrientation = 90;
		}
		parameters.setRotation(mCameraOrientation);
//		parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		mCamera.setParameters(parameters);
	}
	
	void startPreview(){
		mDisplayRotation = CameraUtil.getDisplayRotation(RegistCameraActivity.this);
		mDisplayOrientation = CameraUtil.getDisplayOrientation(
				mDisplayRotation, CURRENT_CAMERA);
		mCamera.setDisplayOrientation(mDisplayOrientation);
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.setPreviewCallback(RegistCameraActivity.this);
//		mCamera.autoFocus(autoFocusCallback);
		mCamera.startPreview();
	}

	void doFacedetect(byte[] data, Camera camera) {
		long start = System.currentTimeMillis();
		Bitmap bmp = CameraUtil.yuvimage2Bitmap(data, camera);
		Matrix matrix = new Matrix();
		matrix.postRotate(270);
		matrix.postScale(-1, 1);
		Bitmap tmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
		bmp.recycle();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int heightScreen = dm.heightPixels;
		int widthScreen = dm.widthPixels;
		Bitmap tmp1 = Bitmap.createScaledBitmap(tmp, widthScreen, heightScreen, true);
		tmp.recycle();
		RectF rect = mFaceView.getRect();
		Bitmap tmp2 = Bitmap.createBitmap(tmp1, (int)rect.left,(int)rect.top, (int)(rect.right-rect.left), (int)(rect.bottom-rect.top));
		tmp1.recycle();
		Bitmap picture = tmp2.copy(Bitmap.Config.RGB_565, true);
		tmp2.recycle();
		
		int N_MAX = 1;
		FaceDetector faceDetector = new FaceDetector(picture.getWidth(),
				picture.getHeight(), N_MAX);
		Face[] faces = new Face[N_MAX];
		faceDetector.findFaces(picture, faces);
		
		
		for (Face f : faces) {
			try {
				PointF midPoint = new PointF();
				f.getMidPoint(midPoint);
				if(f.confidence() < 0.4)
					return;
				findFaceCount++;
				mFaceView.setFaceParameters(midPoint, f.eyesDistance());
				if(findFaceCount == 3){
					showCapture();
					findFaceCount = 0;
				}
				if(modeSwitch.isChecked()){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					picture.compress(CompressFormat.JPEG, 100, out);
					byte[] picBytes = out.toByteArray();
					Intent intent = new Intent(RegistCameraActivity.this, DisplayActivity_.class);
					intent.putExtra("picture", picBytes);
					intent.putExtra("isRegist", true);
					intent.putExtra("auto", true);
					intent.putExtra("currentCamera", CURRENT_CAMERA);
					startActivity(intent);
				}
				
//				System.out.println("YOU:"+midPoint);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				mFaceView.setFaceParameters(null, 0);
				mFaceView.invalidate();
				findFaceCount = 0;
			}finally{
				picture.recycle();
			}
			
			
		}
		
		long end = System.currentTimeMillis();
		System.out.println("耗时：" + (end - start) + " ms");
		
		 Timer timer = new Timer();
		 TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				clearEyeCircle();
			}
		};
		timer.schedule(task, 200);
		
	}
	
	
	@UiThread
	void clearEyeCircle(){
		mFaceView.setFaceParameters(null, 0);
	}
	
	void showCapture(){
		mCapture.setVisibility(View.VISIBLE);
		 Timer timer = new Timer();
		 TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				hiddeCapture();
			}
		};
		timer.schedule(task, 3000);
	}
	
	@UiThread
	void hiddeCapture(){
		mCapture.setVisibility(View.GONE);
	}

	int processCount = 0;
	int findFaceCount = 0;

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		processCount++;
		if (processCount == 30) {
			// System.out.println("Process...");
			 doFacedetect(data, camera);
			processCount = 0;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera == null) {
			mCamera = CameraUtil.getCameraInstance(CURRENT_CAMERA);
			if (mCamera != null) {
				try {
					mCamera.setPreviewDisplay(holder);
					initCameraParameters();
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					showMessage("设置预览失败");
				}
			}
		}
	}
	@UiThread
	void showMessage(String msg) {
		Toast.makeText(RegistCameraActivity.this, msg, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		releaseCamera();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mCamera!=null){
			releaseCamera();
			initSurface();
		}else {
			initSurface();
			if (mCamera == null) {
				mCamera = CameraUtil.getCameraInstance(CURRENT_CAMERA);
				if (mCamera != null) {
					try {
						mCamera.setPreviewDisplay(mSurfaceHolder);
						initCameraParameters();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			startPreview();
		}
		
	}
	
}
