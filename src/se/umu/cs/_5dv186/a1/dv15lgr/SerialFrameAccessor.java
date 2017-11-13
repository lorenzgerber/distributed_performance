package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class SerialFrameAccessor implements IFrameAccessor {
	
	private StreamServiceClient serviceClient;
	private StreamInfo currentStream;
	private PerformanceStatistics performanceStatistics;
	
	private double dropRate;
	private double blockCount;
	private double blockDropped;
	ArrayList<Long> latency = new ArrayList<>();
	
	public SerialFrameAccessor(StreamServiceClient client, StreamInfo stream) {
		serviceClient = client;
		currentStream = stream;
		
	}
	
	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		PerformanceStatistics stats = new PerformanceStatistics(serviceClient.getHost(), blockCount, blockCount, blockCount, latency);
		
		return stats;
	}
	
	@Override
	public StreamInfo getStreamInfo() throws IOException, SocketTimeoutException {
		
		return currentStream;
	}
	
	public class Frame implements IFrame{
		
		Block[][] blocks;
		
		public Frame(int frame) {
			
		}

		@Override
		public Block getBlock(int blockX, int blockY) throws IOException, SocketTimeoutException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
		
	public class PerformanceStatistics implements IPerformanceStatistics{
		
		double dropRate;
		double blockCount;
		double blockDropped;
		ArrayList<Long> latency;
		String hostName;
		
		public PerformanceStatistics(String currentHost, double currentDropRate, double currentBlockCount, double currentBlockDropped, ArrayList<Long> currentLatency) {
			hostName = currentHost;
			dropRate = currentDropRate;
			blockCount = currentBlockCount;
			blockDropped = currentBlockDropped;
			latency = currentLatency;
			
		}
		
		@Override
		public double getPacketDropRate(String host) {
			
			return blockCount/blockDropped;
		}

		@Override
		public double getPacketLatency(String host) {
			OptionalDouble average = latency.stream().mapToLong(a -> a).average();
			return average.isPresent() ? average.getAsDouble() : 0; 
		}

		@Override
		public double getFrameThroughput() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getBandwidthUtilization() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}



	@Override
	public Frame getFrame(int frame) throws IOException, SocketTimeoutException {
		Frame currentFrame = new Frame(frame);	
		int width = currentStream.getWidthInBlocks();
		int height = currentStream.getHeightInBlocks();
		System.out.println("Frame: " + frame);
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				try {
					//System.out.println("requesting frame " + frame + " block " + i + " " + j);
					long t1 = System.currentTimeMillis();
					Block block = serviceClient.getBlock(currentStream.getName(), frame,i,j);
					long t2 = System.currentTimeMillis();
					latency.add(t2-t1);
					blockCount++;
					
				} catch (SocketTimeoutException e) {
					System.out.println("socket timeout");
					blockDropped++;
					
				}
				
			}
		}
		
		
		return currentFrame;
	}

	

}
