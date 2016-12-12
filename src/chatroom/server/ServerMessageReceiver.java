package chatroom.server;

import chatroom.shared.ChatException;
import chatroom.shared.Logger;
import chatroom.shared.Message;
import chatroom.shared.protocol.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Runnable to receive and handle commands from a single client
 */
public class ServerMessageReceiver implements Runnable
{
	private final ClientInstance clientInstance;
	private final ServerState server;

	/**
	 * @param clientInstance The client to listen to
	 * @param server         The server
	 */
	ServerMessageReceiver(ClientInstance clientInstance, ServerState server)
	{
		this.clientInstance = clientInstance;
		this.server = server;
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
						server.removeClient(username);
						break;

					case SEND:
						// read message and send to all clients
						Message m = CommandSend.readMessageFromClient(in, username);
						server.broadcastMessage(m);
						break;

					case LIST:
						CommandList.sendUserList(out, server);
						break;

					case HIST:
						CommandClientHistory.sendChatHistory(out, server);
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
