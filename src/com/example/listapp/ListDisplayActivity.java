package com.example.listapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listapp.list.model.List;
import com.example.listapp.list.model.ListObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class ListDisplayActivity extends Activity {

	LinearLayout layout;
	ScrollView scroll;
	TextView title;
	List list;
	int id;
	Gson gson = new Gson();
	String user;
	String name, ip, port;
	float initialz = 0;
	ListDisplayActivity context;
	LatLng reminderLocation;
	SensorManager mSensorManager;
	SensorEventListener accelerometerListener, lightListener;
	View background;
	ArrayList<EditText> itemFields=new ArrayList<EditText>();
	ArrayList<CheckBox> checkBoxes=new ArrayList<CheckBox>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_list_display);
		id = this.getIntent().getIntExtra("ID", -1);
		user = this.getIntent().getStringExtra("USER");
		name = this.getIntent().getStringExtra("NAME");
		ip = this.getIntent().getStringExtra("IP");
		port = this.getIntent().getStringExtra("PORT");

		layout = (LinearLayout) findViewById(R.id.ListViewLinearLayout);
		title = (TextView) findViewById(R.id.listNameTextView);
		scroll = (ScrollView) findViewById(R.id.listDisplayScrollView);
		background = findViewById(R.id.listDisplayBackground);
		title.setBackgroundColor(0);
		title.setFocusable(false);

		if (id < 0) {
			GetList gl = new GetList();
			gl.execute("http://" + ip + ":" + port + "/api/list?new=true&user="
					+ URLEncoder.encode(user) + "&name="
					+ URLEncoder.encode(name));
		} else {
			GetList gl = new GetList();
			gl.execute("http://" + ip + ":" + port + "/api/list?id=" + id);
		}

		Button save = (Button) findViewById(R.id.dummySaveButton);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PostList pl = new PostList();
				pl.execute("http://" + ip + ":" + port + "/api/list/");

			}

		});
		Button add = (Button) findViewById(R.id.dummyAddButton);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list != null) {
					ListObject obj = new ListObject();
					list.addItem(obj);
					addListObject(obj, 0, false);
				}

			}

		});

		Button location = (Button) findViewById(R.id.locationReminderButton);
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, LocationReminderActivity.class);
				startActivityForResult(i, 1);
			}

		});

		

	}
	
	protected void onResume(){
		super.onResume();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			Sensor accelSensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			accelerometerListener = new AccelerometerListener();
			mSensorManager.registerListener(accelerometerListener, accelSensor, SensorManager.SENSOR_DELAY_UI);
		}
		if(mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!=null){
			Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			lightListener = new SensorEventListener(){

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSensorChanged(SensorEvent event) {
					//title.setText(event.values[0]+"");
					//title.invalidate();
					if(event.values[0]<10){
						background.setBackgroundColor(Color.BLACK);
						title.setTextColor(Color.WHITE);
						for(EditText et:itemFields)
							et.setTextColor(Color.WHITE);
					}else{
						background.setBackgroundColor(Color.WHITE);
						title.setTextColor(Color.BLACK);
						for(EditText et: itemFields)
							et.setTextColor(Color.BLACK);
							
					}
					
				}
				
			};
			mSensorManager.registerListener(lightListener, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	protected void onPause(){
		super.onPause();
		if(accelerometerListener!=null)
			mSensorManager.unregisterListener(accelerometerListener);
		if(lightListener!=null)
			mSensorManager.unregisterListener(lightListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				String lat = data.getStringExtra("LAT");
				String lon = data.getStringExtra("LON");
				reminderLocation = new LatLng(Double.parseDouble(lat),
						Double.parseDouble(lon));
				Log.d(lat, lon);

				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				// Define the criteria how to select the locatioin provider ->
				// use
				// default
				Criteria criteria = new Criteria();
				String provider = LocationManager.PASSIVE_PROVIDER;
				Log.d("PROVIDER", provider);
				locationManager.requestLocationUpdates(provider, 1000, 10,
						new LocationListener() {

							@Override
							public void onProviderDisabled(String provider) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onProviderEnabled(String provider) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onStatusChanged(String provider,
									int status, Bundle extras) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onLocationChanged(Location location) {
								Location old = new Location(location);
								old.setLatitude(reminderLocation.latitude);
								old.setLongitude(reminderLocation.longitude);
								Toast.makeText(context,
										"CHANGED " + location.distanceTo(old),
										Toast.LENGTH_SHORT).show();
								if (location.distanceTo(old) < 150) {
									NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
											context)
											.setSmallIcon(
													R.drawable.ic_launcher)
											.setContentTitle("Location Reminder")
											.setContentText(list.getName());
									
									Intent resultIntent = new Intent(context, ListDisplayActivity.class);
									resultIntent.putExtra("ID", id);
									resultIntent.putExtra("USER",user);
									resultIntent.putExtra("NAME",name);
									resultIntent.putExtra("IP",ip);
									resultIntent.putExtra("PORT",port);
									PendingIntent resultPendingIntent =
									    PendingIntent.getActivity(
									    context,
									    0,
									    resultIntent,
									    PendingIntent.FLAG_UPDATE_CURRENT
									);
									mBuilder.setContentIntent(resultPendingIntent);
									
									NotificationManager mNotifyMgr = 
									        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
									// Builds the notification and issues it.
									mNotifyMgr.notify(1, mBuilder.build());
								}
							}

						});
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		}
	}

	private class PostList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpPost req = new HttpPost(params[0]);
			req.setHeader("content-type", "application/json");
			try {
				req.setEntity(new StringEntity(gson.toJson(list)));
				HttpResponse resp = client.execute(req);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	private class GetList extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urls[0]);
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
			populateList(gson.fromJson(result, List.class));
			title.addTextChangedListener(new TitleChangedWatcher(list));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_display, menu);
		return true;
	}

	private void populateList(List l) {
		checkBoxes = new ArrayList<CheckBox>();
		itemFields = new ArrayList<EditText>();
		list = l;
		if (id < 0)
			id = l.getId();
		list.setId(id);
		if (l.getName() != null && !l.getName().equals(""))
			title.setText(l.getName());
		title.setFocusable(true);
		title.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					view.setBackgroundResource(android.R.drawable.editbox_background_normal);
				} else
					view.setBackgroundColor(0);
			}

		});
		ListObject[] items = l.getItems();
		if (items == null)
			return;
		for (int i = 0; i < items.length; i++) {
			addListObject(items[i], 0, false);
		}
	}

	private void addListObject(ListObject obj, int level, boolean focus) {
		if (obj == null)
			return;
		if (obj.isDeleted())
			return;
		
		LinearLayout ll = new LinearLayout(this);
		CheckBox cb = new CheckBox(this);
		checkBoxes.add(cb);
		cb.setChecked(obj.isCompleted());
		// cb.setText(obj.getName());
		cb.setOnCheckedChangeListener(new CheckChange(obj));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		params.setMargins(level * 25, 0, 0, 0);
		cb.setLayoutParams(params);
		ll.addView(cb);
		EditText et = new EditText(this);
		itemFields.add(et);
		et.setText(obj.getName());
		et.setBackgroundColor(0);
		et.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		et.addTextChangedListener(new TextChangedWatcher(obj));
		et.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus)
					view.setBackgroundResource(android.R.drawable.editbox_background_normal);
				else
					view.setBackgroundColor(0);
			}

		});
		if (focus)
			et.requestFocus();
		ll.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		ll.addView(et);
		layout.addView(ll);

		ListObject[] items = obj.getItems();
		try {
			for (int i = 0; i < items.length; i++) {
				addListObject(items[i], level + 1, false);
			}
		} catch (NullPointerException e) {
		}
	}

	private class CheckChange implements OnCheckedChangeListener {
		ListObject object;

		public CheckChange(ListObject obj) {
			object = obj;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			object.setCompleted(isChecked);

		}

	}

	private class TextChangedWatcher implements TextWatcher {
		ListObject object;

		public TextChangedWatcher(ListObject obj) {
			object = obj;
		}

		@Override
		public void afterTextChanged(Editable s) {
			object.setName(s.toString());

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// UNUSED

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// UNUSED

		}

	}

	private class TitleChangedWatcher implements TextWatcher {
		List list;

		public TitleChangedWatcher(List l) {
			list = l;
		}

		@Override
		public void afterTextChanged(Editable s) {
			list.setName(s.toString());

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}
	}
	
	private class AccelerometerListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			final float alpha = 0.8f;

			// Isolate the force of gravity with the low-pass filter.
			float gravity[] = new float[3];
			float linear_acceleration[] = new float[3];
			gravity[0] = alpha * gravity[0] + (1 - alpha)
					* event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha)
					* event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha)
					* event.values[2];
			// Log.d("X",""+gravity[0]);
			// Log.d("Y",""+gravity[1]);

			// Remove the gravity contribution with the high-pass
			// filter.
			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			linear_acceleration[2] = event.values[2] - gravity[2];

			// Log.d("Z",""+linear_acceleration[2]);
			float z = linear_acceleration[2];

			if (initialz == 0) {
				initialz = z;
				return;
			}
			// Log.d("SCROLL",2*scroll.getMaxScrollAmount()+
			// " "+scroll.getScrollY());
			if (z - initialz > 2) {
				if (scroll.canScrollVertically(1)) {
					Log.d("SCROLL DOWN", "NOW");
					scroll.setScrollY(scroll.getScrollY() + 100);
				}
			} else if (z - initialz < -2) {
				if (scroll.canScrollVertically(-1)) {
					scroll.setScrollY(scroll.getScrollY() - 100);
				}
			}

		}

	}

}
