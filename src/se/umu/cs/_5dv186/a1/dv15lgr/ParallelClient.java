package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.util.ArrayList;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.dv15lgr.SerialFrameAccessor.PerformanceStatistics;

public class ParallelClient {

	int FrameThroughput;
	int BandwithUtilization;
	
	public static void listStreamInfo (StreamServiceClient client) throws IOException {
		StreamInfo[] streams = client.listStreams();
		System.out.println("found " + streams.length + " streams");
		for (StreamInfo stream : streams){
			System.out.println("  '" + stream.getName() + "': " + stream.getLengthInFrames() + " frames, " +
					stream.getWidthInBlocks() + " x " + stream.getHeightInBlocks() + " blocks"); 
		}  
	}
	
	
	public static void main(String args[]) {
		
		final String host0 = "harry.cs.umu.se";
		final String host1 = "bellatrix.cs.umu.se";
		final String host2 = "dobby.cs.umu.se";
		final String host3 = "draco.cs.umu.se";
		
		final int timeout = 500;
		final String username = "test1";
		final String stream = "stream7";
		
		try{
			StreamServiceClient clients[] = new StreamServiceClient[4];
			clients[0] = DefaultStreamServiceClient.bind(host0,timeout,username);
			clients[1] = DefaultStreamServiceClient.bind(host0,timeout,username);
			clients[2] = DefaultStreamServiceClient.bind(host0,timeout,username);
			clients[3] = DefaultStreamServiceClient.bind(host0,timeout,username);
		
		
			listStreamInfo(clients[0]);

			Factory factory = new Factory();
			ParallelFrameAccessor frameAccessor = factory.getFrameAccessor(clients, stream);
			
			//  used to determine how many times to iterate
			StreamInfo test = frameAccessor.getStreamInfo();
			int numberOfFrames = test.getLengthInFrames();
			
			for (int i = 0; i < numberOfFrames; i++) {
				
				//System.out.println("We are here " + i);
				frameAccessor.getFrame(i);
							
			}
			
			//PerformanceStatistics perform = frameAccessor.getPerformanceStatistics();
			
			} catch (Exception e) {
				e.printStackTrace();		
			}
		
	}

}
