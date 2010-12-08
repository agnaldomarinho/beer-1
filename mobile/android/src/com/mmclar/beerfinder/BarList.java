package com.mmclar.beerfinder;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mmclar.beerfinder.R;

public class BarList extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	this.setupList();
    }
    
    public void setupList() {
    	JSONObject barsObj = Util.GetJson("bars/(1.1,2.2)/");
    	try {
	    	final JSONArray barArray = (JSONArray) barsObj.get("bars");
	    	
	    	BarListItem[] barListItems = new BarListItem[barArray.length()];
	    	for (int i = 0; i < barArray.length(); i++) {
	    		barListItems[i] = new BarListItem();
	    		barListItems[i].Name = barArray.getJSONObject(i).getString("name");
	    		barListItems[i].TapCount = barArray.getJSONObject(i).getJSONArray("taps").length();
	    	}
	    	
	    	setListAdapter(new BarListAdapter(this, R.layout.bars_item, barListItems));
	
	    	ListView lv = getListView();
	    	lv.setTextFilterEnabled(true);
	    	
	    	lv.setOnItemClickListener(new OnItemClickListener() {
	    		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    			Intent i = new Intent(view.getContext(), BeerList.class);
	    			try{
	    				String s = barArray.getJSONObject(position).toString();
	    				i.putExtra("barData", s);
	    			}
	    			catch (JSONException ex){
	    			}
	    			startActivity(i);
	    		}
	    	});
    	}
		catch (JSONException ex) {
		}
    }
    
    private class BarListAdapter extends ArrayAdapter<BarListItem> {
    	private BarListItem[] items;
    	
    	public BarListAdapter(Context context, int textViewResourceId, BarListItem[] barListItems) {
    		super(context, textViewResourceId, barListItems);
    		this.items = barListItems;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		  View v = convertView;
              if (v == null) {
                  LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  v = vi.inflate(R.layout.bars_item, null);
              }

              BarListItem bli = items[position];
              if (bli != null) {
                      TextView tvName = (TextView) v.findViewById(R.id.tvName);
                      TextView tvTapCount = (TextView) v.findViewById(R.id.tvTapCount);
                      if (tvName != null) {
                            tvName.setText(bli.Name);                            }
                      if(tvTapCount != null) {
                            tvTapCount.setText(Integer.toString(bli.TapCount) + (bli.TapCount == 1 ? " Tap" : " Taps"));
                      }
              }
              return v;
    	}
    }
}
