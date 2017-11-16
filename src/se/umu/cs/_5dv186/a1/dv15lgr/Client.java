package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.dv15lgr.FrameAccessor.Frame;
import se.umu.cs._5dv186.a1.dv15lgr.FrameAccessor.PerformanceStatistics;

public class Client {
	
	static int NUMBER_OF_FRAMES = 10;

	public static void listStreamInfo (StreamServiceClient client) throws IOException {
		StreamInfo[] streams = client.listStreams();
		System.out.println("found " + streams.length + " streams");
		for (StreamInfo stream : streams){
			System.out.println("  '" + stream.getName() + "': " + stream.getLengthInFrames() + " frames, " +
					stream.getWidthInBlocks() + " x " + stream.getHeightInBlocks() + " blocks"); 
		}  
	}
	
	public static void main(String args[]) {
		
		int timeout;
		String username = "dv15lgr";
		String stream = "stream7";
		
		try{
			
			StreamServiceClient clients[] = new StreamServiceClient[args.length-2];
			
			if(args.length < 3) {
				System.out.println("args: timeout user host_1 [host_n]");
			}
			
			timeout = Integer.parseInt(args[0]);
			username = args[1];
			
			int clientCounter = 0;
			for(int i = 2; i < args.length; i++) {
				clients[clientCounter] = DefaultStreamServiceClient.bind(args[i],timeout, username);
				clientCounter++;
			}
			
			//listStreamInfo(clients[0]);

			Factory factory = new Factory();
			FrameAccessor frameAccessor = factory.getFrameAccessor(clients, stream);
			
			// used to determine how many times to iterate
			// currently not used as stream services are too slow
			StreamInfo test = frameAccessor.getStreamInfo();
			int numberOfFrames = test.getLengthInFrames();
			
			for (int i = 0; i < NUMBER_OF_FRAMES; i++) {
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
	}
}