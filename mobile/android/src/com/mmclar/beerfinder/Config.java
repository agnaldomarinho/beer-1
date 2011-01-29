package com.mmclar.beerfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.Build;

public class Config {
	private static String _urlBase = null;
	
	static {
		boolean emulator = "sdk".equals(Build.PRODUCT);
		if (emulator) {
			_urlBase = "http://10.0.2.2:8000";
			try {
				URLConnection connection = new URL(_urlBase + "/bars/").openConnection();
				connection.setConnectTimeout(1000);
		    	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 1024 * 16);
		    	StringBuffer builder = new StringBuffer();
		    	String line;
		    	while ((line = reader.readLine()) != null) {
		    		builder.append(line).append("\n");
		    	}
	    	}
	    	catch (IOException ex) {
	    		_urlBase = null;
	    	}
		}

		if (null == _urlBase) {
			_urlBase = "http://beer.mmclar.dyndns.org";
		}
	}

	public static String getUrlBase() {
		return _urlBase;
	}
}
