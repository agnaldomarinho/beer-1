/**
 * 
 */
package com.mmclar.beerfinder.beeractivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mmclar.beerfinder.Data;
import com.mmclar.beerfinder.R;
import com.mmclar.beerfinder.Util;

final class BrewerySelectListener implements OnItemSelectedListener {
	/**
	 * 
	 */
	private final BeerLongClickListener beerLongClickListener;
	private final JSONArray breweriesArray;
	private final Spinner spnBreweries;
	private final Dialog dialog;
	private final int[] breweryIds;
	private final EditText edtBeerName;
	private final EditText edtBreweryName;
	private final int selectedTapIndex;
	private final Spinner spnBeers;

	BrewerySelectListener(BeerLongClickListener beerLongClickListener, JSONArray breweriesArray, Spinner spnBreweries, Dialog dialog, int[] breweryIds, EditText edtBeerName, EditText edtBreweryName, int selectedTapIndex, Spinner spnBeers) {
		this.beerLongClickListener = beerLongClickListener;
		this.breweriesArray = breweriesArray;
		this.spnBreweries = spnBreweries;
		this.dialog = dialog;
		this.breweryIds = breweryIds;
		this.edtBeerName = edtBeerName;
		this.edtBreweryName = edtBreweryName;
		this.selectedTapIndex = selectedTapIndex;
		this.spnBeers = spnBeers;
	}

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
				int selectedBeerId = this.beerLongClickListener.beerList.taps[selectedTapIndex].BeerId;
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
				public void onNothingSelected(AdapterView<?> arg0) {
				}
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

						beerChangeObject.put("tap", BrewerySelectListener.this.beerLongClickListener.beerList.taps[selectedTapIndex].Id);

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

						dialog.dismiss();
						Util.postJson("/changeBeer/", beerChangeObject);
						new Data().reload();
						BrewerySelectListener.this.beerLongClickListener.beerList.setupList();
					} catch (JSONException ex) {
					}
				}
			});
			
			Button btnRemove = (Button) dialog.findViewById(R.id.btnRemove);
			btnRemove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Util.getJson("/removeTap/" + BrewerySelectListener.this.beerLongClickListener.beerList.taps[selectedTapIndex].Id);
					new Data().reload();
					BrewerySelectListener.this.beerLongClickListener.beerList.setupList();
				}
			});
		} catch (JSONException ex) {
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}