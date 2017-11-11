package se.umu.cs._5dv186.a1.dv15lgr;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class SerialClient {
	
	public SerialClient() {
		
		final String host = "harry.cs.umu.se";
		final int timeout = 1000;
		final String username = "dv15lgr";
		final String stream = "stream1";
		
		try{
		StreamServiceClient client = DefaultStreamServiceClient.bind(host,timeout,username);

		Factory factory = new Factory();
		FrameAccessor frameAccessor = factory.getFrameAccessor(client, stream);
		
		//  used to determine how many times to iterate
		StreamInfo test = frameAccessor.getStreamInfo();
		int numberOfFrames = test.getLengthInFrames();
		
		for (int i = 0; i < numberOfFrames; i++) {
			
			frameAccessor.getFrame(i);
						
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
	}
	
	

}
