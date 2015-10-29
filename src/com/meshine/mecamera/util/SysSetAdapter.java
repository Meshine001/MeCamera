package com.meshine.mecamera.util;

import java.util.List;

import com.meshine.mecamera.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SysSetAdapter extends BaseAdapter{
	Context mContext;
	List<String> list;
	LayoutInflater inflater;
	String[] info = { "图片大小: ", "其他设置: "};
	

	public SysSetAdapter(Context mContext, List<String> list) {
		super();
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = LayoutInflater.from(mContext);
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.list_item_title);
			convertView.setTag(holder);
		}else {
			holder  = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(info[position] + list.get(position));
		
		return convertView;
	}
	
	class ViewHolder{
		TextView text;
	}

}
