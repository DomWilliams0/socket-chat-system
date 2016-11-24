package chatroom.server;

import chatroom.Logger;
import chatroom.Protocol;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerMessageReceiver implements Runnable
{
	private final ClientInstance clientInstance;
	private final ChatServer serverInstance;

	public ServerMessageReceiver(ClientInstance clientInstance, ChatServer serverInstance)
	{
		this.clientInstance = clientInstance;
		this.serverInstance = serverInstance;
	}

	@Override
	public void run()
	{
		// wait for messages
		while (true)
		{
			// TODO needs to be interrupted

			BufferedReader in = clientInstance.getIn();
			String username = clientInstance.getUsername();

			Protocol.RequestPrologue request = Protocol.readCommandPrologue(in, Protocol.Opcode.QUIT, Protocol.Opcode.SEND);
			if (request == null)
				return;

			// validate sender
			if (!username.equals(request.getUsername()))
			{
				Logger.error("User '%s' tried sending a command as '%s', uh oh", username, request.getUsername());
				continue;
			}

			// handle opcode
			switch (request.getOpcode())
			{
				case QUIT:
					// delegate quit message
					serverInstance.removeClient(username);
					break;

				case SEND:
					try
					{
						// read message and send to all clients
						String encoded = in.readLine();
						serverInstance.broadcastEncodedMessage(username, encoded);

					} catch (IOException e)
					{
						Logger.error(e.getMessage());
						return;
					}
					break;
			}

		}

	}
}
