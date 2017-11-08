package se.umu.cs._5dv186.a1.dv15lgr;

import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public interface IFactory {
	
	//----------------------------------------------------------
	//----------------------------------------------------------
	  
	//----------------------------------------------------------
	public SerialFrameAccessor getFrameAccessor (StreamServiceClient client, String stream);

	//----------------------------------------------------------
	public ParallelFrameAccessor getFrameAccessor (StreamServiceClient[] clients, String stream);
	 

}
