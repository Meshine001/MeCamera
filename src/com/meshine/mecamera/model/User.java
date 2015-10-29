package com.meshine.mecamera.model;

import android.graphics.Bitmap;

public class User {
	private int id;
	private String username;
	private String userpassword;
	
	private String nickname;
	private String email;
	private String tel;
	private String location;
	
	private String headUrl;
	
	private String preViewUrl;

	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String username, String userpassword, String preViewUrl) {
		super();
		this.username = username;
		this.userpassword = userpassword;
		this.preViewUrl = preViewUrl;
	}


	public String getPreViewUrl() {
		return preViewUrl;
	}

	public void setPreViewUrl(String preViewUrl) {
		this.preViewUrl = preViewUrl;
	}

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getUserpassword() {
		return userpassword;
	}



	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}



	public String getNickname() {
		return nickname;
	}



	public void setNickname(String nickname) {
		this.nickname = nickname;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getTel() {
		return tel;
	}



	public void setTel(String tel) {
		this.tel = tel;
	}



	public String getLocation() {
		return location;
	}



	public void setLocation(String location) {
		this.location = location;
	}



	public String getHeadUrl() {
		return headUrl;
	}



	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}



	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", userpassword="
				+ userpassword + ", nickname=" + nickname + ", email=" + email
				+ ", tel=" + tel + ", location=" + location + ", headUrl="
				+ headUrl + "]";
	}

	
	
}
