/**
 * 
 */
package com.mmclar.beerfinder.beeractivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mmclar.beerfinder.R;
import com.mmclar.beerfinder.Util;
import com.mmclar.beerfinder.R.id;
import com.mmclar.beerfinder.R.layout;

import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public final class BeerLongClickListener implements AdapterView.OnItemLongClickListener {
	/**
	 * 
	 */
	public final BeerList beerList;

	/**
	 * @param beerList
	 */
	BeerLongClickListener(BeerList beerList) {
		this.beerList = beerList;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int selectedTapIndex, long arg3) {
		final Dialog dialog = new Dialog(view.getContext());
		dialog.setContentView(R.layout.beer_change);
		dialog.setTitle("Change Beer");

		JSONObject breweriesObj = Util.getJson("/breweries/");
		try {
			final JSONArray breweriesArray = breweriesObj.getJSONArray("breweries");
			String[] breweryNames = new String[breweriesArray.length() + 1];
			final int[] breweryIds = new int[breweriesArray.length() + 1];
			JSONObject breweryObj;
			int selectedBreweryId = this.beerList.taps[selectedTapIndex].BreweryId;
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
			spnBreweries.setOnItemSelectedListener(new BrewerySelectListener(this, breweriesArray, spnBreweries, dialog, breweryIds, edtBeerName, edtBreweryName, selectedTapIndex, spnBeers));

			dialog.show();
		} catch (JSONException ex) {
		}
		return false;
	}
}