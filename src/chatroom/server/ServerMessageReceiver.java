package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.protocol.Command;
import chatroom.shared.protocol.Opcode;
import chatroom.shared.protocol.Protocol;
import chatroom.shared.protocol.RequestPrologue;

import java.io.BufferedReader;
import java.io.BufferedWriter;

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
			BufferedWriter out = clientInstance.getOut();
			String username = clientInstance.getUsername();

			try
			{
				RequestPrologue request = Command.readPrologue(in,
					Opcode.QUIT, Opcode.SEND, Opcode.LIST);

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

					case LIST:
						int userCount = serverInstance.getUserCount();
						Command.sendArgument(Integer.toString(userCount), out);
						Command.sendArgument(serverInstance.getUserList(Protocol.DELIMITER), out);
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
