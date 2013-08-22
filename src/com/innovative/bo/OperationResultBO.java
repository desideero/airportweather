package com.innovative.bo;

import java.util.UUID;

public class OperationResultBO {
	// NOTE: This id should be same as OperationID (which this is result of).
	protected UUID mID;
	
	public OperationResultBO(UUID id) {
		mID = id;
	}
	
	public UUID getId() {
		return mID;
	}
}
