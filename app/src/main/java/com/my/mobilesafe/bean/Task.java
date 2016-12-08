package com.my.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 *
 * @author zehua
 *
 */
public class Task {

	private String appName;
	private Drawable appIcon;
	private int pid;
	private int memorySize;
	private boolean isChecked = false;
	private String processName;
	
	private boolean isUserApp;
	
	
	
	public boolean isUserApp() {
		return isUserApp;
	}
	public void setIsUserApp(boolean userApp) {
		this.isUserApp = userApp;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean checked) {
		this.isChecked = checked;
	}
	
}
