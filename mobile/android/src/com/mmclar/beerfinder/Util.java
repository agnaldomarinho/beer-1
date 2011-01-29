package com.mmclar.beerfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {
	public static JSONObject GetJson(String path) {

		try {
			URLConnection connection = new URL(Config.getUrlBase() + path).openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 1024 * 16);
			StringBuffer builder = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
			return new JSONObject(builder.toString());
		} catch (IOException ex) {
		} catch (JSONException ex) {
		}

		return null;
	}

	public static void PostJson(String path, JSONObject json) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(Config.getUrlBase() + path).openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
			osw.write("json=" + json.toString());
			osw.close();
			connection.getInputStream();
		} catch (IOException ex) {
		}
	}
}
