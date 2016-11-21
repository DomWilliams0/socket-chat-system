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

			// read opcode
			String opcodeStr = null;
			try
			{
				// read opcode
				opcodeStr = in.readLine();

				if (opcodeStr == null)
				{
					Logger.error("Read error");
					return;
				}

				Protocol.Opcode opcode = Protocol.Opcode.parse(opcodeStr);

				// read username and ensure it's correct
				String usernameStr = in.readLine();
				if (!username.equals(usernameStr))
				{
					Logger.error("User '%s' tried sending a command as '%s', uh oh", username, usernameStr);
					continue;
				}

				// delegate quit message
				if (opcode == Protocol.Opcode.QUIT)
				{
					serverInstance.removeClient(username);
					return;
				}

				// invalid opcode
				else if (opcode != Protocol.Opcode.SEND)
				{
					Logger.error("Expected send opcode, but received '%s' from client '%s'", opcodeStr, username);
					continue;
				}

				// read message
				String encoded = in.readLine();
				serverInstance.broadcastMessage(username, encoded);

			} catch (IOException e)
			{
				Logger.error(e.getMessage());
				return;
			}

		}

	}
}
