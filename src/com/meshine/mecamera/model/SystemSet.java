package com.meshine.mecamera.model;

public class SystemSet {
	private int imageHeight;
	private int imageWidth;
	private int maxHeight;
	private int maxWidth;
	private boolean registed;
	
	public SystemSet() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	public int getMaxWidth() {
		return maxWidth;
	}
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public boolean isRegisted() {
		return registed;
	}
	public void setRegisted(boolean registed) {
		this.registed = registed;
	}
	@Override
	public String toString() {
		return "SystemSet [imageHeight=" + imageHeight + ", imageWidth="
				+ imageWidth + ", maxHeight=" + maxHeight + ", maxWidth="
				+ maxWidth + "]";
	}
	
	
	
}
