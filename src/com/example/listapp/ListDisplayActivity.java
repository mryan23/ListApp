package com.example.listapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.listapp.list.model.List;
import com.example.listapp.list.model.ListObject;
import com.google.gson.Gson;

public class ListDisplayActivity extends Activity {

	LinearLayout layout;
	TextView title;
	List list;
	Gson gson=new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_display);

		layout = (LinearLayout) findViewById(R.id.ListViewLinearLayout);
		title = (TextView) findViewById(R.id.listNameTextView);
		
		GetList gl = new GetList();
		gl.execute("http://10.0.2.2:8080/api/list/list%201");
		
		Button save = (Button)findViewById(R.id.dummySaveButton);
		save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PostList pl = new PostList();
				pl.execute("http://10.0.2.2:8080/api/list/list%201");
				
			}
			
		});
	}
	
	private class PostList extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPost req = new HttpPost(params[0]);
			req.setHeader("content-type", "application/json");
			try {
				req.setEntity(new StringEntity(gson.toJson(list)));
				HttpResponse resp = client.execute(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	private class GetList extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... urls) {
			try{
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urls[0]);
				Log.d("HERE",request.getURI().toString());
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
			}catch(Exception e){
				e.printStackTrace();
				Log.d("CRAP","CRAP");
				return null;
			}
		}
		protected void onPostExecute(String result){
			populateList(gson.fromJson(result, List.class));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_display, menu);
		return true;
	}

	private void populateList(List l) {
		list = l;
		title.setText(l.getName());
		ListObject[] items = l.getItems();
		for (int i = 0; i < items.length; i++) {
			addListObject(items[i], 0);
		}
	}

	private void addListObject(ListObject obj, int level) {
		if(obj.isDeleted())
			return;
		CheckBox cb = new CheckBox(this);
		cb.setChecked(obj.isCompleted());
		cb.setText(obj.getName());
		cb.setOnCheckedChangeListener(new CheckChange(obj));
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
	private class CheckChange implements OnCheckedChangeListener{
		ListObject object;
		public CheckChange(ListObject obj){
			object = obj;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			object.setCompleted(isChecked);
			
		}
		
	}

	

}
