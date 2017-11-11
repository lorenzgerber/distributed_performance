package se.umu.cs._5dv186.a1.dv15lgr;

import ki.types.ds.StreamInfo;
import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class Factory implements IFactory {
	

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient client, String streamName) {
		FrameAccessor accessor = null;
		try {
			StreamInfo[] streams = client.listStreams();
			for(StreamInfo stream : streams) {
				if(stream.getName().equals(streamName)) {
					accessor = new FrameAccessor(client, stream);
				}
			}
		} catch (Exception e) {
			System.out.println("exception");
		}
		return accessor;
	}

	@Override
	public FrameAccessor getFrameAccessor(StreamServiceClient[] clients, String stream) {
		// TODO Auto-generated method stub
		return null;
	}

}
