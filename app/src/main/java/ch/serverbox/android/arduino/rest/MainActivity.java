/*
 * MainActivity.java
 * This file is part of ArduinoYunREST
 *
 * Copyright (C) 2013 - Andreas Rudolf
 * You should have received a copy of the GNU General Public License
 * along with ArduinoYunREST. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.serverbox.android.arduino.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.example.arduinoled.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * (c) Nexus-Computing GmbH Switzerland
 * @author Andreas Rudolf, 27.09.2013
 * Use this Android application together with the official Arduino bridge sketch from here:
 * http://arduino.cc/en/Tutorial/Bridge
 * Make sure to adjust ARDUINO_IP_ADDRESS to the IP of your Arduino Yun
 */
public class MainActivity extends Activity {

	// TODO: adjust ARDUINO_IP_ADDRESS
	public final String ARDUINO_IP_ADDRESS = "192.168.1.49";
	public final String TAG = "ArduinoYun";
	public final int DELAY_VALUE = 300;
	
	private SeekBar mSeekBar;
	private Button mButton;
	private Boolean mConnected;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton = (Button) findViewById(R.id.myButton);
//		mButton.getBackground().setColorFilter(0xFFFF0000,PorterDuff.Mode.MULTIPLY);
		mButton.getBackground().setColorFilter(Color.GRAY,PorterDuff.Mode.MULTIPLY);
		
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mQueue.clear();
				mQueue.offer(progress);
			}
		});
	}
	
	@Override
	protected void onStart() {
		mStop.set(false);
		if(sNetworkThreadSend == null){
			sNetworkThreadSend = new Thread(mNetworkRunnableSend);
			sNetworkThreadSend.start();
		}
		if(sNetworkThreadReceive == null){
			sNetworkThreadReceive = new Thread(mNetworkRunnableReceive);
			sNetworkThreadReceive.start();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		mStop.set(true);
		mQueue.clear();
		mQueue.offer(-1);
		if(sNetworkThreadSend != null) sNetworkThreadSend.interrupt();
		if(sNetworkThreadReceive != null) sNetworkThreadReceive.interrupt();
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClickfw(View view) {
		try {
			mQueue.offer(333);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClickrv(View view) {
		try {
			mQueue.offer(444);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClickLeft(View view) {
		try {
			mQueue.offer(777);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClickRight(View view) {
		try {
			mQueue.offer(888);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onClick(View view) {
		try {
			Toast.makeText(getApplicationContext(), "Send",
					   Toast.LENGTH_SHORT).show();
			mQueue.offer(0);
//			mButton.setBackgroundColor(getResources().getColor(R.color.red));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void log(String s){
		Log.d(">==< "+TAG+" >==<", s);
	}

	private ArrayBlockingQueue<Integer> mQueue = new ArrayBlockingQueue<Integer>(100);
	private AtomicBoolean mStop = new AtomicBoolean(false);

	private static Thread sNetworkThreadSend = null;
	private final Runnable mNetworkRunnableSend = new Runnable() {

		
		@Override
		public void run() {
			log("starting network thread for sending");
//			String urlBase = "http://"+ARDUINO_IP_ADDRESS+"/arduino/analog/13/";
			String urlBase = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/last";
			String urlBase2 = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/forward";
			String urlBase3 = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/reverse";
			String urlBase4 = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/left";
			String urlBase5 = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/right";
			String url;

			try {
				while(!mStop.get()){
					int val = mQueue.take();
					if((val >= 0)&&(val <=255)){
						HttpClient httpClient = new DefaultHttpClient();
						url = urlBase.concat(String.valueOf(val));
						log("executing httpClient request");
						HttpResponse response = httpClient.execute(new HttpGet(url));
						log("httpClient response:");						
					}
					else if (val == 333){
						HttpClient httpClient = new DefaultHttpClient();
						log("executing httpClient request");
						
						HttpResponse response = httpClient.execute(new HttpGet(urlBase2));
						log("httpClient response:");						
					}
					else if (val == 444){
						HttpClient httpClient = new DefaultHttpClient();
						log("executing httpClient request");
						HttpResponse response = httpClient.execute(new HttpGet(urlBase3));
					}
					else if (val == 777){
						HttpClient httpClient = new DefaultHttpClient();
						log("executing httpClient request");
						HttpResponse response = httpClient.execute(new HttpGet(urlBase4));
					}
					else if (val == 888){
						HttpClient httpClient = new DefaultHttpClient();
						log("executing httpClient request");
						HttpResponse response = httpClient.execute(new HttpGet(urlBase5));
//						mButton.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.MULTIPLY);

					}
						
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				mConnected = false;
//				mButton.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
				log("***** Client Protocol Exception *****");
			} catch (IOException e) {
				e.printStackTrace();
				mConnected = false;
//				mButton.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
				log("***** Strack Trace Exception *****");
			} catch (InterruptedException e) {
				e.printStackTrace();
				mConnected = false;
//			mButton.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
				log("***** Interrupted Exception *****");
			}

			log("returning from network thread for sending");
			sNetworkThreadSend = null;
		}
	};

	private static Thread sNetworkThreadReceive = null;
	private final Runnable mNetworkRunnableReceive = new Runnable() {

		@Override
		public void run() {
			log("starting network thread for receiving");

//			String url = "http://"+ARDUINO_IP_ADDRESS+"/data/get/D13";
			String url = "http://"+ARDUINO_IP_ADDRESS+"/arduino/robot/last";

			while(!mStop.get()){
				try {
					String string = readURL(url);
					String value = "";
					try {
						JSONObject jsonMain = new JSONObject(string);
						value = jsonMain.get("value").toString();
						log("value = "+value);
						mConnected = true;

					} catch (Exception e) {
						e.printStackTrace();
						log("***** Client Protocol Exception *****");
						mConnected = false;
					}
					updateText(value);
					
					Thread.sleep(DELAY_VALUE);
//					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					log("***** Interrupted Exception *****");
					mConnected = false;
				}
			}

			log("returning from network thread for receiving");
			sNetworkThreadReceive = null;
		}
	};

	public void updateText(final String value){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView textView2 = (TextView) findViewById(R.id.value_addr);
				textView2.setText(ARDUINO_IP_ADDRESS);

				TextView textView = (TextView) findViewById(R.id.value);
				textView.setText(value);
				if (mConnected)
					mButton.getBackground().setColorFilter(Color.GREEN,PorterDuff.Mode.MULTIPLY);
				else
					mButton.getBackground().setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);			
			}
		});
		
	}
	
	public String readURL(String value){
		URL url;
		StringBuilder builder = new StringBuilder();
		try {
			url = new URL(value);
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){ 
				builder.append(inputLine);
			}
			in.close();
			log("************* readURL = "+ url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}
