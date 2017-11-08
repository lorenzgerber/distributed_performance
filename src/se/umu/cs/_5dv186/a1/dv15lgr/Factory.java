package se.umu.cs._5dv186.a1.dv15lgr;

import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public class Factory implements IFactory {

	@Override
	public SerialFrameAccessor getFrameAccessor(StreamServiceClient client, String stream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParallelFrameAccessor getFrameAccessor(StreamServiceClient[] clients, String stream) {
		// TODO Auto-generated method stub
		return null;
	}

}
