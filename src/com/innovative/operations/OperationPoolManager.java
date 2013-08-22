/*
 * class OperationPoolManager:
 * This class allows the rest of the application to send operation requests 
 * that can be queued for execution. 
 * This class is an implementation of ThreadPoolExecutor that processes 
 * operation requests in multiple preloaded threads.
 * 
 * And this is a singleton.
 * 
 * NOTE: The reason for this ThreadPool is that, we don't want to start a 
 * thread for every single request. With that we might end up having too 
 * many threads running and it becomes expensive. Also starting and stopping 
 * a thread is also expensive in terms of processor cycles.
 *  
 * @author: John K Gummadi
 */
package com.innovative.operations;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OperationPoolManager extends ThreadPoolExecutor {
	private static final int CORE_POOL_SIZE = 1;
	private static final int MAX_POOL_SIZE = 8;
	private static final int KEEP_ALIVE_SECONDS = 20; // i.e., after these many seconds, terminate extra threads (that are not core threads) that are idle.
	private HashMap<UUID, Worker> mWorkers = new HashMap<UUID, Worker>();
	
	// Note that this declaration must come last; it uses the objects above.
	private static final OperationPoolManager INSTANCE = new OperationPoolManager();
	
	private OperationPoolManager() {
		super(
			CORE_POOL_SIZE, 
			MAX_POOL_SIZE, 
			KEEP_ALIVE_SECONDS, 
			TimeUnit.SECONDS, 
			new ArrayBlockingQueue<Runnable>(MAX_POOL_SIZE));
	} //OperationPoolManager()
	
	public static OperationPoolManager getInstance() {
		return INSTANCE;
	} //getInstance()
	
	public void addRequest(Operation operation) {
		Worker worker = new Worker(operation);
		
		// NOTE: Adding workers to a hashmap, so we can implement cancel if we need to.
		mWorkers.put(operation.mID, worker);
		this.execute(worker);
	} //addRequest()
	
	// Cancel an operation by RequestId
	public void cancelOperation(UUID requestId) {
		Worker worker = mWorkers.get(requestId);
		if (worker!=null) {
			worker.cancel();
		}
	} //cancelOperation()
	
	
	
	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		// NOTE: Any preparation goes here.
		super.beforeExecute(t, r); // Keep "super.beforeExecute" at the end.
	} //beforeExecute()
	
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		
		if (t == null && r instanceof Worker) {
			try {
				sendResult((Worker)r);
			} 
			catch (Exception e) { 
				/**
				 * TODO: If something goes wrong, we have no way
				 * to pass the result back.
				 * Revisit this: May be get the worker from mWorkers and 
				 * use listener to send the result (error) back. 
				 */
				t = e.getCause();
			}
		}
	} //afterExecute()
	
	private void sendResult(Worker worker) {
		if (worker != null) { 
			Operation operation = worker.getOperation();
			operation.completed();
			
			// Remove the request as we're done with operation!
			mWorkers.remove(operation.mID);
		}
	} //sendResult()
	
} //class OperationPoolManager
