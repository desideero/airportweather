package com.innovative.operations;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.innovative.bo.OperationResultBO;

public abstract class Operation {
	public enum RequestType {
		Undefined, 
		AirportWeather,
		// NOTE: Add other request types as and when needed
	}
	
	public interface OperationCallback {
		public void onStarted(Operation operation);
		public void onComplete(Operation operation);
	}
	
	protected OperationCallback mCallback = null;
	protected UUID mID;
	protected RequestType mRequestType;
	protected OperationResultBO mResultBO = null;
	
	protected HttpClient mHttpClient = null;
	protected HttpPost mHttpPost = null;
	protected HttpResponse mHttpResponse = null;
	
	private boolean mIsCancelled = false;
	
	public Operation(RequestType requestType, OperationCallback callback) {
		mID = UUID.randomUUID();
		mRequestType = requestType;
		mCallback = callback;
	}
	
	public UUID getId() {
		return mID;
	}
	
	public RequestType getRequestType() {
		return mRequestType;
	}
	
	public OperationResultBO getResult() {
		return mResultBO;
	}
	
	protected String httpRequest(String url){
        String results = null;
        try
        {
        		mHttpClient = new DefaultHttpClient();
            mHttpPost = new HttpPost(url);
            mHttpResponse = mHttpClient.execute(mHttpPost);
            if (!isCancelled()) {
	            if(mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	            {
	                results = EntityUtils.toString(mHttpResponse.getEntity());
	            }
            }
            setCancelled(false);
        }catch(IOException e){
            e.printStackTrace();
        }
        return results;
    }
	
	private synchronized void setCancelled(boolean bCancelled) {
		mIsCancelled = bCancelled;
	}
	
	private synchronized boolean isCancelled() {
		return mIsCancelled;
	}
	
	// After "execute" call, the result gets stored in Operation class itself
	public abstract void execute();
	public abstract void completed();
	
	public void cancel() {
		setCancelled(true);
		mHttpPost.abort();
	}
} //class Operation
