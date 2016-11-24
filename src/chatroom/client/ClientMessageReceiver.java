package chatroom.client;

import chatroom.shared.Logger;
import chatroom.shared.Protocol;
import chatroom.server.Message;

import java.io.BufferedReader;
import java.io.IOException;

class ClientMessageReceiver implements Runnable
{
	private final ChatClient client;

	ClientMessageReceiver(ChatClient client)
	{
		this.client = client;
	}

	@Override
	public void run()
	{
		// wait for messages
		while (true)
		{
			// TODO needs to be interrupted on disconnect

			BufferedReader in = client.getIn();

			// read prologue
			Protocol.RequestPrologue request = Protocol.readCommandPrologue(in, Protocol.Opcode.SEND);
			if (request == null)
				return;

			// read message
			try
			{
				String encodedMessage = in.readLine();

				Message m = new Message(request.getUsername(), encodedMessage);
				m.decode();

				client.onReceiveMessage(m);

			} catch (IOException e)
			{
				Logger.error(e.getMessage());
				return;
			}

		}

	}
}
