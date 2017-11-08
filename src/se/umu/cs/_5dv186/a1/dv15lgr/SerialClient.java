package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class SerialClient {
	
	public static final int DEFAULT_TIMEOUT = 1000;
	
	
	public static void listStreamInfo (StreamServiceClient client) throws IOException {
		StreamInfo[] streams = client.listStreams();
		System.out.println("found " + streams.length + " streams");
		for (StreamInfo stream : streams){
			System.out.println("  '" + stream.getName() + "': " + stream.getLengthInFrames() + " frames, " +
					stream.getWidthInBlocks() + " x " + stream.getHeightInBlocks() + " blocks");	
		}	
	}
	
	public static void main(String[] args) {
		try {
			final String host     = (args.length > 0) ? args[0] : "localhost";
		    final int timeout     = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_TIMEOUT;
		    final String username = (args.length > 2) ? args[2] : "test";

		    StreamServiceClient client = DefaultStreamServiceClient.bind(host,timeout,username);
		    listStreamInfo(client);
		    
		    Factory FrameAccessorFactory = new Factory();
		    SerialFrameAccessor SerialAccessor = FrameAccessorFactory.getFrameAccessor(client, "stream01");
		    
		    
		    
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
	      
		
		
		

		
		
		
		
		
	}

}
