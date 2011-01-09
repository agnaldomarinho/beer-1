package com.mmclar.beerfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Data {
	private static JSONObject _data;
	
	public Data(){
		if (_data == null){
			Reload();
		}
	}
	
	public void Reload(){
		_data = Util.GetJson("/bars/(1.1,2.2)/");
	}
	
	public JSONObject GetBars(){
		return _data;
	}
	
	public JSONObject GetBar(int requestedId) {
		try{
			JSONArray bars = _data.getJSONArray("bars");
			for (int i = 0; i < bars.length(); i++){
				JSONObject bar = bars.getJSONObject(i);
				if (bar.getInt("id") == requestedId) {
					return bar;
				}
			}
		} catch (JSONException ex){
		}
		return null;
	}
}