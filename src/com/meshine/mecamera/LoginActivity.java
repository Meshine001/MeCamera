package com.meshine.mecamera;

import java.io.File;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.rest.RestService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;







import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.UserService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@ViewById(R.id.log_username)
	EditText txtUsername;

	@ViewById(R.id.log_password)
	EditText txtPassword;

	@ViewById(R.id.log_login)
	Button btnLogin;
	@ViewById(R.id.log_regist)
	TextView regist;
	@ViewById(R.id.log_forget_password)
	TextView forgetPassword;

	@AfterViews
	void afterViews() {
		AgentApplication.getInstance().exit();
		AgentApplication.getInstance().addActivity(this);
		getUserInfo();
	}
	
	@ViewById(R.id.face_progressBar)
	ProgressBar mProgressBar;

	@Click(R.id.log_login)
	void login() {
		if(txtUsername.length() == 0){
			showMessage("用户名不能为空");
			return;
		}
		if(txtPassword.length() == 0){
			showMessage("密码不能为空");
			return;
		}
		startLogin();
		login2Server(txtUsername.getText().toString(), txtPassword.getText()
				.toString());
	}

	@Click(R.id.log_regist)
	void regist() {
		Intent intent = new Intent(LoginActivity.this, RegistCameraActivity_.class);
		startActivity(intent);
	}
	
	@Click(R.id.log_forget_password)
	void fogetPassword(){
		showMessage("还未开通此服务");
	}

	@RestService
	UserService userService;

	@Background
	void login2Server(String username, String password) {

		String result = null;
		try {
			result = userService.login(username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			endLogin();
			showMessage("无法连接到服务器");
		}
		String state = null;
		try {
			JSONObject jo = new JSONObject(result);
			state = jo.getString("state");
		} catch (Exception e) {
			// TODO Auto-generated catch block
//				e.printStackTrace();
			endLogin();
			showMessage("无法连接到服务器");
			return;
		}
		if (!state.equals("success")) {
			endLogin();
			showMessage(state);
			return;
		}
		
		endLogin();
		
		loginSuccess(username, password);
	}
	
	@UiThread
	void endLogin(){
		mProgressBar.setIndeterminate(false);
		mProgressBar.setVisibility(View.GONE);
		btnLogin.setEnabled(true);
	}
	
	@UiThread
	void startLogin(){
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
		btnLogin.setEnabled(false);
	}
	@UiThread
	void loginSuccess(String username, String password) {
//		 保存登录数据
		SystemSet set = SharedPreUtil.getSysSet(LoginActivity.this);
		if(!set.isRegisted()){
			User u = new User(username, password, null);
			SharedPreUtil.saveUserInfo(LoginActivity.this, u);
		}else {
			txtUsername.setText("");
			txtPassword.setText("");
		}
		set.setRegisted(true);
		SharedPreUtil.saveSysSet(LoginActivity.this, set);
		
		Intent intent = new Intent(LoginActivity.this, CameraActivity_.class);
		startActivity(intent);
	}

	@UiThread
	void showMessage(String msg) {
		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getUserInfo();
		super.onResume();
	}

	void getUserInfo() {
		User u = SharedPreUtil
				.getUserInfo(LoginActivity.this);
		String username = u.getUsername();
		String password = u.getUserpassword();
		if ((username != null) && (password != null)) {
			txtUsername.setText(username);
			txtPassword.setText(password);
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.setIndeterminate(true);
			btnLogin.setEnabled(false);
			login2Server(username, password);
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

}
