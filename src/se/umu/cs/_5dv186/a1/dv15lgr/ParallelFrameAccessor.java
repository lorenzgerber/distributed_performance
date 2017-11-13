package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.OptionalDouble;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.FrameAccessor;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;
import se.umu.cs._5dv186.a1.dv15lgr.IFrameAccessor.IFrame;
import se.umu.cs._5dv186.a1.dv15lgr.IFrameAccessor.IPerformanceStatistics;
import se.umu.cs._5dv186.a1.dv15lgr.SerialFrameAccessor.Frame;
import se.umu.cs._5dv186.a1.dv15lgr.SerialFrameAccessor.PerformanceStatistics;


class GetBlockRunnable implements Runnable {
	
	private StreamServiceClient rClient;
	private StreamInfo rStream;
	private int rFrame;
	private int rBlockX;
	private int rBlockY;
	
	public GetBlockRunnable(StreamServiceClient client, StreamInfo stream,  int frame, int blockX, int blockY) {
		rClient = client;
		rStream = stream;
		rFrame = frame;
		rBlockX = blockX;
		rBlockY = blockY;
		
	}
	

	@Override
	public void run() {
		try {
			//System.out.println("requesting frame " + rFrame + " block " + rBlockX + " " + rBlockY);
			Block block = rClient.getBlock(rStream.getName(), rFrame,rBlockX,rBlockY);
			//System.out.println("obtained frame " + rFrame + " block " + rBlockX + " " + rBlockY);
		} catch (SocketTimeoutException e) {
			System.out.println("socket timeout");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}


public class ParallelFrameAccessor implements IFrameAccessor {
	private StreamServiceClient[] serviceClients;
	private StreamInfo currentStream;
	private PerformanceStatistics performanceStatistics;
	
	private double dropRate;
	private double blockCount;
	private double blockDropped;
	ArrayList<Long> latency = new ArrayList<>();
	
	public ParallelFrameAccessor(StreamServiceClient[] clients, StreamInfo stream) {
		serviceClients = clients;
		currentStream = stream;
		
	}
	
	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		PerformanceStatistics stats = new PerformanceStatistics(serviceClients[0].getHost(), blockCount, blockCount, blockCount, latency);
		
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
				
				long t1 = System.currentTimeMillis();
		
				Thread thread1 = new Thread(new GetBlockRunnable(serviceClients[0], currentStream, frame, i, j));
				thread1.start();
				if(j < height) {
					j++;
				} else if (i < width) {
					i++;
				}
					
				Thread thread2 = new Thread(new GetBlockRunnable(serviceClients[1], currentStream, frame, i, j));
				thread2.start();
				if(j < height) {
					j++;
				} else if (i < width) {
					i++;
				}
				Thread thread3 = new Thread(new GetBlockRunnable(serviceClients[2], currentStream, frame, i, j));
				thread3.start();
				if(j < height) {
					j++;
				} else if (i < width) {
					i++;
				}
				Thread thread4 = new Thread(new GetBlockRunnable(serviceClients[3], currentStream, frame, i, j));
				thread4.start();
				
				try {
					thread1.join(0);
					thread2.join(0);
					thread3.join(0);
					thread4.join(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Block block = serviceClients[0].getBlock(currentStream.getName(), frame,i,j);
				long t2 = System.currentTimeMillis();
				latency.add(t2-t1);
				blockCount++;
				
				/*
				
				try {
					//System.out.println("requesting frame " + frame + " block " + i + " " + j);
					long t1 = System.currentTimeMillis();
					pool.execute(new GetBlockRunnable(serviceClients[0], currentStream, frame, i, j));
					//Block block = serviceClients[0].getBlock(currentStream.getName(), frame,i,j);
					long t2 = System.currentTimeMillis();
					latency.add(t2-t1);
					blockCount++;
					
				} catch (SocketTimeoutException e) {
					System.out.println("socket timeout");
					blockDropped++;
					
				}*/
				
			}
		}
		
		return currentFrame;
	}


}
