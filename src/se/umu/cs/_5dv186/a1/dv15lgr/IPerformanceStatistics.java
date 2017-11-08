package se.umu.cs._5dv186.a1.dv15lgr;

public interface IPerformanceStatistics {
	
	//----------------------------------------------------------
	//----------------------------------------------------------
	    
	//----------------------------------------------------------
	// returns packet drop rate in percent (%) (or -1 for unused hosts)
	public double getPacketDropRate (String host);

	//----------------------------------------------------------
	// returns packet latency in milliseconds (ms) (or -1 for unused hosts)
	public double getPacketLatency (String host);

	//----------------------------------------------------------
	// returns frame throughput in frames per second (fps)
	public double getFrameThroughput ();

	  
	//----------------------------------------------------------
	// returns bandwidth utilization in bits per second (bps)
	public double getBandwidthUtilization ();

}
