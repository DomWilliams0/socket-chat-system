package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.protocol.Command;
import chatroom.shared.protocol.Opcode;
import chatroom.shared.protocol.RequestPrologue;

import java.io.BufferedReader;

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

			try
			{
				RequestPrologue request = Command.readPrologue(in, Opcode.QUIT, Opcode.SEND);

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
						// read message and send to all clients
						serverInstance.broadcastEncodedMessage(username, Command.readArgument(in));
						break;
				}
			} catch (ChatException e)
			{
				e.printStackTrace();

				if (e.isSerious())
					break;
			}

		}

	}
}
