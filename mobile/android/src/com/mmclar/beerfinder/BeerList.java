package com.mmclar.beerfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class BeerList extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setupList();
	}

	public void setupList() {
		
		final Activity act = this;
		
		try {
			int barId = (Integer) getIntent().getExtras().get("barId");
			JSONObject barObj = Util.GetJson("/bar/" + barId).getJSONObject(
					"bar");
			JSONArray beerArray = (JSONArray) barObj.get("taps");

			final Tap[] taps = new Tap[beerArray.length()];
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

			lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int selectedTapIndex, long arg3) {
					final Dialog dialog = new Dialog(view.getContext());
					dialog.setContentView(R.layout.beer_change);
					dialog.setTitle("Change Beer");
	
					JSONObject breweriesObj = Util.GetJson("/breweries/");
					try {
						final JSONArray breweriesArray = breweriesObj.getJSONArray("breweries");
						String[] breweryNames = new String[breweriesArray.length() + 1];
						final int[] breweryIds = new int[breweriesArray.length() + 1];
						JSONObject breweryObj;
						int selectedBreweryId = taps[selectedTapIndex].BreweryId;
						int selectedBreweryIndex = 0;
						for (int i = 0; i < breweriesArray.length(); i++) {
							breweryObj = breweriesArray.getJSONObject(i);
							breweryNames[i] = breweryObj.getString("name");
							breweryIds[i] = breweryObj.getInt("id");
							if (breweryIds[i] == selectedBreweryId) {
								selectedBreweryIndex = i;
							}
						}
						breweryNames[breweriesArray.length()] = "Add new brewery...";
						breweryIds[breweriesArray.length()] = -1;
	
						final Spinner spnBreweries = (Spinner) dialog.findViewById(R.id.spnBreweries);
						final EditText edtBreweryName = (EditText) dialog.findViewById(R.id.edtBreweryName);
						final Spinner spnBeers = (Spinner) dialog.findViewById(R.id.spnBeers);
						final EditText edtBeerName = (EditText) dialog.findViewById(R.id.edtBeerName);
	
						ArrayAdapter<String> breweryAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, breweryNames);
						breweryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spnBreweries.setAdapter(breweryAdapter);
						spnBreweries.setSelection(selectedBreweryIndex);
						spnBreweries.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> arg0, View view, int index, long arg3) {
								try {
									int selectedBeerIndex = 0;
									String[] beerNames = new String[1];
									int[] pBeerIds = new int[1];
									if (breweryIds[index] == -1) {
										edtBreweryName.setVisibility(View.VISIBLE);
									} else {
										edtBreweryName.setVisibility(View.GONE);
										JSONObject breweryObj = breweriesArray.getJSONObject(index);
										JSONArray beersArray = breweryObj.getJSONArray("beers");
										beerNames = new String[beersArray.length() + 1];
										pBeerIds = new int[beersArray.length() + 1];
										JSONObject beerObj;
										int selectedBeerId = taps[selectedTapIndex].BeerId;
										for (int i = 0; i < beersArray.length(); i++) {
											beerObj = beersArray.getJSONObject(i);
											beerNames[i] = beerObj.getString("name");
											pBeerIds[i] = beerObj.getInt("id");
											if (pBeerIds[i] == selectedBeerId) {
												selectedBeerIndex = i;
											}
										}
									}

									beerNames[beerNames.length - 1] = "Add new beer...";
									pBeerIds[beerNames.length - 1] = -1;

									final int[] beerIds = pBeerIds;

									ArrayAdapter<String> beerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, beerNames);
									beerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
									spnBeers.setAdapter(beerAdapter);
									spnBeers.setSelection(selectedBeerIndex);
									spnBeers.setOnItemSelectedListener(new OnItemSelectedListener() {
										@Override
										public void onItemSelected(AdapterView<?> arg0, View view, int beerIndex, long arg3) {
											if (beerIds[beerIndex] == -1) {
												edtBeerName.setVisibility(View.VISIBLE);
											} else {
												edtBeerName.setVisibility(View.GONE);
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
											try {
												JSONObject beerChangeObject = new JSONObject();

												beerChangeObject.put("tap", taps[selectedTapIndex].Id);

												int selectedBreweryId = breweryIds[spnBreweries.getSelectedItemPosition()];
												if (selectedBreweryId == -1) {
													beerChangeObject.put("breweryName", edtBreweryName.getText());
												} else {
													beerChangeObject.put("breweryId", selectedBreweryId);
												}

												int selectedBeerId = beerIds[spnBeers.getSelectedItemPosition()];
												if (selectedBeerId == -1) {
													beerChangeObject.put("beerName", edtBeerName.getText());
												} else {
													beerChangeObject.put("beerId", selectedBeerId);
												}

												Util.PostJson("/changeBeer/", beerChangeObject);
												
												act.startActivity(act.getIntent());
											} catch (JSONException ex) {
											}
										}
									});
								} catch (JSONException ex) {
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) { }
						});
	
						dialog.show();
					} catch (JSONException ex) {
					}
					return false;
				}
			});

		} catch (JSONException ex) {
		}
	}

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