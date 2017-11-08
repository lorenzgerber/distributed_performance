package se.umu.cs._5dv186.a1.dv15lgr;

import se.umu.cs._5dv186.a1.client.StreamServiceClient;

public interface IFactory {


		public FrameAccessor getFrameAccessor(StreamServiceClient client, String stream);

		public FrameAccessor getFrameAccessor(StreamServiceClient[] clients, String stream); 

}
