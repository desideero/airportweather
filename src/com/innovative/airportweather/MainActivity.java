package com.innovative.airportweather;

import java.util.UUID;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.innovative.bo.AirportWeatherBO;
import com.innovative.operations.AirportWeatherOperation;
import com.innovative.operations.Operation;
import com.innovative.operations.Operation.OperationCallback;
import com.innovative.operations.OperationPoolManager;

public class MainActivity extends Activity {
	private Spinner spnAirportsList = null;
	private OperationPoolManager mOperationManager = OperationPoolManager.getInstance();
	UUID mCurrentRequestId = null;
	
	private TableLayout tlWeatherData = null;
	private TextView tvAirportTitle = null;
	private TextView tvDateTime = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initUIControls();
		populateAirports();
	}

	private void initUIControls() {
		tlWeatherData = (TableLayout) findViewById(R.id.tlWeatherData);
		tvAirportTitle = (TextView) findViewById(R.id.tvAirportTitle);
		tvDateTime = (TextView) findViewById(R.id.tvDateTime);
		
	    spnAirportsList = (Spinner)findViewById(R.id.spnAirportsList);
	    spnAirportsList.setOnItemSelectedListener(mItemSelectedListener);
	}
	
	private void populateAirports() {
		if (spnAirportsList!=null) {
			ArrayAdapter<Airport> mAdapterAirports = new ArrayAdapter<Airport>(this, R.layout.spinner_item);
			mAdapterAirports.setDropDownViewResource(android.R.layout.select_dialog_singlechoice); // This shows radio buttons for selection
			spnAirportsList.setAdapter(mAdapterAirports);
			mAdapterAirports.addAll(Airport.values());
			// TODO: Get GPS location and choose the nearest airport
			spnAirportsList.setSelection(mAdapterAirports.getPosition(Airport.SFO), true);
		}
	}
	
	OnItemSelectedListener mItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
			clearData(getString(R.string.loading));
			
			Airport airport = (Airport) spnAirportsList.getSelectedItem();
			AirportWeatherOperation airportWeatherOp = new AirportWeatherOperation("K"+airport.getCode(), mOperationCallback);
			
			// Cancel previous request (if any)
			if (mCurrentRequestId != null) {
				mOperationManager.cancelOperation(mCurrentRequestId);
			}
			mOperationManager.addRequest(airportWeatherOp);
			mCurrentRequestId = airportWeatherOp.getId();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			;
		}
		
	};
	
	OperationCallback mOperationCallback = new OperationCallback() {
		@Override
		public void onStarted(Operation operation) {
			;
		}
		
		@Override
		public void onComplete(final Operation operation) {
			final AirportWeatherBO awBO = (AirportWeatherBO) operation.getResult();
			// NOTE: Display only on the latest request. Ignore previous requests (if any).
			if (awBO.getId() == mCurrentRequestId) {
				runOnUiThread (new Runnable () { @Override public void run () {
					populateData(awBO);
				}});
			}
		}
	};
	
	private void clearData(String titleCardString) {
		tvAirportTitle.setText(titleCardString);
		tvDateTime.setText("");
		tvDateTime.setVisibility(View.GONE);
		tlWeatherData.removeAllViews();
		tlWeatherData.setVisibility(View.GONE);
	}
	
	private void populateData(AirportWeatherBO bo) {
		if (bo==null || bo.mStationName.isEmpty()) {
			clearData(getString(R.string.no_data));
		}
		else {
			tvAirportTitle.setText(bo.mStationName);
			tvDateTime.setText(bo.mDatetime); //TODO: Format this properly...
			tvDateTime.setVisibility(View.VISIBLE);
			
			// Add rows
			addRow("Humidity: ", String.valueOf(bo.mHumidity));
			addRow("Temperature: ", String.valueOf(bo.mTemperature));
			addRow("Wind Speed: ", String.valueOf(bo.mWindSpeed));
			addRow("WindDirection: ", String.valueOf(bo.mWindDirection));
			addRow("Clouds: ", bo.mClouds);
			addRow("Elevation: ", String.valueOf(bo.mElevation));
			addRow("Longitude: ", String.valueOf(bo.mLongitude));
			addRow("Latitude: ", String.valueOf(bo.mLatitude));
			addRow("Sea Level Pressure: ", String.valueOf(bo.mSeaLevelPressure));
			
			tlWeatherData.setVisibility(View.VISIBLE);
		}
	}
	
	private void addRow(String name, String value) {
		TableRow row = (TableRow)LayoutInflater.from(this).inflate(R.layout.weather_table_row, null, false);
		TextView tvWeatherDataName = (TextView)row.findViewById(R.id.tvWeatherDataName);
		TextView tvWeatherDataValue = (TextView)row.findViewById(R.id.tvWeatherDataValue);
		tvWeatherDataName.setText(name);
		tvWeatherDataValue.setText(value);
		tlWeatherData.addView(row);
//		TableLayout.LayoutParams lp = (TableLayout.LayoutParams)row.getLayoutParams();
//		if (lp!=null)
//			lp.setMargins(2, 10, 2, 10);
	}
}
