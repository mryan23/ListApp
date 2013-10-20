package com.example.listapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.listapp.list.model.List;
import com.google.gson.Gson;

public class AllListsActivity extends Activity {

	LinearLayout layout;
	Gson gson = new Gson();
	String user,ip,port;
	AllListsActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_lists);
		layout = (LinearLayout) findViewById(R.id.AllListsLayout);
		Intent i = this.getIntent();
		user = i.getStringExtra("USER");
		ip = i.getStringExtra("IP");
		port = i.getStringExtra("PORT");
		updateUI();
	}
	
	private void updateUI(){
		layout.removeAllViews();
		GetLists gl = new GetLists();
		
		gl.execute("http://"+ip+":"+port+"/api/list?user=" + user);

		Button newButton = (Button) findViewById(R.id.newListButton);
		context = this;
		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Intent i = new
				 * Intent(getApplicationContext(),ListDisplayActivity.class);
				 * i.putExtra("ID", -1); i.putExtra("USER",user);
				 * startActivity(i);
				 */
				AlertDialog.Builder editalert = new AlertDialog.Builder(context);

				editalert.setTitle("New List");
				editalert.setMessage("Enter List Name");

				final EditText input = new EditText(context);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT);
				input.setLayoutParams(lp);
				editalert.setView(input);

				editalert.setPositiveButton("Create",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent i = new Intent(getApplicationContext(),
										ListDisplayActivity.class);
								i.putExtra("ID", -1);
								i.putExtra("USER", user);
								i.putExtra("NAME", input.getText().toString());
								i.putExtra("IP", ip);
								i.putExtra("PORT", port);
								startActivity(i);

							}
						});
				editalert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});

				editalert.show();
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_lists, menu);
		return true;
	}

	private class GetLists extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urls[0]);
				Log.d("HERE", request.getURI().toString());
				HttpResponse response = client.execute(request);

				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				String httpResponseVal = sb.toString();
				return httpResponseVal;
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("CRAP", "CRAP");
				return null;
			}
		}

		protected void onPostExecute(String result) {
			List[] lists = gson.fromJson(result, List[].class);
			addButtons(lists);
		}

	}

	private void addButtons(List[] lists){
		for(List l: lists){
			LinearLayout ll = new LinearLayout(this);
			TextView tv = new TextView(this);
			tv.setText(l.getName());
			Button open = new Button(this);
			open.setText("Open");
			open.setOnClickListener(new ButtonListener(l.getId(), ButtonListener.OPEN));
			Button delete = new Button(this);
			delete.setOnClickListener(new ButtonListener(l.getId(),ButtonListener.DELETE));
			delete.setText("Delete");
			ll.addView(open);
			ll.addView(delete);
			ll.addView(tv);
			layout.addView(ll);
		}
	}

	private class ButtonListener implements OnClickListener {
		public static final int OPEN = 1;
		public static final int DELETE = 2;
		private int id;
		private int function;

		public ButtonListener(int id, int function) {
			this.id = id;
			this.function = function;
		}

		@Override
		public void onClick(View v) {
			if (function == OPEN) {
				Intent i = new Intent(getApplicationContext(),
						ListDisplayActivity.class);
				i.putExtra("ID", id);
				i.putExtra("USER", user);
				i.putExtra("IP",ip);
				i.putExtra("PORT",port);
				startActivity(i);
			}else if(function == DELETE){
				DeleteList dlt = new DeleteList();
				dlt.execute("http://"+ip+":"+port+"/api/list?id=" + id);
				updateUI();
			}
		}
	}
	
	private class DeleteList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpDelete req = new HttpDelete(params[0]);
			req.setHeader("content-type", "application/json");
			try {
				HttpResponse resp = client.execute(req);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

}
