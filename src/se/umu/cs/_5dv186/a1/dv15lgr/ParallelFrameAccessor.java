package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;


public class ParallelFrameAccessor implements IFrameAccessor {
	private StreamServiceClient[] serviceClients;
	private StreamInfo currentStream;
	private PerformanceStatistics performanceStatistics;
	private ExecutorService pool = Executors.newFixedThreadPool(4);
	ArrayList<Long> latency = new ArrayList<>();
	HashMap<String, ConnectionPerformanceContainer> performanceContainers;
		
	public ParallelFrameAccessor(StreamServiceClient[] clients, StreamInfo stream) {
		serviceClients = clients;
		currentStream = stream;
		for(StreamServiceClient client:clients) {
			client.getHost();
		}
		
	}
	
	public void shutdownThreadPool() {
		pool.shutdown();
		try {
		  pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		PerformanceStatistics stats = new PerformanceStatistics(serviceClients[0].getHost());
		
		return stats;
	}
	
	@Override
	public StreamInfo getStreamInfo() throws IOException, SocketTimeoutException {
		
		return currentStream;
	}
	
	public class Frame implements IFrame{
		
		Block[][] blocks;
		int fFrame;
		
		public Frame(int frame) {
			fFrame = frame;
			
		}

		@Override
		public Block getBlock(int blockX, int blockY) throws IOException, SocketTimeoutException {
			return null;
		}
		
		public int getFrameId() {
			return fFrame;
		}
		
	}
		
	public class PerformanceStatistics implements IPerformanceStatistics{
		
		double dropRate;
		double blockCount;
		double blockDropped;
		ArrayList<Long> latency;
		String hostName;
		
		public PerformanceStatistics(String currentHost) {
			hostName = currentHost;
			
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
		int currentPoolSize = 0;
		
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				
				if (pool instanceof ThreadPoolExecutor) {
			        currentPoolSize = ((ThreadPoolExecutor) pool).getActiveCount();
				}
				
				while(currentPoolSize >=4) {
					if (pool instanceof ThreadPoolExecutor) {
				        currentPoolSize = ((ThreadPoolExecutor) pool).getActiveCount();
					}
				}
				pool.execute(new GetBlockRunnable(serviceClients[i%4], currentStream, currentFrame, i, j));				
			}
		}
		
		return currentFrame;
	}
	
	class ConnectionPerformanceContainer{
		private String cHostName;
		private int cBlockCount = 0;
		private int cDropCount = 0;
		private ArrayList<Double> cLatency;
		
		public ConnectionPerformanceContainer(String hostName) {
			cHostName = hostName;
			cLatency = new ArrayList<>();
		}
		
		public void incBlockCount() {
			cBlockCount++;
		}
		
		public void incDropCount() {
			cDropCount++;
		}
		
		public void addLatency(double latency) {
			cLatency.add(latency);
		}
		
	}
	
	
	class GetBlockRunnable implements Runnable {
		
		private StreamServiceClient rClient;
		private StreamInfo rStream;
		private Frame rFrame;
		private int rBlockX;
		private int rBlockY;
		
		public GetBlockRunnable(StreamServiceClient client, StreamInfo stream,  Frame frame, int blockX, int blockY) {
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
				Block block = rClient.getBlock(rStream.getName(), rFrame.getFrameId(),rBlockX,rBlockY);
				//System.out.println("obtained frame " + rFrame + " block " + rBlockX + " " + rBlockY);
			} catch (SocketTimeoutException e) {
				System.out.println("socket timeout");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}


}
