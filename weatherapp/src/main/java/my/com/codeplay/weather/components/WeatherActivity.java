/*******************************************************************************
 * Copyright (c) 2014 CodePlay Studio. All rights reserved.
 * 
 * This app is designed for training purpose used in Native Android�
 * for Mobile Development course conducted by CodePlay Studio. 
 * The source code is provided "AS IS". Any expressed or implied warranties, 
 * including, but not limited to, the implied warranties of merchantability 
 * and fitness for a particular purpose are disclaimed. In no event shall the 
 * copyright holder be liable for any direct, indirect, incidental, special, 
 * exemplary, or consequential damages (Including, but not limited to, 
 * procurement of goods or services; loss of use, data, or profits; 
 * or business interruption) however caused and on any theory of liability, 
 * whether in contract, strict liability, or tort (Including negligence or otherwise) 
 * arising in any way out of the use of this source code, even if advised of the 
 * possibility of such damage.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the above copyright notice
 * and disclaimer are retained in the redistributions of source code or
 * reproduced in the documentation and/or other materials provided
 * with the redistributions in binary form.
 ******************************************************************************/
package my.com.codeplay.weather.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import my.com.codeplay.weather.R;

public class WeatherActivity extends Activity {
	private static final String TAG = WeatherActivity.class.getSimpleName();
	private static final boolean DEBUG = true;

	private static final String URL_SCHEME 		= "https";
	private static final String WEATHER_BASE_URL= "https://api.openweathermap.org/data/2.5/weather?";
	private static final String PARAM_APPID		= "APPID";
	private static final String PARAM_MODE		= "mode";
	private static final String PARAM_CITY_ID	= "id";

	private static final String APP_ID = "82445b6c96b99bc3ffb78a4c0e17fca5";
	private static final String MODE = "json";
	private static final String DEFAULT_CITY_ID = "1735161"; // q=kuala lumpur

	private static final String NO_CONN = "No_Conn";
	private static final String ERROR = "Error";
	private static int TIMEOUT = 15000;
	
	private AlertDialog alertDialog;
	private RelativeLayout rlNoConn, rlWeather;
	private LinearLayout llRetry, llLoading;
	private ProgressBar progressBar;
	private Button btnRefresh;
	private TextView tvLocation, tvTemperature, tvHumidity, tvWindSpeed, tvCloudiness;
    private ImageView ivIcon;
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_retry:
		case R.id.button_refresh:
			new WeatherDataRetrival().execute();
			break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		rlWeather = (RelativeLayout) findViewById(R.id.layout_weather);
		rlNoConn  = (RelativeLayout) findViewById(R.id.layout_no_conn);
		llRetry   = (LinearLayout) findViewById(R.id.panel_retry);
		llLoading = (LinearLayout) findViewById(R.id.panel_loading);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		btnRefresh  = (Button) findViewById(R.id.button_refresh);
		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		tvLocation  = (TextView) findViewById(R.id.location);
		tvTemperature = (TextView) findViewById(R.id.temperature);
		tvHumidity = (TextView) findViewById(R.id.humidity);
		tvWindSpeed = (TextView) findViewById(R.id.wind_speed);
		tvCloudiness = (TextView) findViewById(R.id.cloudiness);
        ivIcon = (ImageView) findViewById(R.id.icon);
		
		new WeatherDataRetrival().execute();
	}
	
	private void show(boolean bln) {
		rlWeather.setVisibility(bln ? View.VISIBLE : View.GONE);
		if (bln) {
			progressBar.setVisibility(View.GONE);
			btnRefresh.setVisibility(View.VISIBLE);
		}
		rlNoConn.setVisibility(bln ? View.GONE : View.VISIBLE);
		if (!bln) {
			llRetry.setVisibility(View.VISIBLE);
			llLoading.setVisibility(View.GONE);
		}
	}
	
	private class WeatherDataRetrival extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			if (rlWeather.isShown()) {
				progressBar.setVisibility(View.VISIBLE);
				btnRefresh.setVisibility(View.INVISIBLE);
			} else {
				llRetry.setVisibility(View.GONE);
				llLoading.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			NetworkInfo info = ((ConnectivityManager) WeatherActivity.this
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				/*
				Uri.Builder uriBuilder = new Uri.Builder();
				uriBuilder.scheme(URL_SCHEME)
						.authority(WEATHER_BASE_URL)
						.appendQueryParameter(PARAM_APPID, APP_ID)
						.appendQueryParameter(PARAM_MODE, MODE)
						.appendQueryParameter(PARAM_CITY_ID, DEFAULT_CITY_ID)
						.build();
				// */

				Uri uri = Uri.parse(WEATHER_BASE_URL).buildUpon()
							.appendQueryParameter(PARAM_APPID, APP_ID)
							.appendQueryParameter(PARAM_MODE, MODE)
							.appendQueryParameter(PARAM_CITY_ID, DEFAULT_CITY_ID)
							.build();

				HttpURLConnection conn = null;
				BufferedReader bufferedReader = null;
				try {
					//URL url = new URL(uriBuilder.toString());
					URL url = new URL(uri.toString());
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(TIMEOUT);
					conn.setReadTimeout(TIMEOUT);
					conn.connect();

					int responseCode = conn.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						if (bufferedReader != null) {
							String realine;
							/*
							 * Mutability Difference:
							 *
							 * String is immutable, if you try to alter their values, another object
							 * gets created, whereas StringBuffer and StringBuilder are mutable so
							 * they can change their values.
							 *
							 * Thread-Safety Difference:
							 *
							 * The difference between StringBuffer and StringBuilder is that StringBuffer
							 * is thread-safe. So when the application needs to be run only in a single
							 * thread then it is better to use StringBuilder. StringBuilder is more
							 * efficient than StringBuffer.
							 */
							StringBuilder stringBuilder = new StringBuilder();
							//StringBuffer stringBuffer = new StringBuffer();
							while ((realine = bufferedReader.readLine()) != null) {
								/*
								 * Since the returned data is in JSON, adding a newline isn't necessary
								 * (It won't affect parsing), but it does make debugging a lot easier
								 * when print out the completed buffer for debugging.
								 */
								stringBuilder.append(realine + "\n");
							}

							if (stringBuilder.length() == 0) {
								return ERROR;
							}

							return stringBuilder.toString();
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}

					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				return NO_CONN;
			}
			return ERROR;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (result.equals(NO_CONN)) {
				show(false);
			} else if (result.equals(ERROR)) {
				show(true);
			} else {
				if (DEBUG) Log.d(TAG, "Weather JSON String: " + result);
				try {
					getWeatherDataFromJson(result);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				show(true);
			}
		}

		private static final String OWM_NAME 	= "name";
		private static final String OWM_SYSTEM 	= "sys";
		private static final String OWM_COUNTRY = "country";
		private static final String OWM_MAIN	= "main";
		private static final String OWM_TEMPERATURE = "temp";
		private static final String OWM_HUMIDITY = "humidity";
		private static final String OWM_WIND = "wind";
		private static final String OWM_SPEED = "speed";
		private static final String OWM_CLOUDS = "clouds";
		private static final String OWM_ALL = "all";
        private static final String OWM_WEATHER = "weather";
        private static final String OWM_ID = "id";
        private static final String OWM_DESCRIPTION = "description";
        private static final String OWM_ICON = "icon";

		private void getWeatherDataFromJson(String jsonStr) throws JSONException {
			final JSONObject weatherJSON = new JSONObject(jsonStr);

			tvLocation.setText(
					getString(
							R.string.location,
							weatherJSON.getString(OWM_NAME),
							weatherJSON.getJSONObject(OWM_SYSTEM).getString(OWM_COUNTRY)
					)
			);

			final JSONObject mainJSON  = weatherJSON.getJSONObject(OWM_MAIN);
			// Temperature in Kelvin. Subtracted 273.15 from this figure to convert to Celsius.
			final double temp = mainJSON.getDouble(OWM_TEMPERATURE);
			tvTemperature.setText(String.valueOf(Math.round(temp - 273.15)));

			final int humidity = mainJSON.getInt(OWM_HUMIDITY);
			tvHumidity.setText(String.format(getResources().getString(R.string.percent), humidity));

			final double speed = weatherJSON.getJSONObject(OWM_WIND).getDouble(OWM_SPEED);
            final String strSpeed = String.format(getResources().getString(R.string.wind_speed), speed);
			tvWindSpeed.setText(strSpeed);

			final int cloudiness = weatherJSON.getJSONObject(OWM_CLOUDS).getInt(OWM_ALL);
			tvCloudiness.setText(String.format(getResources().getString(R.string.percent), cloudiness));

            final JSONArray weatherJSONArray = weatherJSON.getJSONArray(OWM_WEATHER);
            if (weatherJSONArray.length()>0) {
                final JSONObject jsonObject = weatherJSONArray.getJSONObject(0);
                ivIcon.setImageResource(matchWeatherConditionCode(jsonObject.getInt(OWM_ID)));
            }
		}

        private int matchWeatherConditionCode(int id) {
            switch (id) {
                case 200:
                case 201:
                case 202:
                case 210:
                case 211: // thunderstorm
                case 212:
                case 221:
                case 230:
                case 231:
                case 232:
                    return R.drawable.ic_thunderstorm_large;
                case 300:
                case 301: // drizzle
                case 302:
                case 310:
                case 311:
                case 312:
                case 313:
                case 314:
                case 321:
                    return R.drawable.ic_drizzle_large;
                case 500:
                case 501:
                case 502:
                case 503:
                case 504:
                case 511:
                case 520:
                case 521:
                case 522:
                case 531:
                    return R.drawable.ic_rain_large;
                case 600:
                case 601: // snow
                case 602:
                case 611:
                case 612:
                case 615:
                case 616:
                case 620:
                case 621:
                case 622:
                    return R.drawable.ic_snow_large;
                case 800: // clear sky
                    return R.drawable.ic_day_clear_large;
                case 801: // few clouds
                    return R.drawable.ic_day_few_clouds_large;
                case 802: // scattered clouds
                    return R.drawable.ic_scattered_clouds_large;
                case 803: // broken clouds
                case 804: // overcast clouds
                    return R.drawable.ic_broken_clouds_large;
                case 701:
                case 711:
                case 721:
                case 731:
                case 741: // fog
                case 751:
                case 761:
                case 762:
                    return R.drawable.ic_fog_large;
                case 781:
                case 900:
                    return R.drawable.ic_tornado_large;
                case 905: // windy
                    return R.drawable.ic_windy_large;
                case 906: // hail
                    return R.drawable.ic_hail_large;

                //case 771: // squalls
                //case 901: // tropical storm
                //case 902: // hurricane
                //case 903: // cold
                //case 904: // hot
                //case 951: // calm
                //case 952: // light breeze
                //case 953: // gentle breeze
                //case 954: // moderate breeze
                //case 955: // fresh breeze
                //case 956: // strong breeze
                //case 957: // high wind, near gale
                //case 958: // gale
                //case 959: // severe gale
                //case 960: // storm
                //case 961: // violent storm
                //case 962: // hurricane
            }
            return 0;
        }
		
	}
}
