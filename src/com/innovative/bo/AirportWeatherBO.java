package com.innovative.bo;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

public class AirportWeatherBO extends OperationResultBO {
	
	public enum CloudsCode {
		Undefined,
		ClearSkies,
		FewClouds, 
		ModerateClouds, 
		ThickClouds;
		
		public static CloudsCode get(String cloudsCodeStr) {
			if (cloudsCodeStr==null)
				return Undefined;
			else if (cloudsCodeStr.equalsIgnoreCase("CLEAR")) //TODO: Find correct String value 
				return ClearSkies;
			else if (cloudsCodeStr.equalsIgnoreCase("FEW"))
				return FewClouds;
			else if (cloudsCodeStr.equalsIgnoreCase("MODERATE")) //TODO: Find correct String value 
				return ModerateClouds;
			else if (cloudsCodeStr.equalsIgnoreCase("THICK")) //TODO: Find correct String value 
				return ThickClouds;
			return Undefined;
		}
	}
	
	public String mWeatherCondition = "N/A";
	public String mClouds = "N/A";
	public String mObservation = "";
	public int mWindDirection = 0;
	public String mICAO = ""; 
	public double mSeaLevelPressure = 0.00d;
	public int mElevation = 0;
	public String mCountryCode = "";
	public CloudsCode mCloudsCode = CloudsCode.Undefined;
	public double mLongitude = 0.00d;
	public double mTemperature = 0.00d;
	public double mDewPoint = 0.00d;
	public int mWindSpeed = 0;
	public int mHumidity = 0;
	public String mStationName = "";
	public String mDatetime = ""; //TODO: Convert this to Date/Time
	public double mLatitude = 0.00d;
	
	public AirportWeatherBO(UUID id, String jsonString) {
		super(id);
		parse(jsonString);
	}
	
	private boolean parse(String jsonString) {
		boolean bRet = false;
		JSONObject jsonObj = null;
		JSONObject weatherObservation = null;
		if (jsonString==null)
			return false;
		try {
			jsonObj = new JSONObject(jsonString);
			if (jsonObj.has("weatherObservation"))
				weatherObservation = (JSONObject)jsonObj.get("weatherObservation");
			if (weatherObservation != null) {
				/**
				 * NOTE: Had to put try on each field as some values we get from the server, 
				 *  JSONObject cannot handle it and throws an exception.
				 *  Eg: We expect double for "temperature" and sometimes we get empty string
				 */
				try {
					if (weatherObservation.has("weatherCondition"))
						mWeatherCondition = weatherObservation.getString("weatherCondition");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("clouds"))
						mClouds = weatherObservation.getString("clouds");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("observation"))
						mObservation = weatherObservation.getString("observation");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("windDirection"))
						mWindDirection = weatherObservation.getInt("windDirection");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("ICAO"))
						mICAO = weatherObservation.getString("ICAO");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("seaLevelPressure"))
						mSeaLevelPressure = weatherObservation.getDouble("seaLevelPressure");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("elevation"))
						mElevation = weatherObservation.getInt("elevation");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("countryCode"))
						mCountryCode = weatherObservation.getString("countryCode");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("cloudsCode"))
						mCloudsCode = CloudsCode.get(weatherObservation.getString("cloudsCode"));
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("lng"))
						mLongitude = weatherObservation.getInt("lng");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("temperature"))
						mTemperature = weatherObservation.getDouble("temperature");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("dewPoint"))
						mDewPoint = weatherObservation.getDouble("dewPoint");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("windSpeed"))
						mWindSpeed = weatherObservation.getInt("windSpeed");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("humidity"))
						mHumidity = weatherObservation.getInt("humidity");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("stationName"))
						mStationName = weatherObservation.getString("stationName");
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("datetime"))
						mDatetime = weatherObservation.getString("datetime"); //TODO: Convert this to Date/Time
				} catch (Exception e) { ; }
				
				try {
					if (weatherObservation.has("lat"))
						mLatitude = weatherObservation.getDouble("lat");
				} catch (Exception e) { ; }
				
				// All good!
				bRet = true;
				
			} //if (weatherObservation != null)
		} //try
		catch (JSONException e) {
			e.printStackTrace();
		}
		return bRet;
	} //parse()
}
