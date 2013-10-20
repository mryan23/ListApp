package com.example.listapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	EditText userField,ipField,portField;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button dummy = (Button)findViewById(R.id.dummyShowListButton);
		dummy.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),AllListsActivity.class);
				i.putExtra("USER", userField.getText().toString());
				i.putExtra("IP", ipField.getText().toString());
				i.putExtra("PORT", portField.getText().toString());
				startActivity(i);
				
			}
			
		});
		userField = (EditText)findViewById(R.id.userNameTextField);
		ipField = (EditText)findViewById(R.id.ipAddressTextField);
		portField = (EditText)findViewById(R.id.portTextField);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
