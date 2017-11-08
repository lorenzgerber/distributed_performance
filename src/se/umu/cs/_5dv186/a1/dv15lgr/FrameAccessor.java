package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class FrameAccessor implements IFrameAccessor {
	
	private SerialPerformanceStatistics performanceStatistics;

	
	public FrameAccessor() {
		performanceStatistics = new SerialPerformanceStatistics();
		
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Frame getFrame(int frame) throws IOException, SocketTimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PerformanceStatistics getPerformanceStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
