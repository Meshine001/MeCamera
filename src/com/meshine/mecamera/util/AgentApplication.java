package com.meshine.mecamera.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AgentApplication extends Application{
	 private List<Activity> activitys = null;
	 private static AgentApplication instance;
	public AgentApplication() {
		super();
		activitys = new LinkedList<Activity>();
	}
	
	public static AgentApplication getInstance(){
		 if (null == instance) {
	            instance = new AgentApplication();
	        }
	        return instance;
	}
	 
	   // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if(!activitys.contains(activity)){
                activitys.add(activity);
            }
        }else{
            activitys.add(activity);
        }
         
    }
 
    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
			for (Activity activity : activitys) {
                activity.finish();
            }
        }
//        System.exit(0);
    }
	 
    
	
}
