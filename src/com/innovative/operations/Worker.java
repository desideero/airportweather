package com.innovative.operations;

public class Worker implements Runnable {
	private Operation mOperation = null;
	
	public Worker(Operation operation) {
		mOperation = operation;
	} //Worker()
	
	@Override
	public void run() {
		mOperation.execute();
	}
	
	public Operation getOperation() {
		return mOperation;
	}
	
	public void cancel() {
		if (mOperation != null)
			mOperation.cancel();
	}
}
