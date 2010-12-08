package com.mmclar.beerfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class BeerList extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	this.setupList();
    }
    
    public void setupList() {
    	try {
    		String json = (String) getIntent().getExtras().get("barData");
    		JSONObject barsObj = new JSONObject(json);
    		JSONArray beerArray = (JSONArray) barsObj.get("taps");
    		
    		BeerListItem[] beerListItems = new BeerListItem[beerArray.length()];
    		for (int i = 0; i < beerArray.length(); i++){
    			beerListItems[i] = new BeerListItem();
    			beerListItems[i].Name = beerArray.getJSONObject(i).getString("name");
    			beerListItems[i].Brewery = beerArray.getJSONObject(i).getString("brewery");
    		}
    		
    		setListAdapter(new BeerListAdapter(this, R.layout.beers_item, beerListItems));
  
	    	ListView lv = getListView();
	    	lv.setLongClickable(true);
	    	
	    	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
					final Dialog dialog = new Dialog(view.getContext());
					dialog.setContentView(R.layout.beer_change_dialog);
					dialog.setTitle("Change Beer");

					JSONObject breweriesObj = Util.GetJson("/breweries/");
					try {
						final JSONArray breweriesArray = breweriesObj.getJSONArray("breweries");
						String[] breweryNames = new String[breweriesArray.length()];
						int[] breweryIds = new int[breweriesArray.length()];
						JSONObject breweryObj;
						for (int i = 0; i < breweriesArray.length(); i++) {
							breweryObj = breweriesArray.getJSONObject(i);
							breweryNames[i] = breweryObj.getString("name");
							breweryIds[i] = breweryObj.getInt("id");
						}
						
						Spinner spnBreweries = (Spinner) dialog.findViewById(R.id.spnBreweries);
						ArrayAdapter<String> breweryAdapter =
							new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, breweryNames);
						breweryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spnBreweries.setAdapter(breweryAdapter);
						spnBreweries.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0, View view, int index, long arg3) {
								try {
									JSONObject breweryObj = breweriesArray.getJSONObject(index);
									JSONArray beersArray = breweryObj.getJSONArray("beers");
									String[] beerNames = new String[beersArray.length()];
									int[] beerIds = new int[beersArray.length()];
									JSONObject beerObj;
									for (int i = 0; i < beersArray.length(); i++) {
										beerObj = beersArray.getJSONObject(i);
										beerNames[i] = beerObj.getString("name");
										beerIds[i] = beerObj.getInt("id");
									}
									
									Spinner spnBeers = (Spinner) dialog.findViewById(R.id.spnBeers);
									ArrayAdapter<String> beerAdapter =
										new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, beerNames);
									beerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
									spnBeers.setAdapter(beerAdapter);
								}
								catch (JSONException ex) {
									
								}
							      
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) { }
						});

						Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
						btnCancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						
						Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
						btnOK.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
	    
						dialog.show();
					}
					catch (JSONException ex){
					}
					return false;
				}
	    	});
	    	
    	}
    	catch (JSONException ex) {
    	}
    }
	/*public class MyOnItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	      Toast.makeText(parent.getContext(), "The brewery is " +
	          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
	    }

	    public void onNothingSelected(AdapterView<?> parent) { }
	}*/    
    private class BeerListAdapter extends ArrayAdapter<BeerListItem> {
    	private BeerListItem[] items;
    	
    	public BeerListAdapter(Context context, int textViewResourceId, BeerListItem[] beerListItems) {
    		super(context, textViewResourceId, beerListItems);
    		this.items = beerListItems;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		  View v = convertView;
              if (v == null) {
                  LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                  v = vi.inflate(R.layout.beers_item, null);
              }

              BeerListItem bli = items[position];
              if (bli != null) {
                  TextView tvName = (TextView) v.findViewById(R.id.tvName);
                  TextView tvBrewery = (TextView) v.findViewById(R.id.tvBrewery);
                  if (tvName != null) {
                        tvName.setText(bli.Name);                            }
                  if(tvBrewery != null) {
                        tvBrewery.setText(bli.Brewery);
                  }
              }
              return v;
    	}
    }
}