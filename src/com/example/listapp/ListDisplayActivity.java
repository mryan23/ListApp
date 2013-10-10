package com.example.listapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.listapp.list.model.List;
import com.example.listapp.list.model.ListObject;
import com.google.gson.Gson;

public class ListDisplayActivity extends Activity {

	LinearLayout layout;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_display);

		layout = (LinearLayout) findViewById(R.id.ListViewLinearLayout);
		title = (TextView) findViewById(R.id.listNameTextView);

		List l = new List();
		l.setName("List 1");
		ListObject[] items = new ListObject[3];
		ListObject item0 = new ListObject();
		item0.setName("Item0");
		items[0] = item0;
		ListObject item1 = new ListObject();
		item1.setName("Item1");
		ListObject subItem10 = new ListObject();
		subItem10.setName("SubItem10");
		ListObject[] subItems = { subItem10 };
		item1.setItems(subItems);
		item1.setCompleted(true);
		items[1] = item1;
		l.setItems(items);
		ListObject item2 = new ListObject();
		item2.setName("Item2");
		items[2]=item2;

		Gson gson = new Gson();
		Log.d("JSON", gson.toJson(l));
		populateList(l);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_display, menu);
		return true;
	}

	private void populateList(List l) {
		title.setText(l.getName());
		ListObject[] items = l.getItems();
		for (int i = 0; i < items.length; i++) {
			addListObject(items[i], 0);
		}
	}

	private void addListObject(ListObject obj, int level) {
		CheckBox cb = new CheckBox(this);
		cb.setSelected(obj.isCompleted());
		cb.setText(obj.getName());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.FILL_PARENT);
		params.setMargins(level*25,0,0,0);
		cb.setLayoutParams(params);
		layout.addView(cb);

		ListObject[] items = obj.getItems();
		try {
			for (int i = 0; i < items.length; i++) {
				addListObject(items[i], level + 1);
			}
		} catch (NullPointerException e) {
		}
	}

}
