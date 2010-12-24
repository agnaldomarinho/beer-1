package com.mmclar.beerfinder;

import android.os.Build;

public class Config {
	public static final String URL_BASE = 
		"sdk".equals( Build.PRODUCT ) ? 
				"http://10.0.2.2:8000" :
					"http://beer.mmclar.dyndns.org";
}
