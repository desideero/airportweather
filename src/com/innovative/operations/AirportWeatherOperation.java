package com.innovative.operations;

import com.innovative.bo.AirportWeatherBO;

public class AirportWeatherOperation extends Operation {
	private String mAirportCode = null;
	private String mURL = null;
	
	public AirportWeatherOperation(String airportCode, OperationCallback callback) {
		super(RequestType.AirportWeather, callback);
		mAirportCode = airportCode;
		mURL = "http://ws.geonames.org/weatherIcaoJSON?ICAO=" + mAirportCode;
	}
	
	@Override
	public void execute() {
		if (mCallback!=null)
			mCallback.onStarted(this);
		String jsonStr = httpRequest(mURL);
		
		AirportWeatherBO awBO = new AirportWeatherBO(mID, jsonStr);
		mResultBO = awBO;
	}

	@Override
	public void completed() {
		if (mCallback!=null)
			mCallback.onComplete(this);
	}
	
	public String getURL() {
		return mURL;
	}
}
