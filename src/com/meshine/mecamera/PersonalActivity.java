package com.meshine.mecamera;

import java.util.ArrayList;
import java.util.List;

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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.model.User;
import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.SysSetAdapter;
import com.meshine.mecamera.util.UserInfoAdapter;
import com.meshine.mecamera.util.UserService;
import com.meshine.mecamera.view.CircleImageView;
import com.meshine.mecamera.view.Topbar;
import com.meshine.mecamera.view.Topbar.TopbarClickListener;

@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_pesonal)
public class PersonalActivity extends Activity {

	@ViewById(R.id.per_topbar)
	Topbar topbar;

	@ViewById(R.id.per_head)
	CircleImageView headView;

	@ViewById(R.id.per_infolist)
	ListView infolist;

	@ViewById(R.id.per_syslist)
	ListView sysSetList;

	@ViewById(R.id.per_logout)
	Button btnLogout;

	List<String> userInfo;
	UserInfoAdapter mUserInfoAdapter;

	Bitmap mHead = null;
	String username;
	String nickname = "获取中";
	String email = "获取中";
	String tel = "获取中";
	String location = "获取中";

	@AfterViews
	void afterViews() {
		initTopbar();
		getInfoFromLocal();
		initSysSetList();
		initInfoList();
//		getHead(username);
//		getUserInfo(username);
	}
	
	void getInfoFromLocal(){
		User u = SharedPreUtil
				.getUserInfo(PersonalActivity.this);
		username = u.getUsername();
		nickname = u.getNickname();
		email = u.getEmail();
		tel = u.getTel();
		location = u.getLocation();
		headView.setImageBitmap(BitmapFactory.decodeFile(u.getHeadUrl()));
	}

	void initSysSetList() {
		SystemSet sysSet = SharedPreUtil
				.getSysSet(PersonalActivity.this);
		int imageHeigt = sysSet.getImageHeight();
		int imageWidth = sysSet.getImageWidth();
		List<String> sysSets = new ArrayList<String>();
//		sysSets.add(""+imageHeigt+"x"+imageWidth);
		sysSets.add("128x96");
		sysSets.add(" ");
		SysSetAdapter sysSetAdapter = new SysSetAdapter(PersonalActivity.this,
				sysSets);
		sysSetList.setAdapter(sysSetAdapter);
//		sysSetList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				switch (position) {
//				case 0:
////					setSysset("图片大小");
//					break;
//				case 1:
//					
//					break;
//
//				default:
//					break;
//				}
//				
//			}
//		});
	}

	void initTopbar() {
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

	@Click(R.id.per_logout)
	void logout() {
		SharedPreUtil.cleanUserInfo(PersonalActivity.this);
		SharedPreUtil.cleanSysSet(PersonalActivity.this);
		AgentApplication.getInstance().exit();
		finish();
	}

	void initInfoList() {
		userInfo = new ArrayList<String>();
		userInfo.add(nickname);
		userInfo.add(username);
		userInfo.add(email);
		userInfo.add(tel);
		userInfo.add(location);

		mUserInfoAdapter = new UserInfoAdapter(PersonalActivity.this, userInfo);

		infolist.setAdapter(mUserInfoAdapter);

		infolist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					setInfo("昵称", username);
//					Toast.makeText(PersonalActivity.this, "设置昵称",
//							Toast.LENGTH_SHORT).show();
					break;
				case 2:
					setInfo("Email", username);
//					Toast.makeText(PersonalActivity.this, "设置Email",
//							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					setInfo("手机", username);
//					Toast.makeText(PersonalActivity.this, "设置手机",
//							Toast.LENGTH_SHORT).show();
					break;
				case 4:
					setInfo("地区", username);
//					Toast.makeText(PersonalActivity.this, "设置地区",
//							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

			}
		});
	}
	


	@RestService
	UserService userService;
	
	

	@Background
	void getHead(String username) {
		byte[] data = null;
		try {
			data = userService.getHead(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			showMessage("无法连接到服务器");
			return;
		}
		setHead(data);
	}

	@Background
	void getUserInfo(String username) {
		String result = null;
		try {
			result = userService.getUserInfo(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			showMessage("无法连接到服务器");
			return;
		}
		setUserInfo(result);
	}

	@Click(R.id.per_head)
	void gotoSetHead(){
		Intent intent = new Intent(PersonalActivity.this, HeadActivity_.class);
		intent.putExtra("username", username);
		startActivity(intent);
	}
	
	@UiThread
	void setUserInfo(String info) {
		try {
			JSONObject jo = new JSONObject(info);
			nickname = jo.getString("nickname");
			email = jo.getString("email");
			tel = jo.getString("tel");
			location = jo.getString("location");
			initInfoList();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@UiThread
	void setHead(byte[] data) {
		mHead = BitmapFactory.decodeByteArray(data, 0, data.length);
		headView.setImageBitmap(mHead);
	}

	@UiThread
	void showMessage(String msg) {
		Toast.makeText(PersonalActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onResume() {
		
		initTopbar();
		getInfoFromLocal();
		initSysSetList();
		initInfoList();
		getHead(username);
		getUserInfo(username);
		super.onResume();
	}

	void setInfo(String title,String username){
		Intent intent = new Intent(PersonalActivity.this, InfoActiviy_.class);
		intent.putExtra("title", title);
		intent.putExtra("username", username);
		startActivity(intent);
	}
	
	void setSysset(String title){
		Intent intent = new Intent(PersonalActivity.this, InfoActiviy_.class);
		intent.putExtra("title", title);
		startActivity(intent);
	}
}
