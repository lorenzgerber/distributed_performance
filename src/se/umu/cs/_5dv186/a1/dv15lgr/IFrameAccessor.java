package se.umu.cs._5dv186.a1.dv15lgr;

import java.io.IOException;
import java.net.SocketTimeoutException;
import ki.types.ds.Block;
import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.FrameAccessor.Frame;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public interface IFrameAccessor {
  //----------------------------------------------------------
  public StreamInfo getStreamInfo ()
    throws IOException, SocketTimeoutException;

  //----------------------------------------------------------
  public Frame getFrame (int frame)
    throws IOException, SocketTimeoutException;

  //----------------------------------------------------------
  public IPerformanceStatistics getPerformanceStatistics ();


}