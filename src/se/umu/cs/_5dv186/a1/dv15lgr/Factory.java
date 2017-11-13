package se.umu.cs._5dv186.a1.dv15lgr;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class Factory implements IFactory {
	

	@Override
	public SerialFrameAccessor getFrameAccessor(StreamServiceClient client, String streamName) {
		SerialFrameAccessor accessor = null;
		try {
			StreamInfo[] streams = client.listStreams();
			for(StreamInfo stream : streams) {
				if(stream.getName().equals(streamName)) {
					accessor = new SerialFrameAccessor(client, stream);
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
		}
		return accessor;
	}

	@Override
	public ParallelFrameAccessor getFrameAccessor(StreamServiceClient[] clients, String streamName) {
		ParallelFrameAccessor accessor = null;
		try {
			StreamInfo[] streams = clients[0].listStreams();
			for(StreamInfo stream : streams) {
				if(stream.getName().equals(streamName)) {
					accessor = new ParallelFrameAccessor(clients, stream);
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
		}
		return accessor;

	}

}
