package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.dv15lgr.FrameAccessor.Frame;
import se.umu.cs._5dv186.a1.dv15lgr.FrameAccessor.PerformanceStatistics;


public class SerialClient {

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
		final String username = "dv15lgr";
		final String stream = "stream7";
		
		try{
			
			StreamServiceClient clients[] = new StreamServiceClient[1];
			clients[0] = DefaultStreamServiceClient.bind(host3,timeout,username);
			
			listStreamInfo(clients[0]);

			Factory factory = new Factory();
			FrameAccessor frameAccessor = factory.getFrameAccessor(clients, stream);
			
			//  used to determine how many times to iterate
			StreamInfo test = frameAccessor.getStreamInfo();
			int numberOfFrames = test.getLengthInFrames();
			
			for (int i = 0; i < 2; i++) {
				Frame frame = frameAccessor.getFrame(i);
			}
			
			frameAccessor.shutdownThreadPool();
			System.out.println("Finished Stream");
			
			PerformanceStatistics stats = frameAccessor.getPerformanceStatistics();
			System.out.println("frames per second: " + stats.getFrameThroughput());
			System.out.println("Bits per second: " + stats.getBandwidthUtilization());
			for(StreamServiceClient client:clients) {
				System.out.println("Droprate " + client.getHost() + " "+ stats.getPacketDropRate(client.getHost()) + "%");
				System.out.println("Latency " + client.getHost() + " "+ stats.getPacketLatency(client.getHost()) + "ms");
				
				for(Long latency : stats.getRawLatency(client.getHost())) {
					System.out.println(latency);
				}
				
				
			} 
			
			
			
			} catch (Exception e) {
				e.printStackTrace();		
			}
		
		System.out.println("-----------------------------------------------------------------------------------------------------vi är typ klart");
		
	}

}