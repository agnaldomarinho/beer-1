package com.mmclar.beerfinder;

import com.mmclar.beerfinder.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BeerFinder extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.base_actions_item, BASE_ACTIONS));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i;
				switch (position) {
				case 0:
					i = new Intent(view.getContext(), BarList.class);
					startActivityForResult(i, 0);
					break;
				case 3:
					i = new Intent(view.getContext(), BarMap.class);
					startActivityForResult(i, 0);
					break;
				}
			}
		});
	}

	static final String[] BASE_ACTIONS = new String[] { "Bars", "Beers", "Favorites", "Find Me" };
}
