package se.umu.cs._5dv186.a1.dv15lgr;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class Factory implements IFactory {
	

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient client, String streamName) {
		FrameAccessor accessor = null;
		StreamServiceClient clients[] = new StreamServiceClient[1];
		clients[0] = client;
		try {
			StreamInfo[] streams = client.listStreams();
			for(StreamInfo stream : streams) {
				if(stream.getName().equals(streamName)) {
					accessor = new FrameAccessor(clients, stream);
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
		}
		return accessor;
	}

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient[] clients, String streamName) {
		FrameAccessor accessor = null;
		try {
			StreamInfo[] streams = clients[0].listStreams();
			for(StreamInfo stream : streams) {
				if(stream.getName().equals(streamName)) {
					accessor = new FrameAccessor(clients, stream);
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
		}
		return accessor;

	}

}
