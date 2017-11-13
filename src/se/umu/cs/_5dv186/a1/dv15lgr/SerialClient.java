package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;






public final class SerialClient {
	
	
	  public static void listStreamInfo (StreamServiceClient client) throws IOException {
		  StreamInfo[] streams = client.listStreams();
		  System.out.println("found " + streams.length + " streams");
		  for (StreamInfo stream : streams){
			  System.out.println("  '" + stream.getName() + "': " + stream.getLengthInFrames() + " frames, " + 
					  stream.getWidthInBlocks() + " x " + stream.getHeightInBlocks() + " blocks");
		  }
	  }
	
	
	public static void main(String args[]) {
		
		final String host = "harry.cs.umu.se";
		final int timeout = 250;
		final String username = "dv15lgr";
		final String stream = "stream1";
		
		try{
		StreamServiceClient client = DefaultStreamServiceClient.bind(host,timeout,username);
		
		listStreamInfo(client);

		Factory factory = new Factory();
		FrameAccessor frameAccessor = factory.getFrameAccessor(client, stream);
		
		//  used to determine how many times to iterate
		StreamInfo test = frameAccessor.getStreamInfo();
		int numberOfFrames = test.getLengthInFrames();
		
		for (int i = 0; i < numberOfFrames; i++) {
			
			System.out.println("We are here " + i);
			frameAccessor.getFrame(i);
						
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
	}
	
	

}
