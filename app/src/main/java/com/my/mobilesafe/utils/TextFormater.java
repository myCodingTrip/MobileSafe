package com.my.mobilesafe.utils;

import java.text.DecimalFormat;

public class TextFormater {

	/**
	 * 返回byte的数据大小对应的文本
	 *
	 * @param size
	 * @return
	 */
	public static String getDataSize(long size) {
		if (size < 0) {
			size = 0;
		}
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return size + "bytes";
		} else if (size < 1024 * 1024) {
			float kbSize = size / 1024f;
			return formater.format(kbSize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbSize = size / 1024f / 1024f;
			return formater.format(mbSize) + "MB";
		} else{
			float gbSize = size / 1024f / 1024f / 1024f;
			return formater.format(gbSize) + "GB";
		}

	}

	/**
	 * 返回kb的数据大小对应的文本
	 *
	 * @param size
	 * @return
	 */
	public static String getKBDataSize(long size) {
		if (size < 0) {
			size = 0;
		}

		return getDataSize(size * 1024);

	}
}
