package chatroom.client;

import chatroom.client.ui.ConsoleInterface;
import chatroom.client.ui.GraphicalInterface;
import chatroom.client.ui.IInterface;

public class ChatClient
{
	private final ClientConnection connection;
	private final IInterface ui;

	public ChatClient(ClientIdentity identity, IInterface ui)
	{
		this.ui = ui;
		this.connection = new ClientConnection(identity, ui);
	}

	public static void main(String[] args)
	{
		if (args.length != 4)
			throw new IllegalArgumentException("Usage: <username> <gui | console> <address> <port>");

		String username = args[0];
		String uiChoice = args[1];
		String address = args[2];
		Integer port = Integer.parseInt(args[3]);

		IInterface ui;
		switch (uiChoice)
		{
			case "gui":
				ui = new GraphicalInterface();
				break;
			case "console":
				ui = new ConsoleInterface(System.out);
				break;
			default:
				throw new IllegalArgumentException("UI choice can be either \"gui\" or \"console\"");
		}

		ClientIdentity identity = new ClientIdentity(username);

		ChatClient client = new ChatClient(identity, ui);
		boolean success = client.start(address, port);

		System.exit(success ? 0 : 1);
	}

	public boolean start(String address, int port)
	{
		// connect to server
		if (!connection.connect(address, port))
		{
			return false;
		}

		// run UI
		ui.start(connection);

		// disconnect
		connection.disconnect();

		return true;
	}
}
