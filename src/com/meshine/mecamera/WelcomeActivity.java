package com.meshine.mecamera;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.meshine.mecamera.model.SystemSet;
import com.meshine.mecamera.util.AgentApplication;
import com.meshine.mecamera.util.SharedPreUtil;
import com.meshine.mecamera.util.ViewPagerAdapter;


@WindowFeature({ Window.FEATURE_NO_TITLE })
@Fullscreen
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity implements OnClickListener,
OnPageChangeListener{
	
	@ViewById(R.id.welcome_guide)
	ImageView ivGuide;
	@ViewById(R.id.welcome_viewpager)
	ViewPager viewPager;
	@ViewById(R.id.welcome_linearLayout)
	LinearLayout welcome_linearLayout;
	
	ViewPagerAdapter viewPagerAdapter;
	ArrayList<View> views;
	static final int[] pics = { R.drawable.guide1, R.drawable.guide2,
			R.drawable.guide3, R.drawable.guide4 };
	ImageView[] points;
	int currentIndex;
	
	@AfterViews
	void afterViews(){
		AgentApplication.getInstance().addActivity(this);
		SystemSet set = SharedPreUtil.getSysSet(WelcomeActivity.this);
		if(set.isRegisted()){
			Intent intent = new Intent(WelcomeActivity.this, LoginActivity_.class);
			startActivity(intent);
		}else {
			guideAnimation();
		}
		
	}
	
	void guideAnimation(){
		AnimationSet animationSet = new AnimationSet(true);
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				ivGuide.setVisibility(View.GONE);
				views = new ArrayList<View>();
				viewPagerAdapter = new ViewPagerAdapter(views);
				initData();
			}
		});
		animationSet.addAnimation(alphaAnimation);
		ivGuide.startAnimation(alphaAnimation);
		
	}
	
	void initData(){
		 LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
	                LinearLayout.LayoutParams.MATCH_PARENT,
	                LinearLayout.LayoutParams.MATCH_PARENT);
		 for (int i = 0; i < pics.length; i++) {
	            ImageView iv = new ImageView(this);
	            iv.setLayoutParams(mParams);
	            iv.setScaleType(ScaleType.FIT_XY);
	            iv.setImageResource(pics[i]);
	            views.add(iv);
	        }
		 
		 viewPager.setAdapter(viewPagerAdapter);
		 viewPager.setOnPageChangeListener(this);
		 
		 initPoint();
	}

	void initPoint(){
		  points = new ImageView[pics.length];
		  // 循环取得小点图片
	        for (int i = 0; i < pics.length; i++) {
	            // 得到一个LinearLayout下面的每一个子元素
	            points[i] = (ImageView) welcome_linearLayout.getChildAt(i);
	            // 默认都设为灰色
	            points[i].setEnabled(true);
	            // 给每个小点设置监听
	            points[i].setOnClickListener(this);
	            // 设置位置tag，方便取出与当前位置对应
	            points[i].setTag(i);
	        }

	        // 设置当面默认的位置
	        currentIndex = 0;
	        // 设置为白色，即选中状态
	        points[currentIndex].setEnabled(false);
	}
	
	void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }
	
	void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		 setCurDot(arg0);
		 if(arg0 == 3){
			 final Intent intent= new Intent();
			 Timer timer = new Timer();
			 TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					intent.setClass(WelcomeActivity.this, LoginActivity_.class);
					WelcomeActivity.this.startActivity(intent);
				}
			};
			timer.schedule(task, 1000);
//			 Intent intent = new Intent(WelcomeActivity.this, LoginActivity_.class);
//				startActivity(intent);
		 }
	}

	@Override
	public void onClick(View v) {
		 int position = (Integer) v.getTag();
	        setCurView(position);
	        setCurDot(position);
		
	}
}
