package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.*;

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
					Opcode.QUIT, Opcode.SEND, Opcode.LIST, Opcode.HIST);

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
						Message m = CommandSend.readMessageFromClient(in, username);
						serverInstance.broadcastMessage(m);
						break;

					case LIST:
						CommandList.sendUserList(out, serverInstance);
						break;

					case HIST:
						CommandClientHistory.sendChatHistory(out, serverInstance);
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
