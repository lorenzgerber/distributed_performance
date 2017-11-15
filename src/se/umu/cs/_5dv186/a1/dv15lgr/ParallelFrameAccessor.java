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
	private long start;
	private long stop;
	private int frames = 0;
	private ExecutorService pool = Executors.newFixedThreadPool(4);
	ArrayList<Long> latency = new ArrayList<>();
	HashMap<String, ConnectionPerformanceContainer> performanceContainers = new HashMap<>();
		
 	public ParallelFrameAccessor(StreamServiceClient[] clients, StreamInfo stream) {
		serviceClients = clients;
		currentStream = stream;
		for(StreamServiceClient client:clients) {
			performanceContainers.put(client.getHost(), new ConnectionPerformanceContainer());
		}
		start = System.currentTimeMillis();
		
	}
	
	public void shutdownThreadPool() {
		pool.shutdown();
		try {
		  pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stop = System.currentTimeMillis();
	}
	
	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		PerformanceStatistics stats = new PerformanceStatistics(performanceContainers, start, stop, frames);
		
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
		
		private HashMap<String, ConnectionPerformanceContainer> conPerform;
		private long pStart;
		private long pStop;
		private int pFrames;
		
		public PerformanceStatistics(HashMap<String, ConnectionPerformanceContainer> performanceContainers, long start, long stop, int frames ) {
			conPerform = performanceContainers;
			pStart = start;
			pStop = stop;
			pFrames = frames;
			
		}
		
		
		@Override
		public double getPacketDropRate(String host) {
			ConnectionPerformanceContainer current = conPerform.get(host);
			System.out.println(current.cBlockCount + " " + current.cDropCount);
			double dropRate = (double) Math.round(100.0/current.cBlockCount*current.cDropCount*100)/100;
			return dropRate;
		}

		@Override
		public double getPacketLatency(String host) {
			ConnectionPerformanceContainer current = conPerform.get(host);
			OptionalDouble average = current.cLatency.stream().mapToLong(a -> a).average();
			double result;
			if(average.isPresent()) {
				 result = (double) Math.round(average.getAsDouble() * 100) / 100;
			} else {
				result = 0;
			}
			return result;
		}

		@Override
		public double getFrameThroughput() {
			double elapsedTimeSec = (double) Math.round((pStop - pStart)/10)/100;
			System.out.println("elapsed time " + elapsedTimeSec);
			System.out.println("total frames " + pFrames);
			double result = (double) Math.round(frames / elapsedTimeSec*100)/100;
			return result;
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
				pool.execute(new GetBlockRunnable(serviceClients[i%4],performanceContainers.get(serviceClients[i%4].getHost()), currentStream, currentFrame, i, j));				
			}
		}
		System.out.print(frame + " ");
		frames++;
		return currentFrame;
	}
	
	class ConnectionPerformanceContainer{
		private int cBlockCount = 0;
		private int cDropCount = 0;
		private ArrayList<Long> cLatency;
		
		public ConnectionPerformanceContainer() {
			cLatency = new ArrayList<>();
		}
		
		public void incBlockCount() {
			cBlockCount++;
		}
		
		public void incDropCount() {
			cDropCount++;
		}
		
		public void addLatency(Long latency) {
			cLatency.add(latency);
		}
		
	}
	
	
	class GetBlockRunnable implements Runnable {
		
		private StreamServiceClient rClient;
		private ConnectionPerformanceContainer rPerformance;
		private StreamInfo rStream;
		private Frame rFrame;
		private int rBlockX;
		private int rBlockY;
		
		public GetBlockRunnable(StreamServiceClient client, ConnectionPerformanceContainer performance, StreamInfo stream,  Frame frame, int blockX, int blockY) {
			rClient = client;
			rPerformance = performance;
			rStream = stream;
			rFrame = frame;
			rBlockX = blockX;
			rBlockY = blockY;
			
		}
		

		@Override
		public void run() {
			try {
				//System.out.println("requesting frame " + rFrame + " block " + rBlockX + " " + rBlockY);
				rPerformance.incBlockCount();
				long t1 = System.currentTimeMillis();
				Block block = rClient.getBlock(rStream.getName(), rFrame.getFrameId(),rBlockX,rBlockY);
				long t2 = System.currentTimeMillis();	
				rPerformance.addLatency(t2-t1);
				System.out.println("(" + rFrame.getFrameId() + ", " + rBlockX + ", " + rBlockY + ")");
			} catch (SocketTimeoutException e) {
				System.out.println("timeout");
				rPerformance.incDropCount();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}


}
