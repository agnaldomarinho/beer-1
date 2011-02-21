package com.mmclar.beerfinder.beeractivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mmclar.beerfinder.Data;
import com.mmclar.beerfinder.R;
import com.mmclar.beerfinder.Tap;
import com.mmclar.beerfinder.Util;
import com.mmclar.beerfinder.R.id;
import com.mmclar.beerfinder.R.layout;
import com.mmclar.beerfinder.R.menu;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class BeerList extends ListActivity {

	private int barId;
	Tap[] taps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setupList();
	}

	void setupList() {
		try {
			barId = (Integer) getIntent().getExtras().get("barId");
			JSONObject barObj = new Data().getBar(barId);
			JSONArray beerArray = (JSONArray) barObj.get("taps");

			taps = new Tap[beerArray.length()];
			for (int i = 0; i < beerArray.length(); i++) {
				taps[i] = new Tap();
				taps[i].Id = beerArray.getJSONObject(i).getInt("id");
				taps[i].Position = beerArray.getJSONObject(i).getInt("position");
				JSONObject beer = beerArray.getJSONObject(i).getJSONObject("beer");
				taps[i].BeerId = beer.getInt("id");
				taps[i].BeerName = beer.getString("name");
				JSONObject brewery = beer.getJSONObject("brewery");
				taps[i].BreweryId = brewery.getInt("id");
				taps[i].BreweryName = brewery.getString("name");
			}

			setListAdapter(new TapAdapter(this, R.layout.beers_item, taps));

			ListView lv = getListView();
			lv.setLongClickable(true);

			lv.setOnItemLongClickListener(new BeerLongClickListener(this));
		} catch (JSONException ex) {
			Log.e("BeerList", ex.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.beers_menu, menu);
		return true;
	};

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_tap);
		dialog.setTitle("Add Tap");
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		final Spinner spnLocations = (Spinner) dialog.findViewById(R.id.spnLocations);

		String[] slotDescriptions = new String[taps.length + 1];
		for (int i = 0; i < taps.length; i++) {
			slotDescriptions[i] = "(" + i + ")" + " " + taps[i].BeerName + " (" + taps[i].BreweryName + ")";
		}
		slotDescriptions[slotDescriptions.length - 1] = "At End";
		ArrayAdapter<String> locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, slotDescriptions);
		locationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnLocations.setAdapter(locationsAdapter);
		spnLocations.setSelection(slotDescriptions.length - 1);

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
				Util.getJson("/addBeer/" + barId + "/" + (spnLocations.getSelectedItemPosition() + 1));
				new Data().reload();
				setupList();
			}
		});

		dialog.show();

		return true;
	};

	private class TapAdapter extends ArrayAdapter<Tap> {
		private Tap[] items;

		public TapAdapter(Context context, int textViewResourceId, Tap[] beerListItems) {
			super(context, textViewResourceId, beerListItems);
			this.items = beerListItems;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.beers_item, null);
			}

			Tap t = items[position];
			if (t != null) {
				TextView tvName = (TextView) v.findViewById(R.id.tvName);
				TextView tvBrewery = (TextView) v.findViewById(R.id.tvBrewery);
				if (tvName != null) {
					tvName.setText(t.BeerName);
				}
				if (tvBrewery != null) {
					tvBrewery.setText(t.BreweryName);
				}
			}
			return v;
		}
	}
}