package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class FrameAccessor implements IFrameAccessor {
	
	private StreamServiceClient serviceClient;
	private StreamInfo currentStream;
	private SerialPerformanceStatistics performanceStatistics;
	
	private int dropRate;
	private int blockCount;
	private int blockDropped;
	ArrayList<Long> latency = new ArrayList<>();
	
	public FrameAccessor(StreamServiceClient client, StreamInfo stream) {
		performanceStatistics = new SerialPerformanceStatistics();
		serviceClient = client;
		currentStream = stream;
		
	}
	
	public class SerialFrame implements Frame{
		
		Block[][] blocks;
		
		public SerialFrame(int frame) {
			
		}

		@Override
		public Block getBlock(int blockX, int blockY) throws IOException, SocketTimeoutException {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
		
	public class SerialPerformanceStatistics implements PerformanceStatistics{

		@Override
		public double getPacketDropRate(String host) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getPacketLatency(String host) {
			// TODO Auto-generated method stub
			return 0;
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
	public StreamInfo getStreamInfo() throws IOException, SocketTimeoutException {
		
		return currentStream;
	}

	@Override
	public Frame getFrame(int frame) throws IOException, SocketTimeoutException {
		Frame currentFrame = new SerialFrame(frame);	
		int width = currentStream.getWidthInBlocks();
		int height = currentStream.getHeightInBlocks();
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				try {
					System.out.println("requesting frame " + i + " " + j);
					long t1 = System.currentTimeMillis()
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

	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
