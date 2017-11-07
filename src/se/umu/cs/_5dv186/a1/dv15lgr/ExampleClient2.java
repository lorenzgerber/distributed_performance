package se.umu.cs._5dv186.a1.dv15lgr;


import java.io.IOException;
import java.net.SocketTimeoutException;
import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.DefaultStreamServiceClient;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public final class ExampleClient2
{
  public static final int DEFAULT_TIMEOUT = 1000;


  //----------------------------------------------------------
  //----------------------------------------------------------
  public static void listStreamInfo (StreamServiceClient client)
    throws IOException
  {
    StreamInfo[] streams = client.listStreams();
    System.out.println("found " + streams.length + " streams");
    for (StreamInfo stream : streams)
    {
      System.out.println("  '" + stream.getName() + "': " + stream.getLengthInFrames() + " frames, " +
                         stream.getWidthInBlocks() + " x " + stream.getHeightInBlocks() + " blocks");
    }
  }

  //----------------------------------------------------------
	public static void main (String[] args)
	{
    try
    {
      final String host     = (args.length > 0) ? args[0] : "localhost";
      final int timeout     = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_TIMEOUT;
      final String username = (args.length > 2) ? args[2] : "test";

      StreamServiceClient client = DefaultStreamServiceClient.bind(host,timeout,username);

      listStreamInfo(client);
      

      int nr = 100;
      int count = 0;
      String stream = "stream1";
      int frame = 1;
      int blockX = 1;
      int blockY = 1;
      for (int i=0; i<nr; i++)
      {
        try
        {
          long t1 = System.currentTimeMillis();
          Block block = client.getBlock(stream, 1,10,10);
          long t2 = System.currentTimeMillis();
          System.out.println("block received from server in " + (t2 - t1) + " ms");
          for(int j = 0; j < 16*16; j++) {
        	  //System.out.print(block.getPixels()[j].getB());
          }
          count++;
        }
        catch (SocketTimeoutException e)
        {
          System.out.println("socket timeout");
        }
      }
      System.out.println("received " + count + " / " + nr);
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	}
}
