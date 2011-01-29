package com.mmclar.beerfinder;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
		JSONObject barsObj = new Data().GetBars();
		try {
			final JSONArray barArray = (JSONArray) barsObj.get("bars");

			final BarListItem[] barListItems = new BarListItem[barArray.length()];
			for (int i = 0; i < barArray.length(); i++) {
				barListItems[i] = new BarListItem();
				barListItems[i].Id = barArray.getJSONObject(i).getInt("id");
				barListItems[i].Name = barArray.getJSONObject(i).getString("name");
				barListItems[i].TapCount = barArray.getJSONObject(i).getJSONArray("taps").length();
			}

			setListAdapter(new BarListAdapter(this, R.layout.bars_item, barListItems));

			ListView lv = getListView();
			lv.setTextFilterEnabled(true);

			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent i = new Intent(view.getContext(), BeerList.class);
					i.putExtra("barId", barListItems[position].Id);
					startActivity(i);
				}
			});
		} catch (JSONException ex) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bars_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_bar);
		dialog.setTitle("Add Bar");
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

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
				final EditText edtBarName = (EditText) dialog.findViewById(R.id.edtBarName);
				JSONObject addBarObject = new JSONObject();

				try {
					addBarObject.put("barName", edtBarName.getText());
				} catch (JSONException ex) {
				}

				dialog.dismiss();
				Util.PostJson("/addBar/", addBarObject);
				setupList();
			}
		});

		dialog.show();
		return true;
	};

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
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.bars_item, null);
			}
			BarListItem bli = items[position];
			if (bli != null) {
				TextView tvName = (TextView) v.findViewById(R.id.tvName);
				TextView tvTapCount = (TextView) v.findViewById(R.id.tvTapCount);
				if (tvName != null) {
					tvName.setText(bli.Name);
				}
				if (tvTapCount != null) {
					tvTapCount.setText(Integer.toString(bli.TapCount) + (bli.TapCount == 1 ? " Tap" : " Taps"));
				}
			}
			return v;
		}
	}
}
